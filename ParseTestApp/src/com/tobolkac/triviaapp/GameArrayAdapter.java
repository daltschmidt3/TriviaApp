package com.tobolkac.triviaapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class GameArrayAdapter extends ArrayAdapter<ParseObject> {
  private final Context context;
  

  public GameArrayAdapter(Context context) {
    super(context, R.layout.game_list_item);
    this.context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.game_list_item, parent, false);
    ParseObject game = getItem(position);
    TextView gameNum = (TextView) rowView.findViewById(R.id.gameNum);
    TextView gameStatusText = (TextView) rowView.findViewById(R.id.gameStatus);
    TextView p1TimeText = (TextView) rowView.findViewById(R.id.yourTime);
    TextView p2TimeText = (TextView) rowView.findViewById(R.id.oppTime);
    TextView p1CorrectText = (TextView) rowView.findViewById(R.id.yourCorrect);
    TextView p2CorrectText = (TextView) rowView.findViewById(R.id.oppCorrect);
    TextView otherLabelText = (TextView) rowView.findViewById(R.id.oppScoreLabel);
    
    ParseUser currentUser = ParseUser.getCurrentUser();
    
    boolean isUserChallenger = false;
    if ((game.getString("challenger")).equals(currentUser.getUsername().toString()))
    {
    	isUserChallenger = true;
    }
    
    
    if(isUserChallenger)
    {
    	gameNum.setText(game.getString("category") + " vs. " + game.getString("opponent"));
    }
    else
    {
    	gameNum.setText(game.getString("category") + " vs. " + game.getString("challenger"));
    }
    
    if (isUserChallenger)
    {
    	//set p1 Time
    	int challengerTime = game.getNumber("challengerTime").intValue();
	    int challengerMin = challengerTime/60;
	    int challengerSec = challengerTime%60;
	    
	    if (challengerSec<10)
	    {
	    	p1TimeText.setText(challengerMin + ":0" + challengerSec);
	    }
	    else
	    {
	    	p1TimeText.setText(challengerMin + ":" + challengerSec);
	    }
	    
    	//set p2 Time
    	if (game.getNumber("opponentTime") == null)
    	{
    		p2TimeText.setText("");
    	}
    	else
    	{
    		int opponentTime = game.getNumber("opponentTime").intValue();
	    	int opponentMin = opponentTime/60;
	    	int opponentSec = opponentTime%60;
	    
		    if(opponentSec<10)
		    {
		    	p2TimeText.setText(opponentMin + ":0" + opponentSec);
		    }
		    else
		    {
		    	p2TimeText.setText(opponentMin + ":" + opponentSec);
		    }
    	}
    	//set p1 Correct
    	int correct = game.getNumber("challengerCorrect").intValue();
    	int total = game.getNumber("numberOfQuestions").intValue();
    	p1CorrectText.setText(correct+"/"+total);
    	
    	//set p2 Correct
    	if (game.getNumber("opponentCorrect") == null)
        {
        	p2CorrectText.setText("");
        }
        else
        {
        	correct = game.getNumber("opponentCorrect").intValue();
        	p2CorrectText.setText(correct+"/"+total);
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
        	gameStatusText.setTextColor(Color.GREEN);
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
    	
    	//set p2 Time
    	if (game.getNumber("opponentTime") == null)
    	{
    		p1TimeText.setText("");
    	}
    	else
    	{
		    int opponentTime = game.getNumber("opponentTime").intValue();
	    	int opponentMin = opponentTime/60;
	    	int opponentSec = opponentTime%60;
	    	if(opponentSec<10)
		    {
		    	p1TimeText.setText(opponentMin + ":0" + opponentSec);
		    }
		    else
		    {
		    	p1TimeText.setText(opponentMin + ":" + opponentSec);
		    }
    	}
	    
	    //set p1 Time
    	int challengerTime = game.getNumber("challengerTime").intValue();
	    int challengerMin = challengerTime/60;
	    int challengerSec = challengerTime%60;
    	
    	if(challengerSec<10)
	    {
	    	p2TimeText.setText(challengerMin + ":0" + challengerSec);
	    }
	    else
	    {
	    	p2TimeText.setText(challengerMin + ":" + challengerSec);
	    }
    	
    	//set p1 Correct
    	int total = game.getNumber("numberOfQuestions").intValue();
    	if (game.getNumber("opponentCorrect") == null)
        {
        	p1CorrectText.setText("");
        }
        else
        {
        	int correct = game.getNumber("opponentCorrect").intValue();
        	p1CorrectText.setText(correct+"/"+total);
        }
    	
    	//set p2 Correct
    	int correct = game.getNumber("challengerCorrect").intValue();
    	p2CorrectText.setText(correct+"/"+total);
    	
    	//set other name
    	otherLabelText.setText(game.getString("challenger")+":");

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
        	gameStatusText.setTextColor(Color.GREEN);
        	gameStatusText.setTextSize(30);
        }
    }
    
    return rowView;
  }
}