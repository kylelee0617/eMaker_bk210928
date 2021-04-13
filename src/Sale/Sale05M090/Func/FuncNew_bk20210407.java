package Sale.Sale05M090.Func;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import Farglory.util.FargloryUtil;
import Farglory.util.MLPUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FuncNew_bk20210407 extends bTransaction {
  public boolean action(String value) throws Throwable {
    System.out.println("chk==>" + getUser() + " , value==>�s�W");
    
    if (getUser() != null && getUser().toUpperCase().equals("B9999")) {
      messagebox("�s�W�v�������\!!!");
      return false;
    }
    
    //
    JTable jtableTable1 = getTable("table1");
    if (jtableTable1.getRowCount() == 0) {
      message("[�Ȥ���] ���i�ť�");
      return false;
    }
    
    getButton("ButtonSSMediaID").doClick();
    getButton("ButtonSetSaleID").doClick();

    MLPUtils mlpUtils = new MLPUtils();
    FargloryUtil exeUtil = new FargloryUtil();
    talk dbDoc = getTalk("" + get("put_dbDoc"));
    String stringSaleWay = getValue("SaleWay").trim();
    String stringProjectID1 = getValue("field1").trim();
    String stringOrderNo = getValue("OrderNo").trim();
    String stringDate = exeUtil.getDateConvert(getValue("field2").trim());
    String stringTrxDate = getValue("field2").trim();
    JTabbedPane jtabbedpane1 = getTabbedPane("tab1");
    float floatPercentage = 0;
    String stringCustomNo = "";
    String stringCustomName = "";
    String stringPercentage = "";
    String stringAddress = "";
    String stringNationality = "";// 20090414
    String stringTel = "";
    String stringTel2 = "";// 2010-4-16 �s�W�q��2
    String stringCity = "";
    String stringTown = "";
    String stringZIP = "";
    String stringSql = "";
    String stringCellphone = "";
    String[][] retTown = null;

    // 20200620 Kyle : ����ˮ�PASS�A�إ�AML�ˮ֪���
    Map amlCons = new HashMap();
    amlCons.put("OrderNo", stringOrderNo);
    amlCons.put("ProjectID1", stringProjectID1);
    amlCons.put("TrxDate", stringTrxDate);
    amlCons.put("funcName", "�ʫ��ҩ���");
    amlCons.put("ActionName", "�s�W");

    for (int intRow = 0; intRow < jtableTable1.getRowCount(); intRow++) {
      stringCustomNo = ("" + getValueAt("table1", intRow, "CustomNo")).trim();
      stringCustomName = ("" + getValueAt("table1", intRow, "CustomName")).trim();
      stringPercentage = ("" + getValueAt("table1", intRow, "Percentage")).trim();
      stringNationality = ("" + getValueAt("table1", intRow, "Nationality")).trim();// 20090414
      stringAddress = ("" + getValueAt("table1", intRow, "Address")).trim();
      stringTel = ("" + getValueAt("table1", intRow, "Tel")).trim();
      stringTel2 = ("" + getValueAt("table1", intRow, "Tel2")).trim();// 2010-4-16 �s�W�q��2
      stringCity = ("" + getValueAt("table1", intRow, "City")).trim();
      stringTown = ("" + getValueAt("table1", intRow, "Town")).trim();
      stringZIP = ("" + getValueAt("table1", intRow, "ZIP")).trim();
      stringCellphone = ("" + getValueAt("table1", intRow, "Cellphone")).trim();

      // �M�������Ҧr�����ť�
      // setValueAt("table1" , stringCustomNo, intRow, "CustomNo");

      // �ˮ�
      if (!"".equals(stringCellphone) && stringCellphone.length() != 10) {
        messagebox("�� " + (intRow + 1) + " �C��[��ʹq��] �j�p���� 10 �X�C");
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        return false;
      }
      //
      if ("1".equals(stringNationality)) {
        if (stringCustomNo.length() == 0) {
          messagebox("�� " + (intRow + 1) + " �C��[�νs/�����Ҹ�] ���i�ť�!�C");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
        if (stringCustomNo.length() != 8 && stringCustomNo.length() != 10) {
          messagebox("�� " + (intRow + 1) + " �C��[�νs/�����Ҹ�] ���׿��~!�C");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
        if (stringCustomNo.length() == 8 && check.isCoId(stringCustomNo) == false) {
          messagebox("�� " + (intRow + 1) + " �C��[�νs/�����Ҹ�] �Τ@�s�����~!�C");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
        if (stringCustomNo.length() == 10 && check.isID(stringCustomNo) == false) {
          messagebox("�� " + (intRow + 1) + " �C��[�νs/�����Ҹ�] �����Ҹ����~!�C");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
      }
      if ("4".equals(stringNationality)) {
        if (stringCustomNo.length() != 9) {
          messagebox("�� " + (intRow + 1) + " �C��[�νs/�����Ҹ�] ���׿��~!�C");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
      }
      //
      stringSql = " SELECT  ZIP " + " FROM  Town b " + " WHERE  Coun   IN  (SELECT  Coun  FROM  City  WHERE  CounName='" + stringCity + "') " + " AND  TownName  =  '" + stringTown
          + "' ";
      retTown = dbDoc.queryFromPool(stringSql);
      if (retTown.length == 0) {
        messagebox("�� " + (intRow + 1) + " �C��[����][�m��] ���Y�����T�C");
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        return false;
      }
      if (!stringZIP.equals(retTown[0][0].trim())) {
        if (stringZIP.length() > 3)
          stringZIP = stringZIP.substring(0, 3);
        if (!stringZIP.equals(retTown[0][0].trim())) {
          messagebox("�� " + (intRow + 1) + " �C��[�l���ϸ�] �����T�C");
          jtableTable1.setRowSelectionInterval(intRow, intRow);
          return false;
        }
      }
      // [�νs/�����Ҹ�]
      if ("1".equals(stringNationality) && stringCustomNo.length() == 0) {// 20090414
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("����:" + (intRow + 1) + "-[�νs/�����Ҹ�] ���i�ť�");
        return false;
      }
      //
      if (stringCustomName.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("����:" + (intRow + 1) + "-[�q��m�W] ���i�ť�");
        return false;
      }
      // [���%]
      if (stringPercentage.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("����:" + (intRow + 1) + "-[���%] ���i�ť�");
        return false;
      }
      if (Float.parseFloat(stringPercentage.trim()) < 1) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("����:" + (intRow + 1) + "-[���%] ���i�p�� 1");
        return false;
      }
      floatPercentage = floatPercentage + Float.parseFloat(stringPercentage);
      //
      if (stringAddress.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("����:" + (intRow + 1) + "-[�a�}] ���i�ť�");
        return false;
      }
      //
      if (stringTel.length() == 0) {
        jtableTable1.setRowSelectionInterval(intRow, intRow);
        message("����:" + (intRow + 1) + "-[�q��] ���i�ť�");
        return false;
      }

    }
    if (floatPercentage != 100) {
      message("[���%] ������ 100");
      return false;
    }
    //
    JTable jtableTable2 = getTable("table2");
    if (jtableTable2.getRowCount() == 0) {
      message("[��O���] ���i�ť�");
      jtabbedpane1.setSelectedIndex(0);
      return false;
    }
    for (int intRow = 0; intRow < jtableTable2.getRowCount(); intRow++) {
      // [�ɼӧO]
      String stringPosition = jtableTable2.getValueAt(intRow, 3).toString();
      if (stringPosition.length() == 0) {
        message("����:" + (intRow + 1) + "-[�ɼӧO] ���i�ť�");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 2).toString().length() == 0) {
        message("����:" + (intRow + 1) + "-[�Ш�] ���i�ť�");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 4).toString().length() == 0) {
        message("����:" + (intRow + 1) + "-[�W��] ���i�ť�");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 5).toString().length() == 0) {
        message("����:" + (intRow + 1) + "-[�P��] ���i�ť�");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
      //
      if (jtableTable2.getValueAt(intRow, 6).toString().length() == 0) {
        message("����:" + (intRow + 1) + "-[���] ���i�ť�");
        jtableTable2.setRowSelectionInterval(intRow, intRow);
        jtabbedpane1.setSelectedIndex(0);
        return false;
      }
    }
    talk dbSale = getTalk("Sale");
    JTable jtableTable7 = getTable("table7");
    String stringOrderNoBonus = "";
    for (int intNo = 0; intNo < jtableTable7.getRowCount(); intNo++) {
      stringOrderNoBonus = ("" + getValueAt("table7", intNo, "OrderNoBonus")).trim();
      //
      stringSql = "SELECT  OrderNo  FROM  Sale05M092  WHERE  ISNULL(StatusCd,'')  = ''  AND  OrderNo  =  '" + stringOrderNoBonus + "' ";
      if (dbSale.queryFromPool(stringSql).length == 0) {
        jtabbedpane1.setSelectedIndex(5);
        jtableTable7.setRowSelectionInterval(intNo, intNo);
        messagebox("�P���M�Ϊ�� �� " + (intNo + 1) + " �C�� [�ϥνs��] ���s�b�C");
        return false;
      }
    }
    // ��P���� B3018 2012/09/17 S
    String[][] retSale05M246 = null;
    if ("".equals(stringProjectID1)) {
      messagebox("[�קO] ���i���ťաC");
      getcLabel("field1").requestFocus();
      return false;
    }
    if (stringDate.length() != 10) {
      messagebox("[���]����榡���~(YYYY/MM/DD)�C");
      getcLabel("field2").requestFocus();
      return false;
    }
    if (!"".equals(stringSaleWay)) {
      // �s�b�ˮ�
      stringSql = "SELECT  Num,  PlanDateS,  PlanDateE " + " FROM  Sale05M246  " + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " AND  StrategyNo  =  '" + stringSaleWay
          + "' " + " AND  PlanDateS  <=  '" + stringDate + "' " + " AND  PlanDateE  >=  '" + stringDate + "' ";
      retSale05M246 = dbSale.queryFromPool(stringSql);
      if (retSale05M246.length == 0) {
        messagebox("[��P����]��ƿ��~�C");
        getcLabel("SaleWay").requestFocus();
        return false;
      }
      // �ƶq�ˮ�
      double doubleNum = exeUtil.doParseDouble(retSale05M246[0][0].trim());
      String stringPlanDateS = retSale05M246[0][1].trim();
      String stringPlanDateE = retSale05M246[0][2].trim();
      if (doubleNum > 0) {
        stringSql = "SELECT  ProjectID1 " + " FROM  Sale05M090 " + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " AND  SaleWay  =  '" + stringSaleWay + "' "
            + " AND  OrderNo  <>  '" + stringOrderNo + "' " + " AND  OrderDate  >=  '" + stringPlanDateS + "' " + " AND  OrderDate  <=  '" + stringPlanDateE + "' ";
        retSale05M246 = dbSale.queryFromPool(stringSql);
        if (exeUtil.doParseDouble("" + (retSale05M246.length + 1)) > doubleNum) {
          String stringStrategyName = exeUtil.getNameUnion("StrategyName", "Sale05M244", " AND  StrategyNo  = '" + stringSaleWay + "' ", new Hashtable(), dbSale);
          messagebox("�ʫ��ҩ��浧�ƶW�L��P����(" + stringStrategyName + ")�ҳ]�w��" + convert.FourToFive("" + doubleNum, 0) + " �����ƶq�C");
          getcLabel("SaleWay").requestFocus();
          return false;
        }
      }
    }
    // ��P���� B3018 2012/09/17 E
    // �C��N�X�ˮ� ���H�q 2015/05/25 S
    String stringSSMediaID = getValue("SSMediaID").trim();
    String stringSSMediaID1 = getValue("SSMediaID1").trim();
    if ("".equals(stringSSMediaID)) {
      // messagebox("[�C��N�X] ���i���ťաC") ;
      // return false ;
    } else {
      String[][] retMediaSS = dbSale.queryFromPool(" SELECT  SSMediaName  FROM  Media_SS  WHERE  SSMediaID=  '" + stringSSMediaID + "'  AND  Stop  =  'N' ");
      if (retMediaSS.length == 0) {
        messagebox("[�C��N�X] ���s�b��Ʈw���C");
        return false;
      }
    }
    // 2015-12-10 B3018 ��X�H�ˮ�
    JTable jtable9 = getTable("table9");
    String stringSaleID1 = "";
    String stringZ6SaleID2 = "";
    String stringCSSaleID2 = "";
    String stringSaleName1 = "";
    String stringZ6SaleName2 = "";
    String stringCSSaleName2 = "";
    if (jtable9.getRowCount() <= 0) {
      jtabbedpane1.setSelectedIndex(1);
      messagebox("[��X�H���] ���i�L��ơC");
      return false;
    }
    for (int intNo = 0; intNo < jtable9.getRowCount(); intNo++) {
      stringSaleID1 = ("" + getValueAt("table9", intNo, "SaleID1")).trim();
      stringSaleName1 = ("" + getValueAt("table9", intNo, "SaleName1")).trim();
      stringZ6SaleID2 = ("" + getValueAt("table9", intNo, "Z6SaleID2")).trim();
      stringZ6SaleName2 = ("" + getValueAt("table9", intNo, "Z6SaleName2")).trim();
      stringCSSaleID2 = ("" + getValueAt("table9", intNo, "CSSaleID2")).trim();
      stringCSSaleName2 = ("" + getValueAt("table9", intNo, "CSSaleName2")).trim();
      //
      if ("".equals(stringSaleID1)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[��X�H���] �� " + (intNo + 1) + " �C�� [�P��(���)-���s] ���i���ťաC");
        return false;
      }
      if ("".equals(stringSaleName1)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[��X�H���] �� " + (intNo + 1) + " �C�� [�P��(���)-��X�H] ���i���ťաC");
        return false;
      }
      if ("".equals(stringCSSaleID2)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[��X�H���] �� " + (intNo + 1) + " �C�� [�����H��-���s] ���i���ťաC");
        return false;
      }
      if (!"".equals(stringCSSaleID2) && "".equals(stringCSSaleName2)) {
        jtabbedpane1.setSelectedIndex(1);
        jtable9.setRowSelectionInterval(intNo, intNo);
        messagebox("[��X�H���] �� " + (intNo + 1) + " �C�� [�����H��-��X�H] ���i���ťաC");
        return false;
      }
    }
    // 20191024 �����q�H
    JTable jtable6 = getTable("table6");
    String strBCustomNo = "";
    String strBenName = "";
    String strCountryName = "";
    for (int intNo = 0; intNo < jtable6.getRowCount(); intNo++) {
      strBCustomNo = ("" + getValueAt("table6", intNo, "BCustomNo")).trim();
      strBenName = ("" + getValueAt("table6", intNo, "BenName")).trim();
      strCountryName = ("" + getValueAt("table6", intNo, "CountryName")).trim();
      if ("".equals(strBCustomNo)) {
        jtabbedpane1.setSelectedIndex(2);
        jtable6.setRowSelectionInterval(intNo, intNo);
        messagebox("[�����q�H���] �� " + (intNo + 1) + " �C�� [���q�H�m�W] ���i���ťաC");
        return false;
      }
      if ("".equals(strBenName)) {
        jtabbedpane1.setSelectedIndex(2);
        jtable6.setRowSelectionInterval(intNo, intNo);
        messagebox("[�����q�H���] �� " + (intNo + 1) + " �C�� [�����Ҹ�] ���i���ťաC");
        return false;
      }
      if ("".equals(strCountryName)) {
        jtabbedpane1.setSelectedIndex(2);
        jtable6.setRowSelectionInterval(intNo, intNo);
        messagebox("[�����q�H���] �� " + (intNo + 1) + " �C�� [��O] ���i���ťաC");
        return false;
      }
    }
    // �C��N�X�ˮ� 2015/05/25 E
    // 2016-05-09 B3018
    JTable jtable3 = getTable("table3");
    String stringQty = "";
    String stringTotalAmt = "";
    String stringItemNo = "";
    for (int intNo = 0; intNo < jtable3.getRowCount(); intNo++) {
      stringQty = ("" + getValueAt("table3", intNo, "Qty")).trim();
      stringTotalAmt = ("" + getValueAt("table3", intNo, "TotalAmt")).trim();
      stringItemNo = ("" + getValueAt("table3", intNo, "ItemNo")).trim();
      //
      if (!stringItemNo.startsWith("Y"))
        continue;
      if (exeUtil.doParseDouble(stringTotalAmt) > 0 && exeUtil.doParseDouble(stringQty) == 0) {
        messagebox("[�ذe���] �� " + (intNo + 1) + " �C�� [�ƶq] ���i���ťաC");
        return false;
      }
    }
    JTable jtable12 = getTable("table12");
    String stringComLoadMoney = getValue("ComLoadMoney").trim();
    String stringComNo = "";
    String stringComLoadDate = "";
    String stringPrincipalAmt = "";
    String stringInterestAmt = "";
    String stringInterestKind = "";
    String stringSqlAnd = "";
    double doublePrincipalAmt = 0;
    double doubleComLoadMoney = 0;
    Vector vectorACom = null;
    for (int intNo = 0; intNo < jtable12.getRowCount(); intNo++) {
      stringComNo = ("" + getValueAt("table12", intNo, "Com_No")).trim();
      stringComLoadDate = ("" + getValueAt("table12", intNo, "ComLoadDate")).trim();
      stringPrincipalAmt = ("" + getValueAt("table12", intNo, "PrincipalAmt")).trim();
      stringInterestAmt = ("" + getValueAt("table12", intNo, "InterestAmt")).trim();
      stringInterestKind = ("" + getValueAt("table12", intNo, "InterestKind")).trim();
      // �~�D�O
      if ("".equals(stringComNo)) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("���q�U���� " + (intNo + 1) + " �椧 [�~�D�O] ���i���ťաC");
        return false;
      }
      stringSqlAnd = " AND  ISNULL(COMPANY_CD,'')  <>  ''  " + " AND  Com_No  =  '" + stringComNo + "' " + " AND Com_No IN (SELECT  distinct H_COM " + " FROM  Sale05M040 "
          + " WHERE  ProjectID1  =  '" + stringProjectID1 + "' " + " UNION " + " SELECT  distinct  L_COM " + " FROM  Sale05M040 " + " WHERE  ProjectID1  =  '" + stringProjectID1
          + "' " + " ) ";
      vectorACom = exeUtil.getQueryDataHashtable("A_Com", new Hashtable(), stringSqlAnd, dbSale);
      if (vectorACom.size() == 0) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("���q�U���� " + (intNo + 1) + " �椧 [�~�D�O] ���s�b��Ʈw���C");
        return false;
      }
      // ���q�U���O
      if ("".equals(stringComLoadDate)) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("���q�U���� " + (intNo + 1) + " �椧 [���q�U���O] ���i���ťաC");
        return false;
      }
      stringComLoadDate = exeUtil.getDateAC(stringComLoadDate, "���q�U���O");
      if (stringComLoadDate.length() != 10) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("���q�U���� " + (intNo + 1) + " �椧 [���q�U���O] ����榡(YYYY/mm/dd)���~�C");
        return false;
      }
      // �U���������B
      if (exeUtil.doParseDouble(stringPrincipalAmt) <= 0) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("���q�U���� " + (intNo + 1) + " �椧 [�U���������B] ���i�� 0�C");
        return false;
      }
      doublePrincipalAmt += exeUtil.doParseDouble(stringPrincipalAmt);
      // �Q����I�覡
      if ("".equals(stringInterestKind)) {
        jtable12.setRowSelectionInterval(intNo, intNo);
        jtabbedpane1.setSelectedIndex(3);
        messagebox("���q�U���� " + (intNo + 1) + " �椧 [�Q����I�覡] ���i���ťաC");
        return false;
      }
    }
    // �ˮ֤��q�U�`�B �n����U���������B �[�`
    doublePrincipalAmt = exeUtil.doParseDouble(convert.FourToFive("" + doublePrincipalAmt, 4));
    doubleComLoadMoney = exeUtil.doParseDouble(convert.FourToFive(stringComLoadMoney, 4));
    if (doublePrincipalAmt != doubleComLoadMoney) {
      jtabbedpane1.setSelectedIndex(3);
      getcLabel("ComLoadMoney").requestFocus();
      messagebox("[���q�U�`�B] ������ ���q�U��椧�U���������B �[�`�C");
      return false;
    }
    message("");
    //
    if (getValue("CompanyNo").length() != 0 && getValue("field1").length() != 0 && getValue("field2").length() != 0) {
      // talk dbSale = getTalk(""+get("put_dbSale"));
      String stringSQL = "";
      // ���ʫ��ҩ���s��
      getButton("ButtonOrderNo").doClick();
      //
      stringSQL = " INSERT  " + " INTO Sale05M090_Flow " + " ( " + " OrderNo," + " FlowStatus," + " EmployeeNo," + " EDateTime " + " ) " + " VALUES " + " ( " + "'"
          + getValue("field3").trim() + "'," + "N'�g��'," + "N'" + getUser() + "'," + "N'" + datetime.getTime("YYYY/mm/dd h:m:s") + "'" + " ) ";
      dbSale.execFromPool(stringSQL);
      //
      stringSQL = " INSERT  " + " INTO Sale05M090_Flow_HIS " + " ( " + " OrderNo," + " FlowStatus," + " EmployeeNo," + " EDateTime " + " ) " + " VALUES " + " ( " + "'"
          + getValue("field3").trim() + "'," + "N'�g��'," + "N'" + getUser() + "'," + "N'" + datetime.getTime("YYYY/mm/dd h:m:s") + "'" + " ) ";
      dbSale.execFromPool(stringSQL);
      setValue("OrderNo", getValue("field3").trim());
    }
    
    // �~������W���ˮ�
    System.out.println("========�s�W_����W���ˮ�=======S");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    talk db400 = getTalk("400CRM");
    talk dbJGENLIB = getTalk("JGENLIB");
    String stringSQL = "";
    String strBDaysql = "";
    String strPW0Dsql = "";
    String str400sql = "";
    String retQueryLog[][] = null;
    String retCList[][] = null;
    String errMsg = "";
    // �e����
    String strProjectID1 = getValue("field1").trim();
    String strOrderDate = getValue("field2").trim();
    String strOrderNo = getValue("field3").trim();
    System.out.println("strProjectID1=====>" + strProjectID1);
    System.out.println("strOrderDate=====>" + strOrderDate);
    System.out.println("strOrderNo=====>" + strOrderNo);
    // LOG NOW DATE
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    String strNowDate = nowsdf.format(now);
    String tempROCYear = "" + (Integer.parseInt(strNowDate.substring(0, strNowDate.length() - 4)) - 1911);
    String RocNowDate = tempROCYear + strNowDate.substring(strNowDate.length() - 4, strNowDate.length());
    String strNowTime = new SimpleDateFormat("HHmmss").format(getDate());
    System.out.println("RocNowDate====>" + RocNowDate);
    System.out.println("strNowTime====>" + strNowTime);
    String strNowTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate());
    // ���u�X
    String userNo = getUser().toUpperCase().trim();
    String empNo = "";
    String[][] retEip = null;
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }
    System.out.println("empNo====>" + empNo);
    // �O���y���b
    int intRecordNo = 1;
    String[][] ret05M070;
    stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + strOrderNo + "'";
    ret05M070 = dbSale.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo====>" + intRecordNo);
    // actionNo
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    String actionNo = strNowDate + strNowTime + ram;
    System.out.println("actionNo=====>" + actionNo);
    
    // �Ȥ���
    String[][] ret91Table = getTableData("table1");
    if (ret91Table.length > 0) {
      for (int a = 0; a < ret91Table.length; a++) {
        String strCustID = ret91Table[a][5].trim();
        String strCustName = ret91Table[a][6].trim();
        String strBirthday = ret91Table[a][8].trim();
        System.out.println("strCustID=====>" + strCustID);
        System.out.println("strCustName=====>" + strCustName);
        System.out.println("strBirthday=====>" + strBirthday);

        // ����W��
        // AS400
        if ("".equals(strBirthday)) {
          strBDaysql = "AND CUSTOMERNAME='" + strCustName + "'";
        } else {

          if (strBirthday.indexOf("/") == -1) {
            String yyyy = strBirthday.substring(0, 4);
            String MM = strBirthday.substring(4, 6);
            String dd = strBirthday.substring(6, 8);
            strBirthday = yyyy + "-" + MM + "-" + dd;
          } else {
            strBirthday = strBirthday.replace("/", "-");
          }
          System.out.println("strBirthday=====>" + strBirthday);
          strBDaysql = "AND ( CUSTOMERNAME='" + strCustName + "' AND BIRTHDAY = '" + strBirthday.replace("/", "-") + "' )";
        }
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustID + "' " + strBDaysql;
        retCList = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          // 400 LOG
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustID + "', '" + strCustName + "', '773', '018', '�ӫȤᬰ���ަW���H������W��A�T�����ýШ̨���~�����q���@�~�|��k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          // SALE LOG
          stringSQL = "INSERT INTO Sale05M070 (OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, OrderDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "
              + " VALUES ('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + actionNo + "','�ʫ��ҩ���','�Ȥ���','�s�W','�Ȥ�" + strCustName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + strCustID + "','" + strCustName + "','" + strOrderDate + "','RY','773','018','�Ȥ�" + strCustName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale.execFromPool(stringSQL);
          intRecordNo++;
          if ("".equals(errMsg)) {
            errMsg = "�Ȥ�" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          } else {
            errMsg = errMsg + "\n�Ȥ�" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, OrderDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "
              + " VALUES ('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + actionNo + "','�ʫ��ҩ���','�Ȥ���','�s�W','���ŦX','" + strCustID + "','" + strCustName
              + "','" + strOrderDate + "','RY','773','018','�Ȥ�" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale.execFromPool(stringSQL);
          intRecordNo++;
        }
        
      }
    }
    // �����q�H
    System.out.println("�����q�H=====>");
    String[][] ret91BenTable = getTableData("table6");
    if (ret91BenTable.length > 0) {
      for (int b = 0; b < ret91BenTable.length; b++) {
        String strCustID = ret91BenTable[b][4].trim();
        String strCustName = ret91BenTable[b][3].trim();
        String strBirthday = ret91BenTable[b][5].trim();
        System.out.println("strCustID=====>" + strCustID);
        System.out.println("strCustName=====>" + strCustName);
        System.out.println("strBirthday=====>" + strBirthday);

        // ����W��
        // AS400
        if ("".equals(strBirthday)) {
          strBDaysql = "AND CUSTOMERNAME='" + strCustName + "'";
        } else {
          if (strBirthday.indexOf("/") == -1) {
            String yyyy = strBirthday.substring(0, 4);
            String MM = strBirthday.substring(4, 6);
            String dd = strBirthday.substring(6, 8);
            strBirthday = yyyy + "-" + MM + "-" + dd;
          } else {
            strBirthday = strBirthday.replace("/", "-");
          }
          System.out.println("strBirthday=====>" + strBirthday);
          strBDaysql = "AND ( CUSTOMERNAME='" + strCustName + "' AND BIRTHDAY = '" + strBirthday + "' )";
        }
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustID + "' " + strBDaysql;
        retCList = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          // 400 LOG
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustID + "', '" + strCustName + "', '773', '018', '�ӫȤᬰ���ަW���H������W��A�T�����ýШ̨���~�����q���@�~�|��k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          // SALE LOG
          stringSQL = "INSERT INTO Sale05M070 (OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, OrderDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "
              + " VALUES ('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + actionNo + "','�ʫ��ҩ���','�����q�H���','�s�W','�����q�H" + strCustName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + strCustID + "','" + strCustName + "','" + strOrderDate + "','RY','773','018','�����q�H" + strCustName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale.execFromPool(stringSQL);
          intRecordNo++;
          if ("".equals(errMsg)) {
            errMsg = "�����q�H" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          } else {
            errMsg = errMsg + "\n�����q�H" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, OrderDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "
              + " VALUES ('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + actionNo + "','�ʫ��ҩ���','�����q�H���','�s�W','���ŦX','" + strCustID + "','" + strCustName
              + "','" + strOrderDate + "','RY','773','018','�����q�H" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbSale.execFromPool(stringSQL);
          intRecordNo++;
        }
      }
    }
    // �N�z�H
    System.out.println("�N�z�H=====>");
    String[][] ret91AgentTable = getTableData("table10");
    if (ret91AgentTable.length > 0) {
      for (int c = 0; c < ret91AgentTable.length; c++) {
        String strCustID = ret91AgentTable[c][4].trim();
        String strCustName = ret91AgentTable[c][3].trim();
        System.out.println("strCustID=====>" + strCustID);
        System.out.println("strCustName=====>" + strCustName);

        // ����W��
        // Query_Log ���ͤ�
        strPW0Dsql = "SELECT BIRTHDAY FROM QUERY_LOG WHERE PROJECT_ID = '" + strProjectID1 + "' AND QUERY_ID = '" + strCustID + "' AND NAME = '" + strCustName + "'";
        retQueryLog = dbPW0D.queryFromPool(strPW0Dsql);
        if (retQueryLog.length > 0) {
          strBDaysql = "AND ( CUSTOMERNAME='" + strCustName + "' AND BIRTHDAY = '" + retQueryLog[0][0].trim().replace("/", "-") + "' )";
        } else {
          strBDaysql = "AND CUSTOMERNAME='" + strCustName + "'";
        }
        System.out.println("strBDaysql====>" + strBDaysql);
        // AS400
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustID + "' " + strBDaysql;
        retCList = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          // 400 LOG
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustID + "', '" + strCustName + "', '773', '018', '�ӫȤᬰ���ަW���H������W��A�T�����ýШ̨���~�����q���@�~�|��k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          // SALE LOG
          stringSQL = "INSERT INTO Sale05M070 (OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, OrderDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "
              + " VALUES ('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + actionNo + "','�ʫ��ҩ���','�N�z�H���','�s�W','�N�z�H" + strCustName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + strCustID + "','" + strCustName + "','" + strOrderDate + "','RY','773','018','�N�z�H" + strCustName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale.execFromPool(stringSQL);
          intRecordNo++;
          if ("".equals(errMsg)) {
            errMsg = "�N�z�H" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          } else {
            errMsg = errMsg + "\n�N�z�H" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, OrderDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "
              + " VALUES ('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + actionNo + "','�ʫ��ҩ���','�N�z�H���','�s�W','���ŦX','" + strCustID + "','" + strCustName
              + "','" + strOrderDate + "','RY','773','018','�N�z�H" + strCustName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale.execFromPool(stringSQL);
          intRecordNo++;
        }

      }
    }
    System.out.println("========�s�W_����W���ˮ�=======E");

    // SHOW MSG
    if (!"".equals(errMsg)) {
      System.out.println(">>>msg");
      setValue("errMsgBoxText", errMsg);
      getButton("errMsgBoxBtn").doClick();
      getButton("sendMail").doClick();
      return false;
    }

    // �w�f
    JButton buyedInfo = getButton("BuyedInfo");
    buyedInfo.setText("checkAndEmail");
    buyedInfo.doClick();
    put("TrustAccountNo", value);
    setValue("actionText", "�s�W");
    getButton("ButtonTrustAccountNo").doClick();

    // 20200619 Kyle : ��s���q�H��T start
    // �ˬd���q�H
    if (mlpUtils.checkHasBen(getTableData("table1"), getTableData("table6")) == false)
      return false;

    // �ˬd���q�H���
    if (mlpUtils.checkBenColumn(getTableData("table6"), amlCons) == false)
      return false;

    // ��s�����q�H��
    getButton("updateBen").doClick();
    System.out.println("updateBen=====> Done");
    // ��s���q�H��T end

    return true;
  }

  public String getInformation() {
    return "---------------\u65b0\u589e\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
