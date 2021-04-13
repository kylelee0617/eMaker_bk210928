package Sale.Sale05M090;

import jcx.db.talk;
import jcx.util.*;
import jcx.html.*;
import java.util.*;

import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCheckTools_Lyods;
import Farglory.aml.RiskCustomBean;
import Farglory.util.*;

public class CheckRiskNew extends jcx.jform.sproc {

  public String getDefaultValue(String value) throws Throwable {
    System.out.println("Class >>> Sale.Sale05M090.ChekRiskNew");
    System.out.println(datetime.getTime("h:m:s"));
    
    String strOrderNo = getValue("field3").trim();
    String strOrderDate = getValue("field2").trim();
    String strProjectID1 = getValue("field1").trim();
    String actionText = getValue("actionText").trim();
    
    //組成查詢主要客戶
    String[][] retCustom = getTableData("table1");
    RiskCustomBean[] cBeans = new RiskCustomBean[retCustom.length];
    for(int ii=0 ; ii<retCustom.length ; ii++) {
      RiskCustomBean cBean = new RiskCustomBean();
      cBean.setCustomNo(retCustom[ii][5].trim());
      cBean.setCustomName(retCustom[ii][6].trim());
      cBean.setBirthday(retCustom[ii][8].trim());
      cBean.setIndustryCode(retCustom[ii][24].trim());
      cBeans[ii] = cBean;
    }
    
    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setDbSale(getTalk("Sale"));
    aBean.setDbEMail(getTalk("eMail"));
    aBean.setDb400CRM(getTalk("400CRM"));
    aBean.setDbEIP(getTalk("EIP"));
    aBean.setDbPw0D(getTalk("pw0d"));
    aBean.setEmakerUserNo(getUser());
    aBean.setOrderNo(strOrderNo);
    aBean.setProjectID1(strProjectID1);
    aBean.setOrderDate(strOrderDate);
    aBean.setActionName(actionText);
    aBean.setFunc("購屋證明單");
    aBean.setRecordType("風險計算受益人資料");
    aBean.setUpdSale05M091(true);
    aBean.setUpd070Log(true);
    aBean.setSendMail(true);
    RiskCheckTools_Lyods risk = new RiskCheckTools_Lyods(aBean);
    Result rs = risk.processRisk(cBeans);

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
