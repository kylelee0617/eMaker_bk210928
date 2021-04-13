package Sale.Sale05M093;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;

public class Table6CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    // 可自定欄位檢核條件
    // 傳入值 value 原輸入值
    KUtils kutil = new KUtils();
    talk dbSale = getTalk("Sale");
    JTable tb6 = getTable("table6");
    int s_row = tb6.getSelectedRow();
    String projectID = getValue("ProjectID1").trim();
    String errMsg = "";
    
    // 購屋證明單日期
    String orderNo = getValue("OrderNo").trim();
    String[][] order = dbSale.queryFromPool("select a.OrderNo , a.OrderDate from Sale05M090 a where a.OrderNo = '" + orderNo + "' ");
    String orderDate = order[0][0].trim();
    
    if (!"".equals(value)) {
      QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(projectID, value);
      if (qBean != null) {
        String aName = qBean.getName();
        String birthDay = qBean.getBirthday();
        String jobType = qBean.getJobType();
        String bStatus = qBean.getbStatus();
        String cStatus = qBean.getcStatus();
        String rStatus = qBean.getrStatus();
        setValueAt("table6", aName, s_row, "AgentName");
        setValueAt("table6", birthDay, s_row, "Birthday");
        setValueAt("table6", bStatus, s_row, "IsBlackList");
        setValueAt("table6", cStatus, s_row, "IsControlList");
        setValueAt("table6", rStatus, s_row, "IsLinked");
        
        // 萊斯Start
        String birth = birthDay.length() == 0 ? " " : birthDay.toString().replace("-", "");
        String ind = jobType.length() == 0 ? " " : jobType;
        String amlText = orderNo + "," + orderDate + "," + value + "," + aName + "," + birth + "," + ind + "," + "query1821";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();
        // 萊斯END
        
        if ("Y".equals(bStatus) || "Y".equals(cStatus)) {// 黑名單
          errMsg += aName + "-代理人為疑似黑名單對象，請覆核確認後，再進行後續交易。\n";
        }
        if ("Y".equals(rStatus)) {// 利關人
          errMsg += aName + " -代理人為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
        }
        if (!"".equals(errMsg)) {
          messagebox(errMsg);
        }
      } else {
        setValueAt("table6", "", s_row, "AgentName");
        setValueAt("table6", "", s_row, "IsBlackList");
        setValueAt("table6", "", s_row, "IsControlList");
        setValueAt("table6", "", s_row, "IsLinked");
        message("無此筆代理人資訊。");
      }
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
