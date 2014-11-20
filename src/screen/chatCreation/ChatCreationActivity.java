package screen.chatCreation;


import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import screen.inbox.InboxActivity;
import screen.map.MapActivity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import coderunners.geolocationalchat.R;

import com.google.android.gms.maps.model.LatLng;

import comm.HttpRequest;
import comm.TaskParams_SendNewChat;
import data.chatCreation.ChatSummaryToDb;

/**
 * The chat creation activity can be used by the user to create a new chat at
 * their current location, adding a title and an initial message. In the future
 * we'd like to add additional options such as a time limit once we add more
 * moderation tools for the user.
 */
public class ChatCreationActivity extends ActionBarActivity {

	//TODO: Character limitations?
    /**
     * Minimum length of a title that can be submitted by a user
     */
	public static final int MIN_TITLE_LENGTH = 1;
	
	/**
	 * Maximum length of a title that can be submitted by a user
	 */
    public static final int MAX_TITLE_LENGTH = Integer.MAX_VALUE;
	
    /**
     * Minimum length of a message that can be submitted by a user
     */
	public static final int MIN_MESSAGE_LENGTH = 1;
	
	/**
	 * Maximum length of a message that can be submitted by a user
	 */
	public static final int MAX_MESSAGE_LENGTH = Integer.MAX_VALUE;

	private static final String SEND_NEW_CHAT_URI = "http://cmpt370duan.byethost10.com/createch.php";

	/**
	 * Creates a new Chat Creation window.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_creation_activity);
		
		//TODO: Grab tags from database
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("Sports");
		tags.add("Event");
		tags.add("Food");
		tags.add("Games");
		tags.add("Request");
		
		LinearLayout tagsList = (LinearLayout) findViewById(R.id.tags_list);
		int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
		
		for(int i=0; i<tags.size(); i++)
		{
		  CheckBox checkBox = new CheckBox(getApplicationContext());
		  checkBox.setText(tags.get(i));
		  checkBox.setTextColor(getResources().getColor(R.color.abc_secondary_text_material_light));
		  checkBox.setButtonDrawable(id);
		  tagsList.addView(checkBox);
		}
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

		if(title.length() < MIN_TITLE_LENGTH)
		{
			Toast.makeText(getApplicationContext(), "Your title must be longer than " + MIN_TITLE_LENGTH + " characeters", Toast.LENGTH_LONG).show();
		}
		else if(title.length() > MAX_TITLE_LENGTH) 
		{
			Toast.makeText(getApplicationContext(), "Your title must be no longer than " + MAX_TITLE_LENGTH + " characters", Toast.LENGTH_LONG).show();
		}
		else if(message.length() < MIN_MESSAGE_LENGTH)
		{
			Toast.makeText(getApplicationContext(), "Your message must be longer than " + MIN_MESSAGE_LENGTH + " characters", Toast.LENGTH_LONG).show();
		}
		else if(message.length() > MAX_MESSAGE_LENGTH) 
		{
			Toast.makeText(getApplicationContext(), "Your message must be no longer than " + MAX_MESSAGE_LENGTH + " characters", Toast.LENGTH_LONG).show();
		}
		else
		{
			new SendNewChatTask().execute(new ChatSummaryToDb(
					title, new LatLng(InboxActivity.LAT,InboxActivity.LONG), new String[]{"fake tag 1", "fake tag 2"}, MapActivity.USER_ID_AND_NAME.userId, message, new DateTime()));

			finish();
		}
	}

	private class SendNewChatTask extends AsyncTask<ChatSummaryToDb, Void, Void>
	{	
		@Override
		protected Void doInBackground(ChatSummaryToDb... params) 
		{
			TaskParams_SendNewChat sendEntity = new TaskParams_SendNewChat(params[0]);
			
			try {
				HttpRequest.post(sendEntity, SEND_NEW_CHAT_URI);
			} catch (IOException e) {
				//TODO: Implement retries properly, presumably by setting the DefaultHttpRequestRetryHandler.
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			}

			return null;
		}
	}
}
