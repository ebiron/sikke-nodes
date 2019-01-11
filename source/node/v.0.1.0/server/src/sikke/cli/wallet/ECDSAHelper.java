package sikke.cli.wallet;



import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.util.Arrays;

import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;

public class ECDSAHelper {

	public static ECKeyHelper ecKeyHelper = new ECKeyHelper();
	public static ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
	public static final ECDomainParameters CURVE;
	public static final BigInteger HALF_CURVE_ORDER;
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public boolean isKeyCreated = false;

	static {
		Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
		X9ECParameters params = SECNamedCurves.getByName("secp256k1");
		CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
		HALF_CURVE_ORDER = params.getN().shiftRight(1);
	}

	public static KeyFactory kf() throws Exception {
		KeyFactory kf = KeyFactory.getInstance("ECDSA", "SC");
		return kf;
	}

	static private String adjustTo64(String s) {
		switch (s.length()) {
		case 62:
			return "00" + s;
		case 63:
			return "0" + s;
		case 64:
			return s;
		default:
			throw new IllegalArgumentException("not a valid key: " + s);
		}
	}

	public static KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
		keyGen.initialize(ecSpec);

		KeyPair kp = keyGen.generateKeyPair();

		return kp;
	}

	public String generatePublicKey() throws Exception {

		PublicKey pub;
		KeyPair kp = generateKeyPair();
		if (!isKeyCreated) {
			pub = kp.getPublic();
			privateKey = kp.getPrivate();
			isKeyCreated = true;
		} else {
			pub = publicKey;
		}

		ECPublicKey epub = (ECPublicKey) pub;
		ECPoint pt = epub.getW();
		String sx = adjustTo64(pt.getAffineX().toString(16));
		String sy = adjustTo64(pt.getAffineY().toString(16));
		String bcPub = sx + sy;
		//System.out.println("bcPub: " + bcPub);

		byte[] bytePub = AppHelper.hexStringToByteArray(bcPub);
		String pub58 = Base58.encode(bytePub);

		return pub58;
	}

	public String generatePrivateKey() throws Exception {
		PrivateKey pvt;
		KeyPair kp = generateKeyPair();

		if (!isKeyCreated) {
			pvt = kp.getPrivate();
			publicKey = kp.getPublic();
			isKeyCreated = true;

		} else {
			pvt = privateKey;
		}

		ECPrivateKey ecPrivateKeyvt = (ECPrivateKey) pvt;
		String strPrivate = adjustTo64(ecPrivateKeyvt.getS().toString(16));

		byte[] bytePvt = AppHelper.hexStringToByteArray(strPrivate);

		String pub58 = Base58.encode(bytePvt);

		//System.out.println("s[" + pub58.length() + "]: " + strPrivate);

		return pub58;
	}

	public static PrivateKey importPrivateKey(String privateKey) throws Exception {

		byte[] private_decode = Base58.decode(privateKey);

		BigInteger priv_big_integer = new BigInteger(1, private_decode);
		ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(priv_big_integer, spec);
		PrivateKey privKey = kf().generatePrivate(ecPrivateKeySpec);

		// to check private key is correct
		ECPrivateKey epvt = (ECPrivateKey) privKey;
		String sepvt = adjustTo64(epvt.getS().toString(16));
		System.out.println(sepvt);

		return privKey;
	}

	public static PublicKey importPublicKey(String publicKey) throws Exception {

		byte[] public_decode = Base58.decode(publicKey);

		ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
		java.security.spec.ECPoint w = new java.security.spec.ECPoint(
				new BigInteger(1, Arrays.copyOfRange(public_decode, 0, 32)),
				new BigInteger(1, Arrays.copyOfRange(public_decode, 32, 64)));
		PublicKey public_key = kf().generatePublic(new java.security.spec.ECPublicKeySpec(w, params));

		ECPublicKey epub = (ECPublicKey) public_key;
		ECPoint pt = epub.getW();
		String sx = adjustTo64(pt.getAffineX().toString(16));
		String sy = adjustTo64(pt.getAffineY().toString(16));
		String bcPub = sx + sy;
		//System.out.println("bcPub: " + bcPub);

		return public_key;
	}

	public static String publicKeyFromPrivate(BigInteger priv_big_integer) {

		byte[] pub_key = ecKeyHelper.publicKeyFromPrivate(priv_big_integer, false);
		String str_pub_key = AppHelper.toHexString(pub_key).substring(2);
		System.out.println(str_pub_key);

		return str_pub_key;
	}

	public static String sign(String str, PrivateKey privKey) throws Exception {

		Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
		ecdsaSign.initSign(privKey);
		ecdsaSign.update(str.getBytes("UTF-8"));
		byte[] signature = ecdsaSign.sign();
		System.out.println(new BigInteger(1, signature).toString(16));

		String sign = new BigInteger(1, signature).toString(16);

		return sign;
	}

	public static Boolean verify(String str, PublicKey publicKey, byte[] signature) throws Exception {

		Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
		ecdsaVerify.initVerify(publicKey);
		ecdsaVerify.update(str.getBytes("UTF-8"));
		boolean verify = ecdsaVerify.verify(signature);
		System.out.println(verify);
		return verify;
	}

}
