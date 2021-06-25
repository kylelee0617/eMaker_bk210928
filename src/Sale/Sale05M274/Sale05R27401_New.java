// 修改日期:20161017~1018 員工編號:B3774  變動太多刪除所有Mark
package Sale.Sale05M274;

import jcx.jform.bTransaction;
import jcx.db.*;
import com.jacob.activeX.*;
import com.jacob.com.*;
import jcx.util.*;
import java.util.*;

public class Sale05R27401_New extends bTransaction {
  talk dbSale = getTalk("" + get("put_dbSale"));
  talk dbFED1 = getTalk("" + get("put_dbFED1"));
  talk dbFE3D = getTalk("" + get("put_dbFE3D"));
  talk dbCRM = getTalk("" + get("put_db400CRM"));
  // talk dbPW0D = getTalk("pw0d");

  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    Farglory.util.FargloryUtil exeUtil = new Farglory.util.FargloryUtil();
    long longTime1 = exeUtil.getTimeInMillis();
    doExcel();
    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("+關掉預覽---" + ((longTime2 - longTime1) / 1000) + "秒---");
    return false;
  }

  public void doExcel() throws Throwable {
    Farglory.util.FargloryUtil exeUtil = new Farglory.util.FargloryUtil();
    long longTime1 = exeUtil.getTimeInMillis();
    //
    String stringStatus = getValue("Status").trim(); // for Query
    String stringContractNo = getValue("ContractNo").trim();
    String stringCompanyCd = getValue("CompanyCd").trim();
    String stringProjectID1 = "";
    String stringBarCode = "";
    String stringContractType = "";
    String stringHouse = "";
    String stringCar = "";
    double doubleGiftMoney = 0;
    double doubleCommMoney = 0;
    double doubleCommMoney1 = 0;
    double doubleViMoney = 0;
    String stringCustomName = "";
    String stringContractDate = "";
    String stringTransferDate = "";
    String stringFieldName = ((javax.swing.JLabel) getcLabel("TransferDate").getComponent(1)).getText().trim();// for Query
    int intCars = 0;
    String stringCashInDate = "";
    String stringLoanMoney = "";
    String stringComLoanMoney = ""; // 修改日期:20170327 員工編號:B3774
    String stringSOther = "";
    String stringChangeNameFee = "";
    String stringOrderType = "";
    int intSpecialDocCount = 0;
    String stringIDType = "";
    String stringPassportType = "";
    String stringResidencePermitType = "";
    String stringMoneyScaleType = "";
    String stringDealType = "";
    String stringCmpChangeType = "";
    String stringAttachmentsRemark = "";
    String stringTemp = "";
    String stringLineTemp = "";
    String stringAttachmentOtherItems = "";
    int intAttachmentOtherItemsRow = 0;
    String stringSql = "";
    String retData[][] = null;
    String retSales[][] = null;
    String retSpecialData[][] = null;
    String stringTitle = "";
    String retFED1023[][] = null;
    String stringSales = "";
    String stringSalesCS = "";

    String aryHistoryData[][] = null;
    String stringState = "";
    String stringID = "";
    String stringIDName = "";
    String stringSigner = "";
    String stringSignerName = "";
    String stringTime = "";
    String stringSignerandTime1 = "";
    String stringSignerandTime2 = "";
    String stringSignerandTime3 = "";
    String stringSignerandTime4 = "";
    String stringSignerandTime5 = "";
    String stringIDandTimeOther1 = "";
    String stringIDandTimeOther2 = "";
    String stringIDandTimeOther3 = "";
    String stringIDandTimeOther4 = "";
    String stringIDandTimeOther5 = "";
    String stringIDandTimeCS1 = "";
    String stringIDandTimeCS2 = "";
    String stringIDandTimeCS3 = "";
    int intHistoryRow = 0;
    boolean blnIsNewData = true;
    boolean blnIsEndState = true;
    int intColDatas = 1;
    String stringLimitIDCS = "";
    String stringIsVoid = "";
    boolean blnHasCS = false;
    boolean blnHasNotCS = false;
    String stringOrderDate = "";
    String stringLogDateTime = datetime.getTime("YYYY/mm/dd h:m:s");
    // 法領遵循
    String strLCutName = ""; // 客戶名稱
    String strLRisk = ""; // 風險等級
    String strLRelt = ""; // 政治關係人
    String strLCBlst = ""; // 客戶控管名單
    String strLAName = ""; // 第三關係人
    String strLARisk = ""; // 第三風險等級
    String strLAlst = ""; // 第三關係人控管名單
    String strLARelt = ""; // 第三政治關係人
    //
    stringSql = "SELECT CmpCompanyCd " + "FROM Sale05M294 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "'";
    retData = dbSale.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      if ("CS".equals(retData[intRow][0])) {
        blnHasCS = true;
      } else if (!"CS".equals(retData[intRow][0])) {
        blnHasNotCS = true;
      }
    }
    //
    stringSql = "SELECT RTRIM(ProjectID1) ProjectID1, " + // 0
        "BarCode, " + "CustomName, " + "ContractDate, " + "CashInDate, " + "CONVERT(decimal(16,0),LoanMoney*10000) LoanMoney, " + // 5
        "SOther, " + "ChangeNameFee, " + "(CASE WHEN OrderType='0' THEN '10' WHEN OrderType='' THEN 'X' ELSE OrderType END) OrderType, "
        + "(CASE WHEN CmpChangeType='0' THEN '10' WHEN CmpChangeType='' THEN 'X' ELSE CmpChangeType END) CmpChangeType, "
        + "(CASE WHEN IDType='0' THEN '10' WHEN IDType='' THEN 'X' ELSE IDType END) IDType, " + // 10
        "(CASE WHEN PassportType='0' THEN '10' WHEN PassportType='' THEN 'X' ELSE PassportType END) PassportType, "
        + "(CASE WHEN ResidencePermitType='0' THEN '10' WHEN ResidencePermitType='' THEN 'X' ELSE ResidencePermitType END) ResidencePermitType, "
        + "(CASE WHEN MoneyScaleType='0' THEN '10' WHEN MoneyScaleType='' THEN 'X' ELSE MoneyScaleType END) MoneyScaleType, "
        + "(CASE WHEN DealType='0' THEN '10' WHEN DealType='' THEN 'X' ELSE DealType END) DealType, " + "AttachmentsRemark, " + // 15
        "IsVoid, " + "ContractType, " +
        // Start 修改日期:20170327 員工編號:B3774
        // "TransferDate "+
        "TransferDate, " + "ISNULL(CONVERT(decimal(16,0),ComLoanMoney*10000),0) ComLoanMoney " +
        // End 修改日期:20170327 員工編號:B3774
        "FROM Sale05M274 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "'";
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length > 0) {
      stringProjectID1 = retData[0][0];
      stringBarCode = retData[0][1];
      stringCustomName = retData[0][2];
      stringContractDate = retData[0][3];
      stringCashInDate = retData[0][4];
      stringLoanMoney = retData[0][5];
      stringSOther = retData[0][6];
      stringChangeNameFee = retData[0][7];
      stringOrderType = retData[0][8];
      stringCmpChangeType = retData[0][9];
      stringIDType = retData[0][10];
      stringPassportType = retData[0][11];
      stringResidencePermitType = retData[0][12];
      stringMoneyScaleType = retData[0][13];
      stringDealType = retData[0][14];
      stringAttachmentsRemark = retData[0][15];
      stringIsVoid = retData[0][16];
      stringContractType = retData[0][17];
      stringTransferDate = retData[0][18].trim();
      stringComLoanMoney = retData[0][19]; // 修改日期:20170327 員工編號:B3774
    }
    //
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
    String stringFilePath = "";
    if ("Y".equals(stringIsVoid)) {
      // stringFilePath = "G:\\資訊室\\Excel\\Sale05R27401_Void.xls";
      stringFilePath = "G:\\測試用\\Excel\\Sale05R27401_Void.xls";
    } else {
      // stringFilePath = "G:\\資訊室\\Excel\\Sale05R27401.xls";
      stringFilePath = "G:\\測試用\\Excel\\Sale05R27401.xls";
    }
    
    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheets = (Dispatch) retVector.get(3);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheetTemp = null;
    int intInsertDataRow = 0;
    int intInsertSaleRow1 = 0;
    int intInsertSaleRow2 = 0;
    int intDeleteDataRowS = 0;
    int intDeleteDataRowE = 0;
    int intDeleteDataRowCSS = 0;
    int intDeleteDataRowCSE = 0;
    // 條碼
    exeExcel.putDataIntoExcel(13, 1, "*" + stringBarCode + "*", objectSheet1);
    // 案別
    exeExcel.putDataIntoExcel(1, 3, stringProjectID1, objectSheet1);
    // 戶別
    stringSql = "SELECT Position " + "FROM Sale05M278 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' " + "AND HouseCar='House' "
        + "ORDER BY OrderNo, Position";
    retData = dbSale.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      stringHouse = stringHouse.length() == 0 ? retData[intRow][0] : stringHouse + "," + retData[intRow][0];
    }
    exeExcel.putDataIntoExcel(3, 3, stringHouse, objectSheet1);
    // 車位編號
    stringSql = "SELECT Position " + "FROM Sale05M278 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' " + "AND HouseCar='Car' "
        + "ORDER BY OrderNo, Position";
    retData = dbSale.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      stringCar = stringCar.length() == 0 ? retData[intRow][0] : stringCar + "," + retData[intRow][0];
      intCars++;
    }
    exeExcel.putDataIntoExcel(9, 3, stringCar, objectSheet1);
    // 客戶名稱
    exeExcel.putDataIntoExcel(3, 4, stringCustomName, objectSheet1);
    // 簽約日期
    exeExcel.putDataIntoExcel(9, 4, stringContractDate, objectSheet1);
    // 讓與日期
    if (stringTransferDate.length() > 0) {
      stringTransferDate = "(" + stringFieldName + "：" + stringTransferDate + ")";
      exeExcel.putDataIntoExcel(12, 4, stringTransferDate, objectSheet1);
    }

    // 類別*****************************************************************************************************************************
    // 房地款(元)、車位款(元)
    stringSql = "SELECT HouseCar, " + // 0
        "SUM(PingSu) PingSu, " + "SUM(ListPrice)*10000 ListPrice, " + "SUM(DealMoney)*10000 DealMoney, " + "SUM(GiftMoney)*10000 GiftMoney, " + "SUM(CommMoney)*10000 CommMoney, " + // 5
        "SUM(CommMoney1)*10000 CommMoney1, " + "SUM(ViMoney)*10000 ViMoney, " + "SUM(PureMoney)*10000 PureMoney, "
        + "ROUND(SUM(PureMoney)*10000/SUM(CASE WHEN HouseCar='House' THEN PingSu ELSE 1 END),0) PerPureMoney, " + "SUM(FloorPrice)*10000 FloorPrice, " + // 10
        "SUM(BalaMoney)*10000 BalaMoney, " + "(CASE WHEN HouseCar='House' THEN SUM(LoanCreditPercent) " + "ELSE (SELECT TOP 1 LoanCreditPercent " + "FROM Sale05M278 "
        + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' " + "AND HouseCar='Car' "
        + "ORDER BY OrderNo, RecordNo) END) LoanCreditPercent " + "FROM Sale05M278 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
        + "GROUP BY HouseCar " + "ORDER BY HouseCar desc";
    retData = dbSale.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      if ("House".equals(retData[intRow][0])) {
        intInsertDataRow = 6;
        // 坪數
        exeExcel.putDataIntoExcel(2, intInsertDataRow, retData[intRow][1], objectSheet1);
      } else {
        intInsertDataRow = 10;
        // 車位數
        exeExcel.putDataIntoExcel(2, intInsertDataRow, "" + intCars, objectSheet1);
      }
      // 牌 價
      exeExcel.putDataIntoExcel(3, intInsertDataRow, retData[intRow][2], objectSheet1);
      // 售 價
      exeExcel.putDataIntoExcel(5, intInsertDataRow, retData[intRow][3], objectSheet1);
      // 贈送、佣金、中原佣金、利息
      doubleGiftMoney = doubleGiftMoney + Double.parseDouble(retData[intRow][4]);
      doubleCommMoney = doubleCommMoney + Double.parseDouble(retData[intRow][5]);
      doubleCommMoney1 = doubleCommMoney1 + Double.parseDouble(retData[intRow][6]);
      doubleViMoney = doubleViMoney + Double.parseDouble(retData[intRow][7]);
      exeExcel.putDataIntoExcel(7, intInsertDataRow, retData[intRow][4], objectSheet1);
      exeExcel.putDataIntoExcel(7, intInsertDataRow + 1, retData[intRow][5], objectSheet1);
      exeExcel.putDataIntoExcel(7, intInsertDataRow + 2, retData[intRow][6], objectSheet1);
      exeExcel.putDataIntoExcel(7, intInsertDataRow + 3, retData[intRow][7], objectSheet1);
      // 淨售價
      exeExcel.putDataIntoExcel(8, intInsertDataRow, retData[intRow][8], objectSheet1);
      // 淨售單價
      exeExcel.putDataIntoExcel(9, intInsertDataRow, retData[intRow][9], objectSheet1);
      // 底價
      exeExcel.putDataIntoExcel(11, intInsertDataRow, retData[intRow][10], objectSheet1);
      // 超低價
      exeExcel.putDataIntoExcel(13, intInsertDataRow, retData[intRow][11], objectSheet1);
      // 貸款額度
      exeExcel.putDataIntoExcel(14, intInsertDataRow, retData[intRow][12], objectSheet1);
    }
    // 贈送、佣金、中原佣金、利息標題
    if (doubleGiftMoney > 0) {
      stringTemp = "■贈送";
    } else {
      stringTemp = "□贈送";
    }
    if (doubleCommMoney > 0) {
      stringTemp = stringTemp + "\n■佣金";
    } else {
      stringTemp = stringTemp + "\n□佣金";
    }
    if (doubleCommMoney1 > 0) {
      stringTemp = stringTemp + "\n■中原佣金";
    } else {
      stringTemp = stringTemp + "\n□中原佣金";
    }
    if (doubleViMoney > 0) {
      stringTemp = stringTemp + "\n■利息";
    } else {
      stringTemp = stringTemp + "\n□利息";
    }
    exeExcel.putDataIntoExcel(7, 5, stringTemp, objectSheet1);
    // **********************************************************************************************************************************

    // 特約事項***********************************************************************************************************************
    // 一、簽約金兌現日
    stringTemp = "一、簽約金兌現日：" + stringCashInDate;
    exeExcel.putDataIntoExcel(1, 15, stringTemp, objectSheet1);
    // 二、銀貸金額
    stringTemp = "二、銀貸金額：" + format.format(stringLoanMoney, "999,999,999,999").trim() + "元整";
    // Start 修改日期:20170327 員工編號:B3774
    if (operation.compareTo(stringComLoanMoney, "0") > 0) {
      stringTemp = stringTemp + "，公司貸總額：" + format.format(stringComLoanMoney, "999,999,999,999").trim() + "元整";
    }
    // End 修改日期:20170327 員工編號:B3774
    exeExcel.putDataIntoExcel(1, 16, stringTemp, objectSheet1);
    // 三、贈送
    stringSql = "SELECT T206.Spec, " + "T210.TotalAmt*10000 TotalAmt " + "FROM Sale05M210 T210, Sale05M092 T92, Sale05M206 T206 " + "WHERE T210.OrderNo=T92.OrderNo "
        + "AND T210.Position=T92.Position " + "AND T210.ItemNo=T206.ItemNo " + "AND (T210.Qty>0 OR T210.TotalAmt>0) " + "AND ISNULL(T92.StatusCd,'')='' "
        + "AND T210.OrderNo IN (SELECT OrderNo " + "FROM Sale05M275_New " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "') "
        + "ORDER BY T206.ItemNo";
    retData = dbSale.queryFromPool(stringSql);
    intInsertDataRow = 18;
    if (retData.length == 0) {
      stringTemp = "三、贈送：無";
      exeExcel.putDataIntoExcel(1, 17, stringTemp, objectSheet1);
      exeExcel.doDeleteRows(19, 19, objectSheet1);
    } else {
      // 寫贈送標題
      exeExcel.putDataIntoExcel(3, 17, "贈送名稱", objectSheet1);
      exeExcel.putDataIntoExcel(8, 17, "金額", objectSheet1);
      exeExcel.putDataIntoExcel(9, 17, "贈送名稱", objectSheet1);
      exeExcel.putDataIntoExcel(14, 17, "金額", objectSheet1);
      // 表格匯出
      for (int intRow = 0; intRow < retData.length;) {
        exeExcel.doCopyRow(19 + intRow / 2, objectSheet1);
        exeExcel.putDataIntoExcel(3, 18 + intRow / 2, retData[intRow][0], objectSheet1);
        exeExcel.putDataIntoExcel(8, 18 + intRow / 2, retData[intRow][1], objectSheet1);
        if (intRow + 1 < retData.length) {
          exeExcel.putDataIntoExcel(9, 18 + intRow / 2, retData[intRow + 1][0], objectSheet1);
          exeExcel.putDataIntoExcel(14, 18 + intRow / 2, retData[intRow + 1][1], objectSheet1);
        }
        intRow = intRow + 2;
        intInsertDataRow++;
      }
      exeExcel.doDeleteRows(intInsertDataRow + 1, intInsertDataRow + 1, objectSheet1);
      exeExcel.doLineStyle3("D18:O" + intInsertDataRow, objectSheet1, "1", "2", "1", "2", "1", "2", "", "", "1", "2", "1", "2");
    }
    // 四、佣金&中原佣金
    stringSql = "SELECT FriendID, '顧問'+FriendName+'-佣金'+CONVERT(varchar,CommMoney)+'元' STAT " + "FROM Sale05M354 " + "WHERE ContractNo='" + stringContractNo + "' "
        + "AND CommMoney>0 " + "UNION " + "SELECT FriendID, '顧問'+FriendName+'-中原佣金'+CONVERT(varchar,CommMoney1)+'元' STAT " + "FROM Sale05M354 " + "WHERE ContractNo='"
        + stringContractNo + "' " + "AND CommMoney1>0 " + "ORDER BY FriendID, STAT DESC ";
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length == 0) {
      stringTemp = "四、佣金：無";
    } else {
      for (int intRow = 0; intRow < retData.length; intRow++) {
        if (intRow == 0) {
          stringTemp = "四、佣金：" + retData[intRow][1];
        } else {
          stringTemp = stringTemp + ", " + retData[intRow][1];
        }
      }
    }
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    exeExcel.doDeleteRows(intInsertDataRow + 1, intInsertDataRow + 1, objectSheet1);
    // 五、特案內容
    stringSql = "SELECT DocNo1, " + "DocNo2, " + "DocNo3 " + "FROM Sale05M281 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
        + "ORDER BY DocNo1, DocNo2, DocNo3";
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length == 0) {
      stringTemp = "五、特案內容：無";
      exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
      intInsertDataRow++;
      exeExcel.doDeleteRows(intInsertDataRow + 1, intInsertDataRow + 1, objectSheet1);
    } else {
      for (int intRow = 0; intRow < retData.length; intRow++) {
        exeExcel.doCopyRow(intInsertDataRow + 2, objectSheet1);
        stringTemp = retData[intRow][0] + retData[intRow][1] + retData[intRow][2];
        stringSql = "SELECT T272.ItemlsName " + "FROM Sale05M282 T282, Sale05M272 T272 " + "WHERE T282.ItemCd=T272.ItemCd " + "AND T282.ItemlsCd=T272.ItemlsCd "
            + "AND T282.IsChoose='Y' " + "AND T282.ContractNo='" + stringContractNo + "' " + "AND T282.CompanyCd='" + stringCompanyCd + "' " + "AND T282.DocNo1='"
            + retData[intRow][0] + "' " + "AND T282.DocNo2='" + retData[intRow][1] + "' " + "AND T282.DocNo3='" + retData[intRow][2] + "' "
            + "ORDER BY LEN(T282.ItemlsCd), T282.ItemlsCd";
        retSpecialData = dbSale.queryFromPool(stringSql);
        for (int intNo = 0; intNo < retSpecialData.length; intNo++) {
          stringTemp = stringTemp + "," + retSpecialData[intNo][0];
        }
        exeExcel.putDataIntoExcel(3, intInsertDataRow, stringTemp, objectSheet1);
        intInsertDataRow++;
      }
      exeExcel.doDeleteRows(intInsertDataRow + 1, intInsertDataRow + 2, objectSheet1);
    }
    intSpecialDocCount = retData.length;
    // 六、名義人變更
    stringSql = "SELECT COUNT(*) Counts " + "FROM Sale05M279 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' " + "AND ITemCd='F01' "
        + "AND IsChoose='Y'";
    retData = dbSale.queryFromPool(stringSql);
    if (Integer.parseInt(stringChangeNameFee) > 0 || Integer.parseInt(retData[0][0]) > 0) {
      stringTemp = "六、■名義人變更，收手續費 ";
    } else {
      stringTemp = "六、□名義人變更，收手續費 ";
    }
    stringTemp = stringTemp + format.format(stringChangeNameFee, "999,999,999").trim() + "元整";
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    if (Integer.parseInt(retData[0][0]) > 0) {
      stringTemp = "    ■免收費關係";
    } else {
      stringTemp = "    □免收費關係";
    }
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    stringSql = "SELECT T279.ItemlsCd, " + // 0
        "((CASE WHEN T279.IsChoose='Y' THEN '■' ELSE '□' END)+T272.ItemlsName) ItemlsName " + "FROM Sale05M279 T279, Sale05M272 T272 " + "WHERE T279.ItemCd=T272.ItemCd "
        + "AND T279.ItemlsCd=T272.ItemlsCd " + "AND T279.ContractNo='" + stringContractNo + "' " + "AND T279.CompanyCd='" + stringCompanyCd + "' " + "AND T279.ITemCd='F01' "
        + "ORDER BY LEN(T279.ItemlsCd), T279.ItemlsCd";
    retData = dbSale.queryFromPool(stringSql);
    stringTemp = "";
    for (int intRow = 0; intRow < retData.length; intRow++) {
      stringTemp = stringTemp + retData[intRow][1];
    }
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    // 七、其它
    exeExcel.putDataIntoExcel(3, intInsertDataRow, stringSOther, objectSheet1);
    intInsertDataRow = intInsertDataRow + 5;
    // 八、法令遵循
    System.out.println("===========八、法令遵循==========");
    String[][] retCust = null;
    stringSql = "SELECT CustomNo, ContractNo FROM SALE05M277 WHERE CONTRACTNO = '" + stringContractNo + "'";
    retCust = dbSale.queryFromPool(stringSql);
    for (int i = 0; i < retCust.length; i++) {
      if (i > 0) {
        strLCutName = strLCutName + "\n";
        strLRelt = strLRelt + "\n";
        strLCBlst = strLCBlst + "\n";
        strLRisk = strLRisk + "\n";
      }
      // 由購屋證明單取得客戶
      String[][] retCustom = null;
      stringSql = "SELECT " + "c.OrderNo,c.RecordNo,c.auditorship,c.Nationality,c.CountryName,c.CustomNo,c.CustomName, "
          + "c.IsLinked,c.IsControlList,c.IsBlackList,c.TrxDate,c.TitleCD, c.RiskValue  " + // 8 9
          "FROM SALE05M091 c Inner Join SALE05M275_new n On c.OrderNo = n.OrderNo " + "WHERE ISNULL(c.StatusCd , '') = '' AND n.ContractNo = '" + stringContractNo
          + "' And c.CustomNo = '" + retCust[i][0] + "'";
      retCustom = dbSale.queryFromPool(stringSql);
      for (int j = 0; j < retCustom.length; j++) {
        strLCutName = strLCutName + retCustom[j][6];

        // 法人去查BEN
        if (retCustom[j][5].length() == 8) {
          boolean tempYN = false;
          // 先看法人自己黑不黑
          if ("Y".startsWith(retCustom[j][8]) || "Y".startsWith(retCustom[j][9])) {
            tempYN = true;
          }

          // 查詢訂單紀錄 (實受人)
          String[][] ret91Ben = null;
          String sql91Ben = "SELECT c.IsControlList,c.IsBlackList,c.BCustomNo FROM SALE05M091ben c Inner Join SALE05M275_new n On c.OrderNo = n.OrderNo " + "WHERE n.ContractNo = '"
              + stringContractNo + "'  AND c.CustomNo = '" + retCustom[j][5] + "' AND ISNULL(c.StatusCd , '') = '' ";
          ret91Ben = dbSale.queryFromPool(sql91Ben);

          boolean tempYN2 = false; // PEPS; 實受人只要有一筆就為"是"
          for (int l = 0; l < ret91Ben.length; l++) {// 多筆實受人
            if ("Y".equals(ret91Ben[l][0]) || "Y".equals(ret91Ben[l][1])) {
              tempYN = true;
            }
            // 政治關係(PEPS)
            String[][] retLBLst = null;
            stringSql = "SELECT CUSTOMERID FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L "
                + "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171') " +
//                            "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171' AND C.REMOVEDDATE >= '9999-12-31 24:00:00') " +
//                            "AND ISREMOVE = 'N' " +
                "AND CUSTOMERID = '" + ret91Ben[l][2].trim() + "'";
            retLBLst = dbCRM.queryFromPool(stringSql);
            if (retLBLst.length > 0) {
              tempYN2 = true;
            }
          }

          if (tempYN) {
            strLCBlst = strLCBlst + "是";
          } else {
            strLCBlst = strLCBlst + "否";
          }
          if (tempYN2) {
            strLRelt = strLRelt + "是";
          } else {
            strLRelt = strLRelt + "否";
          }
        } else { // 自然人
          if (!"".equals(retCustom[j][8])) {
            if ("Y".startsWith(retCustom[j][8]) || "Y".startsWith(retCustom[j][9])) {
              strLCBlst = strLCBlst + "是";
            } else {
              strLCBlst = strLCBlst + "否";
            }
          } else {
            strLCBlst = strLCBlst + "否";
          }
          // 政治關係(PEPS)
          String[][] retLBLst = null;
          stringSql = "SELECT CUSTOMERID FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L " +
//                          "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171' AND C.REMOVEDDATE >= '9999-12-31 24:00:00') " +
              "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171' ) " +
//                          "AND ISREMOVE = 'N' " +
              "AND CUSTOMERID = '" + retCust[i][0] + "'";
          retLBLst = dbCRM.queryFromPool(stringSql);
          if (retLBLst.length > 0) {
            strLRelt = strLRelt + "是";
          } else {
            strLRelt = strLRelt + "否";
          }
        }

        if (!"".equals(retCustom[j][12])) {
          strLRisk = strLRisk + retCustom[j][12];
        }
      }
    }

    // 第三人
    stringSql = "SELECT ContractNo, DesignatedId, DesignatedName, Controllist, RiskValue   FROM sale05m356 WHERE ContractNo = '" + stringContractNo + "'";
    retCust = dbSale.queryFromPool(stringSql);
    if (retCust.length > 0) {
      for (int i = 0; i < retCust.length; i++) {
        if (i > 0) {
          strLAName = strLAName + "\n";
          strLAlst = strLAlst + "\n";
          strLARelt = strLARelt + "\n";
          strLARisk = strLARisk + "\n";
        }
        strLAName = strLAName + retCust[i][2];
        if (!"".equals(retCust[i][3])) {
          if ("Y".startsWith(retCust[i][3])) {
            strLAlst = strLAlst + "是";
          } else {
            strLAlst = strLAlst + "否";
          }
        } else {
          strLAlst = strLAlst + "否";
        }
        strLARisk = strLARisk + (retCust[i][4]).trim();
        // 第三政治關係
        String[][] retLBLst = null;
        stringSql = "SELECT CUSTOMERID FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L "
            + "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171') " +
//                        "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171' AND C.REMOVEDDATE >= '9999-12-31 24:00:00') " +
//                        "AND ISREMOVE = 'N' " +
            "AND CUSTOMERID = '" + retCust[i][1] + "'";
        retLBLst = dbCRM.queryFromPool(stringSql);
        if (retLBLst.length > 0) {
          strLARelt = strLARelt + "是";
        } else {
          strLARelt = strLARelt + "否";
        }
      }

    } else {
      strLAName = strLAName + "無";
      strLAlst = strLAlst + "無";
      strLARelt = strLARelt + "無";
      strLARisk = strLARisk + "無";
    }

    exeExcel.putDataIntoExcel(2, intInsertDataRow, strLCutName, objectSheet1); // 姓名
    exeExcel.putDataIntoExcel(3, intInsertDataRow, strLRisk, objectSheet1); // 風險等級
    exeExcel.putDataIntoExcel(5, intInsertDataRow, strLCBlst, objectSheet1); // 控管黑名單
    exeExcel.putDataIntoExcel(7, intInsertDataRow, strLRelt, objectSheet1); // 政治關係人
    exeExcel.putDataIntoExcel(8, intInsertDataRow, strLAName, objectSheet1); // 第三人
    exeExcel.putDataIntoExcel(9, intInsertDataRow, strLARisk, objectSheet1); // 第三風險等級
    exeExcel.putDataIntoExcel(11, intInsertDataRow, strLAlst, objectSheet1); // 第三人控管黑名單
    exeExcel.putDataIntoExcel(13, intInsertDataRow, strLARelt, objectSheet1); // 第三政治關係
    intInsertDataRow = intInsertDataRow + 1;
    System.out.println("===============================");
    // **********************************************************************************************************************************

    // 附件*****************************************************************************************************************************
    // 1.訂單、成交紀錄
    stringTemp = "1.訂單 " + stringOrderType + " 份、" + "成交紀錄 " + stringDealType + " 份";
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    // 2.公司變更登記事項卡、身份證影本、護照影本、居留證影本
    stringTemp = "2.公司變更登記事項卡 " + stringCmpChangeType + " 份、" + "身份證影本 " + stringIDType + " 份";
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    stringTemp = "  護照影本 " + stringPassportType + " 份、" + "居留證影本 " + stringResidencePermitType + " 份";
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    // 3.拆款表、特案份數
    stringTemp = "3.拆款表 " + stringMoneyScaleType + " 份、" + "特案 " + intSpecialDocCount + " 份";
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    // 4.附件說明
    stringTemp = "4.附件說明：" + stringAttachmentsRemark;
    exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
    intInsertDataRow++;
    // 5.其它
    stringSql = "SELECT T279.ItemlsCd, "
        + "((CASE WHEN T279.IsChoose='Y' THEN '■' ELSE '□' END)+T272.ItemlsName+(CASE WHEN T279.IsChoose='Y' AND T279.ItemlsCd='Other99' THEN ' '+T279.Remark ELSE '' END)) ItemlsName "
        + "FROM Sale05M279 T279, Sale05M272 T272 " + "WHERE T279.ItemCd=T272.ItemCd " + "AND T279.ItemlsCd=T272.ItemlsCd " + "AND T279.ContractNo='" + stringContractNo + "' "
        + "AND T279.CompanyCd='" + stringCompanyCd + "' " + "AND T279.ITemCd='O01' " + "ORDER BY LEN(T279.ItemlsCd), T279.ItemlsCd";
    retData = dbSale.queryFromPool(stringSql);
    stringTemp = "";
    stringLineTemp = "";
    for (int intRow = 0; intRow < retData.length; intRow++) {
      stringLineTemp = stringLineTemp + retData[intRow][1];
      if (stringLineTemp.length() > 40) {
        stringTemp = stringTemp + "\n";
        stringLineTemp = retData[intRow][1];
      }
      stringTemp = stringTemp + retData[intRow][1];
    }
    exeExcel.putDataIntoExcel(2, intInsertDataRow, stringTemp, objectSheet1);
    stringAttachmentOtherItems = stringTemp;
    intAttachmentOtherItemsRow = intInsertDataRow;
    // 6.指定過戶人
    if ("成屋".equals(stringContractType)) {
      stringSql = "SELECT ToTransferName " + "FROM Sale05M350 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
          + "ORDER BY ToTransferName";
      retData = dbSale.queryFromPool(stringSql);
      if (retData.length > 0) {
        stringTemp = "";
        for (int intRow = 0; intRow < retData.length; intRow++) {
          stringTemp = stringTemp.length() == 0 ? retData[intRow][0] : stringTemp + "," + retData[intRow][0];
        }
        stringTemp = "6.指定過戶人：" + stringTemp;
        intInsertDataRow = intInsertDataRow + 3;
        intInsertSaleRow1 = intInsertDataRow + 6;
        intInsertSaleRow2 = intInsertDataRow + 3;
        intDeleteDataRowS = intInsertDataRow + 9;
        intDeleteDataRowE = intInsertDataRow + 15;
        intDeleteDataRowCSS = intInsertDataRow + 2;
        intDeleteDataRowCSE = intInsertDataRow + 9;
        exeExcel.putDataIntoExcel(1, intInsertDataRow, stringTemp, objectSheet1);
      } else {
        intInsertSaleRow1 = intInsertDataRow + 9;
        intInsertSaleRow2 = intInsertDataRow + 6;
        intDeleteDataRowS = intInsertDataRow + 12;
        intDeleteDataRowE = intInsertDataRow + 18;
        intDeleteDataRowCSS = intInsertDataRow + 5;
        intDeleteDataRowCSE = intInsertDataRow + 12;
      }
    } else {
      intInsertSaleRow1 = intInsertDataRow + 9;
      intInsertSaleRow2 = intInsertDataRow + 6;
      intDeleteDataRowS = intInsertDataRow + 12;
      intDeleteDataRowE = intInsertDataRow + 18;
      intDeleteDataRowCSS = intInsertDataRow + 5;
      intDeleteDataRowCSE = intInsertDataRow + 12;
    }
    // **********************************************************************************************************************************

    // 會簽單位***只是先暫存售出人及簽核人資訊*****************************************************************************
    // 售出人
    //
    stringSql = "SELECT SalesNo " + "FROM Sale05M295Print " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "'";
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length > 0) {
      if (blnHasNotCS) {
        stringSql = "SELECT DISTINCT SalesName, " + "SalesNo " + "FROM Sale05M295Print " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd
            + "' " + "AND CmpCompanyCd='01' " + "ORDER BY SalesNo";
        retData = dbSale.queryFromPool(stringSql);
        for (int intRow = 0; intRow < retData.length; intRow++) {
          stringSales = stringSales.length() == 0 ? retData[intRow][0] : stringSales + "," + retData[intRow][0];
        }
      }
      if (blnHasCS) {
        stringSql = "SELECT DISTINCT SalesName, " + "SalesNo " + "FROM Sale05M295Print " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd
            + "' " + "AND CmpCompanyCd='CS' " + "ORDER BY SalesNo";
        retData = dbSale.queryFromPool(stringSql);
        if (retData.length > 0) {
          for (int intRow = 0; intRow < retData.length; intRow++) {
            stringSalesCS = stringSalesCS.length() == 0 ? retData[intRow][0] : stringSalesCS + "," + retData[intRow][0];
          }
        } else {
          stringSalesCS = stringSales;
        }
      }
      //
      stringSql = "SELECT HLCompanyCd " + "FROM Sale05M289 T1 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
          + "AND HLCompanyCd='CS' " + "AND EXISTS(SELECT T92.SaleFlag " + "FROM Sale05M274 T274, Sale05M278 T278, Sale05M092 T92 " + "WHERE T274.ContractNo=T278.ContractNo "
          + "AND T274.CompanyCd=T278.CompanyCd " + "AND T278.OrderNo=T92.OrderNo " + "AND T278.HouseCar=T92.HouseCar " + "AND T278.Position=T92.Position "
          + "AND T274.ContractNo=T1.ContractNo " + "AND T274.CompanyCd=T1.CompanyCd " + "AND ISNULL(T92.SaleFlag,'')='')";
      retData = dbSale.queryFromPool(stringSql);
      if (retData.length > 0) {
        stringSql = "SELECT F_INP_TIME " + "FROM Sale05M274_FLOWC " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
            + "AND SUBSTRING(F_INP_TIME,1,8)>='20120808'";
        retData = dbSale.queryFromPool(stringSql);
        if (retData.length > 0) {
          stringSql = "SELECT SalesNo, " + "(CASE WHEN SUBSTRING(T295.SalesNo,1,1)='A' THEN 'A0'+SUBSTRING(T295.SalesNo,2,3) "
              + "WHEN SUBSTRING(T295.SalesNo,1,1)='C' THEN 'C0'+SUBSTRING(T295.SalesNo,2,3) " + "ELSE 'B'+T295.SalesNo END) SalesFullNo, " + "T90.Orderdate, " + "T295.SalesName "
              + "FROM Sale05M295 T295, Sale05M090 T90 " + "WHERE T295.OrderNo=T90.OrderNo " + "AND T295.ContractNo='" + stringContractNo + "' " + "AND T295.CompanyCd='"
              + stringCompanyCd + "' " + "ORDER BY T295.SalesNo";
          retData = dbSale.queryFromPool(stringSql);
          for (int intNo = 0; intNo < retData.length; intNo++) {
            stringOrderDate = convert.add0(convert.ac2roc(retData[intNo][2].replaceAll("/", "")), "7");
            stringSql = "SELECT EMP_NO " + "FROM FE3D74 " + "WHERE INSUR_KIND='1' " + "AND FIRM_NO='84703052' " + "AND EMP_NO='" + retData[intNo][1] + "' " + "AND '"
                + stringOrderDate + "' BETWEEN REGISTER_DATE AND (CASE WHEN ISNULL(CANCEL_DATE,'')='' THEN '9991231' ELSE CANCEL_DATE END)";
            retSales = dbFE3D.queryFromPool(stringSql);
            if (retSales.length > 0) {
              if (stringLimitIDCS.indexOf(retData[intNo][3]) == -1) {
                stringLimitIDCS = stringLimitIDCS.length() == 0 ? retData[intNo][1] : stringLimitIDCS + "," + retData[intNo][1];
              }
            }
          }
        } else {
          // 先暫存限定Sale05M315人員ID
          if ("H56A".equals(stringProjectID1) || "H73A".equals(stringProjectID1) || "H85A".equals(stringProjectID1) || "H93A".equals(stringProjectID1)
              || "H106A".equals(stringProjectID1)) {
            stringSql = "SELECT (CASE WHEN SUBSTRING(SalesNo,1,1)='C' THEN 'C0'+RIGHT(SalesNo,3) " + "ELSE 'B'+SalesNo END) SalesNo " + "FROM Sale05M315 " + "WHERE ProjectID1='"
                + stringProjectID1 + "' " + "AND '" + stringContractDate + "' BETWEEN StartDate AND EndDate " + "ORDER BY SalesNo";
            retData = dbSale.queryFromPool(stringSql);
            for (int intRow = 0; intRow < retData.length; intRow++) {
              stringLimitIDCS = stringLimitIDCS.length() == 0 ? retData[intRow][0] : stringLimitIDCS + "," + retData[intRow][0];
            }
            // Start 修改日期:20170802 員工編號:B3774
            // }else if("H91A".equals(stringProjectID1) || "H110A".equals(stringProjectID1)
            // || "H116A".equals(stringProjectID1)){
          } else if ("H91A".equals(stringProjectID1) || "H110A".equals(stringProjectID1) || "H111A".equals(stringProjectID1) || "H116A".equals(stringProjectID1)) {
            // End 修改日期:20170802 員工編號:B3774
            stringSql = "SELECT OrderDate " + "FROM Sale05M090 " + "WHERE OrderNo IN (SELECT OrderNo " + "FROM Sale05M275_New " + "WHERE ContractNo='" + stringContractNo + "' "
                + "AND CompanyCd='" + stringCompanyCd + "') " + "AND OrderDate>='2012/02/16'";
            retData = dbSale.queryFromPool(stringSql);
            if (retData.length > 0) {
              stringSql = "SELECT (CASE WHEN SUBSTRING(SalesNo,1,1)='C' THEN 'C0'+RIGHT(SalesNo,3) " + "ELSE 'B'+SalesNo END) SalesNo " + "FROM Sale05M315 " + "WHERE ProjectID1='"
                  + stringProjectID1 + "' " + "AND '" + stringContractDate + "' BETWEEN StartDate AND EndDate " + "ORDER BY SalesNo";
              retData = dbSale.queryFromPool(stringSql);
              for (int intRow = 0; intRow < retData.length; intRow++) {
                stringLimitIDCS = stringLimitIDCS.length() == 0 ? retData[intRow][0] : stringLimitIDCS + "," + retData[intRow][0];
              }
            }
          }
        }
      }
    } else {
      stringSql = "SELECT HLCompanyCd " + "FROM Sale05M289 T1 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
          + "AND HLCompanyCd='CS' " + "AND EXISTS(SELECT T92.SaleFlag " + "FROM Sale05M274 T274, Sale05M278 T278, Sale05M092 T92 " + "WHERE T274.ContractNo=T278.ContractNo "
          + "AND T274.CompanyCd=T278.CompanyCd " + "AND T278.OrderNo=T92.OrderNo " + "AND T278.HouseCar=T92.HouseCar " + "AND T278.Position=T92.Position "
          + "AND T274.ContractNo=T1.ContractNo " + "AND T274.CompanyCd=T1.CompanyCd " + "AND ISNULL(T92.SaleFlag,'')='')";
      retData = dbSale.queryFromPool(stringSql);
      if (retData.length > 0) {
        stringSql = "SELECT F_INP_TIME " + "FROM Sale05M274_FLOWC " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
            + "AND SUBSTRING(F_INP_TIME,1,8)>='20120808'";
        retData = dbSale.queryFromPool(stringSql);
        if (retData.length > 0) {
          stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
              + stringCompanyCd + "' " + "ORDER BY SalesNo";
          retData = dbSale.queryFromPool(stringSql);
          for (int intRow = 0; intRow < retData.length; intRow++) {
            if (stringSales.indexOf(retData[intRow][3]) == -1) {
              stringSales = stringSales.length() == 0 ? retData[intRow][3] : stringSales + "," + retData[intRow][3];
            }
            if (blnHasNotCS) {
              stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                  + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                  + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
              dbSale.execFromPool(stringSql);
              stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                  + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ,"
                  + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
              dbSale.execFromPool(stringSql);
            }
          }
          //
          stringSql = "SELECT T295.OrderNo, " + // 0
              "T295.RecordNo, " + "T295.SalesNo, " + "T295.SalesName, " + "(CASE WHEN SUBSTRING(T295.SalesNo,1,1)='A' THEN 'A0'+SUBSTRING(T295.SalesNo,2,3) "
              + "WHEN SUBSTRING(T295.SalesNo,1,1)='C' THEN 'C0'+SUBSTRING(T295.SalesNo,2,3) " + "ELSE 'B'+T295.SalesNo END) SalesFullNo, " + "T90.Orderdate " + // 5
              "FROM Sale05M295 T295, Sale05M090 T90 " + "WHERE T295.OrderNo=T90.OrderNo " + "AND T295.ContractNo='" + stringContractNo + "' " + "AND T295.CompanyCd='"
              + stringCompanyCd + "' " + "ORDER BY T295.SalesNo";
          retData = dbSale.queryFromPool(stringSql);
          for (int intRow = 0; intRow < retData.length; intRow++) {
            stringOrderDate = convert.add0(convert.ac2roc(retData[intRow][5].replaceAll("/", "")), "7");
            stringSql = "SELECT EMP_NO " + "FROM FE3D74 " + "WHERE INSUR_KIND='1' " + "AND FIRM_NO='84703052' " + "AND EMP_NO='" + retData[intRow][4] + "' " + "AND '"
                + stringOrderDate + "' BETWEEN REGISTER_DATE AND (CASE WHEN ISNULL(CANCEL_DATE,'')='' THEN '9991231' ELSE CANCEL_DATE END)";
            retSales = dbFE3D.queryFromPool(stringSql);
            if (retSales.length > 0) {
              if (stringSalesCS.indexOf(retData[intRow][3]) == -1) {
                stringSalesCS = stringSalesCS.length() == 0 ? retData[intRow][3] : stringSalesCS + "," + retData[intRow][3];
                stringLimitIDCS = stringLimitIDCS.length() == 0 ? retData[intRow][4] : stringLimitIDCS + "," + retData[intRow][4];
              }
              if (blnHasCS) {
                stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                    + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                    + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                dbSale.execFromPool(stringSql);
                stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                    + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ,"
                    + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                dbSale.execFromPool(stringSql);
              }
            }
          }
        } else {
          if ("H56A".equals(stringProjectID1) || "H85A".equals(stringProjectID1) || "H93A".equals(stringProjectID1) || "H106A".equals(stringProjectID1)) {
            stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
                + stringCompanyCd + "' " + "AND SalesNo IN (SELECT SalesNo " + "FROM Sale05M315 " + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND '" + stringContractDate
                + "' BETWEEN StartDate AND EndDate) " + "ORDER BY SalesNo";
            retData = dbSale.queryFromPool(stringSql);
            for (int intRow = 0; intRow < retData.length; intRow++) {
              if (stringSalesCS.indexOf(retData[intRow][3]) == -1) {
                stringSalesCS = stringSalesCS.length() == 0 ? retData[intRow][3] : stringSalesCS + "," + retData[intRow][3];
              }
              if (blnHasCS) {
                stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                    + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                    + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                dbSale.execFromPool(stringSql);
                stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                    + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ,"
                    + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                dbSale.execFromPool(stringSql);
              }
            }
          } else if ("H73A".equals(stringProjectID1)) {
            stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
                + stringCompanyCd + "' " + "ORDER BY SalesNo";
            retData = dbSale.queryFromPool(stringSql);
            for (int intRow = 0; intRow < retData.length; intRow++) {
              if (stringSales.indexOf(retData[intRow][3]) == -1) {
                stringSales = stringSales.length() == 0 ? retData[intRow][3] : stringSales + "," + retData[intRow][3];
              }
              if (blnHasNotCS) {
                stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                    + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                    + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                dbSale.execFromPool(stringSql);
                stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                    + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ,"
                    + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                dbSale.execFromPool(stringSql);
              }
            }
            //
            stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
                + stringCompanyCd + "' " + "AND SalesNo IN (SELECT SalesNo " + "FROM Sale05M315 " + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND '" + stringContractDate
                + "' BETWEEN StartDate AND EndDate) " + "ORDER BY SalesNo";
            retData = dbSale.queryFromPool(stringSql);
            for (int intRow = 0; intRow < retData.length; intRow++) {
              if (stringSalesCS.indexOf(retData[intRow][3]) == -1) {
                stringSalesCS = stringSalesCS.length() == 0 ? retData[intRow][3] : stringSalesCS + "," + retData[intRow][3];
              }
              if (blnHasCS) {
                stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                    + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                    + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                dbSale.execFromPool(stringSql);
                stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                    + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ,"
                    + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                dbSale.execFromPool(stringSql);
              }
            }
            // Start 修改日期:20170802 員工編號:B3774
            // }else if("H91A".equals(stringProjectID1) || "H110A".equals(stringProjectID1)
            // || "H116A".equals(stringProjectID1)){
          } else if ("H91A".equals(stringProjectID1) || "H110A".equals(stringProjectID1) || "H111A".equals(stringProjectID1) || "H116A".equals(stringProjectID1)) {
            // End 修改日期:20170802 員工編號:B3774
            stringSql = "SELECT OrderDate " + "FROM Sale05M090 " + "WHERE OrderNo IN (SELECT OrderNo " + "FROM Sale05M275_New " + "WHERE ContractNo='" + stringContractNo + "' "
                + "AND CompanyCd='" + stringCompanyCd + "') " + "AND OrderDate>='2012/02/16'";
            retData = dbSale.queryFromPool(stringSql);
            if (retData.length > 0) {
              stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
                  + stringCompanyCd + "' " + "AND SalesNo IN (SELECT SalesNo " + "FROM Sale05M315 " + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND '" + stringContractDate
                  + "' BETWEEN StartDate AND EndDate) " + "ORDER BY SalesNo";
              retData = dbSale.queryFromPool(stringSql);
              for (int intRow = 0; intRow < retData.length; intRow++) {
                if (stringSalesCS.indexOf(retData[intRow][3]) == -1) {
                  stringSalesCS = stringSalesCS.length() == 0 ? retData[intRow][3] : stringSalesCS + "," + retData[intRow][3];
                }
                if (blnHasCS) {
                  stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                      + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                      + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                  dbSale.execFromPool(stringSql);
                  stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, "
                      + "LogDateTime) " + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " "
                      + retData[intRow][1] + " ," + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                  dbSale.execFromPool(stringSql);
                }
              }
            } else {
              stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
                  + stringCompanyCd + "' " + "ORDER BY SalesNo";
              retData = dbSale.queryFromPool(stringSql);
              for (int intRow = 0; intRow < retData.length; intRow++) {
                if (stringSales.indexOf(retData[intRow][3]) == -1) {
                  stringSales = stringSales.length() == 0 ? retData[intRow][3] : stringSales + "," + retData[intRow][3];
                }
                if (blnHasNotCS) {
                  stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                      + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                      + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                  dbSale.execFromPool(stringSql);
                  stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, "
                      + "LogDateTime) " + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " "
                      + retData[intRow][1] + " ," + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                  dbSale.execFromPool(stringSql);
                }
                if (blnHasCS) {
                  stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                      + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                      + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                  dbSale.execFromPool(stringSql);
                  stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, "
                      + "LogDateTime) " + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " "
                      + retData[intRow][1] + " ," + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                  dbSale.execFromPool(stringSql);
                }
              }
              //
              stringSalesCS = stringSales;
            }
          } else {
            stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
                + stringCompanyCd + "' " + "ORDER BY SalesNo";
            retData = dbSale.queryFromPool(stringSql);
            for (int intRow = 0; intRow < retData.length; intRow++) {
              if (stringSales.indexOf(retData[intRow][3]) == -1) {
                stringSales = stringSales.length() == 0 ? retData[intRow][3] : stringSales + "," + retData[intRow][3];
              }
              if (blnHasNotCS) {
                stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                    + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                    + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                dbSale.execFromPool(stringSql);
                stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                    + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ,"
                    + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                dbSale.execFromPool(stringSql);
              }
              if (blnHasCS) {
                stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                    + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                    + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
                dbSale.execFromPool(stringSql);
                stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                    + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ,"
                    + "'" + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
                dbSale.execFromPool(stringSql);
              }
            }
            //
            stringSalesCS = stringSales;
          }
          // 先暫存限定Sale05M315人員ID
          if ("H56A".equals(stringProjectID1) || "H73A".equals(stringProjectID1) || "H85A".equals(stringProjectID1) || "H93A".equals(stringProjectID1)
              || "H106A".equals(stringProjectID1)) {
            stringSql = "SELECT (CASE WHEN SUBSTRING(SalesNo,1,1)='C' THEN 'C0'+RIGHT(SalesNo,3) " + "ELSE 'B'+SalesNo END) SalesNo " + "FROM Sale05M315 " + "WHERE ProjectID1='"
                + stringProjectID1 + "' " + "AND '" + stringContractDate + "' BETWEEN StartDate AND EndDate " + "ORDER BY SalesNo";
            retData = dbSale.queryFromPool(stringSql);
            for (int intRow = 0; intRow < retData.length; intRow++) {
              stringLimitIDCS = stringLimitIDCS.length() == 0 ? retData[intRow][0] : stringLimitIDCS + "," + retData[intRow][0];
            }
            // Start 修改日期:20170802 員工編號:B3774
            // }else if("H91A".equals(stringProjectID1) || "H110A".equals(stringProjectID1)
            // || "H116A".equals(stringProjectID1)){
          } else if ("H91A".equals(stringProjectID1) || "H110A".equals(stringProjectID1) || "H111A".equals(stringProjectID1) || "H116A".equals(stringProjectID1)) {
            // End 修改日期:20170802 員工編號:B3774
            stringSql = "SELECT OrderDate " + "FROM Sale05M090 " + "WHERE OrderNo IN (SELECT OrderNo " + "FROM Sale05M275_New " + "WHERE ContractNo='" + stringContractNo + "' "
                + "AND CompanyCd='" + stringCompanyCd + "') " + "AND OrderDate>='2012/02/16'";
            retData = dbSale.queryFromPool(stringSql);
            if (retData.length > 0) {
              stringSql = "SELECT (CASE WHEN SUBSTRING(SalesNo,1,1)='C' THEN 'C0'+RIGHT(SalesNo,3) " + "ELSE 'B'+SalesNo END) SalesNo " + "FROM Sale05M315 " + "WHERE ProjectID1='"
                  + stringProjectID1 + "' " + "AND '" + stringContractDate + "' BETWEEN StartDate AND EndDate " + "ORDER BY SalesNo";
              retData = dbSale.queryFromPool(stringSql);
              for (int intRow = 0; intRow < retData.length; intRow++) {
                stringLimitIDCS = stringLimitIDCS.length() == 0 ? retData[intRow][0] : stringLimitIDCS + "," + retData[intRow][0];
              }
            }
          }
        }
      } else {
        stringSql = "SELECT OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName " + "FROM Sale05M295 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='"
            + stringCompanyCd + "' " + "ORDER BY SalesNo";
        retData = dbSale.queryFromPool(stringSql);
        for (int intRow = 0; intRow < retData.length; intRow++) {
          if (stringSales.indexOf(retData[intRow][3]) == -1) {
            stringSales = stringSales.length() == 0 ? retData[intRow][3] : stringSales + "," + retData[intRow][3];
          }
          if (blnHasNotCS) {
            stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
            dbSale.execFromPool(stringSql);
            stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "01" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
            dbSale.execFromPool(stringSql);
          }
          if (blnHasCS) {
            stringSql = "INSERT INTO Sale05M295Print(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName) " + "VALUES('"
                + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "')";
            dbSale.execFromPool(stringSql);
            stringSql = "INSERT INTO Sale05M295Log(ContractNo, " + "CompanyCd, " + "CmpCompanyCd, " + "OrderNo, " + "RecordNo, " + "SalesNo, " + "SalesName, " + "LogDateTime) "
                + "VALUES('" + stringContractNo + "'," + "'" + stringCompanyCd + "'," + "'" + "CS" + "'," + "'" + retData[intRow][0] + "'," + " " + retData[intRow][1] + " ," + "'"
                + retData[intRow][2] + "'," + "'" + retData[intRow][3] + "'," + "'" + stringLogDateTime + "')";
            dbSale.execFromPool(stringSql);
          }
        }
        //
        stringSalesCS = stringSales;
      }
    }
    // 審核人(承辦、財務代銷)、營業經辦、營業部(含內業業管、行銷)、開發經辦、開發部、財務經辦、財務室、法務室、不動產、會計
    // 01要用內控簽核人，非01用實際簽核人
    Sale.Sale05M22701 exeFun = new Sale.Sale05M22701();
    stringSql = "SELECT RTRIM(F_INP_STAT) STAT, " + // 0
        "RTRIM(F_INP_ID) ID, " + // 1
        "RTRIM(Signer) Signer, " + // 2
        "(SUBSTRING(F_INP_TIME,1,4)+'/'+SUBSTRING(F_INP_TIME,5,2)+'/'+SUBSTRING(F_INP_TIME,7,2)) F_INP_TIME1 " + // 3
        "FROM Sale05M274_FLOWC_HIS " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' " + "ORDER BY F_INP_TIME";
    retData = dbSale.queryFromPool(stringSql);
    aryHistoryData = new String[retData.length][4];
    for (int intRow = 0; intRow < retData.length - 1; intRow++) {
      stringState = retData[intRow][0];
      stringSigner = retData[intRow + 1][2];
      stringSignerName = exeFun.getEmpName(stringSigner);
      if (stringSignerName.length() == 0) {
        stringSignerName = exeFun.getUserName(stringSigner);
      }
      stringTime = retData[intRow + 1][3];
      blnIsNewData = true;
      for (int intNo = intRow + 1; intNo < retData.length - 1; intNo++) {
        if (retData[intRow][0].equals(retData[intNo][0]) && retData[intRow + 1][2].equals(retData[intNo + 1][2])) {
          stringState = retData[intNo][0];
          stringSigner = retData[intNo + 1][2];
          stringSignerName = exeFun.getEmpName(stringSigner);
          if (stringSignerName.length() == 0) {
            stringSignerName = exeFun.getUserName(stringSigner);
          }
          stringTime = retData[intNo + 1][3];
        }
      }
      for (int intH = 0; intH < intHistoryRow; intH++) {
        if (stringState.equals(aryHistoryData[intH][0]) && stringSigner.equals(aryHistoryData[intH][2])) {
          blnIsNewData = false;
          break;
        }
      }
      if (blnIsNewData) {
        aryHistoryData[intHistoryRow][0] = stringState;
        aryHistoryData[intHistoryRow][1] = stringSigner;
        aryHistoryData[intHistoryRow][2] = stringSignerName;
        aryHistoryData[intHistoryRow][3] = stringTime;
        intHistoryRow++;
      }
    }
    for (int intRow = 0; intRow < aryHistoryData.length; intRow++) {
      if ((("N".equals(stringStatus) || "NA".equals(stringStatus) || "A".equals(stringStatus)) && "承辦".equals(aryHistoryData[intRow][0]))
          || "財務代銷".equals(aryHistoryData[intRow][0])) { // 審核人
        if (stringSignerandTime1.length() == 0) {
          stringSignerandTime1 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime1.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringSignerandTime1 = stringSignerandTime1 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      } else if ((!("N".equals(stringStatus) || "NA".equals(stringStatus) || "A".equals(stringStatus)) && "承辦".equals(aryHistoryData[intRow][0]))
          || "內業業管".equals(aryHistoryData[intRow][0]) || "行銷".equals(aryHistoryData[intRow][0]) || "營業經辦".equals(aryHistoryData[intRow][0])
          || "營業部".equals(aryHistoryData[intRow][0])) {
        if (stringSignerandTime2.length() == 0) {
          stringSignerandTime2 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime2.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringSignerandTime2 = stringSignerandTime2 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      } else if ("開發經辦".equals(aryHistoryData[intRow][0]) || "開發部".equals(aryHistoryData[intRow][0])) {
        if (stringSignerandTime3.length() == 0) {
          stringSignerandTime3 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime3.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringSignerandTime3 = stringSignerandTime3 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      } else if ("財務經辦".equals(aryHistoryData[intRow][0]) || "財務室".equals(aryHistoryData[intRow][0])) {
        if (stringSignerandTime4.length() == 0) {
          stringSignerandTime4 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime4.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringSignerandTime4 = stringSignerandTime4 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      } else if ("法務室".equals(aryHistoryData[intRow][0])) {
        if (stringSignerandTime5.length() == 0) {
          stringSignerandTime5 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime5.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringSignerandTime5 = stringSignerandTime5 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      }
    }
    //
    intHistoryRow = 0;
    aryHistoryData = new String[retData.length][4];
    for (int intRow = 0; intRow < retData.length - 1; intRow++) {
      stringState = retData[intRow][0];
      stringID = retData[intRow + 1][1];
      stringIDName = exeFun.getEmpName(stringID);
      if (stringIDName.length() == 0) {
        stringIDName = exeFun.getUserName(stringID);
      }
      stringTime = retData[intRow + 1][3];
      blnIsNewData = true;
      for (int intNo = intRow + 1; intNo < retData.length - 1; intNo++) {
        if (retData[intRow][0].equals(retData[intNo][0]) && retData[intRow + 1][1].equals(retData[intNo + 1][1])) {
          stringState = retData[intNo][0];
          stringID = retData[intNo + 1][1];
          stringIDName = exeFun.getEmpName(stringID);
          if (stringIDName.length() == 0) {
            stringIDName = exeFun.getUserName(stringID);
          }
          stringTime = retData[intNo + 1][3];
        }
      }
      for (int intH = 0; intH < intHistoryRow; intH++) {
        if (stringState.equals(aryHistoryData[intH][0]) && stringID.equals(aryHistoryData[intH][1])) {
          blnIsNewData = false;
          break;
        }
      }
      if (blnIsNewData) {
        aryHistoryData[intHistoryRow][0] = stringState;
        aryHistoryData[intHistoryRow][1] = stringID;
        aryHistoryData[intHistoryRow][2] = stringIDName;
        aryHistoryData[intHistoryRow][3] = stringTime;
        intHistoryRow++;
      }
    }
    for (int intRow = 0; intRow < aryHistoryData.length; intRow++) {
      if ("承辦".equals(aryHistoryData[intRow][0]) || "財務代銷".equals(aryHistoryData[intRow][0])) { // 審核人
        if (stringIDandTimeOther1.length() == 0) {
          stringIDandTimeOther1 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime1.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringIDandTimeOther1 = stringIDandTimeOther1 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
        //
        if (!(stringLimitIDCS.length() > 0 && stringLimitIDCS.indexOf(aryHistoryData[intRow][1]) == -1)) {
          if (stringIDandTimeCS1.length() == 0) {
            intColDatas++;
            stringIDandTimeCS1 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          } else if (stringIDandTimeCS1.indexOf(aryHistoryData[intRow][2]) == -1) {
            intColDatas++;
            stringIDandTimeCS1 = stringIDandTimeCS1 + (intColDatas % 2 == 0 ? "\n" : "") + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          }
        }
      } else if ("內業業管".equals(aryHistoryData[intRow][0]) || "行銷".equals(aryHistoryData[intRow][0]) || "營業經辦".equals(aryHistoryData[intRow][0])
          || "營業部".equals(aryHistoryData[intRow][0])) {
        if (stringIDandTimeOther2.length() == 0) {
          stringIDandTimeOther2 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime2.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringIDandTimeOther2 = stringIDandTimeOther2 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
        if (!(stringLimitIDCS.length() > 0 && stringLimitIDCS.indexOf(aryHistoryData[intRow][1]) == -1)) {
          if (stringIDandTimeCS1.length() == 0) {
            intColDatas++;
            stringIDandTimeCS1 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          } else if (stringIDandTimeCS1.indexOf(aryHistoryData[intRow][2]) == -1) {
            intColDatas++;
            stringIDandTimeCS1 = stringIDandTimeCS1 + (intColDatas % 2 == 0 ? "\n" : "") + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          }
        }
      } else if ("開發經辦".equals(aryHistoryData[intRow][0]) || "開發部".equals(aryHistoryData[intRow][0])) {
        if (stringIDandTimeOther3.length() == 0) {
          stringIDandTimeOther3 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime3.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringIDandTimeOther3 = stringIDandTimeOther3 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
        if (!(stringLimitIDCS.length() > 0 && stringLimitIDCS.indexOf(aryHistoryData[intRow][1]) == -1)) {
          if (stringIDandTimeCS1.length() == 0) {
            intColDatas++;
            stringIDandTimeCS1 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          } else if (stringIDandTimeCS1.indexOf(aryHistoryData[intRow][2]) == -1) {
            intColDatas++;
            stringIDandTimeCS1 = stringIDandTimeCS1 + (intColDatas % 2 == 0 ? "\n" : "") + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          }
        }
      } else if ("財務經辦".equals(aryHistoryData[intRow][0]) || "財務室".equals(aryHistoryData[intRow][0])) {
        if (stringIDandTimeOther4.length() == 0) {
          stringIDandTimeOther4 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime4.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringIDandTimeOther4 = stringIDandTimeOther4 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      } else if ("法務室".equals(aryHistoryData[intRow][0])) {
        if (stringIDandTimeOther5.length() == 0) {
          stringIDandTimeOther5 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringSignerandTime5.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringIDandTimeOther5 = stringIDandTimeOther5 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
        if (!(stringLimitIDCS.length() > 0 && stringLimitIDCS.indexOf(aryHistoryData[intRow][1]) == -1)) {
          if (stringIDandTimeCS1.length() == 0) {
            intColDatas++;
            stringIDandTimeCS1 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          } else if (stringIDandTimeCS1.indexOf(aryHistoryData[intRow][2]) == -1) {
            intColDatas++;
            stringIDandTimeCS1 = stringIDandTimeCS1 + (intColDatas % 2 == 0 ? "\n" : "") + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
          }
        }
      } else if ("不動產".equals(aryHistoryData[intRow][0])) {
        if (stringIDandTimeCS2.length() == 0) {
          stringIDandTimeCS2 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringIDandTimeCS2.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringIDandTimeCS2 = stringIDandTimeCS2 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      } else if ("會計".equals(aryHistoryData[intRow][0])) {
        if (stringIDandTimeCS3.length() == 0) {
          stringIDandTimeCS3 = aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        } else if (stringIDandTimeCS3.indexOf(aryHistoryData[intRow][2]) == -1) {
          stringIDandTimeCS3 = stringIDandTimeCS3 + "\n" + aryHistoryData[intRow][2] + " " + aryHistoryData[intRow][3];
        }
      }
    }
    // **********************************************************************************************************************************

    //
    Dispatch objectRange = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    Dispatch.call(objectRange, "select");

    // 是否審核結束
    stringSql = "SELECT F_INP_STAT " + "FROM Sale05M274_FLOWC " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "'";
    retData = dbSale.queryFromPool(stringSql);
    if (!"END".equals(retData[0][0])) {
      blnIsEndState = false;
    }

    // 依照房土公司別複製Sheet再處理*****************************************************************************************
    stringSql = "SELECT CmpCompanyCd, " + "CmpContractNo " + "FROM Sale05M294 " + "WHERE ContractNo='" + stringContractNo + "' " + "AND CompanyCd='" + stringCompanyCd + "' "
        + "ORDER BY CmpCompanyCd";
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length > 0) {
      // 有不同公司要複製Sheet
      for (int intRow = 0; intRow < retData.length - 1; intRow++) {
        Dispatch.call(objectSheet1, "Copy", objectSheet1);
        objectSheetTemp = Dispatch.call(objectSheets, "Item", "共用 (2)").toDispatch();
        // 判斷共用或是人壽 1.Sheet名稱 2.標題(人壽才要改) 3.刪除多餘會簽單位Rows 4.塞相關簽核人員
        Dispatch.put(objectSheetTemp, "Name", retData[intRow][0]);
        stringTitle = "";
        stringSql = "SELECT RTRIM(COMPANY_NAME) CompanyName " + "FROM FED1023 " + "WHERE COMPANY_CD='" + retData[intRow][0] + "'";
        retFED1023 = dbFED1.queryFromPool(stringSql);
        if (retFED1023.length > 0) {
          if ("預售".equals(stringContractType) || "成屋".equals(stringContractType)) {
            stringTitle = retFED1023[0][0] + "  合約會審簽呈(" + stringContractType + ")";
          } else {
            stringTitle = retFED1023[0][0] + "  合約會審簽呈";
          }
        }
        exeExcel.putDataIntoExcel(0, 0, stringTitle, objectSheetTemp);
        // 公文編號
        exeExcel.putDataIntoExcel(13, 2, retData[intRow][1], objectSheetTemp);
        // 附件
        // 5.其它
        exeExcel.putDataIntoExcel(2, intAttachmentOtherItemsRow, stringAttachmentOtherItems, objectSheetTemp);
        if ("CS".equals(retData[intRow][0])) {
          exeExcel.doDeleteRows(intDeleteDataRowCSS, intDeleteDataRowCSE, objectSheetTemp);
          // 售出人
          exeExcel.putDataIntoExcel(11, intInsertSaleRow2, stringSalesCS, objectSheetTemp);
          if (blnIsEndState) {
            // 審核人(內業業管、行銷、財務代銷、營業經辦、營業部、開發經辦、開發部、財務經辦、財務室、法務室)、不動產、會計
            // exeExcel.putDataIntoExcel( 6, intInsertSaleRow2, stringIDandTimeCS1,
            // objectSheetTemp);
            // exeExcel.putDataIntoExcel( 4, intInsertSaleRow2, stringIDandTimeCS2,
            // objectSheetTemp);
            exeExcel.putDataIntoExcel(2, intInsertSaleRow2, stringIDandTimeCS3, objectSheetTemp);
          }
        } else if ("01".equals(retData[intRow][0])) {
          exeExcel.doDeleteRows(intDeleteDataRowS, intDeleteDataRowE, objectSheetTemp);
          // 售出人
          exeExcel.putDataIntoExcel(12, intInsertSaleRow1, stringSales, objectSheetTemp);
          if (blnIsEndState) {
            // 審核人(內業業管、行銷、財務代銷)、營業經辦、營業部、開發經辦、開發部、財務經辦、財務室、法務室
            exeExcel.putDataIntoExcel(12, intInsertSaleRow1 - 3, stringSignerandTime1, objectSheetTemp);
            exeExcel.putDataIntoExcel(9, intInsertSaleRow1 - 2, stringSignerandTime2, objectSheetTemp);
            exeExcel.putDataIntoExcel(7, intInsertSaleRow1 - 2, stringSignerandTime3, objectSheetTemp);
            exeExcel.putDataIntoExcel(3, intInsertSaleRow1 - 2, stringSignerandTime4, objectSheetTemp);
            exeExcel.putDataIntoExcel(0, intInsertSaleRow1 - 2, stringSignerandTime5, objectSheetTemp);
          }
        } else {
          exeExcel.doDeleteRows(intDeleteDataRowS, intDeleteDataRowE, objectSheetTemp);
          // 售出人
          exeExcel.putDataIntoExcel(12, intInsertSaleRow1, stringSales, objectSheetTemp);
          if (blnIsEndState) {
            // 審核人(內業業管、行銷、財務代銷)、營業經辦、營業部、開發經辦、開發部、財務經辦、財務室、法務室
            exeExcel.putDataIntoExcel(12, intInsertSaleRow1 - 3, stringIDandTimeOther1, objectSheetTemp);
            exeExcel.putDataIntoExcel(9, intInsertSaleRow1 - 2, stringIDandTimeOther2, objectSheetTemp);
            exeExcel.putDataIntoExcel(7, intInsertSaleRow1 - 2, stringIDandTimeOther3, objectSheetTemp);
            exeExcel.putDataIntoExcel(3, intInsertSaleRow1 - 2, stringIDandTimeOther4, objectSheetTemp);
            exeExcel.putDataIntoExcel(0, intInsertSaleRow1 - 2, stringIDandTimeOther5, objectSheetTemp);
          }
        }
      }
      // 判斷共用或是人壽 1.Sheet名稱 2.標題(人壽才要改) 3.刪除多餘會簽單位Rows 4.塞相關簽核人員
      Dispatch.put(objectSheet1, "Name", retData[retData.length - 1][0]);
      stringSql = "SELECT RTRIM(COMPANY_NAME) CompanyName " + "FROM FED1023 " + "WHERE COMPANY_CD='" + retData[retData.length - 1][0] + "'";
      retFED1023 = dbFED1.queryFromPool(stringSql);
      if (retFED1023.length > 0) {
        if ("預售".equals(stringContractType) || "成屋".equals(stringContractType)) {
          stringTitle = retFED1023[0][0] + "  合約會審簽呈(" + stringContractType + ")";
        } else {
          stringTitle = retFED1023[0][0] + "  合約會審簽呈";
        }
      }
      exeExcel.putDataIntoExcel(0, 0, stringTitle, objectSheet1);
      // 公文編號
      exeExcel.putDataIntoExcel(13, 2, retData[retData.length - 1][1], objectSheet1);
      if ("CS".equals(retData[retData.length - 1][0])) {
        exeExcel.doDeleteRows(intDeleteDataRowCSS, intDeleteDataRowCSE, objectSheet1);
        // 售出人
        exeExcel.putDataIntoExcel(14, intInsertSaleRow2, stringSalesCS, objectSheet1); // 售出人
        if (blnIsEndState) {
          // 審核人(內業業管、行銷、財務代銷、營業經辦、營業部、開發經辦、開發部、財務經辦、財務室、法務室)、不動產、會計
          // exeExcel.putDataIntoExcel( 6, intInsertSaleRow2, stringIDandTimeCS1,
          // objectSheet1); //不動產審核
          // exeExcel.putDataIntoExcel( 4, intInsertSaleRow2, stringIDandTimeCS2,
          // objectSheet1); // 不動產部
          exeExcel.putDataIntoExcel(2, intInsertSaleRow2, stringIDandTimeCS3, objectSheet1); // 財務室
        }
      } else if ("01".equals(retData[retData.length - 1][0])) {
        exeExcel.doDeleteRows(intDeleteDataRowS, intDeleteDataRowE, objectSheet1);
        // 售出人
        exeExcel.putDataIntoExcel(14, intInsertSaleRow1, stringSales + "B1", objectSheet1);
        if (blnIsEndState) {
          // 審核人(內業業管、行銷、財務代銷)、營業經辦、營業部、開發經辦、開發部、財務經辦、財務室、法務室
          exeExcel.putDataIntoExcel(12, intInsertSaleRow1 - 3, stringSignerandTime1, objectSheet1);
          exeExcel.putDataIntoExcel(9, intInsertSaleRow1 - 2, stringSignerandTime2, objectSheet1);
          exeExcel.putDataIntoExcel(7, intInsertSaleRow1 - 2, stringSignerandTime3, objectSheet1);
          exeExcel.putDataIntoExcel(3, intInsertSaleRow1 - 2, stringSignerandTime4, objectSheet1);
          exeExcel.putDataIntoExcel(0, intInsertSaleRow1 - 2, stringSignerandTime5, objectSheet1);
        }
      } else {
        exeExcel.doDeleteRows(intDeleteDataRowS, intDeleteDataRowE, objectSheet1);
        // 售出人
        exeExcel.putDataIntoExcel(14, intInsertSaleRow1, stringSales + "C1", objectSheet1);
        if (blnIsEndState) {
          // 審核人(內業業管、行銷、財務代銷)、營業經辦、營業部、開發經辦、開發部、財務經辦、財務室、法務室
          exeExcel.putDataIntoExcel(12, intInsertSaleRow1 - 3, stringIDandTimeOther1, objectSheet1);
          exeExcel.putDataIntoExcel(9, intInsertSaleRow1 - 2, stringIDandTimeOther2, objectSheet1);
          exeExcel.putDataIntoExcel(7, intInsertSaleRow1 - 2, stringIDandTimeOther3, objectSheet1);
          exeExcel.putDataIntoExcel(3, intInsertSaleRow1 - 2, stringIDandTimeOther4, objectSheet1);
          exeExcel.putDataIntoExcel(0, intInsertSaleRow1 - 2, stringIDandTimeOther5, objectSheet1);
        }
      }
    }
    // **********************************************************************************************************************************
    for (int intRow = 0; intRow < retData.length; intRow++) {
//      System.out.println(">>>retData : " + retData[intRow][0]);
      objectSheetTemp = Dispatch.call(objectSheets, "Item", retData[intRow][0]).toDispatch();
      exeExcel.setProtect("Sale05R27401_New", objectSheetTemp);
    }

    //
    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("實際---" + ((longTime2 - longTime1) / 1000) + "秒---");

    // 釋放 Excel 物件
    if (exeExcel != null) {
      exeExcel.getReleaseExcelObject(retVector);
    }
  }

  public String getInformation() {
    return "---------------列印按鈕程式.preProcess()----------------";
  }
}