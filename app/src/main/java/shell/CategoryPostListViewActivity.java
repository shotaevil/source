package shell;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import net.android.shell.R;

import model.Article;
import model.CategoryPostListAdapter;
import model.Variables;

public class CategoryPostListViewActivity extends Activity{
	CategoryPostListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_post_preview);
		ArrayList<Article> posts = Variables.category.posts;
		adapter = new CategoryPostListAdapter(CategoryPostListViewActivity.this, posts);
		ListView postList = (ListView) findViewById(R.id.category_post_view);
		TextView title = (TextView)findViewById(R.id.category_name);
		TextView leftArrow = (TextView)findViewById(R.id.backBt);
		Typeface font = Typeface.createFromAsset( CategoryPostListViewActivity.this.getAssets(), "fonts/font_awesome.ttf" );
		Typeface title_font = Typeface.createFromAsset(CategoryPostListViewActivity.this.getAssets(), "fonts/robotoslab_bold.ttf" );
		leftArrow.setTypeface(font);
		title.setText(Variables.category.name);
		title.setTypeface(title_font);
		postList.setAdapter(adapter);

		postList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Variables.post = (Article)adapter.getItem(position);
				CategoryPostListViewActivity.this.finish();
				Intent myIntent = new Intent(CategoryPostListViewActivity.this, NewsActivity.class);
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
		CategoryPostListViewActivity.this.finish();
		CategoryPostListViewActivity.this.closeContextMenu();
		Intent myIntent = new Intent(CategoryPostListViewActivity.this, MainActivity.class);
		startActivity(myIntent);			
	}
}
