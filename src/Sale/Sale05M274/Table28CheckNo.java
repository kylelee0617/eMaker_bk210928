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
        String recordType = "�Ȥ���";
        setValueAt("table28", qName, sRow, "DesignatedName");
        setValueAt("table28", bstatus, sRow, "Blacklist");
        setValueAt("table28", cstatus, sRow, "Controllist");
        setValueAt("table28", rstatus, sRow, "Stakeholder");
        
        // �ܴ�Start
        String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                       + "query1821";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        tmpMsg = getValue("AMLText").trim();
//        if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
        errMsg += tmpMsg;
        // �ܴ�END

        // �¦W�� + ���ަW��
        if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
          tmpMsg = "�Ȥ�" + qName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
//          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
          errMsg += tmpMsg;
        }

        // �Q���H
        if ("Y".equals(rstatus)) {
          tmpMsg += "�Ȥ�" + qName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
//          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
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
    return "---------------null(null).DesignatedId.field_check()----------------";
  }
}
