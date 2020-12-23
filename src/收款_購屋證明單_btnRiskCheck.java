

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

public class 收款_購屋證明單_btnRiskCheck extends jcx.jform.sproc {

  public String getDefaultValue(String value) throws Throwable {
    String sysType = "RYB";// 不動產行銷B 銷售C
    talk dbSale = getTalk("Sale");
    talk dbEMail = getTalk("eMail");
    talk dbBen = getTalk("400CRM");
    String[][] retCustom = null;
    retCustom = getTableData("table1");
    String[][] retSBen = null;
    retSBen = getTableData("table6");
    String strProjectID1 = getValue("field1");
    String strOrderNo = getValue("field3").trim();
    String strOrderDate = getValue("field2").trim();

    System.out.println("strOrderNo=====>" + strOrderNo);
    System.out.println("strProjectID1=====>" + strProjectID1);
    System.out.println("strOrderDate=====>" + strOrderDate);

    String stringSQL = "";
    // 棟樓別
    String sql092 = "SELECT HouseCar,Position FROM  Sale05M092 WHERE OrderNo = '" + strOrderNo + "' ORDER BY RecordNo";
    String strHouse = "";
    String strCar = "";
    String[][] retPosition;
    retPosition = dbSale.queryFromPool(sql092);
    if (retPosition.length > 0) {
      for (int a = 0; a < retPosition.length; a++) {
        if ("House".equals(retPosition[a][0].trim())) {
          if ("".equals(strHouse)) {
            strHouse = retPosition[a][1].trim();
          } else {
            strHouse = strHouse + "," + retPosition[a][1].trim();
          }
        } else {
          if ("".equals(strCar)) {
            strCar = retPosition[a][1].trim();
          } else {
            strCar = strCar + "," + retPosition[a][1].trim();
          }
        }
      }
    }
    // 編輯人
    String modifier = "";
    modifier = getUser().toUpperCase().trim();
    talk dbEIP = getTalk("EIP");
    String[][] retEip = null;
    String strSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + modifier + "'";
    retEip = dbEIP.queryFromPool(strSQL);
    if (retEip.length > 0) {
      modifier = retEip[0][0];
    }

    String sysdate = new SimpleDateFormat("yyyyMMdd").format(getDate());
    String systime = new SimpleDateFormat("HHmmss").format(getDate());

    System.out.println("存入風險計算受益人資料-----------------------------------S");
    for (int i = 0; i < retSBen.length; i++) {
      boolean isNew = true;
      String[][] retBen = null;
      String id = retSBen[i][4];
      System.out.println("id:" + id);
      String sqlBen = "Select SHA02 FROM PSHAPF WHERE SHA06 = '" + id + "' And SHA00 = 'RY'";
      retBen = dbBen.queryFromPool(sqlBen);
      String beforeNo = "";

      System.out.println("retBen:" + retBen.length);
      if (retBen.length > 0) {
        beforeNo = retBen[0][0];
        isNew = false;
      }

      String ono = "";
      String oid = "";
      String oname = "";
      ono = retSBen[i][0];
      oid = retSBen[i][1];
      System.out.println("ono:" + ono);
      System.out.println("oid:" + oid);
      String sql91name = "SELECT CustomName FROM Sale05M091 WHERE OrderNo= '" + ono + "' AND CustomNo = '" + oid + "' AND ISNULL(StatusCd , '') = '' ";
      String[][] ret91Name = null;
      ret91Name = dbSale.queryFromPool(sql91name);
      if (ret91Name.length > 0) {
        oname = ret91Name[0][0];
      }
      System.out.println("oname:" + oname);

      String fileType = retSBen[i][7];
      String name = retSBen[i][3];
      String idType = "1";
      String bdate = retSBen[i][5].trim();
      bdate = bdate.replace("/", "");
      if ("".equals(bdate)) {
        bdate = "0";
      }
      String nation = retSBen[i][6].trim();
      String nationCode = "TWN";
      String PDCZPFSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + nation + "'";
      String[][] retPDCZPF = null;
      retPDCZPF = dbBen.queryFromPool(PDCZPFSql);
      nationCode = retPDCZPF[0][0].trim();
      String sqlInsert = "Insert into PSHAPF (SHA00, SHA02, SHA03, SHA04, SHA05, SHA06, SHA07, SHA08 " + ",SHA97, SHA98, SHA99, SHA100, SHA101, SHA102, SHA09, SHA10, SHA11, SHA12 "
          + ") VALUES (" + "'RY','" + strOrderNo + "','" + fileType + "','" + name + "','" + idType + "','" + id + "','" + bdate + "','" + nationCode + "'" + ",'" + modifier + "',"
          + sysdate + "," + systime + ",'" + modifier + "'," + sysdate + "," + systime + ",'R','3','" + oid + "','" + oname + "' )";
      String sqlupdate = "UPDATE PSHAPF SET SHA02='" + strOrderNo + "',SHA03='" + fileType + "', SHA04='" + name + "', SHA05 = '" + idType + "', SHA06 = '" + id + "', SHA07="
          + bdate + ",SHA08='" + nationCode + "'" + ",SHA100='" + modifier + "', SHA101 = " + sysdate + ", SHA102=" + systime + ", SHA09='R', SHA10='3', SHA11='" + oid
          + "', SHA12='" + oname + "' " + " Where SHA00 = 'RY' And SHA06 = '" + id + "'";
      try {
        if (isNew) {
          dbBen.execFromPool(sqlInsert);
        } else {
          dbBen.execFromPool(sqlupdate);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println("存入風險計算受益人資料-----------------------------------E");
    
    System.out.println("存入風險計算客戶資料-----------------------------------S");
    ResourceBundle resource = ResourceBundle.getBundle("sale");
    String as400ip = resource.getString("AS400.IP");
    String as400account = resource.getString("AS400.ACCOUNT");
    String as400password = resource.getString("AS400.PASSWORD");
    String as400init = resource.getString("AS400.INIT");
    String cms00c = resource.getString("CMS00C.LIB");
    String blpc00a = resource.getString("BLPC00A.LIB");
    String psri02 = resource.getString("PSRI02.LIB");
    String riskValue = "";
    String riskPoint = "";

    String strActionText = getValue("actionText").trim();
    // actionNo
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    String actionNo = sysdate + systime + ram;
    ArrayList list = new ArrayList();

    String sqltopmanager = "";
    RPGAS400Interface ra = null;
    RPGAS400Interface rb = null;
    RPGAS400Interface rc = null;

    try {
      ra = new RPGCMS00C(as400ip, as400account, as400password);
      rb = new RPGBLPC00A(as400ip, as400account, as400password);
      rc = new RPGPSRI02(as400ip, as400account, as400password);

      System.out.println(" retCustom.length=====> :" + retCustom.length);
      String msgboxtext = "";
      for (int i = 0; i < retCustom.length; i++) {

        System.out.println("test111>>>" + retCustom[i].length);
        // 20200319 kyle : 過濾已被換名的客戶(不檢核風險值)
        if ("C".equals(retCustom[i][23].trim()))
          continue;

        String strpositionCd = "8";
        if (!"".equals(retCustom[i][5].trim())) {
          strpositionCd = retCustom[i][5].trim();
        }
        sqltopmanager = " SELECT TOP 1 PositionCD, PName, ChairMan From A_Position " + " WHERE PositionCD = '" + strpositionCd + "'" + " ORDER BY PositionCD DESC ";
        String retPosition2[][] = dbSale.queryFromPool(sqltopmanager);
        String isManager = "N";
        if (retPosition2.length > 0) {
          isManager = retPosition2[0][2];
        }
        String type = "N";
        String sex = "";

        if (retCustom[i][5].trim().length() == 8) {
          type = "C";// N: 個人 C: 公司 F: 外國人
        } else {
          // 外國人(以國別判斷)
          if (!"中華民國".equals(retCustom[i][4].trim())) {
            type = "F";
          }
          // 性別
          if (retCustom[i][5].charAt(1) == '1') {
            sex = "M";
          } else if (retCustom[i][5].charAt(1) == '2') {
            sex = "F";
          }
        }
        // 國籍轉碼(若空預設TWN)
        String strCZ09 = "";
        if ("".equals(retCustom[i][4].trim())) {
          strCZ09 = "中華民國";
        } else {
          strCZ09 = retCustom[i][4].trim();
        }
        String strSaleSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCZ09 + "'";
        String retCNYCode[][] = dbBen.queryFromPool(strSaleSql);
        String cnyCode = retCNYCode[0][0].trim();
        System.out.println("cnyCode=====> :" + cnyCode);
        
        LinkedHashMap map = new LinkedHashMap();
        map.put("INAME", retCustom[i][6]); // 客戶姓名
        map.put("IDATE", retCustom[i][8].replace("/", ""));// 生日
        System.out.println("IDATE=====> :" + retCustom[i][8].replace("/", ""));
        map.put("ID", retCustom[i][5]); // 身份證號
        map.put("IAD1", retCustom[i][11]);// 地址 1
        map.put("IAD2", retCustom[i][12]);// 地址 2
        map.put("IAD3", retCustom[i][13]);// 地址 3
        map.put("IADD", retCustom[i][12].trim() + retCustom[i][13].trim() + retCustom[i][14].trim());// 長地址
        map.put("IZIP", retCustom[i][11]);// 郵遞區號
        map.put("ITELO", retCustom[i][16]);// 公司電話
        map.put("ITELH", retCustom[i][17]);// 住家電話
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

        LinkedHashMap mapb = new LinkedHashMap();
        mapb.put("INSID", retCustom[i][5]); // 身份證號
        mapb.put("SYSTEM", sysType); // 系統別
        mapb.put("CHGNO", modifier);// 異動人員員編
        mapb.put("CAPTION", strOrderNo);// 說明
        mapb.put("STRDATE", "0");// 起始日
        mapb.put("ENDDATE", "0");// 終止日
        mapb.put("RTCOD", "");// 回覆碼

        LinkedHashMap mapc = new LinkedHashMap();
        mapc.put("RI0201", retCustom[i][5]); // 身份證號
        mapc.put("RI0202", "RY");// 系統別
        mapc.put("RI0203", "Y");
        mapc.put("RIPOLN", "");
        mapc.put("RIFILE", strOrderNo);// 來源案號
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
        mapc.put("RO0211", "");//
        mapc.put("RO0212", "");//
        mapc.put("RO0213", "");//
        mapc.put("RTNR02", "");// 回覆碼

        boolean a = ra.invoke(as400init, cms00c, map);
        System.out.println("RTCODE:" + ra.getResult()[22]);
        boolean b = rb.invoke(as400init, blpc00a, mapb);
        System.out.println("RTCODE:" + rb.getResult()[6]);
        boolean c = rc.invoke(as400init, psri02, mapc);

        // RA ERROR MSG
        String resultMsg = "";
        if ("1".equals(ra.getResult()[22])) {
          resultMsg = "類別限N或C";
        } else if ("2".equals(ra.getResult()[22])) {
          resultMsg = "類別為N身份證ＩＤ檢核有誤或身份證不可空白";
        } else if ("3".equals(ra.getResult()[22])) {
          resultMsg = "類別為N生日不可為0";
        } else if ("4".equals(ra.getResult()[22])) {
          resultMsg = "類別為N日期格式有誤";
        } else if ("5".equals(ra.getResult()[22])) {
          resultMsg = "姓名不可空白";
        } else if ("6".equals(ra.getResult()[22])) {
          resultMsg = "地址檢核郵區不符合";
        } else {
          resultMsg = "" + rc.getResult()[20];
        }

        System.out.println("19洗錢風險值 :" + rc.getResult()[19]);
        System.out.println("20洗錢風險等級 :" + rc.getResult()[20]);

        riskPoint = rc.getResult()[19].toString().trim();
        riskValue = rc.getResult()[20].toString().trim();

        msgboxtext += "客戶 " + retCustom[i][6] + " 洗錢風險等級 :" + resultMsg + "\n";
        // if("".equals(msgboxtext)){
        // msgboxtext = "客戶 " + retCustom[i][6] + " 洗錢風險等級 :" + resultMsg;
        // }else{
        // msgboxtext = msgboxtext+"\n客戶" + retCustom[i][6] + "洗錢風險等級 :" + resultMsg;
        // }
        // messagebox(i+"洗錢風險等級 :" + rc.getResult()[20]);

        HashMap m = new HashMap();
        m.put("p01", strProjectID1);
        m.put("p02", strHouse.toUpperCase());
        m.put("p025", strCar.toUpperCase());
        m.put("p03", retCustom[i][6]);
        m.put("p035", retCustom[i][5]);
        m.put("p04", strOrderDate);
        m.put("p05", riskPoint);
        m.put("p06", riskValue.replace("風險", ""));
        list.add(m);

        System.out.println("回寫05M091資料-----------------------------------S");
        // talk dbSale091 = getTalk("Sale") ;
        System.out.println("RiskValue=====>" + riskValue);
        String M091Sql = "UPDATE Sale05M091 SET RiskValue = '" + riskValue.trim() + "' WHERE OrderNo = '" + strOrderNo + "' AND CustomNo = '" + retCustom[i][5].trim()
            + "' and ISNULL(StatusCd , '') = '';  ";
        dbSale.execFromPool(M091Sql);
        System.out.println("回寫05M091資料-----------------------------------E");
      }
      ra.disconnect();

      messagebox(msgboxtext);
    } catch (Exception e) {
      e.printStackTrace();
      ra.disconnect();
    }

    System.out.println("存入風險計算客戶資料-----------------------------------E");
    
    
    System.out.println("存入05M070資料-----------------------------------S");
    talk dbJGENLIB = getTalk("JGENLIB");
    String errMsg = "";
    String[][] retPat001;
    String[][] retPat002;
    String[][] retPat003;
    String[][] retPat004;

    // LOG日期,時間
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    String strNowDate = nowsdf.format(now);
    String tempROCYear = "" + (Integer.parseInt(strNowDate.substring(0, strNowDate.length() - 4)) - 1911);
    String RocNowDate = tempROCYear + strNowDate.substring(strNowDate.length() - 4, strNowDate.length());
    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HHmmss");
    String strNowTime = nowTimeSdf.format(now);
    System.out.println("RocNowDate=====>" + RocNowDate);
    System.out.println("strNowTime=====>" + strNowTime);
    
    // 員工碼
    String userNo = getUser().toUpperCase().trim();
    String empNo = "";
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }
    System.out.println("empNo=====>" + empNo);
    
    // 序號
    int intRecordNo = 1;
    String[][] ret05M070;
    stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + strOrderNo + "'";
    ret05M070 = dbSale.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
    
    // 顧客名'
    String strCustomNo = "";
    String strCustomName = "";
    String[][] retCust;
    String custSql = "SELECT CustomNo,CustomName  FROM Sale05M091 WHERE OrderNo = '" + strOrderNo + "' and ISNULL(StatusCd , '') = '' ";
    retCust = dbSale.queryFromPool(custSql);
    if (retCust.length > 0) {
      strCustomNo = retCust[0][0];
      strCustomName = retCust[0][1];
    }
    
    stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97, SHB98, SHB99) VALUES ('"
        + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + actionNo + "','購屋證明單','風險計算受益人資料','" + strActionText + "','風險值:" + riskValue + "','" + strCustomNo
        + "','" + strCustomName + "','" + strOrderDate + "','RY','773','022','風險值:" + riskValue + "','" + modifier + "'," + sysdate + "," + systime + ")";
    dbSale.execFromPool(stringSQL);
    intRecordNo++;

    System.out.println("存入05M070資料-----------------------------------E");
    
    
    System.out.println("發送EMAIL-----------------------------------S");
    String userEmail = "";
    String userEmail2 = "";
    String DPCode = "";
    String DPManageemNo = "";
    String DPeMail = "";
    String DPeMail2 = "";
    String[][] reteMail = null;
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
    ////////////////////////////////////////////////////////////////////////////////////////

    // send email
    boolean hilitgt = false;
    String table1 = "<table style='text-align:center;' border=1><tr><td>案別</td><td>棟樓別</td><td>車位別</td><td>客戶名稱</td><td>付訂日期</td><td>風險綜合值</td><td>客戶風險等級</td><td>說明</td></tr>";
    String tail = "</table>";
    String contextsample = "<tr><td>${p01}</td><td>${p02}</td><td>${p025}</td><td>${p03}</td><td>${p04}</td><td>${p05}</td><td>${p06}</td><td align='left' valign='center'>${p07}</td></tr>";
    String cbottom = "</body></html>";
    String context = strProjectID1 + "案" + strHouse + "不動產訂購客戶風險等級評估結果通知(測試)<BR>";

    context = table1;
    for (int i = 0; i < list.size(); i++) {
      HashMap cm = (HashMap) list.get(i);

      String l1 = new String(contextsample);
      l1 = l1.replace("${p01}", (String) cm.get("p01"));
      l1 = l1.replace("${p02}", (String) cm.get("p02"));
      l1 = l1.replace("${p025}", (String) cm.get("p025"));
      l1 = l1.replace("${p03}", (String) cm.get("p03"));
      l1 = l1.replace("${p035}", (String) cm.get("p035"));
      l1 = l1.replace("${p04}", (String) cm.get("p04"));
      l1 = l1.replace("${p05}", (String) cm.get("p05"));
      l1 = l1.replace("${p06}", (String) cm.get("p06"));
      String tempPo6 = "" + cm.get("p06");
      tempPo6 = tempPo6.trim();
      if ("高".equals(tempPo6.trim())) {
        l1 = l1.replace("${p07}", "洗錢及資恐風險評估為" + tempPo6 + "風險客戶，請依洗錢防制作業規定，執行加強式管控措施");
        hilitgt = true;
      } else {
        l1 = l1.replace("${p07}", "洗錢及資恐風險評估為" + tempPo6 + "風險客戶");
      }

      context = context + l1;
    }

    context = context + tail + cbottom;

    // 寄發通知信件
    String testRemark = " (測試) "; // 在測試環境要加註測試字樣
    String serverIP = get("serverIP").toString().trim();
    System.out.println("serverIP>>>" + serverIP);
    if (serverIP.contains("172.16.")) {
      System.out.println(">>>正式環境<<<");
      testRemark = "";
    }

    String subject = strProjectID1 + "案" + strHouse + "不動產訂購客戶風險等級評估結果通知(測試)" + testRemark;
    if (hilitgt) {
      String[] arrayUser = { "Justin_Lin@fglife.com.tw", userEmail, DPeMail, PNMail };
      String sendRS = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser, subject, context, null, "", "text/html");
      System.out.println("sendRS===>" + sendRS);
    } else {
      String[] arrayUser = { "Justin_Lin@fglife.com.tw", userEmail };
      String sendRS = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser, subject, context, null, "", "text/html");
      System.out.println("sendRS===>" + sendRS);
    }

    System.out.println("發送EMAIL-----------------------------------E");
    return value;
  }

}
