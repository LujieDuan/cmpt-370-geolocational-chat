package screen.inbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import screen.chat.ChatActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import coderunners.geolocationalchat.R;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import comm.ChatSummariesForScreenDeserializer;
import comm.HttpRequest;
import comm.TaskParams_GetInbox;
import comm.TaskParams_SendNewChat;

import data.chat.ChatId;
import data.inbox.ChatSummariesForScreen;
import data.inbox.ChatSummaryForScreen;
import data.newChatCreation.ChatSummaryToDb;


public class InboxActivity extends ListActivity {
	
	public static String DEVICE_ID;
	public static String USER_NAME = "John";
	
	private static final String GET_INBOX_URI = "http://cmpt370duan.byethost10.com/getchs.php"; 
	private static final String SEND_NEW_CHAT_URI = "someOtherUri";
	public static final String TAG_SUCCESS = "success";
	private static final String TAG_CHATSUMMARY_ARRAY = "chats"; 
	
	public static final String CHATID_STRING = "chatId";
	
	static double LONG = 25;
	static double LAT = 50;
    private static ArrayAdapter<ChatSummaryForScreen> adapter;
    
    private static ArrayList<ChatSummaryForScreen> chatSummaries = new ArrayList<ChatSummaryForScreen>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	DEVICE_ID = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
    	
//    	chatSummaries.add(new ChatSummaryForScreen("Massage Needed",  new LatLng(LAT,LONG), new String[]{"massage"}, new ChatId("", null),"Josh", 40, 40, new DateTime()));
//    	chatSummaries.add(new ChatSummaryForScreen("Massage Needed",  new LatLng(LAT,LONG), new String[]{"massage"}, new ChatId("", null),"Josh", 40, 40, new DateTime()));
//    	chatSummaries.add(new ChatSummaryForScreen("Massage Needed",  new LatLng(LAT,LONG), new String[]{"massage"}, new ChatId("", null),"Josh", 40, 40, new DateTime()));
        
        adapter = new InboxItemArrayAdapter(this, chatSummaries);
        setListAdapter(adapter);
        
	    ScheduledThreadPoolExecutor chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);
	    chatUpdateScheduler.scheduleWithFixedDelay(new GetInboxTask(), 0, 30, TimeUnit.SECONDS);
//        final Handler handler = new Handler();
//        handler.post(new Runnable(){
//        	
//        	@Override
//        	public void run() {
//        		
//        		if(names[0].equals("Josh Heinrichs"))
//        			names[0] = "hello";
//        		else
//        			names[0] = "Josh Heinrichs";
//        		
//        		adapter.notifyDataSetChanged();
//        		
//        		handler.postDelayed(this,  1000);
//        		
//        	}
//        });
    }
    
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//      String item = (String) getListAdapter().getItem(position);
//      Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    	
    	Intent chatScreenIntent = new Intent(InboxActivity.this, ChatActivity.class);
    	ChatId curChatId = chatSummaries.get(position).chatId;
    	Log.d("intents", "chatId1: " + curChatId.toString());
    	chatScreenIntent.putExtra(CHATID_STRING,curChatId);
    	startActivity(chatScreenIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class GetInboxTask implements Runnable 
	{
	    @Override
	    public void run() 
	    {
	    	LatLng l = new LatLng(LAT,LONG);
			String[] tags = {""};
			TaskParams_GetInbox sendParams = new TaskParams_GetInbox(l, tags);
			try {
				String responseString = HttpRequest.get(sendParams, GET_INBOX_URI);
				JSONObject responseJson = new JSONObject(responseString);
				if (responseJson.getInt(TAG_SUCCESS) == 1)
				{
					JSONArray summaries = responseJson.getJSONArray(TAG_CHATSUMMARY_ARRAY);
					
					Log.d("dbConnect", "chat summaries: " + summaries);
					Log.d("dbConnect", "trying to convert json...");
					GsonBuilder gsonBuilder = new GsonBuilder();
//					gsonBuilder.registerTypeAdapter(ChatId.class, new ChatIdDeserializer());
//					gsonBuilder.registerTypeAdapter(LatLng.class, new LatLngDeserializer());
					gsonBuilder.registerTypeAdapter(ChatSummariesForScreen.class, new ChatSummariesForScreenDeserializer());
					Gson gson = gsonBuilder.create();
				    ChatSummaryForScreen[] newChatSummaries = gson.fromJson(responseString, ChatSummariesForScreen.class).chats;
					
				    Log.d("dbConnect", "new chat summaries title one: " + newChatSummaries[0].title);
					
				    for (int i = 0; i < newChatSummaries.length; i++)
				    {
				    	if (newChatSummaries[i].chatId == null)
				    		Log.d("dbConnect", "null chatId!");
				    	if (newChatSummaries[i].location == null)
				    		Log.d("dbConnect", "null location!");
//				    	Log.d("intents", "chatId0: " + newChatSummaries[i].chatId.toString());
				    }
				    ArrayList<ChatSummaryForScreen> newChatSummariesList = new ArrayList<ChatSummaryForScreen>(Arrays.asList(newChatSummaries));
				    chatSummaries.clear();
				    chatSummaries.addAll(newChatSummariesList);
				    
				    
//					adapter.notifyDataSetChanged();
					runOnUiThread(new Runnable() {

		                @Override
		                public void run() {
		                	Log.d("dbConnect", "notifying...");
		                	adapter.notifyDataSetChanged();
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
    
    //This may have to go in the "new chat screen"
    private class SendNewChatTask implements Runnable
    {
    	 @Override
 	    public void run() 
 	    {
 	    	ChatSummaryToDb c = new ChatSummaryToDb(
 	    			"new chat title", new LatLng(LAT,LONG), new String[]{""}, "creator user id", "first message", 100, new DateTime());
 			TaskParams_SendNewChat sendEntity = new TaskParams_SendNewChat(c);
 			
 			try {
 				HttpRequest.post(sendEntity, SEND_NEW_CHAT_URI);
 			} catch (IOException e) {
 				e.printStackTrace();
 				Log.e("dbConnect", e.toString());
 			}
 	    }
    }
}
