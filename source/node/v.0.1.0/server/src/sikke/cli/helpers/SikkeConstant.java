package sikke.cli.helpers;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SikkeConstant {

	// Urls
	public static final String GET_WALLET_BALANCE_URL = "/v1/wallet/balance/";
	public static final String GET_TX = "/v1/tx";
	public static final String SEND_TX = "/v1/tx";
	public static final String SIGNUP = "/v1/auth/signup";
	public static final String USER_WALLETS = "/v1/wallet/user_wallets";

	// Status
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_ERROR = "error";

	// Agent
	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String DB_URL = "jdbc:sqlite:sikkeClient.db";

	// Wallet Type
	public static final String WALLET_TYPE_SKK = "SKK";

	// Tx Query Types
	public static final String TX_QUERY_TYPE_HASH = "hash";
	public static final String TX_QUERY_TYPE_BLOCK = "block";
	public static final String TX_QUERY_TYPE_ADDRESS = "address";
	public static final String TX_QUERY_TYPE_SEQUENCE = "seq";
	public static final String SEPERATOR = ":";

	public static final String DEFAULT_ASSET = "SKK";

	public static final int QUERY_LIMIT = 100;
	public static final int TX_QUERY_LIMIT = 3000;
	public static final String MERGE_BALANCE_TEXT = "Merge balance operation";
	public static final Object DOUBLE_UNDERSCORE = "__";
	public static final long INTERVAL_PERIOD = 1000 * 60 * 1;
	public static final long THREAD_DELAY = 0;

	public static final String ALIAS_NAME = "alias_name";
	public static final String LIMIT_HOURLY = "limit_hourly";
	public static final String LIMIT_DAILY = "limit_daily";
	public static final String LIMIT_MAX_AMOUNT = "limit_max_amount";
	public static final String CALLBACK_URL = "callback_url";
	public static final String DEFAULT = "DEFAULT";

	public final static String USER_COULD_NOT_FOUND = "No such user was found in the system. Please check your login and try again. If you do not have an account, register with the system.";
	public final static String YOU_HAVE_LOGGED_IN_SUCCESSFULLY = "You have logged in successfully.";
	public static final String YOU_HAVE_ALREADY_LOGGED_IN = "You are already logged in. You can continue your operation.";
	public final static String LOGOUT_PERFORMED = "Logout operation completed successfully.";
	public static final String ANOTHER_USER_HAVE_ALREADY_LOGGED_IN = "Another user have already logged in. To continue with your acccount you must logout";
	public static final String INCORRECT_PARAMETER_SET = "Insufficient parameter set. email and password is required. Please check login information and try again.";
	public static final String WALLET_NOT_CREATED = "Wallet could not created.";
	public static final String YOU_HAVE_SUCCESSFULLY_REGISTERED_YOU_MUST_BE_LOGGED_IN_TO_USE_THE_SYSTEM = "You have successfully registered. You must be logged in to use the system.";
	public static final String PASSWORD_CANNOT_BE_EMPTY = "Password cannot be empty.";
	public static final String USERNAME_CANNOT_BE_EMPTY = "Username cannot be empty.";

	public static final String TEXT_FROM = "from";
	public static final String TEXT_TO = "to";
	public static final String TEXT_ASSET = "asset";
	public static final String TEXT_AMOUNT = "amount";
	public static final String TEXT_DESC = "desc";
	public static final String TEXT_HIDDEN = "hidden";
	public static final String ADDRESS = "address";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";
	public static final String PASSWORD_COULD_NOT_FOUND = "Password is required. Please check the login information and try again.";
	public static final String DEFAULT_WALLET = "Default Wallet";
	public static final String LOGGED_IN_USER_NOT_FOUND = "Logged in user not found. You need to be logged in to make a transaction.Please use the help menu for help.";
	public static final String USER_REQUIRED = "Email is required. Please check the login information and try again.";
	public static final String FILE_NAME = "wallets.skk";

	public static final String TX_PARAMS_SKIP = "skip";
	public static final String TX_PARAMS_LIMIT = "limit";
	public static final String TX_PARAMS_WALLET = "wallet";
	public static final String TX_PARAMS_ASSET = "asset";
	public static final String TX_PARAMS_TYPE = "type";
	public static final String TX_PARAMS_SUBTYPE = "subtype";
	public static final String TX_PARAMS_STATUS = "status";
	public static final String TX_PARAMS_SORT = "sort";
	public static final String TX_PARAMS_SEQ_GT = "seq_gt";
	public static final String TX_PARAMS_WALLETS = "wallets";
	public static final String TX_PARAMS_PUBLIC_KEY = "public_key";
	public static final String TX_PARAMS_FROM_DATE = "from_date";
	public static final String TX_PARAMS_TO_DATE = "to_date";
	public static final String TX_PARAMS_USER_ID = "user_id";
	public static final String TX_PARAMS_SEQ = "seq";
	public static final String TX_PARAMS_GROUP = "group";

	public static java.sql.Date getCurrentDate() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Date(today.getTime());
	}

	public static long getEpochTime() {
		return System.currentTimeMillis() / 1000;
	}

	public static String centerString(String s) {
		int width = 30;
		int padSize = width - s.length();
		int padStart = s.length() + padSize / 2;
		return String.format("%" + padStart + "s", s);
	}

	public static String formatAmount(String amount_text) {
		if (amount_text.contains(".")) {
			String last_string = amount_text.substring(amount_text.length() - 1);

			while (last_string.equals("0")) {
				amount_text = amount_text.substring(0, amount_text.length() - 1);
				last_string = amount_text.substring(amount_text.length() - 1);
			}
			if (last_string.equals(".")) {
				amount_text = amount_text.substring(0, amount_text.length() - 1);
			}
		}
		return amount_text;
	}

	public static String formatNumber(double number) {

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#########.###", otherSymbols);
		df.setRoundingMode(RoundingMode.DOWN);
		return df.format(number);
	}

	public static long stringDateToEpochTime(String str) {

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		Date date = null;
		try {
			date = df.parse(str);
			return date.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	static final String YOU_HAVE_ALREADY_REGISTERED_YOU_MUST_LOGIN_TO_OPERATE_YOUR_WALLET_OPERATION = "You have already registered. You must login to operate your wallet operation.";

}
