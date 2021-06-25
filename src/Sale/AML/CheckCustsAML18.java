package Sale.AML;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import Farglory.util.KUtils;
import Farglory.util.Result;
import jcx.db.talk;
import jcx.jform.bproc;

public class CheckCustsAML18 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println(">>>�Ȥ�table  >>> Start");
    
    KUtils kUtil = new KUtils();
    String rsMsg = "";
    String funcName = value;
    String projectId = "";
    String orderNo = "";
    String orderDate = "";
    String processType = "query18";
    
    //config
    boolean isTest = false;
    String serverType = "";
    String lyodsSoapURL = "";
    Map config = (HashMap) get("config");
    serverType = config.get("serverType").toString();
    lyodsSoapURL = config.get("lyodsSoapURL").toString();
    isTest = "PROD".equals(serverType) ? false : true;
    
    String tbName = "";
    String comId = "";  //id
    String comName = "";  //name
    String comBirthday = ""; //birthday
    String comIndCode = ""; //IndustryCode
    if(StringUtils.contains(funcName, "�ʫ��ҩ���")) {
      projectId = getValue("field1").trim();
      orderNo = getValue("field3").trim();
      orderDate = getValue("field2").trim();
      tbName = "table1";
    }else if(StringUtils.contains(funcName, "���W")) {
      projectId = getValue("ProjectID1").trim();
      orderNo = getValue("OrderNo").trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
      tbName = "table2";
    }
    
    //21-05-13 Kyle : ��s�ܴ��D�n�Ȥ�
    String[][] retCustom = this.getTableData(tbName);
    for(int tbRow=0 ; tbRow<retCustom.length ; tbRow++) {
      String id = this.getValueAt(tbName, tbRow, comId).toString().trim();
      String name = this.getValueAt(tbName, tbRow, comId).toString().trim();
      String birthday = this.getValueAt(tbName, tbRow, comId).toString().trim();
      String indCode = this.getValueAt(tbName, tbRow, comId).toString().trim();
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + "��s�D�n�Ȥ�P�L�̪����p�H" 
          + "," + id + "," + name + "," + birthday + "," + indCode + "," + processType;
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      rsMsg += getValue("AMLText").trim();
    }

    System.out.println(">>>��s���p�H>>> End");
    return value;
  }

  public String getInformation() {
    return "---------------Test(Test).defaultValue()----------------";
  }
}
