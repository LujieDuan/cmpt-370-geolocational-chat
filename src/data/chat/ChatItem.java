package data.chat;

import java.util.ArrayList;
import org.joda.time.DateTime;
import android.location.Location;

/**
 * This object represents a set of {@link ChatMessage ChatMessages} that have
 * been sent by a single from a similar time and location. This allows messages
 * to be grouped togethor when displayed.
 */
public class ChatItem {

    public ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
  
    public ChatItem(ChatMessage... chatMessages)
    {
      for(ChatMessage message: chatMessages)
      {
        addMessage(message);
      }
    }
    
	public String getName()
	{
	  return messages.get(0).name;
	}
	
	public String getId()
	{
	  return messages.get(0).id;
	}
	
	public String getMessage(int i)
	{
	  return messages.get(i).message;
	}
	
	public Location getLocation()
    {
      return messages.get(messages.size() - 1).location;
    }
	
	public String getDistanceString(Location currLocation)
    {
      return messages.get(messages.size() - 1).getDistanceString(currLocation);
    }
	
	public DateTime getTime()
    {
      return messages.get(messages.size() - 1).time;
    }
	
	public String getTimeString(DateTime currTime)
	{
	  return messages.get(messages.size() - 1).getTimeString(currTime);
	}
	
	public boolean isAddable(ChatMessage chatMessage)
	{
	  return messages.isEmpty() || messages.get(0).id.equals(chatMessage.id);
	}
	
	public void addMessage(ChatMessage chatMessage)
	{
	  if(!isAddable(chatMessage))
	  {
	    throw new IllegalArgumentException();
	  }
	  else
	  {
	    messages.add(chatMessage);
	  }
	}
}
