package Sale.Sale05M090;

import jcx.db.talk;
import jcx.util.*;
import jcx.html.*;
import java.util.*;
import Farglory.util.*;

public class CheckRiskNew extends jcx.jform.sproc {

  public String getDefaultValue(String value) throws Throwable {
    System.out.println("Class >>> Sale.Sale05M090.ChekRiskNew");
    System.out.println(datetime.getTime("h:m:s"));

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

    String[][] retCustom = getTableData("table1");
    String[][] retSBen = getTableData("table6");
    String strOrderNo = getValue("field3").trim();
    String strOrderDate = getValue("field2").trim();
    String strProjectID1 = getValue("field1").trim();
    String actionText = getValue("actionText").trim();
    bean.setRetCustom(retCustom);
    bean.setRetSBen(retSBen);
    bean.setOrderNo(strOrderNo);
    bean.setProjectID1(strProjectID1);
    bean.setOrderDate(strOrderDate);
    bean.setActionText(actionText);
    bean.setFunc("購屋證明單");
    bean.setRecordType("風險計算受益人資料");
    bean.setUpdSale05M091(true);
    bean.setUpd070Log(true);
    bean.setSendMail(true);

    RiskCheckTool check = new RiskCheckTool(bean);
    Result rs = check.processRisk();

    // 執行結果
    String rsStatus = rs.getRsStatus()[3].toString().trim();
    System.out.println("執行結果>>>" + rsStatus);

   //////////////////////
   RiskCheckRS rcRs = (RiskCheckRS) rs.getData();
   
   //風險值結果
   String rsMsg = !"".equals(rcRs.getRsMsg())? rcRs.getRsMsg():"無風險值結果，請確認名單內容是否正確。";
   messagebox(rsMsg);
   
   //寄發Email
   if( "PROD".equals(get("serverType").toString().trim()) ) {
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
