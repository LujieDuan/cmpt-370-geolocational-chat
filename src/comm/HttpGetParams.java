package comm;

import org.apache.http.params.BasicHttpParams;

public abstract class HttpGetParams {
	public abstract static final String URI;
	public HttpGetParams() 
	{
		
	}
	
	public abstract BasicHttpParams getHttpParamsForm();
}
