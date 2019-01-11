/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sikke.cli.helpers._System.system;


public class EchoTransactionHandler implements HttpHandler {

	public EchoTransactionHandler() {
	}

	private Connection connect() {
		// SQLite connection string
		String url = system.getDB();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		try {
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();

			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);
			Headers requestHeaders = he.getRequestHeaders();

			System.out.println(query);

			String response = "";
			String _id = null, prev_hash = null, nonce = null, action_time = null, completion_time = null, from = null,
					to = null, asset = null, hash = null, desc = null, amount = null, fee = null;
			int seq = 0;

			for (String key : parameters.keySet()) {
				if (key.equals("_id")) {
					_id = parameters.get(key).toString();
				}
				if (key.equals("amount")) {
					amount = parameters.get(key).toString();
				}
				if (key.equals("prev_hash")) {
					prev_hash = parameters.get(key).toString();
				}
				if (key.equals("nonce")) {
					nonce = parameters.get(key).toString();
				}
				if (key.equals("action_time")) {
					action_time = parameters.get(key).toString();
				}
				if (key.equals("completion_time")) {
					completion_time = parameters.get(key).toString();
				}
				if (key.equals("from")) {
					from = parameters.get(key).toString();
				}
				if (key.equals("to")) {
					to = parameters.get(key).toString();
				}
				if (key.equals("asset")) {
					asset = parameters.get(key).toString();
				}
				if (key.equals("hash")) {
					hash = parameters.get(key).toString();
				}
				if (key.equals("desc")) {
					desc = parameters.get(key).toString();
				}
				if (key.equals("fee")) {
					fee = parameters.get(key).toString();
				}
				if (key.equals("seq")) {
					seq = Integer.parseInt((String) parameters.get(key));
				}
				response += key + " = " + parameters.get(key) + "\n";
			}

			String sql = "INSERT INTO tx (_id, amount,fee,prev_hash,nonce,action_time,completion_time,_from,_to,asset,hash,seq,desc)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, _id);
				pstmt.setString(2, amount);
				pstmt.setString(3, fee);
				pstmt.setString(4, prev_hash);
				pstmt.setString(5, nonce);
				pstmt.setString(6, action_time);
				pstmt.setString(7, completion_time);
				pstmt.setString(8, from);
				pstmt.setString(9, to);
				pstmt.setString(10, asset);
				pstmt.setString(11, hash);
				pstmt.setInt(12, seq);
				pstmt.setString(13, desc);
				pstmt.executeUpdate();
			} catch (SQLException e) {

			}

			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();

			os.write("ok".getBytes());
			os.close();
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
