//MainActivity Campus Direction

package com.campusdirection;

import java.util.Arrays;

import com.campusdirection.R.string;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity implements SearchFragment.SearchFragmentListener{

	SearchFragment searchFragment;
	public static String lookFor = "Kodiac Corner";
	public static String direction = "";
	// Determine QR Code string
	public static String scanBuild, scanRoom, scanName, inputBuild, inputRoom;
	public static int scanFloor, scanSide, scanIndex, inputFloor;

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
			
			direction = "Here is your scan result ["+result+"]";
			compileDirection();	//compile direction

			//send result to new fragment.
			FragmentManager fm = getFragmentManager();
			InstructionsFragment newFrame = InstructionsFragment.newInstance();
			fm.beginTransaction().replace(R.id.fragmentContainer, newFrame).commit();
		}
	}
	
	// return index value (integer) from an array where room is match.
	public int getRoomIndex()
	{
		Resources res = getResources();
		TypedArray tempBld = null;
		switch(inputBuild){
	   		case "CC1":
	   			tempBld = res.obtainTypedArray(R.array.CC1);
	   			break;
	   		case "CC2":
	   			tempBld = res.obtainTypedArray(R.array.CC2);
	   			break;
	   		case "CC3":
	   			tempBld = res.obtainTypedArray(R.array.CC3);
	   			break;
	   		default:
	   			return -1; // invalid build 
		}
		//return index value(integer) of room location in an array base
		//from user input building and room number
		return Arrays.asList(res.getStringArray(tempBld.getResourceId(inputFloor, 0))).indexOf(inputRoom);
	}


	public void compileDirection()
	{
		int current = Integer.parseInt(scanRoom.replaceAll("[\\D]", ""));      //here we are making our strings for rooms into integers
		int destination = Integer.parseInt(inputRoom.replaceAll("[\\D]", ""));
		if (current== destination+1 || current == destination-1 ){             //if the destination is only +-1 from your location it is behind you.
			direction +="Turn around to find your destination";
		}
		else if (current > destination){
			if (scanSide == 1){
				direction +="Take a right and go forward";   //This should be made the only text that displays on the fragment
			}
			else if (scanSide ==0){
				direction+="Take a left and go forward";
			}
		}
		else if (current<destination){
			if (scanSide == 1){
				direction +="Take a left and go forward";   
			}
			else if (scanSide ==0){
				direction+="Take a right and go forward";
			}
		}
		direction += "\n\n"+ getResources().getString(R.string.testDir, scanBuild, String.valueOf(scanFloor), scanRoom, String.valueOf(scanSide), String.valueOf(scanIndex), scanName); 
	}
	
	// determine split the scan result content
	public void splitScanResult(String str)
	{
		String[] tempStr = str.split("-");
		scanBuild = tempStr[0].trim();
		scanFloor = Integer.parseInt(tempStr[1]);
		scanSide = Integer.parseInt(tempStr[2]);
		scanIndex = Integer.parseInt(tempStr[3]);
		scanRoom = tempStr[4].trim();
		//check to see if location name giving from QR code
		if(tempStr.length == 6)
			scanName = tempStr[5].trim();
		else
			scanName = "";
	}
	
	// determine user input
	public static void setSplitInput(String building, String room, int flr)
	{
		inputBuild = building;
		inputRoom = room;
		inputFloor = flr;
	}
}
