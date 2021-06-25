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
        String recordType = "�N�z�H���";
        setValueAt("table6", qName, sRow, "AgentName");
        setValueAt("table6", bstatus, sRow, "IsBlackList");
        setValueAt("table6", cstatus, sRow, "IsControlList");
        setValueAt("table6", rstatus, sRow, "IsLinked");

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
          tmpMsg = "�N�z�H" + qName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
          errMsg += tmpMsg;
        }

        // �Q���H
        if ("Y".equals(rstatus)) {
          tmpMsg += "�N�z�H" + qName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
          if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
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
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
