//MainActivity Campus Direction

package com.campusdirection;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity implements SearchFragment.SearchFragmentListener{

	SearchFragment searchFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(savedInstanceState != null)
			return;

		if(findViewById(R.id.fragmentContainer) != null)
		{
			searchFragment = new SearchFragment();
	        FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        transaction.add(R.id.fragmentContainer, searchFragment);
	        transaction.commit(); // causes CollectionListFragment to display		
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
//		System.out.println("the code is catch");

		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		// handle scan result
		if (scanResult != null) {
			FragmentManager fm = getFragmentManager();

//			Fragment newFrame = MainFragment.newInstance(scanResult.toString());
			String myResult = intent.getStringExtra("SCAN_RESULT");
			InstructionsFragment newFrame = InstructionsFragment.newInstance(myResult, "");
			//send result to new fragment.
			fm.beginTransaction().replace(R.id.fragmentContainer, newFrame).commit();
		}
	}
	
}
