package sikke;

import com.google.gson.JsonArray;

public class JsonRpcObject {

	public String id;
	public String jsonrpc;
	public String method;
	public JsonArray result;
	public String[] params;
	public JsonRpcErrorObject error;
}
