package screen.inbox;

import java.util.ArrayList;

import org.joda.time.DateTime;

import screen.chat.ChatActivity;
import coderunners.geolocationalchat.R;
import data.chat.ChatMessage;
import data.chat.ChatSummary;
import android.location.Location;
import android.os.Bundle;
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
	
    ArrayAdapter<ChatSummary> adapter;
    
    ArrayList<ChatSummary> chatSummaries = new ArrayList<ChatSummary>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	chatSummaries.add(new ChatSummary(new ChatMessage("Josh Heinrichs", "Josh's ID", "Anyone up for a game of ultimate frisbee?", new Location(""), new DateTime()), 40, 40));
    	chatSummaries.add(new ChatSummary(new ChatMessage("Josh Heinrichs", "Josh's ID", "Anyone up for a game of ultimate frisbee?", new Location(""), new DateTime()), 40, 40));
    	chatSummaries.add(new ChatSummary(new ChatMessage("Josh Heinrichs", "Josh's ID", "Anyone up for a game of ultimate frisbee?", new Location(""), new DateTime()), 40, 40));
        
        adapter = new InboxItemArrayAdapter(this, chatSummaries);
        setListAdapter(adapter);
        
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
}
