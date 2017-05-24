package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

@SuppressWarnings("deprecation")
public class Article{
	public int id;
	public String title;
	public String content;
	public String excerpt;
	public String link;
	public String date;
	public int featured = 0;
	public Category category = new Category();
	public Image featuredImage = new Image();
	public String author;

	public ArrayList<Article> getFeaturedArticles(Activity activity, String link) {
		ArrayList<Article> _result = new ArrayList<Article>();
		try {
			Log.v("Link", link);
			String result = new GetFeaturedArticles(activity).execute(link).get();
			_result = parsePostResults(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}		
		return _result;
	}

	public ArrayList<Article> getArticles(Activity activity, String link){
		ArrayList<Article> result = new ArrayList<Article>();
		try {
			String _result = new GetArticle(activity).execute(link).get();
			result = parsePostResults(_result);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public ArrayList<Article> getCategoryArticles(Activity activity, String link, ProgressDialog dialog){
		ArrayList<Article> result = new ArrayList<Article>();
		try {
			String _result = new GetCategoryArticles(activity, dialog).execute(link).get();		
			result = parsePostResults(_result);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<Article> parsePostResults(String result) {
		ArrayList<Article> posts = new ArrayList<Article>();
		Object _json;
		try {
			_json = new JSONTokener(result).nextValue();
			if (_json instanceof JSONArray){
				JSONArray json = new JSONArray(result);
				JSONObject _image = new JSONObject();
				for(int i=0; i<json.length(); i++){
					Article post = new Article();
					JSONObject _post = json.getJSONObject(i);
					JSONObject _author = _post.getJSONObject("author");
					JSONObject _terms = _post.getJSONObject("terms");
					JSONObject _category = _terms.getJSONArray("category").getJSONObject(0);
					post.author  = _author.getString("first_name")+" "+_author.getString("last_name");
					post.category.id = _category.getInt("ID");
					post.category.name = _category.getString("name");
					post.content = _post.getString("content");
					post.date = parseDate(_post.getString("date"));
					post.excerpt = _post.getString("excerpt");
					post.id = _post.getInt("ID");
					post.link = _post.getString("link");
					post.title = _post.getString("title");
					if(_post.isNull("featured_image")){
						post.featuredImage = new Image();
					}else{
						_image = _post.getJSONObject("featured_image");
						post.featuredImage.id = _image.getInt("ID");
						post.featuredImage.title = _image.getString("title");
						if(!_image.getJSONObject("attachment_meta").getJSONObject("sizes").isNull("thumbnail")){
							post.featuredImage.thumbnail = _image.getJSONObject("attachment_meta").getJSONObject("sizes").getJSONObject("thumbnail").getString("url");
						}
						if(!_image.getJSONObject("attachment_meta").getJSONObject("sizes").isNull("medium")){
							post.featuredImage.medium = _image.getJSONObject("attachment_meta").getJSONObject("sizes").getJSONObject("medium").getString("url");
						}
						if(!_image.getJSONObject("attachment_meta").getJSONObject("sizes").isNull("large")){
							post.featuredImage.large = _image.getJSONObject("attachment_meta").getJSONObject("sizes").getJSONObject("large").getString("url");
						}
					}
					posts.add(post);
				}
			}
			else {
				if (_json instanceof JSONObject){
					String message = "";
					JSONObject json = new JSONObject(result);
					message = json.getString("message");
					Variables.error = message;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return posts;
	}

//	private String getFullHTMLPage(String string) {
//		String result = "<!DOCTYPE html><html><head><style>"
//		return null;
//	}

	private String parseDate(String date) {
		String result = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dateTime = format.parse(date);
			String month = (String) android.text.format.DateFormat.format("MMMM", dateTime);
			String day = (String) android.text.format.DateFormat.format("dd", dateTime);
			String year = (String) android.text.format.DateFormat.format("yyyy", dateTime);
			result = month+" "+day+", "+year;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}

class GetFeaturedArticles extends AsyncTask<String, String, String> {
	Context context;

	public GetFeaturedArticles(Context context) {
		this.context = context; 
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		try {
			response = httpclient.execute(new HttpGet(url[0]));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				responseString = out.toString();
				out.close();
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			//TODO Handle problems..
			e.printStackTrace();
		} catch (IOException e) {
			//TODO Handle problems..
			e.printStackTrace();
		}
		return responseString;
	}
}

class GetArticle extends AsyncTask<String, String, String> {
	Context context;

	public GetArticle(Context context) {
		this.context = context;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		try {
			response = httpclient.execute(new HttpGet(url[0]));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				responseString = out.toString();
				out.close();
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			//TODO Handle problems..
		} catch (IOException e) {
			//TODO Handle problems..
		}
		return responseString;
	}
}

class GetCategoryArticles extends AsyncTask<String, String, String> {
	ProgressDialog dialog;
	Context context;
	
	public GetCategoryArticles(Context context, ProgressDialog dialog) {
		this.dialog = dialog;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Searching the articles, please wait...");
		dialog.show();
	}

	@Override
	protected String doInBackground(String... url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		try {
			response = httpclient.execute(new HttpGet(url[0].replace(" ", "%20")));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				responseString = out.toString();
				out.close();
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			//TODO Handle problems..
		} catch (IOException e) {
			//TODO Handle problems..
		}
		dialog.dismiss();
		return responseString;
	}
}
//
//@Override
//protected void onPostExecute(ArrayList<Article> result) {
//	super.onPostExecute(result);
//	Variables.category.posts = result;
//	MainActivity.this.finish();
//	Intent myIntent = new Intent(MainActivity.this, CategoryPostListViewActivity.class);
//	startActivity(myIntent);
//}
//}
