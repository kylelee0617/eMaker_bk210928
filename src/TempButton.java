
import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;

import java.util.LinkedHashMap;
import com.fglife.risk.*;
import com.ibm.as400.access.AS400;
import java.text.SimpleDateFormat;

public class TempButton extends bproc {

  public String getDefaultValue(String value) throws Throwable {

    System.out.println("================Send Mail Star================");
    value = "";
    /*
     * email 風險名單資料 
     * A.資料整理 
     *  a1.整理客戶 
     *  a2.整理實質受益人 
     *  a3.整理第三人 
     * B.依名單call 400 抓風險值 
     * C.整理email
     * 內容
     */
    String subject = "不動產簽約客戶風險等級評估結果通知(測試)";
    talk dbSale = getTalk("Sale");
    talk dbEIP = getTalk("EIP");
    talk dbBen = getTalk("400CRM");
    talk dbEMail = getTalk("eMail");
    talk dbPW0D = getTalk("pw0d");
    String userNo = getUser().toUpperCase().trim();

    // 編輯人
    String modifier = "";
    modifier = getUser().toUpperCase().trim();
    String[][] retEip = null;
    String strSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + modifier + "'";
    retEip = dbEIP.queryFromPool(strSQL);
    if (retEip.length > 0) {
      modifier = retEip[0][0];
    }

    String empNo = "";
    String userEmail = "";
    String userEmail2 = "";
    String DPCode = "";
    String DPManageemNo = "";
    String DPeMail = "";
    String DPeMail2 = "";
    String stringSQL = "";

    String[][] reteMail = null;
    String sysdate = new SimpleDateFormat("yyyyMMdd").format(getDate());
    String systime = new SimpleDateFormat("HHmmss").format(getDate());

    ResourceBundle resource = ResourceBundle.getBundle("sale");
    String as400ip = resource.getString("AS400.IP");
    String as400account = resource.getString("AS400.ACCOUNT");
    String as400password = resource.getString("AS400.PASSWORD");
    String as400init = resource.getString("AS400.INIT");
    String cms00c = resource.getString("CMS00C.LIB");
    String blpc00a = resource.getString("BLPC00A.LIB");
    String psri02 = resource.getString("PSRI02.LIB");

    RPGAS400Interface ra = null;
    RPGAS400Interface rb = null;
    RPGAS400Interface rc = null;

    String sysType = "RYB";// 不動產行銷B 銷售C

    // 取畫面值
    String strProjectID1 = getValue("ProjectID1").trim();// 案別代碼
    String strContractDate = getValue("ContractDate").trim();// 簽約日期
    String strContractNoDisplay = getValue("ContractNoDisplay").trim();// 合約編號

    String strPosition = ""; // for title
    String strPosition1 = ""; // for context
    String strPosition2 = "";// for context
    String strCustomName = "";
    String strOrderNo = "";
    String strOrderDate = "";
    String strScore = "";
    String strID = "";
    stringSQL = "SELECT Position,CustomName,OrderNo FROM Sale05M275_New WHERE ContractNo = '" + strContractNoDisplay + "' ";
    reteMail = dbSale.queryFromPool(stringSQL);
    if (reteMail.length == 0) {
      // 沒資料
      value = "M275 客戶資料不存在";
      return value;
    }

    strPosition = reteMail[0][0];
    strCustomName = reteMail[0][1];
    strOrderNo = reteMail[0][2];
    // 案別 + 棟樓別 + 車位別
    subject = strProjectID1 + "案" + strPosition + subject;

    stringSQL = "SELECT OrderDate FROM Sale05M090 WHERE OrderNo = '" + strOrderNo + "' ";
    reteMail = dbSale.queryFromPool(stringSQL);
    strOrderDate = reteMail[0][0];
    ////////////////////////////////////
    System.out.println("strProjectID1===>" + strProjectID1);
    System.out.println("strContractDate===>" + strContractDate);
    System.out.println("strContractNoDisplay===>" + strContractNoDisplay);
    System.out.println("strPosition===>" + strPosition);
    System.out.println("strCustomName===>" + strCustomName);
    System.out.println("strOrderNo===>" + strOrderNo);
    System.out.println("strOrderDate===>" + strOrderDate);
    ArrayList list = new ArrayList();

    try {
      String[][] retBuilding = null;
      stringSQL = "Select HouseCar, Position FROM SALE05M092 WHERE OrderNo = '" + strOrderNo + "' Order By RecordNo ";
      retBuilding = dbSale.queryFromPool(stringSQL);
      int ch = 0;
      int cc = 0;
      if (retBuilding.length == 0) { // 沒資料
        value = "M092 棟樓別資料不存在";
        return value;
      }
      for (int i = 0; i < retBuilding.length; i++) {
        String HouseCar = retBuilding[i][0];
        String Building = retBuilding[i][1];
        if (HouseCar.startsWith("House")) {
          if (ch > 0) {
            strPosition1 = strPosition1 + ",";
          }
          strPosition1 = strPosition1 + Building;
          ch++;
        } else if (HouseCar.startsWith("Car")) {
          if (cc > 0) {
            strPosition2 = strPosition2 + ",";
          }
          strPosition2 = strPosition2 + Building;
          cc++;
        }
      }

      // a1整理客戶
      String[][] retCust = null;
      stringSQL = "SELECT CustomNo, ContractNo FROM SALE05M277 WHERE CONTRACTNO = '" + strContractNoDisplay + "'";
      retCust = dbSale.queryFromPool(stringSQL);
      for (int i = 0; i < retCust.length; i++) {
        ra = new RPGCMS00C(as400ip, as400account, as400password);
        rb = new RPGBLPC00A(as400ip, as400account, as400password);
        rc = new RPGPSRI02(as400ip, as400account, as400password);

        String isManager = "N";
        String type = "N";
        String sex = "";
        String cnyCode = "TWN";
        String birthday = "";
        strID = retCust[i][0];

        System.out.println("strID:" + strID);

        // 由購屋證明單取得客戶
        String[][] retCustom = null;
        stringSQL = "SELECT " + "c.CustomName, c.RiskValue  " + "FROM SALE05M091 c Inner Join SALE05M275_new n On c.OrderNo = n.OrderNo " + "WHERE n.ContractNo = '"
            + strContractNoDisplay + "' And c.CustomNo = '" + strID + "'";
        retCustom = dbSale.queryFromPool(stringSQL);

        if (retCustom.length == 0) { // 沒資料
          value = "M091 客戶資料不存在";
          return value;
        }

        for (int idx1 = 0; idx1 < retCustom.length; idx1++) {
          value += "客戶" + retCustom[idx1][0] + "洗錢風險等級 :" + retCustom[idx1][1] + "\n";
        }

        // if("".equals(value)){
        // value = "客戶" + retCustom[0][0] + "洗錢風險等級 :" + retCustom[0][1];
        // }else{
        // value =value+ "\n客戶" + retCustom[0][0] + "洗錢風險等級 :" + retCustom[0][1];
        // }
      }

      // a3.整理第三人
      System.out.println("!!!!!!a3!!!!!!!");
      stringSQL = "SELECT ContractNo, DesignatedId, DesignatedName, ExportingPlace   FROM sale05m356 WHERE ContractNo = '" + strContractNoDisplay + "'";
      retCust = dbSale.queryFromPool(stringSQL);
      for (int i = 0; i < retCust.length; i++) {
        ra = new RPGCMS00C(as400ip, as400account, as400password);
        rb = new RPGBLPC00A(as400ip, as400account, as400password);
        rc = new RPGPSRI02(as400ip, as400account, as400password);
        System.out.println("3 custno =====> :" + retCust[i][1]);

        // 查QueryLog 補資料
        stringSQL = "select top 1 NATIONAL_ID,QUERY_TYPE,NTCODE,NAME,QUERY_ID,BIRTHDAY,SEX,JOB_TYPE,CITY,TOWN,ADDRESS from QUERY_LOG where QUERY_ID = '" + retCust[i][1]
            + "' And PROJECT_ID='" + strProjectID1 + "' Order by QID Desc";
        String retLog[][] = dbPW0D.queryFromPool(stringSQL);
        String isManager = "N";
        String type = "N";
        String sex = "";
        String cnyCode = "TWN";
        String birthday = "0";
        String iad1 = "";
        String iad2 = "";
        String iadd = "";
        String job = "";
        if (retLog.length == 0) {
          if ("".equals(value)) {
            value = "指定第三人風險評估查無資料";
          } else {
            value = value + "\n指定第三人風險評估查無資料";
          }
          // return value;
        } else {
          cnyCode = retLog[0][2];
          sex = retLog[0][6];
          birthday = retLog[0][5].replace("/", "");
          iad1 = retLog[0][8];
          iad2 = retLog[0][9];
          iadd = retLog[0][10];
          job = retLog[0][7];

          // 身分ID
          if (retCust[i][1].trim().length() == 8) {
            type = "C";// N: 個人 C: 公司 F: 外國人
          } else {
            // 外國人(以國別判斷)
            if (!"中華民國".equals(retCust[i][3].trim())) {
              type = "F";
            }
          }

          // 國籍轉碼(若空預設TWN)
          System.out.println("retCust[i][3]:" + retCust[i][3]);
          String strCZ09 = "";
          if ("".equals(retCust[i][3].trim())) {
            strCZ09 = "中華民國";
            type = "N";
          } else {
            strCZ09 = retCust[i][3].trim();
          }
          String strSaleSql2 = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCZ09 + "'";
          String retCNYCode2[][] = dbBen.queryFromPool(strSaleSql2);
          if (retCNYCode2.length > 0) {
            cnyCode = retCNYCode2[0][0].trim();
          }
          System.out.println("cnyCode=====> :" + cnyCode);

        }
        LinkedHashMap map = new LinkedHashMap();
        map.put("INAME", retCust[i][2]); // 客戶姓名
        map.put("IDATE", birthday);// 生日
        map.put("ID", retCust[i][1]); // 身份證號
        map.put("IAD1", iad1);// 地址 1
        map.put("IAD2", iad2);// 地址 2
        map.put("IAD3", "");// 地址 3
        map.put("IADD", iadd);// 長地址
        map.put("IZIP", "");// 郵遞區號
        map.put("ITELO", "");// 公司電話
        map.put("ITELH", "");// 住家電話
        map.put("TYPE", type);// N: 個人 C: 公司
        map.put("SEX", sex);// 性別 M,F
        map.put("CNY", cnyCode);// 國籍
        map.put("JOB", "");// 職業代碼
        map.put("VOC", "");// 行業別
        map.put("CUST", " ");// 監護宣告
        map.put("IESTD", " "); // 設定日期
        map.put("IEXEC", isManager);// 高階管理人 Y/N
        map.put("CNY2", " ");// 國籍 2
        map.put("CNY3", " ");// 國籍 3
        map.put("ICHGD", "");// 變更登記日期
        map.put("CHGNO", modifier);// 異動人員員編
        map.put("RTCOD", "");// 回覆碼
        map.put("INSN", "");// 客戶編號
        System.out.println("cust:" + retCust[i][0]);

        LinkedHashMap mapb = new LinkedHashMap();
        mapb.put("INSID", retCust[i][1]); // 身份證號
        mapb.put("SYSTEM", sysType); // 系統別
        mapb.put("CHGNO", userNo);// 異動人員員編
        mapb.put("CAPTION", strContractNoDisplay);// 說明
        mapb.put("STRDATE", "0");// 起始日
        mapb.put("ENDDATE", "0");// 終止日
        mapb.put("RTCOD", "");// 回覆碼

        LinkedHashMap mapc = new LinkedHashMap();
        mapc.put("RI0201", retCust[i][1]); // 身份證號
        mapc.put("RI0202", "RY");// 系統別
        mapc.put("RI0203", "Y");
        mapc.put("RIPOLN", "");
        mapc.put("RIFILE", strContractNoDisplay);// 來源案號
        mapc.put("RI0204", "");//
        mapc.put("RI0205", "");//
        mapc.put("RI0206", "");//
        mapc.put("RI0207", "");//
        mapc.put("RI0208", "");//
        mapc.put("RI0209", "");//
        mapc.put("RO0201", "0");//
        mapc.put("RO0202", "");//
        mapc.put("RO0203", "0");//
        mapc.put("RO0204", "");//
        mapc.put("RO0205", "0");//
        mapc.put("RO0206", "");//
        mapc.put("RO0207", "0");//
        mapc.put("RO0208", "");//
        mapc.put("RO0209", "0");//
        mapc.put("RO0210", "");//
        mapc.put("RO0211", retCust[i][1]);//
        mapc.put("RO0212", "");//
        mapc.put("RO0213", "");//
        mapc.put("RTNR02", "");// 回覆碼

        boolean a = ra.invoke(as400init, cms00c, map);
        System.out.println("RTCODE:" + ra.getResult()[22]);
        boolean b = rb.invoke(as400init, blpc00a, mapb);
        System.out.println("RTCODE:" + rb.getResult()[6]);
        boolean c = rc.invoke(as400init, psri02, mapc);
        System.out.println("19洗錢風險值 :" + rc.getResult()[19]);
        System.out.println("20洗錢風險等級 :" + rc.getResult()[20]);
        // 寫回風險值
        // messagebox("指定第三人洗錢風險等級 :" + rc.getResult()[20]);
        String sqlWriteRisk = "Update sale05m356 set RiskValue = '" + rc.getResult()[20] + "' Where ContractNo = '" + strContractNoDisplay + "' and DesignatedId = '"
            + retCust[i][1] + "'";
        dbSale.execFromPool(sqlWriteRisk);

        if ("".equals(value)) {
          value = "指定第三人洗錢風險等級 :" + rc.getResult()[20];
        } else {
          value = value + "\n指定第三人洗錢風險等級 :" + rc.getResult()[20];
        }

        HashMap m = new HashMap();
        m.put("p01", strProjectID1);
        m.put("p02", strPosition1);
        m.put("p025", strPosition2);
        m.put("p03", retCust[i][2]);
        m.put("p035", retCust[i][1]);
        // m.put("p04",strOrderDate);
        m.put("p045", strContractDate);
        m.put("p05", "" + rc.getResult()[19]);
        m.put("p06", (String) rc.getResult()[20]);
        list.add(m);

        ra.disconnect();
        rb.disconnect();
        rc.disconnect();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("!!!!!! send mail!!!!!!!");
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }
    /////////////////
    System.out.println("userNo===>" + userNo);
    System.out.println("empNo===>" + empNo);
    ////////////////
    stringSQL = "SELECT DP_CODE,PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPCode = reteMail[0][0];
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        userEmail = reteMail[0][1];
      }
      if (reteMail[0][2] != null && !reteMail[0][2].equals("")) {
        userEmail2 = reteMail[0][2];
      }
    }
    /////////////////////////////////////////
    System.out.println("DPCode===>" + DPCode);
    System.out.println("userEmail===>" + userEmail);
    System.out.println("userEmail2===>" + userEmail2);
    /////////////////////////////////////////////////
    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + DPCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPManageemNo = reteMail[0][0];
    }
    /////////////////////////////////////////
    System.out.println("DPManageemNo===>" + DPManageemNo);
    /////////////////////////////////////////////////
    stringSQL = "SELECT PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + DPManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      if (reteMail[0][0] != null && !reteMail[0][0].equals("")) {
        DPeMail = reteMail[0][0];
      }
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        DPeMail2 = reteMail[0][1];
      }
    }
    ////////////////////////////////////
    System.out.println("DPeMail===>" + DPeMail);
    System.out.println("DPeMail2===>" + DPeMail2);
    /////////////////////////////////////////////////////////
    String PNCode = "";
    String PNManageemNo = "";
    String PNMail = "";

    stringSQL = "SELECT PN_DEPTCODE FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNCode = reteMail[0][0];
    System.out.println("PNCode===>" + PNCode);
    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + PNCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNManageemNo = reteMail[0][0];
    System.out.println("PNManageemNo===>" + PNManageemNo);
    stringSQL = "SELECT PN_EMAIL1 FROM PERSONNEL WHERE PN_EMPNO='" + PNManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNMail = reteMail[0][0];
    System.out.println("PNMail===>" + PNMail);

    // send email
    String ctop = "<html><head><title>合約會審洗錢風險通知</title></head><body>";
    String header1 = "(1)高風險客戶：</br>";
    String header2 = "(2)低風險客戶：</br>";
    String table1 = "<table border=1><tr><td align=\"center\">案別</td><td align=\"center\">棟樓別</td><td align=\"center\">車位別</td><td align=\"center\">客戶名稱</td><td align=\"center\">簽約日期</td><td align=\"center\">風險綜合值</td><td align=\"center\">客戶風險等級</td><td align=\"center\">說明</td></tr>";
    String tail = "</table>";
    String contextsample = "<tr><td align=\"center\">${p01}</td><td align=\"center\">${p02}</td><td align=\"center\">${p025}</td><td align=\"center\">${p03}</td><td align=\"center\">${p045}</td><td align=\"center\">${p05}</td><td align=\"center\">${p06}</td><td>${p07}</td></tr>";
    String cbottom = "</body></html>";
    String context = strProjectID1 + "案" + strPosition + "不動產簽約客戶風險等級評估結果通知<BR>";

    String errMsgText = getValue("errMsgBoxText").trim();
    // String msg
    // ="案別代碼："+strProjectID1+"\n棟樓別："+strPosition+"\n訂戶姓名："+strCustomName+"\n付訂日期："+strOrderDate+"\n簽約日期："+strContractDate+"\n疑似洗錢樣態：\n"+errMsgText;
    // msg = msg.replace("\n","<BR>");
    context = context + table1;
    boolean isHighManager = false;

    for (int i = 0; i < list.size(); i++) {
      HashMap cm = (HashMap) list.get(i);
      String tempPo6 = "" + cm.get("p06");
      tempPo6 = tempPo6.trim();
      if ("高風險".equals(tempPo6)) {
        String l1 = new String(contextsample);
        l1 = l1.replace("${p01}", (String) cm.get("p01"));
        l1 = l1.replace("${p02}", (String) cm.get("p02"));
        l1 = l1.replace("${p025}", (String) cm.get("p025"));
        l1 = l1.replace("${p03}", (String) cm.get("p03"));
        l1 = l1.replace("${p035}", (String) cm.get("p035"));
        // l1 = l1.replace("${p04}",(String) cm.get("p04"));
        l1 = l1.replace("${p045}", (String) cm.get("p045"));
        l1 = l1.replace("${p05}", (String) cm.get("p05"));
        l1 = l1.replace("${p06}", tempPo6.replace("風險", ""));
        l1 = l1.replace("${p07}", "洗錢及資恐風險評估為" + tempPo6.replace("風險", "") + "風險客戶，請依洗錢防制作業規定，執行加強式管控措施");
        context = context + l1;
        isHighManager = true;
      }
    }
    if (!isHighManager) { // 高風險主管才收
      PNMail = userEmail;
    }

    boolean hasRecord = false;
    for (int i = 0; i < list.size(); i++) {
      HashMap cm = (HashMap) list.get(i);
      String tempPo6 = "" + cm.get("p06");
      tempPo6 = tempPo6.trim();
      if (!"高風險".equals(tempPo6)) {
        String l1 = new String(contextsample);
        l1 = l1.replace("${p01}", (String) cm.get("p01"));
        l1 = l1.replace("${p02}", (String) cm.get("p02"));
        l1 = l1.replace("${p025}", (String) cm.get("p025"));
        l1 = l1.replace("${p03}", (String) cm.get("p03"));
        l1 = l1.replace("${p035}", (String) cm.get("p035"));
        // l1 = l1.replace("${p04}",(String) cm.get("p04"));
        l1 = l1.replace("${p045}", (String) cm.get("p045"));
        l1 = l1.replace("${p05}", (String) cm.get("p05"));
        l1 = l1.replace("${p06}", tempPo6.replace("風險", ""));
        l1 = l1.replace("${p07}", "洗錢及資恐風險評估為" + tempPo6.replace("風險", "") + "風險客戶");
        context = context + l1;
      }
    }
    context = context + tail + cbottom;

    String[] arrayUser = { "Justin_Lin@fglife.com.tw", userEmail, PNMail };
    String sendRS = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser, subject, context, null, "", "text/html");
    System.out.println("sendRS===>" + sendRS);
    System.out.println("================Send Mail End================");

    if (!"".equals(value)) {
      messagebox(value);
    }

    return value;

  } // getDefault End

}
