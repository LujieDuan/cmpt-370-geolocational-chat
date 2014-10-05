package chatScreen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import coderunners.geolocationalchat.R;

public class ChatScreen extends Activity
{
	
	  String[][] values = new String[][] {{ "Mike", "Programming Contest on weekend!", "3 hours ago, 200m"}, {"Tom","I will be there!", "2 hours ago, 10m"},{"Doris" ,"Looking forward!~", "2 hours ago, 15m"},
	    		{"Will", "Nice:-", "2 hours ago, 50m"}, {"Anthony", "How much is the ticket?", "1 hours ago, 400m"},{"Me", "On what platform?", "1 hour ago, 0m"},{"Bell", "Windows I think", " just now, 200m"}};

	    
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.chat_screen);

	    final ListView listview = (ListView) findViewById(R.id.listview);

	    final ArrayList<String> list = new ArrayList<String>();
//	    for (int i = 0; i < values.length; ++i) {
//	      list.add(values[i]);
//	    }
	    final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, values);
	    listview.setAdapter(adapter);

//	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//	      @SuppressLint("NewApi") @Override
//	      public void onItemClick(AdapterView<?> parent, final View view,
//	          int position, long id) {
//	        final String item = (String) parent.getItemAtPosition(position);
//	        view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
//	              @Override
//	              public void run() {
//	                list.remove(item);
//	                adapter.notifyDataSetChanged();
//	                view.setAlpha(1);
//	              }
//	            });
//	      }
//
//	    });
	  }

	public class MySimpleArrayAdapter extends ArrayAdapter<String[]> {
		  private final Context context;
		  private final String[][] values;

		  public MySimpleArrayAdapter(Context context, String[][] values) {
		    super(context, R.layout.chat_bubble, values);
		    this.context = context;
		    this.values = values;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.chat_bubble, parent, false);

		    if(values[position][0] == "Me"){
			    TextView textViewMessage = (TextView) rowView.findViewById(R.id.textViewMessage);
			    TextView textViewTimeLocation = (TextView) rowView.findViewById(R.id.timeAndLocation);
			    TextView textViewName = (TextView) rowView.findViewById(R.id.textViewMe);
		    	textViewMessage.setText("      " + values[position][1]);
		    	textViewTimeLocation.setText(values[position][2]);
		    	textViewName.setText(values[position][0]);
		    	textViewMessage.setBackgroundResource(R.drawable.chat_bubble_invert);
		    }else{
			    TextView textViewMessage = (TextView) rowView.findViewById(R.id.textViewMessage);
			    TextView textViewTimeLocation = (TextView) rowView.findViewById(R.id.timeAndLocation);
			    TextView textViewName = (TextView) rowView.findViewById(R.id.textViewName);
		    	textViewMessage.setText("      " + values[position][1]);
		    	textViewTimeLocation.setText(values[position][2]);
		    	textViewName.setText(values[position][0]);
		    }
		    
		

		    return rowView;
		  }
		} 

	} 