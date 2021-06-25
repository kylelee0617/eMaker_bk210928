package Farglory.util;

import jcx.db.talk;

/**
 * 資料庫連線BEAN
 * 
 * @author B04391
 *
 */

public class TalkBean {
  private talk dbSale = null;
  private talk dbEMail = null;
  private talk db400CRM = null;
  private talk dbEIP = null;
  private talk dbPw0D = null;
  private talk dbDOC = null;
  
  public talk getDbSale() {
    return dbSale;
  }
  public void setDbSale(talk dbSale) {
    this.dbSale = dbSale;
  }
  public talk getDbEMail() {
    return dbEMail;
  }
  public void setDbEMail(talk dbEMail) {
    this.dbEMail = dbEMail;
  }
  public talk getDb400CRM() {
    return db400CRM;
  }
  public void setDb400CRM(talk db400crm) {
    db400CRM = db400crm;
  }
  public talk getDbEIP() {
    return dbEIP;
  }
  public void setDbEIP(talk dbEIP) {
    this.dbEIP = dbEIP;
  }
  public talk getDbPw0D() {
    return dbPw0D;
  }
  public void setDbPw0D(talk dbPw0D) {
    this.dbPw0D = dbPw0D;
  }
  public talk getDbDOC() {
    return dbDOC;
  }
  public void setDbDOC(talk dbDOC) {
    this.dbDOC = dbDOC;
  }
}
