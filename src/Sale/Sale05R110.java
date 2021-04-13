/**
 * 2020-01-15 Kyle ��zcode �� �s�W�~�����
 * �U�����ѥN�����ק�e�N���A�Y�U���L��ʫh�ץ����s��
 */

package Sale;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import com.jacob.activeX.*;
import com.jacob.com.*;
import Farglory.util.FargloryUtil;
import Farglory.util.MLPUtils;

public class Sale05R110 extends bproc {
  FargloryUtil exeUtil = new FargloryUtil();
  MLPUtils mlpUtils = new MLPUtils();
  // �����]�w
  int intStartDataRow = 7;
  int intPageDataRow = 25;
  int intPageAllRow = 40;
  int intPageNo = 1;

  // table connect
  talk dbSale = getTalk("" + get("put_dbSale"));
  talk dbInvoice = getTalk("" + get("put_dbInvoice"));
  talk dbFE3D = getTalk("" + get("put_dbFE3D"));

  public String getDefaultValue(String value) throws Throwable {
    if (getValue("CompanyNo").length() == 0) {
      message("[���q�N�X] ���i�ť�!");
      return value;
    }
    if (getValue("ProjectID1").length() == 0) {
      message("[�קO�N�X] ���i�ť�!");
      return value;
    }
    if (getValue("ReceiveDate").length() == 0) {
      message("�����b�ک��Ӥ��] ���i�ť�!");
      return value;
    }

    String stringSQL = "";
    stringSQL = " SELECT Sale05M111.DocNo, " + " Sale05M111.Position, " + " Sale05M111.CustomNo, " + " Sale05M111.PointNo, " + " Sale05M111.DetailItem, " + " Sale05M111.Remark, "
        + " Sale05M111.InvoiceNo, " + " Sale05M111.InvoiceMoney, " + " Sale05M111.InvoiceTax, " + " Sale05M111.InvoiceTotalMoney, " +
// Start �ק��G20090202 ���u�s���GB3774
        // " Sale05M111.InvoiceKind " +
        "Sale05M111.InvoiceKind, " + "Sale05M111.Endorse " +
// End
        " FROM Sale05M110,Sale05M111 " + " WHERE Sale05M110.ReceiveNo = Sale05M111.ReceiveNo ";
    if (getValue("CompanyNo").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.CompanyNo = '" + getValue("CompanyNo") + "'";
    }
    if (getValue("ProjectID1").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.ProjectID1 = '" + getValue("ProjectID1") + "'";
    }
    if (getValue("ReceiveDate").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.ReceiveDate = '" + getValue("ReceiveDate") + "'";
    }
    if (getValue("ReceiveNo").length() > 0) {
      stringSQL = stringSQL + " AND Sale05M110.ReceiveNo = '" + getValue("ReceiveNo") + "'";
    }
    if (value.equals("�C�L�g�a")) {
      stringSQL = stringSQL + "  AND (PointNo='2102' OR PointNo='2104') ";
    }

    String retSale05M111[][] = dbSale.queryFromPool(stringSQL);
    if (retSale05M111.length == 0) {
      message("[�����b�ڷJ�`��] ���s�b!");
      return value;
    }
    // �إ�com����
    ActiveXComponent Excel;
    ComThread.InitSTA();
    Excel = ExcelVerson();
    Excel.setProperty("Visible", new Variant(true));
    Object objectExcel = Excel.getObject();
    Object objectWorkbooks = Dispatch.get(objectExcel, "Workbooks").toDispatch();

    // EXCEL ���|
    String excelPath = "G:\\kyleTest\\Excel\\Sale05R110_AML.xlt";
    Object objectWorkbook = Dispatch.call(objectWorkbooks, "Open", excelPath).toDispatch();
    // System.out.println(">>>Excel Paht>>>" + excelPath);

    Object objectSheets = Dispatch.get(objectWorkbook, "Sheets").toDispatch();
    Object objectSheet1 = Dispatch.call(objectSheets, "Item", "Sheet1").toDispatch();
    Object objectSheet2 = Dispatch.call(objectSheets, "Item", "Sheet2").toDispatch();
    Dispatch.call(objectSheet2, "Activate");
    // Object objectSheet = Dispatch.get(objectWorkbook,"ActiveSheet").toDispatch();
    // A1 for Copy &Paste
    Object objectA1 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    // J36 �g�����.�g��
    Object objectJ36 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "M36" }, new int[1]).toDispatch();
    stringSQL = " SELECT EMP_NAME " + " FROM FE3D05 " + " WHERE EMP_NO = '" + getUser() + "' ";
    String retFE3D05[][] = dbFE3D.queryFromPool(stringSQL);
    if (!"CS".equals(getValue("CompanyNo"))) {
      if (retFE3D05.length == 0)
        Dispatch.put(objectJ36, "Value", getUser());
      else
        Dispatch.put(objectJ36, "Value", retFE3D05[0][0].trim());
    }
    // �b[A2]cell�̶�r
    Object objectA2 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A2" }, new int[1]).toDispatch();
    if (getValue("ProjectID1").equals("H58B") && value.equals("�C�L�g�a")) {
      stringSQL = " SELECT Company_Name " + " FROM FED1023 " + " WHERE Company_CD = 'CS' ";
    } else {
      stringSQL = " SELECT Company_Name " + " FROM FED1023 " + " WHERE Company_CD = '" + getValue("CompanyNo") + "'";
    }
    String retFED1023[][] = dbInvoice.queryFromPool(stringSQL);
    if (retFED1023.length == 0) {
      message("[���q�N�X] ���s�b!");
      return value;
    }
    Dispatch.put(objectA2, "Value", retFED1023[0][0].trim());
    // �b[A3]cell�̶�r
    Object objectA3 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A3" }, new int[1]).toDispatch();
    Dispatch.put(objectA3, "Value", getValue("ProjectID1") + "�����b�ک��ӷJ�`��");
    // �b[A4]cell�̶�r
    String stringReceiveNo = getValue("ReceiveNo").trim();
    String stringTemp = exeUtil.doSubstring(stringReceiveNo, 2, stringReceiveNo.length());
    // Object objectA3=Dispatch.invoke(objectSheet,"Range", Dispatch.Get,new
    // Object[] {"A3"},new int[1]).toDispatch();
    Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A4" }, new int[1]).toDispatch(), "Value",
        "�����b�ڽs���G" + stringTemp + "                                                        " + convert
            .FormatedDate(convert.ac2roc((getValue("ReceiveDate").substring(0, 4) + getValue("ReceiveDate").substring(5, 7) + getValue("ReceiveDate").substring(8, 10))), "�~���"));
    // Body
    int intRecordNo = intStartDataRow;
    // ������ START
    double doubleMoney = 0;
    double doubleTax = 0;
    double doubleTotalMoney = 0;
    double doubleUsableMoney = 0;
    double doubleUsableMoneySum = 0;
    boolean booleanAdd = true;
    String stringDiscountReason = "";
    String stringDoc = "";
    String[][] retSale05M081 = null;
    // ������ END

    String lastDocNo = ""; // �������ڽs��
    String lastCustNo = "";
    String projectID = getValue("ProjectID1").trim(); // �׸�
    for (int intSale05M111 = 0; intSale05M111 < retSale05M111.length; intSale05M111++) {
      // System.out.println(">>>test intRecordNo>>>" + intRecordNo);
      String thisDocNo = retSale05M111[intSale05M111][0].trim(); // �����ڳ渹
      List listOrderNo = mlpUtils.getOrderNo(thisDocNo); // �q��s����
      String customNo = ""; // �νs / ID
      String customName = ""; // �ʶR�H�m�W
      String riskValue = ""; // �ʶR�H���I����
      Map queryLog = mlpUtils.getQueryLog(); // �����ަW��
      String[][] retDeputy = mlpUtils.getDeputy(thisDocNo); // �Nú�H�W��
      int deputyLength = retDeputy.length; // �Nú�H�W��ƶq

      // for(int odNo=0 ; odNo<listOrderNo.size() ; odNo++){
      // System.out.println(">>>orderNo>>>[" + listOrderNo.get(odNo)+ "]");
      // }

      // �O���O�P�@�����ڳ渹
      boolean printMLP = false; // �O�_��X�~����T
      if (!thisDocNo.equals(lastDocNo)) {
        printMLP = true;
      }

      // A ���ڽs��
      stringTemp = retSale05M111[intSale05M111][0];
      stringTemp = exeUtil.doSubstring(stringTemp, 2, stringTemp.length());
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A" + intRecordNo }, new int[1]).toDispatch(), "Value", stringTemp);

      // B �ɼӧO
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "B" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][1]);

      // C �Ȥ�
      StringBuilder qOrderNo = new StringBuilder();
      for (int odNo = 0; odNo < listOrderNo.size(); odNo++) {
        System.out.println(">>>orderNo>>>[" + listOrderNo.get(odNo) + "]");

        if (odNo != 0)
          qOrderNo.append(",");
        qOrderNo.append("'").append(listOrderNo.get(odNo)).append("'");
      }
      stringSQL = "select distinct " + "T84.CustomName ,T91.riskValue , T84.CustomNo " + "from Sale05M084 T84 ,  Sale05M091 T91 " + "where 1=1 "
          + "and T84.CustomNo = T91.CustomNo  " + "and (( ISNULL(T91.TrxDate,'')<>'' ) or ISNULL(T91.TrxDate,'')='' ) " + "and T84.DocNo  =  '" + thisDocNo + "' "
          + "and T84.CustomNo = '" + retSale05M111[intSale05M111][2].trim() + "' " + "and T91.OrderNo in (" + qOrderNo.toString().trim() + ") ";
      // +"and ( T91.StatusCd != 'C' or T91.StatusCd is null ) ";
      String retSale05M084[][] = dbSale.queryFromPool(stringSQL);
      if (retSale05M084.length == 0) {
        message("[�Ȥ�N�X] ���s�b!");
        // return value;
      }

      if (retSale05M084.length != 0) {
        // for(int i=0 ; i<retSale05M084.length ; i++){
        // if( i!= 0 ) {
        // customNo += "\n";
        // customName += "\n";
        // riskValue += "\n";
        // }
        // customNo += retSale05M084[i][2].trim();
        // customName += retSale05M084[i][0].trim();
        // riskValue += retSale05M084[i][1].trim();
        // }

        // ID�û��u�|���@�� (�z�פW)
        customNo = retSale05M084[0][2].trim();
        customName = retSale05M084[0][0].trim();
        riskValue = retSale05M084[0][1].trim();

        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "C" + intRecordNo }, new int[1]).toDispatch(), "Value", customName);
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "D" + intRecordNo }, new int[1]).toDispatch(), "Value", riskValue);
      }

      // (new D) �����q�H(�Y�ʶR�H���k�H)
      String realBeneficiary = "";
      String[][] arrBeneficiary = new String[0][0];
      if (retSale05M084.length != 0) {
        if (mlpUtils.isCusCompany(customNo)) {
          arrBeneficiary = mlpUtils.getCtrlBeneficiary(listOrderNo, customNo);
          realBeneficiary = mlpUtils.getBeneficiaryCtrlYN(projectID, queryLog, arrBeneficiary, "list");
        }
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "E" + intRecordNo }, new int[1]).toDispatch(), "Value", realBeneficiary);
      }

      // 20200715 Kyle : user�n�D�C�ӫȤ�SHOW�@��
      if (!lastCustNo.equals(customNo)) {
        // �O�_����-�ʶR�H
        String ctrlBuyer = mlpUtils.getBuyerCtrlYN(projectID, queryLog, customNo, customName);
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "P" + intRecordNo }, new int[1]).toDispatch(), "Value", ctrlBuyer);

        // �O�_����-�����q�H
        String ctrlRealBeneficiary = "-";
        if (!"".equals(customNo) && mlpUtils.isCusCompany(customNo)) {
          ctrlRealBeneficiary = mlpUtils.getBeneficiaryCtrlYN(projectID, queryLog, arrBeneficiary, "ctrl");
        }
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "Q" + intRecordNo }, new int[1]).toDispatch(), "Value", ctrlRealBeneficiary);

        // �O�_����-�Nú�H
        String ctrlDeputyer = "-";
        if (deputyLength > 0) {
          String depStatus = "";
          ctrlDeputyer = "�_";
          for (int a = 0; a < deputyLength; a++) {
            System.out.println(">>> deputy key >>>" + projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim());
            // if( "Y".equals( queryLog.get( projectID + retDeputy[a][2].trim() +
            // retDeputy[a][1].trim() ) ) ) {
            if (queryLog.get(projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim()) != null) {
              ctrlDeputyer = "�O";
              break;
            }
          }
        }
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "R" + intRecordNo }, new int[1]).toDispatch(), "Value", ctrlDeputyer);

        // �~���q��1
        String amlCall = "���O���_";
        if (retSale05M111[intSale05M111][6].trim().length() != 10) amlCall = ""; 
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "S" + intRecordNo }, new int[1]).toDispatch(), "Value", amlCall);

        // �~���q��2
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "T" + intRecordNo }, new int[1]).toDispatch(), "Value", amlCall);

        // (new I ) �O�_���Hú��
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "O" + intRecordNo }, new int[1]).toDispatch(), "Value", deputyLength > 0 ? "�_" : "�O");

      }

      // �u�n�@��
      if (printMLP) {

        // �������Ȥ�

//        // (new I ) �O�_���Hú��
//        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "O" + intRecordNo }, new int[1]).toDispatch(), "Value", deputyLength > 0 ? "�_" : "�O");

        // //�O�_����-�ʶR�H
        // String ctrlBuyer = mlpUtils.getBuyerCtrlYN( projectID , queryLog , customNo ,
        // customName );
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"P" + intRecordNo},new int[1]).toDispatch(), "Value", ctrlBuyer );

        // //�O�_����-�����q�H
        // String ctrlRealBeneficiary = "-";
        // if( mlpUtils.isCusCompany(customNo) ){
        // ctrlRealBeneficiary = mlpUtils.getBeneficiaryCtrlYN( projectID , queryLog ,
        // arrBeneficiary , "ctrl" );
        // }
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"Q" + intRecordNo},new int[1]).toDispatch(), "Value", ctrlRealBeneficiary );

        // //�O�_����-�Nú�H
        // String ctrlDeputyer = "-";
        // if( deputyLength > 0 ){
        // String depStatus = "";
        // ctrlDeputyer = "�_";
        // for( int a=0 ; a < deputyLength ; a++){
        // System.out.println(">>> deputy key >>>" + projectID + retDeputy[a][2].trim()
        // + retDeputy[a][1].trim());
        // if( "Y".equals( queryLog.get( projectID + retDeputy[a][2].trim() +
        // retDeputy[a][1].trim() ) ) ) {
        // ctrlDeputyer = "�O";
        // break;
        // }
        // }
        // }
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"R" + intRecordNo},new int[1]).toDispatch(), "Value", ctrlDeputyer );

        // //�~���q��1
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"S" + intRecordNo},new int[1]).toDispatch(), "Value", "���O���_" );

        // //�~���q��2
        // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
        // {"T" + intRecordNo},new int[1]).toDispatch(), "Value", "���O���_" );
      }

      // D �K�n�N�X(�~�W)
      stringSQL = " SELECT PointName " + " FROM invoM010 " + " WHERE PointNo = '" + retSale05M111[intSale05M111][3] + "' ";
      String retinvoM010[][] = dbInvoice.queryFromPool(stringSQL);
      if (retinvoM010.length == 0) {
        message("[�K�n�N�X] ���s�b!");
        // return value;
      }
      if (retinvoM010.length != 0) {
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "F" + intRecordNo }, new int[1]).toDispatch(), "Value", retinvoM010[0][0].trim());
      }

      // E ���O
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "G" + intRecordNo }, new int[1]).toDispatch(), "Value",
          retSale05M111[intSale05M111][4].trim());

      // F ���ONo
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "H" + intRecordNo }, new int[1]).toDispatch(), "Value",
          retSale05M111[intSale05M111][5].trim());

      // // (new I ) �O�_���Hú��
      // Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
      // {"I" + intRecordNo},new int[1]).toDispatch(), "Value", deputyLength > 0 ?
      // "�_":"�O" );

      // G �o�����X
      if (retSale05M111[intSale05M111][6].trim().length() != 10) {
        booleanAdd = false;
      } else {
        booleanAdd = true;
      }
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "J" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][6]);

      // H �P���B
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "K" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][7]);
      if (booleanAdd) {
        doubleMoney += Double.parseDouble(retSale05M111[intSale05M111][7].trim());
      } else {
        doubleMoney -= Double.parseDouble(retSale05M111[intSale05M111][7].trim());
      }

      // I �|�B
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "L" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][8]);
      if (booleanAdd) {
        doubleTax += Double.parseDouble(retSale05M111[intSale05M111][8].trim());
      } else {
        doubleTax -= Double.parseDouble(retSale05M111[intSale05M111][8].trim());
      }

      // J �`�p
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "M" + intRecordNo }, new int[1]).toDispatch(), "Value", retSale05M111[intSale05M111][9]);
      if (booleanAdd) {
        doubleTotalMoney += Double.parseDouble(retSale05M111[intSale05M111][9].trim());
      } else {
        doubleTotalMoney -= Double.parseDouble(retSale05M111[intSale05M111][9].trim());
      }

      // K �o���`��
      String stringInvoiceKind = "";
      if (retSale05M111[intSale05M111][10].equals("2"))
        stringInvoiceKind = "�G�p��";
      else if (retSale05M111[intSale05M111][10].equals("3"))
        stringInvoiceKind = "�T�p��";
      else if (!booleanAdd)
        stringInvoiceKind = "������";
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "N" + intRecordNo }, new int[1]).toDispatch(), "Value", stringInvoiceKind);

      // �Ƶ�
      stringDiscountReason = "";
      if (!stringDoc.equals(thisDocNo)) {
        stringSQL = "SELECT  SUM(L_UsableMoney  +  H_UsableMoney) " + " FROM  Sale05M081 " + "WHERE  DocNo  =  '" + thisDocNo + "' ";
        retSale05M081 = dbSale.queryFromPool(stringSQL);
        doubleUsableMoney = doParseDouble(retSale05M081[0][0]);
        doubleUsableMoneySum += doubleUsableMoney;
        if (doubleUsableMoney > 0)
          stringDiscountReason = "�Ȧ��G" + convert.FourToFive("" + doubleUsableMoney, 0);
      }

      if (!booleanAdd) {
        stringSQL = " SELECT  DiscountReason,  H_DiscountMoney " + " FROM  Sale05M080 " + "WHERE  DocNo  =  '" + thisDocNo + "' ";
        String[][] retSale05M080 = dbSale.queryFromPool(stringSQL);
        if (retSale05M080.length > 0) {
          if (doParseDouble(retSale05M080[0][1].trim()) > 0) {
            if (!"".equals(stringDiscountReason))
              stringDiscountReason += ",";
            stringDiscountReason += "�H�U����";
          }
          if (!"".equals(retSale05M080[0][0].trim())) {
            if (!"".equals(stringDiscountReason))
              stringDiscountReason += ",";
            stringDiscountReason += ",";
            stringDiscountReason += retSale05M080[0][0].trim();
          }
        }
        // System.out.println((intSale05M111+1)+"-------------("+stringDiscountReason+")---------------"+retSale05M080.length)
        // ;
        /*
         * Dispatch.put(Dispatch.invoke(objectSheet2,"Range",Dispatch.Get,new Object[]
         * {"O" + intRecordNo},new int[1]).toDispatch(), "Value", stringDiscountReason
         * );
         */
      }
      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "U" + intRecordNo }, new int[1]).toDispatch(), "Value", stringDiscountReason);
      /* Carrey 20071008 add start */
      if (retSale05M111[intSale05M111][3].equals("2102") || retSale05M111[intSale05M111][3].equals("2104")) {
        stringSQL = " select T1.COMPANY_CD from sale05m040 T40,A_COM T1 where T40.ProjectID1='" + getValue("ProjectID1").trim() + "' and T40.Position='"
            + retSale05M111[intSale05M111][1] + "' and T40.L_Com=T1.Com_No ";
        String retSale05NoteCom[][] = dbSale.queryFromPool(stringSQL);
        if (retSale05NoteCom.length > 0 && !retSale05NoteCom[0][0].equals(getValue("CompanyNo").trim()))
          stringDiscountReason += "�N��";
      }

      Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "U" + intRecordNo }, new int[1]).toDispatch(), "Value", stringDiscountReason);
      /* Carrey 20071008 add End */
      // Start �ק��G20090202 ���u�s���GB3774
      if (retSale05M111[intSale05M111][11].trim().length() > 0) {
        stringDiscountReason = stringDiscountReason.length() == 0 ? retSale05M111[intSale05M111][11].trim() : stringDiscountReason + "," + retSale05M111[intSale05M111][11].trim();
        Dispatch.put(Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "U" + intRecordNo }, new int[1]).toDispatch(), "Value", stringDiscountReason);
      }

      // End
      intRecordNo++;
      lastDocNo = thisDocNo;
      lastCustNo = customNo;

      // �����ɥ����NSheet2 Copy Sheet1
      if (intRecordNo == (intPageDataRow + intStartDataRow)) {
        CopyPage(objectSheet1, objectSheet2);
        Object objectRangeClear = Dispatch
            .invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A" + intStartDataRow + ":R" + (intStartDataRow + intPageDataRow - 1) }, new int[1]).toDispatch();
        Dispatch.call(objectRangeClear, "ClearContents");
        intRecordNo = intStartDataRow;
        intPageNo++;
      }
    } // End of Body

    // �������ɥ����NSheet2 Copy Sheet1
    if (intRecordNo != intStartDataRow) {
      CopyPage(objectSheet1, objectSheet2);
      // Start �ק���:20091208 ���u�s��:B3774
      // }
    } else {
      // �᭱��n�S�����
      if (intPageNo > 1) {
        // �]���e���w�⭶��+1, ��ڤW�w��n�S���, �ҥH���ƭn-1
        intPageNo = intPageNo - 1;
      }
    }
    // End �ק���:20091208 ���u�s��:B3774
    // ����
    int intRow = (intPageNo - 1) * intPageAllRow + 32;
    // H �P���B
    Dispatch.put(Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "K" + intRow }, new int[1]).toDispatch(), "Value", "" + doubleMoney);
    // I �|�B
    Dispatch.put(Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "L" + intRow }, new int[1]).toDispatch(), "Value", "" + doubleTax);
    // J �`�p
    Dispatch.put(Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "M" + intRow }, new int[1]).toDispatch(), "Value", "" + doubleTotalMoney);

    // �i��
    // Dispatch.put(Dispatch.invoke(objectSheet1,"Range",Dispatch.Get,new Object[]
    // {"U" + intRow},new int[1]).toDispatch(),
    // "Value",
    // "�Ȧ��G" + format.format(convert.FourToFive(""+doubleUsableMoneySum, 0),
    // "999,999,999").trim()
    // );

    // End of Body
    //
    Dispatch.call(objectSheet1, "Activate");
    objectA1 = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    Dispatch.call(objectA1, "Select");
    Dispatch.call(objectSheet1, "PrintPreview");
    // ����com����
    ComThread.Release();
    return value;
  }

  // ��Client Excel Version �}��
  public ActiveXComponent ExcelVerson() {
    ActiveXComponent Excel;
    ComThread.InitSTA();
    int intExcelVerson = 0;
    // 20130305
    try {
      Excel = new ActiveXComponent("Excel.Application.8");// Excel 97
      System.out.println("Excel 97 is OK!");
      return Excel;
    } catch (Exception Excel97) {
      try {
        Excel = new ActiveXComponent("Excel.Application.9");// Excel 2000
        System.out.println("Excel 2000 is OK!");
        return Excel;
      } catch (Exception Excel2000) {
        try {
          Excel = new ActiveXComponent("Excel.Application.10");// Excel 2002
          System.out.println("Excel 2002 is OK!");
          return Excel;
        } catch (Exception Excel2002) {
          try {
            Excel = new ActiveXComponent("Excel.Application.11");// Excel 2003
            System.out.println("Excel 2003 is OK!");
            return Excel;
          } catch (Exception Excel2003) {
            try {
              Excel = new ActiveXComponent("Excel.Application.12");// Excel 2003
              System.out.println("Excel 2007 is OK!");
              return Excel;
            } catch (Exception Excel2010) {
              try {
                Excel = new ActiveXComponent("Excel.Application.13");// Excel 2003
                System.out.println("Excel 2010 is OK!");
                return Excel;
              } catch (Exception Excel14) {
                try {
                  Excel = new ActiveXComponent("Excel.Application.14");// Excel 2003
                  System.out.println("Excel.Application.14 is OK!");
                  return Excel;
                } catch (Exception Excel15) {
                  try {
                    Excel = new ActiveXComponent("Excel.Application.15");// Excel 2003
                    System.out.println("Excel.Application.15 is OK!");
                    return Excel;
                  } catch (Exception Excel16) {
                    try {
                      Excel = new ActiveXComponent("Excel.Application.16");// Excel 2003
                      System.out.println("Excel.Application.16 is OK!");
                      return Excel;
                    } catch (Exception Excel17) {
                      try {
                        Excel = new ActiveXComponent("Excel.Application.17");// Excel 2003
                        System.out.println("Excel.Application.17 is OK!");
                        return Excel;
                      } catch (Exception Excel18) {
                        try {
                          Excel = new ActiveXComponent("Excel.Application.18");// Excel 2003
                          System.out.println("Excel.Application.18 is OK!");
                          return Excel;
                        } catch (Exception Excel19) {
                          try {
                            Excel = new ActiveXComponent("Excel.Application.19");// Excel 2003
                            System.out.println("Excel.Application.19 is OK!");
                            return Excel;
                          } catch (Exception Excel20) {
                            try {
                              Excel = new ActiveXComponent("Excel.Application.20");// Excel 2003
                              System.out.println("Excel.Application.20 is OK!");
                              return Excel;
                            } catch (Exception ExcelError) {
                              System.out.println("�Шϥ� Excel2010 �H�W����!");

                            }

                          }

                        }

                      }

                    }

                  }

                }

              }

            }

          }
        }
      }
    }
    // 20130305
    Excel = new ActiveXComponent("Excel.Application");
    System.out.println("All is OK!");
    return Excel;
  }

  // Copy Sheet2 Template to Sheet1
  public void CopyPage(Object objectSheet1, Object objectSheet2) {
    Dispatch.call(objectSheet2, "Activate");
    Object objectA1 = Dispatch.invoke(objectSheet2, "Range", Dispatch.Get, new Object[] { "A1" }, new int[1]).toDispatch();
    Dispatch.call(objectA1, "Select");
    Object objectRow = Dispatch.invoke(objectSheet2, "Rows", Dispatch.Get, new Object[] { "1:" + intPageAllRow }, new int[1]).toDispatch();
    Dispatch.call(objectRow, "Copy");
    // Sheet1
    Dispatch.call(objectSheet1, "Activate");
    objectA1 = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[] { "A" + ((intPageNo - 1) * intPageAllRow + 1) }, new int[1]).toDispatch();
    Dispatch.call(objectA1, "Select");
    Dispatch.call(objectSheet1, "Paste");
  }

  public double doParseDouble(String stringNum) throws Exception {
    //
    double doubleNum = 0;
    if ("".equals(stringNum) || "null".equals(stringNum))
      return 0;
    try {
      doubleNum = Double.parseDouble(stringNum);
    } catch (Exception e) {
      System.out.println("�L�k��R[" + stringNum + "]�A�^�� 0�C");
      return 0;
    }
    return doubleNum;
  }

  public String getInformation() {
    return "---------------Print(�C�L).defaultValue()----------------";
  }
}
