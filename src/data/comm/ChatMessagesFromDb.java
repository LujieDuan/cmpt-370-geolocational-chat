package data.comm;

import java.io.Serializable;

import data.app.chat.ChatMessageForScreen;

/**
 * Helps gson to deserialize an incoming array of chat messages.
 * @author wsv759
 *
 */
public class ChatMessagesFromDb implements Serializable
{
	private static final long serialVersionUID = 1L;
	 
    public ChatMessageForScreen[] messages;
}
