package Sale.test;
import java.util.ResourceBundle;

import Farglory.aml.AMLTools_Lyods;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCheckTools_Lyods;
import Farglory.aml.RiskCustomBean;
import Farglory.util.Result;
import Farglory.util.RiskCheckRS;
import jcx.jform.sproc;

public class test111 extends sproc{
  public String getDefaultValue(String value)throws Throwable{
    
    //config
    ResourceBundle resource = ResourceBundle.getBundle("configK");
    String serverType = resource.getString("serverType").trim();
    String lyodsSoapURL = resource.getString("lyodsSoapURL").trim();

    //«È¤á¸ê®Æ
    
    RiskCustomBean custBean = new RiskCustomBean();
    custBean.setCustomNo("E200157225");
    custBean.setCustomName("¶À¢ÝÀA´f");
    custBean.setBirthday("19830101");
    custBean.setIndustryCode("22");
    
    
    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setOrderNo("CS0331H110A10203015");
    aBean.setOrderDate("20130327");
    aBean.setEmakerUserNo(this.getUser());
    aBean.setTestServer("PROD".equals(serverType)? false:true);
    aBean.setLyodsSoapURL(lyodsSoapURL);
    aBean.setDb400CRM(getTalk("400CRM"));
    aBean.setDbSale(getTalk("Sale"));
    aBean.setDbEIP(getTalk("EIP"));
    aBean.setDbEMail(getTalk("eMail"));
    aBean.setDbPw0D(getTalk("pw0d"));
    
    AMLTools_Lyods aml = new AMLTools_Lyods(aBean);
    Result rs1 = aml.chkAML018_San(custBean);
    System.out.println("rs1>>>" + (String)rs1.getData());
    System.out.println("rs12>>>" + (String)rs1.getRsStatus()[1]);
    
    RiskCustomBean[] cBeans = new RiskCustomBean[1];
    cBeans[0] = custBean;
    RiskCheckTools_Lyods risk = new RiskCheckTools_Lyods(aBean);
    Result rs2 = risk.processRisk(cBeans);
    System.out.println("rs2>>>" + ((RiskCheckRS)rs2.getData()).getRsMsg() );
    System.out.println("rs22>>>" + (String)rs2.getRsStatus()[1]);
    
    
    
    return value;
  }
  public String getInformation(){
    return "---------------test111(test111).defaultValue()----------------";
  }
}
