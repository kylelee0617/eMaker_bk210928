package SaleEffect.Sale01R280;

public class PrintBK {
//�^�ǭȬ� true ��ܰ��汵�U�Ӫ���Ʈw���ʩάd��
  // �^�ǭȬ� false ��ܱ��U�Ӥ����������O
  // �ǤJ�� value �� "�s�W","�d��","�ק�","�R��","�C�L","PRINT" (�C�L�w�����C�L���s),"PRINTALL" (�C�L�w���������C�L���s) �䤤���@
  // �I�q���
  String stringOrderDate1         = getValue("OrderDate1").trim();
  String stringOrderDate2         = getValue("OrderDate2").trim();
  if(stringOrderDate1.length() == 0  ||  stringOrderDate2.length() == 0){
    message("�I�q��� ���i�ť�!");
    return false;
  }
  // �ɨ����
  String stringEnougDate1        = getValue("EnougDate1").trim();
  String stringEnougDate2        = getValue("EnougDate2").trim();
  // ñ�����
  String stringContrDate1         = getValue("ContrDate1").trim();
  String stringContrDate2         = getValue("ContrDate2").trim();
  // �X���|�f
  String stringDateCheck1        = getValue("DateCheck1").trim();
  String stringDateCheck2        = getValue("DateCheck2").trim();
  // ñ�������
  String stringDateRange1        = getValue("DateRange1").trim();
  String stringDateRange2        = getValue("DateRange2").trim();
  // 
  String stringKind                     = getValue("Kind").trim();
  // ������
  String stringBuyerDate1         = getValue("BuyerDate1").trim();
  String stringBuyerDate2         = getValue("BuyerDate2").trim();
  if(stringBuyerDate1.length() == 0  ||  stringBuyerDate2.length() == 0){
    message("������ ���i�ť�!");
    return false;
  }
  // �q����
  String stringEDate1                = getValue("EDate1").trim();
  String stringEDate2                = getValue("EDate2").trim();
  // ���I�{
  String stringSellerCashDate1 = getValue("SellerCashDate1").trim();
  String stringSellerCashDate2 = getValue("SellerCashDate2").trim();
  // �R��I�{
  String stringBuyerCashDate1 = getValue("BuyerCashDate1").trim();
  String stringBuyerCashDate2 = getValue("BuyerCashDate2").trim();
  //
  String stringSaleKind              = getValue("SaleKind").trim();
  //
  String stringCompanyNo              = getValue("CompanyNo").trim();
  // ����B�z
  Farglory.util.FargloryUtil  exeFun  =  new  Farglory.util.FargloryUtil();
  if(stringOrderDate1.length() > 0)         setValue("OrderDate1", exeFun.getDateAC(stringOrderDate1,"�I�q���"));
  if(stringOrderDate2.length() > 0)         setValue("OrderDate2", exeFun.getDateAC(stringOrderDate2,"�I�q���"));
  if(stringEnougDate1.length() > 0)        setValue("EnougDate1", exeFun.getDateAC(stringEnougDate1,"�ɨ����"));
  if(stringEnougDate2.length() > 0)        setValue("EnougDate2", exeFun.getDateAC(stringEnougDate2,"�ɨ����"));
  if(stringContrDate1.length() > 0)          setValue("ContrDate1", exeFun.getDateAC(stringContrDate1,"ñ�����"));
  if(stringContrDate2.length() > 0)          setValue("ContrDate2", exeFun.getDateAC(stringContrDate2,"ñ�����"));
  if(stringDateCheck1.length() > 0)        setValue("DateCheck1", exeFun.getDateAC(stringDateCheck1,"�X���|�f"));
  if(stringDateCheck2.length() > 0)        setValue("DateCheck2", exeFun.getDateAC(stringDateCheck2,"�X���|�f"));
  if(stringDateRange1.length() > 0)        setValue("DateRange1", exeFun.getDateAC(stringDateRange1,"ñ�������"));
  if(stringDateRange2.length() > 0)        setValue("DateRange2", exeFun.getDateAC(stringDateRange2,"ñ�������"));
  //
  if(stringBuyerDate1.length() > 0)         setValue("BuyerDate1", exeFun.getDateAC(stringBuyerDate1,"������"));
  if(stringBuyerDate2.length() > 0)         setValue("BuyerDate2", exeFun.getDateAC(stringBuyerDate2,"������"));
  if(stringEDate1.length() > 0)                setValue("EDate1", exeFun.getDateAC(stringEDate1,"�q����"));
  if(stringEDate2.length() > 0)                setValue("EDate2", exeFun.getDateAC(stringEDate2,"�q����"));
  if(stringSellerCashDate1.length() > 0) setValue("SellerCashDate1", exeFun.getDateAC(stringSellerCashDate1,"���I�{��"));
  if(stringSellerCashDate2.length() > 0) setValue("SellerCashDate2", exeFun.getDateAC(stringSellerCashDate2,"���I�{��"));
  if(stringBuyerCashDate1.length() > 0) setValue("BuyerCashDate1", exeFun.getDateAC(stringBuyerCashDate1,"�R��I�{��"));
  if(stringBuyerCashDate2.length() > 0) setValue("BuyerCashDate2", exeFun.getDateAC(stringBuyerCashDate2,"�R��I�{��"));
  //
  talk  dbSale  =  getTalk(""+get("put_dbSale"));
  talk  dbAO  =  getTalk(""+get("put_dbAO"));
  String stringSQL = "";
  String retData[][] = null;
  //
  Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil();
  long  longTime1  =  exeUtil.getTimeInMillis( );
  //
  stringSQL = " speMakerSale01R280_AO5_COM " +
  //stringSQL = " speMakerSale01R280_AO5 " +
            "'" + stringOrderDate1 + "'," +
            "'" + stringOrderDate2 + "'," +
            "'" + stringEnougDate1 + "'," +
            "'" + stringEnougDate2 + "'," +
            "'" + stringContrDate1 + "'," +
            "'" + stringContrDate2 + "'," +
            "'" + stringDateCheck1 + "'," +
            "'" + stringDateCheck2 + "'," +
            "'" + stringDateRange1 + "'," +
            "'" + stringDateRange2 + "'," +
            "'" + stringKind + "', " +
            "'" + stringBuyerDate1 + "'," +
            "'" + stringBuyerDate2 + "'," +
            "'" + stringEDate1 + "'," +
            "'" + stringEDate2 + "'," +
            "'" + stringSellerCashDate1 + "'," +
            "'" + stringSellerCashDate2 + "'," +
            "'" + stringBuyerCashDate1 + "'," +
            "'" + stringBuyerCashDate2 + "'," +
            //"'" + stringSaleKind + "'  "+
            "'" + stringSaleKind + "' , "+
            "'" + stringCompanyNo + "'  "+
            "WITH RECOMPILE";
  retData = dbSale.queryFromPool(stringSQL);
  if(retData.length == 0){
    message("�S�����!");
    return false;
  }
  //�PAO��ư��ˮ�
  stringSQL = "Select [AgentDEPT4],SUM(CAST(TEL_V AS real)) AS TEL_V,SUM(CAST(DS_V AS real)) AS DS_V,SUM(CAST(Income_V AS real)) AS Income_V "+
  ",SUM(CAST(Friend_V AS real)) AS Friend_V,SUM(CAST(First_V AS real)) AS First_V,SUM(CAST(Repeat_V AS real)) AS Repeat_V "+
  " from AO_DayPerReportTempShow where (Date_Str between '"+stringOrderDate1+"' and '"+stringOrderDate2+"') group by [AgentDEPT4] order by [AgentDEPT4] ";
  String[][] retAOData = dbAO.queryFromPool(stringSQL);
  System.out.println(">>>AO Length = " + retAOData.length);
  // if(retAOData.length > 0) {
  //  for(int idx = 0; idx < retData.length; idx++) {
  //    for(int chkIdx = 0; chkIdx < retAOData.length; chkIdx++) {
  //      if(retData[idx][1]  == retAOData[chkIdx][0]) {
  //        retData[idx][3] = retAOData[chkIdx][1];
  //        retData[idx][7] = retAOData[chkIdx][2];
  //        retData[idx][11] = retAOData[chkIdx][3];
  //        retData[idx][15] = retAOData[chkIdx][4];
  //        retData[idx][19] = retAOData[chkIdx][5];
  //        retData[idx][23] = retAOData[chkIdx][6];
  //      }
  //    }
  //  }
  // }
  /**
   * 
   * TODO: �o�q�ק�e�O�S�@�Ϊ��A�������|�]�i�ӭק�ƭ�
   * �ݽT�{...
   * 1. �{�b�]�X�ӼƭȬO�_���T
   * 2. AO SQL�]�X�Ӫ��ƭȬO�q�٬O�ؼ�(���ӬO�q)
   * 3. �Y�O�n�M�Χ�s�᪺�Ʀr�A�h�F���v�n����C
   * 4. ASP����S��2019�~���?
   * 
   * ���� : �a�J���T��AO5�Ʀr�f����%��
   * (����...�r����� "==" �A�u�O�Ѥ~)
   */
   double tmpNum = 0;
  if(retAOData.length > 0) {
    for(int idx = 0; idx < retData.length; idx++) {
      String retKey = retData[idx][0].trim();
      for(int chkIdx = 0; chkIdx < retAOData.length; chkIdx++) {
        if (retKey.equals(retAOData[chkIdx][0].trim()) )  {
          //�q�}
          retData[idx][1] = retAOData[chkIdx][1];
          tmpNum = Double.parseDouble(retData[idx][2].trim());
          if ( tmpNum > 0 ) {
            retData[idx][3] = Double.toString(Double.parseDouble(retData[idx][1].trim()) / tmpNum );
          }
          
          //DS
          retData[idx][5] = retAOData[chkIdx][2];
          tmpNum = Double.parseDouble(retData[idx][6].trim());
          if ( tmpNum > 0 ) {
            retData[idx][7] = Double.toString(Double.parseDouble(retData[idx][5].trim()) / tmpNum );
          }
          //�s����
          retData[idx][9] = retAOData[chkIdx][3];
          tmpNum = Double.parseDouble(retData[idx][10].trim());
          if ( tmpNum > 0 ) {
            retData[idx][11] = Double.toString(Double.parseDouble(retData[idx][9].trim()) / tmpNum );
          }
          //�U��
          retData[idx][13] = retAOData[chkIdx][4];
          tmpNum = Double.parseDouble(retData[idx][14].trim());
          if ( tmpNum > 0 ) {
            retData[idx][15] = Double.toString(Double.parseDouble(retData[idx][13].trim()) / tmpNum );
          }
          //�s�ӤH
          retData[idx][17] = retAOData[chkIdx][5];
          tmpNum = Double.parseDouble(retData[idx][18].trim());
          if ( tmpNum > 0 ) {
            retData[idx][19] = Double.toString(Double.parseDouble(retData[idx][17].trim()) / tmpNum );
          }
          //�ƨӳX
          retData[idx][21] = retAOData[chkIdx][6];
          tmpNum = Double.parseDouble(retData[idx][22].trim());
          if ( tmpNum > 0 ) {
            retData[idx][23] = Double.toString(Double.parseDouble(retData[idx][21].trim()) / tmpNum );
          }
        }
      }
    }
  }
  Farglory.Excel.FargloryExcel  exeExcel  =  new  Farglory.Excel.FargloryExcel();
  Vector   retVector             = exeExcel.getExcelObject("G:\\��T��\\Excel\\SaleEffect\\Sale01R280.xlt");
  Dispatch objectSheet1       = (Dispatch)retVector.get(1);
  int          intInsertDataRow = 3;
  String    stringCondition    = "";
  //
  stringCondition = "�I�q���:" + stringOrderDate1 + "��" + stringOrderDate2;
  if(stringEnougDate1.length() > 0){
    stringCondition += ";�ɨ����:" + stringEnougDate1 + "��" + stringEnougDate2;
  }
  if(stringContrDate1.length() > 0){
    stringCondition += ";ñ�����:" + stringContrDate1 + "��" + stringContrDate2;
  }
  if(stringDateCheck1.length() > 0){
    stringCondition += ";�X���|�f:" + stringDateCheck1 + "��" + stringDateCheck2;
  }
  if(stringDateRange1.length() > 0){
    stringCondition += ";ñ�������:" + stringDateRange1 + "��" + stringDateRange2;
  }
  stringCondition += ";" + stringKind+
                  ";������:" + stringBuyerDate1 + "��" + stringBuyerDate2;
  if(stringEDate1.length() > 0){
    stringCondition += ";�q����:" + stringEDate1 + "��" + stringEDate2;
  }
  if(stringSellerCashDate1.length() > 0){
    stringCondition += ";���I�{��:" + stringSellerCashDate1 + "��" + stringSellerCashDate2;
  }
  if(stringBuyerCashDate1.length() > 0){
    stringCondition += ";�R��I�{��:" + stringBuyerCashDate1 + "��" + stringBuyerCashDate2;
  }
  stringCondition += ";" + stringSaleKind;
  //�e������
  exeExcel.putDataIntoExcel(  0,  0,  stringCondition,  objectSheet1);
  // �ץX���
  for(int intRow=0;intRow<retData.length;intRow++){
    exeExcel.putDataIntoExcel(  0,  intInsertDataRow,  ""+(intRow+1),  objectSheet1);
    for(int intCol=0;intCol<=34;intCol++){
      exeExcel.putDataIntoExcel(intCol+1,  intInsertDataRow,  retData[intRow][intCol].trim(),  objectSheet1);
      // System.out.println(">>>" +intRow + "-" + intCol + ">>>" + retData[intRow][intCol].trim() );
    }
    intInsertDataRow++;
  }
  //
  exeExcel.doDeleteRows(intInsertDataRow+1,  153,  objectSheet1);
  //
  exeExcel.setVisiblePropertyOnFlow(true,  retVector);  // �����㤣��� Excel
  exeExcel.getReleaseExcelObject(retVector);
  //
  long  longTime2  =  exeUtil.getTimeInMillis( );
  System.out.println("���---" + ((longTime2-longTime1)/1000) + "��---");
  return false;
}
