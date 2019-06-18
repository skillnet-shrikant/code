package com.firstdata.security;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.spec.DHParameterSpec;

public class EANEncryptionUtil {

	public static String byteArrayToHex(byte[] a) {
		   StringBuilder sb = new StringBuilder(a.length * 2);
		   for(byte b: a)
		      sb.append(String.format("%02x", b & 0xff));
		   return sb.toString().toUpperCase();
	}
	
	public static SecretKey generateSecret(PrivateKey privateKey, PublicKey publicKey,DHParameterSpec param, String algorithmToGenerateSecret) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException{
		KeyAgreement ka = KeyAgreement.getInstance(algorithmToGenerateSecret,new BouncyCastleProvider());
		ka.init(privateKey,param);
		ka.doPhase(publicKey, true);
		SecretKey secretKey=ka.generateSecret(algorithmToGenerateSecret);
		return secretKey;
		
	}
	
	
	public static String SHAsum(byte[] convertme, String algorithm) throws NoSuchAlgorithmException{
	    MessageDigest md = MessageDigest.getInstance(algorithm);
	    return EANEncryptionUtil.byteArrayToHex(md.digest(convertme));
	}
	
	public static byte[] adjustDESParity (String revisedKek) {
	 	byte[] bytes=hexToBytes(revisedKek);
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            bytes[i] = (byte)((b & 0xfe) | ((((b >> 1) ^ (b >> 2) ^ (b >> 3) ^ (b >> 4) ^ (b >> 5) ^ (b >> 6) ^ (b >> 7)) ^ 0x01) & 0x01));
        }
	 	return bytes;
    }
	
	public static byte[] hexToBytes(String string){
		int length = string.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character
                    .digit(string.charAt(i + 1), 16));
        }
        return data;
	}
	
	public static void printByteArrayString(byte[] a){
		for(byte b:a){
			System.out.print(b+":");
		}
	}
	
	public static String generateSharedSecret(BigInteger clPubKey,BigInteger merPrvKey, BigInteger p, String algorithm) throws NoSuchAlgorithmException{
		BigInteger sharedKey=clPubKey.modPow(merPrvKey,p);
		byte[] sharedKeyArr=sharedKey.toByteArray();
		byte[] revisedSharedKey=new byte[sharedKeyArr.length-1];
		for(int i=1;i<sharedKeyArr.length;i++){
			revisedSharedKey[i-1]=sharedKeyArr[i];
		}		
		String shaSum=SHAsum(revisedSharedKey,algorithm);
		return shaSum;
	}
	
	public static boolean checkForWeakKey(byte[] key) throws InvalidKeyException{
		return DESKeySpec.isWeak(key, 0);
	}
	
	public static byte[] generateRandomDesKey(String desAlgorithmName) throws NoSuchAlgorithmException{
		Key key;
		KeyGenerator generator = KeyGenerator.getInstance(desAlgorithmName);
		key = generator.generateKey();
		return key.getEncoded();
	}
	
	public static byte[] generateRandomBytes(int length){
		SecureRandom sr = new SecureRandom();
		byte[] rndBytes = new byte[length];
		sr.nextBytes(rndBytes);
		return rndBytes;
		
	}
	
	public static byte[] byteBlockWithZero(int length){
		byte[] bytes = new byte[length];
		Arrays.fill( bytes, (byte) 0 );
		return bytes;
	}
	
  public static String encrypt(byte[] value, byte[] key, byte[] ivValue, String secretKeyFactory, String cipherTouse) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
    DESedeKeySpec keySpec = new DESedeKeySpec(key);
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretKeyFactory);
    SecretKey sk = keyFactory.generateSecret(keySpec);
    IvParameterSpec iv = new IvParameterSpec(ivValue, 0, ivValue.length);
    Cipher cipher = Cipher.getInstance(cipherTouse);
    cipher.init(Cipher.ENCRYPT_MODE, sk, iv);
    byte[] ivArray = cipher.getIV();
    byte[] encryptedBytes = cipher.doFinal(value);
    return EANEncryptionUtil.byteArrayToHex(encryptedBytes);
  } 
	
	public static byte[] stringToAsciiBytes(String string){
		byte[] arr=string.getBytes(StandardCharsets.US_ASCII);
		return arr;
	}
	
	public static byte[] calculateChecksum(byte[] byteArray){
		byte checksum=0;
		byte[] arr=new byte[1];
		 for(int i = 0; i < byteArray.length; i++)
	     {
	          checksum += byteArray[i];
	     }
		arr[0]= checksum;
		return arr;
	}
	
	
	
	
}
