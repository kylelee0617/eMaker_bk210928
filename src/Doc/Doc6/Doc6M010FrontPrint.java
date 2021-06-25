package Doc.Doc6;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import Farglory.util.FargloryUtil;
import java.net.*;
import Doc.Doc2M010;

public class Doc6M010FrontPrint extends bTransaction {
  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    Doc2M010 exeFun = new Doc2M010();
    FargloryUtil exeUtil = new FargloryUtil();
    String stringBarCode = getValue("BarCode").trim();
    String stringPrintable = getValue("Printable").trim();
    if ("null".equals(stringPrintable)) stringPrintable = "Y";
    String stringToday = datetime.getToday("YYYY/mm/dd");
    String stringLaserPrinter = "";
    String stringPrinterPosition = "";
    String stringFunctionName = getFunctionName();
    String stringDotPrinter = "";
    String stringUser = getUser();
    String stringFunction = "";
    String[] arrayTemp = null;
    boolean booleanDoc10 = stringFunctionName.indexOf("土地") != -1;
    boolean booleanLaser = booleanDoc10;
    //
    // if("2017/07/03".compareTo(stringToday)<=0)
    booleanLaser = true;
    // 買賣借款 雷射列印
    if (!booleanLaser) {
      // String stringToday = datetime.getToday("YYYY/mm/dd") ;
      // if("2016/02/15".compareTo(stringToday) < 0) {
      if (stringFunctionName.indexOf("管理") != -1) booleanLaser = true;
      /*
       * } else { // 2015-1/-04 財務室允許電射列印 String[][] retFE3D05 =
       * exeFun.getFE3D05(stringUser) ; if(retFE3D05.length>0 &&
       * retFE3D05[0][0].startsWith("022")) { booleanLaser = true ; } }
       */
    }
    /*
     * if(booleanLaser && stringFunctionName.indexOf("管理") != -1) { String
     * stringPrintType = exeFun.getNameUnionDoc("PrintType", "Doc5M030",
     * " AND  BarCode  =  '"+stringBarCode+"' ", new Hashtable(), exeUtil) ;
     * if(!"A".equals(stringPrintType)) booleanLaser = false ; }
     */
    //
    if (!isBatchCheckOK()) return false;
    //
    System.out.println("booleanLaser(" + booleanLaser + ")-------------------------");
    if (booleanLaser) {
      stringFunction = booleanDoc10 ? "土地開發借款申請書-列印(Doc10R00101)" : "借款申請書-列印(Doc6R011)"; // 雷射
      if (booleanDoc10) {
        stringFunction = "土地開發借款申請書-列印(Doc10R00101)"; // 雷射
        // if("2017/07/03".compareTo(stringToday)<=0) {
        stringFunction = "土地開發借款申請書-A4列印(Doc10R00101)";
        // }
      } else {
        stringFunction = "借款申請書-列印(Doc6R011)"; // 雷射
        // if(!"B3018".equals(getUser())) {
        stringFunction = "借款申請書-A4列印(Doc6R012)";
        // }
      }
      //
      stringLaserPrinter = exeUtil.getDefaultPrinter();
      if (stringLaserPrinter.startsWith("PS-") || stringLaserPrinter.indexOf("申請書") != -1) {
        messagebox("預設列表機錯誤1");
        return false;
      }
      String stringPrinter = "";
      String[][] retDoc2M0403 = exeFun.getDoc2M0403("");
      // 0 PrinterName 1 IP 2 ReMark 3 PrinterLabel
      for (int intNo = 0; intNo < retDoc2M0403.length; intNo++) {
        stringPrinter = retDoc2M0403[intNo][3].trim().toUpperCase();
        if (!"".equals(stringPrinter) && stringLaserPrinter.toUpperCase().indexOf(stringPrinter) != -1) {
          messagebox("此列表機 為點陣式列表機，請重新設定。");
          return false;
        }
      }
      put("Doc2M010_PRINT_PrinterNAME", stringLaserPrinter);
    } else {
      stringFunction = "請款申請書-列印-Local(Doc2M010)";
      stringDotPrinter = getDotPrinter(exeUtil, exeFun);
      if (stringDotPrinter.endsWith("ERROR")) {
        messagebox(convert.StringToken(stringDotPrinter, "%-%")[0]);
        return false;
      }
      System.out.println("(" + stringDotPrinter + ")-------------------------");
      arrayTemp = convert.StringToken(stringDotPrinter, "%-%");
      stringDotPrinter = convert.replace(arrayTemp[0], "/", "\\");
      stringPrinterPosition = arrayTemp[1];
      System.out.println("取得 點陣式列表機(" + stringDotPrinter + ")(" + stringPrinterPosition + ")---------------------------------------");
      put("Doc2M010_PRINT_PrinterNAME", stringDotPrinter);
    }
    put("Doc2M010_PRINT_Enable", stringPrintable);
    put("Doc2M010_PRINT_BarCode", stringBarCode);
    put("Doc2M010_PRINT_Function", stringFunctionName);
    put("Doc2M010_FUNCTION", "C");
    //
    System.out.println("stringPrintable(" + stringPrintable + ")--------------------------------" + stringFunction);
    showForm(stringFunction);
    
    //21.06.21 KYLE : 已在 借款申請書-A4列印(Doc6R012) 中處理，此處多餘
//    if ("B3018,".indexOf(stringUser) == -1 && "Y".equals(stringPrintable)) {
//      getInternalFrame(stringFunction).setVisible(false);
//    }
    
    // 成功訊息
    String stringPrintSuccess = ("" + get("Doc2M010_Print_OK")).trim();
    if ("OK".equals(stringPrintSuccess) && "Y".equals(stringPrintable)) {
      String stringTemp = "完成列印。";
      if (booleanLaser) stringTemp += "\n(取消 [點陣式列印]，改為 [雷射列印])";
      
      //申20210601006 Kyle : 因疫情改為手動列印，通知訊息修改
//      stringTemp = "請選擇列印方式~";
//      messagebox(stringTemp);
    }
    System.out.println("--------------------------------列印完成" + stringFunction);
    put("Doc2M010_Print_OK", "null");
    return false;
  }

  public boolean isBatchCheckOK() throws Throwable {
    String stringBarCode = getValue("BarCode").trim();
    if ("".equals(stringBarCode)) return false;
    //
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    String stringEmployeeNo = getValue("EmployeeNo").trim();
    String stringUser = getUser().toUpperCase();
    if (!stringDepartNoSubject.startsWith("033") && !stringEmployeeNo.equals(stringUser.toUpperCase()) && ",B1721,B3018,".indexOf(stringUser) == -1) {
      messagebox("非輸入人員，不可列印。");
      return false;
    }
    return true;
  }

  public String getDotPrinter(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
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
      return stringDotPrinter + "%-%";
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
      String[][] retDoc2M0403 = exeFun.getDoc2M0403(arrayText[0].trim());
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
      return stringDotPrinter + "%-%" + stringPrinterPosition;
    }
    return "%-%";
  }

  public String getInformation() {
    return "---------------列印按鈕程式.preProcess()----------------";
  }
}
