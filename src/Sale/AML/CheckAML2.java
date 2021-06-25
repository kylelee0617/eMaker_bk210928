package Sale.AML;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import Farglory.util.AMLBean;
import Farglory.util.AMLTools;
import Farglory.util.KUtils;
import jcx.db.talk;
import jcx.jform.bproc;

public class CheckAML2 extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    //20191107 洗錢及資恐風險管理政策處理程序作業+防制洗錢及打擊資恐風險評估處理程序作業+洗錢及資恐名單比對處理程序作業
    System.out.println("===========AML============S");
    talk  dbSale =  getTalk("Sale") ;
    talk  db400CRM =  getTalk("400CRM") ;
    talk  dbPW0D =  getTalk("pw0d") ;
    talk  dbJGENLIB  =  getTalk("JGENLIB") ;
    talk  dbEIP  =  getTalk("EIP") ;
    String strSaleSql = "";
    String str400CRMSql = "";
    String strPW0DSql = "";
    String strJGENLIBSql = "";
    String strEIPSql = "";
    String strBDaysql = "";
    String str400sql = "";
    String stringSQL = "";
    String strPW0Dsql = "";
    String[][]   ret080Table;//現金
    String[][]   ret083Table;//信用卡
    String[][]   ret328Table;//銀行
    String[][]   ret082Table;//票據
    String[][]  ret070Table;
    String[][] retPDCZPFTable;
    String[][] retQueryLog;
    String[][] retCList;
    //取畫面值
    String strActionName =  getValue("actionName").trim() ;//作動名稱
    String strCreditCardMoney  =  getValue("CreditCardMoney").trim() ;//信用卡
    String strCashMoney  =  getValue("CashMoney").trim() ;//現金
    String strBankMoney  =  getValue("BankMoney").trim() ;//銀行
    String strCheckMoney  =  getValue("CheckMoney").trim() ;//票據
    String strReceiveMoney = getValue("ReceiveMoney").trim() ;//收款總額
    String strProjectID1 =  getValue("field2").trim() ;//案別代碼
    String strEDate =  getValue("field3").trim() ;//收款日期
    String strDocNo =  getValue("field4").trim() ;//編號
    if("".equals(strCreditCardMoney)){
      strCreditCardMoney = "0";
    }
    if("".equals(strCashMoney)||"0.0".equals(strCashMoney)){
      strCashMoney = "0";
    }
    if("".equals(strBankMoney)||"0.0".equals(strBankMoney)){
      strBankMoney = "0";
    }
    if("".equals(strCheckMoney)){
      strCheckMoney = "0";
    }
    //代繳人相關
    String strDeputy=getValue("PaymentDeputy").trim();
    String strDeputyName = getValue("DeputyName").trim();
    String strDeputyID=getValue("DeputyID").trim();
    String strDeputyRelationship = getValue("DeputyRelationship").trim();
    String bStatus=getValue("B_STATUS").trim();
    String cStatus=getValue("C_STATUS").trim();
    String rStatus=getValue("R_STATUS").trim();
    //購買人姓名
    String allOrderID = "";
    String allOrderName = "";
    String percentage = "";
    String[][] orderCustomTable =  getTableData("table3");
    for (int g = 0; g < orderCustomTable.length; g++) {
      if("".equals(allOrderName)){
        allOrderID =  orderCustomTable[g][3].trim();
        allOrderName =  orderCustomTable[g][4].trim();
        percentage = orderCustomTable[g][5].trim();
      }else{
        allOrderID = allOrderID+"、"+ orderCustomTable[g][3].trim();
        allOrderName = allOrderName+"、"+ orderCustomTable[g][4].trim();
        percentage = percentage+"、"+ orderCustomTable[g][5].trim();
      }
    }
    //13,14
    String rule13=getValue("Rule13").trim();
    String rule14=getValue("Rule14").trim();
    //共用
    String errMsg="";
    String allCustomName = allOrderName;
    String allCustomID = allOrderID;
    //收款日期民國格式
    String[] tempEDate = strEDate.split("/");
    String rocDate = "";
    String year = tempEDate[0];
    int intYear = Integer.parseInt(year) - 1911;
    rocDate = Integer.toString(intYear)+ tempEDate[1]+ tempEDate[2];
    //LOG NOW DATE
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    String strNowDate = nowsdf.format(now);
    String tempROCYear=""+(Integer.parseInt(strNowDate.substring(0,strNowDate.length()-4))-1911);
    String RocNowDate = tempROCYear+strNowDate.substring(strNowDate.length()-4,strNowDate.length());
    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HHmmss");
    String strNowTime = nowTimeSdf.format(now);
    SimpleDateFormat nowTimestampSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String strNowTimestamp =  nowTimestampSdf.format(now);
    //員編
    String userNo = getUser().toUpperCase().trim();
    String empNo="";
    String [][] retEip=null;
    strEIPSql="SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'" ;
    retEip = dbEIP.queryFromPool(strEIPSql);
    if(retEip.length>0){
      empNo=retEip[0][0] ;
    }
    //購物證明單號
    String strOrderNo = "";
    String orderNos = "";
    String[][] orderNoTable =  getTableData("table4");
    strOrderNo=orderNoTable[0][2].trim();
    for (int g = 0; g < orderNoTable.length; g++) {
      if("".equals(orderNos)){
        orderNos =  orderNoTable[g][2].trim();
      }else{
        orderNos += "、"+ orderNoTable[g][2].trim();
      }
    }
    
    //actionNo
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    String actionNo =strNowDate+ strNowTime+ram;
    
    
    //start of 樣態1~4  Kyle
    //1同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。(本單要命中)
    //2同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。
    //3同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。
    //4同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    if(strCashMoney == null || "".equals(strCashMoney)) { //現金總額
      strCashMoney = "0";
    }
    if(strCreditCardMoney == null || "".equals(strCreditCardMoney)) { //信用卡總額
      strCreditCardMoney = "0";
    }
    if(strBankMoney == null || "".equals(strBankMoney)) { //銀行總額
      strBankMoney = "0";
    }
    if(strCheckMoney == null || "".equals(strCheckMoney)) { //票據總額
      strCheckMoney = "0";
    }
    if(strReceiveMoney == null || "".equals(strReceiveMoney)) { //收款單總額
      strReceiveMoney = "0";
    }
    double dCashMoney = Double.parseDouble(strCashMoney);
    double dCheckMoney = Double.parseDouble(strCheckMoney);
    double dCreditMoney = Double.parseDouble(strCreditCardMoney);
    double dBankMoney = Double.parseDouble(strBankMoney);
    double dReceiveMoney = Double.parseDouble(strReceiveMoney);
    String[] orderNoss = orderNos.split("、");
    String[] customNos = allCustomID.split("、");
    String[] percentages = percentage.split("、");
    
    KUtils kutil = new KUtils();
    String tempMsg = "";
    AMLBean aml = new AMLBean();
    aml.setDocNo(strDocNo);
	aml.seteDate(strEDate);
    aml.setOrderNo(strOrderNo);
    aml.setProjectID1(strProjectID1);
    aml.setFuncName("收款");
    aml.setFuncName2("交易");
    aml.setActionName("存檔");
    aml.setCustomTitle("客戶");
    aml.setTrxDate(strEDate);
    aml.setOrderNos(kutil.genQueryInString(orderNoss));
    aml.setCustomNos(kutil.genQueryInString(customNos));
    aml.setCustomNames(allCustomName);
    AMLTools amlTool = new AMLTools(aml);

    //態樣1 - 訂單 - 照比例計算各項總額
    for (int g = 0; g < orderNoss.length; g++) {
      double pers = 1 / orderNoss.length;
      if( (dCashMoney*pers >= 450000 && dCashMoney*pers <= 499999) || (dCreditMoney*pers >= 450000 && dCreditMoney*pers <= 499999)
        || (dCheckMoney*pers >= 450000 && dCheckMoney*pers <= 499999) || (dBankMoney*pers >= 450000 && dBankMoney*pers <= 499999) ) {
        tempMsg = amlTool.chkAML001(aml, "order").getData().toString();
        if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
      }
    }
    
    //態樣1 - 個人 - 照比例計算各項總額
    for (int g = 0; g < customNos.length; g++) {
      double pers = Double.parseDouble(percentages[g].trim()) / 100;
      if ((dCashMoney*pers >= 450000 && dCashMoney*pers <= 499999) || (dCreditMoney*pers >= 450000 && dCreditMoney*pers <= 499999)
          || (dCheckMoney*pers >= 450000 && dCheckMoney*pers <= 499999) || (dBankMoney*pers >= 450000 && dBankMoney*pers <= 499999)) {
        tempMsg = amlTool.chkAML001(aml, "custom").getData().toString();
        if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
      }
    }
    
    //態樣2
    //本單若有一筆現金或匯款介於45~49則檢查前兩天
    //Tips: 訂單跟客戶要分開處理
    if(dCashMoney > 0 || dBankMoney > 0) {
      if( (dCashMoney >= 450000 && dCashMoney <= 499999) || (dBankMoney >= 450000 && dBankMoney <= 499999) ) {  //訂單
        tempMsg = amlTool.chkAML002(aml , "order").getData().toString();
        if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
      }
      for(int g=0 ; g<customNos.length ; g++) {
        if( (dCashMoney*Double.parseDouble(percentages[g].trim())/100 >= 450000 && dCashMoney*Double.parseDouble(percentages[g].trim())/100 <= 499999) 
            || (dBankMoney*Double.parseDouble(percentages[g].trim())/100 >= 450000 && dBankMoney*Double.parseDouble(percentages[g].trim())/100 <= 499999) ) {  //客戶
          aml.setCustomId( customNos[g].trim() );
          tempMsg = amlTool.chkAML002(aml , "custom").getData().toString();
          if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
        }
      }
    }
    
    //態樣3
    if(dCashMoney > 0) {
      for (int g = 0; g < orderNoss.length; g++) {
        tempMsg = amlTool.chkAML0031(aml , orderNoss[g].trim(), "order").getData().toString();
        if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
      }
      
      for(int g=0 ; g<customNos.length ; g++) {
        tempMsg = amlTool.chkAML0031(aml , customNos[g].trim() , "custom").getData().toString();
        if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
      }
    }
    
    //態樣4
    //本訂單第一次天繳款??
    String sql = "";
    String sqlEDate = "";
    for (int g = 0; g < orderNoss.length; g++) {
      sql = "select Top 1 EDate from sale05m080 a , Sale05M086 b where a.DocNo=b.DocNo and b.OrderNo = '"+ orderNoss[g].trim() +"' ORDER BY EDate";
      sqlEDate = dbSale.queryFromPool(sql)[0][0].toString().trim();
      if( !(strEDate.equals(sqlEDate)) && dCashMoney > 0 ) {
        //代表不是本訂單第一天繳款 & 有收現金
        tempMsg = amlTool.chkAML0041(aml , orderNoss[g].trim(), "order").getData().toString();
        if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
      }
    }
    //個人第一天繳款?
    for(int g=0 ; g<customNos.length ; g++) {
      sql = "select top 1 EDate from sale05m080 a , Sale05M084 b where a.DocNo=b.DocNo and  b.CustomNo = '"+customNos[g].trim()+"' order by EDate";
      sqlEDate = dbSale.queryFromPool(sql)[0][0].toString().trim();
      if( (!strEDate.equals(sqlEDate)) && dCashMoney > 0 ) {
        //代表不是此人第一天繳款 & 有收現金
        tempMsg = amlTool.chkAML0041(aml , customNos[g].trim() , "custom").getData().toString();
        if( !errMsg.contains(tempMsg) ) errMsg += tempMsg;
      }
    }
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //End of 態樣1~4 Kyle
    
    //洗錢追蹤流水號
    //20201207 Kyle : 因為態樣1~4另外獨立處理，避免影響原流水號運行，故稍微下移
    int intRecordNo =1;
    //strSaleSql = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='"+strOrderNo+"'";
    strSaleSql = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE DocNo ='"+strDocNo+"'";
    ret070Table = dbSale.queryFromPool(strSaleSql);
    if(!"".equals(ret070Table[0][0].trim())){
      intRecordNo = Integer.parseInt(ret070Table[0][0].trim())+1;
    }
    
    //Pattern5,8,9,10,11,17~20
    //信用卡
    ret083Table  =  getTableData("table5");
    if(ret083Table.length > 0) {
      for(int e=0;e<ret083Table.length;e++){
        String str083Deputy = ret083Table[e][7].trim();//本人繳款
        String str083DeputyName=ret083Table[e][8].trim();//姓名
        String str083DeputyId=ret083Table[e][9].trim();//身分證號
        String str083Rlatsh=ret083Table[e][10].trim();//關係
        String str083Rstatus=ret083Table[e][14].trim();//利關人
        
        //不適用LOG_2,3,4,6,7,9,10,11,12,15,16 (請告訴我不用迴圈寫的理由)
        int[] noUseAML = {2 , 3 , 4 , 6 , 7, 9, 10, 11, 12, 15, 16};
        Map mapAMLMsg = amlTool.getAMLReTurn();
        for(int ii=0 ; ii<noUseAML.length ; ii++) {
          String amlNo = "";
          if( noUseAML[ii]<10 ) {
            amlNo = "00" + noUseAML[ii];
          }else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                                    .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          strSaleSql = "INSERT INTO Sale05M070 "
                     + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                     + "VALUES "
                     + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                     + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                     + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
        }
        
        if("Y".equals(str083Deputy)){ //有代繳人
          //代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
          if("朋友".equals(str083Rlatsh) || "其他".equals(str083Rlatsh)){
            //Sale05M070
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
            //AS400
            strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbJGENLIB.execFromPool(strJGENLIBSql);
            errMsg += "信用卡代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
          }else{
            //不符合
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"','不符合','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
          
          //不動產銷售由第三方代理或繳款，系統檢核提示通報。
          //Sale05M070
          strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
          //AS400
          strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbJGENLIB.execFromPool(strJGENLIBSql);
          errMsg +="信用卡代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";
          
          //客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
          if("Y".equals(str083Rstatus)){
            strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
            //AS400
            strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbJGENLIB.execFromPool(strJGENLIBSql);

            errMsg += "信用卡代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
          }else{
            //不符合
            strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款','信用卡資料','"+strActionName+"', '不符合','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
        }else{
          //不適用
          int[] noUseAML1 = {5 ,8 ,17 ,19 ,20};
          mapAMLMsg = amlTool.getAMLReTurn();
          for(int ii=0 ; ii<noUseAML1.length ; ii++) {
            String amlNo = "";
            if( noUseAML1[ii]<10 ) {
              amlNo = "00" + noUseAML1[ii];
            }else {
              amlNo = "0" + noUseAML1[ii];
            }
            String amlDesc = mapAMLMsg.get(amlNo).toString()
                                      .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
            strSaleSql = "INSERT INTO Sale05M070 "
                       + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                       + "VALUES "
                       + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                       + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                       + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
        }
      }
    }
    
    //現金(只有一筆)
    if(StringUtils.isNumeric(strCashMoney) && Double.parseDouble(strCashMoney) > 0) {
      //不適用LOG_6,9,10,11,12,15,16
      int[] noUseAML = {6,9,10,11,12,15,16};
      Map mapAMLMsg = amlTool.getAMLReTurn();
      for(int ii=0 ; ii<noUseAML.length ; ii++) {
        String amlNo = "";
        if( noUseAML[ii]<10 ) {
          amlNo = "00" + noUseAML[ii];
        }else {
          amlNo = "0" + noUseAML[ii];
        }
        String amlDesc = mapAMLMsg.get(amlNo).toString()
                                  .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
        strSaleSql = "INSERT INTO Sale05M070 "
                   + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                   + "VALUES "
                   + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                   + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                   + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
        dbSale.execFromPool(strSaleSql);
        intRecordNo++;
      }

      if("Y".equals(strDeputy)){//有代繳人
        //代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
        if("朋友".equals(strDeputyRelationship) || "其他".equals(strDeputyRelationship)){
          //Sale05M070
          strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款','現金資料','"+strActionName+"', '代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
          //AS400
          strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbJGENLIB.execFromPool(strJGENLIBSql);

          errMsg += "現金代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
        }else{
          //不符合
          strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款','現金資料','"+strActionName+"', '不符合','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
        }
        
        //不動產銷售由第三方代理或繳款，系統檢核提示通報。
        //Sale05M070
        strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','現金資料','"+strActionName+"','代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
        dbSale.execFromPool(strSaleSql);
        intRecordNo++;
        //AS400
        strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
        dbJGENLIB.execFromPool(strJGENLIBSql);

        errMsg += "現金代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";
        
        
        //客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
        if("Y".equals(rStatus)){
          strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','現金資料','"+strActionName+"', '代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
          //AS400
          strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbJGENLIB.execFromPool(strJGENLIBSql);

          errMsg += "現金代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
        }else{
          //不符合
          strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','現金資料','"+strActionName+"', '不符合','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
        }
      }else{
        //現金本人繳款不適用5,8,17,18,19,20
        int[] noUseAML1 = {5 ,8 ,17 ,19 ,20};
        mapAMLMsg = amlTool.getAMLReTurn();
        for(int ii=0 ; ii<noUseAML1.length ; ii++) {
          String amlNo = "";
          if( noUseAML1[ii]<10 ) {
            amlNo = "00" + noUseAML1[ii];
          }else {
            amlNo = "0" + noUseAML1[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                                    .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          strSaleSql = "INSERT INTO Sale05M070 "
                     + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                     + "VALUES "
                     + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                     + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                     + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
        }
      }
    }
    
    //銀行匯款
    ret328Table  =  getTableData("table9");
    if(ret328Table.length > 0) {
      for(int f=0;f<ret328Table.length;f++){
        String str328Deputy = ret328Table[f][5].trim();//本人繳款
        String str328DeputyName=ret328Table[f][6].trim();//姓名
        String str328DeputyId=ret328Table[f][7].trim();
        String str328ExPlace=ret328Table[f][8].trim();
        String str328Rlatsh=ret328Table[f][9].trim();
        String str328bStatus=ret328Table[f][11].trim();
        String str328cStatus=ret328Table[f][12].trim();
        String str328rStatus=ret328Table[f][13].trim();

        //不適用3,4,6,7,9,11,12,15,16
        int[] noUseAML = {3,4,6,7,9,11,12,15,16};
        Map mapAMLMsg = amlTool.getAMLReTurn();
        for(int ii=0 ; ii<noUseAML.length ; ii++) {
          String amlNo = "";
          if( noUseAML[ii]<10 ) {
            amlNo = "00" + noUseAML[ii];
          }else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                                    .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          strSaleSql = "INSERT INTO Sale05M070 "
                     + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                     + "VALUES "
                     + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                     + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                     + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
        }
      
        
        //自金融監督管理委員會函轉國際防制洗錢組織所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循國際防制洗錢組織建議之國家或地區匯入之交易款項。
        strJGENLIBSql =  "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09 = '" + str328ExPlace + "'";
        retPDCZPFTable = dbJGENLIB.queryFromPool(strJGENLIBSql);
        if(retPDCZPFTable.length > 0){
          String strCZ07 =retPDCZPFTable[0][0].trim();
          if("優先法高".equals(strCZ07)){
            //Sale05M070
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款','銀行資料','"+strActionName+"', '代繳款人"+str328DeputyName+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','010','代繳款人"+str328DeputyName+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
            //AS400
            strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '010', '自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbJGENLIB.execFromPool(strJGENLIBSql);
            
            String strTempMsg = "";
            if("Y".equals(str328Deputy)){
              strTempMsg = "銀行代繳款人"+str328DeputyName;
            }else{
              strTempMsg = "客戶"+allOrderName;
            }
            errMsg += strTempMsg+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。\n";
          }else{
            //不符合
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款','銀行資料','"+strActionName+"', '不符合','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','010','代繳款人"+str328DeputyName+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
        }
        
        if("Y".equals(str328Deputy)){//有代繳人
          //代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
          if("朋友".equals(str328Rlatsh) || "其他".equals(str328Rlatsh)){
            //Sale05M070
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName, RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','銀行資料','"+strActionName+"','代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
            //AS400
            strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbJGENLIB.execFromPool(strJGENLIBSql);

            errMsg += "銀行代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
          }else{
            //不符合
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName, RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','銀行資料','"+strActionName+"','不符合','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
          
          //不動產銷售由第三方代理或繳款，系統檢核提示通報。
          //Sale05M070
          strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','銀行資料','"+strActionName+"','代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
          //AS400
          strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbJGENLIB.execFromPool(strJGENLIBSql);
          errMsg += "銀行代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";
          
          //客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
          if("Y".equals(str328rStatus)){
            strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','銀行資料','"+strActionName+"', '代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
            //AS400
            strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbJGENLIB.execFromPool(strJGENLIBSql);

            errMsg += "銀行代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
          }else{
            //不符合
            strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','銀行資料','"+strActionName+"', '不符合','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
        }else{
          //本人繳款(不適用)5,8,10,17,19,20,21
          int[] noUseAML1 = {5,8,10,17,19,20,21};
          mapAMLMsg = amlTool.getAMLReTurn();
          for(int ii=0 ; ii<noUseAML1.length ; ii++) {
            String amlNo = "";
            if( noUseAML1[ii]<10 ) {
              amlNo = "00" + noUseAML1[ii];
            }else {
              amlNo = "0" + noUseAML1[ii];
            }
            String amlDesc = mapAMLMsg.get(amlNo).toString()
                                      .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
            strSaleSql = "INSERT INTO Sale05M070 "
                       + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                       + "VALUES "
                       + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                       + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                       + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
        } 
      }
    }
    
    //票據
    ret082Table  =  getTableData("table2");
    if(ret082Table.length > 0) {
      for(int g=0;g<ret082Table.length;g++){
        String str082Deputy = ret082Table[g][8].trim();//本人繳款
        String str082DeputyName=ret082Table[g][9].trim();//姓名
        String str082DeputyId=ret082Table[g][10].trim();//身分證號
        String str082Rlatsh=ret082Table[g][11].trim();//關係
        String str082Bstatus=ret082Table[g][13].trim();//黑名單
        String str082Cstatus=ret082Table[g][14].trim();//控管名單
        String str082Rstatus=ret082Table[g][15].trim();//利關人
        
        //不適用2,3,4,6,7,9,10,11,12,15,16
        int[] noUseAML = {2,3,4,6,7,9,10,11,12,15,16};
        Map mapAMLMsg = amlTool.getAMLReTurn();
        for(int ii=0 ; ii<noUseAML.length ; ii++) {
          String amlNo = "";
          if( noUseAML[ii]<10 ) {
            amlNo = "00" + noUseAML[ii];
          }else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                                    .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          strSaleSql = "INSERT INTO Sale05M070 "
                     + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                     + "VALUES "
                     + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                     + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                     + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
        }
        
      
        if("Y".equals(str082Deputy)){//有代繳人
          
          //代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
          if("朋友".equals(str082Rlatsh) || "其他".equals(str082Rlatsh)){
            //Sale05M070
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','票據資料','"+strActionName+"','代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
            //AS400
            strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbJGENLIB.execFromPool(strJGENLIBSql);

            errMsg += "票據代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
          }else{
            //不符合
            strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','票據資料','"+strActionName+"','不符合','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
          //不動產銷售由第三方代理或繳款，系統檢核提示通報。
          //Sale05M070
          strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','票據資料','"+strActionName+"','代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbSale.execFromPool(strSaleSql);
          intRecordNo++;
          //AS400
          strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
          dbJGENLIB.execFromPool(strJGENLIBSql);
          errMsg += "票據代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";

          //客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
          if("Y".equals(str082Rstatus)){
            //Sale05M070
            strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','票據資料','"+strActionName+"','代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
            //AS400
            strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbJGENLIB.execFromPool(strJGENLIBSql);
            errMsg += "票據代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
          }else{
            //不符合
            strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','票據資料','"+strActionName+"','不符合','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
        }else{//本人繳款
          //不適用5,8,17,19,20,21
          int[] noUseAML1 = {5,8,17,19,20,21};
          mapAMLMsg = amlTool.getAMLReTurn();
          for(int ii=0 ; ii<noUseAML1.length ; ii++) {
            String amlNo = "";
            if( noUseAML1[ii]<10 ) {
              amlNo = "00" + noUseAML1[ii];
            }else {
              amlNo = "0" + noUseAML1[ii];
            }
            String amlDesc = mapAMLMsg.get(amlNo).toString()
                                      .replaceAll("<customName>", "").replaceAll("<customTitle>", "").replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
            strSaleSql = "INSERT INTO Sale05M070 "
                       + "(DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
                       + "VALUES "
                       + "('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"'"
                       + ",'"+allCustomName+"','"+strEDate+"','RY','773','" + amlNo + "','" + amlDesc + "'"
                       + ",'"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
            dbSale.execFromPool(strSaleSql);
            intRecordNo++;
          }
        }   
      }
    }
    
    //13.客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。
    if("Y".equals(rule13)){
      //Sale05M070
      strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','客戶資料','"+strActionName+"','客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','013','客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
      dbSale.execFromPool(strSaleSql);
      intRecordNo++;
      //AS400
      strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '013', '客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
      dbJGENLIB.execFromPool(strJGENLIBSql);

      errMsg += "客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。\n";
    }else{
      //不符合
      strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','客戶資料','"+strActionName+"','不符合','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','013','客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
      dbSale.execFromPool(strSaleSql);
      intRecordNo++;
    }
    
    //14.客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。
    if("Y".equals(rule14)){
      //Sale05M070
      strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','客戶資料','"+strActionName+"','客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','014','客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
      dbSale.execFromPool(strSaleSql);
      intRecordNo++;
      //AS400
      strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '014', '客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
      dbJGENLIB.execFromPool(strJGENLIBSql);

      errMsg += "客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。\n";
    }else{
      //不符合
      strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款','客戶資料','"+strActionName+"','不符合','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','014','客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
      dbSale.execFromPool(strSaleSql);
      intRecordNo++;
    }
    
    //每筆輸入時暫存的訊息，加入
    String deputyAMLText = getValue("DeputyAML").trim();
    if(StringUtils.isNotBlank(deputyAMLText)) errMsg += deputyAMLText;
    
    if(!"".equals(errMsg)){
      setValue("errMsgBoxText",errMsg); 
      getButton("errMsgBoxBtn").doClick();
      getButton("sendMail").doClick();
    }
    
    System.out.println("value=====>"+value);
    System.out.println("===========AML============E");
    return value;
  }
  public String getInformation(){
    return "---------------AML(\u6d17\u9322\u9632\u5236).defaultValue()----------------";
  }
}
