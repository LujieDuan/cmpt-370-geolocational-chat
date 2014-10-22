package screen.inbox;

import java.util.ArrayList;

import org.joda.time.DateTime;

import coderunners.geolocationalchat.R;
import data.chat.ChatSummary;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InboxItemArrayAdapter extends ArrayAdapter<ChatSummary> {
  
  private final Context context;
  private ArrayList<ChatSummary> chatSummaries;

  public InboxItemArrayAdapter(Context context, ArrayList<ChatSummary> chatSummaries) {
    super(context, R.layout.inbox_item, chatSummaries);
    this.context = context;
    this.chatSummaries = chatSummaries;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    View rowView = inflater.inflate(R.layout.inbox_item, parent, false);
    
    ChatSummary chatSummary = chatSummaries.get(position);
    
    TextView textViewName = (TextView) rowView.findViewById(R.id.inbox_name);
    TextView textViewPost = (TextView) rowView.findViewById(R.id.inbox_post);
    TextView textViewTime = (TextView) rowView.findViewById(R.id.inbox_time);
    TextView textViewReplies = (TextView) rowView.findViewById(R.id.inbox_replies);
    TextView textViewDistance = (TextView) rowView.findViewById(R.id.inbox_distance);
    
    Location location = new Location("");
    
    textViewName.setText(chatSummary.chatMessage.name);
    textViewPost.setText(chatSummary.chatMessage.message);
    textViewTime.setText(chatSummary.chatMessage.getTimeString(new DateTime()));
    textViewReplies.setText(chatSummary.numMessages + "replies");
    textViewDistance.setText(chatSummary.chatMessage.getDistanceString(location));
    
    return rowView;
  }
} 