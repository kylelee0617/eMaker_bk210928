package Sale.Sale05M274;

import javax.swing.JTable;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.jform.bvalidate;

public class Table27CheckNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    KUtils kUtil = new KUtils();
    JTable tbTurst = getTable("table27");
    int tbTurstRow = tbTurst.getSelectedRow();
    String projectId = getValue("ProjectID1").trim();
    String orderNo = "";
    String orderDate = "";
    
    String tbOrderName = "table1";
    JTable tbOrder = this.getTable(tbOrderName);
    if(tbOrder.getRowCount() > 0) {
      orderNo = this.getValueAt(tbOrderName, 0, "OrderNo").toString().trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
    }

    if (!"".equals(value)) {
      String tmpMsg = "";
      String errMsg = "";
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
        setValueAt("table27", qName, tbTurstRow, "TrusteeName");
        setValueAt("table27", bstatus, tbTurstRow, "Blacklist");
        setValueAt("table27", cstatus, tbTurstRow, "Controllist");
        setValueAt("table27", rstatus, tbTurstRow, "Stakeholder");

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
          tmpMsg = "被委託人" + qName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
          errMsg += tmpMsg; 
        }

        // 利關人
        if ("Y".equals(rstatus)) {
          tmpMsg += "被委託人" + qName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
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
    return "---------------null(null).TrusteeId.field_check()----------------";
  }
}
