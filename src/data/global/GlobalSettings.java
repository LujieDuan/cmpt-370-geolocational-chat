package data.global;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class GlobalSettings 
{
	public static UserIdNamePair userIdAndName;
	
	public static ArrayList<String> allTags;
	
	public static ArrayList<String> tagsToFilterFor = new ArrayList<String>();
	
	public static LatLng curPhoneLocation = new LatLng(52.1310799, -106.6341388);
}
