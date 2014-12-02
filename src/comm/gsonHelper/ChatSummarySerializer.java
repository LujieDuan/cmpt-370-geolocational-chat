package comm.gsonHelper;

import java.lang.reflect.Type;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import comm.HttpRequest;

import data.comm.chatCreation.ChatSummaryToDb;

/**
 * Helps gson to serialize ChatSummary data into json form, before sending to
 * the database.
 * 
 * @author wsv759
 *
 */
public class ChatSummarySerializer implements JsonSerializer<ChatSummaryToDb> {

	@Override
	public JsonElement serialize(ChatSummaryToDb c, Type t,
			JsonSerializationContext context) {
		JsonObject json = new JsonObject();

		json.addProperty("title", c.getTitle());
		json.addProperty("longitude", c.getLocation().longitude);
		json.addProperty("latitude", c.getLocation().latitude);

		JsonArray tagsArray = new JsonArray();
		for (String tag : c.getTags()) {
			tagsArray.add(new JsonPrimitive(tag));
		}

		json.add("tags", tagsArray);
		json.addProperty("userId", c.userId);
		json.addProperty("firstMessage", c.firstMessage);

		DateTimeFormatter formatter = DateTimeFormat
				.forPattern(HttpRequest.DATETIME_FORMAT);
		json.addProperty("maxEndTime", c.maxEndTime.toString(formatter));

		return json;
	}

}
