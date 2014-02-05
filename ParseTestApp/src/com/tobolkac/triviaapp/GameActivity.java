package com.tobolkac.triviaapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class GameActivity extends Activity {

  String challengedUser = "";
  public ParseUser currentUser;
  public String category;
  Button[] buttons;                
  boolean isChallenger;      
  String challenger;
  String opponent;
  ParseObject gameScore;
  String gameId;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    TextView userList = (TextView) findViewById(R.id.userList);
    Bundle b = getIntent().getExtras();

    //Challenging
    if(b.getString("com.parse.Data") == null && b.getString("gameId") == null)
    {
      isChallenger = true;
      userList.setText("Challenging");
      challengedUser = getIntent().getStringExtra("opponentName");

    }
    else if(b.getString("gameId") != null)
    {
    	gameId = b.getString("gameId");
    	isChallenger = false;
    	
    	  ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");				  
		  try {
			ParseObject gameObj = query.get(gameId);
			challenger = gameObj.getString("challenger");
			opponent = gameObj.getString("opponent");
			category = gameObj.getString("category");
		} catch (ParseException e1) {e1.printStackTrace();}

    }
    //Being Challenged
    else
    {
      try {
		  JSONObject data = new JSONObject(b.getString("com.parse.Data"));
		  isChallenger = false;
		  challenger = data.getString("challenger");
		  userList.setText("Being Challenged");
		  opponent = data.getString("opponent");
		  gameId = data.getString("gameId");
//	      Toast.makeText(GameActivity.this, gameId, 5).show();
      } catch (JSONException e) {e.printStackTrace();}
    }

    //Setup game data
    category = getIntent().getStringExtra("category");
    
    //Setup Parse
    //Parse.initialize(this, "paHnFob0MGoBuy16Pzg5YPCH6TMOZfgZPXEOY1em", "1WOoBPDmOAu9CbHfvKIGmNIt2mY32mEvBYoLcPLV");
    //ParseAnalytics.trackAppOpened(getIntent());
    currentUser = ParseUser.getCurrentUser();
                
    //Buttons, bad layout
    buttons = new Button[5];
    buttons[0] = (Button)findViewById(R.id.question);
    buttons[1] = (Button)findViewById(R.id.choice1);
    buttons[2] = (Button)findViewById(R.id.choice2);
    buttons[3] = (Button)findViewById(R.id.choice3);
    buttons[4] = (Button)findViewById(R.id.choice4);
    
    
    if(isChallenger)
    	setUpChallenger();
    else
    	setUpOpponent();
  }                
  
  void setUpChallenger(){

	  TextView userName = (TextView) findViewById(R.id.userName);
	  userName.setText("Current user = " + currentUser.getUsername().toString() + "  Opponent = " + challengedUser);
	                                        
	    // Capture button clicks                                                
	    buttons[3].setOnClickListener(new OnClickListener() {
	      public void onClick(View v) {

	    	int time = (int)(Math.random() * 100);

	    	//Push some sample data into the database.
	        gameScore = new ParseObject("Games");
	        gameScore.put("challenger", currentUser.getUsername());
	        gameScore.put("opponent", challengedUser);
	        gameScore.put("challengerCorrect", 1);
	        gameScore.put("numberOfQuestions", 1);
	        gameScore.put("winner", 0);
	        gameScore.put("challengerTime", time);
	      
	        gameScore.saveInBackground(new SaveCallback() {
	            public void done(ParseException e) {
	                if (e == null) {

	        	        gameId = gameScore.getObjectId();
	                            
	        	        JSONObject data = new JSONObject();
	        	        try {
	        	        	data.put("game", "com.tobolkac.FETCH_GAME");
	        	        	data.put("challenger", currentUser.getUsername());
	        	        	data.put("opponent", challengedUser);
	        	        	data.put("gameId", gameId);	        	        
	        			    } catch (JSONException e1) {e1.printStackTrace();}
	        	                
	        	            ParsePush androidPush = new ParsePush();
	        	            androidPush.setMessage(category + " Trivia Challenge from " + currentUser.getUsername());
	        	            androidPush.setData(data);
	        	            androidPush.setChannel(challengedUser);
	        	            androidPush.sendInBackground();                                	                
	                
	        	            
	        				 Intent intent = new Intent(GameActivity.this, MainActivity.class);
	        				 intent.putExtra("gameId", gameId);
	        				 startActivity(intent);
	                }
	            }
	        });	        
                                          
	      }
	    });                                                                                                             
  }
  
  void setUpOpponent(){
	  buttons[3] = (Button)findViewById(R.id.choice3);
	  TextView userName = (TextView) findViewById(R.id.userName);
	  userName.setText("Current user = " + currentUser.getUsername().toString() + "  Opponent = " + opponent +
	                   "\n challenger = " + challenger+
	  					"\nGameId = " + gameId);
	  
	  buttons[3].setOnClickListener(new OnClickListener() {
		public void onClick(View v) {

      	  ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");		
		  
		  try {
			ParseObject gameObj = query.get(gameId);
			int opponentTime = (int)(Math.random() * 100);
			int challengerTime = gameObj.getNumber("challengerTime").intValue();
			
			gameObj.put("opponentTime", opponentTime);
			gameObj.put("opponentCorrect", 2);


			String res = "You won!";
			//1 = challenger 2 = opponennt 0 = in progress
			if(opponentTime < challengerTime){
				gameObj.put("winner", 1);
				res = "You suck!";
			}
			else
				gameObj.put("winner", 2);
			
			Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
			gameObj.saveInBackground();

			 Intent intent = new Intent(GameActivity.this, MainActivity.class);
			 intent.putExtra("gameId", gameId);
			 startActivity(intent);
			 
		} catch (ParseException e1) {e1.printStackTrace();}

		}});	                                 
  }
}
