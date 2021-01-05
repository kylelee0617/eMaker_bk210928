package Sale.Sale05M093;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Farglory.util.*;
import jcx.db.talk;

public class CheckRiskNew extends jcx.jform.sproc {
  
  public String getDefaultValue(String value) throws Throwable {
    
    System.out.println("Class >>> Sale.Sale05M093.ChekRiskNew");
    
    RiskCheckBean bean = new RiskCheckBean();
    talk dbSale = getTalk("Sale");
    talk dbEMail = getTalk("eMail");
    talk db400CRM = getTalk("400CRM");
    talk dbEIP = getTalk("EIP");
    bean.setDbSale(dbSale);
    bean.setDbEMail(dbEMail);
    bean.setDb400CRM(db400CRM);
    bean.setDbEIP(dbEIP);
    bean.setUserNo(getUser());
    
    //客戶table
    Map name2no = new HashMap();
    String[][] retCustom = getTableData("table2");
    for(int ii=0 ; ii<retCustom.length ; ii++) {
      String[] custom1 = retCustom[ii];
      List list = new ArrayList(Arrays.asList(custom1));
      
      list.add(""); //對齊用(異動日期)
      //不需要處理的，於23位置加上一個"C"，即可忽略(理論上會是最後)
      if( "0".equals(list.get(2).toString()) ) {
        list.add("C");  
      }else if( "1".equals(list.get(2).toString()) ) {
        list.add("");
      }
      //移除這個，以對齊欄位順序
      list.remove(2);
      
      custom1 = (String[]) list.toArray(new String[list.size()]);
      retCustom[ii] = custom1;
      
      //組給下面用的map
      name2no.put(custom1[6].toString().trim() , custom1[5].toString().trim());
    }
    
    //實受人table
    //0.OrderNo, CustomNo, RecordNo, BenName, BCustomNo, 5.Birthday, CountryName, HoldType, IsBlackList, IsControlList, 10.IsLinked, TrxDate, StatusCd
    String[][] retSBen = getTableData("table5");
    for(int ii=0 ; ii<retSBen.length ; ii++) {
      String[] custom1 = retSBen[ii];
      System.out.println("1>>>" + custom1.length);
      System.out.println("1>>>" + custom1[0]);
      
      List list = new ArrayList();
      list.add( custom1[0].toString().trim() );
      list.add( (String)name2no.get(custom1[2].toString().trim()) );
      list.add( custom1[1].toString().trim() );
      list.add( custom1[3].toString().trim() ); //3 實受人姓名
      list.add( custom1[4].toString().trim() );
      list.add( custom1[5].toString().trim() );
      list.add( custom1[6].toString().trim() );
      list.add( custom1[7].toString().trim() ); //7 對象別
      list.add( custom1[8].toString().trim() );
      list.add( custom1[9].toString().trim() );
      list.add( custom1[10].toString().trim() );
      list.add( "" );
      list.add( "" );
      
      custom1 = (String[]) list.toArray(new String[list.size()]);
      retSBen[ii] = custom1;
    }
    
    String strOrderNo = getValue("OrderNo").trim();
    String strProjectID1 = getValue("ProjectID1").trim();
    String strOrderDate = dbSale.queryFromPool("select orderDate from Sale05M090 where orderNo = '" + strOrderNo + "' ")[0][0].trim();
    String actionText = "";
    bean.setRetCustom(retCustom);
    bean.setRetSBen(retSBen);
    bean.setOrderNo(strOrderNo);
    bean.setProjectID1(strProjectID1);
    bean.setOrderDate(strOrderDate);
    bean.setActionText("存檔");
    bean.setFunc("換名");
    bean.setRecordType("風險計算受益人資料");
    bean.setUpdSale05M091(true);
    bean.setUpd070Log(true);
    bean.setSendMail(true);
    
    //執行風險值檢核
    RiskCheckTool check = new RiskCheckTool(bean);
    Result rs = check.processRisk();

    // 執行結果
    String rsStatus = rs.getRsStatus()[3].toString().trim();
    System.out.println("執行結果>>>" + rsStatus);

    RiskCheckRS rcRs = (RiskCheckRS) rs.getData();

    // 風險值結果
    String rsMsg = !"".equals(rcRs.getRsMsg())? rcRs.getRsMsg():"無風險值結果，請確認名單內容是否正確。";
    messagebox(rsMsg);

    // 寄發Email
    if ("PROD".equals(get("serverType").toString().trim())) {
      List rsSendMailList = (List) rcRs.getSendMailList();
      for (int ii = 0; ii < rsSendMailList.size(); ii++) {
        SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
        String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
        System.out.println("寄發MAIL>>>" + sendRS);
      }
    }

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
