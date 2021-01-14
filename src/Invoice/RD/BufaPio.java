package Invoice.RD;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.util.*;

public class BufaPio extends bproc{
  
  KUtils util = new KUtils();
  
  public String getDefaultValue(String value)throws Throwable{
    System.out.println("補發票行動>>>>>>>>>> Start");
    talk dbInvoice = getTalk(""+get("put_dbInvoice"));
    talk dbAs400 = getTalk("AS400");
    String GENLIB = ((Map)get("config")).get("GENLIB").toString().trim();
    
    String projectId = this.getValue("projectID").trim();
    String sDate = this.getValue("SDate").trim();
    String eDate = this.getValue("EDate").trim();
    String fsChar = this.getValue("FSChar").trim();
    StringBuilder sql = null;
    
    //not null
    if( "".equals(sDate) ) {
      messagebox("S日期必填");
      return value;
    }
    
    //TODO: 依照條件取M030發票
    sql = new StringBuilder();
    sql.append("SELECT * FROM INVOM030 a ");
    sql.append("WHERE 1=1 ");
    sql.append("AND invoiceDate >= '" + util.formatACDate(sDate) + "' ");
    if( !"".equals(eDate) ) sql.append("AND invoiceDate <= '" + util.formatACDate(eDate) + "' ");
    if( !"".equals(projectId) ) sql.append("AND projectNo = '" + projectId + "' ");
    if( !"".equals(fsChar) ) sql.append("AND substring(invoiceNO,1,2) = '" + fsChar + "' ");
    sql.append("ORDER BY CreateDateTime desc , InvoiceNo desc");
    String[][] retM030 = dbInvoice.queryFromPool(sql.toString());
    
    if( retM030.length == 0 ) {
      messagebox("查無發票資料");
      return value;
    }
    
    //TODO: 寫入AS400 A 檔，並組成客戶in查詢條件
    String[] arrCustomNos = new String[retM030.length]; 
    for(int idx=0 ; idx<retM030.length ; idx++) {
      String transfer = retM030[idx][23].toString().trim();
      if(transfer.length() >= 4) transfer = transfer.substring(0 , 2);
      
      sql = new StringBuilder();
      sql.append("insert into "+GENLIB+".GLEAPFUF ");
      sql.append("(EA01U, EA02U, EA03U, EA04U, EA05U, EA06U, EA07U, EA08U, EA09U, EA10U, EA11U, EA12U, EA13U, EA14U, EA15U, EA16U, EA17U, EA18U, EA19U, EA20U, EA21U, EA22U) ");
      sql.append("values ");
      sql.append("(");
      sql.append("'").append( retM030[idx][0].toString().trim() ).append("', ");         //發票號碼
      sql.append("'").append( retM030[idx][1].toString().trim() ).append("', ");        //發票日期
      sql.append("'").append( retM030[idx][3].toString().trim() ).append("', ");        //發票聯式
      sql.append("'").append( retM030[idx][4].toString().trim() ).append("', ");       //公司代碼
      sql.append("'").append( retM030[idx][5].toString().trim() ).append("', ");        //部門代碼
      sql.append("'").append( retM030[idx][6].toString().trim() ).append("', ");      //案別代碼
      sql.append("'").append( retM030[idx][7].toString().trim() ).append("', ");       //Invoice Way
      sql.append("'").append( retM030[idx][8].toString().trim() ).append("', ");        //戶別代號
      sql.append("'").append( retM030[idx][9].toString().trim() ).append("', ");        //客戶代號
      sql.append("'").append( retM030[idx][11].toString().trim() ).append("', ");         //摘要
      sql.append("").append( retM030[idx][12].toString().trim() ).append(", ");         //未稅
      sql.append("").append( retM030[idx][13].toString().trim() ).append(", ");        //稅額
      sql.append("").append( retM030[idx][14].toString().trim() ).append(", ");         //含稅
      sql.append("'").append( retM030[idx][15].toString().trim() ).append("', ");         //稅別
      sql.append("").append( retM030[idx][16].toString().trim() ).append(", ");             //已折讓金額
      sql.append("").append( retM030[idx][17].toString().trim() ).append(", ");             //已折讓次數
      sql.append("'").append( retM030[idx][18].toString().trim() ).append("', ");         //已列印YN
      sql.append("").append( retM030[idx][19].toString().trim() ).append(", ");             //補印次數
      sql.append("'").append( retM030[idx][20].toString().trim() ).append("', ");         //作廢YN
      sql.append("'").append( retM030[idx][21].toString().trim() ).append("', ");         //入帳YN
      sql.append("'").append( retM030[idx][22].toString().trim() ).append("', ");          //發票處理方式
      sql.append("'").append( transfer ).append("' ");       //收款/客服
      sql.append(") ");
      dbAs400.execFromPool(sql.toString());
      
      arrCustomNos[idx] = retM030[idx][9].toString().trim();
    }
    
    //TODO: 依照發票取M0C0客戶
    sql = new StringBuilder();
    sql.append("SELECT DISTINCT CustomNo , CustomName from InvoM0C0 ");
    sql.append("WHERE 1=1 ");
    sql.append("AND CustomNo in (" + util.genQueryInString(arrCustomNos) + ") ");
    String[][] retM0C0 = dbInvoice.queryFromPool(sql.toString());
    
    //TODO: 寫入AS400 E檔
    for(int idx=0 ; idx<retM0C0.length ; idx++) {
      sql = new StringBuilder();
      sql.append("insert into "+GENLIB+".GLEDPFUF ");
      sql.append("(ED01U, ED02U) ");
      sql.append("values ");
      sql.append("(");
      sql.append("'").append( retM0C0[idx][0].toString().trim() ).append("', ");
      sql.append("'").append( retM0C0[idx][1].toString().trim() ).append("' ");
      sql.append(") ");
      dbAs400.execFromPool(sql.toString());
    }
    
    message("共轉檔發票" + retM030.length + "筆，客戶" + retM0C0.length + "筆");
    
    System.out.println("補發票行動>>>>>>>>>> End");
    return value;
  }
  public String getInformation(){
    return "---------------BUFAPIO(GO!!!).defaultValue()----------------";
  }
}
