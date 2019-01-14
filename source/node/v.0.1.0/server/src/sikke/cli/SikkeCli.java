/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.sun.net.httpserver.HttpServer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static javax.swing.filechooser.FileSystemView.getFileSystemView;
import sikke.cli.defs.User;
import sikke.cli.helpers.Helpers;
import sikke.cli.helpers.Methods;
import sikke.cli.helpers.SikkeConstant;
import sikke.cli.helpers._System;

import static sikke.cli.helpers._System.helper;
import static sikke.cli.helpers._System.system;

public class SikkeCli {

	public static Helpers helper = new Helpers();
	public static _System system = new _System();

	public static void createNewDatabase(String fileName) {
		String url = "jdbc:sqlite:" + fileName;
		try (Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("The driver name is " + meta.getDriverName());
				System.out.println("A new database has been created.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws IOException, Exception, FileNotFoundException {
		List<User> userList = new ArrayList<>();
		system.initApp();
		String error = "";

		//final EchoPostHandler echoPostHandler = new EchoPostHandler();
		try {
			system.getActiveUsers(null, userList);
			if (userList.size() == 1) {
				system.shouldThreadContinueToWork = true;
			} else {
				system.shouldThreadContinueToWork = false;
			}

			int port = Integer.parseInt(system.getConf("rpcport")) > 999 ? Integer.parseInt(system.getConf("rpcport"))
					: -1;
			String sikkeServer = system.getConf("server");
			String rpcUser = system.getConf("rpcuser");
			String rpcPw = system.getConf("rpcpassword");

			error = sikkeServer.equals("-") ? "server required!\n" : "";
			error += rpcUser.equals("-") ? "rpcuser required!\n" : "";
			error += rpcPw.equals("-") ? "rpcpassword required!\n" : "";
			error += port < 1000 ? "rpcpassword required!\n" : "";

			if (error.isEmpty()) {
				HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
				System.out.println("> Sikke server started at port number: " + port);

				server.createContext("/serverStatus", new RootHandler());
				server.createContext("/newTransaction", new EchoTransactionHandler());
				server.createContext("/", new EchoPostHandler());
				// server.createContext("/echoHeader", new EchoHeaderHandler());
				// server.createContext("/echoGet", new EchoGetHandler());
				// server.createContext("/echoPost", echoPostHandler);

				server.setExecutor(null);
				server.start();
			} else {
				System.err.println(error + "Please check your conf file > " + system.getPath() + "sikke.conf");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Please try again.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		final Timer timer = new Timer();
		long delay = SikkeConstant.THREAD_DELAY;
		long intervalPeriod = SikkeConstant.INTERVAL_PERIOD; // schedules the task to be run in an interval
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (system.shouldThreadContinueToWork) {
					long currentDateTime = System.currentTimeMillis();
					Date currentDate = new Date(currentDateTime);
					DateFormat df = new SimpleDateFormat("dd.MM.yy-HH:mm:ss");

					//System.out.println("> Sikke Timer Thread Invoked, the Time : " + df.format(currentDate));
					try {
						new EchoPostHandler().jsonrpc.methods.syncTx();
					} catch (Exception e) {
						System.out.println("Error on sikke thread : " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		};
		timer.scheduleAtFixedRate(task, delay, intervalPeriod);
	}
}

class test {

	String status;
	String wallet;
}

class Bean {
	String status;
	Body wallet;
}

class Body {
	String address;

}