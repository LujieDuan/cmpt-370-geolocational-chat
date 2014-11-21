package screen.chatCreation;


import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import screen.inbox.InboxActivity;
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
import comm.HttpRequest;
import data.app.global.GlobalSettings;
import data.comm.ChatSummaryToDb;

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
		
		ArrayList<String> tags = GlobalSettings.allTags;
		
		LinearLayout tagsList = (LinearLayout) findViewById(R.id.tags_list);
		int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
		
		for(int i=0; i<tags.size(); i++)
		{
		  CheckBox checkBox = new CheckBox(getApplicationContext());
		  checkBox.setText(tags.get(i));
		  checkBox.setTextColor(getResources().getColor(R.color.black));
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
					title, GlobalSettings.curPhoneLocation, new ArrayList<String>(), GlobalSettings.userIdAndName.userId, message, new DateTime()));

			finish();
		}
	}
	
	/**
	 * Sends the new chat to the database, in the background, and makes toast if unsuccessful.
	 * @author wsv759
	 *
	 * @param ChatSummaryToDb a ChatSummaryToDb object.
	 */
	private class SendNewChatTask extends AsyncTask<ChatSummaryToDb, Void, Void>
	{	
		@Override
		protected Void doInBackground(ChatSummaryToDb... params) 
		{
			ChatSummaryToDb newChatSummary = params[0];
			
			try {
				String responseString = HttpRequest.post(newChatSummary, SEND_NEW_CHAT_URI);
				JSONObject responseJson = new JSONObject(responseString);

				if (responseJson.getInt(InboxActivity.TAG_SUCCESS) != HttpRequest.HTTP_RESPONSE_SUCCESS)
				{
					HttpRequest.makeToastOnRequestRejection(ChatCreationActivity.this, "response", false);
				}
			} catch (IOException e) {
				HttpRequest.makeToastOnServerTimeout(ChatCreationActivity.this, "response", false);
				Log.e("dbConnect", e.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
	}
}
