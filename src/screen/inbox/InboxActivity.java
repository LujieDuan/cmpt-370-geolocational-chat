package screen.inbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import data.chat.ChatId;
import data.global.GlobalSettings;
import data.global.UserIdNamePair;
import data.inbox.ChatSummariesForScreen;
import data.inbox.ChatSummaryForScreen;


public class InboxActivity extends ListActivity {
	
	public static String DEVICE_ID;
	public static String USER_NAME = "John";
	public static UserIdNamePair USER_ID_AND_NAME;
	
	private static final String GET_INBOX_URI = "http://cmpt370duan.byethost10.com/getchs.php"; 

	public static final String TAG_SUCCESS = "success";
	private static final String TAG_CHATSUMMARY_ARRAY = "chats"; 
	
	public static double LONG = 25;
	public static double LAT = 50;
	
	private static final int GET_INBOX_DELAY_SECONDS = 30;
	
    private ArrayAdapter<ChatSummaryForScreen> adapter;
    
    private ArrayList<ChatSummaryForScreen> chatSummaries = new ArrayList<ChatSummaryForScreen>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	String device_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
    	USER_ID_AND_NAME = new UserIdNamePair(device_id, device_id);
    	
        adapter = new InboxItemArrayAdapter(this, chatSummaries);
        setListAdapter(adapter);
        
	    ScheduledThreadPoolExecutor chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);
	    chatUpdateScheduler.scheduleWithFixedDelay(new GetInboxTask(), 0, GET_INBOX_DELAY_SECONDS, TimeUnit.SECONDS);
    }
    
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent chatScreenIntent = new Intent(InboxActivity.this, ChatActivity.class);
    	ChatId curChatId = chatSummaries.get(position).chatId;
    	Log.d("intents", "chatId1: " + curChatId.toString());
    	chatScreenIntent.putExtra(ChatActivity.CHATID_STRING,curChatId);
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
	    	LatLng l = GlobalSettings.curPhoneLocation;
			ArrayList<String> tags = new ArrayList<String>();
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
					gsonBuilder.registerTypeAdapter(ChatSummariesForScreen.class, new ChatSummariesForScreenDeserializer());
					Gson gson = gsonBuilder.create();
				    ChatSummaryForScreen[] newChatSummaries = gson.fromJson(responseString, ChatSummariesForScreen.class).chats;
					
				    Log.d("dbConnect", "new chat summaries title one: " + newChatSummaries[0].title);
				    
				    ArrayList<ChatSummaryForScreen> newChatSummariesList = new ArrayList<ChatSummaryForScreen>(Arrays.asList(newChatSummaries));
				    chatSummaries.clear();
				    chatSummaries.addAll(newChatSummariesList);
				    
					runOnUiThread(new Runnable() {

		                @Override
		                public void run() {
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
}
