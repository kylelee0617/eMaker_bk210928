package Sale.Sale05M093;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;

import org.apache.commons.lang.StringUtils;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;

public class Table6CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {

    KUtils kUtil = new KUtils();
    JTable tb6 = getTable("table6");
    int sRow = tb6.getSelectedRow();
    value = value.trim();
    String projectId = getValue("ProjectID1").trim();
    String orderNo = getValue("OrderNo");
    String orderDate = kUtil.getOrderDateByOrderNo(orderNo); // 取 orderDate

    if (!"".equals(value)) {
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
        String recordType = "代理人資料";
        setValueAt("table6", qName, sRow, "AgentName");
        setValueAt("table6", bstatus, sRow, "IsBlackList");
        setValueAt("table6", cstatus, sRow, "IsControlList");
        setValueAt("table6", rstatus, sRow, "IsLinked");

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
          tmpMsg = "代理人" + qName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //同一人同一態樣只顯示一次
          errMsg += tmpMsg;
        }

        // 利關人
        if ("Y".equals(rstatus)) {
          tmpMsg += "代理人" + qName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
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
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
