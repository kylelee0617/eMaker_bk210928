package Invoice.InvoM030.func;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import javax.swing.TransferHandler.TransferSupport;

import Farglory.util.KUtils;
import Farglory.util.Transaction;
import jcx.db.talk;
import jcx.jform.bTransaction;

/**
 *  PT1527822019699
 *
 * Tip:
 * 這邊開的是行銷的發票( 哈扣ProcessInvoiceNo=1 )
 * 
 * @author B04391
 *
 */

public class InvoM030_New extends bTransaction{
  public boolean action(String value)throws Throwable{
    message("");
    
    talk dbInvoice = getTalk(""+get("put_dbInvoice"));
    talk as400 = getTalk("AS400");
    talk sale = getTalk("Sale");
    dbInvoice = getTalk("Invoice");
    KUtils kUtil = new KUtils();
    Transaction trans = new Transaction();
    
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
    
    String processNo = "1";
    if( "0351".equals( getValue("DepartNo").trim() ) ) {
      processNo = "2";
    }
    
    String transferName = "收款";
    if( "0351".equals( getValue("DepartNo").trim() ) ) {
      transferName = "客服";
    }
    
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
                                " AND ProcessInvoiceNo = '"+processNo+"'";
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
                    " AND ProcessInvoiceNo = '"+processNo+"'" +
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
    for(int i=0;i<retInvoM022.length;i++){    //就top 1 了你跑毛迴圈阿，還是你覺得top 1 不會只有一筆? (by Kyle)
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
     * 1.1. insert InvoM031
     * 2. insert GLEAPFUF
     * 3. check GLEDPFUF
     * 4. insert GLEDPFUF
     * 5. update InvoM022
     */
    try {
      Random r1 = new Random();
      StringBuilder sbSQL = new StringBuilder();
      
      String invoiceTime = "";
      String[] arrTmpInvoiceTime = stringSystemDateTime.split(" ")[1].trim().split(":");
      int tmpInvoTimeH = (Integer.parseInt(arrTmpInvoiceTime[0].trim()) + 1) >=24 ? 23:(Integer.parseInt(arrTmpInvoiceTime[0].trim()) + 1);
      invoiceTime = "" + tmpInvoTimeH + ":" + arrTmpInvoiceTime[1].trim() + ":" + arrTmpInvoiceTime[2].trim();
      //insert InvoM030
      sbSQL = new StringBuilder();
      sbSQL.append("INSERT  INTO  InvoM030 ");
      sbSQL.append("(InvoiceNo, InvoiceDate, InvoiceTime , InvoiceKind, CompanyNo, DepartNo, ProjectNo, InvoiceWay, Hubei, CustomNo, PointNo, "); //11
      sbSQL.append("InvoiceMoney, InvoiceTax, InvoiceTotalMoney, TaxKind, DisCountMoney,DisCountTimes, PrintYes, PrintTimes, DELYes, LuChangYes, ");  //21
      sbSQL.append("ProcessInvoiceNo, Transfer, CreateUserNo, CreateDateTime, LastUserNo, LastDateTime, RandomCode, CustomName ) ");
      sbSQL.append("values ");
      sbSQL.append("( ");
      sbSQL.append("'").append(stringNowInvoiceNo).append("', ");
      sbSQL.append("'").append(getValue("InvoiceDate").trim()).append("', ");
      sbSQL.append("'").append(invoiceTime).append("', ");                        //20201210 Kyle 增加發票時間
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
      sbSQL.append("'").append(processNo).append("', ");                                //ProcessInvoiceNo
      sbSQL.append("'").append("發票"+transferName).append("', ");                                      //Transfer
      sbSQL.append("'").append(getUser().trim().toUpperCase()).append("', ");                   //CreateUserNo
      sbSQL.append("'").append(stringSystemDateTime).append("', ");               //CreateDateTime
      sbSQL.append("'").append(getUser().trim()).append("', ");                   //LastUserNo
      sbSQL.append("'").append(stringSystemDateTime).append("', ");                //LastDateTime
      sbSQL.append("'").append(kUtil.add0(r1.nextInt(9999), 4, "F")).append("', ");  //RandomCode
      sbSQL.append("'").append(customName).append("' ");                         //客戶姓名
      sbSQL.append(") ");
      trans.append(sbSQL.toString());
      
      //寫入InvoM031
      sbSQL = new StringBuilder();
      sbSQL.append("INSERT INTO InvoM031 ");
      sbSQL.append("(InvoiceNo, RecordNo, DetailItem, Remark) ");
      sbSQL.append("VALUES ");
      for(int ii=0 ; ii<A_table.length ; ii++) {
        String[] aTable = A_table[ii];
        if("".equals(aTable[2].trim())) continue;
        if(ii != 0) sbSQL.append(",");
        sbSQL.append("(");
        sbSQL.append("'").append(stringNowInvoiceNo).append("' ") ;
        sbSQL.append(",").append(aTable[1].trim()).append(" ") ;
        sbSQL.append(",'").append(aTable[2].trim()).append("' ") ;
        sbSQL.append(",'").append(aTable[3].trim()).append("' ") ;
        sbSQL.append(") ");
      }
      trans.append(sbSQL.toString());
      
      /*
      //TEST 測試一筆發生錯誤 >> KEY重複
      //insert InvoM030
      sbSQL = new StringBuilder();
      sbSQL.append("INSERT  INTO  InvoM030 ");
      sbSQL.append("(InvoiceNo, InvoiceDate, InvoiceTime , InvoiceKind, CompanyNo, DepartNo, ProjectNo, InvoiceWay, Hubei, CustomNo, PointNo, "); //11
      sbSQL.append("InvoiceMoney, InvoiceTax, InvoiceTotalMoney, TaxKind, DisCountMoney,DisCountTimes, PrintYes, PrintTimes, DELYes, LuChangYes, ");  //21
      sbSQL.append("ProcessInvoiceNo, Transfer, CreateUserNo, CreateDateTime, LastUserNo, LastDateTime, RandomCode, CustomName ) ");
      sbSQL.append("values ");
      sbSQL.append("( ");
      sbSQL.append("'").append(stringNowInvoiceNo).append("', ");
      sbSQL.append("'").append(getValue("InvoiceDate").trim()).append("', ");
      sbSQL.append("'").append(invoiceTime).append("', ");                        //20201210 Kyle 增加發票時間
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
      sbSQL.append("'").append("發票系統").append("', ");                                      //Transfer
      sbSQL.append("'").append(getUser().trim()).append("', ");                   //CreateUserNo
      sbSQL.append("'").append(stringSystemDateTime).append("', ");               //CreateDateTime
      sbSQL.append("'").append(getUser().trim()).append("', ");                   //LastUserNo
      sbSQL.append("'").append(stringSystemDateTime).append("', ");                //LastDateTime
      sbSQL.append("'").append(kUtil.add0(r1.nextInt(9999), 4, "F")).append("', ");  //RandomCode
      sbSQL.append("'").append(customName).append("' ");                         //客戶姓名
      sbSQL.append(") ");
      trans.append(sbSQL.toString());
      */
      
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
      sbSQL.append("and ProcessInvoiceNo='").append(processNo).append("' ");
      sbSQL.append("and InvoiceBook='").append(stringInvoiceBook).append("' ");
      sbSQL.append("and StartNo='").append(stringStartNo).append("' ");
      trans.append(sbSQL.toString());
      
      //執行Transaction
      trans.close();
      String rsTrans = dbInvoice.execFromPool(trans.getString());
      System.out.println("rsTrans>>>" + rsTrans);
      
      //檢查是否開立成功，若022的發票號碼跟這邊一樣，代表沒開立成功，不做接下去的動作
      String testSql = "SELECT TOP 1 FSChar , SUBSTRING(MaxInvoiceNo,3,10)+1 " +
                        " FROM InvoM022 " +
                        " WHERE CompanyNo = '" + getValue("CompanyNo").trim() + "'" +
                        " AND DepartNo = '" + getValue("DepartNo").trim() + "'" +
                        " AND ProjectNo = '" + getValue("ProjectNo").trim() + "'" +
                        " AND InvoiceKind = '" + getValue("InvoiceKind").trim() + "'" +
                        " AND UseYYYYMM = '" + stringInvoiceDate + "'" +
                        " AND (MaxInvoiceDate <= '" + getValue("InvoiceDate").trim() + "' OR MaxInvoiceDate IS NULL OR LEN(MaxInvoiceDate) = 0)" +
                        " AND ENDYES = 'N' " +
                        " AND CloseYes = 'N' " +
                        " AND ProcessInvoiceNo = '"+processNo+"'";
      String retTestInvoM022[][] = dbInvoice.queryFromPool(testSql);
      if( stringNowInvoiceNo.equals(retTestInvoM022[0][0].trim()+retTestInvoM022[0][1].trim()) ) {
        messagebox("發生錯誤  \uff61\uff9f\u30fd(\uff9f?\u0414`)\uff89\uff9f\uff61  ...請洽資訊主辦");
        return false;
      }
      
      
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
      sbSQL.append("'").append(transferName).append("' ");       //收款/客服
      sbSQL.append(") ");
      as400.execFromPool(sbSQL.toString());
      
      //D : 客戶檔
      sbSQL = new StringBuilder();
      sbSQL.append("select ED01U from GLEDPFUF where ED01U = '" +  getValue("CustomNo").trim() + "' ");
      String[][] arrGLEDPFUF = as400.queryFromPool(sbSQL.toString());
      if(arrGLEDPFUF.length == 0 && !"".equals(customName)) {
    //400特殊需求，小於六個字要補滿到六個全形
        if(customName.length() < 6) customName = kUtil.addWhat(customName, 6, "　", 1);
    
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
      message("發生錯誤:" + ex);
      return false;
    }
    
    return false;
  }
}
