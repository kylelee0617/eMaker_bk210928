package Sale.test;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;

public class Test090 extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    String errMsg = "";
    String orderNo = getValue("field3").trim();
    String orderDate = getValue("field2").trim();
    
    JTable t1 = this.getTable("table1");
    int rowCount = t1.getRowCount();
    for(int intRow=0 ; intRow<rowCount ; intRow++) {
      String custNo = this.getValueAt("table1",  intRow,  "CustomNo").toString().trim();
      String custName = this.getValueAt("table1",  intRow,  "CustomName").toString().trim();
      String birth = this.getValueAt("table1", intRow,  "Birthday").toString().length()==0? " ":getValueAt("table1", intRow,  "Birthday").toString();
      String ind = this.getValueAt("table1", intRow,  "IndustryCode").toString().length()==0? " ":getValueAt("table1", intRow,  "IndustryCode").toString();
      
      String amlText = orderNo + "," + orderDate + "," + custNo + "," + custName + "," + birth + "," + ind + "," + "updRelated";
      setValue("AMLText" , amlText);
      getButton("BtCustAML").doClick();
      errMsg += getValue("AMLText").trim();
    }
    
    System.out.println("errMsg>>>" + errMsg);
    
    return value;
  }
  public String getInformation(){
    return "---------------Test(Test).defaultValue()----------------";
  }
}
