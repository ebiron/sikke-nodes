/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import static javax.swing.filechooser.FileSystemView.getFileSystemView;
import static sikke.cli.SikkeCli.helper;
import sikke.cli.defs.User;
import sikke.cli.wallet.AES256Cipher;
import sikke.cli.wallet.AppHelper;
import sikke.cli.wallet.WalletKey;

/**
 *
 * @author mumbucoglu
 */
public class _System {

	public static Helpers helper = new Helpers();
	public static _System system = new _System();
	static public boolean shouldThreadContinueToWork = true;

	public void initApp() throws FileNotFoundException, UnsupportedEncodingException, Exception {
		initFolder();
		// initUser();
	}

	public String getOS(String property) {
		return System.getProperty(property);
	}

	public Connection connect() {
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

	private void initFolder() throws FileNotFoundException, Exception {
		String os = getOS("os.name");
		if (os == null) {
			throw new IOException("Os Name Not Found");
		}
		os = getOS("os.name").toLowerCase();

		String path = null;
		if ((os.contains("linux")) || (os.contains("unix")) || (os.contains("mac os"))) {
			path = FileSystemView.getFileSystemView().getHomeDirectory().toString() + "/.sikke";
		} else if (os.contains("windows")) {
			path = System.getenv("APPDATA") + "\\Sikke";
		}
		File f = new File(path);
		if ((f == null) || (!f.exists())) {
			File createDir1 = new File(path);
			createDir1.mkdir();
			System.out.println("App folder created at > " + path);
			createDefaultConf();
			createTables();
		}
	}

	public void getActiveUsers(Connection conn, List<User> userList) throws Exception {
		String sql = "select * from system_user where is_user_logged_in = 1";
		User user = null;
		boolean isConnectionNull = false;
		try {
			if (conn == null) {
				isConnectionNull = true;
				conn = this.connect();
			}
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				user = new User();
				user.email = rs.getString("email");
				user.user_id = rs.getString("user_id");
				user.access_token = rs.getString("access_token");
				user.refresh_token = rs.getString("refresh_token");
				user.crypt_key = rs.getString("crypt_key");
				user.crypt_iv = rs.getString("crypt_iv");
				user.encrypted_password = rs.getString("encrypted_password");
				user.token_type = rs.getString("token_type");
				user.rt_expires_in = rs.getInt("rt_expires_in");
				user.is_user_logged_in = rs.getBoolean("is_user_logged_in");
				userList.add(user);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (conn != null) {
				if (isConnectionNull) {
					conn.close();
				}
			}
		}
	}

	protected User initUser() throws Exception {
		Connection conn = null;
		String sql = null;
		List<User> userList = new ArrayList<>();
		User user = null;
		User userFromService = null;
		Gson g = new Gson();
		try {
			conn = this.connect();
			getActiveUsers(conn, userList);
			if (userList.size() == 0) {
				// Login e yönlendir
				System.out.print("Sikke User Email : ");
				String email = new Scanner(System.in).nextLine();
				System.out.print("Password : ");
				String password = new Scanner(System.in).nextLine();
				String response = getAccessToken(email, password);
				user = g.fromJson(response.toString(), User.class);
				if (user.status.equals(SikkeConstant.STATUS_SUCCESS)) {

					byte[] pin_byte = AES256Cipher.getRandomAesCryptKey();
					byte[] iv_byte = AES256Cipher.getRandomAesCryptIv();
					userFromService.crypt_key = AppHelper.toHexString(pin_byte);
					user.crypt_iv = AppHelper.toHexString(iv_byte);
					user.encrypted_password = AES256Cipher.encrypt(pin_byte, iv_byte, password);
					user.is_user_logged_in = true;
					saveOrUpdateUser(conn, user);
				}
			} else if (userList.size() == 1) {
				// Aktif kullanýcý var ama API ye sor
				user = userList.get(0);
				String response = getAccessToken(user.email, user.getPassword());
				userFromService = g.fromJson(response.toString(), User.class);
				if (userFromService.status.equals(SikkeConstant.STATUS_SUCCESS)) {
					user.access_token = userFromService.access_token;
					user.refresh_token = userFromService.refresh_token;
					saveOrUpdateUser(conn, user);
				}
			} else if (userList.size() > 1) {
				// Birden çok aktif kullanýcý var tüm kullanýcýlarýn login durumunu 0 a çek
				// logine yönlendir.
				disableAllUserLoginStatus(conn);
				System.out.print("Sikke User Email : ");
				String email = new Scanner(System.in).nextLine();
				System.out.print("Password : ");
				String password = new Scanner(System.in).nextLine();
				String response = getAccessToken(email, password);
				userFromService = g.fromJson(response.toString(), User.class);

				if (userFromService.status.equals(SikkeConstant.STATUS_SUCCESS)) {
					for (User user2 : userList) {
						if (user2.user_id.equals(userFromService.user_id)) {
							user2.access_token = userFromService.access_token;
							user2.refresh_token = userFromService.refresh_token;
							user = user2;
							break;
						}
					}
					if (user != null) {
						user.is_user_logged_in = true;
						saveOrUpdateUser(conn, user);
					} else {
						byte[] pin_byte = AES256Cipher.getRandomAesCryptKey();
						byte[] iv_byte = AES256Cipher.getRandomAesCryptIv();
						user.crypt_key = AppHelper.toHexString(pin_byte);
						user.crypt_iv = AppHelper.toHexString(iv_byte);
						user.encrypted_password = AES256Cipher.encrypt(pin_byte, iv_byte, password);
						user.is_user_logged_in = true;
						saveOrUpdateUser(conn, user);
					}
				}
			}
		} catch (Exception e) {
			/*
			 * System.err.println(""); File dir = new File(getPath()); File[] listFiles =
			 * dir.listFiles(); for (File file : listFiles) { file.delete(); } dir.delete();
			 */

			throw new Exception(e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		return user;
	}

	public void disableAllUserLoginStatus(Connection conn) throws Exception {
		boolean isConnectionNull = false;
		Statement stmt = null;
		try {
			if (conn == null) {
				isConnectionNull = true;
				conn = this.connect();
			}
			String sql = "update system_user set is_user_logged_in = 0";
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (conn != null) {
				if (isConnectionNull) {
					conn.close();
				}
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void saveOrUpdateUser(Connection conn, User user) throws Exception {
		int is_user_logged_in = user.is_user_logged_in ? 1 : 0;
		try {
			String sql = "INSERT "
					+ "INTO system_user (user_id,access_token,alias_name,email,expires_in,name,refresh_token,rt_expires_in,surname,token_type,crypt_key,crypt_iv,encrypted_password,is_user_logged_in)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?) on conflict (user_id) do update set  " + "access_token='"
					+ user.access_token + "'" + " ,refresh_token='" + user.refresh_token + "'" + ", crypt_key='"
					+ user.crypt_key + "'" + " ,crypt_iv='" + user.crypt_iv + "'" + ", is_user_logged_in='"
					+ is_user_logged_in + "' ,encrypted_password='" + user.encrypted_password + "'";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.user_id != null ? user.user_id : user._id);
			pstmt.setString(2, user.access_token);
			pstmt.setString(3, user.alias_name);
			pstmt.setString(4, user.email);
			pstmt.setInt(5, user.expires_in);
			pstmt.setString(6, user.name);
			pstmt.setString(7, user.refresh_token);
			pstmt.setInt(8, user.rt_expires_in);
			pstmt.setString(9, user.surname);
			pstmt.setString(10, user.token_type);
			pstmt.setString(11, user.crypt_key);
			pstmt.setString(12, user.crypt_iv);
			pstmt.setString(13, user.encrypted_password);
			pstmt.setBoolean(14, user.is_user_logged_in);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public String getAccessToken(String email, String password) throws Exception {
		String post_query = "grant_type=password&password=" + password + "&username=" + email;
		String response = helper.sendPost("/v1/oauth/token", post_query, null);
		return response;
	}

	private void createDefaultConf() throws FileNotFoundException, UnsupportedEncodingException {

		String conf = getPath() + "sikke.conf";
		File f = new File(conf);
		if (!f.exists()) {
			try (PrintWriter writer = new PrintWriter(conf, "UTF-8")) {
				writer.println("server=1");
				writer.println("rpcuser=default_user");
				writer.println("rpcpassword=default_password");
				writer.println("rpcport=9090");
				writer.println("rpallowip=*");
				writer.close();
			}
			createNewDatabase(getPath() + "wallets.dat");
		}
	}

	public String getPath() {
		String os = getOS("os.name");
		String path = null;
		if (os.contains("Linux")) {
			path = getFileSystemView().getHomeDirectory().toString();
		} else if (os.contains("Windows")) {
			path = System.getenv("APPDATA");
		}
		String conf_file = os.contains("Linux") ? "/.sikke/" : "\\Sikke\\";

		return path + conf_file;
	}

	public String getDB() {
		return "jdbc:sqlite:" + getPath() + "wallets.dat";
	}

	public String getIP() throws IOException {
		String port = getConf("rpcport");
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		String ip = in.readLine();
		return ip;
	}

	public String getCallbackURL() throws IOException {
		String port = getConf("rpcport");
		return getIP() + ":" + port + "/newTransaction";
	}

	public static void createNewDatabase(String fileName) {
		String url = "jdbc:sqlite:" + fileName;
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(url);
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
			}
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void createTables() throws Exception {
		String system_user = "CREATE TABLE IF NOT EXISTS system_user( id INTEGER PRIMARY KEY NOT NULL, user_id TEXT NOT NULL UNIQUE, access_token TEXT, alias_name TEXT, email TEXT NOT NULL, expires_in INT, name TEXT, refresh_token TEXT, rt_expires_in INT, surname TEXT, token_type TEXT, capacity REAL, crypt_key TEXT, crypt_iv TEXT, encrypted_password TEXT, is_user_logged_in BOOLEAN NOT NULL DEFAULT (0)); ";
		String tx = "CREATE TABLE IF NOT EXISTS tx( id INTEGER PRIMARY KEY, _id INTEGER UNIQUE, seq INTEGER, amount TEXT, fee TEXT, fee_asset STRING, hash TEXT, prev_hash TEXT, nonce TEXT, _from TEXT, _to TEXT, asset TEXT, action_time TEXT, completion_time TEXT, confirm_rate INTEGER, [desc] TEXT, [group] STRING, status INTEGER, type INTEGER, subtype INTEGER); ";
		String wallets = "CREATE TABLE IF NOT EXISTS wallets( id INTEGER PRIMARY KEY, address TEXT NOT NULL UNIQUE, email TEXT, label TEXT, private_key TEXT, public_key TEXT, limit_hourly TEXT, limit_daily TEXT, callback_url STRING, contract_token STRING, limit_max_amount TEXT, is_default INTEGER); ";
		String outdatedWallet = "CREATE TABLE IF NOT EXISTS outdated_wallet( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, address STRING NOT NULL, insert_date DATETIME DEFAULT (CURRENT_DATE) NOT NULL);";

		helper.createTable(system_user);
		helper.createTable(tx);
		helper.createTable(wallets);
		helper.createTable(outdatedWallet);
	}

	public String getConf(String param) throws FileNotFoundException, IOException {
		String conf = getPath() + "sikke.conf";
		FileInputStream fstream = new FileInputStream(conf);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
			String strLine;
			String value = null;
			while ((strLine = br.readLine()) != null) {
				String[] flag = strLine.split("=");

				if (flag[0].equals(param)) {
					value = flag[1];
				}
			}
			return value != null ? value : "-";
		}
	}
}
