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
        if(value.trim().equals("�s�W") || value.trim().equals("�ק�") || value.trim().equals("�R��")) {
          if(getUser() != null && getUser().toUpperCase().equals("B9999")) {
            messagebox(value.trim()+"�v�������\!!!");
            return false;
          }
        }
        //201808check FINISH      
        // NEW
        // �^�ǭȬ� true ��ܰ��汵�U�Ӫ���Ʈw���ʩάd��
        // �^�ǭȬ� false ��ܱ��U�Ӥ����������O
        // �ǤJ�� value �� "�s�W","�d��","�ק�","�R��","�C�L","PRINT" (�C�L�w�����C�L���s),"PRINTALL" (�C�L�w���������C�L���s) �䤤���@
        // �ӿ�ɡA�wñ�ֹL���i����ק�\��
        /*
        getVoucherDepartNo  �����޿�ץ�
        getDescriptionUnion   ����
        getTableDataFrom     ��ǲ�
        */
        getButton("ButtonHalfWidth").doClick() ;
        getButton("ButtonTable22").doClick() ;    // �q���N�X�����B�z �w�]�� �� ���B�B�z
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
            System.out.println("�ɴ�--------------------S") ;
            stringMessage+="<br>1. isBatchCheckOK" ;
            // �s�W�έק�ɡA�ˮ֬������
            if(stringSubject.indexOf("�H�`")==-1  &&  !isBatchCheckOK(value.trim( ),  exeFun,  exeUtil)) {
                put("Doc6M011_STATUS",   "null") ;
                put("Doc6M013_STATUS",   "null") ;
                put("Doc6M010_Table3",     "null") ;
                //
                exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+getValue("BarCode").trim( )+"' ",  true,  exeUtil) ;
                return false ;
            }
            
            stringMessage+="<br>2. isFlowCheckOK" ;
            // �y�{
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
            stringMessage+="<br>3. ����t�ΦP�B" ;
            // ����t�ΦP�B
            String  stringBarCode  =  getValue("BarCodeOld").trim( ); 
            if("�R��".equals(value.trim( ))) {
                doDeleteData(stringBarCode,  exeUtil,  exeFun) ;
            }
            if(!"�R��".equals(value.trim( )))     doCheckDulFactoryNo (exeFun) ;
            // BarCode �B�z
             doBarCode(value.trim(),  exeFun,  exeUtil) ;
            stringMessage+="<br>4. �y�{�O��" ;
            System.out.println("--------------------------�y�{�O��") ;
            doHistory(value.trim(),  exeFun,  exeUtil) ;
            //
            doReSetBarCode(exeUtil,  exeFun) ;
            if(!"�R��".equals(value.trim())) getButton("Button3").doClick() ;// �P�B��P
            if(",B3446,".indexOf(getUser().toUpperCase())==-1)exeUtil.ClipCopy (getValue("BarCode").trim()) ;
            System.out.println("�ɴ�--------------------E") ;
        }catch(Exception e){
            Vector  vectorUse  =  exeFun.getEmployeeNoDoc3M011("P",  "") ;
                          arrayUser  =  (String[])  vectorUse.toArray(new  String[0]) ;
            exeUtil.doEMail(stringSubject,  stringMessage+"<br>"+e.toString(),  stringSend,  arrayUser) ;
            
            messagebox("��Ƶo�Ϳ��~�A�Ь���T�ǡC\n"+stringMessage+"\n"+e.toString()) ;
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
          // ����t�ΦP�B
          stringSql  =  "DELETE  Doc1M040 WHERE  BarCode  =  '"  +  stringBarCode  +  "' "  ;
          addToTransaction(stringSql);
          stringSql  =  "DELETE  Doc1M030 WHERE  BarCode  =  '"  +  stringBarCode  +  "' AND  ComNo  =  '"+stringComNo+"' " ; 
          addToTransaction(stringSql);
          stringSql  =  exeFun.doDeleteDBDoc("Doc2M044_AutoBarCode",  new  Hashtable(),  " AND  BarCode  =  '"+stringBarCode+"' ",  false,  exeUtil) ;
          addToTransaction(stringSql);
          // ��P�t�ΦP�B
          exeFun.doDeleteCiReaMM(stringBarCode) ;
          exeFun.doDeleteCoReaMM(stringBarCode) ;
          // �q���]��
          doMail(exeFun,  exeUtil) ;
    }
    // �ҥ~-���X�s�����s�]�w
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
        // ����ˮ֨ҥ~
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
        // �w��ҥ~
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
        if((stringFlow.indexOf("�g��")!=-1  ||  stringFlow.indexOf("�ӿ�")!=-1)  &&  "�s�W".equals(stringFunction))   {
            stringID            =  exeFun.getMaxIDForDoc6M010( ) ;
            setValue("ID",              stringID) ;
            if(stringFlow.indexOf("--�ӿ�")==-1  &&  stringFlow.indexOf("--�g��")==-1) {
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
    // �q���]��
    public void doMail(Doc.Doc2M010  exeFun,  FargloryUtil  exeUtil)throws Throwable{
        String                             stringID             =  getValue("ID").trim() ;
        String                             stringDocNo     =  getValue("DocNo1").trim()+"-"+getValue("DocNo2").trim()+"-"+getValue("DocNo3").trim() ;
        String                             stringDescript   =  getValue("Descript").trim() ;
        String                             stringBarCode  =  getValue("BarCodeOld").trim( ) ;
        String[][]                         retDoc2M080   =  exeFun.getDoc2M080(stringID,  "�ɴ�%'    AND  Remark  NOT LIKE  '�ɴڥ��R�P%'  AND  Remark  NOT LIKE  '�ɴڨR�P",  "",  "") ;
        if(retDoc2M080.length  >  0) {
            String  stringVoucher  =  retDoc2M080[0][23].trim() ;
                         stringVoucher  =  exeUtil.doSubstring(stringVoucher,  0,  12) ;
            //
            String    stringSubject      =  "[�ɴڥӽЮ�] �R�� �q��" ;
            String    stringContent     =  stringSubject  +  "<br>"  +
                                                        "���X�s���G["+stringBarCode+"]<br>"  +
                                  "����s���G["+stringDocNo+"]<br>"  +
                                  "���夺�e�G"+stringDescript +"<br>"  +
                                  "�w���ǲ��G"+stringVoucher +"<br>"  +
                                  "������ [�멳�w��] �� [�~���w��] �Ф�ʧ@�R�P";
            String    stringSendView  =  "�дڨt��" ;
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
              JOptionPane.showMessageDialog(null,  "�t�� "+stringFactoryNo+"("+stringFactoryName+") ����W�١A��Ʈw�s�b "+(retData.length)+" ���A���ˬd�t�ӬO�_���T�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
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
                JOptionPane.showMessageDialog(null,  "�t�� "+stringFactoryNo+"("+stringFactoryName+") ����W�١A��Ʈw�s�b "+(retData.length)+" ���A���ˬd�t�ӬO�_���T�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
            }
            break ;
        }
    }
    // ����
    public  void  doSyncBarCode( ) throws  Throwable {
        String   stringBarCode        =  getValue("BarCode").trim( ) ;
        String   stringBarCodeOld  =  getValue("BarCodeOld").trim( ) ;
        JTable  jtable                     =  null ;
        //
        //if(stringBarCode.equals(stringBarCodeOld))  return ;
        // 2011/05/10 �S��w�ⱱ��  4  �S��w�ⱱ�� �Ӷ�   5  �S��w�ⱱ�� ñ�e     6 �O��
        // 9 ���ʶ��ص��� Doc6M0171
        // 16 �N�P�X���Ƭd
        // 17 ���ʶ���-�קO���u
        // 22 �O�ι�ӳq���N�X
        for(int  intTableNo=1  ;  intTableNo<=22  ;  intTableNo++) {
            if(intTableNo  ==  7)    continue ; //�ϥα��p
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
        if(!"�s�W".equals(value.trim())) {
            String      stringID            =  getValue("ID").trim() ;
            String[][]  retDoc6M010  =  exeFun.getTableDataDoc("SELECT  UNDERGO_WRITE  FROM  Doc6M010  WHERE  ID  =  "+stringID+" ") ;
            if(retDoc6M010.length  ==  0) {
                message("��Ƶo�Ϳ��~�A�Ь���T�ǡC") ;
                return  false ;   
            }
            stringUnderGoWrite  =  retDoc6M010[0][0].trim() ;
        }
        //
        if("E".equals(stringUnderGoWrite)) {
              message("[�@�o���] ���i���ʸ�ơC") ;
              return  false ;
        }
        if(stringFlow.indexOf("ñ��") !=  -1)   {
            if(!"K".equals(stringUnderGoWrite)  &&  !"B".equals(stringUnderGoWrite)  &&  !"Y".equals(stringUnderGoWrite)) {
                message("�~�ީ|��ñ�֡A���i���� [�ק�] �\��C") ;
                return  false ;
            }
            setValue("UNDERGO_WRITE",  "Y") ;
        }
        if(stringFlow.indexOf("�~��") !=  -1  ||  stringFlow.indexOf("�f��") !=  -1)  {
            if("Y".equals(stringUnderGoWrite)) {
                message("�wñ�ֹL���i���� [�ק�] �\��C") ;
                return  false ;
            }
            if(!booleanFlowI) {
                setValue("UNDERGO_WRITE",  "B") ;
            } else {
                setValue("UNDERGO_WRITE",  "I") ;
            }
        } 
        if(stringFlow.indexOf("�g��")!=-1  ||  stringFlow.indexOf("�ӿ�")!=-1)  {
            if("B".equals(stringUnderGoWrite)  ||  "Y".equals(stringUnderGoWrite)) {
                if(stringFlow.indexOf("--�g��")==-1  &&  stringFlow.indexOf("--�ӿ�")==-1) {
                    message("�wñ�ֹL���i���� [�ק�] [�R��] �\��C") ;
                    return  false ;
                }
            }
            if( "�R��".equals(value.trim( ))  &&  "X".equals(stringUnderGoWrite)) {
                message("�y�{���A���i���� [�R��] �\��C") ;
                return  false ;           
            }
            String  StringEmployeeNo  =  getValue("EmployeeNo").trim( ).toUpperCase() ;
            if(!"B3018".equals(getUser())  &&  !StringEmployeeNo.equals(getUser().toUpperCase())) {
                JOptionPane.showMessageDialog(null,  "�� "  + StringEmployeeNo  +  " �إߤ���ơA�䥦�H���ಧ�ʳB�z�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                return  false ;
            }
            if(stringFlow.indexOf("--�g��")!=-1  ||  stringFlow.indexOf("--�ӿ�")!=-1) {
                if(!booleanFlowI) {
                    setValue("UNDERGO_WRITE",  "B") ;
                } else {
                    setValue("UNDERGO_WRITE",  "I") ;
                }
            } else {
                if(!"".equals(stringUnderGoWrite)  &&  "I,X,".indexOf(stringUnderGoWrite)!=-1) {
                    message("���i���� [�ק�] [�R��] �\��C") ;
                    return  false ;                       
                }
                setValue("UNDERGO_WRITE",  "A") ;
            }
        } 
        if(stringFlow.indexOf("�H�`") !=  -1)   {
            if("Y".equals(stringUnderGoWrite)) {
                messagebox("�wñ�ֹL���i���� [�ק�] [�R��] �\��C") ;
                return  false ;
            } 
            if(!"I".equals(stringUnderGoWrite)) {
                messagebox("�D [�H�`��ñ��]�A���i���� [�ק�] [�R��] �\��C") ;
                return  false ;
            } 
            setValue("UNDERGO_WRITE",  "K") ;
        }
        return  true ;
    }
    // �]�w DocNo
    public  boolean  isBatchCheckOK(String  value,  Doc.Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
         setValue("DocNo1",  getValue("DepartNo").trim()) ;
         // 
         String     stringFlow                      =  getFunctionName() ;
         String     stringBarCode               =  getValue("BarCode").trim( ) ;
         String     stringBarCodeOld         =  getValue("BarCodeOld").trim( ) ;
         String     stringDepartNoSubject  =  ""+get("EMP_DEPT_CD") ;
         // �N�P�X��
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
            messagebox("[�������O] ���~�C") ;
            getcLabel("KindNoD").requestFocus() ;
            return  false ;
        }
        // �̤������O KindNoD�A�ץ��w�w���פ��
        String  stringKindDay      =  exeFun.getKindDay(stringKindNoD) ;
        String  stringCDate          =  getValue("CDate").trim().replaceAll("/",  "") ;
        String   stringPreFinDate =  datetime.dateAdd(stringCDate,  "d",  exeUtil.doParseInteger(stringKindDay)) ;
        setValue("PreFinDate",  exeUtil.getDateConvertRoc(stringPreFinDate)) ;        
        
        // ���q�N�X 
        if(!isComNoOK(exeFun, exeUtil)) return  false ;
        // �ɴڳ�-����s�� 
        if(!isDocNoOK(stringFlow,  value,  stringDepartNoSubject,  exeFun, exeUtil)) return  false ;
        // �ɴڳ�-���ʽs�� 
        if(!isPurchaseNoOK(exeFun, exeUtil)) return  false ;
        // �O�_�w�ǲ��ˮ� 
        if(!isVoucherOK(exeFun, exeUtil)) return  false ;
        // ���X�s���B�z
        stringBarCode  =  isBarCodeOK(stringFlow,  value,  exeFun, exeUtil) ;
        if(stringBarCode.startsWith("ERROR")) return  false ;
        // �ӿ�H�� 
        if(!isOriEmployeeNoOK(stringFlow,  exeFun, exeUtil)) return  false ;
        // ������Y�ˮ�
        if(!isDateCheckOK(value,  stringFlow,  exeFun, exeUtil)) return  false ;
         // �S��w�ⱱ�� ����
         if(!is033FGOK(exeFun, exeUtil)) return  false ;
         // �䥦����ˮ�
         if(!isOtherFieldOK(exeFun, exeUtil)) return  false ;
        // �t�� 
        if(!isFactoryNoCheckOK(stringFlow,  exeUtil,  exeFun))  return  false ;
        // Table1 �o��
        if(!isTable1CheckOK(exeFun,  exeUtil))  return  false ;
        // Table2 ��ú
         if(!isTable2CheckOK(exeUtil,  exeFun))  return  false ;
        // Table3  �S�e
        if(!isTable3CheckOK(exeUtil,  exeFun))  return  false ;
        // �O�Ϊ�� oce
        if(!isTable6CheckOK(exeUtil,  exeFun))  return  false ;
        // ���P�覡�Ϊ��B
        System.out.println("���P�覡�Ϊ��B-------------------------------------------S") ;
        if(!isMoneyCheckOK(exeFun, exeUtil)) return  false ;
        System.out.println("���P�覡�Ϊ��B-------------------------------------------E") ;
         // 2013-09-04 �T���ˮ�
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
        boolean   booleanPurchaseExist  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false��ܥi�H�L���ʳ�  
        if(stringSpecBudget.indexOf(stringDocNo1)==-1) {
            setTableData("Table4",  new  String[0][0]) ;
            setTableData("Table5",  new  String[0][0]) ;  
            return  true ;
        }
        if(!"Z6".equals(stringComNo)) {
            messagebox("[��w�ⱱ��] �Ȥ��\ ["+get("Z6")+"] �ӽСC") ;
            return  false ;
        }
         if(exeFun.getTableDataDoc(stringSql).length  <=  0) {
            messagebox("�D�S��H�������\�ӽ� "+stringDocNo1+" �O�ΡC\n(�����D�Ь� [��P������])") ;
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
            messagebox("��Ƶo�Ϳ��~�A�Ь���T�ǡC") ;
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
            messagebox("[���q�N�X] ���i���ťաC") ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;       
        }
        // 2013-06-19 �� ���q���A(Doc7M056) ���ެO�_�i�ϥ�
        String[][]  retDoc7M056  =  exeFun.getDoc7M056(stringComNo,  "",  "",  "",  "") ;
        if(retDoc7M056.length  ==  0) {
            messagebox("���q "  +  stringComNo  +  "("+exeFun.getCompanyName(stringComNo)+") �����\�ϥΡC\n(�����D�Ь� [�]�ȫ�])") ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;               
        }
        String  stringUseType      =  retDoc7M056[0][4].trim() ;
        String  strinComNoType  =  retDoc7M056[0][2].trim() ;
        if(!"A".equals(stringUseType)) {
            messagebox("���q "  +  stringComNo  +  "("+exeFun.getCompanyName(stringComNo)+") �����\�ϥΡC\n(�����D�Ь� [�]�ȫ�])") ;
            getcLabel("ComNo").requestFocus( ) ;
            return  false ;       
        }
        return  true ;
    }
    public  boolean  isDocNoOK(String  stringFlow,  String  stringFunction,  String  stringDepartNoSubject,  Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        // ����s��������ˮ�
        String     stringBarCode        =  getValue("BarCode").trim( ) ;
        String     stringBarCodeOld  =  getValue("BarCodeOld").trim( ) ;
        String     stringComNo          =  getValue("ComNo").trim( ) ;
        String     stringDocNo1         =  getValue("DocNo1").trim( ) ;
        String     stringDocNo2         =  getValue("DocNo2").trim( ) ;
        String     stringDocNo3         =  getValue("DocNo3").trim( ) ;
        String     stringKindNo          =  getValue("KindNo").trim( ) ;
        String     retDateRoc            =  exeUtil.getDateFullRoc (stringDocNo2+"01",  "12345678") ;
        // �����N�X���s�b DepartNo
        String  stringDepartNo  =  getValue("DepartNo").trim( ) ;
        if("".equals(stringDepartNo)) {
            messagebox("[�����N�X] ���i���ťաC") ;
            getcLabel("DepartNo").requestFocus( ) ;
            return  false ;
        }
        String  stringDepartName  =  exeFun.getDepartName(stringDepartNo) ;
        if("".equals(stringDepartName)) {
            messagebox("[�����N�X] ���s�b��Ʈw���C\n(�����D�Ь� [��T������])") ;
            getcLabel("DepartNo").requestFocus( ) ;
            return  false ;
        }
        if("033H39,0333H39,0333H42,0333H42A,033H42,033H42A,033H42B,".indexOf(stringDepartNo+",")  !=  -1) {
            messagebox("[�����N�X]("+stringDepartNo+") �����\�ϥ�\n�����D�Ь��~�ޡC") ;//�}�ɬ�
            getcLabel("DepartNo").requestFocus( ) ;
            return  false ;
        }
        if(retDateRoc.length( )  !=  9) {
            messagebox("[����s��2] �榡���~(yymm)�C") ;
            getcLabel("DocNo2").requestFocus( ) ;
            return  false ;
        }
        if( "�s�W".equals(stringFunction)  &&  "0231,".indexOf(stringDepartNoSubject+",")!=-1) {
            // �۰ʵ���
            stringDocNo3  =  exeFun.getDocNo3Max(stringComNo,  stringKindNo,  stringDocNo1,  stringDocNo2,  stringDocNo1.startsWith("023")?"B":"A") ;
            setValue("DocNo3",  stringDocNo3) ;
        } else {
            if("".equals(stringDocNo3)) {
                messagebox("[����s��3] ���i���ťաC") ;
                getcLabel("DocNo3").requestFocus( ) ;
                return  false ;
            }
            if(!exeFun.isExistDocNoCheck(stringDocNo1,  stringDocNo2,  stringDocNo3,  stringKindNo,  stringComNo,  stringBarCodeOld)) {
                messagebox("[����N�X] ���СI "  +  stringDocNo1  +  "-"  +  stringDocNo2  +  "-"  +  stringDocNo3) ;
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
            messagebox("����l�ܨt�Τ��A�����ʳ�w [�@�o]�A�����\����C") ;
            return  false ;
         }
        if(retDoc1M030.length  >  0) {
            if(!stringKindNo.equals(retDoc1M030[0][5].trim())) {
                messagebox("[�������O] ���@�P�A�Ь� [��T������] �B�z�C ") ;
                return  false ;
            }
            if(!stringDocNoOld.equals(retDoc1M030[0][2].trim()+retDoc1M030[0][3].trim()+retDoc1M030[0][4].trim())) {
                messagebox("[����N�X] ���@�P�A�Ь� [��T������] �B�z�C ") ;
                return  false ;
            }
        }
        if("�R��".equals(stringFunction)) {
            if(retDoc1M040.length  >  1) {
                messagebox("�ɴڳ�w�����o��A�����\�R���A�Ь� [��T������]�C") ;
                return  false ;
            }
            if(retDoc1M040.length==1  &&  !"1".equals(retDoc1M040[0][6].trim())) {
                messagebox("�ɴڳ�D�Ф�A�����\�R���A�Ь� [��T������]�C") ;
                return  false ;
            }
            return  true ;
        }
        if(!"".equals(stringBarCode)  &&  !"".equals(stringBarCodeOld)  &&  !stringBarCode.equals(stringBarCodeOld)) {
            if(stringFlow.indexOf("�g��")!=-1  ||  stringFlow.indexOf("�ӿ�")!=-1) {
                messagebox("�ɴڳ椣���\�ܧ���X�s���A�Ь� [��T������]�C") ;
                return  false ;
            }
            if(retDoc1M040.length  >  1) {
                messagebox("�ɴڳ�w�����o��A�����\�ܧ���X�s���A�Ь� [��T������]�C") ;
                return  false ;
            }
            if(retDoc1M040.length==1  &&  !"1".equals(retDoc1M040[0][6].trim())) {
                messagebox("�ɴڳ�D�Ф�A�����\�ܧ���X�s���A�Ь� [��T������]�C") ;
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
            JOptionPane.showMessageDialog(null,  "[���ʳ渹] ���i���ťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
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
                stringTemp  =  "[���ʳ渹] ���s�b��Ʈw���C" ;
            } else {
                stringTemp     =  exeFun.getPurchseUndergoWriteName(retDoc3M011[0][15].trim()) ;
                stringTemp     =  "[���ʳ渹] �B�� ["+stringTemp+"] ���A�A�|���������ʬy�{�C\n(�����D�Ь� [���ʫ�])" ;
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
            messagebox("[���ʳ渹] ���s�b�� [��H]�C") ;
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            return  false ;
        }
        if("P93417".equals(stringBarCodePur)) {
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            messagebox("�S����ʳ椣���\�ϥΡC") ;
            return  false ;
        }
        // �I�ڱ���Ϊ��B�ˮ�
        String   stringBarCodeOld                   =  getValue("BarCodeOld").trim( ) ;
        double  doublePurchaseMoney           =  getContractMoney (stringBarCodePur,  stringFactoryNo,  stringGroupID,  exeUtil,  exeFun) ;
        double  doubleExistPurchaseMoney    =  getPaidUpMoney(stringPurchaseNo1,            stringPurchaseNo2,    stringPurchaseNo3,  
                                                        stringFactoryNo,                  stringBarCodePur,       stringGroupID,
                                                        booleanSpecPurchaseNo,       exeUtil,                 exeFun) ;
        double  doubleRealMoneySumL         =  exeUtil.doParseDouble(getValue("ThisPurchaseMoney").trim());
        if(doublePurchaseMoney  <  doubleExistPurchaseMoney+doubleRealMoneySumL) {
            messagebox("[���ʪ��B] + [�w���ʪ��B] �j�� [�X�����B]�C") ;
            getTabbedPane("Tab1").setSelectedIndex(0) ;
            return  false ;
        }
        //�@�禬�渹
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
        hashtableData.put("UseType",        "B") ;    // A �e��   B �w�ϥ�
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
                messagebox(" �w�}�߶ǲ����i�ק�C") ;
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
        if(stringFlow.indexOf("--") ==  -1  &&  (stringFlow.indexOf("�g��")!=-1  ||  stringFlow.indexOf("�ӿ�")!=-1))   {
            // ��P--�ӿ� �۰ʨ� Z �}�Y�����X�s��
        } else {
            if("�s�W".equals(stringFunction)) {
                // �۰ʨ���
                String     stringTemp     =  exeFun.getAutoBarCode(stringComNo,  stringDocNo1,  exeUtil) ;
                if(!"".equals(stringTemp)) {
                    stringBarCode  =  stringTemp ;
                }
            }
            if("".equals(stringBarCode)) {
                String  stringTemp      =  "" ;
                Vector  vectorDoc2M044   =  exeFun.getQueryDataHashtableDoc("Doc2M044",  new  Hashtable(),  " AND  DEPT_CD_NEW  LIKE   '"+exeUtil.doSubstring(stringDocNo1,  0,  3)+"%' ",  new  Vector(),  exeUtil) ;
                if(vectorDoc2M044.size()  ==  0) {
                    stringTemp  =  "[���X�s��] ���i���ťաC" ;
                } else {
                    stringTemp  =  "�۰ʨ��� �� [���X�s��] �w�ϥΧ��C" ;
                }
                messagebox(stringTemp) ;
                getcLabel("BarCode").requestFocus( ) ;
                return  "ERROR" ;
            } else {
                Vector  vectorDoc2M044   =  !"�s�W".equals(stringFunction) ? new  Vector() : exeFun.getQueryDataHashtableDoc("Doc2M044",  new  Hashtable(),  " AND  DEPT_CD  LIKE   '"+exeUtil.doSubstring(stringDocNo1,  0,  3)+"%' ",  new  Vector(),  exeUtil) ;
                if(vectorDoc2M044.size()  > 0) {
                    Hashtable  hashtableData  =  new  Hashtable() ;
                    hashtableData.put("BarCode",          stringBarCode) ;
                    hashtableData.put("EDateTime",        datetime.getTime("YYYY/mm/dd h:m:s")) ;
                    hashtableData.put("LastEmployeeNo",   getUser()) ;
                    hashtableData.put("Descript",         "��P-�ɴ�") ;
                    exeFun.doInsertDBDoc("Doc2M044_AutoBarCode",  hashtableData,  true,  exeUtil) ;
                    //
                    setValue("BarCode",  stringBarCode) ;
                }
            }
            // �P�_ Barcode �G (A ~ Z) 00001 ~ 99999
            String  stringStatus  =  exeFun.getBarCodeKindCheck(stringBarCode,  exeUtil) ;
            if(!"OK".equals(stringStatus)) {
                messagebox(stringStatus+"_") ;
                getcLabel("BarCode").requestFocus( ) ;
                return  "ERROR" ;   
            }
            /*if(!"".equals(stringBarCode)  &&  exeFun.getBarCodeFirstChar( ).indexOf(stringBarCode.substring(0,1))==-1) {
                  messagebox("[���X�s��] �����T�A�Э��s��J�C") ;
                  return  "ERROR" ;
            }
            char        charBarCodeFirst  =  stringBarCode.charAt(0) ;
            double    doubleBarCode     =  exeUtil.doParseDouble(stringBarCode.substring(1)) ;
            boolean  booleanJudge       =  stringBarCode.length( )  ==  6  &&   
                                      Character.isLetter(charBarCodeFirst)  &&
                                      (doubleBarCode >= 1  &&  doubleBarCode <=  99999) ;
            if(!booleanJudge) {
                messagebox("[���X�s��] �榡���~�C") ;
                getcLabel("BarCode").requestFocus( ) ;
                return  "ERROR" ;
            }*/
            // �s�b�ˮ�
            if(!stringBarCodeOld.equals(stringBarCode)) {             
                if(!exeFun.isExistBarCodeCheck(stringBarCode)) {
                    messagebox("[���X�s��] �w�s�b��Ʈw���A�а��� [�d��] ��A�@�ק�C") ;
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
            JOptionPane.showMessageDialog(null,  "[�ӿ�H��] ���i���ťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
            getcLabel("OriEmployeeNo").requestFocus( ) ;
            return  false ; 
        }
        String  stringEmpName  =  exeFun.getEmpName(stringOriEmployeeNo) ;
        if("".equals(stringEmpName)) {
            JOptionPane.showMessageDialog(null,  "[�ӿ�H��] ���s�b��Ʈw���C\n(�����D�Ь� [�H�`��])",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
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
            // �S�O���ޤ��q
            String  stringComNoCF  =  exeFun.getComNoForEmpNo(stringOriEmployeeNo) ;
            if(stringFlow.indexOf("ñ��")== -1  &&  !stringComNo.equals(stringComNoCF)) {
                messagebox("[�ӿ�H��] ��O���q�� ["+exeFun.getCompanyName(stringComNoCF)+"] �D ["+exeFun.getCompanyName(stringComNo)+"]�A�����\���ʡC") ;
                getcLabel("OriEmployeeNo").requestFocus( ) ;
                return  false ; 
            }
        }
        //   0  DEPT_CD     1  EMP_NO       2  EMP_NAME
        if("A0241,".indexOf(getUser())==-1  &&  !stringDepartNo.startsWith("053")  &&  stringFlow.indexOf("ñ��")  ==  -1  &&  "Z6".equals(stringComNo)) {
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
                    messagebox("���� �� [�����N�X] �����������קO�C\n(�����D�Ь� [��P�޲z��])") ;
                    getcLabel("DepartNo").requestFocus( ) ;
                    return  false ;
                }
            } else if(retFE3D05[0][0].indexOf("033")!=-1  ||  retFE3D05[0][0].indexOf("133")!=-1) {
                if(stringDepartNo.indexOf("0333")!=-1  ||  stringDepartNo.startsWith("0533")  ||  stringDepartNo.startsWith("1333")) {
                    messagebox("��P �� [�����N�X] ���i�ϥΥ������קO�C\n(�����D�Ь� [��P�޲z��])") ;
                    getcLabel("DepartNo").requestFocus( ) ;
                    return  false ;
                }
            }*/
        }
        return  true ;
    }
    //
    public  boolean  isDateCheckOK(String  stringValue,  String  stringFlow,  Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        if("�R��".equals(stringValue))  return  true ;
        // ����w�w���פ��
        String  stringCDate          =  getValue("CDate").trim(); 
        //String  stringPreFinDate  =  "107/09/27";
        String  stringPreFinDate  =  getValue("PreFinDate").trim( ) ;
        System.out.println(">>>PreFinDate>>>" + stringPreFinDate);
        if("".equals(stringPreFinDate)) {
            messagebox("[����w�w���פ��] ���i���ťաC") ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ; 
        }
        String  retDateRoc  =  exeUtil.getDateFullRoc (stringPreFinDate,  "����w�w���פ��") ;
        if(retDateRoc.length( )  !=  9) {
            messagebox(retDateRoc) ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ;
        }
        stringPreFinDate  =  retDateRoc ;
        setValue("PreFinDate",  stringPreFinDate) ;
        // ��J��� < ����w�w���פ��
        if(stringPreFinDate.compareTo(stringCDate)<=0) {
            messagebox("[����w�w���פ��][��J���] ������ǿ��~�C") ;
            getcLabel("PreFinDate").requestFocus( ) ;
            return  false ;
        }
        // �ݥΤ��
        String  stringNeedDate  =  getValue("NeedDate").trim( ) ;
        if("".equals(stringNeedDate)) {
            messagebox("[�ݥΤ��] ���i���ťաC") ;
            getcLabel("NeedDate").requestFocus( ) ;
            return  false ; 
        }
        retDateRoc  =  exeUtil.getDateFullRoc (stringNeedDate,  "�ݥΤ��") ;
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
        if(booleanCheck  &&  stringFlow.indexOf("ñ��")==-1  &&  stringNeedDate.compareTo(stringToday)  <=0) {
            messagebox("[�ݥΤ��] ���j�� [����]�C") ;
            getcLabel("NeedDate").requestFocus( ) ;
            return  false ;
        }
        // �w�w���P���
        String  stringDestineExpenseDate  =  getValue("DestineExpenseDate").trim( ) ;
        if("".equals(stringDestineExpenseDate)) {
            messagebox("[�w�w���P���] ���i���ťաC") ;
            getcLabel("DestineExpenseDate").requestFocus( ) ;
            return  false ; 
        }
        retDateRoc  =  exeUtil.getDateFullRoc (stringDestineExpenseDate,  "�w�w���P���") ;
        if(retDateRoc.length( )  !=  9) {
            messagebox(retDateRoc) ;
            getcLabel("DestineExpenseDate").requestFocus( ) ;
            return  false ;
        }
        stringDestineExpenseDate  =  retDateRoc ;
        setValue("DestineExpenseDate",  retDateRoc) ;
        // ��J���  <  �w�w���P���
        if(stringDestineExpenseDate.compareTo(stringCDate)<=0) {
            messagebox("[�w�w���P���][��J���] ������ǿ��~�C") ;
            getcLabel("DestineExpenseDate").requestFocus( ) ;
            return  false ;
        }
        if(stringFlow.indexOf("ñ��")==-1) {
            // �ݥΤ�� stringNeedDate <  �w�w���פ�� stringPreFinDate  < �w�w���P��� stringDestineExpenseDate

            System.out.println("stringNeedDate>>>" + stringNeedDate) ;
            System.out.println("stringPreFinDate>>>" + stringPreFinDate) ;
            System.out.println("stringDestineExpenseDate>>>" + stringDestineExpenseDate) ;
            
            if(booleanCheck  &&  stringPreFinDate.compareTo(stringNeedDate)<0) {
                messagebox("[�w�w���פ��][�ݥΤ��] ������ǿ��~�C") ;
                getcLabel("DestineExpenseDate").requestFocus( ) ;
                return  false ;
            }
            if(stringDestineExpenseDate.compareTo(stringPreFinDate)<0) {
                messagebox("[�w�w���P���][�w�w���פ��] ������ǿ��~�C") ;
                getcLabel("DestineExpenseDate").requestFocus( ) ;
                return  false ;
            }
        }
        return  true ;
    }
    public  boolean  isOtherFieldOK(Doc2M010  exeFun, FargloryUtil  exeUtil) throws  Throwable {
        // ���夺�e
        String  stringDescript  =  getValue("Descript").trim() ;
        if("".equals(stringDescript)) {
            messagebox("[���夺�e] ���i���ťաC") ;
            getcLabel("Descript").requestFocus( ) ;
            return  false ;
        }
        /*String  stringPayCondition1  =  getValue("PayCondition1").trim( ) ;
        String  stringPayCondition2  =  getValue("PayCondition2").trim( ) ;
        if("".equals(stringPayCondition1)  ||  "999".equals(stringPayCondition1)) {
            messagebox("[�I�ڱ���1] ���i���L�C") ;
            return  false ;
        }
        if("".equals(stringPayCondition2))  setValue("PayCondition2",  "999") ;*/
        //
        return  true ;
    }
    public  boolean  isFactoryNoCheckOK(String  stringFlow,  FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        // �Τ@�s��
        String  stringFactoryNo  =  getValue("FactoryNo2").trim() ;
        String  stringPayType    =  getValue("PayType").trim() ;
        String  stringDocNo        =  getValue("DocNo").trim() ;
        // �Τ@�s��
        if("".equals(stringFactoryNo)) {
            messagebox("[�Τ@�s��] ���i���ťաC1") ;
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        String  stringFactoryNoByte  =  code.StrToByte(stringFactoryNo) ;
        if(stringFactoryNo.length( ) != 8  &&  stringFactoryNo.length( ) != 10) {
            // 2015-03-09  B4197 �ӽ� 2014�p�w�߷|���|�����|(3���~��N�����ٴ�) �� �@�ҥ~
            if(",Z0001,Z0776,Z0007,".indexOf(stringFactoryNo)  ==  -1) {
                String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                    messagebox("[�Τ@�s��] �榡���~�C1") ;
                    //getcLabel("FactoryNo").requestFocus( ) ;
                    return  false ;
                }
            }
        }
        if(stringFactoryNo.length( ) == 8  &&  !check.isCoId(stringFactoryNo)) {
            String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
            if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                messagebox("[�Τ@�s��] �榡���~�C2") ;
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
                messagebox("[�Τ@�s��] �榡���~�C3") ;
                //getcLabel("FactoryNo").requestFocus( ) ;
                getcLabel("FactoryNo2").requestFocus( ) ;
                return  false ;
            }
        }
        String[][]  retFED1005            =  exeFun.getFED1005(stringFactoryNo) ;
        if(retFED1005.length  ==  0) {
            messagebox("��Ʈw���L�� [�Τ@�s��]�C") ;
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        // ���v
        String        stringStopUseMessage    =  "" ;
        Hashtable     hashtableCond           =  new  Hashtable() ;
        hashtableCond.put("OBJECT_CD",        stringFactoryNo) ;
        hashtableCond.put("CHECK_DATE",     getValue("CDate").trim()) ;
        hashtableCond.put("SOURCE",           "B") ;
        hashtableCond.put("FieldName",      "[�Τ@�s��] ") ;
        stringStopUseMessage  =  exeFun.getStopUseObjectCDMessage (hashtableCond,  exeUtil) ;
        if(!"TRUE".equals(stringStopUseMessage)) {
            getcLabel("FactoryNo2").requestFocus( ) ;
            messagebox(stringStopUseMessage) ;
            return  false ;
        }
        if(check.isID(stringFactoryNo)  &&  "".equals(retFED1005[0][9].trim())) {
            messagebox("�t�Ӹ�Ƥ� [�n�O�a�}]���ťաA���t�Ӥ����\�ϥΡA�и�[�n�O�a�}]��A�A�ϥΡC\n(�����D�Ь� [�]�ȫ�])") ;
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        String     stringComNo             =  getValue("ComNo").trim( ) ;
        if(!exeFun.isFactoryNoOK(stringComNo,  stringFactoryNo)) {
            getcLabel("FactoryNo2").requestFocus( ) ;
            return  false ;
        }
        // �Ĥ@���ϥ� 2016/11/14 ����
        /*String  stringToday        =  datetime.getToday("yymmdd") ;
        String  stringBarCode    =  getValue("BarCodeOld").trim( ) ;
        if(stringFlow.indexOf("ñ��") !=  -1)   {
            // �Ĥ@���ϥι�H�ήɶ��b�@�Ӥ뤧��
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
                messagebox("[�Τ@�s��] "  +  stringFactoryNo  +  " �Ĥ@���ϥΡC") ;
            }
        }*/
        return  true ;
    }
    // �o��
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
            // ���B
            if(doubleInvoiceTotalMoney ==  0  &&  doubleInvoiceMoney  ==  0) {
                JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o�����B]���i���s�ΪťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            } else if(doubleInvoiceTotalMoney > 0  &&  doubleInvoiceMoney  >  0) {
                // �����B�z
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
            // �ˮ�
            // �Τ@�s��
            if(intRowNo  ==  0)  stringFactoryNoOld  =  stringFactoryNo ;
            if("".equals(stringFactoryNo)) {
                JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�Τ@�s��] ���i���ťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            stringFactoryNoByte  =  code.StrToByte(stringFactoryNo) ;
            if(stringFactoryNo.length( ) == 8  &&  !check.isCoId(stringFactoryNo)) {
            //if(stringFactoryNo.length( ) == 8  &&  !check.isCoId(stringFactoryNo)  &&  stringFactoryNoByte.length( ) == stringFactoryNo.length( )) {
                //String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                //if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                    JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�Τ@�s��] �榡���~�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                    return  false ;
                //}
            }
            if(stringFactoryNo.length( ) != 8) {
            //if(check.isID(stringFactoryNo)  &&  stringFactoryNo.length( ) == 10  &&  stringFactoryNoByte.length( ) == stringFactoryNo.length( )) {
                //String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                //if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                    JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C�A[�Τ@�s��] �����\���ӤH�����ҡC\n(�����D�Ь� [�]�ȫ�])",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                    return  false ;
                //}
            }
            if(!stringFactoryNo.equals(stringFactoryNoOld)) {
                JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C�C\n�Ȥ��\�@�a [�Τ@�s��]�C\n(�����D�Ь� [�]�ȫ�])",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            }
            retFED1005        =  exeFun.getFED1005(stringFactoryNo) ;  //  0  LAST_YMD      1  LAST_USER
            if(retFED1005.length  ==  0) {
                JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C�A��Ʈw���L�� [�Τ@�s��]�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(check.isID(stringFactoryNo)  &&  "".equals(retFED1005[0][9].trim())) {
                messagebox("�o������ "  +  (intRowNo+1)  +  " �C�A�t�Ӹ�Ƥ� [�n�O�a�}]���ťաA���t�Ӥ����\�ϥΡA�и�[�n�O�a�}]��A�A�ϥΡC\n(�����D�Ь� [�]�ȫ�])") ;
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
            // �Ĥ@���ϥ� 2016/11/14 ����
            /*if(stringFlow.indexOf("ñ��") !=  -1)   {
                if(intRowNo ==  0) {
                    // �Ĥ@���ϥι�H�ήɶ��b�@�Ӥ뤧��
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
                        stringMessageFactor  +=   "�o������ "  +  (intRowNo+1)  +  " �C���Τ@�s�� "  +  stringFactoryNo  +  " �Ĥ@���ϥΡC\n" ;
                    }
                }
            }*/
            // /�o�����X
            if("".equals(stringInvoiceNo)  &&   !"D".equals(stringInvoiceKind) ) {
                JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] ���i���ťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            // �o�����X
            if("ABEMLNKOPRST".indexOf(stringInvoiceKind)  !=  -1) {
                if(stringInvoiceNo.trim( ).length( )  !=  10) {
                    JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] �j�p���~�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
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
                    JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C�� [�o���r�y] [�o�����] ���@�P�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                  }
            }
            // �o���榡�ˮ�
            if(",Z,Q,T,".indexOf(stringInvoiceKind) !=-1) {
                // �ƶq-�o�����X
                String[]  arrayInvoiceNo =  convert.StringToken(stringInvoiceNo,  "-") ;
                if(arrayInvoiceNo.length  !=  2) {
                    messagebox("�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] �榡���~(�ƥ�-�o�����X)�C") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(exeUtil.doParseDouble(arrayInvoiceNo[0])  <=  0) {
                    messagebox("�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] �ƥخ榡 �u�ର�Ʀr�C") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(arrayInvoiceNo[0].length()  !=  4) {
                    messagebox("�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] �ƥخ榡 ���� 4 �ӼƦr�C") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(exeUtil.doParseDouble(arrayInvoiceNo[0])  ==  1) {
                    String  stringTempL  =  "Q".equals(stringInvoiceKind)?"���㸹�X25":"����" ;
                    if("T".equals(stringInvoiceKind))  stringTempL  =  "���㸹�X25" ;
                    messagebox("��~�|���Ҫ��� "  +  (intRowNo+1)  +  " �C��[�o�����X] �ƥج� 1�A�o���榡 �п�� "+stringTempL+"�C") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                if(",Q,T,".indexOf(stringInvoiceKind) !=-1  &&  arrayInvoiceNo[1].length()  !=  10) {
                    messagebox("�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] �o�����X�榡 ���� 10 �ӼƦr�C") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
                // ���㸹�X�J�` �X�z���ˬd
                doubleInvoiceTaxL   =  doubleInvoiceTax  /  exeUtil.doParseDouble(arrayInvoiceNo[0]) ;
                if(doubleInvoiceTaxL  >  500) {
                    String  stringTempL  =  "" ;
                    if("Q".equals(stringInvoiceKind)) {
                        stringTempL  =  "���㸹�X�J�`25" ;
                    } else if("Z".equals(stringInvoiceKind)) {
                        stringTempL  =  "���ڷJ�`" ;
                    } else if("T".equals(stringInvoiceKind)) {
                        stringTempL  =  "���㸹�X�J�`���u25" ;
                    }
                    messagebox("��~�|���Ҫ��� "  +  (intRowNo+1)  +  " �C "+stringTempL+" �|�B�X�z���ˬd�C\n�o���|�B 500 �� �H�W�ɡA����W�C�ܡA���i�J�`�B�z�C") ;
                    jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                    jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
            }
            if(vectorInvoiecNo.indexOf(stringInvoiceNo)  !=  -1) {
                // ����
                JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] ���СC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            vectorInvoiecNo.add(stringInvoiceNo.trim( )) ;
            //if(vectorInvoiecNoDB.indexOf(stringInvoiceNo.trim( ))  ==  -1  &&  !exeFun.isExistInvoiceNoCheck(stringInvoiceNo)) {
            if(",C,X,Y,Z,Q,R,S,T,".indexOf(stringInvoiceKind) ==-1) {
                if(!exeFun.isExistInvoiceNoCheck(stringInvoiceNo,  stringBarCodeOld)) {
                    JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] �w�s�b��Ʈw���C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
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
                        JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o�����X] �w�s�b��Ʈw���C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                        jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                        jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                        return  false ;
                    }
                }
            }
            // �o�����|���B�ˬd
            if(!"D".equals(stringInvoiceKind)  &&  !"E".equals(stringInvoiceKind)) {
                if(doubleInvoiceTax  <  (doubleInvoiceMoneyTax-3)  ||  doubleInvoiceTax  >  (doubleInvoiceMoneyTax+3)) {
                      JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o���|�B] ���� [�o�����|���B] ���H [�|�v] ���t 3 ���d�򤺡C\n(�����D�Ь� [�]�ȫ�])",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                      jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                      jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                      return  false ;           
                }
            } else {
                if(doubleInvoiceTax  >  0) {
                    JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C��[�o���榡] �� [���o����] �ɡA[�o���|�B] ���� 0�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                    setValueAt("Table1",  "0",  intRowNo,  "InvoiceTax") ;
                    return  false ;
                }
            }
            // �o�����
            if("".equals(stringInvoiceDate)) {
                JOptionPane.showMessageDialog(null, "�o������ "  +  (intRowNo+1)  +  " �C��[�o�����] ���i���ťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;       
            }
            stringDateRoc  =  exeUtil.getDateFullRoc(stringInvoiceDate,  "�o�����") ;
            if(stringDateRoc.length( )  !=  9) {
                JOptionPane.showMessageDialog(null, stringDateRoc,  "�T��",  JOptionPane.ERROR_MESSAGE) ;
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
                                                "[�o�����] �������b�|�Ӥ뤺�A�p���n����A�Ы� [�O]�A�ýЪ��W�@����C",
                                                "�п��?",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE) ;
                if(ans  ==  JOptionPane.NO_OPTION) {
                    return  false ;
                }
            }
            //
            if(stringTodayCF.compareTo(stringDateRoc)  <  0) {
                JOptionPane.showMessageDialog(null,  "�o������ "  +  (intRowNo+1)  +  " �C�� [�o�����] ����ߩ󤵤�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                jtable1.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ; 
            }
        }
        if(!"".equals(stringMessageFactor)) {
            JOptionPane.showMessageDialog(null,   stringMessageFactor, "�T��",  JOptionPane.ERROR_MESSAGE) ;
        }
        return  true ;
    }
    // �ӤH����
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
                  JOptionPane.showMessageDialog(null,  "�ӤH��ú���� "  +  (intRowNo+1)  +  " �C �榡�� ���q����ú���ڡB���q�����ڡB�K�q���ɡA�t�ӶȤ��\�ϥ� �Τ@�s���B�ӤH�����ҡBZ0001���q���u�BZ8000���έ��u�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                  jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                  jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                  return  false ;   
            }
            // �|�v
            if(!"A".equals(stringReceiptKind)) doubleTaxRate  =  0 ;
            // ���B
            if(doubleReceiptTotalMoney  ==  0  &&  doubleReceiptMoney  ==  0) {
                JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C��[��ú���B]���i���s�ΪťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            } else if(doubleReceiptTotalMoney  >  0  &&  doubleReceiptMoney  >  0) {
                // �����B�z
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
            // �ұo���B�שұo�b�B+��ú�|�B
            doubleReceiptTax                =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptTax,  0)) ;
            doubleReceiptMoney           =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptMoney,  0)) ;
            doubleReceiptTotalMoney   =  exeUtil.doParseDouble(convert.FourToFive(""+doubleReceiptTotalMoney,  0)) ;
            if(doubleReceiptTotalMoney  !=  (doubleReceiptMoney+doubleReceiptTax)) {
                JOptionPane.showMessageDialog(null,  "�ӤH��ú���� "  +  (intRowNo+1)  +  " �C�� �ұo���B ������ �ұo�b�B+��ú�|�B�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(doubleReceiptTax  !=  0)  intHasTaxCount++ ;
            doubleReceiptMoneyTax  =  doubleReceiptTotalMoney  *  doubleTaxRate ;
            //
            // �ɥR�O�O
            if(",Q94544,Q94545,".indexOf(","+stringBarCode+",")==-1  &&  !isSupplementMoneyOK(intRowNo,  hashtableData,  exeUtil,  exeFun)) {
                  jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                  jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                  return  false ;
            }
            // �Τ@�s��
            if(intRowNo  ==  0)  stringFactoryNoOld  =  stringFactoryNo ;
            if("".equals(stringFactoryNo)) {
                JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C[�Τ@�s��] ���i���ťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            stringFactoryNoByte  =  code.StrToByte(stringFactoryNo) ;
            if(stringFactoryNo.length() != 10  &&  stringFactoryNo.length() != 8) {
                if(",Z0001,Z0007,".indexOf(stringFactoryNo)  ==  -1) {
                    String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                    if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                        JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C[�Τ@�s��] �榡���~�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                        return  false ;
                    }
                }
            } else if(stringFactoryNo.length() == 8) {
                if(!check.isCoId(stringFactoryNo)) {
                    String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                    if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                        JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C[�Τ@�s��] �榡���~�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                        return  false ;
                    }
                }
            } else if(stringFactoryNo.length() == 10) {
                if(!check.isID(stringFactoryNo)) {
                    String[][]  retDoc3M015  =  exeFun.getDoc3M015(stringFactoryNo) ;
                    if(retDoc3M015.length==0  ||  "1".equals(retDoc3M015[0][9].trim())) {
                        JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C[�Τ@�s��] �榡���~�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                        return  false ;
                    }
                }
            }
            if(!stringFactoryNo.equals(stringFactoryNoOld)) {
                JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C�o�Ϳ��~�A�Ȥ��\�@�a [�Τ@�s��]�C\n(�����D�Ь� [�]�ȫ�])",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;               
            }
            retFED1005        =  exeFun.getFED1005(stringFactoryNo) ;  //  0  LAST_YMD      1  LAST_USER
            if(retFED1005.length  ==  0) {
                JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C�o�Ϳ��~�A��Ʈw���L�� [�Τ@�s��]�C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
            if(check.isID(stringFactoryNo)  &&  "".equals(retFED1005[0][9].trim())) {
                messagebox("�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C�t�Ӹ�Ƥ� [�n�O�a�}]���ťաA���t�Ӥ����\�ϥΡA�и�[�n�O�a�}]��A�A�ϥΡC\n(�����D�Ь� [�]�ȫ�])") ;
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
            /*if(stringFlow.indexOf("ñ��") !=  -1)   {
                if(intRowNo ==  0) {
                    // �Ĥ@���ϥι�H�ήɶ��b�@�Ӥ뤧��
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
                        stringMessageFactor  +=   "�ӤH���ڲ����� "  +  (intRowNo+1)  +  " �C���Τ@�s�� "  +  stringFactoryNo  +  " �Ĥ@���ϥΡC\n" ;
                    }
                }
            }*/
            // �榡
            // �ұo�b�B
            // ��ú���B
            // �ұo�`�B
            if("A".equals(stringReceiptKind)) {
                if(doubleReceiptTax  <  (doubleReceiptMoneyTax-10)  ||  doubleReceiptTax  >  (doubleReceiptMoneyTax+10)) {
                      if(",Z0001,Z8000,".indexOf(","+stringFactoryNo+",")  ==  -1) {
                          JOptionPane.showMessageDialog(null,  "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C[��ú�|�B] ���� [�ұo�b�B] ���H [�|�v] ���t 10 ���d�򤺡C\n(�����D�Ь� [�]�ȫ�])",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                          jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                          jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                          return  false ;           
                      }
                }
            } else {
                if(doubleReceiptTax  >  0) {
                    JOptionPane.showMessageDialog(null, "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C[���ڮ榡] �� [���q����ú����][�K�q��] �ɡA[��ú���B] ���� 0�C\n(�����D�Ь� [�]�ȫ�])",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                    jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                    jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                    return  false ;
                }
            }
            // �����
            // ����
            if("".equals(stringAcctNo)  &&  stringFlow.indexOf("ñ��") !=  -1) {
                JOptionPane.showMessageDialog(null, "�ӤH���ڪ��� "  +  (intRowNo+1)  +  " �C [����] ���i���ťաC",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                jtable2.setRowSelectionInterval(intRowNo,  intRowNo)  ;
                return  false ;
            }
        }
        String    stringMessage                       =  ""+hashtableData.get("MESSAGE") ;
        if("null".equals(stringMessage))  stringMessage  =  "" ;
        if(!"".equals(stringMessage)) {
            messagebox("�N��ú�ɥR�O�O���B�����A���˪��K��ú�ɥR�O�O����ҩ��C") ;
        }
        /*String      stringPayCondition2   =  getValue("PayCondition2").trim( ) ;
        boolean   booleanFlag                 =  ("".equals(stringPayCondition2)    ||  "999".equals(stringPayCondition2))  &&
                               intHasTaxCount  ==  2  ;
        booleanFlag  =  booleanFlag  ||  intHasTaxCount  >  2 ;
        if(booleanFlag) {
            JOptionPane.showMessageDialog(null,   "�ӤH���ڪ�椤�A�X�{ "  +  intHasTaxCount  +  " �� [���q����ú��T] ��ơA�P [�I�ڱ���] ���@�P�C", "�T��",  JOptionPane.ERROR_MESSAGE) ;
            return  false ;
        }*/
        if(!"".equals(stringMessageFactor)) {
            JOptionPane.showMessageDialog(null,   stringMessageFactor, "�T��",  JOptionPane.ERROR_MESSAGE) ;
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
              messagebox("�ӤH��ú���� "  +  (intRow+1)  +  " �C��"  + stringMessage);
          }
          //
          setValueAt("Table2",  convert.FourToFive(""+doubleSupplementMoney,  0),  intRow,  "SupplementMoney") ;
          return  booleanFlag ;
      }
    
    
    // �S�e
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
            // �ɼӧO
            if("".equals(stringPosition)) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[�ɼӧO] ���i���ťաC", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            // �קO
            if("".equals(stringProjectID1)) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[�קO] ���i���ťաC", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            if("".equals(stringProjectID1Use)) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[��ڮקO] ���i���ťաC", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            stringProjectID1  =  stringProjectID1.toUpperCase() ;
            if(!exeFun.isExistProjectID1PositionCheck(stringProjectID1Use,  stringPosition)) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[�קO] [�ɼӧO]���s�b���Ʈw���C\n(�����D�Ь� [��P�޲z��])", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            setValueAt("Table3",  stringProjectID1,  intNo,  "ProjectID1") ;
            // ��������
            if(vectorKey.indexOf(stringKey)  !=  -1) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[�קO][�ɼӧO] �o�ͭ��ƥӽСC", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            vectorKey.add(stringKey) ;
            //System.out.println("--------------------�S�e��Ʈw����") ;
            stringSqlAnd   =  " AND  M14.ProjectID1Use  =  '"  +  stringProjectID1Use    +  "' " +
                                       " AND  M14.BarCode  <>  '"         +  stringBarCodeOld       +  "' "  +
                           " AND  M14.BarCode  NOT  IN  (SELECT  BarCode  FROM  Doc2M014  WHERE  STATUS_CD = 'Z')";
            retDoc6M014  =  exeFun.getDoc6M014S(stringProjectID1,  stringPosition,  stringSqlAnd)  ;
            if(retDoc6M014.length  >  0) {
                if(!"".equals(stringErrorMessage))  stringErrorMessage  +=  "�A\n" ;
                //
                stringErrorMessage  +=  "[�קO] �� "  +  stringProjectID1  +  "�B[�ɼӧO] �� "  +  stringPosition  +  "�b [���X�s��] �� " ;
                stringMessage            =  "" ;
                for(int  intDoc6M014=0  ;  intDoc6M014<retDoc6M014.length  ;  intDoc6M014++) {
                    stringBarCode  =  retDoc6M014[intDoc6M014][0].trim() ;
                    if("".equals(stringBarCode))  continue ;
                    if(!"".equals(stringMessage))  stringMessage  +=  "�B" ;
                    stringMessage  +=  stringBarCode ;
                }
                stringErrorMessage  +=  stringMessage  +  " �{�b���b�ӽФ��C " ;
            }
            // ñ�����I�{��
            if("".equals(stringSignDate)) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[ñ�����I�{��] ���i���ťաC", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            stringSignDate  =  exeUtil.getDateFullRoc(stringSignDate,  "ñ�����I�{��") ;
            if(stringSignDate.length()  !=  9) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[ñ�����I�{��] ����榡���~�C(yy/mm/dd)", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            //if(!"Y".equals(""+get("Century_START_DATE"))) stringSignDate  =  exeUtil.getDateConvertRoc(stringSignDate) ;
            String  stringCDateL  =  stringCDate ;
            String  stringToday    =  datetime.getToday("yy/mm/dd") ;
            if(stringToday.compareTo(stringCDateL) > 0)  stringCDateL  =  stringToday ;
            if(stringSignDate.compareTo(stringCDateL)  >  0) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[ñ�����I�{��("+stringSignDate+")] �|������A����ӽйS�e�C", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            setValueAt("Table3",  stringSignDate,  intNo,  "SignDate") ;
            // ñ����
            if("".equals(stringSignDate2)) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[ñ����] ���i���ťաC", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            stringSignDate2  =  exeUtil.getDateFullRoc(stringSignDate2,  "ñ����") ;
            if(stringSignDate2.length()  !=  9) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[ñ����] ����榡���~�C(yy/mm/dd)", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            if(stringSignDate2.compareTo(stringCDateL)  >  0) {
                JOptionPane.showMessageDialog(null,   "�S�e���� "  +  (intNo+1)  +  " �C��[ñ����("+stringSignDate2+")] ���� [��J���("+stringCDateL+")]�C", "�T��",  JOptionPane.ERROR_MESSAGE) ;
                jtabbedPane1.setSelectedIndex(intTable3Panel) ;
                return  false ;
            }
            setValueAt("Table3",  stringSignDate2,  intNo,  "SignDate2") ;
        }
        if(!"".equals(stringErrorMessage)) {
            messagebox(stringErrorMessage) ;
            return  false ;
            /*int  ans  =  JOptionPane.showConfirmDialog(null,  
                                            stringErrorMessage  +  "\n�p���n����A�Ы� [�O]�A�~��y�{�C",
                                            "�п��?",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE) ;
            if(ans  ==  JOptionPane.NO_OPTION) {
                return  false ;
            }*/
        }
        if(getFunctionName().indexOf("ñ��")  ==  -1)  return  true ;
        
        
        
        // ���ƧP�_
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
        // �ɴ�
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
        // �д�
        stringSql  =  " SELECT  DISTINCT  BarCode " +
                              " FROM  Doc2M010 "  +
                  " WHERE  1  =  1 "  +
                       " AND  UNDERGO_WRITE  <>  'E' "  +
                     " AND  CDate  <=  '"  +  stringCDate  +  "' "  +
                     " AND  ((BarCode  IN  (SELECT  BarCode  FROM  Doc2M011  WHERE  FactoryNo  =  '"  +  stringFactoryNo  +  "'))  OR "  +
                          " (BarCode  IN  (SELECT  BarCode  FROM  Doc2M013  WHERE  FactoryNo  =  '"  +  stringFactoryNo  +  "'))) " ;
        retData     =  exeFun.getTableDataDoc(stringSql) ;
        JOptionPane.showMessageDialog(null,  "�t�� "+stringFactoryNo+"("+exeFun.getFactoryName(stringFactoryNo) +")�A�]�t�����B�ɴ� "+intCount+" ���B�д� "+retData.length +" ���A�@�ϥ� "+(intCount+retData.length+1)+" ���C",  "�T��",  JOptionPane.ERROR_MESSAGE) ;
        return   true ;
    }
    public  boolean  isTable4CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        return  true ;
    }
    // �O�Ϊ�� 
    public  boolean  isTable6CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        JTable               jTable6                                      =  getTable("Table6") ;
        JTable               jtable9                                    =  getTable("Table9") ;
        JTable               jtable22                                   =  getTable("Table22") ;
        boolean            booleanPurchaseExist               =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false��ܥi�H�L���ʳ�  
        boolean            booleanTable9                  =  jtable9.getRowCount()>0 ;
        boolean            booleanTable22                   =  jtable22.getRowCount()>0 ;
        //
        if(jTable6.getRowCount()  <=  0)            doTable6ErrorAction(-1,  "�п�J [�O��] ��ơC") ;
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
        // ��J���� ���u���� �@�P�ˮ�
        System.out.println("isTable6InputDeptCdOK----------------------------S") ;
        if(!isTable6InputDeptCdOK(exeUtil,  exeFun))  return  false ;
        System.out.println("isTable6InputDeptCdOK----------------------------E") ;
        // �����ˮ�
        if(!isTable6DulDataOK(exeUtil))  return  false ;
        //
        for(int  intNo=0  ;  intNo<jTable6.getRowCount()  ;  intNo++) {
            // �дڥN�X�ˮ�
            if(!isTable6CostIDOK(vectorDoc2M020,  intNo,  exeUtil,  exeFun))                        return  false ;
            // �����ˮ�
            if(!isTable6DepartNoOK(stringExistDeptCd,  intNo,  exeUtil,  exeFun))                     return  false ;
            // �קO
            if(!isTable6ProjectID1OK(intNo,  hashtable1331AProject,  exeUtil,  exeFun))               return  false ;
            // ���B
            hashtableUsedRealMoney  =  isTable6MoneyOK(intNo,  exeUtil,  exeFun,  hashtableUsedRealMoney,  hashtableError) ;
            stringStatus                         =  ""+hashtableError.get("STATUS") ;
            if(!"OK".equals(stringStatus)) {
                return  false ;
            }
        }
        if(booleanTable9) {
            // Table6 �P Table9 �@�P�ˮ�
            //System.out.println("isTable2SameTable17CheckOK----------------------------------") ;
            if(!isTable6SameTable17CheckOK(exeUtil,  exeFun))  return  false ;
            //System.out.println("isTable17CheckOK----------------------------------") ;
            // Table17 �� ���ʶ��خקO���u�ˮ�
            if(!isTable17CheckOK(exeUtil,  exeFun))           return  false ;
        }
        if(booleanTable22) {
            // Table6 �P Table22 �@�P�ˮ�
            System.out.println("isTable2SameTable22CheckOK----------------------------------") ;
            if(!isTable6SameTable22CheckOK(exeUtil,  exeFun))  return  false ;
        }
        // ���O�ˮ�
        System.out.println("���O�ˮ�---------------------------------S") ;
        if(!isTable6CoinTypeCheckOK(exeUtil,  exeFun))              return  false ; 
        System.out.println("���O�ˮ�---------------------------------E") ;
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
            // ����
            if(vectorKEY.indexOf(stringKey)  !=  -1) {
                return  doTable6ErrorAction(intNo,  "[�~�O] + [����] + [�קO] ��ƭ��СC") ;
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
        // �����~�B���קO��ƮɡA�����\
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
        // �����B�קO���u�@�P�ˮ�
        return  doTable6ErrorAction(-1,  "�������s�b�קO���u���C") ;
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
        String                string033FGType                     =  "0" ;  // 2011-04-08 033FG �ץ�  0 ���]�w  1 033FG  2 �D033FG  3 �V��
        String                stringDocNo1                            =  getValue("DocNo1").trim() ;
        String             stringSpecBudget             =  ","+get("SPEC_BUDGET")+"," ;
        for(int  intNo=0  ;  intNo<jTable6.getRowCount()  ;  intNo++) {
            stringInOut                   =  (""  +  getValueAt("Table6",  intNo,  "InOut")).trim( ) ;
            stringDepart                 =  (""  +  getValueAt("Table6",  intNo,  "DepartNo")).trim( ) ;
            if("I".equals(stringInOut)) {
                if(stringSpecBudget.indexOf(stringDepart)  ==  -1) {
                    if("1".equals(string033FGType)){
                        return  doTable6ErrorAction(intNo,  stringDocNo1+" �����\�M�䥦�����ήקO�@�P���u�C\n(�����D�Ь� [�]�ȫ�])") ;
                    }
                    string033FGType  =  "2" ; 
                } else {
                    if(stringDocNo1.equals(stringDepart)) {
                        if("2".equals(string033FGType)){
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" �����\�M�䥦�����ήקO�@�P���u�C\n(�����D�Ь� [�]�ȫ�])") ;
                        }
                        if(stringSpecBudget.indexOf(stringDepart)  ==  -1) {
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" �����\�M�䥦�����ήקO�@�P���u�C\n(�����D�Ь� [�]�ȫ�])") ;
                        }
                        string033FGType  =  "1" ;
                    } else {
                        if("1".equals(string033FGType)){
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" �����\�M�䥦�����ήקO�@�P���u�C\n(�����D�Ь� [�]�ȫ�])") ;
                        }
                        if(stringSpecBudget.indexOf(stringDepart)  !=  -1) {
                            return  doTable6ErrorAction(intNo,  stringDocNo1+" �����\�M�䥦�����ήקO�@�P���u�C\n(�����D�Ь� [�]�ȫ�])") ;
                        }
                        string033FGType  =  "2" ; 
                    }
                }
            } else {
                if("1".equals(string033FGType)){
                    return  doTable6ErrorAction(intNo,  stringDocNo1+" �����\�M�䥦�����ήקO�@�P���u�C\n(�����D�Ь� [�]�ȫ�])") ;
                }
                string033FGType  =  "2" ;
            }
            if("1".equals(string033FGType)){
                if(!stringDocNo1.equals(stringDepart)) {
                    return  doTable6ErrorAction(intNo,  "�קO���u���u�� ["+stringDepart+"] �ɡA[����s��] ���� "+stringDocNo1+"�C\n(�����D�Ь� [�]�ȫ�])") ;
                }
            }
            if("2".equals(string033FGType)){
                if(stringSpecBudget.indexOf(stringDocNo1)  !=  -1) {
                    return  doTable6ErrorAction(intNo,  "[����s��] �� "+stringDocNo1+"�A�קO���u�����Ȥ��\ "+stringDepart+"]�C\n(�����D�Ь� [�]�ȫ�])") ;
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
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false��ܥi�H�L���ʳ�  
        // �дڥN�X���o���ťաC
        if("".equals(stringCostID)) {
            return  doTable6ErrorAction(intNo,  "[�дڥN�X] ���o���ťաC") ;
        }
        if("".equals(stringCostID1.trim( ))) {
            return  doTable6ErrorAction(intNo,  "[�p�дڥN�X] ���o���ťաC") ;
        }
        String    stringBarCode   =  getValue("BarCode").trim() ;
        String    stringComNo     =  getValue("ComNo").trim() ;
        String[][]  retDoc7M011  =  getDoc7M011(stringComNo,  "",  stringCostID,  stringCostID1,  exeFun) ;
        if(retDoc7M011.length  ==  0) {
            if(exeFun.getDoc2M021(stringCostID,  stringCostID1).indexOf(stringBarCode)  ==  -1) {
                return  doTable6ErrorAction(intNo,  "[�p�дڥN�X] ���s�b�� [�O��-�w��N�X-�ɤ�|�p��ع�Ӫ�(Doc2M020)] ���C\n(�����D�Ь� [��P�޲z��])") ;
            }
        }
        if(!booleanPurchaseExist) {
            if(vectorDoc2M020.indexOf(stringCostID  +  "-"  +  stringCostID1)  ==  -1) {
                return  doTable6ErrorAction(intNo,  "[�дڥN�X] �D�i�L���ʳ椧 [�дڥN�X]�C\n(�����D�Ь� [��P�޲z��])") ;
            }
        }
        
        if((","+stringSpecBudget+",").indexOf(stringDocNo)  ==  -1) {
            if(stringDocNo.startsWith("033")  ||  stringDocNo.startsWith("053")  ||  stringDocNo.startsWith("133")) {
                // ��P B2017-03-17 B2358 ��������
                /*if("60,".indexOf(stringCostID)!=-1) {
                    return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C [��P����] �����\�ϥ� [����]�A�����\���ʸ�Ʈw�C\n(�����D�Ь� [�]�ȫ�])�C") ;
                }*/
            } else {
                // �D��P
                if("31,32,".indexOf(stringCostID)  !=  -1) {
                      return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C �D[��P����] �����\�ϥ� [���Z��][�s�Ϊ�]�A�����\���ʸ�Ʈw�C\n(�����D�Ь� [�]�ȫ�])�C") ;
                }
                if(exeUtil.doParseDouble(stringCostID)  >=  70) {
                    if(stringDocNo.startsWith("015")  &&  "721,".indexOf(stringCostID+stringCostID1)!=-1) {
                      // �S�Ҥ��\
                    } else {
                        if(!"0333".equals(getValue("DocNo1"))) {
                            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C �D[��P����] �����\�ϥ� 70 ���᪺�дڥN�X�A�����\���ʸ�Ʈw�C\n(�����D�Ь� [�]�ȫ�])�C") ;
                        }
                    }
                  }
            }
        }
        // �дڥN�X�P���~�~�@�P�ˮ�
        if((","+stringSpecBudget+",03396,03335,033622,03363,0333,03365,").indexOf(","+stringDepart+",")==-1  &&  !exeFun.isInOutToCostID(stringInOut,  stringCostID)) {
            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C [�дڥN�X] �P [��/�~�~] ���@�P�C\n(�����D�Ь� [��P�޲z��])") ;
        }
        if(stringSpecBudget.indexOf(stringDocNo)  !=  -1) {
            if(retDoc7M011.length==0  ||  !retDoc7M011[0][0].startsWith("B")) {
                return  doTable6ErrorAction(intNo,  "["+stringDocNo+"] �Ȥ��\�ӽХ������O�ΡC\n(�����D�Ь� [�]�ȫ�])") ;
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
                  return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C [�дڥN�X][�p�дڥN�X] �����\�ϥΡC\n(�����D�Ь� [��P�޲z��])�C") ;
            }
        }
        // ���o����P�_
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
                    doTable6ErrorAction(intNo,  "�O�Ϊ�椧�� "+(intNo+1)+" �C �дڥN�X�A�Ω󤣱o����A�P [�o���榡] ���P�C\n(�����D�Ь� [�]�ȫ�])") ;
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
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false��ܥi�H�L���ʳ�  
        if(stringDocNo.indexOf("033FZ")!=-1  &&  !"0333".equals(stringDepart)) {
            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C�� ����s�� 033FZ �u��� ���~ 0333 �@���u �C\n(�����D�Ь� [�H�`��])") ;
        }
        if("".equals(stringDepart.trim( ))) {
            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C��[����] ���o���ťաC") ;
        }
        if(exeUtil.doParseDouble(stringDepart)<=0  &&  stringSpecBudget.indexOf(stringDepart+",")==-1) {
            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C��[����] �u�ର 4 �ӼƦr�C\n(�����D�Ь� [��T������])") ;
        }
        if("".equals(exeFun.getDepartName(stringDepart.trim( )))) {
            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C��[����] ���s�b��Ʈw���C") ;
        }
        if("O".equals(stringInOut)) {
            if(",0331,1331,0531".indexOf(","+stringDepart+",")  ==  -1) {
                return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C�� �~�~�ɡA[����] �u�ର [0331] �� [1331] �� [0531]�C") ;
            }
            if(!"0531".equals(stringDepart.trim( ))) {
                String[][]  retAProject  =  exeFun.getTableDataSale("SELECT  ProjectID " +
                                                                " FROM  A_Project "  +
                                                                " WHERE  Depart  =  8 "  +
                                                                  " AND  ProjectID  IN(  '"  +  stringProjectID+"',  '"  +  stringProjectID1+"') "
                                                    ) ;
                if(!"1331".equals(stringDepart)  &&  retAProject.length>0) {
                    return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C���קO���� 1331�A�����\���ʸ�Ʈw�C") ;
                } 
                if("1331".equals(stringDepart)  &&  retAProject.length==0) {
                    return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C���קO������ 1331�A�����\���ʸ�Ʈw�C") ;
                } 
            }
        }   else {
            if(!booleanPurchaseExist  &&  !"".equals(stringExistDeptCd)  &&  stringExistDeptCd.indexOf(exeUtil.doSubstring(stringDepart, 0, 3))  ==  -1) {
                return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C ���~����("+stringDepart.trim( )+")���s�b��Ӥ��q�A�����\���ʸ�Ʈw�C\n(�����D�Ь� [�]�ȫ�])") ;
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
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false��ܥi�H�L���ʳ�  
        // �����~�B���קO��ƮɡA�����\
        if("I".equals(stringInOut)) {
            if(!"".equals(stringProjectID)  ||  !"".equals(stringProjectID1)) {
                return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C ��� [���~] �ɡA�קO���i����ơC") ;
            }
            return  true ;
        }
        if("".equals(stringProjectID.trim( ))) {
            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C��[�j�קO] ���o���ťաC") ;
        }
        if("".equals(stringProjectID1.trim( ))) {
            return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C��[�p�קO] ���o���ťաC") ;
        }
        // �s�b�ˬd
        if("0531".equals(stringDepart)) {
            String      stringSqlAnd   =  " AND  DateStart  <=  '"+stringCDateAC+"'  AND  DateEnd  >=  '"+stringCDateAC+"' " ;
            String[][]  retDoc2M051  =  exeFun.getDoc2M051(stringProjectID1,  "A",  stringSqlAnd) ;
            if(retDoc2M051.length  <=  0) {
                return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C�� �򤶮קO ���s�b���Ʈw���C") ;
            }
        } else {
            if(!exeFun.isExistProjectIDCheck(stringProjectID,  stringProjectID1)) {
                return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C��[�j�קO] [�p�קO] ���s�b���Ʈw���C\n(�����D�Ь� [��P�޲z��])") ;
            }
        }
        if(!"0531".equals(stringDepart)) {
            String  stringDepart1    =  ""+hashtable1331AProject.get(stringProjectID) ;
            String  stringDepart2    =  ""+hashtable1331AProject.get(stringProjectID1) ;
            if(!"1331".equals(stringDepart)  &&  ("8".equals(stringDepart2)  ||  "8".equals(stringDepart1))) {
                return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C���קO���� 1331�A�����\���ʸ�Ʈw�C") ;
            } 
        }
        if("990".equals(stringCostID+stringCostID1)) {
            String  stringProjectID1L =  stringProjectID1 ;
            if("0531".equals(stringDepart)) {
                stringProjectID1L  =  exeFun.get053ProjectID1Doc2M051(stringProjectID1L) ;
            }
            boolean  booleanTemp  =  stringDocNo1.indexOf(stringProjectID1L)!=-1  ||  exeUtil.isDigitNum (stringDocNo1)  ;
            if(!booleanTemp) {
                return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C�� [�O�ΥN�X] �� 990�ɡA[�ɴڳ泡��] ���P �����@�P�C\n(�����D�Ь� [�]�ȫ�])") ;
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
                    return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C [�קO]("+stringProjectID1+") �|�����\�ϥΡC\n(�����D�Ь� [�]�ȫ�])") ;
                }
                if(stringCDateAC.compareTo(retDoc7M0265[0][2].trim())   >  0) {
                    return  doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C [�קO]("+stringProjectID1+") �w�����\�ϥΡC\n(�����D�Ь� [�]�ȫ�])") ;
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
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false��ܥi�H�L���ʳ�  
        //
        hashtableError.put("STATUS",  "") ;
        // �O�Ϊ��B
        if(exeUtil.doParseDouble(stringRealTotalMoney)  ==  0) {
            doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C [�дڪ��B]���i���s�ΪťաC") ;
            return  hashtableUsedRealMoney ;                
        }
        setValueAt("Table6",  stringRealTotalMoney,  intNo,  "RealTotalMoney") ;
        // ���ʤ@�P�ˮ�
        System.out.println("���ʤ@�P�ˮ�---------------------------------------S") ;
        if(jtable9.getRowCount()==0  &&  booleanPurchaseExist) {
            if(hashtableUsedRealMoney  ==  null) {
                  hashtableUsedRealMoney  =  getUsedProjectIDMoney2(exeFun,  exeUtil) ;
            }
            if(hashtableUsedRealMoney  ==  null) {
                doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C ��Ƶo�Ϳ��~�A�Ь���T�ǡC") ;
                return  hashtableUsedRealMoney ;
            }
            double  doubleTemp  =  exeUtil.doParseDouble(""+hashtableUsedRealMoney.get(stringKey)) ;
              if(doubleTemp  <  exeUtil.doParseDouble(stringRealTotalMoney)) {
                doTable6ErrorAction(intNo,  "�O�Ϊ��� " +(intNo+1) +" �C [��Ʈw�����קO���u���B�X�p]("+exeUtil.getFormatNum2(stringRealTotalMoney)+") �j�� [���ʥӽЮѤ��קO���u���B�X�p]("+exeUtil.getFormatNum2(""+doubleTemp)+")�C\n���ˬd [����] [�קO] [�дڥN�X] �P���ʥӽЮѬO�_�@�P�C") ;
                return  hashtableUsedRealMoney ;
              }
        }
        System.out.println("���ʤ@�P�ˮ�---------------------------------------E") ;
        hashtableError.put("STATUS",  "OK") ;
        return  hashtableUsedRealMoney ;
    }
    // �w�дڮקO���B��z
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
                doTable6ErrorAction(-1,  "�ɴڥӽЮ� �����\�h���ȡC") ;
            }
            stringCoinType  =  stringCoinTypeL ;
        }
        //
        setValue("CoinType",  stringCoinType) ;
        return   true ;
    }
    public  boolean  isTable6SameTable17CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        // �קO ���B �@�P�ˮ�
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
                return  doTable6ErrorAction(-1,  "�O�Ϊ�� �P ���ʶ��خקO���u��� �~����Ƥ��@�P�A�Ь���T�ǡC1") ;
            }
            if(vectorKEY17.indexOf(stringKEY)  ==  -1)  vectorKEY17.add(stringKEY) ;
            //
            doubleMoney17  =  exeUtil.doParseDouble(stringRealTotalMoney)  +  exeUtil.doParseDouble(""+hashtableMoney.get(stringKEY+"%-%Table17")) ;
            hashtableMoney.put(stringKEY+"%-%Table17",  convert.FourToFive(""+doubleMoney17,  0)) ;
        }
        if(vectorKEY6.size()  !=  vectorKEY17.size()) {
            return  doTable6ErrorAction(-1,  "�O�Ϊ�� �P ���ʶ��خקO���u��� �~����Ƥ��@�P�A�Ь���T�ǡC2") ;
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
                return  doTable6ErrorAction(-1,  "�O�Ϊ�� �P ���ʶ��خקO���u��� ���B��Ƥ��@�P�A�Ь���T�ǡC3") ;
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
        // ���ʶ��ؤ��קO���u ���B�@�P�ˮ�
        for(int  intNo=0  ;  intNo<jtable17.getRowCount()  ;  intNo++) {
            hashtableTable17Data  =  getTable17DataHashtable(intNo) ;
            // ���ʪ��B 
            stringBarCodePur         =  getBarCodePur(hashtableTable17Data,  exeUtil,  exeFun,  hashtableBarCodePur) ;hashtableTable17Data.put("BarCode",  stringBarCodePur) ;
            System.out.println(intNo+"stringBarCodePur("+stringBarCodePur+")----------------------------------") ;
            doublePurchaseMoney     =  getPurchaseMoneyDoc3M0123(hashtableTable17Data,   exeUtil,  exeFun) ;
            // �дڪ��B-�д� Doc2M0172
            doubleTemp             =  getRequestMoneyDoc2M0172(hashtableTable17Data,   exeUtil,  exeFun) ;
            doubleRequestMoney    =   doubleTemp ;
            // �дڪ��B-�ɴ�-�s�� Doc6M0172
            // �дڪ��B-�ɴڨR�P-�ª� Doc6M0172
            doubleTemp            =  getRequestMoneyDoc6M0172(hashtableTable17Data,   exeUtil,  exeFun) ;
            doubleRequestMoney  +=    doubleTemp ;
            //
            doublePurchaseMoney  =  exeUtil.doParseDouble(convert.FourToFive(""+doublePurchaseMoney,  0)) ;
            doubleRequestMoney    =  exeUtil.doParseDouble(convert.FourToFive(""+doubleRequestMoney,  0)) ;
            if(doublePurchaseMoney  <  doubleRequestMoney) {
                String        stringInOut                   =  ""+hashtableTable17Data.get("InOut") ;
                if("I".equals(stringInOut)) {
                    stringTemp  =  "����("+hashtableTable17Data.get("DepartNo")+")"  ;
                } else {
                    stringTemp  =  "�קO("+hashtableTable17Data.get("ProjectID1")+")"  ;
                }
                stringTemp  +=  "�дڥN�X("+hashtableTable17Data.get("CostID")+hashtableTable17Data.get("CostID1")+")" ;
                return  doTable6ErrorAction(-1,  "���ʶ��ؤ��קO���u��� "+stringTemp+"�w�ϥΪ��B("+exeUtil.getFormatNum2(""+doubleRequestMoney)+") �j�� �i�ιw����B("+exeUtil.getFormatNum2(""+doublePurchaseMoney)+")�C") ;
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
        JTable               jtable1                                     =  getTable("Table1") ; // �o��
        JTable               jtable2                                     =  getTable("Table2") ; // ��ú
        int                      intTable2Panel                        =  3 ;
        int                     intTable1Panel                         =  2 ;
        String               stringPayType                          =  getValue("PayType").trim( ) ;
        double             doubleRealMoneySum              =  exeUtil.doParseDouble(getValue("RealMoneySum").trim( )) ;
        double             doubleInvoiceTotalMoneySum  =  exeUtil.doParseDouble(getValue("InvoiceTotalMoneySum").trim( )) ;
        double             doubleServiceAMT                   =  exeUtil.doParseDouble(getValue("ServiceAMT").trim()) ;
        double             doubleReceiptTotalSum           =  exeUtil.doParseDouble(getValue("ReceiptSum").trim( )) ;
        if("A".equals(stringPayType)) {
            // �ӤH����
            setTableData("Table1",  new  String[0][0]) ;    // �o��
            setValue("BorrowMoney",  convert.FourToFive(""+doubleReceiptTotalSum,0)) ;
            if(doubleReceiptTotalSum <  0) {
                messagebox(" [�ұo�`���B�X�p] ���j��s�C") ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                return  false ;
            }
            if(doubleServiceAMT>0  &&  doubleServiceAMT  !=  doubleReceiptTotalSum) {
                messagebox(" [�ұo�`���B�X�p] ������ [�S�e�`���B]�C") ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                return  false ;
            }
            if(doubleRealMoneySum  !=  doubleReceiptTotalSum) {
                messagebox(" [�ұo�`���B�X�p] ������ [�O�Ϊ��B�X�p]�C") ;
                jtabbedPane1.setSelectedIndex(intTable2Panel) ;
                return  false ;
            }
        } else if("B".equals(stringPayType)) {
            //�o��
            setTableData("Table2",  new  String[0][0]) ;    // ��ú
            setValue("BorrowMoney",  convert.FourToFive(""+doubleInvoiceTotalMoneySum,0)) ;
            if(doubleInvoiceTotalMoneySum <  0) {
                messagebox(" [�o���`���B�X�p] ���j��s�C") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
            if(doubleServiceAMT>0  &&  doubleServiceAMT!=doubleInvoiceTotalMoneySum) {
                messagebox(" [�o���`���B�X�p] ������ [�S�e�`���B]�C") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
            if(doubleRealMoneySum!=doubleInvoiceTotalMoneySum) {
                messagebox(" [�o���`���B�X�p] ������ [�O�Ϊ��B�X�p]�C") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
        } else {
            setTableData("Table1",  new  String[0][0]) ;    // �o��
            setTableData("Table2",  new  String[0][0]) ;    // ��ú
            // �ɴڪ��B
            String  stringBorrowMoney  =  getValue("BorrowMoney").trim() ;
            if("".equals(stringBorrowMoney)  ||  exeUtil.doParseDouble(stringBorrowMoney) == 0) {
                messagebox("[�ɴڪ��B] ���i���ťաC") ;
                getcLabel("BorrowMoney").requestFocus( ) ;
                return  false ;
            }
            if(doubleRealMoneySum  !=  exeUtil.doParseDouble(stringBorrowMoney)) {
                messagebox(" [�ɴڪ��B] ������ [�O�Ϊ��B�X�p]�C") ;
                jtabbedPane1.setSelectedIndex(intTable1Panel) ;
                return  false ;
            }
        }
        // ���ʸ�T���� Table 9
        System.out.println("���ʸ�T����---------------------------------------------S") ;
        if(!isTable9CheckOK(exeUtil,  exeFun))  return  false ;
        System.out.println("���ʸ�T����---------------------------------------------E") ;
        // �w���ˮ�
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
        getButton("ButtonTable9").doClick() ;   // �����дڳ椧[���ʩ��Ӷ���]�����B�M���|���B �X�p���@�P �O�Ϊ�椧���B�X�p�C(�۰ʳB�z)
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
            messagebox("[���ʳ�] ���T�w�겣�ɡA���� �u���\ [�o��]�C") ;
            return  false ;
        }
        talk           dbAsset                         =  getTalk(""+get("put_Asset")) ;
        String[][]  retAsAsset                    =  getAsAsset(exeUtil,  exeFun,  dbAsset) ;
        boolean   booleanExistAsset        =  (retAsAsset.length  >  0) ;
        if(booleanExistAsset) {
            messagebox("[���ʳ�-�t��] �w�s�b �T��t�ήɡA�����\�s�W �дڥӽЮѡC") ;
            return  false ;           
        }
        // 0 �C�b Y     1 �T��N�X      2. �X�p���B     3. ���|���B     4  ���~�~
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
            // [�T��N�X] ���� ���q-�|�p��ئs�b�ˮ֡C
            vectorAsAssetFilter  =  exeUtil.getQueryDataHashtable("AS_ASSET_FILTER",  new  Hashtable(),  " AND  FILTER  = '"+stringFilter+"' ",  vectorColumnName,  dbAsset) ;
            if(vectorAsAssetFilter.size()  ==  0) {
                messagebox("���ʳ�("+stringPurchaseNo+")��[�T��N�X]("+stringFilter+") ���s�b��Ʈw���C") ;
                return  false ;
            }
            hashtableTmp  =  (Hashtable) vectorAsAssetFilter.get(0) ;
            if(hashtableTmp  ==  null) {
                messagebox("���ʳ�("+stringPurchaseNo+")��[�T��N�X]("+stringFilter+") ��Ƶo�Ϳ��~�A�Ь���T�ǡC") ;
                return  false ;
            }
            if(!stringFilter.equals(stringFiletrDo)) {
                if("Y".equals(stringAssAccountAsset)) {
                    // �|�p��ئs�b�ˮ�
                    stringComNoAcctNo  =  ""+hashtableTmp.get("ANMAL_ACNTNO_SET") ;
                    if("null".equals(stringComNoAcctNo)  ||  "".equals(stringComNoAcctNo)) {
                        messagebox("���ʳ�("+stringPurchaseNo+")��[�T��N�X]("+stringFilter+") ���� [�C�b-�|�p���] ���ťաC") ;
                        return  false ;
                    }
                } else {
                    // �������q�������ˮ�
                    stringTemp  =  "SPEC_ACNTNO_SET_"+stringComNo ;
                    if("I".equals(stringInOut))  stringTemp  =  "SPEC_ACNTNO_SET_"+stringComNo+"_IN" ;
                    if(vectorColumnName.indexOf(stringTemp)  ==  -1) {
                        messagebox("���ʳ�("+stringPurchaseNo+")��[�T��N�X]("+stringFilter+") ���s�b���� [���q-�|�p���] ���C") ;
                        return  false ;
                    }
                    // �|�p��ئs�b�ˮ�
                    stringComNoAcctNo  =  ""+hashtableTmp.get(stringTemp) ;
                    if("null".equals(stringComNoAcctNo)  ||  "".equals(stringComNoAcctNo)) {
                        messagebox("���ʳ�("+stringPurchaseNo+")��[�T��N�X]("+stringFilter+") ���� [���q-�|�p���] ���ťաC") ;
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
            // ���o ���ʤ��T��N�X
            stringSql         =  " SELECT  M12.FILTER "  +
                           " FROM  Doc3M012 M12,  Doc3M011 M11 "  +
                           " WHERE  M12.BarCode  =  M11.BarCode "  +
                             " AND  M11.ComNo  =  '"    +   stringComNo                +  "' "  +
                             " AND  M11.DocNo  =  '"      +  stringPurchaseNo         +  "' "  +
                             " AND  M11.KindNo  =  '"     +  stringKindNoPurchase  +  "' "  +
                             " AND  M12.RecordNo  =  "  +  stringRecordNo12        +  " "  ;
            retDoc3M012  =  exeFun.getTableDataDoc(stringSql) ;
            if(retDoc3M012.length  ==  0) {
                System.out.println("�ˬd ���ʳ椧�T��N�X �ɡA�o�Ϳ��~�A�Ь���T�ǡC") ;
                return  new  String[0][0] ;
            }
            stringFilter    =  retDoc3M012[0][0].trim() ;
            stringInOut   =  exeFun.getInOutVoucher("C",  stringRecordNo12,  stringComNo,  stringKindNoPurchase,  stringPurchaseNo,  exeUtil) ;
            if("".equals(stringFilter))  continue ;
            // 0 �C�b Y     1 �T��N�X      2. �X�p���B     3. ���|���B     4  ���~�~
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
            if("".equals(stringSSMediaID)  ||  "�@".equals(stringSSMediaID)) {
                getButton("ButtonTableElse").doClick() ;
                return  doTable6ErrorAction(-1,  "�O�ι�ӳq���N�X��� �� "+(intNo+1)+" �C�� [�q���N�X]���i���ťաC") ;
            }
            if(vectorRecordNo.indexOf(stringRecordNo)  !=  -1)  continue ;
            vectorRecordNo.add(stringRecordNo) ;
            // �O�Ϊ�� ���B�@�P�ˮ�
            doubleRealTotalMoney6    =  getTable6MoneySum (stringRecordNo,  exeUtil) ;
            doubleRealTotalMoney22  =  getTable22MoneySum (stringRecordNo,  exeUtil) ;
            System.out.println("doubleRealTotalMoney2("+convert.FourToFive(""+doubleRealTotalMoney6,  0)+")doubleRealTotalMoney22("+convert.FourToFive(""+doubleRealTotalMoney22,  0)+")-----------------------------------") ;
            if(doubleRealTotalMoney6  !=  doubleRealTotalMoney22) {
                getButton("ButtonTableElse").doClick() ;
                return  doTable6ErrorAction(-1,  "�O�ι�ӳq���N�X��� �� "+(intNo+1)+" �C���O�Ϊ��������B�X�p("+exeUtil.getFormatNum2(""+doubleRealTotalMoney6)+") ���@�P�C(���X�p�G"+exeUtil.getFormatNum2(""+doubleRealTotalMoney22)+")�C") ;
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
    
    // ���ʶ��ت��
    public  boolean  isTable9CheckOK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        // �L���ʳ� �M��
        String  stringPurchaseNoExist  =  getValue("PurchaseNoExist").trim() ;
        if("N".equals(stringPurchaseNoExist)) {
            setTableData("Table9",  new  String[0][0]) ;
            return  true ;
        }
        if(!isNewVersion (exeFun)) {
            setTableData("Table9",  new  String[0][0]) ;
            return  true ;        
        }
        // �P �O�Ϊ�� ���B�@�P
        double    doubleRealTotalMoneySum        =  getTableMoneySum("Table6",  "RealTotalMoney",  exeUtil) ;   
        double    doublePurchaseMoneySum        =  getTableMoneySum("Table9",  "PurchaseMoney",  exeUtil) ;   
        if(doubleRealTotalMoneySum  !=  doublePurchaseMoneySum) {
            messagebox("[���ʶ��ت��]���B�X�p ������ [�O�Ϊ��]���B�X�p�C") ;
            return  false ;
        }
        getButton("ButtonTable9").doClick() ;//Table9 �L�|���B(�۰ʵ���)
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
    // �D (���ʳ欰�T�� �� �e������J����� �� �Ĥ@���ӽФH�`) ���@�B�z
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
        if(stringFunction.indexOf("ñ��")  !=  -1)  stringEmpDeptCd = "033" ;
        //
        return  exeFun.isNewVersion (stringComNo,  stringKindNo,   stringPurchaseNo,  stringFactoryNo,  stringEmpDeptCd,  jtable9) ;
    }
    public  boolean  isBudgetMoneyTable6OK(FargloryUtil  exeUtil,  Doc2M010  exeFun) throws  Throwable {
        boolean            booleanPurchaseExist                  =  "Y".equals(getValue("PurchaseNoExist").trim( )) ? true :  false ;  // false��ܥi�H�L���ʳ�  
        if(booleanPurchaseExist)  return  true ;
        //
        getButton("ButtonTableCheck").doClick() ;
        String[][]  retTableData  =  getTableData("TableCheck") ;
        if(retTableData.length==0) {
            return  doTable6ErrorAction(-1,  "��Ƶo�Ϳ��~�A�Ь� [��T������]�C") ;
        }

        if(retTableData.length==1  &&  "OK".equals(retTableData[0][0]))     return  true ;
          return  false ;
    }
    
    
    
    // ��ǲ�
    // stringType  A ��P   B �޲z�O��    C �g�a�}�o-�R��
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
            retTable[0][0]  =  "�d�L��ơC" ;
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
        // ��J����
        doInputDepartNo(stringType,  hashtableDoc6M010,  exeUtil,  exeFun,  vectorItemCd) ;
        // �t��
        doFactoryNo(hashtableDoc6M010,  vectorDoc6M011,  vectorDoc6M013,  exeUtil) ;
        System.out.println("---------------------�ɤ�" );
        putDebit("1281",  hashtableDoc6M010,  vectorDoc6M011,  vectorDoc6M013,  vectorItemCd,  exeUtil,  exeFun,  retVector) ;
        // �o��
        putDebitForInvoice(hashtableDoc6M010,  vectorDoc6M011,  exeUtil,  exeFun,  retVector) ;
        System.out.println("---------------------�U����") ;
        // �ӤH����
        putDebitForDoc6M013(hashtableDoc6M010,  vectorDoc6M013,  exeUtil,  exeFun,  retVector) ;
        putCedit(hashtableDoc6M010,  vectorDoc6M013,  exeUtil,  exeFun,  retVector) ;
        // �ഫ
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
        // ���Ӥ@�B���ӤG�B���ӤT�B���ӥ|�B���Ӥ� 18-22
        arrayDescription  = getDescriptionUnion(stringDestineExpenseDate,  stringAccountNo,  stringOriEmployeeNo,  retFED1004,  vectorItemCd,  exeUtil,  exeFun) ;
        //
        arrayTemp[2]    =   "N" ;                           // �o��M  �ɤ�N   �U�� O  ��ú R
        arrayTemp[3]    =   "1" ;
        arrayTemp[9]    =  "D" ;                                                   // �ɶU            9
        arrayTemp[10]  =  stringAccountNo ;               // �|�p���         10
        arrayTemp[11]  =  stringInputDepartNo ;               // ����             11
        arrayTemp[12]  =   stringFactoryNo ;                       // ��H            12
        arrayTemp[13]  =  convert.FourToFive(""+ doubleDebit,0) ;   // ���B             13
        arrayTemp[18]  =  arrayDescription[0] ;                         // ���Ӥ@                18
        arrayTemp[19]  =  arrayDescription[1] ;                       // ���ӤG                19
        arrayTemp[20]  =  arrayDescription[2] ;                           // ���ӤT                20
        arrayTemp[21]  =  arrayDescription[3] ;                           // ���ӥ|                21
        arrayTemp[22]  =  arrayDescription[4] ;                       // ���Ӥ�                22
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
            arrayTemp[2]    =   "M" ;                           // �o��M  �ɤ�N   �U�� O  ��ú R
            arrayTemp[3]    =   ""+(intNo+1) ;
            arrayTemp[9]    =  "D" ;                                                   // �ɶU            9
            arrayTemp[10]  =  stringAccountNo ;               // �|�p���         10
            arrayTemp[11]  =  stringInputDepartNo ;               // ����             11
            arrayTemp[12]  =   stringFactoryNo ;                       // ��H            12
            arrayTemp[13]  =  stringInvoiceTax ;                  // ���B             13
            arrayTemp[18]  =  arrayDescription[0] ;                         // ���Ӥ@                18
            arrayTemp[19]  =  arrayDescription[1] ;                       // ���ӤG                19
            arrayTemp[20]  =  arrayDescription[2] ;                           // ���ӤT                20
            arrayTemp[21]  =  arrayDescription[3] ;                           // ���ӥ|                21
            arrayTemp[22]  =  arrayDescription[4] ;                       // ���Ӥ�                22
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
        //  ���Ӹ�ƳB�z(�w�]�� B08�BB09�BB10�BB11�BB21�A�D�W�z�� ITEM_CD �ɡA�ťճB�z)
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
            // 1  ��ú����      2 �ɥR�O�O
            for(int  intNoL=1  ;  intNoL<=2  ;  intNoL++) {
                if(intNoL  ==  1) {
                      stringAcctTax  =  !"".equals(stringAcctNo) ? stringAcctNo : "228203" ;
                      stringAmt         =  stringReceiptTax ;         // ���B
                      stringRowType =  "R" ;
                } else {
                      stringAcctTax   =  "228231" ;               // �|�p���
                      stringAmt         =  stringSupplementMoney ;    // ���B
                      stringRowType =  "Z" ;
                }
                if(exeUtil.doParseDouble(stringAmt)  ==  0)  continue ;
                //
                retFED1004                  =  exeFun.getFED1004(stringAcctTax) ;
                //  ���Ӹ�ƳB�z G08�BB04�BG07�B�A�D�W�z�� ITEM_CD �ɡA�ťճB�z
                for(int  intL=0  ;  intL<5  ;  intL++) {
                    arrayDescription[intL]  =  ""  ;
                    if(intL  >=  retFED1004.length)  continue ; 
                    if("G08".equals(retFED1004[intL][0].trim( )))  {
                        if(!"".equals(stringReceiptDate)) {
                            arrayDescription[intL]  =  convert.replace(stringReceiptDate,  "/",  "") ;  // �ұo���I���ڨ����
                        } else {
                            if(!"".equals(stringVoucherYMD014)) {
                                arrayDescription[intL]  =  exeFun.getExpiredDateUnion((intPos13),                 stringAcctTax,            stringVoucherYMD014,  
                                                                                  stringPayCondition1,  stringPayCondition2,  "",
                                                                              stringComNo,              exeUtil) ;
                                arrayDescription[intL]   =  convert.replace(arrayDescription[intL].trim(),  "/",  "") ;
                            }
                        }
                    }
                    if("G20".equals(retFED1004[intL][0].trim( )))  arrayDescription[intL]  =  stringReceiptTotalMoney ;  // G20 �ұo�`�B
                    if("G06".equals(retFED1004[intL][0].trim( )))  arrayDescription[intL]  =  stringReceiptTotalMoney ;  // 
                    if("G07".equals(retFED1004[intL][0].trim( )))  arrayDescription[intL]  =  stringAmt ;  // 
                    if("228203".equals(stringAcctTax)  &&  "C07".equals(retFED1004[intL][0].trim( ))) arrayDescription[intL]  =  "1281" ;
                }
                arrayTemp[2]    =   stringRowType ;                   // �o��M  �ɤ�N   �U�� O  ��ú R
                arrayTemp[3]    =   ""+(intNo+1) ;
                arrayTemp[9]    =  "C" ;                                                   // �ɶU            9
                arrayTemp[10]  =  stringAcctTax ;                  // �|�p���        10
                arrayTemp[11]  =  stringInputDepartNo ;               // ����             11
                arrayTemp[12]  =   stringFactoryNo ;                       // ��H            12
                arrayTemp[13]  =  stringAmt ;                         // ���B             13
                arrayTemp[16]  =  stringReceiptMoney ;                // �������|���B     16
                arrayTemp[18]  =  arrayDescription[0] ;                         // ���Ӥ@                18
                arrayTemp[19]  =  arrayDescription[1] ;                       // ���ӤG                19
                arrayTemp[20]  =  arrayDescription[2] ;                           // ���ӤT                20
                arrayTemp[21]  =  arrayDescription[3] ;                           // ���ӥ|                21
                arrayTemp[22]  =  arrayDescription[4] ;                       // ���Ӥ�                22
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
                    arrayTempL[2]    =  stringRowType+stringFlow ;                                                      // �o��-��A  �O��-��B  �UC  ����-�fD  �l�q-�UE ��ú-�UF  ��ú-�UG(�|)  ����(�|)-�UH  �h�O�d��-�UI
                    // ���
                    for(int  intL=0  ;  intL<5  ;  intL++) {
                        if( intL  >=  retFED1004.length) continue ;
                        stringItemCd  =  retFED1004[intL][0].trim( ) ;
                        // ����� G08
                        if("G08".equals(stringItemCd)) {
                            stringDate  =   arrayTemp[18+intL].trim() ;
                            if(!"".equals(stringDate)) {
                                arrayTempL[18+intL]  =  datetime.dateAdd(stringDate,  "m",  intCount) ;
                            }
                        }
                        // G20 �ұo�`�B
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
                    arrayTempL[2]    =  stringRowType+stringFlow ;                                         // �o��-��A  �O��-��B  �UC  ����-�fD  �l�q-�UE  ��ú-�UF  ��ú-�UG(�|)  ����(�|)-�UH  �h�O�d��-
                    arrayTempL[13]  =  convert.FourToFive(""+doubleMoney, 0) ;              // ���B           13
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
            // �|�p���
            if(intDoc2M030  <  retDoc2M030.length) stringAcctC  =  retDoc2M030[intDoc2M030][0].trim( ) ;
            if(!"null".equals(stringAcctS))  stringAcctC  =  stringAcctS ;
            // ���Ӥ@�ܤ�
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
            // ��J�}�C��
            arrayDataTemp[2]    =  "O" ;                            // �o��M  �ɤ�N   �U�� O  �l�q P    ��ú R
            arrayDataTemp[3]    =  ""+(intDoc2M030+1) ;                          // No              3
            arrayDataTemp[9]    =  "C" ;                                              // �ɶU             9
            arrayDataTemp[10]  =  stringAcctC ;                         // �|�p���         10
            arrayDataTemp[11]  =  "110301".equals(stringAcctC)?"":stringInputDepartNo ;                 // ����             11
            arrayDataTemp[12]  =   stringFactoryNoL ;                     // ��H             12
            arrayDataTemp[13]  =  convert.FourToFive(stringMoney, 0) ; // ���B            13
            arrayDataTemp[18]  =  arrayDescription[0] ;                         // ���Ӥ@                18
            arrayDataTemp[19]  =  arrayDescription[1] ;                       // ���ӤG                19
            arrayDataTemp[20]  =  arrayDescription[2] ;                            // ���ӤT                 20
            arrayDataTemp[21]  =  arrayDescription[3] ;                            // ���ӥ|                 21
            arrayDataTemp[22]  =  arrayDescription[4] ;                         // ���Ӥ�              22
            //
            if("110301".equals(stringAcctC))  arrayDataTemp[12] =  "0095289" ;
            //
            if(intAcctountCount  <=  1) {
                retVector.add(arrayDataTemp) ;
                continue ;
            }
            // ���q������
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
            // ���q����ú����
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
                arrayTemp[2]    =  "O"+stringFlow ;                                        // �o��-��A  �O��-��B  �UC  ����-�fD  �l�q-�UE  ��ú-�UF  ��ú-�UG(�|)  ����(�|)-�UH  �h�O�d��-
                // ���
                for(int  intNo=0  ;  intNo<5  ;  intNo++) {
                    if( intNo  >=  retFED1004.length) continue ;
                    stringItemCd  =  retFED1004[intNo][0].trim( ) ;
                    // ����� A04
                    if("A04".equals(stringItemCd)  &&  !"".equals(stringVoucherYMD014)) {
                        stringDate                   =    convert.replace(arrayTemp[18+intNo].trim(),  "/",  "") ;
                        arrayTemp[18+intNo]  =  datetime.dateAdd(stringDate,  "m",  intCount) ;
                    }
                }
                //
                arrayTemp[13]  =  convert.FourToFive(""+doubleMoney, 0) ;              // ���B          13
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
            retDataTemp[0]    =   stringBarCode ;                 // ���X�s��         0
            retDataTemp[1]    =   stringDocNo ;                   // ����N��         1
            retDataTemp[4]    =   stringVoucherYMD014 ;           // �ǲ����         4
            retDataTemp[5]    =   "0" ;                                                 // �ǲ��y����            5
            retDataTemp[6]    =   ""  +  (intNo+1) ;                   // �ǲ��Ǹ�        6
            retDataTemp[7]    =  stringComNo ;                  // ���q�N��         7
            retDataTemp[8]    =  stringKind ;                     // Kind             8
            retDataTemp[14]  =   "0" ;                          // �ײv             14
            retDataTemp[15]  =   "0" ;                           // �������B        15
            retDataTemp[16]  =   "0" ;                                                // �дڪ��B         16
            retDataTemp[17]  =   "U" ;                        // ���A-���L�b       17
            retDataTemp[23]  =  stringDescriptL ;                 // �K�n             23
            retDataTemp[24]  =  stringUser  ;                   // �ק��            24
            retDataTemp[25]  =  stringTodayL  ;                 // �ק���         25
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
        if(stringProjectID1.equals("M51A"))    stringProjectID1  =  "M51" ;// �S�ҡA�����®פl�A�|�y����P�M�]�Ȥ��@�P�A�G�{�����ק�
        if(stringProjectID1.equals("H51A"))     stringProjectID1  =  "H51" ;// �S�ҡA�����®פl�A�|�y����P�M�]�Ȥ��@�P�A�G�{�����ק�
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
            //if(stringInputDepartNo.equals("033GT"))           stringInputDepartNo  =  "033H121A"; //2013-09-14 ���ন 033H115A �令 033H121A
        }
        // ��
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
        return "---------------�s�W���s�{��.preProcess()----------------";
    }
}




