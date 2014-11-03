package screen.inbox;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import coderunners.geolocationalchat.R;

import com.google.android.gms.maps.model.LatLng;

import data.inbox.ChatSummaryForScreen;

public class InboxItemArrayAdapter extends ArrayAdapter<ChatSummaryForScreen> {
  
  private final Context context;
  private ArrayList<ChatSummaryForScreen> chatSummaries;

  public InboxItemArrayAdapter(Context context, ArrayList<ChatSummaryForScreen> chatSummaries) {
    super(context, R.layout.inbox_item, chatSummaries);
    this.context = context;
    this.chatSummaries = chatSummaries;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    View rowView = inflater.inflate(R.layout.inbox_item, parent, false);
    
    ChatSummaryForScreen chatSummary = chatSummaries.get(position);
    
    TextView textViewName = (TextView) rowView.findViewById(R.id.inbox_name);
    TextView textViewPost = (TextView) rowView.findViewById(R.id.inbox_post);
    TextView textViewTime = (TextView) rowView.findViewById(R.id.inbox_time);
    TextView textViewReplies = (TextView) rowView.findViewById(R.id.inbox_replies);
    TextView textViewDistance = (TextView) rowView.findViewById(R.id.inbox_distance);
    
    LatLng location = new LatLng(InboxActivity.LAT, InboxActivity.LONG);
    
    textViewName.setText(chatSummary.creatorUserName);
    textViewPost.setText(chatSummary.title);
    textViewTime.setText(chatSummary.lastMessageTime.toString());
    textViewReplies.setText(chatSummary.numMessages + "replies");
    textViewDistance.setText(chatSummary.getDistanceString(location));
    
    return rowView;
  }
} 