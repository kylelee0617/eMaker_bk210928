package Sale.Sale05M090;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import Sale.Sale05M090.Func.*;
import Farglory.aml.AMLTools_Lyods;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCustomBean;
import Farglory.util.Result;
import java.text.SimpleDateFormat;

/**
 * 
 * table1 - �����Ҧr���ˮ�
 * 
 * @author B04391
 *
 */

public class Table1CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {

    // �i�۩w����ˮֱ���
    // �ǤJ�� value ���J��
    value = value.trim();
    JTable jtableTable1 = getTable("table1");
    String orderNo = getValue("field3").trim();
    String orderDate = getValue("field2").trim();
    int s_row = jtableTable1.getSelectedRow();
    setValueAt("table1", value, s_row, "CustomNo");
    talk dbSale = getTalk("Sale");
    talk dbBlist = getTalk("pw0d");

    String modCustFlag = getValue("CustomID_NAME_PER_Editable").toString().trim();

    if (POSITION == 4 && "0".equals(modCustFlag))
      return false;
    String strNowTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate());
    int intSelectRow = jtableTable1.getSelectedRow();
    if (intSelectRow == -1)
      return false;
    String stringProjectID1 = getValue("field1").trim();
    if ("".equals(stringProjectID1)) {
      message("[�קO�N�X] ���i�ť�!");
      return false;
    }
    String stringNationality = ("" + getValueAt("table1", intSelectRow, "Nationality")).trim();
    if ("1".equals(stringNationality)) {
      // �ȥ���H���� 20090414
      if (value.length() == 0) {
        message("[�νs/�����Ҹ�] ���i�ť�!");
        // setFocus("table1", intSelectRow, "CustomNo") ;
        return false;
      }
      // �ȥ���H���� 20090414
      // ����H
      if (value.length() != 8 && value.length() != 10) {
        messagebox("[�νs/�����Ҹ�] ���׿��~!");
        // setFocus("table1", intSelectRow, "CustomNo") ;
        return false;
      }
      if (value.length() == 8 && check.isCoId(value) == false) {
        messagebox("[�νs/�����Ҹ�] �Τ@�s�����~!");
        // setFocus("table1", intSelectRow, "CustomNo") ;
        return false;
      }
      if (value.length() == 10 && check.isID(value) == false) {
        messagebox("[�νs/�����Ҹ�] �����Ҹ����~!");
        // setFocus("table1", intSelectRow, "CustomNo") ;
        return false;
      }
    } else {
      // �~��H
      if (value.length() == 0) {
        setValueAt("table1", "", intSelectRow, "CustomName");
        setValueAt("table1", "", intSelectRow, "Address");
        setValueAt("table1", "", intSelectRow, "Tel");
        setValueAt("table1", "", intSelectRow, "eMail");
        setValueAt("table1", "A", intSelectRow, "auditorship");
        setValueAt("table1", "", intSelectRow, "ZIP");
        setValueAt("table1", "", intSelectRow, "City");
        setValueAt("table1", "", intSelectRow, "Town");
        return true;
      }
    }

    String stringSQL = "";
    stringSQL = " SELECT TOP 1 CustomName, " + " Address, " + " Tel, " + " eMail, " + " Nationality, " + " auditorship, " + " ZIP, " + " City, " + " Town, " + " Cellphone "
        + " FROM Sale05M091 " + " WHERE CustomNo = '" + value + "'" + " ORDER BY OrderNo DESC ";
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    for (int intSale05M091 = 0; intSale05M091 < retSale05M091.length; intSale05M091++) {
      setValueAt("table1", retSale05M091[intSale05M091][0].trim(), intSelectRow, "CustomName");
      setValueAt("table1", retSale05M091[intSale05M091][1].trim(), intSelectRow, "Address");
      setValueAt("table1", retSale05M091[intSale05M091][2].trim(), intSelectRow, "Tel");
      setValueAt("table1", retSale05M091[intSale05M091][3].trim(), intSelectRow, "eMail");
      setValueAt("table1", retSale05M091[intSale05M091][4].trim(), intSelectRow, "Nationality");
      setValueAt("table1", retSale05M091[intSale05M091][5].trim(), intSelectRow, "auditorship");
      setValueAt("table1", retSale05M091[intSale05M091][6].trim(), intSelectRow, "ZIP");
      setValueAt("table1", retSale05M091[intSale05M091][7].trim(), intSelectRow, "City");
      setValueAt("table1", retSale05M091[intSale05M091][8].trim(), intSelectRow, "Town");
      setValueAt("table1", retSale05M091[intSale05M091][9].trim(), intSelectRow, "Cellphone");
    }
    String bstatus = "";
    String cstatus = "";
    String rstatus = "";
    String logName = "";
    String errSmg = "";
    String strProjectID1 = getValue("field1").trim();
    String sqlBlack = "Select TOP 1 B_STATUS, C_STATUS, R_STATUS,NAME,BIRTHDAY FROM QUERY_LOG WHERE QUERY_ID = '" + value + "' AND PROJECT_ID = '" + strProjectID1
        + "' Order By QID Desc ";
    String retBlack[][] = dbBlist.queryFromPool(sqlBlack);
    if (retBlack.length > 0) {
      bstatus = retBlack[0][0].trim();
      cstatus = retBlack[0][1].trim();
      rstatus = retBlack[0][2].trim();
      logName = retBlack[0][3].trim();
      String BirthDay = retBlack[0][4].trim().replace("/", "-");

      setValueAt("table1", logName, intSelectRow, "CustomName");
      setValueAt("table1", bstatus, intSelectRow, "IsBlackList");
      setValueAt("table1", cstatus, intSelectRow, "IsControlList");
      setValueAt("table1", rstatus, intSelectRow, "IsLinked");
      System.out.println("BirthDay=====>" + BirthDay);

      // -------------------------------AML Start-------------------------------------
      // ����W��181
      talk db400 = getTalk("400CRM");
      String str400sql = "";
      if ("".equals(BirthDay)) {
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE = 'X181' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + value + "' AND CUSTOMERNAME='" + logName + "' ";
      } else {
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE = 'X181' AND C.REMOVEDDATE >= '"
            + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + value + "' AND ( CUSTOMERNAME='" + logName + "' AND BIRTHDAY = '" + BirthDay + "' )";
      }
      String retCList[][] = db400.queryFromPool(str400sql);
      if (retCList.length > 0) {
        if ("".equals(errSmg)) {
          errSmg = "�Ȥ�" + logName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
        } else {
          errSmg = errSmg + "\n�Ȥ�" + logName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
        }
      }

      // PEPS�W��171
      str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE = 'X171' AND C.REMOVEDDATE >= '"
          + strNowTimestamp + "' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '" + value + "' AND CUSTOMERNAME='" + logName + "' ";
      if (!"".equals(BirthDay)) {
        str400sql += " AND BIRTHDAY = '" + BirthDay + "' ";
      }
      String ret171List[][] = db400.queryFromPool(str400sql);
      if (ret171List.length > 0) {
        if ("".equals(errSmg)) {
          errSmg = "�Ȥ�" + logName + "�B�a�x�����Φ��K�����Y���H�A�����n�F�v��¾�ȤH�h�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
        } else {
          errSmg = errSmg + "\n�Ȥ�" + logName + "�B�a�x�����Φ��K�����Y���H�A�����n�F�v��¾�ȤH�h�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C";
        }
      }
      
      // �ܴ�Start
      String birth = getValueAt("table1", s_row,  "Birthday").toString().length()==0? " ":getValueAt("table1", s_row,  "Birthday").toString();
      String ind = getValueAt("table1", s_row,  "IndustryCode").toString().length()==0? " ":getValueAt("table1", s_row,  "IndustryCode").toString();
      String amlText = orderNo + "," + orderDate + "," + value + "," + logName + "," + birth + "," + ind + "," + "query1821";
      setValue("AMLText" , amlText);
      getButton("BtCustAML").doClick();
      errSmg += getValue("AMLText").trim();
      // �ܴ�END
      
      // -------------------------------AML End-------------------------------------

      // �¦W��&���ަW��
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
      messagebox("�L���Ȥ��T�C no");
    }
    if (jtableTable1.getRowCount() == 1)
      setValueAt("table1", "100", intSelectRow, "Percentage");
    
    // �w�f
    JButton buyedInfo = getButton("BuyedInfo");
    buyedInfo.setText("userCusNo=" + value);
    buyedInfo.doClick();
    
    return true;
  }

  public String getInformation() {
    return "---------------null(null).CustomNo.field_check()----------------";
  }
}
