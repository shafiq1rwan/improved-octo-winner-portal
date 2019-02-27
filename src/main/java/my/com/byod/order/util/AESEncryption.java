package my.com.byod.order.util;

import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {

	private final static String INIT_VECTOR = "1234567890123456";
	private final static String SALT = "AbCdEfgH1@3$5^7*";
	private final static String SECRET_KEY = "8y0DtH3s3Cr3Tk3Y";

	public static String encrypt(String value) {
		try {
			Security.setProperty("crypto.policy", "unlimited");
			IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes());

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec genSecretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, genSecretKey, iv);
			return Base64.getUrlEncoder().encodeToString(cipher.doFinal(value.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String encrypted) {
		try {
			Security.setProperty("crypto.policy", "unlimited");
			IvParameterSpec ivspec = new IvParameterSpec(INIT_VECTOR.getBytes());

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec genSecretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, genSecretKey, ivspec);
			return new String(cipher.doFinal(Base64.getUrlDecoder().decode(encrypted)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String delimiter = "|;";
		String value = 1 + delimiter + 1 + delimiter + 1 + delimiter + 123456 + delimiter + "2019-02-27T095400";

		String encrypt = encrypt(value);
		System.out.println(encrypt);
		String decrypt = decrypt(encrypt);
		System.out.println(decrypt);
	}
}