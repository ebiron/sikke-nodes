/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sikke.cli.helpers._System;

/**
 *
 * @author mumbucoglu
 */
public class EchoPostHandler implements HttpHandler {
	JsonRpc jsonrpc = new JsonRpc();
	static Logger logger = Logger.getLogger(EchoPostHandler.class.getName());
	JsonRpcObject jsonRpcObjectIncoming = null;
	JsonRpcObject jsonRpcObjectOutgoing = null;

	public EchoPostHandler() {

	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		OutputStream os = null;
		try {
			Map<String, Object> parameters = new HashMap();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = "";
			String line;

			String hostAddress = he.getRemoteAddress().getAddress().getHostAddress();
			List<String> requestIPs = _System.getConfig("rpcallowip");
			// TODO aþaðýdaki false yi kaldýr.
			if (!requestIPs.contains(hostAddress) && false) {
				he.close();
				return;
			}
			while ((line = br.readLine()) != null) {
				query += line;
			}
			Headers requestHeaders = he.getRequestHeaders();
			String requestMethod = he.getRequestMethod();
			String path = he.getRequestURI().getPath();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			if (!path.equals("/")) {
				Error error = new Error();
				error.error_code = SikkeEnumContainer.HTTPErrorCode.HTTP_NOT_FOUND.getCode();
				error.message = "Only base URL requests are allowed";
				error.status = "error";
				String c = gson.toJson(error);
				os = he.getResponseBody();
				he.sendResponseHeaders(SikkeEnumContainer.HTTPErrorCode.HTTP_NOT_FOUND.getCode(), c.length());
				os.write(c.getBytes());
				os.close();
				return;
			}
			if (!requestMethod.equals(SikkeEnumContainer.HTTPRequestMethod.POST.getRequest())) {
				Error error = new Error();
				error.error_code = SikkeEnumContainer.HTTPErrorCode.HTTP_NOT_FOUND.getCode();
				error.message = "Only POST requests are allowed via base URL";
				error.status = "error";
				String c = gson.toJson(error);
				os = he.getResponseBody();
				he.sendResponseHeaders(SikkeEnumContainer.HTTPErrorCode.HTTP_NOT_FOUND.getCode(), c.length());
				os.write(c.getBytes());
				os.close();
				return;
			}
			if (query == null || query.isEmpty()) {
				Error error = new Error();
				error.error_code = SikkeEnumContainer.HTTPErrorCode.HTTP_NOT_FOUND.getCode();
				error.message = "Only base URL requests are allowed with request body";
				error.status = "error";
				String c = gson.toJson(error);
				os = he.getResponseBody();
				he.sendResponseHeaders(SikkeEnumContainer.HTTPErrorCode.HTTP_NOT_FOUND.getCode(), c.length());
				os.write(c.getBytes());
				os.close();
				return;
			}
			jsonRpcObjectIncoming = (JsonRpcObject) gson.fromJson(query, JsonRpcObject.class);
			String response = "";
			for (String key : parameters.keySet()) {
				response = response + key + " = " + parameters.get(key) + "\n";
			}
			he.sendResponseHeaders(SikkeEnumContainer.HTTPErrorCode.HTTP_OK.getCode(), response.length());
			os = he.getResponseBody();

			List<String> list = new ArrayList(Arrays.asList(jsonRpcObjectIncoming.params));
			list.removeAll(Arrays.asList(new String[] { "", null }));

			JsonRpcObject jsonRpcObjectOutgoing = this.jsonrpc.Methods(jsonRpcObjectIncoming.method,
					(String[]) list.toArray(new String[0]));

			jsonRpcObjectOutgoing.id = jsonRpcObjectIncoming.id;
			jsonRpcObjectOutgoing.method = jsonRpcObjectIncoming.method;		

			String c = gson.toJson(jsonRpcObjectOutgoing);
			os.write(c.getBytes());
			os.close();

		} catch (Exception ex) {

			logger.fatal(Level.FATAL, ex);
		}
	}

	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}

class Error {
	String status;
	String message;
	int error_code;
}
