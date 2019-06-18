package com.security.repository.property;

import com.security.EXTNSecurityConstants;
import com.security.crypto.EXTNEncryptor;

import atg.adapter.gsa.GSAPropertyDescriptor;

/**
 * This class serves as a custom property descriptor for repository definitions.  Items with this property will get
 * encrypted or decrypted as they pass through the repository.  To use the property descriptor, the repository
 * definition has to change:
 *
 * 	1) Remove any data-type attributes
 * 	3) Add property-type="mff.repository.property.EncryptionPropertyDescriptor"
 * 	4) Nest <attribute name="encryptor" bean="/mff/security/crypto/AESEncryptor"/> in between the property tags
 *
 * The resulting property will look like:
 *
 * <code>
 * 	<property name="foo" property-type="mff.repository.property.EncryptionPropertyDescriptor">
 * 		<attribute name="encryptor" bean="/mff/security/crypto/AESEncryptor"/>
 * 	</property>
 * </code>
 *
 * Some descriptors also pass a <attribute name="trace" value="user login"/> tag -- this is for pure debugging
 * purposes.  You can call getValue("trace") and print it out to figure out which tags are invoking the repository
 */
public class EncryptionPropertyDescriptor extends GSAPropertyDescriptor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	EXTNEncryptor mEncryptor;

	/*
	 * Haven't found a use for this yet, but the manual does specify that this method should be overridden
	 * when making a custom property descriptor.  Keeping it in case we need to use it in the future.
	 *
	 * (non-Javadoc)
	 * @see atg.adapter.gsa.GSAPropertyDescriptor#createDBPropertyEditor()
	 */
	/*
	@Override
	public PropertyEditor createDBPropertyEditor() {
		return super.createDBPropertyEditor();
	}
	*/

	/*
	public void printTrace() {
	    String value = (String) getValue("trace");

	    if(value == null) {
		System.out.println("value was null");
	    } else {
		System.out.println("value = " + value);
	    }
	}
	*/

	/*
	 * This method is called each time the repository is queried.  It converts the data found in the database and
	 * returns it to the calling application.  In our case, decryption is happening here.
	 *
	 * The configured encryptor is resolved, then used to decrypt the value and return.
	 *
	 * (non-Javadoc)
	 * @see atg.adapter.gsa.GSAPropertyDescriptor#rawToReal(java.lang.Object)
	 */
	@Override
	public Object rawToReal(Object pArg0) {
		if(pArg0 == null) {
			return null;
		}

		// resolve the component passed in with the repository definition file
		mEncryptor = (EXTNEncryptor) getValue(EXTNSecurityConstants.ENCRYPTOR);

		if(pArg0 instanceof String  && !((String)pArg0).trim().equalsIgnoreCase("")) {
			return mEncryptor.decrypt((String) pArg0);
		} else {
			return null;
		}
	}

	/*
	 * This method is called each time the repository is queried.  It encrypts the query values so they can be compared
	 * with the values in the database.
	 *
	 * The configured encryptor is resolved, then used to decrypt the value and return.
	 *
	 * (non-Javadoc)
	 * @see atg.adapter.gsa.GSAPropertyDescriptor#realToRaw(java.lang.Object)
	 */
	@Override
	public Object realToRaw(Object pRealValue) {
		if(pRealValue == null) {
			return null;
		}

		mEncryptor = (EXTNEncryptor) getValue(EXTNSecurityConstants.ENCRYPTOR);

	    if(pRealValue instanceof String && !((String)pRealValue).trim().equalsIgnoreCase("")) {
			return mEncryptor.encrypt((String) pRealValue);
		} else {
			return null;
		}
	}
}
