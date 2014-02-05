/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tobolkac.triviaapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because {@link ViewPager}
 * automatically plays such an animation when calling {@link ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see ScreenSlidePageFragment
 */
public class ScreenSlideActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
   public static final int NUM_PAGES = 10;
   
   public int timeTaken;
  
   
   //new globals
   public boolean isChallenger;
   public String challengedUser;
   public String gameId;
   public String challenger;
   public String opponent;
   
   public int[] correctArray;
   public int numCorrect;
   public int getNumCorrect()
   {
	   return numCorrect;
   }
   public int getNumPages()
   {
	   return NUM_PAGES;
   }
   
   public ArrayList<Integer> questionNums;
   
   public String cat;
   
   public ParseObject q;
   
   public List<ParseObject> questions;
   
   int expiredQuestionNumber = -1;
   
   public ParseObject getQuestion(int i)
   {
	   return questions.get(i);
   }
   
   ParseObject gameScore;
   ParseUser currentUser;
   
   CorrectQuestionView questionProgress;
   
   Vibrator vibe;
   
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    public static ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    public PagerAdapter mPagerAdapter;
    
    private long startTime = 0L;

	private Handler customHandler = new Handler();

	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	
	long millis;
	
	public long getTimeInMilliseconds()
	{
		return millis;
	}
	
	TextView questionTextView;
	
	public TextView getQuestionTextView(){
		return questionTextView;
	}
	
	public String getFormattedTime(){
		long totalTime = getTimeInMilliseconds();

        int totalSeconds = (int) (millis / 1000);
        int totalMinutes = totalSeconds / 60;
        totalSeconds = totalSeconds % 60;

        String formattedTime = totalMinutes + ":"+totalSeconds;
		
        return formattedTime;
	}
	
	public boolean isExpiring;
	int questionSeconds;
	public int getQuestionTime(){		
		return questionSeconds;
	}
	
	TextView timerTextView;
	
	
	public long lagMillis;
	
	public void setLagMillis(){
		lagMillis = millis;
	}
	
	
	
    CountDownTimer cdt = new CountDownTimer(61000, 1000) {

	     public void onTick(long millisUntilFinished) {
	    	 millis = millisUntilFinished;
	            int seconds = (int) (millis / 1000);
	            int minutes = seconds / 60;
	            seconds = seconds % 60;
	            
	            
	            

	            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
	     }

	     public void onFinish() {
	         timerTextView.setText("0");
	        
		    	Toast.makeText(ScreenSlideActivity.this, "correct: "+numCorrect + " Used maximum Time!", Toast.LENGTH_SHORT).show();
		    	evalGame(numCorrect, getTimeInMilliseconds());
			
	     }
	  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        Bundle b = getIntent().getExtras();
        Parse.initialize(this, "paHnFob0MGoBuy16Pzg5YPCH6TMOZfgZPXEOY1em", "1WOoBPDmOAu9CbHfvKIGmNIt2mY32mEvBYoLcPLV");
        ParseAnalytics.trackAppOpened(getIntent());
//        questionNums = b.getIntArray("questionsNumArray");
//        cat = b.getString("category");
        timerTextView = (TextView) findViewById(R.id.gameTimer);
        timerTextView.setText("2:00");
        cdt.start();
        
        //set up vibrator
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        //Challenging (Creating Game)
        if(b.getString("com.parse.Data") == null && b.getString("gameId") == null)
        {
          isChallenger = true;
//          userList.setText("Challenging");
          challengedUser = getIntent().getStringExtra("opponentName");
          cat = b.getString("category");
          Log.d("category", "category: " + cat);
          questionNums = getIntent().getIntegerArrayListExtra("questionNumArray");
          
          //query to batch retrieve questions
          ParseQuery<ParseObject> queryQuestions = ParseQuery.getQuery("Questions");
          queryQuestions.whereEqualTo("category", cat);
          queryQuestions.whereContainedIn("categoryIndex", questionNums);
          /*queryQuestions.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				Log.d("questions retrieved", "number: " + objects.size());
				questions = objects;
			}
          });*/
          
          correctArray = new int[questionNums.size()];
          for (int i =0; i<correctArray.length; i++){
          	correctArray[i] = 0;
          }
          try {
			questions = queryQuestions.find();
          } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
          }
          Log.d("questionNums after", "In ScrrenSlideActivity: "+questionNums.toString() + " " + questions.size());
          

        }
        else{
        //Being Challenged (From Homescreen)
	        if(b.getString("gameId") != null)
	        {
	        	gameId = b.getString("gameId");
	        }
	        else
	        {
	            try {
	      		  JSONObject data = new JSONObject(b.getString("com.parse.Data"));
	      		  gameId = data.getString("gameId");
	            } catch (JSONException e) {e.printStackTrace();}	
	        }
	        	isChallenger = false;
	        	
	        	  ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");				  
	    		  try {
	    			ParseObject gameObj = query.get(gameId);
	    			challenger = gameObj.getString("challenger");
	    			opponent = gameObj.getString("opponent");
	    			cat = gameObj.getString("category");
	    			//getting question array from game object
	    			JSONArray q = gameObj.getJSONArray("questionsArray");
	    			Log.d("Json array", "length: "+ q.length());
	    			ArrayList<Integer> stuff = new ArrayList<Integer>();
	    			for (int s = 0; s<q.length(); s++)
	    			{
	    				stuff.add((Integer) q.get(s));
	    			}
	    			
	    			Log.d("opponent questions Array", stuff.toString());
	    			
	    			//query to grab questions by category and number
	    			ParseQuery<ParseObject> queryQuestions = ParseQuery.getQuery("Questions");
    	            queryQuestions.whereEqualTo("category", cat);
    	            queryQuestions.whereContainedIn("categoryIndex", stuff);
    	            
    	            correctArray = new int[stuff.size()];
    	            for (int i =0; i<correctArray.length; i++){
    	            	correctArray[i] = 0;
    	            }
    	            try {
    				  questions = queryQuestions.find();
    	            } catch (ParseException e) {
    			    	// TODO Auto-generated catch block
    				  e.printStackTrace();
    	            }
	    		  } catch (ParseException e1) {e1.printStackTrace();} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
        }
        
        
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager manager = getSupportFragmentManager();
        mPagerAdapter = new ScreenSlidePagerAdapter(manager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.

    	        questionProgress.setCorrectArray(correctArray);
    	        questionProgress.setNumCorrect(position + 1);
    	        
                invalidateOptionsMenu();
            }
        });
        
        timerTextView = (TextView) findViewById(R.id.gameTimer);
        startTime = System.currentTimeMillis();
        
        questionProgress = ((CorrectQuestionView) findViewById(R.id.questionProgress));
        questionProgress.setQuestionProgress(true);
        questionProgress.setNumCorrect(1);
        questionProgress.setCorrectArray(correctArray);
        
        
    }
    

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        //menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        /*MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            /*case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;*/

            /*case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

		@Override
        public Fragment getItem(int position) {
			Log.d("position", "position: "+position);
			return ScreenSlidePageFragment.create(position, cat, getQuestion(position));
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    
    public void evalGame(int numCorrect, long time){
    	cdt.cancel();
    	if(isChallenger == true){

    		currentUser = ParseUser.getCurrentUser();
    		
	    	//create game object of current game and push to database
    		gameScore = new ParseObject("Games");
	        gameScore.put("challenger", currentUser.getUsername());
	        gameScore.put("opponent", challengedUser);
	        gameScore.put("challengerCorrect",  numCorrect);
	        gameScore.put("winner", 0);
	        gameScore.put("challengerTime", time/1000);
	        gameScore.put("category", cat);
	        gameScore.put("numberOfQuestions", 10);
	        JSONArray qNUms = new JSONArray();
	        for (int a : questionNums)
	        {
	        	qNUms.put(a);
	        }
	        gameScore.put("questionsArray", qNUms);
	        JSONArray cArray = new JSONArray();
	        for (int a : correctArray)
	        {
	        	cArray.put(a);
	        }
        	gameScore.put("chalCorrectArray", cArray);
	      
	        gameScore.saveInBackground(new SaveCallback() {
	            public void done(ParseException e) {
	                if (e == null) {

	        	        String gameId = gameScore.getObjectId();
	                            
	                	
	        	        JSONObject data = new JSONObject();
	        	        try {
	        	        	data.put("game", "com.tobolkac.FETCH_GAME");
	        	        	data.put("challenger", currentUser.getUsername());
	        	        	data.put("opponent", challengedUser);
	        	        	data.put("gameId", gameId);	

	        			    } catch (JSONException e1) {e1.printStackTrace();}
	        	                
	        	            ParsePush androidPush = new ParsePush();
	        	            androidPush.setMessage(cat + " Trivia Challenge from " + currentUser.getUsername());
	        	            androidPush.setData(data);
	        	            androidPush.setChannel(challengedUser);
	        	            androidPush.sendInBackground();                               	                
	                
	        	            
	    					Intent intent = new Intent(ScreenSlideActivity.this, MainActivity.class);
	    					intent.putExtra("gameId", gameId);
	    					startActivity(intent);
	    					finish();
	                }
	            }
	        });	 

    	}
    	else{
    		
    		
    		
    		
        	  ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");	
        	  ParseQuery<ParseObject> queryChalllangerRecord = ParseQuery.getQuery("Record");	
        	  ParseQuery<ParseObject> queryOpponentRecord = ParseQuery.getQuery("Record");
    		  try {
    			ParseObject gameObj = query.get(gameId);
//    			int opponentTime = (int)(Math.random() * 100);
    			int challengerTime = gameObj.getNumber("challengerTime").intValue();
    			int challengerCorrect = gameObj.getNumber("challengerCorrect").intValue();    			
    			
    			gameObj.put("opponentTime", time/1000);
    			gameObj.put("opponentCorrect", numCorrect);
    			
    			queryChalllangerRecord.whereEqualTo("user", gameObj.getString("challenger"));
    			ParseObject challangerRecord = queryChalllangerRecord.getFirst();
    			
    			queryOpponentRecord.whereEqualTo("user", gameObj.getString("opponent"));
    			ParseObject opponentRecord = queryOpponentRecord.getFirst();
    			

    			
    			String res = "You won!";
    			
    			//1 = challenger 2 = opponennt 0 = in progress
    			if(numCorrect > challengerCorrect){
    				//opponent wins
    				gameObj.put("winner", 2);
    				int oWins = opponentRecord.getInt("wins");
    				opponentRecord.put("wins", (oWins+1));
    				
    				int cLoses = challangerRecord.getInt("loses");
    				opponentRecord.put("wins", (cLoses+1));
    				
    			}
    			else if(numCorrect < challengerCorrect){
    				//challenger wins
    				gameObj.put("winner", 1);
    				res = "You Lost!";
    				int oLoses = opponentRecord.getInt("loses");
    				opponentRecord.put("loses", (oLoses+1));
    				
    				int cWins = challangerRecord.getInt("wins");
    				challangerRecord.put("wins", (cWins+1));
    			}
    			else
    			{
    				if(time < challengerTime){
    					//opponent wins
        				gameObj.put("winner", 2);
        				int oWins = opponentRecord.getInt("wins");
        				opponentRecord.put("wins", (oWins+1));
        				
        				int cLoses = challangerRecord.getInt("loses");
        				challangerRecord.put("loses", (cLoses+1));
    				}
    				else{
    					//challenger wins
        				gameObj.put("winner", 1);
        				res = "You Lost!";
        				int oLoses = opponentRecord.getInt("loses");
        				opponentRecord.put("loses", (oLoses+1));
        				
        				int cWins = challangerRecord.getInt("wins");
        				challangerRecord.put("wins", (cWins+1));
    				}
    			}

    			JSONArray cArray = new JSONArray();
    	        for (int a : correctArray)
    	        {
    	        	cArray.put(a);
    	        }
            	gameObj.put("oppCorrectArray", cArray);
    			
    			
				Intent intent = new Intent(ScreenSlideActivity.this, MainActivity.class);
				intent.putExtra("gameId",gameId);
				startActivity(intent);
				
				Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    			gameObj.saveInBackground();
    			opponentRecord.saveInBackground();
    			challangerRecord.saveInBackground();
    			finish();
    		} catch (ParseException e1) {e1.printStackTrace();}

    	}
    }
}
