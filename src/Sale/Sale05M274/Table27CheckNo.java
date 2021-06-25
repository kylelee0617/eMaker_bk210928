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
        String recordType = "�Ȥ���";
        setValueAt("table27", qName, tbTurstRow, "TrusteeName");
        setValueAt("table27", bstatus, tbTurstRow, "Blacklist");
        setValueAt("table27", cstatus, tbTurstRow, "Controllist");
        setValueAt("table27", rstatus, tbTurstRow, "Stakeholder");

        // �ܴ�Start
        String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                       + "query1821";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        tmpMsg = getValue("AMLText").trim();
        errMsg += tmpMsg;
        // �ܴ�END

        // �¦W�� + ���ަW��
        if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
          tmpMsg = "�Q�e�U�H" + qName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
          errMsg += tmpMsg; 
        }

        // �Q���H
        if ("Y".equals(rstatus)) {
          tmpMsg += "�Q�e�U�H" + qName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
          errMsg += tmpMsg;
        }

        //���
        if (!"".equals(errMsg)) {
          messagebox(errMsg);
        }

      } else {
        message("�¦W��t�εL����T�C");
      }
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).TrusteeId.field_check()----------------";
  }
}
