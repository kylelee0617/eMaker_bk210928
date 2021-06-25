package Sale.test;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;

import com.fglife.soap.client.BlackListClient;
import com.fglife.soap.cr.RenewRelatedQuery;
import com.fglife.soap.cr.RenewRelatedReply;

import Farglory.aml.AMLTools_Lyods;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCustomBean;
import Farglory.util.Result;
import jcx.jform.*;

public class TestRenewRelated extends sproc{
  public String getDefaultValue(String value)throws Throwable{
    String rsMsg = "";
    Result result = null;
    
    String url = "http://172.22.9.178:8081/CR/cxf/blacklist?wsdl";
//    BlackListClient blickListClient = new BlackListClient(url);
//    RenewRelatedQuery renewRelatedQuery = new RenewRelatedQuery();
//    renewRelatedQuery.setCalculationCode("1");
//    renewRelatedQuery.setCaseNumber("34335433456456345645");
//    renewRelatedQuery.setCustomerType("1");
//    renewRelatedQuery.setId("A123456789");
//    renewRelatedQuery.setKeyNumber("5556666");
//    renewRelatedQuery.setProgramCode("HR01");
//    renewRelatedQuery.setSystemCode("HR");
//    renewRelatedQuery.setUserId("B04600");
//    renewRelatedQuery.setUserUnit("3200100");
//    renewRelatedQuery.setWriteLog("Y");
//    RenewRelatedReply renewRelatedReply = blickListClient.executeRenewRelated(renewRelatedQuery);
//    System.out.println( renewRelatedReply.getResult());
    
    
    BlackListClient blackListClient = new BlackListClient(url);
    RenewRelatedQuery renewRelatedQuery = new RenewRelatedQuery();        
    renewRelatedQuery.setWriteLog("Y");                                   //�g�JLOG v
    renewRelatedQuery.setCalculationCode("1");                            //�O�_�p�⭷�I�����N�X0: ���p�⭷�I�Ȧ��i��W���˴� 1: �p�⭷�I�ȥB�i��W���˴� 2: �p�⭷�I�Ȧ����i��W���˴� 3: ���p�⭷�I�ȥB���i��W���˴�
    renewRelatedQuery.setAddCustomer("Y");                                //�s�W�ε��P�D�n�Ȥ� Y.�s�W N.���P
    renewRelatedQuery.setCustomerType("2");   //�D�n�Ȥ����O v
    renewRelatedQuery.setId("22099131");                                 //�����Ҧr��/�νs v
    renewRelatedQuery.setKeyNumber("CS0331H111A10912006");               //�O�渹�X/�������X v
    renewRelatedQuery.setCaseNumber("CS0331H111A10912006");              //�ץ�s�� v
    renewRelatedQuery.setSystemCode("RYB");                               //�t�ΥN�X v
    renewRelatedQuery.setProgramCode("RYB");                              //�{���N�X v
    renewRelatedQuery.setUserId("B04391");                              //�ӿ�H���s v
    renewRelatedQuery.setUserUnit("");                                    //�ӿ�H��� v
    RenewRelatedReply renewRelatedReply = blackListClient.executeRenewRelated(renewRelatedQuery);
    
    
    System.out.println("errMsg>>>" + renewRelatedReply.getResult());
    
    return value;
  }
  public String getInformation(){
    return "---------------Test(Test).defaultValue()----------------";
  }
}
