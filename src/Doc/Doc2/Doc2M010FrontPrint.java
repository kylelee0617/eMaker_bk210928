package Doc.Doc2;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import java.net.*;
import Farglory.util.FargloryUtil;

public class Doc2M010FrontPrint extends bTransaction {
  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    FargloryUtil exeUtil = new FargloryUtil();
    JButton jbutton = null; // 分攤表列印
    JTable jtable2 = null;
    String stringToday = datetime.getToday("YYYY/mm/dd");
    String stringFunctionName = getFunctionName();
    String stringFunction = "";
    String stringPrinterIP = "";
    String stringPrintable = getValue("Printable").trim();
    String stringComNo = getValue("ComNo").trim();
    //
    put("Doc2M010_Print_OK", "null");
    put("Doc2M010_PRINT_BarCode", "null");
    if (!isBatchCheckOK(exeUtil)) return false;
    // System.out.println("----------------------列表機 START") ;
    stringPrinterIP = getDefaultPrinter(exeUtil);
    if ("ERROR".equals(stringPrinterIP)) return false;
    if (stringFunctionName.indexOf("土地") != -1) {
      stringFunction = "土地開發請款申請書-列印(Doc11R00101)";
      // if("2017/07/03".compareTo(stringToday)<=0) {
      stringFunction = "土地開發請款申請書-A4列印(Doc11R00101)";
      // }
    } else if ("OO".equals(stringComNo)) {
      stringFunction = "請款申請書-列印(Doc2R0111)";
    } else {
      stringFunction = "請款申請書-列印(Doc2R011)";
      jbutton = getButton("Button13");
      jtable2 = getTable("Table2");
      if (!"b3018".equals(getUser())) {
        stringFunction = "請款申請書-A4列印(Doc2R0112)";
      }
    }
    doSession(stringPrinterIP, exeUtil);
    System.out.println("stringFunction(" + stringFunction + ")----------------------S");
    showForm(stringFunction);
    System.out.println("stringFunction(" + stringFunction + ")----------------------E");

    // 成功訊息
    String stringPrintSuccess = ("" + get("Doc2M010_Print_OK")).trim();
    if ("Y".equals(stringPrintable) && ("OK".equals(stringPrintSuccess) || "B3018".equals(getUser()))) {
      doEnd(stringComNo, jtable2, jbutton, stringFunctionName, exeUtil);

//      String[] arrayTemp = convert.StringToken(stringPrinterIP, "\\");
//      JOptionPane.showMessageDialog(null, "完成列印。(列表機：" + arrayTemp[arrayTemp.length - 1] + ")", "訊息", JOptionPane.INFORMATION_MESSAGE);
      
      //申20210601006 Kyle : 因疫情改為手動列印，通知訊息修改
//      messagebox("請選擇列印方式~");
    }
    System.out.println("----------11111111----------------------列印完成" + stringFunction);
    return false;
  }

  public void doEnd(String stringComNo, JTable jtable2, JButton jbutton, String stringFunctionName, FargloryUtil exeUtil) throws Throwable {
    if ("OO".equals(stringComNo)) return;
    if (stringFunctionName.indexOf("土地") != -1) return;
    
    //
    String stringInOut = "";
    String stringDepart = "";
    String stringProjectID = "";
    String stringProjectID1 = "";
    String stringKey = "";
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    String stringUser = getUser();
    Vector vectorKey = new Vector();
    boolean booleanExcel = false;
    for (int intNo = 0; intNo < jtable2.getRowCount(); intNo++) {
      if ("0221".equals(stringDepartNoSubject)) break;
      stringInOut = ("" + jtable2.getValueAt(intNo, 2)).trim();
      stringDepart = ("" + jtable2.getValueAt(intNo, 3)).trim();
      stringProjectID = ("" + jtable2.getValueAt(intNo, 4)).trim();
      stringProjectID1 = ("" + jtable2.getValueAt(intNo, 5)).trim();
      //
      stringKey = stringInOut + "%-%" + stringDepart + "%-%" + stringProjectID + "%-%" + stringProjectID1;
      if (vectorKey.indexOf(stringKey) == -1) vectorKey.add(stringKey);
      //
      if (vectorKey.size() > 1) {
        booleanExcel = true;
        break;
      }
    }
    if (booleanExcel && !"B3849".equals(stringUser)) {
      // 第一次才列印
      String stringPrintCount = getValue("PrintCount").trim();
      if (exeUtil.doParseInteger(stringPrintCount) == 0) {
         System.out.println("stringPrintCount----------------------------------["+stringPrintCount+"]");
        if (jbutton != null) jbutton.doClick();
      }
    }
  }

  public void doSession(String stringPrinterIP, FargloryUtil exeUtil) throws Throwable {
    String stringBarCode = getValue("BarCode").trim();
    String stringPrintable = getValue("Printable").trim();
    String stringFunctionName = getFunctionName();
    String stringType = "";
    //
    if (stringFunctionName.indexOf("土地") != -1) {
      String stringReimburseType = getValue("ReimburseType").trim();
      if (stringReimburseType.indexOf("請款") == -1) {
        stringType = "A"; // 請款申請書
      } else {
        stringType = "B";// 借款沖銷
      }
    } else {
      if (stringFunctionName.indexOf("借款") == -1) {
        stringType = "A"; // 請款申請書
      } else {
        if (stringFunctionName.indexOf("請款") == -1) {
          stringType = "C";// 借款申請書
        } else {
          stringType = "B";// 借款沖銷
        }
      }
    }
    //
    put("Doc2M010_PRINT_BarCode", stringBarCode);
    put("Doc2M010_PRINT_Enable", stringPrintable);
    put("Doc2M010_PRINT_Function", stringFunctionName);
    put("Doc2M010_PRINT_PrinterNAME", stringPrinterIP);
    put("Doc2M010_FUNCTION", stringType);
    //
    if (stringFunctionName.indexOf("土地") != -1) return;
    //
    String stringRealMoneySum = getValue("RealMoneySum").trim();
    String stringRetainMoney = getValue("RetainMoney").trim();
    String stringCheapenMoney = getValue("CheapenMoney").trim();
    put("Doc2M010_PRINT_RealMoneySum", stringRealMoneySum);
    put("Doc2M010_PRINT_RetainMoney", stringRetainMoney);
    put("Doc2M010_PRINT_CheapenMoney", stringCheapenMoney);
  }

  // 檢核
  // 前端資料檢核，正確回傳 True
  public boolean isBatchCheckOK(FargloryUtil exeUtil) throws Throwable {
    String stringBarCode = getValue("BarCode").trim();
    if ("".equals(stringBarCode)) return false;
    //
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    String stringEmployeeNo = getValue("EmployeeNo").trim();
    String stringUser = getUser().toUpperCase();
    // 20180206 暫時移除判斷 by B03812
    /*
     * if(!stringDepartNoSubject.startsWith("033") &&
     * !stringEmployeeNo.equals(stringUser) &&
     * "B1721,B3018,".indexOf(stringUser)==-1) {
     * System.out.println("stringDepartNoSubject="+
     * stringDepartNoSubject+", stringEmployeeNo="+stringEmployeeNo+", stringUser="
     * +stringUser+""); messagebox("非輸入人員，不可列印。") ; return false ; }
     */
    //
    String stringFunctionName = getFunctionName();
    if (stringFunctionName.indexOf("土地") != -1) return true;
    //
    System.out.println("行銷-公司-案別 80% 控管列印-----------------S");
    if (!isCanPrint10(exeUtil)) {
      return false;
    }
    System.out.println("行銷-公司-案別 80% 控管列印-----------------E");
    return true;
  }

  public String getDefaultPrinter(FargloryUtil exeUtil) throws Throwable {
    InetAddress inetaddressClinet = InetAddress.getLocalHost();
    String stringClinetIP = inetaddressClinet.getHostAddress().toString().trim();
    String stringFilePath = "c:\\Emaker_Print\\DocPrint2.ini";
    String stringPrinterIP = "";
    String[] arrayText = exeUtil.getArrayDataFromText(stringFilePath);
    if (arrayText.length != 0) {
      stringPrinterIP = arrayText[0].trim();
    }
    if (!"".equals(stringPrinterIP)) {
      if (!exeUtil.isPrinterExist(stringPrinterIP, "\\")) {
        Vector vectorPrinter = exeUtil.getPrinterPosition("\\");
        String stringPrinterIPL = "";
        boolean booleanFlag = false;
        for (int intNo = 0; intNo < vectorPrinter.size(); intNo++) {
          stringPrinterIPL = ("" + vectorPrinter.get(intNo)).trim();
          if (stringPrinterIPL.equalsIgnoreCase(stringPrinterIP)) {
            stringPrinterIP = stringPrinterIPL;
            booleanFlag = true;
            // System.out.println(intNo+"stringPrinterL("+stringPrinterL+")("+stringPrinter+")-----------------------OK")
            // ;
            break;
          }
        }
        if (!booleanFlag) {
          messagebox("[" + stringPrinterIP + "]列表機不存在，請洽資訊室安裝。[用戶端 IP：" + stringClinetIP + "]");
          return "ERROR";
        }
      }
    } else {
      stringPrinterIP = exeUtil.getDefaultPrinter();
    }
    //
    System.out.println("預設列表機----------------------[" + stringPrinterIP + "]");
    if (stringPrinterIP.startsWith("PS-") || stringPrinterIP.indexOf("請款申請書") != -1 || stringPrinterIP.indexOf("請購申請書") != -1) {
      messagebox("預設列表機錯誤");
      return "ERROR";
    }
    String stringPrinterIPCF = "";
    String stringPrintable = getValue("Printable").trim();
    String[][] retDoc2M0403 = (new Doc.Doc2M010()).getDoc2M0403("");
    // 0 PrinterName 1 IP 2 ReMark 3 PrinterLabel
    for (int intNo = 0; intNo < retDoc2M0403.length; intNo++) {
      stringPrinterIPCF = retDoc2M0403[intNo][3].trim().toUpperCase();

      if (!"".equals(stringPrinterIPCF) && stringPrinterIP.toUpperCase().indexOf(stringPrinterIPCF) != -1) {
        messagebox("採用雷射列印，預設列表機不可為點陣式列表機。\n請重新設定預設列印機，並重新進入系統。");
        if (!"B3018".equals(getUser()) || !"Y".equals(stringPrintable)) return "ERROR";
      }
    }
    System.out.println("設定列表機----------------------[" + stringPrinterIP + "]");
    return stringPrinterIP;
  }

  public boolean isCanPrint10(FargloryUtil exeUtil) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    JTable jtable = getTable("Table2");
    String stringComNo = getValue("ComNo").trim();
    String stringProjectID1 = "";
    String stringBudgetID = "";
    String stringCostID = "";
    String stringCostID1 = "";
    String stringEDateTime = getValue("EDateTime").trim();
    String stringPurchaseNoExist = getValue("PurchaseNoExist").trim();
    String stringSqlAnd = "";
    String stringFlow = getFunctionName();
    Vector vectorProjectID1 = new Vector();
    Vector vectorBudgetCd = new Vector();
    Hashtable hashtableAnd = new Hashtable();
    //
    if (!"B3018".equals(getUser())) return true;
    if (stringFlow.indexOf("行銷") == -1) return true;
    if (stringFlow.indexOf("經辦") == -1) return true;
    if ("Y".equals(stringPurchaseNoExist)) return true;
    //
    vectorBudgetCd.add("BC31"); // 郵電
    vectorBudgetCd.add("BC32"); // 水電
    vectorBudgetCd.add("BC33"); // 其他勞務費
    vectorBudgetCd.add("BC35"); // 佣金
    vectorBudgetCd.add("BC36"); // 接待中心租賃
    vectorBudgetCd.add("BD46"); // 刷卡手續費
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      stringProjectID1 = ("" + getValueAt("Table2", intNo, "ProjectID1")).trim();
      stringCostID = ("" + getValueAt("Table2", intNo, "CostID")).trim();
      stringCostID1 = ("" + getValueAt("Table2", intNo, "CostID1")).trim();
      stringBudgetID = getBudgetID(stringComNo, "", stringCostID, stringCostID1, dbDoc);
      //
      if (!stringBudgetID.startsWith("B")) continue;
      if (vectorProjectID1.indexOf(stringProjectID1) != -1) continue;
      if (vectorBudgetCd.indexOf(stringBudgetID) != -1) continue;
      //
      vectorProjectID1.add(stringProjectID1);
      // 是否控管
      hashtableAnd.put("ComNo", stringComNo);
      hashtableAnd.put("ProjectID1", stringProjectID1);
      hashtableAnd.put("Contr_STATUS", "B");
      System.out.println("公司 80 %控管-------------------------------------------是否控管");
      if (exeUtil.getQueryDataHashtable("Doc7M02681", hashtableAnd, "", new Vector(), dbDoc).size() == 0) continue;
      // 是否在控管日期內
      hashtableAnd.put("ComNo", stringComNo);
      hashtableAnd.put("ProjectID1", stringProjectID1);
      stringSqlAnd = " AND  TimeStart  <=  '" + stringEDateTime + "' " + " AND  (ISNULL(TimeEnd,  '')  =  ''  OR  TimeEnd  >=  '" + stringEDateTime + "') ";
      System.out.println("公司 80 %控管-------------------------------------------是否在控管日期內");
      if (exeUtil.getQueryDataHashtable("Doc7M02683", hashtableAnd, stringSqlAnd, new Vector(), dbDoc).size() == 0) continue;
      // 非例外控管申請單
      hashtableAnd.put("ComNo", stringComNo);
      hashtableAnd.put("ProjectID1", stringProjectID1);
      hashtableAnd.put("IDExcept", getValue("ID"));
      hashtableAnd.put("DocNoType", "A");
      System.out.println("公司 80 %控管-------------------------------------------是否例外控管申請單");
      if (exeUtil.getQueryDataHashtable("Doc7M02682", hashtableAnd, "", new Vector(), dbDoc).size() == 0) {
        messagebox("業主別-案別(" + stringProjectID1 + ")預算超出80%預警，待企劃主管確認後才可列印+送呈。");
        return false;
      }
    }
    return true;
  }

  public String getBudgetID(String stringComNo, String stringBudgetID, String stringCostID, String stringCostID1, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc7M011 = null;
    // 0 BudgetID 1 CostID 2 CostID1 3 Description
    stringSql = "SELECT  BudgetID,  CostID,  CostID1,  Description " + " FROM  Doc2M020 " + " WHERE  ComNo  =  '" + stringComNo + "' " + " AND  BudgetID  <>  '' "
        + " AND  NOT(BudgetID  IS  NULL) " + " AND  UseStatus  =  'Y' ";
    if (!"".equals(stringBudgetID)) stringSql += " AND  BudgetID  =  '" + stringBudgetID + "' ";
    if (!"".equals(stringCostID)) stringSql += " AND  CostID  =  '" + stringCostID + "' ";
    if (!"".equals(stringCostID1)) stringSql += " AND  CostID1  =  '" + stringCostID1 + "' ";
    stringSql += " ORDER BY BudgetID,  CostID,  CostID1 ";
    retDoc7M011 = dbDoc.queryFromPool(stringSql);
    if (retDoc7M011.length > 0) return retDoc7M011[0][0].trim();
    return "";
  }

  public String getInformation() {
    return "---------------列印按鈕程式.preProcess()----------------";
  }
}
