package sikke.cli.wallet;

import java.security.MessageDigest;

import org.spongycastle.crypto.digests.RIPEMD160Digest;

public class EcdsaDeneme {


    public static void main(String[] args) {
       /* ECDSAHelper ecdsa = new ECDSAHelper();
        try {
            String privateKeyBase58 = ecdsa.generatePrivateKey();
            String publicKeyBase58 = ecdsa.generatePublicKey();

            String decodedPrivateKey = AppHelper.toHexString(Base58.decode(privateKeyBase58));
            String decodedPublicKey = AppHelper.toHexString(Base58.decode(publicKeyBase58));

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
            String walletNumber = "SKK" + address;
            System.out.println("\n\n\nprivateKeyBase58 : " + privateKeyBase58 + "\npublicKeyBase58 : " + publicKeyBase58 + "\nwalletNumber : " + walletNumber);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

*/
    }

}
