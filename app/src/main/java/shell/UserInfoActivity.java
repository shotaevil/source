package shell;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.android.shell.R;

import model.DBUserHelper;
import model.User;
import model.Variables;

public class UserInfoActivity extends Activity{
	TextView note;
	TextView firstNameLabel;
	TextView lastNameLabel;
	TextView emailLabel;
	TextView expirationDate;
	TextView packageNameLabel;
	TextView packageName;
	TextView backBt;
	EditText _firstname;
	EditText _lastname;
	EditText _email;
	Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);

		note = (TextView)findViewById(R.id.note_label);
		firstNameLabel = (TextView)findViewById(R.id.first_name_label);
		lastNameLabel = (TextView)findViewById(R.id.last_name_label);
		emailLabel = (TextView)findViewById(R.id.email_label);
		expirationDate = (TextView)findViewById(R.id.expiration_label);
		packageNameLabel = (TextView)findViewById(R.id.package_name_label);
		packageName = (TextView)findViewById(R.id.package_name);
		_firstname = (EditText)findViewById(R.id.firstname);
		_lastname = (EditText)findViewById(R.id.lastname);
		_email = (EditText)findViewById(R.id.email);
		backBt = (TextView)findViewById(R.id.backBt);
		font = Typeface.createFromAsset(UserInfoActivity.this.getAssets(), "fonts/font_awesome.ttf" );
		backBt.setTypeface(font);

		if(Variables.user.hasValidSubscription==1){
			note.setText(getResources().getString(R.string.active_subscription));
			expirationDate.setText("It will end at "+Variables.user.subscriptionExpires);
		}else{
			note.setText(getResources().getString(R.string.inactive_subscription));
			expirationDate.setVisibility(View.INVISIBLE);
		}
		_firstname.setText(Variables.user.firstName);
		_lastname.setText(Variables.user.lastName);
		_email.setText(Variables.user.email);
		packageName.setText(Variables.user.packageName);
	}

	public void GoBack(View v){
		CloseTheView();
	}

	private void CloseTheView() {
		UserInfoActivity.this.finish();
		UserInfoActivity.this.closeContextMenu();
		Intent myIntent = new Intent(UserInfoActivity.this, MainActivity.class);
		startActivity(myIntent);			
	}

	public void Logout(View v){
		DBUserHelper userHelper = new DBUserHelper(UserInfoActivity.this);
		userHelper.dropTable();
		Variables.user = new User();
		CloseTheView();
	}

	public void RenewSubscription(View v) {
		UserInfoActivity.this.finish();
		UserInfoActivity.this.closeContextMenu();
		Intent myIntent = new Intent(UserInfoActivity.this, SubscribeActivity.class);
		startActivity(myIntent);
	}
}
