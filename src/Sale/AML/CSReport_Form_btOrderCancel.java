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

public class CSReport_Form_btOrderCancel extends bproc {
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
    String stringPrintExcel = "G:\\kyleTest\\Excel\\EMK_CSReport\\CSReport_Form_btOrderCancel.xlt";
    // String stringPrintExcel = "D:\\CSReport_Form_btOrderCancel.xlt";
    System.out.println(stringPrintExcel);

    // 建立Excel物件
    Vector retVector = exeFun.getExcelObject(stringPrintExcel);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);

    exeFun.putDataIntoExcel(0, 3, "案號:" + deptCd + deptCd1, objectSheet1);

    // Start of Body 資料本體
    for (int intRow = 0; intRow < mainList.size(); intRow++) {
      String[] thisRow = (String[]) mainList.get(intRow);
      int recordNo = intRow + exeFun.getStartDataRow();

      for (int intCon = 0; intCon < 6; intCon++) {
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

    for (int rowMD = 0; rowMD < mainData.length; rowMD++) {
      String[] newRow = new String[6];
      String[] thisRow = mainData[rowMD];
      String thisKey = thisRow[0].trim() + thisRow[1].trim() + thisRow[2].trim();

      newRow[0] = thisRow[0].trim() + thisRow[1].trim(); // 案號
      newRow[1] = thisRow[2].trim(); // 戶號
      newRow[2] = thisRow[3].trim(); // 姓名
      newRow[3] = thisRow[4].trim(); // ID
      newRow[4] = this.formatROCDate(thisRow[5].trim()); // 簽約日
      newRow[5] = this.formatROCDate(thisRow[6].trim()); // 解約日

      mainList.add(newRow);
    }

    return mainList;
  }

  public String[][] getMainData() throws Throwable {
    StringBuilder sbSql = new StringBuilder();
    sbSql.append("SELECT I.DEPT_CD, I.DEPT_CD_1, I.OBJECT_CD, RTRIM(J.OBJECT_FULL_NAME) as name, OBJECT_ID, J.APPOINT_DATE, I.ANSWER_DATE ");
    sbSql.append("from FE5D17 I WITH (NOLOCK) ,FE5D05 J WITH (NOLOCK) ");
    sbSql.append("where 1=1 ");
    sbSql.append("AND I.OBJECT_CD = J.OBJECT_CD  AND I.DEPT_CD = J.DEPT_CD ");
    sbSql.append("AND I.DEPT_CD_1 = J.DEPT_CD_1 ");
    // sbSql.append("AND I.DEPT_CD = '" + deptCd + "' and I.DEPT_CD_1 = '" + deptCd1
    // + "' ");
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
        sbSql.append("AND I.ANSWER_DATE >= " + ROCDate[0] + " ");
      }
      if (!"".equals(ROCDate[1]) && !ROCDate[0].equals(ROCDate[1])) {
        sbSql.append("AND I.ANSWER_DATE <= " + ROCDate[1] + " ");
      }
    }

    sbSql.append("ORDER BY I.DEPT_CD, I.DEPT_CD_1, I.OBJECT_CD ");

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