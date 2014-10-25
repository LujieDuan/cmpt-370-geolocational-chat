package data.chat;

import org.joda.time.DateTime;

import android.location.Location;

public class ChatMessageFromDb extends ChatMessage 
{
	public String name;
	public DateTime time;
	
	public ChatMessageFromDb(String name, String id, String message,
			Location location, DateTime time) {
		super(name, id, message, location, time);
		// TODO Auto-generated constructor stub
	}

}
