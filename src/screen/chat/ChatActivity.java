package screen.chat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
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

public class ChatActivity extends Activity
{
//  JSONParser jParser = new JSONParser();
//  ArrayList<HashMap<String, String>> productsList = new ArrayList<HashMap<String, String>>();
//  
//  // url to get all products list
//  private static String url_all_products = "http://192.168.0.14/android/getSomeThing.php";
//
//  // JSON Node names
//  private static final String TAG_SUCCESS = "success";
//  private static final String TAG_PRODUCTS = "products";
//  private static final String TAG_PID = "pid";
//  private static final String TAG_NAME = "name";
//
//  // products JSONArray
//  JSONArray products = null;

	
	ArrayList<ChatItem> valueList = new ArrayList<ChatItem>();	  
	  
	MySimpleArrayAdapter adapter;
	  
	@SuppressLint("NewApi") @Override
	  protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//for Internet!!!!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		
		  String result = "";
		    InputStream isr = null;
		    try{
		    	HttpClient httpclient = new DefaultHttpClient();
		    	HttpPost httppost = new HttpPost("http://cmpt370duan.byethost10.com/getSomeThing.php");
		    	HttpResponse response = httpclient.execute(httppost);
		    	HttpEntity entity = response.getEntity();
		    	isr = entity.getContent();
		    }catch(Exception e){
		    	Log.e("log_tag", "Error in http connection " + e.toString());
		    }

		    try{
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
		    	StringBuilder sb = new StringBuilder();
		    	String line = null;
		    	while ((line = reader.readLine()) != null){
		    			sb.append(line+ "..");
		    			
		    	}
		    	isr.close();
		    	result = sb.toString();
		    }
		    catch (Exception e){
		    	Log.e("log_tag", "Error converting result" + e.toString());
		    }
		    
		    Log.e("log_tagggggg", "!!!!" + result + "ddd");
		
		
		valueList.add(new ChatItem("Mike", "Programming Contest on weekend!", "3 hours ago", "200m") );
		valueList.add(new ChatItem("Tom","I will be there!", "2 hours ago", "10m") );
		valueList.add(new ChatItem("Doris" ,"Looking forward!~", "2 hours ago", "15m") );
		valueList.add(new ChatItem("Will", "Nice:-", "2 hours ago", "50m") );
		valueList.add(new ChatItem("Anthony", "How much is the ticket?", "1 hour ago", "400m") );
		valueList.add(new ChatItem("Me", "On what platform?", "1 hour ago", "0m") );
		valueList.add(new ChatItem("William van der Kamp", result, " just now", "200m") );//for database debug!!!!
		
	    setContentView(R.layout.chat_screen);

	    final ListView listView = (ListView) findViewById(R.id.listview);

	    adapter = new MySimpleArrayAdapter(this, valueList);
	    listView.setAdapter(adapter);
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
			if(valueList.get(valueList.size() - 1).name.equals("Me"))
			{
				valueList.get(valueList.size() - 1).messages.add(message);
				valueList.get(valueList.size() - 1).time = "just now";
				valueList.get(valueList.size() - 1).distance = "0m";
			}
			else
			{
				valueList.add(new ChatItem ("Me", message, "just now", "0m") );
			}
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
			if(values.get(position).name.equals("Me"))
			{
				itemView = inflater.inflate(R.layout.chat_item_me, parent, false);
				LinearLayout bubbleList = (LinearLayout) itemView.findViewById(R.id.chat_bubble_list);
				for(int i=0; i<values.get(position).messages.size(); i++)
				{
					View bubbleView = inflater.inflate(R.layout.chat_bubble_me, parent, false);
					TextView textViewMessage = (TextView) bubbleView.findViewById(R.id.textViewMessage);
					textViewMessage.setText(values.get(position).messages.get(i));
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
					textViewMessage.setText(values.get(position).messages.get(i));
					bubbleList.addView(bubbleView);
				}
			}
			
			TextView textViewName = (TextView) itemView.findViewById(R.id.textViewName);	
			textViewName.setText(values.get(position).name);
			
			TextView textViewTimeLocation = (TextView) itemView.findViewById(R.id.timeAndLocation);
			textViewTimeLocation.setText(values.get(position).time + ", " + values.get(position).distance);
				
			return itemView;
		}
		
	} 

} 