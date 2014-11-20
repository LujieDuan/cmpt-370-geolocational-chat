package comm;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.google.gson.Gson;

import data.UserIdNamePair;

public class TaskParams_SendNewUserName extends HttpPutEntity 
{
	public UserIdNamePair newUserIdNamePair;
	
	public TaskParams_SendNewUserName(UserIdNamePair newUserIdNamePair) 
	{
		this.newUserIdNamePair = newUserIdNamePair;
	}
	
	@Override
	public StringEntity asJsonStringEntity() 
	{
	    Gson gson = new Gson();

		String jsonString = gson.toJson(newUserIdNamePair);
		
		Log.d("dbConnect", "send string: " + jsonString);
		StringEntity se = null;
		try {
			se = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return se;
	}

}
