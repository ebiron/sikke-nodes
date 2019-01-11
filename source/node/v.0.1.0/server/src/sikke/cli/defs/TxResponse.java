/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.defs;

public class TxResponse {
	public String message;
	public String status;
	public tx tx;
	@Override
	public String toString() {
		return "TxResponse [status=" + status + ", tx=" + tx + "]";
	}

	

}
