package Sale.AML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fglife.soap.cr.RenewRelatedReply;

import Farglory.aml.AMLTools_Lyods;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCheckTools_Lyods;
import Farglory.aml.RiskCustomBean;
import Farglory.util.Result;
import Farglory.util.ResultStatus;
import Farglory.util.RiskCheckRS;
import Farglory.util.SendMailBean;

/**
 * 
 * 必須要有一個 server side 的按鈕，無法直接從前端使用後端物件
 * 
 * 傳入值:
 * (空白用半形空格表示，取出後直接trim掉)
 * 0.orderNo
 * 1.orderDate
 * 2.custNo
 * 3.custName
 * 4.birthday
 * 5.industryCode
 * 
 * @author B04391
 *
 */
public class BtCustAML extends jcx.jform.sproc {
  public String getDefaultValue(String value) throws Throwable {
    String rsMsg = "";
    String serverType = "";
    boolean isTest = false;
    String lyodsSoapURL = "";
    Result result = null;
    
    //config
    Map config = (HashMap) get("config");
    serverType = config.get("serverType").toString();
    lyodsSoapURL = config.get("lyodsSoapURL").toString();
    isTest = "PROD".equals(serverType) ? false : true;
    
    //param
    String custAMLText = getValue("AMLText");
    String[] arrParam = custAMLText.split(",");
    String orderNo = arrParam[0].toString().trim();
    String orderDate = arrParam[1].toString().trim();
    if( orderNo.length() == 0 ) {
      rsMsg = "<BtCustAML無訂單編號，程序終止>";
      return value;
    }
    if( orderDate.length() == 0 ) {
      rsMsg = "<BtCustAML無訂單日期，程序終止>";
      return value;
    }
    
    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setOrderNo(orderNo);
    aBean.setOrderDate(orderDate.replaceAll("/", "").replaceAll("/", ""));
    aBean.setEmakerUserNo(getUser());
    aBean.setTestServer(isTest);
    aBean.setLyodsSoapURL(lyodsSoapURL);
    aBean.setDb400CRM(getTalk("400CRM"));
    aBean.setDbSale(getTalk("Sale"));
    aBean.setDbEIP(getTalk("EIP"));
    AMLTools_Lyods aml = new AMLTools_Lyods(aBean);

    RiskCustomBean custBean = new RiskCustomBean();
    custBean.setCustomNo(arrParam[2].toString().trim());      //身分證字號
    custBean.setCustomName(arrParam[3].toString().trim());    //姓名
    custBean.setBirthday(arrParam[4].replaceAll("/", "").replaceAll("-", "").toString().trim());  //生日
    custBean.setIndustryCode(arrParam[5].toString().trim());  //業別
    
    String processType = arrParam[4].toString().trim();
    if( "query1821".equals(processType) ) { //查詢PEPS or 制裁
      // 制裁名單
      result = aml.chkAML018_San(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        rsMsg += result.getData().toString().trim() + "\n";
      }
      // PEPS
      result = aml.chkAML021_PEPS(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        rsMsg += result.getData().toString().trim() + "\n";
      }
    }else if( "query18".equals(processType) ) { //只看制裁
      // 制裁名單
      result = aml.chkAML018_San(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        rsMsg += result.getData().toString().trim() + "\n";
      }
    }
    else if( "updRelated".equals(processType) ) {  //更新關聯人
      result = aml.renewRelated(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        RenewRelatedReply related = (RenewRelatedReply) result.getData(); 
        rsMsg += related.getResult().toString().trim() + "\n";
      }
    }else if( "custListRiskCheck".equals(processType) ) {  //主要客戶風險值
      String[][] table1 = this.getTableData("table1");
      RiskCustomBean[] cBeans = new RiskCustomBean[table1.length];
      for(int i=0 ; i<table1.length ; i++) {
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustomNo(table1[i][5].trim());
        cBean.setCustomName(table1[i][6].trim());
        cBean.setBirthday(table1[i][8].trim());
        cBean.setIndustryCode(table1[i][24].trim());
        cBeans[i] = cBean;
      }
      RiskCheckTools_Lyods risk = new RiskCheckTools_Lyods(aBean);
      result = risk.processRisk(cBeans);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        RiskCheckRS rcRs = (RiskCheckRS) result.getData(); 
        //風險值結果
        rsMsg += (!"".equals(rcRs.getRsMsg())? rcRs.getRsMsg():"無風險值結果，請確認名單內容是否正確。") + "\n";
        //寄發Email
        if( !isTest ) {
          List rsSendMailList = (List) rcRs.getSendMailList();
          for (int ii = 0; ii < rsSendMailList.size(); ii++) {
            SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
            String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
            System.out.println("寄發MAIL>>>" + sendRS);
          }
        }
      }
      
    }
    
    setValue("AMLText" , rsMsg);

    return value;
  }

  public String getInformation() {
    return "---------------button7(button7).defaultValue()----------------";
  }
}
