package Sale.AML;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import Farglory.util.KUtils;
import jcx.db.talk;
import jcx.jform.bproc;

public class RenewRelated extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println(">>>更新關聯人>>> Start");
    
    KUtils kUtil = new KUtils();
    String errMsg = "";
    String funcName = value.trim();
    String recordType = "更新主要客戶與他們的關聯人";
    String projectId = "";
    String orderNo = "";
    String orderDate = "";
    String processType = "updRelated";
    
    //config
    Map config = (HashMap) get("config");
    boolean isTest = "PROD".equals(config.get("serverType").toString()) ? false : true;
    
    if(StringUtils.contains(funcName, "購屋證明單")) {
      projectId = getValue("field1").trim();
      orderNo = getValue("field3").trim();
      orderDate = getValue("field2").trim();
    }else if(StringUtils.contains(funcName, "換名")) {
      projectId = getValue("ProjectID1").trim();
      orderNo = getValue("OrderNo").trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
    }
    
    //21-05-13 Kyle : 更新萊斯主要客戶
    talk dbSale = getTalk("Sale");
    String sql = "select CustomNo , CustomName , Birthday , IndustryCode from Sale05M091 where orderNO = '"+orderNo+"' and ISNULL(statusCd, '') != 'C' ";
    String[][] retCustom = dbSale.queryFromPool(sql);
    for(int i=0 ; i<retCustom.length ; i++) {
      String tmpId = retCustom[i][0].trim();  //id
      String tmpName = retCustom[i][0].trim();  //name
      String tmpBirth = retCustom[i][0].trim(); //birthday
      String tmpIndCode = retCustom[i][0].trim(); //IndustryCode
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + tmpId + "," + tmpName + "," + tmpBirth + "," + tmpIndCode + "," + processType;
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      errMsg += getValue("AMLText").trim();
    }
    
    if(StringUtils.isNotBlank(errMsg)) {
      System.out.println(funcName + "結果:" + errMsg);
    }

    System.out.println(">>>更新關聯人>>> End");
    return value;
  }

  public String getInformation() {
    return "---------------Test(Test).defaultValue()----------------";
  }
}
