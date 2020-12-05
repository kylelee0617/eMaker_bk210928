//2020413 Kyle add
package Sale.AML;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import com.jacob.activeX.*;
import com.jacob.com.*;
import Farglory.util.*;

public class CSReport_Form_btReceiveCount extends bproc {
  talk dbFE5D = getTalk("FE5D");
  String deptCd = "";
  String deptCd1 = "";
  String objectCd = "";
  String customName = "";
  String queryDate = "";
  String[][] mainData = null;

  public String getDefaultValue(String value) throws Throwable {
    deptCd = getValue("deptCd").trim();
    deptCd1 = getValue("deptCd1").trim();
    objectCd = getValue("objectCd").trim();
    customName = getValue("customName").trim();
    queryDate = getValue("QueryDate").trim();
    mainData = this.getMainData();
    System.out.println("mainData Size>>>" + mainData.length);

    doExcel();
    return value;
  }

  public void doExcel() throws Throwable {
    List mainList = processMainList(mainData);
    System.out.println("mainList size>>>" + mainList.size());

    // 建立表格
    int startDataRow = 7;
    int endDataRow = mainList.size() + 5;
    Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(startDataRow, endDataRow, endDataRow, 1);

    // 吃sample檔路徑
    String stringPrintExcel = "G:\\kyleTest\\Excel\\EMK_CSReport\\CSReport_Form_btReceiveCount.xlt";
    // String stringPrintExcel = "D:\\testEX.xlt";
    System.out.println(stringPrintExcel);

    // 建立Excel物件
    Vector retVector = exeFun.getExcelObject(stringPrintExcel);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);

    exeFun.putDataIntoExcel(0, 3, "案號:" + deptCd + deptCd1, objectSheet1);

    // Start of Body 資料本體
    for (int intRow = 0; intRow < mainList.size(); intRow++) {
      String[] thisRow = (String[]) mainList.get(intRow);
      int recordNo = intRow + exeFun.getStartDataRow();

      for (int intCon = 0; intCon < 8; intCon++) {
        exeFun.putDataIntoExcel(intCon, recordNo, thisRow[intCon], objectSheet1);
      }
    }
    // End of Body

    exeFun.getReleaseExcelObject(retVector);
    message("輸出報表完成!!");

    return;
  }

  /**
   * 組成主要列表 List
   */
  public List processMainList(String[][] mainData) throws Throwable {
    List mainList = new ArrayList();

    int orderCashCount = 0;
    int orderCount = 0;
    String lastKey = "";
    for (int rowMD = 0; rowMD < mainData.length; rowMD++) {
      String[] newRow = new String[8];
      String[] thisRow = mainData[rowMD];
      String thisKey = thisRow[0].trim() + thisRow[1].trim() + thisRow[2].trim();
      int thisPay = (int) Math.floor(Float.parseFloat(thisRow[6].trim()));

      // 重置 & 累計現金金額(次數)
      if (!lastKey.equals(thisKey)) {
        orderCashCount = 0;
        orderCount = 0;
      }
      orderCashCount += thisPay;
      orderCount++;

      newRow[0] = thisRow[0].trim() + thisRow[1].trim(); // 案號
      newRow[1] = thisRow[2].trim(); // 戶號
      newRow[2] = thisRow[4].trim(); // 姓名
      newRow[3] = Integer.toString(thisPay); // 金額
      newRow[4] = this.formatROCDate(thisRow[5].trim()); // 收款日
      newRow[5] = ""; // 大額通報
      newRow[6] = Integer.toString(orderCount); // 繳交現金次數
      newRow[7] = Integer.toString(orderCashCount); // 繳交現金累計金額

      lastKey = thisKey;
      mainList.add(newRow);
    }

    return mainList;
  }

  /**
   * 0.案別 1.照號 2.戶號 3.收款單編號 4.姓名 5.日期 6.金額
   */
  public String[][] getMainData() throws Throwable {
    StringBuilder sbSql = new StringBuilder();
    sbSql.append("SELECT I.DEPT_CD , I.DEPT_CD_1, I.OBJECT_CD, CHECK_FLOW_NO, RTRIM(J.OBJECT_FULL_NAME) as NAME , CHECK_YMD , PAPER_AMT ");
    sbSql.append("FROM FE5D10 I WITH (NOLOCK) ");
    sbSql.append("LEFT JOIN FE5D05 J WITH (NOLOCK) ON I.OBJECT_CD = J.OBJECT_CD  AND I.DEPT_CD = J.DEPT_CD AND I.DEPT_CD_1 = J.DEPT_CD_1 ");
    sbSql.append("WHERE 1=1 ");
    sbSql.append("AND ISNULL(I.AMT_KIND , '') = '2' ");
    if (deptCd.length() != 0) {
      sbSql.append("AND I.DEPT_CD = '" + deptCd + "' ");
    }
    if (deptCd1.length() != 0) {
      sbSql.append("AND I.DEPT_CD_1 = '" + deptCd1 + "' ");
    }
    if (objectCd.length() != 0) {
      sbSql.append("AND I.OBJECT_CD = '" + objectCd + "' ");
    }
    if (customName.length() != 0) {
      sbSql.append("AND J.OBJECT_FULL_NAME like '%" + customName + "%' ");
    }

    if (queryDate.length() != 0) {
      String[] ROCDate = formatQueryDate(queryDate);
      if (!"".equals(ROCDate[0])) {
        sbSql.append("AND I.CHECK_YMD >= " + ROCDate[0] + " ");
      }
      if (!"".equals(ROCDate[1]) && !ROCDate[0].equals(ROCDate[1])) {
        sbSql.append("AND I.CHECK_YMD <= " + ROCDate[1] + " ");
      }
    }

    sbSql.append("ORDER BY I.DEPT_CD,I.DEPT_CD_1,I.OBJECT_CD,CHECK_YMD DESC , CHECK_FLOW_NO ASC");

    String[][] strArrRS = dbFE5D.queryFromPool(sbSql.toString());

    return strArrRS;
  }

  private String[] formatQueryDate(String dayStr) throws Throwable {
    String[] spDay = dayStr.split(" and ");
    String bDay = spDay[0].trim();
    String eDay = spDay[1].trim();

    if (bDay.length() != 8 || eDay.length() != 8) {
      spDay[0] = " '' ";
      spDay[1] = " '' ";
    } else {
      String newBDay = " '" + (Integer.parseInt(bDay.substring(0, 4)) - 1911) + "" + bDay.substring(4, 6) + "" + bDay.substring(6, 8) + "' ";
      String newEDay = " '" + (Integer.parseInt(eDay.substring(0, 4)) - 1911) + "" + eDay.substring(4, 6) + "" + eDay.substring(6, 8) + "' ";
      spDay[0] = newBDay;
      spDay[1] = newEDay;
    }

    return spDay;
  }

  private String formatROCDate(String dayStr) throws Throwable {
    if (dayStr.length() != 7) {
      return "";
    }

    return dayStr.substring(0, 3) + "/" + dayStr.substring(3, 5) + "/" + dayStr.substring(5, 7);
  }

  public String getInformation() {
    return "---------------button2(列印).defaultValue()----------------";
  }
}