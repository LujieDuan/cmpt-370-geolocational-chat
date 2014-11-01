package data.chat;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

public class ChatMessageFromDb extends ChatMessage 
{
	public int messageId;
	public String creatorUserName;
	public DateTime time;
	
	public ChatMessageFromDb(String message, String userId,
			String creatorUserName, int messageId,DateTime time) {
		super(message, userId);
		
		this.messageId = messageId;
		this.creatorUserName = creatorUserName;
		this.time = time;
	}
	
	  public String getTimeString(DateTime currTime)
	  {    
	    int years = Years.yearsBetween(time, currTime).getYears();  
	    int months = Months.monthsBetween(time, currTime).getMonths(); 
	    int days = Days.daysBetween(time, currTime).getDays(); 
	    int hours = Hours.hoursBetween(time, currTime).getHours(); 
	    int minutes = Minutes.minutesBetween(time, currTime).getMinutes(); 
	    
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
	  
}
