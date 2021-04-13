package Invoice.InvoM040.func;
import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;

import Farglory.util.KUtils;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;

/**
 * 原檔 : PT1387941781533
 * 2020/12/07 Kyle : add開立折讓寫入AS400功能
 * 
 * @author B04391
 *
 */

public class DisCount_New extends bTransaction{
  
  KUtils kUtil = new KUtils();
  
  public boolean action(String value)throws Throwable{
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
    message("");
    String stringDiscountDate = getValue("DiscountDate");
    if(!check.isACDay(stringDiscountDate.replace("/",""))) {
        message("折讓單日期錯誤(YYYY/MM/DD)");
        return false;
    }
    talk dbInvoice = getTalk("Invoice");
    String stringSQL = "";
    String stringUserkey = "";
    
    //明細
    String [][] A_table = getTableData("table1");
    if (A_table.length ==0){
       message("明細必須至少有一筆");
       return false;
    }
    
    //折讓日期不可小於發票日期
    for(int ii=0 ; ii<A_table.length ; ii++) {
      String[] aTable = A_table[ii];
      String thisInvoDate = aTable[3].trim();
      long subDays = kUtil.subACDaysRDay(stringDiscountDate, thisInvoDate);
      if(subDays < 0) {
        message("折讓日期不可小於第" + aTable[1].trim() + "筆發票日期");
        return false;
      }
    }
    
    Calendar cal= Calendar.getInstance();//Current time
    stringUserkey = getUser() + "_T" + ""+( (cal.get(Calendar.HOUR_OF_DAY)*10000) + (cal.get(Calendar.MINUTE)*100) + cal.get(Calendar.SECOND) );
    stringSQL = "DELETE FROM InvoM041TempBody WHERE UseKey = '" + stringUserkey + "'";
    dbInvoice.execFromPool(stringSQL);
    int intBodyCount = 0; 
    String stringCustomNo = "";
    String stringMessage = "";
    int intCustomNo = 0;
    for(int i=0;i<A_table.length;i++){  //寫副檔
       if(!A_table[i][8].equals("0")){
           if (!stringCustomNo.equals(A_table[i][18])){
             stringCustomNo = A_table[i][18];
           stringMessage += stringCustomNo+ "、";
             intCustomNo ++;     
         }
        stringSQL =  "INSERT INTO InvoM041TempBody " +
                          "(" +
                            " UseKey," +
                            " RecordNo," +
                            " ChoiceYES," +
                            " InvoiceNo," +
                            " InvoiceKind," +
                            " InvoiceWay," +
                            " PointNo," +
                            " InvoiceMoney," +
                            " InvoiceTax," +
                            " InvoiceTotalMoney," +
                            " YiDiscountMoney," +
                            " DiscountItemMoney," +
                            " TaxRate," +
                            " TaxKind," +
                            " ProcessInvoiceNo," +
                            " DisCountTimes" +
                          " ) " +
                   " VALUES (" +
                      "'" + stringUserkey +  "'," +
                      (intBodyCount + 1) + "," +
                      "'" +  A_table[i][0] +  "'," +                
                      "'" +  A_table[i][2] +  "'," +
                      "'" +  A_table[i][12]+  "'," +                
                      "'" +  A_table[i][13]+  "'," +                
                      "'" +  A_table[i][4]+  "'," +               
                           A_table[i][10]+  "," +          //8     
                           A_table[i][11]+  "," +               
                           A_table[i][6]+  "," +                
                           A_table[i][7]+  "," +                                     
                           A_table[i][8]+  "," +                                                         
                      "'" +  A_table[i][17]+  "'," +                
                      "'" +  A_table[i][14]+  "'," +                                
                      "'" +  A_table[i][15]+  "'," +                                
                      "'" +  A_table[i][16]+  "'" +                                               
                      ")";
        dbInvoice.execFromPool(stringSQL);
        intBodyCount++;
      }
    }
    if(intCustomNo >1){ 
      stringMessage = stringMessage.substring(0,stringMessage.length()-1);
      message("同一張折讓不可有多筆客戶的發票!，請開不同的折讓單；" + stringMessage);     
        return false; 
    } 
    /*
    if (stringDiscountDate.equals("9999/01/01")){
    } 
    */
    // 計算產生幾筆折讓單(遇到每{n=N_RecordNo%}筆作循環時使用)
    int intDiscountNoCount = intBodyCount  / 8;
    intDiscountNoCount ++;
    if (intBodyCount % 8 == 0) intDiscountNoCount = intDiscountNoCount -1;
    if(intDiscountNoCount == 0){
      message("發票明細 不可空白!");
      return false;
    }
    getButton("button3").doClick();
    //
    String retSystemDateTime[][] = dbInvoice.queryFromPool("spInvoSystemDateTime  'Admin'");
    String stringSystemDateTime ="";
    stringSystemDateTime = retSystemDateTime[0][0].replace("-","/");
    stringSystemDateTime = stringSystemDateTime.substring(0,19);
    //
    stringSQL = "spInvoM040Insert " +
                   intDiscountNoCount + "," +
               "'" + getValue("DiscountDate").trim() + "'," +
               "'" + getValue("CompanyNo").trim() + "'," +
               "'" + getValue("DepartNo").trim() + "'," +
               "'" + getValue("ProjectNo").trim() + "'," +           
               "'" + getValue("HuBei").trim() + "'," +                               
               "'" + getValue("CustomNo").trim() + "'," +                              
               "'" + getValue("DiscountWay").trim() + "'," +                     
               "'',"
               + getValue("DiscountMoney").trim() + ","                                         
               + getValue("DiscountTax").trim() + ","                                         
               + getValue("DiscountTotalMoney").trim() + "," +
               "'1'," +                              
               "'" + getUser() + "'," +
               "'" + stringSystemDateTime  + "'," +
               "'" + stringSystemDateTime  + "'," +          
                   "'A'," +
               "'" + stringUserkey + "'," +
               "'" + getValue("Reason").trim() + "'" ;
    dbInvoice.execFromPool(stringSQL);
    stringSQL = "SELECT DiscountNo FROM InvoM040 WHERE EmployeeNo = '" + getUser() + "' AND ModifyDateTime = '" + stringSystemDateTime + "'";
    String retInvoM040[][] = dbInvoice.queryFromPool(stringSQL);
    String stringDiscountNo = "";
    for(int n=0;n<retInvoM040.length;n++){
      stringDiscountNo += retInvoM040[n][0] + ";";
      
      //寫入AS400折讓主檔
      talk as400 = getTalk("AS400");
      String disCountNo = retInvoM040[n][0].trim();
      StringBuilder sbSQL = new StringBuilder();
      sbSQL.append("INSERT INTO GLEBPFUF ");
      sbSQL.append("(EB01U, EB02U, EB03U, EB04U, EB05U, EB06U, EB07U, EB08U, EB09U, EB10U, EB11U, EB12U, EB13U, EB14U, EB15U, EB16U, EB17U, EB18U, EB19U) ");
      sbSQL.append("values ");
      sbSQL.append("(");
      sbSQL.append("'").append(disCountNo).append("', ");                 //折讓號碼
      sbSQL.append("'").append(getValue("DiscountDate").trim()).append("', ");     //折讓日期
      sbSQL.append("'").append(getValue("CompanyNo").trim()).append("', ");       //公司代碼
      sbSQL.append("'").append(getValue("DepartNo").trim()).append("', ");        //部門代碼
      sbSQL.append("'").append(getValue("ProjectNo").trim()).append("', ");       //案別代碼
      sbSQL.append("'").append(getValue("HuBei").trim()).append("', ");           //戶別代號
      sbSQL.append("'").append(getValue("CustomNo").trim()).append("', ");        //客戶代號
      sbSQL.append("'").append(getValue("DiscountWay").trim()).append("', ");      //Invoice Way
      sbSQL.append("'").append("").append("', ");                                  //新戶別
      sbSQL.append("").append(getValue("DiscountMoney").trim()).append(", ");      //未稅
      sbSQL.append("").append(getValue("DiscountTax").trim()).append(", ");        //稅額
      sbSQL.append("").append(getValue("DiscountTotalMoney").trim()).append(", "); //含稅
      sbSQL.append("'").append("N").append("', ");                                //已列印YN
      sbSQL.append("").append(0).append(", ");                                    //補印次數
      sbSQL.append("'").append("N").append("', ");                                //作廢YN
      sbSQL.append("'").append("N").append("', ");                                //入帳YN
      sbSQL.append("'").append(getUser()).append("', ");                          //修改人
      sbSQL.append("'").append(stringSystemDateTime).append("', ");                //收款時間
      sbSQL.append("'").append("1").append("' ");                                 //PROCESS DISCOUNT
      sbSQL.append(") ");
      as400.execFromPool(sbSQL.toString());
      
      //寫入AS400折讓明細
      int GLECPFUFCount = 1;
      for(int i=0;i<A_table.length;i++){
        sbSQL = new StringBuilder();
        sbSQL.append("INSERT INTO GLECPFUF ");
        sbSQL.append("(EC01U, EC02U, EC03U, EC04U, EC05U, EC06U, EC07U, EC08U, EC09U, EC10U) ");
        sbSQL.append("values ");
        sbSQL.append("(");
        sbSQL.append("'").append(disCountNo).append("', ");                         //折讓號碼
        sbSQL.append("").append(GLECPFUFCount).append(", ");                        //筆數
        sbSQL.append("'").append("Y").append("', ");                                //勾選
        sbSQL.append("'").append( A_table[i][2].trim() ).append("', ");             //發票號碼
        sbSQL.append("'").append( A_table[i][4].trim() ).append("', ");            //摘要代碼
        sbSQL.append("").append( A_table[i][10].trim() ).append(", ");               //未稅
        sbSQL.append("").append( A_table[i][11].trim() ).append(", ");               //稅額
        sbSQL.append("").append( A_table[i][6].trim() ).append(", ");               //總金額
        sbSQL.append("").append( A_table[i][7].trim() ).append(", ");              //已折讓金額
        sbSQL.append("").append( A_table[i][8].trim() ).append(" ");                //欲折讓金額
        sbSQL.append(") ");
        as400.execFromPool(sbSQL.toString());
        GLECPFUFCount++;
      }
      
    }
    action(9);
    message("已產生折讓單 = " + stringDiscountNo);    
    // 2013-12-25 複製到剪貼簿      
    Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil() ;
    exeUtil.ClipCopy (exeUtil.doSubstring(stringDiscountNo,  0,  stringDiscountNo.length()-1)) ;
    return false;
  }
}
