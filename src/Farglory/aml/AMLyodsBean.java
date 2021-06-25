package Farglory.aml;

import java.util.ArrayList;
import java.util.List;

import Farglory.util.TalkBean;
import jcx.db.talk;

/**
 * �~���@��BEAN
 * 
 * Func : �\�ඵ EX ���W�B�ʫ��ҩ��� 
 * RecordType : �\�ඵ�Ӷ� EX �Ȥ��ơB�N�z�H���
 * ActionName : �s�W�B�ק�B�R�� 
 * errMsg : �ŦX���˺A���e�A�ά�"���A��" or "���ŦX" 
 * AMLNo : AML�˺A�s��
 */

public class AMLyodsBean {
  private talk dbSale = null;
  private talk dbEMail = null;
  private talk db400CRM = null;
  private talk dbEIP = null;
  private talk dbPw0D = null;
  private TalkBean tBean = null;
  
  private String orderNo = "";        // �ʫ��ҩ���s��
  private String orderDate = "";      // �ʫ��ҩ�����
  private String docNo = "";          // ���ڳ�s��
  private String eDate = "";          // ���ڳ���
  private String contractNo = "";     // �X���s��
  private String cDate = "";          // �X�����
  
  private String projectID1 = "";     // �קO�N�X
  private String trxDate = "";        // �B�z���
  private String actionName = "�s��"; // �sSale05M070�ϥ�
  private String actionNo = "";       // �sSale05M070�ϥ�
  private String func = "";           // �\�� (�ʫ��ҩ���B���ڡB�X���|�f�B���W...����)
  private String recordType = "";     // �ƻ��� (�Ȥ��ơB�N�z�H��ơB���I�p��...ETC)
  private String customTitle = "";    // �Ȥ���Y
  private String customId = "";       // �Ȥ�id
  private String customName = "";     // �Ȥ�W��
  private String AMLNo = "";          // AML�s��
  private String errMsg = "";         // �d�ߵ��Gmsg
  private String emakerUserNo = "";   // �ϥΪ�EMAKER�s��
  
  //param
  private RiskCustomBean custBean = null;      // �e�˥D�n�Ȥ�
  private RiskRelatedBean relatedBean = null;  // �e�����p�H
  private List listCustom = new ArrayList();  // List RiskCustomBean
  private List listBen = new ArrayList();     // List RiskRelatedBean
  private String customNos = "";      // �r�����j��custNos
  private String customNames = "";    // �r�����j��custNames
  private String orderNos = "";       // �r�����j��orderNos

  //�ܴ��� - �w�]�����ìd�߭��I��
  private String lyodsSoapURL = "";   // webservice url
  private String riskResult = "Y";    // Y: ���A�p�⭷�I��, N: ���A�S���p�⭷�I��, R: �����A�u�p�⭷�I��
  private String checkAll = "Y";      // Y: �ˬd�Ҧ����O, N: �u�ˬd����W��
  private String modifyData = "Y";    // Y: ��s�Ȥ���, N: ����s�Ȥ��ơA�Ȧ��d��
  private String addCustomer = "Y";    // Y: �s�W, N: ���P
  private String addAccount = "Y";     // Y: �s�W, N: ���P
  private String calculationCode = "1";     // �O�_�p�⭷�I�����N�X0: ���p�⭷�I�Ȧ��i��W���˴� 1: �p�⭷�I�ȥB�i��W���˴� 2: �p�⭷�I�Ȧ����i��W���˴� 3: ���p�⭷�I�ȥB���i��W���˴�
  
  private boolean isTestServer = true;   // �O�_����������
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
