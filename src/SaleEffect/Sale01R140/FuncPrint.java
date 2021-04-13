package SaleEffect.Sale01R140;

import jcx.jform.bTransaction;
import jcx.db.*;
import jcx.util.*;
import com.jacob.activeX.*;
import com.jacob.com.*;
import java.util.*;
import Farglory.util.FargloryUtil;

public class FuncPrint extends bTransaction {
  FargloryUtil exeUtil = new FargloryUtil();
  talk dbSale = getTalk("" + get("put_dbSale"));
  talk dbAO = getTalk("dbAO");
  talk dbDoc = getTalk("dbDoc");

  public boolean action(String value) throws Throwable {
    // �^�ǭȬ� true ��ܰ��汵�U�Ӫ���Ʈw���ʩάd��
    // �^�ǭȬ� false ��ܱ��U�Ӥ����������O
    // �ǤJ�� value �� "�s�W","�d��","�ק�","�R��","�C�L","PRINT" (�C�L�w�����C�L���s),"PRINTALL" (�C�L�w���������C�L���s) �䤤���@
    
    if (!isBatchCheckOK()) return false;

    long longTime1 = exeUtil.getTimeInMillis();

    doExcelNew();

    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("+�����w��---" + ((longTime2 - longTime1) / 1000) + "��---");
    return false;
  }

  // �ˮ�
  // �e�ݸ���ˮ֡A���T�^�� True
  public boolean isBatchCheckOK() throws Throwable {
    int countOC = 0; // �I�q���ñ���饲���n���@flag
    String retDate = "";
    int countDate = 0;

    // �I�q���
    String strStartDate = getValue("StartDate").trim();  // �I�q���-�_
    if (!"".equals(strStartDate)) {
      retDate = exeUtil.getDateAC(strStartDate, "���(�_)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("StartDate").requestFocus();
        return false;
      }
      setValue("StartDate", retDate);
      countDate++;
      countOC++;
    }
    String strEndDate = getValue("EndDate").trim();      // �I�q���-��
    if (!"".equals(strEndDate)) {
      retDate = exeUtil.getDateAC(strEndDate, "���(��)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("EndDate").requestFocus();
        return false;
      }
      setValue("EndDate", retDate);
      countDate++;
    }
    if (countDate == 1) {
      message("[�I�q���(�_)(��)] ���P�ɭ���C");
      return false;
    }
    
    //�~�Z�ѷ�
    String dateType = this.getValue("dateType");
    if( dateType.length() == 0 ) {
      message("[�~�Z�ѷ�] ���i�ťաC");
      return false;
    }
    if( !("OrderDate".equals(dateType) || "ContrDate".equals(dateType) || "�p�Ĥl�~�����".equals(dateType)) ) {
      message("[�~�Z�ѷ�] �Ѽƿ��~�C(�A�ç�� ��B!)");
      return false; 
    }

    return true;
  }

  public void doExcelNew() throws Throwable {
    //
    talk dbDocCS = getTalk("InvDoc");
    //
    if (dbDocCS == null) {
      messagebox("�L�k�s�u�� �H��-���ʽдڨt�ΡC");
    }
    //
    long longTime1 = exeUtil.getTimeInMillis();
    //
    Hashtable hashtableMoney = new Hashtable();
    String stringStartDate = getValue("StartDate").trim();
    String stringEndDate = getValue("EndDate").trim();
    String dateType = this.getValue("dateType");
    String stringSql = "";
    String stringToday = datetime.getToday1();
    String retArea[][] = null;
    String aryProjectID1[] = null;
    String stringArea = "";
    String stringProjectID1 = "";
    String stringSaleType = "";
    String retData[][] = null;
    String retData_2[][] = null;
    String retData_3[][] = null;
    double doubleTemp = 0;
    String stringNotInProjectID1Sql = "";
    String stringNotInProjectID1Sql2 = "";
    Vector vcTemp = new Vector();
    String stringNotExistProjectID1s = "";
    boolean blnHasProjectID1 = false;
    /*
     * ���ݤ��� 701/702/703/704/710/730 POP 781/782/783/784/785/786 ���� 740/741/743/744/745/746/748/749 ���x 750 �q�� 771 �s�� 772 �q�v�| 773 DM(����) 791/792 �j���ӤH���� 742/892/803 ���� 861/862 ²�T 863
     */
    stringSql = "SELECT T22.Area, '', T22.ProjectID " 
              + "FROM Sale09M020 T20, Sale09M022 T22 " 
              + "WHERE T20.TypeGUID=T22.TypeGUID " 
              + "AND T20.UseForType='�C��^�X��' "
              + "AND T20.ChooseType='A' " 
              + "AND '" + stringToday + "' BETWEEN T20.StartDate AND T20.EndDate " 
              + "ORDER BY T22.OrderByNo";
    retArea = dbSale.queryFromPool(stringSql);
    if (retArea.length == 0) {
      message("�d�L���");
      if (dbDocCS != null) dbDocCS.close();
      return;
    }
    if ("B3774".equals(getUser())) {
      // retArea = new String[][]{{"�s����","H82A"},{"���M��","H99A,H105A"}};
    }
    // �ư��קO
    stringSql = "SELECT RTRIM(ProjectID) " + "FROM AO_SPSale03R530B_DenyProjectID";
    retData = dbAO.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      stringNotInProjectID1Sql = stringNotInProjectID1Sql.length() == 0 ? retData[intRow][0] : stringNotInProjectID1Sql + "','" + retData[intRow][0];
    }
    stringNotInProjectID1Sql2 = "AND (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END) NOT IN ('" + stringNotInProjectID1Sql + "') ";
    stringNotInProjectID1Sql = "AND ProjectID1 NOT IN ('" + stringNotInProjectID1Sql + "') ";
    
    //�~�Z�@�Τ������
    String saleQDate = "";
    if( "�p�Ĥl�~�����".equals(dateType) ) {
      saleQDate = "AND OrderDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' "
                + "AND ContrDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' ";
    }else {
      saleQDate = "AND " + dateType + " BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' ";
    }
    
    // �~�Z
    stringSql = "SELECT DISTINCT RTRIM(ProjectID1) " 
              + "FROM A_Sale " 
              + "WHERE 1=1 "
              + saleQDate
              + "AND DealMoney > 0 "
              + "AND (LEN(Position) > 0 OR LEN(Car) > 0) " 
//              + "AND L_Com = '8' " 
              + stringNotInProjectID1Sql;
    retData = dbSale.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      vcTemp.addElement(retData[intRow][0]);
    }
    
    // �ӤH
    stringSql = "SELECT DISTINCT RTRIM(CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END) " + "FROM MIS_NewAOMixView WITH (NOLOCK) "
        + "WHERE CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND (Nego_Type IN ('�s�ӤH','�ƨӳX') OR Nego_Way='�ӹq') "
        + "AND ISNULL(value_point,0)>0 " + stringNotInProjectID1Sql2;
    retData = dbAO.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      if (vcTemp.indexOf(retData[intRow][0]) == -1) {
        vcTemp.addElement(retData[intRow][0]);
      }
    }
    // �O��
    stringSql = "SELECT DISTINCT RTRIM(ProjectID1) " + "FROM Doc3M014_UseMoney_AC_view " + "WHERE RTRIM(CostID)+RTRIM(CostID1) IN (SELECT RTRIM(CostID)+RTRIM(CostID1) " + "FROM Doc2M020 "
        + "WHERE BudgetID LIKE 'B%' " + "AND BudgetID NOT IN ('BF10', 'BF20') " + "AND  ComNo  =  'CS') " + "AND InOut='O' " + "AND DepartNo<>'0531' " + "AND CostID1<>'' " + "AND UseDate BETWEEN '"
        + stringStartDate + "' AND '" + stringEndDate + "' " + stringNotInProjectID1Sql;
    retData = dbDoc.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      if (vcTemp.indexOf(retData[intRow][0]) == -1) {
        vcTemp.addElement(retData[intRow][0]);
      }
    }

    // 20180716 �j��ɱM�סA������Ƥ]���
    String[] staticProjectList = {
        "H92A", "H93A", "H95A", "H98A", "H99A", "H101A", "H102A", "H103A", "H106A", "H100A", "H110A", "H111A" };
    for (int chk = 0; chk < staticProjectList.length; chk++) {
      if (vcTemp.indexOf(staticProjectList[chk]) == -1) {
        vcTemp.addElement(staticProjectList[chk]);
      }
    }

    /*
     * // �O��(CS) stringSql = "SELECT DISTINCT RTRIM(ProjectID1) "+ "FROM Doc3M014_UseMoney_AC_view "+ "WHERE RTRIM(CostID)+RTRIM(CostID1) IN (SELECT RTRIM(CostID)+RTRIM(CostID1) "+ "FROM Doc2M020 "+
     * "WHERE BudgetID LIKE 'B%' "+ "AND BudgetID NOT IN ('BF10', 'BF20') "+ ") "+ "AND InOut='O' "+ "AND DepartNo<>'0531' "+ "AND CostID1<>'' "+
     * "AND UseDate BETWEEN '"+stringStartDate+"' AND '"+stringEndDate+"' "+ stringNotInProjectID1Sql; retData = dbDocCS.queryFromPool(stringSql); for(int intRow=0; intRow<retData.length; intRow++){
     * if(vcTemp.indexOf(retData[intRow][0]) == -1){ vcTemp.addElement(retData[intRow][0]); } }
     */
    //
    aryProjectID1 = (String[]) vcTemp.toArray(new String[0]);
    Arrays.sort(aryProjectID1);
    //
    for (int intRow = 0; intRow < aryProjectID1.length; intRow++) {
      blnHasProjectID1 = false;
      for (int intNo = 0; intNo < retArea.length; intNo++) {
        if (retArea[intNo][2].indexOf(aryProjectID1[intRow]) >= 0) {
          blnHasProjectID1 = true;
          break;
        }
      }
      if (!blnHasProjectID1) {
        stringNotExistProjectID1s = stringNotExistProjectID1s.length() == 0 ? aryProjectID1[intRow] : stringNotExistProjectID1s + "," + aryProjectID1[intRow];
      }
    }

    //
    if (stringNotExistProjectID1s.length() > 0) {
      message("�קO���]�w��\"�U���ϰ���@\"�A�Х��]�w�G" + stringNotExistProjectID1s);
      return;
    }
    //
    for (int intNo = 0; intNo < retArea.length; intNo++) {
      for (int intRow = 0; intRow < aryProjectID1.length; intRow++) {
        stringProjectID1 = aryProjectID1[intRow];
        if (retArea[intNo][2].indexOf(aryProjectID1[intRow]) >= 0) {
          retArea[intNo][1] = retArea[intNo][1].length() == 0 ? aryProjectID1[intRow] : retArea[intNo][1] + "," + aryProjectID1[intRow];
        }
      }
    }
    //
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
    String stringFilePath = "G:\\��T��\\Excel\\SaleEffect\\Sale01R140_Emaker_New_PA1.xltx";
    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    int intStartDataRow = 1;
    int intInsertDataRow = intStartDataRow;
    int intStartDataCol = 6;
    int intInsertDataCol = intStartDataCol;
    String stringCostID = "";
    String stringKEY = "";
    String[] arrayCostID = null;
    String stringAOProjectID1Sql = "";
    //
    Dispatch objectWorkbook = (Dispatch) retVector.get(4);
    Dispatch objectApplication = Dispatch.get(objectWorkbook, "Application").toDispatch();
    Dispatch.put(objectApplication, "Calculation", "3");
    //
    // ���
    String stringExcelDate = "";
    if( "OrderDate".equals(dateType) ) {
      stringExcelDate = "�I�q";
    }else if( "ContrDate".equals(dateType) ) {
      stringExcelDate = "ñ��";
    }else {
      stringExcelDate = "�I�q+ñ��";
    }
    if ( !"".equals(stringStartDate) ) stringExcelDate += "���:" + stringStartDate + "��" + stringEndDate;
    
    exeExcel.putDataIntoExcel(0, 0, stringExcelDate, objectSheet1);
    // 20180925 �ഫ value_point = null �����
    String getValueNullPointSql = "SELECT Small_ID, Value_Point, count(*) FROM Cus_SmallMixView WHERE Value_Point is Null And Create_Time between '" + stringStartDate + " 00:00:00' and '"
        + stringEndDate + " 23:59:59' GROUP BY Small_ID, Value_Point, Create_Time ORDER BY Create_Time";
    String[][] listData = dbAO.queryFromPool(getValueNullPointSql);
    for (int idx = 0; idx < listData.length; idx++) {
      String updateValuePointValue = "1";
      switch (Integer.parseInt(listData[idx][2])) {
      case 1:
        updateValuePointValue = "1";
        break;
      case 2:
        updateValuePointValue = "0.5";
        break;
      case 3:
        updateValuePointValue = "0.333";
        break;
      case 4:
        updateValuePointValue = "0.25";
        break;
      case 5:
        updateValuePointValue = "0.2";
        break;
      }
      String transValuePointSql = "UPDATE [110701_cussmallmedia] SET value_point = '" + updateValuePointValue + "' WHERE Small_ID = " + Integer.parseInt(listData[idx][0]) + "";
      dbAO.execFromPool(transValuePointSql);
    }
    //
    // �ץX���
    for (int intNo = 0; intNo < retArea.length; intNo++) {
      if (retArea[intNo][1].length() == 0) {
        continue;
      }
      //
      stringArea = retArea[intNo][0];
      aryProjectID1 = retArea[intNo][1].split(",");
      // System.out.println("stringArea = "+stringArea);
      //
      // �ƻs��
      exeExcel.doCopyColumns(intInsertDataCol + 9, intInsertDataCol + 17, objectSheet1);
      //
      hashtableMoney = getMoneyHashtableUnion("B", stringStartDate, stringEndDate, aryProjectID1, exeUtil, dbDoc, dbDocCS);
      //
      for (int intRow = 0; intRow < aryProjectID1.length; intRow++) {
        intInsertDataRow = intStartDataRow;
        stringProjectID1 = aryProjectID1[intRow];
        // System.out.println("stringProjectID1 = "+stringProjectID1);
        //
        if ("H82A".equals(stringProjectID1)) {
          stringAOProjectID1Sql = "WHERE Series_Name LIKE '%H82A' ";
        } else {
          stringAOProjectID1Sql = "WHERE Series_Name='" + stringProjectID1 + "' ";
        }
        //
        /*
         * stringSql = "SELECT ProjectID "+ "FROM A_Group1 "+ "WHERE ProjectID1='"+stringProjectID1+"'"; 
         * retData = dbSale.queryFromPool(stringSql); 
         * if(retData.length > 0){ 
         *  stringSaleType = "��";
         * }else{ 
         *  stringSaleType = "�P��"; 
         * }
         */
        stringSaleType = "�P��";
        // �ƻs��
        exeExcel.doCopyColumns(intInsertDataCol + 3, intInsertDataCol + 5, objectSheet1);
        // �קO
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, stringProjectID1, objectSheet1);
        // �~�Z******************************************************************************************************************
        // �P����B-�ؼ�
        stringSql = "SELECT ISNULL(SUM(Targets),0) " 
                  + "FROM A_STarMM " 
                      + "WHERE ProjectID='" + stringProjectID1 + "' " 
                  + "AND YearMM BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        retData = dbSale.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][0], objectSheet1);
        // �P����B-���
        if (stringSaleType.equals("�P��")) {
          stringSql = "SELECT ISNULL(SUM(DealMoney),0) " 
                    + "FROM A_Sale " 
                    + "WHERE ProjectID1='" + stringProjectID1 + "' " 
                    + saleQDate
                    + "AND (LEN(Position) > 0 OR LEN(Car) > 0) ";
//                    + "AND L_Com = '8'";
        } else {
          stringSql = "SELECT ISNULL(SUM(OKSale),0) " + "FROM Sale02M050 " + "WHERE ProjectID1='" + stringProjectID1 + "' " 
                    + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][0], objectSheet1);
        // �C��^�X���榨���v-�ؼ�
        intInsertDataRow = 8;
        stringSql = "SELECT CostRate/100 " + "FROM Doc7M020 " + "WHERE STATUS='U' " + "AND STATUS!='C' " + "AND FunctionType='B' " + "AND ProjectID1='" + stringProjectID1 + "'";
        retData = dbDoc.queryFromPool(stringSql);
        if (retData.length > 0) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        } else {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, "0", objectSheet1);
        }
        // �C��ӤH�ӹq************************************************************************************************************
        intInsertDataRow = 12;
        // ����
        stringSql = "SELECT ISNULL(SUM(CASE WHEN Nego_Type='�s�ӤH' THEN value_point ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN Nego_Type IN ('�s�ӤH','�ƨӳX') THEN value_point ELSE 0 END),0),"
            + "ISNULL(SUM(CASE WHEN Nego_Way='�ӹq' THEN value_point ELSE 0 END),0) " + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        retData = dbAO.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        exeExcel.putDataIntoExcel(intInsertDataCol + 1, intInsertDataRow, retData[0][1], objectSheet1);
        exeExcel.putDataIntoExcel(intInsertDataCol + 2, intInsertDataRow++, retData[0][2], objectSheet1);
        //
        // 1 ���ݤ��� 2 POP 1 ���� 2 ���x 3 �q�� 4 �s�� 5 �q�v�| 6 DM(����) 7 �j���ӤH���� 8 �~���X�@ 9 ��L 10 ���� 11 ²�T
        stringSql = "SELECT ISNULL(ROUND(SUM(CASE WHEN NewCode2='C01' THEN value_point ELSE 0 END),1),0) MediaSum1, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='K' THEN value_point ELSE 0 END),1),0) MediaSum2, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I04' THEN value_point ELSE 0 END),1),0) MediaSum3, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I05' THEN value_point ELSE 0 END),1),0) MediaSum4, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I06' THEN value_point ELSE 0 END),1),0) MediaSum5, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I07' THEN value_point ELSE 0 END),1),0) MediaSum6, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I08' THEN value_point ELSE 0 END),1),0) MediaSum7, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I09' THEN value_point ELSE 0 END),1),0) MediaSum8, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2 IN ('I10','I11','I12') THEN value_point ELSE 0 END),1),0) MediaSum9, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode4='I120101' THEN value_point ELSE 0 END),1),0) MediaSum10, " + "0 MediaSum11, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='D' THEN value_point ELSE 0 END),1),0) MediaSum12, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='L' THEN value_point ELSE 0 END),1),0) MediaSum13 "
            + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Type='�s�ӤH'";
        retData = dbAO.queryFromPool(stringSql);
        stringSql = "SELECT ISNULL(ROUND(SUM(CASE WHEN NewCode2='C01' THEN value_point ELSE 0 END),1),0) MediaSum1, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='K' THEN value_point ELSE 0 END),1),0) MediaSum2, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I04' THEN value_point ELSE 0 END),1),0) MediaSum3, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I05' THEN value_point ELSE 0 END),1),0) MediaSum4, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I06' THEN value_point ELSE 0 END),1),0) MediaSum5, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I07' THEN value_point ELSE 0 END),1),0) MediaSum6, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I08' THEN value_point ELSE 0 END),1),0) MediaSum7, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I09' THEN value_point ELSE 0 END),1),0) MediaSum8, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2 IN ('I10','I11','I12') THEN value_point ELSE 0 END),1),0) MediaSum9, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode4='I120101' THEN value_point ELSE 0 END),1),0) MediaSum10, " + "0 MediaSum11, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='D' THEN value_point ELSE 0 END),1),0) MediaSum12, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='L' THEN value_point ELSE 0 END),1),0) MediaSum13 "
            + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Type IN ('�s�ӤH','�ƨӳX')";
        retData_2 = dbAO.queryFromPool(stringSql);
        stringSql = "SELECT ISNULL(ROUND(SUM(CASE WHEN NewCode2='C01' THEN value_point ELSE 0 END),1),0) MediaSum1, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='K' THEN value_point ELSE 0 END),1),0) MediaSum2, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I04' THEN value_point ELSE 0 END),1),0) MediaSum3, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I05' THEN value_point ELSE 0 END),1),0) MediaSum4, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I06' THEN value_point ELSE 0 END),1),0) MediaSum5, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I07' THEN value_point ELSE 0 END),1),0) MediaSum6, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I08' THEN value_point ELSE 0 END),1),0) MediaSum7, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2='I09' THEN value_point ELSE 0 END),1),0) MediaSum8, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode2 IN ('I10','I11','I12') THEN value_point ELSE 0 END),1),0) MediaSum9, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode4='I120101' THEN value_point ELSE 0 END),1),0) MediaSum10, " + "0 MediaSum11, "
            + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='D' THEN value_point ELSE 0 END),1),0) MediaSum12, " + "ISNULL(ROUND(SUM(CASE WHEN NewCode1='L' THEN value_point ELSE 0 END),1),0) MediaSum13 "
            + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
            // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Way='�ӹq'";
        retData_3 = dbAO.queryFromPool(stringSql);
        for (int x = 0; x < retData[0].length; x++) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][x], objectSheet1);
          exeExcel.putDataIntoExcel(intInsertDataCol + 1, intInsertDataRow, retData_2[0][x], objectSheet1);
          exeExcel.putDataIntoExcel(intInsertDataCol + 2, intInsertDataRow++, retData_3[0][x], objectSheet1);
        }
        // �ƨӳX
        intInsertDataRow = 28;
        stringSql = "SELECT SUM(value_point) " + "FROM MIS_NewAOMixView WITH (NOLOCK) " +
        // "WHERE (CASE WHEN Series_Name='NEWH82A' THEN 'H82A' ELSE Series_Name END)='"+stringProjectID1+"' "+
            stringAOProjectID1Sql + "AND CONVERT(char(10),Create_Time,111) BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "' " + "AND Nego_Type='�ƨӳX'";
        retData = dbAO.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        // �C�馨����B*************************************************************************************************
        // 1 ���ݤ��� 2 POP 1 ���� 2 ���x 3 �q�� 4 �s�� 5 �q�v�| 6 DM(����) 7 �j���ӤH���� 8 �~���X�@ 9 ��L 10 ���� 11 ²�T
        intInsertDataRow = 46;
        if (stringSaleType.equals("�P��")) {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN DealMoney ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN DealMoney ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN DealMoney ELSE 0 END),0),"
                    + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN DealMoney ELSE 0 END),0),"
                    + "0, " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN DealMoney ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN DealMoney ELSE 0 END),0) "
                    + "FROM A_Sale " 
                    + "WHERE ProjectID1='" + stringProjectID1 + "' "
                    + saleQDate
                    + "AND (LEN(Position) > 0 OR LEN(Car) > 0) ";
//                    + "AND L_Com = '8'";
        } else {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN OKSale ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN OKSale ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN OKSale ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN OKSale ELSE 0 END),0)," + "0, "
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN OKSale ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN OKSale ELSE 0 END),0) " + "FROM Sale02M050 "
              + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        for (int x = 0; x < retData[0].length; x++) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][x], objectSheet1);
        }
        // �C�馨����*************************************************************************************************
        // 1 ���ݤ��� 2 POP 1 ���� 2 ���x 3 �q�� 4 �s�� 5 �q�v�| 6 DM(����) 7 �j���ӤH���� 8 �~���X�@ 9 ��L 10 ���� 11 ²�T
        intInsertDataRow = 61;
        if (stringSaleType.equals("�P��")) {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN 1 ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN 1 ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN 1 ELSE 0 END),0)," + "0, "
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN 1 ELSE 0 END),0) " 
              + "FROM A_Sale "
              + "WHERE ProjectID1='" + stringProjectID1 + "' "
              + saleQDate
//              + "AND L_Com = '8' "
              + "AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
              + "AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
              + "AND DealMoney > 0";
        } else {
          stringSql = "SELECT ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='C01' THEN 1 ELSE 0 END),0), " + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='K' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I04' THEN 1 ELSE 0 END),0)," + "iSNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I05' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I06' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I07' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I08' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3)='I09' THEN 1 ELSE 0 END),0),"
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,3) IN ('I10','I11','I12') THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SSMediaID='I120101' THEN 1 ELSE 0 END),0)," + "0, "
              + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='D' THEN 1 ELSE 0 END),0)," + "ISNULL(SUM(CASE WHEN SUBSTRING(SSMediaID,1,1)='L' THEN 1 ELSE 0 END),0) " + "FROM Sale02M050 "
              + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        for (int x = 0; x < retData[0].length; x++) {
          exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow++, retData[0][x], objectSheet1);
        }
        // �`����X�p
        intInsertDataRow = 76;
        if (stringSaleType.equals("�P��")) {
          stringSql = "SELECT COUNT(*) Counts " 
                    + "FROM A_Sale " 
                    + "WHERE ProjectID1='" + stringProjectID1 + "' "
                    + saleQDate
//                    + "AND L_Com = '8' " 
                    + "AND (LEN(Position) > 0 OR LEN(PositionRent) > 0) " 
                    + "AND SUBSTRING(Position,1,1) NOT IN ('+','-') " 
                    + "AND DealMoney > 0";
        } else {
          stringSql = "SELECT COUNT(*) Counts " + "FROM Sale02M050 " + "WHERE ProjectID1='" + stringProjectID1 + "' " + "AND BuyerDate BETWEEN '" + stringStartDate + "' AND '" + stringEndDate + "'";
        }
        retData = dbSale.queryFromPool(stringSql);
        exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0], objectSheet1);
        // �C��^�X����*******************************************************************************************************
        // 1 ���ݤ��� 2 POP 1 ���� 2 ���x 3 �q�� 4 �s�� 5 �q�v�| 6 DM(����) 7 �j���ӤH���� 8 �~���X�@ 9 ��L 10 ���� 11 ²�T
        // intInsertDataRow = 79;
        for (int intRowL = 67; intRowL < 94; intRowL++) {
          stringCostID = exeExcel.getDataFromExcel2(5, intRowL, objectSheet1);
          if ("".equals(stringCostID)) continue;
          arrayCostID = convert.StringToken(stringCostID, "\n");
          doubleTemp = 0;
          for (int intL = 0; intL < arrayCostID.length; intL++) {
            stringCostID = arrayCostID[intL];
            if ("".equals(stringCostID)) continue;
            stringKEY = stringProjectID1 + "%-%" + stringCostID;
            //
            doubleTemp += exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
            System.out.println("00(" + intL + ")stringKEY(" + stringKEY + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")--------------------------------------");
          }
          System.out.println("11Col(" + intInsertDataCol + ")Row(" + intRowL + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")--------------------------------------");
          exeExcel.putDataIntoExcel(intInsertDataCol, intRowL, convert.FourToFive("" + doubleTemp, 4), objectSheet1);
        }
        /*
         * arrayMoney = (String[])hashtableMoney.get(stringProjectID1); if(arrayMoney == null){ arrayMoney = new String[arrayCostID.length]; for(int intNoL=0; intNoL<arrayMoney.length; intNoL++)
         * arrayMoney[intNoL] = "0"; } for(int x=0; x<arrayMoney.length-1; x++){
         * System.out.println("Col("+intInsertDataCol+")Row("+(intInsertDataRow+1)+")Data("+arrayMoney[x]+")-------------------------------"); exeExcel.putDataIntoExcel(intInsertDataCol,
         * intInsertDataRow++, arrayMoney[x], objectSheet1); }
         */

        // �`�馨���X�p
        intInsertDataRow = 93;
        /*
         * stringSql = "SELECT SUM(RealMoney) RealMoney "+ "FROM Z_CoReaMM "+ "WHERE ProjectID1='"+stringProjectID1+"' "+ "AND YYMM BETWEEN '"+stringStartDate+"' AND '"+stringEndDate+"' "+
         * "AND (ComNo='06' OR ComNo='20' OR ComNo IS NULL OR LEN(ComNo)=0) "; retData = dbSale.queryFromPool(stringSql); exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, retData[0][0],
         * objectSheet1);
         */
        // exeExcel.putDataIntoExcel(intInsertDataCol, intInsertDataRow, arrayMoney[arrayMoney.length-1], objectSheet1);
        // �C��^�X���榨���v***Excel�۰ʭp�⤣�μg******************************************************************
        // �ӤH����***Excel�۰ʭp�⤣�μg******************************************************************
        //
        intInsertDataCol = intInsertDataCol + 3;
      }
      //
      for (int intDel = 1; intDel <= 6; intDel++) {
        exeExcel.doDeleteColumns(intInsertDataCol, objectSheet1);
      }
      //
      exeExcel.putDataIntoExcel(intInsertDataCol, 1, stringArea, objectSheet1);
      //
      intInsertDataCol = intInsertDataCol + 3;
    }

    // �R���h�l��
    for (int intDel = 1; intDel <= 18; intDel++) {
      exeExcel.doDeleteColumns(intInsertDataCol, objectSheet1);
    }
    //
    Dispatch.put(objectApplication, "Calculation", "1");
    //
    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("���---" + ((longTime2 - longTime1) / 1000) + "��---");

    // ���� Excel ����
    if (exeExcel != null) {
      exeExcel.getReleaseExcelObject(retVector);
    }
  }

  // ��Ʈw Doc
  public Hashtable getMoneyHashtable(String stringDateStart, String stringDateEnd, String[] aryProjectID, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS) throws Throwable {
    return getMoneyHashtableUnion("A", stringDateStart, stringDateEnd, aryProjectID, exeUtil, dbDoc, dbDocCS);
  }

  public Hashtable getMoneyHashtableUnion(String stringType, String stringDateStart, String stringDateEnd, String[] aryProjectID, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS) throws Throwable {
    Hashtable hashtableMoney = new Hashtable();
    String stringProjectID1AndSql = "";
    //
    for (int intNo = 0; intNo < aryProjectID.length; intNo++) {
      if (!"".equals(stringProjectID1AndSql)) stringProjectID1AndSql += ", ";
      stringProjectID1AndSql += " '" + aryProjectID[intNo] + "' ";
      //
      System.out.println(intNo + "-------------------------------------" + aryProjectID[intNo]);
    }
    if (!"".equals(stringProjectID1AndSql)) stringProjectID1AndSql = " AND  ProjectID1 IN (" + stringProjectID1AndSql + ") ";
    //
    doCetMoney(stringType, stringDateStart, stringDateEnd, stringProjectID1AndSql, exeUtil, dbDoc, dbDocCS, hashtableMoney);
    return hashtableMoney;
  }

  public void doCetMoney(String stringType, String stringDateStart, String stringDateEnd, String stringProjectID1AndSql, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS, Hashtable hashtableMoney)
      throws Throwable {
    String stringSql = "";
    String stringAndSql11Date = "";
    String stringAndSql14 = "";
    String stringDate = "096/11/26";
    String[][] retData = new String[0][0];
    //
    stringDateStart = exeUtil.getDateConvert(stringDateStart);
    stringDateEnd = exeUtil.getDateConvert(stringDateEnd);
    //
    if (!"".equals(stringDateStart)) stringAndSql11Date = " AND  UseDate  >=  '" + stringDateStart + "' \n";
    if (!"".equals(stringDateEnd)) stringAndSql11Date += " AND  UseDate  <=  '" + stringDateEnd + "' \n";
    // 0 �קO 1 CostID 2 CostID1 3 SSMediaID 4 ���B
    // 0 �ĵo���B
    stringSql = " SELECT  ProjectID1,  CostID,  CostID1,  SSMediaID,  SUM(RealMoney) \n" + " FROM  Doc3M014_UseMoney_AC_view \n"
        + " WHERE  RTRIM(CostID)+RTRIM(CostID1)  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc2M020 \n" + " WHERE  BudgetID  LIKE  'B%' \n" + " AND  BudgetID NOT IN ('BF10',  'BF20') "
        + " AND  ComNo  =  'CS' " + " ) \n" + " AND  InOut  =  'O' \n" + " AND  DepartNo  <>  '0531' \n" + "" + stringProjectID1AndSql + " \n" + " AND  CostID1  <> '' " + stringAndSql11Date
        + " GROUP BY ProjectID1,  CostID,  CostID1,  SSMediaID \n";
    retData = dbDoc.queryFromPool(stringSql);
    doData(stringType, retData, exeUtil, hashtableMoney);
    /*
     * if(dbDocCS != null) { System.out.println("�H��---------------------------------------1") ; retData = dbDocCS.queryFromPool(stringSql.replaceAll("AND  ComNo  =  'CS'", "")) ; doData(stringType,
     * retData, exeUtil, hashtableMoney) ; }
     */
  }

  public void doData(String stringType, String[][] retData, FargloryUtil exeUtil, Hashtable hashtableMoney) throws Throwable {
    int intPos = 0;
    String stringProjectID1 = "";
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostIDLimit = "";
    String stringMoney = "";
    String stringKEY = "";
    String stringSSMediaID = "";
    String[] arrayCostIDLimit = null;
    String[] arrayMoney = null;
    double doubleTemp = 0;
    // 0 �קO 1 CostID 2 CostID1 3 SSMediaID 4 ���B
    for (int intNo = 0; intNo < retData.length; intNo++) {
      stringProjectID1 = retData[intNo][0].trim();
      stringCostID = retData[intNo][1].trim();
      stringCostID1 = retData[intNo][2].trim();
      stringSSMediaID = retData[intNo][3].trim();
      stringMoney = retData[intNo][4].trim();
      //
      if ("B".equals(stringType)) {
        if (stringSSMediaID.length() < 9) {
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, 0, 1);
        } else {
          stringSSMediaID = convert.StringToken(stringSSMediaID, "-")[0];
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, stringSSMediaID.length() - 9, stringSSMediaID.length() - 8);
        }

        if ("C".equals(stringSSMediaID)) {
          stringCostID = "70";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // C ���ݤ���(���ݳq��)
        if ("K".equals(stringSSMediaID)) {
          stringCostID = "78";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // K POP�q��
        if ("I".equals(stringSSMediaID)) {
          stringCostID = "89";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // I �C��q��
        if ("D".equals(stringSSMediaID)) {
          stringCostID = "86";
          stringCostID1 = "1";
          stringSSMediaID = "";
        } // D �����q��
        if ("L".equals(stringSSMediaID)) {
          stringCostID = "86";
          stringCostID1 = "3";
          stringSSMediaID = "";
        } // L ²�T�q��
      }
      stringKEY = stringProjectID1 + "%-%" + stringCostID + stringCostID1;
      doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
      System.out.println("��1(" + intNo + ")stringKEY(" + stringKEY + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")--------------------------------------");
      hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
      //
      stringKEY = stringProjectID1 + "%-%ALL";
      doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
      // System.out.println("��2("+intNo+")stringKEY("+stringKEY+")("+convert.FourToFive(""+doubleTemp, 4)+")--------------------------------------") ;
      hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
    }
  }

  public String getInformation() {
    return "---------------�C��^�X��(�ʤ�)(�P�s���)(Sale01R140_AO5_New_PA1_2)----------------";
  }
}