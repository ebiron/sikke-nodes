/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.defs;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sikke.cli.wallet.AES256Cipher;
import sikke.cli.wallet.AppHelper;

/**
 *
 * @author selim
 */
public class User {
	public wallet wallet;
	public String access_token;
	public String alias_name;
	public String user_id;
	public String _id;
	public String email;
	public String name;
	public String surname;
	public String refresh_token;
	public int rt_expires_in;
	public String status;
	public int expires_in;
	public String token_type;
	public String crypt_key;
	public String crypt_iv;
	public String encrypted_password;
	public boolean is_user_logged_in;

	public String password;

	public String getPassword() throws Exception {
		try {
			password = AES256Cipher.decrypt(AppHelper.hexStringToByteArray(crypt_key),
					AppHelper.hexStringToByteArray(crypt_iv), encrypted_password);
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			
			e.printStackTrace();
			throw new Exception(e);
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
