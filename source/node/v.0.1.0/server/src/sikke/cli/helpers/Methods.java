/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.helpers;

import static sikke.cli.helpers._System.helper;
import static sikke.cli.helpers._System.system;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javafx.scene.input.ScrollEvent.VerticalTextScrollUnits;
import sikke.cli.defs.TxResponse;
import sikke.cli.defs.User;
import sikke.cli.defs.UserResponse;
import sikke.cli.defs.WalletFromFile;
import sikke.cli.defs.WalletResponse;
import sikke.cli.defs.sikkeApi;
import sikke.cli.defs.tx;
import sikke.cli.defs.wallet;
import sikke.cli.wallet.AES256Cipher;
import sikke.cli.wallet.AppHelper;
import sikke.cli.wallet.ECDSAHelper;
import sikke.cli.wallet.WalletKey;

/**
 *
 * @author mumbucoglu
 */
public class Methods {

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	HashMap<wallet, Integer> hmap = null;
	static boolean isWalletCreated = false;

	private Connection connect() {

		String url = system.getDB();
		Connection conn = null;
		try {
			// Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public JsonArray getHistories(String[] params) throws Exception {
		String error = "";
		String whereClause = "";
		JsonArray result = new JsonArray();
		Connection conn = null;
		String sql = null;
		try {
			conn = this.connect();
			if (getOnlyActiveUser(result, conn) == null) {
				return result;
			}
			if (params != null && params.length == 1) {
				String param = params[0];
				String[] criterias = replaceSpaceAndSplit(param);
				if (criterias.length == 2) {
					String key = criterias[0].toLowerCase();
					String value = criterias[1];
					if (key.equals(SikkeConstant.TX_QUERY_TYPE_ADDRESS)) {
						whereClause = " t._from = '" + value + "'";
					} else if (key.equals(SikkeConstant.TX_QUERY_TYPE_HASH)) {
						whereClause = "  t.hash = '" + value + "'";
					} else if (key.equals(SikkeConstant.TX_QUERY_TYPE_SEQUENCE)) {
						whereClause = "  t.seq = '" + value + "'";
					} else if (key.equals(SikkeConstant.TX_QUERY_TYPE_BLOCK)) {
						whereClause = "  t.block = '" + value + "'";
					} else {
						result.add("Unknown query type.Available queries; [address,hash,seq,block]:value");
						return result;
					}
				}
				sql = "SELECT * FROM tx t, wallets w,system_user u where u.email = w.email and w.address = t._from and u.is_user_logged_in = 1 and "
						+ whereClause;
			} else {
				sql = "SELECT * FROM tx t, wallets w, system_user u WHERE u.email = w.email AND w.address = t._from AND u.is_user_logged_in = 1 order by t.seq desc limit 100";
			}

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i = 0;

			while (rs.next()) {
				JsonObject jo = new JsonObject();
				jo.addProperty("_id", rs.getString("_id"));
				jo.addProperty("action_time", rs.getString("action_time"));
				jo.addProperty("amount", rs.getString("amount"));
				jo.addProperty("asset", rs.getString("asset"));
				jo.addProperty("complete_time", rs.getString("completion_time"));
				jo.addProperty("confirm_rate", rs.getString("confirm_rate"));
				jo.addProperty("desc", rs.getString("desc"));
				jo.addProperty("fee", rs.getString("fee"));
				jo.addProperty("fee_asset", rs.getString("fee_asset"));
				jo.addProperty("group", rs.getString("group"));
				jo.addProperty("hash", rs.getString("hash"));
				jo.addProperty("nonce", rs.getString("nonce"));
				jo.addProperty("prev_hash", rs.getString("prev_hash"));
				jo.addProperty("seq", rs.getInt("seq"));
				jo.addProperty("status", rs.getInt("status"));
				jo.addProperty("subtype", rs.getInt("subtype"));
				jo.addProperty("to", rs.getString("_to"));
				jo.addProperty("type", rs.getInt("type"));
				jo.addProperty("wallet", rs.getString("_from"));
				result.add(jo);
				i++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception(e);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception(e);
		} finally {
			DBClose(conn);
		}
		JsonObject tx = new JsonObject();
		tx.add("", result.getAsJsonArray());
		return result;
	}

	public User getOnlyActiveUser(JsonArray result, Connection conn) throws Exception {
		List<User> userList = new ArrayList<>();
		User user = null;
		system.getActiveUsers(conn, userList);
		if (userList.size() == 0) {
			result.add(SikkeConstant.LOGGED_IN_USER_NOT_FOUND);
		} else if (userList.size() == 1) {
			user = userList.get(0);
		} else if (userList.size() > 1) {
			system.disableAllUserLoginStatus(conn);
			result.add(SikkeConstant.LOGGED_IN_USER_NOT_FOUND);
		}
		return user;
	}

	public JsonArray listWallets(String[] params) throws Exception {
		JsonArray result = new JsonArray();
		Connection conn = null;
		try {
			conn = this.connect();
			if (getOnlyActiveUser(result, conn) == null) {
				return result;
			}
			String sql = "SELECT w.*, ifnull(a.asset, 'SKK') asset, ifnull(a.balance, 0) balance FROM system_user u, wallets w LEFT JOIN( SELECT t._from, t.asset, round(ifnull(sum(t.amount), 0), 8) AS balance FROM wallets w, tx t WHERE w.address = t._from GROUP BY t._from, t.asset) a ON w.address = a._from WHERE u.email = w.email AND u.is_user_logged_in = 1;  ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			String prevAddress = "";
			JsonObject balanceObj = null;
			while (rs.next()) {
				String address = rs.getString("address");
				String asset = rs.getString("asset");
				String balance = rs.getString("balance");

				if (prevAddress.equals(address)) {
					balanceObj.addProperty(asset, balance);
				} else {
					JsonObject jo = new JsonObject();
					balanceObj = new JsonObject();
					balanceObj.addProperty(asset, balance);

					jo.addProperty("address", address);
					jo.add("balances", balanceObj);
					jo.addProperty("callback_url", rs.getString("callback_url"));
					jo.addProperty("contract_token", rs.getString("contract_token"));
					jo.addProperty("limit_daily", rs.getString("limit_daily"));
					jo.addProperty("limit_hourly", rs.getString("limit_hourly"));
					jo.addProperty("limit_max_amount", rs.getString("limit_max_amount"));
					jo.addProperty("is_default", rs.getBoolean("is_default"));
					result.add(jo);
				}
				prevAddress = address;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			DBClose(conn);
		}
		return result;
	}

	public JsonArray createWallet(String[] params) throws Exception {
		String error = null;
		JsonArray result = null;
		int params_len = 1;
		String label = null;
		int isDefault = 0;
		Connection conn = null;
		User user = null;
		List<User> userList = new ArrayList<>();
		try {
			conn = this.connect();
			user = getOnlyActiveUser(result, conn);
			if (user == null) {
				return result;
			}
			if (params != null) {
				if (params.length > 1) {
					error = "Too many parameters, please see help menu.";
					result = new JsonArray();
					result.add(error);
					return result;
				} else if (params.length == 1) {
					label = params[0];
				}
			}
			WalletKey walletKey = WalletKey.getWalletKeys();
			result = createWallet(label, isDefault, null, null, null, walletKey, user, conn);
			if (result != null) {
				isWalletCreated = true;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception(e);
		} finally {
			DBClose(conn);
		}
	}

	public JsonArray createWallet(String label, int isDefault, Double limitHourly, Double limitDaily,
			Double limitMaxAmount, WalletKey walletKey, User user, Connection conn) {
		JsonArray result = null;
		JsonObject jo = null;
		try {
			if (conn == null) {
				conn = this.connect();
			}

			String email = user.email;
			String sql = "INSERT INTO wallets (address,email,label,private_key,public_key,is_default,limit_hourly,limit_daily,limit_max_amount) VALUES(?,?,?,?,?,?,?,?,?)";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, walletKey.getAddress());
			pstmt.setString(2, email);
			pstmt.setString(3, label != null ? label : "");
			pstmt.setString(4, walletKey.getPrivateKey());
			pstmt.setString(5, walletKey.getPublicKey());
			pstmt.setInt(6, isDefault);
			pstmt.setString(7, String.valueOf(limitHourly));
			pstmt.setString(8, String.valueOf(limitDaily));
			pstmt.setString(9, String.valueOf(limitMaxAmount));
			pstmt.executeUpdate();
			result = new JsonArray();
			jo = new JsonObject();

			jo.addProperty("address", walletKey.getAddress());
			jo.addProperty("email", email);
			jo.addProperty("label", label != null ? label : "");
			jo.addProperty("private_key", walletKey.getPrivateKey());
			jo.addProperty("public_key", walletKey.getPublicKey());
			jo.addProperty("is_default", isDefault);
			jo.addProperty("limit_hourly", limitHourly);
			jo.addProperty("limit_daily", limitDaily);
			jo.addProperty("limit_max_amount", limitMaxAmount);
			result.add(jo);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return result;
	}

	public JsonArray getBalance(String[] params) throws Exception {
		String sql;
		String whereClause = "";
		JsonArray result = new JsonArray();

		Connection conn = null;
		try {
			conn = this.connect();
			if (getOnlyActiveUser(result, conn) == null) {
				return result;
			}
			if (params != null && params.length > 0) {
				int paramsLength = params.length;
				if (paramsLength == 1) {
					if (params[0].length() > 3) {
						whereClause = " and t._from = '" + params[0] + "'";
					} else {
						whereClause = " and t.asset = '" + params[0].toUpperCase() + "'";
					}
				} else if (paramsLength == 2) {
					String asset, from = "";
					if (params[0].length() > 3) {
						from = params[0];
						asset = params[1].toUpperCase();
					} else {
						from = params[1];
						asset = params[0].toUpperCase();
					}
					whereClause = " and t.asset = '" + asset + "' and t._from = '" + from + "'";
				}
			}
			sql = "SELECT t._from as address, t.asset as asset, round(ifnull(sum(t.amount),0),8) balance FROM wallets w, tx t,system_user u where w.address = t._from and u.email = w.email and u.is_user_logged_in = 1 "
					+ whereClause + " group by t._from, t.asset";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			String prevAddress = "";
			// JsonArray jsonArray = null;
			JsonObject jsonWalletObj = null;
			JsonObject jo = null;
			while (rs.next()) {
				String address = rs.getString("address");
				String asset = rs.getString("asset");
				String balance = rs.getString("balance");

				if (prevAddress.equals(address)) {
					jo.addProperty(asset, balance);
				} else {
					jsonWalletObj = new JsonObject();
					jo = new JsonObject();
					jo.addProperty(asset, balance);
					jsonWalletObj.add(address, jo);
					result.add(jsonWalletObj);
				}
				prevAddress = address;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception(e);
		} finally {
			DBClose(conn);
		}
		return result;
	}

	public JsonArray send(String[] params) throws Exception {
		String error = "";
		String whereClause = "";
		String sql = null;
		JsonArray result = new JsonArray();
		int paramsLength = params.length;
		String from = null;
		String to = null;
		String asset = null;
		String privateKey = null;
		String publicKey = null;
		double amount = 0;
		String desc = null;
		int hidden = 0;
		Gson g = new Gson();
		Connection conn = null;
		User user = null;

		try {
			conn = this.connect();
			user = getOnlyActiveUser(result, conn);
			if (user == null) {
				return result;
			}
			if (params == null || params.length < 2) {
				error = "Insufficient parameter set";
				result.add(error);
				return result;
			}
			for (int i = 0; i < params.length; i++) {
				String param = params[i];
				String[] criterias = replaceSpaceAndSplit(param);
				String key = criterias[0].toLowerCase();
				String value = criterias[1];

				if (key.equals(SikkeConstant.TEXT_FROM)) {
					from = value;
				} else if (key.equals(SikkeConstant.TEXT_TO)) {
					to = value;
				} else if (key.equals(SikkeConstant.TEXT_ASSET)) {
					asset = value;
				} else if (key.equals(SikkeConstant.TEXT_AMOUNT)) {
					amount = new Double(value);
				} else if (key.equals(SikkeConstant.TEXT_DESC)) {
					desc = value;
				} else if (key.equals(SikkeConstant.TEXT_HIDDEN)) {
					hidden = Integer.parseInt(value);
				}
			}
			if (to == null) {
				error = "Address to be sent cannot bu empty";
				result.add(error);
				return result;
			}
			if (amount <= 0) {
				error = "Amount must be greater than (0)zero";
				result.add(error);
				return result;
			}
			if (asset == null) {
				asset = SikkeConstant.DEFAULT_ASSET;
			}
			if (from == null) {
				sql = "SELECT w.address,w.private_key,w.public_key FROM wallets w,system_user u where u.email = w.email and w.is_default= 1 and u.is_user_logged_in = 1";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()) {
					from = rs.getString("address");
					privateKey = rs.getString("private_key");
					publicKey = rs.getString("public_key");
				} else {
					error = "Default wallet not found";
					result.add(error);
					return result;
				}
			} else {
				Statement stmt = conn.createStatement();
				sql = "select w.address,w.private_key,w.public_key from wallets w,system_user u where w.address ='"
						+ from + "' and u.email = w.email and u.is_user_logged_in = 1";
				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()) {
					from = rs.getString("address");
					privateKey = rs.getString("private_key");
					publicKey = rs.getString("public_key");
				} else {
					error = "Wallet not found your database.You must import your private key";
					result.add(error);
					return result;
				}
			}
			String query = from + "?asset=" + asset;
			String strBalance = new Helpers().sendGet(SikkeConstant.GET_WALLET_BALANCE_URL, query);
			Balance balance = gson.fromJson(strBalance, Balance.class);
			if (balance.balance >= amount) {
				String amountStr = SikkeConstant.formatNumber(amount);
				amountStr = SikkeConstant.formatAmount(String.valueOf(amount));

				long nonce = SikkeConstant.getEpochTime();
				StringBuilder sbTextToBeSigned = new StringBuilder();
				sbTextToBeSigned.append(from).append(SikkeConstant.DOUBLE_UNDERSCORE).append(to)
						.append(SikkeConstant.DOUBLE_UNDERSCORE).append(amountStr)
						.append(SikkeConstant.DOUBLE_UNDERSCORE).append(asset).append(SikkeConstant.DOUBLE_UNDERSCORE)
						.append(nonce);
				PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);
				String signedTx = ECDSAHelper.sign(sbTextToBeSigned.toString(), pvKey);
				StringBuilder sbPostQuery = new StringBuilder();

				sbPostQuery.append("tx_w_number=").append(from).append("&tx_to_w_number=").append(to)
						.append("&tx_sign=").append(signedTx).append("&tx_amount=").append(amountStr)
						.append("&tx_desc=").append(desc).append("&tx_asset=").append(asset).append("&w_pub_key=")
						.append(publicKey).append("&tx_nonce=").append(nonce).append("&is_hidden=").append(hidden);

				String response = helper.sendPost("/v1/tx", sbPostQuery.toString(), null);
				// System.err.println(response);
				TxResponse txResponse = g.fromJson(response.toString(), TxResponse.class);
				// System.out.println(txResponse);
				if (txResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
					tx tx = txResponse.tx;
					if (new Long(nonce) == Long.parseLong(tx.nonce)) {
						insertTx(conn, tx);
						JsonObject jo = new JsonObject();
						jo.addProperty("_id", tx._id);
						jo.addProperty("seq", tx.seq);
						jo.addProperty("amount", tx.amount);
						jo.addProperty("fee_asset", tx.fee_asset);
						jo.addProperty("hash", tx.hash);
						jo.addProperty("prev_hash", tx.prev_hash);
						jo.addProperty("nonce", tx.nonce);
						jo.addProperty("wallet", tx.wallet);
						jo.addProperty("to", tx.to);
						jo.addProperty("asset", tx.asset);
						jo.addProperty("action_time", tx.action_time);
						jo.addProperty("complete_time", tx.complete_time);
						jo.addProperty("confirmRate", tx.confirmRate);
						jo.addProperty("desc", tx.desc);
						jo.addProperty("group", tx.group);
						jo.addProperty("status", tx.status);
						jo.addProperty("type", tx.type);
						jo.addProperty("subtype", tx.subtype);
						result.add(jo);
						return result;
					}
				} else {
					result.add(txResponse.message);
					return result;
				}
			} else {
				error = "Not enough balance.";
				System.err.println(error);
				result.add(error);
				return result;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			DBClose(conn);
		}
		return result;
	}

	private void insertTx(Connection conn, tx tx) throws SQLException {
		String sql;
		sql = "INSERT INTO tx (_id,seq,amount,fee,fee_asset,hash,prev_hash,nonce,_from,_to,asset,action_time,completion_time,confirm_rate,[desc],[group],status,type,subtype) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, tx._id); // id
		pstmt.setInt(2, tx.seq);// seq
		pstmt.setString(3, String.valueOf(tx.amount));
		pstmt.setString(4, String.valueOf(tx.fee));// fee
		pstmt.setString(5, tx.fee_asset);// fee_asset
		pstmt.setString(6, tx.hash);// hash
		pstmt.setString(7, tx.prev_hash);// prev_hash
		pstmt.setString(8, tx.nonce);// nonce
		pstmt.setString(9, tx.wallet);// from
		pstmt.setString(10, tx.to); // to
		pstmt.setString(11, tx.asset);// asset
		pstmt.setString(12, String.valueOf(tx.action_time));// action time
		pstmt.setString(13, String.valueOf(tx.complete_time));// completion_time
		pstmt.setString(14, tx.confirmRate);// block
		pstmt.setString(15, tx.desc);// desc
		pstmt.setString(16, tx.group);// group
		pstmt.setInt(17, tx.status);// status
		pstmt.setInt(18, tx.type);// type
		pstmt.setInt(19, tx.subtype);// subtype
		pstmt.executeUpdate();
	}

	public JsonArray importWallet(String[] params) throws Exception {
		String error = "";
		String sql;
		JsonArray result = new JsonArray();
		int paramsLength = params.length;
		Connection conn = null;
		List<User> userList = new ArrayList();
		User user = null;
		try {
			conn = this.connect();
			user = getOnlyActiveUser(result, conn);
			if (user == null) {
				return result;
			}
			if (params != null && params.length == 1) {
				String encodedPrivateKey = params[0];
				WalletKey walletKey = WalletKey.getWalletKeysFromPrivateKey(encodedPrivateKey);
				sql = "select w.address from wallets w,system_user u where w.address = '" + walletKey.getAddress()
						+ "' and w.email = u.email and u.is_user_logged_in = 1";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()) {
					error = "The wallet you want to import already exists";
					// System.out.println(error);
					result.add(error);
					rs.close();
				} else {
					rs.close();
					stmt.close();
					sql = "INSERT INTO wallets (address,email,label,private_key,public_key,is_default,limit_hourly,limit_daily,limit_max_amount) VALUES(?,?,?,?,?,?,?,?,?)";

					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, walletKey.getAddress());
					pstmt.setString(2, user.email);
					pstmt.setString(3, "");
					pstmt.setString(4, walletKey.getPrivateKey());
					pstmt.setString(5, walletKey.getPublicKey());
					pstmt.setInt(6, 0);
					pstmt.setString(7, "");
					pstmt.setString(8, "");
					pstmt.setString(9, "");
					pstmt.executeUpdate();
					JsonObject jo = new JsonObject();
					jo.addProperty("address", walletKey.getAddress());
					jo.addProperty("privateKey", walletKey.getPrivateKey());
					jo.addProperty("publicKey", walletKey.getPublicKey());
					jo.addProperty("address", walletKey.getAddress());
					jo.addProperty("email", user.email);

					result.add(jo);
					return result;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			DBClose(conn);
		}
		return result;
	}

	public JsonArray makeDefault(String[] params) throws Exception {
		String error = "";
		String sql;
		JsonArray result = new JsonArray();
		int paramsLength = params.length;
		Connection conn = null;
		User user = null;
		try {
			conn = this.connect();
			user = getOnlyActiveUser(result, conn);
			if (user == null) {
				return result;
			}
			if (params != null && params.length == 1) {
				String walletAddress = params[0];
				sql = "select w.id from wallets w,system_user u where w.address = '" + walletAddress
						+ "' and u.email = w.email and u.is_user_logged_in = 1";

				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()) {
					int id = rs.getInt("id");
					stmt.addBatch("UPDATE wallets SET is_default = 0");
					stmt.addBatch("UPDATE wallets SET is_default  = 1  WHERE id ='" + id + "'");
					stmt.executeBatch();
					error = "Wallet address [" + walletAddress + "] is defaulted.";
					result.add(error);
				} else {
					error = "The wallet you want to make default is not found.";
					// System.out.println(error);
					result.add(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			DBClose(conn);
		}

		return result;
	}

	public JsonArray createWalletAndSave(String[] params) throws Exception {
		String error = null;
		JsonArray result = null;
		String aliasName = null;
		Double limitHourly = null;
		Double limitDaily = null;
		Double limitMaxAmount = null;
		Connection conn = null;
		int isDefault = 0;
		try {
			conn = this.connect();
			if (params != null || params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					String param = params[i];
					String[] criterias = replaceSpaceAndSplit(param);
					String key = criterias[0].toLowerCase();
					String value = criterias[1];
					if (key.equals(SikkeConstant.ALIAS_NAME)) {
						aliasName = value;
					} else if (key.equals(SikkeConstant.LIMIT_HOURLY)) {
						limitHourly = Double.parseDouble(value);
					} else if (key.equals(SikkeConstant.LIMIT_DAILY)) {
						limitDaily = Double.parseDouble(value);
					} else if (key.equals(SikkeConstant.LIMIT_MAX_AMOUNT)) {
						limitMaxAmount = Double.parseDouble(value);
					} else if (key.equals(SikkeConstant.DEFAULT)) {
						isDefault = Integer.parseInt(value);
					}
				}
			}
			WalletKey walletKey = WalletKey.getWalletKeys();
			return createAccountAndSave(aliasName, limitHourly, limitDaily, limitMaxAmount, isDefault, walletKey, null,
					conn);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	private JsonArray createAccountAndSave(String aliasName, Double limitHourly, Double limitDaily,
			Double limitMaxAmount, int isDefault, WalletKey walletKey, User user, Connection conn) throws Exception {
		JsonArray result = new JsonArray();
		Gson g = new Gson();
		String error = "";
		List<User> userList = new ArrayList<>();
		boolean isConnectionNull = false;
		try {
			if (conn == null) {
				conn = this.connect();
				isConnectionNull = true;
			}
			if (user == null) {
				user = getOnlyActiveUser(result, conn);
				if (user == null) {
					return result;
				}
			}
			if (walletKey == null) {
				result.add(SikkeConstant.WALLET_NOT_CREATED);
				return result;
			}
			long nonce = SikkeConstant.getEpochTime();
			String nonceStr = String.valueOf(nonce);
			StringBuilder sb = new StringBuilder();

			String privateKey = walletKey.getPrivateKey();
			PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);
			String signedTx = ECDSAHelper.sign(nonceStr, pvKey);

			String plainText = AES256Cipher.decrypt(AppHelper.hexStringToByteArray(user.crypt_key),
					AppHelper.hexStringToByteArray(user.crypt_iv), user.encrypted_password);
			byte[] u_password = AES256Cipher.key128Bit(plainText);
			String encryptedPvtKey = AES256Cipher.encryptPvt(u_password, privateKey);

			// System.out.println("encryptedPvtKey : " + encryptedPvtKey);

			String decryptedPAssword = AES256Cipher.decryptPvt(u_password, encryptedPvtKey);

			sb.append("w_pub_key=" + walletKey.getPublicKey());
			sb.append("&sign=" + signedTx);
			sb.append("&w_zeugma=" + encryptedPvtKey);
			sb.append("&w_owner_id=" + user.user_id);
			sb.append("&nonce=" + nonceStr);
			sb.append("&w_status=" + 1);
			if (aliasName != null) {
				sb.append("&w_alias_name=" + aliasName);
			}
			if (limitHourly != null) {
				sb.append("&w_limit_hourly=" + String.valueOf(limitHourly));
			}
			if (limitDaily != null) {
				sb.append("&w_limit_daily=" + String.valueOf(limitDaily));
			}
			if (limitMaxAmount != null) {
				sb.append("&w_limit_max_amount=" + String.valueOf(limitMaxAmount));
			}
			String response = helper.sendPost("/v1/wallet", sb.toString(), SikkeConstant.REQUEST_PUT);
			// System.err.println(response);
			WalletResponse walletResponse = g.fromJson(response.toString(), WalletResponse.class);

			if (walletResponse != null) {
				if (walletResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
					wallet wallet = walletResponse.wallet;
					if (wallet != null) {
						result = createWallet(wallet.alias_name, isDefault, limitHourly, limitDaily, limitMaxAmount,
								walletKey, user, conn);
						isWalletCreated = true;
						return result;
					}
				} else {
					error = "Wallet creation failed.";
					result.add(error);
					return result;
				}
			}
			error = "Wallet creation failed.";
			result.add(error);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			if (isConnectionNull) {
				DBClose(conn);
			}
		}
		return result;
	}

	public JsonArray syncWallet(String[] params) throws Exception {
		JsonArray result = new JsonArray();
		Gson g = new Gson();
		String sql = null;
		String error = "";
		StringBuilder sb = null;
		String limitHourly = null, aliasName = null, limitMaxAmount = null, limitDaily = null, privateKey = null,
				nonce = null, publicKey = null, signedTx = null, address = null;
		Connection conn = null;
		User user = null;
		List<User> userList = new ArrayList<>();
		try {
			conn = this.connect();
			user = getOnlyActiveUser(result, conn);
			if (user == null) {
				return result;
			}
			Statement stmt = conn.createStatement();
			if (params == null || params.length == 0) {
				sql = "SELECT w.* FROM wallets w,system_user u where u.email = w.email and u.is_user_logged_in = 1";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					sb = new StringBuilder();
					aliasName = rs.getString("label");
					limitHourly = rs.getString("limit_hourly");
					limitDaily = rs.getString("limit_daily");
					limitMaxAmount = rs.getString("limit_max_amount");
					privateKey = rs.getString("private_key");
					nonce = String.valueOf(SikkeConstant.getEpochTime());
					publicKey = rs.getString("public_key");
					int isDefault = rs.getInt("is_default");

					PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);
					signedTx = ECDSAHelper.sign(nonce, pvKey);

					sb.append("w_pub_key=" + publicKey);
					sb.append("&sign=" + signedTx);
					sb.append("&w_owner_id=" + user.user_id);
					sb.append("&nonce=" + nonce);
					sb.append("&w_status=" + 1);
					sb.append("&w_status=" + 1);
					sb.append("&w_is_default=" + rs.getInt("is_default"));

					if (aliasName != null) {
						sb.append("&w_alias_name=" + aliasName);
					}
					if (limitDaily != null) {
						sb.append("&w_limit_daily=" + limitDaily);
					}
					if (limitHourly != null) {
						sb.append("&limit_hourly=" + limitHourly);
					}
					if (limitMaxAmount != null) {
						sb.append("&w_limit_max_amount=" + limitMaxAmount);
					}
					String response = helper.sendPost("/v1/wallet", sb.toString(), SikkeConstant.REQUEST_PUT);
					WalletResponse walletResponse = g.fromJson(response, WalletResponse.class);
					if (walletResponse != null) {
						if (walletResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
							wallet wallet = walletResponse.wallet;
							if (wallet != null) {
								JsonObject jsonObj = new JsonObject();
								jsonObj.addProperty("address", wallet.address);
								result.add(jsonObj);
							}
						}
					}
				}
				DBClose(conn);
				return result;
			} else {
				for (int i = 0; i < params.length; i++) {
					String param = params[i];
					if (param.toLowerCase().startsWith("address:")) {
						address = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("limit_daily:")) {
						limitDaily = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("limit_hourly:")) {
						limitHourly = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("limit_max_amount:")) {
						limitMaxAmount = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("alias_name:")) {
						aliasName = param.split(":")[1];
					}
				}
				if (address == null) {
					error = "Address field cannot be empty";
					result.add(error);
					DBClose(conn);
					return result;
				}
				sql = "select * from wallets w, system_user u where w.address ='" + address
						+ "' and w.email = u.email and u.is_user_logged_in = 1";
				ResultSet rs = stmt.executeQuery(sql);

				if (rs.next()) {
					sb = new StringBuilder();
					aliasName = aliasName == null ? rs.getString("label") : aliasName;
					limitHourly = limitHourly == null ? rs.getString("limit_hourly") : limitHourly;
					limitDaily = limitDaily == null ? rs.getString("limit_daily") : limitDaily;
					limitMaxAmount = limitMaxAmount == null ? rs.getString("limit_max_amount") : limitMaxAmount;

					privateKey = rs.getString("private_key");
					publicKey = rs.getString("public_key");
					nonce = String.valueOf(SikkeConstant.getEpochTime());
					PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);

					signedTx = ECDSAHelper.sign(nonce, pvKey);

					sb.append("w_pub_key=" + publicKey);
					sb.append("&sign=" + signedTx);
					sb.append("&w_owner_id=" + user.user_id);
					sb.append("&nonce=" + nonce);
					sb.append("&w_status=" + 1);
					sb.append("&w_is_default=" + rs.getInt("is_default"));

					if (aliasName != null) {
						sb.append("&w_alias_name=" + aliasName);
					}
					if (limitDaily != null) {
						sb.append("&w_limit_daily=" + limitDaily);
					}
					if (limitHourly != null) {
						sb.append("&limit_hourly=" + limitHourly);
					}
					if (limitMaxAmount != null) {
						sb.append("&w_limit_max_amount=" + limitMaxAmount);
					}
					String response = helper.sendPost("/v1/wallet", sb.toString(), SikkeConstant.REQUEST_PUT);
					WalletResponse walletResponse = g.fromJson(response, WalletResponse.class);
					if (walletResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
						sql = "update wallets set label=?, limit_daily=?, limit_hourly=?, limit_max_amount=? where address = ?";

						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, aliasName);
						pstmt.setString(2, limitDaily);
						pstmt.setString(3, limitHourly);
						pstmt.setString(4, limitMaxAmount);
						pstmt.setString(5, address);
						pstmt.executeUpdate();

						JsonObject jo = new JsonObject();
						jo.addProperty("aliasName", aliasName);
						jo.addProperty("address", address);
						jo.addProperty("limitHourly", limitHourly);
						jo.addProperty("limitDaily", limitDaily);
						result.add(jo);
					}
				} else {
					error = "No such wallet was found.";
					result.add(error);
					DBClose(conn);
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			DBClose(conn);
		}
		return result;
	}

	public void DBClose(Connection conn) throws SQLException {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}

	public JsonArray help(String[] params) {

		JsonArray jsonArray = new JsonArray();
		StringBuilder sb = new StringBuilder();

		sb.append(SikkeConstant.centerString("\n...:::Sikke Client Help Menu:::..."));
		sb.append("\n[] means optional field   ,   () means mandatory field");
		sb.append("\n\n--" + String.format("%1$-15s %2$10s", "getTransactions",
				" : ([address:value] | [hash:value] | [seq:value] | [block:value])  --  Example request: address:'SKK1N5WHL2m6WcfqF29Uj...'"));
		sb.append("\n--" + String.format("%1$-15s", "listAccounts"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "createWallet", " : [[label]]"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "createAndSaveWallet",
				" : [[alias_name:value] & [limit_daily:value] & [limit_hourly:value] & [limit_max_amount:value]]"));// TODO
		sb.append("\n--" + String.format("%1$-15s %2$10s", "synchWallet", " : [[label]]"));// TODO
		sb.append("\n--" + String.format("%1$-15s %2$10s", "getBalance", " : [[address],[asset]]"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "sendTx",
				" : ([from:value],(to:value),[asset:value],(amount),[desc])  -- if the sender wallet is not specified. The default wallet is used."));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "importWallet", " : ((private key))"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "makeDefault", " : ((address))"));

		sb.append("\n--" + String.format("%1$-15s %2$10s", "makeDefault", " : ((address))"));

		// System.out.println(sb.toString());

		jsonArray.add(SikkeConstant.centerString(" "));
		jsonArray.add(SikkeConstant.centerString("...::: Sikke Client Help Menu :::..."));
		jsonArray.add(SikkeConstant.centerString(" "));
		jsonArray.add(SikkeConstant.centerString(" "));
		jsonArray.add("[] means optional field  , () means required field");
		jsonArray.add(SikkeConstant.centerString(" "));
		jsonArray.add(String.format("%1$-25s %2$20s", "-register", " : ((email:Value),(password:Value))"));
		jsonArray.add(String.format("%1$-25s %2$20s", "-login", " : ((email:Value), (password:Value))"));
		jsonArray.add(String.format("%1$-25s %2$20s", "-logout", " "));
		jsonArray.add(String.format("%1$-25s %2$20s", "-getBalance", " : [[address],[asset]]"));
		jsonArray.add(String.format("%1$-25s %2$20s", "-makeDefault", " : ((address))                    "));
		jsonArray.add(String.format("%1$-25s %2$20s", "-importWallet", " : ((private key))          "));
		jsonArray.add(String.format("%1$-25s %2$20s", "-createWallet", " : [[label]]        "));
		jsonArray.add(String.format("%1$-25s %2$20s", "-createAndSaveWallet",
				" : [[alias_name:value] & [limit_daily:value] & [limit_hourly:value] & [limit_max_amount:value]]"));
		jsonArray.add(String.format("%1$-25s %2$20s", "-listWallets", ""));
		jsonArray.add(String.format("%1$-25s %2$20s", "-synchWallet", " : [[label]]          "));
		jsonArray.add(String.format("%1$-25s %2$20s", "-getHistories",
				" : [[address:value],[hash:value] | [seq:value] | [block:value]]  -->  Example request: (address:SKK1N5WHL2m6WcfqF29Uj...)"));
		jsonArray.add(String.format("%1$-25s %2$20s", "-mergeBalances", " : [[label]]          "));
		jsonArray.add(String.format("%1$-25s %2$20s", "-send",
				" : ([from:value],(to:value),[asset:value],(amount),[desc])  --> if the sender wallet is not specified. The default wallet is used."));
		jsonArray.add(String.format("%1$-25s %2$20s", "-importWallets", ""));
		jsonArray.add(String.format("%1$-25s %2$20s", "-exportWallets", ""));
		jsonArray.add(String.format("%1$-25s %2$20s", "-help", " : Shows help menu       "));
		jsonArray.add(SikkeConstant.centerString(" "));
		jsonArray.add(SikkeConstant.centerString(" "));
		jsonArray.add(SikkeConstant.centerString(" "));
		jsonArray.add(String.format("%1$-25s %2$20s", "Sikke Client System Github",
				": https://github.com/sikke-official/sikke-java-client "));
		return jsonArray;
	}

	public JsonArray mergeBalance(String[] params) throws Exception {

		String error = null;
		JsonArray result = new JsonArray();
		Gson g = new Gson();
		Connection conn = null;
		String sql = null;
		wallet receiverWallet = null;
		wallet senderWallet = null;
		String asset = null;
		HashSet<String> hashAddress = null;
		User user = null;
		JsonObject jo = null;
		try {
			conn = this.connect();
			user = getOnlyActiveUser(result, conn);
			if (user == null) {
				return result;
			}
			hashAddress = new HashSet<String>();
			sql = "select * from wallets w,system_user u where u.email = w.email and u.is_user_logged_in = 1";
			if (params != null) {
				if (params.length == 0) {
					sql += " and w.is_default = 1";
				} else if (params.length > 0) {
					for (String param : params) {
						String[] criterias = replaceSpaceAndSplit(param);
						String key = criterias[0].toLowerCase();
						String value = criterias[1];
						if (key.equals(SikkeConstant.ADDRESS)) {
							sql += " and w.address='" + value + "'";
						} else if (key.equals(SikkeConstant.TEXT_ASSET)) {
							asset = value.toUpperCase();
						}
					}
				}
			}
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				receiverWallet = new wallet();
				receiverWallet.privateKey = rs.getString("private_key");
				receiverWallet.address = rs.getString("address");
				receiverWallet.publicKey = rs.getString("public_key");
			}
			if (receiverWallet == null) {
				error = "Default wallet(receiver wallet) could not found.";
				result.add(error);
				return result;
			}
			String whereClausePart = "";
			if (asset != null) {
				whereClausePart = " and t.asset = '" + asset + "'";
			}
			sql = "SELECT w1.address,w1.private_key,w1.public_key,a.amount,a.asset FROM wallets w1,(SELECT t._from,t.asset,sum(t.amount) amount FROM wallets w,tx t,system_user u WHERE u.email = w.email and  u.is_user_logged_in = 1 and w.address = t._from and w.is_default = 0 "
					+ whereClausePart + "GROUP BY t._from,t.asset) a WHERE w1.address = a._from and a.amount >1";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			String desc = null, signedTx = null, response = null;
			StringBuilder sbTextToBeSigned, sbPostQuery;
			PrivateKey pvKey = null;

			while (rs.next()) {
				senderWallet = new wallet();
				senderWallet.address = rs.getString("address");
				senderWallet.privateKey = rs.getString("private_key");
				senderWallet.publicKey = rs.getString("public_key");
				senderWallet.balance = rs.getDouble("amount");
				senderWallet.asset = rs.getString("asset");

				long nonce = SikkeConstant.getEpochTime();
				String amountStr = SikkeConstant.formatNumber(senderWallet.balance);
				amountStr = SikkeConstant.formatAmount(amountStr);

				sbTextToBeSigned = new StringBuilder().append(senderWallet.address)
						.append(SikkeConstant.DOUBLE_UNDERSCORE).append(receiverWallet.address)
						.append(SikkeConstant.DOUBLE_UNDERSCORE).append(amountStr)
						.append(SikkeConstant.DOUBLE_UNDERSCORE).append(senderWallet.asset)
						.append(SikkeConstant.DOUBLE_UNDERSCORE).append(String.valueOf(nonce));

				pvKey = ECDSAHelper.importPrivateKey(senderWallet.privateKey);
				signedTx = ECDSAHelper.sign(sbTextToBeSigned.toString(), pvKey);

				desc = SikkeConstant.MERGE_BALANCE_TEXT;
				sbPostQuery = new StringBuilder().append("tx_w_number=").append(senderWallet.address)
						.append("&tx_to_w_number=").append(receiverWallet.address).append("&tx_sign=").append(signedTx)
						.append("&tx_amount=").append(amountStr).append("&tx_desc=").append(desc).append("&tx_asset=")
						.append(senderWallet.asset).append("&w_pub_key=").append(senderWallet.publicKey)
						.append("&tx_nonce=").append(String.valueOf(nonce)).append("&is_hidden=0");

				response = helper.sendPost("/v1/tx", sbPostQuery.toString(), null);
				// System.err.println(response);
				TxResponse txResponse = g.fromJson(response.toString(), TxResponse.class);
				if (txResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
					insertTx(conn, txResponse.tx);
					jo = new JsonObject();
					jo.addProperty("from", senderWallet.address);
					jo.addProperty("to", receiverWallet.address);
					jo.addProperty("asset", senderWallet.asset);
					jo.addProperty("amount", amountStr);
					result.add(jo);
				} else if (txResponse.status.equals(SikkeConstant.STATUS_ERROR)) {
					// TODO Error code a bakarak yetersiz bakiye kontrolü yapýlcak

					String strBalance = new Helpers().sendGet(SikkeConstant.GET_WALLET_BALANCE_URL,
							senderWallet.address + "?asset=" + senderWallet.asset);
					Balance balance = gson.fromJson(strBalance, Balance.class);

					if (balance != null && balance.balance > 0) {
						amountStr = SikkeConstant.formatNumber(balance.balance);
						amountStr = SikkeConstant.formatAmount(amountStr);

						sbTextToBeSigned = new StringBuilder().append(senderWallet.address)
								.append(SikkeConstant.DOUBLE_UNDERSCORE).append(receiverWallet.address)
								.append(SikkeConstant.DOUBLE_UNDERSCORE).append(amountStr)
								.append(SikkeConstant.DOUBLE_UNDERSCORE).append(senderWallet.asset)
								.append(SikkeConstant.DOUBLE_UNDERSCORE).append(String.valueOf(nonce));

						signedTx = ECDSAHelper.sign(sbTextToBeSigned.toString(), pvKey);

						sbPostQuery = new StringBuilder().append("tx_w_number=").append(senderWallet.address)
								.append("&tx_to_w_number=").append(receiverWallet.address).append("&tx_sign=")
								.append(signedTx).append("&tx_amount=").append(amountStr).append("&tx_desc=")
								.append(desc).append("&tx_asset=").append(senderWallet.asset).append("&w_pub_key=")
								.append(senderWallet.publicKey).append("&tx_nonce=").append(String.valueOf(nonce))
								.append("&is_hidden=0");

						response = helper.sendPost("/v1/tx", sbPostQuery.toString(), null);
						if (txResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
							insertTx(conn, txResponse.tx);
							jo = new JsonObject();
							jo.addProperty("senderWallet", senderWallet.address);
							jo.addProperty("receiverWallet", receiverWallet.address);
							jo.addProperty("asset", senderWallet.asset);
							jo.addProperty("amount", amountStr);
							result.add(jo);
						} else if (txResponse.status.equals(SikkeConstant.STATUS_ERROR)) {
							hashAddress.add(senderWallet.address);
							continue;
							// insertOutdatedWallet(conn, senderWallet);
						}
					}
					hashAddress.add(senderWallet.address);
					// insertOutdatedWallet(conn, senderWallet);
				}
			}
			insertOutdatedWallet(conn, hashAddress);
			stmt.close();
			conn.close();
			repairTx(params);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception(e);
		} finally {
			DBClose(conn);
		}
		return result;
	}

	public String[] replaceSpaceAndSplit(String param) {
		String[] criterias = param.replaceAll(" ", "").split(SikkeConstant.SEPERATOR);
		return criterias;
	}

	public JsonArray repairTx(String[] params) throws Exception {
		Connection con = null;
		String sql = null;
		String address = null;
		JsonArray result = new JsonArray();
		String error = null;
		Gson g = new Gson();
		HashSet<String> hashAddress = null;
		User user = null;
		try {
			con = this.connect();
			hashAddress = new HashSet<>();
			user = getOnlyActiveUser(result, con);
			if (user == null) {
				return result;
			}
			sql = "SELECT a.*, ifnull(b.maxSeqNum, 0) maxSeqNumber FROM( SELECT w.address, w.public_key, w.private_key FROM outdated_wallet o, wallets w, system_user u WHERE w.address = o.address AND u.email = w.email AND u.is_user_logged_in = 1) a LEFT JOIN ( SELECT _from, max(t.seq) AS maxSeqNum FROM tx t, system_user u, wallets w WHERE t._from = w.address AND w.email = u.email AND u.is_user_logged_in = 1 GROUP BY t._from ) b ON a.address = b._from order by maxSeqNumber desc ;";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				address = rs.getString("address");
				String publicKey = rs.getString("public_key");
				// int maxSeqNumber = rs.getInt("maxSeqNumber");
				int skip = 0;
				int limit = SikkeConstant.QUERY_LIMIT;
				int totalRecordBasedOnAddress = 0;

				while (skip >= 0) {
					StringBuilder sbTx = new StringBuilder().append("wallet=").append(address).append("&w_pub_key=")
							.append(publicKey)./* append("&seq_gt=").append(maxSeqNumber). */append("&limit=")
							.append(String.valueOf(limit)).append("&skip=").append(String.valueOf(skip))
							.append("&sort=asc");
					String response = helper.sendGet("/v1/tx?", sbTx.toString());
					// System.err.println(response);

					JsonObject json = (JsonObject) new JsonParser().parse(response);
					JsonArray jsonArray = (JsonArray) json.get("tx_items");

					if (jsonArray.size() > 0) {
						totalRecordBasedOnAddress += jsonArray.size();
						List<tx> txList = gson.fromJson(jsonArray, new TypeToken<List<tx>>() {
						}.getType());

						if (txList != null && txList.size() > 0) {
							if (txList.size() > limit) {
								skip++;
							} else {
								skip = -1;
							}
							for (tx tx : txList) {
								insertOrUpdateTx(con, tx);
							}
						}
					} else {
						skip = -1;
					}
				}
				error = totalRecordBasedOnAddress + " tx inserted/updated on wallet [" + address + "]";
				result.add(error);
				hashAddress.add(address);
			}
			deleteOutdatedWallets(con, hashAddress);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			DBClose(con);
		}
		if (address == null) {
			error = "No wallet to repair.";
			result.add(error);
		}
		return result;
	}

	synchronized public JsonArray syncTx() throws Exception {
		JsonArray result = new JsonArray();
		Connection con = null;
		wallet wallet = null;
		String error = null;
		String sql = null;
		Gson g = new Gson();
		int maxSeqNum = 0;

		try {
			con = this.connect();
			if (hmap == null) {
				hmap = new HashMap<wallet, Integer>();
				getWalletInfo(con, hmap);
			} else {
				if (isWalletCreated) {
					hmap = new HashMap<wallet, Integer>();
					getWalletInfo(con, hmap);
					isWalletCreated = false;
				}
			}
			Iterator iterator = hmap.entrySet().iterator();
			while (iterator.hasNext()) {
				int skip = 0;
				int limit = SikkeConstant.QUERY_LIMIT;
				int totalRecordBasedOnAddress = 0;

				Map.Entry me = (Map.Entry) iterator.next();
				wallet = (sikke.cli.defs.wallet) me.getKey();
				maxSeqNum = (int) me.getValue();

				while (skip >= 0) {
					StringBuilder sbTx = new StringBuilder();
					sbTx.append("wallet=").append(wallet.address).append("&w_pub_key=").append(wallet.publicKey)
							.append("&seq_gt=").append(maxSeqNum).append("&limit=").append(String.valueOf(limit))
							.append("&skip=").append(String.valueOf(skip)).append("&sort=asc");

					String response = helper.sendGet("/v1/tx?", sbTx.toString());
					// System.err.println(response);
					JsonObject json = (JsonObject) new JsonParser().parse(response);
					JsonArray jsonArray = (JsonArray) json.get("tx_items");

					if (jsonArray != null && jsonArray.size() > 0) {
						totalRecordBasedOnAddress += jsonArray.size();
						List<tx> txList = gson.fromJson(jsonArray, new TypeToken<List<tx>>() {
						}.getType());
						if (txList != null) {
							for (int i = 0; i < txList.size(); i++) {
								tx t = txList.get(i);
								int txSize = txList.size();
								insertOrUpdateTx(con, t);

								if (i == txSize - 1) {
									me.setValue(t.seq);
								}
							}
							if (txList.size() == limit) {
								skip++;
								continue;
							} else {
								error = totalRecordBasedOnAddress + " tx inserted/updated on wallet [" + wallet.address
										+ "]";
								result.add(error);
								break;
							}
						} else {
							break;
						}
					} else {
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBClose(con);
		}
		return result;
	}

	public void getWalletInfo(Connection con, HashMap<wallet, Integer> hmap2) throws SQLException {
		String sql;
		wallet wallet;
		int maxSeqNum;
		sql = "SELECT u.email, w.*, ifnull(a.maxSeqNumber, 0) maxSeqNum FROM system_user u, wallets w LEFT JOIN( SELECT t._from, max(t.seq) AS maxSeqNumber FROM tx t GROUP BY t._from) a ON w.address = a._from WHERE u.email = w.email AND u.is_user_logged_in = 1; ";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			wallet = new wallet();
			maxSeqNum = rs.getInt("maxSeqNum");
			wallet.address = rs.getString("address");
			wallet.publicKey = rs.getString("public_key");
			hmap.put(wallet, maxSeqNum);
		}
	}

	private void insertOrUpdateTx(Connection con, tx tx) throws SQLException {
		String sql;
		sql = "insert into tx (_id,seq,amount,fee,fee_asset,hash,prev_hash,nonce,_from,_to,asset,action_time,completion_time,confirm_rate,[desc],[group],status,type,subtype) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) on conflict (_id) do update set " + "amount ="
				+ tx.amount + ",fee='" + tx.fee + "'" + ",fee_asset='" + tx.fee_asset + "'" + ",hash='" + tx.hash + "'"
				+ ",prev_hash='" + tx.prev_hash + "'" + ",nonce='" + tx.nonce + "'" + ",action_time=" + tx.action_time
				+ ",completion_time=" + tx.complete_time + ",_from='" + tx.wallet + "'" + ",_to='" + tx.to + "'"
				+ ",asset='" + tx.asset + "'" + ",[group]=" + tx.group + ",seq=" + tx.seq + ",[desc]='" + tx.desc + "'"
				+ ",confirm_rate=" + tx.confirmRate + ",status=" + tx.status + ",type=" + tx.type + ",subtype="
				+ tx.subtype;
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, tx._id); // id
		pstmt.setInt(2, tx.seq);// seq
		pstmt.setString(3, String.valueOf(tx.amount));
		pstmt.setString(4, tx.fee);// fee
		pstmt.setString(5, tx.fee_asset);// fee_asset
		pstmt.setString(6, tx.hash);// hash
		pstmt.setString(7, tx.prev_hash);// prev_hash
		pstmt.setString(8, tx.nonce);// nonce
		pstmt.setString(9, tx.wallet);// from
		pstmt.setString(10, tx.to); // to
		pstmt.setString(11, tx.asset);// asset
		pstmt.setString(12, String.valueOf(tx.action_time));// action time
		pstmt.setString(13, String.valueOf(tx.complete_time));// completion_time
		pstmt.setString(14, tx.confirmRate);// confirmRate
		pstmt.setString(15, tx.desc);// desc
		pstmt.setString(16, tx.group);// group
		pstmt.setInt(17, tx.status);// status
		pstmt.setInt(18, tx.type);// type
		pstmt.setInt(19, tx.subtype);// subtype
		pstmt.executeUpdate();
	}

	private void deleteOutdatedWallets(Connection con, HashSet<String> addresses) throws Exception {
		Statement statement = con.createStatement();
		for (String address : addresses) {
			String query = "delete from outdated_wallet where address='" + address + "'";
			statement.addBatch(query);
		}
		statement.executeBatch();
		statement.close();
	}

	private void insertOutdatedWallet(Connection conn, HashSet<String> addresses) throws SQLException {
		Statement statement = conn.createStatement();
		for (String address : addresses) {
			int a = 0;
			String query = "insert or ignore into outdated_wallet (address) values('" + address + "')";
			statement.addBatch(query);
		}
		statement.executeBatch();
		statement.close();
	}

	public JsonArray test(String[] params) {
		JsonArray ja = new JsonArray();
		ja.add("BAÞARILI");
		return ja;
	}

	public JsonArray logout(String[] params) throws Exception {
		Connection con = null;
		JsonArray result = new JsonArray();
		try {
			con = this.connect();
			system.disableAllUserLoginStatus(con);
			system.shouldThreadContinueToWork = false;
			result.add(SikkeConstant.LOGOUT_PERFORMED);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		}
		return result;
	}

	public JsonArray login(String[] params) throws Exception {
		String username = null;
		String password = null;
		Connection conn = null;
		List<User> userList = new ArrayList<>();
		JsonArray result = new JsonArray();
		Gson g = new Gson();
		User user = null;
		User userFromService = null;

		try {
			if (params != null) {
				if (params.length == 2) {
					for (String param : params) {
						String[] criterias = replaceSpaceAndSplit(param);
						String key = criterias[0].toLowerCase();
						String value = criterias[1];
						if (key.equals(SikkeConstant.EMAIL)) {
							username = value;
							continue;
						}
						if (key.equals(SikkeConstant.PASSWORD)) {
							password = value;
							continue;
						}
					}
				} else {
					result.add(SikkeConstant.INCORRECT_PARAMETER_SET);
					return result;
				}
			}
			if (username == null) {
				result.add(SikkeConstant.USER_REQUIRED);
				return result;
			}
			if (password == null) {
				result.add(SikkeConstant.PASSWORD_COULD_NOT_FOUND);
				return result;
			}
			conn = this.connect();
			system.getActiveUsers(conn, userList);
			if (userList.size() == 0) {
				String response = system.getAccessToken(username, password);
				user = g.fromJson(response.toString(), User.class);
				if (user.status.equals(SikkeConstant.STATUS_SUCCESS)) {

					byte[] pin_byte = AES256Cipher.getRandomAesCryptKey();
					byte[] iv_byte = AES256Cipher.getRandomAesCryptIv();
					user.crypt_key = AppHelper.toHexString(pin_byte);
					user.crypt_iv = AppHelper.toHexString(iv_byte);
					user.encrypted_password = AES256Cipher.encrypt(pin_byte, iv_byte, password);
					String passwordNew = AES256Cipher.decrypt(pin_byte, iv_byte, user.encrypted_password);

					user.is_user_logged_in = true;
					system.saveOrUpdateUser(conn, user);
					system.shouldThreadContinueToWork = true;
					// jsonArray.add(SikkeConstant.USER_COULD_NOT_FOUND);
					result.add(SikkeConstant.YOU_HAVE_LOGGED_IN_SUCCESSFULLY);
					return result;
				} else {
					result.add(SikkeConstant.USER_COULD_NOT_FOUND);
					return result;
				}
			} else if (userList.size() == 1) {
				user = userList.get(0);
				String plainPassword = user.getPassword();
				if (user.email.equals(username) && password.equals(plainPassword)) {
					String response = system.getAccessToken(user.email, plainPassword);
					userFromService = g.fromJson(response.toString(), User.class);
					if (userFromService.status.equals(SikkeConstant.STATUS_SUCCESS)) {
						user.access_token = userFromService.access_token;
						user.refresh_token = userFromService.refresh_token;
						user.is_user_logged_in = true;
						system.saveOrUpdateUser(conn, user);
					}
					result.add(SikkeConstant.YOU_HAVE_ALREADY_LOGGED_IN);
				} else {
					result.add(SikkeConstant.ANOTHER_USER_HAVE_ALREADY_LOGGED_IN);
				}
				system.shouldThreadContinueToWork = true;
				return result;
			} else if (userList.size() > 1) {
				system.disableAllUserLoginStatus(conn);
				String response = system.getAccessToken(username, password);
				userFromService = g.fromJson(response.toString(), User.class);

				if (userFromService.status.equals(SikkeConstant.STATUS_SUCCESS)) {

					byte[] pin_byte = AES256Cipher.getRandomAesCryptKey();
					byte[] iv_byte = AES256Cipher.getRandomAesCryptIv();

					user.crypt_iv = AppHelper.toHexString(iv_byte);
					user.crypt_key = AppHelper.toHexString(pin_byte);
					user.encrypted_password = AES256Cipher.encrypt(pin_byte, iv_byte, password);
					user.is_user_logged_in = true;
					user.access_token = userFromService.access_token;
					user.refresh_token = userFromService.refresh_token;
					system.saveOrUpdateUser(conn, user);

					system.shouldThreadContinueToWork = true;
					result.add(SikkeConstant.YOU_HAVE_LOGGED_IN_SUCCESSFULLY);
					return result;
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return result;
	}

	public JsonArray register(String[] params) throws Exception {
		JsonArray result = new JsonArray();
		Connection con = null;
		User user = null;
		String username = null;
		String password = null;
		StringBuilder sbPostQuery = new StringBuilder();
		Gson g = new Gson();
		try {
			con = this.connect();
			user = getOnlyActiveUser(result, con);
			if (user != null) {
				result = new JsonArray();
				result.add("A user has already logged in. You must logout to register.");
				return result;
			} else {
				result = new JsonArray();
			}
			for (int i = 0; i < params.length; i++) {
				String param = params[i];
				String[] criterias = replaceSpaceAndSplit(param);
				String key = criterias[0].toLowerCase();
				String value = criterias[1];

				if (key.equals(SikkeConstant.EMAIL)) {
					username = value;
					continue;
				} else if (key.equals(SikkeConstant.PASSWORD)) {
					password = value;
					continue;
				}
			}
			if (username == null) {
				result.add("Username cannot be empty.");
			}
			if (password == null) {
				result.add("Password cannot be empty.");
			}
			sbPostQuery.append("email=").append(username).append("&password=").append(password)
					.append("&password_confirm=").append(password);

			String response = helper.sendPost("/v1/auth/signup", sbPostQuery.toString(), null);
			UserResponse userResponse = g.fromJson(response.toString(), UserResponse.class);
			if (userResponse != null) {
				if (userResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
					user = userResponse.user;
					byte[] pin_byte = AES256Cipher.getRandomAesCryptKey();
					byte[] iv_byte = AES256Cipher.getRandomAesCryptIv();
					user.crypt_key = AppHelper.toHexString(pin_byte);
					user.crypt_iv = AppHelper.toHexString(iv_byte);
					user.crypt_key = AppHelper.toHexString(pin_byte);
					user.encrypted_password = AES256Cipher.encrypt(pin_byte, iv_byte, password);
					user.user_id = user._id;
					system.saveOrUpdateUser(con, user);
					result.add("You have successfully registered. You must be logged in to use the system.");

					WalletKey walletKey = WalletKey.getWalletKeys();
					return createAccountAndSave(SikkeConstant.DEFAULT_WALLET, null, null, null, 1, walletKey, user,
							con);
				} else {
					result.add(userResponse.message);
					return result;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return result;
	}

	public JsonArray exportWallets(String[] params) throws Exception {

		Connection conn = null;
		String sql = null;
		User user = null;
		JsonArray result = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		FileWriter fileWriter = null;
		try {
			conn = this.connect();
			user = getOnlyActiveUser(result, conn);
			if (user == null) {
				return result;
			}
			sql = "select w.address,w.public_key,w.private_key from wallets w, system_user u where  w.email = u.email and u.is_user_logged_in = 1;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			JsonArray jsonArrayWallet = new JsonArray();
			jsonObject.add("wallets", jsonArrayWallet);
			int numberOfWallet = 0;
			while (rs.next()) {
				JsonObject jsonWallet = new JsonObject();
				jsonWallet.addProperty("private_key", rs.getString("private_key"));
				jsonWallet.addProperty("public_key", rs.getString("public_key"));
				jsonWallet.addProperty("wallet_address", rs.getString("address"));
				jsonArrayWallet.add(jsonWallet);
				numberOfWallet++;
			}
			String path = new File(".").getCanonicalPath() + File.separator;
			String file = path + SikkeConstant.FILE_NAME;
			fileWriter = new FileWriter(file);
			fileWriter.write(jsonObject.toString());
			result.add(numberOfWallet
					+ " wallets have been exported. Your exported wallets are written to the file \"wallets.skk\" at "
					+ path);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception(e);
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return result;
	}

	public JsonArray importWallets(String[] params) throws Exception {
		Connection con = null;
		User user = null;
		JsonArray result = new JsonArray();
		Gson gson = new Gson();
		FileReader fileReader = null;
		try {
			con = this.connect();
			user = getOnlyActiveUser(result, con);
			if (user == null) {
				return result;
			}
			String path = new File(".").getCanonicalPath() + File.separator;
			String file = path + SikkeConstant.FILE_NAME;
			File f = new File(file);
			if (f.exists() && !f.isDirectory()) {
				fileReader = new FileReader(file);
				JsonReader reader = new JsonReader(fileReader);
				JsonObject json = (JsonObject) new JsonParser().parse(reader);
				JsonArray jsonArray = (JsonArray) json.get("wallets");
				List<WalletFromFile> walletList = gson.fromJson(jsonArray, new TypeToken<List<WalletFromFile>>() {
				}.getType());

				if (walletList != null && walletList.size() > 0) {
					for (WalletFromFile wallet : walletList) {
						String sql = "INSERT INTO wallets (address, email, private_key, public_key) VALUES(?,?,?,?) on conflict (address) do update  set "
								+ " address='" + wallet.wallet_address + "'" + ", public_key='" + wallet.public_key
								+ "'" + ", private_key='" + wallet.private_key + "'";
						PreparedStatement pstmt = con.prepareStatement(sql);
						pstmt.setString(1, wallet.wallet_address);
						pstmt.setString(2, user.email);
						pstmt.setString(3, wallet.private_key);
						pstmt.setString(4, wallet.public_key);
						pstmt.executeUpdate();
					}
					result.add(walletList.size()
							+ " wallets were successfully imported from the \"wallets.skk\" file at " + path);
					isWalletCreated = true;
				} else {
					result.add("No wallet to import in file. Please check the file and try again.");
				}
			} else {
				result.add("The file to import is not found" + path
						+ " Please make sure the file to be imported is in the same directory as the jar file and try again.");
			}
		} catch (Exception e) {
			System.out.println(e);
			throw new Exception(e);
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
			if (con != null) {
				con.close();
			}
		}
		return result;
	}
}

class Balance {

	double balance;
}
