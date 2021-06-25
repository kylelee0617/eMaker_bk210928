package Farglory.test;

import com.fglife.soap.client.BlackListClient;
import com.fglife.soap.cr.MainQuery;
import com.fglife.soap.cr.MainReply;
import com.fglife.soap.cr.RenewRelatedQuery;
import com.fglife.soap.cr.RenewRelatedReply;

public class BlackListClientTest {

  public static void main(String[] args) throws Exception {
    testExecuteMain();
    testExecuteRenewRelated();
  }

  public static void testExecuteMain() throws Exception {
    String url = "http://172.22.9.178:8081/CR/cxf/blacklist?wsdl";

    BlackListClient blickListClient = new BlackListClient(url);
    MainQuery mainQuery = new MainQuery();
    mainQuery.setAddAccount("Y");
    mainQuery.setAddCustomer("Y");
    mainQuery.setApplyDate("Y");
    mainQuery.setBirth("Y");
    mainQuery.setBirthNation("Y");
    mainQuery.setCaseNumber("Y");
    mainQuery.setChangeOrgnization("");
    mainQuery.setChannel("");
    mainQuery.setChineseName("¤ý¤j©ú");
    mainQuery.setContractDate("");
    mainQuery.setCustomerType("");
    mainQuery.setEnglishName("");
    mainQuery.setId("");
    mainQuery.setIndustry("22");
    mainQuery.setIndustry2("");
    mainQuery.setKeyNumber("");
    mainQuery.setModifyData("Y");
    mainQuery.setProduct("");
    mainQuery.setProfession("15000430");
    mainQuery.setProfession2("");
    mainQuery.setProgramCode("HR");
    mainQuery.setRegisterNation("TWN");
    mainQuery.setRiskResult("Y");
    mainQuery.setSex("M");
    mainQuery.setSystemCode("HR");
    mainQuery.setUserId("B04600");
    mainQuery.setUserUnit("3200100");
    mainQuery.setWriteLog("Y");
    System.out.println("---------request---------");
    System.out.println(mainQuery);
    MainReply mainReply = blickListClient.executeMain(mainQuery);
    System.out.println("---------response---------");
    System.out.println(mainReply.getIsBlacklist());
    System.out.println(mainReply.getComment());
    for (String hitstatus : mainReply.getHitStatusList().getHitStatus()) {
      System.out.println("hitstatus-" + hitstatus);
    }
  }

  public static void testExecuteRenewRelated() throws Exception {
    String url = "http://172.22.9.178:8081/CR/cxf/blacklist?wsdl";
    BlackListClient blickListClient = new BlackListClient(url);
    RenewRelatedQuery renewRelatedQuery = new RenewRelatedQuery();
    renewRelatedQuery.setCalculationCode("1");
    renewRelatedQuery.setCaseNumber("34335433456456345645");
    renewRelatedQuery.setCustomerType("1");
    renewRelatedQuery.setId("A123456789");
    renewRelatedQuery.setKeyNumber("5556666");
    renewRelatedQuery.setProgramCode("HR01");
    renewRelatedQuery.setSystemCode("HR");
    renewRelatedQuery.setUserId("B04600");
    renewRelatedQuery.setUserUnit("3200100");
    renewRelatedQuery.setWriteLog("Y");
    System.out.println("---------request---------");
    System.out.println(renewRelatedQuery.getId());
    RenewRelatedReply renewRelatedReply = blickListClient.executeRenewRelated(renewRelatedQuery);
    System.out.println("---------response---------");
    System.out.println(renewRelatedReply.getResult());
  }
}
