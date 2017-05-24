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

public class DBUserHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "Source.db";
	public static final String USER_TABLE_NAME = "User";

	public DBUserHelper(Context context){
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
				"create table User " +
						"(id integer primary key autoincrement not null, firstName text, lastName text, email text, hasValidSubscription integer, subscriptionExpires text, packageName text, counter integer)"
				);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS User");
		onCreate(db);
	}

	public void dropTable(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS User");
		Log.v("DROP", "Dropped");
	}

	public void createTable(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS User");
		db.execSQL(
				"create table User " +
						"(id integer primary key autoincrement not null, firstName text, lastName text, email text, hasValidSubscription integer, subscriptionExpires text, packageName text, counter integer)"
				);
	}

	public boolean insertUser (String firstName, String lastName, String email,
			int hasValidSubscription, String subscriptionExpires,
			String packageName, int counter)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("firstName", firstName);
		contentValues.put("lastName", lastName);
		contentValues.put("email", email);
		contentValues.put("hasValidSubscription", hasValidSubscription);
		contentValues.put("subscriptionExpires", subscriptionExpires);
		contentValues.put("packageName", packageName);
		contentValues.put("counter", counter);


//		Cursor c=db.rawQuery("SELECT * FROM Category WHERE id='"+apiId+"'", null);
//		if(c.moveToFirst())
//		{
//			Log.v("Error", "Record exist");
//			db.update("Category", contentValues, "apiId = ? ", new String[] { Integer.toString(apiId) } );
//
//		}
//		else
//		{
//			Log.v("Error", "Record doesn't exist");
			db.insert("User", null, contentValues);
//		}

		return true;
	}

	public User getData(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		User result = new User();
		Cursor res =  db.rawQuery( "select * from User where id="+id+"", null );
		res.moveToFirst();
		result.firstName = res.getString(res.getColumnIndex("firstName"));
		result.lastName = res.getString(res.getColumnIndex("lastName"));
		result.email = res.getString(res.getColumnIndex("email"));
		result.hasValidSubscription = res.getInt(res.getColumnIndex("hasValidSubscription"));
		result.subscriptionExpires = res.getString(res.getColumnIndex("subscriptionExpires"));
		result.packageName = res.getString(res.getColumnIndex("packageName"));
		result.counter = res.getInt(res.getColumnIndex("counter"));
		return result;
	}

	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
		return numRows;
	}

	public boolean updateUser (Integer id, String firstName, String lastName, String email,
			int hasValidSubscription, String subscriptionExpires,
			String packageName, int counter)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("firstName", firstName);
		contentValues.put("lastName", lastName);
		contentValues.put("email", email);
		contentValues.put("hasValidSubscription", hasValidSubscription);
		contentValues.put("subscriptionExpires", subscriptionExpires);
		contentValues.put("packageName", packageName);
		contentValues.put("counter", counter);
		db.update("User", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
		return true;
	}
	
	public boolean updateUserCounter (Integer id, int counter)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("counter", counter);
		db.update("User", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
		return true;
	}

	public Integer deleteUser (Integer id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete("User", 
				"apiId = ? ", 
				new String[] { Integer.toString(id) });
	}

	public ArrayList<User> getAllUsers()
	{
		ArrayList<User> result = new ArrayList<User>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor resultSet = db.rawQuery("Select * from User",null);
		resultSet.moveToFirst();
		for(int i=0; i<resultSet.getCount(); i++){
			User user = new User();
			user.id = resultSet.getInt(resultSet.getColumnIndex("id"));
			user.firstName = resultSet.getString(resultSet.getColumnIndex("firstName"));
			user.lastName = resultSet.getString(resultSet.getColumnIndex("lastName"));
			user.email = resultSet.getString(resultSet.getColumnIndex("email"));
			user.hasValidSubscription = resultSet.getInt(resultSet.getColumnIndex("hasValidSubscription"));
			user.subscriptionExpires = resultSet.getString(resultSet.getColumnIndex("subscriptionExpires"));
			user.packageName = resultSet.getString(resultSet.getColumnIndex("packageName"));
			user.counter = resultSet.getInt(resultSet.getColumnIndex("counter"));
			resultSet.moveToNext();
			result.add(user);
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
