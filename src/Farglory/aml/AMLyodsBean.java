package Farglory.aml;

import java.util.ArrayList;
import java.util.List;

import Farglory.util.TalkBean;
import jcx.db.talk;

/**
 * 洗防共用BEAN
 * 
 * Func : 功能項 EX 換名、購屋證明單 
 * RecordType : 功能項細項 EX 客戶資料、代理人資料
 * ActionName : 新增、修改、刪除 
 * errMsg : 符合的樣態內容，或為"不適用" or "不符合" 
 * AMLNo : AML樣態編號
 */

public class AMLyodsBean {
  private talk dbSale = null;
  private talk dbEMail = null;
  private talk db400CRM = null;
  private talk dbEIP = null;
  private talk dbPw0D = null;
  private TalkBean tBean = null;
  
  private String orderNo = "";        // 購屋證明單編號
  private String orderDate = "";      // 購屋證明單日期
  private String docNo = "";          // 收款單編號
  private String eDate = "";          // 收款單日期
  private String contractNo = "";     // 合約編號
  private String cDate = "";          // 合約日期
  
  private String projectID1 = "";     // 案別代碼
  private String trxDate = "";        // 處理日期
  private String actionName = "存檔"; // 存Sale05M070使用
  private String actionNo = "";       // 存Sale05M070使用
  private String func = "";           // 功能 (購屋證明單、收款、合約會審、換名...等等)
  private String recordType = "";     // 甚麼資料 (客戶資料、代理人資料、風險計算...ETC)
  private String customTitle = "";    // 客戶抬頭
  private String customId = "";       // 客戶id
  private String customName = "";     // 客戶名稱
  private String AMLNo = "";          // AML編號
  private String errMsg = "";         // 查詢結果msg
  private String emakerUserNo = "";   // 使用者EMAKER編號
  
  //param
  private RiskCustomBean custBean = null;      // 送檢主要客戶
  private RiskRelatedBean relatedBean = null;  // 送檢關聯人
  private List listCustom = new ArrayList();  // List RiskCustomBean
  private List listBen = new ArrayList();     // List RiskRelatedBean
  private String customNos = "";      // 逗號分隔的custNos
  private String customNames = "";    // 逗號分隔的custNames
  private String orderNos = "";       // 逗號分隔的orderNos

  //萊斯用 - 預設為比對並查詢風險值
  private String lyodsSoapURL = "";   // webservice url
  private String riskResult = "Y";    // Y: 比對，計算風險值, N: 比對，沒有計算風險值, R: 不比對，只計算風險值
  private String checkAll = "Y";      // Y: 檢查所有類別, N: 只檢查制裁名單
  private String modifyData = "Y";    // Y: 更新客戶資料, N: 不更新客戶資料，僅有查詢
  private String addCustomer = "Y";    // Y: 新增, N: 註銷
  private String addAccount = "Y";     // Y: 新增, N: 註銷
  private String calculationCode = "1";     // 是否計算風險評分代碼0: 不計算風險值但進行名單檢測 1: 計算風險值且進行名單檢測 2: 計算風險值但不進行名單檢測 3: 不計算風險值且不進行名單檢測
  
  private boolean isTestServer = true;   // 是否為測試環境
  private boolean updSale05M091 = false;
  private boolean updSale05M277 = false;
  private boolean updSale05M356 = false;
  private boolean upd070Log = false;
  private boolean sendMail = false;
  
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
  public String getProjectID1() {
    return projectID1;
  }
  public void setProjectID1(String projectID1) {
    this.projectID1 = projectID1;
  }
  public String getTrxDate() {
    return trxDate;
  }
  public void setTrxDate(String trxDate) {
    this.trxDate = trxDate;
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
  public String getCustomTitle() {
    return customTitle;
  }
  public void setCustomTitle(String customTitle) {
    this.customTitle = customTitle;
  }
  public String getCustomId() {
    return customId;
  }
  public void setCustomId(String customId) {
    this.customId = customId;
  }
  public String getCustomName() {
    return customName;
  }
  public void setCustomName(String customName) {
    this.customName = customName;
  }
  public String getAMLNo() {
    return AMLNo;
  }
  public void setAMLNo(String aMLNo) {
    AMLNo = aMLNo;
  }
  public String getErrMsg() {
    return errMsg;
  }
  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }
  public String getEmakerUserNo() {
    return emakerUserNo;
  }
  public void setEmakerUserNo(String emakerUserNo) {
    this.emakerUserNo = emakerUserNo;
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
  public List getListCustom() {
    return listCustom;
  }
  public void setListCustom(List listCustom) {
    this.listCustom = listCustom;
  }
  public List getListBen() {
    return listBen;
  }
  public void setListBen(List listBen) {
    this.listBen = listBen;
  }
  public String getCustomNos() {
    return customNos;
  }
  public void setCustomNos(String customNos) {
    this.customNos = customNos;
  }
  public String getCustomNames() {
    return customNames;
  }
  public void setCustomNames(String customNames) {
    this.customNames = customNames;
  }
  public String getOrderNos() {
    return orderNos;
  }
  public void setOrderNos(String orderNos) {
    this.orderNos = orderNos;
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
  public String getLyodsSoapURL() {
    return lyodsSoapURL;
  }
  public void setLyodsSoapURL(String lyodsSoapURL) {
    this.lyodsSoapURL = lyodsSoapURL;
  }
  public boolean isTestServer() {
    return isTestServer;
  }
  public void setTestServer(boolean isTestServer) {
    this.isTestServer = isTestServer;
  }
  public boolean isUpdSale05M091() {
    return updSale05M091;
  }
  public void setUpdSale05M091(boolean updSale05M091) {
    this.updSale05M091 = updSale05M091;
  }
  public boolean isUpdSale05M277() {
    return updSale05M277;
  }
  public void setUpdSale05M277(boolean updSale05M277) {
    this.updSale05M277 = updSale05M277;
  }
  public boolean isUpdSale05M356() {
    return updSale05M356;
  }
  public void setUpdSale05M356(boolean updSale05M356) {
    this.updSale05M356 = updSale05M356;
  }
  public boolean isUpd070Log() {
    return upd070Log;
  }
  public void setUpd070Log(boolean upd070Log) {
    this.upd070Log = upd070Log;
  }
  public boolean isSendMail() {
    return sendMail;
  }
  public void setSendMail(boolean sendMail) {
    this.sendMail = sendMail;
  }
  public String getAddAccount() {
    return addAccount;
  }
  public void setAddAccount(String addAccount) {
    this.addAccount = addAccount;
  }
  public String getCalculationCode() {
    return calculationCode;
  }
  public void setCalculationCode(String calculationCode) {
    this.calculationCode = calculationCode;
  }
  public TalkBean gettBean() {
    return tBean;
  }
  public void settBean(TalkBean tBean) {
    this.tBean = tBean;
  }
  
}
