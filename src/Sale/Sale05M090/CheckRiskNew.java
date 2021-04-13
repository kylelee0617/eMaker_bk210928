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
    
    //�զ��d�ߥD�n�Ȥ�
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
    aBean.setFunc("�ʫ��ҩ���");
    aBean.setRecordType("���I�p����q�H���");
    aBean.setUpdSale05M091(true);
    aBean.setUpd070Log(true);
    aBean.setSendMail(true);
    RiskCheckTools_Lyods risk = new RiskCheckTools_Lyods(aBean);
    Result rs = risk.processRisk(cBeans);

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
