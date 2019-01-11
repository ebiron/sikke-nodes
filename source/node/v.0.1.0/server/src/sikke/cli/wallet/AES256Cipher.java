package sikke.cli.wallet;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Cipher {

	public static byte[] getRandomAesCryptKey() {
		try {
			Random random = new Random((new Date()).getTime());
			byte[] salt = new byte[8];
			random.nextBytes(salt);
			MessageDigest sha256Hash = MessageDigest.getInstance("SHA-256");
			sha256Hash.update(salt);

			return sha256Hash.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

			return null;
		}
	}

	public static byte[] twiceHash256(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(data.getBytes());

//		Log.v("sha256", toHexString(md.digest()));

		return md.digest();
	}

	public static byte[] getRandomAesCryptIv() {
		byte[] randomBytes = new byte[16];
		new SecureRandom().nextBytes(randomBytes);

		return new IvParameterSpec(randomBytes).getIV();
	}

	public static String encrypt(byte[] aesCryptKey, byte[] aesCryptIv, String plainText)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		AlgorithmParameterSpec ivSpec = new IvParameterSpec(aesCryptIv);
		SecretKeySpec newKey = new SecretKeySpec(aesCryptKey, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

		String result = Base64.encodeToString(cipher.doFinal(plainText.getBytes(Charset.forName("UTF-8"))), Base64.NO_WRAP); 
		return result;
	}

	public static String decrypt(byte[] aesCryptKey, byte[] aesCryptIv, String cipherText)
			throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		AlgorithmParameterSpec ivSpec = new IvParameterSpec(aesCryptIv);
		SecretKeySpec newKey = new SecretKeySpec(aesCryptKey, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

		return new String(cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP)), "UTF-8");
	}

	public static byte[] key128Bit(String pin_code) throws Exception {
		byte[] keyBytes;
		keyBytes = pin_code.getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		keyBytes = sha.digest(keyBytes);
//		keyBytes = Arrays.copyOf(keyBytes, 16); // use only first 128 bit

		return keyBytes;
	}

	public static String encryptPvt(byte[] key, String message) throws Exception {

		byte[] iv = AES256Cipher.getRandomAesCryptIv();
		String encryptedData = AES256Cipher.encrypt(key, iv, message);
		byte[] encryptedDataBytes = Base64.decode(encryptedData, Base64.DEFAULT);
		encryptedData = Base64.encodeToString(addAll(iv, encryptedDataBytes), Base64.DEFAULT);
		return encryptedData;
	}

	public static String decryptPvt(byte[] key, String encryptedMsg) throws Exception {
		byte[] encryptedMsgByte = Base64.decode(encryptedMsg, Base64.DEFAULT);
		byte[] ivByte = Arrays.copyOfRange(encryptedMsgByte, 0, 16);
		byte[] msgByte = Arrays.copyOfRange(encryptedMsgByte, 16, encryptedMsgByte.length);

		String decryptedMsg = AES256Cipher.decrypt(key, ivByte, Base64.encodeToString(msgByte, Base64.DEFAULT));

		return decryptedMsg;
	}

	public static byte[] addAll(final byte[] array1, byte[] array2) {
		byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

}
