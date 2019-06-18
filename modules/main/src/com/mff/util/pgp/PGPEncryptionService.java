package com.mff.util.pgp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import atg.commerce.CommerceException;
import atg.nucleus.GenericService;

public class PGPEncryptionService extends GenericService {

  private String publicKeyFileName;
  private String pgpExtension;
  private boolean asciiArmored = true;
  private boolean integrityCheck = true;

  private boolean deleteFileOnEncryption;

  public boolean encrypt(String pFileName) throws CommerceException {
    
    try {
      
      String encryptedFileName = null;
      if (pFileName.indexOf('.') != -1) {
        encryptedFileName = pFileName.substring(0, pFileName.indexOf('.')) + getPgpExtension();
      } else {
       throw new CommerceException("The pFilename should contain an extension " + pFileName);
      }
      
      vlogInfo("Generating the following encrypted filename {0}", encryptedFileName);
      
      FileOutputStream out = new FileOutputStream(encryptedFileName);
      FileInputStream keyIn = new FileInputStream(publicKeyFileName);
      PGPUtils.encryptFile(out, pFileName, PGPUtils.readPublicKey(keyIn), asciiArmored, integrityCheck);
      out.close();
      keyIn.close();
      
      if (deleteFileOnEncryption) {
        File inputFile = new File(pFileName);
        inputFile.delete();
      }
    } catch (Exception e) {
      throw new CommerceException(e);
    }
    return true;
  }

  public String getPublicKeyFileName() {
    return publicKeyFileName;
  }

  public void setPublicKeyFileName(String pPublicKeyFileName) {
    publicKeyFileName = pPublicKeyFileName;
  }

  public String getPgpExtension() {
    return pgpExtension;
  }

  public void setPgpExtension(String pPgpExtension) {
    pgpExtension = pPgpExtension;
  }

  public boolean isAsciiArmored() {
    return asciiArmored;
  }

  public void setAsciiArmored(boolean pAsciiArmored) {
    asciiArmored = pAsciiArmored;
  }

  public boolean isIntegrityCheck() {
    return integrityCheck;
  }

  public void setIntegrityCheck(boolean pIntegrityCheck) {
    integrityCheck = pIntegrityCheck;
  }

  public boolean isDeleteFileOnEncryption() {
    return deleteFileOnEncryption;
  }

  public void setDeleteFileOnEncryption(boolean pDeleteFileOnEncryption) {
    deleteFileOnEncryption = pDeleteFileOnEncryption;
  }

}
