package shell;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import net.android.shell.R;

import model.Image;
import model.Variables;

public class NewsActivity extends Activity {
	TextView title;
	TextView leftArrow;
	TextView categoryName;
	TextView author;
	Typeface font;
	ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-gxenerated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_preview);
		title = (TextView)findViewById(R.id.top_title);
		leftArrow = (TextView)findViewById(R.id.backBt);
		font = Typeface.createFromAsset( NewsActivity.this.getAssets(), "fonts/font_awesome.ttf" );
		categoryName = (TextView)findViewById(R.id.category_name);
		leftArrow.setTypeface(font);
		categoryName.setText(Html.fromHtml(Variables.post.category.name));
		//		TextView content = (TextView)findViewById(R.id.top_content);
		Typeface title_font = Typeface.createFromAsset( NewsActivity.this.getAssets(), "fonts/robotoslab_bold.ttf" );
		WebView webview = (WebView)this.findViewById(R.id.top_content);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadDataWithBaseURL("", Variables.post.content, "text/html", "UTF-8", "");
		author = (TextView)findViewById(R.id.top_author);
		image = (ImageView)findViewById(R.id.image_news);
		title.setText(Html.fromHtml(Variables.post.title));
		title.setTypeface(title_font);
		author.setText("by "+Variables.post.author + " - " +Variables.post.date);
		//		content.setText(Variables.post.content);
		String imageLink = getImageLink(Variables.post.featuredImage);
		//		new ImageLoadTask(imageLink, image).execute();
		loadImage(imageLink, image);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		CloseTheView();
	}
	private void CloseTheView() {
		NewsActivity.this.finish();
		NewsActivity.this.closeContextMenu();
		Intent myIntent = new Intent(NewsActivity.this, MainActivity.class);
		startActivity(myIntent);			
	}
	public void Share(View v){
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
		startActivity(Intent.createChooser(sharingIntent, "Share using"));
	}
	public void GoBack(View v){
		CloseTheView();
	}
	private void loadImage(String imageLink, ImageView image) {
		if(!imageLink.equals("")){
			Picasso.with(NewsActivity.this).load(imageLink).into(image);	
		}else{
			image.setImageResource(R.drawable.logo_blue);//set default image from drawable folder
			image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

		}
	}
	//	public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
	//
	//		private String url;
	//		private ImageView imageView;
	//
	//		public ImageLoadTask(String url, ImageView imageView) {
	//			this.url = url;
	//			this.imageView = imageView;
	//		}
	//
	//		@Override
	//		protected Bitmap doInBackground(Void... params) {
	//			try {
	//				URL urlConnection = new URL(url);
	//				HttpURLConnection connection = (HttpURLConnection) urlConnection
	//						.openConnection();
	//				connection.setDoInput(true);
	//				connection.connect();
	//				InputStream input = connection.getInputStream();
	//				Bitmap myBitmap = BitmapFactory.decodeStream(input);
	//				return myBitmap;
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//			return null;
	//		}
	//
	//		@Override
	//		protected void onPostExecute(Bitmap result) {
	//			super.onPostExecute(result);
	//			imageView.setImageBitmap(result);
	//		}
	//	}
	private String getImageLink(Image featuredImage) {
		if(!featuredImage.large.equals(""))
			return featuredImage.large;
		if(!featuredImage.medium.equals(""))
			return featuredImage.medium;
		if(!featuredImage.thumbnail.equals(""))
			return featuredImage.thumbnail;
		return "";
	}
}
