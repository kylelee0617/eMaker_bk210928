package Doc.Doc3;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import java.net.*;
import Farglory.util.FargloryUtil;
import Doc.Doc2M010;

public class Doc3M010FrontPrint extends bTransaction {
  public boolean action(String value) throws Throwable {
    // �^�ǭȬ� true ��ܰ��汵�U�Ӫ���Ʈw���ʩάd��
    // �^�ǭȬ� false ��ܱ��U�Ӥ����������O
    // �ǤJ�� value �� "�s�W","�d��","�ק�","�R��","�C�L","PRINT" (�C�L�w�����C�L���s),"PRINTALL"
    // (�C�L�w���������C�L���s) �䤤���@
    // isB34ControlPrint ��~�����ަC�L
    // doB34Print ��~���C�L
    Doc2M010 exeFun = new Doc2M010();
    FargloryUtil exeUtil = new FargloryUtil();
    JButton jButton = getButton("Button13");
    JTable jtable = getTable("Table3");
    String stringBarCode = getValue("BarCode").trim();
    String stringComNo = getValue("ComNo").trim();
    String stringPrintable = getValue("Printable").trim();
    String stringToday = datetime.getToday("YYYY/mm/dd");
    String stringUser = getUser();
    String stringFunctionName = getFunctionName();

    // Excel �C�L
    if (!"OO".equals(stringComNo)) {
      getButton("ButtonPrint").doClick();
      return false;
    }
    
    /**
     * 
     * �a�M���o��U���n�����O�S�Ϊ�
     * (���q���|�OCS�A���ӤW���N�n�Q�ਫ)
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */

    //
    System.out.println("��P-���q-�קO 80% ���ަC�L-----------------S");
    if (!isCanPrint10(exeUtil, exeFun)) return false;
    System.out.println("��P-���q-�קO 80% ���ަC�L-----------------E");

    // ��~�����ޱ��O�ΦC�L
    Vector vectorProjectID1 = new Vector();
    String stringLaserPrinter = isB34ControlPrint(exeUtil, exeFun, vectorProjectID1);
    if ("ERROR".equals(stringLaserPrinter)) return false;

    // [�S��H��] �C�L [���ʳ�] �ĥ� [�p�g�C�L��]
    Vector vectorEmployeeNo = exeFun.getEmployeeNoDoc3M011("10", "");
    String stringFunction = "";
    String stringDotPrinter = "";
    String stringPrinterPosition = "";
    String[] arrayTemp = null;

    if ("OO".equals(stringComNo) || vectorEmployeeNo.indexOf(stringUser.toUpperCase()) != -1) {
      System.out.println("[�S��H��] �C�L [���ʳ�] �ĥ� [�p�g�C�L��](" + stringUser + ")---------------------------------------");
      //
      stringFunction = "OO".equals(stringComNo) ? "���ʳ�C�L-����(Doc3R014)" : "���ʳ�C�L(Doc3R011)";
      // ���o �p�g�C���
      if ("".equals(stringLaserPrinter)) {
        stringLaserPrinter = getLaserPrinter(exeUtil, exeFun);
        if (stringLaserPrinter.endsWith("ERROR")) {
          messagebox(convert.StringToken(stringLaserPrinter, "%-%")[0]);
          return false;
        }
      }
      if ("B3018".equals(stringUser)) {
        stringFunction = "���ʳ�C�L-A4(Doc3R015)";
      }
      System.out.println("���o �p�g�C���(" + stringLaserPrinter + ")---------------------------------------");
    } else {
      // ���o �I�}���C���
      stringDotPrinter = getDocPrinter(exeUtil, exeFun);
      if (stringDotPrinter.endsWith("ERROR")) {
        messagebox(convert.StringToken(stringDotPrinter, "%-%")[0]);
        return false;
      }
      arrayTemp = convert.StringToken(stringDotPrinter, "%-%");
      stringDotPrinter = arrayTemp[0];
      stringFunction = arrayTemp[1];
      stringPrinterPosition = arrayTemp[2];
      System.out.println("���o �I�}���C���(" + stringDotPrinter + ")(" + stringPrinterPosition + ")---------------------------------------");
    }

    // �C�L�Ѽ�
    if (!"".equals(stringLaserPrinter)) {
      put("Doc3M011_PRINT_BarCode", "");
      showForm(stringFunction);
    }
    if ("".equals(stringDotPrinter)) stringDotPrinter = stringLaserPrinter;
    put("Doc3M011_PRINT_PrinterNAME", stringDotPrinter.replace("/", "\\"));
    put("Doc3M011_PRINT_BarCode", stringBarCode);
    put("Doc3M011_PRINT_Function", stringFunctionName);
    put("Doc3M011_PRINT_Enable", stringPrintable);
    
    // �C�L�I�s
    System.out.println("stringFunction(" + stringFunction + ")stringPrintable(" + stringPrintable + ")------------------------------------");
    showForm(stringFunction);
    
    // �C�L������
    if ("Y".equals(stringPrintable) && "B3018,".indexOf(getUser()) == -1) getInternalFrame(stringFunction).setVisible(false);
    
    // ���\�T��
    String stringPrintSuccess = ("" + get("Doc3M011_Print_OK")).trim();
    if ("Y".equals(stringPrintable) && "OK".equals(stringPrintSuccess)) {
      // �I�s ñ��
      // if("Y".equals(stringPrintable)) showForm("���ʳ�C�L-A4-ñ��(Doc3R0151)") ;
      // ���u��C�L
      doPrintAfter(stringBarCode, jtable, jButton, exeUtil, exeFun);
      // ��ܰT��
      if (!"".equals(stringPrinterPosition)) stringPrinterPosition = "\n�Ыe�� [" + stringPrinterPosition + "] �������ʳ�C";
      messagebox("�����C�L�C" + stringPrinterPosition);
      // ��~�C�L�ޱ���
      doB34Print(stringLaserPrinter, vectorProjectID1);
    }
    System.out.println("--------------------------------�C�L����" + stringFunction);
    put("Doc3M011_Print_OK", "null");

    return false;
  }

  public void doPrintAfter(String stringBarCode, JTable jtable, JButton jButton, FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    if ("0221".equals(stringDepartNoSubject)) return;
    String stringInOut = "";
    String stringDepart = "";
    String stringProjectID = "";
    String stringProjectID1 = "";
    String stringKey = "";
    String stringTableName = getFunctionName().indexOf("Doc3") != -1 ? "Doc3M011" : "Doc5M011";
    Vector vectorKey = new Vector();
    boolean booleanExcel = false;
    // �Ĥ@���~�C�L
    String stringPrintCount = exeFun.getNameUnionDoc("PrintCount", stringTableName, " AND  BarCode  =  '" + stringBarCode + "' ", new Hashtable(), exeUtil);
    if (exeUtil.doParseInteger(stringPrintCount) > 1) return;
    // �h�קO �� �h���� �~�C�L
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      stringInOut = ("" + jtable.getValueAt(intNo, 2)).trim();
      stringDepart = ("" + jtable.getValueAt(intNo, 3)).trim();
      stringProjectID = ("" + jtable.getValueAt(intNo, 4)).trim();
      stringProjectID1 = ("" + jtable.getValueAt(intNo, 5)).trim();
      stringKey = stringInOut + "%-%" + stringDepart + "%-%" + stringProjectID + "%-%" + stringProjectID1;
      //
      if (vectorKey.indexOf(stringKey) == -1) vectorKey.add(stringKey);
      //
      if (vectorKey.size() > 1) {
        booleanExcel = true;
        break;
      }
    }
    if (booleanExcel) {
      jButton.doClick();
    }
  }

  public String getDocPrinter(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    String stringFilePath = "C:\\Emaker_Print\\DocPrint3.ini"; // �������w�C���
    String stringDotPrinter = "";
    String stringFunction = "";
    String[] arrayText = null;
    File filePrinterPath = new File(stringFilePath);
    if (filePrinterPath.exists()) {
      // �������w�C���
      arrayText = exeUtil.getArrayDataFromText(stringFilePath);
      stringDotPrinter = convert.replace(arrayText[0].trim(), "\\", "/");
      if ("".equals(stringDotPrinter)) {
        return "��Ƶo�Ϳ��~�A�Ь���T�ǳ]�w1�C%-%ERROR";
      }
      if (!exeUtil.isPrinterExist(stringDotPrinter)) {
        Vector vectorPrinter = exeUtil.getPrinterPosition("/");
        String stringPrinterL = "";
        for (int intNo = 0; intNo < vectorPrinter.size(); intNo++) {
          stringPrinterL = ("" + vectorPrinter.get(intNo)).trim();
          if (stringPrinterL.equalsIgnoreCase(stringDotPrinter)) stringDotPrinter = stringPrinterL;
        }
        if (!exeUtil.isPrinterExist(stringDotPrinter)) {
          return "[" + stringDotPrinter + "]�C������s�b�A�Ь���T�Ǧw�ˡC%-%ERROR";
        }
      }
      return stringDotPrinter + "%-%���ʥӽЮ�-�C�L-�@��(Doc3M010)%-%";
    }

    //
    stringFilePath = "C:\\Emaker_Print\\DocPrinter.ini"; // ��� ���o �C���
    filePrinterPath = new File(stringFilePath);
    if (!filePrinterPath.exists()) {
      stringFilePath = "C:\\DocPrinter.ini";
      filePrinterPath = new File(stringFilePath);
    }
    if (!filePrinterPath.exists()) {
      return "�|���]�w�C����A�Ь���T�ǳ]�w�C%-%ERROR";
    }
    arrayText = exeUtil.getArrayDataFromText(stringFilePath);
    if (arrayText.length == 3) {
      String stringPrinterIP = "";
      String stringPrinterLabel = "";
      String stringPrinterPosition = "";
      // ��Ӫ� ���o�C���
      String[][] retDoc2M0403 = exeFun.getDoc2M0403(arrayText[1].trim());
      if (retDoc2M0403.length == 0) {
        return "�o�Ϳ��~�A�гq����T�ǳB�z3%-%ERROR";
      }
      stringPrinterIP = retDoc2M0403[0][1].trim();
      stringPrinterPosition = retDoc2M0403[0][2].trim();
      stringPrinterLabel = retDoc2M0403[0][3].trim();
      if (stringPrinterIP.length() == 0) {
        return "�C����]�w���~�A�гq����T�ǳB�z4%-%ERROR";
      }
      if (stringPrinterPosition.length() == 0) {
        return "�C����]�w���~�A�гq����T�ǳB�z5%-%ERROR";
      }
      if (stringPrinterLabel.length() == 0) {
        return "�C����]�w���~�A�гq����T�ǳB�z6%-%ERROR";
      }
      stringDotPrinter = "//" + stringPrinterIP + "/" + stringPrinterLabel;
      //
      if (!exeUtil.isPrinterExist(stringDotPrinter)) {
        Vector vectorPrinter = exeUtil.getPrinterPosition("/");
        String stringPrinterL = "";
        for (int intNo = 0; intNo < vectorPrinter.size(); intNo++) {
          stringPrinterL = ("" + vectorPrinter.get(intNo)).trim();
          if (stringPrinterL.equalsIgnoreCase(stringDotPrinter)) stringDotPrinter = stringPrinterL;
        }
        if (!exeUtil.isPrinterExist(stringDotPrinter)) {
          return "[" + stringDotPrinter + "]�C������s�b�A�Ь���T�Ǧw�ˡC%-%ERROR";
        }
      }
      return stringDotPrinter + "%-%���ʥӽЮ�-�C�L-�@��(Doc3M010)%-%" + stringPrinterPosition;
    }

    // �ª�
    // �� �קO ��ñ���w�\��A�i�ӿ�ܦC���
    // �s�i�]�w�w���ϥΦ��ؤ覡
    Vector vectorProject = new Vector();
    String stringProject = arrayText[0].trim();
    if (stringProject.length() == 0) {
      return "�C����]�w���~�A�гq����T�ǳB�z7%-%ERROR";
    }
    if (arrayText.length >= 2 && vectorProject.indexOf(arrayText[1].trim() + stringProject) != -1) {
      stringProject = arrayText[1].trim() + stringProject;
    }
    // �w�s�b���קO�W��
    vectorProject.add("Local");
    vectorProject.add("�H�`");
    //
    vectorProject.add("F1");
    vectorProject.add("H40");
    vectorProject.add("H45");
    vectorProject.add("H45_Local");
    vectorProject.add("H52");
    vectorProject.add("H52Local");
    vectorProject.add("H59A");
    vectorProject.add("H61");
    vectorProject.add("H63");
    vectorProject.add("H120");
    vectorProject.add("M");
    vectorProject.add("���ӥ�");
    vectorProject.add("�j�ǼC��");
    vectorProject.add("��h");
    vectorProject.add("��ڪ��ļs��");
    vectorProject.add("��ڪ��ļs��Local");
    //
    if (vectorProject.indexOf(stringProject) == -1) {
      return "�C����]�w���~�A�гq����T�ǳB�z8%-%ERROR";
    }
    stringFunction = "���ʥӽЮ�-�C�L-" + stringProject + "(Doc3M010)";
    return "%-%" + stringFunction + "%-%";
  }

  public String isB34ControlPrint(FargloryUtil exeUtil, Doc2M010 exeFun, Vector vectorProjectID1) throws Throwable {
    String stringUser = getUser().toUpperCase();
    String stringDepartNoSubjectN = "" + get("EMP_DEPT_CD_N"); //
    // �S�w�H���~�@�B�z
    if ("B3018,".indexOf(stringUser) == -1) return "";
    // �ȱ�����~��
    if (!"B34A".equals(stringDepartNoSubjectN)) return "";
    //
    String stringDocNo = getValue("DocNo1").trim() + "-" + getValue("DocNo2").trim() + "-" + getValue("DocNo3").trim();
    String stringEDateTime = getValue("EDateTime").trim();
    String stringLaserPrinter = "";
    //
    if (!isCanPrint(exeUtil, vectorProjectID1)) return "ERROR";
    //
    if (vectorProjectID1.size() > 0) {
      stringLaserPrinter = getLaserPrinter(exeUtil, exeFun);
      if (stringLaserPrinter.endsWith("ERROR")) {
        messagebox(convert.StringToken(stringLaserPrinter, "%-%")[0]);
        return "ERROR";
      }
      put("Doc5R017_DocNo", stringDocNo);
      put("Doc5R017_BarCode", getValue("BarCode").trim());
      put("Doc5R017_EDateTime", stringEDateTime);
    }
    return stringLaserPrinter;
  }

  public String getLaserPrinter(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    String stringFilePathL = "c:\\Emaker_Print\\DocPrint2.ini";
    String stringPrinterL = "";
    String stringLaserPrinter = "";
    String[] arrayTextL = null;
    if ((new File(stringFilePathL)).exists()) {
      System.out.println("c:\\Emaker_Print\\DocPrint2.ini �s�b---------------------------------------");
      arrayTextL = exeUtil.getArrayDataFromText(stringFilePathL);
      if (arrayTextL.length != 0) {
        stringPrinterL = arrayTextL[0].trim();
      }
      if (!"".equals(stringPrinterL)) {
        // �C����s�b�ˬd
        if (!exeUtil.isPrinterExist(stringPrinterL, "\\")) {
          Vector vectorPrinter = exeUtil.getPrinterPosition("\\");
          String stringPrinterL2 = "";
          boolean booleanFlag = false;
          for (int intNo = 0; intNo < vectorPrinter.size(); intNo++) {
            stringPrinterL2 = ("" + vectorPrinter.get(intNo)).trim();
            if (stringPrinterL2.equalsIgnoreCase(stringPrinterL)) {
              stringPrinterL = stringPrinterL2;
              booleanFlag = true;
              break;
            }
          }
          if (!booleanFlag) {
            return "[" + stringPrinterL + "]�p�g�C������s�b�A�Цw�˫�A�A�~��i�� [��~�x���C�L]�C%-%ERROR";
          }
        }
      }
    }
    System.out.println("stringPrinterL(" + stringPrinterL + ")---------------------------------------1");
    if (!"".equals(stringPrinterL)) {
      stringLaserPrinter = stringPrinterL;
    } else {
      stringLaserPrinter = exeUtil.getDefaultPrinter();
      System.out.println("stringLaserPrinter(" + stringLaserPrinter + ")---------------------------------------2");
    }
    System.out.println("stringLaserPrinter(" + stringLaserPrinter + ")---------------------------------------3");
    if (stringLaserPrinter.indexOf("�ӽЮ�") != -1) {
      return "�p�g�C��� ���I�}���C����A�Э��s�]�w�C%-%ERROR";
    }
    String stringPrinter = "";
    String[][] retDoc2M0403 = exeFun.getDoc2M0403("");
    // 0 PrinterName 1 IP 2 ReMark 3 PrinterLabel
    for (int intNo = 0; intNo < retDoc2M0403.length; intNo++) {
      stringPrinter = retDoc2M0403[intNo][3].trim();
      if (!"".equals(stringPrinter) && stringLaserPrinter.indexOf(stringPrinter) != -1) {
        return "�p�g�C��� ���I�}���C����A�Э��s�]�w�C%-%ERROR";
      }
    }
    return stringLaserPrinter;
  }

  public void doB34Print(String stringDoc5R017PrinterIP, Vector vectorProjectID1) throws Throwable {
    if (vectorProjectID1.size() == 0) return;
    //
    String stringFunctionL = "��~�ޱ��C�L(Doc5R017)";
    String stringProjectID1 = "";
    //
    put("Doc5R017_PRINT_PrinterNAME", stringDoc5R017PrinterIP);
    for (int intNo = 0; intNo < vectorProjectID1.size(); intNo++) {
      stringProjectID1 = "" + vectorProjectID1.get(intNo);
      //
      put("Doc5R017_ProjectID1", stringProjectID1);
      //
      showForm(stringFunctionL);
      if ("B3018,b4177,".indexOf(getUser()) == -1) getInternalFrame(stringFunctionL).setVisible(false);
    }
  }

  public boolean isCanPrint(FargloryUtil exeUtil, Vector vectorProjectID1) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    JTable jtable = getTable("Table3");
    String stringProjectID1 = "";
    String stringCostID = "";
    String stringRealMoney = "";
    String stringCostIDCF = "D0101,D0201,D0301," + // �]�p�ʥ�
        "D0102,D0202,D0302," + // ��y�ʥ�
        "D0103,D0203,D0303," + // ��P�ʥ�
        "D0104,D0204,D0304," + // ��~�ʥ�
        "D0105,D0205,D0305," + // ��Ӹ��v
        "D0401,D0402,D0403,D0404,D0405,"; // �M�׶O��
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      stringProjectID1 = ("" + getValueAt("Table3", intNo, "ProjectID1")).trim();
      stringCostID = ("" + getValueAt("Table3", intNo, "CostID")).trim();
      stringCostID = exeUtil.doSubstring(stringCostID, 0, 5);
      //
      if (vectorProjectID1.indexOf(stringProjectID1) != -1) continue;
      //
      // �дڥN�X �s�b �ˮ�
      if (stringCostIDCF.indexOf(stringCostID) == -1) continue;
      // �קO�s�b Doc5R017 �ˮ�
      stringRealMoney = exeUtil.getNameUnion("RealMoney", "Doc5M017", " AND  ProjectID1  =  '" + stringProjectID1 + "' ", new Hashtable(), dbDoc);
      if ("".equals(stringRealMoney)) {
        messagebox("[�קO] �|���إߺޱ���ơA�����\�C�L�C ");
        return false;
      }
      vectorProjectID1.add(stringProjectID1);
    }
    return true;
  }

  public boolean isCanPrint10(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    JTable jtable = getTable("Table3");
    String stringComNo = getValue("ComNo").trim();
    String stringProjectID1 = "";
    String stringBudgetID = "";
    String stringCostID = "";
    String stringCostID1 = "";
    String stringEDateTime = getValue("EDateTime").trim();
    String stringSqlAnd = "";
    String stringFlow = getFunctionName();
    Vector vectorProjectID1 = new Vector();
    Vector vectorBudgetCd = new Vector();
    Hashtable hashtableAnd = new Hashtable();
    //
    if (!"".equals(getUser())) return true;
    if (stringFlow.indexOf("��P") == -1) return true;
    if (stringFlow.indexOf("�g��") == -1) return true;
    //
    vectorBudgetCd.add("BC31"); // �l�q
    vectorBudgetCd.add("BC32"); // ���q
    vectorBudgetCd.add("BC33"); // ��L�ҰȶO
    vectorBudgetCd.add("BC35"); // ����
    vectorBudgetCd.add("BC36"); // ���ݤ��߯���
    vectorBudgetCd.add("BD46"); // ��d����O
    for (int intNo = 0; intNo < jtable.getRowCount(); intNo++) {
      stringProjectID1 = ("" + getValueAt("Table3", intNo, "ProjectID1")).trim();
      stringCostID = ("" + getValueAt("Table3", intNo, "CostID")).trim();
      stringCostID1 = ("" + getValueAt("Table3", intNo, "CostID1")).trim();
      stringBudgetID = getBudgetID(stringComNo, "", stringCostID, stringCostID1, exeFun);
      //
      if (!stringBudgetID.startsWith("B")) continue;
      if (vectorProjectID1.indexOf(stringProjectID1) != -1) continue;
      if (vectorBudgetCd.indexOf(stringBudgetID) != -1) continue;
      //
      vectorProjectID1.add(stringProjectID1);
      // �O�_����
      hashtableAnd.put("ComNo", stringComNo);
      hashtableAnd.put("ProjectID1", stringProjectID1);
      hashtableAnd.put("Contr_STATUS", "B");
      System.out.println("���q 80 %����-------------------------------------------�O�_����");
      if (exeUtil.getQueryDataHashtable("Doc7M02681", hashtableAnd, "", new Vector(), dbDoc).size() == 0) continue;
      // �O�_�b���ޤ����
      hashtableAnd.put("ComNo", stringComNo);
      hashtableAnd.put("ProjectID1", stringProjectID1);
      stringSqlAnd = " AND  TimeStart  <=  '" + stringEDateTime + "' " + " AND  (ISNULL(TimeEnd,  '')  =  ''  OR  TimeEnd  >=  '" + stringEDateTime + "') ";
      System.out.println("���q 80 %����-------------------------------------------�O�_�b���ޤ����");
      if (exeUtil.getQueryDataHashtable("Doc7M02683", hashtableAnd, stringSqlAnd, new Vector(), dbDoc).size() == 0) continue;
      // �D�ҥ~���ޥӽг�
      hashtableAnd.put("ComNo", stringComNo);
      hashtableAnd.put("ProjectID1", stringProjectID1);
      hashtableAnd.put("IDExcept", getValue("ID"));
      hashtableAnd.put("DocNoType", "A");
      System.out.println("���q 80 %����-------------------------------------------�O�_�ҥ~���ޥӽг�");
      if (exeUtil.getQueryDataHashtable("Doc7M02682", hashtableAnd, "", new Vector(), dbDoc).size() == 0) {
        messagebox("�~�D�O-�קO(" + stringProjectID1 + ")�w��W�X80%�wĵ�A�ݥ����D�޽T�{��~�i�C�L+�e�e�C");
        return false;
      }
    }
    return true;
  }

  public String getBudgetID(String stringComNo, String stringBudgetID, String stringCostID, String stringCostID1, Doc2M010 exeFun) throws Throwable {
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc7M011 = null;
    // 0 BudgetID 1 CostID 2 CostID1 3 Description
    stringSql = "SELECT  BudgetID,  CostID,  CostID1,  Description " + " FROM  Doc2M020 " + " WHERE  ComNo  =  '" + stringComNo + "' " + " AND  BudgetID  <>  '' "
        + " AND  NOT(BudgetID  IS  NULL) " + " AND  UseStatus  =  'Y' ";
    if (!"".equals(stringBudgetID)) stringSql += " AND  BudgetID  =  '" + stringBudgetID + "' ";
    if (!"".equals(stringCostID)) stringSql += " AND  CostID  =  '" + stringCostID + "' ";
    if (!"".equals(stringCostID1)) stringSql += " AND  CostID1  =  '" + stringCostID1 + "' ";
    stringSql += " ORDER BY BudgetID,  CostID,  CostID1 ";
    retDoc7M011 = exeFun.getTableDataDoc(stringSql);
    if (retDoc7M011.length > 0) return retDoc7M011[0][0].trim();
    return "";
  }

  public String getInformation() {
    return "---------------�C�L���s�{��.preProcess()----------------";
  }
}
