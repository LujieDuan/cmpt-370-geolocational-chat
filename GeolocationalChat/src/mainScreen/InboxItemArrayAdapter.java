package mainScreen;

import coderunners.geolocationalchat.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InboxItemArrayAdapter extends ArrayAdapter<String> {
  private final Context context;
  
  private String[] names;
  private final String[] posts;
  private final String[] times;
  private final String[] replies;
  private final String[] distances;

  public InboxItemArrayAdapter(Context context, String[] names, String[] posts, String[] times, String[] replies, String[] distances) {
    super(context, R.layout.inbox_item, names);
    this.context = context;
    this.names = names;
    this.posts = posts;
    this.times = times;
    this.replies = replies;
    this.distances = distances;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    View rowView = inflater.inflate(R.layout.inbox_item, parent, false);
    
    TextView textViewName = (TextView) rowView.findViewById(R.id.inbox_name);
    TextView textViewPost = (TextView) rowView.findViewById(R.id.inbox_post);
    TextView textViewTime = (TextView) rowView.findViewById(R.id.inbox_time);
    TextView textViewReplies = (TextView) rowView.findViewById(R.id.inbox_replies);
    TextView textViewDistance = (TextView) rowView.findViewById(R.id.inbox_distance);
    
    textViewName.setText(names[position]);
    textViewPost.setText(posts[position]);
    textViewTime.setText(times[position]);
    textViewReplies.setText(replies[position]);
    textViewDistance.setText(distances[position]);
    
    return rowView;
  }
} 