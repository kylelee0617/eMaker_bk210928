// �ק���:20100520 ���u�s��:B3774  �]�s�W�i��ܬO�_�ץX�ذe�����ܰʹL�j�A�G��mark�B�R���Ҧ�mark
package Sale;

import jcx.jform.bTransaction;
import java.util.*;
import jcx.util.*;
import jcx.db.*;
import com.jacob.activeX.*;
import com.jacob.com.*;

public class Sale05R21004 extends bTransaction {
  talk dbSale = getTalk("" + get("put_dbSale"));

  public boolean action(String value) throws Throwable {
    // �^�ǭȬ� true ��ܰ��汵�U�Ӫ���Ʈw���ʩάd��
    // �^�ǭȬ� false ��ܱ��U�Ӥ����������O
    // �ǤJ�� value �� "�s�W","�d��","�ק�","�R��","�C�L","PRINT" (�C�L�w�����C�L���s),"PRINTALL"
    // (�C�L�w���������C�L���s) �䤤���@
    Farglory.util.FargloryUtil exeUtil = new Farglory.util.FargloryUtil();
    // �I�q����-�_
    String stringOrderDateS = getValue("OrderDateS").trim();
    if (stringOrderDateS.length() == 0) {
      message("�п�J�I�q����-�_!");
      getcLabel("OrderDateS").requestFocus();
      return false;
    }
    stringOrderDateS = exeUtil.getDateAC(stringOrderDateS, "�I�q����-�_");
    if (stringOrderDateS.length() != 10) {
      message(stringOrderDateS);
      getcLabel("OrderDateS").requestFocus();
      return false;
    } else {
      setValue("OrderDateS", stringOrderDateS);
    }
    // �I�q����-��
    String stringOrderDateE = getValue("OrderDateE").trim();
    if (stringOrderDateE.length() == 0) {
      message("�п�J�I�q����-��!");
      getcLabel("OrderDateE").requestFocus();
      return false;
    }
    stringOrderDateE = exeUtil.getDateAC(stringOrderDateE, "�I�q����-��");
    if (stringOrderDateE.length() != 10) {
      message(stringOrderDateE);
      getcLabel("OrderDateE").requestFocus();
      return false;
    } else {
      setValue("OrderDateE", stringOrderDateE);
    }
    if (datetime.subDays1(stringOrderDateS.replaceAll("/", ""), stringOrderDateE.replaceAll("/", "")) > 0) {
      message("�I�q�����_���i�H�j��!");
      return false;
    }
    //
    long longTime1 = exeUtil.getTimeInMillis();
    doExcel();
    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("+�����w��---" + ((longTime2 - longTime1) / 1000) + "��---");
    //
    return false;
  }

  public void doExcel() throws Throwable {
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringOrderDateS = getValue("OrderDateS").trim();
    String stringOrderDateE = getValue("OrderDateE").trim();
    String stringIsExportGifts = getValue("IsExportGifts").trim();
    String retProjectID1[][] = null;
    // �קO
    retProjectID1 = getProjectID1(stringProjectID1, stringOrderDateS, stringOrderDateE);
    if (retProjectID1.length == 0) {
      message("�d�L��ơC");
      return;
    }
    //
    Farglory.util.FargloryUtil exeUtil = new Farglory.util.FargloryUtil();
    long longTime1 = exeUtil.getTimeInMillis();
    //
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel(7, 29, 38, 1);
    Vector retVector = exeExcel.getExcelObject("G:\\��T��\\Excel\\Sale05R21004.xls");
    Dispatch objectSheets = (Dispatch) retVector.get(3);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);
    int intStartDataRow = exeExcel.getStartDataRow();
    int intPageAllRow = exeExcel.getPageAllRow();
    int intInsertDataRow = intStartDataRow;
    //
    String retItemNo[][] = null;
    String retPositionCarGifts[][] = null;
    int intField = 1; // ����w�]��1�ذӫ~
    String stringItemNo = "";
    String stringSpec = "";
    Vector vectorItemNo = null;
    Hashtable hashtableItemNoCol = null;
    Hashtable hashtableItemNoCount = null;
    Hashtable hashtableItemNoAmt = null;
    int intCol = 0;
    int intCount = 0;
    double doubleAmt = 0;
    String stringPosition = "";
    String stringCar = "";
    int intDataEndRow = 0;
    int intPages = 0;
    //
    // �I�q����
    exeExcel.putDataIntoExcel(0, 3, "�I�q�����G" + stringOrderDateS + "-" + stringOrderDateE, objectSheet2);
    //
    for (int intP = 0; intP < retProjectID1.length; intP++) {
      // �٭�Sheet�νƻsSheet
      if (intP > 0) {
        if ("Yes".equals(stringIsExportGifts)) {
          if (vectorItemNo.size() > 1) {
            exeExcel.doDeleteColumns2("Z:" + exeExcel.getExcelColumnName("A", vectorItemNo.size() * 2 + 22), objectSheet2);
          }
        } else {
          exeExcel.doDeleteColumns2("X:Y", objectSheet2);
        }
        exeExcel.doClearContents("A7:W35", objectSheet2);
        Dispatch.call(objectSheet2, "Copy", objectSheet2);
        objectSheet1 = Dispatch.call(objectSheets, "Item", "Sheet2 (2)").toDispatch();
      }
      // �gSheet�W��
      stringProjectID1 = retProjectID1[intP][0];
      Dispatch.put(objectSheet1, "Name", stringProjectID1);
      Dispatch.call(objectSheet1, "Select");
      // �ذe���ةһ����ƻs�ζץX�W��
      if ("Yes".equals(stringIsExportGifts)) {
        retItemNo = getItemNo(stringProjectID1, stringOrderDateS, stringOrderDateE);
        vectorItemNo = new Vector();
        hashtableItemNoCol = new Hashtable();
        hashtableItemNoCount = new Hashtable();
        hashtableItemNoAmt = new Hashtable();
        exeExcel.setClearCol(0, retItemNo.length * 2 + 23);
        for (int intNo = intField; intNo < retItemNo.length; intNo++) {
          exeExcel.doCopyColumns(23, 24, objectSheet1);
          exeExcel.doCopyColumns(23, 24, objectSheet2);
        }
        for (int intNo = 0; intNo < retItemNo.length; intNo++) {
          stringItemNo = retItemNo[intNo][0].trim();
          stringSpec = getSpec(stringItemNo);
          vectorItemNo.add(stringItemNo);
          exeExcel.putDataIntoExcel(23 + intNo * 2, 4, stringItemNo + "~" + stringSpec, objectSheet2);
          hashtableItemNoCol.put(stringItemNo, "" + (23 + intNo * 2));
        }
      }
      // ����
      exeExcel.putDataIntoExcel(0, 1, "�קO�G" + stringProjectID1, objectSheet2);
      intStartDataRow = exeExcel.getStartDataRow();
      intPageAllRow = exeExcel.getPageAllRow();
      intInsertDataRow = intStartDataRow;
      intCol = 0;
      intCount = 0;
      doubleAmt = 0;
      stringPosition = "";
      exeExcel.setPageNo(1);
      // �]�U�ɼӧO���
      retPositionCarGifts = getPositionCarGifts(stringProjectID1, stringOrderDateS, stringOrderDateE, stringIsExportGifts);
      for (int intNo = 0; intNo < retPositionCarGifts.length; intNo++) {
        stringPosition = retPositionCarGifts[intNo][0].trim();
        stringCar = retPositionCarGifts[intNo][1].trim();
        stringItemNo = retPositionCarGifts[intNo][23].trim();
        // �ɼӧO�B����O
        exeExcel.putDataIntoExcel(0, intInsertDataRow, stringPosition, objectSheet2);
        exeExcel.putDataIntoExcel(1, intInsertDataRow, stringCar, objectSheet2);
        // �Ȥ�W��
        exeExcel.putDataIntoExcel(2, intInsertDataRow, getCustomName(retPositionCarGifts[intNo][2].trim()), objectSheet2);
        // �I�q��Bñ����B�W�ơB�P���B����B�ذe�B�����B�b���B�����B�W���B��X�H1~10
        for (int i = 3; i <= 22; i++) {
          exeExcel.putDataIntoExcel(i, intInsertDataRow, retPositionCarGifts[intNo][i].trim(), objectSheet2);
        }
        //
        if ("Yes".equals(stringIsExportGifts)) {
          if (Double.parseDouble(retPositionCarGifts[intNo][25]) > 0) {
            intCol = doParseInteger("" + hashtableItemNoCol.get(stringItemNo));
            exeExcel.putDataIntoExcel(intCol, intInsertDataRow, retPositionCarGifts[intNo][24].trim(), objectSheet2);
            exeExcel.putDataIntoExcel(intCol + 1, intInsertDataRow, retPositionCarGifts[intNo][25].trim(), objectSheet2);
            // �Ȧs�UItemNo�֥[�ƶq�B���B
            intCount = doParseInteger("" + hashtableItemNoCount.get(stringItemNo));
            hashtableItemNoCount.put(stringItemNo, "" + (intCount + doParseInteger(retPositionCarGifts[intNo][24].trim())));
            doubleAmt = doParseDouble("" + hashtableItemNoAmt.get(stringItemNo));
            hashtableItemNoAmt.put(stringItemNo, "" + (doubleAmt + doParseDouble(retPositionCarGifts[intNo][25].trim())));
          }
        }
        //
        if (intNo != retPositionCarGifts.length - 1) {
          if (!(retPositionCarGifts[intNo + 1][0].trim().equals(stringPosition) && retPositionCarGifts[intNo + 1][1].trim().equals(stringCar))) {
            intInsertDataRow++;
          }
        } else {
          intInsertDataRow++;
        }
        // �����ɡA�N Sheet2 Copy Sheet1
        intInsertDataRow = exeExcel.doChangePage(intInsertDataRow, objectSheet1, objectSheet2);
      }
      if (intInsertDataRow != intStartDataRow) {
        // doChangePage���٦����
        exeExcel.CopyPage(objectSheet1, objectSheet2);
      } else {
        // doChangePage���n�S�����
        if (exeExcel.getPageNo() > 1) {
          // �]��doChangePage�|�⭶��+1, ��ڤW�w��n�S���, �ҥH���ƭn-1
          exeExcel.setPageNo(exeExcel.getPageNo() - 1);
        }
      }
      // ����
      intPages = exeExcel.getPageNo();
      intDataEndRow = intPages * intPageAllRow - 3;
      if ("Yes".equals(stringIsExportGifts)) {
        exeExcel.putDataIntoExcel(0, intDataEndRow, "�X�p", objectSheet1);
        // ����
        for (int intNo = 0; intNo < vectorItemNo.size(); intNo++) {
          stringItemNo = ("" + vectorItemNo.get(intNo)).trim();
          intCount = doParseInteger("" + hashtableItemNoCount.get(stringItemNo));
          doubleAmt = doParseDouble("" + hashtableItemNoAmt.get(stringItemNo));
          intCol = doParseInteger("" + hashtableItemNoCol.get(stringItemNo));
          if (intCol == 0)
            continue;
          exeExcel.putDataIntoExcel(intCol, intDataEndRow, "" + intCount, objectSheet1);
          exeExcel.putDataIntoExcel(intCol + 1, intDataEndRow, "" + doubleAmt, objectSheet1);
        }
        // �ؽu
        exeExcel.doLineStyle2("X" + (intDataEndRow + 1) + ":" + exeExcel.getExcelColumnName("A", vectorItemNo.size() * 2 + 22) + (intDataEndRow + 1), objectSheet1, "1", "4", "1",
            "3", "1", "4", "1", "3", "1", "2");
        exeExcel.doLineStyle2("A" + (intDataEndRow + 1) + ":W" + (intDataEndRow + 1), objectSheet1, "1", "4", "1", "3", "1", "3", "1", "4", "", "");
        // �Y���S���ذe���� �N�n�R��EXCEL�w���� �õe�k���ؽu
        if (retItemNo.length == 0) {
          exeExcel.doDeleteColumns2("X:Y", objectSheet1);
          for (int intPage = 1; intPage <= intPages; intPage++) {
            exeExcel.doLineStyle2("W" + (6 + 38 * (intPage - 1)) + ":W" + (36 + 38 * (intPage - 1)), objectSheet1, "", "", "1", "3", "", "", "1", "3", "", "");
          }
        }
      } else {
        exeExcel.doDeleteColumns2("X:Y", objectSheet1);
        for (int intPage = 1; intPage <= intPages; intPage++) {
          exeExcel.doLineStyle2("W" + (6 + 38 * (intPage - 1)) + ":W" + (35 + 38 * (intPage - 1)), objectSheet1, "", "", "1", "3", "", "", "1", "3", "", "");
        }
      }
    }

    //
    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("���---" + ((longTime2 - longTime1) / 1000) + "��---");

    // ���� Excel ����
    if (exeExcel != null) {
      Dispatch.call(objectSheet2, "Delete");
      exeExcel.getReleaseExcelObject(retVector);
    }
  }

  //
  public int doParseInteger(String stringNum) throws Exception {
    int intNum = 0;
    //
    if ("".equals(stringNum) || "null".equals(stringNum))
      return 0;
    try {
      intNum = Integer.parseInt(stringNum);
    } catch (Exception e) {
      System.out.println("�L�k��R[" + stringNum + "]�A�^�� 0�C");
      return 0;
    }
    return intNum;
  }

  //
  public double doParseDouble(String stringAmt) throws Exception {
    double doubleAmt = 0;
    //
    if ("".equals(stringAmt) || "null".equals(stringAmt))
      return 0;
    try {
      doubleAmt = Double.parseDouble(stringAmt);
    } catch (Exception e) {
      System.out.println("�L�k��R[" + stringAmt + "]�A�^�� 0�C");
      return 0;
    }
    return doubleAmt;
  }

  public String getCustomName(String stringOrderNo) throws Throwable {
    String stringCustomName = "";
    String stringCustomNameL = "";
    String stringSql = "";
    String retData[][] = null;
    //
    stringSql = "select CustomName " + "from Sale05M091 " + "where OrderNo='" + stringOrderNo + "' " + "and ISNULL(StatusCd,'')='' ";
    retData = dbSale.queryFromPool(stringSql);
    for (int intNo = 0; intNo < retData.length; intNo++) {
      stringCustomNameL = retData[intNo][0].trim();
      if (stringCustomNameL.length() == 0) {
        continue;
      }
      if (stringCustomName.length() > 0) {
        stringCustomName += ",";
      }
      stringCustomName += stringCustomNameL;
    }
    //
    return stringCustomName;
  }

  // ��� Sale05M206
  public String getSpec(String stringItemNo) throws Throwable {
    String stringSpec = "";
    String stringSql = "";
    String retData[][] = null;
    //
    // 0
    stringSql = "select Spec " + "from Sale05M206 " + "where ItemNo='" + stringItemNo + "'";
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length > 0) {
      stringSpec = retData[0][0].trim();
    }
    //
    return stringSpec;
  }

  public String[][] getPositionCarGifts(String stringProjectID1, String stringOrderDateS, String stringOrderDateE, String stringIsExportGifts) throws Throwable {
    String stringSql = "";
    String retData[][] = null;
    //
    // 0
    stringSql = "select (case when T92.HouseCar='House' then T92.Position else '' end) Position, " + "(case when T92.HouseCar='Car' then T92.Position else '' end) Car, "
        + "T90.OrderNo, " + "T90.OrderDate, " + "(select distinct (case when convert(char(10),ContrDate,111)='1900/01/01' then '' " + "else convert(char(10),ContrDate,111) end) "
        + "from A_Sale " + "where ProjectID1=T90.ProjectID1 " + "and HouseCar=(case when T92.HouseCar='House' then 'Position' else 'Car' end) "
        + "and (case when HouseCar='Position' then Position else Car end)=T92.Position " +
        // "and ((ISNULL(OrderNo,'')='' and
        // convert(char(10),OrderDate,111)=T90.OrderDate) or "+
        // "(ISNULL(OrderNo,'')!='' and OrderNo=T90.OrderNo))
        ") ContrDate, " +
        // 5
        "T40.PingSu, " + "T92.ListPrice, " + "T92.DealMoney, " + "T92.GiftMoney, " + "T92.CommMoney, " +
        // 10
        "(T92.DealMoney -T92.GiftMoney -T92.CommMoney -T92.ViMoney) PureMoney, " + "T40.FloorPrice, "
        + "(T92.DealMoney -T92.GiftMoney -T92.CommMoney -T92.ViMoney -T40.FloorPrice) BalaMoney, " + "T90.SaleName1, " + "T90.SaleName2, " +
        // 15
        "T90.SaleName3, " + "T90.SaleName4, " + "T90.SaleName5, " + "T90.SaleName6, " + "T90.SaleName7, " +
        // 20
        "T90.SaleName8, " + "T90.SaleName9, " + "T90.SaleName10, ";
    if ("Yes".equals(stringIsExportGifts)) {
      stringSql = stringSql + "T210.ItemNo, " + "ISNULL(T210.Qty,0) Qty, " + "ISNULL(T210.TotalAmt,0) TotalAmt ";
    } else {
      stringSql = stringSql + "'' ItemNo, " + "'' Qty, " + "'' TotalAmt ";
    }
    stringSql = stringSql + "from Sale05M090 T90, Sale05M040 T40, Sale05M092 T92 ";
    if ("Yes".equals(stringIsExportGifts)) {
      stringSql = stringSql + "left join Sale05M210 T210 " + "on T92.OrderNo=T210.OrderNo " + "and T92.Position=T210.Position ";
    }
    stringSql = stringSql + "where T90.OrderNo=T92.OrderNo " + "and T90.ProjectID1=T40.ProjectID1 " + "and T92.HouseCar=T40.HouseCar " + "and T92.Position=T40.Position "
        + "and T90.ProjectID1='" + stringProjectID1 + "' " + "and ISNULL(T92.StatusCd,'')='' ";
    if (stringOrderDateS.length() > 0) {
      stringSql = stringSql + "and T90.OrderDate>='" + stringOrderDateS + "' ";
    }
    if (stringOrderDateE.length() > 0) {
      stringSql = stringSql + "and T90.OrderDate<='" + stringOrderDateE + "' ";
    }
    stringSql = stringSql + "order by T90.OrderNo, T92.HouseCar desc, T92.Position";
    retData = dbSale.queryFromPool(stringSql);
    //
    return retData;
  }

  public String[][] getProjectID1(String stringProjectID1, String stringOrderDateS, String stringOrderDateE) throws Throwable {
    String stringSql = "";
    String retData[][] = null;
    //
    // 0
    stringSql = "select distinct T90.ProjectID1 from Sale05M090 T90, Sale05M092 T92 where T90.OrderNo=T92.OrderNo and ISNULL(T92.StatusCd,'')='' ";
    if (stringProjectID1.length() > 0) {
      stringSql = stringSql + "and T90.ProjectID1='" + stringProjectID1 + "' ";
    }
    if (stringOrderDateS.length() > 0) {
      stringSql = stringSql + "and T90.OrderDate>='" + stringOrderDateS + "' ";
    }
    if (stringOrderDateE.length() > 0) {
      stringSql = stringSql + "and T90.OrderDate<='" + stringOrderDateE + "' ";
    }
    stringSql = stringSql + "order by T90.ProjectID1";
    retData = dbSale.queryFromPool(stringSql);
    //
    return retData;
  }

  public String[][] getItemNo(String stringProjectID1, String stringOrderDateS, String stringOrderDateE) throws Throwable {
    String stringSql = "";
    String retData[][] = null;

    stringSql = "select distinct T210.ItemNo " + "from Sale05M210 T210, Sale05M090 T90, Sale05M092 T92 " + "where T210.ProjectID1='" + stringProjectID1 + "' "
        + "and T210.TotalAmt > 0 " + "and T210.OrderNo=T90.OrderNo " + "and T210.OrderNo=T92.OrderNo " + "and T210.Position=T92.Position " + "and ISNULL(T92.StatusCd,'')='' ";
    if (stringOrderDateS.length() > 0) {
      stringSql = stringSql + "and T90.OrderDate>='" + stringOrderDateS + "' ";
    }
    if (stringOrderDateE.length() > 0) {
      stringSql = stringSql + "and T90.OrderDate<='" + stringOrderDateE + "' ";
    }
    stringSql = stringSql + "order by T210.ItemNo";
    retData = dbSale.queryFromPool(stringSql);
    //
    return retData;
  }

  public String getInformation() {
    return "---------------�q�����(Sale05R21004)----------------";
  }
}