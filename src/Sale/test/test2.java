package Sale.test;

import Farglory.util.KUtils;
import Farglory.util.RiskCheckBean;
import Farglory.util.RiskCheckTool;
import jcx.db.talk;
import jcx.jform.bproc;

public class test2 extends bproc {
  KUtils kUtil = new KUtils();

  public String getDefaultValue(String value) throws Throwable {
    talk dbSale = getTalk("Sale");
    talk dbEMail = getTalk("eMail");
    talk db400CRM = getTalk("400CRM");
    talk dbEIP = getTalk("EIP");
//  put("dbSale" , dbSale);
//  put("dbEMail" , dbEMail);
//  put("db400CRM" , db400CRM);
//  put("dbEIP" , dbEIP);

    String[][] retCustom = getTableData("table1");
    String[][] retSBen = getTableData("table6");
    String strOrderNo = getValue("field3").trim();
    String strOrderDate = getValue("field2").trim();
    String strProjectID1 = getValue("field1").trim();
    String actionText = getValue("actionText").trim();

    RiskCheckBean bean = new RiskCheckBean();
    bean.setDbSale(dbSale);
    bean.setDbEMail(dbEMail);
    bean.setDb400CRM(db400CRM);
    bean.setDbEIP(dbEIP);
    bean.setRetCustom(retCustom);
    bean.setRetSBen(retSBen);
    bean.setOrderNo(strOrderNo);
    bean.setProjectID1(strProjectID1);
    bean.setOrderDate(strOrderDate);
    bean.setActionText(actionText);
//    bean.setUpdSale05M091(true);
    RiskCheckTool check = new RiskCheckTool(bean);
    check.processRisk();

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
