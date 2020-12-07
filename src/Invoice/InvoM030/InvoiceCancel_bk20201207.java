package Invoice.InvoM030;

import java.util.Calendar;
import javax.swing.JOptionPane;
import Invoice.utils.AS400SQL;
import Invoice.vo.GLEAPFUFBean;
import jcx.db.talk;
import jcx.jform.bproc;

/**
 * 紀祇布...
 * 
 * JAVA : PL1607052531611
 * 2020/12/07 Kyle : 糤紀祇布糶AS400
 * 
 * @author B04391
 *
 */

public class InvoiceCancel_bk20201207 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    /*
    if(getValue("PrintYes").trim().equals("N")){
      message("ゼ祇布 ぃ紀");
      return value;
    }
    */
    if(getValue("DELYes").trim().equals("Y")){
      message("紀祇布 ぃ紀");
      return value;
    }
    //
    if(getValue("ProcessInvoiceNo").trim().equals("2")){
      message("犁穨场祇布 ぃ紀");
      return value;
    }
    /*
    // i dont know why~~~
    if(getValue("Transfer").trim().equals("Μ蹿")){
      message("Μ蹿祇布 ぃ紀");
      return value;
    }
    */
    //紀ぃ 矗眶
    String stringCreateUserNo = getValue("CreateUserNo");

    if(!stringCreateUserNo.equalsIgnoreCase (getUser())){
      int  ans  =  JOptionPane.showConfirmDialog(null,  
                                      "紀籔ミぃ 琌膥尿?",
                                      "癟",  
                                      JOptionPane.YES_NO_OPTION,
                                      JOptionPane.WARNING_MESSAGE) ;
      if(ans  ==  JOptionPane.NO_OPTION)  return value;
    }


    talk dbInvoice = getTalk(""+get("put_dbInvoice"));
    //矪瞶场
    String stringSQL = " SELECT TOP 1 DepartNo " +
                      " FROM InvoProcessDepartNo " +
                    " WHERE DepartNo = '" + getValue("DepartNo").trim() + "'" +
                          " AND EmployeeNo = '" + getUser() + "'" ;
    String retInvoProcessDepartNo[][] = dbInvoice.queryFromPool(stringSQL);
    if(retInvoProcessDepartNo.length == 0){
      message("ぃ矪瞶 场祇布");
      //return false;
    }
    dbInvoice = getTalk("Invoice");
    stringSQL = "SELECT InvoiceStartNo," +
                     " CloseYES " +
                         " FROM InvoM022 " +
                       " WHERE InvoiceStartNo <= '" + getValue("InvoiceNo").trim() + "' AND InvoiceEndNo >= '" + getValue("InvoiceNo").trim() + "'";
    String retInvoM022[][] = dbInvoice.queryFromPool(stringSQL);
    String stringInvoiceStartNo = "";
    String stringCloseYES = "";
    for(int i=0;i<retInvoM022.length;i++){
      stringInvoiceStartNo = retInvoM022[i][0];
      stringCloseYES = retInvoM022[i][1];
    }
    if (stringCloseYES.equals("Y")){
      message("祇布闽眀 ぃ紀");
      return value; 
    }
    stringSQL = " SELECT InvoM040.DiscountNo " +
                        " FROM InvoM040,InvoM041" +
                        " WHERE InvoM040.DiscountNo = InvoM041.DiscountNo" +
                              " AND InvoiceNo = '" + getValue("InvoiceNo").trim() + "'" +
                              " AND DELYES = 'N' ";
    String retInvoM040[][] = dbInvoice.queryFromPool(stringSQL);  
    if (retInvoM040.length > 0){
      message(  "ч琵虫:" + retInvoM040[0][0] + " ч琵祇布 ぃ紀");
      return value;   
    }
    //
    String retSystemDateTime[][] = dbInvoice.queryFromPool("spInvoSystemDateTime  'Admin'");
    String stringSystemDateTime ="";
    stringSystemDateTime = retSystemDateTime[0][0].replace("-","/");
    stringSystemDateTime = stringSystemDateTime.substring(0,19);
    String stringUserkey = "";
    Calendar cal= Calendar.getInstance();//Current time
    stringUserkey = getUser() + "_T" + ""+( (cal.get(Calendar.HOUR_OF_DAY)*10000) + (cal.get(Calendar.MINUTE)*100) + cal.get(Calendar.SECOND) );
    stringSQL = "spInvoM030UpdateDEL " +
               "'" + getValue("InvoiceNo").trim() + "'," +
               "'" + getValue("InvoiceDate").trim() + "'," +
               "'" + getValue("InvoiceKind").trim() + "'," +
               "'" + getValue("CompanyNo").trim() + "'," +
               "'" + getValue("DepartNo").trim() + "'," +
               "'" + getValue("ProjectNo").trim() + "'," +           
               "'" + getValue("InvoiceWay").trim() + "'," +                    
               "'" + getValue("HuBei").trim() + "'," +                     
               "'" + getValue("CustomNo").trim() + "'," +                    
               "'"+ getValue("PointNo").trim() + "'," +                              
                       getValue("InvoiceMoney").trim() + "," +                                         
                       getValue("InvoiceTax").trim() + "," +                                         
                       getValue("InvoiceTotalMoney").trim() + "," +
               "'"+ getValue("TaxKind").trim() + "'," +                                            
               "'4'," +
               "'" + getUser() + "'," +
               "'" + stringSystemDateTime  + "'," +
               "'" + stringSystemDateTime  + "'," +          
                   "'U'," +
               "'" + stringUserkey + "'";
    dbInvoice.execFromPool(stringSQL);
    JOptionPane.showMessageDialog(null,  "紀Θ",  "癟",  JOptionPane.INFORMATION_MESSAGE) ;

    return value;
  }
}
