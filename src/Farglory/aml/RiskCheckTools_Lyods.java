package Farglory.aml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.fglife.soap.cr.MainReply;

import Farglory.util.Result;
import Farglory.util.ResultStatus;
import Farglory.util.RiskCheckRS;
import Farglory.util.SendMailBean;
import Farglory.util.Transaction;
import jcx.db.talk;
import jcx.jform.bvalidate;

public class RiskCheckTools_Lyods extends bvalidate {
  String serverIP, serverName; // ���A��IP , NAME
  talk dbSale, dbEMail, dbBen, dbEIP = null;
  String sysType = "RYB";// ���ʲ���PB �P��C
  String strProjectID1 = "";
  String strOrderNo = "";
  String strOrderDate = "";
  
  ResultStatus rsStatus = new ResultStatus();
  String serverType = "";
  String lyodsSoapURL = "";

  AMLyodsBean aml = null;
  boolean isTest = true;
  String stringSQL = "";
  String testFlag = "";
  String userNo = "";
  String empNo = "";
  String sysdate = "";
  String systime = "";
  String actionNo = "";
  String strHouse = "";
  String strCar = "";

  public RiskCheckTools_Lyods(AMLyodsBean bean) throws Throwable {
    this.aml = bean;
    strProjectID1 = aml.getProjectID1();
    strOrderNo = aml.getOrderNo();
    strOrderDate = aml.getOrderDate();
    System.out.println("strOrderNo=====>" + strOrderNo);
    System.out.println("strProjectID1=====>" + strProjectID1);
    System.out.println("strOrderDate=====>" + strOrderDate);
    
    //config
    lyodsSoapURL = this.aml.getLyodsSoapURL();
    
    //ip & serverName
    isTest = this.aml.isTestServer();
    if (isTest) {
      testFlag = " (����) ";
      System.out.println("����>>>" + testFlag);
    }

    //params
    this.dbSale = bean.getDbSale();
    this.dbEMail = bean.getDbEMail();
    this.dbBen = bean.getDb400CRM();
    this.dbEIP = bean.getDbEIP();

    // Emaker�u��
    this.userNo = bean.getEmakerUserNo();
    // FG�u��
    this.getEMPNO();
    // �t�Τ���ɶ�
    this.getDateTime();
    // action
    this.getActionNo();
    // HouseCar
    this.getHouseCar();
  }

  /**
   * ��X: 
   * 
   * @return
   * @throws Throwable
   */
  public Result processRisk(RiskCustomBean[] cBeans) throws Throwable {
    Result rs = new Result();
    RiskCheckRS rcRS = new RiskCheckRS();
//    rs.setRsStatus(ResultStatus.SUCCESSBUTSOMEERROR);

    String riskValue = "";
    String riskPoint = "";
    ArrayList list = new ArrayList();
    
    try {
      String msgboxtext = "";
      String tmpMsgText = "";
      
//      List custList = this.aml.getListCustom();
//      for(int i=0 ; i<custList.size() ; i++) {
      for(int i=0 ; i<cBeans.length ; i++) {
        RiskCustomBean cBean = cBeans[i];
        String custNo = cBean.getCustomNo();
        String custName = cBean.getCustomName();
        
        if (isTest) {
          System.out.println("custom>>>" + i + "-" + cBean.getCustomNo() + "," + cBean.getCustomName());
        }
        
//        this.aml.setOrderNo(this.strOrderNo);
//        this.aml.setOrderDate(this.strOrderDate);
//        this.aml.setEmakerUserNo(getUser());
//        this.aml.setCustBean(cBean);
        this.aml.setRiskResult("Y");
        this.aml.setCheckAll("Y");
        LyodsTools lyodsTools = new LyodsTools(aml);
        MainReply mainReply = (MainReply) lyodsTools.checkRisk().getData();
        
        //�T���B�z
        String resultMsg = !"".equals(mainReply.getMessage().trim())? mainReply.getMessage().trim() : mainReply.getRiskLevel().trim();
        
        riskPoint = mainReply.getRiskScore().trim();
        riskValue = mainReply.getRiskLevel().trim();
        System.out.println("19�~�����I�� :" + riskPoint);
        System.out.println("20�~�����I���� :" + riskValue);
        
        String customTitle = "�Ȥ� ";
        if(this.aml.getRecordType().indexOf("���w�ĤT�H") != -1) {
          customTitle = "���w�ĤT�H "; 
        }
        
        msgboxtext += customTitle + custName + " �~�����I���� :" + resultMsg + "\n";
        tmpMsgText += custName + "\t" 
                   + resultMsg.trim() + "\t" 
                   + riskPoint.trim() + "\t" 
                   + mainReply.getCustomerRisk().trim() + "\t"
                   + mainReply.getNationRisk().trim() + "\t" 
                   + mainReply.getChannelRisk().trim() + "\t" 
                   + mainReply.getProductRisk().trim() + "\n";

        HashMap m = new HashMap();
        m.put("p01", strProjectID1);
        m.put("p02", strHouse.toUpperCase());
        m.put("p025", strCar.toUpperCase());
        m.put("p03", custName);
        m.put("p035", custNo);
        m.put("p04", strOrderDate);
        m.put("p05", riskPoint);
        m.put("p06", riskValue.replace("���I", ""));
        m.put("riskValue", riskValue);
        list.add(m);        
      }
      
      // ���I�ȵ��G��X
      rcRS.setRsMsg(msgboxtext);

      // ��s�Ȥ᭷�I��
      if (this.aml.isUpdSale05M091()) this.updSaleM091(list);
      
      //��s�X���Ȥ᭷�I��
      if (this.aml.isUpdSale05M277()) this.updSaleM277(list);
      
      //��s�X�����w�ĤT�H���I��
      if (this.aml.isUpdSale05M356()) this.updSaleM356(list);
      
      // insert into Sale05M070
      if (this.aml.isUpd070Log()) this.insSale05M070(list);

      // �զ�MAIL
      SendMailBean smBean = new SendMailBean();
      smBean = this.sendMail(list);

      List sendMailList = new ArrayList();
      sendMailList.add(smBean);
      rcRS.setSendMailList(sendMailList);

      rs.setData(rcRS);
      rs.setRsStatus(ResultStatus.SUCCESS);
      
    } catch (Exception e) {
      System.out.println("���~�T��:" + e.toString());
      rs.setExp(e);
      rs.setRsStatus(ResultStatus.ERROR);
      
    }

    return rs;
  }
  

  // ��empNo
  public void getEMPNO() throws Throwable {
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + this.userNo + "'";
    String[][] retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      this.empNo = retEip[0][0];
    }else {
      System.out.println(">>>None EmpNo<<<");
      empNo = "FGLife";
    }
  }

  // �t�Τ���ɶ�
  public void getDateTime() throws Throwable {
    sysdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
    systime = new SimpleDateFormat("HHmmss").format(new Date());
  }

  // actionNo
  public void getActionNo() throws Throwable {
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    this.actionNo = this.sysdate + this.systime + ram;
    System.out.println("strActionNo>>>" + this.actionNo);
  }

  // �ɼӧO
  public void getHouseCar() throws Throwable {
    String sql092 = "SELECT HouseCar,Position FROM Sale05M092 WHERE OrderNo = '" + aml.getOrderNo() + "' ORDER BY RecordNo";
    String[][] retPosition = dbSale.queryFromPool(sql092);
    if (retPosition.length > 0) {
      for (int a = 0; a < retPosition.length; a++) {
        if ("House".equals(retPosition[a][0].trim())) {
          if ("".equals(strHouse)) {
            strHouse = retPosition[a][1].trim();
          } else {
            strHouse = strHouse + "," + retPosition[a][1].trim();
          }
        } else {
          if ("".equals(strCar)) {
            strCar = retPosition[a][1].trim();
          } else {
            strCar = strCar + "," + retPosition[a][1].trim();
          }
        }
      }
    }
  }

  /**
   * �^�gSale05M091 �Ȥ᭷�I��
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String updSaleM091(List customList) throws Throwable {
    System.out.println("�^�g05M091���----------" + customList.size() + "------------------S");

    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String M091Sql = "UPDATE Sale05M091 SET RiskValue = '" + data.get("riskValue").toString().trim() + "' " 
                     + "WHERE OrderNo = '" + this.aml.getOrderNo() + "' AND CustomNo = '" + data.get("p035").toString().trim() + "' and ISNULL(StatusCd , '') = '';  ";
//      dbSale.execFromPool(M091Sql);
      trans.append(M091Sql);
    }
    trans.close();
    dbSale.execFromPool(trans.getString());

    System.out.println("�^�g05M091���----------" + customList.size() + "------------------E");
    return "0";
  }

  /**
   * �^�gSale05M277 �X���Ȥ᭷�I��
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String updSaleM277(List customList) throws Throwable {
    System.out.println("�^�g05M277���----------" + customList.size() + "------------------S");

    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String M277Sql = "UPDATE Sale05M277 "
      				 + "SET RiskValue = '" + data.get("riskValue").toString().trim() + "' " 
      				 + "WHERE ContractNo = '" + this.aml.getContractNo() + "' "
      				 + "AND CustomNo = '" + data.get("p035").toString().trim() + "' "
      				 + "AND ISNULL(StatusCd , '') = '' ";
      trans.append(M277Sql);
    }
    trans.close();
    dbSale.execFromPool(trans.getString());

    System.out.println("�^�g05M277���----------" + customList.size() + "------------------E");
    return "0";
  }
  
  /**
   * �^�gSale05M356 �X�����w�ĤT�H���I��
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String updSaleM356(List customList) throws Throwable {
    System.out.println("�^�g05M356���----------" + customList.size() + "------------------S");

    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String M277Sql = "UPDATE Sale05M356 "
      				 + "SET RiskValue = '" + data.get("riskValue").toString().trim() + "' " 
      				 + "WHERE ContractNo = '" + this.aml.getContractNo() + "' "
      				 + "  AND DesignatedId = '" + data.get("p035").toString().trim() + "' ";
      trans.append(M277Sql);
    }
    trans.close();
    dbSale.execFromPool(trans.getString());

    System.out.println("�^�g05M277���----------" + customList.size() + "------------------E");
    return "0";
  }

  /**
   * �g�JSale05M070
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String insSale05M070(List customList) throws Throwable {
    System.out.println("�s�J05M070���-----------------------------------S");

    // �Ǹ�
    int intRecordNo = 1;
    if (this.aml.getFunc().indexOf("�ʫ�") == 0 && !"".equals(this.aml.getOrderNo())) {
      stringSQL = "SELECT MAX(CAST(RecordNo AS decimal(18, 0))) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + this.aml.getOrderNo() + "'";
    } else if (this.aml.getFunc().indexOf("�X��") == 0 && !"".equals(this.aml.getContractNo())) {
      stringSQL = "SELECT MAX(CAST(RecordNo AS decimal(18, 0))) AS MaxNo FROM Sale05M070 WHERE ContractNo ='" + this.aml.getContractNo() + "'";
    }
    String[][] ret05M070 = dbSale.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("insSale05M070 intRecordNo >>>" + intRecordNo);

    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String strCustomNo = data.get("p035").toString().trim();
      String strCustomName = data.get("p03").toString().trim();
      String riskValue = data.get("riskValue").toString().trim();
      stringSQL = "INSERT INTO Sale05M070 "
          + "(OrderNo ,ContractNo ,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate ,CDate ,SHB00,SHB06A,SHB06B,SHB06,SHB97, SHB98, SHB99) " 
          + "VALUES "
          + "('" + this.aml.getOrderNo() + "' ,'"+this.aml.getContractNo()+"' ,'" + this.aml.getProjectID1() + "','" + intRecordNo + "','" + actionNo + "','" + this.aml.getFunc() + "','"
          + this.aml.getRecordType() + "' " + ",'" + this.aml.getActionName() + "','���I��:" + riskValue + "','" + strCustomNo + "' " + ",'" + strCustomName + "','"
          + this.aml.getOrderDate() + "' ,'"+this.aml.getcDate()+"' ,'RY','773','022','���I��:" + riskValue + "','" + empNo + "'," + this.sysdate + "," + this.systime + ") ";
      trans.append(stringSQL);
      intRecordNo++;
    }
    trans.close();
    dbSale.execFromPool(trans.getString());

    System.out.println("�s�J05M070���-----------------------------------E");
    return "0";
  }

  // �oMAIL
  public SendMailBean sendMail(List customList) throws Throwable {
    System.out.println("�զ� EMAIL-----------------------------------S");
    String userEmail = "";
    String userEmail2 = "";
    String DPCode = "";
    String DPManageemNo = "";
    String DPeMail = "";
    String DPeMail2 = "";
    String[][] reteMail = null;

    ////////////////
    stringSQL = "SELECT DP_CODE,PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPCode = reteMail[0][0];
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        userEmail = reteMail[0][1];
      }
      if (reteMail[0][2] != null && !reteMail[0][2].equals("")) {
        userEmail2 = reteMail[0][2];
      }
    }
    System.out.println("DPCode===>" + DPCode);
    System.out.println("userEmail===>" + userEmail);
    System.out.println("userEmail2===>" + userEmail2);
    
    /////////////////////////////////////////////////
    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + DPCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPManageemNo = reteMail[0][0];
    }
    System.out.println("DPManageemNo===>" + DPManageemNo);
    
    /////////////////////////////////////////////////
    stringSQL = "SELECT PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + DPManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      if (reteMail[0][0] != null && !reteMail[0][0].equals("")) {
        DPeMail = reteMail[0][0];
      }
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        DPeMail2 = reteMail[0][1];
      }
    }
    System.out.println("DPeMail===>" + DPeMail);
    System.out.println("DPeMail2===>" + DPeMail2);
    
    /////////////////////////////////////////////////////////
    String PNCode = "";
    String PNManageemNo = "";
    String PNMail = "";
    stringSQL = "SELECT PN_DEPTCODE FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNCode = reteMail[0][0];
    System.out.println("PNCode===>" + PNCode);
    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + PNCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNManageemNo = reteMail[0][0];
    System.out.println("PNManageemNo===>" + PNManageemNo);
    stringSQL = "SELECT PN_EMAIL1 FROM PERSONNEL WHERE PN_EMPNO='" + PNManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNMail = reteMail[0][0];
    System.out.println("PNMail===>" + PNMail);
    ////////////////////////////////////////////////////////////////////////////////////////

    // send email
    boolean hilitgt = false;
    String table1 = "<table style='text-align:center;' border=1><tr><td>�קO</td><td>�ɼӧO</td><td>����O</td><td>�Ȥ�W��</td><td>�I�q���</td><td>���I��X��</td><td>�Ȥ᭷�I����</td><td>����</td></tr>";
    String tail = "</table>";
    String contextsample = "<tr><td>${p01}</td><td>${p02}</td><td>${p025}</td><td>${p03}</td><td>${p04}</td><td>${p05}</td><td>${p06}</td><td align='left' valign='center'>${p07}</td></tr>";
    String cbottom = "</body></html>";
    String context = this.aml.getProjectID1() + "��" + strHouse + "���ʲ��q�ʫȤ᭷�I���ŵ������G�q��" + testFlag + "<BR>";

    context = table1;
    for (int i = 0; i < customList.size(); i++) {
      HashMap cm = (HashMap) customList.get(i);

      String l1 = new String(contextsample);
      l1 = l1.replace("${p01}", (String) cm.get("p01"));
      l1 = l1.replace("${p02}", (String) cm.get("p02"));
      l1 = l1.replace("${p025}", (String) cm.get("p025"));
      l1 = l1.replace("${p03}", (String) cm.get("p03"));
      l1 = l1.replace("${p035}", (String) cm.get("p035"));
      l1 = l1.replace("${p04}", (String) cm.get("p04"));
      l1 = l1.replace("${p05}", (String) cm.get("p05"));
      l1 = l1.replace("${p06}", (String) cm.get("p06"));
      String tempPo6 = "" + cm.get("p06");
      tempPo6 = tempPo6.trim();
      if ("��".equals(tempPo6.trim())) {
        l1 = l1.replace("${p07}", "�~���θꮣ���I������" + tempPo6 + "���I�Ȥ�A�Ш̬~������@�~�W�w�A����[�j���ޱ����I");
        hilitgt = true;
      } else {
        l1 = l1.replace("${p07}", "�~���θꮣ���I������" + tempPo6 + "���I�Ȥ�");
      }

      context = context + l1;
    } // customList for End
    context = context + tail + cbottom;
 
    String subject = this.aml.getProjectID1() + "��" + strHouse + "���ʲ�" + this.aml.getFunc() + this.aml.getRecordType() + "�q��" + testFlag;
    SendMailBean send = new SendMailBean();
    send.setColm1("ex.fglife.com.tw");
    send.setColm2("Emaker-Invoice@fglife.com.tw");
    send.setSubject(subject);
    send.setContext(context);
    send.setColm6(null);
    send.setColm7("");
    send.setColm8("text/html");

    if (hilitgt) {
      String[] arrayUser = { "Kyle_Lee@fglife.com.tw", userEmail, DPeMail, PNMail };
      send.setArrayUser(arrayUser);
    } else {
      String[] arrayUser = { "Kyle_Lee@fglife.com.tw", userEmail };
      send.setArrayUser(arrayUser);
    }
    System.out.println("�զ� EMAIL-----------------------------------E");

    return send;
  }
  

  // �O�Ʀr�^�� true�A�_�h�^�� false�C
  public boolean check(String value) throws Throwable {
    return false;
  }

}
