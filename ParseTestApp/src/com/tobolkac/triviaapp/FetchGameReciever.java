package com.tobolkac.triviaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FetchGameReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		Intent intentGame = new Intent(context, GameActivity.class);
    	context.startActivity(intentGame);
	}
}
