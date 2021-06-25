package Sale.Sale05M093;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import javax.swing.*;
import Farglory.util.*;

public class ChgNameSave_bk210602 extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    System.out.println("chk==>" + getUser() + " , value==>����s��");
    if (getUser() != null && getUser().toUpperCase().equals("B9999")) {
      messagebox("����s���v�������\!!!");
      return value;
    }
    
    MLPUtils mlpUtils = new MLPUtils();
    talk dbSale = getTalk("Sale");
    int RecordNo = 0;
    String stringSQL = "";
    String stringCustomName = "";
    String stringCustomNo = "";
    String stringPercent = "";
    String stringPercentSum = "0";
    String stringOrderNo = getValue("OrderNo").trim();
    String stringAddress = "";
    String stringTel = "";
    String stringTrxDate = getValue("TrxDate").trim();
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringDocNo = "";
    String stringEDate = "";
    String stringChangeName = "";
    String[][] tb1_string = getTableData("table1");
    String[][] tb2_string = getTableData("table2");
    String[][] tb5_string = getTableData("table5");
    String[][] tb6_string = getTableData("table6");
    String[][] retSale05M080 = null;
    String[][] retSale05M086 = null;
    String[][] retSale05M087 = null;
    String[][] retSale05M091 = null;
    String[][] retSale05M091Ben = null;
    String[][] retSale05M091Agent = null;
    boolean flag1 = false;
    boolean flag2 = false;// �����q�H
    boolean flag3 = false;// �N�z�H
    Vector vectorSql = new Vector();
    Vector vectorCustomNo = new Vector();
    JTable jtable2 = getTable("table2");
    int intcount = 0;
    String paperNo = getValue("PaperNo") == null ? "" : getValue("PaperNo").trim();
    String majorID = getValue("MajorID") == null ? "" : getValue("MajorID").trim();
    String majorName = getValue("MajorName").trim();

    // 20200212 kyle >> key: customName , value: customNo
    Map tb2Map = new HashMap();

    // �ˮ�
    // ���W���
    if (stringTrxDate.length() == 0) {
      message("[���W���] ���i�ť�");
      return value;
    }
    stringTrxDate = convert.replace(stringTrxDate, "/", "");
    if (!check.isACDay(stringTrxDate)) {
      message("[���W���] �榡���~(YYYY/MM/DD)�C");
      return value;
    } else {
      setValue("TrxDate", convert.FormatedDate(stringTrxDate, "/"));
    }
    // ����p�󴫦W�������ƥ��}�ߵo���ɡA��ܰT���A�����ʧ@
    stringSQL = "SELECT  DocNo  FROM  Sale05M086  WHERE  OrderNo  =  '" + stringOrderNo + "' ";
    retSale05M086 = dbSale.queryFromPool(stringSQL);
    for (int intNo = 0; intNo < retSale05M086.length; intNo++) {
      stringDocNo = retSale05M086[intNo][0].trim();
      //
      System.out.println("");
      stringSQL = "SELECT  EDate  FROM  Sale05M080 WHERE  DocNo  =  '" + stringDocNo + "' ";
      retSale05M080 = dbSale.queryFromPool(stringSQL);
      if (retSale05M080.length == 0) {
        message("[�ϥνs��] ���s�b�� [���ڳ�(Sale05M080] ���C");
        return value;
      }
      //
      stringEDate = convert.replace(retSale05M080[0][0].trim(), "/", "");
      // System.out.println((intNo+1)+"--------------------"+stringEDate+"-----------------"+datetime.subDays1(stringTrxDate,
      // stringEDate)) ;
      if (datetime.subDays1(stringTrxDate, stringEDate) > 0) {
        // ����p�󴫦W����ɡA�P�_�O�_�}�ߵo��
        stringSQL = "SELECT  InvoiceNo  FROM  Sale05M087  WHERE  DocNo  =  '" + stringDocNo + "' ";
        retSale05M087 = dbSale.queryFromPool(stringSQL);
        if (retSale05M087.length == 0) {
          message("[���ڳ�s��] �� " + stringDocNo + " [���ڤ��] �� " + stringEDate + " ����ƥ��}����o���C");
          return value;
        }
      }
    }
    stringTrxDate = getValue("TrxDate").trim();

    // 20200620 Kyle : ����ˮ�PASS�A�إ�AML�ˮ֪���
    Map amlCons = new HashMap();
    amlCons.put("OrderNo", stringOrderNo);
    amlCons.put("ProjectID1", stringProjectID1);
    amlCons.put("TrxDate", stringTrxDate);
    amlCons.put("funcName", "���W");
    amlCons.put("ActionName", "�s��");
    AMLTools amlTools = new AMLTools(amlCons);

    // ���2
    talk dbDoc = getTalk("Doc");
    String stringNationality = "";
    String stringZIP = "";
    String stringCity = "";
    String stringTown = "";
    String stringAuditorship = ""; // 20090401 �W�[����
    // 20191224 �t�X�~�������s�s�W���
    String strCountryName = "";
    String strBirthday = "";
    String strMajorName = "";
    String strPositionName = "";
    String strCellphone = "";
    String strTel2 = "";
    String streMail = "";
    String strIsBlackList = "";
    String strIsControlList = "";
    String strIsLinked = "";
    String stringSql = "";
    String[][] retTown = null;
    for (int i = 0; i < tb2_string.length; i++) {
      stringChangeName = ("" + getValueAt("table2", i, "ChangeName")).trim();
      stringAuditorship = ("" + getValueAt("table2", i, "auditorship")).trim();
      stringNationality = ("" + getValueAt("table2", i, "Nationality")).trim();
      strCountryName = ("" + getValueAt("table2", i, "CountryName")).trim();
      stringCustomNo = ("" + getValueAt("table2", i, "CustomNoNew")).trim();
      stringCustomName = ("" + getValueAt("table2", i, "CustomNameNew")).trim();
      stringPercent = ("" + getValueAt("table2", i, "Percentage")).trim();
      strBirthday = ("" + getValueAt("table2", i, "Birthday")).trim();
      strMajorName = ("" + getValueAt("table2", i, "MajorName")).trim();
      strPositionName = ("" + getValueAt("table2", i, "PositionName")).trim();
      stringZIP = ("" + getValueAt("table2", i, "ZIP")).trim();
      stringCity = ("" + getValueAt("table2", i, "City")).trim();
      stringTown = ("" + getValueAt("table2", i, "Town")).trim();
      stringAddress = ("" + getValueAt("table2", i, "Address")).trim();
      strCellphone = ("" + getValueAt("table2", i, "Cellphone")).trim();
      stringTel = ("" + getValueAt("table2", i, "Tel")).trim();
      strTel2 = ("" + getValueAt("table2", i, "Tel2")).trim();
      streMail = ("" + getValueAt("table2", i, "eMail")).trim();
      strIsBlackList = ("" + getValueAt("table2", i, "IsBlackList")).trim();
      strIsControlList = ("" + getValueAt("table2", i, "IsControlList")).trim();
      strIsLinked = ("" + getValueAt("table2", i, "IsLinked")).trim();

      if ("1".equals(stringChangeName)) {

        // ID�ť�
        if ("".equals(stringCustomNo)) {
          message("[�νs/�����Ҹ�] ���i���ťաC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }

        // �q��m�W�ť�
        if ("".equals(stringCustomName)) {
          message("[�q��m�W] ���i���ťաC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }

        // 20200610 Kyle : �s�Ȥ�AML����W���ˬd
        System.out.println(">>>start to chk181");
        amlCons.put("customId", stringCustomNo);
        amlCons.put("customName", stringCustomName);
        amlCons.put("funcName2", "�Ȥ���");
        amlCons.put("customTitle", "�Ȥ�");
        String amlRS = amlTools.chkX181_Sanctions(amlCons);
        if (!"".equals(amlRS)) {
          messagebox(amlRS);
          return value;
        }
        System.out.println(">>>end to chk181");
        // �s�Ȥ��ˮ֧���

        flag1 = true;
        // �Ȥ�
        if ("".equals(stringNationality)) {
          message("[���y] ���i���ťաC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }

        // �k�H���ˮֹ���H�O�_�ť�
        System.out.println("tb5_string.length==>" + tb5_string.length);
        if (stringCustomNo.length() == 8) {
          if (tb5_string.length == 0) {
            messagebox("��s�Ȥ��ƥ]�t�k�H�A�Ы��w������q�H!!");
            return value;
          }
          // �Ӫk�H�O�_�s�b
          boolean flag91Ben = true;
          if (tb5_string.length > 0) {
            for (int e = 0; e < tb5_string.length; e++) {
              String strCustomNo = "";
              strCustomNo = ("" + getValueAt("table5", e, "CustomNo")).trim();
              if (stringCustomName.equals(strCustomNo)) { // �o�����H��CustomNo�A�˪����OCustomName
                flag91Ben = false;
                break;
              }
            }
            if (flag91Ben) {
              message("�k�H[" + stringCustomName + "]�A�Ы��w������q�H!!");
              return value;
            }
          }
        }
        if ("1".equals(stringNationality)) {
          // ����H
          if (stringCustomNo.length() != 8 && stringCustomNo.length() != 10) {
            message("[�νs/�����Ҹ�] ���׿��~!");
            jtable2.setRowSelectionInterval(i, i);
            return value;
          }
          if (stringCustomNo.length() == 8 && check.isCoId(stringCustomNo) == false) {
            message("[�νs/�����Ҹ�] �Τ@�s�����~!");
            jtable2.setRowSelectionInterval(i, i);
            return value;
          }
          if (stringCustomNo.length() == 10 && check.isID(stringCustomNo) == false) {
            message("[�νs/�����Ҹ�] �����Ҹ����~!");
            jtable2.setRowSelectionInterval(i, i);
            return value;
          }
        } else {
          // �~��H
        }
        
        // �O�_�w���ʫ��ҩ���
        stringSQL = "SELECT  CustomNo " + " FROM  Sale05M091 " + " WHERE  OrderNo  =  '" + stringOrderNo + "' " + " AND  CustomNo  =  '" + stringCustomNo + "' ";
        retSale05M091 = dbSale.queryFromPool(stringSQL);
        if (retSale05M091.length > 0) {
          // message("�ӵ��Ȥ�:" +stringCustomName+"�b���ʫ��ҩ���w����ơC");
          // jtable2.setRowSelectionInterval(i, i) ;
          // return value ;
        }

        // ����
        if (vectorCustomNo.indexOf(stringCustomNo) != -1) {
          message("�ӵ��Ȥ�:" + stringCustomName + " ��ƭ��СC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        vectorCustomNo.add(stringCustomNo);

        // ���
        if ("".equals(stringPercent)) {
          message("[���%] ���i���ťաC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        if (!check.isFloat(stringPercent, "5,2")) {
          message("[���%] �������Ʀr�C");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        System.out.println("stringPercent:" + stringPercent);
        if (Float.parseFloat(stringPercent) < 1) {
          message("[���%] ���o�p�� 1 %�C");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        stringPercentSum = operation.floatAdd(stringPercentSum, stringPercent, 2);
        System.out.println("stringPercentSum:" + stringPercentSum);

        if ("".equals(stringCity)) {
          message("[����] ���i���ťաC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        if ("".equals(stringTown)) {
          message("[�m��]  ���i���ťաC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        stringSql = " SELECT  ZIP " + " FROM  Town b " + " WHERE  Coun   IN  (SELECT  Coun  FROM  City  WHERE  CounName='" + stringCity + "') " + " AND  TownName  =  '"
            + stringTown + "' ";
        retTown = dbDoc.queryFromPool(stringSql);
        if (retTown.length == 0) {
          messagebox("[����][�m��] ���Y�����T�C");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        /*
         * if(stringZIP.length() > 3) stringZIP = stringZIP.substring(0,3) ;
         * if(!stringZIP.equals(retTown[0][0].trim())) { messagebox("[�l���ϸ�] �����T�C") ;
         * jtable2.setRowSelectionInterval(i, i) ; return value ; }
         */
        if ("".equals(stringAddress)) {
          message("[�a�}] ���i���ťաC");
          jtable2.setRowSelectionInterval(i, i);
          return value;
        }
        // kyle 20200212 - �S��ݨD: �Ntable2[][]��MAP
        tb2Map.put(tb2_string[i][7].trim(), tb2_string[i][6].trim());
      }
    }

    System.out.println("stringPercentSum=====>" + stringPercentSum);
    if (Float.parseFloat(stringPercentSum) != 100) {
      message("[���%] �X�p���� 100 %�C");
      // jtable2.setRowSelectionInterval(i, i) ;
      return value;
    }

    // System.out.println("flag1"+flag1);
    if (flag1 == false) {
      message("�Цܤ֤Ŀ�@�����W");
      return value;
    }

    // 20200620 Kyle : ���q�H���~���ˮ�
    if (mlpUtils.checkBenColumn(tb5_string, amlCons) == false)
      return value;

    // ���ʸ�Ʈw
    // �Ȥ���
    stringSQL = "SELECT  max(RecordNo) " + " FROM  Sale05M091 " + " WHERE  OrderNo  =  '" + stringOrderNo + "' ";
    retSale05M091 = dbSale.queryFromPool(stringSQL);
    RecordNo = Integer.parseInt(retSale05M091[0][0]) + 1;
    //
    String stringCustomNoNew = "";
    String stringCustomNewName = "";
    String stringEmail = "";
    for (int i = 0; i < tb1_string.length; i++) {
      stringCustomNo = ("" + getValueAt("table1", i, "CustomNo")).trim();
      stringCustomName = ("" + getValueAt("table1", i, "CustomName")).trim();
      //
      stringSQL = " UPDATE Sale05M091 SET " + "  StatusCd = 'C', " + "  TrxDate = '" + stringTrxDate + "' " + " WHERE OrderNo  =  '" + stringOrderNo + "' " + " and  CustomNo  =  '"
          + stringCustomNo + "' " + " and  ISNULL(StatusCd,'')='' ";
      System.out.println(i + "------------------UPDATE  Sale05M091----------" + stringSQL);
      vectorSql.add(stringSQL);

      stringSQL = " INSERT  " + " INTO Sale05M093" + " ( " + " ProjectID1," + " OrderNo," + " RecordNo," + " TrxDate," + " CustomNo, " + " CustomName, " + " CustomNoNew, "
          + " CustomNewName, " + " DiscountOpen, " + " PaperNo, " + " MajorID, " + " MajorName " + " ) " + " VALUES " + " ( " + " '" + getValue("ProjectID1").trim() + "', " + " '"
          + stringOrderNo + "', " + " N'" + (i + 1) + "', " + " N'" + stringTrxDate + "', " + " N'" + stringCustomNo + "', " + " N'" + stringCustomName + "', ";
      System.out.println("tb2_string.length: " + tb2_string.length);
      System.out.println("tb1_string.length: " + tb1_string.length);
      if (tb2_string.length < tb1_string.length) {
        stringCustomNoNew = ("" + getValueAt("table2", 0, "CustomNoNew")).trim();
        stringCustomNewName = ("" + getValueAt("table2", 0, "CustomNameNew")).trim();
      } else {
        stringCustomNoNew = ("" + getValueAt("table2", i, "CustomNoNew")).trim();
        stringCustomNewName = ("" + getValueAt("table2", i, "CustomNameNew")).trim();
      }
      stringSQL = stringSQL + " N'" + stringCustomNoNew + "' , " + " N'" + stringCustomNewName + "' , " + " '', ";
      stringSQL = stringSQL + "'" + paperNo + "','" + majorID + "','" + majorName + "') ";
      System.out.println(i + "------------------INSERT  Sale05M093----------" + stringSQL);
      vectorSql.add(stringSQL);

    }
    // �s�W91
    // �Ȥ���
    for (int i = 0; i < tb2_string.length; i++) {
      stringChangeName = ("" + getValueAt("table2", i, "ChangeName")).trim();

      stringAuditorship = ("" + getValueAt("table2", i, "auditorship")).trim();
      stringNationality = ("" + getValueAt("table2", i, "Nationality")).trim();
      strCountryName = ("" + getValueAt("table2", i, "CountryName")).trim();
      stringCustomNo = ("" + getValueAt("table2", i, "CustomNoNew")).trim();
      stringCustomName = ("" + getValueAt("table2", i, "CustomNameNew")).trim();
      stringPercent = ("" + getValueAt("table2", i, "Percentage")).trim();
      strBirthday = ("" + getValueAt("table2", i, "Birthday")).trim();
      strMajorName = ("" + getValueAt("table2", i, "MajorName")).trim();
      strPositionName = ("" + getValueAt("table2", i, "PositionName")).trim();
      stringZIP = ("" + getValueAt("table2", i, "ZIP")).trim();
      stringCity = ("" + getValueAt("table2", i, "City")).trim();
      stringTown = ("" + getValueAt("table2", i, "Town")).trim();
      stringAddress = ("" + getValueAt("table2", i, "Address")).trim();
      strCellphone = ("" + getValueAt("table2", i, "Cellphone")).trim();
      stringTel = ("" + getValueAt("table2", i, "Tel")).trim();
      strTel2 = ("" + getValueAt("table2", i, "Tel2")).trim();
      streMail = ("" + getValueAt("table2", i, "eMail")).trim();
      strIsBlackList = ("" + getValueAt("table2", i, "IsBlackList")).trim();
      strIsControlList = ("" + getValueAt("table2", i, "IsControlList")).trim();
      strIsLinked = ("" + getValueAt("table2", i, "IsLinked")).trim();

      // System.out.println("i2"+tb2_string[i][2]);
      if ("1".equals(stringChangeName)) {
        stringSQL = " INSERT  INTO Sale05M091  ( " + "   													OrderNo,RecordNo,auditorship,Nationality,CountryName, "
            + "   													CustomNo,CustomName,Percentage,Birthday,MajorName, " + "   													PositionName,ZIP,City,Town,Address,Cellphone, "
            + "   													Tel,Tel2,eMail,IsBlackList,IsLinked,IsControlList,TrxDateDown " + " ) VALUES ( " + "'" + stringOrderNo + "'," + "N'" + RecordNo + "',"
            + "N'" + stringAuditorship + "'," + "N'" + stringNationality + "'," + "N'" + strCountryName + "'," + "N'" + stringCustomNo + "'," + "N'" + stringCustomName + "',"
            + "N'" + stringPercent + "'," + "N'" + strBirthday + "'," + "N'" + strMajorName + "'," + "N'" + strPositionName + "', " + "N'" + stringZIP + "', " + "N'" + stringCity
            + "' ," + "N'" + stringTown + "' ," + "N'" + stringAddress + "' ," + "N'" + strCellphone + "' ," + "N'" + stringTel + "' ," + "N'" + strTel2 + "' ," + "N'" + streMail
            + "' ," + "N'" + strIsBlackList + "' ," + "N'" + strIsLinked + "' ," + "N'" + strIsControlList + "' ," + "N'" + stringTrxDate + "' " + " ) ";
        System.out.println(i + "------------------INSERT  Sale05M091----------" + stringSQL);
        vectorSql.add(stringSQL);
        RecordNo = RecordNo + 1;
      }
    }

    // ��� + �N�z�H�@�����
    String trxDate = getValue("TrxDate").trim();

    // ����H
    String strNew91BenNo = "";
    String strNew91BenName = "";
    String str91BenCustomNo = "";
    String str91BenNo = "";
    String str91BenName = "";
    String str91BenBirthday = "";
    String str91BenCountryName = "";
    String str91BenHoldType = "";
    String str91BenIsBlackList = "";
    String str91BenIsControlList = "";
    String str91BenIsLinked = "";

    // 20200226 kyle : �n�D�令���Ȥᴫ�W���˫O�d��ƨå�StatusCd='C'�аO
    // stringSQL= "DELETE FROM Sale05M091Ben WHERE OrderNo = '" +
    // getValue("OrderNo") + "'" ;
    stringSQL = "UPDATE Sale05M091Ben set StatusCd = 'C', TrxDate = '" + trxDate + "' WHERE OrderNo = '" + getValue("OrderNo") + "'";
    vectorSql.add(stringSQL);

    System.out.println("tb5_string.length=====>>" + tb5_string.length);
    if (tb5_string.length > 0) {
      stringSQL = "select ISNULL(max(RecordNo), 0)+1 from Sale05M091Ben where orderNo = '" + getValue("OrderNo").trim() + "' ";
      String[][] retMax = dbSale.queryFromPool(stringSQL);
      int maxRecordNo = Integer.parseInt(retMax[0][0].trim());

      for (int j = 0; j < tb5_string.length; j++) {
        str91BenNo = ("" + getValueAt("table5", j, "BenNo")).trim();
        str91BenName = ("" + getValueAt("table5", j, "BenName")).trim();
        str91BenBirthday = ("" + getValueAt("table5", j, "Birthday")).trim();
        str91BenCountryName = ("" + getValueAt("table5", j, "CountryName")).trim();
        str91BenHoldType = ("" + getValueAt("table5", j, "HoldType")).trim();
        str91BenIsBlackList = ("" + getValueAt("table5", j, "IsBlackList")).trim();
        str91BenIsControlList = ("" + getValueAt("table5", j, "IsControlList")).trim();
        str91BenIsLinked = ("" + getValueAt("table5", j, "IsLinked")).trim();
        String getMapKey = tb5_string[j][2].trim();
        int recordNo = j + maxRecordNo;
        stringCustomNo = tb2Map.get(getMapKey) == null ? "" : tb2Map.get(getMapKey).toString();
        System.out.println("tb5_string.stringCustomNo=====>>" + stringCustomNo);
        if ("".equals(stringCustomNo)) {
          messagebox("�����q�H [" + getMapKey + "] �䤣������Ȥ�");
          message("�нT�{��s�Ȥ�W��ι��������q�H�O�_���T!!");
          return value;
        }

        str91BenBirthday = convert.replace(str91BenBirthday, "/", "");

        stringSQL = " INSERT INTO Sale05M091Ben (OrderNo,RecordNo,CustomNo,BenName,BCustomNo,Birthday,CountryName,HoldType,IsBlackList,IsControlList,IsLinked ) " + " VALUES   ( "
            + "'" + getValue("OrderNo") + "'," + "N'" + recordNo + "'," + "N'" + stringCustomNo + "'," + "N'" + str91BenName + "'," + "N'" + str91BenNo + "'," + "N'"
            + str91BenBirthday + "'," + "N'" + str91BenCountryName + "'," + "N'" + str91BenHoldType + "'," + "N'" + str91BenIsBlackList + "'," + "N'" + str91BenIsControlList + "',"
            + "N'" + str91BenIsLinked + "' " + " ) ";
        System.out.println(j + "------------------INSERT  Sale05M091Ben----------" + stringSQL);
        vectorSql.add(stringSQL);
      }
    }

    // �N�z�H
    String strNew91ACustomNo = "";
    String strNew91AgentName = "";
    String str91OrderNo = "";
    String str91CustomNo = "";
    String str91ACustomNo = "";
    String str91AgentName = "";
    String str91AgentCountryName = "";
    String str91AgentRel = "";
    String str91AgentReason = "";
    String str91AgentIsBlackList = "";
    String str91AgentIsControlList = "";
    String str91AgentIsLinked = "";

    // 20200226 kyle : �n�D�令���Ȥᴫ�W���˫O�d��ƨå�StatusCd='C'�аO
    // stringSQL= "DELETE FROM Sale05M091Agent WHERE OrderNo = '" +
    // getValue("OrderNo") + "'" ;
    stringSQL = "UPDATE Sale05M091Agent set StatusCd = 'C', TrxDate = '" + trxDate + "' WHERE OrderNo = '" + getValue("OrderNo") + "'";
    vectorSql.add(stringSQL);

    if (tb6_string.length > 0) {
      stringSQL = "select ISNULL(max(RecordNo), 0)+1 from Sale05M091Agent where orderNo = '" + getValue("OrderNo").trim() + "' ";
      String[][] retMax = dbSale.queryFromPool(stringSQL);
      int maxRecordNo = Integer.parseInt(retMax[0][0].trim());

      for (int k = 0; k < tb6_string.length; k++) {
        str91OrderNo = ("" + getValueAt("table6", k, "OrderNo")).trim();
        str91CustomNo = ("" + getValueAt("table6", k, "CustomNo")).trim();
        str91ACustomNo = ("" + getValueAt("table6", k, "ACustomNo")).trim();
        str91AgentName = ("" + getValueAt("table6", k, "AgentName")).trim();
        str91AgentCountryName = ("" + getValueAt("table6", k, "CountryName")).trim();
        str91AgentRel = ("" + getValueAt("table6", k, "AgentRel")).trim();
        str91AgentReason = ("" + getValueAt("table6", k, "AgentReason")).trim();
        str91AgentIsBlackList = ("" + getValueAt("table6", k, "IsBlackList")).trim();
        str91AgentIsControlList = ("" + getValueAt("table6", k, "IsControlList")).trim();
        str91AgentIsLinked = ("" + getValueAt("table6", k, "IsLinked")).trim();
        String getMapKey = tb6_string[k][2].trim();
        int recordNo = k + maxRecordNo;
        stringCustomNo = tb2Map.get(getMapKey) == null ? "" : tb2Map.get(getMapKey).toString();
        System.out.println("tb5_string.stringCustomNo=====>>" + stringCustomNo);
        if ("".equals(stringCustomNo)) {
          messagebox("�N�z�H [" + getMapKey + "] �䤣������Ȥ�");
          message("�нT�{��s�Ȥ�W��ι����N�z�H�O�_���T!!");
          return value;
        }
        stringSQL = " INSERT INTO Sale05M091Agent (OrderNo,RecordNo,CustomNo,AgentName,ACustomNo,CountryName,AgentRel,AgentReason,IsBlackList,IsControlList,IsLinked ) "
            + " VALUES   (    " + "N'" + getValue("OrderNo") + "'," + "N'" + recordNo + "'," + "N'" + stringCustomNo + "'," + "N'" + str91AgentName + "'," + "N'" + str91ACustomNo
            + "'," + "N'" + str91AgentCountryName + "'," + "N'" + str91AgentRel + "'," + "N'" + str91AgentReason + "'," + "N'" + str91AgentIsBlackList + "'," + "N'"
            + str91AgentIsControlList + "'," + "N'" + str91AgentIsLinked + "' " + " ) ";
        System.out.println(k + "------------------INSERT  Sale05M091Agent----------" + stringSQL);
        vectorSql.add(stringSQL);
      }
    }
    if (vectorSql.size() > 0) {
      dbSale.execFromPool((String[]) vectorSql.toArray(new String[0]));
      getButton("button3").setVisible(true);
      // getButton("button4").setVisible(true); //20200212 kyle mod:
      // �R���|�y�������q�H�����A���pUSER�Τ���o�ӥ\�������
    }
    //
    stringSQL = "SELECT * FROM Sale05M091 " + " WHERE OrderNo = '" + getValue("OrderNo") + "'" + " and isnull(StatusCd,'') = '' ";
    String[][] ret91 = dbSale.queryFromPool(stringSQL);
    System.out.println("stringSQL==>" + stringSQL);
    System.out.println("ret91.length==>" + ret91.length);
    if (ret91.length == 0) {
      stringSQL = "UPDATE  Sale05M091 SET " + "  StatusCd = '', " + "  TrxDate = '' " + " WHERE OrderNo = '" + getValue("OrderNo") + "'" + " and CustomNo = '" + stringCustomNo
          + "' ";
      dbSale.execFromPool(stringSQL);
      stringSQL = "Delete from  Sale05M093  " + " WHERE OrderNo = '" + getValue("OrderNo") + "'" + " and CustomNo = '" + stringCustomNo + "' ";
      dbSale.execFromPool(stringSQL);
      getButton("button3").setVisible(false);
      // getButton("button4").setVisible(true); //20200212 kyle mod:
      // �R���|�y�������q�H�����A���pUSER�Τ���o�ӥ\�������
    }

    System.out.println("�~�����I��-------------------------------------S");
    getButton("CheckRiskNew").doClick();
    System.out.println("�~�����I��--------------------------------------E");

    System.out.println("AML-------------------------------------S");
    setValue("actionText", "�s��");
    getButton("AML").doClick();
    System.out.println("AML-------------------------------------E");

    messagebox("���W���\");

    return value;
  }

  public String getInformation() {
    return "---------------button1(button1).defaultValue()----------------";
  }
}
