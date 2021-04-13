package Sale.util;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.util.Result;

public class SendMailAction extends bproc {

  talk dbSale = getTalk("Sale");
  talk dbEIP = getTalk("EIP");
  talk dbEMail = getTalk("eMail");
  String stringSQL = "";
  String userNo = getUser().toUpperCase().trim();
  String empNo = "";
  String userEmail = "";
  String userEmail2 = "";
  String DPCode = "";
  String DPManageemNo = "";
  String DPeMail = "";
  String DPeMail2 = "";
  String[][] retEip = null;
  String[][] reteMail = null;
  String PNMail = "";
  String testRemark = "(����)"; // �b�������ҭn�[�����զr��
  String testPGMail = "Kyle_Lee@fglife.com.tw"; // �������ұH�e����mail

  // �e����
  String strProjectID1 = ""; // �קO�N�X
  String strOrderDate = ""; // �I�q���
  String strOrderNo = ""; // �q��s��
  String strPosition = "";
  String strCustomName = "";
  String errMsgText = "";

  public SendMailAction() {
    // 20200508 kyle Add �ھڦ��A���O�_�[���հT��
    String serverIP = get("serverIP").toString().trim();
    System.out.println("serverIP>>>" + serverIP);

    if (serverIP.contains("172.16.14.4")) {
      testRemark = "";
      testPGMail = "";
      System.out.println(">>>��������<<<");
    }
  }

  public String getDefaultValue(String value) throws Throwable {

    System.out.println("==============�~�����v�ˮ�SENDMAIL STAR====================================");

    this.getSendUser();
    this.getFormData();

    // 20200511 - kyle mod : �ǥѤ��P�ǤJvalue���椣�Pmail�ʧ@�A�������X��result�Avlaue�٭쬰�w�]��
    if ("rReview".equals(value)) {
      // �w�f��
      System.out.println(">>>send email kk");
      String[] biRs = getValue("BuyedResult").trim().split(",");
      Result rs = this.sendRreviewMail(biRs[1]);
      if (rs.getReturnCode() != 0) {
        messagebox(rs.getReturnMsg());
      }
      System.out.println(">>>mail RS:" + rs.getReturnMsg());
    } else {
      // send email
      System.out.println(">>>send email old");
      // ����W��
      if (errMsgText.indexOf("����W��") >= 0) {
        String msg2 = "�@�B���ʲ������T�G<BR><BR>1. ��    �O�G<u>" + strProjectID1 + "</u>&emsp;2. �ɼӧO�G<u>" + strPosition + "</u>&emsp;3. �Ȥ�m�W�G<u>" + strCustomName + "</u>&emsp;4. �I�q����G<u>"
            + strOrderDate + "</u>&emsp;5. �ʫε��������G<u>" + strOrderDate + "</u><BR><BR>�G�B�ŦX�æ��~���A�˳q���G<BR><BR>�Ȥ�" + strCustomName + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC";
        msg2 = msg2.replace("\n", "<BR>");

        String subject2 = strProjectID1 + "��" + strPosition + "���ʲ�����ŦX�æ��~���θꮣ�A�˨t�γq��" + testRemark;
        String[] arrayUser2 = { "Justin_Lin@fglife.com.tw", userEmail, DPeMail, PNMail };
        String sendRS2 = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser2, subject2, msg2, null, "", "text/html");

        System.out.println("sendRS2===>" + sendRS2);
      } else {
        String msg = "�@�B���ʲ������T�G<BR><BR>1. ��    �O�G<u>" + strProjectID1 + "</u>&emsp;2. �ɼӧO�G<u>" + strPosition + "</u>&emsp;3. �Ȥ�m�W�G<u>" + strCustomName + "</u>&emsp;4. �I�q����G<u>"
            + strOrderDate + "</u>&emsp;5. �ʫε��������G<u>" + strOrderDate + "</u><BR><BR>�G�B�ŦX�æ��~���A�˳q���G<BR><BR>" + errMsgText;
        msg = msg.replace("\n", "<BR>");
        String subject = strProjectID1 + "��" + strPosition + "���ʲ�����ŦX�æ��~���θꮣ�A�˨t�γq��" + testRemark;
        String[] arrayUser = { "Justin_Lin@fglife.com.tw", userEmail };
        String sendRS = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser, subject, msg, null, "", "text/html");

        System.out.println("sendRS===>" + sendRS);
      }
    }

    System.out.println("==============�~�����v�ˮ�SENDMAIL END====================================");

    return "sendMail";
  }

  /*
   * 20200507 kyle add msg = �T�����e
   */
  public Result sendRreviewMail(String msg) throws Throwable {
    Result rs = new Result();

    try {
      String msg2 = "�קO�N�X�G" + strProjectID1 + "<BR>�ɼӧO�G" + strPosition + "<BR>�q��m�W�G" + strCustomName + "<BR>�I�q����G" + strOrderDate + "<BR>ĵ�ܰT�� : " + msg.replaceAll("\n", "<BR>");
      String subject2 = "�ʫ��ҩ���  �קO�G" + strProjectID1 + "  �ɼӧO�G" + strPosition + "  �Ȥ᥼�����w�f�q��" + testRemark;
      String[] arrayUser2 = { testPGMail, userEmail, DPeMail, PNMail };
      String sendRS2 = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser2, subject2, msg2, null, "", "text/html");
      if ("".equals(sendRS2)) {
        rs.setReturnMsg("�o�eMail���\");
      } else {
        rs.setReturnCode(93);
        rs.setReturnMsg(sendRS2);
      }
    } catch (Exception ex) {
      rs.setReturnCode(92);
      rs.setReturnMsg("�o�eMail���~!!");
      rs.setExp(ex);
    }

    return rs;
  }

  public void getSendUser() throws Throwable {
    // �ӿ�ID
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }

    // �ӿ�EMAIL
    stringSQL = "SELECT DP_CODE,PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPCode = reteMail[0][0];
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        userEmail = reteMail[0][1];
      }
      if (reteMail[0][2] != null && !reteMail[0][2].equals("")) {
        userEmail2 = reteMail[0][2];
      }
    }

    // ���ID
    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + DPCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPManageemNo = reteMail[0][0];
    }

    // ���MAIL
    stringSQL = "SELECT PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + DPManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      if (reteMail[0][0] != null && !reteMail[0][0].equals("")) {
        DPeMail = reteMail[0][0];
      }
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        DPeMail2 = reteMail[0][1];
      }
    }

    // ����
    String PNCode = "";
    String PNManageemNo = "";

    stringSQL = "SELECT PN_DEPTCODE FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNCode = reteMail[0][0];

    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + PNCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNManageemNo = reteMail[0][0];

    stringSQL = "SELECT PN_EMAIL1 FROM PERSONNEL WHERE PN_EMPNO='" + PNManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNMail = reteMail[0][0];
  }

  public void getFormData() throws Throwable {
    // ���e����
    strProjectID1 = getValue("field1").trim(); // �קO�N�X
    strOrderDate = getValue("field2").trim(); // �I�q���
    strOrderNo = getValue("field3").trim(); // �q��s��
    errMsgText = getValue("errMsgBoxText").trim();

    strPosition = getTableData("table2")[0][3].toString().trim();
    strCustomName = getTableData("table1")[0][6].toString().trim();
  }

  public String getInformation() {
    return "---------------emailTestBtn(emailTestBtn).defaultValue()----------------";
  }
}
