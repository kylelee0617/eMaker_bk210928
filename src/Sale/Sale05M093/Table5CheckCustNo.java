package Sale.Sale05M093;

import javax.swing.JTable;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bvalidate;

public class Table5CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    // 可自定欄位檢核條件
    // 傳入值 value 原輸入值
    KUtils kutil = new KUtils();
    talk dbSale = getTalk("Sale");
    JTable tb5 = getTable("table5");
    int s_row = tb5.getSelectedRow();
    String projectID = getValue("ProjectID1").trim();
    String errMsg = "";

    // 購屋證明單日期
    String orderNo = getValue("OrderNo").trim();
    String[][] order = dbSale.queryFromPool("select a.OrderNo , a.OrderDate from Sale05M090 a where a.OrderNo = '" + orderNo + "' ");
    String orderDate = order[0][0].trim();

    if (!"".equals(value)) {
      QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(projectID, value);
      
      if (qBean != null) {
        String benName = qBean.getName();
        String birthDay = qBean.getBirthday();
        String jobType = qBean.getJobType();
        String bStatus = qBean.getbStatus();
        String cStatus = qBean.getcStatus();
        String rStatus = qBean.getrStatus();
        setValueAt("table5", benName, s_row, "BenName");
        setValueAt("table5", birthDay, s_row, "Birthday");
        setValueAt("table5", bStatus, s_row, "IsBlackList");
        setValueAt("table5", cStatus, s_row, "IsControlList");
        setValueAt("table5", rStatus, s_row, "IsLinked");
        
        // 萊斯Start
        String birth = birthDay.length() == 0 ? " " : birthDay.toString().replace("-", "");
        String ind = jobType.length() == 0 ? " " : jobType;
        String amlText = orderNo + "," + orderDate + "," + value + "," + benName + "," + birth + "," + ind + "," + "query1821";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();
        // 萊斯END

        if ("Y".equals(bStatus) || "Y".equals(cStatus)) {
          errMsg += benName + "-實質受益人為疑似黑名單對象，請覆核確認後，再進行後續交易。\n";
        }
        
        if ("Y".equals(rStatus)) {// 利關人
          errMsg += benName + " -實質受益人為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
        }
        if (!"".equals(errMsg)) {
          messagebox(errMsg);
        }
      } else {
        setValueAt("table5", "", s_row, "BenName");
        setValueAt("table5", "", s_row, "IsBlackList");
        setValueAt("table5", "", s_row, "IsControlList");
        setValueAt("table5", "", s_row, "IsLinked");
        message("無此實質受益人資訊。");
      }
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).BenNO.field_check()----------------";
  }
}
