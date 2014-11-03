package comm;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.location.Location;

public class TaskParams_GetInbox extends HttpGetParams
{
	/** the current location of the phone, as calculated when this constructor is called.*/
	public Location curPhoneLocation;
	
	/** string array. This will filter the chats which are received by the database,
	 * so that each received chat includes at least one of these tags. */
	public String[] tags;
	
	/**
	 * Make a new GetDataTaskParams object, with specified l.
	 * @param curPhoneLocation the current location of the phone, as calculated when this constructor is called.
	 * @param tags string array. This will filter the chats which are received by the database,
	 * so that each received chat includes at least one of these tags.
	 */
	public TaskParams_GetInbox(Location curPhoneLocation, String[] tags)
	{
		this.curPhoneLocation = curPhoneLocation;
		this.tags = tags;
	}

	@Override
	public String getHttpStringForm()
	{
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

	    params.add(new BasicNameValuePair("curPhoneLat", Double.toString(curPhoneLocation.getLatitude())));
	    params.add(new BasicNameValuePair("curPhoneLong", Double.toString(curPhoneLocation.getLongitude())));
		params.add(new BasicNameValuePair("tags", tags.toString()));
	
		return URLEncodedUtils.format(params, "utf-8");
	}
}
