/**
 * 20200602 Kyle : 洗錢樣態查詢功能
 * 20200603 Kyle : 本來應該要把2X種樣態都寫進來，但沒時間所以只寫了制裁跟PEPS，希望以後有時間補齊
 * 
 * 可於使用處設定一AMLCODE陣列控制
 * select * from saleRY773 order by AMLno asc
 * AML 001  同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。
 * AML 002  同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。
 * AML 003  同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。
 * AML 004  同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。
 * AML 005  代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。
 * AML 006  同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。
 * AML 007  同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。
 * AML 008  不動產銷售由第三方代理或繳款，系統檢核提示通報。
 * AML 009  客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。
 * AML 010  自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。
 * AML 011  交易最終受益人或交易人為主管機關公告之恐怖分子或團體；或國際認定或追查之恐怖組織；或交易資金疑似與恐怖組織有關聯者，應依資恐防制法進行相關作業。
 * AML 012  客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。
 * AML 013  客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。
 * AML 014  客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。
 * AML 015  要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。
 * AML 016  要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。
 * AML 017  客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。
 * AML 018  客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。
 * AML 019  客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
 * AML 020  客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。
 * AML 021  客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查。
 */
package Farglory.aml;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.fglife.soap.cr.MainReply;
import com.fglife.soap.cr.RenewRelatedReply;

import Farglory.util.Result;
import Farglory.util.ResultStatus;
import Farglory.util.TalkBean;
import jcx.db.talk;
import jcx.jform.bvalidate;

/**
 * 洗錢各態樣查詢
 * 
 * @author B04391
 *
 */

public class AMLTools_Lyods extends bvalidate {
  // DB
  talk db400 = null;
  talk dbSale = null;
  talk dbEIP = null;
  TalkBean tBean = null;
  boolean isTestServer = true;
  String lyodsSoapURL = "";

  // param 傳入值
  StringBuilder sbRsMsg = new StringBuilder();
  String strDocNo = "";
  String strOrderNo = ""; // 購屋證明單編號
  String strProjectID1 = ""; // 案別代碼
  String strOrderDate = ""; // 購屋證明單日期
  String strActionName = "存檔"; // 存Sale05M070使用
  String strActionNo = ""; // 存Sale05M070使用
  String func = "";

  int intRecordNo = 1; // 存Sale05M070使用
  Map mapAMLMsg = null;
  String strNowDate = "";
  String strNowDate2 = "";
  String rocNowDate = "";
  String strNowTime = "";
  String strNowTime2 = "";

  // 員工編號
  String userNo = "";
  String empNo = "";

  AMLyodsBean aml;

  public AMLTools_Lyods() throws Throwable {
  }

  public AMLTools_Lyods(AMLyodsBean aml) throws Throwable {
    db400 = aml.getDb400CRM();
    dbSale = aml.getDbSale();
    dbEIP = aml.getDbEIP();
    tBean = aml.gettBean();

    // config
    isTestServer = aml.isTestServer();
    lyodsSoapURL = aml.getLyodsSoapURL();

    this.aml = aml;
    strProjectID1 = aml.getProjectID1();
    strOrderDate = aml.getOrderDate();
    strActionName = aml.getActionName();
    func = aml.getFunc();

    // LOG日期,時間
    this.getDateTime();

    // 員工編號 & EIPNO
    this.getEmpNo();

    // 序號
    this.getRecordNo070ByType(aml);

    // actionNo
    this.getActionNo();

    // 取得AML態樣中文說明
    this.getAML();

  }

  public String getAML() throws Throwable {
    String rs = "getAML Error";
    String sql = "select * from saleRY773 where AMLType = 'AML' order by AMLNo asc";
    String[][] retAML = dbSale.queryFromPool(sql);
    mapAMLMsg = new HashMap();
    for (int i = 0; i < retAML.length; i++) {
      String[] retAML1 = retAML[i];
      mapAMLMsg.put(retAML1[1], retAML1[2]);
    }
    rs = "";
    return rs;
  }

  public void getActionNo() throws Throwable {
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    strActionNo = strNowDate + strNowTime + ram;
    System.out.println("strActionNo=====>" + strActionNo);
  }

  // 能依照使用功能對照recordNo
  public void getRecordNo070ByType(AMLyodsBean aml) throws Throwable {
    String stringSQL = "";
    if (aml.getFunc().indexOf("收款") == 0 && !"".equals(aml.getDocNo())) {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE DocNo ='" + aml.getDocNo() + "' ";
    } else if (aml.getFunc().indexOf("合約") == 0 && !"".equals(aml.getContractNo())) {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE ContractNo ='" + aml.getOrderNo() + "' ";
    } else {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + aml.getOrderNo() + "' ";
    }
    String[][] ret05M070 = dbSale.queryFromPool(stringSQL);
    if (ret05M070.length > 0 && !"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
  }

  public void getDateTime() throws Throwable {
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    strNowDate = nowsdf.format(now);

    String tempROCYear = "" + (Integer.parseInt(strNowDate.substring(0, strNowDate.length() - 4)) - 1911);
    rocNowDate = tempROCYear + strNowDate.substring(strNowDate.length() - 4, strNowDate.length());

    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HHmmss");
    strNowTime = nowTimeSdf.format(now);

    SimpleDateFormat nowsdf2 = new SimpleDateFormat("yyyy-MM-dd");
    strNowDate2 = nowsdf2.format(now);
    SimpleDateFormat nowTimeSdf2 = new SimpleDateFormat("HH:mm:ss");
    strNowTime2 = nowTimeSdf2.format(now);

    System.out.println("RocNowDate=====>" + rocNowDate);
    System.out.println("strNowTime=====>" + strNowTime);
  }

  public void getEmpNo() throws Throwable {
    // 員工碼
    String[][] retEip = null;
    String stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + this.aml.getEmakerUserNo() + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    } else {
      System.out.println(">>>None EmpNo<<<");
      empNo = "B04391";
    }
  }

  public boolean check(String value) throws Throwable {
    return false;
  }

  // 看命中那些
  public String getLyodsHits(RiskCustomBean cBean) throws Throwable {
    System.out.println(">>>getLyodsHits Start");
    StringBuilder sbMsg = new StringBuilder();

    // Lyods GO
    this.aml.setRiskResult("N"); // 比對 沒計算

    LyodsTools lyodsTools = new LyodsTools(this.aml);
    Result result = lyodsTools.checkRisk();
    if (result.getRsStatus()[0] != ResultStatus.SUCCESS[0]) {
      System.out.println("getLyodsHits Error>>>" + result.getExp().toString());
      return "ERROR";
    }

    MainReply mainReply = (MainReply) result.getData();
    if (mainReply != null) {
      if (StringUtils.isBlank(mainReply.getMessage().toString())) {
        List hits = mainReply.getHitStatusList().getHitStatus();
        sbMsg.append("命中:");
        for (int ii = 0; ii < hits.size(); ii++) {
          String hit = hits.get(ii).toString().trim();
          sbMsg.append(hit).append(";");
        }
      } else {
        sbMsg.append(mainReply.getMessage().toString());
      }

    } else {
      sbMsg.append("Lyods Null");
    }

    System.out.println(">>>getLyodsHits End");
    return sbMsg.toString();
  }

  /**
   * 不適用
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result insNotUse(int[] noUseAML, RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";

    for (int ii = 0; ii < noUseAML.length; ii++) {
      String amlNo = "";
      if (noUseAML[ii] < 10) {
        amlNo = "00" + noUseAML[ii];
      } else {
        amlNo = "0" + noUseAML[ii];
      }
      aml.setAMLNo(amlNo);
      aml.setErrMsg("不適用");

      this.insSale070(cBean);
    }

    rs.setData(rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 5.代理人非二等親
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result chkAML005(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    String agentRel = cBean.getAgentRel();
    aml.setAMLNo("005");

    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim();
    rsMsg = amlDesc.replaceAll("<customName>", cBean.getCustomName()).replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("<customName2>", cBean.getCustomName2())
        .replaceAll("<customTitle2>", cBean.getCustTitle2());

    if (StringUtils.equals(agentRel, "朋友") || StringUtils.equals(agentRel, "其他")) {
      aml.setErrMsg(rsMsg);
      this.insCR400(cBean);
    } else {
      // 不符合
      aml.setErrMsg("不符合");
    }
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);

    return rs;
  }

  /**
   * 8.有代理人
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result chkAML008(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("008");

    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim();
    rsMsg = amlDesc.replaceAll("<customName>", cBean.getCustomName()).replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("<customName2>", cBean.getCustomName2())
        .replaceAll("<customTitle2>", cBean.getCustTitle2());

    aml.setErrMsg(rsMsg);
    this.insCR400(cBean);
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 9. 資恐地區
   * 
   * @param aml
   * @param keyNo
   * @param type
   * @return
   * @throws Throwable
   */
  public Result chkAML009(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("009");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(ResultStatus.NODATA_AMLMSG[0]));
      rs.setReturnMsg(ResultStatus.NODATA_AMLMSG[3]);
      return rs;
    }

    String sql = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + cBean.getCountryName() + "' ";
    String[][] ret009 = db400.queryFromPool(sql);
    aml.setErrMsg("不符合");
    if (ret009.length > 0) {
      String strCZ07 = ret009[0][0].trim();
      if ("優先法高".equals(strCZ07)) {
        rsMsg = amlDesc.replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("customName", cBean.getCustomName());

        // As400 符合
        aml.setErrMsg(rsMsg);
        this.insCR400(cBean);
      }
    }
    // 無論hit與否都要insert Sale05M070
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 12.合約第三人
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result chkAML012(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("012");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    rsMsg = amlDesc.replaceAll("<customName>", cBean.getCustomName()).replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("<customName2>", cBean.getCustomName2())
        .replaceAll("<customTitle2>", cBean.getCustTitle2());
    aml.setErrMsg(rsMsg);
    this.insCR400(cBean);
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 17.黑名單 (包含控管)
   * 
   * @param RiskCustomBean
   * @return
   * @throws Throwable
   */
  public Result chkAML017(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("017");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(ResultStatus.NODATA_AMLMSG[0]));
      rs.setReturnMsg(ResultStatus.NODATA_AMLMSG[3]);
      return rs;
    }

    aml.setErrMsg("不符合");
    if (StringUtils.equals(cBean.getbStatus(), "Y") || StringUtils.equals(cBean.getbStatus(), "Y")) {
      rsMsg = amlDesc.replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("customName", cBean.getCustomName());

      // As400 符合
      aml.setErrMsg(rsMsg);
      this.insCR400(cBean);
    }
    // 無論hit與否都要insert Sale05M070
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  // 18. 制裁名單 X181
  public Result chkAML018_San(RiskCustomBean cBean) throws Throwable {
    System.out.println(">>>chkAML018_San Start");
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    aml.setAMLNo("018");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setRsStatus(ResultStatus.NODATA_AMLMSG);
      return rs;
    }

    String custTitle = cBean.getCustTitle();
    String custNo = cBean.getCustomNo();
    String custName = cBean.getCustomName();

    // Lyods GO
    this.aml.setRiskResult("N"); // 比對 沒計算
    this.aml.setCheckAll("N"); // 只看制裁
    this.aml.setCustBean(cBean);
    System.out.println("AML018 Test1");
    LyodsTools lyodsTools = new LyodsTools(this.aml);
    System.out.println("AML018 Test2");
    Result result = lyodsTools.checkRisk();
    System.out.println("AML018 Test3");
    if (result.getRsStatus()[0] != ResultStatus.SUCCESS[0]) {
      System.out.println("chkAML018_San Error>>>" + result.getExp().toString());
      return result;
    }

    MainReply mainReply = (MainReply) result.getData();
    boolean isHit = false;
    if (mainReply != null) {
      if (StringUtils.isBlank(mainReply.getMessage().toString())) {
        List hits = mainReply.getHitStatusList().getHitStatus();
        for (int ii = 0; ii < hits.size(); ii++) {
          String hit = hits.get(ii).toString().trim();
          if (hit.equals(HitStatus.SAN[1])) {
            sbMsg.append(amlDesc.replaceAll("<customTitle>", custTitle).replaceAll("<customName>", custName)).append("<br>");
            isHit = true;
            break;
          }
        }
      } else {
        sbMsg.append(mainReply.getMessage().toString());
      }

      if (isHit) {
        aml.setErrMsg(sbMsg.toString());
        this.insCR400(cBean);
      } else {
        aml.setErrMsg("不符合");
      }
      this.insSale070(cBean);

      rs.setData(sbMsg.toString());
      rs.setRsStatus(ResultStatus.SUCCESS);
    } else {
      rs.setData("Lyods Null");
      rs.setRsStatus(ResultStatus.ERROR);
    }

    System.out.println(">>>chkAML018_San End");
    return rs;
  }

  /**
   * 19.利害關係人
   * 
   * @param RiskCustomBean
   * @return
   * @throws Throwable
   */
  public Result chkAML019(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("019");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(ResultStatus.NODATA_AMLMSG[0]));
      rs.setReturnMsg(ResultStatus.NODATA_AMLMSG[3]);
      return rs;
    }

    aml.setErrMsg("不符合");
    if (StringUtils.equals(cBean.getrStatus(), "Y")) {
      rsMsg = amlDesc.replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("customName", cBean.getCustomName());

      // As400 符合
      aml.setErrMsg(rsMsg);
      this.insCR400(cBean);
    }
    // 無論hit與否都要insert Sale05M070
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  // 21. 政治PEPS X171
  public Result chkAML021_PEPS(RiskCustomBean cBean) throws Throwable {
    System.out.println(">>>chkAML021_PEPS Start");
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    aml.setAMLNo("021");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setRsStatus(ResultStatus.NODATA_AMLMSG);
      return rs;
    }

    String custTitle = cBean.getCustTitle();
    String custNo = cBean.getCustomNo();
    String custName = cBean.getCustomName();

    // Lyods GO
    this.aml.setRiskResult("N"); // 比對 沒計算
    this.aml.setCustBean(cBean);
    LyodsTools lyodsTools = new LyodsTools(this.aml);
    Result result = lyodsTools.checkRisk();
    if (result.getRsStatus()[0] != ResultStatus.SUCCESS[0]) {
      System.out.println("chkAML021_PEPS Error>>>" + result.getExp().toString());
      return result;
    }

    MainReply mainReply = (MainReply) result.getData();
    boolean isHit = false;
    if (mainReply != null) {
      if (StringUtils.isBlank(mainReply.getMessage().toString())) {
        List hits = mainReply.getHitStatusList().getHitStatus();
        for (int ii = 0; ii < hits.size(); ii++) {
          String hit = hits.get(ii).toString().trim();
          if (hit.equals(HitStatus.DPEP[1])) {
            sbMsg.append(amlDesc.replaceAll("<customTitle>", custTitle).replaceAll("<customName>", custName)).append("<br>");
            isHit = true;
            break;
          }
        }
      } else {
        sbMsg.append(mainReply.getMessage().toString());
      }

      if (isHit) {
        aml.setErrMsg(sbMsg.toString());
        this.insCR400(cBean);
      } else {
        aml.setErrMsg("不符合");
      }
      this.insSale070(cBean);

      rs.setData(sbMsg.toString());
      rs.setRsStatus(ResultStatus.SUCCESS);
    } else {
      rs.setData("<<Lyods Return Null>>");
      rs.setRsStatus(ResultStatus.ERROR);
    }

    System.out.println(">>>chkAML021_PEPS End");
    return rs;
  }

  /**
   * 更新關聯人
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result renewRelated(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();

    aml.setCustBean(cBean);
    // Lyods GO
    LyodsTools lyodsTools = new LyodsTools(aml);
    Result result = lyodsTools.renewRelated();
    if (result.getExp() != null) {
      System.out.println("renewRelated Error>>>" + result.getExp().toString());
      return result;
    }

    RenewRelatedReply renewRelatedReply = (RenewRelatedReply) result.getData();
    if (renewRelatedReply.getMessage().length() == 0) {
      rs.setRsStatus(ResultStatus.SUCCESS);
      rs.setData(renewRelatedReply);
    } else {
      rs.setRsStatus(ResultStatus.ERROR);
      rs.setReturnMsg(renewRelatedReply.getMessage());
    }

    return lyodsTools.renewRelated();
  }

  /**
   * 寫Sale log Bean版 Func : 功能項 EX 換名、購屋證明單 RecordType : 功能項細項 EX 客戶資料、代理人資料
   * ActionName : 新增、修改、刪除 errMsg : 符合的樣態內容，或為 "不適用" or "不符合" AMLNo : AML樣態編號
   */
  public String insSale070(RiskCustomBean cBean) throws Throwable {
    String rsMsg = "0";
    String sql = "INSERT INTO Sale05M070 "
        + "(DocNo,OrderNo, ContractNo, ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate, EDate, CDate, SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
        + "VALUES " + "('" + aml.getDocNo() + "','" + aml.getOrderNo() + "', '" + aml.getContractNo() + "', '" + strProjectID1 + "','" + intRecordNo + "','" + strActionNo + "', "
        + "'" + aml.getFunc() + "' " + ",'" + aml.getRecordType() + "','" + strActionName + "','" + aml.getErrMsg() + "', " + "'" + cBean.getCustomNo() + "','"
        + cBean.getCustomName() + "' " + ",'" + aml.getOrderDate() + "' ,'" + aml.geteDate() + "' ,'" + aml.getcDate() + "' " + ",'RY','773','" + aml.getAMLNo() + "','"
        + aml.getErrMsg() + "', " + "'" + empNo + "', '" + rocNowDate + "', '" + strNowTime + "') ";
    dbSale.execFromPool(sql);
    intRecordNo++;
    return rsMsg;
  }

  public String insCR400(RiskCustomBean cBean) throws Throwable {
    String pKey = "0";
    String amlNo = aml.getAMLNo(); // 不同功能下放的欄位有所不同
    if ("001".equals(amlNo) || "002".equals(amlNo) || "003".equals(amlNo) || "004".equals(amlNo)) {
      pKey = aml.getDocNo();
    } else if ("018".equals(amlNo) || "021".equals(amlNo)) {
      pKey = aml.getOrderNo();
    }

    String rsMsg = "";
    String sql = "INSERT INTO PSHBPF " + "(SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) " + "VALUES " + "('RY', '" + pKey + "', '" + rocNowDate
        + "', '" + cBean.getCustomNo() + "', '" + cBean.getCustomName() + "'" + ", '773', '" + aml.getAMLNo() + "', " + "'" + aml.getErrMsg() + "','" + empNo + "','" + rocNowDate
        + "','" + strNowTime + "') ";
    db400.execFromPool(sql);
    return rsMsg;
  }

  // 取得AML態樣說明
  public Map getAMLDesc() {
    return mapAMLMsg;
  }

}
