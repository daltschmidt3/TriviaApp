package com.tobolkac.triviaapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class MainActivity extends Activity {
	public static String arr[] = {"stuff", "stuff", "stuff", "stuff", "stuff", "stuff", "stuff", "stuff", "stuff", "stuff",
			"stuff", "stuff", "stuff", "stuff"};
	
	public static String arr2[] = {"stuff", "stuff"};
	
	public static List<ParseObject> gamesList;

	List<ParseObject> gl;
	
	public static List<ParseObject> getGamesList()
	{
		return gamesList;
	}
	
	public void setGamesList(List<ParseObject> games)
	{
		gamesList =  games;
	}
	
	public ParseUser currentUser;
	
	PullToRefreshListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Parse.initialize(this, "paHnFob0MGoBuy16Pzg5YPCH6TMOZfgZPXEOY1em", "1WOoBPDmOAu9CbHfvKIGmNIt2mY32mEvBYoLcPLV");
		currentUser = ParseUser.getCurrentUser();
		
		//ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
		//parseInstallation.put("username", currentUser.getUsername());
		//parseInstallation.saveInBackground();
		
		PushService.setDefaultPushCallback(this, ScreenSlideActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		
		ParseAnalytics.trackAppOpened(getIntent());
		
		TextView userRecord = (TextView) findViewById(R.id.userRecord);
		if (currentUser != null) {
			

			userRecord.setText(currentUser.getUsername());
			
			ParseQuery<ParseObject> queryRecord = ParseQuery.getQuery("Record");
			queryRecord.whereEqualTo("user", currentUser.getUsername());

			//get user record
	        ParseObject s;
			try {
				s = queryRecord.getFirst();
				int wins = (Integer) s.getNumber("wins");
		        int loses = (Integer) s.getNumber("loses");
		        Log.d("score", "wins: " + wins + " loses: "+ loses);
				userRecord.setText("Record: " + wins + "-" + loses);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			
			lv = (PullToRefreshListView) findViewById(R.id.gamesList);
			lv.setOnRefreshListener(new OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	                // Your code to refresh the list contents
	                // Make sure you call listView.onRefreshComplete()
	                // once the loading is done. This can be done from here or any
	                // place such as when the network request has completed successfully.
	                fetchTimelineAsync();
	            }
	        });
			
			gamesQuery();
			
			ImageView addGameImage = (ImageView)  findViewById(R.id.newGameImage);
			addGameImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, NewGameActivity.class);
			    	startActivity(intent);
					
				}
			});
			
			
			
		} else {
		  // show the signup or login screen
			Intent intent = new Intent(MainActivity.this, LogInActivity.class);
	    	startActivity(intent);
		}
		
		
		
		
	}
	
	public void gamesQuery()
	{
		ParseQuery<ParseObject> queryGames1 = ParseQuery.getQuery("Games");
		queryGames1.whereEqualTo("challenger", currentUser.getUsername());
		
		ParseQuery<ParseObject> queryGames2 = ParseQuery.getQuery("Games");
		queryGames2.whereEqualTo("opponent", currentUser.getUsername());
	
		List<ParseQuery<ParseObject>> listQueryGames = new ArrayList<ParseQuery<ParseObject>>();
		listQueryGames.add(queryGames1);
		listQueryGames.add(queryGames2);
		
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(listQueryGames);
		mainQuery.orderByDescending("updatedAt");
		//query games and build games list
		mainQuery.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> gamesList, ParseException e) {
		        if (e == null) {
		            Log.d("score", "Retrieved " + gamesList.size() + " games");
		            
		            //ArrayList<ParseObject> gamesArray = new ArrayList<ParseObject>();
					GameDrawArrayAdapter adapter = new GameDrawArrayAdapter(MainActivity.this);
					adapter.addAll(gamesList);
					lv.setAdapter(adapter);
					
					//Shit Dalton Added
					gl = gamesList;
					lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
						if(((TextView)view.findViewById(R.id.gameStatus)).getText().equals("Your Turn"))
						{
							ParseObject game = gl.get(position);
							if( currentUser.getUsername().equals(game.getString("opponent")) && (game.getInt("winner") == 0)){
							    String curGameId = gl.get(position).getObjectId();
	//							Intent intent = new Intent(MainActivity.this, GameActivity.class);
								Intent intent = new Intent(MainActivity.this, ScreenSlideActivity.class);
								intent.putExtra("gameId", curGameId);
							    startActivity(intent);
							}
						}
					}
					});
					
					
					Log.d("score", "Retrieved2 " + gamesList.size() + " games");
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
		    }
		});
	}

	public void fetchTimelineAsync() {
		
		gamesQuery();
		lv.onRefreshComplete();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.logOut:
	        	//clear all channels for this device
	        	ParseInstallation install = new ParseInstallation();
		    	ArrayList name = new ArrayList<String>();
		    	install.put("channels", name);
		    	try {
					install.save();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	ParseUser.logOut();
	        	Intent intent = new Intent(MainActivity.this, LogInActivity.class);
		    	startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
