
/**
 * 2020-01-20 Kyle 新增關於洗防法功能
 */
package Farglory.util;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.table.*;
import jcx.util.*;
import jcx.net.smtp;
import jcx.db.talk;
import javax.mail.MessagingException;
import javax.print.*;
// 剪貼簿
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.*;

public class MLPUtils extends bvalidate {

  // DB connection
  talk dbSale = getTalk("Sale");
  talk dbPW0D = getTalk("pw0d");
  talk db400CRM = getTalk("400CRM");
  Farglory.util.FargloryUtil exeFun = new Farglory.util.FargloryUtil();

  // 是數字回傳 true，否則回傳 false。
  public boolean check(String value) throws Throwable {
    return false;
  }

  /**
   * 查詢業別中文
   */
  public Map get400PDCZPF() throws Throwable {
    String sql = "SELECT CZ02 , CZ09 FROM PDCZPF WHERE CZ02 <> '' And CZ01='INDUSTRY' ORDER BY CZ02 ";
    String[][] retQueryResult = db400CRM.queryFromPool(sql);
    Map qMap = new HashMap();
    if (retQueryResult.length > 0) {
      for (int row = 0; row < retQueryResult.length; row++) {
        String mapKey = retQueryResult[row][0].trim();
        qMap.put(mapKey, retQueryResult[row][1].trim());
      }
    }
    return qMap;
  }

  /**
   * 查詢Query_Log All for 業別
   */
  public Map getQueryLog_All() throws Throwable {
    String sql = "select Project_id , query_id , name , JOB_TYPE , C_Status from QUERY_LOG order by JOB_TYPE asc ";
    String[][] retQueryLog = dbPW0D.queryFromPool(sql);
    Map qMap = new HashMap();
    if (retQueryLog.length > 0) {
      for (int row = 0; row < retQueryLog.length; row++) {
        String mapKey = retQueryLog[row][0].trim() + retQueryLog[row][1].trim() + retQueryLog[row][2].trim();
        qMap.put(mapKey, retQueryLog[row]);
      }
    }
    return qMap;
  }

  /**
   * 查詢控管名單 條件: 控管名單 C_Status = 'Y' or B_Status=Y
   */
  public Map getQueryLog() throws Throwable {
    String sql = "select name , query_id , 'Y' , Project_id from QUERY_LOG where  C_Status = 'Y' or B_Status = 'Y' ";
    String[][] retQueryLog = dbPW0D.queryFromPool(sql);
    Map qMap = new HashMap();
    if (retQueryLog.length > 0) {
      for (int row = 0; row < retQueryLog.length; row++) {
        qMap.put(retQueryLog[row][3].trim() + retQueryLog[row][1].trim() + retQueryLog[row][0].trim(), retQueryLog[row][2].trim());
      }
    }
    return qMap;
  }

  // 查詢代繳人
  public String[][] getDeputy(String docNo) throws Throwable {
    String sql = "select PaymentDeputy , DeputyName , DeputyID , DeputyRelationship from Sale05M080 where DocNo = '" + docNo
        + "' and PaymentDeputy = 'Y' and (LEN(DeputyName)>0 or LEN(DeputyID)>0 or LEN(DeputyRelationship)>0) " + "union "
        + "select PaymentDeputy , DeputyName , DeputyID , DeputyRelationship from Sale05M082 where DocNo = '" + docNo
        + "' and PaymentDeputy = 'Y' and (LEN(DeputyName)>0 or LEN(DeputyID)>0 or LEN(DeputyRelationship)>0) " + "union "
        + "select PaymentDeputy , DeputyName , DeputyID , DeputyRelationship from Sale05M083 where DocNo = '" + docNo
        + "' and PaymentDeputy = 'Y' and (LEN(DeputyName)>0 or LEN(DeputyID)>0 or LEN(DeputyRelationship)>0) " + "union "
        + "select PaymentDeputy , DeputyName , DeputyID , DeputyRelationship from Sale05M328 where DocNo = '" + docNo
        + "' and PaymentDeputy = 'Y' and (LEN(DeputyName)>0 or LEN(DeputyID)>0 or LEN(DeputyRelationship)>0) ";

    String[][] retDeputy = dbSale.queryFromPool(sql);
    return retDeputy;
  }

  // 查詢訂單S
  public List getOrderNo(String docNo) throws Throwable {
    String sql = "select OrderNo from Sale05M086 where DocNo = '" + docNo + "' ";
    String[][] sqlResault = dbSale.queryFromPool(sql);
    List resault = new ArrayList();
    if (sqlResault.length > 0) {
      for (int i = 0; i < sqlResault.length; i++) {
        resault.add(sqlResault[i][0].trim());
      }
    }
    return resault;
  }
  
  /**
   * 取得實質受益人名單(單法人版) (先試試單法人單訂單)
   * 若目標為折讓單則傳入trxDate參數
   * 
   * @param orderNo 單一訂單
   * @param customNo  單法人ID
   * @param trxDate 若為折讓單則接收折讓日期
   * @return
   * @throws Throwable
   */
  public String[][] getCtrlBeneficiary(List orderNo, String customNo, String trxDate) throws Throwable {
    String sql = "select BCustomNo , BenName  from Sale05M091ben T91 " 
               + "where OrderNo = '" + orderNo.get(0) + "' and CustomNo = '" + customNo + "' ";
    if(trxDate.length() == 0) {
      sql += "and ISNULL(trxDate, '') = '' ";
    }else {
      sql += "and ISNULL(trxDate, '') = '" + trxDate + "' ";
    }
    String[][] sqlRS = dbSale.queryFromPool(sql);

    return sqlRS;
  }

  // 取得實質受益人名單
  public String[][] getCtrlBeneficiary(List orderNo, String customNo) throws Throwable {
    StringBuilder sqlQ1 = new StringBuilder();
    for (int i = 0; i < orderNo.size(); i++) {
      if (i != 0) {
        sqlQ1.append(",");
      }
      sqlQ1.append("'").append(orderNo.get(i)).append("'");
    }

    StringBuilder sqlQ2_Cus = new StringBuilder();
    String[] cusNoOne = customNo.split("\n");
    for (int i = 0; i < cusNoOne.length; i++) {
      if (i != 0)
        sqlQ2_Cus.append(",");
      sqlQ2_Cus.append("'").append(cusNoOne[i]).append("'");
    }

    String sql = "select BCustomNo , BenName  from Sale05M091ben T91 where OrderNo in (" + sqlQ1.toString().trim() + ") and CustomNo in (" + sqlQ2_Cus.toString().trim()
        + ")  and ISNULL(StatusCd, '') != 'C'  ";
    String[][] sqlRS = dbSale.queryFromPool(sql);

    return sqlRS;
  }

  // 實質受益人是否有符合控管名單
  public String getBeneficiaryCtrlYN(String projectID, Map queryLog, String[][] arrBeneficiary, String mode) throws Throwable {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < arrBeneficiary.length; i++) {
      String thisMapKey = projectID + arrBeneficiary[i][0].trim() + arrBeneficiary[i][1].trim();
      System.out.println(">>> ben key>>>" + thisMapKey);
      if (i != 0) {
        sb.append("\n");
      }
      if ("list".equals(mode))
        sb.append(arrBeneficiary[i][1].trim());
      if ("ctrl".equals(mode))
        sb.append(arrBeneficiary[i][1].trim() + ":" + ("Y".equals(queryLog.get(thisMapKey)) ? "是" : "否"));
    }
    return sb.toString();
  }

  // 購買人控管名單處理
  public String getBuyerCtrlYN(String projectID, Map queryLog, String customNos, String customNames) throws Throwable {
    String[] cusNo = customNos.split("\n");
    String[] cusName = customNames.split("\n");
    StringBuilder sbRS = new StringBuilder();
    System.out.println("buyer size>>>" + cusNo.length);

    for (int i = 0; i < cusNo.length; i++) {
      if (i > 0)
        sbRS.append("\n");
      String thisCusNo = cusNo[i];
      String thisCusName = cusName[i];
      String thisMapKey = projectID + thisCusNo + thisCusName;
      System.out.println(">>> buyer key >>>" + thisMapKey);
      if ("Y".equals(queryLog.get(thisMapKey))) {
        sbRS.append("是");
      } else {
        sbRS.append("否");
      }
    }

    return sbRS.toString();
  }

  // 判斷購買人中是否有法人
  public boolean isCusCompany(String cusNos) throws Throwable {
    System.out.println(">>> isCusCompany==> cusNos >>>" + cusNos);

    boolean boo = false;
    String[] cusNo = cusNos.split("\n");
    for (int i = 0; i < cusNo.length; i++) {
      String thisCusNo = cusNo[i].trim();
      // System.out.println(">>>檢測 ==>" + thisCusNo) ;
      String firstWord = thisCusNo.substring(0, 1);

      // System.out.println(">>>檢測1 ==>" + thisCusNo.length()) ;
      // System.out.println(">>>檢測2 ==>" + firstWord) ;

      if ((thisCusNo.length() != 10) && !firstWord.matches("[a-zA-Z]+")) {
        // System.out.println(thisCusNo + " 是法人") ;
        boo = true;
        break;
      }
    }
    return boo;
  }

  public boolean checkHasBen(String[][] customs, String[][] bCustoms) throws Throwable {
    System.out.println(">>>檢查法人是否有受益人<<<");

    for (int idx1 = 0; idx1 < customs.length; idx1++) {
      String cNo = customs[idx1][5].trim();
      String cStatus = customs[idx1][23].trim();
      if (this.isCusCompany(cNo) && !"C".equals(cStatus)) {
        // System.out.println(">>>法人!!<<<") ;
        boolean chkHas = false;
        for (int idx2 = 0; idx2 < bCustoms.length; idx2++) {
          String bcNo = bCustoms[idx2][1].trim();
          String bcStatus = bCustoms[idx2][12].trim();
          if (bcNo.equals(cNo) && !"C".equals(bcStatus)) {
            chkHas = true;
            break;
          }
        }
        if (chkHas == false) {
          messagebox("請指定法人 [" + customs[idx1][6].trim() + "]  之有效實質受益人。");
          return false; // 受益人清單中找不到對應ID
        }
      }
    }
    return true;
  }

  /**
   * 實質受益人欄位檢核 20200610 Kyle : 增加制裁名單先行檢核，檢核未過阻擋新增/修改，用amlCons是否為null控制是否檢核
   * 
   */
  public boolean checkBenColumn(String[][] bCustoms, Map amlCons) throws Throwable {
    System.out.println(">>>檢查受益人欄位<<<");

    AMLTools amlTools = null;
    if (amlCons != null) {
      // amlCons不為NULL = 需要檢核
      amlTools = new AMLTools(amlCons);
    }
    for (int idx2 = 0; idx2 < bCustoms.length; idx2++) {
      String[] ben1 = bCustoms[idx2];
      String birthDay = exeFun.getDateAC(ben1[5].trim(), "");
      String stringCustomNo = ben1[4].trim();
      String stringCustomName = ben1[3].trim();

      if ("".equals(ben1[6].trim())) {
        messagebox("請指定實質受益人 [" + stringCustomName + "]  之國別。");
        return false;
      }
      if ("".equals(ben1[7].trim())) {
        messagebox("請指定實質受益人 [" + stringCustomName + "]  之對象別。");
        return false;
      }
      if (birthDay.length() != 10) {
        messagebox("實質受益人 [" + stringCustomName + "]  日期格式需為YYYY/MM/DD");
        return false;
      }

      // 制裁名單檢核
      if (amlCons != null) {
        amlCons.put("customId", stringCustomNo);
        amlCons.put("customName", stringCustomName);
        amlCons.put("customTitle", "實質受益人");
        amlCons.put("funcName2", "實質受益人資料");
        String amlRS = amlTools.chkX181_Sanctions(amlCons);
        if (!"".equals(amlRS)) {
          messagebox(amlRS);
          return false;
        }
      }
    }

    return true;
  }

  /**
   * 交易時客戶欄位檢核 customers : id , name
   */
  public String[] checkCustomer_018_021(String[][] customers, Map amlCons) throws Throwable {
    AMLTools amlTools = null;
    String[] rsMsg = new String[2];
    String msg018 = "";
    String msg021 = "";
    for (int aa = 0; aa < customers.length; aa++) {
      String[] customer = customers[aa];
      String stringCustomNo = customer[0].toString().trim();
      String stringCustomName = customer[1].toString().trim();
      String stringOrderNo = customer[2].trim();

      System.out.println("checkUserColumn>>>" + stringCustomNo + stringCustomName);

      amlCons.put("OrderNo", stringOrderNo);
      amlTools = new AMLTools(amlCons);

      amlCons.put("customId", stringCustomNo);
      amlCons.put("customName", stringCustomName);
      amlCons.put("customTitle", "客戶");

      // GO 018
      String amlRS = amlTools.chkX181_Sanctions(amlCons);
      if (!"".equals(amlRS)) {
        msg018 += amlRS;
      }

      // GO 021
      amlRS = amlTools.chkX171_PEPS(amlCons);
      if (!"".equals(amlRS)) {
        msg021 += amlRS;
      }
    }
    rsMsg[0] = msg018;
    rsMsg[1] = msg021;

    return rsMsg;
  }

  /**
   * 由訂單取得客戶資訊
   * 
   */
  public String[][] getCustomerInfo(String orderNos) throws Throwable {
    String chkSQL = "select customNo , customName , orderNo from Sale05M091 where orderNo in(" + orderNos + ") and ISNULL(statusCd , '') != 'C' ";
    String[][] retCustomData = dbSale.queryFromPool(chkSQL);

    return retCustomData;
  }

}
