package SaleEffect.Sale01R290;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import com.jacob.activeX.*;
import com.jacob.com.*;

import Farglory.util.FargloryUtil;

public class FuncPrint_R290_AO5 extends bTransaction {
  FargloryUtil fgUtil = new FargloryUtil();
  
  // ����ˮ�
  public boolean isBatchCheckOK() throws Throwable {
    int countOC = 0; // �I�q���ñ���饲���n���@flag
    String retDate = "";
    int countDate = 0;

    // �I�q���
    String orderDate1 = this.getValue("OrderDate1");
    if (!"".equals(orderDate1)) {
      retDate = fgUtil.getDateAC(orderDate1, "�I�q���(�_)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("OrderDate1").requestFocus();
        return false;
      }
      setValue("OrderDate1", retDate);
      countDate++;
      countOC++;
    }
    String orderDate2 = this.getValue("OrderDate2");
    if (!"".equals(orderDate2)) {
      retDate = fgUtil.getDateAC(orderDate2, "�I�q���(��)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("OrderDate2").requestFocus();
        return false;
      }
      setValue("OrderDate2", retDate);
      countDate++;
    }
    if (countDate == 1) {
      message("[�I�q���(�_)(��)] ���P�ɭ���C");
      return false;
    }

    // ñ�����
    String contrDate1 = this.getValue("ContrDate1");
    if (!"".equals(contrDate1)) {
      retDate = fgUtil.getDateAC(contrDate1, "ñ�����(�_)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("ContrDate1").requestFocus();
        return false;
      }
      setValue("ContrDate1", retDate);
      countDate++;
      countOC++;
    }
    String contrDate2 = this.getValue("ContrDate1");
    if (!"".equals(contrDate2)) {
      retDate = fgUtil.getDateAC(contrDate2, "ñ�����(��)");
      if (retDate.length() != 10) {
        message(retDate);
        getcLabel("ContrDate1").requestFocus();
        return false;
      }
      setValue("ContrDate1", retDate);
      countDate++;
    }
    if (countDate == 1) {
      message("[ñ�����(�_)(��)] ���P�ɭ���C");
      return false;
    }

    // �I�q�Pñ���ݾܤ@�Φ@��
    if (countOC == 0) {
      message("[�I�q���] �P [ñ�����] �����ܤ@�H�W��g");
      return false;
    }

    setValue("BuyerDate1", !"".equals(orderDate1) ? orderDate1 : contrDate1);
    setValue("BuyerDate2", !"".equals(orderDate2) ? orderDate2 : contrDate2);
    
    //�̷~�Z�ѷӤ��P�A����I�q��ñ�����
    String dateType = this.getValue("dateType").trim();
    if("OrderDate".equals(dateType) ) {
      if(orderDate1.length() == 0 || orderDate2.length() == 0) {
        message("�~�Z�ѷӥI�q��A�h�I�q������i����");
        return false;
      }
    }else if("ContrDate".equals(dateType) ) {
      if(contrDate1.length() == 0 || contrDate2.length() == 0) {
        message("�~�Z�ѷ�ñ����A�hñ��������i����");
        return false;
      }
    }
    
    return true;
  }
  
  public boolean action(String value) throws Throwable {
    
    //�I�q&ñ�� ����ˮ�
    if( !this.isBatchCheckOK() ) return false;
    
    // �I�q���
    String stringOrderDate1 = getValue("OrderDate1").trim();
    String stringOrderDate2 = getValue("OrderDate2").trim();
    // �ɨ����
    String stringEnougDate1 = getValue("EnougDate1").trim();
    String stringEnougDate2 = getValue("EnougDate2").trim();
    // ñ�����
    String stringContrDate1 = getValue("ContrDate1").trim();
    String stringContrDate2 = getValue("ContrDate2").trim();
    // �X���|�f
    String stringDateCheck1 = getValue("DateCheck1").trim();
    String stringDateCheck2 = getValue("DateCheck2").trim();
    // ñ�������
    String stringDateRange1 = getValue("DateRange1").trim();
    String stringDateRange2 = getValue("DateRange2").trim();
    //
    String stringKind = getValue("Kind").trim();
    // ������
    String stringBuyerDate1 = getValue("BuyerDate1").trim();
    String stringBuyerDate2 = getValue("BuyerDate2").trim();
    if (stringBuyerDate1.length() == 0 || stringBuyerDate2.length() == 0) {
      message("������ ���i�ť�!");
      return false;
    }
    // �q����
    String stringEDate1 = getValue("EDate1").trim();
    String stringEDate2 = getValue("EDate2").trim();
    // ���I�{
    String stringSellerCashDate1 = getValue("SellerCashDate1").trim();
    String stringSellerCashDate2 = getValue("SellerCashDate2").trim();
    // �R��I�{
    String stringBuyerCashDate1 = getValue("BuyerCashDate1").trim();
    String stringBuyerCashDate2 = getValue("BuyerCashDate2").trim();
    //
    String stringSaleKind = getValue("SaleKind").trim();
    //
    String stringCompanyNo = getValue("CompanyNo").trim();
    //�~�Z�ѷ�
    String strDateType = this.getValue("dateType");
    
    // ����B�z
    Farglory.util.FargloryUtil exeFun = new Farglory.util.FargloryUtil();
    if (stringOrderDate1.length() > 0)
      setValue("OrderDate1", exeFun.getDateAC(stringOrderDate1, "�I�q���"));
    if (stringOrderDate2.length() > 0)
      setValue("OrderDate2", exeFun.getDateAC(stringOrderDate2, "�I�q���"));
    if (stringEnougDate1.length() > 0)
      setValue("EnougDate1", exeFun.getDateAC(stringEnougDate1, "�ɨ����"));
    if (stringEnougDate2.length() > 0)
      setValue("EnougDate2", exeFun.getDateAC(stringEnougDate2, "�ɨ����"));
    if (stringContrDate1.length() > 0)
      setValue("ContrDate1", exeFun.getDateAC(stringContrDate1, "ñ�����"));
    if (stringContrDate2.length() > 0)
      setValue("ContrDate2", exeFun.getDateAC(stringContrDate2, "ñ�����"));
    if (stringDateCheck1.length() > 0)
      setValue("DateCheck1", exeFun.getDateAC(stringDateCheck1, "�X���|�f"));
    if (stringDateCheck2.length() > 0)
      setValue("DateCheck2", exeFun.getDateAC(stringDateCheck2, "�X���|�f"));
    if (stringDateRange1.length() > 0)
      setValue("DateRange1", exeFun.getDateAC(stringDateRange1, "ñ�������"));
    if (stringDateRange2.length() > 0)
      setValue("DateRange2", exeFun.getDateAC(stringDateRange2, "ñ�������"));
    //
    if (stringBuyerDate1.length() > 0)
      setValue("BuyerDate1", exeFun.getDateAC(stringBuyerDate1, "������"));
    if (stringBuyerDate2.length() > 0)
      setValue("BuyerDate2", exeFun.getDateAC(stringBuyerDate2, "������"));
    if (stringEDate1.length() > 0)
      setValue("EDate1", exeFun.getDateAC(stringEDate1, "�q����"));
    if (stringEDate2.length() > 0)
      setValue("EDate2", exeFun.getDateAC(stringEDate2, "�q����"));
    if (stringSellerCashDate1.length() > 0)
      setValue("SellerCashDate1", exeFun.getDateAC(stringSellerCashDate1, "���I�{��"));
    if (stringSellerCashDate2.length() > 0)
      setValue("SellerCashDate2", exeFun.getDateAC(stringSellerCashDate2, "���I�{��"));
    if (stringBuyerCashDate1.length() > 0)
      setValue("BuyerCashDate1", exeFun.getDateAC(stringBuyerCashDate1, "�R��I�{��"));
    if (stringBuyerCashDate2.length() > 0)
      setValue("BuyerCashDate2", exeFun.getDateAC(stringBuyerCashDate2, "�R��I�{��"));
    //
    talk dbSale = getTalk("" + get("put_dbSale"));
    String stringSQL = "";
    String retData[][] = null;
    //
    Farglory.util.FargloryUtil exeUtil = new Farglory.util.FargloryUtil();
    long longTime1 = exeUtil.getTimeInMillis();
    //
    stringSQL = " speMakerSale01R290_AO5_COM2 " 
              + "'" + stringOrderDate1 + "'," 
              + "'" + stringOrderDate2 + "'," 
              + "'" + stringEnougDate1 + "'," 
              + "'" + stringEnougDate2 + "'," 
              + "'" + stringContrDate1 + "'," 
              + "'" + stringContrDate2 + "'," 
              + "'" + stringDateCheck1 + "'," 
              + "'" + stringDateCheck2 + "'," 
              + "'" + stringDateRange1 + "'," 
              + "'" + stringDateRange2 + "'," 
              + "'" + stringKind + "', " 
              + "'" + stringBuyerDate1 + "'," 
              + "'" + stringBuyerDate2 + "'," 
              + "'" + stringEDate1 + "'," 
              + "'" + stringEDate2 + "',"
              + "'" + stringSellerCashDate1 + "'," 
              + "'" + stringSellerCashDate2 + "'," 
              + "'" + stringBuyerCashDate1 + "'," 
              + "'" + stringBuyerCashDate2 + "'," 
              + "'" + stringSaleKind + "' , " 
              + "'" + stringCompanyNo + "'  "
              + ",'" + strDateType + "'  "
              + "WITH RECOMPILE";
    retData = dbSale.queryFromPool(stringSQL);
    if (retData.length == 0) {
      message("�S�����!");
      return false;
    }
    
    //�PAO��ư��ˮ�  (�̷�[�~�Z�ѷ�]���ܤ���϶�)
    String strTypeDate1 = ( "ContrDate".equals(strDateType) )? stringContrDate1 : stringOrderDate1;
    String strTypeDate2 = ( "ContrDate".equals(strDateType) )? stringContrDate2 : stringOrderDate2;
    talk dbAO = getTalk("" + get("put_dbAO"));
    stringSQL = "Select [AgentDEPT4],[Agent_Num],[Agent_Name],SUM(CAST(TEL_V AS real)) AS TEL_V,SUM(CAST(DS_V AS real)) AS DS_V,SUM(CAST(Income_V AS real)) AS Income_V "
        + ",SUM(CAST(Friend_V AS real)) AS Friend_V,SUM(CAST(First_V AS real)) AS First_V,SUM(CAST(Repeat_V AS real)) AS Repeat_V "
        + " from AO_DayPerReportTempShow where (Date_Str between '" + strTypeDate1 + "' and '" + strTypeDate2
        + "') group by [AgentDEPT4], Agent_Num, Agent_Name order by [AgentDEPT4] ";
    String[][] retAOData = dbAO.queryFromPool(stringSQL);
    double tmpNum = 0;
    if (retAOData.length > 0) {
      for (int idx = 0; idx < retData.length; idx++) {
        for (int chkIdx = 0; chkIdx < retAOData.length; chkIdx++) {
          System.out.println("retData:" + retData[idx][2].trim() + " <==> retAOData:" + retAOData[chkIdx][2].trim());
          if (retData[idx][2].trim().equals(retAOData[chkIdx][2].trim())) {
            System.out.println("equals GO");
            retData[idx][3] = retAOData[chkIdx][3];
            tmpNum = Double.parseDouble(retData[idx][4].trim());
            if (tmpNum > 0) {
              retData[idx][5] = Double.toString(Double.parseDouble(retData[idx][3].trim()) / tmpNum);
            }
            retData[idx][7] = retAOData[chkIdx][4];
            tmpNum = Double.parseDouble(retData[idx][8].trim());
            if (tmpNum > 0) {
              retData[idx][9] = Double.toString(Double.parseDouble(retData[idx][7].trim()) / tmpNum);
            }
            retData[idx][11] = retAOData[chkIdx][5];
            tmpNum = Double.parseDouble(retData[idx][12].trim());
            if (tmpNum > 0) {
              retData[idx][13] = Double.toString(Double.parseDouble(retData[idx][11].trim()) / tmpNum);
            }
            retData[idx][15] = retAOData[chkIdx][6];
            tmpNum = Double.parseDouble(retData[idx][16].trim());
            if (tmpNum > 0) {
              retData[idx][17] = Double.toString(Double.parseDouble(retData[idx][15].trim()) / tmpNum);
            }
            // System.out.println("go caculate");
            // retData[idx][17] =
            // String.valueOf(Math.round(Double.parseDouble(retData[idx][15])/Double.parseDouble(retData[idx][16]))
            // );
            retData[idx][19] = retAOData[chkIdx][7];
            tmpNum = Double.parseDouble(retData[idx][20].trim());
            if (tmpNum > 0) {
              retData[idx][21] = Double.toString(Double.parseDouble(retData[idx][19].trim()) / tmpNum);
            }
            retData[idx][23] = retAOData[chkIdx][8];
            tmpNum = Double.parseDouble(retData[idx][24].trim());
            if (tmpNum > 0) {
              retData[idx][25] = Double.toString(Double.parseDouble(retData[idx][23].trim()) / tmpNum);
            }
          }
        }
      }
    }
    //
    Farglory.Excel.FargloryExcel exeExcel = new Farglory.Excel.FargloryExcel();
    Vector retVector = exeExcel.getExcelObject("G:\\��T��\\Excel\\SaleEffect\\Sale01R290.xlt");
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    int intInsertDataRow = 2;
    String stringCondition = "";
    String stringBalaPrint = getValue("BalaPrint").trim();
    //
    stringCondition = "�I�q���:" + stringOrderDate1 + "��" + stringOrderDate2;
    if (stringEnougDate1.length() > 0) {
      stringCondition += ";�ɨ����:" + stringEnougDate1 + "��" + stringEnougDate2;
    }
    if (stringContrDate1.length() > 0) {
      stringCondition += ";ñ�����:" + stringContrDate1 + "��" + stringContrDate2;
    }
    if (stringDateCheck1.length() > 0) {
      stringCondition += ";�X���|�f:" + stringDateCheck1 + "��" + stringDateCheck2;
    }
    if (stringDateRange1.length() > 0) {
      stringCondition += ";ñ�������:" + stringDateRange1 + "��" + stringDateRange2;
    }
    stringCondition += ";" + stringKind + ";" + getDisplayValue("BalaPrint") + ";������:" + stringBuyerDate1 + "��" + stringBuyerDate2;
    if (stringEDate1.length() > 0) {
      stringCondition += ";�q����:" + stringEDate1 + "��" + stringEDate2;
    }
    if (stringSellerCashDate1.length() > 0) {
      stringCondition += ";���I�{��:" + stringSellerCashDate1 + "��" + stringSellerCashDate2;
    }
    if (stringBuyerCashDate1.length() > 0) {
      stringCondition += ";�R��I�{��:" + stringBuyerCashDate1 + "��" + stringBuyerCashDate2;
    }
    stringCondition += ";" + stringSaleKind;
    // �e������
    exeExcel.putDataIntoExcel(0, 0, stringCondition, objectSheet1);
    // �ץX���
    for (int intRow = 0; intRow < retData.length; intRow++) {
      for (int intCol = 0; intCol <= 33; intCol++) {
        if ((intCol == 31 || intCol == 32) && "N".equals(stringBalaPrint)) {
          continue;
        }
        exeExcel.putDataIntoExcel(intCol, intInsertDataRow, retData[intRow][intCol].trim(), objectSheet1);
      }
      intInsertDataRow++;
    }
    //
    exeExcel.doDeleteRows(intInsertDataRow + 1, 1002, objectSheet1);
    //
    exeExcel.setVisiblePropertyOnFlow(true, retVector); // �����㤣��� Excel
    exeExcel.getReleaseExcelObject(retVector);
    //
    long longTime2 = exeUtil.getTimeInMillis();
    System.out.println("���---" + ((longTime2 - longTime1) / 1000) + "��---");
    return false;
  }

  public String getInformation() {
    return "---------------\u5217\u5370\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
