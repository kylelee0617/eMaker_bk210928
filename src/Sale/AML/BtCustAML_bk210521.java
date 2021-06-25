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
    String serverType = "";
    boolean isTest = false;
    String lyodsSoapURL = "";
    Result result = null;
    
    //config
    Map config = (HashMap) get("config");
    serverType = config.get("serverType").toString();
    lyodsSoapURL = config.get("lyodsSoapURL").toString();
    isTest = "PROD".equals(serverType) ? false : true;
      
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
    aBean.setOrderDate(orderDate.replaceAll("/", "").replaceAll("/", ""));
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
    }
    else if( "updRelated".equals(processType) ) {  //��s���p�H
      result = aml.renewRelated(custBean);
      if(ResultStatus.SUCCESS[0].equals(result.getRsStatus()[0]) ) {
        RenewRelatedReply related = (RenewRelatedReply) result.getData(); 
        rsMsg += related.getResult().toString().trim() + "\n";
      }
    }else if( "custListRiskCheck".equals(processType) ) {  //�D�n�Ȥ᭷�I��
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
        //���I�ȵ��G
        rsMsg += (!"".equals(rcRs.getRsMsg())? rcRs.getRsMsg():"�L���I�ȵ��G�A�нT�{�W�椺�e�O�_���T�C") + "\n";
        //�H�oEmail
        if( !isTest ) {
          List rsSendMailList = (List) rcRs.getSendMailList();
          for (int ii = 0; ii < rsSendMailList.size(); ii++) {
            SendMailBean smbean = (SendMailBean) rsSendMailList.get(ii);
            String sendRS = sendMailbcc(smbean.getColm1(), smbean.getColm2(), smbean.getArrayUser(), smbean.getSubject(), smbean.getContext(), null, "", "text/html");
            System.out.println("�H�oMAIL>>>" + sendRS);
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
