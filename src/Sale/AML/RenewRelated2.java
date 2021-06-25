package Sale.AML;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;

import jcx.jform.*;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.util.Result;

public class RenewRelated2 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println(">>>更新關聯人>>> Start");
    String errMsg = "";
    String rsMsg = "";  
    Result result = null;
    String funcName = value;
    String projectId = "";
    String orderNo = "";
    String orderDate = "";
    String processType = "updRelated";
    
    //config
    boolean isTest = false;
    String serverType = "";
    String lyodsSoapURL = "";
    Map config = (HashMap) get("config");
    serverType = config.get("serverType").toString();
    lyodsSoapURL = config.get("lyodsSoapURL").toString();
    isTest = "PROD".equals(serverType) ? false : true;
    
    if(StringUtils.contains(funcName, "購屋證明單")) {
      projectId = getValue("field1").trim();
      orderNo = getValue("field3").trim();
      orderDate = getValue("field2").trim();
    }else if(StringUtils.contains(funcName, "換名")) {
//      projectId = getValue("field1").trim();
//      orderNo = getValue("field3").trim();
//      orderDate = getValue("field2").trim();
    }
    
    //21-05-13 Kyle : 更新萊斯主要客戶
    talk dbSale = getTalk("Sale");
    String sql = "select CustomNo , CustomName , Birthday , IndustryCode from Sale05M091 where orderNO = '"+orderNo+"' and ISNULL(statusCd, '') != 'C' ";
    String[][] retCustom = dbSale.queryFromPool(sql);
    for(int i=0 ; i<retCustom.length ; i++) {
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + getFunctionName() + "," + "更新主要客戶與他們的關聯人" 
          + "," + retCustom[i][0].trim() + "," + retCustom[i][1].trim() + "," + retCustom[i][2].trim() + "," + retCustom[i][3].trim() + "," + processType;
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      errMsg += getValue("AMLText").trim();
    }
    
    //21-05-13 Kyle : 更新萊斯主要客戶
//    JTable t1 = this.getTable(tableCode);
//    int rowCount = t1.getRowCount();
//    for (int intRow = 0; intRow < rowCount; intRow++) {
//      String custNo = this.getValueAt(tableCode, intRow, "CustomNo").toString().trim();
//      String custName = this.getValueAt(tableCode, intRow, "CustomName").toString().trim();
//      String birth = getValueAt(tableCode, intRow, "Birthday").toString().trim();
//      String indCode = getValueAt(tableCode, intRow, "IndustryCode").toString();
//
//      String amlText = projectId + "," + orderNo + "," + orderDate + "," + getFunctionName() + "," + "更新主要客戶與他們的關聯人" 
//                     + "," + custNo + "," + custName + "," + birth + "," + indCode + "," + processType;
//      setValue("AMLText", amlText);
//      getButton("BtCustAML").doClick();
//      errMsg += getValue("AMLText").trim();
//    }
    
    System.out.println("errMsg>>>" + errMsg);

    System.out.println(">>>更新關聯人>>> End");
    return value;
  }

  public String getInformation() {
    return "---------------Test(Test).defaultValue()----------------";
  }
}
