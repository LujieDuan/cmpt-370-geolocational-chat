package comm;

import java.lang.reflect.Type;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class LatLngDeserializer implements JsonDeserializer<LatLng> 
{
	@Override
    public LatLng deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context)
        throws JsonParseException {
		Log.d("dbConnect", "reached LatLndDeserializer");
		
        final JsonObject obj = json.getAsJsonObject();
        final JsonElement lonElement = obj.get("longitude");
        final JsonElement latElement = obj.get("latitude");
        
        return new LatLng(latElement.getAsDouble(), lonElement.getAsDouble());
    }
}
