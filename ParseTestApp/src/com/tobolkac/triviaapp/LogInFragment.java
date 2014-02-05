package com.tobolkac.triviaapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LogInFragment extends Fragment{
   @Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
      /**
       * Inflate the layout for this fragment
       */
      
      
      
		
		return inflater.inflate(
			      R.layout.fragment_log_in, container, false);
      
   }
   
   public void logInProcess() 
	{
		TextView usernameTextView = (TextView) getView().findViewById(R.id.logInUserName);
		TextView passwordTextView = (TextView) getView().findViewById(R.id.logInPassword);
		
		ParseUser user = new ParseUser();
		user.setUsername(usernameTextView.getText().toString());
		user.setPassword(
				passwordTextView.getText().toString());
		
		ParseUser.logInInBackground(usernameTextView.getText().toString(), passwordTextView.getText().toString(), 
				new LogInCallback() {
			  public void done(ParseUser user, ParseException e) {
			    if (user != null) {
			      // Hooray! The user is logged in.
			    	Intent intent = new Intent(getActivity(), MainActivity.class);
			    	startActivity(intent);
			    } else {
			      // Signup failed. Look at the ParseException to see what happened.
			    	Toast.makeText(getActivity(), "Log In Failed, If not a user please Sign Up", Toast.LENGTH_SHORT).show();
			    }
			  }
			});
	}
}
