/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.defs;

public class WalletResponse {
	public String status;
	public wallet wallet;

	@Override
	public String toString() {
		return "WalletResponse [status=" + status + ", wallet=" + wallet + "]";
	}
}
