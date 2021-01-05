/**
 * 2020-01-15 Kyle 整理code 並 新增洗防欄位
 * 各項註解代號為修改前代號，若下版無改動則修正為新版
 */

package Sale;

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
import Farglory.util.MLPUtils;

public class Sale05R110 extends bproc {
  FargloryUtil exeUtil = new FargloryUtil();
  MLPUtils mlpUtils = new MLPUtils();
  // 跳頁設定
  int intStartDataRow = 7;
  int intPageDataRow = 25;
  int intPageAllRow = 40;
  int intPageNo = 1;

  // table connect
  talk dbSale = getTalk("" + get("put_dbSale"));
  talk dbInvoice = getTalk("" + get("put_dbInvoice"));
  talk dbFE3D = getTalk("" + get("put_dbFE3D"));

  public String getDefaultValue(String value) throws Throwable {
    if (getValue("CompanyNo").length() == 0) {
      message("[公司代碼] 不可空白!");
      return value;
    }
    if (getValue("ProjectID1").length() == 0) {
      message("[案別代碼] 不可空白!");
      return value;
    }
    if (getValue("ReceiveDate").length() == 0) {
      message("應收帳款明細日期] 不可空白!");
      return value;
    }

    String stringSQL = "";
    stringSQL = " SELECT Sale05M111.DocNo, " + " Sale05M111.Position, " + " Sale05M111.CustomNo, " + " Sale05M111.PointNo, " + " Sale05M111.DetailItem, " + " Sale05M111.Remark, "
        + " Sale05M111.InvoiceNo, " + " Sale05M111.InvoiceMoney, " + " Sale05M111.InvoiceTax, " + " Sale05M111.InvoiceTotalMoney, " +
// Start 修改日：20090202 員工編號：B3774
        // " Sale05M111.InvoiceKind " +
        "Sale05M111.InvoiceKind, " + "Sale05M111.Endorse " +
// End
        " FROM Sale05M110,Sale05M111 " + " WHERE Sale05M110.ReceiveNo = Sale05M111.ReceiveNo ";
    if (getValue("CompanyNo").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.CompanyNo = '" + getValue("CompanyNo") + "'";
    }
    if (getValue("ProjectID1").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.ProjectID1 = '" + getValue("ProjectID1") + "'";
    }
    if (getValue("ReceiveDate").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.ReceiveDate = '" + getValue("ReceiveDate") + "'";
    }
    if (getValue("ReceiveNo").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.ReceiveNo = '" + getValue("ReceiveNo") + "'";
    }
    if (value.equals("列印土地")) {
      stringSQL = stringSQL + "  AND (PointNo='2102' OR PointNo='2104') ";
    }

    String retSale05M111[][] = dbSale.queryFromPool(stringSQL);
    if (retSale05M111.length == 0) {
      message("[應收帳款彙總表] 不存在!");
      return value;
    }
    // 建立com元件
    ActiveXComponent Excel;
    ComThread.InitSTA();
    Excel = ExcelVerson();
    Excel.setProperty("Visible", new Variant(true));
    Object objectExcel = Excel.getObject();
    Object objectWorkbooks = Dispatch.get(objectExcel, "Workbooks").toDispatch();

    // EXCEL 路徑
    String excelPath = "G:\\kyleTest\\Excel\\Sale05R110_AML.xlt";
    Object objectWorkbook = Dispatch.call(objectWorkbooks, "Open", excelPath).toDispatch();
    // System.out.println(">>>Excel Paht>>>" + excelPath);

    Object objectSheets = Dispatch.get(objectWorkbook, "Sheets").toDispatch();
    Object objectSheet1 = Dispatch.call(objectSheets, "Item", "Sheet1").toDispatch();
    Object objectSheet2 = Dispatch.call(objectSheets, "Item", "Sheet2").toDispatch();
    Dispatch.call(objectSheet2, "Activate");
    // Object objectSheet = Dispatch.get(objectWorkbook,"ActiveSheet").toDispatch();
    // A1 for Copy &Paste
    Object objectA1 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    // J36 經收單位.經辦
    Object objectJ36 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "M36" }, new int[1]).toDispatch();
    stringSQL = " SELECT EMP_NAME " + " FROM FE3D05 " + " WHERE EMP_NO = '" + getUser() + "' ";
    String retFE3D05[][] = dbFE3D.queryFromPool(stringSQL);
    if (!"CS".equals(getValue("CompanyNo"))) {
      if (retFE3D05.length == 0)
        Dispatch.put(objectJ36, "Value", getUser());
      else
        Dispatch.put(objectJ36, "Value", retFE3D05[0][0].trim());
    }
    // 在[A2]cell裡塞字
    Object objectA2 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A2" }, new int[1]).toDispatch();
    if (getValue("ProjectID1").equals("H58B") && value.equals("列印土地")) {
      stringSQL = " SELECT Company_Name " + " FROM FED1023 " + " WHERE Company_CD = 'CS' ";
    } else {
      stringSQL = " SELECT Company_Name " + " FROM FED1023 " + " WHERE Company_CD = '" + getValue("CompanyNo") + "'";
    }
    String retFED1023[][] = dbInvoice.queryFromPool(stringSQL);
    if (retFED1023.length == 0) {
      message("[公司代碼] 不存在!");
      return value;
    }
    Dispatch.put(objectA2, "Value", retFED1023[0][0].trim());
    // 在[A3]cell裡塞字
    Object objectA3 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A3" }, new int[1]).toDispatch();
    Dispatch.put(objectA3, "Value", getValue("ProjectID1") + "應收帳款明細彙總表");
    // 在[A4]cell裡塞字
    String stringReceiveNo = getValue("ReceiveNo").trim();
    String stringTemp = exeUtil.doSubstring(stringReceiveNo, 2, stringReceiveNo.length());
    // Object objectA3=Dispatch.invoke(objectSheet,"Range", Dispatch.Get,new
    // Object[] {"A3"},new int[1]).toDispatch();
    Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A4" }, new int[1]).toDispatch(), "Value",
        "應收帳款編號：" + stringTemp + "                                                        " + convert
            .FormatedDate(convert.ac2roc((getValue("ReceiveDate").substring(0, 4) + getValue("ReceiveDate").substring(5, 7) + getValue("ReceiveDate").substring(8, 10))), "年月日"));
    // Body
    int intRecordNo = intStartDataRow;
    // 折讓單 START
    double doubleMoney = 0;
    double doubleTax = 0;
    double doubleTotalMoney = 0;
    double doubleUsableMoney = 0;
    double doubleUsableMoneySum = 0;
    boolean booleanAdd = true;
    String stringDiscountReason = "";
    String stringDoc = "";
    String[][] retSale05M081 = null;
    // 折讓單 END

    String lastDocNo = ""; // 紀錄收款編號
    String lastCustNo = "";
    String projectID = getValue("ProjectID1").trim(); // 案號
    for (int intSale05M111 = 0; intSale05M111 < retSale05M111.length; intSale05M111++) {
      // System.out.println(">>>test intRecordNo>>>" + intRecordNo);
      String thisDocNo = retSale05M111[intSale05M111][0].trim(); // 本收款單號
      List listOrderNo = mlpUtils.getOrderNo(thisDocNo); // 訂單編號們
      String customNo = ""; // 統編 / ID
      String customName = ""; // 購買人姓名
      String riskValue = ""; // 購買人風險等級
      Map queryLog = mlpUtils.getQueryLog(); // 受控管名單
      String[][] retDeputy = mlpUtils.getDeputy(thisDocNo); // 代繳人名單
      int deputyLength = retDeputy.length; // 代繳人名單數量

      // for(int odNo=0 ; odNo<listOrderNo.size() ; odNo++){
      // System.out.println(">>>orderNo>>>[" + listOrderNo.get(odNo)+ "]");
      // }

      // 是不是同一筆收款單號
      boolean printMLP = false; // 是否輸出洗防資訊
      if (!thisDocNo.equals(lastDocNo)) {
        printMLP = true;
      }

      // A 收款編號
      stringTemp = retSale05M111[intSale05M111][0];
      stringTemp = exeUtil.doSubstring(stringTemp, 2, stringTemp.length());
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A" + intRecordNo }, new int[1]).toDispatch(), "Value", stringTemp);

      // B 棟樓別
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "B" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][1]);

      // C 客戶
      StringBuilder qOrderNo = new StringBuilder();
      for (int odNo = 0; odNo < listOrderNo.size(); odNo++) {
        System.out.println(">>>orderNo>>>[" + listOrderNo.get(odNo) + "]");

        if (odNo != 0)
          qOrderNo.append(",");
        qOrderNo.append("'").append(listOrderNo.get(odNo)).append("'");
      }
      stringSQL = "select distinct " + "T84.CustomName ,T91.riskValue , T84.CustomNo " + "from Sale05M084 T84 ,  Sale05M091 T91 " + "where 1=1 "
          + "and T84.CustomNo = T91.CustomNo  " + "and (( ISNULL(T91.TrxDate,'')<>'' ) or ISNULL(T91.TrxDate,'')='' ) " + "and T84.DocNo  =  '" + thisDocNo + "' "
          + "and T84.CustomNo = '" + retSale05M111[intSale05M111][2].trim() + "' " + "and T91.OrderNo in (" + qOrderNo.toString().trim() + ") ";
      // +"and ( T91.StatusCd != 'C' or T91.StatusCd is null ) ";
      String retSale05M084[][] = dbSale.queryFromPool(stringSQL);
      if (retSale05M084.length == 0) {
        message("[客戶代碼] 不存在!");
        // return value;
      }

      if (retSale05M084.length != 0) {
        // for(int i=0 ; i<retSale05M084.length ; i++){
        // if( i!= 0 ) {
        // customNo += "\n";
        // customName += "\n";
        // riskValue += "\n";
        // }
        // customNo += retSale05M084[i][2].trim();
        // customName += retSale05M084[i][0].trim();
        // riskValue += retSale05M084[i][1].trim();
        // }

        // ID永遠只會有一筆 (理論上)
        customNo = retSale05M084[0][2].trim();
        customName = retSale05M084[0][0].trim();
        riskValue = retSale05M084[0][1].trim();

        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "C" + intRecordNo }, new int[1]).toDispatch(), "Value", customName);
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "D" + intRecordNo }, new int[1]).toDispatch(), "Value", riskValue);
      }

      // (new D) 實質受益人(若購買人為法人)
      String realBeneficiary = "";
      String[][] arrBeneficiary = new String[0][0];
      if (retSale05M084.length != 0) {
        if (mlpUtils.isCusCompany(customNo)) {
          arrBeneficiary = mlpUtils.getCtrlBeneficiary(listOrderNo, customNo);
          realBeneficiary = mlpUtils.getBeneficiaryCtrlYN(projectID, queryLog, arrBeneficiary, "list");
        }
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "E" + intRecordNo }, new int[1]).toDispatch(), "Value", realBeneficiary);
      }

      // 20200715 Kyle : user要求每個客戶SHOW一次
      if (!lastCustNo.equals(customNo)) {
        // 是否控管-購買人
        String ctrlBuyer = mlpUtils.getBuyerCtrlYN(projectID, queryLog, customNo, customName);
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "P" + intRecordNo }, new int[1]).toDispatch(), "Value", ctrlBuyer);

        // 是否控管-實質受益人
        String ctrlRealBeneficiary = "-";
        if (!"".equals(customNo) && mlpUtils.isCusCompany(customNo)) {
          ctrlRealBeneficiary = mlpUtils.getBeneficiaryCtrlYN(projectID, queryLog, arrBeneficiary, "ctrl");
        }
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "Q" + intRecordNo }, new int[1]).toDispatch(), "Value", ctrlRealBeneficiary);

        // 是否控管-代繳人
        String ctrlDeputyer = "-";
        if (deputyLength > 0) {
          String depStatus = "";
          ctrlDeputyer = "否";
          for (int a = 0; a < deputyLength; a++) {
            System.out.println(">>> deputy key >>>" + projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim());
            // if( "Y".equals( queryLog.get( projectID + retDeputy[a][2].trim() +
            // retDeputy[a][1].trim() ) ) ) {
            if (queryLog.get(projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim()) != null) {
              ctrlDeputyer = "是";
              break;
            }
          }
        }
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "R" + intRecordNo }, new int[1]).toDispatch(), "Value", ctrlDeputyer);

        // 洗防通報1
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "S" + intRecordNo }, new int[1]).toDispatch(), "Value", "□是□否");

        // 洗防通報2
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "T" + intRecordNo }, new int[1]).toDispatch(), "Value", "□是□否");
        
        // (new I ) 是否本人繳款
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "O" + intRecordNo }, new int[1]).toDispatch(), "Value", deputyLength > 0 ? "否" : "是");
        
      }

      // 只要一次
      if (printMLP) {

        // 移走的客戶

//        // (new I ) 是否本人繳款
//        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "O" + intRecordNo }, new int[1]).toDispatch(), "Value", deputyLength > 0 ? "否" : "是");

        // //是否控管-購買人
        // String ctrlBuyer = mlpUtils.getBuyerCtrlYN( projectID , queryLog , customNo ,
        // customName );
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"P" + intRecordNo},new int[1]).toDispatch(), "Value", ctrlBuyer );

        // //是否控管-實質受益人
        // String ctrlRealBeneficiary = "-";
        // if( mlpUtils.isCusCompany(customNo) ){
        // ctrlRealBeneficiary = mlpUtils.getBeneficiaryCtrlYN( projectID , queryLog ,
        // arrBeneficiary , "ctrl" );
        // }
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"Q" + intRecordNo},new int[1]).toDispatch(), "Value", ctrlRealBeneficiary );

        // //是否控管-代繳人
        // String ctrlDeputyer = "-";
        // if( deputyLength > 0 ){
        // String depStatus = "";
        // ctrlDeputyer = "否";
        // for( int a=0 ; a < deputyLength ; a++){
        // System.out.println(">>> deputy key >>>" + projectID + retDeputy[a][2].trim()
        // + retDeputy[a][1].trim());
        // if( "Y".equals( queryLog.get( projectID + retDeputy[a][2].trim() +
        // retDeputy[a][1].trim() ) ) ) {
        // ctrlDeputyer = "是";
        // break;
        // }
        // }
        // }
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"R" + intRecordNo},new int[1]).toDispatch(), "Value", ctrlDeputyer );

        // //洗防通報1
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"S" + intRecordNo},new int[1]).toDispatch(), "Value", "□是□否" );

        // //洗防通報2
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"T" + intRecordNo},new int[1]).toDispatch(), "Value", "□是□否" );
      }

      // D 摘要代碼(品名)
      stringSQL = " SELECT PointName " + " FROM invoM010 " + " WHERE PointNo = '" + retSale05M111[intSale05M111][3] + "' ";
      String retinvoM010[][] = dbInvoice.queryFromPool(stringSQL);
      if (retinvoM010.length == 0) {
        message("[摘要代碼] 不存在!");
        // return value;
      }
      if (retinvoM010.length != 0) {
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "F" + intRecordNo }, new int[1]).toDispatch(), "Value", retinvoM010[0][0].trim());
      }

      // E 期別
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "G" + intRecordNo }, new int[1]).toDispatch(), "Value",
          retSale05M111[intSale05M111][4].trim());

      // F 期別No
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "H" + intRecordNo }, new int[1]).toDispatch(), "Value",
          retSale05M111[intSale05M111][5].trim());

      // // (new I ) 是否本人繳款
      // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
      // {"I" + intRecordNo},new int[1]).toDispatch(), "Value", deputyLength > 0 ?
      // "否":"是" );

      // G 發票號碼
      if (retSale05M111[intSale05M111][6].trim().length() != 10) {
        booleanAdd = false;
      } else {
        booleanAdd = true;
      }
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "J" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][6]);

      // H 銷售額
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "K" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][7]);
      if (booleanAdd) {
        doubleMoney += Double.parseDouble(retSale05M111[intSale05M111][7].trim());
      } else {
        doubleMoney -= Double.parseDouble(retSale05M111[intSale05M111][7].trim());
      }

      // I 稅額
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "L" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][8]);
      if (booleanAdd) {
        doubleTax += Double.parseDouble(retSale05M111[intSale05M111][8].trim());
      } else {
        doubleTax -= Double.parseDouble(retSale05M111[intSale05M111][8].trim());
      }

      // J 總計
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "M" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][9]);
      if (booleanAdd) {
        doubleTotalMoney += Double.parseDouble(retSale05M111[intSale05M111][9].trim());
      } else {
        doubleTotalMoney -= Double.parseDouble(retSale05M111[intSale05M111][9].trim());
      }

      // K 發票總類
      String stringInvoiceKind = "";
      if (retSale05M111[intSale05M111][10].equals("2"))
        stringInvoiceKind = "二聯式";
      else if (retSale05M111[intSale05M111][10].equals("3"))
        stringInvoiceKind = "三聯式";
      else if (!booleanAdd)
        stringInvoiceKind = "折讓單";
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "N" + intRecordNo }, new int[1]).toDispatch(), "Value", stringInvoiceKind);

      // 備註
      stringDiscountReason = "";
      if (!stringDoc.equals(thisDocNo)) {
        stringSQL = "SELECT  SUM(L_UsableMoney  +  H_UsableMoney) " + " FROM  Sale05M081 " + "WHERE  DocNo  =  '" + thisDocNo + "' ";
        retSale05M081 = dbSale.queryFromPool(stringSQL);
        doubleUsableMoney = doParseDouble(retSale05M081[0][0]);
        doubleUsableMoneySum += doubleUsableMoney;
        if (doubleUsableMoney > 0)
          stringDiscountReason = "暫收：" + convert.FourToFive("" + doubleUsableMoney, 0);
      }

      if (!booleanAdd) {
        stringSQL = " SELECT  DiscountReason,  H_DiscountMoney " + " FROM  Sale05M080 " + "WHERE  DocNo  =  '" + thisDocNo + "' ";
        String[][] retSale05M080 = dbSale.queryFromPool(stringSQL);
        if (retSale05M080.length > 0) {
          if (doParseDouble(retSale05M080[0][1].trim()) > 0) {
            if (!"".equals(stringDiscountReason))
              stringDiscountReason += ",";
            stringDiscountReason += "信貸折讓";
          }
          if (!"".equals(retSale05M080[0][0].trim())) {
            if (!"".equals(stringDiscountReason))
              stringDiscountReason += ",";
            stringDiscountReason += ",";
            stringDiscountReason += retSale05M080[0][0].trim();
          }
        }
        // System.out.println((intSale05M111+1)+"-------------("+stringDiscountReason+")---------------"+retSale05M080.length)
        // ;
        /*
         * Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
         * {"O" + intRecordNo},new int[1]).toDispatch(), "Value", stringDiscountReason
         * );
         */
      }
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "U" + intRecordNo }, new int[1]).toDispatch(), "Value", stringDiscountReason);
      /* Carrey 20071008 add start */
      if (retSale05M111[intSale05M111][3].equals("2102") || retSale05M111[intSale05M111][3].equals("2104")) {
        stringSQL = " select T1.COMPANY_CD from sale05m040 T40,A_COM T1 where T40.ProjectID1='" + getValue("ProjectID1").trim() + "' and T40.Position='"
            + retSale05M111[intSale05M111][1] + "' and T40.L_Com=T1.Com_No ";
        String retSale05NoteCom[][] = dbSale.queryFromPool(stringSQL);
        if (retSale05NoteCom.length > 0 && !retSale05NoteCom[0][0].equals(getValue("CompanyNo").trim()))
          stringDiscountReason += "代收";
      }

      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "U" + intRecordNo }, new int[1]).toDispatch(), "Value", stringDiscountReason);
      /* Carrey 20071008 add End */
      // Start 修改日：20090202 員工編號：B3774
      if (retSale05M111[intSale05M111][11].trim().length() > 0) {
        stringDiscountReason = stringDiscountReason.length() == 0 ? retSale05M111[intSale05M111][11].trim() : stringDiscountReason + "," + retSale05M111[intSale05M111][11].trim();
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "U" + intRecordNo }, new int[1]).toDispatch(), "Value", stringDiscountReason);
      }

      // End
      intRecordNo++;
      lastDocNo = thisDocNo;
      lastCustNo = customNo;

      // 滿頁時必須將Sheet2 Copy Sheet1
      if (intRecordNo == (intPageDataRow + intStartDataRow)) {
        CopyPage(objectSheet1, objectSheet2);
        Object objectRangeClear = Dispatch
            .invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A" + intStartDataRow + ":R" + (intStartDataRow + intPageDataRow - 1) }, new int[1]).toDispatch();
        Dispatch.call(objectRangeClear, "ClearContents");
        intRecordNo = intStartDataRow;
        intPageNo++;
      }
    } // End of Body

    // 未滿頁時必須將Sheet2 Copy Sheet1
    if (intRecordNo != intStartDataRow) {
      CopyPage(objectSheet1, objectSheet2);
      // Start 修改日期:20091208 員工編號:B3774
      // }
    } else {
      // 後面剛好沒有資料
      if (intPageNo > 1) {
        // 因為前面已把頁數+1, 實際上已剛好沒資料, 所以頁數要-1
        intPageNo = intPageNo - 1;
      }
    }
    // End 修改日期:20091208 員工編號:B3774
    // 頁尾
    int intRow = (intPageNo - 1) * intPageAllRow + 32;
    // H 銷售額
    Dispatch.put(Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "K" + intRow }, new int[1]).toDispatch(), "Value", "" + doubleMoney);
    // I 稅額
    Dispatch.put(Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "L" + intRow }, new int[1]).toDispatch(), "Value", "" + doubleTax);
    // J 總計
    Dispatch.put(Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "M" + intRow }, new int[1]).toDispatch(), "Value", "" + doubleTotalMoney);

    // 可用
    // Dispatch.put(Dispatch.invoke(objectSheet1,"Range",Dispatch.Get,new Object[]
    // {"U" + intRow},new int[1]).toDispatch(),
    // "Value",
    // "暫收：" + format.format(convert.FourToFive(""+doubleUsableMoneySum, 0),
    // "999,999,999").trim()
    // );

    // End of Body
    //
    Dispatch.call(objectSheet1, "Activate");
    objectA1 = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    Dispatch.call(objectA1, "Select");
    Dispatch.call(objectSheet1, "PrintPreview");
    // 釋放com元件
    ComThread.Release();
    return value;
  }

  // 依Client Excel Version 開啟
  public ActiveXComponent ExcelVerson() {
    ActiveXComponent Excel;
    ComThread.InitSTA();
    int intExcelVerson = 0;
    // 20130305
    try {
      Excel = new ActiveXComponent("Excel.Application.8");// Excel 97
      System.out.println("Excel 97 is OK!");
      return Excel;
    } catch (Exception Excel97) {
      try {
        Excel = new ActiveXComponent("Excel.Application.9");// Excel 2000
        System.out.println("Excel 2000 is OK!");
        return Excel;
      } catch (Exception Excel2000) {
        try {
          Excel = new ActiveXComponent("Excel.Application.10");// Excel 2002
          System.out.println("Excel 2002 is OK!");
          return Excel;
        } catch (Exception Excel2002) {
          try {
            Excel = new ActiveXComponent("Excel.Application.11");// Excel 2003
            System.out.println("Excel 2003 is OK!");
            return Excel;
          } catch (Exception Excel2003) {
            try {
              Excel = new ActiveXComponent("Excel.Application.12");// Excel 2003
              System.out.println("Excel 2007 is OK!");
              return Excel;
            } catch (Exception Excel2010) {
              try {
                Excel = new ActiveXComponent("Excel.Application.13");// Excel 2003
                System.out.println("Excel 2010 is OK!");
                return Excel;
              } catch (Exception Excel14) {
                try {
                  Excel = new ActiveXComponent("Excel.Application.14");// Excel 2003
                  System.out.println("Excel.Application.14 is OK!");
                  return Excel;
                } catch (Exception Excel15) {
                  try {
                    Excel = new ActiveXComponent("Excel.Application.15");// Excel 2003
                    System.out.println("Excel.Application.15 is OK!");
                    return Excel;
                  } catch (Exception Excel16) {
                    try {
                      Excel = new ActiveXComponent("Excel.Application.16");// Excel 2003
                      System.out.println("Excel.Application.16 is OK!");
                      return Excel;
                    } catch (Exception Excel17) {
                      try {
                        Excel = new ActiveXComponent("Excel.Application.17");// Excel 2003
                        System.out.println("Excel.Application.17 is OK!");
                        return Excel;
                      } catch (Exception Excel18) {
                        try {
                          Excel = new ActiveXComponent("Excel.Application.18");// Excel 2003
                          System.out.println("Excel.Application.18 is OK!");
                          return Excel;
                        } catch (Exception Excel19) {
                          try {
                            Excel = new ActiveXComponent("Excel.Application.19");// Excel 2003
                            System.out.println("Excel.Application.19 is OK!");
                            return Excel;
                          } catch (Exception Excel20) {
                            try {
                              Excel = new ActiveXComponent("Excel.Application.20");// Excel 2003
                              System.out.println("Excel.Application.20 is OK!");
                              return Excel;
                            } catch (Exception ExcelError) {
                              System.out.println("請使用 Excel2010 以上版本!");

                            }

                          }

                        }

                      }

                    }

                  }

                }

              }

            }

          }
        }
      }
    }
    // 20130305
    Excel = new ActiveXComponent("Excel.Application");
    System.out.println("All is OK!");
    return Excel;
  }

  // Copy Sheet2 Template to Sheet1
  public void CopyPage(Object objectSheet1, Object objectSheet2) {
    Dispatch.call(objectSheet2, "Activate");
    Object objectA1 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    Dispatch.call(objectA1, "Select");
    Object objectRow = Dispatch.invoke(objectSheet2, "Rows", Dispatch.Get, new Object[] { "1:" + intPageAllRow }, new int[1]).toDispatch();
    Dispatch.call(objectRow, "Copy");
    // Sheet1
    Dispatch.call(objectSheet1, "Activate");
    objectA1 = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "A" + ((intPageNo - 1) * intPageAllRow + 1) }, new int[1]).toDispatch();
    Dispatch.call(objectA1, "Select");
    Dispatch.call(objectSheet1, "Paste");
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

  public String getInformation() {
    return "---------------Print(列印).defaultValue()----------------";
  }
}
