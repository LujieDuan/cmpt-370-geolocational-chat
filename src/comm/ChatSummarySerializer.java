package comm;

import java.lang.reflect.Type;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.chatCreation.ChatSummaryToDb;

public class ChatSummarySerializer implements JsonSerializer<ChatSummaryToDb> {

	
	@Override
	public JsonElement serialize(ChatSummaryToDb c, Type t,
			JsonSerializationContext context) {
		JsonObject json = new JsonObject();

		json.addProperty("title", c.title);
		json.addProperty("longitude", c.location.longitude);
		json.addProperty("latitude", c.location.latitude);
		
		JsonArray tagsArray = new JsonArray();
		for(int i = 0; i < c.tags.length; i++)
		{
			tagsArray.add(new JsonPrimitive(c.tags[i]));
		}
		
		json.add("tags",tagsArray);
		json.addProperty("userId", c.userId);
		json.addProperty("firstMessage", c.firstMessage);
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(HttpRequest.DATETIME_FORMAT);
		json.addProperty("maxEndTime", c.maxEndTime.toString(formatter));
		
		return json;
	}

}
