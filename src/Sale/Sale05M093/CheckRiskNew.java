package Sale.Sale05M093;

import Farglory.aml.AMLyodsBean;
import Farglory.util.KUtils;
import jcx.db.talk;

public class CheckRiskNew extends jcx.jform.bproc {
  
  public String getDefaultValue(String value) throws Throwable {
    System.out.println("Class >>> Sale.Sale05M093.ChekRiskNew");
    
    talk dbSale = getTalk("Sale");
    talk dbEMail = getTalk("eMail");
    talk db400CRM = getTalk("400CRM");
    talk dbEIP = getTalk("EIP");
    KUtils kutil = new KUtils();
    String strOrderNo = getValue("OrderNo").trim();
    String strProjectID1 = getValue("ProjectID1").trim();
    String strOrderDate = kutil.getOrderDateByOrderNo(strOrderNo);
    String rsMsg = "";
    
    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setDbSale(dbSale);
    aBean.setDbEMail(dbEMail);
    aBean.setDb400CRM(db400CRM);
    aBean.setDbEIP(dbEIP);
    aBean.setEmakerUserNo(getUser());
    aBean.setOrderNo(strOrderNo);
    aBean.setProjectID1(strProjectID1);
    aBean.setOrderDate(strOrderDate);
    aBean.setActionName("存檔");
    aBean.setFunc("換名");
    aBean.setRecordType("風險計算結果");
    aBean.setUpdSale05M091(true);
    aBean.setUpd070Log(true);
    aBean.setSendMail(true);
    
    //客戶table
    String[][] retCustom = getTableData("table2");
    for(int ii=0 ; ii<retCustom.length ; ii++) {
      String custNo = retCustom[ii][5].trim();
      String custName = retCustom[ii][6].trim();
      String birth = retCustom[ii][8].trim();
      String ind = kutil.getIndustryCodeByMajorName(retCustom[ii][10].trim());
      
      String amlText = strOrderNo + "," + strOrderDate + "," + custNo + "," + custName + "," + birth + "," + ind + "," + "custListRiskCheck";
      setValue("AMLText" , amlText);
      getButton("BtCustAML").doClick();
      rsMsg += getValue("AMLText").trim();
    }
    messagebox(rsMsg);

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
