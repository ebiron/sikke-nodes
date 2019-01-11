/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sikke.cli.helpers.Methods;

/**
 *
 * @author mumbucoglu
 */
class JSONRPC {

	Methods methods = new Methods();

	public JsonArray Methods(String method, String[] params) throws Exception {
		String result = null;
		JsonArray rs = null;
		switch (method) {
		case "createWallet":
			rs = methods.createWallet(params);
			break;
		case "createWalletAndSave":
			rs = methods.createWalletAndSave(params);
			break;
		case "syncWallet":
			rs = methods.syncWallet(params);
			break;
		case "getHistories":
			rs = methods.getHistories(params);
			break;
		case "listWallets":
			rs = methods.listWallets(params);
			break;
		case "getBalances":
			rs = methods.getBalance(params);
			break;
		case "send":
			rs = methods.send(params);
			break;
		case "importWallet":
			rs = methods.importWallet(params);
			break;
		case "makeDefault":
			rs = methods.makeDefault(params);
			break;
		case "mergeBalances":
			rs = methods.mergeBalance(params);
			break;
		case "repairTx":
			rs = methods.repairTx(params);
			break;
		case "syncTx":
			rs = methods.syncTx();
			break;
		case "help":
			rs = methods.help(params);
			break;
		case "test":
			rs = methods.test(params);
			break;
		case "login":
			rs = methods.login(params);
			break;
		case "register":
			rs = methods.register(params);
			break;
		case "logout":
			rs = methods.logout(params);
			break;
		case "exportWallets":
			rs = methods.exportWallets(params);
			break;
		case "importWallets":
			rs = methods.importWallets(params);
			break;
		default:
			result = "Invalid method!";
			rs.add(result);
			break;
		}
		return rs;
	}

}
