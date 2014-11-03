package data.chat;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ChatMessagesForScreen implements Serializable
{
	private static final long serialVersionUID = 1L;
	 
    public ChatMessageForScreen[] messages;
}
