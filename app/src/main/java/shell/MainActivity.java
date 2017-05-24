package shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import net.android.shell.R;

import model.Article;
import model.Category;
import model.CategoryListAdapter;
import model.Constants;
import model.DBArticleHelper;
import model.Image;
import model.LoginDialog;
import model.Variables;


@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CategoryListAdapter mAdapter;
	private LinearLayout sectionButton;
	private LinearLayout leftRL;
	private ProgressDialog dialog;
	Button rightBt;
	Button userBt;
	LinearLayout news;
	TextView leftArrow;
	Typeface font;
	EditText search;
	SwipeRefreshLayout swipeLayout;
	ScrollView scrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_expandableListView);
		leftRL = (LinearLayout)findViewById(R.id.side_menu);
		rightBt = (Button)findViewById(R.id.rightBt);
		userBt = (Button)findViewById(R.id.userBt);
		sectionButton = (LinearLayout)findViewById(R.id.sections);
		news = (LinearLayout)findViewById(R.id.top_news);
		leftArrow = (TextView)findViewById(R.id.backBt);
		font = Typeface.createFromAsset( MainActivity.this.getAssets(), "fonts/font_awesome.ttf" );
		scrollView = (ScrollView)findViewById(R.id.main_scroll);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				//				new Handler().postDelayed(new Runnable() {
				//			        @Override public void run() {
				//			            swipeLayout.setRefreshing(false);
				//			        }
				//			    }, 5000);	

				getArticles();
				swipeLayout.setRefreshing(false);

			}
		});
		swipeLayout.setColorScheme(getResources().getColor(android.R.color.holo_blue_bright),
				getResources().getColor(android.R.color.holo_green_light),
				getResources().getColor(android.R.color.holo_orange_light),
				getResources().getColor(android.R.color.holo_red_light));
		leftArrow.setTypeface(font);
		if(Variables.user.email.equals("")){
			rightBt.setVisibility(View.VISIBLE);
			userBt.setVisibility(View.INVISIBLE);
		}else{
			rightBt.setVisibility(View.INVISIBLE);
			userBt.setVisibility(View.VISIBLE);
		}

		addDrawerItems(Variables.categories);
		if(Variables.featuredPosts.size()>0){
			addTopNews(Variables.featuredPosts.get(0));

		}
		addNewsToView(news, Variables.posts);
		addNewsToHorizontalScrollView(Variables.featuredPosts);

		sectionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(leftRL);
			}
		});

		search = (EditText) findViewById(R.id.search);
		search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				String searchText = search.getText().toString();
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if(isNetworkAvailable()){
						new GetSearchResults().execute(Constants.searchURL+searchText+"&access_token="+Variables.token);
					}else{
						Toast.makeText(getApplicationContext(), "No Internet connection. Please check your connection settings and try again.", Toast.LENGTH_LONG).show();
					}
					handled = true;
				}
				return handled;
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
		if (dialog != null)
			dialog.dismiss();
	}


	private void addNewsToHorizontalScrollView(ArrayList<Article> featuredPosts) {
		LinearLayout horizontalScrollView = (LinearLayout)findViewById(R.id.main_horizontal_scrollview);
		for (int i = 1; i < featuredPosts.size(); i++) {
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.horizontal_scroll_item, null);
			TextView scrollTitle = (TextView) view.findViewById(R.id.scroll_title);
			Typeface title_font = Typeface.createFromAsset( MainActivity.this.getAssets(), "fonts/robotoslab_bold.ttf" );
			String imageLink = getImageLink(featuredPosts.get(i).featuredImage);
			ImageView backgroundImage = (ImageView)view.findViewById(R.id.scroll_layout);
			scrollTitle.setText(featuredPosts.get(i).title);
			scrollTitle.setTypeface(title_font);
			loadImage(imageLink, backgroundImage);
			horizontalScrollView.addView(view);
			setOnClickListener(view, i, Variables.featuredPosts);
		}		
	}

	private void addTopNews(Article rlmArticle) {
		TextView title = (TextView)findViewById(R.id.top_title);
		ImageView image = (ImageView)findViewById(R.id.top_image_news);
		Typeface title_font = Typeface.createFromAsset( MainActivity.this.getAssets(), "fonts/robotoslab_bold.ttf" );
		title.setText(Html.fromHtml(rlmArticle.title));
		title.setTypeface(title_font);
		String imageLink = getImageLink(rlmArticle.featuredImage);
		loadImage(imageLink, image);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	public void GoToUserInfo(View v){
		MainActivity.this.finish();
		MainActivity.this.closeContextMenu();
		Intent myIntent = new Intent(MainActivity.this, UserInfoActivity.class);
		startActivity(myIntent);
	}

	public void GoToSubscribe(View v){
		LoginDialog dialog = new LoginDialog(MainActivity.this);
		dialog.showDialog(MainActivity.this);
	}

	private void addNewsToView(LinearLayout news, ArrayList<Article> posts) {
		for(int j=0; j<posts.size(); j++){
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.top_news, null);
			LinearLayout layout = (LinearLayout)view.findViewById(R.id.top_main_news);
			TextView title = (TextView)view.findViewById(R.id.top_title);
			Typeface title_font = Typeface.createFromAsset( MainActivity.this.getAssets(), "fonts/robotoslab_bold.ttf" );
			TextView webview = (TextView)view.findViewById(R.id.top_content);
			//TextView content = (TextView)view.findViewById(R.id.top_content);
			TextView author = (TextView)view.findViewById(R.id.top_author);
			ImageView image = (ImageView)view.findViewById(R.id.image_news);
			String imageLink = getImageLink(posts.get(j).featuredImage);
			title.setText(Html.fromHtml(posts.get(j).title));
			title.setTypeface(title_font);
			//			author.setText("by "+posts.get(j).author + " - " +posts.get(j).date);
			author.setText(posts.get(j).date);

			//content.setText(Variables.posts.get(j).excerpt);
			webview.setText(Html.fromHtml(posts.get(j).excerpt));
			Shader textShader = new LinearGradient(0, 30, 0, 45, new int[] {
					Color.BLACK, Color.GRAY, getResources().getColor(R.color.gray) },
					null, TileMode.CLAMP);
			webview.getPaint().setShader(textShader);
			loadImage(imageLink, image);
			news.addView(view);
			setOnClickListener(layout, j, posts);
		}		
	}

	private void loadImage(String imageLink, ImageView image) {
		if(!imageLink.equals("")){
			Picasso.with(MainActivity.this).load(imageLink).into(image);	
		}else{
			image.setImageResource(R.drawable.logo_blue);//set default image from drawable folder
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
	}

	private String getImageLink(Image featuredImage) {
		if(!featuredImage.large.equals(""))
			return featuredImage.large;
		if(!featuredImage.medium.equals(""))
			return featuredImage.medium;
		if(!featuredImage.thumbnail.equals(""))
			return featuredImage.thumbnail;
		return "";
	}


	private void setOnClickListener(View view, final int j, final ArrayList<Article> posts) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Variables.user.counter++;
				if(Variables.user.hasValidSubscription>0 || Variables.user.counter<5){
					Variables.post = posts.get(j);
					MainActivity.this.finish();
					Intent myIntent = new Intent(MainActivity.this, NewsActivity.class);
					startActivity(myIntent);
				}else{
					GoToSubscribe(v);
				}
			}
		});		
	}

	private void addDrawerItems(ArrayList<Category> categories) {

		mAdapter = new CategoryListAdapter(MainActivity.this, categories);
		mDrawerList.setAdapter(mAdapter);

		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Variables.category = (Category)mAdapter.getItem(position);
				getCategoryPosts(Variables.category.name, Variables.token);
			}

		});
	}

	//		public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
	//	
	//			private String url;
	//			private ImageView imageView;
	//	
	//			public ImageLoadTask(String url, ImageView imageView) {
	//				this.url = url;
	//				this.imageView = imageView;
	//			}
	//	
	//			@Override
	//			protected Bitmap doInBackground(Void... params) {
	//				try {
	//					URL urlConnection = new URL(url);
	//					HttpURLConnection connection = (HttpURLConnection) urlConnection
	//							.openConnection();
	//					connection.setDoInput(true);
	//					connection.connect();
	//					InputStream input = connection.getInputStream();
	//					Bitmap myBitmap = BitmapFactory.decodeStream(input);
	//					return myBitmap;
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//				return null;
	//			}
	//	
	//			@Override
	//			protected void onPostExecute(Bitmap result) {
	//				super.onPostExecute(result);
	//				imageView.setImageBitmap(result);
	//			}
	//	
	//		}
	public void getCategoryPosts(String categoryName, String token) {
		if(isNetworkAvailable()){
			Article article = new Article();
			dialog = new ProgressDialog(MainActivity.this);
			Variables.category.posts = article.getCategoryArticles(MainActivity.this, Constants.categoryPostsURL+categoryName+"&access_token="+token, dialog);
			if(Variables.category.posts.size()>0){
				MainActivity.this.finish();
				Intent myIntent = new Intent(MainActivity.this, CategoryPostListViewActivity.class);
				startActivity(myIntent);
			}else{
				Toast.makeText(getApplicationContext(), "There is no post in selected category.", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), "No Internet connection. Please check your connection settings and try again.", Toast.LENGTH_LONG).show();
		}
	}
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	class GetSearchResults extends AsyncTask<String, String, ArrayList<Article>> {
		private ProgressDialog dialog;
		public GetSearchResults() {
			dialog = new ProgressDialog(MainActivity.this);
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Searching the results, please wait.");
			dialog.show();
		}
		@Override
		protected ArrayList<Article> doInBackground(String... url) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			Article article = new Article();
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
			return article.parsePostResults(responseString);
		}

		@Override
		protected void onPostExecute(ArrayList<Article> result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			super.onPostExecute(result);
			Variables.search = result;
			if(Variables.search.size()>0){
				MainActivity.this.finish();
				Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(myIntent);
			}else
				Toast.makeText(getApplicationContext(), "There is no search results.", Toast.LENGTH_LONG).show();
		}
	}

	private void getArticles() {
		Article _article = new Article();
		ArrayList<Article> articles = _article.getArticles(MainActivity.this, Constants.postURL+"?filter[posts_per_page]=3&access_token="+Variables.token);
		addArticlesToDB(articles);				
	}

	public void addArticlesToDB(ArrayList<Article> articles){
		DBArticleHelper articleDBHelper = new DBArticleHelper(MainActivity.this);
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

		//		if(Variables.posts==null){
		//			Toast.makeText(getApplicationContext(), Variables.error, Toast.LENGTH_LONG).show();
		//			finish();
		//		}else{
		getFeaturedArticles();
		//		}
	}

	private void getFeaturedArticles() {
		Article _article = new Article();
		ArrayList<Article> featuredArticles = _article.getFeaturedArticles(MainActivity.this, Constants.featuredPostsURL+"&filter[posts_per_page]=3&access_token="+Variables.token);
		addFeaturedArticlesToDB(featuredArticles);		
	}

	public void addFeaturedArticlesToDB(ArrayList<Article> featuredArticles) {
		DBArticleHelper articleDBHelper = new DBArticleHelper(MainActivity.this);
		for(int i=0; i<featuredArticles.size(); i++){
			Article article = featuredArticles.get(i);
			articleDBHelper.insertArticle(article.id, article.title, article.content, article.excerpt, article.link, article.date, article.category.id, article.category.name, article.featuredImage.thumbnail, article.featuredImage.medium, article.featuredImage.large, 1, article.author);
		}				
		Variables.featuredPosts = articleDBHelper.getAllArticles(1);
		if(Variables.featuredPosts.size()>0) {
			addTopNews(Variables.featuredPosts.get(0));
		}
		addNewsToView(news, Variables.posts);
		addNewsToHorizontalScrollView(Variables.featuredPosts);
	}


}
