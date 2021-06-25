package Sale.AML;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import Farglory.aml.AMLTools_Lyods;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCustomBean;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import Farglory.util.Result;
import Farglory.util.TalkBean;
import jcx.jform.sproc;

public class AML extends sproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println("==============�~�����v�ˮ�LOG START====================================");
    
    //config
    Map config = (HashMap) get("config");
    boolean isTest = "PROD".equals(config.get("serverType").toString()) ? false : true;
    String lyodsSoapURL = config.get("lyodsSoapURL").toString();
    
    TalkBean tBean = new TalkBean();
    tBean.setDb400CRM(getTalk("400CRM"));
    tBean.setDbPw0D(getTalk("pw0d"));
    tBean.setDbSale(getTalk("Sale"));
    tBean.setDbEIP(getTalk("EIP"));

//  KUtils kUtil = new KUtils();
    KUtils kUtil = new KUtils(tBean);
    Result rs = new Result();
    String stringSQL = "";
    String errMsg = "";
    String funcName = value.trim();
    String recordType = "";
    String actionName = getValue("actionText").trim();
    String projectId = "";
    String orderNo = "";
    String orderDate = "";
    if(StringUtils.contains(funcName, "�ʫ��ҩ���")) {
      projectId = getValue("field1").trim();
      orderNo = getValue("field3").trim();
      orderDate = getValue("field2").trim();
    }else if(StringUtils.contains(funcName, "���W")) {
      projectId = getValue("ProjectID1").trim();
      orderNo = getValue("OrderNo").trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
    }
    
    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setDb400CRM(getTalk("400CRM"));
    aBean.setDbPw0D(getTalk("pw0d"));
    aBean.setDbSale(tBean.getDbSale());
    aBean.setDbEIP(getTalk("EIP"));
    aBean.settBean(tBean);
    aBean.setProjectID1(projectId);
    aBean.setOrderNo(orderNo);
    aBean.setOrderDate(orderDate);
    aBean.setFunc(funcName);
    aBean.setRecordType(recordType);
    aBean.setActionName(actionName);
    aBean.setEmakerUserNo(getUser());
    aBean.setTestServer(isTest);
    aBean.setLyodsSoapURL(lyodsSoapURL);
    
    
    System.out.println("==============�Ȥ���==============");
    stringSQL = "SELECT CustomNo, CustomName, CountryName, IsBlackList, IsControlList, IsLinked FROM Sale05M091 WHERE ORDERNO = '" + orderNo + "' "
        + "and ISNULL(StatusCd , '') != 'C' ";
    String[][] custData = tBean.getDbSale().queryFromPool(stringSQL);
    if (custData.length > 0) {
      aBean.setRecordType("�Ȥ���");
      AMLTools_Lyods aml = new AMLTools_Lyods(aBean);
      
      for (int m = 0; m < custData.length; m++) {
        String custNo = custData[m][0].trim();
        String custName = custData[m][1].trim();
        QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);
        
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustTitle("�Ȥ�");
        cBean.setCustomNo(custNo);      //�����Ҧr��
        cBean.setCustomName(custName);    //�m�W
        cBean.setBirthday(qBean.getBirthday());  //�ͤ�
        cBean.setIndustryCode(qBean.getJobType());  //�~�O
        cBean.setCountryName(custData[m][2].trim());  //��W
        cBean.setbStatus(custData[m][3].trim());      //�¦W��
        cBean.setcStatus(custData[m][4].trim());      //���ަW��
        cBean.setrStatus(custData[m][5].trim());      //�Q�`���Y�H

        // ���A��LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        aml.insNotUse(noUseAML, cBean);

        //18. ����W��
        errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
        
        //21. PEPS
        errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
        
        // 9. �ꮣ�a��
        errMsg += (aml.chkAML009(cBean).getData()).toString().trim();
        
        // 17.�¦W��&���ަW��
        errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
        
        // 19.�Q�`���Y�H
        errMsg += (aml.chkAML019(cBean).getData()).toString().trim();
        
      } // for end
    }
    System.out.println("============================");
    
    
    System.out.println("==============�����q�H==============");
    stringSQL = "select  BCustomNo, BenName , CountryName, IsBlackList, IsControlList, IsLinked, "
              + "CustomNo, (select top 1 CustomName from sale05m091 b where a.OrderNo=b.OrderNo and a.CustomNo=b.CustomNo and ISNULL(b.StatusCd, '') != 'C') "  
              + "from Sale05M091Ben a "
              + "where OrderNo = '" + orderNo + "' and ISNULL(a.StatusCd, '') != 'C' ";
    String[][] benData = tBean.getDbSale().queryFromPool(stringSQL);
    if (benData.length > 0) {
      aBean.setRecordType("�����q�H���");
      AMLTools_Lyods aml = new AMLTools_Lyods(aBean);
      
      for (int m = 0; m < benData.length; m++) {
        String custNo = benData[m][0].trim();
        QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);
        
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustTitle("�����q�H");
        cBean.setCustomNo(benData[m][0].trim());      //�����Ҧr��
        cBean.setCustomName(benData[m][1].trim());    //�m�W
        cBean.setBirthday(qBean.getBirthday());  //�ͤ�
        cBean.setIndustryCode(qBean.getJobType());  //�~�O
        cBean.setCountryName(benData[m][2].trim());  //��W
        cBean.setbStatus(benData[m][3].trim());      //�¦W��
        cBean.setcStatus(benData[m][4].trim());      //���ަW��
        cBean.setrStatus(benData[m][5].trim());      //�Q�`���Y�H
        cBean.setCustTitle2("�Ȥ�");
        cBean.setCustomName2(benData[m][7].trim());
        
        // ���A��LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        aml.insNotUse(noUseAML, cBean);

        //18. ����W��
        errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
        
        //21. PEPS
        errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
        
        // 9. �ꮣ�a��
        errMsg += (aml.chkAML009(cBean).getData()).toString().trim();
        
        // 17.�¦W��&���ަW��
        errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
        
        // 19.�Q�`���Y�H
        errMsg += (aml.chkAML019(cBean).getData()).toString().trim();

      } // for end
    } // �����q�H END
    System.out.println("============================");

    System.out.println("==============�N�z�H==============");
    stringSQL = "select a.ACustomNo , a.AgentName, a.CountryName, a.IsBlackList, a.IsControlList, a.IsLinked, a.AgentRel, a.CustomNo, " 
              + "(SELECT TOP 1 CustomName FROM SALE05M091 b WHERE b.CustomNo = a.CustomNo AND b.OrderNo= a.OrderNo ) as custName " 
              + "from Sale05M091Agent a where ISNULL(a.StatusCd, '') != 'C' and a.orderNo = '" + orderNo + "' ";
    String[][] agentData = tBean.getDbSale().queryFromPool(stringSQL);
    if (agentData.length > 0) {
      aBean.setRecordType("�N�z�H���");
      AMLTools_Lyods aml = new AMLTools_Lyods(aBean);
      
      for (int m = 0; m < agentData.length; m++) {
        String custNo = agentData[m][0].trim();
        QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);
        
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustTitle("�N�z�H");
        cBean.setCustomNo(custNo);      //�����Ҧr��
        cBean.setCustomName(agentData[m][1].trim());    //�m�W
        cBean.setBirthday(qBean.getBirthday());  //�ͤ�
        cBean.setIndustryCode(qBean.getJobType());  //�~�O
        cBean.setCountryName(agentData[m][2].trim());  //��W
        cBean.setbStatus(agentData[m][3].trim());      //�¦W��
        cBean.setcStatus(agentData[m][4].trim());      //���ަW��
        cBean.setrStatus(agentData[m][5].trim());      //�Q�`���Y�H
        cBean.setAgentRel(agentData[m][6].trim());
        cBean.setCustTitle2("�Ȥ�");
        cBean.setCustomName2(agentData[m][8].trim());  //�D�Hname
        
        // ���A��LOG1~4 ,6 ,7 ,10~16
        int[] noUseAML = { 1, 2, 3, 4, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        aml.insNotUse(noUseAML, cBean);

//        String custSA = "�Ȥ�" + agentData[m][1].trim() + "��";
//        if (m == 0) errMsg += "\n";

        //18. ����W��
        errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
        
        //21. PEPS
        errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
        
        // 9. �ꮣ�a��
        errMsg += (aml.chkAML009(cBean).getData()).toString().trim();
        
        // 17.�¦W��&���ަW��
        errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
        
        // 19.�Q�`���Y�H
        errMsg += (aml.chkAML019(cBean).getData()).toString().trim();

        // 8. �~���ĤK��(���N�z�H)
        errMsg += (aml.chkAML008(cBean).getData()).toString().trim().replaceAll("<br>", "\n");

        // 5. ���Y�D�G����ÿ�
        errMsg += (aml.chkAML005(cBean).getData()).toString().trim().replaceAll("<br>", "\n");

      } // for end
    } // �N�z�H END
    System.out.println("============================");

    // �e�XerrMsg
    if (StringUtils.isNotBlank(errMsg)) {
//      setValue("errMsgBoxText", errMsg);
      messagebox(errMsg);
//      getButton("errMsgBoxBtn").doClick();
//      getButton("sendMail").doClick();
    }
    System.out.println("==============�~�����v�ˮ�LOG END====================================");
    return value;
  }

  public String getInformation() {
    return "---------------AML(AML).defaultValue()----------------";
  }
}
