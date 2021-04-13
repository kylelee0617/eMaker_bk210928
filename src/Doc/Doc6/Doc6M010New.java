package  Doc.Doc6 ;
import      jcx.jform.bTransaction;
import      java.io.*;
import      java.util.*;
import      java.util.regex.Pattern ;
import      jcx.util.*;
import      jcx.html.*;
import      jcx.db.*;
import      javax.swing.* ;
import     com.jacob.activeX.*;
import     com.jacob.com.*;
import     Doc.Doc2M010 ;
import     Farglory.util.FargloryUtil;

public  class  Doc6M010New  extends  bTransaction{
    public  boolean  action (String  value) throws  Throwable{
        //201808check BEGIN
        System.out.println("chk==>"+getUser()+" , action value==>"+value.trim());
        if(value.trim().equals("新增") || value.trim().equals("修改") || value.trim().equals("刪除")) {
          if(getUser() != null && getUser().toUpperCase().equals("B9999")) {
            messagebox(value.trim()+"權限不允許!!!");
            return false;
          }
        }
        //201808check FINISH      
        // NEW
        // 回傳值為 true 表示執行接下來的資料庫異動或查詢
        // 回傳值為 false 表示接下來不執行任何指令
        // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
        // 承辦時，已簽核過不可執行修改功能
        /*
        getVoucherDepartNo  部門邏輯修正
        getDescriptionUnion   明細
        getTableDataFrom     轉傳票
        */
        getButton("ButtonHalfWidth").doClick() ;
        getButton("ButtonTable22").doClick() ;    // 通路代碼對應處理 預設值 及 金額處理
        //
        Doc2M010                       exeFun                   =  new  Doc2M010( ) ;
        FargloryUtil                      exeUtil                     =  new  FargloryUtil() ;
        put("Doc6M011_STATUS",                 "use") ;
        put("Doc6M013_STATUS",                 "use") ;
        put("Doc6M010_Table3",                   "NO") ;
        if("SYS".equals(getUser()))  {
            doSyncBarCode( ) ;
            put("Doc6M011_STATUS",   "null") ;
            put("Doc6M013_STATUS",   "null") ;
            put("Doc6M010_Table3",     "null") ;
            return  true ;
        }
        //
        String                             stringSubject           =  getFunctionName() ;
        String                             stringSend               =  "emaker@farglory.com.tw" ;
        String[]                           arrayUser                =  {"B3018@farglory.com.tw"} ;
        String                             stringBarCodeE       =  getValue("BarCode").trim( ) ;
        String                             stringBarCodeOldE  =  getValue("BarCodeOld").trim( ) ;
        String                             stringMessage          =  stringSubject+"("+value.trim()+")-----"+stringBarCodeE+"-----------"+stringBarCodeOldE+"<br>" ;
        //
        try {
            System.out.println("借款--------------------S") ;
            stringMessage+="<br>1. isBatchCheckOK" ;
            // 新增及修改時，檢核相關欄位
            if(stringSubject.indexOf("人總")==-1  &&  !isBatchCheckOK(value.trim( ),  exeFun,  exeUtil)) {
                put("Doc6M011_STATUS",   "null") ;
                put("Doc6M013_STATUS",   "null") ;
                put("Doc6M010_Table3",     "null") ;
                //
                exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+getValue("BarCode").trim( )+"' ",  true,  exeUtil) ;
                return false ;
            }
            
            stringMessage+="<br>2. isFlowCheckOK" ;
            // 流程
            if(!isFlowCheckOK(value.trim( ),  exeFun))  {
                put("Doc6M011_STATUS",   "null") ;
                put("Doc6M013_STATUS",   "null") ;
                put("Doc6M010_Table3",     "null") ;
                //
                exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+getValue("BarCode").trim( )+"' ",  true,  exeUtil) ;
                return false ;        
            }
            if("B3018".equals(getUser().toUpperCase()))  {
                put("Doc6M011_STATUS",   "null") ;
                put("Doc6M013_STATUS",   "null") ;
                put("Doc6M010_Table3",     "null") ;
                return  false ;
            }
            stringMessage+="<br>3. 公文系統同步" ;
            // 公文系統同步
            String  stringBarCode  =  getValue("BarCodeOld").trim( ); 
            if("刪除".equals(value.trim( ))) {
                doDeleteData(stringBarCode,  exeUtil,  exeFun) ;
            }
            if(!"刪除".equals(value.trim( )))     doCheckDulFactoryNo (exeFun) ;
            // BarCode 處理
             doBarCode(value.trim(),  exeFun,  exeUtil) ;
            stringMessage+="<br>4. 流程記錄" ;
            System.out.println("--------------------------流程記錄") ;
            doHistory(value.trim(),  exeFun,  exeUtil) ;
            //
            doReSetBarCode(exeUtil,  exeFun) ;
            if(!"刪除".equals(value.trim())) getButton("Button3").doClick() ;// 同步行銷
            if(",B3446,".indexOf(getUser().toUpperCase())==-1)exeUtil.ClipCopy (getValue("BarCode").trim()) ;
            System.out.println("借款--------------------E") ;
        }catch(Exception e){
            Vector  vectorUse  =  exeFun.getEmployeeNoDoc3M011("P",  "") ;
                          arrayUser  =  (String[])  vectorUse.toArray(new  String[0]) ;
            exeUtil.doEMail(stringSubject,  stringMessage+"<br>"+e.toString(),  stringSend,  arrayUser) ;
            
            messagebox("資料發生錯誤，請洽資訊室。\n"+stringMessage+"\n"+e.toString()) ;
            put("Doc6M011_STATUS",   "null") ;
            put("Doc6M013_STATUS",   "null") ;
            put("Doc6M010_Table3",     "null") ;
            //
            exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+getValue("BarCode").trim( )+"' ",  true,  exeUtil) ;
            return  false ;
        }
        put("Doc6M011_STATUS",   "null") ;
        put("Doc6M013_STATUS",   "null") ;
        put("Doc6M010_Table3",     "null") ;
        return true;
    }
    public void  doDeleteData(String  stringBarCode,  FargloryUtil  exeUtil,  Doc.Doc2M010  exeFun)throws Throwable{
          String  stringSql         =  "" ;
          String  stringComNo  =  getValue("ComNo").trim() ;
          // 公文系統同步
          stringSql  =  "DELETE  Doc1M040 WHERE  BarCode  =  '"  +  stringBarCode  +  "' "  ;
          addToTransaction(stringSql);
          stringSql  =  "DELETE  Doc1M030 WHERE  BarCode  =  '"  +  stringBarCode  +  "' AND  ComNo  =  '"+stringComNo+"' " ; 
          addToTransaction(stringSql);
          stringSql  =  exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  false,  exeUtil) ;
          addToTransaction(stringSql);
          // 行銷系統同步
          exeFun.doDeleteCiReaMM(stringBarCode) ;
          exeFun.doDeleteCoReaMM(stringBarCode) ;
          // 通知財務
          doMail(exeFun,  exeUtil) ;
    }
    // 例外-條碼編號重新設定
    public void  doReSetBarCode(FargloryUtil  exeUtil,  Doc2M010  exeFun)throws Throwable{
        String        stringBarCode        =  getValue("BarCode").trim() ;
        String        stringBarCodeOld  =  getValue("BarCodeOld").trim() ;
        Vector        vectorDoc2M040   =  new  Vector() ;
        Hashtable  hashtableAnd        =  new  Hashtable() ;
        Hashtable  hashtableTemp     =  new  Hashtable() ;
        Hashtable  hashtableData       =  new  Hashtable() ;
        boolean      booleanDB            =  !"B3018".equals(getUser()) ;
        //
        vectorDoc2M040  =  exeFun.getQueryDataHashtableDoc("Doc2M040",  hashtableAnd,  " AND  1=1 ",  new  Vector(),  exeUtil) ;
        System.out.println("vectorDoc2M040("+vectorDoc2M040.size()+")-----------------------------------------1") ;
        if(vectorDoc2M040.size()  ==  0)                    return ;
        //
        hashtableTemp  =  (Hashtable)  vectorDoc2M040.get(0) ;
        System.out.println("hashtableTemp("+(hashtableTemp==null)+")-----------------------------------------2") ;
        if(hashtableTemp  ==  null)                              return ;
        //
        System.out.println("stringBarCode("+stringBarCode+")stringBarCodeOld("+stringBarCodeOld+")-----------------------------------------3") ;
        if("".equals(stringBarCodeOld) )                    return ;
        if(stringBarCodeOld.equals(stringBarCode))  return ;
        // 日期檢核例外
        String  stringBarCodeDoc6M010  =  (""+hashtableTemp.get("BarCodeDoc6M010")).trim() ;
        String  stringSql                            =  "" ;
        System.out.println("stringBarCodeDoc6M010("+stringBarCodeDoc6M010+")-----------------------------------------4") ;
        if(!"".equals(stringBarCodeDoc6M010)  &&  !"null".equals(stringBarCodeDoc6M010)) {
            if(stringBarCodeDoc6M010.indexOf(stringBarCodeOld)  !=  -1) {
                hashtableAnd.put("BarCodeDoc6M010",   stringBarCodeDoc6M010) ;
                hashtableData.put("BarCodeDoc6M010",  stringBarCodeDoc6M010.replaceAll(stringBarCodeOld,  stringBarCode)) ;
                stringSql  =  exeFun.doUpdateDBDoc("Doc2M040",  "",  hashtableData,  hashtableAnd,  booleanDB,  exeUtil) ;
                System.out.println("stringSql("+stringSql+")-----------------------------------------4") ;
            }
        }
        // 預算例外
        String  stringBarCodeDoc3M011  =  ""+hashtableTemp.get("BarCodeDoc3M011") ;
        System.out.println("stringBarCodeDoc3M011("+stringBarCodeDoc3M011+")-----------------------------------------5") ;
        if(!"".equals(stringBarCodeDoc3M011)  &&  !"null".equals(stringBarCodeDoc3M011)) {
            if(stringBarCodeDoc3M011.indexOf(stringBarCodeOld)  !=  -1) {
                hashtableAnd.put("BarCodeDoc3M011",   stringBarCodeDoc3M011) ;
                hashtableData.put("BarCodeDoc3M011",  stringBarCodeDoc3M011.replaceAll(stringBarCodeOld,  stringBarCode)) ;
                stringSql  =  exeFun.doUpdateDBDoc("Doc2M040",  "",  hashtableData,  hashtableAnd,  booleanDB,  exeUtil) ;
                System.out.println("stringSql("+stringSql+")-----------------------------------------5") ;
            }
        }
    }
    public void doBarCode(String  stringFunction,  Doc2M010  exeFun,  FargloryUtil  exeUtil)throws Throwable{
        String  stringFlow                           =  getFunctionName() ;
        String   stringID                              =  getValue("ID").trim() ;
        String  stringBarCode                    =  "" ;
        if((stringFlow.indexOf("經辦")!=-1  ||  stringFlow.indexOf("承辦")!=-1)  &&  "新增".equals(stringFunction))   {
            stringID            =  exeFun.getMaxIDForDoc6M010( ) ;
            setValue("ID",              stringID) ;
            if(stringFlow.indexOf("--承辦")==-1  &&  stringFlow.indexOf("--經辦")==-1) {
                stringBarCode  =  exeFun.getMaxBarCode("Z") ;
                setValue("BarCode",  stringBarCode) ;
            }
        }
        doSyncBarCode( ) ;
    }
    public void doHistory(String  stringFunction,  Doc2M010  exeFun,  FargloryUtil  exeUtil)throws Throwable{
        String      stringBarCodeE         =  getValue("BarCode").trim( ) ;
        String      stringBarCodeOldE   =  getValue("BarCodeOld").trim( ) ;
        String      stringDeptCd             =  "" ;
        String      stringUser                 =  getUser().toUpperCase() ;
        String      stringDocNo              =  getValue("DocNo1").trim()+getValue("DocNo2").trim()+getValue("DocNo3").trim() ;
        String      stringDescript            =  getValue("Descript").trim() ;
        String      stringToday               =  datetime.getToday("yymmdd") ;
        String      stringID                     =  getValue("ID").trim() ;
        String[][]  retFE3D103              =  exeFun.getFE3D103(stringUser,  "",  stringToday) ;  
        //
        if(retFE3D103.length  >  0)  stringDeptCd  =  retFE3D103[0][0].trim() ;
        //
        exeFun.doInsertForDoc2M010History(stringID,                      datetime.getTime("YYYY/mm/dd h:m:s"),  stringUser,  stringDeptCd,  getFunctionName()+" "+stringFunction+"---"+stringDocNo+"---"+stringBarCodeE+"---"+stringBarCodeOldE,
                                       stringDescript,            true)  ;
    }
    // 通知財務
    public void doMail(Doc.Doc2M010  exeFun,  FargloryUtil  exeUtil)throws Throwable{
        String                             stringID             =  getValue("ID").trim() ;
        String                             stringDocNo     =  getValue("DocNo1").trim()+"-"+getValue("DocNo2").trim()+"-"+getValue("DocNo3").trim() ;
        String                             stringDescript   =  getValue("Descript").trim() ;
        String                             stringBarCode  =  getValue("BarCodeOld").trim( ) ;
        String[][]                         retDoc2M080   =  exeFun.getDoc2M080(stringID,  "借款%'    AND  Remark  NOT LIKE  '借款未沖銷%'  AND  Remark  NOT LIKE  '借款沖銷",  "",  "") ;
        if(retDoc2M080.length  >  0) {
            String  stringVoucher  =  retDoc2M080[0][23].trim() ;
                         stringVoucher  =  exeUtil.doSubstring(stringVoucher,  0,  12) ;
            //
            String    stringSubject      =  "[借款申請書] 刪除 通知" ;
            String    stringContent     =  stringSubject  +  "<br>"  +
                                                        "條碼編號：["+stringBarCode+"]<br>"  +
                                  "公文編號：["+stringDocNo+"]<br>"  +
                                  "公文內容："+stringDescript +"<br>"  +
                                  "預估傳票："+stringVoucher +"<br>"  +
                                  "此筆有 [月底預估] 或 [年底預估] 請手動作沖銷";
            String    stringSendView  =  "請款系統" ;
            String    stringSend          =  "emaker@Farglory.com.tw" ;
            String[]  arrayUser           =  null ;
            String[][]retUser               =  exeFun.getDoc3M011EmployeeNo("",  " AND  FunctionType  IN ('P',  'Y') ") ;
            Vector    vectorUser        =  new  Vector() ;
            for(int  intNo=0  ;  intNo<retUser.length  ;  intNo++) {
                vectorUser.add(retUser[intNo][0].trim()+"@farglory.com.tw") ;
            }
            arrayUser  =  (String[])  vectorUser.toArray(new  String[0]) ;
            exeUtil.doEMail(stringSubject,  stringContent,  stringSend,  arrayUser) ;
        }
    }
    public  void  doCheckDulFactoryNo (Doc.Doc2M010  exeFun) throws  Throwable{
        JTable      jtable1                        =  getTable("Table6") ;
        String      stringFactoryNo          =  getValue("FactoryNo2").trim() ;
        String      stringFactoryName     =  "" ;
        String      stringFactoryNameQ  =  "" ;
        String[]    arrayTableName        =  {"Table1",  "Table2"} ;
        String[][]  retData                       =  null ;
        //
        if(!"".equals(stringFactoryNo)) {
            stringFactoryName  =  exeFun.getFactoryName(stringFactoryNo) ;
            //
            if(stringFactoryName.length()>2) {
              stringFactoryNameQ  =  stringFactoryName.substring(0,2) ;
            } else {
              stringFactoryNameQ  =  stringFactoryName ;
            }
            //
            retData  =  exeFun.getDoc3M015And("",  " AND  OBJECT_SHORT_NAME  LIKE  '" +  stringFactoryNameQ  +  "%' ") ;
            if(retData.length  >  1) {
              JOptionPane.showMessageDialog(null,  "廠商 "+stringFactoryNo+"("+stringFactoryName+") 中文名稱，資料庫存在 "+(retData.length)+" 筆，請檢查廠商是否正確。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            }
        }
        //
        for(int  intNo=0  ;  intNo<arrayTableName.length  ;  intNo++) {
            if(getTable(arrayTableName[intNo]).getRowCount()  ==  0)  continue ;
            //
            stringFactoryNo    =  (""+getValueAt(arrayTableName[intNo],  0,  "FactoryNo")).trim() ;
            stringFactoryName  =  exeFun.getFactoryName(stringFactoryNo) ;
            //
            if(stringFactoryName.length()>2) {
                stringFactoryNameQ  =  stringFactoryName.substring(0,2) ;
            } else {
                stringFactoryNameQ  =  stringFactoryName ;
            }
            //
            retData  =  exeFun.getFED1005("",  " AND  OBJECT_SHORT_NAME  LIKE  '" +  stringFactoryNameQ  +  "%' ") ;
            if(retData.length  >  1) {
                JOptionPane.showMessageDialog(null,  "廠商 "+stringFactoryNo+"("+stringFactoryName+") 中文名稱，資料庫存在 "+(retData.length)+" 筆，請檢查廠商是否正確。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            }
            break ;
        }
    }
    // 應用
    public  void  doSyncBarCode( ) throws  Throwable {
        String   stringBarCode        =  getValue("BarCode").trim( ) ;
        String   stringBarCodeOld  =  getValue("BarCodeOld").trim( ) ;
        JTable  jtable                     =  null ;
        //
        //if(stringBarCode.equals(stringBarCodeOld))  return ;
        // 2011/05/10 特殊預算控管  4  特殊預算控管 細項   5  特殊預算控管 簽呈     6 費用
        // 9 請購項目視窗 Doc6M0171
        // 16 代銷合約備查
        // 17 請購項目-案別分攤
        // 22 費用對照通路代碼
        for(int  intTableNo=1  ;  intTableNo<=22  ;  intTableNo++) {
            if(intTableNo  ==  7)    continue ; //使用情況
            if(intTableNo  ==  8)    continue ;
            if(intTableNo  ==  10)  continue ;
            if(intTableNo  ==  11)  continue ;
            if(intTableNo  ==  12)  continue ;
            if(intTableNo  ==  13)  continue ;
            if(intTableNo  ==  14)  continue ;
            if(intTableNo  ==  15)  continue ;
            if(intTableNo  ==  18)  continue ;
            if(intTableNo  ==  19)  continue ;
            if(intTableNo  ==  20)  continue ;
            if(intTableNo  ==  21)  continue ;
            jtable  =  getTable("Table"+intTableNo) ;
            for(int  intNo=0  ;  intNo<jtable.getRowCount()  ;  intNo++) {
                setValueAt("Table"+intTableNo,  stringBarCode,  intNo,  "BarCode") ;
            }
        }
    }

    
    
    
    public  boolean  isFlowCheckOK(String  value,  Doc.Doc2M010  exeFun) throws  Throwable {
        String  stringUnderGoWrite       =  getValue("UNDERGO_WRITE").trim( ) ;
        String  stringFlow                      =  getFunctionName() ;
        String   stringDocNo1                =  getValue("DocNo1").trim( ) ;
        boolean  booleanFlowI             =  stringDocNo1.indexOf("033FZ")!=-1 ;
        //
        if(!"新增".equals(value.trim())) {
            String      stringID            =  getValue("ID").trim() ;
            String[][]  retDoc6M010  =  exeFun.getTableDataDoc("SELECT  UNDERGO_WRITE  FROM  Doc6M010  WHERE  ID  =  "+stringID+" ") ;
            if(retDoc6M010.length  ==  0) {
                message("資料發生錯誤，請洽資訊室。") ;
                return  false ;   
            }
            stringUnderGoWrite  =  retDoc6M010[0][0].trim() ;
        }
        //
        if("E".equals(stringUnderGoWrite)) {
              message("[作廢資料] 不可異動資料。") ;
              return  false ;
        }
        if(stringFlow.indexOf("簽核") !=  -1)   {
            if(!"K".equals(stringUnderGoWrite)  &&  !"B".equals(stringUnderGoWrite)  &&  !"Y".equals(stringUnderGoWrite)) {
                message("業管尚未簽核，不可執行 [修改] 功能。") ;
                return  false ;
            }
            setValue("UNDERGO_WRITE",  "Y") ;
        }
        if(stringFlow.indexOf("業管") !=  -1  ||  stringFlow.indexOf("審核") !=  -1)  {
            if("Y".equals(stringUnderGoWrite)) {
                message("已簽核過不可執行 [修改] 功能。") ;
                return  false ;
            }
            if(!booleanFlowI) {
                setValue("UNDERGO_WRITE",  "B") ;
            } else {
                setValue("UNDERGO_WRITE",  "I") ;
            }
        } 
        if(stringFlow.indexOf("經辦")!=-1  ||  stringFlow.indexOf("承辦")!=-1)  {
            if("B".equals(stringUnderGoWrite)  ||  "Y".equals(stringUnderGoWrite)) {
                if(stringFlow.indexOf("--經辦")==-1  &&  stringFlow.indexOf("--承辦")==-1) {
                    message("已簽核過不可執行 [修改] [刪除] 功能。") ;
                    return  false ;
                }
            }
            if( "刪除".equals(value.trim( ))  &&  "X".equals(stringUnderGoWrite)) {
                message("流程中，不可執行 [刪除] 功能。") ;
                return  false ;           
            }
            String  StringEmployeeNo  =  getValue("EmployeeNo").trim( ).toUpperCase() ;
            if(!"B3018".equals(getUser())  &&  !StringEmployeeNo.equals(getUser().toUpperCase())) {
                JOptionPane.showMessageDialog(null,  "由 "  + StringEmployeeNo  +  " 建立之資料，其它人不能異動處理。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            if(stringFlow.indexOf("--經辦")!=-1  ||  stringFlow.indexOf("--承辦")!=-1) {
                if(!booleanFlowI) {
                    setValue("UNDERGO_WRITE",  "B") ;
                } else {
                    setValue("UNDERGO_WRITE",  "I") ;
                }
            } else {
                if(!"".equals(stringUnderGoWrite)  &&  "I,X,".indexOf(stringUnderGoWrite)!=-1) {
                    message("不可執行 [修改] [刪除] 功能。") ;
                    return  false ;                       
                }
                setValue("UNDERGO_WRITE",  "A") ;
            }
        } 
        if(stringFlow.indexOf("人總") !=  -1)   {
            if("Y".equals(stringUnderGoWrite)) {
                messagebox("已簽核過不可執行 [修改] [刪除] 功能。") ;
                return  false ;
            } 
            if(!"I".equals(stringUnderGoWrite)) {
                messagebox("非 [人總待簽核]，不可執行 [修改] [刪除] 功能。") ;
                return  false ;
            } 
            setValue("UNDERGO_WRITE",  "K") ;
        }
        return  true ;
    }
    // 設定 DocNo
    public  boolean  isBatchCheckOK(String  value,  Doc.Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
         setValue("DocNo1",  getValue("DepartNo").trim()) ;
         // 
         String     stringFlow                      =  getFunctionName() ;
         String     stringBarCode               =  getValue("BarCode").trim( ) ;
         String     stringBarCodeOld         =  getValue("BarCodeOld").trim( ) ;
         String     stringDepartNoSubject  =  ""+get("EMP_DEPT_CD") ;
         // 代銷合約
        put("Doc7M02691_STATUS",  "DB") ;
        getButton("ButtonTable16").doClick() ;
        if(!"OK".equals(""+get("Doc7M02691_STATUS"))) {
            return  false ;
        }
        //
        getButton("ButtonKindNoD").doClick() ;
        String     stringKindNoD            =  getValue("KindNoD").trim() ;
        Vector    retVector                    =  (Vector)get("Doc5M030_KindNoDs") ;
        Vector  vectorKindNoDValue  =  (Vector)retVector.get(0) ;
        Vector  vectorKindNoDView    =  (Vector)retVector.get(1) ;
        setReference("KindNoD",  vectorKindNoDView,  vectorKindNoDValue) ;
        if(vectorKindNoDValue.indexOf(stringKindNoD)  ==  -1) {
            messagebox("[公文類別] 錯誤。") ;
            getcLabel("KindNoD").requestFocus() ;
            return  false ;
        }
        // 依公文類別 KindNoD，修正預定結案日期
        String  stringKindDay      =  exeFun.getKindDay(stringKindNoD) ;
        String  stringCDate          =  getValue("CDate").trim().replaceAll("/",  "") ;
        String   stringPreFinDate =  datetime.dateAdd(stringCDate,  "d",  exeUtil.doParseInteger(stringKindDay)) ;
        setValue("PreFinDate",  exeUtil.getDateConvertRoc(stringPreFinDate)) ;        
        
        // 公司代碼 
        if(!isComNoOK(exeFun, exeUtil)) return  false ;
        // 借款單-公文編號 
        if(!isDocNoOK(stringFlow,  value,  stringDepartNoSubject,  exeFun, exeUtil)) return  false ;
        // 借款單-請購編號 
        if(!isPurchaseNoOK(exeFun, exeUtil)) return  false ;
        // 是否已傳票檢核 
        if(!isVoucherOK(exeFun, exeUtil)) return  false ;
        // 條碼編號處理
        stringBarCode  =  isBarCodeOK(stringFlow,  value,  exeFun, exeUtil) ;
        if(stringBarCode.startsWith("ERROR")) return  false ;
        // 承辦人員 
        if(!isOriEmployeeNoOK(stringFlow,  exeFun, exeUtil)) return  false ;
        // 日期關係檢核
        if(!isDateCheckOK(value,  stringFlow,  exeFun, exeUtil)) return  false ;
         // 特殊預算控管 限制
         if(!is033FGOK(exeFun, exeUtil)) return  false ;
         // 其它欄位檢核
         if(!isOtherFieldOK(exeFun, exeUtil)) return  false ;
        // 廠商 
        if(!isFactoryNoCheckOK(stringFlow,  exeUtil,  exeFun))  return  false ;
        // Table1 發票
        if(!isTable1CheckOK(exeFun,  exeUtil))  return  false ;
        // Table2 扣繳
         if(!isTable2CheckOK(exeUtil,  exeFun))  return  false ;
        // Table3  酬庸
        if(!isTable3CheckOK(exeUtil,  exeFun))  return  false ;
        // 費用表格 oce
        if(!isTable6CheckOK(exeUtil,  exeFun))  return  false ;
        // 報銷方式及金額
        System.out.println("報銷方式及金額-------------------------------------------S") ;
        if(!isMoneyCheckOK(exeFun, exeUtil)) return  false ;
        System.out.println("報銷方式及金額-------------------------------------------E") ;
         // 2013-09-04 固資檢核
         if(!isAssetOK(exeFun,  exeUtil))     return  false ;
        //
         setValue("LastEmployeeNo",  getUser()) ;
         if(!"".equals(stringBarCode))  setValue("BarCode",  stringBarCode) ;
        return  true ;
    }
    public  boolean  is033FGOK(Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
         JTable    jtable4                     =  getTable("Table4") ;
         String     stringComNo           =  getValue("ComNo").trim( ) ;
         String     stringDocNo1          =  getValue("DocNo1").trim( ) ;
         String     stringEmployeeNo  =  getValue("EmployeeNo").trim() ;
         String     stringFunctionType =  exeFun.get033FGFunctionType (stringDocNo1,  exeUtil) ;
         String     stringSpecBudget    =  ","+get("SPEC_BUDGET")+"," ;
         String     stringSql                 =  "SELECT  EmployeeNo "  +
                                                             " FROM  Doc3M011_EmployeeNo "  +
                                 " WHERE  FunctionType  =  '"+stringFunctionType+"' "  +
                                       " AND  EmployeeNo  =  '"+stringEmployeeNo+"' " ;
        boolean   booleanPurchaseExist  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false表示可以無請購單  
        if(stringSpecBudget.indexOf(stringDocNo1)==-1) {
            setTableData("Table4",  new  String[0][0]) ;
            setTableData("Table5",  new  String[0][0]) ;  
            return  true ;
        }
        if(!"Z6".equals(stringComNo)) {
            messagebox("[殊預算控管] 僅允許 ["+get("Z6")+"] 申請。") ;
            return  false ;
        }
         if(exeFun.getTableDataDoc(stringSql).length  <=  0) {
            messagebox("非特殊人員不允許申請 "+stringDocNo1+" 費用。\n(有問題請洽 [行銷企劃室])") ;
            return  false ;
         }
         if(booleanPurchaseExist)  return  true ;
         //
         if(jtable4.getRowCount()==0) {
              getButton("Button033FGInput").doClick() ;
         }
        getButton("Button033FG").doClick() ;
         String[][]  retTableData  =  getTableData("TableCheck") ;
        if(retTableData.length==0) {
            messagebox("資料發生錯誤，請洽資訊室。") ;
            return  false ; 
        }
        if(retTableData.length==1  &&  "OK".equals(retTableData[0][0])){
            return  true ;
        }
        return  false ; 
    }
    public  boolean  isComNoOK(Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        String     stringComNo  =  getValue("ComNo").trim( ) ;
        if("".equals(stringComNo)) {
            messagebox("[公司代碼] 不可為空白。") ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;       
        }
        // 2013-06-19 由 公司型態(Doc7M056) 控管是否可使用
        String[][]  retDoc7M056  =  exeFun.getDoc7M056(stringComNo,  "",  "",  "",  "") ;
        if(retDoc7M056.length  ==  0) {
            messagebox("公司 "  +  stringComNo  +  "("+exeFun.getCompanyName(stringComNo)+") 不允許使用。\n(有問題請洽 [財務室])") ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;               
        }
        String  stringUseType      =  retDoc7M056[0][4].trim() ;
        String  strinComNoType  =  retDoc7M056[0][2].trim() ;
        if(!"A".equals(stringUseType)) {
            messagebox("公司 "  +  stringComNo  +  "("+exeFun.getCompanyName(stringComNo)+") 不允許使用。\n(有問題請洽 [財務室])") ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;       
        }
        return  true ;
    }
    public  boolean  isDocNoOK(String  stringFlow,  String  stringFunction,  String  stringDepartNoSubject,  Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        // 公文編號之日期檢核
        String     stringBarCode        =  getValue("BarCode").trim( ) ;
        String     stringBarCodeOld  =  getValue("BarCodeOld").trim( ) ;
        String     stringComNo          =  getValue("ComNo").trim( ) ;
        String     stringDocNo1         =  getValue("DocNo1").trim( ) ;
        String     stringDocNo2         =  getValue("DocNo2").trim( ) ;
        String     stringDocNo3         =  getValue("DocNo3").trim( ) ;
        String     stringKindNo          =  getValue("KindNo").trim( ) ;
        String     retDateRoc            =  exeUtil.getDateFullRoc (stringDocNo2+"01",  "12345678") ;
        // 部門代碼不存在 DepartNo
        String  stringDepartNo  =  getValue("DepartNo").trim( ) ;
        if("".equals(stringDepartNo)) {
            messagebox("[部門代碼] 不可為空白。") ;
            getcLabel("DepartNo").requestFocus( ) ;
            return  false ;
        }
        String  stringDepartName  =  exeFun.getDepartName(stringDepartNo) ;
        if("".equals(stringDepartName)) {
            messagebox("[部門代碼] 不存在資料庫中。\n(有問題請洽 [資訊企劃室])") ;
            getcLabel("DepartNo").requestFocus( ) ;
            return  false ;
        }
        if("033H39,0333H39,0333H42,0333H42A,033H42,033H42A,033H42B,".indexOf(stringDepartNo+",")  !=  -1) {
            messagebox("[部門代碼]("+stringDepartNo+") 不允許使用\n有問題請洽業管。") ;//徐玉珊
            getcLabel("DepartNo").requestFocus( ) ;
            return  false ;
        }
        if(retDateRoc.length( )  !=  9) {
            messagebox("[公文編號2] 格式錯誤(yymm)。") ;
            getcLabel("DocNo2").requestFocus( ) ;
            return  false ;
        }
        if( "新增".equals(stringFunction)  &&  "0231,".indexOf(stringDepartNoSubject+",")!=-1) {
            // 自動給號
            stringDocNo3  =  exeFun.getDocNo3Max(stringComNo,  stringKindNo,  stringDocNo1,  stringDocNo2,  stringDocNo1.startsWith("023")?"B":"A") ;
            setValue("DocNo3",  stringDocNo3) ;
        } else {
            if("".equals(stringDocNo3)) {
                messagebox("[公文編號3] 不可為空白。") ;
                getcLabel("DocNo3").requestFocus( ) ;
                return  false ;
            }
            if(!exeFun.isExistDocNoCheck(stringDocNo1,  stringDocNo2,  stringDocNo3,  stringKindNo,  stringComNo,  stringBarCodeOld)) {
                messagebox("[公文代碼] 重覆！ "  +  stringDocNo1  +  "-"  +  stringDocNo2  +  "-"  +  stringDocNo3) ;
                getcLabel("DocNo3").requestFocus( ) ;
                return  false ;
            }
        }
        setValue("DocNo",  stringDocNo1  +  stringDocNo2  +  stringDocNo3) ;
        //
        String      stringDocNoOld  =  getValue("DocNoOld").trim( ) ;
        String[][]  retDoc1M030     =  exeFun.getDoc1M030(stringBarCodeOld) ;
        String[][]  retDoc1M040     =  exeFun.getDoc1M040(stringBarCodeOld) ;
         if(retDoc1M030.length>0  &&  "5".equals(retDoc1M030[0][6].trim())) {
            messagebox("公文追蹤系統中，此請購單已 [作廢]，不允許執行。") ;
            return  false ;
         }
        if(retDoc1M030.length  >  0) {
            if(!stringKindNo.equals(retDoc1M030[0][5].trim())) {
                messagebox("[公文類別] 不一致，請洽 [資訊企劃室] 處理。 ") ;
                return  false ;
            }
            if(!stringDocNoOld.equals(retDoc1M030[0][2].trim()+retDoc1M030[0][3].trim()+retDoc1M030[0][4].trim())) {
                messagebox("[公文代碼] 不一致，請洽 [資訊企劃室] 處理。 ") ;
                return  false ;
            }
        }
        if("刪除".equals(stringFunction)) {
            if(retDoc1M040.length  >  1) {
                messagebox("借款單已有收發文，不允許刪除，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            if(retDoc1M040.length==1  &&  !"1".equals(retDoc1M040[0][6].trim())) {
                messagebox("借款單非創文，不允許刪除，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            return  true ;
        }
        if(!"".equals(stringBarCode)  &&  !"".equals(stringBarCodeOld)  &&  !stringBarCode.equals(stringBarCodeOld)) {
            if(stringFlow.indexOf("經辦")!=-1  ||  stringFlow.indexOf("承辦")!=-1) {
                messagebox("借款單不允許變更條碼編號，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            if(retDoc1M040.length  >  1) {
                messagebox("借款單已有收發文，不允許變更條碼編號，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            if(retDoc1M040.length==1  &&  !"1".equals(retDoc1M040[0][6].trim())) {
                messagebox("借款單非創文，不允許變更條碼編號，請洽 [資訊企劃室]。") ;
                return  false ;
            }
        }
        return  true ;
    }
    public  boolean  isPurchaseNoOK(Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        String      stringPurchaseNoExist          =  getValue("PurchaseNoExist").trim() ;
        String     stringComNo                          =  getValue("ComNo").trim( ) ;
        String      stringPurchaseNo1               =  getValue("PurchaseNo1").trim() ;
        String      stringPurchaseNo2               =  getValue("PurchaseNo2").trim() ;
        String      stringPurchaseNo3               =  getValue("PurchaseNo3").trim() ;
        String      stringTemp                             =  "" ;
        String[][]  retDoc3M011                         =  null ;
        if("N".equals(stringPurchaseNoExist)) {
            setValue("PurchaseNo1",     "") ;
            setValue("PurchaseNo2",     "") ;
            setValue("PurchaseNo3",     "") ;
            setValue("PurchaseNo",      "") ;
            setValue("OptometryNo",       "") ;
            setValue("OptometryNo1",    "") ;
            setValue("OptometryNo2",    "") ;
            setValue("OptometryNo3",    "") ;
            return  true ;
        }
        if("".equals(stringPurchaseNo1)  ||  "".equals(stringPurchaseNo2)  ||  "".equals(stringPurchaseNo3)) {
            JOptionPane.showMessageDialog(null,  "[請購單號] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("PurchaseNo3").requestFocus( ) ;
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            return  false ;       
        }
        setValue("PurchaseNo",  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3);
        //
        retDoc3M011                       =  exeFun.getDoc3M011(stringComNo,  stringPurchaseNo1,  stringPurchaseNo2,  stringPurchaseNo3,  " AND  UNDERGO_WRITE  =  'Y' ") ;
        if(retDoc3M011.length  ==  0) {
            retDoc3M011  =  exeFun.getDoc3M011(stringComNo,  stringPurchaseNo1,  stringPurchaseNo2,  stringPurchaseNo3,  "") ;
            if(retDoc3M011.length  ==  0) {
                stringTemp  =  "[請購單號] 不存在資料庫中。" ;
            } else {
                stringTemp     =  exeFun.getPurchseUndergoWriteName(retDoc3M011[0][15].trim()) ;
                stringTemp     =  "[請購單號] 處於 ["+stringTemp+"] 狀態，尚未完成請購流程。\n(有問題請洽 [採購室])" ;
            }
            messagebox(stringTemp) ;
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            return  false ;
        }
        String      stringBarCodePur          =  retDoc3M011[0][12].trim() ;
        String      stringGroupID                 =  getGroupID(stringBarCodePur,  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  exeUtil,  exeFun) ;  
        String      stringFactoryNo             =  getValue("FactoryNo2").trim() ;  if("".equals(stringFactoryNo))  return  true ;
        String[][]  retDoc3M013                 =  exeFun.getDoc3M013Union(stringComNo,  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  stringFactoryNo,  true) ;
        boolean  booleanSpecPurchaseNo   =  exeFun.isSpectPurchaseNo (stringComNo,  "17",  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  " AND  M13.GroupName  LIKE  '%#-#B' ") ;
        if(retDoc3M013.length  ==  0) {
            messagebox("[請購單號] 不存在此 [對象]。") ;
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            return  false ;
        }
        if("P93417".equals(stringBarCodePur)) {
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            messagebox("特殊請購單不允許使用。") ;
            return  false ;
        }
        // 付款條件及金額檢核
        String   stringBarCodeOld                   =  getValue("BarCodeOld").trim( ) ;
        double  doublePurchaseMoney           =  getContractMoney (stringBarCodePur,  stringFactoryNo,  stringGroupID,  exeUtil,  exeFun) ;
        double  doubleExistPurchaseMoney    =  getPaidUpMoney(stringPurchaseNo1,            stringPurchaseNo2,    stringPurchaseNo3,  
                                                        stringFactoryNo,                  stringBarCodePur,       stringGroupID,
                                                        booleanSpecPurchaseNo,       exeUtil,                 exeFun) ;
        double  doubleRealMoneySumL         =  exeUtil.doParseDouble(getValue("ThisPurchaseMoney").trim());
        if(doublePurchaseMoney  <  doubleExistPurchaseMoney+doubleRealMoneySumL) {
            messagebox("[請購金額] + [已請購金額] 大於 [合約金額]。") ;
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            return  false ;
        }
        //　驗收單號
        String      stringOptometryNo1      =  getValue("OptometryNo1") ;
        String      stringOptometryNo2      =  getValue("OptometryNo2") ;
        String      stringOptometryNo3      =  getValue("OptometryNo3") ;
        String      stringOptometryVersion  =  getValue("OptometryVersion") ;
        Hashtable  hashtableCondition       =  new  Hashtable() ;
        if("B".equals(stringOptometryVersion)  &&  exeFun.isExistOptometryNo(stringComNo,  stringOptometryNo1,  "",  stringOptometryNo2,  stringOptometryNo3,  stringBarCodeOld,  "",  hashtableCondition)) {
            setValue("OptometryNo1",    "") ;
            setValue("OptometryNo2",    "") ;
            setValue("OptometryNo3",    "") ;
        }
        return  true ;
    }
    public  String  getGroupID (String  stringBarCodePur,  String  stringPurchaseNo,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
        JTable      jtable9                 =  getTable("Table9") ;
        String        stringBarCode       =  "" ;
        String        stringComNo         =  getValue("ComNo").trim() ;
        String        stringGroupID       =  "" ;
        String        stringPurchaseNoL   =  "" ;
        String        stringRecordNo12L   =  "" ;
        Hashtable  hashtableAnd           =  new  Hashtable() ;
        //
        for(int  intNo=0  ;  intNo<jtable9.getRowCount()  ;  intNo++) {
            stringPurchaseNoL    =  (""+getValueAt("Table9",  intNo,  "PurchaseNo")).trim() ;
            stringRecordNo12L    =  (""+getValueAt("Table9",  intNo,  "RecordNo12")).trim() ;
            //
            if(!stringPurchaseNo.equals(stringPurchaseNoL))  continue ;
            //
            hashtableAnd.put("BarCode",       stringBarCodePur) ;
            hashtableAnd.put("RecordNo",     stringRecordNo12L) ;
            stringGroupID  =  exeFun.getNameUnionDoc("GroupID",  "Doc3M012",  "",  hashtableAnd,  exeUtil) ;
            return  stringGroupID ;
        }
        //
        return  "" ;
    }
    public  double  getContractMoney (String  stringBarCodePur,  String  stringFactoryNo,  String  stringGroupID,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
        JTable        jtable9                                   =  getTable("Table9") ;
        double      doublePurchaseMoneyDB     =  0 ;
        String      stringContractMoney         =  "" ;
        String      stringGroupIDL                  =  "" ;
        String      stringGroupName             =  "" ;
        String[]    arrayGroupName            =  null ;
        Vector      vectorDoc3M013            =  new  Vector() ;
        Hashtable  hashtableAnd                 =  new  Hashtable() ;
        Hashtable  hashtableDoc3M013        =  new  Hashtable() ;
        //
        hashtableAnd.put("BarCode",  stringBarCodePur) ;
        hashtableAnd.put("FactoryNo",  stringFactoryNo) ;
        vectorDoc3M013  =  exeFun.getQueryDataHashtableDoc("Doc3M013",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
        for(int  intNo=0  ;  intNo<vectorDoc3M013.size()  ;  intNo++) {
            hashtableDoc3M013         =  (Hashtable)vectorDoc3M013.get(intNo) ;
            stringContractMoney         =  ""+hashtableDoc3M013.get("PurchaseSumMoney") ;
            stringGroupName           =  (""+hashtableDoc3M013.get("GroupName")).trim() ;
            stringGroupIDL            =  (""+hashtableDoc3M013.get("GroupID")).trim() ;
            //
            arrayGroupName  =  convert.StringToken(stringGroupName,  "#-#") ;
            //
            if(arrayGroupName.length==4  &&  jtable9.getRowCount()>0) {
                if(stringGroupID.equals(stringGroupIDL))  return  exeUtil.doParseDouble(stringContractMoney) ;
            } else {
                return  exeUtil.doParseDouble(stringContractMoney) ;
            }
        }
        return  doublePurchaseMoneyDB ;
    }
    public  double  getPaidUpMoney(String     stringPurchaseNo1,        String      stringPurchaseNo2,    String      stringPurchaseNo3,  
                                 String     stringFactoryNo,                String         stringBarCodePur,       String       stringGroupID,
                                 boolean     booleanSpecPurchaseNo,  FargloryUtil  exeUtil,             Doc2M010   exeFun) throws  Throwable {
        String        stringBarCode                      =  getValue("BarCodeOld").trim( ) ;                                 
        String        stringComNo                    =  getValue("ComNo").trim() ; 
        Hashtable  hashtableData               =  new  Hashtable() ;
        //
        hashtableData.put("ComNo",           stringComNo) ;
        hashtableData.put("PurchaseNo1",  stringPurchaseNo1) ;
        hashtableData.put("PurchaseNo2",  stringPurchaseNo2) ;
        hashtableData.put("PurchaseNo3",  stringPurchaseNo3) ;
        hashtableData.put("FactoryNo",      stringFactoryNo) ;
        hashtableData.put("EDateTime",      "") ;
        hashtableData.put("GroupID",          stringGroupID) ;
        hashtableData.put("BarCode",       stringBarCode) ;
        hashtableData.put("BarCodePur",  stringBarCodePur) ;
        hashtableData.put("UseType",        "B") ;    // A 前期   B 已使用
        //
        return  exeFun.getPaidUpMoney(hashtableData,  exeUtil) ;
    }
    public  boolean  isVoucherOK(Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        String     stringBarCodeOld  =  getValue("BarCodeOld").trim( ) ;
        String     stringStatus           =  exeFun.getStatusForDoc2M014(stringBarCodeOld) ;
        if(("E".equals(stringStatus))  ||  ("Z".equals(stringStatus))) {
            String[][]  retTable                  =  exeFun.getDoc2M014(stringBarCodeOld) ;
            String      stringVoucherYMD  =  retTable[0][4].trim() ;
            //
            stringVoucherYMD  =  convert.replace(stringVoucherYMD,  "/",  "") ;
            stringVoucherYMD  =  convert.ac2roc(stringVoucherYMD) ;
            retTable                  =  exeFun.getFED1012(stringVoucherYMD,  retTable[0][5].trim(),  retTable[0][7].trim(),  retTable[0][8].trim()) ;
            if(retTable.length  >  0) {
                messagebox(" 已開立傳票不可修改。") ;
                if(!"B3018".equals(getUser()))
                return  false ;
            }
        }
        return  true ;
    }

    public  String  isBarCodeOK(String  stringFlow,  String  stringFunction,  Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        String     stringBarCode          =  getValue("BarCode").trim( ) ;
        String     stringBarCodeOld    =  getValue("BarCodeOld").trim( ) ;
        String     stringComNo             =  getValue("ComNo").trim( ) ;
        
        String     stringDocNo1  =  getValue("DocNo1").trim( ) ;
        if(stringFlow.indexOf("--") ==  -1  &&  (stringFlow.indexOf("經辦")!=-1  ||  stringFlow.indexOf("承辦")!=-1))   {
            // 行銷--承辦 自動取 Z 開頭的條碼編號
        } else {
            if("新增".equals(stringFunction)) {
                // 自動取值
                String     stringTemp     =  exeFun.getAutoBarCode(stringComNo,  stringDocNo1,  exeUtil) ;
                if(!"".equals(stringTemp)) {
                    stringBarCode  =  stringTemp ;
                }
            }
            if("".equals(stringBarCode)) {
                String  stringTemp      =  "" ;
                Vector  vectorDoc2M044   =  exeFun.getQueryDataHashtableDoc("Doc2M044",  new  Hashtable(),  " AND  DEPT_CD_NEW  LIKE   '"+exeUtil.doSubstring(stringDocNo1,  0,  3)+"%' ",  new  Vector(),  exeUtil) ;
                if(vectorDoc2M044.size()  ==  0) {
                    stringTemp  =  "[條碼編號] 不可為空白。" ;
                } else {
                    stringTemp  =  "自動取號 之 [條碼編號] 已使用完。" ;
                }
                messagebox(stringTemp) ;
                getcLabel("BarCode").requestFocus( ) ;
                return  "ERROR" ;
            } else {
                Vector  vectorDoc2M044   =  !"新增".equals(stringFunction) ? new  Vector() : exeFun.getQueryDataHashtableDoc("Doc2M044",  new  Hashtable(),  " AND  DEPT_CD  LIKE   '"+exeUtil.doSubstring(stringDocNo1,  0,  3)+"%' ",  new  Vector(),  exeUtil) ;
                if(vectorDoc2M044.size()  > 0) {
                    Hashtable  hashtableData  =  new  Hashtable() ;
                    hashtableData.put("BarCode",          stringBarCode) ;
                    hashtableData.put("EDateTime",        datetime.getTime("YYYY/mm/dd h:m:s")) ;
                    hashtableData.put("LastEmployeeNo",   getUser()) ;
                    hashtableData.put("Descript",         "行銷-借款") ;
                    exeFun.doInsertDBDoc("Doc2M044_AutoBarCode",  hashtableData,  true,  exeUtil) ;
                    //
                    setValue("BarCode",  stringBarCode) ;
                }
            }
            // 判斷 Barcode ： (A ~ Z) 00001 ~ 99999
            String  stringStatus  =  exeFun.getBarCodeKindCheck(stringBarCode,  exeUtil) ;
            if(!"OK".equals(stringStatus)) {
                messagebox(stringStatus+"_") ;
                getcLabel("BarCode").requestFocus( ) ;
                return  "ERROR" ;   
            }
            /*if(!"".equals(stringBarCode)  &&  exeFun.getBarCodeFirstChar( ).indexOf(stringBarCode.substring(0,1))==-1) {
                  messagebox("[條碼編號] 不正確，請重新輸入。") ;
                  return  "ERROR" ;
            }
            char        charBarCodeFirst  =  stringBarCode.charAt(0) ;
            double    doubleBarCode     =  exeUtil.doParseDouble(stringBarCode.substring(1)) ;
            boolean  booleanJudge       =  stringBarCode.length( )  ==  6  &&   
                                      Character.isLetter(charBarCodeFirst)  &&
                                      (doubleBarCode >= 1  &&  doubleBarCode <=  99999) ;
            if(!booleanJudge) {
                messagebox("[條碼編號] 格式錯誤。") ;
                getcLabel("BarCode").requestFocus( ) ;
                return  "ERROR" ;
            }*/
            // 存在檢核
            if(!stringBarCodeOld.equals(stringBarCode)) {             
                if(!exeFun.isExistBarCodeCheck(stringBarCode)) {
                    messagebox("[條碼編號] 已存在資料庫中，請執行 [查詢] 後，作修改。") ;
                    getcLabel("BarCode").requestFocus( ) ;
                    return  "ERROR" ;
                }
            }
        }
        return  stringBarCode  ;
    }
    public  boolean  isOriEmployeeNoOK(String  stringFlow,  Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        String  stringOriEmployeeNo  =  getValue("OriEmployeeNo").trim( ) ;
        if("".equals(stringOriEmployeeNo)) {
            JOptionPane.showMessageDialog(null,  "[承辦人員] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("OriEmployeeNo").requestFocus( ) ;
            return  false ; 
        }
        String  stringEmpName  =  exeFun.getEmpName(stringOriEmployeeNo) ;
        if("".equals(stringEmpName)) {
            JOptionPane.showMessageDialog(null,  "[承辦人員] 不存在資料庫中。\n(有問題請洽 [人總室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("OriEmployeeNo").requestFocus( ) ;
            return  false ; 
        }
        String     stringComNo      =  getValue("ComNo").trim( ) ;
        String     stringDepartNo  =  getValue("DepartNo").trim( ) ;
        if(",01,12,".indexOf(stringComNo)  !=  -1) {
            String  stringDoc3M011EmployeeNo  =  exeFun.getTableDataDoc("SELECT  EmployeeNo  FROM  Doc3M011_EmployeeNo  WHERE  FunctionType = 'W' ")[0][0].trim() ;
            if(stringDepartNo.startsWith("033") ||  stringDepartNo.startsWith("053") ||  stringDepartNo.startsWith("133")) {
                if(!stringDoc3M011EmployeeNo.equals(stringOriEmployeeNo)) {
                    stringOriEmployeeNo  =  stringDoc3M011EmployeeNo ;
                    setValue("OriEmployeeNo",  stringDoc3M011EmployeeNo) ;
                }
            }
            // 特別控管公司
            String  stringComNoCF  =  exeFun.getComNoForEmpNo(stringOriEmployeeNo) ;
            if(stringFlow.indexOf("簽核")== -1  &&  !stringComNo.equals(stringComNoCF)) {
                messagebox("[承辦人員] 投保公司為 ["+exeFun.getCompanyName(stringComNoCF)+"] 非 ["+exeFun.getCompanyName(stringComNo)+"]，不允許異動。") ;
                getcLabel("OriEmployeeNo").requestFocus( ) ;
                return  false ; 
            }
        }
        //   0  DEPT_CD     1  EMP_NO       2  EMP_NAME
        if("A0241,".indexOf(getUser())==-1  &&  !stringDepartNo.startsWith("053")  &&  stringFlow.indexOf("簽核")  ==  -1  &&  "Z6".equals(stringComNo)) {
            String[][]  retFE3D05  =  exeFun.getFE3D05(stringOriEmployeeNo) ;
            if(retFE3D05.length  == 0)  return  false ;
            //
            Hashtable  hashtableData  =  new  Hashtable() ;
            hashtableData.put("DEPT_CD_USER",  retFE3D05[0][0].trim()) ;
            hashtableData.put("DEPT_CD",          stringDepartNo) ;
            hashtableData.put("EmployeeNo",     getValue("EmployeeNo").trim()) ;
            String  stringErr  =  exeFun.getDocNoUserCheckErr (hashtableData,  exeUtil) ;
            if(!"".equals(stringErr)) {
                messagebox(stringErr) ;
                getcLabel("DepartNo").requestFocus( ) ;
                return  false ;
            }
            //
            /*if(retFE3D05[0][0].indexOf("0333")  !=  -1) {
                if( !(stringDepartNo.indexOf("0333")!=-1  || stringDepartNo.startsWith("0533")  ||  stringDepartNo.startsWith("1333"))  &&  
                  "033FG,033FZ,033MP,".indexOf(stringDepartNo)==-1 ) {
                    messagebox("企劃 之 [部門代碼] 須為企劃類案別。\n(有問題請洽 [行銷管理室])") ;
                    getcLabel("DepartNo").requestFocus( ) ;
                    return  false ;
                }
            } else if(retFE3D05[0][0].indexOf("033")!=-1  ||  retFE3D05[0][0].indexOf("133")!=-1) {
                if(stringDepartNo.indexOf("0333")!=-1  ||  stringDepartNo.startsWith("0533")  ||  stringDepartNo.startsWith("1333")) {
                    messagebox("行銷 之 [部門代碼] 不可使用企劃類案別。\n(有問題請洽 [行銷管理室])") ;
                    getcLabel("DepartNo").requestFocus( ) ;
                    return  false ;
                }
            }*/
        }
        return  true ;
    }
    //
    public  boolean  isDateCheckOK(String  stringValue,  String  stringFlow,  Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        if("刪除".equals(stringValue))  return  true ;
        // 公文預定結案日期
        String  stringCDate          =  getValue("CDate").trim(); 
        //String  stringPreFinDate  =  "107/09/27";
        String  stringPreFinDate  =  getValue("PreFinDate").trim( ) ;
        System.out.println(">>>PreFinDate>>>" + stringPreFinDate);
        if("".equals(stringPreFinDate)) {
            messagebox("[公文預定結案日期] 不可為空白。") ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ; 
        }
        String  retDateRoc  =  exeUtil.getDateFullRoc (stringPreFinDate,  "公文預定結案日期") ;
        if(retDateRoc.length( )  !=  9) {
            messagebox(retDateRoc) ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ;
        }
        stringPreFinDate  =  retDateRoc ;
        setValue("PreFinDate",  stringPreFinDate) ;
        // 輸入日期 < 公文預定結案日期
        if(stringPreFinDate.compareTo(stringCDate)<=0) {
            messagebox("[公文預定結案日期][輸入日期] 日期順序錯誤。") ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ;
        }
        // 需用日期
        String  stringNeedDate  =  getValue("NeedDate").trim( ) ;
        if("".equals(stringNeedDate)) {
            messagebox("[需用日期] 不可為空白。") ;
            getcLabel("NeedDate").requestFocus( ) ;
            return  false ; 
        }
        retDateRoc  =  exeUtil.getDateFullRoc (stringNeedDate,  "需用日期") ;
        if(retDateRoc.length( )  !=  9) {
            messagebox(retDateRoc) ;
            getcLabel("NeedDate").requestFocus( ) ;
            return  false ;
        }
        stringNeedDate  =  retDateRoc ;
        setValue("NeedDate",  stringNeedDate) ;
        //
        String  stringBarCodeOld       = getValue("BarCodeOld").trim() ;
        String  stringBarCodeExcept  =  exeFun.getDoc2M040( )[11] ;
        String  stringToday                =  datetime.getToday("yy/mm/dd") ;  
        boolean  booleanCheck        =  "".equals(stringBarCodeOld)  ||  stringBarCodeExcept.indexOf(stringBarCodeOld)==-1 ;
        if(booleanCheck  &&  stringFlow.indexOf("簽核")==-1  &&  stringNeedDate.compareTo(stringToday)  <=0) {
            messagebox("[需用日期] 須大於 [今日]。") ;
            getcLabel("NeedDate").requestFocus( ) ;
            return  false ;
        }
        // 預定報銷日期
        String  stringDestineExpenseDate  =  getValue("DestineExpenseDate").trim( ) ;
        if("".equals(stringDestineExpenseDate)) {
            messagebox("[預定報銷日期] 不可為空白。") ;
            getcLabel("DestineExpenseDate").requestFocus( ) ;
            return  false ; 
        }
        retDateRoc  =  exeUtil.getDateFullRoc (stringDestineExpenseDate,  "預定報銷日期") ;
        if(retDateRoc.length( )  !=  9) {
            messagebox(retDateRoc) ;
            getcLabel("DestineExpenseDate").requestFocus( ) ;
            return  false ;
        }
        stringDestineExpenseDate  =  retDateRoc ;
        setValue("DestineExpenseDate",  retDateRoc) ;
        // 輸入日期  <  預定報銷日期
        if(stringDestineExpenseDate.compareTo(stringCDate)<=0) {
            messagebox("[預定報銷日期][輸入日期] 日期順序錯誤。") ;
            getcLabel("DestineExpenseDate").requestFocus( ) ;
            return  false ;
        }
        if(stringFlow.indexOf("簽核")==-1) {
            // 需用日期 stringNeedDate <  預定結案日期 stringPreFinDate  < 預定報銷日期 stringDestineExpenseDate

            System.out.println("stringNeedDate>>>" + stringNeedDate) ;
            System.out.println("stringPreFinDate>>>" + stringPreFinDate) ;
            System.out.println("stringDestineExpenseDate>>>" + stringDestineExpenseDate) ;
            
            if(booleanCheck  &&  stringPreFinDate.compareTo(stringNeedDate)<0) {
                messagebox("[預定結案日期][需用日期] 日期順序錯誤。") ;
                getcLabel("DestineExpenseDate").requestFocus( ) ;
                return  false ;
            }
            if(stringDestineExpenseDate.compareTo(stringPreFinDate)<0) {
                messagebox("[預定報銷日期][預定結案日期] 日期順序錯誤。") ;
                getcLabel("DestineExpenseDate").requestFocus( ) ;
                return  false ;
            }
        }
        return  true ;
    }
    public  boolean  isOtherFieldOK(Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        // 公文內容
        String  stringDescript  =  getValue("Descript").trim() ;
        if("".equals(stringDescript)) {
            messagebox("[公文內容] 不可為空白。") ;
            getcLabel("Descript").requestFocus( ) ;
            return  false ;
        }
        /*String  stringPayCondition1  =  getValue("PayCondition1").trim( ) ;
        String  stringPayCondition2  =  getValue("PayCondition2").trim( ) ;
        if("".equals(stringPayCondition1)  ||  "999".equals(stringPayCondition1)) {
            messagebox("[付款條件1] 不可為無。") ;
            return  false ;
        }
        if("".equals(stringPayCondition2))  setValue("PayCondition2",  "999") ;*/
        //
        return  true ;
    }
    public  boolean  isFactoryNoCheckOK(String  stringFlow,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        // 統一編號
        String  stringFactoryNo  =  getValue("FactoryNo2").trim() ;
        String  stringPayType    =  getValue("PayType").trim() ;
        String  stringDocNo        =  getValue("DocNo").trim() ;
        // 統一編號
        if("".equals(stringFactoryNo)) {
            messagebox("[統一編號] 不可為空白。1") ;
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        String  stringFactoryNoByte  =  code.StrToByte(stringFactoryNo) ;
        if(stringFactoryNo.length( ) != 8  &&  stringFactoryNo.length( ) != 10) {
            // 2015-03-09  B4197 申請 2014聯歡晚會機會中獎稅(3月薪資代扣後還款) 時 作例外
            if(",Z0001,Z0776,Z0007,".indexOf(stringFactoryNo)  ==  -1) {
                String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                    messagebox("[統一編號] 格式錯誤。1") ;
                    //getcLabel("FactoryNo").requestFocus( ) ;
                    return  false ;
                }
            }
        }
        if(stringFactoryNo.length( ) == 8  &&  !check.isCoId(stringFactoryNo)) {
            String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
            if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                messagebox("[統一編號] 格式錯誤。2") ;
                getcLabel("FactoryNo2").requestFocus( ) ;
                return  false ;
            }
        }
        if(stringFactoryNo.length( ) == 10) {
            boolean  booleanError  =  false ;
            if(",C,D,".indexOf(","+stringPayType+",")  !=  -1)    booleanError  =  true ;
            if(!check.isID(stringFactoryNo)) {
                String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                    booleanError  =  true ;
                }
            }
            if(booleanError  &&  ",033H98A10512005,033H98A10512004,".indexOf(stringDocNo)==-1) {
                messagebox("[統一編號] 格式錯誤。3") ;
                //getcLabel("FactoryNo").requestFocus( ) ;
                getcLabel("FactoryNo2").requestFocus( ) ;
                return  false ;
            }
        }
        String[][]  retFED1005            =  exeFun.getFED1005(stringFactoryNo) ;
        if(retFED1005.length  ==  0) {
            messagebox("資料庫中無此 [統一編號]。") ;
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        // 停權
        String        stringStopUseMessage    =  "" ;
        Hashtable     hashtableCond           =  new  Hashtable() ;
        hashtableCond.put("OBJECT_CD",        stringFactoryNo) ;
        hashtableCond.put("CHECK_DATE",     getValue("CDate").trim()) ;
        hashtableCond.put("SOURCE",           "B") ;
        hashtableCond.put("FieldName",      "[統一編號] ") ;
        stringStopUseMessage  =  exeFun.getStopUseObjectCDMessage (hashtableCond,  exeUtil) ;
        if(!"TRUE".equals(stringStopUseMessage)) {
            getcLabel("FactoryNo2").requestFocus( ) ;
            messagebox(stringStopUseMessage) ;
            return  false ;
        }
        if(check.isID(stringFactoryNo)  &&  "".equals(retFED1005[0][9].trim())) {
            messagebox("廠商資料之 [登記地址]為空白，此廠商不允許使用，請補[登記地址]後，再使用。\n(有問題請洽 [財務室])") ;
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        String     stringComNo             =  getValue("ComNo").trim( ) ;
        if(!exeFun.isFactoryNoOK(stringComNo,  stringFactoryNo)) {
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        // 第一次使用 2016/11/14 停用
        /*String  stringToday        =  datetime.getToday("yymmdd") ;
        String  stringBarCode    =  getValue("BarCodeOld").trim( ) ;
        if(stringFlow.indexOf("簽核") !=  -1)   {
            // 第一次使用對象及時間在一個月之內
            //  0  LAST_YMD       1  LAST_USER
            String      stringLastYmd       =  "" ;
            boolean  booleanFactoryNo  =  true ;
            //
            if(retFED1005.length  >  0)               stringLastYmd  =  exeFun.getFED1005LastYMD(retFED1005[0][0].trim(),  exeUtil).replaceAll("/","") ;
            //
            booleanFactoryNo  =  !"".equals(stringLastYmd)  &&
                               Math.abs(datetime.subDays1(stringToday,  convert.replace(stringLastYmd,  "/",  "")))  <  30  &&
                              exeFun.isFirstTimeUseFactoryNo(stringBarCode,  stringComNo,  stringFactoryNo) ;
            if(booleanFactoryNo) {
                messagebox("[統一編號] "  +  stringFactoryNo  +  " 第一次使用。") ;
            }
        }*/
        return  true ;
    }
    // 發票
    public  boolean  isTable1CheckOK(Doc.Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        JTable               jtable1                            =  getTable("Table1") ;
        JTabbedPane   jtabbedPane1                =  getTabbedPane("Tab1") ;
        String                stringInvoiceNo               =  ""  ;
        String                stringFactoryNo              =  "" ;
        String                stringFactoryNoOld         =  "" ;
        String                stringFactoryName          =  "" ;
        String                stringInvoiceDate            =  "" ;
        String                stringDateRoc                 =  "" ;
        String                stringInvoiceKind             =  "" ;
        String                stringFactoryNoByte        =  "" ;
        String                stringFlow                        =  getFunctionName() ;
        String                stringComNo                   =  getValue("ComNo").trim() ;
        String                stringBarCode                 =  getValue("BarCode").trim() ;
        String                stringBarCodeOld            =  getValue("BarCodeOld").trim() ;
        String                stringToday                     =  getToday("yymmdd") ;
        String                stringTodayCF                =  getToday("yy/mm/dd") ;
        String                stringMessageFactor       =  "" ;
        String[]              retDoc2M014                  =  null ;
        String[][]            retFED1005                    =  new  String[0][0] ;
        Vector               vectorInvoiecNo               =  new  Vector( ) ;
        Vector               vectorInvoiecNoDB           =  (Vector) get("Doc6M010_InvoiceNo_Vector") ;
        Vector               vectorFactoryNoFirm       =  new  Vector() ;
        double              doubleInvoiceTax             =  0 ;
        double           doubleInvoiceTaxL      =  0 ;
        double              doubleInvoiceMoneyTax   =  0 ;
        double              doubleInvoiceMoney         =  0 ;
        double             doubleTaxRate                   =  0 ;
        double              doubleInvoiceTotalMoney  =  0 ;
        boolean         booleanAppyTypeD        =   isApplyTypeDVoucher2(exeFun) ;
        int                      intMaxRow                         =  0 ;
        int                      intTable1Panel                  =  2 ;
        //
        intMaxRow  =  jtable1.getRowCount() ;
        //
        if(intMaxRow  ==  0)  return  true ;
        //
        if(",D,E,".indexOf((""+getValueAt("Table1",  0,  "InvoiceKind")).trim( )) !=  -1) {
            doubleTaxRate  =  0 ;
        } else {
            retDoc2M014     =  exeFun.getDoc2M040() ;
            doubleTaxRate  =  exeUtil.doParseDouble(retDoc2M014[4].trim( ))  /  100 ;
        }
        stringTodayCF  =  exeUtil.getDateConvertFullRoc(stringTodayCF) ;
        for(int  intRowNo=0  ;  intRowNo<intMaxRow  ;  intRowNo++) {
            stringInvoiceNo                  =  (""  +  getValueAt("Table1",  intRowNo,  "InvoiceNo")).trim( ) ;
            stringFactoryNo                 =  (""  +  getValueAt("Table1",  intRowNo,  "FactoryNo")).trim( ) ;
            stringInvoiceDate               =  (""  +  getValueAt("Table1",  intRowNo,  "InvoiceDate")).trim( ) ;
            stringInvoiceKind                =  (""  +  getValueAt("Table1",  intRowNo,  "InvoiceKind")).trim( ) ;
            doubleInvoiceTax               =  exeUtil.doParseDouble(""  +  getValueAt("Table1",  intRowNo,  "InvoiceTax")) ;
            doubleInvoiceTotalMoney  =  exeUtil.doParseDouble(""  +  getValueAt("Table1",  intRowNo,  "InvoiceTotalMoney")) ;
            doubleInvoiceMoney          =  exeUtil.doParseDouble((""  +  getValueAt("Table1",  intRowNo,  "InvoiceMoney")).trim( )) ;
            //
            //setValueAt("Table1",  booleanAppyTypeD?"B":"A",  intRowNo,  "InvoiceKind") ;
            // 金額
            if(doubleInvoiceTotalMoney ==  0  &&  doubleInvoiceMoney  ==  0) {
                JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票金額]不可為零或空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            } else if(doubleInvoiceTotalMoney > 0  &&  doubleInvoiceMoney  >  0) {
                // 不做處理
            } else if(doubleInvoiceMoney  >  0) {
                if(doubleInvoiceTax  ==  0) {
                    doubleInvoiceTotalMoney  =  doubleInvoiceMoney  *  (1+doubleTaxRate) ;
                } else {
                    doubleInvoiceTotalMoney  =  doubleInvoiceMoney  +  doubleInvoiceTax ;
                }
                doubleInvoiceTotalMoney  =  exeUtil.doParseDouble(convert.FourToFive(""+doubleInvoiceTotalMoney,  0)) ;
                setValueAt("Table1",  convert.FourToFive(""+doubleInvoiceTotalMoney,0),  intRowNo,  "InvoiceTotalMoney") ;
            } else {
                if(doubleInvoiceTax  ==  0) {
                    doubleInvoiceMoney        =  doubleInvoiceTotalMoney  /  (1+doubleTaxRate) ;
                } else {
                    doubleInvoiceMoney        =  doubleInvoiceTotalMoney  -  doubleInvoiceTax ;
                }
                doubleInvoiceMoney  =  exeUtil.doParseDouble(convert.FourToFive(""+doubleInvoiceMoney,  0)) ;
                setValueAt("Table1",  convert.FourToFive(""+doubleInvoiceMoney,0),  intRowNo,  "InvoiceMoney") ;
            }
            if(doubleInvoiceTax  ==  0) {
                doubleInvoiceTax          =  doubleInvoiceTotalMoney  -  doubleInvoiceMoney ;
                doubleInvoiceTax          =  exeUtil.doParseDouble(convert.FourToFive(""+doubleInvoiceTax,  0)) ;
                setValueAt("Table1",  convert.FourToFive(""+doubleInvoiceTax,0),  intRowNo,  "InvoiceTax") ;
            }
            doubleInvoiceMoneyTax    =  doubleInvoiceMoney  *  doubleTaxRate ;
            // 檢核
            // 統一編號
            if(intRowNo  ==  0)  stringFactoryNoOld  =  stringFactoryNo ;
            if("".equals(stringFactoryNo)) {
                JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[統一編號] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            stringFactoryNoByte  =  code.StrToByte(stringFactoryNo) ;
            if(stringFactoryNo.length( ) == 8  &&  !check.isCoId(stringFactoryNo)) {
            //if(stringFactoryNo.length( ) == 8  &&  !check.isCoId(stringFactoryNo)  &&  stringFactoryNoByte.length( ) == stringFactoryNo.length( )) {
                //String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                //if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                    JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[統一編號] 格式錯誤。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    return  false ;
                //}
            }
            if(stringFactoryNo.length( ) != 8) {
            //if(check.isID(stringFactoryNo)  &&  stringFactoryNo.length( ) == 10  &&  stringFactoryNoByte.length( ) == stringFactoryNo.length( )) {
                //String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                //if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                    JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列，[統一編號] 不允許為個人身份證。\n(有問題請洽 [財務室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    return  false ;
                //}
            }
            if(!stringFactoryNo.equals(stringFactoryNoOld)) {
                JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列。\n僅允許一家 [統一編號]。\n(有問題請洽 [財務室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            }
            retFED1005        =  exeFun.getFED1005(stringFactoryNo) ;  //  0  LAST_YMD      1  LAST_USER
            if(retFED1005.length  ==  0) {
                JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列，資料庫中無此 [統一編號]。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(check.isID(stringFactoryNo)  &&  "".equals(retFED1005[0][9].trim())) {
                messagebox("發票表格第 "  +  (intRowNo+1)  +  " 列，廠商資料之 [登記地址]為空白，此廠商不允許使用，請補[登記地址]後，再使用。\n(有問題請洽 [財務室])") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(vectorFactoryNoFirm.indexOf(stringFactoryNo)==-1  &&  !exeFun.isFactoryNoOK(stringComNo,  stringFactoryNo)) {
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            vectorFactoryNoFirm.add(stringFactoryNo) ;
            // 第一次使用 2016/11/14 停用
            /*if(stringFlow.indexOf("簽核") !=  -1)   {
                if(intRowNo ==  0) {
                    // 第一次使用對象及時間在一個月之內
                    //  0  LAST_YMD       1  LAST_USER
                    String      stringLastYmd       =  "" ;
                                     retFED1005            =  exeFun.getFED1005(stringFactoryNo) ;
                    boolean  booleanFactoryNo  =  true ;
                    //
                    if(retFED1005.length  >  0) {
                        stringLastYmd  =  exeFun.getFED1005LastYMD(retFED1005[0][0].trim(),  exeUtil).replaceAll("/","") ;
                    }
                    //
                    booleanFactoryNo  =  !"".equals(stringLastYmd)  &&
                                       !"".equals(stringFactoryNo)  &&
                                       Math.abs(datetime.subDays1(stringToday,  convert.replace(stringLastYmd,  "/",  "")))  <  30  &&
                                      exeFun.isFirstTimeUseFactoryNo(stringBarCode,  stringComNo,  stringFactoryNo) ;
                    if(booleanFactoryNo) {
                        stringMessageFactor  +=   "發票表格第 "  +  (intRowNo+1)  +  " 列之統一編號 "  +  stringFactoryNo  +  " 第一次使用。\n" ;
                    }
                }
            }*/
            // /發票號碼
            if("".equals(stringInvoiceNo)  &&   !"D".equals(stringInvoiceKind) ) {
                JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            // 發票號碼
            if("ABEMLNKOPRST".indexOf(stringInvoiceKind)  !=  -1) {
                if(stringInvoiceNo.trim( ).length( )  !=  10) {
                    JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 大小錯誤。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
            }
            if("ABMLNKOPRST".indexOf(stringInvoiceKind)  !=  -1) {//
                  String  stringDateAC   =  exeUtil.getDateConvert(stringInvoiceDate).replaceAll("/",  "") ;
                  String  stringACYear   =  datetime.getYear(stringDateAC) ;
                  String  stringMonth    =  datetime.getMonth(stringDateAC) ;
                  if(!exeFun.isCheckInvoiceDoc2M047OK(stringInvoiceNo.substring(0,2),  stringACYear,  stringMonth,  "A") ) {
                    JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之 [發票字軌] [發票日期] 不一致。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                  }
            }
            // 發票格式檢核
            if(",Z,Q,T,".indexOf(stringInvoiceKind) !=-1) {
                // 數量-發票號碼
                String[]  arrayInvoiceNo =  convert.StringToken(stringInvoiceNo,  "-") ;
                if(arrayInvoiceNo.length  !=  2) {
                    messagebox("發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 格式錯誤(數目-發票號碼)。") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(exeUtil.doParseDouble(arrayInvoiceNo[0])  <=  0) {
                    messagebox("發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 數目格式 只能為數字。") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(arrayInvoiceNo[0].length()  !=  4) {
                    messagebox("發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 數目格式 須為 4 個數字。") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(exeUtil.doParseDouble(arrayInvoiceNo[0])  ==  1) {
                    String  stringTempL  =  "Q".equals(stringInvoiceKind)?"載具號碼25":"收據" ;
                    if("T".equals(stringInvoiceKind))  stringTempL  =  "載具號碼25" ;
                    messagebox("營業稅憑證表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 數目為 1，發票格式 請選擇 "+stringTempL+"。") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(",Q,T,".indexOf(stringInvoiceKind) !=-1  &&  arrayInvoiceNo[1].length()  !=  10) {
                    messagebox("發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 發票號碼格式 須為 10 個數字。") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                // 載具號碼彙總 合理性檢查
                doubleInvoiceTaxL   =  doubleInvoiceTax  /  exeUtil.doParseDouble(arrayInvoiceNo[0]) ;
                if(doubleInvoiceTaxL  >  500) {
                    String  stringTempL  =  "" ;
                    if("Q".equals(stringInvoiceKind)) {
                        stringTempL  =  "載具號碼彙總25" ;
                    } else if("Z".equals(stringInvoiceKind)) {
                        stringTempL  =  "收據彙總" ;
                    } else if("T".equals(stringInvoiceKind)) {
                        stringTempL  =  "載具號碼彙總分攤25" ;
                    }
                    messagebox("營業稅憑證表格第 "  +  (intRowNo+1)  +  " 列 "+stringTempL+" 稅額合理性檢查。\n發票稅額 500 元 以上時，須單獨列示，不可彙總處理。") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
            }
            if(vectorInvoiecNo.indexOf(stringInvoiceNo)  !=  -1) {
                // 本身
                JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 重覆。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            vectorInvoiecNo.add(stringInvoiceNo.trim( )) ;
            //if(vectorInvoiecNoDB.indexOf(stringInvoiceNo.trim( ))  ==  -1  &&  !exeFun.isExistInvoiceNoCheck(stringInvoiceNo)) {
            if(",C,X,Y,Z,Q,R,S,T,".indexOf(stringInvoiceKind) ==-1) {
                if(!exeFun.isExistInvoiceNoCheck(stringInvoiceNo,  stringBarCodeOld)) {
                    JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 已存在資料庫中。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                Hashtable  hashtableLimit   =  new  Hashtable() ;
                String      stringDateStart  = stringInvoiceDate ;
                //
                stringDateStart =  exeUtil.getDateConvertRoc(stringDateStart).replaceAll("/",  "") ;
                stringDateStart  =  datetime.dateAdd(stringDateStart,  "y",  -2) ;
                //
                hashtableLimit.put("ComNo",      stringComNo) ;
                hashtableLimit.put("DateStart",  stringDateStart) ;
                hashtableLimit.put("InvoiceNo",  stringInvoiceNo) ;
                if(!exeFun.isExistInvoiceNoCheckFED1012(hashtableLimit,  exeUtil)) {
                    String  stringBarCodeDoc2M0102  =  exeFun.getNameUnionDoc("BarCodeDoc2M0102",  "Doc2M040",  " AND  ISNULL(BarCodeDoc2M0102,'')  <>  '' ",  new  Hashtable(),  exeUtil) ;
                    if("".equals(stringBarCodeOld)  ||  stringBarCodeDoc2M0102.indexOf(stringBarCodeOld)==-1) {
                        JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票號碼] 已存在資料庫中。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                        jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                        jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                        return  false ;
                    }
                }
            }
            // 發票未稅金額檢查
            if(!"D".equals(stringInvoiceKind)  &&  !"E".equals(stringInvoiceKind)) {
                if(doubleInvoiceTax  <  (doubleInvoiceMoneyTax-3)  ||  doubleInvoiceTax  >  (doubleInvoiceMoneyTax+3)) {
                      JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票稅額] 須為 [發票未稅金額] 乘以 [稅率] 正負 3 的範圍內。\n(有問題請洽 [財務室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                      jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                      jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                      return  false ;           
                }
            } else {
                if(doubleInvoiceTax  >  0) {
                    JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票格式] 為 [不得扣抵] 時，[發票稅額] 應為 0。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    setValueAt("Table1",  "0",  intRowNo,  "InvoiceTax") ;
                    return  false ;
                }
            }
            // 發票日期
            if("".equals(stringInvoiceDate)) {
                JOptionPane.showMessageDialog(null, "發票表格第 "  +  (intRowNo+1)  +  " 列之[發票日期] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;       
            }
            stringDateRoc  =  exeUtil.getDateFullRoc(stringInvoiceDate,  "發票日期") ;
            if(stringDateRoc.length( )  !=  9) {
                JOptionPane.showMessageDialog(null, stringDateRoc,  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;       
            } else {
                setValueAt("Table1",  stringDateRoc,  intRowNo,  "InvoiceDate") ;
            }
            if(Math.abs(datetime.subDays1(stringToday,  convert.replace(stringDateRoc,  "/",  "")))  >  120) {
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                int  ans  =  JOptionPane.showConfirmDialog(null,  
                                                "[發票日期] 期限須在四個月內，如仍要執行，請按 [是]，並請附上罰扣單。",
                                                "請選擇?",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE) ;
                if(ans  ==  JOptionPane.NO_OPTION) {
                    return  false ;
                }
            }
            //
            if(stringTodayCF.compareTo(stringDateRoc)  <  0) {
                JOptionPane.showMessageDialog(null,  "發票表格第 "  +  (intRowNo+1)  +  " 列之 [發票日期] 不能晚於今日。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ; 
            }
        }
        if(!"".equals(stringMessageFactor)) {
            JOptionPane.showMessageDialog(null,   stringMessageFactor, "訊息",  JOptionPane.ERROR_MESSAGE) ;
        }
        return  true ;
    }
    // 個人收據
    public  boolean  isTable2CheckOK(FargloryUtil  exeUtil,  Doc.Doc2M010  exeFun) throws  Throwable {
        JTable               jtable2                                =  getTable("Table2") ;
        JTabbedPane   jtabbedPane1                    =  getTabbedPane("Tab1") ;
        int                      intTable2Panel                   =  3 ;
        int                      intHasTaxCount                  =  0  ;
        String                stringFactoryNo                  =  "" ;
        String                stringFactoryNoByte           =  "" ;
        String                stringFactoryNoOld            =  "" ;
        String                stringFactoryName             =  "" ;
        String                stringReceiptDate               =  "" ;
        String                stringDateRoc                    =  "" ;
        String                stringReceiptKind               =  "" ;
        String                stringCDateRoc                  =  getValue("CDate").trim() ;
        String                stringTodayRoc                  =  datetime.getToday("yy/mm/dd") ;
        String                stringComNo                       =  getValue("ComNo").trim() ;
        String                stringBarCode                    =  getValue("BarCode").trim() ;
        String                stringToday                        =  getToday("yymmdd") ;
        String                stringMessageFactor          =  "" ;
        String                stringFlow                           =  getFunctionName() ;
        String                stringAcctNo                       =  "" ;
        String                stringSupplementMoney      =  "" ;
        String[][]            retFED1005                         =  new  String[0][0] ;
        double              doubleReceiptTax               =  0 ;
        double              doubleReceiptMoney          =  0 ;
        double              doubleReceiptTotalMoney  =  0 ;
        double              doubleReceiptMoneyTax    =  0 ;
        double              doubleTaxRate                   =  0  ;
        double              doubleSupplementMoney   =  0  ;
        double              doubleSupplementMoneyCF=  0  ;
        boolean            booleanSupplementMoney0  =  false ;
        boolean            booleanSupplementFlag       =  true ;
        Vector              vectorFactoryNoFirm             =  new  Vector() ;
        Hashtable         hashtableData                   =  new  Hashtable() ; 
        //
        //
        if( jtable2.getRowCount()  ==  0) return  true ;
        //
        for(int  intRowNo=0  ;  intRowNo< jtable2.getRowCount()  ;  intRowNo++) {
            stringFactoryNo                   =  (""  +  getValueAt("Table2",  intRowNo,  "FactoryNo")).trim( ) ;
            stringReceiptDate               =  (""  +  getValueAt("Table2",  intRowNo,  "ReceiptDate")).trim( ) ;
            stringReceiptKind                =  (""  +  getValueAt("Table2",  intRowNo,  "ReceiptKind")).trim( ) ;
            stringSupplementMoney     =  (""  +  getValueAt("Table2",  intRowNo,  "SupplementMoney")).trim( ) ;
            doubleReceiptTax               =  exeUtil.doParseDouble(""  +  getValueAt("Table2",  intRowNo,  "ReceiptTax")) ;
            doubleReceiptMoney          =  exeUtil.doParseDouble((""  +  getValueAt("Table2",  intRowNo,  "ReceiptMoney")).trim( )) ;
            doubleReceiptTotalMoney  =  exeUtil.doParseDouble((""  +  getValueAt("Table2",  intRowNo,  "ReceiptTotalMoney")).trim( )) ;
            doubleTaxRate                   =  exeUtil.doParseDouble((""  +  getValueAt("Table2",  intRowNo,  "ReceiptTaxType")).trim( ))  /  100 ;
            stringAcctNo                        =  (""  +  getValueAt("Table2",  intRowNo,  "ACCT_NO")).trim( ) ;
            booleanSupplementFlag      =  true ;
            //
            if(",A,B,C,".indexOf(","+stringReceiptKind+",")!=-1  &&  
               ",Z0001,Z8000,Z0007,".indexOf(","+stringFactoryNo+",")==-1  &&  
               stringFactoryNo.length() != 8  &&
               stringFactoryNo.length() != 10  ) {
                  JOptionPane.showMessageDialog(null,  "個人扣繳表格第 "  +  (intRowNo+1)  +  " 列 格式為 須通報扣繳收據、須通報收據、免通報時，廠商僅允許使用 統一編號、個人身份證、Z0001公司員工、Z8000集團員工。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                  jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                  jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                  return  false ;   
            }
            // 稅率
            if(!"A".equals(stringReceiptKind)) doubleTaxRate  =  0 ;
            // 金額
            if(doubleReceiptTotalMoney  ==  0  &&  doubleReceiptMoney  ==  0) {
                JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列之[扣繳金額]不可為零或空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            } else if(doubleReceiptTotalMoney  >  0  &&  doubleReceiptMoney  >  0) {
                // 不做處理
            } else if(doubleReceiptMoney  >  0) {
                if(doubleReceiptTax  ==  0) {
                    doubleReceiptTotalMoney  =  doubleReceiptMoney  /  (1-doubleTaxRate) ;
                }  else {
                    doubleReceiptTotalMoney  =   doubleReceiptMoney  +  doubleReceiptTax ;
                }
                doubleReceiptTotalMoney  =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptTotalMoney,  0)) ;
                 setValueAt("Table2",  convert.FourToFive(""+doubleReceiptTotalMoney,  0),  intRowNo,  "ReceiptTotalMoney") ;
            } else {
                if(doubleReceiptTax  ==  0) {
                    doubleReceiptMoney          =  doubleReceiptTotalMoney  *  (1-doubleTaxRate) ;
                } else {
                    doubleReceiptMoney          =  doubleReceiptTotalMoney  -  doubleReceiptTax ;
                }
                doubleReceiptMoney          =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptMoney,  0)) ;
                setValueAt("Table2",  convert.FourToFive(""+doubleReceiptMoney,  0),  intRowNo,  "ReceiptMoney") ;
            }
            if(doubleReceiptTax  ==  0) {
                doubleReceiptTax          =  doubleReceiptTotalMoney  -  doubleReceiptMoney ;
                doubleReceiptTax          =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptTax,  0)) ;
                setValueAt("Table2",  convert.FourToFive(""+doubleReceiptTax,  0),  intRowNo,  "ReceiptTax") ;
            }
            // 所得金額＝所得淨額+扣繳稅額
            doubleReceiptTax                =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptTax,  0)) ;
            doubleReceiptMoney           =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptMoney,  0)) ;
            doubleReceiptTotalMoney   =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptTotalMoney,  0)) ;
            if(doubleReceiptTotalMoney  !=  (doubleReceiptMoney+doubleReceiptTax)) {
                JOptionPane.showMessageDialog(null,  "個人扣繳表格第 "  +  (intRowNo+1)  +  " 列之 所得金額 不等於 所得淨額+扣繳稅額。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(doubleReceiptTax  !=  0)  intHasTaxCount++ ;
            doubleReceiptMoneyTax  =  doubleReceiptTotalMoney  *  doubleTaxRate ;
            //
            // 補充保費
            if(",Q94544,Q94545,".indexOf(","+stringBarCode+",")==-1  &&  !isSupplementMoneyOK(intRowNo,  hashtableData,  exeUtil,  exeFun)) {
                  jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                  jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                  return  false ;
            }
            // 統一編號
            if(intRowNo  ==  0)  stringFactoryNoOld  =  stringFactoryNo ;
            if("".equals(stringFactoryNo)) {
                JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列[統一編號] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            stringFactoryNoByte  =  code.StrToByte(stringFactoryNo) ;
            if(stringFactoryNo.length() != 10  &&  stringFactoryNo.length() != 8) {
                if(",Z0001,Z0007,".indexOf(stringFactoryNo)  ==  -1) {
                    String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                    if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                        JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列[統一編號] 格式錯誤。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                        return  false ;
                    }
                }
            } else if(stringFactoryNo.length() == 8) {
                if(!check.isCoId(stringFactoryNo)) {
                    String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                    if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                        JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列[統一編號] 格式錯誤。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                        return  false ;
                    }
                }
            } else if(stringFactoryNo.length() == 10) {
                if(!check.isID(stringFactoryNo)) {
                    String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                    if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                        JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列[統一編號] 格式錯誤。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                        return  false ;
                    }
                }
            }
            if(!stringFactoryNo.equals(stringFactoryNoOld)) {
                JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列發生錯誤，僅允許一家 [統一編號]。\n(有問題請洽 [財務室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            }
            retFED1005        =  exeFun.getFED1005(stringFactoryNo) ;  //  0  LAST_YMD      1  LAST_USER
            if(retFED1005.length  ==  0) {
                JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列發生錯誤，資料庫中無此 [統一編號]。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(check.isID(stringFactoryNo)  &&  "".equals(retFED1005[0][9].trim())) {
                messagebox("個人收據表格第 "  +  (intRowNo+1)  +  " 列廠商資料之 [登記地址]為空白，此廠商不允許使用，請補[登記地址]後，再使用。\n(有問題請洽 [財務室])") ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(vectorFactoryNoFirm.indexOf(stringFactoryNo)==-1  &&  !exeFun.isFactoryNoOK(stringComNo,  stringFactoryNo)) {
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
             }
             vectorFactoryNoFirm.add(stringFactoryNo) ;
            /*if(stringFlow.indexOf("簽核") !=  -1)   {
                if(intRowNo ==  0) {
                    // 第一次使用對象及時間在一個月之內
                    //  0  LAST_YMD       1  LAST_USER
                    String      stringLastYmd       =  "" ;
                                     retFED1005            =  exeFun.getFED1005(stringFactoryNo) ;
                    boolean  booleanFactoryNo  =  true ;
                    //
                    if(retFED1005.length  >  0) {
                        stringLastYmd  =  exeFun.getFED1005LastYMD(retFED1005[0][0].trim(),  exeUtil).replaceAll("/","") ;
                    }
                    //
                    booleanFactoryNo  =  !"".equals(stringLastYmd)  &&
                                       !"".equals(stringFactoryNo)  &&
                                       Math.abs(datetime.subDays1(stringToday,  convert.replace(stringDateRoc,  "/",  "")))  <  30  &&
                                      exeFun.isFirstTimeUseFactoryNo(stringBarCode,  stringComNo,  stringFactoryNo) ;
                    if(booleanFactoryNo) {
                        stringMessageFactor  +=   "個人收據票表格第 "  +  (intRowNo+1)  +  " 列之統一編號 "  +  stringFactoryNo  +  " 第一次使用。\n" ;
                    }
                }
            }*/
            // 格式
            // 所得淨額
            // 扣繳金額
            // 所得總額
            if("A".equals(stringReceiptKind)) {
                if(doubleReceiptTax  <  (doubleReceiptMoneyTax-10)  ||  doubleReceiptTax  >  (doubleReceiptMoneyTax+10)) {
                      if(",Z0001,Z8000,".indexOf(","+stringFactoryNo+",")  ==  -1) {
                          JOptionPane.showMessageDialog(null,  "個人收據表格第 "  +  (intRowNo+1)  +  " 列[扣繳稅額] 須為 [所得淨額] 乘以 [稅率] 正負 10 的範圍內。\n(有問題請洽 [財務室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                          jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                          jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                          return  false ;           
                      }
                }
            } else {
                if(doubleReceiptTax  >  0) {
                    JOptionPane.showMessageDialog(null, "個人收據表格第 "  +  (intRowNo+1)  +  " 列[收款格式] 為 [須通報扣繳收據][免通報] 時，[扣繳金額] 應為 0。\n(有問題請洽 [財務室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                    jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
            }
            // 到期日
            // 對應
            if("".equals(stringAcctNo)  &&  stringFlow.indexOf("簽核") !=  -1) {
                JOptionPane.showMessageDialog(null, "個人收據表格第 "  +  (intRowNo+1)  +  " 列 [對應] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
        }
        String    stringMessage                       =  ""+hashtableData.get("MESSAGE") ;
        if("null".equals(stringMessage))  stringMessage  =  "" ;
        if(!"".equals(stringMessage)) {
            messagebox("代收繳補充保費金額為０，請檢附免扣繳補充保費資格證明。") ;
        }
        /*String      stringPayCondition2   =  getValue("PayCondition2").trim( ) ;
        boolean   booleanFlag                 =  ("".equals(stringPayCondition2)    ||  "999".equals(stringPayCondition2))  &&
                               intHasTaxCount  ==  2  ;
        booleanFlag  =  booleanFlag  ||  intHasTaxCount  >  2 ;
        if(booleanFlag) {
            JOptionPane.showMessageDialog(null,   "個人收據表格中，出現 "  +  intHasTaxCount  +  " 筆 [須通報扣繳資訊] 資料，與 [付款條件] 不一致。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
            return  false ;
        }*/
        if(!"".equals(stringMessageFactor)) {
            JOptionPane.showMessageDialog(null,   stringMessageFactor, "訊息",  JOptionPane.ERROR_MESSAGE) ;
        }
        
        return  true ;
    }
    public boolean isSupplementMoneyOK(int  intRow,  Hashtable  hashtableData,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws Throwable{
          String         stringThisField                  =  "SupplementMoney" ;
          String         stringField                        =  "" ;
          String         stringValue                      =  "" ;
          String          stringToday                     =  datetime.getToday("YYYY/mm/dd") ;
          String      stringEmployeeNo        =  getValue("EmployeeNo").trim() ;
          String[]       arrayField                         =  {"ReceiptTotalMoney",  "ReceiptMoney",  "ReceiptTax",  "ReceiptTaxType",  "ReceiptKind",  "FactoryNo",  "ACCT_NO",  "SupplementMoney"} ;
          //
          hashtableData.put("PayCondition1",  "0") ;
          hashtableData.put("EmployeeNo",  stringEmployeeNo) ;
          hashtableData.put("TODAY",        stringToday) ;
          hashtableData.put("SpecUserID",   "Y") ;
          hashtableData.put("TYPE",         "CHECK") ;
          for(int  intNo=0  ;  intNo<arrayField.length  ;  intNo++) {
              stringField      =  arrayField[intNo] ;
              stringValue     =  (""+getValueAt("Table2",  intRow,  stringField)).trim() ;
              //
              hashtableData.put(stringField,  stringValue) ;
          }
          hashtableData.put("SpecCostID",  "N") ;
          //
          hashtableData.put("Table21Exist",  "N") ;
          //
          boolean  booleanFlag              =  exeFun.isSupplementMoneyOK(hashtableData,  exeUtil) ;
          double   doubleSupplementMoney    =  exeUtil.doParseDouble(""+hashtableData.get("SupplementMoney")) ;
          String    stringMessage                       =  ""+hashtableData.get("MESSAGE") ;
          //
          if("null".equals(stringMessage))  stringMessage  =  "" ;
          if(!booleanFlag  &&  !"".equals(stringMessage)) {
              messagebox("個人扣繳表格第 "  +  (intRow+1)  +  " 列之"  + stringMessage);
          }
          //
          setValueAt("Table2",  convert.FourToFive(""+doubleSupplementMoney,  0),  intRow,  "SupplementMoney") ;
          return  booleanFlag ;
      }
    
    
    // 酬庸
    public  boolean  isTable3CheckOK(FargloryUtil  exeUtil,  Doc.Doc2M010  exeFun) throws  Throwable {
        JTabbedPane   jtabbedPane1            =  getTabbedPane("Tab1") ;
        JTable               jtable3                        =  getTable("Table3") ;
        int                      intRowCount               =  jtable3.getRowCount() ;
        int                      intTable3Panel           =  4 ;
        String                stringBarCodeOld      =  getValue("BarCodeOld").trim( ) ;
        String                stringBarCode           =  "" ;
        String                stringPosition             =  "" ;
        String                stringProjectID1         =  "" ;
        String                stringProjectID1Use   =  "" ;
        String                stringProjectID1T       =  "" ;
        String                 stringKey                  =  "" ;
        String                 stringErrorMessage  =  "" ;
        String                 stringMessage          =  "" ;
        String                  stringSignDate        =  "" ;
        String                  stringSignDate2      =  "" ;
        String                  stringCDate            =  getValue("CDate").trim() ;
        String                  stringSqlAnd           =  "" ;
        String[][]              retDoc6M014          =  null ;
        Vector  vectorKey                  =  new  Vector( ) ;
        //
        if(intRowCount  ==  0)  return  true ;
        //
        stringCDate  =  exeUtil.getDateConvertFullRoc(stringCDate) ;
        for(int  intNo=0  ;  intNo<intRowCount  ;  intNo++) {
            stringPosition            =  (""+getValueAt("Table3",  intNo,  "Position")).trim() ;
            stringProjectID1        =  (""+getValueAt("Table3",  intNo,  "ProjectID1")).trim() ;
            stringProjectID1Use  =  (""+getValueAt("Table3",  intNo,  "ProjectID1Use")).trim() ;
            stringSignDate          =  (""+getValueAt("Table3",  intNo,  "SignDate")).trim() ;
            stringSignDate2        =  (""+getValueAt("Table3",  intNo,  "SignDate2")).trim() ;
            stringKey                   =  stringProjectID1  +  "-"  +  stringProjectID1Use  +  "-"  +  stringPosition ;
            // 棟樓別
            if("".equals(stringPosition)) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[棟樓別] 不可為空白。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            // 案別
            if("".equals(stringProjectID1)) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[案別] 不可為空白。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            if("".equals(stringProjectID1Use)) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[實際案別] 不可為空白。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            stringProjectID1  =  stringProjectID1.toUpperCase() ;
            if(!exeFun.isExistProjectID1PositionCheck(stringProjectID1Use,  stringPosition)) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[案別] [棟樓別]不存在於資料庫中。\n(有問題請洽 [行銷管理室])", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            setValueAt("Table3",  stringProjectID1,  intNo,  "ProjectID1") ;
            // 本身重複
            if(vectorKey.indexOf(stringKey)  !=  -1) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[案別][棟樓別] 發生重複申請。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            vectorKey.add(stringKey) ;
            //System.out.println("--------------------酬庸資料庫重複") ;
            stringSqlAnd   =  " AND  M14.ProjectID1Use  =  '"  +  stringProjectID1Use    +  "' " +
                                       " AND  M14.BarCode  <>  '"         +  stringBarCodeOld       +  "' "  +
                           " AND  M14.BarCode  NOT  IN  (SELECT  BarCode  FROM  Doc2M014  WHERE  STATUS_CD = 'Z')";
            retDoc6M014  =  exeFun.getDoc6M014S(stringProjectID1,  stringPosition,  stringSqlAnd)  ;
            if(retDoc6M014.length  >  0) {
                if(!"".equals(stringErrorMessage))  stringErrorMessage  +=  "，\n" ;
                //
                stringErrorMessage  +=  "[案別] 為 "  +  stringProjectID1  +  "、[棟樓別] 為 "  +  stringPosition  +  "在 [條碼編號] 為 " ;
                stringMessage            =  "" ;
                for(int  intDoc6M014=0  ;  intDoc6M014<retDoc6M014.length  ;  intDoc6M014++) {
                    stringBarCode  =  retDoc6M014[intDoc6M014][0].trim() ;
                    if("".equals(stringBarCode))  continue ;
                    if(!"".equals(stringMessage))  stringMessage  +=  "、" ;
                    stringMessage  +=  stringBarCode ;
                }
                stringErrorMessage  +=  stringMessage  +  " 現在正在申請中。 " ;
            }
            // 簽約金兌現日
            if("".equals(stringSignDate)) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[簽約金兌現日] 不可為空白。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            stringSignDate  =  exeUtil.getDateFullRoc(stringSignDate,  "簽約金兌現日") ;
            if(stringSignDate.length()  !=  9) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[簽約金兌現日] 日期格式錯誤。(yy/mm/dd)", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            //if(!"Y".equals(""+get("Century_START_DATE"))) stringSignDate  =  exeUtil.getDateConvertRoc(stringSignDate) ;
            String  stringCDateL  =  stringCDate ;
            String  stringToday    =  datetime.getToday("yy/mm/dd") ;
            if(stringToday.compareTo(stringCDateL) > 0)  stringCDateL  =  stringToday ;
            if(stringSignDate.compareTo(stringCDateL)  >  0) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[簽約金兌現日("+stringSignDate+")] 尚未到期，不能申請酬庸。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            setValueAt("Table3",  stringSignDate,  intNo,  "SignDate") ;
            // 簽約日
            if("".equals(stringSignDate2)) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[簽約日] 不可為空白。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            stringSignDate2  =  exeUtil.getDateFullRoc(stringSignDate2,  "簽約日") ;
            if(stringSignDate2.length()  !=  9) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[簽約日] 日期格式錯誤。(yy/mm/dd)", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            if(stringSignDate2.compareTo(stringCDateL)  >  0) {
                JOptionPane.showMessageDialog(null,   "酬庸表格第 "  +  (intNo+1)  +  " 列之[簽約日("+stringSignDate2+")] 早於 [輸入日期("+stringCDateL+")]。", "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            setValueAt("Table3",  stringSignDate2,  intNo,  "SignDate2") ;
        }
        if(!"".equals(stringErrorMessage)) {
            messagebox(stringErrorMessage) ;
            return  false ;
            /*int  ans  =  JOptionPane.showConfirmDialog(null,  
                                            stringErrorMessage  +  "\n如仍要執行，請按 [是]，繼續流程。",
                                            "請選擇?",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE) ;
            if(ans  ==  JOptionPane.NO_OPTION) {
                return  false ;
            }*/
        }
        if(getFunctionName().indexOf("簽核")  ==  -1)  return  true ;
        
        
        
        // 次數判斷
        int            intCount              =  0 ;
        String      stringSql             =  "" ;
        String      stringFactoryNo  = getValue("FactoryNo2").trim() ;
        String[][]  retData               =  null ;
        //
        if("".equals(stringFactoryNo)) {
            if(getTable("Table1").getRowCount()  >  0) {
                stringFactoryNo  =  (""+getValueAt("Table1",  0,  "FactoryNo")).trim() ;
            } else {
                stringFactoryNo  =  (""+getValueAt("Table2",  0,  "FactoryNo")).trim() ;
            }
        }
        // 借款
        String  stringKindNo  =  getValue("KindNo").trim() ;
        //
        stringSql  =  " SELECT  DISTINCT  BarCode " +
                              " FROM  Doc6M010 "  +
                  " WHERE  KindNo  =  '"+stringKindNo+"' "  +
                       " AND  BarCode  IN  (SELECT  BarCode  FROM  Doc6M014) "  +
                     " AND  UNDERGO_WRITE  <>  'E' "  +
                     " AND  BarCode  <>  '"  +  stringBarCodeOld  +  "' "  +
                     " AND  CDate  <=  '"      +  stringCDate             +  "' "  +
                     " AND  ((FactoryNo  =  '"  +  stringFactoryNo  +  "')  OR " +
                                " (BarCode  IN  (SELECT  BarCode  FROM  Doc6M011  WHERE  FactoryNo  =  '"  +  stringFactoryNo  +  "'))  OR "  +
                          " (BarCode  IN  (SELECT  BarCode  FROM  Doc6M013  WHERE  FactoryNo  =  '"  +  stringFactoryNo  +  "'))) " ;
        retData     =  exeFun.getTableDataDoc(stringSql) ;
        intCount  +=  retData.length ;
        // 請款
        stringSql  =  " SELECT  DISTINCT  BarCode " +
                              " FROM  Doc2M010 "  +
                  " WHERE  1  =  1 "  +
                       " AND  UNDERGO_WRITE  <>  'E' "  +
                     " AND  CDate  <=  '"  +  stringCDate  +  "' "  +
                     " AND  ((BarCode  IN  (SELECT  BarCode  FROM  Doc2M011  WHERE  FactoryNo  =  '"  +  stringFactoryNo  +  "'))  OR "  +
                          " (BarCode  IN  (SELECT  BarCode  FROM  Doc2M013  WHERE  FactoryNo  =  '"  +  stringFactoryNo  +  "'))) " ;
        retData     =  exeFun.getTableDataDoc(stringSql) ;
        JOptionPane.showMessageDialog(null,  "廠商 "+stringFactoryNo+"("+exeFun.getFactoryName(stringFactoryNo) +")，包含本次、借款 "+intCount+" 筆、請款 "+retData.length +" 筆，共使用 "+(intCount+retData.length+1)+" 次。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
        return   true ;
    }
    public  boolean  isTable4CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        return  true ;
    }
    // 費用表格 
    public  boolean  isTable6CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable               jTable6                                      =  getTable("Table6") ;
        JTable               jtable9                                    =  getTable("Table9") ;
        JTable               jtable22                                   =  getTable("Table22") ;
        boolean            booleanPurchaseExist               =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false表示可以無請購單  
        boolean            booleanTable9                  =  jtable9.getRowCount()>0 ;
        boolean            booleanTable22                   =  jtable22.getRowCount()>0 ;
        //
        if(jTable6.getRowCount()  <=  0)            doTable6ErrorAction(-1,  "請輸入 [費用] 資料。") ;
        //
        if(!isTable6033FGOK(exeUtil,  exeFun))    return  false ;
        //
        String                stringExistDeptCd                        =  booleanPurchaseExist?"":"";//exeFun.getVFEE(stringComNo,  stringCDate,  exeUtil) ;
        String                stringStatus                                  =  "" ;
        System.out.println("getNoPurchaseCostID2ForDoc2M020-----------------------------------------S") ;
        Vector                vectorDoc2M020                        =  exeFun.getNoPurchaseCostID2ForDoc2M020(getValue("ComNo")) ;
        System.out.println("getNoPurchaseCostID2ForDoc2M020-----------------------------------------E") ;
        Hashtable          hashtableUsedRealMoney          =  null ;
        Hashtable          hashtableError                   =  new  Hashtable() ;
        Hashtable         hashtable1331AProject                = exeFun.getAProject( ) ;
        // 輸入部門 分攤部門 一致檢核
        System.out.println("isTable6InputDeptCdOK----------------------------S") ;
        if(!isTable6InputDeptCdOK(exeUtil,  exeFun))  return  false ;
        System.out.println("isTable6InputDeptCdOK----------------------------E") ;
        // 重複檢核
        if(!isTable6DulDataOK(exeUtil))  return  false ;
        //
        for(int  intNo=0  ;  intNo<jTable6.getRowCount()  ;  intNo++) {
            // 請款代碼檢核
            if(!isTable6CostIDOK(vectorDoc2M020,  intNo,  exeUtil,  exeFun))                        return  false ;
            // 部門檢核
            if(!isTable6DepartNoOK(stringExistDeptCd,  intNo,  exeUtil,  exeFun))                     return  false ;
            // 案別
            if(!isTable6ProjectID1OK(intNo,  hashtable1331AProject,  exeUtil,  exeFun))               return  false ;
            // 金額
            hashtableUsedRealMoney  =  isTable6MoneyOK(intNo,  exeUtil,  exeFun,  hashtableUsedRealMoney,  hashtableError) ;
            stringStatus                         =  ""+hashtableError.get("STATUS") ;
            if(!"OK".equals(stringStatus)) {
                return  false ;
            }
        }
        if(booleanTable9) {
            // Table6 與 Table9 一致檢核
            //System.out.println("isTable2SameTable17CheckOK----------------------------------") ;
            if(!isTable6SameTable17CheckOK(exeUtil,  exeFun))  return  false ;
            //System.out.println("isTable17CheckOK----------------------------------") ;
            // Table17 之 請購項目案別分攤檢核
            if(!isTable17CheckOK(exeUtil,  exeFun))           return  false ;
        }
        if(booleanTable22) {
            // Table6 與 Table22 一致檢核
            System.out.println("isTable2SameTable22CheckOK----------------------------------") ;
            if(!isTable6SameTable22CheckOK(exeUtil,  exeFun))  return  false ;
        }
        // 幣別檢核
        System.out.println("幣別檢核---------------------------------S") ;
        if(!isTable6CoinTypeCheckOK(exeUtil,  exeFun))              return  false ; 
        System.out.println("幣別檢核---------------------------------E") ;
        return  true ;
    }
    public  boolean  doTable6ErrorAction(int  intRowNo,  String  stringErrMessage) throws  Throwable {
        JTabbedPane   jtabbedPane1                      =  getTabbedPane("Tab1") ;
        int                      intTable1Panel                    =  1 ;
        JTable               jtable6                                 =  getTable("Table6") ;
        if(!"".equals(stringErrMessage))messagebox(stringErrMessage) ;
        if(intRowNo  !=  -1)                     jtable6.setRowSelectionInterval(intRowNo,  intRowNo) ;
        jtabbedPane1.setSelectedIndex(intTable1Panel) ;
        return  false ;
    }
    public  boolean  isTable6DulDataOK(FargloryUtil  exeUtil) throws  Throwable {
        JTable jtable6                 =  getTable("Table6") ;
        String  stringInOut          =  "" ;
        String  stringCostID        =  "" ;
        String  stringCostID1      =  "" ;
        String  stringDepart        =  "" ;
        String  stringProjectID    =  "" ;
        String  stringProjectID1  =  "" ;
        String  stringKey            =  "" ;
        String  stringLimit           =  "%-%" ;
        Vector  vectorKEY         =  new  Vector() ;
        for(int  intNo=0  ;  intNo<jtable6.getRowCount()  ;  intNo++) {
            stringInOut                   =  (""  +  getValueAt("Table6",  intNo,  "InOut")).trim( ) ;
            stringCostID                 =  (""  +  getValueAt("Table6",  intNo,  "CostID")).trim( ) ;
            stringCostID1               =  (""  +  getValueAt("Table6",  intNo,  "CostID1")).trim( ) ;
            stringDepart                 =  (""  +  getValueAt("Table6",  intNo,  "DepartNo")).trim( ) ;
            stringProjectID             =  (""  +  getValueAt("Table6",  intNo,  "ProjectID")).trim( ) ;
            stringProjectID1           =  (""  +  getValueAt("Table6",  intNo,  "ProjectID1")).trim( ) ;
            stringKey                      =  stringInOut          + stringLimit +    // 2  InOut
                                         stringDepart       + stringLimit +     // 3  DepartNo
                                         stringProjectID    + stringLimit +     // 4  ProjectID
                                           stringProjectID1  + stringLimit +    // 5  ProjectID1
                                           stringCostID        + stringLimit +      // 6  CostID
                                           stringCostID1  ;                      // 7  CostID1
            // 重複
            if(vectorKEY.indexOf(stringKey)  !=  -1) {
                return  doTable6ErrorAction(intNo,  "[業別] + [部門] + [案別] 資料重覆。") ;
            }
            vectorKEY.add(stringKey) ;
        }
        return  true ;
    }
    public  boolean  isTable6InputDeptCdOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable       jtable6                          =  getTable("Table6") ;
        String       stringInOut                    =  "" ;
        String       stringDepartNo             =  "" ;
        String     stringProjectID1            =  "" ;
        String       sringProjectIDComput  =  getProjectIDFromDepartNo(exeUtil,  exeFun) ;
        boolean   boolean053Start          =  getValue("DepartNo").startsWith("053") ;
        //
        System.out.println("sringProjectIDComput("+sringProjectIDComput+")------------------------------") ;
        if("".equals(sringProjectIDComput))  return true ;
        // 當為內業且有案別資料時，不允許
        for(int  intRowNo=0  ;  intRowNo<jtable6.getRowCount()  ;  intRowNo++) {
            stringDepartNo  =  (""  +  getValueAt("Table6",  intRowNo,  "DepartNo")).trim( ) ;
            stringProjectID1 =  (""  +  getValueAt("Table6",  intRowNo,  "ProjectID1")).trim( ) ;
            //
            if("".equals(stringProjectID1))  continue ;
            //
            if(boolean053Start) {
                if("0531".equals(stringDepartNo)  &&  sringProjectIDComput.indexOf(","+stringProjectID1+",")!=-1) return  true ;
            } else {
                if(!"0531".equals(stringDepartNo)  &&  sringProjectIDComput.indexOf(","+stringProjectID1+",")!=-1) return  true ;
            }
        }
        // 部門、案別分攤一致檢核
        return  doTable6ErrorAction(-1,  "部門不存在案別分攤中。") ;
    }
    public  String  getProjectIDFromDepartNo(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String    stringPurchaseNoExist  =  "N" ; //getValue("PurchaseNoExist").trim( ) ;
        String    stringComNo                 =  getValue("ComNo").trim() ;
        String    stringDepartNo             =  getValue("DepartNo").trim() ;
        String    stringBarCode               =  getValue("BarCode").trim() ;
        return  exeFun.getProjectIDFromDepartNo(stringDepartNo,  stringPurchaseNoExist,  stringBarCode,  stringComNo,  exeUtil) ;
    }
    public  boolean  isTable6033FGOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable               jTable6                                      =  getTable("Table6") ;
        String                stringInOut                                 =  "" ;
        String                stringDepart                              =  "" ;
        String                string033FGType                     =  "0" ;  // 2011-04-08 033FG 修正  0 未設定  1 033FG  2 非033FG  3 混用
        String                stringDocNo1                            =  getValue("DocNo1").trim() ;
        String             stringSpecBudget             =  ","+get("SPEC_BUDGET")+"," ;
        for(int  intNo=0  ;  intNo<jTable6.getRowCount()  ;  intNo++) {
            stringInOut                   =  (""  +  getValueAt("Table6",  intNo,  "InOut")).trim( ) ;
            stringDepart                 =  (""  +  getValueAt("Table6",  intNo,  "DepartNo")).trim( ) ;
            if("I".equals(stringInOut)) {
                if(stringSpecBudget.indexOf(stringDepart)  ==  -1) {
                    if("1".equals(string033FGType)){
                        return  doTable6ErrorAction(intNo,  stringDocNo1+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                    }
                    string033FGType  =  "2" ; 
                } else {
                    if(stringDocNo1.equals(stringDepart)) {
                        if("2".equals(string033FGType)){
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                        }
                        if(stringSpecBudget.indexOf(stringDepart)  ==  -1) {
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                        }
                        string033FGType  =  "1" ;
                    } else {
                        if("1".equals(string033FGType)){
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                        }
                        if(stringSpecBudget.indexOf(stringDepart)  !=  -1) {
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                        }
                        string033FGType  =  "2" ; 
                    }
                }
            } else {
                if("1".equals(string033FGType)){
                    return  doTable6ErrorAction(intNo,  stringDocNo1+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                }
                string033FGType  =  "2" ;
            }
            if("1".equals(string033FGType)){
                if(!stringDocNo1.equals(stringDepart)) {
                    return  doTable6ErrorAction(intNo,  "案別分攤分攤給 ["+stringDepart+"] 時，[公文編號] 須為 "+stringDocNo1+"。\n(有問題請洽 [財務室])") ;
                }
            }
            if("2".equals(string033FGType)){
                if(stringSpecBudget.indexOf(stringDocNo1)  !=  -1) {
                    return  doTable6ErrorAction(intNo,  "[公文編號] 為 "+stringDocNo1+"，案別分攤部門僅允許 "+stringDepart+"]。\n(有問題請洽 [財務室])") ;
                }
            }
        }
        return  true ;
    }
    public  boolean  isTable6CostIDOK(Vector  vectorDoc2M020,  int  intNo,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String               stringDocNo                  =  getValue("DocNo1").trim() ;
        String                stringInOut                 =  (""  +  getValueAt("Table6",  intNo,  "InOut")).trim( ) ;
        String                stringDepart                =  (""  +  getValueAt("Table6",  intNo,  "DepartNo")).trim( ) ;
        String                stringCostID               =  (""  +  getValueAt("Table6",  intNo,  "CostID")).trim( ) ;
        String                stringCostID1             =  (""  +  getValueAt("Table6",  intNo,  "CostID1")).trim( ) ;
        String                stringSpecBudget    =  ","+get("SPEC_BUDGET")+"," ;
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false表示可以無請購單  
        // 請款代碼不得為空白。
        if("".equals(stringCostID)) {
            return  doTable6ErrorAction(intNo,  "[請款代碼] 不得為空白。") ;
        }
        if("".equals(stringCostID1.trim( ))) {
            return  doTable6ErrorAction(intNo,  "[小請款代碼] 不得為空白。") ;
        }
        String    stringBarCode   =  getValue("BarCode").trim() ;
        String    stringComNo     =  getValue("ComNo").trim() ;
        String[][]  retDoc7M011  =  getDoc7M011(stringComNo,  "",  stringCostID,  stringCostID1,  exeFun) ;
        if(retDoc7M011.length  ==  0) {
            if(exeFun.getDoc2M021(stringCostID,  stringCostID1).indexOf(stringBarCode)  ==  -1) {
                return  doTable6ErrorAction(intNo,  "[小請款代碼] 不存在於 [費用-預算代碼-借方會計科目對照表(Doc2M020)] 中。\n(有問題請洽 [行銷管理室])") ;
            }
        }
        if(!booleanPurchaseExist) {
            if(vectorDoc2M020.indexOf(stringCostID  +  "-"  +  stringCostID1)  ==  -1) {
                return  doTable6ErrorAction(intNo,  "[請款代碼] 非可無請購單之 [請款代碼]。\n(有問題請洽 [行銷管理室])") ;
            }
        }
        
        if((","+stringSpecBudget+",").indexOf(stringDocNo)  ==  -1) {
            if(stringDocNo.startsWith("033")  ||  stringDocNo.startsWith("053")  ||  stringDocNo.startsWith("133")) {
                // 行銷 B2017-03-17 B2358 取消控管
                /*if("60,".indexOf(stringCostID)!=-1) {
                    return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 [行銷部室] 不允許使用 [租金]，不允許異動資料庫。\n(有問題請洽 [財務室])。") ;
                }*/
            } else {
                // 非行銷
                if("31,32,".indexOf(stringCostID)  !=  -1) {
                      return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 非[行銷部室] 不允許使用 [公績金][零用金]，不允許異動資料庫。\n(有問題請洽 [財務室])。") ;
                }
                if(exeUtil.doParseDouble(stringCostID)  >=  70) {
                    if(stringDocNo.startsWith("015")  &&  "721,".indexOf(stringCostID+stringCostID1)!=-1) {
                      // 特例允許
                    } else {
                        if(!"0333".equals(getValue("DocNo1"))) {
                            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 非[行銷部室] 不允許使用 70 之後的請款代碼，不允許異動資料庫。\n(有問題請洽 [財務室])。") ;
                        }
                    }
                  }
            }
        }
        // 請款代碼與內外業一致檢核
        if((","+stringSpecBudget+",03396,03335,033622,03363,0333,03365,").indexOf(","+stringDepart+",")==-1  &&  !exeFun.isInOutToCostID(stringInOut,  stringCostID)) {
            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 [請款代碼] 與 [內/外業] 不一致。\n(有問題請洽 [行銷管理室])") ;
        }
        if(stringSpecBudget.indexOf(stringDocNo)  !=  -1) {
            if(retDoc7M011.length==0  ||  !retDoc7M011[0][0].startsWith("B")) {
                return  doTable6ErrorAction(intNo,  "["+stringDocNo+"] 僅允許申請企劃類費用。\n(有問題請洽 [財務室])") ;
            }
        }
        // 
        String        stringPurchaseNoExist   =  getValue("PurchaseNoExist").trim() ;
        String               stringCDateAC                 =  exeUtil.getDateConvert(getValue("CDate").trim()) ;
        String        stringSqlAnd        =   " AND  FunctionType LIKE  '%4%' "+
                                    " AND  (DateStart='9999/99/99'  OR  DateStart<='"+stringCDateAC+"' )" +
                                    " AND  (DateEnd='9999/99/99'    OR  DateEnd>='"  +stringCDateAC+"' )" +
                                    " AND  ComNo  IN ('ALL',  '"  +stringComNo+"' )"+
                                    " AND  CostID  =  '"+stringCostID+"' " +
                                    " AND  CostID1  =  '"+stringCostID1+"' " ;
        if(!"Y".equals(stringPurchaseNoExist)) {
            Vector    vectorDoc2M0201   =  exeFun.getQueryDataHashtableDoc("Doc2M0201",  new  Hashtable(),  stringSqlAnd,  new  Vector(),  exeUtil) ;
            if(vectorDoc2M0201.size()  >  0) {
                  return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 [請款代碼][小請款代碼] 不允許使用。\n(有問題請洽 [行銷管理室])。") ;
            }
        }
        // 不得扣抵判斷
        JTable        jtable1                 =  getTable("Table1") ;
        String         stringFieldName  =  "I".equals(stringInOut) ?  "ACCT_IN" : "ACCT_OUT" ;
        String         stringAcctNo       =  "" ;
        Hashtable  hashtableAnd     =  new  Hashtable() ;
        hashtableAnd.put("ComNo",   stringComNo) ;
        hashtableAnd.put("CostID",    stringCostID) ;
        hashtableAnd.put("CostID1",  stringCostID1) ;
        stringAcctNo  =  exeFun.getNameUnionDoc(stringFieldName,  "Doc2M020",  "",  hashtableAnd,  exeUtil) ;
        if(",568128,620128,620114,568114,".indexOf(stringAcctNo)  !=  -1)  {
            if(jtable1.getRowCount()  >  0) {
                String   stringInvoiceKindB  =  (""+getValueAt("Table1",  0,  "InvoiceKind")).trim() ;
                if(!"D".equals(stringInvoiceKindB)) {
                    doTable6ErrorAction(intNo,  "費用表格之第 "+(intNo+1)+" 列 請款代碼適用於不得扣扺，與 [發票格式] 不同。\n(有問題請洽 [財務室])") ;
                }
            }
        }
        return  true ;
    }
      public  String[][]  getDoc7M011(String  stringComNo,  String  stringBudgetID,  String  stringCostID,  String  stringCostID1,  Doc2M010  exeFun) throws  Throwable {
        String      stringSql                   =  "" ;
        String      stringDescription           =  "" ;
        String[][]  retDoc7M011                 =  null ;
        // 0  BudgetID    1  CostID         2  CostID1        3  Description
        stringSql  =  "SELECT  DISTINCT  BudgetID,  CostID,  CostID1,  Description "  +
                   " FROM   Doc2M020 "  +
                  " WHERE  UseStatus  =  'Y' ";
        if(!"".equals(stringComNo))            stringSql  +=  " AND  ComNo  =  '"         +  stringComNo          +  "' " ;
        if(!"".equals(stringBudgetID))         stringSql  +=  " AND  BudgetID  =  '"      +  stringBudgetID       +  "' " ;
        if(!"".equals(stringCostID))           stringSql  +=  " AND  CostID  =  '"        +  stringCostID         +  "' " ;
        if(!"".equals(stringCostID1))         stringSql  +=  " AND  CostID1  =  '"       +  stringCostID1        +  "' " ;
        stringSql  +=  " ORDER BY BudgetID,  CostID,  CostID1 " ;
        retDoc7M011  =  exeFun.getTableDataDoc(stringSql) ;
        return  retDoc7M011 ;
      }
    public  boolean  isTable6DepartNoOK(String  stringExistDeptCd,  int  intNo,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String               stringDocNo                                   =  getValue("DocNo1").trim() ;
        String                stringInOut                                    =  (""  +  getValueAt("Table6",  intNo,  "InOut")).trim( ) ;
        String                stringDepart                                  =  (""  +  getValueAt("Table6",  intNo,  "DepartNo")).trim( ) ;
        String                stringProjectID                              =  (""  +  getValueAt("Table6",  intNo,  "ProjectID")).trim( ) ;
        String                stringProjectID1                           =  (""  +  getValueAt("Table6",  intNo,  "ProjectID1")).trim( ) ;
        String            stringSpecBudget                  =  ","+get("SPEC_BUDGET")+"," ;
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false表示可以無請購單  
        if(stringDocNo.indexOf("033FZ")!=-1  &&  !"0333".equals(stringDepart)) {
            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之 公文編號 033FZ 只能用 內業 0333 作分攤 。\n(有問題請洽 [人總室])") ;
        }
        if("".equals(stringDepart.trim( ))) {
            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之[部門] 不得為空白。") ;
        }
        if(exeUtil.doParseDouble(stringDepart)<=0  &&  stringSpecBudget.indexOf(stringDepart+",")==-1) {
            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之[部門] 只能為 4 個數字。\n(有問題請洽 [資訊企劃室])") ;
        }
        if("".equals(exeFun.getDepartName(stringDepart.trim( )))) {
            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之[部門] 不存在資料庫中。") ;
        }
        if("O".equals(stringInOut)) {
            if(",0331,1331,0531".indexOf(","+stringDepart+",")  ==  -1) {
                return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之 外業時，[部門] 只能為 [0331] 或 [1331] 或 [0531]。") ;
            }
            if(!"0531".equals(stringDepart.trim( ))) {
                String[][]  retAProject  =  exeFun.getTableDataSale("SELECT  ProjectID " +
                                                                " FROM  A_Project "  +
                                                                " WHERE  Depart  =  8 "  +
                                                                  " AND  ProjectID  IN(  '"  +  stringProjectID+"',  '"  +  stringProjectID1+"') "
                                                    ) ;
                if(!"1331".equals(stringDepart)  &&  retAProject.length>0) {
                    return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列此案別應為 1331，不允許異動資料庫。") ;
                } 
                if("1331".equals(stringDepart)  &&  retAProject.length==0) {
                    return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列此案別不應為 1331，不允許異動資料庫。") ;
                } 
            }
        }   else {
            if(!booleanPurchaseExist  &&  !"".equals(stringExistDeptCd)  &&  stringExistDeptCd.indexOf(exeUtil.doSubstring(stringDepart, 0, 3))  ==  -1) {
                return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 內業部門("+stringDepart.trim( )+")不存在於該公司，不允許異動資料庫。\n(有問題請洽 [財務室])") ;
            }
        }
        return  true ;
    }
    public  boolean  isTable6ProjectID1OK(int  intNo,  Hashtable  hashtable1331AProject,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String                stringComNo                                 =  getValue("ComNo").trim() ;
        String                stringInOut                                    =  (""  +  getValueAt("Table6",  intNo,  "InOut")).trim( ) ;
        String                stringDepart                                  =  (""  +  getValueAt("Table6",  intNo,  "DepartNo")).trim( ) ;
        String                stringProjectID                              =  (""  +  getValueAt("Table6",  intNo,  "ProjectID")).trim( ) ;
        String                stringProjectID1                            =  (""  +  getValueAt("Table6",  intNo,  "ProjectID1")).trim( );
        String                stringCostID                                  =  (""  +  getValueAt("Table6",  intNo,  "CostID")).trim( ) ;
        String                stringCostID1                                =  (""  +  getValueAt("Table6",  intNo,  "CostID1")).trim( ) ;
        String                stringCDateAC                             =  exeUtil.getDateConvert(getValue("CDate").trim()) ;
        String                stringDocNo1                                =  getValue("DocNo1").trim() ;
        String[][]            retDoc7M0265                              =  null ;
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false表示可以無請購單  
        // 當為內業且有案別資料時，不允許
        if("I".equals(stringInOut)) {
            if(!"".equals(stringProjectID)  ||  !"".equals(stringProjectID1)) {
                return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 選擇 [內業] 時，案別不可有資料。") ;
            }
            return  true ;
        }
        if("".equals(stringProjectID.trim( ))) {
            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之[大案別] 不得為空白。") ;
        }
        if("".equals(stringProjectID1.trim( ))) {
            return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之[小案別] 不得為空白。") ;
        }
        // 存在檢查
        if("0531".equals(stringDepart)) {
            String      stringSqlAnd   =  " AND  DateStart  <=  '"+stringCDateAC+"'  AND  DateEnd  >=  '"+stringCDateAC+"' " ;
            String[][]  retDoc2M051  =  exeFun.getDoc2M051(stringProjectID1,  "A",  stringSqlAnd) ;
            if(retDoc2M051.length  <=  0) {
                return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之 仲介案別 不存在於資料庫中。") ;
            }
        } else {
            if(!exeFun.isExistProjectIDCheck(stringProjectID,  stringProjectID1)) {
                return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列之[大案別] [小案別] 不存在於資料庫中。\n(有問題請洽 [行銷管理室])") ;
            }
        }
        if(!"0531".equals(stringDepart)) {
            String  stringDepart1    =  ""+hashtable1331AProject.get(stringProjectID) ;
            String  stringDepart2    =  ""+hashtable1331AProject.get(stringProjectID1) ;
            if(!"1331".equals(stringDepart)  &&  ("8".equals(stringDepart2)  ||  "8".equals(stringDepart1))) {
                return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列此案別應為 1331，不允許異動資料庫。") ;
            } 
        }
        if("990".equals(stringCostID+stringCostID1)) {
            String  stringProjectID1L =  stringProjectID1 ;
            if("0531".equals(stringDepart)) {
                stringProjectID1L  =  exeFun.get053ProjectID1Doc2M051(stringProjectID1L) ;
            }
            boolean  booleanTemp  =  stringDocNo1.indexOf(stringProjectID1L)!=-1  ||  exeUtil.isDigitNum (stringDocNo1)  ;
            if(!booleanTemp) {
                return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列此 [費用代碼] 為 990時，[借款單部門] 須與 部門一致。\n(有問題請洽 [財務室])") ;
            }
        }
        if(!booleanPurchaseExist) {
            retDoc7M0265  =  exeFun.getTableDataDoc("SELECT  AreaNum,  DateStart,  DateEnd,  ProjectIDMajor " +
                                                " FROM  Doc7M0265 "  +
                                                " WHERE  ComNo  =  '"  +  stringComNo  +  "' "  +
                                                  " AND  ProjectID1  =  '"  +  stringProjectID1+"' "
                                                ) ;
            if(retDoc7M0265.length >  0) {
                if(stringCDateAC.compareTo(retDoc7M0265[0][1].trim()) <0) {
                    return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 [案別]("+stringProjectID1+") 尚不允許使用。\n(有問題請洽 [財務室])") ;
                }
                if(stringCDateAC.compareTo(retDoc7M0265[0][2].trim())   >  0) {
                    return  doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 [案別]("+stringProjectID1+") 已不允許使用。\n(有問題請洽 [財務室])") ;
                }
            }
        }
        return  true ;
    }
    public  Hashtable  isTable6MoneyOK(int  intNo,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Hashtable  hashtableUsedRealMoney,  Hashtable  hashtableError) throws  Throwable {
        JTable jtable9                          =  getTable("Table9") ;
        String  stringInOut                   =  (""  +  getValueAt("Table6",  intNo,  "InOut")).trim( ) ;
        String  stringCostID                 =  (""  +  getValueAt("Table6",  intNo,  "CostID")).trim( ) ;
        String  stringCostID1               =  (""  +  getValueAt("Table6",  intNo,  "CostID1")).trim( ) ;
        String  stringDepart                 =  (""  +  getValueAt("Table6",  intNo,  "DepartNo")).trim( ) ;
        String  stringProjectID             =  (""  +  getValueAt("Table6",  intNo,  "ProjectID")).trim( ) ;
        String  stringProjectID1           =  (""  +  getValueAt("Table6",  intNo,  "ProjectID1")).trim( ) ;
        String  stringRealTotalMoney  =  (""  +  getValueAt("Table6",  intNo,  "RealTotalMoney")).trim( ) ;
        String  stringLimit                     =  "%---%" ;
        String  stringKey                      =  stringInOut          + stringLimit +    // 2  InOut
                                             stringDepart       + stringLimit +     // 3  DepartNo
                                             stringProjectID    + stringLimit +     // 4  ProjectID
                                               stringProjectID1  + stringLimit +    // 5  ProjectID1
                                               stringCostID        + stringLimit +      // 6  CostID
                                               stringCostID1  ;                      // 7  CostID1*/
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false表示可以無請購單  
        //
        hashtableError.put("STATUS",  "") ;
        // 費用金額
        if(exeUtil.doParseDouble(stringRealTotalMoney)  ==  0) {
            doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 [請款金額]不可為零或空白。") ;
            return  hashtableUsedRealMoney ;                
        }
        setValueAt("Table6",  stringRealTotalMoney,  intNo,  "RealTotalMoney") ;
        // 請購一致檢核
        System.out.println("請購一致檢核---------------------------------------S") ;
        if(jtable9.getRowCount()==0  &&  booleanPurchaseExist) {
            if(hashtableUsedRealMoney  ==  null) {
                  hashtableUsedRealMoney  =  getUsedProjectIDMoney2(exeFun,  exeUtil) ;
            }
            if(hashtableUsedRealMoney  ==  null) {
                doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 資料發生錯誤，請洽資訊室。") ;
                return  hashtableUsedRealMoney ;
            }
            double  doubleTemp  =  exeUtil.doParseDouble(""+hashtableUsedRealMoney.get(stringKey)) ;
              if(doubleTemp  <  exeUtil.doParseDouble(stringRealTotalMoney)) {
                doTable6ErrorAction(intNo,  "費用表格第 " +(intNo+1) +" 列 [資料庫中之案別分攤金額合計]("+exeUtil.getFormatNum2(stringRealTotalMoney)+") 大於 [請購申請書之案別分攤金額合計]("+exeUtil.getFormatNum2(""+doubleTemp)+")。\n請檢查 [部門] [案別] [請款代碼] 與請購申請書是否一致。") ;
                return  hashtableUsedRealMoney ;
              }
        }
        System.out.println("請購一致檢核---------------------------------------E") ;
        hashtableError.put("STATUS",  "OK") ;
        return  hashtableUsedRealMoney ;
    }
    // 已請款案別金額整理
    public  Hashtable  getUsedProjectIDMoney2(Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        Vector      vectorTable6Data   =  new  Vector() ;
        String       stringBarCode         =  getValue("BarCodeOld").trim() ;
        String       stringComNo           =  getValue("ComNo").trim() ;
        String[]     arrayTemp              =  new  String[7] ;
        JTable      jtable                      =  getTable("Table6") ;
        double     doubleRealMoney   =  0 ;
        for(int  intNo=0  ;  intNo<jtable.getRowCount()  ;  intNo++) {
            doubleRealMoney  +=  exeUtil.doParseDouble(""+getValueAt("Table6",  intNo,  "RealTotalMoney")) ;
        }
        arrayTemp[0]  =  getValue("PurchaseNo1").trim() ;
        arrayTemp[1]  =  getValue("PurchaseNo2").trim() ;
        arrayTemp[2]  =  getValue("PurchaseNo3").trim() ;
        arrayTemp[3]  =  "Z" ;
        arrayTemp[4]  =  convert.FourToFive(""+doubleRealMoney,  0) ;
        arrayTemp[5]  =  getValue("FactoryNo2").trim() ;
        arrayTemp[6]  =  "" ;
        vectorTable6Data.add(arrayTemp) ;
        
        return  exeFun.getUsedProjectIDMoney2(true,  stringComNo,  stringBarCode,  (String[][])  vectorTable6Data.toArray(new  String[0][0])) ;
    }
    public  boolean  isTable6CoinTypeCheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable                jTable6                                         =  getTable("Table6") ;
        String                stringComNo                                 =  getValue("ComNo").trim() ;
        String                stringInOut                                     = "" ;
        String                stringProjectID1                            = "" ;
        String                stringCoinTypeL                           = "" ;
        String                stringCoinType                             = "NTD" ;
        Vector                vectorProjectID1                          =  new  Vector() ;
        Hashtable         hashtableAnd                               =  new  Hashtable() ;
        //
        for(int  intNo=0 ;  intNo<jTable6.getRowCount( )  ;  intNo++) {
            stringInOut                =  (""+getValueAt("Table6",  intNo,  "InOut")).trim() ;
            stringProjectID1         =  (""+getValueAt("Table6",  intNo,  "ProjectID1")).trim() ;
            //
            if("I".equals(stringInOut)) {
                 stringCoinTypeL  =  "NTD" ;
            } else {
                if(vectorProjectID1.indexOf(stringProjectID1)  !=  -1)  continue ;
                vectorProjectID1.add(stringProjectID1) ;
                //
                hashtableAnd.put("ComNo",        stringComNo) ;
                hashtableAnd.put("ProjectID1",  stringProjectID1) ;
                stringCoinTypeL   =  exeFun.getNameUnionDoc("CoinType",  "Doc7M0204",  "",  hashtableAnd,  exeUtil) ;
                if("".equals(stringCoinTypeL)) {
                    hashtableAnd.put("ComNo",        stringComNo) ;
                    hashtableAnd.put("ProjectID1",  stringProjectID1) ;
                    stringCoinTypeL   =  exeFun.getNameUnionDoc("CoinType",  "Doc7M020",  "",  hashtableAnd,  exeUtil) ;
                }
                if("".equals(stringCoinTypeL))  stringCoinTypeL  =  "NTD" ;
            }
            //
            if(!"".equals(stringCoinTypeL)  &&  !"".equals(stringCoinType)  &&  !stringCoinType.equals(stringCoinTypeL)) {
                doTable6ErrorAction(-1,  "借款申請書 不允許多幣值。") ;
            }
            stringCoinType  =  stringCoinTypeL ;
        }
        //
        setValue("CoinType",  stringCoinType) ;
        return   true ;
    }
    public  boolean  isTable6SameTable17CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        // 案別 金額 一致檢核
        JTable      jtable6                 =  getTable("Table6") ;
        JTable      jtable17              =  getTable("Table17") ;
        String        stringInOut           =  "" ;
        String        stringDepartNo        =  "" ;
        String        stringProjectID1      =  "" ;
        String        stringRealTotalMoney  =  "" ;
        String        stringCostID            =  "" ;
        String        stringCostID1         =  "" ;
        String        stringKEY               =  "" ;
        Vector      vectorKEY6          =  new  Vector() ;
        Vector      vectorKEY17         =  new  Vector() ;
        Hashtable  hashtableMoney       =  new  Hashtable() ;
        double        doubleMoney6          =  0 ;
        double        doubleMoney17         =  0 ;
        for(int  intNo=0  ;  intNo<jtable6.getRowCount()  ;  intNo++) {
            stringInOut             =  (""+getValueAt("Table6",  intNo,  "InOut")).trim() ;
            stringDepartNo        =  (""+getValueAt("Table6",  intNo,  "DepartNo")).trim() ;
            stringProjectID1        =  (""+getValueAt("Table6",  intNo,  "ProjectID1")).trim() ;
            stringRealTotalMoney  =  (""+getValueAt("Table6",  intNo,  "RealTotalMoney")).trim() ;
            stringCostID            =  (""+getValueAt("Table6",  intNo,  "CostID")).trim() ;
            stringCostID1         =  (""+getValueAt("Table6",  intNo,  "CostID1")).trim() ;
            //
            stringKEY  =  ("I".equals(stringInOut)) ? stringDepartNo : stringProjectID1 ;
            stringKEY  =  stringKEY+"%-%"+stringCostID+"%-%"+stringCostID1 ;
            //
            if(vectorKEY6.indexOf(stringKEY)  ==  -1)  vectorKEY6.add(stringKEY) ;
            //
            doubleMoney6  =  exeUtil.doParseDouble(stringRealTotalMoney)  +  exeUtil.doParseDouble(""+hashtableMoney.get(stringKEY+"%-%Table6")) ;
            hashtableMoney.put(stringKEY+"%-%Table6",  convert.FourToFive(""+doubleMoney6,  0)) ;
        }
        for(int  intNo=0  ;  intNo<jtable17.getRowCount()  ;  intNo++) {
            stringInOut             =  (""+getValueAt("Table17",  intNo,  "InOut")).trim() ;
            stringDepartNo        =  (""+getValueAt("Table17",  intNo,  "DepartNo")).trim() ;
            stringProjectID1        =  (""+getValueAt("Table17",  intNo,  "ProjectID1")).trim() ;
            stringRealTotalMoney  =  (""+getValueAt("Table17",  intNo,  "PurchaseMoney")).trim() ;
            stringCostID            =  (""+getValueAt("Table17",  intNo,  "CostID")).trim() ;
            stringCostID1         =  (""+getValueAt("Table17",  intNo,  "CostID1")).trim() ;
            //
            stringKEY  =  ("I".equals(stringInOut)) ? stringDepartNo : stringProjectID1 ;
            stringKEY  =  stringKEY+"%-%"+stringCostID+"%-%"+stringCostID1 ;
            //
            if(vectorKEY6.indexOf(stringKEY)  ==  -1) {
                return  doTable6ErrorAction(-1,  "費用表格 與 請購項目案別分攤表格 品項資料不一致，請洽資訊室。1") ;
            }
            if(vectorKEY17.indexOf(stringKEY)  ==  -1)  vectorKEY17.add(stringKEY) ;
            //
            doubleMoney17  =  exeUtil.doParseDouble(stringRealTotalMoney)  +  exeUtil.doParseDouble(""+hashtableMoney.get(stringKEY+"%-%Table17")) ;
            hashtableMoney.put(stringKEY+"%-%Table17",  convert.FourToFive(""+doubleMoney17,  0)) ;
        }
        if(vectorKEY6.size()  !=  vectorKEY17.size()) {
            return  doTable6ErrorAction(-1,  "費用表格 與 請購項目案別分攤表格 品項資料不一致，請洽資訊室。2") ;
        }
        for(int  intNo=0  ;  intNo<vectorKEY6.size()  ;  intNo++) {
            stringKEY  =  ""+vectorKEY6.get(intNo) ;
            //
            doubleMoney6    =  exeUtil.doParseDouble(""+hashtableMoney.get(stringKEY+"%-%Table6")) ;
            doubleMoney6    =  exeUtil.doParseDouble(convert.FourToFive(""+doubleMoney6,  0)) ;
            doubleMoney17  =  exeUtil.doParseDouble(""+hashtableMoney.get(stringKEY+"%-%Table17")) ;
            doubleMoney17  =  exeUtil.doParseDouble(convert.FourToFive(""+doubleMoney17,  0)) ;
            //
            if(doubleMoney6  !=  doubleMoney17) {
                return  doTable6ErrorAction(-1,  "費用表格 與 請購項目案別分攤表格 金額資料不一致，請洽資訊室。3") ;
            }
        }
        return  true ;
    }
    public  boolean  isTable17CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable      jtable17              =  getTable("Table17") ;
        String        stringTemp            =  "" ;
        String      stringBarCodePur    =  "" ;
        Hashtable  hashtableAnd           =  new  Hashtable() ;
        Hashtable  hashtableBarCodePur  =  new  Hashtable() ;
        Hashtable  hashtableTable17Data =  new  Hashtable() ;
        double        doublePurchaseMoney   =  0 ;
        double        doubleRequestMoney   =  0 ;
        double        doubleTemp          =  0 ;
        // 請購項目之案別分攤 金額一致檢核
        for(int  intNo=0  ;  intNo<jtable17.getRowCount()  ;  intNo++) {
            hashtableTable17Data  =  getTable17DataHashtable(intNo) ;
            // 請購金額 
            stringBarCodePur         =  getBarCodePur(hashtableTable17Data,  exeUtil,  exeFun,  hashtableBarCodePur) ;hashtableTable17Data.put("BarCode",  stringBarCodePur) ;
            System.out.println(intNo+"stringBarCodePur("+stringBarCodePur+")----------------------------------") ;
            doublePurchaseMoney     =  getPurchaseMoneyDoc3M0123(hashtableTable17Data,   exeUtil,  exeFun) ;
            // 請款金額-請款 Doc2M0172
            doubleTemp             =  getRequestMoneyDoc2M0172(hashtableTable17Data,   exeUtil,  exeFun) ;
            doubleRequestMoney    =   doubleTemp ;
            // 請款金額-借款-新版 Doc6M0172
            // 請款金額-借款沖銷-舊版 Doc6M0172
            doubleTemp            =  getRequestMoneyDoc6M0172(hashtableTable17Data,   exeUtil,  exeFun) ;
            doubleRequestMoney  +=    doubleTemp ;
            //
            doublePurchaseMoney  =  exeUtil.doParseDouble(convert.FourToFive(""+doublePurchaseMoney,  0)) ;
            doubleRequestMoney    =  exeUtil.doParseDouble(convert.FourToFive(""+doubleRequestMoney,  0)) ;
            if(doublePurchaseMoney  <  doubleRequestMoney) {
                String        stringInOut                   =  ""+hashtableTable17Data.get("InOut") ;
                if("I".equals(stringInOut)) {
                    stringTemp  =  "部門("+hashtableTable17Data.get("DepartNo")+")"  ;
                } else {
                    stringTemp  =  "案別("+hashtableTable17Data.get("ProjectID1")+")"  ;
                }
                stringTemp  +=  "請款代碼("+hashtableTable17Data.get("CostID")+hashtableTable17Data.get("CostID1")+")" ;
                return  doTable6ErrorAction(-1,  "請購項目之案別分攤表格 "+stringTemp+"已使用金額("+exeUtil.getFormatNum2(""+doubleRequestMoney)+") 大於 可用預算金額("+exeUtil.getFormatNum2(""+doublePurchaseMoney)+")。") ;
            }
        }
        return  true ;
    }
    public  Hashtable  getTable17DataHashtable(int  intRowNo) throws  Throwable {
        Hashtable  hashtableTable17Data  =  new  Hashtable() ;
        String         stringFieldName           =  "" ;
        String         stringFieldValue           =  "" ;
        String[]       arrayFieldName           =  {"InOut",        "DepartNo",           "ProjectID",        "ProjectID1",  "CostID",  
                                                                     "CostID1",     "PurchaseMoney",    "PurchaseNo",     "RecordNo12"} ;
        for(int  intNo=0  ;  intNo<arrayFieldName.length  ;  intNo++) {
            stringFieldName  =  arrayFieldName[intNo].trim() ;
            stringFieldValue  =  (""+getValueAt("Table17",  intRowNo,  stringFieldName)).trim() ;
            //
            System.out.println("stringFieldName("+stringFieldName+")stringFieldValue("+stringFieldValue+")-------------------------------") ;
            hashtableTable17Data.put(stringFieldName,  stringFieldValue) ;
        }
        return  hashtableTable17Data ;
    }
    public  String  getBarCodePur(Hashtable  hashtableTable17Data,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Hashtable  hashtableBarCodePur) throws  Throwable {
        String  stringPurchaseNo  =  (""+hashtableTable17Data.get("PurchaseNo")).trim() ;
        String  stringBarCodePur  =  ""+hashtableBarCodePur.get(stringPurchaseNo) ;
        //
        if(!"null".equals(stringBarCodePur))  return  stringBarCodePur ;
        //
        String      stringComNo       =  getValue("ComNo").trim() ;
        String      stringKindNo        =  getValue("KindNo").trim() ;
        Hashtable  hashtableAnd           =  new  Hashtable() ;
        //
        if("24".equals(stringKindNo))  stringKindNo  =  "17" ;
        if("26".equals(stringKindNo))  stringKindNo  =  "17" ;
        if("23".equals(stringKindNo))  stringKindNo  =  "15" ;
        //
        hashtableAnd.put("ComNo",  stringComNo) ;
        hashtableAnd.put("KindNo",  stringKindNo) ;
        hashtableAnd.put("DocNo",   stringPurchaseNo) ;
        stringBarCodePur  =  exeFun.getNameUnionDoc("BarCode",  "Doc3M011",  "",  hashtableAnd,  exeUtil) ;
        hashtableBarCodePur.put(stringPurchaseNo,  stringBarCodePur) ;
        return  stringBarCodePur ;
    }
    public  double  getPurchaseMoneyDoc3M0123(Hashtable  hashtableTable17Data,   FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String        stringBarCodePur          =  ""+hashtableTable17Data.get("BarCode") ;
        String        stringRecordNo             =  ""+hashtableTable17Data.get("RecordNo12") ;
        String        stringInOut                   =  ""+hashtableTable17Data.get("InOut") ;
        String        stringDepartNo             =  ""+hashtableTable17Data.get("DepartNo") ;
        String        stringProjectID             =  ""+hashtableTable17Data.get("ProjectID") ;
        String        stringProjectID1             =  ""+hashtableTable17Data.get("ProjectID1") ;
        String        stringCostID                  =  ""+hashtableTable17Data.get("CostID") ;
        String        stringCostID1               =  ""+hashtableTable17Data.get("CostID1") ;
        String        stringSql                        =  "" ;
        String[][]    retDoc3M0123               =  null ;
        //
        stringSql            =  " SELECT  SUM(M123.PurchaseMoney - M123.NoUseRealMoney)"  +
                                        " FROM  Doc3M012 M12,  Doc3M0123 M123 "  +
                        " WHERE  M12.BarCode    =  M123.BarCode "  +
                             " AND  M12.RecordNo  =  M123.RecordNo "  +
                           " AND  M12.BarCode  =  '"    +stringBarCodePur   +"' "  +
                           " AND  M12.RecordNo  =  "  +stringRecordNo     +" "  +
                           " AND  M12.CostID  =  '"       +stringCostID          +"' "  +
                           " AND  M12.CostID1  =  '"      +stringCostID1        +"' "  +
                           " AND  M123.InOut  =  '"       +stringInOut            +"' "  +
                           " AND  M123.DepartNo  =  '"  +stringDepartNo     +"' "  +
                           " AND  M123.ProjectID  =  '"  +stringProjectID      +"' "  +
                           " AND  M123.ProjectID1  =  '"+stringProjectID1    +"' "  ;
        retDoc3M0123  =  exeFun.getTableDataDoc(stringSql) ;
        return  exeUtil.doParseDouble(retDoc3M0123[0][0]) ;
    }
    public  double  getRequestMoneyDoc2M0172(Hashtable  hashtableTable17Data,   FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String        stringRecordNo             =  ""+hashtableTable17Data.get("RecordNo12") ;
        String        stringInOut                   =  ""+hashtableTable17Data.get("InOut") ;
        String        stringDepartNo             =  ""+hashtableTable17Data.get("DepartNo") ;
        String        stringProjectID             =  ""+hashtableTable17Data.get("ProjectID") ;
        String        stringProjectID1             =  ""+hashtableTable17Data.get("ProjectID1") ;
        String        stringCostID                  =  ""+hashtableTable17Data.get("CostID") ;
        String        stringCostID1               =  ""+hashtableTable17Data.get("CostID1") ;
        String       stringPurchaseNo       =  (""+hashtableTable17Data.get("PurchaseNo")).trim() ;
        String        stringSql                        =  "" ;
        String        stringBarCode               =  getValue("BarCode").trim() ;
        String      stringComNo       =  getValue("ComNo").trim() ;
        String      stringKindNo        =  getValue("KindNo").trim() ;
        String[][]    retDoc2M0172               =  null ;
        //
        stringSql            =  " SELECT  SUM(M172.PurchaseMoney)"  +
                                        " FROM  Doc2M010 M10,  Doc2M0172 M172 "  +
                        " WHERE  M10.BarCode    =  M172.BarCode "  +
                           " AND  M10.BarCode  <>  '"       +stringBarCode       +"' "  +
                           " AND  M10.ComNo  =  '"          +stringComNo           +"' "  +
                           " AND  M10.KindNo  =  '"           +stringKindNo           +"' "  +
                           " AND  M172.PurchaseNo  =  '"      +stringPurchaseNo   +"' "  +
                           " AND  M172.RecordNo12  =   "      +stringRecordNo      +"  "  +
                           " AND  M172.InOut  =  '"           +stringInOut              +"' "  +
                           " AND  M172.DepartNo  =  '"      +stringDepartNo     +"' "  +
                           " AND  M172.ProjectID  =  '"         +stringProjectID      +"' "  +
                           " AND  M172.ProjectID1  =  '"      +stringProjectID1    +"' "  +
                           " AND  M172.CostID  =  '"            +stringCostID          +"' "  +
                           " AND  M172.CostID1  =  '"           +stringCostID1        +"' "  ;
        retDoc2M0172  =  exeFun.getTableDataDoc(stringSql) ;
        return  exeUtil.doParseDouble(retDoc2M0172[0][0]) ;
    }
    public  double  getRequestMoneyDoc6M0172(Hashtable  hashtableTable17Data,   FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String        stringRecordNo             =  ""+hashtableTable17Data.get("RecordNo12") ;
        String        stringInOut                   =  ""+hashtableTable17Data.get("InOut") ;
        String        stringDepartNo             =  ""+hashtableTable17Data.get("DepartNo") ;
        String        stringProjectID             =  ""+hashtableTable17Data.get("ProjectID") ;
        String        stringProjectID1             =  ""+hashtableTable17Data.get("ProjectID1") ;
        String        stringCostID                  =  ""+hashtableTable17Data.get("CostID") ;
        String        stringCostID1               =  ""+hashtableTable17Data.get("CostID1") ;
        String       stringPurchaseNo       =  (""+hashtableTable17Data.get("PurchaseNo")).trim() ;
        String        stringSql                        =  "" ;
        String        stringBarCode               =  getValue("BarCode").trim() ;
        String      stringComNo       =  getValue("ComNo").trim() ;
        //String      stringKindNo        =  getValue("KindNo").trim() ;
        String[][]    retDoc6M0172               =  null ;
        //
        stringSql            =  " SELECT  SUM(M172.PurchaseMoney-ISNULL(NoUsePurchaseMoney,  0))"  +
                                        " FROM  Doc6M010 M10,  Doc6M0172 M172 "  +
                        " WHERE  M10.BarCode    =  M172.BarCode "  +
                           " AND  M10.PurchaseNoExist  =  'Y' "  +
                           " AND  M10.BarCode  <>  '"       +stringBarCode       +"' "  +
                           " AND  M10.ComNo  =  '"          +stringComNo           +"' "  +
                           //" AND  M10.KindNo  =  '"           +stringKindNo           +"' "  +
                           " AND  M172.PurchaseNo  =  '"      +stringPurchaseNo   +"' "  +
                           " AND  M172.RecordNo12  =   "      +stringRecordNo      +"  "  +
                           " AND  M172.InOut  =  '"           +stringInOut              +"' "  +
                           " AND  M172.DepartNo  =  '"      +stringDepartNo     +"' "  +
                           " AND  M172.ProjectID  =  '"         +stringProjectID      +"' "  +
                           " AND  M172.ProjectID1  =  '"      +stringProjectID1    +"' "  +
                           " AND  M172.CostID  =  '"            +stringCostID          +"' "  +
                           " AND  M172.CostID1  =  '"           +stringCostID1        +"' "  ;
        retDoc6M0172  =  exeFun.getTableDataDoc(stringSql) ;
        return  exeUtil.doParseDouble(retDoc6M0172[0][0]) ;
    }
    
    public  boolean  isMoneyCheckOK(Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        JTabbedPane   jtabbedPane1                         =  getTabbedPane("Tab1") ;
        JTable               jtable1                                     =  getTable("Table1") ; // 發票
        JTable               jtable2                                     =  getTable("Table2") ; // 扣繳
        int                      intTable2Panel                        =  3 ;
        int                     intTable1Panel                         =  2 ;
        String               stringPayType                          =  getValue("PayType").trim( ) ;
        double             doubleRealMoneySum              =  exeUtil.doParseDouble(getValue("RealMoneySum").trim( )) ;
        double             doubleInvoiceTotalMoneySum  =  exeUtil.doParseDouble(getValue("InvoiceTotalMoneySum").trim( )) ;
        double             doubleServiceAMT                   =  exeUtil.doParseDouble(getValue("ServiceAMT").trim()) ;
        double             doubleReceiptTotalSum           =  exeUtil.doParseDouble(getValue("ReceiptSum").trim( )) ;
        if("A".equals(stringPayType)) {
            // 個人收據
            setTableData("Table1",  new  String[0][0]) ;    // 發票
            setValue("BorrowMoney",  convert.FourToFive(""+doubleReceiptTotalSum,0)) ;
            if(doubleReceiptTotalSum <  0) {
                messagebox(" [所得總金額合計] 須大於零。") ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                return  false ;
            }
            if(doubleServiceAMT>0  &&  doubleServiceAMT  !=  doubleReceiptTotalSum) {
                messagebox(" [所得總金額合計] 須等於 [酬庸總金額]。") ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                return  false ;
            }
            if(doubleRealMoneySum  !=  doubleReceiptTotalSum) {
                messagebox(" [所得總金額合計] 須等於 [費用金額合計]。") ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                return  false ;
            }
        } else if("B".equals(stringPayType)) {
            //發票
            setTableData("Table2",  new  String[0][0]) ;    // 扣繳
            setValue("BorrowMoney",  convert.FourToFive(""+doubleInvoiceTotalMoneySum,0)) ;
            if(doubleInvoiceTotalMoneySum <  0) {
                messagebox(" [發票總金額合計] 須大於零。") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
            if(doubleServiceAMT>0  &&  doubleServiceAMT!=doubleInvoiceTotalMoneySum) {
                messagebox(" [發票總金額合計] 須等於 [酬庸總金額]。") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
            if(doubleRealMoneySum!=doubleInvoiceTotalMoneySum) {
                messagebox(" [發票總金額合計] 須等於 [費用金額合計]。") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
        } else {
            setTableData("Table1",  new  String[0][0]) ;    // 發票
            setTableData("Table2",  new  String[0][0]) ;    // 扣繳
            // 借款金額
            String  stringBorrowMoney  =  getValue("BorrowMoney").trim() ;
            if("".equals(stringBorrowMoney)  ||  exeUtil.doParseDouble(stringBorrowMoney) == 0) {
                messagebox("[借款金額] 不可為空白。") ;
                getcLabel("BorrowMoney").requestFocus( ) ;
                return  false ;
            }
            if(doubleRealMoneySum  !=  exeUtil.doParseDouble(stringBorrowMoney)) {
                messagebox(" [借款金額] 須等於 [費用金額合計]。") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
        }
        // 請購資訊項目 Table 9
        System.out.println("請購資訊項目---------------------------------------------S") ;
        if(!isTable9CheckOK(exeUtil,  exeFun))  return  false ;
        System.out.println("請購資訊項目---------------------------------------------E") ;
        // 預算檢核
        if(!isBudgetMoneyTable6OK(exeUtil,  exeFun))            return  false ;
        return  true ;
    }
    public  boolean  isApplyTypeDVoucher2(Doc2M010  exeFun) throws  Throwable {
        String         stringComNo                      =  getValue("ComNo"); 
        String         stringKindNo                      =  getValue("KindNo"); 
        String        stringPurchaseNo1             =  getValue("PurchaseNo1").trim() ;
        String        stringPurchaseNo2             =  getValue("PurchaseNo2").trim() ;
        String       stringPurchaseNo3             =  getValue("PurchaseNo3").trim() ;
        String        stringPurchaseNo               =  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3 ;
        String       stringFactoryNo                  =  getValue("FactoryNo2").trim() ;
        String        stringPurchaseNoExist       =  getValue("PurchaseNoExist").trim() ;
        if(stringPurchaseNoExist.startsWith("N"))  return  false ;
        return  exeFun.isApplyTypeDVoucher2("C",  stringComNo,  stringPurchaseNo,  stringKindNo,  stringFactoryNo) ;
    }
    public  boolean  isAssetOK(Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        getButton("ButtonTable9").doClick() ;   // 本次請款單之[請購明細項目]之金額和未稅金額 合計須一致 費用表格之金額合計。(自動處理)
        //
        String         stringComNo                      =  getValue("ComNo"); 
        //String         stringKindNo                      =  getValue("KindNo"); 
        String        stringPurchaseNo1             =  getValue("PurchaseNo1").trim() ;
        String        stringPurchaseNo2             =  getValue("PurchaseNo2").trim() ;
        String       stringPurchaseNo3             =  getValue("PurchaseNo3").trim() ;
        String        stringPurchaseNo               =  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3 ;
        //String       stringFactoryNo                  =  getValue("FactoryNo2").trim() ;
        //
        if(!isApplyTypeDVoucher2(exeFun)) return  true ;
        //
        if(getTableData("Table2").length  >  0) {
            messagebox("[請購單] 為固定資產時，憑證 只允許 [發票]。") ;
            return  false ;
        }
        talk           dbAsset                         =  getTalk(""+get("put_Asset")) ;
        String[][]  retAsAsset                    =  getAsAsset(exeUtil,  exeFun,  dbAsset) ;
        boolean   booleanExistAsset        =  (retAsAsset.length  >  0) ;
        if(booleanExistAsset) {
            messagebox("[請購單-廠商] 已存在 固資系統時，不允許新增 請款申請書。") ;
            return  false ;           
        }
        // 0 列帳 Y     1 固資代碼      2. 合計金額     3. 未稅金額     4  內外業
        String        stringAssAccountAsset    =  "" ;
        String        stringComNoAcctNo        =  "" ;
        String        stringFilter                       =  "" ;
        String        stringInOut                      =  "" ;
        String        stringFiletrDo                  =  exeFun.getNameUnionDoc("Remark",  "Doc2M0401",  " AND  UseType  =  'U' ",  new  Hashtable(),  exeUtil) ;
        String        stringTemp                      =  "" ;
        String[][]    retAsAssetDoc                =  getAsAssetDoc(exeFun,  exeUtil) ;
        Vector        vectorAsAssetFilter       =  new  Vector() ;
        Vector        vectorColumnName        =  new  Vector() ;
        Hashtable  hashtableTmp                =  new  Hashtable() ;
        for(int  intNo=0  ;  intNo<retAsAssetDoc.length  ;  intNo++) {
            stringAssAccountAsset           =  retAsAssetDoc[intNo][0].trim() ;
            stringFilter                              =  retAsAssetDoc[intNo][1].trim() ;
            stringInOut                             =  retAsAssetDoc[intNo][4].trim() ;
            // [固資代碼] 對應 公司-會計科目存在檢核。
            vectorAsAssetFilter  =  exeUtil.getQueryDataHashtable("AS_ASSET_FILTER",  new  Hashtable(),  " AND  FILTER  = '"+stringFilter+"' ",  vectorColumnName,  dbAsset) ;
            if(vectorAsAssetFilter.size()  ==  0) {
                messagebox("請購單("+stringPurchaseNo+")之[固資代碼]("+stringFilter+") 不存在資料庫中。") ;
                return  false ;
            }
            hashtableTmp  =  (Hashtable) vectorAsAssetFilter.get(0) ;
            if(hashtableTmp  ==  null) {
                messagebox("請購單("+stringPurchaseNo+")之[固資代碼]("+stringFilter+") 資料發生錯誤，請洽資訊室。") ;
                return  false ;
            }
            if(!stringFilter.equals(stringFiletrDo)) {
                if("Y".equals(stringAssAccountAsset)) {
                    // 會計科目存在檢核
                    stringComNoAcctNo  =  ""+hashtableTmp.get("ANMAL_ACNTNO_SET") ;
                    if("null".equals(stringComNoAcctNo)  ||  "".equals(stringComNoAcctNo)) {
                        messagebox("請購單("+stringPurchaseNo+")之[固資代碼]("+stringFilter+") 對應 [列帳-會計科目] 為空白。") ;
                        return  false ;
                    }
                } else {
                    // 對應公司欄位欄位檢核
                    stringTemp  =  "SPEC_ACNTNO_SET_"+stringComNo ;
                    if("I".equals(stringInOut))  stringTemp  =  "SPEC_ACNTNO_SET_"+stringComNo+"_IN" ;
                    if(vectorColumnName.indexOf(stringTemp)  ==  -1) {
                        messagebox("請購單("+stringPurchaseNo+")之[固資代碼]("+stringFilter+") 不存在對應 [公司-會計科目] 中。") ;
                        return  false ;
                    }
                    // 會計科目存在檢核
                    stringComNoAcctNo  =  ""+hashtableTmp.get(stringTemp) ;
                    if("null".equals(stringComNoAcctNo)  ||  "".equals(stringComNoAcctNo)) {
                        messagebox("請購單("+stringPurchaseNo+")之[固資代碼]("+stringFilter+") 對應 [公司-會計科目] 為空白。") ;
                        return  false ;
                    }
                }
            }
        }
        return  true ;
    }
    public  String[][]  getAsAsset(FargloryUtil  exeUtil,  Doc2M010  exeFun,  talk  dbAsset) throws  Throwable {
        String      stringComNo            =  getValue("ComNo").trim() ;
        String      stringKindNo            =  getValue("KindNo").trim() ;
        String      stringPurchaseNo1  =  getValue("PurchaseNo1").trim() ;
        String      stringPurchaseNo2  =  getValue("PurchaseNo2").trim() ;
        String      stringPurchaseNo3  =  getValue("PurchaseNo3").trim() ;
        String      stringPurchaseNo    =  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3 ;
        String      stringFactoryNo       =  getValue("FactoryNo2").trim() ;
        return  exeFun.getAsAssetUnit(stringComNo,  stringKindNo,  stringPurchaseNo,  stringFactoryNo,  exeUtil,  dbAsset) ;
    }
    public  String[][]  getAsAssetDoc(Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        JTable        jtable9                         =  getTable("Table9") ;
        String         stringRecordNo12       =  "" ;
        String         stringPurchaseNo        =  "" ;
        String         stringSql                      =  "" ;
        String         stringComNo                =  getValue("ComNo").trim() ;
        String         stringKindNo                 =  getValue("KindNo").trim() ;
        String        stringKindNoPurchase  =  stringKindNo ;
        String        stringFilter                     = "" ;             
        String        stringInOut                    = "" ;             
        String[]      arrayTemp                    =  null ;
        String[][]     retAsAsset                   =  new  String[0][0] ;
        String[][]     retDoc3M012               =  null ;
        Vector        vectorKEY                   =  new  Vector() ;
        Vector        vectorTemp                 =  new  Vector() ;
        //
        if("23".equals(stringKindNoPurchase))  stringKindNoPurchase  =  "15" ;
        if("24".equals(stringKindNoPurchase))  stringKindNoPurchase  =  "17" ;
        for(int  intNo=0  ;  intNo<jtable9.getRowCount()  ;  intNo++) {
            stringRecordNo12        =  (""+getValueAt("Table9",  intNo,  "RecordNo12")).trim() ;
            stringPurchaseNo         =  (""+getValueAt("Table9",  intNo,  "PurchaseNo")).trim() ;
            //
            if(vectorKEY.indexOf(stringPurchaseNo+"---"+stringRecordNo12)  !=  -1)continue ;
            vectorKEY.add(stringPurchaseNo+"---"+stringRecordNo12) ;
            // 取得 請購之固資代碼
            stringSql         =  " SELECT  M12.FILTER "  +
                           " FROM  Doc3M012 M12,  Doc3M011 M11 "  +
                           " WHERE  M12.BarCode  =  M11.BarCode "  +
                             " AND  M11.ComNo  =  '"    +   stringComNo                +  "' "  +
                             " AND  M11.DocNo  =  '"      +  stringPurchaseNo         +  "' "  +
                             " AND  M11.KindNo  =  '"     +  stringKindNoPurchase  +  "' "  +
                             " AND  M12.RecordNo  =  "  +  stringRecordNo12        +  " "  ;
            retDoc3M012  =  exeFun.getTableDataDoc(stringSql) ;
            if(retDoc3M012.length  ==  0) {
                System.out.println("檢查 請購單之固資代碼 時，發生錯誤，請洽資訊室。") ;
                return  new  String[0][0] ;
            }
            stringFilter    =  retDoc3M012[0][0].trim() ;
            stringInOut   =  exeFun.getInOutVoucher("C",  stringRecordNo12,  stringComNo,  stringKindNoPurchase,  stringPurchaseNo,  exeUtil) ;
            if("".equals(stringFilter))  continue ;
            // 0 列帳 Y     1 固資代碼      2. 合計金額     3. 未稅金額     4  內外業
            arrayTemp      =  new  String[5] ;
            arrayTemp[0]  =  "N" ;
            arrayTemp[1]  =  stringFilter ;
            arrayTemp[2]  =  "" ;
            arrayTemp[3]  =  "" ;
            arrayTemp[4]  =  stringInOut ;
            vectorTemp.add(arrayTemp) ;
        }
        retAsAsset  =  (String[][])  vectorTemp.toArray(new  String[0][0]) ;
        return  retAsAsset ;
    }
    public  boolean  isTable6SameTable22CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable      jtable22                  =  getTable("Table22") ;
        String      stringSSMediaID           =  "" ;
        String      stringRecordNo            =  "" ;
        Vector        vectorRecordNo          =  new  Vector() ;
        double      doubleRealTotalMoney6     =  0 ;
        double      doubleRealTotalMoney22     =  0 ;
        //
        for(int  intNo=0  ;  intNo<jtable22.getRowCount()  ;  intNo++) {
            stringSSMediaID   =  (""+getValueAt("Table22",  intNo,  "SSMediaID")).trim() ;
            stringRecordNo  =  (""+getValueAt("Table22",  intNo,  "RecordNo")).trim() ;
            if("".equals(stringSSMediaID)  ||  "　".equals(stringSSMediaID)) {
                getButton("ButtonTableElse").doClick() ;
                return  doTable6ErrorAction(-1,  "費用對照通路代碼表格 第 "+(intNo+1)+" 列之 [通路代碼]不可為空白。") ;
            }
            if(vectorRecordNo.indexOf(stringRecordNo)  !=  -1)  continue ;
            vectorRecordNo.add(stringRecordNo) ;
            // 費用表格 金額一致檢核
            doubleRealTotalMoney6    =  getTable6MoneySum (stringRecordNo,  exeUtil) ;
            doubleRealTotalMoney22  =  getTable22MoneySum (stringRecordNo,  exeUtil) ;
            System.out.println("doubleRealTotalMoney2("+convert.FourToFive(""+doubleRealTotalMoney6,  0)+")doubleRealTotalMoney22("+convert.FourToFive(""+doubleRealTotalMoney22,  0)+")-----------------------------------") ;
            if(doubleRealTotalMoney6  !=  doubleRealTotalMoney22) {
                getButton("ButtonTableElse").doClick() ;
                return  doTable6ErrorAction(-1,  "費用對照通路代碼表格 第 "+(intNo+1)+" 列之費用表格對應金額合計("+exeUtil.getFormatNum2(""+doubleRealTotalMoney6)+") 不一致。(表格合計："+exeUtil.getFormatNum2(""+doubleRealTotalMoney22)+")。") ;
            }
        }
        return  true ;
    }
    public  double  getTable6MoneySum (String  stringRecordNoCF,  FargloryUtil  exeUtil) throws  Throwable{
        JTable    jtable6                 =  getTable("Table6") ;
        String    stringRealTotalMoney     =  (""+getValueAt("Table6",  exeUtil.doParseInteger(stringRecordNoCF)-1,  "RealTotalMoney")).trim() ;
        return  exeUtil.doParseDouble(stringRealTotalMoney) ;
    }
    public  double  getTable22MoneySum (String  stringRecordNoCF,  FargloryUtil  exeUtil) throws  Throwable{
        JTable    jtable22                =  getTable("Table22") ;
        String    stringRecordNo          =  "" ;
        String    stringRealTotalMoney     =  "" ;
        double    doubleRealTotalMoney    =  0 ;
        for(int  intNo=0  ;  intNo<jtable22.getRowCount()  ;  intNo++) {
            stringRecordNo        =  (""+getValueAt("Table22",  intNo,  "RecordNo")).trim() ;
            stringRealTotalMoney  =  (""+getValueAt("Table22",  intNo,  "RealTotalMoney")).trim() ;
            //
            if(!stringRecordNoCF.equals(stringRecordNo))  continue ;
            //
            doubleRealTotalMoney  +=  exeUtil.doParseDouble(stringRealTotalMoney) ;
        }
        return  exeUtil.doParseDouble(convert.FourToFive(""+doubleRealTotalMoney,  0)) ;
    }
    
    // 請購項目表格
    public  boolean  isTable9CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        // 無請購單 清空
        String  stringPurchaseNoExist  =  getValue("PurchaseNoExist").trim() ;
        if("N".equals(stringPurchaseNoExist)) {
            setTableData("Table9",  new  String[0][0]) ;
            return  true ;
        }
        if(!isNewVersion (exeFun)) {
            setTableData("Table9",  new  String[0][0]) ;
            return  true ;        
        }
        // 與 費用表格 金額一致
        double    doubleRealTotalMoneySum        =  getTableMoneySum("Table6",  "RealTotalMoney",  exeUtil) ;   
        double    doublePurchaseMoneySum        =  getTableMoneySum("Table9",  "PurchaseMoney",  exeUtil) ;   
        if(doubleRealTotalMoneySum  !=  doublePurchaseMoneySum) {
            messagebox("[請購項目表格]金額合計 不等於 [費用表格]金額合計。") ;
            return  false ;
        }
        getButton("ButtonTable9").doClick() ;//Table9 無稅金額(自動給值)
        return  true ;
    }
    public  double  getTableMoneySum(String  stringTable,  String  stringFieldName,  FargloryUtil  exeUtil) throws  Throwable {
        JTable   jtable                      =  getTable(stringTable) ;
        String    stringMoney           =  "" ;
        double  doubleMoneySum  =  0 ;
        for(int  intNo=0  ;  intNo<jtable.getRowCount()  ;  intNo++) {
            stringMoney  =  (""+getValueAt(stringTable,  intNo,  stringFieldName)).trim() ;
            //
            doubleMoneySum  +=  exeUtil.doParseDouble(stringMoney) ;
        }
        doubleMoneySum  =  exeUtil.doParseDouble(convert.FourToFive(""+doubleMoneySum,  0)) ;
        return  doubleMoneySum ;
    }
    // 非 (請購單為固資 或 前期有輸入此表格 或 第一次申請人總) 不作處理
    public  boolean  isNewVersion (Doc2M010  exeFun) throws  Throwable{
        JTable      jtable9                    =  getTable("Table9") ;     
        String      stringComNo           =  getValue("ComNo").trim() ; 
        String      stringKindNo            =  getValue("KindNo").trim() ; 
        String     stringEmpDeptCd     =  ""+get("EMP_DEPT_CD") ;
        String      stringPurchaseNo1  =  getValue("PurchaseNo1").trim() ; 
        String      stringPurchaseNo2  =  getValue("PurchaseNo2").trim() ; 
        String      stringPurchaseNo3  =  getValue("PurchaseNo3").trim() ; 
        String      stringPurchaseNo    =  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3 ; 
        String      stringFactoryNo        =  "" ; 
        String      stringFunction          =  getFunctionName() ; 
        //
        if(stringFunction.indexOf("簽核")  !=  -1)  stringEmpDeptCd = "033" ;
        //
        return  exeFun.isNewVersion (stringComNo,  stringKindNo,   stringPurchaseNo,  stringFactoryNo,  stringEmpDeptCd,  jtable9) ;
    }
    public  boolean  isBudgetMoneyTable6OK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false表示可以無請購單  
        if(booleanPurchaseExist)  return  true ;
        //
        getButton("ButtonTableCheck").doClick() ;
        String[][]  retTableData  =  getTableData("TableCheck") ;
        if(retTableData.length==0) {
            return  doTable6ErrorAction(-1,  "資料發生錯誤，請洽 [資訊企劃室]。") ;
        }

        if(retTableData.length==1  &&  "OK".equals(retTableData[0][0]))     return  true ;
          return  false ;
    }
    
    
    
    // 轉傳票
    // stringType  A 行銷   B 管理費用    C 土地開發-買賣
    public  String[][]  getTableDataFrom(String  stringBarCode,  String  stringType) throws  Throwable {
        Doc2M010         exeFun  =  new  Doc2M010( ) ;
        FargloryUtil         exeUtil   =  new  FargloryUtil() ;
        //
        String        stringTable10             =  "A".equals(stringType) ? "Doc6M010" : "Doc5M030" ;
        String        stringTable11             =  "A".equals(stringType) ? "Doc6M011" : "Doc5M031" ;
        String        stringTable13             =  "A".equals(stringType) ? "Doc6M013" : "Doc5M033" ;
        String[][]    retTable                    =  new  String[1][1] ;
        Vector        vectorDoc6M010        =  new  Vector(); 
        Vector        vectorDoc6M011        =  new  Vector(); 
        Vector        vectorDoc6M013        =  new  Vector(); 
        Hashtable  hashtableDoc6M010    =  null ;

        vectorDoc6M010 =  exeFun.getQueryDataHashtableDoc(stringTable10,  new  Hashtable(),  " AND  BarCode  = '"+stringBarCode+"' ",  new  Vector(),  exeUtil) ;
        if(vectorDoc6M010.size()  ==  0) {
            retTable[0][0]  =  "查無資料。" ;
            return retTable ;
        }
        hashtableDoc6M010  =  (Hashtable)  vectorDoc6M010.get(0) ;
        vectorDoc6M011       =  exeFun.getQueryDataHashtableDoc(stringTable11,  new  Hashtable(),  " AND  BarCode  = '"+stringBarCode+"' ",  new  Vector(),  exeUtil) ;
        vectorDoc6M013       =  exeFun.getQueryDataHashtableDoc(stringTable13,  new  Hashtable(),  " AND  BarCode  = '"+stringBarCode+"' ",  new  Vector(),  exeUtil) ;
        //
        retTable  =  getConvertToArrayUnion(stringType,  hashtableDoc6M010,  vectorDoc6M011,  vectorDoc6M013,  exeUtil,  exeFun) ;
        return  retTable ;
    }
    public  String[][]  getConvertToArrayUnion(String  stringType,  Hashtable  hashtableDoc6M010,  Vector  vectorDoc6M011,  Vector  vectorDoc6M013,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String[][]     retTable                             =  new  String[1][1] ;
        Vector        retVector                            =  new  Vector( ) ;
        Vector        vectorItemCd                       =  new  Vector() ;
        // 輸入部門
        doInputDepartNo(stringType,  hashtableDoc6M010,  exeUtil,  exeFun,  vectorItemCd) ;
        // 廠商
        doFactoryNo(hashtableDoc6M010,  vectorDoc6M011,  vectorDoc6M013,  exeUtil) ;
        System.out.println("---------------------借方" );
        putDebit("1281",  hashtableDoc6M010,  vectorDoc6M011,  vectorDoc6M013,  vectorItemCd,  exeUtil,  exeFun,  retVector) ;
        // 發票
        putDebitForInvoice(hashtableDoc6M010,  vectorDoc6M011,  exeUtil,  exeFun,  retVector) ;
        System.out.println("---------------------貸方資料") ;
        // 個人收據
        putDebitForDoc6M013(hashtableDoc6M010,  vectorDoc6M013,  exeUtil,  exeFun,  retVector) ;
        putCedit(hashtableDoc6M010,  vectorDoc6M013,  exeUtil,  exeFun,  retVector) ;
        // 轉換
        retTable  =  getFullTableData(retVector,  hashtableDoc6M010,  exeUtil) ;
        return  retTable ;
    }
    public  void  doFactoryNo(Hashtable  hashtableDoc6M010,  Vector  vectorDoc6M011,  Vector  vectorDoc6M013,  FargloryUtil  exeUtil) throws  Throwable {
        String         stringFactoryNo                      =  (""+hashtableDoc6M010.get("FactoryNo")).trim( ) ;  if("null".equals(stringFactoryNo))  stringFactoryNo  =  "" ;
        String         stringFactoryNoL             =  stringFactoryNo ;
        if(!"".equals(stringFactoryNo))  return  ;
        //
        if(vectorDoc6M011.size()  >  0) {
            Hashtable  hashtableDoc6M011  =  (Hashtable)vectorDoc6M011.get(0) ;
                                stringFactoryNo         =  (""+hashtableDoc6M011.get("FactoryNo")).trim( ) ;  if("null".equals(stringFactoryNo))  stringFactoryNo  =  "" ;
            if(!"".equals(stringFactoryNo))  {
                hashtableDoc6M010.put("FactoryNo",  stringFactoryNo) ;
            }
            return ;
        }
        if(vectorDoc6M013.size()  >  0) {
            Hashtable  hashtableDoc6M013  =  null ;
            for(int  intNo=0  ;  intNo<vectorDoc6M013.size()  ;  intNo++) {
                hashtableDoc6M013  =  (Hashtable)vectorDoc6M013.get(0) ;
                stringFactoryNo       =  (""+hashtableDoc6M013.get("FactoryNo")).trim( ) ;  if("null".equals(stringFactoryNo))  stringFactoryNo  =  "" ;
                if(!"".equals(stringFactoryNo)) {
                    stringFactoryNoL  =  stringFactoryNo ;
                    if(!"Z8000".equals(stringFactoryNo))  {
                        hashtableDoc6M010.put("FactoryNo",  stringFactoryNo) ;
                        return ;
                    }
                }
            }
        }
        hashtableDoc6M010.put("FactoryNo",  stringFactoryNoL) ; 
    }
    public  void  doInputDepartNo(String  stringType,  Hashtable  hashtableDoc6M010,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Vector  vectorItemCd) throws  Throwable {
        String         stringDepartNo                    =  "" ;
        //
        if("A".equals(stringType)) {
                      stringDepartNo                   =  (""+hashtableDoc6M010.get("DepartNo")).trim( ) ;  if("053M0101D".equals(stringDepartNo))  stringDepartNo =  "053M401D" ;
            String         stringInputDepartNo            =  getVoucherDepartNo(stringDepartNo,  exeUtil) ;
            //String         stringDepartNoOrigin            =  getVoucherDepartNo(stringDepartNo,  "A",  exeUtil) ;
            /*if("033GT".equals(stringDepartNoOrigin)  &&  !stringInputDepartNo.equals(stringDepartNoOrigin)) {
                String[]  arrayTemp     =  new  String[2] ;
                arrayTemp[0]  =  "I05" ;
                arrayTemp[1]  =  "04" ;
                vectorItemCd.add(arrayTemp) ;
            }*/
            hashtableDoc6M010.put("InputDepartNo",  stringInputDepartNo) ;
        } else {  
            String      stringComNo           =  (""+hashtableDoc6M010.get("ComNo")).trim( ) ; 
            String      stringDepartNo1       =  (""+hashtableDoc6M010.get("DepartNo1")).trim( ) ; 
            String      stringDepartNo2       =  (""+hashtableDoc6M010.get("DepartNo2")).trim( ) ; 
            String      stringAcctNoType      =   "".equals(stringDepartNo2) ? "I" : "A" ; 
            String         stringComNoType    =  exeFun.getComNoType(stringComNo) ;  
            Hashtable  hashtableInOut       =  new  Hashtable() ;
                      stringDepartNo        =  exeFun.getVoucherDepartNoDoc5(stringComNo,  stringComNoType,  stringAcctNoType,  stringDepartNo1,  stringDepartNo2, stringDepartNo2,  "",    hashtableInOut,  exeUtil)  ;
            //
            hashtableDoc6M010.put("InputDepartNo",  stringDepartNo) ;
        }
    }
    public  void  putDebit(String  stringAccountNo,  Hashtable  hashtableDoc6M010,  Vector  vectorDoc6M011,  Vector  vectorDoc6M013,  Vector  vectorItemCd,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Vector  retVector) throws  Throwable {
        String         stringOriEmployeeNo              =  (""+hashtableDoc6M010.get("OriEmployeeNo")).trim( ) ;
        String         stringDestineExpenseDate       =  (""+hashtableDoc6M010.get("DestineExpenseDate")).trim( ) ;
        String         stringInputDepartNo                  =  (""+hashtableDoc6M010.get("InputDepartNo")).trim( ) ;
        String         stringFactoryNo                      =  (""+hashtableDoc6M010.get("FactoryNo")).trim( ) ;
        String[]      arrayTemp                 =  new  String[27] ;  for(int  intNo=0  ;  intNo<arrayTemp.length  ;  intNo++)  arrayTemp[intNo]  =  "" ;
        String[]       arrayDescription                       =  null ;
        String[][]      retFED1004                    =  exeFun.getFED1004(stringAccountNo) ;
        double        doubleDebit                             =  getDebit(hashtableDoc6M010,  vectorDoc6M011,  vectorDoc6M013,  exeUtil) ;
        //
        // 明細一、明細二、明細三、明細四、明細五 18-22
        arrayDescription  = getDescriptionUnion(stringDestineExpenseDate,  stringAccountNo,  stringOriEmployeeNo,  retFED1004,  vectorItemCd,  exeUtil,  exeFun) ;
        //
        arrayTemp[2]    =   "N" ;                           // 發票M  借方N   貸方 O  扣繳 R
        arrayTemp[3]    =   "1" ;
        arrayTemp[9]    =  "D" ;                                                   // 借貸            9
        arrayTemp[10]  =  stringAccountNo ;               // 會計科目         10
        arrayTemp[11]  =  stringInputDepartNo ;               // 部門             11
        arrayTemp[12]  =   stringFactoryNo ;                       // 對象            12
        arrayTemp[13]  =  convert.FourToFive(""+ doubleDebit,0) ;   // 金額             13
        arrayTemp[18]  =  arrayDescription[0] ;                         // 明細一                18
        arrayTemp[19]  =  arrayDescription[1] ;                       // 明細二                19
        arrayTemp[20]  =  arrayDescription[2] ;                           // 明細三                20
        arrayTemp[21]  =  arrayDescription[3] ;                           // 明細四                21
        arrayTemp[22]  =  arrayDescription[4] ;                       // 明細五                22
         // 
         if("110301".equals(stringAccountNo))  arrayTemp[12] =  "0095289" ;
         //
         retVector.add(arrayTemp) ;
    }
    public  double  getDebit(Hashtable  hashtableDoc6M010,  Vector  vectorDoc6M011,  Vector  vectorDoc6M013,  FargloryUtil  exeUtil) throws  Throwable {
        String         stringBorrowMoney                  =  (""+hashtableDoc6M010.get("BorrowMoney")).trim( ) ;
        double        doubleDebit                             =  exeUtil.doParseDouble(stringBorrowMoney) ;
        //
        if(vectorDoc6M011.size()  >  0) {
            String      stringInvoiceMoney      =  "" ; 
            Hashtable   hashtableDoc6M011     =  new  Hashtable() ;
                      doubleDebit           =  0 ;
            for(int  intNo=0  ;  intNo<vectorDoc6M011.size()  ;  intNo++) {
                  hashtableDoc6M011  =  (Hashtable)  vectorDoc6M011.get(intNo) ;
                  stringInvoiceMoney    =  ""+hashtableDoc6M011.get("InvoiceMoney") ;
                  //
                  doubleDebit    +=  exeUtil.doParseDouble(stringInvoiceMoney) ;
            }
            if(doubleDebit  >  0)  return  doubleDebit ;
        }
        if(vectorDoc6M013.size()  >  0) {
            String      stringReceiptTotalMoney =  "" ; 
            Hashtable   hashtableDoc6M013     =  new  Hashtable() ;
                      doubleDebit           =  0 ;
            for(int  intNo=0  ;  intNo<vectorDoc6M013.size()  ;  intNo++) {
                  hashtableDoc6M013         =  (Hashtable)  vectorDoc6M013.get(intNo) ;
                  stringReceiptTotalMoney    =  ""+hashtableDoc6M013.get("ReceiptTotalMoney") ;
                  //
                  doubleDebit    +=  exeUtil.doParseDouble(stringReceiptTotalMoney) ;
            }
            if(doubleDebit  >  0)  return  doubleDebit ;
        }
        return  exeUtil.doParseDouble(stringBorrowMoney) ;
    }
    public  void  putDebitForInvoice(Hashtable  hashtableDoc6M010,  Vector  vectorDoc6M011,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Vector  retVector) throws  Throwable {
        String         stringInputDepartNo                  =  (""+hashtableDoc6M010.get("InputDepartNo")).trim( ) ;
        String         stringFactoryNo                      =  "" ;
        String        stringAccountNo                       =  "" ;
        String         stringInvoiceTax                        =  "" ;
        String         stringInvoiceNo                         =  "" ;
        String[]      arrayTemp                 =  null ;
        String[]       arrayDescription                       =  null ;
        String[][]      retFED1004                    =  null ;
        String[]       retDoc2M040                          =  exeFun.getDoc2M040( ) ;
        Hashtable  hashtableDoc6M011                =  null ;
        //
        stringAccountNo  =  retDoc2M040[0].trim( ) ;
        retFED1004        =  exeFun.getFED1004(stringAccountNo) ;
        //
        for(int  intNo=0  ;  intNo<vectorDoc6M011.size()  ;  intNo++) {
            hashtableDoc6M011  =  (Hashtable)  vectorDoc6M011.get(intNo) ;
            stringFactoryNo                  =  (""+hashtableDoc6M011.get("FactoryNo")).trim( ) ;
            stringInvoiceTax                 =  (""+hashtableDoc6M011.get("InvoiceTax")).trim( ) ;
            //
            if(exeUtil.doParseDouble(stringInvoiceTax)  ==  0)  continue ;
            //
            arrayDescription  =  getDescriptionDoc6M011(retFED1004,  hashtableDoc6M011,  exeUtil,  exeFun) ;
            //
            arrayTemp                 =  new  String[27] ;  for(int  intNoL=0  ;  intNoL<arrayTemp.length  ;  intNoL++)  arrayTemp[intNoL]  =  "" ;
            //
            arrayTemp[2]    =   "M" ;                           // 發票M  借方N   貸方 O  扣繳 R
            arrayTemp[3]    =   ""+(intNo+1) ;
            arrayTemp[9]    =  "D" ;                                                   // 借貸            9
            arrayTemp[10]  =  stringAccountNo ;               // 會計科目         10
            arrayTemp[11]  =  stringInputDepartNo ;               // 部門             11
            arrayTemp[12]  =   stringFactoryNo ;                       // 對象            12
            arrayTemp[13]  =  stringInvoiceTax ;                  // 金額             13
            arrayTemp[18]  =  arrayDescription[0] ;                         // 明細一                18
            arrayTemp[19]  =  arrayDescription[1] ;                       // 明細二                19
            arrayTemp[20]  =  arrayDescription[2] ;                           // 明細三                20
            arrayTemp[21]  =  arrayDescription[3] ;                           // 明細四                21
            arrayTemp[22]  =  arrayDescription[4] ;                       // 明細五                22
             // 
             retVector.add(arrayTemp) ;
         }
    }
    public  String[]  getDescriptionDoc6M011(String[][]  retFED1004,  Hashtable  hashtableDoc6M011,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String     stringItemCd                     =  "" ;
        String     stringInvoiceDate             =   ""+hashtableDoc6M011.get("InvoiceDate") ;
        String     stringInvoiceNo                =   ""+hashtableDoc6M011.get("InvoiceNo") ;
        String     stringInvoiceMoney          =   ""+hashtableDoc6M011.get("InvoiceMoney") ;
        String     stringInvoiceTotalMoney  =   ""+hashtableDoc6M011.get("InvoiceTotalMoney") ;
        String     stringInvoiceKind              =   ""+hashtableDoc6M011.get("InvoiceKind") ;
        String[]   arrayDescription              =  {"",  "",  "",  "",  ""} ;
        //
        stringInvoiceDate  =  exeUtil.getDateConvertRoc(stringInvoiceDate).replaceAll("/",  "") ;
        //  明細資料處理(預設為 B08、B09、B10、B11、B21，非上述之 ITEM_CD 時，空白處理)
        for(int  intNo=0  ;  intNo<retFED1004.length  ;  intNo++) {
            stringItemCd  =  retFED1004[intNo][0].trim( ) ;
              //    
            if("B08".equals(stringItemCd))   arrayDescription[intNo]  =  stringInvoiceDate ;
            if("B09".equals(stringItemCd))   arrayDescription[intNo]  =  stringInvoiceNo ;
            if("B10".equals(stringItemCd))   arrayDescription[intNo]  =  stringInvoiceMoney ;
            if("B11".equals(stringItemCd))   arrayDescription[intNo]  =  stringInvoiceTotalMoney ;
            if("B40".equals(stringItemCd))   arrayDescription[intNo]  =  exeFun.getInvoiceKindName(stringInvoiceKind) ;
        }
        //
        return  arrayDescription ;
    }
    
    public  void  putDebitForDoc6M013(Hashtable  hashtableDoc6M010,  Vector  vectorDoc6M013,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Vector  retVector) throws  Throwable {
        int               intPos13                                   =  0 ;
        int                intAcctountCount                     =  getAccountCunt(hashtableDoc6M010,  exeUtil) ;
        double        doubleMoney               =  0 ;
        double        doubleMoneySum          =  0 ;
        String         stringInputDepartNo                  =  (""+hashtableDoc6M010.get("InputDepartNo")).trim( ) ;
        String         stringPayCondition1                  =  (""+hashtableDoc6M010.get("PayCondition1")).trim( ) ;
        String         stringPayCondition2                  =  (""+hashtableDoc6M010.get("PayCondition2")).trim( ) ;
        String         stringComNo                          =  ""+hashtableDoc6M010.get("ComNo") ;
        String         stringFactoryNo                      =  "" ;
        String        stringAcctNo                            =  "" ;
        String        stringAcctTax                         =  "" ;
        String         stringReceiptTax                       =  "" ;
        String         stringReceiptKind                     =  "" ;
        String         stringReceiptDate                     =  "" ;
        String         stringSupplementMoney            =  "" ;
        String         stringReceiptMoney                  =  "" ;
        String         stringReceiptTotalMoney          =  "" ;
        String         stringAmt                        =  "" ;
        String         stringRowType                =  "" ;
        String         stringFlow                       =  "" ;
        String          stringVoucherYMD014           =  getVoucherYMD( ) ;
        String         stringItemCd                   =  "" ;
        String         stringDate                     =  "" ;
        String[]      arrayTemp                 =  new  String[27] ;  for(int  intNo=0  ;  intNo<arrayTemp.length  ;  intNo++)  arrayTemp[intNo]  =  "" ;
        String[]      arrayTempL                =  null ;
        String[]       arrayDescription                       =  {"",  "",  "",  "",  ""} ;
        String[]       arrayRatio                             =  null ;
        String[]       arrayMoney                           =  null ;
        String[]       arrayMoney2                          =  null ;
        String[][]      retFED1004                    =  null ;
        Hashtable  hashtableDoc6M013                =  null ;
        //
        
        //
        for(int  intNo=0  ;  intNo<vectorDoc6M013.size()  ;  intNo++) {
            hashtableDoc6M013          =  (Hashtable)  vectorDoc6M013.get(intNo) ;
            stringFactoryNo                  =  (""+hashtableDoc6M013.get("FactoryNo")).trim( ) ;
            stringReceiptKind                =  (""+hashtableDoc6M013.get("ReceiptKind")).trim( ) ;
            stringReceiptDate               =  (""+hashtableDoc6M013.get("ReceiptDate")).trim( ) ;
            stringReceiptMoney            =  (""+hashtableDoc6M013.get("ReceiptMoney")).trim( ) ;
            stringReceiptTax                =  (""+hashtableDoc6M013.get("ReceiptTax")).trim( ) ;
            stringReceiptTotalMoney     =  (""+hashtableDoc6M013.get("ReceiptTotalMoney")).trim( ) ;
            //stringReceiptTaxType        =  (""+hashtableDoc6M013.get("ReceiptTaxType")).trim( ) ;
            stringAcctNo                =  (""+hashtableDoc6M013.get("ACCT_NO")).trim( ) ;
            stringSupplementMoney      =  (""+hashtableDoc6M013.get("SupplementMoney")).trim( ) ;
            //
            intPos13++ ;
            //
            // 1  扣繳分錄      2 補充保費
            for(int  intNoL=1  ;  intNoL<=2  ;  intNoL++) {
                if(intNoL  ==  1) {
                      stringAcctTax  =  !"".equals(stringAcctNo) ? stringAcctNo : "228203" ;
                      stringAmt         =  stringReceiptTax ;         // 金額
                      stringRowType =  "R" ;
                } else {
                      stringAcctTax   =  "228231" ;               // 會計科目
                      stringAmt         =  stringSupplementMoney ;    // 金額
                      stringRowType =  "Z" ;
                }
                if(exeUtil.doParseDouble(stringAmt)  ==  0)  continue ;
                //
                retFED1004                  =  exeFun.getFED1004(stringAcctTax) ;
                //  明細資料處理 G08、B04、G07、，非上述之 ITEM_CD 時，空白處理
                for(int  intL=0  ;  intL<5  ;  intL++) {
                    arrayDescription[intL]  =  ""  ;
                    if(intL  >=  retFED1004.length)  continue ; 
                    if("G08".equals(retFED1004[intL][0].trim( )))  {
                        if(!"".equals(stringReceiptDate)) {
                            arrayDescription[intL]  =  convert.replace(stringReceiptDate,  "/",  "") ;  // 所得給付票據到期日
                        } else {
                            if(!"".equals(stringVoucherYMD014)) {
                                arrayDescription[intL]  =  exeFun.getExpiredDateUnion((intPos13),                 stringAcctTax,            stringVoucherYMD014,  
                                                                                  stringPayCondition1,  stringPayCondition2,  "",
                                                                              stringComNo,              exeUtil) ;
                                arrayDescription[intL]   =  convert.replace(arrayDescription[intL].trim(),  "/",  "") ;
                            }
                        }
                    }
                    if("G20".equals(retFED1004[intL][0].trim( )))  arrayDescription[intL]  =  stringReceiptTotalMoney ;  // G20 所得總額
                    if("G06".equals(retFED1004[intL][0].trim( )))  arrayDescription[intL]  =  stringReceiptTotalMoney ;  // 
                    if("G07".equals(retFED1004[intL][0].trim( )))  arrayDescription[intL]  =  stringAmt ;  // 
                    if("228203".equals(stringAcctTax)  &&  "C07".equals(retFED1004[intL][0].trim( ))) arrayDescription[intL]  =  "1281" ;
                }
                arrayTemp[2]    =   stringRowType ;                   // 發票M  借方N   貸方 O  扣繳 R
                arrayTemp[3]    =   ""+(intNo+1) ;
                arrayTemp[9]    =  "C" ;                                                   // 借貸            9
                arrayTemp[10]  =  stringAcctTax ;                  // 會計科目        10
                arrayTemp[11]  =  stringInputDepartNo ;               // 部門             11
                arrayTemp[12]  =   stringFactoryNo ;                       // 對象            12
                arrayTemp[13]  =  stringAmt ;                         // 金額             13
                arrayTemp[16]  =  stringReceiptMoney ;                // 折讓未稅金額     16
                arrayTemp[18]  =  arrayDescription[0] ;                         // 明細一                18
                arrayTemp[19]  =  arrayDescription[1] ;                       // 明細二                19
                arrayTemp[20]  =  arrayDescription[2] ;                           // 明細三                20
                arrayTemp[21]  =  arrayDescription[3] ;                           // 明細四                21
                arrayTemp[22]  =  arrayDescription[4] ;                       // 明細五                22
                 //
                arrayRatio    =  new  String[intAcctountCount] ;  for(int  intL=0  ;  intL<arrayRatio.length  ;  intL++)  arrayRatio[intL] = "10" ;
                arrayMoney  =  exeUtil.getMoneyFromRatio(stringAmt,  arrayRatio) ;
                for(int  intCount=0  ;  intCount<intAcctountCount ;  intCount++) {
                    arrayTempL    =  exeUtil.doCopyArray(arrayTemp) ;
                    doubleMoney  =  exeUtil.doParseDouble(arrayMoney[intCount]) ;
                    //
                    if(intCount  !=  0)  {  
                        stringFlow  =  ""+intCount ;
                    } else {  
                        stringFlow  =  "" ;
                    }
                    arrayTempL[2]    =  stringRowType+stringFlow ;                                                      // 發票-借A  費用-借B  貸C  折讓-貨D  郵電-貸E 扣繳-貸F  扣繳-貸G(稅)  折讓(稅)-貸H  退保留款-貸I
                    // 到期
                    for(int  intL=0  ;  intL<5  ;  intL++) {
                        if( intL  >=  retFED1004.length) continue ;
                        stringItemCd  =  retFED1004[intL][0].trim( ) ;
                        // 到期日 G08
                        if("G08".equals(stringItemCd)) {
                            stringDate  =   arrayTemp[18+intL].trim() ;
                            if(!"".equals(stringDate)) {
                                arrayTempL[18+intL]  =  datetime.dateAdd(stringDate,  "m",  intCount) ;
                            }
                        }
                        // G20 所得總額
                        if("G20".equals(stringItemCd)) {
                            doubleMoneySum  =  exeUtil.doParseDouble(arrayTemp[18+intL].trim()) ;
                            arrayMoney2         =  exeUtil.getMoneyFromRatio(""+doubleMoneySum,  arrayRatio) ;
                            //
                            arrayTempL[18+intL]  =  convert.FourToFive(arrayMoney2[intCount],  0) ;
                        }
                        // G06
                        if("G06".equals(stringItemCd)) {
                            doubleMoneySum  =  exeUtil.doParseDouble(arrayTemp[18+intL].trim()) ;
                            arrayMoney2         =  exeUtil.getMoneyFromRatio(""+doubleMoneySum,  arrayRatio) ;
                            //
                            arrayTempL[18+intL]  =  convert.FourToFive(arrayMoney2[intCount],  0) ;
                        }
                        if("G07".equals(stringItemCd)) {
                            doubleMoneySum  =  exeUtil.doParseDouble(arrayTemp[18+intL].trim()) ;
                            arrayMoney2         =  exeUtil.getMoneyFromRatio(""+doubleMoneySum,  arrayRatio) ;
                            //
                            arrayTempL[18+intL]  =  convert.FourToFive(arrayMoney2[intCount],  0) ;
                        }
                    }
                    //
                    arrayTempL[2]    =  stringRowType+stringFlow ;                                         // 發票-借A  費用-借B  貸C  折讓-貨D  郵電-貸E  扣繳-貸F  扣繳-貸G(稅)  折讓(稅)-貸H  退保留款-
                    arrayTempL[13]  =  convert.FourToFive(""+doubleMoney, 0) ;              // 金額           13
                    //
                    retVector.add(arrayTempL) ;
                }
            }
        }
    }
    public  void  putCedit(Hashtable  hashtableDoc6M010,  Vector  vectorDoc6M013,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Vector  retVector) throws  Throwable {
        String        stringAcctC                                 =  "" ;
        String        stringMoney                   =  "" ;
        String        stringAcctS                                 =  (""+get("Doc2M014_AcctNo")).trim() ;
        String          stringPayCondition1                   =  (""+hashtableDoc6M010.get("PayCondition1")).trim( ) ;
        String          stringPayCondition2                   =  (""+hashtableDoc6M010.get("PayCondition2")).trim( ) ;
        String         stringInputDepartNo                      =  (""+hashtableDoc6M010.get("InputDepartNo")).trim( ) ;
        String         stringComNo                                =  ""+hashtableDoc6M010.get("ComNo") ;
        String         stringFactoryNo                        =  (""+hashtableDoc6M010.get("FactoryNo")).trim( ) ;
        String         stringFactoryNoL                     =  "" ;
        String          stringVoucherYMD014               =  getVoucherYMD( ) ;
        String          stringItemCd                              =  "" ;
        String          stringFlow                                      =  "" ;
        String          stringDate                                      =  "" ;
        String[]        arrayTemp                                     =  null ;
        String[]        arrayDataTemp                             =  null ;
        String[]        arrayDescription                            =  new  String[5] ;
        String[]        arrayRatio                                  =  null ;
        String[]        arrayMoneyFront                       =  null ;
        String[]        arrayMoney                              =  null ;
        String[][]     retDoc2M030                            =  {{"2121"}} ;
        String[][]     retFED1004                             =  null ;
        double        doubleMoneySum                      =  0 ;
        double        doubleMoney                             =  0 ;
        double          doubleReceiptKindCMoneySum    =  getReceiptKindCMoneSum(vectorDoc6M013,  exeUtil) ; //System.out.println("doubleReceiptKindCMoneySum("+convert.FourToFive(""+doubleReceiptKindCMoneySum,  0)+")-------------------------------------") ;
        double        doubleTotalMoneySum           =  getCedit(retVector,  exeUtil);
        int       intMaxCount                 =  ("999".equals(stringPayCondition2)) ? 1 :  2 ;
        //
        int                intAcctountCount                         =  getAccountCunt(hashtableDoc6M010,  exeUtil) ;
        //
        arrayRatio          =  new  String[intMaxCount] ;  for(int  intNoL=0  ;  intNoL<arrayRatio.length  ;  intNoL++)  arrayRatio[intNoL] = "10" ;
        arrayMoneyFront     =  exeUtil.getMoneyFromRatio(""+doubleTotalMoneySum,  arrayRatio) ;
        for(int  intDoc2M030=0  ;  intDoc2M030<intMaxCount  ;  intDoc2M030++) {
            arrayDataTemp  =  new  String[27] ;     for(int  intNo=0  ;  intNo<arrayDataTemp.length  ;  intNo++)arrayDataTemp[intNo]  =  "" ;
            stringMoney     =  arrayMoneyFront[intDoc2M030] ;
            stringFactoryNoL  =  stringFactoryNo ;
            // 會計科目
            if(intDoc2M030  <  retDoc2M030.length) stringAcctC  =  retDoc2M030[intDoc2M030][0].trim( ) ;
            if(!"null".equals(stringAcctS))  stringAcctC  =  stringAcctS ;
            // 明細一至五
            retFED1004         =  exeFun.getFED1004(stringAcctC) ;
            for(int  intNo=0  ;  intNo<5  ;  intNo++) {
                arrayDescription[intNo]  =  "" ;
                if(intNo  >=  retFED1004.length)  continue ;
                stringItemCd  =  retFED1004[intNo][0].trim( ) ;
                if("A04".equals(stringItemCd)  &&  !"".equals(stringVoucherYMD014)) {
                    arrayDescription[intNo]  =  exeFun.getExpiredDateUnion((intDoc2M030+1),      stringAcctC,                stringVoucherYMD014,  
                                                                                                               stringPayCondition1,  stringPayCondition2,   "",
                                                                   stringComNo,              exeUtil) ;
                }
                                 if("A02".equals(stringItemCd)  &&  "110301".equals(stringAcctC)) {
                                                      if("Z6".equals(stringComNo)) {arrayDescription[intNo]  =  "12675-0" ;    stringFactoryNoL  =  "0095289" ;}
                                             else if("71".equals(stringComNo)) {arrayDescription[intNo]  =  "13187-7" ;     stringFactoryNoL  =  "0095289" ;}
                                             else if("72".equals(stringComNo)) {arrayDescription[intNo]  =  "14403-4" ;     stringFactoryNoL  =  "0095289" ;}
                                             else if("73".equals(stringComNo)) {arrayDescription[intNo]  =  "15264-3" ;     stringFactoryNoL  =  "0095289" ;}
                                             else if("Z4".equals(stringComNo)) {arrayDescription[intNo]  =  "12673-2" ;     stringFactoryNoL  =  "0095289" ;}
                                             else if("Z5".equals(stringComNo)) {arrayDescription[intNo]  =  "1561-8" ;       stringFactoryNoL  =  "0095289" ;}
                                             else if("20".equals(stringComNo)) {arrayDescription[intNo]  =  "102362" ;      stringFactoryNoL  =  "0170077" ;}
                                             else if("00".equals(stringComNo)) {arrayDescription[intNo]  =  "9516-1-00" ;  stringFactoryNoL  =  "0095289" ;}
                                             else if("Z3".equals(stringComNo)) {arrayDescription[intNo]  =  "5438-4" ;       stringFactoryNoL  =  "0095289" ;}
                                             else if("Z0".equals(stringComNo)) {arrayDescription[intNo]  =  "13188-6" ;     stringFactoryNoL  =  "0095289" ;}
                                             else if("Z2".equals(stringComNo)) {arrayDescription[intNo]  =  "12676-1" ;     stringFactoryNoL  =  "0095289" ;}
                                             else if("10".equals(stringComNo)) {arrayDescription[intNo]  =  "107-0" ;         stringFactoryNoL  =  "0095289" ;}
                                             else if("ZD".equals(stringComNo)) {arrayDescription[intNo]  =  "86666000" ;  stringFactoryNoL  =  "0095289" ;}
                                             else if("01".equals(stringComNo)) {arrayDescription[intNo]  =  "7978-3" ;       stringFactoryNoL  =  "0095289" ;}
                                             else if("76".equals(stringComNo)) {arrayDescription[intNo]  =  "07288200" ;  stringFactoryNoL  =  "0095289" ;} 
                                             else if("12".equals(stringComNo)) {arrayDescription[intNo]  =  "3237-9" ;       stringFactoryNoL  =  "0095289" ;} 
                                             else if("ZA".equals(stringComNo)) {arrayDescription[intNo]  =  "12712-2" ;     stringFactoryNoL  =  "0095289" ;} 
                                             else if("02".equals(stringComNo)) {arrayDescription[intNo]  =  "20885500" ;  stringFactoryNoL  =  "0095289" ;} 
                                             //else if("Z2".equals(stringComNo)) {arrayDescription[intNo]   =  "3237-9" ;        stringFactoryNoL  =  "0095289" ;} 
                                 }
            }
            // 放入陣列中
            arrayDataTemp[2]    =  "O" ;                            // 發票M  借方N   貸方 O  郵電 P    扣繳 R
            arrayDataTemp[3]    =  ""+(intDoc2M030+1) ;                          // No              3
            arrayDataTemp[9]    =  "C" ;                                              // 借貸             9
            arrayDataTemp[10]  =  stringAcctC ;                         // 會計科目         10
            arrayDataTemp[11]  =  "110301".equals(stringAcctC)?"":stringInputDepartNo ;                 // 部門             11
            arrayDataTemp[12]  =   stringFactoryNoL ;                     // 對象             12
            arrayDataTemp[13]  =  convert.FourToFive(stringMoney, 0) ; // 金額            13
            arrayDataTemp[18]  =  arrayDescription[0] ;                         // 明細一                18
            arrayDataTemp[19]  =  arrayDescription[1] ;                       // 明細二                19
            arrayDataTemp[20]  =  arrayDescription[2] ;                            // 明細三                 20
            arrayDataTemp[21]  =  arrayDescription[3] ;                            // 明細四                 21
            arrayDataTemp[22]  =  arrayDescription[4] ;                         // 明細五              22
            //
            if("110301".equals(stringAcctC))  arrayDataTemp[12] =  "0095289" ;
            //
            if(intAcctountCount  <=  1) {
                retVector.add(arrayDataTemp) ;
                continue ;
            }
            // 須通報收據
            if(doubleReceiptKindCMoneySum  >  0) {
                arrayTemp        =  exeUtil.doCopyArray(arrayDataTemp) ;
                arrayTemp[13]  =  convert.FourToFive(""+doubleReceiptKindCMoneySum,  0) ;
                retVector.add(arrayTemp) ;
                //
                //System.out.println("stringMoney("+stringMoney+")-------------------------------------") ;
                stringMoney  =  ""+(exeUtil.doParseDouble(stringMoney)-doubleReceiptKindCMoneySum) ;
                //System.out.println("stringMoney("+stringMoney+")doubleReceiptKindBMoneySum("+convert.FourToFive(""+doubleReceiptKindBMoneySum,  0)+")-------------------------------------") ;
                intAcctountCount-- ;
            }
            // 須通報扣繳收據
            arrayRatio    =  new  String[intAcctountCount] ;  for(int  intNoL=0  ;  intNoL<arrayRatio.length  ;  intNoL++)  arrayRatio[intNoL] = "10" ;
            arrayMoney  =  exeUtil.getMoneyFromRatio(stringMoney,  arrayRatio) ;
            for(int  intCount=0  ;  intCount<intAcctountCount ;  intCount++) {
                arrayTemp     =  exeUtil.doCopyArray(arrayDataTemp) ;
                doubleMoney  =  exeUtil.doParseDouble(arrayMoney[intCount]) ;
                //
                if(intCount  !=  0  ||  doubleReceiptKindCMoneySum>0)  {  
                     stringFlow  =  ""+intCount ;
                } else {  
                    stringFlow  =  "" ;
                }
                arrayTemp[2]    =  "O"+stringFlow ;                                        // 發票-借A  費用-借B  貸C  折讓-貨D  郵電-貸E  扣繳-貸F  扣繳-貸G(稅)  折讓(稅)-貸H  退保留款-
                // 到期
                for(int  intNo=0  ;  intNo<5  ;  intNo++) {
                    if( intNo  >=  retFED1004.length) continue ;
                    stringItemCd  =  retFED1004[intNo][0].trim( ) ;
                    // 到期日 A04
                    if("A04".equals(stringItemCd)  &&  !"".equals(stringVoucherYMD014)) {
                        stringDate                   =    convert.replace(arrayTemp[18+intNo].trim(),  "/",  "") ;
                        arrayTemp[18+intNo]  =  datetime.dateAdd(stringDate,  "m",  intCount) ;
                    }
                }
                //
                arrayTemp[13]  =  convert.FourToFive(""+doubleMoney, 0) ;              // 金額          13
                //
                retVector.add(arrayTemp) ;
            }
        }
    }
    public  double  getCedit(Vector  retVector,  FargloryUtil  exeUtil) throws  Throwable {
        String         stringDbCrCd                           =  "" ;
        String[]    arrayTemp                 =  null ;
        double        doubleDebit                             =  0 ;
        //
        for(int  intNo=0  ;  intNo<retVector.size()  ;  intNo++) {
              arrayTemp       =  (String[])  retVector.get(intNo) ;
              stringDbCrCd   =  arrayTemp[9] ;
              //
              if("D".equals(stringDbCrCd)) {
                  doubleDebit    +=  exeUtil.doParseDouble(arrayTemp[13]) ;
              } else {
                  doubleDebit    -=  exeUtil.doParseDouble(arrayTemp[13]) ;
              }
        }
        return  doubleDebit ;
    }
    public  double  getReceiptKindCMoneSum(Vector  vectorDoc6M013,  FargloryUtil  exeUtil) throws  Throwable {
        String          stringReceiptKind                           =  "" ;
        String          stringReceiptTotalMoney               =  "" ;
        double        doubleReceiptKindCMoneySum        =  0 ;
        Hashtable   hashtableDoc6M013           =  null ;
        //
        for(int  intNo=0  ;  intNo<vectorDoc6M013.size()  ;  intNo++) {
              hashtableDoc6M013           =  (Hashtable)  vectorDoc6M013.get(intNo) ;
              stringReceiptKind         =  (""+hashtableDoc6M013.get("ReceiptKind")).trim() ;
              stringReceiptTotalMoney     =  (""+hashtableDoc6M013.get("ReceiptTotalMoney")).trim() ;
              //
              //System.out.println(intNo+"stringReceiptKind("+stringReceiptKind+")stringReceiptTotalMoney("+stringReceiptTotalMoney+")----------------------------------------S") ;
              if(!"C".equals(stringReceiptKind)) continue ;
              //
              doubleReceiptKindCMoneySum    +=  exeUtil.doParseDouble(stringReceiptTotalMoney) ;
              //System.out.println(intNo+"stringReceiptKind("+stringReceiptKind+")doubleReceiptKindBMoneySum("+convert.FourToFive(""+doubleReceiptKindBMoneySum,  0)+")----------------------------------------E") ;
        }
        return  doubleReceiptKindCMoneySum ;
    }
    public  int  getAccountCunt(Hashtable  hashtableDoc6M010,  FargloryUtil  exeUtil) throws  Throwable {
        String    stringAccountCountS               =  (""+get("Doc2M014_AccountCount")).trim( ) ;      if("null".equals(stringAccountCountS))    stringAccountCountS    =  "" ;
        String      stringAccountCount                  =  !"".equals(stringAccountCountS)  ?  stringAccountCountS  :  (""+hashtableDoc6M010.get("AccountCount")).trim( ) ;
        if(exeUtil.doParseDouble(stringAccountCount)  <=  0)  stringAccountCount  =  "1" ;
        return  exeUtil.doParseInteger(stringAccountCount) ;
    }
    public  String[][]  getFullTableData(Vector  retVector,  Hashtable  hashtableDoc6M010,  FargloryUtil  exeUtil) throws  Throwable {
        String         stringBarCode                     =  ""+hashtableDoc6M010.get("BarCode") ;
        String         stringComNo                       =  ""+hashtableDoc6M010.get("ComNo") ;
        String         stringDocNo                        =  ""+hashtableDoc6M010.get("DocNo") ;
        String         stringKind                            =  "0" ;
        String         stringDescript                     =  getDescript(hashtableDoc6M010,  exeUtil) ;
        String         stringDescriptL                    =  "" ;
        String         stringVoucherYMD014        =  getVoucherYMD( ) ;
        String          stringUser                          =  getUser( ) ;
        String         stringToday                        =  getToday("yymmdd") ;
        String         stringTodayL                      =  "" ;
        String[]       retDataTemp                      =  null ;
        String[][]     retTable                              =  (String[][])  retVector.toArray(new  String[0][0]) ;
        for(int  intNo=0  ;  intNo<retTable.length  ;  intNo++) {
            System.out.println(intNo+"getFullTableData------------------------------------------------22222") ;
            retDataTemp     =  retTable[intNo] ;
            stringTodayL     =  retDataTemp[25].trim() ;
            stringDescriptL  =  retDataTemp[23].trim() ;
            //
            if("".equals(stringTodayL))     stringTodayL     =  stringToday ;
            if("".equals(stringDescriptL))  stringDescriptL  =  stringDescript ;
            //
            retDataTemp[0]    =   stringBarCode ;                 // 條碼編號         0
            retDataTemp[1]    =   stringDocNo ;                   // 公文代號         1
            retDataTemp[4]    =   stringVoucherYMD014 ;           // 傳票日期         4
            retDataTemp[5]    =   "0" ;                                                 // 傳票流水號            5
            retDataTemp[6]    =   ""  +  (intNo+1) ;                   // 傳票序號        6
            retDataTemp[7]    =  stringComNo ;                  // 公司代號         7
            retDataTemp[8]    =  stringKind ;                     // Kind             8
            retDataTemp[14]  =   "0" ;                          // 匯率             14
            retDataTemp[15]  =   "0" ;                           // 原類金額        15
            retDataTemp[16]  =   "0" ;                                                // 請款金額         16
            retDataTemp[17]  =   "U" ;                        // 狀態-未過帳       17
            retDataTemp[23]  =  stringDescriptL ;                 // 摘要             23
            retDataTemp[24]  =  stringUser  ;                   // 修改者            24
            retDataTemp[25]  =  stringTodayL  ;                 // 修改日期         25
            retDataTemp[26]  =  "A"  ;                           // 
            System.out.println("intNo------------------------------"+retTable[intNo][6].trim( )) ;
        }
        return  retTable ;
    }
    public  String  getDescript(Hashtable  hashtableDoc6M010,  FargloryUtil  exeUtil) throws  Throwable {
        String  stringDescript  =  (""+hashtableDoc6M010.get("Descript")).replaceAll("\n","") ;
        String  stringTemp      =  "" ;
        //
        stringTemp   =  exeUtil.doSubstring(code.StrToByte(stringDescript), 0, 30) ;
        stringTemp    =  code.ByteToStr(stringTemp) ;
        if(stringDescript.indexOf(stringTemp)  ==  -1) {
            stringTemp   =  exeUtil.doSubstring(code.StrToByte(stringDescript), 0, 29) ;
            stringTemp    =  code.ByteToStr(stringTemp) ;
        }
        stringDescript  =  stringTemp ;
        stringDescript  =  convert.replace(stringDescript,  "'",  "''") ;
        //
        return  stringDescript ;    
    }
    public  String  getVoucherYMD( ) throws  Throwable {
        String  stringVoucherYMD014    =  (""+get("Doc2M014_VOUCHER_YMD")).trim( ) ;          if("null".equals(stringVoucherYMD014)) stringVoucherYMD014  =  "" ;
        return  stringVoucherYMD014 ;
    }
    
    

    
    public  String  getVoucherDepartNo(String  stringInputDepartNo,  FargloryUtil  exeUtil) throws  Throwable {
        return   getVoucherDepartNo(stringInputDepartNo,  "",  exeUtil) ;
    }
    public  String  getVoucherDepartNo(String  stringInputDepartNo,  String  stringType,  FargloryUtil  exeUtil) throws  Throwable {
        System.out.println("getVoucherDepartNo--------------------S---"+stringInputDepartNo) ;
        char     charWord            =  'A' ;
        char[]   arrayChar           =  stringInputDepartNo.toCharArray() ;
        String  stringInOut          =  "I" ;
        String  stringDepartNo    =  stringInputDepartNo ;
        String  stringProjectID1   =  "" ;
        String  stringSpecBudget =  ","+get("SPEC_BUDGET")+"," ;
        if(stringSpecBudget.indexOf(stringDepartNo)  !=  -1) {
            stringInOut          =  "I" ;
        } else if("033MP".indexOf(stringDepartNo)  !=  -1) {
            stringInOut          =  "O" ;
            stringProjectID1  =  stringDepartNo ;
        } else {
            for(int  intNo=0  ;  intNo<arrayChar.length  ;  intNo++) {
                charWord  =  arrayChar[intNo] ;
                if(!Character.isDigit(charWord)){
                    stringInOut          =  "O" ;
                    stringDepartNo   =  stringInputDepartNo.substring(0,  intNo) ;
                    stringProjectID1  =  stringInputDepartNo.substring(intNo) ;
                    break ;
                }
            }
        }
        if(stringProjectID1.equals("M51A"))    stringProjectID1  =  "M51" ;// 特例，此為舊案子，會造成行銷和財務不一致，故程式面修改
        if(stringProjectID1.equals("H51A"))     stringProjectID1  =  "H51" ;// 特例，此為舊案子，會造成行銷和財務不一致，故程式面修改
        if(stringProjectID1.equals("H52"))       stringProjectID1  =  "H52A" ;
        if(stringProjectID1.equals("H32"))       stringProjectID1  =  "H32A" ;// 
        if(stringProjectID1.equals("H37"))       stringProjectID1  =  "H37A" ;// 
        if(stringProjectID1.equals("H40"))       stringProjectID1  =  "H40A" ;// 
        if(stringDepartNo.indexOf("053")!=-1  &&  stringProjectID1.equals("H42A"))  stringProjectID1  =  "H42" ;// 
        if(stringProjectID1.equals("H50"))       stringProjectID1  =  "H50A" ;// 
        //if(stringProjectID1.equals("E02A"))     stringProjectID1  =  "E2" ;// 
        if(stringProjectID1.equals("XM43"))    stringProjectID1  =  "M43" ;// 
        if(Pattern.matches("033\\d", stringDepartNo)  &&  stringProjectID1.equals("H36A"))        stringProjectID1  =  "H36" ;
        //
        if("033MP".equals(stringProjectID1)) stringInputDepartNo  =  stringProjectID1 ;// 
        if("O".equals(stringInOut)) {
            if("033MP".equals(stringProjectID1)) {
                  stringInputDepartNo  =  stringProjectID1 ;
            } else {
                stringInputDepartNo  =  stringDepartNo.substring(0,  3)  +  stringProjectID1;  
            }
        } else {
            if(",03396,03365,03335,033622,03363,0333,".indexOf(","+stringDepartNo+",")  ==  -1  &&
                !stringDepartNo.startsWith("022")) {
                if(exeUtil.isDigitNum (stringDepartNo)){
                  stringDepartNo   =  exeUtil.doSubstring(stringDepartNo,  0,  3) +  "1" ;// 
                }
            }
            stringInputDepartNo  =  stringDepartNo ;
        }
        if(stringProjectID1.equals("ST"))                       stringInputDepartNo  =  "03327" ;// 
        if("053M0101D".equals(stringInputDepartNo)) stringInputDepartNo   =  "053M401D" ;// 
        if("053M0201D".equals(stringInputDepartNo)) stringInputDepartNo   =  "053M0201" ;// 
        if("053H0101D".equals(stringInputDepartNo))  stringInputDepartNo   =  "053H0101" ;// 
        if(          "033ST".equals(stringInputDepartNo)) stringInputDepartNo  =  "03327" ;
        if(        "033XG2".equals(stringInputDepartNo)) stringInputDepartNo  =  "133G2" ;
        if(      "133O01A".equals(stringInputDepartNo)) stringInputDepartNo  =  "133O1" ;
        if(!"A".equals(stringType)) {
            //if(stringInputDepartNo.equals("033GT"))           stringInputDepartNo  =  "033H121A"; //2013-09-14 原轉成 033H115A 改成 033H121A
        }
        // 原
        /*int  intPos  =  stringInputDepartNo.indexOf("0333") ;
        if(intPos==0  &&  stringInputDepartNo.length()>4)       stringInputDepartNo    =  "033"+stringInputDepartNo.substring(4) ;
        if(",03396,03365,03335,033622,03363,0333,".indexOf(","+stringInputDepartNo+",")==-1  &&  stringInputDepartNo.indexOf("033")!=-1  &&  exeUtil.isDigitNum (stringInputDepartNo))     stringInputDepartNo   =  "0331" ;// 
        if(stringInputDepartNo.indexOf("023")!=-1  &&  exeUtil.isDigitNum (stringInputDepartNo))     stringInputDepartNo   =  "0231" ;// */
        System.out.println("getVoucherDepartNo--------------------E---") ;
        return  stringInputDepartNo ;
    }
    public  String[]  getDescriptionUnion(String  stringDestineExpenseDate,  String  stringAccountNo,  String  stringOriEmployeeNo,  String[][]  retFED1004,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        return  getDescriptionUnion(stringDestineExpenseDate,  stringAccountNo,  stringOriEmployeeNo,  retFED1004,  new  Vector(),  exeUtil,  exeFun) ;
    }
    public  String[]  getDescriptionUnion(String  stringDestineExpenseDate,  String  stringAccountNo,  String  stringOriEmployeeNo,  String[][]  retFED1004,  Vector  vectorItemCd,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String    stringItemCd         =  "" ;
        String    stringTemp           =  "" ;
        String[]  arrayDescription    =  new  String[5] ;             for(int  intNo=0  ;  intNo<arrayDescription.length  ;  intNo++)           arrayDescription[intNo]  =  "" ;
        String[]  arrayTemp             =  null ;
        //
        for(int  intNo=0  ;  intNo<5  ;  intNo++) {
             if(intNo  >=  retFED1004.length)       continue ;
             //
             stringItemCd  =  retFED1004[intNo][0].trim() ;
            if("E01".equals(stringItemCd)) {
                arrayDescription[intNo]  =  convert.replace(exeUtil.getDateConvertRoc(stringDestineExpenseDate),  "/",  "") ;
            } else if("E02".equals(stringItemCd)) {
                arrayDescription[intNo]  =  exeFun.getEmpName(stringOriEmployeeNo) ;
            }
            if("110301".equals(stringAccountNo)  &&  intNo==0)    arrayDescription[intNo]  =  "12675-0" ;
            //
            for(int  intNoL=0  ;  intNoL<vectorItemCd.size()  ;  intNoL++) {
                arrayTemp  =  (String[])vectorItemCd.get(intNoL) ;  if(arrayTemp  ==  null)  continue ;
                if(arrayTemp[0].equals(stringItemCd)) {
                    stringTemp                =  exeFun.getUseName(arrayTemp[0],  arrayTemp[1]) ;
                    //
                    if("".equals(stringTemp)) stringTemp  =  arrayTemp[1] ;
                    //
                    arrayDescription[intNo]    =   stringTemp ;   
                }
            }
        }
        return  arrayDescription ;
    }
    //
    public  String  getInformation( ) {
        return "---------------新增按鈕程式.preProcess()----------------";
    }
}




