package com.sktekki.agile;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.*;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		
		adView = (AdView)this.findViewById(R.id.adView);	
		AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
		
		
		
		Button startBtn = (Button) findViewById(R.id.Start_Btn);
		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MediaPlayer mp = MediaPlayer.create(Main.this, R.raw.buttonsound2);
				mp.start();
				if (isNetworkAvailable()) {
					Intent ToTestScreen = new Intent(Main.this,
							TestScreen.class);
					startActivity(ToTestScreen);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							Main.this);
					builder.setMessage("Please Check your internet connectivity.");
					builder.setCancelable(false);
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									Main.this.finish();
								}
							});

					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			}
		});
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();		
		EasyTracker.getInstance(this).activityStop(this);  // Add this method
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }
	
	@Override
	protected void onPause() {
		adView.pause();
		super.onPause();
	}
	@Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }
	
	 @Override
	    protected void onDestroy() {
		 adView.destroy();
	        super.onDestroy();
	    }

}
