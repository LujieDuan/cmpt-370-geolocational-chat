package comm.httpParams;

/**
 * any TaskParams_* classes used as params for an http get request must extend
 * this.
 * 
 * @author wsv759
 *
 */
public abstract class HttpGetParams {
	public abstract String getHttpStringForm();
}
