package comm.gsonHelper;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import comm.HttpRequest;

/**
 * Helps gson to deserialize the incoming json DateTime data from the database.
 * 
 * @author wsv759
 *
 */
public class DateTimeDeserializer implements JsonDeserializer<DateTime> {
	@Override
	public DateTime deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern(HttpRequest.DATETIME_FORMAT);
		return formatter.parseDateTime(json.getAsString());
	}
}
