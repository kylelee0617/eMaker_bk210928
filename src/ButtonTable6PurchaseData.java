    //if(!"B3018".equals(getUser())) {
    //    getButton("ButtonPurchaseDataOLD").doClick() ;
    //    return  value ;
    //}
    String  stringStatus    =  (""+get("Doc2M010_STATUS")).trim( ) ;
    if(!"".equals(stringStatus)    &&  !"null".equals(stringStatus) &&  !"FormLoad".equals(stringStatus))   return  value  ;
    //
    //
    Doc2M010         exeFun  =  new  Doc2M010( ) ;
    FargloryUtil        exeUtil    =  new  FargloryUtil() ;
    //
    System.out.println("------------------------請購單 S") ;
    Hashtable  hashtablePaidUpMoney   =  new  Hashtable() ;
    Vector        vectorBarCodePur        =  new  Vector() ;
    Vector        vectorPayCondition        =  new  Vector() ;
    System.out.println("isTable6DataOK------------------------S") ;
    String[]      arrayMessage            =  isTable6DataOK(exeUtil,  exeFun,  hashtablePaidUpMoney,  vectorPayCondition) ;
    System.out.println("isTable6DataOK("+hashtablePaidUpMoney.get("023510306004")+")------------------------E("+vectorPayCondition.size()+")") ;
    String          stringErrorMessage         =  arrayMessage[arrayMessage.length-1] ;
    boolean      booleanError                    =  false ;
    boolean      booleanNew                     =  getNewVersion (exeFun) ;   
    //
    
    if("ERROR".equals(arrayMessage[0].trim())) {
        booleanError            =  true ;
        setTableData("Table8",  new  String[0][0]) ;
        //
        doDescriptAndStatusControl (vectorBarCodePur,  booleanError,  booleanNew,  exeUtil,  exeFun) ;
        //
        if(!"".equals(stringErrorMessage))messagebox(stringErrorMessage) ;
        return  value ;
    }
    doSetTable8Data (hashtablePaidUpMoney,  arrayMessage,  exeUtil,  exeFun,  vectorBarCodePur) ;
    
    System.out.println("doSetFactoryNoData()-------------------------------------------S") ;
    doSetFactoryNoData (exeUtil,  exeFun) ;
    System.out.println("doSetFactoryNoData-------------------------------------------E") ;
    
    doSetPayConditionData (vectorPayCondition,   exeUtil,  exeFun) ;
    
    System.out.println("doSetTable6Data-------------------------------------------S") ;
    doSetTable6Data (exeUtil,  exeFun) ;
    System.out.println("doSetTable6Data-------------------------------------------E") ;
    
    doDescriptAndStatusControl (vectorBarCodePur,  booleanError,  booleanNew,  exeUtil,  exeFun) ;
    //
    getButton("ButtonTable7Status").doClick() ;
    return value;
}
public  String[]  isTable6DataOK (FargloryUtil  exeUtil,  Doc2M010  exeFun,  Hashtable  hashtablePaidUpMoney,  Vector  vectorPayCondition) throws  Throwable{
    JTable        jtable6                                   =  getTable("Table6") ;
    JTable        jtable9                                   =  getTable("Table9") ;
    String        stringBarCodePur                  =  "" ;
    String        stringComNo                           =  getValue("ComNo").trim( ) ;
    String        stringCDate                             =  getValue("CDate").trim() ;
    String        stringPurchaseNo1                 =  "" ;
    String        stringPurchaseNo2                 =  "" ;
    String        stringPurchaseNo3                 =  "" ;
    String        stringFactoryNo                   =  "" ;
    String        stringFactoryNoL                  =  "" ;
    String        stringFactoryNoTable            =  "NONE" ;
    String        stringPrimaryKey                    =  "" ;
    String        stringErrorMessage                =  "" ;
    String        stringGroupID                           =  "" ;
    String        stringGroupIDL                        =  "" ;
    String        stringExistDate                         =  "" ;
    String        stringPurchaseMoneyL            =  "" ;
    String        stringContractMoneyL              =  "" ;
    String        stringNoUseRealMoneyL           =  "" ;
    String        stringGroupNameL                =  "" ;
    String        stringPayCondition1L              =  "" ;
    String        stringPayCondition2L              =  "" ;
    String        stringApplyType                     =  "" ;
    String        stringUnipurchase                     =  "" ;
    String        stringUnipurchaseOld              =  "" ;
    String       stringSqlAnd                 =   " AND  ((ApplyType  =  'F' AND UNDERGO_WRITE  IN ('S','H')  AND  CDate  <  '098/12/01')  OR "  +
                                              " UNDERGO_WRITE  =  'Y')" ;
    String[]      arrayMessage                          =  new  String[jtable6.getRowCount()+2] ;
    String[]    arrayGroupName            =  null ;
    boolean     booleanFlag                             =  true ;
    boolean      booleanSpecPurchaseNo      =  false ;
    Hashtable   hashtableAnd                    =  new  Hashtable() ;
    Hashtable   hashtableDoc3M011             =  null ;
    Hashtable   hashtableDoc3M013             =  null ;
    Vector          vectorDoc3M011            =  null ;
    Vector          vectorDoc3M013            =  null ;
    Vector        vectorPrimaryKey                  =  new  Vector( ) ;
    double         doublePaidUpMoney              =  0 ;
    double         doubleThisPurchaseMoney    =  0 ;
    double         doublePurchaseMoneyL        =  0 ;
    double         doubleTemp                   =  0 ;
    //
    for(int  intNo=0  ;  intNo<arrayMessage.length  ;  intNo++) arrayMessage[intNo] = "" ;
    //
    for(int  intNo=0  ;  intNo<jtable6.getRowCount( )  ;  intNo++) {
        stringPurchaseNo1               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo1")).trim( ) ;
        stringPurchaseNo2               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo2")).trim( ) ;
        stringPurchaseNo3               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo3")).trim( ) ;
        stringFactoryNo                    =  (""+getValueAt("Table6",  intNo,  "FactoryNo")).trim( ) ;
        stringPrimaryKey                  =  stringPurchaseNo1  +  "-"  +  stringPurchaseNo2  +  "-"  +  stringPurchaseNo3 ;
        //
        if("".equals(stringFactoryNo)) {
            if("NONE".equals(stringFactoryNoTable)) {
                for(int  intNoL=0  ;  intNoL<jtable6.getRowCount( )  ;  intNoL++) {
                    stringFactoryNo  =  (""+getValueAt("Table6",  intNoL,  "FactoryNo")).trim( ) ;
                    if(!"".equals(stringFactoryNo)) {
                        stringFactoryNoTable  =  stringFactoryNo ;
                        break ;
                    }
                }
            } else {
                stringFactoryNo  =  stringFactoryNoTable ;
                setValueAt("Table6",  stringFactoryNo,  intNo,  "FactoryNo") ;
            }
        }
        //
        if(exeUtil.doParseInteger(stringPurchaseNo2)  ==  0) {
            if(!"".equals(stringErrorMessage)) stringErrorMessage  +=  "\n" ;
            stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列 [請購單號之請購年月] 格式錯誤(yymm)。" ;
            //
            arrayMessage[0]           =  "ERROR" ;
            arrayMessage[intNo+1]    =  "ERROR" ;
            continue ;
        }
        if(vectorPrimaryKey.indexOf(stringPrimaryKey)  !=  -1) {
            if(!"".equals(stringErrorMessage)) stringErrorMessage  +=  "\n" ;
            stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列 [請購單號] 資料重覆。" ;
            //
            arrayMessage[0]           =  "ERROR" ;
            arrayMessage[intNo+1]    =  "ERROR" ;
            continue ;
        }
        vectorPrimaryKey.add(stringPrimaryKey) ;
        //
        booleanFlag           =  !"".equals(stringPurchaseNo1)  &&
                           !"".equals(stringPurchaseNo2)  &&
                           !"".equals(stringPurchaseNo3)  &&
                           !"".equals(stringComNo) ;
        if(!booleanFlag) {    
            if(!"".equals(stringErrorMessage)) stringErrorMessage  +=  "\n" ;
            stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列之 [請購單號] 為空白。";
            //
            arrayMessage[0]           =  "ERROR" ;
            arrayMessage[intNo+1]    =  "ERROR" ;           
            continue ;
        }
        hashtableAnd.put("ComNo",     stringComNo) ;
        hashtableAnd.put("DocNo1",    stringPurchaseNo1) ;
        hashtableAnd.put("DocNo2",    stringPurchaseNo2) ;
        hashtableAnd.put("DocNo3",    stringPurchaseNo3) ;
        vectorDoc3M011  =  exeFun.getQueryDataHashtableDoc("Doc3M011",  hashtableAnd,  stringSqlAnd,  new  Vector(),  exeUtil) ;
        if(vectorDoc3M011.size()  ==  0) {
            hashtableAnd.put("ComNo",     stringComNo) ;
            hashtableAnd.put("DocNo1",    stringPurchaseNo1) ;
            hashtableAnd.put("DocNo2",    stringPurchaseNo2) ;
            hashtableAnd.put("DocNo3",    stringPurchaseNo3) ;
            vectorDoc3M011  =  exeFun.getQueryDataHashtableDoc("Doc3M011",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
            //
            if(!"".equals(stringErrorMessage)) stringErrorMessage  +=  "\n" ;
            if(vectorDoc3M011.size()  ==  0) {
                stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列之 [請購單號] 不存在。"  ;                 
            } else {
                       hashtableDoc3M011  =  (Hashtable)  vectorDoc3M011.get(0) ;
                String  stringUnderGoWrite  =  (""+hashtableDoc3M011.get("UNDERGO_WRITE")) ;
                       stringUnderGoWrite  =  exeFun.getPurchseUndergoWriteName(stringUnderGoWrite) ;
                if("業管".equals(stringUnderGoWrite)) {
                    stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列之 [請購單號] 處於 ["+stringUnderGoWrite+"] 狀態，尚未完成請購流程，請通知 採購室 完成請購流程。" ;
                } else {
                    stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列之 [請購單號] 處於 ["+stringUnderGoWrite+"] 狀態，尚未完成請購流程，請通知 行銷管理室 完成請購流程。" ;                   
                }
            }
            //
            arrayMessage[0]           =  "ERROR" ;
            arrayMessage[intNo+1]    =  "ERROR" ;
            continue ;
        }
        hashtableDoc3M011  =  (Hashtable)  vectorDoc3M011.get(0) ;
        stringBarCodePur       =  (""+hashtableDoc3M011.get("BarCode")).trim() ;
        stringExistDate            =  (""+hashtableDoc3M011.get("ExistDate")).trim() ;
        stringApplyType          =  (""+hashtableDoc3M011.get("ApplyType")).trim() ;
        // 檢核 統購一致
        stringUnipurchase  =  ("F".equals(stringApplyType))  ?  "Y" :  "N" ;
        if(!"".equals(stringUnipurchaseOld)  &&  !stringUnipurchaseOld.equals(stringUnipurchase)) {
            if(!"".equals(stringErrorMessage))  stringErrorMessage  +=  "\n" ;
            stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列 [請購單號] 的 [統購] 不一致。"  ;
            arrayMessage[0]           =  "ERROR" ;
            arrayMessage[intNo+1]    =  "ERROR" ;
            continue ;
        }
        stringUnipurchaseOld  =  stringUnipurchase ;
        //
        hashtableAnd.put("BarCode",  stringBarCodePur) ;
        if(!"".equals(stringFactoryNo)) hashtableAnd.put("FactoryNo",  stringFactoryNo) ;
        vectorDoc3M013  =  exeFun.getQueryDataHashtableDoc("Doc3M013",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
        if(vectorDoc3M013.size()  ==  0) {
            if(!"".equals(stringErrorMessage))  stringErrorMessage  +=  "\n";
            stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列之 [請購單號] 不存在此廠商("+stringFactoryNo+")。";
            arrayMessage[0]           =  "ERROR" ;
            arrayMessage[intNo+1]    =  "ERROR" ;
            continue ;
        }
        if("".equals(stringFactoryNo)) {
            hashtableDoc3M013     =  (Hashtable)vectorDoc3M013.get(0) ;
            stringFactoryNo            =  ""+hashtableDoc3M013.get("FactoryNo") ;
            setValueAt("Table6",  stringFactoryNo,  intNo,  "FactoryNo") ;
        }
        // 付款條件
        stringGroupID                     =  getGroupID(stringBarCodePur,  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  exeUtil,  exeFun) ;  
        System.out.println("isSpectPurchaseNo-------------------------------------------------------S") ;
        booleanSpecPurchaseNo   =  exeFun.isSpectPurchaseNo (stringComNo,  "17",  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  " AND  M13.GroupName  LIKE  '%#-#B' ") ;
        System.out.println("getPaidUpMoney----booleanSpecPurchaseNo("+booleanSpecPurchaseNo+")-------------------------------------------------------S") ;
        doublePaidUpMoney          =  getPaidUpMoney(stringPurchaseNo1,       stringPurchaseNo2,    stringPurchaseNo3,  
                                                   stringFactoryNo,               stringBarCodePur,     stringGroupID,
                                               booleanSpecPurchaseNo,  exeUtil,             exeFun,
                                               hashtablePaidUpMoney) ;
        System.out.println("getPaidUpMoney("+doublePaidUpMoney+")-----------------------------------------------------------E") ;
        doubleThisPurchaseMoney  =  exeFun.doParseDouble((""+getValueAt("Table6",  intNo,  "PurchaseMoney")).trim( )) ;
        doublePurchaseMoneyL      =  0 ;
        //booleanFlag                         =  false ;
        for(int  intNoL=0  ;  intNoL<vectorDoc3M013.size()  ;  intNoL++) {
            hashtableDoc3M013         =  (Hashtable)vectorDoc3M013.get(intNoL) ;
            stringPurchaseMoneyL      =  ""+hashtableDoc3M013.get("PurchaseMoney") ;
            stringContractMoneyL        =  ""+hashtableDoc3M013.get("PurchaseSumMoney") ;
            stringFactoryNoL              =  (""+hashtableDoc3M013.get("FactoryNo")).trim() ;
            stringNoUseRealMoneyL     =  (""+hashtableDoc3M013.get("NoUseRealMoney")).trim() ;
            stringGroupNameL        =  (""+hashtableDoc3M013.get("GroupName")).trim() ;
            stringGroupIDL            =  (""+hashtableDoc3M013.get("GroupID")).trim() ;
            stringPayCondition1L      =  (""+hashtableDoc3M013.get("PayCondition1")).trim() ;
            stringPayCondition2L      =  (""+hashtableDoc3M013.get("PayCondition2")).trim() ;
            //
            arrayGroupName  =  convert.StringToken(stringGroupNameL,  "#-#") ;
            //
            if(arrayGroupName.length==4  &&  jtable9.getRowCount()>0) {
                // 新版，以 GroupID 作分類
                System.out.println(intNoL+"付款條件 新版-----------------------------------------------------------") ;
                if(!stringGroupID.equals(stringGroupIDL))  continue ;
            } else {
                // 以廠商作區分
                System.out.println(intNoL+"付款條件 舊版-----------------------------------------------------------") ;
                if(!stringFactoryNo.equals(stringFactoryNoL))  continue ; 
            }
            System.out.println(intNoL+"付款條件-----------------------------------------------------------OK") ;
            hashtablePaidUpMoney.put(stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3+"%-%B",  stringContractMoneyL) ;
            //
            if(!"".equals(stringExistDate)  &&  stringCDate.compareTo(stringExistDate)>=0) {
                doubleTemp  =  exeUtil.doParseDouble(""+hashtablePaidUpMoney.get(stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3))   +  exeUtil.doParseDouble(stringNoUseRealMoneyL) ;
                hashtablePaidUpMoney.put(stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  convert.FourToFive(""+doubleTemp,  0)) ;
            }
            //
            doublePurchaseMoneyL  +=  exeUtil.doParseDouble(stringPurchaseMoneyL) ;
            // 已動支 < 累計，符合本次之付款條件
            System.out.println(intNoL+"付款條件 OK ("+(doublePaidUpMoney  <  doublePurchaseMoneyL)+")doublePaidUpMoney("+doublePaidUpMoney+")doublePurchaseMoneyL("+doublePurchaseMoneyL+")stringPayCondition1L("+stringPayCondition1L+")stringPayCondition2L("+stringPayCondition2L+")-----------------------------------------------------------2") ;
            if(doublePaidUpMoney  <  doublePurchaseMoneyL) {
                // 增加
                if(!"999".equals(stringPayCondition1L)) {
                    if(vectorPayCondition.indexOf(stringPayCondition1L)==-1)  vectorPayCondition.add(stringPayCondition1L) ;
                }
                if(!"999".equals(stringPayCondition2L)) {
                    if(vectorPayCondition.indexOf(stringPayCondition2L)==-1)  vectorPayCondition.add(stringPayCondition2L) ;
                }
                if(vectorPayCondition.size()  >  2) {
                    if(!"".equals(stringErrorMessage))  stringErrorMessage  +=  "\n";
                    stringErrorMessage  +=  "第 "  +(intNo+1)  +  " 列之 [付款條件] 不一致。";
                    //
                    arrayMessage[0]           =  "ERROR" ;
                    arrayMessage[intNo+1]    =  "ERROR" ;
                    booleanFlag                    =  true ;
                    break  ;
                }
            }
            // 本次動支 + 已動支 < 累計，跳開
            System.out.println(intNoL+"付款條件doubleThisPurchaseMoney("+doubleThisPurchaseMoney+")+doublePaidUpMoney("+doublePaidUpMoney+")<doublePurchaseMoneyL("+doublePurchaseMoneyL+") ("+(doubleThisPurchaseMoney+doublePaidUpMoney  >  doublePurchaseMoneyL)+")-----------------------------------------------------------1") ;
            if(doubleThisPurchaseMoney+doublePaidUpMoney  <=  doublePurchaseMoneyL) {
                    break ;
            }
        }
    }
    arrayMessage[arrayMessage.length-1]  =  stringErrorMessage ;
    return  arrayMessage ;
}
public  void  doSetTable8Data (Hashtable  hashtablePaidUpMoney,  String[]  arrayMessage,  FargloryUtil  exeUtil,  Doc2M010  exeFun,  Vector  vectorBarCodePur) throws  Throwable{
    JTable        jtable6                                     =  getTable("Table6") ;
    int               intSelectRow                          =  jtable6.getSelectedRow( ) ;
    String        stringComNo                             =  getValue("ComNo").trim( ) ;
    String        stringBarCode                         =  getValue("BarCode").trim( ) ;
    String        stringBarCodePur                  =  "" ;
    String        stringPurchaseNo1                 =  "" ;
    String        stringPurchaseNo2                 =  "" ;
    String        stringPurchaseNo3                 =  "" ;
    String        stringFactoryNo                        =  "" ;
    String        stringStatus                              =  "" ;
    String         stringContractMoney                =  "" ;
    String         stringPaidUpMoney                  =  "" ;
    String         stringNoPayMoney                   =  "" ;
    String         stringExistRealMoney                =  "" ;
    String[]    retTempTableData            =  null ;
    Hashtable   hashtableAnd                    =  new  Hashtable() ;
    Hashtable   hashtableDoc3M011             =  null ;
    Hashtable   hashtableDoc3M013             =  null ;
    Vector         vectorDoc3M011           =  null ;
    Vector         vectorDoc3M013           =  null ;
    Vector        vectorTableData            =  new  Vector() ;
    
    for(int  intNo=0  ;  intNo<jtable6.getRowCount( )  ;  intNo++) {
        stringPurchaseNo1               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo1")).trim( ) ;
        stringPurchaseNo2               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo2")).trim( ) ;
        stringPurchaseNo3               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo3")).trim( ) ;
        stringFactoryNo                    =  (""+getValueAt("Table6",  intNo,  "FactoryNo")).trim( ) ;
        stringStatus                          =  arrayMessage[intNo+1] ;
        retTempTableData               =  new  String[4] ;
        //
        stringPurchaseNo2  =  ""+exeUtil.doParseInteger(stringPurchaseNo2) ;
        //
        if("ERROR".equals(stringStatus)) {
            vectorTableData.add(retTempTableData) ;
            continue ;
        }
        // Doc3M011
        hashtableAnd.put("ComNo",     stringComNo) ;
        hashtableAnd.put("DocNo1",    stringPurchaseNo1) ;
        hashtableAnd.put("DocNo2",    stringPurchaseNo2) ;
        hashtableAnd.put("DocNo3",    stringPurchaseNo3) ;
        vectorDoc3M011      =  exeFun.getQueryDataHashtableDoc("Doc3M011",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
        hashtableDoc3M011   =  (Hashtable)  vectorDoc3M011.get(0) ;
        stringBarCodePur        =  (""+hashtableDoc3M011.get("BarCode")).trim() ;
        //
        if(vectorBarCodePur.indexOf(stringBarCodePur)  ==  -1)  vectorBarCodePur.add(stringBarCodePur) ;
        // Doc3M013
        hashtableAnd.put("BarCode",  stringBarCodePur) ;
        hashtableAnd.put("FactoryNo",  stringFactoryNo) ;
        vectorDoc3M013  =  exeFun.getQueryDataHashtableDoc("Doc3M013",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
        //已付金額
        stringPaidUpMoney    =  ""+exeUtil.doParseDouble(""+hashtablePaidUpMoney.get(stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3)) ;
        stringPaidUpMoney        =  convert.FourToFive(stringPaidUpMoney,  0) ;
        System.out.println("doSetTable8Data----stringPaidUpMoney("+stringPaidUpMoney+")-------------------------------") ;
        // 合約金額
        stringContractMoney  =  ""+exeUtil.doParseDouble(""+hashtablePaidUpMoney.get(stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3+"%-%B")) ;
        stringContractMoney  =  convert.FourToFive(stringContractMoney,  0) ;
        // 未付金額
        stringNoPayMoney  =  operation.floatSubtract(stringContractMoney,  stringPaidUpMoney,  0) ;
        stringNoPayMoney  =  convert.FourToFive(stringNoPayMoney,  0) ;
        // 已請款未付金額
        stringExistRealMoney     =  ""+exeFun.getUseMoney(stringBarCode,         stringComNo,           stringPurchaseNo1,  stringPurchaseNo2,  
                                                    stringPurchaseNo3,  "",                  stringFactoryNo,       true)  ; // 已請款金額               
        //
        retTempTableData[0]  =  stringContractMoney ;
        retTempTableData[1]  =  stringPaidUpMoney ;   // 已付金額
        retTempTableData[2]  =  stringNoPayMoney ;    
        retTempTableData[3]  =  stringExistRealMoney ; 
        vectorTableData.add(retTempTableData) ;
    }
    setTableData("Table8",  (String[][])  vectorTableData.toArray(new  String[0][0])) ;
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
public  void  doSetFactoryNoData (FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
    String    stringFactoryNo             =  (""+getValueAt("Table6",  0,  "FactoryNo")).trim().toUpperCase() ;
    String    stringFactoryName         =  exeFun.getFactoryName( stringFactoryNo ) ;
    String     stringVoucherFlowNoLable  =  getButton("VoucherFlowNoButton").getLabel() ;  

    if(!"".equals(stringVoucherFlowNoLable)  &&  !"B3018".equals(getUser()))  return  ;
    if("".equals(stringFactoryNo))            return ;
    //
    if("".equals(stringFactoryName) &&  !"".equals(stringFactoryNo)) {
        messagebox("此 [廠商]("+stringFactoryNo+") 為 新建廠商 尚不允許使用，請通知採購室，完成廠商確認流程。") ;
    }
    //
    JTable     jtable                               =  null ;
    String     stringTable                        =  "" ;
    
    String[]   arrayTable                         =  {"Table1",  "Table3"} ;
    for(int  intNo=0  ;  intNo<arrayTable.length  ;  intNo++) {
        stringTable  =  arrayTable[intNo].trim() ;
        jtable           =  getTable(stringTable) ;
        System.out.println((intNo+1)+"Table("+stringTable+")---------------------------------------------------") ;
        for(int  intNoL=0  ;  intNoL<jtable.getRowCount()  ;  intNoL++) {
            setValueAt(stringTable,  stringFactoryNo,  intNoL,  "FactoryNo") ;
        }
        jtable.updateUI() ;
    }
}
public  void  doSetPayConditionData (Vector  vectorPayCondition,   FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
    String  stringPurchaseNoExist  =  getValue("PurchaseNoExist").trim() ;
    if(!"Y".equals(stringPurchaseNoExist)) {
        setEditable("PayCondition1",  true) ;
        setEditable("PayCondition2",  true) ;
        return  ;
    }
    setEditable("PayCondition1",  false) ;
    setEditable("PayCondition2",  false) ;
    //
    if(vectorPayCondition.size()  ==  0) return ;
    //
    String    stringPayCondition1  =  (""+vectorPayCondition.get(0)).trim() ;
    String    stringPayCondition2  =  (vectorPayCondition.size()  ==  2)  ?  (""+vectorPayCondition.get(1)).trim()  :  "999" ;
    //
    setValue("PayCondition1",  stringPayCondition1) ;
    setValue("PayCondition2",  stringPayCondition2) ;
}
public  void  doSetTable6Data (FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
    JTable     jtable6                         =  getTable("Table6") ;
    JTable     jtable8                         =  getTable("Table8") ;
    JTable     jtable9                         =  getTable("Table9") ;
    int          intSelectRow                 =  jtable6.getSelectedRow( ) ;
    int          intRowCount6                =  jtable6.getRowCount( ) ;
    int          intRowCount8                =  jtable8.getRowCount( ) ;
    int          intRowCount9                =  jtable9.getRowCount( ) ;
    //
    if(intRowCount6  !=  intRowCount8) return ;
    if(intSelectRow  !=  -1)    jtable8.setRowSelectionInterval(intSelectRow,  intSelectRow) ;
    //
    if(intRowCount9  >  0)             return ;
    //
    double    doubleExistRealMoney  =  0 ;
    double    doublePurchaseMoney  =  0 ;
    String      stringComNo           =  getValue("ComNo").trim() ;
    String      stringFactoryNo             =  "" ;
    String      stringPurchaseNo1        =  "" ;
    String      stringPurchaseNo2        =  "" ;
    String      stringPurchaseNo3        =  "" ;
    String      stringPurchaseNo          =  "" ;
    String      stringPurchaseMoney    =  "" ;
    String[][]  retDoc3M013           =  null ;
    for(int  intNo=0  ;  intNo<intRowCount6  ;  intNo++) {
        doubleExistRealMoney   =  exeUtil.doParseDouble((""+getValueAt("Table8",  intNo,  "PaidUpMoney")).trim( )) ;
        stringPurchaseNo1         =  (""+getValueAt("Table6",  intNo,  "PurchaseNo1")).trim( ) ;
        stringPurchaseNo2         =  (""+getValueAt("Table6",  intNo,  "PurchaseNo2")).trim( ) ;
        stringPurchaseNo3         =  (""+getValueAt("Table6",  intNo,  "PurchaseNo3")).trim( ) ;
        stringPurchaseMoney     =  (""+getValueAt("Table6",  intNo,  "PurchaseMoney")).trim( ) ;
        stringFactoryNo             =  (""+getValueAt("Table6",  intNo,  "FactoryNo")).trim( ) ;
        stringPurchaseNo           =   stringPurchaseNo1 +  stringPurchaseNo2  +  stringPurchaseNo3 ;
        //
        retDoc3M013                =  exeFun.getDoc3M013(stringComNo,  stringPurchaseNo,  stringFactoryNo) ;
        doublePurchaseMoney =  0 ;
        for(int  intNoL=0  ;  intNoL<retDoc3M013.length  ;  intNoL++) {
            doublePurchaseMoney  +=  exeUtil.doParseDouble(retDoc3M013[intNoL][4].trim()) ;
            if(doublePurchaseMoney  >  doubleExistRealMoney) break ;
        }
        //
        /*if(POSITION==1  && exeUtil.doParseDouble(stringPurchaseMoney)  ==  0) {
            stringPurchaseMoney  =  convert.FourToFive(""+(doublePurchaseMoney  -  doubleExistRealMoney),  0) ;
            jtable6.setValueAt(stringPurchaseMoney,  intNo,   9) ;
        }*/
    }
    jtable6.updateUI() ;
}
public  void  doDescriptAndStatusControl (Vector  vectorBarCodePur,  boolean  booleanError,  boolean  booleanNew,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
  System.out.println("doDesc: >>>vectorBarCodePur" + vectorBarCodePur);
  System.out.println("doDesc: >>>booleanError" +booleanError );
  System.out.println("doDesc: >>>booleanNew" + booleanNew);
  
    boolean  booleanAsset            =  false ;
    String      stringFactoryNo             =  (""+getValueAt("Table6",  0,  "FactoryNo")).trim( ) ;
    if(booleanError) {
        booleanAsset  =  false ;
    } else if(booleanNew)  {
        booleanAsset  =  true ;
    } else {
        booleanAsset  =  isAsset (exeUtil,  exeFun) ;
    }
    setValue("Table6Edit",  booleanAsset?"Y":"N") ;
    //
    JTable        jtable6                                   =  getTable("Table6") ;
    for(int  intNo=0  ;  intNo<jtable6.getRowCount( )  ;  intNo++) {
        setEditable("Table6",  intNo,  "PurchaseMoney",  !booleanAsset) ;
    }
    //
    if(booleanError) return ;
    //
    if(getTableData("Table9").length  ==  0) {
        doSetDescriptOldVersion (stringFactoryNo,  vectorBarCodePur,  exeUtil,  exeFun) ;
    } else {
        // 新版
        doSetDescriptNewVersion (exeUtil,  exeFun) ;
    }
}
public  boolean  isAsset (FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
    JTable        jtable6                                     =  getTable("Table6") ;
    String        stringApplyType                     =  "" ;
    String        stringBarCodePur                    =  "" ;
    String        stringBarCode                         =  getValue("BarCodeOld").trim( ) ;
    String        stringComNo                             =  getValue("ComNo").trim( ) ;
    String        stringPurchaseNo1                 =  "" ;
    String        stringPurchaseNo2                 =  "" ;
    String        stringPurchaseNo3                 =  "" ;
    String        stringSql                         =  "" ;
    String[][]      retDoc2M017             =  null ;
    String[][]      retDoc2M0171              =  null ;
    Vector         vectorDoc3M011           =  null ;
    Vector         vectorDoc3M012           =  null ;
    Hashtable   hashtableDoc3M011             =  null ;
    Hashtable   hashtableAnd                    =  new  Hashtable() ;
    for(int  intNo=0  ;  intNo<jtable6.getRowCount( )  ;  intNo++) {
        stringPurchaseNo1               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo1")).trim( ) ;
        stringPurchaseNo2               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo2")).trim( ) ;
        stringPurchaseNo3               =  (""+getValueAt("Table6",  intNo,  "PurchaseNo3")).trim( ) ;
        //
        hashtableAnd.put("ComNo",     stringComNo) ;
        hashtableAnd.put("DocNo1",    stringPurchaseNo1) ;
        hashtableAnd.put("DocNo2",    stringPurchaseNo2) ;
        hashtableAnd.put("DocNo3",    stringPurchaseNo3) ;
        vectorDoc3M011      =  exeFun.getQueryDataHashtableDoc("Doc3M011",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
        hashtableDoc3M011   =  (Hashtable)  vectorDoc3M011.get(0) ;
        stringApplyType          =  (""+hashtableDoc3M011.get("ApplyType")).trim() ;
        stringBarCodePur       =  (""+hashtableDoc3M011.get("BarCode")).trim() ;
        //
        if(!"D".equals(stringApplyType)) {// ApplyType
            // 不是固資，且不是固資特定請款代碼時，必定不是固資
            hashtableAnd.put("BarCode",     stringBarCodePur) ;
            vectorDoc3M012      =  exeFun.getQueryDataHashtableDoc("Doc3M012",  hashtableAnd,  " AND  RTRIM(CostID)+RTRIM(CostID1)  IN ('704','395','396','392')  ",  new  Vector(),  exeUtil) ;        
            if(vectorDoc3M012.size()  ==  0)  return  false ;
        }
        // 不存在 Doc2M0171 ，且已請款，必定不是固定資產
        stringSql    =  " SELECT  M17.BarCode " +
                    " FROM  Doc2M0171 M17,  Doc2M010  M10 "  +
                    " WHERE  M17.BarCode  =  M10.BarCode "  +
                      " AND  M17.PurchaseNo  =  '"  +stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3+"' "  +
                      " AND  M10.ComNo  =  '"           +stringComNo       +"' "  ;
        retDoc2M0171  =  exeFun.getTableDataDoc(stringSql) ;
        if(retDoc2M0171.length  ==  0) {
              // 未請過款
              stringSql    =  " SELECT  M17.BarCode " +
                          " FROM  Doc2M017 M17,  Doc2M010  M10 "  +
                           " WHERE  M17.BarCode  =  M10.BarCode "  +
                             " AND  M10.ComNo  =  '"          +stringComNo              +"' "  +
                             " AND  M17.PurchaseNo1  =  '" +stringPurchaseNo1  +"' "  +
                             " AND  M17.PurchaseNo2  =  '" +stringPurchaseNo2 +"' "  +
                              " AND  M17.PurchaseNo3  =  '" +stringPurchaseNo3 +"' "  +
                              " AND  M10.BarCode  <>  '"       +stringBarCode         +"' "  ;
              retDoc2M017  =  exeFun.getTableDataDoc(stringSql) ;
              if(retDoc2M017.length  >  0) {
                  return  false ;
              }
        }
        //
    }
    return  true ;
}
public  void  doSetDescriptNewVersion (FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
    JTable        jtable9                          =  getTable("Table9") ;
    String      stringDescript          =  getValue("Descript").trim( ) ;  if(!"".equals(stringDescript))  return  ;
    String      stringComNo         =  getValue("ComNo").trim() ;
    String      stringKindNoPur       =  getValue("KindNo").trim() ;
    String      stringPurchaseNo      =  "" ;
    String      stringClassName         =  "" ;
    String      stringRecordNo12      =  "" ;
    String      stringBarCodePur      =  "" ;
    Vector        vectorDoc3M011        =  null ;
    Hashtable  hashtableAnd           =  new  Hashtable() ;
    Hashtable  hashtableDoc3M011    =  new  Hashtable() ;
    Hashtable  hashtableDoc3M012    =  new  Hashtable() ;
    //
    if("24".equals(stringKindNoPur))  stringKindNoPur  =  "17" ;
    if("23".equals(stringKindNoPur))  stringKindNoPur  =  "15" ;
    for(int  intNo=0  ;  intNo<jtable9.getRowCount()  ;  intNo++) {
        stringPurchaseNo   =  (""+getValueAt("Table9",  intNo,  "PurchaseNo")).trim() ;
        stringRecordNo12  =  (""+getValueAt("Table9",  intNo,  "RecordNo12")).trim() ;
        //
        hashtableAnd.put("ComNo",  stringComNo) ;
        hashtableAnd.put("KindNo",  stringKindNoPur) ;
        hashtableAnd.put("DocNo",  stringPurchaseNo) ;
        stringBarCodePur  =  exeFun.getNameUnionDoc("BarCode",  "Doc3M011",  "",  hashtableAnd,  exeUtil) ;
        //
        if("".equals(stringBarCodePur))  continue ;
        //
        hashtableAnd.put("BarCode",       stringBarCodePur) ;
        hashtableAnd.put("RecordNo",      stringRecordNo12) ;
        stringClassName  =  exeFun.getNameUnionDoc("ClassName",  "Doc3M012",  "",  hashtableAnd,  exeUtil) ;
        if("".equals(stringClassName))  continue ;
        //
        if(!"".equals(stringDescript))  stringDescript  +=  "\n" ;
        stringDescript  +=  stringClassName ;
    }
    setValue("Descript",  stringDescript) ;
}
public  void  doSetDescriptOldVersion (String  stringFactoryNo,  Vector  vectorBarCodePur,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable{
    String      stringDescript          =  getValue("Descript").trim( ) ;  if(POSITION  !=  1)  return  ;
    String      stringBarCodePur      =  "" ;
    String      stringClassName         =  "" ;
    Vector        vectorDoc3M012        =  null ;
    Vector        vectorClassName       =  new  Vector() ;
    Vector        vectorClassNameSum  =  new  Vector() ;
    Hashtable  hashtableAnd           =  new  Hashtable() ;
    Hashtable  hashtableDoc3M012    =  new  Hashtable() ;
    for(int  intNo=0  ;  intNo<vectorBarCodePur.size()  ;  intNo++) {
        stringBarCodePur  =  ""+vectorBarCodePur.get(intNo) ;
        //
        hashtableAnd.put("FactoryNo",  stringFactoryNo) ;
        hashtableAnd.put("BarCode",  stringBarCodePur) ;
        vectorDoc3M012  =  exeFun.getQueryDataHashtableDoc("Doc3M012",  hashtableAnd,  "",  new  Vector(),  exeUtil) ;
        vectorClassName  =  new  Vector() ;
        //
        for(int  intNoL=0  ;  intNoL<vectorDoc3M012.size()  ;  intNoL++) {
            hashtableDoc3M012   =  (Hashtable)vectorDoc3M012.get(intNoL) ;
            stringClassName         =  (""+hashtableDoc3M012.get("ClassName")).trim() ;
            //
            if(vectorClassName.indexOf(stringClassName)  ==  -1)    vectorClassName.add(stringClassName) ;
        }
        vectorClassNameSum.add(vectorClassName) ;
    }
          stringDescript  =  "" ;
    String  stringTemp    =  "" ;
    for(int  intNo=0  ;  intNo<vectorClassNameSum.size()  ;  intNo++) {
        vectorClassName  =  (Vector)  vectorClassNameSum.get(intNo) ;
        //
        if(!"".equals(stringDescript))  stringDescript  +=  "\n" ;
        //
        stringTemp        =  "" ;
        for(int  intNoL=0  ;  intNoL<vectorClassName.size()  ;  intNoL++) {
            if(!"".equals(stringTemp))  stringTemp  +=  "、" ;
            stringTemp  +=  ""+vectorClassName.get(intNoL) ;
        }
        //
        if(!"".equals(stringTemp)) {
            stringTemp      +=  "。" ;
            stringDescript  +=  stringTemp ;
        }
    }
    setValue("Descript",  stringDescript) ;
}
public  boolean  getNewVersion (Doc2M010  exeFun) throws  Throwable{
    JTable      jtable6                    =  getTable("Table6") ;
    JTable      jtable9                    =  getTable("Table9") ;   
    String      stringBarCode         =  getValue("BarCode").trim() ; 
    String      stringComNo            =  getValue("ComNo").trim() ; 
    String      stringKindNo            =  getValue("KindNo").trim() ; 
    String       stringEmpDeptCd     =  getValue("DepartNo").trim() ; //get("EMP_DEPT_CD") ;
    String      stringPurchaseNo    =  "" ; 
    String      stringPurchaseNo1  =  "" ; 
    String      stringPurchaseNo2  =  "" ; 
    String      stringPurchaseNo3  =  "" ; 
    String      stringSql                   =  "" ; 
    String      stringFactoryNo        =  "" ; 
    String    stringOptometryBarCode     =  ""+get("OPTOMETRY_BarCode") ; 
    boolean  booleanExist              =  true ;
    Hashtable  hashtableData        =  new  Hashtable() ;
    //
    hashtableData.put("ComNo",        stringComNo) ;
    hashtableData.put("KindNo",       stringKindNo) ;
    hashtableData.put("EmpDeptCd",    stringEmpDeptCd) ;
    hashtableData.put("UserID",       getUser()) ;
    for(int  intNo=0  ;  intNo<jtable6.getRowCount()  ;  intNo++) {
        stringPurchaseNo1  =  (""+getValueAt("Table6",  intNo,  "PurchaseNo1")).trim() ;
        stringPurchaseNo2  =  (""+getValueAt("Table6",  intNo,  "PurchaseNo2")).trim() ;
        stringPurchaseNo3  =  (""+getValueAt("Table6",  intNo,  "PurchaseNo3")).trim() ;
        stringPurchaseNo    =  "" ;
        stringFactoryNo       =  (""+getValueAt("Table6",  intNo,  "FactoryNo")).trim() ;
        //
        if(!"".equals(stringPurchaseNo1)  &&  !"".equals(stringPurchaseNo2)  &&  !"".equals(stringPurchaseNo3))  stringPurchaseNo    =  stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3 ; 
        //
        hashtableData.put("FactoryNo",      stringFactoryNo) ;
        hashtableData.put("PurchaseNo",   stringPurchaseNo) ;
        return  exeFun.isNewVersion (hashtableData,  jtable9) ;
    }
    return  false ;
}
public  double  getPaidUpMoney(String     stringPurchaseNo1,        String      stringPurchaseNo2,    String      stringPurchaseNo3,  
                             String     stringFactoryNo,                String         stringBarCodePur,       String       stringGroupID,
                             boolean     booleanSpecPurchaseNo,  FargloryUtil  exeUtil,             Doc2M010   exeFun,
                             Hashtable  hashtablePaidUpMoney) throws  Throwable {
        String      stringEDateTime                  =  getValue("EDateTime").trim() ;    
    String      stringBarCode                      =  getValue("BarCodeOld").trim( ) ;                                 
    String      stringComNo                    =  getValue("ComNo").trim() ; 
    double    doublePaidUpMoney        = 0 ;
    Hashtable  hashtableData               =  new  Hashtable() ;
    //
    hashtableData.put("ComNo",           stringComNo) ;
    hashtableData.put("PurchaseNo1",  stringPurchaseNo1) ;
    hashtableData.put("PurchaseNo2",  stringPurchaseNo2) ;
    hashtableData.put("PurchaseNo3",  stringPurchaseNo3) ;
    hashtableData.put("FactoryNo",      stringFactoryNo) ;
    hashtableData.put("EDateTime",      stringEDateTime) ;
    hashtableData.put("GroupID",          stringGroupID) ;
    hashtableData.put("BarCode",       stringBarCode) ;
    hashtableData.put("BarCodePur",  stringBarCodePur) ;
    hashtableData.put("UseType",        "A") ;    // A 前期   B 已使用
    //
    doublePaidUpMoney  =  exeFun.getPaidUpMoney(hashtableData,  exeUtil) ;
    //
    System.out.println("getPaidUpMoney("+stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3+")----------------------------("+convert.FourToFive(""+doublePaidUpMoney,  0)+")") ;
    hashtablePaidUpMoney.put(stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,  convert.FourToFive(""+doublePaidUpMoney,  0)) ;
    return  doublePaidUpMoney ;