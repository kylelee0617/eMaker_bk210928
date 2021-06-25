package Sale.Sale05M274;

import jcx.jform.bNotify;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AfterNew extends bNotify{
  public void actionPerformed(String value)throws Throwable{
    // 當執行完 Transaction 時,會執行本段程式
    //可用以寄發Email通知或是自動再處理自定Transaction
    talk  dbSale  =  getTalk(""+get("put_dbSale"));
    String stringSql                 = "";
    String retData[][]               = null;
    String stringProjectID1      = getValue("ProjectID1").trim();
    String stringContractDate = convert.ac2roc(getValue("ContractDate").trim().replaceAll("/",""));
    String stringContractNo    = getValue("ContractNo").trim();
    String stringCompanyCd  = getValue("CompanyCd").trim(); // 修改日期:20120517 員工編號:B3774
    //
    stringSql = "select count(ContractNo) "+
              "from Sale05M274_FLOWC_HIS "+
              // Start 修改日期:20120517 員工編號:B3774
              //"where ContractNo='"+stringContractNo+"'";
              "where ContractNo='"+stringContractNo+"' "+
              "and CompanyCd='"+stringCompanyCd+"'";
              // and 修改日期:20120517 員工編號:B3774
    retData = dbSale.queryFromPool(stringSql);
    if(Integer.parseInt(retData[0][0]) > 1){
      return;
    }
    //
    talk  dbFE3D  =  getTalk(""+get("put_dbFE3D"));
    talk  dbDoc     =  getTalk(""+get("put_dbDoc"));
    // Start 修改日期:20100625 員工編號:B3774
    //Sale.Sale05M27401  exeFun274  =  new  Sale.Sale05M27401();
    Sale.Sale05M27401_New  exeFun274  =  new  Sale.Sale05M27401_New();
    // End 修改日期:20100625 員工編號:B3774
    Sale.Sale05M22701  exeFun227  =  new  Sale.Sale05M22701();
    //
    int intProjEndPosition = 0;
    if(stringProjectID1.length() > 4){
      intProjEndPosition = 8;
    // Start 修改日期:20100203 員工編號:B3774
    }else if(stringProjectID1.length() == 4){
      intProjEndPosition = 7;
    // End 修改日期:20100203 員工編號:B3774
    // Start 修改日期:20110511 員工編號:B3774
    //}else{
    }else if(stringProjectID1.length() == 3){
    // End 修改日期:20110511 員工編號:B3774
      // Start 修改日期:20100203 員工編號:B3774
      //intProjEndPosition = 7;
      intProjEndPosition = 6;
      // End 修改日期:20100203 員工編號:B3774
    // Start 修改日期:20110511 員工編號:B3774
    }else{
      intProjEndPosition = 5;
    // End 修改日期:20110511 員工編號:B3774
    }
    //
    int intKindDay = 0;
    stringSql = "select KindDay "+
              // Start 修改日期:20110406 員工編號:B3774
              //"from Doc1M010 "+
              "from Doc1M011 "+
              // End 修改日期:20110406 員工編號:B3774
              "where KindNo='58'";
    retData = dbDoc.queryFromPool(stringSql);
    if(retData.length > 0){
      intKindDay = Integer.parseInt(retData[0][0]);
    }
    String stringBarCode                = getValue("BarCode").trim(); // 1
    String stringCDate                    = datetime.getToday("yy/mm/dd");
    // Start 修改日期:20100204 員工編號:B3774
    // Start 修改日期:20100603 員工編號:B3774
    String stringCTime                    = datetime.getTime("h:m:s");
    //String stringCTime                    = datetime.getTime("pm/am h點m分s秒").substring(0,2)+" "+datetime.getTime("h:m:s");
    // End 修改日期:20100603 員工編號:B3774
    // End 修改日期:20100204 員工編號:B3774
    String stringEDateTime             = datetime.getTime("YYYY/mm/dd h:m:s"); // 4
    String stringPreFinDate            = convert.FormatedDate(convert.roc2ac(datetime.dateAdd(stringCDate.replaceAll("/",""), "d", intKindDay)),"/");
    String stringKindNo                   = "58";
    String stringKindNoD                 = stringKindNo; // 修改日期:20100625 員工編號:B3774
    // Start 修改日期:20120517 員工編號:B3774
    //String stringComNo                   = getValue("CompanyCd").trim(); // 7
    String stringComNo                   = stringCompanyCd; // 7
    // End 修改日期:20120517 員工編號:B3774
    String stringDepartNo                = stringContractNo.substring(0, intProjEndPosition);
    // Start 修改日期:20100203 員工編號:B3774
    stringSql = "select DEPT_CD "+
              "from FE3D01 "+
              "where DEPT_CD='"+stringDepartNo+"'";
    retData = dbFE3D.queryFromPool(stringSql);
    if(retData.length == 0){
      stringSql = "select RTRIM(DEPT_CD) "+
                "from FE3D01 "+
                "where DEPT_CD='"+stringDepartNo.substring(0,stringDepartNo.length()-1)+"'";
      retData = dbFE3D.queryFromPool(stringSql);
      if(retData.length > 0){
        stringDepartNo = retData[0][0];
      }
    }
    // End 修改日期:20100203 員工編號:B3774
    //
    String stringEmployeeNo           = getUser();
    String stringDocNo1                  = stringDepartNo; // 10
    // Start 修改日期:20100621 員工編號:B3774
    //String stringDocNo2                  = stringContractNo.substring(intProjEndPosition, intProjEndPosition+stringContractDate.length());
    String stringDocNo2                  = datetime.getToday("yymm");
    // End 修改日期:20100621 員工編號:B3774
    String stringDocNo3                  = "";
    String stringDocNo                    = ""; // 13
    // Start 修改日期:20120517 員工編號:B3774
    //String stringDescript                 = exeFun274.getDescript(stringContractNo);
    String stringDescript                 = exeFun274.getDescript(stringContractNo, stringCompanyCd);
    // End 修改日期:20120517 員工編號:B3774
    String stringOriEmployeeName = exeFun227.getEmpName(stringEmployeeNo);
    String stringLastDepart             = stringDepartNo; // 16
    String stringLastDateTime         = stringEDateTime;
    String stringInOut                      = "";
    String stringProjectID                = ""; // 19
               stringProjectID1               = "";
    String stringCostID                    = "";
    String stringRealMoney             = ""; // 22
    String stringRemark                  = "";
    String stringRecordNo               = "";
    // 處理公文流水號
    // Start 修改日期:20170515 員工編號:B3774
    if("CS".equals(stringComNo)){
      stringComNo = "20";
    }
    // End 修改日期:20170515 員工編號:B3774
    //
    // Start 修改日期:20100120 員工編號:B3774
    /*
    stringSql = "select max(DocNo3)+1 "+
              "from Doc1M030 "+
              "where DocNo1='"+stringDocNo1+"' "+
              "and DocNo2='"+stringDocNo2+"'";
    retData = dbDoc.queryFromPool(stringSql);
    if("".equals(retData[0][0])){
      stringDocNo3 = "001";
    }else{
      stringDocNo3 = format.format(String.valueOf(Integer.parseInt(retData[0][0])+1), "000");
    }
    */
    stringSql = "select isnull(right(convert(char(4),1001+max(DocNo3)),3),'001') "+
              "from Doc1M030 "+
              "where KindNo ='58' "+
              // Start 修改日期:20100621 員工編號:B3774
              "and ComNo='"+stringComNo+"' "+
              "and hand = 'N' "+
              // End 修改日期:20100621 員工編號:B3774
              "and DocNo1='"+stringDocNo1+"' "+
              "and DocNo2='"+stringDocNo2+"'";
    retData = dbDoc.queryFromPool(stringSql);
    stringDocNo3 = retData[0][0];
    // End 修改日期:20100120 員工編號:B3774
    stringDocNo = stringDocNo1 + stringDocNo2 + stringDocNo3;
    //
    exeFun274.doInsertDoc1M030(stringBarCode,     stringCDate,            stringCTime, // 1
                              // Start 修改日期:20100625 員工編號:B3774
                              //stringEDateTime, stringPreFinDate,    stringKindNo, // 4
                              stringEDateTime, stringPreFinDate,    stringKindNo,  stringKindNoD, // 4
                              // End 修改日期:20100625 員工編號:B3774
                              stringComNo,       stringDepartNo,       stringEmployeeNo, // 8
                              stringDocNo1,      stringDocNo2,          stringDocNo3, // 11
                              stringDocNo,        stringDescript,          stringOriEmployeeName, // 14
                              stringLastDepart, stringLastDateTime, stringInOut, // 17
                              stringProjectID,    stringProjectID1,       stringCostID, // 20
                              stringRealMoney, stringRemark,           stringRecordNo); // 23
    //
    exeFun274.doInsertDoc1M040(stringBarCode,     stringCDate,      stringCTime, 
                              stringEDateTime, stringDepartNo, stringEmployeeNo);
    //
    // Start 修改日期:20091224 員工編號:B3774
    // Start 修改日期:20100108 員工編號:B3774
    String stringStatus             = getValue("Status").trim();
    // Start 修改日期:20100317 員工編號:B3774
    //if("N".equals(stringStatus)){
    // Start 修改日期:20151123 員工編號:B3774
    //if("N".equals(stringStatus) || "A".equals(stringStatus)){
    if("N".equals(stringStatus) || "NA".equals(stringStatus) || "A".equals(stringStatus)){
    // End 修改日期:20151123 員工編號:B3774
    // End 修改日期:20100317 員工編號:B3774
    // End 修改日期:20100108 員工編號:B3774
            stringProjectID1      = getValue("ProjectID1").trim();
            stringContractDate = getValue("ContractDate").trim();
      String stringCashInDate    = getValue("CashInDate").trim();
      stringSql = "select HouseCar, Position "+
                "from Sale05M278 "+
                // Start 修改日期:20120517 員工編號:B3774
                //"where ContractNo='"+stringContractNo+"'";
                "where ContractNo='"+stringContractNo+"' "+
                "and CompanyCd='"+stringCompanyCd+"'";
                // End 修改日期:20120517 員工編號:B3774
      retData = dbSale.queryFromPool(stringSql);
      for(int intRow=0; intRow<retData.length; intRow++){
        stringSql = "UPDATE A_Sale "+
                  "SET ContrDate=convert(datetime , '"+stringContractDate+"' , 21), "+
                      "DateRange=convert(datetime , '"+stringCashInDate+"' , 21) "+
                  "WHERE ID1=(SELECT ID1 "+
                             "FROM A_Sale "+
                             // Start 修改日期:20100412 員工編號:B3774
                             //"WHERE ProjectID1='"+stringProjectID1+"' ";
                             "WHERE ProjectID1='"+(stringProjectID1.equals("H38A")?"H38":stringProjectID1)+"' ";
                             // End 修改日期:20100412 員工編號:B3774
        if("House".equals(retData[intRow][0])){
          stringSql = stringSql +  "AND HouseCar='Position'"+
                             "AND Position='"+retData[intRow][1]+"')";
        }else if("Car".equals(retData[intRow][0])){
          stringSql = stringSql +  "AND HouseCar='Car'"+
                             "AND Car='"+retData[intRow][1]+"')";
        }
        dbSale.execFromPool(stringSql);
      }
    } // 修改日期:20100108 員工編號:B3774
    // End 修改日期:20091224 員工編號:B3774
    
    /*
    // 20191030 洗錢控管名單LOG
    System.out.println("==============洗錢防治檢核LOG START====================================") ;
    talk  dbJGENLIB  =  getTalk("JGENLIB");
    talk  dbEIP  =  getTalk("EIP");
    String errMsg = "";
    String stringSQL = "";
    //序號
    String orderNo = "";
    String[][] orderNoTable =  getTableData("table1");
    orderNo= orderNoTable[0][5].trim();
    System.out.println("orderNo====>"+orderNo) ;
    //合約編號
    System.out.println("合約編號ContractNo====>"+stringContractNo) ;
    //LOG日期,時間
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    String strNowDate = nowsdf.format(now);
    String tempROCYear=""+(Integer.parseInt(strNowDate.substring(0,strNowDate.length()-4))-1911);
    String RocNowDate = tempROCYear+strNowDate.substring(strNowDate.length()-4,strNowDate.length());
    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HH:mm:ss");
    String strNowTime = nowTimeSdf.format(now);
    strNowTime=strNowTime.replace(":","");
    //員工碼
    String userNo = getUser().toUpperCase().trim();
    String empNo="";
    String [][] retEip=null;
    stringSQL="SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'" ;
    retEip = dbEIP.queryFromPool(stringSQL);
    if(retEip.length>0){
      empNo=retEip[0][0] ;
    }
    //LOG 序號
    int intRecordNo =1;
    String[][]  ret05M070 ;
    stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='"+orderNo+"'";
    ret05M070 = dbSale.queryFromPool(stringSQL);
    if(!"".equals(ret05M070[0][0].trim())){
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim())+1;
    }
    System.out.println("intRecordNo====>"+intRecordNo) ;
    stringSQL = "SELECT ProjectID1,ContractDate FROM Sale05M274 WHERE ContractNo='"+stringContractNo+"'";
    String[][]  retSale = dbSale.queryFromPool(stringSQL);
    if(retSale.length > 0){//有合約記錄
      String strProjectID1 = retSale[0][0].trim();
      String strContractDate =  retSale[0][1].trim();
      
      //被委託人
      stringSQL = "SELECT IsChoose FROM Sale05M279 where ContractNo ='"+stringContractNo+"' and   ItemCd='O01' and ItemlsCd = 'Other2'";
      String[][]  retOther2 = dbSale.queryFromPool(stringSQL);
      if(retOther2.length > 0){//被委託人 START
        String strIsChoose =retOther2[0][0].trim() ;
        if("Y".equals(strIsChoose)){
          stringSQL = "SELECT TrusteeName,TrusteeId,Blacklist,Controllist,Stakeholder FROM Sale05M355 where ContractNo ='"+stringContractNo+"'";
          String[][]  retM355 = dbSale.queryFromPool(stringSQL);
          if(retM355.length > 0){
            for(int a=0; a <retM355.length;a++){//loopstart
              String strTrusteeName = retM355[0][0].trim();
              String strTrusteeId =retM355[0][1].trim();
              String strBlacklist =retM355[0][2].trim();
              String strControllist =retM355[0][3].trim();
              String strStakeholder =retM355[0][4].trim();
              //第8條
              stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+strTrusteeId+"','"+strTrusteeName+"','"+strContractDate+"','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
              dbSale.execFromPool(stringSQL);
              intRecordNo++;
              //AS400
              stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strTrusteeId+"', '"+strTrusteeName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
              dbJGENLIB.execFromPool(stringSQL);  
              if("".equals(errMsg)){
                errMsg ="簽約被委託人"+strTrusteeName+"為不動產銷售由第三方代理或繳款，系統檢核提示通報。";
              }else{
                errMsg =errMsg+"\n簽約被委託人"+strTrusteeName+"為不動產銷售由第三方代理或繳款，系統檢核提示通報。";
              }
              //黑名單
              if("Y".equals(strBlacklist)){   
                stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+strTrusteeId+"','"+strTrusteeName+"','"+strContractDate+"','RY','773','020','該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbSale.execFromPool(stringSQL);
                intRecordNo++;
                //AS400
                stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strTrusteeId+"', '"+strTrusteeName+"', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbJGENLIB.execFromPool(stringSQL);  
                if("".equals(errMsg)){
                  errMsg ="簽約被委託人為疑似黑名單對象，請覆核確認後，再進行後續交易。";
                }else{
                  errMsg =errMsg+"\n簽約被委託人為疑似黑名單對象，請覆核確認後，再進行後續交易。";
                }
              }
              //控管名單
              if("Y".equals(strControllist)){   
                stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+strTrusteeId+"','"+strTrusteeName+"','"+strContractDate+"','RY','773','017','該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbSale.execFromPool(stringSQL);
                intRecordNo++;
                //AS400
                stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strTrusteeId+"', '"+strTrusteeName+"', '773', '017', '該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbJGENLIB.execFromPool(stringSQL);  
                if("".equals(errMsg)){
                  errMsg ="簽約被委託人為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。";
                }else{
                  errMsg =errMsg+"\n簽約被委託人為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。";
                }
              }
              //制裁名單
              talk db400 = getTalk("400CRM");
              String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE IN ('X181','X171')) AND CUSTOMERID = '"+strTrusteeId+"' AND CUSTOMERNAME='"+strTrusteeName+"'";
              String retCList[][] = db400.queryFromPool(str400sql);
              if(retCList.length > 0) {
                stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+strTrusteeId+"','"+strTrusteeName+"','"+strContractDate+"','RY','773','018','該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbSale.execFromPool(stringSQL);
                intRecordNo++;
                //AS400
                stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strTrusteeId+"', '"+strTrusteeName+"', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbJGENLIB.execFromPool(stringSQL);  
                if("".equals(errMsg)){
                  errMsg ="簽約被委託人為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。";
                }else{
                  errMsg =errMsg+"\n簽約被委託人為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。";
                }
              }
              //利害關係人
              if("Y".equals(strStakeholder)){   
                stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+strTrusteeId+"','"+strTrusteeName+"','"+strContractDate+"','RY','773','019','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbSale.execFromPool(stringSQL);
                intRecordNo++;
                //AS400
                stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strTrusteeId+"', '"+strTrusteeName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbJGENLIB.execFromPool(stringSQL);  
                if("".equals(errMsg)){
                  errMsg ="簽約被委託人為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
                }else{
                  errMsg =errMsg+"\n簽約被委託人為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
                }
              }
            }//loop end
          }
        }
      }//被委託人 END
      //指定第三人
      stringSQL = "SELECT IsChoose FROM Sale05M279 where ContractNo ='"+stringContractNo+"' and   ItemCd='O01' and ItemlsCd = 'Other2'";
      String[][]  retOther44 = dbSale.queryFromPool(stringSQL);
      if(retOther44.length > 0){//指定第三人 START
        for(int b=0; b <retOther44.length;b++){//loopstart
          String strIsChoose =retOther2[0][0].trim() ;
          if("Y".equals(strIsChoose)){
            stringSQL = "SELECT DesignatedName,DesignatedId,Blacklist,Controllist,Stakeholder,ExportingPlace FROM Sale05M356 where ContractNo ='"+stringContractNo+"'";
            String[][]  retM356 = dbSale.queryFromPool(stringSQL);
            if(retM356.length > 0){
              for(int a=0; a <retM356.length;a++){//loopstart
                String strDesignatedName = retM356[0][0].trim();
                String strDesignatedId =retM356[0][1].trim();
                String strBlacklist =retM356[0][2].trim();
                String strControllist =retM356[0][3].trim();
                String strStakeholder =retM356[0][4].trim();
                String strExportingPlace =retM356[0][5].trim();
                //第12條
                stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+strDesignatedId+"','"+strDesignatedName+"','"+strContractDate+"','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbSale.execFromPool(stringSQL);
                intRecordNo++;
                //AS400
                stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strDesignatedId+"', '"+strDesignatedName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                dbJGENLIB.execFromPool(stringSQL);  
                if("".equals(errMsg)){
                  errMsg ="簽約指定第三人要求將不動產權利登記予第三人，系統檢核提示通報。請依洗錢防制作業辦理!!!";
                }else{
                  errMsg =errMsg+"\n簽約指定第三人要求將不動產權利登記予第三人，系統檢核提示通報。請依洗錢防制作業辦理!!!";
                }
                //資恐地區  
                stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='"+strExportingPlace+"'";
                String[][] retCZPF= dbJGENLIB.queryFromPool(stringSQL);
                if(retCZPF.length > 0){
                  String strCZ07 =retCZPF[0][0].trim();
                  if("優先法高".equals(strCZ07)){
                    stringSQL = "INSERT INTO Sale05M070 (DocNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','客戶係來自金融監督管理委員會函轉國際防制洗錢組織所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循國際防制洗錢組織建議之國家或地區。','"+strDesignatedId+"','"+strDesignatedName+"','"+strContractDate+"','RY','773','009','客戶係來自金融監督管理委員會函轉國際防制洗錢組織所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循國際防制洗錢組織建議之國家或地區。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                    dbSale.execFromPool(stringSQL);
                    intRecordNo++;
                    //AS400
                    stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strDesignatedId+"', '"+strDesignatedName+"', '773', '009', '客戶係來自金融監督管理委員會函轉國際防制洗錢組織所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循國際防制洗錢組織建議之國家或地區。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                    dbJGENLIB.execFromPool(stringSQL);
                    if("".equals(errMsg)){
                      errMsg ="簽約指定第三人係來自金融監督管理委員會函轉國際防制洗錢組織所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循國際防制洗錢組織建議之國家或地區。請依洗錢防制作業辦理!!!";
                    }else{
                      errMsg =errMsg+"\n簽約指定第三人係來自金融監督管理委員會函轉國際防制洗錢組織所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循國際防制洗錢組織建議之國家或地區。請依洗錢防制作業辦理!!!";
                    }
                  }
                }
                //黑名單
                if("Y".equals(strBlacklist)){   
                  stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+strDesignatedId+"','"+strDesignatedName+"','"+strContractDate+"','RY','773','020','該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                  dbSale.execFromPool(stringSQL);
                  intRecordNo++;
                  //AS400
                  stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strDesignatedId+"', '"+strDesignatedName+"', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                  dbJGENLIB.execFromPool(stringSQL);  
                  if("".equals(errMsg)){
                    errMsg ="簽約指定第三人為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。";
                  }else{
                    errMsg =errMsg+"\n簽約指定第三人為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。";
                  }
                }
                //控管名單
                if("Y".equals(strControllist)){   
                  stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+strDesignatedId+"','"+strDesignatedName+"','"+strContractDate+"','RY','773','017','該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                  dbSale.execFromPool(stringSQL);
                  intRecordNo++;
                  //AS400
                  stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strDesignatedId+"', '"+strDesignatedName+"', '773', '017', '該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                  dbJGENLIB.execFromPool(stringSQL);  
                  if("".equals(errMsg)){
                    errMsg ="簽約指定第三人為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。";
                  }else{
                    errMsg =errMsg+"\n簽約指定第三人為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。";
                  }
                  //制裁名單
                  talk db400 = getTalk("400CRM");
                  String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE IN ('X181','X171')) AND CUSTOMERID = '"+strDesignatedId+"' AND CUSTOMERNAME='"+strDesignatedName+"'";
                  String retCList[][] = db400.queryFromPool(str400sql);
                  if(retCList.length > 0) {
                    stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+strDesignatedId+"','"+strDesignatedName+"','"+strContractDate+"','RY','773','018','該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                    dbSale.execFromPool(stringSQL);
                    intRecordNo++;
                    //AS400
                    stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strDesignatedId+"', '"+strDesignatedName+"', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                    dbJGENLIB.execFromPool(stringSQL);  
                    if("".equals(errMsg)){
                      errMsg ="簽約指定第三人為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。";
                    }else{
                      errMsg =errMsg+"\n簽約指定第三人為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。";
                    }
                  }
                }
                //利害關係人
                if("Y".equals(strStakeholder)){   
                  stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,Func,RecordType,RecordDesc,CustomID,CustomName,CDate,SHB00,SHB06A,SHB06B,SHB06, SHB97, SHB98, SHB99) VALUES ('"+orderNo+"','"+strProjectID1+"','"+intRecordNo+"','合約會審','附件-被委託人','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+strDesignatedId+"','"+strDesignatedName+"','"+strContractDate+"','RY','773','019','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                  dbSale.execFromPool(stringSQL);
                  intRecordNo++;
                  //AS400
                  stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+orderNo+"', '"+RocNowDate+"', '"+strDesignatedId+"', '"+strDesignatedName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
                  dbJGENLIB.execFromPool(stringSQL);  
                  if("".equals(errMsg)){
                    errMsg ="簽約指定第三人為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
                  }else{
                    errMsg =errMsg+"\n簽約指定第三人為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
                  }
                }
              }//loop end
            }
          }
        }//loop end
      }//指定第三人 END
    }
    if(!"".equals(errMsg)){
      setValue("errMsgBoxText",errMsg); 
      getButton("errMsgBoxBtn").doClick();
      return;
    }
    System.out.println("==============洗錢防治檢核LOG START====================================") ;
    */
    
    //洗錢防治檢核LOG
    setValue("text11","新增");  
    getButton("AML").doClick();
    getButton("CheckRiskNew2021").doClick(); //計算風險值

    // Start 修改日期:20091223 員工編號:B3774
    Hashtable hash = new Hashtable();
    hash.put("table17.CmpContractNo", getValue("ContractNo").trim());
    action(2, hash);
    //
    getButton("btnReSizeFileNameField").doClick(); // 修改日期:20091225 員工編號:B3774
    //
    // End 修改日期:20091223 員工編號:B3774
    return;
  }
  public String getInformation(){
    return "---------------\u627f\u8fa6.Notify()----------------";
  }
}
