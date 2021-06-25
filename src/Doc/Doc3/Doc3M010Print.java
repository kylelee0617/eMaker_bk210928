package Doc.Doc3;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import Farglory.util.FargloryUtil;

public class Doc3M010Print extends bTransaction {
  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    System.out.println("列印----------------------S");
    talk dbDoc = getTalk("" + get("put_Doc"));
    FargloryUtil exeUtil = new FargloryUtil();
    String stringBarCode = ("" + get("Doc3M011_PRINT_BarCode")).trim();
    if ("".equals(stringBarCode) || "null".equals(stringBarCode)) {
      return false;
    }
    String stringPrevFunction = ("" + get("Doc3M011_PRINT_Function")).trim();
    String stringPrinterNAME = ("" + get("Doc3M011_PRINT_PrinterNAME")).trim();
    String stringPrintable = "" + get("Doc3M011_PRINT_Enable");
    String stringFlow = getFunctionName();
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    boolean booleanFlow = true;
    boolean booleanstringFunction = true;
    boolean booleanSource = stringPrevFunction.indexOf("Doc5M") == -1;
    boolean booleanPrintable = "Y".equals(stringPrintable);
    
    //
    System.out.println("booleanSource------------------------------------[" + stringPrevFunction + "][" + booleanSource + "]");
    // setVisible("BarCode", "B3018".equals(getUser())) ;
    setValue("BarCode", stringBarCode);
    if (!"".equals(stringPrinterNAME) && !"null".equals(stringPrinterNAME)) {
      setPrinter(stringPrinterNAME);
      System.out.println("stringPrinterNAME------------------------------------[" + stringPrinterNAME + "]");
    }
    
    //
    setVisible("PrintCountPrint", false);
    if (!booleanSource || stringDepartNoSubject.indexOf("023") != -1) {
      setVisible("BarCode", true);
      setPrintable("BarCode", true);
    } else {
      setVisible("BarCode", false);
      setPrintable("BarCode", false);
    }
    booleanFlow = doPrint(stringBarCode, booleanPrintable, booleanSource, exeUtil, dbDoc);
    if (booleanPrintable && booleanFlow) {
      String stringPrintCount = "" + (exeUtil.doParseInteger(getValue("PrintCountPrint").trim()) + 1);
      if ("B3018,TEST,b3249,".indexOf(getUser()) == -1) {
        doUpdateStatus(booleanSource ? "Doc3M011" : "Doc5M011", "PrintCount", stringPrintCount, stringBarCode);
      }
      put("Doc3M011_Print_OK", "OK");
    }
    System.out.println("列印----------------------E");
    return false;
  }

  public boolean doPrint(String stringBarCode, boolean booleanPrintable, boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    int intMaxRow = 0;
    int intPos = 0;
    int intPrintCount = 0;
    int intCountForPage = 5; // 頁行數
    String stringApplyMoney = "";
    String stringApplyType = null;
    String stringApplyTypeL = null;
    String stringBudgetName = "";
    String stringBudgetNum = "";
    String stringCheckAdd = "";
    String stringApplyTxt = "";
    String stringCheckAddDescript = "";
    String stringClassName = "";
    String stringClassNamePrint1 = "";
    String stringClassNamePrint2 = "";
    String stringClassNameDescript = "";
    String stringClassNameDescriptPrint1 = "";
    String stringClassNameDescriptPrint2 = "";
    String stringCostID = "";
    String stringCostID1 = "";
    String stringFactoryName = "";
    String stringFactoryNo = "";
    String stringFactoryNoL = "";
    String stringHistoryPrice = "";
    String stringUnit = "";
    String stringFunctionName = getFunctionName();
    String stringPayCondition1 = "";
    String stringPayCondition2 = "";
    String stringPurchaseMoney = "";
    String stringPurchaseMoneyL = "";
    String stringTable1Source = "";
    String stringTemp = "";
    String stringActualPrice = "";
    String stringActualPriceL = "";
    String stringUnipurchase = "";
    String stringComNo = "";
    String stringUser = getUser().toUpperCase();
    String[] arrayTemp = null;
    String[][] retDoc3M011 = getDoc3M011(booleanSource ? "Doc3M011" : "Doc5M011", stringBarCode);
    stringComNo = retDoc3M011[0][0].trim();
    String[][] retDoc3M012 = getDoc3M012(booleanSource ? "Doc3M012" : "Doc5M012", stringBarCode);
    String[][] retDoc3M013 = getDoc3M013(booleanSource ? "Doc3M013" : "Doc5M013", stringBarCode);
    String[][] retDoc7M011 = null;
    double doubleTemp = 0;
    Vector vectorTemp = new Vector();
    Vector vectorFactoryNo = new Vector();
    Hashtable hashtableFactoryNoGetPayCondition = new Hashtable();
    Hashtable hashtablePurchaseMoney = new Hashtable();
    Vector vectorLaserPrinter = getEmployeeNoDoc3M011("10", "");
    boolean booleanPrintNew = "OO".equals(stringComNo) || (!"".equals(stringUser) && vectorLaserPrinter.indexOf(stringUser) != -1);
    //
    if (retDoc3M011.length == 0) {
      JOptionPane.showMessageDialog(null, "查無資料。", "訊息", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (retDoc3M012.length == 0) {
      JOptionPane.showMessageDialog(null, "查 [請款資訊] 無資料。", "訊息", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    //
    stringApplyType = retDoc3M011[0][4].trim();
    stringApplyTypeL = stringApplyType;
    stringUnipurchase = retDoc3M011[0][16].trim();
    if (booleanSource) {
      if ("F".equals(stringApplyType)) {
        stringUnipurchase = "Y";
      } else {
        stringUnipurchase = "N";
      }
    }
    // 請款單左上角增加列印時間：(097/02 17:00) PrintDateTime2
    String stringToday = datetime.getTime("yy/mm/dd h:m:s");
    int intPosS = stringToday.indexOf("/");
    int intPosE = stringToday.lastIndexOf(":");
    setValue("PrintDateTime2", "列印時間：" + stringToday.substring(intPosS + 1, intPosE));
    if (!booleanPrintNew) {
      getcLabel("PrintDateTime2").setLocation(20, 40);
      getcLabel("BarCode").setLocation(17, 60);
    } else {
      // 雷射
      getcLabel("PrintDateTime2").setLocation(12, 5);
      getcLabel("BarCode").setLocation(7, 25);
    }
    // 幣別 18
    String stringCoinType = retDoc3M011[0][18].trim();
    String stringCoinName = exeUtil.getNameUnion("CoinName", "Doc7M001", " AND  CoinType  =  '" + stringCoinType + "' ", new Hashtable(), dbDoc);
    if (!"".equals(stringCoinName)) {
      if (booleanPrintNew) {
        // 雷射
        stringCoinName = "幣別：" + stringCoinName;
      } else {
        stringCoinName = "幣別：" + stringCoinName;
        getcLabel("CoinType").setLocation(875, 115);
      }
    }
    setValue("CoinType", stringCoinName);
    // 承辦人 及 列印日期
    String stringOriEmployeeNo = retDoc3M011[0][17].trim();
    stringToday = datetime.getToday("yy/mm/dd");
    //
    intPos = stringToday.indexOf("/");
    stringToday = stringToday.substring(intPos + 1);
    //
    setValue("OriEmployeeNo", getEmpName(stringOriEmployeeNo, exeUtil));
    setValue("PrintDateTime", stringToday);
    if (!booleanPrintNew) {
      // if(!"B3018".equals(getUser())) {
      getcLabel("OriEmployeeNo").setLocation(935, 703);
      getcLabel("PrintDateTime").setLocation(935, 723);
    } else {
      // getcLabel("OriEmployeeNo").setLocation(920,670) ;
      // getcLabel("PrintDateTime").setLocation(920,690) ;
    }

    // System.out.println("stringApplyType--------------------------------["+stringApplyType+"]");
    // System.out.println("stringUnipurchase--------------------------------["+stringUnipurchase+"]");
    
    if ("Y".equals(stringUnipurchase)) {
      if (booleanSource || retDoc3M013.length > 0) {
        if (retDoc3M013.length == 0) {
          JOptionPane.showMessageDialog(null, "查 [付款資訊] 無資料。", "訊息", JOptionPane.ERROR_MESSAGE);
          return false;
        }
        // 統購列印-廠商整理
        // 0 FactoryNo 1 PurchaseSumMoney 2 PercentRate 3 MonthNum 4 PurchaseMoney
        // 5 PayCondition1 6 PayCondition2 7 Descript 8 NoUseRealMoney
        for (int intNo = 0; intNo < retDoc3M013.length; intNo++) {
          stringFactoryNo = retDoc3M013[intNo][0].trim();
          stringPurchaseMoney = retDoc3M013[intNo][4].trim();
          stringPayCondition1 = retDoc3M013[intNo][5].trim();
          stringPayCondition2 = retDoc3M013[intNo][6].trim();
          // 廠商
          if (vectorFactoryNo.indexOf(stringFactoryNo) == -1) vectorFactoryNo.add(stringFactoryNo);
          // 付款條件(付款金額)
          vectorTemp = (Vector) hashtableFactoryNoGetPayCondition.get(stringFactoryNo);
          if (vectorTemp == null) {
            vectorTemp = new Vector();
          }
          if (vectorTemp.indexOf(stringPayCondition1) == -1 && !"999".equals(stringPayCondition1)) {
            doubleTemp = exeUtil.doParseDouble("" + hashtablePurchaseMoney.get(stringFactoryNo + "-" + stringPayCondition1)) + exeUtil.doParseDouble(stringPurchaseMoney);
            hashtablePurchaseMoney.put(stringFactoryNo + "-" + stringPayCondition1, convert.FourToFive("" + doubleTemp, 0));
            //
            vectorTemp.add(stringPayCondition1);
          }
          if (vectorTemp.indexOf(stringPayCondition2) == -1 && !"999".equals(stringPayCondition2)) {
            doubleTemp = exeUtil.doParseDouble("" + hashtablePurchaseMoney.get(stringFactoryNo + "-" + stringPayCondition2)) + exeUtil.doParseDouble(stringPurchaseMoney);
            hashtablePurchaseMoney.put(stringFactoryNo + "-" + stringPayCondition2, convert.FourToFive("" + doubleTemp, 0));
            //
            vectorTemp.add(stringPayCondition2);
          }
          hashtableFactoryNoGetPayCondition.put(stringFactoryNo, vectorTemp);
          // 付款金額
        }
      } else {
        String stringRecordNo = "";
        String stringRecordNoL = "";
        String[][] retDoc3M0130 = getDoc3M0130("Doc5M0130", stringBarCode);
        if (retDoc3M0130.length == 0) {
          JOptionPane.showMessageDialog(null, "查 [付款資訊] 無資料。", "訊息", JOptionPane.ERROR_MESSAGE);
          return false;
        }
        for (int intNo = 0; intNo < retDoc3M012.length; intNo++) {
          stringFactoryNo = retDoc3M012[intNo][8].trim();
          stringRecordNo = retDoc3M012[intNo][15].trim();
          // 廠商
          if (vectorFactoryNo.indexOf(stringFactoryNo) == -1) vectorFactoryNo.add(stringFactoryNo);
          // 0 RecordNo 1 PayCondition1 2 PayCondition2 3 PurchaseMoney
          for (int intNoL = 0; intNoL < retDoc3M0130.length; intNoL++) {
            stringRecordNoL = retDoc3M0130[intNoL][0].trim();
            System.out
                .println("stringFactoryNo(" + stringFactoryNo + ")stringRecordNo(" + stringRecordNo + ")stringRecordNoL(" + stringRecordNoL + ")------------------------------");
            if (!stringRecordNoL.equals(stringRecordNo)) continue;
            stringPayCondition1 = retDoc3M0130[intNoL][1].trim();
            stringPayCondition2 = retDoc3M0130[intNoL][2].trim();
            stringPurchaseMoney = retDoc3M0130[intNoL][3].trim();
            // 付款條件(付款金額)
            vectorTemp = (Vector) hashtableFactoryNoGetPayCondition.get(stringFactoryNo);
            if (vectorTemp == null) {
              vectorTemp = new Vector();
              hashtableFactoryNoGetPayCondition.put(stringFactoryNo, vectorTemp);
              System.out.println("NEW  stringFactoryNo(" + stringFactoryNo + ")stringRecordNo(" + stringRecordNo + ")stringRecordNoL(" + stringRecordNoL + ")(" + vectorTemp.size()
                  + ")------------------------------");
            }
            if (vectorTemp.indexOf(stringPayCondition1) == -1 && !"999".equals(stringPayCondition1)) {
              doubleTemp = exeUtil.doParseDouble("" + hashtablePurchaseMoney.get(stringFactoryNo + "-" + stringPayCondition1)) + exeUtil.doParseDouble(stringPurchaseMoney);
              hashtablePurchaseMoney.put(stringFactoryNo + "-" + stringPayCondition1, convert.FourToFive("" + doubleTemp, 0));
              //
              if (vectorTemp.indexOf(stringPayCondition1) == -1) {
                vectorTemp.add(stringPayCondition1);
                System.out.println("stringFactoryNo(" + stringFactoryNo + ")stringRecordNoL(" + stringRecordNoL + ")stringPayCondition1(" + stringPayCondition1 + ")("
                    + vectorTemp.size() + ")------------------------------1");
              }
            }
            if (vectorTemp.indexOf(stringPayCondition2) == -1 && !"999".equals(stringPayCondition2)) {
              doubleTemp = exeUtil.doParseDouble("" + hashtablePurchaseMoney.get(stringFactoryNo + "-" + stringPayCondition2)) + exeUtil.doParseDouble(stringPurchaseMoney);
              hashtablePurchaseMoney.put(stringFactoryNo + "-" + stringPayCondition2, convert.FourToFive("" + doubleTemp, 0));
              //
              if (vectorTemp.indexOf(stringPayCondition2) == -1) {
                System.out.println("stringFactoryNo(" + stringFactoryNo + ")stringRecordNoL(" + stringRecordNoL + ")stringPayCondition2(" + stringPayCondition2 + ")("
                    + vectorTemp.size() + ")------------------------------2");
                vectorTemp.add(stringPayCondition2);
              }
            }
          }
        }
      }
      // 廠商列印
      for (int intNo = 0; intNo < 4; intNo++) {
        if (intNo < vectorFactoryNo.size()) {
          stringFactoryNo = ("" + vectorFactoryNo.get(intNo)).trim();
          stringFactoryName = getFactoryNameForDoc3M015(stringFactoryNo);
        } else {
          stringFactoryNo = "";
          stringFactoryName = "";
        }
        setValue("FactoryNoPrint" + (intNo + 1), stringFactoryNo);
        setValue("FactoryNamePrint" + (intNo + 1), stringFactoryName);
      }
    } else {
      // 廠商列印
      for (int intNo = 0; intNo < 4; intNo++) {
        setValue("FactoryNoPrint" + (intNo + 1), "");
        setValue("FactoryNamePrint" + (intNo + 1), "");
      }
    }
    // 頁數
    intMaxRow = retDoc3M012.length;
    intPrintCount = intMaxRow / 5 + ((intMaxRow % 5 > 0) ? 1 : 0);
    if (intPrintCount == 0) return false;
    // 0 ComNo 1 DocNo 2 CDate 3 NeedDate 4 ApplyType
    // 5 Analysis 6 DepartNo 7 EDateTime 8 CDate 9 PrintCount
    // 10 CheckAdd 11 CheckAddDescript 12 ExistDate 13 PayConditionCross 14
    // UNDERGO_WRITE
    // 15 Table1Source
    //
    setPrintable("OriEmployeeNo", !"CS".equals(stringComNo));
    setValue("ComNoPrint", getCompanyName(stringComNo, stringBarCode, exeUtil)); // 公司
    setValue("DocNoPrint", retDoc3M011[0][1]); // 申購編號
    setValue("CDatePrint", retDoc3M011[0][2]); // 申請日期
    setValue("NeedDatePrint", retDoc3M011[0][3]); // 需求日期
    setValue("DepartNoPrint", retDoc3M011[0][6].trim()); // 案別
    setValue("DepartNamePrint", getDeptName(stringComNo, retDoc3M011[0][6].trim(), exeUtil)); //
    setVisible("PrintCountPrint", !"B3018".equals(getUser()));
    setValue("PrintCountPrint", "" + exeUtil.doParseInteger(retDoc3M011[0][9].trim())); // 補印次數
    //
    setValue("DepartInform", "");
    if (retDoc3M011[0][1].indexOf("033FZ") != -1) {
      setValue("DepartInform", "敬會人總室");
    }
    // 申講名稱
    stringCostID = retDoc3M012[0][0].trim();
    stringCostID1 = retDoc3M012[0][1].trim();
    stringBudgetName = "";
    if (booleanSource) {
      if (!"".equals(stringCostID) && !"".equals(stringCostID1)) {
        retDoc7M011 = getDoc2M020(stringComNo, stringCostID, stringCostID1);
        if (retDoc7M011.length > 0) {
          stringBudgetName = retDoc7M011[0][3].trim();
        }
      }
    } else {
      if (!"".equals(stringCostID)) {
        stringBudgetName = getCost3Name(stringCostID.substring(0, 5));
      }
    }
    // if("B3018".equals(getUser()))stringBudgetName = "一二三四五六七八九十一二三四五六七八" ;
    // if(!booleanPrintNew) {
    arrayTemp = exeUtil.doCutStringBySize(18, stringBudgetName);
    setValue("BudgetNamePrint", arrayTemp[0].trim());
    arrayTemp = exeUtil.doCutStringBySize(18, arrayTemp[1].trim());
    setValue("BudgetNamePrint2", arrayTemp[0].trim());
    /*
     * } else { arrayTemp = exeUtil.doCutStringBySize(18, stringBudgetName) ;
     * stringTemp = arrayTemp[0].trim()+"\n" ; arrayTemp =
     * exeUtil.doCutStringBySize(18, arrayTemp[1].trim()) ; stringTemp +=
     * arrayTemp[0].trim() ; setValue("BudgetNamePrint", stringTemp) ; }
     */
    // System.out.println("工料預算申請分類---------------------------"+stringFunctionName)
    // ;
    if ("".equals(stringApplyType)) {
      message("[工料預算申請分類] 為空白。");
      return false;
    }
    //
    int intPosAdd = 0;
    if (!booleanPrintNew) {
      if (stringFunctionName.indexOf("國際金融廣場") != -1) {
        intPosAdd = 4;
      } else if (stringFunctionName.indexOf("H52") != -1) {
        intPosAdd = 3;
      } else if (stringFunctionName.indexOf("公司") != -1) {
        intPosAdd = -4;
      } else if (stringFunctionName.indexOf("Server") != -1) {
        intPosAdd = -4;
      }
    }
    //
    if (booleanPrintNew) {
      setPrintable("ApplyTypePrintA", false);
      setPrintable("ApplyTypePrintB", false);
      setPrintable("ApplyTypePrintC", false);
      setPrintable("ApplyTypePrintD", false);
      setPrintable("ApplyTypePrintE", false);
    }
    System.out.println("stringApplyType--------------------------------[" + stringApplyType + "][" + stringApplyTypeL + "]");
    if ("Y".equals(stringUnipurchase)) stringApplyTypeL = "F";
    //
    switch (stringApplyTypeL.charAt(0)) {
    // 預算
    case 'A':
      System.out.println("booleanPrintNew(" + booleanPrintNew + ")--------------------------------");
      if (!booleanPrintNew) {
        getcLabel("ApplyTypePrint").setLocation(9 + intPosAdd, 149);
      } else {
        setPrintable("ApplyTypePrintA", true);
      }
      System.out.println("booleanPrintNew(" + booleanPrintNew + ")--------------------------------E");
      stringApplyTxt = "";
      break;
    // 統購
    case 'F':
      if (!booleanPrintNew) {
        getcLabel("ApplyTypePrint").setLocation(330 + intPosAdd, 149);
      } else {
        setPrintable("ApplyTypePrintE", true);
      }
      stringApplyTxt = "統購";
      break;
    // 固定資產
    case 'D':
      if (!booleanPrintNew) {
        getcLabel("ApplyTypePrint").setLocation(231 + intPosAdd, 149);
        getcLabel("DepartInform").setLocation(455, 715);
      } else {
        setPrintable("ApplyTypePrintD", true);
      }
      stringApplyTxt = "";
      setValue("DepartInform", "敬會人總室");
      break;
    // 追加
    case 'B':
      if (!booleanPrintNew) {
        getcLabel("ApplyTypePrint").setLocation(89 + intPosAdd, 149);
      } else {
        setPrintable("ApplyTypePrintB", true);
      }
      stringApplyTxt = "";
      break;
    // 變更
    case 'C':
      if (!booleanPrintNew) {
        getcLabel("ApplyTypePrint").setLocation(161 + intPosAdd, 149);
      } else {
        setPrintable("ApplyTypePrintC", true);
      }
      stringApplyTxt = "";
      break;
    // 其它
    case 'E':
      if (!booleanPrintNew) {
        getcLabel("ApplyTypePrint").setLocation(328 + intPosAdd, 149);
      }
      setPrintable("ApplyTypePrintE", true);
      // stringApplyTxt="" ;
      break;
    default:
      if (!booleanPrintNew) getcLabel("ApplyTypePrint").setLocation(0, -100);
      stringApplyTxt = "";
      break;
    }
    // if("B3018".equals(getUser()))stringApplyTxt = "統購" ;
    setValue("ApplyTXTPrint", stringApplyTxt);
    System.out.println("檢附---------------------------" + stringApplyTxt);
    stringCheckAdd = retDoc3M011[0][10].trim();
    stringCheckAddDescript = "";
    if ("".equals(stringCheckAdd)) {
      message("[檢附] 為空白。");
      return false;
    }
    if (!booleanPrintNew) {
      setValue("CheckAddPrint", "V");
    } else {
      setPrintable("CheckAddPrintA", false);
      setPrintable("CheckAddPrintB", false);
      setPrintable("CheckAddPrintC", false);
      setPrintable("CheckAddPrintD", false);
      setPrintable("CheckAddPrintE", false);
      setPrintable("CheckAddPrintF", false);
    }
    switch (stringCheckAdd.charAt(0)) {
    // 圖面
    case 'A':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(341 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintA", true);
      }
      break;
    // 施工規範
    case 'B':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(448 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintB", true);
      }
      break;
    // 材料規範
    case 'C':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(554 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintC", true);
      }
      break;
    // 補充說明
    case 'D':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(662 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintD", true);
      }
      break;
    // 進度表
    case 'E':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(768 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintE", true);
      }
      break;
    // 其它
    case 'F':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(851 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintF", true);
      }
      stringCheckAddDescript = retDoc3M011[0][11].trim();
      break;
    // 報價單
    case 'G':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(851 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintF", true);
      }
      stringCheckAddDescript = "報價單";
      break;
    // 合約書
    case 'H':
      if (!booleanPrintNew) {
        getcLabel("CheckAddPrint").setLocation(851 + intPosAdd, 455);
      } else {
        setPrintable("CheckAddPrintF", true);
      }
      stringCheckAddDescript = "合約書";
      break;
    default:
      if (!booleanPrintNew) getcLabel("CheckAddPrint").setLocation(0, -100);
      break;
    }
    // if("B3018".equals(getUser()))stringCheckAddDescript = "12345678901234567890"
    // ;
    setValue("CheckAddDescriptPrint", stringCheckAddDescript);
    // System.out.println("表格---------------------------|"+intPrintCount) ;
    // intMaxRow = 5 ;
    // intPrintCount = 1 ;
    String stringPayDescription = "";
    if ("Y".equals(stringUnipurchase)) {
      System.out.println("stringUnipurchase---------------------------|" + stringUnipurchase);
      // 付款條件
      if (vectorFactoryNo.size() == 1) {
        // 單一廠商，不列印廠商
        stringFactoryNo = ("" + vectorFactoryNo.get(0)).trim();
        vectorTemp = (Vector) hashtableFactoryNoGetPayCondition.get(stringFactoryNo);
        System.out.println("單一廠商(" + stringFactoryNo + ")，不列印廠商---------------------------");
        if (vectorTemp != null) {
          if (vectorTemp.size() == 1) {
            stringPayCondition1 = ("" + vectorTemp.get(0)).trim();
            // 單一付款條件
            System.out.println("單一付款條件---------------------------");
            stringFactoryName = getFactoryNameForDoc3M015(stringFactoryNo);
            stringPurchaseMoney = "" + hashtablePurchaseMoney.get(stringFactoryNo + "-" + stringPayCondition1);
            stringPayDescription = "含稅，驗收無誤，" + stringPurchaseMoney + "元付款，月結" + getPayContionName(stringPayCondition1) + "期票。";
          } else {
            // 多付款條件
            for (int intPay = 0; intPay < vectorTemp.size(); intPay++) {
              stringPayCondition1 = ("" + vectorTemp.get(intPay)).trim();
              stringPurchaseMoney = "" + hashtablePurchaseMoney.get(stringFactoryNo + "-" + stringPayCondition1);
              //
              if (!"".equals(stringPayDescription)) stringPayDescription += "\n";
              //
              stringPayDescription += "含稅，驗收無誤，" + stringPurchaseMoney + "元付款，月結" + getPayContionName(stringPayCondition1) + "期票。";
            }
            System.out.println("多付款條件---------------------------");
          }
          // 超過行數，不允列印
          arrayTemp = convert.StringToken(stringPayDescription, "\n");
          System.out.println("付款行數---------------------------" + arrayTemp.length);
          /*
           * for(int intT=arrayTemp.length ; intT<11 ; intT++) { stringPayDescription +=
           * "\n"+(intT+1)+"XXXXXXXXXXXXXXX" ; }
           */
          if (arrayTemp.length + 1 > 10) {
            if (!booleanPrintNew) {
              setVisible("textDescription", true);
              setVisible("textDescription2", true);
              setVisible("DescriptionAll", false);
            } else {
              // stringPayDescription = "1. □含稅 □未稅 □其他：\n2. 驗收無誤，100%付款，月結 期票" ;
            }
          } else {
            if (!booleanPrintNew) {
              setVisible("DescriptionAll", true);
              setVisible("textDescription", false);
              setVisible("textDescription2", false);
            }
          }
        } else {
          // 預設列印
          if (!booleanPrintNew) {
            setVisible("textDescription", true);
            setVisible("textDescription2", true);
            setVisible("DescriptionAll", false);
          } else {
            stringPayDescription = "1. □含稅　□未稅　□其他：\n2. 驗收無誤，100%付款，月結　　　期票";
          }
        }
      } else {
        // 多廠商
        for (int intFactory = 0; intFactory < vectorFactoryNo.size(); intFactory++) {
          stringFactoryNo = ("" + vectorFactoryNo.get(intFactory)).trim();
          vectorTemp = (Vector) hashtableFactoryNoGetPayCondition.get(stringFactoryNo);
          if (vectorTemp == null) {
            continue;
          }
          if (vectorTemp.size() == 1) {
            // 單一付款條件
            stringPayCondition1 = ("" + vectorTemp.get(0)).trim();
            //
            if (!"".equals(stringPayDescription)) stringPayDescription += "\n";
            //
            stringPayDescription += getFactoryNameForDoc3M015(stringFactoryNo) + "\n" + "含稅，驗收無誤，" + stringPurchaseMoney + "元付款，月結" + getPayContionName(stringPayCondition1)
                + "期票。";
          } else {
            // 多付款條件
            if (!"".equals(stringPayDescription)) stringPayDescription += "\n";
            //
            stringPayDescription += getFactoryNameForDoc3M015(stringFactoryNo) + "\n";
            //
            for (int intPay = 0; intPay < vectorTemp.size(); intPay++) {
              stringPayCondition1 = ("" + vectorTemp.get(intPay)).trim();
              stringPurchaseMoney = "" + hashtablePurchaseMoney.get(stringFactoryNo + "-" + stringPayCondition1);
              //
              if (!"".equals(stringPayDescription) && intPay > 0) stringPayDescription += "\n";
              //
              stringPayDescription += "含稅，驗收無誤，" + stringPurchaseMoney + "元付款，月結" + getPayContionName(stringPayCondition1) + "期票。";
            }
          }
        }
        // 超過行數，不允列印
        arrayTemp = convert.StringToken(stringPayDescription, "\n");
        /*
         * for(int intT=arrayTemp.length ; intT<11 ; intT++) { stringPayDescription +=
         * "\n"+(intT+1)+"XXXXXXXXXXXXXXX" ; }
         */
        if (arrayTemp.length + 1 >= 10) {
          if (!booleanPrintNew) {
            setVisible("textDescription", true);
            setVisible("textDescription2", true);
            setVisible("DescriptionAll", false);
          } else {
            stringPayDescription = "1. □含稅　□未稅　□其他：\n2. 驗收無誤，100%付款，月結　　　期票";
          }
        } else {
          if (!booleanPrintNew) {
            setVisible("DescriptionAll", true);

            setVisible("textDescription", false);
            setVisible("textDescription2", false);
          } else {
            stringPayDescription = "1. □含稅　□未稅　□其他：\n2. 驗收無誤，100%付款，月結　　　期票";
          }
        }
      }
    } else {
      if (!booleanPrintNew) {
        setVisible("textDescription", true);
        setVisible("textDescription2", true);
        setVisible("DescriptionAll", false);
      } else {
        stringPayDescription = "1. □含稅　□未稅　□其他：\n2. 驗收無誤，100%付款，月結　　　期票";
      }
    }
    setValue("DescriptionAll", stringPayDescription);
    System.out.println("stringPayDescription(" + stringPayDescription + ")---------------------------------------------");
    /*
     * 0 CostID 1 CostID1 2 ClassName 3 Descript 4 Unit / 5 BudgetNum 6 ActualNum 7
     * HistoryPrice 8 FactoryNo 9 ActualPrice / 10 PurchaseMoney 11 ApplyMoney 12
     * PurchaseMoney 13 ProjectID1 14 ClassNameDescript
     */
    boolean booleanFlag782 = false;
    boolean booleanCostID703 = false;
    String stringCostID703 = "703,704";
    String stringFILTER = "";
    Vector vectorCostID = new Vector();
    vectorCostID.add("782");
    //
    for (int intNo = 0; intNo < retDoc3M012.length; intNo++) {
      stringCostID = retDoc3M012[intNo][0].trim();
      stringCostID1 = retDoc3M012[intNo][1].trim();
      stringFILTER = retDoc3M012[intNo][16].trim();
      if (stringCostID703.indexOf(stringCostID + stringCostID1) != -1) booleanCostID703 = true;
      if (!"".equals(stringFILTER)) booleanCostID703 = true;
    }
    if (booleanCostID703) {
      setValue("DepartInform", "敬會人總室");
    }
    // 管理費用 且 筆數大於 5 筆
    if (retDoc3M012.length > 5) {
      double doubleTempL = 0;
      for (int intNo = 0; intNo < retDoc3M012.length; intNo++) {
        doubleTempL += exeUtil.doParseDouble(retDoc3M012[intNo][11]);
      }
      for (int intNo = 1; intNo <= 5; intNo++) {
        setValue("NoPrint" + (intNo), ""); // No
        setValue("ClassNamePrint" + (intNo) + "1", ""); // 分類工料名稱
        setValue("ClassNamePrint" + (intNo) + "2", ""); // 分類工料名稱
        setValue("UnitPrint" + intNo, ""); // 單位
        setValue("HistoryPricePrint" + intNo, ""); // 預算單價
        setValue("BudgetNumPrint" + intNo, ""); // 申請數量
      }
      setValue("NoPrint1", "1"); // No
      setValue("ClassNamePrint11", "詳附件"); // 分類工料名稱
      setValue("UnitPrint1", "式"); // 單位
      setValue("HistoryPricePrint1", exeUtil.getFormatNum2("" + doubleTempL)); // 預算單價
      setValue("BudgetNumPrint1", ""); // 申請數量
      if (booleanPrintable && "B3018,,".indexOf(getUser()) == -1) {
        action(6);
        System.out.println("列印----------------------YYYY");
      }
    } else {
      for (int intCount = 0; intCount < intPrintCount; intCount++) {
        if (!booleanPrintNew) {
          setValue("PageNoPrint", "" + (intCount + 1) + "/" + intPrintCount); // 頁數
        } else {
          setValue("PageNoPrint", "共計：" + (intCount + 1) + "/" + intPrintCount + "頁"); // 頁數
        }
        for (int intNo = 0; intNo < 5; intNo++) {
          for (int intNoL = 0; intNoL < 4; intNoL++) {
            // 採發單價、採發金額 清空
            setValue("ActualPrice" + (intNoL + 1) + (intNo + 1), "");
            setValue("PurchaseMoney" + (intNoL + 1) + (intNo + 1), "");
          }
        }
        for (int intNo = 0; intNo < intCountForPage; intNo++) {
          intPos = (intCount * intCountForPage) + intNo + 1;
          // System.out.println(intPos+"/"+intCount+"---------------------------|"+intMaxRow)
          // ;
          if (intPos <= intMaxRow) {
            if (intPos <= retDoc3M012.length) {
              stringCostID = retDoc3M012[intPos - 1][0].trim();
              stringCostID1 = retDoc3M012[intPos - 1][1].trim();
              stringClassName = retDoc3M012[intPos - 1][2].trim();
              stringUnit = retDoc3M012[intPos - 1][4].trim();
              stringBudgetNum = retDoc3M012[intPos - 1][5].trim();
              stringHistoryPrice = retDoc3M012[intPos - 1][7].trim();
              stringApplyMoney = retDoc3M012[intPos - 1][11].trim();
              stringClassNameDescript = retDoc3M012[intPos - 1][14].trim();
              stringFILTER = retDoc3M012[intPos - 1][16].trim();
              //
              stringFactoryNo = retDoc3M012[intPos - 1][8].trim();
              stringActualPrice = retDoc3M012[intPos - 1][9].trim();
              stringPurchaseMoney = retDoc3M012[intPos - 1][10].trim();
              //
              if (!"".equals(stringFILTER)) {
                stringCostID = stringFILTER;
                stringCostID1 = "";
                stringClassName = getFilterName(stringFILTER);
              }
            }
            System.out.println("分類料名稱 ClassNamePrint---------------------------|" + stringClassName);
            if (stringClassName.indexOf(stringClassNameDescript) != -1) {
              stringClassName = stringCostID + stringCostID1 + "-" + stringClassName;
              System.out.println("stringClassName1----------------------------------------------1");
            } else if (stringClassNameDescript.indexOf(stringClassName) != -1) {
              stringClassName = stringCostID + stringCostID1 + "-" + stringClassNameDescript;
            } else {
              stringTemp = stringCostID + stringCostID1 + "-" + stringClassName + "-" + stringClassNameDescript;
              if (code.StrToByte(stringTemp).length() > 50) {
                // 分類工料名稱 只能 8 個中文字
                arrayTemp = exeUtil.doCutStringBySize(16, stringClassName);
                stringClassName = arrayTemp[0].trim();
                stringTemp = stringCostID + stringCostID1 + "-" + stringClassName + "-" + stringClassNameDescript;
                if (code.StrToByte(stringTemp).length() > 50) {
                  // 內容只能 15 個中文字
                  arrayTemp = exeUtil.doCutStringBySize(30, stringClassNameDescript);
                  stringClassNameDescript = arrayTemp[0].trim();
                }
              }
              stringClassName = stringCostID + stringCostID1 + "-" + stringClassName + "-" + stringClassNameDescript;
            }
            System.out.println("stringClassName(" + stringClassName + ")----------------------------");
            arrayTemp = exeUtil.doCutStringBySize(26, stringClassName);
            stringClassNamePrint1 = arrayTemp[0].trim();
            stringClassNamePrint2 = arrayTemp[1].trim();
            if (!"".equals(stringClassNamePrint2)) {
              arrayTemp = exeUtil.doCutStringBySize(26, stringClassNamePrint2);
              stringClassNamePrint2 = arrayTemp[0].trim();
            }
            System.out.println("11111stringClassName(" + stringClassName + ")stringClassNamePrint1(" + stringClassNamePrint1 + ")stringClassNamePrint2(" + stringClassNamePrint2
                + ")----------------------------");
            setValue("NoPrint" + (intNo + 1), "" + intPos); // 項次 NoPrint
            // if(!booleanPrintNew) {
            setValue("ClassNamePrint" + (intNo + 1) + "1", stringClassNamePrint1);
            setValue("ClassNamePrint" + (intNo + 1) + "2", stringClassNamePrint2);
            System.out.println("2222stringClassName(" + stringClassName + ")(" + (intNo + 1) + ")stringClassNamePrint1(" + stringClassNamePrint1 + ")stringClassNamePrint2("
                + stringClassNamePrint2 + ")----------------------------");
            /*
             * } else { stringTemp = stringClassNamePrint1+"\n"+stringClassNamePrint2 ;
             * setValue("ClassNamePrint" +(intNo+1)+"1", stringTemp) ; }
             */
            stringHistoryPrice = convert.FourToFive(stringHistoryPrice, 1);
            setValue("UnitPrint" + (intNo + 1), stringUnit); // 單位 UnitPrint
            setValue("BudgetNumPrint" + (intNo + 1), exeUtil.getFormatNum(stringBudgetNum)); // 申請數量
            setValue("HistoryPricePrint" + (intNo + 1), exeUtil.getFormatNum(stringHistoryPrice)); // 歷史單價
            // 統購
            if ("Y".equals(stringUnipurchase)) {
              for (int intNoL = 0; intNoL < 4; intNoL++) {
                if (intNoL < vectorFactoryNo.size()) {
                  stringFactoryNoL = ("" + vectorFactoryNo.get(intNoL)).trim();
                  if (stringFactoryNo.equals(stringFactoryNoL)) {
                    // 採發單價、採發金額
                    stringActualPriceL = exeUtil.getFormatNum(stringActualPrice);
                    stringPurchaseMoneyL = exeUtil.getFormatNum(stringPurchaseMoney);
                  } else {
                    // 採發單價、採發金額 清空
                    stringActualPriceL = "";
                    stringPurchaseMoneyL = "";
                  }
                } else {
                  // 採發單價、採發金額 清空
                  stringActualPriceL = "";
                  stringPurchaseMoneyL = "";
                }
                stringActualPriceL = convert.FourToFive(stringActualPriceL, 1);
                setValue("ActualPrice" + (intNoL + 1) + (intNo + 1), stringActualPriceL);
                setValue("PurchaseMoney" + (intNoL + 1) + (intNo + 1), stringPurchaseMoneyL);
                // System.out.println("ActualPrice"
                // +(intNoL+1)+(intNo+1)+"---------------------------|"+stringActualPriceL) ;
                // System.out.println("PurchaseMoney"
                // +(intNoL+1)+(intNo+1)+"---------------------------|"+stringPurchaseMoneyL) ;
              }
            }
          } else {
            setValue("NoPrint" + (intNo + 1), "");
            setValue("ClassNamePrint" + (intNo + 1) + "1", "");
            setValue("ClassNamePrint" + (intNo + 1) + "2", "");
            setValue("UnitPrint" + (intNo + 1), "");
            setValue("BudgetNumPrint" + (intNo + 1), "");
            setValue("HistoryPricePrint" + (intNo + 1), "");
          }
        }
        // 第二頁以後刪除
        if (intCount > 0) {
          setValue("DeleteLinePrint", "_________________________________________________________________________________________________________________");
        } else {
          setValue("DeleteLinePrint", "");
        }
        setVisible("PrintCountPrint", false);
        if (booleanPrintable && "B3018,,".indexOf(getUser()) == -1) {
          action(6);
          System.out.println("列印----------------------YYYY");
        }
      }
    }
    // System.out.println("doPrint---------------------------E") ;
    return true;
  }

  public String getPayContionName(String stringPayCondition) {
    //
    if ("000".equals(stringPayCondition)) return "現金";
    if ("0".equals(stringPayCondition)) return "即期";
    //
    return stringPayCondition + "天";
  }

  // 資料庫
  // 資料庫 Doc
  // 表格 Doc3M011
  public void doUpdateStatus(String stringTable, String stringFieldName, String stringFieldValue, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    stringSql = "UPDATE  " + stringTable + " " + "  SET   " + stringFieldName + "  =  '" + stringFieldValue + "' " + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    dbDoc.execFromPool(stringSql);
  }

  public String[][] getDoc3M011(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M011 = null;
    // 0 ComNo 1 DocNo 2 CDate 3 NeedDate 4 ApplyType
    // 5 Analysis 6 DepartNo 7 EDateTime 8 CDate 9 PrintCount
    // 10 CheckAdd 11 CheckAddDescript 12 ExistDate 13 PayConditionCross 14
    // UNDERGO_WRITE
    // 15 Table1Source 16 Unipurchase 17 OriEmployeeNo 18 CoinType
    stringSql = " SELECT  ComNo,              DocNo,                      CDate,                NeedDate,              ApplyType, "
        + " Analysis,           DepartNo,                 EDateTime,        CDate,                    PrintCount, "
        + " CheckAdd,        CheckAddDescript,  ExistDate,            PayConditionCross,  UNDERGO_WRITE, " + " Table1Source,    Unipurchase,            OriEmployeeNo,  CoinType "
        + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' " + " AND  UNDERGO_WRITE  <>  'X' ";
    retDoc3M011 = dbDoc.queryFromPool(stringSql);
    return retDoc3M011;
  }

  public String[][] getDoc3M011EmployeeNo(String FunctionType, String stringSqlAnd) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M011EmployeeNo = null;
    //
    stringSql = " SELECT  EmployeeNo,  FunctionType,  Remark " + " FROM  Doc3M011_EmployeeNo " + " WHERE    FunctionType  =  '" + FunctionType + "'  ";
    if (!"".equals(stringSqlAnd)) {
      stringSql += stringSqlAnd;
    } else {
      stringSql += " ORDER BY  EmployeeNo, FunctionType ";
    }

    retDoc3M011EmployeeNo = dbDoc.queryFromPool(stringSql);
    return retDoc3M011EmployeeNo;
  }

  public Vector getEmployeeNoDoc3M011(String FunctionType, String stringSqlAnd) throws Throwable {
    String stringSql = "";
    String stringEmployeeNo = "";
    String[][] retDoc3M011EmployeeNo = getDoc3M011EmployeeNo(FunctionType, stringSqlAnd);
    Vector vectorEmployeeNo = new Vector();
    //
    for (int intNo = 0; intNo < retDoc3M011EmployeeNo.length; intNo++) {
      stringEmployeeNo = retDoc3M011EmployeeNo[intNo][0].trim().toUpperCase();
      if (vectorEmployeeNo.indexOf(stringEmployeeNo) != -1) continue;
      vectorEmployeeNo.add(stringEmployeeNo);
    }
    return vectorEmployeeNo;
  }

  // 表格 Doc3M012
  public String[][] getDoc3M012(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M012 = null;
    /*
     * 0 CostID 1 CostID1 2 ClassName 3 Descript 4 Unit / 5 BudgetNum 6 ActualNum 7
     * HistoryPrice 8 FactoryNo 9 ActualPrice / 10 PurchaseMoney 11 ApplyMoney 12
     * PurchaseMoney 13 ProjectID1 14 ClassNameDescript 15 RecordNo 16 FILTER
     */
    stringSql = " SELECT  CostID,                   CostID1,         ClassName,          Descript,      Unit, "
        + " BudgetNum,          ActualNum,    HistoryPrice,        FactoryNo,   ActualPrice, " + " PurchaseMoney,  ApplyMoney,  PurchaseMoney,  ProjectID1,  ClassNameDescript, "
        + " RecordNo,            FILTER " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' " + " ORDER BY  RecordNo ";
    retDoc3M012 = dbDoc.queryFromPool(stringSql);
    return retDoc3M012;
  }

  // 表格 Doc3M0130
  public String[][] getDoc3M0130(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M013 = null;
    // 0 RecordNo 1 PayCondition1 2 PayCondition2 3 PurchaseMoney
    stringSql = " SELECT  RecordNo,        PayCondition1,    PayCondition2,  PurchaseMoney " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' "
        + " ORDER BY RecordNo, RecordNo130 ";
    retDoc3M013 = dbDoc.queryFromPool(stringSql);
    return retDoc3M013;
  }

  // 表格 Doc3M013
  public String[][] getDoc3M013(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M013 = null;
    // 0 FactoryNo 1 PurchaseSumMoney 2 PercentRate 3 MonthNum 4 PurchaseMoney
    // 5 PayCondition1 6 PayCondition2 7 Descript 8 NoUseRealMoney
    stringSql = " SELECT  FactoryNo,            PurchaseSumMoney,    PercentRate,  MonthNum,      PurchaseMoney, "
        + " PayCondition1,    PayCondition2,              Descript,          NoUseRealMoney " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' "
        + " ORDER BY FactoryNo, RecordNo ";
    retDoc3M013 = dbDoc.queryFromPool(stringSql);
    return retDoc3M013;
  }

  // 表格 Doc3M015
  public String getFactoryNameForDoc3M015(String stringFactoryNo) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringFactoryName = "";
    String[][] retDoc3M015 = null;
    //
    stringSql = " SELECT  OBJECT_SHORT_NAME " + " FROM  Doc3M015 " + " WHERE  OBJECT_CD  =  '" + stringFactoryNo + "' ";
    retDoc3M015 = dbDoc.queryFromPool(stringSql);
    if (retDoc3M015.length != 0) {
      stringFactoryName = retDoc3M015[0][0].trim();
    }
    return stringFactoryName;
  }

  // 表格 Doc2M020
  public String[][] getDoc2M020(String stringComNo, String stringCostID, String stringCostID1) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc7M011 = null;
    // 0 BudgetID 1 CostID 2 CostID1 3 Description
    stringSql = "SELECT  BudgetID,  CostID,  CostID1,  Description " + " FROM  Doc2M020 " + " WHERE  ComNo  =  '" + stringComNo + "' " + " AND  BudgetID  <>  '' "
        + " AND  NOT(BudgetID  IS  NULL) ";
    if (!"".equals(stringCostID)) stringSql += " AND  CostID  =  '" + stringCostID + "' ";
    if (!"".equals(stringCostID1)) stringSql += " AND  CostID1  =  '" + stringCostID1 + "' ";
    stringSql += " ORDER BY BudgetID,  CostID,  CostID1 ";
    retDoc7M011 = dbDoc.queryFromPool(stringSql);
    return retDoc7M011;
  }

  public String getCost3Name(String stringCostID) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc7M052 = null;
    //
    if ("".equals(stringCostID)) return stringDescription;
    //
    stringSql = "SELECT  DESCRIPTION " + " FROM  Doc7M052 " + " WHERE  RTRIM(CostID1)+RTRIM(CostID2)+RTRIM(CostID3)  =  '" + stringCostID + "' ";
    retDoc7M052 = dbDoc.queryFromPool(stringSql);
    if (retDoc7M052.length > 0) stringDescription = retDoc7M052[0][0].trim();
    return stringDescription;
  }

  public String getCost4Name(String stringCostID) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc7M053 = null;
    //
    if ("".equals(stringCostID)) return stringDescription;
    //
    stringSql = "SELECT  DESCRIPTION " + " FROM  Doc7M053 " + " WHERE  CostID  =  '" + stringCostID + "' ";
    retDoc7M053 = dbDoc.queryFromPool(stringSql);
    if (retDoc7M053.length > 0) stringDescription = retDoc7M053[0][0].trim();
    return stringDescription;
  }

  public String getDeptName(String stringComNo, String stringDeptCd, FargloryUtil exeUtil) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringDeptName = "";
    Hashtable hashtableAnd = new Hashtable();
    //
    hashtableAnd.put("DEPT_CD", stringDeptCd);
    stringDeptName = exeUtil.getNameUnion("DEPT_Name", "Doc2M010_DeptCd", "", hashtableAnd, dbDoc);
    if ("".equals(stringDeptName)) {
      hashtableAnd.put("DEPT_CD", stringDeptCd);
      stringDeptName = exeUtil.getNameUnion("DEPT_NAME", "FE3D01", "", hashtableAnd, dbDoc);
    }
    if ("CS".equals(stringComNo)) stringDeptName = "";
    return stringDeptName;
  }

  // 資料庫 FED1
  // 表格 FED1023
  // 公司名稱
  public String getCompanyName(String stringCompanyCd, String stringBarCode, FargloryUtil exeUtil) throws Throwable {
    talk dbFED1 = getTalk("" + get("put_FED1"));
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringCompanyName = "";
    String stringProjectID1 = "";
    //
    if ("OO".equals(stringCompanyCd)) {
      stringProjectID1 = exeUtil.getNameUnion("DepartNo2", "Doc5M011", "AND  BarCode  =  '" + stringBarCode + "'", new Hashtable(), dbDoc);
      stringCompanyName = exeUtil.getNameUnion("Descript", "Doc2M010_ProjectID1", "AND  ProjectID1  =  '" + stringProjectID1 + "'", new Hashtable(), dbDoc);
      if (stringCompanyName.length() != 0) {
        return stringCompanyName;
      }
    }
    //
    stringCompanyName = exeUtil.getNameUnion("COMPANY_NAME", "FED1023", "AND  COMPANY_CD  =  '" + stringCompanyCd + "'", new Hashtable(), dbFED1);
    if (stringCompanyName.length() != 0) {
      return stringCompanyName;
    }
    //
    stringCompanyName = exeUtil.getNameUnion("ComName", "Doc7M056", "AND  ComNo  =  '" + stringCompanyCd + "'", new Hashtable(), dbDoc);
    if (stringCompanyName.length() != 0) {
      return stringCompanyName;
    }
    return "";
  }

  public String getEmpName(String stringEmpNo, FargloryUtil exeUtil) throws Throwable {
    talk dbFE3D = getTalk("" + get("put_FE3D"));
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringEmpName = "";
    //
    stringEmpName = exeUtil.getNameUnion("EMP_NAME", "FE3D05", "AND  EMP_NO  =  '" + stringEmpNo + "'", new Hashtable(), dbFE3D);
    if (stringEmpName.length() != 0) {
      return stringEmpName;
    }
    stringEmpName = exeUtil.getNameUnion("EMP_NAME", "Doc5M011_EmployeeNo", "AND  EMP_NO  =  '" + stringEmpNo + "'", new Hashtable(), dbDoc);
    if (stringEmpName.length() != 0) {
      return stringEmpName;
    }
    return "";
  }

  // 資料庫 Asset
  // 表格 AS_ASSET_FILTER
  // 固資名稱
  public String getFilterName(String stringFILTER) throws Throwable {
    talk dbAsset = getTalk("" + get("put_Asset"));
    String stringSql = "";
    String stringF3Name = "";
    String[][] retAsAssetFilter = null;
    //
    stringSql = " SELECT  F3_NAME " + " FROM  AS_ASSET_FILTER " + " WHERE  FILTER  =  '" + stringFILTER + "' ";
    retAsAssetFilter = dbAsset.queryFromPool(stringSql);
    if (retAsAssetFilter.length != 0) {
      stringF3Name = retAsAssetFilter[0][0].trim();
    }
    return stringF3Name;
  }

  public String getInformation() {
    return "---------------查詢按鈕程式.preProcess()----------------";
  }
}
