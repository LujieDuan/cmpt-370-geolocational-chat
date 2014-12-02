package data.app.chat;

import org.joda.time.DateTime;
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
