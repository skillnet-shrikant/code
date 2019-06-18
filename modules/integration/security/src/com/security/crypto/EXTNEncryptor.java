package com.security.crypto;

/**
 * An interface for high-level encryption architecture.
 *
 * @author Peter Goetsch (pgoetsch@knowledgepath.com)
 */
public interface EXTNEncryptor {

	/**
	 * Encrypts a passed in String.
	 *
	 * @param pStr the str
	 * @return the string
	 */
	public String encrypt(String pStr);

	/**
	 * Decrypts a passed in String.
	 *
	 * @param pEncryptedString the encrypted string
	 * @return the string
	 */
	public String decrypt(String pEncryptedString);
}
