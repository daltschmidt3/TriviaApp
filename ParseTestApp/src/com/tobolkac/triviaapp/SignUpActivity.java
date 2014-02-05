package com.tobolkac.triviaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {
	
	TextView usernameTextView;
	
	Vibrator vibe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//set up vibrator
		vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		Button signUpButton = (Button) findViewById(R.id.signUpButtonSignUp);
		signUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//vibrate on button press
				vibe.vibrate(40);
				signUpProcess();
				
			}
		});
		
		
	}
	
	public void signUpProcess()
	{
		usernameTextView = (TextView) findViewById(R.id.signUpUserName);
		TextView passwordTextView = (TextView) findViewById(R.id.signUpPassword);
		TextView emailTextView = (TextView) findViewById(R.id.signUpEmail);
		
		ParseUser user = new ParseUser();
		user.setUsername(usernameTextView.getText().toString());
		user.setPassword(passwordTextView.getText().toString());
		user.setEmail(emailTextView.getText().toString());
		user.signUpInBackground(new SignUpCallback() {
			  public void done(ParseException e) {
			    if (e == null) {
			      // Hooray! Let them use the app now.
			    	ParseObject record = new ParseObject("Record");
			    	record.put("user", usernameTextView.getText().toString());
			    	record.put("loses", 0);
			    	record.put("wins", 0);
			    	try {
						record.save();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    	Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
			    	startActivity(intent);
			    	finish();
			    } else {
			      // Sign up didn't succeed. Look at the ParseException
			      // to figure out what went wrong
			    	Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();

			    }
			  }
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
