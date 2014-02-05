package com.tobolkac.triviaapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class NewGameActivity extends Activity {
	public ListView opponentList;
	public TextView opponentName;
	public Button startGameButton;
	public Button startGameNewButton;
	public Spinner categorySpinner;
	
	List<ParseObject> questions;
	
	public EditText searchText;
	
	public int numQuestions;
	
	ArrayAdapter<String> adapter;
	
	int[] checkedItem;
	
	ArrayList<Integer> questionNums;

	String opponent;
	
	Vibrator vibe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		// Show the Up button in the action bar.
		setupActionBar();
		Parse.initialize(this, "paHnFob0MGoBuy16Pzg5YPCH6TMOZfgZPXEOY1em", "1WOoBPDmOAu9CbHfvKIGmNIt2mY32mEvBYoLcPLV");
		ParseAnalytics.trackAppOpened(getIntent());
		final ParseUser currentUser = ParseUser.getCurrentUser();
		
		opponentList = (ListView) findViewById(R.id.opponentList);
		
		//set up vibrator
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		ParseQuery<ParseObject> queryOpponents = ParseQuery.getQuery("Record");
		
		try {
			List<ParseObject> records = queryOpponents.find();
			Log.d("score", "Retrieved " + records.size() + " records");
			String[] names = new String[records.size()-1];
            int count = 0;
			for (ParseObject p : records)
			{
				if(!(p.getString("user").equals(currentUser.getUsername().toString())))
				{
					names[count] = (p.getString("user"));
					count++;
				}
			}
			
			checkedItem = new int[count+1];
			
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, names);
			
			opponentList.setAdapter(adapter);
			
			searchText = (EditText) findViewById(R.id.searchText);
			
			searchText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					NewGameActivity.this.adapter.getFilter().filter(arg0); 
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		opponentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				for (int i = 0; i<parent.getChildCount(); i++)
				{
					((CheckedTextView) parent.getChildAt(i)).setChecked(false);
					
				}
				CheckedTextView item = (CheckedTextView) view;
				item.setChecked(true);
				opponent = ((TextView) view).getText().toString();
				
				
			}
		});
		
		categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		
		startGameButton = (Button) findViewById(R.id.startGameButton);
		
		
		
		
		
		startGameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//vibrate on button click
				vibe.vibrate(40);
				//get totals for the number of questions in each category
				//saves alot of time only having to retrieve one row verses grabbing every one
				ParseQuery<ParseObject> numberOfCategory = ParseQuery.getQuery("QuestionTotals");
				ParseObject totals = null;
				try {
					totals = numberOfCategory.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				questionNums = new ArrayList<Integer>();
				int len = totals.getInt(categorySpinner.getSelectedItem().toString());
				Log.d("number of questions", categorySpinner.getSelectedItem().toString() +": " + len);
				for (int i = 1; i<=10; i++)
				{
					int num = (int)((Math.random()*len)+1);
					Log.d("questionNums before", questionNums.toString());
					while (questionNums.contains(num))
					{
						num = (int)((Math.random()*len)+1);
					}
					Log.d("questionNums after", ""+num);
					questionNums.add(num); 
				}
				JSONArray qNums = new JSONArray();
				for (Integer i : questionNums)
				{
					qNums.put(i);
				}
				Log.d("questionNums", ""+qNums);
				
				//Start question activity, pass random numbers to it
				Intent intent = new Intent(NewGameActivity.this, ScreenSlideActivity.class);
				intent.putExtra("opponentName", opponent);
				intent.putExtra("category", categorySpinner.getSelectedItem().toString());
				intent.putIntegerArrayListExtra("questionNumArray", questionNums);
		    	startActivity(intent);
				finish();
		    	
			}
		});
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("New Challenge");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_game, menu);
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
