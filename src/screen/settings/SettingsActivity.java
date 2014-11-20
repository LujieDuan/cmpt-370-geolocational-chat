package screen.settings;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import coderunners.geolocationalchat.R;

import comm.HttpRequest;
import comm.TaskParams_SendNewChat;
import comm.TaskParams_SendNewUserName;

import data.UserIdNamePair;

public class SettingsActivity extends Activity {

  //TODO: Limited name size?
  public static final int MAX_NAME_LENGTH = Integer.MAX_VALUE;
  
  public static final String SEND_NEW_ALIAS_URI = "a really good uri";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.settings_activity);
    
    EditText editName = (EditText) findViewById(R.id.edit_name);
    //TODO: Get user's name;
    String name = "Anonymous";
    editName.setHint(name);
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
  
  public void onSubmit(View v)
  {
    EditText editName = (EditText) findViewById(R.id.edit_name);
    String name = editName.getText().toString().trim();
    
    if(name.isEmpty())
    {
      Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_LONG).show();
    }
    else if(name.length() > MAX_NAME_LENGTH) 
    {
      Toast.makeText(getApplicationContext(), "Your name must be no longer than " + MAX_NAME_LENGTH + " characters", Toast.LENGTH_LONG).show();
    }
    else
    {
    	//TODO: finish this once I have got usernames down.
//      new SendNewAliasTask().execute()
      editName.setHint(name);
      editName.setText("");
      finish();
    }
    
  }
  
  private class SendNewAliasTask extends AsyncTask<UserIdNamePair, Void, Void>
	{	
		@Override
		protected Void doInBackground(UserIdNamePair... params) 
		{
			TaskParams_SendNewUserName sendEntity = new TaskParams_SendNewUserName(params[0]);

			try {
				HttpRequest.put(sendEntity, SEND_NEW_ALIAS_URI);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			}

			return null;
		}
	}
}
