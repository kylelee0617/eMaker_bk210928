

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

public class 收款_換名_btnRiskCheck extends jcx.jform.sproc {

  public String getDefaultValue(String value) throws Throwable {

    String sysType = "RYB";// 不動產行銷B 銷售C
    talk dbSale = getTalk("Sale");
    talk dbBen = getTalk("400CRM");
    String[][] retCustom = null;
    retCustom = getTableData("table2");

    String modifier = "";
    String fileNo = getValue("OrderNo");

    String sysdate = new SimpleDateFormat("yyyyMMdd").format(getDate());
    String systime = new SimpleDateFormat("HHmmss").format(getDate());

    System.out.println("存入風險計算受益人資料-----------------------------------S");

    String[][] retSBen = null;
    retSBen = getTableData("table5");

    System.out.println("retSBen.length=====>" + retSBen.length);
    for (int i = 0; i < retSBen.length; i++) {
      boolean isNew = true;
      String[][] retBen = null;
      String id = retSBen[i][4];
      String sqlBen = "Select SHA02 FROM PSHAPF WHERE SHA06 = '" + id + "' And SHA00 = 'RY'";
      retBen = dbBen.queryFromPool(sqlBen);
      String beforeNo = "";

      System.out.println("retBen:" + retBen.length);
      if (retBen.length > 0) {
        beforeNo = retBen[0][0];
        isNew = false;
      }
      String pno = "";

      String fileType = retSBen[i][7];
      String name = retSBen[i][3];
      String idType = "1";
      String bdate = retSBen[i][5].trim();
      if ("".equals(bdate)) {
        bdate = "0";
      }
      String nation = retSBen[i][6].trim();
      String nationCode = "TWN";
      String PDCZPFSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + nation + "'";
      String[][] retPDCZPF = null;
      retPDCZPF = dbBen.queryFromPool(PDCZPFSql);
      nationCode = retPDCZPF[0][0].trim();
      String sqlInsert = "Insert into PSHAPF (SHA00, SHA02, SHA03, SHA04, SHA05, SHA06, SHA07, SHA08 ,SHA97, SHA98, SHA99 ) VALUES (" + "'RY','" + fileNo + "','" + fileType + "','"
          + name + "','" + idType + "','" + id + "','" + bdate + "','" + nationCode + "'" + ",'" + modifier + "'," + sysdate + "," + systime + ")";
      String sqlupdate = "UPDATE PSHAPF SET SHA02='" + fileNo + "',SHA03='" + fileType + "', SHA04='" + name + "', SHA05 = '" + idType + "', SHA06 = '" + id + "', SHA07=" + bdate
          + ",SHA08='" + nationCode + "'" + ",SHA100='" + modifier + "', SHA101 = " + sysdate + ", SHA102=" + systime + " Where SHA00 = 'RY' And SHA06 = '" + id + "'";
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
    
    System.out.println("換名_存入風險計算客戶資料-----------------------------------S");
    ResourceBundle resource = ResourceBundle.getBundle("sale");
    String as400ip = resource.getString("AS400.IP");
    String as400account = resource.getString("AS400.ACCOUNT");
    String as400password = resource.getString("AS400.PASSWORD");
    String as400init = resource.getString("AS400.INIT");
    String cms00c = resource.getString("CMS00C.LIB");
    String blpc00a = resource.getString("BLPC00A.LIB");
    String psri02 = resource.getString("PSRI02.LIB");
    String riskValue = "";

    String sqltopmanager = "";
    RPGAS400Interface ra = null;
    RPGAS400Interface rb = null;
    RPGAS400Interface rc = null;

    try {
      ra = new RPGCMS00C(as400ip, as400account, as400password);
      rb = new RPGBLPC00A(as400ip, as400account, as400password);
      rc = new RPGPSRI02(as400ip, as400account, as400password);

      StringBuffer riskValueMsg = new StringBuffer();
      for (int i = 0; i < retCustom.length; i++) {
        if ("1".equals(retCustom[i][2].trim())) {
          sqltopmanager = " SELECT TOP 1 PositionCD, PName, ChairMan From A_Position " + " WHERE PName = '" + retCustom[i][11] + "'" + " ORDER BY PositionCD DESC ";
          String retPosition[][] = dbSale.queryFromPool(sqltopmanager);
          String isManager = "N";
          if (retPosition.length > 0) {
            isManager = retPosition[0][2];
          }
          String type = "N";
          String sex = "";

          if (retCustom[i][6].trim().length() == 8) {
            type = "C";// N: 個人 C: 公司 F: 外國人
          } else {
            // 外國人(以國別判斷)
            if (!"中華民國".equals(retCustom[i][4].trim())) {
              type = "F";
            }
            // 性別
            if (retCustom[i][6].charAt(1) == '1') {
              sex = "M";
            } else if (retCustom[i][6].charAt(1) == '2') {
              sex = "F";
            }
          }

          // 編輯人
          modifier = getUser().toUpperCase().trim();
          talk dbEIP = getTalk("EIP");
          String[][] retEip = null;
          String strSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + modifier + "'";
          retEip = dbEIP.queryFromPool(strSQL);
          if (retEip.length > 0) {
            modifier = retEip[0][0];
          }

          // 國籍轉碼
          String cnyCode = "TWN";
          String strSaleSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE'AND CZ09='" + retCustom[i][5].trim() + "'";
          String retCNYCode[][] = dbBen.queryFromPool(strSaleSql);
          if (retCNYCode.length > 0) {
            cnyCode = retCNYCode[0][0].trim();
          }
          System.out.println("cnyCode=====> :" + cnyCode);
          LinkedHashMap map = new LinkedHashMap();
          fileNo = getValue("OrderNo");
          map.put("INAME", retCustom[i][7]); // 客戶姓名
          map.put("IDATE", retCustom[i][9].replace("/", ""));// 生日
          map.put("ID", retCustom[i][6]); // 身份證號
          map.put("IAD1", retCustom[i][13]);// 地址 1
          map.put("IAD2", retCustom[i][14]);// 地址 2
          map.put("IAD3", retCustom[i][15]);// 地址 3
          map.put("IADD", retCustom[i][13].trim() + retCustom[i][14].trim() + retCustom[i][15].trim());// 長地址
          map.put("IZIP", retCustom[i][12]);// 郵遞區號
          map.put("ITELO", retCustom[i][17]);// 公司電話
          map.put("ITELH", retCustom[i][18]);// 住家電話
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
          mapb.put("INSID", retCustom[i][6]); // 身份證號
          mapb.put("SYSTEM", sysType); // 系統別
          mapb.put("CHGNO", modifier);// 異動人員員編
          mapb.put("CAPTION", fileNo);// 說明
          mapb.put("STRDATE", "0");// 起始日
          mapb.put("ENDDATE", "0");// 終止日
          mapb.put("RTCOD", "");// 回覆碼

          LinkedHashMap mapc = new LinkedHashMap();
          mapc.put("RI0201", retCustom[i][6]); // 身份證號
          mapc.put("RI0202", "RY");// 系統別
          mapc.put("RI0203", "Y");
          mapc.put("RIPOLN", "");
          mapc.put("RIFILE", fileNo);// 來源案號
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

          System.out.println("19洗錢風險值 :" + rc.getResult()[19]);
          System.out.println("20洗錢風險等級 :" + rc.getResult()[20]);

          riskValue = rc.getResult()[20].toString().trim();
          riskValueMsg.append("客戶 ").append(retCustom[i][7]).append(" 洗錢風險等級 :").append(riskValue).append("\n");

          System.out.println("回寫05M091資料-----------------------------------S");
          String M091Sql = "UPDATE Sale05M091 SET RiskValue = '" + riskValue.trim() + "' WHERE OrderNo = '" + fileNo + "' AND CustomNo='" + retCustom[i][6].trim()
              + "' AND isnull(StatusCd,'') = '' ";
          dbSale.execFromPool(M091Sql);
          System.out.println("回寫05M091資料-----------------------------------E");
        }
      }
      messagebox(riskValueMsg.toString());
      ra.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      ra.disconnect();
    }

    System.out.println("存入風險計算客戶資料-----------------------------------E");

    return value;
  }

}
