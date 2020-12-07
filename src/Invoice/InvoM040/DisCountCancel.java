package Invoice.InvoM040;
import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;

public class DisCountCancel extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    talk dbInvoice = getTalk("Invoice");
    talk dbSale = getTalk("Sale");
    String stringSQL = "";
    String stringUserkey = "";
    if (getValue("DELYes").equals("Y")){
      message("折讓單已作廢!無法作廢 ");
      return value; 
    }
    stringSQL = " SELECT DiscountNo " +
                        " FROM InvoM040" +
                        " WHERE DiscountNo = '" + getValue("DiscountNo") + "'" +
                    " AND DELYes ='Y'";
    String retInvoM040[][] = dbInvoice.queryFromPool(stringSQL);
    if (retInvoM040.length >0){
      message("折讓單已作廢!無法作廢 ");
      return value; 
    }
    if (!getUser().equals("B1085")){
    stringSQL = " SELECT DiscountNo " +
                        " FROM Sale05M194" +
                        " WHERE DiscountNo = '" + getValue("DiscountNo") + "'";
    String retSale05M194[][] = dbSale.queryFromPool(stringSQL);
    if (retSale05M194.length >0){
      message("收款系統 已有折讓!無法作廢 ");
      return value; 
    }
    }
    //
    Calendar cal= Calendar.getInstance();//Current time
    stringUserkey = getUser() + "_T" + ""+( (cal.get(Calendar.HOUR_OF_DAY)*10000) + (cal.get(Calendar.MINUTE)*100) + cal.get(Calendar.SECOND) );
    String retSystemDateTime[][] = dbInvoice.queryFromPool("spInvoSystemDateTime  'Admin'");
    String stringSystemDateTime ="";
    stringSystemDateTime = retSystemDateTime[0][0].replace("-","/");
    stringSystemDateTime = stringSystemDateTime.substring(0,19);
    //
    stringSQL = "spInvoM040UpdateDEL " +
               "'" + getValue("DiscountNo").trim() + "'," +
               "'" + getValue("DiscountDate").trim() + "'," +
               "'" + getValue("CompanyNo").trim() + "'," +
               "'" + getValue("DepartNo").trim() + "'," +
               "'" + getValue("ProjectNo").trim() + "'," +           
               "'" + getValue("HuBei").trim() + "'," +                               
               "'" + getValue("CustomNo").trim() + "'," +                              
               "'" + getValue("DiscountWay").trim() + "'," +                     
               "''," +                               
                       getValue("DiscountMoney").trim() + "," +                                        
                       getValue("DiscountTax").trim() + "," +                                        
                       getValue("DiscountTotalMoney").trim() + "," +
               "'2'," +                              
               "'" + getUser() + "'," +
               "'" + stringSystemDateTime  + "'," +
               "'" + stringSystemDateTime  + "'," +          
                   "'D'," +
               "'" + stringUserkey + "'" ;
    dbInvoice.execFromPool(stringSQL);
    setValue("DELYes","Y");
    message("已作廢折讓單 = " + getValue("DiscountNo"));          
    return value;
  }
}
