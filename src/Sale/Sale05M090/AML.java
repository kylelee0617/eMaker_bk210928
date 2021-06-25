package Sale.Sale05M090;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bproc;

public class AML extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    // 2019/10/23 洗錢 控管名單LOG
    System.out.println("==============洗錢防治檢核LOG START====================================");
    talk dbSale05M = getTalk("Sale");
    talk dbJGENLIB = getTalk("JGENLIB");
    talk dbEIP = getTalk("EIP");
    
    KUtils kutil = new KUtils();
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
    
    //取RY773洗錢態樣
    Map mapAMLMsg = kutil.getAMLDesc();
    
    System.out.println("==============客戶資料==============");
    stringSQL = "SELECT CountryName,CustomNo,CustomName,IsBlackList,IsControlList,IsLinked,Birthday FROM Sale05M091 WHERE ORDERNO = '" + strOrderNo + "' "
        + "and ISNULL(StatusCd , '') = '' ";
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
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','客戶資料','" + strActionName + "','不適用','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
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
      
        // 9. 資恐地區
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
//            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + getValue("OrderNo").trim() + "', '"
//                + RocNowDate + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','" + empNo + "','"
//                + RocNowDate + "','" + strNowTime + "')";
//            dbJGENLIB.execFromPool(stringSQL);
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
//          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime
//              + "')";
//          dbJGENLIB.execFromPool(stringSQL);
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
//          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime
//              + "')";
//          dbJGENLIB.execFromPool(stringSQL);
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
        
      } // for end
    }
    System.out.println("============================");
    
    
    System.out.println("==============實質受益人==============");
    ret05M070 = getTableData("table6");
    if (ret05M070.length > 0) {
      for (int m = 0; m < ret05M070.length; m++) {
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
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','實質受益人資料','" + strActionName + "','實質受益人" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','實質受益人" + strCustomName
                + "係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區,，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
//            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','" + empNo + "','" + RocNowDate
//                + "','" + strNowTime + "')";
//            dbJGENLIB.execFromPool(stringSQL);
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
//          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime
//              + "')";
//          dbJGENLIB.execFromPool(stringSQL);
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
//          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime
//              + "')";
//          dbJGENLIB.execFromPool(stringSQL);
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
        
      } // for end
    } // 實質受益人 END
    System.out.println("============================");

    System.out.println("==============代理人==============");
    stringSQL = "select a.CountryName , a.ACustomNo , a.AgentName , a.IsBlackList , a.IsControlList , a.IsLinked , a.AgentRel , a.CustomNo, " 
              + "(SELECT TOP 1 CustomName FROM SALE05M091 b WHERE b.CustomNo = a.CustomNo AND b.OrderNo= a.OrderNo ) as custName " 
              + "from Sale05M091Agent a where ISNULL(a.StatusCd, '') != 'C' and a.orderNo = '" + strOrderNo + "' ";
    ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (ret05M070.length > 0) {
      for (int m = 0; m < ret05M070.length; m++) {
//        String strCountryName = ret05M070[m][0].trim();
        String strAgentNo = ret05M070[m][1].trim();
        String strAgentName = ret05M070[m][2].trim();
        String strIsBlackList = ret05M070[m][3].trim();
        String strIsControlList = ret05M070[m][4].trim();
        String strIsLinked = ret05M070[m][5].trim();
        String strAgentRel = ret05M070[m][6].trim();
        String strCustNo = ret05M070[m][7].trim();
        String strCustName = ret05M070[m][8].trim();
        
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
                            .replaceAll("<customName>", strAgentNo).replaceAll("<customTitle>", "客戶")
                            .replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不適用','" + strAgentNo + "','"
              + strAgentName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        String custSA = "客戶" + strCustName + "之";
        if (m == 0) errMsg += "\n";

        //TODO: 代理人制裁PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strAgentNo);
        String birth = qBean.getBirthday().length() == 0 ? " " : qBean.getBirthday().toString().replace("-", "");
        String indCode = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();  
        String amlText = stringProjectID1 + "," + strOrderNo + "," + stringOrderDate + "," + getFunctionName() + "," + "客戶制裁及PEPS" 
            + "," + strAgentNo + "," + strAgentName + "," + birth + "," + indCode + "," + "query1821";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

        // 8. 洗錢第八條(有代理人)
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人" + strAgentName
            + "為辦理不動產交易，請依洗錢及資恐防制作業辦理。','" + strAgentNo + "','" + strAgentName + "','" + stringOrderDate + "','RY','773','008','代理人" + strAgentName
            + "為辦理不動產交易，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
//        // AS400
//        stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate + "', '"
//            + strAgentNo + "', '" + strAgentName + "', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
//        dbJGENLIB.execFromPool(stringSQL);
        errMsg += custSA + "代理人" + strAgentName + "代為辦理其不動產交易，請依洗錢及資恐防制作業辦理。\n";

        // 5. 關係非二等血姻親
        if ("朋友".equals(strAgentRel) || "其他".equals(strAgentRel)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人 " + strAgentName + " 與客戶 "
              + coutsomName + " 非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + strAgentNo + "','" + strAgentName + "','" + stringOrderDate + "','RY','773','005','代理人 " + strAgentName + " 與客戶 "
              + coutsomName + " 非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
//          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//              + "', '" + strAgentNo + "', '" + strAgentName + "', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
//          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA.replace("之", "與") + "代理人" + strAgentName + "非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strAgentNo + "','"
              + strAgentName + "','" + stringOrderDate + "','RY','773','005','代理人 " + strAgentName + " 與客戶 " + coutsomName + " 非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        // 20. 黑名單
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人" + strAgentName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + strAgentNo + "','" + strAgentName + "','" + stringOrderDate + "','RY','773','020','代理人" + strAgentName
              + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
//          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//              + "', '" + strAgentNo + "', '" + strAgentName + "', '773', '020', '該客戶為疑似黑名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','" + empNo + "','" + RocNowDate + "','" + strNowTime
//              + "')";
//          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "代理人" + strAgentName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strAgentNo + "','"
              + strAgentName + "','" + stringOrderDate + "','RY','773','020','代理人" + strAgentName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // 19. 利害關係人
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','代理人" + strAgentName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + strAgentNo + "','" + strAgentName + "','" + stringOrderDate + "','RY','773','019','代理人" + strAgentName
              + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
//          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
//              + "', '" + strAgentNo + "', '" + strAgentName + "', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate + "','" + strNowTime
//              + "')";
//          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "代理人" + strAgentName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";

        } else {
          // 不符合
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','購屋證明單','代理人資料','" + strActionName + "','不符合','" + strAgentNo + "','"
              + strAgentName + "','" + stringOrderDate + "','RY','773','019','代理人" + strAgentName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
     
      } // for end
    } // 代理人 END
    System.out.println("============================");

    // 送出errMsg
    if (StringUtils.isNotBlank(errMsg)) {
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
