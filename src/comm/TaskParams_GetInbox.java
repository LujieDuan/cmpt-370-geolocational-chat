package comm;

import org.apache.http.params.BasicHttpParams;

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
	public BasicHttpParams getHttpParamsForm()
	{
		BasicHttpParams params = new BasicHttpParams();
		
		params.setDoubleParameter("curPhoneLat", curPhoneLocation.getLatitude());
		params.setDoubleParameter("curPhoneLong", curPhoneLocation.getLongitude());
		params.setParameter("tags", tags);
		
		return params;
	}
	
	
}
