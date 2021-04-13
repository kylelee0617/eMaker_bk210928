package Sale.Sale05M093.copy;

import javax.swing.*;
import jcx.jform.bproc;
import cLabel;
import jcx.jform.bNotify;
import jcx.jform.bBase;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AML extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    // 2019/12/28 �~�� ���ަW��LOG
    System.out.println("==============�~�����v�ˮ�LOG START====================================");
    talk dbSale05M = getTalk("Sale");
    talk dbJGENLIB = getTalk("JGENLIB");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    String stringSQL = "";
    String strBDaysql = "";
    String errMsg = "";
    String[][] retPat001;
    String[][] retPat002;
    String[][] retPat003;
    String[][] retPat004;
    String[][] retQueryLog;
    String[][] tb2_string = getTableData("table2");
    String[][] tb5_string = getTableData("table5");
    String[][] tb6_string = getTableData("table6");
    String strOrderNo = getValue("OrderNo").trim();
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringOrderDate = getValue("TrxDate").trim();
    String strActionName = "�s��";
    System.out.println("strOrderNo=====>" + strOrderNo);
    System.out.println("stringProjectID1=====>" + stringProjectID1);
    System.out.println("stringOrderDate=====>" + stringOrderDate);
    System.out.println("strActionName=====>" + strActionName);
    // LOG���,�ɶ�
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    String strNowDate = nowsdf.format(now);
    String tempROCYear = "" + (Integer.parseInt(strNowDate.substring(0, strNowDate.length() - 4)) - 1911);
    String RocNowDate = tempROCYear + strNowDate.substring(strNowDate.length() - 4, strNowDate.length());
    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HHmmss");
    String strNowTime = nowTimeSdf.format(now);
    SimpleDateFormat nowTimestampSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String strNowTimestamp = nowTimestampSdf.format(now);
    System.out.println("strNowTimestamp=====>" + strNowTimestamp);
    System.out.println("RocNowDate=====>" + RocNowDate);
    System.out.println("strNowTime=====>" + strNowTime);
    // ���u�X
    String userNo = getUser().toUpperCase().trim();
    String empNo = "";
    String[][] retEip = null;
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }
    System.out.println("empNo=====>" + empNo);
    // �Ǹ�
    int intRecordNo = 1;
    String[][] ret05M070;
    stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + getValue("OrderNo").trim() + "'";
    ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
    // actionNo
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    String strActionNo = strNowDate + strNowTime + ram;
    System.out.println("strActionNo=====>" + strActionNo);
    System.out.println("==============�Ȥ���==============");
    // stringSQL = "SELECT
    // CountryName,CustomNo,CustomName,IsBlackList,IsControlList,IsLinked,Birthday
    // FROM Sale05M091 WHERE ORDERNO = '"+strOrderNo+"'";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (tb2_string.length > 0) {
      for (int m = 0; m < tb2_string.length; m++) {// �P�@�q��U
        String strCountryName = tb2_string[m][5].trim();
        String strCustomNo = tb2_string[m][6].trim();
        String strCustomName = tb2_string[m][7].trim();
        String strIsBlackList = tb2_string[m][20].trim();
        String strIsControlList = tb2_string[m][21].trim();
        String strIsLinked = tb2_string[m][22].trim();
        String strBirthday = tb2_string[m][9].trim();

        // ���A��LOG1~8
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','�P�@�Ȥ�P�@��~�餺2��(�t)�H�W�]�t�{���B�״ڡB�H�Υd�B�䲼����A�B�C���Ҥ���s�x��450,000~499,999���A�t���ˮֹwĵ�C','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','�P�@�Ȥ�3����~�餺�A��2��H�{���ζ״ڹF450,000~499,999��, �t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','�P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','�P�@�Ȥ�3����~�餺�A�֭pú��{���W�L50�U��, �t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 5
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','005','�Nú�ڤH�P�ʶR�H���Y���D�G���ˤ���/�ÿˡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','�P�@�Ȥᤣ�ʲ��R��Añ���e�h�q�����ʶR�A���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','�P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 8
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','008','���ʲ��P��ѲĤT��N�z��ú�ڡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // ����W��
        talk db400 = getTalk("400CRM");
        String str400sql = "";
        System.out.println("strBirthday=====>" + strBirthday);
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        } else {
          int x = strBirthday.indexOf("/");
          if (x > -1) {
            strBirthday = strBirthday.replace("/", "-");
          } else {
            String yyyy = strBirthday.substring(0, 4);
            String mm = strBirthday.substring(4, 6);
            String dd = strBirthday.substring(6, 8);

            strBirthday = yyyy + "-" + mm + "-" + dd;
          }
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + strBirthday
              + "' )";
        }
        String retCList[][] = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','018','�Ȥ�" + strCustomName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '018', '�ӫȤᬰ���ަW���H������W��A�T�����ýШ̨���~�����q���@�~�|��k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�Ȥ�" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          } else {
            errMsg = errMsg + "\n�Ȥ�" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','018','�Ȥ�" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // X171
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        } else {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + strBirthday
              + "' )";
        }
        String ret171List[][] = db400.queryFromPool(str400sql);
        if (ret171List.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
              + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','021','�Ȥ�" + strCustomName
              + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '021', '�Ȥ�Ψ���q�H�B�a�x�����Φ��K�����Y���H�A���{���B�����ꤺ�~�F���ΰ�ڲ�´���n�F�v��¾�ȡA�Х[�j�Ȥ��¾�լd�A�Ш̬~������@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�Ȥ�" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
          } else {
            errMsg = errMsg + "\n�Ȥ�" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','021','�Ȥ�" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // �ꮣ�a��
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("�u���k��".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','�Ȥ�" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + getValue("OrderNo").trim() + "', '"
                + RocNowDate + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '�Ȥ�Y�ӦۥD�޾����Ҥ��i����~���P�����ꮣ���Y���ʥ�����a�Φa�ϡA�Ψ�L����`�Υ��R����`����a�Φa�ϡA���ˮ֨�X�z�ʡC','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            if ("".equals(errMsg)) {
              errMsg = "�Ȥ�" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            } else {
              errMsg = errMsg + "\n�Ȥ�" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            }
          } else {
            // ���ŦX
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','�Ȥ�" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }
        // �¦W��
        // ���ަW��
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','�Ȥ�" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '�ӫȤᬰ�æ��¦W���H�A�а���[�j���Ȥ��¾�f�d�ę̀���~�������q���@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�Ȥ�" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          } else {
            errMsg = errMsg + "\n�Ȥ�" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','�Ȥ�" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // �Q�`���Y�H
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','�Ȥ�" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '�ӫȤᬰ���q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�Ȥ�" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          } else {
            errMsg = errMsg + "\n�Ȥ�" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','�Ȥ�" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        //// ���A��LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','�ۥD�޾����Ҥ��i����~���P������U���ƥ��l���Y���ʥ�����a�Φa�ϡB�Ψ�L����`�Υ��R����`����a�Φa�϶פJ������ڶ��A���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','����̲ר��q�H�Υ���H�����ĺʷ��޲z�e���|����~��F���Ҵ��Ѥ����Ƥ��l�ι���F�ΰ�ڬ~�������´�{�w�ΰl�d�����Ʋ�´�F�Υ������æ��Φ��X�z�z���h�ûP���Ƭ��ʡB���Ʋ�´�θ�U���ƥD�q�����p�̡A���̸ꮣ����k�i������@�~�C','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','�Ȥ�n�D�N���ʲ��v�Q�n�O���ĤT�H�A���ണ�X�������p�Ωڵ����������`���p�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','�Ȥ��I���ʲ�������ڶ��A�H�{�r��I�q���H�~�U�����ڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','�Ȥ��ñ���e���e�I�M�۳ƴڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','�n�D���q�}�ߨ����T��I�������䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','�n�D���q�}�ߺM�P����u(�������u)�䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
      } // for end
    }
    System.out.println("============================");
    System.out.println("==============�����q�H==============");
    // stringSQL = "SELECT
    // CountryName,BCustomNo,BenName,IsBlackList,IsControlList,IsLinked,Birthday
    // FROM Sale05M091BEN WHERE ORDERNO = '"+strOrderNo+"'";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (tb5_string.length > 0) {// �����q�H START
      for (int m = 0; m < tb5_string.length; m++) {// �P�@�q��U
        String strCountryName = tb5_string[m][6].trim();
        String strCustomNo = tb5_string[m][4].trim();
        String strCustomName = tb5_string[m][3].trim();
        String strIsBlackList = tb5_string[m][8].trim();
        String strIsControlList = tb5_string[m][9].trim();
        String strIsLinked = tb5_string[m][10].trim();
        String strBirthday = tb5_string[m][5].trim();

        // ���A��LOG1~8
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','�P�@�Ȥ�P�@��~�餺2��(�t)�H�W�]�t�{���B�״ڡB�H�Υd�B�䲼����A�B�C���Ҥ���s�x��450,000~499,999���A�t���ˮֹwĵ�C','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','�P�@�Ȥ�3����~�餺�A��2��H�{���ζ״ڹF450,000~499,999��, �t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','�P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','�P�@�Ȥ�3����~�餺�A�֭pú��{���W�L50�U��, �t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 5
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','005','�Nú�ڤH�P�ʶR�H���Y���D�G���ˤ���/�ÿˡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','�P�@�Ȥᤣ�ʲ��R��Añ���e�h�q�����ʶR�A���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','�P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 8
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','008','���ʲ��P��ѲĤT��N�z��ú�ڡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // ����W��
        talk db400 = getTalk("400CRM");
        String str400sql = "";
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
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
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + strBirthday
              + "' )";
        }
        String retCList[][] = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','018','�����q�H" + strCustomName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '018', '�ӫȤᬰ���ަW���H������W��A�T�����ýШ̨���~�����q���@�~�|��k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�����q�H" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          } else {
            errMsg = errMsg + "\n�����q�H" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','018','�����q�H" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // ����W��X171
        if ("".equals(strBirthday)) {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND CUSTOMERNAME='" + strCustomName + "'";
        } else {
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
              + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + strBirthday
              + "' )";
        }
        String ret171List[][] = db400.queryFromPool(str400sql);
        if (ret171List.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
              + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','021','�����q�H" + strCustomName
              + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '021', '�Ȥ�Ψ���q�H�B�a�x�����Φ��K�����Y���H�A���{���B�����ꤺ�~�F���ΰ�ڲ�´���n�F�v��¾�ȡA�Х[�j�Ȥ��¾�լd�A�Ш̬~������@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�����q�H" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
          } else {
            errMsg = errMsg + "\n�����q�H" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','021','�����q�H" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // �ꮣ�a��
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("�u���k��".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','�����q�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '�Ȥ�Y�ӦۥD�޾����Ҥ��i����~���P�����ꮣ���Y���ʥ�����a�Φa�ϡA�Ψ�L����`�Υ��R����`����a�Φa�ϡA���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate
                + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            if ("".equals(errMsg)) {
              errMsg = "�����q�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            } else {
              errMsg = errMsg + "\n�����q�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            }
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','009','�����q�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // �¦W��
        // ���ަW��
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','�����q�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '�ӫȤᬰ�æ��¦W���H�A�а���[�j���Ȥ��¾�f�d�ę̀���~�������q���@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�����q�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          } else {
            errMsg = errMsg + "\n�����q�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','�����q�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // �Q�`���Y�H
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','�����q�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '�ӫȤᬰ���q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�����q�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          } else {
            errMsg = errMsg + "\n�����q�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','�����q�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        //// ���A��LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','�ۥD�޾����Ҥ��i����~���P������U���ƥ��l���Y���ʥ�����a�Φa�ϡB�Ψ�L����`�Υ��R����`����a�Φa�϶פJ������ڶ��A���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','����̲ר��q�H�Υ���H�����ĺʷ��޲z�e���|����~��F���Ҵ��Ѥ����Ƥ��l�ι���F�ΰ�ڬ~�������´�{�w�ΰl�d�����Ʋ�´�F�Υ������æ��Φ��X�z�z���h�ûP���Ƭ��ʡB���Ʋ�´�θ�U���ƥD�q�����p�̡A���̸ꮣ����k�i������@�~�C','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','�Ȥ�n�D�N���ʲ��v�Q�n�O���ĤT�H�A���ണ�X�������p�Ωڵ����������`���p�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','�Ȥ��I���ʲ�������ڶ��A�H�{�r��I�q���H�~�U�����ڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','�Ȥ��ñ���e���e�I�M�۳ƴڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','�n�D���q�}�ߨ����T��I�������䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','�n�D���q�}�ߺM�P����u(�������u)�䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
      } // for end
    } // �����q�H END
    System.out.println("============================");
    
    
    System.out.println("==============�N�z�H==============");
    // stringSQL = "SELECT
    // CountryName,ACustomNo,AgentName,IsBlackList,IsControlList,IsLinked, AgentRel
    // FROM Sale05M091Agent WHERE ORDERNO = '"+strOrderNo+"'";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (tb6_string.length > 0) {// �N�z�H START
      for (int m = 0; m < tb6_string.length; m++) {// �P�@�q��U
        String strCountryName = tb6_string[m][5].trim();
        String strCustomNo = tb6_string[m][4].trim();
        String strOrderCustomName = tb6_string[m][2].trim();
        String strCustomName = tb6_string[m][3].trim();
        String strIsBlackList = tb6_string[m][8].trim();
        String strIsControlList = tb6_string[m][9].trim();
        String strIsLinked = tb6_string[m][10].trim();
        String strAgentRel = tb6_string[m][6].trim();
        
        String custSA = "�Ȥ�" + strOrderCustomName + "��";
        if (m == 0) errMsg += "\n";

        // ���A��LOG1~4,6,7
        // 1
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','001','�P�@�Ȥ�P�@��~�餺2��(�t)�H�W�]�t�{���B�״ڡB�H�Υd�B�䲼����A�B�C���Ҥ���s�x��450,000~499,999���A�t���ˮֹwĵ�C','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 2
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','002','�P�@�Ȥ�3����~�餺�A��2��H�{���ζ״ڹF450,000~499,999��, �t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 3
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','003','�P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 4
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','004','�P�@�Ȥ�3����~�餺�A�֭pú��{���W�L50�U��, �t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 6
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','006','�P�@�Ȥᤣ�ʲ��R��Añ���e�h�q�����ʶR�A���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 7
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','007','�P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

        // ����W��
        talk db400 = getTalk("400CRM");
        // Query_Log ���ͤ�
        String strPW0Dsql = "SELECT BIRTHDAY FROM QUERY_LOG WHERE PROJECT_ID = '" + stringProjectID1 + "' AND QUERY_ID = '" + strCustomNo + "' AND NAME = '" + strCustomName + "'";
        retQueryLog = dbPW0D.queryFromPool(strPW0Dsql);
        if (retQueryLog.length > 0) {
          strBDaysql = "AND ( CUSTOMERNAME='" + strCustomName + "' AND BIRTHDAY = '" + retQueryLog[0][0].trim().replace("/", "-") + "' )";
        } else {
          strBDaysql = "AND CUSTOMERNAME='" + strCustomName + "'";
        }
        System.out.println("strBDaysql====>" + strBDaysql);
        // AS400
        String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' " + strBDaysql;
        String retCList[][] = db400.queryFromPool(str400sql);
        if (retCList.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','018','�N�z�H" + strCustomName
              + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '018', '�ӫȤᬰ���ަW���H������W��A�T�����ýШ̨���~�����q���@�~�|��k��ǡC','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          
          errMsg += custSA + "�N�z�H" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC\n";
//          if ("".equals(errMsg)) {
//            errMsg = "�N�z�H" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
//          } else {
//            errMsg = errMsg + "\n�N�z�H" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
//          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','018','�N�z�H" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // AS400 X171
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + strCustomNo + "' " + strBDaysql;
        String ret171List[][] = db400.queryFromPool(str400sql);
        if (ret171List.length > 0) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
              + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','021','�N�z�H" + strCustomName
              + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '021', '�Ȥ�Ψ���q�H�B�a�x�����Φ��K�����Y���H�A���{���B�����ꤺ�~�F���ΰ�ڲ�´���n�F�v��¾�ȡA�Х[�j�Ȥ��¾�լd�A�Ш̬~������@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbJGENLIB.execFromPool(stringSQL);
          
          errMsg += custSA + "�N�z�H" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C\n";
//          if ("".equals(errMsg)) {
//            errMsg = "�N�z�H" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
//          } else {
//            errMsg = errMsg + "\n�N�z�H" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
//          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','021','�N�z�H" + strCustomName + "�����n�F�v��¾�ȤH�h�P��a�x�����Φ��K�����Y���H�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // �~���ĤK��
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
            + "�N����z���ʲ�����A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','008','�N�z�H" + strCustomName
            + "�N����z���ʲ�����A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // AS400
        stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate + "', '"
            + strCustomNo + "', '" + strCustomName + "', '773', '008', '���ʲ��P��ѲĤT��N�z��ú�ڡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbJGENLIB.execFromPool(stringSQL);
        errMsg += custSA + "�N�z�H" + strCustomName + "�N����z�䤣�ʲ�����A�Ш̬~���θꮣ����@�~��z�C\n";
//        if ("".equals(errMsg)) {
//          errMsg = "�N�z�H" + strCustomName + "�N����z���ʲ�����A�Ш̬~���θꮣ����@�~��z�C";
//        } else {
//          errMsg = errMsg + "\n�N�z�H" + strCustomName + "�N����z���ʲ�����A�Ш̬~���θꮣ����@�~��z�C";
//        }
        
        // �ꮣ�a��
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          System.out.println("strCZ07==>" + strCZ07);
          if ("�u���k��".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','�N�z�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '�Ȥ�Y�ӦۥD�޾����Ҥ��i����~���P�����ꮣ���Y���ʥ�����a�Φa�ϡA�Ψ�L����`�Υ��R����`����a�Φa�ϡA���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate
                + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            errMsg += custSA + "�N�z�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�Ш̬~���θꮣ����@�~��z�C\n";
//            if ("".equals(errMsg)) {
//              errMsg = "�N�z�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
//            } else {
//              errMsg = errMsg + "\n�N�z�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
//            }
            
          } else {
            // ���ŦX
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','�N�z�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }
        
        // ���Y
        if (!"".equals(strAgentRel)) {
          if ("�B��".equals(strAgentRel) || "��L".equals(strAgentRel)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName + "�P�Ȥ�"
                + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','005','�N�z�H" + strCustomName
                + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '005', '�Nú�ڤH�P�ʶR�H���Y���D�G���ˤ���/�ÿˡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            errMsg += custSA + "�N�z�H" + strCustomName + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C\n";
//            if ("".equals(errMsg)) {
//              errMsg = "�N�z�H" + strCustomName + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C";
//            } else {
//              errMsg = errMsg + "\n�N�z�H" + strCustomName + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C";
//            }
          } else {
            // ���ŦX
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','005','�N�z�H" + strCustomName + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }
        
        // �¦W��
        // ���ަW��
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','�N�z�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '�ӫȤᬰ�æ��¦W���H�A�а���[�j���Ȥ��¾�f�d�ę̀���~�������q���@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "�N�z�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
//          if ("".equals(errMsg)) {
//            errMsg = "�N�z�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
//          } else {
//            errMsg = errMsg + "\n�N�z�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
//          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','�N�z�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // �Q�`���Y�H
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','�N�z�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '�ӫȤᬰ���q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "�N�z�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
//          if ("".equals(errMsg)) {
//            errMsg = "�N�z�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
//          } else {
//            errMsg = errMsg + "\n�N�z�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
//          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','�N�z�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        //// ���A��LOG10~16
        // 10
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','010','�ۥD�޾����Ҥ��i����~���P������U���ƥ��l���Y���ʥ�����a�Φa�ϡB�Ψ�L����`�Υ��R����`����a�Φa�϶פJ������ڶ��A���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 11
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate
            + "','RY','773','011','����̲ר��q�H�Υ���H�����ĺʷ��޲z�e���|����~��F���Ҵ��Ѥ����Ƥ��l�ι���F�ΰ�ڬ~�������´�{�w�ΰl�d�����Ʋ�´�F�Υ������æ��Φ��X�z�z���h�ûP���Ƭ��ʡB���Ʋ�´�θ�U���ƥD�q�����p�̡A���̸ꮣ����k�i������@�~�C','" + empNo + "','" + RocNowDate
            + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 12
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','012','�Ȥ�n�D�N���ʲ��v�Q�n�O���ĤT�H�A���ണ�X�������p�Ωڵ����������`���p�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 13
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','013','�Ȥ��I���ʲ�������ڶ��A�H�{�r��I�q���H�~�U�����ڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','"
            + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 14
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','014','�Ȥ��ñ���e���e�I�M�۳ƴڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 15
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','015','�n�D���q�}�ߨ����T��I�������䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // 16
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
            + strCustomName + "','" + stringOrderDate + "','RY','773','016','�n�D���q�}�ߺM�P����u(�������u)�䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
            + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;

      } // for end
    } // �N�z�H END
    System.out.println("============================");
    
    
    // �e�XerrMsg
    if (!"".equals(errMsg)) {
      setValue("errMsgBoxText", errMsg);
      getButton("errMsgBoxBtn").doClick();
      getButton("sendMail").doClick();
    }
    System.out.println("==============�~�����v�ˮ�LOG END====================================");
    return value;
  }

  public String getInformation() {
    return "---------------button6(AML).defaultValue()----------------";
  }
}
