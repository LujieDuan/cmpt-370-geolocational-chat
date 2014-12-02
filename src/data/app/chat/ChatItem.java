package data.app.chat;

import java.util.ArrayList;
import org.joda.time.DateTime;

/**
 * This object represents a set of {@link ChatMessageForScreen ChatMessages}
 * that have been sent by a single from a similar time and location. This allows
 * messages to be grouped together when displayed.
 */
public class ChatItem {

	private ArrayList<ChatMessageForScreen> messages = new ArrayList<ChatMessageForScreen>();

	/**
	 * Constructs a new {@link ChatItem} from the given chat messages.
	 * 
	 * @param chatMessages
	 *            A series of messages which belong to a single user.
	 */
	public ChatItem(ChatMessageForScreen... chatMessages) {
		for (ChatMessageForScreen message : chatMessages) {
			addMessage(message);
		}
	}

	/**
	 * Returns the number of messages contained within this chat item.
	 */
	public int numMessages() {
		return messages.size();
	}

	/**
	 * Returns the ith {@link ChatMessageForScreen} in the chat item
	 * 
	 * @param i
	 *            index of the chat item to be returned.
	 */
	public ChatMessageForScreen getChatMessageForScreen(int i) {
		return messages.get(i);
	}

	/**
	 * Returns the name of the user whose messages are contained within this
	 * chat item.
	 */
	public String getName() {
		return messages.get(0).getName();
	}

	/**
	 * Returns the phone ID of the user whose messages are contained within this
	 * chat item.
	 */
	public String getUserId() {
		return messages.get(0).getUserId();
	}

	/**
	 * Returns the ith message in the chat item
	 * 
	 * @param i
	 *            index of the chat item to be returned.
	 */
	public String getMessage(int i) {
		return messages.get(i).getMessage();
	}

	/**
	 * Returns the time at which the most recent message was sent.
	 */
	public DateTime getTime() {
		return messages.get(messages.size() - 1).getTime();
	}
	
	/**
	 * Returns true if the given chat message can be added to the chat item,
	 * meaning that the chat item is either empty or has a matching user id.
	 * Otherwise, false is returned.
	 * 
	 * @param chatMessage
	 *            Chat message to check
	 */
	public boolean isAddable(ChatMessageForScreen chatMessage) {
		return messages.isEmpty()
				|| messages.get(0).getUserId().equals(chatMessage.getUserId());
	}

	/**
	 * Adds the given message to the chat item
	 * 
	 * @param chatMessage
	 *            chat message to be added
	 */
	public void addMessage(ChatMessageForScreen chatMessage) {
		if (!isAddable(chatMessage)) {
			throw new IllegalArgumentException();
		} else {
			messages.add(chatMessage);
		}
	}
}
