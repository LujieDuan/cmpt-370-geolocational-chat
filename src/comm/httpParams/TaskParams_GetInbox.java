package comm.httpParams;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.model.LatLng;

import data.app.global.GlobalSettings;

/**
 * Encapsulates the parameters needed for the http request to get inbox.
 * 
 * @author wsv759
 *
 */
public class TaskParams_GetInbox extends HttpGetParams {
	/**
	 * the current location of the phone, as calculated when this constructor is
	 * called.
	 */
	public LatLng curPhoneLocation;

	/**
	 * string array. This will filter the chats which are received by the
	 * database, so that each received chat includes at least one of these tags.
	 */
	public ArrayList<String> tags;

	/**
	 * Make a new GetDataTaskParams object, with specified l.
	 * 
	 * @param curPhoneLocation
	 *            the current location of the phone, as calculated when this
	 *            constructor is called.
	 * @param tags
	 *            string array. This will filter the chats which are received by
	 *            the database, so that each received chat includes at least one
	 *            of these tags.
	 */
	public TaskParams_GetInbox(LatLng curPhoneLocation, ArrayList<String> tags) {
		this.curPhoneLocation = curPhoneLocation;
		this.tags = tags;
	}

	@Override
	public String getHttpStringForm() {
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

		params.add(new BasicNameValuePair("latitude", Double
				.toString(curPhoneLocation.latitude)));
		params.add(new BasicNameValuePair("longitude", Double
				.toString(curPhoneLocation.longitude)));

		if (GlobalSettings.tagFilteringIsOn && tags.size() > 0) {
			for (String tag : tags)
				params.add(new BasicNameValuePair("tags[]", tag));

		} else {
			params.add(new BasicNameValuePair("tags[]", ""));
		}

		return URLEncodedUtils.format(params, "utf-8");
	}
}
