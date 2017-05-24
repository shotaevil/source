package shell;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import net.android.shell.R;

import model.Article;
import model.CategoryPostListAdapter;
import model.Variables;

public class SearchActivity extends Activity{
	CategoryPostListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_post_preview);
		adapter = new CategoryPostListAdapter(SearchActivity.this, Variables.search);
		ListView postList = (ListView) findViewById(R.id.category_post_view);
		TextView title = (TextView)findViewById(R.id.category_name);
		TextView leftArrow = (TextView)findViewById(R.id.backBt);
		Typeface font = Typeface.createFromAsset( SearchActivity.this.getAssets(), "fonts/font_awesome.ttf" );
		leftArrow.setTypeface(font);
		title.setText("Search results");
		postList.setAdapter(adapter);

		postList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Variables.post = (Article)adapter.getItem(position);
				SearchActivity.this.finish();
				Intent myIntent = new Intent(SearchActivity.this, NewsActivity.class);
				startActivity(myIntent);				
			}
		});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		CloseTheView();
	}
	public void GoBack(View v){
		CloseTheView();
	}

	private void CloseTheView() {
		SearchActivity.this.finish();
		SearchActivity.this.closeContextMenu();
		Intent myIntent = new Intent(SearchActivity.this, MainActivity.class);
		startActivity(myIntent);			
	}
}
