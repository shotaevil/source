package model;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.android.shell.R;

public class CategoryPostListAdapter extends BaseAdapter {

	private ArrayList<Article> data;
	//	private String[] data2;
	private Context context;

	public CategoryPostListAdapter(Context context, ArrayList<Article> posts) {
		super();
		this.data = posts;
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = LayoutInflater.from(context).
				inflate(R.layout.top_news, parent, false);
		Article post = data.get(position);

		ImageView image = (ImageView)rowView.findViewById(R.id.image_news);
		TextView topTitle = (TextView) rowView.findViewById(R.id.top_title);
		TextView topAuthor = (TextView) rowView.findViewById(R.id.top_author);
		TextView topContent = (TextView)rowView.findViewById(R.id.top_content);
		Typeface title_font = Typeface.createFromAsset( context.getAssets(), "fonts/robotoslab_bold.ttf" );
		topTitle.setText(Html.fromHtml(post.title));
		topTitle.setTypeface(title_font);
		topContent.setText(Html.fromHtml(post.excerpt));
		topAuthor.setText("by "+post.author + " - " +post.date);
		String imageLink = getImageLink(post.featuredImage);
		if(imageLink!=""){
			Picasso.with(context).load(imageLink).into(image);	
		}else{
			image.setImageResource(R.drawable.logo_blue);//set default image from drawable folder
			image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}

		return rowView;
	}

	private String getImageLink(Image featuredImage) {
		if(featuredImage.large!="")
			return featuredImage.large;
		if(featuredImage.medium!="")
			return featuredImage.medium;
		if(featuredImage.thumbnail!="")
			return featuredImage.thumbnail;
		return "";
	}


}

