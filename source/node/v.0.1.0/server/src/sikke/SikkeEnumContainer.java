package sikke;

public class SikkeEnumContainer {

	public enum HTTPRequestMethod {

		GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

		private String request;

		HTTPRequestMethod(String htpRequestMethod) {
			this.request = htpRequestMethod;
		}

		public String getRequest() {
			return request;
		}
	}

	public enum HTTPErrorCode {

		HTTP_OK(200), HTTP_BAD_REQUEST(400), HTTP_UNAUTHORIZED(401), HTTP_FORBIDDEN(403), HTTP_NOT_FOUND(404),
		HTTP_INTERNAL_SERVER_ERROR(500), HTTP_SERVICE_UNAVAILABLE(503);

		private int code;

		HTTPErrorCode(int httpErrorCode) {
			this.code = httpErrorCode;
		}

		public int getCode() {
			return code;
		}
	}

	public enum RPCErrorCode {
		RPC_INVALID_REQUEST(-32600), RPC_METHOD_NOT_FOUND(-32601), RPC_INVALID_PARAMS(-32602),
		RPC_INTERNAL_ERROR(-32603), RPC_PARSE_ERROR(-32700);

		private int code;

		RPCErrorCode(int rpcErrorCode) {
			this.code = rpcErrorCode;
		}

		public int getCode() {
			return code;
		}
	}
}
