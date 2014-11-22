package screen.settings;


import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import coderunners.geolocationalchat.R;
import data.app.global.GlobalSettings;
import data.base.UserIdNamePair;

/**
 * The settings activity can be used by the user to update various information.
 * Currently, it only allows a user to change their name, but in the future it
 * may allow them to do other things such as adjust their notification settings
 * once notifications are implemented.
 */
public class SettingsActivity extends ActionBarActivity {

	//TODO: Limit name sizes?
	/**
	 * Minimum name length which may be entered by a user
	 */
	public static final int MIN_NAME_LENGTH = 1;

	/** 
	 * Maximum name length which may be entered by a user
	 */
	public static final int MAX_NAME_LENGTH = 30;

	public static final String SEND_NEW_USER_NAME_URI = "http://cmpt370duan.byethost10.com/updateuser.php";

	ArrayList<CheckBox> checkBoxTags = new ArrayList<CheckBox>();
	
	/**
	 * Sets up a settings screen to be displayed to the user
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		EditText editName = (EditText) findViewById(R.id.edit_name);
		editName.setHint(GlobalSettings.userIdAndName.userName);
		
		ArrayList<String> tags = GlobalSettings.allTags;
        
        LinearLayout tagsList = (LinearLayout) findViewById(R.id.filter_list);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        
        for(int i=0; i<tags.size(); i++)
        {
          CheckBox checkBox = new CheckBox(getApplicationContext());
          checkBox.setText(tags.get(i));
          checkBox.setTextColor(getResources().getColor(R.color.black));
          checkBox.setButtonDrawable(id);
          for(int j=0; j<GlobalSettings.tagsToFilterFor.size(); j++)
          {
            if(tags.get(i).equals(GlobalSettings.tagsToFilterFor.get(j)))
            {
              checkBox.setChecked(true); 
            }
          }
          checkBox.setEnabled(GlobalSettings.tagFilteringIsOn);
          tagsList.addView(checkBox);
          checkBoxTags.add(checkBox);
        }
        
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_filter);
        checkBox.setChecked(GlobalSettings.tagFilteringIsOn);
	}

	public void onFilterCheck(View v)
	{
	  
	  CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_filter);
	  
	  if(checkBox.isChecked())
	  {
	    for(int i=0; i<checkBoxTags.size(); i++)
	    {
	      checkBoxTags.get(i).setEnabled(true);
	    }
	  }
	  else
	  {
	    for(int i=0; i<checkBoxTags.size(); i++)
        {
          checkBoxTags.get(i).setEnabled(false);
          
        }
	  } 
	  
	  //TODO: Refresh view?
	  v.invalidate();
	}
	
	/**
	 * Returns to the previous screen without submitting any data entered by the
	 * user.
	 * @param v
	 */
	public void onCancel(View v)
	{
		finish();
	}

	/**
	 * Submits the user's settings, sending the user's new name to the database.
	 * If the name provided is shorter than {@link MIN_NAME_LENGTH} or longer
	 * than {@link MAX_NAME_LENGTH}, the user will be notified via a 
	 * {@link Toast}.
	 * @param v
	 */
	public void onSubmit(View v)
	{
		EditText editName = (EditText) findViewById(R.id.edit_name);
		String name = editName.getText().toString().trim();

		if(name.length() > MAX_NAME_LENGTH) 
		{
			Toast.makeText(getApplicationContext(), "Please enter a name no longer than " + MAX_NAME_LENGTH + " characters", Toast.LENGTH_LONG).show();
		}
		else
		{
		    if(!name.isEmpty())
		    {
      			new SendNewUserNameTask(this).execute(new UserIdNamePair(GlobalSettings.userIdAndName.userId, name));
      			
      			//Wait until the sendNewUserNameTask finishes. If it was unsuccessful, just keep the same name as currently.
      			try {
      				this.wait();
      			} catch (InterruptedException e) {
      				finish();
      			}
      			
      			editName.setHint(GlobalSettings.userIdAndName.userName);
      			editName.setText("");
		    }
			
			CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_filter);

			GlobalSettings.tagsToFilterFor.clear();
			
			for(int i=0; i<checkBoxTags.size(); i++)
            {
              if(checkBoxTags.get(i).isChecked())
              {
                GlobalSettings.tagsToFilterFor.add((String) checkBoxTags.get(i).getText());
              }
            }
			
			GlobalSettings.tagFilteringIsOn = checkBox.isChecked();
			
			finish();
		}

	}
}
