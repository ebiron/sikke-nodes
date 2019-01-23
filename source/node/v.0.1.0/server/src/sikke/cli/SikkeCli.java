/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.net.httpserver.HttpServer;

import sikke.cli.defs.User;
import sikke.cli.helpers.Helpers;
import sikke.cli.helpers.SikkeConstant;
import sikke.cli.helpers._System;

public class SikkeCli {

	public static Helpers helper = new Helpers();
	public static _System system = new _System();

	public static void main(String[] args) throws IOException, Exception, FileNotFoundException {
		Thread.currentThread().setName("Sikke Main Thread");
		List<User> userList = new ArrayList<>();
		system.initApp();
		String error = "";

		try {
			system.getActiveUsers(null, userList);
			if (userList.size() == 1) {
				_System.shouldThreadContinueToWork = true;
			} else {
				_System.shouldThreadContinueToWork = false;
			}
			String strPort = _System.getConfig("rpcport").get(0);
			int port = Integer.parseInt(strPort) > 999 ? Integer.parseInt(strPort) : -1;

			String sikkeServer = _System.getConfig("server").get(0);
			String rpcUser = _System.getConfig("rpcuser").get(0);
			String rpcPw = _System.getConfig("rpcpassword").get(0);

			error = sikkeServer.equals("-") ? "server required!\n" : "";
			error += rpcUser.equals("-") ? "rpcuser required!\n" : "";
			error += rpcPw.equals("-") ? "rpcpassword required!\n" : "";
			error += port < 1000 ? "rpcpassword required!\n" : "";

			if (error.isEmpty()) {
				HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
				System.out.println("> Sikke Node Server started at port number: " + port);
				server.createContext("/serverStatus", new RootHandler());
				server.createContext("/newTransaction", new EchoTransactionHandler());
				server.createContext("/", new EchoPostHandler());
				server.setExecutor(null);
				server.start();
				initSystemTray();
			} else {
				System.err.println(error + "Please check your conf file > " + system.getPath() + "sikke.conf");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Please try again.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		final Timer timer = new Timer("Sikke Timer Task");
		long delay = SikkeConstant.THREAD_DELAY;
		long intervalPeriod = SikkeConstant.INTERVAL_PERIOD;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (_System.shouldThreadContinueToWork) {
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

	private static void initSystemTray() {
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {

		if (SystemTray.isSupported()) {
			final PopupMenu popup = new PopupMenu();
			final TrayIcon trayIcon = new TrayIcon(createImage("/sikke24.gif", "Sikke Node "));
			trayIcon.setImageAutoSize(true);
			final SystemTray tray = SystemTray.getSystemTray();
			final int port = Integer.parseInt(_System.getConfig("rpcport").get(0));

			// Create a popup menu components
			MenuItem aboutItem = new MenuItem("About");
			Menu displayMenu = new Menu("Display");
			MenuItem infoItem = new MenuItem("Info");
			MenuItem noneItem = new MenuItem("None");
			MenuItem exitItem = new MenuItem("Exit Sikke Node Server");

			// Add components to popup menu
			popup.add(aboutItem);
			popup.addSeparator();
			popup.add(displayMenu);
			displayMenu.add(infoItem);
			displayMenu.add(noneItem);
			popup.add(exitItem);

			trayIcon.setPopupMenu(popup);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.out.println("Sikke Node Icon could not be added.");
				return;
			}

			trayIcon.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(null,
							"Server started successfully. The server works on port number:" + port);
				}
			});

			aboutItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(null,
							"Server started successfully. The server works on port number:" + port);
				}
			});

			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MenuItem item = (MenuItem) e.getSource();

					System.out.println(item.getLabel());
					if ("Error".equals(item.getLabel())) {

						trayIcon.displayMessage("Sikke Node Server", "This is an error message",
								TrayIcon.MessageType.ERROR);

					} else if ("Warning".equals(item.getLabel())) {

						trayIcon.displayMessage("Sikke Node Server", "This is a warning message",
								TrayIcon.MessageType.WARNING);

					} else if ("Info".equals(item.getLabel())) {

						trayIcon.displayMessage("Sikke Node Server", "This is an info message",
								TrayIcon.MessageType.INFO);

					} else if ("None".equals(item.getLabel())) {

						trayIcon.displayMessage("Sikke Node Server", "This is an ordinary message",
								TrayIcon.MessageType.NONE);
					}
				}
			};
			trayIcon.displayMessage("Sikke Node Server", "Sikke Node Server started successfully on port : " + port,
					TrayIcon.MessageType.INFO);

			infoItem.addActionListener(listener);
			noneItem.addActionListener(listener);
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tray.remove(trayIcon);
					System.exit(0);
				}
			});
		}
	}

	protected static Image createImage(String path, String description) {
		URL imageURL = SikkeCli.class.getResource(path);
		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}
}