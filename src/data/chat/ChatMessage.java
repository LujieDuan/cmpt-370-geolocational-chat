package data.chat;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

import android.location.Location;

public class ChatMessage {
  public String name;
  public String id;
  public String message;
  public Location location;
  public DateTime time;
  
  public ChatMessage(String name, String id, String message, Location location, DateTime time)
  {
    this.name = name;
    this.id = id;
    this.message = message;
    this.location = location;
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
  
  public String getDistanceString(Location currLocation)
  {    
    float distance = currLocation.distanceTo(location);
    return distance + "m away";
  }
}
