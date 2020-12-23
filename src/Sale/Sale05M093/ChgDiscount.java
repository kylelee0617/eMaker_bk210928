import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.util.KUtils;

public class ChgDiscount extends bproc{
  KUtils util = new KUtils();
  
  public String getDefaultValue(String value)throws Throwable{
    //201808check BEGIN
        System.out.println("chk==>"+getUser()+" , value==>換戶開折讓");
          if(getUser() != null && getUser().toUpperCase().equals("B9999")) {
            messagebox("換戶開折讓權限不允許!!!");
            return value;
        }
        //201808check FINISH  
        //換名產生發票  111
        talk  dbSale  =  getTalk((String)get("put_dbSale")) ;//SQL2000
        //talk dbFED1 = getTalk(""+get("put_dbFED1"));
        talk dbInvoice = getTalk(""+get("put_dbInvoice"));
        String stringSQL                   =  "";
        String stringInvoiceMessage =  "";
        String stringCompanyNo       =  "";
        String stringDocNo                =  "";
        String stringDateTime           =  datetime.getTime("YYYY/mm/dd h:m:s") ;
        String stringUserID                =  getUser() ;
        String stringHuBeiCond ="HuBei in (";
        stringSQL = " SELECT Position  FROM Sale05M092" +             
                  " WHERE OrderNo = '" + getValue("OrderNo").trim() + "' " +
                  "  AND ISNULL(StatusCd,'')<>'D' " ;
        String retSale05M092[][] = dbSale.queryFromPool(stringSQL);
        for(int i = 0 ;i < retSale05M092.length;i++){
          if (i==0){
            stringHuBeiCond=stringHuBeiCond+"'" +retSale05M092[i][0].trim() + "'";
          }else {
            stringHuBeiCond=stringHuBeiCond+",'" +retSale05M092[i][0].trim() + "'";
          }     
        }
        stringHuBeiCond=stringHuBeiCond+")";    
        String stringCustomNo="";
        /*String stringCustomNoNew="";
        stringSQL = " SELECT  CustomNo,CustomNoNew  " + 
                    " FROM  Sale05M093 "  +
                  " WHERE  OrderNo  =  '"  +  getValue("OrderNo").trim( ) +  "' "  + //換名日期
                        " AND  TrxDate  =  '"   +  getValue("TrxDate").trim( )   +  "' " ;
        */
        stringSQL="SELECT  CustomNo " +
                          " FROM  Sale05M091 "   +
                " WHERE  OrderNo  =  '"  +  getValue("OrderNo").trim( ) +  "' "  + 
                        " AND  isnull(TrxDate,'')  =  '"   +  getValue("TrxDate").trim( )   +  "' " ;         
        String retSale05M093[][] = dbSale.queryFromPool(stringSQL);
        int               intInvoiceCount       =  0 ;
        int               intAvailableInvoice  =  0 ;
        int               intCount                  =  0 ;
        String         stringDepartNo       =  "" ;
        String         stringProjectID1      =  "" ;
        String         stringEDate            =  "" ;
        String         stringYM                 =  "" ;
        String         stringSql                 =  "" ;
        String         stringKey                =  "" ;
        String         stringCount             =  "" ;
        String[]       arrayInvoiceKind     =  {"2",  "3"} ;
        String[][]     retSale05M080       =  null ;
        String[][]     retInvom030            =  null ;
        String[][]     retInvoM022            =  null ;
        String[][]     retSale05M081       =  null ;  //
        Vector        vectorCompanyNo  =  new  Vector( ) ;
        Hashtable  hashtableCount      =  new  Hashtable( ) ;
        //Temp 
        stringSql   = " SELECT  T80.CompanyNo,  T80.DepartNo,  T80.ProjectID1,  T93.TrxDate,  T80.DocNo "  +
                    " FROM  Sale05M086 T86,  SALE05M093 T93,  Sale05M080 T80 "  +
                  " WHERE  T93.OrderNo  =  '" + getValue("OrderNo").trim() + "' " +
                        " AND  T86.OrderNo  =  T93.OrderNo" +
                        " AND  T80.DocNo  =  T86.DocNo ";
        retSale05M080  =  dbSale.queryFromPool(stringSql);
        for(int intSale05M080 = 0 ;intSale05M080 < retSale05M080.length;intSale05M080++){
            //stringCompanyNo  =  retSale05M080[intSale05M080][0].trim( ) ;
            stringDepartNo      =  retSale05M080[intSale05M080][1].trim( ) ;
            stringProjectID1     =  retSale05M080[intSale05M080][2].trim( ) ;       
            stringEDate           =  retSale05M080[intSale05M080][3].trim( ) ;  
            stringYM                =  (Integer.parseInt(stringEDate.substring(0,4))-1911)  +  stringEDate.substring(5,7) ;
                stringDocNo          =  retSale05M080[intSale05M080][4].trim( ) ; 
        }
        if (retSale05M093.length>0){
          //stringCustomNoNew=retSale05M093[0][1];
        }else{
          message("無換名資料");
          return value;
        }
        for(int i = 0 ;i < retSale05M093.length;i++){
          stringCustomNo=retSale05M093[i][0];
          //stringCustomNoNew=retSale05M093[i][1];
          String [][] tb2_string=getTableData("table2"); // 無使用
            //
          String stringInvoiceYYYYMM = "";
          String stringFSChar = "";
          String stringStartNo = "";
          String stringInvoiceBook = "";
          String stringInvoiceStartNo = "";
          String stringInvoiceEndNo = "";
          String stringMaxInvoiceNo = "";
          String stringInvoiceNo = "";
          String stringEndYes = "";
          int intInvoiceNo = 0;
          String stringDeptCd="";
          if (stringProjectID1.equals("H45A"))
            stringDeptCd= " and (ProjectNo='H45A' or ProjectNo='H45T') " ;
          else
            stringDeptCd= " and ProjectNo='" + stringProjectID1 +"' " ;
          
          stringSQL = " SELECT InvoiceNo,DisCountMoney,HuBei,PointNo,InvoiceTotalMoney,CompanyNo,InvoiceKind,ProjectNo "+
                      " FROM Invom030 " +   
                      " where "  + stringHuBeiCond + stringDeptCd +
                      //" and ProjectNo='" + stringProjectID1 +"' " + 
                      " and CustomNo='" + stringCustomNo+"'   "+
                      //" and CompanyNo ='" + stringCompanyNo +"' " +   //941215            
                      " and (InvoiceTotalMoney-DisCountMoney) >0"+      
                      " AND DELYes='N' " + 
                      " order by CompanyNo,InvoiceKind,PointNo";                
          retSale05M081 = dbInvoice.queryFromPool(stringSQL);
          if (retSale05M081.length == 0){
              message("無發票資料!");
              return value;   
          }         
          int        intRecordNo          = 1;//for Sale05M081  
          String  stringHuBeiTemp  =  "" ;
          String  stringNo                =  "";
          int        intNo                     =  0;
          for(int intSale05M081 = 0 ;intSale05M081 < retSale05M081.length;intSale05M081++){
                  String stringInvoiceNoOld = retSale05M081[intSale05M081][0].trim();
                  stringSQL= " select InvoiceTotalMoney  " +
                                       " from Invom030 where ProjectNo='" + retSale05M081[intSale05M081][7] + "' " +
                                        " and InvoiceNo='" + stringInvoiceNoOld + "'" ;                           
                  retInvom030 = dbInvoice.queryFromPool(stringSQL);         
                  
                  //String stringHuBei=retSale05M081[intSale05M081][2];         
                  String stringHuBei=retSale05M081[intSale05M081][5]+retSale05M081[intSale05M081][6];         
                  System.out.println("stringHuBeiTemp:"+stringHuBeiTemp);
                  System.out.println("stringHuBei:"+stringHuBei);
                  System.out.println("intSale05M081:"+intSale05M081);
                  if ((!stringHuBeiTemp.equals(stringHuBei) && !stringHuBei.equals("")) || (intRecordNo-1)  %  8  ==  0){           
                    intRecordNo  =  1 ;
                    String  stringTemp   =  "";
                    String  stringmaxNo  =  "";
                    
                    for(int j =(5-(stringTemp+stringDepartNo).length())  ;j>0 ;j--){
                       stringTemp="0"+stringTemp;
                    }
                    stringSQL="select MAX(DiscountNo) DiscountNo from InvoM040 "+                     
                             " WHERE CompanyNo = '" + retSale05M081[intSale05M081][5] + "' " +
                                       " AND DepartNo = '" + stringDepartNo + "'" +
                                       " AND SUBSTRING(DiscountDate,1,7) = SUBSTRING('"+ stringEDate +"',1,7) ";                    
                    String retInvoM040[][] = dbInvoice.queryFromPool(stringSQL);
                    System.out.println("retInvoM040[0][0]"+retInvoM040[0][0]);
                    System.out.println("retInvoM040[0][0].length"+retInvoM040[0][0].length());
                    if (retInvoM040[0][0].length()>0){    
                       stringmaxNo=retInvoM040[0][0];
                       stringmaxNo = "000" + (Integer.parseInt(stringmaxNo.substring(stringmaxNo.length()-3))+1);
                       stringmaxNo = stringmaxNo.substring(stringmaxNo.length()-3); 
                       stringNo=retSale05M081[intSale05M081][5]+stringTemp+stringDepartNo+stringYM+stringmaxNo;         
                    }else{
                       stringNo= retSale05M081[intSale05M081][5]+stringTemp+stringDepartNo+stringYM+"001";
                    }  
                    stringInvoiceMessage  +=  stringNo  +  "\n" ;
                    stringSQL="INSERT INTO Invom040 " +
                              "select '" + stringNo + "','" + stringEDate +"', CompanyNo,"  +
                              "'" + stringDepartNo+ "',ProjectNo, '',"  +
                              "CustomNo,'A', '' ,0,"+
                              "0 ,0, 'N' ,0 ,'N' ,"+
                              "'N' ,'"+ getUser() +"', '" +   datetime.getTime("YYYY/mm/dd h:m:s")  +"', '1', '收款.換名' , '' , '' " + 
                              " from Invom030 where ProjectNo='" + retSale05M081[intSale05M081][7] + "'" +
                              " AND InvoiceNo='" + stringInvoiceNoOld + "'" ;                           
                    dbInvoice.execFromPool(stringSQL);      
                    System.out.println("stringSQL"+stringSQL);
                    
                    stringSQL="INSERT INTO SALE05M098 values( " +
                              "'" + getValue("ProjectID1").trim() + "','" + getValue("OrderNo").trim() +"', " +
                              "'" + getValue("TrxDate").trim() +"','" + stringNo + "',0)"   ;
                    dbSale.execFromPool(stringSQL);                           
                    System.out.println("stringSQL"+stringSQL);          
                    intNo=0;
                    
                  }         
                  int integerYiDiscountMoney =Integer.parseInt(retSale05M081[intSale05M081][1]);
                  double intInvoiceTotalMoney =Double.parseDouble(retSale05M081[intSale05M081][4]);
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
                  String stringTaxRate = "";
                  String stringTaxKind = "";    
                  double intInvoiceMoney=0;
                  double intInvoiceTax=0;
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
                            " from Invom030 where ProjectNo='" + retSale05M081[intSale05M081][7] + "'" +
                            " AND InvoiceNo='" + stringInvoiceNoOld + "'" ;                           
                  dbInvoice.execFromPool(stringSQL);
                  System.out.println("stringSQL"+stringSQL);
                  
                  //AS400 GLECPFUF 折讓明細
                  talk as400 = getTalk("400CRM");
                  String GENLIB = ((Map)get("config")).get("GENLIB").toString().trim();
                  
                  StringBuilder sbSQL = new StringBuilder();
                  stringSQL = "select * FROM Invom041 WHERE DiscountNo = '"+stringNo+"'" ;
                  String[][] retM041 = dbInvoice.queryFromPool(stringSQL);
                  for(int ii=0 ; ii<retM041.length ; ii++) {
                    String[] m041 = retM041[ii];
                    sbSQL = new StringBuilder();
                    sbSQL.append("INSERT INTO "+GENLIB+".GLECPFUF ");
                    sbSQL.append("(EC01U, EC02U, EC03U, EC04U, EC05U, EC06U, EC07U, EC08U, EC09U, EC10U) ");
                    sbSQL.append("values ");
                    sbSQL.append("(");
                    sbSQL.append("'").append( m041[0].trim() ).append("', ");             //折讓號碼
                    sbSQL.append("").append( m041[1].trim() ).append(", ");               //筆數
                    sbSQL.append("'").append( m041[2].trim() ).append("', ");             //勾選
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
                  " DiscountTax= DiscountTax+ " + intInvoiceTax + ",DiscountTotalMoney=DiscountTotalMoney + " + (intInvoiceTotalMoney -  integerYiDiscountMoney ) +" "+
                  " where DiscountNo ='" + stringNo +"'"    ;             
                  dbInvoice.execFromPool(stringSQL);
                  System.out.println("stringSQL"+stringSQL);
                  
                  //AS400 GLEBPFUF 折讓主檔
                  stringSQL = "select * FROM Invom040 WHERE DiscountNo = '"+stringNo+"'" ;
                  String[][] retM040 = dbInvoice.queryFromPool(stringSQL);
                  for(int ii=0 ; ii<retM040.length ; ii++) {
                    String[] m040 = retM040[ii];
                    sbSQL = new StringBuilder();
                    sbSQL.append("INSERT INTO "+GENLIB+".GLEBPFUF ");
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
                  
                  stringSQL="update Invom030 set DisCountMoney=DisCountMoney+ " + (intInvoiceTotalMoney -  integerYiDiscountMoney ) +" ,DisCountTimes=DisCountTimes+1, "+
                                                              " UpdateUserNo =  '"     +  stringUserID      +  "', "  +
                                              " UpdateDateTime =  '"  +  stringDateTime +  "', "  +
                                              " LastUserNo =  '"          +  stringUserID       +  "', "  +
                                              " LastDateTime =  '"       +  stringDateTime  +  "' "   +
                                    " where InvoiceNo ='" + stringInvoiceNoOld +"'"   ;             
                  dbInvoice.execFromPool(stringSQL);
                  System.out.println("stringSQL"+stringSQL);
                  
                  stringSQL="update SALE05M098 set DiscountTotalMoney=DiscountTotalMoney  + " + (intInvoiceTotalMoney -  integerYiDiscountMoney ) +" " +                    
                            " WHERE ProjectID1 = '" + getValue("ProjectID1").trim() + "'" +
                            " AND OrderNo = '" + getValue("OrderNo").trim() + "'" +
                            " AND TrxDate = '" + getValue("TrxDate").trim() + "'" +                   
                            " AND DiscountNo ='" + stringNo +"'"    ;                             
                  dbSale.execFromPool(stringSQL);   
                  System.out.println("stringSQL"+stringSQL);                            
                            
                  stringHuBeiTemp=retSale05M081[intSale05M081][5].trim()+retSale05M081[intSale05M081][6].trim(); //stringHuBei;                 
                  intRecordNo = intRecordNo + 1;                    
                  
                  stringSql="select OrderNo from SALE05M061CHANGE  " + 
                               " WHERE  OrderNo = '" + getValue("OrderNo").trim() + "' " +
                                    " AND ProjectID1='" + stringProjectID1 + "'  AND Position='" + retSale05M081[intSale05M081][2].trim() +"'  " ;          
                  String retSale05M061[][]  =  dbSale.queryFromPool(stringSql);               
                  if (retSale05M061.length<=0){
                    stringSQL="INSERT INTO SALE05M061CHANGE SELECT '" + getValue("OrderNo").trim() + "',*  FROM SALE05M061 " + 
                                             " WHERE ProjectID1='" + stringProjectID1 + "' " +
                                                   " AND Position='" + retSale05M081[intSale05M081][2].trim() +"'";                     
                    //dbSale.execFromPool(stringSQL); 
                  }
                  System.out.println("end"+stringSql);    
                  stringSQL="update Sale05M061 set H_ReceiveMoney=0,H_MomentaryMoney=0,L_ReceiveMoney=0, " + 
                                                                             " L_MomentaryMoney=0,HL_ReceiveMoney=0,HL_MomentaryMoney=0 " + 
                                   " where ProjectID1='" + stringProjectID1 +"'  and Position='" + retSale05M081[intSale05M081][2].trim() +"'";
                    dbSale.execFromPool(stringSQL);   
            } 
          }//End of for(int intInvoice = 1;intInvoice <=2;intInvoice++) 房屋款、土地款       
            
            stringSQL="update SALE05M093 set DiscountOpen ='Y' " +
                            " WHERE ProjectID1 ='" + getValue("ProjectID1").trim() + "'" +
                                 " AND OrderNo = '" + getValue("OrderNo").trim() + "'" +
                                 " AND TrxDate = '" + getValue("TrxDate").trim() + "'" ;
            dbSale.execFromPool(stringSQL);   
            System.out.println("stringSQL"+stringSQL);  
        //--------------------------
          stringSQL = "";
        //
        stringSQL = "SELECT ProjectID1 " +
                    " FROM Sale05M090 " +
                  " WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" ;
        String[][]  retSale05M090 = dbSale.queryFromPool(stringSQL);
        if(retSale05M090.length == 0){
            message("購屋証明單:" + getValue("OrderNo").trim() + " 不存在!"); 
          return value;   
        }
         stringProjectID1 = "";
        stringProjectID1 = retSale05M090[0][0].trim();
        //
        stringSQL = "SELECT CustomName " +
                    " FROM Sale05M091 " +
                  " WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" +
                        " AND ISNULL(StatusCd,'')<>'C' " +          
                   " ORDER BY RecordNo ";
        String[][]  retSale05M091 = dbSale.queryFromPool(stringSQL);          
        if(retSale05M091.length == 0){
            message("購屋証明單:" + getValue("OrderNo").trim() + " 客戶(Sale05M091) 不存在!");  
          return value;   
        }
        String stringCustom = "";
        for(int  intSale05M091=0  ;  intSale05M091<retSale05M091.length  ;  intSale05M091++){
          if(intSale05M091==0)  stringCustom = retSale05M091[intSale05M091][0];
          else stringCustom = "-" + stringCustom + retSale05M091[intSale05M091][0];
        }
        //stringCustom = retSale05M091[0][0].trim();
        //
        stringSQL = "SELECT HouseCar, " +
                         " Position " +
                    " FROM Sale05M092 " +
                  " WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" +
                        " AND ISNULL(StatusCd,'')<>'D' " +
                  " ORDER BY HouseCar DESC,RecordNo" ;
        retSale05M092  =  dbSale.queryFromPool(stringSQL);
        String stringPosition1 = "";
        for(int  intSale05M092=0  ;  intSale05M092<retSale05M092.length  ;  intSale05M092++){
          String stringHouseCar = retSale05M092[intSale05M092][0].trim();
          String stringPosition = retSale05M092[intSale05M092][1].trim();
          //車位連結戶別
          if (stringHouseCar.equals("House")) stringPosition1 = stringPosition;
          //
          stringSQL = "SELECT ID1 " +
                           " FROM A_Sale " + 
                    " WHERE ProjectID1 = '" + stringProjectID1 + "'";
          if(stringHouseCar.equals("House"))
            stringSQL = stringSQL + " AND HouseCar = 'Position'" +  
                                  " AND Position = '" + stringPosition + "'";
          if(stringHouseCar.equals("Car"))
            stringSQL = stringSQL + " AND HouseCar = 'Car'" +   
                                  " AND Car = '" + stringPosition + "'";
          String[][]  retA_Sale  =  dbSale.queryFromPool(stringSQL);
          if(retA_Sale.length == 0){
              message("行銷 A_Sale 棟樓別不存在!"); 
            return value;   
          }
          String stringID1 = retA_Sale[0][0];
          //UPDATE A_Sale
           stringSQL = "UPDATE A_Sale " + 
                       " SET  Custom = '" + stringCustom + "'"; 
          if(stringHouseCar.equals("Car")){
            stringSQL = stringSQL + ", Custom1 = '" +   stringCustom + "'";
          }
          stringSQL = stringSQL + 
                    " WHERE ID1 = " +  stringID1;
            dbSale.execFromPool(stringSQL);
        }
        message("OK!");
        //--------------------------        
            JOptionPane.showMessageDialog(null,  stringInvoiceMessage,"折讓 開立成功!", JOptionPane.INFORMATION_MESSAGE); 
            action(2);
        return value;
  }
  public String getInformation(){
    return "---------------button3(\u7522\u6536\u767c\u7968).defaultValue()----------------";
  }
}
