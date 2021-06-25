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
    renewRelatedQuery.setWriteLog("Y");                                   //寫入LOG v
    renewRelatedQuery.setCalculationCode("1");                            //是否計算風險評分代碼0: 不計算風險值但進行名單檢測 1: 計算風險值且進行名單檢測 2: 計算風險值但不進行名單檢測 3: 不計算風險值且不進行名單檢測
    renewRelatedQuery.setAddCustomer("Y");                                //新增或註銷主要客戶 Y.新增 N.註銷
    renewRelatedQuery.setCustomerType("2");   //主要客戶類別 v
    renewRelatedQuery.setId("22099131");                                 //身分證字號/統編 v
    renewRelatedQuery.setKeyNumber("CS0331H111A10912006");               //保單號碼/契約號碼 v
    renewRelatedQuery.setCaseNumber("CS0331H111A10912006");              //案件編號 v
    renewRelatedQuery.setSystemCode("RYB");                               //系統代碼 v
    renewRelatedQuery.setProgramCode("RYB");                              //程式代碼 v
    renewRelatedQuery.setUserId("B04391");                              //承辦人員編 v
    renewRelatedQuery.setUserUnit("");                                    //承辦人單位 v
    RenewRelatedReply renewRelatedReply = blackListClient.executeRenewRelated(renewRelatedQuery);
    
    
    System.out.println("errMsg>>>" + renewRelatedReply.getResult());
    
    return value;
  }
  public String getInformation(){
    return "---------------Test(Test).defaultValue()----------------";
  }
}
