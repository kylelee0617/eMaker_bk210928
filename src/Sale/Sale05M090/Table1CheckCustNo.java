package Sale.Sale05M090;

import javax.swing.JButton;
import javax.swing.JTable;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bvalidate;
import jcx.util.check;

/**
 * table1 - �����Ҧr���ˮ�
 * 
 * @author B04391
 *
 */

public class Table1CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {
    System.out.println("getFunctionName>>>" + getFunctionName());
    System.out.println("POSITION>>>" + POSITION);

    value = value.trim();
    KUtils kUtil = new KUtils();
    talk dbSale = getTalk("Sale");
    JTable tb1 = getTable("table1");
    int sRow = tb1.getSelectedRow();
    setValueAt("table1", value, sRow, "CustomNo");

    String projectId = getValue("field1").trim();
    String orderNo = getValue("field3").trim();
    String orderDate = getValue("field2").trim();
    String modCustFlag = getValue("CustomID_NAME_PER_Editable").toString().trim();

    if (POSITION == 4 && "0".equals(modCustFlag)) return false;
    
    if (sRow == -1) return false;
    
    if ("".equals(projectId)) {
      message("[�קO�N�X] ���i�ť�!");
      return false;
    }
    String stringNationality = ("" + getValueAt("table1", sRow, "Nationality")).trim();
    if ("1".equals(stringNationality)) {
      // �ȥ���H���� 20090414
      if (value.length() == 0) {
        message("[�νs/�����Ҹ�] ���i�ť�!");
        return false;
      }
      if (value.length() != 8 && value.length() != 10) {
        messagebox("[�νs/�����Ҹ�] ���׿��~!");
        return false;
      }
      if (value.length() == 8 && check.isCoId(value) == false) {
        messagebox("[�νs/�����Ҹ�] �Τ@�s�����~!");
        return false;
      }
      if (value.length() == 10 && check.isID(value) == false) {
        messagebox("[�νs/�����Ҹ�] �����Ҹ����~!");
        return false;
      }
    }
    
    //�M��O�_���P�@�ӫȤ������
    String stringSQL = "";
    stringSQL = "SELECT TOP 1 CustomName,  Address,  Tel,  eMail, Nationality, auditorship, ZIP, City, Town, Cellphone " + "FROM Sale05M091  WHERE CustomNo = '" + value + "' "
        + "ORDER BY OrderNo DESC ";
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    boolean ret091HasData = retSale05M091.length > 0;
    setValueAt("table1", ret091HasData ? retSale05M091[0][0].trim() : "", sRow, "CustomName");
    setValueAt("table1", ret091HasData ? retSale05M091[0][1].trim() : "", sRow, "Address");
    setValueAt("table1", ret091HasData ? retSale05M091[0][2].trim() : "", sRow, "Tel");
    setValueAt("table1", ret091HasData ? retSale05M091[0][3].trim() : "", sRow, "eMail");
    setValueAt("table1", ret091HasData ? retSale05M091[0][4].trim() : "", sRow, "Nationality");
    setValueAt("table1", ret091HasData ? retSale05M091[0][5].trim() : "", sRow, "auditorship");
    setValueAt("table1", ret091HasData ? retSale05M091[0][6].trim() : "", sRow, "ZIP");
    setValueAt("table1", ret091HasData ? retSale05M091[0][7].trim() : "", sRow, "City");
    setValueAt("table1", ret091HasData ? retSale05M091[0][8].trim() : "", sRow, "Town");
    setValueAt("table1", ret091HasData ? retSale05M091[0][9].trim() : "", sRow, "Cellphone");

    //�����򥻸�ư��ݶ¦W��d��
    String tmpMsg = "";
    String errMsg = "";
    String amlRsMix = getValue("AMLRsMix").trim();
    QueryLogBean qBean = kUtil.getQueryLogByCustNoProjectId(projectId, value);
    if (qBean != null) {
      String bstatus = qBean.getbStatus();
      String cstatus = qBean.getcStatus();
      String rstatus = qBean.getrStatus();
      String qName = qBean.getName();
      String birthday = qBean.getBirthday();
      String indCode = qBean.getJobType();
      String funcName = getFunctionName().trim();
      String recordType = "�Ȥ���";
      setValueAt("table1", qName, sRow, "CustomName");
      setValueAt("table1", bstatus, sRow, "IsBlackList");
      setValueAt("table1", cstatus, sRow, "IsControlList");
      setValueAt("table1", rstatus, sRow, "IsLinked");

      // �ܴ�Start
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                     + "query1821";
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      tmpMsg = getValue("AMLText").trim();
      errMsg += tmpMsg;
      // �ܴ�END

      // �¦W�� + ���ަW��
      if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
        tmpMsg = "�Ȥ�" + qName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
        errMsg += tmpMsg;
      }

      // �Q���H
      if ("Y".equals(rstatus)) {
        tmpMsg += "�Ȥ�" + qName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
        errMsg += tmpMsg;
      }

      //���
      if (!"".equals(errMsg)) {
        messagebox(errMsg);
      }

    } else {
      messagebox("�¦W��t�εL����T�C");
    }
    
    //�Y�u���@���A����100%
    if (tb1.getRowCount() == 1)
      setValueAt("table1", "100", sRow, "Percentage");

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
