package comm.httpEntity;

import org.apache.http.entity.StringEntity;

/**
 * Any data structures to send to the database via a post request must implement
 * this.
 * 
 * @author wsv759
 *
 */
public interface HttpPostEntity {
	public StringEntity asJsonStringEntity();
}
