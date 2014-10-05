package chatScreen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import coderunners.geolocationalchat.R;

public class ChatScreen extends Activity
{
	ArrayList<String[]> valueList = new ArrayList<String[]>();	  
	  
	MySimpleArrayAdapter adapter;
	  
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		valueList.add(new String[] { "Mike", "Programming Contest on weekend!", "3 hours ago, 200m"} );
		valueList.add(new String[] {"Tom","I will be there!", "2 hours ago, 10m"} );
		valueList.add(new String[] {"Doris" ,"Looking forward!~", "2 hours ago, 15m"} );
		valueList.add(new String[] {"Will", "Nice:-", "2 hours ago, 50m"} );
		valueList.add(new String[] {"Anthony", "How much is the ticket?", "1 hours ago, 400m"} );
		valueList.add(new String[] {"Me", "On what platform?", "1 hour ago, 0m"} );
		valueList.add(new String[] {"Bell", "Windows I think", " just now, 200m"} );
		
	    setContentView(R.layout.chat_screen);

	    final ListView listView = (ListView) findViewById(R.id.listview);

	    adapter = new MySimpleArrayAdapter(this, valueList);
	    listView.setAdapter(adapter);
	  }
	
	public void sendMessage(View v)
	{
		EditText editText = (EditText) findViewById(R.id.EditText);
		String message = editText.getText().toString().trim();
		if(!message.equals(""))
		{
			valueList.add(new String[] {"Me", message, "just now, 0m"} );
			adapter.notifyDataSetChanged();
		}
		editText.setText("");
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<String[]> {
		  
		private final Context context;
		private final ArrayList<String[]> values;

		public MySimpleArrayAdapter(Context context, ArrayList<String[]> values) {
			super(context, R.layout.chat_bubble, values);
			this.context = context;
		    this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
			View rowView = inflater.inflate(R.layout.chat_bubble, parent, false);
			
		    if(values.get(position)[0] == "Me"){
			    TextView textViewMessage = (TextView) rowView.findViewById(R.id.textViewMessage);
			    TextView textViewTimeLocation = (TextView) rowView.findViewById(R.id.timeAndLocation);
			    TextView textViewName = (TextView) rowView.findViewById(R.id.textViewMe);
		    	textViewMessage.setText("      " + values.get(position)[1]);
		    	textViewTimeLocation.setText(values.get(position)[2]);
		    	textViewName.setText(values.get(position)[0]);
		    	textViewMessage.setBackgroundResource(R.drawable.chat_bubble_invert);
		    }else{
			    TextView textViewMessage = (TextView) rowView.findViewById(R.id.textViewMessage);
			    TextView textViewTimeLocation = (TextView) rowView.findViewById(R.id.timeAndLocation);
			    TextView textViewName = (TextView) rowView.findViewById(R.id.textViewName);
		    	textViewMessage.setText("      " + values.get(position)[1]);
		    	textViewTimeLocation.setText(values.get(position)[2]);
		    	textViewName.setText(values.get(position)[0]);
		    }
		    
		

		    return rowView;
		  }
		} 

	} 