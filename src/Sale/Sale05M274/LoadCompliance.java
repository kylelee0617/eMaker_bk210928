package Sale.Sale05M274;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;

public class LoadCompliance extends bproc {

  public String getDefaultValue(String value) throws Throwable {
    talk dbSale = getTalk("Sale");
    talk db400 = getTalk("400CRM");
    String stringSql = "";
    String str400sql = "";
    String retData[][] = null;
    String retX171[][] = null;
    String strOrderNo = "";
    //
    JTable jtable = getTable("table1");
    for (int intRow = 0; intRow < jtable.getRowCount(); intRow++) {
      if (("" + getValueAt("table1", intRow, "OrderNo")).trim().length() > 0) {
        strOrderNo = ("" + getValueAt("table1", intRow, "OrderNo")).trim();
      }
    }
    System.out.println("strOrderNo=====>" + strOrderNo);
    ArrayList list = new ArrayList();
    String strCustomNo = "";
    String strCustomName = "";
    String str91BenName = "";
    String strRiskValue = "";
    String strCustBlack = "否";
    String strCustPePS = "否";
    String str91BenBlack = "";
    String str91BenPePS = "";
    String strCheckDate = "";
    String strAMLMsg = "";
    stringSql = "SELECT CustomNo,CustomName,RiskValue,IsLinked,IsControlList,IsBlackList  FROM Sale05M091 WHERE orderNo='" + strOrderNo
        + "'  AND (TrxDate is null  or TrxDate = '') AND (StatusCd is null or StatusCd='')  ORDER BY RECORDNO";
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length > 0) {
      for (int a = 0; a < retData.length; a++) {
        // 共用部分
        strCustomNo = retData[a][0].trim();
        strCustomName = retData[a][1].trim();
        strRiskValue = retData[a][2].trim();

        // 客戶部分
        // 控管黑名單
        if ("Y".equals(retData[a][3].trim())) {
          strCustBlack = "是";
        } else if ("Y".equals(retData[a][4].trim())) {
          strCustBlack = "是";
        } else if ("Y".equals(retData[a][5].trim())) {
          strCustBlack = "是";
        }
        // PEPS
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171') AND CUSTOMERID = '"
            + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        retX171 = db400.queryFromPool(str400sql);
        if (retX171.length > 0) {
          strCustPePS = "是";
        }
        // 實質受益人部分
        if (strCustomNo.length() == 8) {// 法人
          String sql91Ben = "SELECT BCustomNo,BenName,IsBlackList,IsControlList,IsLinked FROM Sale05M091Ben WHERE OrderNo = '" + strOrderNo + "' and CustomNo = '" + strCustomNo
              + "' and ISNULL(StatusCd, '') != 'C' ";
          String ret91Ben[][] = dbSale.queryFromPool(sql91Ben);
          if (ret91Ben.length > 0) {
            // 重置實質受益人
            str91BenName = "";
            for (int b = 0; b < ret91Ben.length; b++) {
              String BenNo = ret91Ben[b][0].trim();
              String BenName = ret91Ben[b][1].trim();
              if ("".equals(str91BenName)) {
                str91BenName = BenName;
              } else {
                str91BenName = str91BenName + "\n" + BenName;
              }
              // 控管黑名單
              String a91temp = "否";
              if ("Y".equals(ret91Ben[b][2].trim())) {
                a91temp = "是";
              } else if ("Y".equals(ret91Ben[b][3].trim())) {
                a91temp = "是";
              } else if ("Y".equals(ret91Ben[b][4].trim())) {
                a91temp = "是";
              }
              if ("".equals(str91BenBlack)) {
                str91BenBlack = a91temp;
              } else {
                str91BenBlack = str91BenBlack + "\n" + a91temp;
              }
              // PEPS
              String b91PEPStemp = "否";
              str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE='X171') AND CUSTOMERID = '"
                  + BenNo + "' AND CUSTOMERNAME='" + BenName + "'";
              retX171 = db400.queryFromPool(str400sql);
              if (retX171.length > 0) {
                b91PEPStemp = "是";
              }
              if ("".equals(str91BenPePS)) {
                str91BenPePS = b91PEPStemp;
              } else {
                str91BenPePS = str91BenPePS + "\n" + b91PEPStemp;
              }
            }
          }
        }
        // AML部分
        stringSql = 
              " SELECT OrderDate,EDate,CDate,RecordDesc,Func,RecordType,ActionName,SHB98,SHB06B,customid, customName FROM Sale05M070 WHERE RecordDesc <> '不符合' AND RecordDesc<>'不適用' AND SHB06B <> '022' AND SHB06B <> '018'  AND orderNo='"+ strOrderNo + "'  " 
            + " UNION "
            + " SELECT OrderDate,EDate,CDate,RecordDesc,Func,RecordType,ActionName,SHB98,SHB06B,customid, customName FROM Sale05M070 WHERE RecordDesc <> '不符合' AND RecordDesc<>'不適用' AND SHB06B <> '022' AND SHB06B = '018'  AND RecordType = '附件-指定第三人'  AND orderNo='"+ strOrderNo + "' " 
            + " ORDER BY Func DESC, RecordType DESC, OrderDate ASC, CDate ASC, EDate ASC";
        String retAMLData[][] = dbSale.queryFromPool(stringSql);
        if (retAMLData.length > 0) {// 多筆符合
          String lastKey = ""; // 用以過濾相同的紀錄
          for (int c = 0; c < retAMLData.length; c++) {

            // K: 過濾重複物件
            String tmpKey = strCustomName.trim() + "-" + retAMLData[c][4].trim() + "-" + retAMLData[c][5].trim() + "-" + retAMLData[c][8];
            // System.out.println(">>>last : " + lastKey);
            // System.out.println(">>>tmp : " + tmpKey);
            if (lastKey.equals(tmpKey)) {
              // System.out.println(">>>continue");
              continue;
            }
            // System.out.println(">>>gogogogo");

            // 檢核日
            if (!"".equals(retAMLData[c][0].trim())) {
              strCheckDate = retAMLData[c][0].trim();
            } else if (!"".equals(retAMLData[c][1].trim())) {
              strCheckDate = retAMLData[c][1].trim();
            } else if (!"".equals(retAMLData[c][2].trim())) {
              strCheckDate = retAMLData[c][2].trim();
            }
            
            if("收款".equals( retAMLData[c][4].trim() )) {
              strCheckDate = retAMLData[c][1].trim();
            }

            // 態樣文字
            strAMLMsg = retAMLData[c][3].trim();

            // 國民年轉換
            String strChangDate = retAMLData[c][7].trim();
            String strChangYYYY = strChangDate.substring(0, 3);
            String strChangMM = strChangDate.substring(3, 5);
            String strChangDD = strChangDate.substring(5, 7);

            strChangYYYY = "" + (Integer.parseInt(strChangYYYY) + 1911);
            strChangDate = strChangYYYY + "/" + strChangMM + "/" + strChangDD;

            HashMap m = new HashMap();
            m.put("CustomName", strCustomName);
            m.put("BenName", str91BenName);
            m.put("RiskValue", strRiskValue);
            m.put("CustBlack", strCustBlack);
            m.put("CustPePS", strCustPePS);
            m.put("91BenBlack", str91BenBlack);
            m.put("91BenPePS", str91BenPePS);
            m.put("CheckDate", strCheckDate);
            m.put("AMLMsg", strAMLMsg);
            m.put("Func", retAMLData[c][4].trim());
            m.put("RecordType", retAMLData[c][5].trim());
            m.put("ActionName", retAMLData[c][6].trim());
            m.put("ChangDate", strChangDate);
            list.add(m);
            lastKey = tmpKey;
          }
        } else {// 無符合
          HashMap m = new HashMap();
          m.put("CustomName", strCustomName);
          m.put("BenName", str91BenName);
          m.put("RiskValue", strRiskValue);
          m.put("CustBlack", strCustBlack);
          m.put("CustPePS", strCustPePS);
          m.put("91BenBlack", str91BenBlack);
          m.put("91BenPePS", str91BenPePS);
          m.put("CheckDate", strCheckDate);
          m.put("ChandDate", "");
          m.put("AMLMsg", strAMLMsg);
          m.put("Func", "");
          m.put("RecordType", "");
          m.put("ActionName", "");
          list.add(m);
        }
      }
    }

    String[][] ret2 = new String[list.size()][13];
    int retIndex = 0;
    for (int i = 0; i < list.size(); i++) {
      HashMap cm = (HashMap) list.get(i);
      ret2[retIndex][0] = (String) cm.get("CustomName");
      ret2[retIndex][1] = (String) cm.get("RiskValue");
      ret2[retIndex][2] = (String) cm.get("CustBlack");
      ret2[retIndex][3] = (String) cm.get("CustPePS");
      ret2[retIndex][4] = (String) cm.get("BenName");
      ret2[retIndex][5] = (String) cm.get("91BenBlack");
      ret2[retIndex][6] = (String) cm.get("91BenPePS");
      ret2[retIndex][7] = (String) cm.get("Func");
      ret2[retIndex][8] = (String) cm.get("RecordType");
      ret2[retIndex][9] = (String) cm.get("ActionName");
      ret2[retIndex][10] = (String) cm.get("CheckDate");
      ret2[retIndex][11] = (String) cm.get("ChangDate");
      ret2[retIndex][12] = (String) cm.get("AMLMsg");
      retIndex++;
    }
    setTableData("table29", ret2);
    return value;
  }

  public String getInformation() {
    return "---------------btnLoadCompliance(\u8f09\u5165\u6cd5\u4ee4\u9075\u5faa).defaultValue()----------------";
  }
}
