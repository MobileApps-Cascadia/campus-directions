campus-directions
=================

Directions app for the Cascadia campus
  Description of our app:
    For our app, when someone wants to find directions to a specific room at Cascadia, they enter in the destination room and then scan a nearby QR code which notifies the app of the users current location.  Given these two locations the app outputs directions for the user to take to get closer to their destination.  The app will have a drop down menu for selecting building options, the user can then enter in a specific room number and click a button to choose to scan their current location QR code.  Additional options on the app are to select a destination from radio button options and then scan the current location to receive directions.
  
  Audience Statement:
    New students:  Any student who is new to Cascadia Community College. 
    Visitors: Any person that has come to Cascadia Community college to visit a student, teacher, advisor, attend a     meeting, graduation, etc.
    Guest speakers: Any person that has come to Cascadia Community college to be a guest speaker.
    Lost students: Any current or future student who is lost and needs directions.
    Teacher: Any teacher who is giving class at Cascadia Community College and needs directions. 


First Use Case:
Precondition: Database contains destination, location paired information.
Description: A splash screen shows for a moment, then the destination screen. A building is chosen from the drop down menu, then the text entry line is touched which brings up a soft keyboard. A room number is entered and the search button is pressed, hiding the keyboard. The QR scanning screen pops up.
A QR code is read through the camera which pulls location information into the app.  A screen pops up with a simple direction pulled from the apps database (resulting from the specific location and destination combination). App is terminated.
Exceptions: QR code could not work (user may have to find another QR code that responds when scanned).  A faulty room number could be entered (resulting in an alert that an accurate number is needed).
Postcondition: none


Second Use Case:
Precondition: Database contains destination, location paired information.
Description: A splash screen shows momentarily then the destination page opens.
The radio button for a specific location is touched from the selections listed. The QR scan screen opens. A QR code is read through the camera. The direction screen pops up giving a direction.  Under this instruction is a button saying “Scan another QR code” which is pressed to bring up the QR scan screen again. Another code is read and these steps are repeated until the app is terminated.
Exceptions: QR code could not work as in the previous case.
Postcondition: none.
