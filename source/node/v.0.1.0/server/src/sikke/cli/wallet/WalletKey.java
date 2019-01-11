package sikke.cli.wallet;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.jcajce.provider.symmetric.ARC4.Base;

import sikke.cli.helpers.SikkeConstant;

public class WalletKey {

	private String publicKey;
	private String privateKey;
	private String address;

	public WalletKey() {

	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public static final WalletKey getWalletKeys() {
		WalletKey walletKey = null;
		ECDSAHelper ecdsa = new ECDSAHelper();
		try {

			String privateKeyBase58 = ecdsa.generatePrivateKey();
			String publicKeyBase58 = ecdsa.generatePublicKey();

			String decodedPrivateKey = AppHelper.toHexString(Base58.decode(privateKeyBase58));
			String decodedPublicKey = AppHelper.toHexString(Base58.decode(publicKeyBase58));

			String walletNumber = getWalletAddressFromPublicAndPrivateKey(decodedPrivateKey, decodedPublicKey);
			walletKey = new WalletKey();
			walletKey.setPrivateKey(privateKeyBase58);
			walletKey.setPublicKey(publicKeyBase58);
			walletKey.setAddress(walletNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return walletKey;
	}

	private static String getWalletAddressFromPublicAndPrivateKey(String decodedPrivateKey, String decodedPublicKey)
			throws NoSuchAlgorithmException {
		byte[] decodedPrivateKeyByteArray = AppHelper.hexStringToByteArray(decodedPrivateKey);
		byte[] decodedPublicKeyByteArray = AppHelper.hexStringToByteArray(decodedPublicKey);

		byte[] addressBytes = decodedPublicKeyByteArray;
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] sha256Digest = digest.digest(addressBytes);

		RIPEMD160Digest digest2 = new RIPEMD160Digest();
		digest2.update(sha256Digest, 0, sha256Digest.length);

		byte[] payload = new byte[digest2.getDigestSize() + 1];

		payload[0] = 0x00;
		byte[] accountId = new byte[digest2.getDigestSize()];
		digest2.doFinal(accountId, 0);
		System.arraycopy(accountId, 0, payload, 1, accountId.length);

		byte[] sha256Digest1 = digest.digest(payload);
		byte[] sha256Digest2 = digest.digest(sha256Digest1);

		byte[] checksum = new byte[4];
		System.arraycopy(sha256Digest2, 0, checksum, 0, 4);
		byte[] dataToEncode = new byte[checksum.length + payload.length];

		System.arraycopy(payload, 0, dataToEncode, 0, payload.length);
		System.arraycopy(checksum, 0, dataToEncode, payload.length, checksum.length);

		String address = Base58.encode(dataToEncode);
		String walletNumber = SikkeConstant.WALLET_TYPE_SKK + address;
		// System.out.println("\n\n\nprivateKeyBase58 : " + privateKeyBase58 +
		// "\npublicKeyBase58 : " + publicKeyBase58 + "\nwalletNumber : " +
		// walletNumber);
		return walletNumber;
	}

	public static final WalletKey getWalletKeysFromPrivateKey(String encodedPrivateKey) {

		WalletKey walletKey = new WalletKey();
		ECDSAHelper ecdsaHelper = new ECDSAHelper();
		try {
			byte[] decodedPrivateKeyByteArr = Base58.decode(encodedPrivateKey);
			String publicKey = ecdsaHelper.publicKeyFromPrivate(new BigInteger(1, decodedPrivateKeyByteArr));
			byte[] publicKeyByteArr = AppHelper.hexStringToByteArray(publicKey);

			String decodedPrivateKey = AppHelper.toHexString(decodedPrivateKeyByteArr);
			String decodedPublicKey = AppHelper.toHexString(publicKeyByteArr);

			String walletAddress = getWalletAddressFromPublicAndPrivateKey(decodedPrivateKey, decodedPublicKey);
			walletKey.setAddress(walletAddress);
			walletKey.setPrivateKey(encodedPrivateKey);
			walletKey.setPublicKey(Base58.encode(publicKeyByteArr));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return walletKey;
	}
}
