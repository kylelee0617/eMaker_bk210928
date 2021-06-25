package Sale.Sale05M090;

import javax.swing.JButton;
import javax.swing.JTable;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bvalidate;
import jcx.util.check;

/**
 * table1 - 身分證字號檢核
 * 
 * @author B04391
 *
 */

public class Table1CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    System.out.println("getFunctionName>>>" + getFunctionName());
    System.out.println("POSITION>>>" + POSITION);

    value = value.trim();
    KUtils kUtil = new KUtils();
    talk dbSale = getTalk("Sale");
    JTable tb1 = getTable("table1");
    int sRow = tb1.getSelectedRow();
    setValueAt("table1", value, sRow, "CustomNo");

    String projectId = getValue("field1").trim();
    String orderNo = getValue("field3").trim();
    String orderDate = getValue("field2").trim();
    String modCustFlag = getValue("CustomID_NAME_PER_Editable").toString().trim();

    if (POSITION == 4 && "0".equals(modCustFlag)) return false;
    
    if (sRow == -1) return false;
    
    if ("".equals(projectId)) {
      message("[案別代碼] 不可空白!");
      return false;
    }
    String stringNationality = ("" + getValueAt("table1", sRow, "Nationality")).trim();
    if ("1".equals(stringNationality)) {
      // 僅本國人限制 20090414
      if (value.length() == 0) {
        message("[統編/身分證號] 不可空白!");
        return false;
      }
      if (value.length() != 8 && value.length() != 10) {
        messagebox("[統編/身分證號] 長度錯誤!");
        return false;
      }
      if (value.length() == 8 && check.isCoId(value) == false) {
        messagebox("[統編/身分證號] 統一編號錯誤!");
        return false;
      }
      if (value.length() == 10 && check.isID(value) == false) {
        messagebox("[統編/身分證號] 身分證號錯誤!");
        return false;
      }
    }
    
    //尋找是否有同一個客戶能摳資料
    String stringSQL = "";
    stringSQL = "SELECT TOP 1 CustomName,  Address,  Tel,  eMail, Nationality, auditorship, ZIP, City, Town, Cellphone " + "FROM Sale05M091  WHERE CustomNo = '" + value + "' "
        + "ORDER BY OrderNo DESC ";
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    boolean ret091HasData = retSale05M091.length > 0;
    setValueAt("table1", ret091HasData ? retSale05M091[0][0].trim() : "", sRow, "CustomName");
    setValueAt("table1", ret091HasData ? retSale05M091[0][1].trim() : "", sRow, "Address");
    setValueAt("table1", ret091HasData ? retSale05M091[0][2].trim() : "", sRow, "Tel");
    setValueAt("table1", ret091HasData ? retSale05M091[0][3].trim() : "", sRow, "eMail");
    setValueAt("table1", ret091HasData ? retSale05M091[0][4].trim() : "", sRow, "Nationality");
    setValueAt("table1", ret091HasData ? retSale05M091[0][5].trim() : "", sRow, "auditorship");
    setValueAt("table1", ret091HasData ? retSale05M091[0][6].trim() : "", sRow, "ZIP");
    setValueAt("table1", ret091HasData ? retSale05M091[0][7].trim() : "", sRow, "City");
    setValueAt("table1", ret091HasData ? retSale05M091[0][8].trim() : "", sRow, "Town");
    setValueAt("table1", ret091HasData ? retSale05M091[0][9].trim() : "", sRow, "Cellphone");

    //偷完基本資料偷看黑名單查詢
    String tmpMsg = "";
    String errMsg = "";
    String amlRsMix = getValue("AMLRsMix").trim();
    QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, value);
    if (qBean != null) {
      String bstatus = qBean.getbStatus();
      String cstatus = qBean.getcStatus();
      String rstatus = qBean.getrStatus();
      String qName = qBean.getName();
      String birthday = qBean.getBirthday();
      String indCode = qBean.getJobType();
      String funcName = getFunctionName().trim();
      String recordType = "客戶資料";
      setValueAt("table1", qName, sRow, "CustomName");
      setValueAt("table1", bstatus, sRow, "IsBlackList");
      setValueAt("table1", cstatus, sRow, "IsControlList");
      setValueAt("table1", rstatus, sRow, "IsLinked");

      // 萊斯Start
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                     + "query1821";
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      tmpMsg = getValue("AMLText").trim();
      errMsg += tmpMsg;
      // 萊斯END

      // 黑名單 + 控管名單
      if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
        tmpMsg = "客戶" + qName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
        errMsg += tmpMsg;
      }

      // 利關人
      if ("Y".equals(rstatus)) {
        tmpMsg += "客戶" + qName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
        errMsg += tmpMsg;
      }

      //顯示
      if (!"".equals(errMsg)) {
        messagebox(errMsg);
      }

    } else {
      messagebox("黑名單系統無此資訊。");
    }
    
    //若只有一筆，給予100%
    if (tb1.getRowCount() == 1)
      setValueAt("table1", "100", sRow, "Percentage");

    // 定審
    JButton buyedInfo = getButton("BuyedInfo");
    buyedInfo.setText("userCusNo=" + value);
    buyedInfo.doClick();

    return true;
  }

  public String getInformation() {
    return "---------------null(null).CustomNo.field_check()----------------";
  }
}
