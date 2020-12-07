package Invoice.InvoM030.func;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import Farglory.util.KUtils;
import jcx.db.talk;
import jcx.jform.bTransaction;

/**
 * Tip:
 * 這邊開的是行銷的發票( 哈扣ProcessInvoiceNo=1 )
 * 
 * @author B04391
 *
 */

public class InvoM030New_2020 extends bTransaction{
  public boolean action(String value)throws Throwable{
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
    message("");
    
    talk dbInvoice = getTalk(""+get("put_dbInvoice"));
    talk as400 = getTalk("AS400");
    talk sale = getTalk("Sale");
    dbInvoice = getTalk("Invoice");
    KUtils kUtil = new KUtils();
    
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
    stringSQL = "SELECT Nationality, CustomName FROM InvoM0C0 WHERE CustomNo = '" +  getValue("CustomNo").trim() + "'" ;
    String retInvoM0C0[][] = dbInvoice.queryFromPool(stringSQL);
    String customName = "";
    if(retInvoM0C0.length > 0){
      String stringNationality =  retInvoM0C0[0][0];
      customName = retInvoM0C0[0][1];
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
    
//    dbInvoice = getTalk("Invoice");
    /*
    //發票聯式
    Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil() ;
    if (getValue("CustomNo").length()==8 && exeUtil.isDigitNum (getValue("CustomNo")))
      setValue("InvoiceKind","3");
    else
      setValue("InvoiceKind","2");  
    */  
      
    //取發票號碼
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
    for(int i=0;i<retInvoM022.length;i++){    //就top 1 了你跑毛迴圈阿，還是你覺得top 1 不會只有一筆?
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
//    System.out.println("取得發票號碼>>>" + stringNowInvoiceNo);
    if (stringNowInvoiceNo.length() < 10){
      message("電腦發票已用完 請洽財務室領取!");
      return false;
    }
    
    message("取得發票號碼>>>" + stringNowInvoiceNo);
    
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
    stringSQL = "DELETE FROM InvoM030TempBody WHERE UseKey = '" + stringUserkey + "'";
    dbInvoice.execFromPool(stringSQL);
    for(int i=0;i<A_table.length;i++){
      stringSQL =  " INSERT INTO InvoM030TempBody" +
                        "(" +
                        " UseKey," +
                        " RecordNo," +
                        " DetailItem," +
                        " Remark" +
                        " ) VALUES (" +
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
    
    /**
     * 1. insert InvoM030
     * 2. insert GLEAPFUF
     * 3. check GLEDPFUF
     * 4. insert GLEDPFUF
     * 5. update InvoM022
     */
    try {
      Random r1 = new Random();
      StringBuilder sbSQL = new StringBuilder();
      
      //insert InvoM030
      sbSQL = new StringBuilder();
      sbSQL.append("INSERT  INTO  InvoM030 ");
      sbSQL.append("(InvoiceNo, InvoiceDate, InvoiceKind, CompanyNo, DepartNo, ProjectNo, InvoiceWay, Hubei, CustomNo, PointNo, ");
      sbSQL.append("InvoiceMoney, InvoiceTax, InvoiceTotalMoney, TaxKind, DisCountMoney,DisCountTimes, PrintYes, PrintTimes, DELYes, LuChangYes, ");
      sbSQL.append("ProcessInvoiceNo, Transfer, CreateUserNo, CreateDateTime, LastUserNo, LastDateTime, RandomCode, CustomName ) ");
      sbSQL.append("values ");
      sbSQL.append("( ");
      sbSQL.append("'").append(stringNowInvoiceNo).append("', ");
      sbSQL.append("'").append(getValue("InvoiceDate").trim()).append("', ");
      sbSQL.append("'").append(getValue("InvoiceKind").trim()).append("', ");
      sbSQL.append("'").append(getValue("CompanyNo").trim()).append("', ");
      sbSQL.append("'").append(getValue("DepartNo").trim()).append("', ");
      sbSQL.append("'").append(getValue("ProjectNo").trim()).append("', ");
      sbSQL.append("'").append(getValue("InvoiceWay").trim()).append("', ");
      sbSQL.append("'").append(getValue("HuBei").trim()).append("', ");
      sbSQL.append("'").append(getValue("CustomNo").trim()).append("', ");
      sbSQL.append("'").append(getValue("PointNo").trim()).append("', ");
      sbSQL.append("").append(getValue("InvoiceMoney").trim()).append(", ");      //未稅
      sbSQL.append("").append(getValue("InvoiceTax").trim()).append(", ");        //稅額
      sbSQL.append("").append(getValue("InvoiceTotalMoney").trim()).append(", "); //含稅
      sbSQL.append("'").append(getValue("TaxKind").trim()).append("', ");         //稅別
      sbSQL.append("").append(0).append(", ");                                    //已折讓金額
      sbSQL.append("").append(0).append(", ");                                    //已折讓次數
      sbSQL.append("'").append("N").append("', ");                                //已列印YN
      sbSQL.append("").append(0).append(", ");                                    //補印次數
      sbSQL.append("'").append("N").append("', ");                                //作廢YN
      sbSQL.append("'").append("N").append("', ");                                //入帳YN
      sbSQL.append("'").append("1").append("', ");                                //ProcessInvoiceNo
      sbSQL.append(null + ", ");                                                  //Transfer
      sbSQL.append("'").append(getUser().trim()).append("', ");                   //CreateUserNo
      sbSQL.append("'").append(stringSystemDateTime).append("', ");               //CreateDateTime
      sbSQL.append("'").append(getUser().trim()).append("', ");                   //LastUserNo
      sbSQL.append("'").append(stringSystemDateTime).append("', ");                //LastDateTime
      sbSQL.append("'").append(kUtil.add0(r1.nextInt(9999), 4, "F")).append("', ");  //RandomCode
      sbSQL.append("'").append(customName).append("' ");                         //客戶姓名
      sbSQL.append(") ");
      dbInvoice.execFromPool(sbSQL.toString());
      
      //更新InvoM022
      sbSQL = new StringBuilder();
      sbSQL.append("update InvoM022 ");
      sbSQL.append("set MaxInvoiceNo='").append(stringNowInvoiceNo).append("' ");
      sbSQL.append(",MaxInvoiceDate='").append(getValue("InvoiceDate").trim()).append("' ");
      sbSQL.append("where 1=1 ");
      sbSQL.append("and CompanyNo='").append(getValue("CompanyNo").trim()).append("' ");
      sbSQL.append("and DepartNo='").append(getValue("DepartNo").trim()).append("' ");
      sbSQL.append("and ProjectNo='").append(getValue("ProjectNo").trim()).append("' ");
      sbSQL.append("and InvoiceKind='").append(getValue("InvoiceKind").trim()).append("' ");
      sbSQL.append("and UseYYYYMM='").append(stringInvoiceDate).append("' ");
      sbSQL.append("and ENDYES='").append("N").append("' ");
      sbSQL.append("and CloseYes='").append("N").append("' ");
      sbSQL.append("AND (MaxInvoiceDate <='").append(getValue("InvoiceDate").trim()).append("' or MaxInvoiceDate IS NULL OR LEN(MaxInvoiceDate) = 0) ");
      sbSQL.append("and ProcessInvoiceNo='").append("1").append("' ");
      dbInvoice.execFromPool(sbSQL.toString());
      
      //寫入AS400
      Vector vectorSql = new Vector();
      //A : 主檔
      sbSQL = new StringBuilder();
      sbSQL.append("insert into GLEAPFUF ");
      sbSQL.append("(EA01U, EA02U, EA03U, EA04U, EA05U, EA06U, EA07U, EA08U, EA09U, EA10U, EA11U, EA12U, EA13U, EA14U, EA15U, EA16U, EA17U, EA18U, EA19U, EA20U, EA21U, EA22U) ");
      sbSQL.append("values ");
      sbSQL.append("(");
      sbSQL.append("'").append(stringNowInvoiceNo).append("', ");                 //發票號碼
      sbSQL.append("'").append(getValue("InvoiceDate").trim()).append("', ");     //發票日期
      sbSQL.append("'").append(getValue("InvoiceKind").trim()).append("', ");     //發票聯式
      sbSQL.append("'").append(getValue("CompanyNo").trim()).append("', ");       //公司代碼
      sbSQL.append("'").append(getValue("DepartNo").trim()).append("', ");        //部門代碼
      sbSQL.append("'").append(getValue("ProjectNo").trim()).append("', ");       //案別代碼
      sbSQL.append("'").append(getValue("InvoiceWay").trim()).append("', ");      //Invoice Way
      sbSQL.append("'").append(getValue("HuBei").trim()).append("', ");           //戶別代號
      sbSQL.append("'").append(getValue("CustomNo").trim()).append("', ");        //客戶代號
      sbSQL.append("'").append(getValue("PointNo").trim()).append("', ");         //摘要
      sbSQL.append("").append(getValue("InvoiceMoney").trim()).append(", ");      //未稅
      sbSQL.append("").append(getValue("InvoiceTax").trim()).append(", ");        //稅額
      sbSQL.append("").append(getValue("InvoiceTotalMoney").trim()).append(", "); //含稅
      sbSQL.append("'").append(getValue("TaxKind").trim()).append("', ");         //稅別
      sbSQL.append("").append(0).append(", ");             //已折讓金額
      sbSQL.append("").append(0).append(", ");             //已折讓次數
      sbSQL.append("'").append("N").append("', ");         //已列印YN
      sbSQL.append("").append(0).append(", ");             //補印次數
      sbSQL.append("'").append("N").append("', ");         //作廢YN
      sbSQL.append("'").append("N").append("', ");         //入帳YN
      sbSQL.append("'").append("").append("', ");          //發票處理方式
      sbSQL.append("'").append("收款").append("' ");       //收款/客服
      sbSQL.append(") ");
      as400.execFromPool(sbSQL.toString());
      
      //D : 客戶檔
      sbSQL = new StringBuilder();
      sbSQL.append("select ED01U from GLEDPFUF where ED01U = '" +  getValue("CustomNo").trim() + "' ");
      String[][] arrGLEDPFUF = as400.queryFromPool(sbSQL.toString());
      if(arrGLEDPFUF.length == 0 && !"".equals(customName)) {
        sbSQL = new StringBuilder();
        sbSQL.append("insert into GLEDPFUF ");
        sbSQL.append("(ED01U, ED02U) ");
        sbSQL.append("values ");
        sbSQL.append("(");
        sbSQL.append("'").append(getValue("CustomNo").trim()).append("', ");
        sbSQL.append("'").append(customName).append("' ");
        sbSQL.append(") ");
        as400.execFromPool(sbSQL.toString());
      }
//      as400.execFromPool( (String[]) vectorSql.toArray(new String[0]) );
      
      setValue("InvoiceNo",stringNowInvoiceNo);
      message("已產生發票 = " +  stringNowInvoiceNo);   
    } catch(Exception ex) {
      message("發生錯誤1:" + ex);
      return false;
    }
    
    return false;
  }
}
