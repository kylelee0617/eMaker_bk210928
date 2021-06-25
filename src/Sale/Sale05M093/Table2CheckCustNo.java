package Sale.Sale05M093;

import javax.swing.JTable;
import org.apache.commons.lang.StringUtils;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bvalidate;
import jcx.util.check;

public class Table2CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {

    value = value.trim();
    KUtils kUtil = new KUtils();
    talk dbSale = getTalk("Sale");
    JTable tb2 = getTable("table2");
    int sRow = tb2.getSelectedRow();
    setValueAt("table2", value, sRow, "CustomNo");
    
    String stringSQL = "";
    String projectId = getValue("ProjectID1").trim();
    String orderNo = getValue("OrderNo");
    String orderDate = kUtil.getOrderDateByOrderNo(orderNo);

    if (sRow == -1) return true;
    
    String stringNationality = ("" + getValueAt("table2", sRow, "Nationality")).trim();
    if ("1".equals(stringNationality)) {
      // 本國人
      if (value.length() != 8 && value.length() != 10) {
        message("[統編/身分證號] 長度錯誤!");
        return false;
      }
      if (value.length() == 8 && check.isCoId(value) == false) {
        message("[統編/身分證號] 統一編號錯誤!");
        return false;
      }
      if (value.length() == 10 && check.isID(value) == false) {
        message("[統編/身分證號] 身分證號錯誤!");
        return false;
      }
    }
    
    //AIdent
    JTable tb10 = getTable("table10");
    int tb10Row = tb10.getSelectedRow();
    if("父母法定代理".equals(value) || "未成年".equals(value)) {
      this.setValueAt("table10", "B", tb10Row, "AIdent");
    }

    // 嘗試從sale05m091取客戶資料
    // 0 auditorship 1 Nationality 2 CountryName 3 CustomName 4 Percentage 5
    // Birthday 6 MajorName 7 PositionName 8 ZIP 9 City
    // 10 Town 11 Address 12 Cellphone 13 Tel 14 Tel2 15 eMail 16 IsBlackList 17
    // IsControlList 18 IsLinked
    stringSQL = " SELECT TOP 1 auditorship, Nationality, CountryName, CustomName, Percentage, Birthday,MajorName, PositionName,ZIP,City,Town,Address,Cellphone,Tel,Tel2,eMail,IsBlackList, IsControlList, IsLinked "
        + " FROM Sale05M091 WHERE CustomNo = '" + value + "' " + " ORDER BY OrderNo DESC ";
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    boolean ret091HasData = retSale05M091.length > 0;
    setValueAt("table2", ret091HasData ? retSale05M091[0][0].trim() : "", sRow, "auditorship"); // 身份
    setValueAt("table2", ret091HasData ? retSale05M091[0][1].trim() : "", sRow, "Nationality"); // 國籍
    setValueAt("table2", ret091HasData ? retSale05M091[0][2].trim() : "", sRow, "CountryName"); // 國別
    setValueAt("table2", ret091HasData ? retSale05M091[0][3].trim() : "", sRow, "CustomNameNew"); // 訂戶姓名
    setValueAt("table2", "100", sRow, "Percentage"); // 比例
    setValueAt("table2", ret091HasData ? retSale05M091[0][5].trim() : "", sRow, "Birthday"); // 生日/註冊日
    setValueAt("table2", ret091HasData ? retSale05M091[0][6].trim() : "", sRow, "MajorName"); // 業別
    setValueAt("table2", ret091HasData ? retSale05M091[0][7].trim() : "", sRow, "PositionName"); // 職位
    setValueAt("table2", ret091HasData ? retSale05M091[0][8].trim() : "", sRow, "ZIP"); // 郵遞區號
    setValueAt("table2", ret091HasData ? retSale05M091[0][9].trim() : "", sRow, "City"); // 縣市
    setValueAt("table2", ret091HasData ? retSale05M091[0][10].trim() : "", sRow, "Town"); // 鄉鎮
    setValueAt("table2", ret091HasData ? retSale05M091[0][11].trim() : "", sRow, "Address"); // 住址
    setValueAt("table2", ret091HasData ? retSale05M091[0][12].trim() : "", sRow, "Cellphone"); // 行動電話
    setValueAt("table2", ret091HasData ? retSale05M091[0][13].trim() : "", sRow, "Tel"); // 電話
    setValueAt("table2", ret091HasData ? retSale05M091[0][14].trim() : "", sRow, "Tel2"); // 電話2
    setValueAt("table2", ret091HasData ? retSale05M091[0][15].trim() : "", sRow, "eMail"); // EMAIL
    setValueAt("table2", ret091HasData ? retSale05M091[0][16].trim() : "", sRow, "IsBlackList"); // 黑名單
    setValueAt("table2", ret091HasData ? retSale05M091[0][17].trim() : "", sRow, "IsControlList"); // 控管名單
    setValueAt("table2", ret091HasData ? retSale05M091[0][17].trim() : "", sRow, "IsLinked"); // 利害關係人

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

      setValueAt("table2", qName, sRow, "CustomNameNew");
      setValueAt("table2", bstatus, sRow, "IsBlackList");
      setValueAt("table2", cstatus, sRow, "IsControlList");
      setValueAt("table2", rstatus, sRow, "IsLinked");

      // 萊斯Start
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                     + "query1821";
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      tmpMsg = getValue("AMLText").trim();
      if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //同一人同一態樣只顯示一次
      errMsg += tmpMsg;
      // 萊斯END

      // 黑名單 + 控管名單
      if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
        tmpMsg = "客戶" + qName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
        if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //同一人同一態樣只顯示一次
        errMsg += tmpMsg;
      }

      // 利關人
      if ("Y".equals(rstatus)) {
        tmpMsg += "客戶" + qName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
        if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //同一人同一態樣只顯示一次
        errMsg += tmpMsg;
      }

      //顯示
      if (!"".equals(errMsg)) {
        messagebox(errMsg);
      }
    } else {
      message("黑名單系統無此資訊。");
    }

    return true;
  }

  public String getInformation() {
    return "---------------null(null).CustomNoNew.field_check()----------------";
  }
}
