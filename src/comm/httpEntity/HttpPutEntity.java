package comm.httpEntity;

import org.apache.http.entity.StringEntity;

/**
 * Any data structures to send to the database via a put request must implement
 * this.
 * 
 * @author wsv759
 *
 */
public interface HttpPutEntity {
	public StringEntity asJsonStringEntity();
}
