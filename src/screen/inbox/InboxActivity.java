package screen.inbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import comm.HttpRequest;
import comm.TaskParams_GetInbox;
import comm.TaskParams_SendNewChat;
import screen.chat.ChatActivity;
import coderunners.geolocationalchat.R;
import data.chat.ChatId;
import data.inbox.ChatSummaryForScreen;
import data.newChatCreation.ChatSummaryToDb;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;


public class InboxActivity extends ListActivity {

	
//	String[] names = new String[] { 
//    		"Josh Heinrichs", 
//    		"Karen Janzen", 
//    		"William van der Kamp", 
//    		"ASSU", 
//    		"Josh Heinrichs", 
//    		"Josh Heinrichs", 
//    		"Josh Heinrichs", 
//    		"Josh Heinrichs" };
//    String[] posts = new String[] { 
//    		"Anyone up for a game of ultimate frisbee?", 
//    		"Anyone want to meet for coffee?", 
//    		"Can anyone give me a ride to the university? Hard to get to class without busses available.", 
//    		"Free burgers outside!", 
//    		"I just found a great deal at Staples!", 
//    		"Anyone up for a game of ultimate frisbee?", 
//    		"Anyone up for a game of ultimate frisbee?", 
//    		"Anyone up for a game of ultimate frisbee?", 
//    		"Anyone up for a game of ultimate frisbee?", };
//    String[] times = new String[] { 
//    		"2 hours ago", 
//    		"30 minutes ago", 
//    		"2 hours ago", 
//    		"2 hours ago", 
//    		"2 hours ago", 
//    		"2 hours ago", 
//    		"2 hours ago", 
//    		"2 hours ago", 
//    		"2 hours ago" };
//    String[] replies = new String[] { 
//    		"2 replies", 
//    		"0 replies", 
//    		"2 replies", 
//    		"2 replies", 
//    		"2 replies", 
//    		"2 replies", 
//    		"2 replies", 
//    		"2 replies", 
//    		"2 replies" };
//    String[] distances = new String[] { 
//    		"500m away", 
//    		"2km away", 
//    		"500m away", 
//    		"500m away", 
//    		"500m away", 
//    		"500m away", 
//    		"500m away", 
//    		"500m away", 
//    		"500m away" };
//    int[] chatIds = new int[] {
//    		1,
//    		2,
//    		3,
//    		4,
//    		5,
//    		6,
//    		7,
//    		8,
//    		9
//    };
	public static String DEVICE_ID;
	public static final String USER_NAME = "John";
	
	private static final String GET_INBOX_URI = "someUri"; 
	private static final String SEND_NEW_CHAT_URI = "someOtherUri"; 
	
    private static ArrayAdapter<ChatSummaryForScreen> adapter;
    
    private static ArrayList<ChatSummaryForScreen> chatSummaries = new ArrayList<ChatSummaryForScreen>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	DEVICE_ID = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
    	
    	chatSummaries.add(new ChatSummaryForScreen("Massage Needed",  new Location(""), new String[]{"massage"}, new ChatId("", null),"Josh", 40, 40, new DateTime()));
    	chatSummaries.add(new ChatSummaryForScreen("Massage Needed",  new Location(""), new String[]{"massage"}, new ChatId("", null),"Josh", 40, 40, new DateTime()));
    	chatSummaries.add(new ChatSummaryForScreen("Massage Needed",  new Location(""), new String[]{"massage"}, new ChatId("", null),"Josh", 40, 40, new DateTime()));
        
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
		private ChatSummaryForScreen[] newChatSummaries = null;
		
	    @Override
	    public void run() 
	    {
	    	Location l = new Location("");
			String[] tags = {""};
			TaskParams_GetInbox sendParams = new TaskParams_GetInbox(l, tags);
			
			Gson gson = new Gson(); 
			
			String responseString = "";
			try {
				responseString = HttpRequest.get(sendParams, GET_INBOX_URI);
				newChatSummaries = gson.fromJson(responseString, ChatSummaryForScreen[].class);
			} catch (IOException | JsonSyntaxException e) {
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			}
			
			chatSummaries = new ArrayList<ChatSummaryForScreen>(Arrays.asList(newChatSummaries));
			adapter.notifyDataSetChanged();
	    }
	}
    
    //This may have to go in the "new chat screen"
    private class SendNewChatTask implements Runnable
    {
    	 @Override
 	    public void run() 
 	    {
 	    	ChatSummaryToDb c = new ChatSummaryToDb(
 	    			"new chat title", new Location(""), new String[]{""}, "creator user id", "first message", 100, new DateTime());
 			TaskParams_SendNewChat sendEntity = new TaskParams_SendNewChat(c);
 			
 			try {
 				HttpRequest.put(sendEntity, SEND_NEW_CHAT_URI);
 			} catch (IOException e) {
 				e.printStackTrace();
 				Log.e("dbConnect", e.toString());
 			}
 	    }
    }
}
