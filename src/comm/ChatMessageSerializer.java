package comm;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.chat.ChatMessageToDb;

public class ChatMessageSerializer implements JsonSerializer<ChatMessageToDb> 
{
	@Override
	public JsonElement serialize(ChatMessageToDb c, Type typeOfT,
			JsonSerializationContext context) 
	{
		JsonObject json = new JsonObject();
		
		json.addProperty("creatorId", c.chatId.creatorId);
		json.addProperty("timeId", c.chatId.getTimeIdString());
		json.addProperty("userId", c.userId);
		json.addProperty("message", c.message);
		return json;
	}

}