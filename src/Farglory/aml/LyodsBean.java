package Farglory.aml;

import java.util.*;

import jcx.db.talk;

/**
 * func : 功能項 EX 換名、購屋證明單 
 * RecordType : 功能項細項 EX 客戶資料、代理人資料
 * ActionName(ActionName) : 新增、修改、刪除 
 * errMsg : 符合的樣態內容，或為"不適用" or "不符合" 
 * AMLNo : AML樣態編號
 * 
 * @author B04391
 *
 */

public class LyodsBean {
	//DB setting
  private talk dbSale = null;
	private talk dbEMail = null;
	private talk db400CRM = null;
	private talk dbEIP = null;
	
	//param
	private RiskCustomBean custBean = null;      // 送檢主要客戶
	private RiskRelatedBean relatedBean = null;  // 送檢關聯人
	private String emakerUserNo = "";            // 使用者EMAKER編號
	
	//預設為比對並查詢風險值
	private String riskResult = "Y";   // Y: 比對，計算風險值, N: 比對，沒有計算風險值, R: 不比對，只計算風險值
	private String checkAll = "Y";     // Y: 檢查所有類別, N: 只檢查制裁名單
	private String modifyData = "N";   // Y: 更新客戶資料, N: 不更新客戶資料，僅有查詢
	private String addCustomer = "";   // Y: 新增, N: 註銷
	
	//no date
  private String orderNo = "";       // 購屋證明單編號
  private String orderDate = "";     // 購屋證明單日期
  private String docNo = "";         // 收款單編號
  private String eDate = "";         // 收款單日期
  private String contractNo = "";    // 合約編號
  private String cDate = "";         // 合約日期
  
  //for Sale05M070
  private String func = "";          // 功能 (購屋證明單、收款、合約會審、換名...等等)
  private String recordType = "";    // 甚麼資料 (客戶資料、代理人資料、風險計算...ETC)
  private String actionName = "";    // 新增、寫入、存檔...等等
  private String actionNo = "";      // 
  
  //Lyods soap
	private String soapURL = "";       // webservice url

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

  public RiskCustomBean getCustBean() {
    return custBean;
  }

  public void setCustBean(RiskCustomBean custBean) {
    this.custBean = custBean;
  }

  public RiskRelatedBean getRelatedBean() {
    return relatedBean;
  }

  public void setRelatedBean(RiskRelatedBean relatedBean) {
    this.relatedBean = relatedBean;
  }

  public String getEmakerUserNo() {
    return emakerUserNo;
  }

  public void setEmakerUserNo(String emakerUserNo) {
    this.emakerUserNo = emakerUserNo;
  }

  public String getRiskResult() {
    return riskResult;
  }

  public void setRiskResult(String riskResult) {
    this.riskResult = riskResult;
  }

  public String getCheckAll() {
    return checkAll;
  }

  public void setCheckAll(String checkAll) {
    this.checkAll = checkAll;
  }

  public String getModifyData() {
    return modifyData;
  }

  public void setModifyData(String modifyData) {
    this.modifyData = modifyData;
  }

  public String getAddCustomer() {
    return addCustomer;
  }

  public void setAddCustomer(String addCustomer) {
    this.addCustomer = addCustomer;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate;
  }

  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  public String geteDate() {
    return eDate;
  }

  public void seteDate(String eDate) {
    this.eDate = eDate;
  }

  public String getContractNo() {
    return contractNo;
  }

  public void setContractNo(String contractNo) {
    this.contractNo = contractNo;
  }

  public String getcDate() {
    return cDate;
  }

  public void setcDate(String cDate) {
    this.cDate = cDate;
  }

  public String getFunc() {
    return func;
  }

  public void setFunc(String func) {
    this.func = func;
  }

  public String getRecordType() {
    return recordType;
  }

  public void setRecordType(String recordType) {
    this.recordType = recordType;
  }

  public String getActionName() {
    return actionName;
  }

  public void setActionName(String actionName) {
    this.actionName = actionName;
  }

  public String getActionNo() {
    return actionNo;
  }

  public void setActionNo(String actionNo) {
    this.actionNo = actionNo;
  }

  public String getSoapURL() {
    return soapURL;
  }

  public void setSoapURL(String soapURL) {
    this.soapURL = soapURL;
  }

}
