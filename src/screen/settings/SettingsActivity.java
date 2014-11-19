package screen.settings;

import coderunners.geolocationalchat.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

  //TODO: Limited name size?
  public static final int MAX_NAME_LENGTH = Integer.MAX_VALUE;
  
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
      //TODO: Send name to database
      editName.setHint(name);
      editName.setText("");
      finish();
    }
    
  }
  
}
