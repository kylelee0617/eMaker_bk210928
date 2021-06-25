package Doc.Doc3;

import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import javax.swing.*;
import Farglory.util.FargloryUtil;
import Farglory.Excel.FargloryExcel;
import com.jacob.activeX.*;
import com.jacob.com.*;

//
public class Doc3R011_Print3 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println(value + "------------------------------S");
    talk dbDoc = getTalk("" + get("put_Doc"));
    talk dbFED1 = getTalk("" + get("put_FED1"));
    talk dbFE3D = getTalk("" + get("put_FE3D"));
    talk dbAsset = getTalk("" + get("put_Asset"));
    FargloryUtil exeUtil = new FargloryUtil();
    String stringThisFunction = getFunctionName();
    String stringToday = datetime.getToday("YYYY/mm/dd");
    //
    doExcel(stringThisFunction, exeUtil, dbDoc, dbFE3D, dbFED1, dbAsset);
    System.out.println(value + "------------------------------E");
    return value;
  }

  public void doExcel(String stringThisFunction, FargloryUtil exeUtil, talk dbDoc, talk dbFE3D, talk dbFED1, talk dbAsset) throws Throwable {
    String stringBarCode = "";
    boolean booleanSource = false;
    Vector vectorDoc3M011 = null;
    Vector vectorDoc3M012 = null;
    String stringPrintType = getPrintType(booleanSource, stringThisFunction, vectorDoc3M011, vectorDoc3M012, exeUtil);
    String stringExcelFileName = "";
    int intRow = 0;
    int intSheetName = 0;
    System.out.println("vectorDoc3M011-----");
    System.out.print(vectorDoc3M011);
    
    //
    if (stringThisFunction.indexOf("請購申請書-簽核彙總") != -1) {
      JTable jtable = getTable("Table1");
      String stringFunction = "";
      //
      intRow = jtable.getSelectedRow();
      stringBarCode = ("" + getValueAt("Table1", intRow, "BarCode"));
      System.out.println("stringBarCode(" + stringBarCode + ")------------------------------");
      
      if ("".equals(stringBarCode)) return;
      stringFunction = ("" + getValueAt("Table1", intRow, "Function"));
      booleanSource = "A".equals(stringFunction);
    } else {
      stringBarCode = getValue("BarCode");
      if ("".equals(stringBarCode)) return;
      booleanSource = stringThisFunction.indexOf("Doc5") == -1;
    }
    vectorDoc3M011 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc3M011" : "Doc5M011", new Hashtable(), " AND  BarCode  =  '" + stringBarCode + "' ", dbDoc);
    if (vectorDoc3M011.size() == 0) return;
    vectorDoc3M012 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc3M012" : "Doc5M012", new Hashtable(), " AND  BarCode  =  '" + stringBarCode + "' ORDER BY  RecordNo ",
        dbDoc);
    if (vectorDoc3M012.size() == 0) return;
    stringExcelFileName = stringBarCode.startsWith("Z") ? "請購單-承辦列印2.xlt" : "請購單-承辦列印.xltx";
    intSheetName = "A".equals(stringPrintType) ? 1 : 2;
    //
    FargloryExcel exeExcel = new FargloryExcel();
    //if (stringThisFunction.indexOf("採購") == -1) exeExcel.setVisibleProperty(false);
    String stringFilePath = "g:/資訊室/Excel/Doc/Doc3/" + stringExcelFileName;
    System.out.println("\n\nstringFilePath(" + stringFilePath + ")----------------------------------------------------\n\n");

    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheets = (Dispatch) retVector.get(3);
    Dispatch objectClick = null;
    
    //
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(intSheetName)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    
    // A 對應 方案1及方案2 B對應方案3
    // 表頭列印
    doPrintDoc3M011(booleanSource, stringPrintType, vectorDoc3M011, vectorDoc3M012, objectSheet1, exeExcel, exeUtil, dbDoc, dbFE3D, dbFED1, dbAsset);
    
    // 請購項目
    doPrintDoc3M012(booleanSource, stringPrintType, stringThisFunction, vectorDoc3M011, vectorDoc3M012, objectSheet1, exeExcel, exeUtil, dbDoc, dbFED1, dbAsset);
    
    //參數PrintOut : 直接列印
    //if (stringThisFunction.indexOf("採購") == -1) Dispatch.call(objectSheet1, "PrintOut");
    
    if (vectorDoc3M012.size() > 5) {
      if (stringThisFunction.indexOf("採購") == -1) {
        put("Doc3M011_PRINT", "N");
      } else {
        put("Doc3M011_PRINT", "Y");
      }
      getButton("ButtonPrintItem").doClick();
    }
    
    //保護EXCEL 密碼為申請書單號
    //Dispatch call參數 : sheets(get3那個) , patch定義參數 , sheet名稱 
    exeExcel.setProtect("20210601006", Dispatch.call(objectSheets, "Item", "001").toDispatch());

    //if (stringThisFunction.indexOf("請購申請書-簽核彙總") == -1) messagebox("已輸出EXCEL");

    // 釋放 Excel 物件
    exeExcel.getReleaseExcelObject(retVector);
  }

  // A 對應 方案1及方案2 B對應方案3
  public String getPrintType(boolean booleanSource, String stringThisFunction, Vector vectorDoc3M011, Vector vectorDoc3M012, FargloryUtil exeUtil) throws Throwable {
    /*
     * if(stringThisFunction.indexOf("承辦") != -1) return "A" ; // 方案1及方案2
     * //if(!isUnipurchase(booleanSource, vectorDoc3M011, exeUtil)) return "A" ; //
     * String stringFactoryNo = "" ; Vector vectorFactoryNo = new Vector() ; for(int
     * intNo=0 ; intNo<vectorDoc3M012.size() ; intNo++) { stringFactoryNo =
     * exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "FactoryNo") ; //
     * if("".equals(stringFactoryNo)) stringFactoryNo = "NONE" ;
     * if(vectorFactoryNo.indexOf(stringFactoryNo) == -1)
     * vectorFactoryNo.add(stringFactoryNo) ; } return (vectorFactoryNo.size() <= 3)
     * ? "A" : "B" ;
     */
    return "A";
  }

  public boolean isUnipurchase(boolean booleanSource, Vector vectorDoc3M011, FargloryUtil exeUtil) throws Throwable {
    if (booleanSource) {
      // 行銷
      String stringApplyType = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "ApplyType");
      if ("F".equals(stringApplyType)) return true;
    } else {
      // 管理
      String stringUnipurchase = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "Unipurchase");
      if ("Y".equals(stringUnipurchase)) return true;
    }
    return false;
  }

  // 表頭
  public void doPrintDoc3M011(boolean booleanSource, String stringPrintType, Vector vectorDoc3M011, Vector vectorDoc3M012, Dispatch objectSheet1, FargloryExcel exeExcel,
      FargloryUtil exeUtil, talk dbDoc, talk dbFE3D, talk dbFED1, talk dbAsset) throws Throwable {
    // 公司
    String stringBarCode = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "BarCode");
    System.out.println("doPrintDoc3M011 stringBarCodear>>>" + stringBarCode);
    String stringComNo = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "ComNo");
    String stringComName = getCompanyName(stringComNo, stringBarCode, exeUtil, dbDoc, dbFED1);
    exeExcel.putDataIntoExcel(16, 0, "*" + stringBarCode + "*", objectSheet1);
    exeExcel.putDataIntoExcel(0, 0, stringComName, objectSheet1);
    //
    String stringPurchaseState = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "PurchaseState");
    if (stringBarCode.equals("S01412")) {
      stringPurchaseState = "緊急請購";
    }
    if (!"正常請購".equals(stringPurchaseState)) {
      exeExcel.putDataIntoExcel(1, 1, stringPurchaseState, objectSheet1);
    }
    // 案別
    String stringDepartNo = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DepartNo");
    String stringDepartName = getDeptName(stringComNo, stringDepartNo, exeUtil, dbDoc);
    System.out.println("單位(案別)==>" + exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DepartNo"));
    exeExcel.putDataIntoExcel(0, 2, "單位（案別）：" + stringDepartNo, objectSheet1);

    // 申購名稱
    String stringBudgetName = getPrintBudgetName(booleanSource, stringPrintType, vectorDoc3M011, vectorDoc3M012, exeUtil, dbDoc, dbAsset);
    exeExcel.putDataIntoExcel(4, 2, "申購名稱：" + stringBudgetName, objectSheet1);
    System.out.println("單位(案別)==>" + exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DepartNo"));
    
    // 申購編號
    String stringDocNo1 = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DocNo1");
    String stringDocNo2 = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DocNo2");
    String stringDocNo3 = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DocNo3");
    String stringDocNo = stringDocNo1 + "-" + stringDocNo2 + "-" + stringDocNo3;
    exeExcel.putDataIntoExcel(10, 2, "申請編號：" + stringDocNo, objectSheet1);
    
    // 申購日期
    String stringCDate = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "CDate");
    exeExcel.putDataIntoExcel(0, 3, "申購日期：" + exeUtil.getDateConvert(stringCDate), objectSheet1);
    // 需求日期
    String stringNeedDate = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "NeedDate");
    exeExcel.putDataIntoExcel(10, 3, "需求日期：" + exeUtil.getDateConvert(stringNeedDate) + "  共計：1頁", objectSheet1);
    // 申請分類
    String stringApplyType = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "ApplyType");
    String stringApplyTxt = "";
    String[] arrayApplyType = { "□", "□", "□", "□", "□" };
    //
    if (!booleanSource) {
      if ("E".equals(stringApplyType)) {
        stringApplyType = "C";
      } else if ("C".equals(stringApplyType)) {
        stringApplyType = "E";
      }
    }
    if ("A".equals(stringApplyType)) {
      arrayApplyType[0] = "■"; // 預算
    } else if ("F".equals(stringApplyType)) {
      arrayApplyType[4] = "■"; // 統購
      stringApplyTxt = "統購";
    } else if ("D".equals(stringApplyType)) {
      arrayApplyType[3] = "■"; // 固定資產
    } else if ("B".equals(stringApplyType)) {
      arrayApplyType[1] = "■"; // 追加
    } else if ("C".equals(stringApplyType)) {
      arrayApplyType[2] = "■"; // 變更
    } else if ("E".equals(stringApplyType)) {
      arrayApplyType[4] = "■"; // 其它
    }
    exeExcel.putDataIntoExcel(0, 6,
        arrayApplyType[0] + "1.預算內" + arrayApplyType[1] + "2.追加" + arrayApplyType[2] + "3.變更" + arrayApplyType[3] + "4.固定資產" + arrayApplyType[4] + "5.其它：" + stringApplyTxt,
        objectSheet1);

    // 檢附
    String stringCheckAdd = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "CheckAdd");
    String stringCheckAddDescript = "";
    String stringCheckValue = "";
    String[] arrayCheckAdd = { "□", "□", "□", "□", "□", "□", "□", "□", "□", "□", "□", "□" };
    /*
     * 新版 : 12匹 簽呈 I 預算編列表 J 報價單 G 指定廠商聲明書 K 圖面 A 施工規範 B 材料規範 C 補充說明 D 進度表 E 合約書範本 H
     * 公司查詢 L 其它： F
     */
    if ("A".equals(stringCheckAdd)) {
      arrayCheckAdd[4] = "■"; // 圖面
    } else if ("B".equals(stringCheckAdd)) {
      arrayCheckAdd[5] = "■"; // 施工規範
    } else if ("C".equals(stringCheckAdd)) {
      arrayCheckAdd[6] = "■"; // 材料規範
    } else if ("D".equals(stringCheckAdd)) {
      arrayCheckAdd[7] = "■"; // 補充說明
    } else if ("E".equals(stringCheckAdd)) {
      arrayCheckAdd[8] = "■"; // 進度表
    } else if ("G".equals(stringCheckAdd)) {
      arrayCheckAdd[2] = "■"; // 報價單
    } else if ("H".equals(stringCheckAdd)) {
      arrayCheckAdd[9] = "■"; // 合約書
    } else if ("F".equals(stringCheckAdd)) {
      arrayCheckAdd[11] = "■"; // 其它
      stringCheckAddDescript = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "CheckAddDescript");
    } else if ("I".equals(stringCheckAdd)) {
      arrayCheckAdd[0] = "■"; // 簽呈
    } else if ("J".equals(stringCheckAdd)) {
      arrayCheckAdd[1] = "■"; // 預算編列
    } else if ("K".equals(stringCheckAdd)) {
      arrayCheckAdd[3] = "■"; // 指定廠商
    } else if ("L".equals(stringCheckAdd)) {
      arrayCheckAdd[10] = "■"; // 公司查詢
    }
    stringCheckValue = "檢附：" + arrayCheckAdd[0] + "簽呈" + arrayCheckAdd[1] + "預算編列表" + arrayCheckAdd[2] + "報價單" + arrayCheckAdd[3] + "指定廠商聲明書 \n" + arrayCheckAdd[4] + "圖面"
        + arrayCheckAdd[5] + "施工規範" + arrayCheckAdd[6] + "材料規範" + arrayCheckAdd[7] + "補充說明" + arrayCheckAdd[8] + "進度表 \n" + arrayCheckAdd[9] + "合約書範本" + arrayCheckAdd[10] + "公司查詢"
        + arrayCheckAdd[11] + "其它：" + stringCheckAddDescript;
    exeExcel.putDataIntoExcel(8, "B".equals(stringPrintType) ? 18 : 17, stringCheckValue, objectSheet1);
    // 承辦人員
    // String stringOriEmployeeNo = exeUtil.getVectorFieldValue(vectorDoc3M011, 0,
    // "OriEmployeeNo") ;
    // String stringEmpName = exeUtil.getNameUnion("EMP_NAME", "FE3D05", " AND
    // EMP_NO = '" + stringOriEmployeeNo + "'", new Hashtable(), dbFE3D) ;
    // exeExcel.putDataIntoExcel(14, 27, stringEmpName, objectSheet1) ;
    //
    // String stringToday = datetime.getToday("yy/mm/dd") ;
    // int intPos = stringToday.indexOf("/") ;
    // stringToday = stringToday.substring(intPos+1) ;
    // exeExcel.putDataIntoExcel(14, 28, stringToday, objectSheet1) ;
    //
    if ("D".equals(stringApplyType)) {
      exeExcel.putDataIntoExcel(7, 25, "敬會人總室", objectSheet1);
    }
  }

  public String getPrintBudgetName(boolean booleanSource, String stringPrintType, Vector vectorDoc3M011, Vector vectorDoc3M012, FargloryUtil exeUtil, talk dbDoc, talk dbAsset)
      throws Throwable {
    String[] arrayTemp = null;
    String stringBarCode = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "BarCode");
    String stringFilter = exeUtil.getVectorFieldValue(vectorDoc3M012, 0, "FILTER");
    String stringComNo = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "ComNo");
    String stringCostID = exeUtil.getVectorFieldValue(vectorDoc3M012, 0, "CostID");
    String stringCostID1 = exeUtil.getVectorFieldValue(vectorDoc3M012, 0, "CostID1");
    String stringCostIDDetail = exeUtil.getVectorFieldValue(vectorDoc3M012, 0, "CostIDDetail");
    String stringBudgetName = "";
    Hashtable hashtableAnd = new Hashtable();
    if (!"".equals(stringFilter)) {
      stringBudgetName = exeUtil.getNameUnion("F2_NAME", "AS_ASSET_FILTER", " AND  FILTER  =  '" + stringFilter + "' ", new Hashtable(), dbDoc);
    } else {
      if (booleanSource) {
        if (!"".equals(stringCostIDDetail)) {
          stringBudgetName = exeUtil.getNameUnion("CostID2TXT", "Doc2M022", " AND  CostIDDetail  =  '" + stringCostIDDetail + "' ", new Hashtable(), dbDoc);
        } else if (!"".equals(stringCostID) && !"".equals(stringCostID1)) {
          String[][] retDoc7M011 = getDoc2M020(stringComNo, stringCostID, stringCostID1, dbDoc);
          if (retDoc7M011.length > 0) {
            stringBudgetName = retDoc7M011[0][3].trim();
          }
        }
      } else {
        // 管理
        hashtableAnd.put("CostID", stringCostID);
        hashtableAnd.put("CostID1", stringCostID1);
        stringBudgetName = exeUtil.getNameUnion("DESCRIPTION", "Doc7M052",
            " AND  RTRIM(CostID1)+RTRIM(CostID2)+RTRIM(CostID3)  =  '" + exeUtil.doSubstring(stringCostID, 0, 5) + "' ", new Hashtable(), dbDoc);
        if ("其他".equals(stringBudgetName)) {
          stringBudgetName = exeUtil.getNameUnion("DESCRIPTION", "Doc7M053", " AND  CostID  =  '" + stringCostID + "' ", new Hashtable(), dbDoc);
        }
        if ("其他".equals(stringBudgetName)) {
          stringBudgetName = exeUtil.getNameUnion("ClassNameDescript", "Doc5M012", " AND  BarCode  =  '" + stringBarCode + "' ORDER BY  RecordNo ", new Hashtable(), dbDoc);
        }
        if (!"".equals(stringCostID)) {
          stringBudgetName = getCost3Name(stringCostID.substring(0, 5), dbDoc);
        }
      }
    }
    return stringBudgetName;
  }

  // 請購項目
  public void doPrintDoc3M012(boolean booleanSource, String stringPrintType, String stringThisFunction, Vector vectorDoc3M011, Vector vectorDoc3M012, Dispatch objectSheet1,
      FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbDoc, talk dbFED1, talk dbAsset) throws Throwable {
    System.out.println("doPrintDoc3M012----");
    String stringCostID = "";
    String stringCostID1 = "";
    String stringClassName = "";
    String stringUnit = "";
    String stringHistoryPrice = "";
    String stringBudgetNum = "";
    String stringActualPrice = "";
    String stringClassNameDescript = "";
    String stringPurchaseMoney = "";
    String stringFILTER = "";
    String stringFactoryNo = "";
    String stringFactoryName = "";
    String stringContactMan = "";
    String stringApplyMoney = "";
    String stringTemp = "";
    String stringCostIDDetail = "";
    Vector vectorFactoryNo = new Vector();
    boolean booleanUnipurchase = isUnipurchase(booleanSource, vectorDoc3M011, exeUtil);
    boolean booleanPrint = false;
    boolean booleanFilter = false;
    double doubleApplyMoney = 0;
    double doublePurchaseMoney = 0;
    //
    for (int intNo = 0; intNo < vectorDoc3M012.size(); intNo++) {
      stringCostID = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "CostID");
      stringCostID1 = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "CostID1");
      stringClassName = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "ClassName");
      stringUnit = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "Unit");
      stringBudgetNum = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "BudgetNum");
      stringHistoryPrice = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "HistoryPrice"); // stringHistoryPrice = convert.FourToFive(stringHistoryPrice, 1) ;
      stringApplyMoney = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "ApplyMoney");
      stringClassNameDescript = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "ClassNameDescript");
      stringFILTER = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "FILTER");
      stringFactoryNo = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "FactoryNo");
      stringActualPrice = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "ActualPrice");
      stringPurchaseMoney = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "PurchaseMoney");
      stringCostIDDetail = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "CostIDDetail");
      if ("null".equals(stringCostIDDetail)) stringCostIDDetail = "";
      //
      doubleApplyMoney += exeUtil.doParseDouble(stringApplyMoney);
      doublePurchaseMoney += exeUtil.doParseDouble(stringPurchaseMoney);
      //
      if (!"".equals(stringFILTER)) {
        booleanFilter = true;
      }
      if (",703,702,".indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
        booleanFilter = true;
      }
      // intPos = vectorFactoryNo.indexOf(stringFactoryNo) ;
      //
      if (!"".equals(stringFILTER)) {
        stringCostID = stringFILTER;
        stringCostID1 = "";
        stringClassName = getFilterName(stringFILTER);
      } else if (!"".equals(stringCostIDDetail)) {
        /*
         * stringClassName = exeUtil.doSubstring(stringCostIDDetail, 0,
         * 4)+"-"+exeUtil.getNameUnion("DESCRIPTION", "Doc2M022",
         * " AND  CostIDDetail  =  '"+stringCostIDDetail+"' ", new Hashtable(), dbDoc) ;
         */
        // 20180223 修正工料名稱內容列印資料邏輯
        String costIDTemp = exeUtil.doSubstring(stringCostIDDetail, 0, 4) + "-"
            + exeUtil.getNameUnion("DESCRIPTION", "Doc2M022", " AND  CostIDDetail  =  '" + stringCostIDDetail + "' ", new Hashtable(), dbDoc);
        stringTemp = costIDTemp + "-" + stringClassName;
        if (code.StrToByte(stringTemp).length() > 50) {
          // 分類工料名稱 只能 15 個中文字
          String[] arrayTemp = exeUtil.doCutStringBySize(30, stringClassName);
          stringClassName = arrayTemp[0].trim();
          stringTemp = costIDTemp + "-" + stringClassName;
        }
        stringClassName = stringTemp;
      } else {
        if (stringClassName.indexOf(stringClassNameDescript) != -1) {
          stringClassName = stringCostID + stringCostID1 + "-" + stringClassName;
        } else if (stringClassNameDescript.indexOf(stringClassName) != -1) {
          stringClassName = stringCostID + stringCostID1 + "-" + stringClassNameDescript;
        } else {
          stringClassName = stringCostID + stringCostID1 + "-" + stringClassName + "-" + stringClassNameDescript;
        }
      }
      if ("B".equals(stringPrintType)) continue; // 合併列印
      if ("A".equals(stringPrintType) && vectorDoc3M012.size() > 5) continue; // 合併列印
      //
      booleanPrint = true;
      stringHistoryPrice = exeUtil.getFormatNum2(stringHistoryPrice);
      //
      exeExcel.putDataIntoExcel(0, intNo + 10, "" + (intNo + 1), objectSheet1); // No
      exeExcel.putDataIntoExcel(1, intNo + 10, stringClassName, objectSheet1); // 分類工料
      exeExcel.putDataIntoExcel(2, intNo + 10, stringHistoryPrice, objectSheet1); // 預算單價
      exeExcel.putDataIntoExcel(4, intNo + 10, stringUnit, objectSheet1); // 單位
      exeExcel.putDataIntoExcel(5, intNo + 10, stringBudgetNum, objectSheet1); // 申請數量
      // 歷史單價
      // 廠商
      // 採發單價
      // 採發金額
    }
    if (!booleanPrint) {
      String stringBudgetName = getPrintBudgetName(booleanSource, stringPrintType, vectorDoc3M011, vectorDoc3M012, exeUtil, dbDoc, dbAsset) + "\n詳附件";
      stringTemp = exeUtil.getFormatNum2(convert.FourToFive("" + doubleApplyMoney, 0));
      exeExcel.putDataIntoExcel(0, 10, "1", objectSheet1); // No
      exeExcel.putDataIntoExcel(1, 10, stringBudgetName, objectSheet1); // 分類工料
      exeExcel.putDataIntoExcel(2, 10, stringTemp, objectSheet1); // 預算單價
      exeExcel.putDataIntoExcel(4, 10, "式", objectSheet1); // 單位
      exeExcel.putDataIntoExcel(5, 10, "1", objectSheet1); // 申請數量
    }
    // 業主來價
    stringTemp = "業主來價：" + exeUtil.getFormatNum2(convert.FourToFive("" + doubleApplyMoney, 0));
    exeExcel.putDataIntoExcel(0, 17, stringTemp, objectSheet1);
    // 本案金額總計
    // 廠商
    //
    if (booleanFilter) {
      exeExcel.putDataIntoExcel(7, 25, "敬會人總室", objectSheet1);
    }
  }

  public String getFactoryNoTel(Vector vectorDoc3M015, FargloryUtil exeUtil) throws Throwable {
    String stringContactTel = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "CONTACT_TEL_NO2_PURCHASE");
    String stringTemp = "";
    // 聯絡電話
    if (!"".equals(stringContactTel)) {
      stringTemp = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "CONTACT_TEL_NO1_PURCHASE");
      if (!"".equals(stringTemp)) stringContactTel = stringTemp + "-" + stringContactTel;
      stringTemp = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "CONTACT_TEL_NO3_PURCHASE");
      if (!"".equals(stringTemp)) stringContactTel = stringContactTel + "#" + stringTemp;
      return stringContactTel;
    }
    // 行動電話
    stringContactTel = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "CONTACT_NO_PURCHASE");
    if (!"".equals(stringContactTel)) {
      return stringContactTel;
    }
    // 公司電話
    stringContactTel = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "COMPANY_TEL_NO2_PURCHASE");
    if (!"".equals(stringContactTel)) {
      stringTemp = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "COMPANY_TEL_NO1_PURCHASE");
      if (!"".equals(stringTemp)) stringContactTel = stringTemp + "-" + stringContactTel;
      stringTemp = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "COMPANY_TEL_NO3_PURCHASE");
      if (!"".equals(stringTemp)) stringContactTel = stringContactTel + "#" + stringTemp;
      return stringContactTel;
    }
    return stringContactTel;
  }

  public void doClearContent(int intNowPage, int intMaxPage, Dispatch objectSheet1, FargloryExcel exeExcel) throws Throwable {
    exeExcel.putDataIntoExcel(15, 3, "共計：" + intNowPage + "/" + intMaxPage + "頁", objectSheet1);
    // 清空
    // 表格資料 及 廠商
    exeExcel.doClearContents(1, 6, 16, 23, objectSheet1);
    // 內容
    // 小計
    // 建議廠商
    exeExcel.doClearContents("J25:Q26", objectSheet1);
    exeExcel.doClearContents(9, 24, 16, 24, objectSheet1);
    exeExcel.doClearContents(9, 25, 16, 25, objectSheet1);
  }

  // 資料庫 Doc
  // 表格 Doc2M010_DeptCd
  public String getDeptName(String stringComNo, String stringDeptCd, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
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

  // 表格 Doc2M020
  public String[][] getDoc2M020(String stringComNo, String stringCostID, String stringCostID1, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc7M011 = null;
    // 0 BudgetID 1 CostID 2 CostID1 3 Description
    stringSql = "SELECT  BudgetID,  CostID,  CostID1,  Description " + " FROM  Doc2M020 " + " WHERE  ComNo  =  '" + stringComNo + "' " + " AND  NOT(BudgetID  IS  NULL) ";
    if (!"".equals(stringCostID)) stringSql += " AND  CostID  =  '" + stringCostID + "' ";
    if (!"".equals(stringCostID1)) stringSql += " AND  CostID1  =  '" + stringCostID1 + "' ";
    stringSql += " ORDER BY BudgetID,  CostID,  CostID1 ";
    retDoc7M011 = dbDoc.queryFromPool(stringSql);
    return retDoc7M011;
  }

  // 表格 Doc7M052
  public String getCost3Name(String stringCostID, talk dbDoc) throws Throwable {
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

  // 資料庫 FED1
  // 表格 FED1023
  // 公司名稱
  public String getCompanyName(String stringCompanyCd, String stringBarCode, FargloryUtil exeUtil, talk dbDoc, talk dbFED1) throws Throwable {
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

  // 資料庫 Asset
  // 表格 AS_ASSET_FILTER
  // 固資名稱
  public String getFilterName(String stringFILTER) throws Throwable {
    /*
     * talk dbAsset = getTalk("" +get("put_Asset")); String stringSql = "" ; String
     * stringF3Name = "" ; String[][] retAsAssetFilter = null ; // stringSql =
     * " SELECT  F3_NAME " + " FROM  AS_ASSET_FILTER " + " WHERE  FILTER  =  '" +
     * stringFILTER + "' " ; retAsAssetFilter = dbAsset.queryFromPool(stringSql) ;
     * if(retAsAssetFilter.length != 0) { stringF3Name =
     * retAsAssetFilter[0][0].trim( ) ; } return stringF3Name ;
     */
    return "";
  }

  //
  public String getClientFile(String stringServerPath, FargloryUtil exeUtil) throws Throwable {
    String stringClientPath = "";
    String[] arrayTemp = convert.StringToken(stringServerPath, "/");
    //
    if (exeUtil.doSaveFile(stringServerPath, "Y")) {
      stringClientPath = "C:\\Emaker_Util\\" + arrayTemp[arrayTemp.length - 1].trim();
      System.out.println("stringClientPath(" + stringClientPath + ")--------------------------------");
      return stringClientPath;
    }
    return stringClientPath;
  }

  public String getInformation() {
    return "---------------button5(\u532f\u51fa).defaultValue()----------------";
  }
}
