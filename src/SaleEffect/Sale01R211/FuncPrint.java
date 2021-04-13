package SaleEffect.Sale01R211;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import com.jacob.activeX.*;
import com.jacob.com.*;

public class FuncPrint extends bTransaction {
  public boolean action(String value) throws Throwable {
    talk dbSale = getTalk("" + get("put_dbSale"));
    String stringDate = getValue("Date").trim().replace("/", "");
    String stringProjectID = getValue("ProjectID").trim();
    String dateType = this.getValue("dateType").trim();
    String stringSQL = "";
    String retData[][] = null;
    if (stringProjectID.length() == 0) {
      message("[案別] 不可為空!");
      getcLabel("ProjectID").requestFocus();
      return false;
    }
    stringSQL = "SELECT ProjectID FROM A_Project WHERE ProjectID = '" + stringProjectID + "' ";
    retData = dbSale.queryFromPool(stringSQL);
    if (retData.length == 0) {
      message("[案別] 錯誤!");
      getcLabel("ProjectID").requestFocus();
      return false;
    }
    // 檢核日期
    if (stringDate.length() == 0) {
      message("[年月] 不可為空白!");
      getcLabel("Date").requestFocus();
      return false;
    }
    if (stringDate.length() != 6) {
      message("[年月] 日期格式錯誤(YYYY/MM)");
      getcLabel("Date").requestFocus();
      return false;
    }
    if (!check.isDate(stringDate, "YYYYmm")) {
      message("[年月] 日期格式錯誤(YYYY/MM)");
      getcLabel("Date").requestFocus();
      return false;
    }
    setValue("Date", stringDate.substring(0, 4) + "/" + stringDate.substring(4));
    
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
    String stringFilePath = "G:\\資訊室\\Excel\\SaleEffect\\Sale01R211.xlt";
    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    String stringMonthStart = "";
    String stringMonthEnd = "";
    String stringOriginStart = "1990/01/01";
    String stringBeforeMonthStart = "";
    String stringBeforeMonthEnd = "";
    String stringYear = stringDate.substring(0, 4);
    String stringMonth = stringDate.substring(4, 6);
    String stringRate1 = "";
    String stringRate2 = "";
    int intMonth = 0;
    String stringMonthNo = "";
    float[] floatTarget = new float[3];
    intMonth = Integer.parseInt(stringMonth);
    exeExcel.putDataIntoExcel(2, 1, stringProjectID, objectSheet1);
    exeExcel.putDataIntoExcel(17, 1, stringMonth, objectSheet1);
    /**** 接待中心 ****/
    stringSQL = "SELECT DemoRoomMOney FROM A_Project WHERE ProjectID = '" + stringProjectID + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(2, 48, retData[0][0], objectSheet1);
    String stringDemoRoomMoney = retData[0][0];
    /******* 目標 *******/
    for (int intMonthNo = 1; intMonthNo <= 12; intMonthNo++) {
      if (intMonthNo < 10) {
        stringMonthNo = "0" + intMonthNo;
      } else {
        stringMonthNo = "" + intMonthNo;
      }
      stringMonthStart = stringYear + "/" + stringMonthNo + "/01";
      for (int day = 31; day >= 28; day--) {
        if (check.isDate(stringYear + stringMonthNo + day, "YYYYmmdd")) {
          stringMonthEnd = stringYear + "/" + stringMonthNo + "/" + day;
          break;
        }
      }
      message(stringMonthEnd);
      // (5,intMonthNo)業績.目標
      stringSQL = " sp1SaleTarget2 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 4, retData[0][0], objectSheet1);
      // (16,intMonthNo) 人力.目標(總人力)
      stringSQL = " sp2ManTarget " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'," + "'1'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 15, retData[0][0], objectSheet1);
      // (21,intMonthNo) 人力.目標(直接)
      stringSQL = " sp2ManTarget " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'," + "'2'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 20, retData[0][0], objectSheet1);
    }
    for (int intMonthNo = 1; intMonthNo <= intMonth; intMonthNo++) {
      if (intMonthNo == 1)
        stringMonthNo = "01";
      if (intMonthNo == 2)
        stringMonthNo = "02";
      if (intMonthNo == 3)
        stringMonthNo = "03";
      if (intMonthNo == 4)
        stringMonthNo = "04";
      if (intMonthNo == 5)
        stringMonthNo = "05";
      if (intMonthNo == 6)
        stringMonthNo = "06";
      if (intMonthNo == 7)
        stringMonthNo = "07";
      if (intMonthNo == 8)
        stringMonthNo = "08";
      if (intMonthNo == 9)
        stringMonthNo = "09";
      if (intMonthNo == 10)
        stringMonthNo = "10";
      if (intMonthNo == 11)
        stringMonthNo = "11";
      if (intMonthNo == 12)
        stringMonthNo = "12";
      stringMonthStart = stringYear + "/" + stringMonthNo + "/01";
      for (int day = 31; day >= 28; day--) {
        if (check.isDate(stringYear + stringMonthNo + day, "YYYYmmdd")) {
          stringMonthEnd = stringYear + "/" + stringMonthNo + "/" + day;
          break;
        }
      }
      // 費用率
      stringSQL = "SELECT CONVERT(decimal(6,4),CoProject/100) AS Rate1," + " CONVERT(decimal(6,4),CoComm/100) AS Rate2 " + " FROM A_Project1 " + " WHERE StartDate <= '"
          + stringMonthStart + "'" + " AND EndDate >= '" + stringMonthStart + "'" + " AND ProjectID = '" + stringProjectID + "'";
      retData = dbSale.queryFromPool(stringSQL);
      if (retData.length > 0) {
        for (int i = 0; i < retData.length; i++) {
          stringRate1 = retData[0][0];
          stringRate2 = retData[0][1];
        }
      }
      // 去年同期
      stringBeforeMonthStart = (Integer.parseInt(stringYear) - 1) + stringMonthStart.substring(4, 10);
      stringBeforeMonthEnd = (Integer.parseInt(stringYear) - 1) + stringMonthEnd.substring(4, 10);
      // (5,?)業績.目標
      /*
       * stringSQL = " sp1SaleTarget2 " + "'" + stringProjectID + "'," + "'" +
       * stringMonthStart + "'," + "'" + stringMonthEnd + "'"; Vector vectorSP5 =
       * databaseSale.select(stringSQL,stringClass); Family beanFamilySP5 = null;
       * beanFamilySP5 = (Family) vectorSP5.get(0); floatTarget[0] +=
       * Float.parseFloat(beanFamilySP5.get(""));
       */
      // (6,?)業績.出售
      stringSQL = " sp1SaleReal2_ProjectID1_2021 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 5, retData[0][0], objectSheet1);
      // (7,?)業績.出租
      stringSQL = " sp1SaleReal3_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 6, retData[0][0], objectSheet1);
      // (10,?)去年同期.業績
      stringSQL = " sp1SaleReal_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringBeforeMonthStart + "'," + "'" + stringBeforeMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 9, retData[0][0], objectSheet1);
      // (12,?)業績.超價
      stringSQL = " sp1SaleBala_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 11, retData[0][0], objectSheet1);
      // (14,?)業績.佣金
      stringSQL = " sp1SaleComm_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 13, retData[0][0], objectSheet1);
      // (21,?) 人力.目標(總人力)
      /*
       * stringSQL = " sp2ManTarget " + "'" + stringProjectID + "'," + "'" +
       * stringMonthStart + "'," + "'" + stringMonthEnd + "'," + "'1'"; String
       * retData7[][] = dbSale.queryFromPool(stringSQL);
       */
      // (17,?) 人力.實際(總人力)
      stringSQL = " sp2ManReal1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 16, retData[0][0], objectSheet1);
      // (19,?) 人力.實際.(總人力)(去年同期)
      stringSQL = " sp2ManReal1 " + "'" + stringProjectID + "'," + "'" + stringBeforeMonthStart + "'," + "'" + stringBeforeMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 18, retData[0][0], objectSheet1);
      // (21,?) 人力.目標(直接)
      /*
       * stringSQL = " sp2ManTarget " + "'" + stringProjectID + "'," + "'" +
       * stringMonthStart + "'," + "'" + stringMonthEnd + "'," + "'2'"; Vector
       * vectorSP21 = databaseSale.select(stringSQL,stringClass); Family
       * beanFamilySP21 = null; beanFamilySP21 = (Family) vectorSP21.get(0);
       * floatTarget[2] += Float.parseFloat(beanFamilySP21.get(""));
       */
      // (22,?) 人力.實際(直接)
      stringSQL = " sp2ManReal2 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 21, retData[0][0], objectSheet1);
      // (23,?) 人力.實際.直接(去年同期)
      stringSQL = " sp2ManReal2 " + "'" + stringProjectID + "'," + "'" + stringBeforeMonthStart + "'," + "'" + stringBeforeMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 22, retData[0][0], objectSheet1);
      // (41,?) 實績預算.管理
      stringSQL = " sp4RealBudget_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'," + "'1'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 41, retData[0][0], objectSheet1);
      // (42,?) 成本.管理
      stringSQL = " sp4Cost1_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 42, retData[0][0], objectSheet1);
      // (45,?) 成本.管理.去年同期
      stringSQL = " sp4Cost1_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringBeforeMonthStart + "'," + "'" + stringBeforeMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 45, retData[0][0], objectSheet1);
      // (46,?) 實績預算.廣告
      stringSQL = " sp4RealBudget_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'," + "'2'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 46, retData[0][0], objectSheet1);
      // (50,?) 成本.廣告.去年同期
      stringSQL = " sp4Cost2_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringBeforeMonthStart + "'," + "'" + stringBeforeMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 50, retData[0][0], objectSheet1);
      // (50,?) 企劃成本.接待中心
      if (stringDemoRoomMoney.length() > 0) {
        stringSQL = "SELECT SUM(realmoney) " + "FROM z_coreamm " + "WHERE ProjectID1 = '" + stringProjectID + "' " + "AND RTRIM(CostID)+RTRIM(CostID1) IN ('701','710','730') "
            + "AND YYMM BETWEEN '" + stringMonthStart + "' AND '" + stringMonthEnd + "' ";
        retData = dbSale.queryFromPool(stringSQL);
        if (retData[0][0].equals("")) {
          exeExcel.putDataIntoExcel(4 + intMonthNo, 51, "0", objectSheet1);
        } else {
          exeExcel.putDataIntoExcel(4 + intMonthNo, 51, retData[0][0], objectSheet1);
        }
        float floatRealMoney = 0;
        if (!retData[0][0].equals("")) {
          floatRealMoney = Float.parseFloat(retData[0][0]) * 1000;
        }
        // (47,?) 成本.廣告
        stringSQL = " sp4Cost2_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
        retData = dbSale.queryFromPool(stringSQL);
        float floatCost = 0;
        if (!retData[0][0].equals("")) {
          floatCost = Float.parseFloat(retData[0][0]) * 1000;
          floatCost = (floatCost - floatRealMoney) / 1000;
        }
        exeExcel.putDataIntoExcel(4 + intMonthNo, 47, "" + floatCost, objectSheet1);
      } else {
        // (47,?) 成本.廣告
        stringSQL = " sp4Cost2_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
        retData = dbSale.queryFromPool(stringSQL);
        exeExcel.putDataIntoExcel(4 + intMonthNo, 47, retData[0][0], objectSheet1);
      }
      // 銷售戶數
      stringSQL = " spSale01R210_SaleCount_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 58, retData[0][0], objectSheet1);
      // 銷售戶數 去年同期
      stringSQL = " spSale01R210_SaleCount_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringBeforeMonthStart + "'," + "'" + stringBeforeMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 60, retData[0][0], objectSheet1);
      // 退戶率
      stringSQL = " spSale01R210_DelMoney_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringMonthStart + "'," + "'" + stringMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 62, retData[0][0], objectSheet1);
      // 退戶率 去年同期
      stringSQL = " spSale01R210_DelMoney_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringBeforeMonthStart + "'," + "'" + stringBeforeMonthEnd + "'";
      retData = dbSale.queryFromPool(stringSQL);
      exeExcel.putDataIntoExcel(4 + intMonthNo, 64, retData[0][0], objectSheet1);
    }
    /************** 本月平均 **************/
    // 業績-目標
    /*
     * out.print(".Cells(5,19).Value = "); out.print("\"");
     * out.print(floatTarget[0]/intMonth); out.println("\""); //人力.目標(總人力)
     * out.print(".Cells(16,19).Value = "); out.print("\"");
     * out.print(floatTarget[1]/intMonth); out.println("\""); //人力-目標(直接)
     * out.print(".Cells(21,19).Value = "); out.print("\"");
     * out.print(floatTarget[2]/intMonth); out.println("\"");
     */
    // (5,19)業績.目標
    stringSQL = " sp1SaleTarget1 " + "'" + stringProjectID + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 4, retData[0][0], objectSheet1);
    // (6,19)業績.出售
    stringSQL = " sp1SaleReal2_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 5, retData[0][0], objectSheet1);
    // (7,19)業績.出租
    stringSQL = " sp1SaleReal3_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 6, retData[0][0], objectSheet1);
    // (10,19)去年同期.業績
    stringSQL = " sp1SaleReal_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringBeforeMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 9, retData[0][0], objectSheet1);
    // (12,19)業績.超價
    stringSQL = " sp1SaleBala_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 11, retData[0][0], objectSheet1);
    // (14,19)業績.佣金
    stringSQL = " sp1SaleComm_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 13, retData[0][0], objectSheet1);
    // (16,19) 人力.目標(總人力)
    stringSQL = " sp2ManTarget " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'," + "'1'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 15, retData[0][0], objectSheet1);
    // (17,19) 人力.實際(總人力)
    stringSQL = " sp2ManReal1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 16, retData[0][0], objectSheet1);
    // (19,19) 人力.實際.(總人力)(去年同期)
    stringSQL = " sp2ManReal1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringBeforeMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 18, retData[0][0], objectSheet1);
    // (21,19) 人力.目標(直接)
    stringSQL = " sp2ManTarget " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'," + "'2'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 20, retData[0][0], objectSheet1);
    // (22,19) 人力.實際(直接)
    stringSQL = " sp2ManReal2 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 21, retData[0][0], objectSheet1);
    // (23,19) 人力.實際.直接(去年同期)
    stringSQL = " sp2ManReal2 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringBeforeMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 22, retData[0][0], objectSheet1);
    // (41,19) 實績預算.管理
    stringSQL = " sp4RealBudget_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'," + "'1'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 41, retData[0][0], objectSheet1);
    // (42,19) 成本.管理
    stringSQL = " sp4Cost1_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 42, retData[0][0], objectSheet1);
    // (45,19) 成本.管理.去年同期
    stringSQL = " sp4Cost1_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringBeforeMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 45, retData[0][0], objectSheet1);
    // (46,19) 實績預算.廣告
    stringSQL = " sp4RealBudget_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'," + "'2'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 46, retData[0][0], objectSheet1);
    // (50,19) 成本.廣告.去年同期
    stringSQL = " sp4Cost2_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringBeforeMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 50, retData[0][0], objectSheet1);
    // (51,19) 成本.接待中心
    if (stringDemoRoomMoney.length() > 0) {
      stringSQL = "SELECT SUM(realmoney) " + "FROM z_coreamm " + "WHERE ProjectID1 = '" + stringProjectID + "' " + "AND RTRIM(CostID)+RTRIM(CostID1) IN ('701','710','730') "
          + "AND YYMM BETWEEN '" + stringOriginStart + "' AND '" + stringMonthEnd + "' ";
      retData = dbSale.queryFromPool(stringSQL);
      if (retData[0][0].equals("")) {
        exeExcel.putDataIntoExcel(18, 51, "0", objectSheet1);
        // (47,19) 成本.廣告
        stringSQL = " sp4Cost2_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
        retData = dbSale.queryFromPool(stringSQL);
        exeExcel.putDataIntoExcel(18, 47, retData[0][0], objectSheet1);
      } else {
        exeExcel.putDataIntoExcel(18, 51, retData[0][0], objectSheet1);
        String Temp = retData[0][0];
        // (47,19) 成本.廣告
        stringSQL = " sp4Cost2_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
        retData = dbSale.queryFromPool(stringSQL);
        Temp = "" + (Float.parseFloat(retData[0][0]) - Float.parseFloat(Temp));
        exeExcel.putDataIntoExcel(18, 47, Temp, objectSheet1);
      }
    }
    // 銷售戶數
    stringSQL = " spSale01R210_SaleCount_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 58, retData[0][0], objectSheet1);
    // 銷售戶數 去年同期
    stringSQL = " spSale01R210_SaleCount_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringBeforeMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 60, retData[0][0], objectSheet1);
    // 退戶率
    stringSQL = " spSale01R210_DelMoney_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 62, retData[0][0], objectSheet1);
    // 退戶率 去年同期
    stringSQL = " spSale01R210_DelMoney_ProjectID1 " + "'" + stringProjectID + "'," + "'" + stringOriginStart + "'," + "'" + stringBeforeMonthEnd + "'";
    retData = dbSale.queryFromPool(stringSQL);
    exeExcel.putDataIntoExcel(18, 64, retData[0][0], objectSheet1);
    // 釋放 Excel 物件
    if (exeExcel != null) {
      exeExcel.getReleaseExcelObject(retVector);
    }
    return false;
  }

  public String getInformation() {
    return "---------------\u5217\u5370\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
