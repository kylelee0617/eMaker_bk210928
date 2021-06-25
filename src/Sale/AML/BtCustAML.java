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
    Result result = null;
    
    //config
    Map config = (HashMap) get("config");
    boolean isTest = "PROD".equals(config.get("serverType").toString()) ? false : true;
    String lyodsSoapURL = config.get("lyodsSoapURL").toString();
      
    /**
     * Param
     * 
     * 0. projectId 案別代碼
     * 1. orderNo 訂單編號
     * 2. orderDate 訂單日期
     * 3. func 功能大
     * 4. recordType 功能小
     * 5. custNo 客戶ID
     * 6. custName 客戶name
     * 7. birth 生日/註冊日
     * 8. indCode 行業別代碼
     * 9. processType 執行代碼
     */
    String custAMLText = getValue("AMLText");
    String[] arrParam = custAMLText.split(",");
    String projectId = arrParam[0].toString().trim();
    String orderNo = arrParam[1].toString().trim();
    String orderDate = arrParam[2].toString().trim();
    String func = arrParam[3].toString().trim();
    String recordType = arrParam[4].toString().trim();
    String custNo = arrParam[5].toString().trim();
    String custName = arrParam[6].toString().trim();
    String birth = arrParam[7].toString().trim();
    String indCode = arrParam[8].toString().trim();
    String processType = arrParam[9].toString().trim();
    System.out.println(">>>orderNO:" + orderNo +">>>orderDate:" + orderDate +">>>custNo:" + custNo +">>>custName:" + custName 
        +">>>birth:" + birth +">>>indCode:" + indCode +">>>processType:" + processType);
    
    //欄位檢核
    if( orderNo.length() == 0 ) {
      rsMsg = "<BtCustAML無訂單編號，程序終止>";
      return value;
    }
    if( orderDate.length() == 0 ) {
      rsMsg = "<BtCustAML無訂單日期，程序終止>";
      return value;
    }
    
    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setProjectID1(projectId);
    aBean.setOrderNo(orderNo);
    aBean.setOrderDate(orderDate.replaceAll("/", "").replaceAll("-", ""));
    aBean.setFunc(func);
    aBean.setRecordType(recordType);
    aBean.setEmakerUserNo(getUser());
    aBean.setTestServer(isTest);
    aBean.setLyodsSoapURL(lyodsSoapURL);
    aBean.setDb400CRM(getTalk("400CRM"));
    aBean.setDbSale(getTalk("Sale"));
    aBean.setDbEIP(getTalk("EIP"));
    AMLTools_Lyods aml = new AMLTools_Lyods(aBean);

    RiskCustomBean custBean = new RiskCustomBean();
    custBean.setCustomNo(custNo);      //身分證字號
    custBean.setCustomName(custName);    //姓名
    custBean.setBirthday(birth.replaceAll("/", "").replaceAll("-", ""));  //生日
    custBean.setIndustryCode(indCode);  //業別
    
    if( "query1821".equals(processType) ) { //查詢PEPS or 制裁
      System.out.println(">>query1821...");
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
//      rsMsg += aml.getLyodsHits(custBean);  //看命中甚麼
    }else if( "query18".equals(processType) ) { //只看制裁
      // 制裁名單
      result = aml.chkAML018_San(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        rsMsg += result.getData().toString().trim() + "\n";
      }
    }else if( "updRelated".equals(processType) ) {  //更新關聯人
      result = aml.renewRelated(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        RenewRelatedReply related = (RenewRelatedReply) result.getData(); 
        rsMsg += related.getResult().toString().trim() + "\n";
      }
    }
    
    setValue("AMLText" , rsMsg);

    return value;
  }

  public String getInformation() {
    return "---------------button7(button7).defaultValue()----------------";
  }
}
