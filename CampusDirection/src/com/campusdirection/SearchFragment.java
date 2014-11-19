package com.campusdirection;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.Spinner;
import android.widget.Toast;

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
   
   // set AddEditFragmentListener when Fragment attached   
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
	   View view = inflater.inflate(R.layout.activity_search_screen, container, false);
	   
	   //get user input building/room number
	   textRoom = (EditText) view.findViewById(R.id.textRoom);
	   arrayBuilding = (Spinner) view.findViewById(R.id.arrayBuilding);	      
	   searchButton = (Button) view.findViewById(R.id.searchButton);
		
	   searchButton.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {

				/* when searchButton click,
				 * do all the validation to make sure room number user enter is valid.
				 * 
				 * If everything correct, then OPEN the "instruction_activity".
				 * 1. when "instruction_activity" fragment is open. the scan will automatic open
				 * 2. when scan complete, return back to "instruction_activity".
				 * 3. User make rescan again anytime.
				 * 4. user have option to Start Over, this will available on main menu selection.
				 *    this will return (pop) back to search_activity screen.
				 */

				// launch Instruction/Result fragment after validate user input				
				if(validateRoom()){
					splitInput();	//determine floor number base on user enter room number
					if(isRoom())
						instructionFrag();	//launching instruction/result fragment
					else{
						//display dialog message to ask user re-enter room number.
					}						
				}else{
				
					//display dialog message to ask user enter room. Room number field can't be leave blank.
				}
			}
		});		      
	   return view;
   }
   
   // check to see if the room user enter existed in that building/floor
   public boolean isRoom()
   {
	   Resources res = getResources();
	   TypedArray tempBld;
//	   String[] tempFlr;
	   switch(MainActivity.inputBuild){
	   		case "CC1":
	   			tempBld = res.obtainTypedArray(R.array.CC1);
	   			return verifyRoom(res.getStringArray(tempBld.getResourceId(MainActivity.inputFloor, 0)));
	   		case "CC2":
	   			break;
	   		case "CC3":
	   			break;
	   		default:
	   			return false;
	   }
	//   tempBld.recycle();
	   return true;
	   
	   /*
	   TypedArray ta = res.obtainTypedArray(R.array.array0);
	   int n = ta.length();
	   String[][] array = new String[n][];
	   for (int i = 0; i < n; ++i) {
	       int id = ta.getResourceId(i, 0);
	       if (id > 0) {
	           array[i] = res.getStringArray(id);
	       } else {
	           // something wrong with the XML
	       }
	   }
	   ta.recycle(); // Important!
	   */
   }
   
   // check existing room per floor plan
   public boolean verifyRoom(String[] arr)
   {
	   Toast.makeText(getActivity(), String.valueOf(arr.length), Toast.LENGTH_SHORT).show();
	   return true;
   }
   
   // check to see if user enter room number
   public boolean validateRoom()
   {
	   if(textRoom.getText().toString().trim().equals(""))
		   return false;
	   else{
		   MainActivity.lookFor = String.valueOf(arrayBuilding.getSelectedItem())+"-"+textRoom.getText().toString();
		   return true;
	   }
   }
   
   // split user input into piece (building, room, floor)
   public void splitInput()
   {
   
	   String tempBd = String.valueOf(arrayBuilding.getSelectedItem());
	   String tempRm = textRoom.getText().toString();
	   int tempFlr = Integer.parseInt((tempRm.replaceAll("[\\D]", "")).substring(0, 1));
	   
	   //verify floor level base on user input room number
	   int tempNum = Integer.parseInt(tempRm.replaceAll("[\\D]", ""));
	   if(tempNum < 100) tempFlr = 0;
	   MainActivity.setSplitInput(tempBd, tempRm, tempFlr);
   }

   // launch Result/Instruction fragment for direction
   public void instructionFrag()
   {
	   	instructionsFragment = new InstructionsFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragmentContainer, instructionsFragment);
		ft.addToBackStack(null);
		ft.commit(); // causes CollectionListFragment to displa		      
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
//         case R.id.action_add:
//            listener.onAddMovie();
//            return true;
      }
      
      return super.onOptionsItemSelected(item); // call super's method
   }
   
} // end class AddEditFragment


