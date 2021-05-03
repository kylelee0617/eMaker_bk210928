package Sale.Sale05M274;

import java.util.*;

import Farglory.aml.AMLyodsBean;
import Farglory.util.*;
import jcx.db.talk;

public class CheckRiskNew2021 extends jcx.jform.bproc {
  KUtils util = new KUtils();

  public String getDefaultValue(String value) throws Throwable {
    System.out.println("Class >>> Sale.Sale05M274.ChekRiskNew");

    talk dbSale = getTalk("Sale");
    talk dbEMail = getTalk("eMail");
    talk db400CRM = getTalk("400CRM");
    talk dbEIP = getTalk("EIP");
    talk dbPW0D = getTalk("pw0d");
    talk dbDoc = getTalk("Doc");
    KUtils kutil = new KUtils();

    // �e�����
    String strProjectID1 = getValue("ProjectID1").trim(); // �קO�N�X
    String strContractDate = getValue("ContractDate").trim(); // ñ�����
    String contractNo = getValue("ContractNoDisplay").trim(); // �X���s��
    boolean reCheckRisk = false; // �O�_���s�ˮ�
    String rsMsg = ""; // ��ܰT��
    String sql = "";

    // �q���T
    // 210112 Kyle : ��\���T�{�L �X��:�q�� = 1:1�A�ҥH�������Ĥ@��
    sql = "select top 1 a.OrderNo , OrderDate from Sale05M090 a , Sale05M275_New b where a.OrderNo = b.OrderNo and b.ContractNo = '" + contractNo + "' order by orderDate ";
    String[][] retOrder = dbSale.queryFromPool(sql);
    String strOrderNo = retOrder[0][0].toString().trim();
    String strOrderDate = retOrder[0][1].toString().trim();

    AMLyodsBean aBean = new AMLyodsBean();
    aBean.setDbSale(dbSale);
    aBean.setDbEMail(dbEMail);
    aBean.setDb400CRM(db400CRM);
    aBean.setDbEIP(dbEIP);
    aBean.setEmakerUserNo(getUser());
    aBean.setOrderNo(strOrderNo);
    aBean.setOrderDate(strOrderDate);
    aBean.setContractNo(contractNo);
    aBean.setProjectID1(strProjectID1);
    aBean.setActionName("�s�W");
    aBean.setFunc("�X��");
    aBean.setRecordType("���w�ĤT�H���I�ȭp�⵲�G");
    aBean.setUpdSale05M356(true);
    aBean.setUpd070Log(true);
    aBean.setSendMail(true);

    // TODO: 1. ��������w�ĤT�H���I�ȧP�O
    sql = "SELECT DesignatedId, DesignatedName, ExportingPlace  FROM sale05m356 WHERE ContractNo = '" + contractNo + "'";
    String[][] retM356 = dbSale.queryFromPool(sql);
    for (int ii = 0; ii < retM356.length; ii++) {
      String desNo = retM356[ii][0].toString().trim();
      String desName = retM356[ii][1].toString().trim();
      QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(strProjectID1, desNo);
      if (qBean == null) {
        rsMsg += "���w�ĤT�H" + desName + "�d�L�¦W���ơA�Х�����¦W��d�� \n";
        continue;
      }

      String birth = qBean.getBirthday().length() == 0 ? " " : qBean.getBirthday();
      String ind = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
      String amlText = strOrderNo + "," + strOrderDate + "," + desNo + "," + desName + "," + birth + "," + ind + "," + "custListRiskCheck";
      setValue("AMLText", amlText);
      getButton("BtCustAML").doClick();
      rsMsg += getValue("AMLText").trim();
    }
    // End of 1.

    
    // 2. ����Ȥ᭷�I���ˮ�
    long subDays = util.subACDaysRDay(strContractDate, strOrderDate);
    System.out.println("subDays>>>" + subDays);
    if (subDays >= 90)
      reCheckRisk = true; // �I�q��Pñ����W�L90��A�ݭ��s�ˮ֭��I
    
    if(reCheckRisk) {
      sql = "select a.CustomNo , a.CustomName , a.Birthday, a.IndustryCode from Sale05M091 a where a.OrderNo = '" + strOrderNo
          + "' " + "and ISNULL(a.StatusCd , '') != 'C'";
      String[][] retCustomers = dbSale.queryFromPool(sql);
      for (int ii = 0; ii < retCustomers.length; ii++) {
        String customNo = retCustomers[ii][0].toString().trim();
        String customName = retCustomers[ii][1].toString().trim();
        String birth = retCustomers[ii][2].toString().trim().length() == 0 ? " " : retCustomers[ii][2].toString().trim();
        String ind = retCustomers[ii][3].toString().trim().length() == 0 ? " " : retCustomers[ii][3].toString().trim();
        String amlText = strOrderNo + "," + strOrderDate + "," + customNo + "," + customName + "," + birth + "," + ind + "," + "custListRiskCheck";
        setValue("AMLText", amlText);
        getButton("BtCustAML").doClick();
        rsMsg += getValue("AMLText").trim();
      }
    }

    messagebox(rsMsg);

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
