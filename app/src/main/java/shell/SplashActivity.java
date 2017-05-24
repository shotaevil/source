package shell;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import net.android.shell.R;

import model.Article;
import model.Category;
import model.Constants;
import model.DBArticleHelper;
import model.DBCategoryHelper;
import model.Variables;

@SuppressWarnings("deprecation")
public class SplashActivity extends Activity {
	public static final String TAG = SplashActivity.class.getName();
	DBCategoryHelper categoryDBHelper;
	DBArticleHelper articleDBHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		categoryDBHelper = new DBCategoryHelper(SplashActivity.this);
		articleDBHelper = new DBArticleHelper(SplashActivity.this);
		
		if(isNetworkAvailable()){
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("username", Constants.username);
			data.put("password", Constants.password);
			data.put("secret_key", Constants.secret_key);
			data.put("client_id", Constants.client_id);
			AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);
			asyncHttpPost.execute(Constants.authorizationURL);		
		}else{
			getDataFromDB();
		}

	}

	private void getDataFromDB() {
		if(categoryDBHelper.doesTableExist("Category")){
			Variables.categories = categoryDBHelper.getAllCategories();
		}
		if(categoryDBHelper.doesTableExist("Article")){
			Variables.posts = articleDBHelper.getAllArticles(0);
			Variables.featuredPosts = articleDBHelper.getAllArticles(1);
		}
		openMainActivity();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public class AsyncHttpPost extends AsyncTask<String, String, String> {
		private HashMap<String, String> mData = null;// post data

		public AsyncHttpPost(HashMap<String, String> data) {
			mData = data;
		}

		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;
			String str = "";
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL

			try {
				// set up post data
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				Iterator<String> it = mData.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
				}

				post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));

				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			Log.v("Result", result);
			JSONObject json;
			String token = "";
			int status_code = 0;
			try {
				json = new JSONObject(result);
				token = json.getString("message");
				status_code = json.getInt("code");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(status_code!=403 && status_code!=1000 && status_code!=1006){
				Variables.token = token;
				Category _category = new Category();
				ArrayList<Category> categories = _category.getCategories(SplashActivity.this, Constants.categoryURL+"?access_token="+Variables.token);
				addCategoriesToDB(categories);
			}else{
				SplashActivity.this.finish();
				Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void addFeaturedArticlesToDB(ArrayList<Article> featuredArticles) {
		for(int i=0; i<featuredArticles.size(); i++){
			Article article = featuredArticles.get(i);
			articleDBHelper.insertArticle(article.id, article.title, article.content, article.excerpt, article.link, article.date, article.category.id, article.category.name, article.featuredImage.thumbnail, article.featuredImage.medium, article.featuredImage.large, 1, article.author);
		}				
		Variables.featuredPosts = articleDBHelper.getAllArticles(1);
		if(Variables.featuredPosts==null){
			Toast.makeText(getApplicationContext(), Variables.error, Toast.LENGTH_LONG).show();
			finish();
		}else{
			openMainActivity();
		}		
	}

	public void addCategoriesToDB(ArrayList<Category> categories) {
		if(categoryDBHelper.doesTableExist("Category")){
			Log.v("Category", "Ima tabele");
			for(int i=0; i<categories.size(); i++){
				categoryDBHelper.updateCategory(categories.get(i).id, categories.get(i).name, categories.get(i).icon);
			}
		}else{
			categoryDBHelper.createTable();
			for(int i=0; i<categories.size(); i++){
				categoryDBHelper.insertCategory(categories.get(i).id, categories.get(i).name, categories.get(i).icon);
			}				
		}
		Variables.categories = categoryDBHelper.getAllCategories();

		if(Variables.categories.size()<=0)
			Variables.categories = categories;
		getArticles();
	}

	public void addArticlesToDB(ArrayList<Article> articles){
		if(articleDBHelper.doesTableExist("Article")){
			Log.v("Article", "Ima tabele");
			for(int i=0; i<articles.size(); i++){
				Article article = articles.get(i);
				articleDBHelper.updateCategory(article.id, article.title, article.content, article.excerpt, article.link, article.date, article.category.id, article.category.name, article.featuredImage.thumbnail, article.featuredImage.medium, article.featuredImage.large, 0, article.author);
			}
		}else{
			articleDBHelper.createTable();
			for(int i=0; i<articles.size(); i++){
				Article article = articles.get(i);
				articleDBHelper.insertArticle(article.id, article.title, article.content, article.excerpt, article.link, article.date, article.category.id, article.category.name, article.featuredImage.thumbnail, article.featuredImage.medium, article.featuredImage.large, 0, article.author);
			}
		}
		Variables.featuredPosts = articleDBHelper.getAllArticles(1);						
		Variables.posts = articleDBHelper.getAllArticles(0);

		if(Variables.posts==null){
			Toast.makeText(getApplicationContext(), Variables.error, Toast.LENGTH_LONG).show();
			finish();
		}else{
			getFeaturedArticles();
		}
	}

	private void getArticles() {
		Article _article = new Article();
		ArrayList<Article> articles = _article.getArticles(SplashActivity.this, Constants.postURL+"?access_token="+Variables.token);
		addArticlesToDB(articles);				
	}

	private void getFeaturedArticles() {
		Article _article = new Article();
		ArrayList<Article> featuredArticles = _article.getFeaturedArticles(SplashActivity.this, Constants.featuredPostsURL+"&access_token="+Variables.token);
		Log.v("Featured articles", String.valueOf(featuredArticles.size()));
		addFeaturedArticlesToDB(featuredArticles);
	}

	private void openMainActivity() {
		Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
		SplashActivity.this.startActivity(myIntent);
		SplashActivity.this.finish();		
	}
}



