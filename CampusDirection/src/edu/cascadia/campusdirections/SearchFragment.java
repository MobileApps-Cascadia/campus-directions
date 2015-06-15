package edu.cascadia.campusdirections;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class SearchFragment extends Fragment
{
   // callback method implemented by MainActivity  
   public interface SearchFragmentListener
   {
      // called after edit completed so movie can be redisplayed
//      public void onAddEditCompleted(long rowID);
   }
   
   private SearchFragmentListener listener; 
   private Button searchButton;
   InstructionsFragment instructionsFragment;
   private EditText textRoom;
   private Spinner arrayBuilding;
   private RadioGroup radioGroup;   ///for the radio buttons that are the exact location the user is looking for
  // private String Kodiac = "Kodiac Corner", Library = "Library", Bookstore = "Book Store", Mobiushall = "Mobius Hall", LibraryAnnex = "Library Annex";
   
   // set AddEditFragmentListener when Fragment is attached   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (SearchFragmentListener) activity; 
   }

   // remove AddEditFragmentListener when Fragment detached
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null; 
   }

      // called after View is created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
	   super.onCreateView(inflater, container, savedInstanceState);    
	   final View view = inflater.inflate(R.layout.activity_search_screen, container, false);
	   
	   //get user input for the building/room number
	   textRoom = (EditText) view.findViewById(R.id.textRoom);
//	   textRoom.setRawInputType(InputType.TYPE_CLASS_NUMBER);
	   arrayBuilding = (Spinner) view.findViewById(R.id.arrayBuilding);	      
	   searchButton = (Button) view.findViewById(R.id.searchButton);
	   
	   radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup); 
	   
	   
	   searchButton.setOnClickListener(new View.OnClickListener() {           ////////////////////////////////+++++
	
			@Override
			public void onClick(View v) {
				MainActivity.direction = "";	//reset direction string
				MainActivity.specialDirection = ""; //reset special room location
				MainActivity.searchClick = false;	//reset

				/* when the searchButton is clicked,
				 * do all the validation to make sure the room number entered is valid.
				 * 
				 * If everything is correct, then OPEN the "instruction_activity".
				 * 1. when the "instruction_activity" fragment is opened the scan will auto open
				 * 2. when scan is complete, return back to "instruction_activity".
				 * 3. User can rescan again at anytime.
				 * 4. user has option to Start Over, this will be available on the main menu selection.
				 *    this will return (pop) back to search_activity screen.
				 */
				
				int selected = radioGroup.getCheckedRadioButtonId();

				// launch Instruction/Result fragment after validate user input				
				if(validateRoom()){
					splitInput();	//determine floor number base on user enter room number
					if(isRoom())
						instructionFrag();	//launching instruction/result fragment
					else{
						//display dialog message to ask user re-enter room number.
						showDialog(R.string.roomTitle, R.string.invalidRoom, MainActivity.lookFor.toString());
					}						
				}else{
				
					if(selected > 0){	//is there any radio button selected
						//determine what radio button is selected
						onSelectRadioButton(selected);
						instructionFrag();	//launching instruction/result fragment				
					}else{
						//display dialog message to ask user enter room. Room number field can't be leave blank.
						showDialog(R.string.emptyInput, R.string.msgInput, "");
					}
				}
			}
		});		      
	   return view;
   }
   
   public void onSelectRadioButton(int id)
   {
	   String tempBd, tempRm;
	   int tempFlr, tempLoc;

	   switch (id){
	   		case R.id.kodiacCornerButton:
	   			tempBd = "CC1"; tempRm = "121"; tempFlr = 1; tempLoc = 2; MainActivity.lookFor = getResources().getString(R.string.kodiacCorner);
	   		break;
	   		case R.id.LibraryButton:
	   			tempBd = "LIB"; tempRm = "100"; tempFlr = 1; tempLoc = 0; MainActivity.lookFor = getResources().getString(R.string.library);
	   		break;
	   		case R.id.LibraryAnnexButton:
	   			tempBd = "LBA"; tempRm = "103"; tempFlr = 1; tempLoc = 0; MainActivity.lookFor = getResources().getString(R.string.libraryAnnex);
	   		break;
	   		case R.id.bookstoreButton:
	   			tempBd = "BS"; tempRm = "100"; tempFlr = 1; tempLoc = 0; MainActivity.lookFor = getResources().getString(R.string.bookstore);
	   		break;
	   		case R.id.mobiusHallButton:
	   			tempBd = "CC3"; tempRm = "101"; tempFlr = 1; tempLoc = 0; MainActivity.lookFor = getResources().getString(R.string.mobiusHall);
	   		break;
	   		default:
	   			tempBd = "Unk"; tempRm = "Unk"; tempFlr = -1; tempLoc = -1;
	   			break;
	   }
		
	   //set preset location
	   MainActivity.setSplitInput(tempBd, tempRm, tempFlr, tempLoc);
   }
  
   
   // check to see if the room user enter existed in that building/floor
   public boolean isRoom()
   {
	   Resources res = getResources();
	   TypedArray tempBld;
	   switch(MainActivity.inputBuild){
	   		case "CC1":
	   			if(MainActivity.inputFloor >= 0 && MainActivity.inputFloor < 4){
	   				tempBld = res.obtainTypedArray(R.array.CC1);
	   				return verifyRoom(res.getStringArray(tempBld.getResourceId(MainActivity.inputFloor, 0)));
	   			}else return false;
	   		case "CC2":
	   			if(MainActivity.inputFloor >= 0 && MainActivity.inputFloor < 4){
	   				tempBld = res.obtainTypedArray(R.array.CC2);
	   				return verifyRoom(res.getStringArray(tempBld.getResourceId(MainActivity.inputFloor, 0)));
	   			}else return false;
	   		case "CC3":
	   			if(MainActivity.inputFloor > 0 && MainActivity.inputFloor < 4){//changed final 3 to 4 and got cc3-300 floors to be recognized!
	   				tempBld = res.obtainTypedArray(R.array.CC3);
	   				return verifyRoom(res.getStringArray(tempBld.getResourceId(MainActivity.inputFloor, 0)));
	   			}else return false;
		   	case "LBA": //Share building: Library Annex
		   		if(MainActivity.inputFloor == 1){
		   			tempBld = res.obtainTypedArray(R.array.LBA);
		   			return verifyRoom(res.getStringArray(tempBld.getResourceId(MainActivity.inputFloor, 0)));		   			
		   		}else return false;		   		
		   	case "LIB": //Share building: Library Annex
		   		if(MainActivity.inputFloor == 1){
		   			tempBld = res.obtainTypedArray(R.array.LIB);
		   			return verifyRoom(res.getStringArray(tempBld.getResourceId(MainActivity.inputFloor, 0)));		   			
		   		}else return false;		   		
	   		default:
	   			return false; //building is invalid
	   }
   }
   
   // check existing room per floor plan
   public boolean verifyRoom(String[] arr)
   {
//	   Toast.makeText(getActivity(), String.valueOf(Arrays.asList(arr).contains(MainActivity.inputRoom)), Toast.LENGTH_SHORT).show();
		return Arrays.asList(arr).contains(MainActivity.inputRoom);
   }
   
   // check to see if user enter room number
   public boolean validateRoom()
   {
	   String tempRm = textRoom.getText().toString().trim();
	   String tempRmRep = tempRm.replaceAll("[\\D]", "");
	   
	   if(tempRm.equals(""))
		   return false;
	   else{
		   if(tempRmRep == "")
			   return false;
//			   Toast.makeText(getActivity(), String.valueOf(tempRmRep), Toast.LENGTH_SHORT).show();
		   else{   
			   MainActivity.lookFor = String.valueOf(arrayBuilding.getSelectedItem())+"-"+textRoom.getText().toString().trim();
			   return true;
		   }
	   }
   }
   
   // split user input into piece (building, room, floor)
   public void splitInput()
   {
   
	   String tempBd = String.valueOf(arrayBuilding.getSelectedItem());
	   String tempRm = textRoom.getText().toString();
	   int tempFlr = Integer.parseInt((tempRm.replaceAll("[\\D]", "")).substring(0, 1));
	   int tempLoc = Integer.parseInt((tempRm.replaceAll("[\\D]", "")).substring(1, 2));
	   
	   //verify floor level base on user input room number
	   int tempNum = Integer.parseInt(tempRm.replaceAll("[\\D]", ""));
	   if(tempNum < 100) tempFlr = 0;
	   MainActivity.setSplitInput(tempBd, tempRm, tempFlr, tempLoc);
   }

   // launch Result/Instruction fragment for direction
   public void instructionFrag()
   {
	   	instructionsFragment = new InstructionsFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragmentContainer, instructionsFragment);
		ft.addToBackStack(null);
		ft.commit(); // causes CollectionListFragment to display		      
   }
   
   // display this fragment's menu items
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.main, menu);
   }

   // handle choice from options menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId())
      {
//         case R.id.action_clear:
//            return true;
      }
      
      return super.onOptionsItemSelected(item); // call super's method
   }
   
   // display an AlertDialog when invalid room detect
   public void showDialog(final int msgTitle, final int msgFormat, final String str)
   {
	    AlertDialog.Builder displayMsg = new AlertDialog.Builder(getActivity());
	    displayMsg.setTitle(msgTitle);
	    displayMsg.setMessage(getResources().getString(msgFormat, str));
 
	    displayMsg.setPositiveButton(getResources().getText(R.string.okBut),
	    new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int arg1) {
	    		//do something when OK button click
        		//leave blank if just close window only.
        	}
        });
	    
	    displayMsg.show();
    }   
}


