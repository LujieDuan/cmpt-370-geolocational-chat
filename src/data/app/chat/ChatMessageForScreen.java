package data.app.chat;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import comm.HttpRequest;
import data.base.ChatMessage;

/**
 * Chat message, including all data needed for the chat screen to display the
 * message.
 * 
 * @author wsv759
 *
 */
public class ChatMessageForScreen extends ChatMessage {
	
    private int messageId;
	
    private String userName;
	
	private DateTime time;

	/**
	 * Creates a new {@link ChatMessageForScreen}, taking in a string as
	 * {@link DateTime} in {@link HttpRequest#DATETIME_FORMAT} format.
	 * 
	 * @param message
	 *            The associated message
	 * @param userId
	 *            The phone ID of the user which posted the message
	 * @param userName
	 *            The name of the user
	 * @param messageId
	 *            The ID of the message
	 * @param time
	 *            The time at which it was created
	 */
	public ChatMessageForScreen(String message, String userId, String userName,
			int messageId, String time) {
		super(message, userId);

		this.messageId = messageId;
		this.userName = userName;
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern(HttpRequest.DATETIME_FORMAT);
		this.time = formatter.parseDateTime(time);
	}

	/**
	 * Creates a new {@link ChatMessageForScreen}, taking in a {@link DateTime}
	 * object.
	 * 
	 * @param message
	 *            The associated message
	 * @param userId
	 *            The phone ID of the user which posted the message
	 * @param userName
	 *            The name of the user
	 * @param messageId
	 *            The ID of the message
	 * @param time
	 *            The time at which it was created
	 */
	public ChatMessageForScreen(String message, String userId, String userName,
			int messageId, DateTime time) {
		super(message, userId);

		this.messageId = messageId;
		this.userName = userName;
		this.time = time;
	}

	/**
	 * Returns the a string containing the relative time at which the message
	 * was posted.
	 * 
	 * @param currTime
	 *            Current time
	 */
	public String getTimeString(DateTime currTime) {
		int years = Years.yearsBetween(time, currTime).getYears();
		int months = Months.monthsBetween(time, currTime).getMonths();
		int days = Days.daysBetween(time, currTime).getDays();
		int hours = Hours.hoursBetween(time, currTime).getHours();
		int minutes = Minutes.minutesBetween(time, currTime).getMinutes();

		String str = "";

		if (years > 0)
			if (years == 1)
				str = years + " year ago";
			else
				str = years + " years ago";
		else if (months > 0)
			if (months == 1)
				str = months + " month ago";
			else
				str = months + " months ago";
		else if (days > 0)
			if (days == 1)
				str = days + " day ago";
			else
				str = days + " days ago";
		else if (hours > 0)
			if (hours > 0)
				str = hours + " hour ago";
			else
				str = hours + " hours ago";
		else if (minutes > 0)
			if (minutes == 1)
				str = minutes + " minute ago";
			else
				str = minutes + " minutes ago";
		else
			str = "just now";

		return str;
	}

	/**
	 * Returns the name of the user which sent this message
	 */
    public String getName() {
      return userName;
    }

    /**
     * Returns the date and time at which this message was sent
     */
    public DateTime getTime() {
      return time;
    }
    
    /**
     * Returns the id of the message
     */
    public int getMessageId() {
      return messageId;
    }

}
