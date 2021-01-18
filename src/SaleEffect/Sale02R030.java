package SaleEffect;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import com.jacob.activeX.*;
import com.jacob.com.*;

public class Sale02R030 extends bTransaction {
  double double坪數小計11Global = 0;
  double double訂價小計Global = 0;
  double double售價小計Global = 0;
  double double贈送小計Global = 0;
  double double佣金小計Global = 0;
  double double中原佣金小計Global = 0; // 2015-11-18 B3018
  double double利息小計Global = 0; // 2017-05-02 B4474
  double double淨售小計Global = 0;
  double double底價小計Global = 0;
  double double超低價小計Global = 0;
  talk dbSale = getTalk((String) get("put_dbSale"));// SQL2000

  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    // 將前端的資料作處理
    if (!isBatchCheckOK())
      return false;
    // 對 Excel 作處理
    doExcel();
    return false;
  }

  //欄位檢核
  public boolean isBatchCheckOK() throws Throwable {
    String stringPrintFile = "";
    String stringTmp = "";
    String stringSql = "";
    // 案別
    if ("".equals(getValue("ProjectID").trim())) {
      message("案別不可為空白。");
      getcLabel("ProjectID").requestFocus();
      return false;
    }
    String[][] retAProject = getAProject(getValue("ProjectID").trim());
    if (retAProject.length == 0) {
      message("無此案別存在。");
      getcLabel("ProjectID").requestFocus();
      return false;
    }
    // 案別限制
    String[][] retSale02M020 = getSale02M020();
    if (retSale02M020.length > 0) {
      String[][] retSale02M020Count = getSale02M020(getValue("ProjectID").trim());
      if (retSale02M020Count.length == 0) {
        message("無使用此案別權限，請洽行管室主管開放!");
        getcLabel("ProjectID").requestFocus();
        return false;
      }
    }
    
    // 付訂日期
    String retDate = "";
    int count = 0;
    String stringOrderDateStart = getValue("OrderDateStart").trim();
    if( !"".equals(stringOrderDateStart) ) {
      retDate = getDateAC(stringOrderDateStart, "付訂日期(起)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("OrderDateStart").requestFocus();
        return false;
      }
      setValue("OrderDateStart", retDate);
      count++;
    }
    String stringOrderDateEnd = getValue("OrderDateEnd").trim();
    if( !"".equals(stringOrderDateEnd) ) {
      retDate = getDateAC(stringOrderDateEnd, "付訂日期(迄)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("OrderDateEnd").requestFocus();
        return false;
      }
      setValue("OrderDateEnd", retDate);
      count++;
    }
    System.out.println("count>>>" + count);
    if(count == 1) {
      message("[付訂日期(起)(迄)] 須同時限制。");
      getcLabel("OrderDateStart").requestFocus();
      return false;
    }
    
    // 補足日期
    String stringEnougDateStart = getValue("EnougDateStart").trim();
    String stringEnougDateEnd = getValue("EnougDateEnd").trim();
    if (!"".equals(stringEnougDateStart) && !"".equals(stringEnougDateEnd)) {
      retDate = getDateAC(stringEnougDateStart, "補足日期(起)");
      if (!"".equals(stringEnougDateStart) && retDate.length() != 10) {
        message(retDate);
        getcLabel("EnougDateStart").requestFocus();
        return false;
      } else {
        setValue("EnougDateStart", retDate);
      }
      retDate = getDateAC(stringEnougDateEnd, "補足日期(起)");
      if (!"".equals(stringEnougDateEnd) && retDate.length() != 10) {
        message(retDate);
        getcLabel("EnougDateEnd").requestFocus();
        return false;
      } else {
        setValue("EnougDateEnd", retDate);
      }
    }
    if (!"".equals(stringEnougDateStart)) {
      message("[補足日期(起)(迄)] 須同時限制。");
      getcLabel("EnougDateStart").requestFocus();
      return false;
    }
    if (!"".equals(stringEnougDateEnd)) {
      message("[補足日期(起)(迄)] 須同時限制。");
      getcLabel("EnougDateEnd").requestFocus();
      return false;
    }
    // 簽約日期
    String stringContrDateStart = getValue("ContrDateStart").trim();
    String stringContrDateEnd = getValue("ContrDateEnd").trim();
    if (!"".equals(stringContrDateStart) && !"".equals(stringContrDateEnd)) {
      retDate = getDateAC(stringContrDateStart, "簽約日期(起)");
      if (!"".equals(stringContrDateStart) && retDate.length() != 10) {
        message(retDate);
        getcLabel("ContrDateStart").requestFocus();
        return false;
      } else {
        setValue("ContrDateStart", retDate);
      }
      retDate = getDateAC(stringContrDateEnd, "簽約日期(起)");
      if (!"".equals(stringContrDateEnd) && retDate.length() != 10) {
        message(retDate);
        getcLabel("ContrDateEnd").requestFocus();
        return false;
      } else {
        setValue("ContrDateEnd", retDate);
      }
    }
    if (!"".equals(stringContrDateStart)) {
      message("[簽約日期(起)(迄)] 須同時限制。");
      getcLabel("ContrDateStart").requestFocus();
      return false;
    }
    if (!"".equals(stringContrDateEnd)) {
      message("[簽約日期(起)(迄)] 須同時限制。");
      getcLabel("ContrDateEnd").requestFocus();
      return false;
    }
    // 合約會審
    String stringDateCheckStart = getValue("DateCheckStart").trim();
    String stringDateCheckEnd = getValue("DateCheckEnd").trim();
    if (!"".equals(stringDateCheckStart) && !"".equals(stringDateCheckEnd)) {
      retDate = getDateAC(stringDateCheckStart, "合約會審(起)");
      if (!"".equals(stringDateCheckStart) && retDate.length() != 10) {
        message(retDate);
        getcLabel("DateCheckStart").requestFocus();
        return false;
      } else {
        setValue("DateCheckStart", retDate);
      }
      retDate = getDateAC(stringDateCheckEnd, "合約會審(起)");
      if (!"".equals(stringDateCheckEnd) && retDate.length() != 10) {
        message(retDate);
        getcLabel("DateCheckEnd").requestFocus();
        return false;
      } else {
        setValue("DateCheckEnd", retDate);
      }
    }
    if (!"".equals(stringDateCheckStart)) {
      message("[合約會審(起)(迄)] 須同時限制。");
      getcLabel("ContrDateStart").requestFocus();
      return false;
    }
    if (!"".equals(stringDateCheckEnd)) {
      message("[合約會審(起)(迄)] 須同時限制。");
      getcLabel("DateCheckEnd").requestFocus();
      return false;
    }
    // 簽約金到期
    String stringDateRangeStart = getValue("DateRangeStart").trim();
    String stringDateRangeEnd = getValue("DateRangeEnd").trim();
    if (!"".equals(stringDateRangeStart) && !"".equals(stringDateRangeEnd)) {
      retDate = getDateAC(stringDateRangeStart, "簽約金到期(起)");
      if (!"".equals(stringDateRangeStart) && retDate.length() != 10) {
        message(retDate);
        getcLabel("DateRangeStart").requestFocus();
        return false;
      } else {
        setValue("DateRangeStart", retDate);
      }
      retDate = getDateAC(stringDateRangeEnd, "簽約金到期(起)");
      if (!"".equals(stringDateRangeEnd) && retDate.length() != 10) {
        message(retDate);
        getcLabel("DateRangeEnd").requestFocus();
        return false;
      } else {
        setValue("DateRangeEnd", retDate);
      }
    }
    if (!"".equals(stringDateRangeStart)) {
      message("[簽約金到期(起)(迄)] 須同時限制。");
      getcLabel("DateRangeStart").requestFocus();
      return false;
    }
    if (!"".equals(stringDateRangeEnd)) {
      message("[簽約金到期(起)(迄)] 須同時限制。");
      getcLabel("DateRangeEnd").requestFocus();
      return false;
    }
    // Check File 是否存在
    message("");
    return true;
  }
  //欄位檢核 END

  public void doExcel() throws Throwable {
    // 由 Function 選擇 Excel 位置，並執行該程式
    String stringExcelName = "";
    String stringFunction = getValue("Function").trim();
    String stringDisplay = "";
    switch (Integer.parseInt(stringFunction)) {
    case 0:
      stringDisplay = getValue("Display").trim();
      if ("1".equals(stringDisplay)) {
        stringExcelName = "SaleOutDetailSale1.XLS";// 出售房屋簡表
      } else {
        stringExcelName = "SaleOutDetailSale.XLS";// 出售房屋非簡表
      }
      doSaleOutDetailSale(stringExcelName, "A_Sale"); // 20090603 修正
      break;
    case 3:
      stringDisplay = getValue("Display").trim();
      if ("1".equals(stringDisplay)) {
        stringExcelName = "SaleOutDetailSale1.XLS";// 出售車位簡表
      } else {
        stringExcelName = "SaleOutDetailSale.XLS";// 出售車位非簡表
      }
      doSaleOutDetailSale(stringExcelName, "A_Sale"); // 20090603 修正
      break;
    case 1:
      stringDisplay = getValue("Display").trim();
      if ("1".equals(stringDisplay)) {
        stringExcelName = "SaleOutDetailRent1.XLS";// 出租房屋簡表
      } else {
        stringExcelName = "SaleOutDetailRent.XLS";// 出租房屋非簡表
      }
      doSaleOutDetailRent(stringExcelName, "A_Sale");// 20090603 修正
      break;
    case 4:
      stringDisplay = getValue("Display").trim();
      if ("1".equals(stringDisplay)) {
        stringExcelName = "SaleOutDetailRent1.XLS";// 出租車位簡表
      } else {
        stringExcelName = "SaleOutDetailRent.XLS";// 出租車位非簡表
      }
      doSaleOutDetailRent(stringExcelName, "A_Sale");// 20090603 修正
      break;
    case 2:
      stringDisplay = getValue("Display").trim();
      if ("1".equals(stringDisplay)) {
        stringExcelName = "SaleOutDetailDelete1.XLS"; // 退戶房屋簡表
      } else {
        stringExcelName = "SaleOutDetailDelete.XLS"; // 退戶房屋非簡表
      }
      doSaleOutDetailDelete(stringExcelName, "A_Sale1");
      break;
    case 5:
      stringDisplay = getValue("Display").trim();
      if ("1".equals(stringDisplay)) {
        stringExcelName = "SaleOutDetailDelete1.XLS";// 退戶車位簡表
      } else {
        stringExcelName = "SaleOutDetailDelete.XLS";// 退戶車位非簡表
      }
      doSaleOutDetailDelete(stringExcelName, "A_Sale1");
      break;
    }
  }

  public boolean doSaleOutDetailDelete(String stringExcelName, String stringTableSelect) throws Throwable {
    Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(6, 20, 33, 1);
    // 資料處理
    String stringHComLocal = "";
    String stringLComLocal = "";
    String stringSql = "";
    String stringProjectID = getValue("ProjectID").trim();
    String stringOrderDateStart = getValue("OrderDateStart").trim();
    String stringOrderDateEnd = getValue("OrderDateEnd").trim();
    String stringSqlAnd = getSqlAnd() + getSqlAnd2();
    String stringDistributeNo = getValue("Distribute").trim();
    String stringHouseLand = getValue("HouseLand").trim();
    String stringHouseCar = "";
    String stringFunction = getValue("Function").trim();
    String stringThisType = "";
    boolean booleanHouse = true;
    boolean booleanLand = true;
    //
    switch (Integer.parseInt(stringFunction)) {
    case 0:
    case 1:
    case 2:
      stringThisType = "房子"; // 房子車位
      break;
    case 3:
    case 4:
    case 5:
      stringThisType = "車位"; // 房子車位
      break;
    }
    switch (Integer.parseInt(stringHouseLand)) {
    case 0:
      stringHouseCar = "";
      break;
    case 1:
      stringHouseCar = "H_";
      break;
    case 2:
      stringHouseCar = "L_";
      break;
    }
    /*
     * 0.Position 1.PositionRent 2.Car 3.CarRent 4.Custom 5.OrderDate 6.OrderMon1
     * 7.OrderMon2 8.OrderMon3 9.EnougDate 10.EnougMon1 11.EnougMon2 12.EnougMon3
     * 13.ContrDate 14.ContrMon1 15.ContrMon2 16.ContrMon3 17.Deldate 18.PingSu
     * 19.PreMoney1 20.PreMoney2 21.PreMoney3 22.DealMoney1 23.DealMoney2
     * 24.DealMoney3 25.GiftMoney1 26.GiftMoney2 27.GiftMoney3 28.CommMoney1
     * 29.CommMoney2 30.CommMoney3 31.PureMoney1 32.PureMoney2 33.PureMoney3
     * 34.LastMoney1 35. LastMoney2 36.LastMoney3 37.BalaMoney1 38.BalaMoney2
     * 39.BalaMoney3 40.SaleName1 41.SaleName2 42.SaleName3 43.SaleName4
     * 44.SaleName5 45.MediaName 46.ZoneName 47.MajorName 48.UseType 49.Remark
     * 50.DateRange 51.DateCheck 52.DateFile 53.DateBonus 54.H_COM 55.L_COM
     * 56.SaleName6 57.SaleName7 58.SaleName8 59.SaleName9 60.SaleName10
     * 61.CommMoney11 62.CommMoney12 63.CommMoney13
     */
    String[][] retASale = getDeleteForASale1(stringProjectID, stringOrderDateStart, stringOrderDateEnd, stringThisType, stringHouseCar, stringSqlAnd);
    // 有無資料檢查
    if (retASale.length == 0) {
      message("查無資料。");
      return false;
    }
    // 取得 Exce 物件
    Vector retVector = exeFun.getExcelObject("G:\\資訊室\\Excel\\SaleEffect\\" + stringExcelName);
    System.out.println("-------" + "G:\\資訊室\\Excel\\SaleEffect\\" + stringExcelName + "------");
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);
    Dispatch objectClick = null;
    exeFun.setClearCol(0, 39);
    // 一致性列印
    // 欄位變更
    if ("房子".equals(stringThisType)) {
      exeFun.putDataIntoExcel(1, 4, "棟樓別", objectSheet2);
    } else if ("車位".equals(stringThisType)) {
      exeFun.putDataIntoExcel(1, 4, "車位別", objectSheet2);
    }
    // 公司名
    exeFun.putDataIntoExcel(0, 1, getACom(stringDistributeNo), objectSheet2);
    // 案別
    exeFun.putDataIntoExcel(0, 3, "案別：" + stringProjectID, objectSheet2);
    // 日期
    exeFun.putDataIntoExcel(4, 3, "日期：" + getConertFormatDate(stringOrderDateStart) + "~" + getConertFormatDate(stringOrderDateEnd), objectSheet2);
    // 條件SQL
    String stringConditionDescription = getConditionDescription();
    String stringConditionDescription2 = getConditionDescription2();
    exeFun.putDataIntoExcel(19, 3, stringConditionDescription + stringConditionDescription2, objectSheet2);
    /*
     * 0.H_Com 1.L_Com 2.OpenDate 3.PositionMoneyS1 4.PositionMoneyS2
     * 5.PositionMoneyS3 6.CarMoneyS1 7.CarMoneyS2 8.CarMoneyS3
     */
    String[][] retAProjectLocal = getDeleteForAProject(stringProjectID, stringHouseCar);
    // retAProjectLocal START
    // 公開日期 OpenDate
    exeFun.putDataIntoExcel(30, 3, "公開日期：" + getConertFormatDate(convert.replace(retAProjectLocal[0][2].trim(), "-", "").substring(0, 8)), objectSheet2);
    // 可售金額
    double double可售金額 = 0;
    for (int intNo = 0; intNo < retAProjectLocal.length; intNo++) {
      stringHComLocal = retAProjectLocal[intNo][0].trim();
      stringLComLocal = retAProjectLocal[intNo][1].trim();
      booleanHouse = false;
      booleanLand = false;
      if ("".equals(stringHouseCar)) {
        // 房土
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      //
      if ("房子".equals(stringThisType)) {
        if (booleanHouse)
          double可售金額 += doParseDouble(retAProjectLocal[0][4].trim(), "1-1");// PositionMoneyS2
        if (booleanLand)
          double可售金額 += doParseDouble(retAProjectLocal[0][5].trim(), "1-2");// PositionMoneyS3
      } else if ("車位".equals(stringThisType)) {
        if (booleanHouse)
          double可售金額 += doParseDouble(retAProjectLocal[0][7].trim(), "1-4");// CarMoneyS2
        if (booleanLand)
          double可售金額 += doParseDouble(retAProjectLocal[0][8].trim(), "1-5");// CarMoneyS3
      }
    }
    exeFun.putDataIntoExcel(2, 26, "" + double可售金額, objectSheet2);
    // retAProjectLocal END
    // 累計
    // retASale累計 START
    /*
     * 0.SumPingSu 1.SumPreMoney 2.SumPreMoney2 3.SumPreMoney3 4.SumDealMoney
     * 5.SumDealMoney2 6.SumDealMoney3 7.SumGiftMoney 8.SumGiftMoney2
     * 9.SumGiftMoney3 10.SumCommMoney 11.SumCommMoney2 12.SumCommMoney3
     * 13.SumPureMoney 14.SumPureMoney2 15.SumPureMoney3 16.SumLastMoney
     * 17.SumLastMoney2 18.SumLastMoney3 19.SumBalaMoney 20.SumBalaMoney2
     * 21.SumBalaMoney3 22. H_COM 23. L_COM 24.SumCommMoney11 25.SumCommMoney12 26
     * SumCommMoney13
     */
    String[][] retASale累計 = getSumDeleteForASale1(stringHouseCar, stringProjectID, stringOrderDateEnd, stringThisType, getSqlAnd2());
    double double坪數累計 = 0;
    double double訂價累計 = 0;
    double double售價累計 = 0;
    double double贈送累計 = 0;
    double double佣金累計 = 0;
    double double中原佣金累計 = 0; // 2015-11-18 B3018
    double double利息累計 = 0; // 2017-05-02 B4474
    double double淨售累計 = 0;
    double double底價累計 = 0;
    double double超低價累計 = 0;
    for (int intNo = 0; intNo < retASale累計.length; intNo++) {
      // 坪數累計
      if ("Z".equals(stringProjectID)) { // M^2 Convert 坪
        // SumPingSu
        double坪數累計 += doParseDouble(retASale累計[intNo][0].trim(), "1-7") * 0.3025;
      } else {
        // SumPingSu
        double坪數累計 += doParseDouble(retASale累計[intNo][0].trim(), "1-8");
      }
      //
      stringHComLocal = retASale累計[intNo][22].trim();
      stringLComLocal = retASale累計[intNo][23].trim();
      booleanHouse = false;
      booleanLand = false;
      if ("".equals(stringHouseCar)) {
        // 房土
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      //
      if (booleanHouse) {
        // SumPreMoney2
        double訂價累計 += doParseDouble(retASale累計[intNo][2].trim(), "1-9");
        // SumDealMoney2
        double售價累計 += doParseDouble(retASale累計[intNo][5].trim(), "1-10");
        // SumGiftMoney2
        double贈送累計 += doParseDouble(retASale累計[intNo][8].trim(), "1-11");
        // SumCommMoney2
        double佣金累計 += doParseDouble(retASale累計[intNo][11].trim(), "1-12");
        double中原佣金累計 += doParseDouble(retASale累計[intNo][25].trim(), "1-12"); // 2015-11-18 B3018
        // SumPureMoney2
        double淨售累計 += doParseDouble(retASale累計[intNo][14].trim(), "1-13");
        // SumLastMoney2
        double底價累計 += doParseDouble(retASale累計[intNo][17].trim(), "1-14");
        // SumBalaMoney2
        double超低價累計 += doParseDouble(retASale累計[intNo][20].trim(), "1-15");
        //
        double利息累計 += doParseDouble(retASale累計[intNo][28].trim(), "利息"); // 2017-05-02 B4474
      }
      if (booleanLand) {
        // SumPreMoney3
        double訂價累計 += doParseDouble(retASale累計[intNo][3].trim(), "1-16");
        // SumDealMoney3
        double售價累計 += doParseDouble(retASale累計[intNo][6].trim(), "1-17");
        // SumGiftMoney3
        double贈送累計 += doParseDouble(retASale累計[intNo][9].trim(), "1-18");
        // SumCommMoney3
        double佣金累計 += doParseDouble(retASale累計[intNo][12].trim(), "1-19");
        double中原佣金累計 += doParseDouble(retASale累計[intNo][26].trim(), "1-19"); // 2015-11-18 B3018
        // SumPureMoney3
        double淨售累計 += doParseDouble(retASale累計[intNo][15].trim(), "1-20");
        // SumLastMoney3
        double底價累計 += doParseDouble(retASale累計[intNo][18].trim(), "1-21");
        // SumBalaMoney3
        double超低價累計 += doParseDouble(retASale累計[intNo][21].trim(), "1-22");
        //
        double利息累計 += doParseDouble(retASale累計[intNo][29].trim(), "利息"); // 2017-05-02 B4474
      }
    }
    exeFun.putDataIntoExcel(10, 26, "" + double坪數累計, objectSheet2);
    exeFun.putDataIntoExcel(11, 26, "" + double訂價累計, objectSheet2);
    exeFun.putDataIntoExcel(12, 26, "" + double售價累計, objectSheet2);
    exeFun.putDataIntoExcel(13, 26, "" + double贈送累計, objectSheet2);
    exeFun.putDataIntoExcel(14, 26, "" + double佣金累計, objectSheet2);
    exeFun.putDataIntoExcel(15, 26, "" + double中原佣金累計, objectSheet2); // 2015-11-18 B3018
    exeFun.putDataIntoExcel(16, 26, "" + double利息累計, objectSheet2); // 2017-05-02 B4474
    exeFun.putDataIntoExcel(17, 26, "" + double淨售累計, objectSheet2);
    exeFun.putDataIntoExcel(18, 26, "" + double底價累計, objectSheet2);
    exeFun.putDataIntoExcel(19, 26, "" + double超低價累計, objectSheet2);
    // retASale累計 END
    // 退戶率
    double double退戶率 = 0;
    if (double可售金額 != 0)
      double退戶率 = double售價累計 / double可售金額;
    exeFun.putDataIntoExcel(35, 26, "" + double退戶率, objectSheet2);
    // 重復性列印
    double double坪數小計 = 0;
    double double訂價小計 = 0;
    double double售價小計 = 0;
    double double贈送小計 = 0;
    double double佣金小計 = 0;
    double double中原佣金小計 = 0; // 2015-11-18 B3018
    double double利息小計 = 0; // 2017-05-02 B4474
    double double淨售小計 = 0;
    double double底價小計 = 0;
    double double超低價小計 = 0;

    double double坪數11 = 0;
    double double付訂金額5 = 0;
    double double補足金額7 = 0;
    double double簽約金額9 = 0;
    double double訂價12 = 0;
    double double售價13 = 0;
    double double贈送14 = 0;
    double double佣金15 = 0;
    double double中原佣金 = 0; // 2015-11-18 B3018
    double double利息 = 0; // 2017-05-02 B4474
    double double淨售16 = 0;
    double double底價17 = 0;
    double double超低價18 = 0;
    int intStartDataRow = exeFun.getStartDataRow();
    int intPageDataRow = exeFun.getPageDataRow();
    int intRecordNo = intStartDataRow;
    for (int intRetASale = 0; intRetASale < retASale.length; intRetASale++) {
      // No
      exeFun.putDataIntoExcel(0, intRecordNo, Integer.toString(intRetASale + 1), objectSheet2);
      //
      if ("房子".equals(stringThisType)) {
        if (!"".equals(retASale[intRetASale][0].trim())) { // Position
          exeFun.putDataIntoExcel(1, intRecordNo, retASale[intRetASale][0].trim(), objectSheet2);// Position
        } else {
          exeFun.putDataIntoExcel(1, intRecordNo, retASale[intRetASale][1].trim(), objectSheet2);// PositionRent
        }
      } else if ("車位".equals(stringThisType)) {
        if (!"".equals(retASale[intRetASale][2].trim())) { // Car
          exeFun.putDataIntoExcel(1, intRecordNo, retASale[intRetASale][2].trim(), objectSheet2);// Car
        } else {
          exeFun.putDataIntoExcel(1, intRecordNo, retASale[intRetASale][3].trim(), objectSheet2);// CarRent
        }
      }
      // 客戶姓名 Custom
      exeFun.putDataIntoExcel(2, intRecordNo, retASale[intRetASale][4].trim(), objectSheet2);
      // 付訂日期 OrderDate
      exeFun.putDataIntoExcel(3, intRecordNo, retASale[intRetASale][5].trim(), objectSheet2);
      // 補足日期 EnougDate
      exeFun.putDataIntoExcel(5, intRecordNo, retASale[intRetASale][9].trim(), objectSheet2);
      // 簽約日期 ContrDate
      exeFun.putDataIntoExcel(7, intRecordNo, retASale[intRetASale][13].trim(), objectSheet2);
      // 退戶日期 Deldate
      exeFun.putDataIntoExcel(9, intRecordNo, retASale[intRetASale][17].trim(), objectSheet2);
      // 坪數 PingSu
      double坪數11 = doParseDouble(retASale[intRetASale][18].trim(), "1-30");
      exeFun.putDataIntoExcel(10, intRecordNo, "" + double坪數11, objectSheet2);
      //
      stringHComLocal = retASale[intRetASale][54].trim();
      stringLComLocal = retASale[intRetASale][55].trim();
      booleanHouse = false;
      booleanLand = false;
      if ("".equals(stringHouseCar)) {
        // 房土
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      //
      double付訂金額5 = 0;
      double補足金額7 = 0;
      double簽約金額9 = 0;
      double訂價12 = 0;
      double售價13 = 0;
      double贈送14 = 0;
      double佣金15 = 0;
      double中原佣金 = 0; // 2015-11-18 B3018
      double利息 = 0; // 2017-05-02 B4474
      double淨售16 = 0;
      double底價17 = 0;
      double超低價18 = 0;
      if (booleanHouse) {
        // 付訂金額5 OrderMon2
        double付訂金額5 += doParseDouble(retASale[intRetASale][7].trim(), "1-31");
        // 補足金額7 EnougMon2
        double補足金額7 += doParseDouble(retASale[intRetASale][11].trim(), "1-32");
        // 簽約金額9 ContrMon2
        double簽約金額9 += doParseDouble(retASale[intRetASale][15].trim(), "1-33");
        // PreMoney2
        double訂價12 += doParseDouble(retASale[intRetASale][20].trim(), "1-34");
        // 售價13 DealMoney2
        double售價13 += doParseDouble(retASale[intRetASale][23].trim(), "1-35");
        // 贈送14 GiftMoney2
        double贈送14 += doParseDouble(retASale[intRetASale][26].trim(), "1-36");
        // 佣金15 CommMoney2
        double佣金15 += doParseDouble(retASale[intRetASale][29].trim(), "1-37");
        double中原佣金 += doParseDouble(retASale[intRetASale][62].trim(), "1-37"); // 2015-11-18 B3018
        // 淨售 PureMoney2
        double淨售16 += doParseDouble(retASale[intRetASale][32].trim(), "1-38");
        // 底價 LastMoney2
        double底價17 += doParseDouble(retASale[intRetASale][35].trim(), "1-39");
        // BalaMoney2
        double超低價18 += doParseDouble(retASale[intRetASale][38].trim(), "1-40");
        //
        double利息 += doParseDouble(retASale[intRetASale][65].trim(), "利息"); // 2017-05-02 B4474
      }
      if (booleanLand) {
        // 付訂金額5 OrderMon3
        double付訂金額5 += doParseDouble(retASale[intRetASale][8].trim(), "1-41");
        // 補足金額7 EnougMon3
        double補足金額7 += doParseDouble(retASale[intRetASale][12].trim(), "1-42");
        // 簽約金額9 ContrMon3
        double簽約金額9 += doParseDouble(retASale[intRetASale][16].trim(), "1-43");
        // 訂價 PreMoney3
        double訂價12 += doParseDouble(retASale[intRetASale][21].trim(), "1-44");
        // 售價13 DealMoney3
        double售價13 += doParseDouble(retASale[intRetASale][24].trim(), "1-45");
        // 贈送14 GiftMoney3
        double贈送14 += doParseDouble(retASale[intRetASale][27].trim(), "1-46");
        // 佣金15 CommMoney3
        double佣金15 += doParseDouble(retASale[intRetASale][30].trim(), "1-47");
        double中原佣金 += doParseDouble(retASale[intRetASale][63].trim(), "1-47"); // 2015-11-18 B3018
        // 淨售 PureMoney3
        double淨售16 += doParseDouble(retASale[intRetASale][33].trim(), "1-48");
        // 底價 LastMoney3
        double底價17 += doParseDouble(retASale[intRetASale][36].trim(), "1-49");
        // BalaMoney3
        double超低價18 += doParseDouble(retASale[intRetASale][39].trim(), "1-50");
        //
        double利息 += doParseDouble(retASale[intRetASale][66].trim(), "利息"); // 2017-05-02 B4474
      }
      exeFun.putDataIntoExcel(4, intRecordNo, "" + double付訂金額5, objectSheet2);
      exeFun.putDataIntoExcel(6, intRecordNo, "" + double補足金額7, objectSheet2);
      exeFun.putDataIntoExcel(8, intRecordNo, "" + double簽約金額9, objectSheet2);
      exeFun.putDataIntoExcel(11, intRecordNo, "" + double訂價12, objectSheet2);
      exeFun.putDataIntoExcel(12, intRecordNo, "" + double售價13, objectSheet2);
      exeFun.putDataIntoExcel(13, intRecordNo, "" + double贈送14, objectSheet2);
      exeFun.putDataIntoExcel(14, intRecordNo, "" + double佣金15, objectSheet2);
      exeFun.putDataIntoExcel(15, intRecordNo, "" + double中原佣金, objectSheet2); // 2015-11-18 B3018
      exeFun.putDataIntoExcel(16, intRecordNo, "" + double利息, objectSheet2); // 2017-05-02 B4474
      exeFun.putDataIntoExcel(17, intRecordNo, "" + double淨售16, objectSheet2);
      exeFun.putDataIntoExcel(18, intRecordNo, "" + double底價17, objectSheet2);
      exeFun.putDataIntoExcel(19, intRecordNo, "" + double超低價18, objectSheet2);
      // 售出人1 SaleName1
      exeFun.putDataIntoExcel(20, intRecordNo, retASale[intRetASale][40].trim(), objectSheet2);
      // 售出人2 SaleName2
      exeFun.putDataIntoExcel(21, intRecordNo, retASale[intRetASale][41].trim(), objectSheet2);
      // 售出人3 SaleName3
      exeFun.putDataIntoExcel(22, intRecordNo, retASale[intRetASale][42].trim(), objectSheet2);
      // 售出人4 SaleName4
      exeFun.putDataIntoExcel(23, intRecordNo, retASale[intRetASale][43].trim(), objectSheet2);
      // 售出人5 SaleName5
      exeFun.putDataIntoExcel(24, intRecordNo, retASale[intRetASale][44].trim(), objectSheet2);
      exeFun.putDataIntoExcel(25, intRecordNo, retASale[intRetASale][56].trim(), objectSheet2);// 售出人6 SaleName6
      exeFun.putDataIntoExcel(26, intRecordNo, retASale[intRetASale][57].trim(), objectSheet2);// 售出人7 SaleName7
      exeFun.putDataIntoExcel(27, intRecordNo, retASale[intRetASale][58].trim(), objectSheet2);// 售出人8 SaleName8
      exeFun.putDataIntoExcel(28, intRecordNo, retASale[intRetASale][59].trim(), objectSheet2);// 售出人9 SaleName9
      exeFun.putDataIntoExcel(29, intRecordNo, retASale[intRetASale][60].trim(), objectSheet2);// 售出人10 SaleName10

      // 媒體 MediaName
      exeFun.putDataIntoExcel(30, intRecordNo, retASale[intRetASale][45].trim(), objectSheet2);
      // 區域 ZoneName
      exeFun.putDataIntoExcel(31, intRecordNo, retASale[intRetASale][46].trim(), objectSheet2);
      // 業別 MajorName
      exeFun.putDataIntoExcel(32, intRecordNo, retASale[intRetASale][47].trim(), objectSheet2);
      // 用途 UseType
      exeFun.putDataIntoExcel(33, intRecordNo, retASale[intRetASale][48].trim(), objectSheet2);
      // 備註 Remark
      exeFun.putDataIntoExcel(34, intRecordNo, retASale[intRetASale][49].trim(), objectSheet2);
      // 票期 DateRange
      exeFun.putDataIntoExcel(35, intRecordNo, retASale[intRetASale][50].trim(), objectSheet2);
      // 合約會審 DateCheck
      exeFun.putDataIntoExcel(36, intRecordNo, retASale[intRetASale][51].trim(), objectSheet2);
      // 合約歸檔 DateFile
      exeFun.putDataIntoExcel(37, intRecordNo, retASale[intRetASale][52].trim(), objectSheet2);
      // 獎金請領 DateBonus
      exeFun.putDataIntoExcel(38, intRecordNo, retASale[intRetASale][53].trim(), objectSheet2);
      // 小計
      double坪數小計 += double坪數11;
      double訂價小計 += double訂價12;
      double售價小計 += double售價13;
      double贈送小計 += double贈送14;
      double佣金小計 += double佣金15;
      double中原佣金小計 += double中原佣金; // 2015-11-18 B3018
      double利息小計 += double利息; // 2017-05-02 B4474
      double淨售小計 += double淨售16;
      double底價小計 += double底價17;
      double超低價小計 += double超低價18;
      // 下一筆
      intRecordNo++;
      // 滿頁時必須將Sheet2 Copy Sheet1
      // if(intRecordNo == intStartDataRow){
      if (intRecordNo >= (intPageDataRow + intStartDataRow)) {
        // 小計列印
        // if(intRecordNo == retASale.length) {
        exeFun.putDataIntoExcel(10, 25, "" + double坪數小計, objectSheet2);
        exeFun.putDataIntoExcel(11, 25, "" + double訂價小計, objectSheet2);
        exeFun.putDataIntoExcel(12, 25, "" + double售價小計, objectSheet2);
        exeFun.putDataIntoExcel(13, 25, "" + double贈送小計, objectSheet2);
        exeFun.putDataIntoExcel(14, 25, "" + double佣金小計, objectSheet2);
        exeFun.putDataIntoExcel(15, 25, "" + double中原佣金小計, objectSheet2); // 2015-11-18 B3018
        exeFun.putDataIntoExcel(16, 25, "" + double利息小計, objectSheet2); // 2017-05-02 B4474
        exeFun.putDataIntoExcel(17, 25, "" + double淨售小計, objectSheet2);
        exeFun.putDataIntoExcel(18, 25, "" + double底價小計, objectSheet2);
        exeFun.putDataIntoExcel(19, 25, "" + double超低價小計, objectSheet2);
        // }
      }
      intRecordNo = exeFun.doChangePage(intRecordNo, objectSheet1, objectSheet2);
    } // For LOOP END
      // 複製未滿頁
    if (intRecordNo != intStartDataRow) {
      // 小計
      exeFun.putDataIntoExcel(10, 25, "" + double坪數小計, objectSheet2);
      exeFun.putDataIntoExcel(11, 25, "" + double訂價小計, objectSheet2);
      exeFun.putDataIntoExcel(12, 25, "" + double售價小計, objectSheet2);
      exeFun.putDataIntoExcel(13, 25, "" + double贈送小計, objectSheet2);
      exeFun.putDataIntoExcel(14, 25, "" + double佣金小計, objectSheet2);
      exeFun.putDataIntoExcel(15, 25, "" + double中原佣金小計, objectSheet2); // 2015-11-18 B3018
      exeFun.putDataIntoExcel(16, 25, "" + double利息小計, objectSheet2); // 2017-05-02 B4474
      exeFun.putDataIntoExcel(17, 25, "" + double淨售小計, objectSheet2);
      exeFun.putDataIntoExcel(18, 25, "" + double底價小計, objectSheet2);
      exeFun.putDataIntoExcel(19, 25, "" + double超低價小計, objectSheet2);
      // 複製
      exeFun.CopyPage(objectSheet1, objectSheet2);
      exeFun.doClearContents("A" + (intStartDataRow + 1) + ":AL" + (intStartDataRow + intPageDataRow), objectSheet2);
      exeFun.doAdd1PageNo();
    }
    // 頁尾列印
    // 釋放 Excel 物件
    exeFun.getReleaseExcelObject(retVector);
    return true;
  }

  public boolean doSaleOutDetailRent(String stringExcelName, String stringTableSelect) throws Throwable {
    Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(6, 20, 34, 1);
    // 資料處理
    String stringSql = "";
    String stringHComLocal = "";
    String stringLComLocal = "";
    String stringProjectID = getValue("ProjectID").trim();
    String stringOrderDateStart = getValue("OrderDateStart").trim();
    String stringOrderDateEnd = getValue("OrderDateEnd").trim();
    String stringDistributeNo = getValue("Distribute").trim();
    String stringHouseLand = getValue("HouseLand").trim();
    String stringHouseCar = "";
    String stringFunction = getValue("Function").trim();
    String stringThisType = "";
//      String     stringOrderForm         =  getValue("OrderForm").trim( ) ;
//      String     stringOrderBy             =  "" ;
    //
    switch (Integer.parseInt(stringFunction)) {
    case 0:
    case 1:
    case 2:
      stringThisType = "房子"; // 房子車位
      break;
    case 3:
    case 4:
    case 5:
      stringThisType = "車位"; // 房子車位
      break;
    }
    switch (Integer.parseInt(stringHouseLand)) {
    case 0:
      stringHouseCar = "";
      break;
    case 1:
      stringHouseCar = "H_";
      break;
    case 2:
      stringHouseCar = "L_";
      break;
    }
    // 取得資料
    /*
     * 0.PositionRent 1.CarRent 2.Custom 3.OrderDate 4.OrderMon1 5.EnougDate
     * 6.EnougMon1 7.ContrDate 8.ContrMon1 9.Deldate 10.PingSu 11.RentRange
     * 12.RentLast 13.PingRent 14.Rent 15.RentFree 16.Guranteer 17.PreMoney1
     * 18.DealMoney1 19.GiftMoney1 20.CommMoney1 21.PureMoney1 22.LastMoney1
     * 23.BalaMoney1 24.SaleName1 25.SaleName2 26.SaleName3 27.SaleName4
     * 28.SaleName5 29.MediaName 30.ZoneName 31.MajorName 32.UseType 33.Remark
     * 34.DateRange 35.DateCheck 36.DateFile 37.DateBonus 38.SaleName6 39.SaleName7
     * 40.SaleName8 41.SaleName9 42.SaleName10 43 H_Com 44.L_Com 45.DealMoney
     * 46.H_DealMoney 47.L_DealMoney 48 CommMoney1 49 H_LastMoney 50 L_LastMoney 51
     * H_GiftMoney 52 L_GiftMoney 53 H_CommMoney 54 L_CommMoney 55 H_CommMoney1 56
     * L_CommMoney1 57 H_BalaMoney 58 L_BalaMoney
     */
    String[][] retASale = getRentForASale(stringHouseCar, stringProjectID, stringOrderDateStart, stringOrderDateEnd, stringThisType);
    // 有無資料檢查
    if (retASale.length == 0) {
      message("查無資料。");
      return false;
    }
    // 取得 Exce 物件
    Vector retVector = exeFun.getExcelObject("G:\\資訊室\\Excel\\SaleEffect\\" + stringExcelName);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);
    Dispatch objectClick = null;
    exeFun.setClearCol(0, 39);
    //
    // 一致性列印
    // 欄位變更
    if ("房子".equals(stringThisType)) {
      exeFun.putDataIntoExcel(0, 3, "棟樓出租", objectSheet2);
    } else if ("車位".equals(stringThisType)) {
      exeFun.putDataIntoExcel(0, 3, "車位出租", objectSheet2);
    }
    // 公司名
    exeFun.putDataIntoExcel(0, 1, getACom(stringDistributeNo), objectSheet2);
    // 案別
    exeFun.putDataIntoExcel(0, 3, "案別：" + stringProjectID, objectSheet2);
    // 日期
    // if("B3018".equals(getUser()))
    // messagebox(stringOrderDateStart+"："+getConertFormatDate(stringOrderDateStart)
    // + "~" + stringOrderDateEnd+"："+getConertFormatDate(stringOrderDateEnd));
    exeFun.putDataIntoExcel(5, 3, "日期：" + getConertFormatDate(stringOrderDateStart) + "~" + getConertFormatDate(stringOrderDateEnd), objectSheet2);
    // 條件SQL
    String stringConditionDescription = getConditionDescription();
    String stringConditionDescription2 = getConditionDescription2();
    exeFun.putDataIntoExcel(21, 3, stringConditionDescription + stringConditionDescription2, objectSheet2);
    // retAProjectLocal START
    // 0 H_Com 1 L_Com 2 OpenDate 3 PositionMoneyR1 4 CarMoneyR1
    String[][] retAProjectLocal = getRentForAProject(stringProjectID, stringHouseCar);
    stringHComLocal = retAProjectLocal[0][0];
    stringLComLocal = retAProjectLocal[0][1];

    // 公開日期
    exeFun.putDataIntoExcel(34, 3, "公開日期：" + getConertFormatDate(convert.replace(retAProjectLocal[0][2].trim(), "-", "").substring(0, 8)), objectSheet2);
    // 可售金額
    if ("房子".equals(stringThisType)) {
      exeFun.putDataIntoExcel(2, 25, retAProjectLocal[0][3], objectSheet2); // PositionMoneyR1
    } else if ("房子".equals(stringThisType)) {
      exeFun.putDataIntoExcel(2, 25, retAProjectLocal[0][4], objectSheet2); // CarMoneyR1
    }
    // retAProjectLocal END
    // 重復-----retASale
    double double坪數小計 = 0;
    double double租期月小計 = 0;
    double double租金坪小計 = 0;
    double double租金月小計 = 0;
    double double總租金小計 = 0;
    double double贈送小計 = 0;
    double double佣金小計 = 0;
    double double中原佣金小計 = 0; // 2015-11-18 B3018
    double double利息小計 = 0; // 2017-05-02 B4474
    double double總底價小計 = 0;
    double double超低價小計 = 0;
    double double免租期小計 = 0;
    double double坪數9 = 0;
    double double租金底價月11 = 0;
    double double租期月10 = 0;
    double double租金坪12 = 0;
    double double租金月13 = 0;
    double double總租金14 = 0;
    double double贈送15 = 0;
    double double佣金16 = 0;
    double double中原佣金 = 0; // 2015-11-18 B3018
    double double利息 = 0; // 2017-05-02 B4474
    double double總底價17 = 0;
    double double超低價18 = 0;
    double double免租期19 = 0;
    int intStartDataRow = exeFun.getStartDataRow();
    int intPageDataRow = exeFun.getPageDataRow();
    int intRecordNo = intStartDataRow;
    boolean booleanHouse = false;
    boolean booleanLand = false;
    for (int intRetASale = 0; intRetASale < retASale.length; intRetASale++) {
      double坪數9 = 0;
      double租期月10 = 0;
      double租金底價月11 = 0;
      double租金坪12 = 0;
      double租金月13 = 0;
      double總租金14 = 0;
      double贈送15 = 0;
      double佣金16 = 0;
      double中原佣金 = 0; // 2015-11-18 B3018
      double利息 = 0; // 2017-05-02 B4474
      double總底價17 = 0;
      double超低價18 = 0;
      double免租期19 = 0;
      // No
      exeFun.putDataIntoExcel(0, intRecordNo, "" + (intRetASale + 1), objectSheet2);
      //
      if ("房子".equals(stringThisType)) {
        exeFun.putDataIntoExcel(1, intRecordNo, retASale[intRetASale][0].trim(), objectSheet2);// PositionRent
      } else if ("車位".equals(stringThisType)) {
        exeFun.putDataIntoExcel(1, intRecordNo, retASale[intRetASale][1].trim(), objectSheet2);// CarRent
      }
      // 客戶姓名 Custom
      exeFun.putDataIntoExcel(2, intRecordNo, retASale[intRetASale][2].trim(), objectSheet2);
      // 付訂日期 OrderDate
      exeFun.putDataIntoExcel(3, intRecordNo, retASale[intRetASale][3].trim(), objectSheet2);
      // 付訂金額 OrderMon1
      exeFun.putDataIntoExcel(4, intRecordNo, retASale[intRetASale][4].trim(), objectSheet2);
      // 簽約日期 ContrDate
      exeFun.putDataIntoExcel(5, intRecordNo, retASale[intRetASale][7].trim(), objectSheet2);
      // 簽約金額 ContrMon1
      exeFun.putDataIntoExcel(6, intRecordNo, retASale[intRetASale][8].trim(), objectSheet2);
      // 退戶日期 Deldate
      exeFun.putDataIntoExcel(7, intRecordNo, retASale[intRetASale][9].trim(), objectSheet2);
      // 坪數 PingSu
      double坪數9 = doParseDouble(retASale[intRetASale][10].trim(), "1");
      exeFun.putDataIntoExcel(8, intRecordNo, "" + double坪數9, objectSheet2);
      // 租期_月 RentRange
      double租期月10 = doParseDouble(retASale[intRetASale][11].trim(), "2");
      exeFun.putDataIntoExcel(9, intRecordNo, "" + double租期月10, objectSheet2);
      // 租金底價(月) RentLast (隱藏)
      double租金底價月11 = doParseDouble(retASale[intRetASale][12].trim(), "3");
      exeFun.putDataIntoExcel(10, intRecordNo, "" + double租金底價月11, objectSheet2);
      // 租金(坪) PingRent
      double租金坪12 = doParseDouble(retASale[intRetASale][13].trim(), "4");
      exeFun.putDataIntoExcel(11, intRecordNo, "" + double租金坪12, objectSheet2);
      // 租金(月) Rent
      double租金月13 = doParseDouble(retASale[intRetASale][14].trim(), "5");
      exeFun.putDataIntoExcel(12, intRecordNo, "" + double租金月13, objectSheet2);
      // 總租金
      // double總租金14 = double租期月10 * double租金月13 ;
      /*
       * if("0".equals(stringHouseLand)) { // 全部 //double總租金14 =
       * doParseDouble(retASale[intRetASale][45].trim( ), "10-1") ; // double總租金14 = 0
       * ; if("".equals(stringDistributeNo) ||
       * stringDistributeNo.equals(retASale[intRetASale][43].trim( )))double總租金14 +=
       * doParseDouble(retASale[intRetASale][46].trim( ), "10-1") ;
       * if("".equals(stringDistributeNo) ||
       * stringDistributeNo.equals(retASale[intRetASale][44].trim( )))double總租金14 +=
       * doParseDouble(retASale[intRetASale][47].trim( ), "10-2") ; } else
       * if("1".equals(stringHouseLand)) { // 房屋 double總租金14 =
       * doParseDouble(retASale[intRetASale][46].trim( ), "10-3") ; //
       * if(!"".equals(stringDistributeNo) &&
       * !stringDistributeNo.equals(retASale[intRetASale][43].trim( )))double總租金14 = 0
       * ; } else if("2".equals(stringHouseLand)) { // 土地 double總租金14 =
       * doParseDouble(retASale[intRetASale][47].trim( ), "10-4") ; //
       * if(!"".equals(stringDistributeNo) &&
       * !stringDistributeNo.equals(retASale[intRetASale][44].trim( )))double總租金14 = 0
       * ; } else { double總租金14 = 0 ; } //
       * 
       * exeFun.putDataIntoExcel(13, intRecordNo, ""+double總租金14, objectSheet2) ;
       */
      // 贈送 GiftMoney1
      // double贈送15 = doParseDouble(retASale[intRetASale][19].trim( ), "6") ;
      // exeFun.putDataIntoExcel(14, intRecordNo, ""+double贈送15, objectSheet2) ;
      // 佣金 CommMoney1
      // double佣金16 = doParseDouble(retASale[intRetASale][20].trim( ), "7") ;
      // exeFun.putDataIntoExcel(15, intRecordNo, ""+double佣金16, objectSheet2) ;
      // 中原佣金 CommMoney1
      // double中原佣金 = doParseDouble(retASale[intRetASale][48].trim( ), "7") ; //
      // 2015-11-18 B3018
      // exeFun.putDataIntoExcel(16, intRecordNo, ""+double中原佣金, objectSheet2) ; //
      // 2015-11-18 B3018
      // 總底價
      // double總底價17 = double租期月10 * double租金底價月11 ;
      // if("B3018".equals(getUser())) messagebox(convert.FourToFive(""+double租期月10,
      // 0)+" * "+convert.FourToFive(""+double租金底價月11, 0)+" =
      // "+convert.FourToFive(""+double總底價17, 0)) ;
      // exeFun.putDataIntoExcel(17, intRecordNo, ""+double總底價17, objectSheet2) ;
      // 超低價 BalaMoney1
      // double超低價18 = doParseDouble(retASale[intRetASale][23].trim( ), "8") ;
      // exeFun.putDataIntoExcel(18, intRecordNo, ""+double超低價18, objectSheet2) ;

      /** 2017/08/02 B4474 修改 將EXCEL 往後推一格 EX 免租變 20 原 19 **/

      // 免租期 RentFree
      double免租期19 = doParseDouble(retASale[intRetASale][15].trim(), "9");
      exeFun.putDataIntoExcel(20, intRecordNo, "" + double免租期19, objectSheet2);
      // 保證金 Guranteer
      exeFun.putDataIntoExcel(21, intRecordNo, retASale[intRetASale][16].trim(), objectSheet2);
      // 售出人1 SaleName1
      exeFun.putDataIntoExcel(22, intRecordNo, retASale[intRetASale][24].trim(), objectSheet2);
      // 售出人2 SaleName2
      exeFun.putDataIntoExcel(23, intRecordNo, retASale[intRetASale][25].trim(), objectSheet2);
      // 售出人3 SaleName3
      exeFun.putDataIntoExcel(24, intRecordNo, retASale[intRetASale][26].trim(), objectSheet2);
      // 售出人4 SaleName4
      exeFun.putDataIntoExcel(25, intRecordNo, retASale[intRetASale][27].trim(), objectSheet2);
      // 售出人5 SaleName5
      exeFun.putDataIntoExcel(26, intRecordNo, retASale[intRetASale][28].trim(), objectSheet2);
      exeFun.putDataIntoExcel(27, intRecordNo, retASale[intRetASale][38].trim(), objectSheet2);// 售出人6 SaleName6
      exeFun.putDataIntoExcel(28, intRecordNo, retASale[intRetASale][39].trim(), objectSheet2);// 售出人7 SaleName7
      exeFun.putDataIntoExcel(29, intRecordNo, retASale[intRetASale][40].trim(), objectSheet2);// 售出人8 SaleName8
      exeFun.putDataIntoExcel(30, intRecordNo, retASale[intRetASale][41].trim(), objectSheet2);// 售出人9 SaleName9
      exeFun.putDataIntoExcel(31, intRecordNo, retASale[intRetASale][42].trim(), objectSheet2);// 售出人10 SaleName10

      // 媒體 MediaName
      exeFun.putDataIntoExcel(32, intRecordNo, retASale[intRetASale][29].trim(), objectSheet2);
      // 區域 ZoneName
      exeFun.putDataIntoExcel(33, intRecordNo, retASale[intRetASale][30].trim(), objectSheet2);
      // 業別 MajorName
      exeFun.putDataIntoExcel(34, intRecordNo, retASale[intRetASale][31].trim(), objectSheet2);
      // 用途 UseType
      exeFun.putDataIntoExcel(35, intRecordNo, retASale[intRetASale][32].trim(), objectSheet2);
      // 備註 Remark
      exeFun.putDataIntoExcel(36, intRecordNo, retASale[intRetASale][33].trim(), objectSheet2);
      // 票期 DateRange
      exeFun.putDataIntoExcel(37, intRecordNo, retASale[intRetASale][34].trim(), objectSheet2);
      // 合約會審 DateCheck
      exeFun.putDataIntoExcel(38, intRecordNo, retASale[intRetASale][35].trim(), objectSheet2);
      // 合約歸檔 DateFile
      exeFun.putDataIntoExcel(39, intRecordNo, retASale[intRetASale][36].trim(), objectSheet2);
      // 獎金請領 DateBonus
      exeFun.putDataIntoExcel(40, intRecordNo, retASale[intRetASale][37].trim(), objectSheet2);
      // 小計
      stringHComLocal = retASale[intRetASale][43].trim();
      stringLComLocal = retASale[intRetASale][44].trim();
      booleanHouse = false;
      booleanLand = false;
      if ("".equals(stringHouseCar)) {
        // 房土 oce
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      double總租金14 = 0;
      double贈送15 = 0;
      double佣金16 = 0;
      double中原佣金 = 0;
      double利息 = 0;
      double總底價17 = 0;
      double超低價18 = 0;
      if (booleanHouse) { // H_Com
        double總租金14 += doParseDouble(retASale[intRetASale][46].trim(), "10-3");
        double贈送15 += doParseDouble(retASale[intRetASale][51].trim(), "10-3");
        double佣金16 += doParseDouble(retASale[intRetASale][53].trim(), "10-3");
        double中原佣金 += doParseDouble(retASale[intRetASale][55].trim(), "10-3");
        double利息 += doParseDouble(retASale[intRetASale][59].trim(), "利息");
        double總底價17 += doParseDouble(retASale[intRetASale][49].trim(), "17-1");
        double超低價18 += doParseDouble(retASale[intRetASale][57].trim(), "17-1");
      }
      if (booleanLand) { // L_Com
        double總租金14 += doParseDouble(retASale[intRetASale][47].trim(), "10-4");
        double贈送15 += doParseDouble(retASale[intRetASale][52].trim(), "10-4");
        double佣金16 += doParseDouble(retASale[intRetASale][54].trim(), "10-4");
        double中原佣金 += doParseDouble(retASale[intRetASale][56].trim(), "10-4");
        double利息 += doParseDouble(retASale[intRetASale][60].trim(), "利息");
        double總底價17 += doParseDouble(retASale[intRetASale][50].trim(), "17-1");
        double超低價18 += doParseDouble(retASale[intRetASale][58].trim(), "17-1");
      }
      exeFun.putDataIntoExcel(13, intRecordNo, "" + double總租金14, objectSheet2);
      exeFun.putDataIntoExcel(14, intRecordNo, "" + double贈送15, objectSheet2);
      exeFun.putDataIntoExcel(15, intRecordNo, "" + double佣金16, objectSheet2);
      exeFun.putDataIntoExcel(16, intRecordNo, "" + double中原佣金, objectSheet2); // 2015-11-18 B3018
      exeFun.putDataIntoExcel(17, intRecordNo, "" + double利息, objectSheet2); // 2017-05-02 B4474
      exeFun.putDataIntoExcel(18, intRecordNo, "" + double總底價17, objectSheet2);
      exeFun.putDataIntoExcel(19, intRecordNo, "" + double超低價18, objectSheet2);

      //
      double坪數小計 += double坪數9;
      double租期月小計 += double租期月10;
      double租金坪小計 += double租金坪12;
      double租金月小計 += double租金月13;
      double總租金小計 += double總租金14;
      double贈送小計 += double贈送15;
      double佣金小計 += double佣金16;
      double中原佣金小計 += double中原佣金; // 2015-11-18 B3018
      double利息小計 += double利息; // 2017-05-02 B4474
      double總底價小計 += double總底價17;
      double超低價小計 += double超低價18;
      double免租期小計 += double免租期19;
      intRecordNo++;
      // 滿頁時必須將Sheet2 Copy Sheet1
      if (intRecordNo >= (intPageDataRow + intStartDataRow)) {
        // 小計列印
        // if(intRetASale == retASale.length) {
        exeFun.putDataIntoExcel(8, 25, "" + double坪數小計, objectSheet2);
        exeFun.putDataIntoExcel(9, 25, "" + double租期月小計, objectSheet2);
        exeFun.putDataIntoExcel(11, 25, "" + double租金坪小計, objectSheet2);
        exeFun.putDataIntoExcel(12, 25, "" + double租金月小計, objectSheet2);
        exeFun.putDataIntoExcel(13, 25, "" + double總租金小計, objectSheet2);
        exeFun.putDataIntoExcel(14, 25, "" + double贈送小計, objectSheet2);
        exeFun.putDataIntoExcel(15, 25, "" + double佣金小計, objectSheet2);
        exeFun.putDataIntoExcel(16, 25, "" + double中原佣金小計, objectSheet2); // 2015-11-18 B3018
        exeFun.putDataIntoExcel(17, 25, "" + double利息小計, objectSheet2); // 2017-05-02 B4474
        exeFun.putDataIntoExcel(18, 25, "" + double總底價小計, objectSheet2);
        exeFun.putDataIntoExcel(19, 25, "" + double超低價小計, objectSheet2);
        exeFun.putDataIntoExcel(20, 25, "" + double免租期小計, objectSheet2);
        // }
      }
      intRecordNo = exeFun.doChangePage(intRecordNo, objectSheet1, objectSheet2);
      /*
       * if(intRecordNo == intStartDataRow){ //小計列印 if(intRetASale == retASale.length)
       * { exeFun.putDataIntoExcel(8, 25, ""+double坪數小計, objectSheet2) ;
       * exeFun.putDataIntoExcel(9, 25, ""+double租期月小計, objectSheet2) ;
       * exeFun.putDataIntoExcel(11, 25, ""+double租金坪小計, objectSheet2) ;
       * exeFun.putDataIntoExcel(12, 25, ""+double租金月小計, objectSheet2) ;
       * exeFun.putDataIntoExcel(13, 25, ""+double總租金小計, objectSheet2) ;
       * exeFun.putDataIntoExcel(14, 25, ""+double贈送小計, objectSheet2) ;
       * exeFun.putDataIntoExcel(15, 25, ""+double佣金小計, objectSheet2) ;
       * exeFun.putDataIntoExcel(16, 25, ""+double中原佣金小計, objectSheet2) ; //
       * 2015-11-18 B3018 exeFun.putDataIntoExcel(17, 25, ""+double總底價小計,
       * objectSheet2) ; exeFun.putDataIntoExcel(18, 25, ""+double超低價小計, objectSheet2)
       * ; exeFun.putDataIntoExcel(19, 25, ""+double免租期小計, objectSheet2) ; } }
       */
    } // For LOOP END
      // 複製未滿頁
    if (intRecordNo != intStartDataRow) {
      // 小計
      exeFun.putDataIntoExcel(8, 25, "" + double坪數小計, objectSheet2);
      exeFun.putDataIntoExcel(9, 25, "" + double租期月小計, objectSheet2);
      exeFun.putDataIntoExcel(11, 25, "" + double租金坪小計, objectSheet2);
      exeFun.putDataIntoExcel(12, 25, "" + double租金月小計, objectSheet2);
      exeFun.putDataIntoExcel(13, 25, "" + double總租金小計, objectSheet2);
      exeFun.putDataIntoExcel(14, 25, "" + double贈送小計, objectSheet2);
      exeFun.putDataIntoExcel(15, 25, "" + double佣金小計, objectSheet2);
      exeFun.putDataIntoExcel(16, 25, "" + double中原佣金小計, objectSheet2); // 2015-11-18 B3018
      exeFun.putDataIntoExcel(17, 25, "" + double利息小計, objectSheet2); // 2017-05-02 B4474
      exeFun.putDataIntoExcel(18, 25, "" + double總底價小計, objectSheet2);
      exeFun.putDataIntoExcel(19, 25, "" + double超低價小計, objectSheet2);
      exeFun.putDataIntoExcel(20, 25, "" + double免租期小計, objectSheet2);
      // 複製
      exeFun.CopyPage(objectSheet1, objectSheet2);
      exeFun.doClearContents("A" + (intStartDataRow + 1) + ":AN" + (intStartDataRow + intPageDataRow), objectSheet2);
      exeFun.doAdd1PageNo();
    }
    // 頁尾列印
    // 釋放 Excel 物件
    exeFun.getReleaseExcelObject(retVector);
    return true;
  }

  public boolean doSaleOutDetailSale(String stringExcelName, String stringTableSelect) throws Throwable {
    Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(6, 20, 28, 1);
    //
    String stringTmp = "";
    String stringTmp2 = "";
    String stringSql = "";
    String stringOrderDateStart = getValue("OrderDateStart").trim();
    String stringOrderDateEnd = getValue("OrderDateEnd").trim();
    String stringProjectID = getValue("ProjectID").trim();
    String stringDistributeNo = getValue("Distribute").trim();
    String stringHouseLand = getValue("HouseLand").trim();
    String stringHouseCar = "";
    String stringFunction = getValue("Function").trim();
    String stringThisType = "";
    String stringOrderForm = getValue("OrderForm").trim();
    String stringOrderBy = "";
    String stringLComLocal = "";
    String stringHComLocal = "";
    String[][] retASale2 = null;
    double double本月成交單價 = 0;
    boolean booleanHouse = true;
    boolean booleanLand = true;
    // retAProjectLocal START
    String[][] retAProjectLocal = getSaleForAProject(stringProjectID);
    //
    stringHComLocal = retAProjectLocal[0][0];
    stringLComLocal = retAProjectLocal[0][1];
    // retAProjectLocal END
    switch (Integer.parseInt(stringFunction)) {
    case 0:
    case 1:
    case 2:
      stringThisType = "房子"; // 房子車位
      if ("1".equals(stringOrderForm)) {
        stringOrderBy = "OrderDate"; // 排序方式
      } else {
        stringOrderBy = "Position"; // 排序方式
      }
      break;
    case 3:
    case 4:
    case 5:
      stringThisType = "車位"; // 房子車位
      if ("1".equals(stringOrderForm)) {
        stringOrderBy = "Car"; // 排序方式
      } else {
        stringOrderBy = "OrderDate"; // 排序方式
      }
      break;
    }
    switch (Integer.parseInt(stringHouseLand)) {
    case 0:
      stringHouseCar = "";
      break;
    case 1:
      stringHouseCar = "H_";
      break;
    case 2:
      stringHouseCar = "L_";
      break;
    }
    // 主體
    /*
     * OrderMon2 0 _ H_Com 1 _ L_Com 2 _ Position 3 _ Car 4 _ Custom 5 _ OrderDate 6
     * _ OrderMon1 7 _ OrderMon2 8 _ OrderMon3 9 _EnougDate 10 _ EnougMon1 11 _
     * EnougMon2 12 _ EnougMon3 13 _ ContrDate 14 _ ContrMon1 15 _ ContrMon2 16 _
     * ContrMon3 16 _ Deldate 18 _ PingSu 19 _ PreMoney1 20 _ PreMoney2 21 _
     * PreMoney3 22 _ DealMoney1 23 _ DealMoney2 24 _ DealMoney3 25 _ GiftMoney1 26
     * _ GiftMoney2 27 _ GiftMoney3 28 _ CommMoney1 29 _ CommMoney2 30 _ CommMoney3
     * 31 _ PureMoney1 32 _ PureMoney2 33 _ PureMoney3 34 _ LastMoney1 35 _
     * LastMoney2 36 _ LastMoney3 37 _ BalaMoney1 38 _ BalaMoney2 39 _ BalaMoney3 40
     * _ SaleName1 41 _ SaleName2 42 _ SaleName3 43 _ SaleName4 44 _ SaleName5 45 _
     * MediaName 46 _ ZoneName 47 _ MajorName 48 _ UseType 49 _ Remark 50 _
     * DateRange 51 _ DateCheck 52 _ DateFile 53 _ DateBonus 54 _ SaleName6 55 _
     * SaleName7 56 _ SaleName8 57 _ SaleName9 58 _ SaleName10 59 _ CommMoney1 60 _
     * CommMoney2 61 _ CommMoney1 62 ViMoney 63 64
     */
    String[][] retASale = getSaleForASale(stringHouseCar, stringProjectID, stringOrderDateStart, stringOrderDateEnd, stringThisType, stringOrderBy, getSqlAnd(), getSqlAnd2());
    if (retASale.length == 0) {
      message("查無資料。");
      return false;
    }
    // 取得 Excel 物件
    Vector retVector = exeFun.getExcelObject("G:\\資訊室\\Excel\\SaleEffect\\" + stringExcelName);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);
    Dispatch objectClick = null;
    exeFun.setClearCol(0, 37);
    // 頁首
    // 更改欄位名稱
    if ("房子".equals(stringThisType)) {
      stringTmp2 = "棟樓別";
    } else if ("車位".equals(stringThisType)) {
      stringTmp2 = "車位別";
    }
    exeFun.putDataIntoExcel(1, 4, stringTmp2, objectSheet2);
    // 公司名
    exeFun.putDataIntoExcel(0, 1, getACom(stringDistributeNo), objectSheet2);
    // 案別
    exeFun.putDataIntoExcel(0, 3, "案別：" + stringProjectID, objectSheet2);
    // 日期
    exeFun.putDataIntoExcel(4, 3, "日期：" + getConertFormatDate(stringOrderDateStart) + "~" + getConertFormatDate(stringOrderDateEnd), objectSheet2);
    // 條件SQL
    String stringConditionDescription = getConditionDescription();
    String stringConditionDescription2 = getConditionDescription2();
    exeFun.putDataIntoExcel(19, 3, stringConditionDescription + stringConditionDescription2, objectSheet2);
    // 公開日期
    exeFun.putDataIntoExcel(30, 3, "公開日期：" + getConertFormatDate(convert.replace(retAProjectLocal[0][2].trim(), "-", "").substring(0, 8)), objectSheet2);
    // 累計
    // 累計戶數
    // String[][] retTotRecForASale = getTotRecForASale(stringProjectID,
    // getSqlAnd(), getSqlAnd2(), stringThisType) ;
    // exeFun.putDataIntoExcel(2, 26, retTotRecForASale[0][0].trim(), objectSheet2)
    // ;//MajorName
    // 可售金額
    // retAProject START
    // 0 H_Com 1 L_Com
    // 2. PositionMoneyS1 3. PositionMoneyS2 4. PositionMoneyS3
    // 5. CarMoneyS1 6. CarMoneyS2 7. CarMoneyS3
    // 8. OpenDate
    String[][] retAProject = getSaleForAProject(stringProjectID, stringHouseCar);
    double double可售金額3 = 0;
    for (int intNo = 0; intNo < retAProject.length; intNo++) {
      booleanHouse = false;
      booleanLand = false;
      stringHComLocal = retAProject[intNo][0].trim();
      stringLComLocal = retAProject[intNo][1].trim();
      if ("".equals(stringHouseCar)) {
        // 房土
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      if ("房子".equals(stringThisType)) {
        if (booleanHouse)
          double可售金額3 += doParseDouble(retAProject[intNo][3].trim(), "2"); // PositionMoneyS2
        if (booleanLand)
          double可售金額3 += doParseDouble(retAProject[intNo][4].trim(), "3"); // PositionMoneyS3
      } else if ("車位".equals(stringThisType)) {
        if (booleanHouse)
          double可售金額3 += doParseDouble(retAProject[intNo][6].trim(), "5"); // CarMoneyS2
        if (booleanLand)
          double可售金額3 += doParseDouble(retAProject[intNo][7].trim(), "6"); // CarMoneyS3
      }
    }
    exeFun.putDataIntoExcel(2, 25, "" + double可售金額3, objectSheet2);// MajorName
    // retAProject END
    // 累計
    double double坪數累計11 = 0;
    double double訂價累計12 = 0;
    double double售價累計13 = 0;
    double double贈送累計14 = 0;
    double double佣金累計15 = 0;
    double double中原佣金累計 = 0; // 2015-11-18 B3018
    double double利息累計 = 0; // 2017-05-02 B4474
    double double淨售累計16 = 0;
    double double底價累計17 = 0;
    double double超低價累計18 = 0;
    /*
     * 0 H_Com 1 L_Com 2. SumPingSu 3. SumPreMoney 4. SumPreMoney2 5. SumPreMoney3
     * 6. SumDealMoney 7. SumDealMoney2 8. SumDealMoney3 9. SumGiftMoney 10.
     * SumGiftMoney2 11. SumGiftMoney3 12.SumCommMoney 13.SumCommMoney2 14.
     * SumCommMoney3 15. SumPureMoney 16. SumPureMoney2 17. SumPureMoney3 18.
     * SumLastMoney 19. SumLastMoney2 20. SumLastMoney3 21.SumBalaMoney 22.
     * SumBalaMoney2 23. SumBalaMoney3
     */
    // System.out.println("-----------------------------------------累計") ;
    retASale2 = getSumSaleForASale(stringHouseCar, stringProjectID, stringOrderDateEnd, stringThisType, getSqlAnd2());
    //
    double坪數累計11 = 0;
    double訂價累計12 = 0;
    double售價累計13 = 0;
    double贈送累計14 = 0;
    double佣金累計15 = 0;
    double中原佣金累計 = 0; // 2015-11-18 B3018
    double利息累計 = 0; // 2017-05-02 B4474
    double淨售累計16 = 0;
    double底價累計17 = 0;
    double超低價累計18 = 0;
    for (int intNo = 0; intNo < retASale2.length; intNo++) {
      if ("Z".equals(stringProjectID.trim())) {// ** M^2 Convert 坪
        double坪數累計11 += doParseDouble(retASale2[intNo][2].trim(), "8") * 0.3025; // SumPingSu
      } else {
        double坪數累計11 += doParseDouble(retASale2[intNo][2].trim(), "9");
      }
      booleanHouse = false;
      booleanLand = false;
      stringHComLocal = retASale2[intNo][0].trim();
      stringLComLocal = retASale2[intNo][1].trim();
      if ("".equals(stringHouseCar)) {
        // 房土
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      if (booleanHouse) {
        double訂價累計12 += doParseDouble(retASale2[intNo][4].trim(), "10"); // SumPreMoney2
        double售價累計13 += doParseDouble(retASale2[intNo][7].trim(), "11");// SumDealMoney2
        double贈送累計14 += doParseDouble(retASale2[intNo][10].trim(), "12");// SumGiftMoney2
        double佣金累計15 += doParseDouble(retASale2[intNo][13].trim(), "13");// SumCommMoney2
        double中原佣金累計 += doParseDouble(retASale2[intNo][25].trim(), "13");// SumCommMoney12 // 2015-11-18 B3018
        double利息累計 += doParseDouble(retASale2[intNo][28].trim(), "13");// SumCommMoney12 // 2017-05-02 B4474
        double淨售累計16 += doParseDouble(retASale2[intNo][16].trim(), "14");// SumPureMoney2
        double底價累計17 += doParseDouble(retASale2[intNo][19].trim(), "15");// SumLastMoney2
        double超低價累計18 += doParseDouble(retASale2[intNo][22].trim(), "16");// SumBalaMoney2
      }
      if (booleanLand) {
        double訂價累計12 += doParseDouble(retASale2[intNo][5].trim(), "17"); // .SumPreMoney3
        double售價累計13 += doParseDouble(retASale2[intNo][8].trim(), "18"); // SumDealMoney3
        double贈送累計14 += doParseDouble(retASale2[intNo][11].trim(), "19"); // SumGiftMoney3
        double佣金累計15 += doParseDouble(retASale2[intNo][14].trim(), "20"); // SumCommMoney3
        double中原佣金累計 += doParseDouble(retASale2[intNo][26].trim(), "20"); // SumCommMoney13 // 2015-11-18 B3018
        double利息累計 += doParseDouble(retASale2[intNo][29].trim(), "20"); // SumCommMoney13 // 2017-05-02 B4474
        double淨售累計16 += doParseDouble(retASale2[intNo][17].trim(), "21"); // SumPureMoney3
        double底價累計17 += doParseDouble(retASale2[intNo][20].trim(), "22"); // SumLastMoney3
        double超低價累計18 += doParseDouble(retASale2[intNo][23].trim(), "23"); // SumBalaMoney3
      }
    }
    exeFun.putDataIntoExcel(10, 26, "" + double坪數累計11, objectSheet2);
    // (intPageNoGlobal-2)*intPageAllRowGlobal+26
    exeFun.putDataIntoExcel(11, 26, "" + double訂價累計12, objectSheet2);
    exeFun.putDataIntoExcel(12, 26, "" + double售價累計13, objectSheet2);//
    exeFun.putDataIntoExcel(13, 26, "" + double贈送累計14, objectSheet2);
    exeFun.putDataIntoExcel(14, 26, "" + double佣金累計15, objectSheet2);
    exeFun.putDataIntoExcel(15, 26, "" + double中原佣金累計, objectSheet2); // 2015-11-18 B3018
    exeFun.putDataIntoExcel(16, 26, "" + double利息累計, objectSheet2); // 2017-05-02 B4474
    exeFun.putDataIntoExcel(17, 26, "" + double淨售累計16, objectSheet2);
    exeFun.putDataIntoExcel(18, 26, "" + double底價累計17, objectSheet2);
    exeFun.putDataIntoExcel(19, 26, "" + double超低價累計18, objectSheet2);
    // '** 累計成交單價
    // retSumDealMoneyForASale START
    // 0 H_COM 1 L_COM
    // 2 SumDealMoney 3 SumDealMoney2 4 SumDealMoney3 5 SumPingSu
    String[][] retSumDealMoneyForASale = getSumDealMoneyForASale(stringHouseCar, stringProjectID, stringOrderDateEnd, getSqlAnd2(), stringThisType);
    double double累計成交單價 = 0; // Cells(25, 31)
    double double坪數 = 0;
    for (int intNo = 0; intNo < retSumDealMoneyForASale.length; intNo++) {
      // SumPingSu
      double坪數 += doParseDouble(retSumDealMoneyForASale[intNo][5].trim(), "42");
      stringHComLocal = retSumDealMoneyForASale[intNo][0].trim();
      stringLComLocal = retSumDealMoneyForASale[intNo][1].trim();
      booleanHouse = false;
      booleanLand = false;
      if ("".equals(stringHouseCar)) {
        // 房土
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      if (booleanHouse)
        double累計成交單價 += doParseDouble(retSumDealMoneyForASale[intNo][3], "43"); // SumDealMoney2
      if (booleanLand)
        double累計成交單價 += doParseDouble(retSumDealMoneyForASale[intNo][4], "44"); // SumDealMoney3
    }
    if (double坪數 == 0) {
      exeFun.putDataIntoExcel(31, 26, "0", objectSheet2);// 改改
    } else {
      if ("Z".equals(stringProjectID)) { // ** M^2 Convert 坪
        double累計成交單價 = double累計成交單價 / (double坪數 * 0.3025);
      } else {
        double累計成交單價 = double累計成交單價 / double坪數;
      }
      exeFun.putDataIntoExcel(36, 26, "" + double累計成交單價, objectSheet2);// 改改
    }
    // retSumDealMoneyForASale END
    //
    int intStartDataRow = exeFun.getStartDataRow();
    int intPageDataRow = exeFun.getPageDataRow();
    int intRecordNo = intStartDataRow;
    for (int intRetASale = 0; intRetASale < retASale.length; intRetASale++) {
      // No
      exeFun.putDataIntoExcel(0, intRecordNo, "" + (intRetASale + 1), objectSheet2);
      // 棟樓別 or 車位別
      if ("房子".equals(stringThisType)) {
        stringTmp = retASale[intRetASale][2]; // Position
      } else if ("車位".equals(stringThisType)) {
        stringTmp = retASale[intRetASale][3]; // Car
      }
      exeFun.putDataIntoExcel(1, intRecordNo, stringTmp, objectSheet2);
      // Custom客戶姓名
      exeFun.putDataIntoExcel(2, intRecordNo, retASale[intRetASale][4].trim(), objectSheet2);
      // OrderDate 付訂日期
      exeFun.putDataIntoExcel(3, intRecordNo, retASale[intRetASale][5].trim(), objectSheet2);
      // EnougDate 補足日期
      exeFun.putDataIntoExcel(5, intRecordNo, retASale[intRetASale][9].trim(), objectSheet2);
      // ContrDate簽約日期
      exeFun.putDataIntoExcel(7, intRecordNo, retASale[intRetASale][13].trim(), objectSheet2);
      // Deldate
      exeFun.putDataIntoExcel(9, intRecordNo, retASale[intRetASale][17].trim(), objectSheet2);
      //
      stringHComLocal = retASale[intRetASale][0].trim();
      stringLComLocal = retASale[intRetASale][1].trim();
      booleanHouse = false;
      booleanLand = false;
      if ("".equals(stringHouseCar)) {
        // 房土
        if (!"".equals(stringDistributeNo)) {
          if (stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
          if (stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
        } else {
          booleanHouse = true; // 房子累加
          booleanLand = true; // 土地累加
        }
      } else {
        // 房
        if ("H_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringHComLocal.equals(stringDistributeNo))
            booleanHouse = true; // 房子累加
        // 土
        if ("L_".equals(stringHouseCar))
          if ("".equals(stringDistributeNo) || stringLComLocal.equals(stringDistributeNo))
            booleanLand = true; // 土地累加
      }
      //
      double double付訂金額5 = 0;
      double double補足金額7 = 0;
      double double簽約金額9 = 0;
      double double坪數11 = 0;
      double double訂價12 = 0;
      double double售價13 = 0;
      double double贈送14 = 0;
      double double佣金15 = 0;
      double double淨售16 = 0;
      double double底價17 = 0;
      double double超低價18 = 0;
      double double中原佣金 = 0; // 2015-11-18 B3018
      double double利息 = 0; // 2017-05-02 B4474
      // PingSu
      if ("Z".equals(stringProjectID)) { // '** M^2 Convert 坪
        double坪數11 = doParseDouble(retASale[intRetASale][18], "49") * 0.3025;// PingSu
      } else {
        double坪數11 = doParseDouble(retASale[intRetASale][18], "50");
      }
      exeFun.putDataIntoExcel(10, intRecordNo, "" + double坪數11, objectSheet2);
      //
      if (booleanHouse) { // H_Com
        double付訂金額5 += doParseDouble(retASale[intRetASale][7], "51"); // OrderMon2
        double補足金額7 += doParseDouble(retASale[intRetASale][11], "52");// EnougMon2
        double簽約金額9 += doParseDouble(retASale[intRetASale][15], "53"); // ContrMon2
        double訂價12 += doParseDouble(retASale[intRetASale][20], "54");// PreMoney2
        double售價13 += doParseDouble(retASale[intRetASale][23], "55");// DealMoney2
        double贈送14 += doParseDouble(retASale[intRetASale][26], "56");// GiftMoney2
        double佣金15 += doParseDouble(retASale[intRetASale][29], "57");// CommMoney2
        double淨售16 += doParseDouble(retASale[intRetASale][32], "58");// PureMoney2
        double底價17 += doParseDouble(retASale[intRetASale][35], "59");// LastMoney2
        double超低價18 += doParseDouble(retASale[intRetASale][38], "60");// BalaMoney2
        double中原佣金 += doParseDouble(retASale[intRetASale][60], "71");// CommMoney12 // 2015-11-18 B3018
        double利息 += doParseDouble(retASale[intRetASale][63], "71");// CommMoney12 // 2017-05-02 B4474
      }
      if (booleanLand) { // L_Com
        double付訂金額5 += doParseDouble(retASale[intRetASale][8], "61"); // OrderMon3
        double補足金額7 += doParseDouble(retASale[intRetASale][12], "62");// EnougMon3
        double簽約金額9 += doParseDouble(retASale[intRetASale][16], "63");// ContrMon3
        double訂價12 += doParseDouble(retASale[intRetASale][21], "64");// PreMoney3
        double售價13 += doParseDouble(retASale[intRetASale][24], "65");// DealMoney3
        double贈送14 += doParseDouble(retASale[intRetASale][27], "66");// GiftMoney3
        double佣金15 += doParseDouble(retASale[intRetASale][30], "67");// CommMoney3
        double淨售16 += doParseDouble(retASale[intRetASale][33], "68");// PureMoney3
        double底價17 += doParseDouble(retASale[intRetASale][36], "69");// LastMoney3
        double超低價18 += doParseDouble(retASale[intRetASale][39], "70");// BalaMoney3
        double中原佣金 += doParseDouble(retASale[intRetASale][61], "72");// CommMoney13 // 2015-11-18 B3018
        double利息 += doParseDouble(retASale[intRetASale][64], "72");// CommMoney13 // 2017-05-02 B4474
      }
      exeFun.putDataIntoExcel(4, intRecordNo, "" + double付訂金額5, objectSheet2);
      exeFun.putDataIntoExcel(6, intRecordNo, "" + double補足金額7, objectSheet2);
      exeFun.putDataIntoExcel(8, intRecordNo, "" + double簽約金額9, objectSheet2);
      exeFun.putDataIntoExcel(11, intRecordNo, "" + double訂價12, objectSheet2);
      exeFun.putDataIntoExcel(12, intRecordNo, "" + double售價13, objectSheet2);
      exeFun.putDataIntoExcel(13, intRecordNo, "" + double贈送14, objectSheet2);
      exeFun.putDataIntoExcel(14, intRecordNo, "" + double佣金15, objectSheet2);
      exeFun.putDataIntoExcel(15, intRecordNo, "" + double中原佣金, objectSheet2); // 2015-11-18 B3018
      exeFun.putDataIntoExcel(16, intRecordNo, "" + double利息, objectSheet2); // 2017-05-02 B4474
      exeFun.putDataIntoExcel(17, intRecordNo, "" + double淨售16, objectSheet2);
      exeFun.putDataIntoExcel(18, intRecordNo, "" + double底價17, objectSheet2);
      exeFun.putDataIntoExcel(19, intRecordNo, "" + double超低價18, objectSheet2);
      //
      exeFun.putDataIntoExcel(20, intRecordNo, retASale[intRetASale][40].trim() + (intRetASale + 1), objectSheet2);// SaleName1
      exeFun.putDataIntoExcel(21, intRecordNo, retASale[intRetASale][41].trim(), objectSheet2);// SaleName2
      exeFun.putDataIntoExcel(22, intRecordNo, retASale[intRetASale][42].trim(), objectSheet2);// SaleName3
      exeFun.putDataIntoExcel(23, intRecordNo, retASale[intRetASale][43].trim(), objectSheet2);// SaleName4
      exeFun.putDataIntoExcel(24, intRecordNo, retASale[intRetASale][44].trim(), objectSheet2);// SaleName5
      exeFun.putDataIntoExcel(25, intRecordNo, retASale[intRetASale][54].trim(), objectSheet2);// SaleName6
      exeFun.putDataIntoExcel(26, intRecordNo, retASale[intRetASale][55].trim(), objectSheet2);// SaleName7
      exeFun.putDataIntoExcel(27, intRecordNo, retASale[intRetASale][56].trim(), objectSheet2);// SaleName8

      exeFun.putDataIntoExcel(28, intRecordNo, retASale[intRetASale][57].trim(), objectSheet2);// SaleName9
      exeFun.putDataIntoExcel(29, intRecordNo, retASale[intRetASale][58].trim(), objectSheet2);// SaleName10
      exeFun.putDataIntoExcel(30, intRecordNo, retASale[intRetASale][45].trim(), objectSheet2);// MediaName
      exeFun.putDataIntoExcel(31, intRecordNo, retASale[intRetASale][46].trim(), objectSheet2);// ZoneName
      exeFun.putDataIntoExcel(32, intRecordNo, retASale[intRetASale][47].trim(), objectSheet2);// MajorName
      exeFun.putDataIntoExcel(33, intRecordNo, retASale[intRetASale][48].trim(), objectSheet2);// UseType
      exeFun.putDataIntoExcel(34, intRecordNo, retASale[intRetASale][49].trim(), objectSheet2);// Remark
      exeFun.putDataIntoExcel(35, intRecordNo, retASale[intRetASale][50].trim(), objectSheet2);// DateRange
      exeFun.putDataIntoExcel(36, intRecordNo, retASale[intRetASale][51].trim(), objectSheet2);// DateCheck
      exeFun.putDataIntoExcel(37, intRecordNo, retASale[intRetASale][52].trim(), objectSheet2);// DateFile
      exeFun.putDataIntoExcel(38, intRecordNo, retASale[intRetASale][53].trim(), objectSheet2);// DateBonus

      // 小計
      if (!"".equals(retASale[intRetASale][18].trim())) { // PingSu
        double坪數小計11Global += double坪數11;
      }
      // PreMoney1
      if (!"".equals(retASale[intRetASale][19].trim())) {
        double訂價小計Global += double訂價12;
      }
      // DealMoney1
      if (!"".equals(retASale[intRetASale][22].trim())) {
        double售價小計Global += double售價13;
      }
      // GiftMoney1
      if (!"".equals(retASale[intRetASale][25].trim())) {
        double贈送小計Global += double贈送14;
      }
      // CommMoney1
      if (!"".equals(retASale[intRetASale][28].trim())) {
        double佣金小計Global += double佣金15;
      }
      if (!"".equals(retASale[intRetASale][29].trim())) {
        double中原佣金小計Global += double中原佣金; // 2015-11-18 B3018
      }
      if (!"".equals(retASale[intRetASale][64].trim())) {
        double利息小計Global += double利息; // 2017-05-02 B4474
      }
      // PureMoney1
      if (!"".equals(retASale[intRetASale][31].trim())) {
        double淨售小計Global += double淨售16;
      }
      // LastMoney1
      if (!"".equals(retASale[intRetASale][34].trim())) {
        double底價小計Global += double底價17;
      }
      // BalaMoney1
      if (!"".equals(retASale[intRetASale][37].trim())) {
        double超低價小計Global += double超低價18;
      }
      intRecordNo++;
      // 滿頁時必須將Sheet2 Copy Sheet1
      if (intRecordNo >= (intPageDataRow + intStartDataRow)) {
        // 本月成交單價
        if (double坪數小計11Global == 0) {
          double本月成交單價 = 0;
        } else {
          double本月成交單價 = double售價小計Global / double坪數小計11Global;
        }
        exeFun.putDataIntoExcel(35, 25, "" + double本月成交單價, objectSheet2);
        exeFun.putDataIntoExcel(10, 25, "" + double坪數小計11Global, objectSheet2);
        exeFun.putDataIntoExcel(11, 25, "" + double訂價小計Global, objectSheet2);
        exeFun.putDataIntoExcel(12, 25, "" + double售價小計Global, objectSheet2);
        exeFun.putDataIntoExcel(13, 25, "" + double贈送小計Global, objectSheet2);
        exeFun.putDataIntoExcel(14, 25, "" + double佣金小計Global, objectSheet2);
        exeFun.putDataIntoExcel(15, 25, "" + double中原佣金小計Global, objectSheet2); // 2015-11-18 B3018
        exeFun.putDataIntoExcel(16, 25, "" + double利息小計Global, objectSheet2); // 2017-05-02 B4474
        exeFun.putDataIntoExcel(17, 25, "" + double淨售小計Global, objectSheet2);
        exeFun.putDataIntoExcel(18, 25, "" + double底價小計Global, objectSheet2);
        exeFun.putDataIntoExcel(19, 25, "" + double超低價小計Global, objectSheet2);
      }
      intRecordNo = exeFun.doChangePage(intRecordNo, objectSheet1, objectSheet2);
    } // For LOOP END
      // 複製未滿頁
    if (intRecordNo != intStartDataRow) {
      // 本月成交單價
      if (double坪數小計11Global == 0) {
        double本月成交單價 = 0;
      } else {
        double本月成交單價 = double售價小計Global / double坪數小計11Global;
      }
      exeFun.putDataIntoExcel(35, 25, "" + double本月成交單價, objectSheet2);
      exeFun.putDataIntoExcel(10, 25, "" + double坪數小計11Global, objectSheet2);
      exeFun.putDataIntoExcel(11, 25, "" + double訂價小計Global, objectSheet2);
      exeFun.putDataIntoExcel(12, 25, "" + double售價小計Global, objectSheet2);
      exeFun.putDataIntoExcel(13, 25, "" + double贈送小計Global, objectSheet2);
      exeFun.putDataIntoExcel(14, 25, "" + double佣金小計Global, objectSheet2);
      exeFun.putDataIntoExcel(15, 25, "" + double中原佣金小計Global, objectSheet2); // 2015-11-18 B3018
      exeFun.putDataIntoExcel(16, 25, "" + double利息小計Global, objectSheet2); // 2017-05-02 B4474
      exeFun.putDataIntoExcel(17, 25, "" + double淨售小計Global, objectSheet2);
      exeFun.putDataIntoExcel(18, 25, "" + double底價小計Global, objectSheet2);
      exeFun.putDataIntoExcel(19, 25, "" + double超低價小計Global, objectSheet2);

      exeFun.CopyPage(objectSheet1, objectSheet2);
      exeFun.doClearContents("A" + (intStartDataRow + 1) + ":AG" + (intStartDataRow + intPageDataRow), objectSheet2);
      //
      exeFun.doAdd1PageNo();
    }
    // 釋放 Excel 物件
    exeFun.getReleaseExcelObject(retVector);
    return true;
  }

  // 應用
  public String getSqlAnd() throws Exception {
    String stringSqlAnd = "";
    // 付訂日期
    String stringOrderDateEnd = getValue("OrderDateEnd").trim();
    if (!"".equals(stringOrderDateEnd))
      stringSqlAnd += " AND  OrderDate  <=  '" + stringOrderDateEnd + "'";
    // 補足日期
    String stringEnougDateStart = getValue("EnougDateStart").trim();
    if (!"".equals(stringEnougDateStart))
      stringSqlAnd += " AND  EnougDate  >=  '" + stringEnougDateStart + "'";
    String stringEnougDateEnd = getValue("EnougDateEnd").trim();
    if (!"".equals(stringEnougDateEnd))
      stringSqlAnd += " AND  EnougDate  <=  '" + stringEnougDateEnd + "' ";
    // 簽約日期
    String stringContrDateStart = getValue("ContrDateStart").trim();
    if (!"".equals(stringContrDateStart))
      stringSqlAnd += " AND  ContrDate  >=  '" + stringContrDateStart + "' ";
    String stringContrDateEnd = getValue("ContrDateEnd").trim();
    if (!"".equals(stringContrDateEnd))
      stringSqlAnd += " AND  ContrDate  <= '" + stringContrDateEnd + "' ";
    // 合約會審
    String stringDateCheckStart = getValue("DateCheckStart").trim();
    if (!"".equals(stringDateCheckStart))
      stringSqlAnd += " AND  DateCheck  >=  '" + stringDateCheckStart + "' ";
    String stringDateCheckEnd = getValue("DateCheckEnd").trim();
    if (!"".equals(stringDateCheckEnd))
      stringSqlAnd += " AND  DateCheck  <=  '" + stringDateCheckEnd + "'";
    // 簽約金到期
    String stringDateRangeStart = getValue("DateRangeStart").trim();
    if (!"".equals(stringDateRangeStart))
      stringSqlAnd += " AND  DateRange  >=  '" + stringDateRangeStart + "' ";
    String stringDateRangeEnd = getValue("DateRangeEnd").trim();
    if (!"".equals(stringDateRangeEnd))
      stringSqlAnd += " AND  DateRange  <=  '" + stringDateRangeEnd + "' ";
    return stringSqlAnd;
  }

  public String getSqlAnd2() throws Exception {
    String stringSqlAnd = "";
    // 區別
    String stringDistributeNo = getValue("Distribute").trim();
    String stringHouseLand = getValue("HouseLand").trim();
    String stringFunction = getValue("Function").trim();
    if (!"".equals(stringDistributeNo)) {
      switch (Integer.parseInt(stringHouseLand)) {
      case 0: // 全部
        stringSqlAnd = " AND  (H_Com  =  '" + stringDistributeNo + "'   OR  " + " L_Com  =  '" + stringDistributeNo + "') ";
        break;
      case 1: // 房屋
        stringSqlAnd = " AND  H_Com  = '" + stringDistributeNo + "' ";
        break;
      case 2: // 土地
        stringSqlAnd = " AND L_Com  = '" + stringDistributeNo + "' ";
        break;
      }
      switch (Integer.parseInt(stringFunction)) {
      case 0:
        // stringSqlAnd += " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " ;
        break;
      case 1:
        // stringSqlAnd += " AND SUBSTRING(PositionRent,1,1) NOT IN ('+','-') " ;
        break;
      case 2:
        // stringSqlAnd += " AND (SUBSTRING(Position,1,1) NOT IN ('+','-') OR" +
        // " SUBSTRING(PositionRent,1,1) NOT IN ('+','-')) " ;
        break;
      case 3:
        // stringSqlAnd += " AND SUBSTRING(Car,1,1) NOT IN ('+','-') " ;
        break;
      case 4:
        // stringSqlAnd += " AND SUBSTRING(CarRent,1,1) NOT IN ('+','-') " ;
        break;
      case 5:
        // stringSqlAnd += " AND (SUBSTRING(Car,1,1) NOT IN ('+','-') OR" +
        // " SUBSTRING(CarRent,1,1) NOT IN ('+','-')) " ;
        break;
      }
    }
    // 地主
    String stringLandOwner = getValue("LandOwner").trim();
    switch (Integer.parseInt(stringLandOwner)) {
    case 0:
      break;
    case 1:
      stringSqlAnd += " AND  LandOwner  =  'N' ";
      break;
    case 2:
      stringSqlAnd += " AND  LandOwner  =  'Y' ";
      break;
    }
    return stringSqlAnd;
  }

  public String getSqlAnd3() throws Exception {
    String stringSqlAnd = "";
    // 區別
    String stringDistributeNo = getValue("Distribute").trim();
    String stringHouseLand = getValue("HouseLand").trim();
    String stringFunction = getValue("Function").trim();
    if (!"".equals(stringDistributeNo)) {
      switch (Integer.parseInt(stringFunction)) {
      case 0:
        // stringSqlAnd += " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " ;
        break;
      case 1:
        // stringSqlAnd += " AND SUBSTRING(PositionRent,1,1) NOT IN ('+','-') " ;
        break;
      case 2:
        // stringSqlAnd += " AND (SUBSTRING(Position,1,1) NOT IN ('+','-') OR" +
        // " SUBSTRING(PositionRent,1,1) NOT IN ('+','-')) " ;
        break;
      case 3:
        // stringSqlAnd += " AND SUBSTRING(Car,1,1) NOT IN ('+','-') " ;
        break;
      case 4:
        // stringSqlAnd += " AND SUBSTRING(CarRent,1,1) NOT IN ('+','-') " ;
        break;
      case 5:
        // stringSqlAnd += " AND (SUBSTRING(Car,1,1) NOT IN ('+','-') OR" +
        // " SUBSTRING(CarRent,1,1) NOT IN ('+','-')) " ;
        break;
      }
    }
    // 地主
    String stringLandOwner = getValue("LandOwner").trim();
    switch (Integer.parseInt(stringLandOwner)) {
    case 0:
      break;
    case 1:
      stringSqlAnd += " AND  LandOwner  =  'N' ";
      break;
    case 2:
      stringSqlAnd += " AND  LandOwner  =  'Y' ";
      break;
    }
    return stringSqlAnd;
  }

  public String[] getSqlAnd4() throws Exception {
    String stringSqlAnd = "";
    // 區別
    String stringDistributeNo = getValue("Distribute").trim();
    String stringHouseLand = getValue("HouseLand").trim();
    Vector vectorSql = new Vector();
    if (!"".equals(stringDistributeNo)) {
      switch (Integer.parseInt(stringHouseLand)) {
      case 0: // 全部
        stringSqlAnd = " AND  H_Com  = '" + stringDistributeNo + "' ";
        vectorSql.add(stringSqlAnd);
        stringSqlAnd = " AND L_Com  = '" + stringDistributeNo + "' ";
        vectorSql.add(stringSqlAnd);
        break;
      case 1: // 房屋
        stringSqlAnd = " AND  H_Com  = '" + stringDistributeNo + "' ";
        vectorSql.add(stringSqlAnd);
        break;
      case 2: // 土地
        stringSqlAnd = " AND L_Com  = '" + stringDistributeNo + "' ";
        vectorSql.add(stringSqlAnd);
        break;
      }
    } else {
      vectorSql.add("");
    }
    return (String[]) vectorSql.toArray(new String[0]);
  }

  public String getConditionDescription() throws Exception {
    String stringConditionDescription = "";
    // 區別
    String stringDistribute = getValue("Distribute").trim();
    if (stringDistribute.length() > 0) {
      stringConditionDescription += "區別:" + stringDistribute + ";";
    } else {
      stringConditionDescription += "區別:無" + ";";
    }
    // 房屋、土地
    String stringHouseLand = getValue("HouseLand").trim();
    switch (Integer.parseInt(stringHouseLand)) {
    case 0:
      stringConditionDescription += "房土:全部;";
      break;
    case 1:
      stringConditionDescription += "房土:房屋;";
      break;
    case 2:
      stringConditionDescription += "房土:土地;";
      break;
    }
    // 地主
    String stringLandOwner = getValue("LandOwner").trim();
    switch (Integer.parseInt(stringLandOwner)) {
    case 0:
      stringConditionDescription += "地主:全部;";
      break;
    case 1:
      stringConditionDescription += "地主:不含地主;";
      break;
    case 2:
      stringConditionDescription += "地主:地主;";
      break;
    }
    // 補足日期
    String stringEnougDateStart = convert.replace(getValue("EnougDateStart").trim(), "/", "");
    String stringEnougDateEnd = convert.replace(getValue("EnougDateEnd").trim(), "/", "");
    if (!"".equals(stringEnougDateStart) && !"".equals(stringEnougDateEnd)) {
      stringConditionDescription += "補足:" + stringEnougDateStart + "～" + stringEnougDateEnd + ";";
    }
    // 簽約日期
    String stringContrDateStart = convert.replace(getValue("ContrDateStart").trim(), "/", "");
    String stringContrDateEnd = convert.replace(getValue("ContrDateEnd").trim(), "/", "");
    if (!"".equals(stringContrDateStart) && !"".equals(stringContrDateEnd)) {
      stringConditionDescription += "簽約:" + stringContrDateStart + "～" + stringContrDateEnd + ";";
    }
    return stringConditionDescription;
  }

  public String getDateAC(String value, String stringErrorFieldName) throws Throwable {
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
      return "[" + stringErrorFieldName + "] 日期格式錯誤(YYYY/MM/DD)。";
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
      return "[" + stringErrorFieldName + "] 日期格式錯誤(YYYY/MM/DD)。";
    }
    return retDate;
  }

  public String getConditionDescription2() throws Exception {
    String stringConditionDescription = "";
    // 合約會審*
    String stringDateCheckStart = convert.replace(getValue("DateCheckStart").trim(), "/", "");
    String stringDateCheckEnd = convert.replace(getValue("DateCheckEnd").trim(), "/", "");
    if (!"".equals(stringDateCheckStart) && !"".equals(stringDateCheckEnd)) {
      stringConditionDescription += "合約會審:" + stringDateCheckStart + "～" + stringDateCheckEnd + ";";
    }
    // 簽約金到期*
    String stringDateRangeStart = convert.replace(getValue("DateRangeStart").trim(), "/", "");
    String stringDateRangeEnd = convert.replace(getValue("DateRangeEnd").trim(), "/", "");
    if (!"".equals(stringDateRangeStart) && !"".equals(stringDateRangeEnd)) {
      stringConditionDescription += "簽約金到期:" + stringDateRangeStart + "～" + stringDateRangeEnd + ";";
    }
    return stringConditionDescription;
  }

  // 日期格式轉換
  public String getConertFormatDate(String stringDate) {
    String stringTmp = convert.ac2roc(convert.replace(stringDate, "/", ""));
    String retDate = convert.FormatedDate(stringTmp, "/");
    return retDate;
  }

  public double doParseDouble(String stringNum, String stringPosition) throws Exception {
    //
    double doubleNum = 0;
    if ("".equals(stringNum) || "null".equals(stringNum)) {
      System.out.println("[" + stringNum + "]-----------" + stringPosition + "無法剖析[" + stringNum + "]，回傳 0。");
      return 0;
    }
    try {
      doubleNum = Double.parseDouble(stringNum);
    } catch (Exception e) {
      System.out.println(stringPosition + "無法剖析[" + stringNum + "]，回傳 0。");
      return 0;
    }
    return doubleNum;
  }

  // 資料庫
  public String getACom(String stringDistributeNo) throws Exception {
    String stringSql = "";
    String stringACom = "";
    String[][] retACom = null;
    //
    stringSql = "SELECT  Com_Name  FROM  A_Com  WHERE  Com_No  ='" + stringDistributeNo + "'";
    retACom = dbSale.queryFromPool(stringSql);
    if (retACom.length != 0)
      stringACom = retACom[0][0].trim();
    return stringACom;
  }

  // 檢核
  public String[][] getAProject(String stringProjectID) throws Exception {
    String stringSql = "";
    String[][] retAProject = null;
    //
    stringSql = "SELECT  PROJECTID,  PROJECTNAME " + " FROM  A_PROJECT " + " WHERE  PROJECTID  =  '" + stringProjectID + "' ";
    retAProject = dbSale.queryFromPool(stringSql);
    return retAProject;
  }

  public String[][] getDeleteForAProject(String stringProjectID, String stringHouseCar) throws Exception {
    String stringSql = "";
    String[][] retAProject = null;
    //
    stringSql = "SELECT  H_Com,                                              L_Com,                      OpenDate,  " + stringHouseCar
        + "PositionMoneyS,  H_PositionMoneyS,  L_PositionMoneyS,  " + stringHouseCar + "CarMoneyS,          H_CarMoneyS,          L_CarMoneyS " + " FROM  A_Project "
        + " WHERE  ProjectID  =  '" + stringProjectID + "'";
    retAProject = dbSale.queryFromPool(stringSql);
    return retAProject;
  }

  public String[][] getSaleForAProject(String stringProjectID, String stringHouseCar) throws Exception {
    String stringSql = "";
    String[][] retAProject = null;
    //
    stringSql = "SELECT      H_Com,  L_Com, " + stringHouseCar + "PositionMoneyS,  H_PositionMoneyS,   L_PositionMoneyS,  " + stringHouseCar
        + "CarMoneyS,          H_CarMoneyS,           L_CarMoneyS, " + " OpenDate " + " FROM  A_Project " + " WHERE  ProjectID  =  '" + stringProjectID + "'";
    retAProject = dbSale.queryFromPool(stringSql);
    return retAProject;
  }

  public String[][] getRentForAProject(String stringProjectID, String stringHouseCar) throws Exception {
    String stringSql = "";
    String[][] retAProject = null;
    // 0 H_Com 1 L_Com 2 OpenDate 3 PositionMoneyR1 4 CarMoneyR1
    stringSql = "SELECT  H_Com,                                                   L_Com,                                            OpenDate,  " + stringHouseCar
        + "PositionMoneyR,  " + stringHouseCar + "CarMoneyR " + " FROM  A_Project " + " WHERE  ProjectID  =  '" + stringProjectID + "'";
    retAProject = dbSale.queryFromPool(stringSql);
    return retAProject;
  }

  public String[][] getSaleForAProject(String stringProjectID) throws Exception {
    String stringSql = "";
    String[][] retAProject = null;
    //
    stringSql = "SELECT  H_Com,   L_Com,  OpenDate  " + " FROM  A_Project " + " WHERE  ProjectID  =  '" + stringProjectID + "'";
    retAProject = dbSale.queryFromPool(stringSql);
    return retAProject;
  }

  public String[][] getTotRecForASale(String stringProjectID, String stringSqlAnd, String stringSqlAnd2, String stringThisType) throws Exception {
    String stringSql = "";
    String[][] retASale = null;
    //
    stringSql = "SELECT  COUNT(*)  " + " FROM  A_Sale " + " WHERE  ProjectID1  =  '" + stringProjectID + "' " + stringSqlAnd2 + stringSqlAnd + " AND  Dealmoney > 0 ";
    // " AND Not IsDate(DelDate) "
    if ("房子".equals(stringThisType)) {
      stringSql += " AND LEN(Position) > 0 ";
      // " AND SUBSTRING(Position,1,1) NOT IN ('+','-') ";
    } else if ("車位".equals(stringThisType)) {
      stringSql += " AND LEN(Car) > 0 ";
      // " AND SUBSTRING(Car,1,1) NOT IN ('+','-') ";
    }
    retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getSumDealMoneyForASale(String stringHouseCar, String stringProjectID, String stringOrderDateEnd, String stringSqlAnd2, String stringThisType)
      throws Exception {
    String stringSql = "";
    String[][] retASale = null;
    // 0 H_COM 1 L_COM
    // 2 SumDealMoney 3 SumDealMoney2 4 SumDealMoney3 5 SumPingSu
    stringSql = "SELECT  H_COM,                                                     L_COM,  " + " SUM(" + stringHouseCar + "DealMoney),   SUM(H_DealMoney),  SUM(L_DealMoney), "
        + " SUM(PingSu) " + " FROM  A_SALE " + " WHERE  ProjectID1 = '" + stringProjectID + "' " + " AND  OrderDate  <=  '" + stringOrderDateEnd + "'  " + stringSqlAnd2;
    // " AND Not IsDate(DelDate) " +
    if ("房子".equals(stringThisType)) {
      stringSql += " AND LEN(Position)  >  0 ";
    } else if ("車位".equals(stringThisType)) {
      stringSql += " AND LEN(Car)  >  0 ";
    }
    stringSql += " GROUP BY  H_COM,  L_COM ";
    retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getRentForASale(String stringHouseCar, String stringProjectID, String stringOrderDateStart, String stringOrderDateEnd, String stringThisType) throws Exception {
    String stringSql = "";
    String stringSql前端畫面 = getSqlAnd2() + getSqlAnd();
    // String[] arraySql = getSqlAnd4() ;
    String[][] retASale = null;
    /*
     * 0.PositionRent 1.CarRent 2.Custom 3.OrderDate 4.OrderMon1 5.EnougDate
     * 6.EnougMon1 7.ContrDate 8.ContrMon1 9.Deldate 10.PingSu 11.RentRange
     * 12.RentLast 13.PingRent 14.Rent 15.RentFree 16.Guranteer 17.PreMoney1
     * 18.DealMoney1 19.GiftMoney1 20.CommMoney1 21.PureMoney1 22.LastMoney1
     * 23.BalaMoney1 24.SaleName1 25.SaleName2 26.SaleName3 27.SaleName4
     * 28.SaleName5 29.MediaName 30.ZoneName 31.MajorName 32.UseType 33.Remark
     * 34.DateRange 35.DateCheck 36.DateFile 37.DateBonus 38.SaleName6 39.SaleName7
     * 40.SaleName8 41.SaleName9 42.SaleName10 43 H_Com 44.L_Com 45.DealMoney
     * 46.H_DealMoney 47.L_DealMoney 48 CommMoney1 49 H_LastMoney 50 L_LastMoney 51
     * H_GiftMoney 52 L_GiftMoney 53 H_CommMoney 54 L_CommMoney 55 H_CommMoney1 56
     * L_CommMoney1 57 H_BalaMoney 58 L_BalaMoney 59 H_ViMoney 60 L_ViMoney
     */
    stringSql = "SELECT  PositionRent,                                 CarRent,                                         Custom,                                          OrderDate,  "
        + stringHouseCar + "OrderMon,          EnougDate,  " + stringHouseCar + "EnougMon,      ContrDate," + stringHouseCar
        + "ContrMon,           Deldate,                                          PingSu,                                           RentRange,  "
        + "RentLast,                                       PingRent,                                        Rent,                                               RentFree,  "
        + "Guranteer,  " + stringHouseCar + "PreMoney,  " + stringHouseCar + "DealMoney,  " + stringHouseCar + "GiftMoney,  " + stringHouseCar + "CommMoney,  " + stringHouseCar
        + "PureMoney,  " + stringHouseCar + "LastMoney,  " + stringHouseCar + "BalaMoney,  "
        + "SaleName1,                                    SaleName2,                                     SaleName3,                                    SaleName4,  "
        + "SaleName5,                                    MediaName,                                    ZoneName,                                    MajorName, "
        + "UseType,                                        Remark,                                            DateRange,                                    DateCheck,  "
        + "DateFile,                                        DateBonus,                                      SaleName6,                                     SaleName7, "
        + "SaleName8,                                   SaleName9,                                      SaleName10,                                   H_Com, "
        + " L_Com,                                          DealMoney,                                      H_DealMoney,                                L_DealMoney,"
        + stringHouseCar + "CommMoney1,    H_LastMoney,                                  L_LastMoney,                                 H_GiftMoney, \n"
        + " L_GiftMoney,                                H_CommMoney,                             L_CommMoney,                              H_CommMoney1,   \n"
        + " L_CommMoney1,                         H_BalaMoney,                                 L_BalaMoney,                         H_ViMoney,                                 L_ViMoney "
        + " FROM  A_SALE \n" + " WHERE  ProjectID1  =  '" + stringProjectID + "' \n" + " AND  OrderDate  Between  '" + stringOrderDateStart + "'   AND  '" + stringOrderDateEnd
        + "' \n" + stringSql前端畫面;
    // " AND Not IsDate(DelDate) " ;
    if ("房子".equals(stringThisType)) {
      stringSql += " AND  LEN(PositionRent)  >  0 \n" + " ORDER BY  OrderDate,  Custom \n";
    } else if ("車位".equals(stringThisType)) {
      stringSql += " AND  LEN(CarRent)  >  0 \n" + " ORDER BY  OrderDate,  Custom \n";
    }
    retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getSaleForASale(String stringHouseCar, String stringProjectID, String stringOrderDateStart, String stringOrderDateEnd, String stringThisType,
      String stringOrderBy, String stringSqlAnd, String stringSqlAnd2) throws Exception {
    String stringSql = "";
    /*
     * OrderMon2 0 _ H_Com 1 _ L_Com 2 _ Position 3 _ Car 4 _ Custom 5 _ OrderDate 6
     * _ OrderMon1 7 _ OrderMon2 8 _ OrderMon3 9 _EnougDate 10 _ EnougMon1 11 _
     * EnougMon2 12 _ EnougMon3 13 _ ContrDate 14 _ ContrMon1 15 _ ContrMon2 16 _
     * ContrMon3 16 _ Deldate 18 _ PingSu 19 _ PreMoney1 20 _ PreMoney2 21 _
     * PreMoney3 22 _ DealMoney1 23 _ DealMoney2 24 _ DealMoney3 25 _ GiftMoney1 26
     * _ GiftMoney2 27 _ GiftMoney3 28 _ CommMoney1 29 _ CommMoney2 30 _ CommMoney3
     * 31 _ PureMoney1 32 _ PureMoney2 33 _ PureMoney3 34 _ LastMoney1 35 _
     * LastMoney2 36 _ LastMoney3 37 _ BalaMoney1 38 _ BalaMoney2 39 _ BalaMoney3 40
     * _ SaleName1 41 _ SaleName2 42 _ SaleName3 43 _ SaleName4 44 _ SaleName5 45 _
     * MediaName 46 _ ZoneName 47 _ MajorName 48 _ UseType 49 _ Remark 50 _
     * DateRange 51 _ DateCheck 52 _ DateFile 53 _ DateBonus 54 _ SaleName6 55 _
     * SaleName7 56 _ SaleName8 57 _ SaleName9 58 _ SaleName10 59 _ CommMoney1 60 _
     * CommMoney2 61 _ CommMoney1 62_ViMoney 63 64
     */
    stringSql = "SELECT  H_Com,                                        L_Com,                                        Position,                                      Car, "
        + " Custom,                                      OrderDate,  " + stringHouseCar + "OrderMon,    H_OrderMon, " + " L_OrderMon,                              EnougDate,  "
        + stringHouseCar + "EnougMon,    H_EnougMon, " + " L_EnougMon,                             ContrDate,  " + stringHouseCar + "ContrMon,     H_ContrMon, "
        + " L_ContrMon,                              Deldate,                                       PingSu,  " + stringHouseCar + "PreMoney, "
        + " H_PreMoney,                              L_PreMoney,  " + stringHouseCar + "DealMoney,   H_DealMoney, " + " L_DealMoney,  " + stringHouseCar
        + "GiftMoney,    H_GiftMoney,                              L_GiftMoney,  " + stringHouseCar + "CommMoney,    H_CommMoney,                          L_CommMoney,  "
        + stringHouseCar + "PureMoney, " + " H_PureMoney,                            L_PureMoney,  " + stringHouseCar + "LastMoney,    H_LastMoney, " + " L_LastMoney,  "
        + stringHouseCar + "BalaMoney,    H_BalaMoney,                              L_BalaMoney, "
        + " SaleName1,                                  SaleName2,                                SaleName3,                                  SaleName4, "
        + " SaleName5,                                  MediaName,                               ZoneName,                                   MajorName, "
        + " UseType,                                      Remark,                                       DateRange,                                  DateCheck, "
        + " DateFile,                                       DateBonus,                                SaleName6,                                  SaleName7, "
        + " SaleName8,                                  SaleName9,                                SaleName10, " + stringHouseCar + "CommMoney1, "
        + " H_CommMoney1,                         L_CommMoney1 , " + stringHouseCar + "ViMoney, " + " H_ViMoney,                                   L_ViMoney " + " FROM  A_Sale "
        + " WHERE  ProjectID1  =  '" + stringProjectID + "'" + " AND  OrderDate  Between  '" + stringOrderDateStart + "'  AND  '" + stringOrderDateEnd + "' " + stringSqlAnd2
        + stringSqlAnd;
    if ("房子".equals(stringThisType)) {
      stringSql += " AND  LEN(Position)  >  0  ORDER BY  " + stringOrderBy;
    } else if ("車位".equals(stringThisType)) {
      stringSql += " AND  LEN(Car)  >  0  ORDER BY  " + stringOrderBy;
    }
    String[][] retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getSumSaleForASale(String stringHouseCar, String stringProjectID, String stringOrderDateEnd, String stringThisType, String stringSqlAnd2) throws Exception {
    String stringSql = "";
    /*
     * 0 H_Com 1 L_Com 2. SumPingSu 3. SumPreMoney 4. SumPreMoney2 5. SumPreMoney3
     * 6. SumDealMoney 7. SumDealMoney2 8. SumDealMoney3 9. SumGiftMoney 10.
     * SumGiftMoney2 11. SumGiftMoney3 12.SumCommMoney 13.SumCommMoney2 14.
     * SumCommMoney3 15. SumPureMoney 16. SumPureMoney2 17. SumPureMoney3 18.
     * SumLastMoney 19. SumLastMoney2 20. SumLastMoney3 21.SumBalaMoney 22.
     * SumBalaMoney2 23. SumBalaMoney3 24 SumCommMoney11 25.SumCommMoney12 26
     * SumCommMoney13 27 ViMoney1 28 ViMoney2 29 ViMoney3
     */
    stringSql = "SELECT H_Com,                           L_Com,  " + " SUM(PingSu),                SUM(" + stringHouseCar + "PreMoney),       SUM(H_PreMoney), "
        + " SUM(L_PreMoney),       SUM(" + stringHouseCar + "DealMoney),     SUM(H_DealMoney),  " + " SUM(L_DealMoney),     SUM(" + stringHouseCar
        + "GiftMoney),       SUM(H_GiftMoney), " + " SUM(L_GiftMoney),      SUM(" + stringHouseCar + "CommMoney),   SUM(H_CommMoney),  " + " SUM(L_CommMoney),  SUM("
        + stringHouseCar + "PureMoney),     SUM(H_PureMoney), " + " SUM(L_PureMoney),    SUM(" + stringHouseCar + "LastMoney),       SUM(H_LastMoney),  "
        + " SUM(L_LastMoney),     SUM(" + stringHouseCar + "BalaMoney),       SUM(H_BalaMoney), " + " SUM(L_BalaMoney),     SUM(" + stringHouseCar
        + "CommMoney1),  SUM(H_CommMoney1),  " + " SUM(L_CommMoney1),     SUM(" + stringHouseCar + "ViMoney),  SUM(H_ViMoney),  " + " SUM(L_ViMoney)" + " FROM  A_SALE "
        + " WHERE  ProjectID1 = '" + stringProjectID + "'" + " AND DEalmoney <> 0 " + " AND  OrderDate <= '" + stringOrderDateEnd + "'  " + stringSqlAnd2;
    // " AND Not IsDate(DelDate) " ;
    if ("房子".equals(stringThisType)) {
      stringSql += " AND LEN(Position) > 0 ";
    } else if ("車位".equals(stringThisType)) {
      stringSql += " AND LEN(Car) > 0 ";
    }
    stringSql += "GROUP BY H_Com,  L_Com ";
    System.out.println(stringSql);
    String[][] retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getDeleteForASale1(String stringProjectID, String stringOrderDateStart, String stringOrderDateEnd, String stringTypeC, String stringType, String stringSqlAnd)
      throws Exception {
    String stringSql = "";
    String stringOrderDateCondition = getValue("OrderDateCondition").trim();
    /*
     * 0.Position 1.PositionRent 2.Car 3.CarRent 4.Custom 5.OrderDate 6.OrderMon1
     * 7.OrderMon2 8.OrderMon3 9.EnougDate 10.EnougMon1 11.EnougMon2 12.EnougMon3
     * 13.ContrDate 14.ContrMon1 15.ContrMon2 16.ContrMon3 17.Deldate 18.PingSu
     * 19.PreMoney1 20.PreMoney2 21.PreMoney3 22.DealMoney1 23.DealMoney2
     * 24.DealMoney3 25.GiftMoney1 26.GiftMoney2 27.GiftMoney3 28.CommMoney1
     * 29.CommMoney2 30.CommMoney3 31.PureMoney1 32.PureMoney2 33.PureMoney3
     * 34.LastMoney1 35. LastMoney2 36.LastMoney3 37.BalaMoney1 38.BalaMoney2
     * 39.BalaMoney3 40.SaleName1 41.SaleName2 42.SaleName3 43.SaleName4
     * 44.SaleName5 45.MediaName 46.ZoneName 47.MajorName 48.UseType 49.Remark
     * 50.DateRange 51.DateCheck 52.DateFile 53.DateBonus 54.H_COM 55.L_COM
     * 56.SaleName6 57.SaleName7 58.SaleName8 59.SaleName9 60.SaleName10
     * 61.CommMoney11 62.CommMoney12 63.CommMoney13 64.ViMoney1 65.ViMoney2
     * 66.ViMoney3
     */
    stringSql = "SELECT  Position,                         PositionRent,                      Car,                                     CarRent,  "
        + " Custom,                           OrderDate,  " + stringType + "OrderMon ,  H_OrderMon,  " + " L_OrderMon,                   EnougDate,  " + stringType
        + "EnougMon,   H_EnougMon,  " + " L_EnougMon,                  ContrDate,  " + stringType + "ContrMon,    H_ContrMon,  "
        + " L_ContrMon ,                   Deldate,                               PingSu,  " + stringType + "PreMoney,  " + " H_PreMoney ,                   L_PreMoney,  "
        + stringType + "DealMoney,  H_DealMoney,  " + " L_DealMoney ,  " + stringType + "GiftMoney,   H_GiftMoney,                      L_GiftMoney,  " + stringType
        + "CommMoney,  H_CommMoney,                 L_CommMoney,  " + stringType + "PureMoney,  " + " H_PureMoney,                  L_PureMoney,  " + stringType
        + "LastMoney,  H_LastMoney,  " + " L_LastMoney ,  " + stringType + "BalaMoney,  H_BalaMoney,                    L_BalaMoney,  "
        + " SaleName1,                       SaleName2,                         SaleName3,                        SaleName4,  "
        + " SaleName5,                       MediaName,                        ZoneName,                         MajorName,  "
        + " UseType,                            Remark,                               DateRange,                        DateCheck,  "
        + " DateFile,                            DateBonus,                         H_COM,                              L_COM, "
        + " SaleName6,                       SaleName7,                          SaleName8,                       SaleName9, " + " SaleName10, " + stringType
        + "CommMoney1,  H_CommMoney1,           L_CommMoney1 ," + " " + stringType + "ViMoney,  H_ViMoney,           L_ViMoney " + " FROM A_Sale1 " + " WHERE  ProjectID1  =  '"
        + stringProjectID + "' " + " AND  DelDate  Between  '" + stringOrderDateStart + "'  AND  '" + stringOrderDateEnd + "' " + stringSqlAnd;
    // " AND IsDate(DelDate) "
    if ("Y".equals(stringOrderDateCondition)) {
      stringSql += " AND  OrderDate  NOT  BETWEEN  '" + stringOrderDateStart + "'  AND  '" + stringOrderDateEnd + "' ";
    }
    if ("房子".equals(stringTypeC)) {
      stringSql += " AND  ((Position           IS  NOT  NULL  AND  Position          <>  '')  OR" + " (PositionRent  IS  NOT  NULL  AND  PositionRent  <>  ''))  "
          + "  ORDER BY  Position,  PositionRent ";
    } else if ("車位".equals(stringTypeC)) {
      stringSql += " AND  ((Car           IS  NOT  NULL  AND  Car         <>  '')  OR" + " (CarRent  IS  NOT  NULL  AND  CarRent  <>  ''))  " + "  ORDER BY  Car,  CarRent ";
    }
    String[][] retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getSumDeleteForASale1(String stringHouseCar, String stringProjectID, String stringOrderDateEnd, String stringThisType, String stringSqlAnd) throws Exception {
    String stringSql = "";
    String stringOrderDateCondition = getValue("OrderDateCondition").trim();
    /*
     * 0.SumPingSu 1.SumPreMoney 2.SumPreMoney2 3.SumPreMoney3 4.SumDealMoney
     * 5.SumDealMoney2 6.SumDealMoney3 7.SumGiftMoney 8.SumGiftMoney2
     * 9.SumGiftMoney3 10.SumCommMoney 11.SumCommMoney2 12.SumCommMoney3
     * 13.SumPureMoney 14.SumPureMoney2 15.SumPureMoney3 16.SumLastMoney
     * 17.SumLastMoney2 18.SumLastMoney3 19.SumBalaMoney 20.SumBalaMoney2
     * 21.SumBalaMoney3 22.H_COM 23.L_COM 24.SumCommMoney11 25.SumCommMoney12 26
     * SumCommMoney13 27.Sum(ViMoney) 28.Sum(H_ViMoney) 29.Sum(L_ViMoney)
     */
    stringSql = "SELECT  SUM(PingSu),                SUM(" + stringHouseCar + "PreMoney),      SUM(H_PreMoney),  " + " SUM(L_PreMoney),       SUM(" + stringHouseCar
        + "DealMoney),    SUM(H_DealMoney),  " + " SUM(L_DealMoney),     SUM(" + stringHouseCar + "GiftMoney),      SUM(H_GiftMoney),  " + " SUM(L_GiftMoney),      SUM("
        + stringHouseCar + "CommMoney),  SUM(H_CommMoney),  " + " SUM(L_CommMoney),  SUM(" + stringHouseCar + "PureMoney),    SUM(H_PureMoney),  " + " SUM(L_PureMoney),  SUM("
        + stringHouseCar + "LastMoney),     SUM(H_LastMoney),  " + " SUM(L_LastMoney),      SUM(" + stringHouseCar + "BalaMoney),   SUM(H_BalaMoney), "
        + " SUM(L_BalaMoney),      H_COM,                                                     L_COM,  " + " SUM(" + stringHouseCar
        + "CommMoney1),  SUM(H_CommMoney1), SUM(L_CommMoney), " + " SUM(" + stringHouseCar + "ViMoney),    SUM(H_ViMoney) ,  SUM(L_ViMoney) " + " FROM  A_Sale1 "
        + " WHERE  ProjectID1  =  '" + stringProjectID + "' " + " AND  DelDate  <=  '" + stringOrderDateEnd + "' " + stringSqlAnd;
    // " AND IsDate(DelDate) "
    if ("Y".equals(stringOrderDateCondition)) {
      String stringOrderDateStart = getValue("OrderDateStart").trim();
      stringSql += " AND  OrderDate  NOT  BETWEEN  '" + stringOrderDateStart + "'  AND  '" + stringOrderDateEnd + "' ";
    }
    if ("房子".equals(stringThisType)) {
      stringSql += " AND  LEN(Position)  >  0 ";
    } else {
      stringSql += " AND  LEN(Car)  >  0 ";
    }
    stringSql += " GROUP BY  H_COM,  L_COM ";
    String[][] retASale = dbSale.queryFromPool(stringSql);
    return retASale;
  }

  public String[][] getSale02M020() throws Exception {
    String stringSql = "";
    String[][] retSale02M020 = null;
    //
    stringSql = "SELECT  ProjectID1  " + " FROM  Sale02M020 " + " WHERE  EMP_NO  =  '" + getUser() + "' ";
    retSale02M020 = dbSale.queryFromPool(stringSql);
    return retSale02M020;
  }

  public String[][] getSale02M020(String stringProjectID1) throws Exception {
    String stringSql = "";
    String[][] retSale02M020 = null;
    //
    stringSql = "SELECT  ProjectID1  " + " FROM  Sale02M020 " + " WHERE  EMP_NO  =  '" + getUser() + "' " + " AND  ProjectID1  = '" + stringProjectID1 + "' ";
    retSale02M020 = dbSale.queryFromPool(stringSql);
    return retSale02M020;
  }

  public String getInformation() {
    return "---------------\u5217\u5370\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
