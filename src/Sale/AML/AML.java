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
    System.out.println("==============洗錢防治檢核LOG START====================================");
    
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
    if(StringUtils.contains(funcName, "購屋證明單")) {
      projectId = getValue("field1").trim();
      orderNo = getValue("field3").trim();
      orderDate = getValue("field2").trim();
    }else if(StringUtils.contains(funcName, "換名")) {
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
    
    
    System.out.println("==============客戶資料==============");
    stringSQL = "SELECT CustomNo, CustomName, CountryName, IsBlackList, IsControlList, IsLinked FROM Sale05M091 WHERE ORDERNO = '" + orderNo + "' "
        + "and ISNULL(StatusCd , '') != 'C' ";
    String[][] custData = tBean.getDbSale().queryFromPool(stringSQL);
    if (custData.length > 0) {
      aBean.setRecordType("客戶資料");
      AMLTools_Lyods aml = new AMLTools_Lyods(aBean);
      
      for (int m = 0; m < custData.length; m++) {
        String custNo = custData[m][0].trim();
        String custName = custData[m][1].trim();
        QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);
        
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustTitle("客戶");
        cBean.setCustomNo(custNo);      //身分證字號
        cBean.setCustomName(custName);    //姓名
        cBean.setBirthday(qBean.getBirthday());  //生日
        cBean.setIndustryCode(qBean.getJobType());  //業別
        cBean.setCountryName(custData[m][2].trim());  //國名
        cBean.setbStatus(custData[m][3].trim());      //黑名單
        cBean.setcStatus(custData[m][4].trim());      //控管名單
        cBean.setrStatus(custData[m][5].trim());      //利害關係人

        // 不適用LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        aml.insNotUse(noUseAML, cBean);

        //18. 制裁名單
        errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
        
        //21. PEPS
        errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
        
        // 9. 資恐地區
        errMsg += (aml.chkAML009(cBean).getData()).toString().trim();
        
        // 17.黑名單&控管名單
        errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
        
        // 19.利害關係人
        errMsg += (aml.chkAML019(cBean).getData()).toString().trim();
        
      } // for end
    }
    System.out.println("============================");
    
    
    System.out.println("==============實質受益人==============");
    stringSQL = "select  BCustomNo, BenName , CountryName, IsBlackList, IsControlList, IsLinked, "
              + "CustomNo, (select top 1 CustomName from sale05m091 b where a.OrderNo=b.OrderNo and a.CustomNo=b.CustomNo and ISNULL(b.StatusCd, '') != 'C') "  
              + "from Sale05M091Ben a "
              + "where OrderNo = '" + orderNo + "' and ISNULL(a.StatusCd, '') != 'C' ";
    String[][] benData = tBean.getDbSale().queryFromPool(stringSQL);
    if (benData.length > 0) {
      aBean.setRecordType("實質受益人資料");
      AMLTools_Lyods aml = new AMLTools_Lyods(aBean);
      
      for (int m = 0; m < benData.length; m++) {
        String custNo = benData[m][0].trim();
        QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);
        
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustTitle("實質受益人");
        cBean.setCustomNo(benData[m][0].trim());      //身分證字號
        cBean.setCustomName(benData[m][1].trim());    //姓名
        cBean.setBirthday(qBean.getBirthday());  //生日
        cBean.setIndustryCode(qBean.getJobType());  //業別
        cBean.setCountryName(benData[m][2].trim());  //國名
        cBean.setbStatus(benData[m][3].trim());      //黑名單
        cBean.setcStatus(benData[m][4].trim());      //控管名單
        cBean.setrStatus(benData[m][5].trim());      //利害關係人
        cBean.setCustTitle2("客戶");
        cBean.setCustomName2(benData[m][7].trim());
        
        // 不適用LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        aml.insNotUse(noUseAML, cBean);

        //18. 制裁名單
        errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
        
        //21. PEPS
        errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
        
        // 9. 資恐地區
        errMsg += (aml.chkAML009(cBean).getData()).toString().trim();
        
        // 17.黑名單&控管名單
        errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
        
        // 19.利害關係人
        errMsg += (aml.chkAML019(cBean).getData()).toString().trim();

      } // for end
    } // 實質受益人 END
    System.out.println("============================");

    System.out.println("==============代理人==============");
    stringSQL = "select a.ACustomNo , a.AgentName, a.CountryName, a.IsBlackList, a.IsControlList, a.IsLinked, a.AgentRel, a.CustomNo, " 
              + "(SELECT TOP 1 CustomName FROM SALE05M091 b WHERE b.CustomNo = a.CustomNo AND b.OrderNo= a.OrderNo ) as custName " 
              + "from Sale05M091Agent a where ISNULL(a.StatusCd, '') != 'C' and a.orderNo = '" + orderNo + "' ";
    String[][] agentData = tBean.getDbSale().queryFromPool(stringSQL);
    if (agentData.length > 0) {
      aBean.setRecordType("代理人資料");
      AMLTools_Lyods aml = new AMLTools_Lyods(aBean);
      
      for (int m = 0; m < agentData.length; m++) {
        String custNo = agentData[m][0].trim();
        QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);
        
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustTitle("代理人");
        cBean.setCustomNo(custNo);      //身分證字號
        cBean.setCustomName(agentData[m][1].trim());    //姓名
        cBean.setBirthday(qBean.getBirthday());  //生日
        cBean.setIndustryCode(qBean.getJobType());  //業別
        cBean.setCountryName(agentData[m][2].trim());  //國名
        cBean.setbStatus(agentData[m][3].trim());      //黑名單
        cBean.setcStatus(agentData[m][4].trim());      //控管名單
        cBean.setrStatus(agentData[m][5].trim());      //利害關係人
        cBean.setAgentRel(agentData[m][6].trim());
        cBean.setCustTitle2("客戶");
        cBean.setCustomName2(agentData[m][8].trim());  //主人name
        
        // 不適用LOG1~4 ,6 ,7 ,10~16
        int[] noUseAML = { 1, 2, 3, 4, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        aml.insNotUse(noUseAML, cBean);

//        String custSA = "客戶" + agentData[m][1].trim() + "之";
//        if (m == 0) errMsg += "\n";

        //18. 制裁名單
        errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
        
        //21. PEPS
        errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
        
        // 9. 資恐地區
        errMsg += (aml.chkAML009(cBean).getData()).toString().trim();
        
        // 17.黑名單&控管名單
        errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
        
        // 19.利害關係人
        errMsg += (aml.chkAML019(cBean).getData()).toString().trim();

        // 8. 洗錢第八條(有代理人)
        errMsg += (aml.chkAML008(cBean).getData()).toString().trim().replaceAll("<br>", "\n");

        // 5. 關係非二等血姻親
        errMsg += (aml.chkAML005(cBean).getData()).toString().trim().replaceAll("<br>", "\n");

      } // for end
    } // 代理人 END
    System.out.println("============================");

    // 送出errMsg
    if (StringUtils.isNotBlank(errMsg)) {
//      setValue("errMsgBoxText", errMsg);
      messagebox(errMsg);
//      getButton("errMsgBoxBtn").doClick();
//      getButton("sendMail").doClick();
    }
    System.out.println("==============洗錢防治檢核LOG END====================================");
    return value;
  }

  public String getInformation() {
    return "---------------AML(AML).defaultValue()----------------";
  }
}
