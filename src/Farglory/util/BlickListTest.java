//import com.fasterxml.jackson.databind.ObjectMapper;
import com.fglife.soap.client.BlickListClient;
import com.fglife.soap.cr.MainQuery;
import com.fglife.soap.cr.MainReply;
import com.fglife.soap.cr.RenewRelatedQuery;
import com.fglife.soap.cr.RenewRelatedReply;

public class BlickListTest {

    public void testExecuteMain() throws Exception {
        String url = "http://172.22.9.178:8081/CR/cxf/blacklist?wsdl";
        BlickListClient blickListClient = new BlickListClient(url);
        MainQuery mainQuery = new MainQuery();
        mainQuery.setRiskResult("Y");         //查風險值 v
        mainQuery.setCheckAll("Y");           //查所有類別 v
        mainQuery.setChangeOrgnization("");   //轉指派部門 v
        mainQuery.setAddCustomer("Y");        //新增或註銷主要客戶 v
        mainQuery.setAddAccount("Y");         //新增或註銷保單 v
        mainQuery.setWriteLog("Y");           //寫入LOG v
        mainQuery.setModifyData("Y");         //更新客戶資料
        mainQuery.setCustomerType("");        //主要客戶類別 v
        mainQuery.setChineseName("王大明");   //中文姓名
        mainQuery.setEnglishName("");         //英文姓名
        mainQuery.setId("");                  //身分證字號 v
        mainQuery.setSex("M");                //性別
        mainQuery.setBirth("Y");              //生日
        mainQuery.setRegisterNation("TWN");   //居住地國籍
        mainQuery.setBirthNation("Y");        //出生地國籍
        mainQuery.setProfession("15000430");  //職業類型
        mainQuery.setProfession2("");         //次職業類型
        mainQuery.setIndustry("22");          //行業類別
        mainQuery.setIndustry2("");           //次行業類別
        mainQuery.setKeyNumber("");           //保單號碼/契約號碼 v
        mainQuery.setCaseNumber("Y");         //案件編號 v
        mainQuery.setSystemCode("CL");        //系統代碼
        mainQuery.setProgramCode("CL01");     //程式代碼 v
        mainQuery.setApplyDate("Y");          //受理/申請日期
        mainQuery.setContractDate("");        //契約生效日期
        mainQuery.setProduct("");             //產品代碼
        mainQuery.setChannel("");             //通路代碼
        mainQuery.setUserId("B04600");        //承辦人員編 v
        mainQuery.setUserUnit("3200100");     //承辦人單位
        
        System.out.println("---------request---------");
//        System.out.println( new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(mainQuery));
        MainReply mainReply = blickListClient.executeMain(mainQuery);
        System.out.println("---------response---------");
//        System.out.println( new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(mainReply));
    }


    public void testExecuteRenewRelated() throws Exception {
        String url = "http://172.22.9.178:8081/CR/cxf/blacklist?wsdl";
        BlickListClient blickListClient = new BlickListClient(url);
        RenewRelatedQuery renewRelatedQuery = new RenewRelatedQuery();
        renewRelatedQuery.setWriteLog("Y");                       //寫入LOG v
        renewRelatedQuery.setCustomerType("1");                   //主要客戶類別 v
        renewRelatedQuery.setId("A123456789");                    //身分證字號/統編 v
        renewRelatedQuery.setKeyNumber("5556666");                //保單號碼/契約號碼 v
        renewRelatedQuery.setCaseNumber("34335433456456345645");  //案件編號 v
        renewRelatedQuery.setSystemCode("CL");                    //系統代碼 v
        renewRelatedQuery.setProgramCode("CL01");                 //程式代碼 v
        renewRelatedQuery.setUserId("B04600");                    //承辦人員編 v
        renewRelatedQuery.setUserUnit("3200100");                 //承辦人單位 v
        System.out.println("---------request---------");
//        System.out.println( new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(renewRelatedQuery));
        RenewRelatedReply renewRelatedReply = blickListClient.executeRenewRelated(renewRelatedQuery);
        System.out.println("---------response---------");
//        System.out.println( new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(renewRelatedReply));
    }
}
