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
    talk dbSale05M = getTalk("Sale");
    talk dbJGENLIB = getTalk("JGENLIB");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    
    KUtils kutil = new KUtils();
    String stringSQL = "";
    String strBDaysql = "";
    String errMsg = "";
    String[][] retPat001;
    String[][] retPat002;
    String[][] retPat003;
    String[][] retPat004;
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

        // 不適用LOG1~8
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 5
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 8
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // 萊斯 : 制裁名單 & PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = strBirthday.length() == 0 ? " " : strBirthday.toString().replace("-", "");
        String ind = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
        String amlText = strOrderNo + "," + stringOrderDate + "," + strCustomNo + "," + strCustomName + "," + birth + "," + ind + "," + "query18";
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
        // 黑名單
        // 控管名單
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
        //// 不適用LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','交易最終受益人或交易人為金融監督管理委員會函轉外國政府所提供之恐怖分子或團體；或國際洗錢防制組織認定或追查之恐怖組織；或交易資金疑似或有合理理由懷疑與恐怖活動、恐怖組織或資助恐怖主義有關聯者，應依資恐防制法進行相關作業。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
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

        // 不適用LOG1~8
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 5
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 8
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // 萊斯 : 制裁名單 & PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = strBirthday.length() == 0 ? " " : strBirthday.toString().replace("-", "");
        String ind = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
        String amlText = strOrderNo + "," + stringOrderDate + "," + strCustomNo + "," + strCustomName + "," + birth + "," + ind + "," + "query18";
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
        // 黑名單
        // 控管名單
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
        //// 不適用LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','交易最終受益人或交易人為金融監督管理委員會函轉外國政府所提供之恐怖分子或團體；或國際洗錢防制組織認定或追查之恐怖組織；或交易資金疑似或有合理理由懷疑與恐怖活動、恐怖組織或資助恐怖主義有關聯者，應依資恐防制法進行相關作業。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','實質受益人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
      } // for end
    } // 實質受益人 END
    System.out.println("============================");

    
    System.out.println("==============代理人==============");
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

        // 不適用LOG1~4,6,7
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // 萊斯 : 制裁名單 & PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = qBean.getBirthday().length() == 0 ? " " : qBean.getBirthday().toString().replace("-", "");
        String ind = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
        String amlText = strOrderNo + "," + stringOrderDate + "," + strCustomNo + "," + strCustomName + "," + birth + "," + ind + "," + "query18";
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
//        if ("".equals(errMsg)) {
//          errMsg = "代理人" + strCustomName + "代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
//        } else {
//          errMsg = errMsg + "\n代理人" + strCustomName + "代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
//        }

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
//            if ("".equals(errMsg)) {
//              errMsg = "代理人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。";
//            } else {
//              errMsg = errMsg + "\n代理人" + strCustomName + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。";
//            }

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
//            if ("".equals(errMsg)) {
//              errMsg = "代理人" + strCustomName + "與客戶" + strOrderCustomName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
//            } else {
//              errMsg = errMsg + "\n代理人" + strCustomName + "與客戶" + strOrderCustomName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
//            }
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

        // 黑名單
        // 控管名單
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
//          if ("".equals(errMsg)) {
//            errMsg = "代理人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
//          } else {
//            errMsg = errMsg + "\n代理人" + strCustomName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
//          }
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
//          if ("".equals(errMsg)) {
//            errMsg = "代理人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
//          } else {
//            errMsg = errMsg + "\n代理人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
//          }
        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不符合','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','代理人" + strCustomName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        //// 不適用LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','交易最終受益人或交易人為金融監督管理委員會函轉外國政府所提供之恐怖分子或團體；或國際洗錢防制組織認定或追查之恐怖組織；或交易資金疑似或有合理理由懷疑與恐怖活動、恐怖組織或資助恐怖主義有關聯者，應依資恐防制法進行相關作業。','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','換名','代理人資料','" + strActionName + "','不適用','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

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
