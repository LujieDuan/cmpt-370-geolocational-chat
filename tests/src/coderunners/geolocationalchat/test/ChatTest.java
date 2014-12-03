package coderunners.geolocationalchat.test;


import java.util.ArrayList;

import junit.framework.TestCase;

import org.joda.time.DateTime;

import data.app.chat.Chat;
import data.app.chat.ChatMessageForScreen;

public class ChatTest extends TestCase {

  Chat chat = new Chat();
  
  ArrayList<ChatMessageForScreen> messages = new ArrayList<ChatMessageForScreen>();
  
  @Override
  public void setUp()
  {	  
    messages.add(new ChatMessageForScreen("message1", "id1", "user1", 1, new DateTime()));
    
    messages.add(new ChatMessageForScreen("message2", "id2", "user2", 2, new DateTime()));
    
    messages.add(new ChatMessageForScreen("message3", "id1", "user1", 3, new DateTime()));
    messages.add(new ChatMessageForScreen("message4", "id1", "user1", 4, new DateTime()));
    
    messages.add(new ChatMessageForScreen("message5", "id2", "user2", 5, new DateTime()));
    messages.add(new ChatMessageForScreen("message6", "id2", "user2", 6, new DateTime()));
    
    messages.add(new ChatMessageForScreen("message7", "id1", "user1", 7, new DateTime()));
    messages.add(new ChatMessageForScreen("message8", "id1", "user1", 8, new DateTime()));
    messages.add(new ChatMessageForScreen("message9", "id1", "user1", 9, new DateTime()));
    
    messages.add(new ChatMessageForScreen("message10", "id2", "user2", 10, new DateTime()));
    messages.add(new ChatMessageForScreen("message11", "id2", "user2", 11, new DateTime()));
    messages.add(new ChatMessageForScreen("message12", "id2", "user2", 12, new DateTime()));
  }
  
  public void testNumMessages()
  {
	assertEquals(chat.numMessages(), 0);
    for(int i=0; i<messages.size(); i++)
    {
      chat.addMessages(messages.get(i));
      assertEquals(chat.numMessages(), i+1);
    }
  }
  
  public void testGetChatMessage()
  {
    for(int i=0; i<messages.size(); i++)
    {
      chat.addMessages(messages.get(i));
      for(int j=0; j<=i; j++)
      {
        assertTrue(chat.getChatMessage(j) == messages.get(j));
      }
    }
  }
  
  public void testGetChatItems()
  {
	assertEquals(chat.getChatItems().size(), 0);
	  
	chat.addMessages(messages.get(0));
    assertEquals(chat.getChatItems().size(), 1);
    
    chat.addMessages(messages.get(1));
    assertEquals(chat.getChatItems().size(), 2);
    
    chat.addMessages(messages.get(2));
    chat.addMessages(messages.get(3));
    assertEquals(chat.getChatItems().size(), 3);
    
    chat.addMessages(messages.get(4));
    chat.addMessages(messages.get(5));
    assertEquals(chat.getChatItems().size(), 4);
    
    chat.addMessages(messages.get(6));
    chat.addMessages(messages.get(7));
    chat.addMessages(messages.get(8));
    assertEquals(chat.getChatItems().size(), 5);
    
    chat.addMessages(messages.get(9));
    chat.addMessages(messages.get(10));
    chat.addMessages(messages.get(11));
    assertEquals(chat.getChatItems().size(), 6);
  }
  
}
