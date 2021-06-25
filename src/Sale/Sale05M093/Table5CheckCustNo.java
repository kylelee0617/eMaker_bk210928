package Sale.Sale05M093;

import javax.swing.JTable;
import org.apache.commons.lang.StringUtils;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.jform.bvalidate;

public class Table5CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {

    KUtils kUtil = new KUtils();
    JTable tb5 = getTable("table5");
    int sRow = tb5.getSelectedRow();
    value = value.trim();
    String projectId = getValue("ProjectID1").trim();
    String orderNo = getValue("OrderNo");
    String orderDate = kUtil.getOrderDateByOrderNo(orderNo); // �� orderDate

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
        String recordType = "�����q�H���";
        setValueAt("table5", qName, sRow, "BenName");
        setValueAt("table5", bstatus, sRow, "IsBlackList");
        setValueAt("table5", cstatus, sRow, "IsControlList");
        setValueAt("table5", rstatus, sRow, "IsLinked");

        // �ܴ�Start
        String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                       + "query1821";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        tmpMsg = getValue("AMLText").trim();
        if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
        errMsg += tmpMsg;
        // �ܴ�END

        // �¦W�� + ���ަW��
        if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
          tmpMsg = "�����q�H" + qName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
          errMsg += tmpMsg;
        }

        // �Q���H
        if ("Y".equals(rstatus)) {
          tmpMsg += "�����q�H" + qName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
          errMsg += tmpMsg;
        }

        //���
        if (!"".equals(errMsg)) {
          messagebox(errMsg);
        }

      } else {
        message("�¦W��L����T�C");
      }
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).BenNO.field_check()----------------";
  }
}
