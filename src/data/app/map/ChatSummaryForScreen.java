package data.app.map;


import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

import com.google.android.gms.maps.model.LatLng;

import data.base.ChatId;
import data.base.ChatSummary;

/**
 * Individual chat, as displayed by the map screen. Only includes information relevant to the map screen.
 * Does NOT include the actual chat contents, as the map screen doesn't need those.
 * @author wsv759
 *
 */
public class ChatSummaryForScreen extends ChatSummary 
{
	public ChatId chatId;
	public String creatorUserName;
	public int numMessages;
	public DateTime lastMessageTime;
	public int numMessagesRead = 0;
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 * @param chatId as assigned by the database.
	 * @param creatorUserName the alias of the one who created the chat
	 */
	public ChatSummaryForScreen(String title, LatLng location, ArrayList<String> tags, 
			ChatId chatId, String creatorUserName, int numMessages, int numMessagesRead, DateTime lastMessageTime) 
	{
		super(title, location, tags);
		
		this.chatId = chatId;
		this.creatorUserName = creatorUserName;
		this.numMessages = numMessages;
		this.numMessagesRead = numMessagesRead;
		this.lastMessageTime = lastMessageTime;
		
	}
	
	/**
	 * Returns the number of messages
	 */
	public int getNumMessages()
	{
	  return numMessages;
	}
	
	/**
	 * Returns the number of messages which have been read
	 */
	public int getNumMessagesRead()
	{
	  return numMessagesRead;
	}
	
	/**
	 * Returns the number of messages which have not been read
	 */
	public int getNumMessagesUnread()
	{
	  return numMessages - numMessagesRead;
	}
	
	/**
	 * Marks all messages as read
	 */
	public void readMessages()
	{
	  numMessagesRead = numMessages;
	}
	
	public String getUserName()
	{
	  return creatorUserName;
	}
	
	public String getTitle()
	{
	  return title;
	}
	
	/**
	 * Returns a string representation which describes how long it has been
	 * since a reply was posted to the chat
	 */
	public String getTimeString()
	{
	  DateTime currTime = new DateTime();
	  
	  int years = Years.yearsBetween(lastMessageTime, currTime).getYears();  
	  int months = Months.monthsBetween(lastMessageTime, currTime).getMonths(); 
	  int days = Days.daysBetween(lastMessageTime, currTime).getDays(); 
	  int hours = Hours.hoursBetween(lastMessageTime, currTime).getHours(); 
	  int minutes = Minutes.minutesBetween(lastMessageTime, currTime).getMinutes(); 

	  String str = "";

	  if(years > 0) 
	    if(years == 1) str = years + " year ago";
	    else str = years + " years ago";
	  else if(months > 0)  
	    if(months == 1) str = months + " month ago";
	    else str = months + " months ago";
	  else if(days > 0) 
	    if(days == 1) str = days + " day ago";
	    else str = days + " days ago";
	  else if(hours > 0) 
	    if(hours > 0) str = hours + " hour ago";
	    else str = hours + " hours ago";
	  else if(minutes > 0) 
	    if(minutes == 1) str = minutes + " minute ago";
	    else str = minutes + " minutes ago";
	  else str = "just now";

	  return str;
	}
	
	/**
	 * Returns a string representation which describes the number of messages
	 * which are within the chat, and the number which have been read
	 */
	public String getNumMessagesString()
	{
	  if(numMessages == 1)
	  {
	    return numMessages + " reply, " + getNumMessagesUnread() + " unread";
	  }
	  else
	  {
	    return numMessages + " replies, " + getNumMessagesUnread() + " unread";
	  }
	}
	
	
}
