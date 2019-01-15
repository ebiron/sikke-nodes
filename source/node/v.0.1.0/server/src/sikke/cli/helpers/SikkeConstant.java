package sikke.cli.helpers;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class SikkeConstant {

	// Urls
	public static final String accessTokenUrl = "https://api.sikke.network/v1/oauth/token";
	public static final String refreshTokenUrl = "https://api.sikke.network/v1/oauth/refresh_token";
	public static final String registerUserUrl = "https://api.sikke.network/v1/auth/signup";
	public static final String queryBalanceUrl = "https://api.sikke.network/v1/wallet/all_asset_balance/";
	public static final String createWalletUrl = "https://api.sikke.network/v1/wallet/generate_wallet";

	public static final String GET_WALLET_BALANCE_URL = "/v1/wallet/balance/";
	public static final String SEND_TX_URL = "https://api.sikke.network/v1/wallet/balance/";

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
	// Request Types
	public static final String REQUEST_POST = "POST";
	public static final String REQUEST_PUT = "PUT";
	public static final String REQUEST_GET = "GET";

	public static final int QUERY_LIMIT = 100;
	public static final String MERGE_BALANCE_TEXT = "Merge balance operation";
	public static final Object DOUBLE_UNDERSCORE = "__";
	public static final long INTERVAL_PERIOD = 1000 * 10 * 1;
	public static final long THREAD_DELAY = 0;

	public static final String ALIAS_NAME = "alias_name";
	public static final String LIMIT_HOURLY = "limit_hourly";
	public static final String LIMIT_DAILY = "limit_daily";
	public static final String LIMIT_MAX_AMOUNT = "limit_max_amount";
	public static final String DEFAULT = "DEFAULT";

	public final static String USER_COULD_NOT_FOUND = "No such user was found in the system. Please check your login and try again. If you do not have an account, register with the system.";
	public final static String YOU_HAVE_LOGGED_IN_SUCCESSFULLY = "You have logged in successfully.";
	public static final String YOU_HAVE_ALREADY_LOGGED_IN = "You are already logged in. You can continue your operation.";
	public final static String LOGOUT_PERFORMED = "Logout operation completed successfully.";
	public static final String ANOTHER_USER_HAVE_ALREADY_LOGGED_IN = "Another user have already logged in. To continue with your acccount you must logout";
	public static final String INCORRECT_PARAMETER_SET = "Insufficient parameter set. email and password is required. Please check login information and try again.";
	public static final String WALLET_NOT_CREATED = "Wallet could not created.";

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
	public static final String PASSWORD_COULD_NOT_FOUND = "password is required. Please check the login information and try again.";
	public static final String DEFAULT_WALLET = "Default Wallet";
	public static final String LOGGED_IN_USER_NOT_FOUND = "Logged in user not found. You need to be logged in to make a transaction.Please use the help menu for help.";
	public static final String USER_REQUIRED = "email is required. Please check the login information and try again.";
	public static final String FILE_NAME = "wallets.skk";

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
		DecimalFormat df = new DecimalFormat("#########.##", otherSymbols);
		df.setRoundingMode(RoundingMode.DOWN);
		return df.format(number);
	}

}
