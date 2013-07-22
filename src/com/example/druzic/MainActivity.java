package com.example.druzic;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	
	private SignatureView canvasView;
	private TestView testView;
    private TestView2 testView2;
    private MelodyView melodyView;
    private BackgroundTestView bgTestView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*canvasView = new SignatureView(this);
		setContentView(canvasView); // final code */
		
		testView = new TestView(this);
        melodyView = new MelodyView(this);
        testView2 = new TestView2(this);
        bgTestView = new BackgroundTestView(this);

		setContentView(bgTestView); // Test code
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
