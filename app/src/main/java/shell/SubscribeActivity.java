package shell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import net.android.shell.R;

public class SubscribeActivity extends Activity{
	WebView webView;
	ProgressDialog progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe);
		TextView leftArrow = (TextView)findViewById(R.id.backBt);
		Typeface font = Typeface.createFromAsset( SubscribeActivity.this.getAssets(), "fonts/font_awesome.ttf" );
		leftArrow.setTypeface(font);
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		//		webView.loadUrl("http://thesourcemagazine.org/");
		String url = "http://thesourcemagazine.org/";
		startWebView(url);

	}
	private void startWebView(String url) {

		WebSettings settings = webView.getSettings();

		settings.setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);

		progressBar = new ProgressDialog(SubscribeActivity.this);
		progressBar.setMessage("Loading...");
		progressBar.show();

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (progressBar.isShowing()) {
					progressBar.dismiss();
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(SubscribeActivity.this, "Error:" + description, Toast.LENGTH_SHORT).show();

			}
		});
		webView.loadUrl(url);
	}
	public void GoBack(View v){
		CloseTheView();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		CloseTheView();
	}
	private void CloseTheView() {
		SubscribeActivity.this.finish();
		SubscribeActivity.this.closeContextMenu();
		Intent myIntent = new Intent(SubscribeActivity.this, MainActivity.class);
		startActivity(myIntent);			
	}
}
