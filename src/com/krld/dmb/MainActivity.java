package com.krld.dmb;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.view.View.*;

public class MainActivity extends Activity
{

	private DaysView daysView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
	    daysView = new DaysView(this);
		final LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
		layout.addView(daysView);
	}

    @Override
    protected void onPause() {
        super.onPause();
        daysView.stopThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        daysView.startThread();
    }
}
