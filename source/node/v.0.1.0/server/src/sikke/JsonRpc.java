/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke;

import sikke.cli.helpers.Methods;
import sikke.cli.helpers._System;

/**
 *
 * @author mumbucoglu
 */
class JsonRpc {

	Methods methods = new Methods();

	public JsonRpcObject Methods(String method, String[] params) throws Exception {
		JsonRpcObject jsonRpcObject = null;
		switch (method) {
		case "createWallet":
			jsonRpcObject = methods.createWallet(params);
			break;
		case "createWalletAndSave":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.createWalletAndSave(params);
			}
			break;
		case "syncWallets":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.syncWallets(params, null);
			}
			break;
		case "getHistories":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.getHistories(params);
			}
			break;
		case "listWallets":
			jsonRpcObject = methods.listWallets(params);
			break;
		case "getBalances":
			jsonRpcObject = methods.getBalances(params);
			break;
		case "send":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.send(params);
			}
			break;
		case "importWallet":
			jsonRpcObject = methods.importWallet(params);
			break;
		case "makeDefault":
			jsonRpcObject = methods.makeDefault(params);
			break;
		case "mergeBalances":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.mergeBalance(params);
			}
			break;
		case "repairTx":
			jsonRpcObject = methods.repairTx(params);
			break;
		case "help":
			jsonRpcObject = methods.help(params);
			break;
		case "login":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.login(params);
			}
			break;
		case "register":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.register(params);
			}
			break;
		case "logout":
			jsonRpcObject = methods.logout(params);
			break;
		case "exportWallets":
			jsonRpcObject = methods.exportWallets(params);
			break;
		case "importWallets":
			jsonRpcObject = methods.importWallets(params);
			break;
		case "getTransactions":
			jsonRpcObject = _System.isSikkeAPIReachable();
			if (jsonRpcObject == null) {
				jsonRpcObject = methods.getTransactions(params);
			}
			break;
		default:
			jsonRpcObject = new JsonRpcObject();
			jsonRpcObject.error = new JsonRpcErrorObject(1, "Invalid command! Please see help menu below");
			break;
		}
		return jsonRpcObject;
	}

}
