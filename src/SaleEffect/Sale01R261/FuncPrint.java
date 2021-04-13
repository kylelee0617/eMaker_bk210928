package SaleEffect.Sale01R261;

/**
 * 1. 透過SP查詢日期範圍內的通路
 * 2. 依照各月&各通路統計金額
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
	String projectId1 = "";
	String orderDate1 = "";
	String orderDate2 = "";
	String contrDate1 = "";
	String contrDate2 = "";

	// 欄位檢核
	public boolean isBatchCheckOK() throws Throwable {
		if (getValue("ProjectID1").length() == 0) {
			message("案別 不可空白!");
			return false;
		}
		
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
		if (countDate != 2) {
			message("[付訂日期(起)(迄)] 必填且須同時限制。");
			return false;
		}

		setValue("BuyerDate1", !"".equals(orderDate1) ? orderDate1 : contrDate1);
		setValue("BuyerDate2", !"".equals(orderDate2) ? orderDate2 : contrDate2);

		return true;
	}

	public boolean action(String value) throws Throwable {
		//欄位檢核
		if( !this.isBatchCheckOK() ) return false;
		
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
				  + " WHERE 1=1 "
				  + " AND BuyerDate BETWEEN '1900/01/01' AND '" + StringBuyerDate2 + "'"
			      + " AND LEN(SSMediaID) > 0 "
				  + " AND ProjectID = '" + stringProjectID1 + "' " 
				  + " GROUP BY ProjectID" + " ORDER BY ProjectID";
		String retProjectIDA[][] = dbSale.queryFromPool(stringSQL);
		if (retProjectID1.length == 0 && retProjectIDA.length == 0) {
			message("沒有資料!");
			return false;
		}
		//
		Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
		Vector retVector = exeExcel.getExcelObject("G:\\資訊室\\Excel\\SaleEffect\\Sale01R261A.xlt");
		Dispatch objectSheet1 = (Dispatch) retVector.get(1);

		if("OrderDate".equals(dateType)) {
			stringSQL = " speMakerSale01R251A2 "
					  + "'1900/01/01'," 
					  + "'" + StringOrderDate2 + "'," 
					  + "'1900/01/01'," 
					  + "'9999/12/31'," 
					  + "'1900/01/01'," 
					  + "'" + StringBuyerDate2 + "'," 
					  + "'" + stringProjectID1 + "',"
					  + "'" + stringSSMediaID1 + "'";
		}else {
			stringSQL = " speMakerSale01R251A2 " 
					  + "'1900/01/01'," 
					  + "'9999/12/31'," 
					  + "'1900/01/01'," 
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
		String stringYear = StringOrderDate1.substring(0, 4);

		// 案別:
		exeExcel.putDataIntoExcel(0, 1, "案別:" + stringProjectID1, objectSheet1);
		exeExcel.putDataIntoExcel(2, 1, "年度:" + stringYear, objectSheet1);
		for (int j = 0; j <= 12; j++) {
			switch (j) {
			case 0:
				StringOrderDate1 = stringYear + "/01/01";
				StringOrderDate2 = stringYear + "/01/31";
				break;
			case 1:
				StringOrderDate1 = stringYear + "/02/01";
				StringOrderDate2 = stringYear + "/02/29";
				if (!check.isDate(convert.replace(StringOrderDate2, "/", ""), "YYYYmmdd"))
					StringOrderDate2 = stringYear + "/02/28";
				break;
			case 2:
				StringOrderDate1 = stringYear + "/03/01";
				StringOrderDate2 = stringYear + "/03/31";
				break;
			case 3:
				StringOrderDate1 = stringYear + "/04/01";
				StringOrderDate2 = stringYear + "/04/30";
				break;
			case 4:
				StringOrderDate1 = stringYear + "/05/01";
				StringOrderDate2 = stringYear + "/05/31";
				break;
			case 5:
				StringOrderDate1 = stringYear + "/06/01";
				StringOrderDate2 = stringYear + "/06/30";
				break;
			case 6:
				StringOrderDate1 = stringYear + "/07/01";
				StringOrderDate2 = stringYear + "/07/31";
				break;
			case 7:
				StringOrderDate1 = stringYear + "/08/01";
				StringOrderDate2 = stringYear + "/08/31";
				break;
			case 8:
				StringOrderDate1 = stringYear + "/09/01";
				StringOrderDate2 = stringYear + "/09/30";
				break;
			case 9:
				StringOrderDate1 = stringYear + "/10/01";
				StringOrderDate2 = stringYear + "/10/31";
				break;
			case 10:
				StringOrderDate1 = stringYear + "/11/01";
				StringOrderDate2 = stringYear + "/11/30";
				break;
			case 11:
				StringOrderDate1 = stringYear + "/12/01";
				StringOrderDate2 = stringYear + "/12/31";
				break;
			case 12:
				StringOrderDate1 = "1900/01/01";
				StringOrderDate2 = stringYear + "/12/31";
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
					exeExcel.setBackgroundColorRange("15", "A" + (intRow + i + 1) + ":AF" + (intRow + i + 1), objectSheet1);
					// 金額
					if (retProjectID1.length > 0) {
						stringSQL = " SELECT SUM(DealMoney) " 
								  + " FROM A_Sale " 
								  + " WHERE " + dateType + " BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' "
								  + " AND ProjectID1 = '" + stringProjectID1 + "' " 
								  + " AND LEN(SSMediaID) > 0 " 
								  + stringSSMediaID1Sql 
								  + " AND SSMediaID LIKE '" + LMediaID + "%' ";
						/*
						 * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
						 * " WHERE SSMediaID LIKE '" + LMediaID + "%'" + " )" ;
						 */
					} else {
						stringSQL = " SELECT ISNULL(SUM(OKSale),0) OKSale " 
								  + " FROM Sale02M050 " 
								  + " WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " 
								  + " AND ProjectID='" + stringProjectID1 + "' " 
								  + " AND LEN(SSMediaID) > 0 " 
								  + " AND SSMediaID LIKE '" + LMediaID + "%' ";
					}
					String retLMedia[][] = dbSale.queryFromPool(stringSQL);
					if (retLMedia[0][0].length() == 0)
						SUMDealMoney = "0";
					else
						SUMDealMoney = retLMedia[0][0];
					// 累計
					if (j == 12)
						exeExcel.putDataIntoExcel(31, intRow + i, SUMDealMoney, objectSheet1);
					else
						exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
					// 戶數
					if (retProjectID1.length > 0) {
						stringSQL = " SELECT COUNT(DealMoney) " 
    								  + " FROM A_Sale " 
    								  + " WHERE " + dateType + " BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'"
    								  + " AND ProjectID1 = '" + stringProjectID1 + "'" 
    								  + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql 
    								  + " AND SSMediaID LIKE '" + LMediaID + "%'" 
    								  + " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
    								  + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
    								  + " AND DealMoney > 0 ";
					} else {
						stringSQL = "SELECT COUNT(Position) PositionCounts " + "FROM Sale02M050 T50, Sale02M051 T51 " + "WHERE T50.AgencyNo=T51.AgencyNo "
								+ "AND T50.BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " + "AND T50.ProjectID='" + stringProjectID1 + "' "
								+ "AND LEN(T50.SSMediaID) > 0 " + "AND T50.SSMediaID LIKE '" + LMediaID + "%' " + "AND T51.HouseCar='House'";
					}
					String retLMediaCount[][] = dbSale.queryFromPool(stringSQL);
					if (retLMediaCount[0][0].length() == 0)
						COUNTHouse = "0";
					else
						COUNTHouse = retLMediaCount[0][0];
					// 累計
					if (j == 12)
						exeExcel.putDataIntoExcel(30, intRow + i, COUNTHouse, objectSheet1);
					else
						exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
					// 個案加總
					doubleLCOUNTHouse += Double.parseDouble(COUNTHouse);
					doubleLSUMDealMoney += Double.parseDouble(SUMDealMoney);
					if (j != 12) {
						// 右合計
						StringCOUNTHouse = exeExcel.getDataFromExcel2(28, intRow + i, objectSheet1);
						if (StringCOUNTHouse.length() == 0)
							StringCOUNTHouse = "0";
						exeExcel.putDataIntoExcel(28, intRow + i, "" + (Double.parseDouble(StringCOUNTHouse) + Double.parseDouble(COUNTHouse)), objectSheet1);

						StringSUMDealMoney = exeExcel.getDataFromExcel2(29, intRow + i, objectSheet1);
						if (StringSUMDealMoney.length() == 0)
							StringSUMDealMoney = "0";
						exeExcel.putDataIntoExcel(29, intRow + i, "" + (Double.parseDouble(StringSUMDealMoney) + Double.parseDouble(SUMDealMoney)), objectSheet1);
					}
					LMediaGroup = LMediaID;
					intRow++;
				}
				// 中分類
				if (!MMediaGroup.equals(MMediaID)) {
					exeExcel.putDataIntoExcel(1, intRow + i, retMedia[i][3].trim(), objectSheet1);
					// 底色
					exeExcel.setBackgroundColorRange("36", "B" + (intRow + i + 1) + ":AF" + (intRow + i + 1), objectSheet1);
					// 金額
					if (retProjectID1.length > 0) {
						stringSQL = " SELECT SUM(DealMoney) " 
								  + "  FROM A_Sale " 
								  + " WHERE " + dateType + " BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'"
								  + " AND ProjectID1 = '" + stringProjectID1 + "'" 
								  + " AND LEN(SSMediaID) > 0 " 
								  + stringSSMediaID1Sql 
								  + " AND SSMediaID LIKE '" + MMediaID + "%'";
						/*
						 * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
						 * " WHERE SSMediaID LIKE '" + MMediaID + "%'" + " )" ;
						 */
					} else {
						stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " 
								  + "FROM Sale02M050 " 
								  + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " 
								  + "AND ProjectID='" + stringProjectID1 + "' " 
								  + "AND LEN(SSMediaID) > 0 " 
								  + "AND SSMediaID LIKE '" + MMediaID + "%'";
					}
					String retMMedia[][] = dbSale.queryFromPool(stringSQL);
					if (retMMedia[0][0].length() == 0)
						SUMDealMoney = "0";
					else
						SUMDealMoney = retMMedia[0][0];
					// 累計
					if (j == 12)
						exeExcel.putDataIntoExcel(31, intRow + i, SUMDealMoney, objectSheet1);
					else
						exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
					// 戶數
					if (retProjectID1.length > 0) {
						stringSQL = " SELECT COUNT(DealMoney) " 
								  + "  FROM A_Sale " 
								  + " WHERE " + dateType + " BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'"
								  + " AND ProjectID1 = '" + stringProjectID1 + "'" + " AND LEN(SSMediaID) > 0 " + stringSSMediaID1Sql + " AND SSMediaID LIKE '" + MMediaID + "%'" 
								  + " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
								  + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
								  + " AND DealMoney > 0 ";
					} else {
						stringSQL = "SELECT COUNT(Position) PositionCounts " 
								  + "FROM Sale02M050 T50, Sale02M051 T51 " + "WHERE T50.AgencyNo=T51.AgencyNo "
								+ "AND T50.BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " + "AND T50.ProjectID='" + stringProjectID1 + "' "
								+ "AND LEN(T50.SSMediaID) > 0 " + "AND T50.SSMediaID LIKE '" + MMediaID + "%' " + "AND T51.HouseCar='House'";
					}
					String retMMediaCount[][] = dbSale.queryFromPool(stringSQL);
					if (retMMediaCount[0][0].length() == 0)
						COUNTHouse = "0";
					else
						COUNTHouse = retMMediaCount[0][0];
					// 累計
					if (j == 12)
						exeExcel.putDataIntoExcel(30, intRow + i, COUNTHouse, objectSheet1);
					else
						exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
					if (j != 12) {
						// 右合計
						StringCOUNTHouse = exeExcel.getDataFromExcel2(28, intRow + i, objectSheet1);
						if (StringCOUNTHouse.length() == 0)
							StringCOUNTHouse = "0";
						exeExcel.putDataIntoExcel(28, intRow + i, "" + (Double.parseDouble(StringCOUNTHouse) + Double.parseDouble(COUNTHouse)), objectSheet1);

						StringSUMDealMoney = exeExcel.getDataFromExcel2(29, intRow + i, objectSheet1);
						if (StringSUMDealMoney.length() == 0)
							StringSUMDealMoney = "0";
						exeExcel.putDataIntoExcel(29, intRow + i, "" + (Double.parseDouble(StringSUMDealMoney) + Double.parseDouble(SUMDealMoney)), objectSheet1);
					}
					MMediaGroup = MMediaID;
					intRow++;
				}
				// 小、細分類
				if (LMediaGroup.equals(LMediaID) && MMediaGroup.equals(MMediaID)) {
					exeExcel.putDataIntoExcel(2, intRow + i, retMedia[i][5].trim(), objectSheet1);
					exeExcel.putDataIntoExcel(3, intRow + i, retMedia[i][7].trim(), objectSheet1);
					// 金額
					if (retProjectID1.length > 0) {
						stringSQL = " SELECT SUM(DealMoney) " 
								  + "  FROM A_Sale " 
								  + " WHERE " + dateType + " BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "' "
								  + " AND ProjectID1 = '" + stringProjectID1 + "'" 
								  + " AND LEN(SSMediaID) > 0 " 
								  + stringSSMediaID1Sql 
								  + " AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%'";
						/*
						 * " AND MediaID IN( SELECT DISTINCT MediaID " + " FROM SSMedia_Z_Cost " +
						 * " WHERE SSMediaID LIKE '" + retMedia[i][6].trim() + "%'" + " )" ;
						 */
					} else {
						stringSQL = "SELECT ISNULL(SUM(OKSale),0) OKSale " + "FROM Sale02M050 " + "WHERE BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2
								+ "' " + "AND ProjectID='" + stringProjectID1 + "' " + "AND LEN(SSMediaID) > 0 " + "AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%'";
					}
					String retSSMedia[][] = dbSale.queryFromPool(stringSQL);
					if (retSSMedia[0][0].length() == 0)
						SUMDealMoney = "0";
					else
						SUMDealMoney = retSSMedia[0][0];
					// 累計
					if (j == 12)
						exeExcel.putDataIntoExcel(31, intRow + i, SUMDealMoney, objectSheet1);
					else
						exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, intRow + i, SUMDealMoney, objectSheet1);
					// 戶數
					if (retProjectID1.length > 0) {
						stringSQL = " SELECT COUNT(DealMoney) " 
								  + " FROM A_Sale " 
								  + " WHERE " + dateType + " BETWEEN '" + StringOrderDate1 + "' AND '" + StringOrderDate2 + "'"
								  + " AND ProjectID1 = '" + stringProjectID1 + "'" 
								  + " AND LEN(SSMediaID) > 0 " 
								  + stringSSMediaID1Sql 
								  + " AND SSMediaID LIKE '" + retMedia[i][6].trim() + "%'" 
								  + " AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
								  + " AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
								  + " AND DealMoney > 0 ";
					} else {
						stringSQL = "SELECT COUNT(Position) PositionCounts " + "FROM Sale02M050 T50, Sale02M051 T51 " + "WHERE T50.AgencyNo=T51.AgencyNo "
								+ "AND T50.BuyerDate BETWEEN '" + StringBuyerDate1 + "' AND '" + StringBuyerDate2 + "' " + "AND T50.ProjectID='" + stringProjectID1 + "' "
								+ "AND LEN(T50.SSMediaID) > 0 " + "AND T50.SSMediaID LIKE '" + retMedia[i][6].trim() + "%' " + "AND T51.HouseCar='House'";
					}
					String retSSMediaCount[][] = dbSale.queryFromPool(stringSQL);
					if (retSSMediaCount[0][0].length() == 0)
						COUNTHouse = "0";
					else
						COUNTHouse = retSSMediaCount[0][0];
					// 累計
					if (j == 12)
						exeExcel.putDataIntoExcel(30, intRow + i, COUNTHouse, objectSheet1);
					else
						exeExcel.putDataIntoExcel(intColumn + 2 * j, intRow + i, COUNTHouse, objectSheet1);
					if (j != 12) {
						// 右合計
						StringCOUNTHouse = exeExcel.getDataFromExcel2(28, intRow + i, objectSheet1);
						if (StringCOUNTHouse.length() == 0)
							StringCOUNTHouse = "0";
						exeExcel.putDataIntoExcel(28, intRow + i, "" + (Double.parseDouble(StringCOUNTHouse) + Double.parseDouble(COUNTHouse)), objectSheet1);

						StringSUMDealMoney = exeExcel.getDataFromExcel2(29, intRow + i, objectSheet1);
						if (StringSUMDealMoney.length() == 0)
							StringSUMDealMoney = "0";
						exeExcel.putDataIntoExcel(29, intRow + i, "" + (Double.parseDouble(StringSUMDealMoney) + Double.parseDouble(SUMDealMoney)), objectSheet1);
					}
				}

				intRecordAll = intRow + i;
			}
			// 個案加總
			if (j == 12) {
				exeExcel.putDataIntoExcel(30, 99, "" + doubleLCOUNTHouse, objectSheet1);
				exeExcel.putDataIntoExcel(31, 99, "" + doubleLSUMDealMoney, objectSheet1);
			} else {
				exeExcel.putDataIntoExcel(intColumn + 2 * j, 99, "" + doubleLCOUNTHouse, objectSheet1);
				exeExcel.putDataIntoExcel(intColumn + 2 * j + 1, 99, "" + doubleLSUMDealMoney, objectSheet1);
			}
			// 右合計 & 個案加總
			if (j != 12) {
				StringCOUNTHouse = exeExcel.getDataFromExcel2(28, 99, objectSheet1);
				if (StringCOUNTHouse.length() == 0)
					StringCOUNTHouse = "0";
				exeExcel.putDataIntoExcel(28, 99, "" + (Double.parseDouble(StringCOUNTHouse) + doubleLCOUNTHouse), objectSheet1);

				StringSUMDealMoney = exeExcel.getDataFromExcel2(29, 99, objectSheet1);
				if (StringSUMDealMoney.length() == 0)
					StringSUMDealMoney = "0";
				exeExcel.putDataIntoExcel(29, 99, "" + (Double.parseDouble(StringSUMDealMoney) + doubleLSUMDealMoney), objectSheet1);
			}
			doubleLCOUNTHouse = 0;
			doubleLSUMDealMoney = 0;
		}
		exeExcel.doDeleteRows(intRecordAll + 2, 99, objectSheet1);
		for (int k = intColumnAll + 2; k <= 27; k++) {
			exeExcel.doDeleteColumns(intColumnAll + 2, objectSheet1);
		}
		exeExcel.setVisiblePropertyOnFlow(true, retVector); // 控制顯不顯示 Excel
		exeExcel.getReleaseExcelObject(retVector);
		return false;
	}

	public String getInformation() {
		return "---------------\u5217\u5370\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
	}
}
