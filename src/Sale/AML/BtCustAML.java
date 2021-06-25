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
 * �����n���@�� server side �����s�A�L�k�����q�e�ݨϥΫ�ݪ���
 * 
 * �ǤJ��:
 * (�ťեΥb�ΪŮ��ܡA���X�᪽��trim��)
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
     * 0. projectId �קO�N�X
     * 1. orderNo �q��s��
     * 2. orderDate �q����
     * 3. func �\��j
     * 4. recordType �\��p
     * 5. custNo �Ȥ�ID
     * 6. custName �Ȥ�name
     * 7. birth �ͤ�/���U��
     * 8. indCode ��~�O�N�X
     * 9. processType ����N�X
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
    
    //����ˮ�
    if( orderNo.length() == 0 ) {
      rsMsg = "<BtCustAML�L�q��s���A�{�ǲפ�>";
      return value;
    }
    if( orderDate.length() == 0 ) {
      rsMsg = "<BtCustAML�L�q�����A�{�ǲפ�>";
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
    custBean.setCustomNo(custNo);      //�����Ҧr��
    custBean.setCustomName(custName);    //�m�W
    custBean.setBirthday(birth.replaceAll("/", "").replaceAll("-", ""));  //�ͤ�
    custBean.setIndustryCode(indCode);  //�~�O
    
    if( "query1821".equals(processType) ) { //�d��PEPS or ���
      System.out.println(">>query1821...");
      // ����W��
      result = aml.chkAML018_San(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        rsMsg += result.getData().toString().trim() + "\n";
      }
      // PEPS
      result = aml.chkAML021_PEPS(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        rsMsg += result.getData().toString().trim() + "\n";
      }
//      rsMsg += aml.getLyodsHits(custBean);  //�ݩR���ƻ�
    }else if( "query18".equals(processType) ) { //�u�ݨ��
      // ����W��
      result = aml.chkAML018_San(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        rsMsg += result.getData().toString().trim() + "\n";
      }
    }else if( "updRelated".equals(processType) ) {  //��s���p�H
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
