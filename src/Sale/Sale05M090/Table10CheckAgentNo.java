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
        String recordType = "�N�z�H���";
        setValueAt("table10", qName, sRow, "AgentName");
        setValueAt("table10", bstatus, sRow, "IsBlackList");
        setValueAt("table10", cstatus, sRow, "IsControlList");
        setValueAt("table10", rstatus, sRow, "IsLinked");

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
          tmpMsg = "�N�z�H" + qName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
          errMsg += tmpMsg;
        }

        // �Q���H
        if ("Y".equals(rstatus)) {
          tmpMsg += "�N�z�H" + qName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
          errMsg += tmpMsg;
        }

        //���
        if (!"".equals(errMsg)) {
          messagebox(errMsg);
        }

      } else {
        setValueAt("table10", "", sRow, "AgentName");
        setValueAt("table10", "", sRow, "IsBlackList");
        setValueAt("table10", "", sRow, "IsControlList");
        setValueAt("table10", "", sRow, "IsLinked");
        message("�L�����N�z�H��T�C");
      }
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
