package Sale.Sale05M090;

import jcx.db.talk;
import jcx.util.*;
import jcx.html.*;
import java.util.*;
import Farglory.util.*;

public class CheckRiskNew_bk20210412 extends jcx.jform.sproc {

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
    bean.setFunc("�ʫ��ҩ���");
    bean.setRecordType("���I�p����q�H���");
    bean.setUpdSale05M091(true);
    bean.setUpd070Log(true);
    bean.setSendMail(true);

    RiskCheckTool check = new RiskCheckTool(bean);
    Result rs = check.processRisk();

    // ���浲�G
    String rsStatus = rs.getRsStatus()[3].toString().trim();
    System.out.println("���浲�G>>>" + rsStatus);

   //////////////////////
   RiskCheckRS rcRs = (RiskCheckRS) rs.getData();
   
   //���I�ȵ��G
   String rsMsg = !"".equals(rcRs.getRsMsg())? rcRs.getRsMsg():"�L���I�ȵ��G�A�нT�{�W�椺�e�O�_���T�C";
   messagebox(rsMsg);
   
   //�H�oEmail
   if( "PROD".equals(get("serverType").toString().trim()) ) {
     List rsSendMailList = (List) rcRs.getSendMailList();
     for (int ii = 0; ii < rsSendMailList.size(); ii++) {
       SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
       String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
       System.out.println("�H�oMAIL>>>" + sendRS);
     }
   }

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
