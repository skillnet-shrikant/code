package com.mff.util.pgp.test;

import com.mff.util.pgp.PGPEncryptionService;

import atg.commerce.CommerceException;
import atg.nucleus.GenericService;

public class TestPGPEncryptionService extends GenericService {
  PGPEncryptionService pgpEncryptionService;
  
  String filename = "/tmp/input.txt";
  
  public void testEncryption() {
    try {
      getPgpEncryptionService().encrypt(filename);
    } catch (CommerceException e) {
      vlogError(e, "Error occurred");
    }
  }

  public PGPEncryptionService getPgpEncryptionService() {
    return pgpEncryptionService;
  }

  public void setPgpEncryptionService(PGPEncryptionService pPgpEncryptionService) {
    pgpEncryptionService = pPgpEncryptionService;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String pFilename) {
    filename = pFilename;
  }
  
}
