// 20191001 Justin 整理/新增
//20200107 Kyle 新增風險等級欄位

package Sale.Sale05R193;

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

public class Sale05R192AML extends bproc {
  talk dbSale = getTalk("Sale");
  talk dbInvoice = getTalk("" + get("put_dbInvoice"));
  talk dbFE3D = getTalk("" + get("put_dbFE3D"));
  talk dbJGENLib = getTalk("JGENLIB");
  talk dbPW0D = getTalk("pw0d");
  FargloryUtil exeUtil = new FargloryUtil();
  MLPUtils mlpUtils = new MLPUtils();
  int maxRow = 34;

  public String getDefaultValue(String value) throws Throwable {
    if (!isBatchCheckOK())
      return value;
    String[][] retSale05M080 = getSale05M080();
    if (retSale05M080.length == 0) {
      message("無收款資料 !");
      return value;
    } else {
      doExcel(retSale05M080);
    }
    return value;
  }

  public boolean isBatchCheckOK() throws Throwable {
    if (getValue("CompanyNo").length() == 0) {
      message("[公司代碼] 不可空白!");
      return false;
    }
    if (getValue("ProjectID1").length() == 0) {
      message("[案別代碼] 不可空白!");
      return false;
    }
    if (getValue("ReceiveDate").length() == 0) {
      message("收款日期] 不可空白!");
      return false;
    }
    return true;
  }

  // retSale05M080：
  public void doExcel(String[][] retSale05M080) throws Throwable {
    List mainList = processMainList(retSale05M080);
    System.out.println("mainList size>>>" + mainList.size());

    // 建立表格
    Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(8, 34, mainList.size(), 1);

    // 吃sample檔路徑
//    String stringPrintExcel = "G:\\kyleTest\\Excel\\Sale05R192_MLP.xls";
    String stringPrintExcel = "G:\\kyleTest\\Excel\\Sale05R192_AML.xls";
    // System.out.println(stringPrintExcel);

    // 建立Excel物件
    Vector retVector = exeFun.getExcelObject(stringPrintExcel);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);

    // R44 經辦
    String stringEmpName = getEmpName();
    if (!"CS".equals(getValue("CompanyNo"))) {
      exeFun.putDataIntoExcel(18, 44, stringEmpName, objectSheet1);
    }

    // A3 案別
    exeFun.putDataIntoExcel(0, 3, "案別:" + getValue("ProjectID1"), objectSheet1);

    // A4 收款彙總編號
    String stringTemp = getValue("ReceiveNo").trim();
    stringTemp = exeUtil.doSubstring(stringTemp, 2, stringTemp.length());
    exeFun.putDataIntoExcel(0, 4, "收款彙總編號:" + stringTemp, objectSheet1);

    // H4 民國年
    String stringDate = convert
        .FormatedDate(convert.ac2roc((getValue("ReceiveDate").substring(0, 4) + getValue("ReceiveDate").substring(5, 7) + getValue("ReceiveDate").substring(8, 10))), "年月日");
    exeFun.putDataIntoExcel(9, 4, stringDate, objectSheet1);

    // A1 公司名
    String stringCompanyNo = getValue("CompanyNo").trim();
    exeFun.putDataIntoExcel(0, 1, getCompanyName(stringCompanyNo), objectSheet1);

    // Q5 日期時間
    exeFun.putDataIntoExcel(19, 4, getToday("yy/mm/dd") + " " + getTime("h:m:s"), objectSheet1);

    // Start of Body 資料本體
    for (int intRow = 0; intRow < mainList.size(); intRow++) {
      String[] thisRow = (String[]) mainList.get(intRow);
      int recordNo = intRow + exeFun.getStartDataRow();

      // TODO: 是否需要計算從哪邊開始切到第二頁(暫時不用)

      for (int intCon = 1; intCon <= 20; intCon++) {
        exeFun.putDataIntoExcel(intCon, recordNo, thisRow[intCon - 1], objectSheet1);
      }
    }
    // End of Body

    exeFun.getReleaseExcelObject(retVector);
    return;
  }

  /**
   * 組成主要列表 List 0. A收款編號 1. C客戶-戶別 2. D客戶-姓名 3. E風險等級 4. F交易-品名 5. G交易-期別 6. H金額
   * 7. I現金 8. J信用卡 9. K銀行存款 10. L票據金額 11. M是否本人繳款 12. N代繳人姓名 13. O與購買人關係 14.
   * P是否控管-購買人 15. Q是否控管-實質受益人(購買人為法人) 16. R是否控管-代繳人 17. S符合疑似洗錢表徵態樣 18. T洗防內部申報
   * 19. U大額通報
   */
  public List processMainList(String[][] m080) throws Throwable {
    List mainList = new ArrayList();
    Map queryLog = mlpUtils.getQueryLog();
    String[][] retDeputy = null; // 代繳人BY單號
    int deputyLength = 0; // 代繳人總數
    int deputyReal = 0; // 代繳人目前計數
    List thisOrderNos = null; // 對應收款單號的訂單編號
    String projectID = getValue("ProjectID1").trim();

    // System.out.println(">>> map qLog >>>" + queryLog.size() );
    // for (Iterator it = queryLog.entrySet().iterator(); it.hasNext();) {
    // Map.Entry mapEntry = (Map.Entry) it.next();
    // System.out.println("The key is: " + mapEntry.getKey() + ",value is :" +
    // mapEntry.getValue());
    // }

    String lastDocNo = "";
    for (int row080 = 0; row080 < m080.length; row080++) {
      String[] newRow = new String[20];
      String[] thisRow = m080[row080];
      String position = thisRow[1].trim(); // 棟樓別
      String moneyH = thisRow[4].trim(); // 房屋款金額
      String moneyL = thisRow[5].trim(); // 土地款金額

      // 1.戶別
      newRow[1] = thisRow[1].trim();

      // 5. 期別
      newRow[5] = thisRow[3].trim();

      // 2.姓名 & 3.風險等級
      // 必須先取得姓名及ID，後面會用到
      String customName = "";
      String customNo = "";
      String riskValue = "";
      String retSale05M086[][] = getSale05M086(thisRow);
      if (retSale05M086.length > 0) {
        String retSale05M084[][] = getSale05M084(thisRow, retSale05M086);
        if (retSale05M084.length > 0) {
          String lastCustomNo = "";
          if (retSale05M086.length > 0 && retSale05M084.length > 0) {
            for (int intSale05M084 = 0; intSale05M084 < retSale05M084.length; intSale05M084++) {
              String thisCustomNo = retSale05M084[intSale05M084][1];
              if(lastCustomNo.equals(thisCustomNo)) {
                continue;
              }
              
              if (intSale05M084 != 0) {
                customName += "\n";
                customNo += "\n";
                riskValue += "\n";
              }
              customName += retSale05M084[intSale05M084][0];
              customNo += retSale05M084[intSale05M084][1];
              riskValue += retSale05M084[intSale05M084][2].trim();
              
              lastCustomNo = thisCustomNo;
            }
          }
        } else if (retSale05M084.length == 0) {
          message("[客戶代碼] 不存在!");
          System.out.println("084 Fail");
          ComThread.Release();
          continue;
        }
      } else if (retSale05M086.length == 0) {
        message("[客戶代碼] 不存在!");
        System.out.println("086 Fail");
        ComThread.Release();
        continue;
      }
      newRow[2] = "".equals(customName) ? "" : customName;
      newRow[3] = riskValue;
      // System.out.println(">>>id>>>" + customNo );

      // 只要一次
      String thisDocNo = thisRow[0].trim(); // this收款單編號
      if (!thisDocNo.equals(lastDocNo)) { // 跟上面是不是同一筆單號
        // 取得訂單編號
        thisOrderNos = mlpUtils.getOrderNo(thisDocNo);
        // System.out.println(">>> orderNo Size >>>" + thisOrderNos.size());

        // 第一次進入此單號載入代繳人，減少對資料庫loading
        retDeputy = new String[0][0];
        retDeputy = mlpUtils.getDeputy(thisDocNo);
        deputyLength = retDeputy.length;
        deputyReal = 0;
        // System.out.println(">>>retDeputyLength>>>" + deputyLength);

        // 0.收款單編號
        newRow[0] = exeUtil.doSubstring(thisDocNo, thisDocNo.length() - 3, thisDocNo.length());
        // 7.現金
        newRow[7] = thisRow[7].trim();
        // 8.信用卡
        newRow[8] = thisRow[8].trim();
        // 9.銀行存款
        newRow[9] = thisRow[9].trim();
        // 10.票據金額
        newRow[10] = "".equals(thisRow[10].trim()) ? "0" : thisRow[10].trim();

        // 12. 代繳人姓名
        // 13. 與購買人關係
        String row12 = "-";
        String row13 = "-";
        for (int i = 0; i < deputyLength; i++) {
          if (i == 0) {
            row12 = "";
            row13 = "";
          } else {
            row12 += " / ";
            row13 += " / ";
          }
          row12 += retDeputy[i][1].trim();
          row13 += retDeputy[i][3].trim();
        }
        newRow[12] = row12;
        newRow[13] = row13;

        // 11. 是否本人繳款
        newRow[11] = deputyLength > 0 ? "否" : "是";

        // 14. 是否控管 - 購買人
        newRow[14] = mlpUtils.getBuyerCtrlYN(projectID, queryLog, customNo, customName);

        // 15. 是否控管 - 實質受益人(若購買人為法人)
        String realBeneficiary = "-";
        String[][] arrBeneficiary = mlpUtils.getCtrlBeneficiary(thisOrderNos, customNo);
        if (mlpUtils.isCusCompany(customNo)) {
          String tmpBen = mlpUtils.getBeneficiaryCtrlYN(projectID, queryLog, arrBeneficiary, "ctrl");
          if (!"".equals(tmpBen))
            realBeneficiary = tmpBen;
        }
        newRow[15] = realBeneficiary;

        // 16. 是否控管 - 代繳人
        String ctrlDeputyer = "-";
        if (deputyLength > 0) {
          String depStatus = "";
          ctrlDeputyer = "否";
          for (int a = 0; a < deputyLength; a++) {
            System.out.println(">>> deputy key >>>" + projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim());
            if ("Y".equals(queryLog.get(projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim()))) {
              ctrlDeputyer = "是";
              break;
            }
          }
        }
        newRow[16] = ctrlDeputyer;

        // 17. 符合疑似洗錢
        int mayWM = Integer.parseInt(thisRow[18].trim());
        newRow[17] = mayWM > 0 ? "是" : "否";

        // 18. 洗防內部申報
        newRow[18] = "□是□否□不適用";

        // 19. 大額通報
        newRow[19] = "□是□否□不適用";
      }

      // 4.品名 (除了house car 外還要判斷是否土地款)
      String houseCar = "--";
      String houseCarMode = ""; // H建物 D土地
      if ("House".equals(thisRow[2].trim())) {
        if (Double.parseDouble(moneyH) > 0) {
          houseCar = "房屋款";
          houseCarMode = "H";
        } else {
          houseCar = "土地款";
          houseCarMode = "D";
        }
        // 同時有的狀況稍後處理
      } else if ("Car".equals(thisRow[2].trim())) {
        if (Double.parseDouble(moneyH) > 0) {
          houseCar = "車位款-建物";
          houseCarMode = "H";
        } else {
          houseCar = "車位款-土地";
          houseCarMode = "D";
        }
        // 同時有的狀況稍後處理
      }
      newRow[4] = houseCar;

      // 6. 金額
      String priceHDC = "";
      if ("H".equals(houseCarMode)) {
        priceHDC = moneyH;
      } else if ("D".equals(houseCarMode)) {
        priceHDC = moneyL;
      }
      newRow[6] = priceHDC;

      // 12. 代繳人姓名
      // 13. 與購買人關係
      // 代繳人筆數>0 且 現在指標 > 總筆數
      /*
       * if( deputyLength > 0 && deputyReal < deputyLength ){ newRow[12] =
       * retDeputy[deputyReal][1].trim(); newRow[13] =
       * retDeputy[deputyReal][3].trim(); deputyReal++; }
       */

      // 新增至主列表
      mainList.add(newRow);

      // 特殊處理 : 若同時有土地款，則需要新增一筆
      if ("H".equals(houseCarMode) && Double.parseDouble(moneyL) > 0) {
        String[] newRow2 = new String[20];
        newRow2[1] = thisRow[1].trim();
        newRow2[2] = customName;
        newRow2[3] = riskValue;
        if ("House".equals(thisRow[2].trim())) {
          newRow2[4] = "土地款";
        } else if ("Car".equals(thisRow[2].trim())) {
          newRow2[4] = "車位款-土地";
        }
        newRow2[5] = thisRow[3].trim();
        newRow2[6] = moneyL;

        // ↓代繳人記得也要處理
        // 12. 代繳人姓名
        // 13. 與購買人關係
        // 代繳人筆數>0 且 現在指標 > 總筆數
        /*
         * 20200130 - user表示代繳人放同一欄 if( deputyLength > 0 && deputyReal < deputyLength){
         * newRow[12] = retDeputy[deputyReal][1].trim(); newRow[13] =
         * retDeputy[deputyReal][3].trim(); deputyReal++; }
         */

        mainList.add(newRow2);
      }

      // 特殊處理 趴兔 : 若代繳人為多筆且本單號要結束卻列不完時...
      /*
       * 20200130 - user表示代繳人放同一欄 String nextRowDocNo = row080+1 >= m080.length ?
       * "noNextRow":m080[row080+1][0].trim(); if( !thisDocNo.equals( nextRowDocNo )
       * ){ if( deputyLength > 0 && (deputyLength - deputyReal) > 0 ){ for(int
       * i=deputyReal ; i<deputyLength ; i++){ String[] newRow3 = new String[20];
       * newRow[12] = retDeputy[deputyReal][1].trim(); newRow[13] =
       * retDeputy[deputyReal][3].trim(); mainList.add( newRow3 ); } } }
       */

      // 為下一筆紀錄本筆收款單號
      lastDocNo = thisDocNo;
    }

    return mainList;
  }

  public String[][] getSale05M080() throws Throwable {
    String qreceiveNo = "";
    if (getValue("ReceiveNo").length() > 0) {
      qreceiveNo = " where ReceiveNo = '" + getValue("ReceiveNo") + "'";
    }

    String sql = "SELECT " + "Sale05M080.DocNo " + ",Sale05M081.Position " + ",Sale05M081.HouseCar "
        + ", (SELECT ITEMLS_CHINESE FROM Sale05M052 WHERE ITEM_CD = 'Z01' AND Sale05M052.ITEMLS_CD = Sale05M081.ITEMLS_CD ) as C_ITEMLS_CD "
        + ",(Sale05M081.H_ReceiveMoney * 10000) as H_ReceiveMoney " + ", (Sale05M081.L_ReceiveMoney* 10000) as L_ReceiveMoney "
        + ", ISNULL(Sale05M081.L_ReceiveMoney_Other * 10000, 0) L_ReceiveMoney_Other " + ",Sale05M080.CashMoney " + ",Sale05M080.CreditCardMoney "
        + ",Sale05M080.BankMoney, (select sum(CheckMoney) from Sale05M082 where Sale05M082.DocNo = Sale05M080.DocNo) as billMoney " + ",Sale05M080.B_STATUS "
        + ",Sale05M080.C_STATUS " + ",Sale05M080.R_STATUS " + ",Sale05M080.PaymentDeputy " + ",Sale05M080.DeputyName " + ",Sale05M080.DeputyRelationship " + ",Sale05M080.DeputyID "
        + ",(select count(*) from Sale05M070 where Sale05M070.DocNo = Sale05M080.DocNo and RecordDesc != '不適用' and RecordDesc != '不符合' and RecordDesc not like '%低風險%' and RecordDesc not like '%中風險%') as cot70 "
        + "FROM Sale05M080 ,Sale05M081 " + "WHERE Sale05M080.DocNo = Sale05M081.DocNo " + "AND Sale05M080.DocNo in (SELECT DocNo FROM SALE05M193 " + qreceiveNo + " ) ";

    if (getValue("CompanyNo").length() > 0) {
      sql += "AND Sale05M080.CompanyNo = '" + getValue("CompanyNo") + "' ";
    }
    if (getValue("ProjectID1").length() > 0) {
      sql += "AND Sale05M080.ProjectID1 = '" + getValue("ProjectID1") + "' ";
    }
    if (getValue("ReceiveDate").length() > 0) {
      sql += "AND Sale05M080.EDate = '" + getValue("ReceiveDate") + "' ";
    }

    sql += "ORDER BY Sale05M080.DocNo,HouseCar DESC,Sale05M081.Position,ORDER_NO";

    String retSale05M080[][] = dbSale.queryFromPool(sql);
    return retSale05M080;
  }

  // 取得公司名稱
  public String getCompanyName(String stringCompanyCD) throws Throwable {
    String stringSql = "";
    String stringCompanyName = "";
    String[][] retFED1023 = null;
    //
    stringSql = "SELECT  Company_Name  FROM  FED1023  WHERE  Company_CD  =  '" + stringCompanyCD + "' ";
    retFED1023 = dbInvoice.queryFromPool(stringSql);
    if (retFED1023.length != 0) {
      stringCompanyName = retFED1023[0][0].trim();
    }
    return stringCompanyName;
  }

  // 取得經辦
  public String getEmpName() throws Throwable {
    String stringEmpName = getUser();
    String stringSQL = "";
    //
    stringSQL = " SELECT EMP_NAME " + " FROM FE3D05 " + " WHERE EMP_NO = '" + stringEmpName + "'";
    String retFE3D05[][] = dbFE3D.queryFromPool(stringSQL);
    if (retFE3D05.length != 0) {
      stringEmpName = retFE3D05[0][0].trim();
    }
    return stringEmpName;
  }

  public String[][] getSale05M084(String[] retSale05M080, String[][] retSale05M086) throws Throwable {
    String sql = "SELECT  distinct T84.CustomName , T91.CustomNo , T91.riskValue , T91.StatusCd " 
               + "FROM Sale05M084 T84 ,  Sale05M091 T91 " 
               + "WHERE DocNo = '" + retSale05M080[0] + "' "
               + "and T84.CustomNo = T91.CustomNo "
//               +"and T91.OrderNo='"+ retSale05M086[0][0] +"' and ( (ISNULL(T91.TrxDate,'')<>'' and ISNULL(T91.TrxDate,'') > '"+ getValue("ReceiveDate") +"' ) or ISNULL(T91.TrxDate,'')='' ) ";
               + "and T91.OrderNo='" + retSale05M086[0][0] + "' " + "and ( (ISNULL(T91.STatusCd,'') = 'C' and ISNULL(T91.TrxDate,'') > '" + getValue("ReceiveDate") + "')   or   ISNULL(T91.STatusCd,'') = '' ) "
               + "order by T91.CustomNo , T91.StatusCd desc "; 

    String retSale05M084[][] = dbSale.queryFromPool(sql);
    return retSale05M084;
  }

  public String[][] getSale05M086(String[] retSale05M080) throws Throwable {
    String stringSQL = " SELECT distinct T86.OrderNo " + " FROM Sale05M086 T86 ,Sale05M092 T92 ,Sale05M081 T81 " + " WHERE T86.DocNo = '" + retSale05M080[0] + "' "
        + " and T92.OrderNo=T86.OrderNo " + " and T81.DocNo=T86.DocNo " + " and T81.Position=T92.Position " + " and T92.Position= '" + retSale05M080[1] + "' "
        + " and ISNULL(T92.StatusCd,'')<>'D' ";
    String retSale05M086[][] = dbSale.queryFromPool(stringSQL);
    return retSale05M086;
  }

  public String getClientFile(String stringServerPath, FargloryUtil exeUtil) throws Throwable {
    String stringClientPath = "";
    String[] arrayTemp = convert.StringToken(stringServerPath, "/");
    //
    if (exeUtil.doSaveFile(stringServerPath, "Y")) {
      stringClientPath = "C:\\Emaker_Util\\" + arrayTemp[arrayTemp.length - 1].trim();
      return stringClientPath;
    }
    return stringServerPath;
  }

  public String getInformation() {
    return "---------------button2(列印).defaultValue()----------------";
  }
}