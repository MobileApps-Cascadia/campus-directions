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
	String lookFor = "Kodiac Corner";
	String direction = "";
	// Determine QR Code string
	String scanBuild, inputBuild, inputRoom;
	int scanFloor, scanSide, scanIndex, inputFloor;

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

		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		// handle scan result
		if (scanResult != null) {

//			Fragment newFrame = MainFragment.newInstance(scanResult.toString());
			String result = intent.getStringExtra("SCAN_RESULT");
			
			splitScanResult(result); //split scan result
			compileDirection();	//compile direction
			
			direction = "Here is your scan result ["+result+"]";
			//send result to new fragment.
			FragmentManager fm = getFragmentManager();
			InstructionsFragment newFrame = InstructionsFragment.newInstance();
			fm.beginTransaction().replace(R.id.fragmentContainer, newFrame).commit();
		}
	}
	
	public void compileDirection()
	{
		
	}
	
	// determine split the scan result content
	public void splitScanResult(String str)
	{
		String[] tempStr = str.split("-");
		scanBuild = tempStr[0];
		scanFloor = Integer.parseInt(tempStr[1]);
		scanSide = Integer.parseInt(tempStr[2]);
		scanIndex = Integer.parseInt(tempStr[3]);
	}
}
