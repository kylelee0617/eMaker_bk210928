package Sale.Sale05M093;

import javax.swing.JTable;
import org.apache.commons.lang.StringUtils;
import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bvalidate;
import jcx.util.check;

public class Table2CheckCustNo extends bvalidate {
  public boolean check(String value) throws Throwable {

    value = value.trim();
    KUtils kUtil = new KUtils();
    talk dbSale = getTalk("Sale");
    JTable tb2 = getTable("table2");
    int sRow = tb2.getSelectedRow();
    setValueAt("table2", value, sRow, "CustomNo");
    
    String stringSQL = "";
    String projectId = getValue("ProjectID1").trim();
    String orderNo = getValue("OrderNo");
    String orderDate = kUtil.getOrderDateByOrderNo(orderNo);

    if (sRow == -1) return true;
    
    String stringNationality = ("" + getValueAt("table2", sRow, "Nationality")).trim();
    if ("1".equals(stringNationality)) {
      // ����H
      if (value.length() != 8 && value.length() != 10) {
        message("[�νs/�����Ҹ�] ���׿��~!");
        return false;
      }
      if (value.length() == 8 && check.isCoId(value) == false) {
        message("[�νs/�����Ҹ�] �Τ@�s�����~!");
        return false;
      }
      if (value.length() == 10 && check.isID(value) == false) {
        message("[�νs/�����Ҹ�] �����Ҹ����~!");
        return false;
      }
    }
    
    //AIdent
    JTable tb10 = getTable("table10");
    int tb10Row = tb10.getSelectedRow();
    if("�����k�w�N�z".equals(value) || "�����~".equals(value)) {
      this.setValueAt("table10", "B", tb10Row, "AIdent");
    }

    // ���ձqsale05m091���Ȥ���
    // 0 auditorship 1 Nationality 2 CountryName 3 CustomName 4 Percentage 5
    // Birthday 6 MajorName 7 PositionName 8 ZIP 9 City
    // 10 Town 11 Address 12 Cellphone 13 Tel 14 Tel2 15 eMail 16 IsBlackList 17
    // IsControlList 18 IsLinked
    stringSQL = " SELECT TOP 1 auditorship, Nationality, CountryName, CustomName, Percentage, Birthday,MajorName, PositionName,ZIP,City,Town,Address,Cellphone,Tel,Tel2,eMail,IsBlackList, IsControlList, IsLinked "
        + " FROM Sale05M091 WHERE CustomNo = '" + value + "' " + " ORDER BY OrderNo DESC ";
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    boolean ret091HasData = retSale05M091.length > 0;
    setValueAt("table2", ret091HasData ? retSale05M091[0][0].trim() : "", sRow, "auditorship"); // ����
    setValueAt("table2", ret091HasData ? retSale05M091[0][1].trim() : "", sRow, "Nationality"); // ���y
    setValueAt("table2", ret091HasData ? retSale05M091[0][2].trim() : "", sRow, "CountryName"); // ��O
    setValueAt("table2", ret091HasData ? retSale05M091[0][3].trim() : "", sRow, "CustomNameNew"); // �q��m�W
    setValueAt("table2", "100", sRow, "Percentage"); // ���
    setValueAt("table2", ret091HasData ? retSale05M091[0][5].trim() : "", sRow, "Birthday"); // �ͤ�/���U��
    setValueAt("table2", ret091HasData ? retSale05M091[0][6].trim() : "", sRow, "MajorName"); // �~�O
    setValueAt("table2", ret091HasData ? retSale05M091[0][7].trim() : "", sRow, "PositionName"); // ¾��
    setValueAt("table2", ret091HasData ? retSale05M091[0][8].trim() : "", sRow, "ZIP"); // �l���ϸ�
    setValueAt("table2", ret091HasData ? retSale05M091[0][9].trim() : "", sRow, "City"); // ����
    setValueAt("table2", ret091HasData ? retSale05M091[0][10].trim() : "", sRow, "Town"); // �m��
    setValueAt("table2", ret091HasData ? retSale05M091[0][11].trim() : "", sRow, "Address"); // ��}
    setValueAt("table2", ret091HasData ? retSale05M091[0][12].trim() : "", sRow, "Cellphone"); // ��ʹq��
    setValueAt("table2", ret091HasData ? retSale05M091[0][13].trim() : "", sRow, "Tel"); // �q��
    setValueAt("table2", ret091HasData ? retSale05M091[0][14].trim() : "", sRow, "Tel2"); // �q��2
    setValueAt("table2", ret091HasData ? retSale05M091[0][15].trim() : "", sRow, "eMail"); // EMAIL
    setValueAt("table2", ret091HasData ? retSale05M091[0][16].trim() : "", sRow, "IsBlackList"); // �¦W��
    setValueAt("table2", ret091HasData ? retSale05M091[0][17].trim() : "", sRow, "IsControlList"); // ���ަW��
    setValueAt("table2", ret091HasData ? retSale05M091[0][17].trim() : "", sRow, "IsLinked"); // �Q�`���Y�H

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

      setValueAt("table2", qName, sRow, "CustomNameNew");
      setValueAt("table2", bstatus, sRow, "IsBlackList");
      setValueAt("table2", cstatus, sRow, "IsControlList");
      setValueAt("table2", rstatus, sRow, "IsLinked");

      // �ܴ�Start
      String amlText = projectId + "," + orderNo + "," + orderDate + "," + funcName + "," + recordType + "," + value + "," + qName + "," + birthday + "," + indCode + ","
                     + "query1821";
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      tmpMsg = getValue("AMLText").trim();
      if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
      errMsg += tmpMsg;
      // �ܴ�END

      // �¦W�� + ���ަW��
      if ("Y".equals(bstatus) || "Y".equals(cstatus)) {
        tmpMsg = "�Ȥ�" + qName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
        if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
        errMsg += tmpMsg;
      }

      // �Q���H
      if ("Y".equals(rstatus)) {
        tmpMsg += "�Ȥ�" + qName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
        if(!StringUtils.contains(amlRsMix, tmpMsg)) setValue("AMLRsMix", amlRsMix + tmpMsg);  //�P�@�H�P�@�A�˥u��ܤ@��
        errMsg += tmpMsg;
      }

      //���
      if (!"".equals(errMsg)) {
        messagebox(errMsg);
      }
    } else {
      message("�¦W��t�εL����T�C");
    }

    return true;
  }

  public String getInformation() {
    return "---------------null(null).CustomNoNew.field_check()----------------";
  }
}
