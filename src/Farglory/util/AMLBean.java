package Farglory.util;

/**
 * funcName(Func) : 功能項 EX 換名、購屋證明單 
 * funcName2(RecordType) : 功能項細項 EX 客戶資料、代理人資料
 * ActionName(ActionName) : 新增、修改、刪除 
 * errMsg : 符合的樣態內容，或為"不適用" or "不符合" 
 * AMLNo : AML樣態編號
 */

public class AMLBean {
  private String orderNo = "";        // 購屋證明單編號
  private String orderDate = "";      // 購屋證明單日期
  private String projectID1 = "";     // 案別代碼
  private String trxDate = "";        // 日期
  private String actionName = "存檔"; // 存Sale05M070使用
  private String actionNo = "";       // 存Sale05M070使用
  private String funcName = "";       // 功能 (購屋證明單、收款、合約會審、換名...等等)
  private String funcName2 = "";      // 功能資料類 (客戶資料、代理人資料...)
  private String customTitle = "";    //客戶抬頭
  private String customId = "";       //客戶id
  private String customName = "";     //客戶名稱
  private String AMLNo = "";          //AML編號
  private String errMsg = "";         //查詢結果msg
  
  private String docNo = "";          //收款單編號
  private String bDate = "";          //不知道
  private String eDate = "";          //收款單日期
  
  private String contractNo = "";     //合約編號
  private String cDate = "";          //合約日期
  
  private String customNos = "";
  private String customNames = "";
  private String orderNos = ""; 
  
  private String orderOrDocNo = "";

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getProjectID1() {
    return projectID1;
  }

  public String getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate;
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

  public String getFuncName() {
    return funcName;
  }

  public void setFuncName(String funcName) {
    this.funcName = funcName;
  }

  public String getFuncName2() {
    return funcName2;
  }

  public void setFuncName2(String funcName2) {
    this.funcName2 = funcName2;
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

  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  public String getbDate() {
    return bDate;
  }

  public void setbDate(String bDate) {
    this.bDate = bDate;
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

  public String getOrderOrDocNo() {
    return orderOrDocNo;
  }

  public void setOrderOrDocNo(String orderOrDocNo) {
    this.orderOrDocNo = orderOrDocNo;
  }
}
