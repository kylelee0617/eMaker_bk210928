package SaleEffect;

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
import Farglory.util.FargloryUtil;
import Farglory.Excel.FargloryExcel;

public class Sale03R510_202101 extends bproc {
  String dateType = ""; 
  
  public String getDefaultValue(String value) throws Throwable {
    System.out.println("ButtonExcelView20180503 ========= START =========");
    FargloryUtil exeUtil = new FargloryUtil();
    talk dbSale = getTalk("" + get("put_dbSale"));
    talk dbDoc = getTalk("" + get("put_dbDoc"));
    talk dbDocCS = null;// exeUtil.getTalkCS("Doc") ; // 2018019 //
    talk dbAO = getTalk("" + get("put_dbAO"));
    
    //����������
    dateType = getValue("dateType").trim();
    
    //
    if (!isBatchCheckOK())
      return value;
    //
    /*
     * if(dbDocCS == null) { messagebox("�L�k�s�u�� �H�� �t�ΡC") ; }
     */
    dbExcel(exeUtil, dbAO, dbDoc, dbDocCS, dbSale);
    System.out.println("ButtonExcelView20180503 ========= END =========");
    return value;
  }

  public void dbExcel(FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbDocCS, talk dbSale) throws Throwable {
    int[] arrayRow = { 9, 10, 11, 12, 13, 14, 16, 17, 18, 20, 21 };
    String stringFilePath = "g:\\��T��\\Excel\\SaleEffect\\Sale03R510_20180503.xlt";
    FargloryExcel exeExcel = new FargloryExcel();
    Hashtable hashtableDataPosition = null;
    Vector retVector = exeExcel.getExcelObject(stringFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheets = (Dispatch) retVector.get(3);
    hashtableDataPosition = doExcel1(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbDocCS, dbSale);

    // �~�Z
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(2)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel2(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbSale);
    
    // �s�ӤH
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(3)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel3(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbSale);
    
    // �`�ӤH
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(4)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel4(arrayRow, objectSheet1, exeExcel, exeUtil, dbAO, dbDoc, dbSale);
    
    // 3-6 DATA-�����O�� (��)8
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(8)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel8(hashtableDataPosition, objectSheet1, exeExcel, exeUtil);
    // 3-6 DATA-�����O�� (��)9
    objectSheet1 = Dispatch.call(objectSheets, "Item", new Variant(9)).toDispatch();
    Dispatch.call(objectSheet1, "Activate");
    doExcel9(hashtableDataPosition, objectSheet1, exeExcel, exeUtil);
    
    // ���� Excel ����
    exeExcel.getReleaseExcelObject(retVector);
  }

  public Hashtable doExcel1(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbDocCS, talk dbSale)
      throws Throwable {
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
//    String dateType = getValue("dateType").trim();
    // String stringFilePath =
    // "g:\\��T��\\Excel\\SaleEffect\\Sale03R510_2017-02-18.xlt" ;
    long longTime1 = exeUtil.getTimeInMillis();
    long longTime2 = 0;
    String[] arrayValuePOS = null;
    String[] arraySSMediaIDPos = { "4", "6", "8", "10", "12", "14", "18", "20", "22", "26", "28" };
    String[] arraySSMediaID = { "H", "F", "A", "G", "J", "B", "I", "D", "L", "K", "C" };
    String[] arrayValue = { "3", "5", "7", "9", "11", "13", "17", "19", "21", "25", "27" };
    String[][] retTitle2 = { { "���ĳq��", "�w�ʫȤ�", "���ʦW��", "���~�X�@", "�P�~�q��", "���~��" }, { "�C��q��", "�����q��", "²�T" }, { "POP", "���ݳq��" } };
    Vector vectorTitles = getTitles(retTitle2);
    // �Ĥ@���
    String[][] retSale03R510A = new String[0][0];
    String[][] retSale03R510A2 = new String[0][0];
    String[][] retSale03R510ADealDay = new String[0][0];
    System.out.println("stringYearAC>>>" + stringYearAC);
    System.out.println("stringMonth>>>" + stringMonth);
    System.out.println("dateType>>>" + dateType);
    retSale03R510A = dbSale.queryFromPool("speMakerSale03R510A2  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth  + "',  '" + dateType + "' " ); // �~�Z
    retSale03R510A2 = dbAO.queryFromPool("AO_SPSale03R510A  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // �s�ӤH�� 11�B�`�ӤH�� 13
    retSale03R510ADealDay = dbAO.queryFromPool("AO_SPSale03R510DealDay  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // ����Ѽ�19
    // �ĤG���
    String[][] retSale03R510B = dbSale.queryFromPool("speMakerSale03R510B2  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth  + "',  '" + dateType + "' " ); // �~�Z
    String stringRowLine = "";
    Vector vectorCostID = new Vector();
    Hashtable hashtableDataPositionTable1 = new Hashtable();
    Hashtable hashtableDataPositionTable2 = new Hashtable();
    Hashtable hashtableSale03R510B = getSale03R510BH(stringProjectID1, stringYearAC, stringMonth, retSale03R510B, exeUtil, dbSale); // �~�Z
    Hashtable hashtableSale03R510B2 = getSale03R510B2H(stringProjectID1, stringYearAC, stringMonth, exeUtil, dbAO); // �s�ӤH
    Hashtable hashtableSale03R510C = getSale03R510CH(stringProjectID1, stringYearAC, stringMonth, exeUtil, dbAO); // �q���`�ӤH
    Hashtable hashtableData = null;
    //
    hashtableDataPositionTable1 = getMoneyFrontTable1(arraySSMediaIDPos, arraySSMediaID, retSale03R510B, exeUtil, dbDoc, dbDocCS, vectorCostID);
    hashtableDataPositionTable2 = getMoneyFrontTable2(arrayRow, vectorTitles, arraySSMediaIDPos, arraySSMediaID, retSale03R510B, exeUtil, dbDoc, dbDocCS, vectorCostID);
    //
    // 20180504 �ϥζǤJ�Ѽ�
    // FargloryExcel exeExcel = new FargloryExcel( ) ;
    //
    // exeExcel.setVisibleProperty(false) ; // �����㤣��� Excel
    //
    // Vector retVector = exeExcel.getExcelObject(stringFilePath) ;
    // 20180504 �ϥζǤJ�Ѽ�
    // Dispatch objectSheet1 = (Dispatch)retVector.get(1) ;
    Dispatch objectClick = null;
    int intRowOrder = 99;
    int intRowL = 0;
    int intCol = 0;
    int intCol2 = 0;
    int intColOrder11 = 4;
    int intColOrder12 = 8;
    int intColOrder21 = 10;
    int intColOrder22 = 14;
    int intColOrder31 = 16;
    int intColOrder32 = 20;
    int intColOrder41 = 22;
    int intColOrder42 = 26;
    int intColOrder51 = 28;
    int intColOrder52 = 32;
    int intPos = 0;
    int intPos2 = 0;
    String stringValue = "";
    String stringColName = "";
    String stringTemp = "";
    String stringTitle = "";
    String stringTotMoney = exeUtil.getNameUnion("TotMoney", "A_Project", " AND  ProjectID  =  '" + stringProjectID1 + "' ", new Hashtable(), dbSale);
    String[][] retDataPosition = null;
    // ���D
    stringValue = stringProjectID1 + "�צ�P���������";
    exeExcel.putDataIntoExcel(0, 0, stringValue, objectSheet1);
    stringValue = ("O".equals(dateType)? "�I�q":"ñ��") + "�~�סG" + stringYearAC + "/" + stringMonth + "��";
    exeExcel.putDataIntoExcel(0, 1, stringValue, objectSheet1);
    for (int intRow = 0; intRow <= 57; intRow++) {
      // �Ĥ@���
      if (intRow == 17) {
        retDataPosition = (String[][]) hashtableDataPositionTable1.get("Table1DATA");
        if (retDataPosition != null) {
          for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
            intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
            intRowL = exeUtil.doParseInteger(retDataPosition[intNo][1]);
            stringValue = retDataPosition[intNo][2];
            //
            if (exeUtil.doParseDouble(stringValue) == 0)
              stringValue = "0";
            //
            exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRowL), stringValue, objectSheet1);
          }
        }
      }
      
      // �~�Z
      for (int intNo = 0; intNo < retSale03R510A.length; intNo++) {
        intRowL = exeUtil.doParseInteger(retSale03R510A[intNo][0].trim()) - 1;
        //
        if (intRowL <= 0) continue;
        if (intRowL != intRow) continue;
        // 1-12 ��
        for (int intNoL = 1; intNoL <= 12; intNoL++) {
          stringValue = retSale03R510A[intNo][intNoL + 1].trim();
          intCol = 4 + (intNoL - 1) * 2;
          exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
        }
        // 3 �h�~12��
        stringValue = retSale03R510A[intNo][14].trim();
        exeExcel.putDataIntoExcel(3, intRowL, stringValue, objectSheet1);
        // 28�~�֭p
        stringValue = retSale03R510A[intNo][15].trim();
        exeExcel.putDataIntoExcel(28, intRowL, stringValue, objectSheet1);
        // 31 �ײ֭p 2016-08-09 �אּ�`�P���B
        stringValue = retSale03R510A[intNo][16].trim();
        if (intRowL == 3) stringValue = stringTotMoney;
        exeExcel.putDataIntoExcel(31, intRowL, stringValue, objectSheet1);
      }
      
      // �s�ӤH�� 11�B�`�ӤH�� 13
      for (int intNo = 0; intNo < retSale03R510A2.length; intNo++) {
        intRowL = exeUtil.doParseInteger(retSale03R510A2[intNo][0].trim()) - 1;
        //
        if (intRowL <= 0)
          continue;
        if (intRowL != intRow)
          continue;
        // 1-12 ��
        for (int intNoL = 1; intNoL <= 12; intNoL++) {
          stringValue = retSale03R510A2[intNo][intNoL + 1].trim();
          intCol = 4 + (intNoL - 1) * 2;
          exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
        }
        // 14 �h�~
        stringValue = retSale03R510A2[intNo][14].trim();
        exeExcel.putDataIntoExcel(3, intRowL, stringValue, objectSheet1);
        // 15
        stringValue = retSale03R510A2[intNo][15].trim();
        exeExcel.putDataIntoExcel(28, intRowL, stringValue, objectSheet1);
        // 16 �ײ֭p
        stringValue = retSale03R510A2[intNo][16].trim();
        exeExcel.putDataIntoExcel(31, intRowL, stringValue, objectSheet1);
      }
      // ����Ѽ�19 -> 24
      for (int intNo = 0; intNo < retSale03R510ADealDay.length; intNo++) {
        intRowL = exeUtil.doParseInteger(retSale03R510ADealDay[intNo][0].trim()) - 1;
        //
        if (intRowL <= 0)
          continue;
        if (intRowL != intRow)
          continue;
        // 1-12 ��
        for (int intNoL = 1; intNoL <= 12; intNoL++) {
          stringValue = retSale03R510ADealDay[intNo][intNoL + 1].trim();
          intCol = 4 + (intNoL - 1) * 2;
          exeExcel.putDataIntoExcel(intCol, intRowL + 5, stringValue, objectSheet1);
        }
        // 14 �h�~
        stringValue = retSale03R510ADealDay[intNo][14].trim();
        exeExcel.putDataIntoExcel(3, intRowL + 5, stringValue, objectSheet1);
        // 15 �~�֭p
        stringValue = retSale03R510ADealDay[intNo][15].trim();
        exeExcel.putDataIntoExcel(28, intRowL + 5, stringValue, objectSheet1);
        // 16 �ײ֭p
        stringValue = retSale03R510ADealDay[intNo][16].trim();
        exeExcel.putDataIntoExcel(31, intRowL + 5, stringValue, objectSheet1);
      }

      // �ĤG���
      if (intRow == 43) {
        retDataPosition = (String[][]) hashtableDataPositionTable2.get("Table2DATA");
        if (retDataPosition != null) {
          for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
            intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
            intRowL = exeUtil.doParseInteger(retDataPosition[intNo][1]);
            stringValue = retDataPosition[intNo][2];
            //
            if (exeUtil.doParseDouble(stringValue) == 0)
              stringValue = "0";
            //
            System.out.println("�ĤG��� �O�� Col(" + intCol + ")Row(" + intRowL + ")��(" + stringValue + ")--------------------------------------------");
            exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
            // exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRowL), stringValue,
            // objectSheet1) ;
          }
        }
      }

      // �ĤG���-�~�Z
      stringRowLine = convert.add0("" + intRow, "3");
      System.out.println(stringRowLine + "--------------------------------------------");
      hashtableData = (Hashtable) hashtableSale03R510B.get(stringRowLine);
      if (hashtableData != null) {
        for (int intNo = 0; intNo < vectorTitles.size(); intNo++) {
          stringTitle = "" + vectorTitles.get(intNo);
          stringValue = "" + hashtableData.get(stringTitle);
          if ("".equals(stringValue))
            stringValue = "0";
          if ("null".equals(stringValue))
            stringValue = "0";
          //
          intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNo].trim());
          //
          exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRow), stringValue, objectSheet1);
        }
        // ��������
        stringValue = "" + hashtableData.get("��������");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        // System.out.println(stringRowLine+"�~�Z("+getChangeExcelRow(intRow)+")("+stringValue+")-------------------------")
        // ;
        exeExcel.putDataIntoExcel(3, getChangeExcelRow(intRow), stringValue, objectSheet1);
        // PASS
        stringValue = "" + hashtableData.get("PASS");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        // System.out.println(stringRowLine+"�~�Z("+getChangeExcelRow(intRow)+")("+stringValue+")-------------------------")
        // ;
        exeExcel.putDataIntoExcel(2, getChangeExcelRow(intRow), stringValue, objectSheet1);
      }

      // // �ĤG���-�q���`�ӤH-��������
      hashtableData = (Hashtable) hashtableSale03R510C.get(stringRowLine);
      if (hashtableData != null) {
        for (int intNo = 0; intNo < vectorTitles.size(); intNo++) {
          stringTitle = "" + vectorTitles.get(intNo);
          stringValue = "" + hashtableData.get(stringTitle);
          if ("".equals(stringValue))
            stringValue = "0";
          if ("null".equals(stringValue))
            stringValue = "0";
          //
          intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNo].trim());
          //
          exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRow), stringValue, objectSheet1);
        }
        // ��������
        stringValue = "" + hashtableData.get("��������");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(3, getChangeExcelRow(intRow), stringValue, objectSheet1);
        // PASS
        stringValue = "" + hashtableData.get("PASS");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(2, getChangeExcelRow(intRow), stringValue, objectSheet1);
      }

      // �ĤG���-�s�ӤH ��b�̫�
      hashtableData = (Hashtable) hashtableSale03R510B2.get(stringRowLine);
      if (hashtableData != null) {
        for (int intNo = 0; intNo < vectorTitles.size(); intNo++) {
          stringTitle = "" + vectorTitles.get(intNo);
          stringValue = "" + hashtableData.get(stringTitle);
          if ("".equals(stringValue))
            stringValue = "0";
          if ("null".equals(stringValue))
            stringValue = "0";
          //
          intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNo].trim());
          //
          exeExcel.putDataIntoExcel(intCol, getChangeExcelRow(intRow), stringValue, objectSheet1);
        }
        // ��������
        stringValue = "" + hashtableData.get("��������");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(3, getChangeExcelRow(intRow), stringValue, objectSheet1);
        // PASS
        stringValue = "" + hashtableData.get("PASS");
        if ("".equals(stringValue) || "null".equals(stringValue))
          stringValue = "0";
        exeExcel.putDataIntoExcel(2, getChangeExcelRow(intRow), stringValue, objectSheet1);
      }

      //
      if (intRow == 57) {
        SetOrderList("�~�Z", "MAX", 32, 33, intColOrder11, intColOrder12, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel);
        SetOrderList("�s�ӤH", "MAX", 47, 48, intColOrder21, intColOrder22, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel);
        SetOrderList("�`�ӤH", "MAX", 61, 62, intColOrder31, intColOrder32, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel);
        // �ӤH�O��-�W��-��
        SetOrderList("�ӤH�O��", "MIN", 71, 72, intColOrder41, intColOrder42, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // ��
        SetOrderList("�ӤH�O��", "MIN", 81, 82, intColOrder41, intColOrder42, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // ��
        // ����O�βv-����-��
        SetOrderList("����O�βv", "MIN", 95, 96, intColOrder51, intColOrder52, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // ��
        SetOrderList("����O�βv", "MIN", 89, 90, intColOrder51, intColOrder52, intRowOrder, arraySSMediaID, arraySSMediaIDPos, retTitle2, objectSheet1, exeUtil, exeExcel); // ��
      }
    }
    retDataPosition = (String[][]) hashtableDataPositionTable2.get("OTHER");
    if (retDataPosition != null) {
      for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
        intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
        intRowL = exeUtil.doParseInteger(retDataPosition[intNo][1]);
        stringValue = retDataPosition[intNo][2];
        //
        // System.out.println(intNo+"intCol("+intCol+")intRowL("+intRowL+")stringValue("+stringValue+")------------------------------------S")
        // ;
        if ("null".equals(stringValue))
          continue;
        //
        // System.out.println("OTHER
        // Col("+intCol+")Row("+intRowL+")��("+intRowL+")------------------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRowL, stringValue, objectSheet1);
      }
    }
    // exeExcel.setPreView(false, "C:\\oce.xls") ; // ���w���ɡA�B���ǤJ���|�ɡA�t�s�s�ɡC
    // exeExcel.setVisiblePropertyOnFlow(false, retVector) ; // �����㤣��� Excel
    //
    // ���� Excel ����
    // exeExcel.getReleaseExcelObject(retVector) ;

    return hashtableDataPositionTable2;
  }

  public Vector getTitles(String[][] retTitle2) throws Throwable {
    Vector vectorTitles = new Vector();
    //
    for (int intNo = 0; intNo < retTitle2.length; intNo++) {
      for (int intNoL = 0; intNoL < retTitle2[intNo].length; intNoL++) {
        vectorTitles.add(retTitle2[intNo][intNoL].trim());
      }
    }
    //
    return vectorTitles;
  }

  // �~�Z
  public Hashtable getSale03R510BH(String stringProjectID1, String stringYearAC, String stringMonth, String[][] retSale03R510B, FargloryUtil exeUtil, talk dbSale)
      throws Throwable {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
    String[] arrayRowDefine = { "�C��", "�L", "���ʦW��", "���ĳq��", "�w�ʫȤ�", "���~�X�@", "�P�~�q��", "���~��", "�C��q��", "�����q��", "²�T", "POP", "���ݳq��", "��������", "PASS" };
    String stringRowLine = "";
    String stringTemp = "";
    Hashtable hashtableSale03R510B = new Hashtable();
    Hashtable hashtableSale03R510BData = new Hashtable();
    int intRow = 0;
    for (int intNo = 0; intNo < retSale03R510B.length; intNo++) {
      stringRowLine = retSale03R510B[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1;
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510BData = (Hashtable) hashtableSale03R510B.get(stringRowLine);
      if (hashtableSale03R510BData == null) {
        hashtableSale03R510BData = new Hashtable();
        hashtableSale03R510B.put(stringRowLine, hashtableSale03R510BData);
      }
      for (int intNoL = 0; intNoL < arrayRowDefine.length; intNoL++) {
        stringTemp = retSale03R510B[intNo][intNoL].trim();
        if (intNoL < 13 && intRow == 28) {
          stringTemp = "";
        }
        // System.out.println(stringRowLine+"("+arrayRowDefine[intNoL]+")("+stringTemp+")-------------------------")
        // ;
        hashtableSale03R510BData.put(arrayRowDefine[intNoL], stringTemp);
      }
    }
    // 2 3 4
    // "�C��", "�L", "���ʦW��", "���ĳq��", "�w�ʫȤ�", "���~�X�@", "�P�~�q��", "���~��", "�C��q��", "�����q��",
    // "²�T", "POP", "���ݳq��", "��������", "PASS"
    // 3 4 2
    // "���ĳq��", "�w�ʫȤ�", "���ʦW��"
    String stringTemp2 = "";
    String stringTemp3 = "";
    String stringTemp4 = "";
    for (int intNo = 0; intNo < retSale03R510B.length; intNo++) {
      stringTemp2 = retSale03R510B[intNo][2].trim();
      stringTemp3 = retSale03R510B[intNo][3].trim();
      stringTemp4 = retSale03R510B[intNo][4].trim();
      //
      retSale03R510B[intNo][2] = stringTemp3;
      retSale03R510B[intNo][3] = stringTemp4;
      retSale03R510B[intNo][4] = stringTemp2;
    }
    return hashtableSale03R510B;
  }

  public Hashtable getSale03R510B2H(String stringProjectID1, String stringYearAC, String stringMonth, FargloryUtil exeUtil, talk dbAO) throws Throwable {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12
    String[] arrayRowDefine = { "�C��", "�L", "���ʦW��", "���ĳq��", "�w�ʫȤ�", "���~�X�@", "�P�~�q��", "���~��", "�C��q��", "�����q��", "²�T", "POP", "���ݳq��" };
    String[][] retSale03R510BE = dbAO.queryFromPool("AO_SPSale03R510B_E  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // �s�ӤH���������͡BPASS
    String[][] retSale03R510B2 = dbAO.queryFromPool("AO_SPSale03R510B  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // �s�ӤH
    String stringRowLine = "";
    String stringTemp = "";
    Hashtable hashtableSale03R510B2 = new Hashtable();
    Hashtable hashtableSale03R510B2Data = new Hashtable();
    int intRow = 0;
    // �ĤG���-�s�ӤH-�������͡BPASS
    for (int intNo = 0; intNo < retSale03R510BE.length; intNo++) {
      stringRowLine = retSale03R510BE[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1;
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510B2Data = (Hashtable) hashtableSale03R510B2.get(stringRowLine);
      if (hashtableSale03R510B2Data == null) {
        hashtableSale03R510B2Data = new Hashtable();
        hashtableSale03R510B2.put(stringRowLine, hashtableSale03R510B2Data);
      }
      // ��������
      stringTemp = retSale03R510BE[intNo][2].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510B2Data.put("��������", stringTemp);
      }
      // PASS
      stringTemp = retSale03R510BE[intNo][3].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510B2Data.put("PASS", stringTemp);
      }
    }
    for (int intNo = 0; intNo < retSale03R510B2.length; intNo++) {
      stringRowLine = retSale03R510B2[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1; // �C�ƴ�1
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510B2Data = (Hashtable) hashtableSale03R510B2.get(stringRowLine);
      if (hashtableSale03R510B2Data == null) {
        hashtableSale03R510B2Data = new Hashtable();
        hashtableSale03R510B2.put(stringRowLine, hashtableSale03R510B2Data);
      }
      for (int intNoL = 0; intNoL < arrayRowDefine.length; intNoL++) {
        stringTemp = retSale03R510B2[intNo][intNoL].trim();
        if (intRow == 34) {
          stringTemp = "";
        }
        hashtableSale03R510B2Data.put(arrayRowDefine[intNoL], stringTemp);
      }
    }
    return hashtableSale03R510B2;
  }

  public Hashtable getSale03R510CH(String stringProjectID1, String stringYearAC, String stringMonth, FargloryUtil exeUtil, talk dbAO) throws Throwable {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12
    String[] arrayRowDefine = { "�C��", "�L", "���ʦW��", "���ĳq��", "�w�ʫȤ�", "���~�X�@", "�P�~�q��", "���~��", "�C��q��", "�����q��", "²�T", "POP", "���ݳq��" };
    String[][] retSale03R510C = dbAO.queryFromPool("AO_SPSale03R510C  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // �q���`�ӤH
    String[][] retSale03R510CE = dbAO.queryFromPool("AO_SPSale03R510C_E  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonth + "' "); // �q���`�ӤH����������*/
    String stringRowLine = "";
    String stringTemp = "";
    Hashtable hashtableSale03R510C = new Hashtable();
    Hashtable hashtableSale03R510CData = new Hashtable();
    int intRow = 0;
    // �ĤG���-�q���`�ӤH-�������͡BPASS
    for (int intNo = 0; intNo < retSale03R510CE.length; intNo++) {
      stringRowLine = retSale03R510CE[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1;
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510CData = (Hashtable) hashtableSale03R510C.get(stringRowLine);
      if (hashtableSale03R510CData == null) {
        hashtableSale03R510CData = new Hashtable();
        hashtableSale03R510C.put(stringRowLine, hashtableSale03R510CData);
      }
      // ��������
      stringTemp = retSale03R510CE[intNo][2].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510CData.put("��������", stringTemp);
      }
      // PASS
      stringTemp = retSale03R510CE[intNo][3].trim();
      if (exeUtil.doParseDouble(stringTemp) > 0) {
        hashtableSale03R510CData.put("PASS", stringTemp);
      }
    }
    for (int intNo = 0; intNo < retSale03R510C.length; intNo++) {
      stringRowLine = retSale03R510C[intNo][0].trim();
      intRow = exeUtil.doParseInteger(stringRowLine) - 1; // �C�ƴ�1
      //
      stringRowLine = convert.add0("" + intRow, "3");
      //
      hashtableSale03R510CData = (Hashtable) hashtableSale03R510C.get(stringRowLine);
      if (hashtableSale03R510CData == null) {
        hashtableSale03R510CData = new Hashtable();
        hashtableSale03R510C.put(stringRowLine, hashtableSale03R510CData);
      }
      for (int intNoL = 0; intNoL < arrayRowDefine.length; intNoL++) {
        stringTemp = retSale03R510C[intNo][intNoL].trim();
        if (intRow == 40) {
          stringTemp = "";
        }
        hashtableSale03R510CData.put(arrayRowDefine[intNoL], stringTemp);
      }
    }
    return hashtableSale03R510C;
  }

  public void SetOrderList(String stringTitile, String stringOrderType, int intGetDataRow, int intPutDataRow, int intColOrder11, int intColOrder12, int intRowOrder,
      String[] arraySSMediaID, String[] arraySSMediaIDPos, String[][] retTitle2, Dispatch objectSheet1, FargloryUtil exeUtil, FargloryExcel exeExcel) throws Throwable {
    int intCol = 0;
    int intCol2 = 0;
    int intPos = 0;
    int intPos2 = 0;
    String stringTemp = "";
    String stringValue = "";
    String stringColName = "";
    String[] arrayValue = new String[arraySSMediaID.length];
    String[] arrayValuePOS = new String[arraySSMediaID.length];
    for (int intNoL = 0; intNoL < arraySSMediaID.length; intNoL++) {
      // �~�֭p
      stringTemp = exeExcel.getDataFromExcel2(exeUtil.doParseInteger(arraySSMediaIDPos[intNoL]), intGetDataRow, objectSheet1);
      if ("null".equals(stringTemp))
        stringTemp = "";
      // System.out.println(stringTitile+"�ƦW1
      // Col("+arraySSMediaIDPos[intNoL]+")Row("+intGetDataRow+")��("+stringTemp+")------------------------------------")
      // ;
      arrayValue[intNoL] = stringTemp;
    }
    arrayValuePOS = exeUtil.getOrderList(stringOrderType, arrayValue);
    for (int intNoL = 0; intNoL < arraySSMediaID.length; intNoL++) {
      stringValue = arrayValuePOS[intNoL];
      intCol = exeUtil.doParseInteger(arraySSMediaIDPos[intNoL]);
      //
      exeExcel.putDataIntoExcel(intCol, intPutDataRow, stringValue, objectSheet1);
      // �ƦW
      // 1-3 �W
      // System.out.println(stringTitile+" �ƦW2
      // Col("+intCol+")RowL("+intPutDataRow+")��("+stringValue+")------------------------------------")
      // ;
      intPos = exeUtil.doParseInteger(exeUtil.doDeleteDogAfterZero(stringValue));
      if (intPos > 3)
        continue;
      if (intPos == 0)
        continue;
      //
      if (intCol <= 14) {
        intPos2 = 0;
        intCol2 = (intCol - 4) / 2;
      } else if (intCol >= 26) {
        intPos2 = 2;
        intCol2 = (intCol - 26) / 2;
      } else {
        intPos2 = 1;
        intCol2 = (intCol - 18) / 2;
      }
      //
      exeExcel.putDataIntoExcel(intColOrder11, intRowOrder + intPos, retTitle2[intPos2][intCol2], objectSheet1);
      stringColName = exeExcel.getExcelColumnName("A", intCol);
      if (",�~�Z,�s�ӤH,�`�ӤH,".indexOf(stringTitile) != -1)
        stringTemp = "=" + stringColName + "" + (intGetDataRow + 1) + "/AG" + (intGetDataRow + 1);
      if (",����O�βv,�ӤH�O��,".indexOf(stringTitile) != -1)
        stringTemp = "=" + stringColName + "" + (intGetDataRow + 1);
      // System.out.println(stringTitile+" �W��
      // Col("+intColOrder12+")Row("+(intRowOrder+intPos)+")��("+stringTemp+")------------------------------------")
      // ;
      exeExcel.putDataIntoExcel(intColOrder12, intRowOrder + intPos, stringTemp, objectSheet1);
    }
  }

  public int getChangeExcelRow(int intRow) throws Throwable {
    // �~�Z
    if (25 == intRow + 1)
      return 29;// �����25 �אּ 30
    if (26 == intRow + 1)
      return 30;// �W���26 �אּ 31
    if (28 == intRow + 1)
      return 32;// �~�֭p28 �אּ 33
    if (29 == intRow + 1)
      return 33;// �~�ƦW29 �אּ 34
    if (30 == intRow + 1)
      return 34;// �ײ֭p30 �אּ 35
    // �s�ӤH
    if (31 == intRow + 1)
      return 38;// �����31 �אּ 39
    if (32 == intRow + 1)
      return 41;// �W���32 �אּ 42
    if (34 == intRow + 1)
      return 45;// �~�֭p34 �אּ 46
    if (35 == intRow + 1)
      return 48;// �~�ƦW35 �אּ 49 ��JAVA �B�z
    if (36 == intRow + 1)
      return 49;// �ײ֭p36 �אּ 50
    // �`�ӤH
    if (37 == intRow + 1)
      return 52;// �����37 �אּ 53
    if (38 == intRow + 1)
      return 55;// �W���38 �אּ 56
    if (40 == intRow + 1)
      return 59;// �~�֭p40 �אּ 60
    if (41 == intRow + 1)
      return 62;// �~�ƦW41 �אּ 63 ��JAVA�B�z
    if (42 == intRow + 1)
      return 63;// �ײ֭p42 �אּ 64
    // �ƦW
    if (61 == intRow + 1)
      return 100;
    if (62 == intRow + 1)
      return 101;
    if (63 == intRow + 1)
      return 102;
    return intRow;
  }

  // �~�Z speMakerSale03R510B_DATA 'H111A','2015','08'
  public void doExcel2(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbSale) throws Throwable {
    int intCol = 2;
    int intRow = 0;
    String stringProjectID1 = getValue("ProjectID1").trim();
//    String dateType = getValue("dateType").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringMonthL = "";
    String stringValue = "";
    String[][] retTableData = null;
    for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringMonthL = convert.add0("" + intNo, "2");
      retTableData = dbSale.queryFromPool("speMakerSale03R510B_DATA2  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonthL + "',  '" + dateType + "' " ); // �~�Z
      for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
        stringValue = retTableData[0][intNoL + 2];
        intRow = arrayRow[intNoL];
        // System.out.println("intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
      }
      intCol++;
    }
  }

  // �s�ӤH AO_SPSale03R530B_DATA 'H111A','2015','08'
  public void doExcel3(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbSale) throws Throwable {
    int intCol = 2;
    int intRow = 0;
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringMonthL = "";
    String stringValue = "";
    String[][] retTableData = null;
    for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringMonthL = convert.add0("" + intNo, "2");
      retTableData = dbAO.queryFromPool("AO_SPSale03R510B_DATA  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonthL + "' "); // �~�Z
      for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
        stringValue = retTableData[0][intNoL + 2];
        intRow = arrayRow[intNoL];
        // System.out.println("intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
      }
      intCol++;
    }
  }

  // �`�ӤH AO_SPSale03R530C_DATA 'H111A','2015','08'
  public void doExcel4(int[] arrayRow, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil, talk dbAO, talk dbDoc, talk dbSale) throws Throwable {
    int intCol = 2;
    int intRow = 0;
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringMonthL = "";
    String stringValue = "";
    String[][] retTableData = null;
    for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringMonthL = convert.add0("" + intNo, "2");
      retTableData = dbAO.queryFromPool("AO_SPSale03R510C_DATA  '" + stringProjectID1 + "',  '" + stringYearAC + "',  '" + stringMonthL + "' "); // �~�Z
      for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
        stringValue = retTableData[0][intNoL + 2];
        intRow = arrayRow[intNoL];
        // System.out.println("intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
        // ;
        exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
      }
      intCol++;
    }
  }

  public void doExcel8(Hashtable hashtableDataPosition, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil) throws Throwable {
    int intCol = 0;
    int intRow = 0;
    String stringValue = "";
    String stringValue2 = "";
    String[][] retDataPosition = (String[][]) hashtableDataPosition.get("SHEET8");
    if (retDataPosition == null)
      return;
    for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
      intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
      intRow = exeUtil.doParseInteger(retDataPosition[intNo][1]);
      stringValue = retDataPosition[intNo][2];
      //
      if (exeUtil.doParseDouble(stringValue) == 0)
        stringValue = "0";
      //
      exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
    }
  }

  public void doExcel9(Hashtable hashtableDataPosition, Dispatch objectSheet1, FargloryExcel exeExcel, FargloryUtil exeUtil) throws Throwable {
    int intCol = 0;
    int intRow = 0;
    String stringValue = "";
    String stringValue2 = "";
    String[][] retDataPosition = (String[][]) hashtableDataPosition.get("SHEET9");
    if (retDataPosition == null)
      return;
    for (int intNo = 0; intNo < retDataPosition.length; intNo++) {
      intCol = exeUtil.doParseInteger(retDataPosition[intNo][0]);
      intRow = exeUtil.doParseInteger(retDataPosition[intNo][1]);
      stringValue = retDataPosition[intNo][2];
      //
      if (exeUtil.doParseDouble(stringValue) == 0)
        stringValue = "0";
      //
      exeExcel.putDataIntoExcel(intCol, intRow, stringValue, objectSheet1);
    }
  }

  public String[] getCostIDDoc7M070(String[] arraySSMediaID, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringSSMediaID = "";
    String stringTemp = "";
    String[] arrayCostID = new String[arraySSMediaID.length];
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
      stringSSMediaID = arraySSMediaID[intNo].trim();
      //
      hashtableAnd.put("UseType", "A");
      hashtableAnd.put("SSMediaID", stringSSMediaID);
      hashtableAnd.put("MoneyType", "A");
      vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, "", dbDoc);
      //
      stringTemp = "";
      for (int intNoL = 0; intNoL < vectorDoc7M070.size(); intNoL++) {
        hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNoL);
        if (hashtableDoc7M070 == null)
          continue;
        stringCostID = "" + hashtableDoc7M070.get("CostID");
        stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
        //
        stringTemp += "," + stringCostID + stringCostID1 + ",";

      }
      arrayCostID[intNo] = stringTemp;
      // System.out.println(intNo+"("+stringTemp+")------------------------------------------------")
      // ;
    }
    return arrayCostID;
  }

  // �����k�� �� 1-6���
  public String getCostIDMoneyTypeAC(FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostIDMoneyTypeAC = "";
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    hashtableAnd.put("UseType", "A");
    vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, " AND  MoneyType  IN  ('A',  'C') ORDER BY  CostID,  CostID1 ", dbDoc);
    //
    for (int intNo = 0; intNo < vectorDoc7M070.size(); intNo++) {
      hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNo);
      if (hashtableDoc7M070 == null)
        continue;
      stringCostID = "" + hashtableDoc7M070.get("CostID");
      stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
      //
      stringCostIDMoneyTypeAC += "," + stringCostID + stringCostID1 + ",";

    }
    return stringCostIDMoneyTypeAC;
  }

  public String getCostID1To6MediaDoc7M070(FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostID1To6Media = "";
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    hashtableAnd.put("UseType", "A");
    hashtableAnd.put("MoneyType", "C");
    vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, " ORDER BY  CostID,  CostID1 ", dbDoc);
    //
    for (int intNo = 0; intNo < vectorDoc7M070.size(); intNo++) {
      hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNo);
      if (hashtableDoc7M070 == null)
        continue;
      stringCostID = "" + hashtableDoc7M070.get("CostID");
      stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
      //
      stringCostID1To6Media += "," + stringCostID + stringCostID1 + ",";

    }
    return stringCostID1To6Media;
  }

  public String getCostID1To11MediaDoc7M070(FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringCostID1To11Media = "";
    Vector vectorDoc7M070 = null;
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc7M070 = null;
    //
    hashtableAnd.put("UseType", "A");
    hashtableAnd.put("MoneyType", "B");
    vectorDoc7M070 = exeUtil.getQueryDataHashtable("Doc7M070", hashtableAnd, " ORDER BY  CostID,  CostID1 ", dbDoc);
    //
    for (int intNo = 0; intNo < vectorDoc7M070.size(); intNo++) {
      hashtableDoc7M070 = (Hashtable) vectorDoc7M070.get(intNo);
      if (hashtableDoc7M070 == null)
        continue;
      stringCostID = "" + hashtableDoc7M070.get("CostID");
      stringCostID1 = "" + hashtableDoc7M070.get("CostID1");
      //
      stringCostID1To11Media += "," + stringCostID + stringCostID1 + ",";

    }
    return stringCostID1To11Media;
  }

  public Hashtable getMoneyFrontTable1(String[] arraySSMediaIDPos, String[] arraySSMediaID, String[][] retSale03R510B, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS,
      Vector vectorCostID) throws Throwable {
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringDateStart = "";
    String stringDateEnd = "";
    Hashtable hashtableMoney = new Hashtable();
    Hashtable hashtableDataPosition = new Hashtable();
    // ����϶�
    stringDateStart = stringYearAC + "1201";
    stringDateStart = datetime.dateAdd(stringDateStart, "y", -1);
    stringDateStart = exeUtil.getDateConvertFullRoc(stringDateStart);
    stringDateEnd = stringYearAC + stringMonth + "01";
    stringDateEnd = datetime.dateAdd(stringDateEnd, "m", 1);
    stringDateEnd = datetime.dateAdd(stringDateEnd, "d", -1);
    stringDateEnd = exeUtil.getDateConvertFullRoc(stringDateEnd);
    // ���� �e�~12���-���~X��(�C��B�~�֭p) 001%-%103/05
    doCetMoneyTable1("A%-%001", stringProjectID1, stringDateStart, stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);
    // ���� �ܥ��멳 �ײ֭p 002
    doCetMoneyTable1("A%-%002", stringProjectID1, "", stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);

    // �����k�� �e�~12���-���~X��(�C��B�~�֭p) 001%-%103/05
    // System.out.println("doCetMoneyTable1
    // B-----------------------------------------------------------S") ;
    doCetMoneyTable1("B%-%001", stringProjectID1, stringDateStart, stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);
    // System.out.println("doCetMoneyTable1
    // B-----------------------------------------------------------E") ;
    // �����k�� �ܥ��멳 �ײ֭p 002
    doCetMoneyTable1("B%-%002", stringProjectID1, "", stringDateEnd, exeUtil, dbDoc, dbDocCS, hashtableMoney);

    // ��ƳB�z 15
    Vector vectorData = new Vector();
    int intRow = 16;
    int intCol = 0;
    String stringDate = stringDateStart;
    String stringKEY = "";
    String stringValue = "";
    String stringCostID = "";
    String[] arrayTemp = new String[3];
    double doubleYear = 0;
    //
    stringDateStart = stringDateStart.replaceAll("/", "");
    for (int intNo = 0; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringDate = datetime.dateAdd(stringDateStart, "m", intNo);
      stringDate = exeUtil.getDateConvert(stringDate);
      stringDate = exeUtil.doSubstring(stringDate, 0, 7);
      intCol = (intNo == 0) ? 3 : 4 + (intNo - 1) * 2;
      // ��
      stringKEY = "A%-%001%-%" + stringDate;
      stringValue = "" + hashtableMoney.get(stringKEY);
      // �~�֭p�[�`
      if (intNo > 0) {
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      //
      // System.out.println(intCol+":"+intRow+"KEY("+stringKEY+")("+convert.FourToFive(stringValue,
      // 4)+")------------------------------") ;
      arrayTemp = new String[3];
      arrayTemp[0] = "" + intCol;
      arrayTemp[1] = "" + intRow;
      arrayTemp[2] = stringValue;
      vectorData.add(arrayTemp);
    }
    // �~�֭p
    arrayTemp = new String[3];
    arrayTemp[0] = "28";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = convert.FourToFive("" + doubleYear, 0);
    vectorData.add(arrayTemp);
    // �ײ֭p
    stringValue = "" + hashtableMoney.get("A%-%002");
    arrayTemp = new String[3];
    arrayTemp[0] = "31";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = stringValue;
    vectorData.add(arrayTemp);

    intRow = 17;
    doubleYear = 0;
    //
    stringDateStart = stringDateStart.replaceAll("/", "");
    for (int intNo = 0; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
      stringDate = datetime.dateAdd(stringDateStart, "m", intNo);
      stringDate = exeUtil.getDateConvert(stringDate);
      stringDate = exeUtil.doSubstring(stringDate, 0, 7);
      intCol = (intNo == 0) ? 3 : 4 + (intNo - 1) * 2;
      // ��
      stringKEY = "B%-%001%-%" + stringDate;
      stringValue = "" + hashtableMoney.get(stringKEY);
      // �~�֭p�[�`
      if (intNo > 0) {
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      //
      // System.out.println(intCol+":"+intRow+"KEY("+stringKEY+")("+convert.FourToFive(stringValue,
      // 4)+")------------------------------") ;
      arrayTemp = new String[3];
      arrayTemp[0] = "" + intCol;
      arrayTemp[1] = "" + intRow;
      arrayTemp[2] = stringValue;
      vectorData.add(arrayTemp);
    }
    // �~�֭p
    arrayTemp = new String[3];
    arrayTemp[0] = "28";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = convert.FourToFive("" + doubleYear, 0);
    vectorData.add(arrayTemp);
    // �ײ֭p
    stringValue = "" + hashtableMoney.get("B%-%002");
    arrayTemp = new String[3];
    arrayTemp[0] = "31";
    arrayTemp[1] = "" + intRow;
    arrayTemp[2] = stringValue;
    vectorData.add(arrayTemp);

    //
    hashtableDataPosition.put("Table1DATA", (String[][]) vectorData.toArray(new String[0][0]));

    return hashtableDataPosition;
  }

  public Hashtable getMoneyFrontTable2(int[] arrayRow, Vector vectorTitles, String[] arraySSMediaIDPos, String[] arraySSMediaID, String[][] retSale03R510B, FargloryUtil exeUtil,
      talk dbDoc, talk dbDocCS, Vector vectorCostID) throws Throwable {
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringYearAC = getValue("YearAC").trim();
    String stringMonth = getValue("Month").trim();
    String stringDateStart = "";
    String stringDateEnd = "";
    String stringCostID1To6Media = getCostID1To6MediaDoc7M070(exeUtil, dbDoc);
    String stringCostID1To11Media = getCostID1To11MediaDoc7M070(exeUtil, dbDoc);
    String stringCostIDMoneyTypeAC = getCostIDMoneyTypeAC(exeUtil, dbDoc);
    String[] arrayCostID = getCostIDDoc7M070(arraySSMediaID, exeUtil, dbDoc);
    String[] arrayMoney = { "", "", "", "", "", "", "", "", "", "", "" };
    String[] arrayMoneyPOS = { "", "", "", "", "", "", "", "", "", "", "" };
    String[][] retQueryDate = new String[2][2];
    Hashtable hashtableMoney = new Hashtable();
    Hashtable hashtableDataPosition = new Hashtable();
    // ����϶�
    stringDateStart = stringYearAC + "1201";
    stringDateStart = datetime.dateAdd(stringDateStart, "y", -1);
    stringDateStart = exeUtil.getDateConvertFullRoc(stringDateStart);
    stringDateEnd = stringYearAC + stringMonth + "01";
    stringDateEnd = datetime.dateAdd(stringDateEnd, "m", 1);
    stringDateEnd = datetime.dateAdd(stringDateEnd, "d", -1);
    stringDateEnd = exeUtil.getDateConvertFullRoc(stringDateEnd);

    // �e�~12���-���~X��(�C��B�~�֭p) ���� A%-%003%-%�q���N�X(�дڥN�X)%-%103/05
    // �e�~12���-���~X��(�C��B�~�֭p) �� B%-%003%-%�q���N�X(�дڥN�X)%-%103/05
    doCetMoneyTable2("003", stringProjectID1, stringDateStart, stringDateEnd, stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, arraySSMediaID, arrayCostID,
        retSale03R510B, exeUtil, dbDoc, dbDocCS, hashtableMoney, vectorCostID);
    // �ܥ��멳 �ײ֭p �� A%-%004%-%�q���N�X(�дڥN�X)
    // �ܥ��멳 �ײ֭p �� B%-%004%-%�q���N�X(�дڥN�X)
    System.out.println("�ײ֭p---------------------------------S");
    doCetMoneyTable2("004", stringProjectID1, "", stringDateEnd, stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, arraySSMediaID, arrayCostID,
        retSale03R510B, exeUtil, dbDoc, dbDocCS, hashtableMoney, vectorCostID);
    System.out.println("�ײ֭p---------------------------------E");

    // ��ƳB�z 15
    Vector vectorData = new Vector();
    int intRow = 0;
    int intRowL = 0;
    int intCol = 0;
    String stringDate = stringDateStart;
    String stringKEY = "";
    String stringValue = "";
    String stringCostID = "";
    String stringMode = "";
    String[] arrayTemp = new String[3];
    String[] arrayMode = { "A", "B" }; // A �� B ��(���]�t�@�P���u)
    double doubleYear = 0;
    //
    // ��ƳB�z 68 �����-�q���N�X
    intRow = 67;
    stringDate = stringYearAC + stringMonth + "01";
    stringDate = exeUtil.getDateConvert(stringDate);
    stringDate = exeUtil.doSubstring(stringDate, 0, 7);
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNo] + "%-%" + stringDate;
        stringValue = "" + hashtableMoney.get(stringKEY);
        //
        if (exeUtil.doParseDouble(stringValue) == 0)
          stringValue = "0";
        //
        System.out.println("(" + intNo + ")" + arraySSMediaIDPos[intNo] + ":" + intRowL + " ����� KEY(" + stringKEY + ")(" + vectorTitles.get(intNo) + ")("
            + convert.FourToFive(stringValue, 4) + ")------------------------------");
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
      }
      // �t��
      doubleYear = 0;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
        //
        stringValue = "" + hashtableMoney.get(stringKEY);
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
    }
    // ��ƳB�z 70 �W���-�q���N�X
    intRow = 69;
    stringDate = stringYearAC + stringMonth + "01";
    stringDate = datetime.dateAdd(stringDate, "m", -1);
    stringDate = exeUtil.getDateConvert(stringDate);
    stringDate = exeUtil.doSubstring(stringDate, 0, 7);
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNo] + "%-%" + stringDate;
        stringValue = "" + hashtableMoney.get(stringKEY);
        //
        if (exeUtil.doParseDouble(stringValue) == 0)
          stringValue = "0";
        //
        // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------1")
        // ;
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
      }
      // �t��
      doubleYear = 0;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
        //
        stringValue = "" + hashtableMoney.get(stringKEY);
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
      //
    }
    // ��ƳB�z 74 �~�֭p-�q���N�X
    intRow = 73;
    stringDateStart = stringYearAC + "0101";
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        doubleYear = 0;
        for (int intNoL = 1; intNoL <= exeUtil.doParseInteger(stringMonth); intNoL++) {
          stringDate = datetime.dateAdd(stringDateStart, "m", (intNoL - 1));
          stringDate = exeUtil.getDateConvert(stringDate);
          stringDate = exeUtil.doSubstring(stringDate, 0, 7);
          //
          stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNo] + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------1")
          // ;
          doubleYear += exeUtil.doParseDouble(stringValue);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------�֭p")
          // ;
        }
        //
        // if(doubleYear == 0) continue ;
        //
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
        vectorData.add(arrayTemp);
      }
      // �t��
      doubleYear = 0;
      stringDateStart = stringYearAC + "0101";
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        for (int intNoL = 1; intNoL <= exeUtil.doParseInteger(stringMonth); intNoL++) {
          stringDate = datetime.dateAdd(stringDateStart, "m", (intNoL - 1));
          stringDate = exeUtil.getDateConvert(stringDate);
          stringDate = exeUtil.doSubstring(stringDate, 0, 7);
          //
          stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------1")
          // ;
          doubleYear += exeUtil.doParseDouble(stringValue);
          // System.out.println("stringKEY("+stringKEY+")stringValue("+stringValue+")--------------------------------------�֭p")
          // ;
        }
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
    }
    // ��ƳB�z 76 �ײ֭p-�q���N�X
    intRow = 75;
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < arraySSMediaID.length; intNo++) {
        stringKEY = stringMode + "%-%004%-%" + arraySSMediaID[intNo];
        stringValue = "" + hashtableMoney.get(stringKEY);
        //
        if (exeUtil.doParseDouble(stringValue) == 0)
          stringValue = "0";
        // if(exeUtil.doParseDouble(stringValue) == 0) continue ;
        arrayMoney[intNo] = stringValue;
        //
        arrayTemp = new String[3];
        arrayTemp[0] = arraySSMediaIDPos[intNo];
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
      }
      // �t��
      doubleYear = 0;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        stringKEY = stringMode + "%-%004%-%" + stringCostID;
        //
        stringValue = "" + hashtableMoney.get(stringKEY);
        doubleYear += exeUtil.doParseDouble(stringValue);
      }
      arrayTemp = new String[3];
      arrayTemp[0] = "3";
      arrayTemp[1] = "" + intRowL;
      arrayTemp[2] = convert.FourToFive("" + doubleYear, 4);
      vectorData.add(arrayTemp);
    }
    hashtableDataPosition.put("Table2DATA", (String[][]) vectorData.toArray(new String[0][0]));

    // ��ƳB�z ��-�дڥN�X
    vectorData = new Vector();
    stringDateStart = stringYearAC + "0101";
    stringDateStart = datetime.dateAdd(stringDateStart, "m", -1);
    stringDateStart = exeUtil.getDateConvert(stringDateStart).replaceAll("/", "");
    //
    arrayMode[1] = "";
    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      if ("".equals(stringMode))
        continue;
      vectorData = new Vector();
      intRow = 66;
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        for (int intNoL = 0; intNoL <= exeUtil.doParseInteger(stringMonth); intNoL++) {
          stringDate = datetime.dateAdd(stringDateStart, "m", intNoL);
          stringDate = exeUtil.getDateConvertFullRoc(stringDate);
          stringDate = exeUtil.doSubstring(stringDate, 0, 7);
          //
          stringKEY = stringMode + "%-%003%-%" + stringCostID + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // System.out.println("1stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")------------------------------------S")
          // ;
          if (exeUtil.doParseDouble(stringValue) == 0)
            continue;
          //
          intRowL++;
          arrayTemp = new String[3];
          arrayTemp[0] = "0";
          arrayTemp[1] = "" + intRowL;
          arrayTemp[2] = stringCostID;
          vectorData.add(arrayTemp);
          arrayTemp = new String[3];
          arrayTemp[0] = "3";
          arrayTemp[1] = "" + intRowL;
          arrayTemp[2] = stringDate;
          vectorData.add(arrayTemp);
          arrayTemp = new String[3];
          arrayTemp[0] = "5";
          arrayTemp[1] = "" + intRowL;
          arrayTemp[2] = stringValue;
          vectorData.add(arrayTemp);
          // System.out.println("1stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")intRow("+intRow+")stringDate("+stringDate+")------------------------------------E")
          // ;
        }
      }
      intRow = 100;
      intRowL = "A".equals(stringMode) ? intRow : intRow + 10;
      // ��ƳB�z �ײ֭p-�дڥN�X
      for (int intNo = 0; intNo < vectorCostID.size(); intNo++) {
        stringCostID = "" + vectorCostID.get(intNo);
        //
        stringKEY = stringMode + "%-%004%-%" + stringCostID;
        stringValue = "" + hashtableMoney.get(stringKEY);
        // System.out.println("2stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")------------------------------------S")
        // ;
        if (exeUtil.doParseDouble(stringValue) == 0)
          continue;
        //
        intRowL++;
        arrayTemp = new String[3];
        arrayTemp[0] = "0";
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringCostID;
        vectorData.add(arrayTemp);
        arrayTemp = new String[3];
        arrayTemp[0] = "5";
        arrayTemp[1] = "" + intRowL;
        arrayTemp[2] = stringValue;
        vectorData.add(arrayTemp);
        // System.out.println("2stringKEY("+stringKEY+")stringValue("+stringValue+")stringCostID("+stringCostID+")intRow("+intRow+")------------------------------------S")
        // ;
      }
      hashtableDataPosition.put("OTHER", (String[][]) vectorData.toArray(new String[0][0]));
    }

    // SHEET8 �M SHEET9 ��ƳB�z
    arrayMode = new String[] { "A", "B" };
    String[] arraySheet = { "SHEET9", "SHEET8" };

    for (int intMode = 0; intMode < arrayMode.length; intMode++) {
      stringMode = arrayMode[intMode];
      if ("".equals(stringMode))
        continue;
      vectorData = new Vector();
      intCol = 2;
      stringDateStart = stringYearAC + "0101";
      for (int intNo = 1; intNo <= exeUtil.doParseInteger(stringMonth); intNo++) {
        stringDate = datetime.dateAdd(stringDateStart, "m", (intNo - 1));
        stringDate = exeUtil.getDateConvert(stringDate);
        stringDate = exeUtil.doSubstring(stringDate, 0, 7);
        for (int intNoL = 0; intNoL < arrayRow.length; intNoL++) {
          stringKEY = stringMode + "%-%003%-%" + arraySSMediaID[intNoL] + "%-%" + stringDate;
          stringValue = "" + hashtableMoney.get(stringKEY);
          // 20180508 ���ഫ �� ���ġB�w�ʡB���� ==> ���ʡB���ġB�w��
          switch (arrayRow[intNoL]) {
          case 9:
            intRow = 10;
            break;
          case 10:
            intRow = 11;
            break;
          case 11:
            intRow = 9;
            break;
          default:
            intRow = arrayRow[intNoL];
            break;
          }
          //
          if (exeUtil.doParseDouble(stringValue) == 0)
            stringValue = "0";
          //
          // System.out.println("SHEET8------------stringKEY("+stringKEY+")intCol("+intCol+")intRow("+intRow+")stringValue("+stringValue+")----------------------------")
          // ;
          arrayTemp = new String[3];
          arrayTemp[0] = "" + intCol;
          arrayTemp[1] = "" + intRow;
          arrayTemp[2] = stringValue;
          vectorData.add(arrayTemp);
        }
        intCol++;
      }
      hashtableDataPosition.put(arraySheet[intMode], (String[][]) vectorData.toArray(new String[0][0]));
    }

    return hashtableDataPosition;
  }

  // �ˮ�
  // �e�ݸ���ˮ֡A���T�^�� True
  public boolean isBatchCheckOK() throws Throwable {
    String stringProjectID = getValue("ProjectID1").trim();
    if ("".equals(stringProjectID)) {
      messagebox("[�קO] ���i���ťաC");
      getcLabel("ProjectID1").requestFocus();
      return false;
    }
    String stringYearAC = getValue("YearAC").trim();
    if ("".equals(stringYearAC)) {
      messagebox("[����~] ���i���ťաC");
      getcLabel("YearAC").requestFocus();
      return false;
    }
    String stringMonth = getValue("Month").trim();
    if ("".equals(stringMonth)) {
      messagebox("[��] ���i���ťաC");
      getcLabel("Month").requestFocus();
      return false;
    }
    String dateType = getValue("dateType").trim();
    if ("".equals(dateType)) {
      messagebox("[�~�Z�ѷ�] ���i���ťաC");
      getcLabel("dateType").requestFocus();
      return false;
    }
    return true;
  }

  // ��Ʈw Doc
  public void doCetMoneyTable1(String stringSign, String stringProjectID1, String stringDateStart, String stringDateEnd, FargloryUtil exeUtil, talk dbDoc, talk dbDocCS,
      Hashtable hashtableMoney) throws Throwable {
    String stringSql = "";
    String[][] retData = new String[0][0];
    // 2015-12-01 B3018 781 �� 782 ��J��� 2015-12-01�ᤣ�C�J�B�z
    stringDateStart = exeUtil.getDateConvert(stringDateStart);
    stringDateEnd = exeUtil.getDateConvert(stringDateEnd);
    // 0 �ĵo���B 1 �w����B 2 �д� 3 �ɴڨR�P 4 �ץ����B
    // 5 �ɴڨR�P
    stringSql = " SELECT  SUBSTRING(CONVERT(char(8),UseDate,111),1,7),  SUM(RealMoney) \n" + " FROM  Doc3M014_UseMoney_AC_view \n"
        + " WHERE   ( RTRIM(CostID)+RTRIM(CostID1)  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc2M020 \n" + " WHERE  BudgetID  LIKE  'B%' \n"
        + " AND  RTRIM(CostID)+RTRIM(CostID1) NOT IN ('871',  '900',  '910') " + " AND  ComNo  =  'CS' " + " ) \n" +
        // " OR \n" +
        // " RTRIM(CostID)+RTRIM(CostID1) IN ('269', '729', '899')" +
        " )\n" + " AND  RTRIM(CostID)+RTRIM(CostID1)  NOT  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc7M070 \n" + " WHERE  UseType  =  'A' \n"
        + " AND  MoneyType  =  'X' " + " ) \n" + " AND  InOut  =  'O' \n" + " AND  DepartNo  <>  '0531' \n" + " AND  ProjectID1  =  '" + stringProjectID1 + "' \n";
    if (!"".equals(stringDateStart))
      stringSql += " AND  UseDate  >=  '" + stringDateStart + "' \n";
    if (!"".equals(stringDateEnd))
      stringSql += " AND  UseDate  <=  '" + stringDateEnd + "' \n";
    if (stringSign.startsWith("B")) {
      stringSql += " AND  (RTRIM(CostID)+RTRIM(CostID1) IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc7M070 \n" + " WHERE  UseType = 'A' \n"
          + " AND  MoneyType IN('A',  'C') ) \n" + " OR " + " ISNULL(SSMediaID,'')  <> '' " + ") ";
    }
    stringSql += " GROUP BY SUBSTRING(CONVERT(char(8),UseDate,111),1,7) \n";
    retData = dbDoc.queryFromPool(stringSql);
    doDataTable1(stringSign, retData, exeUtil, hashtableMoney);
    if (dbDocCS != null) {
      // System.out.println("�H��---------------------------------------1") ;
      retData = dbDocCS.queryFromPool(stringSql.replaceAll("AND  ComNo  =  'Z6'", ""));
      doDataTable1(stringSign, retData, exeUtil, hashtableMoney);
    }
    // 2015-12-01 B3018 781 �� 782 ��J��� 2015-12-01������ʶ��ؤ�����϶�
    // Doc3M011 ���w CDate 2015-12-01 ��
    // Doc3M012 ���w �дڥN�X�� 781 �� 782�B�d�ߦ~�� �j�󵥩� DateStart �B�p�󵥩� DateEnd
    // Doc3M0123 ���w�קO
    // �d�ߦ~�� �j�� DateStart�A�_�l����� �~��/01/01 �A�_�l��� 01
    // �d�ߦ~�� ���� DateStart�A�_�l����� DateStart �A�_�l��� �� DateStart �Ҧb���
    // �d�ߦ~�� �p�� DateEnd�A��������� �~��/12/31 �A������� �� 12
    // �d�ߦ~�� ���� DateEnd�A��������� DateEnd �A������� �� DateEnd �Ҧb���
    // �p���@����B
    // �j�� ���o�C��ϥΤѼơA�p�� �ʤ���B
    // stringSign+"%-%"+stringYYMM
  }

  public void doDataTable1(String stringSign, String[][] retData, FargloryUtil exeUtil, Hashtable hashtableMoney) throws Throwable {
    String stringYYMM = "";
    String stringMoney = "";
    String stringKEY = "";
    double doubleTemp = 0;
    for (int intNo = 0; intNo < retData.length; intNo++) {
      stringYYMM = retData[intNo][0].trim();
      stringMoney = retData[intNo][1].trim();
      stringKEY = stringSign + "%-%" + stringYYMM;
      //
      if (stringSign.endsWith("002"))
        stringKEY = stringSign;
      //
      doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
      // System.out.println("KEY("+stringKEY+")("+convert.FourToFive(""+doubleTemp,
      // 4)+")------------------------------") ;
      hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
    }
  }

  public void doCetMoneyTable2(String stringSign, String stringProjectID1, String stringDateStart, String stringDateEnd, String stringCostIDMoneyTypeAC,
      String stringCostID1To6Media, String stringCostID1To11Media, String[] arraySSMediaID, String[] arrayCostID, String[][] retSale03R510B, FargloryUtil exeUtil, talk dbDoc,
      talk dbDocCS, Hashtable hashtableMoney, Vector vectorCostID) throws Throwable {
    String stringSql = "";
    String[][] retData = new String[0][0];
    // 2015-12-01 B3018 781 �� 782 ��J��� 2015-12-01�ᤣ�C�J�B�z
    stringDateStart = exeUtil.getDateConvert(stringDateStart);
    stringDateEnd = exeUtil.getDateConvert(stringDateEnd);
    // �q���N�� �קO+�q��������(J03)+�褸�~��(201605)+���ɲŸ�(-)+�y����(002)
    // 0 CostID 1 CostID1 2 SSMediaID 3 UseDate 4 RealMoney
    stringSql = " SELECT  CostID,  CostID1,  SSMediaID,  SUBSTRING(CONVERT(char(8),UseDate,111),1,7),  SUM(RealMoney) \n" + " FROM  Doc3M014_UseMoney_AC_view \n"
        + " WHERE    ( RTRIM(CostID)+RTRIM(CostID1)  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc2M020 \n" + " WHERE  BudgetID  LIKE  'B%' \n"
        + " AND  RTRIM(CostID)+RTRIM(CostID1) NOT IN ('871',  '900',  '910') " + " AND  ComNo  =  'CS' " + " ) \n" +
        // " OR \n" +
        // " RTRIM(CostID)+RTRIM(CostID1) IN ('269', '729', '899')" +
        " )\n" + " AND  RTRIM(CostID)+RTRIM(CostID1)  NOT  IN  (SELECT  RTRIM(CostID)+RTRIM(CostID1) \n" + " FROM  Doc7M070 \n" + " WHERE  UseType  =  'A' \n"
        + " AND  MoneyType  =  'X' " + " ) \n" + " AND  InOut  =  'O' \n" + " AND  DepartNo  <>  '0531' \n" + " AND  ProjectID1  =  '" + stringProjectID1 + "' \n";
    if (!"".equals(stringDateStart))
      stringSql += " AND  UseDate  >=  '" + stringDateStart + "' \n";
    if (!"".equals(stringDateEnd))
      stringSql += " AND  UseDate  <=  '" + stringDateEnd + "' \n";
    stringSql += " GROUP BY CostID,  CostID1,  SSMediaID,  SUBSTRING(CONVERT(char(8),UseDate,111),1,7) \n";
    retData = dbDoc.queryFromPool(stringSql);
    doData2(stringSign, "A", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil, hashtableMoney,
        vectorCostID);
    System.out.println(stringSign + "��---------------------------------------��S");
    doData2(stringSign, "B", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil, hashtableMoney,
        vectorCostID);
    System.out.println(stringSign + "��---------------------------------------��E");
    if (dbDocCS != null) {
      System.out.println("�H��---------------------------------------4");
      retData = dbDocCS.queryFromPool(stringSql.replaceAll("AND  ComNo  =  'Z6'", ""));
      doData2(stringSign, "A", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil,
          hashtableMoney, vectorCostID);
      doData2(stringSign, "B", stringCostIDMoneyTypeAC, stringCostID1To6Media, stringCostID1To11Media, retData, arraySSMediaID, arrayCostID, retSale03R510B, exeUtil,
          hashtableMoney, vectorCostID);
    }
    // 2015-12-01 B3018 781 �� 782 ��J��� 2015-12-01������ʶ��ؤ�����϶�
    // Doc3M011 ���w CDate 2015-12-01 ��
    // Doc3M012 ���w �дڥN�X�� 781 �� 782�B�d�ߦ~�� �j�󵥩� DateStart �B�p�󵥩� DateEnd
    // Doc3M0123 ���w�קO
    // �d�ߦ~�� �j�� DateStart�A�_�l����� �~��/01/01 �A�_�l��� 01
    // �d�ߦ~�� ���� DateStart�A�_�l����� DateStart �A�_�l��� �� DateStart �Ҧb���
    // �d�ߦ~�� �p�� DateEnd�A��������� �~��/12/31 �A������� �� 12
    // �d�ߦ~�� ���� DateEnd�A��������� DateEnd �A������� �� DateEnd �Ҧb���
    // �p���@����B
    // �j�� ���o�C��ϥΤѼơA�p�� �ʤ���B
    // stringSign+"%-%"+arraySSMediaIDL[intNoL]+"%-%"+stringYYMM ;
  }

  public void doData2(String stringSign, String stringCostIDMoneyMode, String stringCostIDMoneyTypeAC, String stringCostID1To6Media, String stringCostID1To11Media,
      String[][] retData, String[] arraySSMediaID, String[] arrayCostID, String[][] retSale03R510B, FargloryUtil exeUtil, Hashtable hashtableMoney, Vector vectorCostID)
      throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String stringYYMM = "";
    String stringMoney = "";
    String stringSSMediaID = "";
    String stringKEY = "";
    String stringCostIDAvgMoney = ""; // 2015-08-27 �令�̷~�Z ",730,820,231,233,261,262,269,490,491,492,831," ;
    String stringItem = "";
    String[] arraySSMediaIDL = null;
    String[] arraySSMediaIDMoneyL = null;
    String[] arrayRatio = new String[arraySSMediaID.length];
    for (int intNo = 0; intNo < arrayRatio.length; intNo++)
      arrayRatio[intNo] = "1";
    String[] arrayRatio1To6Media = new String[6];
    for (int intNo = 0; intNo < arrayRatio1To6Media.length; intNo++)
      arrayRatio1To6Media[intNo] = "0";
    String[] arrayRatio1To11Media = new String[arraySSMediaID.length];
    for (int intNo = 0; intNo < arrayRatio1To11Media.length; intNo++)
      arrayRatio1To11Media[intNo] = "0";
    double doubleTemp = 0;
    boolean booleanNeg = false;
    boolean booleanFlag = false;
    //
    if (retSale03R510B.length == 0) {
      for (int intNoL = 1; intNoL <= 6; intNoL++) {
        arrayRatio1To6Media[intNoL - 1] = "1";
      }
      for (int intNoL = 1; intNoL <= 11; intNoL++) {
        arrayRatio1To11Media[intNoL - 1] = "1";
      }
    }
    for (int intNo = 0; intNo < retSale03R510B.length; intNo++) {
      stringItem = retSale03R510B[intNo][1].trim();
      if ("B.�~�Z.�ײ֭p".equals(stringItem)) {
        booleanFlag = false;
        for (int intNoL = 1; intNoL <= 6; intNoL++) {
          arrayRatio1To6Media[intNoL - 1] = retSale03R510B[intNo][intNoL + 1];
          // System.out.println(intNoL+stringItem+"("+retSale03R510B[intNo][intNoL+1]+")---------------------------------------------------1")
          // ;
          if (exeUtil.doParseDouble(arrayRatio1To6Media[intNoL - 1]) > 0)
            booleanFlag = true;
        }
        if (!booleanFlag) {
          for (int intNoL = 1; intNoL <= 6; intNoL++) {
            arrayRatio1To6Media[intNoL - 1] = "1";
          }
        }
        //
        booleanFlag = false;
        for (int intNoL = 1; intNoL <= 11; intNoL++) {
          arrayRatio1To11Media[intNoL - 1] = retSale03R510B[intNo][intNoL + 1];
          // System.out.println(intNoL+stringItem+"("+retSale03R510B[intNo][intNoL+1]+")---------------------------------------------------2")
          // ;
          if (exeUtil.doParseDouble(arrayRatio1To11Media[intNoL - 1]) > 0)
            booleanFlag = true;
        }
        if (!booleanFlag) {
          for (int intNoL = 1; intNoL <= 11; intNoL++) {
            arrayRatio1To11Media[intNoL - 1] = "1";
          }
        }
      }
    }
    // �q���N�� �קO+�q��������(J03)+�褸�~��(201605)+���ɲŸ�(-)+�y����(002)
    // 0 CostID 1 CostID1 2 SSMediaID 3 UseDate 4 RealMoney
    double doubleTempSum = 0;
    boolean booleanFlagL = true;
    for (int intNo = 0; intNo < retData.length; intNo++) {
      stringCostID = retData[intNo][0].trim();
      stringCostID1 = retData[intNo][1].trim();
      stringSSMediaID = retData[intNo][2].trim();
      stringYYMM = retData[intNo][3].trim();
      stringMoney = retData[intNo][4].trim();
      // doubleTempSum = 0 ;
      //
      if (exeUtil.doParseDouble(stringMoney) == 0)
        continue;
      //
      if ("B".equals(stringCostIDMoneyMode)) {
        System.out.println(intNo + "stringCostIDMoneyTypeAC(" + stringCostIDMoneyTypeAC + ")stringCostIDMoneyMode(" + stringCostIDMoneyMode + ")SSMediaID(" + stringSSMediaID
            + ")KEY(" + stringCostID + stringCostID1 + "%-%" + stringYYMM + ")(" + convert.FourToFive(stringMoney, 4) + ")------------------------------0");
      }
      if ("".equals(stringSSMediaID)) {
        if ("B".equals(stringCostIDMoneyMode) && stringCostIDMoneyTypeAC.indexOf(stringCostID + stringCostID1) == -1)
          continue;
        // booleanFlagL = false ;
      } else {
        if (stringSSMediaID.length() < 9) {
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, 0, 1);
        } else {
          stringSSMediaID = convert.StringToken(stringSSMediaID, "-")[0];
          stringSSMediaID = exeUtil.doSubstring(stringSSMediaID, stringSSMediaID.length() - 9, stringSSMediaID.length() - 8);
        }
        if (",A,H,F,G,J,B,I,D,L,K,C,".indexOf(stringSSMediaID) == -1) {
          System.out.println(intNo + "���]�t 11 �j������------------------------------");
        }
        // System.out.println(intNo+"KEY("+stringCostID+stringCostID1+"%-%"+stringYYMM+")("+convert.FourToFive(stringMoney,
        // 4)+")------------------------------1") ;
        // booleanFlagL = true ;
      }
      // if("B".equals(stringCostIDMoneyMode))System.out.println(intNo+"SSMediaID("+stringSSMediaID+")KEY("+stringCostID+stringCostID1+"%-%"+stringYYMM+")("+convert.FourToFive(stringMoney,
      // 4)+")------------------------------1") ;
      //
      if ("".equals(stringSSMediaID)) {
        for (int intNoL = 0; intNoL < arrayCostID.length; intNoL++) {
          if (arrayCostID[intNoL].indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
            stringSSMediaID = arraySSMediaID[intNoL];
            break;
          }
        }
      }
      booleanNeg = false;
      if ("".equals(stringSSMediaID)) {
        System.out.println(intNo + " ���u------------------------------2");
        if (stringCostID1To11Media.indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
          // �̷~�Z����������B�� 1-11 �q��
          if (exeUtil.doParseDouble(stringMoney) < 0) {
            booleanNeg = true;
            stringMoney = convert.FourToFive("" + exeUtil.doParseDouble(stringMoney) * -1, 0);
          }
          arraySSMediaIDL = exeUtil.doCopyArray(arraySSMediaID);
          System.out.println(intNo + " �̷~�Z����������B�� 1-11 �q��(" + stringMoney + ")------------------------------2");
          arraySSMediaIDMoneyL = exeUtil.getMoneyFromRatio(stringMoney, arrayRatio1To11Media);
        } else if (stringCostID1To6Media.indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
          // �̷~�Z����������B�� 1-6 �q��
          if (exeUtil.doParseDouble(stringMoney) < 0) {
            booleanNeg = true;
            stringMoney = convert.FourToFive("" + exeUtil.doParseDouble(stringMoney) * -1, 0);
          }
          arraySSMediaIDL = new String[6];
          for (int intNoL = 0; intNoL < arraySSMediaIDL.length; intNoL++) {
            arraySSMediaIDL[intNoL] = arraySSMediaID[intNoL];
          }
          System.out.println(intNo + " �̷~�Z����������B�� 1-6 �q��(" + stringMoney + ")------------------------------2");
          arraySSMediaIDMoneyL = exeUtil.getMoneyFromRatio(stringMoney, arrayRatio1To6Media);
        } else if (stringCostIDAvgMoney.indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
          // ����������B�� 11 �q��
          if (exeUtil.doParseDouble(stringMoney) < 0) {
            booleanNeg = true;
            stringMoney = convert.FourToFive("" + exeUtil.doParseDouble(stringMoney) * -1, 0);
          }
          System.out.println(intNo + " ����������B�� 11 �q��2(" + stringMoney + ")------------------------------2");
          arraySSMediaIDL = exeUtil.doCopyArray(arraySSMediaID);
          arraySSMediaIDMoneyL = exeUtil.getMoneyFromRatio(stringMoney, arrayRatio);
        } else {
          System.out.println(intNo + " ��L------------------------------2");
          stringSSMediaID = stringCostID + stringCostID1;
          if (vectorCostID.indexOf(stringSSMediaID) == -1)
            vectorCostID.add(stringSSMediaID);
          //
          arraySSMediaIDL = new String[1];
          arraySSMediaIDL[0] = stringSSMediaID;
          arraySSMediaIDMoneyL = new String[1];
          arraySSMediaIDMoneyL[0] = stringMoney;
        }
      } else {
        System.out.println(intNo + " �����k��------------------------------2");
        arraySSMediaIDL = new String[1];
        arraySSMediaIDL[0] = stringSSMediaID;
        arraySSMediaIDMoneyL = new String[1];
        arraySSMediaIDMoneyL[0] = stringMoney;
      }
      //
      for (int intNoL = 0; intNoL < arraySSMediaIDL.length; intNoL++) {
        stringMoney = arraySSMediaIDMoneyL[intNoL];
        if (stringSign.endsWith("003")) {
          stringKEY = stringCostIDMoneyMode + "%-%" + stringSign + "%-%" + arraySSMediaIDL[intNoL] + "%-%" + stringYYMM;
        } else {
          stringKEY = stringCostIDMoneyMode + "%-%" + stringSign + "%-%" + arraySSMediaIDL[intNoL];
        }
        //
        if (booleanNeg) {
          doubleTemp = exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY)) - exeUtil.doParseDouble(stringMoney) / 10000;
        } else {
          doubleTemp = exeUtil.doParseDouble(stringMoney) / 10000 + exeUtil.doParseDouble("" + hashtableMoney.get(stringKEY));
        }
        if (stringYYMM.startsWith("2016")) {
          doubleTempSum += exeUtil.doParseDouble(stringMoney);
          // System.out.println(intNo+"("+stringKEY+")("+convert.FourToFive(stringMoney,
          // 0)+")("+convert.FourToFive(""+doubleTempSum,
          // 0)+")------------------------------3") ;
        }
        // if("B".equals(stringCostIDMoneyMode))System.out.println(intNo+"CostID("+stringCostID+")CostID1("+stringCostID1+")
        // KEY("+stringKEY+")("+convert.FourToFive(""+doubleTemp,
        // 4)+")("+convert.FourToFive(""+doubleTempSum,
        // 4)+")------------------------------3") ;
        // if("H".equals(arraySSMediaIDL[intNoL]) && "B".equals(stringCostIDMoneyMode))
        // {
        System.out.println("(" + intNo + "--" + intNoL + ")CostID(" + stringCostID + ")CostID1(" + stringCostID1 + ") stringYYMM(" + stringYYMM + ")KEY(" + stringKEY
            + ")THISMoney(" + stringMoney + ")(" + convert.FourToFive("" + doubleTemp, 4) + ")------------------------------3");
        // }
        hashtableMoney.put(stringKEY, convert.FourToFive("" + doubleTemp, 4));
        //
      }
    }
  }

  public String getInformation() {
    return "---------------ButtonExcel(ButtonExcel).defaultValue()----------------";
  }
}
