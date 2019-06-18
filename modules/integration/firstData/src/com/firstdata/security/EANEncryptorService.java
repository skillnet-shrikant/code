package com.firstdata.security;

import java.util.Arrays;

import atg.nucleus.GenericService;

public class EANEncryptorService extends GenericService {
	
	String mDecryptedMWK;
	String mSecretKeyFactory;
	String mCipherToUse;
	String mEan;
	String mEncryptedEan;
	
	public String getEncryptedEan() {
		return mEncryptedEan;
	}

	public void setEncryptedEan(String pEncryptedEan) {
		mEncryptedEan = pEncryptedEan;
	}

	public String getEan() {
		return mEan;
	}

	public void setEan(String pEan) {
		mEan = pEan;
	}

	public String getCipherToUse() {
		return mCipherToUse;
	}

	public void setCipherToUse(String pCipherToUse) {
		mCipherToUse = pCipherToUse;
	}

	public String getSecretKeyFactory() {
		return mSecretKeyFactory;
	}

	public void setSecretKeyFactory(String pSecretKeyFactory) {
		mSecretKeyFactory = pSecretKeyFactory;
	}

	public String getDecryptedMWK() {
		return mDecryptedMWK;
	}

	public void setDecryptedMWK(String pDecryptedMWK) {
		mDecryptedMWK = pDecryptedMWK;
	}
	
	private byte[] generateRandomBytes(){
		vlogDebug("EANEncryptorService: generateRandomBytes: Start");
		byte[] arr=EANEncryptionUtil.generateRandomBytes(7);
		vlogDebug("EANEncryptorService: generateRandomBytes: End");
		return arr;
	}
	
	private byte[] generateEANAscii(String ean){
	  vlogDebug("EANEncryptorService: generateEANAscii: Start");
		
		byte[] arr=EANEncryptionUtil.stringToAsciiBytes(ean);
		byte[] result=new byte[8];
		if(arr.length>8){
			result=Arrays.copyOfRange(arr, 0, 8);
		}
		else if(arr.length==8){
			result=Arrays.copyOf(arr, 8);
		}
		else {
			for(int i=0;i<result.length;i++){
				if(i<arr.length){
					result[i]=arr[i];
				}
				else {
					result[i]=0;
				}
			}
			
		}
		vlogDebug("EANEncryptorService: generateEANAscii: End");
		return result;
	}
	
	private byte[] generateChecksum(byte[] asciiEan){
	  vlogDebug("EANEncryptorService: generateChecksum: Start");
		byte[] checksum=EANEncryptionUtil.calculateChecksum(asciiEan);
		vlogDebug("EANEncryptorService: generateChecksum: End");
		return checksum;
	}
	
	private String generateEanBlock(String random, String checkSum, String asciiEan){
		return random+checkSum+asciiEan;
	}
	
	public void testEncryptEan(){
		try{
			String ean=getEan();
			String eanEc=encryptEan(ean);
			setEncryptedEan(eanEc);
		}
		catch(Exception ex){
			
		}
	}
	
	public String encryptEan(String ean) throws Exception{
	  vlogDebug("EANEncryptorService: encryptedEan: Start");
		String decryptedMWK=getDecryptedMWK();
		if(decryptedMWK==null){
			throw new Exception("Exception occurred. Decrypted MWK is needed for EAN encryption");
		}
		String secretKeyFactory=getSecretKeyFactory();
		if(secretKeyFactory==null){
			throw new Exception("Exception occurred. secretKeyFactory is needed for EAN encryption");
		}
		String cipherToUse=getCipherToUse();
		if(cipherToUse==null){
			throw new Exception("Exception occurred. cipherToUse is needed for EAN encryption");
		}
		byte[] randomSevenByte=generateRandomBytes();
		String randomSevenBytes=EANEncryptionUtil.byteArrayToHex(randomSevenByte);
		vlogDebug(randomSevenBytes);
		byte[] asciiEan=generateEANAscii(ean);
		String strinAsciiEan=EANEncryptionUtil.byteArrayToHex(asciiEan);
		vlogDebug(strinAsciiEan);
		byte[] checkSum=generateChecksum(asciiEan);
		String checkSumString=EANEncryptionUtil.byteArrayToHex(checkSum);
		vlogDebug(checkSumString);
		String eanBlock=generateEanBlock(randomSevenBytes,checkSumString,strinAsciiEan);
		vlogDebug(eanBlock);
		byte[] ivValue=EANEncryptionUtil.byteBlockWithZero(8);
		String encryptedEan=EANEncryptionUtil.encrypt(EANEncryptionUtil.hexToBytes(eanBlock), EANEncryptionUtil.hexToBytes(decryptedMWK), ivValue,secretKeyFactory, cipherToUse);
		vlogDebug(encryptedEan);
		vlogDebug("EANEncryptorService: encryptedEan: End");
		return encryptedEan;
	}
	
	
	

}
