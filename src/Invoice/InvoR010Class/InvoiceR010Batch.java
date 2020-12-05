package Invoice.InvoR010Class;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
//import com.lowagie.tools.Executable;

/**
 * 1. 取畫面上列表
 * 2. 列表中找尋已作廢發票並通知
 * 3. 列印
 * 4. 寫入列印紀錄
 */

public class InvoiceR010Batch extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    System.out.println("-----------------------------------------S") ;
    String pPrinter = getValue("Printer").trim() ; //"TOSHIBA e-STUDIO452Series PCL6"; 
    //
    if("".equals(pPrinter)) {
        message("請選擇列表機") ;
        return  value ;
    }
    /*
    Farglory.util.FargloryUtil  exeUtil       =  new  Farglory.util.FargloryUtil() ;
    String                              pPrinterT  =  convert.replace(pPrinter,  "\\",  "/") ;
    if(!exeUtil.isPrinterExist2 (pPrinterT)) {
        message("["+pPrinterT+"] 列表機不存在，請洽資訊室安裝。") ;
        return  value ;
    }
    */
    //Runtime.getRuntime().exec("cmd /c start taskkill /f /im acrord32.exe");
    String CompanyNo=getValue("CompanyNo");
    String InvoiceDate = getValue("InvoiceDate");
    String [][] table = getTableData("table1");
    String PrintUserNo = getUser();
    Hashtable ServerPrint = new Hashtable();
     StringBuffer choose = new StringBuffer();
     int cv=0;
    for(int x = 0 ; x<table.length ; x++){
      if(table[x][0].trim().equals("Y")){
        if(choose.length()==0)choose.append("'"+table[x][2]+"'"); else choose.append(",'"+table[x][2]+"'");
        cv++;
      }
    }
    if(cv==0){
      messagebox("請至少勾選一筆資料");
      return value;
    }
    StringBuffer sql = new StringBuffer();
    sql.append(" select InvoiceNo ");
    sql.append(" from InvoM030");
    sql.append(" where DELYes='Y'");
    System.out.println("CompanyNo------>>"+CompanyNo.trim()) ;
    System.out.println("InvoiceDate------->>>>>"+InvoiceDate.trim()) ;
    if(CompanyNo.length()!=0)  sql.append(" and CompanyNo='"+CompanyNo+"'");
    if(InvoiceDate.length()!=0)  sql.append(" and SUBSTRING(InvoiceDate,1,4)+SUBSTRING(InvoiceDate,6,2)+SUBSTRING(InvoiceDate,9,2) between "+InvoiceDate.replaceAll("/","")+" ");
    if(choose.length()!=0)  sql.append(" and InvoiceNo IN ("+choose.toString()+") ORDER BY InvoiceNo");
    String [][] M030 = getTalk("Invoice").queryFromPool(sql.toString());
    System.out.println("-----------------------------------------11111") ;
    HashMap hM030 = new HashMap();
    for(int x=0 ; x<M030.length ; x++){
      hM030.put(M030[x][0],M030[x][0]);
    }
    StringBuffer vM030 = new StringBuffer();
    for(int x = 0 ; x<table.length ; x++){
      if(hM030. containsKey(table[x][2]) && table[x][0].trim().equals("Y")){
        if(vM030.length()==0)vM030.append("發票號碼作廢，請點選燈泡，檢視作廢發票清單\r\n");
        vM030.append("發票號碼 : "+table[x][2]+"\r\n");
      }
    }
    System.out.println("-----------------------------------------222222") ;
    String PrintDateTime=datetime.getToday("YYYYmmdd")+" "+datetime.getTime("hms");
    setValue("PrintDateTime",PrintDateTime);
    ServerPrint.put("PrintDateTime",PrintDateTime);   
    ServerPrint.put("CompanyNo",CompanyNo);
    ServerPrint.put("table",table);
    ServerPrint.put("PrintUserNo",PrintUserNo);
    ServerPrint.put("InvoiceDate",InvoiceDate);
    ServerPrint.put("PrintStatus","產生發票");
    ServerPrint.put("POSITION","130");
    ServerPrint.put("choose",choose);
    
    System.out.println("PrintDateTime=====>"+PrintDateTime) ;
    System.out.println("CompanyNo=====>"+CompanyNo) ;
    System.out.println("table=====>"+table) ;
    System.out.println("PrintUserNo=====>"+PrintUserNo) ;
    System.out.println("InvoiceDate=====>"+InvoiceDate) ;
    System.out.println("-----------------------------------------333333") ;
        
    Hashtable PDF=(Hashtable)call("ServerPrint","PDF",ServerPrint);
    System.out.println("-----------------------------------------44444") ;
        
    Vector PATH_A3=(Vector)PDF.get("FILE_NAME");
    Vector       vectorData  =  new  Vector() ;
    String[]      arrayTemp  =  null ;
    String []    A3_PATH = (String[])PATH_A3.toArray(new String[0]);
    talk dbInvoice = getTalk("Invoice");        
    String stringSQL = "";    
    //put("InvoR010_PDF",PDF);
    for(int x=0 ; x<A3_PATH.length ; x++){
      arrayTemp =  new  String[1] ;
      arrayTemp[0]  = A3_PATH[x]+"_A4_Mul_Print.pdf"  ;
      vectorData.add(arrayTemp) ;
    
      stringSQL = " INSERT InvoR010Batch" +
                     " ( " +
                     " PrintTime," +
                     " Stubpath," +
                     " Printer," +
                     " PrintYES " +
                     " ) " +                       
                     "VALUES " +
                     " ( " +
                       "'" + PrintDateTime + "'," +
                       "'" + arrayTemp[0] + "'," +
                       "'" + pPrinter + "'," +                         
                       "'N'" +
                     " ) " ;                                    
       dbInvoice.execFromPool(stringSQL) ;
    }
       //
    if(vM030.length()!=0)  message(vM030.toString() );
    getButton("button5").doClick() ;
    messagebox("已進入印表排程");
    //messagebox("印表完畢");
    getButton("button1").setVisible(false) ;
    return value;
  }
  public String getInformation(){
    return "---------------button1(發票套印).defaultValue()----------------";
  }
}
