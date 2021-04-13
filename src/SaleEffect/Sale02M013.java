package SaleEffect;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.util.FargloryUtil;

public class Sale02M013 extends bproc {
  talk dbSale = getTalk((String) get("put_dbSale"));// SQL2000

  public String getDefaultValue(String value) throws Throwable {
    getButton("ButtonSetSaleID").doClick(); // 2015-10-15 B3018
    //
    FargloryUtil exeUtil = new FargloryUtil();
    String[][] retASale1 = null;
    if ("聯結戶別".equals(value.trim())) {
      if (!isBatchCheckOK(exeUtil))
        return value;
      getButton("ButtonSSMediaID").doClick();
      doInsertSale1030(exeUtil);

      put("SaleM1031_Default_ProjectID1", getValue("PC_Default_ProjectID1").trim());
      // 車戶聯結類別 SaleM1031.Show 1
      showForm("車位聯結戶別(Sale02M0131)");
      action(9);
      return value;
    }
    if ("查詢退戶".equals(value.trim())) {
      retASale1 = getASale1();
      if (retASale1.length == 0) {
        message("無退戶資料");
      } else {
        // 退戶資料
        showForm("查詢退戶(SaleM02M0132)");
        action(9);
        setTableData("Table1", retASale1);
      }
    } else if ("修改[Alt+S]".equals(value.trim())) {
      if (!isBatchCheckOK(exeUtil))
        return value;
      getButton("ButtonSSMediaID").doClick();
      doInsertSale1030(exeUtil);
      doChangeButtonSale02M0120();
    }
    return value;
  }

  public void doChangeButtonSale02M0120() throws Throwable {
    //
    int intColorType = 5; // 藍(0,0,255)
    String stringButton = getValue("PC_OperatorPosition").trim();
    String stringFloor = getValue("PC_OperatorIndex").trim();
    String stringHouseCar = getValue("PC_HouseCar").trim();
    String stringText1_11 = getValue("Text1_11").trim();
    String stringText1_12 = getValue("Text1_12").trim();
    String stringText1_13 = getValue("Text1_13").trim();
    String stringText1_14 = getValue("Text1_14").trim();
    String stringCheck1_0 = getValue("Check1_0").trim();
    String stringCheck1_1 = getValue("Check1_1").trim();
    String stringSidbEdit2_8 = getValue("SidbEdit2_8").trim();
    String stringSidbEdit2_4 = getValue("SidbEdit2_4").trim();
    String stringSidbEdit2_0 = getValue("SidbEdit2_0").trim();
    String stringButtonText = "";
    String stringPosition = "";
    JButton jbuttonPosition = null;
    // 棟樓別
    stringPosition = getValue("Text1_11").trim();
    if ("".equals(stringPosition))
      stringPosition = getValue("Text1_12").trim();
    if ("".equals(stringPosition))
      stringPosition = getValue("Text1_13").trim();
    if ("".equals(stringPosition))
      stringPosition = getValue("Text1_14").trim();
    //
    if ("Position".equals(stringHouseCar) && !"".equals(stringText1_11)) {
      stringButtonText = stringText1_11;
    } else if ("Position".equals(stringHouseCar) && !"".equals(stringText1_12)) {
      stringButtonText = stringText1_12;
    } else if ("Car".equals(stringHouseCar) && !"".equals(stringText1_13)) {
      stringButtonText = stringText1_13;
    } else if ("Car".equals(stringHouseCar) && !"".equals(stringText1_14)) {
      stringButtonText = stringText1_14;
    }
    if ("Y".equals(stringCheck1_0)) {
      intColorType = 0; // 黑(0,0,0)
    } else if ("Y".equals(stringCheck1_1)) {
      intColorType = 1; // 黃(255,255,0)
    } else if (check.isACDay(convert.replace(stringSidbEdit2_8, "/", ""))) {
      intColorType = 2; // 紫(255,0,255)
    } else if (check.isACDay(convert.replace(stringSidbEdit2_4, "/", ""))) {
      intColorType = 3; // 橙(255,128,0)
    } else if (check.isACDay(convert.replace(stringSidbEdit2_0, "/", ""))) {
      intColorType = 4; // 綠(0,255,0)
    }
    getInternalFrame("銷況-戶別明細(Sale02M013)").setVisible(false);

    put("Sale02M013_TEXT", stringButtonText);
    put("Sale02M013_ColorType", "" + intColorType);
    put("Sale02M013_OperatorPosition", stringButton);
    put("Sale02M013_OperatorIndex", stringFloor);
    showForm("銷況-戶別(Sale02M012)");
    action(9);
    setValue("ProjectID", "" + get("Sale02M010_ProjectID"));
    setValue("HouseCar", "" + get("Sale02M12_HouseCar"));
    setValue("DomNo", "" + get("Sale02M011_DomNo"));
    setValue("DomName", "" + get("Sale02M011_DomName"));
    setValue("ShowPosition", stringPosition);
    getButton("FormLoad2").doClick();
    int intPostion = Integer.parseInt(("" + get("Sale02M12_TabbedPanePos")).trim());
    getTabbedPane("Tab1").setSelectedIndex(intPostion);
    System.out.println(stringButton + "_" + stringFloor);
    getButton("button" + stringButton + "_" + stringFloor).requestFocus();
  }

  public void doInsertSale1030(FargloryUtil exeUtil) throws Throwable {
    //
    String stringSql = "";
    String stringProjectID1 = getValue("PC_Default_ProjectID1").trim(); /* PC_Default_ProjectID1$ 初始值提供 */
    String stringOperatorPosition = getValue("PC_OperatorPosition").trim(); /* PC_OperatorPosition$ 由戶別 Sale1020 */
    String stringOperatorIndex = getValue("PC_OperatorIndex").trim(); /* PC_OperatorIndex$ 由戶別 Sale1020 */
    String stringHouseCar = getValue("PC_HouseCar").trim(); // PC_HouseCar$
    String stringOperatorDom = getValue("PC_OperatorDom").trim(); /* PC_OperatorDom$由戶別 Sale1020 */
    String stringSidtEdit3_0 = getValue("SidtEdit3_0").trim();
    if (stringSidtEdit3_0.equals("1900/01/01"))
      stringSidtEdit3_0 = "";
    String stringTableName = !check.isACDay(convert.replace(stringSidtEdit3_0, "/", "")) ? "A_Sale " : "A_Sale1 "; // 有問題
    // String stringMaxID1 = getMaxID1(stringTableName) ;
    double doubleNRate = "Z".equals(stringProjectID1) ? 0.00275 : 1;
    Vector vectorSql = new Vector();
    // System.out.println("stringMaxID1--------------"+stringMaxID1) ;
    /*
     * doDeleteASale(stringProjectID1, stringOperatorPosition, stringOperatorIndex,
     * stringHouseCar, stringOperatorDom, dbSale) ; doInsertData1(stringTableName,
     * stringMaxID1, doubleNRate) ;// DB 之 ID1 無法自動取值，須自動取得最大 ID1+1
     * doDeleteASale(stringProjectID1, stringOperatorPosition, stringOperatorIndex,
     * stringHouseCar, stringOperatorDom, dbMISPROSale) ;
     * doInsertData(stringTableName, doubleNRate, dbSale) ;// DB 之 ID1 可自動取值。
     */

    // doInsertData2(stringTableName, doubleNRate, stringProjectID1,
    // stringOperatorPosition, stringOperatorIndex,
    // stringHouseCar, stringOperatorDom) ;// 先存入 MISPRO_Sale 再同步 SQL 2k
    doInsertData3(stringTableName.trim(), doubleNRate, stringProjectID1, stringOperatorPosition, stringOperatorIndex, stringHouseCar, stringOperatorDom, exeUtil);
  }

  public void doInsertData3(String stringTableName, double doubleNRate, String stringProjectID1, String stringOperatorPosition, String stringOperatorIndex, String stringHouseCar,
      String stringOperatorDom, FargloryUtil exeUtil) throws Throwable {
    String stringSql = "";
    String stringID1 = getID1_ASale(stringProjectID1, stringOperatorPosition, stringOperatorIndex, stringHouseCar, stringOperatorDom, "A_Sale", dbSale);
    String[] arrayDBField = { "OperatorPosition", "OperatorIndex", "HouseCar", "OperatorDom", "YearMM", // 00-04
        "Com", "Depart", "ProjectID", "ProjectID0", "ProjectID1", // 05-09
        "H_Com", "H_LandOwner", "H_LandShare", "L_Com", "L_LandOwner", // 10-14
        "L_LandShare", "LandOwner", "LandShare", "Position", "PositionRent", // 15-19
        "Car", "CarRent", "Custom", "OrderDate", "OrderMon", // 20-24
        "H_OrderMon", "L_OrderMon", "EnougDate", "EnougMon", "H_EnougMon", // 25-29
        "L_EnougMon", "ContrDate", "ContrMon", "H_ContrMon", "L_ContrMon", // 30-34
        "Deldate", "PingSu", "DealDiscount", "BonusDiscount", "PreMoney", // 35-39
        "H_PreMoney", "L_PreMoney", "DealMoney", "H_DealMoney", "L_DealMoney", // 40-44
        "GiftMoney", "H_GiftMoney", "L_GiftMoney", "CommMoney", "H_CommMoney", // 45-49
        "L_CommMoney", "PureMoney", "H_PureMoney", "L_PureMoney", "LastMoney", // 50-54
        "H_LastMoney", "L_LastMoney", "BalaMoney", "H_BalaMoney", "L_BalaMoney", // 55-59
        "SaleID1", "SaleName1", "SaleID2", "SaleName2", "SaleID3", // 60-64
        "SaleName3", "SaleID4", "SaleName4", "SaleID5", "SaleName5", // 65-69
        "SaleID6", "SaleName6", "SaleID7", "SaleName7", "SaleID8", // 70-74
        "SaleName8", "SaleID9", "SaleName9", "SaleID10", "SaleName10", // 75-79
        "SaleGroup", "MediaID", "MediaName", "ZoneID", "ZoneName", // 80-84
        "MajorID", "MajorName", "UseType", "Remark", "DateRange", // 85-89
        "DateCheck", "DateFile", "DateBonus", "RentRange", "PingRentPrice", // 86-90
        "PingRent", "PingRentLast", "RentPrice", "Rent", "RentLast", // 91-94
        "Guranteer", "RentFree", "Position1", "PositionRent1", "Custom1", // 95-99
        "ViMoney", "H_ViMoney", "L_ViMoney", "AO_sn", "Plan1", // 100-104
        "ComNo", "OrderNo", "SSMediaID", "SSMediaID1", // 105-108
        "CommMoney1", "H_CommMoney1", "L_CommMoney1" }; // 109-111
    // 欄位為空白，表示不同步資料
    String[] arrayFieldName = { "PC_OperatorPosition", "PC_OperatorIndex", "PC_HouseCar", "PC_OperatorDom", "SidbEdit2_0", // 00-04
        "Text1_0", "PC_Default_Depart", "Text1_1", "Text1_2", "Text1_3", // 05-09
        "Text1_5", "Check1_6", "Check1_7", "Text1_8", "Check1_9", // 10-14
        "Check1_10", "Check1_0", "Check1_1", "Text1_11", "Text1_12", // 15-19
        "Text1_13", "Text1_14", "Text1_4", "SidbEdit2_0", "SidbEdit2_1", // 20-24
        "SidbEdit2_2", "SidbEdit2_3", "SidbEdit2_4", "SidbEdit2_5", "SidbEdit2_6", // 25-29
        "SidbEdit2_7", "SidbEdit2_8", "SidbEdit2_9", "SidbEdit2_10", "SidbEdit2_11", // 30-34
        "SidtEdit3_0", "SidtEdit3_1", "SidtEdit3_2", "SidtEdit3_3", "SidbEdit4_0", // 35-39
        "SidbEdit4_1", "SidbEdit4_2", "SidbEdit4_3", "SidbEdit4_4", "SidbEdit4_5", // 40-44
        "SidbEdit4_6", "SidbEdit4_7", "SidbEdit4_8", "SidbEdit4_9", "SidbEdit4_10", // 45-49
        "SidbEdit4_11", "SidbEdit4_12", "SidbEdit4_13", "SidbEdit4_14", "SidbEdit4_15", // 50-54
        "SidbEdit4_16", "SidbEdit4_17", "SidbEdit4_18", "SidbEdit4_19", "SidbEdit4_20", // 55-59
        "CSComboBox5_0", "Text5_1", "CSComboBox5_2", "Text5_3", "CSComboBox5_4", // 60-64
        "Text5_5", "CSComboBox5_6", "Text5_7", "CSComboBox5_8", "Text5_9", // 65-69
        "CSComboBox5_10", "Text5_11", "CSComboBox5_12", "Text5_13", "CSComboBox5_14", // 70-74
        "Text5_15", "CSComboBox5_16", "Text5_17", "CSComboBox5_18", "Text5_19", // 75-79
        "", "CSComboBox7_0", "Text7_1", "CSComboBox7_4", "Text7_5", // 80-84
        "CSComboBox7_2", "Text7_3", "Text7_6", "Text7_7", "SidtEdit6_0", // 85-89
        "SidtEdit6_1", "SidtEdit6_2", "SidtEdit6_3", "SidtEdit6_4", "SidbEdit8_0", // 86-90
        "SidbEdit8_1", "SidbEdit8_2", "SidbEdit9_0", "SidbEdit9_1", "SidbEdit9_2", // 91-94
        "SidbEdit10_0", "SidbEdit10_1", "SESSION%PC_SaleM1031_Text1_0", "SESSION%PC_SaleM1031_Text1_1", "SESSION%PC_SaleM1031_Text1_2", // 95-99
        "SidbEdit4_21", "SidbEdit4_22", "SidbEdit4_23", "", "Text40", // 100-104
        "Text7_0", "", "SSMediaID", "SSMediaID1", // 105-108
        "SidbEdit4_24", "SidbEdit4_25", "SidbEdit4_26" }; // 109-111 2015-10-12
    String stringDBField = "";
    String stringFieldName = "";
    String stringFieldValue = "";
    String[] arrayTemp = null;
    Hashtable hashtableDBField = new Hashtable();
    Vector vectorSql = new Vector();
    //
    // JTable jtable = getTable("Table1") ;
    for (int intNo = 0; intNo < arrayFieldName.length; intNo++) {
      stringDBField = arrayDBField[intNo].trim();
      stringFieldName = arrayFieldName[intNo].trim();
      if ("".equals(stringFieldName))
        continue;
      //
      if (stringFieldName.startsWith("SESSION")) {
        stringFieldValue = ("" + get(stringFieldName.substring(8)));
      } else if (stringFieldName.toUpperCase().startsWith("TABLE")) {
        // arrayTemp = convert.StringToken(stringFieldName, "%") ;
        // stringFieldValue =(""+getValueAt(arrayTemp[0], doParseInteger(arrayTemp[1]),
        // arrayTemp[2])).trim() ;
      } else {
        stringFieldValue = getValue(stringFieldName).trim();
      }
      if ("OperatorPosition".equals(stringDBField)) {
        stringFieldValue = "SSCommand" + stringFieldValue;
      } else if (("OrderMon,          H_OrderMon,  L_OrderMon,    EnougMon,       H_EnougMon," + "L_EnougMon,     ContrMon,      H_ContrMon,     L_ContrMon,    PreMoney,"
          + "H_PreMoney,      L_PreMoney,  DealMoney,       H_DealMoney,  L_DealMoney," + "GiftMoney,          H_GiftMoney,  L_GiftMoney,    CommMoney,    H_CommMoney,"
          + "L_CommMoney,  PureMoney,    H_PureMoney,  L_PureMoney,  LastMoney," + "H_LastMoney,     L_LastMoney, BalaMoney,       H_BalaMoney,  L_BalaMoney,"
          + "PingRentPrice,   PingRent,        PingRentLast,   RentPrice,         Rent," + "RentLast,             Guranteer,      ViMoney,           H_ViMoney,       L_ViMoney,"
          + "CommMoney1,     H_CommMoney1,  L_CommMoney1,").indexOf(stringDBField + ",") != -1) { // 2015-10-12 B3018
        stringFieldValue = "" + (doParseDouble(stringFieldValue) * doubleNRate);
      } else if ("PingSu,DealDiscount,BonusDiscount,RentRange,RentFree,".indexOf(stringDBField) != -1) {
        stringFieldValue = "" + (doParseDouble(stringFieldValue));
      }
      //
      // System.out.println("["+stringFieldName+"]--------------["+stringFieldValue+"]")
      // ;
      hashtableDBField.put(stringDBField, stringFieldValue);
    }
    // System.out.println("getColumnsFromPool--------------S["+stringTableName+"]")
    // ;
    String[][] retTableHead = dbSale.getColumnsFromPool(stringTableName);
    // System.out.println("getColumnsFromPool--------------E") ;
    if ("".equals(stringID1)) {
      // 新增
      stringSql = getInsertASale(stringID1, hashtableDBField, retTableHead);

      // System.out.println("SQL--------------\n"+stringSql) ;
      dbSale.execFromPool(stringSql);
      // vectorSql.add(stringSql) ;
    } else {
      // 修改
      stringSql = getUpdateASale(stringID1, hashtableDBField, retTableHead);
      // System.out.println("SQL--------------\n"+stringSql) ;
      dbSale.execFromPool(stringSql);
      // vectorSql.add(stringSql) ;
    }
    //
    String stringID1New = "";
    // 退戶時
    String stringSidtEdit3_0 = getValue("SidtEdit3_0").trim();
    String stringTable = "A_Sale_SaleID";
    String stringID11 = "";
    boolean booleanDelete = check.isACDay(convert.replace(stringSidtEdit3_0, "/", ""));
    stringID1 = getID1_ASale(stringProjectID1, stringOperatorPosition, stringOperatorIndex, stringHouseCar, stringOperatorDom, "A_Sale", dbSale);
    if (booleanDelete) {
      // 新增至 A_Sale1
      stringSql = getInsertASale1(stringID1, retTableHead, dbSale);
      dbSale.execFromPool(stringSql);
      //
      stringID11 = getID1_ASale1(stringProjectID1, stringOperatorPosition, stringOperatorIndex, stringHouseCar, stringOperatorDom, "A_Sale1", dbSale);
      // vectorSql.add(stringSql) ;
      // System.out.println("SQL--------------\n"+stringSql) ;
      // 更新 A_Sale
      stringSql = doUpdateInitASale(stringID1, dbSale);
      dbSale.execFromPool(stringSql);
      // vectorSql.add(stringSql) ;
      //
      stringTable = "A_Sale1_SaleID";
    } else {
      // stringID1New = stringID1 ; //getID1_ASale(stringProjectID1,
      // stringOperatorPosition, stringOperatorIndex, stringHouseCar,
      // stringOperatorDom, "A_Sale", dbSale) ;
    }
    // 售出人尚未改成表格 不執行
    // getInsertASaleMan(stringID1, stringTable, stringID1New, dbSale) ;
    doInsertASaleSaleIDSql(stringID1, stringID11, stringTable, exeUtil, dbSale, vectorSql);
    //
    // dbSale.execFromPool((String[]) vectorSql.toArray(new String[0])) ;
  }

  public String getInsertASale(String stringID1, Hashtable hashtableDBField, String[][] retTableHead) throws Throwable {
    int intFlow = 1;
    String stringTableName = "A_Sale";
    String stringSql = "";
    String stringSqlList = "";
    String stringSqlSetData = "";
    String stringDBField = "";
    String stringDBType = "";
    String stringFieldValue = "";
    for (int intNo = 0; intNo < retTableHead.length; intNo++) {
      stringDBField = retTableHead[intNo][0].trim();
      stringDBType = retTableHead[intNo][2].trim().toUpperCase();
      stringFieldValue = "" + hashtableDBField.get(stringDBField);
      //
      if ("ID1".equals(stringDBField))
        continue;
      if ("null".equals(stringFieldValue))
        continue;
      //
      if (!"".equals(stringSqlList))
        stringSqlList += ",";
      if (!"".equals(stringSqlSetData))
        stringSqlSetData += ",";
      stringSqlList += " " + stringDBField + " ";
      //
      if (stringDBType.indexOf("CHAR") != -1 || "DATETIME,".indexOf(stringDBType + ",") != -1) {
        if (stringDBType.equals("NVARCHAR")) {
          stringSqlSetData += "  N'" + stringFieldValue + "'  ";
        } else {
          stringSqlSetData += "  '" + stringFieldValue + "'  ";
        }
      } else {
        stringSqlSetData += "   " + stringFieldValue + "  ";
      }
      intFlow++;
      if (intFlow % 5 == 0)
        stringSqlSetData += "\n";
    }
    stringSql = "INSERT  INTO  " + stringTableName + "   (" + stringSqlList + ") " + " VALUES  (" + stringSqlSetData + ") ";
    return stringSql;
  }

  public String getUpdateASale(String stringID1, Hashtable hashtableDBField, String[][] retTableHead) throws Throwable {
    int intFlow = 1;
    String stringTableName = "A_Sale";
    String stringSql = "";
    String stringSqlSet = "";
    String stringDBField = "";
    String stringDBType = "";
    String stringFieldValue = "";
    for (int intNo = 0; intNo < retTableHead.length; intNo++) {
      stringDBField = retTableHead[intNo][0].trim();
      stringDBType = retTableHead[intNo][2].trim().toUpperCase();
      stringFieldValue = "" + hashtableDBField.get(stringDBField);
      //
      if ("null".equals(stringFieldValue))
        continue;
      //
      if (!"".equals(stringSqlSet))
        stringSqlSet += ",";
      if (stringDBType.indexOf("CHAR") != -1 || "DATETIME,".indexOf(stringDBType + ",") != -1) {
        if (stringDBType.equals("NVARCHAR")) {
          stringSqlSet += stringDBField + "  =  N'" + stringFieldValue + "' -- " + intFlow + "\n";
        } else {
          stringSqlSet += stringDBField + "  =  '" + stringFieldValue + "' -- " + intFlow + "\n";
        }
      } else {
        stringSqlSet += stringDBField + " =  " + stringFieldValue + "  -- " + intFlow + "\n";
      }
      intFlow++;
    }
    stringSql = " UPDATE  " + stringTableName + "  SET  \n" + stringSqlSet + " \n WHERE  ID1  =  " + stringID1 + " ";
    return stringSql;
  }

  public void doInsertASaleSaleIDSql(String stringID1, String stringID11, String stringTableName, FargloryUtil exeUtil, talk dbSale, Vector vectorSql) throws Throwable {
    vectorSql = new Vector();
    JTable jtable1 = getTable("Table1");
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableData = new Hashtable();
    Vector vectorASaleSaleID = null;
    String stringFieldName = "";
    String stringValue = "";
    String stringSql = "";
    String stringOrderNo = exeUtil.getNameUnion("OrderNo", stringTableName, " AND  ID1  =  " + stringID1 + " ", new Hashtable(), dbSale);
    String[] arrayTableField = { "ID1", "ProjectID1", "HouseCar", "Position", "Car", "OrderNo", "RecordNo", "SaleID1", "SaleName1", "DirectorID1", "DirectorName1", "Z6SaleID2",
        "Z6SaleName2", "Z6DirectorID2", "Z6DirectorName2", "CSSaleID2", "CSSaleName2", "CSDirectorID2", "CSDirectorName2" };
    // 刪除
    stringSql = exeUtil.doDeleteDB("A_Sale_SaleID", new Hashtable(), " AND  ID1  =  " + stringID1 + " ", false, dbSale);
    vectorSql.add(stringSql);
    // stringSql = exeUtil.doDeleteDB("A_Sale1_SaleID", new Hashtable(), " AND ID1 =
    // "+stringID1+" ", false, dbSale) ; vectorSql.add(stringSql) ;
    //
    if (!"".equals(stringID11))
      stringID1 = stringID11;
    for (int intNo = 0; intNo < jtable1.getRowCount(); intNo++) {
      hashtableData = new Hashtable();
      for (int intNoL = 0; intNoL < arrayTableField.length; intNoL++) {
        stringFieldName = arrayTableField[intNoL];
        if ("ID1".equals(stringFieldName)) {
          stringValue = stringID1;
        } else if ("ProjectID1".equals(stringFieldName)) {
          stringValue = getValue("Text1_3").trim();
        } else if ("HouseCar".equals(stringFieldName)) {
          stringValue = getValue("PC_HouseCar").trim();
        } else if ("Position".equals(stringFieldName)) {
          stringValue = getValue("Text1_11").trim();
        } else if ("Car".equals(stringFieldName)) {
          stringValue = getValue("Text1_13").trim();
        } else if ("OrderNo".equals(stringFieldName)) {
          stringValue = stringOrderNo;
        } else {
          stringValue = ("" + getValueAt("Table1", intNo, stringFieldName)).trim();
        }
        hashtableData.put(stringFieldName, stringValue);
      }
      stringSql = exeUtil.doInsertDB(stringTableName, hashtableData, false, dbSale);
      vectorSql.add(stringSql);
    }
    dbSale.execFromPool((String[]) vectorSql.toArray(new String[0]));
  }

  public String getInsertASale1(String stringID1, String[][] retTableHead, talk dbSale) throws Throwable {
    int intFlow = 0;
    String stringSql = "";
    String stringSqlList = "";
    String stringSqlSetData = "";
    String stringDBField = "";
    String stringDBType = "";
    String stringFieldValue = "";
    String[][] retASale = null;
    for (int intNo = 0; intNo < retTableHead.length; intNo++) {
      stringDBField = retTableHead[intNo][0].trim();
      //
      if ("ID1".equals(stringDBField))
        continue;
      //
      if (!"".equals(stringSqlList))
        stringSqlList += ",";
      if (intNo != 0 && intFlow % 5 == 0)
        stringSqlList += "\n";
      stringSqlList += stringDBField;
      intFlow++;
    }
    intFlow = 0;
    //
    stringSql = "SELECT " + stringSqlList + " FROM  A_Sale  WHERE  ID1  =  " + stringID1 + " ";
    retASale = dbSale.queryFromPool(stringSql);
    //
    for (int intNo = 0; intNo < retTableHead.length; intNo++) {
      stringDBField = retTableHead[intNo][0].trim();
      stringDBType = retTableHead[intNo][2].trim().toUpperCase();
      stringFieldValue = retASale[0][intFlow].trim();
      //
      if ("ID1".equals(stringDBField))
        continue;
      //
      if (!"".equals(stringSqlSetData))
        stringSqlSetData += ",";
      if (intNo != 0 && intFlow % 5 == 0)
        stringSqlSetData += "\n";
      //
      if (stringDBType.indexOf("CHAR") != -1 || "DATETIME,".indexOf(stringDBType + ",") != -1) {
        if (stringDBType.equals("NVARCHAR")) {
          stringSqlSetData += "  N'" + stringFieldValue + "'  ";
        } else {
          stringSqlSetData += "  '" + stringFieldValue + "'  ";
        }
      } else {
        stringFieldValue = "" + doParseDouble(stringFieldValue);
        stringSqlSetData += "   " + stringFieldValue + "  ";
      }
      intFlow++;
    }
    stringSql = "INSERT  INTO  A_Sale1   \n(" + stringSqlList + ") \n" + " VALUES  \n(" + stringSqlSetData + ") ";
    return stringSql;
  }

  public String doUpdateInitASale(String stringID1, talk dbSale) throws Throwable {
    String stringSQL = "";
    stringSQL = "UPDATE A_Sale " + " SET YearMM = ''," + " Depart = 0," + " Custom =''," + " OrderDate =''," + " OrderMon = 0," + " H_OrderMon = 0," + " L_OrderMon = 0,"
        + " EnougDate =''," + " EnougMon = 0," + " H_EnougMon = 0," + " L_EnougMon = 0," + " ContrDate =''," + " ContrMon = 0," + " H_ContrMon = 0," + " L_ContrMon = 0,"
        + " Deldate =''," + " PingSu = 0," + " DealDiscount = 0," + " BonusDiscount = 0," + " PreMoney = 0," + " H_PreMoney = 0," + " L_PreMoney = 0," + " DealMoney = 0,"
        + " H_DealMoney = 0," + " L_DealMoney = 0," + " GiftMoney = 0," + " H_GiftMoney = 0," + " L_GiftMoney = 0," + " CommMoney = 0," + " H_CommMoney = 0," + " L_CommMoney = 0,"
        + " CommMoney1 = 0," + // 2015-10-12 B3018
        " H_CommMoney1 = 0," + // 2015-10-12 B3018
        " L_CommMoney1 = 0," + // 2015-10-12 B3018
        " PureMoney = 0," + " H_PureMoney = 0," + " L_PureMoney = 0," + " LastMoney = 0," + " H_LastMoney = 0," + " L_LastMoney = 0," + " BalaMoney = 0," + " H_BalaMoney = 0,"
        + " L_BalaMoney = 0," + " SaleID1 =''," + " SaleName1 =''," + " SaleID2 =''," + " SaleName2 =''," + " SaleID3 =''," + " SaleName3 =''," + " SaleID4 ='',"
        + " SaleName4 =''," + " SaleID5 =''," + " SaleName5 =''," + " SaleGroup =''," + " MediaID =''," + " MediaName =''," + " ZoneID =''," + " ZoneName =''," + " MajorID ='',"
        + " MajorName =''," + " UseType =''," + " Remark =''," + " DateRange =''," + " DateCheck =''," + " DateFile =''," + " DateBonus =''," + " RentRange = 0,"
        + " PingRentPrice = 0," + " PingRent = 0," + " PingRentLast = 0," + " RentPrice = 0," + " Rent = 0," + " RentLast = 0," + " Guranteer = 0," + " RentFree = 0,"
        + " Position1 =''," + " PositionRent1 =''," + " Custom1 =''," + " ViMoney = 0," + " H_ViMoney = 0," + " L_ViMoney = 0," + " AO_sn = 0," + " Plan1 =''," + " ComNo ='',"
        + " OrderNo ='' " + "  , SSMediaID ='' " + // 2010-12-03 新增 SSMediaID
        "  , SSMediaID1 ='' " + // 2015-05-25 新增 SSMediaID1 B3018
        " WHERE ID1 = " + stringID1;
    // dbSale.execFromPool(stringSQL) ;
    return stringSQL;
  }

  public void getInsertASaleMan(String stringID1, String stringTable, String stringID1New, talk dbSale) throws Throwable {
    Vector vectorSql = new Vector();
    JTable jtable = getTable("Table1");
    String SalePer = "";
    String SalesNo = "";
    String SalesName = "";
    String Sales = "";
    String NETSale = "";
    String DirectorNo = "";
    String DirectorName = "";
    String stringOrderNo = getOrderNo(stringID1);
    String stringSql = "";
    String DEPT_CD = "";
    // 先刪除
    if (!"".equals(stringID1))
      vectorSql.add("DELETE  FROM  A_SaleMan  WHERE  ID1  = " + stringID1 + " ");
    // 後新增
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      SalePer = ("" + getValueAt("Table1", intNo, "SalePer")).trim();
      SalesNo = ("" + getValueAt("Table1", intNo, "SalesNo")).trim();
      SalesName = ("" + getValueAt("Table1", intNo, "SalesName")).trim();
      Sales = ("" + getValueAt("Table1", intNo, "Sale")).trim();
      NETSale = ("" + getValueAt("Table1", intNo, "NETSale")).trim();
      DirectorNo = ("" + getValueAt("Table1", intNo, "DirectorNo")).trim();
      DirectorName = ("" + getValueAt("Table1", intNo, "DirectorName")).trim();
      DEPT_CD = ("" + getValueAt("Table1", intNo, "DEPT_CD")).trim();
      //
      if (doParseDouble(Sales) == 0)
        Sales = "0";
      if (doParseDouble(NETSale) == 0)
        NETSale = "0";
      //
      stringSql = "INSERT  INTO  " + stringTable
          + " ( OrderNo,                     ID1 ,                     RecordNo ,      SalePer ,       SalesNo ,          SalesName ,          Sale ,          NETSale,         DirectorNo,      DirectorName,         DEPT_CD ) "
          + " VALUES ( '" + stringOrderNo + "',  " + stringID1New + ", " + (intNo + 1) + ",  " + SalePer + ",  '" + SalesNo + "',   '" + SalesName + "' ,   " + Sales + ",  "
          + NETSale + ",  '" + DirectorNo + "',  '" + DirectorName + "',  '" + DEPT_CD + "' ) ";
      vectorSql.add(stringSql);
    }
    if (vectorSql.size() > 0)
      dbSale.execFromPool((String[]) vectorSql.toArray(new String[0]));
  }

  public String getOrderNo(String stringID1) throws Throwable {
    String stringOrderNo = "";
    String[][] retASaleMan = getA_SaleMan(stringID1);
    for (int intNo = 0; intNo < retASaleMan.length; intNo++) {
      stringOrderNo = retASaleMan[intNo][10].trim();
      if (!"".equals(stringOrderNo)) {
        return stringOrderNo;
      }
    }
    return "";
  }

  public String[][] getA_SaleMan(String stringID1) throws Throwable {
    //
    String stringSql = "";
    String stringUserID = "";
    String[][] retASaleMan = null;
    // 0 ID1 1 RecordNo 2 SalePer 3 SalesNo 4 SalesName
    // 5 DirectorNo 6 DirectorName 7 DEPT_CD 8 Sale 9 NETSale
    // 10 OrderNo
    stringSql = "SELECT  ID1,  RecordNo,  SalePer,  SalesNo,  SalesName,  DirectorNo,  DirectorName,  DEPT_CD,  Sale,  NETSale,  OrderNo " + " FROM  A_SaleMan "
        + " WHERE  ID1  =  " + stringID1 + " " + " ORDER BY  RecordNo ";
    retASaleMan = dbSale.queryFromPool(stringSql);
    return retASaleMan;
  }

  public void doInsertData2(String stringTableName, double doubleNRate, String stringProjectID1, String stringOperatorPosition, String stringOperatorIndex, String stringHouseCar,
      String stringOperatorDom) throws Throwable {
    String stringSql = "";
    String stringID1 = "";
    // System.out.println("MISPROSale------------------") ;
    // 刪除
    doDeleteASale(stringProjectID1, stringOperatorPosition, stringOperatorIndex, stringHouseCar, stringOperatorDom, dbSale);
    // 新增
    doInsertData(stringTableName, doubleNRate, dbSale);
    // 取得 ID1
    // stringID1 = getID1(stringTableName, stringProjectID1, stringOperatorPosition,
    // stringOperatorIndex,
    // stringHouseCar, stringOperatorDom, dbMISPROSale) ;
    // System.out.println("新增至 2K------------------") ;
    // 刪除
    // doDeleteASale(stringProjectID1, stringOperatorPosition, stringOperatorIndex,
    // stringHouseCar, stringOperatorDom, dbSale) ;
    // 新增
    // doInsertData1(stringTableName, stringID1, doubleNRate) ;
  }

  public void doDeleteASale(String stringProjectID1, String stringOperatorPosition, String stringOperatorIndex, String stringHouseCar, String stringOperatorDom, talk dbConnect)
      throws Throwable {
    String stringSql = "";
    //
    stringSql = "DELETE  FROM  A_Sale " + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " AND  OperatorPosition  =  'SSCommand" + stringOperatorPosition + "' "
        + " AND  OperatorIndex  =  '" + stringOperatorIndex + "' " + " AND  HouseCar  =  '" + stringHouseCar + "' " + " AND  OperatorDom  =  '" + stringOperatorDom + "' ";
    System.out.println("doDeleteASale--------------" + stringSql);
    dbConnect.execFromPool(stringSql);
  }

  public String getMaxID1(String stringTableName) throws Throwable {
    String stringSql = "";
    String stringMaxID1 = "0";
    String[][] retDBData = null;
    //
    stringSql = "SELECT  MAX(ID1)  FROM  " + stringTableName;
    retDBData = dbSale.queryFromPool(stringSql);
    if (retDBData.length != 0) {
      stringMaxID1 = "" + (doParseInteger(retDBData[0][0].trim()) + 1);
    }
    return stringMaxID1;
  }

  public String getID1_ASale(String stringProjectID, String stringOperatorPosition, String stringOperatorIndex, String stringHouseCar, String stringOperatorDom, String stringTable,
      talk dbConnect) throws Throwable {
    String stringSql = "";
    String stringID1 = "";
    String[][] retDBData = null;
    //
    stringSql = "SELECT  ID1 " + " FROM  " + stringTable + " " + " WHERE  ProjectID1  =  '" + stringProjectID + "' " + " AND  OperatorPosition  =  'SSCommand"
        + stringOperatorPosition + "' " + " AND  OperatorIndex  =  '" + stringOperatorIndex + "' " + " AND  HouseCar  =  '" + stringHouseCar + "' " + " AND  OperatorDom  =  '"
        + stringOperatorDom + "' ";
    System.out.println("getID1--------------" + stringSql);
    retDBData = dbConnect.queryFromPool(stringSql);
    if (retDBData.length != 0) {
      stringID1 = retDBData[0][0].trim();
    }
    return stringID1;
  }

  public String getID1_ASale1(String stringProjectID, String stringOperatorPosition, String stringOperatorIndex, String stringHouseCar, String stringOperatorDom,
      String stringTable, talk dbConnect) throws Throwable {
    String stringSql = "";
    String stringID1 = "";
    String[][] retDBData = null;
    //
    stringSql = "SELECT  ID1 " + " FROM  " + stringTable + " " + " WHERE  ProjectID1  =  '" + stringProjectID + "' " + " AND  OperatorPosition  =  'SSCommand"
        + stringOperatorPosition + "' " + " AND  OperatorIndex  =  '" + stringOperatorIndex + "' " + " AND  HouseCar  =  '" + stringHouseCar + "' " + " AND  OperatorDom  =  '"
        + stringOperatorDom + "' " + " ORDER BY  ID1 DESC ";
    System.out.println("getID1--------------" + stringSql);
    retDBData = dbConnect.queryFromPool(stringSql);
    if (retDBData.length != 0) {
      stringID1 = retDBData[0][0].trim();
    }
    return stringID1;
  }

  public String getID1(String stringTableName, String stringProjectID, String stringOperatorPosition, String stringOperatorIndex, String stringHouseCar, String stringOperatorDom,
      talk dbConnect) throws Throwable {
    String stringSql = "";
    String stringMaxID1 = "0";
    String[][] retDBData = null;
    //
    stringSql = "SELECT  ID1 " + " FROM  " + stringTableName + " WHERE  ProjectID1  =  '" + stringProjectID + "' " + " AND  OperatorPosition  =  'SSCommand"
        + stringOperatorPosition + "' " + " AND  OperatorIndex  =  '" + stringOperatorIndex + "' " + " AND  HouseCar  =  '" + stringHouseCar + "' " + " AND  OperatorDom  =  '"
        + stringOperatorDom + "' ";
    System.out.println("getID1--------------" + stringSql);
    retDBData = dbConnect.queryFromPool(stringSql);
    if (retDBData.length != 0) {
      stringMaxID1 = "" + (doParseInteger(retDBData[0][0].trim()) + 1);
    }
    return stringMaxID1;
  }

  public void doInsertData1(String stringTableName, String stringMaxID1, double doubleNRate) throws Throwable {
    String stringSql = "";
    String stringProjectID1 = getValue("PC_Default_ProjectID1").trim(); /* PC_Default_ProjectID1$ 初始值提供 */
    String stringOperatorPosition = getValue("PC_OperatorPosition").trim(); /* PC_OperatorPosition$ 由戶別 Sale1020 */
    String stringOperatorIndex = getValue("PC_OperatorIndex").trim(); /* PC_OperatorIndex$ 由戶別 Sale1020 */
    String stringHouseCar = getValue("PC_HouseCar").trim(); // PC_HouseCar$
    String stringOperatorDom = getValue("PC_OperatorDom").trim(); /* PC_OperatorDom$由戶別 Sale1020 */
    String stringText10 = "" + get("PC_SaleM1031_Text1_0");
    String stringText11 = "" + get("PC_SaleM1031_Text1_1");
    String stringText12 = "" + get("PC_SaleM1031_Text1_2");
    //
    stringText10 = "null".equals(stringText10.trim()) ? "" : stringText10.trim();
    stringText11 = "null".equals(stringText10.trim()) ? "" : stringText11.trim();
    stringText12 = "null".equals(stringText10.trim()) ? "" : stringText12.trim();
    //
    //
    String stringNull = null;
    stringSql = "INSERT  INTO " + stringTableName + " (OperatorPosition,  OperatorIndex,  HouseCar,          OperatorDom,      YearMM, "
        + " Depart,                    Com,                    ProjectID,            ProjectID0,           ProjectID1, "
        + " H_Com,                    H_LandOwner,   H_LandShare,    L_Com,                  L_LandOwner, "
        + " L_LandShare,          LandOwner,       LandShare,         Position,               PositionRent,  "
        + " Car,                          CarRent,             Custom,               OrderDate,           OrderMon, "
        + " H_OrderMon,          L_OrderMon,     EnougDate,         EnougMon,          H_EnougMon, "
        + " L_EnougMon,         ContrDate,          ContrMon,           H_ContrMon,       L_ContrMon, "
        + " Deldate,            PingSu,                DealDiscount,     BonusDiscount,   PreMoney,  "
        + " H_PreMoney,          L_PreMoney,      DealMoney,          H_DealMoney,     L_DealMoney, "
        + " GiftMoney,          H_GiftMoney,      L_GiftMoney,      CommMoney,       H_CommMoney, "
        + " L_CommMoney,      PureMoney,        H_PureMoney,    L_PureMoney,     LastMoney, "
        + " H_LastMoney,         L_LastMoney,     BalaMoney,         H_BalaMoney,      L_BalaMoney, "
        + " SaleID1,                    SaleName1,        SaleID2,                SaleName2,          SaleID3, "
        + " SaleName3,              SaleID4,              SaleName4,          SaleID5,                SaleName5, "
        + " SaleID6,                    SaleName6,         DateRange,         DateCheck,          DateFile, "
        + " DateBonus,              RentRange,        MediaID,              MediaName,        MajorID, "
        + " MajorName,             ZoneID,                ZoneName,          UseType,             Remark, "
        + " PingRentPrice,        PingRent,            PingRentLast,       RentPrice,          Rent,  "
        + " RentLast,                Guranteer,           RentFree,              Position1,           PositionRent1, "
        + " Custom1,                 ViMoney,             H_ViMoney,            L_ViMoney,        Plan1,  "
        + " ComNo,                   ID1,                       SaleID7,                   SaleName7,       SaleID8, "
        + "  SaleName8,            SaleID9,               SaleName9,            SaleID10,             SaleName10) " + " VALUES ( 'SSCommand" + stringOperatorPosition + "', " + // 0
        " '" + stringOperatorIndex + "', " + // 1
        " '" + stringHouseCar + "', " + // 2
        " '" + stringOperatorDom + "', " + // 3
        " '" + getValue("SidbEdit2_0").trim() + "', " + // 4
        getValue("PC_Default_Depart").trim() + ", " + // 5
        " '" + getValue("Text1_0").trim() + "', " + // 6
        " '" + getValue("Text1_1").trim() + "', " + // 7
        " '" + getValue("Text1_2").trim() + "', " + // 8
        " '" + getValue("Text1_3").trim() + "', " + // 9
        " '" + getValue("Text1_5").trim() + "', " + // 10
        " '" + getValue("Check1_6").trim() + "', " + // 11
        " '" + getValue("Check1_7").trim() + "', " + // 12
        " '" + getValue("Text1_8").trim() + "', " + // 13
        " '" + getValue("Check1_9").trim() + "', " + // 14
        " '" + getValue("Check1_10").trim() + "', " + // 15
        " '" + getValue("Check1_0").trim() + "', " + // 16
        " '" + getValue("Check1_1").trim() + "', " + // 17
        " '" + getValue("Text1_11").trim() + "', " + // 18
        " '" + getValue("Text1_12").trim() + "', " + // 19
        " '" + getValue("Text1_13").trim() + "', " + // 20
        " '" + getValue("Text1_14").trim() + "', " + // 21
        " '" + getValue("Text1_4").trim() + "', " + // 22
        " '" + getValue("SidbEdit2_0").trim() + "', " + // 23
        (doParseDouble(getValue("SidbEdit2_1").trim()) * doubleNRate) + ", " + // 24
        (doParseDouble(getValue("SidbEdit2_2").trim()) * doubleNRate) + ", " + // 25
        (doParseDouble(getValue("SidbEdit2_3").trim()) * doubleNRate) + ", " + // 26
        " '" + getValue("SidbEdit2_4").trim() + "', " + // 27
        (doParseDouble(getValue("SidbEdit2_5").trim()) * doubleNRate) + ", " + // 28
        (doParseDouble(getValue("SidbEdit2_6").trim()) * doubleNRate) + ", " + // 29
        (doParseDouble(getValue("SidbEdit2_7").trim()) * doubleNRate) + ", " + // 30
        " '" + getValue("SidbEdit2_8").trim() + "', " + // 31
        (doParseDouble(getValue("SidbEdit2_9").trim()) * doubleNRate) + ", " + // 32
        (doParseDouble(getValue("SidbEdit2_10").trim()) * doubleNRate) + ", " + // 33
        (doParseDouble(getValue("SidbEdit2_11").trim()) * doubleNRate) + ", " + // 34
        " '" + getValue("SidtEdit3_0").trim() + "', " + // 35
        doParseDouble(getValue("SidtEdit3_1").trim()) + ", " + // 36
        doParseDouble(getValue("SidtEdit3_2").trim()) + ", " + // 37
        doParseDouble(getValue("SidtEdit3_3").trim()) + ", " + // 38
        (doParseDouble(getValue("SidbEdit4_0").trim()) * doubleNRate) + ", " + // 39
        (doParseDouble(getValue("SidbEdit4_1").trim()) * doubleNRate) + ", " + // 40
        (doParseDouble(getValue("SidbEdit4_2").trim()) * doubleNRate) + ", " + // 41
        (doParseDouble(getValue("SidbEdit4_3").trim()) * doubleNRate) + ", " + // 42
        (doParseDouble(getValue("SidbEdit4_4").trim()) * doubleNRate) + ", " + // 43
        (doParseDouble(getValue("SidbEdit4_5").trim()) * doubleNRate) + ", " + // 44
        (doParseDouble(getValue("SidbEdit4_6").trim()) * doubleNRate) + ", " + // 45
        (doParseDouble(getValue("SidbEdit4_7").trim()) * doubleNRate) + ", " + // 46
        (doParseDouble(getValue("SidbEdit4_8").trim()) * doubleNRate) + ", " + // 47
        (doParseDouble(getValue("SidbEdit4_9").trim()) * doubleNRate) + ", " + // 48
        (doParseDouble(getValue("SidbEdit4_10").trim()) * doubleNRate) + ", " + // 49
        (doParseDouble(getValue("SidbEdit4_11").trim()) * doubleNRate) + ", " + // 50
        (doParseDouble(getValue("SidbEdit4_12").trim()) * doubleNRate) + ", " + // 51
        (doParseDouble(getValue("SidbEdit4_13").trim()) * doubleNRate) + ", " + // 52
        (doParseDouble(getValue("SidbEdit4_14").trim()) * doubleNRate) + ", " + // 53
        (doParseDouble(getValue("SidbEdit4_15").trim()) * doubleNRate) + ", " + // 54
        (doParseDouble(getValue("SidbEdit4_16").trim()) * doubleNRate) + ", " + // 55
        (doParseDouble(getValue("SidbEdit4_17").trim()) * doubleNRate) + ", " + // 56
        (doParseDouble(getValue("SidbEdit4_18").trim()) * doubleNRate) + ", " + // 57
        (doParseDouble(getValue("SidbEdit4_19").trim()) * doubleNRate) + ", " + // 58
        (doParseDouble(getValue("SidbEdit4_20").trim()) * doubleNRate) + ", " + // 59
        " '" + getValue("CSComboBox5_0").trim() + "', " + // 60
        " '" + getValue("Text5_1").trim() + "', " + // 61
        " '" + getValue("CSComboBox5_2").trim() + "', " + // 62
        " '" + getValue("Text5_3").trim() + "', " + // 63
        " '" + getValue("CSComboBox5_4").trim() + "', " + // 64
        " '" + getValue("Text5_5").trim() + "', " + // 65
        " '" + getValue("CSComboBox5_6").trim() + "', " + // 66
        " '" + getValue("Text5_7").trim() + "', " + // 67
        " '" + getValue("CSComboBox5_8").trim() + "', " + // 68
        " '" + getValue("Text5_9").trim() + "', " + // 69
        " '" + getValue("CSComboBox5_10").trim() + "', " + // 20090414
        " '" + getValue("Text5_11").trim() + "', " + // 20090414
        " '" + getValue("SidtEdit6_0").trim() + "', " + // 72
        " '" + getValue("SidtEdit6_1").trim() + "', " + // 73
        " '" + getValue("SidtEdit6_2").trim() + "', " + // 74
        " '" + getValue("SidtEdit6_3").trim() + "', " + // 75
        doParseDouble(getValue("SidtEdit6_4").trim()) + ", " + // 76
        " '" + getValue("CSComboBox7_0").trim() + "', " + // 77
        " '" + getValue("Text7_1").trim() + "', " + // 78
        " '" + getValue("CSComboBox7_2").trim() + "', " + // 79
        " '" + getValue("Text7_3").trim() + "', " + // 80
        " '" + getValue("CSComboBox7_4").trim() + "', " + // 81
        " '" + getValue("Text7_5").trim() + "', " + // 82
        " '" + getValue("Text7_6").trim() + "', " + // 83
        " '" + getValue("Text7_7").trim() + "', " + // 84
        (doParseDouble(getValue("SidbEdit8_0").trim()) * doubleNRate) + ", " + // 85
        (doParseDouble(getValue("SidbEdit8_1").trim()) * doubleNRate) + ", " + // 86
        (doParseDouble(getValue("SidbEdit8_2").trim()) * doubleNRate) + ", " + // 87
        (doParseDouble(getValue("SidbEdit9_0").trim()) * doubleNRate) + ", " + // 88
        (doParseDouble(getValue("SidbEdit9_1").trim()) * doubleNRate) + ", " + // 89
        (doParseDouble(getValue("SidbEdit9_2").trim()) * doubleNRate) + ", " + // 90
        (doParseDouble(getValue("SidbEdit10_0").trim()) * doubleNRate) + ", " + // 91
        doParseDouble(getValue("SidbEdit10_1").trim()) + ", " + // 92
        " '" + stringText10 + "', " + // 93
        " '" + stringText11 + "', " + // 94
        " '" + stringText12 + "', " + // 95
        (doParseDouble(getValue("SidbEdit4_21").trim()) * doubleNRate) + ", " + // 96
        (doParseDouble(getValue("SidbEdit4_22").trim()) * doubleNRate) + ", " + // 97
        (doParseDouble(getValue("SidbEdit4_23").trim()) * doubleNRate) + ", " + // 98
        " '" + getValue("Text40").trim() + "', "; // 99
    if ("".equals(getValue("Text7_0").trim())) {
      stringSql += "null, "; // 100
    } else {
      stringSql += " '" + getValue("Text7_0").trim() + "', "; // 100
    }
    stringSql += " " + stringMaxID1 + ", " + // 101
        " '" + getValue("CSComboBox5_12").trim() + "', " + // 20090525
        " '" + getValue("Text5_13").trim() + "', " + // 20090525
        " '" + getValue("CSComboBox5_14").trim() + "', " + // 20090525
        " '" + getValue("Text5_15").trim() + "', " + // 20090525
        " '" + getValue("CSComboBox5_16").trim() + "', " + // 20090525
        " '" + getValue("Text5_17").trim() + "', " + // 20090525
        " '" + getValue("CSComboBox5_18").trim() + "', " + // 20090525
        " '" + getValue("Text5_19").trim() + "') "; // 20090525
    System.out.println("doInsertData1-----11111---------" + stringSql);
    dbSale.execFromPool(stringSql);
  }

  public void doInsertData(String stringTableName, double doubleNRate, talk dbConnect) throws Throwable {
    String stringSql = "";
    String stringProjectID1 = getValue("PC_Default_ProjectID1").trim(); /* PC_Default_ProjectID1$ 初始值提供 */
    String stringOperatorPosition = getValue("PC_OperatorPosition").trim(); /* PC_OperatorPosition$ 由戶別 Sale1020 */
    String stringOperatorIndex = getValue("PC_OperatorIndex").trim(); /* PC_OperatorIndex$ 由戶別 Sale1020 */
    String stringHouseCar = getValue("PC_HouseCar").trim(); // PC_HouseCar$
    String stringOperatorDom = getValue("PC_OperatorDom").trim(); /* PC_OperatorDom$由戶別 Sale1020 */
    String stringText10 = "" + get("PC_SaleM1031_Text1_0");
    String stringText11 = "" + get("PC_SaleM1031_Text1_1");
    String stringText12 = "" + get("PC_SaleM1031_Text1_2");
    String stringSSMediaID = getValue("SSMediaID").trim(); // 2010-04-21 新增
    String stringSSMediaID1 = getValue("SSMediaID1").trim(); // 2015-05-25 新增 B3018
    //
    stringText10 = "null".equals(stringText10.trim()) ? "" : stringText10.trim();
    stringText11 = "null".equals(stringText10.trim()) ? "" : stringText11.trim();
    stringText12 = "null".equals(stringText10.trim()) ? "" : stringText12.trim();
    //
    //
    stringSql = "INSERT  INTO " + stringTableName + " (OperatorPosition,  OperatorIndex,  HouseCar,          OperatorDom,      YearMM, "
        + " Depart,                    Com,                    ProjectID,            ProjectID0,           ProjectID1, "
        + " H_Com,                    H_LandOwner,   H_LandShare,    L_Com,                  L_LandOwner, "
        + " L_LandShare,          LandOwner,       LandShare,         Position,               PositionRent,  "
        + " Car,                          CarRent,             Custom,               OrderDate,           OrderMon, "
        + " H_OrderMon,          L_OrderMon,     EnougDate,         EnougMon,          H_EnougMon, "
        + " L_EnougMon,         ContrDate,          ContrMon,           H_ContrMon,       L_ContrMon, "
        + " Deldate,            PingSu,                DealDiscount,     BonusDiscount,   PreMoney,  "
        + " H_PreMoney,          L_PreMoney,      DealMoney,          H_DealMoney,     L_DealMoney, "
        + " GiftMoney,          H_GiftMoney,      L_GiftMoney,      CommMoney,       H_CommMoney, "
        + " L_CommMoney,      PureMoney,        H_PureMoney,    L_PureMoney,     LastMoney, "
        + " H_LastMoney,         L_LastMoney,     BalaMoney,         H_BalaMoney,      L_BalaMoney, "
        + " SaleID1,                    SaleName1,        SaleID2,                SaleName2,          SaleID3, "
        + " SaleName3,              SaleID4,              SaleName4,          SaleID5,                SaleName5, "
        + " DateRange,              DateCheck,        DateFile,               DateBonus,         RentRange, "
        + " MediaID,                   MediaName,      MajorID,                MajorName,         ZoneID, "
        + " ZoneName,               UseType,            Remark,               PingRentPrice,    PingRent, "
        + " PingRentLast,          RentPrice,          Rent,                     RentLast,             Guranteer, "
        + " RentFree,                 Position1,           PositionRent1,     Custom1,             ViMoney, "
        + " H_ViMoney,              L_ViMoney,         Plan1,                    ComNo,               SaleID6, "
        + " SaleName6,              SaleID7,               SaleName7,          SaleID8,               SaleName8, "
        + " SaleID9,                     SaleName9,        SaleID10,              SaleName10,        SSMediaID, " + " SSMediaID1 ) " + " VALUES ( 'SSCommand"
        + stringOperatorPosition + "', " + // 0
        " '" + stringOperatorIndex + "', " + // 1
        " '" + stringHouseCar + "', " + // 2
        " '" + stringOperatorDom + "', " + // 3
        " '" + getValue("SidbEdit2_0").trim() + "', " + // 4
        getValue("PC_Default_Depart").trim() + ", " + // 5
        " '" + getValue("Text1_0").trim() + "', " + // 6
        " '" + getValue("Text1_1").trim() + "', " + // 7
        " '" + getValue("Text1_2").trim() + "', " + // 8
        " '" + getValue("Text1_3").trim() + "', " + // 9
        " '" + getValue("Text1_5").trim() + "', " + // 10
        " '" + getValue("Check1_6").trim() + "', " + // 11
        " '" + getValue("Check1_7").trim() + "', " + // 12
        " '" + getValue("Text1_8").trim() + "', " + // 13
        " '" + getValue("Check1_9").trim() + "', " + // 14
        " '" + getValue("Check1_10").trim() + "', " + // 15
        " '" + getValue("Check1_0").trim() + "', " + // 16
        " '" + getValue("Check1_1").trim() + "', " + // 17
        " '" + getValue("Text1_11").trim() + "', " + // 18
        " '" + getValue("Text1_12").trim() + "', " + // 19
        " '" + getValue("Text1_13").trim() + "', " + // 20
        " '" + getValue("Text1_14").trim() + "', " + // 21
        " '" + getValue("Text1_4").trim() + "', " + // 22
        " '" + getValue("SidbEdit2_0").trim() + "', " + // 23
        (doParseDouble(getValue("SidbEdit2_1").trim()) * doubleNRate) + ", " + // 24
        (doParseDouble(getValue("SidbEdit2_2").trim()) * doubleNRate) + ", " + // 25
        (doParseDouble(getValue("SidbEdit2_3").trim()) * doubleNRate) + ", " + // 26
        " '" + getValue("SidbEdit2_4").trim() + "', " + // 27
        (doParseDouble(getValue("SidbEdit2_5").trim()) * doubleNRate) + ", " + // 28
        (doParseDouble(getValue("SidbEdit2_6").trim()) * doubleNRate) + ", " + // 29
        (doParseDouble(getValue("SidbEdit2_7").trim()) * doubleNRate) + ", " + // 30
        " '" + getValue("SidbEdit2_8").trim() + "', " + // 31
        (doParseDouble(getValue("SidbEdit2_9").trim()) * doubleNRate) + ", " + // 32
        (doParseDouble(getValue("SidbEdit2_10").trim()) * doubleNRate) + ", " + // 33
        (doParseDouble(getValue("SidbEdit2_11").trim()) * doubleNRate) + ", " + // 34
        " '" + getValue("SidtEdit3_0").trim() + "', " + // 35
        doParseDouble(getValue("SidtEdit3_1").trim()) + ", " + // 36
        doParseDouble(getValue("SidtEdit3_2").trim()) + ", " + // 37
        doParseDouble(getValue("SidtEdit3_3").trim()) + ", " + // 38
        (doParseDouble(getValue("SidbEdit4_0").trim()) * doubleNRate) + ", " + // 39
        (doParseDouble(getValue("SidbEdit4_1").trim()) * doubleNRate) + ", " + // 40
        (doParseDouble(getValue("SidbEdit4_2").trim()) * doubleNRate) + ", " + // 41
        (doParseDouble(getValue("SidbEdit4_3").trim()) * doubleNRate) + ", " + // 42
        (doParseDouble(getValue("SidbEdit4_4").trim()) * doubleNRate) + ", " + // 43
        (doParseDouble(getValue("SidbEdit4_5").trim()) * doubleNRate) + ", " + // 44
        (doParseDouble(getValue("SidbEdit4_6").trim()) * doubleNRate) + ", " + // 45
        (doParseDouble(getValue("SidbEdit4_7").trim()) * doubleNRate) + ", " + // 46
        (doParseDouble(getValue("SidbEdit4_8").trim()) * doubleNRate) + ", " + // 47
        (doParseDouble(getValue("SidbEdit4_9").trim()) * doubleNRate) + ", " + // 48
        (doParseDouble(getValue("SidbEdit4_10").trim()) * doubleNRate) + ", " + // 49
        (doParseDouble(getValue("SidbEdit4_11").trim()) * doubleNRate) + ", " + // 50
        (doParseDouble(getValue("SidbEdit4_12").trim()) * doubleNRate) + ", " + // 51
        (doParseDouble(getValue("SidbEdit4_13").trim()) * doubleNRate) + ", " + // 52
        (doParseDouble(getValue("SidbEdit4_14").trim()) * doubleNRate) + ", " + // 53
        (doParseDouble(getValue("SidbEdit4_15").trim()) * doubleNRate) + ", " + // 54
        (doParseDouble(getValue("SidbEdit4_16").trim()) * doubleNRate) + ", " + // 55
        (doParseDouble(getValue("SidbEdit4_17").trim()) * doubleNRate) + ", " + // 56
        (doParseDouble(getValue("SidbEdit4_18").trim()) * doubleNRate) + ", " + // 57
        (doParseDouble(getValue("SidbEdit4_19").trim()) * doubleNRate) + ", " + // 58
        (doParseDouble(getValue("SidbEdit4_20").trim()) * doubleNRate) + ", " + // 59
        " '" + getValue("CSComboBox5_0").trim() + "', " + // 60
        " '" + getValue("Text5_1").trim() + "', " + // 61
        " '" + getValue("CSComboBox5_2").trim() + "', " + // 62
        " '" + getValue("Text5_3").trim() + "', " + // 63
        " '" + getValue("CSComboBox5_4").trim() + "', " + // 64
        " '" + getValue("Text5_5").trim() + "', " + // 65
        " '" + getValue("CSComboBox5_6").trim() + "', " + // 66
        " '" + getValue("Text5_7").trim() + "', " + // 67
        " '" + getValue("CSComboBox5_8").trim() + "', " + // 68
        " '" + getValue("Text5_9").trim() + "', " + // 69
        " '" + getValue("SidtEdit6_0").trim() + "', " + // 70
        " '" + getValue("SidtEdit6_1").trim() + "', " + // 71
        " '" + getValue("SidtEdit6_2").trim() + "', " + // 72
        " '" + getValue("SidtEdit6_3").trim() + "', " + // 73
        doParseDouble(getValue("SidtEdit6_4").trim()) + ", " + // 74
        " '" + getValue("CSComboBox7_0").trim() + "', " + // 75
        " '" + getValue("Text7_1").trim() + "', " + // 76
        " '" + getValue("CSComboBox7_2").trim() + "', " + // 77
        " '" + getValue("Text7_3").trim() + "', " + // 78
        " '" + getValue("CSComboBox7_4").trim() + "', " + // 79
        " '" + getValue("Text7_5").trim() + "', " + // 80
        " '" + getValue("Text7_6").trim() + "', " + // 81
        " '" + getValue("Text7_7").trim() + "', " + // 82
        (doParseDouble(getValue("SidbEdit8_0").trim()) * doubleNRate) + ", " + // 83
        (doParseDouble(getValue("SidbEdit8_1").trim()) * doubleNRate) + ", " + // 84
        (doParseDouble(getValue("SidbEdit8_2").trim()) * doubleNRate) + ", " + // 85
        (doParseDouble(getValue("SidbEdit9_0").trim()) * doubleNRate) + ", " + // 86
        (doParseDouble(getValue("SidbEdit9_1").trim()) * doubleNRate) + ", " + // 87
        (doParseDouble(getValue("SidbEdit9_2").trim()) * doubleNRate) + ", " + // 88
        (doParseDouble(getValue("SidbEdit10_0").trim()) * doubleNRate) + ", " + // 89
        doParseDouble(getValue("SidbEdit10_1").trim()) + ", " + // 90
        " '" + stringText10 + "', " + // 91
        " '" + stringText11 + "', " + // 92
        " '" + stringText12 + "', " + // 93
        (doParseDouble(getValue("SidbEdit4_21").trim()) * doubleNRate) + ", " + // 94
        (doParseDouble(getValue("SidbEdit4_22").trim()) * doubleNRate) + ", " + // 95
        (doParseDouble(getValue("SidbEdit4_23").trim()) * doubleNRate) + ", " + // 96
        " '" + getValue("Text40").trim() + "', " + // 97
        " '" + getValue("Text7_0").trim() + "', " + // 98
        " '" + getValue("CSComboBox5_10").trim() + "', " + // 99 20090414
        " '" + getValue("Text5_11").trim() + "', " + // 100 20090414
        " '" + getValue("CSComboBox5_12").trim() + "', " + // 99 20090525
        " '" + getValue("Text5_13").trim() + "', " + // 100 20090525
        " '" + getValue("CSComboBox5_14").trim() + "', " + // 99 20090525
        " '" + getValue("Text5_15").trim() + "', " + // 100 20090525
        " '" + getValue("CSComboBox5_16").trim() + "', " + // 99 20090525
        " '" + getValue("Text5_17").trim() + "', " + // 100 20090525
        " '" + getValue("CSComboBox5_18").trim() + "', " + // 99 20090525
        " '" + getValue("Text5_19").trim() + "', " + // 100 20090525
        " '" + stringSSMediaID + "', " + // 2010-04-21 新增
        " '" + stringSSMediaID1 + "') "; // 2015-05-25 新增 B3018
    System.out.println("doInsertData------22222--------" + stringSql);
    dbConnect.execFromPool(stringSql);
  }

  public boolean isBatchCheckOK(FargloryUtil exeUtil) throws Throwable {
    // 棟樓別
    String stringText1_11 = getValue("Text1_11").trim();
    String stringText1_12 = getValue("Text1_12").trim();
    String stringText1_13 = getValue("Text1_13").trim();
    String stringText1_14 = getValue("Text1_14").trim();
    /*
     * if("".equals(stringText1_11) && "".equals(stringText1_12) &&
     * "".equals(stringText1_13) && "".equals(stringText1_14)) {
     * message("棟樓或車位不能空白。") ; return false ; }
     */
    // 付訂日期 SidbEdit2_0
    String retDate = "";
    String stringTemp = getValue("SidbEdit2_0").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "付訂日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidbEdit2_0").requestFocus();
        return false;
      } else {
        setValue("SidbEdit2_0", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    // 補足日期 SidbEdit2_4
    stringTemp = getValue("SidbEdit2_4").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "補足日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidbEdit2_4").requestFocus();
        return false;
      } else {
        setValue("SidbEdit2_4", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    // 簽約日期 SidbEdit2_8
    stringTemp = getValue("SidbEdit2_8").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "簽約日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidbEdit2_8").requestFocus();
        return false;
      } else {
        setValue("SidbEdit2_8", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    // 退戶日期 SidtEdit3_0
    stringTemp = getValue("SidtEdit3_0").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "退戶日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidtEdit3_0").requestFocus();
        return false;
      } else {
        setValue("SidtEdit3_0", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    // 票期日期 SidtEdit6_0
    stringTemp = getValue("SidtEdit6_0").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "票期日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidtEdit6_0").requestFocus();
        return false;
      } else {
        setValue("SidtEdit6_0", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    // 合約會審 SidtEdit6_1
    stringTemp = getValue("SidtEdit6_1").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "合約會審日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidtEdit6_1").requestFocus();
        return false;
      } else {
        setValue("SidtEdit6_1", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    // 合約歸檔 SidtEdit6_2
    stringTemp = getValue("SidtEdit6_2").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "合約歸檔日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidtEdit6_2").requestFocus();
        return false;
      } else {
        setValue("SidtEdit6_2", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    // 獎金請領 SidtEdit6_3
    stringTemp = getValue("SidtEdit6_3").trim();
    if (!"".equals(stringTemp)) {
      retDate = getDate(stringTemp, "獎金請領日期");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("SidtEdit6_3").requestFocus();
        return false;
      } else {
        setValue("SidtEdit6_3", retDate.trim());
        // System.out.println("------"+retDate) ;
      }
    }
    /*
     * // 售出人 5 String stringProjectID1 = getValue("PC_Default_ProjectID1").trim( )
     * ; String stringUserName = "" ; String stringUserID = "" ; stringTemp =
     * getValue("CSComboBox5_0").trim( ) ; if(!"".equals(stringTemp)) {
     * stringUserName = getUserName(stringProjectID1, stringTemp) ;
     * if(!"".equals(stringUserName)) { setValue("Text5_1", stringUserName) ; } else
     * { message("資料庫無此資料。") ; getcLabel("CSComboBox5_0").requestFocus() ; return
     * false ; } } stringTemp = getValue("Text5_1").trim( ) ;
     * if(!"".equals(stringTemp)) { stringUserID = getUserID(stringProjectID1,
     * stringTemp) ; if(!"".equals(stringUserID)) { setValue("CSComboBox5_0",
     * stringUserID) ; } else { message("資料庫無此資料。") ;
     * getcLabel("Text5_1").requestFocus() ; return false ; } } stringTemp =
     * getValue("CSComboBox5_2").trim( ) ; if(!"".equals(stringTemp)) {
     * stringUserName = getUserName(stringProjectID1, stringTemp) ;
     * if(!"".equals(stringUserName)) { setValue("Text5_3", stringUserName) ; } else
     * { message("資料庫無此資料。") ; getcLabel("CSComboBox5_2").requestFocus() ; return
     * false ; } } stringTemp = getValue("Text5_3").trim( ) ;
     * if(!"".equals(stringTemp)) { stringUserID = getUserID(stringProjectID1,
     * stringTemp) ; if(!"".equals(stringUserID)) { setValue("CSComboBox5_2",
     * stringUserID) ; } else { message("資料庫無此資料。") ;
     * getcLabel("Text5_3").requestFocus() ; return false ; } } stringTemp =
     * getValue("CSComboBox5_4").trim( ) ; if(!"".equals(stringTemp)) {
     * stringUserName = getUserName(stringProjectID1, stringTemp) ;
     * if(!"".equals(stringUserName)) { setValue("Text5_5", stringUserName) ; } else
     * { message("資料庫無此資料。") ; getcLabel("CSComboBox5_4").requestFocus() ; return
     * false ; } } stringTemp = getValue("Text5_5").trim( ) ;
     * if(!"".equals(stringTemp)) { stringUserID = getUserID(stringProjectID1,
     * stringTemp) ; if(!"".equals(stringUserID)) { setValue("CSComboBox5_4",
     * stringUserID) ; } else { message("資料庫無此資料。") ;
     * getcLabel("Text5_5").requestFocus() ; return false ; } } stringTemp =
     * getValue("CSComboBox5_6").trim( ) ; if(!"".equals(stringTemp)) {
     * stringUserName = getUserName(stringProjectID1, stringTemp) ;
     * if(!"".equals(stringUserName)) { setValue("Text5_7", stringUserName) ; } else
     * { message("資料庫無此資料。") ; getcLabel("CSComboBox5_6").requestFocus() ; return
     * false ; } } stringTemp = getValue("Text5_7").trim( ) ;
     * if(!"".equals(stringTemp)) { stringUserID = getUserID(stringProjectID1,
     * stringTemp) ; if(!"".equals(stringUserID)) { setValue("CSComboBox5_6",
     * stringUserID) ; } else { message("資料庫無此資料。") ;
     * getcLabel("Text5_7").requestFocus() ; return false ; } } stringTemp =
     * getValue("CSComboBox5_8").trim( ) ; if(!"".equals(stringTemp)) {
     * stringUserName = getUserName(stringProjectID1, stringTemp) ;
     * if(!"".equals(stringUserName)) { setValue("Text5_9", stringUserName) ; } else
     * { message("資料庫無此資料。") ; getcLabel("CSComboBox5_8").requestFocus() ; return
     * false ; } } stringTemp = getValue("Text5_9").trim( ) ;
     * if(!"".equals(stringTemp)) { stringUserID = getUserID(stringProjectID1,
     * stringTemp) ; if(!"".equals(stringUserID)) { setValue("CSComboBox5_8",
     * stringUserID) ; } else { message("資料庫無此資料。") ;
     * getcLabel("Text5_9").requestFocus() ; return false ; } }
     */
    // 媒體 2010-04-21 新增 [媒體] 不可為空白及存在檢核
    String stringSSMediaID = getValue("SSMediaID").trim();
    String stringSSMediaID1 = getValue("SSMediaID1").trim();
    String stringSidbEdit20 = getValue("SidbEdit2_0").trim(); // 付訂日期
    if ("".equals(stringSSMediaID)) {
      // message("[媒體] 不可為空白。") ;
      // getcLabel("SSMediaID").requestFocus() ;
      // return false ;
    } else {
      String stringSSMediaName = getSSMediaName(stringSSMediaID);
      if ("".equals(stringSSMediaName)) {
        message("[媒體] 不存在資料庫中。");
        getcLabel("SSMediaID").requestFocus();
        return false;
      }
    }

    // 業別
    stringTemp = getValue("CSComboBox7_2").trim();
    if (!"".equals(stringTemp)) {
      String stringMajorName = getMajorName(stringTemp);
      if (!"".equals(stringMajorName)) {
        setValue("Text7_3", stringMajorName);
      } else {
        message("業別無此資料。");
        getcLabel("CSComboBox7_2").requestFocus();
        return false;
      }
    }
    stringTemp = getValue("Text7_3").trim();
    if (!"".equals(stringTemp)) {
      String stringMajorID = getMajorID(stringTemp);
      if (!"".equals(stringMajorID)) {
        setValue("CSComboBox7_2", stringMajorID);
      } else {
        message("業別無此資料。");
        getcLabel("Text7_3").requestFocus();
        return false;
      }
    }
    // 區域
    stringTemp = getValue("CSComboBox7_4").trim();
    if (!"".equals(stringTemp)) {
      String stringZoneName = getZoneName(stringTemp);
      if (!"".equals(stringZoneName)) {
        setValue("Text7_5", stringZoneName);
      } else {
        message("資料庫無此資料。");
        getcLabel("CSComboBox7_4").requestFocus();
        return false;
      }
    }
    stringTemp = getValue("Text7_5").trim();
    if (!"".equals(stringTemp)) {
      String stringZoneID = getZoneID(stringTemp);
      if (!"".equals(stringZoneID)) {
        setValue("CSComboBox7_4", stringZoneID);
      } else {
        message("資料庫無此資料。");
        getcLabel("Text7_5").requestFocus();
        return false;
      }
    }
    // 2015-12-10 B3018 售出人檢核 S
    JTable jtable1 = getTable("Table1");
    JTabbedPane jTabbedPane1 = getTabbedPane("tab1");
    String stringSaleID1 = "";
    String stringZ6SaleID2 = "";
    String stringCSSaleID2 = "";
    String stringSaleName1 = "";
    String stringZ6SaleName2 = "";
    String stringCSSaleName2 = "";
    if (jtable1.getRowCount() <= 0) {
      if (exeUtil.doParseDouble(getValue("SidbEdit4_3").trim()) == 0) {
        message("");
        return true;
      } else {
        jTabbedPane1.setSelectedIndex(1);
        message("[售出人表格] 不可無資料。");
        return false;
      }
    }
    for (int intNo = 0; intNo < jtable1.getRowCount(); intNo++) {
      stringSaleID1 = ("" + getValueAt("Table1", intNo, "SaleID1")).trim();
      stringSaleName1 = ("" + getValueAt("Table1", intNo, "SaleName1")).trim();
      stringZ6SaleID2 = ("" + getValueAt("Table1", intNo, "Z6SaleID2")).trim();
      stringZ6SaleName2 = ("" + getValueAt("Table1", intNo, "Z6SaleName2")).trim();
      stringCSSaleID2 = ("" + getValueAt("Table1", intNo, "CSSaleID2")).trim();
      stringCSSaleName2 = ("" + getValueAt("Table1", intNo, "CSSaleName2")).trim();
      //
      if ("".equals(stringSaleID1)) {
        jTabbedPane1.setSelectedIndex(1);
        jtable1.setRowSelectionInterval(intNo, intNo);
        message("[售出人表格] 第 " + (intNo + 1) + " 列之 [銷售(實際)-員編] 不可為空白。");
        return false;
      }
      if ("".equals(stringSaleName1)) {
        jTabbedPane1.setSelectedIndex(1);
        jtable1.setRowSelectionInterval(intNo, intNo);
        message("[售出人表格] 第 " + (intNo + 1) + " 列之 [銷售(實際)-售出人] 不可為空白。");
        return false;
      }
      if ("".equals(stringZ6SaleID2) && "".equals(stringCSSaleID2)) {
        jTabbedPane1.setSelectedIndex(1);
        jtable1.setRowSelectionInterval(intNo, intNo);
        message("[售出人表格] 第 " + (intNo + 1) + " 列之 [遠雄房屋(掛帳)-員編][遠雄人壽(掛帳)-員編] 不可皆為空白。");
        return false;
      }
      if (!"".equals(stringZ6SaleID2) && "".equals(stringZ6SaleName2)) {
        jTabbedPane1.setSelectedIndex(1);
        jtable1.setRowSelectionInterval(intNo, intNo);
        message("[售出人表格] 第 " + (intNo + 1) + " 列之 [遠雄房屋(實際)-售出人] 不可為空白。");
        return false;
      }
      if (!"".equals(stringCSSaleID2) && "".equals(stringCSSaleName2)) {
        jTabbedPane1.setSelectedIndex(1);
        jtable1.setRowSelectionInterval(intNo, intNo);
        messagebox("[售出人表格] 第 " + (intNo + 1) + " 列之 [遠雄人壽(實際)-售出人] 不可為空白。");
        return false;
      }
    }
    // if("B3018".equals(getUser())) { messagebox("測試") ;return false ;}
    // 2015-12-10 B3018 售出人檢核 E
    message("");
    return true;
  }

  public String[][] getASale(String stringProjectID1, String stringOperatorPosition, String stringOperatorIndex, String stringHouseCar, String stringOperatorDom) throws Throwable {
    String stringSql = "";
    String[][] retASale = null;
    // 0 Com 1 ProjectID 2 ProjectID0 3 ProjectID1 4 Custom
    // 5 LandOwner 6 LandShare 7 H_Com 8 H_LandOwner 9 H_LandShare
    // 10 L_Com 11 L_LandOwner 12 L_LandShare 13 Position 14 PositionRent
    // 15 Car 16 CarRent 17 OrderDate 18 OrderMon 19 H_OrderMon
    // 20 L_OrderMon 21 EnougDate 22 EnougMon 23 H_EnougMon 24 L_EnougMon
    // 25 ContrDate 26 ContrMon 27 H_ContrMon 28 L_ContrMon 29 Deldate
    // 30 PingSu, 31 DealDiscount 32 BonusDiscount 33 PreMoney 34 H_PreMoney
    // 35 L_PreMoney 36 DealMoney 37 H_DealMoney 38 L_DealMoney 39 GiftMoney
    // 40 H_GiftMoney 41 L_GiftMoney 42 CommMoney 43 H_CommMoney 44 L_CommMoney
    // 45 PureMoney 46 H_PureMoney 47 L_PureMoney 48 LastMoney 49 H_LastMoney
    // 50 L_LastMoney 51 BalaMoney 52 H_BalaMoney 53 L_BalaMoney 54 DealDiscount
    // 55 ViMoney 56 H_ViMoney 57 L_ViMoney 58 SaleID1 59 SaleName1
    // 60 SaleID2 61 SaleName2 62 SaleID3 63 SaleName3 64 SaleID4
    // 65 SaleName4 66 SaleID5 67 SaleName5 68 DateRange 69 DateCheck
    // 70 DateFile 71 DateBonus 72 RentRange 73 MediaID 74 MediaName
    // 75 MajorID 76 MajorName 77 ZoneID 78 ZoneName 79 UseType
    // 80 Remark 81 PingRentPrice 82 PingRent 83 PingRentLast 84 RentPrice
    // 85 Rent 86 RentLast 87 Guranteer 88 RentFree 89 Position1
    // 90 PositionRent1 91 Custom1 92 Plan1 93 ComNo 94 SaleID1
    // 95 SaleName1 96 SaleID7 97 SaleName7 98 SaleID8 99 SaleName8
    // 100 SaleID9 101 SaleName9 102 SaleID10 103 SaleName10 104 SSMediaID
    // 105 SSMediaID1 106 CommMoney1 107 H_CommMoney1 108 LCommMoney1 109 ID1
    stringSql = "SELECT  Com,                    ProjectID,          ProjectID0,         ProjectID1,           Custom, "
        + " LandOwner,        LandShare,       H_Com,               H_LandOwner,     H_LandShare, "
        + " L_Com,                L_LandOwner,  L_LandShare,     Position,               PositionRent, "
        + " Car,                      CarRent,           OrderDate,          OrderMon,           H_OrderMon, "
        + " L_OrderMon,      EnougDate,      EnougMon,         H_EnougMon,      L_EnougMon, " + " ContrDate,          ContrMon,        H_ContrMon,       L_ContrMon,      Deldate, "
        + " PingSu,               DealDiscount,   BonusDiscount,  PreMoney,           H_PreMoney, "
        + " L_PreMoney,      DealMoney,      H_DealMoney,     L_DealMoney,     GiftMoney, " + " H_GiftMoney,     L_GiftMoney,    CommMoney,      H_CommMoney,  L_CommMoney, "
        + " PureMoney,       H_PureMoney,  L_PureMoney,     LastMoney,         H_LastMoney, " + " L_LastMoney,    BalaMoney,       H_BalaMoney,     L_BalaMoney,     DealDiscount, "
        + " ViMoney,            H_ViMoney,       L_ViMoney,          SaleID1,               SaleName1, "
        + " SaleID2,              SaleName2,       SaleID3,                SaleName3,         SaleID4, "
        + " SaleName4,       SaleID5,              SaleName5,          DateRange,        DateCheck, "
        + " DateFile,            DateBonus,        RentRange,         MediaID,              MediaName, "
        + " MajorID,             MajorName,       ZoneID,                 ZoneName,          UseType, "
        + " Remark,             PingRentPrice,  PingRent,             PingRentLast,      RentPrice, "
        + " Rent,                  RentLast,           Guranteer,           RentFree,             Position1, "
        + " PositionRent1,  Custom1,           Plan1,                   ComNo,                 SaleID6, "
        + " SaleName6,       SaleID7,              SaleName7,         SaleID8,                SaleName8, "
        + " SaleID9,             SaleName9,        SaleID10,             SaleName10,        SSMediaID, " + // 20090525 // 20100421 增加 SSMediaID 欄位
        " SSMediaID1,     CommMoney1,   H_CommMoney1,  L_CommMoney1,  ID1 " + // 2015-10-12 增加 CommMoney1、H_CommMoney1、L_CommMoney1 欄位
        " FROM  A_Sale " + " WHERE  ProjectID1 =  '" + stringProjectID1 + "' " + " AND  OperatorPosition  =  'SSCommand" + stringOperatorPosition + "' "
        + " AND  OperatorIndex  =  '" + stringOperatorIndex + "' " + " AND  HouseCar  =  '" + stringHouseCar + "' " + " AND  OperatorDom  =  '" + stringOperatorDom + "' ";

    retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getASale(String stringProjectID1, String stringText10, String stringText11) throws Throwable {
    //
    String stringSql = "";
    String[][] retASale = null;
    // 0 HouseRate 1 LandRate 2 H_Com 3 L_Com 4 Depart
    stringSql = "SELECT  Custom " + " FROM  A_Sale " + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " AND  HouseCar  =  'Position' ";
    if (!"".equals(stringText10)) {
      stringSql += " AND  Position  =  '" + stringText10 + "' ";
    }
    if (!"".equals(stringText11)) {
      stringSql += " AND  PositionRent  =  '" + stringText11 + "' ";
    }
    retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getASale1() throws Throwable {
    String stringSql = "";
    String stringProjectID1 = getValue("PC_Default_ProjectID1").trim(); /* PC_Default_ProjectID1$ 初始值提供 */
    String stringOperatorPosition = getValue("PC_OperatorPosition").trim(); /* PC_OperatorPosition$ 由戶別 Sale1020 */
    String stringOperatorIndex = getValue("PC_OperatorIndex").trim(); /* PC_OperatorIndex$ 由戶別 Sale1020 */
    String stringHouseCar = getValue("PC_HouseCar").trim(); // PC_HouseCar$
    String stringOperatorDom = getValue("PC_OperatorDom").trim(); /* PC_OperatorDom$由戶別 Sale1020 */
    String[][] retASale1 = null;
    //
    stringSql = "SELECT  Deldate,       Position,       PositionRent,  Car,             CarRent, " + " Custom,       DealMoney,  BalaMoney,     Remark,      OrderDate, "
        + " OrderMon,  EnougDate,   EnougMon,     ContrDate,  ContrMon " + " FROM  A_Sale1 " + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' "
        + " AND  OperatorPosition  =  'SSCommand" + stringOperatorPosition + "' " + " AND  OperatorIndex  =  '" + stringOperatorIndex + "' " + " AND  HouseCar  =  '"
        + stringHouseCar + "' " + " AND  OperatorDom  =  '" + stringOperatorDom + "' " + " ORDER BY  DelDate DESC ";
    retASale1 = dbSale.queryFromPool(stringSql);
    return retASale1;
  }

  // 2010-04-21 新增至系統
  public String getSSMediaName(String stringSSMediaID) throws Throwable {
    String stringSql = "";
    String[][] retMediaSS = null;
    //
    stringSql = " SELECT  SSMediaName  " + " FROM  Media_SS  " + " WHERE  SSMediaID=  '" + stringSSMediaID + "' " + " AND  Stop  =  'N' ";
    retMediaSS = dbSale.queryFromPool(stringSql);
    if (retMediaSS.length == 0)
      return "";
    return retMediaSS[0][0].trim();
  }

  public String[][] getASale1(String stringProjectID1) throws Throwable {
    //
    String stringSql = "";
    String[][] retAProject = null;
    // 0 HouseRate 1 LandRate 2 H_Com 3 L_Com 4 Depart
    stringSql = "SELECT  HouseRate,  LandRate,  H_Com,  L_Com,  Depart " + " FROM  A_Project " + " WHERE  ProjectID  =  '" + stringProjectID1 + "' ";
    retAProject = dbSale.queryFromPool(stringSql);
    return retAProject;
  }

  // 售出人
  public String getUserID(String stringProjectID1, String stringUserName) throws Throwable {
    //
    String stringSql = "";
    String stringUserID = "";
    String[][] retAEmployee = null;
    //
    stringSql = "SELECT  UserID " + " FROM  A_Employee " +
//                   " WHERE  RTRIM(ProjectID)  =  '"  +  stringProjectID1  +  "' " +
        " WHERE  RTRIM(UserName)  =  '" + stringUserName + "' " + " AND  OnJob  =  'Y' ";
    retAEmployee = dbSale.queryFromPool(stringSql);
    if (retAEmployee.length != 0) {
      stringUserID = retAEmployee[0][0].trim();
    }
    return stringUserID;
  }

  public String getUserName(String stringProjectID1, String stringUserID) throws Throwable {
    //
    String stringSql = "";
    String stringUserName = "";
    String[][] retAEmployee = null;
    //
    stringSql = "SELECT  UserName " + " FROM  A_Employee " +
    // " WHERE RTRIM(ProjectID) = '" + stringProjectID1 + "' " +
        " WHERE  RTRIM(UserID)  =  '" + stringUserID + "' " + " AND  OnJob  =  'Y' ";
    retAEmployee = dbSale.queryFromPool(stringSql);
    if (retAEmployee.length != 0) {
      stringUserName = retAEmployee[0][0].trim();
    }
    return stringUserName;
  }

  // 媒體
  public String getMediaID(String stringMediaName) throws Throwable {
    //
    String stringSql = "";
    String stringMediaID = "";
    String[][] retAMedia = null;
    //
    stringSql = "SELECT  MediaID " + " FROM  A_Media " + " WHERE  RTRIM(MediaName)  =  '" + stringMediaName + "' ";
    retAMedia = dbSale.queryFromPool(stringSql);
    if (retAMedia.length != 0) {
      stringMediaID = retAMedia[0][0].trim();
    }
    return stringMediaID;
  }

  public String getMediaName(String stringMediaID) throws Throwable {
    //
    String stringSql = "";
    String stringMediaName = "";
    String[][] retAMedia = null;
    //
    stringSql = "SELECT  MediaName " + " FROM  A_Media " + " WHERE  RTRIM(MediaID)  =  '" + stringMediaID + "' ";
    retAMedia = dbSale.queryFromPool(stringSql);
    if (retAMedia.length != 0) {
      stringMediaName = retAMedia[0][0].trim();
    }
    return stringMediaName;
  }

  // 業別
  public String getMajorID(String stringMajorName) throws Throwable {
    //
    String stringSql = "";
    String stringMajorID = "";
    String[][] retAMajor = null;
    //
    stringSql = "SELECT  MajorID " + " FROM  A_Major " + " WHERE  RTRIM(MajorName)  =  '" + stringMajorName + "' ";
    retAMajor = dbSale.queryFromPool(stringSql);
    if (retAMajor.length != 0) {
      stringMajorID = retAMajor[0][0].trim();
    }
    return stringMajorID;
  }

  public String getMajorName(String stringMajorID) throws Throwable {
    //
    String stringSql = "";
    String stringMajorName = "";
    String[][] retAMajor = null;
    //
    stringSql = "SELECT  MajorName " + " FROM  A_Major " + " WHERE  RTRIM(MajorID)  =  '" + stringMajorID + "' ";
    retAMajor = dbSale.queryFromPool(stringSql);
    if (retAMajor.length != 0) {
      stringMajorName = retAMajor[0][0].trim();
    }
    return stringMajorName;
  }

  // 區域
  public String getZoneID(String stringZoneName) throws Throwable {
    //
    String stringSql = "";
    String stringZoneID = "";
    String[][] retAZone = null;
    //
    stringSql = "SELECT  ZoneID " + " FROM  A_Zone " + " WHERE  RTRIM(ZoneName)  =  '" + stringZoneName + "' ";
    retAZone = dbSale.queryFromPool(stringSql);
    if (retAZone.length != 0) {
      stringZoneID = retAZone[0][0].trim();
    }
    return stringZoneID;
  }

  public String getZoneName(String stringZoneID) throws Throwable {
    //
    String stringSql = "";
    String stringZoneName = "";
    String[][] retAZone = null;
    //
    stringSql = "SELECT  ZoneName " + " FROM  A_Zone " + " WHERE  RTRIM(ZoneID)  =  '" + stringZoneID + "' ";
    retAZone = dbSale.queryFromPool(stringSql);
    if (retAZone.length != 0) {
      stringZoneName = retAZone[0][0].trim();
    }
    return stringZoneName;
  }

  //
  public String doDatetimeToString(String stringDatetime) throws Throwable {
    String stringDate = "";
    String stringTemp = "";
    // 檢核
    System.out.println("S-----------------" + stringDatetime);
    if ("".equals(stringDatetime) || stringDatetime.length() < 10)
      return stringDate;
    stringTemp = convert.replace(stringDatetime.substring(0, 10), "-", "");
    if (!check.isACDay(stringTemp))
      return stringDate;
    stringDate = convert.FormatedDate(stringTemp, "/");
    if ("1900/01/01".equals(stringDate))
      stringDate = "";
    System.out.println("E-----------------" + stringDatetime);
    return stringDate;
  }

  // 日期
  public String getDate(String value, String stringErrorFieldName) throws Throwable {
    int intIndexStart = 0;
    int intIndexEnd = value.indexOf("/");
    String stringTmp = "";
    Vector retVector = new Vector();
    int i = 0;
    while (intIndexEnd != -1) {
      stringTmp = value.substring(intIndexStart, intIndexEnd);
      if (!"".equals(stringTmp))
        retVector.add(stringTmp.trim());
      intIndexStart = intIndexEnd + "/".length();
      intIndexEnd = value.indexOf("/", intIndexStart);
      i++;
    }
    stringTmp = value.substring(intIndexStart, value.length());
    if (!"".equals(stringTmp))
      retVector.add(stringTmp.trim());
    boolean booleanFlow = (retVector.size() != 3);
    booleanFlow = booleanFlow && ((retVector.size() == 1 && value.length() != 8) || (retVector.size() != 1));
    if (booleanFlow) {
      return "[" + stringErrorFieldName + "] 日期格式錯誤(YYYY/MM/DD)";
    }
    stringTmp = "";
    booleanFlow = true;
    if (((String) retVector.get(0)).length() < 4)
      stringTmp = "0" + (String) retVector.get(0);
    else
      stringTmp = (String) retVector.get(0);
    for (int intRetVector = 1; intRetVector < retVector.size(); intRetVector++) {
      if (((String) retVector.get(intRetVector)).length() == 1) {
        stringTmp += "/0" + (String) retVector.get(intRetVector);
      } else {
        stringTmp += "/" + (String) retVector.get(intRetVector);
      }
    }
    if (stringTmp.length() == 8) {
      stringTmp = stringTmp.substring(0, 4) + "/" + stringTmp.substring(4, 6) + "/" + stringTmp.substring(6, 8);
    }
    String retDate = stringTmp;
    stringTmp = stringTmp.substring(0, 4) + stringTmp.substring(5, 7) + stringTmp.substring(8, 10);
    if (!check.isACDay(stringTmp)) {
      return "[" + stringErrorFieldName + "] 日期格式錯誤(YYYY/MM/DD)";
    }
    return retDate;
  }

  public double doRemoveSign(String stringSign) {
    //
    String stringTemp = "0";
    double doubleNum = 0;
    if ("".equals(stringSign))
      return doubleNum;
    try {
      stringTemp = convert.replace(stringSign, ",", "");
      doubleNum = doParseDouble(stringTemp);
    } catch (Exception e) {
      return 0;
    }
    return doubleNum;
  }

  public double doParseDouble(String stringNum) throws Exception {
    //
    double doubleNum = 0;
    if ("".equals(stringNum) || "null".equals(stringNum))
      return 0;
    try {
      doubleNum = Double.parseDouble(stringNum);
    } catch (Exception e) {
      System.out.println("無法剖析[" + stringNum + "]，回傳 0。");
      return 0;
    }
    return doubleNum;
  }

  public int doParseInteger(String stringNum) throws Exception {
    //
    int intNum = 0;
    if ("".equals(stringNum) || "null".equals(stringNum))
      return 0;
    try {
      intNum = Integer.parseInt(stringNum);
    } catch (Exception e) {
      System.out.println("無法剖析[" + stringNum + "]，回傳 0。");
      return 0;
    }
    return intNum;
  }

  public String getInformation() {
    return "---------------button1(\u67e5\u8a62\u9000\u6236).defaultValue()----------------";
  }
}
