/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sikke.cli.helpers._System;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mumbucoglu
 */
public class RootHandler implements HttpHandler {

	public RootHandler() {
	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		int port = Integer.parseInt(new _System().getConf("rpcport"));
		String response = "<h1>Server started successfully.\n" + "<br>The server works on port number " + port + "</h1>";
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
