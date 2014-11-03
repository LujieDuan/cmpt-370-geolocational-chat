package comm;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.chat.ChatId;

public class ChatIdDeserializer implements JsonDeserializer<ChatId> {

	@Override
	public ChatId deserialize(JsonElement json, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		Log.d("dbConnect", "reached chatIdDeserializer");
		JsonObject obj = json.getAsJsonObject();
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(HttpRequest.DATETIME_FORMAT);
		DateTime dt = formatter.parseDateTime(obj.get("timeId").getAsString());
		
		Log.d("dbConnect", "chatId innards: " + obj.get("timeId").getAsString() + obj.get("creatorId").getAsString());
		String creatorId = obj.get("creatorId").getAsString();
		
		return new ChatId(creatorId, dt);
	}

}
