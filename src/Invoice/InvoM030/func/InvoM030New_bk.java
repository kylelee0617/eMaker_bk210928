package Invoice.InvoM030.func;
import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
public class InvoM030New_bk extends bTransaction{
  public boolean action(String value)throws Throwable{
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
    message("");
    talk dbInvoice = getTalk(""+get("put_dbInvoice"));
    //處理部門
    String stringSQL = " SELECT TOP 1 DepartNo " +
                      " FROM InvoProcessDepartNo " +
                    " WHERE DepartNo = '" + getValue("DepartNo").trim() + "'" +
                          " AND EmployeeNo = '" + getUser() + "'" ;
    String retInvoProcessDepartNo[][] = dbInvoice.queryFromPool(stringSQL);
    if(retInvoProcessDepartNo.length == 0){
      //message("不可處理 此部門發票");
      //return false;
    }
    //發票聯式
    dbInvoice = getTalk("Invoice");
    //
    stringSQL = " SELECT Nationality " +
                " FROM InvoM0C0 " +
              " WHERE CustomNo = '" +  getValue("CustomNo").trim() + "'" ;
    String retInvoM0C0[][] = dbInvoice.queryFromPool(stringSQL);
    if(retInvoM0C0.length > 0){
      String stringNationality =  retInvoM0C0[0][0];
      if (stringNationality.equals("1")){
        if (getValue("CustomNo").length() == 8 && getValue("InvoiceKind").equals("2")){
          setValue("InvoiceKind","3");
          message("發票已強制改為 三聯 !");
          //return false;
        }
        if (getValue("CustomNo").length() == 10 && getValue("InvoiceKind").equals("3")){
          setValue("InvoiceKind","2");
          message("發票已強制改為 二聯 !");
          //return false;
        }     
      }
      if (stringNationality.equals("2")){
        if (getValue("CustomNo").length() == 10 && getValue("InvoiceKind").equals("3")){
          setValue("InvoiceKind","2");    
          message("發票已強制改為 二聯 !");
          return false;
        }     
      }   
    } 
    dbInvoice = getTalk("Invoice");
    /*
    //發票聯式
    Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil() ;
    if (getValue("CustomNo").length()==8 && exeUtil.isDigitNum (getValue("CustomNo")))
      setValue("InvoiceKind","3");
    else
      setValue("InvoiceKind","2");  
    */  
      
    //發票號碼
    String stringInvoiceDate = getValue("InvoiceDate").trim();
    stringInvoiceDate = stringInvoiceDate.substring(0,7);
    stringSQL = "SELECT TOP 1 InvoiceYYYYMM," +
                                    " FSChar," +
                                    " StartNo," +
                                    " InvoiceBook," +
                                    " InvoiceStartNo," +
                                    " InvoiceEndNo," +
                                    " MaxInvoiceNo, " +
                    " SUBSTRING(MaxInvoiceNo,3,10)+1" +
                               " FROM InvoM022 " +
                              " WHERE CompanyNo = '" + getValue("CompanyNo").trim() + "'" +
                                " AND DepartNo = '" + getValue("DepartNo").trim() + "'" +
                                " AND ProjectNo = '" + getValue("ProjectNo").trim() + "'" +
                                " AND InvoiceKind = '" + getValue("InvoiceKind").trim() + "'" +
                                " AND UseYYYYMM = '" + stringInvoiceDate + "'" +
                                " AND (MaxInvoiceDate <= '" + getValue("InvoiceDate").trim() + "' OR MaxInvoiceDate IS NULL OR LEN(MaxInvoiceDate) = 0)" +
                                " AND ENDYES = 'N' " +
                                " AND CloseYes = 'N' " +
                                " AND ProcessInvoiceNo = '1'";
    if  (getValue("ProjectNo").trim().equals("E2AII") || getValue("ProjectNo").trim().equals("H802A")){
      stringSQL = "SELECT TOP 1 InvoiceYYYYMM," +
                      " FSChar," +
                      " StartNo," +
                      " InvoiceBook," +
                      " InvoiceStartNo," +
                      " InvoiceEndNo," +
                      " MaxInvoiceNo, " +
                      " SUBSTRING(MaxInvoiceNo,3,10)+1" +
                     " FROM InvoM022 " +
                    " WHERE CompanyNo = '" + getValue("CompanyNo").trim() + "'" +
                    " AND DepartNo = '" + getValue("DepartNo").trim() + "'" +
                    " AND ProjectNo = '" + getValue("ProjectNo").trim() + "'" +
                    " AND InvoiceKind = '" + getValue("InvoiceKind").trim() + "'" +
                    " AND UseYYYYMM = '" + stringInvoiceDate + "'" +
                    " AND (MaxInvoiceDate <= '" + getValue("InvoiceDate").trim() + "' OR MaxInvoiceDate IS NULL OR LEN(MaxInvoiceDate) = 0)" +
                    " AND ENDYES = 'N' " +
                    " AND CloseYes = 'N' " +
                    " AND ProcessInvoiceNo = '1'" +
                    " AND CompanyNo+BranchNo IN( " +  
                                                " SELECT  CompanyNo+BranchNo " +
                                                 " FROM Invom025" +
                                                 " WHERE ProjectNo = '" + getValue("ProjectNo").trim() + "'" +
                                                   " AND HuBei = '" + getValue("HuBei").trim() + "'" +  
                                                ")";     
    }             
    String retInvoM022[][] = dbInvoice.queryFromPool(stringSQL);
    String stringInvoiceYYYYMM = "";
    String stringFSChar = "";
    String stringStartNo = "";
    String stringInvoiceBook = "";
    String stringInvoiceStartNo = "";
    String stringInvoiceEndNo = "";
    String stringMaxInvoiceNo = "";
    String stringMaxInvoiceNo1 = "";
    String stringNowInvoiceNo = "";
    String stringEndYes = "N";
    for(int i=0;i<retInvoM022.length;i++){
      stringInvoiceYYYYMM = retInvoM022[i][0];
      stringFSChar = retInvoM022[i][1];
      stringStartNo = retInvoM022[i][2];
      stringInvoiceBook = retInvoM022[i][3];
      stringInvoiceStartNo = retInvoM022[i][4];
      stringInvoiceEndNo = retInvoM022[i][5];
      stringMaxInvoiceNo = retInvoM022[i][6].trim();
      stringMaxInvoiceNo1 = retInvoM022[i][7].trim();
      if (stringMaxInvoiceNo1.length() == 7) stringMaxInvoiceNo1 = "0" + stringMaxInvoiceNo1;
      if (stringMaxInvoiceNo1.length() == 6) stringMaxInvoiceNo1 = "00" + stringMaxInvoiceNo1;  
      if (stringMaxInvoiceNo1.length() == 5) stringMaxInvoiceNo1 = "000" + stringMaxInvoiceNo1;   
      if (stringMaxInvoiceNo1.length() == 4) stringMaxInvoiceNo1 = "0000" + stringMaxInvoiceNo1;      
      if (stringMaxInvoiceNo1.length() == 3) stringMaxInvoiceNo1 = "00000" + stringMaxInvoiceNo1;       
      if (stringMaxInvoiceNo1.length() == 2) stringMaxInvoiceNo1 = "000000" + stringMaxInvoiceNo1;
    System.out.println("stringMaxInvoiceNo="  + stringMaxInvoiceNo);
    System.out.println("stringMaxInvoiceNo1="  + stringMaxInvoiceNo1);
    System.out.println("stringMaxInvoiceNo.length()="  + stringMaxInvoiceNo.length());                                        
      if (stringMaxInvoiceNo.length()==0){
        stringNowInvoiceNo = stringInvoiceStartNo;
      }else{
        stringNowInvoiceNo = stringFSChar + stringMaxInvoiceNo1;  
      }
      if (stringNowInvoiceNo.equals(stringInvoiceEndNo)) stringEndYes ="Y";
    }
    System.out.println(stringNowInvoiceNo);
    if (stringNowInvoiceNo.length() < 10){
      message("電腦發票已用完 請洽財務室領取!");
      return false;
    }
    message(stringNowInvoiceNo);
    //明細
    String [][] A_table = getTableData("table1");
    if (A_table.length ==0){
       message("明細必須至少有一筆");
       return false;
    }
    String stringUserkey = "";
    Calendar cal= Calendar.getInstance();//Current time
    stringUserkey = getUser() + "_T" + ""+( (cal.get(Calendar.HOUR_OF_DAY)*10000) + (cal.get(Calendar.MINUTE)*100) + cal.get(Calendar.SECOND) );
    message(stringUserkey);
    stringSQL = "DELETE " +
                          " FROM InvoM030TempBody " +
              " WHERE UseKey = '" + stringUserkey + "'";
    dbInvoice.execFromPool(stringSQL);
    for(int i=0;i<A_table.length;i++){
      stringSQL =  " INSERT " +
                              " INTO InvoM030TempBody" +
                        "(" +
                        " UseKey," +
                        " RecordNo," +
                        " DetailItem," +
                        " Remark" +
                        " ) " +
                 " VALUES (" +
                                    "'" + stringUserkey +  "'," +
                                    A_table[i][1] + "," +
                                    "'" +  A_table[i][2] +  "'," +
                                    "'" +  A_table[i][3]+  "'" +                
                                  ")";
      dbInvoice.execFromPool(stringSQL);
    }
    //
    String retSystemDateTime[][] = dbInvoice.queryFromPool("spInvoSystemDateTime  'Admin'");
    String stringSystemDateTime ="";
    stringSystemDateTime = retSystemDateTime[0][0].replace("-","/");
    stringSystemDateTime = stringSystemDateTime.substring(0,19);
    message(stringSystemDateTime);
    getButton("buttonMoney").doClick() ;
    stringSQL = "spInvoM030Insert " +
               "'" + stringNowInvoiceNo + "'," +
               "'" + stringFSChar + "'," +
               "'"+ stringStartNo + "'," +
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
               "'1'," +
               "'" + stringInvoiceYYYYMM + "'," +
                   stringInvoiceBook + "," +
               "'"+ stringEndYes + "'," +
               "'" + getUser() + "'," +
               "'" + stringSystemDateTime  + "'," +
               "'" + stringSystemDateTime  + "'," +          
                   "'A'," +
               "'" + stringUserkey + "'";
    Object aa = dbInvoice.execFromPool(stringSQL);
    System.out.println(">>>aa:" + aa);
    
    setValue("InvoiceNo",stringNowInvoiceNo);
    message("已產生發票 = " +  stringNowInvoiceNo);         
    return false;
  }
}
