package sikke.cli.helpers;

import static sikke.cli.helpers._System.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	private static Connection connection = null;

	private String url = system.getDB();

	private Connect() {
		try {
			// Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.err.println("Impossible de se connecter a la database");
			System.exit(0);
		} /*
			 * catch (ClassNotFoundException e) {
			 * System.err.println("Impossible de charger les Driver"); System.exit(0); }
			 */
	}

	public static Connection getConnect() {
		if (connection == null) {
			new Connect();
		}
		return connection;
	}
}