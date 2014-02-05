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


import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;




//import com.f2prateek.progressbutton.ProgressButton;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * ScreenSlideActivity} samples.</p>
 */
public class ScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    
    public static int numQuestions = 3;
    
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    
    private String questionString;
    
    private String[] answersArray;
    
    private int answerCorrect;
    
    private static int numCorrect = 0;
    
    TextView numberCorrectText;
    
    public static ScreenSlideActivity ac;
    
    public ParseObject q;
    
    public static int[] questionsNumArray;
    public static String cat;

    ParseObject gameScore;
    ParseUser currentUser;
    
    static List<ParseObject> questions;
    

    int seconds;
    private long startTime;

	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	
	
	TextView questionTimerTextView;// = ac.getQuestionTextView();
	


    
    
    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber, String cat, ParseObject q) {
    	
    	
    	//query to get question for the page
    	//ParseQuery<ParseObject> queryQuestion = ParseQuery.getQuery("Questions");
    	//Log.d("page number query", "question num: " +cat );
		//queryQuestion.whereEqualTo("category", cat);
		//queryQuestion.whereEqualTo("categoryIndex", pageNumber+1);//questionsNumArray[pageNumber]);
		
    	Log.d("question in Fragment", "number: " + q.getInt("categoryIndex"));
		
		ParseObject question = q; //ac.getQuestion(pageNumber);
		/*try {
			question = queryQuestion.getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
		String[] answers = new String[4];
		for (int i = 0; i<4; i++)
		{
			answers[i] = question.getString("answer"+(i+1));
		}
		
		if (pageNumber ==0)
		{
			numCorrect = 0;
		}
		
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString("question", question.getString("question").toString());
        args.putStringArray("answers", answers);
        args.putInt("answerCorrect", question.getNumber("answerCorrect").intValue());
        
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Parse.initialize(getActivity(), "paHnFob0MGoBuy16Pzg5YPCH6TMOZfgZPXEOY1em", "1WOoBPDmOAu9CbHfvKIGmNIt2mY32mEvBYoLcPLV");
        ac = (ScreenSlideActivity) getActivity();
        Log.d("a", "!!!create!!!");
        
        
        Bundle b = getArguments();
        mPageNumber = b.getInt(ARG_PAGE);
        questionString = b.getString("question").toString();
        answersArray = b.getStringArray("answers");
        answerCorrect = b.getInt("answerCorrect");
        questions = ac.questions;
        
        
        cat = ac.cat;
        //questionsNumArray = b.getIntArray("questionsNumArray");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	numQuestions = ac.getNumPages();
        Log.d("a", "XXXcreateXXX");
//        resetTimer();
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                "Question " + (mPageNumber+1) + " of " + numQuestions);
        

        
        //ProgressButton b = (ProgressButton) getActivity().findViewById(R.id.progressButton1);
        //b.setProgress(50);

//        questionTimerTextView = (TextView) rootView.findViewById(R.id.questionTimer);
        
        TextView question = (TextView) rootView.findViewById(R.id.questionText);
        question.setText(questionString);
        
        Button answer1Button = (Button) rootView.findViewById(R.id.answer1Button);
        answer1Button.setText(answersArray[0]);

        

        
        Button answer2Button = (Button) rootView.findViewById(R.id.answer2Button);
        answer2Button.setText(answersArray[1]);
        
        Button answer3Button = (Button) rootView.findViewById(R.id.answer3Button);
        answer3Button.setText(answersArray[2]);
        
        Button answer4Button = (Button) rootView.findViewById(R.id.answer4Button);
        answer4Button.setText(answersArray[3]);
        
        numberCorrectText = (TextView) getActivity().findViewById(R.id.numberCorrect);
        numberCorrectText.setText("" + numCorrect);
        
        Log.d("oo",""+answer1Button.getTextSize());
        if(answersArray[0].length() > 22){
        	answer1Button.setTextSize(12);
        }	
        if(answersArray[1].length() > 22){
        	answer2Button.setTextSize(12);
        }
        if(answersArray[2].length() > 22){
        	answer3Button.setTextSize(12);
        }
        if(answersArray[3].length() > 22){
        	answer4Button.setTextSize(12);
        }
        
        
        
        answer1Button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ac.setLagMillis();
//				questionTimerHandler.removeCallbacks(questionTimerRunnable, null);
//				resetTimer();
				//vibrate on button click
				ac.vibe.vibrate(40);
				//check against correct and add to correct
				if (answerCorrect == 1)
				{
					numCorrect++;
					ac.numCorrect++;
					numberCorrectText.setText("" + numCorrect);
					ac.correctArray[mPageNumber] = 1;
				}
				if(getPageNumber()+1 == numQuestions)
				{
					
			    	Toast.makeText(getActivity(), "correct: "+numCorrect + " time: " + ac.getFormattedTime(), Toast.LENGTH_SHORT).show();
			    	ac.evalGame(numCorrect, ac.getTimeInMilliseconds());
//					Intent intent = new Intent(getActivity(), MainActivity.class);
//					startActivity(intent);
				}
				ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
				Log.d("page number", "page number: " +(getPageNumber()));
				pager.setCurrentItem(getPageNumber() + 1);
			}
		});
        answer2Button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ac.setLagMillis();
				//vibrate on button click
				ac.vibe.vibrate(40);
				//check against correct and add to correct
				if (answerCorrect == 2)
				{
					numCorrect++;	
					ac.numCorrect++;
					numberCorrectText.setText("" + numCorrect);	
					ac.correctArray[mPageNumber] = 1;
				}
				if(getPageNumber()+1 == numQuestions)
				{
			    	Toast.makeText(getActivity(), "correct: "+numCorrect + " time: " + ac.getFormattedTime(), Toast.LENGTH_SHORT).show();
			    	ac.evalGame(numCorrect, ac.getTimeInMilliseconds());
//					Intent intent = new Intent(getActivity(), MainActivity.class);
//					startActivity(intent);
				}
				ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
				Log.d("page number", "page number: " +(getPageNumber()));
				pager.setCurrentItem(getPageNumber() + 1);
			}
		});
		answer3Button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ac.setLagMillis();
				//vibrate on button click
				ac.vibe.vibrate(40);
				//check against correct and add to correct
				if (answerCorrect == 3)
				{
					numCorrect++;
					ac.numCorrect++;
					numberCorrectText.setText("" + numCorrect);
					ac.correctArray[mPageNumber] = 1;
				}
				if(getPageNumber()+1 == numQuestions)
				{
			    	Toast.makeText(getActivity(), "correct: "+numCorrect + " time: " + ac.getFormattedTime(), Toast.LENGTH_SHORT).show();
			    	ac.evalGame(numCorrect, ac.getTimeInMilliseconds());
//					Intent intent = new Intent(getActivity(), MainActivity.class);
//					startActivity(intent);
				}
				ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
				Log.d("page number", "page number: " +(getPageNumber()));
				pager.setCurrentItem(getPageNumber() + 1);
			}
		});
		answer4Button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ac.setLagMillis();
				//vibrate on button click
				ac.vibe.vibrate(40);
				//check against correct and add to correct
				if (answerCorrect == 4)
				{
					numCorrect++;
					ac.numCorrect++;
					numberCorrectText.setText("" + numCorrect);
					ac.correctArray[mPageNumber] = 1;
				}
				if(getPageNumber()+1 == numQuestions)
				{
			    	Toast.makeText(getActivity(), "correct: "+numCorrect + " time: " + ac.getFormattedTime(), Toast.LENGTH_SHORT).show();
			    	ac.evalGame(numCorrect, ac.getTimeInMilliseconds());
//					Intent intent = new Intent(getActivity(), MainActivity.class);
//					startActivity(intent);
				}
				ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
				Log.d("page number", "page number: " +(getPageNumber()));
				pager.setCurrentItem(getPageNumber() + 1);
			}
		});
		
        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    
}
