package Farglory.util;

/**
 * funcName(Func) : �\�ඵ EX ���W�B�ʫ��ҩ��� 
 * funcName2(RecordType) : �\�ඵ�Ӷ� EX �Ȥ��ơB�N�z�H���
 * ActionName(ActionName) : �s�W�B�ק�B�R�� 
 * errMsg : �ŦX���˺A���e�A�ά�"���A��" or "���ŦX" 
 * AMLNo : AML�˺A�s��
 */

public class AMLBean {
  private String orderNo = "";        // �ʫ��ҩ���s��
  private String orderDate = "";      // �ʫ��ҩ�����
  private String projectID1 = "";     // �קO�N�X
  private String trxDate = "";        // ���
  private String actionName = "�s��"; // �sSale05M070�ϥ�
  private String actionNo = "";       // �sSale05M070�ϥ�
  private String funcName = "";       // �\�� (�ʫ��ҩ���B���ڡB�X���|�f�B���W...����)
  private String funcName2 = "";      // �\������ (�Ȥ��ơB�N�z�H���...)
  private String customTitle = "";    //�Ȥ���Y
  private String customId = "";       //�Ȥ�id
  private String customName = "";     //�Ȥ�W��
  private String AMLNo = "";          //AML�s��
  private String errMsg = "";         //�d�ߵ��Gmsg
  
  private String docNo = "";          //���ڳ�s��
  private String bDate = "";          //�����D
  private String eDate = "";          //���ڳ���
  
  private String contractNo = "";     //�X���s��
  private String cDate = "";          //�X�����
  
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
