package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.android.shell.R;
import shell.SubscribeActivity;

public class LoginDialog {
	String username = "";
	String password = "";
	Activity activity;
	DBUserHelper userDBHelper;

	public LoginDialog(Activity activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		userDBHelper = new DBUserHelper(activity);
	}
	public void showDialog(final Activity activity){
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.login);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.dimAmount=0.7f;
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		RelativeLayout close = (RelativeLayout)dialog.findViewById(R.id.close_layout);
		Button signInBt = (Button)dialog.findViewById(R.id.sign_in_bt);
		Button subscribeBt = (Button)dialog.findViewById(R.id.subscribe_bt);
		Button forgotBt = (Button)dialog.findViewById(R.id.forgot_pass_bt);
		EditText _username = (EditText)dialog.findViewById(R.id.username);
		EditText _password = (EditText)dialog.findViewById(R.id.password);

		//		username = _username.getText().toString();
		//		password = _password.getText().toString();

		//valid user
		username = "valid@thesourcemagazine.org";
		password = "p(ksaaI6x)D&r!lPMY7n)G)Z";

		//invalid user
		//		username = "invalid@thesourcemagazine.org";
		//		password = "sJ*#Ce3HmnCvUpAMq#XUhGhX";

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();	// TODO Auto-generated method stub

			}
		});
		signInBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isNetworkAvailable()){
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("username", username);
					data.put("password", password);
					data.put("secret_key", Constants.secret_key);
					data.put("client_id", Constants.client_id);

					AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data, dialog);
					asyncHttpPost.execute(Constants.authorizationURL);	
				}else{
					Toast.makeText(activity, "No Internet connection. Please check your connection settings and try again.", Toast.LENGTH_LONG).show();
				}
			}
		});
		subscribeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();	// TODO Auto-generated method stub
				Intent myIntent = new Intent(activity, SubscribeActivity.class);
				activity.startActivity(myIntent);
			}
		});
		forgotBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();	// TODO Auto-generated method stub
			}
		});
		dialog.show();
	}

	class GetUserInfo extends AsyncTask<String, String, String> {
		ProgressDialog mDialog;
		Dialog dialog;

		public GetUserInfo(Dialog dialog) {
			this.dialog = dialog;
			mDialog = new ProgressDialog(activity);
		}

		@Override
		protected void onPreExecute() {
			mDialog.setMessage("Searching the user info. Please wait...");
			mDialog.show();
		}

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


		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.v("UserInfo", result);
			JSONObject json;
			User user = new User();
			String message = "";
			int status_code = 0;
			try {
				json = new JSONObject(result);
				message = json.getString("message");
				status_code = json.getInt("code");
				user.firstName = json.getString("first_name");
				user.lastName = json.getString("last_name");
				user.email = json.getString("email");
				user.hasValidSubscription = json.getInt("has_valid_subscription");
				user.subscriptionExpires = json.getString("subscription_expires");
				user.packageName = json.getString("package");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("Token", message);
			Log.v("Status code", ""+status_code);
			if(status_code<2000){
				Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}else{
				userDBHelper.createTable();
				userDBHelper.insertUser(user.firstName, user.lastName, user.email, user.hasValidSubscription, user.subscriptionExpires, user.packageName, user.counter);
				//				Variables.user = user;
				Variables.user = userDBHelper.getAllUsers().get(0);
				Log.v("User", " \n"+Variables.user.id+" \n"+Variables.user.firstName + " \n"+Variables.user.lastName+" \n"+Variables.user.email+" \n"+Variables.user.hasValidSubscription+" \n"+Variables.user.subscriptionExpires+" \n"+Variables.user.packageName+" \n"+Variables.user.counter);			
//				dialog.dismiss();
//				mDialog.cancel();
				Button rightBt = (Button)activity.findViewById(R.id.rightBt);
				Button userBt = (Button)activity.findViewById(R.id.userBt);

				if(Variables.user.email.equals("")){
					rightBt.setVisibility(View.VISIBLE);
					userBt.setVisibility(View.INVISIBLE);
				}else{
					rightBt.setVisibility(View.INVISIBLE);
					userBt.setVisibility(View.VISIBLE);
				}
			}
			mDialog.cancel();
			dialog.dismiss();

		}
	}

	public class AsyncHttpPost extends AsyncTask<String, String, String> {
		private HashMap<String, String> mData = null;// post data
		Dialog mDialog = null;

		/**
		 * constructor
		 * @param dialog 
		 */
		public AsyncHttpPost(HashMap<String, String> data, Dialog dialog) {
			Log.v("data", data.toString());
			mData = data;
			mDialog = dialog;
		}

		/**
		 * background
		 */
		@SuppressWarnings("deprecation")
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
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
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
			Log.v("Token", token);
			Log.v("Status code", ""+status_code);
			if(status_code<2000){
				Toast.makeText(activity.getApplicationContext(), token, Toast.LENGTH_LONG).show();
			}else{
				Variables.token = token;
				new GetUserInfo(mDialog).execute(Constants.userInfoURL+"?access_token="+Variables.token);
			}

		}
	}
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
