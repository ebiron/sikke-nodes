/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sikke.cli.helpers._System;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author selim
 */
public class EchoHeaderHandler implements HttpHandler {

	public EchoHeaderHandler() {
	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		// EchoHeaderhandler ile istek alýnmadýðýndan kapalýdýr.
		String hostAddress = he.getRemoteAddress().getAddress().getHostAddress();
		List<String> requestIPs = _System.getConfig("rpcallowip");
		if (!requestIPs.contains(hostAddress) || true) {
			he.close();
			return;
		}
	}

}
