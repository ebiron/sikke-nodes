
package sikke.cli.defs;

public class WalletResponse {
	public String status;
	public wallet wallet;

	@Override
	public String toString() {
		return "WalletResponse [status=" + status + ", wallet=" + wallet + "]";
	}
}
