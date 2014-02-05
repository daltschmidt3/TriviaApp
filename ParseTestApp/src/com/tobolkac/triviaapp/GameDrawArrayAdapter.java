package com.tobolkac.triviaapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class GameDrawArrayAdapter extends ArrayAdapter<ParseObject> {
  private final Context context;
  

  public GameDrawArrayAdapter(Context context) {
    super(context, R.layout.game_list_draw_item);
    this.context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.game_list_draw_item, parent, false);
    ParseObject game = getItem(position);
    TextView gameNum = (TextView) rowView.findViewById(R.id.gameNum);
    TextView gameStatusText = (TextView) rowView.findViewById(R.id.gameStatus);

    TextView otherLabelText = (TextView) rowView.findViewById(R.id.oppScoreLabel);
    
    ParseUser currentUser = ParseUser.getCurrentUser();
    
    boolean isUserChallenger = false;
    if ((game.getString("challenger")).equals(currentUser.getUsername().toString()))
    {
    	isUserChallenger = true;
    }
    
    
    if(isUserChallenger)
    {
    	gameNum.setText(game.getString("category") + " trivia vs. " + game.getString("opponent"));
    }
    else
    {
    	gameNum.setText(game.getString("category") + " trivia vs. " + game.getString("challenger"));
    }
    
    if (isUserChallenger)
    {
    	//set p1 Time
    	int challengerTime = game.getNumber("challengerTime").intValue();
	    int challengerMin = challengerTime/60;
	    int challengerSec = challengerTime%60;
	    
	    int challengerCorrect = game.getNumber("challengerCorrect").intValue();
	    int[] chalCorrectArray;
	    JSONArray chalCorrect = game.getJSONArray("chalCorrectArray");

	    ///Was a bug because there could be zero correct
	    if(chalCorrect == null){ //!!!!!!
	    	chalCorrectArray = new int[0];
	    }
	    else{
		  chalCorrectArray = new int[chalCorrect.length()];
	    
	    
		for (int s = 0; s<chalCorrect.length(); s++)
		{
			try {
				chalCorrectArray[s] = (Integer) chalCorrect.get(s);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    }
	    ((CorrectQuestionView) rowView.findViewById(R.id.challengerDrawCorrect)).setNumCorrect(10);
	    ((CorrectQuestionView) rowView.findViewById(R.id.challengerDrawCorrect)).setDisplayResults(true);
	    ((CorrectQuestionView) rowView.findViewById(R.id.challengerDrawCorrect)).setCorrectArray(chalCorrectArray);
	    
	    if (game.getNumber("opponentCorrect") != null)
        {
	    	 int opponentCorrect = game.getNumber("opponentCorrect").intValue();
	    	 JSONArray oppCorrect = game.getJSONArray("oppCorrectArray");
	 		int[] oppCorrectArray = new int[oppCorrect.length()];
	 		for (int s = 0; s<chalCorrect.length(); s++)
	 		{
	 			try {
	 				oppCorrectArray[s] = (Integer) oppCorrect.get(s);
	 			} catch (JSONException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 			}
	 		}
	 	    ((CorrectQuestionView) rowView.findViewById(R.id.opponentDrawCorrect)).setNumCorrect(10);
	 	   ((CorrectQuestionView) rowView.findViewById(R.id.opponentDrawCorrect)).setDisplayResults(true);
	 	    ((CorrectQuestionView) rowView.findViewById(R.id.opponentDrawCorrect)).setCorrectArray(oppCorrectArray);
        }
        
	    
	   
	    
    	//set other name
    	otherLabelText.setText(game.getString("opponent")+":");

    	//set game status
    	if (game.getNumber("winner").intValue() == 0)
        {
        	if (game.getNumber("opponentCorrect") == null)
            {
        		gameStatusText.setText(game.getString("opponent").toString() + "'s Turn");
        		//gameStatusText.setTextColor(Color.YELLOW);
        		gameStatusText.setTextSize(16);
            }
            else if (game.getNumber("challengerCorrect") == null )
            {
            	gameStatusText.setText("Your Turn");
            	gameStatusText.setTextColor(Color.BLUE);
            	gameStatusText.setTextSize(16);
            }
        }
        else if (game.getNumber("winner").intValue() == 1)
        {
        	gameStatusText.setText("W");
        	gameStatusText.setTextColor(Color.rgb(73, 173, 16));
        	gameStatusText.setTextSize(30);
        }
        else if (game.getNumber("winner").intValue() == 2)
        {
        	gameStatusText.setText("L");
        	gameStatusText.setTextColor(Color.RED);
        	gameStatusText.setTextSize(30);
        }
    	
    }
    else
    {
    	
    	
    	//set other name
    	otherLabelText.setText(game.getString("challenger")+":");

    	int challengerCorrect = game.getNumber("challengerCorrect").intValue();
	    
	    int[] chalCorrectArray= new int[0];
    	JSONArray chalCorrect = game.getJSONArray("chalCorrectArray");
    	///Bug fix for 0 correct
    if(chalCorrect != null){
		chalCorrectArray = new int[chalCorrect.length()];
		for (int s = 0; s<chalCorrect.length(); s++)
		{
			try {
				chalCorrectArray[s] = (Integer) chalCorrect.get(s);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	    ((CorrectQuestionView) rowView.findViewById(R.id.opponentDrawCorrect)).setNumCorrect(challengerCorrect);
	    ((CorrectQuestionView) rowView.findViewById(R.id.opponentDrawCorrect)).setDisplayResults(true);
	    ((CorrectQuestionView) rowView.findViewById(R.id.opponentDrawCorrect)).setCorrectArray(chalCorrectArray);
    	if (game.getNumber("opponentCorrect") != null)
        {
	    	 int opponentCorrect = game.getNumber("opponentCorrect").intValue();
	    	 JSONArray oppCorrect = game.getJSONArray("oppCorrectArray");
		 		int[] oppCorrectArray = new int[oppCorrect.length()];
		 		for (int s = 0; s<chalCorrect.length(); s++)
		 		{
		 			try {
		 				oppCorrectArray[s] = (Integer) oppCorrect.get(s);
		 			} catch (JSONException e) {
		 				// TODO Auto-generated catch block
		 				e.printStackTrace();
		 			}
		 		}
		 	    ((CorrectQuestionView) rowView.findViewById(R.id.challengerDrawCorrect)).setNumCorrect(opponentCorrect);

			    ((CorrectQuestionView) rowView.findViewById(R.id.challengerDrawCorrect)).setDisplayResults(true);
		 	    ((CorrectQuestionView) rowView.findViewById(R.id.challengerDrawCorrect)).setCorrectArray(oppCorrectArray);
        }
    	//set game status
    	if (game.getNumber("winner").intValue() == 0)
        {
        	if (game.getNumber("challengerCorrect") == null )
            {
        		gameStatusText.setText(game.getString("opponent").toString() + "'s Turn");
        		//gameStatusText.setTextColor(Color.YELLOW);
        		gameStatusText.setTextSize(16);
            }
            else if (game.getNumber("opponentCorrect") == null)
            {
            	gameStatusText.setText("Your Turn");
            	gameStatusText.setTextColor(Color.BLUE);
            	gameStatusText.setTextSize(16);
            }
        }
        else if (game.getNumber("winner").intValue() == 1)
        {
        	gameStatusText.setText("L");
        	gameStatusText.setTextColor(Color.RED);
        	gameStatusText.setTextSize(30);
        }
        else if (game.getNumber("winner").intValue() == 2)
        {
        	gameStatusText.setText("W");
        	gameStatusText.setTextColor(Color.rgb(73, 173, 16));
        	gameStatusText.setTextSize(30);
        }
    }
    
    return rowView;
  }
}