package zeus.manager.data;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class ResponseData extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2097421611817424017L;

}
