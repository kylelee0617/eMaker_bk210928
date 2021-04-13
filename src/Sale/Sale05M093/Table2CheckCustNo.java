package Sale.Sale05M093;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;

public class Table2CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    // �i�۩w����ˮֱ���
    // �ǤJ�� value ���J��
    talk dbSale = getTalk("Sale");
    JTable jtableTable2 = getTable("table2");
    int intSelectRow = jtableTable2.getSelectedRow();
    String stringSQL = "";
    String stringNationality = "";
    String stringCustomNoNew = value.trim();

    // �ʫ��ҩ�����
    String orderNo = getValue("OrderNo").trim();
    String[][] order = dbSale.queryFromPool("select a.OrderNo , a.OrderDate from Sale05M090 a where a.OrderNo = '" + orderNo + "' ");
    String orderDate = order[0][0].trim();

    if (intSelectRow == -1)
      return true;
    stringNationality = ("" + getValueAt("table2", intSelectRow, "Nationality")).trim();
    if ("1".equals(stringNationality)) {
      // ����H
      if (stringCustomNoNew.length() != 8 && stringCustomNoNew.length() != 10) {
        message("[�νs/�����Ҹ�] ���׿��~!");
        return false;
      }
      if (stringCustomNoNew.length() == 8 && check.isCoId(stringCustomNoNew) == false) {
        message("[�νs/�����Ҹ�] �Τ@�s�����~!");
        return false;
      }
      if (stringCustomNoNew.length() == 10 && check.isID(stringCustomNoNew) == false) {
        message("[�νs/�����Ҹ�] �����Ҹ����~!");
        return false;
      }
      if (stringCustomNoNew.length() == 8 && check.isCoId(stringCustomNoNew) == true) {
        JTabbedPane jtp2 = getTabbedPane("tab2");
        // jtp2.setEnabledAt(1, true);
      }
    } else {
      // �~��H
      if ("".equals(stringCustomNoNew)) {
        setValueAt("table2", "A", intSelectRow, "auditorship"); // ����
        setValueAt("table2", "", intSelectRow, "CountryName"); // ��O
        setValueAt("table2", "", intSelectRow, "CustomNoNew"); // �q��m�W
        // setValueAt("table2", "", intSelectRow, "Percentage"); // ��� %
        setValueAt("table2", "", intSelectRow, "Birthday"); // �ͤ�/���U��
        setValueAt("table2", "", intSelectRow, "MajorName"); // �~�O
        setValueAt("table2", "", intSelectRow, "PositionName"); // ¾��
        setValueAt("table2", "", intSelectRow, "ZIP"); // �l���ϸ�
        setValueAt("table2", "", intSelectRow, "City"); // ����
        setValueAt("table2", "", intSelectRow, "Town"); // �m��
        setValueAt("table2", "", intSelectRow, "Address"); // �a�}
        setValueAt("table2", "", intSelectRow, "Cellphone"); // ��ʹq��
        setValueAt("table2", "", intSelectRow, "Tel"); // �q��
        setValueAt("table2", "", intSelectRow, "Tel2"); // �q��2
        setValueAt("table2", "", intSelectRow, "eMail"); // e-Mail
        setValueAt("table2", "", intSelectRow, "IsBlackList"); // �¦W��
        setValueAt("table2", "", intSelectRow, "IsControlList"); // ���ަW��
        setValueAt("table2", "", intSelectRow, "IsLinked"); // �Q�`���Y�H

        return true;
      }
    }
    // 0 auditorship 1 Nationality 2 CountryName 3 CustomName 4 Percentage
    // 5 Birthday 6 MajorName 7 PositionName 8 ZIP 9 City
    // 10 Town 11 Address 12 Cellphone 13 Tel 14 Tel2
    // 15 eMail 16 IsBlackList 17 IsControlList 18 IsLinked
    stringSQL = " SELECT TOP 1 auditorship, Nationality, CountryName, CustomName, Percentage, Birthday,MajorName, PositionName,ZIP,City,Town,Address,Cellphone,Tel,Tel2,eMail,IsBlackList, IsControlList, IsLinked "
        + " FROM Sale05M091 " + " WHERE CustomNo = '" + stringCustomNoNew + "' " + " ORDER BY OrderNo DESC ";
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    if (retSale05M091.length <= 0) {
      setValueAt("table2", "A", intSelectRow, "auditorship"); // ����
      setValueAt("table2", "", intSelectRow, "CountryName"); // ��O
      setValueAt("table2", "", intSelectRow, "CustomNoNew"); // �q��m�W
      // setValueAt("table2", "", intSelectRow, "Percentage"); // ��� %
      setValueAt("table2", "", intSelectRow, "Birthday"); // �ͤ�/���U��
      setValueAt("table2", "", intSelectRow, "MajorName"); // �~�O
      setValueAt("table2", "", intSelectRow, "PositionName"); // ¾��
      setValueAt("table2", "", intSelectRow, "ZIP"); // �l���ϸ�
      setValueAt("table2", "", intSelectRow, "City"); // ����
      setValueAt("table2", "", intSelectRow, "Town"); // �m��
      setValueAt("table2", "", intSelectRow, "Address"); // �a�}
      setValueAt("table2", "", intSelectRow, "Cellphone"); // ��ʹq��
      setValueAt("table2", "", intSelectRow, "Tel"); // �q��
      setValueAt("table2", "", intSelectRow, "Tel2"); // �q��2
      setValueAt("table2", "", intSelectRow, "eMail"); // e-Mail
      setValueAt("table2", "", intSelectRow, "IsBlackList"); // �¦W��
      setValueAt("table2", "", intSelectRow, "IsControlList"); // ���ަW��
      setValueAt("table2", "", intSelectRow, "IsLinked"); // �Q�`���Y�H
    }
    for (int intSale05M091 = 0; intSale05M091 < retSale05M091.length; intSale05M091++) {
      // System.out.println("retSale05M091[intSale05M091][0]:"+retSale05M091[intSale05M091][0]);
      setValueAt("table2", retSale05M091[intSale05M091][0].trim(), intSelectRow, "auditorship"); // ����
      setValueAt("table2", retSale05M091[intSale05M091][1].trim(), intSelectRow, "Nationality"); // ���y
      setValueAt("table2", retSale05M091[intSale05M091][2].trim(), intSelectRow, "CountryName"); // ��O
      setValueAt("table2", retSale05M091[intSale05M091][3].trim(), intSelectRow, "CustomNameNew"); // �q��m�W
      // setValueAt("table2", retSale05M091[intSale05M091][4].trim(), intSelectRow,
      // "Percentage"); //���
      setValueAt("table2", "100", intSelectRow, "Percentage"); // ���
      setValueAt("table2", retSale05M091[intSale05M091][5].trim(), intSelectRow, "Birthday"); // �ͤ�/���U��
      setValueAt("table2", retSale05M091[intSale05M091][6].trim(), intSelectRow, "MajorName"); // �~�O
      setValueAt("table2", retSale05M091[intSale05M091][7].trim(), intSelectRow, "PositionName"); // ¾��
      setValueAt("table2", retSale05M091[intSale05M091][8].trim(), intSelectRow, "ZIP"); // �l���ϸ�
      setValueAt("table2", retSale05M091[intSale05M091][9].trim(), intSelectRow, "City"); // ����
      setValueAt("table2", retSale05M091[intSale05M091][10].trim(), intSelectRow, "Town"); // �m��
      setValueAt("table2", retSale05M091[intSale05M091][11].trim(), intSelectRow, "Address"); // ��}
      setValueAt("table2", retSale05M091[intSale05M091][12].trim(), intSelectRow, "Cellphone"); // ��ʹq��
      setValueAt("table2", retSale05M091[intSale05M091][13].trim(), intSelectRow, "Tel"); // �q��
      setValueAt("table2", retSale05M091[intSale05M091][14].trim(), intSelectRow, "Tel2"); // �q��2
      setValueAt("table2", retSale05M091[intSale05M091][15].trim(), intSelectRow, "eMail"); // EMAIL
      setValueAt("table2", retSale05M091[intSale05M091][16].trim(), intSelectRow, "IsBlackList"); // �¦W��
      setValueAt("table2", retSale05M091[intSale05M091][17].trim(), intSelectRow, "IsControlList"); // ���ަW��
      setValueAt("table2", retSale05M091[intSale05M091][17].trim(), intSelectRow, "IsLinked"); // �Q�`���Y�H
    }
    talk dbBlist = getTalk("pw0d");
    String bstatus = "";
    String cstatus = "";
    String rstatus = "";
    String logName = "";
    String errSmg = "";
    String strProjectID1 = getValue("ProjectID1").trim();
    String sqlBlack = "Select TOP 1 B_STATUS, C_STATUS, R_STATUS, NAME, BIRTHDAY, JOB_TYPE FROM QUERY_LOG WHERE QUERY_ID = '" + value + "' AND PROJECT_ID = '" + strProjectID1
        + "' Order By QID Desc ";
    String retBlack[][] = dbBlist.queryFromPool(sqlBlack);
    if (retBlack.length > 0) {
      bstatus = retBlack[0][0].trim();
      cstatus = retBlack[0][1].trim();
      rstatus = retBlack[0][2].trim();
      logName = retBlack[0][3].trim();
      String birthDay = retBlack[0][4].trim().replace("/", "-");
      String jobType = retBlack[0][5].trim();

      setValueAt("table2", logName, intSelectRow, "CustomNameNew");
      setValueAt("table2", bstatus, intSelectRow, "IsBlackList");
      setValueAt("table2", cstatus, intSelectRow, "IsControlList");
      setValueAt("table2", rstatus, intSelectRow, "IsLinked");
      setValueAt("table2", birthDay, intSelectRow, "BirthDay");

      // �ܴ�Start
      String birth = birthDay.length() == 0 ? " " : birthDay.toString().replace("-", "");
      String ind = jobType.length() == 0 ? " " : jobType;
      String amlText = orderNo + "," + orderDate + "," + value + "," + logName + "," + birth + "," + ind + "," + "query1821";
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      errSmg += getValue("AMLText").trim();
      // �ܴ�END

      // �¦W��
      // ���ަW��
      if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
        if ("".equals(errSmg)) {
          errSmg = "�Ȥ�" + logName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
        } else {
          errSmg = errSmg + "\n�Ȥ�" + logName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
        }
      }
      if ("Y".equals(rstatus)) {// �Q���H
        if ("".equals(errSmg)) {
          errSmg = "�Ȥ�" + logName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
        } else {
          errSmg = errSmg + "\n�Ȥ�" + logName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
        }
      }
      if (!"".equals(errSmg)) {
        messagebox(errSmg);
      }
    } else {
      messagebox("�L���Ȥ��T�C");
      message("�L���Ȥ��T�C");
    }
    message("");
    return true;
  }

  public String getInformation() {
    return "---------------null(null).CustomNoNew.field_check()----------------";
  }
}
