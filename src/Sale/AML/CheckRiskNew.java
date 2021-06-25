package Sale.AML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCheckTools_Lyods;
import Farglory.aml.RiskCustomBean;
import Farglory.util.KUtils;
import Farglory.util.Result;
import Farglory.util.RiskCheckRS;
import Farglory.util.SendMailBean;
import Farglory.util.TalkBean;
import jcx.db.talk;
import jcx.util.datetime;

/**
 * 此功能在資料庫異動後執行，
 * 故從資料庫取得主要客戶資料，可不受畫面影響與購屋證明單共用
 * 
 * @author B04391
 *
 */
public class CheckRiskNew extends jcx.jform.sproc {

  public String getDefaultValue(String value) throws Throwable {
    System.out.println("Class >>> Sale.AML.ChekRiskNew");
    System.out.println(datetime.getTime("h:m:s"));
    
    //config
    Map config = (HashMap) get("config");
    boolean isTest = "PROD".equals(config.get("serverType").toString()) ? false : true;
    String lyodsSoapURL = config.get("lyodsSoapURL").toString();
    
    talk dbSale = getTalk("Sale");
    TalkBean tBean = new TalkBean();
    tBean.setDbSale(dbSale);

    KUtils kUtil = new KUtils(tBean);
    String funcName = value;
    String recordType = "客戶風險值計算";
    String projectId = "";
    String orderNo = "";
    String orderDate = "";
    String actionText = "";
    
    //根據不同功能取得案別訂單編號等資訊
    System.out.println(">>>funcName:" + funcName);
    if(StringUtils.contains(funcName, "購屋證明單")) {
      projectId = getValue("field1").trim();
      orderNo = getValue("field3").trim();
      orderDate = getValue("field2").trim();
      actionText = getValue("actionText").trim();
    }else if(StringUtils.contains(funcName, "換名")) {
      projectId = getValue("ProjectID1").trim();
      orderNo = getValue("OrderNo").trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
      actionText = getValue("actionText").trim();
    }
    
    //DB取出主要客戶名單
    String sql = "select CustomNo , CustomName , Birthday , IndustryCode from Sale05M091 where orderNO = '" + orderNo + "' and ISNULL(statusCd, '') != 'C' ";
    String[][] retCustom = dbSale.queryFromPool(sql);
    RiskCustomBean[] cBeans = new RiskCustomBean[retCustom.length];
    for(int ii=0 ; ii<retCustom.length ; ii++) {
      RiskCustomBean cBean = new RiskCustomBean();
      cBean.setCustomNo(retCustom[ii][0].trim());
      cBean.setCustomName(retCustom[ii][1].trim());
      cBean.setBirthday(retCustom[ii][2].trim());
      cBean.setIndustryCode(retCustom[ii][3].trim());
      cBeans[ii] = cBean;
    }
    System.out.println("CR test1");
    
    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setProjectID1(projectId);
    aBean.setOrderNo(orderNo);
    aBean.setOrderDate(orderDate);
    aBean.setActionName(actionText);
    aBean.setFunc(funcName);
    aBean.setRecordType(recordType);
    aBean.setEmakerUserNo(getUser());
    aBean.setTestServer(isTest);
    aBean.setLyodsSoapURL(lyodsSoapURL);
    aBean.setDbSale(getTalk("Sale"));
    aBean.setDbEMail(getTalk("eMail"));
    aBean.setDb400CRM(getTalk("400CRM"));
    aBean.setDbEIP(getTalk("EIP"));
    aBean.setDbPw0D(getTalk("pw0d"));
    aBean.setUpdSale05M091(true);
    aBean.setUpd070Log(true);
    aBean.setSendMail(true);
    System.out.println("CR test2");
    RiskCheckTools_Lyods risk = new RiskCheckTools_Lyods(aBean);
    Result rs = risk.processRisk(cBeans);

    // 執行結果
    String rsStatus = rs.getRsStatus()[3].toString().trim();
    System.out.println("執行結果>>>" + rsStatus);

   //////////////////////
   RiskCheckRS rcRs = (RiskCheckRS) rs.getData();
   
   //風險值結果
   String rsMsg = !"".equals(rcRs.getRsMsg())? rcRs.getRsMsg():"無風險值結果";
   messagebox(rsMsg);
   
   
   //寄發Email
   if( "PROD".equals(get("serverType").toString().trim()) ) {
     List rsSendMailList = (List) rcRs.getSendMailList();
     for (int ii = 0; ii < rsSendMailList.size(); ii++) {
       SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
       String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
       System.out.println("寄發MAIL>>>" + sendRS);
     }
   }

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
