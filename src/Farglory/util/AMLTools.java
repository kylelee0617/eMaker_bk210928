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
package Farglory.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jcx.db.talk;
import jcx.jform.bvalidate;

/**
 * 洗錢各態樣查詢 1. 初版(態樣18 & 態樣21)還沒用eclipse開發，使用bean裝載變數不方便，故先用map。 2.
 * 第二版因為放在eclipse中開發了，故使用AMLBean做參數傳遞處理。 3. 期望我或後手有時間把初版的模式改變一下。
 * 
 * @author B04391
 *
 */

public class AMLTools extends bvalidate {
  // DB
  talk db400 = null;
  talk dbSale = null;
  talk dbEIP = null;
  talk dbPW0D = null;

  KUtils kutil = new KUtils();
  ResultStatus rsStat = new ResultStatus();

  // param 傳入值
  StringBuilder sbRsMsg = new StringBuilder();
  String strDocNo = "";
  String strOrderNo = ""; // 購屋證明單編號
  String strProjectID1 = ""; // 案別代碼
  String strOrderDate = ""; // 購屋證明單日期
  String strActionName = "存檔"; // 存Sale05M070使用
  String strActionNo = ""; // 存Sale05M070使用
  String funcName = ""; // 收款單時會先傳入

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

  AMLBean aml;
  Map mapCustomers = new HashMap();
  List orderList = null;
  List customNoList = null;

  public AMLTools() throws Throwable {
  }

  public AMLTools(AMLBean aml) throws Throwable {
    db400 = getTalk("400CRM");
    dbSale = getTalk("Sale");
    dbEIP = getTalk("EIP");
    dbPW0D = getTalk("pw0d");

    this.aml = aml;
//    strDocNo = aml.getDocNo();
//    strOrderNo = aml.getOrderNo();
    strProjectID1 = aml.getProjectID1();
    strOrderDate = aml.getTrxDate();
    strActionName = aml.getActionName();
    funcName = aml.getFuncName();
    orderList = new ArrayList(Arrays.asList(aml.getOrderNos().split(",")));
    customNoList = new ArrayList(Arrays.asList(aml.getCustomNos().split(",")));

    // LOG日期,時間
    this.getDateTime();

    // 員工編號 & EIPNO
    userNo = getUser().toUpperCase().trim();
    this.getEmpNo();

    // 序號
    this.getRecordNo070ByType(aml);

    // actionNo
    this.getActionNo();

    // 取得AML態樣中文說明
    this.getAML();

    // 取得本收款單所有客戶ID姓名對應
    this.getCustomers();
  }

  public AMLTools(Map cons) throws Throwable {
    db400 = getTalk("400CRM");
    dbSale = getTalk("Sale");
    dbEIP = getTalk("EIP");
    dbPW0D = getTalk("pw0d");

    strOrderNo = cons.get("OrderNo") != null ? cons.get("OrderNo").toString().trim() : "";
    strProjectID1 = cons.get("ProjectID1") != null ? cons.get("ProjectID1").toString().trim() : "";
    strOrderDate = cons.get("TrxDate") != null ? cons.get("TrxDate").toString().trim() : "";
    strActionName = cons.get("ActionName") != null ? cons.get("ActionName").toString().trim() : "";
    funcName = cons.get("funcName") != null ? cons.get("funcName").toString().trim() : "";

    // LOG日期,時間
    this.getDateTime();

    // 員工編號 & EIPNO
    userNo = getUser().toUpperCase().trim();
    this.getEmpNo();

    // 序號
    this.getRecordNo070();

    // actionNo
    this.getActionNo();

    // 取得AML態樣中文說明
    this.getAML();
  }

  public void getCustomers() throws Throwable {
    String sql = "select CustomNo , CustomName from sale05M084 where DocNo = '" + this.aml.getDocNo() + "' ";
    String[][] retQuery = dbSale.queryFromPool(sql);
    if (retQuery.length > 0) {
      for (int i = 0; i < retQuery.length; i++) {
        mapCustomers.put(retQuery[i][0].trim(), retQuery[i][1].trim());
      }
    }
  }

  public Map getAMLReTurn() throws Throwable {
    Map rsMap = new HashMap();
    String sql = "select * from saleRY773 where AMLType = 'AML' order by AMLNo asc";
    String[][] retAML = dbSale.queryFromPool(sql);
    for (int i = 0; i < retAML.length; i++) {
      String[] retAML1 = retAML[i];
      rsMap.put(retAML1[1].trim(), retAML1[2].trim());
    }
    return rsMap;
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

  // 能依照功能切換版本
  public void getRecordNo070ByType(AMLBean aml) throws Throwable {
    String stringSQL = "";
    if ("收款".equals(aml.getFuncName()) && !"".equals(aml.getDocNo())) {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE DocNo ='" + aml.getDocNo() + "' ";
    } else {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + aml.getOrderNo() + "' ";
    }

    String[][] ret05M070 = dbSale.queryFromPool(stringSQL);
    if (ret05M070.length > 0 && !"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
  }

  public void getRecordNo070() throws Throwable {
    String[][] ret05M070;
    String stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + this.strOrderNo + "' ";
    ret05M070 = dbSale.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
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
    String stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + this.userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    } else {
      System.out.println(">>>None EmpNo<<<");
    }
  }

  public boolean check(String value) throws Throwable {
    return false;
  }

  /**
   * AML 001
   * 同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。(收款本日所有繳款)
   * 觸發條件: 必定觸發 查詢條件包含45~49判斷，程式無須再處理
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML001(AMLBean aml, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("001");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    System.out.println("AML01 start>>>");
    String shareCondition = "";
    // 訂單
    if ("order".equals(type)) {
      aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
      aml.setCustomNames(aml.getCustomNames());
      shareCondition = "and a.EDate = '" + aml.getTrxDate() + "' and c.OrderNo in (" + aml.getOrderNos() + ") ";
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'cash' , 'cash no' as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(a.CashMoney , 0) as money ");
      sbSQL.append(", ISNULL(a.CashMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) as oneMoney ");
      sbSQL.append("from Sale05M080 a , sale05m086 c ");
      sbSQL.append("where 1=1 and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(a.CashMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and 499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("UNION ");
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'credit' , b.CreditCardNo as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(b.CreditCardMoney, 0) as money ");
      sbSQL.append(", ISNULL(b.CreditCardMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  as oneMoney ");
      sbSQL.append("from Sale05M080 a  , Sale05M083 b , sale05m086 c ");
      sbSQL.append("where b.DocNo = a.DocNo and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(b.CreditCardMoney, 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  BETWEEN 450000 and 499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("UNION ");
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'bank' , b.BankNo as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(b.BankMoney, 0) as money ");
      sbSQL.append(", ISNULL(b.BankMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  as oneMoney ");
      sbSQL.append("from Sale05M080 a  , Sale05M328 b , sale05m086 c ");
      sbSQL.append("where b.DocNo = a.DocNo and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(b.BankMoney, 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and  499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("UNION ");
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'check' , b.CheckNo as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(b.CheckMoney, 0) as money ");
      sbSQL.append(", ISNULL(b.CheckMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  as oneMoney ");
      sbSQL.append("from Sale05M080 a  , Sale05M082 b , sale05m086 c ");
      sbSQL.append("where b.DocNo = a.DocNo and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(b.CheckMoney, 0)/(select count(e.OrderNo) from Sale05M086 e where e.DocNo = a.DocNo group by e.DocNo) BETWEEN 450000 and 499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("order by a.DocNo desc ,a.EDate DESC ");
      String[][] retAML0011 = dbSale.queryFromPool(sbSQL.toString(), 300);
      int count1 = 0;
      String lastKey1 = "";
      for (int i = 0; i < retAML0011.length; i++) {
        String thisKey1 = retAML0011[i][0].trim();
        if (lastKey1.equals(thisKey1)) { // 相同即表示第二筆
          if (count1 >= 1)
            continue; // 訊息一次就好
          String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames());
          sbMsg.append(msg).append("\n");
          aml.setErrMsg(msg);
          count1++;

          // 寫符合紀錄
          aml.setOrderNo(thisKey1);
          this.insSale070(aml);
          this.insCR400(aml);

          // 從清單中移除符合的編號
          orderList.remove("'" + thisKey1 + "'");
        } else {
          count1 = 0;
        }
        lastKey1 = thisKey1;
      }

      // 寫不符合紀錄
      for (int i = 0; i < orderList.size(); i++) {
        aml.setOrderNo(orderList.get(i).toString().replaceAll("'", ""));
        aml.setErrMsg("不符合");
        this.insSale070(aml);
      }
    } // End type = order

    // 客戶部分
    if ("custom".equals(type)) {
      aml.setOrderNo("");
      shareCondition = "and a.EDate = '" + aml.getTrxDate() + "' and c.CustomNo in (" + aml.getCustomNos() + ") ";
      String sql = "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'cash' " + ", ISNULL(a.CashMoney , 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.CashMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 " + "from Sale05M080 a , sale05m084 c  "
          + "where 1=1 and a.DocNo=c.DocNo  "
          + "and (ISNULL(a.CashMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) "
          + shareCondition + "UNION " + "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'credit' " + ", ISNULL(b.CreditCardMoney, 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.CreditCardMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 "
          + "from Sale05M080 a  , Sale05M083 b , sale05m084 c " + "where b.DocNo = a.DocNo and a.DocNo=c.DocNo "
          + "and (ISNULL(b.CreditCardMoney, 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) "
          + shareCondition + "UNION " + "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'bank' " + ", ISNULL(b.BankMoney, 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.BankMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 "
          + "from Sale05M080 a  , Sale05M328 b , sale05m084 c " + "where b.DocNo = a.DocNo and a.DocNo=c.DocNo "
          + "and (ISNULL(b.BankMoney, 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and  499999) "
          + shareCondition + "UNION " + "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'check' " + ", ISNULL(b.CheckMoney, 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.CheckMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 "
          + "from Sale05M080 a  , Sale05M082 b , sale05m084 c " + "where b.DocNo = a.DocNo and a.DocNo=c.DocNo "
          + "and (ISNULL(b.CheckMoney, 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) "
          + shareCondition + "order by c.CustomNo desc ,a.EDate DESC ";
      String[][] retAML0012 = dbSale.queryFromPool(sql, 300);
      int count2 = 0;
      String lastKey2 = "";
      for (int i = 0; i < retAML0012.length; i++) {
        String thisKey2 = retAML0012[i][0].trim();
        if (lastKey2.equals(thisKey2)) { // 相同即表示第二筆
          if (count2 >= 1)
            continue; // 訊息一次就好
          String msg = amlDesc.replaceAll("<customName>", retAML0012[i][1].trim());
          sbMsg.append(msg).append("\n");
          aml.setErrMsg(msg);
          count2++;

          // 寫符合紀錄
          aml.setCustomId(thisKey2);
          aml.setCustomName(retAML0012[i][1].trim());
          this.insSale070(aml);
          this.insCR400(aml);

          // 從清單中移除符合的編號
          customNoList.remove("'" + thisKey2 + "'");
        } else {
          count2 = 0;
        }

        lastKey2 = thisKey2;
      }

      // 寫不符合紀錄
      for (int i = 0; i < customNoList.size(); i++) {
        String noMatchId = customNoList.get(i).toString().replaceAll("'", "");
        aml.setCustomId(noMatchId);
        aml.setCustomName(mapCustomers.get(noMatchId).toString());
        aml.setErrMsg("不符合");
        this.insSale070(aml);
      }
    } // End of type = custom

    // 重置名單
    orderList = new ArrayList(Arrays.asList(aml.getOrderNos().split(",")));
    customNoList = new ArrayList(Arrays.asList(aml.getCustomNos().split(",")));

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }

  /**
   * Kyle AML 002 同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 觸發條件:
   * 收款單本身命中一次45~49萬需執行檢核。 查詢前兩日再命中一次即產生態樣。 2020/11/26 :
   * 發現訂單跟客戶應該要分開處理(因為在收款單那邊就要拆分訂單跟個人是否命中)，客戶部分已經寫好就懶得改了!!
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML002(AMLBean aml, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("002");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    // 因為第一次命中應該是收款單本身，故只要找前兩天即可
    String startEDate = kutil.getDateAfterNDays(aml.getTrxDate().trim(), "/", -2);
    String endEDate = kutil.getDateAfterNDays(aml.getTrxDate().trim(), "/", -1);

    System.out.println("AML02 start>>>");
    // 訂單
    aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
    aml.setCustomNames(aml.getCustomNames());
    if ("order".equals(type)) {
      sbSQL.append("SELECT c.OrderNo ,a.EDate ");
      sbSQL.append(", SUM(a.CashMoney)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) as CashMoney ");
      sbSQL.append(", SUM(a.BankMoney)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) as BankMoney ");
      sbSQL.append("FROM Sale05M080 a , sale05m086 c ");
      sbSQL.append("WHERE 1=1 and a.DocNo=c.DocNo ");
      sbSQL.append("and a.EDate BETWEEN '" + startEDate + "' AND '" + endEDate + "' and c.OrderNo in (" + aml.getOrderNos() + ") ");
      sbSQL.append("GROUP BY c.OrderNo ,a.EDate , a.DocNo ,a.CashMoney ,a.CreditCardMoney ,a.BankMoney ,a.CheckMoney ");
      sbSQL.append("HAVING ( (ISNULL(a.CashMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and 499999) ");
      sbSQL.append("or (ISNULL(a.BankMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and 499999) ) ");
      sbSQL.append("ORDER BY c.OrderNo DESC ,a.EDate DESC ");
      String[][] retAML0021 = dbSale.queryFromPool(sbSQL.toString(), 300);
      String lastKey1 = "";
      for (int i = 0; i < retAML0021.length; i++) {
        String thisKey1 = retAML0021[i][0].trim();
        if (lastKey1.equals(thisKey1))
          continue; // 重複的不要

        // 處理命中訊息
        String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames());
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // 寫符合紀錄
        aml.setOrderNo(thisKey1);
        this.insSale070(aml);
        this.insCR400(aml);

        // 從清單中移除符合的編號
        orderList.remove("'" + thisKey1 + "'");

        lastKey1 = thisKey1;
      }

      // 寫不符合紀錄
      for (int i = 0; i < orderList.size(); i++) {
        aml.setOrderNo(orderList.get(i).toString().replaceAll("'", ""));
        aml.setErrMsg("不符合");
        this.insSale070(aml);
      }
    }

    // 客戶
    aml.setOrderNo("");
    if ("custom".equals(type)) {
      sbSQL.append("select c.CustomNo ,c.CustomName ,a.EDate ");
      sbSQL.append(", SUM(a.CashMoney)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 as CashMoney ");
      sbSQL.append(", SUM(a.BankMoney)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 as BankMoney ");
      sbSQL.append("from Sale05M080 a , sale05m084 c ");
      sbSQL.append("where 1=1 and a.DocNo=c.DocNo ");
      sbSQL.append("and a.EDate BETWEEN '" + startEDate + "' AND '" + endEDate + "' and c.CustomNo = '" + aml.getCustomId() + "' ");
      sbSQL.append("GROUP BY a.DocNo ,a.EDate , c.CustomNo ,c.CustomName ,a.CashMoney ,a.CreditCardMoney ,a.BankMoney ,a.CheckMoney ");
      sbSQL.append(
          "HAVING ((ISNULL(a.CashMoney , 0)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) ");
      sbSQL.append(
          "or (ISNULL(a.BankMoney , 0)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) ) ");
      sbSQL.append("ORDER BY c.CustomNo DESC ,a.EDate DESC ");
      String[][] retAML0022 = dbSale.queryFromPool(sbSQL.toString(), 300);
      String lastKey2 = "";
      for (int i = 0; i < retAML0022.length; i++) {
        String thisKey2 = retAML0022[i][0].trim();
        if (lastKey2.equals(thisKey2))
          continue; // 重複的不要

        // 處理命中訊息
        String msg = amlDesc.replaceAll("<customName>", retAML0022[i][1].trim());
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // 寫符合紀錄
        aml.setCustomId(thisKey2);
        aml.setCustomName(retAML0022[i][1].trim());
        this.insSale070(aml);
        this.insCR400(aml);

        // 從清單中移除符合的編號
        customNoList.remove("'" + thisKey2 + "'");

        lastKey2 = thisKey2;
      }

      // 寫不符合紀錄
      for (int i = 0; i < customNoList.size(); i++) {
        String noMatchId = customNoList.get(i).toString().replaceAll("'", "");
        aml.setCustomId(noMatchId);
        aml.setCustomName(mapCustomers.get(noMatchId).toString());
        aml.setErrMsg("不符合");
        this.insSale070(aml);
      }
    }

    // 重置名單
    orderList = new ArrayList(Arrays.asList(aml.getOrderNos().split(",")));
    customNoList = new ArrayList(Arrays.asList(aml.getCustomNos().split(",")));

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }

  /**
   * AML 0031 同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。 觸發條件 :
   * 收款單當下有收現金，且昨日、前日未命中大額(AML003) 查詢訂單或單一客戶三日內收款累計，超過50萬產生態樣
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML0031(AMLBean aml, String keyNo, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("003");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    //設定日期範圍
    String startEDate = aml.getTrxDate().trim();
    String endEDate = aml.getTrxDate().trim();

    System.out.println("AML031 start>>>");
    System.out.println("AML031 日期範圍>>>" + startEDate + " - " + endEDate);
    System.out.println("AML031 訂單號碼>>>" + aml.getOrderNos()); // sql in 格式
    System.out.println("AML031 客戶IDS>>>" + aml.getCustomNos()); // sql in 格式
    System.out.println("AML031 客戶NAMES>>>" + aml.getCustomNames()); // name、name、name

    // 訂單
    aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
    aml.setCustomNames(aml.getCustomNames());
    if ("order".equals(type)) {
      String orderNo = keyNo;
      System.out.println("AML031 訂單>>>"+ orderNo);

      String[][] retO = this.getOrderDayPayA4(startEDate, endEDate, orderNo);
      float dayAmt = 0; // 每日累計
      for (int i = 0; i < retO.length; i++) {
        String keyDate = retO[i][3].toString().trim();
        float amt = Float.parseFloat(retO[i][2].toString().trim());
        dayAmt += amt;
        System.out.println("AML031 日期>>>" + keyDate + "// 金額>>>" + amt);
      }
      System.out.println("AML031 日期>>>" + aml.getTrxDate() + "// 單日累計>>>" + dayAmt);

      if (dayAmt >= 500000) {
        // 單日累計達標，產生態樣
        String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames() );
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // 寫符合紀錄
        aml.setOrderNo(orderNo);
        this.insSale070(aml);
        this.insCR400(aml);

        // 從清單中移除符合的編號
        customNoList.remove("'" + orderNo + "'");
      }

      // TODO: 寫不符合態樣
    }

    // 客戶
    aml.setOrderNo("");
    if ("custom".equals(type)) {
      String[] arrCustNos = aml.getCustomNos().split(",");
      String[] arrCustNames = aml.getCustomNames().split("、");
      int ii = 0;
      for (ii = 0; ii < arrCustNos.length; ii++) {
        if (keyNo.equals(arrCustNos[ii].toString().trim().replaceAll("'", "")))
          break; // 找本ID在陣列中的位置
      }
      String custName = arrCustNames[ii].toString().trim();
      String custNo = arrCustNos[ii].toString().trim().replaceAll("'", "");
      System.out.println("AML031 客戶>>>" + custNo+custName);

      String[][] retC = this.getCustDayPayA4(startEDate, endEDate, custNo);
      float dayAmt = 0; // 每日累計
      for (int i = 0; i < retC.length; i++) {
        String keyDate = retC[i][3].toString().trim();
        float amt = Float.parseFloat(retC[i][2].toString().trim());
        dayAmt += amt;
        System.out.println("AML031 日期>>>" + keyDate + "// 金額>>>" + amt);
      }
      System.out.println("AML031 日期>>>" + aml.getTrxDate() + "// 單日累計>>>" + dayAmt);

      if (dayAmt >= 500000) {
        //累計達標
        String msg = amlDesc.replaceAll("<customName>", custName);
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // 寫符合紀錄
        aml.setCustomId(custNo);
        aml.setCustomName(custName);
        this.insSale070(aml);
        this.insCR400(aml);

        // 從清單中移除符合的編號
        customNoList.remove("'" + custNo + "'");
      }

      // TODO: 寫不符合態樣
    }

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }

  /**
   * AML 0041 同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。 觸發條件 :
   * 收款單當下有收現金，且昨日、前日未命中大額(AML003) 查詢訂單或單一客戶三日內收款累計，超過50萬產生態樣
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML0041(AMLBean aml, String keyNo, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("004");

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    String startEDate = kutil.getDateAfterNDays(aml.getTrxDate().trim(), "/", -2);
    String endEDate = aml.getTrxDate().trim();

    System.out.println("AML041 start>>>");
    System.out.println("AML041 日期範圍>>>" + startEDate + " AND " + endEDate);
    System.out.println("AML041 訂單號碼>>>" + aml.getOrderNos()); // sql in 格式
    System.out.println("AML041 客戶IDS>>>" + aml.getCustomNos()); // sql in 格式
    System.out.println("AML041 客戶NAMES>>>" + aml.getCustomNames()); // name、name、name

    // 訂單
    aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
    aml.setCustomNames(aml.getCustomNames());
    if ("order".equals(type)) {
      String orderNo = keyNo;
      System.out.println("AML041 訂單>>>"+ orderNo);

      String[][] retAML0041 = this.getOrderDayPayA4(startEDate, endEDate, orderNo);
      float totalAmt = 0; // 累計總額
      float dayAmt = 0; // 每日累計
      String lastKey = "";
      for (int i = 0; i < retAML0041.length; i++) {
        String keyDate = retAML0041[i][3].toString().trim();
        float amt = Float.parseFloat(retAML0041[i][2].toString().trim());

        // key不同 每日加進總額後歸零
        if (!lastKey.equals(keyDate)) {
          System.out.println("AML041 日期小記" + keyDate + ">>>" + dayAmt);
          totalAmt += dayAmt;
          dayAmt = 0;
        }

        dayAmt += amt;
        System.out.println("AML041 日期>>>" + keyDate + "// 金額>>>" + amt);

        // 每日累計達大額標準，退出流程(三日內有大額即不顯示累計)
        if (dayAmt >= 500000) {
          System.out.println("AML041 本日大額" + keyDate + ">>>，流程結束");
          break;
        }

        // 最後一筆 = 每日加回總額
        if (i == retAML0041.length - 1)
          totalAmt += dayAmt;

        lastKey = keyDate;
      }
      System.out.println("AML041 三日累計>>>" + totalAmt);

      if (totalAmt >= 500000) {
        // 三日累計達標，產生態樣
        String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames());
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // 寫符合紀錄
        aml.setOrderNo(orderNo);
        this.insSale070(aml);
        this.insCR400(aml);

        // 從清單中移除符合的編號
        customNoList.remove("'" + orderNo + "'");
      }

      /*
       * 感覺會有問題，先不寫 
       * //寫不符合紀錄 
       * for (int i = 0; i < orderList.size(); i++) {
       * aml.setOrderNo(orderList.get(i).toString().replaceAll("'", ""));
       * aml.setErrMsg("不符合"); this.insSale070(aml); }
       */
    }

    // 客戶
    aml.setOrderNo("");
    if ("custom".equals(type)) {
      String[] arrCustNos = aml.getCustomNos().split(",");
      String[] arrCustNames = aml.getCustomNames().split("、");
      int ii = 0;
      for (ii = 0; ii < arrCustNos.length; ii++) {
        if (keyNo.equals(arrCustNos[ii].toString().trim().replaceAll("'", "")))
          break; // 找本ID在陣列中的位置
      }
      String custName = arrCustNames[ii].toString().trim();
      String custNo = arrCustNos[ii].toString().trim().replaceAll("'", "");
      System.out.println("AML041 客戶>>>"+custNo+custName);

      String[][] retCustA4 = this.getCustDayPayA4(startEDate, endEDate, custNo);
      float totalAmt = 0; // 累計總額
      String lastKey = "";
      float dayAmt = 0; // 每日累計
      for (int i = 0; i < retCustA4.length; i++) {
        String keyDate = retCustA4[i][3].toString().trim();
        float amt = Float.parseFloat(retCustA4[i][2].toString().trim());

        // key不同 每日加進總額後歸零
        if (!lastKey.equals(keyDate)) {
          totalAmt += dayAmt;
          dayAmt = 0;
        }

        dayAmt += amt;
        System.out.println("AML041 日期>>>" + keyDate + "// 金額>>>" + amt);

        // 每日累計達大額標準，退出流程(三日內有大額即不顯示累計)
        if (dayAmt >= 500000) {
          System.out.println("AML041 本日大額" + keyDate + ">>>，流程結束");
          break;
        }

        // 最後一筆 = 每日加回總額
        if (i == retCustA4.length - 1)
          totalAmt += dayAmt;

        lastKey = keyDate;
      }
      System.out.println("AML041 三日累計>>>" + totalAmt);

      if (totalAmt >= 500000) {
        // 三日累計達標，產生態樣
        String msg = amlDesc.replaceAll("<customName>", custName);
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // 寫符合紀錄
        aml.setCustomId(custNo);
        aml.setCustomName(custName);
        this.insSale070(aml);
        this.insCR400(aml);

        // 從清單中移除符合的編號
        customNoList.remove("'" + custNo + "'");
      }

      /*
       * 感覺會有問題，先不寫 
       * //寫不符合紀錄 
       * for (int i = 0; i < customNoList.size(); i++) { String
       * noMatchId = customNoList.get(i).toString().replaceAll("'", "");
       * aml.setCustomId(noMatchId);
       * aml.setCustomName(mapCustomers.get(noMatchId).toString());
       * aml.setErrMsg("不符合"); this.insSale070(aml); }
       */
    }

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }
  
  public String[][] getOrderDayPayA4(String sDate, String eDate, String orderNo) throws Throwable {
    StringBuilder sbSQL = new StringBuilder();
    sbSQL.append("select c.OrderNo , a.DocNo ");
    sbSQL.append(", (a.CashMoney/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)) as CashMoney ");
    sbSQL.append(", a.EDate ");
    sbSQL.append("from Sale05M080 a , sale05m086 c where 1=1 and a.DocNo=c.DocNo ");
    sbSQL.append("and a.EDate BETWEEN '" + sDate + "' AND '" + eDate + "' and c.OrderNo = '" + orderNo + "' ");
    sbSQL.append("ORDER BY c.OrderNo DESC , a.edate DESC ");
    String[][] ret = dbSale.queryFromPool(sbSQL.toString());
    return ret;
  }

  public String[][] getCustDayPayA4(String sDate, String eDate, String custNo) throws Throwable {
    StringBuilder sbSQL = new StringBuilder();
    sbSQL.append("select c.CustomNo ,c.CustomName ");
    sbSQL.append(", (a.CashMoney * (select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100) as CashMoney ");
    sbSQL.append(", a.EDate ");
    sbSQL.append("from Sale05M080 a , sale05m084 c where 1=1 and a.DocNo=c.DocNo ");
    sbSQL.append("and a.EDate BETWEEN '" + sDate + "' AND '" + eDate + "' and c.CustomNo = '" + custNo + "' ");
    sbSQL.append("ORDER BY c.CustomNo DESC ,a.edate DESC ");
    String[][] ret = dbSale.queryFromPool(sbSQL.toString());
    return ret;
  }

  // 21. 政治PEPS X171
  public String chkX171_PEPS(Map cons) throws Throwable {
    cons.put("AMLNo", "021");
    String rsMsg = "";

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(cons.get("AMLNo").toString().trim()) == null ? "" : mapAMLMsg.get(cons.get("AMLNo").toString().trim()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rsMsg = "[Error : 查無此樣態說明]";
      return rsMsg;
    }
    // System.out.println("amlDesc>>>" + amlDesc);

    String sql = "SELECT * FROM CRCLNAPF " + "WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L "
        + "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE " + "AND L.CONTROLCLASSIFICATIONCODE ='X171' " + "AND C.REMOVEDDATE >= '" + strNowDate2 + " " + strNowTime2
        + "' ) " + "AND ISREMOVE = 'N'  " + "AND CUSTOMERID ='" + cons.get("customId") + "' ";
    String[][] retQuery = db400.queryFromPool(sql);

    if (retQuery.length > 0) {
      // 符合樣態
      String[] retQuery1 = retQuery[0];
      String cusId = retQuery1[3].toString().trim();
      String cusName = retQuery1[5].toString().trim();
      String customTitle = cons.get("customTitle").toString();
      System.out.println("cusName>>>" + cusName);
      System.out.println("customTitle>>>" + customTitle);
      rsMsg += (amlDesc.replaceAll("<customTitle>", customTitle).replaceAll("<customName>", cusName) + "<br>");
      cons.put("errMsg", rsMsg);

      this.insSale070(cons);
      this.insCR400(cons);
    } else {
      // 不符合樣態
      cons.put("errMsg", "不符合");
      this.insSale070(cons);
    }

    return rsMsg;
  }

  // 18. 制裁名單 X181
  public String chkX181_Sanctions(Map cons) throws Throwable {
    cons.put("AMLNo", "018");
    String rsMsg = "";

    // 先取得樣態說明
    String amlDesc = mapAMLMsg.get(cons.get("AMLNo").toString().trim()) == null ? "" : mapAMLMsg.get(cons.get("AMLNo").toString().trim()).toString().trim(); // 樣態示警文字
    if ("".equals(amlDesc)) {
      rsMsg = "[Error : 查無此樣態說明]";
      return rsMsg;
    }

    String sql = "SELECT * FROM CRCLNAPF " + "WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L "
        + "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE " + "AND L.CONTROLCLASSIFICATIONCODE ='X181' " + "AND C.REMOVEDDATE >= '" + strNowDate2 + " " + strNowTime2
        + "' ) " + "AND ISREMOVE = 'N'  " + "AND CUSTOMERID ='" + cons.get("customId") + "' ";
    String[][] retQuery = db400.queryFromPool(sql);

    if (retQuery.length > 0) {
      // 符合樣態
      String[] retQuery1 = retQuery[0];
      String cusId = retQuery1[3].toString().trim();
      String cusName = retQuery1[5].toString().trim();
      String customTitle = cons.get("customTitle").toString();
      rsMsg += (amlDesc.replaceAll("<customTitle>", customTitle).replaceAll("<customName>", cusName) + "<br>");
      cons.put("errMsg", rsMsg);

      this.insSale070(cons);
      this.insCR400(cons);
    } else {
      // 不符合樣態
      cons.put("errMsg", "不符合");
      this.insSale070(cons);
    }

    return rsMsg;
  }

  /**
   * 寫Sale log Bean版 
   * funcName(Func) : 功能項 EX 換名、購屋證明單 
   * funcName2(RecordType) : 功能項細項 EX 客戶資料、代理人資料 
   * ActionName(ActionName) : 新增、修改、刪除 
   * errMsg : 符合的樣態內容，或為"不適用" or "不符合" 
   * AMLNo : AML樣態編號
   */
  public String insSale070(AMLBean aml) throws Throwable {
    String rsMsg = "0";
    String sql = "INSERT INTO Sale05M070 "
        + "(DocNo,OrderNo, ContractNo, ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate, EDate, CDate, SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
        + "VALUES " 
        + "('" + aml.getDocNo() + "','" + aml.getOrderNo() + "', '"+ aml.getContractNo()+"', '" + strProjectID1 + "','" + intRecordNo + "','" + strActionNo + "', " + "'" + aml.getFuncName() + "' "
        + ",'"+ aml.getFuncName2() + "','" + strActionName + "','" + aml.getErrMsg() + "', " + "'" + aml.getCustomId() + "','" + aml.getCustomName() + "' "
        + ",'" + aml.getOrderDate() + "' ,'"+aml.geteDate()+"' ,'"+aml.getcDate()+"' "
        + ",'RY','773','" + aml.getAMLNo() + "','" + aml.getErrMsg() + "', " + "'" + empNo + "', '" + rocNowDate + "', '" + strNowTime + "') ";
    dbSale.execFromPool(sql);
    intRecordNo++;
    return rsMsg;
  }

  public String insCR400(AMLBean aml) throws Throwable {
    String pKey = "0";
    String amlNo = aml.getAMLNo(); // 不同功能下放的欄位有所不同
    if ("001".equals(amlNo) || "002".equals(amlNo) || "003".equals(amlNo) || "004".equals(amlNo)) {
      pKey = aml.getDocNo();
    } else if ("018".equals(amlNo) || "021".equals(amlNo)) {
      pKey = aml.getOrderNo();
    }

    String customId = aml.getCustomId().toString().split(",")[0].toString();
    String rsMsg = "";
    String sql = "INSERT INTO PSHBPF " + "(SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) " + "VALUES " + "('RY', '" + pKey + "', '" + rocNowDate
        + "', '" + customId + "', '" + aml.getCustomName() + "'" + ", '773', '" + aml.getAMLNo() + "', " + "'" + aml.getErrMsg() + "','" + empNo + "','" + rocNowDate + "','"
        + strNowTime + "') ";
    db400.execFromPool(sql);
    return rsMsg;
  }

  /**
   * funcName(Func) : 功能項 EX 換名、購屋證明單 funcName2(RecordType) : 功能項2 EX 客戶資料、代理人資料
   * ActionName(ActionName) : 新增、修改、刪除 errMsg : 符合的樣態內容，或為"不適用"、"不符合" AMLNo :
   * AML樣態編號
   */
  public String insSale070(Map cons) throws Throwable {
    String rsMsg = "";
    String sql = "INSERT INTO Sale05M070 "
        + "(OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) " + "VALUES "
        + "('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + strActionNo + "', " + "'" + cons.get("funcName") + "','" + cons.get("funcName2") + "','"
        + strActionName + "','" + cons.get("errMsg") + "', " + "'" + cons.get("customId") + "','" + cons.get("customName") + "','" + strOrderDate + "','RY','773','"
        + cons.get("AMLNo") + "','" + cons.get("errMsg") + "', " + "'" + empNo + "', '" + rocNowDate + "', '" + strNowTime + "') ";

    try {
      dbSale.execFromPool(sql);
    } catch (Exception ex) {
      rsMsg = "[Error : ins070 error]";
      System.out.println(">>>ins070 Error : " + cons);
      ex.printStackTrace();
    }

    return rsMsg;
  }

  public String insCR400(Map cons) throws Throwable {
    String rsMsg = "";
    String sql = "INSERT INTO PSHBPF " + "(SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) " + "VALUES ('RY', '" + strOrderNo + "', '" + rocNowDate
        + "', '" + cons.get("customId") + "', '" + cons.get("customName") + "', '773', '" + cons.get("AMLNo") + "', " + "'" + cons.get("errMsg") + "','" + empNo + "','"
        + rocNowDate + "','" + strNowTime + "') ";

    try {
      db400.execFromPool(sql);
    } catch (Exception ex) {
      rsMsg = "[Error : ins400 error]";
      System.out.println(">>>ins400 Error : " + cons);
      ex.printStackTrace();
    }

    return rsMsg;
  }

  // 取得AML態樣說明
  public Map getAMLDesc() {
    return mapAMLMsg;
  }

}
