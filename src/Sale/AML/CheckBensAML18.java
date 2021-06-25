package Sale.AML;

import org.apache.commons.lang.StringUtils;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import Farglory.util.TalkBean;
import jcx.jform.bproc;

/**
 * �b�s�W�έק��x�s�e�A�ˬd�k�H������H�O�_������W��ÿ�X���G
 * 
 * @author B04391
 *
 */
public class CheckBensAML18 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println(">>> �����q�H AML18  >>> Start");
    
    KUtils kUtil = new KUtils();
    String rsMsg = "";
    String funcName = value.trim();
    String recordType = "�����q�H���";
    String projectId = "";
    String orderNo = "";
    String orderDate = "";
    String processType = "query18";
    String tbName = "";
    String comId = "";  //id column
    if(StringUtils.contains(funcName, "�ʫ��ҩ���")) {
      projectId = getValue("field1").trim();
      orderNo = getValue("field3").trim();
      orderDate = getValue("field2").trim();
      tbName = "table6";
      comId = "BCustomNo";
    }else if(StringUtils.contains(funcName, "���W")) {
      projectId = getValue("ProjectID1").trim();
      orderNo = getValue("OrderNo").trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
      tbName = "table5";
      comId = "BenNo";
    }
    
    TalkBean tBean = kUtil.getTBean();
    
    String[][] tbData = this.getTableData(tbName);
    for(int tbRow=0 ; tbRow<tbData.length ; tbRow++) {
      String id = this.getValueAt(tbName, tbRow, comId).toString().trim();
      QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, id);
      if(qBean == null) {
        System.out.println("qBean No DATA : " + id);
        continue;
      }
      String name = qBean.getName();
      String birthday = qBean.getBirthday();
      String indCode = qBean.getJobType();
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + id + "," + name + "," + birthday + "," + indCode + "," + processType;
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      rsMsg += getValue("AMLText").trim();
    }
    
    //���
    if (StringUtils.isNotBlank(rsMsg)) {
      messagebox(rsMsg);
      setValue("AMLText" , rsMsg);
    }

    System.out.println(">>> �����q�H AML18  >>> End");
    return value;
  }

  public String getInformation() {
    return "---------------Test(Test).defaultValue()----------------";
  }
}
