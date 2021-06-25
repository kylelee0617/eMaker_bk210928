package Sale.Sale05M274;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import Farglory.aml.AMLTools_Lyods;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCustomBean;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import Farglory.util.Result;
import jcx.db.talk;
import jcx.jform.bproc;

public class AML extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    // 20191030 洗錢控管名單LOG
    System.out.println("==============Sale05M274 AML START====================================");
    talk dbSale = getTalk("Sale");

    // config
    Map config = (HashMap) get("config");
    boolean isTest = "PROD".equals(config.get("serverType").toString()) ? false : true;
    String lyodsSoapURL = config.get("lyodsSoapURL").toString();

    KUtils kUtil = new KUtils();
    Result rs = new Result();
    String stringSQL = "";
    String errMsg = "";
    String funcName = getFunctionName().trim();
    String recordType = "";
    String actionName = getValue("text11").trim();
    String projectId = getValue("ProjectID1").trim();
    String orderNo = "";
    String orderDate = "";
    StringBuilder custNames = new StringBuilder();

    // 畫面值
    String stringContractNo = getValue("ContractNoDisplay").trim();

    // 訂單編號
    String tbOrderName = "table1";
    JTable tbOrder = this.getTable(tbOrderName);
    if (tbOrder.getRowCount() > 0) {
      orderNo = this.getValueAt(tbOrderName, 0, "OrderNo").toString().trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
    }

    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setProjectID1(projectId);
    aBean.setOrderNo(orderNo);
    aBean.setOrderDate(orderDate.replaceAll("/", "").replaceAll("-", ""));
    aBean.setFunc(funcName);
    aBean.setRecordType(recordType);
    aBean.setActionName(actionName);
    aBean.setEmakerUserNo(getUser());
    aBean.setTestServer(isTest);
    aBean.setLyodsSoapURL(lyodsSoapURL);
    aBean.setDb400CRM(getTalk("400CRM"));
    aBean.setDbSale(dbSale);
    aBean.setDbEIP(getTalk("EIP"));

    stringSQL = "SELECT ProjectID1,ContractDate FROM Sale05M274 WHERE ContractNo='" + stringContractNo + "'";
    String[][] retSale = dbSale.queryFromPool(stringSQL);
    if (retSale.length > 0) {// 有合約記錄

      // 客戶資料
      stringSQL = "SELECT CustomNo, CustomName, CountryName, IsBlackList, IsControlList, IsLinked FROM Sale05M091 WHERE ORDERNO = '" + orderNo + "' "
          + "and ISNULL(StatusCd , '') != 'C' ";
      String[][] custData = dbSale.queryFromPool(stringSQL);
      if (custData.length > 0) {
        aBean.setRecordType("客戶資料");
        AMLTools_Lyods aml = new AMLTools_Lyods(aBean);

        for (int m = 0; m < custData.length; m++) {
          String custNo = custData[m][5].trim();
          String custName = custData[m][6].trim();
          QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);

          RiskCustomBean cBean = new RiskCustomBean();
          cBean.setCustTitle("客戶");
          cBean.setCustomNo(custData[m][0].trim()); // 身分證字號
          cBean.setCustomName(custName); // 姓名
          cBean.setBirthday(qBean.getBirthday()); // 生日
          cBean.setIndustryCode(qBean.getJobType()); // 業別
          cBean.setCountryName(custData[m][2].trim()); // 國名
          cBean.setbStatus(custData[m][3].trim()); // 黑名單
          cBean.setcStatus(custData[m][4].trim()); // 控管名單
          cBean.setrStatus(custData[m][5].trim()); // 利害關係人

          // 18. 制裁名單
          errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();

          // 21. PEPS
          errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
          
          //因為合約會審的第三人跟委託人都沒有"主人"，故以客戶集合作為主人名
          custNames.append(m>0? ",":"").append(custName);
        }
      }

      // 被委託人
      stringSQL = "SELECT IsChoose FROM Sale05M279 where ContractNo ='" + stringContractNo + "' and ItemCd='O01' and ItemlsCd = 'Other2'";
      String[][] retOther2 = dbSale.queryFromPool(stringSQL);
      if (retOther2.length > 0) {// 被委託人 START
        String strIsChoose = retOther2[0][0].trim();
        if ("Y".equals(strIsChoose)) {
          stringSQL = "SELECT TrusteeName,TrusteeId,Blacklist,Controllist,Stakeholder,Relation FROM Sale05M355 where ContractNo ='" + stringContractNo + "'";
          String[][] retM355 = dbSale.queryFromPool(stringSQL);
          if (retM355.length > 0) {
            aBean.setRecordType("被委託人資料");
            AMLTools_Lyods aml = new AMLTools_Lyods(aBean);

            for (int m = 0; m < retM355.length; m++) {
              String custNo = retM355[m][1].trim();
              QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);

              RiskCustomBean cBean = new RiskCustomBean();
              cBean.setCustTitle("被委託人");
              cBean.setCustomNo(custNo); // 身分證字號
              cBean.setCustomName(retM355[m][0].trim()); // 姓名
              cBean.setBirthday(qBean.getBirthday()); // 生日
              cBean.setIndustryCode(qBean.getJobType()); // 業別
              cBean.setbStatus(retM355[m][2].trim()); // 黑名單
              cBean.setcStatus(retM355[m][3].trim()); // 控管名單
              cBean.setrStatus(retM355[m][4].trim()); // 利害關係人
              cBean.setAgentRel(retM355[m][5].trim());// 原因
              cBean.setCustTitle2("客戶");
              cBean.setCustomName2(custNames.toString());  //主人name

              // 不適用LOG1,2,3,4,6,7,9~16
              int[] noUseAML = { 1, 2, 3, 4, 6, 7, 9, 10, 11 ,12 ,13 ,14 ,15 ,16};
              aml.insNotUse(noUseAML, cBean);
              
              // 5. 關係非二等血姻親
              errMsg += (aml.chkAML005(cBean).getData()).toString().trim();
              
              // 8. 洗錢第八條
              errMsg += (aml.chkAML008(cBean).getData()).toString().trim();
              
              // 17.黑名單&控管名單
              errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
              
              //18. 制裁名單
              errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
              
              // 19.利害關係人
              errMsg += (aml.chkAML019(cBean).getData()).toString().trim();
              
              //21. PEPS
              errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
            }
          }
        }
      } // 被委託人 END

      // 指定第三人
      stringSQL = "SELECT IsChoose FROM Sale05M279 where ContractNo ='" + stringContractNo + "' and   ItemCd='O01' and ItemlsCd = 'Other44'";
      String[][] retOther44 = dbSale.queryFromPool(stringSQL);
      if (retOther44.length > 0) {// 指定第三人 START
        String strIsChoose = retOther44[0][0].trim();
        if ("Y".equals(strIsChoose)) {
          stringSQL = "SELECT DesignatedName,DesignatedId,Blacklist,Controllist,Stakeholder,ExportingPlace,Relation FROM Sale05M356 where ContractNo ='" + stringContractNo + "'";
          String[][] retM356 = dbSale.queryFromPool(stringSQL);
          if (retM356.length > 0) {
            aBean.setRecordType("被委託人資料");
            AMLTools_Lyods aml = new AMLTools_Lyods(aBean);

            for (int m = 0; m < retM356.length; m++) {
              String custNo = retM356[m][1].trim();
              QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, custNo);

              RiskCustomBean cBean = new RiskCustomBean();
              cBean.setCustTitle("被委託人");
              cBean.setCustomNo(custNo); // 身分證字號
              cBean.setCustomName(retM356[m][0].trim()); // 姓名
              cBean.setBirthday(qBean.getBirthday()); // 生日
              cBean.setIndustryCode(qBean.getJobType()); // 業別
              cBean.setbStatus(retM356[m][2].trim()); // 黑名單
              cBean.setcStatus(retM356[m][3].trim()); // 控管名單
              cBean.setrStatus(retM356[m][4].trim()); // 利害關係人
              cBean.setCountryName(retM356[m][5].trim()); //國別
              cBean.setAgentRel(retM356[m][6].trim());// 原因
              cBean.setCustTitle2("客戶");
              cBean.setCustomName2(custNames.toString());  //主人name
              
              // 不適用LOG1~4,6, 7, 8, 10,13~16
              int[] noUseAML = { 1, 2, 3, 4, 6, 7, 8, 9, 10,13 ,14 ,15 ,16};
              aml.insNotUse(noUseAML, cBean);
              
              //5. 關係非二等血姻親
              errMsg += (aml.chkAML005(cBean).getData()).toString().trim();
              
              //9. 資恐地區
              errMsg += (aml.chkAML009(cBean).getData()).toString().trim();

              //12. 合約第三人
              errMsg += (aml.chkAML012(cBean).getData()).toString().trim();
              
              //17.黑名單&控管名單
              errMsg += (aml.chkAML017(cBean).getData()).toString().trim();
              
              //18. 制裁名單
              errMsg += (aml.chkAML018_San(cBean).getData()).toString().trim();
              
              //19.利害關係人
              errMsg += (aml.chkAML019(cBean).getData()).toString().trim();
              
              //21. PEPS
              errMsg += (aml.chkAML021_PEPS(cBean).getData()).toString().trim();
      
            }
          }
        }
      } // 指定第三人 END
    }
    if (!"".equals(errMsg)) {
      setValue("errMsgBoxText", errMsg);
      getButton("errMsgBoxBtn").doClick();
      // getButton("sendMail2").doClick();
    }
    System.out.println("==============洗錢防治檢核LOG END====================================");
    return value;
  }

  public String getInformation() {
    return "---------------\u6d17\u9322\u9632\u5236(AML).defaultValue()----------------";
  }
}
