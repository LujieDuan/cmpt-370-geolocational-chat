package screen.chat;

import java.util.ArrayList;




import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import coderunners.geolocationalchat.R;
import data.chat.Chat;
import data.chat.ChatItem;
import data.chat.ChatMessage;

import org.joda.time.DateTime;

public class ChatActivity extends Activity
{
  Chat chat = new Chat();

  MySimpleArrayAdapter adapter;

  Handler  hUpdate;
  Runnable rUpdate;

  int scrollState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    hUpdate = new Handler();
    rUpdate = new Runnable(){
      @Override
      public void run() {
        if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
          adapter.notifyDataSetChanged();
      }
    };

    chat.addMessages(
        new ChatMessage("Mike", "Mike's ID", "Programming contest this weekend!", new Location(""), new DateTime()),
        new ChatMessage("Tom", "Tom's ID", "I will be there!", new Location(""), new DateTime()),
        new ChatMessage("Doris", "Doris' ID", "Looking forward!~", new Location(""), new DateTime()),
        new ChatMessage("Will", "Will's ID", "Nice:-", new Location(""), new DateTime()),
        new ChatMessage("Anthony", "Anthony's ID", "How much is the ticket?", new Location(""), new DateTime()),
        new ChatMessage("Me", "My ID", "On what platform?", new Location(""), new DateTime()),
        new ChatMessage("Will", "Will's ID", "Windows I think", new Location(""), new DateTime()));

    setContentView(R.layout.chat_screen);

    final ListView listView = (ListView) findViewById(R.id.listview);



    adapter = new MySimpleArrayAdapter(this, chat.chatItems);
    listView.setAdapter(adapter);

    OnScrollListener scroll = new OnScrollListener()
    {

      @Override
      public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        ChatActivity.this.scrollState = scrollState;
      }

    };

    listView.setOnScrollListener(scroll);

    Thread tUpdate = new Thread() {
      public void run() {
        while(true) {
          hUpdate.post(rUpdate);
          try 
          {
            sleep(5000);
          } 
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    tUpdate.setPriority(Thread.MIN_PRIORITY);
    tUpdate.start();
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
      chat.addMessages(new ChatMessage("Me", "My ID", message, new Location(""), new DateTime()));
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
      if(values.get(position).getId().equals("My ID"))
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

      textViewTimeLocation.setText(values.get(position).getTimeString(new DateTime()) + ", " + values.get(position).getDistanceString(location));

      return itemView;
    }

  } 

} 