package SaleEffect.Sale01R251;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import com.jacob.activeX.*;
import com.jacob.com.*;
import Farglory.util.*;

public class FuncPrint extends bTransaction {
  FargloryUtil util = new FargloryUtil();
  String orderDate1 = "";
  String orderDate2 = "";
  String contrDate1 = "";
  String contrDate2 = "";

  // 欄位檢核
  public boolean isBatchCheckOK() throws Throwable {
    int countOC = 0; // 付訂日跟簽約日必須要有一flag
    String retDate = "";
    int countDate = 0;

    // 付訂日期
    orderDate1 = this.getValue("OrderDate1");
    if (!"".equals(orderDate1)) {
      retDate = util.getDateAC(orderDate1, "付訂日期(起)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("OrderDate1").requestFocus();
        return false;
      }
      setValue("OrderDate1", retDate);
      countDate++;
      countOC++;
    }
    orderDate2 = this.getValue("OrderDate2");
    if (!"".equals(orderDate2)) {
      retDate = util.getDateAC(orderDate2, "付訂日期(迄)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("OrderDate2").requestFocus();
        return false;
      }
      setValue("OrderDate2", retDate);
      countDate++;
    }
    if (countDate == 1) {
      message("[付訂日期(起)(迄)] 須同時限制。");
      return false;
    }

    // 簽約日期
    contrDate1 = this.getValue("ContrDate1");
    if (!"".equals(contrDate1)) {
      retDate = util.getDateAC(contrDate1, "簽約日期(起)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("ContrDate1").requestFocus();
        return false;
      }
      setValue("ContrDate1", retDate);
      countDate++;
      countOC++;
    }
    contrDate2 = this.getValue("ContrDate1");
    if (!"".equals(contrDate2)) {
      retDate = util.getDateAC(contrDate2, "簽約日期(迄)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("ContrDate1").requestFocus();
        return false;
      }
      setValue("ContrDate1", retDate);
      countDate++;
    }
    if (countDate == 1) {
      message("[簽約日期(起)(迄)] 須同時限制。");
      return false;
    }

    // 付訂與簽約需擇一或共有
    if (countOC == 0) {
      message("[付訂日期] 與 [簽約日期] 必須擇一以上填寫");
      return false;
    }

    setValue("BuyerDate1", !"".equals(orderDate1) ? orderDate1 : contrDate1);
    setValue("BuyerDate2", !"".equals(orderDate2) ? orderDate2 : contrDate2);

    return true;
  }

  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一

    // 檢核
    if (!this.isBatchCheckOK()) return false;

    String StringOrderDate1 = getValue("OrderDate1");
    String StringOrderDate2 = getValue("OrderDate2");
    String strContrDate1 = getValue("ContrDate1");
    String strContrDate2 = getValue("ContrDate2");
    String StringBuyerDate1 = getValue("BuyerDate1");
    String StringBuyerDate2 = getValue("BuyerDate2");
    String stringSSMediaID1 = getValue("SSMediaID1");
    String stringSSMediaID1Sql = "";
    //
    if (!"全部".equals(stringSSMediaID1)) {
      stringSSMediaID1Sql = " AND SSMediaID1='" + stringSSMediaID1 + "' ";
    }
    //
    talk dbSale = getTalk("Sale");
    String stringSQL = "";
    //
    stringSQL = " SELECT ProjectID1 " 
              + " FROM A_Sale " 
              + " WHERE 1=1 ";
    if( !"".equals(StringOrderDate1) ) stringSQL += " AND OrderDate BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' ";
    if( !"".equals(strContrDate1) ) stringSQL += " AND ContrDate BETWEEN '" + strContrDate1 + "' AND '" + strContrDate2 + "' ";
    stringSQL += " AND LEN(SSMediaID) > 0 "
              + stringSSMediaID1Sql 
              + " GROUP BY ProjectID1" + " ORDER BY ProjectID1";
    String retProjectID1[][] = dbSale.queryFromPool(stringSQL);
    //
    stringSQL = " SELECT ProjectID " 
              + " FROM Sale02M050 " 
              + " WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "'" 
              + " AND LEN(SSMediaID) > 0 "
              + " GROUP BY ProjectID" + " ORDER BY ProjectID";
    String retProjectIDA[][] = dbSale.queryFromPool(stringSQL);
    if (retProjectID1.length == 0 && retProjectIDA.length == 0) {
      message("沒有資料!");
      return false;
    }
    //
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
    Vector retVector = exeExcel.getExcelObject("G:\\資訊室\\Excel\\SaleEffect\\Sale01R251A.xltX");
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    //
    stringSQL = " speMakerSale01R251A2 " 
              + "'" + StringOrderDate1 + "'," 
              + "'" + StringOrderDate2 + "',"
              + "'" + strContrDate1 + "'," 
              + "'" + strContrDate2 + "',"
              + "'" + StringBuyerDate1 + "'," 
              + "'" + StringBuyerDate2 + "'," 
              + "'',"
              + "'" + stringSSMediaID1 + "' ";
    String retMedia[][] = dbSale.queryFromPool(stringSQL);
    int intColumn = 4;
    int intRecordAll = 0;
    int intColumnAll = 0;
    double doubleLCOUNTHouse = 0;
    double doubleLSUMDealMoney = 0;
    String StringCOUNTHouse = "0";
    String StringSUMDealMoney = "0";
    int intColumnA = 406;
    int intColumnAllA = 0;
    double doubleLCOUNTHouseA = 0;
    double doubleLSUMDealMoneyA = 0;
    String StringCOUNTHouseA = "0";
    String StringSUMDealMoneyA = "0";
    exeExcel.getDataFromExcel2(1, 1, objectSheet1);
    String stringDate = "";
    if ( !"".equals(StringOrderDate1) ) stringDate += "付訂日期:" + StringOrderDate1 + "～" + StringOrderDate1;
    if ( !"".equals(strContrDate1) ) stringDate += (stringDate.length()==0? "":"    //    ") + "簽約日期:" + strContrDate1 + "～" + strContrDate2;
    // 日期
    exeExcel.putDataIntoExcel(0, 1, stringDate, objectSheet1);
    
    // 銷售
    for (int j = 0; j < retProjectID1.length; j++) {
      String stringProjectID1 = retProjectID1[j][0].trim();
      // System.out.println("stringProjectID1="+stringProjectID1);
      exeExcel.putDataIntoExcel(intColumn + 2 * j, 2, stringProjectID1, objectSheet1);
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
        // System.out.println("LMediaID="+LMediaID);
        // 大分類
        if (!LMediaGroup.equals(LMediaID)) {
          exeExcel.putDataIntoExcel(0, intRow + i, retMedia[i][1].trim(), objectSheet1);
          // 底色
          exeExcel.setBackgroundColorRange("15", "A" + (intRow + i + 1) + ":SP" + (intRow + i + 1), objectSheet1);
          // 金額
          stringSQL = " SELECT SUM(DealMoney) " 
                    + " FROM A_Sale "
                    + " WHERE 1=1"; 
          if( !"".equals(StringOrderDate1) ) stringSQL += " AND OrderDate BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' ";
          if( !"".equals(strContrDate1) ) stringSQL += " AND ContrDate BETWEEN '" + strContrDate1 + "' AND '" + strContrDate2 + "' ";
          stringSQL += " AND ProjectID1 = '" + stringProjectID1 + "'" 
                    + " AND LEN(SSMediaID) > 0 " 
                    + stringSSMediaID1Sql 
                    + " AND SSMediaID LIKE  '" + LMediaID + "%' ";
          /*
           * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
           * " WHERE SSMediaID LIKE '" + LMediaID + "%'" + " )" ;
           */
          String retLMedia[][] = dbSale.queryFromPool(stringSQL);
          if (retLMedia[0][0].length() == 0)
            SUMDealMoney = "0";
          else
            SUMDealMoney = retLMedia[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          stringSQL = " SELECT COUNT(DealMoney) " 
                    + "  FROM A_Sale " 
                    + " WHERE 1=1"; 
          if( !"".equals(StringOrderDate1) ) stringSQL += " AND OrderDate BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' ";
          if( !"".equals(strContrDate1) ) stringSQL += " AND ContrDate BETWEEN '" + strContrDate1 + "' AND '" + strContrDate2 + "' "; 
          stringSQL += " AND ProjectID1 = '" + stringProjectID1 + "'" 
                    + " AND LEN(SSMediaID) > 0 " 
                    + stringSSMediaID1Sql 
                    + " AND SSMediaID LIKE  '" + LMediaID + "%' " 
                    + " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
                    + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " + " AND DealMoney > 0 ";
          String retLMediaCount[][] = dbSale.queryFromPool(stringSQL);
          if (retLMediaCount[0][0].length() == 0)
            COUNTHouse = "0";
          else
            COUNTHouse = retLMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          // 個案加總
          doubleLCOUNTHouse += Double.parseDouble(COUNTHouse);
          doubleLSUMDealMoney += Double.parseDouble(SUMDealMoney);

          // 右銷售合計
          StringCOUNTHouse = exeExcel.getDataFromExcel2(404, intRow + i, objectSheet1);
          if (StringCOUNTHouse.length() == 0)
            StringCOUNTHouse = "0";
          exeExcel.putDataIntoExcel(404, intRow + i, "" + (Double.parseDouble(StringCOUNTHouse) + Double.parseDouble(COUNTHouse)), objectSheet1);
          StringSUMDealMoney = exeExcel.getDataFromExcel2(405, intRow + i, objectSheet1);
          if (StringSUMDealMoney.length() == 0)
            StringSUMDealMoney = "0";
          exeExcel.putDataIntoExcel(405, intRow + i, "" + (Double.parseDouble(StringSUMDealMoney) + Double.parseDouble(SUMDealMoney)), objectSheet1);

          LMediaGroup = LMediaID;
          intRow++;
        }
        // 中分類
        if (!MMediaGroup.equals(MMediaID)) {
          exeExcel.putDataIntoExcel(1, intRow + i, retMedia[i][3].trim(), objectSheet1);
          // 底色
          exeExcel.setBackgroundColorRange("36", "B" + (intRow + i + 1) + ":SP" + (intRow + i + 1), objectSheet1);
          // 金額
          stringSQL = " SELECT SUM(DealMoney) " 
                    + " FROM A_Sale " 
                    + " WHERE 1=1"; 
          if( !"".equals(StringOrderDate1) ) stringSQL += " AND OrderDate BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' ";
          if( !"".equals(strContrDate1) ) stringSQL += " AND ContrDate BETWEEN '" + strContrDate1 + "' AND '" + strContrDate2 + "' "; 
          stringSQL += " AND ProjectID1 = '" + stringProjectID1 + "'" 
                    + " AND LEN(SSMediaID) > 0 " 
                    + stringSSMediaID1Sql 
                    + " AND SSMediaID LIKE '" + MMediaID + "%' ";
          String retMMedia[][] = dbSale.queryFromPool(stringSQL);
          if (retMMedia[0][0].length() == 0)
            SUMDealMoney = "0";
          else
            SUMDealMoney = retMMedia[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          stringSQL = " SELECT COUNT(DealMoney) " 
                    + "  FROM A_Sale " 
                    + " WHERE 1=1"; 
          if( !"".equals(StringOrderDate1) ) stringSQL += " AND OrderDate BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' ";
          if( !"".equals(strContrDate1) ) stringSQL += " AND ContrDate BETWEEN '" + strContrDate1 + "' AND '" + strContrDate2 + "' "; 
          stringSQL += " AND ProjectID1 = '" + stringProjectID1 + "'" 
                    + " AND LEN(SSMediaID) > 0 " 
                    + stringSSMediaID1Sql 
                    + " AND SSMediaID LIKE '" + MMediaID + "%' " 
                    + " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
                    + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " + " AND DealMoney > 0 ";
          String retMMediaCount[][] = dbSale.queryFromPool(stringSQL);
          if (retMMediaCount[0][0].length() == 0)
            COUNTHouse = "0";
          else
            COUNTHouse = retMMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          // 右銷售合計
          StringCOUNTHouse = exeExcel.getDataFromExcel2(404, intRow + i, objectSheet1);
          if (StringCOUNTHouse.length() == 0)
            StringCOUNTHouse = "0";
          exeExcel.putDataIntoExcel(404, intRow + i, "" + (Double.parseDouble(StringCOUNTHouse) + Double.parseDouble(COUNTHouse)), objectSheet1);
          StringSUMDealMoney = exeExcel.getDataFromExcel2(405, intRow + i, objectSheet1);
          if (StringSUMDealMoney.length() == 0)
            StringSUMDealMoney = "0";
          exeExcel.putDataIntoExcel(405, intRow + i, "" + (Double.parseDouble(StringSUMDealMoney) + Double.parseDouble(SUMDealMoney)), objectSheet1);
          MMediaGroup = MMediaID;
          intRow++;
        }
        // 小、細分類
        if (LMediaGroup.equals(LMediaID) && MMediaGroup.equals(MMediaID)) {
          exeExcel.putDataIntoExcel(2, intRow + i, retMedia[i][5].trim(), objectSheet1);
          exeExcel.putDataIntoExcel(3, intRow + i, retMedia[i][7].trim(), objectSheet1);
          // 金額
          stringSQL = " SELECT SUM(DealMoney) " 
                    + " FROM A_Sale " 
                    + " WHERE 1=1"; 
          if( !"".equals(StringOrderDate1) ) stringSQL += " AND OrderDate BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' ";
          if( !"".equals(strContrDate1) ) stringSQL += " AND ContrDate BETWEEN '" + strContrDate1 + "' AND '" + strContrDate2 + "' "; 
          stringSQL += " AND ProjectID1 = '" + stringProjectID1 + "'" 
                    + " AND LEN(SSMediaID) > 0 " 
                    + stringSSMediaID1Sql 
                    + " AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%' ";
          /*
           * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
           * " WHERE SSMediaID LIKE '" + retMedia[i][6].trim() + "%'" + " )" ;
           */
          String retSSMedia[][] = dbSale.queryFromPool(stringSQL);
          if (retSSMedia[0][0].length() == 0)
            SUMDealMoney = "0";
          else
            SUMDealMoney = retSSMedia[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          stringSQL = " SELECT COUNT(DealMoney) " 
                    + "  FROM A_Sale " 
                    + " WHERE 1=1"; 
          if( !"".equals(StringOrderDate1) ) stringSQL += " AND OrderDate BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' ";
          if( !"".equals(strContrDate1) ) stringSQL += " AND ContrDate BETWEEN '" + strContrDate1 + "' AND '" + strContrDate2 + "' "; 
          stringSQL += " AND ProjectID1 = '" + stringProjectID1 + "'" 
                    + " AND LEN(SSMediaID) > 0 " 
                    + stringSSMediaID1Sql 
                    + " AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%' " 
                    + " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
                    + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
                    + " AND DealMoney > 0 ";
          String retSSMediaCount[][] = dbSale.queryFromPool(stringSQL);
          if (retSSMediaCount[0][0].length() == 0)
            COUNTHouse = "0";
          else
            COUNTHouse = retSSMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          // 右銷售合計
          StringCOUNTHouse = exeExcel.getDataFromExcel2(404, intRow + i, objectSheet1);
          if (StringCOUNTHouse.length() == 0)
            StringCOUNTHouse = "0";
          exeExcel.putDataIntoExcel(404, intRow + i, "" + (Double.parseDouble(StringCOUNTHouse) + Double.parseDouble(COUNTHouse)), objectSheet1);
          StringSUMDealMoney = exeExcel.getDataFromExcel2(405, intRow + i, objectSheet1);
          if (StringSUMDealMoney.length() == 0)
            StringSUMDealMoney = "0";
          exeExcel.putDataIntoExcel(405, intRow + i, "" + (Double.parseDouble(StringSUMDealMoney) + Double.parseDouble(SUMDealMoney)), objectSheet1);
        }

        intRecordAll = intRow + i;
      }
      // 個案加總
      exeExcel.putDataIntoExcel(intColumn + 2 * j, 204, "" + doubleLCOUNTHouse, objectSheet1);
      exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, 204, "" + doubleLSUMDealMoney, objectSheet1);
      // 右銷售合計 & 個案加總
      StringCOUNTHouse = exeExcel.getDataFromExcel2(404, 204, objectSheet1);
      if (StringCOUNTHouse.length() == 0)
        StringCOUNTHouse = "0";
      exeExcel.putDataIntoExcel(404, 204, "" + (Double.parseDouble(StringCOUNTHouse) + doubleLCOUNTHouse), objectSheet1);
      StringSUMDealMoney = exeExcel.getDataFromExcel2(405, 204, objectSheet1);
      if (StringSUMDealMoney.length() == 0)
        StringSUMDealMoney = "0";
      exeExcel.putDataIntoExcel(405, 204, "" + (Double.parseDouble(StringSUMDealMoney) + doubleLSUMDealMoney), objectSheet1);
      doubleLCOUNTHouse = 0;
      doubleLSUMDealMoney = 0;
    }
    // 仲介
    for (int j = 0; j < retProjectIDA.length; j++) {
      String stringProjectID = retProjectIDA[j][0].trim();
      exeExcel.putDataIntoExcel(intColumnA + 2 * j, 2, stringProjectID, objectSheet1);
      intColumnAllA = intColumnA + 2 * j;
      // 費用
      int intRow = 4;
      String LMediaGroup = "";
      String MMediaGroup = "";
      String SUMDealMoney = "0";
      String COUNTHouse = "0";
      for (int i = 0; i < retMedia.length; i++) {
        String LMediaID = retMedia[i][0].trim();
        String MMediaID = retMedia[i][2].trim();
        // 大分類
        if (!LMediaGroup.equals(LMediaID)) {
          exeExcel.putDataIntoExcel(0, intRow + i, retMedia[i][1].trim(), objectSheet1);
          // 底色
          exeExcel.setBackgroundColorRange("15", "A" + (intRow + i + 1) + ":SP" + (intRow + i + 1), objectSheet1);
          // 金額
          stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " 
                    + "FROM Sale02M050 " 
                    + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' "
                    + "AND ProjectID='" + stringProjectID + "' " 
                    + "AND LEN(SSMediaID) > 0 " 
                    + "AND SSMediaID LIKE '" + LMediaID + "%' ";
          String retLMedia[][] = dbSale.queryFromPool(stringSQL);
          SUMDealMoney = retLMedia[0][0];
          exeExcel.putDataIntoExcel(intColumnA + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          stringSQL = "SELECT COUNT(Position) PositionCounts " 
                    + "FROM Sale02M050 T50, Sale02M051 T51 " 
                    + "WHERE T50.AgencyNo=T51.AgencyNo " + "AND T50.BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " 
                    + "AND T50.ProjectID='" + stringProjectID + "' " 
                    + "AND LEN(T50.SSMediaID) > 0 " 
                    + "AND T50.SSMediaID LIKE '"
              + LMediaID + "%' " + "AND T51.HouseCar='House'";
          String retLMediaCount[][] = dbSale.queryFromPool(stringSQL);
          COUNTHouse = retLMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumnA + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          // 個案加總
          doubleLCOUNTHouseA += Double.parseDouble(COUNTHouse);
          doubleLSUMDealMoneyA += Double.parseDouble(SUMDealMoney);
          // 右仲介合計
          StringCOUNTHouseA = exeExcel.getDataFromExcel2(506, intRow + i, objectSheet1);
          if (StringCOUNTHouseA.length() == 0)
            StringCOUNTHouseA = "0";
          exeExcel.putDataIntoExcel(506, intRow + i, "" + (Double.parseDouble(StringCOUNTHouseA) + Double.parseDouble(COUNTHouse)), objectSheet1);
          StringSUMDealMoneyA = exeExcel.getDataFromExcel2(507, intRow + i, objectSheet1);
          if (StringSUMDealMoneyA.length() == 0)
            StringSUMDealMoneyA = "0";
          exeExcel.putDataIntoExcel(507, intRow + i, "" + (Double.parseDouble(StringSUMDealMoneyA) + Double.parseDouble(SUMDealMoney)), objectSheet1);
          //
          LMediaGroup = LMediaID;
          intRow++;
        }
        // 中分類
        if (!MMediaGroup.equals(MMediaID)) {
          exeExcel.putDataIntoExcel(1, intRow + i, retMedia[i][3].trim(), objectSheet1);
          // 底色
          exeExcel.setBackgroundColorRange("36", "A" + (intRow + i + 1) + ":SP" + (intRow + i + 1), objectSheet1);
          // 金額
          stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " 
                    + "FROM Sale02M050 " 
                    + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' "
                    + "AND ProjectID='" + stringProjectID + "' " 
                    + "AND LEN(SSMediaID) > 0 " 
                    + "AND SSMediaID LIKE '" + MMediaID + "%'";
          String retMMedia[][] = dbSale.queryFromPool(stringSQL);
          SUMDealMoney = retMMedia[0][0];
          exeExcel.putDataIntoExcel(intColumnA + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          stringSQL = "SELECT COUNT(Position) PositionCounts " 
                    + "FROM Sale02M050 T50, Sale02M051 T51 " 
                    + "WHERE T50.AgencyNo=T51.AgencyNo " + "AND T50.BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " 
                    + "AND T50.ProjectID='" + stringProjectID + "' " 
                    + "AND LEN(T50.SSMediaID) > 0 " 
                    + "AND T50.SSMediaID LIKE '"
              + MMediaID + "%' " + "AND T51.HouseCar='House'";
          String retMMediaCount[][] = dbSale.queryFromPool(stringSQL);
          COUNTHouse = retMMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumnA + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          // 右仲介合計
          StringCOUNTHouseA = exeExcel.getDataFromExcel2(506, intRow + i, objectSheet1);
          if (StringCOUNTHouseA.length() == 0)
            StringCOUNTHouseA = "0";
          exeExcel.putDataIntoExcel(506, intRow + i, "" + (Double.parseDouble(StringCOUNTHouseA) + Double.parseDouble(COUNTHouse)), objectSheet1);
          StringSUMDealMoneyA = exeExcel.getDataFromExcel2(507, intRow + i, objectSheet1);
          if (StringSUMDealMoneyA.length() == 0)
            StringSUMDealMoneyA = "0";
          exeExcel.putDataIntoExcel(507, intRow + i, "" + (Double.parseDouble(StringSUMDealMoneyA) + Double.parseDouble(SUMDealMoney)), objectSheet1);
          //
          MMediaGroup = MMediaID;
          intRow++;
        }
        // 小、細分類
        if (LMediaGroup.equals(LMediaID) && MMediaGroup.equals(MMediaID)) {
          exeExcel.putDataIntoExcel(2, intRow + i, retMedia[i][5].trim(), objectSheet1);
          exeExcel.putDataIntoExcel(3, intRow + i, retMedia[i][7].trim(), objectSheet1);
          // 金額
          stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " 
                    + "FROM Sale02M050 " 
                    + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' "
                    + "AND ProjectID='" + stringProjectID + "' " 
                    + "AND LEN(SSMediaID) > 0 " 
                    + "AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%'";
          String retSSMedia[][] = dbSale.queryFromPool(stringSQL);
          SUMDealMoney = retSSMedia[0][0];
          exeExcel.putDataIntoExcel(intColumnA + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
          // 戶數
          stringSQL = "SELECT COUNT(Position) PositionCounts " 
                    + "FROM Sale02M050 T50, Sale02M051 T51 " 
                    + "WHERE T50.AgencyNo=T51.AgencyNo " + "AND T50.BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " 
                    + "AND T50.ProjectID='" + stringProjectID + "' " 
                    + "AND LEN(T50.SSMediaID) > 0 " 
                    + "AND T50.SSMediaID LIKE '" + retMedia[i][6].trim() + "%' " 
                    + "AND T51.HouseCar='House'";
          String retSSMediaCount[][] = dbSale.queryFromPool(stringSQL);
          COUNTHouse = retSSMediaCount[0][0];
          exeExcel.putDataIntoExcel(intColumnA + 2 * j, intRow + i, COUNTHouse, objectSheet1);
          // 右仲介合計
          StringCOUNTHouseA = exeExcel.getDataFromExcel2(506, intRow + i, objectSheet1);
          if (StringCOUNTHouseA.length() == 0)
            StringCOUNTHouseA = "0";
          exeExcel.putDataIntoExcel(506, intRow + i, "" + (Double.parseDouble(StringCOUNTHouseA) + Double.parseDouble(COUNTHouse)), objectSheet1);
          StringSUMDealMoneyA = exeExcel.getDataFromExcel2(507, intRow + i, objectSheet1);
          if (StringSUMDealMoneyA.length() == 0)
            StringSUMDealMoneyA = "0";
          exeExcel.putDataIntoExcel(507, intRow + i, "" + (Double.parseDouble(StringSUMDealMoneyA) + Double.parseDouble(SUMDealMoney)), objectSheet1);
        }
        //
        intRecordAll = intRow + i;
      }
      // 個案加總
      exeExcel.putDataIntoExcel(intColumnA + 2 * j, 204, "" + doubleLCOUNTHouseA, objectSheet1);
      exeExcel.putDataIntoExcel(intColumnA + 2 * j + 1, 204, "" + doubleLSUMDealMoneyA, objectSheet1);
      // 右仲介合計 & 個案加總
      StringCOUNTHouseA = exeExcel.getDataFromExcel2(506, 204, objectSheet1);
      if (StringCOUNTHouseA.length() == 0)
        StringCOUNTHouseA = "0";
      exeExcel.putDataIntoExcel(506, 204, "" + (Double.parseDouble(StringCOUNTHouseA) + doubleLCOUNTHouseA), objectSheet1);
      StringSUMDealMoneyA = exeExcel.getDataFromExcel2(507, 204, objectSheet1);
      if (StringSUMDealMoneyA.length() == 0)
        StringSUMDealMoneyA = "0";
      exeExcel.putDataIntoExcel(507, 204, "" + (Double.parseDouble(StringSUMDealMoneyA) + doubleLSUMDealMoneyA), objectSheet1);
      doubleLCOUNTHouseA = 0;
      doubleLSUMDealMoneyA = 0;
    }
    // 刪除多餘列
    exeExcel.doDeleteRows(intRecordAll + 2, 204, objectSheet1);
    System.out.println("intColumnAllA=" + intColumnAllA);
    System.out.println("intColumnAll=" + intColumnAll);
    // 刪除仲介多餘欄
    if (intColumnAllA == 0) {
      intColumnAllA = 404;
    }
    for (int k = intColumnAllA + 2; k <= 505; k++) {
      exeExcel.doDeleteColumns(intColumnAllA + 2, objectSheet1);
    }
    // 刪除銷售多餘欄
    for (int k = intColumnAll + 2; k <= 403; k++) {
      exeExcel.doDeleteColumns(intColumnAll + 2, objectSheet1);
    }
    //
    Dispatch objectRange = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    Dispatch.call(objectRange, "select");
    //
    exeExcel.setVisiblePropertyOnFlow(true, retVector); // 控制顯不顯示 Excel
    exeExcel.getReleaseExcelObject(retVector);
    return false;
  }

  public String getInformation() {
    return "---------------\u5217\u5370\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
