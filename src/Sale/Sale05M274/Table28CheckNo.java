package Sale.Sale05M274;

import javax.swing.JTable;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.jform.bvalidate;

public class Table28CheckNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    KUtils kUtil = new KUtils();
    JTable tb28 = getTable("table28");
    int sRow = tb28.getSelectedRow();
    
    String projectId = getValue("ProjectID1").trim();
    String orderNo = "";
    String orderDate = "";
    
    String tbName = "table1";
    JTable tbOrder = this.getTable(tbName);
    if(tbOrder.getRowCount() > 0) {
      orderNo = this.getValueAt(tbName, 0, "OrderNo").toString().trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
    }
    
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
        String recordType = "客戶資料";
        setValueAt("table28", qName, sRow, "DesignatedName");
        setValueAt("table28", bstatus, sRow, "Blacklist");
        setValueAt("table28", cstatus, sRow, "Controllist");
        setValueAt("table28", rstatus, sRow, "Stakeholder");
        
        // 萊斯Start
        String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                       + "query1821";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        tmpMsg = getValue("AMLText").trim();
//        if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //同一人同一態樣只顯示一次
        errMsg += tmpMsg;
        // 萊斯END

        // 黑名單 + 控管名單
        if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
          tmpMsg = "客戶" + qName + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
//          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //同一人同一態樣只顯示一次
          errMsg += tmpMsg;
        }

        // 利關人
        if ("Y".equals(rstatus)) {
          tmpMsg += "客戶" + qName + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
//          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //同一人同一態樣只顯示一次
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
    return "---------------null(null).DesignatedId.field_check()----------------";
  }
}
