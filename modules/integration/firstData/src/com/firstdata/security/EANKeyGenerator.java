package com.firstdata.security;

import java.math.BigInteger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;

import atg.nucleus.GenericService;


public class EANKeyGenerator extends GenericService {

	private static final int DEFAULT_KEY_LENGTH_TYPE=2;
	
	private String mFirstDataPrime;
	private String mFirstDataGenerator;
	private String mCLGCPublicKey;
	private String mAlgorithmForKeyGeneration;
	private String mMerchantPublicKey;
	private String mMerchantPrivateKey;
	private String mShaAlgorithm;
	private String mRandomDesKeyAlgorithm;
	private String mDecryptedMwk;
	private String mDerivedKek;
	private String mEncryptedMwk;
	private String mSecretKeyFactory;
	private String mCipherToUse;
	private int mRandomDesKeyLengthType;
	private String mSharedSecretAlgorithm;
	
	public String getSharedSecretAlgorithm(){
		return mSharedSecretAlgorithm;
	}
	
	public void setSharedSecretAlgorithm(String pSharedSecretAlgorithm){
		mSharedSecretAlgorithm=pSharedSecretAlgorithm;
	}
	
	public void setRandomDesKeyLengthType(int pRandomDesKeyLengthType){
		mRandomDesKeyLengthType=pRandomDesKeyLengthType;
	}
	
	public int getRandomDesKeyLengthType(){
		return mRandomDesKeyLengthType;
	}
	
	public String getSecretKeyFactory() {
		return mSecretKeyFactory;
	}

	public void setSecretKeyFactory(String pSecretKeyFactory) {
		mSecretKeyFactory = pSecretKeyFactory;
	}

	public String getCipherToUse() {
		return mCipherToUse;
	}

	public void setCipherToUse(String pCipherToUse) {
		mCipherToUse = pCipherToUse;
	}

	public String getEncryptedMwk() {
		return mEncryptedMwk;
	}

	public void setEncryptedMwk(String pEncryptedMwk) {
		mEncryptedMwk = pEncryptedMwk;
	}

	public String getDerivedKek() {
		return mDerivedKek;
	}

	public void setDerivedKek(String pDerivedKek) {
		mDerivedKek = pDerivedKek;
	}

	public String getDecryptedMwk() {
		return mDecryptedMwk;
	}

	public void setDecryptedMwk(String pDecryptedMwk) {
		mDecryptedMwk = pDecryptedMwk;
	}

	public String getRandomDesKeyAlgorithm() {
		return mRandomDesKeyAlgorithm;
	}

	public void setRandomDesKeyAlgorithm(String pRandomDesKeyAlgorithm) {
		mRandomDesKeyAlgorithm = pRandomDesKeyAlgorithm;
	}

	public String getShaAlgorithm() {
		return mShaAlgorithm;
	}

	public void setShaAlgorithm(String pShaAlgorithm) {
		mShaAlgorithm = pShaAlgorithm;
	}

	public String getMerchantPublicKey() {
		return mMerchantPublicKey;
	}

	public void setMerchantPublicKey(String pMerchantPublicKey) {
		mMerchantPublicKey = pMerchantPublicKey;
	}

	public String getMerchantPrivateKey() {
		return mMerchantPrivateKey;
	}

	public void setMerchantPrivateKey(String pMerchantPrivateKey) {
		mMerchantPrivateKey = pMerchantPrivateKey;
	}

	public String getAlgorithmForKeyGeneration() {
		return mAlgorithmForKeyGeneration;
	}

	public void setAlgorithmForKeyGeneration(String pAlgorithmForKeyGeneration) {
		mAlgorithmForKeyGeneration = pAlgorithmForKeyGeneration;
	}

	public String getCLGCPublicKey() {
		return mCLGCPublicKey;
	}

	public void setCLGCPublicKey(String pCLGCPublicKey) {
		mCLGCPublicKey = pCLGCPublicKey;
	}

	public String getFirstDataPrime() {
		return mFirstDataPrime;
	}

	public void setFirstDataPrime(String pPFirstDataPrime) {
		mFirstDataPrime = pPFirstDataPrime;
	}

	public String getFirstDataGenerator() {
		return mFirstDataGenerator;
	}

	public void setFirstDataGenerator(String pFirstDataGenerator) {
		mFirstDataGenerator = pFirstDataGenerator;
	}
	
	
	public void generateSpecificPublicAndPrivateKeyPair(){
	  vlogDebug("EANKeyGenerator:generateSpecificPublicAndPrivateKeyPair():Start");
		try {
			String prime = getFirstDataPrime();
			String generator=getFirstDataGenerator();
			if(prime==null||generator==null){
				throw new Exception("Exception occurred. Prime and Generator are needed to generate public key");
			}
			vlogDebug("First Data Prime is:\t"+prime);
			vlogDebug("First Data generator is:\t"+generator);
			String modifiedPrime="00"+prime;
			vlogDebug("Modified Prime is:\t"+modifiedPrime);
			byte[] bytePrime=hexToBytes(modifiedPrime);
			vlogDebug("Length of prime:\t"+bytePrime.length);
			vlogDebug("Prime as byte array:\t");
			EANEncryptionUtil.printByteArrayString(bytePrime);
			byte[] byteGenerator=hexToBytes(generator);
			vlogDebug("Generator as byte array:\t");
			EANEncryptionUtil.printByteArrayString(byteGenerator);
			//byte[] encodedPrime=Base64.getEncoder().encode(bytePrime);
			BigInteger primeAsInteger=new BigInteger(bytePrime);
			vlogDebug("Prime as Integer:\t"+primeAsInteger.longValue());
			BigInteger generatorAsInteger=new BigInteger(byteGenerator);
			vlogDebug("Generator as Integer:\t"+generatorAsInteger.longValue());
			createSpecificKey(primeAsInteger,generatorAsInteger);
			vlogDebug("EANKeyGenerator:generateSpecificPublicAndPrivateKeyPair():End");
		}
		catch(Exception ex){
			vlogWarning("EANKeyGenerator:generateSpecificPublicAndPrivateKeyPair():Exception Occurred"+ex.getMessage());
		}
		
	}
	
	public void generateRandomPublicAndPrivateKeyPair(){
	  vlogDebug("EANKeyGenerator:generateRandomPublicAndPrivateKeyPair():Start");
		try {
			
			createKey();
			vlogDebug("EANKeyGenerator:generateRandomPublicAndPrivateKeyPair():End");
		}
		catch(Exception ex){
			vlogWarning("EANKeyGenerator:generateRandomPublicAndPrivateKeyPair():Exception Occurred"+ex.getMessage());
		}
		
	}
	
	public void generateDecryptedMWK() throws Exception{
	  vlogDebug("EANKeyGenerator:generateDecryptedMWK():Start");
		try {
			String clgcPubKey=getCLGCPublicKey();
			if(clgcPubKey==null){
				throw new Exception("Exception occurred. CLGC public key is needed");
			}
			String merPrvKey=getMerchantPrivateKey();
			if(merPrvKey==null){
				throw new Exception("Exception occurred. Merchant Private Key is needed");
			}
			String prime=getFirstDataPrime();
			if(prime==null){
				throw new Exception("Exception occurred. First Data prime is needed");
			}
			String modifiedPrime="00"+prime;
			String generator=getFirstDataGenerator();
			if(generator==null){
				throw new Exception("Exception occurred. First Data Generator is needed");
			}
			String algorithmForKeyGeneration=getSharedSecretAlgorithm();
			if(algorithmForKeyGeneration==null){
				throw new Exception("Exception occurred. Algorithm for key generation is needed");
			}
			
			BigInteger clPub=new BigInteger(hexToBytes(clgcPubKey));
			BigInteger mrPrv=new BigInteger(hexToBytes(merPrvKey));
			BigInteger prm=new BigInteger(hexToBytes(modifiedPrime));
			BigInteger gen=new BigInteger(hexToBytes(generator));
			
			SecretKey sharedSecret=generateSharedSecret(clPub, mrPrv, prm, gen, algorithmForKeyGeneration);
			
			String sharedSecretHex=EANEncryptionUtil.byteArrayToHex(sharedSecret.getEncoded());
			vlogDebug("Computed shared secret:\t"+sharedSecretHex);
			String sha1SharedSecret=sha1HashOfSharedSecret(sharedSecret);
			vlogDebug("Computed SHA1 hash of shared secret:\t"+sha1SharedSecret);
			byte[] sharedSecretArray=hexToBytes(sha1SharedSecret);
			String kek = generateKEK(sharedSecretArray);
			vlogDebug("Computed Kek:\t"+kek);
			String revisedKek=generateRevisedKEK(kek);
			vlogDebug("Computed Revised Kek:\t"+revisedKek);
			byte[] derivedKek=adjustDESParity(revisedKek);
			vlogDebug("Is parity applied to derived kek:\t"+!Arrays.equals(hexToBytes(revisedKek),derivedKek));
			vlogDebug("Derived Kek is:\t"+EANEncryptionUtil.byteArrayToHex(derivedKek));
			boolean isWeakKek=EANEncryptionUtil.checkForWeakKey(derivedKek);
			vlogDebug("Is derivedkek weak:\t"+isWeakKek);
			if(!isWeakKek){
				setDerivedKek(EANEncryptionUtil.byteArrayToHex(derivedKek));
				vlogDebug("Derieved kek is set");
				int desKeyLengthType=getRandomDesKeyLengthType();
				if(desKeyLengthType==0){
					desKeyLengthType=DEFAULT_KEY_LENGTH_TYPE;
				}
				byte[] randomKeyK=generateRandomDesKeyK(desKeyLengthType);
				vlogDebug("Generated random data:\t"+EANEncryptionUtil.byteArrayToHex(randomKeyK));
				String revisedK=generateRevisedKEK(EANEncryptionUtil.byteArrayToHex(randomKeyK));
				vlogDebug("Revised K:\t"+revisedK);
				byte[] derivedK= adjustDESParity(revisedK);
				boolean isDerivedKWeak=EANEncryptionUtil.checkForWeakKey(derivedK);
				vlogDebug("Is Derived K weak:"+isDerivedKWeak);
				if(!isDerivedKWeak){
					setDecryptedMwk(EANEncryptionUtil.byteArrayToHex(derivedK));
					vlogDebug("DecryptedMwk is set");
				}
			}
			vlogDebug("EANKeyGenerator:generateDecryptedMWK():End");
		}
		catch(Exception ex){
			vlogWarning("EANKeyGenerator:generateDecryptedMWK():Exception occurred"+ex.getMessage());
		}
	}
	
	public void generateEncryptedMWK() throws Exception{
	  vlogDebug("EANKeyGenerator:generateEncryptedMWK():Started");
		try {
			String decryptedMWK=getDecryptedMwk();
			if(decryptedMWK==null){
				throw new Exception("Exception occurred: Decrypted MWK is required");
			}
			String derivedKek=getDerivedKek();
			if(derivedKek==null){
				throw new Exception("Exception occurred: DerivedKek is required");
			}
			byte[] derievedK=hexToBytes(decryptedMWK);
			byte[] randomBytes=generateRandomBytes(8);
			vlogDebug("Generated Random Byte array:"+EANEncryptionUtil.byteArrayToHex(randomBytes));
			byte[] zeroByteBlock=byteBlockWithZero(8);
			vlogDebug("Generated zero byte array:"+EANEncryptionUtil.byteArrayToHex(zeroByteBlock));
			String checkValue=encrypt(zeroByteBlock,derievedK);
			vlogDebug("Generated checkvalue:"+checkValue);
			String keyBlock=generateKeyBlock(EANEncryptionUtil.byteArrayToHex(randomBytes),EANEncryptionUtil.byteArrayToHex(derievedK),checkValue);
			vlogDebug("Generated Key block:"+keyBlock);
			String encryptedKeyBlock=encrypt(hexToBytes(keyBlock),hexToBytes(derivedKek));
			vlogDebug("Encrypted Key block:\t"+encryptedKeyBlock);
			setEncryptedMwk(encryptedKeyBlock);	
			vlogDebug("EANKeyGenerator:generateEncryptedMWK():End");
		}
		catch(Exception ex){
		  vlogWarning("EANKeyGenerator:generateEncryptedMWK():Exception occurred:"+ex.getMessage());
		}
		
	}
	

	
	private void createSpecificKey(BigInteger p, BigInteger g) throws Exception {
	  vlogDebug("EANKeyGenerator:createSpecificKey:Start");
		String keyGenerationAlgorithm=getAlgorithmForKeyGeneration();
		if(keyGenerationAlgorithm==null){
			throw new Exception("Exception occurred. Algorithm name is needed to generate public and private key");
		}
	    KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyGenerationAlgorithm,new BouncyCastleProvider());
	    DHParameterSpec param = new DHParameterSpec(p, g);
	    kpg.initialize(param);
	    KeyPair kp = kpg.generateKeyPair();
	    KeyFactory kfactory = KeyFactory.getInstance(keyGenerationAlgorithm);

	    DHPublicKeySpec pubSpec = (DHPublicKeySpec) kfactory.getKeySpec(kp.getPublic(),
	        DHPublicKeySpec.class);
	    DHPrivateKeySpec prvSpec=(DHPrivateKeySpec)kfactory.getKeySpec(kp.getPrivate(), DHPrivateKeySpec.class);
	    byte[] privateKeyArr=prvSpec.getX().toByteArray();
	    byte[] publicKeyArr=pubSpec.getY().toByteArray();
	    
	    String merchantPublicKey=EANEncryptionUtil.byteArrayToHex(publicKeyArr);
	    vlogDebug("Merchant Public Key creation Successful");
	    setMerchantPublicKey(merchantPublicKey);
	    String merchantPrivateKey=EANEncryptionUtil.byteArrayToHex(privateKeyArr);
	    setMerchantPrivateKey(merchantPrivateKey);
	    vlogDebug("EANKeyGenerator:createSpecificKey:End");
	    
	  }
	
	private void createKey() throws Exception {
		vlogDebug("EANKeyGenerator:createKey:Started");
		String keyGenerationAlgorithm=getAlgorithmForKeyGeneration();
		if(keyGenerationAlgorithm==null){
			throw new Exception("Exception occurred. Algorithm name is needed to generate public and private key");
		}
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyGenerationAlgorithm,new BouncyCastleProvider());
	    KeyPair kp = kpg.generateKeyPair();
	    KeyFactory kfactory = KeyFactory.getInstance(keyGenerationAlgorithm);

	    DHPublicKeySpec pubSpec = (DHPublicKeySpec) kfactory.getKeySpec(kp.getPublic(),
	        DHPublicKeySpec.class);
	    DHPrivateKeySpec prvSpec=(DHPrivateKeySpec)kfactory.getKeySpec(kp.getPrivate(), DHPrivateKeySpec.class);
	    
	    byte[] privateKeyArr=prvSpec.getX().toByteArray();
	    byte[] publicKeyArr=pubSpec.getY().toByteArray();
	    
	    byte[] modifiedPrivateKeyArr=Arrays.copyOfRange(privateKeyArr,1,privateKeyArr.length);
	    setMerchantPublicKey(EANEncryptionUtil.byteArrayToHex(publicKeyArr));
	    setMerchantPrivateKey(EANEncryptionUtil.byteArrayToHex(modifiedPrivateKeyArr));
	    vlogDebug("EANKeyGenerator:createKey:End");
	  }
	
	private String generateKeyBlock(String random, String decryptedMWK, String checkValue){
		vlogDebug("EANKeyGenerator:generateKeyBlock");
		return random+decryptedMWK+checkValue;
	}
	
	private String encrypt(byte[] value,byte[] key) { 
		vlogDebug("EANKeyGenerator:encrypt:Start");
	    try { 
	     String secretKeyFactory=getSecretKeyFactory();
	     if(secretKeyFactory==null){
	    	 throw new Exception("Exception occurred. Secret Key Factory is needed");
	     }
	     String cipherTouse=getCipherToUse();
	     if(cipherTouse==null){
	    	 throw new Exception("Exception occurred. Cipher to use is needed");
	     }
	     byte[] ivValue=EANEncryptionUtil.byteBlockWithZero(8);
	     String encryptedString=EANEncryptionUtil.encrypt(value, key, ivValue, secretKeyFactory, cipherTouse);
	     vlogDebug("EANKeyGenerator:encrypt:End");
	     return encryptedString; 
	    } catch (Exception e) { 
	    	vlogError(e,"EANKeyGenerator:encrypt:Exception occurred");
	    } 
	    return null; 
	  } 
	
	private byte[] byteBlockWithZero(int length){
		vlogDebug("EANKeyGenerator:byteBlockWithZero:Start");
		byte[] bytes = EANEncryptionUtil.byteBlockWithZero(length);
		vlogDebug("EANKeyGenerator:byteBlockWithZero:End");
		return bytes;
	}
	
	private byte[] generateRandomBytes(int length){
		vlogDebug("EANKeyGenerator:generateRandomBytes:Start");
		byte[] rndBytes=EANEncryptionUtil.generateRandomBytes(length);
		vlogDebug("EANKeyGenerator:generateRandomBytes:End");
		return rndBytes;
		
	}
	
	private byte[] generateRandomDesKeyK(int numKeys) throws NoSuchAlgorithmException{
		vlogDebug("EANKeyGenerator:generateRandomDesKeyK with parameters:Start");
		String desAlgorithmName=getRandomDesKeyAlgorithm();
		if(desAlgorithmName==null){
			throw new NoSuchAlgorithmException("Exception occurred. DesAlgorithm name is required");
		}
		byte[] randomK=new byte[8*numKeys];
		for(int i=1;i<=numKeys;i++){
			Key key;
			SecureRandom sr = new SecureRandom();
			KeyGenerator generator=KeyGenerator.getInstance(desAlgorithmName);
			generator.init(sr);
			key=generator.generateKey();
			byte[] keyBytes= key.getEncoded();
			int k=0;
			for(int j=(i-1)*8;j<i*8;j++){
				randomK[j]=keyBytes[k];
				k++;
			}
			
			vlogDebug("Random keylength:"+keyBytes.length);
		}
		vlogDebug("EANKeyGenerator:generateRandomDesKeyK with parameters:End");
		return randomK;
	}
	
	private byte[] generateRandomDesKeyK() throws NoSuchAlgorithmException{
		vlogDebug("EANKeyGenerator:generateRandomDesKeyK:Start");
		String desAlgorithmName=getRandomDesKeyAlgorithm();
		if(desAlgorithmName==null){
			throw new NoSuchAlgorithmException("Exception occurred. DesAlgorithm name is required");
		}
		byte[] bytes=EANEncryptionUtil.generateRandomDesKey(desAlgorithmName);
		vlogDebug("EANKeyGenerator:generateRandomDesKeyK:End");
		return bytes;
	}
	
	
	
	private byte[] adjustDESParity (String revisedKek) {
		vlogDebug("EANKeyGenerator:adjustDESParity:Start");
	 	byte[] bytes=EANEncryptionUtil.adjustDESParity(revisedKek);
        vlogDebug("EANKeyGenerator:adjustDESParity:End");
	 	return bytes;
    }
	
	private String generateRevisedKEK(String hexString){
		vlogDebug("EANKeyGenerator:generateRevisedKEK:Start");
		String revisedKek=hexString;
		revisedKek=revisedKek+hexString.substring(0,16);
		vlogDebug("EANKeyGenerator:generateRevisedKEK:End");
		return revisedKek;
	}
	
	private String generateKEK(byte[] shaSum){
		vlogDebug("EANKeyGenerator:generateKEK:Start");
		byte[] kek=new byte[16];
		for(int i=0;i<16;i++){
			kek[i]=shaSum[i];
		}
		vlogDebug("EANKeyGenerator:generateKEK:End");
		return EANEncryptionUtil.byteArrayToHex(kek);
	}
	
	private SecretKey generateSharedSecret(BigInteger clPub,BigInteger mrPrv, BigInteger prm, BigInteger gen,String algorithmForKeyGeneration ) throws InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeySpecException{
		
		KeyFactory keyFact=KeyFactory.getInstance(algorithmForKeyGeneration,new BouncyCastleProvider());
		DHParameterSpec param=new DHParameterSpec(prm,gen);
		
		//Merchant Key Generation from hex bytes
		DHPrivateKeySpec merDhPrivateKeySpec=new DHPrivateKeySpec(mrPrv, prm, gen);
		DHPrivateKey merDhPrivateKey=(DHPrivateKey)keyFact.generatePrivate(merDhPrivateKeySpec);
		
		//CLGC Pub Key Gen from hex bytes
		DHPublicKeySpec clgcDhPubspec=new DHPublicKeySpec(clPub,prm,gen);
		DHPublicKey clgcDHPubKey=(DHPublicKey)keyFact.generatePublic(clgcDhPubspec);
		
		SecretKey sharedSecret=EANEncryptionUtil.generateSecret(merDhPrivateKey, clgcDHPubKey, param, algorithmForKeyGeneration);
		return sharedSecret;
	}
	
	private String sha1HashOfSharedSecret(SecretKey secretKey)throws NoSuchAlgorithmException{
		String shaAlgorithm=getShaAlgorithm();
		if(shaAlgorithm==null || shaAlgorithm.length()==0){
			throw new NoSuchAlgorithmException("Exception occurred. Sha Algorithm name is required");
		}
		String shaKey=EANEncryptionUtil.SHAsum(secretKey.getEncoded(),shaAlgorithm);
		return shaKey;
	}
	
	private String generateSharedSecret(BigInteger clPubKey,BigInteger merPrvKey, BigInteger p) throws NoSuchAlgorithmException{
		vlogDebug("EANKeyGenerator:generateSharedSecret:Start");
		String shaAlgorithm=getShaAlgorithm();
		if(shaAlgorithm==null || shaAlgorithm.length()==0){
			throw new NoSuchAlgorithmException("Exception occurred. Sha Algorithm name is required");
		}
		String shaKey=EANEncryptionUtil.generateSharedSecret(clPubKey, merPrvKey, p,shaAlgorithm);
		vlogDebug("EANKeyGenerator:generateSharedSecret:End");
		return shaKey;
	}
	
	private byte[] hexToBytes(String string){
		vlogDebug("EANKeyGenerator:hexToBytes:Start");
		byte[] data=EANEncryptionUtil.hexToBytes(string);
		vlogDebug("EANKeyGenerator:hexToBytes:End");
        return data;
	}
	
	
	
	
}
