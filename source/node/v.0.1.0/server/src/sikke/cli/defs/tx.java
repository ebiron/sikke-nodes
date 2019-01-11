/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.defs;

public class tx {

	public String _id;
	public long action_time;
	public long complete_time;
	public double amount;
	public String asset;
	public String desc;
	public String fee;
	public String fee_asset;
	public String hash;
	public String nonce;
	public String prev_hash;
	public int status;
	public int subtype;
	public String to;
	public int type;
	public int seq;
	public String wallet;
	public String confirmRate;
	public String block;
	public String group;

	@Override
	public String toString() {
		return "tx [_id=" + _id + ", action_time=" + action_time + ", complete_time=" + complete_time + ", amount="
				+ amount + ", asset=" + asset + ", desc=" + desc + ", fee=" + fee + ", fee_asset=" + fee_asset
				+ ", hash=" + hash + ", nonce=" + nonce + ", prev_hash=" + prev_hash + ", status=" + status
				+ ", subtype=" + subtype + ", to=" + to + ", type=" + type + ", seq=" + seq + ", wallet=" + wallet
				+ ", confirmRate=" + confirmRate + ", block=" + block + ", group=" + group + "]";
	}

}
