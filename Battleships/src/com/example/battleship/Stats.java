package com.example.battleship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Stats extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		
		int p1_numdestroyed=1;
		int p2_numdestroyed=1;
		
		int p1_accuracy=0;
		int p2_accuracy=0;
		
		int battle_time=2;
		int num_turns=10;
		
		p1_accuracy = (int) (((double)p1_numdestroyed) / ((double)num_turns) *100);
		p2_accuracy = (int) (((double)p2_numdestroyed) / ((double)num_turns) *100);
		
	}
	
	public void onNewGameButtonClick(View view)
	{
		Intent intent = new Intent(this, ConnectActivity.class);
		startActivity(intent);
	}
	
	public void onMainButtonClick(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
