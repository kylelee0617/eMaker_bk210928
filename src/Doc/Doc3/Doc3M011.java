package   Doc.Doc3 ;
import      jcx.jform.bTransaction;
import      java.io.*;
import      java.util.*;
import      jcx.util.*;
import      jcx.html.*;
import      jcx.db.*;
import      javax.swing.* ;
import      Farglory.util.FargloryUtil  ; 
import      Doc.Doc2M010 ;
public  class  Doc3M011  extends  bTransaction {
    public  boolean  action (String  value) throws  Throwable { 
        //201808check BEGIN
        System.out.println("chk==>"+getUser()+" , action value==>"+value.trim());
        if(value.trim().equals("新增") || value.trim().equals("修改") || value.trim().equals("刪除")) {
          if(getUser() != null && getUser().toUpperCase().equals("B9999")) {
            messagebox(value.trim()+"權限不允許!!!");
            return false;
          }
        }
        //201808check FINISH          
        // 回傳值為 true 表示執行接下來的資料庫異動或查詢
        // 回傳值為 false 表示接下來不執行任何指令
        // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
        // isTable12NoPageOK                    附件是否允許例外處理
        // doSyncBarCode                        新增前 BarCode 設值
        // doSetPurchaseSupervisor                  採購科主管之設定
        // doSetBackFlowData                    退件之承辦簽核時，直接簽核至該退件者前一關卡
        System.out.println("Doc3M011 Check BarCodeOld ==================> "+getValue("BarCodeOld"));
        getButton("ButtonHalfWidth").doClick() ;          // 全型處理
        getButton("ButtonSetTable12Data").doClick() ;       // 附件表格處理 
        getButton("ButtonPurchaseState").doClick() ;      // 請購狀態修正
        getButton("ButtonPurchaseSubject").doClick() ;    // 申購名稱處理
        getButton("ButtonTable3Default").doClick() ;
        getButton("ButtonDoc3M0123").doClick() ;        // 請購項目案別分攤處理
        getButton("ButtonGroupID").doClick()  ;           // 付款資料
        getButton("ButtonFlow").doClick() ;
        //
        Doc.Doc2M010                exeFun                              =  new  Doc.Doc2M010( ) ;
        FargloryUtil                      exeUtil                               =  new  FargloryUtil() ;
        String                              stringSubject                   =  getFunctionName() ;
        String                              stringSend                      =  "emaker@farglory.com.tw" ;
        String[]                            arrayUser                       =  {"B3018@farglory.com.tw"} ;
        String                  stringUndergoWirteCheck   =  ""+get("Doc3M011_UNDERGO_CHECK") ; put("Doc3M011_UNDERGO_CHECK",  "null") ;    put("Doc3M011_UNDERGO_CHECK_L",  stringUndergoWirteCheck) ;
        String                              stringBarCodeE              =  getValue("BarCode").trim( ) ;
        String                              stringBarCodeOldE           =  getValue("BarCodeOld").trim( ) ;
        String                              stringMessage                   =  stringSubject+"("+value.trim()+")異動資料庫-----"+stringBarCodeE+"-----------"+stringBarCodeOldE+"(使用者："+getUser()+")<br>" ;
        //
        try {
            String      stringBarCode  =  getValue("BarCode").trim( ); 
            getButton("ButtonTable3Default").doClick() ;// 案別分攤
            //getButton("button1").doClick() ;// 付款條件
            //if("SYS".equals(getUser()))  { 
            if("SYS".equals(getUser())  &&  !"刪除".equals(value))  { 
                doSyncBarCode( ) ; 
                /*
                if(!isTable12CheckOK(exeUtil,  exeFun))  return  false ;
                */
                /*if("刪除".equals(value.trim( ))) {
                    exeFun.doDeleteDoc1M040(stringBarCode) ;
                    exeFun.doDeleteDoc1M030(stringBarCode) ;
                }*/
                //getButton("ButtonSynDocFlow").doClick() ;
                //setFlowDataNew(exeUtil,   exeFun) ;
                getButton("ButtonTable10View").doClick() ;
                //
                return  true ;
            }
            put("Doc3M010_Status",  "STOP") ;

            if(!isFlowCheckOK(value.trim( ),  exeFun,  exeUtil))                 {put("Doc3M010_Status",  "NULL") ;  return  false ;}
            if(!isBatchCheckOK(value,  exeFun,  exeUtil)){
                put("Doc3M010_Status",                "NULL") ;  
                exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+getValue("BarCode").trim( )+"' ",  true,  exeUtil) ;
                return  false ;
            }

            if(!isCheckDoc3M080(exeFun,  exeUtil)) {
                put("Doc3M010_Status",                "NULL") ;  
                exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+getValue("BarCode").trim( )+"' ",  true,  exeUtil) ;
                return  false ;
            }

            setFlowData(exeUtil,  exeFun) ;
            if(!"刪除".equals(value.trim( ))  &&  stringSubject.indexOf("人總")  ==  -1)  doCheckDulFactoryNo (exeFun) ;
            if(getUser().endsWith("--")){
                put("Doc3M010_Status",                "NULL") ;
                return  false ;
            }
            // BarCode 自動取值
            String  stringID                      =  getValue("ID").trim() ;
            String  stringFunctionName  =  getFunctionName() ;
            if("新增".equals(value.trim())  &&  stringFunctionName.indexOf("承辦")!=-1)   {
                stringID  =  exeFun.getMaxIDForDoc3M011( ) ;
                setValue("ID",              stringID) ;
                //
                //if(!booleanNoPageDate) {
                //    String      stringDepartNoSubject         =  ""+get("EMP_DEPT_CD") ;
                //    if(stringFunctionName.indexOf("--")==-1)  setValue("BarCode",  exeFun.getMaxBarCode("Z")) ;
                //}
            }
            // 簽核流程記錄
            System.out.println("doInsertDoc5M0182-----------------------------------------------------S") ;
             doInsertDoc5M0182 (exeUtil,  exeFun) ;
             System.out.println("doInsertDoc5M0182-----------------------------------------------------E") ;
             //
            doReSetBarCode(exeUtil,  exeFun) ;
            doSyncBarCode( ) ;
            getButton("ButtonSyn").doClick() ;      // 同步公文追蹤
            //
            doKindNoD (exeUtil,  exeFun) ;
            // 流程記錄
            String     stringDeptCd               =  "" ;
            String      stringUser                   =  getUser().toUpperCase() ;
            String      stringComNo              =  getValue("ComNo").trim() ;
            String      stringDocNo               =  getValue("DocNo1").trim()+getValue("DocNo2").trim()+getValue("DocNo3").trim() ;
            String      stringClassNameList  =  getValue("ClassNameList").trim() ;
            String       stringToday              =  datetime.getToday("yymmdd") ;
                             stringToday              =  exeUtil.getDateConvertFullRoc(stringToday).replaceAll("/","") ;
            String[][]  retFE3D103               =  exeFun.getFE3D103(stringUser,  "",  stringToday) ;
            //
            if(retFE3D103.length  >  0)  stringDeptCd  =  retFE3D103[0][0].trim() ;
            //
            stringClassNameList  =  convert.replace(stringClassNameList,  "'",  "''") ;
            exeFun.doInsertForDoc3M011History(stringID,                                                   datetime.getTime("YYYY/mm/dd h:m:s"),  stringUser,  stringDeptCd,  getFunctionName()+" "+value+"---"+stringBarCodeE+"---"+stringBarCodeOldE,
                                          stringClassNameList+"---"+stringDocNo,  stringComNo,  true)  ;
            if("刪除".equals(value.trim( ))) {
                exeFun.doDeleteDoc1M040(stringBarCode) ;
                exeFun.doDeleteDoc1M030(stringBarCode) ;
                exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  true,  exeUtil) ;
            }
            if("B3849,B3446,".indexOf(getUser().toUpperCase())==-1)exeUtil.ClipCopy (getValue("BarCode").trim()) ;
        }catch(Exception e){
            Vector  vectorUse  =  exeFun.getEmployeeNoDoc3M011("P",  "") ;
                          arrayUser  =  (String[])  vectorUse.toArray(new  String[0]) ;
            exeUtil.doEMail(stringSubject,  stringMessage+"<br>"+e.toString(),  stringSend,  arrayUser) ;
            messagebox("程式發生錯誤") ;
            message("資訊\n"+e.toString()) ;
            //
            exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+getValue("BarCode").trim( )+"' ",  true,  exeUtil) ;
            return  false ;
        }
        getButton("ButtonTable10View").doClick() ;
        if(",B3018,".indexOf(","+getUser().toUpperCase()+",")!=-1){ messagebox("TEST") ;return  false ;}
        return true;
    }
    public  void  doInsertDoc5M0182 (FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
        String          stringFlow                      =  getFunctionName() ; 
        String          stringSql                       =  "" ; 
        String          stringUndergoWrite          = "" ;
        String          stringUndergoName         = "" ;
        String        stringUndergoWirteCheck   =  ""+get("Doc3M011_UNDERGO_CHECK_L") ; 
        JTable        jtable10                  =  getTable("Table10") ; 
        System.out.println(jtable10.getRowCount()+"-----------------------------------------------------S") ;
        if(jtable10.getRowCount()  ==  0)  return ;
        Hashtable     hashtableData             =  new  Hashtable() ;
        if(stringFlow.indexOf("採購")  !=  -1) {
            //stringUndergoName  =  ("Y".equals(stringUndergoWirteCheck))?"採購室承辦-送呈" : "採購室承辦" ;
            stringUndergoName  =  ("Y".equals(stringUndergoWirteCheck))?"完成流程" : "採購室承辦" ;
            stringUndergoWrite  =  "50" ;
        } else if(stringFlow.indexOf("承辦")  !=  -1) {
            stringUndergoName  =  "承辦" ;
            stringUndergoWrite  =  "10" ;
        } else {
            stringUndergoName  =  "業管" ;
            stringUndergoWrite  =  "10" ;
        }
        // 簽核記錄
        hashtableData.put("ID",                    getValue("ID").trim()) ;
        hashtableData.put("SourceType",              "A") ;
        hashtableData.put("BarCode",              getValue("BarCode").trim()) ;
        hashtableData.put("CheckDate",            datetime.getTime("YYYY/mm/dd h:m:s")) ;
        hashtableData.put("UNDERGO_WRITE",  stringUndergoWrite) ;
        hashtableData.put("UNDERGO_NAME",       stringUndergoName) ;
        hashtableData.put("CheckStatus",          "Y") ;
        hashtableData.put("CheckEmployeeNo",    getUser()) ;
        hashtableData.put("Descript",                     "") ;
        stringSql  =  exeFun.doInsertDBDoc("Doc5M0182",  hashtableData,  false,  exeUtil) ;addToTransaction(stringSql) ;
        System.out.println(""+stringSql) ;
    }
    // 例外-條碼編號重新設定
    public void  doReSetBarCode(FargloryUtil  exeUtil,  Doc2M010  exeFun)throws Throwable{
        String        stringBarCode        =  getValue("BarCode").trim() ;
        String        stringBarCodeOld  =  getValue("BarCodeOld").trim() ;
        Vector        vectorDoc2M040   =  new  Vector() ;
        Hashtable  hashtableAnd        =  new  Hashtable() ;
        Hashtable  hashtableTemp     =  new  Hashtable() ;
        Hashtable  hashtableData       =  new  Hashtable() ;
        //
        vectorDoc2M040  =  exeFun.getQueryDataHashtableDoc("Doc2M040",  hashtableAnd,  " ORDER BY  ACCT_D ",  new  Vector(),  exeUtil) ;
        if(vectorDoc2M040.size()  ==  0)                    return ;
        //
        hashtableTemp  =  (Hashtable)  vectorDoc2M040.get(0) ;
        if(hashtableTemp  ==  null)                              return ;
        //
        if("".equals(stringBarCodeOld) )                    return ;
        if(stringBarCodeOld.equals(stringBarCode))  return ;
        // 預算例外
        String  stringBarCodeDoc3M011  =  ""+hashtableTemp.get("BarCodeDoc3M011") ;
        //
        if("".equals(stringBarCodeDoc3M011)  ||  "null".equals(stringBarCodeDoc3M011)) return ;
        
        if(stringBarCodeDoc3M011.indexOf(stringBarCodeOld)  ==  -1) return ;
        hashtableAnd.put("BarCodeDoc3M011",   stringBarCodeDoc3M011) ;
        hashtableData.put("BarCodeDoc3M011",  stringBarCodeDoc3M011.replaceAll(stringBarCodeOld,  stringBarCode)) ;
        exeFun.doUpdateDBDoc("Doc2M040",  "",  hashtableData,  hashtableAnd,  true,  exeUtil) ;
    }
    public  void  doKindNoD (FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
        JTable   jtable3                      =  getTable("Table3") ;
        String    stringUndergoWrite  =  getValue("UNDERGO_WRITE").trim() ;
        String    stringField                =  (getFunctionName().indexOf("採購")==-1)?"BudgetMoney":"RealMoney" ;
        String    stringKindNoD          =  "" ;
        double  doubleMoney           =  0 ;
        for(int  intNo=0  ;  intNo<jtable3.getRowCount()  ;  intNo++) {
            doubleMoney  +=  exeUtil.doParseDouble((""+getValueAt("Table3",  intNo,  stringField)).trim()) ;
        }
        /* 2016-07-18 修正
        17-6 一般類請購單(1000萬以上)
        17-5 一般類請購單(200萬~1000萬)
        17-4 一般類請購單(200萬以下)
        */
        if(doubleMoney  <=  2000000) {
            stringKindNoD  =  "17-4" ;
        }else if(doubleMoney  <=  10000000) {
            stringKindNoD  =  "17-5" ;
        }else  {
            stringKindNoD  =  "17-6" ;
        }
        setValue("KindNoD",  stringKindNoD) ;
        // 依公文類別 KindNoD，修正預定結案日期
        String  stringKindDay      =  exeFun.getKindDay(stringKindNoD) ;
        String  stringCDate          =  getValue("CDate").trim().replaceAll("/",  "") ;
        String  stringCDateAC     =  exeUtil.getDateConvert(stringCDate.replaceAll("/",  "")) ;
        String   stringPreFinDate =  datetime.dateAdd(stringCDate,  "d",  exeUtil.doParseInteger(stringKindDay)) ;
        setValue("PreFinDate",  exeUtil.getDateConvertFullRoc(stringPreFinDate)) ;
    }
    // 其它合約登錄作業檢核
    public  boolean  isCheckDoc3M080 (Doc.Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable{
        Vector  vectorUserU  =  new  Vector() ;
        //vectorUserU.add("B3018") ;
        //vectorUserU.add("B1381") ;
        if(vectorUserU.indexOf(getUser().toUpperCase())  ==  -1)    return  true ;
        //
        String  stringFunctionName  =  getFunctionName() ;
        String  stringComNo             =  getValue("ComNo").trim();
        String  stringDocNo               =  getValue("DocNo").trim();
        String  stringBarCode           =  getValue("BarCode").trim();
        String  stringApply                =  getValue("ApplyType").trim() ;
        String  stringCDate              =  getValue("CDate").trim() ;
                    stringCDate             =  exeUtil.getDateConvertFullRoc(stringCDate) ;
        //
        if(stringFunctionName.indexOf("採購")  ==  -1)  return  true ;
        if("F".equals(stringApply))                                  return  true ;
        //判斷其它合約是否有登錄
        // 0  數目      1  AcceptRealDate       2  PickRealDate
        String[][]  retDoc8M010  =  getDoc8M010(exeFun) ;
        boolean  booleanFlag    =  true ;
        //
        if (retDoc8M010==null  ||   retDoc8M010.length==0      ||   retDoc8M010[0][0].trim().length()==0  ||   "0".equals(retDoc8M010[0][0].trim())) {
            // 不存在
            JInternalFrame  ff  =  getInternalFrame(stringFunctionName);
            int                       n  =  JOptionPane.showConfirmDialog(ff , "請至 [其它合約登錄作業] 進行合約登錄" , "訊息提示" , JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION){
                put("Brian_SA001_2_status" , "1");
                put("Brian_SA001_2_CDate" , stringCDate);
                showDialog("其他合約登錄作業(Doc8M010)" , "" , false , true , 100 , 100 , 850 , 650);
            } 
            return  false ;
        } else if (retDoc8M010!=null  &&  ((retDoc8M010[0][1].trim().length()!=0  &&  retDoc8M010[0][2].trim().length()==0) || 
                                                               (retDoc8M010[0][1].trim().length()==0  &&  retDoc8M010[0][2].trim().length()!=0)) ) {
            JInternalFrame  ff  =  getInternalFrame(stringFunctionName) ; 
            int                        n  =  JOptionPane.showConfirmDialog(ff , "請至其他合約登錄作業輸入收文實際日及採發實際日" , "訊息提示" , JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION){
                put("Brian_SA001_2_status" , "2");
                put("Brian_SA001_2_CDate" , stringCDate);
                //
                Hashtable htQuery = new Hashtable ();
                htQuery.put("ComNo" ,             stringComNo);
                htQuery.put("BarCode" ,          stringBarCode);
                htQuery.put("DocNo" ,              stringDocNo);
                htQuery.put("PurchaseType",  "");
                htQuery.put("CDate" ,               "");  
                showDialog("其他合約登錄作業(Doc8M010)" , "" , false , true , 100 , 100 , 850 , 650 , htQuery);
            }
            return  false ;
        } else if (retDoc8M010!=null  &&  retDoc8M010[0][1].trim().length()!=0  &&  retDoc8M010[0][2].trim().length()!=0) {
            String  stringID    =  getValue("ID").trim() ;
            String  stringSql  =  "" ;
            // 存在
            stringSql  =  " UPDATE  Doc8M010  SET  ClassName  =  (SELECT  ClassName "  +
                                                                                                         " FROM  Doc3M012 "  +
                                                          " WHERE  RecordNo  =  '1' "  +
                                                                " AND  ID  =  '"  +  convert.ToSql(stringID)  +  "'), "  +
                                                                          " FactoryNo   =  (SELECT  FactoryNo "  +
                                                                        " FROM  Doc3M012 "  +
                                                          " WHERE  RecordNo  =  '1'  "  +
                                                               " AND  ID  =  '"  +  convert.ToSql(stringID)  +  "'), "  +
                                                                          " PurchaseEmployeeNo  =  (SELECT  PurchaseEmployeeNo "  +
                                                                                            " FROM  Doc3M011 "  +
                                                                    " WHERE  ID  =  '"  +  convert.ToSql(stringID)  +  "'), "  +
                                                                          " BudgetMoney  =  (SELECT  SUM(ApplyMoney) "  +
                                                                              " FROM   Doc3M012 "  +
                                                             " WHERE  ID  =  '"  +  convert.ToSql(stringID)  +  "'), "  +
                                                                          " RealMoney       =  (SELECT  SUM(PurchaseMoney) "  +
                                                                              " FROM  Doc3M012 "  +
                                                            " WHERE  ID  =  '"  +  convert.ToSql(stringID)  +  "'), "  +
                                                                         " DifferenceMoney  =  (SELECT  ISNULL(SUM(ApplyMoney),0) - ISNULL(SUM(PurchaseMoney),0) "  +
                                                                                     " FROM  Doc3M012 "  +
                                                               " WHERE  ID  =  '"  +  convert.ToSql(stringID)  +  "'), "  +
                                                                         " DifferenceRate  =  (CASE (SELECT  SUM(ApplyMoney) FROM Doc3M012  WHERE  ID  =  '"+convert.ToSql(stringID)+"') "  +
                                                                                " WHEN  0   THEN  0  "  +
                                                                                                            " ELSE (SELECT  ((ISNULL(SUM(ApplyMoney),0) - ISNULL(SUM(PurchaseMoney),0)) /  ISNULL(SUM(ApplyMoney),1) * 100) "  +
                                                                           " FROM  Doc3M012 "  +
                                                                   " WHERE  ID  =  '"  +  convert.ToSql(stringID)  +  "')  end) "  +
                             " WHERE  ComNo  =  '"     +  convert.ToSql(stringComNo)    +  "' "  +
                                   " AND  BarCode  =  '"  +  convert.ToSql(stringBarCode)  +  "' "  +
                                   " AND  DocNo  =  '"      +  convert.ToSql(stringDocNo)     +  "' " ;
            addToTransaction(stringSql);
            System.out.println("---------------------------------------"+stringSql) ;
        }
        return  true ;
    }
    public  void  doCheckDulFactoryNo (Doc.Doc2M010  exeFun) throws  Throwable{
        String      stringFactoryNo         =  "" ;
        String      stringFactoryName    =  "" ;
        String      stringFactoryNameQ  =  "" ;
        String      stringMessage            =  "" ;
        Vector      vectorFactoryNo       =  getFactoryNoTable2( ) ;
        String[][]  retData                      =  null ;
        //
        for(int  intNo=0  ;  intNo<vectorFactoryNo.size()  ;  intNo++) {
            stringFactoryNo       =  (""+vectorFactoryNo.get(intNo)).trim() ;
            stringFactoryName  =  exeFun.getFactoryNameForDoc3M015(stringFactoryNo) ;
            //
            if(stringFactoryName.length()>2) {
                stringFactoryNameQ  =  stringFactoryName.substring(0,2) ;
            } else {
                stringFactoryNameQ  =  stringFactoryName ;
            }
            //
            retData  =  exeFun.getDoc3M015And("",  " AND  OBJECT_SHORT_NAME  LIKE  '"  +  stringFactoryNameQ  +  "%' ") ;
            if(retData.length  >  1) {
                if(!"".equals(stringMessage))  stringMessage  +=  "\n" ;
                stringMessage +=  "廠商 "+stringFactoryNo+"("+stringFactoryName+") 中文名稱，資料庫存在 "+(retData.length)+" 筆，請檢查廠商是否正確。" ;
            }
        }
        if(!"".equals(stringMessage))JOptionPane.showMessageDialog(null,  stringMessage,  "訊息",  JOptionPane.ERROR_MESSAGE) ;
    }
    public  boolean  isFlowCheckOK(String  value,  Doc.Doc2M010  exeFun,  FargloryUtil exeUtil) throws  Throwable {
        String      stringUnderGoWrite        =  getValue("UNDERGO_WRITE").trim( ) ;
        String      stringFlow                        =  getFunctionName() ;
        String      stringComNo                     =  getValue("ComNo").trim();
        String    stringNoPageDate        =  ""+get("NO_PAGE_DATE") ;     // 請購無紙化上線日期
        String    stringTodayL              =  exeUtil.getDateConvert(getValue("CDate")) ;
        boolean booleanNoPageDate       =  stringTodayL.compareTo(stringNoPageDate)>0 ;
        if(booleanNoPageDate  &&  getTableData("Table10").length<=0) {
            messagebox("簽核流程資料錯誤") ;
            return  false ;
        }
        //
        if(!"新增".equals(value.trim())) {
            String      stringID            =  getValue("ID").trim() ;
            String[][]  retDoc3M011  =  exeFun.getTableDataDoc("SELECT  UNDERGO_WRITE  FROM  Doc3M011  WHERE  ID  =  "+stringID+" ") ;
            if(retDoc3M011.length  ==  0) {
                JOptionPane.showMessageDialog(null,  "資料發生錯誤，請洽資訊室。1",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;   
            }
            stringUnderGoWrite  =  retDoc3M011[0][0].trim() ;
        }
        
        /*
        // 20201110 Kyle : 擬改為更新後續流程資料 START
        // 後續流程已使用，不可異動資料庫
        //請款
        String     stringDocNo     =  getValue("DocNo1").trim()+getValue("DocNo2").trim()+getValue("DocNo3").trim() ;
        String[][]  retDoc5M027  =  exeFun.getTableDataDoc("SELECT  M10.BarCode,  M10.UNDERGO_WRITE "  +
                                                     " FROM  Doc2M017 M17,  Doc2M010  M10 "  +
                                                  "  WHERE  M17.BarCode  =  M10.BarCode "  +
                                                      " AND  M17.PurchaseNo  =  '"+stringDocNo+"' "  +
                                                      " AND  M10.ComNo  =  '"         +stringComNo+"' ") ;
        if(retDoc5M027.length  >  0) {
            for(int  intNo=0  ;  intNo<retDoc5M027.length  ;  intNo++) {
                if(!"E".equals(retDoc5M027[0][1].trim())){
                    messagebox("後續流程(請款)已使用，不可異動資料庫。") ;
                    return  false ;   
                }
            }
        } 
        // Doc5M030 借款沖銷
        String[][]  retDoc5M030  =  exeFun.getTableDataDoc("SELECT  BarCode,  UNDERGO_WRITE "  +
                                                                                              " FROM  Doc6M010 "  +
                                                  " WHERE  PurchaseNo  =  '"+stringDocNo+"' "  +
                                                       " AND  ComNo  =  '"         +stringComNo+"' ") ;
        if(retDoc5M030.length  >  1) {
            for(int  intNo=0  ;  intNo<retDoc5M030.length  ;  intNo++) {
                if(!"E".equals(retDoc5M030[0][1].trim())){
                    messagebox("後續流程(借款沖銷)已使用，不可異動資料庫。") ;
                    return  false ;   
                }
            }
        }
        // 20201110 Kyle : 擬改為更新後續流程資料 END
        */
        

        // 體系主管 且 統購 且 付款資訊有資料
        String      stringBarCodeOld    =  getValue("BarCodeOld").trim( ) ;
        String[][]  retDoc3M013           =  exeFun.getDoc3M013(stringBarCodeOld,  "") ;
        if("X".equals(stringUnderGoWrite)) {
            JOptionPane.showMessageDialog(null,  "作廢資料，不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            return  false ;       
        }

        // 簽核流程變更2016-07-04       
        JTable    jtable10          =  getTable("Table10") ;
        boolean  booleanTable10  =  (jtable10.getRowCount()  >  0) ;  
        Vector     vectorUser          =  new  Vector() ;  vectorUser.add("B3018") ;
        if(booleanTable10)  return  isFlowCheckOKNew(stringUnderGoWrite,  vectorUser,  exeFun,  exeUtil) ;
        
        
        //
        if(stringFlow.indexOf("承辦")!=-1  &&  stringFlow.indexOf("--")==-1)  {
            if("G".equals(stringUnderGoWrite)  ||  "H".equals(stringUnderGoWrite)) {
                JOptionPane.showMessageDialog(null,  "人總資料，不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            /*if("R".equals(stringUnderGoWrite)) {
                JOptionPane.showMessageDialog(null,  "採購退件資料，不可執行 [修改] [刪除] 功能。1",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }*/
            if("B".equals(stringUnderGoWrite)) {
                JOptionPane.showMessageDialog(null,  "業管已簽核過不可執行 [修改] [刪除] 功能。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            if(!"A".equals(stringUnderGoWrite)  &&  !"".equals(stringUnderGoWrite.trim())) {
                JOptionPane.showMessageDialog(null,  "["+stringUnderGoWrite+"]非 [承辦] 流程不可執行 [修改] [刪除] 功能。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            //
            String  StringEmployeeNo  =  getValue("EmployeeNo").trim( ).toUpperCase() ;
            //
            if(vectorUser.indexOf(getUser())  ==  -1) {
                if(!StringEmployeeNo.equals(getUser().toUpperCase())) {
                    JOptionPane.showMessageDialog(null,  "由 "  + StringEmployeeNo  +  " 建立之資料，其它人不能異動處理。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    return  false ;
                }
            }
        } 
        if(stringFlow.indexOf("--承辦")!=-1){
            // I：待人總簽核    O：退件(人總)    
            if(!"".equals(stringUnderGoWrite)  &&  "I,O,G,H,C,".indexOf(stringUnderGoWrite)==-1) {
                JOptionPane.showMessageDialog(null,  "不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            //
            String  StringEmployeeNo  =  getValue("EmployeeNo").trim( ).toUpperCase() ;
            if(vectorUser.indexOf(getUser().toUpperCase())  ==  -1) {
                if(!StringEmployeeNo.equals(getUser().toUpperCase())) {
                    JOptionPane.showMessageDialog(null,  "由 "  + StringEmployeeNo  +  " 建立之資料，其它人不能異動處理。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    return  false ;
                }
            }
        }
        if(stringFlow.indexOf("業管") !=  -1)   {
            if("G".equals(stringUnderGoWrite)  ||  "H".equals(stringUnderGoWrite)) {
                JOptionPane.showMessageDialog(null,  "人總資料，不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            //  允許流程
            //  A；行銷-承辦   B：業管        R：退件(採購)    Q：退件(人總)
            // P：行銷專案     S：體系主管
            if("A,B,R,Q,P,S,J,".indexOf(stringUnderGoWrite)  ==  -1) {
                JOptionPane.showMessageDialog(null,  "未完成流程，不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
        }
        // 人總
        if(stringFlow.indexOf("人總") !=  -1)   {
            // I：待人總簽核    J：人總-審核
            if("I,J,K,".indexOf(stringUnderGoWrite)   ==  -1) {
                JOptionPane.showMessageDialog(null,  "不可執行 [修改] 功能。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
        }
        // 行銷
        if(stringFlow.indexOf("採購") !=  -1)   {
            // 固資 S
            String      stringToday                     =  exeUtil.getDateConvert(getValue("CDate").trim()) ;
            String      stringAssetDate              =  ""+get("ASSET_DATE"); 
            String      stringApply                      =  getValue("ApplyType").trim() ;
            boolean   booleanAssetDate         =  !"".equals(stringAssetDate)  &&  !"null".equals(stringAssetDate)  &&  stringToday.compareTo(stringAssetDate)>=0 ;
            // 固資 E
            if("R".equals(stringUnderGoWrite)) {
                JOptionPane.showMessageDialog(null,  "採購退件資料，不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            //  允許流程
            //  B：業管        K：人總-審核   Y：採購    H：人總-承辦
            // S：體系主管     C：採購
            if("B,K,C,S,H,Y,".indexOf(stringUnderGoWrite)  ==  -1) {
                JOptionPane.showMessageDialog(null,  "未完成流程，不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            if("B3018".equals(getUser()))  booleanAssetDate  =  true ;
            if(booleanAssetDate  &&  ",P83219,E35990,J29564,P83218,".indexOf(","+stringBarCodeOld+",")==-1){  
              //20180309 不檢查固定資產 By B03812
              /*
                // 固資
                if("D".equals(stringApply)) {
                  String[][]  retDoc1M040  =  exeFun.getTableDataDoc("SELECT  Barcode "  +
                                                               " FROM  Doc1M040 "  +
                                                             " WHERE  Barcode  =  '"+stringBarCodeOld+"' "+
                                                                " AND  DepartNo  LIKE  '023%' "+
                                                                " AND  DocStatus  =  '4' ") ;
                    if(retDoc1M040.length  ==  0) {
                        messagebox("[固定資產]請購單，[人總] 公文追蹤尚未出文，不可異動資料庫。") ;
                        return  false ;
                    }
                }
                */
            }
        }
        return  true ;
    }
    public  boolean  isFlowCheckOKNew(String  stringUnderGoWrite,  Vector  vectorUser,  Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        JTable      jtable10                            =  getTable("Table10") ;
        String      stringCheckDate10           =  "" ;
        String      stringUndergoType10         =  "" ;
        String      stringUndergoType10Next   =  "" ;
        String      stringCheckEmployeeNo10   =  "" ;
        String      stringUndergoWrite10            =  "" ;
        String      stringUndergoName10           =  "" ;
        String      stringFlow                            =  getFunctionName() ;
        String      stringUser                  = getUser().toUpperCase() ;
        String      stringApply                             =  getValue("ApplyType").trim() ;
        String      stringBarCodeOld                  =  getValue("BarCodeOld").trim( ) ;
        String      StringEmployeeNo            =  getValue("EmployeeNo").trim( ).toUpperCase() ;
        String      stringUndergoWirteCheck       =  ""+get("Doc3M011_UNDERGO_CHECK_L") ; 
        boolean   booleanUndergoWriteCheck  =  "Y".equals(stringUndergoWirteCheck) ;
        //
        if(",Q07511,Q81614,Q81619,Q14353,Q57670,Q81747,Q57711,Q14387,Q81146,Q81748,".indexOf(","+stringBarCodeOld+",")  !=  -1)  return  true ;
        if("Y".equals(stringUnderGoWrite)) {
            String  stringTemp  =  "" ;
            if("Y".equals(stringUnderGoWrite))  stringTemp  =  "已完成簽核流程" ;
            JOptionPane.showMessageDialog(null,  stringTemp+"，不可異動資料庫。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            return  false ;       
        }
        //
        for(int  intNo=0  ;  intNo<jtable10.getRowCount()  ;  intNo++) {
            stringCheckDate10             =  (""+getValueAt("Table10",  intNo,  "CheckDate")).trim() ;
            stringUndergoType10       =  (""+getValueAt("Table10",  intNo,  "UNDERGO_TYPE")).trim() ;
            stringCheckEmployeeNo10  =  (""+getValueAt("Table10",  intNo,  "CheckEmployeeNo")).trim() ;
            stringUndergoWrite10          =  (""+getValueAt("Table10",  intNo,  "UNDERGO_WRITE")).trim() ;
            stringUndergoName10          =  (""+getValueAt("Table10",  intNo,  "UNDERGO_NAME")).trim() ;
            if("".equals(stringCheckDate10)) {
                stringUndergoType10Next  =  stringUndergoWrite10 ;
                break ;
            }
        }
        String  stringCheckEmployeeNoSign10  =  "" ;
        for(int  intNo=jtable10.getRowCount()-1  ;  intNo>=0  ;  intNo--) {
            stringCheckDate10               =  (""+getValueAt("Table10",  intNo,  "CheckDate")).trim() ;
            stringUndergoType10         =  (""+getValueAt("Table10",  intNo,  "UNDERGO_TYPE")).trim() ;
            stringCheckEmployeeNo10     =  (""+getValueAt("Table10",  intNo,  "CheckEmployeeNo")).trim() ;
            stringCheckEmployeeNoSign10   =  (""+getValueAt("Table10",  intNo,  "CheckEmployeeNoSign")).trim() ;
            stringUndergoWrite10              =  (""+getValueAt("Table10",  intNo,  "UNDERGO_WRITE")).trim() ;
            stringUndergoName10             =  (""+getValueAt("Table10",  intNo,  "UNDERGO_NAME")).trim() ;
            if(!"".equals(stringCheckDate10)) {
                if("55".equals(stringUndergoWrite10)  &&  stringCheckEmployeeNoSign10.equals(getUser().toUpperCase())) {
                    continue ;
                } else {
                    break ;
                }
            }
        }
        // 承辦10→科20→室30→單位體系主管40→採購室(2)50,60→總管理處70→建設董事長80→集團董事長90
        String   stringToday            =  datetime.getToday("YYYY/mm/dd") ;
        if(stringFlow.indexOf("承辦")!=-1  &&  stringFlow.indexOf("--") ==  -1)   {
            if("G".equals(stringUnderGoWrite)  ||  "H".equals(stringUnderGoWrite)) {
                messagebox("人總資料，不可異動資料庫。") ;
                return  false ;
            }
            /*if("R".equals(stringUnderGoWrite)) {
                messagebox("採購退件資料，不可執行 [修改] [刪除] 功能。") ;
                return  false ;
            }*/
            // A 已送呈 不可執行 [修改] [刪除] 功能。
            if("M".equals(stringUnderGoWrite)) {
                messagebox("已送呈 不可執行 [修改] [刪除] 功能。") ;
                return  false ;             
            }
            if("B".equals(stringUnderGoWrite)) {
                messagebox("業管已簽核過不可執行 [修改] [刪除] 功能。") ;
                return  false ;
            }
            if(!"10".equals(stringUndergoWrite10)  &&  !"10".equals(stringUndergoType10Next)) { 
                messagebox("已簽核至 "+stringUndergoName10+" 流程，不可異動資料庫。") ;
                return  false ;
            }
            //
            if(vectorUser.indexOf(getUser())  ==  -1) {
                if(!StringEmployeeNo.equals(getUser().toUpperCase())) {
                    messagebox("由 "  + StringEmployeeNo  +  " 建立之資料，其它人不能異動處理。") ;
                    return  false ;
                }
            }
        }
        if(stringFlow.indexOf("--承辦")  !=  -1)  {
            if(!"10".equals(stringUndergoWrite10)  &&  !"10".equals(stringUndergoType10Next)) { 
                messagebox("已簽核至 "+stringUndergoName10+" 流程，不可異動資料庫。") ;
                return  false ;
            }
            //
            if(vectorUser.indexOf(getUser().toUpperCase())  ==  -1) {
                if(!StringEmployeeNo.equals(getUser().toUpperCase())) {
                    messagebox("由 "  + StringEmployeeNo  +  " 建立之資料，其它人不能異動處理。") ;
                    return  false ;
                }
            }
        }

        if(stringFlow.indexOf("業管") !=  -1)   {
            if("G".equals(stringUnderGoWrite)  ||  "H".equals(stringUnderGoWrite)) {
                messagebox("人總資料，不可異動資料庫。") ;
                return  false ;
            }
            if("A".equals(stringUnderGoWrite)) {
                messagebox("承辦尚未送呈資料，不可異動資料庫。") ;
                return  false ;
            }
            if(!"10".equals(stringUndergoWrite10)  &&  !"10".equals(stringUndergoType10Next)) { 
                messagebox("已簽核至 "+stringUndergoName10+" 流程，不可異動資料庫。") ;
                return  false ;
            }
        }
        
        
        // 採購
        if(stringFlow.indexOf("採購") !=  -1)   {
            if("R".equals(stringUnderGoWrite)) {
                messagebox("退件資料，不可異動資料庫。") ;
                return  false ;
            }
            if(!booleanUndergoWriteCheck  &&  "C".equals(stringUnderGoWrite)) {
                messagebox("已送呈資料，不可異動資料庫。") ;
                return  false ;
            }
            // 固資
            if(!"50".equals(stringUndergoWrite10)  &&  !"50".equals(stringUndergoType10Next)) { 
                if(exeUtil.doParseDouble(stringUndergoWrite10)  <  40) {
                    messagebox("[一般類]之請購單尚未簽核至 [單位體系主管]，不可異動資料庫。") ;
                    return  false ;
                } else {
                    messagebox("已簽核至 "+stringUndergoName10+" 流程，不可異動資料庫。") ;
                    return  false ;
                }
            }
            //2018/03/09 不檢查固資判斷  by B03812
            /*
            if("D".equals(stringApply)) {
              String[][]  retDoc1M040  =  exeFun.getTableDataDoc("SELECT  Barcode "  +
                                                           " FROM  Doc1M040 "  +
                                                         " WHERE  Barcode  =  '"+stringBarCodeOld+"' "+
                                                            " AND  DepartNo  LIKE  '023%' "+
                                                            " AND  DocStatus  =  '4' ") ;
                if(retDoc1M040.length  ==  0) {
                    messagebox("[固定資產]請購單，[人總] 公文追蹤尚未出文，不可異動資料庫。") ;
                    return  false ;
                }
            } 
            */
        }
        return  true ;
    }
    
    
    
    
    public  void  setFlowData(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable jtable10  =  getTable("Table10") ;
        if(jtable10.getRowCount()  >  0)  {
            setFlowDataNew(exeUtil,  exeFun) ;
            return ;
        }
        //
        String      stringFlow                  =  getFunctionName() ;
        String      stringApply                =  getValue("ApplyType").trim() ;
        
        //20180204 全部統一流程完成
        if("".equals(getValue("PurchaseEmployeeNo").trim()))   setValue("PurchaseEmployeeNo",     getUser().toUpperCase()) ;
        setValue("UNDERGO_WRITE",          "Y") ;
        setValue("FlowEndDate",                  datetime.getToday("YYYY/mm/dd")) ;

        //
        if(stringFlow.indexOf("承辦")!=-1)  {
            setValue("UNDERGO_WRITE",  "A") ;
        } 
        if(stringFlow.indexOf("--承辦")!=-1)  {
            // I：待人總簽核    O：退件(人總)    
            if(getValue("DocNo1").indexOf("033FZ")  !=  -1) {
                setValue("UNDERGO_WRITE",  "I") ;  // 人總待簽核
            } else {
                setValue("UNDERGO_WRITE",  "H") ;  // 變成單一流程
            }
        }
        if(stringFlow.indexOf("業管") !=  -1)   {
            if(getValue("DocNo1").indexOf("033FZ")  !=  -1) {
                setValue("UNDERGO_WRITE",  "J") ;  // 人總待簽核
            } else {
                setValue("UNDERGO_WRITE",  "B") ;
            }
        }
        // 人總
        if(stringFlow.indexOf("人總") !=  -1)   {
            setValue("UNDERGO_WRITE",  "K") ;
        }
        // 行銷
        if(stringFlow.indexOf("採購") !=  -1)   {
            if("".equals(getValue("PurchaseEmployeeNo").trim()))   setValue("PurchaseEmployeeNo",     getUser().toUpperCase()) ;
            setValue("UNDERGO_WRITE",          "Y") ;
            setValue("FlowEndDate",                  datetime.getToday("YYYY/mm/dd")) ;
        }
    }
    public  void  setFlowDataNew(FargloryUtil  exeUtil,   Doc2M010  exeFun) throws  Throwable {
        int         intPos                        =  -1 ;
        JTable        jtable10                      =  getTable("Table10") ;
        String        stringCheckDate               =  "" ;
        String        stringUndergoWrite            =  "" ;
        String        stringSql                     =  "" ;
        String        stringCheckEmployeeNo       =  "" ;
        String        stringUndergoType             =  "" ;
        String        stringDeptCd                =  "" ;
        String        stringEmployeeNo              =  "" ;
        String        stringUser                  =  getUser().toUpperCase() ;
        String      stringPurchaseEmployeeNo      =  getValue("PurchaseEmployeeNo").trim() ;
        String          stringFlow                          =  getFunctionName() ; 
        String        stringTodayTime             =  datetime.getTime("YYYY/mm/dd h:m:s") ;
        String        stringUndergoWirteCheck          =  ""+get("Doc3M011_UNDERGO_CHECK_L") ; 
        Hashtable     hashtableData                 =  new  Hashtable() ;
        boolean     booleanEndFlow              =  true ;
        boolean     booleanUndergoWriteCheck    =  "Y".equals(stringUndergoWirteCheck) ;
        //
        if(stringFlow.indexOf("採購") !=  -1)   {
            if("".equals(stringPurchaseEmployeeNo))    setValue("PurchaseEmployeeNo",     stringUser) ;
        }
        // 取得簽核人員之科室
        stringDeptCd              =  exeFun.getNameUnionFE3D("DEPT_CD",  "FE3D05",  " AND  EMP_NO  =  '"+stringUser+"' ",  new  Hashtable(),  exeUtil) ;  
        for(int  intNo=0  ;  intNo<jtable10.getRowCount()  ;  intNo++) {
            stringCheckDate                 =  (""+getValueAt("Table10",  intNo,  "CheckDate")).trim() ;
            stringCheckEmployeeNo         =  (""+getValueAt("Table10",  intNo,  "CheckEmployeeNo")).trim() ;
            stringUndergoType                  =  (""+getValueAt("Table10",  intNo,  "UNDERGO_TYPE")).trim() ;
            stringUndergoWrite          =  (""+getValueAt("Table10",  intNo,  "UNDERGO_WRITE")).trim() ;
            //
            // 判斷是否完成流程
            if("".equals(stringCheckDate)) {
                if("A".equals(stringUndergoType)) {
                    if(stringFlow.indexOf("採購") ==  -1 && !stringCheckEmployeeNo.equals(stringUser)) {
                      booleanEndFlow  =  false ;
                    }
                } else {
                    if(!stringDeptCd.startsWith(stringCheckEmployeeNo))booleanEndFlow  =  false ;
                    if(stringUser.equalsIgnoreCase("B9034")) {
                      booleanEndFlow = true;
                    }
                }
            }
            //
            if(stringFlow.indexOf("承辦")!=-1)  {
                if("10".equals(stringUndergoWrite)) {
                    intPos  =  intNo ;
                }
                stringEmployeeNo  =  getValue("OriEmployeeNo").trim() ;
            } else if(stringFlow.indexOf("業管")  !=  -1) {
                if("10".equals(stringUndergoWrite)) {
                    intPos  =  intNo ;
                }
                stringEmployeeNo  =  getValue("OriEmployeeNo").trim() ;
            } else {
                if("50".equals(stringUndergoWrite)) {
                    intPos  =  intNo ;
                }
                stringEmployeeNo  =  stringPurchaseEmployeeNo ;
            }
        }
        // 設定簽核流程表格
        // 採購 且 採購非送呈 時不作此處理
        if(stringFlow.indexOf("採購")==-1  ||  booleanUndergoWriteCheck) {
            setValueAt("Table10",  stringTodayTime,       intPos,  "CheckDate") ;
            setValueAt("Table10",  stringEmployeeNo,    intPos,  "CheckEmployeeNoSign") ;
        }
        // 設定主表格之簽核狀態
        if(stringFlow.indexOf("--承辦")!=-1)  {
            setValue("UNDERGO_WRITE",  "H") ;  // 變成單一流程
        } else if(stringFlow.indexOf("承辦")!=-1)   {
            setValue("UNDERGO_WRITE",  "Y".equals(stringUndergoWirteCheck) ? "M" : "A") ;
            
        } else if(stringFlow.indexOf("業管") !=  -1)  {
            setValue("UNDERGO_WRITE",  "B") ;
        } else {
            // 採購送呈機制
            String    stringUndergoWirteL         =   "" ;
            if("Y".equals(stringUndergoWirteCheck))  {
                //stringUndergoWirteL  =  "C" ;
                stringUndergoWirteL  =  "Y" ;
                if(booleanEndFlow) {
                    stringUndergoWirteL  =  "Y" ;
                }
            } else {
                stringUndergoWirteL  =  booleanEndFlow  ?  "Y"  :  "H" ;
            }
            setValue("UNDERGO_WRITE",        stringUndergoWirteL) ;  
        }
        // 判斷是否完成流程
        if(booleanEndFlow) {
            setValue("FlowEndDate",                 datetime.getToday("YYYY/mm/dd")) ;
        }
        //20180426 無採購科主管流程
        /*
        // 採購科主管處理
        doSetPurchaseSupervisor(booleanUndergoWriteCheck,  stringDeptCd,  stringTodayTime,  exeUtil,   exeFun) ;
        */
        // 退件之承辦簽核時，直接簽核至該退件者前一關卡
        doSetBackFlowData(stringTodayTime,  exeUtil,   exeFun) ;
    }
    public  void  doSetPurchaseSupervisor(boolean  booleanUndergoWriteCheck,  String  stringDeptCd,  String  stringTodayTime,  FargloryUtil  exeUtil,   Doc2M010  exeFun) throws  Throwable {
        if(!stringDeptCd.startsWith("021"))  return   ;
        if(",OO,CS,".indexOf(","+getValue("ComNo").trim()+",")  !=  -1)  return ;
        // 承辦人員之科主管
        String    stringPurchaseSupervisor              =  exeFun.getNameUnionFE3D("CENSOR_EMP_NO",  "FE3D110",  " AND  DEPT_CD  =  '"+stringDeptCd+"' AND  ITEM  =  '2' ",  new  Hashtable(),  exeUtil) ;          if("".equals(stringPurchaseSupervisor))  return ;
        boolean booleanPurchaseSupervisor      =  stringPurchaseSupervisor.equals(getUser().toUpperCase())   ;
        //
        String    stringTable            =  "Table10" ;
        JTable      jtable                  =  getTable(stringTable) ;
        String    stringRecordNo        =  "" ;
        String      stringUndergoWrite      =  "" ;
        for(int  intNo=0  ;  intNo<jtable.getRowCount()  ;  intNo++) {
            stringUndergoWrite        =  (""+getValueAt(stringTable,  intNo,  "UNDERGO_WRITE")).trim() ;
            stringRecordNo          =  (""+getValueAt(stringTable,  intNo,  "RecordNo")).trim() ;
            //
            if("55".equals(stringUndergoWrite)) {
                // 存在簽核欄位
                setValueAt(stringTable,  stringPurchaseSupervisor,  intNo,  "CheckEmployeeNo") ;
                if(booleanPurchaseSupervisor  &&  booleanUndergoWriteCheck) {
                    setValueAt(stringTable,  stringTodayTime,          intNo,  "CheckDate") ;
                    setValueAt(stringTable,  stringPurchaseSupervisor, intNo,  "CheckEmployeeNoSign") ;
                }
                return ;
            }
            if(exeUtil.doParseDouble(stringUndergoWrite)  >  55) {
                break ;
            }
        }
        Hashtable  hashtableData  =  new  Hashtable() ;
        // 不存在簽核欄位
        hashtableData.put("ID",                   getValue("ID").trim()) ;
        hashtableData.put("RecordNo",             stringRecordNo) ;
        hashtableData.put("SourceType",                 getFunctionName().indexOf("Doc3")!=-1?"A" : "B") ;
        hashtableData.put("BarCode",                getValue("BarCode").trim()) ;
        hashtableData.put("UNDERGO_WRITE",    "55") ;
        hashtableData.put("UNDERGO_NAME",       "採購科主管") ;
        hashtableData.put("UNDERGO_KindNo",     "審") ;
        hashtableData.put("UNDERGO_TYPE",       "A") ;
        hashtableData.put("CheckEmployeeNo",      stringPurchaseSupervisor) ;
        hashtableData.put("CheckEmployeeNoSign",  booleanPurchaseSupervisor?stringPurchaseSupervisor:"") ;
        hashtableData.put("CheckDate",              booleanPurchaseSupervisor?stringTodayTime:"") ;       
        hashtableData.put("Descript",                       "") ;
        String  stringSql  =  exeFun.doInsertDBDoc("Doc5M0181",  hashtableData,  false,  exeUtil) ;System.out.println(""+stringSql) ;addToTransaction(stringSql) ;
        System.out.println("新增採購簽核科主管------------------------------------------"+stringSql) ;
    }
    // 退件之業管簽核時，直接簽核至該退件者前一關卡
    public  void  doSetBackFlowData(String  stringTodayTime,  FargloryUtil  exeUtil,   Doc2M010  exeFun) throws  Throwable {
        //System.out.println("退件之承辦簽核時，直接簽核至該退件者前一關卡 doSetBackFlowData------------------------------------------S") ;
        if(getFunctionName().indexOf("業管")  ==  -1)                   return ;      // 非業管，退件不處理
        // 本請購單之前有退件記錄
        String        stringBarCode             =  getValue("BarCode").trim() ;
        Vector        vectorDoc5M0182             =  exeFun.getQueryDataHashtableDoc("Doc5M0182",  new  Hashtable(),  " AND BarCode  =  '"+stringBarCode+"' ORDER BY  CheckDate DESC ",  new  Vector(),  exeUtil) ;if(vectorDoc5M0182.size()  ==  0)  return ;
        String        stringCheckEmployeeNo       =  "" ;
        String      stringUndergoName       =  "" ;
        String        stringUndergoWrite          =  "" ;
        String        stringUndergoWriteCheck   =  "" ;
        String        stringCheckStatus         =  "" ;
        Hashtable     hashtableCheckEmployeeNo  =  new  Hashtable() ;
        for(int  intNo=0  ;  intNo<vectorDoc5M0182.size()  ;  intNo++) {
            stringCheckEmployeeNo     =  exeUtil.getVectorFieldValue(vectorDoc5M0182,  intNo,  "CheckEmployeeNo") ;
            stringUndergoName         =  exeUtil.getVectorFieldValue(vectorDoc5M0182,  intNo,  "UNDERGO_NAME") ;
            stringUndergoWrite          =  exeUtil.getVectorFieldValue(vectorDoc5M0182,  intNo,  "UNDERGO_WRITE") ;   if("null".equals(stringUndergoWrite)) stringUndergoWrite  =  "" ;
            stringCheckStatus         =  exeUtil.getVectorFieldValue(vectorDoc5M0182,  intNo,  "CheckStatus") ;
            // 簽核關卡代碼
            if("".equals(stringUndergoWrite)) {
                stringUndergoWrite    =  getUndergoWrite(stringUndergoName,  exeUtil) ; 
            }
            // 簽核關卡代碼 vs 簽核人員
            hashtableCheckEmployeeNo.put(stringUndergoWrite,  stringCheckEmployeeNo) ;
            //System.out.println(intNo+"-----簽核關卡代碼 vs 簽核人員-------------------------------------1") ;
            //
            if("承辦".equals(stringUndergoName))                continue ;
            if(exeUtil.doParseDouble(stringUndergoWrite)  <=  20)     break ;
            // 退件
            if(!"N".equals(stringCheckStatus))  continue ;
            // 記錄退件關卡
            stringUndergoWriteCheck  =  stringUndergoWrite ;
            //System.out.println(intNo+"-----退件關卡("+stringUndergoWriteCheck+")-------------------------------------2") ;
        }
        if("".equals(stringUndergoWriteCheck))  return ;
        //
        JTable        jtable10                    =  getTable("Table10") ;
        String        stringCheckDateL          =  "" ;
        String        stringUndergoWriteL       =  "" ;
        String        stringUndergoWrite2L        =  "" ;
        String        stringCheckEmployeeNoSign  =  "" ;
        for(int  intNo=0  ;  intNo<jtable10.getRowCount()  ;  intNo++) {
            stringUndergoWriteL             =  (""+getValueAt("Table10",  intNo,  "UNDERGO_WRITE")).trim() ;
            stringCheckDateL              =  (""+getValueAt("Table10",  intNo,  "CheckDate")).trim() ;
            stringCheckEmployeeNo           =  (""+getValueAt("Table10",  intNo,  "CheckEmployeeNo")).trim() ;
            stringUndergoWrite2L            =  exeUtil.doSubstring(stringUndergoWriteL,  0  ,  2) ;
            // 之前關卡對應之簽核人員
            stringCheckEmployeeNoSign     =  ""+hashtableCheckEmployeeNo.get(stringUndergoWriteL) ;
            if("null".equals(stringCheckEmployeeNoSign)) {
                stringCheckEmployeeNoSign     =  ""+hashtableCheckEmployeeNo.get(stringUndergoWrite2L) ;
            }
            if("null".equals(stringCheckEmployeeNoSign))   stringCheckEmployeeNoSign  =  "" ;
            if("".equals(stringCheckEmployeeNoSign)) {
                stringCheckEmployeeNoSign  =  convert.StringToken(stringCheckEmployeeNo,  ",")[0].trim() ;
            } else if(stringCheckEmployeeNo.indexOf(stringCheckEmployeeNoSign)  ==  -1) {
                stringCheckEmployeeNoSign  =  convert.StringToken(stringCheckEmployeeNo,  ",")[0].trim() ;
            }
            //System.out.println(intNo+"-----之前關卡("+stringUndergoWriteL+")對應之簽核人員--("+stringCheckEmployeeNoSign+")-------------------------------------0") ;
            // 已有簽核資料不處理
            if(!"".equals(stringCheckDateL))  continue ;
            //System.out.println(intNo+"-----已有簽核資料不處理-------------------------------------1") ;
            // 判斷是否是退件單位
            if(stringUndergoWriteCheck.length()  >  2) {
                //System.out.println(intNo+"-----判斷是否是退件單位-------------------------------------2-1") ;
                if(stringUndergoWriteCheck.equals(stringUndergoWriteL))  break ;
                //System.out.println(intNo+"-----判斷是否是退件單位-------------------------------------2-2") ;
            } else {
                //System.out.println(intNo+"-----判斷是否是退件單位-------------------------------------3-1") ;
                if(stringUndergoWriteCheck.equals(stringUndergoWrite2L))  break ;
                //System.out.println(intNo+"-----("+stringUndergoWriteCheck+")("+stringUndergoWrite2L+")判斷是否是退件單位-------------------------------------3-2") ;
            }
            // 採購承辦之後不作處理
            if(exeUtil.doParseDouble(stringUndergoWrite2L)  >=  50)  break ;
            //System.out.println(intNo+"-----採購承辦之後不作處理-------------------------------------4") ;
            // 省略流程
            setValueAt("Table10",  stringTodayTime,             intNo,  "CheckDate") ;
            setValueAt("Table10",  stringCheckEmployeeNoSign,  intNo,  "CheckEmployeeNoSign") ;
        }
        getButton("ButtonSynDocFlow").doClick() ;
        //System.out.println("退件之承辦簽核時，直接簽核至該退件者前一關卡 doSetBackFlowData------------------------------------------E") ;
    }
    public  String  getUndergoWrite(String  stringUndergoName,  FargloryUtil  exeUtil) throws  Throwable {
        if("承辦科主管".equals(stringUndergoName))         return  "20" ;
        if("行銷企劃室承辦".equals(stringUndergoName))     return  "32-01" ;
        if("行銷企劃室主管".equals(stringUndergoName))     return  "32-02" ;
        if("行銷企劃室".equals(stringUndergoName))         return  "33" ;
        if("行銷管理處".equals(stringUndergoName))         return  "34" ;
        if("單位體系主管".equals(stringUndergoName))      return  "40" ;
        if("設計監理".equals(stringUndergoName))            return  "42" ;
        if("資訊室主管".equals(stringUndergoName))         return  "43" ;
        if("人總室總務主管".equals(stringUndergoName))     return  "46" ;
        if("採購室承辦".equals(stringUndergoName))         return  "50" ;
        return  "" ;
    }
    public  void  doSyncBarCode( ) throws  Throwable {
        String   stringBarCode        =  getValue("BarCode").trim( ) ;
        String   stringBarCodeT     =  "" ;
        JTable  jtable                     =  null ;
        //   1-3,6
        //  Table1    請購項目                  Table2    付款資訊                    Table3    案別分攤
        //  Table4    使用狀態                        Table5    無                                     Table6    請購資訊之案別分攤
        //  Table7    033FG 案別細項控管      Table8    033FG 簽呈控管              Table9    請購資訊之POP資訊         
        //  Table10  請購單-簽核流程             Table11  請購單-流程記錄             Table12   附件
        //  Table13  無                                    Table14  無                                    Table15  無
        //  Table16 請購單-代銷合約備查    Table17 議價資訊表格        Table18 廠商議價資訊表格
        for(int  intTableNo=1  ;  intTableNo<=18  ;  intTableNo++) {
            if(intTableNo==4)    continue ;
            if(intTableNo==5)    continue ;
            if(intTableNo==13)  continue ;
            if(intTableNo==14)  continue ;
            if(intTableNo==15)  continue ;
            jtable  =  getTable("Table"+intTableNo) ;
            for(int  intNo=0  ;  intNo<jtable.getRowCount()  ;  intNo++) {
                   stringBarCodeT  =  (""+getValueAt("Table"+intTableNo,  intNo,  "BarCode")).trim() ;
                setValueAt("Table"+intTableNo,  stringBarCode,  intNo,  "BarCode") ;
            }
        }
    }
      // 前端資料檢核，正確回傳 True
      public  boolean  isBatchCheckOK(String  stringFunction,  Doc.Doc2M010  exeFun,  Farglory.util.FargloryUtil  exeUtil)throws  Throwable {
         setValue("DocNo1",  getValue("DepartNo").trim()) ;
         // 
         String       stringFlow                          =  getFunctionName() ;
         String       stringBarCode                   =  getValue("BarCode").trim( ) ;
         String       stringBarCodeOld            =  getValue("BarCodeOld").trim( ) ;
         String[][]   retDoc1M040                   =  exeFun.getDoc1M040(stringBarCodeOld) ;
         String     stringUndergoWirteCheck   =  ""+get("Doc3M011_UNDERGO_CHECK_L") ; 
         boolean  booleanUndergoWriteCheck  =  stringFlow.indexOf("採購")==-1  ||
                                            "Y".equals(stringUndergoWirteCheck)  ;
         // 代銷合約
        put("Doc7M02691_STATUS",  "DB") ;
        getButton("ButtonTable16").doClick() ;
        if(booleanUndergoWriteCheck  &&  !"OK".equals(""+get("Doc7M02691_STATUS"))) {
            messagebox("請輸入 代銷合約 資料") ;
            return  false ;
        }
         // 特殊預算控管 限制人員 
         String     stringSpecBudget       =  ","+get("SPEC_BUDGET")+"," ;
         String     stringEmployeeNo         =  getValue("EmployeeNo").trim() ;
         String     stringDocNo1                =  getValue("DocNo1").trim() ;
         String     stringFunctionType     =  exeFun.get033FGFunctionType (stringDocNo1,  exeUtil) ;
         if(booleanUndergoWriteCheck  &&
            stringSpecBudget.indexOf(","+stringDocNo1+",")!=-1  &&  exeFun.getTableDataDoc("SELECT  EmployeeNo "  +
                                                                                                                                                        " FROM  Doc3M011_EmployeeNo "  +
                                                                               " WHERE  FunctionType  =  '"+stringFunctionType+"' "  +
                                                                                      " AND  EmployeeNo  =  '"+stringEmployeeNo+"' ").length  <=  0) {
              messagebox("非特殊人員不允許申請 "+stringDocNo1+" 費用。\n(有問題請洽 [行銷企劃室])") ;
            return  false ;
         }
         if(stringSpecBudget.indexOf(","+stringDocNo1+",")!=-1  &&  getTable("Table7").getRowCount()  ==  0) {
              getButton("Button033FGInput").doClick() ;
         }
         //
         if(stringFlow.indexOf("人總")  !=  -1)  return  true ;
        // 公司代碼
        String  stringComNo  =  getValue("ComNo").trim( ) ;
        if("".equals(stringComNo)) {
            JOptionPane.showMessageDialog(null,  "[公司代碼] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;       
        }
        /*
        if(!"A".equals(exeFun.getUseType(stringComNo,  ""))) {
            messagebox("公司 "  +  stringComNo  +  "("+exeFun.getCompanyName(stringComNo)+") 不允許使用。\n(有問題請洽 [財務室])") ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;       
        }
        */

         // 公文編碼
         String       stringPurchaseNo1         =  getValue("DocNo1").trim( ) ;
         String       stringPurchaseNo2         =  getValue("DocNo2").trim( ) ;
         String       stringPurchaseNo3         =  getValue("DocNo3").trim( ) ;
         String       stringKindNo                    =  getValue("KindNo").trim( ) ;
         String       stringDepartNoSubject   =  ""+get("EMP_DEPT_CD") ;
         String       stringPurchaseNo            =  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3 ;
         //
         if("".equals(stringPurchaseNo1)) {
            JOptionPane.showMessageDialog(null,  "[請購單號1] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("DocNo1").requestFocus( ) ;
            return  false ;
         }
         if("".equals(stringPurchaseNo2)) {
            JOptionPane.showMessageDialog(null,  "[請購單號2] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("DocNo2").requestFocus( ) ;
            return  false ;
         }
        // 請購單之日期檢核
        String  retDateRoc  =  exeUtil.getDateFullRoc (stringPurchaseNo2+"01",  "12345678") ;
        if(retDateRoc.length( )  !=  9) {
            JOptionPane.showMessageDialog(null,  "[請購單號2] 格式錯誤(yymm)。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("DocNo2").requestFocus( ) ;
            return  false ;
        }
        retDateRoc              =  exeUtil.getDateConvertRoc(retDateRoc).replaceAll("/","") ;
        stringPurchaseNo2  =  datetime.getYear(retDateRoc)+datetime.getMonth(retDateRoc) ;
        if( "新增".equals(stringFunction)  &&  "0231,".indexOf(stringDepartNoSubject+",")!=-1) {
            // 自動給號
            stringPurchaseNo3        =  exeFun.getDocNo3Max(stringComNo,  stringKindNo,  stringPurchaseNo1,  stringPurchaseNo2,  stringPurchaseNo1.startsWith("023")?"B":"A") ;
            stringPurchaseNo          =  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3 ;
            setValue("DocNo3",  stringPurchaseNo3) ;
        } else {
             if("".equals(stringPurchaseNo3)) {
                JOptionPane.showMessageDialog(null,  "[請購單號3] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                getcLabel("DocNo3").requestFocus( ) ;
                return  false ;
             }
             // 重覆
             System.out.println("BarCode Check ======> Old:"+stringBarCodeOld+" :::::::::::::::::::::::::::::: New:"+stringBarCode+" <================ END");
            boolean  booleanFlag  =  exeFun.isExistDocNoCheck(stringPurchaseNo1,  stringPurchaseNo2,  stringPurchaseNo3,  stringKindNo,  stringComNo,  stringBarCodeOld) ;
             if(!booleanFlag) {
                JOptionPane.showMessageDialog(null,  "[請購單號] 發生重覆。1",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                getcLabel("DocNo3").requestFocus( ) ;
                return  false ;
             }
         }
        // 以條碼編號檢核公文編號是否存在，且一致
        String      stringDocNoOld  =  getValue("DocNoOld").trim( ) ;
        String[][]  retDoc1M030     =  exeFun.getDoc1M030(stringBarCodeOld) ;
        boolean  booleanNoUser  =  exeFun.isNoUseDoc1M040(stringBarCodeOld) ;
         if(retDoc1M030.length>0  &&  "5".equals(retDoc1M030[0][6].trim())) {
            messagebox("公文追蹤系統中，此請購單已 [作廢]，不允許執行。") ;
            return  false ;
         }
        if(retDoc1M030.length  >  0  &&  !booleanNoUser) {
            if(!stringKindNo.equals(retDoc1M030[0][5].trim())) {
                messagebox("[公文類別] 不一致，請洽 [資訊企劃室] 處理。 ") ;
                return  false ;
            }
            if(!stringDocNoOld.equals(retDoc1M030[0][2].trim()+retDoc1M030[0][3].trim()+retDoc1M030[0][4].trim())) {
                messagebox("[公文代碼] 不一致，請洽 [資訊企劃室] 處理。 "+stringDocNoOld) ;
                return  false ;
            }
        }
         setValue("DocNo", stringPurchaseNo) ;
        if("刪除".equals(stringFunction)) {
            if(retDoc1M040.length  >  1) {
                messagebox(" 請購單已有收發文，不允許刪除，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            if(retDoc1M040.length==1  &&  !"1".equals(retDoc1M040[0][6].trim())) {
                messagebox(" 請購單非創文，不允許刪除，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            return  true ;
        }
        if(!"".equals(stringBarCode)  &&  !"".equals(stringBarCodeOld)  &&  !stringBarCode.equals(stringBarCodeOld)) {
            if(stringFlow.indexOf("承辦")  !=  -1) {
                messagebox(" 請購單不允許變更條碼編號，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            if(booleanNoUser  &&  retDoc1M040.length  >  1) {
                messagebox(" 請購單已有收發文，不允許變更條碼編號，請洽 [資訊企劃室]。") ;
                return  false ;
            }
            if(booleanNoUser  &&  retDoc1M040.length==1  &&  !"1".equals(retDoc1M040[0][6].trim())) {
                messagebox(" 請購單非創文，不允許變更條碼編號，請洽 [資訊企劃室]。") ;
                return  false ;
            }
        }
        // 條碼不可空白
        boolean     booleanFlow             =  !(stringFlow.indexOf("承辦")  !=  -1  ||  stringFlow.indexOf("業管")  !=  -1    ||  stringFlow.indexOf("審核")  !=  -1) ;
        String        stringNoPageDate    =  ""+get("NO_PAGE_DATE") ;     // 請購無紙化上線日期
        String      stringToday          =  exeUtil.getDateConvert(getValue("CDate")) ;
        boolean   booleanNoPageDate   =  stringToday.compareTo(stringNoPageDate)>=0 ;
        if("新增".equals(stringFunction)) {
            String      stringTemp      =  exeFun.getNoPageAutoBarCode("S",  exeUtil) ;
            Hashtable   hashtableData   =  new  Hashtable() ;
            //
            if(!"".equals(stringTemp)) {
                stringBarCode  =  stringTemp ;
                hashtableData.put("BarCode",          stringBarCode) ;
                hashtableData.put("EDateTime",        datetime.getTime("YYYY/mm/dd h:m:s")) ;
                hashtableData.put("LastEmployeeNo",   getUser()) ;
                hashtableData.put("Descript",         "請購") ;
                exeFun.doInsertDBDoc("Doc2M044_AutoBarCode",  hashtableData,  true,  exeUtil) ;
            }
            put("BarCode_Tmp",  stringBarCode) ;
        }
        if("".equals(stringBarCode)) {
            String  stringTemp   =  "" ;
                    stringTemp  =  "自動取號 之 [條碼編號] 已使用完。" ;
            messagebox(stringTemp) ;
            getcLabel("BarCode").requestFocus( ) ;
            return  false ;
        }

        //
        if(!"".equals(stringBarCode)) {
            String  stringBarCode1  = stringBarCode.substring(0,1) ;
            if(!(stringFlow.indexOf("承辦")!=-1  &&  stringFlow.indexOf("--")==-1)  &&  exeFun.getBarCodeFirstChar( ).indexOf(stringBarCode1)==-1) {
                if(stringBarCode1.equals("S")) {
                } else {
                    messagebox("[條碼編號] 不正確，請重新輸入。") ;
                    return  false ;
                }
            }
        }
        //
        // 資料庫存在檢核
        if(stringFlow.indexOf("業管")  !=  -1  ||  stringFlow.indexOf("審核")  !=  -1) {
            if(!stringBarCodeOld.equals(stringBarCode)  &&  !exeFun.isExistBarCodeCheck(stringBarCode)) {
                messagebox("[條碼編號] 已存在資料庫中，請執行 [查詢] 後，作修改。") ;
                getcLabel("BarCode").requestFocus( ) ;
                return  false ;
            }
        }
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
            return  false ;
        }
         // 需求日期
         String  stringNeedDate  =  getValue("NeedDate").trim() ;
         if("".equals(stringNeedDate)) {
            messagebox("[需求日期] 不可為空白。") ;
            getcLabel("NeedDate").requestFocus( ) ;
            return  false ; 
         }
        stringNeedDate  =  exeUtil.getDateFullRoc (stringNeedDate,  "需求日期") ;
        if(stringNeedDate.length( )  !=  9) {
            messagebox(stringNeedDate) ;
            getcLabel("NeedDate").requestFocus( ) ;
            return  false ;
        }
        setValue("NeedDate",      stringNeedDate) ;
        setValue("NeedDateEnd",   stringNeedDate) ;
         //預定結案日期
         String  stringPreFinDate  =  getValue("PreFinDate").trim( ) ;
         if("".equals(stringPreFinDate)) {
            messagebox("[預定結案日期] 不可為空白。") ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ; 
         }
        stringPreFinDate  =  exeUtil.getDateFullRoc (stringPreFinDate,  "預定結案日期") ;
        if(stringPreFinDate.length( )  !=  9) {
            messagebox(stringPreFinDate) ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ;
        }
        setValue("PreFinDate",  stringPreFinDate) ;
        // 承辦人員
        String  stringOriEmployeeNo  =  getValue("OriEmployeeNo").trim( ) ;
        if("".equals(stringOriEmployeeNo)) {
            messagebox("[承辦人員] 不可為空白。") ;
            getcLabel("OriEmployeeNo").requestFocus( ) ;
            return  false ; 
        }
        // 
        String  stringEmpName  =  exeFun.getEmpName(stringOriEmployeeNo) ;
        if("".equals(stringEmpName)) {
            messagebox("[承辦人員] 不存在資料庫中。\n(有問題請洽 [人總室])") ;
            getcLabel("OriEmployeeNo").requestFocus( ) ;
            //if(!getUser().startsWith("b"))
            return  false ; 
        }

        if(",01,12,".indexOf(","+stringComNo+",")  !=  -1) {
            String  stringDoc3M011EmployeeNo  =  exeFun.getTableDataDoc("SELECT  EmployeeNo  FROM  Doc3M011_EmployeeNo  WHERE  FunctionType = 'W' ")[0][0].trim() ;
            if(stringDepartNo.startsWith("033") ||  stringDepartNo.startsWith("053") ||  stringDepartNo.startsWith("133")) {
                if(!stringDoc3M011EmployeeNo.equals(stringOriEmployeeNo)) {
                    stringOriEmployeeNo  =  stringDoc3M011EmployeeNo ;
                    setValue("OriEmployeeNo",  stringDoc3M011EmployeeNo) ;
                }
            }
            // 特別控管公司
            String  stringComNoCF  =  exeFun.getComNoForEmpNo(stringOriEmployeeNo) ;
            if(stringFlow.indexOf("採購")== -1  &&  !stringComNo.equals(stringComNoCF)) {
                messagebox("[承辦人員] 投保公司為 ["+exeFun.getCompanyName(stringComNoCF)+"] 非 ["+exeFun.getCompanyName(stringComNo)+"]，不允許異動。") ;
                getcLabel("OriEmployeeNo").requestFocus( ) ;
                return  false ; 
            }
        }
        //   0  DEPT_CD     1  EMP_NO       2  EMP_NAME 
        String[][]  retFE3D05  =  exeFun.getFE3D05(stringOriEmployeeNo) ;
        if(retFE3D05.length  == 0)  return  false ;
        if("B3841,".indexOf(getUser())==-1   &&    // B3841 徐玉珊
          stringFlow.indexOf("採購")==-1) {
            // 
            Hashtable  hashtableData  =  new  Hashtable() ;
            hashtableData.put("DEPT_CD_USER",   retFE3D05[0][0].trim()) ;
            hashtableData.put("DEPT_CD",          stringDepartNo) ;
            hashtableData.put("EmployeeNo",     getValue("EmployeeNo").trim()) ;
            //String  stringErr  =  getDocNoUserCheckErrTEST (hashtableData,  exeUtil) ;
            String  stringErr  =  exeFun.getDocNoUserCheckErr (hashtableData,  exeUtil) ;
            if(!"".equals(stringErr)) {
                messagebox(stringErr) ;
                getcLabel("DepartNo").requestFocus( ) ;
                return  false ;
            }
        }

        if(booleanFlow) {
            // 採購人員
            String  stringPurchaseEmployeeNo  =  getValue("PurchaseEmployeeNo").trim( ) ;
            if("".equals(stringPurchaseEmployeeNo)) {
                //messagebox("[採購人員] 不可為空白。") ;
                //getcLabel("PurchaseEmployeeNo").requestFocus( ) ;
                //return  false ; 
            } else {
                stringEmpName  =  exeFun.getEmpName(stringPurchaseEmployeeNo) ;
                if("".equals(stringEmpName)) {
                    messagebox("[採購人員] 不存在資料庫中。\n(有問題請洽 [人總室])") ;
                    getcLabel("PurchaseEmployeeNo").requestFocus( ) ;
                    return  false ; 
                }
            }
        }
        // 申請分類  ApplyType
        String  stringApplyType        =  getValue("ApplyType").trim() ;
        String  stringUnderGoWrite  =  getValue("UNDERGO_WRITE").trim( ) ;
        if("".equals(stringApplyType)) {
            messagebox("請選擇 [申請分類]。") ;
            getcLabel("ApplyType").requestFocus( ) ;
            return  false ; 
        }
        if(!"F".equals(stringApplyType)  &&  stringFlow.indexOf("採購")==-1) {
            setTableData("Table2",  new  String[0][0]) ;
        }
        // 檢附
        String  stringCheckAdd  =  getValue("CheckAdd").trim() ;
        if("".equals(stringCheckAdd)) {
            messagebox("請選擇 [檢附]。") ;
            getcLabel("CheckAdd").requestFocus( ) ;
            return  false ; 
        }
        String  stringCheckAddDescript  =  getValue("CheckAddDescript").trim() ;
        if("F".equals(stringCheckAdd)) {
            if("".equals(stringCheckAddDescript)) {
                messagebox("請輸入 [檢附其它說明] 內容。") ;
                getcLabel("CheckAddDescript").requestFocus( ) ;
                return  false ; 
            }
        } else {
            setValue("CheckAddDescript",  "") ;
        }

        // 分析及要求  Analysis
        String    stringAnalysis          =  getValue("Analysis").trim() ;
        String    stringPayConditionTXT   =  getValue("PayConditionTXT").trim() ;
        boolean   booleanFlag           =  isTable1PurchaseMoney0(exeUtil) ;
        if(booleanUndergoWriteCheck  &&  stringFlow.indexOf("採購")!=-1  &&  "".equals(stringAnalysis)) {
            if(!booleanFlag) {
                messagebox("[分析及要求] 不允許空白。") ;
                return  false ;
            }
        }
        if(booleanUndergoWriteCheck  &&  stringFlow.indexOf("採購")!=-1  &&  "".equals(stringPayConditionTXT)) {
            if(!booleanFlag) {
                messagebox("[付款說明] 不允許空白。") ;
                return  false ;
            }
        }
        // 請購資訊
        if(!isTable1CheckOK(booleanUndergoWriteCheck,  exeFun,  exeUtil))  return  false ;
        // 付款資訊
        boolean  booleanApply  =  "096/11/26".compareTo(exeUtil.getDateConvertFullRoc(getValue("CDate").trim()))<=0  &&  "F".equals(getValue("ApplyType").trim()) ;
        if((booleanFlow  ||  booleanApply)  &&  !"T".equals(""+get("Doc3M011_Negative"))  &&  !isTable2CheckOK(booleanUndergoWriteCheck,  exeFun,  exeUtil))  return  false ;
        // 案別分攤
        if(!isTable3CheckOK(booleanUndergoWriteCheck,  stringFunction,  exeUtil,  exeFun))  return  false ;
        System.out.println("-----------------------------------------------------------isTable3CheckOK........................................END") ;
        // 幣別
        if(!isCoinTypeCheckOK(exeUtil,  exeFun))  return  false ;
        // 附件
        /*
        if(!isTable12CheckOK(exeUtil,  exeFun))  return  false ;
        */
        // 廠商議價表格檢核
        if(!isTable18CheckOK(booleanUndergoWriteCheck,  exeUtil,  exeFun))  return  false ;
        //
        if(!"".equals(stringBarCode))  setValue("BarCode",  stringBarCode) ;
              return  true ;
        }
         // 公文編號  行銷及企劃 檢核
         public  String  getDocNoUserCheckErrTEST (Hashtable  hashtableData,  FargloryUtil  exeUtil) throws  Throwable{
                  String      stringUseDeptCd   =  (""+hashtableData.get("DEPT_CD_USER")).trim() ;
                  String      stringUseDeptCd3  =   exeUtil.doSubstring(stringUseDeptCd,  0,  3) ;
                  String      stringDepartNo    =  (""+hashtableData.get("DEPT_CD")).trim() ;
                  String      stringEmployeeNo  =  (""+hashtableData.get("EmployeeNo")).trim() ;
                  String      stringDepartNo4   =   exeUtil.doSubstring(stringDepartNo,  0,  4) ;
                  String      stringSpecBudget  =  ",017PR,033FG,033VIP,033CRM," ; //+get("SPEC_BUDGET")+"," ;
                  
                  Vector      vectorDeptCd      =  new  Vector() ;
                  boolean     booleanFlag       =  true ;
                  // 特殊部門 視為 企劃
                  vectorDeptCd.add("0338") ;
                  //
         System.out.println("stringUseDeptCd("+stringUseDeptCd+")---------------------------------") ;
                  //
                  if(stringUseDeptCd.indexOf("0333")!=-1  ||  vectorDeptCd.indexOf(stringUseDeptCd)!=-1) {
                        // 企劃
                        booleanFlag  =  (",0333,0533,1333,".indexOf(","+stringDepartNo4+",")       ==    -1)  &&                        // 判斷公文追蹤非企劃
                                        ((","+stringSpecBudget+",033FZ,033MP,").indexOf(","+stringDepartNo+",") == -1)  &&              // 例外部門，不受檢核限制
                                        (",B2834,".indexOf(","+stringEmployeeNo+",")  !=-1  &&  "033TC".equals(stringDepartNo)) ;       // 例外人員，不受檢核限制  B2834 張淑真
                        if(booleanFlag) {
                              return  "企劃 之 [部門代碼] 須為企劃類案別。\n(有問題請洽 [行銷管理室])" ;
                        } 
                  } else if(",033,133,".indexOf(stringUseDeptCd3)  !=  -1) {
                        // 行銷
                        booleanFlag  =  (",0333,0533,1333,".indexOf(","+stringDepartNo4+",")       !=    -1) ;                                     // 判斷公文追蹤是企劃
                        if(booleanFlag) {
                              return  "行銷 之 [部門代碼] 不可為企劃類案別 。\n(有問題請洽 [行銷管理室])" ;
                        }     
                  }
                  return  "" ;
         }
    // 請購資訊
    public  boolean  isTable1CheckOK(boolean  booleanUndergoWriteCheck,  Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        //put("Doc3M010_PurchaseMoney",  "null") ;
        talk                dbAsset                                         =  getTalk(""+get("put_Asset")) ;
        talk          dbAO                              =  getTalk(""+get("put_AO")) ;
        JTable              jtable1                                                       =  getTable("Table1") ;
        JTabbedPane   jtabbedPane1                                              =  getTabbedPane("Tab1") ;
        int                     intTablePanel                                           =  0 ;
        String                stringBarCode                                           =  getValue("BarCode").trim() ;
        String                stringApplyType                                       =  getValue("ApplyType").trim() ;
        String                stringRecordNo                                        =  "" ;
        String                stringClassName                                     =  "" ;
        String                stringClassNameDescript                         =  "" ;
        String                stringComNo                                            =  getValue("ComNo").trim() ;
        String                stringCostID                                              =  "" ;
        String                stringCostID1                                           =  "" ;
        String                stringCostID2                                           =  "" ;
        String                stringDescript                                            =  "" ;
        String                stringKey                                                 =  "" ;
        String                stringType                                                =  "" ;
        String                stringUnit                                                    =  "" ;
        String                stringBudgetNum                                       =  "" ;
        String                stringHistoryPrice                                      =  "" ;
        String                stringActualNum                                       =  "" ;
        String                stringApplyMoney                                    =  "" ;
        String                stringFlow                                                =  getFunctionName() ;
        String                stringFactoryNo                                       =  "" ;
        String                stringActualPrice                                     =  "" ;
        String                stringPurchaseMoney                               =  "" ;
        String                stringPurchaseMoneyCompute                =  "" ;
        String                stringUnderGoWrite                                =  getValue("UNDERGO_WRITE").trim( ) ;
        String                stringCDate                                               =  exeUtil.getDateConvertFullRoc(getValue("CDate").trim()) ;
        String            stringStopUseMessage                  =  "" ;
        String                stringDocNo1                                       =  getValue("DocNo1").trim( ) ;
        String              stringSpecBudget                      =  ","+get("SPEC_BUDGET")+"," ;
        double               doubleApplyMoneySum                            =  0 ;
        double               doublePurchaseMoney                            =  0 ;
        double               doublePurchaseMoneySum                     =  0 ;
        double               doubleTotalPurchaseMoneySum              =  0 ;
        double               doubleTemp                                             =  0 ;
        Vector               vectorTable2FactoryNo                              =  getFactoryNoTable2( ) ;    // 付款條件廠商
        Vector               vectorCostID                                             =  getCostIDTable3( ) ;
        Vector               vectorFactoryNoCostID                            =  new  Vector() ;
        Hashtable         hashtableFactoryNo                                  =  new  Hashtable( ) ;
        Hashtable          hashtableCond                          =  new  Hashtable() ;
        boolean             booleanApplyF                                           =  "F".equals(stringApplyType) ;    // 統購F
        boolean             booleanUser                                             =  stringFlow.indexOf("採購")  ==  -1   ;
        if(jtable1.getRowCount()  ==  0) {
            return  retTable1Message(" [請購資訊] 無資料。",  -1,  false,  exeUtil) ;
        }
        Vector     vectorMoneySignSame  = new  Vector() ;
        // 固資
        int               intAssetCount                       =  0 ;
        String          stringFILTER                        =  "" ;
        String          stringTemp                          =  "" ;
        String        stringRecordNoDoc3M017    =  "" ;
        boolean       booleanAssetDate                =  true ;//!"".equals(stringAssetDate)  &&  !"null".equals(stringAssetDate)  &&  stringToday.compareTo(stringAssetDate)>=0 ;
        //
        String          stringCDateAC                     =  exeUtil.getDateConvert(getValue("CDate")) ;
        String        stringFiletrDo                        =  "" ;
        String            stringFactoryNo17           =  "" ;
        String            stringPopCode               =  "" ;
        String      stringCostIDDetail          =  "" ;
        String      stringUseStatus           =  "" ;
        String      stringBigBudget           =  "" ;
        String      stringSSMediaID            =  "" ;
        String      stringSSMediaID1          =  "" ;
        String      stringSSMediaID1DB          =  "" ;
        String        stringControlType           =  ""+get("ONLY_CONTROL_AMT") ;
        String[][]    retDoc2M0401                    =  exeFun.getDoc2M0401("",  "U",  "") ;if(retDoc2M0401.length>0)  stringFiletrDo  =  retDoc2M0401[0][2].trim() ;    // Remark  固資加工
        Hashtable   hashtbleFunctionType        =  exeFun.getCostIDVDoc2M0201H(stringComNo,  "",  "",  stringCDateAC,  "")  ;
        Hashtable   hashtableFactoryNoAsset       =  new  Hashtable() ;
        Hashtable   hashtableDoc3M016           =  new  Hashtable() ;
        Hashtable   hashtableDoc3M017           =  new  Hashtable() ;
        Vector        vecrorCostIDTypeO             =  (Vector)  hashtbleFunctionType.get("O") ;    if(vecrorCostIDTypeO  ==  null)  vecrorCostIDTypeO  =  new  Vector() ; // 固資請款代碼
        Vector        vectorFactoryNo                 =  new  Vector( ) ;
        Vector        vecrorRecordNoToTable9    =  new  Vector( ) ;
        Vector        vectorAsAssetFilter         =  new  Vector( ) ;
        Vector        vectorDoc2M022              =  new  Vector( ) ;
        //
        String        stringTodayL                  =  exeUtil.getDateConvert(getValue("CDate")) ;
        String      stringNoPageDate          =  ""+get("NO_PAGE_DATE") ;     // 請購無紙化上線日期
        boolean     booleanNoPageDate         =  stringTodayL.compareTo(stringNoPageDate)>=0;

        for(int  intNo=0  ;  intNo<jtable1.getRowCount() ;  intNo++) {
            stringRecordNo                  =  (""+getValueAt("Table1",  intNo,  "RecordNo")).trim() ;
            stringCostID                        =  (""+getValueAt("Table1",  intNo,  "CostID")).trim() ;
            stringCostID1                     =  (""+getValueAt("Table1",  intNo,  "CostID1")).trim() ; 
            stringType                          =  (""+getValueAt("Table1",  intNo,  "TYPE")).trim() ;
            stringUnit                            =  (""+getValueAt("Table1",  intNo,  "Unit")).trim() ;
            stringBudgetNum                 =  (""+getValueAt("Table1",  intNo,  "BudgetNum")).trim() ;           // 預算數量
            stringHistoryPrice                =  (""+getValueAt("Table1",  intNo,  "HistoryPrice")).trim() ;            // 歷史單價
            stringApplyMoney              =  (""+getValueAt("Table1",  intNo,  "ApplyMoney")).trim() ;            // 預算金額
            stringActualNum                 =  (""+getValueAt("Table1",  intNo,  "ActualNum")).trim() ;           // 實際數量
            stringFactoryNo                   =  (""+getValueAt("Table1",  intNo,  "FactoryNo")).trim() ;
            stringClassName                 =  (""+getValueAt("Table1",  intNo,  "ClassName")).trim() ;
            stringClassNameDescript   =  (""+getValueAt("Table1",  intNo,  "ClassNameDescript")).trim() ;
            stringDescript                      =  (""+getValueAt("Table1",  intNo,  "Descript")).trim() ;              // 規格
            stringActualPrice                 =  (""+getValueAt("Table1",  intNo,  "ActualPrice")).trim() ;           // 發包單價
            stringPurchaseMoney         =  (""+getValueAt("Table1",  intNo,  "PurchaseMoney")).trim() ;         // 請購金額
            stringApplyMoney              =  (""+getValueAt("Table1",  intNo,  "ApplyMoney")).trim() ;                // 申請金額
            stringFILTER                      =  (""+getValueAt("Table1",  intNo,  "FILTER")).trim() ;
            stringRecordNoDoc3M017  =  (""+getValueAt("Table1",  intNo,  "RecordNoDoc3M017")).trim() ;
            stringPopCode                   =  (""+getValueAt("Table1",  intNo,  "PopCode")).trim() ;
            stringCostIDDetail                =  (""+getValueAt("Table1",  intNo,  "CostIDDetail")).trim() ;
            stringSSMediaID                 =  (""+getValueAt("Table1",  intNo,  "SSMediaID")).trim() ;
            // 分類工料代碼
            if(!"D".equals(stringApplyType)) {
                // 非固資
                setValueAt("Table1",  "",  intNo,  "FILTER") ;
                //                
                if(stringBarCode.startsWith("S")) {
                    if("".equals(stringCostIDDetail)) {
                        stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [分類工料代碼] 不可為空白。" ;
                        return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                    }
                    vectorDoc2M022  =  exeFun.getQueryDataHashtableDoc("Doc2M022",  new  Hashtable(),  " AND  CostIDDetail  =  '"+stringCostIDDetail+"' ",  new  Vector(),  exeUtil) ;
                    if(vectorDoc2M022.size()  <=  0 ) {
                        stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [分類工料代碼] 不存在資料庫中。" ;
                        return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                    }
                    stringUseStatus  =  exeUtil.getVectorFieldValue(vectorDoc2M022,  0,  "UseStatus") ;
                    if(!"Y".equals(stringUseStatus)) {
                        stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [分類工料代碼] 不允許使用。" ;
                        return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                    }
                    stringCostID    =  exeUtil.getVectorFieldValue(vectorDoc2M022,  0,  "CostID") ;   setValueAt("Table1",  stringCostID,   intNo,  "CostID") ;
                    stringCostID1   =  exeUtil.getVectorFieldValue(vectorDoc2M022,  0,  "CostID1") ;    setValueAt("Table1",  stringCostID1,  intNo,  "CostID1") ;
                    stringCostID2   =  exeUtil.getVectorFieldValue(vectorDoc2M022,  0,  "CostID2") ;    setValueAt("Table1",  stringCostID2,  intNo,  "CostID2") ;
                }
                //20180206 採購才檢核 POP 代碼
                if(!stringCostIDDetail.startsWith("78")) {          
                  if(getFunctionName().indexOf("採購")  != -1) {
                    if(booleanUndergoWriteCheck  &&  ",782,".indexOf(stringCostID+stringCostID1)!=-1  &&  "".equals(stringPopCode)) {
                        stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [POP 代碼] 不可為空白。" ;
                        return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                    }
                  }
                }
                // 對應 Table9 POP 材質 ?????
                // 非固資請款代碼檢核 2018-02-14 移除此檢核
                vecrorCostIDTypeO.add("703") ;
                vecrorCostIDTypeO.add("704") ;
                vecrorCostIDTypeO.add("395") ;
                vecrorCostIDTypeO.add("396") ;
                vecrorCostIDTypeO.add("392") ;
                vecrorCostIDTypeO.add("3110") ;  // 2014-06-17 
                vecrorCostIDTypeO.add("3211") ;  // 2014-06-17 
                /*if(booleanUndergoWriteCheck  &&  vecrorCostIDTypeO.indexOf(stringCostID+stringCostID1)  !=  -1) {
                    stringTemp  =  "" ;
                    for(int  intNoL=0  ;  intNoL<vecrorCostIDTypeO.size()  ;  intNoL++) {
                        if(!"".equals(stringTemp))  stringTemp  +=  "、" ;
                        stringTemp  +=  ""+vecrorCostIDTypeO.get(intNoL) ;
                    }
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 非 [固定資產] 的 請款代碼 不能輸入"+stringTemp+"。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                }*/
                // 
                if(stringSpecBudget.indexOf(stringDocNo1)  ==  -1) {
                    if(",033,053,133,".indexOf(exeUtil.doSubstring(stringDocNo1,  0,  3))  ==  -1) {
                        if("31,32,".indexOf(stringCostID)!=-1) {
                            stringTemp  =  "第 "  +  (intNo+1)  +  " 列 非行銷部室 不允許使用 [大-請款代碼](31、32)。\n(有問題請洽 [財務室])。" ;
                            return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                        }
                        if(exeUtil.doParseDouble(stringCostID)  >=  70 ) {
                            if(stringDocNo1.startsWith("015")  &&  "721,".indexOf(stringCostID+stringCostID1)!=-1) {
                              // 特例允許
                            } else {
                                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 非行銷部室 不允許使用 70 之後的請款代碼，不允許異動資料庫。\n(有問題請洽 [財務室])。" ;
                                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                            }
                        }
                    } 
                }
                // 請款代碼    CostID、CostID1
                //20180212 採購才檢核分類工料代碼
                if(getFunctionName().indexOf("採購")  != -1) {
                  if("".equals(stringCostID)) {
                      stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [大-請款代碼] 不可為空白。" ;
                      return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                  }
                  if("".equals(stringCostID1)) {
                      stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [中-請款代碼] 不可為空白。" ;
                      return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
      
                  }
                  if(exeFun.getDoc7M011(stringComNo,  "",  stringCostID,  stringCostID1).length==0) {
                      if(exeFun.getDoc2M0201(stringComNo,  stringCostID,  stringCostID1,  "A").length  ==  0) {
                          stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [大-請款代碼][中-請款代碼] 不存在 [預算費用對照表] 中。\n(有問題請洽 [行銷管理室])" ;
                          return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                      }
                  }
                  // Table 9 驗證
                  if("781".equals(stringCostID+stringCostID1)) {
                      //vecrorRecordNoToTable9.add(stringRecordNo) ;    // 
                  }
                }
            } else if("D".equals(stringApplyType)) {
                // 固資
                setValueAt("Table1",  "",  intNo,  "CostIDDetail") ;
                //
                //20180309 移除固定資產限制
                /*
                if("".equals(stringFILTER)) {             
                    if(booleanUndergoWriteCheck) {
                        stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [固資代碼] 不可為空白。" ;
                        return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                    }
                } else {
                    vectorAsAssetFilter  =  exeUtil.getQueryDataHashtable("AS_ASSET_FILTER",  new  Hashtable(),  " AND  FILTER  = '"+stringFILTER+"' ",  dbAsset) ;
                    if(booleanUndergoWriteCheck  &&  vectorAsAssetFilter.size()  ==  0) {
                        stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [固資代碼] 不存在資料庫中。" ;
                        return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                    }
                    // 固資之請款代碼 依第一個案別分攤 作 分類，至 Doc2M0201 取值(略)    二層變一層時，不允許修改請款代碼
                    // 固資計算
                    if(!stringFILTER.equals(stringFiletrDo)) {
                        // 固資-請款代碼計算
                        intAssetCount  =  exeUtil.doParseInteger(""+hashtableFactoryNoAsset.get(stringFactoryNo+"A"))  +  1 ;  
                        hashtableFactoryNoAsset.put(stringFactoryNo+"A",  ""+intAssetCount) ;
                        //
                    } else {
                        // 固資-加工-請款代碼計算
                        intAssetCount  =  exeUtil.doParseInteger(""+hashtableFactoryNoAsset.get(stringFactoryNo+"B"))  +  1 ;  
                      hashtableFactoryNoAsset.put(stringFactoryNo+"B",  ""+intAssetCount) ;
                      }
                }
                */
            }
            // 通路代碼 檢核
            if(!"".equals(stringSSMediaID)) {
                if(!isERPKeyExist(stringSSMediaID,  exeUtil,  dbAO)) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [通路代碼] 不存在資料庫中。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
                }
                // 特殊請款代碼一定不可有活動代碼
                // 活動代碼 與  請款代碼 對應檢核
                stringSSMediaID1      =  exeUtil.doSubstring(stringSSMediaID,  stringSSMediaID.length()-13,  stringSSMediaID.length()-12) ;
                stringSSMediaID1DB    =  getSSMediaIDDoc(stringCostID,  stringCostID1,  exeUtil,  exeFun) ;
                if(!"".equals(stringSSMediaID1DB)  &&  !stringSSMediaID1.equals(stringSSMediaID1DB)) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [請款代碼]"+stringSSMediaID1DB+" 對應 [通路代碼] 關係錯誤。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
                }
                // 須為企劃類請款代碼
                stringBigBudget         =  getBigBudget(stringCostID,  stringCostID1,  exeUtil,  exeFun) ;
                if("A".equals(stringBigBudget)) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [通路代碼] 只能使用企劃類的 請款代碼。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
                }
            } else {
                //20180206 採購才檢核 POP 代碼
                if(!stringCostIDDetail.startsWith("78"))  {     
                  if(getFunctionName().indexOf("採購")  != -1) {
                      //20180323 將POP的請款代碼和活動代碼有關判斷暫時先拿掉
                      /*
                      if(booleanUndergoWriteCheck  &&  stringDocNo1.indexOf("333")==-1  &&  "782".equals(stringCostID+stringCostID1)) {
                          stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 POP 請款代碼 活動代碼 不可為空白。" ;
                          return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
                      }
                      */
                  }
                }
            }
            //
            if(vectorFactoryNo.indexOf(stringFactoryNo)  ==  -1)  vectorFactoryNo.add(stringFactoryNo) ;
            //
            stringKey                         =  stringCostID  +  "-"  +  stringCostID1 ;
            doubleApplyMoneySum  +=  exeUtil.doParseDouble(stringApplyMoney) ;
            //
            if(intNo==0) {
                if("781".equals(stringCostID+stringCostID1)) {
                    setValue("ClassNameList",  stringClassNameDescript) ;
                } else {
                    if("".equals(stringClassName)) {
                        setValue("ClassNameList",  stringClassNameDescript) ; 
                    } else {
                        setValue("ClassNameList",  stringClassName) ;
                    }
                }
            }
            //  　統購                   採購
            /*if((booleanApplyF ||  !booleanUser)  &&  vectorFactoryNoCostID.indexOf(stringCostIDDetail+"-"+stringFactoryNo)  !=  -1) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [統一編號][分類工料代碼] 重複。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
            }*/
            // 請購數量   BudgetNum
            if(exeUtil.doParseDouble(stringBudgetNum)  <=  0) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [請購數量] 不可小於等於 0。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
            }
            if((","+stringControlType+",").indexOf(","+stringUnit+",")!=-1  &&  exeUtil.doParseDouble(stringBudgetNum)!=1) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [單位] 為 ["+stringControlType+"] 時，[請購數量] 只能為 1。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
            }
            if(!check.isFloat(stringBudgetNum,  "11,4")) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [請購數量] 格式錯誤，只允許7 位數及小數點後 4 位。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;
            }
            // 請購金額
            if(exeUtil.doParseDouble(stringApplyMoney)<0  &&  ",701,702,".indexOf(","+stringCostID+stringCostID1+",")==-1) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [請購金額] 不可小於 0。\n[僅 請款代碼 701接待中心裝璜設計費用(含精神堡壘)、702樣品屋裝璜設計費用 允許申請負值。]" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
            }
            // 
            if(!"".equals(stringApplyMoney)  &&  exeUtil.doParseDouble(stringApplyMoney)==0) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [請購金額] 不允許等於 0。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
            }
            // 預算單價
            if(exeUtil.doParseDouble(stringHistoryPrice)  ==  0) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [預算單價] 不允許等於 0。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
            }
            if(booleanUser) {
                // 非採購
                // 實際數量   ActualNum
                setValueAt("Table1",  stringBudgetNum,  intNo,  "ActualNum") ;
                stringActualNum  =  stringBudgetNum ;
            } else {
                // 採購
                // 實際數量
                if(exeUtil.doParseDouble(stringActualNum)  <  0) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [實際數量] 不可小於 0。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
                }
                if((","+stringControlType+",").indexOf(","+stringUnit+",")!=-1  &&  exeUtil.doParseDouble(stringActualNum)!=1) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [單位] 為 ["+stringControlType+"] 時，[實際數量] 只能為 1。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
                }
                if(!check.isFloat(stringActualNum,  "11,4")) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [請購數量] 格式錯誤，只允許 7 位數及小數點後 4 位。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
                }
                // 採發金額
                if(exeUtil.doParseDouble(stringPurchaseMoney)<0  &&  ",701,702,".indexOf(","+stringCostID+stringCostID1+",")==-1) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [採發金額] 不可小於 0。\n[僅 請款代碼 701接待中心裝璜設計費用(含精神堡壘)、702樣品屋裝璜設計費用 允許申請負值。]" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
                }
                // 採發金額、採發計算金額處理
                if(exeUtil.doParseDouble(stringPurchaseMoney) !=  0  &&  exeUtil.doParseDouble(stringActualPrice)  !=  0) {
                    doubleTemp                               =  exeUtil.doParseDouble(stringActualPrice)  *  exeUtil.doParseDouble(stringActualNum) ;
                    stringPurchaseMoneyCompute  =  convert.FourToFive(""+doubleTemp, 0) ;
                    setValueAt("Table1",  stringPurchaseMoneyCompute,  intNo,  "PurchaseMoneyCompute") ;
                } else if(exeUtil.doParseDouble(stringPurchaseMoney) !=  0) {
                    // 請購金額
                    doubleTemp         =  exeUtil.doParseDouble(stringPurchaseMoney)  /  exeUtil.doParseDouble(stringActualNum) ;
                    stringActualPrice  =  convert.FourToFive(""+doubleTemp, 2) ;
                    setValueAt("Table1",  stringActualPrice,  intNo,  "ActualPrice") ;
                    //
                    doubleTemp                               =  exeUtil.doParseDouble(stringActualPrice)  *  exeUtil.doParseDouble(stringActualNum) ;
                    stringPurchaseMoneyCompute  =  convert.FourToFive(""+doubleTemp, 0) ;
                     setValueAt("Table1",  stringPurchaseMoneyCompute,  intNo,  "PurchaseMoneyCompute") ;
                } else {
                    //  發包單價
                    doubleTemp                               =  exeUtil.doParseDouble(stringActualPrice)  *  exeUtil.doParseDouble(stringActualNum) ;
                    stringPurchaseMoney                =  convert.FourToFive(""+doubleTemp, 0) ;
                    stringPurchaseMoneyCompute  =  stringPurchaseMoney ;
                    setValueAt("Table1",  stringPurchaseMoney,  intNo,  "PurchaseMoney") ;
                    setValueAt("Table1",  stringPurchaseMoney,  intNo,  "PurchaseMoneyCompute") ;
                }
                if(exeUtil.doParseDouble(stringPurchaseMoney)  !=  exeUtil.doParseDouble(stringPurchaseMoneyCompute)) {
                    int  ans  =  JOptionPane.showConfirmDialog(null,  
                                                    "第 "  +  (intNo+1)  +  " 列 之 [計算金額] 不等於 [請購金額]，如要繼續請按 [是]。",
                                                    "請選擇?",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.WARNING_MESSAGE) ;
                    if(ans  ==  JOptionPane.NO_OPTION) {
                        return  false ;
                    }
                }
                //
                doublePurchaseMoney                  =  exeUtil.doParseDouble(stringPurchaseMoney) ;
                doubleTotalPurchaseMoneySum  +=  doublePurchaseMoney ;
                // 預算單價
                if(exeUtil.doParseDouble(stringHistoryPrice)  ==  0) setValueAt("Table1",  stringActualPrice,  intNo,  "HistoryPrice") ;
                if(exeUtil.doParseDouble(stringApplyMoney)  ==  0) {
                    setValueAt("Table1",  stringActualNum,                 intNo,  "BudgetNum") ;
                    setValueAt("Table1",  stringActualPrice,                 intNo,  "HistoryPrice") ;
                    setValueAt("Table1",  ""+doublePurchaseMoney,   intNo,  "ApplyMoney") ;
                }
            }
            // 採發金額
            if(exeUtil.doParseDouble(stringPurchaseMoney) >0) {
                if(vectorMoneySignSame.indexOf("正數")==-1)  vectorMoneySignSame.add("正數") ;
            } else if(exeUtil.doParseDouble(stringPurchaseMoney) <0) {
                if(vectorMoneySignSame.indexOf("負數")==-1)  vectorMoneySignSame.add("負數") ;
            } 
            // 預算金額
            if(exeUtil.doParseDouble(stringApplyMoney) >0) {
                if(vectorMoneySignSame.indexOf("正數")==-1)  vectorMoneySignSame.add("正數") ;
            } else if(exeUtil.doParseDouble(stringApplyMoney) <0) {
                if(vectorMoneySignSame.indexOf("負數")==-1)  vectorMoneySignSame.add("負數") ;
                put("Doc3M011_Negative",  "T") ;
            } 
            if(vectorMoneySignSame.size() > 1) {
                stringTemp  =  "金額發生錯誤。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
            }
            // FactoryNo 在 [付款資訊] 須有相對應的項目
            if(booleanUndergoWriteCheck  &&  vectorTable2FactoryNo.indexOf(stringFactoryNo)  ==  -1  &&  (vectorTable2FactoryNo.size()  >  0  ||  "C".equals(stringUnderGoWrite)  ||  "Y".equals(stringUnderGoWrite))) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 在 [請購資訊] 無相對應的廠商付款資訊。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
            }
            // 請款代碼在 [案別分攤] 須有相對應的項目
            if(booleanUndergoWriteCheck  &&  vectorCostID.size() > 0  &&  vectorCostID.indexOf(stringKey)  ==  -1) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [案別分離] 無相對應的分攤資料。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
            }
            //                                            非統購
            /*if(!booleanNoPageDate  &&  !booleanApplyF  &&  "".equals(stringClassName)) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [規格] 不可為空白。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
            }*/
            if("".equals(stringClassNameDescript)) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [內容] 不可為空白。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
            }
            // 單位       Unit
            if("".equals(stringUnit)) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [單位] 不可為空白。" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
            }
            if((!booleanUser  ||  booleanApplyF)  &&  booleanUndergoWriteCheck) { 
                if("".equals(stringFactoryNo)) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [廠商統一編號] 不可為空白。" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;    
                }
                if(exeFun.getDoc3M015(stringFactoryNo).length  ==  0) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [廠商統一編號] 不存在 [請購廠商維護作業(Doc3M015)]。\n(有問題請洽 [採購室])" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
                }
                // 停權
                hashtableCond.put("OBJECT_CD",        stringFactoryNo) ;
                hashtableCond.put("CHECK_DATE",     stringCDate) ;
                hashtableCond.put("SOURCE",           "A") ;
                hashtableCond.put("FieldName",        "第 "  +  (intNo+1)  +  " 列 之 [廠商統一編號]") ;
                stringStopUseMessage  =  exeFun.getStopUseObjectCDMessage (hashtableCond,  exeUtil) ;
                if(!"TRUE".equals(stringStopUseMessage)) {
                    return  retTable1Message(stringStopUseMessage,  intNo,  false,  exeUtil) ;  
                }
            }
            if(booleanUndergoWriteCheck  &&  booleanApplyF) {
                // 統購
                if("805".equals(stringCostID+stringCostID1)) {
                    // 贈品 Doc3M016、Doc3M017
                    if(!isApplyF805OK(intNo,  exeUtil,  exeFun))  return  false ;
                } else {
                    // 非贈品 Doc3M0174
                    if(!isApplyFNot805OK(intNo,  exeUtil,  exeFun))  return  false ;
                }
            }
        }
        if(doubleApplyMoneySum  ==  0) {
            JOptionPane.showMessageDialog(null,  "[請購金額] 合計 不可等於 0。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            return  false ; 
        }
        //20180309 不檢查固定資產 By B03812
        /*
        if(booleanUndergoWriteCheck  &&  booleanAssetDate  &&  stringFlow.indexOf("採購")!=-1  &&  "D".equals(stringApplyType) ){
            for(int  intNo=0  ;  intNo<vectorFactoryNo.size()  ;  intNo++)             {
                stringFactoryNo  =  ""+vectorFactoryNo.get(intNo) ;
                intAssetCount     =  exeUtil.doParseInteger(""+hashtableFactoryNoAsset.get(stringFactoryNo+"A")) ;  
                if(intAssetCount  ==  0) {
                    stringTemp  =  "廠商("+stringFactoryNo+") 固定資產請購單之固資項目 至少 一列。" ;
                    return  retTable1Message(stringTemp,  -1,  false,  exeUtil) ; 
                } 
                intAssetCount     =  exeUtil.doParseInteger(""+hashtableFactoryNoAsset.get(stringFactoryNo+"B")) ;  
                if(intAssetCount  >  1) {
                    stringTemp  =  "廠商("+stringFactoryNo+") 固定資產請購單之非固資項目 僅 能一列。" ;
                    return  retTable1Message(stringTemp,  -1,  false,  exeUtil) ; 
                } 
            } 
        }
        */
        if(booleanUndergoWriteCheck  &&  !isTable1SameTable9CheckOK(vecrorRecordNoToTable9,  exeFun,  exeUtil)) {
              jtabbedPane1.setSelectedIndex(intTablePanel) ;
              return  false ; 
        }
        return  true ;
    }
    public  boolean  isApplyF805OK(int  intNo,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String      stringCDate           =  exeUtil.getDateConvert(getValue("CDate").trim()) ;
        String      stringDateStart         =  stringCDate ; //(""+getValueAt("Table1",  intNo,  "DateStart")).trim() ;
        String      stringDateEnd           =  stringCDate ; //(""+getValueAt("Table1",  intNo,  "DateEnd")).trim() ;
        String      stringCostID              =  (""+getValueAt("Table1",  intNo,  "CostID")).trim() ;
        String      stringCostID1           =  (""+getValueAt("Table1",  intNo,  "CostID1")).trim() ;
        String      stringRecordNoDoc3M017  =  (""+getValueAt("Table1",  intNo,  "RecordNoDoc3M017")).trim() ;
        String      stringFactoryNo           =  (""+getValueAt("Table1",  intNo,  "FactoryNo")).trim() ;
        String      stringTemp            =  "" ;
        String      stringFactoryNo17       =  "" ;
        String      stringFunctionName      =  getFunctionName() ;
        Hashtable   hashtableDoc3M016       =  null ;
        Hashtable   hashtableDoc3M017       =  null ;
        //
        hashtableDoc3M016  =  getDoc3M016(stringCostID,  stringCostID1,  stringDateStart,  stringDateEnd,  exeUtil,  exeFun) ;
        if(hashtableDoc3M016  ==  null) {
            stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 不存在 [2.統購廠商費用對照表-贈品(Doc3M0162)]。\n(有問題請洽 [採購室])" ;
            return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
        }
        if(exeUtil.doParseDouble(stringRecordNoDoc3M017)  >  0) {
            hashtableDoc3M017  =  getDoc3M017(stringRecordNoDoc3M017,  hashtableDoc3M016,  exeUtil,  exeFun) ;
            if(hashtableDoc3M017  ==  null) {
                stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 不存在 [2.統購廠商費用對照表-贈品(Doc3M0162)]。\n(有問題請洽 [採購室])" ;
                return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
            }
            if(!isControlNumOK(stringRecordNoDoc3M017,  hashtableDoc3M017,  exeUtil,  exeFun))  return  false ;
        }
        // 廠商檢核
        stringFactoryNo17  =  ""+hashtableDoc3M017.get("FactoryNo") ;  if("null".equals(stringFactoryNo17))  stringFactoryNo17  =  "" ;
        if(!"".equals(stringFactoryNo17)) {
            if(!stringFactoryNo.equals(stringFactoryNo17)) {
                if(stringFunctionName.indexOf("採購")  ==  -1) {
                    setValueAt("Table1",  stringFactoryNo17,  intNo,  "FactoryNo") ;
                }
            }
        } else {
            stringDateStart   =  ""+hashtableDoc3M017.get("DateStart") ;
            stringDateEnd     =  ""+hashtableDoc3M017.get("DateEnd") ;
            if(exeFun.getDoc3M0171(stringCostID,      stringCostID1,  "",  stringFactoryNo,  stringDateStart,  stringDateEnd,  "").length  ==  0) {
                if(stringFunctionName.indexOf("採購")  ==  -1) {
                    stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 [廠商統一編號] 不存在 [2.統購廠商費用對照表-贈品(Doc3M0162)] 中。\n(有問題請洽 [採購室])" ;
                    return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
                }
            }
        }
        return  true ;
    }
    public  boolean  isControlNumOK(String  stringRecordNoDoc3M017,  Hashtable  hashtableDoc3M017,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String  stringControlNum          =  ""+hashtableDoc3M017.get("ControlNum") ;
        double  doubleControlNum      =  exeUtil.doParseDouble(stringControlNum) ;      
        double  doubleUseNum          =  getNumDoc3M012(stringRecordNoDoc3M017,  hashtableDoc3M017,  exeUtil,  exeFun) ;
        //
        if(doubleControlNum  <=  0)  return  true ;
        //
        if(doubleUseNum  >  doubleControlNum) {
            messagebox("統購管控數量 為 "+exeUtil.getFormatNum2(""+doubleControlNum)+"，現已申請(含本項) "+exeUtil.getFormatNum2(""+doubleUseNum)+"。 ") ;
            return  false ;
        }
        return  true ;
    }
    public  double  getNumDoc3M012(String  stringRecordNoDoc3M017,  Hashtable  hashtableDoc3M017,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String              stringCostID              =  ""+hashtableDoc3M017.get("CostID") ;
        String              stringCostID1                 =  ""+hashtableDoc3M017.get("CostID1") ;
        String              stringCostID2                 =  ""+hashtableDoc3M017.get("CostID2") ;  
        String              stringDateStart             =  ""+hashtableDoc3M017.get("DateStart") ;
        String              stringDateEnd                   =  ""+hashtableDoc3M017.get("DateEnd") ;
        String        stringBarCode             =  getValue("BarCode").trim() ;
        String          stringSql                     =  "" ;
        String          stringUseNum                      =  "" ;
        String[][]      retDoc3M012               =  null ;
        double        doubleUseNum            = 0 ;
        //
         stringSql          =  " SELECT  M12.ActualNum " +
                                        " FROM  Doc3M011 M11,  Doc3M012 M12 "  +
                   " WHERE  M11.BarCode  =  M12.BarCode " +
                         " AND  M11.UNDERGO_WRITE  <>  'X' "  +
                        " AND  M11.BarCode  <>  '"      +stringBarCode          +"' "  +
                      " AND  M11.CDate  BETWEEN  '"   +stringDateStart        +"'  AND  '"+stringDateEnd+"' " +
                      " AND  M12.CostID  =  '"                  +stringCostID                       +"' " +
                      " AND  M12.CostID1  =  '"                +stringCostID1                     +"' " +
                      " AND  M12.RecordNoDoc3M017  =  "+stringRecordNoDoc3M017  +" " ;
         retDoc3M012  =  exeFun.getTableDataDoc(stringSql) ;
         for(int  intNo=0  ;  intNo<retDoc3M012.length  ;  intNo++) {
            stringUseNum =  retDoc3M012[intNo][0].trim() ;
            //
            doubleUseNum  +=  exeUtil.doParseDouble(stringUseNum)  ;
         }
         talk       dbDocCS       =  exeUtil.getTalkCS("Doc") ;
         if(dbDocCS  !=  null) {
             retDoc3M012  =  dbDocCS.queryFromPool(stringSql) ;
             for(int  intNo=0  ;  intNo<retDoc3M012.length  ;  intNo++) {
                stringUseNum =  retDoc3M012[intNo][0].trim() ;
                //
                doubleUseNum  +=  exeUtil.doParseDouble(stringUseNum)  ;
             }
         }
         doubleUseNum  +=  exeUtil.doParseDouble(getValue("ActualNum")) ;
         return  doubleUseNum ;
    }
    public  boolean  isApplyFNot805OK(int  intNo,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String      stringDateStart         =  (""+getValueAt("Table1",  intNo,  "DateStart")).trim() ;
        String      stringDateEnd           =  (""+getValueAt("Table1",  intNo,  "DateEnd")).trim() ;
        String      stringCostIDDetail        =  (""+getValueAt("Table1",  intNo,  "CostIDDetail")).trim() ;
        String      strinDocNo173           =  (""+getValueAt("Table1",  intNo,  "DocNo173")).trim() ;
        String      stringRecordNoDoc3M017  =  (""+getValueAt("Table1",  intNo,  "RecordNoDoc3M017")).trim() ;
        String      stringFactoryNo           =  (""+getValueAt("Table1",  intNo,  "FactoryNo")).trim() ;
        String      stringUnit                =  (""+getValueAt("Table1",  intNo,  "Unit")).trim() ;
        String      stringClassName       =  (""+getValueAt("Table1",  intNo,  "ClassName")).trim() ;
        String      stringCDate           =  exeUtil.getDateConvert(getValue("CDate").trim()) ;
        String      stringCDateUse          =  exeUtil.getDateConvert(getValue("CDateUse").trim()) ;
        String      stringTemp            =  "" ;
        String      stringFactoryNo17       =  "" ;
        String      stringSqlAnd          =  "" ;
        String      stringFunctionName      =  getFunctionName() ;
        Hashtable   hashtableDoc3M016       =  null ;
        Hashtable   hashtableDoc3M017       =  null ;
        Hashtable   hashtableAnd              =  new  Hashtable() ;
        Vector    vectorDoc3M0174       =  new  Vector() ;
        //
        if(stringFunctionName.indexOf("採購")  !=  -1)   return  true ;
        // 
        if(!stringCostIDDetail.startsWith("74")) {
            if("".equals(stringCDateUse))  stringCDateUse  =  stringCDate ;
            //
            stringDateStart   =  stringCDateUse ;
            stringDateEnd     = stringCDateUse ;
        }
        // 
        hashtableAnd.put("DocNo",     strinDocNo173) ;
        hashtableAnd.put("CostIDDetail",  stringCostIDDetail) ;
        hashtableAnd.put("RecordNo",    stringRecordNoDoc3M017) ;
        hashtableAnd.put("FactoryNo",   stringFactoryNo) ;
        //hashtableAnd.put("ItemName",  stringClassName) ;    // 規格
        //hashtableAnd.put("Unit",        stringUnit) ;       // 單位
        stringSqlAnd    =  " AND  ( DateStart = '9999/99/99'  OR  DateStart <= '"+stringDateStart+"' )" +
                         " AND  ( DateEnd = '9999/99/99'  OR  DateEnd >= '"+stringDateEnd+"' )" ;
        vectorDoc3M0174  =  exeFun.getQueryDataHashtableDoc("Doc3M0174",  hashtableAnd,   stringSqlAnd,  new  Vector(),  exeUtil) ;
        if(vectorDoc3M0174.size()  ==  0) {
            stringTemp  =  "第 "  +  (intNo+1)  +  " 列 之 不存在 [3.統購廠商費用對照表-行銷(Doc3M0163)]。\n(有問題請洽 [採購室])" ;
            return  retTable1Message(stringTemp,  intNo,  false,  exeUtil) ;  
        }
        // 廠商檢核
        stringFactoryNo17  =  exeUtil.getVectorFieldValue(vectorDoc3M0174,  0,  "FactoryNo") ;
        if(!"".equals(stringFactoryNo17)) {
            if(!stringFactoryNo.equals(stringFactoryNo17)) {
                setValueAt("Table1",  stringFactoryNo17,  intNo,  "FactoryNo") ;
            }
        }
        return  true ;
    }
    public  boolean  retTable1Message(String  stringMessage,  int  intRowPosition,  boolean  booleanRet,  FargloryUtil  exeUtil) throws  Throwable {
        JTabbedPane     jtabbedPane1                                          =  getTabbedPane("Tab1") ;
        JTable               jtable1                                                      =  getTable("Table1") ;
        int                     intTablePanel                                           =  0 ;
        //
        if(intRowPosition  !=  -1)jtable1.setRowSelectionInterval(intRowPosition,  intRowPosition) ;
        jtabbedPane1.setSelectedIndex(intTablePanel) ;
        //
        if(!"".equals(stringMessage))messagebox(stringMessage) ;
        return  booleanRet ;
    }
    public  String  getBigBudget(String  stringCostID,  String  stringCostID1,  FargloryUtil  exeUtil,  Doc2M010  exeFun)throws  Throwable {
        String      stringBigBudget     =  "" ;
        String      stringBudgetID        =  "" ;
        String      stringComNo       =  getValue("ComNo") ;
        Hashtable  hashtableAnd         =  new  Hashtable() ;
        //
        hashtableAnd.put("ComNo",       stringComNo) ;
        hashtableAnd.put("CostID",      stringCostID) ;
        hashtableAnd.put("CostID1",     stringCostID1) ;
        stringBudgetID  =  exeFun.getNameUnionDoc("BudgetID",  "Doc2M020",  "",  hashtableAnd,  exeUtil) ;
        if(!stringBudgetID.startsWith("B"))  return "A" ;
        //
        hashtableAnd.put("BudgetID",     stringBudgetID) ;
        stringBigBudget  =  exeFun.getNameUnionDoc("BigBudget",  "Doc7M072",  "",  hashtableAnd,  exeUtil) ;
        //
        return  stringBigBudget ;
    }
    public  boolean  isERPKeyExist(String  stringERPKey,  FargloryUtil  exeUtil,  talk  dbAO)throws  Throwable {
        Vector      vectorViewAOSeminar     =  null ;
        Hashtable   hashtableAnd          =  new  Hashtable() ;
        //
        hashtableAnd.put("ERP_Key",  stringERPKey) ;
        hashtableAnd.put("set_flag",    "預算通過") ;
        vectorViewAOSeminar  =  exeUtil.getQueryDataHashtable("View_AO_Seminar",  hashtableAnd,  "",  dbAO)  ;
        //
        if(vectorViewAOSeminar.size()  ==  0)  {
            return  false ;
        }
        return  true ;
    }
    public  String  getSSMediaIDDoc(String  stringCostID,  String  stringCostID1,  FargloryUtil  exeUtil,  Doc2M010  exeFun)throws  Throwable {
        String      stringSSMediaID1   =  "" ;
        Hashtable  hashtableAnd          =  new  Hashtable() ;
        //
        hashtableAnd.put("CostID",      stringCostID) ;
        hashtableAnd.put("CostID1",       stringCostID1) ;
        hashtableAnd.put("UseType",     "A") ;
        stringSSMediaID1  =  exeFun.getNameUnionDoc("SSMediaID",  "Doc7M070",  "",  hashtableAnd,  exeUtil) ;
        if("".equals(stringSSMediaID1))  return "" ;
        //
        stringSSMediaID1  =  exeUtil.doSubstring(stringSSMediaID1,  0,  1) ;
        return  stringSSMediaID1 ;
    }
    public  Hashtable  getDoc3M016(String  stringCostID,  String  stringCostID1,  String  stringDateStart,  String  stringDateEnd,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
           String         stringSqlAnd          =  "" ;
           Hashtable        hashtableAnd          =  new  Hashtable() ;
           Vector         vectorDoc3M016      =  null ;
           //
         stringSqlAnd  = " AND  ( (DateStart  >=  '"   +  stringDateStart   +  "'  AND  DateStart  <=  '" +  stringDateEnd  +  "')  OR "  +
                        " (DateEnd   >=  '"   +  stringDateStart   +  "'  AND  DateEnd   <=  '"  +  stringDateEnd  +  "')  OR "  +
                        " (DateStart  <=  '"   +  stringDateStart   +  "'  AND  DateEnd   >=  '"  +  stringDateEnd  +  "')  OR "  +
                        " (DateStart  >=  '"   +  stringDateStart   +  "'  AND  DateEnd   <=  '"  +  stringDateEnd  +  "')) " ;
          if(!"".equals(stringCostID))      hashtableAnd.put("CostID",      stringCostID) ;
          if(!"".equals(stringCostID1))       hashtableAnd.put("CostID1",       stringCostID1) ;
           vectorDoc3M016  =  exeFun.getQueryDataHashtableDoc("Doc3M016",  hashtableAnd,  stringSqlAnd,  new  Vector(),  exeUtil) ;
           //
           if(vectorDoc3M016.size()  <=  0)  return  null ;
           if(vectorDoc3M016.size()  >    1)  return  null ;
           return (Hashtable) vectorDoc3M016.get(0) ;
    }
    public  Hashtable  getDoc3M017(String  stringRecordNo,  Hashtable  hashtableDoc3M016,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        String                stringCostID                  =  ""+hashtableDoc3M016.get("CostID") ;
        String                stringCostID1                 =  ""+hashtableDoc3M016.get("CostID1") ;
        String                stringDateStart         =  ""+hashtableDoc3M016.get("DateStart") ;
        String                stringDateEnd           =  ""+hashtableDoc3M016.get("DateEnd") ;
        String            stringSql                 =  "" ;
        Vector          vectorDoc3M017        =  null ;
        Hashtable       hashtableAnd             =  new  Hashtable() ;
        // 請購
        hashtableAnd.put("CostID",      stringCostID) ;
        hashtableAnd.put("CostID1",       stringCostID1) ;
        hashtableAnd.put("DateStart",     stringDateStart) ;
        hashtableAnd.put("DateEnd",       stringDateEnd) ;
        hashtableAnd.put("RecordNo",    stringRecordNo) ;
        vectorDoc3M017  =  exeFun.getQueryDataHashtableDoc("Doc3M017",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
        if(vectorDoc3M017.size()  !=  1)  return  null ;
        return (Hashtable) vectorDoc3M017.get(0) ;
    }
    public  boolean  isTable1SameTable9CheckOK(Vector  vecrorRecordNoToTable9,  Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        if(vecrorRecordNoToTable9.size()  ==  0)  {
            setTableData("Table9",  new  String[0][0]) ;
            return  true ;
        }
        //
        JTable               jtable9                            =  getTable("Table9") ;
        String        stringRecordNo        =  "" ;
        String        stringRecordNoL         =  "" ;
        String[][]    retTable9Data           =  getTableData("Table9") ;
        Vector        vectorTable9Data        =  new  Vector() ;
        boolean       booleanFlag           =  true ;
        for(int  intNo=0  ;  intNo<vecrorRecordNoToTable9.size()  ;  intNo++) {
            stringRecordNo  =  (""+vecrorRecordNoToTable9.get(intNo)).trim() ;
            booleanFlag       =  false ;
            for(int  intNoL=0  ;  intNoL<jtable9.getRowCount()  ;  intNoL++) {
                stringRecordNoL  =  (""+getValueAt("Table9",  intNoL,  "RecordNo")).trim() ;
                if(stringRecordNo.equals(stringRecordNoL)) {
                    booleanFlag  =  true ;
                    break ;
                }
            }
            if(!booleanFlag) {
                messagebox("[請購資訊表格] 第"+stringRecordNo+"列無對應的 POP 材質資訊。") ;
                return  false ;
            }
        }
        for(int  intNo=0  ;  intNo<jtable9.getRowCount()  ;  intNo++) {
            stringRecordNo  =  (""+getValueAt("Table9",  intNo,  "RecordNo")).trim() ;
            if(vecrorRecordNoToTable9.indexOf(stringRecordNo)  !=  -1) {
                // 存在
                vectorTable9Data.add(retTable9Data[intNo]) ;
            }
        }
        setTableData("Table9",  (String[][])  vectorTable9Data.toArray(new  String[0][0])) ;
        return true ;
    }
    
    // 付款資訊
    public  boolean  isTable2CheckOK(boolean  booleanUndergoWriteCheck,  Doc.Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        JTabbedPane     jtabbedPane1                      =  getTabbedPane("Tab1") ;
        JTable               jtable2                                  =  getTable("Table2") ;
        int                     intTablePanel                       =  1 ;
        String                stringFactoryNo                     =  "" ;
        String                stringRecordNo                    =  "" ;
        String                stringRecordNoOld               =  "" ;
        String                stringPayCondition1             =  "" ;
        String                stringPayCondition2             =  "" ;
        String                stringPercentRate                =  "" ;
        String                stringMonthNum                  =  "" ;
        String                stringPurchaseMoney           =  "" ;
        String                stringGroupID                      =  "" ;
        double                doublePercentRate                =  0 ;
        double                doublePercentRateSum         =  0 ;
        double                doublePurchaseMoney           =  0 ;
        double                doublePurchaseMoneySum    =  0 ;
        double                doublePurchaseMoneySum2  =  0 ;
        Vector               vectorFactoryNo                    =  new Vector( ) ;
        Hashtable         hashtablePercentRate           =  new  Hashtable( ) ;
        Hashtable           hashtablePurchaseMoney      =  new  Hashtable( ) ;
        if(jtable2.getRowCount()  ==  0) {
            if(!booleanUndergoWriteCheck  ||  isTable1PurchaseMoney0(exeUtil)) {
                return  true ;
            }
            JOptionPane.showMessageDialog(null,  " [付款資訊] 無資料。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            jtabbedPane1.setSelectedIndex(intTablePanel) ;
            return  false ;
        }
        for(int  intNo=0 ;  intNo<jtable2.getRowCount( )  ;  intNo++) {
            stringFactoryNo            =  (""+getValueAt("Table2",  intNo,  "FactoryNo")).trim() ;
            stringPayCondition1     =  (""+getValueAt("Table2",  intNo,  "PayCondition1")).trim() ;
            stringPayCondition2     =  (""+getValueAt("Table2",  intNo,  "PayCondition2")).trim() ;
            stringPercentRate        =  (""+getValueAt("Table2",  intNo,  "PercentRate")).trim() ;
            stringPurchaseMoney  =  (""+getValueAt("Table2",  intNo,  "PurchaseMoney")).trim() ;
            stringMonthNum           =  (""+getValueAt("Table2",  intNo,  "MonthNum")).trim() ;
            stringGroupID               =  (""+getValueAt("Table2",  intNo,  "GroupID")).trim() ;
            // FactoryNo
            if(vectorFactoryNo.indexOf(stringFactoryNo+"-"+stringGroupID)  ==  -1)  vectorFactoryNo.add(stringFactoryNo+"-"+stringGroupID) ;
            // 比例           PercentRate
            if("".equals(stringPercentRate)) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [比例] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table2",  intNo,  "PercentRate") ;
                return  false ; 
            }
            doublePercentRate          =  exeUtil.doParseDouble(stringPercentRate) ;
            doublePercentRateSum   =  exeUtil.doParseDouble(""+hashtablePercentRate.get(stringFactoryNo+"-"+stringGroupID)) ;
            doublePercentRateSum +=  doublePercentRate ;
            hashtablePercentRate.put(stringFactoryNo+"-"+stringGroupID,  convert.FourToFive(""+doublePercentRateSum,  0)) ;
            // PurchaseMoney
            doublePurchaseMoney           =  exeUtil.doParseDouble(stringPurchaseMoney) ;
            doublePurchaseMoneySum    =  exeUtil.doParseDouble(""+hashtablePurchaseMoney.get(stringFactoryNo+"-"+stringGroupID)) ;
            doublePurchaseMoneySum  +=  doublePurchaseMoney ;
            hashtablePurchaseMoney.put(stringFactoryNo+"-"+stringGroupID,  ""+doublePurchaseMoneySum) ;
            // 付款條件       PayCondition1
            if("999".equals(stringPayCondition1)) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [付款條件1] 不可為無。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table2",  intNo,  "PayCondition") ;
                return  false ; 
            }
            // 期數           MonthNum
/*            if("".equals(stringMonthNum)) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [期數] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table2",  intNo,  "MonthNum") ;
                return  false ; 
            }*/
        }
        // 修正 請購資訊 與 付款資訊 一致 邏輯 
        String            stringKey                         =  "" ;
        String[]          arrayTemp                     =  null ;
        Hashtable           hashtablePurchaseMoneyTable1    =  getGroupIDTable1(exeUtil) ;
        for(int  intNo=0  ;  intNo<vectorFactoryNo.size()  ;  intNo++) {
            stringKey                      =  (""+vectorFactoryNo.get(intNo)).trim() ;
            arrayTemp                    =  convert.StringToken(stringKey,  "-") ;
            stringFactoryNo            =  arrayTemp[0].trim() ;
            // 100 % 檢核
            doublePercentRateSum   =  exeFun.doParseDouble(""+hashtablePercentRate.get(stringKey)) ;
            /*if(doublePercentRateSum  !=  100) {
                JOptionPane.showMessageDialog(null,  "[廠商] 為 "  +  stringFactoryNo  +  " 之 [比例] 和不為 100 %。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                return  false ; 
            }*/
            
            
            // 金額相等檢核
            doublePurchaseMoneySum      =  exeUtil.doParseDouble(""+hashtablePurchaseMoney.get(stringKey)) ;
            doublePurchaseMoneySum2    =  exeUtil.doParseDouble(""+hashtablePurchaseMoneyTable1.get(stringKey)) ;
            if(doublePurchaseMoneySum  !=  doublePurchaseMoneySum2) {
                JOptionPane.showMessageDialog(null,  "[廠商] 為 "  +  stringFactoryNo  +  " 之 [金額] 總和 ("+convert.FourToFive(""+doublePurchaseMoneySum,  0)  +  ")"  +
                                                                                                                       " 與相對應 [請購資訊] 總和 ("+convert.FourToFive(""+doublePurchaseMoneySum2,  0)  +  ") 不同 。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                return  false ; 
            }
        }
        return  true ;
    }
    public  Hashtable  getGroupIDTable1(FargloryUtil  exeUtil) throws  Throwable {
        JTable               jtable1                                                     =  getTable("Table1") ;
        String                stringKEY                                                =  "" ;
        String                stringGroupID                                         =  "" ;
        String                stringFactoryNo                                      =  "" ;
        String                stringPurchaseMoney                             =  "" ;
        Hashtable         hashtablePurchaseMoney               =  new  Hashtable( ) ;
        double               doublePurchaseMoney               =  0 ;     
        for(int  intNo=0  ;  intNo<jtable1.getRowCount() ;  intNo++) {
            stringGroupID                   =  (""+getValueAt("Table1",  intNo,  "GroupID")).trim() ;
            stringFactoryNo                 =  (""+getValueAt("Table1",  intNo,  "FactoryNo")).trim() ;
            stringPurchaseMoney       =  (""+getValueAt("Table1",  intNo,  "PurchaseMoney")).trim() ;     // 請購金額
            //
            stringKEY  =  stringFactoryNo+"-"+stringGroupID ;
            //
            doublePurchaseMoney           =  exeUtil.doParseDouble(stringPurchaseMoney)  +  exeUtil.doParseDouble(""+hashtablePurchaseMoney.get(stringKEY)) ;
            hashtablePurchaseMoney.put(stringKEY,  convert.FourToFive(""+doublePurchaseMoney,  0)) ;
            //System.out.println("getGroupIDTable1------------stringKEY("+stringKEY+")doublePurchaseMoney("+doublePurchaseMoney+")------------------------------------------------") ;
        }
        return  hashtablePurchaseMoney;
    }
    public  Vector  getFactoryNoTable2( ) throws  Throwable {
        JTable   jtable2                 =  getTable("Table2") ;
        String    stringFactoryNo   =  "" ;
        Vector   vectorFactoryNo  =  new  Vector( ) ;
        for(int  intNo=0 ;  intNo<jtable2.getRowCount( )  ;  intNo++) {
            stringFactoryNo  =  (""+getValueAt("Table2",  intNo,  "FactoryNo")).trim() ;
            //
            if(vectorFactoryNo.indexOf(stringFactoryNo)  !=  -1)  continue ;
            //
            vectorFactoryNo.add(stringFactoryNo) ;
        }
        return  vectorFactoryNo ;
    }
    // 案別分攤 
    public  boolean  isTable3CheckOK(boolean  booleanUndergoWriteCheck,  String  stringFunction,  Farglory.util.FargloryUtil  exeUtil,  Doc.Doc2M010  exeFun) throws  Throwable {
        JTabbedPane   jtabbedPane1                              =  getTabbedPane("Tab1") ;
        JTable                jtable3                                         =  getTable("Table3") ;
        int                      intTablePanel                               =  4 ;
        int                      intYear                                         =  0 ;
        String                stringActionNo                              =  getValue("ActionNo").trim() ;
        String                stringBarCode                             =  getValue("BarCodeOld").trim( ) ;
        String                stringBudgetID                             =  "" ;
        String                stringBudgetMoney                      =  "" ;
        String                stringCDate                                  =  exeUtil.getDateConvertFullRoc(getValue("CDate").trim()) ;
        String                stringNeedDate                            =  getValue("NeedDate").trim() ;
        String                stringNeedDateAC                       =  exeUtil.getDateConvert(stringNeedDate) ;
        String                stringCDateAC                             =  "" ;
        String                stringCostID                                 = "" ;
        String                stringCostID1                               = "" ;
        String                stringComNo                                  =  getValue("ComNo").trim() ;
        String                stringDateStart                             =  "" ;
        String                stringDepartNo                             =  "" ;
        String                stringDulKey                                 =  "" ;
        String                stringEDateTime                           =  getValue("EDateTime").trim() ;
        String                stringFlow                                     =  getFunctionName() ;
        String                stringKey                                       =  "" ;
        String                stringProjectID1                            = "" ;
        String                stringProjectID1Use                      = "" ;
        String                stringRealMoney                           = "" ;
        String                stringType                                     =  "" ;
        String                stringTemp                                     =  "" ;
        String                stringLimitOut                                =  "%--%" ;
        String                stringDateStage                             =  "" ;
        String                stringStageDateStart                     =  "" ;
        String                stringStageDateEnd                       =  "" ;
        String                stringProjectID                                =  "" ; 
        String                sringProjectIDComput                     =  getProjectIDFromDepartNo(exeUtil,  exeFun) ;
        String                stringInOut                                      =  "" ;
        String                stringSqlAnd                                   =  "" ;
        String[][]            retDoc7M011                                 =  null ;
        String[][]            retDoc7M015                                 =  null ;
        String[][]            retDoc7M020                                 =  null ;
        double              doubleActionMoney                         = 0 ;
        double              doubleBudgetMoney                       = 0 ;
        double              doubleRealMoney                           = 0 ;
        double              doubleRealMoneySum                    = 0 ;
        double              doubleRealMoneySumS                   = 0 ;
        //Hashtable         hashtableDateStart                          =  new  Hashtable() ;                                   // 避免 重複判斷階段調整作業之生效日期，儲存
        Hashtable         hashtableBudgetMoney                  =  new  Hashtable() ;                                     // 預算檢核時使用
        Hashtable         hashtableBudgetMoneyBudgetID    =  new  Hashtable() ;                                   // 預算檢核時使用
        Hashtable         hashtableBudgetMoney2                 =  new  Hashtable() ;                                     // 預算檢核時使用
        Hashtable         hashtableBudgetMoneyBudgetID2 =  new  Hashtable() ;                                   // 預算檢核時使用
        Hashtable         hashtableRealMoney                      =  new  Hashtable() ;                                   // [請購資訊] 及 [案別分攤] 金額 一致檢核
        Vector               vectorCostID                                  =  new  Vector() ;                                     // [請購資訊] 及 [案別分攤] 金額 一致檢核
        Vector               vectorDulKey                                 =  new  Vector() ;                                      // 判斷表格 [案別][請款代碼][小請款代碼] 是否重複
        Vector               vectorProjectID1BudgetID               =  new  Vector() ;                                      // 預算檢核時使用
        Vector               vectorProjectID1Type                     =  new  Vector() ;                                      // 避免重複 預算 及 階段存在 檢核
        Vector               vectorProjectID1NoUseBudget        =  new  Vector() ;                                                                           // 不檢查預算
        Vector               vectorProject                                    =  new  Vector() ;
        Vector              vectorSpecCostID              =  getFunctionTypeUDoc2M0201 (exeUtil,  exeFun) ;
        boolean            booleanApply                                   =  ("096/11/26".compareTo(stringCDate)<=0) &&  "F".equals(getValue("ApplyType").trim()) ;
        boolean            booleanUser                                  =  stringFlow.indexOf("採購")  ==  -1 ;
        boolean            booleanTemp                                =  true ;
        boolean            boolean053Start                           =  getValue("DepartNo").startsWith("053") ;
        Hashtable        hashtableDoc3M043                       =  getDoc3M043(stringActionNo,  vectorProject,  exeFun,  exeUtil) ;
        Hashtable        hashtableThisUseMoney                =  new  Hashtable() ;

        if(booleanUndergoWriteCheck  &&  jtable3.getRowCount()  ==  0) {
          System.out.println(".......................................................isTable3CheckOK--------------------[案別分攤] 無資料。") ;
            JOptionPane.showMessageDialog(null,  " [案別分攤] 無資料。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
            jtabbedPane1.setSelectedIndex(intTablePanel) ;
            return  false ;
        }

        // 專案
        // 活動代碼 E02573、G89456 須設例外
        if(!"".equals(stringActionNo)  &&  vectorProject.size()==0)  {
            if(!"Z6".equals(stringComNo)) {
                messagebox("非 ["+get("Z6")+"] 不允許使用 專案 流程。") ;
                return  false ;
            }
            //
            String[][]  retDoc3M040  =  getDoc3M040(stringComNo,  stringActionNo,  exeFun) ;
            if(retDoc3M040.length  ==  0) {
                messagebox(" [活動代碼] 不存在。") ;
            } else {
                stringTemp  =  retDoc3M040[0][0].trim() ;
                if(!"".equals(stringTemp)) {
                    messagebox(" [活動代碼] 尚未完成流程。\n(有問題請洽 [行銷企劃室])") ;
                } else {
                    /*switch(stringTemp.charAt(0)) {
                        case 'A' : stringTemp  =  "創文" ;         break ;
                        case 'B' : stringTemp  =  "主辦人" ;     break ;
                        case 'C' : stringTemp  =  "審核" ;        break ;
                        case 'Y' : stringTemp  =  "體系主管" ; break ;
                    }*/
                    messagebox(" [活動代碼] 位於 [承辦] 流程，不允許執行。\n(有問題請洽 [行銷企劃室])") ;
                }
            }
            return  false ;
        }
        //
        vectorProjectID1NoUseBudget.add("0331---F1---F1") ;   // 
        vectorProjectID1NoUseBudget.add("0531---H42---H42A") ;   // 
        vectorProjectID1NoUseBudget.add("0531---M---M51A") ;   // 
        //
        stringCDateAC              =  exeUtil.getDateConvert(stringCDate) ;
        intYear                          =  exeUtil.doParseInteger(datetime.getYear(stringCDateAC.replaceAll("/",  ""))) ;    
        //
        boolean  booleanDepartProjectIDSame  =  "".equals(sringProjectIDComput) ;

        // 內業控管
        Vector     vectorDeptCd                  =  new  Vector() ;
        String    stringSpecBudget      =  ""+get("SPEC_BUDGET") ;
        String    stringDocNo1                  =  getValue("DocNo1").trim() ;
        String[]    arraySpecBudget         =  convert.StringToken(stringSpecBudget,",") ;
        vectorDeptCd.add("0331") ;    // 不動產行銷科
        vectorDeptCd.add("0333") ;      // 行銷企劃室
        for(int  intNo=0  ;  intNo<arraySpecBudget.length  ;  intNo++)  vectorDeptCd.add(arraySpecBudget[intNo]) ;    
        Vector  vectorDeptCd2  =  new  Vector() ;
        vectorDeptCd2.add("03365") ;    // 2014-01-28 依王承歡 新增
        //
        String     stringUndergoWrite  =  getValue("UNDERGO_WRITE").trim() ;
        String     stringApplyType        =  getValue("ApplyType").trim() ;
        String     string033FGType      =  "0" ;  // 0 未設定  1 033FG  2 非033FG  3 混用
        boolean  booleanRealMoney  =  stringFlow.indexOf("採購")!=-1; //("C,Y,".indexOf(stringUndergoWrite)!=-1)  ||  ("S".equals(stringUndergoWrite)  &&  "F".equals(stringApplyType)) ;
        boolean  booleanCostID805   =  false ;
        //
        if(!"Z6".equals(stringComNo)  &&  (","+stringSpecBudget+",").indexOf(","+stringDocNo1+",")!=-1) {
            messagebox("非 ["+get("Z6")+"] 不允許使用 "+stringDocNo1+" 費用。") ;
            return  false ;
        }
        Vector  vectorDoc2M0201 =  null ;
        String    stringSqlAnd807 =   " AND  FunctionType LIKE  '%2%' "+
                             " AND  (DateStart='9999/99/99'  OR  DateStart<='"+stringCDate+"' )" +
                             " AND  (DateEnd='9999/99/99'    OR  DateEnd>='"  +stringCDate+"' )" +
                             " AND  ComNo  IN ('ALL',  '"  +stringComNo+"' )" ;
        for(int  intNo=0 ;  intNo<jtable3.getRowCount( )  ;  intNo++) {
            stringInOut                 =  (""+getValueAt("Table3",  intNo,  "InOut")).trim() ;
            stringProjectID           =  (""+getValueAt("Table3",  intNo,  "ProjectID")).trim() ;
            stringProjectID1         =  (""+getValueAt("Table3",  intNo,  "ProjectID1")).trim() ;
            stringCostID               =  (""+getValueAt("Table3",  intNo,  "CostID")).trim() ;
            stringCostID1             =  (""+getValueAt("Table3",  intNo,  "CostID1")).trim() ;
            stringRealMoney        =  (""+getValueAt("Table3",  intNo,  "RealMoney")).trim() ;
            stringBudgetMoney    =  (""+getValueAt("Table3",  intNo,  "BudgetMoney")).trim() ;
            stringDepartNo           =  (""+getValueAt("Table3",  intNo,  "DepartNo")).trim() ;
            //
            if("805".equals(stringCostID+stringCostID1))  booleanCostID805  =  true ;
            // 310 僅允許使用在借款中
            if("310".indexOf(stringCostID+stringCostID1)  !=  -1) {
                  messagebox("請款代碼 310 不允許使用。\n(有問題請洽 [財務室])") ;
                  jtabbedPane1.setSelectedIndex(intTablePanel) ;
                  jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                  return  false ; 
            }
            //
            vectorDoc2M0201   =  exeFun.getQueryDataHashtableDoc("Doc2M0201",  new  Hashtable(),  stringSqlAnd807+" AND  CostID  =  '"+stringCostID+"' " +" AND  CostID1  =  '"+stringCostID1+"' ",  new  Vector(),  exeUtil) ;
            if(vectorDoc2M0201.size()  >  0) {
                  messagebox("[大-請款代碼][中-請款代碼]("+stringCostID+stringCostID1+") 不允許使用。\n(有問題請洽 [行銷管理室])。") ;
                  jtabbedPane1.setSelectedIndex(intTablePanel) ;
                  jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                  return  false ;   
            }
            //
            if(!"".equals(stringBarCode)  &&  "E99385,E99362,".indexOf(stringBarCode)  !=  -1) {

            } else if(",033CRM,".indexOf(","+getValue("DocNo1")+",")  !=  -1) {
                // 不作控管
            } else {
                System.out.println(intNo+"stringSpecBudget("+stringSpecBudget+")stringDepartNo("+stringDepartNo+")string033FGType("+string033FGType+")----------------------------------------") ;
                if(stringSpecBudget.indexOf(stringDepartNo)  ==  -1) {
                    if("1".equals(string033FGType)){
                        messagebox(stringDepartNo+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                        jtabbedPane1.setSelectedIndex(intTablePanel) ;
                        jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                        return  false ; 
                    }
                    if(stringSpecBudget.indexOf(getValue("DocNo1"))  !=  -1) {
                        messagebox(getValue("DocNo1")+"之案別分攤只能為"+getValue("DocNo1")+"。\n(有問題請洽 [財務室])") ;
                        jtabbedPane1.setSelectedIndex(intTablePanel) ;
                        jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                        return  false ;                     
                    }
                    System.out.println(intNo+"string033FGType("+string033FGType+")----------------------------------------1") ;
                    string033FGType  =  "2" ;
                } else {
                    if("I".equals(stringInOut)) {
                        if(stringDocNo1.equals(stringDepartNo)) {
                            if("2".equals(string033FGType)){
                                messagebox(stringDepartNo+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                                return  false ; 
                            }
                            if(stringSpecBudget.indexOf(stringDepartNo)  ==  -1) {
                                messagebox(stringDepartNo+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                                return  false ;                             
                            }
                            string033FGType  =  "1" ;
                        } else {
                            if("1".equals(string033FGType)){
                                messagebox(stringDepartNo+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                                return  false ; 
                            }
                            if(stringSpecBudget.indexOf(stringDepartNo)  !=  -1) {
                                messagebox(stringDepartNo+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                                return  false ;                             
                            }
                            string033FGType  =  "2" ; 
                        }
                    } else {
                        if("1".equals(string033FGType)){
                            messagebox(stringDepartNo+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                            jtabbedPane1.setSelectedIndex(intTablePanel) ;
                            jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                            return  false ; 
                        }
                        if(stringSpecBudget.indexOf(stringDepartNo)  !=  -1) {
                            messagebox(stringDepartNo+" 不允許和其它部門或案別共同分攤。\n(有問題請洽 [財務室])") ;
                            jtabbedPane1.setSelectedIndex(intTablePanel) ;
                            jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                            return  false ;                             
                        }
                        string033FGType  =  "2" ;
                    }
                    System.out.println(intNo+"string033FGType("+string033FGType+")----------------------------------------2") ;
                    if("1".equals(string033FGType)){
                        if(!stringDepartNo.equals(stringDocNo1)) {
                            messagebox("案別分攤分攤給 ["+stringDepartNo+"] 時，公文編號須為 "+stringDocNo1+"。\n(有問題請洽 [財務室])") ;
                            jtabbedPane1.setSelectedIndex(intTablePanel) ;
                            jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                            return  false ; 
                        }
                    }
                    if("2".equals(string033FGType)){
                        if(stringSpecBudget.indexOf(stringDocNo1)  !=  -1) {
                            messagebox("公文編號為 "+stringDocNo1+"時，案別分攤部門僅允許 ["+stringDepartNo+"]。\n(有問題請洽 [財務室])") ;
                            jtabbedPane1.setSelectedIndex(intTablePanel) ;
                            jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                            return  false ; 
                        }
                    }
                }
            }
            // 人壽案別檢查
            /*if("Z6".equals(stringComNo)  &&  !"".equals(stringProjectID1)&&  "H56A,H85A".indexOf(stringProjectID1)!=-1) {
                messagebox("第 " +(intNo+1) +" 列 [遠雄房屋] 不允許使用 H56A、H85A 之 案別！，不允許異動資料庫。\n(有問題請洽 [財務室])") ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ; 
            }*/
            //if("B3018".equals(getUser())) {
            //    messagebox(sringProjectIDComput) ;
            //}
            if(!booleanDepartProjectIDSame) {
                if(boolean053Start) {
                    if("0531".equals(stringDepartNo)  &&  sringProjectIDComput.indexOf(","+stringProjectID1+",")!=-1) booleanDepartProjectIDSame  =  true ;
                } else {
                    if(!"0531".equals(stringDepartNo)  &&  sringProjectIDComput.indexOf(","+stringProjectID1+",")!=-1) booleanDepartProjectIDSame  =  true ;
                }
            }
            //
            if("0531".equals(stringDepartNo)) {
                stringProjectID1Use  =  stringDepartNo.substring(0,  3)  +  stringProjectID1;
            } else {
                stringProjectID1Use  =  stringProjectID1 ;
            }
            //
            if("".equals(stringProjectID)) {
                stringKey  =  stringDepartNo ;
            } else {
                stringKey  =  stringProjectID+"---"+stringProjectID1 ;
            }
            if(!"".equals(stringActionNo)  &&  vectorProject.indexOf(stringKey)==-1  &&  "E02573,".indexOf(stringBarCode+",")==-1) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [案別] ("+stringProjectID+"---"+stringProjectID1+") 不存在 [活動代碼] 中。\n(有問題請洽 [行銷企劃室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ; 
            } else {
                if(booleanRealMoney) {
                    stringTemp  =  stringRealMoney ;
                } else {
                    stringTemp   =  stringBudgetMoney ;
                }
                doubleActionMoney  +=  exeFun.doParseDouble(stringTemp) ;
                stringTemp                 =  ""+(exeUtil.doParseDouble(""+hashtableThisUseMoney.get(stringKey))+exeUtil.doParseDouble(stringTemp)) ;
                hashtableThisUseMoney.put(stringKey,  stringTemp) ;
            }
            //
            stringKey                    =  stringCostID            +  "-"  +  stringCostID1 ;
            if("I".equals(stringInOut)) {
                stringDulKey              =  stringDepartNo  +  "-"  +  stringKey ;
            } else {
                stringDulKey              =  stringProjectID1Use  +  "-"  +  stringKey ;
            }
            //
            /*if(exeUtil.doParseDouble(stringRealMoney)  ==  0) {
                if(booleanUser){
                  JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [金額]  不可為 0。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                  jtabbedPane1.setSelectedIndex(intTablePanel) ;
                  jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                  return  false ; 
                }
            }*/
            // 重複檢核
            if(vectorDulKey.indexOf(stringDulKey)  !=  -1) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [案別] [大-請款代碼] [中-請款代碼] 發生重複。\n(有問題請洽 [行銷管理室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ; 
            }
            vectorDulKey.add(stringDulKey) ;
            // 請款代碼                   CostID、CostID1
            if("".equals(stringCostID)) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [大-請款代碼] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ; 
            }
            if("".equals(stringCostID1)) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [中-請款代碼] 不可為空白。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ; 
            }
            retDoc7M011  =  exeFun.getDoc7M011(stringComNo,  "",  stringCostID,  stringCostID1) ;
            if(retDoc7M011.length==0) {
                if("1".equals(string033FGType)  ||  exeFun.getDoc2M0201(stringComNo,  stringCostID,  stringCostID1,  "A").length  ==  0) {
                    JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 請款代碼 不存在於 [預算費用對照作業(Doc7M015)] 中。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTablePanel) ;
                    jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                    return  false ;               
                }
            }
            if(vectorDeptCd2.indexOf(stringDepartNo)==-1   &&  vectorDeptCd.indexOf(stringDepartNo)==-1   &&  !exeFun.isInOutToCostID(stringInOut.trim( ),  stringCostID.trim( ))) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 [請款代碼] 與 [內/外業] 不一致。\n(有問題請洽 [行銷管理室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ; 
            }
            if(vectorCostID.indexOf(stringKey)  ==  -1)       vectorCostID.add(stringKey) ;
            // 單一請款代碼檢核 
            if(vectorSpecCostID.size()>0  &&  vectorSpecCostID.indexOf(stringCostID+stringCostID1)!=-1) {
                if(vectorCostID.size()  >  1) {
                    JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 特殊 [請款代碼] 不允許和其它 [請款代碼] 一起請購。\n(有問題請洽 [資訊企劃室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTablePanel) ;
                    jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                    return  false ;                 
                }
            }
            /* 案階段調整 存在檢核 */
            stringBudgetID  =  (retDoc7M011.length>0) ? retDoc7M011[0][0].trim() : "" ;
            stringType         =  (stringBudgetID.length()>0) ? stringBudgetID.substring(0,  1) : "" ;
            // "H45A".equals(stringProjectID1)  &&  
            if("1".equals(string033FGType)  &&  !"B".equals(stringType)  &&  ",J20996,".indexOf(","+stringBarCode+",")==-1) {
                messagebox("第 "  +  (intNo+1)  +  " 列 "+stringDocNo1+" 只允許使用企劃類 [請款代碼]。\n(有問題請洽 [財務室])") ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ;                   
            }
            if("03365,".indexOf(stringDepartNo+",")!=-1  &&  "A".equals(stringType)) {
                stringProjectID1Use  =  stringDepartNo ;
            }
            if("B".equals(stringType)  &&  "F1".equals(stringProjectID1)  &&  "23,26,49,72".indexOf(stringCostID+",")==-1) {
                messagebox("第 "  +  (intNo+1)  +  " 列 案別 F1 只允許申請特定 [請款代碼] (23,26,49,72)。\n(有問題請洽 [財務室])") ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable3.setRowSelectionInterval(intNo,  intNo)  ;
                return  false ;   
            }
            booleanTemp    =  !("001".equals(stringCostID+stringCostID1))  &&  
                                           !"".equals(stringProjectID1Use)  &&  
                             !"".equals(stringType)  &&  
                             vectorProjectID1Type.indexOf(stringProjectID1Use+"-"+stringType)  ==  -1  &&
                             vectorProjectID1NoUseBudget.indexOf(stringDepartNo+"---"+stringProjectID+"---"+stringProjectID1)  ==  -1 ;  // 特殊案別，不作預算檢核
            if(vectorDeptCd.indexOf(stringDepartNo)!=-1  &&  "B".equals(stringType)) {
                stringProjectID1Use  =  stringDepartNo ;
                booleanTemp           =  false ;
            }
            if(booleanTemp) {
                stringStageDateStart       =  "" ; 
                stringStageDateEnd        =  "" ; 
                retDoc7M020                  =  exeFun.getDoc7M020ForComNo(stringComNo,  stringProjectID1Use,  stringType,  stringNeedDateAC,  "<=",  "",  "U") ;
                if(retDoc7M020.length  ==  0) {
                    booleanTemp              =  true ;
                    stringDateStage          =  "00" ;
                } else {
                    booleanTemp              =  false ;
                }
                // 0  BuildYMD        1  DateStage                                      2  BudgetMoney                   3  StageDateStart      4  StageDateEnd
                // 5  DateStart            
                vectorProjectID1Type.add(stringProjectID1Use+"-"+stringType) ;
            }
            if(!"".equals(stringProjectID1Use)  &&  vectorProjectID1BudgetID.indexOf(stringProjectID1Use+"-"+stringBudgetID)  ==  -1
                                                                   &&  vectorProjectID1NoUseBudget.indexOf(stringDepartNo+"---"+stringProjectID+"---"+stringProjectID1)  ==  -1) vectorProjectID1BudgetID.add(stringProjectID1Use+"-"+stringBudgetID) ;
            // 總金額 須等於 請購資訊總金額  RealMoney
            doubleRealMoney  =  exeUtil.doParseDouble(stringRealMoney)  ;
            /*if(booleanUser  &&  doubleRealMoney  ==  0) {
                JOptionPane.showMessageDialog(null,  "第 "  +  (intNo+1)  +  " 列 之 [總金額] 不可為 0。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table3",  intNo,  "RealMoney") ;
                return  false ; 
            }*/
            // 資料處理
            doubleRealMoneySum        =  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) ;
            doubleRealMoneySum      +=  doubleRealMoney ;
            hashtableRealMoney.put(stringKey,                ""+doubleRealMoneySum) ;   // [請購資訊] 及 [案別分攤] 金額 一致檢核
            //
            doubleRealMoneySum    =  exeUtil.doParseDouble(""+hashtableBudgetMoney.get(stringProjectID1Use+"-"+stringType)) ;
            doubleRealMoneySum  +=  doubleRealMoney ;
            hashtableBudgetMoney.put(stringProjectID1Use+"-"+stringType,  ""+doubleRealMoneySum) ;    // 預算檢核時使用
            //System.out.println(stringProjectID1Use+"-"+stringType+"]-------------------------------------------------"+doubleRealMoneySum) ;
            //
            doubleRealMoneySum    =  exeUtil.doParseDouble(""+hashtableBudgetMoneyBudgetID.get(stringProjectID1Use+"-"+stringBudgetID)) ;
            doubleRealMoneySum  +=  doubleRealMoney ;
            hashtableBudgetMoneyBudgetID.put(stringProjectID1Use+"-"+stringBudgetID,  ""+doubleRealMoneySum) ;    // 預算檢核時使用
            //System.out.println(stringProjectID1Use+"-"+stringBudgetID+"]-------------------------------------------------"+doubleRealMoneySum) ;
            // 預算金額
            doubleBudgetMoney        =  exeUtil.doParseDouble(stringBudgetMoney)  ;
            doubleRealMoneySum     =  exeUtil.doParseDouble(""+hashtableBudgetMoney2.get(stringProjectID1Use+"-"+stringType)) ;
            doubleRealMoneySum   +=  doubleBudgetMoney ;
            hashtableBudgetMoney2.put(stringProjectID1Use+"-"+stringType,  ""+doubleRealMoneySum) ; 
            //System.out.println(stringProjectID1Use+"-"+stringType+"]-------------------------------------------------"+doubleRealMoneySum) ;
            //
            doubleRealMoneySum    =  exeUtil.doParseDouble(""+hashtableBudgetMoneyBudgetID2.get(stringProjectID1Use+"-"+stringBudgetID)) ;
            doubleRealMoneySum  +=  doubleBudgetMoney ;
            hashtableBudgetMoneyBudgetID2.put(stringProjectID1Use+"-"+stringBudgetID,  ""+doubleRealMoneySum) ;   // 預算檢核時使用
            //System.out.println(stringProjectID1Use+"-"+stringBudgetID+"]-------------------------------------------------"+doubleRealMoneySum) ;
        }
        // 805 
        String  stringCheckAdd            =  getValue("CheckAdd").trim();
        String  stringCheckAddDescript    =  getValue("CheckAddDescript").trim();
        if(booleanCostID805) {
            if(!"F".equals(stringApplyType)) {
                  if(!"F".equals(stringCheckAdd)) {
                        // 非統購 非其它
                        setValue("CheckAdd",          "F") ;
                        setValue("CheckAddDescript",   "簽呈編號：") ;
                        messagebox("請款代碼 805 贈品 非統購時，[檢附] 必須選擇 其它，[檢附說明] 必須輸入 簽呈編號。") ;
                        return  false ;
                  } else {
                        if(stringCheckAddDescript.indexOf("簽呈編號")  ==  -1) {
                            messagebox("請款代碼 805 贈品 非統購時，[檢附] 必須選擇 其它，[檢附說明] 必須輸入 簽呈編號。") ;
                            return  false ;
                        }
                  }
            }
        }
        // 專案檢查 
        if(!"".equals(stringActionNo)  &&  !(!"".equals(stringBarCode)  &&  "E02573,".indexOf(stringBarCode+",")!=-1)) {
            String         stringKeyL                                  =  "" ;
            Hashtable  hashtableUseMoneyDoc3M011  =  getUseMoneyDoc3M011(stringBarCode,  stringActionNo, exeFun,  exeUtil) ;
            double       doubleUseMoney                         =  0 ;
            double       doubleCheckMoney                     =  0 ;
            for(int  intNo=0  ;  intNo<vectorProject.size()  ;  intNo++) {
                stringKeyL                =  ""+vectorProject.get(intNo) ;
                doubleUseMoney      =  exeUtil.doParseDouble(""+hashtableUseMoneyDoc3M011.get(stringKeyL))  +  exeUtil.doParseDouble(""+hashtableThisUseMoney.get(stringKeyL)) ;
                doubleCheckMoney  =  exeUtil.doParseDouble(""+hashtableDoc3M043.get(stringKeyL)) ;
                if(doubleCheckMoney+100  <  doubleUseMoney) {
                    JOptionPane.showMessageDialog(null,  "案別("+stringKeyL+") 已使用金額("+exeUtil.getFormatNum2 (""+doubleUseMoney)+")超過專案可用金額("+exeUtil.getFormatNum2 (""+doubleCheckMoney)+")。\n(有問題請洽 [行銷企劃室])",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTablePanel) ;
                    return  false ;   
                }
            }
        }
        // 部門、案別分攤一致檢核
        if(!booleanDepartProjectIDSame  &&  !"".equals(stringBarCode)  &&  "E43509,E45116,E43517,".indexOf(stringBarCode)  ==  -1) {
            boolean  booleanError  =  true ;
            if(",J38125,".indexOf(","+stringBarCode+",")  !=  -1) {
                booleanError  =  false ;
            }
            if(booleanError) {
                JOptionPane.showMessageDialog(null,  "部門不存在案別分攤中。("+sringProjectIDComput+")",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                return  false ;   
            }
        }
        if((stringFlow.indexOf("採購")!=-1  ||  booleanApply)  &&  booleanUndergoWriteCheck) {
            //Hashtable         hashtableRealMoneyS                  =  (Hashtable) get("Doc3M010_PurchaseMoney_CostID") ;    // [請購資訊] 及 [案別分攤] 金額 一致檢核
            Hashtable         hashtableRealMoneyS                  =  getCostIDMoneyTable1(exeUtil) ;     // [請購資訊] 及 [案別分攤] 金額 一致檢核
            for(int  intNo=0  ;  intNo<vectorCostID.size()  ;  intNo++) {
                stringKey                         =  (""+vectorCostID.get(intNo)).trim() ;
                doubleRealMoneySum    =  exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKey)) ;
                doubleRealMoneySumS  =  exeUtil.doParseDouble(""+hashtableRealMoneyS.get(stringKey)) ;
                //System.out.println(intNo+"[請購資訊] 及 [案別分攤] 金額 一致檢核-----stringKey("+stringKey+")doubleRealMoneySum("+doubleRealMoneySum+")doubleRealMoneySumS("+doubleRealMoneySumS+")----------------------------------------") ;
                // 本身：[請購資訊] 及 [案別分攤] 金額 一致檢核
                if(doubleRealMoneySum  !=  doubleRealMoneySumS) {
                    String[]  arrayCostID  =  convert.StringToken(stringKey,  "-") ;
                    //
                    stringCostID   =  "" ;
                    stringCostID1  =  "" ;
                    if(arrayCostID.length  ==  2) {
                        stringCostID    =  arrayCostID[0] ;
                        stringCostID1  =  arrayCostID[1] ;
                    }
                    JOptionPane.showMessageDialog(null,  "請款代碼("+stringCostID+")、小請款代碼("+stringCostID1+") 之 對應的 [ 案別分攤]("+doubleRealMoneySum+") 及 [採購資訊]("+doubleRealMoneySumS+") 總金額不相等。",  "訊息",  JOptionPane.ERROR_MESSAGE) ;
                    return  false ;
                }
            }
        }

        if(!booleanUndergoWriteCheck)  return  true ;
        //
        vectorProjectID1Type  =  new  Vector() ;
        //
        String         stringBuildYMD                    =  "" ;
        String         stringBudgetName                =  "" ;
        String         stringDateStartRoc              =  "" ;
        String         stringErrorMessage             =  "" ;
        String         stringMailMessage               =  "" ;
        String         stringEmployeeNo               =  "" ;
        String         stringProjectID1F                 =  "" ;
        String         stringStageName                 =  "" ;
        String[]       retMoneyCheck                    =  null ;
        String[]       retMoneyCheck2                  =  null ;
        String[]       arrayTemp                           =  null ;
        String[]       arrayProjectID1BudgetID   =  (String[]) vectorProjectID1BudgetID.toArray(new  String[0]) ;
        String[][]     retDoc7M012                      =  null ;
        String[][]     retDoc7M019                      =  null ;
        String[][]     retDoc7M021                      =  null ;
        double       doubleBudgetMoneyL         =  0 ;
        double       doubleBudgetMoneyL2         =  0 ;
        double       doubleBudgetMoneySum    = 0 ;
        double       doubleBudgetMoneySumL  =  0 ;
        double       doubleBudgetMoneySumL2  =  0 ;
        double       doublePurchaseMoney       =  0 ;
        double       doublePurchaseMoney2     = 0 ;
        boolean     booleanError                       =  true  ;
        boolean     booleanCheckNew               =  (","+stringSpecBudget+",").indexOf(","+stringDocNo1+",")!=-1 ;
        Hashtable  hashtableUserIDMessage  =  new  Hashtable() ;
        Vector        vectorUserID                      =  new  Vector() ;
        //
        Arrays.sort(arrayProjectID1BudgetID) ;
        if(booleanCheckNew) {
            // 033FG 預算特別檢查
            getButton("Button033FG").doClick() ;
            //
            String[][]  retTableData  =  getTableData("TableCheck") ;
            if(retTableData.length==0) {
                messagebox("資料發生錯誤，請洽資訊室。2") ;
                return  false ;
            }
            if(retTableData.length==1  &&  "OK".equals(retTableData[0][0])) {
                return  true ;
            }
            booleanError  =  false ;
        } else {
            setTableData("Table7",  new  String[0][0]) ;
            setTableData("Table8",  new  String[0][0]) ;
            System.out.println("年度檢核------------------------------------------------S異動資料庫") ;
            Vector  vectorTableCheck  =  new  Vector() ;
            for(int  intNo=0  ;  intNo<arrayProjectID1BudgetID.length  ;  intNo++) {
                stringTemp                            =  arrayProjectID1BudgetID[intNo].trim() ;
                arrayTemp                            =  convert.StringToken(stringTemp,  "-") ;               
                stringProjectID1                    =  arrayTemp[0].trim() ;      stringProjectID1F  =  stringProjectID1 ;  if("".equals(stringProjectID1))  continue ;
                stringBudgetID                      =  arrayTemp[1].trim() ;                                                                        if("".equals(stringBudgetID))     continue ;
                stringType                             =  stringBudgetID.substring(0,  1) ;
                doubleBudgetMoneySumL    =  exeUtil.doParseDouble(""+hashtableBudgetMoney.get(stringProjectID1+"-"+stringType)) ;
                doubleBudgetMoneySumL2  =  exeUtil.doParseDouble(""+hashtableBudgetMoney2.get(stringProjectID1+"-"+stringType)) ;
                doubleBudgetMoneyL           =  exeUtil.doParseDouble(""+hashtableBudgetMoneyBudgetID.get(stringProjectID1+"-"+stringBudgetID)) ;
                doubleBudgetMoneyL2        =  exeUtil.doParseDouble(""+hashtableBudgetMoneyBudgetID2.get(stringProjectID1+"-"+stringBudgetID)) ;
                //
                arrayTemp                           =  new  String[1] ;
                arrayTemp[0]                       =  stringProjectID1+"%-%"+stringBudgetID+"%-%"+doubleBudgetMoneyL+"%-%"+doubleBudgetMoneySumL+"%-%"+doubleBudgetMoneyL2+"%-%"+doubleBudgetMoneySumL2 ;
                vectorTableCheck.add(arrayTemp) ;
            }

            if(vectorTableCheck.size()  >  0) {
                setTableData("TableCheck",  (String[][])vectorTableCheck.toArray(new  String[0][0])) ;
                getButton("ButtonTableCheck").doClick() ;
                String[][]  retTableData  =  getTableData("TableCheck") ;
                if(retTableData.length==0) {
                    messagebox("資料發生錯誤，請洽資訊室。3") ;
                    return  false ;
                }
                if(retTableData.length==1  &&  "OK".equals(retTableData[0][0]))     return  true ;
                return  false ;
            } else {
                return  true ;
            }
        }
        return  booleanError ;
    }
    public  Hashtable  getCostIDMoneyTable1(FargloryUtil  exeUtil) throws  Throwable {
        Hashtable         hashtablePurchaseMoney                       =  new  Hashtable( ) ;
        JTable               jtable1                                                     =  getTable("Table1") ;
        String                stringApplyMoney                                   =  "" ;
        String                stringCostID                                            =  "" ;
        String                stringCostID1                                          =  "" ;
        String                stringKey                                                 =  "" ;
        String                stringPurchaseMoney                             =  "" ;
        String                stringFlow                                               =  getFunctionName() ;
        double              doublePurchaseMoney                           =  0 ;
        boolean             booleanFlag                                            =  stringFlow.indexOf("採購")  ==  -1  ;
        for(int  intNo=0  ;  intNo<jtable1.getRowCount() ;  intNo++) {
            stringApplyMoney             =  (""+getValueAt("Table1",  intNo,  "ApplyMoney")).trim() ;  // 預算金額
            stringPurchaseMoney       =  (""+getValueAt("Table1",  intNo,  "PurchaseMoney")).trim() ;     // 請購金額
            stringCostID                      =  (""+getValueAt("Table1",  intNo,  "CostID")).trim() ;
            stringCostID1                    =  (""+getValueAt("Table1",  intNo,  "CostID1")).trim() ; 
            //
            stringKey                           =  stringCostID  +  "-"  +  stringCostID1 ;
            //
            doublePurchaseMoney                  =  exeUtil.doParseDouble(stringPurchaseMoney) ;
            if(booleanFlag)     doublePurchaseMoney  =  exeUtil.doParseDouble(stringApplyMoney) ;
            //
            doublePurchaseMoney          +=  exeUtil.doParseDouble(""+hashtablePurchaseMoney.get(stringKey)) ;
            hashtablePurchaseMoney.put(stringKey,  ""+doublePurchaseMoney) ;
            //System.out.println(intNo+"getCostIDMoneyTable1-----stringKey("+stringKey+")doublePurchaseMoney("+doublePurchaseMoney+")----------------------------------------") ;
        }
        return  hashtablePurchaseMoney;
    }
    public  Vector  getFunctionTypeUDoc2M0201 (FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
        String            stringComNo                 =  getValue("ComNo").trim() ;
        String              stringCDateAC                 =  exeUtil.getDateConvert(getValue("CDate").trim()) ;
        String            stringSqlAnd                  =  " AND  (ComNo  =  '"+stringComNo+"'  OR  ComNo  =  'ALL') "  +
                                          " AND  FunctionType  LIKE  '%U%' " +
                                          " AND  (DateStart  <=  '"+stringCDateAC+"'  OR  DateStart  = '9999/99/99') " +
                                          " AND  (DateEnd  >=  '"+stringCDateAC+"') " ;
        String             stringCostID                     =  "" ;
        String             stringCostID1                   =  "" ;
        Vector            vectorSpecCostID        =  new  Vector() ;
        Vector            vectorDoc2M0201           =  exeFun.getQueryDataHashtableDoc("Doc2M0201",  new  Hashtable(),  stringSqlAnd,  new  Vector(),  exeUtil) ;
        Hashtable     hashtableDoc2M0201    =  null ;
        for(int  intNo=0  ;  intNo<vectorDoc2M0201.size()  ;  intNo++) {
            hashtableDoc2M0201  =  (Hashtable) vectorDoc2M0201.get(intNo) ;  if(hashtableDoc2M0201  ==  null)  continue ;
            stringCostID                 =  ""+hashtableDoc2M0201.get("CostID") ;        
            stringCostID1               =  ""+hashtableDoc2M0201.get("CostID1") ;        
            //
            System.out.println(intNo+"getFunctionTypeUDoc2M0201("+(stringCostID+stringCostID1)+")---------------------------------") ;
            if(vectorSpecCostID.indexOf(stringCostID+stringCostID1)  ==  -1)  vectorSpecCostID.add(stringCostID+stringCostID1) ;
        }
        return  vectorSpecCostID ;
    }
    public  boolean  isCoinTypeCheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable                jtable3                                         =  getTable("Table3") ;
        String                stringComNo                                 =  getValue("ComNo").trim() ;
        String                stringInOut                                    = "" ;
        String                stringProjectID1                            = "" ;
        String                stringCoinTypeL                           = "" ;
        String                stringCoinType                             = "" ;
        Vector                vectorProjectID1                          =  new  Vector() ;
        Hashtable         hashtableAnd                               =  new  Hashtable() ;
        //
        for(int  intNo=0 ;  intNo<jtable3.getRowCount( )  ;  intNo++) {
            stringProjectID1         =  (""+getValueAt("Table3",  intNo,  "ProjectID1")).trim() ;
            stringInOut                 =  (""+getValueAt("Table3",  intNo,  "InOut")).trim() ;
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
                messagebox("請購單 不允許多幣值。") ;
                return  false ;
            }
            stringCoinType  =  stringCoinTypeL ;
        }
        //
        setValue("CoinType",  stringCoinType) ;
        return   true ;
    }
    public  void  doMailData(String  stringProjectID1,  String  stringType,  String  stringMailMessage,  Hashtable  hashtableUserIDMessage,  Vector  vectorUserID,  Doc.Doc2M010  exeFun) throws  Throwable {
        String      stringEmployeeNo  =   "" ;
        String      stringTemp             =  "" ;
        String[][]  retDoc7M019         =  exeFun.getDoc7M019(stringProjectID1,  "",  stringType,  "A") ;
        for(int  intDoc7M019=0  ;  intDoc7M019<retDoc7M019.length  ;  intDoc7M019++) {
            stringEmployeeNo  =  retDoc7M019[intDoc7M019][1].trim() ;
            stringTemp             =  ""+hashtableUserIDMessage.get(stringEmployeeNo) ;
            if("null".equals(stringTemp))  stringTemp    =  "" ;
            if(!"".equals(stringTemp))        stringTemp  +=  "<br>" ;
            stringTemp  +=  stringMailMessage ;
            hashtableUserIDMessage.put(stringEmployeeNo,  stringTemp) ;
            if(vectorUserID.indexOf(stringEmployeeNo)  ==  -1)  vectorUserID.add(stringEmployeeNo) ;
        }
        retDoc7M019  =  exeFun.getDoc7M019("ALL",  "",  stringType,  "A") ;
        for(int  intDoc7M019=0  ;  intDoc7M019<retDoc7M019.length  ;  intDoc7M019++) {
            stringEmployeeNo  =  retDoc7M019[intDoc7M019][1].trim() ;
            stringTemp             =  ""+hashtableUserIDMessage.get(stringEmployeeNo) ;
            if("null".equals(stringTemp))  stringTemp    =  "" ;
            if(!"".equals(stringTemp))        stringTemp  +=  "<br>" ;
            stringTemp  +=  stringMailMessage ;
            hashtableUserIDMessage.put(stringEmployeeNo,  stringTemp) ;
            if(vectorUserID.indexOf(stringEmployeeNo)  ==  -1)  vectorUserID.add(stringEmployeeNo) ;
        }
    }
    public  String  getProjectIDFromDepartNo(Farglory.util.FargloryUtil  exeUtil,  Doc.Doc2M010  exeFun) throws  Throwable {
        String    stringDepartNo  =  getValue("DepartNo").trim() ;
        String    stringBarCode   =  getValue("BarCode").trim() ;
        String    stringComNo     =  getValue("ComNo").trim() ;
        return  exeFun.getProjectIDFromDepartNo(stringDepartNo,  "",  stringBarCode,  stringComNo,  exeUtil) ; 
    }
    public  Vector  getCostIDTable3( ) throws  Throwable {
        Vector  vectorCostID        =  new  Vector( ) ;
        JTable   jtable3                =  getTable("Table3") ; 
        String    stringCostID       =  "" ;
        String    stringCostID1     =  "" ;
        String    stringKey            =  "" ;
        for(int  intNo=0 ;  intNo<jtable3.getRowCount( )  ;  intNo++) {
            stringCostID    =  (""+getValueAt("Table3",  intNo,  "CostID")).trim() ;
            stringCostID1  =  (""+getValueAt("Table3",  intNo,  "CostID1")).trim() ;
            stringKey         =  stringCostID  +  "-"  +  stringCostID1 ;
            if(vectorCostID.indexOf(stringKey)  !=  -1)  continue ;
            vectorCostID.add(stringKey) ;
        }
        return  vectorCostID ;
    }
    //
    public  String[][]  getDoc3M040(String  stringComNo,  String  stringActionNo,  Doc.Doc2M010  exeFun) throws  Throwable {
        String      stringSql                =  "" ;
        String      stringKey               =  "" ;
        String[][]  retDoc3M040        =  null ;
        //
        stringSql  =  "SELECT  UNDERGO_WRITE "  +
                             " FROM  Doc3M040 "  +
                   " WHERE  DocNo  =  '"   +  stringActionNo  +  "' " +
                         " AND  ComNo  =  '"  +  stringComNo    +  "' "  ;
        retDoc3M040  =  exeFun.getTableDataDoc(stringSql) ;
        return  retDoc3M040 ;
    }
    public  Hashtable  getDoc3M043(String  stringActionNo,  Vector  vectorProject,  Doc.Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        String        stringSql                     =  "" ;
        String         stringKey                   =  "" ;
        String         stringDateAC             =  exeUtil.getDateConvert(getValue("CDate").trim()) ;
        String         stringComNo              =  getValue("ComNo").trim() ;
        String[][]     retDoc3M043             =  null ;
        double       doubleTemp               =  0 ;
        Hashtable  hashtableDoc3M043  =  new  Hashtable() ;
        //
        if("".equals(stringActionNo)) {
            return  hashtableDoc3M043 ;
        }
        //
        stringSql  =  "SELECT  ProjectID,  ProjectID1,  UseMoney,  DepartNo "  +
                             " FROM  Doc3M043 "  +
                   " WHERE  RTRIM(DocNo1)+RTRIM(DocNo2)+RTRIM(DocNo3)  IN  (SELECT  DocNo "  +
                                                                                                                                   " FROM  Doc3M040 "  +
                                                                         " WHERE  UNDERGO_WRITE  =  'Y' "  +
                                                                               " AND  ComNo  =  '"        +  stringComNo     +  "' "  +
                                                                             " AND  DocNo  =  '"         +  stringActionNo  +  "' "  +
                                                                             " AND  DateStart  <=  '"  +  stringDateAC     +  "' "  +
                                                                             " AND  DateEnd   >=  '"   +  stringDateAC      +  "') "  ;
        retDoc3M043  =  exeFun.getTableDataDoc(stringSql) ;
        for(int  intNo=0  ;  intNo<retDoc3M043.length  ;  intNo++) {
            if("".equals(retDoc3M043[intNo][0].trim())) {
                stringKey  =  retDoc3M043[intNo][3].trim() ;
            } else {
                stringKey  =  retDoc3M043[intNo][0].trim()  +  "---"+retDoc3M043[intNo][1].trim() ;
                /*if("Z6".equals(stringComNo)) {
                    if("H56A,H85A,".indexOf(retDoc3M043[intNo][1].trim())  !=  -1)  continue ;
                }
                if("CS".equals(stringComNo)) {
                    if("H56A,H85A,".indexOf(retDoc3M043[intNo][1].trim())  ==  -1)  continue ;
                }*/
            }
            if(vectorProject.indexOf(stringKey)==-1)  vectorProject.add(stringKey) ;
            doubleTemp  =  exeUtil.doParseDouble(""+hashtableDoc3M043.get(stringKey))+exeUtil.doParseDouble(retDoc3M043[intNo][2].trim()) ;
            hashtableDoc3M043.put(stringKey,  convert.FourToFive(""+doubleTemp,  0)) ;
        }
        return  hashtableDoc3M043 ;
    }
    public  Hashtable  getUseMoneyDoc3M011(String  stringBarCode,  String  stringActionNo, Doc.Doc2M010  exeFun,  FargloryUtil  exeUtil) throws  Throwable {
        String        stringSql                                      =  "" ;
        String         stringKey                                     =  "" ;
        String         stringProjectID                            =  "" ;
        String         stringProjectID1                          =  "" ;
        String         stringDepartNo                           =  "" ;
        String[][]     retDoc3M014                              =  null ;
        double       doubleTemp                                =  0 ;
        Hashtable  hashtableUseMoneyDoc3M011  =  new  Hashtable() ;
        //
        stringSql  =  "SELECT  ProjectID,  ProjectID1,  (RealMoney-NoUseRealMoney),  M14.DepartNo "  +
                             " FROM  Doc3M014 M14,  Doc3M011 M11 "  +
                   " WHERE  M14.BarCode  =  M11.BarCode "  +
                       " AND  (M11.UNDERGO_WRITE  IN  ('Y',  'C')  OR "  +
                               " (M11.UNDERGO_WRITE='S'  AND  ApplyType  =  'F'))"  +
                     " AND  M11.UNDERGO_WRITE  <>  'X' "  +
                     " AND  M11.BarCode  <>  '" +  stringBarCode  +  "' "  +
                     " AND  M11.ActionNo  =  '" +  stringActionNo  +  "' "  ;
        retDoc3M014  =  exeFun.getTableDataDoc(stringSql) ;
        for(int  intNo=0  ;  intNo<retDoc3M014.length  ;  intNo++) {
            stringProjectID    =  retDoc3M014[intNo][0].trim() ;
            stringProjectID1  =  retDoc3M014[intNo][1].trim() ;
            stringDepartNo  =  retDoc3M014[intNo][3].trim() ;
            if("".equals(stringProjectID)  &&  "".equals(stringProjectID1)) {
                stringKey            =  stringDepartNo ;
            } else {
                stringKey            =  stringProjectID  +  "---"+stringProjectID1 ;
            }
            doubleTemp  =  exeUtil.doParseDouble(""+hashtableUseMoneyDoc3M011.get(stringKey))+exeUtil.doParseDouble(retDoc3M014[intNo][2].trim()) ;
            hashtableUseMoneyDoc3M011.put(stringKey,  convert.FourToFive(""+doubleTemp,  0)) ;
        }
        stringSql  =  "SELECT  ProjectID,  ProjectID1,  (RealMoney-NoUseRealMoney),  M14.DepartNo "  +
                             " FROM  Doc3M014 M14,  Doc3M011 M11 "  +
                   " WHERE  M14.BarCode  =  M11.BarCode "  +
                       " AND  NOT  (M11.UNDERGO_WRITE  IN  ('Y',  'C')  OR "  +
                                        " (M11.UNDERGO_WRITE='S'  AND  ApplyType  =  'F'))"  +
                     " AND  M11.UNDERGO_WRITE  <>  'X' "  +
                     " AND  M11.BarCode  <>  '" +  stringBarCode  +  "' "  +
                     " AND  M11.ActionNo  =  '" +  stringActionNo  +  "' "  ;
        retDoc3M014  =  exeFun.getTableDataDoc(stringSql) ;
        for(int  intNo=0  ;  intNo<retDoc3M014.length  ;  intNo++) {
            stringProjectID    =  retDoc3M014[intNo][0].trim() ;
            stringProjectID1  =  retDoc3M014[intNo][1].trim() ;
            stringDepartNo   =  retDoc3M014[intNo][3].trim() ;
            if("".equals(stringProjectID)  &&  "".equals(stringProjectID1)) {
                stringKey            =  stringDepartNo ;
            } else {
                stringKey            =  stringProjectID  +  "---"+stringProjectID1 ;
            }
            doubleTemp  =  exeUtil.doParseDouble(""+hashtableUseMoneyDoc3M011.get(stringKey))+exeUtil.doParseDouble(retDoc3M014[intNo][2].trim()) ;
            hashtableUseMoneyDoc3M011.put(stringKey,  convert.FourToFive(""+doubleTemp,  0)) ;
        }
        return  hashtableUseMoneyDoc3M011 ;
    }
    public  String[][]  getDoc8M010(Doc.Doc2M010  exeFun) throws  Throwable {
        String      stringSql         =  "" ;
        String     stringComNo    = getValue("ComNo").trim();
        String     stringDocNo     = getValue("DocNo").trim();
        String     stringBarCode = getValue("BarCode").trim();
        String[][]  retDoc8M010  =  null ;
        // 0  數目      1  AcceptRealDate       2  PickRealDate
        stringSql  =    " SELECT  COUNT(*),   AcceptRealDate,  PickRealDate "  +
                              " FROM  Doc8M010 "  +
                        " WHERE  ComNo  =  '"     +  convert.ToSql(stringComNo)     +  "' "  +
                              " AND  BarCode  =  '"  +  convert.ToSql(stringBarCode)  +  "' "  +
                              " AND  DocNo  =  '"     +  convert.ToSql(stringDocNo)      +  "' "  +
                  " GROUP BY  AcceptRealDate,  PickRealDate " ;
        retDoc8M010  =  exeFun.getTableDataDoc(stringSql) ;
        return  retDoc8M010 ;
    }
    
    
    public  boolean  isTable12CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTabbedPane jtabbedPane1                =  getTabbedPane("Tab1") ;
        int                 intTablePanel                 =  3 ;
        JTable      jtable10              =  getTable("Table10") ;  // 簽核流程 表格
        JTable      jtable12              =  getTable("Table12") ;  // 附件        表格
        String      stringRecordNo        =  "" ;
        String      stringPutDateTime     =  "" ;
        String      stringEmployeeNo        =  "" ;
        String      stringBuildYM           =  "" ;
        String      stringYYYYMM          =  exeUtil.doSubstring(getValue("EDateTime"),  0,  7).replaceAll("/",  "") ;
        String      stringStubPath        =  "" ;
        String      stringDescript            =  "" ;
        String      stringDocumentName    =  "";
        String      stringFunctionName    =  getFunctionName() ;
        Vector      vectorRecordNo          =  new  Vector() ;
        Hashtable  hashtableStubPath      =  new  Hashtable() ;
        //
        System.out.println("isTable12CheckOK------------------------1") ;
        System.out.println("jtable10("+jtable10.getRowCount()+")------------------------2") ;
        System.out.println("jtable12("+jtable12.getRowCount()+")------------------------3") ;
        /*if(jtable10.getRowCount()==0) {
            System.out.println("isTable12CheckOK------------------------4") ;
            return  true ;
        }*/
        // 線上簽核時，附件不可為空白
        if(jtable10.getRowCount()>0  &&  jtable12.getRowCount()==0  &&  stringFunctionName.indexOf("採購")==-1) {
            if(!isTable12NoPageOK(exeUtil,  exeFun)) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                messagebox("線上簽核時，附件不可為空白") ;
                return  false ;
            }
        }
        boolean  booleanFlag  =  false ;
        for(int  intNo=0  ;  intNo<jtable12.getRowCount()  ;  intNo++) {
            stringRecordNo       =  (""+getValueAt("Table12",  intNo,  "RecordNo")).trim() ;
            stringPutDateTime  =  (""+getValueAt("Table12",  intNo,  "PutDateTime")).trim() ;
            stringEmployeeNo   =  (""+getValueAt("Table12",  intNo,  "EmployeeNo")).trim() ;
            stringBuildYM           =  (""+getValueAt("Table12",  intNo,  "BuildYM")).trim() ;
            stringStubPath         =  (""+getValueAt("Table12",  intNo,  "StubPath")).trim() ;
            stringDocumentName=  (""+getValueAt("Table12",  intNo,  "DocumentName")).trim() ;
            stringDescript           =  (""+getValueAt("Table12",  intNo,  "DocumentRemark")).trim() ;
            System.out.println(intNo+"Table12------------------------") ;
            //
            if("".equals(stringPutDateTime)) {
                setValueAt("Table12",  datetime.getTime("YYYY/mm/dd h:m:s"),  intNo,  "PutDateTime") ;
            }
            if("".equals(stringEmployeeNo)) {
                setValueAt("Table12",  getUser(),  intNo,  "EmployeeNo") ;
            }
            if("".equals(stringBuildYM)) {
                setValueAt("Table12",  stringYYYYMM,  intNo,  "BuildYM") ;
            }
            // 序號
            if("".equals(stringRecordNo)) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable12.setRowSelectionInterval(intNo,  intNo) ;
                messagebox("附件表格之第 "+(intNo+1)+" 行之 序號 不可空白。") ;
                return  false ;
            }
            if(vectorRecordNo.indexOf(stringRecordNo)  !=  -1) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable12.setRowSelectionInterval(intNo,  intNo) ;
                messagebox("附件表格之第 "+(intNo+1)+" 行之 序號 重複。") ;
                return  false ;
            }
            vectorRecordNo.add(stringRecordNo) ;
            // 名稱
            /*if("".equals(stringDocumentName)) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable12.setRowSelectionInterval(intNo,  intNo) ;
                messagebox("附件表格之第 "+(intNo+1)+" 行之 名稱 不可空白。") ;
                return  false ;
            }*/
            // 說明
            if("".equals(stringDescript)) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable12.setRowSelectionInterval(intNo,  intNo) ;
                messagebox("附件表格之第 "+(intNo+1)+" 行之 資料名稱 不可空白。") ;
                return  false ;
            }
            // 路徑
            if("".equals(stringStubPath)) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable12.setRowSelectionInterval(intNo,  intNo) ;
                messagebox("附件表格之第 "+(intNo+1)+" 行之 路徑 不可空白。") ;
                return  false ;
            }
            if(stringStubPath.endsWith(".pdf")  ||  stringStubPath.endsWith(".PDF")) {
            } else if(stringStubPath.endsWith(".jpg")  ||  stringStubPath.endsWith(".JPG")) {
            } else if(stringStubPath.endsWith(".jpge")  ||  stringStubPath.endsWith(".JPGE")) {
            } else {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable12.setRowSelectionInterval(intNo,  intNo) ;
                messagebox("附件表格之第 "+(intNo+1)+" 行之 只允許上傳 PDF 及 JPG 檔案。") ;
                return  false ;           
            }
            System.out.println(intNo+"Table12--isUpdateFileOK----------------------") ;
            booleanFlag  =  isUpdateFileOK(intNo,  stringStubPath,  stringYYYYMM,  hashtableStubPath) ;
            if(!booleanFlag) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                jtable12.setRowSelectionInterval(intNo,  intNo) ;
                return  false ;                       
            }
            System.out.println(intNo+"Table12--isUpdateFileOK("+booleanFlag+")----------------------") ;
        }
        return  true ;
    }
    public  boolean  isTable12NoPageOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable    jtable1             =  getTable("Table1") ;
        String    stringApplyType     =  getValue("ApplyType").trim() ;
        String    stringDocNo1      =  getValue("DocNo1").trim() ;
        String    stringCDate           =  exeUtil.getDateConvert(getValue("CDate")) ;
        String    stringCostID          =  "" ;
        String    stringCostID1       =  "" ;
        String    stringSqlAnd      =  " AND  CostIDType  =  'A' "  +
                                " AND  UseType  =  'A' "  +
                                " AND  DeptCd_Doc  LIKE  '"+exeUtil.doSubstring(stringDocNo1,  0,  3) +"%' " +
                                " AND  (UseDateStart  = '9999/99/99' OR  UseDateStart<='"+stringCDate+"') " +
                                " AND  (UseDateEnd  = '9999/99/99'   OR  UseDateEnd>='"+stringCDate+"') " ;
        Vector    vectorDoc5M0183   =  new  Vector() ;
        Vector    vectorCostID          =  new  Vector() ;
        Vector    vectorColumnName    =  new  Vector() ;
        //        
        vectorDoc5M0183  =  exeFun.getQueryDataHashtableDoc("Doc5M0183",  new  Hashtable(),  stringSqlAnd,  vectorColumnName,  exeUtil)  ;
        if(vectorDoc5M0183.size()  ==  0)  return  false ;
        for(int  intNo=0  ;  intNo<vectorDoc5M0183.size()  ;  intNo++) {
            stringCostID    =  exeUtil.getVectorFieldValue(vectorDoc5M0183,  intNo,  "CostID",  vectorColumnName) ;
            stringCostID1   =  exeUtil.getVectorFieldValue(vectorDoc5M0183,  intNo,  "CostID1",  vectorColumnName) ;
            //
            vectorCostID.add(stringCostID+stringCostID1) ;
        }
        //if(!"F".equals(stringApplyType))                return  false ;
        //
        for(int  intNo=0  ;  intNo<jtable1.getRowCount()  ;  intNo++) {
            stringCostID  =  (""+getValueAt("Table1",  intNo,  "CostID")).trim() ;
            stringCostID1  =  (""+getValueAt("Table1",  intNo,  "CostID1")).trim() ;
            //
            if(vectorCostID.indexOf(stringCostID+stringCostID1)==-1)  return  false ;
        }
        return  true ;
    }
    public boolean isUpdateFileOK(int  intRowPos,  String  stringStubPath,  String  stringYYYYMM,  Hashtable  hashtableStubPath)throws  Throwable {
        hashtableStubPath.put(""+intRowPos,  stringStubPath) ;
        // 2017/11/13 修正 檔名無條碼編號問題
        String  stringBarCode       = getValue("BarCode").trim();
        String  stringBarCodeT    =  ""+get("BarCode_Tmp") ;          put("BarCode_Tmp",  "null") ;if(!"null".equals(stringBarCodeT)  &&  "".equals(stringBarCode))  stringBarCode  =  stringBarCodeT ;
        String  stringPutDateTime  =  datetime.getTime("YYYY/mm/dd h:m:s")  ;           stringPutDateTime  =  stringPutDateTime.replaceAll(" ",  "_").replaceAll("/",  "").replaceAll(":",  "") ;
        String  stringFullFileName  =  stringBarCode+"_"+stringPutDateTime+"_" ;
        String  stringFilePath          =  "g:\\02部室區\\0151資訊企劃室\\01 專案彙總區\\系統上傳文件\\Doc\\請購附件\\"+stringYYYYMM+"" ;
        if(stringStubPath.startsWith(stringFilePath)) {
            return  true ;
        }
        getButton("ButtonMkDir").doClick() ;
        //
        String[][]  retTableData  =  getTableData("TableCheck") ;
        if(retTableData.length!=1)              return  false ;
        if(!"OK".equals(retTableData[0][0].trim())) return  false ;
        // 檔名 格式檢核(檔名不可有空白)
        String[]  arrayFile         =  convert.StringToken(stringStubPath,  "\\") ;
        String    stringFileName  =  arrayFile[arrayFile.length-1].trim() ;
        String[]  arrayFileName    =  convert.StringToken(stringFileName,   " ") ;
        if(arrayFileName.length  >  1) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之[上傳檔案]之檔案路徑及檔案名稱 不可有空白。") ;
            return  false ;
        }
        if(stringStubPath.indexOf("%")  !=  -1) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之[上傳檔案]之檔案路徑及檔案名稱 不可有 %。") ;
            return  false ;         
        }
        if(stringStubPath.indexOf("=")  !=  -1) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之[上傳檔案]之檔案路徑及檔案名稱 不可有 等號(＝)。") ;
            return  false ;         
        }
        if(stringStubPath.indexOf(",")  !=  -1) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之[上傳檔案]之檔案路徑及檔案名稱 不可有 逗號(，)。") ;
            return  false ;         
        }
        if(stringStubPath.indexOf("&")  !=  -1) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之[上傳檔案]之檔案路徑及檔案名稱 不可有 &。") ;
            return  false ;         
        }
        if(stringStubPath.indexOf("_")  !=  -1) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之[上傳檔案]之檔案路徑及檔案名稱 不可有 底線(_)。") ;
            return  false ;         
        }
        // 本機檔案存在檢核
        if(!(new  File(stringStubPath)).exists()) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之[上傳檔案] 不存在。") ;
            return  false ;       
        }
        // 上傳檔案
        String  stringFileTarget      =  stringFilePath+"\\"+stringFullFileName+stringFileName ;
        //
        stringStubPath     =  convert.replace(stringStubPath,  "\\",  "/") ;
        stringFileTarget   =  convert.replace(stringFileTarget,  "\\",  "/") ;
        if(!upload(stringStubPath,  stringFileTarget)) {
            doBackStubPath(stringFilePath,  hashtableStubPath) ;
            messagebox("附件表格之第 "+(intRowPos+1)+" 列之上傳檔案失敗("+stringStubPath+")。") ;
            return  false ;
        }
        // 
        stringFileTarget  =  convert.replace(stringFileTarget,  "/",  "\\") ;
        setValueAt("Table12",  stringFileTarget,  intRowPos,  "StubPath") ;
        return  true ;
    }
    public void doBackStubPath(String  stringFilePath,  Hashtable  hashtableStubPath)throws  Throwable {
        JTable  jtable12                =  getTable("Table12") ;
        String   stringStubPathOld  =  "" ;
        for(int  intNo=0  ;  intNo<jtable12.getRowCount( )  ;  intNo++) {
            stringStubPathOld   =  (""+hashtableStubPath.get(""+intNo)).trim() ;    if("null".equals(stringStubPathOld))  continue ;
            //
            setValueAt("Table12",  stringStubPathOld,  intNo,  "StubPath") ;
        }
    }
    public  boolean  isTable18CheckOK(boolean  booleanUndergoWriteCheck,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTabbedPane   jtabbedPane1                       =  getTabbedPane("Tab1") ;
        int                   intTablePanel                       =  2 ;
        JTable        jtable1                       =  getTable("Table1") ;  
        JTable        jtable18                      =  getTable("Table18") ;  
        String        stringFactoryNo               =  "" ;
        String        stringPurchaseMoney           =  "" ;
        String        stringPurchaseMoneyFirst        =  "" ;
        String        stringPurchaseMoneyEnd      =  "" ;
        String        stringNoPageDate            =  ""+get("NO_PAGE_DATE") ;     if(stringNoPageDate.length()  !=  10)   stringNoPageDate  =  "2017/12/31" ;// 請購無紙化上線日期
        String        stringToday                   =  exeUtil.getDateConvert(getValue("CDate")) ;
        String        stringApplyType               =  getValue("ApplyType").trim() ;
        String        stringBarCode               =  getValue("BarCode").trim() ;
        Vector          vectorFactoryNo1            =  new  Vector() ;
        Vector          vectorFactoryNo18             =  new  Vector() ;
        double          doubleTemp                  =  0 ;
        Hashtable   hashtablePurchaseMoney1   =  new  Hashtable() ;
        Hashtable   hashtablePurchaseMoney18    =  new  Hashtable() ;
        //
        if(getFunctionName().indexOf("採購")  == -1)                                              return  true ;
        if(jtable18.getRowCount()==0  &&  (!booleanUndergoWriteCheck  ||  "F".equals(stringApplyType)))   return  true ;
        if(isTable1PurchaseMoney0(exeUtil))  {
            setTableData("Table18",  new  String[0][0]) ;
            return  true ;
        }
        //
        for(int  intNo=0  ;  intNo<jtable18.getRowCount()  ;  intNo++) {
            stringFactoryNo         =  (""+getValueAt("Table18",  intNo,  "FactoryNo")).trim() ;
            stringPurchaseMoneyFirst    =  (""+getValueAt("Table18",  intNo,  "PurchaseMoneyFirst")).trim() ;
            stringPurchaseMoneyEnd    =  (""+getValueAt("Table18",  intNo,  "PurchaseMoneyEnd")).trim() ;
            //
            if(vectorFactoryNo18.indexOf(stringFactoryNo)  !=  -1) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table18",  intNo,  "FactoryNo") ;
                messagebox("廠商議價記錄表格之第 "+(intNo+1)+" 列之[廠商] 重複出現。") ;
                return  false ;
            }
            vectorFactoryNo18.add(stringFactoryNo) ;
            //
            if("".equals(stringFactoryNo)) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table18",  intNo,  "FactoryNo") ;
                messagebox("廠商議價記錄表格之第 "+(intNo+1)+" 列之[廠商] 不可為空白。") ;
                return  false ;
            }
            // 廠商存在檢核
            if(exeFun.getDoc3M015(stringFactoryNo).length  ==  0) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table18",  intNo,  "FactoryNo") ;
                messagebox("廠商議價記錄表格之第 "  +  (intNo+1)  +  " 列 之 [廠商統一編號] 不存在 [請購廠商維護作業(Doc3M015)]。") ;
                return  false ; 
            }
            // 得標價及決標價必須不等於 0
            if(exeUtil.doParseDouble(stringPurchaseMoneyFirst)  ==  0) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table18",  intNo,  "PurchaseMoneyFirst") ;
                messagebox("廠商議價記錄表格之第 "  +  (intNo+1)  +  " 列 之 [得標價] 不允許等於 0。") ;
                return  false ; 
            }
            if(exeUtil.doParseDouble(stringPurchaseMoneyEnd)  ==  0) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                setFocus("Table18",  intNo,  "PurchaseMoneyEnd") ;
                messagebox("廠商議價記錄表格之第 "  +  (intNo+1)  +  " 列 之 [得標價] 不允許等於 0。") ;
                return  false ; 
            }
            hashtablePurchaseMoney18.put(stringFactoryNo,  stringPurchaseMoneyEnd) ;
        }
        for(int  intNo=0  ;  intNo<jtable1.getRowCount()  ;  intNo++) {
            stringFactoryNo         =  (""+getValueAt("Table1",  intNo,  "FactoryNo")).trim() ;
            stringPurchaseMoney     =  (""+getValueAt("Table1",  intNo,  "PurchaseMoney")).trim() ;
            // 得標廠商必須存在於議價廠商中
            if(vectorFactoryNo18.indexOf(stringFactoryNo)  ==  -1)  {
                jtabbedPane1.setSelectedIndex(0) ;
                jtable1.setRowSelectionInterval(intNo,  intNo) ;
                messagebox("請購項目表格之第 "  +  (intNo+1)  +  " 列 之 [廠商] 不存在 [廠商議價記錄] 中。") ;
                return  false ; 
            }
            if(vectorFactoryNo1.indexOf(stringFactoryNo)  ==  -1)  vectorFactoryNo1.add(stringFactoryNo) ;
            //
            doubleTemp  =  exeUtil.doParseDouble(stringPurchaseMoney)  +  exeUtil.doParseDouble(""+hashtablePurchaseMoney1.get(stringFactoryNo)) ;
            hashtablePurchaseMoney1.put(stringFactoryNo,  convert.FourToFive(""+doubleTemp,  0)) ;
        }
        // 得標廠商之決標價須一致請購合約金額 
        /*double  doublePurchaseMoney1    =  0 ;
        double  doublePurchaseMoney18  =  0 ;
        for(int  intNo=0  ;  intNo<vectorFactoryNo1.size()  ;  intNo++) {
            stringFactoryNo       =  ""+vectorFactoryNo1.get(intNo) ;
            doublePurchaseMoney1    =  exeUtil.doParseDouble(convert.FourToFive(""+hashtablePurchaseMoney1.get(stringFactoryNo),  0)) ;
            doublePurchaseMoney18   =  exeUtil.doParseDouble(convert.FourToFive(""+hashtablePurchaseMoney18.get(stringFactoryNo),  0)) ;
            if(doublePurchaseMoney1  !=  doublePurchaseMoney18) {
                jtabbedPane1.setSelectedIndex(intTablePanel) ;
                messagebox("廠商議價記錄表格 與  請購項目表格 之 廠商("+stringFactoryNo+")[得標價] 不一致。") ;
                return  false ;   
            }
        }*/
        return  true ;
    }
    public  boolean  isTable1PurchaseMoney0(FargloryUtil  exeUtil) throws  Throwable {
        JTable  jtable              =  getTable("Table1") ;
        String  stringPurchaseMoney   =  "" ;
        for(int  intNo=0  ;  intNo<jtable.getRowCount()  ;  intNo++) {
            stringPurchaseMoney  =  (""+getValueAt("Table1",  intNo,  "PurchaseMoney")).trim() ;
            //
            if(exeUtil.doParseDouble(stringPurchaseMoney)  !=  0)  return  false ;
        }
        return  true ;
    }
    //
    public String getInformation(){
        return "---------------新增按鈕程式.preProcess()----------------";
    }
}



