package Doc.Doc6;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import Farglory.util.FargloryUtil;
import java.net.*;
import Doc.Doc2M010;

public class Doc6M010FrontPrint extends bTransaction {
  public boolean action(String value) throws Throwable {
    // �^�ǭȬ� true ��ܰ��汵�U�Ӫ���Ʈw���ʩάd��
    // �^�ǭȬ� false ��ܱ��U�Ӥ����������O
    // �ǤJ�� value �� "�s�W","�d��","�ק�","�R��","�C�L","PRINT" (�C�L�w�����C�L���s),"PRINTALL"
    // (�C�L�w���������C�L���s) �䤤���@
    Doc2M010 exeFun = new Doc2M010();
    FargloryUtil exeUtil = new FargloryUtil();
    String stringBarCode = getValue("BarCode").trim();
    String stringPrintable = getValue("Printable").trim();
    if ("null".equals(stringPrintable)) stringPrintable = "Y";
    String stringToday = datetime.getToday("YYYY/mm/dd");
    String stringLaserPrinter = "";
    String stringPrinterPosition = "";
    String stringFunctionName = getFunctionName();
    String stringDotPrinter = "";
    String stringUser = getUser();
    String stringFunction = "";
    String[] arrayTemp = null;
    boolean booleanDoc10 = stringFunctionName.indexOf("�g�a") != -1;
    boolean booleanLaser = booleanDoc10;
    //
    // if("2017/07/03".compareTo(stringToday)<=0)
    booleanLaser = true;
    // �R��ɴ� �p�g�C�L
    if (!booleanLaser) {
      // String stringToday = datetime.getToday("YYYY/mm/dd") ;
      // if("2016/02/15".compareTo(stringToday) < 0) {
      if (stringFunctionName.indexOf("�޲z") != -1) booleanLaser = true;
      /*
       * } else { // 2015-1/-04 �]�ȫǤ��\�q�g�C�L String[][] retFE3D05 =
       * exeFun.getFE3D05(stringUser) ; if(retFE3D05.length>0 &&
       * retFE3D05[0][0].startsWith("022")) { booleanLaser = true ; } }
       */
    }
    /*
     * if(booleanLaser && stringFunctionName.indexOf("�޲z") != -1) { String
     * stringPrintType = exeFun.getNameUnionDoc("PrintType", "Doc5M030",
     * " AND  BarCode  =  '"+stringBarCode+"' ", new Hashtable(), exeUtil) ;
     * if(!"A".equals(stringPrintType)) booleanLaser = false ; }
     */
    //
    if (!isBatchCheckOK()) return false;
    //
    System.out.println("booleanLaser(" + booleanLaser + ")-------------------------");
    if (booleanLaser) {
      stringFunction = booleanDoc10 ? "�g�a�}�o�ɴڥӽЮ�-�C�L(Doc10R00101)" : "�ɴڥӽЮ�-�C�L(Doc6R011)"; // �p�g
      if (booleanDoc10) {
        stringFunction = "�g�a�}�o�ɴڥӽЮ�-�C�L(Doc10R00101)"; // �p�g
        // if("2017/07/03".compareTo(stringToday)<=0) {
        stringFunction = "�g�a�}�o�ɴڥӽЮ�-A4�C�L(Doc10R00101)";
        // }
      } else {
        stringFunction = "�ɴڥӽЮ�-�C�L(Doc6R011)"; // �p�g
        // if(!"B3018".equals(getUser())) {
        stringFunction = "�ɴڥӽЮ�-A4�C�L(Doc6R012)";
        // }
      }
      //
      stringLaserPrinter = exeUtil.getDefaultPrinter();
      if (stringLaserPrinter.startsWith("PS-") || stringLaserPrinter.indexOf("�ӽЮ�") != -1) {
        messagebox("�w�]�C������~1");
        return false;
      }
      String stringPrinter = "";
      String[][] retDoc2M0403 = exeFun.getDoc2M0403("");
      // 0 PrinterName 1 IP 2 ReMark 3 PrinterLabel
      for (int intNo = 0; intNo < retDoc2M0403.length; intNo++) {
        stringPrinter = retDoc2M0403[intNo][3].trim().toUpperCase();
        if (!"".equals(stringPrinter) && stringLaserPrinter.toUpperCase().indexOf(stringPrinter) != -1) {
          messagebox("���C��� ���I�}���C����A�Э��s�]�w�C");
          return false;
        }
      }
      put("Doc2M010_PRINT_PrinterNAME", stringLaserPrinter);
    } else {
      stringFunction = "�дڥӽЮ�-�C�L-Local(Doc2M010)";
      stringDotPrinter = getDotPrinter(exeUtil, exeFun);
      if (stringDotPrinter.endsWith("ERROR")) {
        messagebox(convert.StringToken(stringDotPrinter, "%-%")[0]);
        return false;
      }
      System.out.println("(" + stringDotPrinter + ")-------------------------");
      arrayTemp = convert.StringToken(stringDotPrinter, "%-%");
      stringDotPrinter = convert.replace(arrayTemp[0], "/", "\\");
      stringPrinterPosition = arrayTemp[1];
      System.out.println("���o �I�}���C���(" + stringDotPrinter + ")(" + stringPrinterPosition + ")---------------------------------------");
      put("Doc2M010_PRINT_PrinterNAME", stringDotPrinter);
    }
    put("Doc2M010_PRINT_Enable", stringPrintable);
    put("Doc2M010_PRINT_BarCode", stringBarCode);
    put("Doc2M010_PRINT_Function", stringFunctionName);
    put("Doc2M010_FUNCTION", "C");
    //
    System.out.println("stringPrintable(" + stringPrintable + ")--------------------------------" + stringFunction);
    showForm(stringFunction);
    
    //21.06.21 KYLE : �w�b �ɴڥӽЮ�-A4�C�L(Doc6R012) ���B�z�A���B�h�l
//    if ("B3018,".indexOf(stringUser) == -1 && "Y".equals(stringPrintable)) {
//      getInternalFrame(stringFunction).setVisible(false);
//    }
    
    // ���\�T��
    String stringPrintSuccess = ("" + get("Doc2M010_Print_OK")).trim();
    if ("OK".equals(stringPrintSuccess) && "Y".equals(stringPrintable)) {
      String stringTemp = "�����C�L�C";
      if (booleanLaser) stringTemp += "\n(���� [�I�}���C�L]�A�אּ [�p�g�C�L])";
      
      //��20210601006 Kyle : �]�̱��אּ��ʦC�L�A�q���T���ק�
//      stringTemp = "�п�ܦC�L�覡~";
//      messagebox(stringTemp);
    }
    System.out.println("--------------------------------�C�L����" + stringFunction);
    put("Doc2M010_Print_OK", "null");
    return false;
  }

  public boolean isBatchCheckOK() throws Throwable {
    String stringBarCode = getValue("BarCode").trim();
    if ("".equals(stringBarCode)) return false;
    //
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    String stringEmployeeNo = getValue("EmployeeNo").trim();
    String stringUser = getUser().toUpperCase();
    if (!stringDepartNoSubject.startsWith("033") && !stringEmployeeNo.equals(stringUser.toUpperCase()) && ",B1721,B3018,".indexOf(stringUser) == -1) {
      messagebox("�D��J�H���A���i�C�L�C");
      return false;
    }
    return true;
  }

  public String getDotPrinter(FargloryUtil exeUtil, Doc2M010 exeFun) throws Throwable {
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
      return stringDotPrinter + "%-%";
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
      String[][] retDoc2M0403 = exeFun.getDoc2M0403(arrayText[0].trim());
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
      return stringDotPrinter + "%-%" + stringPrinterPosition;
    }
    return "%-%";
  }

  public String getInformation() {
    return "---------------�C�L���s�{��.preProcess()----------------";
  }
}
