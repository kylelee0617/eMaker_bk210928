package Sale.Sale05M093;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;

public class Table6CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    // �i�۩w����ˮֱ���
    // �ǤJ�� value ���J��
    KUtils kutil = new KUtils();
    talk dbSale = getTalk("Sale");
    JTable tb6 = getTable("table6");
    int s_row = tb6.getSelectedRow();
    String projectID = getValue("ProjectID1").trim();
    String errMsg = "";
    
    // �ʫ��ҩ�����
    String orderNo = getValue("OrderNo").trim();
    String[][] order = dbSale.queryFromPool("select a.OrderNo , a.OrderDate from Sale05M090 a where a.OrderNo = '" + orderNo + "' ");
    String orderDate = order[0][0].trim();
    
    if (!"".equals(value)) {
      QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(projectID, value);
      if (qBean != null) {
        String aName = qBean.getName();
        String birthDay = qBean.getBirthday();
        String jobType = qBean.getJobType();
        String bStatus = qBean.getbStatus();
        String cStatus = qBean.getcStatus();
        String rStatus = qBean.getrStatus();
        setValueAt("table6", aName, s_row, "AgentName");
        setValueAt("table6", birthDay, s_row, "Birthday");
        setValueAt("table6", bStatus, s_row, "IsBlackList");
        setValueAt("table6", cStatus, s_row, "IsControlList");
        setValueAt("table6", rStatus, s_row, "IsLinked");
        
        // �ܴ�Start
        String birth = birthDay.length() == 0 ? " " : birthDay.toString().replace("-", "");
        String ind = jobType.length() == 0 ? " " : jobType;
        String amlText = orderNo + "," + orderDate + "," + value + "," + aName + "," + birth + "," + ind + "," + "query1821";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();
        // �ܴ�END
        
        if ("Y".equals(bStatus) || "Y".equals(cStatus)) {// �¦W��
          errMsg += aName + "-�N�z�H���æ��¦W���H�A���ЮֽT�{��A�A�i��������C\n";
        }
        if ("Y".equals(rStatus)) {// �Q���H
          errMsg += aName + " -�N�z�H�����q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
        }
        if (!"".equals(errMsg)) {
          messagebox(errMsg);
        }
      } else {
        setValueAt("table6", "", s_row, "AgentName");
        setValueAt("table6", "", s_row, "IsBlackList");
        setValueAt("table6", "", s_row, "IsControlList");
        setValueAt("table6", "", s_row, "IsLinked");
        message("�L�����N�z�H��T�C");
      }
    }
    return true;
  }

  public String getInformation() {
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
