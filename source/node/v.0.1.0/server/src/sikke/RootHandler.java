/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sikke.RootHandler;
import sikke.cli.helpers._System;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mumbucoglu
 */
public class RootHandler implements HttpHandler {

	public RootHandler() {
	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		try {

			InetSocketAddress socketAddress = he.getRemoteAddress();
			InetAddress inetAddress = socketAddress.getAddress();
			String hostAddress = inetAddress.getHostAddress();

			List<String> requestIPs = _System.getConfig("rpcallowip");
			if (!requestIPs.contains(hostAddress)) {
				he.close();
				return;
			}
			int port = Integer.parseInt(_System.getConfig("rpcport").get(0));
			String response = "<h1>Server started successfully.\n" + "<br>The server works on port number " + port
					+ "</h1>";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception ex) {
			//System.out.println("An error occured : " + ex.getMessage());
			Logger.getLogger(RootHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
