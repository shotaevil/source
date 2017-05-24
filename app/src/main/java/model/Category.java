package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import net.android.shell.R;

public class Category{
	public int id = 0;
	public String name = "";
	public ArrayList<Article> posts;
	public String icon;

	public ArrayList<Category> getCategories(Activity activity, String link) {
		ArrayList<Category> results = new ArrayList<Category>();
		String _result;
		try {
			_result = new GetCategory(activity).execute(link).get();
			JSONArray json = new JSONArray(_result);;
			for(int i=0; i<json.length(); i++){
				Category category = new Category();
				JSONObject _category = json.getJSONObject(i);
				category.id = _category.getInt("ID");
				category.name = _category.getString("name");
				category.icon = getCategoryIcon(activity, category.name);
				results.add(category);
			}
			try {
				json = new JSONArray(_result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return results;
	}

	public String getCategoryIcon(Activity activity, String name) {
		// TODO Auto-generated method stub
		Resources resources = activity.getResources();
		if(name.contains("Agriculture"))
			return resources.getString(R.string.icon_leaf);
		if(name.contains("Cover"))
			return resources.getString(R.string.icon_bookmark);
		if(name.contains("Energy"))
			return resources.getString(R.string.icon_glass);
		if(name.contains("Environment"))
			return resources.getString(R.string.icon_cloud);
		if(name.contains("Events"))
			return resources.getString(R.string.icon_calendar);
		if(name.contains("Featured"))
			return resources.getString(R.string.icon_star);
		if(name.contains("Features"))
			return resources.getString(R.string.icon_star_white);
		if(name.contains("From"))
			return resources.getString(R.string.icon_user);
		if(name.contains("Industry"))
			return resources.getString(R.string.icon_industry);
		if(name.contains("Insight"))
			return resources.getString(R.string.icon_eye);
		if(name.equals("Interview"))
			return resources.getString(R.string.icon_microphone);
		if(name.equals("Interviews"))
			return resources.getString(R.string.icon_globe);
		if(name.contains("News"))
			return resources.getString(R.string.icon_flag);
		if(name.contains("Record"))
			return resources.getString(R.string.icon_microphone);
		if(name.contains("Opinion"))
			return resources.getString(R.string.icon_opinion);
		if(name.contains("Review"))
			return resources.getString(R.string.icon_glass);
		if(name.contains("Urban"))
			return resources.getString(R.string.icon_building);
		if(name.contains("Urbanisation"))
			return resources.getString(R.string.icon_cubes);
		if(name.contains("Utitlities"))
			return resources.getString(R.string.icon_customization);
		if(name.equals("Wastewater"))
			return resources.getString(R.string.icon_filter);
		return resources.getString(R.string.icon_globe);
	}
}
class GetCategory extends AsyncTask<String, String, String> {
	Context context;

	public GetCategory(Context context) {
		this.context = context;
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
}
