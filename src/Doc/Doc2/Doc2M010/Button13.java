package Doc.Doc2.Doc2M010;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import com.jacob.activeX.*;
import com.jacob.com.*;
import Farglory.util.FargloryUtil;

public class Button13 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    // 檢核
    Doc.Doc2M010 exeFun10 = new Doc.Doc2M010();
    Farglory.util.FargloryUtil exeUtil = new Farglory.util.FargloryUtil();
    String[] arrayFlowData = convert.StringToken("" + get("Doc2M010_DATA"), "%-%");
    put("Doc2M010_DATA", "null");
    System.out.println("----------------------------------------------------------1");
    String stringComNo = ""; // 公司
    String stringBarCode = ""; // 條碼編號
    if (arrayFlowData.length == 2) {
      stringComNo = arrayFlowData[0].trim();
      stringBarCode = arrayFlowData[1].trim();
    } else {
      stringComNo = getValue("ComNo").trim();
      stringBarCode = getValue("BarCode").trim();
    }
    if ("".equals(stringComNo)) return value;
    if ("".equals(stringBarCode)) return value;
    System.out.println("----------------------------------------------------------2");
    // 資料處理
    // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
    // 5 CostID1 6 RealMoney 7 RealTotalMoney
    String[][] retDoc2M012 = exeFun10.getDoc2M012(stringBarCode);
    if (retDoc2M012.length == 0) return value;
    String stringCostID = retDoc2M012[0][4].trim();
    // 零用金
    boolean booleanPocketMoney = false;
    Vector vectorPocketMoney = new Vector();
    vectorPocketMoney.add("31");
    if (vectorPocketMoney.indexOf(stringCostID) != -1) booleanPocketMoney = true;
    //
    //
    int intPos = 0;
    String stringInOut = "";
    String stringDepartNo = "";
    String stringProjectID1 = "";
    String stringRealMoney = "";
    String stringKey = "";
    String[] arrayTemp = null;
    double doubleRealMoneySum = 0;
    double doubleRate = 0;
    Vector vectorTableData = new Vector();
    Vector vectorTableDataKey = new Vector();
    for (int intRowNo = 0; intRowNo < retDoc2M012.length; intRowNo++) {
      stringInOut = retDoc2M012[intRowNo][0].trim();
      stringDepartNo = retDoc2M012[intRowNo][1].trim();
      stringProjectID1 = retDoc2M012[intRowNo][3].trim();
      stringRealMoney = retDoc2M012[intRowNo][6].trim();
      stringKey = stringInOut + "---" + stringDepartNo + "----" + stringProjectID1;
      intPos = vectorTableDataKey.indexOf(stringKey);
      if (intPos == -1) {
        vectorTableData.add(retDoc2M012[intRowNo]);
        vectorTableDataKey.add(stringKey);
        //
        arrayTemp = retDoc2M012[intRowNo];
        arrayTemp[6] = "0";
      } else {
        arrayTemp = (String[]) vectorTableData.get(intPos);
      }
      arrayTemp[6] = "" + (exeUtil.doParseDouble(arrayTemp[6]) + exeUtil.doParseDouble(stringRealMoney));
      //
      doubleRealMoneySum += exeUtil.doParseDouble(stringRealMoney);
    }
    retDoc2M012 = (String[][]) vectorTableData.toArray(new String[0][0]);
    //
    int intStartRow = 7; // 開始列數
    int intPageRowCount = 15; // 一頁列印列數
    int intPageTotalRow = 33; // 一頁總列數
    int intPageStart = 1;
    int intRowCount = 0;
    int intRowCountP = 0;
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    String stringFileName = "Doc2M010.xlt";
    String stringSheetName = "";
    boolean booleanVersionNew = "B3018,".indexOf(getUser()) != -1 || "0231".equals(stringDepartNoSubject);
    if (booleanVersionNew) {
      intStartRow = 4;
      intRowCount = retDoc2M012.length;
      stringFileName = "Doc2M010-2.xlt";
      // if("B3018".equals(getUser())) messagebox(""+intRowCount) ;
      if (intRowCount <= 20) {
        intPageRowCount = 20;
        intPageTotalRow = 37;
        intRowCountP = 24;
        stringSheetName = "Sheet1_20";
      } else if (intRowCount > 25) {
        intPageRowCount = 30;
        intPageTotalRow = 45;
        intRowCountP = 34;
        stringSheetName = "Sheet1_30";
      } else {
        intPageRowCount = 25;
        intPageTotalRow = 40;
        intRowCountP = 29;
        stringSheetName = "Sheet1_25";
      }
    }
    // 0 FactoryNo 1 InvoiceKind 2 InvoiceDate 3 InvoiceNo 4 InvoiceMoney
    // 5 InvoiceTax 6 InvoiceTotalMoney 7 DeductKind 8 RecordNo
    Farglory.Excel.FargloryExcel exeFun = null;
    if (booleanVersionNew) {
      exeFun = new Farglory.Excel.FargloryExcel(stringSheetName);
    } else {
      exeFun = new Farglory.Excel.FargloryExcel(intStartRow, intPageRowCount, intPageTotalRow, intPageStart);
    }
    // 資料處理
    String stringClientFilePath = "g:/資訊室/Excel/Doc/" + stringFileName;
    /*
     * if(!(new File(stringClientFilePath)).exists()) { stringClientFilePath =
     * "http://emaker.farglory.com.tw:8080/servlet/baServer3?step=6?filename="+
     * stringClientFilePath ; }
     */
    stringClientFilePath = getClientFile(stringClientFilePath, exeUtil);
    if ("".equals(stringClientFilePath)) return value;
    // 取得 Exce 物件
    Vector retVector = exeFun.getExcelObject(stringClientFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = booleanVersionNew ? objectSheet1 : (Dispatch) retVector.get(2);
    Dispatch objectSheets = (Dispatch) retVector.get(3);
    Dispatch objectClick = null;
    exeFun.setClearCol(1, 4); // 設定清除欄位為 B 到 R
    // 表頭
    if (booleanVersionNew) {
      exeFun.putDataIntoExcel(1, 0, exeFun10.getCompanyName(stringComNo), objectSheet2);
      exeFun.putDataIntoExcel(1, 1, exeFun10.getCostName(stringCostID) + "分攤表", objectSheet2);
    } else {
      // 公司
      exeFun.putDataIntoExcel(1, 1, exeFun10.getCompanyName(stringComNo), objectSheet2); // 案別名稱
    }
    //
    int intStartDataRow = booleanVersionNew ? intStartRow : exeFun.getStartDataRow();
    int intPageDataRow = exeFun.getPageDataRow();
    int intRecordNo = intStartDataRow;
    double doubleRateSum = 0;
    for (int intRowNo = 0; intRowNo < retDoc2M012.length; intRowNo++) {
      stringInOut = retDoc2M012[intRowNo][0].trim();
      stringDepartNo = retDoc2M012[intRowNo][1].trim();
      stringProjectID1 = retDoc2M012[intRowNo][3].trim();
      stringRealMoney = retDoc2M012[intRowNo][6].trim();
      doubleRate = exeFun10.doParseDouble(stringRealMoney) / doubleRealMoneySum;
      doubleRate = exeFun10.doParseDouble(convert.FourToFive("" + doubleRate, 6));
      //
      doubleRateSum += doubleRate;
      if ("I".equals(stringInOut)) {
        exeFun.putDataIntoExcel(1, intRecordNo, exeFun10.getDepartName(stringDepartNo), objectSheet2); // 案別名稱
        exeFun.putDataIntoExcel(2, intRecordNo, stringDepartNo, objectSheet2);// 案別代碼
      } else {
        if (stringDepartNo.startsWith("053")) stringProjectID1 = "053" + stringProjectID1;
        exeFun.putDataIntoExcel(1, intRecordNo, exeFun10.getProjectName(stringProjectID1), objectSheet2); // 案別名稱
        exeFun.putDataIntoExcel(2, intRecordNo, stringProjectID1, objectSheet2);// 案別代碼
      }
      exeFun.putDataIntoExcel(3, intRecordNo, convert.FourToFive("" + doubleRate, 6), objectSheet2);// 比例
      exeFun.putDataIntoExcel(4, intRecordNo, convert.FourToFive(stringRealMoney, 0), objectSheet2);// 分攤金額

      intRecordNo++;
      // 滿頁時，將 Sheet2 Copy Sheet1
      if (!booleanVersionNew && intRecordNo == (intPageDataRow + intStartDataRow)) {
        double doubleAmt = exeFun10.doParseDouble(exeFun.getDataFromExcel(0, 21, objectSheet2)) + exeFun10.doParseDouble(exeFun.getDataFromExcel(0, 22, objectSheet2));
        exeFun.putDataIntoExcel(0, 22, convert.FourToFive("" + doubleAmt, 0), objectSheet2);
      }
      if (!booleanVersionNew) intRecordNo = exeFun.doChangePage(intRecordNo, objectSheet1, objectSheet2);
    } // For END
      // 複製未滿頁
    if (!booleanVersionNew && intRecordNo != intStartDataRow) {
      double doubleAmt = exeFun10.doParseDouble(exeFun.getDataFromExcel(0, 21, objectSheet2)) + exeFun10.doParseDouble(exeFun.getDataFromExcel(0, 22, objectSheet2));
      exeFun.putDataIntoExcel(0, 22, convert.FourToFive("" + doubleAmt, 0), objectSheet2);
      //
      exeFun.CopyPage(objectSheet1, objectSheet2);
      exeFun.doClearContents("B" + (intStartDataRow + 1) + ":E" + (intStartDataRow + intPageDataRow), objectSheet2);
      exeFun.doAdd1PageNo();
    }
    //
    int intAllPage = exeFun.getPageNo();
    int intPageAllRow = exeFun.getPageAllRow();
    int intRow = booleanVersionNew ? intRowCountP : (intAllPage - 2) * intPageAllRow + 21;
    double doubleSum = exeFun10.doParseDouble(exeFun10.getRealMoneySumForDoc2M012(stringBarCode));
    double doubleTotalSum = exeFun10.doParseDouble(exeFun10.getRealTotalMoneySumForDoc2M012(stringBarCode));
    double doubleTax = doubleTotalSum - doubleSum;
    // 小計
    exeFun.putDataIntoExcel(4, intRow, convert.FourToFive("" + doubleSum, 0), objectSheet1);
    //
    exeFun.putDataIntoExcel(3, intRow, convert.FourToFive("" + doubleRateSum, 6), objectSheet1);
    // 稅額
    exeFun.putDataIntoExcel(4, (intRow + 1), convert.FourToFive("" + doubleTax, 0), objectSheet1);
    // 合計
    exeFun.putDataIntoExcel(4, (intRow + 2), convert.FourToFive("" + doubleTotalSum, 0), objectSheet1);
    if (booleanVersionNew) {
      String stringSheetNameL = "";
      String[] arraySheetName = { "Sheet1_20", "Sheet1_25", "Sheet1_30" };
      for (int intNo = 0; intNo < arraySheetName.length; intNo++) {
        stringSheetNameL = arraySheetName[intNo];
        objectSheet1 = Dispatch.call(objectSheets, "Item", stringSheetNameL).toDispatch();
        if (stringSheetNameL.equals(stringSheetName)) {
          if (objectSheet1 != null && !"B3018".equals(getUser())) Dispatch.put(objectSheet1, "Name", "Sheet");
        } else {
          Dispatch.call(objectSheet1, "Delete");
        }
      }
    }
    // 釋放 Excel 物件
    exeFun.setPreView(false, ""); // 當不預覽時，且有傳入路徑時，另存新檔。
    exeFun.getReleaseExcelObject(retVector);
    return value;
  }

  public String getClientFile(String stringServerPath, FargloryUtil exeUtil) throws Throwable {
    String stringClientPath = "";
    String[] arrayTemp = convert.StringToken(stringServerPath, "/");
    //
    if (exeUtil.doSaveFile(stringServerPath, "Y")) {
      stringClientPath = "C:\\Emaker_Util\\" + arrayTemp[arrayTemp.length - 1].trim();
      return stringClientPath;
    }
    return stringServerPath;
  }
}
