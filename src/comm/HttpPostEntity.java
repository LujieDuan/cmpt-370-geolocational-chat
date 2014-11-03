package comm;

import org.apache.http.entity.StringEntity;

public abstract class HttpPostEntity 
{
	public abstract StringEntity asJsonStringEntity();
}
