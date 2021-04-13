package SaleEffect.Sale01R140;

import jcx.jform.bTransaction;
import jcx.db.*;
import jcx.util.*;
import com.jacob.activeX.*;
import com.jacob.com.*;
import java.util.*;
import Farglory.util.FargloryUtil;

public class FuncPrint extends bTransaction {
  FargloryUtil exeUtil = new FargloryUtil();
  talk dbSale = getTalk("" + get("put_dbSale"));
  talk dbAO = getTalk("dbAO");
  talk dbDoc = getTalk("dbDoc");

  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
    
    if (!isBatchCheckOK()) return false;

    long longTime1 = exeUtil.getTimeInMillis();

    doExcelNew();

    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("+關掉預覽---" + ((longTime2 - longTime1) / 1000) + "秒---");
    return false;
  }

  // 檢核
  // 前端資料檢核，正確回傳 True
  public boolean isBatchCheckOK() throws Throwable {
    int countOC = 0; // 付訂日跟簽約日必須要有一flag
    String retDate = "";
    int countDate = 0;

    // 付訂日期
    String strStartDate = getValue("StartDate").trim();  // 付訂日期-起
    if (!"".equals(strStartDate)) {
      retDate = exeUtil.getDateAC(strStartDate, "日期(起)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("StartDate").requestFocus();
        return false;
      }
      setValue("StartDate", retDate);
      countDate++;
      countOC++;
    }
    String strEndDate = getValue("EndDate").trim();      // 付訂日期-迄
    if (!"".equals(strEndDate)) {
      retDate = exeUtil.getDateAC(strEndDate, "日期(迄)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("EndDate").requestFocus();
        return false;
      }
      setValue("EndDate", retDate);
      countDate++;
    }
    if (countDate == 1) {
      message("[付訂日期(起)(迄)] 須同時限制。");
      return false;
    }
    
    //業績參照
    String dateType = this.getValue("dateType");
    if( dateType.length() == 0 ) {
      message("[業績參照] 不可空白。");
      return false;
    }
    if( !("OrderDate".equals(dateType) || "ContrDate".equals(dateType) || "小孩子才做選擇".equals(dateType)) ) {
      message("[業績參照] 參數錯誤。(再亂改阿 傻B!)");
      return false; 
    }

    return true;
  }

  public void doExcelNew() throws Throwable {
    //
    talk dbDocCS = getTalk("InvDoc");
    //
    if (dbDocCS == null) {
      messagebox("無法連線至 人壽-請購請款系統。");
    }
    //
    long longTime1 = exeUtil.getTimeInMillis();
    //
    Hashtable hashtableMoney = new Hashtable();
    String stringStartDate = getValue("StartDate").trim();
    String stringEndDate = getValue("EndDate").trim();
    String dateType = this.getValue("dateType");
    String stringSql = "";
    String stringToday = datetime.getToday1();
    String retArea[][] = null;
    String aryProjectID1[] = null;
    String stringArea = "";
    String stringProjectID1 = "";
    String stringSaleType = "";
    String retData[][] = null;
    String retData_2[][] = null;
    String retData_3[][] = null;
    double doubleTemp = 0;
    String stringNotInProjectID1Sql = "";
    String stringNotInProjectID1Sql2 = "";
    Vector vcTemp = new Vector();
    String stringNotExistProjectID1s = "";
    boolean blnHasProjectID1 = false;
    /*
     * 接待中心 701/702/703/704/710/730 POP 781/782/783/784/785/786 報紙 740/741/743/744/745/746/748/749 雜誌 750 電視 771 廣播 772 電影院 773 DM(派夾) 791/792 大型來人活動 742/892/803 網路 861/862 簡訊 863
     */
    stringSql = "SELECT T22.Area, '', T22.ProjectID " 
              + "FROM Sale09M020 T20, Sale09M022 T22 " 
              + "WHERE T20.TypeGUID=T22.TypeGUID " 
              + "AND T20.UseForType='媒體回饋表' "
              + "AND T20.ChooseType='A' " 
              + "AND '" + stringToday + "' BETWEEN T20.StartDate AND T20.EndDate " 
              + "ORDER BY T22.OrderByNo";
    retArea = dbSale.queryFromPool(stringSql);
    if (retArea.length == 0) {
      message("查無資料");
      if (dbDocCS != null) dbDocCS.close();
      return;
    }
    if ("B3774".equals(getUser())) {
      // retArea = new String[][]{{"新莊區","H82A"},{"中和區","H99A,H105A"}};
    }
    // 排除案別
    stringSql = "SELECT RTRIM(ProjectID) " + "FROM AO_SPSale03R530B_DenyProjectID";
    retData = dbAO.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      stringNotInProjectID1Sql = stringNotInProjectID1Sql.length() == 0 ? retData[intRow][0] : stringNotInProjectID1Sql + "','" + retData[intRow][0];
    }
    stringNotInProjectID1Sql2 = "AND (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END) NOT IN ('" + stringNotInProjectID1Sql + "') ";
    stringNotInProjectID1Sql = "AND ProjectID1 NOT IN ('" + stringNotInProjectID1Sql + "') ";
    
    //業績共用日期條件
    String saleQDate = "";
    if( "小孩子才做選擇".equals(dateType) ) {
      saleQDate = "AND OrderDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' "
                + "AND ContrDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' ";
    }else {
      saleQDate = "AND " + dateType + " BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' ";
    }
    
    // 業績
    stringSql = "SELECT DISTINCT RTRIM(ProjectID1) " 
              + "FROM A_Sale " 
              + "WHERE 1=1 "
              + saleQDate
              + "AND DealMoney > 0 "
              + "AND (LEN(Position) > 0 OR LEN(Car) > 0) " 
//              + "AND L_Com = '8' " 
              + stringNotInProjectID1Sql;
    retData = dbSale.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      vcTemp.addElement(retData[intRow][0]);
    }
    
    // 來人
    stringSql = "SELECT DISTINCT RTRIM(CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END) " + "FROM MIS_NewAOMixView WITH (NOLOCK) "
        + "WHERE CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND (Nego_Type IN ('新來人','複來訪') OR Nego_Way='來電') "
        + "AND ISNULL(value_point,0)>0 " + stringNotInProjectID1Sql2;
    retData = dbAO.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      if (vcTemp.indexOf(retData[intRow][0]) == -1) {
        vcTemp.addElement(retData[intRow][0]);
      }
    }
    // 費用
    stringSql = "SELECT DISTINCT RTRIM(ProjectID1) " + "FROM Doc3M014_UseMoney_AC_view " + "WHERE RTRIM(CostID)+RTRIM(CostID1) IN (SELECT RTRIM(CostID)+RTRIM(CostID1) " + "FROM Doc2M020 "
        + "WHERE BudgetID LIKE 'B%' " + "AND BudgetID NOT IN ('BF10', 'BF20') " + "AND  ComNo  =  'CS') " + "AND InOut='O' " + "AND DepartNo<>'0531' " + "AND CostID1<>'' " + "AND UseDate BETWEEN '"
        + stringStartDate + "' AND '" + stringEndDate + "' " + stringNotInProjectID1Sql;
    retData = dbDoc.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      if (vcTemp.indexOf(retData[intRow][0]) == -1) {
        vcTemp.addElement(retData[intRow][0]);
      }
    }

    // 20180716 強制補專案，未有資料也顯示
    String[] staticProjectList = {
        "H92A", "H93A", "H95A", "H98A", "H99A", "H101A", "H102A", "H103A", "H106A", "H100A", "H110A", "H111A" };
    for (int chk = 0; chk < staticProjectList.length; chk++) {
      if (vcTemp.indexOf(staticProjectList[chk]) == -1) {
        vcTemp.addElement(staticProjectList[chk]);
      }
    }

    /*
     * // 費用(CS) stringSql = "SELECT DISTINCT RTRIM(ProjectID1) "+ "FROM Doc3M014_UseMoney_AC_view "+ "WHERE RTRIM(CostID)+RTRIM(CostID1) IN (SELECT RTRIM(CostID)+RTRIM(CostID1) "+ "FROM Doc2M020 "+
     * "WHERE BudgetID LIKE 'B%' "+ "AND BudgetID NOT IN ('BF10', 'BF20') "+ ") "+ "AND InOut='O' "+ "AND DepartNo<>'0531' "+ "AND CostID1<>'' "+
     * "AND UseDate BETWEEN '"+stringStartDate+"' AND '"+stringEndDate+"' "+ stringNotInProjectID1Sql; retData = dbDocCS.queryFromPool(stringSql); for(int intRow=0; intRow<retData.length; intRow++){
     * if(vcTemp.indexOf(retData[intRow][0]) == -1){ vcTemp.addElement(retData[intRow][0]); } }
     */
    //
    aryProjectID1 = (String[]) vcTemp.toArray(new String[0]);
    Arrays.sort(aryProjectID1);
    //
    for (int intRow = 0; intRow < aryProjectID1.length; intRow++) {
      blnHasProjectID1 = false;
      for (int intNo = 0; intNo < retArea.length; intNo++) {
        if (retArea[intNo][2].indexOf(aryProjectID1[intRow]) >= 0) {
          blnHasProjectID1 = true;
          break;
        }
      }
      if (!blnHasProjectID1) {
        stringNotExistProjectID1s = stringNotExistProjectID1s.length() == 0 ? aryProjectID1[intRow] : stringNotExistProjectID1s + "," + aryProjectID1[intRow];
      }
    }

    //
    if (stringNotExistProjectID1s.length() > 0) {
      message("案別未設定於\"各式區域維護\"，請先設定：" + stringNotExistProjectID1s);
      return;
    }
    //
    for (int intNo = 0; intNo < retArea.length; intNo++) {
      for (int intRow = 0; intRow < aryProjectID1.length; intRow++) {
        stringProjectID1 = aryProjectID1[intRow];
        if (retArea[intNo][2].indexOf(aryProjectID1[intRow]) >= 0) {
          retArea[intNo][1] = retArea[intNo][1].length() == 0 ? aryProjectID1[intRow] : retArea[intNo][1] + "," + aryProjectID1[intRow];
        }
      }
    }
    //
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
    String stringFilePath = "G:\\資訊室\\Excel\\SaleEffect\\Sale01R140_Emaker_New_PA1.xltx";
    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    int intStartDataRow = 1;
    int intInsertDataRow = intStartDataRow;
    int intStartDataCol = 6;
    int intInsertDataCol = intStartDataCol;
    String stringCostID = "";
    String stringKEY = "";
    String[] arrayCostID = null;
    String stringAOProjectID1Sql = "";
    //
    Dispatch objectWorkbook = (Dispatch) retVector.get(4);
    Dispatch objectApplication = Dispatch.get(objectWorkbook, "Application").toDispatch();
    Dispatch.put(objectApplication, "Calculation", "3");
    //
    // 日期
    String stringExcelDate = "";
    if( "OrderDate".equals(dateType) ) {
      stringExcelDate = "付訂";
    }else if( "ContrDate".equals(dateType) ) {
      stringExcelDate = "簽約";
    }else {
      stringExcelDate = "付訂+簽約";
    }
    if ( !"".equals(stringStartDate) ) stringExcelDate += "日期:" + stringStartDate + "～" + stringEndDate;
    
    exeExcel.putDataIntoExcel(0, 0, stringExcelDate, objectSheet1);
    // 20180925 轉換 value_point = null 的資料
    String getValueNullPointSql = "SELECT Small_ID, Value_Point, count(*) FROM Cus_SmallMixView WHERE Value_Point is Null And Create_Time between '" + stringStartDate + " 00:00:00' and '"
        + stringEndDate + " 23:59:59' GROUP BY Small_ID, Value_Point, Create_Time ORDER BY Create_Time";
    String[][] listData = dbAO.queryFromPool(getValueNullPointSql);
    for (int idx = 0; idx < listData.length; idx++) {
      String updateValuePointValue = "1";
      switch (Integer.parseInt(listData[idx][2])) {
      case 1:
        updateValuePointValue = "1";
        break;
      case 2:
        updateValuePointValue = "0.5";
        break;
      case 3:
        updateValuePointValue = "0.333";
        break;
      case 4:
        updateValuePointValue = "0.25";
        break;
      case 5:
        updateValuePointValue = "0.2";
        break;
      }
      String transValuePointSql = "UPDATE [110701_cussmallmedia] SET value_point = '" + updateValuePointValue + "' WHERE Small_ID = " + Integer.parseInt(listData[idx][0]) + "";
      dbAO.execFromPool(transValuePointSql);
    }
    //
    // 匯出資料
    for (int intNo = 0; intNo < retArea.length; intNo++) {
      if (retArea[intNo][1].length() == 0) {
        continue;
      }
      //
      stringArea = retArea[intNo][0];
      aryProjectID1 = retArea[intNo][1].split(",");
      // System.out.println("stringArea = "+stringArea);
      //
      // 複製欄
      exeExcel.doCopyColumns(intInsertDataCol + 9, intInsertDataCol + 17, objectSheet1);
      //
      hashtableMoney = getMoneyHashtableUnion("B", stringStartDate, stringEndDate, aryProjectID1, exeUtil, dbDoc, dbDocCS);
      //
      for (int intRow = 0; intRow < aryProjectID1.length; intRow++) {
        intInsertDataRow = intStartDataRow;
        stringProjectID1 = aryProjectID1[intRow];
        // System.out.println("stringProjectID1 = "+stringProjectID1);
        //
        if ("H82A".equals(stringProjectID1)) {
          stringAOProjectID1Sql = "WHERE Series_Name LIKE '%H82A' ";
        } else {
          stringAOProjectID1Sql = "WHERE Series_Name='" + stringProjectID1 + "' ";
        }
        //
        /*
         * stringSql = "SELECT ProjectID "+ "FROM A_Group1 "+ "WHERE ProjectID1='"+stringProjectID1+"'"; 
         * retData = dbSale.queryFromPool(stringSql); 
         * if(retData.length > 0){ 
         *  stringSaleType = "仲介";
         * }else{ 
         *  stringSaleType = "銷售"; 
         * }
         */
        stringSaleType = "銷售";
        // 複製欄
        exeExcel.doCopyColumns(intInsertDataCol + 3, intInsertDataCol + 5, objectSheet1);
        // 案別
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, stringProjectID1, objectSheet1);
        // 業績******************************************************************************************************************
        // 銷售金額-目標
        stringSql = "SELECT ISNULL(SUM(Targets),0) " 
                  + "FROM A_STarMM " 
                      + "WHERE ProjectID='" + stringProjectID1 + "' " 
                  + "AND YearMM BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        retData = dbSale.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][0], objectSheet1);
        // 銷售金額-實際
        if (stringSaleType.equals("銷售")) {
          stringSql = "SELECT ISNULL(SUM(DealMoney),0) " 
                    + "FROM A_Sale " 
                    + "WHERE ProjectID1='" + stringProjectID1 + "' " 
                    + saleQDate
                    + "AND (LEN(Position) > 0 OR LEN(Car) > 0) ";
//                    + "AND L_Com = '8'";
        } else {
          stringSql = "SELECT ISNULL(SUM(OKSale),0) " + "FROM Sale02M050 " + "WHERE ProjectID1='" + stringProjectID1 + "' " 
                    + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][0], objectSheet1);
        // 媒體回饋成交成本率-目標
        intInsertDataRow = 8;
        stringSql = "SELECT CostRate/100 " + "FROM Doc7M020 " + "WHERE STATUS='U' " + "AND STATUS!='C' " + "AND FunctionType='B' " + "AND ProjectID1='" + stringProjectID1 + "'";
        retData = dbDoc.queryFromPool(stringSql);
        if (retData.length > 0) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        } else {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, "0", objectSheet1);
        }
        // 媒體來人來電************************************************************************************************************
        intInsertDataRow = 12;
        // 分類
        stringSql = "SELECT ISNULL(SUM(CASE WHEN Nego_Type='新來人' THEN value_point ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN Nego_Type IN ('新來人','複來訪') THEN value_point ELSE 0 END),0),"
            + "ISNULL(SUM(CASE WHEN Nego_Way='來電' THEN value_point ELSE 0 END),0) " + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        retData = dbAO.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        exeExcel.putDataIntoExcel(intInsertDataCol + 1, intInsertDataRow, retData[0][1], objectSheet1);
        exeExcel.putDataIntoExcel(intInsertDataCol + 2, intInsertDataRow++, retData[0][2], objectSheet1);
        //
        // 1 接待中心 2 POP 1 報紙 2 雜誌 3 電視 4 廣播 5 電影院 6 DM(派夾) 7 大型來人活動 8 外部合作 9 其他 10 網路 11 簡訊
        stringSql = "SELECT ISNULL(ROUND(SUM(CASE WHEN NewCode2='C01' THEN value_point ELSE 0 END),1),0) MediaSum1, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='K' THEN value_point ELSE 0 END),1),0) MediaSum2, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I04' THEN value_point ELSE 0 END),1),0) MediaSum3, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I05' THEN value_point ELSE 0 END),1),0) MediaSum4, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I06' THEN value_point ELSE 0 END),1),0) MediaSum5, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I07' THEN value_point ELSE 0 END),1),0) MediaSum6, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I08' THEN value_point ELSE 0 END),1),0) MediaSum7, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I09' THEN value_point ELSE 0 END),1),0) MediaSum8, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2 IN ('I10','I11','I12') THEN value_point ELSE 0 END),1),0) MediaSum9, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode4='I120101' THEN value_point ELSE 0 END),1),0) MediaSum10, " + "0 MediaSum11, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='D' THEN value_point ELSE 0 END),1),0) MediaSum12, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='L' THEN value_point ELSE 0 END),1),0) MediaSum13 "
            + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Type='新來人'";
        retData = dbAO.queryFromPool(stringSql);
        stringSql = "SELECT ISNULL(ROUND(SUM(CASE WHEN NewCode2='C01' THEN value_point ELSE 0 END),1),0) MediaSum1, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='K' THEN value_point ELSE 0 END),1),0) MediaSum2, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I04' THEN value_point ELSE 0 END),1),0) MediaSum3, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I05' THEN value_point ELSE 0 END),1),0) MediaSum4, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I06' THEN value_point ELSE 0 END),1),0) MediaSum5, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I07' THEN value_point ELSE 0 END),1),0) MediaSum6, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I08' THEN value_point ELSE 0 END),1),0) MediaSum7, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I09' THEN value_point ELSE 0 END),1),0) MediaSum8, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2 IN ('I10','I11','I12') THEN value_point ELSE 0 END),1),0) MediaSum9, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode4='I120101' THEN value_point ELSE 0 END),1),0) MediaSum10, " + "0 MediaSum11, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='D' THEN value_point ELSE 0 END),1),0) MediaSum12, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='L' THEN value_point ELSE 0 END),1),0) MediaSum13 "
            + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Type IN ('新來人','複來訪')";
        retData_2 = dbAO.queryFromPool(stringSql);
        stringSql = "SELECT ISNULL(ROUND(SUM(CASE WHEN NewCode2='C01' THEN value_point ELSE 0 END),1),0) MediaSum1, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='K' THEN value_point ELSE 0 END),1),0) MediaSum2, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I04' THEN value_point ELSE 0 END),1),0) MediaSum3, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I05' THEN value_point ELSE 0 END),1),0) MediaSum4, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I06' THEN value_point ELSE 0 END),1),0) MediaSum5, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I07' THEN value_point ELSE 0 END),1),0) MediaSum6, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I08' THEN value_point ELSE 0 END),1),0) MediaSum7, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I09' THEN value_point ELSE 0 END),1),0) MediaSum8, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2 IN ('I10','I11','I12') THEN value_point ELSE 0 END),1),0) MediaSum9, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode4='I120101' THEN value_point ELSE 0 END),1),0) MediaSum10, " + "0 MediaSum11, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='D' THEN value_point ELSE 0 END),1),0) MediaSum12, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='L' THEN value_point ELSE 0 END),1),0) MediaSum13 "
            + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Way='來電'";
        retData_3 = dbAO.queryFromPool(stringSql);
        for (int x = 0; x < retData[0].length; x++) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][x], objectSheet1);
          exeExcel.putDataIntoExcel(intInsertDataCol + 1, intInsertDataRow, retData_2[0][x], objectSheet1);
          exeExcel.putDataIntoExcel(intInsertDataCol + 2, intInsertDataRow++, retData_3[0][x], objectSheet1);
        }
        // 複來訪
        intInsertDataRow = 28;
        stringSql = "SELECT SUM(value_point) " + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
        // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Type='複來訪'";
        retData = dbAO.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        // 媒體成交金額*************************************************************************************************
        // 1 接待中心 2 POP 1 報紙 2 雜誌 3 電視 4 廣播 5 電影院 6 DM(派夾) 7 大型來人活動 8 外部合作 9 其他 10 網路 11 簡訊
        intInsertDataRow = 46;
        if (stringSaleType.equals("銷售")) {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN DealMoney ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN DealMoney ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN DealMoney ELSE 0 END),0),"
                    + "0, " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN DealMoney ELSE 0 END),0) "
                    + "FROM A_Sale " 
                    + "WHERE ProjectID1='" + stringProjectID1 + "' "
                    + saleQDate
                    + "AND (LEN(Position) > 0 OR LEN(Car) > 0) ";
//                    + "AND L_Com = '8'";
        } else {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN OKSale ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN OKSale ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN OKSale ELSE 0 END),0)," + "0, "
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN OKSale ELSE 0 END),0) " + "FROM Sale02M050 "
              + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        for (int x = 0; x < retData[0].length; x++) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][x], objectSheet1);
        }
        // 媒體成交戶數*************************************************************************************************
        // 1 接待中心 2 POP 1 報紙 2 雜誌 3 電視 4 廣播 5 電影院 6 DM(派夾) 7 大型來人活動 8 外部合作 9 其他 10 網路 11 簡訊
        intInsertDataRow = 61;
        if (stringSaleType.equals("銷售")) {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN 1 ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN 1 ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN 1 ELSE 0 END),0)," + "0, "
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN 1 ELSE 0 END),0) " 
              + "FROM A_Sale "
              + "WHERE ProjectID1='" + stringProjectID1 + "' "
              + saleQDate
//              + "AND L_Com = '8' "
              + "AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
              + "AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
              + "AND DealMoney > 0";
        } else {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN 1 ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN 1 ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN 1 ELSE 0 END),0)," + "0, "
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN 1 ELSE 0 END),0) " + "FROM Sale02M050 "
              + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        for (int x = 0; x < retData[0].length; x++) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][x], objectSheet1);
        }
        // 總成交合計
        intInsertDataRow = 76;
        if (stringSaleType.equals("銷售")) {
          stringSql = "SELECT COUNT(*) Counts " 
                    + "FROM A_Sale " 
                    + "WHERE ProjectID1='" + stringProjectID1 + "' "
                    + saleQDate
//                    + "AND L_Com = '8' " 
                    + "AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
                    + "AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
                    + "AND DealMoney > 0";
        } else {
          stringSql = "SELECT COUNT(*) Counts " + "FROM Sale02M050 " + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        // 媒體回饋成本*******************************************************************************************************
        // 1 接待中心 2 POP 1 報紙 2 雜誌 3 電視 4 廣播 5 電影院 6 DM(派夾) 7 大型來人活動 8 外部合作 9 其他 10 網路 11 簡訊
        // intInsertDataRow = 79;
        for (int intRowL = 67; intRowL < 94; intRowL++) {
          stringCostID = exeExcel.getDataFromExcel2(5, intRowL, objectSheet1);
          if ("".equals(stringCostID)) continue;
          arrayCostID = convert.StringToken(stringCostID, "\n");
          doubleTemp = 0;
          for (int intL = 0; intL < arrayCostID.length; intL++) {
            stringCostID = arrayCostID[intL];
            if ("".equals(stringCostID)) continue;
            stringKEY = stringProjectID1 + "%-%" + stringCostID;
            //
            doubleTemp += exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
            System.out.println("00(" + intL + ")stringKEY(" + stringKEY + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")--------------------------------------");
          }
          System.out.println("11Col(" + intInsertDataCol + ")Row(" + intRowL + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")--------------------------------------");
          exeExcel.putDataIntoExcel(intInsertDataCol, intRowL, convert.FourToFive("" + doubleTemp, 4), objectSheet1);
        }
        /*
         * arrayMoney = (String[])hashtableMoney.get(stringProjectID1); if(arrayMoney == null){ arrayMoney = new String[arrayCostID.length]; for(int intNoL=0; intNoL<arrayMoney.length; intNoL++)
         * arrayMoney[intNoL] = "0"; } for(int x=0; x<arrayMoney.length-1; x++){
         * System.out.println("Col("+intInsertDataCol+")Row("+(intInsertDataRow+1)+")Data("+arrayMoney[x]+")-------------------------------"); exeExcel.putDataIntoExcel(intInsertDataCol,
         * intInsertDataRow++, arrayMoney[x], objectSheet1); }
         */

        // 總體成本合計
        intInsertDataRow = 93;
        /*
         * stringSql = "SELECT SUM(RealMoney) RealMoney "+ "FROM Z_CoReaMM "+ "WHERE ProjectID1='"+stringProjectID1+"' "+ "AND YYMM BETWEEN '"+stringStartDate+"' AND '"+stringEndDate+"' "+
         * "AND (ComNo='06' OR ComNo='20' OR ComNo IS NULL OR LEN(ComNo)=0) "; retData = dbSale.queryFromPool(stringSql); exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0],
         * objectSheet1);
         */
        // exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, arrayMoney[arrayMoney.length-1], objectSheet1);
        // 媒體回饋成交成本率***Excel自動計算不用寫******************************************************************
        // 來人成本***Excel自動計算不用寫******************************************************************
        //
        intInsertDataCol = intInsertDataCol + 3;
      }
      //
      for (int intDel = 1; intDel <= 6; intDel++) {
        exeExcel.doDeleteColumns(intInsertDataCol, objectSheet1);
      }
      //
      exeExcel.putDataIntoExcel(intInsertDataCol, 1, stringArea, objectSheet1);
      //
      intInsertDataCol = intInsertDataCol + 3;
    }

    // 刪除多餘欄
    for (int intDel = 1; intDel <= 18; intDel++) {
      exeExcel.doDeleteColumns(intInsertDataCol, objectSheet1);
    }
    //
    Dispatch.put(objectApplication, "Calculation", "1");
    //
    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("實際---" + ((longTime2 - longTime1) / 1000) + "秒---");

    // 釋放 Excel 物件
    if (exeExcel != null) {
      exeExcel.getReleaseExcelObject(retVector);
    }
  }

  // 資料庫 Doc
  public Hashtable getMoneyHashtable(String stringDateStart, String stringDateEnd, String[] aryProjectID, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS) throws Throwable {
    return getMoneyHashtableUnion("A", stringDateStart, stringDateEnd, aryProjectID, exeUtil, dbDoc, dbDocCS);
  }

  public Hashtable getMoneyHashtableUnion(String stringType, String stringDateStart, String stringDateEnd, String[] aryProjectID, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS) throws Throwable {
    Hashtable hashtableMoney = new Hashtable();
    String stringProjectID1AndSql = "";
    //
    for (int intNo = 0; intNo < aryProjectID.length; intNo++) {
      if (!"".equals(stringProjectID1AndSql)) stringProjectID1AndSql += ", ";
      stringProjectID1AndSql += " '" + aryProjectID[intNo] + "' ";
      //
      System.out.println(intNo + "-------------------------------------" + aryProjectID[intNo]);
    }
    if (!"".equals(stringProjectID1AndSql)) stringProjectID1AndSql = " AND  ProjectID1 IN (" + stringProjectID1AndSql + ") ";
    //
    doCetMoney(stringType, stringDateStart, stringDateEnd, stringProjectID1AndSql, exeUtil, dbDoc, dbDocCS, hashtableMoney);
    return hashtableMoney;
  }

  public void doCetMoney(String stringType, String stringDateStart, String stringDateEnd, String stringProjectID1AndSql, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS, Hashtable hashtableMoney)
      throws Throwable {
    String stringSql = "";
    String stringAndSql11Date = "";
    String stringAndSql14 = "";
    String stringDate = "096/11/26";
    String[][] retData = new String[0][0];
    //
    stringDateStart = exeUtil.getDateConvert(stringDateStart);
    stringDateEnd = exeUtil.getDateConvert(stringDateEnd);
    //
    if (!"".equals(stringDateStart)) stringAndSql11Date = " AND  UseDate  >=  '" + stringDateStart + "' \n";
    if (!"".equals(stringDateEnd)) stringAndSql11Date += " AND  UseDate  <=  '" + stringDateEnd + "' \n";
    // 0 案別 1 CostID 2 CostID1 3 SSMediaID 4 金額
    // 0 採發金額
    stringSql = " SELECT  ProjectID1,  CostID,  CostID1,  SSMediaID,  SUM(RealMoney) \n" + " FROM  Doc3M014_UseMoney_AC_view \n"
        + " WHERE  RTRIM(CostID)+RTRIM(CostID1)  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc2M020 \n" + " WHERE  BudgetID  LIKE  'B%' \n" + " AND  BudgetID NOT IN ('BF10',  'BF20') "
        + " AND  ComNo  =  'CS' " + " ) \n" + " AND  InOut  =  'O' \n" + " AND  DepartNo  <>  '0531' \n" + "" + stringProjectID1AndSql + " \n" + " AND  CostID1  <> '' " + stringAndSql11Date
        + " GROUP BY ProjectID1,  CostID,  CostID1,  SSMediaID \n";
    retData = dbDoc.queryFromPool(stringSql);
    doData(stringType, retData, exeUtil, hashtableMoney);
    /*
     * if(dbDocCS != null) { System.out.println("人壽---------------------------------------1") ; retData = dbDocCS.queryFromPool(stringSql.replaceAll("AND  ComNo  =  'CS'", "")) ; doData(stringType,
     * retData, exeUtil, hashtableMoney) ; }
     */
  }

  public void doData(String stringType, String[][] retData, FargloryUtil exeUtil, Hashtable hashtableMoney) throws Throwable {
    int intPos = 0;
    String stringProjectID1 = "";
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostIDLimit = "";
    String stringMoney = "";
    String stringKEY = "";
    String stringSSMediaID = "";
    String[] arrayCostIDLimit = null;
    String[] arrayMoney = null;
    double doubleTemp = 0;
    // 0 案別 1 CostID 2 CostID1 3 SSMediaID 4 金額
    for (int intNo = 0; intNo < retData.length; intNo++) {
      stringProjectID1 = retData[intNo][0].trim();
      stringCostID = retData[intNo][1].trim();
      stringCostID1 = retData[intNo][2].trim();
      stringSSMediaID = retData[intNo][3].trim();
      stringMoney = retData[intNo][4].trim();
      //
      if ("B".equals(stringType)) {
        if (stringSSMediaID.length() < 9) {
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, 0, 1);
        } else {
          stringSSMediaID = convert.StringToken(stringSSMediaID, "-")[0];
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, stringSSMediaID.length() - 9, stringSSMediaID.length() - 8);
        }

        if ("C".equals(stringSSMediaID)) {
          stringCostID = "70";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // C 接待中心(接待通路)
        if ("K".equals(stringSSMediaID)) {
          stringCostID = "78";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // K POP通路
        if ("I".equals(stringSSMediaID)) {
          stringCostID = "89";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // I 媒體通路
        if ("D".equals(stringSSMediaID)) {
          stringCostID = "86";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // D 網路通路
        if ("L".equals(stringSSMediaID)) {
          stringCostID = "86";
          stringCostID1 = "3";
          stringSSMediaID = "";
        } // L 簡訊通路
      }
      stringKEY = stringProjectID1 + "%-%" + stringCostID + stringCostID1;
      doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
      System.out.println("原1(" + intNo + ")stringKEY(" + stringKEY + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")--------------------------------------");
      hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
      //
      stringKEY = stringProjectID1 + "%-%ALL";
      doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
      // System.out.println("原2("+intNo+")stringKEY("+stringKEY+")("+convert.FourToFive(""+doubleTemp, 4)+")--------------------------------------") ;
      hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
    }
  }

  public String getInformation() {
    return "---------------媒體回饋表(動支)(同新月報)(Sale01R140_AO5_New_PA1_2)----------------";
  }
}