package data.base;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.google.gson.Gson;
import comm.HttpRequest;
import comm.httpEntity.HttpPostEntity;

/**
 * Each user of this app is uniquely identified by their userId string. They can
 * also pick their own non-unique display userName.
 * 
 * These are highly coupled by nature, so they are paired in this class.
 * 
 * @author wsv759
 *
 */
public class UserIdNamePair implements HttpPostEntity {
	
    protected String userId;
	protected String userName;

	/**
	 * Creates a new {@link UserIdNamePair}.
	 * 
	 * @param userId
	 *            The user's phone ID
	 * @param userName
	 *            The user's alias
	 */
	public UserIdNamePair(String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}
	
	public String getUserId()
	{
	  return userId;
	}
	
	public String getUserName()
	{
	  return userName;
	}

	public StringEntity asJsonStringEntity() {
		Gson gson = new Gson();

		String jsonString = gson.toJson(this);

		Log.d("dbConnect", "json string for new username to send: "
				+ jsonString);
		StringEntity se = null;
		try {
			se = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpRequest.setEncodingAndType(se);

		return se;
	}
}
