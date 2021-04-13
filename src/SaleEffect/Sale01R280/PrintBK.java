package SaleEffect.Sale01R280;

public class PrintBK {
//回傳值為 true 表示執行接下來的資料庫異動或查詢
  // 回傳值為 false 表示接下來不執行任何指令
  // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
  // 付訂日期
  String stringOrderDate1         = getValue("OrderDate1").trim();
  String stringOrderDate2         = getValue("OrderDate2").trim();
  if(stringOrderDate1.length() == 0  ||  stringOrderDate2.length() == 0){
    message("付訂日期 不可空白!");
    return false;
  }
  // 補足日期
  String stringEnougDate1        = getValue("EnougDate1").trim();
  String stringEnougDate2        = getValue("EnougDate2").trim();
  // 簽約日期
  String stringContrDate1         = getValue("ContrDate1").trim();
  String stringContrDate2         = getValue("ContrDate2").trim();
  // 合約會審
  String stringDateCheck1        = getValue("DateCheck1").trim();
  String stringDateCheck2        = getValue("DateCheck2").trim();
  // 簽約金到期
  String stringDateRange1        = getValue("DateRange1").trim();
  String stringDateRange2        = getValue("DateRange2").trim();
  // 
  String stringKind                     = getValue("Kind").trim();
  // 成交日期
  String stringBuyerDate1         = getValue("BuyerDate1").trim();
  String stringBuyerDate2         = getValue("BuyerDate2").trim();
  if(stringBuyerDate1.length() == 0  ||  stringBuyerDate2.length() == 0){
    message("成交日期 不可空白!");
    return false;
  }
  // 訂單日期
  String stringEDate1                = getValue("EDate1").trim();
  String stringEDate2                = getValue("EDate2").trim();
  // 賣方兌現
  String stringSellerCashDate1 = getValue("SellerCashDate1").trim();
  String stringSellerCashDate2 = getValue("SellerCashDate2").trim();
  // 買方兌現
  String stringBuyerCashDate1 = getValue("BuyerCashDate1").trim();
  String stringBuyerCashDate2 = getValue("BuyerCashDate2").trim();
  //
  String stringSaleKind              = getValue("SaleKind").trim();
  //
  String stringCompanyNo              = getValue("CompanyNo").trim();
  // 日期處理
  Farglory.util.FargloryUtil  exeFun  =  new  Farglory.util.FargloryUtil();
  if(stringOrderDate1.length() > 0)         setValue("OrderDate1", exeFun.getDateAC(stringOrderDate1,"付訂日期"));
  if(stringOrderDate2.length() > 0)         setValue("OrderDate2", exeFun.getDateAC(stringOrderDate2,"付訂日期"));
  if(stringEnougDate1.length() > 0)        setValue("EnougDate1", exeFun.getDateAC(stringEnougDate1,"補足日期"));
  if(stringEnougDate2.length() > 0)        setValue("EnougDate2", exeFun.getDateAC(stringEnougDate2,"補足日期"));
  if(stringContrDate1.length() > 0)          setValue("ContrDate1", exeFun.getDateAC(stringContrDate1,"簽約日期"));
  if(stringContrDate2.length() > 0)          setValue("ContrDate2", exeFun.getDateAC(stringContrDate2,"簽約日期"));
  if(stringDateCheck1.length() > 0)        setValue("DateCheck1", exeFun.getDateAC(stringDateCheck1,"合約會審"));
  if(stringDateCheck2.length() > 0)        setValue("DateCheck2", exeFun.getDateAC(stringDateCheck2,"合約會審"));
  if(stringDateRange1.length() > 0)        setValue("DateRange1", exeFun.getDateAC(stringDateRange1,"簽約金到期"));
  if(stringDateRange2.length() > 0)        setValue("DateRange2", exeFun.getDateAC(stringDateRange2,"簽約金到期"));
  //
  if(stringBuyerDate1.length() > 0)         setValue("BuyerDate1", exeFun.getDateAC(stringBuyerDate1,"成交日期"));
  if(stringBuyerDate2.length() > 0)         setValue("BuyerDate2", exeFun.getDateAC(stringBuyerDate2,"成交日期"));
  if(stringEDate1.length() > 0)                setValue("EDate1", exeFun.getDateAC(stringEDate1,"訂單日期"));
  if(stringEDate2.length() > 0)                setValue("EDate2", exeFun.getDateAC(stringEDate2,"訂單日期"));
  if(stringSellerCashDate1.length() > 0) setValue("SellerCashDate1", exeFun.getDateAC(stringSellerCashDate1,"賣方兌現日"));
  if(stringSellerCashDate2.length() > 0) setValue("SellerCashDate2", exeFun.getDateAC(stringSellerCashDate2,"賣方兌現日"));
  if(stringBuyerCashDate1.length() > 0) setValue("BuyerCashDate1", exeFun.getDateAC(stringBuyerCashDate1,"買方兌現日"));
  if(stringBuyerCashDate2.length() > 0) setValue("BuyerCashDate2", exeFun.getDateAC(stringBuyerCashDate2,"買方兌現日"));
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
    message("沒有資料!");
    return false;
  }
  //與AO資料做檢核
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
   * TODO: 這段修改前是沒作用的，完全不會跑進來修改數值
   * 需確認...
   * 1. 現在跑出來數值是否正確
   * 2. AO SQL跑出來的數值是量還是目標(應該是量)
   * 3. 若是要套用更新後的數字，則達成率要重算。
   * 4. ASP那邊沒有2019年資料?
   * 
   * 結論 : 帶入正確的AO5數字病重算%數
   * (媽的...字串比對用 "==" ，真是天才)
   */
   double tmpNum = 0;
  if(retAOData.length > 0) {
    for(int idx = 0; idx < retData.length; idx++) {
      String retKey = retData[idx][0].trim();
      for(int chkIdx = 0; chkIdx < retAOData.length; chkIdx++) {
        if (retKey.equals(retAOData[chkIdx][0].trim()) )  {
          //電開
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
          //新建檔
          retData[idx][9] = retAOData[chkIdx][3];
          tmpNum = Double.parseDouble(retData[idx][10].trim());
          if ( tmpNum > 0 ) {
            retData[idx][11] = Double.toString(Double.parseDouble(retData[idx][9].trim()) / tmpNum );
          }
          //顧問
          retData[idx][13] = retAOData[chkIdx][4];
          tmpNum = Double.parseDouble(retData[idx][14].trim());
          if ( tmpNum > 0 ) {
            retData[idx][15] = Double.toString(Double.parseDouble(retData[idx][13].trim()) / tmpNum );
          }
          //新來人
          retData[idx][17] = retAOData[chkIdx][5];
          tmpNum = Double.parseDouble(retData[idx][18].trim());
          if ( tmpNum > 0 ) {
            retData[idx][19] = Double.toString(Double.parseDouble(retData[idx][17].trim()) / tmpNum );
          }
          //複來訪
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
  Vector   retVector             = exeExcel.getExcelObject("G:\\資訊室\\Excel\\SaleEffect\\Sale01R280.xlt");
  Dispatch objectSheet1       = (Dispatch)retVector.get(1);
  int          intInsertDataRow = 3;
  String    stringCondition    = "";
  //
  stringCondition = "付訂日期:" + stringOrderDate1 + "～" + stringOrderDate2;
  if(stringEnougDate1.length() > 0){
    stringCondition += ";補足日期:" + stringEnougDate1 + "～" + stringEnougDate2;
  }
  if(stringContrDate1.length() > 0){
    stringCondition += ";簽約日期:" + stringContrDate1 + "～" + stringContrDate2;
  }
  if(stringDateCheck1.length() > 0){
    stringCondition += ";合約會審:" + stringDateCheck1 + "～" + stringDateCheck2;
  }
  if(stringDateRange1.length() > 0){
    stringCondition += ";簽約金到期:" + stringDateRange1 + "～" + stringDateRange2;
  }
  stringCondition += ";" + stringKind+
                  ";成交日期:" + stringBuyerDate1 + "～" + stringBuyerDate2;
  if(stringEDate1.length() > 0){
    stringCondition += ";訂單日期:" + stringEDate1 + "～" + stringEDate2;
  }
  if(stringSellerCashDate1.length() > 0){
    stringCondition += ";賣方兌現日:" + stringSellerCashDate1 + "～" + stringSellerCashDate2;
  }
  if(stringBuyerCashDate1.length() > 0){
    stringCondition += ";買方兌現日:" + stringBuyerCashDate1 + "～" + stringBuyerCashDate2;
  }
  stringCondition += ";" + stringSaleKind;
  //畫面條件
  exeExcel.putDataIntoExcel(  0,  0,  stringCondition,  objectSheet1);
  // 匯出資料
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
  exeExcel.setVisiblePropertyOnFlow(true,  retVector);  // 控制顯不顯示 Excel
  exeExcel.getReleaseExcelObject(retVector);
  //
  long  longTime2  =  exeUtil.getTimeInMillis( );
  System.out.println("實際---" + ((longTime2-longTime1)/1000) + "秒---");
  return false;
}
