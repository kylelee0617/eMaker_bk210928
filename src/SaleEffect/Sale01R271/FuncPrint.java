package SaleEffect.Sale01R271;

/**
 * 1. 透過SP查詢日期範圍內的通路
 * 2. 依照當月&年&案累計各通路統計金額
 */

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import com.jacob.activeX.*;
import com.jacob.com.*;

import Farglory.util.FargloryUtil;

public class FuncPrint extends bTransaction {
  FargloryUtil util = new FargloryUtil();
  
  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    if (getValue("ProjectID1").length() == 0) {
      message("案別 不可空白!");
      return false;
    }
    if (getValue("OrderDate1").length() == 0 || getValue("OrderDate2").length() == 0) {
      message("成交日期 不可空白!");
      return false;
    }
    String stringDate1 = getValue("OrderDate1");
    if ("".equals(stringDate1))
      return true;
    stringDate1 = util.getDateAC(stringDate1, "日期");
    if (stringDate1.length() != 10) {
      message(stringDate1);
      return false;
    }
    setValue("OrderDate1", stringDate1);
    stringDate1 = getValue("OrderDate2");
    if ("".equals(stringDate1))
      return true;
    stringDate1 = util.getDateAC(stringDate1, "日期");
    if (stringDate1.length() != 10) {
      message(stringDate1);
      return false;
    }
    setValue("OrderDate2", stringDate1);
    setValue("BuyerDate2", stringDate1);
    String StringOrderDate1 = getValue("OrderDate1");
    String StringOrderDate2 = getValue("OrderDate2");
    String StringBuyerDate1 = getValue("BuyerDate1");
    String StringBuyerDate2 = getValue("BuyerDate2");
    String stringProjectID1 = getValue("ProjectID1");
    String stringSSMediaID1 = getValue("SSMediaID1");
    String stringSSMediaID1Sql = "";
    String dateType = getValue("dateType");
    
    //
    if (!"全部".equals(stringSSMediaID1)) {
      stringSSMediaID1Sql = "AND SSMediaID1='" + stringSSMediaID1 + "' ";
    }
    //
    talk dbSale = getTalk("Sale");
    String stringSQL = "";
    //
    stringSQL = " SELECT ProjectID1 " 
              + " FROM A_Sale " 
              + " WHERE " + dateType + " BETWEEN '1900/01/01' AND '" + StringOrderDate2 + "' "
              + " AND LEN(SSMediaID) > 0 " 
              + stringSSMediaID1Sql
              + " AND ProjectID1 = '" + stringProjectID1 + "' " 
              + " GROUP BY ProjectID1" + " ORDER BY ProjectID1";
    String retProjectID1[][] = dbSale.queryFromPool(stringSQL);
    stringSQL = " SELECT ProjectID " 
              + "  FROM Sale02M050 " 
              + " WHERE BuyerDate BETWEEN '1900/01/01' AND '" + StringBuyerDate2 + "'" 
              + " AND LEN(SSMediaID) > 0 "
              + " AND ProjectID = '" + stringProjectID1 + "' " 
              + " GROUP BY ProjectID" + " ORDER BY ProjectID";
    String retProjectIDA[][] = dbSale.queryFromPool(stringSQL);
    if (retProjectID1.length == 0 && retProjectIDA.length == 0) {
      message("沒有資料!");
      return false;
    }    //7*/
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
    Vector retVector = exeExcel.getExcelObject("G:\\資訊室\\Excel\\SaleEffect\\Sale01R271A.xlt");
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    //
    String stringYear = StringOrderDate1.substring(0, 4);
//    stringSQL = " speMakerSale01R251A " 
//              + "'1900/01/01' " 
//              + ",'" + StringOrderDate2 + "' "
//              + ",'1900/01/01' "
//              + ",'" + StringBuyerDate2 + "' "
//              + ",'" + stringProjectID1 + "' "
//              + ",'" + stringSSMediaID1 + "' ";

    if("OrderDate".equals(dateType)) {
      stringSQL = " speMakerSale01R251A2 " 
            + "'1900/01/01'," 
            + "'" + StringOrderDate2 + "'," 
            + "'1900/01/02'," 
            + "'9999/12/31'," 
            + "'1900/01/01'," 
            + "'" + StringBuyerDate2 + "'," 
            + "'" + stringProjectID1 + "',"
            + "'" + stringSSMediaID1 + "'";
    }else {
      stringSQL = " speMakerSale01R251A2 " 
            + "'1900/01/01'," 
            + "'9999/12/31'," 
            + "'1900/01/02'," 
            + "'" + StringOrderDate2 + "'," 
            + "'1900/01/01',"
            + "'" + StringBuyerDate2 + "'," 
            + "'" + stringProjectID1 + "',"
            + "'" + stringSSMediaID1 + "'";
    }
    
    
    String retMedia[][] = dbSale.queryFromPool(stringSQL);
    int intColumn = 4;
    int intRecordAll = 0;
    int intColumnAll = 0;
    double doubleLCOUNTHouse = 0;
    double doubleLSUMDealMoney = 0;
    String StringCOUNTHouse = "0";
    String StringSUMDealMoney = "0";
    // 案別:
    exeExcel.putDataIntoExcel(0, 1, "案別:" + stringProjectID1, objectSheet1);
    exeExcel.putDataIntoExcel(8, 1, "截至:" + StringOrderDate2, objectSheet1);
    for (int j = 0; j <= 2; j++) {
      switch (j) {
      case 0:
        StringOrderDate1 = getValue("OrderDate1");
        StringOrderDate2 = getValue("OrderDate2");
        break;
      case 1:
        StringOrderDate1 = stringYear + "/01/01";
        StringOrderDate2 = getValue("OrderDate2");
        break;
      case 2:
        StringOrderDate1 = "1900/01/01";
        StringOrderDate2 = getValue("OrderDate2");
        break;
      }
      StringBuyerDate1 = StringOrderDate1;
      StringBuyerDate2 = StringOrderDate2;
      intColumnAll = intColumn + 2 * j;
      // 費用
      int intRow = 4;
      String LMediaGroup = "";
      String MMediaGroup = "";
      String SUMDealMoney = "0";
      String COUNTHouse = "0";
      for (int i = 0; i < retMedia.length; i++) {
        // exeExcel.putDataIntoExcel(0, intRow + i, "" + (i+1),objectSheet1) ;
        String LMediaID = retMedia[i][0].trim();
        String MMediaID = retMedia[i][2].trim();
        // 大分類
        if (!LMediaGroup.equals(LMediaID)) {
          exeExcel.putDataIntoExcel(0, intRow + i, retMedia[i][1].trim(), objectSheet1);
          // 底色
          exeExcel.setBackgroundColorRange("15", "A" + (intRow + i + 1) + ":J" + (intRow + i + 1), objectSheet1);
          // 金額
          if (retProjectID1.length > 0) {
            stringSQL = " SELECT SUM(DealMoney) " 
                      + "  FROM A_Sale " 
                      + " WHERE "+ dateType +" BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'" 
                      + " AND ProjectID1 = '" + stringProjectID1 + "'" 
                      + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql 
                      + " AND SSMediaID LIKE '" + LMediaID + "%'";
          } else {
            stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " 
                      + "FROM Sale02M050 " 
                      + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' "
                      + "AND ProjectID='" + stringProjectID1 + "' " 
                      + "AND LEN(SSMediaID) > 0 " 
                      + "AND SSMediaID LIKE '" + LMediaID + "%'";
          }
          String retLMedia[][] = dbSale.queryFromPool(stringSQL);
          if (retLMedia[0][0].length() == 0)
            SUMDealMoney = "0";
          else
            SUMDealMoney = retLMedia[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          if (retProjectID1.length > 0) {
            stringSQL = " SELECT COUNT(DealMoney) " 
                      + " FROM A_Sale " 
                      + " WHERE "+ dateType +" BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'"
                      + " AND ProjectID1 = '" + stringProjectID1 + "'" 
                      + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql 
                      + " AND SSMediaID LIKE '" + LMediaID + "%'" +
                      " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
                      + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
                      + " AND DealMoney > 0 ";
          } else {
            stringSQL = "SELECT COUNT(Position) PositionCounts " + "FROM Sale02M050 T50, Sale02M051 T51 " + "WHERE T50.AgencyNo=T51.AgencyNo " + "AND T50.BuyerDate BETWEEN '"
                + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " + "AND T50.ProjectID='" + stringProjectID1 + "' " + "AND LEN(T50.SSMediaID) > 0 "
                + "AND T50.SSMediaID LIKE '" + LMediaID + "%' " + "AND T51.HouseCar='House'";
          }
          String retLMediaCount[][] = dbSale.queryFromPool(stringSQL);
          if (retLMediaCount[0][0].length() == 0)
            COUNTHouse = "0";
          else
            COUNTHouse = retLMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          // 個案加總
          doubleLCOUNTHouse += Double.parseDouble(COUNTHouse);
          doubleLSUMDealMoney += Double.parseDouble(SUMDealMoney);

          LMediaGroup = LMediaID;
          intRow++;
        }
        // 中分類
        if (!MMediaGroup.equals(MMediaID)) {
          exeExcel.putDataIntoExcel(1, intRow + i, retMedia[i][3].trim(), objectSheet1);
          // 底色
          exeExcel.setBackgroundColorRange("36", "B" + (intRow + i + 1) + ":J" + (intRow + i + 1), objectSheet1);
          // 金額
          if (retProjectID1.length > 0) {
            stringSQL = " SELECT SUM(DealMoney) " 
                      + "  FROM A_Sale " 
                      + " WHERE "+ dateType +" BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'" 
                      + " AND ProjectID1 = '" + stringProjectID1 + "'" 
                      + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql + " AND SSMediaID LIKE '" + MMediaID + "%'";
            /*
             * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
             * " WHERE SSMediaID LIKE '" + MMediaID + "%'" + " )" ;
             */
          } else {
            stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " + "FROM Sale02M050 " + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' "
                + "AND ProjectID='" + stringProjectID1 + "' " + "AND LEN(SSMediaID) > 0 " + "AND SSMediaID LIKE '" + MMediaID + "%'";
          }
          String retMMedia[][] = dbSale.queryFromPool(stringSQL);
          if (retMMedia[0][0].length() == 0)
            SUMDealMoney = "0";
          else
            SUMDealMoney = retMMedia[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          if (retProjectID1.length > 0) {
            stringSQL = " SELECT COUNT(DealMoney) " + "  FROM A_Sale " + " WHERE "+ dateType +" BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'"
                + " AND ProjectID1 = '" + stringProjectID1 + "'" + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql + " AND SSMediaID LIKE '" + MMediaID + "%'" +
                /*
                 * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
                 * " WHERE SSMediaID LIKE '" + MMediaID + "%'" + " )" +
                 */
                " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " + " AND DealMoney > 0 ";
          } else {
            stringSQL = "SELECT COUNT(Position) PositionCounts " + "FROM Sale02M050 T50, Sale02M051 T51 " + "WHERE T50.AgencyNo=T51.AgencyNo " + "AND T50.BuyerDate BETWEEN '"
                + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " + "AND T50.ProjectID='" + stringProjectID1 + "' " + "AND LEN(T50.SSMediaID) > 0 "
                + "AND T50.SSMediaID LIKE '" + MMediaID + "%' " + "AND T51.HouseCar='House'";
          }
          String retMMediaCount[][] = dbSale.queryFromPool(stringSQL);
          if (retMMediaCount[0][0].length() == 0)
            COUNTHouse = "0";
          else
            COUNTHouse = retMMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          MMediaGroup = MMediaID;
          intRow++;
        }
        // 小、細分類
        if (LMediaGroup.equals(LMediaID) && MMediaGroup.equals(MMediaID)) {
          exeExcel.putDataIntoExcel(2, intRow + i, retMedia[i][5].trim(), objectSheet1);
          exeExcel.putDataIntoExcel(3, intRow + i, retMedia[i][7].trim(), objectSheet1);
          // 金額
          if (retProjectID1.length > 0) {
            stringSQL = " SELECT SUM(DealMoney) " + "  FROM A_Sale " + " WHERE "+ dateType +" BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'" + " AND ProjectID1 = '"
                + stringProjectID1 + "'" + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql + " AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%'";
            /*
             * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
             * " WHERE SSMediaID LIKE '" + retMedia[i][6].trim() + "%'" + " )" ;
             */
          } else {
            stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " + "FROM Sale02M050 " + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' "
                + "AND ProjectID='" + stringProjectID1 + "' " + "AND LEN(SSMediaID) > 0 " + "AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%'";
          }
          String retSSMedia[][] = dbSale.queryFromPool(stringSQL);
          if (retSSMedia[0][0].length() == 0)
            SUMDealMoney = "0";
          else
            SUMDealMoney = retSSMedia[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          if (retProjectID1.length > 0) {
            stringSQL = " SELECT COUNT(DealMoney) " + "  FROM A_Sale " + " WHERE "+ dateType +" BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'"
                + " AND ProjectID1 = '" + stringProjectID1 + "'" + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql + " AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%'" +
                /*
                 * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
                 * " WHERE SSMediaID LIKE '" + retMedia[i][6].trim() + "%'" + " )" +
                 */
                " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " + " AND DealMoney > 0 ";
          } else {
            stringSQL = "SELECT COUNT(Position) PositionCounts " + "FROM Sale02M050 T50, Sale02M051 T51 " + "WHERE T50.AgencyNo=T51.AgencyNo " + "AND T50.BuyerDate BETWEEN '"
                + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " + "AND T50.ProjectID='" + stringProjectID1 + "' " + "AND LEN(T50.SSMediaID) > 0 "
                + "AND T50.SSMediaID LIKE '" + retMedia[i][6].trim() + "%' " + "AND T51.HouseCar='House'";
          }
          String retSSMediaCount[][] = dbSale.queryFromPool(stringSQL);
          if (retSSMediaCount[0][0].length() == 0)
            COUNTHouse = "0";
          else
            COUNTHouse = retSSMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
        }

        intRecordAll = intRow + i;
      }
      // 個案加總
      exeExcel.putDataIntoExcel(intColumn + 2 * j, 99, "" + doubleLCOUNTHouse, objectSheet1);
      exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, 99, "" + doubleLSUMDealMoney, objectSheet1);
      doubleLCOUNTHouse = 0;
      doubleLSUMDealMoney = 0;
    }
    exeExcel.doDeleteRows(intRecordAll + 2, 99, objectSheet1);
    exeExcel.setVisiblePropertyOnFlow(true, retVector); // 控制顯不顯示 Excel
    exeExcel.getReleaseExcelObject(retVector);
    return false;
  }

  public String getInformation() {
    return "---------------\u5217\u5370\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
