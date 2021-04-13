package SaleEffect;

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
import Farglory.Excel.FargloryExcel;

public class Sale03R510_202101 extends bproc {
  String dateType = ""; 
  
  public String getDefaultValue(String value) throws Throwable {
    System.out.println("ButtonExcelView20180503 ========= START =========");
    FargloryUtil exeUtil = new FargloryUtil();
    talk dbSale = getTalk("" + get("put_dbSale"));
    talk dbDoc = getTalk("" + get("put_dbDoc"));
    talk dbDocCS = null;// exeUtil.getTalkCS("Doc") ; // 2018019 //
    talk dbAO = getTalk("" + get("put_dbAO"));
    
    //全域欄位取值
    dateType = getValue("dateType").trim();
    
    //
    if (!isBatchCheckOK())
      return value;
    //
    /*
     * if(dbDocCS == null) { messagebox("無法連線至 人壽 系統。") ; }
     */
    dbExcel(exeUtil, dbAO, dbDoc, dbDocCS, dbSale);
    System.out.println("ButtonExcelView20180503 ========= END =========");
    return value;
  }

  public void dbExcel(FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbDocCS, talk dbSale) throws Throwable {
    int[] arrayRow = { 9, 10, 11, 12, 13, 14, 16, 17, 18, 20, 21 };
    String stringFilePath = "g:\\資訊室\\Excel\\SaleEffect\\Sale03R510_20180503.xlt";
    FargloryExcel exeExcel = new FargloryExcel();
    Hashtable hashtableDataPosition = null;
    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheets = (Dispatch) retVector.get(3);
    hashtableDataPosition = doExcel1(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbDocCS, dbSale);

    // 業績
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(2)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel2(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbSale);
    
    // 新來人
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(3)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel3(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbSale);
    
    // 總來人
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(4)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel4(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbSale);
    
    // 3-6 DATA-企劃費用 (純)8
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(8)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel8(hashtableDataPosition, objectSheet1, exeExcel, exeUtil);
    // 3-6 DATA-企劃費用 (全)9
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(9)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel9(hashtableDataPosition, objectSheet1, exeExcel, exeUtil);
    
    // 釋放 Excel 物件
    exeExcel.getReleaseExcelObject(retVector);
  }

  public Hashtable doExcel1(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbDocCS, talk dbSale)
      throws Throwable {
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
//    String dateType = getValue("dateType").trim();
    // String stringFilePath =
    // "g:\\資訊室\\Excel\\SaleEffect\\Sale03R510_2017-02-18.xlt" ;
    long longTime1 = exeUtil.getTimeInMillis();
    long longTime2 = 0;
    String[] arrayValuePOS = null;
    String[] arraySSMediaIDPos = { "4", "6", "8", "10", "12", "14", "18", "20", "22", "26", "28" };
    String[] arraySSMediaID = { "H", "F", "A", "G", "J", "B", "I", "D", "L", "K", "C" };
    String[] arrayValue = { "3", "5", "7", "9", "11", "13", "17", "19", "21", "25", "27" };
    String[][] retTitle2 = { { "直效通路", "已購客戶", "未購名單", "異業合作", "同業通路", "企業團" }, { "媒體通路", "網路通路", "簡訊" }, { "POP", "接待通路" } };
    Vector vectorTitles = getTitles(retTitle2);
    // 第一表格
    String[][] retSale03R510A = new String[0][0];
    String[][] retSale03R510A2 = new String[0][0];
    String[][] retSale03R510ADealDay = new String[0][0];
    System.out.println("stringYearAC>>>" + stringYearAC);
    System.out.println("stringMonth>>>" + stringMonth);
    System.out.println("dateType>>>" + dateType);
    retSale03R510A = dbSale.queryFromPool("speMakerSale03R510A2  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth  + "',  '" + dateType + "' " ); // 業績
    retSale03R510A2 = dbAO.queryFromPool("AO_SPSale03R510A  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // 新來人數 11、總來人數 13
    retSale03R510ADealDay = dbAO.queryFromPool("AO_SPSale03R510DealDay  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // 成交天數19
    // 第二表格
    String[][] retSale03R510B = dbSale.queryFromPool("speMakerSale03R510B2  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth  + "',  '" + dateType + "' " ); // 業績
    String stringRowLine = "";
    Vector vectorCostID = new Vector();
    Hashtable hashtableDataPositionTable1 = new Hashtable();
    Hashtable hashtableDataPositionTable2 = new Hashtable();
    Hashtable hashtableSale03R510B = getSale03R510BH(stringProjectID1, stringYearAC, stringMonth, retSale03R510B, exeUtil, dbSale); // 業績
    Hashtable hashtableSale03R510B2 = getSale03R510B2H(stringProjectID1, stringYearAC, stringMonth, exeUtil, dbAO); // 新來人
    Hashtable hashtableSale03R510C = getSale03R510CH(stringProjectID1, stringYearAC, stringMonth, exeUtil, dbAO); // 通路總來人
    Hashtable hashtableData = null;
    //
    hashtableDataPositionTable1 = getMoneyFrontTable1(arraySSMediaIDPos, arraySSMediaID, retSale03R510B, exeUtil, dbDoc, dbDocCS, vectorCostID);
    hashtableDataPositionTable2 = getMoneyFrontTable2(arrayRow, vectorTitles, arraySSMediaIDPos, arraySSMediaID, retSale03R510B, exeUtil, dbDoc, dbDocCS, vectorCostID);
    //
    // 20180504 使用傳入參數
    // FargloryExcel exeExcel = new FargloryExcel( ) ;
    //
    // exeExcel.setVisibleProperty(false) ; // 控制顯不顯示 Excel
    //
    // Vector retVector = exeExcel.getExcelObject(stringFilePath) ;
    // 20180504 使用傳入參數
    // Dispatch objectSheet1 = (Dispatch)retVector.get(1) ;
    Dispatch objectClick = null;
    int intRowOrder = 99;
    int intRowL = 0;
    int intCol = 0;
    int intCol2 = 0;
    int intColOrder11 = 4;
    int intColOrder12 = 8;
    int intColOrder21 = 10;
    int intColOrder22 = 14;
    int intColOrder31 = 16;
    int intColOrder32 = 20;
    int intColOrder41 = 22;
    int intColOrder42 = 26;
    int intColOrder51 = 28;
    int intColOrder52 = 32;
    int intPos = 0;
    int intPos2 = 0;
    String stringValue = "";
    String stringColName = "";
    String stringTemp = "";
    String stringTitle = "";
    String stringTotMoney = exeUtil.getNameUnion("TotMoney", "A_Project", " AND  ProjectID  =  '" + stringProjectID1 + "' ", new Hashtable(), dbSale);
    String[][] retDataPosition = null;
    // 標題
    stringValue = stringProjectID1 + "案行銷策略月報表";
    exeExcel.putDataIntoExcel(0, 0, stringValue, objectSheet1);
    stringValue = ("O".equals(dateType)? "付訂":"簽約") + "年度：" + stringYearAC + "/" + stringMonth + "月";
    exeExcel.putDataIntoExcel(0, 1, stringValue, objectSheet1);
    for (int intRow = 0; intRow <= 57; intRow++) {
      // 第一表格
      if (intRow == 17) {
        retDataPosition = (String[][]) hashtableDataPositionTable1.get("Table1DATA");
        if (retDataPosition != null) {
          for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
            intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
            intRowL = exeUtil.doParseInteger(retDataPosition[intNo][1]);
            stringValue = retDataPosition[intNo][2];
            //
            if (exeUtil.doParseDouble(stringValue) == 0)
              stringValue = "0";
            //
            exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRowL), stringValue, objectSheet1);
          }
        }
      }
      
      // 業績
      for (int intNo = 0; intNo < retSale03R510A.length; intNo++) {
        intRowL = exeUtil.doParseInteger(retSale03R510A[intNo][0].trim()) - 1;
        //
        if (intRowL <= 0) continue;
        if (intRowL != intRow) continue;
        // 1-12 月
        for (int intNoL = 1; intNoL <= 12; intNoL++) {
          stringValue = retSale03R510A[intNo][intNoL + 1].trim();
          intCol = 4 + (intNoL - 1) * 2;
          exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
        }
        // 3 去年12月
        stringValue = retSale03R510A[intNo][14].trim();
        exeExcel.putDataIntoExcel(3, intRowL, stringValue, objectSheet1);
        // 28年累計
        stringValue = retSale03R510A[intNo][15].trim();
        exeExcel.putDataIntoExcel(28, intRowL, stringValue, objectSheet1);
        // 31 案累計 2016-08-09 改為總銷金額
        stringValue = retSale03R510A[intNo][16].trim();
        if (intRowL == 3) stringValue = stringTotMoney;
        exeExcel.putDataIntoExcel(31, intRowL, stringValue, objectSheet1);
      }
      
      // 新來人數 11、總來人數 13
      for (int intNo = 0; intNo < retSale03R510A2.length; intNo++) {
        intRowL = exeUtil.doParseInteger(retSale03R510A2[intNo][0].trim()) - 1;
        //
        if (intRowL <= 0)
          continue;
        if (intRowL != intRow)
          continue;
        // 1-12 月
        for (int intNoL = 1; intNoL <= 12; intNoL++) {
          stringValue = retSale03R510A2[intNo][intNoL + 1].trim();
          intCol = 4 + (intNoL - 1) * 2;
          exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
        }
        // 14 去年
        stringValue = retSale03R510A2[intNo][14].trim();
        exeExcel.putDataIntoExcel(3, intRowL, stringValue, objectSheet1);
        // 15
        stringValue = retSale03R510A2[intNo][15].trim();
        exeExcel.putDataIntoExcel(28, intRowL, stringValue, objectSheet1);
        // 16 案累計
        stringValue = retSale03R510A2[intNo][16].trim();
        exeExcel.putDataIntoExcel(31, intRowL, stringValue, objectSheet1);
      }
      // 成交天數19 -> 24
      for (int intNo = 0; intNo < retSale03R510ADealDay.length; intNo++) {
        intRowL = exeUtil.doParseInteger(retSale03R510ADealDay[intNo][0].trim()) - 1;
        //
        if (intRowL <= 0)
          continue;
        if (intRowL != intRow)
          continue;
        // 1-12 月
        for (int intNoL = 1; intNoL <= 12; intNoL++) {
          stringValue = retSale03R510ADealDay[intNo][intNoL + 1].trim();
          intCol = 4 + (intNoL - 1) * 2;
          exeExcel.putDataIntoExcel(intCol, intRowL + 5, stringValue, objectSheet1);
        }
        // 14 去年
        stringValue = retSale03R510ADealDay[intNo][14].trim();
        exeExcel.putDataIntoExcel(3, intRowL + 5, stringValue, objectSheet1);
        // 15 年累計
        stringValue = retSale03R510ADealDay[intNo][15].trim();
        exeExcel.putDataIntoExcel(28, intRowL + 5, stringValue, objectSheet1);
        // 16 案累計
        stringValue = retSale03R510ADealDay[intNo][16].trim();
        exeExcel.putDataIntoExcel(31, intRowL + 5, stringValue, objectSheet1);
      }

      // 第二表格
      if (intRow == 43) {
        retDataPosition = (String[][]) hashtableDataPositionTable2.get("Table2DATA");
        if (retDataPosition != null) {
          for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
            intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
            intRowL = exeUtil.doParseInteger(retDataPosition[intNo][1]);
            stringValue = retDataPosition[intNo][2];
            //
            if (exeUtil.doParseDouble(stringValue) == 0)
              stringValue = "0";
            //
            System.out.println("第二表格 費用 Col(" + intCol + ")Row(" + intRowL + ")值(" + stringValue + ")--------------------------------------------");
            exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
            // exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRowL), stringValue,
            // objectSheet1) ;
          }
        }
      }

      // 第二表格-業績
      stringRowLine = convert.add0("" + intRow, "3");
      System.out.println(stringRowLine + "--------------------------------------------");
      hashtableData = (Hashtable) hashtableSale03R510B.get(stringRowLine);
      if (hashtableData != null) {
        for (int intNo = 0; intNo < vectorTitles.size(); intNo++) {
          stringTitle = "" + vectorTitles.get(intNo);
          stringValue = "" + hashtableData.get(stringTitle);
          if ("".equals(stringValue))
            stringValue = "0";
          if ("null".equals(stringValue))
            stringValue = "0";
          //
          intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNo].trim());
          //
          exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRow), stringValue, objectSheet1);
        }
        // 遠雄之友
        stringValue = "" + hashtableData.get("遠雄之友");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        // System.out.println(stringRowLine+"業績("+getChangeExcelRow(intRow)+")("+stringValue+")-------------------------")
        // ;
        exeExcel.putDataIntoExcel(3, getChangeExcelRow(intRow), stringValue, objectSheet1);
        // PASS
        stringValue = "" + hashtableData.get("PASS");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        // System.out.println(stringRowLine+"業績("+getChangeExcelRow(intRow)+")("+stringValue+")-------------------------")
        // ;
        exeExcel.putDataIntoExcel(2, getChangeExcelRow(intRow), stringValue, objectSheet1);
      }

      // // 第二表格-通路總來人-遠雄之友
      hashtableData = (Hashtable) hashtableSale03R510C.get(stringRowLine);
      if (hashtableData != null) {
        for (int intNo = 0; intNo < vectorTitles.size(); intNo++) {
          stringTitle = "" + vectorTitles.get(intNo);
          stringValue = "" + hashtableData.get(stringTitle);
          if ("".equals(stringValue))
            stringValue = "0";
          if ("null".equals(stringValue))
            stringValue = "0";
          //
          intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNo].trim());
          //
          exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRow), stringValue, objectSheet1);
        }
        // 遠雄之友
        stringValue = "" + hashtableData.get("遠雄之友");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(3, getChangeExcelRow(intRow), stringValue, objectSheet1);
        // PASS
        stringValue = "" + hashtableData.get("PASS");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(2, getChangeExcelRow(intRow), stringValue, objectSheet1);
      }

      // 第二表格-新來人 放在最後
      hashtableData = (Hashtable) hashtableSale03R510B2.get(stringRowLine);
      if (hashtableData != null) {
        for (int intNo = 0; intNo < vectorTitles.size(); intNo++) {
          stringTitle = "" + vectorTitles.get(intNo);
          stringValue = "" + hashtableData.get(stringTitle);
          if ("".equals(stringValue))
            stringValue = "0";
          if ("null".equals(stringValue))
            stringValue = "0";
          //
          intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNo].trim());
          //
          exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRow), stringValue, objectSheet1);
        }
        // 遠雄之友
        stringValue = "" + hashtableData.get("遠雄之友");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(3, getChangeExcelRow(intRow), stringValue, objectSheet1);
        // PASS
        stringValue = "" + hashtableData.get("PASS");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(2, getChangeExcelRow(intRow), stringValue, objectSheet1);
      }

      //
      if (intRow == 57) {
        SetOrderList("業績", "MAX", 32, 33, intColOrder11, intColOrder12, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel);
        SetOrderList("新來人", "MAX", 47, 48, intColOrder21, intColOrder22, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel);
        SetOrderList("總來人", "MAX", 61, 62, intColOrder31, intColOrder32, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel);
        // 來人費用-名次-純
        SetOrderList("來人費用", "MIN", 71, 72, intColOrder41, intColOrder42, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // 全
        SetOrderList("來人費用", "MIN", 81, 82, intColOrder41, intColOrder42, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // 純
        // 成交費用率-成交-全
        SetOrderList("成交費用率", "MIN", 95, 96, intColOrder51, intColOrder52, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // 純
        SetOrderList("成交費用率", "MIN", 89, 90, intColOrder51, intColOrder52, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // 全
      }
    }
    retDataPosition = (String[][]) hashtableDataPositionTable2.get("OTHER");
    if (retDataPosition != null) {
      for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
        intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
        intRowL = exeUtil.doParseInteger(retDataPosition[intNo][1]);
        stringValue = retDataPosition[intNo][2];
        //
        // System.out.println(intNo+"intCol("+intCol+")intRowL("+intRowL+")stringValue("+stringValue+")------------------------------------S")
        // ;
        if ("null".equals(stringValue))
          continue;
        //
        // System.out.println("OTHER
        // Col("+intCol+")Row("+intRowL+")值("+intRowL+")------------------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
      }
    }
    // exeExcel.setPreView(false, "C:\\oce.xls") ; // 當不預覽時，且有傳入路徑時，另存新檔。
    // exeExcel.setVisiblePropertyOnFlow(false, retVector) ; // 控制顯不顯示 Excel
    //
    // 釋放 Excel 物件
    // exeExcel.getReleaseExcelObject(retVector) ;

    return hashtableDataPositionTable2;
  }

  public Vector getTitles(String[][] retTitle2) throws Throwable {
    Vector vectorTitles = new Vector();
    //
    for (int intNo = 0; intNo < retTitle2.length; intNo++) {
      for (int intNoL = 0; intNoL < retTitle2[intNo].length; intNoL++) {
        vectorTitles.add(retTitle2[intNo][intNoL].trim());
      }
    }
    //
    return vectorTitles;
  }

  // 業績
  public Hashtable getSale03R510BH(String stringProjectID1, String stringYearAC, String stringMonth, String[][] retSale03R510B, FargloryUtil exeUtil, talk dbSale)
      throws Throwable {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
    String[] arrayRowDefine = { "列數", "無", "未購名單", "直效通路", "已購客戶", "異業合作", "同業通路", "企業團", "媒體通路", "網路通路", "簡訊", "POP", "接待通路", "遠雄之友", "PASS" };
    String stringRowLine = "";
    String stringTemp = "";
    Hashtable hashtableSale03R510B = new Hashtable();
    Hashtable hashtableSale03R510BData = new Hashtable();
    int intRow = 0;
    for (int intNo = 0; intNo < retSale03R510B.length; intNo++) {
      stringRowLine = retSale03R510B[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1;
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510BData = (Hashtable) hashtableSale03R510B.get(stringRowLine);
      if (hashtableSale03R510BData == null) {
        hashtableSale03R510BData = new Hashtable();
        hashtableSale03R510B.put(stringRowLine, hashtableSale03R510BData);
      }
      for (int intNoL = 0; intNoL < arrayRowDefine.length; intNoL++) {
        stringTemp = retSale03R510B[intNo][intNoL].trim();
        if (intNoL < 13 && intRow == 28) {
          stringTemp = "";
        }
        // System.out.println(stringRowLine+"("+arrayRowDefine[intNoL]+")("+stringTemp+")-------------------------")
        // ;
        hashtableSale03R510BData.put(arrayRowDefine[intNoL], stringTemp);
      }
    }
    // 2 3 4
    // "列數", "無", "未購名單", "直效通路", "已購客戶", "異業合作", "同業通路", "企業團", "媒體通路", "網路通路",
    // "簡訊", "POP", "接待通路", "遠雄之友", "PASS"
    // 3 4 2
    // "直效通路", "已購客戶", "未購名單"
    String stringTemp2 = "";
    String stringTemp3 = "";
    String stringTemp4 = "";
    for (int intNo = 0; intNo < retSale03R510B.length; intNo++) {
      stringTemp2 = retSale03R510B[intNo][2].trim();
      stringTemp3 = retSale03R510B[intNo][3].trim();
      stringTemp4 = retSale03R510B[intNo][4].trim();
      //
      retSale03R510B[intNo][2] = stringTemp3;
      retSale03R510B[intNo][3] = stringTemp4;
      retSale03R510B[intNo][4] = stringTemp2;
    }
    return hashtableSale03R510B;
  }

  public Hashtable getSale03R510B2H(String stringProjectID1, String stringYearAC, String stringMonth, FargloryUtil exeUtil, talk dbAO) throws Throwable {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12
    String[] arrayRowDefine = { "列數", "無", "未購名單", "直效通路", "已購客戶", "異業合作", "同業通路", "企業團", "媒體通路", "網路通路", "簡訊", "POP", "接待通路" };
    String[][] retSale03R510BE = dbAO.queryFromPool("AO_SPSale03R510B_E  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // 新來人之遠雄之友、PASS
    String[][] retSale03R510B2 = dbAO.queryFromPool("AO_SPSale03R510B  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // 新來人
    String stringRowLine = "";
    String stringTemp = "";
    Hashtable hashtableSale03R510B2 = new Hashtable();
    Hashtable hashtableSale03R510B2Data = new Hashtable();
    int intRow = 0;
    // 第二表格-新來人-遠雄之友、PASS
    for (int intNo = 0; intNo < retSale03R510BE.length; intNo++) {
      stringRowLine = retSale03R510BE[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1;
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510B2Data = (Hashtable) hashtableSale03R510B2.get(stringRowLine);
      if (hashtableSale03R510B2Data == null) {
        hashtableSale03R510B2Data = new Hashtable();
        hashtableSale03R510B2.put(stringRowLine, hashtableSale03R510B2Data);
      }
      // 遠雄之友
      stringTemp = retSale03R510BE[intNo][2].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510B2Data.put("遠雄之友", stringTemp);
      }
      // PASS
      stringTemp = retSale03R510BE[intNo][3].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510B2Data.put("PASS", stringTemp);
      }
    }
    for (int intNo = 0; intNo < retSale03R510B2.length; intNo++) {
      stringRowLine = retSale03R510B2[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1; // 列數減1
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510B2Data = (Hashtable) hashtableSale03R510B2.get(stringRowLine);
      if (hashtableSale03R510B2Data == null) {
        hashtableSale03R510B2Data = new Hashtable();
        hashtableSale03R510B2.put(stringRowLine, hashtableSale03R510B2Data);
      }
      for (int intNoL = 0; intNoL < arrayRowDefine.length; intNoL++) {
        stringTemp = retSale03R510B2[intNo][intNoL].trim();
        if (intRow == 34) {
          stringTemp = "";
        }
        hashtableSale03R510B2Data.put(arrayRowDefine[intNoL], stringTemp);
      }
    }
    return hashtableSale03R510B2;
  }

  public Hashtable getSale03R510CH(String stringProjectID1, String stringYearAC, String stringMonth, FargloryUtil exeUtil, talk dbAO) throws Throwable {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12
    String[] arrayRowDefine = { "列數", "無", "未購名單", "直效通路", "已購客戶", "異業合作", "同業通路", "企業團", "媒體通路", "網路通路", "簡訊", "POP", "接待通路" };
    String[][] retSale03R510C = dbAO.queryFromPool("AO_SPSale03R510C  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // 通路總來人
    String[][] retSale03R510CE = dbAO.queryFromPool("AO_SPSale03R510C_E  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // 通路總來人之遠雄之友*/
    String stringRowLine = "";
    String stringTemp = "";
    Hashtable hashtableSale03R510C = new Hashtable();
    Hashtable hashtableSale03R510CData = new Hashtable();
    int intRow = 0;
    // 第二表格-通路總來人-遠雄之友、PASS
    for (int intNo = 0; intNo < retSale03R510CE.length; intNo++) {
      stringRowLine = retSale03R510CE[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1;
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510CData = (Hashtable) hashtableSale03R510C.get(stringRowLine);
      if (hashtableSale03R510CData == null) {
        hashtableSale03R510CData = new Hashtable();
        hashtableSale03R510C.put(stringRowLine, hashtableSale03R510CData);
      }
      // 遠雄之友
      stringTemp = retSale03R510CE[intNo][2].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510CData.put("遠雄之友", stringTemp);
      }
      // PASS
      stringTemp = retSale03R510CE[intNo][3].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510CData.put("PASS", stringTemp);
      }
    }
    for (int intNo = 0; intNo < retSale03R510C.length; intNo++) {
      stringRowLine = retSale03R510C[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1; // 列數減1
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510CData = (Hashtable) hashtableSale03R510C.get(stringRowLine);
      if (hashtableSale03R510CData == null) {
        hashtableSale03R510CData = new Hashtable();
        hashtableSale03R510C.put(stringRowLine, hashtableSale03R510CData);
      }
      for (int intNoL = 0; intNoL < arrayRowDefine.length; intNoL++) {
        stringTemp = retSale03R510C[intNo][intNoL].trim();
        if (intRow == 40) {
          stringTemp = "";
        }
        hashtableSale03R510CData.put(arrayRowDefine[intNoL], stringTemp);
      }
    }
    return hashtableSale03R510C;
  }

  public void SetOrderList(String stringTitile, String stringOrderType, int intGetDataRow, int intPutDataRow, int intColOrder11, int intColOrder12, int intRowOrder,
      String[] arraySSMediaID, String[] arraySSMediaIDPos, String[][] retTitle2, Dispatch objectSheet1, FargloryUtil exeUtil, FargloryExcel exeExcel) throws Throwable {
    int intCol = 0;
    int intCol2 = 0;
    int intPos = 0;
    int intPos2 = 0;
    String stringTemp = "";
    String stringValue = "";
    String stringColName = "";
    String[] arrayValue = new String[arraySSMediaID.length];
    String[] arrayValuePOS = new String[arraySSMediaID.length];
    for (int intNoL = 0; intNoL < arraySSMediaID.length; intNoL++) {
      // 年累計
      stringTemp = exeExcel.getDataFromExcel2(exeUtil.doParseInteger(arraySSMediaIDPos[intNoL]), intGetDataRow, objectSheet1);
      if ("null".equals(stringTemp))
        stringTemp = "";
      // System.out.println(stringTitile+"排名1
      // Col("+arraySSMediaIDPos[intNoL]+")Row("+intGetDataRow+")值("+stringTemp+")------------------------------------")
      // ;
      arrayValue[intNoL] = stringTemp;
    }
    arrayValuePOS = exeUtil.getOrderList(stringOrderType, arrayValue);
    for (int intNoL = 0; intNoL < arraySSMediaID.length; intNoL++) {
      stringValue = arrayValuePOS[intNoL];
      intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNoL]);
      //
      exeExcel.putDataIntoExcel(intCol, intPutDataRow, stringValue, objectSheet1);
      // 排名
      // 1-3 名
      // System.out.println(stringTitile+" 排名2
      // Col("+intCol+")RowL("+intPutDataRow+")值("+stringValue+")------------------------------------")
      // ;
      intPos = exeUtil.doParseInteger(exeUtil.doDeleteDogAfterZero(stringValue));
      if (intPos > 3)
        continue;
      if (intPos == 0)
        continue;
      //
      if (intCol <= 14) {
        intPos2 = 0;
        intCol2 = (intCol - 4) / 2;
      } else if (intCol >= 26) {
        intPos2 = 2;
        intCol2 = (intCol - 26) / 2;
      } else {
        intPos2 = 1;
        intCol2 = (intCol - 18) / 2;
      }
      //
      exeExcel.putDataIntoExcel(intColOrder11, intRowOrder + intPos, retTitle2[intPos2][intCol2], objectSheet1);
      stringColName = exeExcel.getExcelColumnName("A", intCol);
      if (",業績,新來人,總來人,".indexOf(stringTitile) != -1)
        stringTemp = "=" + stringColName + "" + (intGetDataRow + 1) + "/AG" + (intGetDataRow + 1);
      if (",成交費用率,來人費用,".indexOf(stringTitile) != -1)
        stringTemp = "=" + stringColName + "" + (intGetDataRow + 1);
      // System.out.println(stringTitile+" 名次
      // Col("+intColOrder12+")Row("+(intRowOrder+intPos)+")值("+stringTemp+")------------------------------------")
      // ;
      exeExcel.putDataIntoExcel(intColOrder12, intRowOrder + intPos, stringTemp, objectSheet1);
    }
  }

  public int getChangeExcelRow(int intRow) throws Throwable {
    // 業績
    if (25 == intRow + 1)
      return 29;// 本月值25 改為 30
    if (26 == intRow + 1)
      return 30;// 上月值26 改為 31
    if (28 == intRow + 1)
      return 32;// 年累計28 改為 33
    if (29 == intRow + 1)
      return 33;// 年排名29 改為 34
    if (30 == intRow + 1)
      return 34;// 案累計30 改為 35
    // 新來人
    if (31 == intRow + 1)
      return 38;// 本月值31 改為 39
    if (32 == intRow + 1)
      return 41;// 上月值32 改為 42
    if (34 == intRow + 1)
      return 45;// 年累計34 改為 46
    if (35 == intRow + 1)
      return 48;// 年排名35 改為 49 由JAVA 處理
    if (36 == intRow + 1)
      return 49;// 案累計36 改為 50
    // 總來人
    if (37 == intRow + 1)
      return 52;// 本月值37 改為 53
    if (38 == intRow + 1)
      return 55;// 上月值38 改為 56
    if (40 == intRow + 1)
      return 59;// 年累計40 改為 60
    if (41 == intRow + 1)
      return 62;// 年排名41 改為 63 由JAVA處理
    if (42 == intRow + 1)
      return 63;// 案累計42 改為 64
    // 排名
    if (61 == intRow + 1)
      return 100;
    if (62 == intRow + 1)
      return 101;
    if (63 == intRow + 1)
      return 102;
    return intRow;
  }

  // 業績 speMakerSale03R510B_DATA 'H111A','2015','08'
  public void doExcel2(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbSale) throws Throwable {
    int intCol = 2;
    int intRow = 0;
    String stringProjectID1 = getValue("ProjectID1").trim();
//    String dateType = getValue("dateType").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringMonthL = "";
    String stringValue = "";
    String[][] retTableData = null;
    for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringMonthL = convert.add0("" + intNo, "2");
      retTableData = dbSale.queryFromPool("speMakerSale03R510B_DATA2  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonthL + "',  '" + dateType + "' " ); // 業績
      for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
        stringValue = retTableData[0][intNoL + 2];
        intRow = arrayRow[intNoL];
        // System.out.println("intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
      }
      intCol++;
    }
  }

  // 新來人 AO_SPSale03R530B_DATA 'H111A','2015','08'
  public void doExcel3(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbSale) throws Throwable {
    int intCol = 2;
    int intRow = 0;
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringMonthL = "";
    String stringValue = "";
    String[][] retTableData = null;
    for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringMonthL = convert.add0("" + intNo, "2");
      retTableData = dbAO.queryFromPool("AO_SPSale03R510B_DATA  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonthL + "' "); // 業績
      for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
        stringValue = retTableData[0][intNoL + 2];
        intRow = arrayRow[intNoL];
        // System.out.println("intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
      }
      intCol++;
    }
  }

  // 總來人 AO_SPSale03R530C_DATA 'H111A','2015','08'
  public void doExcel4(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbSale) throws Throwable {
    int intCol = 2;
    int intRow = 0;
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringMonthL = "";
    String stringValue = "";
    String[][] retTableData = null;
    for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringMonthL = convert.add0("" + intNo, "2");
      retTableData = dbAO.queryFromPool("AO_SPSale03R510C_DATA  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonthL + "' "); // 業績
      for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
        stringValue = retTableData[0][intNoL + 2];
        intRow = arrayRow[intNoL];
        // System.out.println("intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
      }
      intCol++;
    }
  }

  public void doExcel8(Hashtable hashtableDataPosition, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil) throws Throwable {
    int intCol = 0;
    int intRow = 0;
    String stringValue = "";
    String stringValue2 = "";
    String[][] retDataPosition = (String[][]) hashtableDataPosition.get("SHEET8");
    if (retDataPosition == null)
      return;
    for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
      intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
      intRow = exeUtil.doParseInteger(retDataPosition[intNo][1]);
      stringValue = retDataPosition[intNo][2];
      //
      if (exeUtil.doParseDouble(stringValue) == 0)
        stringValue = "0";
      //
      exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
    }
  }

  public void doExcel9(Hashtable hashtableDataPosition, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil) throws Throwable {
    int intCol = 0;
    int intRow = 0;
    String stringValue = "";
    String stringValue2 = "";
    String[][] retDataPosition = (String[][]) hashtableDataPosition.get("SHEET9");
    if (retDataPosition == null)
      return;
    for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
      intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
      intRow = exeUtil.doParseInteger(retDataPosition[intNo][1]);
      stringValue = retDataPosition[intNo][2];
      //
      if (exeUtil.doParseDouble(stringValue) == 0)
        stringValue = "0";
      //
      exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
    }
  }

  public String[] getCostIDDoc7M070(String[] arraySSMediaID, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringSSMediaID = "";
    String stringTemp = "";
    String[] arrayCostID = new String[arraySSMediaID.length];
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
      stringSSMediaID = arraySSMediaID[intNo].trim();
      //
      hashtableAnd.put("UseType", "A");
      hashtableAnd.put("SSMediaID", stringSSMediaID);
      hashtableAnd.put("MoneyType", "A");
      vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, "", dbDoc);
      //
      stringTemp = "";
      for (int intNoL = 0; intNoL < vectorDoc7M070.size(); intNoL++) {
        hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNoL);
        if (hashtableDoc7M070 == null)
          continue;
        stringCostID = "" + hashtableDoc7M070.get("CostID");
        stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
        //
        stringTemp += "," + stringCostID + stringCostID1 + ",";

      }
      arrayCostID[intNo] = stringTemp;
      // System.out.println(intNo+"("+stringTemp+")------------------------------------------------")
      // ;
    }
    return arrayCostID;
  }

  // 直接歸屬 及 1-6拆分
  public String getCostIDMoneyTypeAC(FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostIDMoneyTypeAC = "";
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    hashtableAnd.put("UseType", "A");
    vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, " AND  MoneyType  IN  ('A',  'C') ORDER BY  CostID,  CostID1 ", dbDoc);
    //
    for (int intNo = 0; intNo < vectorDoc7M070.size(); intNo++) {
      hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNo);
      if (hashtableDoc7M070 == null)
        continue;
      stringCostID = "" + hashtableDoc7M070.get("CostID");
      stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
      //
      stringCostIDMoneyTypeAC += "," + stringCostID + stringCostID1 + ",";

    }
    return stringCostIDMoneyTypeAC;
  }

  public String getCostID1To6MediaDoc7M070(FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostID1To6Media = "";
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    hashtableAnd.put("UseType", "A");
    hashtableAnd.put("MoneyType", "C");
    vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, " ORDER BY  CostID,  CostID1 ", dbDoc);
    //
    for (int intNo = 0; intNo < vectorDoc7M070.size(); intNo++) {
      hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNo);
      if (hashtableDoc7M070 == null)
        continue;
      stringCostID = "" + hashtableDoc7M070.get("CostID");
      stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
      //
      stringCostID1To6Media += "," + stringCostID + stringCostID1 + ",";

    }
    return stringCostID1To6Media;
  }

  public String getCostID1To11MediaDoc7M070(FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostID1To11Media = "";
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    hashtableAnd.put("UseType", "A");
    hashtableAnd.put("MoneyType", "B");
    vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, " ORDER BY  CostID,  CostID1 ", dbDoc);
    //
    for (int intNo = 0; intNo < vectorDoc7M070.size(); intNo++) {
      hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNo);
      if (hashtableDoc7M070 == null)
        continue;
      stringCostID = "" + hashtableDoc7M070.get("CostID");
      stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
      //
      stringCostID1To11Media += "," + stringCostID + stringCostID1 + ",";

    }
    return stringCostID1To11Media;
  }

  public Hashtable getMoneyFrontTable1(String[] arraySSMediaIDPos, String[] arraySSMediaID, String[][] retSale03R510B, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS,
      Vector vectorCostID) throws Throwable {
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringDateStart = "";
    String stringDateEnd = "";
    Hashtable hashtableMoney = new Hashtable();
    Hashtable hashtableDataPosition = new Hashtable();
    // 日期區間
    stringDateStart = stringYearAC + "1201";
    stringDateStart = datetime.dateAdd(stringDateStart, "y", -1);
    stringDateStart = exeUtil.getDateConvertFullRoc(stringDateStart);
    stringDateEnd = stringYearAC + stringMonth + "01";
    stringDateEnd = datetime.dateAdd(stringDateEnd, "m", 1);
    stringDateEnd = datetime.dateAdd(stringDateEnd, "d", -1);
    stringDateEnd = exeUtil.getDateConvertFullRoc(stringDateEnd);
    // 全部 前年12月至-本年X月(每月、年累計) 001%-%103/05
    doCetMoneyTable1("A%-%001", stringProjectID1, stringDateStart, stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);
    // 全部 至本月底 案累計 002
    doCetMoneyTable1("A%-%002", stringProjectID1, "", stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);

    // 直接歸屬 前年12月至-本年X月(每月、年累計) 001%-%103/05
    // System.out.println("doCetMoneyTable1
    // B-----------------------------------------------------------S") ;
    doCetMoneyTable1("B%-%001", stringProjectID1, stringDateStart, stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);
    // System.out.println("doCetMoneyTable1
    // B-----------------------------------------------------------E") ;
    // 直接歸屬 至本月底 案累計 002
    doCetMoneyTable1("B%-%002", stringProjectID1, "", stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);

    // 資料處理 15
    Vector vectorData = new Vector();
    int intRow = 16;
    int intCol = 0;
    String stringDate = stringDateStart;
    String stringKEY = "";
    String stringValue = "";
    String stringCostID = "";
    String[] arrayTemp = new String[3];
    double doubleYear = 0;
    //
    stringDateStart = stringDateStart.replaceAll("/", "");
    for (int intNo = 0; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringDate = datetime.dateAdd(stringDateStart, "m", intNo);
      stringDate = exeUtil.getDateConvert(stringDate);
      stringDate = exeUtil.doSubstring(stringDate, 0, 7);
      intCol = (intNo == 0) ? 3 : 4 + (intNo - 1) * 2;
      // 月
      stringKEY = "A%-%001%-%" + stringDate;
      stringValue = "" + hashtableMoney.get(stringKEY);
      // 年累計加總
      if (intNo > 0) {
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      //
      // System.out.println(intCol+":"+intRow+"KEY("+stringKEY+")("+convert.FourToFive(stringValue,
      // 4)+")------------------------------") ;
      arrayTemp = new String[3];
      arrayTemp[0] = "" + intCol;
      arrayTemp[1] = "" + intRow;
      arrayTemp[2] = stringValue;
      vectorData.add(arrayTemp);
    }
    // 年累計
    arrayTemp = new String[3];
    arrayTemp[0] = "28";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = convert.FourToFive("" + doubleYear, 0);
    vectorData.add(arrayTemp);
    // 案累計
    stringValue = "" + hashtableMoney.get("A%-%002");
    arrayTemp = new String[3];
    arrayTemp[0] = "31";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = stringValue;
    vectorData.add(arrayTemp);

    intRow = 17;
    doubleYear = 0;
    //
    stringDateStart = stringDateStart.replaceAll("/", "");
    for (int intNo = 0; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringDate = datetime.dateAdd(stringDateStart, "m", intNo);
      stringDate = exeUtil.getDateConvert(stringDate);
      stringDate = exeUtil.doSubstring(stringDate, 0, 7);
      intCol = (intNo == 0) ? 3 : 4 + (intNo - 1) * 2;
      // 月
      stringKEY = "B%-%001%-%" + stringDate;
      stringValue = "" + hashtableMoney.get(stringKEY);
      // 年累計加總
      if (intNo > 0) {
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      //
      // System.out.println(intCol+":"+intRow+"KEY("+stringKEY+")("+convert.FourToFive(stringValue,
      // 4)+")------------------------------") ;
      arrayTemp = new String[3];
      arrayTemp[0] = "" + intCol;
      arrayTemp[1] = "" + intRow;
      arrayTemp[2] = stringValue;
      vectorData.add(arrayTemp);
    }
    // 年累計
    arrayTemp = new String[3];
    arrayTemp[0] = "28";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = convert.FourToFive("" + doubleYear, 0);
    vectorData.add(arrayTemp);
    // 案累計
    stringValue = "" + hashtableMoney.get("B%-%002");
    arrayTemp = new String[3];
    arrayTemp[0] = "31";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = stringValue;
    vectorData.add(arrayTemp);

    //
    hashtableDataPosition.put("Table1DATA", (String[][]) vectorData.toArray(new String[0][0]));

    return hashtableDataPosition;
  }

  public Hashtable getMoneyFrontTable2(int[] arrayRow, Vector vectorTitles, String[] arraySSMediaIDPos, String[] arraySSMediaID, String[][] retSale03R510B, FargloryUtil exeUtil,
      talk dbDoc, talk dbDocCS, Vector vectorCostID) throws Throwable {
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringDateStart = "";
    String stringDateEnd = "";
    String stringCostID1To6Media = getCostID1To6MediaDoc7M070(exeUtil, dbDoc);
    String stringCostID1To11Media = getCostID1To11MediaDoc7M070(exeUtil, dbDoc);
    String stringCostIDMoneyTypeAC = getCostIDMoneyTypeAC(exeUtil, dbDoc);
    String[] arrayCostID = getCostIDDoc7M070(arraySSMediaID, exeUtil, dbDoc);
    String[] arrayMoney = { "", "", "", "", "", "", "", "", "", "", "" };
    String[] arrayMoneyPOS = { "", "", "", "", "", "", "", "", "", "", "" };
    String[][] retQueryDate = new String[2][2];
    Hashtable hashtableMoney = new Hashtable();
    Hashtable hashtableDataPosition = new Hashtable();
    // 日期區間
    stringDateStart = stringYearAC + "1201";
    stringDateStart = datetime.dateAdd(stringDateStart, "y", -1);
    stringDateStart = exeUtil.getDateConvertFullRoc(stringDateStart);
    stringDateEnd = stringYearAC + stringMonth + "01";
    stringDateEnd = datetime.dateAdd(stringDateEnd, "m", 1);
    stringDateEnd = datetime.dateAdd(stringDateEnd, "d", -1);
    stringDateEnd = exeUtil.getDateConvertFullRoc(stringDateEnd);

    // 前年12月至-本年X月(每月、年累計) 全部 A%-%003%-%通路代碼(請款代碼)%-%103/05
    // 前年12月至-本年X月(每月、年累計) 純 B%-%003%-%通路代碼(請款代碼)%-%103/05
    doCetMoneyTable2("003", stringProjectID1, stringDateStart, stringDateEnd, stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, arraySSMediaID, arrayCostID,
        retSale03R510B, exeUtil, dbDoc, dbDocCS, hashtableMoney, vectorCostID);
    // 至本月底 案累計 全 A%-%004%-%通路代碼(請款代碼)
    // 至本月底 案累計 純 B%-%004%-%通路代碼(請款代碼)
    System.out.println("案累計---------------------------------S");
    doCetMoneyTable2("004", stringProjectID1, "", stringDateEnd, stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, arraySSMediaID, arrayCostID,
        retSale03R510B, exeUtil, dbDoc, dbDocCS, hashtableMoney, vectorCostID);
    System.out.println("案累計---------------------------------E");

    // 資料處理 15
    Vector vectorData = new Vector();
    int intRow = 0;
    int intRowL = 0;
    int intCol = 0;
    String stringDate = stringDateStart;
    String stringKEY = "";
    String stringValue = "";
    String stringCostID = "";
    String stringMode = "";
    String[] arrayTemp = new String[3];
    String[] arrayMode = { "A", "B" }; // A 全 B 純(不包含共同分攤)
    double doubleYear = 0;
    //
    // 資料處理 68 本月值-通路代碼
    intRow = 67;
    stringDate = stringYearAC + stringMonth + "01";
    stringDate = exeUtil.getDateConvert(stringDate);
    stringDate = exeUtil.doSubstring(stringDate, 0, 7);
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNo] + "%-%" + stringDate;
        stringValue = "" + hashtableMoney.get(stringKEY);
        //
        if (exeUtil.doParseDouble(stringValue) == 0)
          stringValue = "0";
        //
        System.out.println("(" + intNo + ")" + arraySSMediaIDPos[intNo] + ":" + intRowL + " 本月值 KEY(" + stringKEY + ")(" + vectorTitles.get(intNo) + ")("
            + convert.FourToFive(stringValue, 4) + ")------------------------------");
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
      }
      // 差異
      doubleYear = 0;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
        //
        stringValue = "" + hashtableMoney.get(stringKEY);
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
    }
    // 資料處理 70 上月值-通路代碼
    intRow = 69;
    stringDate = stringYearAC + stringMonth + "01";
    stringDate = datetime.dateAdd(stringDate, "m", -1);
    stringDate = exeUtil.getDateConvert(stringDate);
    stringDate = exeUtil.doSubstring(stringDate, 0, 7);
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNo] + "%-%" + stringDate;
        stringValue = "" + hashtableMoney.get(stringKEY);
        //
        if (exeUtil.doParseDouble(stringValue) == 0)
          stringValue = "0";
        //
        // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------1")
        // ;
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
      }
      // 差異
      doubleYear = 0;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
        //
        stringValue = "" + hashtableMoney.get(stringKEY);
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
      //
    }
    // 資料處理 74 年累計-通路代碼
    intRow = 73;
    stringDateStart = stringYearAC + "0101";
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        doubleYear = 0;
        for (int intNoL = 1; intNoL <= exeUtil.doParseInteger(stringMonth); intNoL++) {
          stringDate = datetime.dateAdd(stringDateStart, "m", (intNoL - 1));
          stringDate = exeUtil.getDateConvert(stringDate);
          stringDate = exeUtil.doSubstring(stringDate, 0, 7);
          //
          stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNo] + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------1")
          // ;
          doubleYear += exeUtil.doParseDouble(stringValue);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------累計")
          // ;
        }
        //
        // if(doubleYear == 0) continue ;
        //
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
        vectorData.add(arrayTemp);
      }
      // 差異
      doubleYear = 0;
      stringDateStart = stringYearAC + "0101";
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        for (int intNoL = 1; intNoL <= exeUtil.doParseInteger(stringMonth); intNoL++) {
          stringDate = datetime.dateAdd(stringDateStart, "m", (intNoL - 1));
          stringDate = exeUtil.getDateConvert(stringDate);
          stringDate = exeUtil.doSubstring(stringDate, 0, 7);
          //
          stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------1")
          // ;
          doubleYear += exeUtil.doParseDouble(stringValue);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------累計")
          // ;
        }
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
    }
    // 資料處理 76 案累計-通路代碼
    intRow = 75;
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        stringKEY = stringMode + "%-%004%-%" + arraySSMediaID[intNo];
        stringValue = "" + hashtableMoney.get(stringKEY);
        //
        if (exeUtil.doParseDouble(stringValue) == 0)
          stringValue = "0";
        // if(exeUtil.doParseDouble(stringValue) == 0) continue ;
        arrayMoney[intNo] = stringValue;
        //
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
      }
      // 差異
      doubleYear = 0;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        stringKEY = stringMode + "%-%004%-%" + stringCostID;
        //
        stringValue = "" + hashtableMoney.get(stringKEY);
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
    }
    hashtableDataPosition.put("Table2DATA", (String[][]) vectorData.toArray(new String[0][0]));

    // 資料處理 月-請款代碼
    vectorData = new Vector();
    stringDateStart = stringYearAC + "0101";
    stringDateStart = datetime.dateAdd(stringDateStart, "m", -1);
    stringDateStart = exeUtil.getDateConvert(stringDateStart).replaceAll("/", "");
    //
    arrayMode[1] = "";
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      if ("".equals(stringMode))
        continue;
      vectorData = new Vector();
      intRow = 66;
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        for (int intNoL = 0; intNoL <= exeUtil.doParseInteger(stringMonth); intNoL++) {
          stringDate = datetime.dateAdd(stringDateStart, "m", intNoL);
          stringDate = exeUtil.getDateConvertFullRoc(stringDate);
          stringDate = exeUtil.doSubstring(stringDate, 0, 7);
          //
          stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // System.out.println("1stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")------------------------------------S")
          // ;
          if (exeUtil.doParseDouble(stringValue) == 0)
            continue;
          //
          intRowL++;
          arrayTemp = new String[3];
          arrayTemp[0] = "0";
          arrayTemp[1] = "" + intRowL;
          arrayTemp[2] = stringCostID;
          vectorData.add(arrayTemp);
          arrayTemp = new String[3];
          arrayTemp[0] = "3";
          arrayTemp[1] = "" + intRowL;
          arrayTemp[2] = stringDate;
          vectorData.add(arrayTemp);
          arrayTemp = new String[3];
          arrayTemp[0] = "5";
          arrayTemp[1] = "" + intRowL;
          arrayTemp[2] = stringValue;
          vectorData.add(arrayTemp);
          // System.out.println("1stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")intRow("+intRow+")stringDate("+stringDate+")------------------------------------E")
          // ;
        }
      }
      intRow = 100;
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      // 資料處理 案累計-請款代碼
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        //
        stringKEY = stringMode + "%-%004%-%" + stringCostID;
        stringValue = "" + hashtableMoney.get(stringKEY);
        // System.out.println("2stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")------------------------------------S")
        // ;
        if (exeUtil.doParseDouble(stringValue) == 0)
          continue;
        //
        intRowL++;
        arrayTemp = new String[3];
        arrayTemp[0] = "0";
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringCostID;
        vectorData.add(arrayTemp);
        arrayTemp = new String[3];
        arrayTemp[0] = "5";
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
        // System.out.println("2stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")intRow("+intRow+")------------------------------------S")
        // ;
      }
      hashtableDataPosition.put("OTHER", (String[][]) vectorData.toArray(new String[0][0]));
    }

    // SHEET8 和 SHEET9 資料處理
    arrayMode = new String[] { "A", "B" };
    String[] arraySheet = { "SHEET9", "SHEET8" };

    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      if ("".equals(stringMode))
        continue;
      vectorData = new Vector();
      intCol = 2;
      stringDateStart = stringYearAC + "0101";
      for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
        stringDate = datetime.dateAdd(stringDateStart, "m", (intNo - 1));
        stringDate = exeUtil.getDateConvert(stringDate);
        stringDate = exeUtil.doSubstring(stringDate, 0, 7);
        for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
          stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNoL] + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // 20180508 做轉換 原 直效、已購、未購 ==> 未購、直效、已購
          switch (arrayRow[intNoL]) {
          case 9:
            intRow = 10;
            break;
          case 10:
            intRow = 11;
            break;
          case 11:
            intRow = 9;
            break;
          default:
            intRow = arrayRow[intNoL];
            break;
          }
          //
          if (exeUtil.doParseDouble(stringValue) == 0)
            stringValue = "0";
          //
          // System.out.println("SHEET8------------stringKEY("+stringKEY+")intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
          // ;
          arrayTemp = new String[3];
          arrayTemp[0] = "" + intCol;
          arrayTemp[1] = "" + intRow;
          arrayTemp[2] = stringValue;
          vectorData.add(arrayTemp);
        }
        intCol++;
      }
      hashtableDataPosition.put(arraySheet[intMode], (String[][]) vectorData.toArray(new String[0][0]));
    }

    return hashtableDataPosition;
  }

  // 檢核
  // 前端資料檢核，正確回傳 True
  public boolean isBatchCheckOK() throws Throwable {
    String stringProjectID = getValue("ProjectID1").trim();
    if ("".equals(stringProjectID)) {
      messagebox("[案別] 不可為空白。");
      getcLabel("ProjectID1").requestFocus();
      return false;
    }
    String stringYearAC = getValue("YearAC").trim();
    if ("".equals(stringYearAC)) {
      messagebox("[執行年] 不可為空白。");
      getcLabel("YearAC").requestFocus();
      return false;
    }
    String stringMonth = getValue("Month").trim();
    if ("".equals(stringMonth)) {
      messagebox("[月] 不可為空白。");
      getcLabel("Month").requestFocus();
      return false;
    }
    String dateType = getValue("dateType").trim();
    if ("".equals(dateType)) {
      messagebox("[業績參照] 不可為空白。");
      getcLabel("dateType").requestFocus();
      return false;
    }
    return true;
  }

  // 資料庫 Doc
  public void doCetMoneyTable1(String stringSign, String stringProjectID1, String stringDateStart, String stringDateEnd, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS,
      Hashtable hashtableMoney) throws Throwable {
    String stringSql = "";
    String[][] retData = new String[0][0];
    // 2015-12-01 B3018 781 及 782 輸入日期 2015-12-01後不列入處理
    stringDateStart = exeUtil.getDateConvert(stringDateStart);
    stringDateEnd = exeUtil.getDateConvert(stringDateEnd);
    // 0 採發金額 1 預算金額 2 請款 3 借款沖銷 4 修正金額
    // 5 借款沖銷
    stringSql = " SELECT  SUBSTRING(CONVERT(char(8),UseDate,111),1,7),  SUM(RealMoney) \n" + " FROM  Doc3M014_UseMoney_AC_view \n"
        + " WHERE   ( RTRIM(CostID)+RTRIM(CostID1)  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc2M020 \n" + " WHERE  BudgetID  LIKE  'B%' \n"
        + " AND  RTRIM(CostID)+RTRIM(CostID1) NOT IN ('871',  '900',  '910') " + " AND  ComNo  =  'CS' " + " ) \n" +
        // " OR \n" +
        // " RTRIM(CostID)+RTRIM(CostID1) IN ('269', '729', '899')" +
        " )\n" + " AND  RTRIM(CostID)+RTRIM(CostID1)  NOT  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc7M070 \n" + " WHERE  UseType  =  'A' \n"
        + " AND  MoneyType  =  'X' " + " ) \n" + " AND  InOut  =  'O' \n" + " AND  DepartNo  <>  '0531' \n" + " AND  ProjectID1  =  '" + stringProjectID1 + "' \n";
    if (!"".equals(stringDateStart))
      stringSql += " AND  UseDate  >=  '" + stringDateStart + "' \n";
    if (!"".equals(stringDateEnd))
      stringSql += " AND  UseDate  <=  '" + stringDateEnd + "' \n";
    if (stringSign.startsWith("B")) {
      stringSql += " AND  (RTRIM(CostID)+RTRIM(CostID1) IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc7M070 \n" + " WHERE  UseType = 'A' \n"
          + " AND  MoneyType IN('A',  'C') ) \n" + " OR " + " ISNULL(SSMediaID,'')  <> '' " + ") ";
    }
    stringSql += " GROUP BY SUBSTRING(CONVERT(char(8),UseDate,111),1,7) \n";
    retData = dbDoc.queryFromPool(stringSql);
    doDataTable1(stringSign, retData, exeUtil, hashtableMoney);
    if (dbDocCS != null) {
      // System.out.println("人壽---------------------------------------1") ;
      retData = dbDocCS.queryFromPool(stringSql.replaceAll("AND  ComNo  =  'Z6'", ""));
      doDataTable1(stringSign, retData, exeUtil, hashtableMoney);
    }
    // 2015-12-01 B3018 781 及 782 輸入日期 2015-12-01後改抓請購項目之日期區間
    // Doc3M011 限定 CDate 2015-12-01 後
    // Doc3M012 限定 請款代碼為 781 及 782、查詢年度 大於等於 DateStart 、小於等於 DateEnd
    // Doc3M0123 限定案別
    // 查詢年度 大於 DateStart，起始日期為 年度/01/01 ，起始月份 01
    // 查詢年度 等於 DateStart，起始日期為 DateStart ，起始月份 為 DateStart 所在月份
    // 查詢年度 小於 DateEnd，結束日期為 年度/12/31 ，結束月份 為 12
    // 查詢年度 等於 DateEnd，結束日期為 DateEnd ，結束月份 為 DateEnd 所在月份
    // 計算單一日金額
    // 迴圈 取得每月使用天數，計算 動支金額
    // stringSign+"%-%"+stringYYMM
  }

  public void doDataTable1(String stringSign, String[][] retData, FargloryUtil exeUtil, Hashtable hashtableMoney) throws Throwable {
    String stringYYMM = "";
    String stringMoney = "";
    String stringKEY = "";
    double doubleTemp = 0;
    for (int intNo = 0; intNo < retData.length; intNo++) {
      stringYYMM = retData[intNo][0].trim();
      stringMoney = retData[intNo][1].trim();
      stringKEY = stringSign + "%-%" + stringYYMM;
      //
      if (stringSign.endsWith("002"))
        stringKEY = stringSign;
      //
      doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
      // System.out.println("KEY("+stringKEY+")("+convert.FourToFive(""+doubleTemp,
      // 4)+")------------------------------") ;
      hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
    }
  }

  public void doCetMoneyTable2(String stringSign, String stringProjectID1, String stringDateStart, String stringDateEnd, String stringCostIDMoneyTypeAC,
      String stringCostID1To6Media, String stringCostID1To11Media, String[] arraySSMediaID, String[] arrayCostID, String[][] retSale03R510B, FargloryUtil exeUtil, talk dbDoc,
      talk dbDocCS, Hashtable hashtableMoney, Vector vectorCostID) throws Throwable {
    String stringSql = "";
    String[][] retData = new String[0][0];
    // 2015-12-01 B3018 781 及 782 輸入日期 2015-12-01後不列入處理
    stringDateStart = exeUtil.getDateConvert(stringDateStart);
    stringDateEnd = exeUtil.getDateConvert(stringDateEnd);
    // 通路代號 案別+通路中分類(J03)+西元年月(201605)+分界符號(-)+流水號(002)
    // 0 CostID 1 CostID1 2 SSMediaID 3 UseDate 4 RealMoney
    stringSql = " SELECT  CostID,  CostID1,  SSMediaID,  SUBSTRING(CONVERT(char(8),UseDate,111),1,7),  SUM(RealMoney) \n" + " FROM  Doc3M014_UseMoney_AC_view \n"
        + " WHERE    ( RTRIM(CostID)+RTRIM(CostID1)  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc2M020 \n" + " WHERE  BudgetID  LIKE  'B%' \n"
        + " AND  RTRIM(CostID)+RTRIM(CostID1) NOT IN ('871',  '900',  '910') " + " AND  ComNo  =  'CS' " + " ) \n" +
        // " OR \n" +
        // " RTRIM(CostID)+RTRIM(CostID1) IN ('269', '729', '899')" +
        " )\n" + " AND  RTRIM(CostID)+RTRIM(CostID1)  NOT  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc7M070 \n" + " WHERE  UseType  =  'A' \n"
        + " AND  MoneyType  =  'X' " + " ) \n" + " AND  InOut  =  'O' \n" + " AND  DepartNo  <>  '0531' \n" + " AND  ProjectID1  =  '" + stringProjectID1 + "' \n";
    if (!"".equals(stringDateStart))
      stringSql += " AND  UseDate  >=  '" + stringDateStart + "' \n";
    if (!"".equals(stringDateEnd))
      stringSql += " AND  UseDate  <=  '" + stringDateEnd + "' \n";
    stringSql += " GROUP BY CostID,  CostID1,  SSMediaID,  SUBSTRING(CONVERT(char(8),UseDate,111),1,7) \n";
    retData = dbDoc.queryFromPool(stringSql);
    doData2(stringSign, "A", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil, hashtableMoney,
        vectorCostID);
    System.out.println(stringSign + "全---------------------------------------純S");
    doData2(stringSign, "B", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil, hashtableMoney,
        vectorCostID);
    System.out.println(stringSign + "全---------------------------------------純E");
    if (dbDocCS != null) {
      System.out.println("人壽---------------------------------------4");
      retData = dbDocCS.queryFromPool(stringSql.replaceAll("AND  ComNo  =  'Z6'", ""));
      doData2(stringSign, "A", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil,
          hashtableMoney, vectorCostID);
      doData2(stringSign, "B", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil,
          hashtableMoney, vectorCostID);
    }
    // 2015-12-01 B3018 781 及 782 輸入日期 2015-12-01後改抓請購項目之日期區間
    // Doc3M011 限定 CDate 2015-12-01 後
    // Doc3M012 限定 請款代碼為 781 及 782、查詢年度 大於等於 DateStart 、小於等於 DateEnd
    // Doc3M0123 限定案別
    // 查詢年度 大於 DateStart，起始日期為 年度/01/01 ，起始月份 01
    // 查詢年度 等於 DateStart，起始日期為 DateStart ，起始月份 為 DateStart 所在月份
    // 查詢年度 小於 DateEnd，結束日期為 年度/12/31 ，結束月份 為 12
    // 查詢年度 等於 DateEnd，結束日期為 DateEnd ，結束月份 為 DateEnd 所在月份
    // 計算單一日金額
    // 迴圈 取得每月使用天數，計算 動支金額
    // stringSign+"%-%"+arraySSMediaIDL[intNoL]+"%-%"+stringYYMM ;
  }

  public void doData2(String stringSign, String stringCostIDMoneyMode, String stringCostIDMoneyTypeAC, String stringCostID1To6Media, String stringCostID1To11Media,
      String[][] retData, String[] arraySSMediaID, String[] arrayCostID, String[][] retSale03R510B, FargloryUtil exeUtil, Hashtable hashtableMoney, Vector vectorCostID)
      throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringYYMM = "";
    String stringMoney = "";
    String stringSSMediaID = "";
    String stringKEY = "";
    String stringCostIDAvgMoney = ""; // 2015-08-27 改成依業績 ",730,820,231,233,261,262,269,490,491,492,831," ;
    String stringItem = "";
    String[] arraySSMediaIDL = null;
    String[] arraySSMediaIDMoneyL = null;
    String[] arrayRatio = new String[arraySSMediaID.length];
    for (int intNo = 0; intNo < arrayRatio.length; intNo++)
      arrayRatio[intNo] = "1";
    String[] arrayRatio1To6Media = new String[6];
    for (int intNo = 0; intNo < arrayRatio1To6Media.length; intNo++)
      arrayRatio1To6Media[intNo] = "0";
    String[] arrayRatio1To11Media = new String[arraySSMediaID.length];
    for (int intNo = 0; intNo < arrayRatio1To11Media.length; intNo++)
      arrayRatio1To11Media[intNo] = "0";
    double doubleTemp = 0;
    boolean booleanNeg = false;
    boolean booleanFlag = false;
    //
    if (retSale03R510B.length == 0) {
      for (int intNoL = 1; intNoL <= 6; intNoL++) {
        arrayRatio1To6Media[intNoL - 1] = "1";
      }
      for (int intNoL = 1; intNoL <= 11; intNoL++) {
        arrayRatio1To11Media[intNoL - 1] = "1";
      }
    }
    for (int intNo = 0; intNo < retSale03R510B.length; intNo++) {
      stringItem = retSale03R510B[intNo][1].trim();
      if ("B.業績.案累計".equals(stringItem)) {
        booleanFlag = false;
        for (int intNoL = 1; intNoL <= 6; intNoL++) {
          arrayRatio1To6Media[intNoL - 1] = retSale03R510B[intNo][intNoL + 1];
          // System.out.println(intNoL+stringItem+"("+retSale03R510B[intNo][intNoL+1]+")---------------------------------------------------1")
          // ;
          if (exeUtil.doParseDouble(arrayRatio1To6Media[intNoL - 1]) > 0)
            booleanFlag = true;
        }
        if (!booleanFlag) {
          for (int intNoL = 1; intNoL <= 6; intNoL++) {
            arrayRatio1To6Media[intNoL - 1] = "1";
          }
        }
        //
        booleanFlag = false;
        for (int intNoL = 1; intNoL <= 11; intNoL++) {
          arrayRatio1To11Media[intNoL - 1] = retSale03R510B[intNo][intNoL + 1];
          // System.out.println(intNoL+stringItem+"("+retSale03R510B[intNo][intNoL+1]+")---------------------------------------------------2")
          // ;
          if (exeUtil.doParseDouble(arrayRatio1To11Media[intNoL - 1]) > 0)
            booleanFlag = true;
        }
        if (!booleanFlag) {
          for (int intNoL = 1; intNoL <= 11; intNoL++) {
            arrayRatio1To11Media[intNoL - 1] = "1";
          }
        }
      }
    }
    // 通路代號 案別+通路中分類(J03)+西元年月(201605)+分界符號(-)+流水號(002)
    // 0 CostID 1 CostID1 2 SSMediaID 3 UseDate 4 RealMoney
    double doubleTempSum = 0;
    boolean booleanFlagL = true;
    for (int intNo = 0; intNo < retData.length; intNo++) {
      stringCostID = retData[intNo][0].trim();
      stringCostID1 = retData[intNo][1].trim();
      stringSSMediaID = retData[intNo][2].trim();
      stringYYMM = retData[intNo][3].trim();
      stringMoney = retData[intNo][4].trim();
      // doubleTempSum = 0 ;
      //
      if (exeUtil.doParseDouble(stringMoney) == 0)
        continue;
      //
      if ("B".equals(stringCostIDMoneyMode)) {
        System.out.println(intNo + "stringCostIDMoneyTypeAC(" + stringCostIDMoneyTypeAC + ")stringCostIDMoneyMode(" + stringCostIDMoneyMode + ")SSMediaID(" + stringSSMediaID
            + ")KEY(" + stringCostID + stringCostID1 + "%-%" + stringYYMM + ")(" + convert.FourToFive(stringMoney, 4) + ")------------------------------0");
      }
      if ("".equals(stringSSMediaID)) {
        if ("B".equals(stringCostIDMoneyMode) && stringCostIDMoneyTypeAC.indexOf(stringCostID + stringCostID1) == -1)
          continue;
        // booleanFlagL = false ;
      } else {
        if (stringSSMediaID.length() < 9) {
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, 0, 1);
        } else {
          stringSSMediaID = convert.StringToken(stringSSMediaID, "-")[0];
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, stringSSMediaID.length() - 9, stringSSMediaID.length() - 8);
        }
        if (",A,H,F,G,J,B,I,D,L,K,C,".indexOf(stringSSMediaID) == -1) {
          System.out.println(intNo + "不包含 11 大分類中------------------------------");
        }
        // System.out.println(intNo+"KEY("+stringCostID+stringCostID1+"%-%"+stringYYMM+")("+convert.FourToFive(stringMoney,
        // 4)+")------------------------------1") ;
        // booleanFlagL = true ;
      }
      // if("B".equals(stringCostIDMoneyMode))System.out.println(intNo+"SSMediaID("+stringSSMediaID+")KEY("+stringCostID+stringCostID1+"%-%"+stringYYMM+")("+convert.FourToFive(stringMoney,
      // 4)+")------------------------------1") ;
      //
      if ("".equals(stringSSMediaID)) {
        for (int intNoL = 0; intNoL < arrayCostID.length; intNoL++) {
          if (arrayCostID[intNoL].indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
            stringSSMediaID = arraySSMediaID[intNoL];
            break;
          }
        }
      }
      booleanNeg = false;
      if ("".equals(stringSSMediaID)) {
        System.out.println(intNo + " 分攤------------------------------2");
        if (stringCostID1To11Media.indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
          // 依業績平均拆分金額至 1-11 通路
          if (exeUtil.doParseDouble(stringMoney) < 0) {
            booleanNeg = true;
            stringMoney = convert.FourToFive("" + exeUtil.doParseDouble(stringMoney) * -1, 0);
          }
          arraySSMediaIDL = exeUtil.doCopyArray(arraySSMediaID);
          System.out.println(intNo + " 依業績平均拆分金額至 1-11 通路(" + stringMoney + ")------------------------------2");
          arraySSMediaIDMoneyL = exeUtil.getMoneyFromRatio(stringMoney, arrayRatio1To11Media);
        } else if (stringCostID1To6Media.indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
          // 依業績平均拆分金額至 1-6 通路
          if (exeUtil.doParseDouble(stringMoney) < 0) {
            booleanNeg = true;
            stringMoney = convert.FourToFive("" + exeUtil.doParseDouble(stringMoney) * -1, 0);
          }
          arraySSMediaIDL = new String[6];
          for (int intNoL = 0; intNoL < arraySSMediaIDL.length; intNoL++) {
            arraySSMediaIDL[intNoL] = arraySSMediaID[intNoL];
          }
          System.out.println(intNo + " 依業績平均拆分金額至 1-6 通路(" + stringMoney + ")------------------------------2");
          arraySSMediaIDMoneyL = exeUtil.getMoneyFromRatio(stringMoney, arrayRatio1To6Media);
        } else if (stringCostIDAvgMoney.indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
          // 平均拆分金額至 11 通路
          if (exeUtil.doParseDouble(stringMoney) < 0) {
            booleanNeg = true;
            stringMoney = convert.FourToFive("" + exeUtil.doParseDouble(stringMoney) * -1, 0);
          }
          System.out.println(intNo + " 平均拆分金額至 11 通路2(" + stringMoney + ")------------------------------2");
          arraySSMediaIDL = exeUtil.doCopyArray(arraySSMediaID);
          arraySSMediaIDMoneyL = exeUtil.getMoneyFromRatio(stringMoney, arrayRatio);
        } else {
          System.out.println(intNo + " 其他------------------------------2");
          stringSSMediaID = stringCostID + stringCostID1;
          if (vectorCostID.indexOf(stringSSMediaID) == -1)
            vectorCostID.add(stringSSMediaID);
          //
          arraySSMediaIDL = new String[1];
          arraySSMediaIDL[0] = stringSSMediaID;
          arraySSMediaIDMoneyL = new String[1];
          arraySSMediaIDMoneyL[0] = stringMoney;
        }
      } else {
        System.out.println(intNo + " 直接歸屬------------------------------2");
        arraySSMediaIDL = new String[1];
        arraySSMediaIDL[0] = stringSSMediaID;
        arraySSMediaIDMoneyL = new String[1];
        arraySSMediaIDMoneyL[0] = stringMoney;
      }
      //
      for (int intNoL = 0; intNoL < arraySSMediaIDL.length; intNoL++) {
        stringMoney = arraySSMediaIDMoneyL[intNoL];
        if (stringSign.endsWith("003")) {
          stringKEY = stringCostIDMoneyMode + "%-%" + stringSign + "%-%" + arraySSMediaIDL[intNoL] + "%-%" + stringYYMM;
        } else {
          stringKEY = stringCostIDMoneyMode + "%-%" + stringSign + "%-%" + arraySSMediaIDL[intNoL];
        }
        //
        if (booleanNeg) {
          doubleTemp = exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY)) - exeUtil.doParseDouble(stringMoney) / 10000;
        } else {
          doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
        }
        if (stringYYMM.startsWith("2016")) {
          doubleTempSum += exeUtil.doParseDouble(stringMoney);
          // System.out.println(intNo+"("+stringKEY+")("+convert.FourToFive(stringMoney,
          // 0)+")("+convert.FourToFive(""+doubleTempSum,
          // 0)+")------------------------------3") ;
        }
        // if("B".equals(stringCostIDMoneyMode))System.out.println(intNo+"CostID("+stringCostID+")CostID1("+stringCostID1+")
        // KEY("+stringKEY+")("+convert.FourToFive(""+doubleTemp,
        // 4)+")("+convert.FourToFive(""+doubleTempSum,
        // 4)+")------------------------------3") ;
        // if("H".equals(arraySSMediaIDL[intNoL]) && "B".equals(stringCostIDMoneyMode))
        // {
        System.out.println("(" + intNo + "--" + intNoL + ")CostID(" + stringCostID + ")CostID1(" + stringCostID1 + ") stringYYMM(" + stringYYMM + ")KEY(" + stringKEY
            + ")THISMoney(" + stringMoney + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")------------------------------3");
        // }
        hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
        //
      }
    }
  }

  public String getInformation() {
    return "---------------ButtonExcel(ButtonExcel).defaultValue()----------------";
  }
}
