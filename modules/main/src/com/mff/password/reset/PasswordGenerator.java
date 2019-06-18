
package com.mff.password.reset;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import atg.core.util.StringUtils;

/**
 * Encapsulates logic dealing with generating and hashing passwords.
 *
 */
public class PasswordGenerator
{
    private static final Log logger = LogFactory.getLog
(PasswordGenerator.class);

    private static MessageDigest MESSAGE_DIGEST = null;

    static
    {
        try
        {
            MESSAGE_DIGEST = MessageDigest.getInstance("SHA1");
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.fatal("Unable to get instance of SHA1 algorithm", e);
        }
    }

    /**
     * Returns a new password that is at least 8 characters in length and
consists of alphabets and digits.
     *
     * @return a new password that is at least 8 characters in length and
consists of alphabets and digits.
     */
    public static String next()
    {
        return RandomStringUtils.random(8,
"abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789");
    }

    /**
     * Returns a hash of the provided password text using the SHA-1
alogrithm.
     *
     * @param password
     * @return a hash of the provided password text using the SHA-1
alogrithm.
     */
    public static String hash(String password)
    {
        String retVal = null;
        if (StringUtils.isNotBlank(password))
        {
            byte[] passwordAsBytes = MESSAGE_DIGEST.digest
(password.getBytes());
            retVal = String.valueOf(Hex.encodeHex(passwordAsBytes));
        }
        return retVal;
    }
}
