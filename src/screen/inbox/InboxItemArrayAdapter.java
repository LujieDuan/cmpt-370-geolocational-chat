package screen.inbox;

import java.util.ArrayList;

import coderunners.geolocationalchat.R;
import data.inbox.ChatSummaryForInbox;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InboxItemArrayAdapter extends ArrayAdapter<ChatSummaryForInbox> {
  
  private final Context context;
  private ArrayList<ChatSummaryForInbox> chatSummaries;

  public InboxItemArrayAdapter(Context context, ArrayList<ChatSummaryForInbox> chatSummaries) {
    super(context, R.layout.inbox_item, chatSummaries);
    this.context = context;
    this.chatSummaries = chatSummaries;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    View rowView = inflater.inflate(R.layout.inbox_item, parent, false);
    
    ChatSummaryForInbox chatSummary = chatSummaries.get(position);
    
    TextView textViewName = (TextView) rowView.findViewById(R.id.inbox_name);
    TextView textViewPost = (TextView) rowView.findViewById(R.id.inbox_post);
    TextView textViewTime = (TextView) rowView.findViewById(R.id.inbox_time);
    TextView textViewReplies = (TextView) rowView.findViewById(R.id.inbox_replies);
    TextView textViewDistance = (TextView) rowView.findViewById(R.id.inbox_distance);
    
    Location location = new Location("");
    
    textViewName.setText(chatSummary.creatorUserName);
    textViewPost.setText(chatSummary.title);
    textViewTime.setText(chatSummary.latestMessageTime.toString());
    textViewReplies.setText(chatSummary.numMessages + "replies");
    textViewDistance.setText(chatSummary.getDistanceString(location));
    
    return rowView;
  }
} 