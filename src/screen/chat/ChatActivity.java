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
import data.chat.ChatMessageFromDb;

import org.joda.time.DateTime;
import org.json.JSONException;

import com.google.gson.Gson;

import comm.HttpRequest;
import comm.TaskParams_GetNewMessages;

public class ChatActivity extends Activity
{
	private static final String GET_NEW_MESSAGES_URI = "http://cmpt370duan.byethost10.com/get_user_name.php";
	
	private static Chat chat;
	  
	private static MySimpleArrayAdapter adapter;
	  
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		chat = new Chat();
		chat.addMessages(
		    new ChatMessageFromDb("Programming contest this weekend!", "Mike's ID", "Mike",1, new DateTime()),
		    new ChatMessageFromDb( "I will be there!","Tom's ID","Tom", 2, new DateTime()),
		    new ChatMessageFromDb( "Looking forward!~","Doris' ID","Doris",  3, new DateTime()),
		    new ChatMessageFromDb( "Nice:-", "Will's ID","Will", 4, new DateTime()),
		    new ChatMessageFromDb("How much is the ticket?","Anthony's ID", "Anthony", 5 , new DateTime()),
		    new ChatMessageFromDb("On what platform?", "My ID", "Me", 6, new DateTime()),
		    new ChatMessageFromDb("Windows I think","Will's ID", "Will",  7, new DateTime()));
		
	    setContentView(R.layout.chat_screen);

	    final ListView listView = (ListView) findViewById(R.id.listview);

	    adapter = new MySimpleArrayAdapter(this, chat.chatItems);
	    listView.setAdapter(adapter);
	    
	    ScheduledThreadPoolExecutor chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);
	    
	    chatUpdateScheduler.scheduleWithFixedDelay(new GetNewMessagesTask(), 0, 5, TimeUnit.SECONDS);
	  }
	
	public void sendMessage(View v)
	{
		View parentView = (View) v.getParent();
		parentView = (View) parentView.getParent();
		ListView listView = (ListView) parentView.findViewById(R.id.listview);
		
		EditText editText = (EditText) findViewById(R.id.EditText);
		String message = editText.getText().toString().trim();
		editText.setText("");
		if(!message.equals(""))
		{
		    chat.addMessages(new ChatMessageFromDb(message,"My ID","Me", 8, new DateTime()));
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
			adapter.notifyDataSetChanged();
			
			listView.smoothScrollToPosition(listView.getBottom());
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
	
	private class GetNewMessagesTask implements Runnable 
	{
		private ChatMessageFromDb[] newChatMessages = null;
		
	    @Override
	    public void run() 
	    {
	    	ChatId chatId = new ChatId("123456789012345", new DateTime(2014,10,25,16,46,29));
			
			int lastMessageId = 0;
			TaskParams_GetNewMessages sendParams = new TaskParams_GetNewMessages(chatId, lastMessageId);
			
			Gson gson = new Gson(); 
//			String json = gson.toJson(sendParams);
			
			String responseString = "";
			try {
				responseString = HttpRequest.get(sendParams, GET_NEW_MESSAGES_URI);
				
				newChatMessages = gson.fromJson(responseString, ChatMessageFromDb[].class);
			} catch (JSONException | IOException e) {
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			}
			chat.addMessages(newChatMessages);
			adapter.notifyDataSetChanged();
	    }
	}
} 