// 20191001 Justin ��z/�s�W
//20200107 Kyle �s�W���I�������

package Sale.Sale05R193;

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

public class Sale05R192AML extends bproc {
  talk dbSale = getTalk("Sale");
  talk dbInvoice = getTalk("" + get("put_dbInvoice"));
  talk dbFE3D = getTalk("" + get("put_dbFE3D"));
  talk dbJGENLib = getTalk("JGENLIB");
  talk dbPW0D = getTalk("pw0d");
  FargloryUtil exeUtil = new FargloryUtil();
  MLPUtils mlpUtils = new MLPUtils();
  int maxRow = 34;

  public String getDefaultValue(String value) throws Throwable {
    if (!isBatchCheckOK())
      return value;
    String[][] retSale05M080 = getSale05M080();
    if (retSale05M080.length == 0) {
      message("�L���ڸ�� !");
      return value;
    } else {
      doExcel(retSale05M080);
    }
    return value;
  }

  public boolean isBatchCheckOK() throws Throwable {
    if (getValue("CompanyNo").length() == 0) {
      message("[���q�N�X] ���i�ť�!");
      return false;
    }
    if (getValue("ProjectID1").length() == 0) {
      message("[�קO�N�X] ���i�ť�!");
      return false;
    }
    if (getValue("ReceiveDate").length() == 0) {
      message("���ڤ��] ���i�ť�!");
      return false;
    }
    return true;
  }

  // retSale05M080�G
  public void doExcel(String[][] retSale05M080) throws Throwable {
    List mainList = processMainList(retSale05M080);
    System.out.println("mainList size>>>" + mainList.size());

    // �إߪ��
    Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(8, 34, mainList.size(), 1);

    // �Ysample�ɸ��|
//    String stringPrintExcel = "G:\\kyleTest\\Excel\\Sale05R192_MLP.xls";
    String stringPrintExcel = "G:\\kyleTest\\Excel\\Sale05R192_AML.xls";
    // System.out.println(stringPrintExcel);

    // �إ�Excel����
    Vector retVector = exeFun.getExcelObject(stringPrintExcel);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);

    // R44 �g��
    String stringEmpName = getEmpName();
    if (!"CS".equals(getValue("CompanyNo"))) {
      exeFun.putDataIntoExcel(18, 44, stringEmpName, objectSheet1);
    }

    // A3 �קO
    exeFun.putDataIntoExcel(0, 3, "�קO:" + getValue("ProjectID1"), objectSheet1);

    // A4 ���ڷJ�`�s��
    String stringTemp = getValue("ReceiveNo").trim();
    stringTemp = exeUtil.doSubstring(stringTemp, 2, stringTemp.length());
    exeFun.putDataIntoExcel(0, 4, "���ڷJ�`�s��:" + stringTemp, objectSheet1);

    // H4 ����~
    String stringDate = convert
        .FormatedDate(convert.ac2roc((getValue("ReceiveDate").substring(0, 4) + getValue("ReceiveDate").substring(5, 7) + getValue("ReceiveDate").substring(8, 10))), "�~���");
    exeFun.putDataIntoExcel(9, 4, stringDate, objectSheet1);

    // A1 ���q�W
    String stringCompanyNo = getValue("CompanyNo").trim();
    exeFun.putDataIntoExcel(0, 1, getCompanyName(stringCompanyNo), objectSheet1);

    // Q5 ����ɶ�
    exeFun.putDataIntoExcel(19, 4, getToday("yy/mm/dd") + " " + getTime("h:m:s"), objectSheet1);

    // Start of Body ��ƥ���
    for (int intRow = 0; intRow < mainList.size(); intRow++) {
      String[] thisRow = (String[]) mainList.get(intRow);
      int recordNo = intRow + exeFun.getStartDataRow();

      // TODO: �O�_�ݭn�p��q����}�l����ĤG��(�Ȯɤ���)

      for (int intCon = 1; intCon <= 20; intCon++) {
        exeFun.putDataIntoExcel(intCon, recordNo, thisRow[intCon - 1], objectSheet1);
      }
    }
    // End of Body

    exeFun.getReleaseExcelObject(retVector);
    return;
  }

  /**
   * �զ��D�n�C�� List 0. A���ڽs�� 1. C�Ȥ�-��O 2. D�Ȥ�-�m�W 3. E���I���� 4. F���-�~�W 5. G���-���O 6. H���B
   * 7. I�{�� 8. J�H�Υd 9. K�Ȧ�s�� 10. L���ڪ��B 11. M�O�_���Hú�� 12. N�Nú�H�m�W 13. O�P�ʶR�H���Y 14.
   * P�O�_����-�ʶR�H 15. Q�O�_����-�����q�H(�ʶR�H���k�H) 16. R�O�_����-�Nú�H 17. S�ŦX�æ��~����x�A�� 18. T�~�������ӳ�
   * 19. U�j�B�q��
   */
  public List processMainList(String[][] m080) throws Throwable {
    List mainList = new ArrayList();
    Map queryLog = mlpUtils.getQueryLog();
    String[][] retDeputy = null; // �Nú�HBY�渹
    int deputyLength = 0; // �Nú�H�`��
    int deputyReal = 0; // �Nú�H�ثe�p��
    List thisOrderNos = null; // �������ڳ渹���q��s��
    String projectID = getValue("ProjectID1").trim();

    // System.out.println(">>> map qLog >>>" + queryLog.size() );
    // for (Iterator it = queryLog.entrySet().iterator(); it.hasNext();) {
    // Map.Entry mapEntry = (Map.Entry) it.next();
    // System.out.println("The key is: " + mapEntry.getKey() + ",value is :" +
    // mapEntry.getValue());
    // }

    String lastDocNo = "";
    for (int row080 = 0; row080 < m080.length; row080++) {
      String[] newRow = new String[20];
      String[] thisRow = m080[row080];
      String position = thisRow[1].trim(); // �ɼӧO
      String moneyH = thisRow[4].trim(); // �Ыδڪ��B
      String moneyL = thisRow[5].trim(); // �g�a�ڪ��B

      // 1.��O
      newRow[1] = thisRow[1].trim();

      // 5. ���O
      newRow[5] = thisRow[3].trim();

      // 2.�m�W & 3.���I����
      // ���������o�m�W��ID�A�᭱�|�Ψ�
      String customName = "";
      String customNo = "";
      String riskValue = "";
      String retSale05M086[][] = getSale05M086(thisRow);
      if (retSale05M086.length > 0) {
        String retSale05M084[][] = getSale05M084(thisRow, retSale05M086);
        if (retSale05M084.length > 0) {
          String lastCustomNo = "";
          if (retSale05M086.length > 0 && retSale05M084.length > 0) {
            for (int intSale05M084 = 0; intSale05M084 < retSale05M084.length; intSale05M084++) {
              String thisCustomNo = retSale05M084[intSale05M084][1];
              if(lastCustomNo.equals(thisCustomNo)) {
                continue;
              }
              
              if (intSale05M084 != 0) {
                customName += "\n";
                customNo += "\n";
                riskValue += "\n";
              }
              customName += retSale05M084[intSale05M084][0];
              customNo += retSale05M084[intSale05M084][1];
              riskValue += retSale05M084[intSale05M084][2].trim();
              
              lastCustomNo = thisCustomNo;
            }
          }
        } else if (retSale05M084.length == 0) {
          message("[�Ȥ�N�X] ���s�b!");
          System.out.println("084 Fail");
          ComThread.Release();
          continue;
        }
      } else if (retSale05M086.length == 0) {
        message("[�Ȥ�N�X] ���s�b!");
        System.out.println("086 Fail");
        ComThread.Release();
        continue;
      }
      newRow[2] = "".equals(customName) ? "" : customName;
      newRow[3] = riskValue;
      // System.out.println(">>>id>>>" + customNo );

      // �u�n�@��
      String thisDocNo = thisRow[0].trim(); // this���ڳ�s��
      if (!thisDocNo.equals(lastDocNo)) { // ��W���O���O�P�@���渹
        // ���o�q��s��
        thisOrderNos = mlpUtils.getOrderNo(thisDocNo);
        // System.out.println(">>> orderNo Size >>>" + thisOrderNos.size());

        // �Ĥ@���i�J���渹���J�Nú�H�A��ֹ��Ʈwloading
        retDeputy = new String[0][0];
        retDeputy = mlpUtils.getDeputy(thisDocNo);
        deputyLength = retDeputy.length;
        deputyReal = 0;
        // System.out.println(">>>retDeputyLength>>>" + deputyLength);

        // 0.���ڳ�s��
        newRow[0] = exeUtil.doSubstring(thisDocNo, thisDocNo.length() - 3, thisDocNo.length());
        // 7.�{��
        newRow[7] = thisRow[7].trim();
        // 8.�H�Υd
        newRow[8] = thisRow[8].trim();
        // 9.�Ȧ�s��
        newRow[9] = thisRow[9].trim();
        // 10.���ڪ��B
        newRow[10] = "".equals(thisRow[10].trim()) ? "0" : thisRow[10].trim();

        // 12. �Nú�H�m�W
        // 13. �P�ʶR�H���Y
        String row12 = "-";
        String row13 = "-";
        for (int i = 0; i < deputyLength; i++) {
          if (i == 0) {
            row12 = "";
            row13 = "";
          } else {
            row12 += " / ";
            row13 += " / ";
          }
          row12 += retDeputy[i][1].trim();
          row13 += retDeputy[i][3].trim();
        }
        newRow[12] = row12;
        newRow[13] = row13;

        // 11. �O�_���Hú��
        newRow[11] = deputyLength > 0 ? "�_" : "�O";

        // 14. �O�_���� - �ʶR�H
        newRow[14] = mlpUtils.getBuyerCtrlYN(projectID, queryLog, customNo, customName);

        // 15. �O�_���� - �����q�H(�Y�ʶR�H���k�H)
        String realBeneficiary = "-";
        String[][] arrBeneficiary = mlpUtils.getCtrlBeneficiary(thisOrderNos, customNo);
        if (mlpUtils.isCusCompany(customNo)) {
          String tmpBen = mlpUtils.getBeneficiaryCtrlYN(projectID, queryLog, arrBeneficiary, "ctrl");
          if (!"".equals(tmpBen))
            realBeneficiary = tmpBen;
        }
        newRow[15] = realBeneficiary;

        // 16. �O�_���� - �Nú�H
        String ctrlDeputyer = "-";
        if (deputyLength > 0) {
          String depStatus = "";
          ctrlDeputyer = "�_";
          for (int a = 0; a < deputyLength; a++) {
            System.out.println(">>> deputy key >>>" + projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim());
            if ("Y".equals(queryLog.get(projectID + retDeputy[a][2].trim() + retDeputy[a][1].trim()))) {
              ctrlDeputyer = "�O";
              break;
            }
          }
        }
        newRow[16] = ctrlDeputyer;

        // 17. �ŦX�æ��~��
        int mayWM = Integer.parseInt(thisRow[18].trim());
        newRow[17] = mayWM > 0 ? "�O" : "�_";

        // 18. �~�������ӳ�
        newRow[18] = "���O���_�����A��";

        // 19. �j�B�q��
        newRow[19] = "���O���_�����A��";
      }

      // 4.�~�W (���Fhouse car �~�٭n�P�_�O�_�g�a��)
      String houseCar = "--";
      String houseCarMode = ""; // H�ت� D�g�a
      if ("House".equals(thisRow[2].trim())) {
        if (Double.parseDouble(moneyH) > 0) {
          houseCar = "�Ыδ�";
          houseCarMode = "H";
        } else {
          houseCar = "�g�a��";
          houseCarMode = "D";
        }
        // �P�ɦ������p�y��B�z
      } else if ("Car".equals(thisRow[2].trim())) {
        if (Double.parseDouble(moneyH) > 0) {
          houseCar = "�����-�ت�";
          houseCarMode = "H";
        } else {
          houseCar = "�����-�g�a";
          houseCarMode = "D";
        }
        // �P�ɦ������p�y��B�z
      }
      newRow[4] = houseCar;

      // 6. ���B
      String priceHDC = "";
      if ("H".equals(houseCarMode)) {
        priceHDC = moneyH;
      } else if ("D".equals(houseCarMode)) {
        priceHDC = moneyL;
      }
      newRow[6] = priceHDC;

      // 12. �Nú�H�m�W
      // 13. �P�ʶR�H���Y
      // �Nú�H����>0 �B �{�b���� > �`����
      /*
       * if( deputyLength > 0 && deputyReal < deputyLength ){ newRow[12] =
       * retDeputy[deputyReal][1].trim(); newRow[13] =
       * retDeputy[deputyReal][3].trim(); deputyReal++; }
       */

      // �s�W�ܥD�C��
      mainList.add(newRow);

      // �S��B�z : �Y�P�ɦ��g�a�ڡA�h�ݭn�s�W�@��
      if ("H".equals(houseCarMode) && Double.parseDouble(moneyL) > 0) {
        String[] newRow2 = new String[20];
        newRow2[1] = thisRow[1].trim();
        newRow2[2] = customName;
        newRow2[3] = riskValue;
        if ("House".equals(thisRow[2].trim())) {
          newRow2[4] = "�g�a��";
        } else if ("Car".equals(thisRow[2].trim())) {
          newRow2[4] = "�����-�g�a";
        }
        newRow2[5] = thisRow[3].trim();
        newRow2[6] = moneyL;

        // ���Nú�H�O�o�]�n�B�z
        // 12. �Nú�H�m�W
        // 13. �P�ʶR�H���Y
        // �Nú�H����>0 �B �{�b���� > �`����
        /*
         * 20200130 - user��ܥNú�H��P�@�� if( deputyLength > 0 && deputyReal < deputyLength){
         * newRow[12] = retDeputy[deputyReal][1].trim(); newRow[13] =
         * retDeputy[deputyReal][3].trim(); deputyReal++; }
         */

        mainList.add(newRow2);
      }

      // �S��B�z �w�� : �Y�Nú�H���h���B���渹�n�����o�C������...
      /*
       * 20200130 - user��ܥNú�H��P�@�� String nextRowDocNo = row080+1 >= m080.length ?
       * "noNextRow":m080[row080+1][0].trim(); if( !thisDocNo.equals( nextRowDocNo )
       * ){ if( deputyLength > 0 && (deputyLength - deputyReal) > 0 ){ for(int
       * i=deputyReal ; i<deputyLength ; i++){ String[] newRow3 = new String[20];
       * newRow[12] = retDeputy[deputyReal][1].trim(); newRow[13] =
       * retDeputy[deputyReal][3].trim(); mainList.add( newRow3 ); } } }
       */

      // ���U�@�������������ڳ渹
      lastDocNo = thisDocNo;
    }

    return mainList;
  }

  public String[][] getSale05M080() throws Throwable {
    String qreceiveNo = "";
    if (getValue("ReceiveNo").length() > 0) {
      qreceiveNo = " where ReceiveNo = '" + getValue("ReceiveNo") + "'";
    }

    String sql = "SELECT " + "Sale05M080.DocNo " + ",Sale05M081.Position " + ",Sale05M081.HouseCar "
        + ", (SELECT ITEMLS_CHINESE FROM Sale05M052 WHERE ITEM_CD = 'Z01' AND Sale05M052.ITEMLS_CD = Sale05M081.ITEMLS_CD ) as C_ITEMLS_CD "
        + ",(Sale05M081.H_ReceiveMoney * 10000) as H_ReceiveMoney " + ", (Sale05M081.L_ReceiveMoney* 10000) as L_ReceiveMoney "
        + ", ISNULL(Sale05M081.L_ReceiveMoney_Other * 10000, 0) L_ReceiveMoney_Other " + ",Sale05M080.CashMoney " + ",Sale05M080.CreditCardMoney "
        + ",Sale05M080.BankMoney, (select sum(CheckMoney) from Sale05M082 where Sale05M082.DocNo = Sale05M080.DocNo) as billMoney " + ",Sale05M080.B_STATUS "
        + ",Sale05M080.C_STATUS " + ",Sale05M080.R_STATUS " + ",Sale05M080.PaymentDeputy " + ",Sale05M080.DeputyName " + ",Sale05M080.DeputyRelationship " + ",Sale05M080.DeputyID "
        + ",(select count(*) from Sale05M070 where Sale05M070.DocNo = Sale05M080.DocNo and RecordDesc != '���A��' and RecordDesc != '���ŦX' and RecordDesc not like '%�C���I%' and RecordDesc not like '%�����I%') as cot70 "
        + "FROM Sale05M080 ,Sale05M081 " + "WHERE Sale05M080.DocNo = Sale05M081.DocNo " + "AND Sale05M080.DocNo in (SELECT DocNo FROM SALE05M193 " + qreceiveNo + " ) ";

    if (getValue("CompanyNo").length() > 0) {
      sql += "AND Sale05M080.CompanyNo = '" + getValue("CompanyNo") + "' ";
    }
    if (getValue("ProjectID1").length() > 0) {
      sql += "AND Sale05M080.ProjectID1 = '" + getValue("ProjectID1") + "' ";
    }
    if (getValue("ReceiveDate").length() > 0) {
      sql += "AND Sale05M080.EDate = '" + getValue("ReceiveDate") + "' ";
    }

    sql += "ORDER BY Sale05M080.DocNo,HouseCar DESC,Sale05M081.Position,ORDER_NO";

    String retSale05M080[][] = dbSale.queryFromPool(sql);
    return retSale05M080;
  }

  // ���o���q�W��
  public String getCompanyName(String stringCompanyCD) throws Throwable {
    String stringSql = "";
    String stringCompanyName = "";
    String[][] retFED1023 = null;
    //
    stringSql = "SELECT  Company_Name  FROM  FED1023  WHERE  Company_CD  =  '" + stringCompanyCD + "' ";
    retFED1023 = dbInvoice.queryFromPool(stringSql);
    if (retFED1023.length != 0) {
      stringCompanyName = retFED1023[0][0].trim();
    }
    return stringCompanyName;
  }

  // ���o�g��
  public String getEmpName() throws Throwable {
    String stringEmpName = getUser();
    String stringSQL = "";
    //
    stringSQL = " SELECT EMP_NAME " + " FROM FE3D05 " + " WHERE EMP_NO = '" + stringEmpName + "'";
    String retFE3D05[][] = dbFE3D.queryFromPool(stringSQL);
    if (retFE3D05.length != 0) {
      stringEmpName = retFE3D05[0][0].trim();
    }
    return stringEmpName;
  }

  public String[][] getSale05M084(String[] retSale05M080, String[][] retSale05M086) throws Throwable {
    String sql = "SELECT  distinct T84.CustomName , T91.CustomNo , T91.riskValue , T91.StatusCd " 
               + "FROM Sale05M084 T84 ,  Sale05M091 T91 " 
               + "WHERE DocNo = '" + retSale05M080[0] + "' "
               + "and T84.CustomNo = T91.CustomNo "
//               +"and T91.OrderNo='"+ retSale05M086[0][0] +"' and ( (ISNULL(T91.TrxDate,'')<>'' and ISNULL(T91.TrxDate,'') > '"+ getValue("ReceiveDate") +"' ) or ISNULL(T91.TrxDate,'')='' ) ";
               + "and T91.OrderNo='" + retSale05M086[0][0] + "' " + "and ( (ISNULL(T91.STatusCd,'') = 'C' and ISNULL(T91.TrxDate,'') > '" + getValue("ReceiveDate") + "')   or   ISNULL(T91.STatusCd,'') = '' ) "
               + "order by T91.CustomNo , T91.StatusCd desc "; 

    String retSale05M084[][] = dbSale.queryFromPool(sql);
    return retSale05M084;
  }

  public String[][] getSale05M086(String[] retSale05M080) throws Throwable {
    String stringSQL = " SELECT distinct T86.OrderNo " + " FROM Sale05M086 T86 ,Sale05M092 T92 ,Sale05M081 T81 " + " WHERE T86.DocNo = '" + retSale05M080[0] + "' "
        + " and T92.OrderNo=T86.OrderNo " + " and T81.DocNo=T86.DocNo " + " and T81.Position=T92.Position " + " and T92.Position= '" + retSale05M080[1] + "' "
        + " and ISNULL(T92.StatusCd,'')<>'D' ";
    String retSale05M086[][] = dbSale.queryFromPool(stringSQL);
    return retSale05M086;
  }

  public String getClientFile(String stringServerPath, FargloryUtil exeUtil) throws Throwable {
    String stringClientPath = "";
    String[] arrayTemp = convert.StringToken(stringServerPath, "/");
    //
    if (exeUtil.doSaveFile(stringServerPath, "Y")) {
      stringClientPath = "C:\\Emaker_Util\\" + arrayTemp[arrayTemp.length - 1].trim();
      return stringClientPath;
    }
    return stringServerPath;
  }

  public String getInformation() {
    return "---------------button2(�C�L).defaultValue()----------------";
  }
}