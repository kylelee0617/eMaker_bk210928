package Sale.Sale05M090;

import javax.swing.*;
import jcx.jform.bproc;
import cLabel;
import jcx.jform.bNotify;
import jcx.jform.bBase;
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

public class AML extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    // 2019/10/23 洗錢 控管名單LOG
    System.out.println("==============洗錢防治檢核LOG START====================================");
    talk dbSale05M = getTalk("Sale");
    talk dbJGENLIB = getTalk("JGENLIB");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    String stringSQL = "";
    String strBDaysql = "";
    String errMsg = "";
    String[][] retPat001;
    String[][] retPat002;
    String[][] retPat003;
    String[][] retPat004;
    String[][] retQueryLog;
    String strOrderNo = getValue("field3").trim();
    String stringProjectID1 = getValue("field1").trim();
    String stringOrderDate = getValue("field2").trim();
    String strActionName = getValue("actionText").trim();
    System.out.println("strOrderNo=====>" + strOrderNo);
    System.out.println("stringProjectID1=====>" + stringProjectID1);
    System.out.println("stringOrderDate=====>" + stringOrderDate);
    System.out.println("strActionName=====>" + strActionName);
    // 訂戶姓名
    String coutsomName = "";
    String[][] orderNoTable = getTableData("table1");
    for (int g = 0; g < orderNoTable.length; g++) {
      if ("".equals(coutsomName)) {
        coutsomName = orderNoTable[g][6].trim();
      } else {
        coutsomName = coutsomName + "、" + orderNoTable[g][6].trim();
      }
    }
    // LOG日期,時間
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    String strNowDate = nowsdf.format(now);
    String tempROCYear = "" + (Integer.parseInt(strNowDate.substring(0, strNowDate.length() - 4)) - 1911);
    String RocNowDate = tempROCYear + strNowDate.substring(strNowDate.length() - 4, strNowDate.length());
    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HHmmss");
    String strNowTime = nowTimeSdf.format(now);
    SimpleDateFormat nowTimestampSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String strNowTimestamp = nowTimestampSdf.format(now);
    // System.out.println("strNowTimestamp=====>"+strNowTimestamp) ;
    // System.out.println("RocNowDate=====>"+RocNowDate) ;
    // System.out.println("strNowTime=====>"+strNowTime) ;
    // 員工碼
    String userNo = getUser().toUpperCase().trim();
    String empNo = "";
    String[][] retEip = null;
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }
    // System.out.println("empNo=====>"+empNo) ;
    // 序號
    int intRecordNo = 1;
    String[][] ret05M070;
    stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + getValue("OrderNo").trim() + "'";
    ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    // System.out.println("intRecordNo=====>"+intRecordNo) ;
    // actionNo
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    String strActionNo = strNowDate + strNowTime + ram;
    // System.out.println("strActionNo=====>"+strActionNo) ;
    System.out.println("==============客戶資料==============");
    stringSQL = "SELECT CountryName,CustomNo,CustomName,IsBlackList,IsControlList,IsLinked,Birthday FROM Sale05M091 WHERE ORDERNO = '" + strOrderNo + "' "
        + "and ISNULL(StatusCd , '') = '' ";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    ret05M070 = getTableData("table1");
    if (ret05M070.length > 0) {
      for (int m = 0; m < ret05M070.length; m++) {// 同一訂單下
        if ("C".equals(ret05M070[m][23].trim())) {// 換名的不看
          continue;
        }
        String strCountryName = ret05M070[m][4].trim();
        String strCustomNo = ret05M070[m][5].trim();
        String strCustomName = ret05M070[m][6].trim();
        String strIsBlackList = ret05M070[m][21].trim();
        String strIsControlList = ret05M070[m][20].trim();
        String strIsLinked = ret05M070[m][19].trim();
        String strBirthday = ret05M070[m][8].trim();

        // 不適用LOG1~8
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 5
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 8
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // 制裁名單
        talk db400 = getTalk("400CRM");
        String str400sql = "";
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        } else {
          int x = strBirthday.indexOf("/");
          if (x > -1) {
            strBirthday = strBirthday.replace("/", "-");
          } else {
            String yyyy = strBirthday.substring(0, 4);
            String mm = strBirthday.substring(4, 6);
            String dd = strBirthday.substring(6, 8);

            strBirthday = yyyy + "-" + mm + "-" + dd;
          }
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + strBirthday + "' )";
        }
        String retCList[][] = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','客戶" + strCustomName
              + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','018','客戶" + strCustomName
              + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "客戶" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
          } else {
            errMsg = errMsg + "\n客戶" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','018','客戶" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 制裁名單171
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        } else {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + strBirthday + "' )";
        }
        String ret171List[][] = db400.queryFromPool(str400sql);
        if (ret171List.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','客戶" + strCustomName
              + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','021','客戶" + strCustomName
              + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '021', '客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "客戶" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
          } else {
            errMsg = errMsg + "\n客戶" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','021','客戶" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 資恐地區
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("優先法高".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','客戶" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','客戶" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + getValue("OrderNo").trim() + "', '"
                + RocNowDate + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            if ("".equals(errMsg)) {
              errMsg = "客戶" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。";
            } else {
              errMsg = errMsg + "\n客戶" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。";
            }
          } else {
            // 不符合
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','客戶" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }
        // 黑名單&控管名單
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','客戶" + strCustomName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','客戶" + strCustomName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "客戶" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
          } else {
            errMsg = errMsg + "\n客戶" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','客戶" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 利害關係人
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','客戶" + strCustomName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','客戶" + strCustomName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "客戶" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
          } else {
            errMsg = errMsg + "\n客戶" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','客戶" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        //// 不適用LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','交易最終受益人或交易人為金融監督管理委員會函轉外國政府所提供之恐怖分子或團體；或國際洗錢防制組織認定或追查之恐怖組織；或交易資金疑似或有合理理由懷疑與恐怖活動、恐怖組織或資助恐怖主義有關聯者，應依資恐防制法進行相關作業。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
      } // for end
    }
    System.out.println("============================");
    System.out.println("==============實質受益人==============");
    // stringSQL = "SELECT
    // CountryName,BCustomNo,BenName,IsBlackList,IsControlList,IsLinked,Birthday
    // FROM Sale05M091BEN WHERE ORDERNO = '"+strOrderNo+"' "
    // + "and ISNULL(StatusCd , '') = '' ";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    ret05M070 = getTableData("table6");
    if (ret05M070.length > 0) {// 實質受益人 START
      for (int m = 0; m < ret05M070.length; m++) {// 同一訂單下
        String strCountryName = ret05M070[m][6].trim();
        String strCustomNo = ret05M070[m][4].trim();
        String strCustomName = ret05M070[m][3].trim();
        String strIsBlackList = ret05M070[m][8].trim();
        String strIsControlList = ret05M070[m][9].trim();
        String strIsLinked = ret05M070[m][10].trim();
        String strBirthday = ret05M070[m][5].trim();
        // 20200707 Kyle : 過濾已被換名的實受人
        String strStatusCd = getValueAt("table6", m, "StatusCd").toString().trim();
        if ("C".equals(strStatusCd))
          continue;

        // 不適用LOG1~8
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 5
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 8
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // 制裁名單
        talk db400 = getTalk("400CRM");
        String str400sql = "";
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        } else {
          if (strBirthday.indexOf("/") == -1) {
            String yyyy = strBirthday.substring(0, 4);
            String MM = strBirthday.substring(4, 6);
            String dd = strBirthday.substring(6, 8);
            strBirthday = yyyy + "-" + MM + "-" + dd;
          } else {
            strBirthday = strBirthday.replace("/", "-");
          }
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + strBirthday + "' )";
        }
        String retCList[][] = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
              + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','018','實質受益人" + strCustomName
              + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "實質受益人" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
          } else {
            errMsg = errMsg + "\n實質受益人" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','018','實質受益人" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 制裁名單171
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        } else {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '"
              + strBirthday + "' )";
        }
        String ret171List[][] = db400.queryFromPool(str400sql);
        if (ret171List.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
              + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','021','實質受益人" + strCustomName
              + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '021', '客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "實質受益人" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
          } else {
            errMsg = errMsg + "\n實質受益人" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','021','實質受益人" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        // 資恐地區
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("優先法高".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','實質受益人" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','" + empNo + "','" + RocNowDate
                + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            if ("".equals(errMsg)) {
              errMsg = "實質受益人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。";
            } else {
              errMsg = errMsg + "\n實質受益人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。";
            }
          } else {
            // 不符合
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','實質受益人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','009','客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        // 黑名單&控管名單
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','實質受益人" + strCustomName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "實質受益人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
          } else {
            errMsg = errMsg + "\n實質受益人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','實質受益人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 利害關係人
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','實質受益人" + strCustomName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "實質受益人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
          } else {
            errMsg = errMsg + "\n實質受益人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','實質受益人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        //// 不適用LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','交易最終受益人或交易人為金融監督管理委員會函轉外國政府所提供之恐怖分子或團體；或國際洗錢防制組織認定或追查之恐怖組織；或交易資金疑似或有合理理由懷疑與恐怖活動、恐怖組織或資助恐怖主義有關聯者，應依資恐防制法進行相關作業。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
      } // for end
    } // 實質受益人 END
    System.out.println("============================");

    System.out.println("==============代理人==============");
    // Kyle : 20200820後刪除
    // stringSQL = "SELECT a.CountryName, a.ACustomNo, a.AgentName, a.IsBlackList,
    // a.IsControlList, a.IsLinked, a.AgentRel, b.CustomName "
    // +"FROM Sale05M091Agent a , Sale05M091 b WHERE a.orderNo = b.orderNo and
    // b.customNo = a.CustomNo and a.ORDERNO = '"+strOrderNo+"' "
    // + "and ISNULL(a.StatusCd , '') = '' ";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    ret05M070 = getTableData("table10");
    if (ret05M070.length > 0) { // 代理人 START
      for (int m = 0; m < ret05M070.length; m++) {// 同一訂單下
        String strCountryName = ret05M070[m][5].trim();
        String strCustomNo = ret05M070[m][4].trim();
        String strCustomName = ret05M070[m][3].trim();
        String strIsBlackList = ret05M070[m][8].trim();
        String strIsControlList = ret05M070[m][9].trim();
        String strIsLinked = ret05M070[m][10].trim();
        String strAgentRel = ret05M070[m][6].trim();
        String strCompanyNO = ret05M070[m][2].trim();
        String strCompanyName = "";
        String strgetCompNameSql = "SELECT TOP 1 CustomName FROM SALE05M091 WHERE CustomNo = '" + strCompanyNO + "' AND OrderNo= '" + strOrderNo + "' ";
        String[][] retgetCompName;
        retgetCompName = dbSale05M.queryFromPool(strgetCompNameSql);
        if (retgetCompName.length > 0) {
          strCompanyName = retgetCompName[0][0].trim();
        }
        // 20200707 Kyle : 過濾已被換名的代理人
        String strStatusCd = getValueAt("table10", m, "StatusCd").toString().trim();
        if ("C".equals(strStatusCd))
          continue;

        String custSA = "客戶" + strCompanyName + "之";
        if (m == 0)
          errMsg += "\n";

        // 不適用LOG1~4,6,7
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // 18. 制裁名單
        talk db400 = getTalk("400CRM");
        // Query_Log 拿生日
        String strPW0Dsql = "SELECT BIRTHDAY FROM QUERY_LOG WHERE PROJECT_ID = '" + stringProjectID1 + "' AND QUERY_ID = '" + strCustomNo + "' AND NAME = '" + strCustomName + "'";
        retQueryLog = dbPW0D.queryFromPool(strPW0Dsql);
        if (retQueryLog.length > 0) {
          strBDaysql = "AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + retQueryLog[0][0].trim().replace("/", "-") + "' )";
        } else {
          strBDaysql = "AND CUSTOMERNAME='" + strCustomName + "'";
        }
        // System.out.println("strBDaysql====>"+strBDaysql) ;

        // AS400-181
        String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' " + strBDaysql;
        String retCList[][] = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料', '" + strActionName + "', '代理人" + strCustomName
              + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','018','代理人" + strCustomName
              + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "代理人" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','018','代理人" + strCustomName + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        // 171
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N' AND CUSTOMERID = '" + strCustomNo + "' " + strBDaysql;
        String ret171List[][] = db400.queryFromPool(str400sql);
        if (ret171List.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人" + strCustomName
              + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','021','代理人" + strCustomName
              + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '021', '客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "代理人" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','021','代理人" + strCustomName + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 9.資恐地區
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          // System.out.println("strCZ07==>"+strCZ07) ;
          if ("優先法高".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人 " + strCustomName
                + " 係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','代理人 " + strCustomName
                + " 係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','" + empNo + "','" + RocNowDate
                + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            errMsg += custSA + "代理人 " + strCustomName + " 係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。\n";

          } else {
            // 不符合
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','代理人 " + strCustomName + " 係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }

        // 8. 洗錢第八條(有代理人)
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人" + strCustomName
            + "為辦理不動產交易，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','008','代理人" + strCustomName
            + "為辦理不動產交易，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // AS400
        stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate + "', '"
            + strCustomNo + "', '" + strCustomName + "', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbJGENLIB.execFromPool(stringSQL);
        errMsg += custSA + "代理人" + strCustomName + "代為辦理其不動產交易，請依洗錢及資恐防制作業辦理。\n";

        // 5. 關係非二等血姻親
        if ("朋友".equals(strAgentRel) || "其他".equals(strAgentRel)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人 " + strCustomName + " 與客戶 "
              + coutsomName + " 非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','005','代理人 " + strCustomName + " 與客戶 "
              + coutsomName + " 非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA.replace("之", "與") + "代理人" + strCustomName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','005','代理人 " + strCustomName + " 與客戶 " + coutsomName + " 非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        // 20. 黑名單
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人" + strCustomName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','代理人" + strCustomName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "代理人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','代理人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 19. 利害關係人
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人" + strCustomName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','代理人" + strCustomName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "代理人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','代理人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 不適用LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','交易最終受益人或交易人為金融監督管理委員會函轉外國政府所提供之恐怖分子或團體；或國際洗錢防制組織認定或追查之恐怖組織；或交易資金疑似或有合理理由懷疑與恐怖活動、恐怖組織或資助恐怖主義有關聯者，應依資恐防制法進行相關作業。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

      } // for end
    } // 代理人 END
    System.out.println("============================");

    // System.out.println("errMsg============================"+errMsg) ;
    // 送出errMsg
    if (!"".equals(errMsg)) {
      setValue("errMsgBoxText", errMsg);
      getButton("errMsgBoxBtn").doClick();
      getButton("sendMail").doClick();
    }
    System.out.println("==============洗錢防治檢核LOG END====================================");
    return value;
  }

  public String getInformation() {
    return "---------------AML(AML).defaultValue()----------------";
  }
}
