package screen.chatCreation;


import java.io.IOException;

import org.joda.time.DateTime;

import screen.inbox.InboxActivity;
import screen.map.MapActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import coderunners.geolocationalchat.R;

import com.google.android.gms.maps.model.LatLng;

import comm.HttpRequest;
import comm.TaskParams_SendNewChat;
import data.chatCreation.ChatSummaryToDb;

public class ChatCreationActivity extends ActionBarActivity {

	//TODO: Character limitations?
	public static final int MAX_TITLE_LENGTH = Integer.MAX_VALUE;
	public static final int MAX_MESSAGE_LENGTH = Integer.MAX_VALUE;

	private static final String SEND_NEW_CHAT_URI = "http://cmpt370duan.byethost10.com/createch.php";

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
