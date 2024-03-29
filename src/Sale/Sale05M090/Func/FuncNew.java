package Sale.Sale05M090.Func;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import Farglory.util.FargloryUtil;
import Farglory.util.KUtils;
import Farglory.util.MLPUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bTransaction;
import jcx.util.check;
import jcx.util.convert;
import jcx.util.datetime;

public class FuncNew extends bTransaction {
  public boolean action(String value) throws Throwable {
    System.out.println("chk==>" + getUser() + " , value==>新增");

    if (getUser() != null && getUser().toUpperCase().equals("B9999")) {
      messagebox("新增權限不允許!!!");
      return false;
    }

    //
    JTable jtableTable1 = getTable("table1");
    if (jtableTable1.getRowCount() == 0) {
      message("[客戶資料] 不可空白");
      return false;
    }

    getButton("ButtonSSMediaID").doClick();
    getButton("ButtonSetSaleID").doClick();

    KUtils kutil = new KUtils();
    MLPUtils mlpUtils = new MLPUtils();
    FargloryUtil exeUtil = new FargloryUtil();
    talk dbDoc = getTalk("" + get("put_dbDoc"));
    String stringSaleWay = getValue("SaleWay").trim();
    String stringProjectID1 = getValue("field1").trim();
    String stringOrderNo = getValue("OrderNo").trim();
    String stringDate = exeUtil.getDateConvert(getValue("field2").trim());
    String stringTrxDate = getValue("field2").trim();
    JTabbedPane jtabbedpane1 = getTabbedPane("tab1");
    float floatPercentage = 0;
    String stringCustomNo = "";
    String stringCustomName = "";
    String stringPercentage = "";
    String stringAddress = "";
    String stringNationality = "";// 20090414
    String stringTel = "";
    String stringTel2 = "";// 2010-4-16 新增電話2
    String stringCity = "";
    String stringTown = "";
    String stringZIP = "";
    String stringSql = "";
    String stringCellphone = "";
    String[][] retTown = null;

    // 20200620 Kyle : 表單檢核PASS，建立AML檢核物件
    Map amlCons = new HashMap();
    amlCons.put("OrderNo", stringOrderNo);
    amlCons.put("ProjectID1", stringProjectID1);
    amlCons.put("TrxDate", stringTrxDate);
    amlCons.put("funcName", "購屋證明單");
    amlCons.put("ActionName", "新增");

    for (int intRow = 0; intRow < jtableTable1.getRowCount(); intRow++) {
      stringCustomNo = ("" + getValueAt("table1", intRow, "CustomNo")).trim();
      stringCustomName = ("" + getValueAt("table1", intRow, "CustomName")).trim();
      stringPercentage = ("" + getValueAt("table1", intRow, "Percentage")).trim();
      stringNationality = ("" + getValueAt("table1", intRow, "Nationality")).trim();// 20090414
      stringAddress = ("" + getValueAt("table1", intRow, "Address")).trim();
      stringTel = ("" + getValueAt("table1", intRow, "Tel")).trim();
      stringTel2 = ("" + getValueAt("table1", intRow, "Tel2")).trim();// 2010-4-16 新增電話2
      stringCity = ("" + getValueAt("table1", intRow, "City")).trim();
      stringTown = ("" + getValueAt("table1", intRow, "Town")).trim();
      stringZIP = ("" + getValueAt("table1", intRow, "ZIP")).trim();
      stringCellphone = ("" + getValueAt("table1", intRow, "Cellphone")).trim();

      // 清除身分證字號的空白
      // setValueAt("table1" , stringCustomNo, intRow, "CustomNo");

      // 檢核
      if (!"".equals(stringCellphone) && stringCellphone.length() != 10) {
        messagebox("第 " + (intRow + 1) + " 列之[行動電話] 大小須為 10 碼。");
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        return false;
      }
      //
      if ("1".equals(stringNationality)) {
        if (stringCustomNo.length() == 0) {
          messagebox("第 " + (intRow + 1) + " 列之[統編/身分證號] 不可空白!。");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
        if (stringCustomNo.length() != 8 && stringCustomNo.length() != 10) {
          messagebox("第 " + (intRow + 1) + " 列之[統編/身分證號] 長度錯誤!。");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
        if (stringCustomNo.length() == 8 && check.isCoId(stringCustomNo) == false) {
          messagebox("第 " + (intRow + 1) + " 列之[統編/身分證號] 統一編號錯誤!。");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
        if (stringCustomNo.length() == 10 && check.isID(stringCustomNo) == false) {
          messagebox("第 " + (intRow + 1) + " 列之[統編/身分證號] 身分證號錯誤!。");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
      }
      if ("4".equals(stringNationality)) {
        if (stringCustomNo.length() != 9) {
          messagebox("第 " + (intRow + 1) + " 列之[統編/身分證號] 長度錯誤!。");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
      }
      //
      stringSql = " SELECT  ZIP " + " FROM  Town b " + " WHERE  Coun   IN  (SELECT  Coun  FROM  City  WHERE  CounName='" + stringCity + "') " + " AND  TownName  =  '" + stringTown
          + "' ";
      retTown = dbDoc.queryFromPool(stringSql);
      if (retTown.length == 0) {
        messagebox("第 " + (intRow + 1) + " 列之[縣市][鄉鎮] 關係不正確。");
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        return false;
      }
      if (!stringZIP.equals(retTown[0][0].trim())) {
        if (stringZIP.length() > 3)
          stringZIP = stringZIP.substring(0, 3);
        if (!stringZIP.equals(retTown[0][0].trim())) {
          messagebox("第 " + (intRow + 1) + " 列之[郵遞區號] 不正確。");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
      }
      // [統編/身分證號]
      if ("1".equals(stringNationality) && stringCustomNo.length() == 0) {// 20090414
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("筆數:" + (intRow + 1) + "-[統編/身分證號] 不可空白");
        return false;
      }
      //
      if (stringCustomName.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("筆數:" + (intRow + 1) + "-[訂戶姓名] 不可空白");
        return false;
      }
      // [比例%]
      if (stringPercentage.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("筆數:" + (intRow + 1) + "-[比例%] 不可空白");
        return false;
      }
      if (Float.parseFloat(stringPercentage.trim()) < 1) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("筆數:" + (intRow + 1) + "-[比例%] 不可小於 1");
        return false;
      }
      floatPercentage = floatPercentage + Float.parseFloat(stringPercentage);
      //
      if (stringAddress.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("筆數:" + (intRow + 1) + "-[地址] 不可空白");
        return false;
      }
      //
      if (stringTel.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("筆數:" + (intRow + 1) + "-[電話] 不可空白");
        return false;
      }

    }
    if (floatPercentage != 100) {
      message("[比例%] 必須為 100");
      return false;
    }
    //
    JTable jtableTable2 = getTable("table2");
    if (jtableTable2.getRowCount() == 0) {
      message("[戶別資料] 不可空白");
      jtabbedpane1.setSelectedIndex(0);
      return false;
    }
    for (int intRow = 0; intRow < jtableTable2.getRowCount(); intRow++) {
      // [棟樓別]
      String stringPosition = jtableTable2.getValueAt(intRow, 3).toString();
      if (stringPosition.length() == 0) {
        message("筆數:" + (intRow + 1) + "-[棟樓別] 不可空白");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 2).toString().length() == 0) {
        message("筆數:" + (intRow + 1) + "-[房車] 不可空白");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 4).toString().length() == 0) {
        message("筆數:" + (intRow + 1) + "-[坪數] 不可空白");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 5).toString().length() == 0) {
        message("筆數:" + (intRow + 1) + "-[牌價] 不可空白");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 6).toString().length() == 0) {
        message("筆數:" + (intRow + 1) + "-[售價] 不可空白");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
    }
    talk dbSale = getTalk("Sale");
    JTable jtableTable7 = getTable("table7");
    String stringOrderNoBonus = "";
    for (int intNo = 0; intNo < jtableTable7.getRowCount(); intNo++) {
      stringOrderNoBonus = ("" + getValueAt("table7", intNo, "OrderNoBonus")).trim();
      //
      stringSql = "SELECT  OrderNo  FROM  Sale05M092  WHERE  ISNULL(StatusCd,'')  = ''  AND  OrderNo  =  '" + stringOrderNoBonus + "' ";
      if (dbSale.queryFromPool(stringSql).length == 0) {
        jtabbedpane1.setSelectedIndex(5);
        jtableTable7.setRowSelectionInterval(intNo, intNo);
        messagebox("銷獎專用表格 第 " + (intNo + 1) + " 列之 [使用編號] 不存在。");
        return false;
      }
    }
    // 行銷策略 B3018 2012/09/17 S
    String[][] retSale05M246 = null;
    if ("".equals(stringProjectID1)) {
      messagebox("[案別] 不可為空白。");
      getcLabel("field1").requestFocus();
      return false;
    }
    if (stringDate.length() != 10) {
      messagebox("[日期]日期格式錯誤(YYYY/MM/DD)。");
      getcLabel("field2").requestFocus();
      return false;
    }
    if (!"".equals(stringSaleWay)) {
      // 存在檢核
      stringSql = "SELECT  Num,  PlanDateS,  PlanDateE " + " FROM  Sale05M246  " + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " AND  StrategyNo  =  '" + stringSaleWay
          + "' " + " AND  PlanDateS  <=  '" + stringDate + "' " + " AND  PlanDateE  >=  '" + stringDate + "' ";
      retSale05M246 = dbSale.queryFromPool(stringSql);
      if (retSale05M246.length == 0) {
        messagebox("[行銷策略]資料錯誤。");
        getcLabel("SaleWay").requestFocus();
        return false;
      }
      // 數量檢核
      double doubleNum = exeUtil.doParseDouble(retSale05M246[0][0].trim());
      String stringPlanDateS = retSale05M246[0][1].trim();
      String stringPlanDateE = retSale05M246[0][2].trim();
      if (doubleNum > 0) {
        stringSql = "SELECT  ProjectID1 " + " FROM  Sale05M090 " + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " AND  SaleWay  =  '" + stringSaleWay + "' "
            + " AND  OrderNo  <>  '" + stringOrderNo + "' " + " AND  OrderDate  >=  '" + stringPlanDateS + "' " + " AND  OrderDate  <=  '" + stringPlanDateE + "' ";
        retSale05M246 = dbSale.queryFromPool(stringSql);
        if (exeUtil.doParseDouble("" + (retSale05M246.length + 1)) > doubleNum) {
          String stringStrategyName = exeUtil.getNameUnion("StrategyName", "Sale05M244", " AND  StrategyNo  = '" + stringSaleWay + "' ", new Hashtable(), dbSale);
          messagebox("購屋證明單筆數超過行銷策略(" + stringStrategyName + ")所設定的" + convert.FourToFive("" + doubleNum, 0) + " 筆的數量。");
          getcLabel("SaleWay").requestFocus();
          return false;
        }
      }
    }
    // 行銷策略 B3018 2012/09/17 E
    // 媒體代碼檢核 楊信義 2015/05/25 S
    String stringSSMediaID = getValue("SSMediaID").trim();
    String stringSSMediaID1 = getValue("SSMediaID1").trim();
    if ("".equals(stringSSMediaID)) {
      // messagebox("[媒體代碼] 不可為空白。") ;
      // return false ;
    } else {
      String[][] retMediaSS = dbSale.queryFromPool(" SELECT  SSMediaName  FROM  Media_SS  WHERE  SSMediaID=  '" + stringSSMediaID + "'  AND  Stop  =  'N' ");
      if (retMediaSS.length == 0) {
        messagebox("[媒體代碼] 不存在資料庫中。");
        return false;
      }
    }
    // 2015-12-10 B3018 售出人檢核
    JTable jtable9 = getTable("table9");
    String stringSaleID1 = "";
    String stringZ6SaleID2 = "";
    String stringCSSaleID2 = "";
    String stringSaleName1 = "";
    String stringZ6SaleName2 = "";
    String stringCSSaleName2 = "";
    if (jtable9.getRowCount() <= 0) {
      jtabbedpane1.setSelectedIndex(1);
      messagebox("[售出人表格] 不可無資料。");
      return false;
    }
    for (int intNo = 0; intNo < jtable9.getRowCount(); intNo++) {
      stringSaleID1 = ("" + getValueAt("table9", intNo, "SaleID1")).trim();
      stringSaleName1 = ("" + getValueAt("table9", intNo, "SaleName1")).trim();
      stringZ6SaleID2 = ("" + getValueAt("table9", intNo, "Z6SaleID2")).trim();
      stringZ6SaleName2 = ("" + getValueAt("table9", intNo, "Z6SaleName2")).trim();
      stringCSSaleID2 = ("" + getValueAt("table9", intNo, "CSSaleID2")).trim();
      stringCSSaleName2 = ("" + getValueAt("table9", intNo, "CSSaleName2")).trim();
      //
      if ("".equals(stringSaleID1)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[售出人表格] 第 " + (intNo + 1) + " 列之 [銷售(實際)-員編] 不可為空白。");
        return false;
      }
      if ("".equals(stringSaleName1)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[售出人表格] 第 " + (intNo + 1) + " 列之 [銷售(實際)-售出人] 不可為空白。");
        return false;
      }
      if ("".equals(stringCSSaleID2)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[售出人表格] 第 " + (intNo + 1) + " 列之 [遠雄人壽-員編] 不可為空白。");
        return false;
      }
      if (!"".equals(stringCSSaleID2) && "".equals(stringCSSaleName2)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[售出人表格] 第 " + (intNo + 1) + " 列之 [遠雄人壽-售出人] 不可為空白。");
        return false;
      }
    }
    // 20191024 實質受益人
    JTable jtable6 = getTable("table6");
    String strBCustomNo = "";
    String strBenName = "";
    String strCountryName = "";
    for (int intNo = 0; intNo < jtable6.getRowCount(); intNo++) {
      strBCustomNo = ("" + getValueAt("table6", intNo, "BCustomNo")).trim();
      strBenName = ("" + getValueAt("table6", intNo, "BenName")).trim();
      strCountryName = ("" + getValueAt("table6", intNo, "CountryName")).trim();
      if ("".equals(strBCustomNo)) {
        jtabbedpane1.setSelectedIndex(2);
        jtable6.setRowSelectionInterval(intNo, intNo);
        messagebox("[實質受益人表格] 第 " + (intNo + 1) + " 列之 [受益人姓名] 不可為空白。");
        return false;
      }
      if ("".equals(strBenName)) {
        jtabbedpane1.setSelectedIndex(2);
        jtable6.setRowSelectionInterval(intNo, intNo);
        messagebox("[實質受益人表格] 第 " + (intNo + 1) + " 列之 [身分證號] 不可為空白。");
        return false;
      }
      if ("".equals(strCountryName)) {
        jtabbedpane1.setSelectedIndex(2);
        jtable6.setRowSelectionInterval(intNo, intNo);
        messagebox("[實質受益人表格] 第 " + (intNo + 1) + " 列之 [國別] 不可為空白。");
        return false;
      }
    }
    // 媒體代碼檢核 2015/05/25 E
    // 2016-05-09 B3018
    JTable jtable3 = getTable("table3");
    String stringQty = "";
    String stringTotalAmt = "";
    String stringItemNo = "";
    for (int intNo = 0; intNo < jtable3.getRowCount(); intNo++) {
      stringQty = ("" + getValueAt("table3", intNo, "Qty")).trim();
      stringTotalAmt = ("" + getValueAt("table3", intNo, "TotalAmt")).trim();
      stringItemNo = ("" + getValueAt("table3", intNo, "ItemNo")).trim();
      //
      if (!stringItemNo.startsWith("Y"))
        continue;
      if (exeUtil.doParseDouble(stringTotalAmt) > 0 && exeUtil.doParseDouble(stringQty) == 0) {
        messagebox("[贈送表格] 第 " + (intNo + 1) + " 列之 [數量] 不可為空白。");
        return false;
      }
    }
    JTable jtable12 = getTable("table12");
    String stringComLoadMoney = getValue("ComLoadMoney").trim();
    String stringComNo = "";
    String stringComLoadDate = "";
    String stringPrincipalAmt = "";
    String stringInterestAmt = "";
    String stringInterestKind = "";
    String stringSqlAnd = "";
    double doublePrincipalAmt = 0;
    double doubleComLoadMoney = 0;
    Vector vectorACom = null;
    for (int intNo = 0; intNo < jtable12.getRowCount(); intNo++) {
      stringComNo = ("" + getValueAt("table12", intNo, "Com_No")).trim();
      stringComLoadDate = ("" + getValueAt("table12", intNo, "ComLoadDate")).trim();
      stringPrincipalAmt = ("" + getValueAt("table12", intNo, "PrincipalAmt")).trim();
      stringInterestAmt = ("" + getValueAt("table12", intNo, "InterestAmt")).trim();
      stringInterestKind = ("" + getValueAt("table12", intNo, "InterestKind")).trim();
      // 業主別
      if ("".equals(stringComNo)) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("公司貸表格第 " + (intNo + 1) + " 行之 [業主別] 不可為空白。");
        return false;
      }
      stringSqlAnd = " AND  ISNULL(COMPANY_CD,'')  <>  ''  " + " AND  Com_No  =  '" + stringComNo + "' " + " AND Com_No IN (SELECT  distinct H_COM " + " FROM  Sale05M040 "
          + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " UNION " + " SELECT  distinct  L_COM " + " FROM  Sale05M040 " + " WHERE  ProjectID1  =  '" + stringProjectID1
          + "' " + " ) ";
      vectorACom = exeUtil.getQueryDataHashtable("A_Com", new Hashtable(), stringSqlAnd, dbSale);
      if (vectorACom.size() == 0) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("公司貸表格第 " + (intNo + 1) + " 行之 [業主別] 不存在資料庫中。");
        return false;
      }
      // 公司貸期別
      if ("".equals(stringComLoadDate)) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("公司貸表格第 " + (intNo + 1) + " 行之 [公司貸期別] 不可為空白。");
        return false;
      }
      stringComLoadDate = exeUtil.getDateAC(stringComLoadDate, "公司貸期別");
      if (stringComLoadDate.length() != 10) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("公司貸表格第 " + (intNo + 1) + " 行之 [公司貸期別] 日期格式(YYYY/mm/dd)錯誤。");
        return false;
      }
      // 各期本金金額
      if (exeUtil.doParseDouble(stringPrincipalAmt) <= 0) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("公司貸表格第 " + (intNo + 1) + " 行之 [各期本金金額] 不可為 0。");
        return false;
      }
      doublePrincipalAmt += exeUtil.doParseDouble(stringPrincipalAmt);
      // 利息支付方式
      if ("".equals(stringInterestKind)) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("公司貸表格第 " + (intNo + 1) + " 行之 [利息支付方式] 不可為空白。");
        return false;
      }
    }
    // 檢核公司貸總額 要等於各期本金金額 加總
    doublePrincipalAmt = exeUtil.doParseDouble(convert.FourToFive("" + doublePrincipalAmt, 4));
    doubleComLoadMoney = exeUtil.doParseDouble(convert.FourToFive(stringComLoadMoney, 4));
    if (doublePrincipalAmt != doubleComLoadMoney) {
      jtabbedpane1.setSelectedIndex(3);
      getcLabel("ComLoadMoney").requestFocus();
      messagebox("[公司貸總額] 不等於 公司貸表格之各期本金金額 加總。");
      return false;
    }
    message("");
    //
    if (getValue("CompanyNo").length() != 0 && getValue("field1").length() != 0 && getValue("field2").length() != 0) {
      // talk dbSale = getTalk(""+get("put_dbSale"));
      String stringSQL = "";
      // 原購屋證明單編號
      getButton("ButtonOrderNo").doClick();
      //
      stringSQL = " INSERT  " + " INTO Sale05M090_Flow " + " ( " + " OrderNo," + " FlowStatus," + " EmployeeNo," + " EDateTime " + " ) " + " VALUES " + " ( " + "'"
          + getValue("field3").trim() + "'," + "N'經辦'," + "N'" + getUser() + "'," + "N'" + datetime.getTime("YYYY/mm/dd h:m:s") + "'" + " ) ";
      dbSale.execFromPool(stringSQL);
      //
      stringSQL = " INSERT  " + " INTO Sale05M090_Flow_HIS " + " ( " + " OrderNo," + " FlowStatus," + " EmployeeNo," + " EDateTime " + " ) " + " VALUES " + " ( " + "'"
          + getValue("field3").trim() + "'," + "N'經辦'," + "N'" + getUser() + "'," + "N'" + datetime.getTime("YYYY/mm/dd h:m:s") + "'" + " ) ";
      dbSale.execFromPool(stringSQL);
      setValue("OrderNo", getValue("field3").trim());
    }

    // 洗錢制裁名單檢核
    System.out.println("========新增_制裁名單檢核=======S");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    talk db400 = getTalk("400CRM");
    talk dbJGENLIB = getTalk("JGENLIB");
    String errMsg = "";
    // 畫面值
    String strProjectID1 = getValue("field1").trim();
    String strOrderDate = getValue("field2").trim();
    String strOrderNo = getValue("field3").trim();

    // 共用
    String custNo = "";
    String custName = "";
    String birth = "";
    String ind = "";
    
    // 案別取Query_Log
    Map mapQueryLog = kutil.getMapQueryLogByProjectId(strProjectID1);

    String[][] ret91Table = getTableData("table1"); // 客戶
    if (ret91Table.length > 0) {
      for (int a = 0; a < ret91Table.length; a++) {
        custNo = this.getValueAt("table1", a, "CustomNo").toString().trim();
        custName = this.getValueAt("table1", a, "CustomName").toString().trim();
        birth = this.getValueAt("table1", a, "Birthday").toString().length() == 0 ? " " : getValueAt("table1", a, "Birthday").toString();
        ind = this.getValueAt("table1", a, "IndustryCode").toString().length() == 0 ? " " : getValueAt("table1", a, "IndustryCode").toString();
        
        String amlText = strOrderNo + "," + strOrderDate + "," + custNo + "," + custName + "," + birth + "," + ind + "," + "query18";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();
      }
    }

    String[][] ret91BenTable = getTableData("table6"); // 實質受益人
    if (ret91BenTable.length > 0) {
      for (int b = 0; b < ret91BenTable.length; b++) {
        custNo = this.getValueAt("table6", b, "CustomNo").toString().trim();
        custName = this.getValueAt("table6", b, "CustomName").toString().trim();
        birth = this.getValueAt("table6", b, "Birthday").toString().length() == 0 ? " " : getValueAt("table1", b, "Birthday").toString();
        
        QueryLogBean qBean = (QueryLogBean) mapQueryLog.get(custNo);
        ind = qBean.getJobType();
        
        String amlText = strOrderNo + "," + strOrderDate + "," + custNo + "," + custName + "," + birth + "," + ind + "," + "query18";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();
      }
    }

    String[][] ret91AgentTable = getTableData("table10"); // 代理人
    if (ret91AgentTable.length > 0) {
      for (int c = 0; c < ret91AgentTable.length; c++) {
        custNo = this.getValueAt("table10", c, "CustomNo").toString().trim();
        custName = this.getValueAt("table10", c, "CustomName").toString().trim();
        
        QueryLogBean qBean = (QueryLogBean) mapQueryLog.get(custNo);
        ind = qBean.getJobType();
        birth = qBean.getBirthday();
        
        String amlText = strOrderNo + "," + strOrderDate + "," + custNo + "," + custName + "," + birth + "," + ind + "," + "query18";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

      }
    }
    System.out.println("========新增_制裁名單檢核=======E");

    // SHOW MSG
    if (!"".equals(errMsg)) {
      System.out.println(">>>msg");
      setValue("errMsgBoxText", errMsg);
      getButton("errMsgBoxBtn").doClick();
      getButton("sendMail").doClick();
      return false;
    }

    // 定審
    JButton buyedInfo = getButton("BuyedInfo");
    buyedInfo.setText("checkAndEmail");
    buyedInfo.doClick();
    put("TrustAccountNo", value);
    setValue("actionText", "新增");
    getButton("ButtonTrustAccountNo").doClick();

    // 20200619 Kyle : 更新受益人資訊 start
    // 檢查受益人
    if (mlpUtils.checkHasBen(getTableData("table1"), getTableData("table6")) == false)
      return false;

    // 檢查受益人欄位
    if (mlpUtils.checkBenColumn(getTableData("table6"), amlCons) == false)
      return false;

    // 更新實質受益人表
    getButton("updateBen").doClick();
    System.out.println("updateBen=====> Done");
    // 更新受益人資訊 end

    return true;
  }

  public String getInformation() {
    return "---------------\u65b0\u589e\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
