package Farglory.aml;

import org.apache.commons.lang3.StringUtils;

import com.fglife.soap.client.BlackListClient;
import com.fglife.soap.cr.MainQuery;
import com.fglife.soap.cr.MainReply;
import com.fglife.soap.cr.RenewRelatedQuery;
import com.fglife.soap.cr.RenewRelatedReply;

import Farglory.util.Result;
import Farglory.util.ResultStatus;
import jcx.db.talk;
import jcx.jform.bvalidate;

/**
 * for 萊斯
 * 
 * @author B04391
 *
 */

public class LyodsTools extends bvalidate {
  // DB
  talk db400 = null;
  talk dbSale = null;
  talk dbEIP = null;
  //param
  String empNo = "";
  String serverType = "";
  String lyodsSoapURL = "";
  String sysType = "RYB";// 不動產行銷B 銷售C
  boolean isTest = true;
  AMLyodsBean lyodsBean;

  public LyodsTools() throws Throwable {
  }

  public LyodsTools(AMLyodsBean lyodsBean) throws Throwable {
    //System.out.println(">>>LyodsTools init start");
    this.lyodsBean = lyodsBean;
    db400 = lyodsBean.getDb400CRM();
    dbSale = lyodsBean.getDbSale();
    dbEIP = lyodsBean.getDbEIP();
    isTest = lyodsBean.isTestServer();
    lyodsSoapURL = lyodsBean.getLyodsSoapURL();

    this.getEMPNO();  //工號轉換
    
    System.out.println(">>>LyodsTools init done");
  }
  
  /**
   * 寫入訂單-客戶，並更新關聯人
   * @return
   * @throws Throwable
   */
  public Result renewRelated() throws Throwable{
    Result rs = new Result();
    RiskCustomBean cBean = lyodsBean.getCustBean();
    String custNo = cBean.getCustomNo();
    
    BlackListClient blackListClient = new BlackListClient(lyodsSoapURL);  
    RenewRelatedQuery renewRelatedQuery = new RenewRelatedQuery();        
    renewRelatedQuery.setWriteLog("Y");                                   //寫入LOG v
    renewRelatedQuery.setCalculationCode(lyodsBean.getCalculationCode()); //是否計算風險評分代碼
    renewRelatedQuery.setAddCustomer(lyodsBean.getAddCustomer());         //新增或註銷主要客戶 Y.新增 N.註銷
    renewRelatedQuery.setCustomerType(this.isCompany(custNo)? "2":"1");   //主要客戶類別 v
    renewRelatedQuery.setId(custNo);                                      //身分證字號/統編 v
    renewRelatedQuery.setKeyNumber(lyodsBean.getOrderNo());               //保單號碼/契約號碼 v
    renewRelatedQuery.setCaseNumber(lyodsBean.getOrderNo());              //案件編號 v
    renewRelatedQuery.setSystemCode("RYB");                               //系統代碼 v
    renewRelatedQuery.setProgramCode("RYB");                              //程式代碼 v
    renewRelatedQuery.setUserId(this.empNo);                              //承辦人員編 v
    renewRelatedQuery.setUserUnit("");                                    //承辦人單位 v
    RenewRelatedReply renewRelatedReply = blackListClient.executeRenewRelated(renewRelatedQuery);
    
    rs.setData(renewRelatedReply);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  public Result checkRisk() throws Throwable {
    Result rs = new Result();
    RiskCustomBean cBean = this.lyodsBean.getCustBean();
    String custNo = cBean.getCustomNo();
    String custName = cBean.getCustomName();

    if (isTest) {
      System.out.println("LydosTools check custom>>>" + custNo + "," + custName);
    }

    // 210114 Kyle : 行業別
    String industryCode = cBean.getIndustryCode() != null ? cBean.getIndustryCode() : "";
    if ( StringUtils.equals(industryCode, "") ) {
      // 舊資料沒有行業別代碼
      String sql = "SELECT CZ02,CZ09 FROM PDCZPF WHERE CZ01='INDUSTRY' And CZ09 = '" + cBean.getMajorName() + "' ";
      String[][] retMajor = db400.queryFromPool(sql);
      if (retMajor.length > 0)
        industryCode = retMajor[0][0] != null ? retMajor[0][0].trim() : "";
    }

    // 經理人?
    String sqltopmanager = " SELECT TOP 1 PositionCD, PName, ChairMan From A_Position  WHERE PName = '" + cBean.getPositionName() + "' ORDER BY PositionCD DESC ";
    String retPosition2[][] = dbSale.queryFromPool(sqltopmanager);
    String isManager = "N";
    if (retPosition2.length > 0) {
      isManager = retPosition2[0][2];
    }
    // 法人 自然人 性別
    String type = "1";
    String sex = "";
    if (custNo.length() == 8) {
      type = "2";// N: 個人 C: 公司 F: 外國人
    } else {
      // 本國人性別
      if (custNo.charAt(1) == '1') {
        sex = "M";
      } else if (custNo.charAt(1) == '2') {
        sex = "F";
      }
    }

    // 國籍轉碼(若空預設TWN)
    String strCZ09 = "";
    if ( StringUtils.equals(cBean.getCountryName() , "") ) {
      strCZ09 = "中華民國";
    } else {
      strCZ09 = cBean.getCountryName().trim();
    }
    String strSaleSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCZ09 + "'";
    String retCNYCode[][] = db400.queryFromPool(strSaleSql);
    String cnyCode = retCNYCode[0][0].trim();
//    System.out.println("cnyCode=====> :" + cnyCode);

    // 組資料，SOAP
    try {
      //System.out.println(">>>testLT1");
      BlackListClient blackListClient = new BlackListClient(lyodsSoapURL);
      //System.out.println(">>>testLT2");
      MainQuery mainQuery = new MainQuery();
      mainQuery.setRiskResult(lyodsBean.getRiskResult());   // 查風險值 v
      mainQuery.setCheckAll(lyodsBean.getCheckAll());       // 查所有類別 v
      mainQuery.setChangeOrgnization("Y");                  // 轉指派部門 v
      mainQuery.setAddCustomer(lyodsBean.getAddCustomer()); // 新增或註銷主要客戶 v
      mainQuery.setAddAccount(lyodsBean.getAddAccount());   // 新增或註銷保單 v
      mainQuery.setWriteLog("Y");                           // 寫入LOG v
      mainQuery.setModifyData(lyodsBean.getModifyData());   // 更新客戶資料
      mainQuery.setCustomerType(type);                      // 主要客戶類別 v
      mainQuery.setChineseName(custName);                   // 中文姓名
      mainQuery.setEnglishName("");                         // 英文姓名
      mainQuery.setId(custNo);                              // 身分證字號 v
      mainQuery.setSex(sex);                                // 性別
      mainQuery.setBirth(cBean.getBirthday().replaceAll("/", "").replaceAll("-", "")); // 生日
      mainQuery.setRegisterNation(cnyCode);                 // 居住地國籍
      mainQuery.setBirthNation("");                         // 出生地國籍
      mainQuery.setProfession("");                          // 職業類型
      mainQuery.setProfession2("");                         // 次職業類型
      mainQuery.setIndustry(industryCode);                  // 行業類別
      mainQuery.setIndustry2("");                           // 次行業類別
      mainQuery.setKeyNumber(lyodsBean.getOrderNo());       // 保單號碼/契約號碼 v
      mainQuery.setCaseNumber(lyodsBean.getOrderNo());      // 案件編號 v (跟訂單編號一樣即可)
      mainQuery.setSystemCode(this.sysType);                // 系統代碼 V (跟程式代碼一樣即可)
      mainQuery.setProgramCode(this.sysType);               // 程式代碼 v
      mainQuery.setApplyDate("");                           // 受理/申請日期
      mainQuery.setContractDate(lyodsBean.getOrderDate().replaceAll("/", "").replaceAll("-", ""));  // 契約生效日期
      mainQuery.setProduct("");                             // 產品代碼
      mainQuery.setChannel("");                             // 通路代碼
      mainQuery.setUserId(this.empNo);                      // 承辦人員編 v
      mainQuery.setUserUnit("");                            // 承辦人單位
      //System.out.println(">>>testLT3");
      MainReply mainReply = blackListClient.executeMain(mainQuery);
      //System.out.println(">>>testLT4");

      // 風險值結果輸出
      rs.setData(mainReply);
      rs.setRsStatus(ResultStatus.SUCCESS);
      //System.out.println(">>>testLT5");
    }
    catch(Exception ex){
      rs.setExp(ex);
      rs.setRsStatus(ResultStatus.ERROR);
    }

    return rs;
  }
  
  /**
   * is法人?
   * @param id
   * @return
   */
  private boolean isCompany(String id) {
    boolean brs = false;
    String[] spCustNo = id.split("");
    if (!spCustNo[0].matches("[A-Z]+") && spCustNo.length == 8) brs = true;
    
    return brs;
  }
  
  // 工號轉換
  public void getEMPNO() throws Throwable {
    String stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + lyodsBean.getEmakerUserNo() + "'";
    String[][] retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      this.empNo = retEip[0][0];
    }
  }

  public boolean check(String value) throws Throwable {
    return false;
  }

}
