package model;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.android.shell.R;

public class CategoryListAdapter extends BaseAdapter {

	private ArrayList<Category> data;
//	private String[] data2;
	private Context context;

	public CategoryListAdapter(Context context, ArrayList<Category> categories) {
		super();
		this.data = categories;
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
				inflate(R.layout.category_list_item, parent, false);

		TextView text1 = (TextView) rowView.findViewById(R.id.text1);
		TextView icon = (TextView) rowView.findViewById(R.id.icon);
		Typeface font = Typeface.createFromAsset( context.getAssets(), "fonts/font_awesome.ttf" );
		icon.setTypeface(font);
		text1.setText(Html.fromHtml(data.get(position).name));
//		icon.setText(context.getString(R.string.icon_heart));
		icon.setText(data.get(position).icon);
//		icon.setImageResource(data2[position]);

		return rowView;
	}

}

