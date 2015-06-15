//MainActivity Campus Direction

package edu.cascadia.campusdirections;

import java.util.Arrays;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements SearchFragment.SearchFragmentListener{

	SearchFragment searchFragment;
	public static String lookFor = "Kodiac Corner";
	public static String direction = "", specialDirection = "", test="";
	// Determine QR Code string
	public static String scanBuild, scanRoom, scanName, scanExit, inputBuild, inputRoom;
	public static int scanFloor, scanSide, scanIndex, inputFloor, inputLocation, scanLocation;
	public static boolean searchClick = false;
	private String LEFT, RIGHT, UP, DOWN, WEST, EAST, SOUTH, NORTH;
	private static String NORTHEND;
	private static String SOUTHEND;
	private static String CENTER;
	private static String UNKNOWNLOC;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LEFT = getResources().getString(R.string.left);
		RIGHT = getResources().getString(R.string.right);
		UP = getResources().getString(R.string.up);
		DOWN = getResources().getString(R.string.down);
		WEST = getResources().getString(R.string.west);
		NORTH = getResources().getString(R.string.north);
		SOUTH = getResources().getString(R.string.south);
		EAST = getResources().getString(R.string.east);
		NORTHEND = getResources().getString(R.string.northEnd);
		SOUTHEND =getResources().getString(R.string.southEnd);
		CENTER = getResources().getString(R.string.center);
		UNKNOWNLOC = getResources().getString(R.string.unknownLoc);
		
		
		
		if(savedInstanceState != null)
			return;
		
		resetResult();
		
		if(findViewById(R.id.fragmentContainer) != null)
		{
			searchFragment = new SearchFragment();
	        FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        transaction.add(R.id.fragmentContainer, searchFragment);
	        transaction.commit(); // causes CollectionListFragment to display		
		}
	}

	@Override		
	
	// This is for scanning the QR code and creating the directions
	// If splitScanResult returns true (that it has a correctly formatted QR code string)
	// then it will compileDirection which puts together the individual strings that build up to the complete directions
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		// handle scan result
		if (scanResult != null) {
			//result string return from scanner
			String result = intent.getStringExtra("SCAN_RESULT"); // result = the string that was scanned from the QR code
			
			if(splitScanResult(result)) //split string value from qr code scan result				
				compileDirection(); //begin compile direction from result
			else
				direction =getResources().getString(R.string.errorHeader)+ "\n" + getResources().getString(R.string.invalidQRCode, result);//"---> Error <----"

			//send result to new fragment.
			FragmentManager fm = getFragmentManager();
			InstructionsFragment newFrame = InstructionsFragment.newInstance();
			fm.beginTransaction().replace(R.id.fragmentContainer, newFrame).commit();
		}
	}
	
	// This method puts together the strings from individual methods to output
	// a complete set of directions to the user
	public void compileDirection()
	{
		direction = getResources().getString(R.string.directionHeader)+"\n";
		// if building input and building scanned are the same
		if(scanBuild.equals(inputBuild)){
			//only if user inside LBA building and look for room in there
			if(inputBuild.equals("LBA") && scanBuild.equals("LBA")){
				direction += getResources().getString(R.string.roomLBA);
				//also determine if they are looking for a room located in a special location
				if(isSpecialRoom()) compileSpecialDir();
				return;
			}//end brief room direction inside LBA building				
				
			// is the input/scan floor the same
			if(inputFloor == scanFloor){
				//determine room direction
				RoomDir();
			}else{
				//determine Floor to Floor direction
				floorDirection();
				//also brief determine room direction as well
				basicRoomDirection();
			}			
			//also determines if the user is looking for a room in a special location
			if(isSpecialRoom()) compileSpecialDir();
		}else{
			//determine Building to Building direction
			if((scanBuild.equals("CC1") && inputBuild.equals("CC2")) || (scanBuild.equals("CC2") && inputBuild.equals("CC1")))
			{
				//determine Floor to Floor direction ("Go DOWN to the 1st floor.")
				floorDirection();
				
				//user is inside building CC1 and looking for a room in building CC2 and vice versa...
				
				switch(scanBuild){
					case "CC1":	// user location in CC1 building and wants to go to CC2 building
						if(scanSide == 1)                                                          //++++++++++++++++++++++++++++++++++++++++++++++++=
							direction += getResources().getString(R.string.roomDirBuilding, RIGHT, inputBuild, lookFor, myRoomLocation(RIGHT));
						else if(scanSide == 0)
							if (inputFloor != scanFloor) //if the user is going from CC1 to CC2 and has to move up or down a floor, 
														// then the directions need to say "take a Right" from the center stairs.
								direction += getResources().getString(R.string.roomDirBuilding, RIGHT, inputBuild, lookFor, myRoomLocation(RIGHT));
							else
								direction += getResources().getString(R.string.roomDirBuilding, LEFT, inputBuild, lookFor, myRoomLocation(RIGHT));
						break;
					case "CC2": // user location in CC2 building and wants to go to CC1 building
						if(scanSide == 1)
							if (inputFloor != scanFloor) //if the user is going from CC2 to CC1 and has to move up or down a floor, 
														// then the directions need to say "take a Right" from the northern stairs.
								direction += getResources().getString(R.string.roomDirBuilding, RIGHT, inputBuild, lookFor, myRoomLocation(LEFT));
							else
								direction += getResources().getString(R.string.roomDirBuilding, LEFT, inputBuild, lookFor, myRoomLocation(LEFT));
						else if(scanSide == 0)
							direction += getResources().getString(R.string.roomDirBuilding, RIGHT, inputBuild, lookFor, myRoomLocation(LEFT));
						break;
					default:
						direction += getResources().getString(R.string.unknownBuild);
						break;
				}	
				//also determines if the user is looking for a room in a special location
				if(isSpecialRoom()) compileSpecialDir();
				
				//BUILDING to BUILDING directions
			}else{
				if(inputBuild.equals("CC3")){ //user wants to go to building CC3
					
					if(scanBuild.equals("CC1") || scanBuild.equals("CC2")){ //start from CC1 or CC2 building
						//user from CC1 or CC2 and wants to go to CC3
						direction += getResources().getString(R.string.exitCC1, inputBuild, RIGHT);
					}else if(scanBuild.equals("LBA")){ //start from LBA share library
						//user from LBA and want to go to CC3 building
						direction += getResources().getString(R.string.exitLBA, inputBuild, LEFT) + getResources().getString(R.string.downSidewalk, NORTH);
					}else if(scanBuild.equals("LIB")){ //start from LIB share library
						//user from LIB and want to go to CC3 building
						direction += getResources().getString(R.string.exitLIB, inputBuild, LEFT) + getResources().getString(R.string.downSidewalk, NORTH);
					}else if(scanBuild.equals("BS")){ //start from Bookstore building
						//user from Bookstore and want to go to CC3 building
						direction += getResources().getString(R.string.exitTheXand, R.string.bookstore, WEST, RIGHT, inputBuild, LEFT);
					}
					
				}else if(inputBuild.equals("CC1") || inputBuild.equals("CC2")){ //user want to go to building CC1 or CC2
					
					if(scanBuild.equals("CC3")){ //start from CC3 building
						//user from building CC3 want to go to CC1 or CC2 building
						direction += getResources().getString(R.string.exitCC3, inputBuild, LEFT);
					}else if(scanBuild.equals("LBA")){ //start from LBA share library building
						//user from LBA and want to go to CC1 or CC2 building
						direction += getResources().getString(R.string.exitLBA, inputBuild, RIGHT) + getResources().getString(R.string.downSidewalk, NORTH);
					}else if(scanBuild.equals("LIB")){ //start from LIB share library building
						//user from LIB and want to go to CC1 or CC2 building
						direction += getResources().getString(R.string.exitLIB, inputBuild, RIGHT) + getResources().getString(R.string.downSidewalk, NORTH);
					}else if(scanBuild.equals("BS")){ //start from Bookstore building
						//user from Bookstore and want to go to CC1 or CC2 building
						direction += getResources().getString(R.string.exitTheXand, R.string.bookstore, WEST, RIGHT, inputBuild, RIGHT) + getResources().getString(R.string.downSidewalk, NORTH);
					}
				}else if(inputBuild.equals("LBA")){ //user want to go to LBA share library building
					if(scanBuild.equals("CC3")){ //start from CC3 building
						//user from building CC3 want to go to LBA building
						direction += getResources().getString(R.string.exitCC3, inputBuild, LEFT);
					}else if(scanBuild.equals("CC1") || scanBuild.equals("CC2")){ //start from CC1 or CC2 building
						//user from CC1 or CC2 building and want to go to LBA building
						direction += getResources().getString(R.string.exitCC1, inputBuild, LEFT) + getResources().getString(R.string.downSidewalk, SOUTH);
					}else if(scanBuild.equals("LIB")){ //start from LBA building
						//user from LBA building and wants to go to LIB library building
						direction += getResources().getString(R.string.exitAndStright);
					}else if(scanBuild.equals("BS")){ //start from Bookstore building
						//user from Bookstore building and want to go to LBA library building
						direction += getResources().getString(R.string.exitTheXand, R.string.bookstore, WEST, LEFT, inputBuild, LEFT)  + getResources().getString(R.string.downSidewalk, NORTH);
					}								
				}else if(inputBuild.equals("LIB")){ //user want to go to LIB share library building
					if(scanBuild.equals("CC3")){ //start from CC3 building
						//user from building CC3 want to go to LIB building
						direction += getResources().getString(R.string.exitCC3, lookFor, RIGHT);
					}else if(scanBuild.equals("CC1") || scanBuild.equals("CC2")){ //start from CC1 or CC2 building
						//user from CC1 or CC2 building and want to go to LIB building
						direction += getResources().getString(R.string.exitCC1, lookFor, RIGHT) + getResources().getString(R.string.downSidewalk, SOUTH);
					}else if(scanBuild.equals("LBA")){ //start from LBA building
						//user from LBA building and want to go to LIB library building
						direction += getResources().getString(R.string.exitAndStright);
					}else if(scanBuild.equals("BS")){ //start from Bookstore building
						//user from Bookstore building and want to go to LIB library building
						direction += getResources().getString(R.string.exitTheXand, R.string.bookstore, WEST, LEFT, inputBuild, RIGHT)  + getResources().getString(R.string.downSidewalk, NORTH);
					}								
					
				}else if(inputBuild.equals("BS")){ //user want to go to Bookstore building
					
					if(scanBuild.equals("CC3")){ //start from CC3 building
						//user is coming from building CC3 and wants to go to Bookstore building
						direction += getResources().getString(R.string.exitCC3, lookFor, LEFT);
					}else if(scanBuild.equals("CC1") || scanBuild.equals("CC2")){ //start from CC1 or CC2 building
						//user from CC1 or CC2 building and want to go to Bookstore building
						direction += getResources().getString(R.string.exitCC1, lookFor, LEFT) + getResources().getString(R.string.downSidewalk, NORTH);
					}else if(scanBuild.equals("LBA")){ //start from LBA building
						//user from LBA building and want to go to Bookstore building
						direction += getResources().getString(R.string.exitTheXand, R.string.libraryAnnex, WEST, RIGHT, inputBuild, RIGHT) + getResources().getString(R.string.throughFFT);
					}else if(scanBuild.equals("LIB")){ //start from Library building
						//user from LBA building and want to go to Bookstore building
						direction += getResources().getString(R.string.exitTheXand, R.string.libraryAnnex, WEST, LEFT, inputBuild, RIGHT) + getResources().getString(R.string.throughFFT);
					}
				}//adding another building requires additional if-else statements
			}
			
		}
	}
	
	//determine Floor to Floor directions
	// This method compares the destination with the scanned floor and provides the fist direction of (for example) "Go UP to the 3rd floor."
	
	public void floorDirection()
	{
			if(inputFloor > scanFloor)
				direction += getResources().getString(R.string.floorDir, UP, stringFloor(inputFloor))+"\n";
			else if(inputFloor < scanFloor)
				direction += getResources().getString(R.string.floorDir, DOWN, stringFloor(inputFloor))+"\n";
			if ((inputFloor != scanFloor) && scanBuild.equals("CC2") && inputBuild.equals("CC1")) 
				direction += getResources().getString(R.string.TakeCC2Stairs)+"\n";
			if ((inputFloor != scanFloor) && scanBuild.equals("CC1") && inputBuild.equals("CC2"))  
				direction += getResources().getString(R.string.TakeCC1Stairs)+"\n";
	}
	

	//provides basic room directions when the user is coming from a different floor
	//
	public void basicRoomDirection()
	{
		int destinationRm = Integer.parseInt(inputRoom.replaceAll("[\\D]", ""));
		if(inputBuild.equals("CC1") || inputBuild.equals("CC2"))
		{
			if(inputLocation <= 3 && inputLocation >= 0)
				direction += getResources().getString(R.string.cc1FloorDir, stringFloor(inputFloor), LEFT, lookFor, myRoomLocation(LEFT));
			else if(inputLocation >= 4 && inputLocation <= 8)
				direction += getResources().getString(R.string.cc1FloorDir, stringFloor(inputFloor), RIGHT, lookFor, myRoomLocation(RIGHT));
		}
		else if(inputBuild.equals("CC3"))
		{
			if(inputLocation <= 4 && inputLocation >= 2)
				direction += getResources().getString(R.string.cc3FloorDir, stringFloor(inputFloor), RIGHT, lookFor, myRoomLocation(LEFT));
			else if(inputLocation >= 0 && inputLocation <= 1)
				direction += getResources().getString(R.string.cc3FloorDir, stringFloor(inputFloor), LEFT, lookFor, myRoomLocation(RIGHT));			
			if (destinationRm > 299)
				direction += getResources().getString(R.string.inUHall); //" as you go through the U shaped hallway that splits off to the West.";
		}
	}
	
	// myRoomLocation means the location of the room relative to if they were moving toward the North (right) or South (left)   (if facing west)
	public String myRoomLocation(String str)
	{
		int tempRoom = Integer.parseInt((inputRoom.replaceAll("[\\D]", "")));
		if(str.equals(LEFT))
		{
			if(tempRoom % 2 == 0) return LEFT;
			else return RIGHT;		
		}else{
			if(tempRoom % 2 == 0) return RIGHT;
			else return LEFT;		
		}
	}
	
	// determine floor level and return text value
	public String stringFloor(int flr)
	{
		switch(flr)
		{
			case 0: return getResources().getString(R.string.zero);
			case 1: return getResources().getString(R.string.first);
			case 2: return getResources().getString(R.string.second);
			case 3: return getResources().getString(R.string.third);
			case 4: return getResources().getString(R.string.fourth);
			case 5: return getResources().getString(R.string.fifth);
			case 6: return getResources().getString(R.string.sixth);
			case 7: return getResources().getString(R.string.seventh);
			default: return "";
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
	   		case "LBA":
	   			tempBld = res.obtainTypedArray(R.array.LBA);
	   			break;
	   		case "LIB":
	   			tempBld = res.obtainTypedArray(R.array.LIB);
	   			break;
	   		default:
	   			return -1; // invalid build 
		}
		//return index value(integer) of room location in an array base
		//from user input building and room number
		return Arrays.asList(res.getStringArray(tempBld.getResourceId(inputFloor, 0))).indexOf(inputRoom);
	}

	// Adds a special direction depending on the situation (ie: CC3 top floor)
	public void compileSpecialDir()
	{
		int destinationRm = Integer.parseInt(inputRoom.replaceAll("[\\D]", ""));
		if (destinationRm > 299 && inputBuild.equals("CC3"))
			{
			specialDirection = getResources().getString(R.string.cc33rdfloor);
			}
		else
		specialDirection = getResources().getString(R.string.specialDirection);
	}
	
	// determine room direction on the same floor.
	public void RoomDir()
	{
		int currentRm = Integer.parseInt(scanRoom.replaceAll("[\\D]", ""));      //here we are changing our strings for rooms into integers
		int destinationRm = Integer.parseInt(inputRoom.replaceAll("[\\D]", ""));
		
		//quick check to see if user already at destination
		if(currentRm == destinationRm){
			direction += getResources().getString(R.string.roomFound, lookFor);
			return;
		}
		
		//if the destination is only +-1 from your location it is behind you.	
		else if (currentRm == destinationRm+1 || currentRm == destinationRm-1 ){             
			direction += getResources().getString(R.string.destination, lookFor);
		}
		
		// Since our arrays are arranged from South to North, we can compare locations in the arrays to determine if 
		// a room is north or south from the scanned room, and then look at the "scan Side" (west or east) to determine if the room is to the left or right
		else if (scanIndex < getRoomIndex()){
			if (scanSide == 1){	// user facing odd/West side room number
				//This should be made the only text that displays on the fragment
				direction += getResources().getString(R.string.roomDir, RIGHT, lookFor, myRoomLocation(RIGHT));
			}
			else if (scanSide == 0){ // user facing even/East side room number
				direction += getResources().getString(R.string.roomDir, LEFT, lookFor, myRoomLocation(RIGHT));
			}
			//if we are on CC3's third floor all directions need to include the understanding that they                             
			//are continuing around the U of the hallway and the directions are "left" or "right" as they continue through it.
			if ((currentRm > 299 || currentRm ==000) && scanBuild.equals("CC3") && destinationRm > 299)
			{
				direction += getResources().getString(R.string.inUHall); //" as you go through the U shaped hallway that splits off to the West.";
			}
		}else if (scanIndex > getRoomIndex()){
			if (scanSide == 1){ // user facing odd/West side room number
				direction += getResources().getString(R.string.roomDir, LEFT, lookFor, myRoomLocation(LEFT));
			}
			else if (scanSide == 0){ // user facing even/East side room number
				direction += getResources().getString(R.string.roomDir, RIGHT, lookFor, myRoomLocation(LEFT));
			}
			//again checking if this is the third floor of CC3 as explained above. (an additional note is needed)                   
			if ((currentRm > 299 || currentRm ==000) && scanBuild.equals("CC3") && destinationRm > 299)
			{
				direction += getResources().getString(R.string.inUHall); //" as you go through the U shaped hallway that splits off to the West.";
			}
		}else	//if wrong index return from array or QR code, display error message.
			direction += getResources().getString(R.string.errorQRCode);
	}
	
	// splits the scanned QR code string into scanBuild-scanFloor-scanSide-scanIndex-scanRoom     scanLocation is the second digit in the room number
	public boolean splitScanResult(String str)
	{
		String[] tempStr = str.split("-");

		//check to see if QR code is valid string format
		if(tempStr.length < 5 && tempStr.length > 6) return false;

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
		
		//determine scan location of room number by second digit.
		scanLocation = Integer.parseInt((scanRoom.replaceAll("[\\D]", "")).substring(1, 2));
		
		//if qr code string format is correct, check whether string contain right value or not.
		if(scanFloor < 0 || scanIndex < 0 || scanBuild.equals("") || scanRoom.equals(""))
			return false;

		return true;
	}
	
	// This method assigns the below variables according to the scanned QR string (which was broken down)
	public static void setSplitInput(String building, String room, int flr, int loc)
	{
		inputBuild = building;
		inputRoom = room;
		inputFloor = flr;
		inputLocation = loc;
	}
	
	// resets all static variables
	public static void resetResult()
	{
		scanBuild = null;
		scanFloor = -1;
		scanSide = -1;
		scanIndex = -1;
		scanRoom = null;
		scanName = null;
		scanExit = null;
		scanLocation = -1;
		searchClick = false;
	}
	
	// scanLocation is the second digit of the room number.  Based on the QR code scanned room/array number, 
	// this method determines where the user currently is (ie: north end of building)   loc= "Center of the building"
	public static  String scanLocation()
	{
		String loc = CENTER;
		int partScan = Integer.parseInt((scanRoom.replaceAll("[\\D]", "")).substring(0,3));
		if (partScan == 000)
		{
			loc=CENTER;
		}
		else
		{switch(scanLocation){
			case 0:
				if(scanBuild.equals("CC1") || scanBuild.equals("CC2"))
					loc = CENTER;
				else if(scanBuild.equals("CC3"))
					loc = NORTHEND;
				break;
			case 1:
				if(scanBuild.equals("CC1") || scanBuild.equals("CC2"))
					loc = CENTER;				
				else if(scanBuild.equals("CC3"))
					loc = NORTHEND;
				break;
			case 2:
				if(scanBuild.equals("CC1") || scanBuild.equals("CC2"))
					loc = SOUTHEND;				
				else if(scanBuild.equals("CC3"))
					loc = CENTER;				
				break;				
			case 3:
				loc = SOUTHEND;				
				break;
			case 4:
				if(scanBuild.equals("CC1") || scanBuild.equals("CC2"))
					loc = NORTHEND;
				else if(scanBuild.equals("CC3"))
					loc = SOUTHEND;
				break;
			case 5:
				loc = NORTHEND;								
				break;
			case 6:
				loc = SOUTHEND;				
				break;
			case 7:
				loc = CENTER;								
				break;
			case 8:
				loc = NORTHEND;								
				break;
			default:
				loc = UNKNOWNLOC;
				break;
		}}
		return loc;
	}
	
	//checks to see if the room the user entered is in that building/floor's Special array
	public boolean isSpecialRoom()
	{
		Resources res = getResources();
		TypedArray tempBld;
		switch(inputBuild){
		  	case "CC1":
		  		if(inputFloor >= 0 && inputFloor < 4){
		  			tempBld = res.obtainTypedArray(R.array.CC1_Special);
		   			return verifySpecRoom(res.getStringArray(tempBld.getResourceId(inputFloor, 0)));
		   		}else return false;
		   	case "CC2":
		   		if(inputFloor >= 0 && inputFloor < 4){
		   			tempBld = res.obtainTypedArray(R.array.CC2_Special);
		   			return verifySpecRoom(res.getStringArray(tempBld.getResourceId(inputFloor, 0)));
		   		}else return false;
		   	case "CC3":
		   		if(inputFloor > 0 && inputFloor < 4){//changed the second number from 3 to 4  
		   			tempBld = res.obtainTypedArray(R.array.CC3_Special);
		   			return verifySpecRoom(res.getStringArray(tempBld.getResourceId(inputFloor, 0)));
		   		}else return false;
		   	case "LBA": //Share building: Library Annex
		   		if(inputFloor == 1){
		   			tempBld = res.obtainTypedArray(R.array.LBA_Special);
		   			return verifySpecRoom(res.getStringArray(tempBld.getResourceId(inputFloor, 0)));		   			
		   		}else return false;		   		
		   	default:
		   		return false; //building is invalid
		}
	}

	// looks to see if the room is listed in the special room array for the specified floor
	public boolean verifySpecRoom(String[] arr)
	{
		return Arrays.asList(arr).contains(inputRoom);
	}	
}
