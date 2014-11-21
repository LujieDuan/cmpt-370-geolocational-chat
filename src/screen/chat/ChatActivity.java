package screen.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import screen.inbox.InboxActivity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import coderunners.geolocationalchat.R;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import comm.DateTimeDeserializer;
import comm.HttpRequest;
import comm.TaskParams_GetNewMessages;
import comm.TaskParams_SendNewMessage;

import data.chat.Chat;
import data.chat.ChatId;
import data.chat.ChatItem;
import data.chat.ChatMessageForScreen;
import data.chat.ChatMessageToDb;
import data.chat.ChatMessagesForScreen;
import data.global.GlobalSettings;

public class ChatActivity extends ActionBarActivity
{
	private static final String GET_NEW_MESSAGES_URI = "http://cmpt370duan.byethost10.com/getmess.php";
	private static final String SEND_NEW_MESSAGE_URI = "http://cmpt370duan.byethost10.com/create_message.php";
	private static final String TAG_MESSAGE_ARRAY = "messages";
	public static final String CHATID_STRING = "chatId";
	
	private Chat chat = new Chat();  
	private ChatId chatId;
	private MySimpleArrayAdapter adapter;
	
	private static final int GET_MESSAGES_DELAY_SEC = 5;
	
	private ScheduledThreadPoolExecutor chatUpdateScheduler;
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.chat_screen);
	    
	    chatId = getIntent().getExtras().getParcelable(CHATID_STRING);
	    
	    final ListView listView = (ListView) findViewById(R.id.listview);

	    adapter = new MySimpleArrayAdapter(this, chat.getChatItems());
	    listView.setAdapter(adapter);
	    
	    chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);
	    chatUpdateScheduler.scheduleWithFixedDelay(new GetNewMessagesTask(), 0, GET_MESSAGES_DELAY_SEC, TimeUnit.SECONDS);
	  }
	
	public void sendMessage(View v)
	{	
		EditText editText = (EditText) findViewById(R.id.EditText);
		String message = editText.getText().toString().trim();
		editText.setText("");
		if(!message.equals(""))
		{
			//TODO implement a dummy message, for immediate viewing.
//			chat.addMessages(new ChatMessageForScreen(message,MapActivity.USER_ID_AND_NAME.userId,MapActivity.USER_ID_AND_NAME.userName, FAKE_MESSAGE_ID, new DateTime()));
//			onChatUpdated();
		
			new SendNewMessageTask().execute(new ChatMessageToDb(message, GlobalSettings.userIdAndName.userId, chatId));
		}
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<ChatItem> {
		  
		private final Context context;
		private final ArrayList<ChatItem> values;

		public MySimpleArrayAdapter(Context context, ArrayList<ChatItem> values) {
			super(context, R.layout.chat_item_me, values);
			this.context = context;
		    this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View itemView;
			
			if(values.get(position).getUserId().equals(GlobalSettings.userIdAndName.userId))
			{
				itemView = inflater.inflate(R.layout.chat_item_me, parent, false);
				LinearLayout bubbleList = (LinearLayout) itemView.findViewById(R.id.chat_bubble_list);
				for(int i=0; i<values.get(position).messages.size(); i++)
				{
					View bubbleView = inflater.inflate(R.layout.chat_bubble_me, parent, false);
					TextView textViewMessage = (TextView) bubbleView.findViewById(R.id.textViewMessage);
					textViewMessage.setText(values.get(position).getMessage(i));
					bubbleList.addView(bubbleView);
				}
			}
			else
			{
				itemView = inflater.inflate(R.layout.chat_item_them, parent, false);
				LinearLayout bubbleList = (LinearLayout) itemView.findViewById(R.id.chat_bubble_list);
				for(int i=0; i<values.get(position).messages.size(); i++)
				{
					View bubbleView = inflater.inflate(R.layout.chat_bubble_them, parent, false);
					TextView textViewMessage = (TextView) bubbleView.findViewById(R.id.textViewMessage);
					textViewMessage.setText(values.get(position).getMessage(i));
					bubbleList.addView(bubbleView);
				}
			}
			
			TextView textViewName = (TextView) itemView.findViewById(R.id.textViewName);	
			textViewName.setText(values.get(position).getName());
			TextView textViewTimeLocation = (TextView) itemView.findViewById(R.id.timeAndLocation);
			
			Location location = new Location("");
			location.setLatitude(0);
			location.setLongitude(0);
			
			textViewTimeLocation.setText(values.get(position).getTimeString(new DateTime()));
			
			return itemView;
		}
		
	} 
	
	/**
	 * Update the UI when the underlying chat messages have changed.
	 */
	private void onChatUpdated()
	{
		adapter.notifyDataSetChanged();
		
		ListView listView = (ListView) findViewById(R.id.listview);
		listView.smoothScrollToPosition(listView.getBottom());
	}
	
	/**
	 * Stop updating the chat, when you leave the chat.
	 */
	@Override
	public void onBackPressed() 
	{
		chatUpdateScheduler.shutdownNow();
		ChatActivity.super.onBackPressed();
	}
	
	/**
	 * Get any new messages for this chat from the database, in the background. 
	 * On success, update the chat. Make toast on failure.
	 * @author wsv759
	 *
	 */
	private class GetNewMessagesTask implements Runnable 
	{
	    @Override
	    public void run() 
	    {
	    	int lastMessageId = -1;
	    	if (chat.numMessages() > 0)
	    	{		    		
	    		lastMessageId = chat.getChatMessageForScreen(chat.numMessages() - 1).messageId;
	    	}
	    	
			TaskParams_GetNewMessages sendParams = new TaskParams_GetNewMessages(chatId, lastMessageId);
			
			try {
				String responseString = HttpRequest.get(sendParams, GET_NEW_MESSAGES_URI);
				JSONObject responseJson = new JSONObject(responseString);
				
				if (responseJson.getInt(InboxActivity.TAG_SUCCESS) == HttpRequest.HTTP_RESPONSE_SUCCESS)
				{
					JSONArray messages = responseJson.getJSONArray(TAG_MESSAGE_ARRAY);
					
					Log.d("dbConnect", "messages: " + messages);
					Log.d("dbConnect", "trying to convert json...");
					GsonBuilder gsonBuilder = new GsonBuilder(); 
					gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
				    Gson gson = gsonBuilder.create();
				    ChatMessageForScreen[] newChatMessages = gson.fromJson(responseString, ChatMessagesForScreen.class).messages;
					Log.d("dbConnect", "new chat messages: " + newChatMessages.toString());
					
					chat.addMessages(newChatMessages);
					runOnUiThread(new Runnable() {

		                @Override
		                public void run() {
		                    onChatUpdated();
		                }
		            });
				}
				else
				{
					HttpRequest.makeToastOnRequestRejection(ChatActivity.this, "new messages", true);
				}
			} catch (IOException e) {		
				HttpRequest.makeToastOnServerTimeout(ChatActivity.this, "new messages", true);
				Log.e("dbConnect", e.toString());
			} catch (JSONException | JsonSyntaxException e) {
				e.printStackTrace();
			}
	    }
	}
	
	/**
	 * Send a new message to the database, in the background. On failure, make toast. 
	 * @author wsv759
	 *
	 */
	private class SendNewMessageTask extends AsyncTask<ChatMessageToDb, Void, Void>
	{	
		@Override
		protected Void doInBackground(ChatMessageToDb... params) {
			TaskParams_SendNewMessage sendEntity = new TaskParams_SendNewMessage(params[0]);
			
			try {
				String responseString = HttpRequest.post(sendEntity, SEND_NEW_MESSAGE_URI);
				JSONObject responseJson = new JSONObject(responseString);

				if (responseJson.getInt(InboxActivity.TAG_SUCCESS) != HttpRequest.HTTP_RESPONSE_SUCCESS)
				{
					HttpRequest.makeToastOnRequestRejection(ChatActivity.this, "response", false);
				}
			} catch (IOException e) {
				HttpRequest.makeToastOnServerTimeout(ChatActivity.this, "response", false);
				Log.e("dbConnect", e.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			//TODO immediately get new messages.
			return null;
		}
	}
} 