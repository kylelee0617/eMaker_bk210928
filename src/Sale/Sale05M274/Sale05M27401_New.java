package Sale.Sale05M274;

import jcx.jform.bTransaction;
import jcx.util.*;
import jcx.db.*;
import javax.swing.*;
import Farglory.util.*;
import java.util.*;

public class Sale05M27401_New extends bTransaction {
  talk dbSale = getTalk("" + get("put_dbSale"));
  talk dbDoc = getTalk("" + get("put_dbDoc"));
  MLPUtils mlpUtils = new MLPUtils();

  String stringIsSFlow1 = ""; // �ק���:20161024 ���u�s��:B3774

  public boolean action(String value) throws Throwable {
    // 201808check BEGIN
    System.out.println("chk==>" + getUser() + " , value==>" + value.trim());
    if ("�s�W".equals(value.trim()) || "�ק�".equals(value.trim()) || "�R��".equals(value.trim())) {
      if (getUser() != null && getUser().toUpperCase().equals("B9999")) {
        messagebox(value.trim() + "�v�������\!!!");
        return false;
      }
    }
    // 201808check FINISH
    // �^�ǭȬ� true ��ܰ��汵�U�Ӫ���Ʈw���ʩάd��
    // �^�ǭȬ� false ��ܱ��U�Ӥ����������O
    // �ǤJ�� value �� "�s�W","�d��","�ק�","�R��","�C�L","PRINT" (�C�L�w�����C�L���s),"PRINTALL"
    // (�C�L�w���������C�L���s) �䤤���@
    message("");
    // Start �ק���:20161024 ���u�s��:B3774
    String stringSProjectID11 = (String) get("put_stringSProjectID11");
    System.out.println("stringSProjectID11=" + stringSProjectID11);
    //
    if (stringSProjectID11.indexOf(getValue("ProjectID1").trim()) != -1) {
      put("put_stringIsSFlow1", "Y");
      stringIsSFlow1 = "Y";
    } else {
      put("put_stringIsSFlow1", "N");
      stringIsSFlow1 = "N";
    }
    System.out.println("stringIsSFlow1=" + get("put_stringIsSFlow1"));
    if ("Y".equals(getValue("IsVoid"))) {
      messagebox("�еo�u�p��q���������");
      return true;
    }
    if ("Y".equals(getValue("IsClickbtnGetBack"))) {
      return true;
    }
    if (!isBatchCheckOK(value)) {
      return false;
    }
    return true;
  }

  public boolean isBatchCheckOK(String stringValue) throws Throwable {
    if ("�s�W".equals(stringValue) || "�ק�".equals(stringValue)) {
      String LastUser = getUser().toUpperCase();
      String LastDateTime = datetime.getTime("YYYY/mm/dd h:m:s");
      setValue("LastUser", LastUser);
      setValue("LastDateTime", LastDateTime);
      //
      String stringSql = "";
      String retData[][] = null;
      // �קO
      String stringProjectID1 = getValue("ProjectID1").trim();
      if (stringProjectID1.length() == 0) {
        message("[�קO] ���i���ť�!");
        getcLabel("ProjectID1").requestFocus();
        return false;
      }
      // Start �ק���:20100412 ���u�s��:B3774
      if ("H38".equals(stringProjectID1)) {
        stringProjectID1 = "H38A";
      }
      // End �ק���:20100412 ���u�s��:B3774
      stringSql = "select ProjectID1 " + "from A_Group " +
      // Start �ק���:20100412 ���u�s��:B3774
      // "where ProjectID1='"+stringProjectID1+"'";
          "where ProjectID1='" + (stringProjectID1.equals("H38A") ? "H38" : stringProjectID1) + "'";
      // End �ק���:20100412 ���u�s��:B3774
      String retAGroup[][] = dbSale.queryFromPool(stringSql);
      if (retAGroup.length == 0) {
        message("[�קO] ���~!");
        getcLabel("ProjectID1").requestFocus();
        return false;
      }
      // Start �ק���:20140718 ���u�s��:B3774
      // �X������
      String stringContractType = getValue("ContractType").trim();
      if (stringContractType.length() == 0) {
        message("[�X������] ���i���ť�!");
        getcLabel("ContractType").requestFocus();
        return false;
      }
      // End �ק���:20140718 ���u�s��:B3774
      String stringStatus = getValue("Status").trim(); // �ק���:20150811 ���u�s��:B3774
      // ñ�����
      String stringContractDate = getValue("ContractDate").trim();
      if (stringContractDate.length() == 0) {
        message("[ñ�����] ���i���ť�!");
        getcLabel("ContractDate").requestFocus();
        return false;
      } else {
        if (stringContractDate.replaceAll("/", "").length() != 8) {
          message("[ñ�����] �榡���~(YYYY/MM/DD) !");
          getcLabel("ContractDate").requestFocus();
          return false;
        }
        Farglory.util.FargloryUtil exeFun = new Farglory.util.FargloryUtil();
        stringContractDate = exeFun.getDateAC(stringContractDate, "ñ�����");
        if (stringContractDate.length() != 10) {
          message(stringContractDate);
          getcLabel("ContractDate").requestFocus();
          return false;
        }
        setValue("ContractDate", stringContractDate);
        // Start �ק���:20091207 ���u�s��:B3774
        // Start �ק���:20110218 ���u�s��:B3774
        // if(!stringContractDate.equals(getValue("ContractDate_Old"))){
        // Start �ק���:20151120 ���u�s��:B3774
        // if(!("G".equals(stringStatus) || "GA".equals(stringStatus) ||
        // "I".equals(stringStatus))){ // �ק���:20150811 ���u�s��:B3774
        if (!("G".equals(stringStatus) || "GA".equals(stringStatus) || "GB".equals(stringStatus) || "I".equals(stringStatus))) {
          // End �ק���:20151120 ���u�s��:B3774
          if ("�s�W".equals(stringValue) || !stringContractDate.equals(getValue("ContractDate_Old"))) {
            // End �ק���:20110218 ���u�s��:B3774
            getButton("btnLoadNewContractNo").doClick();
          }
        } // �ק���:20150811 ���u�s��:B3774
        setValue("ContractDate_Old", stringContractDate);
        // End �ק���:20091207 ���u�s��:B3774
      }
      // ñ���������
      // String stringStatus = getValue("Status").trim(); // �ק���:20150811 ���u�s��:B3774
      String stringCashInDate = getValue("CashInDate").trim();
      // Start �ק���:20100317 ���u�s��:B3774
      // if("N".equals(stringStatus) && stringCashInDate.length() == 0){
      // Start �ק���:20151120 ���u�s��:B3774
      // if(("N".equals(stringStatus) || "A".equals(stringStatus)) &&
      // stringCashInDate.length() == 0){
      if (("N".equals(stringStatus) || "NA".equals(stringStatus) || "A".equals(stringStatus)) && stringCashInDate.length() == 0) {
        // End �ק���:20151120 ���u�s��:B3774
        // End �ק���:20100317 ���u�s��:B3774
        message("[ñ���������] ���i���ť�!");
        getcLabel("CashInDate").requestFocus();
        return false;
      }
      if (stringCashInDate.length() > 0) {
        if (stringCashInDate.replaceAll("/", "").length() != 8) {
          message("[ñ���������] �榡���~(YYYY/MM/DD) !");
          getcLabel("CashInDate").requestFocus();
          return false;
        }
        Farglory.util.FargloryUtil exeFun = new Farglory.util.FargloryUtil();
        stringCashInDate = exeFun.getDateAC(stringCashInDate, "ñ���������");
        if (stringCashInDate.length() != 10) {
          message(stringCashInDate);
          getcLabel("CashInDate").requestFocus();
          return false;
        }
        setValue("CashInDate", stringCashInDate);
      }
      // Start �ק���:20150811 ���u�s��:B3774
      // ���P���
      String stringTransferDate = getValue("TransferDate").trim();
      String stringFieldName = ((JLabel) getcLabel("TransferDate").getComponent(1)).getText().trim(); // �ק���:20151123 ���u�s��:B3774
      // Start �ק���:20151120 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "GA".equals(stringStatus) ||
      // "I".equals(stringStatus)) && stringTransferDate.length() == 0){
      if (("G".equals(stringStatus) || "GA".equals(stringStatus) || "GB".equals(stringStatus) || "I".equals(stringStatus)) && stringTransferDate.length() == 0) {
        // End �ק���:20151120 ���u�s��:B3774
        // Start �ק���:20151123 ���u�s��:B3774
        // message("[���P���] ���i���ť�!");
        message("[" + stringFieldName + "] ���i���ť�!");
        // End �ק���:20151123 ���u�s��:B3774
        getcLabel("TransferDate").requestFocus();
        return false;
      }
      // Start �ק���:20160816 ���u�s��:B3774
      if ("NA".equals(stringStatus)) {
        message("[" + stringFieldName + "] ���i���ť�!");
        getcLabel("TransferDate").requestFocus();
        return false;
      }
      // End �ק���:20160816 ���u�s��:B3774
      if (stringTransferDate.length() > 0) {
        if (stringTransferDate.replaceAll("/", "").length() != 8) {
          // Start �ק���:20151123 ���u�s��:B3774
          // message("[���P���] �榡���~(YYYY/MM/DD) !");
          message("[" + stringFieldName + "] �榡���~(YYYY/MM/DD) !");
          // End �ק���:20151123 ���u�s��:B3774
          getcLabel("TransferDate").requestFocus();
          return false;
        }
        Farglory.util.FargloryUtil exeFun = new Farglory.util.FargloryUtil();
        // Start �ק���:20151123 ���u�s��:B3774
        // stringTransferDate = exeFun.getDateAC(stringTransferDate, "���P���");
        stringTransferDate = exeFun.getDateAC(stringTransferDate, stringFieldName);
        // End �ק���:20151123 ���u�s��:B3774
        if (stringTransferDate.length() != 10) {
          message(stringTransferDate);
          getcLabel("TransferDate").requestFocus();
          return false;
        }
        setValue("TransferDate", stringTransferDate);
        // Start �ק���:20151120 ���u�s��:B3774
        // if("G".equals(stringStatus) || "GA".equals(stringStatus) ||
        // "I".equals(stringStatus)){
        if ("G".equals(stringStatus) || "GA".equals(stringStatus) || "GB".equals(stringStatus) || "I".equals(stringStatus)) {
          // End �ק���:20151120 ���u�s��:B3774
          if ("�s�W".equals(stringValue) || !stringTransferDate.equals(getValue("TransferDate_Old"))) {
            getButton("btnLoadNewContractNo").doClick();
          }
        }
        // Start �ק���:20160816 ���u�s��:B3774
        if ("NA".equals(stringStatus)) {
          if ("�s�W".equals(stringValue) || !stringTransferDate.equals(getValue("TransferDate_Old"))) {
            getButton("btnLoadNewContractNo").doClick();
          }
        }
        // End �ק���:20160816 ���u�s��:B3774
        setValue("TransferDate_Old", stringTransferDate);
      }
      // End �ק���:20150811 ���u�s��:B3774
      // �A�P�_�קO�Bñ������B���P����O�_���Q�ܰʹL
      if ("�s�W".equals(stringValue)) {
        // Start �ק���:20150811 ���u�s��:B3774
        // if(!stringProjectID1.equals(getValue("ProjectID1_Old")) ||
        // !stringContractDate.equals(getValue("ContractDate_Old"))){
        if (!stringProjectID1.equals(getValue("ProjectID1_Old")) || !stringContractDate.equals(getValue("ContractDate_Old"))
            || !stringTransferDate.equals(getValue("TransferDate_Old"))) {
          // End �ק���:20150811 ���u�s��:B3774
          getButton("btnClearData").doClick();
        }
      }
      // �X���s��
      String stringContractNo = getValue("ContractNo").trim();
      if (stringContractNo.length() == 0) {
        message("[�X���s��] ���i���ť�!");
        return false;
      }
      // Start �ק���:20120517 ���u�s��:B3774
      // ���q�O
      String stringCompanyCd = getValue("CompanyCd").trim();
      if (stringCompanyCd.length() == 0) {
        message("[���q�O] ���i���ť�!");
        return false;
      }
      // End �ק���:20120517 ���u�s��:B3774
      // �X���ѽs��
      String stringContractSerialNo = getValue("ContractSerialNo").trim();
      if (stringContractSerialNo.length() == 0) {
        message("[�X���ѽs��] ���i���ť�!");
        getcLabel("ContractSerialNo").requestFocus();
        return false;
      }
      // �ʫ��ҩ�����
      JTable jtable1 = getTable("table1");
      if (jtable1.getRowCount() == 0) {
        message("�п�J�ʫ��ҩ�������!");
        return false;
      }

      // Start �ק���:20091229 ���u�s��:B3774
      String stringPosition = "";
      String stringIsTrust = ""; // �ק���:20110803 ���u�s��:B3774
      for (int intRow = jtable1.getRowCount() - 1; intRow >= 0; intRow--) {
        stringPosition = ("" + getValueAt("table1", intRow, "Position")).trim();
        if (stringPosition.length() == 0) {
          jtable1.setRowSelectionInterval(intRow, intRow);
          getTableButton("table1", 2).doClick();
        }
      }
      // End �ק���:20091229 ���u�s��:B3774

      // �ˮ֭q��s������T
      StringBuilder orderNos = new StringBuilder();
      for (int intRow = 0; intRow < jtable1.getRowCount(); intRow++) {
        stringIsTrust = ("" + getValueAt("table1", intRow, "IsTrust")).trim();
        if (intRow > 0) {
          if (!stringIsTrust.equals(("" + getValueAt("table1", intRow - 1, "IsTrust")).trim())) {
            message("�ʫ��ҩ����檺 [�H�U��] ���@�P !");
            return false;
          }
        }
        setValueAt("table1", stringContractNo, intRow, "ContractNo");
        setValueAt("table1", stringCompanyCd, intRow, "CompanyCd");
        // ��ϥΪ̤ήɶ�
        setValueAt("table1", LastUser, intRow, "LastUser");
        setValueAt("table1", LastDateTime, intRow, "LastDateTime");

        // 20200615 Kyle : ���o�q��s�����X
        if (intRow != 0)
          orderNos.append(",");
        orderNos.append("'").append(("" + getValueAt("table1", intRow, "OrderNo")).trim()).append("'");
      }

      // 20200615 Kyle : �ˮ֫Ȥ�O�_PEPS or ����W��
      String[][] retCustomData = mlpUtils.getCustomerInfo(orderNos.toString());
      Map amlCons = new HashMap();
      amlCons.put("ProjectID1", stringProjectID1);
      amlCons.put("TrxDate", stringContractDate);
      amlCons.put("funcName", "�X���|�f");
      amlCons.put("funcName2", "���");
      String[] amlRsMsg = mlpUtils.checkCustomer_018_021(retCustomData, amlCons);
      if (amlRsMsg.length > 0 && amlRsMsg[0].trim().length() != 0) {
//        if (amlRsMsg[0].trim().length() != 0) {
          messagebox(amlRsMsg[0].trim() + amlRsMsg[1].trim());
          return false;
//        }
      }

      // �X�ͤ��
      String stringBirthday = getValue("Birthday").trim();
      if (stringBirthday.length() > 0) {
        if (stringBirthday.replaceAll("/", "").length() != 8) {
          message("[�X�ͤ��] �榡���~(YYYY/MM/DD) !");
          getcLabel("Birthday").requestFocus();
          return false;
        }
        Farglory.util.FargloryUtil exeFun = new Farglory.util.FargloryUtil();
        stringBirthday = exeFun.getDateAC(stringBirthday, "�X�ͤ��");
        if (stringBirthday.length() != 10) {
          message(stringBirthday);
          getcLabel("Birthday").requestFocus();
          return false;
        }
        setValue("Birthday", stringBirthday);
      }

      // ���X
      // Start �ק���:20091225 ���u�s��:B3774
      String stringBarCode = "";
      if ("�s�W".equals(stringValue)) {
        // �����e����� Sale05M274 ���� BarCode �̤j�ȡA�P Doc1M030 �O�_�@�P
        // ��� START
        stringSql = "select isnull('W'+right(convert(char(6),100000+right(max(BarCode),5)),5),'W00001') " + "from Doc1M030 " + "where BarCode like 'W%'";
        retData = dbDoc.queryFromPool(stringSql);
        String doc1M030Barcode = retData[0][0];
        stringSql = "select isnull('W'+right(convert(char(6),100000+right(max(BarCode),5)),5),'W00001') " + "from Sale05M274 " + "where BarCode like 'W%'";
        retData = dbSale.queryFromPool(stringSql);
        String sale05M274Barcode = retData[0][0];
        if (!doc1M030Barcode.equals(sale05M274Barcode)) {
          int saleBarCodeNum = Integer.parseInt(sale05M274Barcode.substring(1));
          int docBarCodeNum = Integer.parseInt(doc1M030Barcode.substring(1));
          if (saleBarCodeNum > docBarCodeNum) {
            stringSql = "INSERT INTO Doc1M030 (BarCode,CDate,CTime,EDateTime,PreFinDate,KindNo," + "ComNo,DepartNo,EmployeeNo,DocNo1,DocNo2,DocNo3,DocNo,DocClose,Descript,"
                + "OriEmployeeName,LastDepart,LastDateTime,InOut,Depart,ProjectID,ProjectID1,CostID,"
                + "RealMoney,Remark,Hand,RecordNo,DocStatus,DocSpeed,KindNoD,PurConfirm,Urgent) " + "VALUES " + "('" + sale05M274Barcode
                + "','107/02/01','16:27:13','2018/02/01 16:27:13   ','2018/02/02','58','20','033H93A   '"
                + ",'B3358 ','033H93A   ','10702','004','033H93A10702004','N','A01F05,A20001,A20002,�����V#2020'"
                + ",'�P����','033H93A   ','2018/02/01 16:27:13   ',null,'0',null,null,null,null,null,'N',null,'1',null,'58  ',null,null);";
            dbDoc.execFromPool(stringSql);
          }
        }
        // ��� END
        stringSql = "select isnull('W'+right(convert(char(6),100001+right(max(BarCode),5)),5),'W00001') " + "from Doc1M030 " + "where BarCode like 'W%'";
        retData = dbDoc.queryFromPool(stringSql);
        stringBarCode = retData[0][0];
        setValue("BarCode", stringBarCode);
      }
      // End �ק���:20091225 ���u�s��:B3774

      // ��X�H���
      JTable jtable18 = getTable("table18");
      for (int intRow = 0; intRow < jtable18.getRowCount(); intRow++) {
        setValueAt("table18", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table18", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table18", LastUser, intRow, "LastUser");
        setValueAt("table18", LastDateTime, intRow, "LastDateTime");
      }

      // �q�����p�X��
      JTable jtable2 = getTable("table2");
      for (int intRow = 0; intRow < jtable2.getRowCount(); intRow++) {
        setValueAt("table2", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table2", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table2", LastUser, intRow, "LastUser");
        setValueAt("table2", LastDateTime, intRow, "LastDateTime");
      }
      // �ɼӸ��
      JTable jtable4 = getTable("table4");
      if (jtable4.getRowCount() == 0) {
        message("�ɼӪ���Ƥ��i����!");
        return false;
      }
      String stringDealMoney = "0";
      // Start �ק���:20161017 ���u�s��:B3774
      String stringtable4CommMoneySum = "0";
      String stringtable4CommMoney1Sum = "0";
      // End �ק���:20161017 ���u�s��:B3774
      for (int intRow = 0; intRow < jtable4.getRowCount(); intRow++) {
        // ����[�`
        stringDealMoney = operation.floatAdd(stringDealMoney, "" + getValueAt("table4", intRow, "DealMoney"), 4);
        //
        // Start �ק���:20161017 ���u�s��:B3774
        stringtable4CommMoneySum = operation.floatAdd(stringtable4CommMoneySum, "" + getValueAt("table4", intRow, "CommMoney"), 4);
        stringtable4CommMoney1Sum = operation.floatAdd(stringtable4CommMoney1Sum, "" + getValueAt("table4", intRow, "CommMoney1"), 4);
        // End �ק���:20161017 ���u�s��:B3774
        //
        setValueAt("table4", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table4", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table4", LastUser, intRow, "LastUser");
        setValueAt("table4", LastDateTime, intRow, "LastDateTime");
      }
      // Start �ק���:20100412 ���u�s��:B3774
      // �X�����n�Ϊ��B
      JTable jtable20 = getTable("table20");
      if (jtable20.getRowCount() == 0) {
        // message("�X�����n�Ϊ��B��Ƥ��i����!");
        // return false;
      }
      for (int intRow = 0; intRow < jtable20.getRowCount(); intRow++) {
        setValueAt("table20", stringContractNo, intRow, "ContractNo"); // �ק���:20120517 ���u�s��:B3774
        setValueAt("table20", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table20", LastUser, intRow, "LastUser");
        setValueAt("table20", LastDateTime, intRow, "LastDateTime");
      }
      // End �ק���:20100412 ���u�s��:B3774
      // �ɼөФg���
      JTable jtable14 = getTable("table14");
      if (jtable14.getRowCount() == 0) {
        message("�ɼөФg����Ƥ��i����!");
        return false;
      }
      for (int intRow = 0; intRow < jtable14.getRowCount(); intRow++) {
        setValueAt("table14", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table14", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table14", LastUser, intRow, "LastUser");
        setValueAt("table14", LastDateTime, intRow, "LastDateTime");
      }
      // �P�_�y�{�]�w�O�_����
      /*
       * boolean blnIsAll01 = true; boolean blnIsAllCS = true; boolean blnHas01 =
       * false; boolean blnHasCS = false; int intExtraStates = 4; for(int intRow=0;
       * intRow<jtable14.getRowCount(); intRow++){
       * if("01".equals(""+jtable14.getValueAt(intRow, 5))){ blnHas01 = true;
       * blnIsAllCS = false; }else if("CS".equals(""+jtable14.getValueAt(intRow, 5))){
       * blnHasCS = true; blnIsAll01 = false; }else{ blnIsAll01 = false; blnIsAllCS =
       * false; } } if(blnIsAll01){ intExtraStates = 4; }else if(blnIsAllCS ||
       * (blnHasCS && !blnHas01)){ intExtraStates = 2; }else if(blnHas01 && blnHasCS){
       * intExtraStates = 2; }else{ intExtraStates = 4; } stringSql =
       * "SELECT count(distinct CensorSeq) "+ "FROM Sale05M225 "+
       * "WHERE FlowFormID='Sale05M27401' "+ "and ProjectID1='"+stringProjectID1+"'";
       * retData = dbSale.queryFromPool(stringSql); // ������X�Ҧ��`�I,
       * ��ӿ�BEnd�F�D�H�حn�h����ʲ��B�|�p // Start �ק���:20090819 ���u�s��:B3774
       * //if(Integer.parseInt(retData[0][0]) !=
       * (getFlowStates().size()-intExtraStates)){ if(Integer.parseInt(retData[0][0])
       * < (getFlowStates().size()-intExtraStates)){ // End
       * message("�קO��ñ�֬y�{�]�w��Ƥ�����!"); return false; }
       */
      // �U���q�O�X���s��
      JTable jtable17 = getTable("table17");
      if (jtable17.getRowCount() == 0) {
        message("�U���q�O�X���s����ƿ��~!");
        return false;
      }
      for (int intRow = 0; intRow < jtable17.getRowCount(); intRow++) {
        setValueAt("table17", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table17", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table17", LastUser, intRow, "LastUser");
        setValueAt("table17", LastDateTime, intRow, "LastDateTime");
      }
      // �Ȥ���
      JTable jtable3 = getTable("table3");
      if (jtable3.getRowCount() == 0) {
        message("�Ȥ����Ƥ��i����!");
        return false;
      }
      for (int intRow = 0; intRow < jtable3.getRowCount(); intRow++) {
        setValueAt("table3", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table3", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table3", LastUser, intRow, "LastUser");
        setValueAt("table3", LastDateTime, intRow, "LastDateTime");
      }
      // Start �ק���:20121213 ���u�s��:B3774
      // ����n��-�g�a
      JTable jtable21 = getTable("table21");
      for (int intRow = 0; intRow < jtable21.getRowCount(); intRow++) {
        setValueAt("table21", stringContractNo, intRow, "ContractNo");
        setValueAt("table21", stringCompanyCd, intRow, "CompanyCd");
        // ��ϥΪ̤ήɶ�
        setValueAt("table21", LastUser, intRow, "LastUser");
        setValueAt("table21", LastDateTime, intRow, "LastDateTime");
      }
      // ����n��-�ت�
      JTable jtable22 = getTable("table22");
      for (int intRow = 0; intRow < jtable22.getRowCount(); intRow++) {
        setValueAt("table22", stringContractNo, intRow, "ContractNo");
        setValueAt("table22", stringCompanyCd, intRow, "CompanyCd");
        // ��ϥΪ̤ήɶ�
        setValueAt("table22", LastUser, intRow, "LastUser");
        setValueAt("table22", LastDateTime, intRow, "LastDateTime");
      }
      // ����n��-����
      JTable jtable23 = getTable("table23");
      for (int intRow = 0; intRow < jtable23.getRowCount(); intRow++) {
        setValueAt("table23", stringContractNo, intRow, "ContractNo");
        setValueAt("table23", stringCompanyCd, intRow, "CompanyCd");
        // ��ϥΪ̤ήɶ�
        setValueAt("table23", LastUser, intRow, "LastUser");
        setValueAt("table23", LastDateTime, intRow, "LastDateTime");
      }
      // End �ק���:20121213 ���u�s��:B3774
      // Start �ק���:20161017 ���u�s��:B3774
      // ���ͦ����Τ������
      JTable jtable26 = getTable("table26");
      String stringFriendName = "";
      String stringFriendID = "";
      String stringCommMoney = "";
      String stringCommMoney1 = "";
      String stringtable26CommMoneySum = "0";
      String stringtable26CommMoney1Sum = "0";
      for (int intRow = 0; intRow < jtable26.getRowCount(); intRow++) {
        setValueAt("table26", stringContractNo, intRow, "ContractNo");
        setValueAt("table26", stringCompanyCd, intRow, "CompanyCd");
        // ����ID
        stringFriendID = ("" + getValueAt("table26", intRow, "FriendID")).trim();
        if (stringFriendID.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [����ID] ���i���ť�!");
          setFocus("table26", intRow, "FriendID");
          return false;
        }
        // ���ͩm�W
        stringFriendName = ("" + getValueAt("table26", intRow, "FriendName")).trim();
        if (stringFriendName.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [���ͩm�W] ���i���ť�!");
          setFocus("table26", intRow, "FriendName");
          return false;
        }
        // ����
        stringCommMoney = ("" + getValueAt("table26", intRow, "CommMoney")).trim();
        if (stringCommMoney.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [����] ���i���ť�!");
          setFocus("table26", intRow, "CommMoney");
          return false;
        }
        stringtable26CommMoneySum = operation.floatAdd(stringtable26CommMoneySum, stringCommMoney, 0);
        // �������
        stringCommMoney1 = ("" + getValueAt("table26", intRow, "CommMoney1")).trim();
        if (stringCommMoney1.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [�������] ���i���ť�!");
          setFocus("table26", intRow, "CommMoney1");
          return false;
        }
        stringtable26CommMoney1Sum = operation.floatAdd(stringtable26CommMoney1Sum, stringCommMoney1, 0);
        // ��ϥΪ̤ήɶ�
        setValueAt("table26", LastUser, intRow, "LastUser");
        setValueAt("table26", LastDateTime, intRow, "LastDateTime");
      }
      //
      if (datetime.subDays1(stringContractDate.replaceAll("/", ""), "20161101") >= 0) { // �ק���:20161213 ���u�s��:B3774
        if (operation.compareTo(operation.floatMultiply(stringtable4CommMoneySum, "10000", 0), stringtable26CommMoneySum) != 0) {
          message("���ͪ� [����] �X�p ������ �ʫ��ҩ��檺 [����] �X�p!");
          return false;
        }
        if (operation.compareTo(operation.floatMultiply(stringtable4CommMoney1Sum, "10000", 0), stringtable26CommMoney1Sum) != 0) {
          message("���ͪ� [�������] �X�p ������ �ʫ��ҩ��檺 [�������] �X�p!");
          return false;
        }
      } // �ק���:20161213 ���u�s��:B3774
        // End �ק���:20161017 ���u�s��:B3774
        // ��ڪ�
      JTable jtable10 = getTable("table10");
      String stringHLMoney = "0";
      String stringAmtPercent = "0"; // �ק���:20090827 ���u�s��:B3774
      for (int intRow = 0; intRow < jtable10.getRowCount(); intRow++) {
        // �Фg�X�p�[�`
        stringHLMoney = operation.floatAdd(stringHLMoney, "" + getValueAt("table10", intRow, "HL_Money"), 4);
        // Start �ק���:20090827 ���u�s��:B3774
        // �ʤ���[�`
        stringAmtPercent = operation.floatAdd(stringAmtPercent, "" + getValueAt("table10", intRow, "AMT_PERCENT"), 4);
        // End
        setValueAt("table10", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table10", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table10", LastUser, intRow, "LastUser");
        setValueAt("table10", LastDateTime, intRow, "LastDateTime");
      }
      // Start �ק���:20090827 ���u�s��:B3774
      // �ˮ֦ʤ���X�p�O�_��100
      // Start �ק���:20100104 ���u�s��:B3774
      /// * // �ק���:20170508 ���u�s��:B3774
      if (operation.compareTo(stringAmtPercent, "100") == 1) {
        message("��ڪ�ʤ���X�p������100%!");
        return false;
      }
      // */ // �ק���:20170508 ���u�s��:B3774
      // End �ק���:20100104 ���u�s��:B3774
      // End
      // �ˮ֩�ڪ��+��ڪ��[�`�M����O�_�@��
      if (operation.compareTo(stringDealMoney, stringHLMoney) == 1) {
        message("��ڪ�X�p��������!");
        return false;
      }
      // Start �ק���:20100226 ���u�s��:B3774
      // �ذe����
      JTable jtable7 = getTable("table7");
      for (int intRow = 0; intRow < jtable7.getRowCount(); intRow++) {
        setValueAt("table7", stringContractNo, intRow, "ContractNo");
        setValueAt("table7", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table7", LastUser, intRow, "LastUser");
        setValueAt("table7", LastDateTime, intRow, "LastDateTime");
      }
      // End �ק���:20100226 ���u�s��:B3774
      // �S�׽s��
      JTable jtable8 = getTable("table8");
      String stringDocNo1 = ""; // �ק���:20091225 ���u�s��:B3774
      String stringDocNo2 = "";
      String stringDocNo3 = "";
      for (int intRow = 0; intRow < jtable8.getRowCount(); intRow++) {
        // Start �ק���:20091225 ���u�s��:B3774
        stringDocNo1 = ("" + getValueAt("table8", intRow, "DocNo1")).trim();
        if (stringDocNo1.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [�S�׽s��]���i���ť� !");
          setFocus("table8", intRow, "DocNo1");
          return false;
        }
        // End �ק���:20091225 ���u�s��:B3774
        stringDocNo2 = ("" + getValueAt("table8", intRow, "DocNo2")).trim();
        if (stringDocNo2.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [�S�׽s��]���i���ť� !");
          setFocus("table8", intRow, "DocNo2");
          return false;
        }
        stringDocNo3 = ("" + getValueAt("table8", intRow, "DocNo3")).trim();
        if (stringDocNo3.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [�S�׽s��]���i���ť� !");
          setFocus("table8", intRow, "DocNo3");
          return false;
        }
        //
        setValueAt("table8", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table8", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table8", LastUser, intRow, "LastUser");
        setValueAt("table8", LastDateTime, intRow, "LastDateTime");
      }
      // �S�פ��e
      JTable jtable9 = getTable("table9");
      for (int intRow = 0; intRow < jtable9.getRowCount(); intRow++) {
        setValueAt("table9", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table9", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table9", LastUser, intRow, "LastUser");
        setValueAt("table9", LastDateTime, intRow, "LastDateTime");
      }
      // ���W�X��&�K���O���Y
      String stringChangeNameFee = getValue("ChangeNameFee").trim();
      // Start �ק���:20091222 ���u�s��:B3774
      // if("C".equals(stringStatus) && stringChangeNameFee.length() == 0){
      // Start �ק���:20091230 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      // Start �ק���:20100317 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "A".equals(stringStatus) || "I".equals(stringStatus)) &&
      // Start �ק���:20120504 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      // Start �ק���:20151120 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "GA".equals(stringStatus) ||
      // "C".equals(stringStatus) || "CA".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      if (("G".equals(stringStatus) || "GA".equals(stringStatus) || "GB".equals(stringStatus) || "C".equals(stringStatus) || "CA".equals(stringStatus) || "I".equals(stringStatus))
          &&
          // End �ק���:20151120 ���u�s��:B3774
          // End �ק���:20120504 ���u�s��:B3774
          // End �ק���:20100317 ���u�s��:B3774
          // End �ק���:20091230 ���u�s��:B3774
          stringChangeNameFee.length() == 0) {
        // End �ק���:20091222 ���u�s��:B3774
        message("[�W�q�H�ܧ����O] ���i���ť�!");
        getcLabel("ChangeNameFee").requestFocus();
        return false;
      }
      JTable jtable13 = getTable("table13");
      boolean blnChecked = false; // �ק���:20091222 ���u�s��:B3774
      for (int intRow = 0; intRow < jtable13.getRowCount(); intRow++) {
        // Start �ק���:20091222 ���u�s��:B3774
        if ("Y".equals("" + getValueAt("table13", intRow, "IsChoose"))) {
          blnChecked = true;
        }
        // End �ק���:20091222 ���u�s��:B3774
        setValueAt("table13", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table13", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table13", LastUser, intRow, "LastUser");
        setValueAt("table13", LastDateTime, intRow, "LastDateTime");
      }
      // �Y���W(���P��ĳ),���W(���s����),�~��,����O���B=0, �h�K���O���Y����J
      // Start �ק���:20091230 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      // Start �ק���:20100317 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "A".equals(stringStatus) || "I".equals(stringStatus)) &&
      // Start �ק���:20120504 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      // Start �ק���:20151120 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "GA".equals(stringStatus) ||
      // "C".equals(stringStatus) || "CA".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      if (("G".equals(stringStatus) || "GA".equals(stringStatus) || "GB".equals(stringStatus) || "C".equals(stringStatus) || "CA".equals(stringStatus) || "I".equals(stringStatus))
          &&
          // End �ק���:20151120 ���u�s��:B3774
          // End �ק���:20120504 ���u�s��:B3774
          // End �ק���:20100317 ���u�s��:B3774
          // End �ק���:20091230 ���u�s��:B3774
          "0".equals(stringChangeNameFee) && !blnChecked) {
        message("�ܧ����O��0!  �ФĿ�K���O���Y!");
        return false;
      }
      // Start �ק���:20091224 ���u�s��:B3774
      // �W�Ǫ���
      String stringFileName = getValue("FileName").trim();
      // Start �ק���:20091230 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      // Start �ק���:20100317 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "A".equals(stringStatus) || "I".equals(stringStatus)) &&
      // Start �ק���:20120504 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "C".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      // Start �ק���:20151120 ���u�s��:B3774
      // if(("G".equals(stringStatus) || "GA".equals(stringStatus) ||
      // "C".equals(stringStatus) || "CA".equals(stringStatus) ||
      // "I".equals(stringStatus)) &&
      if (("G".equals(stringStatus) || "GA".equals(stringStatus) || "GB".equals(stringStatus) || "C".equals(stringStatus) || "CA".equals(stringStatus) || "I".equals(stringStatus))
          &&
          // End �ק���:20151120 ���u�s��:B3774
          // End �ק���:20120504 ���u�s��:B3774
          // End �ק���:20100317 ���u�s��:B3774
          // End �ק���:20091230 ���u�s��:B3774
          stringFileName.length() == 0) {
        message("[�W�Ǫ���] ���i���ť�!");
        getcLabel("FileName").requestFocus();
        return false;
      }
      // End �ק���:20091224 ���u�s��:B3774
      // ����䥦
      JTable jtable6 = getTable("table6");
      for (int intRow = 0; intRow < jtable6.getRowCount(); intRow++) {
        setValueAt("table6", stringContractNo, intRow, "ContractNo"); // �ק���:20091201 ���u�s��:B3774
        setValueAt("table6", stringCompanyCd, intRow, "CompanyCd"); // �ק���:20120517 ���u�s��:B3774
        // Start �ק���:20100106 ���u�s��:B3774
        if ("Y".equals(("" + getValueAt("table6", intRow, "IsChoose")).trim()) &&
        // Start �ק���:20100601 ���u�s��:B3774
        // "Other19".equals((""+getValueAt("table6", intRow, "ItemlsCd")).trim()) &&
            "Other99".equals(("" + getValueAt("table6", intRow, "ItemlsCd")).trim()) &&
            // End �ק���:20100601 ���u�s��:B3774
            ("" + getValueAt("table6", intRow, "Remark")).trim().length() == 0) {
          message("[����-�䥦���ت�����] ���i���ť�!");
          setFocus("table6", intRow, "Remark");
          return false;
        }
        // �e�U��
        if ("Y".equals(("" + getValueAt("table6", intRow, "IsChoose")).trim()) && "Other2".equals(("" + getValueAt("table6", intRow, "ItemlsCd")).trim())) {
          JTable jtable27 = getTable("table27");
          if (jtable27.getRowCount() <= 0) {
            message("[�Q�e�U�H���] ���i���ť�!");
            return false;
          } else {
            for (int int27Row = 0; int27Row < jtable27.getRowCount(); int27Row++) {
              String custName = jtable27.getValueAt(int27Row, 0).toString();
              String custNo = jtable27.getValueAt(int27Row, 1).toString();
              System.out.println("0custName=====>" + custName);
              System.out.println("1custNo=====>" + custNo);
              if ("".equals(custName) || "".equals(custNo)) {
                message("[�Q�e�U�H���] �m�W/�����Ҧr�����i���ť�!");
                return false;
              }
            }
          }
        }
        // �ĤT�e�U
        if ("Y".equals(("" + getValueAt("table6", intRow, "IsChoose")).trim()) && "Other44".equals(("" + getValueAt("table6", intRow, "ItemlsCd")).trim())) {
          JTable jtable28 = getTable("table28");
          if (jtable28.getRowCount() <= 0) {
            message("[���w�ĤT�H���] ���i���ť�!");
            return false;
          } else {
            for (int int28Row = 0; int28Row < jtable28.getRowCount(); int28Row++) {
              String custName = jtable28.getValueAt(int28Row, 0).toString();
              String custNo = jtable28.getValueAt(int28Row, 1).toString();
              System.out.println("0custName=====>" + custName);
              System.out.println("1custNo=====>" + custNo);
              if ("".equals(custName) || "".equals(custNo)) {
                message("[���w�ĤT�H���] �m�W/�����Ҧr�����i���ť�!");
                return false;
              }
            }
          }
        }
        // End �ק���:20100106 ���u�s��:B3774
        // ��ϥΪ̤ήɶ�
        setValueAt("table6", LastUser, intRow, "LastUser");
        setValueAt("table6", LastDateTime, intRow, "LastDateTime");
      }
      // Start �ק���:20091224 ���u�s��:B3774
      // Start �ק���:20150811 ���u�s��:B3774
      /*
       * // ���P��� String stringTransferDate = getValue("TransferDate").trim(); // Start
       * �ק���:20120504 ���u�s��:B3774 //if("G".equals(stringStatus) &&
       * stringTransferDate.length() == 0){ if(("G".equals(stringStatus) ||
       * "GA".equals(stringStatus)) && stringTransferDate.length() == 0){ // End
       * �ק���:20120504 ���u�s��:B3774 message("[���P���] ���i���ť�!");
       * getcLabel("TransferDate").requestFocus(); return false; }
       * if(stringTransferDate.length() > 0){
       * if(stringTransferDate.replaceAll("/","").length() != 8){
       * message("[���P���] �榡���~(YYYY/MM/DD) !");
       * getcLabel("TransferDate").requestFocus(); return false; }
       * Farglory.util.FargloryUtil exeFun = new Farglory.util.FargloryUtil();
       * stringTransferDate = exeFun.getDateAC(stringTransferDate, "���P���");
       * if(stringTransferDate.length() != 10){ message(stringTransferDate);
       * getcLabel("TransferDate").requestFocus(); return false; }
       * setValue("TransferDate", stringTransferDate); }
       */
      // End �ק���:20150811 ���u�s��:B3774
      // End �ק���:20091224 ���u�s��:B3774
      // Start �ק���:20140721 ���u�s��:B3774
      // ���w�L��H
      JTable jtable25 = getTable("table25");
      String stringToTransferName = "";
      String stringToTransferID = "";
      String stringHoldRatio = "";
      String stringHoldRatioSum = "0";
      for (int intRow = 0; intRow < jtable25.getRowCount(); intRow++) {
        setValueAt("table25", stringContractNo, intRow, "ContractNo");
        setValueAt("table25", stringCompanyCd, intRow, "CompanyCd");
        // ���w�L��H
        stringToTransferName = ("" + getValueAt("table25", intRow, "ToTransferName")).trim();
        if (stringToTransferName.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [���w�L��H] ���i���ť�!");
          setFocus("table25", intRow, "ToTransferName");
          return false;
        }
        // ������
        stringToTransferID = ("" + getValueAt("table25", intRow, "ToTransferID")).trim();
        if (stringToTransferID.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [������] ���i���ť�!");
          setFocus("table25", intRow, "ToTransferID");
          return false;
        }
        // �������%
        stringHoldRatio = ("" + getValueAt("table25", intRow, "HoldRatio")).trim();
        if (stringHoldRatio.length() == 0) {
          message("�� " + (intRow + 1) + " �C�� [�������%] ���i���ť�!");
          setFocus("table25", intRow, "HoldRatio");
          return false;
        }
        stringHoldRatioSum = operation.floatAdd(stringHoldRatioSum, stringHoldRatio, 2);
        // ��ϥΪ̤ήɶ�
        setValueAt("table25", LastUser, intRow, "LastUser");
        setValueAt("table25", LastDateTime, intRow, "LastDateTime");
      }
      // �ˮ� �������% �[�`����100
      if (jtable25.getRowCount() > 0 && operation.compareTo(stringHoldRatioSum, "100") != 0) {
        message("[�������%] �[�`����100!");
        setFocus("table25", 0, "HoldRatio");
        return false;
      }
      // End �ק���:20140721 ���u�s��:B3774
      // Start �ק���:20091201 ���u�s��:B3774
      String stringContractNoDB = getValue("ContractNoDB").trim();
      // Start �ק���:20120517 ���u�s��:B3774
      String stringCompanyCdDB = getValue("CompanyCdDB").trim();
      //
      /*
       * stringSql =
       * "DELETE FROM Sale05M275_New WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M276 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M277 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M278 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // Start �ק���:20100412 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M308 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // End �ק���:20100412 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M279 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M281 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M282 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M283_New WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // Start �ק���:20100226 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M305 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // End �ק���:20100226 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M289 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M294 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M295 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql);
       */
      stringSql = "DELETE FROM Sale05M275_New WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M276 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M277 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // Start �ק���:20121213 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M343 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M344 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M345 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // End �ק���:20121213 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M278 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M308 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M279 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M281 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M282 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M283_New WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M305 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M289 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M294 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M295 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // Start �ק���:20140721 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M350 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // End �ק���:20140721 ���u�s��:B3774
      // Start �ק���:20121210 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M295Print WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M295Log WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // End �ק���:20121210 ���u�s��:B3774
      // End �ק���:20120517 ���u�s��:B3774
      // End �ק���:20091201 ���u�s��:B3774
      //
      // Start �ק���:20100317 ���u�s��:B3774
      // if("N".equals(stringStatus)){ // �ק���:20091229 ���u�s��:B3774
      // Start �ק���:20151120 ���u�s��:B3774
      // if("N".equals(stringStatus) || "A".equals(stringStatus)){
      if ("N".equals(stringStatus) || "NA".equals(stringStatus) || "A".equals(stringStatus)) {
        // End �ק���:20151120 ���u�s��:B3774
        // End �ק���:20100317 ���u�s��:B3774
        // Start �ק���:20161024 ���u�s��:B3774
        if ("Y".equals(stringIsSFlow1)) {
          message("�O�o�n�e��~�g��!");
        } else {
          // End �ק���:20161024 ���u�s��:B3774
          message("�O�o�n�e��P!");
        } // �ק���:20161024 ���u�s��:B3774
        // Start �ק���:20091229 ���u�s��:B3774
        // Start �ק���:20091230 ���u�s��:B3774
        // }else if("G".equals(stringStatus) || "C".equals(stringStatus) ||
        // "I".equals(stringStatus)){
        // Start �ק���:20100317 ���u�s��:B3774
        // }else if("G".equals(stringStatus) || "C".equals(stringStatus) ||
        // "A".equals(stringStatus) || "I".equals(stringStatus)){
        // Start �ק���:20120504 ���u�s��:B3774
        // }else if("G".equals(stringStatus) || "C".equals(stringStatus) ||
        // "I".equals(stringStatus)){
        // Start �ק���:20151120 ���u�s��:B3774
        // }else if("G".equals(stringStatus) || "GA".equals(stringStatus) ||
        // "C".equals(stringStatus) || "CA".equals(stringStatus) ||
        // "I".equals(stringStatus)){
      } else if ("G".equals(stringStatus) || "GA".equals(stringStatus) || "GB".equals(stringStatus) || "C".equals(stringStatus) || "CA".equals(stringStatus)
          || "I".equals(stringStatus)) {
        // End �ק���:20151120 ���u�s��:B3774
        // End �ק���:20120504 ���u�s��:B3774
        // End �ק���:20100317 ���u�s��:B3774
        // End �ק���:20091230 ���u�s��:B3774
        // Start �ק���:20161024 ���u�s��:B3774
        if ("Y".equals(stringIsSFlow1)) {
          message("�O�o�n�e�]�ȸg��!");
        } else {
          // End �ק���:20161024 ���u�s��:B3774
          message("�O�o�n�e��~!");
        } // �ק���:20161024 ���u�s��:B3774
      }
      // End �ק���:20091229 ���u�s��:B3774
    } else if ("�R��".equals(stringValue)) {
      // �w���o�夣�i�R��
      String stringBarCode = getValue("BarCode").trim();
      //
      if (blnIsDispatch(stringBarCode)) {
        message("����w���o��A���i�R��!");
        return false;
      }
      // Start �ק���:20091201 ���u�s��:B3774
      String stringSql = "";
      String stringContractNoDB = getValue("ContractNoDB").trim();
      String stringCompanyCdDB = getValue("CompanyCdDB").trim(); // �ק���:20120517 ���u�s��:B3774
      // Start �ק���:20100104 ���u�s��:B3774
      String stringProjectID1 = getValue("ProjectID1").trim();
      String stringStatus = getValue("Status").trim(); // �ק���:20100108 ���u�s��:B3774
      // �M��A_Sale.ContrDate, DateRange
      // Start �ק���:20100317 ���u�s��:B3774
      // if("N".equals(stringStatus)){ // �ק���:20100108 ���u�s��:B3774
      // Start �ק���:20151120 ���u�s��:B3774
      // if("N".equals(stringStatus) || "A".equals(stringStatus)){
      if ("N".equals(stringStatus) || "NA".equals(stringStatus) || "A".equals(stringStatus)) {
        // End �ק���:20151120 ���u�s��:B3774
        // End �ק���:20100317 ���u�s��:B3774
        stringSql = "select HouseCar, Position " + "from Sale05M278 " +
        // Start �ק���:20120517 ���u�s��:B3774
        // "where ContractNo='"+stringContractNoDB+"'";
            "where ContractNo='" + stringContractNoDB + "' " + "and CompanyCd='" + stringCompanyCdDB + "'";
        // End �ק���:20120517 ���u�s��:B3774
        String retData[][] = dbSale.queryFromPool(stringSql);
        for (int intRow = 0; intRow < retData.length; intRow++) {
          stringSql = "UPDATE A_Sale " + "SET ContrDate=null, " + "DateRange=null " + "WHERE ID1=(SELECT ID1 " + "FROM A_Sale " +
          // Start �ק���:20100412 ���u�s��:B3774
          // "WHERE ProjectID1='"+stringProjectID1+"' ";
              "WHERE ProjectID1='" + (stringProjectID1.equals("H38A") ? "H38" : stringProjectID1) + "' ";
          // End �ק���:20100412 ���u�s��:B3774
          if ("House".equals(retData[intRow][0])) {
            stringSql = stringSql + "AND HouseCar='Position' " + "AND Position='" + retData[intRow][1] + "')";
          } else if ("Car".equals(retData[intRow][0])) {
            stringSql = stringSql + "AND HouseCar='Car' " + "AND Car='" + retData[intRow][1] + "')";
          }
          dbSale.execFromPool(stringSql);
        }
      } // �ק���:20100108 ���u�s��:B3774
        // �O��
        // Start �ק���:20100112 ���u�s��:B3774
      /*
       * stringSql = "Insert into Sale05M304 "+ "select OrderNo, Position, "+
       * "'"+datetime.getTime("YYYY/mm/dd h:m:s")+"', "+
       * "'"+getValue("ContractDate").trim()+"', "+
       * "'"+getValue("CashInDate").trim()+"' "+ "from Sale05M275_New "+
       * "where Contractno='"+stringContractNoDB+"'";
       */
      stringSql = "Insert into Sale05M304 " +
      // Start �ק���:20100202 ���u�s��:B3774
      // "select Contractno, OrderNo, Position, "+
          "select '" + stringBarCode + "' BarCode, " + "'" + stringStatus + "' Status, " + "Contractno, " + "CompanyCd, " + // �ק���:20120517 ���u�s��:B3774
          "OrderNo, " + "Position, " +
          // End �ק���:20100202 ���u�s��:B3774
          "'" + getValue("ContractDate").trim() + "', " + "'" + getValue("CashInDate").trim() + "', " + "'" + getUser().toUpperCase() + "', " + "'"
          + datetime.getTime("YYYY/mm/dd h:m:s") + "' " + "from Sale05M275_New " +
          // Start �ק���:20120517 ���u�s��:B3774
          // "where Contractno='"+stringContractNoDB+"'";
          "where Contractno='" + stringContractNoDB + "' " + "and CompanyCd='" + stringCompanyCdDB + "'";
      // End �ק���:20120517 ���u�s��:B3774
      // End �ק���:20100112 ���u�s��:B3774
      dbSale.execFromPool(stringSql);
      // End �ק���:20100104 ���u�s��:B3774
      //
      // Start �ק���:20120517 ���u�s��:B3774
      /*
       * stringSql =
       * "DELETE FROM Sale05M275_New WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M276 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M277 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M278 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // Start �ק���:20100412 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M308 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // End �ק���:20100412 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M279 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M281 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M282 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M283_New WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // Start �ק���:20100226 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M305 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); // End �ק���:20100226 ���u�s��:B3774 stringSql =
       * "DELETE FROM Sale05M289 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M294 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql); stringSql =
       * "DELETE FROM Sale05M295 WHERE ContractNo='"+stringContractNoDB+"'";
       * dbSale.execFromPool(stringSql);
       */
      stringSql = "DELETE FROM Sale05M275_New WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M276 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M277 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // Start �ק���:20121213 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M343 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M344 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M345 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // End �ק���:20121213 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M278 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M308 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M279 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M281 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M282 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M283_New WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M305 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M289 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M294 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M295 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // Start �ק���:20140721 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M350 WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // Start �ק���:20121005 ���u�s��:B3774
      stringSql = "DELETE FROM Sale05M295Print WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      stringSql = "DELETE FROM Sale05M295Log WHERE ContractNo='" + stringContractNoDB + "' AND CompanyCd='" + stringCompanyCdDB + "'";
      dbSale.execFromPool(stringSql);
      // End �ק���:20121005 ���u�s��:B3774
      // End �ק���:20120517 ���u�s��:B3774
      // End �ק���:20091201 ���u�s��:B3774
      // Start �ק���:20100104 ���u�s��:B3774
      if (getResultSet().getRowCount() == 1) {
        messagebox("��Ʈw���ʧ���");
      }
      // End �ק���:20100104 ���u�s��:B3774
    }

    return true;
  }

  //
  public void doInsertDoc1M030(String stringBarCode, String stringCDate, String stringCTime,
      // Start �ק���:20100625 ���u�s��:B3774
      // String stringEDateTime, String stringPreFinDate, String stringKindNo,
      String stringEDateTime, String stringPreFinDate, String stringKindNo, String stringKindNoD,
      // End �ק���:20100625 ���u�s��:B3774
      String stringComNo, String stringDepartNo, String stringEmployeeNo, String stringDocNo1, String stringDocNo2, String stringDocNo3, String stringDocNo, String stringDescript,
      String stringOriEmployeeName, String stringLastDepart, String stringLastDateTime, String stringInOut, String stringProjectID, String stringProjectID1, String stringCostID,
      String stringRealMoney, String stringRemark, String stringRecordNo) throws Throwable {
    String stringSql = "";
    //
    if ("Z6".equals(stringComNo)) {
      stringComNo = "06";
    }
    stringSql = "INSERT INTO Doc1M030 (BarCode,                  CDate,         CTime, " +
    // Start �ק���:20100625 ���u�s��:B3774
    // "EDateTime, PreFinDate, KindNo, "+
        "EDateTime,              PreFinDate, KindNo, KindNoD, " +
        // End �ק���:20100625 ���u�s��:B3774
        "ComNo,                    DepartNo,    EmployeeNo, " + "DocNo1,                   DocNo2,       DocNo3, " + "DocNo,                     DocClose,    Descript, "
        + "OriEmployeeName, LastDepart, LastDateTime, " + "InOut,                        Depart,        ProjectID, " + "ProjectID1,               CostID,         RealMoney, "
        + "Remark,                    Hand,           RecordNo, " + "DocStatus) " + " VALUES ( '" + stringBarCode + "', " + " '" + stringCDate + "', " + " '" + stringCTime + "', "
        + " '" + stringEDateTime + "', " + " '" + stringPreFinDate + "', " + " '" + stringKindNo + "', " + " '" + stringKindNoD + "', " + " '" + stringComNo + "', " + " '"
        + stringDepartNo + "', " + " '" + stringEmployeeNo + "', " + " '" + stringDocNo1 + "', " + " '" + stringDocNo2 + "', " + " '" + stringDocNo3 + "', " + " '" + stringDocNo
        + "', " + " '" + "N" + "', " + " '" + stringDescript + "', " + " '" + stringOriEmployeeName + "', " + " '" + stringLastDepart + "', " + " '" + stringLastDateTime + "', ";
    if ("".equals(stringInOut)) {
      stringSql += " null, ";
    } else {
      stringSql += " '" + stringInOut + "', ";
    }
    stringSql += " '" + "0" + "', ";
    if ("".equals(stringProjectID)) {
      stringSql += " null, ";
    } else {
      stringSql += " '" + stringProjectID + "', ";
    }
    if ("".equals(stringProjectID1)) {
      stringSql += " null, ";
    } else {
      stringSql += " '" + stringProjectID1 + "', ";
    }
    if ("".equals(stringCostID)) {
      stringSql += " null, ";
    } else {
      stringSql += " '" + stringCostID + "', ";
    }
    if ("".equals(stringRealMoney)) {
      stringSql += " null, ";
    } else {
      stringSql += "  " + stringRealMoney + ", ";
    }
    if ("".equals(stringRemark)) {
      stringSql += " null, ";
    } else {
      stringSql += " '" + stringRemark + "', ";
    }
    stringSql += " '" + "N" + "', ";
    if ("".equals(stringRecordNo)) {
      stringSql += " null, ";
    } else {
      stringSql += " " + stringRecordNo + ", ";
    }
    stringSql += " '" + "1" + "') ";
    dbDoc.execFromPool(stringSql);
  }

  //
  public void doInsertDoc1M040(String stringBarCode, String stringCDate, String stringCTime, String stringEDateTime, String stringDepartNo, String stringEmployeeNo)
      throws Throwable {
    String stringSql = "";
    String retData[][] = null; // �ק���:20151119 ���u�s��:B3774
    //
    stringSql = "INSERT INTO Doc1M040 (BarCode,             CDate,               CTime, " + "EDateTime,          DepartNo,        EmployeeNo, "
        + "CheckDateTime, CheckSecond, DocStatus) " + " VALUES ( '" + stringBarCode + "', " + " '" + stringCDate + "', " + " '" + stringCTime + "', " + " '" + stringEDateTime
        + "', " + " '" + stringDepartNo + "', " + " '" + stringEmployeeNo + "', " + "  " + "null" + ", " + "  " + "null" + ", " + " '" + "1" + "') ";
    dbDoc.execFromPool(stringSql);
    //
    // Start �ק���:20151119 ���u�s��:B3774
    stringSql = "SELECT MAX(ID1) " + "FROM Doc1M040 " + "WHERE BarCode='" + stringBarCode + "'";
    retData = dbDoc.queryFromPool(stringSql);
    if (retData.length > 0) {
      stringSql = "INSERT INTO DOC1CM040 (DI_AL_SERIAL, DI_AL_DATETIME) VALUES(" + retData[0][0] + ", GETDATE())";
      dbDoc.execFromPool(stringSql);
    }
    // End �ק���:20151119 ���u�s��:B3774
  }

  //
  public void doModifyDoc1M030(String stringBarCode, String stringDescript) throws Throwable {
    String stringSql = "";
    //
    stringSql = "UPDATE Doc1M030 " + "SET Descript='" + stringDescript + "' " + "WHERE BarCode='" + stringBarCode + "'";
    dbDoc.execFromPool(stringSql);
  }

  //
  public void doDelDoc1M030040(String stringBarCode) throws Throwable {
    String stringSql = "";
    //
    // Start �ק���:20151119 ���u�s��:B3774
    stringSql = "DELETE FROM DOC1CM040 " + "WHERE DI_AL_SERIAL IN (SELECT ID1 FROM Doc1M040 WHERE BarCode='" + stringBarCode + "')";
    dbDoc.execFromPool(stringSql);
    // End �ק���:20151119 ���u�s��:B3774
    //
    stringSql = "DELETE FROM Doc1M040 " + "WHERE BarCode='" + stringBarCode + "'";
    dbDoc.execFromPool(stringSql);
    //
    stringSql = "DELETE FROM Doc1M030 " + "WHERE BarCode='" + stringBarCode + "'";
    dbDoc.execFromPool(stringSql);
  }

  //
  public boolean blnIsDispatch(String stringBarCode) throws Throwable {
    // �w���o��P�_:
    // 1.Doc1M040�W�L�@��
    // 2.Doc1M040�u���@�����ODocStatus!=1
    String stringSql = "";
    String retData[][] = null;
    boolean blnIsDispatch = false;
    //
    stringSql = "select DocStatus, count(*) " + "from Doc1M040 " + "where BarCode='" + stringBarCode + "' " + "group by DocStatus";
    retData = dbDoc.queryFromPool(stringSql);
    if (retData.length > 0 && (retData.length > 1 || Integer.parseInt(retData[0][1]) > 1)) {
      blnIsDispatch = true;
    } else if (retData.length > 0 && Integer.parseInt(retData[0][1]) == 1) {
      if (!"1".equals(retData[0][0])) {
        blnIsDispatch = true;
      }
    }
    //
    return blnIsDispatch;
  }

  //
  // Start �ק���:20120517 ���u�s��:B3774
  // public String getDescript(String stringContractNo) throws Throwable{
  public String getDescript(String stringContractNo, String stringCompanyCd) throws Throwable {
    // End �ק���:20120517 ���u�s��:B3774
    String stringSql = "";
    String retData[][] = null;
    String stringDescript = "";
    //
    stringSql = "select Position " + "from Sale05M278 " + "where ContractNo='" + stringContractNo + "' " + "and CompanyCd='" + stringCompanyCd + "' " + // �ק���:20120517
    // ���u�s��:B3774
        "order by HouseCar desc, Position";
    retData = dbSale.queryFromPool(stringSql);
    for (int intRow = 0; intRow < retData.length; intRow++) {
      stringDescript = stringDescript.length() == 0 ? retData[intRow][0] : (stringDescript + "," + retData[intRow][0]);
    }
    stringSql = "select CustomName, ContractSerialNo " + "from Sale05M274 " +
    // Start �ק���:20120517 ���u�s��:B3774
    // "where ContractNo='"+stringContractNo+"'";
        "where ContractNo='" + stringContractNo + "' " + "and CompanyCd='" + stringCompanyCd + "'";
    // End �ק���:20120517 ���u�s��:B3774
    retData = dbSale.queryFromPool(stringSql);
    if (retData.length > 0) {
      stringDescript = stringDescript + "," + retData[0][0] + "#" + retData[0][1];
    }
    //
    return stringDescript;
  }

  //
  public String getInformation() {
    return "---------------�s�W���s�{��.preProcess()----------------";
  }
}