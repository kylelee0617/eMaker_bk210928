package Farglory.aml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import com.fglife.soap.client.BlickListClient;
import com.fglife.soap.cr.MainQuery;
import com.fglife.soap.cr.MainReply;
import com.fglife.soap.cr.RenewRelatedQuery;
import com.fglife.soap.cr.RenewRelatedReply;

import Farglory.util.KUtils;
import Farglory.util.Result;
import Farglory.util.ResultStatus;
import jcx.db.talk;
import jcx.jform.bvalidate;

/**
 * for �ܴ�
 * 
 * @author B04391
 *
 */

public class LyodsTools extends bvalidate {
  // DB
  talk db400 = null;
  talk dbSale = null;
  talk dbEIP = null;
  //param
  String empNo = "";
  String serverType = "";
  String lyodsSoapURL = "";
  String sysType = "RYB";// ���ʲ���PB �P��C
  boolean isTest = true;
  //util
  KUtils kutil = new KUtils();
  AMLyodsBean lyodsBean;

  public LyodsTools() throws Throwable {
  }

  public LyodsTools(AMLyodsBean lyodsBean) throws Throwable {
    this.lyodsBean = lyodsBean;
    db400 = lyodsBean.getDb400CRM();
    dbSale = lyodsBean.getDbSale();
    dbEIP = lyodsBean.getDbEIP();
    isTest = lyodsBean.isTestServer();
    lyodsSoapURL = lyodsBean.getLyodsSoapURL();

    this.getEMPNO();  //�u���ഫ
  }
  
  /**
   * �g�J�q��-�Ȥ�A�ç�s���p�H
   * @return
   * @throws Throwable
   */
  public Result renewRelated() throws Throwable{
    Result rs = new Result();
    RiskCustomBean cBean = this.lyodsBean.getCustBean();
    String custNo = cBean.getCustomNo();
    
    BlickListClient blickListClient = new BlickListClient(lyodsSoapURL);  
    RenewRelatedQuery renewRelatedQuery = new RenewRelatedQuery();        
    renewRelatedQuery.setWriteLog("Y");                                   //�g�JLOG v
    renewRelatedQuery.setCustomerType(this.isCompany(custNo)? "2":"1");   //�D�n�Ȥ����O v
    renewRelatedQuery.setId(custNo);                                      //�����Ҧr��/�νs v
    renewRelatedQuery.setKeyNumber(lyodsBean.getOrderNo());               //�O�渹�X/�������X v
    renewRelatedQuery.setCaseNumber(lyodsBean.getOrderNo());              //�ץ�s�� v
    renewRelatedQuery.setSystemCode("RYB");                               //�t�ΥN�X v
    renewRelatedQuery.setProgramCode("RYB");                              //�{���N�X v
    renewRelatedQuery.setUserId(this.empNo);                              //�ӿ�H���s v
    renewRelatedQuery.setUserUnit("");                                    //�ӿ�H��� v
    RenewRelatedReply renewRelatedReply = blickListClient.executeRenewRelated(renewRelatedQuery);
    
    rs.setData(renewRelatedReply);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  public Result checkRisk() throws Throwable {
    Result rs = new Result();
    
    RiskCustomBean cBean = this.lyodsBean.getCustBean();
    String custNo = cBean.getCustomNo();
    String custName = cBean.getCustomName();

    if (isTest) {
      System.out.println("LydosTools check custom>>>" + custNo + "," + custName);
    }

    // 210114 Kyle : ��~�O
    String industryCode = cBean.getIndustryCode() != null ? cBean.getIndustryCode() : "";
    if ("".equals(industryCode)) {
      // �¸�ƨS����~�O�N�X
      String sql = "SELECT CZ02,CZ09 FROM PDCZPF WHERE CZ01='INDUSTRY' And CZ09 = '" + cBean.getMajorName() + "' ";
      String[][] retMajor = db400.queryFromPool(sql);
      if (retMajor.length > 0)
        industryCode = retMajor[0][0] != null ? retMajor[0][0].trim() : "";
    }

    // �g�z�H?
    String sqltopmanager = " SELECT TOP 1 PositionCD, PName, ChairMan From A_Position " + " WHERE PName = '" + cBean.getPositionName() + "'" + " ORDER BY PositionCD DESC ";
    String retPosition2[][] = dbSale.queryFromPool(sqltopmanager);
    String isManager = "N";
    if (retPosition2.length > 0) {
      isManager = retPosition2[0][2];
    }

    // �k�H �۵M�H �ʧO
    String type = "1";
    String sex = "";
    if (custNo.length() == 8) {
      type = "2";// N: �ӤH C: ���q F: �~��H
    } else {
      // ����H�ʧO
      if (custNo.charAt(1) == '1') {
        sex = "M";
      } else if (custNo.charAt(1) == '2') {
        sex = "F";
      }
    }

    // ���y��X(�Y�Źw�]TWN)
    String strCZ09 = "";
    if ("".equals(cBean.getCountryName().trim())) {
      strCZ09 = "���إ���";
    } else {
      strCZ09 = cBean.getCountryName().trim();
    }
    String strSaleSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCZ09 + "'";
    String retCNYCode[][] = db400.queryFromPool(strSaleSql);
    String cnyCode = retCNYCode[0][0].trim();
    System.out.println("cnyCode=====> :" + cnyCode);

    // �ո�ơASOAP
    try {
      BlickListClient blickListClient = new BlickListClient(lyodsSoapURL);
      MainQuery mainQuery = new MainQuery();
      mainQuery.setRiskResult(lyodsBean.getRiskResult());   // �d���I�� v
      mainQuery.setCheckAll(lyodsBean.getCheckAll());       // �d�Ҧ����O v
      mainQuery.setChangeOrgnization("Y");                  // ��������� v
      mainQuery.setAddCustomer(lyodsBean.getAddCustomer()); // �s�W�ε��P�D�n�Ȥ� v
      mainQuery.setAddAccount(lyodsBean.getAddAccount());   // �s�W�ε��P�O�� v
      mainQuery.setWriteLog("Y");                           // �g�JLOG v
      mainQuery.setModifyData(lyodsBean.getModifyData());   // ��s�Ȥ���
      mainQuery.setCustomerType(type);                      // �D�n�Ȥ����O v
      mainQuery.setChineseName(custName);                   // ����m�W
      mainQuery.setEnglishName("");                         // �^��m�W
      mainQuery.setId(custNo);                              // �����Ҧr�� v
      mainQuery.setSex(sex);                                // �ʧO
      mainQuery.setBirth("");                               // �ͤ�
      mainQuery.setRegisterNation(cnyCode);                 // �~��a���y
      mainQuery.setBirthNation("");                         // �X�ͦa���y
      mainQuery.setProfession("");                          // ¾�~����
      mainQuery.setProfession2("");                         // ��¾�~����
      mainQuery.setIndustry(industryCode);                  // ��~���O
      mainQuery.setIndustry2("");                           // ����~���O
      mainQuery.setKeyNumber(lyodsBean.getOrderNo());       // �O�渹�X/�������X v
      mainQuery.setCaseNumber(lyodsBean.getOrderNo());      // �ץ�s�� v (��q��s���@�˧Y�i)
      mainQuery.setSystemCode(this.sysType);                // �t�ΥN�X V (��{���N�X�@�˧Y�i)
      mainQuery.setProgramCode(this.sysType);               // �{���N�X v
      mainQuery.setApplyDate("");                           // ���z/�ӽФ��
      mainQuery.setContractDate(lyodsBean.getOrderDate());  // �����ͮĤ��
      mainQuery.setProduct("");                             // ���~�N�X
      mainQuery.setChannel("");                             // �q���N�X
      mainQuery.setUserId(this.empNo);                      // �ӿ�H���s v
      mainQuery.setUserUnit("");                            // �ӿ�H���
      MainReply mainReply = blickListClient.executeMain(mainQuery);

      // ���I�ȵ��G��X
      rs.setData(mainReply);
      rs.setRsStatus(ResultStatus.SUCCESS);
    }
    catch(Exception ex){
      rs.setExp(ex);
      rs.setRsStatus(ResultStatus.ERROR);
    }

    return rs;
  }
  
  /**
   * is�k�H?
   * @param id
   * @return
   */
  private boolean isCompany(String id) {
    boolean brs = false;
    String[] spCustNo = id.split("");
    if (!spCustNo[0].matches("[A-Z]+") && spCustNo.length == 8) brs = true;
    
    return brs;
  }
  
  // �u���ഫ
  public void getEMPNO() throws Throwable {
    String stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + lyodsBean.getEmakerUserNo() + "'";
    String[][] retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      this.empNo = retEip[0][0];
    }
  }

  public boolean check(String value) throws Throwable {
    return false;
  }

}
