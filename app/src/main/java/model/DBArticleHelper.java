package model;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBArticleHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "Source.db";
	public static final String ARTICLE_TABLE_NAME = "Article";

	public DBArticleHelper(Context context){
		super(context,DATABASE_NAME,null,1);
	}
	
	@Override
	protected void finalize() throws Throwable {
	    this.close();
	    super.finalize();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createTable();
		//		db.execSQL(
		//				"create table Article " +
		//						"(id integer primary key, apiId integer, title text, content text, excerpt text, link text, date text, categoryId integer, categoryName text)"
		//				);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		dropTable();
		createTable();
		//		db.execSQL("DROP TABLE IF EXISTS Article");
		//		onCreate(db);
	}

	public void dropTable(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS Article");
		Log.v("DROP", "Dropped");
	}

	public void createTable(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS Article");
		db.execSQL(
				"create table Article " +
						"(id integer primary key, apiId integer, title text, content text, excerpt text, link text, date text, categoryId integer, categoryName text, thumbnail text, medium text, large text, featured integer, author text)"
				);
	}

	public boolean insertArticle (int apiId, String title, String content, String excerpt, String link, String date, int categoryId, String categoryName, String thumbnail, String medium, String large, Integer featured, String author)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("apiId", apiId);
		contentValues.put("title", title);
		contentValues.put("content", content);
		contentValues.put("excerpt", excerpt);
		contentValues.put("link", link);
		contentValues.put("date", date);
		contentValues.put("categoryId", categoryId);
		contentValues.put("categoryName", categoryName);
		contentValues.put("thumbnail", thumbnail);
		contentValues.put("medium", medium);
		contentValues.put("large", large);
		contentValues.put("featured", featured);
		contentValues.put("author", author);
		
		Cursor c=db.rawQuery("SELECT * FROM Article WHERE apiId='"+apiId+"'", null);
		if(c.moveToFirst())
		{
			Log.v("Error", "Record exist");
			db.update("Article", contentValues, "apiId = ? ", new String[] { Integer.toString(apiId) } );

		}
		else
		{
			Log.v("Error", "Record doesn't exist");
			db.insert("Article", null, contentValues);
		}

		return true;
	}

	public Article getData(int apiId, int featured){
		SQLiteDatabase db = this.getReadableDatabase();
		Article result = new Article();
//		Cursor res =  db.rawQuery( "select * from Article where apiId="+apiId+"", null );
		Cursor res =  db.rawQuery( "select * from Article where apiId="+apiId+" = ? and featured="+featured+"", null );
		res.moveToFirst();
		result.id = res.getInt(res.getColumnIndex("apiId"));
		result.title = res.getString(res.getColumnIndex("title"));
		result.content = res.getString(res.getColumnIndex("content"));
		result.excerpt = res.getString(res.getColumnIndex("excerpt"));
		result.link = res.getString(res.getColumnIndex("link"));
		result.date = res.getString(res.getColumnIndex("date"));
		result.category.id = res.getInt(res.getColumnIndex("categoryId"));
		result.category.name = res.getString(res.getColumnIndex("name"));
		result.featuredImage.thumbnail = res.getString(res.getColumnIndex("thumbnail"));
		result.featuredImage.medium = res.getString(res.getColumnIndex("medium"));
		result.featuredImage.large = res.getString(res.getColumnIndex("large"));
		result.author = res.getString(res.getColumnIndex("author"));
		return result;
	}
	

	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, ARTICLE_TABLE_NAME);
		return numRows;
	}

	public boolean updateCategory (Integer apiId, String title, String content, String excerpt, String link, String date, int categoryId, String categoryName, String thumbnail, String medium, String large, int featured, String author)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("title", title);
		contentValues.put("content", content);
		contentValues.put("excerpt", excerpt);
		contentValues.put("link", link);
		contentValues.put("date", date);
		contentValues.put("categoryId", categoryId);
		contentValues.put("categoryName", categoryName);
		contentValues.put("thumbnail", thumbnail);
		contentValues.put("medium", medium);
		contentValues.put("large", large);
		contentValues.put("featured", featured);
		contentValues.put("author", author);

		db.update("Article", contentValues, "apiId = ? ", new String[] { Integer.toString(apiId) } );
		return true;
	}

	public Integer deleteArticle (Integer apiId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete("Article", 
				"apiId = ? ", 
				new String[] { Integer.toString(apiId) });
	}

	public ArrayList<Article> getAllArticles(int featured)
	{
		ArrayList<Article> result = new ArrayList<Article>();
		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor resultSet = db.rawQuery("Select * from Article ",null);
		Cursor resultSet = db.rawQuery( "select * from Article where featured="+featured+"", null );
		resultSet.moveToFirst();
		for(int i=0; i<resultSet.getCount(); i++){
			Article art = new Article();
			art.id = resultSet.getInt(resultSet.getColumnIndex("apiId"));
			art.title = resultSet.getString(resultSet.getColumnIndex("title"));
			art.content = resultSet.getString(resultSet.getColumnIndex("content"));
			art.excerpt = resultSet.getString(resultSet.getColumnIndex("excerpt"));
			art.link = resultSet.getString(resultSet.getColumnIndex("link"));
			art.date = resultSet.getString(resultSet.getColumnIndex("date"));
			art.category.id = resultSet.getInt(resultSet.getColumnIndex("categoryId"));
			art.category.name = resultSet.getString(resultSet.getColumnIndex("categoryName"));
			art.featuredImage.thumbnail = resultSet.getString(resultSet.getColumnIndex("thumbnail"));
			art.featuredImage.medium = resultSet.getString(resultSet.getColumnIndex("medium"));
			art.featuredImage.large = resultSet.getString(resultSet.getColumnIndex("large"));
			art.author = resultSet.getString(resultSet.getColumnIndex("author"));
			resultSet.moveToNext();
			result.add(art);
		}
		return result;
	}

	public boolean doesTableExist(String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}
}
