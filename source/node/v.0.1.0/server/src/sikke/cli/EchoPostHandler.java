/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 *
 * @author mumbucoglu
 */
public class EchoPostHandler implements HttpHandler {
	JSONRPC jsonrpc = new JSONRPC();

	public EchoPostHandler() {

	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		try {
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();

			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			if(query != null) {
				// parseQuery(query, parameters);
				// System.out.println("query : " + query);
				Headers requestHeaders = he.getRequestHeaders();

				if (requestHeaders.get("Authorization") != null) {
					System.out.println("var");
					String Auth = requestHeaders.get("Authorization").toString();
				} else {
					// System.out.println("yok");
				}

				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				rpcObj rpc = gson.fromJson(query, rpcObj.class);
				// System.out.println(query);
				// System.out.println(rpc.id);

				String response = "";
				for (String key : parameters.keySet()) {
					response += key + " = " + parameters.get(key) + "\n";
				}
				he.sendResponseHeaders(200, response.length());
				OutputStream os = he.getResponseBody();

				rpcObj a = new rpcObj();
				a.id = rpc.id;
				a.jsonrpc = "2.0";
				a.method = rpc.method;

				a.result = jsonrpc.Methods(rpc.method, rpc.params);
				//System.err.println(gson.toJson(a));
				String c = gson.toJson(a);

				os.write(c.getBytes());
				os.close();
			}			
		} catch (Exception ex) {
			Logger.getLogger(EchoPostHandler.class.getName()).log(Level.SEVERE, null, ex);
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

class rpcObj {
	String id;
	String jsonrpc;
	String method;
	JsonArray result;

	String[] params;
}
