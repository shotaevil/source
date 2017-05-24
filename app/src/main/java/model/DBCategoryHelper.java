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

public class DBCategoryHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "Source.db";
	public static final String CATEGORY_TABLE_NAME = "Category";

	public DBCategoryHelper(Context context){
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
		db.execSQL(
				"create table Category " +
						"(id integer primary key, apiId integer, name text, icon text)"
				);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS Category");
		onCreate(db);
	}

	public void dropTable(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS Category");
		Log.v("DROP", "Dropped");
	}

	public void createTable(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS Category");
		db.execSQL(
				"create table Category " +
						"(id integer primary key, apiId integer, name text, icon text)"
				);
	}

	public boolean insertCategory (int apiId, String name, String icon)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("apiId", apiId);
		contentValues.put("name", name);
		contentValues.put("icon", icon);

		Cursor c=db.rawQuery("SELECT * FROM Category WHERE apiId='"+apiId+"'", null);
		if(c.moveToFirst())
		{
			Log.v("Error", "Record exist");
			db.update("Category", contentValues, "apiId = ? ", new String[] { Integer.toString(apiId) } );

		}
		else
		{
			Log.v("Error", "Record doesn't exist");
			db.insert("Category", null, contentValues);
		}

		return true;
	}

	public Category getData(int apiId){
		SQLiteDatabase db = this.getReadableDatabase();
		Category result = new Category();
		Cursor res =  db.rawQuery( "select * from Category where apiId="+apiId+"", null );
		res.moveToFirst();
		result.icon = res.getString(res.getColumnIndex("icon"));
		result.name = res.getString(res.getColumnIndex("name"));
		result.id = res.getInt(res.getColumnIndex("apiId"));
		return result;
	}

	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, CATEGORY_TABLE_NAME);
		return numRows;
	}

	public boolean updateCategory (Integer apiId, String name, String icon)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		//contentValues.put("icon", icon);
		db.update("Category", contentValues, "apiId = ? ", new String[] { Integer.toString(apiId) } );
		return true;
	}

	public Integer deleteCategory (Integer apiId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete("Category", 
				"apiId = ? ", 
				new String[] { Integer.toString(apiId) });
	}

	public ArrayList<Category> getAllCategories()
	{
		Log.v("Category", "getAllCategory...");

		ArrayList<Category> result = new ArrayList<Category>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor resultSet = db.rawQuery("Select * from Category",null);
		resultSet.moveToFirst();
		for(int i=0; i<resultSet.getCount(); i++){
			Category cat = new Category();
			cat.icon = resultSet.getString(resultSet.getColumnIndex("icon"));
			cat.name = resultSet.getString(resultSet.getColumnIndex("name"));
			cat.id = resultSet.getInt(resultSet.getColumnIndex("apiId"));
			resultSet.moveToNext();
			result.add(cat);
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
