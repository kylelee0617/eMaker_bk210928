package Sale.Sale05M093;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;

public class Table2CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    // 可自定欄位檢核條件
    // 傳入值 value 原輸入值
    talk dbSale = getTalk("Sale");
    JTable jtableTable2 = getTable("table2");
    int intSelectRow = jtableTable2.getSelectedRow();
    String stringSQL = "";
    String stringNationality = "";
    String stringCustomNoNew = value.trim();

    // 購屋證明單日期
    String orderNo = getValue("OrderNo").trim();
    String[][] order = dbSale.queryFromPool("select a.OrderNo , a.OrderDate from Sale05M090 a where a.OrderNo = '" + orderNo + "' ");
    String orderDate = order[0][0].trim();

    if (intSelectRow == -1)
      return true;
    stringNationality = ("" + getValueAt("table2", intSelectRow, "Nationality")).trim();
    if ("1".equals(stringNationality)) {
      // 本國人
      if (stringCustomNoNew.length() != 8 && stringCustomNoNew.length() != 10) {
        message("[統編/身分證號] 長度錯誤!");
        return false;
      }
      if (stringCustomNoNew.length() == 8 && check.isCoId(stringCustomNoNew) == false) {
        message("[統編/身分證號] 統一編號錯誤!");
        return false;
      }
      if (stringCustomNoNew.length() == 10 && check.isID(stringCustomNoNew) == false) {
        message("[統編/身分證號] 身分證號錯誤!");
        return false;
      }
      if (stringCustomNoNew.length() == 8 && check.isCoId(stringCustomNoNew) == true) {
        JTabbedPane jtp2 = getTabbedPane("tab2");
        // jtp2.setEnabledAt(1, true);
      }
    } else {
      // 外國人
      if ("".equals(stringCustomNoNew)) {
        setValueAt("table2", "A", intSelectRow, "auditorship"); // 身份
        setValueAt("table2", "", intSelectRow, "CountryName"); // 國別
        setValueAt("table2", "", intSelectRow, "CustomNoNew"); // 訂戶姓名
        // setValueAt("table2", "", intSelectRow, "Percentage"); // 比例 %
        setValueAt("table2", "", intSelectRow, "Birthday"); // 生日/註冊日
        setValueAt("table2", "", intSelectRow, "MajorName"); // 業別
        setValueAt("table2", "", intSelectRow, "PositionName"); // 職位
        setValueAt("table2", "", intSelectRow, "ZIP"); // 郵遞區號
        setValueAt("table2", "", intSelectRow, "City"); // 縣市
        setValueAt("table2", "", intSelectRow, "Town"); // 鄉鎮
        setValueAt("table2", "", intSelectRow, "Address"); // 地址
        setValueAt("table2", "", intSelectRow, "Cellphone"); // 行動電話
        setValueAt("table2", "", intSelectRow, "Tel"); // 電話
        setValueAt("table2", "", intSelectRow, "Tel2"); // 電話2
        setValueAt("table2", "", intSelectRow, "eMail"); // e-Mail
        setValueAt("table2", "", intSelectRow, "IsBlackList"); // 黑名單
        setValueAt("table2", "", intSelectRow, "IsControlList"); // 控管名單
        setValueAt("table2", "", intSelectRow, "IsLinked"); // 利害關係人

        return true;
      }
    }
    // 0 auditorship 1 Nationality 2 CountryName 3 CustomName 4 Percentage
    // 5 Birthday 6 MajorName 7 PositionName 8 ZIP 9 City
    // 10 Town 11 Address 12 Cellphone 13 Tel 14 Tel2
    // 15 eMail 16 IsBlackList 17 IsControlList 18 IsLinked
    stringSQL = " SELECT TOP 1 auditorship, Nationality, CountryName, CustomName, Percentage, Birthday,MajorName, PositionName,ZIP,City,Town,Address,Cellphone,Tel,Tel2,eMail,IsBlackList, IsControlList, IsLinked "
        + " FROM Sale05M091 " + " WHERE CustomNo = '" + stringCustomNoNew + "' " + " ORDER BY OrderNo DESC ";
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    if (retSale05M091.length <= 0) {
      setValueAt("table2", "A", intSelectRow, "auditorship"); // 身份
      setValueAt("table2", "", intSelectRow, "CountryName"); // 國別
      setValueAt("table2", "", intSelectRow, "CustomNoNew"); // 訂戶姓名
      // setValueAt("table2", "", intSelectRow, "Percentage"); // 比例 %
      setValueAt("table2", "", intSelectRow, "Birthday"); // 生日/註冊日
      setValueAt("table2", "", intSelectRow, "MajorName"); // 業別
      setValueAt("table2", "", intSelectRow, "PositionName"); // 職位
      setValueAt("table2", "", intSelectRow, "ZIP"); // 郵遞區號
      setValueAt("table2", "", intSelectRow, "City"); // 縣市
      setValueAt("table2", "", intSelectRow, "Town"); // 鄉鎮
      setValueAt("table2", "", intSelectRow, "Address"); // 地址
      setValueAt("table2", "", intSelectRow, "Cellphone"); // 行動電話
      setValueAt("table2", "", intSelectRow, "Tel"); // 電話
      setValueAt("table2", "", intSelectRow, "Tel2"); // 電話2
      setValueAt("table2", "", intSelectRow, "eMail"); // e-Mail
      setValueAt("table2", "", intSelectRow, "IsBlackList"); // 黑名單
      setValueAt("table2", "", intSelectRow, "IsControlList"); // 控管名單
      setValueAt("table2", "", intSelectRow, "IsLinked"); // 利害關係人
    }
    for (int intSale05M091 = 0; intSale05M091 < retSale05M091.length; intSale05M091++) {
      // System.out.println("retSale05M091[intSale05M091][0]:"+retSale05M091[intSale05M091][0]);
      setValueAt("table2", retSale05M091[intSale05M091][0].trim(), intSelectRow, "auditorship"); // 身份
      setValueAt("table2", retSale05M091[intSale05M091][1].trim(), intSelectRow, "Nationality"); // 國籍
      setValueAt("table2", retSale05M091[intSale05M091][2].trim(), intSelectRow, "CountryName"); // 國別
      setValueAt("table2", retSale05M091[intSale05M091][3].trim(), intSelectRow, "CustomNameNew"); // 訂戶姓名
      // setValueAt("table2", retSale05M091[intSale05M091][4].trim(), intSelectRow,
      // "Percentage"); //比例
      setValueAt("table2", "100", intSelectRow, "Percentage"); // 比例
      setValueAt("table2", retSale05M091[intSale05M091][5].trim(), intSelectRow, "Birthday"); // 生日/註冊日
      setValueAt("table2", retSale05M091[intSale05M091][6].trim(), intSelectRow, "MajorName"); // 業別
      setValueAt("table2", retSale05M091[intSale05M091][7].trim(), intSelectRow, "PositionName"); // 職位
      setValueAt("table2", retSale05M091[intSale05M091][8].trim(), intSelectRow, "ZIP"); // 郵遞區號
      setValueAt("table2", retSale05M091[intSale05M091][9].trim(), intSelectRow, "City"); // 縣市
      setValueAt("table2", retSale05M091[intSale05M091][10].trim(), intSelectRow, "Town"); // 鄉鎮
      setValueAt("table2", retSale05M091[intSale05M091][11].trim(), intSelectRow, "Address"); // 住址
      setValueAt("table2", retSale05M091[intSale05M091][12].trim(), intSelectRow, "Cellphone"); // 行動電話
      setValueAt("table2", retSale05M091[intSale05M091][13].trim(), intSelectRow, "Tel"); // 電話
      setValueAt("table2", retSale05M091[intSale05M091][14].trim(), intSelectRow, "Tel2"); // 電話2
      setValueAt("table2", retSale05M091[intSale05M091][15].trim(), intSelectRow, "eMail"); // EMAIL
      setValueAt("table2", retSale05M091[intSale05M091][16].trim(), intSelectRow, "IsBlackList"); // 黑名單
      setValueAt("table2", retSale05M091[intSale05M091][17].trim(), intSelectRow, "IsControlList"); // 控管名單
      setValueAt("table2", retSale05M091[intSale05M091][17].trim(), intSelectRow, "IsLinked"); // 利害關係人
    }
    talk dbBlist = getTalk("pw0d");
    String bstatus = "";
    String cstatus = "";
    String rstatus = "";
    String logName = "";
    String errSmg = "";
    String strProjectID1 = getValue("ProjectID1").trim();
    String sqlBlack = "Select TOP 1 B_STATUS, C_STATUS, R_STATUS, NAME, BIRTHDAY, JOB_TYPE FROM QUERY_LOG WHERE QUERY_ID = '" + value + "' AND PROJECT_ID = '" + strProjectID1
        + "' Order By QID Desc ";
    String retBlack[][] = dbBlist.queryFromPool(sqlBlack);
    if (retBlack.length > 0) {
      bstatus = retBlack[0][0].trim();
      cstatus = retBlack[0][1].trim();
      rstatus = retBlack[0][2].trim();
      logName = retBlack[0][3].trim();
      String birthDay = retBlack[0][4].trim().replace("/", "-");
      String jobType = retBlack[0][5].trim();

      setValueAt("table2", logName, intSelectRow, "CustomNameNew");
      setValueAt("table2", bstatus, intSelectRow, "IsBlackList");
      setValueAt("table2", cstatus, intSelectRow, "IsControlList");
      setValueAt("table2", rstatus, intSelectRow, "IsLinked");
      setValueAt("table2", birthDay, intSelectRow, "BirthDay");

      // 萊斯Start
      String birth = birthDay.length() == 0 ? " " : birthDay.toString().replace("-", "");
      String ind = jobType.length() == 0 ? " " : jobType;
      String amlText = orderNo + "," + orderDate + "," + value + "," + logName + "," + birth + "," + ind + "," + "query1821";
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      errSmg += getValue("AMLText").trim();
      // 萊斯END

      // 黑名單
      // 控管名單
      if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
        if ("".equals(errSmg)) {
          errSmg = "客戶" + logName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
        } else {
          errSmg = errSmg + "\n客戶" + logName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
        }
      }
      if ("Y".equals(rstatus)) {// 利關人
        if ("".equals(errSmg)) {
          errSmg = "客戶" + logName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
        } else {
          errSmg = errSmg + "\n客戶" + logName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
        }
      }
      if (!"".equals(errSmg)) {
        messagebox(errSmg);
      }
    } else {
      messagebox("無此客戶資訊。");
      message("無此客戶資訊。");
    }
    message("");
    return true;
  }

  public String getInformation() {
    return "---------------null(null).CustomNoNew.field_check()----------------";
  }
}
