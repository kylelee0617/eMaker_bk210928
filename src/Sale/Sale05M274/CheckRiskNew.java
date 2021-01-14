package Sale.Sale05M274;

import java.util.*;

import Farglory.util.*;
import jcx.db.talk;

public class CheckRiskNew extends jcx.jform.sproc {
  
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
    
    boolean reCheckRisk = false;  //是否重新檢核
    String rsMsg = "";  //顯示訊息
    boolean isPROD = false;
    if ("PROD".equals(get("serverType").toString().trim())) {
      isPROD = true;
    }
    
    //畫面欄位
    String strProjectID1 = getValue("ProjectID1").trim();               // 案別代碼
    String strContractDate = getValue("ContractDate").trim();           // 簽約日期
    String strContractNoDisplay = getValue("ContractNoDisplay").trim(); // 合約編號
    
    //訂單資訊
    //210112 Kyle : 跟珮筑確認過 合約:訂單 = 1:1，所以直接取第一筆
    String sql = "select top 1 a.OrderNo , OrderDate from Sale05M090 a , Sale05M275_New b where a.OrderNo = b.OrderNo and b.ContractNo = '"+strContractNoDisplay+"' order by orderDate ";
    String[][] retOrder = dbSale.queryFromPool(sql);
    
    String strOrderNo = retOrder[0][0].toString().trim();
    String strOrderDate = retOrder[0][1].toString().trim();
    System.out.println("strContractDate>>>" + strContractDate);
    System.out.println("strOrderDate>>>" + strOrderDate);
    
    //TODO: 1. 先執行指定第三人風險值判別
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
    
//    rsMsg += "指定第三人風險值: \n";
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
        rsMsg += "指定第三人" + desName + "查無黑名單資料，請先執行黑名單查詢 \n";
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
    bean.setActionText("新增");
    bean.setFunc("合約");
    bean.setRecordType("指定第三人風險值計算結果");
    bean.setUpd070Log(true);
    bean.setSendMail(true);
    
    //執行風險值檢核
    RiskCheckTool check = new RiskCheckTool(bean);
    Result rs = check.processRisk();

    // 執行結果
    String rsStatus = rs.getRsStatus()[3].toString().trim();
    RiskCheckRS rcRs = (RiskCheckRS) rs.getData();
    System.out.println(bean.getFunc() + bean.getRecordType() + ">>>" + rsStatus);

    // 寄發Email
    if (isPROD) {
      List rsSendMailList = (List) rcRs.getSendMailList();
      for (int ii = 0; ii < rsSendMailList.size(); ii++) {
        SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
        String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
        System.out.println("寄發MAIL>>>" + sendRS);
      }
    }
    
    // 風險值結果
    rsMsg += rcRs.getRsMsg();
    //End of 1.
    
    //2. 執行客戶風險值檢核
    long subDays = util.subACDaysRDay(strContractDate, strOrderDate);
    System.out.println("subDays>>>" + subDays);
    
    if(subDays >= 90) reCheckRisk = true; //付訂日與簽約日超過90日，需重新檢核風險
    
    //客戶資訊
    sql = "select a.OrderNo , a.CustomNo , a.CustomName , a.Birthday , a.PositionName , a.CountryName , a.ZIP , a.City , a.Town , a.Address , a.Tel , a.Tel2 , a.StatusCd , a.RiskValue "
        + "from Sale05M091 a where a.OrderNo = '" + strOrderNo + "' "
        + "and ISNULL(a.StatusCd , '') != 'C'";
    String[][] retCustomers = dbSale.queryFromPool(sql);
    
//    rsMsg += "訂單客戶風險值: \n";
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
        retCustom[ii] = (String[]) list.toArray(new String[list.size()]);
      }else {
        rsMsg += "客戶" + customName + "洗錢風險等級 :" + riskValue + "\n";
        String M277Sql = "UPDATE Sale05M277 SET RiskValue = '" + riskValue + "' " + "WHERE ContractNo = '" + strContractNoDisplay + "' AND CustomNo = '"
            + customNo + "' and ISNULL(StatusCd , '') = '';  ";
        trans.append(M277Sql);
      }
    }
    
    //不需要重新判別，就到此為止
    if(!reCheckRisk) {
      trans.close();
      dbSale.execFromPool(trans.getString()); //以訂單客戶風險值寫入合約客戶風險值
      messagebox(rsMsg);
      
      return value;
    }
    
    rsMsg += "重算客戶 \n";
    
    //實受人資訊
    sql = "select a.OrderNo , a.CustomNo , a.BCustomNo , a.BenName  , a.Birthday , a.HoldType , a.CountryName , a.StatusCd "
        + "from Sale05M091Ben a where a.OrderNo = '" + strOrderNo + "' "
        + "and ISNULL(a.StatusCd , '') != 'C'";
    String[][] retBens = dbSale.queryFromPool(sql);
    
    String[][] retSBen = new String[retBens.length][13];
    for(int ii=0 ; ii<retBens.length ; ii++) {
      List list = new ArrayList();
      list.add(""); 
      list.add( retBens[ii][1].toString().trim() ); //1. 法人統編
      list.add("");
      list.add( retBens[ii][3].toString().trim() ); //3. 實受人NAME
      list.add( retBens[ii][2].toString().trim() ); //4. 實受人ID
      list.add( retBens[ii][4].toString().trim() ); //5. 生日
      list.add( retBens[ii][6].toString().trim() ); //6. 國家
      list.add( retBens[ii][5].toString().trim() ); //7. 關係
      list.add("");
      list.add("");
      list.add("");
      list.add("");
      list.add( retBens[ii][7].toString().trim() );  //12. 狀態
      retSBen[ii] = (String[]) list.toArray(new String[list.size()]);
    }
    
    bean.setRetCustom(retCustom);
    bean.setRetSBen(retSBen);
    bean.setOrderNo(strOrderNo);
    bean.setContractNo(strContractNoDisplay);
    bean.setProjectID1(strProjectID1);
    bean.setOrderDate(strOrderDate);
    bean.setActionText("新增");
    bean.setFunc("合約");
    bean.setRecordType("客戶風險值計算結果");
    bean.setUpdSale05M277(true);
    bean.setUpd070Log(true);
    bean.setSendMail(true);
    
    //執行風險值檢核
    RiskCheckTool check2 = new RiskCheckTool(bean);
    Result rs2 = check2.processRisk();

    // 執行結果
    String rsStatus2 = rs2.getRsStatus()[3].toString().trim();
    RiskCheckRS rcRs2 = (RiskCheckRS) rs2.getData();
    System.out.println("執行結果>>>" + rsStatus2);

    // 寄發Email
    if (isPROD) {
      List rsSendMailList = (List) rcRs2.getSendMailList();
      for (int ii = 0; ii < rsSendMailList.size(); ii++) {
        SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
        String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
        System.out.println("寄發MAIL>>>" + sendRS);
      }
    }
    
    // 風險值結果
    rsMsg += rcRs2.getRsMsg();
    messagebox(rsMsg);

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
