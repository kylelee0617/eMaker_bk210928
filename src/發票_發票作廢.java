/**
 * 發票 - PROD - 寫在按鈕
 * @author B04391
 *
 */

public class 發票_發票作廢 {
  
  public String getDefaultValue(String value) throws Throwable {
    //
    if(getValue("PrintYes").trim().equals("N")){
      message("未列印發票 不可作廢");
      return value;
    }
    //
    if(getValue("DELYes").trim().equals("Y")){
      message("已作廢發票 不可作廢");
      return value;
    }
    //
    if(getValue("ProcessInvoiceNo").trim().equals("2")){
      message("營業部發票 不可作廢");
      return value;
    }
    
    /*
    //20200709 Kyle : 不明作用的欄位，會擋住作廢，先拿掉試試
    if(getValue("Transfer").trim().equals("收款")){
      message("收款發票 不可作廢");
      return value;
    }
    */

    //作廢不同人 提醒
    String stringCreateUserNo = getValue("CreateUserNo");

    if(!stringCreateUserNo.equalsIgnoreCase (getUser())){
      int  ans  =  JOptionPane.showConfirmDialog(null,  
                                      "作廢人與建立人不同 是否繼續?",
                                      "訊息",  
                                      JOptionPane.YES_NO_OPTION,
                                      JOptionPane.WARNING_MESSAGE) ;
      if(ans  ==  JOptionPane.NO_OPTION)  return value;
    }


    talk dbInvoice = getTalk(""+get("put_dbInvoice"));
    //處理部門
    String stringSQL = " SELECT TOP 1 DepartNo " +
                      " FROM InvoProcessDepartNo " +
                    " WHERE DepartNo = '" + getValue("DepartNo").trim() + "'" +
                          " AND EmployeeNo = '" + getUser() + "'" ;
    String retInvoProcessDepartNo[][] = dbInvoice.queryFromPool(stringSQL);
    if(retInvoProcessDepartNo.length == 0){
      message("不可處理 此部門發票");
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
      message("此發票已關帳 不可作廢");
      return value; 
    }
    stringSQL = " SELECT InvoM040.DiscountNo " +
                        " FROM InvoM040,InvoM041" +
                        " WHERE InvoM040.DiscountNo = InvoM041.DiscountNo" +
                              " AND InvoiceNo = '" + getValue("InvoiceNo").trim() + "'" +
                              " AND DELYES = 'N' ";
    String retInvoM040[][] = dbInvoice.queryFromPool(stringSQL);  
    if (retInvoM040.length > 0){
      message(  "折讓單:" + retInvoM040[0][0] + " 已折讓此發票 不可作廢");
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
    JOptionPane.showMessageDialog(null,  "作廢成功。",  "訊息",  JOptionPane.INFORMATION_MESSAGE) ;

    return value;
  }
  
}
