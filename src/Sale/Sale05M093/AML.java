package Sale.Sale05M093;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bproc;

public class AML extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    // 2019/12/28 洗錢 控管名單LOG
    System.out.println("==============洗錢防治檢核LOG START====================================");
    KUtils kutil = new KUtils();
    talk dbSale05M = getTalk("Sale");
    talk dbJGENLIB = getTalk("JGENLIB");
    talk dbEIP = getTalk("EIP");
    
    String stringSQL = "";
    String strBDaysql = "";
    String errMsg = "";
    String[][] retPat001;
    String[][] retQueryLog;
    String[][] tb2_string = getTableData("table2");
    String[][] tb5_string = getTableData("table5");
    String[][] tb6_string = getTableData("table6");
    String strOrderNo = getValue("OrderNo").trim();
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringOrderDate = getValue("TrxDate").trim();
    String strActionName = "存檔";
    System.out.println("strOrderNo=====>" + strOrderNo);
    System.out.println("stringProjectID1=====>" + stringProjectID1);
    System.out.println("stringOrderDate=====>" + stringOrderDate);
    System.out.println("strActionName=====>" + strActionName);
    
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
    System.out.println("strNowTimestamp=====>" + strNowTimestamp);
    System.out.println("RocNowDate=====>" + RocNowDate);
    System.out.println("strNowTime=====>" + strNowTime);
    
    // 員工碼
    String userNo = getUser().toUpperCase().trim();
    String empNo = "";
    String[][] retEip = null;
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }
    System.out.println("empNo=====>" + empNo);
    
    // 序號
    int intRecordNo = 1;
    String[][] ret05M070;
    stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + getValue("OrderNo").trim() + "'";
    ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
    
    // actionNo
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    String strActionNo = strNowDate + strNowTime + ram;
    System.out.println("strActionNo=====>" + strActionNo);

    // 取RY773洗錢態樣
    Map mapAMLMsg = kutil.getAMLDesc();

    System.out.println("==============客戶資料==============");
    if (tb2_string.length > 0) {
      for (int m = 0; m < tb2_string.length; m++) {// 同一訂單下
        String strCountryName = tb2_string[m][5].trim();
        String strCustomNo = tb2_string[m][6].trim();
        String strCustomName = tb2_string[m][7].trim();
        String strIsBlackList = tb2_string[m][20].trim();
        String strIsControlList = tb2_string[m][21].trim();
        String strIsLinked = tb2_string[m][22].trim();
        String strBirthday = tb2_string[m][9].trim();

        // 不適用LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16 };
        for (int ii = 0; ii < noUseAML.length; ii++) {
          String amlNo = "";
          if (noUseAML[ii] < 10) {
            amlNo = "00" + noUseAML[ii];
          } else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString().replaceAll("<customName>", strCustomName).replaceAll("<customTitle>", "客戶").replaceAll("<customName2>", "")
              .replaceAll("<customTitle2>", "");
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        //TODO: 主要客戶 18.制裁 & 21.PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = strBirthday.length() == 0 ? " " : strBirthday.toString().replace("-", "");
        String indCode = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
        String amlText = stringProjectID1 + "," + strOrderNo + "," + stringOrderDate + "," + getFunctionName() + "," + "客戶制裁及PEPS" 
            + "," + strCustomNo + "," + strCustomName + "," + birth + "," + indCode + "," + "query1821";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

        // 資恐地區
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("優先法高".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','客戶" + strCustomName
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
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','客戶" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }

        // 控管+黑名單
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','客戶" + strCustomName
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
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','客戶" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // 利害關係人
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','客戶" + strCustomName
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
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','客戶" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

      } // for end
    }
    System.out.println("============================");

    System.out.println("==============實質受益人==============");
    if (tb5_string.length > 0) {// 實質受益人 START
      for (int m = 0; m < tb5_string.length; m++) {// 同一訂單下
        String strCountryName = tb5_string[m][6].trim();
        String strCustomNo = tb5_string[m][4].trim();
        String strCustomName = tb5_string[m][3].trim();
        String strIsBlackList = tb5_string[m][8].trim();
        String strIsControlList = tb5_string[m][9].trim();
        String strIsLinked = tb5_string[m][10].trim();
        String strBirthday = tb5_string[m][5].trim();

        // 不適用LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        for (int ii = 0; ii < noUseAML.length; ii++) {
          String amlNo = "";
          if (noUseAML[ii] < 10) {
            amlNo = "00" + noUseAML[ii];
          } else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                            .replaceAll("<customName>", strCustomName).replaceAll("<customTitle>", "客戶")
                            .replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        //TODO: 實質受益人制裁PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = strBirthday.length() == 0 ? " " : strBirthday.toString().replace("-", "");
        String indCode = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
        String amlText = stringProjectID1 + "," + strOrderNo + "," + stringOrderDate + "," + getFunctionName() + "," + "客戶制裁及PEPS" 
            + "," + strCustomNo + "," + strCustomName + "," + birth + "," + indCode + "," + "query1821";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

        // 資恐地區
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("優先法高".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
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
          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','009','實質受益人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // 黑名單 控管名單
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
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
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','實質受益人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // 利害關係人
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
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
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','實質受益人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
      
      } // for end
    } // 實質受益人 END
    System.out.println("============================");

    System.out.println("==============代理人==============");
    // stringSQL = "SELECT
    // CountryName,ACustomNo,AgentName,IsBlackList,IsControlList,IsLinked, AgentRel
    // FROM Sale05M091Agent WHERE ORDERNO = '"+strOrderNo+"'";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (tb6_string.length > 0) {// 代理人 START
      for (int m = 0; m < tb6_string.length; m++) {// 同一訂單下
        String strCountryName = tb6_string[m][5].trim();
        String strCustomNo = tb6_string[m][4].trim();
        String strOrderCustomName = tb6_string[m][2].trim();
        String strCustomName = tb6_string[m][3].trim();
        String strIsBlackList = tb6_string[m][8].trim();
        String strIsControlList = tb6_string[m][9].trim();
        String strIsLinked = tb6_string[m][10].trim();
        String strAgentRel = tb6_string[m][6].trim();

        String custSA = "客戶" + strOrderCustomName + "之";
        if (m == 0)
          errMsg += "\n";

        // 不適用LOG1~4 ,6 ,7 ,10~16
        int[] noUseAML = { 1, 2, 3, 4, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        for (int ii = 0; ii < noUseAML.length; ii++) {
          String amlNo = "";
          if (noUseAML[ii] < 10) {
            amlNo = "00" + noUseAML[ii];
          } else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                            .replaceAll("<customName>", strCustomName).replaceAll("<customTitle>", "客戶")
                            .replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        //TODO: 代理人制裁PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = qBean.getBirthday().length() == 0 ? " " : qBean.getBirthday().toString().replace("-", "");
        String indCode = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();  
        String amlText = stringProjectID1 + "," + strOrderNo + "," + stringOrderDate + "," + getFunctionName() + "," + "客戶制裁及PEPS" 
            + "," + strCustomNo + "," + strCustomName + "," + birth + "," + indCode + "," + "query1821";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

        // 洗錢第八條
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','代理人" + strCustomName
            + "代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','008','代理人" + strCustomName
            + "代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // AS400
        stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate + "', '"
            + strCustomNo + "', '" + strCustomName + "', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbJGENLIB.execFromPool(stringSQL);
        errMsg += custSA + "代理人" + strCustomName + "代為辦理其不動產交易，請依洗錢及資恐防制作業辦理。\n";


        // 資恐地區
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          System.out.println("strCZ07==>" + strCZ07);
          if ("優先法高".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','代理人" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','代理人" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','" + empNo + "','" + RocNowDate
                + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            errMsg += custSA + "代理人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,請依洗錢及資恐防制作業辦理。\n";
          } else {
            // 不符合
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','代理人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }

        // 關係
        if (!"".equals(strAgentRel)) {
          if ("朋友".equals(strAgentRel) || "其他".equals(strAgentRel)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','代理人" + strCustomName + "與客戶"
                + strOrderCustomName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','005','代理人" + strCustomName
                + "與客戶" + strOrderCustomName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            errMsg += custSA + "代理人" + strCustomName + "與客戶" + strOrderCustomName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
          } else {
            // 不符合
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','005','代理人" + strCustomName + "與客戶" + strOrderCustomName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }

        // 黑名單+控管名單
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','代理人" + strCustomName
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
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','代理人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        // 利害關係人
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','代理人" + strCustomName
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
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','代理人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
      } // for end
    } // 代理人 END
    System.out.println("============================");

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
    return "---------------button6(AML).defaultValue()----------------";
  }
}
