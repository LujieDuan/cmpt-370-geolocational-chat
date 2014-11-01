package data.chat;

import java.util.ArrayList;
import org.joda.time.DateTime;

/**
 * This object represents a set of {@link ChatMessageFromDb ChatMessages} that have
 * been sent by a single from a similar time and location. This allows messages
 * to be grouped togethor when displayed.
 */
public class ChatItem {

    public ArrayList<ChatMessageFromDb> messages = new ArrayList<ChatMessageFromDb>();
  
    public ChatItem(ChatMessageFromDb... chatMessages)
    {
      for(ChatMessageFromDb message: chatMessages)
      {
        addMessage(message);
      }
    }
    
	public String getName()
	{
	  return messages.get(0).creatorUserName;
	}
	
	public String getUserId()
	{
		return messages.get(0).userId;
	}
	
	public String getMessage(int i)
	{
	  return messages.get(i).message;
	}
	
	public DateTime getTime()
    {
      return messages.get(messages.size() - 1).time;
    }
	
	public String getTimeString(DateTime currTime)
	{
	  return messages.get(messages.size() - 1).getTimeString(currTime);
	}
	
	public boolean isAddable(ChatMessageFromDb chatMessage)
	{
	  return messages.isEmpty() || messages.get(0).userId.equals(chatMessage.userId);
	}
	
	public void addMessage(ChatMessageFromDb chatMessage)
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
