package Sale.Sale05M090;

import javax.swing.JTable;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.jform.bvalidate;

public class Table10CheckAgentNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    KUtils kUtil = new KUtils();
    JTable tb10 = getTable("table10");
    int sRow = tb10.getSelectedRow();
    String projectId = getValue("field1").trim();
    String orderNo = getValue("field3").trim();
    String orderDate = getValue("field2").trim();

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
        setValueAt("table10", qName, sRow, "AgentName");
        setValueAt("table10", bstatus, sRow, "IsBlackList");
        setValueAt("table10", cstatus, sRow, "IsControlList");
        setValueAt("table10", rstatus, sRow, "IsLinked");

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
          tmpMsg = "代理人" + qName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
          errMsg += tmpMsg;
        }

        // 利關人
        if ("Y".equals(rstatus)) {
          tmpMsg += "代理人" + qName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
          errMsg += tmpMsg;
        }

        //顯示
        if (!"".equals(errMsg)) {
          messagebox(errMsg);
        }

      } else {
        setValueAt("table10", "", sRow, "AgentName");
        setValueAt("table10", "", sRow, "IsBlackList");
        setValueAt("table10", "", sRow, "IsControlList");
        setValueAt("table10", "", sRow, "IsLinked");
        message("無此筆代理人資訊。");
      }
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
