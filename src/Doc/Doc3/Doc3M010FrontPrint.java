package Doc.Doc3;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import java.net.*;
import Farglory.util.FargloryUtil;
import Doc.Doc2M010;

public class Doc3M010FrontPrint extends bTransaction {
  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    // isB34ControlPrint 營業部控管列印
    // doB34Print 營業部列印
    Doc2M010 exeFun = new Doc2M010();
    FargloryUtil exeUtil = new FargloryUtil();
    JButton jButton = getButton("Button13");
    JTable jtable = getTable("Table3");
    String stringBarCode = getValue("BarCode").trim();
    String stringComNo = getValue("ComNo").trim();
    String stringPrintable = getValue("Printable").trim();
    String stringToday = datetime.getToday("YYYY/mm/dd");
    String stringUser = getUser();
    String stringFunctionName = getFunctionName();

    // Excel 列印
    if (!"OO".equals(stringComNo)) {
      getButton("ButtonPrint").doClick();
      return false;
    }
    
    /**
     * 
     * 靠杯阿這邊下面好像都是沒用的
     * (公司都會是CS，應該上面就要被轉走)
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */

    //
    System.out.println("行銷-公司-案別 80% 控管列印-----------------S");
    if (!isCanPrint10(exeUtil, exeFun)) return false;
    System.out.println("行銷-公司-案別 80% 控管列印-----------------E");

    // 營業部之管控費用列印
    Vector vectorProjectID1 = new Vector();
    String stringLaserPrinter = isB34ControlPrint(exeUtil, exeFun, vectorProjectID1);
    if ("ERROR".equals(stringLaserPrinter)) return false;

    // [特殊人員] 列印 [請購單] 採用 [雷射列印機]
    Vector vectorEmployeeNo = exeFun.getEmployeeNoDoc3M011("10", "");
    String stringFunction = "";
    String stringDotPrinter = "";
    String stringPrinterPosition = "";
    String[] arrayTemp = null;

    if ("OO".equals(stringComNo) || vectorEmployeeNo.indexOf(stringUser.toUpperCase()) != -1) {
      System.out.println("[特殊人員] 列印 [請購單] 採用 [雷射列印機](" + stringUser + ")---------------------------------------");
      //
      stringFunction = "OO".equals(stringComNo) ? "請購單列印-物管(Doc3R014)" : "請購單列印(Doc3R011)";
      // 取得 雷射列表機
      if ("".equals(stringLaserPrinter)) {
        stringLaserPrinter = getLaserPrinter(exeUtil, exeFun);
        if (stringLaserPrinter.endsWith("ERROR")) {
          messagebox(convert.StringToken(stringLaserPrinter, "%-%")[0]);
          return false;
        }
      }
      if ("B3018".equals(stringUser)) {
        stringFunction = "請購單列印-A4(Doc3R015)";
      }
      System.out.println("取得 雷射列表機(" + stringLaserPrinter + ")---------------------------------------");
    } else {
      // 取得 點陣式列表機
      stringDotPrinter = getDocPrinter(exeUtil, exeFun);
      if (stringDotPrinter.endsWith("ERROR")) {
        messagebox(convert.StringToken(stringDotPrinter, "%-%")[0]);
        return false;
      }
      arrayTemp = convert.StringToken(stringDotPrinter, "%-%");
      stringDotPrinter = arrayTemp[0];
      stringFunction = arrayTemp[1];
      stringPrinterPosition = arrayTemp[2];
      System.out.println("取得 點陣式列表機(" + stringDotPrinter + ")(" + stringPrinterPosition + ")---------------------------------------");
    }

    // 列印參數
    if (!"".equals(stringLaserPrinter)) {
      put("Doc3M011_PRINT_BarCode", "");
      showForm(stringFunction);
    }
    if ("".equals(stringDotPrinter)) stringDotPrinter = stringLaserPrinter;
    put("Doc3M011_PRINT_PrinterNAME", stringDotPrinter.replace("/", "\\"));
    put("Doc3M011_PRINT_BarCode", stringBarCode);
    put("Doc3M011_PRINT_Function", stringFunctionName);
    put("Doc3M011_PRINT_Enable", stringPrintable);
    
    // 列印呼叫
    System.out.println("stringFunction(" + stringFunction + ")stringPrintable(" + stringPrintable + ")------------------------------------");
    showForm(stringFunction);
    
    // 列印後關閉
    if ("Y".equals(stringPrintable) && "B3018,".indexOf(getUser()) == -1) getInternalFrame(stringFunction).setVisible(false);
    
    // 成功訊息
    String stringPrintSuccess = ("" + get("Doc3M011_Print_OK")).trim();
    if ("Y".equals(stringPrintable) && "OK".equals(stringPrintSuccess)) {
      // 呼叫 簽核
      // if("Y".equals(stringPrintable)) showForm("請購單列印-A4-簽核(Doc3R0151)") ;
      // 分攤表列印
      doPrintAfter(stringBarCode, jtable, jButton, exeUtil, exeFun);
      // 顯示訊息
      if (!"".equals(stringPrinterPosition)) stringPrinterPosition = "\n請前往 [" + stringPrinterPosition + "] 拿取請購單。";
      messagebox("完成列印。" + stringPrinterPosition);
      // 營業列印管控表
      doB34Print(stringLaserPrinter, vectorProjectID1);
    }
    System.out.println("--------------------------------列印完成" + stringFunction);
    put("Doc3M011_Print_OK", "null");

    return false;
  }

  public void doPrintAfter(String stringBarCode, JTable jtable, JButton jButton, FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    if ("0221".equals(stringDepartNoSubject)) return;
    String stringInOut = "";
    String stringDepart = "";
    String stringProjectID = "";
    String stringProjectID1 = "";
    String stringKey = "";
    String stringTableName = getFunctionName().indexOf("Doc3") != -1 ? "Doc3M011" : "Doc5M011";
    Vector vectorKey = new Vector();
    boolean booleanExcel = false;
    // 第一次才列印
    String stringPrintCount = exeFun.getNameUnionDoc("PrintCount", stringTableName, " AND  BarCode  =  '" + stringBarCode + "' ", new Hashtable(), exeUtil);
    if (exeUtil.doParseInteger(stringPrintCount) > 1) return;
    // 多案別 或 多部門 才列印
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      stringInOut = ("" + jtable.getValueAt(intNo, 2)).trim();
      stringDepart = ("" + jtable.getValueAt(intNo, 3)).trim();
      stringProjectID = ("" + jtable.getValueAt(intNo, 4)).trim();
      stringProjectID1 = ("" + jtable.getValueAt(intNo, 5)).trim();
      stringKey = stringInOut + "%-%" + stringDepart + "%-%" + stringProjectID + "%-%" + stringProjectID1;
      //
      if (vectorKey.indexOf(stringKey) == -1) vectorKey.add(stringKey);
      //
      if (vectorKey.size() > 1) {
        booleanExcel = true;
        break;
      }
    }
    if (booleanExcel) {
      jButton.doClick();
    }
  }

  public String getDocPrinter(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    String stringFilePath = "C:\\Emaker_Print\\DocPrint3.ini"; // 直接指定列表機
    String stringDotPrinter = "";
    String stringFunction = "";
    String[] arrayText = null;
    File filePrinterPath = new File(stringFilePath);
    if (filePrinterPath.exists()) {
      // 直接指定列表機
      arrayText = exeUtil.getArrayDataFromText(stringFilePath);
      stringDotPrinter = convert.replace(arrayText[0].trim(), "\\", "/");
      if ("".equals(stringDotPrinter)) {
        return "資料發生錯誤，請洽資訊室設定1。%-%ERROR";
      }
      if (!exeUtil.isPrinterExist(stringDotPrinter)) {
        Vector vectorPrinter = exeUtil.getPrinterPosition("/");
        String stringPrinterL = "";
        for (int intNo = 0; intNo < vectorPrinter.size(); intNo++) {
          stringPrinterL = ("" + vectorPrinter.get(intNo)).trim();
          if (stringPrinterL.equalsIgnoreCase(stringDotPrinter)) stringDotPrinter = stringPrinterL;
        }
        if (!exeUtil.isPrinterExist(stringDotPrinter)) {
          return "[" + stringDotPrinter + "]列表機不存在，請洽資訊室安裝。%-%ERROR";
        }
      }
      return stringDotPrinter + "%-%請購申請書-列印-一般(Doc3M010)%-%";
    }

    //
    stringFilePath = "C:\\Emaker_Print\\DocPrinter.ini"; // 對照 取得 列表機
    filePrinterPath = new File(stringFilePath);
    if (!filePrinterPath.exists()) {
      stringFilePath = "C:\\DocPrinter.ini";
      filePrinterPath = new File(stringFilePath);
    }
    if (!filePrinterPath.exists()) {
      return "尚未設定列表機，請洽資訊室設定。%-%ERROR";
    }
    arrayText = exeUtil.getArrayDataFromText(stringFilePath);
    if (arrayText.length == 3) {
      String stringPrinterIP = "";
      String stringPrinterLabel = "";
      String stringPrinterPosition = "";
      // 對照表 取得列表機
      String[][] retDoc2M0403 = exeFun.getDoc2M0403(arrayText[1].trim());
      if (retDoc2M0403.length == 0) {
        return "發生錯誤，請通知資訊室處理3%-%ERROR";
      }
      stringPrinterIP = retDoc2M0403[0][1].trim();
      stringPrinterPosition = retDoc2M0403[0][2].trim();
      stringPrinterLabel = retDoc2M0403[0][3].trim();
      if (stringPrinterIP.length() == 0) {
        return "列表機設定錯誤，請通知資訊室處理4%-%ERROR";
      }
      if (stringPrinterPosition.length() == 0) {
        return "列表機設定錯誤，請通知資訊室處理5%-%ERROR";
      }
      if (stringPrinterLabel.length() == 0) {
        return "列表機設定錯誤，請通知資訊室處理6%-%ERROR";
      }
      stringDotPrinter = "//" + stringPrinterIP + "/" + stringPrinterLabel;
      //
      if (!exeUtil.isPrinterExist(stringDotPrinter)) {
        Vector vectorPrinter = exeUtil.getPrinterPosition("/");
        String stringPrinterL = "";
        for (int intNo = 0; intNo < vectorPrinter.size(); intNo++) {
          stringPrinterL = ("" + vectorPrinter.get(intNo)).trim();
          if (stringPrinterL.equalsIgnoreCase(stringDotPrinter)) stringDotPrinter = stringPrinterL;
        }
        if (!exeUtil.isPrinterExist(stringDotPrinter)) {
          return "[" + stringDotPrinter + "]列表機不存在，請洽資訊室安裝。%-%ERROR";
        }
      }
      return stringDotPrinter + "%-%請購申請書-列印-一般(Doc3M010)%-%" + stringPrinterPosition;
    }

    // 舊版
    // 依 案別 標簽指定功能，進而選擇列表機
    // 新進設定已不使用此種方式
    Vector vectorProject = new Vector();
    String stringProject = arrayText[0].trim();
    if (stringProject.length() == 0) {
      return "列表機設定錯誤，請通知資訊室處理7%-%ERROR";
    }
    if (arrayText.length >= 2 && vectorProject.indexOf(arrayText[1].trim() + stringProject) != -1) {
      stringProject = arrayText[1].trim() + stringProject;
    }
    // 已存在的案別名稱
    vectorProject.add("Local");
    vectorProject.add("人總");
    //
    vectorProject.add("F1");
    vectorProject.add("H40");
    vectorProject.add("H45");
    vectorProject.add("H45_Local");
    vectorProject.add("H52");
    vectorProject.add("H52Local");
    vectorProject.add("H59A");
    vectorProject.add("H61");
    vectorProject.add("H63");
    vectorProject.add("H120");
    vectorProject.add("M");
    vectorProject.add("未來市");
    vectorProject.add("大學劍橋");
    vectorProject.add("爵士");
    vectorProject.add("國際金融廣場");
    vectorProject.add("國際金融廣場Local");
    //
    if (vectorProject.indexOf(stringProject) == -1) {
      return "列表機設定錯誤，請通知資訊室處理8%-%ERROR";
    }
    stringFunction = "請購申請書-列印-" + stringProject + "(Doc3M010)";
    return "%-%" + stringFunction + "%-%";
  }

  public String isB34ControlPrint(FargloryUtil exeUtil, Doc2M010 exeFun, Vector vectorProjectID1) throws Throwable {
    String stringUser = getUser().toUpperCase();
    String stringDepartNoSubjectN = "" + get("EMP_DEPT_CD_N"); //
    // 特定人員才作處理
    if ("B3018,".indexOf(stringUser) == -1) return "";
    // 僅控管營業部
    if (!"B34A".equals(stringDepartNoSubjectN)) return "";
    //
    String stringDocNo = getValue("DocNo1").trim() + "-" + getValue("DocNo2").trim() + "-" + getValue("DocNo3").trim();
    String stringEDateTime = getValue("EDateTime").trim();
    String stringLaserPrinter = "";
    //
    if (!isCanPrint(exeUtil, vectorProjectID1)) return "ERROR";
    //
    if (vectorProjectID1.size() > 0) {
      stringLaserPrinter = getLaserPrinter(exeUtil, exeFun);
      if (stringLaserPrinter.endsWith("ERROR")) {
        messagebox(convert.StringToken(stringLaserPrinter, "%-%")[0]);
        return "ERROR";
      }
      put("Doc5R017_DocNo", stringDocNo);
      put("Doc5R017_BarCode", getValue("BarCode").trim());
      put("Doc5R017_EDateTime", stringEDateTime);
    }
    return stringLaserPrinter;
  }

  public String getLaserPrinter(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    String stringFilePathL = "c:\\Emaker_Print\\DocPrint2.ini";
    String stringPrinterL = "";
    String stringLaserPrinter = "";
    String[] arrayTextL = null;
    if ((new File(stringFilePathL)).exists()) {
      System.out.println("c:\\Emaker_Print\\DocPrint2.ini 存在---------------------------------------");
      arrayTextL = exeUtil.getArrayDataFromText(stringFilePathL);
      if (arrayTextL.length != 0) {
        stringPrinterL = arrayTextL[0].trim();
      }
      if (!"".equals(stringPrinterL)) {
        // 列表機存在檢查
        if (!exeUtil.isPrinterExist(stringPrinterL, "\\")) {
          Vector vectorPrinter = exeUtil.getPrinterPosition("\\");
          String stringPrinterL2 = "";
          boolean booleanFlag = false;
          for (int intNo = 0; intNo < vectorPrinter.size(); intNo++) {
            stringPrinterL2 = ("" + vectorPrinter.get(intNo)).trim();
            if (stringPrinterL2.equalsIgnoreCase(stringPrinterL)) {
              stringPrinterL = stringPrinterL2;
              booleanFlag = true;
              break;
            }
          }
          if (!booleanFlag) {
            return "[" + stringPrinterL + "]雷射列表機不存在，請安裝後，再繼續進行 [營業掌控列印]。%-%ERROR";
          }
        }
      }
    }
    System.out.println("stringPrinterL(" + stringPrinterL + ")---------------------------------------1");
    if (!"".equals(stringPrinterL)) {
      stringLaserPrinter = stringPrinterL;
    } else {
      stringLaserPrinter = exeUtil.getDefaultPrinter();
      System.out.println("stringLaserPrinter(" + stringLaserPrinter + ")---------------------------------------2");
    }
    System.out.println("stringLaserPrinter(" + stringLaserPrinter + ")---------------------------------------3");
    if (stringLaserPrinter.indexOf("申請書") != -1) {
      return "雷射列表機 為點陣式列表機，請重新設定。%-%ERROR";
    }
    String stringPrinter = "";
    String[][] retDoc2M0403 = exeFun.getDoc2M0403("");
    // 0 PrinterName 1 IP 2 ReMark 3 PrinterLabel
    for (int intNo = 0; intNo < retDoc2M0403.length; intNo++) {
      stringPrinter = retDoc2M0403[intNo][3].trim();
      if (!"".equals(stringPrinter) && stringLaserPrinter.indexOf(stringPrinter) != -1) {
        return "雷射列表機 為點陣式列表機，請重新設定。%-%ERROR";
      }
    }
    return stringLaserPrinter;
  }

  public void doB34Print(String stringDoc5R017PrinterIP, Vector vectorProjectID1) throws Throwable {
    if (vectorProjectID1.size() == 0) return;
    //
    String stringFunctionL = "營業管控列印(Doc5R017)";
    String stringProjectID1 = "";
    //
    put("Doc5R017_PRINT_PrinterNAME", stringDoc5R017PrinterIP);
    for (int intNo = 0; intNo < vectorProjectID1.size(); intNo++) {
      stringProjectID1 = "" + vectorProjectID1.get(intNo);
      //
      put("Doc5R017_ProjectID1", stringProjectID1);
      //
      showForm(stringFunctionL);
      if ("B3018,b4177,".indexOf(getUser()) == -1) getInternalFrame(stringFunctionL).setVisible(false);
    }
  }

  public boolean isCanPrint(FargloryUtil exeUtil, Vector vectorProjectID1) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    JTable jtable = getTable("Table3");
    String stringProjectID1 = "";
    String stringCostID = "";
    String stringRealMoney = "";
    String stringCostIDCF = "D0101,D0201,D0301," + // 設計缺失
        "D0102,D0202,D0302," + // 營造缺失
        "D0103,D0203,D0303," + // 行銷缺失
        "D0104,D0204,D0304," + // 營業缺失
        "D0105,D0205,D0305," + // 協商補償
        "D0401,D0402,D0403,D0404,D0405,"; // 專案費用
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      stringProjectID1 = ("" + getValueAt("Table3", intNo, "ProjectID1")).trim();
      stringCostID = ("" + getValueAt("Table3", intNo, "CostID")).trim();
      stringCostID = exeUtil.doSubstring(stringCostID, 0, 5);
      //
      if (vectorProjectID1.indexOf(stringProjectID1) != -1) continue;
      //
      // 請款代碼 存在 檢核
      if (stringCostIDCF.indexOf(stringCostID) == -1) continue;
      // 案別存在 Doc5R017 檢核
      stringRealMoney = exeUtil.getNameUnion("RealMoney", "Doc5M017", " AND  ProjectID1  =  '" + stringProjectID1 + "' ", new Hashtable(), dbDoc);
      if ("".equals(stringRealMoney)) {
        messagebox("[案別] 尚未建立管控資料，不允許列印。 ");
        return false;
      }
      vectorProjectID1.add(stringProjectID1);
    }
    return true;
  }

  public boolean isCanPrint10(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    JTable jtable = getTable("Table3");
    String stringComNo = getValue("ComNo").trim();
    String stringProjectID1 = "";
    String stringBudgetID = "";
    String stringCostID = "";
    String stringCostID1 = "";
    String stringEDateTime = getValue("EDateTime").trim();
    String stringSqlAnd = "";
    String stringFlow = getFunctionName();
    Vector vectorProjectID1 = new Vector();
    Vector vectorBudgetCd = new Vector();
    Hashtable hashtableAnd = new Hashtable();
    //
    if (!"".equals(getUser())) return true;
    if (stringFlow.indexOf("行銷") == -1) return true;
    if (stringFlow.indexOf("經辦") == -1) return true;
    //
    vectorBudgetCd.add("BC31"); // 郵電
    vectorBudgetCd.add("BC32"); // 水電
    vectorBudgetCd.add("BC33"); // 其他勞務費
    vectorBudgetCd.add("BC35"); // 佣金
    vectorBudgetCd.add("BC36"); // 接待中心租賃
    vectorBudgetCd.add("BD46"); // 刷卡手續費
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      stringProjectID1 = ("" + getValueAt("Table3", intNo, "ProjectID1")).trim();
      stringCostID = ("" + getValueAt("Table3", intNo, "CostID")).trim();
      stringCostID1 = ("" + getValueAt("Table3", intNo, "CostID1")).trim();
      stringBudgetID = getBudgetID(stringComNo, "", stringCostID, stringCostID1, exeFun);
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

  public String getBudgetID(String stringComNo, String stringBudgetID, String stringCostID, String stringCostID1, Doc2M010 exeFun) throws Throwable {
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
    retDoc7M011 = exeFun.getTableDataDoc(stringSql);
    if (retDoc7M011.length > 0) return retDoc7M011[0][0].trim();
    return "";
  }

  public String getInformation() {
    return "---------------列印按鈕程式.preProcess()----------------";
  }
}
