/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.helpers;

import static javax.swing.filechooser.FileSystemView.getFileSystemView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import sikke.JsonRpcErrorObject;
import sikke.JsonRpcObject;
import sikke.cli.defs.User;
import sikke.cli.defs.wallet;

/**
 *
 * @author mumbucoglu
 */
public class _System {

	public static Helpers helper = new Helpers();
	public static _System system = new _System();

	static public boolean shouldThreadContinueToWork = true;
	// public static boolean isWalletCreated = false;
	public static HashMap<String, List<String>> configMap = null;
	static int maxSequenceNumber = 0;
	public static boolean isAPIReachable = false;
	// static HashMap<wallet, Integer> hmap = null;

	public void initApp() throws FileNotFoundException, UnsupportedEncodingException, Exception {
		initFolder();
		getConfigsFromFile();
	}

	public static JsonRpcObject isSikkeAPIReachable() {
		JsonRpcObject jsonRpcObject = null;
		if(!_System.isAPIReachable) {
			jsonRpcObject = new JsonRpcObject();
			jsonRpcObject.error = new JsonRpcErrorObject(0, "Sikke API does not response");
		}
		return jsonRpcObject;
	}

	private void getConfigsFromFile() throws Exception {
		configMap = new HashMap<>();
		BufferedReader br = null;
		FileInputStream fstream = null;
		try {
			String conf = getPath() + "sikke.conf";
			fstream = new FileInputStream(conf);
			br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				String[] flag = strLine.split("=");
				String confKey = flag[0];
				String confValue = flag[1];
				List<String> values = configMap.get(confKey);
				if (values == null) {
					values = new ArrayList<>();
				}
				values.add(confValue);
				configMap.put(confKey, values);
			}
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			br.close();
			fstream.close();
		}
	}

	public static List<String> getConfig(String param) {
		return configMap.get(param);
	}

	public String getOS(String property) {
		return System.getProperty(property);
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

	public void getActiveUsers(List<User> userList) throws Exception {
		Connection conn = null;
		String sql = "select * from system_user where is_user_logged_in = 1";
		User user = null;
		try {
			if (conn == null) {
				conn = Connect.getConnect();
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
		}
	}

	public void disableAllUserLoginStatus() throws Exception {
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = Connect.getConnect();
			String sql = "update system_user set is_user_logged_in = 0";
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw new Exception(e);
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
				writer.println("rpcallowip=127.0.0.1");
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
		String port = getConfig("rpcport").get(0);
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		String ip = in.readLine();
		return ip;
	}

	public String getCallbackURL() throws IOException {
		String port = getConfig("rpcport").get(0);
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
		String tx = "CREATE TABLE IF NOT EXISTS tx( id INTEGER PRIMARY KEY, _id VARCHAR UNIQUE, seq INTEGER, amount VARCHAR, fee VARCHAR, fee_asset VARCHAR, hash VARCHAR, prev_hash VARCHAR, nonce VARCHAR, _from VARCHAR, _to VARCHAR, asset VARCHAR, action_time VARCHAR, completion_time VARCHAR, confirm_rate VARCHAR, [desc] VARCHAR, [group] INTEGER, status INTEGER, type INTEGER, subtype INTEGER); ";
		String wallets = "CREATE TABLE IF NOT EXISTS wallets( id INTEGER PRIMARY KEY, address TEXT NOT NULL UNIQUE, email TEXT, label TEXT, private_key TEXT, public_key TEXT, limit_hourly TEXT, limit_daily TEXT, limit_max_amount TEXT, callback_url STRING, contract_token STRING, is_default INTEGER); ";
		String outdatedWallet = "CREATE TABLE IF NOT EXISTS outdated_wallet( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, address STRING NOT NULL, insert_date DATETIME DEFAULT (CURRENT_DATE) NOT NULL);";

		helper.createTable(system_user);
		helper.createTable(tx);
		helper.createTable(wallets);
		helper.createTable(outdatedWallet);
	}

	/*
	 * public String getConf(String param) throws FileNotFoundException, IOException
	 * { String conf = getPath() + "sikke.conf"; FileInputStream fstream = new
	 * FileInputStream(conf); try (BufferedReader br = new BufferedReader(new
	 * InputStreamReader(fstream))) { String strLine; String value = null; while
	 * ((strLine = br.readLine()) != null) { String[] flag = strLine.split("="); if
	 * (flag[0].equals(param)) { value = flag[1]; } } return value != null ? value :
	 * "-"; } }
	 */
}
