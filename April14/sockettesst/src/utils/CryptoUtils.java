package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CryptoUtils {
	
	
	/**
	 * Get 128 bits nonce
	 * @return byte
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] generateNonce() throws UnsupportedEncodingException, NoSuchAlgorithmException { 
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] bytes = new byte[128/8];
	    random.nextBytes(bytes);
	    return bytes;
	}
	
	
	  /**
     * Convert Map<String, byte[]> to byte 
     * @param Map<String, byte[]>
     * @return byte[]
     */
    public static byte[] mapToByte(Map<String, byte[]> map){
    	Gson gson = new Gson();
    	String str = gson.toJson(map,  new TypeToken<HashMap<String, byte[]>>(){}.getType());
    	return str.getBytes(Charset.forName("UTF-8"));
    }
    
    /**
     * Convert byte to Map<String, byte[]>
     * @param byte
     * @return Map<String, byte[]>
     */
    public static HashMap<String, byte[]> mapFromByte(byte[] b){
    	String json;
		try {
			json = new String(b, "UTF-8");
			Gson gson = new Gson();
			HashMap<String, byte[]> map = gson.fromJson(json, new TypeToken<HashMap<String, byte[]>>(){}.getType());
	    	return map;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("MapFromByte Encoding error");
			return null;
		}
    }
    
	/**
	 * Generate aes secret key
	 * @return AES key
	 * @throws NoSuchAlgorithmException
	 */
	public static Key generateAESKey(byte[] bytes) throws NoSuchAlgorithmException {
		SecretKey key = new SecretKeySpec(bytes, "AES");
		return key;
	}
    
	/**
	 * AES Encrypt
	 * @param data
	 * @param AES key
	 * @return encrypted data
	 * @throws Exception
	 */
    public static byte[] encryptByAES(byte[] data, Key key){  
        Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		System.out.println("Encrypting error!");
		return data;
    } 
    
	/**
	 * AES Decrypt
	 * @param data
	 * @param AES key
	 * @return encrypted data
	 * @throws Exception
	 */
    public static byte[] decryptByAES(byte[] data, Key key){  
    	try{
    		Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");  
    		cipher.init(Cipher.DECRYPT_MODE, key);  
    		return cipher.doFinal(data);  
	    } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		System.out.println("Decrypting error!");
		return data;
    }  
    
	/**
	 * AES Decrypt
	 * @param data
	 * @param AES key
	 * @return derypted data
	 * @throws Exception
	 */
    public static byte[] decryptByAES(byte[] data, byte[] key){  
     
		try {
			SecretKey secretKey=new SecretKeySpec(key,"AES");  
		    Cipher cipher;
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}  
		System.out.println("Decryption error!");
		return data;
    } 
    
	/**
	 * Load private key from keyfile
	 * @param privateKeyFileName
	 * @return  PrivateKey
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String privateKeyFileName) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		byte[] privateKey = FileUtils.toByteArray(privateKeyFileName);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
		PrivateKey key = rsaKeyFactory.generatePrivate(privateKeySpec);
		System.out.println("private key is generated");
		return key;
	}
	
	/**
	 * Load public key from keyfile
	 * @param publicKeyFileName
	 * @return PublicKey
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String publicKeyFileName) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		byte[] publicKey = FileUtils.toByteArray(publicKeyFileName);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
		PublicKey key = rsaKeyFactory.generatePublic(publicKeySpec);
		System.out.println("public key is generated");
		return key;
	}
	
	/**
	 * Encyrption with RSA public key
	 * @param plaintext
	 * @param publicKey
	 * @return cipherdata
	 * @throws Exception
	 */
	public static byte[] encryptByRSAPublicKey(byte[] data, Key publicKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
	}
	
	/**
	 * Decryption with RSA private key
	 * @param cipherdata
	 * @param privateKey
	 * @return plaintext
	 * @throws Exception
	 */
	public static byte[] decryptByRSAPrivateKey(byte[] data, Key privateKey)throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
	}
	
	/**
	 * Generate 512-bits DH key pair
	 */
	public static Map<String, Key> generateDHKey() throws Exception{  
		KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("DH");  
        keyPairGenerator.initialize(512);  
        KeyPair keyPair=keyPairGenerator.generateKeyPair();  
        DHPublicKey publicKey=(DHPublicKey) keyPair.getPublic();  
        DHPrivateKey privateKey=(DHPrivateKey) keyPair.getPrivate();  
        Map<String,Key> keyMap = new HashMap<String,Key>();  
        keyMap.put("public_key", publicKey);  
        keyMap.put("private_key", privateKey);  
        return keyMap;  
    } 
	
	/**
	 * Hash with salt using SHA-256
	 * @param string
	 * @param salt
	 * @return hash String
	 */
	public static String getSaltHash(byte[] string, byte[] salt)
	{
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(string);
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
	
	
	public static byte[] getSaltHashByteArr(byte[] string, byte[] salt)
	{
		byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            bytes = md.digest(string);

        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return bytes;
    }
	
	
	/**
	 * Generate 512-bits DH key pair from input key 
	 * @param public key from the other side
	 * @return Map<keyname, key>
	 * @throws Exception
	 */
	public static Map<String,Key> generateDHKey(byte[] key) throws Exception{  
	    X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(key);  
	    KeyFactory keyFactory=KeyFactory.getInstance("DH");  
	    PublicKey pubKey=keyFactory.generatePublic(x509KeySpec);  
	    DHParameterSpec dhParamSpec=((DHPublicKey)pubKey).getParams();  
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(keyFactory.getAlgorithm());  
        keyPairGenerator.initialize(dhParamSpec);  
        KeyPair keyPair=keyPairGenerator.genKeyPair();  
        DHPublicKey publicKey=(DHPublicKey)keyPair.getPublic();  
        DHPrivateKey privateKey=(DHPrivateKey)keyPair.getPrivate();  
        Map<String,Key> keyMap=new HashMap<String,Key>();  
        keyMap.put("public_key", publicKey);  
        keyMap.put("private_key", privateKey);  
        return keyMap;  
	}
	
	/**
	 * Generate Session Key
	 * @return session key
	 * @throws IllegalStateException 
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	public static byte[] generateSessionKey(byte[] publicKey, byte[] privateKey) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, InvalidKeySpecException{
		
		KeyFactory keyFactory=KeyFactory.getInstance("DH");  
        X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(publicKey);  
        PublicKey pubKey=keyFactory.generatePublic(x509KeySpec);  
        PKCS8EncodedKeySpec pkcs8KeySpec=new PKCS8EncodedKeySpec(privateKey);  
        PrivateKey priKey=keyFactory.generatePrivate(pkcs8KeySpec);  
        KeyAgreement keyAgree=KeyAgreement.getInstance(keyFactory.getAlgorithm());  
        keyAgree.init(priKey);  
        keyAgree.doPhase(pubKey, true);  
        SecretKey secretKey=keyAgree.generateSecret("AES");  
        return secretKey.getEncoded();  
	}
	
	
	public static void main(String[] args) throws Exception {
	
		byte[] a = new byte[16];
		a = new String("ABCDEFG").getBytes();
		byte[] encrypted = CryptoUtils.encryptByRSAPublicKey(a, CryptoUtils.getPublicKey("public.der"));
		byte[] cipher = new byte[16];
		cipher = CryptoUtils.decryptByRSAPrivateKey(encrypted, CryptoUtils.getPrivateKey("private.der"));
		
		System.out.println("before"+ new String(a));
		System.out.println("after" + new String(cipher));
		String astr = new String(a, "UTF-8");
		String bstr = new String(cipher, "UTF-8");
		System.out.println("length" + a.length);
		System.out.println("length" + cipher.length);
		if(astr.equals(bstr)){
			System.out.println("True");
		}

		encrypted = CryptoUtils.encryptByRSAPublicKey("hello".getBytes(), CryptoUtils.getPublicKey("public.der"));
		cipher = CryptoUtils.decryptByRSAPrivateKey(encrypted, CryptoUtils.getPrivateKey("private.der"));
		
		System.out.println("length" + "hello".getBytes().length);
		System.out.println("length" + encrypted.length);
		System.out.println("length" + cipher.length);
		
		String str = new String(cipher, "UTF-8");
		if(str.equals("Hello")){
		System.out.println(new String(cipher, "UTF-8"));
		}
	}

}