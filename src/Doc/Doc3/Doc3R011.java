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

// Doc3R011_OLD_2017_06_07
public class Doc3R011 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println(value + "------------------------------S");
    talk dbDoc = getTalk("" + get("put_Doc"));
    talk dbFED1 = getTalk("" + get("put_FED1"));
    FargloryUtil exeUtil = new FargloryUtil();
    String stringThisFunction = getFunctionName();
    //
    // if(",B3018,".indexOf(","+getUser()+",") == -1) return value ;
    //
    doExcel(stringThisFunction, exeUtil, dbDoc, dbFED1);
    System.out.println(value + "------------------------------E");
    return value;
  }

  public void doExcel(String stringThisFunction, FargloryUtil exeUtil, talk dbDoc, talk dbFED1) throws Throwable {
    String stringBarCode = "";
    Vector vectorDoc3M011 = null;
    Vector vectorDoc3M012 = null;
    Vector vectorDoc3M012Data = null;
    Vector vectorFactoryNo = new Vector();
    Hashtable hashtableNoGetFactoryNo = new Hashtable();
    Hashtable hashtableNoGetDoc3M012 = new Hashtable();
    String stringPrint = "" + get("Doc3M011_PRINT");
    put("Doc3M011_PRINT", "null");
    boolean booleanPrint = "Y".equals(stringPrint);
    boolean booleanPowerUser = (",B3018,SYS,".indexOf(getUser()) != -1);
    boolean booleanSource = false;
    int intRow = 0;
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
      booleanSource = stringThisFunction.indexOf("Doc5") == -1;
    }
    if ("".equals(stringBarCode)) return;
    //
    vectorDoc3M011 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc3M011" : "Doc5M011", new Hashtable(), " AND  BarCode  =  '" + stringBarCode + "' ", dbDoc);
    if (vectorDoc3M011.size() == 0) return;
    vectorDoc3M012 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc3M012" : "Doc5M012", new Hashtable(), " AND  BarCode  =  '" + stringBarCode + "' ORDER BY  RecordNo ",
        dbDoc);
    if (vectorDoc3M012.size() == 0) return;
    //
    doFactoryNo(vectorDoc3M012, exeUtil, hashtableNoGetFactoryNo, hashtableNoGetDoc3M012);
    //
    FargloryExcel exeExcel = new FargloryExcel(7, 18, 27, 1);
    if (booleanPrint) exeExcel.setVisibleProperty(false); // 控制顯不顯示 Excel
    String stringFilePath = "g:/資訊室/Excel/Doc/Doc3/請購單附件.xlt";
    if (!(new File(stringFilePath)).exists()) stringFilePath = "https://emaker.farglory.com.tw:8080/servlet/baServer3?step=6?filename=" + stringFilePath;
    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);
    Dispatch objectClick = null;
    int intMaxPage = getMaxPage(hashtableNoGetDoc3M012, exeUtil);
    // 表頭列印
    doPrintDoc3M011(booleanSource, vectorDoc3M011, vectorDoc3M012, objectSheet2, exeExcel, exeUtil, dbDoc, dbFED1);
    //
    // intRageCount 計算
    for (int intNo = 1; intNo <= 30; intNo++) {
      vectorFactoryNo = (Vector) hashtableNoGetFactoryNo.get("" + intNo);
      if (vectorFactoryNo == null) break;
      vectorDoc3M012Data = (Vector) hashtableNoGetDoc3M012.get("" + intNo);
      if (vectorDoc3M012Data == null) break;

      // 請購項目
      doPrintDoc3M012(intNo, intMaxPage, booleanSource, vectorFactoryNo, vectorDoc3M011, vectorDoc3M012Data, objectSheet1, objectSheet2, exeExcel, exeUtil, dbDoc, dbFED1);

    }
    if (booleanPrint) {
      Dispatch.call(objectSheet1, "Activate");
      Dispatch.call(objectSheet1, "PrintOut");
    }
    // 釋放 Excel 物件
    exeExcel.getReleaseExcelObject(retVector);
  }

  public void doFactoryNo(Vector vectorDoc3M012, FargloryUtil exeUtil, Hashtable hashtableNoGetFactoryNo, Hashtable hashtableNoGetDoc3M012) throws Throwable {
    Vector vectorTemp = new Vector();
    Vector vectorTemp2 = new Vector();
    Vector vectorFactoryNo = new Vector();
    String stringFactoryNo = "";
    Hashtable hashtableDoc3M012 = new Hashtable();
    for (int intNo = 0; intNo < vectorDoc3M012.size(); intNo++) {
      stringFactoryNo = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "FactoryNo");
      //
      if ("".equals(stringFactoryNo)) stringFactoryNo = "NONE";
      if (vectorFactoryNo.indexOf(stringFactoryNo) == -1) vectorFactoryNo.add(stringFactoryNo);
      //
      vectorTemp = (Vector) hashtableDoc3M012.get(stringFactoryNo);
      if (vectorTemp == null) {
        vectorTemp = new Vector();
        hashtableDoc3M012.put(stringFactoryNo, vectorTemp);
      }
      vectorTemp.add(vectorDoc3M012.get(intNo));
    }
    int intTypeNo = 0;
    for (int intNo = 0; intNo < vectorFactoryNo.size(); intNo++) {
      stringFactoryNo = "" + vectorFactoryNo.get(intNo);
      intTypeNo = (intNo + 1) / 3;
      if ((intNo + 1) % 3 > 0) intTypeNo++;
      // 廠商
      vectorTemp = (Vector) hashtableNoGetFactoryNo.get("" + intTypeNo);
      if (vectorTemp == null) {
        vectorTemp = new Vector();
        hashtableNoGetFactoryNo.put("" + intTypeNo, vectorTemp);
      }
      vectorTemp.add(stringFactoryNo);

      // 項目
      vectorTemp = (Vector) hashtableNoGetDoc3M012.get("" + intTypeNo);
      if (vectorTemp == null) {
        vectorTemp = new Vector();
        hashtableNoGetDoc3M012.put("" + intTypeNo, vectorTemp);
      }
      vectorTemp2 = (Vector) hashtableDoc3M012.get(stringFactoryNo);
      for (int intNoL = 0; intNoL < vectorTemp2.size(); intNoL++) {
        vectorTemp.add(vectorTemp2.get(intNoL));
      }
    }
  }

  public int getMaxPage(Hashtable hashtableNoGetDoc3M012, FargloryUtil exeUtil) throws Throwable {
    int intPageMax = 0;
    Vector vectorTemp = null;
    //
    for (int intNo = 1; intNo <= 30; intNo++) {
      vectorTemp = (Vector) hashtableNoGetDoc3M012.get("" + intNo);
      if (vectorTemp == null) break;
      //
      intPageMax += vectorTemp.size() / 18;
      if (vectorTemp.size() % 18 > 0) intPageMax++;
    }
    return intPageMax;
  }

  // 表頭
  public void doPrintDoc3M011(boolean booleanSource, Vector vectorDoc3M011, Vector vectorDoc3M012, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbDoc,
      talk dbFED1) throws Throwable {
    // 公司
    String stringBarCode = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "BarCode");
    String stringComNo = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "ComNo");
    String stringComName = getCompanyName(stringComNo, stringBarCode, exeUtil, dbDoc, dbFED1);
    exeExcel.putDataIntoExcel(1, 0, stringComName, objectSheet1);
    // 案別
    String stringDepartNo = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DepartNo");
    String stringDepartName = getDeptName(stringComNo, stringDepartNo, exeUtil, dbDoc);
    exeExcel.putDataIntoExcel(4, 2, stringDepartName, objectSheet1);
    // exeExcel.putDataIntoExcel(4, 2, stringDepartNo+" "+stringDepartName,
    // objectSheet1) ;
    // 申購名稱
    doPrintBudgetName(booleanSource, vectorDoc3M011, vectorDoc3M012, objectSheet1, exeExcel, exeUtil, dbDoc);
    // 申請日期
    String stringCDate = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "CDate");
    exeExcel.putDataIntoExcel(3, 3, exeUtil.getDateConvert(stringCDate), objectSheet1);
    // 申購編號
    String stringDocNo1 = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DocNo1");
    String stringDocNo2 = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DocNo2");
    String stringDocNo3 = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "DocNo3");
    String stringDocNo = stringDocNo1 + "-" + stringDocNo2 + "-" + stringDocNo3;
    exeExcel.putDataIntoExcel(13, 2, stringDocNo, objectSheet1);
  }

  public void doPrintBudgetName(boolean booleanSource, Vector vectorDoc3M011, Vector vectorDoc3M012, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil,
      talk dbDoc) throws Throwable {
    String[] arrayTemp = null;
    String stringComNo = exeUtil.getVectorFieldValue(vectorDoc3M011, 0, "ComNo");
    String stringCostID = exeUtil.getVectorFieldValue(vectorDoc3M012, 0, "CostID");
    String stringCostID1 = exeUtil.getVectorFieldValue(vectorDoc3M012, 0, "CostID1");
    String stringBudgetName = "";
    if (booleanSource) {
      if (!"".equals(stringCostID) && !"".equals(stringCostID1)) {
        String[][] retDoc7M011 = getDoc2M020(stringComNo, stringCostID, stringCostID1, dbDoc);
        if (retDoc7M011.length > 0) {
          stringBudgetName = retDoc7M011[0][3].trim();
        }
      }
    } else {
      if (!"".equals(stringCostID)) {
        stringBudgetName = getCost3Name(stringCostID.substring(0, 5), dbDoc);
      }
    }
    exeExcel.putDataIntoExcel(8, 2, stringBudgetName, objectSheet1);
  }

  // 請購項目
  public void doPrintDoc3M012(int intFactoryTypeNo, int intMaxPage, boolean booleanSource, Vector vectorFactoryNo, Vector vectorDoc3M011, Vector vectorDoc3M012,
      Dispatch objectSheet1, Dispatch objectSheet2, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbDoc, talk dbFED1) throws Throwable {
    int intPos = 0;
    int[] arrayPosPrice = { 9, 12, 15 }; // 單價
    int[] arrayPosMoney = { 10, 13, 16 }; // 金額
    int[] arrayPosFactoryNo = { 9, 12, 15 }; // 廠商
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
    String stringCostIDDetail = "";
    Vector vectorDoc3M015 = new Vector();
    Hashtable hashtableFactoryNo = new Hashtable();
    // 清除、設定列數
    doClearContent(exeExcel.getPageNo(), intMaxPage, objectSheet2, exeExcel);
    //
    int intStartDataRow = exeExcel.getStartDataRow();
    int intPageDataRow = exeExcel.getPageDataRow();
    int intRecordNo = intStartDataRow;
    double doubleTemp = 0;
    String stringTEL = "";
    //
    for (int intNo = 0; intNo < 3; intNo++) {
      exeExcel.putDataIntoExcel(arrayPosFactoryNo[intNo], 4, "", objectSheet2);
    }
    int intFlowNo = 0;
    String stringFlowName = "";
    for (int intNo = 0; intNo < vectorFactoryNo.size(); intNo++) {
      stringFactoryNo = "" + vectorFactoryNo.get(intNo);
      if ("NONE".equals(stringFactoryNo)) continue;
      //
      vectorDoc3M015 = exeUtil.getQueryDataHashtable("Doc3M015", new Hashtable(), " AND  OBJECT_CD  =  '" + stringFactoryNo + "' ", dbDoc);
      if (vectorDoc3M015.size() == 0) continue;
      stringFactoryName = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "OBJECT_SHORT_NAME");
      stringContactMan = exeUtil.getVectorFieldValue(vectorDoc3M015, 0, "CONTACT_MAN_PURCHASE");
      if (!"".equals(stringContactMan)) stringContactMan = "/" + stringContactMan;
      stringTEL = getFactoryNoTel(vectorDoc3M015, exeUtil);
      if (!"".equals(stringTEL)) stringTEL = "\n" + stringTEL;
      //
      intFlowNo = (intFactoryTypeNo - 1) * 3 + intNo;
      stringFlowName = exeExcel.getExcelColumnName("A", intFlowNo);
      //
      stringFactoryName = stringFlowName + ". " + stringFactoryName;
      stringFactoryName += stringContactMan + stringTEL;
      exeExcel.putDataIntoExcel(arrayPosFactoryNo[intNo], 4, stringFactoryName, objectSheet2);
    }
    for (int intNo = 0; intNo < vectorDoc3M012.size(); intNo++) {
      stringCostID = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "CostID");
      stringCostID1 = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "CostID1");
      stringClassName = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "ClassName");
      stringUnit = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "Unit");
      stringBudgetNum = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "BudgetNum");
      stringHistoryPrice = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "HistoryPrice");
      stringHistoryPrice = convert.FourToFive(stringHistoryPrice, 1);
      // stringApplyMoney = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo,
      // "ApplyMoney") ;
      stringClassNameDescript = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "ClassNameDescript");
      stringFILTER = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "FILTER");
      stringFactoryNo = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "FactoryNo");
      stringActualPrice = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "ActualPrice");
      stringPurchaseMoney = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "PurchaseMoney");
      stringCostIDDetail = exeUtil.getVectorFieldValue(vectorDoc3M012, intNo, "CostIDDetail");
      if ("null".equals(stringCostIDDetail)) stringCostIDDetail = "";
      //
      intPos = vectorFactoryNo.indexOf(stringFactoryNo);
      //
      if (!"".equals(stringFILTER)) {
        stringCostID = stringFILTER;
        stringCostID1 = "";
        stringClassName = getFilterName(stringFILTER);
      } else if (!"".equals(stringCostIDDetail)) {
        stringClassName = exeUtil.doSubstring(stringCostIDDetail, 0, 4) + "-"
            + exeUtil.getNameUnion("DESCRIPTION", "Doc2M022", " AND  CostIDDetail  =  '" + stringCostIDDetail + "' ", new Hashtable(), dbDoc);
        ;
      } else {
        stringClassName = stringCostID + stringCostID1 + "-" + stringClassNameDescript;
        /*
         * if(stringClassName.indexOf(stringClassNameDescript) != -1) { stringClassName
         * = stringCostID+stringCostID1+"-"+stringClassName ; } else
         * if(stringClassNameDescript.indexOf(stringClassName) != -1) { stringClassName
         * = stringCostID+stringCostID1+"-"+stringClassNameDescript ; } else {
         * stringClassName =
         * stringCostID+stringCostID1+"-"+stringClassName+"-"+stringClassNameDescript ;
         * }
         */
      }
      //
      exeExcel.putDataIntoExcel(1, intRecordNo, "" + (intNo + 1), objectSheet2); // No
      exeExcel.putDataIntoExcel(2, intRecordNo, stringClassName, objectSheet2); // 分類工料
      exeExcel.putDataIntoExcel(4, intRecordNo, stringHistoryPrice, objectSheet2); // 預算單價
      exeExcel.putDataIntoExcel(6, intRecordNo, stringUnit, objectSheet2); // 單位
      exeExcel.putDataIntoExcel(7, intRecordNo, stringBudgetNum, objectSheet2); // 申請數量
      if (intPos != -1) {
        exeExcel.putDataIntoExcel(arrayPosPrice[intPos], intRecordNo, stringActualPrice, objectSheet2); // 單價
        exeExcel.putDataIntoExcel(arrayPosMoney[intPos], intRecordNo, stringPurchaseMoney, objectSheet2); // 金額
        //
        doubleTemp = exeUtil.doParseDouble(stringPurchaseMoney) + exeUtil.doParseDouble("" + hashtableFactoryNo.get(stringFactoryNo));
        hashtableFactoryNo.put(stringFactoryNo, convert.FourToFive("" + doubleTemp, 0));
      }
      intRecordNo++;
      // 滿頁時，將 Sheet2 Copy Sheet1
      if (intRecordNo >= (intPageDataRow + intStartDataRow)) {
        if (intNo == vectorDoc3M012.size() - 1) {
          for (int intNoL = 0; intNoL < vectorFactoryNo.size(); intNoL++) {
            stringFactoryNo = "" + vectorFactoryNo.get(intNoL);
            if ("NONE".equals(stringFactoryNo)) continue;
            doubleTemp = exeUtil.doParseDouble("" + hashtableFactoryNo.get(stringFactoryNo));
            //
            if (doubleTemp == 0) continue;
            //
            exeExcel.putDataIntoExcel(arrayPosPrice[intNoL], 24, convert.FourToFive("" + doubleTemp, 0), objectSheet2);
            exeExcel.putDataIntoExcel(arrayPosPrice[intNoL], 25, "Ｖ", objectSheet2);
          }
        }
        exeExcel.CopyPage(objectSheet1, objectSheet2);
        exeExcel.doAdd1PageNo();
        doClearContent(exeExcel.getPageNo(), intMaxPage, objectSheet2, exeExcel);
        intRecordNo = intStartDataRow;
      }
    }
    if (intRecordNo != intStartDataRow) {
      for (int intNoL = 0; intNoL < vectorFactoryNo.size(); intNoL++) {
        stringFactoryNo = "" + vectorFactoryNo.get(intNoL);
        if ("NONE".equals(stringFactoryNo)) continue;
        doubleTemp = exeUtil.doParseDouble("" + hashtableFactoryNo.get(stringFactoryNo));
        //
        if (doubleTemp == 0) continue;
        //
        exeExcel.putDataIntoExcel(arrayPosPrice[intNoL], 24, convert.FourToFive("" + doubleTemp, 0), objectSheet2);
        exeExcel.putDataIntoExcel(arrayPosPrice[intNoL], 25, "Ｖ", objectSheet2);
      }
      exeExcel.CopyPage(objectSheet1, objectSheet2);
      exeExcel.doAdd1PageNo();
      doClearContent(exeExcel.getPageNo(), intMaxPage, objectSheet2, exeExcel);
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
    return "---------------button5(\u532f\u51fa).defaultValue()----------------";
  }
}
