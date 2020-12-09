package  Sale ;
import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import    jcx.net.*;
import    jcx.net.smtp ;
import  com.jacob.activeX.*;
import  com.jacob.com.*;
import  Farglory.util.FargloryUtil ;
import java.text.SimpleDateFormat;

/**
 * 退戶折讓
 * @author B04391
 *
 */

public class Sale05M094 extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    //201808check BEGIN
    System.out.println("chk==>"+getUser()+" , value==>退戶開折讓");
      if(getUser() != null && getUser().toUpperCase().equals("B9999")) {
        messagebox("退戶開折讓權限不允許!!!");
        return value;
    }
    //201808check FINISH    
    //退戶開折讓單
    
    talk          dbSale              =  getTalk((String)get("put_dbSale")) ;//SQL2000
    talk            dbFED1                      = getTalk(""+get("put_dbFED1"));
    talk            dbInvoice                    =  getTalk(""+get("put_dbInvoice"));
    FargloryUtil  exeUtil               =  new  FargloryUtil() ;
    String stringSQL                   = "";
    String stringInvoiceMessage = "";
    String stringCompanyNo       = "";
    String stringDocNo               ="";
    String stringCond                 ="";
    String stringDepartNo           = "";
    String stringProjectID1          = "";
    String stringEDate                = "";
    String stringYM                     ="";
    double    intAmt                        =0;
    String  stringUserID           =  getUser() ;
    String  stringDateTime      =  datetime.getTime("YYYY/mm/dd h:m:s") ;
    /*
    String stringHuBei ="HuBei in (";
    stringSQL="SELECT Position FROM Sale05M092  "+
              " where OrderNo = '" + getValue("field3").trim() + "' " ;
    String retSale05M092[][] = dbSale.queryFromPool(stringSQL);
    for(int i = 0 ;i < retSale05M092.length;i++){
      if (i==0){
        stringHuBei=stringHuBei+"'" +retSale05M092[i][0].trim() + "'";
      }else {
        stringHuBei=stringHuBei+",'" +retSale05M092[i][0].trim() + "'";
      }     
    }
    stringHuBei=stringHuBei+")";  
    */
    String stringHuBei ="HuBei in (";
    stringSQL = " SELECT T95.Position "  +
                          " FROM Sale05M094 T94,Sale05M095 T95" +             
              " WHERE T94.ProjectID1=T95.ProjectID1   "+
                    " AND T94.OrderNo=T95.OrderNo   "+
                    " AND T94.OrderNo = '" + getValue("field3").trim() + "'" + 
                  " AND T94.TrxDate=T95.TrxDate "+
                    " AND T94.TrxDate='"+ getValue("field2").trim() + "'" ;
    String retSale05M094[][] = dbSale.queryFromPool(stringSQL);
    for(int i = 0 ;i < retSale05M094.length;i++){
      // [戶別]限制條件
      if (i==0){
        stringHuBei=stringHuBei+"'" +retSale05M094[i][0].trim() + "'";
      }else {
        stringHuBei=stringHuBei+",'" +retSale05M094[i][0].trim() + "'";
      }     
      /* [顧客] 限制條件*/
      stringSQL="select T91.CustomNo "  +
                          " from sale05m094 T94,sale05m091 T91 "+
                       " where T94.OrderNo='" + getValue("field3").trim() + "' " +
                           " and T94.OrderNo=T91.OrderNo " + 
                           " and  ISNULL(T91.StatusCd,'')<>'C' ";
      String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
      stringCond ="CustomNo in (";
      for(int j = 0 ;j < retSale05M091.length;j++){
        if (j==0){
          stringCond=stringCond+"'" +retSale05M091[j][0].trim() + "'";      
        }else {
          stringCond=stringCond+",'" +retSale05M091[j][0].trim() + "'";     
        } 
      }
      stringCond=stringCond+")";  
        
      String [][] tb2_string=getTableData("table2");    
      //if(stringFlowStatus.equals("業管-審核")){
        //stringSQL = " SELECT TOP 1 InvoiceNo " + 
        //            " FROM Sale05M087 "  +
        //          " WHERE DocNo = '" + getValue("DocNo").trim() + "'";
        //String retSale05M087[][] = dbSale.queryFromPool(stringSQL);
        //if(retSale05M087.length > 0){
        //  message(  retSale05M087[0][0] + "發票已開立 !");
        //  return value; 
        //}         
        
        //Temp 
        // 公司限制條件
        stringSQL = " SELECT T80.CompanyNo, " + 
                          " T80.DepartNo, " + 
                          " T80.ProjectID1, " + 
                          " T94.TrxDate, " + 
                          " T80.DocNo "+
                    " FROM Sale05M086 T86 , SALE05M094 T94,Sale05M080 T80"  +
                  " WHERE T94.OrderNo = '" + getValue("field3").trim() + "'" +
                  " AND T86.OrderNo=T94.OrderNo" +
                  " AND T80.DocNo=T86.DocNo ";
        String retSale05M080[][] = dbSale.queryFromPool(stringSQL);       
        for(int intSale05M080 = 0 ;intSale05M080 < retSale05M080.length;intSale05M080++){
            stringCompanyNo  =  retSale05M080[intSale05M080][0].trim();
            stringDepartNo      =  retSale05M080[intSale05M080][1].trim();
            stringProjectID1     =  retSale05M080[intSale05M080][2].trim();       
            stringEDate           =  retSale05M080[intSale05M080][3].trim();  
            //stringEDate.substring(0,4)+stringEDate.substring(5,7)
            stringYM                =  (Integer.parseInt(stringEDate.substring(0,4))-1911)+stringEDate.substring(5,7);      
            stringDocNo          =  retSale05M080[intSale05M080][4].trim(); // 沒使用
            //發票開哪一天??  
        }
        //
    }//END retSale05M094
    stringHuBei=stringHuBei+")";    
    String stringInvoiceNo = "";    
    int intInvoiceNo = 0;
    
    //找到已開過發票
    /*
    stringSQL = " SELECT InvoiceNo,DisCountMoney,HuBei,PointNo,InvoiceTotalMoney,CustomNo FROM " 
                + get("put_dbInvoice") +".dbo.Invom030" +
                " where HuBei in(SELECT Position FROM Sale05M092 " +
                " where OrderNo = '" + getValue("field3").trim() +"' ) "+
                " and ProjectNo='" + stringProjectID1 + "'  AND " + stringCond  +
                " and HuBei='" + retSale05M094[i][0].trim() + "'" +
                " order by CustomNo";
    */
     // 0  InvoiceNo              1  DisCountMoney      2  HuBei        3  PointNo
     // 4  InvoiceTotalMoney    5  CustomNo             6  CompanyNo      7  DepartNo
    String stringDeptCd="";
    if (stringProjectID1.equals("H45A"))
      stringDeptCd= " and (ProjectNo='H45A' or ProjectNo='H45T') " ;
    else if (stringProjectID1.equals("H90A")) //Mei 1011022
      stringDeptCd= " and (ProjectNo='H90A' or ProjectNo='H90T') " ;    
    else if (stringProjectID1.equals("H96A")) //Mei 1020506
      stringDeptCd= " and (ProjectNo='H96A' or ProjectNo='H96T') " ;
    else if (stringProjectID1.equals("H75A")) //Mei 1031125
      stringDeptCd= " and (ProjectNo='H75A' or ProjectNo='H75T') " ;                
    else if (stringProjectID1.equals("H99A")) //Mei 1040528
      stringDeptCd= " and (ProjectNo='H99A' or ProjectNo='H99T') " ;              
    else if (stringProjectID1.equals("H105A")) //Mei 1040714
      stringDeptCd= " and (ProjectNo='H105A' or ProjectNo='H105T') " ;    
    else if (stringProjectID1.equals("H103A")) //Mei 1061005
      stringDeptCd= " and (ProjectNo='H103A' or ProjectNo='H103T') " ;                                                    
    else
      stringDeptCd= " and ProjectNo='" + stringProjectID1  +"' " ;
      
    
    stringSQL = " SELECT  InvoiceNo,                  DisCountMoney,  HuBei,            PointNo, "  +
                             " InvoiceTotalMoney,  CustomNo,            CompanyNo,  DepartNo,ProjectNo "  +
                " FROM  Invom030 " +
              " WHERE  " + stringHuBei + stringDeptCd +
                    //" AND  ProjectNo='" + stringProjectID1 + "' "  +
                  " AND " + stringCond  +
                    //" and HuBei='" + retSale05M094[i][0].trim() + "'" +
                    //" and CompanyNo ='" + stringCompanyNo +"' " +
                     " AND  (InvoiceTotalMoney-DisCountMoney) >0"+
                    " AND DELYes='N'    AND PointNo<>'2999'  AND PointNo<>'2109'  " + 
            " ORDER BY CompanyNo,  CustomNo,PointNo,DepartNo"; //,DepartNo 1020506
    String retSale05M081[][] = dbInvoice.queryFromPool(stringSQL);
    if (retSale05M081.length == 0){
        message("找不到發票資料");
        return value;   
    }
    /* 測試 START
    String[]  arrayFiled  =  { "InvoiceNo","DisCountMoney","HuBei","PointNo","InvoiceTotalMoney","CustomNo",  "CompanyNo",  "DepartNo" };
    //
    Farglory.Excel.FargloryExcel  exeFun  =  new  Farglory.Excel.FargloryExcel( ) ;
    
    Vector    retVector        =  exeFun.getExcelObject("D:\\Book1.xls") ;
    Dispatch  objectSheet1  =  (Dispatch)retVector.get(1) ;
    Dispatch  objectClick     =  null ;
    exeFun.setPreView(false,  "D:\\oce.xls") ;  // 當不預覽時，且有傳入路徑時，另存新檔。
    int  intRecord  =   0 ;
    // 測試 END*/
    //收款單明細筆數
    int           intInvoiceCount            =  0;    
    int           intRecordNo                 =  1;//for Sale05M081 
    String     stringCustomNoTemp  =  "";
    String     stringNo                       =  "";
    String     stringCompanyNoOld  =  "" ;
    boolean  booleanFlag               =  true ;
      int            intNo                           =  0;
    
    for(int intSale05M081 = 0 ;intSale05M081 < retSale05M081.length;intSale05M081++){
        String  stringInvoiceNoOld =  retSale05M081[intSale05M081][0].trim();
        String  stringCustomNo     =  retSale05M081[intSale05M081][5].trim( ) ;
        //
        stringCompanyNo        =  retSale05M081[intSale05M081][6].trim( ) ;  
        //stringDepartNo            =  retSale05M081[intSale05M081][7].trim( ) ; 
        booleanFlag                =  !(stringCustomNoTemp+stringCompanyNoOld).equals(stringCustomNo+stringCompanyNo) ;  //&& !stringCustomNo.equals("") ;
        booleanFlag                =  booleanFlag  ||  ((intRecordNo-1) % 8   ==  0) ;  // 原 (intSale05M081==0) 改為 ((intRecordNo-1) % 8   ==  0)
        // 開立折讓頭檔資料進去 DB 中
        if (booleanFlag){
            /* 測試用 START
            exeFun.putDataIntoExcel(0,  intRecord,  "開立折讓頭檔資料進去 DB 中(每個顧客只做一次)",       objectSheet1) ;  
            intRecord++ ;
             測試用 END*/
             intRecordNo          =  1 ;
            // 取得折讓單號碼
            String stringTemp="";
            String stringmaxNo="";
            for(int j =(5-(stringTemp+stringDepartNo).length())  ;j>0 ;j--){
                  stringTemp="0"+stringTemp;
             }
            stringSQL="select MAX(DiscountNo) DiscountNo "  +
                                " from InvoM040 "+                      
                     " WHERE CompanyNo = '" + stringCompanyNo + "' " +
                          " AND DepartNo = '" + stringDepartNo + "'" +
                          " AND SUBSTRING(DiscountDate,1,7) = SUBSTRING('"+ stringEDate +"',1,7)  ";                    
            String retInvoM040[][] = dbInvoice.queryFromPool(stringSQL);
            if (retInvoM040[0][0].length()>0){    
                 stringmaxNo =  retInvoM040[0][0];
                 stringmaxNo = "000" + (Integer.parseInt(stringmaxNo.substring(stringmaxNo.length()-3))+1);
                 stringmaxNo = stringmaxNo.substring(stringmaxNo.length()-3); 
                 stringNo       = stringCompanyNo+stringTemp+stringDepartNo+stringYM+stringmaxNo;         
            }else{
                  stringNo= stringCompanyNo+stringTemp+stringDepartNo+stringYM+"001";
            }  
            System.out.println("stringNo:"+stringNo);
            stringInvoiceMessage = stringInvoiceMessage + stringNo + "\n";
            /*
            // 新增
            stringSQL="INSERT INTO Invom040 " +
                        "SELECT '" + stringNo + "','" + stringEDate +"', CompanyNo,"  +
                                      "'" + stringDepartNo+ "',ProjectNo, '',"  +
                                      "CustomNo,'A', '' ,0,"+
                                      "0 ,0, 'N' ,0 ,'N' ,"+
                                      "'N' ,'"+ getUser() +"', '" +   datetime.getTime("YYYY/mm/dd h:m:s")  +"', '1' ,'收款.退戶' " + 
                         " FROM  Invom030 "  +
                         " WHERE  ProjectNo='" + stringProjectID1 + "'" +
                                " AND InvoiceNo='" + stringInvoiceNoOld + "'" ;
            */              
            if (stringProjectID1.equals("H90A")){ //Mei1011023
                        // 新增
                        stringSQL="INSERT INTO Invom040 " +
                                    "SELECT '" + stringNo + "','" + stringEDate +"', CompanyNo,"  +
                                            "'" + stringDepartNo+ "','H90A', '',"   +
                                            "CustomNo,'A', '' ,0,"+
                                            "0 ,0, 'N' ,0 ,'N' ,"+
                                            "'N' ,'"+ getUser() +"', '" +   datetime.getTime("YYYY/mm/dd h:m:s")  +"', '1' ,'收款.退戶' , '' , '' " + 
                                     " FROM  Invom030 "  +
                                     " WHERE  (ProjectNo='H90A' or ProjectNo='H90T') " +
                                      " AND InvoiceNo='" + stringInvoiceNoOld + "'" ;                                                       
            }else if (stringProjectID1.equals("H105A")){ //Mei1040714
                        // 新增
                        stringSQL="INSERT INTO Invom040 " +
                                    "SELECT '" + stringNo + "','" + stringEDate +"', CompanyNo,"  +
                                            "'" + stringDepartNo+ "','H105A', '',"  +
                                            "CustomNo,'A', '' ,0,"+
                                            "0 ,0, 'N' ,0 ,'N' ,"+
                                            "'N' ,'"+ getUser() +"', '" +   datetime.getTime("YYYY/mm/dd h:m:s")  +"', '1' ,'收款.退戶' , '' , '' " + 
                                     " FROM  Invom030 "  +
                                     " WHERE  (ProjectNo='H105A' or ProjectNo='H105T') " +
                                      " AND InvoiceNo='" + stringInvoiceNoOld + "'" ;                                                                                                             
            }else{                                  
                        // 新增
                        stringSQL="INSERT INTO Invom040 " +
                                    "SELECT '" + stringNo + "','" + stringEDate +"', CompanyNo,"  +
                                            "'" + stringDepartNo+ "',ProjectNo, '',"  +
                                            "CustomNo,'A', '' ,0,"+
                                            "0 ,0, 'N' ,0 ,'N' ,"+
                                            "'N' ,'"+ getUser() +"', '" +   datetime.getTime("YYYY/mm/dd h:m:s")  +"', '1' ,'收款.退戶' , '' , '' " + 
                                     " FROM  Invom030 "  +
                                     " WHERE  ProjectNo='" + stringProjectID1 + "'" +
                                      " AND InvoiceNo='" + stringInvoiceNoOld + "'" ;                                                         
            }                         
                          
            dbInvoice.execFromPool(stringSQL);
            //System.out.println(intSale05M081+"INSERT INTO Invom040--------------------------"+stringSQL) ;
            stringSQL="INSERT INTO SALE05M096 values( " +
                      "'" + stringProjectID1 + "','" + getValue("field3").trim() +"', " +
                      "'" + getValue("field2").trim() +"','" + stringNo + "',0)"  ;
            dbSale.execFromPool(stringSQL);
            //System.out.println(intSale05M081+"INSERT INTO SALE05M096--------------------------"+stringSQL) ;
            intNo=0;
        }
        /* 測試用 START
         doShowDataInExcel(1,  intRecord,  objectSheet1,  exeFun,  arrayFiled,  retSale05M081[intSale05M081]) ;
         intRecord++ ;
         測試用 END*/
        int      integerYiDiscountMoney  =  Integer.parseInt(retSale05M081[intSale05M081][1]);
        double  intInvoiceTotalMoney        =  Double.parseDouble(retSale05M081[intSale05M081][4]);
        intNo=intNo+1;
        //計算稅率    
        stringSQL = "SELECT TaxRate," +
                         " TaxKind" +
                   " FROM InvoM010 " +
                   " WHERE PointNo = '" + retSale05M081[intSale05M081][3] + "'";
        String retInvoM010[][] = dbInvoice.queryFromPool(stringSQL);
        if (retInvoM010.length == 0){
            message("發票系統.摘要代碼 錯誤!");
            return value;       
        }
        String stringTaxRate      = "";
        String stringTaxKind      = "";   
        double    intInvoiceMoney  =  0;
        double    intInvoiceTax       =  0;
        //int intInvoiceTotalMoney = 0;
        for(int intInvoM010 = 0 ;intInvoM010 < retInvoM010.length;intInvoM010++){
            stringTaxRate = retInvoM010[intInvoM010][0].trim();
            stringTaxKind = retInvoM010[intInvoM010][1].trim();     
        }
        System.out.println("stringTaxRate"+stringTaxRate);
        System.out.println("stringTaxKind"+stringTaxKind);
        if (Double.parseDouble(stringTaxRate)  > 0){
            //intInvoiceMoney = Integer.parseInt(convert.FourToFive( (Float.parseFloat((intInvoiceTotalMoney -  integerYiDiscountMoney )) / (1 + Float.parseFloat(stringTaxRate) / 100)), 0));    
            intInvoiceMoney = Double.parseDouble(convert.FourToFive("" + (Double.parseDouble((intInvoiceTotalMoney -  integerYiDiscountMoney)+"") / (1 + Double.parseDouble(stringTaxRate) / 100)), 0));    
        }
        else{
            intInvoiceMoney = intInvoiceTotalMoney;
        }
        
        intInvoiceTax =intInvoiceTotalMoney-intInvoiceMoney-integerYiDiscountMoney;
                
        stringSQL="INSERT INTO Invom041 " +
                                    "select  '" + stringNo + "',"+intNo +" ,'Y' ,InvoiceNo , PointNo ,"+
                                                    ""+intInvoiceMoney+"  ,"+intInvoiceTax+",  InvoiceTotalMoney, "+integerYiDiscountMoney+" ,"+
                                                    " (InvoiceTotalMoney - " + integerYiDiscountMoney + ") " +
                                      " from Invom030 "  +
                           " where ProjectNo='" + retSale05M081[intSale05M081][8] + "'" +
                                             " AND InvoiceNo='" + stringInvoiceNoOld + "'"  ;                           
        dbInvoice.execFromPool(stringSQL);
        //System.out.println(intSale05M081+"INSERT INTO Invom041--------------------------"+stringSQL) ;
        
        //20201208 Kyle : 寫入AS400 GLECPFUF 折讓明細
        talk as400 = getTalk("AS400");
        StringBuilder sbSQL = new StringBuilder();
        stringSQL = "select * FROM Invom041 WHERE DiscountNo = '"+stringNo+"'" ;
        String[][] retM041 = dbInvoice.queryFromPool(stringSQL);
        for(int ii=0 ; ii<retM041.length ; ii++) {
          String[] m041 = retM041[ii];
          sbSQL.append("INSERT INTO GLECPFUF ");
          sbSQL.append("(EC01U, EC02U, EC03U, EC04U, EC05U, EC06U, EC07U, EC08U, EC09U, EC10U) ");
          sbSQL.append("values ");
          sbSQL.append("(");
          sbSQL.append("'").append( m041[0].trim() ).append("', ");                         //折讓號碼
          sbSQL.append("").append( m041[1].trim() ).append(", ");                        //筆數
          sbSQL.append("'").append( m041[2].trim() ).append("', ");                                //勾選
          sbSQL.append("'").append( m041[3].trim() ).append("', ");             //發票號碼
          sbSQL.append("'").append( m041[4].trim() ).append("', ");            //摘要代碼
          sbSQL.append("").append( m041[5].trim() ).append(", ");               //未稅
          sbSQL.append("").append( m041[6].trim() ).append(", ");               //稅額
          sbSQL.append("").append( m041[7].trim() ).append(", ");               //總金額
          sbSQL.append("").append( m041[8].trim() ).append(", ");              //已折讓金額
          sbSQL.append("").append( m041[9].trim() ).append(" ");                //欲折讓金額
          sbSQL.append(") ");
          as400.execFromPool(sbSQL.toString());
          intNo++;
        }
        
        
        stringSQL="update Invom040 set DiscountMoney= DiscountMoney+ " + intInvoiceMoney + " ,"+
                                                                " DiscountTax= DiscountTax+ " + intInvoiceTax + ", "  +
                                    " DiscountTotalMoney=DiscountTotalMoney + " + (intInvoiceTotalMoney -  integerYiDiscountMoney ) +" "+
                           " where DiscountNo ='" + stringNo +"'"   ;             
        dbInvoice.execFromPool(stringSQL);
        
        //20201208 Kyle : 寫入AS400 GLEBPFUF 折讓主檔
        stringSQL = "select * FROM Invom040 WHERE DiscountNo = '"+stringNo+"'" ;
        String[][] retM040 = dbInvoice.queryFromPool(stringSQL);
        for(int ii=0 ; ii<retM040.length ; ii++) {
          String[] m040 = retM040[ii];
          sbSQL = new StringBuilder();
          sbSQL.append("INSERT INTO GLEBPFUF ");
          sbSQL.append("(EB01U, EB02U, EB03U, EB04U, EB05U, EB06U, EB07U, EB08U, EB09U, EB10U, EB11U, EB12U, EB13U, EB14U, EB15U, EB16U, EB17U, EB18U, EB19U) ");
          sbSQL.append("values ");
          sbSQL.append("(");
          sbSQL.append("'").append(m040[0].trim()).append("', ");             //折讓號碼
          sbSQL.append("'").append(m040[1].trim()).append("', ");             //折讓日期
          sbSQL.append("'").append(m040[2].trim()).append("', ");             //公司代碼
          sbSQL.append("'").append(m040[3].trim()).append("', ");             //部門代碼
          sbSQL.append("'").append( m040[4].trim() ).append("', ");           //案別代碼
          sbSQL.append("'").append( m040[5].trim() ).append("', ");           //戶別代號
          sbSQL.append("'").append( m040[6].trim() ).append("', ");           //客戶代號
          sbSQL.append("'").append( m040[7].trim() ).append("', ");           //Invoice Way
          sbSQL.append("'").append( m040[8].trim() ).append("', ");           //新戶別
          sbSQL.append("").append(m040[9].trim()).append(", ");               //未稅
          sbSQL.append("").append(m040[10].trim()).append(", ");              //稅額
          sbSQL.append("").append(m040[11].trim()).append(", ");              //含稅
          sbSQL.append("'").append(m040[12].trim()).append("', ");            //已列印YN
          sbSQL.append("").append(m040[13].trim()).append(", ");              //補印次數
          sbSQL.append("'").append(m040[14].trim()).append("', ");            //作廢YN
          sbSQL.append("'").append(m040[15].trim()).append("', ");            //入帳YN
          sbSQL.append("'").append(m040[16].trim()).append("', ");           //修改人
          sbSQL.append("'").append(m040[17].trim()).append("', ");            //收款時間
          sbSQL.append("'").append(m040[18].trim()).append("' ");             //PROCESS DISCOUNT
          sbSQL.append(") ");
          as400.execFromPool(sbSQL.toString());
        }
        
        //System.out.println(intSale05M081+"update Invom040--------------------------"+stringSQL) ;
        stringSQL="update Invom030 set DisCountMoney=DisCountMoney+ " + (intInvoiceTotalMoney -  integerYiDiscountMoney ) +" , "  +
                                                               " DisCountTimes=DisCountTimes+1, "+
                                                   " UpdateUserNo =  '"     +  stringUserID      +  "', "  +
                                       " UpdateDateTime =  '"  +  stringDateTime +  "', "  +
                                     " LastUserNo =  '"          +  stringUserID       +  "', "  +
                                     " LastDateTime =  '"       +  stringDateTime  +  "' "   +
                          " where InvoiceNo ='" + stringInvoiceNoOld +"'"   ;             
        dbInvoice.execFromPool(stringSQL);
        //System.out.println(intSale05M081+"update Invom030--------------------------"+stringSQL) ;
        stringSQL="update SALE05M096 set DiscountTotalMoney=DiscountTotalMoney  + " + (intInvoiceTotalMoney -  integerYiDiscountMoney ) +" " +                    
                  " WHERE ProjectID1 = '" + getValue("field1").trim() + "'" +
                     " AND OrderNo = '" + getValue("field3").trim() + "'" +
                   " AND TrxDate = '" + getValue("field2").trim() + "'" +                   
                   " AND DiscountNo ='" + stringNo +"'"   ;                             
          dbSale.execFromPool(stringSQL);           
        //  System.out.println(intSale05M081+"update SALE05M096--------------------------"+stringSQL) ;
          stringSQL="INSERT INTO SALE05M061CHANGE SELECT '" + getValue("field3").trim() + "',*  FROM SALE05M061 " + 
                       " WHERE ProjectID1='" + stringProjectID1 + "' " +
                             " AND Position='" + retSale05M081[intSale05M081][2].trim() + "'";                      
         //dbSale.execFromPool(stringSQL);        
         //System.out.println(intSale05M081+"INSERT INTO SALE05M061CHANGE--------------------------"+stringSQL) ;
          stringSQL="DELETE SALE05M061 "  +
                           " WHERE ProjectID1='" + stringProjectID1 + "' " +
                                 " AND Position='" + retSale05M081[intSale05M081][2].trim() + "'";                      
          dbSale.execFromPool(stringSQL);
          //System.out.println(intSale05M081+"DELETE SALE05M061--------------------------"+stringSQL) ;
          stringSQL="INSERT INTO SALE05M060CHANGE SELECT '" + getValue("field3").trim() + "',* "  +
                            " FROM SALE05M060 " + 
                          " WHERE ProjectID1='" + stringProjectID1 + "' " +
                                " AND Position='" + retSale05M081[intSale05M081][2].trim() + "'";                     
          //dbSale.execFromPool(stringSQL); 
          //System.out.println(intSale05M081+"INSERT INTO SALE05M060CHANGE--------------------------"+stringSQL) ;
          stringSQL="DELETE SALE05M060 "  +
                           " WHERE ProjectID1='" + stringProjectID1 + "' " +
                                 " AND Position='" + retSale05M081[intSale05M081][2].trim() + "'";                      
          dbSale.execFromPool(stringSQL);                                                 
          //System.out.println(intSale05M081+"DELETE SALE05M060--------------------------"+stringSQL) ;
          intAmt = intAmt + Double.parseDouble((intInvoiceTotalMoney -  integerYiDiscountMoney )+"");
          stringCustomNoTemp=stringCustomNo;          
          intRecordNo = intRecordNo + 1;
          stringCompanyNoOld  =  stringCompanyNo ; 
      } 
      //}//End of for(int intInvoice = 1;intInvoice <=2;intInvoice++) 房屋款、土地款 
      stringSQL="update SALE05M094 set DiscountOpen ='Y',AMT= "+ intAmt + "  " +
                      " WHERE ProjectID1 ='" + getValue("field1").trim() + "'" +
                            " AND OrderNo = '" + getValue("field3").trim() + "'" +
                            " AND TrxDate = '" + getValue("field2").trim() + "'" ;
      dbSale.execFromPool(stringSQL);
      
      //取得 OrderDate
      stringSQL = "SELECT OrderDate FROM Sale05M090 WHERE OrderNo = '"+getValue("field3").trim()+"'";
      String[][] resultSale05M090Data = dbSale.queryFromPool(stringSQL);
      SimpleDateFormat sdfDiff = new SimpleDateFormat("yyyy/MM/dd");      
      Date chkOrderDate = sdfDiff.parse(resultSale05M090Data[0][0].trim());
      Date chkTrxbackDate = sdfDiff.parse(getValue("field2").trim());
      long diff7days = 7*24*60*60*1000;
      long diffTrxAndOrder = chkTrxbackDate.getTime() - chkOrderDate.getTime();
      
      //20190213 同一客戶不動產買賣契約起始日7天內，退還金額達50 萬元以上 BEGIN
      int chkAmt = 500000;
      String stringTitle = "[不動產洗錢防制通報]同一客戶不動產買賣契約起始日7天內，退還金額達50 萬元以上";
      String stringContent = "案別["+getValue("field1").trim()+"]、客戶名["+retSale05M081[0][5]+"]、交易日["+ getValue("field2").trim() +"]、退款金額["+intAmt+"]、處理人員["+getUser()+"]";
      String[] arrayUser = new String[1];
      arrayUser[0] = "davidwei_wang@fglife.com.tw";
      
      //if(diffTrxAndOrder >= 0 && diffTrxAndOrder <= diff7days && intAmt >= chkAmt) {
        try{
          if ((arrayUser.length!=0) && (!stringContent.trim().equals(""))){
            String sendRS = smtp.sendMailbccUTF8("172.16.8.115", "realty@fglife.com.tw", arrayUser, stringTitle, stringContent, null, "", "text/html");
            if (sendRS.trim().equals("")){
              System.out.println("Send mail complete !!");
            }
            else{
              System.out.println("Send fail!!");
            }
          }
        }
        catch(Exception exc1){
            System.out.println("send fail!!");
        }       
      //} 
      
      
      //20190213 同一客戶不動產買賣契約起始日7天內，退還金額達50 萬元以上 FINISH
      
      //System.out.println("Finanll--------------update SALE05M094--------------------------"+stringSQL) ;
      /*      
      String  OrderNo  =  getValue("field3").trim(); //使用編號
      String  TrxDate   =  getValue("field2").trim(); //退戶日期
      showForm("退戶-同步-行銷(Sale02M040)");
      setValue("OrderNo",OrderNo);
      setValue("TrxDate",TrxDate);
      getButton("button新增").doClick();
      getInternalFrame("退戶-同步-行銷(Sale02M040)").setVisible(false); 
      */
      setValue("OrderNo", getValue("field3").trim());
      setValue("TrxDate",getValue("field2").trim());
      
//"退戶-同步-行銷(Sale02M040)"--------------------------------
//
stringSQL = "SELECT ProjectID1," +
                 " HouseCar, " +
                 " Position " +
            " FROM Sale05M095 " +
          " WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" +
              " AND TrxDate = '" + getValue("TrxDate").trim() + "'";
String[][]  retSale05M092  =  dbSale.queryFromPool(stringSQL);
if(retSale05M092.length == 0){
    message("退戶單號 不存在!"); 
  return value;   
}
Vector      vectorSql             =  new  Vector() ;        //2015-10-16 B3018
Vector      vectorASaleID       =  new  Vector() ;        //2015-10-16 B3018
Hashtable   hashtableASaleID     =  null ;                  //2015-10-16 B3018
for(int  intSale05M092=0  ;  intSale05M092<retSale05M092.length  ;  intSale05M092++){
   stringProjectID1 = retSale05M092[intSale05M092][0].trim();
  String stringHouseCar = retSale05M092[intSale05M092][1].trim();
  String stringPosition = retSale05M092[intSale05M092][2].trim();
  String stringA_Sale = " A_Sale1";
  //
  vectorSql  =  new  Vector() ; 
  //
  stringSQL = "SELECT ID1, " +                      // 0
                  " OperatorPosition," +
                  " OperatorIndex," +
                  " HouseCar," +
                  " OperatorDom," +
                  " YearMM," +
                  " Com," +
                  " Depart," +
                  " ProjectID," +
                  " ProjectID0," +
                  " ProjectID1," +                //10
                  " H_Com," +
                  " H_LandOwner," +
                  " H_LandShare," +
                  " L_Com," +
                  " L_LandOwner," +
                  " L_LandShare," +
                  " LandOwner," +
                  " LandShare," +
                  " Position," +
                  " PositionRent," +                //20
                  " Car," +
                  " CarRent," +
                  " Custom," +
                  " OrderDate," +
                  " OrderMon," +
                  " H_OrderMon," +
                  " L_OrderMon," +
                  " EnougDate," +
                  " EnougMon," +
                  " H_EnougMon," +                //30
                  " L_EnougMon," +
                  " ContrDate," +
                  " ContrMon," +
                  " H_ContrMon," +
                  " L_ContrMon," +
                  " Deldate," +
                  " PingSu," +
                  " DealDiscount," +
                  " BonusDiscount," +
                  " PreMoney," +                    //40
                  " H_PreMoney," +
                  " L_PreMoney," +
                  " DealMoney," +
                  " H_DealMoney," +
                  " L_DealMoney," +
                  " GiftMoney," +
                  " H_GiftMoney," +
                  " L_GiftMoney," +
                  " CommMoney," +
                  " H_CommMoney," +               // 50
                  " L_CommMoney," +
                  " PureMoney," +
                  " H_PureMoney," +
                  " L_PureMoney," +
                  " LastMoney," +
                  " H_LastMoney," +
                  " L_LastMoney," +
                  " BalaMoney," +
                  " H_BalaMoney," +
                  " L_BalaMoney," +                   // 60
                  " SaleID1," +
                  " SaleName1," +
                  " SaleID2," +
                  " SaleName2," +
                  " SaleID3," +
                  " SaleName3," +
                  " SaleID4," +
                  " SaleName4," +
                  " SaleID5," +
                  " SaleName5," +                       // 70
                  " SaleGroup," +
                  " MediaID," +
                  " MediaName," +
                  " ZoneID," +
                  " ZoneName," +
                  " MajorID," +
                  " MajorName," +
                  " UseType," +
                  " Remark," +
                  " DateRange," +                     //80
                  " DateCheck," +
                  " DateFile," +
                  " DateBonus," +
                  " RentRange," +
                  " PingRentPrice," +
                  " PingRent," +
                  " PingRentLast," +
                  " RentPrice," +
                  " Rent," +
                  " RentLast," +                      // 90
                  " Guranteer," +
                  " RentFree," +
                  " Position1," +
                  " PositionRent1," +
                  " Custom1," +
                  " ViMoney," +
                  " H_ViMoney," +
                  " L_ViMoney," +
                  " AO_sn," +
                  " Plan1," +                           // 100
                  " ComNo," +
                  " OrderNo" +                          //  102
                   " , SSMediaID "  +                         // 2010-12-03 增加SSMediaID
                   " , SSMediaID1 "  +                        // 2015-05-25 增加SSMediaID1 B3018
                   " , CommMoney1 "  +                      // 105  2015-10-16 增加 CommMoney1 SB3018
                   " , H_CommMoney1 "  +                  // 106  2015-10-16 增加 H_CommMoney1 SB3018
                   " , L_CommMoney1, "  +                 // 107  2015-10-16 增加 L_CommMoney1 SB3018
                  " SaleID6," +
                  " SaleName6," +
                  " SaleID7," +
                  " SaleName7," +
                  " SaleID8," +
                  " SaleName8," +
                  " SaleID9," +
                  " SaleName9," +
                  " SaleID10," +
                  " SaleName10" +
              " FROM A_Sale " + 
            " WHERE ProjectID1 = '" + stringProjectID1 + "'";
  if(stringHouseCar.equals("House"))
    stringSQL = stringSQL + " AND HouseCar = 'Position'" +  
                          " AND Position = '" + stringPosition + "'";
  if(stringHouseCar.equals("Car"))
    stringSQL = stringSQL + " AND HouseCar = 'Car'" +   
                          " AND Car = '" + stringPosition + "'";
  String[][]  retA_Sale  =  dbSale.queryFromPool(stringSQL);
  String       stringSqlGetASale1ID  =  stringSQL ;
  if(retA_Sale.length == 0){
      message("行銷 A_Sale 棟樓別不存在!"); 
    return value;   
  }
  String stringID1 = retA_Sale[0][0];   
  String  stringID11  =  "" ;
  //
  if( retA_Sale[0][99].length() == 0) retA_Sale[0][99] = "0";
  // 售價 > 0 才 INSERT A_Sale1
  if(Float.parseFloat(retA_Sale[0][43].trim()) > 0){
     stringSQL = "INSERT INTO " + stringA_Sale +
                  " ( " +
                    " OperatorPosition," +
                    " OperatorIndex," +
                    " HouseCar," +
                    " OperatorDom," +
                    " YearMM," +
                    " Com," +
                    " Depart," +
                    " ProjectID," +
                    " ProjectID0," +
                    " ProjectID1," +
                    " H_Com," +
                    " H_LandOwner," +
                    " H_LandShare," +
                    " L_Com," +
                    " L_LandOwner," +
                    " L_LandShare," +
                    " LandOwner," +
                    " LandShare," +
                    " Position," +
                    " PositionRent," +
                    " Car," +
                    " CarRent," +
                    " Custom," +
                    " OrderDate," +
                    " OrderMon," +
                    " H_OrderMon," +
                    " L_OrderMon," +
                    " EnougDate," +
                    " EnougMon," +
                    " H_EnougMon," +
                    " L_EnougMon," +
                    " ContrDate," +
                    " ContrMon," +
                    " H_ContrMon," +
                    " L_ContrMon," +
                    " Deldate," +
                    " PingSu," +
                    " DealDiscount," +
                    " BonusDiscount," +
                    " PreMoney," +
                    " H_PreMoney," +
                    " L_PreMoney," +
                    " DealMoney," +
                    " H_DealMoney," +
                    " L_DealMoney," +
                    " GiftMoney," +
                    " H_GiftMoney," +
                    " L_GiftMoney," +
                    " CommMoney," +
                    " H_CommMoney," +
                    " L_CommMoney," +
                    " PureMoney," +
                    " H_PureMoney," +
                    " L_PureMoney," +
                    " LastMoney," +
                    " H_LastMoney," +
                    " L_LastMoney," +
                    " BalaMoney," +
                    " H_BalaMoney," +
                    " L_BalaMoney," +
                    " SaleID1," +
                    " SaleName1," +
                    " SaleID2," +
                    " SaleName2," +
                    " SaleID3," +
                    " SaleName3," +
                    " SaleID4," +
                    " SaleName4," +
                    " SaleID5," +
                    " SaleName5," +
                    " SaleGroup," +
                    " MediaID," +
                    " MediaName," +
                    " ZoneID," +
                    " ZoneName," +
                    " MajorID," +
                    " MajorName," +
                    " UseType," +
                    " Remark," +
                    " DateRange," +
                    " DateCheck," +
                    " DateFile," +
                    " DateBonus," +
                    " RentRange," +
                    " PingRentPrice," +
                    " PingRent," +
                    " PingRentLast," +
                    " RentPrice," +
                    " Rent," +
                    " RentLast," +
                    " Guranteer," +
                    " RentFree," +
                    " Position1," +
                    " PositionRent1," +
                    " Custom1," +
                    " ViMoney," +
                    " H_ViMoney," +
                    " L_ViMoney," +
                    " AO_sn," +
                    " Plan1," +
                    " ComNo," +
                    " OrderNo" +  
                     " ,SSMediaID " +  // 2010-12-03  SSMediaID 新增
                     " ,SSMediaID1 " +  // 2015-05-25  SSMediaID1 新增 B3018
                     " ,CommMoney1 " +  // 2015-10-16  CommMoney1 新增 B3018
                     " ,H_CommMoney1 " +  // 2015-10-16  CommMoney1 新增 B3018
                     " ,L_CommMoney1 " +  // 2015-10-16  CommMoney1 新增 B3018
                    " ,SaleID6 " +
                    " ,SaleName6" +
                    " ,SaleID7 " +
                    " ,SaleName7" +
                    " ,SaleID8 " +
                    " ,SaleName8" +
                    " ,SaleID9 " +
                    " ,SaleName9" +
                    " ,SaleID10 " +
                    " ,SaleName10 " +
                  " ) " +
              " VALUES " + 
                  " ( " +           
                    "'" + retA_Sale[0][1].trim() + "'," + 
                    "'" + retA_Sale[0][2].trim() + "'," +
                    "'" + retA_Sale[0][3].trim() + "'," + 
                    "'" + retA_Sale[0][4].trim() + "'," +
                    "'" + retA_Sale[0][5].trim() + "'," + 
                    "'" + retA_Sale[0][6].trim() + "'," +
                    retA_Sale[0][7] + "," + 
                    "'" + retA_Sale[0][8].trim() + "'," +
                    "'" + retA_Sale[0][9].trim() + "'," + 
                    "'" + retA_Sale[0][10].trim() + "'," +
                    "'" + retA_Sale[0][11].trim() + "'," + 
                    "'" + retA_Sale[0][12].trim() + "'," +
                    "'" + retA_Sale[0][13].trim() + "'," + 
                    "'" + retA_Sale[0][14].trim() + "'," +
                    "'" + retA_Sale[0][15].trim() + "'," + 
                    "'" + retA_Sale[0][16].trim() + "'," +
                    "'" + retA_Sale[0][17].trim() + "'," + 
                    "'" + retA_Sale[0][18].trim() + "'," +
                    "'" + retA_Sale[0][19].trim() + "'," + 
                    "'" + retA_Sale[0][20].trim() + "'," +
                    "'" + retA_Sale[0][21].trim() + "'," + 
                    "'" + retA_Sale[0][22].trim() + "'," +
                    "'" + retA_Sale[0][23].trim() + "'," + 
                    "'" + retA_Sale[0][24].trim() + "'," +
                    retA_Sale[0][25] + "," + 
                    retA_Sale[0][26] + "," +
                    retA_Sale[0][27] + "," + 
                    "'" + retA_Sale[0][28].trim() + "'," +
                    retA_Sale[0][29] + "," + 
                    retA_Sale[0][30] + "," +
                    retA_Sale[0][31] + "," + 
                    "'" + retA_Sale[0][32].trim() + "'," +
                    retA_Sale[0][33] + "," + 
                    retA_Sale[0][34] + "," +                  
                    retA_Sale[0][35] + "," +                  
                    "'" + getValue("TrxDate").trim() + "'," +                 
                    retA_Sale[0][37] + "," +                  
                    retA_Sale[0][38] + "," +                  
                    retA_Sale[0][39] + "," +
                    retA_Sale[0][40] + "," +
                    retA_Sale[0][41] + "," + 
                    retA_Sale[0][42] + "," +
                    retA_Sale[0][43] + "," + 
                    retA_Sale[0][44] + "," +                  
                    retA_Sale[0][45] + "," +                  
                    retA_Sale[0][46] + "," +                  
                    retA_Sale[0][47] + "," +                  
                    retA_Sale[0][48] + "," +                  
                    retA_Sale[0][49] + "," +
                    retA_Sale[0][50] + "," +
                    retA_Sale[0][51] + "," + 
                    retA_Sale[0][52] + "," +
                    retA_Sale[0][53] + "," + 
                    retA_Sale[0][54] + "," +                  
                    retA_Sale[0][55] + "," +                  
                    retA_Sale[0][56] + "," +                  
                    retA_Sale[0][57] + "," +                  
                    retA_Sale[0][58] + "," +                  
                    retA_Sale[0][59] + "," +                                                  
                    retA_Sale[0][60] + "," +
                    "'" + retA_Sale[0][61].trim() + "'," + 
                    "'" + retA_Sale[0][62].trim() + "'," +
                    "'" + retA_Sale[0][63].trim() + "'," + 
                    "'" + retA_Sale[0][64].trim() + "'," +                  
                    "'" + retA_Sale[0][65].trim() + "'," +                  
                    "'" + retA_Sale[0][66].trim() + "'," +                  
                    "'" + retA_Sale[0][67].trim() + "'," +                  
                    "'" + retA_Sale[0][68].trim() + "'," +                  
                    "'" + retA_Sale[0][69].trim() + "'," +                                                      
                    "'" + retA_Sale[0][70].trim() + "'," +
                    "'" + retA_Sale[0][71].trim() + "'," + 
                    "'" + retA_Sale[0][72].trim() + "'," +
                    "'" + retA_Sale[0][73].trim() + "'," + 
                    "'" + retA_Sale[0][74].trim() + "'," +                  
                    "'" + retA_Sale[0][75].trim() + "'," +                  
                    "'" + retA_Sale[0][76].trim() + "'," +                  
                    "'" + retA_Sale[0][77].trim() + "'," +                  
                    "'" + retA_Sale[0][78].trim() + "'," +                  
                    "'" + retA_Sale[0][79].trim() + "'," +                                                      
                    "'" + retA_Sale[0][80].trim() + "'," +
                    "'" + retA_Sale[0][81].trim() + "'," + 
                    "'" + retA_Sale[0][82].trim() + "'," +
                    "'" + retA_Sale[0][83].trim() + "'," + 
                    retA_Sale[0][84] + "," +                  
                    retA_Sale[0][85] + "," +                  
                    retA_Sale[0][86] + "," +                  
                    retA_Sale[0][87] + "," +                  
                    retA_Sale[0][88] + "," +                  
                    retA_Sale[0][89] + "," +                                                      
                    retA_Sale[0][90] + "," +
                    retA_Sale[0][91] + "," + 
                    retA_Sale[0][92] + "," +
                    "'" + retA_Sale[0][93].trim() + "'," + 
                    "'" + retA_Sale[0][94].trim() + "'," +                  
                    "'" + retA_Sale[0][95].trim() + "'," +                  
                    retA_Sale[0][96] + "," +                  
                    retA_Sale[0][97] + "," +                  
                    retA_Sale[0][98] + "," +                  
                    retA_Sale[0][99].trim() + "," +                                                     
                    "'" + retA_Sale[0][100].trim() + "'," +                                                                       
                    "'" + retA_Sale[0][101].trim() + "'," +                                                                       
                        "'" + getValue("OrderNo").trim() + "' " +
                       " ,'" + retA_Sale[0][103].trim() + "' " +            // 2010-12-03 新增 SSMediaID                                                            
                       " ,'" + retA_Sale[0][104].trim() + "' " +            // 2015-05-25 新增 SSMediaID1
                       " , " + retA_Sale[0][105].trim() + " " +               // 2015-10-16 B3018 新增 CommMoney1
                       " , " + retA_Sale[0][106].trim() + " " +                 // 2015-10-16 B3018 新增 H_CommMoney1
                       " , " + retA_Sale[0][107].trim() + " " +           // 2015-10-16 B3018 新增 L_CommMoney1
                     " , '" + retA_Sale[0][108].trim() + "' " +           // 2015-11-11 B3018 新增 SaleID6
                     " , '" + retA_Sale[0][109].trim() + "' " +           // 2015-11-11 B3018 新增 SaleName6
                     " , '" + retA_Sale[0][110].trim() + "' " +           // 2015-11-11 B3018 新增 SaleID7
                     " , '" + retA_Sale[0][111].trim() + "' " +           // 2015-11-11 B3018 新增 SaleName7
                     " , '" + retA_Sale[0][112].trim() + "' " +           // 2015-11-11 B3018 新增 SaleID8
                     " , '" + retA_Sale[0][113].trim() + "' " +           // 2015-11-11 B3018 新增 SaleName8
                     " , '" + retA_Sale[0][114].trim() + "' " +           // 2015-11-11 B3018 新增 SaleID9
                     " , '" + retA_Sale[0][115].trim() + "' " +           // 2015-11-11 B3018 新增 SaleName9
                     " , '" + retA_Sale[0][116].trim() + "' " +           // 2015-11-11 B3018 新增 SaleID10
                     " , '" + retA_Sale[0][117].trim() + "' " +           // 2015-11-11 B3018 新增 SaleName10
                  " ) ";
     dbSale.execFromPool(stringSQL);
     String[][]  retASale1  =  dbSale.queryFromPool(stringSqlGetASale1ID.replaceAll("A_Sale",  "A_Sale1")+" ORDER BY  ID1 DESC");
     if(retASale1.length  >  0) {
              stringID11   = retASale1[0][0];   
     }
     // 
   }
   //UPDATE A_Sale
   stringSQL = "UPDATE A_Sale " + 
               " SET YearMM = ''," +
                    " Depart = 0," +                                
                  " Custom =''," +               
                  " OrderDate =''," +              
                  " OrderMon = 0," +
                  " H_OrderMon = 0," +
                  " L_OrderMon = 0," +
                  " EnougDate =''," +              
                  " EnougMon = 0," +
                  " H_EnougMon = 0," +
                  " L_EnougMon = 0," +
                  " ContrDate =''," +              
                  " ContrMon = 0," +
                  " H_ContrMon = 0," +
                  " L_ContrMon = 0," +
                  " Deldate =''," +              
                  " PingSu = 0," +                                
                  " DealDiscount = 0," +                                
                  " BonusDiscount = 0," +                               
                  " PreMoney = 0," +                                
                  " H_PreMoney = 0," +                                
                  " L_PreMoney = 0," +                                
                  " DealMoney = 0," +                               
                  " H_DealMoney = 0," +                               
                  " L_DealMoney = 0," +                               
                  " GiftMoney = 0," +                               
                  " H_GiftMoney = 0," +                               
                  " L_GiftMoney = 0," +                               
                  " CommMoney = 0," +                               
                  " H_CommMoney = 0," +                               
                  " L_CommMoney = 0," +                               
                  " CommMoney1 = 0," +          // 2015-10-16 B3018
                  " H_CommMoney1 = 0," +       // 2015-10-16 B3018
                  " L_CommMoney1 = 0," +        // 2015-10-16 B3018
                  " PureMoney = 0," +                               
                  " H_PureMoney = 0," +                               
                  " L_PureMoney = 0," +                               
                  " LastMoney = 0," +                               
                  " H_LastMoney = 0," +                               
                  " L_LastMoney = 0," +                               
                  " BalaMoney = 0," +                               
                  " H_BalaMoney = 0," +                               
                  " L_BalaMoney = 0," +                               
                  " SaleID1 =''," +              
                  " SaleName1 =''," +              
                  " SaleID2 =''," +              
                  " SaleName2 =''," +              
                  " SaleID3 =''," +              
                  " SaleName3 =''," +              
                  " SaleID4 =''," +              
                  " SaleName4 =''," +              
                  " SaleID5 =''," +          
                  " SaleName5 =''," +  
                  " SaleID6 =''," +          
                  " SaleName6 =''," +  
                  " SaleID7 =''," +          
                  " SaleName7 =''," +  
                  " SaleID8 =''," +          
                  " SaleName8 =''," +  
                  " SaleID9 =''," +          
                  " SaleName9 =''," +  
                  " SaleID10 =''," +           
                  " SaleName10 =''," +                    
                  " SaleGroup =''," +              
                  " MediaID =''," +              
                  " MediaName =''," +              
                  " ZoneID =''," +               
                  " ZoneName =''," +               
                  " MajorID =''," +              
                  " MajorName =''," +              
                  " UseType =''," +              
                  " Remark =''," +               
                  " DateRange =''," +              
                  " DateCheck =''," +              
                  " DateFile =''," +               
                  " DateBonus =''," +              
                  " RentRange = 0," +                               
                  " PingRentPrice = 0," +                               
                  " PingRent = 0," +                                
                  " PingRentLast = 0," +                                
                  " RentPrice = 0," +                               
                  " Rent = 0," +                                
                  " RentLast = 0," +                                
                  " Guranteer = 0," +                               
                  " RentFree = 0," +                                
                  " Position1 =''," +              
                  " PositionRent1 =''," +              
                  " Custom1 =''," +              
                  " ViMoney = 0," +                               
                  " H_ViMoney = 0," +                               
                  " L_ViMoney = 0," +                               
                  " AO_sn = 0," +                               
                  " Plan1 =''," +              
                  " ComNo =''," +              
                  " OrderNo =''" +
                   " , SSMediaID ='' " +  // 2010-12-03 新增 
                   " , SSMediaID1 ='' " +  // 2015-05-25 新增 
                    " WHERE ID1 = " +  stringID1;
   //dbSale.execFromPool(stringSQL);
   vectorSql.add(stringSQL) ;
   // 2015-10-16 B3018  START
   // 查詢
   vectorASaleID  =  exeUtil.getQueryDataHashtable("A_Sale_SaleID",  new  Hashtable(),  " AND ID1 = " +  stringID1 +" ",  dbSale) ;
   // 刪除
   stringSQL  =  exeUtil.doDeleteDB("A_Sale_SaleID",  new  Hashtable(),  " AND ID1 = " +  stringID1 +" ",  false,  dbSale) ;
   vectorSql.add(stringSQL) ;
   // 新增
   for(int  intNoL=0  ;  intNoL<vectorASaleID.size()  ;  intNoL++) {
      hashtableASaleID  = (Hashtable)  vectorASaleID.get(intNoL) ;  if(hashtableASaleID  ==  null)  continue ;
      //
      hashtableASaleID.put("ID1",  stringID11) ;
      //
      if(!"".equals(stringID11)) {
          stringSQL  =  exeUtil.doInsertDB("A_Sale1_SaleID",  hashtableASaleID,  false,  dbSale) ;
          vectorSql.add(stringSQL) ;
      }
   }
   dbSale.execFromPool((String[])  vectorSql.toArray(new  String[0])) ;
   // 2015-10-16 B3018  END
}
// 異動資料庫
//dbSale.execFromPool((String[]) vectorSql.toArray(new  String[0])) ;     // 2015-10-16 B3018
message("OK!");
//"退戶-同步-行銷(Sale02M040)"--------------------------------
      
      /* 測試 START
      // 釋放 Excel 物件
      exeFun.getReleaseExcelObject(retVector) ;
      // 測試 END*/
      JOptionPane.showMessageDialog(null,  stringInvoiceMessage,"退戶折讓單 開立成功! 產生折讓單訊息", JOptionPane.INFORMATION_MESSAGE); 
      action(2);
      //showForm("收款單(Sale05M080)");
      //setValue("FlowStatus",stringFlowStatus);
      //getButton("button3").setLabel("簽核流程:[" + stringFlowStatus + "]");
      return value;
  }
  public  void  doShowDataInExcel(int  intcol,  int  intRow,  Dispatch  objectSheet1,  Farglory.Excel.FargloryExcel  exeExcel,  String[]  arrayFiled,  String[] retData) {
    Dispatch  objectClick    =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,  new Object[] {"B"+intRow},  new int[1]).toDispatch();
    Dispatch.call(objectClick,  "Select");
    for(int  intNo=0  ;  intNo<retData.length  ;  intNo++) {
      exeExcel.putDataIntoExcel(intcol+intNo,  intRow,  arrayFiled[intNo]+"："+retData[intNo],       objectSheet1) ;
    }
  }
  public  void  doMail(String  stringSubject,  String  stringContent,  String  stringSend,  String[]  arrayUser) {
      try{
          String  sendRS = sendMailbcc("mismail2", stringSend, arrayUser, stringSubject, stringContent, null, "", "text/html");
          if (sendRS.trim().equals("")){
              System.out.println("Send mail complete !!");
          }else{
              System.out.println("Send fail!!"+sendRS);
          }
      }catch(Exception exc1){
          System.out.println("send fail!!");
      }
  }
  public String getInformation(){
    return "---------------button2(button2).defaultValue()----------------";
  }
}
