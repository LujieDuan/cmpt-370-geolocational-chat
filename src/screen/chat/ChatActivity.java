package screen.chat;

import java.io.IOException;
import java.util.ArrayList;













import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
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
import data.chat.Chat;
import data.chat.ChatId;
import data.chat.ChatItem;
import data.chat.ChatMessageForScreen;
import data.chat.ChatMessageToDb;
import data.chat.ChatMessagesForScreen;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import screen.inbox.InboxActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import comm.DateTimeDeserializer;
import comm.HttpRequest;
import comm.TaskParams_GetNewMessages;
import comm.TaskParams_SendNewMessage;

public class ChatActivity extends Activity
{
	private static final String GET_NEW_MESSAGES_URI = "http://cmpt370duan.byethost10.com/getmess.php";
	private static final String SEND_NEW_MESSAGE_URI = "someKindaUri";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE_ARRAY = "messages";
	
	private static final int FAKE_MESSAGE_ID = -1;
	private Chat chat = new Chat();  
	private static MySimpleArrayAdapter adapter;
	  
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		chat.addMessages(
//		    new ChatMessageForScreen("Programming contest this weekend!", "Mike's ID", "Mike",1, new DateTime()),
//		    new ChatMessageForScreen( "I will be there!","Tom's ID","Tom", 2, new DateTime()),
//		    new ChatMessageForScreen( "Looking forward!~","Doris' ID","Doris",  3, new DateTime()),
//		    new ChatMessageForScreen( "Nice:-", "Will's ID","Will", 4, new DateTime()),
//		    new ChatMessageForScreen("How much is the ticket?","Anthony's ID", "Anthony", 5 , new DateTime()),
//		    new ChatMessageForScreen("On what platform?", "My ID", "Me", 6, new DateTime()),
//		    new ChatMessageForScreen("Windows I think","Will's ID", "Will",  7, new DateTime()));
		
	    setContentView(R.layout.chat_screen);

	    final ListView listView = (ListView) findViewById(R.id.listview);

	    adapter = new MySimpleArrayAdapter(this, chat.chatItems);
	    listView.setAdapter(adapter);
	    
	    ScheduledThreadPoolExecutor chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);
	    chatUpdateScheduler.scheduleWithFixedDelay(new GetNewMessagesTask(), 0, 5, TimeUnit.SECONDS);
	  }
	
	public void sendMessage(View v)
	{	
		EditText editText = (EditText) findViewById(R.id.EditText);
		String message = editText.getText().toString().trim();
		editText.setText("");
		if(!message.equals(""))
		{
			chat.addMessages(new ChatMessageForScreen(message,InboxActivity.DEVICE_ID,InboxActivity.USER_NAME, FAKE_MESSAGE_ID, new DateTime()));
			onChatUpdated();
			
			Thread sendMessageThread = new Thread(new SendNewMessageTask());
			sendMessageThread.run();
			
//			if(valueList.get(valueList.size() - 1).name.equals("Me"))
//			{
//				valueList.get(valueList.size() - 1).messages.add(message);
//				valueList.get(valueList.size() - 1).time = "just now";
//				valueList.get(valueList.size() - 1).distance = "0m";
//			}
//			else
//			{
//				valueList.add(new ChatItem ("Me", message, "just now", "0m") );
//			}
			
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
			
			//TODO: Should check by Phone ID rather than name
			if(values.get(position).getUserId().equals("My ID"))
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
	
	private void onChatUpdated()
	{
		
		adapter.notifyDataSetChanged();
		
		ListView listView = (ListView) findViewById(R.id.listview);
		listView.smoothScrollToPosition(listView.getBottom());
	}
	
	private class GetNewMessagesTask implements Runnable 
	{
	    @Override
	    public void run() 
	    {
	    	ChatId chatId = new ChatId("123456789012345", new DateTime(2014,10,25,16,46,29));
	    	int lastMessageId = -1;
	    	if (chat.chatItems.size() > 0)
	    	{	
	    		ArrayList<ChatMessageForScreen> messages = chat.chatItems.get(chat.chatItems.size() - 1).messages;
	    		lastMessageId = messages.get(messages.size() - 1).messageId;
	    	}
	    	
			TaskParams_GetNewMessages sendParams = new TaskParams_GetNewMessages(chatId, lastMessageId);
			
			
			String responseString = "";
			try {
				responseString = HttpRequest.get(sendParams, GET_NEW_MESSAGES_URI);
				JSONObject responseJson = new JSONObject(responseString);
				
				if (responseJson.getInt(TAG_SUCCESS) == 1)
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
				
			} catch (IOException | JsonSyntaxException e) {
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	private class SendNewMessageTask implements Runnable 
	{	
	    @Override
	    public void run() 
	    {
	    	ChatMessageToDb c = new ChatMessageToDb(
	    			"new message contents", "My ID", new ChatId("chat creator's Id", new DateTime()));
			TaskParams_SendNewMessage sendEntity = new TaskParams_SendNewMessage(c);
			
			try {
				HttpRequest.put(sendEntity, SEND_NEW_MESSAGE_URI);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			}
	    }
	}
} 