package Sale.Sale05M274;

import java.util.*;

import Farglory.util.*;
import jcx.db.talk;

public class CheckRiskNew_bk20210413 extends jcx.jform.sproc {
  KUtils util = new KUtils();
  
  public String getDefaultValue(String value) throws Throwable {
    System.out.println("Class >>> Sale.Sale05M274.ChekRiskNew");
    
    RiskCheckBean bean = new RiskCheckBean();
    talk dbSale = getTalk("Sale");
    talk dbEMail = getTalk("eMail");
    talk db400CRM = getTalk("400CRM");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    talk dbDoc = getTalk("Doc");
    bean.setDbSale(dbSale);
    bean.setDbEMail(dbEMail);
    bean.setDb400CRM(db400CRM);
    bean.setDbEIP(dbEIP);
    bean.setUserNo(getUser());
    
    boolean reCheckRisk = false;  //�O�_���s�ˮ�
    String rsMsg = "";  //��ܰT��
    boolean isPROD = false;
    if ("PROD".equals(get("serverType").toString().trim())) {
      isPROD = true;
    }
    
    //�e�����
    String strProjectID1 = getValue("ProjectID1").trim();               // �קO�N�X
    String strContractDate = getValue("ContractDate").trim();           // ñ�����
    String strContractNoDisplay = getValue("ContractNoDisplay").trim(); // �X���s��
    
    //�q���T
    //210112 Kyle : ��\���T�{�L �X��:�q�� = 1:1�A�ҥH�������Ĥ@��
    String sql = "select top 1 a.OrderNo , OrderDate from Sale05M090 a , Sale05M275_New b where a.OrderNo = b.OrderNo and b.ContractNo = '"+strContractNoDisplay+"' order by orderDate ";
    String[][] retOrder = dbSale.queryFromPool(sql);
    
    String strOrderNo = retOrder[0][0].toString().trim();
    String strOrderDate = retOrder[0][1].toString().trim();
    System.out.println("strContractDate>>>" + strContractDate);
    System.out.println("strOrderDate>>>" + strOrderDate);
    
    //TODO: 1. ��������w�ĤT�H���I�ȧP�O
    //TOWN
    Map mapTown = new HashMap();
    sql = "select DISTINCT Town , TownName , ZIP from Town t2 order by Town ";
    String[][] retTown = dbDoc.queryFromPool(sql);
    for(int ii=0 ; ii<retTown.length ; ii++) {
      mapTown.put(retTown[ii][0].toString().trim() , retTown[ii]);
    }
    System.out.println("mapTown>>>" + mapTown);
    
    //CITY
    Map mapCity = new HashMap();
    sql = "select DISTINCT Coun , CounName from City c2 order by Coun ";
    String[][] retCity = dbDoc.queryFromPool(sql);
    for(int ii=0 ; ii<retCity.length ; ii++) {
      mapCity.put(retCity[ii][0].toString().trim() , retCity[ii][1].toString().trim());
    }
    System.out.println("mapCity>>>" + mapCity);
    
    //JOB
    Map mapJob = new HashMap();
    sql = "SELECT distinct CZ02 , CZ09 FROM PDCZPF WHERE CZ01='INDUSTRY' ORDER BY CZ09 ";
    String[][] retJob = db400CRM.queryFromPool(sql);
    for(int ii=0 ; ii<retJob.length ; ii++) {
      mapJob.put(retJob[ii][0].toString().trim() , retJob[ii][1].toString().trim());
    }
    System.out.println(mapJob);
    
    //
    sql = "SELECT DesignatedId, DesignatedName, ExportingPlace  FROM sale05m356 WHERE ContractNo = '" + strContractNoDisplay + "'";
    String[][] retM356 = dbSale.queryFromPool(sql);
    
//    rsMsg += "���w�ĤT�H���I��: \n";
    String[][] retDes = new String[retM356.length][24];
    for (int ii = 0; ii < retM356.length; ii++) {
      String desNo = retM356[ii][0].toString().trim();
      String desName = retM356[ii][1].toString().trim();
      String desCountry = retM356[ii][2].toString().trim();
      
      //                       0            1           2      3     4     5             6 
      sql = "select top 1 NATIONAL_ID , QUERY_TYPE, BIRTHDAY, CITY, TOWN, ADDRESS , JOB_TYPE from QUERY_LOG where QUERY_ID = '" + desNo
          + "' And PROJECT_ID='" + strProjectID1 + "' Order by QID Desc";
      String retLog[][] = dbPW0D.queryFromPool(sql);
      
      if(retLog.length == 0) {
        rsMsg += "���w�ĤT�H" + desName + "�d�L�¦W���ơA�Х�����¦W��d�� \n";
        continue;
      }
      
      System.out.println("CITY>>>" + retLog[ii][3]);
      System.out.println("TOWN>>>" + retLog[ii][4]);
      System.out.println("job>>>" + retLog[ii][6]);
      
      List list = new ArrayList();
      list.add("");
      list.add("");
      list.add("");
      list.add(""); 
      list.add( desCountry );                      //4 CountryName
      list.add( desNo );                           //5 CustomNo
      list.add( desName );                         //6 CustomName
      list.add("");
      list.add( retLog[ii][2].toString().trim() ); //8 Birthday
      list.add("");
      list.add( "" ); //10 PositionName ..
      list.add( ((String[])mapTown.get(retLog[ii][4].toString().trim()))[2].toString().trim() ); //11 ZIP ..
      list.add( mapCity.get(retLog[ii][3].toString().trim()).toString().trim() ); //12 city
      list.add( ((String[])mapTown.get(retLog[ii][4].toString().trim()))[1].toString().trim() ); //13 town
      list.add( retLog[ii][5].toString().trim() ); //14 address
      list.add("");
      list.add( "" ); //16 tel
      list.add( "" ); //17 tel2
      list.add("");
      list.add("");
      list.add("");
      list.add("");
      list.add("");
      list.add(""); //23 statusCd
      list.add(retLog[ii][6].toString().trim()); //24 majorCD
      retDes[ii] = (String[]) list.toArray(new String[list.size()]);
    }
    
    bean.setRetCustom(retDes);
    bean.setOrderNo(strOrderNo);
    bean.setOrderDate(strOrderDate);
    bean.setContractNo(strContractNoDisplay);
    bean.setProjectID1(strProjectID1);
    bean.setActionText("�s�W");
    bean.setFunc("�X��");
    bean.setRecordType("���w�ĤT�H���I�ȭp�⵲�G");
    bean.setUpdSale05M356(true);
    bean.setUpd070Log(true);
    bean.setSendMail(true);
    
    //���歷�I���ˮ�
    RiskCheckTool check = new RiskCheckTool(bean);
    Result rs = check.processRisk();

    // ���浲�G
    String rsStatus = rs.getRsStatus()[3].toString().trim();
    RiskCheckRS rcRs = (RiskCheckRS) rs.getData();
    System.out.println(bean.getFunc() + bean.getRecordType() + ">>>" + rsStatus);

    // �H�oEmail
    if (isPROD) {
      List rsSendMailList = (List) rcRs.getSendMailList();
      for (int ii = 0; ii < rsSendMailList.size(); ii++) {
        SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
        String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
        System.out.println("�H�oMAIL>>>" + sendRS);
      }
    }
    
    // ���I�ȵ��G
    rsMsg += rcRs.getRsMsg();
    //End of 1.
    
    //2. ����Ȥ᭷�I���ˮ�
    long subDays = util.subACDaysRDay(strContractDate, strOrderDate);
    System.out.println("subDays>>>" + subDays);
    
    if(subDays >= 90) reCheckRisk = true; //�I�q��Pñ����W�L90��A�ݭ��s�ˮ֭��I
    
    //�Ȥ��T
    sql = "select a.OrderNo , a.CustomNo , a.CustomName , a.Birthday , a.PositionName , "
        + "a.CountryName , a.ZIP , a.City , a.Town , a.Address , a.Tel , a.Tel2 , a.StatusCd , a.RiskValue , a.IndustryCode "
        + "from Sale05M091 a where a.OrderNo = '" + strOrderNo + "' "
        + "and ISNULL(a.StatusCd , '') != 'C'";
    String[][] retCustomers = dbSale.queryFromPool(sql);
    
//    rsMsg += "�q��Ȥ᭷�I��: \n";
    Transaction trans = new Transaction();
    String[][] retCustom = new String[retCustomers.length][24];
    for(int ii=0 ; ii<retCustomers.length ; ii++) {
      String customNo = retCustomers[ii][1].toString().trim();
      String customName = retCustomers[ii][2].toString().trim();
      String riskValue = retCustomers[ii][13].toString().trim();
          
      if(reCheckRisk) {
        List list = new ArrayList();
        list.add("");
        list.add("");
        list.add("");
        list.add(""); 
        list.add( retCustomers[ii][5].toString().trim() ); //4 CountryName
        list.add( customNo );                              //5 CustomNo
        list.add( customName );                            //6 CustomName
        list.add("");
        list.add( retCustomers[ii][3].toString().trim() ); //8 Birthday
        list.add("");
        list.add( retCustomers[ii][4].toString().trim() ); //10 PositionName
        list.add( retCustomers[ii][6].toString().trim() ); //11 ZIP
        list.add( retCustomers[ii][7].toString().trim() ); //12 city
        list.add( retCustomers[ii][8].toString().trim() ); //13 town
        list.add( retCustomers[ii][9].toString().trim() ); //14 address
        list.add("");
        list.add( retCustomers[ii][10].toString().trim() ); //16 tel
        list.add( retCustomers[ii][11].toString().trim() ); //17 tel2
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add( retCustomers[ii][12].toString().trim() ); //23 statusCd
        list.add( retCustomers[ii][14].toString().trim() ); //24 IndustryCode
        retCustom[ii] = (String[]) list.toArray(new String[list.size()]);
      }else {
        rsMsg += "�Ȥ� " + customName + " �~�����I���� :" + riskValue + "\n";
        String M277Sql = "UPDATE Sale05M277 SET RiskValue = '" + riskValue + "' " + "WHERE ContractNo = '" + strContractNoDisplay + "' AND CustomNo = '"
            + customNo + "' and ISNULL(StatusCd , '') = '';  ";
        trans.append(M277Sql);
      }
    }
    
    //���ݭn���s�P�O�A�N�즹����
    if(!reCheckRisk) {
      trans.close();
      dbSale.execFromPool(trans.getString()); //�H�q��Ȥ᭷�I�ȼg�J�X���Ȥ᭷�I��
      messagebox(rsMsg);
      
      return value;
    }
    
    rsMsg += "����Ȥ� \n";
    
    //����H��T
    sql = "select a.OrderNo , a.CustomNo , a.BCustomNo , a.BenName  , a.Birthday , a.HoldType , a.CountryName , a.StatusCd "
        + "from Sale05M091Ben a where a.OrderNo = '" + strOrderNo + "' "
        + "and ISNULL(a.StatusCd , '') != 'C'";
    String[][] retBens = dbSale.queryFromPool(sql);
    
    String[][] retSBen = new String[retBens.length][13];
    for(int ii=0 ; ii<retBens.length ; ii++) {
      List list = new ArrayList();
      list.add(""); 
      list.add( retBens[ii][1].toString().trim() ); //1. �k�H�νs
      list.add("");
      list.add( retBens[ii][3].toString().trim() ); //3. ����HNAME
      list.add( retBens[ii][2].toString().trim() ); //4. ����HID
      list.add( retBens[ii][4].toString().trim() ); //5. �ͤ�
      list.add( retBens[ii][6].toString().trim() ); //6. ��a
      list.add( retBens[ii][5].toString().trim() ); //7. ���Y
      list.add("");
      list.add("");
      list.add("");
      list.add("");
      list.add( retBens[ii][7].toString().trim() );  //12. ���A
      retSBen[ii] = (String[]) list.toArray(new String[list.size()]);
    }
    
    bean.setRetCustom(retCustom);
    bean.setRetSBen(retSBen);
    bean.setOrderNo(strOrderNo);
    bean.setContractNo(strContractNoDisplay);
    bean.setProjectID1(strProjectID1);
    bean.setOrderDate(strOrderDate);
    bean.setActionText("�s�W");
    bean.setFunc("�X��");
    bean.setRecordType("�Ȥ᭷�I�ȭp�⵲�G");
    bean.setUpdSale05M277(true);
    bean.setUpd070Log(true);
    bean.setSendMail(true);
    
    //���歷�I���ˮ�
    RiskCheckTool check2 = new RiskCheckTool(bean);
    Result rs2 = check2.processRisk();

    // ���浲�G
    String rsStatus2 = rs2.getRsStatus()[3].toString().trim();
    RiskCheckRS rcRs2 = (RiskCheckRS) rs2.getData();
    System.out.println("���浲�G>>>" + rsStatus2);

    // �H�oEmail
    if (isPROD) {
      List rsSendMailList = (List) rcRs2.getSendMailList();
      for (int ii = 0; ii < rsSendMailList.size(); ii++) {
        SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
        String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
        System.out.println("�H�oMAIL>>>" + sendRS);
      }
    }
    
    // ���I�ȵ��G
    rsMsg += rcRs2.getRsMsg();
    messagebox(rsMsg);

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
