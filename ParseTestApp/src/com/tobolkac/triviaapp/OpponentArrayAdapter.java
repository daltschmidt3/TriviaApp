package com.tobolkac.triviaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.ParseObject;

public class OpponentArrayAdapter extends ArrayAdapter<ParseObject>{
	
	private final Context context;

	public OpponentArrayAdapter(Context context) {
	    super(context, R.layout.game_list_item);
	    this.context = context;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.game_list_item, parent, false);
	    ParseObject game = getItem(position);
	    
	    
	    
	    return rowView;
	  }
}
