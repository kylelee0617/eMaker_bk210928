package  Sale.Sale05M080.Sign ;
import      javax.swing.*;
import      jcx.jform.bproc;
import      java.io.*;
import      java.util.*;
import      jcx.util.*;
import      jcx.html.*;
import      jcx.db.*;
import Farglory.util.KUtils;

/**
 * 行銷發票在這開
 * 
 * @author B04391
 *
 */

public  class  SignSave  extends  bproc {
    talk  dbSale    =  getTalk(""+get("put_dbSale"));
    talk dbInvoice = getTalk(""+get("put_dbInvoice"));
    talk dbTestAs400 = getTalk("test400CRM");
    talk dbAs400 = getTalk("400CRM");
    String GENLIB = ((Map)get("config")).get("GENLIB").toString().trim();
    KUtils kUtil = new KUtils();
    
    //全域mapCustomers
    Map mapCustomers = new HashMap();
    
    public  String  getDefaultValue(String  value) throws  Throwable {
        
        //檢查環境
        String serverIP = get("serverIP").toString().trim();
        String serverKind = serverIP.contains("172.16.14.4")? "prod":"test";
      
        //setValue("FlowStatus",  "業管-審核") ;
        //收款編號:0151M51A950620001
        //setValue("DocNo",  "0151M51A950620001") ;
        // 資料處理
        int               intInvoiceNo                             =  0 ;
        int               intAvailableInvoice                   =  0 ;
        int               intCustomPos                          =  0 ;
        String         stringFlowStatus                      =  getValue("FlowStatus") ;
        String         stringInvoiceNo1                      =  "" ;
        String         stringDocNo                             =  getValue("DocNo").trim( ) ;
        String         stringCompanyNo                    =  "" ;
        String         stringCompanyCd                    =  "" ;
        String         stringDepartNo                        =  "" ;
        String         stringProjectID1                       =  "" ;
        String         stringEDate                             =  "" ;
        String         stringInvoiceNo                       =  "" ;
        String         stringEndYes                          =  "" ;
        String         stringCustomNo                      =  "" ;
        String         stringInvoiceKind                    =  "" ; 
        String         stringPercentage                    =  "" ;
        String         stringOrderNo                         =  "" ;
        String         stringCustomPos                    =  "" ;
        String         stringSUMReceiveCheck        =  "" ;
        String         stringSUMReceiveCheckUse  =  "" ;
        String         stringSUMDiscountCheck       =  "" ;// 折讓 Add
        String         stringKey                                =  "" ;
        String         stringITEMLS_CD                   =  "" ;
        String         stringORDER_NO                   =  "" ;
        String         stringInvoiceTotalMoney         =  "" ;
        String         stringPosition                          =  "" ;
        String         stringL_DiscountMoney          =  "" ;
        String         stringPointNo                          =  "" ;
        String[]       arrayPointNo                           =  {"2101",  "2102",  "2103",  "2104"} ;
        String[][]     retSale05M040                        =  null ;
        String[][]     retSale05M080                        =  null ;
        String[][]     retSale05M081                        =  null ;
        String[][]     retSale05M084P                      =  null ;
        String[][]     retSale05M084                        =  null ;
        String[][]     retSale05M085                        =  null ;
        String[][]     retSale05M085UPDATE          =  null ;
        String[][]     retSale05M086                        =  null ;
          String[][]     retInvoM022                            =  null ;
        String[][]     retInvoM010                            =  null ;
        float            floatSUMReceive                     =  0 ;
        double       doubleSUMReceive                  =  0 ;
        Hashtable  hashtableOrderNo                   =  new  Hashtable( ) ;
        Hashtable  hashtableSUMReceive             =  new  Hashtable( ) ;
        Hashtable  hashtableData                          =  new  Hashtable( ) ;
        Hashtable  hashtableTaxKind                     =  new  Hashtable( ) ;
        Vector        vectorSale05M061                   =  new  Vector( ) ;
        Vector        vectorSale05M061Sql              =  new  Vector( ) ;
        Vector        vectorRet                                 =  new  Vector( ) ;
        Vector        vectorCompanyCd                   =  null ;
        Vector        vectorPosition                          =  null ;
        Vector        vectorUniquePosition               =  new  Vector( ) ;
        Hashtable  hashtablePosition                     =  new  Hashtable() ;
        //
        for(int  intNo=0  ;  intNo<arrayPointNo.length  ;  intNo++) {
            stringPointNo    =  arrayPointNo[intNo] ;
            //  0  TaxRate   1  TaxKind
             retInvoM010  =  getInvoM010(stringPointNo) ;
             if(retInvoM010.length  ==  0) {
                  message("查無對應的稅別種類。") ;
                return  value ;
             } else {
                  hashtableTaxKind.put(stringPointNo,  retInvoM010[0][1].trim( )) ;
             }
        }
        //  2802
        //  0  TaxRate   1  TaxKind
         retInvoM010  =  getInvoM010("2802") ;
         if(retInvoM010.length  ==  0) {
            message("查無對應的稅別種類。") ;
            return  value ;
         } else {
            hashtableTaxKind.put("2802",  retInvoM010[0][1].trim( )) ;
         }
        //
        // doInsert_Sale05M080_Flow_HIS( ) ;  // 歷史檔記錄(可多筆)  Primary Key 為 DocNo、FlowStatus、EDateTime。
        // doInsert_Sale05M080_Flow( ) ;         // 歷史檔記錄               Primary Key 為 DocNo、FlowStatus、EDateTime。
        // 更新狀態 FlowStatus (業管-審核、經辦)  Primary Key 為 DocNo。
        // doUpdate_Sale05M080( ) ;
        //
        getInternalFrame("收款單-簽核").setVisible(false);
        //
        if(stringFlowStatus.equals("業管-審核")){
            // 檢核(發票是否開立) Sale05M087：DocNo、RecordNo
            stringInvoiceNo1  =  isCheckSale05M087Exist( ) ;
            if(!"".equals(stringInvoiceNo1)) {
                message(stringInvoiceNo1  +  "發票已開立！") ;
                return  value ;
            }
            //Temp 
            //  0  CompanyNo      1  DepartNo     2  ProjectID1       3  EDate
            retSale05M080  =  getSale05M080Tmp( ) ; // 回傳應僅一筆 或 無
            for(int  intSale05M080=0  ;  intSale05M080<retSale05M080.length  ;  intSale05M080++) {
                stringCompanyNo  =  retSale05M080[intSale05M080][0].trim( ) ;
                stringDepartNo      =  retSale05M080[intSale05M080][1].trim( ) ;
                stringProjectID1     =  retSale05M080[intSale05M080][2].trim( ) ;
                stringEDate            =  retSale05M080[intSale05M080][3].trim( ) ; //改用發票日期唷   
            }
            System.out.println(stringCompanyNo  +  "-----------------------"+
                             stringDepartNo      +  "-----------------------"+
                             stringProjectID1    +  "-----------------------"+
                             stringEDate) ;
            
            //檢核發票日期不可小於兩天前 : by Kyle
            String today = datetime.getToday("YYYY/mm/dd");
            long subDays = kUtil.subACDaysRDay(today, stringEDate);
            if( "prod".equals(serverKind) ) {
              if(subDays > 2) {
                messagebox("發票日期不可小於兩天前。。。 崩╰(〒皿〒)╯潰 ");
                return value;
              }
            }
            
            // 取得公司
            vectorRet                    =  getCompanys(stringDocNo,  stringProjectID1) ;
            vectorCompanyCd      =  (Vector)    vectorRet.get(0) ;
            vectorPosition             =  (Vector)    vectorRet.get(1) ;
            retSale05M040           =  (String[][])  vectorRet.get(2) ;
            vectorUniquePosition  =  (Vector)     vectorRet.get(3) ;
            String    stringStatus  =  (String)      vectorRet.get(4) ;
            if(vectorCompanyCd ==  null  ||  vectorCompanyCd.size( )  ==  0  ||  "NULL".equals(stringStatus)) {
                message("無相對應之公司資料。") ;
                return  value ;
            }
            //  0  CustomNo    回傳多筆
            Vector  vectorInvoiceKind  =  new  Vector( ) ;
            String   stringNationality     =  "" ;
            retSale05M084P  =  getSale05M084( ) ;  /* Primary Key 為 DocNo、CustomNo */
            for(int  intSale05M084=0  ;  intSale05M084<retSale05M084P.length  ;  intSale05M084++) {
                stringCustomNo = retSale05M084P[intSale05M084][0].trim( ) ;
                stringNationality = retSale05M084P[intSale05M084][1].trim( ) ;
                //
                stringInvoiceKind     =  (stringCustomNo.length( ) == 8)  ?  "3"  :  "2" ;
                if("2".equals(stringNationality))  stringInvoiceKind  =  "2" ;
                if("4".equals(stringNationality))  stringInvoiceKind  =  "2" ;      // 20100401 新增
                if(vectorInvoiceKind.indexOf(stringInvoiceKind)  ==  -1) {
                    vectorInvoiceKind.add(stringInvoiceKind) ;
                }
            }
            
            //此收款單下所有客戶
            mapCustomers = this.getSale05M091Users();
            
            //
            Hashtable  hashtableAvailableInvoice     =  new  Hashtable( ) ;/* 可用的發票數(公司別 + 發票種類(3 or 2)) ─ 最後比對發票數時，要使用。*/
            //保護發票控管檔
            doDeleteSale05M085( ) ; 
            for(int  intCompany=0  ;  intCompany<vectorCompanyCd.size()  ;  intCompany++) {
                stringCompanyCd  =  ""  +  (String)  vectorCompanyCd.get(intCompany) ;
                //System.out.println(intCompany  +  "(保護發票控管檔)--------------------------------"  +  stringCompanyCd) ;
                for(int  intNo=0  ;  intNo<vectorInvoiceKind.size( )  ;  intNo++) {
                    stringInvoiceKind  =  (String)  vectorInvoiceKind.get(intNo) ;
                    //發票號碼自動產生
                    //  0  InvoiceYYYYMM    1  FSChar                2  StartNo             3  InvoiceBook      4  InvoiceStartNo
                    //  5  InvoiceEndNo           6  MaxInvoiceNo     7  MaxInvoiceDate
                    retInvoM022  =  getInvoM022(retSale05M080,  stringInvoiceKind,  stringCompanyCd) ;
                    if (retInvoM022.length == 0){
                        message("公司："  +  getCompanyName(stringCompanyCd)  +  "、案別："  +  stringProjectID1  +  " 之 "  +  stringInvoiceKind  +  " 聯式電腦發票已用完! 請洽 財務室 領取!");
                        doInvoM022Undo(vectorCompanyCd,  intCompany) ;// 將狀態解開，使他人可以使用
                        return value; 
                        
                    }
                    //發票可用號碼
                    for(int  intInvoM022=0  ;  intInvoM022<retInvoM022.length  ;  intInvoM022++) {
                        String  stringEndNo  =  retInvoM022[intInvoM022][5].trim( ) ;
                        String  stringMaxNo  =  retInvoM022[intInvoM022][6].trim( ) ;
                        if(stringMaxNo.length( )  ==  0) {
                            intAvailableInvoice  =  intAvailableInvoice  +  50 ;
                        } else {
                            intAvailableInvoice  =  intAvailableInvoice + doParseInteger(stringEndNo.substring(2,10)) - doParseInteger(stringMaxNo.substring(2,10));
                        } 
            System.out.println("test0>>>" + intAvailableInvoice);
                    }//End of for int intInvoM022
                    hashtableAvailableInvoice.put((stringCompanyCd +  stringInvoiceKind),  ""+intAvailableInvoice) ;
                    //保護發票控管檔
                    for(int  intInvoM022=0  ;  intInvoM022<retInvoM022.length  ;  intInvoM022++) {
                        doUpdateInvoM022(stringCompanyCd,  retInvoM022[intInvoM022]) ;     // 更新狀態，避免他人使用
                        doInsertSale05M085(stringCompanyCd,  retInvoM022[intInvoM022],stringInvoiceKind)  ;  // 將資料複製至 Sale05M080
                    } 
                }             
            }
// 由 Sale05M084 取得之所有客戶 START
            int               intType                                                              =  0 ;
            int               intPos                                                                = 0 ;
            int               intCount                                                             =  0 ;
            Vector        vectorData                                                         =  new  Vector( ) ;
            Hashtable  hashtableCustomCompanyPositionTypeCount  =  new  Hashtable( ) ;  // [發票之項目]計數
            Hashtable  hashtableInvoiceTotalCount                               =  new  Hashtable( ) ;  // [發票]計數(公司別 + 發票種類(3 or 2))
            //System.out.println("(LOOP1)由 Sale05M084 取得之所有客戶--------"+retSale05M084P.length) ;
            for(int  intSale05M084P=0  ;  intSale05M084P<retSale05M084P.length  ;  intSale05M084P++) {
                // 取得不同發票型態總數
                stringCustomNo         =  retSale05M084P[intSale05M084P][0].trim( ) ;
                stringNationality         =  retSale05M084P[intSale05M084P][1].trim( ) ;
                stringInvoiceKind       =  (stringCustomNo.length( ) == 8)  ?  "3"  :  "2" ;
                if("2".equals(stringNationality))  stringInvoiceKind  =  "2" ;
                if("3".equals(stringNationality))  stringInvoiceKind  =  "2" ;    // 2010-3-5  增加
// 取得特定客戶的購屋證明單 START                           
                retSale05M086  =  getSale05M086(stringDocNo,  stringCustomNo) ;
                //System.out.println(intSale05M084P+"(LOOP2)取得特定客戶的購屋證明單--------"+retSale05M086.length) ;
                for(int  intSale05M086=0  ;  intSale05M086<retSale05M086.length  ;  intSale05M086++) {
                    stringOrderNo  =  retSale05M086[intSale05M086][0].trim( ) ;
/* 取得特定客戶、特定購屋證明單之收款單明細  START*/
                    //   0  HouseCar                     1  Position                  2  ORDER_NO               3  ITEMLS_CD
                    //   4  H_MomentaryMoney     5  H_UsableMoney     6  H_ReceiveMoney       7  (H_ReceiveMoney + H_UsableMoney) AS H_InvoiceMoney
                    //   8  L_MomentaryMoney     9  L_UsableMoney    10  L_ReceiveMoney     11  (L_ReceiveMoney + L_UsableMoney) AS L_InvoiceMoney
                    // 12 H_DiscountMoney        13  H_FeeMoney        14  L_FeeMoney 
                    retSale05M081  =  getSale05M081(stringDocNo,  stringOrderNo,  stringCustomNo) ;
                    //
                    for(int  intSale05M081=0  ;  intSale05M081<retSale05M081.length  ;  intSale05M081++) {
                        String  stringHouseCar                        =  retSale05M081[intSale05M081][0].trim( ) ;    // 房車
                        String  stringH_MomentaryMoney       =  retSale05M081[intSale05M081][4].trim( ) ;     // 房-暫收
                        String  stringH_InvoiceMoney              =  retSale05M081[intSale05M081][7].trim( ) ;      // 房-實收+房-可用
                        String  stringL_MomentaryMoney        =  retSale05M081[intSale05M081][8].trim( ) ;      // 土-暫收
                        String  stringL_InvoiceMoney               =  retSale05M081[intSale05M081][11].trim( ) ;  //土-實收+土-可用
                        String  stringFeeMoney                        =  "" ;                                                                 // 消費卷
                        String  stringFeeMoneyTemp               =  "" ;                                                                  // 消費卷
                        String  stringFeeMoneyUse                  =  "" ;                                                                  // 消費卷
                        String  stringSUMReceiveCheckFee    =  ""  ;
                        String  stringInvoiceTotalMoneyTemp  =  "" ;    
                        String  stringH_DiscountMoneyUse      =  "" ;
                        //
                        stringL_DiscountMoney        =  retSale05M081[intSale05M081][12].trim( ) ;  // 利息折讓
                        stringITEMLS_CD                 =  retSale05M081[intSale05M081][3].trim( ) ;//款項
                        stringORDER_NO                 =  retSale05M081[intSale05M081][2].trim( ) ; //期別
                        stringPosition                        =  retSale05M081[intSale05M081][1].trim( ) ;//戶別
                        stringSUMReceiveCheck      =  stringHouseCar  +  stringPosition  +  stringORDER_NO ;
                        intPos                                   =  vectorPosition.indexOf(stringPosition) ;
                        stringPointNo                        =  "" ;
                        // oce
                        if("01002".equals(stringITEMLS_CD)) {
                            if(doParseDouble(stringH_InvoiceMoney)>0)   hashtablePosition.put(stringPosition+"_H",  "Y") ;
                            if(doParseDouble(stringL_InvoiceMoney)>0)   hashtablePosition.put(stringPosition+"_L",  "Y") ;
                        }
                        // 1 建物款   2 土地款
                        for(int  intInvoice=1  ;  intInvoice<=2  ;  intInvoice++) {
                            // intType  種類
                            // 0 房屋-建物款         1 房屋-土地款          2 車位-建物款         3 車位-土地款 
                            // 4 房屋-建物款(消)    5 房屋-土地款(消)   6 車位-建物款(消)  7 車位-土地款 (消)
                            stringH_DiscountMoneyUse    =  (intInvoice == 1) ? stringL_DiscountMoney : "0" ;  // 折讓 Add
                            //房屋款
                            if (intInvoice == 1)  if (stringH_InvoiceMoney.length() ==0 || doParseFloat(stringH_InvoiceMoney) == 0) continue;
                            //土地款
                            if (intInvoice == 2)  if (stringL_InvoiceMoney.length() ==0 || doParseFloat(stringL_InvoiceMoney) == 0) continue;
                            //房屋-建物款
                            if (stringHouseCar.equals("House")  &&  intInvoice == 1){
                                intType                                      =  0 ;
                                stringInvoiceTotalMoneyTemp  =  ""  +  doParseFloat(stringH_InvoiceMoney) * 10000 ;  //發票金額
                                //
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                            }
                            //房屋-土地款
                            if (stringHouseCar.equals("House")  &&  intInvoice == 2){
                                intType                                     =  1 ;
                                stringInvoiceTotalMoneyTemp  =  ""  +  doParseFloat(stringL_InvoiceMoney) * 10000 ;   //發票金額
                                //
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                            }
                            //車位-建物款    
                            if (stringHouseCar.equals("Car")  &&  intInvoice == 1){
                                intType                                     =  2 ;
                                stringInvoiceTotalMoneyTemp  =  ""  +  doParseFloat(stringH_InvoiceMoney) * 10000 ;   //發票金額
                                //
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                            }
                            //車位-土地款
                            if (stringHouseCar.equals("Car")  &&  intInvoice == 2){
                                intType                                     =  3 ;
                                stringInvoiceTotalMoneyTemp  =  ""  +  doParseFloat(stringL_InvoiceMoney) * 10000 ;    //發票金額
                                //
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                            }
                            // 取得同一個產品在同一個購屋證明單內，有幾位顧客並同分擔
                            int  intCustomCount               =  getSale05M086Count(stringDocNo,  stringCustomNo,  stringPosition) ;
                            stringSUMDiscountCheck       =  stringSUMReceiveCheck  +  "-"  +  stringH_DiscountMoneyUse ;      // 折讓 Add
                            stringSUMReceiveCheckUse  =  stringSUMReceiveCheck  +  "-"+stringInvoiceTotalMoneyTemp ;
                            if(intCustomCount  >  1) {
                                // 折讓 Start
                                if(doParseDouble(stringH_DiscountMoneyUse)  >  0) {
                                    intCustomPos  =  doParseInteger(""  +  (String)hashtableOrderNo.get(stringSUMDiscountCheck)) ;
                                    if(intCustomCount  !=  (intCustomPos+1)) {
                                        stringPercentage                  =  getSale05M091ForPercentage(stringOrderNo,  stringCustomNo) ;
                                        stringH_DiscountMoneyUse  =  "" + (doParseDouble(stringH_DiscountMoneyUse) * (doParseDouble(stringPercentage) /100)); 
                                        stringH_DiscountMoneyUse  =  convert.FourToFive(stringH_DiscountMoneyUse,  4);
                                        doubleSUMReceive              =  doParseDouble(""  +  (String)hashtableSUMReceive.get(stringSUMDiscountCheck)) ;
                                        doubleSUMReceive            +=  doParseDouble(stringH_DiscountMoneyUse);
                                        //
                                        hashtableOrderNo.put(stringSUMDiscountCheck,        ""  +  (intCustomPos+1)) ;
                                        hashtableSUMReceive.put(stringSUMDiscountCheck,  ""  +  doubleSUMReceive) ;
                                    } else {
                                        // 最後一筆
                                        doubleSUMReceive              =  doParseDouble(""  +  (String)hashtableSUMReceive.get(stringSUMDiscountCheck)) ;
                                        stringH_DiscountMoneyUse  =  "" + (doParseDouble(stringH_DiscountMoneyUse) - doubleSUMReceive);
                                        stringH_DiscountMoneyUse  =  convert.FourToFive(stringH_DiscountMoneyUse,4);
                                        hashtableSUMReceive.remove(stringSUMDiscountCheck) ;
                                        hashtableOrderNo.remove(stringSUMDiscountCheck) ;
                                    }
                                }
                                // 折讓 End
                                intCustomPos  =  doParseInteger(""  +  (String)hashtableOrderNo.get(stringSUMReceiveCheckUse)) ;
                                if(intCustomCount  !=  (intCustomPos+1)) {
                                    stringPercentage             =  getSale05M091ForPercentage(stringOrderNo,  stringCustomNo) ;
                                    stringInvoiceTotalMoney  =  "" + (doParseFloat(stringInvoiceTotalMoneyTemp) * (doParseFloat(stringPercentage) /100)); 
                                    stringInvoiceTotalMoney  =  convert.FourToFive(stringInvoiceTotalMoney,0);
                                    floatSUMReceive              =  doParseFloat(""  +  (String)hashtableSUMReceive.get(stringSUMReceiveCheckUse)) ;
                                    floatSUMReceive            +=  doParseFloat(stringInvoiceTotalMoney);
                                    // 
                                    //if(intInvoice == 2) {
                                        hashtableOrderNo.put(stringSUMReceiveCheckUse,        ""  +  (intCustomPos+1)) ;
                                        hashtableSUMReceive.put(stringSUMReceiveCheckUse,  ""  +  floatSUMReceive) ;
                                    //}
                                } else {
                                    //最後1個客戶用減的
                                    floatSUMReceive              =  doParseFloat(""  +  (String)hashtableSUMReceive.get(stringSUMReceiveCheckUse)) ;
                                    stringInvoiceTotalMoney  =  "" + (doParseFloat(stringInvoiceTotalMoneyTemp) - floatSUMReceive);
                                    stringInvoiceTotalMoney  =  convert.FourToFive(stringInvoiceTotalMoney,0);
                                    //if(intInvoice == 2) {
                                        hashtableSUMReceive.remove(stringSUMReceiveCheckUse) ;
                                        hashtableOrderNo.remove(stringSUMReceiveCheckUse) ;
                                    //}
                                }
                            } else {                                
                                // 僅一位客戶時
                                stringInvoiceTotalMoney  =  convert.FourToFive(stringInvoiceTotalMoneyTemp,0);
                            }
                            //System.out.println("stringInvoiceTotalMoneyTemp-----------------"+stringInvoiceTotalMoneyTemp+"---------"+stringSUMReceiveCheck+"-----"+stringInvoiceTotalMoney) ;
                            // 儲存資料(客戶編號-公司-Position-狀態-狀態數目)
                            stringCompanyCd                      =  (intType==0  ||  intType==2) ?  retSale05M040[intPos][0] :  retSale05M040[intPos][1] ;
                            stringKey                                   =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  intType ;
                            intCount                                     =  doParseInteger(""  +  (String)  hashtableCustomCompanyPositionTypeCount.get(stringKey)) ;
                            stringKey                                   =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  arrayPointNo[intType]  +  intCount ;
                            /*System.out.println(stringCustomNo           +  "-----------------"  +
                                             stringCompanyCd        +  "-----------------"  +
                                             stringPosition               +  "-----------------"  +
                                             arrayPointNo[intType]  +  "-----------------"  +
                                             intCount                        +  "-----------------"  +  stringH_DiscountMoneyUse) ;*/
                            // 0  stringInvoiceKind               1  stringInvoiceTotalMoney      2  stringITEMLS_CD      3  stringORDER_NO       4  stringPosition
                            // 5  stringL_DiscountMoney     6  stringDocNo                7  L_COM            8  OrderNo
                            vectorData.add(stringInvoiceKind) ;
                            vectorData.add(stringInvoiceTotalMoney) ;
                            vectorData.add(stringITEMLS_CD) ;
                            vectorData.add(stringORDER_NO) ;
                            vectorData.add(stringPosition) ;
                            vectorData.add(stringH_DiscountMoneyUse) ;
                            vectorData.add(stringDocNo) ;
                            vectorData.add(retSale05M040[intPos][3]) ;
                            vectorData.add(stringOrderNo) ;
                            hashtableData.put(stringKey,  vectorData) ;
                            vectorData  =  new  Vector( ) ;
                        }
                        // 消費卷
                        // 1 建物款   2 土地款
                        for(int  intInvoice=1  ;  intInvoice<=2  ;  intInvoice++) {
                            // intType  種類
                            // 0 房屋-建物款         1 房屋-土地款          2 車位-建物款         3 車位-土地款 
                            // 4 房屋-建物款(消)    5 房屋-土地款(消)   6 車位-建物款(消)  7 車位-土地款 (消)
                            // 消費卷 S
                            if(intInvoice == 1) {
                                stringFeeMoney                      =  retSale05M081[intSale05M081][13].trim( ) ;
                            } else {
                                stringFeeMoney                      =  retSale05M081[intSale05M081][14].trim( ) ;
                            }// 消費卷  E
                            if(doParseDouble(stringFeeMoney)  <=  0)  continue ;
                            //房屋-建物款
                            if (stringHouseCar.equals("House")  &&  intInvoice == 1){
                                intType                         =  4 ;
                                stringFeeMoneyTemp  =  ""  +  doParseFloat(stringFeeMoney) * 10000 ;  //發票金額
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                                //System.out.println("intType("+intType+")-----------------"+stringFeeMoneyTemp) ;
                            }
                            //房屋-土地款
                            if (stringHouseCar.equals("House")  &&  intInvoice == 2){
                                intType                         =  5 ;
                                stringFeeMoneyTemp  =  ""  +  doParseFloat(stringFeeMoney) * 10000 ;  //發票金額
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                                //System.out.println("intType("+intType+")-----------------"+stringFeeMoneyTemp) ;
                            }
                            //車位-建物款    
                            if (stringHouseCar.equals("Car")  &&  intInvoice == 1){
                                intType                         =  6 ;
                                stringFeeMoneyTemp  =  ""  +  doParseFloat(stringFeeMoney) * 10000 ;  //發票金額
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                                //System.out.println("intType("+intType+")-----------------"+stringFeeMoneyTemp) ;
                            }
                            //車位-土地款
                            if (stringHouseCar.equals("Car")  &&  intInvoice == 2){
                                intType                         =  7 ;
                                stringFeeMoneyTemp  =  ""  +  doParseFloat(stringFeeMoney) * 10000 ;  //發票金額
                                doCount(intPos,  intType,  stringCustomNo,  stringInvoiceKind,  retSale05M040,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                                //System.out.println("intType("+intType+")-----------------"+stringFeeMoneyTemp) ;
                            }
                            // 取得同一個產品在同一個購屋證明單內，有幾位顧客並同分擔
                            int  intCustomCount               =  getSale05M086Count(stringDocNo,  stringCustomNo,  stringPosition) ;
                            stringSUMReceiveCheckFee  =  stringSUMReceiveCheck  +  "-"+stringFeeMoneyTemp+"Fee" ;
                            if(intCustomCount  >  1) {
                                intCustomPos  =  doParseInteger(""  +  (String)hashtableOrderNo.get(stringSUMReceiveCheckFee)) ;
                                if(intCustomCount  !=  (intCustomPos+1)) {
                                    stringPercentage             =  getSale05M091ForPercentage(stringOrderNo,  stringCustomNo) ;
                                    stringFeeMoneyUse  =  "" + (doParseFloat(stringFeeMoneyTemp) * (doParseFloat(stringPercentage) /100)); 
                                    stringFeeMoneyUse  =  convert.FourToFive(stringFeeMoneyUse,0);
                                    floatSUMReceive       =  doParseFloat(""  +  (String)hashtableSUMReceive.get(stringSUMReceiveCheckFee)) ;
                                    floatSUMReceive      +=  doParseFloat(stringInvoiceTotalMoney);
                                    hashtableOrderNo.put(stringSUMReceiveCheckFee,        ""  +  (intCustomPos+1)) ;
                                    hashtableSUMReceive.put(stringSUMReceiveCheckFee,  ""  +  floatSUMReceive) ;
                                } else {
                                    //最後1個客戶用減的
                                    // 消費卷
                                    floatSUMReceive       =  doParseFloat(""  +  (String)hashtableSUMReceive.get(stringSUMReceiveCheckFee)) ;
                                    stringFeeMoneyUse  =  "" + (doParseFloat(stringFeeMoneyTemp) - floatSUMReceive);
                                    stringFeeMoneyUse  =  convert.FourToFive(stringFeeMoneyUse,0);
                                    hashtableSUMReceive.remove(stringSUMReceiveCheckFee) ;
                                    hashtableOrderNo.remove(stringSUMReceiveCheckFee) ;
                                }
                            } else {                                
                                // 僅一位客戶時
                                stringFeeMoneyUse         =  stringFeeMoneyTemp ;
                            }
                            //System.out.println("stringInvoiceTotalMoneyTemp-----------------"+stringInvoiceTotalMoneyTemp+"---------"+stringSUMReceiveCheck+"-----"+stringInvoiceTotalMoney) ;
                            // 儲存資料(客戶編號-公司-Position-狀態-狀態數目)
                            stringCompanyCd                      =  (intType==0  ||  intType==2) ?  retSale05M040[intPos][0] :  retSale05M040[intPos][1] ;
                            stringKey                                   =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  intType ;
                            intCount                                     =  doParseInteger(""  +  (String)  hashtableCustomCompanyPositionTypeCount.get(stringKey)) ;
                            stringKey                                   =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  arrayPointNo[intType-4]  +  intCount +"Fee" ;
                            /*System.out.println("消費券--------------------["+stringKey+"]") ;
                            System.out.println(stringCustomNo               +  "-----------------"  +
                                             stringCompanyCd            +  "-----------------"  +
                                             stringPosition                   +  "-----------------"  +
                                             arrayPointNo[intType-4]   +  "-----------------"  +
                                             intCount                           +  "-----------------" +
                                             stringFeeMoneyUse         +  "-----------------"  +  stringH_DiscountMoneyUse) ;*/
                            // 0  stringInvoiceKind               1  stringInvoiceTotalMoney      2  stringITEMLS_CD      3  stringORDER_NO       4  stringPosition
                            // 5  stringL_DiscountMoney     6  stringDocNo                7  L_COM            8  OrderNo
                            vectorData.add(stringInvoiceKind) ;
                            vectorData.add(stringFeeMoneyUse) ;
                            vectorData.add(stringITEMLS_CD) ;
                            vectorData.add(stringORDER_NO) ;
                            vectorData.add(stringPosition) ;
                            vectorData.add("0") ;
                            vectorData.add(stringDocNo) ;
                            vectorData.add(retSale05M040[intPos][3]) ;
                            vectorData.add(stringOrderNo) ;
                            hashtableData.put(stringKey,  vectorData) ;
                            vectorData  =  new  Vector( ) ;
                        }
                        /* 暫收款(一個收款明細表，一個暫收款) */
                        if(vectorSale05M061.indexOf(stringSUMReceiveCheck)  ==  -1) {
                            vectorSale05M061Sql  =  doUpdateSale05M061(stringH_MomentaryMoney,  stringL_MomentaryMoney,  stringProjectID1,
                                                                   stringHouseCar,                  stringPosition,                     stringORDER_NO,
                                                                   vectorSale05M061Sql) ;
                            vectorSale05M061.add(stringSUMReceiveCheck) ;
                        }
                    } 
// 取得特定客戶、特定購屋證明單之收款單明細 END
                }
// 取得特定客戶的購屋證明單 END
            }
// 由 Sale05M084 取得之所有客戶  END
            // stringCustomNo  +  stringCompanyCd  +  stringPosition  +  intType ;
            // 未足 5 項目的發票，加1 處理
            for(int  intSale05M084P=0  ;  intSale05M084P<retSale05M084P.length  ;  intSale05M084P++) {
                // 取得不同發票型態總數
                stringCustomNo         =  retSale05M084P[intSale05M084P][0].trim( ) ;
                stringNationality         =  retSale05M084P[intSale05M084P][1].trim( ) ;
                stringInvoiceKind       =  (stringCustomNo.length( ) == 8)  ?  "3"  :  "2" ;
                if("2".equals(stringNationality))  stringInvoiceKind  =  "2" ;
                for(int  intCompanyNo=0  ;  intCompanyNo<vectorCompanyCd.size( )  ;  intCompanyNo++) {
                    stringCompanyCd  =  ""  +  (String) vectorCompanyCd.get(intCompanyNo) ;
                    for(int  intPositionNo=0  ;  intPositionNo<vectorUniquePosition.size( )  ;  intPositionNo++) {
                        stringPosition  =  ""  +  (String) vectorUniquePosition.get(intPositionNo) ;
                        for(int  intNo=0  ;  intNo<8  ;  intNo++) {
                            doCount2(intNo,  stringCustomNo,  stringInvoiceKind,  stringCompanyCd,  stringPosition,  hashtableCustomCompanyPositionTypeCount,  hashtableInvoiceTotalCount) ;
                        }
                    }
                }
            }
            // 檢核
            int  intInvoiceTotalCount  =  0 ;  // 總共要開立的發票數
            for(int  intCompany=0  ;  intCompany<vectorCompanyCd.size()  ;  intCompany++) {
                stringCompanyCd  =  ""  +  (String)  vectorCompanyCd.get(intCompany) ;
                for(int  intNo=0  ;  intNo<vectorInvoiceKind.size()  ;  intNo++) {
                    stringInvoiceKind      =  (String)  vectorInvoiceKind.get(intNo) ;
                    intAvailableInvoice    =  doParseInteger(""  +  (String)   hashtableAvailableInvoice.get(stringCompanyCd  +  stringInvoiceKind)) ;
                    intInvoiceTotalCount =  doParseInteger(""  +  (String)hashtableInvoiceTotalCount.get(stringCompanyCd  +  stringInvoiceKind)) ;
                    //System.out.println(intCompany  +  "-----------"  +  intNo+"-----------"+intAvailableInvoice+"------------------"+intInvoiceTotalCount) ;
                    if (intInvoiceTotalCount  >  intAvailableInvoice) {
                        //message("公司："  +  getCompanyName(stringCompanyCd)  +  "之電腦發票不足！ 請洽 財務室 領取！尚缺"  +  (intInvoiceTotalCount  -  intAvailableInvoice)  +  "張") ;
                        message("公司："  +  getCompanyName(stringCompanyCd)  +  "、案別："  +  stringProjectID1  +  " 之 "  +  stringInvoiceKind  +  " 聯式電腦發票不足！ 請洽 財務室 領取！尚缺"  +  (intInvoiceTotalCount  -  intAvailableInvoice)  +  "張") ;
                        doInvoM022Undo(vectorCompanyCd,  vectorCompanyCd.size( )) ;// 將狀態解開，使他人可以使用
                        return  value ;
                    } 
                }
            }
            // 由 Sale05M084 取得之所有客戶 START
            int            intRecordNo                      =  1 ;  //for Sale05M087  
            int            intDiscountCount              =  1 ;
            int            intDiscountCountInvoice   =  0 ;
            int            intRecordNoSale05M089  =  1 ;
            int            intRecordMax                    =  0 ;
            //int            intFeeCount                      =  0 ;
            Object     objtectReturn                    =  null ;
            Vector     vectorReturn                     =  null ;
            String      stringITEMLS_CHINESE    =  "" ;
            String      stringInvoiceMoney           =  "" ;
            String      stringInvoiceTax                =  "" ;
            String      stringInvoiceMessage        = "";
            String      stringInvoiceYYYYMM       =  "" ;
            String      stringFSChar                     =  "" ;
            String      stringStartNo                     =  "" ;
            String      stringInvoiceBook              =  "" ;
            String      stringInvoiceStartNo           =  "" ;
            String      stringInvoiceEndNo            =  "" ;
            String      stringMaxInvoiceNo            =  "" ;
            String      stringTaxRate                    =  "" ;
            String      stringTaxKind                     =  "" ; 
            String      stringTemp                        =  "" ;
            String      stringDiscountNo               =  "" ;
            String      stringLCom                        =  "" ;
            String      stringFeeMoney                 =  "" ;
            //String      stringInvoiceNoFee            =  "" ;
            double    doubleInvoiceMoney          =  0 ;
            double    doubleInvoiceTotalMoney  =  0 ;
            //double    doubleInvoiceTotalMoneyFee  =  0 ;
            double    doubleInvoiceTax               =  0 ;
            double    doubleDiscountMoney       =  0 ;
            boolean  booleanNextNotNull            =  true ;
            boolean  booleanDiscountFlag         =  false ; 
            boolean  booleanFlag                       =  true ;
            Vector     vectorLCom                       =  new  Vector( ) ;
            //
            vectorLCom.add("1") ;
            vectorLCom.add("K") ;
                vectorLCom.add("L") ;
            vectorLCom.add("P") ; //990609
            vectorLCom.add("U") ; //2010/10/14
            vectorLCom.add("4") ; //1001019
            vectorLCom.add("A5") ; //2014-05-15 B3018
            // 客戶編號-公司-Position-狀態-狀態數目
            for(int  intSale05M084P=0  ;  intSale05M084P<retSale05M084P.length  ;  intSale05M084P++) {
                System.out.println("1111111111進入 Sale05M084P START =============================");
                stringCustomNo  =  retSale05M084P[intSale05M084P][0].trim( ) ;
                stringNationality  =  retSale05M084P[intSale05M084P][1].trim( ) ;
                stringInvoiceKind     =  (stringCustomNo.length( ) == 8)  ?  "3"  :  "2" ;  //聯式
                if("2".equals(stringNationality))  stringInvoiceKind  =  "2" ;
                if("3".equals(stringNationality))  stringInvoiceKind  =  "2" ;      //  2101-3-5
                if("4".equals(stringNationality))  stringInvoiceKind  =  "2" ;      // 20100401
                for(int  intPositionNo=0  ;  intPositionNo<vectorUniquePosition.size( )  ;  intPositionNo++) {
                    System.out.println("22222222進入 vectorUniquePosition START =============================" + intPositionNo + "/" + vectorUniquePosition);    
                    stringPosition  =  ""  +  (String)  vectorUniquePosition.get(intPositionNo) ;
                    for(int  intCompanNo=0  ;  intCompanNo<vectorCompanyCd.size( )  ;  intCompanNo++) {
                        System.out.println("3333333333進入 vectorCompanyCd START =============================" + intCompanNo + "/" + vectorCompanyCd);   
                        stringCompanyCd  =  ""  +  (String)  vectorCompanyCd.get(intCompanNo) ;
                        for(int  intNo=0  ;  intNo<4  ;  intNo++) {
                            System.out.println("44444444進入 intNo START =============================" + intNo + "/" + 4); 
                            intDiscountCount               =  1 ;  // 折讓單流水號
                            intDiscountCountInvoice    =  0 ;
                            doubleInvoiceMoney          =  0 ;
                            doubleInvoiceTotalMoney  =  0 ;
                            doubleInvoiceTax               =  0 ;
                            doubleDiscountMoney        =  0 ;
                            booleanDiscountFlag          =  false ;
                            stringKey                             =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  intNo ;
                            System.out.println("stringKey===========>"+stringKey);
                            System.out.println("hashtableCustomCompanyPositionTypeCount===========>"+hashtableCustomCompanyPositionTypeCount);
                            intRecordMax                      =  doParseInteger(""  +  (String)hashtableCustomCompanyPositionTypeCount.get(stringKey)) ;
                            System.out.println("intRecordMax===========>"+intRecordMax);
                            for(int  intRecord=1  ;  intRecord<=intRecordMax  ;  intRecord++) {
                                System.out.println("55555555進入 intRecordMax START =============================");    
                                booleanNextNotNull  =  true ;
                                stringKey                 =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  arrayPointNo[intNo]  +  intRecord ;
                                objtectReturn           =  hashtableData.get(stringKey) ;
                                if(objtectReturn  ==  null) break ;
                                // 取得資料
                                // 0  stringInvoiceKind               1  stringInvoiceTotalMoney      2  stringITEMLS_CD      3  stringORDER_NO       4  stringPosition
                                // 5  stringL_DiscountMoney     6  stringDocNo                7  L_COM            8  OrderNo
                                vectorReturn                    =  (Vector) objtectReturn ;
                                stringInvoiceKind              =  (String)  vectorReturn.get(0) ;
                                stringInvoiceTotalMoney  =  (String)  vectorReturn.get(1) ;
                                stringITEMLS_CD            =  (String)  vectorReturn.get(2) ;               //  2101-3-5 修改
                                stringITEMLS_CHINESE   =  getItemlsChinese(stringITEMLS_CD) ;         //  2101-3-5 修改
                                stringORDER_NO            =  (String)  vectorReturn.get(3) ;
                                stringPosition                   =  (String)  vectorReturn.get(4) ;
                                stringL_DiscountMoney    =  (String)  vectorReturn.get(5) ;
                                stringDocNo                      =  (String)  vectorReturn.get(6) ;
                                stringLCom                       =  (String)  vectorReturn.get(7) ;
                                stringOrderNo                   =  (String)  vectorReturn.get(8) ;
                                System.out.println("後--------------------"+
                                                               stringCustomNo            +  "-----------------"  +
                                                     stringCompanyCd        +  "-----------------"  +
                                                     stringPosition               +  "-----------------"  +
                                                     arrayPointNo[intNo]      +  "-----------------"  +
                                                     intRecord                     +  "------------------"  +  stringL_DiscountMoney) ;
                                // 判斷下一筆是否有值
                                stringKey                          =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  arrayPointNo[intNo]  +  (intRecord+1) ;
                                objtectReturn                    =  hashtableData.get(stringKey) ;
                                if(objtectReturn  ==  null) booleanNextNotNull  =  false ;
                                // 取得發票號碼
                                if(intRecord  %  5  ==  1) {
                                    System.out.println("6666666666進入 Sale05M084P START =============================");   
                                    /*發票號碼自動產生(僅一筆)*/
                                    //   0  InvoiceYYYYMM     1  FSChar     2  StartNo    3  InvoiceBook    4  InvoiceStartNo
                                    //   5  InvoiceEndNo      6  MaxInvoiceNo
                                    retSale05M085  =  getSale05M085(retSale05M080,stringCompanyCd,stringInvoiceKind) ;
                                    for(int  intSale05M085=0  ;  intSale05M085<retSale05M085.length  ;  intSale05M085++) {
                                        stringInvoiceYYYYMM  =  retSale05M085[intSale05M085][0].trim( ) ;
                                        stringFSChar               =  retSale05M085[intSale05M085][1].trim( ) ;
                                        stringStartNo                =  retSale05M085[intSale05M085][2].trim( ) ;
                                        stringInvoiceBook         =  retSale05M085[intSale05M085][3].trim( ) ;
                                        stringInvoiceStartNo     =  retSale05M085[intSale05M085][4].trim( ) ;
                                        stringInvoiceEndNo      =  retSale05M085[intSale05M085][5].trim( ) ;
                                        stringMaxInvoiceNo      =  retSale05M085[intSale05M085][6].trim( ) ;
                                        //發票號碼
                                        if(stringMaxInvoiceNo.length( )  ==  0) {
                                            stringInvoiceNo  =  stringInvoiceStartNo ;
                                        } else  {
                                            intInvoiceNo       =  doParseInteger(stringMaxInvoiceNo.substring(2,10))  +  1 ;
                                            stringInvoiceNo  =  stringMaxInvoiceNo.substring(0,2)  +  convert.add0(("" + intInvoiceNo),"8") ;
                                        }
                                        System.out.println("取得發票號碼==================>"+stringInvoiceNo);
                                      }
                                      //System.out.println("------------------------發票號碼："+stringInvoiceNo) ;
                                    //發票已開完Flag 
                                    if(stringInvoiceNo.equals(stringInvoiceEndNo))
                                        stringEndYes = "Y";
                                    else
                                        stringEndYes = "N";
                                    //發票訊息 
                                    stringInvoiceMessage  +=  stringInvoiceNo  +  "\n" ;
                                }
                                 //計算稅率
                                /* //  0  TaxRate   1  TaxKind
                                retInvoM010  =  getInvoM010(arrayPointNo[intNo]) ;
                                if (retInvoM010.length  ==  0){
                                    message("發票系統.摘要代碼 錯誤!");
                                    return  value ; 
                                }*/
                                // 存入 DB
                                /*發票.InvoM030(Head)(一筆)*/
                                if(intNo  ==  0) {
                                    if(!"".equals(stringL_DiscountMoney)  &&  doParseDouble(stringL_DiscountMoney)  !=  0) {
                                        doubleDiscountMoney  +=  doParseDouble(stringL_DiscountMoney) *  10000 ;
                                        intDiscountCountInvoice++ ;
                                    }
                                }
                                doubleInvoiceTotalMoney  +=  doParseDouble(stringInvoiceTotalMoney)  ;
                                if(intRecord % 5 == 0  ||  !booleanNextNotNull) {
                                    doubleInvoiceTotalMoney  +=  doubleDiscountMoney  ;
                                    if(intNo  ==  0  ||  intNo  ==  2) {
                                        doubleInvoiceMoney  =  (doubleInvoiceTotalMoney / 1.05) ;
                                    } else {
                                        doubleInvoiceMoney  =  doubleInvoiceTotalMoney ;
                                    }
                                    doubleInvoiceTax       =  doubleInvoiceTotalMoney  -  doubleInvoiceMoney;
                                    stringPointNo  =  arrayPointNo[intNo].trim( ) ;
                                    //
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "M51A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H51A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H50A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H71A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ; //990603
                                    }
                                    // 2010/10/14  修正
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H80A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    }
                                    //1001018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H68A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    }
                                    //2012/05/21 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H102A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H102A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2013/09/04 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H75A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H75A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2012/09/05 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H117A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H117A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2014/04/08 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H118A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H118A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2014-05-15 B3018 START
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H76A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H76A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2014-05-15 B3018 END
                                    //  2101-3-5 修改 Start
                                    if("2101".equals(stringPosition)  ||  "2102".equals(stringPosition)) {
                                        // 房
                                        booleanFlag  =  "Y".equals(""+hashtablePosition.get(stringPosition+"_H")) ;
                                    } else if("2103".equals(stringPosition)  ||  "2104".equals(stringPosition)) {
                                        // 土
                                        booleanFlag  =  "Y".equals(""+hashtablePosition.get(stringPosition+"_L")) ;
                                    } else {
                                        booleanFlag  =  true ;
                                    }
                                    /*if("H72A".equals(stringProjectID1) &&  booleanFlag  &&  "2103".equals(stringPointNo) &&  !"A30148".equals(stringPosition)) {
                                    //if("H72A".equals(stringProjectID1) &&  "01002".equals(stringITEMLS_CD)  &&  "2103".equals(stringPointNo) &&  !"A30148".equals(stringPosition)) {
                                        stringPointNo  =  "2113" ;
                                    }
                                    if("H72A".equals(stringProjectID1) &&  booleanFlag   &&  "2104".equals(stringPointNo) &&  !"A30148".equals(stringPosition)) {
                                    //if("H72A".equals(stringProjectID1) && "01002".equals(stringITEMLS_CD)  &&  "2104".equals(stringPointNo) &&  !"A30148".equals(stringPosition)) {
                                        stringPointNo  =  "2112" ;
                                    }*/
                                    if("2101".equals(stringPointNo) &&  "3".equals(stringNationality)) {
                                        stringPointNo  =  "2114" ;
                                    }
                                    if("2103".equals(stringPointNo) &&  "3".equals(stringNationality)) {
                                        stringPointNo  =  "2115" ;
                                    }
                                    // 20100401 START B3018
                                    if("2101".equals(stringPointNo) &&  "4".equals(stringNationality)) {
                                        stringPointNo  =  "2114" ;
                                    }
                                    if("2103".equals(stringPointNo) &&  "4".equals(stringNationality)) {
                                        stringPointNo  =  "2115" ;
                                    }
                                    // 20100401 END
                                    stringTaxKind  =  (""+hashtableTaxKind.get(stringPointNo)).trim( ) ;
                                    if("".equals(stringTaxKind)  ||  "null".equals(stringTaxKind)) {
                                         retInvoM010  =  getInvoM010(stringPointNo) ;
                                         if(retInvoM010.length  ==  0) {
                                            
                                         } else {
                                            hashtableTaxKind.put(stringPointNo,  retInvoM010[0][1].trim( )) ;
                                            stringTaxKind  =  retInvoM010[0][1].trim( ) ;
                                         }
                                    }
                                    if("2114".equals(stringPointNo)  ||  "2115".equals(stringPointNo)) {
                                        doubleInvoiceMoney  =  doubleInvoiceTotalMoney ;
                                        doubleInvoiceTax       =  0 ;
                                    }
                                    //  2101-3-5 修改 End
                                    doInvertInvoM030(stringInvoiceNo,                                                 stringInvoiceKind,                                                          stringPosition,  
                                                   stringCustomNo,                                                 stringPointNo,                                                               convert.FourToFive(""+doubleInvoiceMoney,  0),
                                                   convert.FourToFive(""+doubleInvoiceTax,  0),  convert.FourToFive(""+doubleInvoiceTotalMoney,0),  stringTaxKind,
                                                   stringCompanyCd,                                             retSale05M080,                                                              ""  +  doubleDiscountMoney,
                                                   intDiscountCountInvoice) ;
                                    doubleInvoiceMoney          =  0 ;
                                    doubleInvoiceTotalMoney  =  0 ;
                                    doubleInvoiceTax               =  0  ;
                                    intDiscountCountInvoice    =  0 ;
                                    doInvertInvoM0C0(stringCustomNo,  stringOrderNo);
                                }
                                /*發票.InvoM031(Body)(多筆)*/
                                if(doParseInteger(stringORDER_NO)<=  0)stringORDER_NO   =  "" ;
                                
                                doInvertInvoM031(stringInvoiceNo,  intRecord,  stringITEMLS_CHINESE,  stringORDER_NO) ;
                                //System.out.println("---------------"+stringORDER_NO+"-----------") ;
                                //
                                if(intRecord  %  5  ==  1) {
                                    doInsertSale05M087(stringDocNo,  intRecordNo,  stringInvoiceNo) ;
                                    intRecordNo++ ;
                                }
                                //發票. UPDATE Sale05M085(發票管控檔)
                                if(intRecord  %  5  ==  1) {
                                    doUpdateSale05M085(stringInvoiceNo,            stringEDate,                  stringEndYes,
                                                      stringCompanyCd,        stringInvoiceYYYYMM,  stringFSChar,  
                                                      stringStartNo,         stringInvoiceBook) ;
                                }
                                //
                                if(intNo  !=  0) continue ;
                                else {
                                    //System.out.println(intSale05M084P+"-------"  +  intSale05M081+"-------"  +intNo+"--------------------------------------------"+intRecord+"--------A") ;
                                    if(!("".equals(stringL_DiscountMoney)  ||  doParseDouble(stringL_DiscountMoney)  ==  0))  {
                                        //System.out.println(intSale05M084P+"-------"  +  intSale05M081+"-------"  +intNo+"--------------------------------------------"+intRecord+"--------B") ;
                                        /* 取得折讓單號碼 */
                                        if(intDiscountCount  %  5  ==  1) {
                                            stringDiscountNo        =  getDiscountNo(stringCompanyCd,  retSale05M080) ;
                                            booleanDiscountFlag  =  true ;
                                        }
                                        // doubleDiscountMoney  +=  doParseDouble(stringL_DiscountMoney) ;
                                        //System.out.println(intSale05M084P+"-------"  +  intSale05M081+"-------"  +intNo+"--------------------------------------------"+intRecord+"--------C") ;
                                        /* 折讓單 InvoM040 (Head)(一筆) */
                                        if(intDiscountCount  %  5  ==  0) {
                                            // 新增
                                            //System.out.println("-----------------------新增頭檔") ;
                                            doInsertInvoM040(stringDiscountNo,  stringCustomNo,  stringCompanyCd,  retSale05M080,  doubleDiscountMoney) ;
                                            //System.out.println(intNo+"--------------------------------------------"+intRecord+"--------D") ;
                                            booleanDiscountFlag  =  false ;
                                            doubleDiscountMoney  =  0 ;
                                            
                                        }
                                        // 折讓單 InvoM041
                                        //System.out.println("-----------------------新增尾檔") ;
                                        doInsertInvoM041(stringDiscountNo,           intDiscountCount,  stringInvoiceNo,  arrayPointNo[intNo], 
                                                       ""  +  (doParseDouble(stringL_DiscountMoney)*10000)) ;
                                        //
                                        doInvertSale05M089(stringDocNo,  intRecordNoSale05M089,  stringDiscountNo,  stringInvoiceNo) ;
                                        intRecordNoSale05M089++ ;
                                        // 新增
                                        intDiscountCount++ ;
                                    }
                                }
                                if((!booleanNextNotNull  ||  intRecord  %  5  ==  0)  &&  doubleDiscountMoney  >  0  &&  booleanDiscountFlag) {
                                    //System.out.println("-----------------------新增頭檔") ;
                                    doInsertInvoM040(stringDiscountNo,  stringCustomNo,  stringCompanyCd,  retSale05M080,  doubleDiscountMoney) ;
                                    //System.out.println(intNo+"--------------------------------------------"+intRecord+"--------E") ;
                                    doubleDiscountMoney  =  0 ;
                                    booleanDiscountFlag  =  false ;
                                }
                            }//
                        }
                        
                        // 消費卷
                        //System.out.println("消費卷--------------------S") ;
                        for(int  intNo=4  ;  intNo<8  ;  intNo++) {
                            //
                            intDiscountCount               =  1 ;  // 折讓單流水號
                            intDiscountCountInvoice    =  0 ;
                            doubleInvoiceMoney          =  0 ;
                            doubleInvoiceTotalMoney  =  0 ;
                            doubleInvoiceTax               =  0 ;
                            doubleDiscountMoney        =  0 ;
                            booleanDiscountFlag          =  false ;
                            stringKey                             =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  intNo ;
                            intRecordMax                      =  doParseInteger(""  +  (String)hashtableCustomCompanyPositionTypeCount.get(stringKey)) ;
                            for(int  intRecord=1  ;  intRecord<=intRecordMax  ;  intRecord++) {
                                booleanNextNotNull  =  true ;
                                stringKey                 =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  arrayPointNo[intNo-4]  +  intRecord+"Fee" ;
                                objtectReturn           =  hashtableData.get(stringKey) ;
                                if(objtectReturn  ==  null) break ;
                                // 取得資料
                                // 0  stringInvoiceKind               1  stringInvoiceTotalMoney      2  stringITEMLS_CD      3  stringORDER_NO       4  stringPosition
                                // 5  stringL_DiscountMoney     6  stringDocNo                7  L_COM            8  stringFeeMoneyUse
                                vectorReturn                    =  (Vector) objtectReturn ;
                                stringInvoiceKind              =  (String)  vectorReturn.get(0) ;
                                stringInvoiceTotalMoney  =  (String)  vectorReturn.get(1) ;
                                stringITEMLS_CHINESE   =  getItemlsChinese((String)  vectorReturn.get(2)) ;
                                stringORDER_NO            =  (String)  vectorReturn.get(3) ;
                                stringPosition                   =  (String)  vectorReturn.get(4) ;
                                stringL_DiscountMoney    =  (String)  vectorReturn.get(5) ;
                                stringDocNo                      =  (String)  vectorReturn.get(6) ;
                                stringLCom                       =  (String)  vectorReturn.get(7) ;
                                /*System.out.println("後-消費券--------------------"+
                                                               stringCustomNo            +  "-----------------"  +
                                                     stringCompanyCd        +  "-----------------"  +
                                                     stringPosition               +  "-----------------"  +
                                                     arrayPointNo[intNo-4]   +  "-----------------"  +
                                                     intRecord                     +  "------------------"  +
                                                 stringInvoiceTotalMoney    +  "------------------"  +  stringL_DiscountMoney) ;*/
                                // 判斷下一筆是否有值
                                stringKey                          =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  arrayPointNo[intNo-4]  +  (intRecord+1)+"Fee" ;
                                objtectReturn                    =  hashtableData.get(stringKey) ;
                                if(objtectReturn  ==  null) booleanNextNotNull  =  false ;
                                // 取得發票號碼
                                if(intRecord  %  5  ==  1) {
                                    /*發票號碼自動產生(僅一筆)*/
                                    //   0  InvoiceYYYYMM     1  FSChar     2  StartNo    3  InvoiceBook    4  InvoiceStartNo
                                    //   5  InvoiceEndNo      6  MaxInvoiceNo
                                    retSale05M085  =  getSale05M085(retSale05M080,stringCompanyCd,stringInvoiceKind) ;
                                    for(int  intSale05M085=0  ;  intSale05M085<retSale05M085.length  ;  intSale05M085++) {
                                        stringInvoiceYYYYMM  =  retSale05M085[intSale05M085][0].trim( ) ;
                                        stringFSChar               =  retSale05M085[intSale05M085][1].trim( ) ;
                                        stringStartNo                =  retSale05M085[intSale05M085][2].trim( ) ;
                                        stringInvoiceBook         =  retSale05M085[intSale05M085][3].trim( ) ;
                                        stringInvoiceStartNo     =  retSale05M085[intSale05M085][4].trim( ) ;
                                        stringInvoiceEndNo      =  retSale05M085[intSale05M085][5].trim( ) ;
                                        stringMaxInvoiceNo      =  retSale05M085[intSale05M085][6].trim( ) ;
                                        //發票號碼
                                        if(stringMaxInvoiceNo.length( )  ==  0) {
                                            stringInvoiceNo  =  stringInvoiceStartNo ;
                                        } else  {
                                            intInvoiceNo       =  doParseInteger(stringMaxInvoiceNo.substring(2,10))  +  1 ;
                                            stringInvoiceNo  =  stringMaxInvoiceNo.substring(0,2)  +  convert.add0(("" + intInvoiceNo),"8") ;
                                        }
                                      }
                                      //System.out.println("------------------------發票號碼："+stringInvoiceNo) ;
                                    //發票已開完Flag 
                                    if(stringInvoiceNo.equals(stringInvoiceEndNo))
                                        stringEndYes = "Y";
                                    else
                                        stringEndYes = "N";
                                    //發票訊息 
                                    stringInvoiceMessage  +=  stringInvoiceNo  +  "\n" ;
                                }
                                // 存入 DB
                                doubleInvoiceTotalMoney  +=  doParseDouble(stringInvoiceTotalMoney)  ;
                                if(intRecord % 5 == 0  ||  !booleanNextNotNull) {
                                    doubleInvoiceTotalMoney  +=  doubleDiscountMoney  ;
                                    if(intNo  ==  4  ||  intNo  ==  6) {
                                        doubleInvoiceMoney  =  (doubleInvoiceTotalMoney / 1.05) ;
                                    } else {
                                        doubleInvoiceMoney  =  doubleInvoiceTotalMoney ;
                                    }
                                    doubleInvoiceTax       =  doubleInvoiceTotalMoney  -  doubleInvoiceMoney;
                                    stringPointNo  =  arrayPointNo[intNo-4].trim( ) ;
                                    //
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "M51A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H51A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H50A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ;
                                    } 
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H71A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ; //990603
                                    }                                         
                                    // 2010/10/14
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H80A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ; 
                                    } 
                                    //1001018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H68A".equals(stringProjectID1)  &&  ("2102".equals(stringPointNo)  ||  "2104".equals(stringPointNo))) {
                                        stringPointNo  =  "2802" ; 
                                    }                                     
                                    //2012/05/21 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H102A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H102A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2013/09/04 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H75A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H75A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2012/09/05 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H117A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H117A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2014/04/08 B3018
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H118A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H118A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2014-05-15 B3018 START
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H76A".equals(stringProjectID1)  &&  "2102".equals(stringPointNo)) {
                                        stringPointNo  =  "2802" ; //2802    受託代銷-代收土地款  2102    土地款
                                    }
                                    if(vectorLCom.indexOf(stringLCom)  != -1  &&  "H76A".equals(stringProjectID1)  &&  "2104".equals(stringPointNo)) {
                                        stringPointNo  =  "2803" ; //2803    受託代銷-代收車土款  2104    車位款─土地 
                                    }
                                    //2014-05-15 END
                                    // 2010/03/15
                                    stringTaxKind  =  (""+hashtableTaxKind.get(stringPointNo)).trim( ) ;
                                    if("".equals(stringTaxKind)  ||  "null".equals(stringTaxKind)) {
                                         retInvoM010  =  getInvoM010(stringPointNo) ;
                                         if(retInvoM010.length  ==  0) {
                                            
                                         } else {
                                            hashtableTaxKind.put(stringPointNo,  retInvoM010[0][1].trim( )) ;
                                            stringTaxKind  =  retInvoM010[0][1].trim( ) ;
                                         }
                                    }
                                    doInvertInvoM030(stringInvoiceNo,                                                 stringInvoiceKind,                                                          stringPosition,  
                                                   stringCustomNo,                                                 stringPointNo,                                                               convert.FourToFive(""+doubleInvoiceMoney,  0),
                                                   convert.FourToFive(""+doubleInvoiceTax,  0),  convert.FourToFive(""+doubleInvoiceTotalMoney,0),  stringTaxKind,
                                                   stringCompanyCd,                                             retSale05M080,                                                              "0",
                                                   0) ;
                                    doubleInvoiceMoney          =  0 ;
                                    doubleInvoiceTotalMoney  =  0 ;
                                    doubleInvoiceTax               =  0  ;
                                    intDiscountCountInvoice    =  0 ;
                                    doInvertInvoM0C0(stringCustomNo,  stringOrderNo);
                                }
                                /*發票.InvoM031(Body)(多筆)*/
                                if(doParseInteger(stringORDER_NO)<=  0)stringORDER_NO   =  "" ;
                                
                                doInvertInvoM031(stringInvoiceNo,  intRecord,  stringITEMLS_CHINESE,  stringORDER_NO) ;
                                //System.out.println("---------------"+stringORDER_NO+"-----------") ;
                                //
                                if(intRecord==intRecordMax)  {
                                    doInvertInvoM031(stringInvoiceNo,  intRecord+1,  "消費券",  "") ;
                                }
                                if(intRecord  %  5  ==  1) {
                                    doInsertSale05M087(stringDocNo,  intRecordNo,  stringInvoiceNo,  "Y") ;
                                    intRecordNo++ ;
                                }
                                //發票. UPDATE Sale05M085(發票管控檔)
                                if(intRecord  %  5  ==  1) {
                                    doUpdateSale05M085(stringInvoiceNo,            stringEDate,                  stringEndYes,
                                                      stringCompanyCd,        stringInvoiceYYYYMM,  stringFSChar,  
                                                      stringStartNo,         stringInvoiceBook) ;
                                }
                            }//
                        }
                        //System.out.println("消費卷--------------------E") ;
                    }
                }
            }
            // 暫存檔 vectorSale05M061Sql
            if(vectorSale05M061Sql.size( )  >  0)
                dbSale.execFromPool((String[])  vectorSale05M061Sql.toArray(new  String[0])) ;
            // 
            for(int  intCompanNo=0  ;  intCompanNo<vectorCompanyCd.size( )  ;  intCompanNo++) {
                stringCompanyCd           =  ""  +  (String)  vectorCompanyCd.get(intCompanNo) ;
                retSale05M085UPDATE  =  getSale05M085(stringCompanyCd) ;
                for(int intSale05M085UPDATE = 0 ;intSale05M085UPDATE < retSale05M085UPDATE.length;intSale05M085UPDATE++){
                    doUpdateInvoM022(retSale05M085UPDATE[intSale05M085UPDATE],  stringCompanyCd) ;
                }
            }
            // 更改折讓(Sale05M089)
            String      stringRecordNo    =  "" ;
            String       stringSql              =  "" ;
            String[][]  retInvoM041         =  null ;
            String[][]  retSale05M089    =  getSale05M089(stringDocNo) ;
            Vector     vectorSql              =  new  Vector( ) ;
            //  0  DiscountNo       1  InvoiceNo
            for(int  intNo=0  ;  intNo<retSale05M089.length  ;  intNo++) {
                stringDiscountNo             =  retSale05M089[intNo][0].trim() ;
                stringInvoiceNo                =  retSale05M089[intNo][1].trim() ;
                stringInvoiceTotalMoney  =  getInvoiceTotalMoney(stringInvoiceNo) ;
                //
                if("".equals(stringInvoiceTotalMoney))  continue ;
                
                stringSql             =  "UPDATE  InvoM041  SET  InvoiceTotalMoney  =  "  +  stringInvoiceTotalMoney  +  
                                 " WHERE  DiscountNo  =  '"  +  stringDiscountNo  +  "' "  +
                                       " AND  InvoiceNo  =  '"     +  stringInvoiceNo     +  "' " ;
                vectorSql.add(stringSql) ;
                
            }
            if(vectorSql.size()  >  0)dbInvoice.execFromPool((String[])  vectorSql.toArray(new  String[0])) ;
            //
            doInsert_Sale05M080_Flow_HIS( ) ;  // 歷史檔記錄(可多筆)  Primary Key 為 DocNo、FlowStatus、EDateTime。
            doInsert_Sale05M080_Flow( ) ;         // 歷史檔記錄               Primary Key 為 DocNo、FlowStatus、EDateTime。
            doUpdate_Sale05M080( ) ;
            
            //發票開立成功，傳送簡訊，再回跳發票訊息 (正式環境才發簡訊)
            if( "prod".equals(serverKind) ) {
              getButton("ButtonCellphone").doClick() ;
            }
            
            //20190117 大額通報處理判斷 BEGIN
            //doBLCASH(stringDocNo);
            //20190117 大額通報處理判斷 FINISH
            JOptionPane.showMessageDialog(null,  stringInvoiceMessage,"發票 開立成功! 產生發票訊息", JOptionPane.INFORMATION_MESSAGE); 
            //
        }
        //
        //showForm("收款單(Sale05M080)");
        setValue("FlowStatus",stringFlowStatus);
        getButton("button3").setLabel("簽核流程:[" + stringFlowStatus + "]");
        //getInternalFrame("購屋簽核").setClosed(true);
        return value;
    }
    
    //2019/02/13 == 處理大額通報資料
    public void doBLCASH(String docNo) throws  Throwable {
      String stringSql = "";
      String updateSql = "";
      String[][] retSale05M080Data = null;
      String[][] retSale05M084Data = null;
      //
      stringSql = " SELECT ProjectID1, EDate, CashMoney "  + 
                  " FROM Sale05M080 "  +
                " WHERE DocNo = '"  + docNo.trim( )  +  "' AND NoticeBLCASH = 'N'";
      retSale05M080Data = dbSale.queryFromPool(stringSql);
      
      double limitMaxAmt = 0.0d;
      if(retSale05M080Data.length > 0 && Double.parseDouble(retSale05M080Data[0][2]) > limitMaxAmt) {
        String nowDateTime = datetime.getTime("YYYY/mm/dd h:m:s");
        nowDateTime = nowDateTime.replaceAll("/", "-");       
        String stringUserID = getUser() ;
        String stringDeptID = "5800800";
        int tradeAmt = (int) Double.parseDouble(retSale05M080Data[0][2]);       
        stringSql = "SELECT CustomNo FROM Sale05M084 WHERE DocNo = '" + docNo.trim() + "' ORDER BY RecordNo";
        retSale05M084Data = dbSale.queryFromPool(stringSql);
        
        //準備通報
        String cntDate = retSale05M080Data[0][1].trim().replaceAll("/", "-")+" 00:00:00";
        String trnType = "不動產收款訂金";
        String ntCode = "TWN";
        String stringCreator = "RealtySale";
        //deptNo = 5800800
        //撈取 max CASH_ID
        String[][] cashID = null;
        stringSql = "SELECT max(CASH_ID) as max_cash_id FROM BLCASH";
        cashID = dbAs400.queryFromPool(stringSql);
        
        for(int i = 0; i < retSale05M084Data.length; i++) {
          int insertCashID = Integer.parseInt(cashID[0][0]) + 1 + i;
          String insertSql = "INSERT INTO BLCASH "+
                        "(CASH_ID,INP_EMPNO,INP_DEPT,KEY_NO,CASE_NO,CNT_DATE,TRN_TYPE,CLIENT_ID,CLIENT_NAT,AGENT_ID,AGENT_NAT,TRN_DATE,TRN_AMOUNT,IS_VALID,CREATOR,CREATETIME) "+
                        "VALUES "+
                        "("+insertCashID+",'"+stringUserID+"','"+stringDeptID+"','"+docNo.trim()+"','"+retSale05M080Data[0][0]+"','"+cntDate+"','"+trnType+"','"+retSale05M084Data[i][0]+"','"+ntCode+"','','','"+cntDate+"',"+tradeAmt+",'Y','"+stringCreator+"','"+nowDateTime+"')";
          dbAs400.execFromPool(insertSql);
        }
        
        //更新通報時間        
        updateSql = "UPDATE Sale05M080 SET NoticeBLCASH = 'Y', NoticeBLCASHdtime = '"+nowDateTime+"' WHERE DocNo = '" + docNo.trim() + "'";
        dbSale.execFromPool(updateSql);
      }
    }
    
    // 公司 START
    // stringStatus 表示須執行回復之數目
    public  void  doInvoM022Undo(Vector  vectorCompanyCd,  int  intCompanySize) throws  Throwable {
        String      stringCompanyCd            =  "" ;
        String[][]  retSale05M085UPDATE  =  null ;
        //
        if(intCompanySize  >=  vectorCompanyCd.size( ))  return ;
        //System.out.println("回復 InvoM022 之狀態  START------------------------------------------------------------"+intCompanySize) ;
        for(int  intCompanNo=0  ;  intCompanNo<intCompanySize  ;  intCompanNo++) {
            stringCompanyCd           =  ""  +  (String)  vectorCompanyCd.get(intCompanNo) ;
            retSale05M085UPDATE  =  getSale05M085(stringCompanyCd) ;
            for(int intSale05M085UPDATE = 0 ;intSale05M085UPDATE < retSale05M085UPDATE.length;intSale05M085UPDATE++){
                doUpdateInvoM022Undo(retSale05M085UPDATE[intSale05M085UPDATE],  stringCompanyCd) ;
            }
        }
        doDeleteSale05M085( ) ;
        //System.out.println("回復 InvoM022 之狀態  END------------------------------------------------------------") ;
    }
    public  String  doStringSubstring(String  stringObject,  int  intStart,  int  intEnd) {
        String  stringRet  =  "" ;
        // intStart
        if(stringObject.length( )  <  intStart)  return  stringRet ;
        intStart  =  (intStart  <  0) ?  0 :  intStart ;
        // intEnd
        if(intEnd  <  0)  return  stringRet ;
        intEnd  =  (stringObject.length( )  <  intEnd) ?  stringObject.length( ) :  intEnd ;
        stringRet  =  stringObject.substring(intStart,  intEnd) ;
        return  stringRet ;
    }
    public  int  doParseInteger(String  stringNum) {
        System.out.println("stringNum>>>" + stringNum) ;
        int  intNum  =  0 ;
        if("".equals(stringNum)  ||  "null".equals(stringNum))  return  0;
        try{
            intNum  =  Integer.parseInt(stringNum) ;
        } catch(Exception e) {
                System.out.println("無法剖析["  +  stringNum  +  "]，回傳 0。") ;
            return  0 ;
        }
        return  intNum ;
    }
    public  float  doParseFloat(String  stringNum) {
        // 
        float  floatNum  =  0 ;
        if("".equals(stringNum)  ||  "null".equals(stringNum))  return  0;
        try{
            floatNum  =  Float.parseFloat(stringNum) ;
        } catch(Exception e) {
                System.out.println("無法剖析["  +  stringNum  +  "]，回傳 0。") ;
            return  0 ;
        }
        return  floatNum ;
    }
    public  double  doParseDouble(String  stringNum) {
        // 
        double  doubleNum  =  0 ;
        if("".equals(stringNum)  ||  "null".equals(stringNum))  return  0;
        try{
            doubleNum  =  Double.parseDouble(stringNum) ;
        } catch(Exception e) {
                System.out.println("無法剖析["  +  stringNum  +  "]，回傳 0。") ;
            return  0 ;
        }
        return  doubleNum ;
    }
    // hashtableCustomCompanyPositionTypeCount  依 KEY 值計算總發票項目    顧客+公司+棟樓別+型態
    // hashtableInvoiceTotalCount                              依 KEY 值計算總發票張數  公司+發票格式
    public  void  doCount(int  intPos,  int  intType,  String  stringCustomNo,  String  stringInvoiceKind,  String[][]  retSale05M040,
                      Hashtable  hashtableCustomCompanyPositionTypeCount,  Hashtable  hashtableInvoiceTotalCount) throws  Throwable {
        int        intCount                     =  0 ;
        int        intInvoiceTotalCount  =  0 ;
        String  stringKey                   =  "" ;
        String  stringPosition             =  "" ;
        String  stringCompanyCd      =  "" ;
        //
        stringPosition         =  retSale05M040[intPos][2] ;
        stringCompanyCd  =  (intType==0  ||  intType==2) ?  retSale05M040[intPos][0].trim( ) : retSale05M040[intPos][1].trim( ) ;
        stringKey               =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  intType ;
        intCount                 =  doParseInteger(""  +  (String)  hashtableCustomCompanyPositionTypeCount.get(stringKey)) ;
        intCount++ ;
        //System.out.println("hashtableCustomCompanyPositionTypeCount-------------"+stringKey+"------------"+intCount) ;
        hashtableCustomCompanyPositionTypeCount.put(stringKey,  ""  +  intCount) ;
        if(intCount  %  5  ==  0)  {
            stringKey                   =  stringCompanyCd  +  stringInvoiceKind ;
            intInvoiceTotalCount  =  doParseInteger(""  +  (String)  hashtableInvoiceTotalCount.get(stringKey))  +  1;
            //System.out.println("hashtableInvoiceTotalCount-------------"+stringKey+"------------"+intInvoiceTotalCount) ;
            hashtableInvoiceTotalCount.put(stringKey,  ""  +  intInvoiceTotalCount) ;
        }
    }
    public  void  doCount2(int  intType,  String  stringCustomNo,  String  stringInvoiceKind,  String  stringCompanyCd,  String  stringPosition,  
                         Hashtable  hashtableCustomCompanyPositionTypeCount,  Hashtable  hashtableInvoiceTotalCount) throws  Throwable {
        int        intCount                     =  0 ;
        int        intInvoiceTotalCount  =  0 ;
        String  stringKey                   =  "" ;
        //
        stringKey               =  stringCustomNo  +  stringCompanyCd  +  stringPosition  +  intType ;
        intCount                 =  doParseInteger(""  +  (String)  hashtableCustomCompanyPositionTypeCount.get(stringKey)) ;
        //System.out.println("hashtableCustomCompanyPositionTypeCount-------------"+stringKey+"------------"+intCount) ;
        if(intCount  %  5  !=  0)  {
            stringKey                   =  stringCompanyCd  +  stringInvoiceKind ;
            intInvoiceTotalCount  =  doParseInteger(""  +  (String)  hashtableInvoiceTotalCount.get(stringKey))  +  1;
            //System.out.println("hashtableInvoiceTotalCount-------------"+stringKey+"------------"+intInvoiceTotalCount) ;
            hashtableInvoiceTotalCount.put(stringKey,  ""  +  intInvoiceTotalCount) ;
        }
    }
    public  Vector  getCompanys(String  stringDocNo,  String  stringProjectID1) throws  Throwable {
        //
        String          stringSql                                 =  "" ;
        String          stringHCompany                     =  ""  ;
        String          stringLCompany                     =  ""  ;
        String          stringPosition                         =  "" ;
        String          stringStatus                           =  "OK" ;
        String          stringLCom                            = "" ;
        String          stringHCom                            = "" ;
        String[]         arraySale05M040                 =  null ;
        String[]        arrayHCom                            =  null ;
        String[]        arrayLCom                            =  null ;
        String[][]      retSale05M040                      =  null ;
        Vector         vectorResult                          =  new  Vector( ) ;
        Vector         vectorCompanyCd                =  new  Vector( ) ;
        Vector         vectorPosition                       =  new  Vector( ) ;
        Vector         vectorUniquePosition            =  new  Vector( ) ;
        Vector         vectorCompanyCdPerson     =  new  Vector( ) ;
        Vector         vectorSale05M040                =  new  Vector( ) ;
        Hashtable   hashtaleHCompanyPositioin  =  new  Hashtable( ) ;
        Hashtable   hashtaleLCompanyPositioin  =  new  Hashtable( ) ;
        //
        vectorCompanyCdPerson.add("1") ;
        vectorCompanyCdPerson.add("K") ;
        vectorCompanyCdPerson.add("L") ;
        vectorCompanyCdPerson.add("P") ; //990609
        vectorCompanyCdPerson.add("U") ; //2010/10/14
        vectorCompanyCdPerson.add("4") ; //1001019
        vectorCompanyCdPerson.add("A5") ; //2014-05-15 B3018
        // 0  H_Com       1 L_Com         2  Position
        //System.out.println("getSale05M040----------------------------") ;
        retSale05M040  =  getSale05M040(stringDocNo,  stringProjectID1) ;
        for(int  intSale05M040=0  ;  intSale05M040<retSale05M040.length  ;  intSale05M040++) {
            arraySale05M040  =  new  String[4] ;
            stringHCom          =  retSale05M040[intSale05M040][0].trim( ) ;
            stringLCom          =  retSale05M040[intSale05M040][1].trim( ) ;
            stringHCompany  =  getACom(stringHCom) ;
            if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "M51A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H51A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H50A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;               
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H71A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //990603                          
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H80A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //2010/10/14
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H68A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //1001018           
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H102A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //2012/04/30 B3018
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H75A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //2013/09/04 B3018
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H76A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //2014-5-15 B3018
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H117A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //2013/09/05 B3018
            }else if(vectorCompanyCdPerson.indexOf(stringLCom)  !=  -1  &&  "H118A".equals(stringProjectID1)) {
                stringLCompany  =  stringHCompany ;     //2014/04/08 B3018
            } else {
                stringLCompany  =   getACom(stringLCom) ;
            }
            
            //if("".equals(stringHCompany)  ||  "".equals(stringLCompany))  stringStatus  =  "NULL" ;
            stringPosition      =  retSale05M040[intSale05M040][2].trim( ) ;
            //
            arraySale05M040[0]  =  stringHCompany ;
            arraySale05M040[1]  =   stringLCompany ;
            arraySale05M040[2]  =   stringPosition ;
            arraySale05M040[3]  =   stringLCom ;
            vectorPosition.add(stringPosition) ;
            if(vectorUniquePosition.indexOf(stringPosition)  ==  -1) {
              vectorUniquePosition.add(stringPosition) ;
            }
            if(!"".equals(stringHCompany)  &&  vectorCompanyCd.indexOf(stringHCompany)  ==  -1) {
              //20180109 add by FGlife-B03812  01 類別(建設)不處理
              if("CS".equals(stringHCompany)) {
                vectorCompanyCd.add(stringHCompany) ;
              }
            }
            if(!"".equals(stringLCompany)  &&  vectorCompanyCd.indexOf(stringLCompany)  ==  -1) {
              //20180109 add by FGlife-B03812  01 類別(建設)不處理
              if("CS".equals(stringLCompany)) {
                vectorCompanyCd.add(stringLCompany) ;
              }
            }
            vectorSale05M040.add(arraySale05M040) ;
        }
        vectorResult.add(vectorCompanyCd) ;  // 不重覆
        vectorResult.add(vectorPosition) ;
        vectorResult.add((String[][])  vectorSale05M040.toArray(new  String[0][0])) ;
        vectorResult.add(vectorUniquePosition) ;// 不重覆
        vectorResult.add(stringStatus) ;
        return  vectorResult ;
    }
    public  String[][]  getSale05M040(String  stringDocNo,  String  stringProjectID1) throws  Throwable {
        String      stringSql            =  "" ;
        String[][]  retSale05M040  =  null ;
        // 0  H_Com       1 L_Com         2  Position
        stringSql  =  "SELECT  DISTINCT  T040.H_Com, T040.L_Com,  T040.Position "  +
                       " FROM  Sale05M086 T86,  Sale05M092 T92,  Sale05M081 T81,  Sale05M091 T91,  Sale05M040 T040 "  +
                     " WHERE  T86.DocNo  =  '"  +  stringDocNo  +  "' "  +
                     " AND  T92.OrderNo  =  T86.OrderNo " +
                     " AND  T81.DocNo  =  T86.DocNo "  + 
                     " AND  T81.Position  =  T92.Position "  +
                     " AND  ISNULL(T92.StatusCd,  '')  <>  'D' "  +
                     " AND  T92.OrderNo  =  T91.OrderNo "  +
                     " AND  ISNULL(T91.StatusCd,  '')  <>  'C' "  +
                     " AND  T81.Position  =  T040.Position "  +  //
                     " AND  T81.HouseCar  =  T040.HouseCar "  +  //
                     " AND  T040.ProjectID1  =  '"  +  stringProjectID1  +"' "  ;
        retSale05M040  =  dbSale.queryFromPool(stringSql);
        return  retSale05M040 ;
    }
    public  String  getACom(String  stringComNo) throws  Throwable {
        String      stringSql                 =  "" ;
        String      stringCompanyCd  =  "" ;
        String[][]  retACom                =  null ;
        // 
        stringSql  =  "SELECT  COMPANY_CD " +
                     " FROM  A_COM "  +
                   " WHERE  Com_No  =  '"  +  stringComNo  +  "' " ;
        retACom  =  dbSale.queryFromPool(stringSql);
        if(retACom.length  !=  0) {
            stringCompanyCd  =  retACom[0][0].trim( ) ;
        }
        return  stringCompanyCd ;
    }
    public  String  getCompanyName(String  stringCompanyCd) throws  Throwable {
        String      stringSql                 =  "" ;
        String      stringCompanyName  =  "" ;
        String[][]  retACom                =  null ;
        // 
        stringSql  =  "SELECT  Com_Name " +
                     " FROM  A_COM "  +
                   " WHERE  COMPANY_CD  =  '"  +  stringCompanyCd  +  "' " ;
        retACom  =  dbSale.queryFromPool(stringSql);
        if(retACom.length  !=  0) {
            stringCompanyName  =  doStringSubstring(retACom[0][0].trim( ),  0,  6) ;
        }
        return  stringCompanyName ;
    }
    // 公司 END
    // 折讓單 START
    // 取得折讓單號碼
    // 0  CompanyNo     1  DepartNo       2  ProjectID1       3  EDate
    public  String  getDiscountNo(String  stringCompanyCd,  String[][]  retSale05M080) throws  Throwable {
        //
        String      stringSql                =  "" ;
        String      stringDiscountNo   =  "" ;
        String      stringmaxNo           =  "" ;
        //String      stringCompanyNo  =  "" ;
        String      stringDepartNo       =  "" ;
        String      stringEDate            =  "" ;
        String      stringYM                 =  "" ;
        String       stringProjectID1    =  "" ;
        String[][]  retInvoM040           =  null ;
        for(int intSale05M080 = 0 ;intSale05M080 < retSale05M080.length;intSale05M080++){
            //stringCompanyNo  =  retSale05M080[intSale05M080][0].trim();
            stringDepartNo      =  retSale05M080[intSale05M080][1].trim();
            stringProjectID1     =  retSale05M080[intSale05M080][2].trim();
            stringEDate           =   retSale05M080[intSale05M080][3].trim(); 
            stringYM                =  (Integer.parseInt(stringEDate.substring(0,4))-1911)  +  stringEDate.substring(5,7) ;
        }
        //
        stringSql  =  "SELECT  MAX(DiscountNo) "  +
                   " FROM  InvoM040 "  +
                   " WHERE  CompanyNo  =  '"  +  stringCompanyCd  +  "' "  +
                         " AND  DepartNo  =  '"  +  stringDepartNo  +  "' "  +
                     " AND  SUBSTRING(DiscountDate, 1, 7)  =  SUBSTRING('"  +  stringEDate  +  "' , 1, 7) " ;
        retInvoM040  =  dbInvoice.queryFromPool(stringSql) ;
        //if (retInvoM040.length  >  0){    
        if (retInvoM040[0][0].length()>0){    
               stringmaxNo  =  retInvoM040[0][0];
               stringmaxNo  =  "000"  +  (Integer.parseInt(stringmaxNo.substring(stringmaxNo.length()-3)) + 1);
               stringmaxNo  =  stringmaxNo.substring(stringmaxNo.length()  -  3); 
               stringDiscountNo        =  stringCompanyCd  +  convert.add0("",  ""+(5-stringDepartNo.length()))  +  stringDepartNo  +  stringYM  +  stringmaxNo ;
        }else{
               stringDiscountNo        =  stringCompanyCd  +  convert.add0("",  ""+(5-stringDepartNo.length()))  +  stringDepartNo  +  stringYM  +  "001" ;
        }  
        //System.out.println("------------------------折讓單號："+stringDiscountNo) ;
        return  stringDiscountNo ;
    }
    // 0  CompanyNo     1  DepartNo       2  ProjectID1       3  EDate
    public  void  doInsertInvoM040(String  stringDiscountNo,  String  stringCustomNo,  String  stringCompanyCd,  String[][]  retSale05M080,  double  doubleDiscountMoney) throws  Throwable {
         String    stringEDate                =  "" ;
         String    stringProjectID1         =  "" ;
         String    stringCompanyNo       =  "" ;
         String    stringDepartNo           =  "" ;
         String    stringSql                     =  "" ;
         String  stringDiscountMoney    =  convert.FourToFive(""+doubleDiscountMoney,0) ;
         String  stringInvoiceMoney       =  "" ;
         String  stringInvoiceTax            =  "" ;
         double  doubleInvoiceMoney   =  doubleDiscountMoney  /  1.05 ;
         double  doubleInvoiceTax        =  doubleDiscountMoney  -  doubleInvoiceMoney ;
        for(int intSale05M080 = 0 ;intSale05M080 < retSale05M080.length;intSale05M080++){
            stringCompanyNo  =  retSale05M080[intSale05M080][0].trim();
            stringDepartNo      =  retSale05M080[intSale05M080][1].trim();
            stringProjectID1     =  retSale05M080[intSale05M080][2].trim();
            stringEDate           =   retSale05M080[intSale05M080][3].trim(); 
        }
         //
         stringInvoiceMoney  =  convert.FourToFive(""+doubleInvoiceMoney,0) ;
         stringInvoiceTax       =  convert.FourToFive(""+doubleInvoiceTax,0) ;
         //
        stringSql  =  "INSERT  INTO  InvoM040 ( DiscountNo,     DiscountDate,              CompanyNo,         DepartNo,                           ProjectNo, "  +
                                        " HuBei,              CustomNo,                    DiscountWay,       NewHuBeiORCustomNo,  DiscountMoney, "  +
                                        " DiscountTax,   DiscountTotalMoney,  PrintYes,               PrintTimes,                         DELYes, "  +
                                        " LuChangYes,  EmployeeNo,                ModifyDateTime,  ProcessDiscountNo ) "  +
                                 " VALUES ( '"  +  stringDiscountNo                                        +  "', "  +  // 折讓單號碼
                                          " '"  +  stringEDate                                                +  "', "   +  // 折讓單日期
                                                " '"  +  stringCompanyCd                                      +  "', "   +  // 公司代碼
                                          " '"  +  stringDepartNo                                          +  "', "   +  // 部門代碼
                                          " '"  +   stringProjectID1                                        +  "', "    +  // 案別代碼            4
                                          " '"  +  ""                                                               +  "', "   +  // 戶別代碼
                                          " '"  +   stringCustomNo                                         +  "', "    +  // 客戶代碼(統編)
                                          " '"  +  "B"                                                             +  "', "   +  // 折讓方式
                                          " '"  +  ""                                                                +  "', "   +  // 戶別代碼 or 客戶代碼
                                               stringInvoiceMoney                                     +  ", "    +  // 折讓未稅金額       9
                                               stringInvoiceTax                                          +  ", "    +  // 折讓稅額
                                                 stringDiscountMoney                                   +  ", "    +  // 折讓總金額
                                          " '"  +  "N"                                                              +  "', "   +  // 已列印(Y/N)
                                                 "0"                                                              +  ", "   +  // 補印次數
                                         " '"  +  "N"                                                              +  "', "   +  // 作癈                     14
                                         " '"  +  "N"                                                              +  "', "   +  // 入帳
                                         " '"  +  getUser( )                                                    +  "', "  +  // 修改人
                                         " '"  +  datetime.getTime("YYYY/mm/dd h:m:s")  +  "', "  +  // 修改時間
                                         " '"   +  "1"                                                              +  "') "  ;  //                                18
        //System.out.println("doInsertInvoM040-----------------"+stringSql) ;
        dbInvoice.execFromPool(stringSql);  
    }
    public  String[][]  getInvoM041(String  stringDiscountNo) throws  Throwable {
          String       stringSql        =  "" ;
          String[][]   retInvoM041  =  null ;
          //  0  DiscountNo       1  RecordNo
          stringSql  =  "SELECT  DiscountNo,  RecordNo "  +
                               " FROM  InvoM041 "  +
                     " WHERE  DiscountNo  =  '"  +  stringDiscountNo  +  "' " ;
          //System.out.println("getInsertInvoM041-----------------"+stringSql) ;
          retInvoM041  =  dbInvoice.queryFromPool(stringSql);
          return  retInvoM041 ;
    }
    public  void  doInsertInvoM041(String  stringDiscountNo,           int  intDiscountCount,  String  stringInvoiceNo,  String  stringITEMLS_CHINESE, 
                              String  stringInvoiceTotalMoney) throws  Throwable {
          stringInvoiceTotalMoney           =  convert.FourToFive(""+stringInvoiceTotalMoney,0) ;
          double  doubleInvoiceMoney   =  Double.parseDouble(stringInvoiceTotalMoney)  /  1.05 ;
          double  doubleInvoiceTax        =  Double.parseDouble(stringInvoiceTotalMoney)  -  doubleInvoiceMoney ;
          String    stringSql                     =  "" ;
          String    stringInvoiceMoney     =  convert.FourToFive(""+doubleInvoiceMoney,0) ;
          String    stringInvoiceTax         =  convert.FourToFive(""+doubleInvoiceTax,0) ;
          //
          stringSql  =  "INSERT  INTO  Invom041  ( DiscountNo,       RecordNo,   ChoiceYES,                 InvoiceNo,               PointNo, "  +
                                           " InvoiceMoney,  InvoiceTax,  InvoiceTotalMoney,  YiDiscountMoney,  DiscountItemMoney) "  +
                                     " VALUES ( '"  +  stringDiscountNo                      +  "', "   + // 折讓單號碼
                                                   intDiscountCount                     +  ", "   +  // 筆數
                                           " '"  +  "Y"                                          +  "', "  +  // 勾選
                                           " '"  +  stringInvoiceNo                       +  "', "   +  // 發票號碼
                                           " '"  +  stringITEMLS_CHINESE         +  "', "   +  // 摘要代碼
                                                stringInvoiceMoney                +  ", "    +  // 發票未稅額
                                                stringInvoiceTax                     +  ", "    +  // 發票稅額
                                                stringInvoiceTotalMoney        +  ", "    +  // 發票總金額
                                                "0"                                         +  ", "    +  // 已折讓金額 
                                                stringInvoiceTotalMoney        +  ") "   ;    // 折讓金額
          //System.out.println("doInsertInvoM041-----------------"+stringSql) ;
          dbInvoice.execFromPool(stringSql);
    }
    // 折讓單 END
    public  String  getItemlsChinese(String  stringITEMLS_CD) throws  Throwable {
        //
        String      stringSql                   =  "" ;
        String      stringItemlsChinese  =  "" ;
        String[][]  retSale05M052         =  null ;
        //
        stringSql   = " SELECT  ITEMLS_CHINESE " + 
                    " FROM  Sale05M052 "  +
                  " WHERE  ITEMLS_CD  =  '"  +  stringITEMLS_CD  +  "' " ;
        retSale05M052  =  dbSale.queryFromPool(stringSql) ;
        if(retSale05M052.length  !=  0) {
            stringItemlsChinese  =  retSale05M052[0][0].trim( ) ;
        }
        return  stringItemlsChinese ;
    }
    public  void  doUpdateSale05M061(int        intInvoice,                   String  stringReceiveMoney,  String  stringProjectID1,
                                   String   stringHouseCar,         String  stringPosition,             String  stringORDER_NO) throws  Throwable {
        String  stringSql  =  " UPDATE Sale05M061 ";
        //房屋款
        if (intInvoice  ==  1) {
            stringSql  +=  " SET  H_ReceiveMoney = H_ReceiveMoney + "    + stringReceiveMoney + "," +
                                  " HL_ReceiveMoney = HL_ReceiveMoney + " + stringReceiveMoney ;
        }
        //土地款   
        if (intInvoice == 2){     
            stringSql  +=  " SET  L_ReceiveMoney = L_ReceiveMoney + "     + stringReceiveMoney + "," +
                            " HL_ReceiveMoney = HL_ReceiveMoney + " + stringReceiveMoney ;
        }
        stringSql  +=  " WHERE  ProjectID1  =  '" + stringProjectID1 +  "' "  +
                         " AND  HouseCar  =  '"  + stringHouseCar  +  "' "  +
                       " AND  Position  =  '"     + stringPosition     +  "' "  +
                         " AND  ORDER_NO  =  " +  stringORDER_NO ;
        //System.out.println("doUpdateSale05M061-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql);
    }
    public  Vector  doUpdateSale05M061(String  stringH_MomentaryMoney,  String  stringL_MomentaryMoney,  String  stringProjectID1,
                                      String  stringHouseCar,                   String  stringPosition,                     String  stringORDER_NO,
                                    Vector  vectorSale05M061Sql) throws  Throwable {
        String  stringSql  =  " UPDATE  Sale05M061  SET  H_MomentaryMoney  =  H_MomentaryMoney  + "  + stringH_MomentaryMoney  +  ", "  +
                                                " L_MomentaryMoney  =  L_MomentaryMoney  +  " + stringL_MomentaryMoney + "," +  
                                                              " HL_MomentaryMoney = H_MomentaryMoney + L_MomentaryMoney " +
                          " WHERE  ProjectID1  =  '"   +  stringProjectID1  +  "' "  +
                            " AND  HouseCar  =  '"   +  stringHouseCar   +  "' "  +
                            " AND  Position  =  '"      +  stringPosition      +  "' "  +
                            " AND  ORDER_NO  =  "  +  stringORDER_NO ;
        //System.out.println("doUpdateSale05M061(2)-----------------------------------"+stringSql) ;
        vectorSale05M061Sql.add(stringSql) ;
        return  vectorSale05M061Sql ;
    }
    // 歷史檔記錄
    // Primary Key 為 DocNo、FlowStatus、EDateTime。
    public  void  doInsert_Sale05M080_Flow_HIS( ) throws  Throwable {
        String  stringSql  =  "" ;
        //
        stringSql  = " INSERT  INTO  Sale05M080_Flow_HIS (DocNo,  FlowStatus,  EmployeeNo,  EDateTime,  Opinion) "  +
                                                                 " VALUES  ( N'"  +  getValue("DocNo").trim( )                         +  "', "  +
                                                       " N'"  +  getValue("FlowStatus").trim( )                 +  "', "  +                  
                                                     " N'"  +  getUser()                                                    +  "', "  +
                                                     " N'"  +  datetime.getTime("YYYY/mm/dd h:m:s") + "', "   +
                                                     " N'"  +  getValue("Opinion").trim( )                       + "' ) " ;
        //System.out.println("doInsert_Sale05M080_Flow_HIS-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql);
    }
    // 歷史檔記錄
    // Primary Key 為 DocNo、FlowStatus、EDateTime。
    public  void  doInsert_Sale05M080_Flow( ) throws  Throwable {
        String      stringSql  =  "" ;
        String[][]  retSale05M080_Flow  =  null ;
        //
        stringSql   = " SELECT  Opinion "  + 
                    " FROM  Sale05M080_Flow "  +
                  " WHERE  DocNo  =  '"  +  getValue("DocNo").trim()  + "' "  +
                      " AND  FlowStatus  =  '"  +  getValue("FlowStatus").trim( )  +  "' " ;
        retSale05M080_Flow  =  dbSale.queryFromPool(stringSql);
        if(retSale05M080_Flow.length  ==  0) {
            stringSql   = " INSERT  INTO  Sale05M080_Flow  (DocNo,  FlowStatus,  EmployeeNo,  EDateTime,  Opinion)  " +
                                                              " VALUES  ( N'"  +  getValue("DocNo").trim( )                          +  "', "  +
                                                              " N'"  +  getValue("FlowStatus").trim( )                  +  "', "  +                  
                                                    " N'"  +  getUser( )                                                    +  "', "  +
                                                    " N'"  +  datetime.getTime("YYYY/mm/dd h:m:s")  +  "', "  +
                                                    " N'"  +  getValue("Opinion").trim( )                        +  "') " ;
            dbSale.execFromPool(stringSql);                
        }
        else{
            stringSql   = " UPDATE  Sale05M080_Flow  SET  EmployeeNo  =  N'"  +  getUser( )  +  "', "  +
                                                                         " EDateTime  =  N'"     +  datetime.getTime("YYYY/mm/dd h:m:s")  +  "', "  +
                                                   " Opinion  =  N'"          +  getValue("Opinion").trim() + "' " +
                       " WHERE  DocNo  =  '"  +  getValue("DocNo").trim( )  +  "' "  +
                           " AND  FlowStatus  =  '"  +  getValue("FlowStatus").trim( )  +  "' " ;
            dbSale.execFromPool(stringSql);                
        }
        //System.out.println("doInsert_Sale05M080_Flow-----------------------------------"+stringSql) ;
    }
    // 更新狀態  
    // Primary Key：DocNo
    public  void  doUpdate_Sale05M080( ) throws  Throwable {
        String  stringSql = " UPDATE  Sale05M080  SET  FlowStatus  =  N'"      +  getValue("FlowStatus")       +  "', "  +
                                                                                    " SendMessage  =  '"  +  getValue("SendMessage")  +  "' "  +
                             " WHERE  DocNo  =  '"  +  getValue("DocNo").trim( )  +  "' " ;
        //System.out.println("doUpdate_Sale05M080( )-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql) ;
    }
    //  0  CompanyNo      1  DepartNo     2  ProjectID1       3  EDate
    public  String[][]  getSale05M080Tmp( ) throws  Throwable {
        String      stringSql            =  "" ;
        String[][]  retSale05M080  =  null ;
        //
//        stringSql   = " SELECT  CompanyNo,  DepartNo,  ProjectID1,  EDate "  +
          stringSql   = " SELECT  CompanyNo,  DepartNo,  ProjectID1,  PaperEDate "  +   //20201211 kyle : 主要使用發票日期 
                              " FROM  Sale05M080 "  +
                  " WHERE  DocNo  =  '"  +  getValue("DocNo").trim( )  +  "' " ;
        retSale05M080  =  dbSale.queryFromPool(stringSql) ;
        return  retSale05M080 ;
    }
    // 取得特定客戶、特定購屋證明單之收款單明細 
    // DocNo、HouseCar、[Position]、ORDER_NO
    public  String[][]  getSale05M081(String  stringDocNo,  String  stringOrderNo,  String  stringCustomNo) throws  Throwable {
        //
        String      stringSql            =  "" ;
        String[][]  retSale05M081  =  null ;
        //   0  HouseCar                     1  Position                  2  ORDER_NO               3  ITEMLS_CD
        //   4  H_MomentaryMoney     5  H_UsableMoney     6  H_ReceiveMoney       7  (H_ReceiveMoney + H_UsableMoney) AS H_InvoiceMoney
        //   8  L_MomentaryMoney     9  L_UsableMoney    10  L_ReceiveMoney     11  (L_ReceiveMoney + L_UsableMoney) AS L_InvoiceMoney
          // 12 H_DiscountMoney        13  H_FeeMoney        14  L_FeeMoney 
        stringSql  =  "SELECT  T81.HouseCar,              T81.Position,         ORDER_NO,              ITEMLS_CD, " + 
                          " H_MomentaryMoney,  H_UsableMoney,  H_ReceiveMoney,  (H_ReceiveMoney + H_UsableMoney) AS H_InvoiceMoney, "  + 
                          " L_MomentaryMoney,   L_UsableMoney,  L_ReceiveMoney,   (L_ReceiveMoney + L_UsableMoney) AS L_InvoiceMoney, "  +
                          " H_DiscountMoney,      H_FeeMoney,       L_FeeMoney "  +
                       " FROM  Sale05M086 T86,  Sale05M092 T92,  Sale05M081 T81,  Sale05M091 T91 "  +
                     " WHERE  T86.DocNo  =  '"  +  stringDocNo  +  "' "  +
                       " AND  T92.OrderNo  =  '"  +  stringOrderNo  +  "' "  +
                     " AND  T92.OrderNo  =  T86.OrderNo " +
                     " AND  T81.DocNo  =  T86.DocNo "  + 
                     " AND  T81.Position  =  T92.Position "  +
                     " AND  ISNULL(T92.StatusCd,  '')  <>  'D' "  +
                     " AND  T92.OrderNo  =  T91.OrderNo "  +
                     " AND  T91.customNo  =  '"  +  stringCustomNo  +  "' " +
                      " AND  ISNULL(T91.StatusCd,  '')  <>  'C' "  +
                " ORDER BY  T92.OrderNo,  T92.Position,  T92.HouseCar DESC, T81.ORDER_NO " ;
        retSale05M081  =  dbSale.queryFromPool(stringSql);
        return  retSale05M081 ;
    }
    // 取得特定客戶之收款單明細 
    // DocNo、HouseCar、[Position]、ORDER_NO
    public  String[][]  getSale05M081ForCustomNo(String  stringDocNo,  String  stringCustomNo) throws  Throwable {
        //
        String      stringSql            =  "" ;
        String[][]  retSale05M081  =  null ;
        // 1  HouseCar                     2  Position                  3  ORDER_NO               4  ITEMLS_CD
        // 5  H_MomentaryMoney     6  H_UsableMoney     7  H_ReceiveMoney       8  (H_ReceiveMoney + H_UsableMoney) AS H_InvoiceMoney
        // 9  L_MomentaryMoney   10  L_UsableMoney    11  L_ReceiveMoney     12  (L_ReceiveMoney + L_UsableMoney) AS L_InvoiceMoney "  +
        stringSql  =  "SELECT  DISTINCT  T81.Position "  +
                       " FROM  Sale05M086 T86,  Sale05M092 T92,  Sale05M081 T81,  Sale05M091 T91 "  +
                     " WHERE  T86.DocNo  =  '"  +  stringDocNo  +  "' "  +
                     " AND  T92.OrderNo  =  T86.OrderNo " +
                     " AND  T81.DocNo  =  T86.DocNo "  + 
                     " AND  T81.Position  =  T92.Position "  +
                     " AND  ISNULL(T92.StatusCd,  '')  <>  'D' "  +
                     " AND  T92.OrderNo  =  T91.OrderNo "  +
                     " AND  T91.customNo  =  '"  +  stringCustomNo  +  "' " +
                      " AND  ISNULL(T91.StatusCd,  '')  <>  'C' "  +
                " ORDER BY  T81.Position " ;
        retSale05M081  =  dbSale.queryFromPool(stringSql);
        return  retSale05M081 ;
    }
    public  String[][]  getSale05M084( ) throws  Throwable {
        // 
        String      stringSql            =  "" ;
        String[][]  retSale05M084  =  null ;
        //  0  CustomNo
        stringSql  =  " SELECT  DISTINCT  CustomNo,  Nationality " + 
                    " FROM  Sale05M084 " + 
                  " WHERE  DocNo  =  '"  +  getValue("DocNo").trim( )  +  "' ";
        retSale05M084  =  dbSale.queryFromPool(stringSql);
        return  retSale05M084 ;
    }
    // 0  CustomNo    1  Percentage
    public  String  getSale05M091ForPercentage(String  stringOrderNo,  String  stringCustomNo) throws  Throwable {
        String      stringSql                =  "" ;
        String      stringPercentage  =  "0" ;
        String[][]  retSale05M091     =  null ;
        //
          stringSql  =  "SELECT  CustomNo,  Percentage " +
                         " FROM  Sale05M091 T91 " +
                    " WHERE  OrderNo  =  '"  +  stringOrderNo  +  "' "  +
                     " AND  CustomNo  =  '"  +  stringCustomNo  +  "' "  +
                     " AND  ISNULL(T91.StatusCd,  '')  <>  'C' " ;    
        retSale05M091  =  dbSale.queryFromPool(stringSql) ;
        if(retSale05M091.length  !=  0) {
            stringPercentage  =  retSale05M091[0][1].trim( ) ;
        }
        return  stringPercentage ;
    }
    public  String  getSale05M091ForNationality(String  stringOrderNo,  String  stringCustomNo) throws  Throwable {
        String      stringSql                =  "" ;
        String      stringNationality    =  "0" ;
        String[][]  retSale05M091     =  null ;
        //
          stringSql  =  "SELECT  Nationality " +
                         " FROM  Sale05M091 T91 " +
                    " WHERE  OrderNo  =  '"  +  stringOrderNo  +  "' "  +
                     " AND  CustomNo  =  '"  +  stringCustomNo  +  "' "  +
                     " AND  ISNULL(T91.StatusCd,  '')  <>  'C' " ;    
        retSale05M091  =  dbSale.queryFromPool(stringSql) ;
        if(retSale05M091.length  !=  0) {
            stringNationality  =  retSale05M091[0][0].trim( ) ;
        }
        return  stringNationality ;
    }
    // Primary Key：DocNo、CompanyNo、InvoiceYYYYMM、FSChar、StartNo、InvoiceBook
    public  void  doDeleteSale05M085( ) throws  Throwable {
        String  stringSql  =  " DELETE  FROM  Sale05M085 " + 
                             " WHERE  DocNo  =  '"  + getValue("DocNo").trim( )  +  "' " ;
        //System.out.println("doDeleteSale05M085-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql);
    }
    // Primary Key：DocNo、CompanyNo、InvoiceYYYYMM、FSChar、StartNo、InvoiceBook
    // retSale05M080
    // 0  CompanyNo     1  DepartNo     2  ProjectID1       3  EDate
    // retInvoM022
    // 0  InvoiceYYYYMM   1  FSChar                2  StartNo             3  InvoiceBook      4  InvoiceStartNo
    // 5  InvoiceEndNo           6  MaxInvoiceNo    7  MaxInvoiceDate
    public  void  doInsertSale05M085(String  stringCompanyCd,  String[]  retInvoM022,String stringInvoiceKind) throws  Throwable {
        String  stringSql  =  " INSERT  INTO Sale05M085 (DocNo,            CompanyNo,        InvoiceYYYYMM,  FSChar,              StartNo, "  +
                                                 " InvoiceBook,  InvoiceStartNo,  InvoiceEndNo,  InvoiceKind,     MaxInvoiceNo,  MaxInvoiceDate, "  +
                                                 " EndYES ) " +
                                                  " VALUES (N'"  +  getValue("DocNo").trim( )  +  "', "  +
                                                          " N'"  +  stringCompanyCd                +  "', "  +
                                              " N'"  +  retInvoM022[0].trim( )          +  "', "  +
                                              " N'"  +  retInvoM022[1].trim( )          +  "', "  +
                                              " N'"  +  retInvoM022[2].trim( )          +  "', "  +
                                              " N'"  +  retInvoM022[3].trim( )          +  "', "  +
                                              " N'"  +  retInvoM022[4].trim( )          +  "', "  +
                                              " N'"  +  retInvoM022[5].trim( )          +  "', "  +     
                                              " N'"  +  stringInvoiceKind                +  "', "  +
                                              " N'"  +  retInvoM022[6].trim( )          +  "', "  +
                                              " N'"  +  retInvoM022[7].trim( )          +  "', "  +
                                              " N'N')" ;
        //System.out.println("doInsertSale05M085-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql);
    }
    // 發票號碼自動產生
    // retSale05M080
    // 0  CompanyNo     1  DepartNo     2  ProjectID1       3  EDate
    public  String[][]  getSale05M085(String[][]  retSale05M080,String  stringCompanyCd,String stringInvoiceKind) throws  Throwable {
        String      stringSql            =  "" ;
        String      stringEDate       =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][3].trim( ) :  "" ;
        String[][]  retSale05M085  =  null ;
        //   0  InvoiceYYYYMM     1  FSChar     2  StartNo    3  InvoiceBook    4  InvoiceStartNo
        //   5  InvoiceEndNo      6  MaxInvoiceNo
        stringSql = " SELECT  TOP 1  InvoiceYYYYMM,  FSChar,  StartNo,  InvoiceBook,  InvoiceStartNo, "  + 
                               " InvoiceEndNo,  MaxInvoiceNo "  +
                  " FROM  Sale05M085 " + 
                  " WHERE  DocNo  =  '"  +  getValue("DocNo").trim( )  +  "' "  +
                      " AND  (MaxInvoiceDate <= '" + stringEDate + "'  OR  MaxInvoiceDate IS NULL) " +
                    " AND  EndYES  =  'N'  AND  CompanyNo  =  '"  +  stringCompanyCd  +  "'  and  InvoiceKind = '" + stringInvoiceKind + "' " +
              " ORDER BY  InvoiceStartNo " ;
        retSale05M085  =  dbSale.queryFromPool(stringSql); 
        return  retSale05M085 ;
    }
    public  String[][]  getSale05M085(String  stringCompanyCd) throws  Throwable {
        String      stringSql                          =  "" ;
        String[][]  retSale05M085UPDATE  =  null ;
        //
        stringSql  =  " SELECT  InvoiceYYYYMM,  FSChar,  StartNo,  InvoiceBook,  MaxInvoiceNo, "  +
                           " MaxInvoiceDate,   EndYES " + 
                      " FROM  Sale05M085 " + 
                    " WHERE  DocNo  =  '"  +  getValue("DocNo").trim( )  +  "' "  +  
                        " AND  CompanyNo  =  '"  +  stringCompanyCd  +  "' " ;
          retSale05M085UPDATE  =  dbSale.queryFromPool(stringSql) ;
          return  retSale05M085UPDATE ;
    }
    public void  doUpdateSale05M085(String  stringInvoiceNo,            String  stringEDate,                  String  stringEndYes,
                                  String  stringCompanyCd,        String  stringInvoiceYYYYMM,  String  stringFSChar,  
                                  String  stringStartNo,            String  stringInvoiceBook) throws  Throwable {
        String  stringSql  =  " UPDATE  Sale05M085  SET  MaxInvoiceNo  =  '"     +  stringInvoiceNo  +  "', " +
                                                       " MaxInvoiceDate  =  '"  +  stringEDate        +  "', " +
                                                       " EndYES  =  '"                 +  stringEndYes     +  "' "  +                     
                         " WHERE  DocNo  =  '"                    +  getValue("DocNo").trim( )  +  "' "  +
                               " AND  CompanyNo  =  '"          +  stringCompanyCd                +  "' "  +
                             " AND  InvoiceYYYYMM  =  '"  +  stringInvoiceYYYYMM         +  "' "  +
                             " AND  FSChar  =  '"                   +  stringFSChar                      +  "' "  +
                             " AND  StartNo  =  '"                   +  stringStartNo                      +  "' "  +                  
                             " AND  InvoiceBook  =  '"          +  stringInvoiceBook               +  "' " ;
        //System.out.println("doUpdateSale05M085-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql) ;
    }
    // 取得特定客戶的購屋證明單
    public  String[][]  getSale05M086(String  stringDocNo,  String  stringCustomNo) throws  Throwable {
        String      stringSql            =  "" ;
        String[][]  retSale05M086  =  null ;
        //
        stringSql  =  " SELECT  DISTINCT  T86.OrderNo " + 
                    " FROM  Sale05M086 T86,  Sale05M092 T92,  Sale05M081 T81,  Sale05M091 T91 "  +
                  " WHERE  T86.DocNo  =  '"  +  stringDocNo  +  "' "  +
                        " AND  T81.DocNo  =  T86.DocNo " + 
                      " AND  T92.OrderNo  =  T86.OrderNo " +
                      " AND  T81.Position  =  T92.Position " + 
                      " AND  ISNULL(T92.StatusCd,  '')  <>  'D' "  +
                      " AND  T92.OrderNo  =  T91.OrderNo "  +
                      " AND  T91.customNo  =  '"  +  stringCustomNo  +  "' " +
                      " AND  ISNULL(T91.StatusCd,  '')  <>  'C' " ;
        //System.out.println("getSale05M086-----------------------------------"+stringSql) ;
        retSale05M086  =  dbSale.queryFromPool(stringSql) ;
        return  retSale05M086 ;
    }
    // 取得同一個產品在同一個購屋證明單內，有幾位顧客並同分擔
    public  int  getSale05M086Count(String  stringDocNo,  String  stringCustomNo,  String  stringPosition) throws  Throwable {
        String      stringSql            =  "" ;
        String[][]  retSale05M086  =  null ;
        //
        stringSql  =  " SELECT  DISTINCT  T91.CustomNo " + 
                    " FROM  Sale05M086 T86,  Sale05M092 T92,  Sale05M081 T81,  Sale05M091 T91 "  +
                  " WHERE  T86.DocNo  =  '"  +  stringDocNo  +  "' "  +
                        " AND  T81.DocNo  =  T86.DocNo " + 
                      " AND  T92.OrderNo  =  T86.OrderNo " +
                      " AND  T92.OrderNo  =  T91.OrderNo "  +
                      " AND  T81.Position  =  T92.Position " + 
                      " AND  T81.Position  =  '"  +  stringPosition  +  "' "  +
                      " AND  ISNULL(T92.StatusCd,  '')  <>  'D' "  +
                      " AND  ISNULL(T91.StatusCd,  '')  <>  'C' " ;
        //System.out.println("getSale05M086Count-----------------------------------"+stringSql) ;
        retSale05M086  =  dbSale.queryFromPool(stringSql) ;
        return  retSale05M086.length ;
    }
    // 檢核是否存在
    // DocNo、RecordNo
    public  String  isCheckSale05M087Exist( ) throws  Throwable {
        String      stringSql              =  "" ;
        String      stringInvoiceNo1  =  "" ;
        String[][]  retSale05M087    =  null ;
        //
        stringSql  =  " SELECT  TOP 1  InvoiceNo "  + 
                    " FROM  Sale05M087 "  +
                  " WHERE  DocNo  =  '"  +  getValue("DocNo").trim( )  +  "' " ;
        retSale05M087  =  dbSale.queryFromPool(stringSql);
        if(retSale05M087.length  !=  0) {
            stringInvoiceNo1  =  retSale05M087[0][0].trim( ) ;
        }
        return  stringInvoiceNo1 ;
    }
    public  void  doInsertSale05M087(String  stringDocNo,  int  intRecordNo,  String  stringInvoiceNo) throws  Throwable {
        doInsertSale05M087(stringDocNo,  intRecordNo,  stringInvoiceNo,  "N") ;
    }
    public  void  doInsertSale05M087(String  stringDocNo,  int  intRecordNo,  String  stringInvoiceNo,  String  stringFeeYes) throws  Throwable {
        String  stringSql  =  "INSERT  INTO  Sale05M087 (DocNo,  RecordNo,  InvoiceNo,  FeeYes,  DELYes) " +
                                             " VALUES ( '"  +  stringDocNo       +  "', "  +
                                                               intRecordNo       +  ", "   +
                                                        " '"  +  stringInvoiceNo  +  "', "  +
                                                " '"  +  stringFeeYes      +  "', "  +
                                                " N'N') " ;

        //System.out.println("doInsertSale05M087-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql);
    }
    //
    public  String[][]  getSale05M089(String  stringDocNo) throws  Throwable {
        String[][]  retSale05M089  =  null ;
        String      stringSql            =  "" ;
        //  0  DiscountNo       1  InvoiceNo
        stringSql  =  "SELECT   DiscountNo,  InvoiceNo " +
                             " FROM  Sale05M089 "  +
                   " WHERE  DocNo  =  '"  +  stringDocNo  +  "' " ;
        //System.out.println("getSale05M089-----------------------------------"+stringSql) ;
        retSale05M089  =  dbSale.queryFromPool(stringSql);
        return  retSale05M089 ;
    }
    public  void  doInvertSale05M089(String  stringDocNo,  int  intRecordNo,  String  stringDiscountNo,  String  stringInvoiceNo) throws  Throwable {
        String  stringSql  =  "INSERT  INTO  Sale05M089 (DocNo,  RecordNo,  DiscountNo,  InvoiceNo) " +
                                             " VALUES ( '"  +  stringDocNo       +  "', "  +
                                                               intRecordNo       +  ", "   +
                                                        " '"  +  stringDiscountNo  +  "', "  +
                                                " '"  +  stringInvoiceNo  +  "') " ;
        //System.out.println("doInvertSale05M089-----------------------------------"+stringSql) ;
        dbSale.execFromPool(stringSql);
    }
    //
    public  String[][]  getInvoM010(String  stringPointNo) throws  Throwable {
        String      stringSql  =  "" ;
        String[][]  retInvoM010  =  null ;
        //  0  TaxRate   1  TaxKind
        stringSql   = "SELECT  TaxRate,  TaxKind "  +
                   " FROM  InvoM010 " +
                   " WHERE  PointNo  =  '"  +  stringPointNo  +  "' " ;
        retInvoM010  =  dbInvoice.queryFromPool(stringSql);
        return  retInvoM010 ;
    }
    // Primary Key：CompanyNo、InvoiceYYYYMM、FSChar、StartNo、InvoiceBook
    public  String[][]  getInvoM022(String[][]  retSale05M080,  String  stringInvoiceKind,  String  stringCompanyCd) throws  Throwable {
        // 
        String      stringSql                =  "" ;
        String      stringCompanyNo  =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][0].trim( ) :  "" ;
        String      stringDepartNo      =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][1].trim( ) :  "" ;
        String      stringProjectID1     =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][2].trim( ) :  "" ;
        String      stringEDate           =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][3].trim( ) :  "" ;
        String[][]  retInvoM022  =  null ;
        //  0  InvoiceYYYYMM    1  FSChar                2  StartNo             3  InvoiceBook      4  InvoiceStartNo
        //  5  InvoiceEndNo           6  MaxInvoiceNo     7  MaxInvoiceDate
        stringSql = " SELECT  TOP 2  InvoiceYYYYMM,  FSChar,              StartNo,                InvoiceBook,  InvoiceStartNo, " + 
                                   " InvoiceEndNo,       MaxInvoiceNo,  MaxInvoiceDate " + 
                      " FROM  InvoM022 " + 
                    " WHERE  CompanyNo  =  '"     +   stringCompanyCd  +  "' "  +
                          " AND  DepartNo  =  '"         +  stringDepartNo  +  "' "  +
                        " AND  ProjectNo  =  '"        +  stringProjectID1  +  "' "  +
                        " AND  InvoiceKind  =  '"     +  stringInvoiceKind  +  "' "  +
                        " AND  UseYYYYMM  =  '"  +  stringEDate.substring(0,7)  +  "' "  +
                        " AND  (MaxInvoiceDate <= '"  +  stringEDate  +  "'  OR  MaxInvoiceDate IS NULL)" +
                        " AND  ENDYES  =  'N' " +
                        " AND  CloseYes  =  'N' " +
                        " AND  ProcessInvoiceNo  =  '1' " + 
                   " ORDER BY  InvoiceStartNo " ;
         System.out.println("getRetInvoM022: "+stringSql);
        retInvoM022  =  dbInvoice.queryFromPool(stringSql);
        return  retInvoM022 ;
    }
    // Primary Key：CompanyNo、InvoiceYYYYMM、FSChar、StartNo、InvoiceBook
    // retSale05M080
    // 0  CompanyNo     1  DepartNo     2  ProjectID1       3  EDate
    // retInvoM022
    // 0  InvoiceYYYYMM   1  FSChar                2  StartNo             3  InvoiceBook      4  InvoiceStartNo
    // 5  InvoiceEndNo           6  MaxInvoiceNo    7  MaxInvoiceDate
    public  void  doUpdateInvoM022(String  stringCompanyCd,  String[]  retInvoM022) throws  Throwable {
        //
        String  stringSql                = "" ;
        //
        stringSql   = " UPDATE  InvoM022  SET  CloseYes  =  N'Y' " + 
                   " WHERE  CompanyNo  =  '"           +  stringCompanyCd  +  "' "  +
                         " AND  InvoiceYYYYMM  =  '"  +  retInvoM022[0].trim( )  +  "' "  +
                       " AND  FSChar  =  '"                   +  retInvoM022[1].trim( )  +  "' "  +
                       " AND  StartNo  =  '"                   +  retInvoM022[2].trim( )  +  "' "  +                   
                       " AND  InvoiceBook  =  '"          +  retInvoM022[3].trim( )  +  "' " ;
        //System.out.println("doUpdateInvoM022-----------------------------------"+stringSql) ;
        dbInvoice.execFromPool(stringSql) ;
    }
    public  void  doUpdateInvoM022(String[]  retSale05M085UPDATE,  String  stringCompanyCd) throws  Throwable {
        String  stringSql = " UPDATE  InvoM022  SET  MaxInvoiceNo  =  '"     +  retSale05M085UPDATE[4].trim( )  +  "', "  +
                                                         " MaxInvoiceDate  =  '"  +  retSale05M085UPDATE[5].trim( )  +  "', "  +
                                                         " EndYES  =  '"                 +  retSale05M085UPDATE[6].trim( )  +  "', "  +
                                                         " CloseYes  =  'N' "  +                    
                        " WHERE  CompanyNo  =  '"           +  stringCompanyCd                          +  "' "  +
                            " AND  InvoiceYYYYMM  =  '"  +  retSale05M085UPDATE[0].trim( )  +  "' "  +
                            "  AND  FSChar  =  '"                   +  retSale05M085UPDATE[1].trim( )  +  "' "  +
                          " AND  StartNo  =  '"                   +  retSale05M085UPDATE[2].trim( )  +  "' "  +                  
                            " AND  InvoiceBook  =  '"          +  retSale05M085UPDATE[3].trim( )  +  "' " ;
        //System.out.println("doUpdateInvoM022(2)-----------------------------------"+stringSql) ;
        dbInvoice.execFromPool(stringSql) ;
    }
    public  void  doUpdateInvoM022Undo(String[]  retSale05M085UPDATE,  String  stringCompanyCd) throws  Throwable {
        String  stringSql = " UPDATE  InvoM022  SET  CloseYes  =  'N' "  +
                        " WHERE  CompanyNo  =  '"           +  stringCompanyCd                          +  "' "  +
                            " AND  InvoiceYYYYMM  =  '"  +  retSale05M085UPDATE[0].trim( )  +  "' "  +
                            "  AND  FSChar  =  '"                   +  retSale05M085UPDATE[1].trim( )  +  "' "  +
                          " AND  StartNo  =  '"                   +  retSale05M085UPDATE[2].trim( )  +  "' "  +                  
                            " AND  InvoiceBook  =  '"          +  retSale05M085UPDATE[3].trim( )  +  "' " ;
        //System.out.println("doUpdateInvoM022(2)-----------------------------------"+stringSql) ;
        dbInvoice.execFromPool(stringSql) ;
    }
    ////發票.InvoM030(Head)
    // retSale05M080
    public  String  getInvoiceTotalMoney(String  stringInvoiceNo) throws  Throwable {
        String       stringSql                           =  "" ;
        String       stringInvoiceTotalMoney  =  "" ;
        String[][]   retInvoM030                     =  null ;
        //
        stringSql        =  "SELECT  InvoiceTotalMoney  FROM  InvoM030  WHERE  InvoiceNo  =  '"  +  stringInvoiceNo  +  "' " ;
        retInvoM030  =  dbInvoice.queryFromPool(stringSql);
        if(retInvoM030.length  >  0)  stringInvoiceTotalMoney  =  retInvoM030[0][0].trim() ;
        return  stringInvoiceTotalMoney ;
    }
    
    //寫入發票主檔
    // 0  CompanyNo     1  DepartNo     2  ProjectID1       3  EDate
    public  void  doInvertInvoM030(String     stringInvoiceNo,      String  stringInvoiceKind,             String   stringPosition,  
                                                       String      stringCustomNo,     String  stringPointNo,                   String  stringInvoiceMoney,
                               String      stringInvoiceTax,    String  stringInvoiceTotalMoney,  String   stringTaxKind,
                               String      stringCompanyCd,  String[][]  retSale05M080,            String  stringL_DiscountMoney,
                               int            intDiscountCountInvoice) throws  Throwable {
        //String  stringCompanyNo  =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][0].trim( ) :  "" ;
        String  stringDepartNo      =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][1].trim( ) :  "" ;
        String  stringProjectID1     =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][2].trim( ) :  "" ;
        String  stringEDate    =  retSale05M080.length!=0 ?  retSale05M080[retSale05M080.length-1][3].trim( ) :  "" ;      //發票日
        String  strInvoiceTime = datetime.getTime("h:m:s") ;                                                               //發票時間
        String  stringDateTime = datetime.getTime("YYYY/mm/dd h:m:s") ;                                                    //發票開立時 系統時間
        String  stringUserID           =  getUser() ;
        String  customName = mapCustomers.get(stringCustomNo) !=null? mapCustomers.get(stringCustomNo).toString():"";
        Random r1 = new Random();
        
        //依照2020/12/11電子發票會議紀錄:
        //1.若發票日非開立當日，則開立時間加一小時
        //2.若加一小時後大於等於24點，則以23點計
        if( !stringDateTime.split(" ")[0].trim().equals(stringEDate) ) {
          String[] arrTmpTime = strInvoiceTime.split(":");
          int tmpTimeH = (Integer.parseInt(arrTmpTime[0].trim()) + 1) >=24 ? 23:(Integer.parseInt(arrTmpTime[0].trim()) + 1);
          //tip : 數字計算後，小於10的0會不見，要再補回來
          strInvoiceTime = kUtil.add0(tmpTimeH, 2, "F") + ":" + arrTmpTime[1].trim() + ":" + arrTmpTime[2].trim(); 
        }
        
        String  stringSql       =  " INSERT  INTO  InvoM030 (InvoiceNo,                InvoiceDate, InvoiceTime,  InvoiceKind,              CompanyNo,  DepartNo, "  +
                                                            " ProjectNo,                InvoiceWay,   Hubei,                         CustomNo,     PointNo, "  +
                                                            " InvoiceMoney,         InvoiceTax,    InvoiceTotalMoney,  TaxKind,         DisCountMoney,  "  +
                                                            " DisCountTimes,        PrintYes,       PrintTimes,                 DELYes,          LuChangYes ," +
                                                            " ProcessInvoiceNo,  Transfer,       CreateUserNo,           CreateDateTime,  LastUserNo, "  +
                                                            " LastDateTime, RandomCode, CustomName) " +
                                " VALUES ( '"  +  stringInvoiceNo                +  "', "  +
                                       " '"  +  stringEDate                     +  "', "  +
                                       " '"  +  strInvoiceTime                    +  "', "  +
                                       " '"  +  stringInvoiceKind             +  "', "  +
                                       " '"  +  stringCompanyCd            +  "', "  +
                                       " '"  +  stringDepartNo                +  "', "  +  // 04
                                       " '"  +  stringProjectID1               +  "', "  +
                                       " 'A', " +
                                       " '"  +  stringPosition                    +  "', "  +
                                       " '"  +  stringCustomNo                +  "', "  +
                                       " '"  +  stringPointNo                    +  "', "  +  // 09
                                            stringInvoiceMoney           +  ", "  +
                                            stringInvoiceTax                +  ", "  +
                                            stringInvoiceTotalMoney   +  ", "  +
                                       " '"  +  stringTaxKind                    +  "', "  +
                                                   stringL_DiscountMoney    +  ", " + // 14
                                                   intDiscountCountInvoice   +  ", " +
                                      " 'N', " +
                                      " 0, " +
                                      " 'N', " +
                                      " 'N', " +
                                      " '1', " +
                                      " '收款', " +
                                      " '"  +  stringUserID                    +  "', "  +
                                      " '"  +  stringDateTime               +  "', "  +
                                      " '"  +  stringUserID                 +  "', "  +
                                      " '"  +  stringDateTime               +  "', "  + 
                                      " '"  +  kUtil.add0(r1.nextInt(9999), 4, "F")  +  "', "  +
                                      " '"  +  customName                   +  "' "  +
                                      "  ) "  ;
        //System.out.println("InvoM030-------------"  +  stringSql) ;
        dbInvoice.execFromPool(stringSql);

        //------------------2020/11/06 Kyle Add support 電子發票
        //寫入AS400
        //A : 主檔
        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("insert into "+GENLIB+".GLEAPFUF ");
        sbSQL.append("(EA01U, EA02U, EA03U, EA04U, EA05U, EA06U, EA07U, EA08U, EA09U, EA10U, EA11U, EA12U, EA13U, EA14U, EA15U, EA16U, EA17U, EA18U, EA19U, EA20U, EA21U, EA22U) ");
        sbSQL.append("values ");
        sbSQL.append("(");
        sbSQL.append("'").append(stringInvoiceNo).append("', ");         //發票號碼
        sbSQL.append("'").append( stringEDate ).append("', ");           //發票日期
        sbSQL.append("'").append( stringInvoiceKind ).append("', ");     //發票聯式
        sbSQL.append("'").append( stringCompanyCd ).append("', ");       //公司代碼
        sbSQL.append("'").append( stringDepartNo ).append("', ");        //部門代碼
        sbSQL.append("'").append( stringProjectID1 ).append("', ");      //案別代碼
        sbSQL.append("'").append( "A" ).append("', ");                   //Invoice Way
        sbSQL.append("'").append( stringPosition ).append("', ");        //戶別代號
        sbSQL.append("'").append( stringCustomNo ).append("', ");        //客戶代號
        sbSQL.append("'").append( stringPointNo ).append("', ");         //摘要
        sbSQL.append("").append( stringInvoiceMoney ).append(", ");      //未稅
        sbSQL.append("").append( stringInvoiceTax ).append(", ");        //稅額
        sbSQL.append("").append( stringInvoiceTotalMoney ).append(", "); //含稅
        sbSQL.append("'").append( stringTaxKind ).append("', ");         //稅別
        sbSQL.append("").append( stringL_DiscountMoney ).append(", ");             //已折讓金額
        sbSQL.append("").append( intDiscountCountInvoice ).append(", ");             //已折讓次數
        sbSQL.append("'").append("N").append("', ");         //已列印YN
        sbSQL.append("").append(0).append(", ");             //補印次數
        sbSQL.append("'").append("N").append("', ");         //作廢YN
        sbSQL.append("'").append("N").append("', ");         //入帳YN
        sbSQL.append("'").append("").append("', ");          //發票處理方式
        sbSQL.append("'").append("收款").append("' ");       //收款/客服
        sbSQL.append(") ");
        dbAs400.execFromPool(sbSQL.toString());
        //------------------2020/11/06 Kyle Add support 電子發票
    }
    
    //寫入發票細項(Body)
    public  void  doInvertInvoM031(String  stringInvoiceNo,  int  intRecord,  String  stringITEMLS_CHINESE,  String  stringORDER_NO) throws  Throwable {
        intRecord  =  (intRecord  %  5  ==  0)  ?  5  :  (intRecord  %  5) ;
        //
        String  stringTemp  =  code.StrToByte(stringITEMLS_CHINESE) ;
        if(stringTemp.length()  >  24) {
            stringITEMLS_CHINESE  =  code.ByteToStr(stringTemp.substring(0,24)) ;
        }
        //
        String  stringSql  = " INSERT  INTO  InvoM031 (InvoiceNo,  RecordNo,  DetailItem,  Remark) " +
                                              " VALUES ( '"  +  stringInvoiceNo  +  "',  "  +
                                                                  intRecord           +  ", "  +    
                                                     " '"  +  stringITEMLS_CHINESE  +  "', "  + 
                                               //" '"  +  stringITEMLS_CHINESE.substring(0,5)  +  "', "  +  
                                                     " '"  +  stringORDER_NO  +  "') " ;
        //System.out.println("InvoM031-------------"  +  stringSql) ;
        dbInvoice.execFromPool(stringSql);
    }
    
    /**
     * 寫入客戶，一次一筆
     * 
     * @param stringCustomNo 
     * @param stringORDER_NO
     * @throws Throwable
     */
    public  void  doInvertInvoM0C0(String  stringCustomNo,  String  stringORDER_NO) throws  Throwable {
      System.out.println("寫入客戶>>>" + stringCustomNo);
      String  stringSql =  "" ;
      String  stringCustomName = mapCustomers.get(stringCustomNo) !=null? mapCustomers.get(stringCustomNo).toString().trim():"";
      String  stringNationality = getSale05M091ForNationality(stringORDER_NO,  stringCustomNo) ;     
      String[][] retInvoM0C0 = null;
      stringSql = " SELECT  CustomName from InvoM0C0 where CustomNo='" + stringCustomNo+ "' " ;
      retInvoM0C0  =  dbInvoice.queryFromPool(stringSql);
      if( retInvoM0C0.length == 0 && !"".equals(stringCustomName) ) {
        stringSql  = " INSERT  INTO  InvoM0C0 (CustomNo,  CustomName,  Transfer,  Nationality) "
                   + " values ('" + stringCustomNo+ "',N'" + stringCustomName+ "','Y',  '"+stringNationality+"')";
        String ret = dbInvoice.execFromPool(stringSql);
      }
      
      //------------------2020/11/06 Kyle Add support 電子發票
      //400一定寫
      //400 D : 客戶檔
      StringBuilder sbSQL = new StringBuilder();
      sbSQL.append("select ED01U from "+GENLIB+".GLEDPFUF where ED01U = '" + stringCustomNo + "' ");
      String[][] arrGLEDPFUF = dbAs400.queryFromPool(sbSQL.toString());
      if(arrGLEDPFUF.length == 0 && !"".equals(stringCustomName)) {
	    //400特殊需求，小於六個字要補滿到六個全形
		if(stringCustomName.length() < 6) stringCustomName = kUtil.addWhat(stringCustomName, 6, "　", 1);
		
        sbSQL = new StringBuilder();
        sbSQL.append("insert into "+GENLIB+".GLEDPFUF ");
        sbSQL.append("(ED01U, ED02U) ");
        sbSQL.append("values ");
        sbSQL.append("(");
        sbSQL.append("'").append( stringCustomNo ).append("', ");
        sbSQL.append("'").append( stringCustomName ).append("' ");
        sbSQL.append(") ");
        dbAs400.execFromPool(sbSQL.toString());
      }
      //------------------2020/11/06 Kyle Add support 電子發票
    }
    
    //取得訂單客戶(較準)
    public Map getSale05M091Users() throws  Throwable {
      Map rsMap = new HashMap();
      StringBuilder sql = new StringBuilder("select customNo , customName from Sale05M091 a ");
      sql.append("where a.orderno in (select b.orderNo from Sale05M086 b where b.DocNo='" + getValue("DocNo").trim() + "')");
      String[][] retSQL = dbSale.queryFromPool(sql.toString());
      if(retSQL.length > 0) {
        for(int i=0 ; i<retSQL.length ; i++) {
          rsMap.put(retSQL[i][0].trim(), retSQL[i][1].trim());
        }
      }
      return rsMap;
    }
    
    public  String  getInformation( ) {
        return "---------------OK(存檔).defaultValue()----------------";
    }
}
