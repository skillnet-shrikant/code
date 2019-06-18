package com.aci.utils;

import java.io.PrintWriter;

import atg.commerce.CommerceException;
import atg.nucleus.GenericService;

public class DefaultSettlementGenerator extends GenericService implements SettlementGenerator {

  @Override
  public PrintWriter getPrintWriter() throws CommerceException {
    return(new PrintWriter(System.out));
  }

}
