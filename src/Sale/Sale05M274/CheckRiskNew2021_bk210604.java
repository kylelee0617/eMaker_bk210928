package Sale.Sale05M274;

import java.util.*;

import javax.swing.JTable;

import org.apache.commons.lang3.StringUtils;

import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCheckTools_Lyods;
import Farglory.aml.RiskCustomBean;
import Farglory.util.*;
import jcx.db.talk;

public class CheckRiskNew2021_bk210604 extends jcx.jform.bproc {

  public String getDefaultValue(String value) throws Throwable {
    System.out.println("Class >>> Sale.Sale05M274.ChekRiskNew");

    talk dbSale = getTalk("Sale");
    talk dbEMail = getTalk("eMail");
    talk db400CRM = getTalk("400CRM");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    talk dbDoc = getTalk("Doc");
    KUtils kUtil = new KUtils();

    // 畫面欄位
    String projectId = getValue("ProjectID1").trim(); // 案別代碼
    String contractDate = getValue("ContractDate").trim(); // 簽約日期
    String contractNo = getValue("ContractNoDisplay").trim(); // 合約編號
    boolean reCheckRisk = false; // 是否重新檢核
    String rsMsg = ""; // 顯示訊息
    String sql = "";
    
    String orderNo = "";
    String orderDate = "";
    String funcName = getFunctionName();
    String recordType = "指定第三人風險值計算結果";
    String actionText = getValue("text11").trim();

    // 訂單資訊
    // 210112 Kyle : 跟珮筑確認過 合約:訂單 = 1:1，所以直接取第一筆 
    String tbOrderName = "table1";
    JTable tbOrder = this.getTable(tbOrderName);
    if(tbOrder.getRowCount() > 0) {
      orderNo = this.getValueAt(tbOrderName, 0, "OrderNo").toString().trim();
      orderDate = kUtil.getOrderDateByOrderNo(orderNo);
    }

    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setDbSale(dbSale);
    aBean.setDbEMail(dbEMail);
    aBean.setDb400CRM(db400CRM);
    aBean.setDbEIP(dbEIP);
    aBean.setEmakerUserNo(getUser());
    aBean.setOrderNo(orderNo);
    aBean.setOrderDate(orderDate);
    aBean.setContractNo(contractNo);
    aBean.setProjectID1(projectId);
    aBean.setActionName(actionText);
    aBean.setFunc(funcName);
    aBean.setRecordType(recordType);
    aBean.setUpdSale05M356(true);
    aBean.setUpd070Log(true);
    aBean.setSendMail(true);

    // TODO: 1. 先執行指定第三人風險值判別
    sql = "SELECT DesignatedId, DesignatedName, ExportingPlace  FROM sale05m356 WHERE ContractNo = '" + contractNo + "'";
    String[][] retM356 = dbSale.queryFromPool(sql);
    RiskCustomBean[] cBeans = new RiskCustomBean[retM356.length];
    for (int ii = 0; ii < retM356.length; ii++) {
      String desNo = retM356[ii][0].toString().trim();
      String desName = retM356[ii][1].toString().trim();
      QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, desNo);
      if (qBean == null) {
        rsMsg += "指定第三人" + desName + "查無黑名單資料，請先執行黑名單查詢 \n";
        continue;
      }
      RiskCustomBean cBean = new RiskCustomBean();
      cBean.setCustomNo(desNo);
      cBean.setCustomName(desName);
      cBean.setBirthday(qBean.getBirthday());
      cBean.setIndustryCode(qBean.getJobType());
      cBeans[ii] = cBean;
    }
    RiskCheckTools_Lyods risk = new RiskCheckTools_Lyods(aBean);
    Result rs = risk.processRisk(cBeans);
    RiskCheckRS rcRs = (RiskCheckRS) rs.getData();
    if(StringUtils.isNotBlank(rcRs.getRsMsg())) {
      rsMsg += rcRs.getRsMsg();
    }
    // End of 1.

    // 2. 執行客戶風險值檢核
    long subDays = kUtil.subACDaysRDay(contractDate, orderDate);
    System.out.println("subDays>>>" + subDays);
    if (subDays >= 90)
      reCheckRisk = true; // 付訂日與簽約日超過90日，需重新檢核風險
    
    if(reCheckRisk) {
      sql = "select CustomNo , CustomName , Birthday , IndustryCode from Sale05M091 where orderNO = '" + orderNo + "' and ISNULL(statusCd, '') != 'C' ";
      String[][] retCustom = dbSale.queryFromPool(sql);
      RiskCustomBean[] cBeans2 = new RiskCustomBean[retCustom.length];
      for(int ii=0 ; ii<retCustom.length ; ii++) {
        RiskCustomBean cBean = new RiskCustomBean();
        cBean.setCustomNo(retCustom[ii][0].trim());
        cBean.setCustomName(retCustom[ii][1].trim());
        cBean.setBirthday(retCustom[ii][2].trim());
        cBean.setIndustryCode(retCustom[ii][3].trim());
        cBeans2[ii] = cBean;
      }
      aBean.setRecordType("客戶風險值重新檢核");
      RiskCheckTools_Lyods risk2 = new RiskCheckTools_Lyods(aBean);
      Result rs2 = risk2.processRisk(cBeans);
      RiskCheckRS rcRs2 = (RiskCheckRS) rs2.getData();
      if(StringUtils.isNotBlank(rcRs2.getRsMsg())) {
        rsMsg += rcRs2.getRsMsg();
      }
    }

    messagebox(rsMsg);

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
