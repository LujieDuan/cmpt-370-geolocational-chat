package data.app.chat;

import java.util.ArrayList;

/**
 * This object represents a collection of messages which together represent a
 * chat which can be both viewed and replied to within a {@link ChatActivity}.
 */
public class Chat {

	private ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();

	/**
	 * Adds the given messages to the {@link Chat}.
	 */
	public void addMessages(ChatMessageForScreen... chatMessages) {
		for (ChatMessageForScreen message : chatMessages) {
			if (!chatItems.isEmpty()
					&& chatItems.get(chatItems.size() - 1).isAddable(message)) {
				chatItems.get(chatItems.size() - 1).addMessage(message);
			} else {
				chatItems.add(new ChatItem(message));
			}
		}
	}

	/**
	 * Returns an array list of chat items, used by {@link ChatActivity} to
	 * format how messages are displayed.
	 */
	public ArrayList<ChatItem> getChatItems() {
		return chatItems;
	}

	/**
	 * Returns the number of messages within the chat
	 */
	public int numMessages() {
		int numMessages = 0;
		for (int i = 0; i < chatItems.size(); i++) {
			numMessages += chatItems.get(i).numMessages();
		}
		return numMessages;
	}

	/**
	 * Returns the ith {@link ChatMessageForScreen} within the chat
	 * 
	 * @param i
	 *            Index number of the message to be returned
	 */
	public ChatMessageForScreen getChatMessage(int i) {
		int j = 0;
		int k = 0;

		while (j + chatItems.get(k).numMessages() <= i) {
			j += chatItems.get(k).numMessages();
			k += 1;
		}

		return chatItems.get(k).getChatMessageForScreen(i - j);
	}
}
