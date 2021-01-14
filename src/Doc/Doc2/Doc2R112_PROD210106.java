package  Doc.Doc2 ;
import      jcx.jform.bTransaction;
import      java.io.*;
import      java.util.*;
import      jcx.util.*;
import      jcx.html.*;
import      jcx.db.*;
import      javax.swing.* ;
import     com.jacob.activeX.*;
import     com.jacob.com.*;
import     Farglory.util.FargloryUtil ;
public  class  Doc2R112_PROD210106  extends  bTransaction{
    talk  dbDoc                             =  getTalk(""  +get("put_Doc"));
    talk  dbSale                            =  getTalk(""  +get("put_Sale"));
    public  boolean  action (String  value) throws  Throwable{
        // 回傳值為 true 表示執行接下來的資料庫異動或查詢
        // 回傳值為 false 表示接下來不執行任何指令
        // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
        // 格式   英制  寬度11.8  高度 8.5
        System.out.println("列印---------------------------S2015-11-04") ;
        talk          dbFED1    =  getTalk(""  +get("put_FED1"));
        talk          dbFE3D    =  getTalk(""  +get("put_FE3D"));
        FargloryUtil       exeUtil    =  new  FargloryUtil() ;
        //
        String      stringPrevFunction              =  (""  +  get("Doc2M010_PRINT_Function")).trim( );  // 新加
        String      stringBarCode                     =  (""  +  get("Doc2M010_PRINT_BarCode")).trim( );  if("null".equals(stringBarCode)  ||  "".equals(stringBarCode))  return  false ;
        String      stringCheapenMoney          =  (""  +  get("Doc2M010_PRINT_CheapenMoney")).trim() ;
        String      stringRetainMoney             =  (""  +  get("Doc2M010_PRINT_RetainMoney") ).trim(); 
        String      stringRealMoneySumPrint   =  "" ;
        String      stringFunction                      =  (""  +  get("Doc2M010_FUNCTION")).trim( );
        String      stringPrinterNAME               =  (""  +  get("Doc2M010_PRINT_PrinterNAME")).trim();     put("Doc2M010_PRINT_PrinterNAME",  "null") ;
        String      stringPrintable                     =  (""  +  get("Doc2M010_PRINT_Enable")).trim() ;               put("Doc2M010_PRINT_Enable",           "null") ;
        String    stringUser            =  getUser() ;
        String    stringTable           =  "" ;
        boolean   booleanFlow                         =  true ;
        boolean   booleanSource                     =  stringPrevFunction.indexOf("Doc5M")==-1 ;
        boolean   booleanToNextFlow       =  false ;
        
        if("".equals(stringRetainMoney)      ||  "null".equals(stringRetainMoney))      stringRetainMoney      =  "0" ;
        //
        if("".equals(stringPrinterNAME)  ||  "null".equals(stringPrinterNAME)) {
            if(!"B3018".equals(stringUser.toUpperCase())) {
                messagebox("列表機錯誤，請洽資訊室。") ;
                return  false ;
            }
        }
        setPrinter(stringPrinterNAME) ;
        // true 已至下一流程    false 仍在本身部室 
        if("B".equals(stringFunction)) {
            stringTable  =  "Doc6M010" ;
        } else {
            stringTable  =  booleanSource?"Doc2M010":"Doc5M020" ;
        }
        booleanToNextFlow     =  isToNextFlow(stringBarCode,  stringTable,  exeUtil,  dbDoc) ;
        doRequestDefault( ) ;
        //
        if("A".equals(stringFunction)) {
              stringCheapenMoney  =   getCheapenMoney(stringBarCode,  stringFunction,  stringPrevFunction,  exeUtil,  dbDoc,  stringCheapenMoney) ;
              //
              boolean  booleanDoc2M0143Exist    =  dbDoc.queryFromPool("SELECT  BarCode  FROM  Doc2M0143  WHERE  BarCode  =  '"+stringBarCode+"' ").length>0 ;
              if(booleanDoc2M0143Exist)  stringCheapenMoney  =  "0"  ;
              // 請款
              booleanFlow               =  doPrint1(stringBarCode,  stringRealMoneySumPrint,  stringCheapenMoney,  booleanSource,  booleanDoc2M0143Exist,  booleanToNextFlow,  exeUtil,  dbDoc,  dbFE3D,  dbFED1,  stringPrevFunction) ;
        } else if("B".equals(stringFunction)) {
              // 借款沖銷
              booleanFlow              =  doPrint2(stringBarCode,  stringPrevFunction,  booleanSource,  booleanToNextFlow,  exeUtil,  dbDoc,  dbFE3D,  dbFED1) ;
        } else {
            getInternalFrame(getFunctionName()).setVisible(false) ;
            return   false ;
        }
        if(booleanFlow) {
            if("B3018,".indexOf(getUser().toUpperCase()+",")==-1  &&  "Y".equals(stringPrintable)) {
                action(6) ;
                getInternalFrame(getFunctionName()).setVisible(false) ;
            }
            if("B3018,".indexOf(getUser().toUpperCase()+",")==-1    &&  "Y".equals(stringPrintable)) {
                if("A".equals(stringFunction)) {
                        stringTable  =  booleanSource  ?  "Doc2M010" :  "Doc5M020" ;
                    } else {
                            stringTable  =  booleanSource  ?  "Doc6M010" :  "Doc5M030" ;
                    }
                if(booleanToNextFlow  &&  !"B3018".equals(getUser().toUpperCase())) {
                      String    stringPrintCount    =  getValue("PrintCount").trim( ) ;
                      int     intPrintCount        =  exeUtil.doParseInteger(stringPrintCount) ;
                      doAddPrintCount(stringTable,  ""+(intPrintCount+1),  stringBarCode) ;
                }
            }
            put("Doc2M010_Print_OK",  "OK") ;
        }
        put("Doc2M010_PRINT_RetainMoney",  "null") ;
        System.out.println("列印---------------------------E2015-11-04") ;
        return false;
    }
    // true 已至下一流程    false 仍在本身部室
    public  boolean  isToNextFlow(String  stringBarCode,  String  stringTableName,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String      stringUndergoWrite    =  "" ;
        String      stringDocStatus         =  "" ;
        String      stringDepartNoOld       =  "" ;
        String      stringDepartNoNow       =  "" ;
        String      stringSqlAnd              =  " AND BarCode  =  '"+stringBarCode+"' " ;
        Hashtable   hashtableDoc1M040   =  null ;
        Hashtable   hashtableDoc2M010   =  null ;
        Vector      vectorDoc1M040      =  new  Vector() ;
        /*補印判斷
        申請單退件，仍在原單位 false
        申請單正常，最後狀態   出文4，已至下一流程 true
        申請單正常，最後狀態非出文4，   同一部室   仍在原單位 false
        申請單正常，最後狀態非出文4，不同一部室    已至下一流程 true*/
        hashtableDoc2M010    =  exeUtil.getQueryDataHashtableH(stringTableName,  new  Hashtable(),  stringSqlAnd,  dbDoc) ;
        stringUndergoWrite      =  ""+hashtableDoc2M010.get("UNDERGO_WRITE") ;
        if("X".equals(stringUndergoWrite))  return  false ;
        if("Y".equals(stringUndergoWrite))  return  true ;
        vectorDoc1M040         =  exeUtil.getQueryDataHashtable("Doc1M040",  new  Hashtable(),  stringSqlAnd+" ORDER BY  EDateTime ",  dbDoc) ;
        if(vectorDoc1M040.size()  ==  0)  return  false ;
        //
        hashtableDoc1M040    =  (Hashtable)vectorDoc1M040.get(vectorDoc1M040.size()-1) ;
        stringDocStatus       =  ""+hashtableDoc1M040.get("DocStatus") ;
        stringDepartNoNow   =  ""+hashtableDoc1M040.get("DepartNo") ;
        if("4".equals(stringDocStatus))  return  true ;
        hashtableDoc1M040    =  (Hashtable)vectorDoc1M040.get(0) ;
        stringDepartNoOld   =  ""+hashtableDoc1M040.get("DepartNo") ;
        if(stringDepartNoOld.startsWith("022")) {
            if(!stringDepartNoOld.equals(stringDepartNoNow))  return  true ;
        } else  {
            stringDepartNoOld   =  exeUtil.doSubstring(stringDepartNoOld,   0,  3) ;
            stringDepartNoNow  =  exeUtil.doSubstring(stringDepartNoNow,  0,  3) ;
            if(!stringDepartNoOld.equals(stringDepartNoNow))  return  true ;
        }
        return  false ;
    }
    public  int  doPutValues(int  intCount,  int  intSize,  int  intStart,  String  stringValue,  String  stringFieldName,  FargloryUtil  exeUtil) throws  Throwable {
        int     intPos          =  0 ;
        int     intCountL     =  intCount ;     if(intCountL  <=  0)  intCountL  =  1 ;
        String    stringFieldNameL    =  "" ;
        String    stringTemp      =  stringValue ;
        String[]    arrayTemp       =  null ;       if("".equals(stringValue))  return -1 ;
        for(int  intNo=1  ;  intNo<=intCountL  ;  intNo++) {
            arrayTemp           =  exeUtil.doCutStringBySize(intSize,  stringTemp) ;
            //
            intPos           =  intNo+intStart ;
            stringFieldNameL  = stringFieldName+""+intPos ;
            if(intCount  ==  0)  stringFieldNameL  =  stringFieldName ;
            //
            if(!"".equals(stringFieldName)) {
                doSetValue(stringFieldNameL,     arrayTemp[0].trim()) ;
                if(stringFieldNameL.startsWith("DescriptPrint2")) setVisible(stringFieldNameL.replaceAll("DescriptPrint2",  "DescriptPrint3"),  false) ;
                if(stringFieldNameL.startsWith("DescriptPrint3")) setVisible(stringFieldNameL.replaceAll("DescriptPrint3",  "DescriptPrint2"),  false) ;
            }
            //System.out.println(intNo+"---"+intPos+"("+arrayTemp[0].trim()+")--------------------------S") ;
            //
            if("".equals(arrayTemp[1])) break ;
            //
            stringTemp  =  arrayTemp[1].trim() ;
        }
        return  intPos ;
    }
    public  void  doRequestDefault( ) throws  Throwable {
        String    stringField  =  "" ;
        String[]  arrayView  =  {  "PrintDateTime2",    "ComNoPrint",     "DocCodeTXT1",      "DocCodeTXT2",  "DocCodeTXT3",  
                        "DocCodeTXT4",      "DocCodeTXT5",      "DocCodeTXT6",    "BarCodePrint", "PrintCount",
                          "DepartPrint",      "YearPrint",      "MonthPrint",     "DayPrint",     "CTimePrint",
                          "DocNoPrint",
                          //
                          "ProjectNo",        "ContractMoney",      "Percent1",  "LastDocNo",
                          "ProjectPrint",     "PaidUpMoney",         "Percent2",  
                          "",           "NoPayMoney",       "Percent3",  "FactoryNoPrint",   "TELPrint",              
                          "DescriptPrint1_1", "DescriptPrint1_2",   "DescriptPrint",  
                          // 估驗部份
                          "No1",          "DescriptPrint2_1",   "DescriptPrint3_1",  "SIZE1",       "UNIT1",        "PRICE1",
                          "No2",          "DescriptPrint2_2",   "DescriptPrint3_2",  "SIZE2",       "UNIT2",        "PRICE2",
                          "No3",          "DescriptPrint2_3",   "DescriptPrint3_3",  "SIZE3",       "UNIT3",        "PRICE3",
                          "No4",          "DescriptPrint2_4",   "DescriptPrint3_4",  "SIZE4",       "UNIT4",        "PRICE4",
                          // 前期...
                          "No21",  "RealMoneySumPreNum_1",  "RealMoneySumPrePercent_1",  "RealMoneySumPrePrint_1",  "RealMoneySumNum_1",  "RealMoneySumPercent_1",  "RealMoneySumPrint_1",  "RealMoneySumAddUpNum_1",  "RealMoneySumAddUpPercent_1",  "RealMoneySumAddUpPrint_1",  
                          "No22",  "RealMoneySumPreNum_2",  "RealMoneySumPrePercent_2",  "RealMoneySumPrePrint_2",  "RealMoneySumNum_2",  "RealMoneySumPercent_2",  "RealMoneySumPrint_2",  "RealMoneySumAddUpNum_2",  "RealMoneySumAddUpPercent_2",  "RealMoneySumAddUpPrint_2",  
                          "No23",  "RealMoneySumPreNum_3",  "RealMoneySumPrePercent_3",  "RealMoneySumPrePrint_3",  "RealMoneySumNum_3",  "RealMoneySumPercent_3",  "RealMoneySumPrint_3",  "RealMoneySumAddUpNum_3",  "RealMoneySumAddUpPercent_3",  "RealMoneySumAddUpPrint_3",  
                          "No24",  "RealMoneySumPreNum_4",  "RealMoneySumPrePercent_4",  "RealMoneySumPrePrint_4",  "RealMoneySumNum_4",  "RealMoneySumPercent_4",  "RealMoneySumPrint_4",  "RealMoneySumAddUpNum_4",   "RealMoneySumAddUpPercent_4", "RealMoneySumAddUpPrint_4",  
                          "",   "",                 "RealMoneySumPrePercent2",    "RealMoneySumPrePrint2",   "RealMoneySumNum2",   "RealMoneySumPercent2",      "RealMoneySumPrint2",     "RealMoneySumAddUpNum2",    "RealMoneySumAddUpPercent2",      "RealMoneySumAddUpPrint2",  
                          "",   "",                 "",                                                "RetainMoneyPrePrint",        "",                 "",                                           "RetainMoneyPrint",      "",                    "",                                                     "RetainMoneyAddUpPrint",  
                          "",   "",                 "",                                                "CheapenMoneyPrePrint",     "",                 "",                                            "CheapenMoneyPrint",    "",                    "",                                                     "CheapenMoneyAddUpPrint",  
                          "",   "",                 "",                                                "ActualMoneyPrePrint",     "",                "",                                             "ActualMoneyPrint",      "",                    "",                                                     "ActualMoneyAddUpPrint",  
                          // 相關單據號
                          "ContractNo",
                          "PurchaseNo1_1",    "PurchaseNo1_2",
                          "OptometryNo1_1", "OptometryNo1_2",
                          "BorrowNo1",      "BorrowNo2",
                          // 簽核
                          "DeifyDepart",      "OriEmployeeNo",    "PrintDateTime",   
                          // 不使用
                          "CoinType"} ;
        for(int  intNo=0  ;  intNo<arrayView.length  ;  intNo++) {
            stringField  =  arrayView[intNo].trim() ;     if("".equals(stringField))  continue ;
            //
            setValue(stringField,  "") ;
            setVisible(stringField,  false) ;
        }
    }
    public  void  doSetValue(String  stringFieldName,  String  stringFieldValue) throws  Throwable {
        setValue(stringFieldName,  stringFieldValue) ;  setVisible(stringFieldName,  true) ;
    }
    public  boolean  isPutDoc2M010DataOK(String  stringType,  boolean  booleanSource,  boolean  booleanToNextFlow,  String  stringPrevFunction,  Vector  vectorDoc2M010,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFE3D,  talk  dbFED1) throws  Throwable {
        //  請款單左上角增加列印時間：(97/02 17:00)    PrintDateTime2
        String    stringToday         =  datetime.getTime("yy/mm/dd h:m:s") ;
        int         intPos              =  stringToday.indexOf("/") ;
        int         intPosE             =  stringToday.lastIndexOf(":") ;
                      stringToday         =  stringToday.substring(intPos+1,  intPosE) ;
        doSetValue("PrintDateTime2",  "列印時間："+stringToday) ;
        // 條碼編號
        String    stringBarCode       =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        if("".equals(stringBarCode)) {
            messagebox("[條碼編號] 為空白，請重新列印。") ;
            return  false ;
        }
        doSetValue("BarCodePrint",  stringBarCode) ;
        setVisible("BarCodePrint",  !stringBarCode.startsWith("Z")) ;
        // 公司
        String    stringComNo       =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String    stringCompanyName   =  getCompanyNameUnion(stringComNo,  stringBarCode,  exeUtil,  dbDoc,  dbFED1) ;
        if("".equals(stringCompanyName)) {
            messagebox("[公司名稱] 為空白，請重新列印。") ;
            return  false ;
        }
        doSetValue("ComNoPrint",  stringCompanyName) ;
        // 補印
        String    stringPrintCount  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PrintCount") ;     stringPrintCount  =  ""+exeUtil.doParseInteger(stringPrintCount) ;
        if(booleanToNextFlow) doSetValue("PrintCount",  stringPrintCount) ;
        // 經辦單位
        String    stringDepartNo    =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DepartNo") ; 
        String    stringDepartNoL    =  "B".equals(stringType)?"":getDeptCdDoc(stringDepartNo,  "",  "",  new  Hashtable(),  dbDoc) ;  if(!"".equals(stringDepartNoL))  stringDepartNo  =  stringDepartNoL ;
        String    stringDepartName  =  getDepartName(stringDepartNo,  dbDoc) ;
        //
        if("CS".equals(stringComNo))  stringDepartName  =  stringDepartNo ;
        //
        if("".equals(stringDepartName)) {
            messagebox("[部門名稱] 為空白，請重新列印。") ;
            return  false ;
        }
        doSetValue("DepartPrint",  stringDepartName) ;
        // 輸入日期
        String    stringCDate   =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "CDate") ;
        String    stringCTime   =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "CTime") ;
        String[]    arrayCDate    =  convert.StringToken(exeUtil.getDateConvert(stringCDate),  "/") ;
        if(arrayCDate.length  !=  3) {
            messagebox("[日期] 錯誤，請重新列印。") ;
            return  false ;
        }
        doSetValue("YearPrint",         arrayCDate[0]) ;
        doSetValue("MonthPrint",      arrayCDate[1]) ;
        doSetValue("DayPrint",        arrayCDate[2]) ;
        doSetValue("CTimePrint",      stringCTime) ;  
        // 公文編號
        String    stringDocNo1  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNo1") ;
        String    stringDocNo2  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNo2") ;
        String    stringDocNo3  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNo3") ;
        String    stringDocNo   =  stringDocNo1+"-"+stringDocNo2+"-"+stringDocNo3 ;
        doSetValue("DocNoPrint",  stringDocNo) ; 
        // 敬會人總室  DeifyDepart
        if("033FZ".equals(stringDocNo1)) {
            doSetValue("DeifyDepart",  "敬會人總室") ;
        }
        // 固定資產時
        Vector      vectorDoc2M018      =  "B".equals(stringType)?new  Vector():exeUtil.getQueryDataHashtable(booleanSource?"Doc2M018":"Doc5M028",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;  
        String      stringOptometryType =  "" ;
        for(int  intNo=0  ;  intNo<vectorDoc2M018.size()  ;  intNo++) {
            stringOptometryType  =  exeUtil.getVectorFieldValue(vectorDoc2M018,  intNo,  "OptometryType").toUpperCase() ;
            if("B".equals(stringOptometryType)) {
                doSetValue("DeifyDepart",  "敬會人總室") ;  
                break ;
            }
        }
        stringOptometryType  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "OptometryType") ;
        if("B".equals(stringOptometryType)) {
            doSetValue("DeifyDepart",  "敬會人總室") ;  
        }
        // 承辦人 及 列印日期
        String    stringOriEmployeeNo  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "OriEmployeeNo") ;
        String    stringTodayL                =  datetime.getToday("yy/mm/dd") ;
        //
        intPos            =  stringToday.indexOf("/") ;
        stringToday   =  stringToday.substring(intPos+1) ;
        //
        //doSetValue("OriEmployeeNo",   getEmpName(stringOriEmployeeNo,  exeUtil,  dbDoc,  dbFE3D)) ;
        //doSetValue("PrintDateTime",       stringTodayL) ;                             
        // 摘要(38個中文字)、估驗部份
        String    stringDescript  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "Descript") ;
        if("".equals(stringDescript)) {
            messagebox("[摘要] 為空白，請重新列印。") ;
            return  false ;
        }
        doPutValues(3,  88,  0,  stringDescript,  "DescriptPrint1_",  exeUtil) ;      // 摘要
        // 估驗部份(15個中文字35個字)
        String    stringPurchaseNoExist  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PurchaseNoExist") ;
        //
        doSetValue("No1",  "1") ;
        doSetValue("No21", "1") ;
        if(stringPurchaseNoExist.startsWith("Y")) {
            doPutValues(4,  30,    0,  stringDescript,  "DescriptPrint2_",  exeUtil) ;        
        } else {
            doPutValues(4,  70,    0,  stringDescript,  "DescriptPrint3_",  exeUtil) ;      
        }
        
        return  true ;
    }
    // 列印 START
    public  boolean  doPrint1(String          stringBarCode,    String      stringRealMoneySumPrint,  String      stringCheapenMoney,
                                             boolean    booleanSource,    boolean   booleanDoc2M0143Exist,      boolean   booleanToNextFlow,    FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFE3D,  talk  dbFED1,
                               String           stringPrevFunction) throws  Throwable {
        String      stringCostID              =  "" ;
        String      stringRetainMoney       =  "" ;
        String      stringRetainBarCode     =  "" ;
        String      stringPurchaseNoExist =  "" ;
        String      stringDocNoType       =  "" ;
        String[][]      retDoc5M0220            =  new  String[0][0] ;
        Vector        vectorDoc2M010        =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M010":"Doc5M020",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;     if(vectorDoc2M010.size()  ==  0) { messagebox("查無資料。2") ;   return false; }
        Vector        vectorDoc2M011        =  new  Vector() ;
        Vector        vectorDoc2M012        =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M012":"Doc5M022",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ; 
        Vector        vectorDoc2M013        =  new  Vector() ;
        boolean     booleanPocketMoney      =  false  ;  //  true  表示為零用金之實例情況        
        //
        //stringRetainMoney     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "RetainMoney") ;
        stringRetainBarCode     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "RetainBarCode") ;
        stringPurchaseNoExist   =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PurchaseNoExist") ;
        stringDocNoType     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNoType") ;
        // 行銷-退保留 請款列印判斷
        if(!"".equals(stringRetainBarCode)) {
            retDoc5M0220  =  getDoc5M0220(stringRetainBarCode,  "",  stringBarCode,  "",  dbDoc) ;    
            if(retDoc5M0220.length  >  0) {
                // 管理費用
            } else {
                // 行銷
                return setDataRetain(booleanToNextFlow,  stringPrevFunction,  vectorDoc2M010,  booleanSource,  exeUtil,  dbDoc,  dbFE3D,  dbFED1);
            }
        }
        if(vectorDoc2M012.size()==0  &&  retDoc5M0220.length==0) {
            messagebox("查無 [費用] 資料。") ;
            return false ;
        }
        if(vectorDoc2M012.size()>0) {
            stringCostID  =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;
            // 零用金
            if(",31,32,".indexOf(stringCostID)  !=  -1)  booleanPocketMoney  =  true ;
        }
        //
        if(!(booleanPocketMoney  &&  !"Y".equals(stringPurchaseNoExist))  &&  "A".equals(stringDocNoType)) {
            vectorDoc2M011        =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M011":"Doc5M021",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
            vectorDoc2M013        =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M013":"Doc5M023",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        }
        System.out.println("-------------------------------設定資料START"); 

        return  setDataDoc2M010(stringCheapenMoney,         stringPrevFunction,       booleanPocketMoney,
                                                  vectorDoc2M010,                   vectorDoc2M011,             vectorDoc2M012,                   vectorDoc2M013,
                                      retDoc5M0220,                   booleanSource,            booleanDoc2M0143Exist,      booleanToNextFlow,  
                                      exeUtil,                    dbDoc,                              dbFE3D,                   dbFED1) ;
    }
    public  boolean  setDataRetain(boolean  booleanToNextFlow,  String  stringPrevFunction,  Vector  vectorDoc2M010,  boolean  booleanSource,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFE3D,  talk  dbFED1) throws  Throwable {
        String        stringBarCode             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String        stringWriteRetainMoney      =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "WriteRetainMoney") ;
        String        stringRetainBarCode           =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "RetainBarCode") ;
        String    stringTemp                =  "" ;
        String        stringFactoryNo                   =  "" ;
        String[][]    retFED1005                        =  getDoc2M018(booleanSource?"Doc2M018":"Doc5M028",  stringBarCode,  dbDoc) ;
        Vector    vectorRetainDB            =  null ;
        //
        if(!isPutDoc2M010DataOK("A",  booleanSource,  booleanToNextFlow,  stringPrevFunction,  vectorDoc2M010,  exeUtil,  dbDoc,  dbFE3D,  dbFED1) ) return  false ;
        // 統一編號
        vectorRetainDB  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M011":"Doc5M021",  new  Hashtable(),  " AND  BarCode  =  '"+stringRetainBarCode+"' ",  dbDoc) ; 
        if(vectorRetainDB.size()  ==  0) {
              vectorRetainDB  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M013":"Doc5M023",  new  Hashtable(),  " AND  BarCode  =  '"+stringRetainBarCode+"' ",  dbDoc) ; 
        }
        stringFactoryNo       =  exeUtil.getVectorFieldValue(vectorRetainDB,  0,  "FactoryNo") ;
        retFED1005      =  getFED1005(stringFactoryNo,  "",  dbDoc,  dbFED1) ;
        if(retFED1005.length  ==  0) {
            stringFactoryNo  =  "Z0001" ;
            retFED1005        =  getFED1005(stringFactoryNo,  "",  dbDoc,  dbFED1) ;
            if(retFED1005.length  ==  0) {
                messagebox(" [廠商名稱] 為空白，請重新列印。") ;
                return  false ;
            }
        }
        String  stringFactoryName   =  retFED1005[0][0].trim() ;
        String  stringTELPrint      =  retFED1005[0][1].trim() ;
        doPutValues(0,  10,  0,  stringFactoryName,  "FactoryNoPrint",  exeUtil) ;
        if(stringTELPrint.length()<=10  )doSetValue("TELPrint",            stringTELPrint) ;

        // 本期
        stringTemp  =  format.format(convert.FourToFive(stringWriteRetainMoney,0),        "999,999,999,999").trim( ) ;
        doSetValue("RealMoneySumPrint",    stringTemp) ; 
        // 估驗部份小計
        doSetValue("RealMoneySumPrint2",  stringTemp) ;
        // 實付金額
        doSetValue("ActualMoneyPrint",        stringTemp) ;
        
        return  true ;
    }


    public  boolean  setDataDoc2M010( String        stringCheapenMoney,     String      stringPrevFunction,   boolean     booleanPocketMoney,
                                        Vector      vectorDoc2M010,           Vector      vectorDoc2M011,       Vector          vectorDoc2M012,               Vector      vectorDoc2M013,
                                      String[][]      retDoc5M0220,               boolean     booleanSource,          boolean        booleanDoc2M0143Exist,     boolean     booleanToNextFlow,
                                      FargloryUtil    exeUtil,                        talk            dbDoc ,             talk         dbFE3D,                talk        dbFED1) throws  Throwable {
        String        stringBarCode             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String        stringCDate               =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "CDate") ;
        String        stringComNo               =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String      stringCostID                    =  "" ;
        String      stringCostID1                     =  "" ;
        String    stringPurchaseNoExist     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PurchaseNoExist") ;
        String       stringRetainBarCode            =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "RetainBarCode") ;
        String       stringRetainType                =  "" ;  // A:請購     B:請款
        String    stringFactoryNo         =  "" ;
        Vector    vectorDoc2M010Retain          =  null ;
        Vector    vectorDoc2M017          =  !stringPurchaseNoExist.startsWith("Y") ? new  Vector() : exeUtil.getQueryDataHashtable(booleanSource?"Doc2M017":"Doc5M027",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        Vector    vectorDoc5M0224       =  exeUtil.getQueryDataHashtable("Doc5M0224",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        Vector        vectorCostID224           =  booleanSource ?
                                                                getCostIDVDoc2M0201V(stringComNo,  exeUtil.getDateConvert(stringCDate),  " AND  FunctionName  LIKE  '%立沖傳票對應%' ",  dbDoc) :
                                             getDoc2M0401V("",  "D",  " AND  FunctionName  LIKE  '%立沖傳票對應%' ",  dbDoc) ; // 立沖對應傳票，允許部門、案別為空白
        if(!"".equals(stringRetainBarCode)) {
            vectorDoc2M010Retain  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M010":"Doc5M020",  new  Hashtable(),  " AND  BarCode  =  '"+stringRetainBarCode+"' ",  dbDoc) ;
            stringRetainType           =  (vectorDoc2M010Retain.size()>0) ? "B" : "A" ;  // A:請購    B:請款
        }
        //if("B3018".equals(getUser()))messagebox("OK0") ;
        // 工程編號
        if(vectorDoc2M012.size()  >  0) {
            stringCostID    =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;
            stringCostID1   =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID1") ;
            doSetValue("ProjectNo",  stringCostID+stringCostID1) ;
        }
        // 工程名稱(案別)：
        if(!isPutProjectOK(vectorDoc2M010,  vectorDoc2M012,  vectorCostID224,   exeUtil))  return  false ;
        // 廠商
        stringFactoryNo  =  doPutFactoryNoOK(booleanSource,  vectorDoc2M010,  vectorDoc2M011,  vectorDoc2M012,  vectorDoc2M013,  vectorDoc2M017,  vectorDoc5M0224,  vectorCostID224,  exeUtil,  dbDoc,  dbFED1,  dbFE3D) ;
        if("".equals(stringFactoryNo))                                                                      return  false ;
        // 
        if(!isPutDoc2M010DataOK("A",  booleanSource,  booleanToNextFlow,  stringPrevFunction,  vectorDoc2M010,  exeUtil,  dbDoc,  dbFE3D,  dbFED1) )  return  false ;
        // 公文編號等...
        //System.out.println("isPutDocNoOK----------------------------S") ;
        if(!isPutDocNoOK(booleanSource,  stringRetainType,  vectorDoc2M010,  vectorDoc2M017,  exeUtil,  dbDoc,  dbFE3D) )                       return  false ;
        //System.out.println("isPutDocNoOK----------------------------E") ;
        // 合約金額等...
        Vector  vectorPurchaseNos  =  new  Vector() ;
        if(vectorDoc2M017.size()>0    ||  "A".equals(stringRetainType)) {
              if(!isPutContractMoneyDatasOK(booleanSource,  stringFactoryNo,  vectorDoc2M010,  vectorDoc2M017,  exeUtil,  dbDoc,  vectorPurchaseNos) )  return  false ;
        }
        // 最遲付款日期
        String      stringDescript        =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "Descript") ;
        String      stringLastPayDate   =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "LastPayDate") ;    if("".equals(stringLastPayDate))  stringLastPayDate  =  "無" ;stringLastPayDate  =  "(最遲付款日期："+stringLastPayDate+")" ;
        int       intPos            =  doPutValues(3,  88,  0,  stringDescript+"　"+stringLastPayDate,  "",  exeUtil) ;      // 摘要
        if(intPos  <=  3) {
            doSetValue("DescriptPrint",  stringLastPayDate) ;
        }         
        // 附件
        doPutDocCodeTXT(booleanSource,  vectorDoc2M010,  exeUtil,  dbDoc) ;

        // 估驗相關資料
        System.out.println("請購細明 估驗資料("+vectorPurchaseNos.size()+")--------------------------------------") ;
        if(!isDefaultItemsDataOK(booleanSource,  booleanDoc2M0143Exist,  stringCheapenMoney,  vectorPurchaseNos,  vectorCostID224,  vectorDoc2M010,  vectorDoc2M012,  vectorDoc2M013,  vectorDoc2M017,  exeUtil,  dbDoc))  return  false ;
        //if("B3018".equals(getUser()))  messagebox("估驗２") ;
        // 請購細明 估驗資料
        doItemsPurchaseData(booleanSource,  stringRetainType,  stringFactoryNo,  vectorPurchaseNos,  vectorDoc2M010,  vectorDoc2M012,  vectorDoc2M017,  exeUtil) ;
        //if("B3018".equals(getUser()))  messagebox("估驗3---"+stringCostID) ;
        if(vectorDoc2M012.size()>0  &&  vectorDoc2M017.size()==0) {
            int           intOtherTable2Row       =  -1 ;   //由立沖判斷 Table2(Doc2M012) 的數目，如果 Table2 大於
            boolean   booleanSumPrint           =  false ;
                  stringCostID              =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ; 
            //if("B3018".equals(getUser().toUpperCase()))messagebox("stringCostID("+stringCostID+")") ;
            if("A170701".equals(stringCostID)) {   
                // 銀行利息費用 OK
                booleanSumPrint  =  !isA170701ItemsDataOK(vectorDoc2M010,  vectorDoc2M012,  exeUtil,  dbDoc) ;
                //if("B3018".equals(getUser()))  messagebox("估驗41---"+booleanSumPrint) ;
            } else if("F293001".equals(stringCostID)) { 
                // 購買基金 OK
                booleanSumPrint  =  !isF293001ItemsDataOK(vectorDoc2M010,  vectorDoc2M012,  exeUtil,  dbDoc) ;
                //if("B3018".equals(getUser()))  messagebox("估驗42---"+booleanSumPrint) ;
            } else if("F273701".equals(stringCostID)) {   
                // 償還銀行借款
                intOtherTable2Row   =  0 ;  
                booleanSumPrint     =  !isF273701ItemsDataOK(vectorDoc5M0224,  vectorDoc2M010,  vectorDoc2M012,  exeUtil,  dbFED1) ;
                //if("B3018".equals(getUser()))  messagebox("估驗43---"+booleanSumPrint) ;
            } else if(!booleanSource  &&  "F282302,F282303,".indexOf(stringCostID)!=-1  &&  vectorDoc5M0224.size()>0) { // 立沖對應傳票
                // 立沖對應傳票
                isF282302ItemsDataOK(vectorDoc5M0224,  vectorDoc2M010,  vectorDoc2M012,  vectorDoc2M013,  exeUtil) ;
                //if("B3018".equals(getUser()))  messagebox("估驗44---"+booleanSumPrint) ;
            } else if(!booleanSource  &&  vectorCostID224.indexOf(stringCostID)  !=  -1) { // 立沖對應傳票
                //if("B3018".equals(getUser().toUpperCase()))messagebox("立沖對應傳票stringCostID("+stringCostID+")") ;
                // 立沖對應傳票 2
                intOtherTable2Row  =  0 ;
                booleanSumPrint  =  !isCostID224ItemsDataOK(booleanSource,  vectorCostID224,  vectorDoc5M0224,  vectorDoc2M010,  vectorDoc2M012,  vectorDoc2M013,  exeUtil,  dbFED1) ;
                //if("B3018".equals(getUser()))  messagebox("估驗45---"+booleanSumPrint) ;
            } else {
                intOtherTable2Row  =  0 ;
                if(!stringPurchaseNoExist.startsWith("Y"))  booleanSumPrint  =  true ;
                //if("B3018".equals(getUser()))  messagebox("估驗46---"+booleanSumPrint) ;
            }
            if(intOtherTable2Row  ==  -1) {
                intOtherTable2Row  =  getOtherTable2Row(booleanSource,  vectorDoc2M012,  vectorDoc5M0224,  exeUtil) ;
            }
            //if("B3018".equals(getUser()))  messagebox("估驗5---booleanSumPrint("+booleanSumPrint+")intOtherTable2Row("+intOtherTable2Row+")") ;
            if(intOtherTable2Row  >  0) {
                // 2013-08-09 列印 立沖之外的請款代碼資料 START F282201 繳納營業稅(F289991稅務罰款)
                doDoc5M0224sItemsDataOK(intOtherTable2Row,  vectorDoc2M012,  vectorDoc2M013,  vectorDoc5M0224,  exeUtil,  dbFED1)  ;
                // 2013-08-09 列印 立沖之外的請款代碼資料 END
            } else if(booleanSumPrint) {
                doSumPrint(booleanSource,  vectorDoc2M010,  vectorDoc2M012,  exeUtil,  dbDoc) ;
                //if("B3018".equals(getUser()))messagebox("OK-SUM") ;
            }
        }
        //if("B3018".equals(getUser()))messagebox("OK4") ;
        //System.out.println("-------------------------------設定資料END"); 
        return  true ;
    }
    public  int  getOtherTable2Row(boolean  booleanSource,  Vector  vectorDoc2M012,  Vector  vectorDoc5M0224,  FargloryUtil  exeUtil) throws  Throwable {
        int            intOtherTable2Row        =  -1 ;   //由立沖判斷 Table2(Doc2M012) 的數目，如果 Table2 大於
        String      stringCostIDL                 =  "" ;
        String      stringCostID1L              =  "" ;
        String      stringKeyL                      =  "" ;
        Vector      vectorCostID                =  new  Vector();
        //
        if(booleanSource)           return  intOtherTable2Row   ;
        if(vectorDoc5M0224.size()==0)   return  intOtherTable2Row   ;
        //
        for(int  intNo=0  ;  intNo<vectorDoc5M0224.size()  ;  intNo++) {
            stringCostIDL                =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "CostID") ; 
            stringCostID1L              =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "CostID1") ; 
            stringKeyL                    =  stringCostIDL +"%-%"+stringCostID1L;
            //
            if(vectorCostID.indexOf(stringKeyL)  ==  -1)  vectorCostID.add(stringKeyL) ;
        }
        intOtherTable2Row  =  vectorDoc2M012.size()  -  vectorCostID.size()  ;
        if(intOtherTable2Row  <=  0)  intOtherTable2Row  =  0 ;
        return  intOtherTable2Row ;
    }
    // 估驗相關資料
    public  void  doDoc5M0224sItemsDataOK(int  intOtherTable2Row,  Vector  vectorDoc2M012,  Vector  vectorDoc2M013,  Vector  vectorDoc5M0224,  FargloryUtil  exeUtil,  talk  dbFED1) throws  Throwable {
        // 2013-08-09 列印 立沖之外的請款代碼資料 START F282201 繳納營業稅(F289991稅務罰款)
        int         intPosL                     =  vectorDoc5M0224.size() ;
        String    stringFactoryNoL      =  "" ;
        String    stringCostIDL     =  "" ;
        String    stringTXT       =  "" ;
        String    stringRealTotalMoney  =  "" ;
        if(vectorDoc2M013.size()  >  0) {
            stringFactoryNoL  =  exeUtil.getVectorFieldValue(vectorDoc2M013,  vectorDoc2M013.size()-1,  "FactoryNo") ;
            stringFactoryNoL  =  getObjectNameFED1005(stringFactoryNoL,  dbFED1) ;
            if("".equals(stringFactoryNoL)) {
                stringFactoryNoL  =  getValue("FactoryNoPrint") ;
            }
        } else {
            stringFactoryNoL  =  getValue("FactoryNoPrint") ;
        }
        for(int  intNo=intOtherTable2Row  ;  intNo<vectorDoc2M012.size()  ;  intNo++) {
            stringCostIDL                =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "CostID") ;
            stringTXT                     =  getCost4Name("",  stringCostIDL,  "",  "",  dbDoc) ;
            stringRealTotalMoney  =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "RealTotalMoney") ;
            //
            intPosL++ ;
            doSetValue("No"+intPosL,                                               ""+intPosL) ;
            doSetValue("No2"+intPosL,                                              ""+intPosL) ;
            doSetValue("DescriptPrint2-"+intPosL,                            stringFactoryNoL  +  " "  +  stringTXT) ;
            doSetValue("RealMoneySumPrint_"+intPosL,              exeUtil.getFormatNum2(stringRealTotalMoney)) ;
            doSetValue("RealMoneySumAddUpPrint_"+intPosL,     exeUtil.getFormatNum2(stringRealTotalMoney)) ;
        }
    }
    public  boolean  isDefaultItemsDataOK(boolean  booleanSource,  boolean  booleanDoc2M0143Exist,  String  stringCheapenMoney,  Vector  vectorPurchaseNos,  Vector  vectorCostID224,  Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  Vector  vectorDoc2M013,  Vector  vectorDoc2M017,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        int             intCostID009Pos             =  2 ;
        int             intCostID009                  =  2 ;
        String      stringRealTotalMoney        =  "" ;
        String      stringComNo               =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String      stringRetainMoney         =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "RetainMoney") ;
        String      stringWriteRetainMoney      =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "WriteRetainMoney") ;
        String      stringPurchaseNoExist       =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PurchaseNoExist") ;
        String      stringDocNoType         =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNoType") ;
        String      stringCostID            =  "" ;
        String      stringCostID1           =  "" ;
        String      stringPercent           =  "" ;
        String      strsingPurchaseNo         =  "" ;
        String      stringSqlAndPurchaseNo      =  "" ;
        double      doubleTemp              =  0 ;
        double    doubleRealMoneySumPrint   =  0 ;
        double    doubleRealMoneySumPrintPos  =  0 ;
        double    doubleRealMoneySumPrintNeg  =  0 ;
        double    doubleActualMoney       =  0 ;
        double      doubleCosID009            =  0 ;
        double    doubleContractMoney       =  exeUtil.doParseDouble(getValue("ContractMoney").replaceAll(",", "").trim()) ;
        double    doublePaidUpMoney       =  exeUtil.doParseDouble(getValue("PaidUpMoney").replaceAll(",", "").trim()) ;
        //
        for(int  intNo=0  ;  intNo<vectorPurchaseNos.size()  ;  intNo++) {
            strsingPurchaseNo  =  ""+vectorPurchaseNos.get(intNo) ;
            //
            if(!"".equals(stringSqlAndPurchaseNo))  stringSqlAndPurchaseNo  +=  " OR " ;
            stringSqlAndPurchaseNo  +=  " PurchaseNo  =  '"  +  strsingPurchaseNo  +  "' " ;
        }
        //
        if(exeUtil.doParseDouble(stringRetainMoney)  ==   0)    stringRetainMoney  =  "0" ;
        if(exeUtil.doParseDouble(stringCheapenMoney)  ==   0)  stringCheapenMoney  =  "0" ;
        //
        for(int  intNo=0  ;  intNo<vectorDoc2M012.size()  ;  intNo++) {
            stringRealTotalMoney      =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "RealTotalMoney") ;
            stringCostID          =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "CostID") ;
            stringCostID1         =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "CostID1") ;
            doubleTemp                          =  exeUtil.doParseDouble(stringRealTotalMoney)  ;
            
            doubleRealMoneySumPrint  +=  doubleTemp ;
            if(doubleTemp  >  0) {
                  doubleRealMoneySumPrintPos  +=  doubleTemp ;
            } else {
                  if("009".equals(stringCostID+stringCostID1)) {
                      doubleCosID009  +=  exeUtil.doParseDouble(stringRealTotalMoney) ;
                  } else {
                      doubleRealMoneySumPrintNeg  +=  doubleTemp ;
                  }
            }

        }
        doubleActualMoney       =   doubleRealMoneySumPrint  -
                              exeUtil.doParseDouble(stringRetainMoney)  -
                              exeUtil.doParseDouble(stringCheapenMoney)  +
                              exeUtil.doParseDouble(stringWriteRetainMoney) ;
        if(doubleRealMoneySumPrint  ==  0) {
            if(!"C".equals(stringDocNoType)) {
                messagebox(" [費用合計] 錯誤，請重新列印。") ;
                return  false ;
            }
        }
        if(doubleActualMoney  ==  0) {
            if(!"C".equals(stringDocNoType)) {
                messagebox(" [實付金額] 為零，請重新列印。") ;
                return  false ;
            }
        }
        // 估驗金額
        doSetValue("No1",                     "1") ;
        doSetValue("No21",                      "1") ;
        doSetValue("RealMoneySumPrint_1",                     exeUtil.getFormatNum2(""+doubleRealMoneySumPrintPos)) ;         // 本期 估驗金額
        doSetValue("RealMoneySumAddUpPrint_1",          exeUtil.getFormatNum2(""+doubleRealMoneySumPrintPos)) ;         // 累計 估驗金額
        if(doubleRealMoneySumPrintNeg  !=  0) {
            doSetValue("No2",    "2") ;
            doSetValue("No22",  "2") ;
            doSetValue("RealMoneySumPrint_2",                   exeUtil.getFormatNum2(""+doubleRealMoneySumPrintNeg)) ;     // 本期 估驗金額
            doSetValue("RealMoneySumAddUpPrint_2",        exeUtil.getFormatNum2(""+doubleRealMoneySumPrintNeg)) ;     // 累計 估驗金額
            //
            intCostID009  =  3 ;
        }
        // 估驗項目說明 已在 isPutDoc2M010DataOK 處理
        if("".equals(getValue("DescriptPrint2_2"))) {
            intCostID009Pos  =  2 ;
        } else if("".equals(getValue("DescriptPrint2_3"))) { 
            intCostID009Pos  =  3 ;
        } else  { 
            intCostID009Pos  =  4 ;
        }
        if(doubleCosID009  <  0) {
            doSetValue("No"+(intCostID009Pos),                                ""+intCostID009) ;
            doSetValue("No2"+(intCostID009Pos),                                 ""+intCostID009) ;
            doSetValue("DescriptPrint2_"+(intCostID009Pos),             getCostID1View(stringComNo,  "009",  dbDoc)) ;
            doSetValue("RealMoneySumPrint_"+intCostID009Pos,        exeUtil.getFormatNum2(""+doubleCosID009)) ;
            doSetValue("RealMoneySumAddUpPrint_"+intCostID009Pos,   exeUtil.getFormatNum2(""+doubleCosID009)) ;
        }
        //if("B3018".equals(getUser()))  messagebox("估驗1") ;
        
        // 比例
        if(stringPurchaseNoExist.startsWith("Y")) {
            stringPercent  =  convert.FourToFive(""+(doubleRealMoneySumPrint / doubleContractMoney*100),  0) ;
            doSetValue("RealMoneySumPercent_1",                stringPercent) ;                                   // 本期估驗 數量
            doSetValue("RealMoneySumAddUpPercent_1",      stringPercent) ;                                    // 累計估驗 數量
            doSetValue("RealMoneySumPercent2",                  stringPercent) ;                                  // 本期估驗合計 數量
            doSetValue("RealMoneySumAddUpPercent2",       stringPercent) ;                                  // 累計估驗合計 數量
        } 

        
        // 估驗合計金額
        doSetValue("RealMoneySumPrint2",                    exeUtil.getFormatNum2(""+doubleRealMoneySumPrint)) ;    // 本期 估驗合計金額
        doSetValue("RealMoneySumAddUpPrint2",          exeUtil.getFormatNum2(""+doubleRealMoneySumPrint)) ;   // 累計 估驗合計金額
        
        
        // 保留金額
        if(exeUtil.doParseDouble(stringRetainMoney)  !=  0) {
            doSetValue("RetainMoneyPrint",                    exeUtil.getFormatNum2(stringRetainMoney)) ;             // 本期保留 金額
            //if("B3018".equals(getUser()))  messagebox("本期保留 金額---"+exeUtil.getFormatNum2(stringRetainMoney)) ;
        } else if(exeUtil.doParseDouble(stringWriteRetainMoney)  !=  0) {
            doSetValue("RetainMoneyPrint",                  "-"+exeUtil.getFormatNum2(stringWriteRetainMoney)) ;        // 本期退保留 金額
        } else {
            doSetValue("RetainMoneyPrint",                  "0") ;                                                                                    // 本期退保留 金額
        }
        // 前期
        // 保留金額修正
        String  stringPreRetainMoney      =  "0" ;
        String  stringAddUpRetainMoney  =  "0" ;
        System.out.println("前期-保留金額修正------------------------------------------------------------------") ;
        if(booleanSource  &&  stringPurchaseNoExist.startsWith("Y")){
            String  stringSql                             =  "" ;
            String  stringEDateTime       =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "EDateTime") ;
            String  stringKindNo          =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "KindNo") ;
            //
            stringSql  =  " SELECT  SUM(M10.RetainMoney) "+
                      " FROM  Doc2M010  M10,  Doc2M017 M17 " +
                    " WHERE  M10.BarCode  =  M17.BarCode "  +
                         " AND  EDateTime  <  '"  +  stringEDateTime + "' "  +
                         " AND  ComNo  =  '"        +   stringComNo      +  "' "  +
                         " AND  KindNo  =  '"        +   stringKindNo      +  "' "  +
                      " AND ("+stringSqlAndPurchaseNo  + ")"  ;
            stringPreRetainMoney  =  dbDoc.queryFromPool(stringSql)[0][0] ;
            if(exeUtil.doParseDouble(stringPreRetainMoney)  ==  0)  stringPreRetainMoney  =  "0" ;
            //
            doSetValue("RetainMoneyPrePrint",       exeUtil.getFormatNum2(stringPreRetainMoney)) ;                // 前期 保留金額
        }
        // 累計
        stringAddUpRetainMoney  =  ""+(exeUtil.doParseDouble(stringPreRetainMoney)+exeUtil.doParseDouble(stringRetainMoney)) ;
        doSetValue("RetainMoneyAddUpPrint",         exeUtil.getFormatNum2(stringAddUpRetainMoney)) ;              // 累計保留 金額
        //if("B3018".equals(getUser()))  messagebox("累計保留 金額---"+exeUtil.getFormatNum2(stringAddUpRetainMoney)) ;
        
        
        
        // 扣款 金額
        doSetValue("CheapenMoneyPrint",             exeUtil.getFormatNum2(stringCheapenMoney)) ;          // 本期 扣款金額
        doSetValue("CheapenMoneyAddUpPrint",  exeUtil.getFormatNum2(stringCheapenMoney)) ;          // 累計 扣款金額
        double    doubleReceiptTax                  =  0 ;
        double    doubleSupplementMoney     =  0 ;
        String    stringSupplementMoney     =  "" ;
        String    stringReceiptMoney        =  "" ;
        String    stringTemp              =  "" ;
        String    stringExistRealMoney        =  "" ;
        if(vectorDoc2M013.size()  >  0) {
            for(int  intNo=0  ;  intNo<vectorDoc2M013.size()  ;  intNo++) {
                stringReceiptMoney      =  exeUtil.getVectorFieldValue(vectorDoc2M013,  intNo,  "ReceiptMoney") ; 
                stringSupplementMoney   =  exeUtil.getVectorFieldValue(vectorDoc2M013,  intNo,  "SupplementMoney") ; 
                //
                doubleSupplementMoney  +=  exeUtil.doParseDouble(stringSupplementMoney) ;
                doubleReceiptTax              +=  exeUtil.doParseDouble(stringReceiptMoney) ;
            }
            stringTemp  =  "" ;
            
            if(doubleSupplementMoney>0  &&  !booleanDoc2M0143Exist) {
                stringTemp         =  "稅"+exeUtil.getFormatNum2(""+doubleReceiptTax)+"+保費"+exeUtil.getFormatNum2(""+doubleSupplementMoney) ;
                //setValue("CheapenMoneyTXT",  stringTemp) ;      // 無空間，不作處理
            }
        }
        
        
        // 實付金額
        if(!booleanSource  &&  "F273701,F282201,F282301,".indexOf(stringCostID)==-1  &&  vectorCostID224.indexOf(stringCostID)  !=  -1) {
            doSetValue("ActualMoneyPrint",              exeUtil.getFormatNum2(""+doubleRealMoneySumPrint)) ;      // 本期實付  金額
            doSetValue("ActualMoneyAddUpPrint",   exeUtil.getFormatNum2(""+doubleRealMoneySumPrint)) ;      // 累計實付  金額
        } else {
            doSetValue("ActualMoneyPrint",                  exeUtil.getFormatNum2(""+doubleActualMoney)) ;          // 本期實付  金額
            doSetValue("ActualMoneyAddUpPrint",     exeUtil.getFormatNum2(""+doubleActualMoney)) ;          // 累計實付  金額
        }

        if(vectorDoc2M017.size()  >  0) {
            // 前期估驗
            stringPercent  =  convert.FourToFive(""+(doublePaidUpMoney / doubleContractMoney*100),  0) ;
            
            doSetValue("RealMoneySumPrePrint_1",           exeUtil.getFormatNum2(""+doublePaidUpMoney)) ;           // 前期估驗 金額
            doSetValue("RealMoneySumPrePercent_1",      stringPercent) ;                                // 前期估驗 比例
            // 前期估驗小計
            doSetValue("RealMoneySumPrePrint2",           exeUtil.getFormatNum2(""+doublePaidUpMoney)) ;            // 前期估驗合計 金額
            doSetValue("RealMoneySumPrePercent2",      stringPercent) ;                                 // 前期估驗合計 數量(無空間)
            // 累計估驗
            String  stringRealMoneySumAddUpPrint   =  ""+(doublePaidUpMoney  +  doubleRealMoneySumPrint) ;
                 stringPercent                 =  convert.FourToFive(""+(exeUtil.doParseDouble(stringRealMoneySumAddUpPrint)/doubleContractMoney*100),  0) ;
            doSetValue("RealMoneySumAddUpPrint_1",            exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint)) ;      // 累計估驗 金額
            doSetValue("RealMoneySumAddUpPercent_1",      stringPercent) ;                                // 累計估驗 數量
            // 累計估驗小計
            doSetValue("RealMoneySumAddUpPrint2",          exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint)) ;     // 累計估驗合計 金額
            doSetValue("RealMoneySumAddUpPercent2",     stringPercent) ;                                  // 累計估驗合計 數量

            
            if(booleanSource){
                // 2011/12/02　行銷
                // 前期－實付金額
                doubleTemp  =    doublePaidUpMoney  //exeUtil.doParseDouble(stringRealMoneySumAddUpPrint)
                          - exeUtil.doParseDouble(stringPreRetainMoney)
                          - exeUtil.doParseDouble(getValue("CheapenMoneyPrePrint").replaceAll(",",  ""));
                stringTemp  =  exeUtil.getFormatNum2(""+doubleTemp) ;
                 //if("B3018".equals(getUser()))  messagebox("(估驗："+stringRealMoneySumAddUpPrint+")(保留："+stringPreRetainMoney+")(扣款："+getValue("CheapenMoneyPrePrint").replaceAll(",",  "")+")") ;
                doSetValue("ActualMoneyPrePrint",  stringTemp) ;
                // 本期－實付金額
                doubleTemp  =    doubleRealMoneySumPrint
                              - exeUtil.doParseDouble(getValue("RetainMoneyPrint").replaceAll(",",  ""))
                              - exeUtil.doParseDouble(getValue("CheapenMoneyPrint").replaceAll(",",  ""));
                stringTemp  =  exeUtil.getFormatNum2(""+doubleTemp) ;
                doSetValue("ActualMoneyPrint",  stringTemp) ;
                // 累計－實付金額
                doubleTemp  =    exeUtil.doParseDouble(getValue("RealMoneySumAddUpPrint2").replaceAll(",",  ""))
                            - exeUtil.doParseDouble(getValue("RetainMoneyAddUpPrint").replaceAll(",",  ""))
                            - exeUtil.doParseDouble(getValue("CheapenMoneyAddUpPrint").replaceAll(",",  ""));
                stringTemp  =  exeUtil.getFormatNum2(""+doubleTemp) ;
                doSetValue("ActualMoneyAddUpPrint",  stringTemp) ;
            }
        } else {
            // 前期－實付金額
            // 本期－實付金額
            doubleTemp  =    exeUtil.doParseDouble(getValue("RealMoneySumPrint2").replaceAll(",",  ""))
                          - exeUtil.doParseDouble(getValue("RetainMoneyPrint").replaceAll(",",  ""))
                          - exeUtil.doParseDouble(getValue("CheapenMoneyPrint").replaceAll(",",  ""));
            stringTemp  =  exeUtil.getFormatNum2(""+doubleTemp) ;
            doSetValue("ActualMoneyPrint",  stringTemp) ;
            // 累計－實付金額
            doubleTemp  =    exeUtil.doParseDouble(getValue("RealMoneySumAddUpPrint2").replaceAll(",",  ""))
                          - exeUtil.doParseDouble(getValue("RetainMoneyAddUpPrint").replaceAll(",",  ""))
                          - exeUtil.doParseDouble(getValue("CheapenMoneyAddUpPrint").replaceAll(",",  ""));
            stringTemp  =  exeUtil.getFormatNum2(""+doubleTemp) ;
            doSetValue("ActualMoneyAddUpPrint",  stringTemp) ;
        }
        return  true ;
    }
    public  Vector  doItemsPurchaseData(boolean  booleanSource,  String  stringRetainType,  String  stringFactoryNo,  Vector  vectorPurchaseNos,  Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  Vector  vectorDoc2M017,  FargloryUtil  exeUtil) throws  Throwable {
        String        stringBarCode           =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String        stringComNo           =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;    
        String        stringCDate              =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "CDate") ;   
        String        stringPurchaseNoExist     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PurchaseNoExist") ;    
        String        stringEDateTime         =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "EDateTime") ;  
        String        stringKindNo            =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "KindNo") ;
        String        stringDocNoType         =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNoType") ;
        String        stringKindNoFront               =  "24".equals(stringKindNo) ? "17" : "15" ;
        Vector    vectorKey             =  new  Vector() ;
        //
        if("C".equals(stringDocNoType))         return  vectorKey ;
        //if(booleanSource)                 return  vectorKey;
        //if(booleanSource  &&  !"B3018".equals(getUser()))                   return  vectorKey;
        if(!stringPurchaseNoExist.startsWith("Y")    &&  !"A".equals(stringRetainType))return vectorKey ;
        //
        String  stringPurchaseNo            =  "" ;
        String  stringSqlAndPurchaseNo    =  "" ;
        for(int  intNo=0  ;  intNo<vectorPurchaseNos.size()  ;  intNo++) {
            stringPurchaseNo  =  ""+vectorPurchaseNos.get(intNo) ;
            //
            if(!"".equals(stringSqlAndPurchaseNo))  stringSqlAndPurchaseNo  +=  " OR " ;
            stringSqlAndPurchaseNo  +=  " PurchaseNo  =  '"  +  stringPurchaseNo  +  "' " ;
        }
        //
        Vector      vectorDoc3M012          =  new  Vector() ;
        Hashtable   hashtableDoc3M012       =  new  Hashtable()  ;
        String        stringKey                       =  "" ;
        String[]    arrayTempL                 =  null ; System.out.println("getDoc3M012-------------------------------S") ;
        String[][]    retDoc3M012               =  getDoc3M012(booleanSource,  stringComNo,  stringFactoryNo,  stringCDate,  stringSqlAndPurchaseNo.replaceAll("PurchaseNo",  "DocNo"),  exeUtil,  dbDoc) ;//System.out.println("getDoc3M012-------------------------------E") ;
        for(int  intNo=0  ;  intNo<retDoc3M012.length  ;  intNo++) {
            stringKey  =  retDoc3M012[intNo][16].trim()+retDoc3M012[intNo][15].trim() ;     // DocNo   RecordNo
            //
            arrayTempL  =  (String[])  hashtableDoc3M012.get(stringKey) ;
            if(arrayTempL  ==  null) {
                arrayTempL  =  retDoc3M012[intNo] ;
                vectorDoc3M012.add(stringKey) ;
                hashtableDoc3M012.put(stringKey,  arrayTempL) ;
            } else {
                arrayTempL[17]  =  ""+(exeUtil.doParseDouble(arrayTempL[17].trim())  +  exeUtil.doParseDouble(retDoc3M012[intNo][17].trim())) ;
            }
        }
        System.out.println("doPrintPurchaseExist----------------------------S") ;
        vectorKey  =  doPrintPurchaseExist(booleanSource,  stringRetainType,  stringComNo,  stringBarCode,  stringEDateTime, stringFactoryNo,  stringSqlAndPurchaseNo,  stringKindNo,  stringKindNoFront,  vectorDoc2M012,  vectorDoc2M017,   vectorDoc3M012,  hashtableDoc3M012,  exeUtil,  dbDoc) ;
        System.out.println("doPrintPurchaseExist----------------------------E") ;
        return  vectorKey ;
    }
    public  void  doChearItem( ) throws  Throwable {
        String    stringField  =  "" ;
        String[]  arrayView  =  {  // 估驗部份
                          "No1",          "DescriptPrint3_1",   "SIZE1",        "UNIT1",        "PRICE1",
                          "No2",          "DescriptPrint3_2",   "SIZE2",        "UNIT2",        "PRICE2",
                          "No3",          "DescriptPrint3_3",   "SIZE3",        "UNIT3",        "PRICE3",
                          "No4",          "DescriptPrint3_4",   "SIZE4",        "UNIT4",        "PRICE4",
                          // 前期...
                          "No21",  "RealMoneySumPreNum_1",  "RealMoneySumPrePercent_1",  "RealMoneySumPrePrint_1",  "RealMoneySumNum_1",  "RealMoneySumPercent_1",  "RealMoneySumPrint_1",  "RealMoneySumAddUpNum_1","RealMoneySumAddUpPercent_1",  "RealMoneySumAddUpPrint_1",  
                          "No22",  "RealMoneySumPreNum_2",  "RealMoneySumPrePercent_2",  "RealMoneySumPrePrint_2",  "RealMoneySumNum_2",  "RealMoneySumPercent_2",  "RealMoneySumPrint_2",  "RealMoneySumAddUpNum_2","RealMoneySumAddUpPercent_2",  "RealMoneySumAddUpPrint_2",  
                          "No23",  "RealMoneySumPreNum_3",  "RealMoneySumPrePercent_3",  "RealMoneySumPrePrint_3",  "RealMoneySumNum_3",  "RealMoneySumPercent_3",  "RealMoneySumPrint_3",  "RealMoneySumAddUpNum_3","RealMoneySumAddUpPercent_3",  "RealMoneySumAddUpPrint_3",  
                          "No24",  "RealMoneySumPreNum_4",  "RealMoneySumPrePercent_4",  "RealMoneySumPrePrint_4",  "RealMoneySumNum_4",  "RealMoneySumPercent_4",  "RealMoneySumPrint_4",  "RealMoneySumAddUpNum_4","RealMoneySumAddUpPercent_4",  "RealMoneySumAddUpPrint_4",  
                        } ;
        for(int  intNo=0  ;  intNo<arrayView.length  ;  intNo++) {
            stringField  =  arrayView[intNo].trim() ;     if("".equals(stringField))  continue ;
            //
            setValue(stringField,  "") ;
            setVisible(stringField,  false) ;
        }
    }
    public  boolean  isA170701ItemsDataOK(Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String        stringBarCode     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String      stringTemp      =  "" ;
        String        stringCostID      =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;     if(!"A170701".equals(stringCostID))   return  false ;
        String[][]    retDoc5M0221      =  getDoc5M0221(stringBarCode,  dbDoc)  ;                   if(retDoc5M0221.length  >  4)       return  false ;
        //
        doChearItem( ) ;
        //    0  BorrowAmt    1  DateStart     2 DateEnd           3  AccrualRate       4  Formula  5  Accrual
        for(int  intNo=0  ;  intNo<retDoc5M0221.length  ;  intNo++) {
            doSetValue("No"+(intNo+1),    ""+(intNo+1)) ;
            doSetValue("No2"+(intNo+1),   ""+(intNo+1)) ;
            //
            stringTemp            =  exeUtil.getFormatNum2(retDoc5M0221[intNo][5].trim()) ;

            doSetValue("RealMoneySumPrint_"+(intNo+1),             stringTemp) ;
            doSetValue("RealMoneySumAddUpPrint_"+(intNo+1),  stringTemp) ;
            // 
            stringTemp  =  exeUtil.getFormatNum2(retDoc5M0221[intNo][0].trim()) +
                                    "("+exeUtil.getDateConvertRoc(retDoc5M0221[intNo][1].trim()).replaceAll("/","")+"～"+exeUtil.getDateConvertRoc(retDoc5M0221[intNo][2].trim()).replaceAll("/","")+")"+
                        " * "+ getDeleteZero (retDoc5M0221[intNo][3].trim(),  exeUtil)  +  " %"  +
                        " * "+retDoc5M0221[intNo][4].trim()+" " ;
            doPutValues(0,  70,  0,  stringTemp,  "DescriptPrint3_"+(intNo+1),  exeUtil) ;                    // 估驗名稱
        }
        return  true ;
    }
    public  boolean  isF293001ItemsDataOK(Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String        stringBarCode     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String      stringTemp      =  "" ;
        String        stringCostID      =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;     if(!"F293001".equals(stringCostID))   return  false ;
        String[][]    retDoc5M0222      =  getDoc5M0222(stringBarCode,  dbDoc)  ;                   if(retDoc5M0222.length  >  4)       return  false ;
        //
        doChearItem( ) ;
        //    0  InvestmentTrust    1  FundNo     2 BandNo           3  AccountNo       4  AccountName  
        //    5  Amt                      6  Unit            7  NetAmt
        for(int  intNo=0  ;  intNo<retDoc5M0222.length  ;  intNo++) {
            doSetValue("No"+(intNo+1),    ""+(intNo+1)) ;
            doSetValue("No2"+(intNo+1),   ""+(intNo+1)) ;
            //
            stringTemp           =  exeUtil.getFormatNum2(retDoc5M0222[intNo][5].trim()) ;
            doSetValue("RealMoneySumPrint_"+(intNo+1),             stringTemp) ;
            doSetValue("RealMoneySumAddUpPrint_"+(intNo+1),  stringTemp) ;
            // 
            stringTemp  =  retDoc5M0222[intNo][0].trim() +    // 投信公司
                                    retDoc5M0222[intNo][1].trim() +  // 基金統一編號
                      retDoc5M0222[intNo][2].trim() +  // 匯款銀行
                      retDoc5M0222[intNo][3].trim() +  // 匯款帳號
                      retDoc5M0222[intNo][4].trim() ;  // 匯款帳號
            doPutValues(0,  70,  0,  stringTemp,  "DescriptPrint3_"+(intNo+1),  exeUtil) ;                    // 估驗名稱
        }
        return  true ;
    }
    public  boolean  isF273701ItemsDataOK(Vector  vectorDoc5M0224,  Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  FargloryUtil  exeUtil,  talk  dbFED1) throws  Throwable {
        String      stringTemp      =  "" ;
        String        stringBarCode     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String        stringComNo     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String        stringEDateTime =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "EDateTime") ;
        String        stringCostID      =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;     if(!"F273701".equals(stringCostID))   return  false ;
        //
        if(vectorDoc5M0224.size()  >  4)      return  false ;
        //
        doChearItem( ) ;
        //  0  VOUCHER_YMD   1  VOUCHER_FLOW_NO   2  VOUCHER_SEQ_NO    3  Amt       4  FactoryNo      5  CostID       6  CostID1
        String      stringVoucherYMD      =  "" ;
        String      stringVoucherFlowNo  =  "" ;
        String      stringVoucherSeqNo   =  "" ;
        String      stringAmt           =  "" ;
        String[][]  retFED1012                =  null ;
        for(int  intNo=0  ;  intNo<vectorDoc5M0224.size()  ;  intNo++) {
            stringVoucherYMD        =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "VOUCHER_YMD") ;
            stringVoucherFlowNo   =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "VOUCHER_FLOW_NO") ;
            stringVoucherSeqNo      =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "VOUCHER_SEQ_NO") ;
            stringAmt             =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "Amt") ;
            retFED1012                    =  getFED1012(stringVoucherYMD,    stringVoucherFlowNo,   stringVoucherSeqNo,  stringComNo,    "0",  dbFED1) ;    if(retFED1012.length  ==  0)  continue ;
            //
            doSetValue("No"+(intNo+1),    ""+(intNo+1)) ;
            doSetValue("No2"+(intNo+1),   ""+(intNo+1)) ;
            //  0  DEPT_CD    1  AMT      2  DESCRIPTION_2
            stringTemp            =  exeUtil.getFormatNum2(stringAmt) ;
            doSetValue("RealMoneySumPrint_"+(intNo+1),             stringTemp) ; 
            doSetValue("RealMoneySumAddUpPrint_"+(intNo+1),  stringTemp) ;
            // 
            stringTemp  =  ""+( exeUtil.doParseDouble(retFED1012[0][1].trim())  -
                                          getAmtDoc5M0224(stringVoucherYMD,  stringVoucherFlowNo,  stringVoucherSeqNo,  stringBarCode,  stringEDateTime,  stringComNo,  exeUtil,  dbDoc)) ;
            if("01---971001---19---2".equals(stringComNo+"---"+stringVoucherYMD+"---"+stringVoucherFlowNo+"---"+stringVoucherSeqNo)) {
                // 650000000 不在我的系統內
                stringTemp  =  ""+(exeUtil.doParseDouble(stringTemp)-exeUtil.doParseDouble("650000000")) ;
            }
            stringTemp  =  exeUtil.getFormatNum2(stringTemp) ;
            stringTemp  =   retFED1012[0][0].trim()+"借款餘額"+stringTemp+"元("+retFED1012[0][2].trim()+")" ;
            doPutValues(0,  70,  0,  stringTemp,  "DescriptPrint3_"+(intNo+1),  exeUtil) ;                    // 估驗名稱
        }
        return  true ;
    }
    public  boolean  isF282302ItemsDataOK(Vector  vectorDoc5M0224,  Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  Vector  vectorDoc2M013,  FargloryUtil  exeUtil) throws  Throwable {
        String      stringTemp      =  "" ;
        String        stringCostID      =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;     if("F282302,F282303,".indexOf(stringCostID)==-1)    return  false ;
        //
        if(vectorDoc5M0224.size()  ==  0)       return  false ;
        //
        doChearItem( ) ;
        //  0  VOUCHER_YMD   1  VOUCHER_FLOW_NO   2  VOUCHER_SEQ_NO    3  Amt       4  FactoryNo      5  CostID       6  CostID1
        // 2013-05-31 楊信義
        int       intPos                  =  0 ;
        String      stringUserL             =  getUser() ;
        String          stringFactoryNoL                      =  "" ;
        String          stringReceiptTotalMoneyL        =  "" ;
        String          stringReceiptTotalMoney13L    =  "" ;
        String          stringTempL                             =  "" ;
        Vector          vectorFactoryNo12L                =  new  Vector() ;
        Vector          vectorFactoryNo13L                =  new  Vector() ;
        Hashtable   hashtablefactoryNo12L             =  new  Hashtable() ;
        Hashtable   hashtablefactoryNo13L             =  new  Hashtable() ;
        double        doubleTempL                           =  0 ;
        double        doubleSumL                              =  0 ;
        for(int  intNo=0  ;  intNo<vectorDoc2M013.size()  ;  intNo++) {
            stringFactoryNoL                =  exeUtil.getVectorFieldValue(vectorDoc2M013,  intNo,  "FactoryNo") ;
            stringReceiptTotalMoneyL  =  exeUtil.getVectorFieldValue(vectorDoc2M013,  intNo,  "ReceiptTotalMoney") ;
            //
            if(vectorFactoryNo13L.indexOf(stringFactoryNoL)  ==  -1)  vectorFactoryNo13L.add(stringFactoryNoL) ;
            //
            doubleTempL   =  exeUtil.doParseDouble(stringReceiptTotalMoneyL)+exeUtil.doParseDouble(""+hashtablefactoryNo13L.get(stringFactoryNoL)) ;
            hashtablefactoryNo13L.put(stringFactoryNoL,  convert.FourToFive(""+doubleTempL,  0)) ;
        }
        for(int  intNo=0  ;  intNo<vectorDoc2M012.size()  ;  intNo++) {
            stringFactoryNoL                =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "CostID") ;
            stringReceiptTotalMoneyL  =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "RealTotalMoney") ;
            //
            if(vectorFactoryNo12L.indexOf(stringFactoryNoL)  ==  -1)  vectorFactoryNo12L.add(stringFactoryNoL) ;
            //
            doubleTempL   =  exeUtil.doParseDouble(stringReceiptTotalMoneyL)+exeUtil.doParseDouble(""+hashtablefactoryNo12L.get(stringFactoryNoL)) ;
            hashtablefactoryNo12L.put(stringFactoryNoL,  convert.FourToFive(""+doubleTempL,  0)) ;
        }
        for(int  intNo=0  ;  intNo<vectorFactoryNo12L.size()  ;  intNo++) {
            stringFactoryNoL                     =  ""+vectorFactoryNo12L.get(intNo) ;
            stringReceiptTotalMoneyL      =  ""+hashtablefactoryNo12L.get(stringFactoryNoL) ;
            stringReceiptTotalMoney13L  =  ""+hashtablefactoryNo13L.get(stringFactoryNoL) ;
            //
            if(vectorFactoryNo13L.indexOf(stringFactoryNoL)  !=  -1)  vectorFactoryNo13L.remove(stringFactoryNoL) ;
            //
            // 以費用表格為主，列印資料
            doubleSumL  +=  exeUtil.doParseDouble(stringReceiptTotalMoneyL) ;
            intPos++ ;
            doSetValue("No"+intPos,                                   ""+intPos) ;
            doSetValue("No2"+intPos,                                  ""+intPos) ;
            stringTempL  =  stringFactoryNoL+"||"+getCost4Name("",  stringFactoryNoL,  "",  "",  dbDoc) ;  
            doPutValues(0,  70,  0,  stringTempL,  "DescriptPrint3_"+intPos,  exeUtil) ;                    // 估驗名稱
            doSetValue("RealMoneySumPrint_"+intPos,       exeUtil.getFormatNum2(stringReceiptTotalMoneyL)+"") ;
            doSetValue("RealMoneySumAddUpPrint_"+intPos,    exeUtil.getFormatNum2(stringReceiptTotalMoneyL)+"") ;
            // 列印 差額
            // F282302  繳納營所稅 無營業稅憑證 不等於 費用表格時，增加 營所稅(高)低估。
            // F282303 未分配盈餘 無營業稅憑證 不等於 費用表格時，增加 未分配盈餘(高)低估。
            doubleTempL  =  exeUtil.doParseDouble(stringReceiptTotalMoney13L)  -  exeUtil.doParseDouble(stringReceiptTotalMoneyL) ;
            doubleTempL  =  exeUtil.doParseDouble(convert.FourToFive(""+doubleTempL,  0)) ;
            
            if(doubleTempL  !=  0) {
                if("F282302".equals(stringFactoryNoL)) {
                    if(doubleTempL  >  0) {
                        stringTempL  =  "營所稅低估" ;
                    } else {
                        stringTempL  =  "營所稅高估" ;
                    }
                } else if("F282303".equals(stringFactoryNoL)) {
                    if(doubleTempL  >  0) {
                        stringTempL  =  "未分配盈餘低估" ;
                    } else {
                        stringTempL  =  "未分配盈餘高估" ;
                    }
                } else {
                    stringTempL  =  "" ;
                }
                //
                doubleSumL  +=  doubleTempL ;
                intPos++ ;
                doSetValue("No"+intPos,                                 ""+intPos) ;
                doSetValue("No2"+intPos,                              ""+intPos) ;
                doPutValues(0,  70,  0,  stringTempL,  "DescriptPrint3_"+intPos,  exeUtil) ;                    // 估驗名稱
                doSetValue("RealMoneySumPrint_"+intPos,               exeUtil.getFormatNum2(convert.FourToFive(""+doubleTempL,0))+"") ;
                doSetValue("RealMoneySumAddUpPrint_"+intPos,     exeUtil.getFormatNum2(convert.FourToFive(""+doubleTempL,0))+"") ;
            }
        }
        for(int  intNo=0  ;  intNo<vectorFactoryNo13L.size()  ;  intNo++) {
            stringFactoryNoL                =  ""+vectorFactoryNo13L.get(intNo) ;
            stringReceiptTotalMoneyL  =  ""+hashtablefactoryNo13L.get(stringFactoryNoL) ;
            doubleSumL                      +=  exeUtil.doParseDouble(stringReceiptTotalMoneyL) ;
            // 列印 差額
            if("F282302".equals(stringFactoryNoL)) {
                stringTempL  =  "營所稅(高)低估" ;
            } else if("F282303".equals(stringFactoryNoL)) {
                stringTempL  =  "未分配盈餘(高)低估" ;
            } else {
                stringTempL  =  "" ;
            }
            //
            intPos++ ;
            doSetValue("No"+intPos,                                 ""+intPos) ;
            doSetValue("No2"+intPos,                                ""+intPos) ;
            doPutValues(0,  70,  0,  stringTempL,  "DescriptPrint3_"+intPos,  exeUtil) ;                    // 估驗名稱
            doSetValue("RealMoneySumPrint_"+intPos,     exeUtil.getFormatNum2(stringReceiptTotalMoneyL)) ;
            doSetValue("RealMoneySumAddUpPrint_"+intPos,     exeUtil.getFormatNum2(stringReceiptTotalMoneyL)) ;
        }
        stringTempL  =  convert.FourToFive(""+doubleSumL,  0) ;
        doSetValue("RealMoneySumPrint2",             exeUtil.getFormatNum2(stringTempL)) ;  
        doSetValue("RealMoneySumAddUpPrint2",  exeUtil.getFormatNum2(stringTempL)) ;  
        doSetValue("ActualMoneyPrint",                    exeUtil.getFormatNum2(stringTempL)) ;   
        doSetValue("ActualMoneyAddUpPrint",        exeUtil.getFormatNum2(stringTempL)) ;  
        return  true ;
    }
    public  boolean  isCostID224ItemsDataOK(boolean  booleanSource,  Vector  vectorCostID224,  Vector  vectorDoc5M0224,  Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  Vector  vectorDoc2M013,  FargloryUtil  exeUtil,  talk  dbFED1) throws  Throwable {
        String        stringCostID      =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;     
        if(booleanSource)                     return  false ;
        //if("B3018".equals(getUser().toUpperCase()))messagebox("立沖對應傳票stringCostID("+stringCostID+")1111") ;
        if(vectorCostID224.indexOf(stringCostID)  ==  -1)   return  false ;
        //if("B3018".equals(getUser().toUpperCase()))messagebox("立沖對應傳票("+vectorDoc5M0224.size()+")2222") ;
        //
        String        stringAmtL                 =  "" ;
        String        stringFactoryNoL        =  "" ;
        String        stringCostIDL         =  "" ;
        String        stringCostID1L          =  "" ;
        String        stringKeyL            =  "" ;
        String      stringTXT       =  "" ;
        String      stringTemp        =  "" ;
        String[]      arrayTempL                =  null ;
        Vector        vectorTableDataT      =  new  Vector() ;
        Vector        vectorKeyT                =  new  Vector() ;
        Hashtable   hashtableTempL      =  null ;
        double    doubleTemp        =  0 ;
        //  0  VOUCHER_YMD   1  VOUCHER_FLOW_NO   2  VOUCHER_SEQ_NO    3  Amt       4  FactoryNo      5  CostID       6  CostID1
        for(int  intNo=0  ;  intNo<vectorDoc5M0224.size()  ;  intNo++) {
            stringFactoryNoL          =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "FactoryNo") ;
            stringCostIDL                =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "CostID") ;
            stringCostID1L              =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "CostID1") ;
            stringAmtL                    =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  intNo,  "Amt") ;
            stringKeyL                    =  stringFactoryNoL+"%-%"+stringCostIDL +"%-%"+stringCostID1L+"%-%"+(exeUtil.doParseDouble(stringAmtL)>0?"A":"B");
            //
            if(vectorKeyT.indexOf(stringKeyL)  ==  -1) {
                vectorTableDataT.add(vectorDoc5M0224.get(intNo)) ;
                vectorKeyT.add(stringKeyL) ;
            } else {
                hashtableTempL      =  (Hashtable)vectorTableDataT.get(vectorKeyT.indexOf(stringKeyL)) ;
                
                doubleTemp  =  (exeUtil.doParseDouble(""+hashtableTempL.get("Amt"))  +   exeUtil.doParseDouble(stringAmtL)) ;
                hashtableTempL.put("Amt",  convert.FourToFive(""+doubleTemp,  0)) ;
            }
        }
        stringTXT  =  getCost4Name("",  stringCostID,  "",  "",  dbDoc) ;
        //if("B3018".equals(getUser().toUpperCase()))messagebox("立沖對應傳票("+vectorTableDataT.size()+")3333") ;
        if(vectorTableDataT.size()  >  4)   return  false ;
        //
        doChearItem( ) ;
        //
        for(int  intNo=0  ;  intNo<vectorTableDataT.size()  ;  intNo++) {
            if(intNo>3) continue ;
            //
            doSetValue("No"+(intNo+1),  ""+(intNo+1)) ;
            doSetValue("No2"+(intNo+1),  ""+(intNo+1)) ;
            //
            stringCostID  =  exeUtil.getVectorFieldValue(vectorTableDataT,  intNo,  "CostID")+exeUtil.getVectorFieldValue(vectorTableDataT,  intNo,  "CostID1") ;
            stringTXT       =  getCost4Name("",  stringCostID,  "",  "",  dbDoc) ;
            //if("B3018".equals(getUser().toUpperCase()))messagebox("立沖對應傳票  stringTXT("+stringTXT+")4444") ;
            stringTemp  =   getObjectNameFED1005(exeUtil.getVectorFieldValue(vectorTableDataT,  intNo,  "FactoryNo"),  dbFED1) +  " "  +  stringTXT+"(詳附件)" ;
            if(vectorTableDataT.size()  ==  1) {
                //if("B3018".equals(getUser().toUpperCase()))messagebox("立沖對應傳票  ("+stringTemp+")5555") ;
                doPutValues(4,  30,  0,  stringTemp,  "DescriptPrint2_",  exeUtil) ;                    // 估驗名稱
            } else {
                doPutValues(0,  70,  0,  stringTemp,  "DescriptPrint2_",  exeUtil) ;                    // 估驗名稱
            }
            //
            doSetValue("RealMoneySumPrint_"+(intNo+1),           exeUtil.getFormatNum2(exeUtil.getVectorFieldValue(vectorTableDataT,  intNo,  "Amt"))) ;
            doSetValue("RealMoneySumAddUpPrint_"+(intNo+1),  exeUtil.getFormatNum2(exeUtil.getVectorFieldValue(vectorTableDataT,  intNo,  "Amt"))) ;
        }
        return  true ;
    }
    public  void  doSumPrint(boolean  booleanSource,  Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String        stringComNo       =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;      
        //
        String          strinProjectID1L         =  "" ;
        String          strinDepartNoL            =  "" ;
        String          strinKeyL                     =  "" ;
        String          stringLimit                   =  "%---%" ;
        String          stringInOutL              =  "" ;
        String          stringInOutOldL         =  "" ;
        String          stringDepartNoL         =  "" ;
        String          stringProjectID1L       =  "" ;
        String          stringDepartNoOldL  =  "" ;
        String      stringCostIDL     =  "" ;
        String      stringCostID1L      =  "" ;
        String      stringRealTotalMoney  =  "" ;
        String        stringFieldValue      =  "" ;
        Vector    vectorCostID              =  new  Vector() ;  
        Hashtable   hashtableMoney          =  new  Hashtable() ;
        boolean       booleanSame           =  true ;
        boolean       booleanCostID013        =  false ;
        for(int  i=0  ;  i<vectorDoc2M012.size()  ;  i++) {
              stringInOutL              =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "InOut") ;
              stringDepartNoL         =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "DepartNo") ;
              stringProjectID1L       =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "ProjectID1") ;
              stringCostIDL             =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "CostID") ;
              stringCostID1L         =  (booleanSource) ? exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "CostID1") : "" ;
              //
              if("013".equals(stringCostIDL+stringCostID1L))  booleanCostID013  =  true ;
              //
              if(!"".equals(stringProjectID1L))  stringDepartNoL  =  stringProjectID1L ;
              if(i  !=  0) {
                  if(!stringInOutOldL.equals(stringInOutL))            {booleanSame  =  false ;  break ;}
                  if(!stringDepartNoOldL.equals(stringDepartNoL))  {booleanSame  =  false ;  break ;}
              }
              stringInOutOldL        =  stringInOutL ;
              stringDepartNoOldL  =  stringDepartNoL ;
        }
        if(vectorDoc2M012.size()<=4  &&  !booleanCostID013) {
            doChearItem( ) ;
        }
        for(int  i=0  ;  i<vectorDoc2M012.size()  ;  i++) {
            strinDepartNoL             =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "DepartNo") ;
            stringProjectID1L          =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "ProjectID1") ;
            stringCostIDL                =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "CostID") ;
            stringCostID1L              =  (booleanSource) ? exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "CostID1")  : "" ;
            stringRealTotalMoney  =  exeUtil.getVectorFieldValue(vectorDoc2M012,  i,  "RealTotalMoney") ;
            //
            stringDepartNoOldL  =  strinDepartNoL ;
            //
            if(!"".equals(stringProjectID1L))  stringDepartNoOldL  =  stringProjectID1L ;
            //
            strinDepartNoL    =  "" ;
            strinProjectID1L  =  "" ;
            //
            if(!"".equals(strinProjectID1L)) {
                if(exeUtil.doParseDouble(stringRealTotalMoney)>0) {
                    strinKeyL  =  stringCostIDL+stringLimit+stringCostID1L+stringLimit+"O"+stringLimit+strinProjectID1L+stringLimit+"P" ;  // 正數
                } else {
                    strinKeyL  =  stringCostIDL+stringLimit+stringCostID1L+stringLimit+"O"+stringLimit+strinProjectID1L+stringLimit+"N" ;  // 負數
                }
            } else {
                if(exeUtil.doParseDouble(stringRealTotalMoney)>0) {
                    strinKeyL  =  stringCostIDL+stringLimit+stringCostID1L+stringLimit+"I"+stringLimit+strinDepartNoL+stringLimit+"P" ;  // 正數
                } else {
                    strinKeyL  =  stringCostIDL+stringLimit+stringCostID1L+stringLimit+"I"+stringLimit+strinDepartNoL+stringLimit+"N" ;  // 負數
                }
            }
            // 列印 [部門 or 案別]+[請款代碼名稱] 
            if(vectorDoc2M012.size()  <=  4) {
                if(booleanSame) {
                    //  [請款代碼名稱]
                    stringFieldValue  =  getCost4Name(stringComNo,  stringCostIDL,  stringCostID1L,  "",  dbDoc) ;
                } else {
                    //  [部門 or 案別]+[請款代碼名稱]
                    stringFieldValue  =  stringDepartNoOldL+  " "  +getCost4Name(stringComNo,  stringCostIDL,  stringCostID1L,  "",  dbDoc) ;
                }
                doSetValue("No"+(i+1),            ""+(i+1)) ;  
                doSetValue("No2"+(i+1),           ""+(i+1)) ;  
                doPutValues(0,  70,  0,  stringFieldValue,  "DescriptPrint3_"+(i+1),  exeUtil) ;                    // 估驗名稱
                doSetValue("RealMoneySumPrint_"+(i+1),          exeUtil.getFormatNum2(stringRealTotalMoney)) ;        // 本期   
                doSetValue("RealMoneySumAddUpPrint_"+(i+1),     exeUtil.getFormatNum2(stringRealTotalMoney)) ;    // 累計
            }
            stringRealTotalMoney  =  ""+(exeUtil.doParseDouble(stringRealTotalMoney)+exeUtil.doParseDouble(""+hashtableMoney.get(strinKeyL))) ;
            hashtableMoney.put(strinKeyL,  stringRealTotalMoney) ;
            if(vectorCostID.indexOf(strinKeyL)  ==  -1) vectorCostID.add(strinKeyL) ;
        }
        if(vectorDoc2M012.size()  <=  4)    return ;
        if(vectorCostID.size()  >  4)       return ;
        if(booleanCostID013)          return ;
        //
        String    stringFieldValue1 =  "" ;
        String[]    arrayCostID          =  (String[]) vectorCostID.toArray(new  String[0]) ;   Arrays.sort(arrayCostID) ;
        String[]   arrayTemp      =  null ;
        doChearItem( ) ;
        for(int  i=0  ;  i<4  ;  i++) {
              if(i>=arrayCostID.length)             continue ;
              //
              strinKeyL                      =  arrayCostID[i] ;
              arrayTemp                    =  convert.StringToken(strinKeyL,  stringLimit) ;
              stringCostIDL               =  arrayTemp[0] ;
              stringCostID1L             =  arrayTemp[1] ;
              strinProjectID1L           =  arrayTemp[3] ;
              stringFieldValue1          =  exeUtil.getFormatNum2(convert.FourToFive(""+hashtableMoney.get(strinKeyL),  0)) ;
              if("".equals(strinProjectID1L)) {
                  stringFieldValue  =  getCost4Name(stringComNo,  stringCostIDL,  stringCostID1L,  "",  dbDoc)+"(詳附件)" ;
              } else {
                  stringFieldValue  =  getCost4Name(stringComNo,  stringCostIDL,  stringCostID1L,  "",  dbDoc)+"("+strinProjectID1L+")" ;
              }
              doSetValue("No"+(i+1),            ""+(i+1)) ;  
              doSetValue("No2"+(i+1),          ""+(i+1)) ;  
              doPutValues(0,  70,  0,  stringFieldValue,  "DescriptPrint3_"+(i+1),  exeUtil) ;
              doSetValue("RealMoneySumPrint_"+(i+1),           stringFieldValue1) ; 
              doSetValue("RealMoneySumAddUpPrint_"+(i+1),  stringFieldValue1) ;
        }
        
    }
    // 附件
    public  void  doPutDocCodeTXT(boolean  booleanSource,  Vector  vectorDoc2M010,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        if(booleanSource)  return   ;
        // 附件資訊
        String        stringBarCode     =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String        stringDocCode     =  "" ;
        String        stringDocCount    =  "" ;
        String        stringDocTXT        =  "" ;
        String[][]    retDoc5M0201      =  getDoc5M0201("Doc5M0201",  stringBarCode,  "",  dbDoc) ;  
        // 僅允許六個附件，每個附件10字元()
        for(int  intNo=0 ;  intNo<6  ;  intNo++) {
              stringDocCode   =  (intNo<retDoc5M0201.length) ? retDoc5M0201[intNo][0].trim() : "" ;
              stringDocCount  =  (intNo<retDoc5M0201.length) ? retDoc5M0201[intNo][1].trim():"" ;
              stringDocTXT     =  (intNo<retDoc5M0201.length) ? retDoc5M0201[intNo][2].trim():"" ;
              //
              if("AX9998,AX9999,".indexOf(stringDocCode+",")  !=  -1) {
                  if("AX9998,".indexOf(stringDocCode+",")!=-1){
                      stringDocCount  =  "" ;
                  }
                  stringDocCode  =   stringDocTXT ;
              } else {
                  stringDocCode   =  getDocDescriptDoc5M0291(stringDocCode,  dbDoc) ;
              }
              if(!"".equals(stringDocCode))  stringDocCode  =  (intNo+1)+". "+stringDocCode ;
              //
              if(retDoc5M0201.length>3  &&  code.StrToByte(stringDocCode).length()+stringDocCount.length()  >  10) {
                  stringDocCode   =  exeUtil.doCutStringBySize(21-stringDocCount.length(),  stringDocCode)[0]+"..." ; 
              }
              if(!"".equals(stringDocCode)  &&  !"".equals(stringDocCount))  stringDocCode  +=  "x"+stringDocCount ;
              //
              if(!"".equals(stringDocCode))doSetValue("DocCodeTXT"+(intNo+1),  stringDocCode) ;
        }
    }
    // 工程名稱(案別)：
    public  boolean  isPutProjectOK(Vector  vectorDoc2M010,  Vector  vectorDoc2M012,  Vector  vectorCostID224,   FargloryUtil  exeUtil) throws  Throwable {
        String        stringBarCode               =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String      stringInOutFirst            =  "" ;
        String      stringProjectFirst            =  "" ;
        String      stringProjectShow           =  "" ;
        if(vectorDoc2M012.size()  !=  0) {
            stringInOutFirst          =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "InOut") ;
            if("I".equals(stringInOutFirst)) {
                stringProjectFirst  =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "DepartNo") ;
                stringProjectShow  =  stringProjectFirst ;
            } else {
                stringProjectFirst  =    exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "DepartNo")  +  
                              exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "ProjectID")  + 
                              exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "ProjectID1")  ;
                                  stringProjectShow  =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "ProjectID1")  ;
            }
        }
        String  stringInOut       =  "" ;
        String  stringDepart      =  "" ;
        String  stringProjectID   =  "" ;
        String  stringProjectID1    =  "" ;
        String  stringProject     =  "" ;
        String  stringCostID         =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;
        for(int  intRowNo=0  ;  intRowNo<vectorDoc2M012.size()  ;  intRowNo++) {
            stringInOut          =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intRowNo,  "InOut") ;
            stringDepart         =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intRowNo,  "DepartNo") ;
            stringProjectID    =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intRowNo,  "ProjectID") ;
            stringProjectID1   =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intRowNo,  "ProjectID1") ;
            if(stringInOut.equals(stringInOutFirst)) {
                if("I".equals(stringInOutFirst)) {
                    stringProject  =  stringDepart ;
                } else {
                    stringProject  =  stringDepart  +  stringProjectID  +  stringProjectID1 ;
                }
                if(!stringProjectFirst.equals(stringProject)) {
                    // 科別不相同
                    stringProjectShow  =  "詳附件" ;
                    break ;
                }
            } else {
                // 非一致的內外業
                stringProjectShow  =  "詳附件" ;
                break ;
            }
        }
        Vector    vectorDoc2M0143           =  exeUtil.getQueryDataHashtable("Doc2M0143",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        if(vectorDoc2M0143.size()==0  &&  vectorDoc2M012.size()>0  &&  vectorCostID224.indexOf(stringCostID)==-1  &&  "".equals(stringProjectShow)) {
            messagebox(" [案別] 為空白，請重新列印。") ;
            return  false ;
        }
        doSetValue("ProjectPrint",  stringProjectShow) ;
        return  true ;
    }   
    public  String  doPutFactoryNoOK(boolean  booleanSource,  Vector  vectorDoc2M010,  Vector  vectorDoc2M011,  Vector  vectorDoc2M012,  Vector  vectorDoc2M013,  Vector  vectorDoc2M017,  Vector  vectorDoc5M0224,  Vector  vectorCostID224,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFED1,  talk  dbFE3D) throws  Throwable {
        String      stringBarCode             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String      stringComNo             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String      stringPurchaseNoExist       =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PurchaseNoExist") ;
        String      stringFactoryNoSpec         =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "FactoryNoSpec") ;
        String      stringDocNoType               =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNoType") ;
        String      stringFactoryNo           =  "" ;
        String      stringTemp              =  "" ;
        String      stringCostID              =  "" ;
        String      stringCostID1             =  "" ;
        String[][]    retFED1005              =  null ;
        boolean     booleanPocketMoney          =  false ;
        boolean     booleanCheck                    =  true ;
        Vector    vectorDoc5M0211           =  booleanSource ? new  Vector() : exeUtil.getQueryDataHashtable("Doc5M0211",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        Vector    vectorFactoryNo         =  new  Vector() ;
        System.out.println("----------------------------------------------1") ;
        if(vectorDoc2M012.size()>0) {
            stringCostID  =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "CostID") ;
            // 零用金
            if(",31,32,".indexOf(stringCostID)  !=  -1)  booleanPocketMoney  =  true ;
        }
        // 廠商
        stringFactoryNo  =  exeUtil.getVectorFieldValue(vectorDoc2M011,  0,  "FactoryNo") ;
        if("".equals(stringFactoryNo)) {
            stringFactoryNo  =  exeUtil.getVectorFieldValue(vectorDoc2M013,  0,  "FactoryNo") ; 
        }
        if("".equals(stringFactoryNo)) {
            stringFactoryNo  =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "FactoryNo") ; 
        }
        if(booleanPocketMoney  &&  !stringPurchaseNoExist.startsWith("Y")) {
            String      stringInOut     =  exeUtil.getVectorFieldValue(vectorDoc2M012,  0,  "InOut") ;    
                    stringFactoryNo   =  ("I".equals(stringInOut)) ? "Z0001" : "Z0007" ;
            System.out.println("stringFactoryNo("+stringFactoryNo+")----------------------------------------------2") ;
        } else {
            if(!"".equals(stringFactoryNoSpec))stringFactoryNo  =  stringFactoryNoSpec ;
            if("A".equals(stringDocNoType)) {
                // 一般、代收代付 
                String[][]  retDoc5M0225  =  getDoc5M0225(stringBarCode,  dbDoc) ;
                if(retDoc5M0225.length  >  0) {
                    //    0  ComNo    1  BarCodeF     2 RecordNo           3  CostID       4  RealTotalMoney  
                    String  stringComNoL  =  retDoc5M0225[0][0].trim() ;
                    retFED1005  =  getFE3D70(stringComNoL,  dbFE3D) ;
                    if(retFED1005.length  >  0) {
                        stringFactoryNo   = retFED1005[0][2].trim() ;
                        booleanCheck     =  false ;
                    }
                    if("".equals(stringFactoryNo)) {
                        for(int  intNo=0  ;  intNo<vectorDoc2M011.size()  ;  intNo++) {
                            stringTemp  =  exeUtil.getVectorFieldValue(vectorDoc2M011,  intNo,  "FactoryNo") ;
                            if(vectorFactoryNo.indexOf(stringTemp)  ==  -1)vectorFactoryNo.add(stringTemp) ;
                        }
                        for(int  intNo=0  ;  intNo<vectorDoc2M013.size()  ;  intNo++) {
                            stringTemp  =  exeUtil.getVectorFieldValue(vectorDoc2M013,  intNo,  "FactoryNo") ;
                            if(vectorFactoryNo.indexOf(stringTemp)  ==  -1)vectorFactoryNo.add(stringTemp) ;
                        }
                        for(int  intNo=0  ;  intNo<vectorDoc5M0211.size()  ;  intNo++) {
                            stringTemp  =  exeUtil.getVectorFieldValue(vectorDoc5M0211,  intNo,  "FactoryNo") ;
                            if(vectorFactoryNo.indexOf(stringTemp)  ==  -1)vectorFactoryNo.add(stringTemp) ;
                        }
                        if(vectorFactoryNo.size()  >  0) {
                            stringFactoryNo         =  ""+vectorFactoryNo.get(0) ;  // 統一編號
                        }
                    }
                }
                System.out.println("stringFactoryNo("+stringFactoryNo+")----------------------------------------------3") ;
            } else if("D".equals(stringDocNoType)) {
                // 借款沖銷
                Vector    vectorDoc5M0202       =  exeUtil.getQueryDataHashtable("Doc5M0202",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ORDER BY  RecordNo ",  dbDoc) ;
                if(vectorDoc5M0202.size()  >  0) {
                    String          stringBarCodeBorrow  =  "" ;
                    String      stringBorrowNo      =  exeUtil.getVectorFieldValue(vectorDoc5M0202,  0,  "BorrowNo") ;
                    Hashtable   hashtableAnd        =  new  Hashtable() ;
                    hashtableAnd.put("ComNo",  stringComNo) ;
                    hashtableAnd.put("KindNo",  "26") ;
                    hashtableAnd.put("DocNo",   stringBorrowNo) ;
                    stringBarCodeBorrow  =  exeUtil.getNameUnion("BarCode",  "Doc5M030",  "",  hashtableAnd,  dbDoc) ;
                    if(!"".equals(stringBarCodeBorrow)) {
                        stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  "Doc5M030",  " AND  BarCode  =  '"+stringBarCodeBorrow+"' ",  new  Hashtable(),  dbDoc) ;    
                        if("".equals(stringFactoryNo)) {
                            stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  "Doc5M031",  " AND  BarCode  =  '"+stringBarCodeBorrow+"' ",  new  Hashtable(),  dbDoc) ;    
                        }
                        if("".equals(stringFactoryNo)) {
                            stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  "Doc5M033",  " AND  BarCode  =  '"+stringBarCodeBorrow+"' ",  new  Hashtable(),  dbDoc) ;    
                        }
                    }
                }
                System.out.println("stringFactoryNo("+stringFactoryNo+")----------------------------------------------4") ;
            } else  if("B".equals(stringDocNoType)) {
                // 逐月開發票
                stringFactoryNo   =  stringFactoryNoSpec ;
                System.out.println("stringFactoryNo("+stringFactoryNo+")----------------------------------------------5") ;
            } else if("C".equals(stringDocNoType)) {
                Vector    vectorDoc5M0220     =  exeUtil.getQueryDataHashtable("Doc5M0220",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
                        stringFactoryNo     =  exeUtil.getVectorFieldValue(vectorDoc5M0220,  0,  "FactoryNo") ;
            }
        }
        System.out.println("stringFactoryNo("+stringFactoryNo+")----------------------------------------------6") ;
        for(int  intNo=0  ;  intNo<vectorDoc2M012.size()  ;  intNo++) {
            stringCostID    =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "CostID") ;
            stringCostID1   =  exeUtil.getVectorFieldValue(vectorDoc2M012,  intNo,  "CostID1") ;
            if(vectorFactoryNo.size()>1  &&  ",810,,".indexOf(","+stringCostID+stringCostID1+",")!=-1) {
                  retFED1005         =  new  String[1][2] ;
                 retFED1005[0][0]  =  "" ;
                 retFED1005[0][1]  =  "" ;
                 booleanCheck       =  false ;
                break ;
            }
            if(vectorFactoryNo.size()>1  &&  ",F304101,".indexOf(","+stringCostID+stringCostID1+",")!=-1) {
                  retFED1005         =  new  String[1][2] ;
                 retFED1005[0][0]  =  "" ;
                 retFED1005[0][1]  =  "" ;
                 booleanCheck       =  false ;
                break ;
            }
            if(!stringPurchaseNoExist.startsWith("Y")) {
                // 多廠商，不列印廠商名稱
                if(",F304001,A010107,".indexOf(","+stringCostID+",")!=-1) {
                    retFED1005          =  new  String[1][2] ;
                    retFED1005[0][0]  =  "" ;
                    retFED1005[0][1]  =  "" ;
                    booleanCheck     =  false ;
                    break ;
                }
                if(",100,101,420,".indexOf(","+stringCostID+stringCostID1+",")!=-1) {
                    retFED1005  =  getFED1005("Z0001",  "",  dbDoc,  dbFED1) ;
                    booleanCheck     =  false ;
                    break ;
                }
                //2013-06-25 楊信義
                if(",A010451,A010452,A010453,A010454,A010455,A010456,".indexOf(","+stringCostID+stringCostID1+",")!=-1  &&  vectorDoc5M0224.size() > 0) {
                    retFED1005  =  getFED1005("G0014",  "",  dbDoc,  dbFED1) ;
                    booleanCheck     =  false ;
                    break ;
                }
                // 撥補零用金
                if(",F297001,A210302,A210202,A210402,C010304,".indexOf(","+stringCostID+",")!=-1) {
                    retFED1005  =  getFED1005("Z0001",  "",  dbDoc,  dbFED1) ;
                    booleanCheck     =  false ;
                    break ;
                }
                // 代管費用-雜支
                if(",D040321,".indexOf(","+stringCostID+",")!=-1) {
                    retFED1005  =  getFED1005("Z0006",  "",  dbDoc,  dbFED1) ;
                    booleanCheck     =  false ;
                    break ;
                }
                // 立沖
                // A110103(辦公室押金)、A312101(退租賃保證金)、A030100(福利金提撥)、A312105租賃退款-暫收款(不動產租賃系統專用)
                if(",A110103,A312101,A030100,A312105,".indexOf(","+stringCostID+",")!=-1) {
                    if(vectorDoc5M0224.size()  >  0) {
                        stringFactoryNo  =  exeUtil.getVectorFieldValue(vectorDoc5M0224,  0,  "FactoryNo") ;
                    }
                    break ;
                } else if(",F273701,A312103,A312104,A312105,".indexOf(","+stringCostID+",")==-1  &&  vectorCostID224.indexOf(stringCostID)!=-1) {
                    retFED1005  =  getFED1005("G0010",  "",  dbDoc,  dbFED1) ;
                    booleanCheck     =  false ;
                    break ;
                }
            }
        }
        System.out.println("stringFactoryNo("+stringFactoryNo+")----------------------------------------------7") ;
        if(booleanCheck) {
            retFED1005  =  getFED1005(stringFactoryNo,  stringComNo,  dbDoc,  dbFED1) ;
            if(retFED1005.length  ==  0) {
                stringFactoryNo  =  "Z0001" ;
                retFED1005        =  getFED1005(stringFactoryNo,  "",  dbDoc,  dbFED1) ;
                if(retFED1005.length  ==  0) {
                    messagebox(" [廠商名稱] 為空白，請重新列印。") ;
                    return  "" ;
                }
            }
        }
        String  stringFactoryName   = retFED1005[0][0].trim() ;
        String  stringTELPrint      = retFED1005[0][1].trim() ;
        doPutValues(0,  10,  0,  stringFactoryName,  "FactoryNoPrint",  exeUtil) ;
        if(stringTELPrint.length()<=12  )doSetValue("TELPrint",            stringTELPrint) ;
        return  stringFactoryNo ;
    }   
    public  boolean  isPutDocNoOK(boolean  booleanSource,  String  stringRetainType,  Vector  vectorDoc2M010,  Vector  vectorDoc2M017,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFE3D) throws  Throwable {
        String      stringBarCode               =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String      stringComNo                 =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String      stringKindNo                  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "KindNo") ;
        String      stringDocNoType             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "DocNoType") ;
        String      stringKindNoFront                   =  "24".equals(stringKindNo) ? "17" : "15" ;
        String    stringPurchaseNoExist       =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "PurchaseNoExist") ;
        // 借款單 
        Vector  vectorDoc5M0202         =  exeUtil.getQueryDataHashtable("Doc5M0202",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ORDER BY  RecordNo ",  dbDoc) ;
        String    stringBorrowNo1       =  (vectorDoc5M0202.size()==0)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  0,  "BorrowNo1") ;
        String    stringBorrowNo2       =  (vectorDoc5M0202.size()==0)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  0,  "BorrowNo2") ;
        String    stringBorrowNo3       =  (vectorDoc5M0202.size()==0)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  0,  "BorrowNo3") ;
        String    stringProjectID1          =  (vectorDoc5M0202.size()==0)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  0,  "ProjectID1") ;
        String    stringBorrowNo              =  stringBorrowNo1  +  stringProjectID1+"-"+stringBorrowNo2  +  "-"+ stringBorrowNo3 ;
        if(!"--".equals(stringBorrowNo))  doSetValue("BorrowNo1",  stringBorrowNo) ;
        stringBorrowNo1       =  (vectorDoc5M0202.size()>=2)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  1,  "BorrowNo1") ;
        stringBorrowNo2       =  (vectorDoc5M0202.size()>=2)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  1,  "BorrowNo2") ;
        stringBorrowNo3       =  (vectorDoc5M0202.size()>=2)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  1,  "BorrowNo3") ;
        stringProjectID1          =  (vectorDoc5M0202.size()>=2)?"":exeUtil.getVectorFieldValue(vectorDoc5M0202,  1,  "ProjectID1") ;
        stringBorrowNo            =  stringBorrowNo1  +  stringProjectID1+"-"+stringBorrowNo2  +  "-"+ stringBorrowNo3 ;
        if(!"--".equals(stringBorrowNo))  doSetValue("BorrowNo2",  stringBorrowNo) ;
        //
        if("C".equals(stringDocNoType)) {
            if("A".equals(stringRetainType)) {
                String    stringBarCodePur  =  exeUtil.getNameUnion("BarCodeRef",  "Doc5M0220",  " AND  BarCode  =  '"+stringBarCode+"' ",  new  Hashtable(),  dbDoc)  ;
                Vector  vectorDoc5M011    =  exeUtil.getQueryDataHashtable("Doc5M011",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCodePur+"' ",  dbDoc) ;
                if(vectorDoc5M011.size()  ==  0) {
                    vectorDoc5M011    =  exeUtil.getQueryDataHashtable("Doc3M011",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCodePur+"' ",  dbDoc) ;
                }
                if(vectorDoc5M011.size()  >  0) {
                    String    stringPurchaseNo1       =  exeUtil.getVectorFieldValue(vectorDoc5M011,  0,  "DocNo1") ;
                    String    stringPurchaseNo2       =  exeUtil.getVectorFieldValue(vectorDoc5M011,  0,  "DocNo2") ;
                    String    stringPurchaseNo3       =  exeUtil.getVectorFieldValue(vectorDoc5M011,  0,  "DocNo3") ;
                    String    stringPurchaseNo            =  stringPurchaseNo1  +  "-"+stringPurchaseNo2  +  "-"+ stringPurchaseNo3 ;
                    doSetValue("PurchaseNo1_1",  stringPurchaseNo) ;  
                }
            }
            return  true ;
        }
        if(!stringPurchaseNoExist.startsWith("Y"))  return  true ;
        if(booleanSource  &&  isOldVersion(vectorDoc2M010,  vectorDoc2M017,  exeUtil,  dbDoc))  return  false ;
        //
        Vector    vectorDoc2M018  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc2M018":"Doc5M028",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        if(vectorDoc2M017.size()  ==  0) {
            messagebox("查無 [請購單編號]，請重新列印。") ;
            return  false ;
        }
        if(vectorDoc2M017.size()  >  2) {
            messagebox("[請購單編號] 數量大於 2，不作列印。") ;
            return  false ;
        }
        //20180212 驗收單資料檢查移除 By B03812
        /*
        if(vectorDoc2M018.size()  ==  0) {
            messagebox("查無 [驗收單編號]，請重新列印。") ;
            return  false ;
        }
        if(vectorDoc2M018.size()  >  2) {
            messagebox("[驗收單編號] 數量大於 2，不作列印。") ;
            return  false ;
        }
        */
        // 請購單
        String    stringPurchaseNo1       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo1") ;
        String    stringPurchaseNo2       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo2") ;
        String    stringPurchaseNo3       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo3") ;
                stringProjectID1              =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "ProjectID1") ;
        String    stringPurchaseNo            =  stringPurchaseNo1  +  stringProjectID1+"-"+stringPurchaseNo2  +  "-"+ stringPurchaseNo3 ;
        if("--".equals(stringPurchaseNo)) {
            messagebox("[請購單編號] 為空白，請重新列印。") ;
            return  false ;
        }
        doSetValue("PurchaseNo1_1",  stringPurchaseNo) ;  
        if(vectorDoc2M017.size()  >  1) {
            stringPurchaseNo1       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  1,  "PurchaseNo1") ;
            stringPurchaseNo2       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  1,  "PurchaseNo2") ;
            stringPurchaseNo3       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  1,  "PurchaseNo3") ;
            stringProjectID1              =  exeUtil.getVectorFieldValue(vectorDoc2M017,  1,  "ProjectID1") ;
            stringPurchaseNo            =  stringPurchaseNo1  +  stringProjectID1+"-"+stringPurchaseNo2  +  "-"+ stringPurchaseNo3 ;
            if("--".equals(stringPurchaseNo)) {
                messagebox("[請購單編號] 為空白，請重新列印。") ;
                return  false ;
            }
            doSetValue("PurchaseNo1_2",  stringPurchaseNo) ;  
        }
        //20180212 取驗收單資料動作移除
        /*
        // 驗收單 E05500
        String    stringOptometryNo1        =  exeUtil.getVectorFieldValue(vectorDoc2M018,  0,  "OptometryNo1") ;
        String    stringOptometryNo2        =  exeUtil.getVectorFieldValue(vectorDoc2M018,  0,  "OptometryNo2") ;
        String    stringOptometryNo3        =  exeUtil.getVectorFieldValue(vectorDoc2M018,  0,  "OptometryNo3") ;
        String    stringOptometryType       =  exeUtil.getVectorFieldValue(vectorDoc2M018,  0,  "OptometryType") ;
                stringProjectID1              =  exeUtil.getVectorFieldValue(vectorDoc2M018,  0,  "ProjectID1") ;
        String    stringOptometryNo           =  stringOptometryNo1  +  stringProjectID1+"-"+stringOptometryNo2  +  "-"+ stringOptometryNo3 ;
        if("--".equals(stringOptometryNo)) {
            messagebox("[驗收單編號] 為空白，請重新列印。") ;
            return  false ;
        }
        if("B".equals(stringOptometryType)) {
            String  stringPurchaseNo1L  =  getValue("PurchaseNo1_1").trim() ;
            String  stringPurchaseNo2L  =  getValue("PurchaseNo1_2").trim() ;
            if(!"".equals(stringPurchaseNo2L)) {
                doSetValue("PurchaseNo1_2",  stringPurchaseNo2L+"(固定資產)") ;  
            }  else if(!"".equals(stringPurchaseNo1L)) {
                doSetValue("PurchaseNo1_1",  stringPurchaseNo1L+"(固定資產)") ;  
            }
        }
        */
        // 採購承辦人員
/*        Hashtable  hashtableAnd                   =  new  Hashtable();
        String        stringPurchaseEmployeeNo     =  "" ;
        String        stringKindNoPur               =  "24".equals(stringKindNo) ? "17" : "15" ;
        String        stringEmpName             =  "" ;
        hashtableAnd.put("ComNo",  stringComNo) ;
        hashtableAnd.put("DocNo",   stringPurchaseNo.replaceAll("-",  "")) ;
        hashtableAnd.put("KindNo",  stringKindNoPur) ;
        stringPurchaseEmployeeNo  =  exeUtil.getNameUnion("PurchaseEmployeeNo",  "Doc5M011",  "",  hashtableAnd,  dbDoc) ;
        if("".equals(stringPurchaseEmployeeNo)) {
            hashtableAnd.put("ComNo",  stringComNo) ;
            hashtableAnd.put("DocNo",   stringPurchaseNo.replaceAll("-",  "")) ;
            hashtableAnd.put("KindNo",  stringKindNoPur) ;
            stringPurchaseEmployeeNo  =  exeUtil.getNameUnion("PurchaseEmployeeNo",  "Doc3M011",  "",  hashtableAnd,  dbDoc) ;
        }
        if(!"".equals(stringPurchaseEmployeeNo)) {
            hashtableAnd.put("EMP_NO",            stringPurchaseEmployeeNo) ;
            stringEmpName  =  exeUtil.getNameUnion("EMP_NAME",  "FE3D05",  "",  hashtableAnd,  dbFE3D) ;
            if(!"".equals(stringEmpName)) {
                stringOptometryNo  =  stringOptometryNo +  " (採購承辦："+stringEmpName+")" ;
            }
        }*/
        //
        //20180212 取驗收單資料動作移除
        /*        
        doSetValue("OptometryNo1_1",  stringOptometryNo) ;  
        if(vectorDoc2M018.size()  >  1) {
            stringOptometryNo1        =  exeUtil.getVectorFieldValue(vectorDoc2M018,  1,  "OptometryNo1") ;
            stringOptometryNo2        =  exeUtil.getVectorFieldValue(vectorDoc2M018,  1,  "OptometryNo2") ;
            stringOptometryNo3        =  exeUtil.getVectorFieldValue(vectorDoc2M018,  1,  "OptometryNo3") ;
            stringProjectID1              =  exeUtil.getVectorFieldValue(vectorDoc2M018,  1,  "ProjectID1") ;
            stringOptometryNo           =  stringOptometryNo1  +  stringProjectID1+"-"+stringOptometryNo2  +  "-"+ stringOptometryNo3 ;
            if("--".equals(stringOptometryNo)) {
                messagebox("[驗收單編號] 為空白，請重新列印。") ;
                return  false ;
            }
            doSetValue("OptometryNo1_2",  stringOptometryNo) ;  
        }
        */
        // 上期請款編號 
        doSetLastDocNo(booleanSource,  vectorDoc2M010,  vectorDoc2M017,  exeUtil,  dbDoc) ;
        // 合約金額 2016-06-04 
        doSetContractNo(stringComNo,  stringKindNoFront,  vectorDoc2M017,  booleanSource,  exeUtil,  dbDoc) ;
        return  true ;
    }
    public  boolean  isOldVersion(Vector  vectorDoc2M010,  Vector  vectorDoc2M017,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String        stringComNo             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String      stringPurchaseNo1       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo1") ;
        String      stringPurchaseNo2       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo2") ;
        String      stringPurchaseNo3       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo3") ;
        String      stringProjectID1              =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "ProjectID1") ;
        String[][]    retDoc3M010               =  getDoc3M010(stringPurchaseNo1+stringProjectID1,  stringPurchaseNo2,  stringPurchaseNo3,  "",  stringComNo,  dbDoc) ;
        return  (retDoc3M010.length  >  0)  ;
    }
    public  void  doSetLastDocNo(boolean  booleanSource,  Vector  vectorDoc2M010,  Vector  vectorDoc2M017,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String        stringComNo             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String        stringBarCode             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String        stringKindNo            =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "KindNo") ;
        String      stringPurchaseNo1       =  "" ;
        String      stringPurchaseNo2       =  "" ;
        String      stringPurchaseNo3       =  "" ;
        String      stringProjectID1              =  "" ;
        String      stringFactoryNo             =  "" ;
        Vector      vectorPurchaseNo          =  new  Vector() ;
        for(int  intNo=0  ;  intNo<vectorDoc2M017.size() ;  intNo++) {
            stringPurchaseNo1       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo1") ;
            stringPurchaseNo2       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo2") ;
            stringPurchaseNo3       =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo3") ;
            stringProjectID1          =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "ProjectID1") ;
            stringFactoryNo         =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "FactoryNo") ;
            //
            vectorPurchaseNo.add(stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3) ;
        }
        // 上期請款編號 
        String  stringLastDoc  =  getLastDocNo(stringComNo,  stringFactoryNo,  vectorPurchaseNo,  stringBarCode,  "",  stringKindNo,  booleanSource,  exeUtil,  dbDoc)  ;
        doSetValue("LastDocNo",  stringLastDoc) ;
    }
    public  void  doSetContractNo(String  stringComNo,  String  stringKindNoFront,  Vector   vectorDoc2M017,  boolean  booleanSource,  FargloryUtil  exeUtil,  talk  dbDoc)throws  Throwable {
        if(!"B3018".equals(getUser()))        return ;
        if(booleanSource)                 return ;
        //
        talk      dbConstAsk                  =  getTalk(""+get("put_Const_Ask")) ;
        String      stringPurchaseNo1           =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo1") ;
        String      stringPurchaseNo2           =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo2") ;
        String      stringPurchaseNo3           =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo3") ;
        String      stringFactoryNo                 =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "FactoryNo") ;
        String      stringProjectID1                =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "ProjectID1") ;
        String      stringPurchaseNo              =  stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3 ;
        String      stringBarCodePur          =  "" ;
        String      stringContractNo            =  "" ;
        String        stringPrdocode              =  "" ;
        Hashtable   hashtableAnd              =  new  Hashtable() ;
        Hashtable   hashtableRelContractDetail    =  new  Hashtable() ;
        Vector      vectorRelContractDetail       =  null ;
        //
        hashtableAnd.put("ComNo",  stringComNo) ;
        hashtableAnd.put("KindNo",  stringKindNoFront) ;
        hashtableAnd.put("DocNo",   stringPurchaseNo) ;
        stringBarCodePur  =  exeUtil.getNameUnion("BarCode",  (booleanSource) ? "Doc3M011" : "Doc5M011",  "",  hashtableAnd,  dbDoc) ;  if("".equals(stringBarCodePur))  return ;
        //
        stringPrdocode  =  exeUtil.getNameUnion("prdocode",  "prdt",  " AND  social  =  '"+stringFactoryNo+"' ",  new  Hashtable(),  dbConstAsk) ;
        
        hashtableAnd.put("barcode",     stringBarCodePur) ;
        hashtableAnd.put("prdocode",  stringPrdocode) ;
        vectorRelContractDetail  =  exeUtil.getQueryDataHashtable("rel_contract_detail",  hashtableAnd,  "",  dbConstAsk) ; if(vectorRelContractDetail.size()  ==  0)   return ;
        hashtableRelContractDetail  =  (Hashtable)  vectorRelContractDetail.get(0) ;                                    if(hashtableRelContractDetail  ==  null)    return ;
        //
        String  stringCcaseCode  =  (""+hashtableRelContractDetail.get("ccasecode")).trim() ;   if("null".equals(stringCcaseCode))  stringCcaseCode  =  "" ;
        String  stringContractIID   =  (""+hashtableRelContractDetail.get("contract_iid")).trim() ;  if("null".equals(stringContractIID))  stringContractIID    =  "" ;
        //
        setValue("ContractNo",  stringCcaseCode+"-"+stringContractIID) ;
    }
    public  boolean  isPutContractMoneyDatasOK(boolean  booleanSource,  String  stringFactoryNo,  Vector  vectorDoc2M010,  Vector  vectorDoc2M017,  FargloryUtil  exeUtil,  talk  dbDoc,  Vector  vectorPurchaseNos) throws  Throwable {
        String          stringBarCode                 =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "BarCode") ;
        String          stringComNo                 =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "ComNo") ;
        String          stringKindNo                  =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "KindNo") ;
        String          stringEDateTime               =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "EDateTime") ;
        String          stringKindNoFront                   =  "24".equals(stringKindNo) ? "17" : "15" ;
        String          stringRetainBarCode             =  exeUtil.getVectorFieldValue(vectorDoc2M010,  0,  "RetainBarCode") ;
        String      stringPurchaseNo            =  "" ;
        String      stringPurchaseNo1         =  "" ;
        String      stringPurchaseNo2         =  "" ;
        String      stringPurchaseNo3         =  "" ;
        String      stringProjectID1            =  "" ;
        Vector    vectorAllPurchaseNos        =  new  Vector() ;
        //
        if(!booleanSource) {
              Vector  vectorPurchaseNosL  =  new  Vector() ;
              if(!"".equals(stringRetainBarCode)) {
                  String[][]  retDoc5M011  =  getDoc3M011_2(booleanSource?"Doc3M011":"Doc5M011",  "",  "",  stringRetainBarCode,  stringKindNoFront,  "",  dbDoc)  ;
                  vectorPurchaseNosL.add(retDoc5M011[0][1].trim()) ;
              } else {
                  stringPurchaseNo1           =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo1") ;
                  stringPurchaseNo2           =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo2") ;
                  stringPurchaseNo3           =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "PurchaseNo3") ;
                  stringProjectID1            =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "ProjectID1") ;
                  stringFactoryNo             =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "FactoryNo") ;
                  vectorPurchaseNosL.add(stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3) ;
              }
                      System.out.println("getAllPurchaseNo--("+vectorAllPurchaseNos.size()+")--------------------------------------------------------------------") ;
                      vectorAllPurchaseNos       =  getAllPurchaseNo(stringComNo,  stringFactoryNo,  vectorPurchaseNosL,  "",  "",  stringKindNo,  booleanSource,  dbDoc) ;
              Vector      vectorDoc2M017L             =  new  Vector() ;
              Hashtable   hashtableTemp       =  null ;
              for(int  intNo=0  ;  intNo<vectorAllPurchaseNos.size()  ;  intNo++) {
                  stringPurchaseNo  =  ""+vectorAllPurchaseNos.get(intNo) ;
                  hashtableTemp  =  new  Hashtable(); 
                  hashtableTemp.put("PurchaseNo1",    stringPurchaseNo) ;
                  hashtableTemp.put("FactoryNo",      stringFactoryNo) ;
                  //
                  System.out.println("stringPurchaseNo("+stringPurchaseNo+")-----------------------------------------") ;
                  vectorDoc2M017L.add(hashtableTemp) ;
              }
              vectorDoc2M017  =  vectorDoc2M017L ;
        } 
        String      stringBarCodePur      =  "" ;
        String      stringGroupID       =  "" ;
        String      stringFactoryNoL        =  "" ;
        String      stringGroupIDL          =  "" ;
        String      stringPurchaseMoneyL    =  "" ;
        String      stringPurchaseMoney   =  "" ;
        String[][]    retDoc3M011         =  null ;
        boolean   booleanSpecPurchaseNo   =  true ;
        Hashtable   hashtableAnd          =  new  Hashtable() ;
        double    doubleTemp2       =  0 ;
        double    doublePurchaseMoney   =  0 ;
        double    doubleExistRealMoney    =  0 ;
        Vector    vectorDoc3M012      =  null ;
        for(int  intNo=0  ;  intNo<vectorDoc2M017.size()  ;  intNo++) {
            stringPurchaseNo1     =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo1") ;
            stringPurchaseNo2     =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo2") ;
            stringPurchaseNo3     =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo3") ;
            stringProjectID1         = exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "ProjectID1") ;
            stringFactoryNo          =  exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "FactoryNo") ;
            stringPurchaseNo       =  stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3 ;
            //
            if(vectorPurchaseNos.indexOf(stringPurchaseNo)  ==  -1)  vectorPurchaseNos.add(stringPurchaseNo) ;
            // 0  ComNo               1 DocNo                             2  CDate            3 NeedDate        4 ApplyType
            // 5  Analysis                 6 DepartNo                       7  EDateTime        8 CDate           9  PrintCount
            // 10 CheckAdd      11 CheckAddDescript     12  BarCode   13  ID
            System.out.println("getDoc3M011_2("+stringPurchaseNo+")-------------------------------") ;
            retDoc3M011  =  getDoc3M011_2( booleanSource?"Doc3M011":"Doc5M011",  stringComNo,  stringPurchaseNo,  "",  stringKindNoFront,  "",  dbDoc) ;
            if(retDoc3M011.length  ==  0) {
                messagebox("找不到對應的請購資料，請重新列印。") ;
                return  false  ;
            }
            stringBarCodePur          =  retDoc3M011[0][12].trim() ;
            System.out.println("stringBarCodePur("+stringBarCodePur+")-------------------------------") ;
            booleanSpecPurchaseNo       =  isSpectPurchaseNo (stringComNo,  stringKindNoFront,  stringPurchaseNo,  " AND  M13.GroupName  LIKE  '%#-#B' ",  booleanSource,  dbDoc) ;
            stringGroupID                      =  getGroupID("Doc2M0171",  stringBarCode,  stringBarCodePur,  stringPurchaseNo,  booleanSource,  exeUtil,  dbDoc) ;  
             // 合約金額
                hashtableAnd.put("FactoryNo",  stringFactoryNo) ;
                hashtableAnd.put("BarCode",     stringBarCodePur) ;
                vectorDoc3M012  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc3M012":"Doc5M012",  hashtableAnd,  "",  dbDoc);
            for(int  intDoc3M012=0  ;  intDoc3M012<vectorDoc3M012.size()  ;  intDoc3M012++) {
                stringFactoryNoL            =  exeUtil.getVectorFieldValue(vectorDoc3M012,  intDoc3M012,  "FactoryNo") ;
                stringGroupIDL              =  exeUtil.getVectorFieldValue(vectorDoc3M012,  intDoc3M012,  "GroupID") ;
                stringPurchaseMoneyL    =  exeUtil.getVectorFieldValue(vectorDoc3M012,  intDoc3M012,  "PurchaseMoney") ;
                //
                if(!stringFactoryNoL.equals(stringFactoryNo))  continue ;
                //
                if(booleanSpecPurchaseNo) {
                    if(!stringGroupID.equals(stringGroupIDL))  continue ;
                }
                doublePurchaseMoney  +=  exeUtil.doParseDouble(stringPurchaseMoneyL) ;
            }
            // 
            doubleTemp2                 =  getExistRealMoney(stringBarCode,     stringComNo,        stringEDateTime,  
                                              stringPurchaseNo,     stringFactoryNo,     stringKindNo,
                                          stringBarCodePur,    stringGroupID,       booleanSpecPurchaseNo,    
                                          booleanSource,        exeUtil,                    dbDoc) ;
            doubleExistRealMoney  +=  doubleTemp2 ;
        }
        stringPurchaseMoney  =  convert.FourToFive(""+doublePurchaseMoney,  0) ;
        // 合約金額
        doSetValue("Percent1",        "100%") ;
        doSetValue("ContractMoney",  format.format(stringPurchaseMoney,  "999,999,999,999").trim( )) ;
        // 已付金額、前期估驗
        String    stringExistRealMoney  =  convert.FourToFive(""+doubleExistRealMoney,  0) ;
        double  doubleRatio        =  doubleExistRealMoney / exeUtil.doParseDouble(stringPurchaseMoney) * 100 ;
        doSetValue("Percent2",       convert.FourToFive(""+doubleRatio,  0)+"%") ;
        doSetValue("PaidUpMoney",  format.format(stringExistRealMoney,  "999,999,999,999").trim( )) ;
        // 未付金額 stringNoPay
        String  stringNoPay  =  operation.floatSubtract(stringPurchaseMoney,  stringExistRealMoney,  0) ;
                   doubleRatio   =  exeUtil.doParseDouble(stringNoPay) / exeUtil.doParseDouble(stringPurchaseMoney) * 100 ;
        doSetValue("Percent3",      convert.FourToFive(""+doubleRatio,  0)+"%") ;
        doSetValue("NoPayMoney",  format.format(stringNoPay,  "999,999,999,999").trim( )) ;
        return  true ;
    }






    public  Vector  doPrintPurchaseExist(boolean  booleanSource,  String     stringRetainType,  String  stringComNo,          String   stringBarCode,       String      stringEDateTime,       String     stringFactoryNo,    String        stringSqlAndPurchaseNo,
                                                          String     stringKindNo,         String  stringKindNoFront,  Vector  vectorDoc2M012,       Vector   vectorDoc2M017,     Vector    vectorDoc3M012,   Hashtable   hashtableDoc3M012,  FargloryUtil  exeUtil,                 talk          dbDoc)throws  Throwable {
        Object        objectTemp                     =  null ;
        int               intCount                          =  1 ;
        //int               intSize                             =  30 ;
        String         stringItemName               =  "" ;
        String         stringKey                         =  "" ;
        String         stringAmtPre                   =  "" ;  // 前期估驗金額
        String         stringNumPre                   =  "" ; // 前期估驗數量
        String         stringAmt                         =  "" ; // 本期估驗金額
        String         stringNum                        =  "" ; // 本期估驗數量
        String         stringAmtSum                  =  "" ; // 累計估驗金額
        String         stringNumSum                 =  "" ;// 累計估驗數量
        String         stringFieldName               =  "" ;
        String         stringTemp                      =  "" ;
        String         stringFieldName2            =  "" ;
        String         stringFieldName11          =  "" ;// 前期估驗金額
        String         stringFieldName12          =  "" ;// 前期估驗數量
        String         stringFieldName21          =  "" ;// 本期估驗金額
        String         stringFieldName22          =  "" ;// 本期估驗數量
        String         stringFieldName31          =  "" ; // 累計估驗金額
        String         stringFieldName32          =  "" ;// 累計估驗數量
        String          stringControlType           =  ""+get("ONLY_CONTROL_AMT") ;
        String[]       arrayDoc3M012              =  null ;
        String[]       arrayDoc5M0272            =  null ;
        String[]       arrayTemp                      =  null ;
        double[]     arrayAmt                         =  {0,0,0,0,0,0} ;
        double    doubleContractMoney   =  exeUtil.doParseDouble(getValue("ContractMoney").replaceAll(",", "").trim()) ;
        double    doubleRatio         = 0 ;
        Vector        vectorKey                        =  new  Vector() ;       // 部門(案別) + 請款代碼
        Hashtable  hashtableDoc5M0272    =  new  Hashtable() ;          
        Hashtable  hashtableMoney             =  new  Hashtable() ;
        Hashtable  hashtableRealMoney      =  new  Hashtable() ;// 已使用金額
        Hashtable  hashtableRealMoneyAll  =   new  Hashtable() ;
        boolean     booleanFlag                    =  true ;
        // 
        String[][]  retDoc5M0272    =  getDoc5M0272(booleanSource,  "",  "",  stringKindNo,  " AND  BarCode =  '"+stringBarCode+"' ",  dbDoc) ;
        String[][]  retDoc5M0272L  =  getITEMCountDoc5M0272(booleanSource,  stringEDateTime,  stringComNo,  stringKindNo,  stringFactoryNo,  " AND ("+stringSqlAndPurchaseNo+") ",  dbDoc) ;
        if(retDoc5M0272L.length>4  ||  "A".equals(stringRetainType)) {
            setValue("DescriptPrint2_1",  getValue("DescriptPrint2_1").trim()+"(詳附件)") ;  
            booleanFlag  =  false ;
            System.out.println("doPrintPurchaseExist-------------------------------詳附件"); 
        }
        System.out.println("vectorDoc3M012("+vectorDoc3M012.size()+")booleanFlag("+booleanFlag+")-------------------------------"); 
        if(vectorDoc3M012.size()<=4  &&  booleanFlag) {
            // 依請購項目作列印
            setValue("DescriptPrint2_1",       "")  ;
            setValue("DescriptPrint2_2",       "")  ;
            setValue("DescriptPrint2_3",       "")  ;
            setValue("DescriptPrint2_4",       "")  ;
            //
            System.out.println("doDoc5M0272------------------------------Ｓ"); 
            doDoc5M0272(booleanSource,  stringBarCode,  stringEDateTime,  stringComNo,  stringKindNo,  stringKindNoFront,  stringFactoryNo,  stringSqlAndPurchaseNo,  hashtableDoc5M0272,  exeUtil,  dbDoc) ;
            System.out.println("doDoc5M0272------------------------------E"); 
            for(int  intNo=0  ;  intNo<vectorDoc3M012.size()  ;  intNo++) {
                stringKey                  =  ""+vectorDoc3M012.get(intNo) ;
                System.out.println(intNo+"---"+stringKey); 
                arrayDoc3M012       =  (String[])  hashtableDoc3M012.get(stringKey) ;
                objectTemp              =  hashtableDoc5M0272.get(stringKey) ; if(objectTemp  ==  null)  continue ;
                arrayDoc5M0272      =  (String[])  objectTemp ;
                stringNumPre            =  arrayDoc5M0272[0].trim() ;
                stringAmtPre             =  arrayDoc5M0272[1].trim() ;
                stringNum                  =  arrayDoc5M0272[2].trim() ;
                stringAmt                   =  arrayDoc5M0272[3].trim() ;               
                //System.out.println(intNo+"stringKey("+stringKey+")stringNumPre("+stringNumPre+")stringAmtPre("+stringAmtPre+")stringNum("+stringNum+")stringAmt("+stringAmt+")----------------------------------"); 
                stringNumSum           =  ""+(exeUtil.doParseDouble(stringNumPre)  +  exeUtil.doParseDouble(stringNum)) ;
                stringAmtSum            =  ""+(exeUtil.doParseDouble(stringAmtPre)  +  exeUtil.doParseDouble(stringAmt)) ;//if("B3018".equals(getUser()))  messagebox("stringAmtPre("+stringAmtPre+")stringAmt("+stringAmt+")stringAmtSum("+stringAmtSum+")11111111") ;
                if(exeUtil.doParseDouble(stringAmtSum)==0)  continue ;
                // 項目(請款名稱+內容) 
                System.out.println(intNo+"項目(請款名稱+內容) ----------------------------------S"); 
                stringItemName    =  getCost4Name("",  arrayDoc3M012[0],  arrayDoc3M012[1],  arrayDoc3M012[18],  dbDoc) ;
                System.out.println(intNo+"項目(請款名稱+內容) ----------------------------------E"); 
                if("".equals(arrayDoc3M012[18])) {
                    if(stringItemName.indexOf(arrayDoc3M012[14].trim())  ==  -1) {
                        stringItemName    =  getCost4Name("",  arrayDoc3M012[0],  arrayDoc3M012[1],  arrayDoc3M012[18],  dbDoc)+"-"+arrayDoc3M012[14] ;
                    }
                }
                //
                arrayAmt[0]  +=  exeUtil.doParseDouble(stringNumPre) ;
                arrayAmt[1]  +=  exeUtil.doParseDouble(stringAmtPre) ;
                arrayAmt[2]  +=  exeUtil.doParseDouble(stringNum) ;
                arrayAmt[3]  +=  exeUtil.doParseDouble(stringAmt) ;
                arrayAmt[4]  +=  exeUtil.doParseDouble(stringNumSum) ;
                arrayAmt[5]  +=  exeUtil.doParseDouble(stringAmtSum) ;
                //
                if((","+stringControlType+",").indexOf(","+arrayDoc3M012[4]+",")!=-1) {
                    stringNumPre    =  "1" ;
                    stringNum         =  "1" ;
                    stringNumSum  =  "1" ;
                    arrayAmt[0]       =  1 ;
                    arrayAmt[2]       =  1 ;
                    arrayAmt[4]       =  1 ;
                }
                doSetValue("No"+intCount,           ""+intCount)  ;                               // No
                doSetValue("No2"+intCount,            ""+intCount)  ;                               // No
                if(vectorDoc3M012.size()  ==  1) {
                    doPutValues(4,  30,  0,  stringItemName,  "DescriptPrint2_",  exeUtil)  ;                 // 項目 
                } else {
                    doPutValues(0,  30,  0,  stringItemName,  "DescriptPrint2_"+intCount,  exeUtil) ;           // 項目 
                }
                doSetValue("SIZE"+intCount,             exeUtil.getFormatNum2(arrayDoc3M012[6]))  ;           // 預算數量  
                doSetValue("UNIT"+intCount,           arrayDoc3M012[4])  ;                              // 單位  
                doSetValue("PRICE"+intCount,          exeUtil.getFormatNum2(arrayDoc3M012[9]))  ;            // 單價   
                // 前期 
                stringNumPre    =  convert.FourToFive(stringNumPre,  2) ;   if(exeUtil.doParseDouble(stringAmtPre)  ==  0)  stringNumPre  =  "0" ;
                doubleRatio   =  exeUtil.doParseDouble(stringAmtPre)  /  doubleContractMoney  *  100 ;
                doSetValue("RealMoneySumPreNum_"    +intCount,  exeUtil.getFormatNum2(stringNumPre))  ;           
                doSetValue("RealMoneySumPrePrint_"    +intCount,  exeUtil.getFormatNum2(stringAmtPre))  ;   
                doSetValue("RealMoneySumPrePercent_"  +intCount,  convert.FourToFive(""+doubleRatio,  0))  ;  
                // 本期 
                stringNum  =  convert.FourToFive(stringNum,  2) ;
                doubleRatio   =  exeUtil.doParseDouble(stringAmt)  /  doubleContractMoney  *  100 ;
                doSetValue("RealMoneySumNum_"     +intCount,  exeUtil.getFormatNum2(stringNum))  ;              
                doSetValue("RealMoneySumPrint_"     +intCount,  exeUtil.getFormatNum2(stringAmt))  ;  
                doSetValue("RealMoneySumPercent_" +intCount,  convert.FourToFive(""+doubleRatio,  0))  ;  
                // 累計
                stringNumSum  =  convert.FourToFive(stringNumSum,  2) ;   if(exeUtil.doParseDouble(stringNumSum)  ==  0)  stringNumSum  =  "0" ;
                doubleRatio   =  exeUtil.doParseDouble(stringAmtSum)  /  doubleContractMoney  *  100 ;
                if("B3018".equals(getUser()))  messagebox("stringAmtPre("+stringAmtPre+")stringAmt("+stringAmt+")stringAmtSum("+stringAmtSum+")22222") ;
                doSetValue("RealMoneySumAddUpNum_"  +intCount,  exeUtil.getFormatNum2(stringNumSum))  ;           
                doSetValue("RealMoneySumAddUpPrint_"  +intCount,  exeUtil.getFormatNum2(stringAmtSum))  ;   
                doSetValue("RealMoneySumAddUpPercent_"+intCount,  convert.FourToFive(""+doubleRatio,  0))  ;  
                //
                intCount++ ;
            }
            // 估驗部份小計
            stringTemp  =  convert.FourToFive(""+arrayAmt[0],  2) ;
            doubleRatio  =  arrayAmt[1]  /  doubleContractMoney  *  100 ;
            if(arrayAmt[1]  ==  0)  stringTemp  =  "0" ;
            //doSetValue("RealMoneySumPreNum2",           exeUtil.getFormatNum2(stringTemp))  ;   
            doSetValue("RealMoneySumPrePrint2",           exeUtil.getFormatNum2(""+arrayAmt[1]))  ;
            doSetValue("RealMoneySumPrePercent2",           convert.FourToFive(""+doubleRatio,  0))  ; 
            stringTemp  =  convert.FourToFive(""+arrayAmt[2],  2) ;
            doubleRatio  =  arrayAmt[3]  /  doubleContractMoney  *  100 ;
            doSetValue("RealMoneySumNum2",                exeUtil.getFormatNum2(stringTemp))  ;   
            doSetValue("RealMoneySumPrint2",                    exeUtil.getFormatNum2(""+arrayAmt[3]))  ;   
            doSetValue("RealMoneySumPercent2",          convert.FourToFive(""+doubleRatio,  0))  ; 
            stringTemp  =  convert.FourToFive(""+arrayAmt[4],  2) ;
            doubleRatio  =  arrayAmt[5]  /  doubleContractMoney  *  100 ;
            doSetValue("RealMoneySumAddUpNum2",     exeUtil.getFormatNum2(stringTemp))  ;   
            doSetValue("RealMoneySumAddUpPrint2",         exeUtil.getFormatNum2(""+arrayAmt[5]))  ;   
            doSetValue("RealMoneySumAddUpPercent2",   convert.FourToFive(""+doubleRatio,  0))  ; 
            vectorKey.add("OK") ;
        }  else {
            System.out.println("doDoc5M0272---doPrintPurchaseExist-------------------------------超過 4 列"); 
        }
        
        if(booleanSource)  return  vectorKey ;
        // 保留金額 
        String    stringPurchaseNo                   =  "" ;
        String    stringRetainMoneyPre           =  "" ;
        String    stringRetainMoneyAddup       =  "" ;
        String    stringReceiptTaxPre              =  "" ;
        String    stringReceiptTaxAddup          =  "" ;
        String    stringActualMoneyPre            =  "" ;
        String    stringActualMoneyAddup        =  "" ;
        String    stringSqlAnd                          =  "" ;
        String    stringSqlAnd2                         =  "" ;
        double  doubleBackRetainMoney        =  0 ;
        double  doubleThisRetainMoneyPrint  =  exeUtil.doParseDouble(getValue("RetainMoneyPrint").trim().replaceAll(",","")) ;
                     stringFactoryNo                     =  exeUtil.getVectorFieldValue(vectorDoc2M017,  0,  "FactoryNo") ;
        for(int  intNo=0  ;  intNo<vectorDoc2M017.size()  ;  intNo++) {
            stringPurchaseNo  =   exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo1")+
                          exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "ProjectID1")+
                          exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo2")+
                          exeUtil.getVectorFieldValue(vectorDoc2M017,  intNo,  "PurchaseNo3") ;
            //
            if(!"".equals(stringSqlAnd))  stringSqlAnd  +=  ", " ;
            stringSqlAnd  +=  " '"+stringPurchaseNo+"' " ;
        }
        stringSqlAnd2                   =   " AND  M20.EDateTime  <=  '" +  stringEDateTime  +  "' "  +
                              " AND  M220.BarCode  <>  '"  +  stringBarCode      +  "' "  +
                                                     " AND  M220.BarCodeRef  IN  (SELECT  BarCode "  +
                                                                           " FROM  Doc5M011 "  +
                                              " WHERE  DocNo  IN  ("+stringSqlAnd+") " +
                                                   " AND  KindNo=  '"+stringKindNoFront+"' )" ;
        doubleBackRetainMoney   =  getBackRetainMoneyDoc5M0220("",  stringFactoryNo,  "",  stringSqlAnd2,  exeUtil,  dbDoc) ;
        stringRetainMoneyPre       =  ""+(exeUtil.doParseDouble(getRetainMoneyDoc5M020(stringBarCode,  stringComNo,  stringFactoryNo,  stringEDateTime,  stringKindNo,  stringSqlAnd,  dbDoc)) -  // 前期保留
                                                            doubleBackRetainMoney) ; // 前期退保留沖銷 Doc5M0220 
        
        if("A".equals(stringRetainType)) {
            // 本期退保留沖銷 Doc5M0220 
        }
        stringRetainMoneyAddup  =  ""+(exeUtil.doParseDouble(stringRetainMoneyPre)  +  doubleThisRetainMoneyPrint) ;
        // 未付金額、已付金額修正 修正 
        String  stringNoPayMoney  =  ""+(exeUtil.doParseDouble(getValue("NoPayMoney").trim().replaceAll(",","")) + 
                                                            exeUtil.doParseDouble(stringRetainMoneyPre)) ;
        String  stringPaidUpMoney  =  ""+(exeUtil.doParseDouble(getValue("PaidUpMoney").trim().replaceAll(",","")) - 
                                                            exeUtil.doParseDouble(stringRetainMoneyPre)) ;
        // 2015-07-23 盧瑜瑋 取消 未付金額及已付金額扣除保留金機制
        //setValue("NoPayMoney",        exeUtil.getFormatNum2(stringNoPayMoney)) ;                         // 未付金額
        //setValue("PaidUpMoney",       exeUtil.getFormatNum2(stringPaidUpMoney)) ;                         // 已付金額
        //
        doSetValue("RetainMoneyPrePrint",        exeUtil.getFormatNum2(stringRetainMoneyPre)) ;       // 前期保留
        doSetValue("RetainMoneyAddUpPrint",  exeUtil.getFormatNum2(stringRetainMoneyAddup)) ;  // 累計保留
        // 扣繳金額
        stringReceiptTaxPre      =  getReceiptTaxDoc5M023(stringBarCode,  stringComNo,  stringFactoryNo,  stringEDateTime,  stringKindNo,  stringSqlAnd,  dbDoc) ;
        stringReceiptTaxAddup  =  ""+(exeUtil.doParseDouble(stringReceiptTaxPre)  +  exeUtil.doParseDouble(getValue("CheapenMoneyPrint").trim().replaceAll(",",""))) ;
        doSetValue("CheapenMoneyPrePrint",       exeUtil.getFormatNum2(stringReceiptTaxPre)) ;            // 前期扣繳
        doSetValue("CheapenMoneyAddUpPrint",  exeUtil.getFormatNum2(stringReceiptTaxAddup)) ;       // 累計扣繳
        // 實付金額
        stringActualMoneyPre       =  ""+(exeUtil.doParseDouble(getValue("RealMoneySumPrePrint2").trim().replaceAll(",",""))      -  exeUtil.doParseDouble(stringReceiptTaxPre)       -  exeUtil.doParseDouble(stringRetainMoneyPre)) ;
        stringActualMoneyAddup  =  ""+(exeUtil.doParseDouble(getValue("RealMoneySumAddUpPrint2").trim().replaceAll(",",""))  -  exeUtil.doParseDouble(stringReceiptTaxAddup)  -  exeUtil.doParseDouble(stringRetainMoneyAddup)) ;
        doSetValue("ActualMoneyPrePrint",       exeUtil.getFormatNum2(stringActualMoneyPre)) ;            // 前期實付
        doSetValue("ActualMoneyAddUpPrint",  exeUtil.getFormatNum2(stringActualMoneyAddup)) ;       // 累計實付
        return  vectorKey ;
    }
    // 借款沖銷
    public  boolean  doPrint2(String  stringBarCode,  String  stringPrevFunction,  boolean  booleanSource,  boolean  booleanToNextFlow,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFE3D,  talk  dbFED1) throws  Throwable {
        String      stringCostID                       =  "" ;
        String      stringBarCode11               =  "" ;
        String[][]  retBarCode11                   =  getBarCodeForDoc6M010(booleanSource?"Doc6M010":"Doc5M030",  stringBarCode,  dbDoc) ;
        String[][]  retDoc6M0131                   =  new  String[0][0] ;   
        //
        if(retBarCode11.length  ==  0) {
            messagebox("查無借款相關資料。") ;
            return false;
        }
        stringBarCode11  =  retBarCode11[0][0].trim() ;

        Vector  vectorDoc6M010  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc6M010":"Doc5M030",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        if(vectorDoc6M010.size()  ==  0) {
            messagebox("查無資料。1") ;
            return false;
        }
        Vector  vectorDoc6M012  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc6M012":"Doc5M032",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        if(vectorDoc6M012.size()  ==  0) {
            messagebox("查無 [費用] 資料。") ;
            return false ;
        }
        Vector  vectorDoc6M011  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc6M011":"Doc5M031",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        Vector  vectorDoc6M013  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc6M013":"Doc5M033",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        Vector  vectorDoc6M0101  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc6M0101":"Doc5M0301",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  dbDoc) ;
        if(vectorDoc6M0101.size()  ==  0) {
            messagebox("[借款單編號] 為空白，請重新列印。") ;
            return  false ;
        }
        return  setDataDoc6M010(stringPrevFunction,  vectorDoc6M010,   vectorDoc6M011,   vectorDoc6M012,  vectorDoc6M013,  vectorDoc6M0101,  retBarCode11,  booleanSource,  booleanToNextFlow,  exeUtil,  dbDoc,  dbFE3D,  dbFED1) ;
    }

    public  boolean  setDataDoc6M010(String   stringPrevFunction,   Vector      vectorDoc6M010,       Vector          vectorDoc6M011,   Vector     vectorDoc6M012,    Vector    vectorDoc6M013,    Vector        vectorDoc6M0101,  String[][]  retBarCode11,
                                                         boolean    booleanSource,      boolean   booleanToNextFlow,    FargloryUtil    exeUtil,          talk      dbDoc,          talk      dbFE3D,         talk        dbFED1) throws  Throwable {
        //String    stringBarCode         =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "BarCode") ;
        // 工程編號
        if(vectorDoc6M012.size()  >  0) {
            String  stringCostID    =  exeUtil.getVectorFieldValue(vectorDoc6M012,  0,  "CostID") ;
            String  stringCostID1   =  exeUtil.getVectorFieldValue(vectorDoc6M012,  0,  "CostID1") ;
            doSetValue("ProjectNo",  stringCostID+stringCostID1) ;
        }
        if(!isPutDoc2M010DataOK("B",  booleanSource,  booleanToNextFlow,  stringPrevFunction,  vectorDoc6M010,  exeUtil,  dbDoc,  dbFE3D,  dbFED1) ) return  false ;
        // 公文編號等...
        if(!isPutDocNo6OK(booleanSource,  vectorDoc6M010,  vectorDoc6M0101,  exeUtil,  dbDoc) )                       return  false ;
        // 廠商
        String  stringFactoryNo  =  doPutFactoryNo6(booleanSource,  vectorDoc6M010,  exeUtil,  dbDoc,  dbFED1) ;
        if("".equals(stringFactoryNo))              return  false ;
        // 工程名稱(案別)：
        if(!isPutProjectOK(vectorDoc6M010,  vectorDoc6M012,  new  Vector(),   exeUtil))  return  false ;
        // 合約金額等...
        if(!isPutContractMoneys6DatasOK(booleanSource,  stringFactoryNo,  vectorDoc6M010,  exeUtil,  dbDoc))  return  false ;
        // 前期.本期及累計
        if(!isItemsDataOK(booleanSource,  retBarCode11,  vectorDoc6M010,   vectorDoc6M012,  vectorDoc6M013,  exeUtil,  dbDoc))  return  false ;
        //System.out.println("-------------------------------設定資料END"); 
        return  true ;
    }
    public  boolean  isPutDocNo6OK(boolean  booleanSource,  Vector  vectorDoc6M010,  Vector  vectorDoc6M0101,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String    stringPurchaseNoExist         =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNoExist") ;
        if(vectorDoc6M0101.size()  >  2) {
            messagebox("[借款單編號] 數量大於 2，不作列印。") ;
            return  false ;
        }
        // 借款單
        String    stringBorrowNo1       =  exeUtil.getVectorFieldValue(vectorDoc6M0101,  0,  "BorrowNo1") ;
        String    stringBorrowNo2       =  exeUtil.getVectorFieldValue(vectorDoc6M0101,  0,  "BorrowNo2") ;
        String    stringBorrowNo3       =  exeUtil.getVectorFieldValue(vectorDoc6M0101,  0,  "BorrowNo3") ;
        String    stringBorrowNo          =  stringBorrowNo1  +  "-"+stringBorrowNo2  +  "-"+ stringBorrowNo3 ;
        doSetValue("BorrowNo1",  stringBorrowNo) ;
        stringBorrowNo1       =  exeUtil.getVectorFieldValue(vectorDoc6M0101,  1,  "BorrowNo1") ;
        stringBorrowNo2       =  exeUtil.getVectorFieldValue(vectorDoc6M0101,  1,  "BorrowNo2") ;
        stringBorrowNo3       =  exeUtil.getVectorFieldValue(vectorDoc6M0101,  1,  "BorrowNo3") ;
        stringBorrowNo          =  stringBorrowNo1  +  "-"+stringBorrowNo2  +  "-"+ stringBorrowNo3 ;
        if(!"--".equals(stringBorrowNo))doSetValue("BorrowNo2",  stringBorrowNo) ;
        // 請購單
        if(stringPurchaseNoExist.startsWith("Y")) {
            String  stringPurchaseNo1  =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNo1") ;
            String  stringPurchaseNo2  =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNo2") ;
            String  stringPurchaseNo3  =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNo3") ;
            String  stringPurchaseNo    =  stringPurchaseNo1 +  stringPurchaseNo2  +  stringPurchaseNo3 ;
            if("".equals(stringPurchaseNo)) {
                messagebox("[請購單編號] 為空白，請重新列印。") ;
                return  false ;
            }
            doSetValue("PurchaseNo1_1",  stringPurchaseNo) ;
            //20180212 移除驗收單判斷
            /*
            // 驗收單編號
            String  stringOptometryNo  =    exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "OptometryNo1") +
                                exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "OptometryNo2") +
                                exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "OptometryNo3");
            if("".equals(stringOptometryNo)) {
                messagebox("[驗收單編號] 為空白，請重新列印。") ;
                return  false ;
            }
            doSetValue("OptometryNo1_1",  stringOptometryNo) ;
            */
            // 合約處理
//            doSetContractNo(stringComNo,  "24",  stringPurchaseNo,  stringFactoryNo,  exeUtil,  dbDoc) ;
        }
        return  true ;
    }
    public  void  doSetContractNo(String  stringComNo,  String  stringKindNo,  String  stringPurchaseNo,  String  stringFactoryNo,  FargloryUtil  exeUtil,  talk  dbDoc)throws  Throwable {
        if(!"B3018".equals(getUser()))  return ;
        //
        talk      dbConstAsk                  =  getTalk(""+get("put_Const_Ask")) ;
        String      stringBarCodePur          =  "" ;
        String      stringContractNo            =  "" ;
        String      stringKindNoFront               =  "24".equals(stringKindNo) ? "17" : "15" ;
        String        stringPrdocode              =  "" ;
        Hashtable   hashtableAnd              =  new  Hashtable() ;
        Hashtable   hashtableRelContractDetail    =  new  Hashtable() ;
        Vector      vectorRelContractDetail       =  null ;
        //
        hashtableAnd.put("ComNo",  stringComNo) ;
        hashtableAnd.put("KindNo",  stringKindNoFront) ;
        hashtableAnd.put("DocNo",   stringPurchaseNo) ;
        stringBarCodePur  =  exeUtil.getNameUnion("BarCode",  "Doc3M011",  "",  hashtableAnd,  dbDoc) ;  if("".equals(stringBarCodePur))  return ;
        //
        stringPrdocode  =  exeUtil.getNameUnion("prdocode",  "prdt",  " AND  social  =  '"+stringFactoryNo+"' ",  new  Hashtable(),  dbConstAsk) ;
        //
        hashtableAnd.put("barcode",     stringBarCodePur) ;
        hashtableAnd.put("prdocode",  stringPrdocode) ;
        vectorRelContractDetail  =  exeUtil.getQueryDataHashtable("rel_contract_detail",  hashtableAnd,  "",  dbConstAsk) ; if(vectorRelContractDetail.size()  ==  0)   return ;
        hashtableRelContractDetail  =  (Hashtable)  vectorRelContractDetail.get(0) ;                                    if(hashtableRelContractDetail  ==  null)    return ;
        //
        String  stringCcaseCode  =  (""+hashtableRelContractDetail.get("ccasecode")).trim() ;   if("null".equals(stringCcaseCode))  stringCcaseCode  =  "" ;
        String  stringContractIID   =  (""+hashtableRelContractDetail.get("contract_iid")).trim() ;  if("null".equals(stringContractIID))  stringContractIID    =  "" ;
        //
        setValue("ContractNo",  stringCcaseCode+"-"+stringContractIID) ;
    }
    public  String  doPutFactoryNo6(boolean  booleanSource,  Vector  vectorDoc6M010,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFED1) throws  Throwable {
        //System.out.println("------------------------廠商名稱：由統一編號查詢廠商名稱") ;
        String        stringFactoryNo  =  getFactoryNo(booleanSource,  vectorDoc6M010,  exeUtil,  dbDoc) ;
        //
        String[][]  retFED1005  =  getFED1005(stringFactoryNo,  "",  dbDoc,  dbFED1) ;
        if(retFED1005.length  ==  0) {
            messagebox(" [廠商名稱] 為空白，請重新列印。") ;
            return  "" ;
        }
        String  stringFactoryName   =  retFED1005[0][0].trim() ;
        String  stringTELPrint      =  retFED1005[0][1].trim() ;
        doPutValues(0,  10,  0,  stringFactoryName,  "FactoryNoPrint",  exeUtil) ;
        if(stringTELPrint.length()<=10  )doSetValue("TELPrint",            stringTELPrint) ;
        return  stringFactoryNo ;
    }
    public  String  getFactoryNo(boolean  booleanSource,  Vector  vectorDoc6M010,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String      stringBarCode     =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "BarCode") ;
        String      stringComNo       =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "ComNo") ;
        String      stringFactoryNo     =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "FactoryNo") ;
        Hashtable   hashtableAnd      =  new  Hashtable() ;
        // 本身
        if(!"".equals(stringFactoryNo))   return  stringFactoryNo ;
        // 本身 發票
        stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  booleanSource?"Doc6M011":"Doc5M031",  " AND  BarCode  =  '"+stringBarCode+"'",  new  Hashtable(),  dbDoc)  ;
        if(!"".equals(stringFactoryNo))   return  stringFactoryNo ;
        // 本身 個人扣繳
        stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  booleanSource?"Doc6M013":"Doc5M033",  " AND  BarCode  =  '"+stringBarCode+"'",  new  Hashtable(),  dbDoc)  ;
        if(!"".equals(stringFactoryNo))   return  stringFactoryNo ;
        // 對應借款編號
        Vector  vectorDoc6M0101  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc6M0101":"Doc5M0301",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"'",  dbDoc) ;
        if(vectorDoc6M0101.size()  ==  0)  return  "" ;
        // 對應借款條碼編號
        hashtableAnd.put("ComNo",  stringComNo) ;
        hashtableAnd.put("KindNo",  "26") ;
        hashtableAnd.put("DocNo1",  exeUtil.getVectorFieldValue(vectorDoc6M0101,  0,  "BorrowNo1")) ;
        hashtableAnd.put("DocNo2",  exeUtil.getVectorFieldValue(vectorDoc6M0101,  0,  "BorrowNo2")) ;
        hashtableAnd.put("DocNo3",  exeUtil.getVectorFieldValue(vectorDoc6M0101,  0,  "BorrowNo3")) ;
        stringBarCode  =  exeUtil.getNameUnion("BarCode",  "Doc6M010",  "",  hashtableAnd,  dbDoc)  ;
        if("".equals(stringBarCode))  return  "" ;
        // 借款本身
        stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  "Doc6M010",  " AND  BarCode  =  '"+stringBarCode+"'",  new  Hashtable(),  dbDoc)  ;
        if(!"".equals(stringFactoryNo))   return  stringFactoryNo ;
        // 借款本身 發票
        stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  booleanSource?"Doc6M011":"Doc5M031",  " AND  BarCode  =  '"+stringBarCode+"'",  new  Hashtable(),  dbDoc)  ;
        if(!"".equals(stringFactoryNo))   return  stringFactoryNo ;
        // 借款本身 個人扣繳
        stringFactoryNo  =  exeUtil.getNameUnion("FactoryNo",  booleanSource?"Doc6M013":"Doc5M033",  " AND  BarCode  =  '"+stringBarCode+"'",  new  Hashtable(),  dbDoc)  ;
        if(!"".equals(stringFactoryNo))   return  stringFactoryNo ;
        //
        return  "" ;
    }
    public  boolean  isPutContractMoneys6DatasOK(boolean  booleanSource,  String  stringFactoryNo,  Vector  vectorDoc6M010,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String      stringBarCode         =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "BarCode") ;  
        String      stringPurchaseNoExist   =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNoExist") ;  if(!stringPurchaseNoExist.startsWith("Y")) return true ;
        String      stringComNo         =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "ComNo") ;  
        String      stringPurchaseNo1     =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNo1") ;  
        String      stringPurchaseNo2     =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNo2") ;  
        String      stringPurchaseNo3     =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNo3") ;  
        String      stringEDateTime       =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "EDateTime") ;  
        String      stringBarCodePur      =  "" ;
        String      stringGroupID       =  "" ;
        String      stringFactoryNoL      =  "" ;
        String      stringGroupIDL        =  "" ;
        String      stringPurchaseMoney   =  "" ;
        String      stringPurchaseMoneyL  =  "" ;
        String      stringExistRealMoney    =  "" ;
        String      stringNoPay         =  "" ;
        Vector      vectorDoc3M011      =  null ;
        Vector      vectorDoc3M012      =  null ;
        Hashtable   hashtableAnd          =  new  Hashtable() ;
        boolean   booleanSpecPurchaseNo =  false ;
        double    doublePurchaseMoney   =  0 ;
        // 合約金額
        hashtableAnd.put("ComNo",  stringComNo) ;
        hashtableAnd.put("KindNo",  "17") ;
        hashtableAnd.put("DocNo1",  stringPurchaseNo1) ;
        hashtableAnd.put("DocNo2",  stringPurchaseNo2) ;
        hashtableAnd.put("DocNo3",  stringPurchaseNo3) ;
        vectorDoc3M011  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc3M011":"Doc5M011",  hashtableAnd,  "",  dbDoc) ;
        if(vectorDoc3M011.size()  ==  0) {
            messagebox("[請購單] 無對應的 [請購資訊]，請洽資訊室。") ;
            return  false ;
        }
        stringBarCodePur        =  exeUtil.getVectorFieldValue(vectorDoc3M011,  0,  "BarCode") ;  
        booleanSpecPurchaseNo     =  isSpectPurchaseNo (stringComNo,  "17",  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  " AND  M13.GroupName  LIKE  '%#-#B' ",  booleanSource,  dbDoc) ;
        stringGroupID                 =  getGroupID("Doc6M0171",  stringBarCode,  stringBarCodePur,  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  booleanSource,  exeUtil,  dbDoc) ;  
        //
        hashtableAnd.put("FactoryNo",  stringFactoryNo) ;
        hashtableAnd.put("BarCode",     stringBarCodePur) ;
        vectorDoc3M012  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc3M012":"Doc5M012",  hashtableAnd,  "",  dbDoc);
        //messagebox("vectorDoc3M012==>barCode="+stringBarCodePur+",FactoryNo="+stringFactoryNo+","+vectorDoc3M012.size());       
        for(int  intDoc3M012=0  ;  intDoc3M012<vectorDoc3M012.size()  ;  intDoc3M012++) {
            stringFactoryNoL            =  exeUtil.getVectorFieldValue(vectorDoc3M012,  intDoc3M012,  "FactoryNo") ;
            stringGroupIDL              =  exeUtil.getVectorFieldValue(vectorDoc3M012,  intDoc3M012,  "GroupID") ;
            stringPurchaseMoneyL    =  exeUtil.getVectorFieldValue(vectorDoc3M012,  intDoc3M012,  "PurchaseMoney") ;
            //
            if(!stringFactoryNoL.equals(stringFactoryNo))  continue ;
            //
            if(booleanSpecPurchaseNo) {
                if(!stringGroupID.equals(stringGroupIDL))  continue ;
            }
            doublePurchaseMoney  +=  exeUtil.doParseDouble(stringPurchaseMoneyL) ;
        }
        stringPurchaseMoney  =  convert.FourToFive(""+doublePurchaseMoney,  0) ; 
        //
        stringExistRealMoney    =  ""+getExistRealMoney( stringBarCode,                                       stringComNo,              stringEDateTime,  
                                            stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,          stringFactoryNo,            "24",
                                        stringBarCodePur,                                                                       stringGroupID,                      booleanSpecPurchaseNo,    
                                        booleanSource,                                    exeUtil,                                  dbDoc) ;
        stringExistRealMoney  =  convert.FourToFive(stringExistRealMoney,  0) ;
        //
        stringNoPay  =  operation.floatSubtract(stringPurchaseMoney,  stringExistRealMoney,  0) ;
        //
        //messagebox("合約金額==>"+stringPurchaseMoney);        
        doSetValue("ContractMoney",  format.format(stringPurchaseMoney,  "999,999,999,999").trim( )) ; 
        doSetValue("PaidUpMoney",     format.format(stringExistRealMoney,  "999,999,999,999").trim( )) ;   
        doSetValue("NoPayMoney",      format.format(stringNoPay,                  "999,999,999,999").trim( )) ; 
        return  true ;
    }
    public  boolean  isItemsDataOK(boolean  booleanSource,  String[][]  retBarCode11,  Vector  vectorDoc6M010,   Vector  vectorDoc6M012,  Vector  vectorDoc6M013,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String    stringRealTotalMoney    =  "" ;
        String    stringRealMoneySum    =  "" ;
        String    stringReceiptTax        =  "" ;
        String    stringSupplementMoney   =  "" ;
        String    stringCheapenMoney    =  "" ;
        String    stringRetainMoney       =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "RetainMoney") ;
        String    stringPurchaseNoExist   =  exeUtil.getVectorFieldValue(vectorDoc6M010,  0,  "PurchaseNoExist") ;
        String    stringPercent       =  "" ;
        double    doubleTemp          =  0 ;
        double  doubleActualMoney   =  0 ;
        double  doubleContractMoney   =  exeUtil.doParseDouble(getValue("ContractMoney").replaceAll(",",  "")) ;
        double  doublePaidUpMoney     =  exeUtil.doParseDouble(getValue("PaidUpMoney").replaceAll(",",  "")) ;
        // 保留金額
        if(exeUtil.doParseDouble(stringRetainMoney)  ==  0) stringRetainMoney  =  "0" ;
        doSetValue("RetainMoneyPrint",  stringRetainMoney) ; 
        // 前期-已付金額
        if(stringPurchaseNoExist.startsWith("Y")) {
            stringPercent  =  convert.FourToFive(""+(doublePaidUpMoney / doubleContractMoney*100),  0);
            doSetValue("RealMoneySumPrePercent_1",      stringPercent) ; 
            doSetValue("RealMoneySumPrePrint_1",           format.format(convert.FourToFive(""+doublePaidUpMoney,  0),  "999,999,999,999").trim( )) ; 
            //  前期-估驗部份小計
            doSetValue("RealMoneySumPrePercent2",    stringPercent) ; 
            doSetValue("RealMoneySumPrePrint2",         format.format(convert.FourToFive(""+doublePaidUpMoney,  0),  "999,999,999,999").trim( )) ; 
        }
        // 本期-費用合計
        for(int  intNo=0  ;  intNo<vectorDoc6M012.size()  ;   intNo++) {
            stringRealTotalMoney  =  exeUtil.getVectorFieldValue(vectorDoc6M012,  intNo,  "RealTotalMoney") ;
            //
            doubleTemp        +=  exeUtil.doParseDouble(stringRealTotalMoney) ;
        }
        stringRealMoneySum  =  convert.FourToFive(""+doubleTemp,  0) ;
        if(doubleTemp  <=  0) {
            messagebox(" [費用合計] 小於等於 0，請重新列印。") ;
            return  false ;
        }
        doSetValue("RealMoneySumPrint_1",         format.format(convert.FourToFive(stringRealMoneySum,0), "999,999,999,999").trim( )) ; 
        doSetValue("RealMoneySumPrint2",          format.format(convert.FourToFive(stringRealMoneySum,0), "999,999,999,999").trim( )) ; 
        // 本期-數量
        stringPercent  =  "" ;
        if(stringPurchaseNoExist.startsWith("Y")) {
            stringPercent  =  convert.FourToFive(""+(doubleTemp / doubleContractMoney*100),  0) ;
        }
        if(!"".equals(stringPercent)) {
            doSetValue("RealMoneySumPercent_1",  stringPercent) ; 
            doSetValue("RealMoneySumPercent2",    stringPercent) ; 
        }
        // 累計
        if(stringPurchaseNoExist.startsWith("Y")) {
            String  stringRealMoneySumAddUpPrint  =  ""+(doublePaidUpMoney  +  doubleTemp) ;
            stringPercent  =  convert.FourToFive(""+(exeUtil.doParseDouble(stringRealMoneySumAddUpPrint) / doubleContractMoney*100),  0) ;
            //
            doSetValue("RealMoneySumAddUpPercent_1",       stringPercent) ; 
            doSetValue("RealMoneySumAddUpPrint_1",             format.format(convert.FourToFive(""+stringRealMoneySumAddUpPrint,  0),  "999,999,999,999").trim( )) ; 
            //  累計-估驗部份小計
            doSetValue("RealMoneySumAddUpPercent2",     stringPercent) ; 
            doSetValue("RealMoneySumAddUpPrint2",          format.format(convert.FourToFive(""+stringRealMoneySumAddUpPrint,  0),  "999,999,999,999").trim( )) ; 
        }
        // 扣款金額
        doubleTemp  =  0 ;
        for(int  intNo=0  ;  intNo<vectorDoc6M013.size()  ;   intNo++) {
            stringReceiptTax        =  exeUtil.getVectorFieldValue(vectorDoc6M013,  intNo,  "ReceiptTax") ;
            stringSupplementMoney  =  exeUtil.getVectorFieldValue(vectorDoc6M013,  intNo,  "SupplementMoney") ;
            //
            doubleTemp  +=  exeUtil.doParseDouble(stringReceiptTax) +exeUtil.doParseDouble(stringSupplementMoney);
        }
        if(doubleTemp  ==  0) {
            String        stringBarCodeL    =  "" ;
            Vector    vectorDoc6M013L   =  null ;
            for(int  intNo=0  ;  intNo<retBarCode11.length  ;  intNo++) {
                stringBarCodeL  =  retBarCode11[intNo][0].trim() ;
                //
                vectorDoc6M013L  =  exeUtil.getQueryDataHashtable(booleanSource?"Doc6M013":"Doc5M033",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCodeL+"' ",  dbDoc) ;
                for(int  intNoL=0  ;  intNoL<vectorDoc6M013L.size()  ;  intNoL++) {
                    stringReceiptTax        =  exeUtil.getVectorFieldValue(vectorDoc6M013L,  intNoL,  "ReceiptTax") ;
                    stringSupplementMoney  =  exeUtil.getVectorFieldValue(vectorDoc6M013L,  intNoL,  "SupplementMoney") ;
                    //
                    doubleTemp  +=  exeUtil.doParseDouble(stringReceiptTax) +exeUtil.doParseDouble(stringSupplementMoney);
                }
              
            }
        }
        stringCheapenMoney  =  convert.FourToFive(""+doubleTemp,  0) ;
        doSetValue("CheapenMoneyPrint",  exeUtil.getFormatNum2(stringCheapenMoney)) ;
        // 實付金額
        doubleActualMoney            =  exeUtil.doParseDouble(stringRealMoneySum)  -
                            exeUtil.doParseDouble(stringRetainMoney)  -
                            exeUtil.doParseDouble(stringCheapenMoney) ;
        if(doubleActualMoney  ==  0) {
            messagebox(" [實付金額] 為零，請重新列印。") ;
            return  false ;
        }
        doSetValue("ActualMoneyPrint",  format.format(convert.FourToFive(""+doubleActualMoney,  0),  "999,999,999,999").trim( )) ;
        return  true ;
    }   
    
    
    public  String  getDeleteZero (String   stringValue,  FargloryUtil  exeUtil) throws  Throwable {
        char     charWord    =  'A' ;
        char[]   arrayChar   =  stringValue.toCharArray() ;
        if(stringValue.indexOf(".")  ==  -1)  return  stringValue ;
        if("".equals(stringValue))                return  stringValue ;
        for(int  intNo=arrayChar.length-1  ;  intNo>=0  ;  intNo--) {
            charWord  =  arrayChar[intNo] ;
            if(".".equals(""+charWord))  break ;
            if(exeUtil.doParseDouble(""+charWord)  >  0)  break ;
            stringValue  =  exeUtil.doSubstring(stringValue,  0,  intNo) ;
        }
        return stringValue;
    }
    // 2014-10-02
    public  double  getExistRealMoney(String        stringBarCode,                String      stringComNo,                  String         stringEDateTime,  
                                    String        stringPurchaseNo,               String          stringFactoryNo,                String       stringKindNo,
                                    String          stringBarCodePur,                 String         stringGroupID,                         boolean     booleanSpecPurchaseNo,    
                                  boolean      booleanSource,                  FargloryUtil  exeUtil,                                    talk              dbDoc) throws  Throwable {
        double    doubleTemp                  = 0 ;
        double    doubleExistRealMoney      = 0 ;
        // 請款申請書
        //doubleTemp     =  getPurchaseMoneyUSEDoc2M017(stringBarCode,  stringComNo,  stringEDateTime,  stringPurchaseNo,  stringFactoryNo,  stringKindNo,  booleanSource,  exeUtil) ;
        doubleTemp     =  getPurchaseMoneyDoc2USE(stringBarCode,  stringComNo,  stringEDateTime,  stringPurchaseNo,  stringFactoryNo,  stringKindNo,  stringBarCodePur,  stringGroupID,  booleanSource,  booleanSpecPurchaseNo,  exeUtil,   dbDoc) ;
        doubleExistRealMoney  +=  doubleTemp ;
        // 借款申請書
        //doubleTemp                   =  getPurchaseMoneyUSEDoc6M012(stringBarCode,  stringComNo,  stringEDateTime,  stringPurchaseNo,  stringFactoryNo,  stringKindNo,  booleanSource,  exeUtil,  dbDoc)  ;
        doubleTemp                   =  getPurchaseMoneyDoc6USE(stringBarCode,  stringComNo,  stringEDateTime,  stringPurchaseNo,  stringFactoryNo,  stringKindNo,  stringBarCodePur,  stringGroupID,  booleanSource,  booleanSpecPurchaseNo,  exeUtil,  dbDoc) ;
        doubleExistRealMoney  +=  doubleTemp ;
        // 未在系統已使用
        doubleTemp                   =  getUSEMoney(stringComNo,  stringPurchaseNo,  stringFactoryNo,  exeUtil,  dbDoc) ;
        doubleExistRealMoney  +=  doubleTemp ;  
        return  doubleExistRealMoney ;
    }
    public  double  getPurchaseMoneyDoc2USE(String      stringBarCode,              String        stringComNo,            String      stringEDateTime,  
                                         String     stringPurchaseNo,               String      stringFactoryNo,        String      stringKindNo,
                                         String         stringBarCodePur,               String        stringGroupID,        boolean      booleanSource,           
                                         boolean     booleanSpecPurchaseNo,   FargloryUtil  exeUtil,            talk               dbDoc) throws  Throwable {
        if(booleanSpecPurchaseNo) {
              return  getPurchaseMoneyUSEDoc2M0171(stringBarCode,          stringComNo,            stringEDateTime,  
                                                                                              stringPurchaseNo,     stringFactoryNo,        stringKindNo,  
                                                      stringBarCodePur,     stringGroupID,           booleanSource,         
                                                      exeUtil,                       dbDoc) ;
        } else {
              return  getPurchaseMoneyUSEDoc2M017(stringBarCode,          stringComNo,            stringEDateTime,  
                                                stringPurchaseNo,       stringFactoryNo,          stringKindNo,
                                                booleanSource,           exeUtil,                          dbDoc) ;   
        }
    }
    public  double  getPurchaseMoneyDoc6USE(String      stringBarCode,              String        stringComNo,            String      stringEDateTime,  
                                         String     stringPurchaseNo,               String      stringFactoryNo,        String          stringKindNo,
                                         String         stringBarCodePur,              String       stringGroupID,             boolean       booleanSource,
                                         boolean     booleanSpecPurchaseNo,   FargloryUtil  exeUtil,            talk               dbDoc) throws  Throwable {
        if(booleanSpecPurchaseNo) {
              return  getPurchaseMoneyUSEDoc6M0171(stringBarCode,       stringComNo,          stringEDateTime,  
                                                                                            stringPurchaseNo,  stringFactoryNo,      stringKindNo,  
                                                  stringBarCodePur,  stringGroupID,         booleanSource,       
                                                  exeUtil,                    dbDoc) ;
        } else {
              return  getPurchaseMoneyUSEDoc6M012(stringBarCode,       stringComNo,          stringEDateTime,  
                                                                                          stringPurchaseNo,  stringFactoryNo,      stringKindNo,  
                                                booleanSource,      exeUtil,                     dbDoc) ;
        }
    }
    // 資料庫
    // 資料庫 Doc
    public  void  doAddPrintCount(String  stringTable,  String  stringPrintCount,  String  stringBarCode) throws  Throwable {
        talk           dbDoc               =  getTalk(""  +get("put_Doc"));
        String      stringSql           =  "" ;
        stringSql  =  "UPDATE  "+stringTable+"  SET  PrintCount  =  "  +  stringPrintCount  +  " "  +
                   " WHERE  BarCode  =  '"  +  stringBarCode  +  "' " ;
        dbDoc.execFromPool(stringSql) ;
    }
    // 表格 Doc2M010
    public  double  getPurchaseMoneyUSEDoc2M017(String           stringBarCode,          String         stringComNo,            String       stringEDateTime,  
                                                                                      String           stringPurchaseNo,     String         stringFactoryNo,        String       stringKindNo,  
                                              boolean        booleanSource,         FargloryUtil  exeUtil,                      talk            dbDoc) throws  Throwable {
        String      stringSumRealMoney  =  "0" ;
        String      stringSql                      =  "" ;
        String      stringTable10             =  booleanSource ? "Doc2M010" : "Doc5M020" ;
        String      stringTable17             =  booleanSource ? "Doc2M017" : "Doc5M027" ;
        String[][]  retDoc2M017               =  null ;
        //
        stringSql  =  "SELECT  SUM(M17.PurchaseMoney) "  +
                    "FROM  "+stringTable10+" M10,  "+stringTable17+" M17 "  +
                   " WHERE  M10.BarCode  =  M17.BarCode "  +
                         " AND  M10.UNDERGO_WRITE  <>  'E' " +
                     " AND  M10.ComNo  =  '"            +  stringComNo          +  "' "  +
                     " AND  M17.PurchaseNo  =  '"   +  stringPurchaseNo  +  "' "  +
                     " AND  M10.KindNo  =  '"            +  stringKindNo          +  "' "  +
                     " AND  M10.BarCode  <>  '"       +  stringBarCode        +  "' " +
                                 " AND  M10.EDateTime  <  '"      +  stringEDateTime    +  "' " +
                     " AND  M17.FactoryNo  =  '"       +  stringFactoryNo     +  "' " ;
        retDoc2M017  =  dbDoc.queryFromPool(stringSql) ;
        if(retDoc2M017.length  !=  0) {
            stringSumRealMoney  =  retDoc2M017[0][0].trim( ) ;
            stringSumRealMoney  =  ""  +exeUtil.doParseDouble(stringSumRealMoney) ;
            stringSumRealMoney  =  convert.FourToFive(stringSumRealMoney,  0) ;
        }
        return  exeUtil.doParseDouble(stringSumRealMoney);
    }
    public  double  getPurchaseMoneyUSEDoc2M0171(String          stringBarCode,          String         stringComNo,            String       stringEDateTime,  
                                                                                        String           stringPurchaseNo,     String         stringFactoryNo,        String       stringKindNo,  
                                                String           stringBarCodePur,    String         stringGroupID,           boolean   booleanSource,         
                                                FargloryUtil    exeUtil,                      talk              dbDoc) throws  Throwable {
        String      stringSumRealMoney  =  "0" ;
        String      stringSql                      =  "" ;
        String      stringTable210            =  booleanSource ? "Doc2M010"   : "Doc5M020" ;
        String      stringTable217            =  booleanSource ? "Doc2M0171" : "Doc5M0272" ;
        String      stringTable312            =  booleanSource ? "Doc3M012"   : "Doc5M012" ;
        String[][]  retDoc2M017               =  null ;
        //
        stringSql  =  "SELECT  SUM(M17.PurchaseMoney) "  +
                    "FROM  "+stringTable210+" M10,  "+stringTable217+" M17 "  +
                   " WHERE  M10.BarCode  =  M17.BarCode "  +
                         " AND  M10.UNDERGO_WRITE  <>  'E' " +
                     " AND  M17.RecordNo12  IN (SELECT  RecordNo "  +
                                                                      " FROM  "+stringTable312+" "  +
                                            " WHERE  BarCode  =  '"+stringBarCodePur+"' "  +
                                                  " AND  GroupID  =  '"+stringGroupID+"' ) "  +
                     " AND  M10.ComNo  =  '"            +  stringComNo          +  "' "  +
                     " AND  M17.PurchaseNo  =  '"   +  stringPurchaseNo  +  "' "  +
                     " AND  M10.KindNo  =  '"            +  stringKindNo          +  "' "  +
                     " AND  M10.BarCode  <>  '"       +  stringBarCode        +  "' " +
                                 " AND  M10.EDateTime  <  '"      +  stringEDateTime    +  "' " +
                     " AND  M17.FactoryNo  =  '"       +  stringFactoryNo     +  "' " ;
        retDoc2M017  =  dbDoc.queryFromPool(stringSql) ;
        if(retDoc2M017.length  !=  0) {
            stringSumRealMoney  =  retDoc2M017[0][0].trim( ) ;
            stringSumRealMoney  =  ""  +exeUtil.doParseDouble(stringSumRealMoney) ;
            stringSumRealMoney  =  convert.FourToFive(stringSumRealMoney,  0) ;
        }
        return  exeUtil.doParseDouble(stringSumRealMoney);
    }
    // 表格 Doc5M0201
    public  String[][]  getDoc5M0201(String  stringTable,  String  stringBarCode,  String  stringSqlAnd,  talk dbDoc) throws  Throwable {
        String      stringSql             =  "" ;
        String      stringSqlTemp    =  "" ;
        String[][]  retDoc5M0201    =  null ; 
        // 0  DocCode     1  DocCount 
        stringSql         =  "SELECT  DocCode,    DocCount,  DocTXT "  +
                        " FROM  "+stringTable+" "  +
                      " WHERE  DocCount  >  0 " ;
        if(!"".equals(stringBarCode))  stringSql  +=  " AND  BarCode  =  '"+stringBarCode+"' " ;
        stringSql          +=  stringSqlAnd  +  " ORDER BY  RecordNo " ;
        retDoc5M0201  =  dbDoc.queryFromPool(stringSql) ;
        return retDoc5M0201 ;
    }
    // 表格 Doc5M0291
    public  String  getDocDescriptDoc5M0291(String  stringDocCode,  talk dbDoc) throws  Throwable {
        String      stringSql           =  "" ;
        String[][]  retDoc5M0291  =  null ;
        //
        stringSql  =  "SELECT  DocDescript "  +
                     " FROM  Doc5M0291 "  +
                   " WHERE  DocCode  =  '"        +  stringDocCode  +  "' " ;
        retDoc5M0291  =  dbDoc.queryFromPool(stringSql) ;
        if(retDoc5M0291.length>0)  return retDoc5M0291[0][0].trim() ;
        return  "" ;
    }
    // 表格 Doc2M018
    public  String[][]  getDoc2M018(String  stringTable,  String  stringBarCode,  talk  dbDoc) throws  Throwable {
        String      stringSql                         =  "" ;
        String[][]  retDoc2M018                  =  null ;
        // 0  OptometryNo1       1  OptometryNo2          2  OptometryNo3    3  OptometryType       4  PurchaseNo
        // 5  OptometryType     6  ProjectID1
        stringSql  =  "SELECT  OptometryNo1,  OptometryNo2,  OptometryNo3,  OptometryType,  PurchaseNo,  OptometryType,  ProjectID1 "  +
                     " FROM  "+stringTable+" "  +
                   " WHERE  BarCode  =  '"        +  stringBarCode  +  "' " ;
        retDoc2M018  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc2M018 ;
    }
    public  String  getCostID1View(String  stringComNo,  String  stringCostID,  talk  dbDoc) throws  Throwable {
        String      stringSql               =  "" ;
        String      stringDescription  =  "" ;
        String[][]  retDoc2M020        =  null ;
        //
        stringSql  =  " SELECT  DESCRIPTION "  +
                    " FROM  Doc2M020 " +
                  " WHERE  ComNo  =  '"+stringComNo+"' "  +
                        " AND  RTRIM(CostID)+RTRIM(CostID1)  =  '"   +  stringCostID   +  "' "  ;
        retDoc2M020  =  dbDoc.queryFromPool(stringSql) ;
        if(retDoc2M020.length  !=  0) {
            stringDescription  = retDoc2M020[0][0].trim( ) ;
        }
        return  stringDescription ;
    }
    // 表格 Doc3M010
    public  String[][]  getDoc3M010(String  stringPurchaseNo1,  String  stringPurchaseNo2,  String  stringPurchaseNo3,  String  stringPurchaseNo4,  String  stringComNo,  talk  dbDoc) throws  Throwable {
        String      stringSql                         =  "" ;
        String[][]  retDoc3M010                 =  null ;
        //   0  ComNo           1  DepartNo                    2  FactoryNo     3  PayCondition1                4  PayCondition2
        //   5  PurchaseMoney       6  Unipurchase                   7  Descript              8  BarCode                            9  CostID                     
        // 10  CostID1          11 ExistPurchaseMoney     12  FactoryNo      13  ContractAffirmDate     14 ExistDate
        stringSql  =  "SELECT  ComNo,                  '',                                     FactoryNo,  PayCondition1,          PayCondition2, "  +
                                          " PurchaseMoney,  Unipurchase,                Descript ,    BarCode,                    CostID, "  +
                          " CostID1,                 ExistPurchaseMoney,  FactoryNo,  ContractAffirmDate,  ExistDate, "  +
                          " DocNo4 " +
                   " FROM  Doc3M010 "  +
                   " WHERE  DocNo1  =  '"  +  stringPurchaseNo1  +  "' "  +
                         " AND  DocNo2  =  '"  +  stringPurchaseNo2  +  "' "  +
                     " AND  DocNo3  =  '"  +  stringPurchaseNo3  +  "' "  +
                     " AND  ComNo  =  '"   +  stringComNo            +  "' " ;
        if(!"".equals(stringPurchaseNo4))  stringSql  +=  " AND  DocNo4  =  '"+stringPurchaseNo4+"' " ;
        retDoc3M010  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc3M010 ;
    }
    // 表格 Doc3M011
    public  String[][]  getDoc3M011_2(String  stringTable,  String  stringComNo,  String  stringDocNo,  String  stringBarCode,  String  stringKindNoFront,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql         =  "" ;
        String[][]  retDoc3M011  =  null ;
        // 0  ComNo                     1 DocNo                               2  CDate            3 NeedDate         4  ApplyType
        // 5  Analysis                      6 DepartNo                          7  EDateTime          8 CDate       9  PrintCount
        // 10 CheckAdd               11 CheckAddDescript      12  BarCode      13 ID            14  PayConditionCross
        // 15  UNDERGO_WRITE   16  ExistDate
        stringSql         =  " SELECT  ComNo,                    DocNo,                       CDate,           NeedDate,   ApplyType, "  +
                                                  " Analysis,                  DepartNo,                  EDateTime,  CDate,          PrintCount, "  +
                                                  " CheckAdd,              CheckAddDescript,   BarCode,      ID,                PayConditionCross, "  +
                                                  " UNDERGO_WRITE, ExistDate "  +
                                     " FROM  "+stringTable+" " +
                                    " WHERE  1  =  1 "  ;
        if(!"".equals(stringComNo))          stringSql  +=  " AND  ComNo  =  '"      +  stringComNo            +  "' "  ;
        if(!"".equals(stringDocNo))           stringSql  +=  " AND  DocNo  =  '"       +  stringDocNo             +  "' "  ;
        if(!"".equals(stringBarCode))        stringSql  +=  " AND  BarCode  =  '"   +  stringBarCode          +  "' "  ;
        if(!"".equals(stringKindNoFront))   stringSql  +=  " AND  KindNo  =  '"     +  stringKindNoFront    +  "' "  ;
        if("".equals(stringSqlAnd)) {
            stringSql  +=  " ORDER BY  DocNo1,  DocNo2,  DocNo3 " ;
        }
        retDoc3M011  =  dbDoc.queryFromPool(stringSql+stringSqlAnd) ;
        return  retDoc3M011 ;
    }
    // 表格 Doc3M011_USE
    public  double  getUSEMoney(String  stringComNo,  String  stringDocNo,  String  stringFactoryNo,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String      stringSql                =  "" ;
        String[][]  retDoc3M011         =  null ;
        double    doubleMoneySum  =  0 ;
        //
        stringSql         =  " SELECT  SUM(USEMoney) " +
                                     " FROM  Doc3M011_USE " +
                                   " WHERE  DocNo  =  '"   +  stringDocNo  +  "' "  +
                            " AND  ComNo  =  '"  +  stringComNo  +  "' "  +
                         " AND  FactoryNo  =  '"+stringFactoryNo+"' " ;
        retDoc3M011         =  dbDoc.queryFromPool(stringSql) ;
        doubleMoneySum  =  exeUtil.doParseDouble(retDoc3M011[0][0])  ;
        stringSql                 =  " SELECT  SUM(RequestPrice) " +
                                            " FROM  Doc5M02722 " +
                                         " WHERE  PurchaseNo  =  '"   +  stringDocNo       +  "' "  +
                                   " AND  ComNo  =  '"            +  stringComNo     +  "' "  +
                                 " AND  FactoryNo  =  '"      +   stringFactoryNo+"' " ;
        retDoc3M011           =  dbDoc.queryFromPool(stringSql) ;
        doubleMoneySum  +=  exeUtil.doParseDouble(retDoc3M011[0][0])  ;
        doubleMoneySum    =  exeUtil.doParseDouble(convert.FourToFive(""+doubleMoneySum,  0)) ;
        return    doubleMoneySum ;
    }
    // 表格 Doc3M012
    public  String[][]  getDoc3M012(boolean  booleanSource,  String  stringComNo,  String  stringFactoryNo,  String  stringCDate,  String  stringSqlAnd,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        if("".equals(stringSqlAnd))  return  new  String[0][0] ;
        //
        String      stringSql            =  "" ;
        String      stringTable11    =  booleanSource ?  "Doc3M011" : "Doc5M011" ;
        String      stringTable12    =  booleanSource ?  "Doc3M012" : "Doc5M012" ;
        String      stringTable130  =  booleanSource ?  ""                  : "Doc5M0130" ;
        String      stringField12     =  booleanSource ?  "CostIDDetail" : " '' " ;
        String      stringField130   =  booleanSource ?  " '' " : "M130.NoUseRealMoney" ;
        String[][]  retDoc3M012     =  null ;
                stringCDate        =  exeUtil.getDateConvert(stringCDate) ;
        /*   0  CostID                      1  CostID1                 2 ClassName                3 Descript            4 Unit
        /   5  BudgetNum              6 ActualNum         7  HistoryPrice                8  FactoryNo     9  ActualPrice
        / 10  PurchaseMoney       11 ApplyMoney       12  PurchaseMoney     13  ProjectID1          14  ClassNameDescript  
        /  15  RecordNo                 16  DocNo,               17 NoUseRealMoney      18  CostIDDetail */
        stringSql         =  " SELECT  M12.CostID,                 M12.CostID1,        M12.ClassName,           M12.Descript,      M12.Unit, "  +
                                                  " M12.BudgetNum,         M12.ActualNum,    M12.HistoryPrice,          M12.FactoryNo,   M12.ActualPrice, "  +
                                                  " M12.PurchaseMoney,  M12.ApplyMoney,  M12.PurchaseMoney,   M12.ProjectID1,  M12.ClassNameDescript, " +
                              " M12.RecordNo,            M11.DocNo,          "+stringField130+",          "+stringField12+" "  ;
        if(booleanSource) {
              stringSql  +=      " FROM  "+stringTable11+" M11,  "+stringTable12+" M12 " +
                           " WHERE  M11.BarCode  =  M12.BarCode " ;
        } else {
              stringSql  +=    " FROM  "+stringTable11+" M11,  "+stringTable12+" M12,  "+stringTable130+" M130 " +
                   " WHERE  M11.BarCode  =  M12.BarCode " +
                     " AND  M11.BarCode  =  M130.BarCode " +
                     " AND  M12.RecordNo  =  M130.RecordNo " ;
        }
        stringSql  +=           " AND  M11.ComNo  =  '"  +  stringComNo  +  "' "  +
                       " AND  (  "  +  stringSqlAnd  +  ") "  ;
        if(!"".equals(stringFactoryNo))  
        stringSql  +=           " AND  M12.FactoryNo  =  '"+stringFactoryNo+"' " ;
        if(!booleanSource  &&  !"".equals(stringCDate))        
        stringSql  +=           " AND  ISNULL(M130.ExistDate,'')  <=  '"+stringCDate+"' " ;
        stringSql  +=  " ORDER BY  M12.RecordNo " ;
        System.out.println("retDoc3M012-------------------------------------------") ;
        retDoc3M012  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc3M012 ;
    }
    // 表格 Doc5M0221
    public  String[][]  getDoc5M0221(String  stringBarCode,  talk  dbDoc) throws  Throwable {
        String      stringSql           =  "" ;
        String[][]  retDoc5M0221  =  null ;
        //    0  BorrowAmt    1  DateStart     2 DateEnd           3  AccrualRate       4  Formula  5  Accrual
        stringSql  =  "SELECT  BorrowAmt,  DateStart,  DateEnd,  AccrualRate,  Formula,  Accrual "  +
                   " FROM  Doc5M0221 "  +
                   " WHERE  BarCode  =  '"  +  stringBarCode  +  "' " +
                " ORDER BY  RecordNo " ;
        retDoc5M0221  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc5M0221 ;
    }
    // 表格 Doc5M0222
    public  String[][]  getDoc5M0222(String  stringBarCode,  talk  dbDoc) throws  Throwable {
        String      stringSql           =  "" ;
        String[][]  retDoc5M0222  =  null ;
        //    0  InvestmentTrust    1  FundNo     2 BandNo           3  AccountNo       4  AccountName  
        //    5  Amt                      6  Unit            7  NetAmt
        stringSql  =  "SELECT  InvestmentTrust,  FundNo,  BandNo,  AccountNo,  AccountName, "  +
                                          " Amt,                        Unit,         NetAmt "  +
                   " FROM  Doc5M0222 "  +
                   " WHERE  BarCode  =  '"  +  stringBarCode  +  "' " +
                " ORDER BY  RecordNo " ;
        retDoc5M0222  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc5M0222 ;
    }
    // 表格 Doc5M0224
    public  double  getAmtDoc5M0224(String  stringVoucherYMD,  String  stringVoucherFlowNo,  String  stringVoucherSeqNo,  String  stringBarCode,  String  stringEDateTime,  String  stringComNo,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String      stringSql                 =  "" ;
        String[][]  retDoc5M0224       =  null ;
        //  
        stringSql  =  "SELECT  SUM(Amt) "  +
                   " FROM  Doc5M0224 "  +
                   " WHERE  VOUCHER_YMD  =  "           +  stringVoucherYMD      +  " "  +
                    " AND  VOUCHER_FLOW_NO  =  "  +  stringVoucherFlowNo  +  " "  +
                    " AND  VOUCHER_SEQ_NO  =  "     +  stringVoucherSeqNo   +  " "  +
                    " AND  BarCode  <>  '"                     +  stringBarCode             +  "' "  +
                    " AND  BarCode IN (SELECT  BarCode "  +
                                      " FROM  Doc5M020 "  +
                                     " WHERE  ComNo  =  '"       +  stringComNo  +  "' "  +
                                        " AND  EDateTime  <  '"  +  stringEDateTime    +  "' "  +
                                     " ) "  ;
        retDoc5M0224  =  dbDoc.queryFromPool(stringSql) ;
        return  exeUtil.doParseDouble(retDoc5M0224[0][0].trim()) ;
    }
    // 表格 Doc5M0225
    public  String[][]  getDoc5M0225(String  stringBarCode,  talk  dbDoc) throws  Throwable {
        String      stringSql           =  "" ;
        String[][]  retDoc5M0225  =  null ;
        //    0  ComNo    1  BarCodeF     2 RecordNo           3  CostID       4  RealTotalMoney  
        stringSql  =  "SELECT  ComNo,  BarCodeF,  RecordNo,  CostID,  RealTotalMoney "  +
                   " FROM  Doc5M0225 "  +
                   " WHERE  BarCode  =  '"  +  stringBarCode  +  "' " +
                " ORDER BY  RecordNo " ;
        retDoc5M0225  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc5M0225   ;
    }
    public  String  getCost4Name(String  stringComNo,  String  stringCostID,  String  stringCostID1,  String  stringCostIDDetail,  talk  dbDoc) throws  Throwable {
        String      stringSql                       =  "" ;
        String      stringDescription          =  "" ;
        String[][]  retDoc7M053               =  null ;
        //
        if("".equals(stringCostID))  return  stringDescription ; 
        // 
        if(!"".equals(stringCostIDDetail)) {
            stringSql  =   " SELECT  DESCRIPTION "  +
                         " FROM  Doc2M022 "  +
                        " WHERE  CostIDDetail  =  '"   +  stringCostIDDetail    +  "' " ;
            retDoc7M053  =  dbDoc.queryFromPool(stringSql) ;    
        } else if(!"".equals(stringCostID1)) {
            stringSql  =   " SELECT  DESCRIPTION "  +
                         " FROM  Doc2M020 "  +
                        " WHERE  CostID  =  '"    +  stringCostID     +  "' " +
                             " AND  CostID1  =  '"  +  stringCostID1   +  "' " ;
              if(!"".equals(stringComNo))  stringSql  +=  " AND  ComNo  =  '"   +  stringComNo    +  "'  " ;
            retDoc7M053  =  dbDoc.queryFromPool(stringSql) ;        
        } else {
            stringSql  =  "SELECT  DESCRIPTION "  +
                         " FROM  Doc7M053 "  +
                        " WHERE  CostID  =  '" +  stringCostID  +  "' " ;
            retDoc7M053  =  dbDoc.queryFromPool(stringSql) ;
        }
        if(retDoc7M053.length  >  0)stringDescription  =  retDoc7M053[0][0].trim() ;
        return  stringDescription ;
    }
    // 表格 Doc6M010
    public  double  getPurchaseMoneyUSEDoc6M012(String           stringBarCode,       String          stringComNo,          String           stringEDateTime,  
                                                                                      String           stringPurchaseNo,  String          stringFactoryNo,      String           stringKindNo,  
                                              boolean       booleanSource,       FargloryUtil  exeUtil,                     talk                dbDoc) throws  Throwable {
        String      stringSumRealMoney  =  "0" ;
        String      stringSql                      =  "" ;
        String[][]  retDoc6M012              =  null ;
        //
        if(!booleanSource)  return  0 ;
        //
        stringSql  =  "SELECT  SUM(M12.RealTotalMoney) "  +
                    "FROM  Doc6M010 M10,  Doc6M012 M12 "  +
                   " WHERE  M10.BarCode  =  M12.BarCode "  +
                         " AND  M10.PurchaseNoExist  IN  ('Y',  'YY') " +
                         " AND  M10.UNDERGO_WRITE  <>  'E' " +
                     " AND  M10.KindNo  =  '"          +  stringKindNo           +  "' "  +
                     " AND  M10.ComNo  =  '"           +  stringComNo          +  "' "  +
                     " AND  M10.PurchaseNo  =  '"  +  stringPurchaseNo  +  "' "  +
                     " AND  M10.FactoryNo  =  '"     +  stringFactoryNo      +  "' " +
                     " AND  M10.BarCode  <>  '"     +  stringBarCode        +  "' " +
                                " AND  M10.EDateTime  <  '"     +  stringEDateTime     +  "' " ;
        retDoc6M012  =  dbDoc.queryFromPool(stringSql) ;
        if(retDoc6M012.length  !=  0) {
            stringSumRealMoney  =  retDoc6M012[0][0].trim( ) ;
            stringSumRealMoney  =  ""  +exeUtil.doParseDouble(stringSumRealMoney) ;
            stringSumRealMoney  =  convert.FourToFive(stringSumRealMoney,  0) ;
        }
        return  exeUtil.doParseDouble(stringSumRealMoney);
    }
    public  double  getPurchaseMoneyUSEDoc6M0171(String           stringBarCode,       String          stringComNo,          String           stringEDateTime,  
                                                                                        String           stringPurchaseNo,  String          stringFactoryNo,      String           stringKindNo,  
                                                String            stringBarCodePur,  String         stringGroupID,         boolean       booleanSource,       
                                              FargloryUtil    exeUtil,                     talk              dbDoc) throws  Throwable {
        String      stringSumRealMoney  =  "0" ;
        String      stringSql                      =  "" ;
        String[][]  retDoc6M0171             =  null ;
        //
        if(!booleanSource)  return  0 ;
        //
        stringSql  =  "SELECT  SUM(M171.PurchaseMoney) "  +
                    "FROM  Doc6M010 M10,  Doc6M0171 M171 "  +
                   " WHERE  M10.BarCode  =  M171.BarCode "  +
                         " AND  M10.PurchaseNoExist  IN  ('Y',  'YY') " +
                         " AND  M10.UNDERGO_WRITE  <>  'E' " +
                     " AND  M10.KindNo  =  '"          +  stringKindNo           +  "' "  +
                     " AND  M171.RecordNo12  IN (SELECT  RecordNo "  +
                                                                        " FROM  Doc3M012 "  +
                                              " WHERE  BarCode  =  '"+stringBarCodePur+"' "  +
                                                   " AND  GroupID  =  '"+stringGroupID+"' ) "  +
                     " AND  M10.ComNo  =  '"             +  stringComNo          +  "' "  +
                     " AND  M171.PurchaseNo  =  '"  +  stringPurchaseNo  +  "' "  +
                     " AND  M171.FactoryNo  =  '"     +  stringFactoryNo      +  "' " +
                     " AND  M10.BarCode  <>  '"       +  stringBarCode        +  "' " +
                                " AND  M10.EDateTime  <  '"       +  stringEDateTime     +  "' " ;
        retDoc6M0171  =  dbDoc.queryFromPool(stringSql) ;
        if(retDoc6M0171.length  !=  0) {
            stringSumRealMoney  =  retDoc6M0171[0][0].trim( ) ;
            stringSumRealMoney  =  ""  +exeUtil.doParseDouble(stringSumRealMoney) ;
            stringSumRealMoney  =  convert.FourToFive(stringSumRealMoney,  0) ;
        }
        return  exeUtil.doParseDouble(stringSumRealMoney);
    }
    public  String[][]  getBarCodeForDoc6M010(String  stringTable,  String  stringBarCode,  talk  dbDoc) throws  Throwable {
        String      stringSql                 =  "" ;
        String      stringBarCide11     =  "" ;
        String      stringKindNo          =  "26" ;
        String[][]  retDoc6M010         =  null ;
        // 0  BarCode     1  DocNo1     2  DocNo2     3  DocNo3
        stringSql  =  "SELECT  DISTINCT  M10.BarCode,  M10.DocNo1,  M10.DocNo2,  M10.DocNo3 "  +
                   " FROM  "+stringTable+" M10 "  +
                   " WHERE  M10.KindNo  =  '"  +  stringKindNo  +  "' "  +
                         " AND  M10.UNDERGO_WRITE  <>  'E' " +
                       " AND  M10.DocNo  IN  (SELECT  BorrowNo "  +
                                                               " FROM  Doc6M0101 "  +
                                           " WHERE  BarCode  =  '"  +  stringBarCode  +  "') " +
                       " AND  M10.ComNo  IN  (SELECT  ComNo "  +
                                                               " FROM  Doc6M010 "  +
                                           " WHERE  BarCode  =  '"  +  stringBarCode  +  "') " ;
        retDoc6M010  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc6M010 ;   
    }
    public  String[][]  getDoc2M010DeptCd(String  stringDeptCd,  String  stringDeptCdDoc,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql         =  "" ;
        String[][]  retDoc2M010  =  null ;
        //   0  DEPT_CD     1  DEPT_CD_Doc        2  DEPT_Name
        stringSql  =  " SELECT  DEPT_CD,  DEPT_CD_Doc,  DEPT_Name "  +
                              " FROM  Doc2M010_DeptCd "  +
                  " WHERE  1=1 " ;
        if(!"".equals(stringDeptCd))        stringSql  +=  " AND  DEPT_CD  =  '"           +  stringDeptCd       +  "' " ;
        if(!"".equals(stringDeptCdDoc))  stringSql  +=  " AND  DEPT_CD_Doc  =  '"  +  stringDeptCdDoc  +  "' " ;
        if("".equals(stringSqlAnd)) {
              stringSql  +=  " ORDER BY  DEPT_CD " ;
        } else {
              stringSql  +=  stringSqlAnd ;
         }
        retDoc2M010  =  dbDoc.queryFromPool(stringSql) ;
        //
        return  retDoc2M010 ;
    }
    // 表格 FE3D01
    // 部門名稱
    public  String  getDepartName(String  stringDepartNo,  talk  dbDoc) throws  Throwable {
        String      stringSql                 =  "" ;
        String      stringDepartName  =  "" ;
        String[][]  retFE3D01              =  null ;
        //
        stringSql  =  " SELECT  DEPT_NAME "  +
                    " FROM  FE3D01 " +
                  " WHERE  DEPT_CD  =  '" +  stringDepartNo  +  "' " ;
        retFE3D01  =  dbDoc.queryFromPool(stringSql) ;
        if(retFE3D01.length  !=  0) {
            stringDepartName  = retFE3D01[0][0].trim( ) ;
        }
        return  stringDepartName ;
    }
    // 資料庫 FE3D
    // 表格 FE3D05
    // 人員名稱
    public  String  getEmpName(String  stringEmpNo,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFE3D) throws  Throwable {
        String      stringSql             =  "" ;
        String      stringEmpName  =  "" ;
        String[][]  retFE3D05          =  null ;
        //
        if(stringEmpNo.startsWith("OO")) {
              stringEmpName   =  exeUtil.getNameUnion("EMP_NAME",  "Doc5M011_EmployeeNo",  " AND  EMP_NO  =  '"+stringEmpNo+"' ",  new  Hashtable(),  dbDoc) ;
        }
        if(!"".equals(stringEmpName))  return  stringEmpName ;
        //
        stringSql  =  " SELECT  EMP_NAME "  +
                    " FROM  FE3D05 " +
                  " WHERE  EMP_NO  =  '" +  stringEmpNo  +  "' " ;
        retFE3D05  =  dbFE3D.queryFromPool(stringSql) ;
        if(retFE3D05.length  !=  0) {
            stringEmpName  = retFE3D05[0][0].trim( ) ;
        }
        return  stringEmpName ;
    }
    public  String[][]  getFE3D70(String  stringComNo,  talk  dbFE3D) throws  Throwable {
        String      stringSql             =  "" ;
        String[][]  retFE3D70          =  null ;
        //
        stringSql  =  " SELECT  INSUR_COMP_NAME,  TEL,  FIRM_NO "  +
                    " FROM  FE3D70 " +
                  " WHERE  COMPANY_CD  =  '" +  stringComNo  +  "' " ;
        retFE3D70  =  dbFE3D.queryFromPool(stringSql) ;
        return  retFE3D70 ;
    }
    // 資料庫 FED1
    // 表格 FED1005
    // 廠商名稱
    public  String[][] getFED1005(String  stringFactoryNo,  String  stringComNo,  talk  dbDoc,  talk  dbFED1) throws  Throwable {

        String        stringSql                   =  "" ;
        String        stringFactoryName   =  "" ;
        String[][]    retFED1005              =  null ;
        //
        if(",OO,CS,".indexOf(","+stringComNo+",")  !=  -1) {
            stringSql  =  " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO2,  COMPANY_TEL_NO1,  COMPANY_TEL_NO3 "  +
                        " FROM  Doc3M015 " +
                      " WHERE  OBJECT_CD  =  '" +  stringFactoryNo  +  "' " ;
                            //" AND  (OBJECT_KIND  =  '3'  OR  OBJECT_KIND  =  '2') " ;
            retFED1005  =  dbDoc.queryFromPool(stringSql) ;
            if(retFED1005.length  >  0) {
                if(!"".equals(retFED1005[0][2].trim())) {
                    retFED1005[0][1]  =  retFED1005[0][2].trim()+"-"+retFED1005[0][1].trim() ;
                }
                if(!"".equals(retFED1005[0][3].trim())) {
                    retFED1005[0][1]  =  retFED1005[0][1].trim()+"#"+retFED1005[0][3].trim() ;
                }
            } else {
                stringSql  =  " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO "  +
                            " FROM  FED1005 " +
                          " WHERE  OBJECT_CD  =  '" +  stringFactoryNo  +  "' " ;
                                //" AND  (OBJECT_KIND  =  '3'  OR  OBJECT_KIND  =  '2') " ;
                retFED1005  =  dbFED1.queryFromPool(stringSql) ;
            }
        } else {
            stringSql  =  " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO "  +
                        " FROM  FED1005 " +
                      " WHERE  OBJECT_CD  =  '" +  stringFactoryNo  +  "' " ;
                            //" AND  (OBJECT_KIND  =  '3'  OR  OBJECT_KIND  =  '2') " ;
            retFED1005  =  dbFED1.queryFromPool(stringSql) ;
            if(retFED1005.length  ==  0) {
                stringSql  =  " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO2,  COMPANY_TEL_NO1,  COMPANY_TEL_NO3 "  +
                            " FROM  Doc3M015 " +
                          " WHERE  OBJECT_CD  =  '" +  stringFactoryNo  +  "' " ;
                                //" AND  (OBJECT_KIND  =  '3'  OR  OBJECT_KIND  =  '2') " ;
                retFED1005  =  dbDoc.queryFromPool(stringSql) ;
                if(retFED1005.length  >  0) {
                    if(!"".equals(retFED1005[0][2].trim())) {
                        retFED1005[0][1]  =  retFED1005[0][2].trim()+"-"+retFED1005[0][1].trim() ;
                    }
                    if(!"".equals(retFED1005[0][3].trim())) {
                        retFED1005[0][1]  =  retFED1005[0][1].trim()+"#"+retFED1005[0][3].trim() ;
                    }
                } 
            }
        }
        return  retFED1005 ;
    }
    public  String getObjectNameFED1005(String  stringFactoryNo,  talk  dbFED1) throws  Throwable {
        String      stringSql                  =  "" ;
        String      stringFactoryName  =  "" ;
        String[][]  retFED1005            =  null ;
        //
        stringSql  =  " SELECT  OBJECT_SHORT_NAME "  +
                    " FROM  FED1005 " +
                  " WHERE  OBJECT_CD  =  '" +  stringFactoryNo  +  "' " ;
                        //" AND  (OBJECT_KIND  =  '3'  OR  OBJECT_KIND  =  '2') " ;
        retFED1005  =  dbFED1.queryFromPool(stringSql) ;
        if(retFED1005.length  >  0)  return  retFED1005[0][0].trim() ;
        return  "" ;
    }
    // 表格 FED1006
    public  String[][]  getFED1012(String  stringVoucherYMD,  String  stringVoucherFlowNo,  String  stringVoucherSeqNo,  String  stringComNo,  String  stringKind,  talk  dbFED1) throws  Throwable {
        String      stringSql        =  "" ;
        String[][]  retFED1012  =  null ;
        //  0  DEPT_CD    1  AMT      2  DESCRIPTION_2
        stringSql  =  "SELECT  DEPT_CD,  AMT,  DESCRIPTION_2 "  +
                   " FROM  FED1012 "  +
                   " WHERE  VOUCHER_YMD  =  "           +  stringVoucherYMD      +  " "  +
                    " AND  VOUCHER_FLOW_NO  =  "  +  stringVoucherFlowNo  +  " "  +
                    " AND  VOUCHER_SEQ_NO  =  "     +  stringVoucherSeqNo  +  " "  +
                    " AND  COMPANY_CD  =  '"             +  stringComNo              +  "' "  +
                    " AND  KIND  =  '"                              +  stringKind                    +  "' "  ;
        retFED1012  =  dbFED1.queryFromPool(stringSql) ;
        return  retFED1012 ;
    }
    // 表格 FED1023
    // 公司名稱
    public  String  getCompanyNameUnion(String  stringCompanyCd,  String  stringBarCode,  FargloryUtil  exeUtil,  talk  dbDoc,  talk  dbFED1) throws  Throwable {
        String      stringSql                     =  "" ;
        String      stringCompanyName  =  "" ;
        String[][]  retFED1023               =  null ;
        //
        if("OO".equals(stringCompanyCd)) {
            String  stringDepartNo2     =  exeUtil.getNameUnion("DepartNo2",  "Doc5M020",  " AND  BarCode  =  '"+stringBarCode+"'  ",  new  Hashtable(),  dbDoc) ;
                stringCompanyName  =  exeUtil.getNameUnion("Descript",  "Doc2M010_ProjectID1",  " AND  ProjectID1  =  '"+stringDepartNo2+"'  ",  new  Hashtable(),  dbDoc) ;
            if(!"".equals(stringCompanyName)) {
                return  stringCompanyName;
            }
        }
        stringSql  =  " SELECT  COMPANY_NAME "  +
                    " FROM  FED1023 " +
                  " WHERE  COMPANY_CD  =  '" +  stringCompanyCd  +  "' " ;
        retFED1023  =  dbFED1.queryFromPool(stringSql) ;
        if(retFED1023.length  !=  0) {
            stringCompanyName  = retFED1023[0][0].trim( ) ;
        }
        return  stringCompanyName ;
    }
    // 資料庫 Sale
    // 表格 A_Project (intNo+1)
    public  String  getProjectName(String  stringProjectID) throws  Throwable {
        talk           dbSale                    =  getTalk(""  +get("put_Sale"));  
        String      stringSql                 =  "" ;
        String      stringProjectName  =  "" ;
        String[][]  retAProject             =  null ;
        //
        stringSql  =  " SELECT  ProjectName "  +
                    " FROM  A_Project " +
                  " WHERE  ProjectID  =  '" +  stringProjectID  +  "' "  ;
        retAProject  =  dbSale.queryFromPool(stringSql) ;
        if(retAProject.length  !=  0) {
            stringProjectName  = retAProject[0][0].trim( ) ;
        }
        return  stringProjectName ;
    }
    // 表格 Doc2M0201
    public  Vector  getCostIDVDoc2M0201V(String  stringComNo,  String  stringDate,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringCostID        =  "" ;
      String      stringCostID1      =  "" ;
      String[][]  retDoc2M0201    =  getDoc2M0201(stringComNo,  "",  "",  "",  stringDate,  stringSqlAnd,  dbDoc) ;
        Vector      vectorCostID    =  new  Vector() ;
        for(int  intNo=0  ;  intNo<retDoc2M0201.length  ;  intNo++) {
            stringCostID   =  retDoc2M0201[intNo][0].trim() ;
            stringCostID1  =  retDoc2M0201[intNo][1].trim() ;
            if(vectorCostID.indexOf(stringCostID+stringCostID1)  ==  -1)  vectorCostID.add(stringCostID+stringCostID1) ;
        }
        return vectorCostID ;
    }
    public  String[][]  getDoc2M0201(String  stringComNo,  String  stringCostID,  String  stringCostID1,  String  stringFunctionType,  String  stringDate,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql     =  "" ;
        String[][]  retDoc2M0201  =  null ;
        // 0 CostID   1 CostID1     2 FunctionType      3 DateStart   4 DateEnd     
        // 5 ComNo    6  Remark     7 FunctionName
        stringSql  =  " SELECT  CostID,  CostID1,  FunctionType,  DateStart,  DateEnd, "  +
                              " ComNo,   Remark,   FunctionName "  +
                        " FROM  Doc2M0201 " +
                       " WHERE  1=1 "  ;
        if(!"".equals(stringComNo)) {
           stringSql  +=  " AND (ComNo  =  'ALL'  OR  ComNo  =  '"  +  stringComNo  +  "') " ;
        }
        if(!"".equals(stringCostID))       stringSql  +=  " AND  CostID   =  '"          +  stringCostID        +  "' " ;
        if(!"".equals(stringCostID1))      stringSql  +=  " AND  CostID1  =  '"          +  stringCostID1       +  "' " ;
        if(!"".equals(stringFunctionType)) stringSql  +=  " AND  FunctionType  LIKE  '%" +  stringFunctionType  +  "%' " ;
        if(!"".equals(stringDate)){
            stringSql  +=  " AND  (DateStart  <=  '" +  stringDate  +  "' OR  DateStart  =  '9999/99/99') " +
                           " AND  (DateEnd    >=  '" +  stringDate  +  "' OR  DateStart  =  '9999/99/99') " ;
        }
        //
        stringSql  +=  stringSqlAnd ;
        if(stringSql.indexOf("ORDER")  ==  -1) {
            stringSql     +=  " ORDER BY CostID,  CostID1 " ;
        }
        retDoc2M0201  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc2M0201 ;
    }
    public  String  getGroupID (String  stringTable2171,  String  stringBarCode,  String  stringBarCodePur,  String  stringPurchaseNo,  boolean  booleanSource,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable{
        String        stringGroupID       =  "" ;
        String        stringPurchaseNoL   =  "" ;
        String        stringRecordNo12L   =  "" ;
        Hashtable  hashtableAnd           =  new  Hashtable() ;
        Hashtable  hashtableTableRow    =  new  Hashtable() ;
        Vector      vectorTableData          = null ;
        // 
        if(!booleanSource)  return  "" ;
        //
        hashtableAnd.put("BarCode",  stringBarCode) ;
        vectorTableData   =  exeUtil.getQueryDataHashtable(stringTable2171,  hashtableAnd,  "",  dbDoc) ;
        for(int  intNo=0  ;  intNo<vectorTableData.size()  ;  intNo++) {
            hashtableTableRow  =  (Hashtable) vectorTableData.get(intNo) ;  if(hashtableTableRow  ==  null)  continue ;
            stringPurchaseNoL    =  (""+hashtableTableRow.get("PurchaseNo")).trim() ;
            stringRecordNo12L    =  (""+hashtableTableRow.get("RecordNo12")).trim() ;
            //
            if(!stringPurchaseNo.equals(stringPurchaseNoL))  continue ;
            //
            hashtableAnd.put("BarCode",       stringBarCodePur) ;
            hashtableAnd.put("RecordNo",     stringRecordNo12L) ;
            stringGroupID  =  exeUtil.getNameUnion("GroupID",  "Doc3M012",  "",  hashtableAnd,  dbDoc) ;
            return  stringGroupID ;
        }
        //
        return  "" ;
    }
    // 表格 Doc2M0401
    public  String[][]  getDoc2M0401(String  stringCostID,  String  stringUseType,  String  stringSqlAnd, talk  dbDoc) throws  Throwable {
        String      stringSql                  =  "" ;
        String      stringSqlAndL          =  "" ;
        String[]    arrayUseType          =  convert.StringToken(stringUseType,  ",") ;  
        String[][]  retDoc2M0401         =  null ;
        // 0 CostID     1  UseType      2  Remark     3  FunctionName
        stringSql  =  " SELECT  CostID,  UseType,  Remark,  FunctionName "  +
                              " FROM  Doc2M0401 " +
                            " WHERE  UseStatus = 'Y' " ;
        if(!"".equals(stringCostID))   stringSql  +=  " AND  CostID  =  '"+stringCostID+"' " ;
        for(int  intNo=0  ;  intNo<arrayUseType.length  ;  intNo++) {
            if(!"".equals(stringSqlAndL))  stringSqlAndL  +=  " OR " ;
            stringSqlAndL  +=  " UseType  LIKE  '%"+arrayUseType[intNo]+"%' " ;
        }
        //if(!"".equals(stringUseType))  stringSql  +=  " AND  UseType  LIKE  '%"+stringUseType+"%' " ;
        stringSql         +=  " AND ("+stringSqlAndL+")" ;
        stringSql         +=  stringSqlAnd ;
        retDoc2M0401  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc2M0401 ;
    }
    public  Vector  getDoc2M0401V(String  stringCostID,  String  stringUseType,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String[][]  retDoc2M040   =  getDoc2M0401(stringCostID,  stringUseType,  stringSqlAnd,  dbDoc) ;
        Vector      vectorCostID  =  new  Vector() ;
        for(int  intNo=0  ; intNo<retDoc2M040.length  ;  intNo++)vectorCostID.add(retDoc2M040[intNo][0].trim()) ;
        return vectorCostID ;
    }
    public  void  doDoc5M0272(boolean  booleanSource,  String  stringBarCode,  String  stringEDateTime,  String  stringComNo,  String  stringKindNo,  String  stringKindNoFront,  String  stringFactoryNo,  String  stringSqlAndPurchaseNo,  Hashtable  hashtableDoc5M0272,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        if("".equals(stringSqlAndPurchaseNo))  return  ; 
        //
        Object        objectTemp                    =  null ;
        String        stringKey                       =  "" ;
        String        stringPurchaseNo          =  "" ;
        String        stringRecordNo              =  "" ;
        String        stringRequestNum        =  "" ;
        String        stringPurchaseMoney   =  "" ;
        String[]      arrayTemp                     =  new  String[4] ;
        String[][]    retPreDoc5M0272         =  getDoc5M0272(booleanSource,  stringEDateTime,  stringComNo,  stringKindNo,  " AND  ("+stringSqlAndPurchaseNo+") \nAND  BarCode  <>  '"+stringBarCode+"' \n AND  FactoryNo  =  '"+stringFactoryNo+"' ",  dbDoc) ;
        String[][]    retDoc5M0272                =  getDoc5M0272(booleanSource,  stringEDateTime,  stringComNo,  stringKindNo,  " AND  ("+stringSqlAndPurchaseNo+")  \nAND  BarCode  =  '"+stringBarCode+"' \n",     dbDoc) ;
        // 0  PurchaseNo       1  RecordNo          2  RequestNum    3  PurchaseMoney
        for(int  intNo=0  ;  intNo<retPreDoc5M0272.length  ;  intNo++) {
            stringPurchaseNo        =  retPreDoc5M0272[intNo][0].trim() ;
            stringRecordNo            =  retPreDoc5M0272[intNo][1].trim() ;
            stringRequestNum       =  retPreDoc5M0272[intNo][2].trim() ;
            stringPurchaseMoney  =  retPreDoc5M0272[intNo][3].trim() ;
            stringKey                     =  stringPurchaseNo+stringRecordNo ;
            System.out.println("stringKey("+stringKey+")前期--------------------------------("+stringRequestNum+")("+stringPurchaseMoney+")") ;
            //
            objectTemp  =  hashtableDoc5M0272.get(stringKey) ;
            if(objectTemp  ==  null) {
                arrayTemp                   =  new  String[4] ;             for(int  i=0  ;  i<arrayTemp.length  ;  i++)  arrayTemp[i] =  "" ;
                hashtableDoc5M0272.put(stringKey,  arrayTemp) ;
            } else {
                arrayTemp  =  (String[])  objectTemp ;
            }
            arrayTemp[0]  =  ""+(exeUtil.doParseDouble(arrayTemp[0])+exeUtil.doParseDouble(stringRequestNum)) ;
            arrayTemp[1]  =  ""+(exeUtil.doParseDouble(arrayTemp[1])+exeUtil.doParseDouble(stringPurchaseMoney)) ;
        }
        String[][]  retPreDoc5M02722      =  getDoc5M02722(stringEDateTime,  stringComNo,  stringKindNoFront,  " AND  ("+stringSqlAndPurchaseNo+")",  exeUtil,  dbDoc)  ;
        for(int  intNo=0  ;  intNo<retPreDoc5M02722.length  ;  intNo++) {
            stringPurchaseNo        =  retPreDoc5M02722[intNo][0].trim() ;
            stringRecordNo            =  retPreDoc5M02722[intNo][1].trim() ;
            stringRequestNum       =  retPreDoc5M02722[intNo][2].trim() ;
            stringPurchaseMoney  =  retPreDoc5M02722[intNo][3].trim() ;
            stringKey                     =  stringPurchaseNo+stringRecordNo ;
            System.out.println("stringKey("+stringKey+")前期--------------------------------"+stringRequestNum) ;
            //
            objectTemp  =  hashtableDoc5M0272.get(stringKey) ;
            if(objectTemp  ==  null) {
                arrayTemp                   =  new  String[4] ;             for(int  i=0  ;  i<arrayTemp.length  ;  i++)  arrayTemp[i] =  "" ;
                hashtableDoc5M0272.put(stringKey,  arrayTemp) ;
            } else {
                arrayTemp  =  (String[])  objectTemp ;
            }
            arrayTemp[0]  =  ""+(exeUtil.doParseDouble(arrayTemp[0])+exeUtil.doParseDouble(stringRequestNum)) ;
            arrayTemp[1]  =  ""+(exeUtil.doParseDouble(arrayTemp[1])+exeUtil.doParseDouble(stringPurchaseMoney)) ;
        }
        for(int  intNo=0  ;  intNo<retDoc5M0272.length  ;  intNo++) {
            stringPurchaseNo        =  retDoc5M0272[intNo][0].trim() ;
            stringRecordNo            =  retDoc5M0272[intNo][1].trim() ;
            stringRequestNum       =  retDoc5M0272[intNo][2].trim() ;
            stringPurchaseMoney  =  retDoc5M0272[intNo][3].trim() ;
            stringKey                     =  stringPurchaseNo+stringRecordNo ;
            System.out.println("stringKey("+stringKey+")本期--------------------------------("+stringRequestNum+")("+stringPurchaseMoney+")") ;
            //
            objectTemp  =  hashtableDoc5M0272.get(stringKey) ;
            if(objectTemp  ==  null) {
                arrayTemp                   =  new  String[4] ;             for(int  i=0  ;  i<arrayTemp.length  ;  i++)  arrayTemp[i] =  "" ;
                hashtableDoc5M0272.put(stringKey,  arrayTemp) ;
            } else {
                arrayTemp  =  (String[])  objectTemp ;
            }
            arrayTemp[2]  =  ""+(exeUtil.doParseDouble(arrayTemp[2])+exeUtil.doParseDouble(stringRequestNum)) ;
            arrayTemp[3]  =  ""+(exeUtil.doParseDouble(arrayTemp[3])+exeUtil.doParseDouble(stringPurchaseMoney)) ;
        }
    }
    // 已請款案別金額整理
    public  Hashtable  getUsedProjectIDMoney(boolean  booleanSource,  String  stringBarCode,  String  stringEDateTime,  String  stringComNo,  String[][]  retDoc2M017,  Vector  vectorKey,  Hashtable  hashtableRealMoneyAll,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String[]     arrayTemp             =  null ;
        Vector      vectorTable6Data  =  new  Vector() ;
        // 0  PurchaseNo1                1  PurchaseNo2          2  PurchaseNo3    3  RetainMoney        4  PurchaseMoney
        // 5  PurchaseNo4       6  FactoryNo               7  ProjectID1
        for(int  intNo=0  ;  intNo<retDoc2M017.length  ;  intNo++) {
          arrayTemp      =  new  String[7] ;
          arrayTemp[0]  =  retDoc2M017[intNo][0].trim() ;   // PurchaseNo1
          arrayTemp[1]  =  retDoc2M017[intNo][1].trim() ;  // "PurchaseNo2
          arrayTemp[2]  =  retDoc2M017[intNo][2].trim() ;  // "PurchaseNo3
          arrayTemp[3]  =   retDoc2M017[intNo][5].trim() ;  // "PurchaseNo4
          arrayTemp[4]  =   retDoc2M017[intNo][4].trim() ;  // "PurchaseMoney
          arrayTemp[5]  =   retDoc2M017[intNo][6].trim() ;  // "FactoryNo
          arrayTemp[6]  =  retDoc2M017[intNo][7].trim() ;
          vectorTable6Data.add(arrayTemp) ;
        }
        return  getUsedProjectIDMoneyDetail(booleanSource,  stringComNo,  stringBarCode,  stringEDateTime,  (String[][])  vectorTable6Data.toArray(new  String[0][0]),  vectorKey,  hashtableRealMoneyAll,  exeUtil,  dbDoc) ;
    }
    public  Hashtable  getUsedProjectIDMoneyDetail(boolean  booleanSource,  String  stringComNo,  String  stringBarCode,  String  stringEDateTime,  String[][]  retTable6Data,  Vector  vectorKey,  Hashtable  hashtableRealMoneyAll,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
          //System.out.println("已請款案別金額整理----------------------S") ;
          Hashtable    hashtableRealMoney            =  new  Hashtable() ;
          String           stringBarCodeL                    =  "" ;
              String           stringKey                               =  "" ;
          String           stringLimit                              =  "%---%" ;
              String           stringPurchaseMoney           =  "" ;
              String           stringPurchaseNo                 =  "" ;
              String           stringPurchaseNo1               =  "" ;
              String           stringPurchaseNo1L             =  "" ;
              String           stringPurchaseNo2               =  "" ;
              String           stringPurchaseNo2L             =  "" ;
              String           stringPurchaseNo3               =  "" ;
              String           stringPurchaseNo3L             =  "" ;
              String           stringPurchaseNo4               =  "" ;
              String           stringSqlAnd                         =  "" ;
              String           stringSqlAnd1                       =  "" ;
              String           stringFactoryNo                    =  "" ;
              String           stringFactoryNoL                  =  "" ;
              String           stringPurchaseSumMoney   =  "" ;
              String           stringNoUseRealMoney       =  "" ;
              String           stringProjectID1                   =  "" ;
              String           stringProjectID1L                 =  "" ;
              String[]         arrayTemp                           =  null ;
              String[][]       retDataTemp                       =  null ;
              String[][]       retDoc2M010                       =  null ;
              String[][]       retDoc2M012                       =  null ;
              String[][]       retDoc3M013                       =  null ;
              String[][]       retDoc3M014                       =  null ;
              double         doubleRealMoney                =  0 ;
              double        doublePurchaseMoney          =  0  ;
              double        doublePurchaseMoneySum   =  0  ;
              double       doubleTemp                            =  0 ;
              Vector        vectorBarCode                        =  new  Vector() ;
              Vector        vectorData                               =  new  Vector() ;
              Vector        vectorKeyThis                          =  new  Vector() ;
              Vector        vectorFactoryNo                      =  new  Vector() ;
              Vector        vectorPurchaseSumMoney     =  new  Vector() ;
          //
          for(int  intNo=0  ;  intNo<retTable6Data.length  ;  intNo++) {
              stringPurchaseNo1        =  retTable6Data[intNo][0].trim() ;
            stringPurchaseNo2        =  retTable6Data[intNo][1].trim() ;
            stringPurchaseNo3        =  retTable6Data[intNo][2].trim() ;
            stringPurchaseNo4        =  retTable6Data[intNo][3].trim() ; 
            stringPurchaseMoney    =  retTable6Data[intNo][4].trim() ;
                      stringFactoryNo             =  retTable6Data[intNo][5].trim() ; 
            stringProjectID1             =  retTable6Data[intNo][6].trim() ; 
            //
            if(!"".equals(stringSqlAnd))  stringSqlAnd  +=  " OR " ;
            stringSqlAnd  =  " (PurchaseNo  =  '"  +stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3  +  "') "  ;
            //
            doublePurchaseMoneySum  +=  exeUtil.doParseDouble(stringPurchaseMoney) ;
            //
            if(!"Z".equals(stringPurchaseNo4))  {
                System.out.println("已請款案別金額整理----------------------ERROR1") ;
                return  null ;
            }
            // 請購之案別分攤整理
            // 0  InOut     1  DepartNo       2  ProjectID        3  ProjectID1   4  CostID
            // 5  CostID1   6  RealMoney      7  BudgetMoney
             retDoc3M014    =  getDoc5M014(booleanSource,  "",  stringComNo,  stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3,  "",  dbDoc)  ;
             vectorKeyThis  =  new  Vector() ;
             for(int  intNoL=0  ;  intNoL<retDoc3M014.length  ;  intNoL++) {
                  doubleRealMoney  =  exeUtil.doParseDouble(retDoc3M014[intNoL][6].trim()) ; 
                  //
                  if("".equals(retDoc3M014[intNoL][3].trim())) {  //  ProjectID1
                      stringKey        =  "I"+stringLimit+
                                                 retDoc3M014[intNoL][1].trim()  + stringLimit +     // 0  DepartNo
                                     retDoc3M014[intNoL][4].trim()  + stringLimit +      // 6  CostID
                                     retDoc3M014[intNoL][5].trim()   ;                          // 7  CostID1
                  } else {
                      stringKey        =  "O"+stringLimit+
                                                 retDoc3M014[intNoL][3].trim()  + stringLimit +     // 5  ProjectID1
                                     retDoc3M014[intNoL][4].trim()  + stringLimit +       // 6  CostID
                                     retDoc3M014[intNoL][5].trim()   ;                          // 7  CostID1
                  }
                   doubleTemp          =   doubleRealMoney  +  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) ;
                   hashtableRealMoney.put(stringKey,  ""+doubleTemp) ;
                   doubleTemp          =  doubleRealMoney  +  exeUtil.doParseDouble(""+hashtableRealMoneyAll.get(stringKey)) ;
                   hashtableRealMoneyAll.put(stringKey,  ""+doubleTemp) ;
                   System.out.println(intNo+"請購案別金額整理----------------------["+stringKey+"]"+doubleRealMoney) ;
                   //
                   if(vectorKey.indexOf(stringKey)      ==  -1)  vectorKey.add(stringKey) ;
                   if(vectorKeyThis.indexOf(stringKey)  ==  -1)  vectorKeyThis.add(stringKey) ;
             }
              retDoc3M013  =  getDoc3M013Union(stringComNo,  stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3,  "",  booleanSource,  dbDoc) ;
              for(int  intNoL=0  ;  intNoL<retDoc3M013.length  ;  intNoL++) {
                    stringFactoryNoL                 =  retDoc3M013[intNoL][0].trim() ;
                  stringPurchaseSumMoney  =  retDoc3M013[intNoL][1].trim() ;
                  stringNoUseRealMoney       =  retDoc3M013[intNoL][8].trim() ;
                  //
                  if(exeUtil.doParseDouble(stringNoUseRealMoney)  >  0) {
                      if(vectorFactoryNo.indexOf(stringFactoryNoL)  !=  -1) {
                          vectorPurchaseSumMoney.remove(vectorFactoryNo.indexOf(stringFactoryNoL)) ;
                          vectorFactoryNo.remove(stringFactoryNoL) ;
                      }
                      //
                      vectorFactoryNo.add(stringFactoryNoL) ;
                      vectorPurchaseSumMoney.add(""+(exeUtil.doParseDouble(stringPurchaseSumMoney)  -  exeUtil.doParseDouble(stringNoUseRealMoney))) ;
                  } else {
                      if(vectorFactoryNo.indexOf(stringFactoryNoL)  ==  -1) {
                          vectorFactoryNo.add(stringFactoryNoL) ;
                          vectorPurchaseSumMoney.add(stringPurchaseSumMoney) ;
                      }
                  }
                       }
                       // 請款申請書
             retDoc2M010     =  getDoc5M020(booleanSource,  stringBarCode,  stringEDateTime,  stringComNo,  stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3,  "",  dbDoc) ;
             vectorBarCode  =  new  Vector() ;
             //
                    vectorBarCode.add(stringBarCode) ; // 本身不計算
            // 請購為單一案別分攤  直接扣除
            // 請款[單請購單]      直接扣除
            // 請款[多請購單]      請款 單費用分攤   直接使用請購金額扣除
            //                              請款 多費用分攤   [先扣除所有請款案別分攤] [再加非此請購單之案別分攤]
            for(int  intNoL=0  ;  intNoL<retDoc2M010.length  ;  intNoL++) {
                stringBarCodeL  =  retDoc2M010[intNoL][0].trim() ;
                //
                if(vectorBarCode.indexOf(stringBarCodeL)!=-1)  continue ;
                vectorBarCode.add(stringBarCodeL) ;
                //
                retDataTemp  =  getDoc5M027Union(booleanSource ?"Doc2M017":"Doc5M027",  stringBarCodeL,  dbDoc) ;
                  for(int  intDataTemp=0  ;  intDataTemp<retDataTemp.length ;  intDataTemp++) {
                      stringPurchaseNo1L  =  retDataTemp[intDataTemp][0].trim() ;
                    stringPurchaseNo2L  =  retDataTemp[intDataTemp][1].trim() ;
                    stringPurchaseNo3L  =  retDataTemp[intDataTemp][2].trim() ;
                    stringProjectID1L       =  retDataTemp[intDataTemp][7].trim() ;
                    stringPurchaseNo     =  stringPurchaseNo1L+stringProjectID1L+stringPurchaseNo2L+stringPurchaseNo3L ;
                    //
                    if(stringPurchaseNo.equals(stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3)) {
                        doublePurchaseMoney  =  exeUtil.doParseDouble(retDataTemp[intDataTemp][4].trim()) ;
                        break ;
                    }
                      }
                // 請購為單一案別分攤
                if(vectorKeyThis.size()  ==  1) {
                          stringKey                 =  (""+vectorKeyThis.get(0)).trim() ;
                        doubleRealMoney   =  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) -
                                                      doublePurchaseMoney ;
                                        hashtableRealMoney.put(stringKey,  ""+doubleRealMoney);
                    System.out.println("1請款   案別金額整理----------------------請購為單一案別分攤["+stringKey+"]"+doubleRealMoney) ;
                    continue;
                      }
                // 請款申請書之請購筆數
                vectorData      =  new  Vector() ;
                // 取得 該筆之費用分攤
                retDoc2M012          =  getDoc5M022Union(booleanSource?"Doc2M012":"Doc5M022",  stringBarCodeL,  "",  dbDoc) ;
                for(int  intT=0  ;  intT<retDoc2M012.length  ;  intT++) {
                    if("".equals(retDoc2M012[intT][3].trim())) {
                        stringKey        =  "I"+stringLimit+
                                                     retDoc2M012[intT][1].trim()  + stringLimit +     // 3  DepartNo
                                       retDoc2M012[intT][4].trim()  + stringLimit +       // 6  CostID
                                       retDoc2M012[intT][5].trim()  ;                            // 7  CostID1
                    } else {
                        stringKey        =  "O"+stringLimit+
                                                     retDoc2M012[intT][3].trim()  + stringLimit +     // 5  ProjectID1
                                       retDoc2M012[intT][4].trim()  + stringLimit +       // 6  CostID
                                       retDoc2M012[intT][5].trim()  ;                           // 7  CostID1                       
                    }
                    // 針對請購單之資料，過濾費用分攤資料，並加總金額
                    if(vectorKeyThis.indexOf(stringKey)  ==  -1) continue ;
                    //
                    if(retDoc2M012.length  ==  1) {
                        doubleRealMoney  =  doublePurchaseMoney ;
                        System.out.println("2 請款   案別金額整理----------------------請款單一案別分攤["+stringKey+"]"+doubleRealMoney) ;
                    } else {
                        doubleRealMoney  =  exeUtil.doParseDouble(retDoc2M012[intT][7].trim()) ;
                        System.out.println("3 請款   案別金額整理----------------------請款多案別分攤 [先] 刪除["+stringKey+"]"+doubleRealMoney) ;
                    }
                    //
                     doubleRealMoney   =  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) -
                                       doubleRealMoney ;
                    hashtableRealMoney.put(stringKey,  ""+doubleRealMoney);
                }
                if(retDataTemp.length  >  1) {
                          // [請款申請書]對應多筆 [請購單]
                    // 0  PurchaseNo1       1  PurchaseNo2          2  PurchaseNo3    3  RetainMoney        4  PurchaseMoney
                    // 5  PurchaseNo4       6  FactoryNo
                    // 請款多費用分攤
                    if(retDoc2M012.length  >  1) {
                                for(int  intT=0  ;  intT<retDataTemp.length ;  intT++) {
                            stringPurchaseNo1L      =  retDataTemp[intT][0].trim() ;
                            stringPurchaseNo2L      =  retDataTemp[intT][1].trim() ;
                            stringPurchaseNo3L      =  retDataTemp[intT][2].trim() ;
                            stringProjectID1L           =  retDataTemp[intT][7].trim() ;
                            doublePurchaseMoney  =  exeUtil.doParseDouble(retDataTemp[intT][4].trim()) ;
                            stringPurchaseNo          =  stringPurchaseNo1L+stringProjectID1L+stringPurchaseNo2L+stringPurchaseNo3L ;
                            // 非本次請購單才作加回動作 
                            if(stringPurchaseNo.equals(stringPurchaseNo1+stringProjectID1+stringPurchaseNo2+stringPurchaseNo3)) continue ;
                            //
                            // 0  InOut     1  DepartNo       2  ProjectID        3  ProjectID1   4  CostID
                            // 5  CostID1   6  RealMoney      7  BudgetMoney
                              retDoc3M014    =  getDoc5M014(booleanSource,  "",  stringComNo,  stringPurchaseNo,  "",  dbDoc) ;
                            for(int  intDoc3M014=0  ;  intDoc3M014<retDoc3M014.length  ;  intDoc3M014++) {
                                doubleRealMoney  =  exeUtil.doParseDouble(retDoc3M014[intDoc3M014][6].trim()) ; 
                                //
                                if("".equals(retDoc3M014[intDoc3M014][3].trim())) {
                                    stringKey        =  "I"+stringLimit+
                                                                             retDoc3M014[intDoc3M014][1].trim()  + stringLimit +    // 3  DepartNo
                                                   retDoc3M014[intDoc3M014][4].trim()  + stringLimit +    // 6  CostID
                                                   retDoc3M014[intDoc3M014][5].trim()   ;                        // 7  CostID1   
                                } else {
                                    stringKey        =  "O"+stringLimit+
                                                                             retDoc3M014[intDoc3M014][3].trim()  + stringLimit +    // 5  ProjectID1
                                                   retDoc3M014[intDoc3M014][4].trim()  + stringLimit +    // 6  CostID
                                                   retDoc3M014[intDoc3M014][5].trim()   ;                         // 7  CostID1                                   
                                }
                                          if(vectorKeyThis.indexOf(stringKey)  ==  -1) continue ;
                                //
                                if(retDoc3M014.length  ==  1) {
                                      // 該請購單為單一案別分攤
                                      doubleRealMoney   =  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) +
                                                                                              doublePurchaseMoney ;
                                                                            System.out.println("4請款   案別金額整理----------------------請款多案別分攤 [後] 加回 請購單單一案別分攤["+stringKey+"]"+doubleRealMoney) ;
                                      hashtableRealMoney.put(stringKey,  ""+doubleRealMoney);
                                                                } else {
                                      // 非模糊(該請購為單一廠商，且請款時為一次使用完)
                                      doubleRealMoney   =  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) +
                                                                                              doubleRealMoney ;
                                      hashtableRealMoney.put(stringKey,  ""+doubleRealMoney);
                                      System.out.println("4請款   案別金額整理----------------------請款多案別分攤 [後] 加回 請購單一次申請書完["+stringKey+"]"+doubleRealMoney) ;
                                                                }
                                                    }
                                 }
                          }
                          }
                     }
          }
          // 借款沖銷
          if(!"".equals(stringSqlAnd))  stringSqlAnd1  =  " AND  BarCode  IN  (SELECT  BarCode "  +
                                                                                                                       " FROM  "+(booleanSource?"Doc6M010":"Doc5M030")+" "  +
                                                                                                                     " WHERE  ("  +  stringSqlAnd  +  ")"  +
                                                                   " AND  EDateTime  <= '"+stringEDateTime+"' " +
                                                                   " AND  PurchaseNoExist  =  'Y' )"   ;
          for(int  intNo=0  ;  intNo<vectorKey.size()  ;  intNo++) {
                  stringKey    =  (""+vectorKey.get(intNo)) ;
            arrayTemp    =  convert.StringToken(stringKey,  stringLimit) ;
            //
            if(arrayTemp.length  !=  4)  {
                System.out.println("已請款案別金額整理----------------------ERROR3["+stringKey+"]") ;
                return  null ;
                      }
            // 
            doubleRealMoney   =  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) ;
            doubleRealMoney  -=  getRealTotalMoneySumForDoc5M032(booleanSource,  stringBarCode,  arrayTemp[0],  arrayTemp[1],  arrayTemp[2],  arrayTemp[3],  stringSqlAnd1,  exeUtil,  dbDoc) ;
            hashtableRealMoney.put(stringKey,  ""+doubleRealMoney) ;
            System.out.println(doubleRealMoney+"----------------------["+stringKey+"]") ;
          }
          //System.out.println("已請款案別金額整理----------------------E") ;
          return  hashtableRealMoney ;
    }
    public  boolean  isSpectPurchaseNo (String  stringComNo,  String  stringKindNo,  String  stringDocNo,  String  stringSqlAnd,  boolean  booleanSource,  talk dbDoc) throws  Throwable{
        String      stringSql         =  "" ;
        String[][]  retDoc3M013   =  null ;
        //
        if(!booleanSource)  return  false ;
        //
        stringSql          =  " SELECT  M13.BarCode "  +
                        " FROM  Doc3M011 M11,  Doc3M013 M13 " +
                       " WHERE  M11.BarCode  =  M13.BarCode "  +
                         stringSqlAnd  +
                         " AND  M11.ComNo  =  '"        +  stringComNo              +  "' "  +
                         " AND  M11.DocNo  =  '"           +  stringDocNo               +  "' "  +
                          " AND  M11.KindNo  =  '"          +  stringKindNo           +  "' "  ;
        retDoc3M013  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc3M013.length  >  0 ;
    }
    // 表格 Doc5M013
    public  String[][]  getDoc3M013Union(String  stringComNo,  String  stringDocNo,  String  stringFactoryNo,  boolean  booleanTable,  talk  dbDoc) throws  Throwable {
        String      stringSql    =  "" ;
        String      stringTable1 =  booleanTable?"Doc3M013":"Doc5M013" ;
        String      stringTable2 =  booleanTable?"Doc3M011":"Doc5M011" ;
        String[][]  retDoc3M013  =  null ;
        // 0  FactoryNo         1  PurchaseSumMoney       2  PercentRate      3  MonthNum     4  PurchaseMoney
        // 5  PayCondition1         6  PayCondition2                   7  Descript
        stringSql         =  " SELECT  FactoryNo,              PurchaseSumMoney,   PercentRate,  MonthNum,      PurchaseMoney, "  +
                                                   " PayCondition1,      PayCondition2,              Descript,         NoUseRealMoney" +
                                     " FROM  "+stringTable1+" " +
                                   " WHERE  BarCode IN  (SELECT BarCode " +
                                                                           " FROM  "+stringTable2+" "  +
                                                                         " WHERE  ComNo =  '"   +  stringComNo  +"' "  +
                                                                               " AND  DocNo  =  '"  +  stringDocNo  +"') " ;
        if(!"".equals(stringFactoryNo))  stringSql  +=  " AND  FactoryNo  =  '"+stringFactoryNo+"' " ;
        stringSql  +=  " ORDER BY FactoryNo, RecordNo " ;
        retDoc3M013  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc3M013 ;
    }
    // 表格 Doc5M014
    public  String[][]  getDoc5M014(boolean  booleanSource,  String  stringBarCode,  String  stringComNo,  String  stringDocNo,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql            =  "" ;
        String      stringSqlTemp   =  "" ;
        String      stringTable14    =  booleanSource ? "Doc3M014" :  "Doc5M014" ;
        String      stringTable11    =  booleanSource ? "Doc3M011" :  "Doc5M011" ;
        String[][]  retDoc3M014    =  null ; 
        // 0  InOut     1  DepartNo       2  ProjectID        3  ProjectID1   4  CostID
        // 5  CostID1   6  RealMoney      7  BudgetMoney
        stringSql         =  "SELECT  M14.InOut,     M14.DepartNo,                                               M14.ProjectID,            M14.ProjectID1, M14.CostID, "  +
                                                 " M14.CostID1, (M14.RealMoney-M14.NoUseRealMoney), M14.BudgetMoney "  +
                         " FROM  "+stringTable14+" M14 ,  "+stringTable11+" M11 "  +
                       " WHERE  M14.BarCode  =  M11.BarCode " +
                             " AND  M11.UNDERGO_WRITE  <>  'X' " ;
        if(!"".equals(stringBarCode))  stringSql  +=  " AND  M11.BarCode  =  '"+stringBarCode+"' " ;
        if(!"".equals(stringComNo))     stringSql  +=  " AND  M11.ComNo    =  '"+stringComNo+"' " ;
        if(!"".equals(stringDocNo))      stringSql  +=  " AND  M11.DocNo    =  '"+stringDocNo+"' " ;
        stringSql   +=  stringSqlAnd  +  " ORDER BY  M14.ProjectID1,  M14.CostID,  M14.CostID1 " ;
        retDoc3M014  =  dbDoc.queryFromPool(stringSql) ;
        return retDoc3M014 ;
    }
    // 表格 Doc5M020
    public  String  getRetainMoneyDoc5M020(String  stringBarCode,  String  stringComNo,  String  stringFactoryNo,  String  stringEDateTime,  String  stringKindNo,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql                         =  "" ;
        String[][]  retDoc5M0272               =  null ;
        // 
        stringSql  =  "SELECT  SUM(M20.RetainMoney) "  +
                     " FROM  Doc5M020 M20,  Doc5M027 M27 "  +
                   " WHERE  M20.BarCode  =  M27.BarCode "  +
                         " AND  M20.BarCode  <>  '"  +  stringBarCode     +  "' " +
                     " AND  EDateTime  <=  '"       +  stringEDateTime  +  "' " +
                     " AND  ComNo  =  '"               +  stringComNo        +  "' " +
                     " AND  KindNo  =  '"               +  stringKindNo        +  "' " +
                     " AND  FactoryNo  =  '"          +  stringFactoryNo   +  "' " +
                     " AND  PurchaseNo  IN  ("+  stringSqlAnd        +  ") "   ;
        retDoc5M0272  =  dbDoc.queryFromPool(stringSql) ;

        return  retDoc5M0272[0][0] ;
    }
    public  String[][]  getDoc5M020(boolean  booleanSource,  String  stringBarCode,  String  stringEDateTime,  String  stringComNo,  String  stringPurchaseNo,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql            =  "" ;
        String      stringTable10    =  booleanSource ? "Doc2M010" :  "Doc5M020" ;
        String      stringTable17    =  booleanSource ? "Doc2M017" :  "Doc5M027" ;
        String[][]  retDoc5M020    =  null ; 
        // 0  InOut     1  DepartNo       2  ProjectID        3  ProjectID1   4  CostID
        // 5  CostID1   6  RealMoney      7  BudgetMoney
              stringSql  =  "SELECT  BarCode "  +
                                    " FROM  "+stringTable10+" "  +
                          " WHERE  BarCode <> '"+stringBarCode+"' "  +
                            " AND  UNDERGO_WRITE  <>  'E' "  +
                      " AND  ComNo  =  '"+stringComNo+"' "  +
                            " AND  EDateTime  <=  '"+stringEDateTime+"' "  +
                            " AND  BarCode  IN  (SELECT  BarCode "  +
                                                              " FROM  "+stringTable17+" "  +
                                              " WHERE  PurchaseNo  =  '"+stringPurchaseNo+"') "  +
                            " ORDER BY  EDateTime " ;
        retDoc5M020  =  dbDoc.queryFromPool(stringSql) ;
        return retDoc5M020 ;
    }
    public  String[][]  getDoc5M022Union(String  stringTable,  String  stringBarCode,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql           =  "" ;
        String[][]  retDoc5M022   =  null ;
        //  0  InOut            1  DepartNo       2  ProjectID              3  ProjectID1       4  CostID           
        //  5  CostID1        6  RealMoney      7  RealTotalMoney
        stringSql  =  "SELECT  InOut,      DepartNo,       ProjectID,       ProjectID1,  CostID, "  +
                                          " CostID1,   RealMoney,    RealTotalMoney "  +
                    " FROM  "+stringTable+" "  +
                    " WHERE  1=1 "  ;
        if(!"".equals(stringBarCode))  stringSql  +=  " AND  BarCode  =  '"  +  stringBarCode  +  "' "  ;
        stringSql  +=  stringSqlAnd+
                      " ORDER BY  RecordNo " ;
        retDoc5M022  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc5M022 ;
    }
    // 表格 Doc5M023
    public  String  getReceiptTaxDoc5M023(String  stringBarCode,  String  stringComNo,  String  stringFactoryNo,  String  stringEDateTime,  String  stringKindNo,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql                         =  "" ;
        String[][]  retDoc5M0272               =  null ;
        // 
        stringSql  =  "SELECT  SUM(ISNULL(ReceiptTax,0)+ISNULL(SupplementMoney,0)) "  +
                     " FROM  Doc5M020 M20,  Doc5M027 M27,  Doc5M023 M23 "  +
                   " WHERE  M20.BarCode  =  M27.BarCode "  +
                         " AND  M20.BarCode  =  M23.BarCode "  +
                         " AND  M20.BarCode  <>  '"     +  stringBarCode     +  "' " +
                     " AND  EDateTime  <=  '"          +  stringEDateTime  +  "' " +
                     " AND  ComNo  =  '"                  +  stringComNo        +  "' " +
                     " AND  KindNo  =  '"                  +  stringKindNo        +  "' " +
                     " AND  M27.FactoryNo  =  '"     +  stringFactoryNo   +  "' " +
                     " AND  PurchaseNo  IN  ("+  stringSqlAnd        +  ") "   ;
        retDoc5M0272  =  dbDoc.queryFromPool(stringSql) ;

        return  retDoc5M0272[0][0] ;
    }
    // 表格 Doc5M027
    public  String[][]  getDoc5M027Union(String  stringTable,  String  stringBarCode,  talk  dbDoc) throws  Throwable {
        String      stringSql                         =  "" ;
        String[][]  retDoc5M027                 =  null ;
        // 0  PurchaseNo1       1  PurchaseNo2          2  PurchaseNo3    3  RetainMoney        4  PurchaseMoney
        // 5  PurchaseNo4   6  FactoryNo              7 ProjectID1
        stringSql  =  "SELECT  PurchaseNo1,  PurchaseNo2,  PurchaseNo3,  RetainMoney,  PurchaseMoney, "  +
                            " PurchaseNo4,  FactoryNo,    ProjectID1 "  +
                     " FROM  "+stringTable+" "  +
                   " WHERE  BarCode  =  '"        +  stringBarCode  +  "' " ;
        retDoc5M027  =  dbDoc.queryFromPool(stringSql) ;

        return  retDoc5M027 ;
    }
    // 表格 Doc5M0272
    public  String[][]  getITEMCountDoc5M0272(boolean  booleanSource,  String  stringEDateTime,  String  stringComNo,  String  stringKindNo,  String  stringFactoryNo,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql                         =  "" ;
        String    stringTable272         =  booleanSource ? "Doc2M0171" : "Doc5M0272" ;
        String    stringTable20          =  booleanSource ? "Doc2M010"   : "Doc5M020" ;
        String[][]  retDoc5M0272               =  null ;
        stringSql  =  "SELECT  DISTINCT  PurchaseNo,  RecordNo12\n"  +
                     " FROM  "+stringTable272+" \n"  +
                   " WHERE  FactoryNo  =  '"+stringFactoryNo+"' " +
                   stringSqlAnd ;
        if(!"".equals(stringComNo)  ||  !"".equals(stringEDateTime)) {
            stringSql  +=  " AND  BarCode  IN  (SELECT  BarCode \n"  +
                                                               " FROM  "+stringTable20+" \n"  +
                                        " WHERE  UNDERGO_WRITE  <>  'E' \n";
            if(!"".equals(stringEDateTime))  stringSql  +=  " AND  EDateTime  <=  '"  +  stringEDateTime  +  "' \n"  ;
            if(!"".equals(stringComNo))        stringSql  +=  " AND  ComNo  =  '"          +  stringComNo        +  "' \n" ;
            if(!"".equals(stringKindNo))        stringSql  +=  " AND  KindNo  =  '"          +  stringKindNo        +  "' \n" ;
            stringSql  +=  ") \n" ;
        }
        retDoc5M0272  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc5M0272 ;
    }
    public  String[][]  getDoc5M0272(boolean  booleanSource,  String  stringEDateTime,  String  stringComNo,  String  stringKindNo,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String        stringSql                           =  "" ;
        String      stringTalbe272          =  booleanSource ? "Doc2M0171" :  "Doc5M0272" ;
        String      stringTalbe371          =  booleanSource ? "Doc6M0171" :  "Doc5M0371" ;
        String      stringTalbe20         =  booleanSource ? "Doc2M010"   :  "Doc5M020" ;
        String      stringTalbe30         =  booleanSource ? "Doc6M010"   :  "Doc5M030" ;
        String[][]    retDoc5M0272                =  null ;
        Vector        vectorTableData     =  new  Vector() ;
        // 0  PurchaseNo       1  RecordNo12          2  RequestNum    3  PurchaseMoney
        stringSql  =  "SELECT  PurchaseNo,  RecordNo12,  SUM(RequestNum),  SUM(PurchaseMoney) \n"  +
                     " FROM  "+stringTalbe272+" \n"  +
                   " WHERE  RequestNum >  0 " +
                   stringSqlAnd ;
        if(!"".equals(stringComNo)  ||  !"".equals(stringEDateTime)) {
            stringSql  +=  " AND  BarCode  IN  (SELECT  BarCode \n"  +
                                                               " FROM  "+stringTalbe20+" \n"  +
                                    " WHERE  UNDERGO_WRITE  <>  'E' \n";
            if(!"".equals(stringEDateTime))  stringSql  +=  " AND  EDateTime  <=  '"  +  stringEDateTime  +  "' \n"  ;
            if(!"".equals(stringComNo))        stringSql  +=  " AND  ComNo  =  '"          +  stringComNo        +  "' \n" ;
            if(!"".equals(stringKindNo))        stringSql  +=  " AND  KindNo  =  '"          +  stringKindNo        +  "' \n" ;
            stringSql  +=  ") \n" ;
        }
        stringSql  +=  " GROUP BY  PurchaseNo,  RecordNo12 " ;
        retDoc5M0272  =  dbDoc.queryFromPool(stringSql) ;   for(int  intNo=0  ;  intNo<retDoc5M0272.length  ;  intNo++)  vectorTableData.add(retDoc5M0272[intNo]) ;
        // 借款
        // 0  PurchaseNo       1  RecordNo12          2  RequestNum    3  PurchaseMoney
        stringSql  =  "SELECT  PurchaseNo,  RecordNo12,  SUM(RequestNum),  SUM(PurchaseMoney) \n"  +
                     " FROM  "+stringTalbe371+" \n"  +
                   " WHERE  RequestNum >  0 " +
                   stringSqlAnd ;
        if(!"".equals(stringComNo)  ||  !"".equals(stringEDateTime)) {
            stringSql  +=  " AND  BarCode  IN  (SELECT  BarCode \n"  +
                                                               " FROM  "+stringTalbe30+" \n"  +
                                    " WHERE  UNDERGO_WRITE  <>  'E' \n";
            if(!"".equals(stringEDateTime))  stringSql  +=  " AND  EDateTime  <=  '"  +  stringEDateTime  +  "' \n"  ;
            if(!"".equals(stringComNo))        stringSql  +=  " AND  ComNo  =  '"          +  stringComNo        +  "' \n" ;
            if(!"".equals(stringKindNo))        stringSql  +=  " AND  KindNo  =  '"          +  stringKindNo        +  "' \n" ;
            stringSql  +=  ") \n" ;
        }
        stringSql  +=  " GROUP BY  PurchaseNo,  RecordNo12 " ;
        retDoc5M0272  =  dbDoc.queryFromPool(stringSql) ;   for(int  intNo=0  ;  intNo<retDoc5M0272.length  ;  intNo++)  vectorTableData.add(retDoc5M0272[intNo]) ;
        return  (String[][])  vectorTableData.toArray(new  String[0][0]) ;
    }
    // 表格 Doc5M02722
    public  String[][]  getDoc5M02722(String  stringEDateTime,  String  stringComNo,  String  stringKindNoFront,  String  stringSqlAnd,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String      stringSql                         =  "" ;
        String[][]  retDoc5M02722             =  null ;
        // 0  PurchaseNo       1  RecordNo12          2  RequestNum    3  PurchaseMoney
        stringSql  =  "SELECT  PurchaseNo,  RecordNo12,  SUM(RequestNum),  SUM(RequestPrice) \n"  +
                     " FROM  Doc5M02722 \n"  +
                   " WHERE  1=1 " +stringSqlAnd ;
        if(!"".equals(stringEDateTime))  stringSql  +=  " AND  RequestDate  <=  '"  +  exeUtil.doSubstring(stringEDateTime, 0, 10)  +  "' \n"  ;
        if(!"".equals(stringComNo))        stringSql  +=  " AND  ComNo  =  '"              +  stringComNo                                                   +  "' \n" ;
        if(!"".equals(stringKindNoFront))stringSql  +=  " AND  KindNo  =  '"              +  stringKindNoFront                                           +  "' \n" ;
        stringSql            +=  " GROUP BY  PurchaseNo,  RecordNo12 " ;
        retDoc5M02722  =  dbDoc.queryFromPool(stringSql) ;
        return  retDoc5M02722 ;
    }
    // 表格 Doc5M032
    public  double  getRealTotalMoneySumForDoc5M032(boolean  booleanSource,  String  stringBarCodeExcept,  String  stringInOut,    String  stringDepartNo, 
                                                                                           String     stringCostID,       String  stringCostID1,              String  stringSqlAnd,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String      stringSql                                 =  "" ;
        String      stringTable10                         =  booleanSource ? "Doc6M010" :  "Doc5M030" ;
        String      stringTable12                         =  booleanSource ? "Doc6M012" :  "Doc5M032" ;
        String[][]  retDoc5M032                         =  null ; 
        // 
        stringSql         =  "SELECT  SUM(RealTotalMoney) "  +
                                    " FROM  "+stringTable12+" "  +
                        " WHERE  BarCode  NOT  IN  (SELECT  BarCode  FROM  Doc6M010  WHERE  UNDERGO_WRITE  =  'E' )" ;
        if(!"".equals(stringBarCodeExcept))   stringSql  +=  "  AND  BarCode  <>  '"  +  stringBarCodeExcept  +  "' " ;
        if("I".equals(stringInOut)) {
            if(!"".equals(stringDepartNo))           stringSql   +=  " AND  ISNULL(ProjectID1,'')  = '' "  +
                                                                                             " AND  DepartNo  =  '"   +  stringDepartNo            +  "' " ;
        } else {
            if(!"".equals(stringDepartNo))           stringSql   +=  " AND  ProjectID1  =  '"   +  stringDepartNo            +  "' " ;
        }
        if(!"".equals(stringCostID))                stringSql  +=  " AND  CostID  =  '"         +  stringCostID                +  "' " ;
        if(!"".equals(stringCostID1))              stringSql  +=  " AND  CostID1  =  '"       +  stringCostID1              +  "' " ;
        stringSql  +=  stringSqlAnd ;
        retDoc5M032  =  dbDoc.queryFromPool(stringSql) ;
        return exeUtil.doParseDouble(retDoc5M032[0][0]) ;
    }
    public  String[][]  getDoc5M0220(String  stringBarCodeRef,  String  stringFactoryNo,  String  stringBarCode,  String  stringSqlAnd,  talk  dbDoc) throws  Throwable {
        String      stringSql      =  "" ;
        String[][]  retDoc5M0220   =  null ; 
        // 0 BarCode      1  BarCodeRef         2  FactoryNo      3  RecordNo     4  BackRetainMoney      5  EDateTime
        stringSql         =  "SELECT  M220.BarCode,  M220.BarCodeRef,  M220.FactoryNo,  M220.RecordNo,  M220.BackRetainMoney,  M20.EDateTime "  +
                                    " FROM  Doc5M0220 M220,  Doc5M020 M20 "  +
                       "  WHERE  M220.BarCode  =  M20.BarCode " +
                       stringSqlAnd ;
        if(!"".equals(stringFactoryNo))        stringSql  +=  "  AND  M220.FactoryNo  =  '"+stringFactoryNo   +"' " ;
        if(!"".equals(stringBarCode))          stringSql  +=  "  AND  M220.BarCode    =  '"+stringBarCode     +"' " ;
        if(!"".equals(stringBarCodeRef))     stringSql  +=  "  AND  M220.BarCodeRef =  '"+stringBarCodeRef  +"' " ;
        stringSql  +=  " ORDER BY  M20.EDateTime,  M220.RecordNo " ;
        retDoc5M0220  =  dbDoc.queryFromPool(stringSql) ;
        return retDoc5M0220 ;
    }
    public  double  getBackRetainMoneyDoc5M0220(String  stringBarCodeRef,  String  stringFactoryNo,  String  stringBarCode,  String  stringSqlAnd,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        String[][]  retDoc5M0220                 =  getDoc5M0220(stringBarCodeRef,  stringFactoryNo,  stringBarCode,  stringSqlAnd,  dbDoc) ;
        double    doubleBackRetainMoney  =  0 ;
        for(int  intNo=0  ;  intNo<retDoc5M0220.length  ;  intNo++) {
            doubleBackRetainMoney  +=  exeUtil.doParseDouble(retDoc5M0220[intNo][4].trim()) ;
        }
        return  doubleBackRetainMoney;
    }
    public  String  getDeptCdDoc(String  stringDeptCd,  String  stringDeptCdDoc,  String  stringSqlAnd,  Hashtable  hashtableDeptCdDoc,  talk  dbDoc) throws  Throwable {
        String  stringDeptCdDocL  =  (""+hashtableDeptCdDoc.get(stringDeptCd)).trim() ;
        if(!"null".equals(stringDeptCdDocL)) {
            return  stringDeptCdDocL ;
        }
        //
        String[][]  retDoc2M010  =  getDoc2M010DeptCd(stringDeptCd,  stringDeptCdDoc,  stringSqlAnd,  dbDoc) ;
        //
        if(retDoc2M010.length  >  0)  {
            stringDeptCdDocL  =  retDoc2M010[0][1].trim() ;
            hashtableDeptCdDoc.put(stringDeptCd,  stringDeptCdDocL) ;
            return  stringDeptCdDocL ;
        }
        //
        hashtableDeptCdDoc.put(stringDeptCd,  "") ;
        return  "" ;
    }
    // 由某一組請購單，取得所有有[前後期關係]的請購單號
    public  Vector  getAllPurchaseNo(String  stringComNo,  String  stringFactoryNo,  Vector  vectorPurchaseNo,  String  stringBarCode,  String  stringSqlAnd,  String  stringKindNo,  boolean  booleanSource,  talk  dbDoc) throws  Throwable {
        String      stringSql                     =  "" ;
        String      stringPurchaseNoSql  =  getPurchaseNoSql(vectorPurchaseNo) ;
        String      stringPurchaseNo       =  "" ;
        String      stringTable17             =  booleanSource ? "Doc2M017" :  "Doc5M027" ;
        String      stringTable10             =  booleanSource ? "Doc2M010" :  "Doc5M020" ;
        String      stringTable60             =  booleanSource ? "Doc6M010" :  "Doc5M030" ;
        String[][]  retDoc2M017              =  null ;
        String[][]  retDoc6M010              =  null ;
        Vector      vectorPurchaseNoAll  =  new  Vector() ;   if(vectorPurchaseNo.size()  ==  0)  return  vectorPurchaseNoAll ;
        // 請款
        boolean  booleanFlag  =  true ;
        for(int  intNo=0  ;  intNo<100  ;  intNo++) {
            stringSql  =  "SELECT  M17.PurchaseNo " +
                           " FROM  "  +  stringTable17+ " M17,  "+stringTable10+" M10 " +
                          " WHERE  M17.BarCode  =  M10.BarCode " +
                             " AND  M10.ComNo  =  '"     +stringComNo     +"' " +
                         " AND  M10.KindNo  =  '"     +stringKindNo     +"' " +
                             " AND  M17.FactoryNo  =  '"+stringFactoryNo+"' " +  
                           //" AND  PurchaseNo  NOT IN ("+stringPurchaseNoSql+") " +
                             " AND  M10.BarCode  IN  (SELECT  M20.BarCode " +
                                                           " FROM  "  +  stringTable17+ " M27,  "+stringTable10+" M20 " +
                                                         " WHERE  M27.BarCode  =  M20.BarCode " +
                                                               " AND  M20.ComNo  =  '"     +stringComNo     +"' " + 
                                                               " AND  M27.FactoryNo  =  '"+stringFactoryNo+"' " +  
                                                               " AND  M27.PurchaseNo  IN ("+stringPurchaseNoSql+")) " +
                            stringSqlAnd ;
            if(!"".equals(stringBarCode))  stringSql  +=  " AND  M10.BarCode  <>  '"+stringBarCode+"' "  ;
            retDoc2M017  =  dbDoc.queryFromPool(stringSql) ;
            booleanFlag  =  false ;
            for(int  intNoL=0  ;  intNoL<retDoc2M017.length  ;  intNoL++) {
                stringPurchaseNo  =  retDoc2M017[intNoL][0].trim() ;
                //
                if(vectorPurchaseNoAll.indexOf(stringPurchaseNo)==-1) {
                  vectorPurchaseNoAll.add(stringPurchaseNo) ;
                  booleanFlag  =  true ;
                }
            }
            if(!booleanFlag) {
                break ;
            }
            stringPurchaseNoSql  =  getPurchaseNoSql(vectorPurchaseNoAll) ;
        }
        //
        return  vectorPurchaseNoAll ;
    }
    public  String  getPurchaseNoSql(Vector  vectorPurchaseNo) throws  Throwable {
        String  stringSqlAnd      =  "" ;
        String  stringPurchaseNo  =  "" ;
        for(int  intNo=0  ;  intNo<vectorPurchaseNo.size()  ;  intNo++) {
          stringPurchaseNo  =  (""+vectorPurchaseNo.get(intNo)).trim() ;  if("null".equals(stringPurchaseNo))  continue ;
                                                                                 if("".equals(stringPurchaseNo))         continue ;
          if(!"".equals(stringSqlAnd))  stringSqlAnd  +=  ", " ;
          stringSqlAnd     +=  "'"+stringPurchaseNo+"'" ;
        }
        return stringSqlAnd ;
    }
    public  String  getLastDocNo(String  stringComNo,  String  stringFactoryNo,  Vector  vectorPurchaseNo,  String  stringBarCode,  String  stringSqlAnd,  String  stringKindNo,  boolean  booleanSource,  FargloryUtil  exeUtil,  talk  dbDoc) throws  Throwable {
        Vector        vectorAllPurchaseNo  =  getAllPurchaseNo(stringComNo,  stringFactoryNo,  vectorPurchaseNo,  "",  stringSqlAnd,  stringKindNo,  booleanSource,  dbDoc) ;
        Vector        vectorEDateTime        =  new  Vector() ;
        Hashtable  hashtableDocNo         =  new  Hashtable() ;
        Hashtable  hashtableBarCode      =  new  Hashtable() ;
        //
        doGetPurchaseNoOrderByEDateTime(hashtableDocNo,  hashtableBarCode,  vectorEDateTime,  stringComNo,  stringFactoryNo,  vectorAllPurchaseNo,  "",  stringKindNo,  booleanSource) ;
        //
        int            intPos                  =  0 ;
        String      stringEDateTime  =  "" ;
        String      stringDocNo         =  "" ;
        String      stringBarCodeCF =  "" ;
        String[]    arrayEDateTime  =  (String[])  vectorEDateTime.toArray(new  String[0]) ;
        String[]    arrayDocNo          =  null ;
        Vector     vectorDocNo        =  null ;
        Vector     vectorBarCode     =  null ;
        boolean  booleanFlag         =  true ;
        Arrays.sort(arrayEDateTime) ;
        for(int  intNo=0  ;  intNo<arrayEDateTime.length  ;  intNo++) {
            stringEDateTime  =  arrayEDateTime[intNo].trim() ;
            vectorDocNo         =  (Vector)  hashtableDocNo.get(stringEDateTime) ;
            vectorBarCode      =  (Vector)  hashtableBarCode.get(stringEDateTime) ;
            if(vectorBarCode  ==  null) {
                doEMail("條碼編號("+stringBarCode+")之請款單列印發生錯誤，vectorBarCode 為 null",  exeUtil) ;
                return  "" ;
            }
            if(vectorDocNo  ==  null) {
                doEMail("條碼編號("+stringBarCode+")之請款單列印發生錯誤，vectorDocNo 為 null",  exeUtil) ;
                return  "" ;
            }
            if(vectorDocNo.size()  !=  vectorBarCode.size()) {
                doEMail("條碼編號("+stringBarCode+")之請款單列印發生錯誤，vectorDocNo("+vectorDocNo.size()+") 與 vectorBarCode("+vectorBarCode.size()+") 數量不一致",  exeUtil) ;
                return  "" ;
            }
            if(vectorDocNo.size()  ==  0) {
                doEMail("條碼編號("+stringBarCode+")之請款單列印發生錯誤，vectorDocNo 數量為 0",  exeUtil) ;
                return  "" ;
            }
            if(vectorBarCode.size()  ==  0) {
                doEMail("條碼編號("+stringBarCode+")之請款單列印發生錯誤，vectorBarCode 數量為 0",  exeUtil) ;
                return  "" ;
            }
            booleanFlag  =  false ;
            for(int  intNoL=0  ;  intNoL<vectorBarCode.size()  ;  intNoL++) {
                stringBarCodeCF  =  (""+vectorBarCode.get(intNoL)).trim() ;  
                if("null".equals(stringBarCodeCF)) {
                    doEMail("條碼編號("+stringBarCode+")之請款單列印發生錯誤，vectorBarCode之第 "+(intNoL+1)+" 為 null",  exeUtil) ;
                    return  "" ;
                }
                if(stringBarCode.equals(stringBarCodeCF)) {
                    booleanFlag  =  true ;
                    break ;
                }
            }
            if(booleanFlag)  break ;
            //
            arrayDocNo  =  (String[]) vectorDocNo.toArray(new  String[0]) ;  Arrays.sort(arrayDocNo) ;
            stringDocNo  =   arrayDocNo[0].trim() ;  
        }
        return stringDocNo ;
    }
    public  void  doEMail(String  stringContent,  FargloryUtil  exeUtil) throws  Throwable {
        String    stringSend  =  "B3018@farglory.com.tw" ;
        String[]  arrayUser    =  {stringSend} ;
        exeUtil.doEMail("請款單列印錯誤",  stringContent,  stringSend,  arrayUser) ;
    }
    // [前後期關係]的請款單號之請款順序
    public  void  doGetPurchaseNoOrderByEDateTime(Hashtable  hashtableDocNo,  Hashtable  hashtableBarCode,  Vector  vectorEDateTime,  String  stringComNo,  String  stringFactoryNo,  Vector  vectorPurchaseNo,  String  stringSqlAnd,  String  stringKindNo,  boolean  booleanSource) throws  Throwable {
        String      stringSql                     =  "" ;
        String      stringPurchaseNoSql  =  getPurchaseNoSql(vectorPurchaseNo) ;
        String      stringDocNo               =  "" ;
        String      stringTable17             =  booleanSource ? "Doc2M017" :  "Doc5M027" ;
        String      stringTable10             =  booleanSource ? "Doc2M010" :  "Doc5M020" ;
        String      stringTable60             =  booleanSource ? "Doc6M010" :  "Doc5M030" ;
        String      stringEDateTime         =  "" ;
        String      stringBarCode            =  "" ;
        String[][]  retDoc2M017              =  null ;
        String[][]  retDoc6M010              =  null ;
        Vector      vectorTemp                =  new  Vector() ;
        Vector      vectorBarCode           =  new  Vector() ;
        // 請款
        stringSql  =  "SELECT  M10.EDateTime,  M10.DocNo, M10.BarCode " +
                   " FROM  "  +  stringTable17+ " M17,  "+stringTable10+" M10 " +
                   " WHERE  M17.BarCode  =  M10.BarCode " +
                    " AND  M10.ComNo  =  '"    +stringComNo    +"' " + 
                    " AND  M10.KindNo  =  '"    +stringKindNo    +"' " + 
                    " AND  M17.FactoryNo  =  '"+stringFactoryNo+"' " + 
                    " AND  M17.PurchaseNo  IN ("+stringPurchaseNoSql+") " +
                    stringSqlAnd +
               " ORDER BY  M10.EDateTime,  M10.DocNo, M10.BarCode " ;
        retDoc2M017  =  dbDoc.queryFromPool(stringSql) ;
        for(int  intNo=0  ;  intNo<retDoc2M017.length  ;  intNo++) {
            stringEDateTime    =  retDoc2M017[intNo][0].trim() ;
            stringDocNo           =  retDoc2M017[intNo][1].trim() ;
            stringBarCode       =  retDoc2M017[intNo][2].trim() ;
            System.out.println("stringBarCode("+stringBarCode+")--------------------") ;
            //
            if(vectorEDateTime.indexOf(stringEDateTime) ==-1)    vectorEDateTime.add(stringEDateTime) ;
            //
            vectorTemp       =  (Vector) hashtableDocNo.get(stringEDateTime) ;
            vectorBarCode  =  (Vector) hashtableBarCode.get(stringEDateTime) ;
            if(vectorTemp  ==  null) {
               vectorTemp     =  new  Vector() ;
               vectorBarCode  =  new  Vector() ;
               hashtableDocNo.put(stringEDateTime,     vectorTemp)  ;
               hashtableBarCode.put(stringEDateTime,  vectorBarCode)  ;
            }
            vectorTemp.add(stringDocNo) ;
            vectorBarCode.add(stringBarCode) ;
        }
        //借款沖銷 
        stringSql  =  "SELECT  M10.EDateTime,  M10.DocNo,  M10.BarCode " +
                       " FROM  "  +  stringTable60+ " M10 " +
                      " WHERE  M10.ComNo  =  '"      +stringComNo     +"' " + 
                     " AND  M10.KindNo  =  '"      +stringKindNo     +"' " + 
                         " AND  M10.FactoryNo  =  '" +stringFactoryNo+"' " + 
                         " AND  M10.PurchaseNo  IN ("+stringPurchaseNoSql+") " +
                         stringSqlAnd +
                     " ORDER BY  M10.EDateTime,  M10.DocNo, M10.BarCode " ;
        retDoc6M010  =  dbDoc.queryFromPool(stringSql) ;
        for(int  intNo=0  ;  intNo<retDoc6M010.length  ;  intNo++) {
            stringEDateTime   =  retDoc2M017[intNo][0].trim() ;
            stringDocNo          =  retDoc6M010[intNo][1].trim() ;
            stringBarCode      =  retDoc2M017[intNo][2].trim() ;
            //
            if(vectorEDateTime.indexOf(stringEDateTime) ==-1)    vectorEDateTime.add(stringEDateTime) ;
            //
            vectorTemp       =  (Vector) hashtableDocNo.get(stringEDateTime) ;
            vectorBarCode  =  (Vector) hashtableBarCode.get(stringEDateTime) ;
            if(vectorTemp  ==  null) {
                 vectorTemp     =  new  Vector() ;
                 vectorBarCode  =  new  Vector() ;
                 hashtableDocNo.put(stringEDateTime,    vectorTemp)  ;
                 hashtableBarCode.put(stringEDateTime,  vectorBarCode)  ;
            }
            vectorTemp.add(stringDocNo) ;
            vectorBarCode.add(stringBarCode) ;
        }
    }
    public  String  getCheapenMoney(String  stringBarCode,  String  stringFunction,  String  stringPrevFunction,  FargloryUtil  exeUtil,  talk  dbDoc,  String  stringCheapenMoney) throws  Throwable {
        boolean     booleanSource            =  stringPrevFunction.indexOf("Doc5M")==-1 ;
        String        stringSql                    =  "SELECT  SUM(ISNULL(ReceiptTax,0) + ISNULL(SupplementMoney,0))  FROM  Doc5M023  WHERE  BarCode  =  '"+stringBarCode+"' " ;
        String[][]    retData                      =  new  String[0][0] ;
        //
        if("".equals(stringCheapenMoney)  ||  "null".equals(stringCheapenMoney))    stringCheapenMoney    =  "0" ;
        //
        if("A".equals(stringFunction)) {
            // 請款
             if(booleanSource) {
                retData  =  dbDoc.queryFromPool(stringSql.replaceAll("Doc5M023",  "Doc2M013")) ;    // 行銷
             } else {
                retData  =  dbDoc.queryFromPool(stringSql) ;                            // 管理
             }
        } else if("B".equals(stringFunction)) {
            retData  =  dbDoc.queryFromPool(stringSql.replaceAll("Doc5M023",  "Doc6M013")) ;        // 借款沖銷
        } 
        if(retData.length  ==  0) return  stringCheapenMoney ;
        //
        stringCheapenMoney  =  convert.FourToFive(""+exeUtil.doParseDouble(retData[0][0]),  0) ;
        //
        return  stringCheapenMoney;
    }
    public  String  getInformation( ) {
        return "---------------新增按鈕程式.preProcess()----------------";
    }
}
