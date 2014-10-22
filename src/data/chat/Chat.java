package data.chat;

import java.util.ArrayList;

public class Chat {
  
  public ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
  
  public void addMessages(ChatMessage... chatMessages)
  {
    for(ChatMessage message : chatMessages)
    {
      if(!chatItems.isEmpty() && chatItems.get(chatItems.size() - 1).isAddable(message))
      {
        chatItems.get(chatItems.size() - 1).addMessage(message);
      }
      else
      {
        chatItems.add(new ChatItem(message));
      }
    }
  }
}
