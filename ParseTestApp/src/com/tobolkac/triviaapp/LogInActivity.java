package com.tobolkac.triviaapp;


import java.util.ArrayList;
import java.util.Set;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class LogInActivity extends FragmentActivity {
	
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	
	Vibrator vibe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		
		//set up vibrator
		vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		/*fragmentManager = getFragmentManager();
		
		fragmentTransaction = fragmentManager.beginTransaction();
	    
	    LogInFragment logInFrag = new LogInFragment();
	    fragmentManager.beginTransaction()
        .add(R.id.fragment_container, logInFrag).commit();*/
		
		//Parse.initialize(this, "paHnFob0MGoBuy16Pzg5YPCH6TMOZfgZPXEOY1em", "1WOoBPDmOAu9CbHfvKIGmNIt2mY32mEvBYoLcPLV");
		
		//ParseAnalytics.trackAppOpened(getIntent());	
		
		ParseUser user = new ParseUser();
		user.setUsername("test");
		user.setPassword("123");
		/*user.signUpInBackground(new SignUpCallback() {
			  public void done(ParseException e) {
			    if (e == null) {
			      // Hooray! Let them use the app now.
			    } else {
			      // Sign up didn't succeed. Look at the ParseException
			      // to figure out what went wrong
			    }
			  }
		});*/
		
		
		Button signUpButton = (Button) findViewById(R.id.signUpButton);
		signUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//vibrate on button press
				vibe.vibrate(40);
				Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
		    	startActivity(intent);
				
			}
		});
		Button logInButton = (Button) findViewById(R.id.logInButton);
		logInButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//vibrate on button press
				vibe.vibrate(40);
				logInProcess();
				
			}
		});
		
		//have log in screen show at start
		/*fragmentManager = getFragmentManager();
	    fragmentTransaction = fragmentManager.beginTransaction();
	    
	    LogInFragment logInFrag = new LogInFragment();
	    fragmentTransaction.replace(R.id.logIn_content, logInFrag);
	    fragmentTransaction.commit();*/
		
		
	    
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}
	
	public void showSignUp()
	{
		/*FragmentManager fragmentManager = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	    
	    LogInFragment logInFrag = new LogInFragment();
	    //fragmentTransaction.replace(R.id.signUp_fragment, logInFrag);
	    fragmentTransaction.commit();*/
   		Toast.makeText(LogInActivity.this, "Log In Failed, If not a user please Sign Up", Toast.LENGTH_SHORT).show();

	}
	
	



public void logInProcess() 
{
	TextView usernameTextView = (TextView) findViewById(R.id.logInUserName);
	TextView passwordTextView = (TextView) findViewById(R.id.logInPassword);
	
	ParseUser user = new ParseUser();
	user.setUsername(usernameTextView.getText().toString());
	user.setPassword(
			passwordTextView.getText().toString());
	
	ParseUser.logInInBackground(usernameTextView.getText().toString(), passwordTextView.getText().toString(), 
			new LogInCallback() {
		  public void done(ParseUser user, ParseException e) {
		    if (user != null) {
		      // Hooray! The user is logged in.
		    	//subscribe device to logged in username
		    	//-----------erase previous channels--------------------
		    	
		    	Set<String> ch = PushService.getSubscriptions(getApplicationContext());
		    	ParseInstallation install = new ParseInstallation();
		    	ArrayList name = new ArrayList<String>();
		    	name.add(user.getUsername().toString());
		    	install.put("channels", name);
		    	try {
					install.save();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	
		    	Intent intent = new Intent(LogInActivity.this, MainActivity.class);
		    	startActivity(intent);
		    	finish();
		    } else {
		      // Signup failed. Look at the ParseException to see what happened.
		    	Toast.makeText(LogInActivity.this, "Log In Failed, If not a user please Sign Up", Toast.LENGTH_SHORT).show();
		    }
		  }
		});
}
}
