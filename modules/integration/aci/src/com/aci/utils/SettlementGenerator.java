package com.aci.utils;

import java.io.PrintWriter;

import atg.commerce.CommerceException;

public interface SettlementGenerator {
  
  public PrintWriter getPrintWriter() throws CommerceException;

}
