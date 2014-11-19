package screen.chatCreation;

import coderunners.geolocationalchat.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChatCreationActivity extends Activity {

  //TODO: Character limitations?
  public static final int MAX_TITLE_LENGTH = Integer.MAX_VALUE;
  public static final int MAX_MESSAGE_LENGTH = Integer.MAX_VALUE;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.chat_creation_activity);
  }
  
  
  /**
   * Exits the activity, discarding any changes which have been entered.
   * @param v
   */
  public void onCancel(View v)
  {
    finish();
  }
  
  /**
   * Submits the new chat to the database, provided that all required fields
   * have been given input. Exits the activity if successful, and prompts the
   * user for the missing information otherwise.
   * @param v
   */
  public void onSubmit(View v)
  {
    EditText editTitle = (EditText) findViewById(R.id.edit_title);
    EditText editMessage = (EditText) findViewById(R.id.edit_message);
    
    String title = editTitle.getText().toString().trim();
    String message = editMessage.getText().toString().trim();
    
    if(title.isEmpty())
    {
      Toast.makeText(getApplicationContext(), "Please enter a title", Toast.LENGTH_LONG).show();
    }
    else if(title.length() > MAX_TITLE_LENGTH) 
    {
      Toast.makeText(getApplicationContext(), "Your title must be no longer than " + MAX_TITLE_LENGTH + " characters", Toast.LENGTH_LONG).show();
    }
    else if(message.isEmpty())
    {
      Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
    }
    else if(message.length() > MAX_MESSAGE_LENGTH) 
    {
      Toast.makeText(getApplicationContext(), "Your message must be no longer than " + MAX_MESSAGE_LENGTH + " characters", Toast.LENGTH_LONG).show();
    }
    else
    {
      //TODO: Send chat to database
      finish();
    }
  }
  
  
}
