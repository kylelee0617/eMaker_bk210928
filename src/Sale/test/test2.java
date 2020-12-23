package Sale.test;

import Farglory.util.KUtils;
import Farglory.util.RiskCheckBean;
import Farglory.util.RiskCheckTool;
import jcx.db.talk;
import jcx.util.*;
import jcx.html.*;
import java.util.*;
import Farglory.util.*;

public class test2 extends jcx.jform.sproc {
  KUtils kUtil = new KUtils();

  public String getDefaultValue(String value) throws Throwable {
    System.out.println("test2>>>0");
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
    bean.setUpdSale05M091(true);
    bean.setUpd070Log(true);
    bean.setSendMail(true);

    RiskCheckTool check = new RiskCheckTool(bean);
    Result rs = check.processRisk();
    Map rsData = (Map) rs.getData();

    // 執行結果
    String rsStatus = rs.getRsStatus()[3].toString().trim();
    System.out.println("執行結果>>>" + rsStatus);

    // 風險值結果
    String rsMsg = rsData.get("rsMsg").toString().trim();
    messagebox(rsMsg);

    // 寄發Email
    List rsSendMailList = (List) rsData.get("sendMailList");
    for (int ii = 0; ii < rsSendMailList.size(); ii++) {
      SendMailBean smbean = new SendMailBean();
      System.out.println("bean1>>>" + smbean.getColm1());
      System.out.println("bean2>>>" + smbean.getColm2());
      System.out.println("bean3>>>" + smbean.getArrayUser());
      System.out.println("bean4>>>" + smbean.getSubject());
      System.out.println("bean5>>>" + smbean.getContext());
      
      
      
//      String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
//      System.out.println("寄發MAIL>>>" + sendRS);
    }

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
