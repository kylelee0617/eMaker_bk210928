package Sale.AML;
import javax.swing.*;
import jcx.jform.bproc;
import cLabel;
import Farglory.util.*;
import jcx.jform.bNotify;
import jcx.jform.bBase;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

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
		//洗錢追蹤流水號
		int intRecordNo =1;
		strSaleSql = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='"+strOrderNo+"'";
		ret070Table = dbSale.queryFromPool(strSaleSql);
		if(!"".equals(ret070Table[0][0].trim())){
			intRecordNo = Integer.parseInt(ret070Table[0][0].trim())+1;
		}
		//actionNo
		String ram = "";
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			ram += String.valueOf(random.nextInt(10));
		}
		String actionNo =strNowDate+ strNowTime+ram;
		
		
		//start of 樣態1~4  Kyle
		//1同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。
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
		double dBankMoney = Double.parseDouble(strBankMoney);
		double dReceiveMoney = Double.parseDouble(strReceiveMoney);
		String[] orderNoss = orderNos.split("、");
		String[] customNos = allCustomID.split("、");
		String[] percentages = percentage.split("、");
		
		KUtils kutil = new KUtils();
		String tempMsg = "";
		AMLBean aml = new AMLBean();
		aml.setProjectID1(strProjectID1);
		aml.setFuncName("收款");
		aml.setActionName(strActionName);
		aml.setCustomTitle("客戶");
		aml.setTrxDate(strEDate);
		aml.setOrderNos(kutil.genQueryInString(orderNoss));
		aml.setCustomNos(kutil.genQueryInString(customNos));
		aml.setCustomNames(allCustomName);
		AMLTools amlTool = new AMLTools(aml);
		
		//TODO: 態樣1
		tempMsg = amlTool.chkAML001(aml).getData().toString();
		errMsg += tempMsg; 
		
		//TODO: 態樣2
		//本單若有一筆現金或匯款介於45~49則檢查前兩天
		//Tips: 訂單跟客戶要分開處理
		if(dCashMoney > 0 || dBankMoney > 0) {
		  if( (dCashMoney >= 450000 && dCashMoney <= 499999) || (dBankMoney >= 450000 && dBankMoney <= 499999) ) {  //訂單
	      tempMsg = amlTool.chkAML002(aml , "order").getData().toString();
	      errMsg += tempMsg;
	    }
	    for(int g=0 ; g<customNos.length ; g++) {
	      if( (dCashMoney*Double.parseDouble(percentages[g].trim())/100 >= 450000 && dCashMoney*Double.parseDouble(percentages[g].trim())/100 <= 499999) 
	          || (dBankMoney*Double.parseDouble(percentages[g].trim())/100 >= 450000 && dBankMoney*Double.parseDouble(percentages[g].trim())/100 <= 499999) ) {  //客戶
	        aml.setCustomId( customNos[g].trim() );
	        tempMsg = amlTool.chkAML002(aml , "custom").getData().toString();
	        errMsg += tempMsg;
	      }
	    }
		}
		
		//TODO: 態樣3
		if(dCashMoney > 0) {
		  tempMsg = amlTool.chkAML003(aml).getData().toString();
	    errMsg += tempMsg;
		}
		
		//TODO: 態樣4
		if(dCashMoney > 0) {
		  tempMsg = amlTool.chkAML004(aml).getData().toString();
	    errMsg += tempMsg;
		}
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//End of 態樣1~4 Kyle
		
		
		//Pattern5,8,9,10,11,17~20
		//信用卡
		ret083Table  =  getTableData("table5");
		if(ret083Table.length > 0) {
			for(int e=0;e<ret083Table.length;e++){
				String str083Deputy = ret083Table[e][7].trim();//本人繳款
				String str083DeputyName=ret083Table[e][8].trim();//姓名
				String str083DeputyId=ret083Table[e][9].trim();//身分證號
				String str083Rlatsh=ret083Table[e][10].trim();//關係
				String str083Bstatus=ret083Table[e][12].trim();//黑名單
				String str083Cstatus=ret083Table[e][13].trim();//控管名單
				String str083Rstatus=ret083Table[e][14].trim();//利關人
		System.out.println("str083Deputy=====>"+str083Deputy);
		System.out.println("str083DeputyName=====>"+str083DeputyName);
		System.out.println("str083DeputyId=====>"+str083DeputyId);
		System.out.println("str083Rlatsh=====>"+str083Rlatsh);
		System.out.println("str083Bstatus=====>"+str083Bstatus);
		System.out.println("str083Cstatus=====>"+str083Cstatus);
		System.out.println("str083Rstatus=====>"+str083Rstatus);
				//不適用LOG_2,3,4,6,7,9,10,11,12,15,16
				//2
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//3
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//4
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//6
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//7
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//9
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','009','客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//10
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//11
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','011','交易最終受益人或交易人為主管機關公告之恐怖分子或團體；或國際認定或追查之恐怖組織；或交易資金疑似與恐怖組織有關聯者，應依資恐防制法進行相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//12
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//15
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//16
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				
				if("Y".equals(str083Deputy)){//有代繳人
					//18制裁名單
					//Query_Log 拿生日
					strPW0Dsql = "SELECT BIRTHDAY FROM QUERY_LOG WHERE PROJECT_ID = '"+strProjectID1+"' AND QUERY_ID = '"+str083DeputyId+"' AND NAME = '"+str083DeputyName+"'";
					retQueryLog = dbPW0D.queryFromPool(strPW0Dsql);
					if(retQueryLog.length > 0) {
						System.out.println("BIRTHDAY====>"+retQueryLog[0][0].trim().replace("/","-")) ;
						strBDaysql = "AND ( CUSTOMERNAME='"+str083DeputyName+"' AND BIRTHDAY = '"+retQueryLog[0][0].trim().replace("/","-")+"' )";
					}else{
						strBDaysql = "AND CUSTOMERNAME='"+str083DeputyName+"'";
					}
					System.out.println("strBDaysql====>"+strBDaysql) ;
					//AS400
					str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+str083DeputyId+"' "+strBDaysql ;
					retCList = db400CRM.queryFromPool(str400sql);
					if(retCList.length > 0) {
						//400 LOG
						stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(stringSQL);	
						//SALE LOG
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+str083DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
						/*
						if("".equals(errMsg)){
							errMsg ="信用卡代繳款人"+str083DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
						}else{
							errMsg =errMsg+"\n信用卡代繳款人"+str083DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
						}
						*/
						errMsg =errMsg+"信用卡代繳款人"+str083DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。\n";
					}else{
						//不符合
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','不符合','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+str083DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
					}
					//X171
					str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+str083DeputyId+"' "+strBDaysql ;
					retCList = db400CRM.queryFromPool(str400sql);
					if(retCList.length > 0) {
						//400 LOG
						stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '021', '該客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(stringSQL);	
						//SALE LOG
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+str083DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
						/*
						if("".equals(errMsg)){
							errMsg ="信用卡代繳款人"+str083DeputyName+"家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
						}else{
							errMsg =errMsg+"\n信用卡代繳款人"+str083DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
						}
						*/
						errMsg =errMsg+"信用卡代繳款人"+str083DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。\n";
					}else{
						//不符合
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','不符合','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+str083DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
					}
					//代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
					if("朋友".equals(str083Rlatsh) || "其他".equals(str083Rlatsh)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="信用卡代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
						}else{
							errMsg =errMsg+"\n信用卡代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
						}
						*/
						errMsg =errMsg+"信用卡代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','不符合','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str083DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
					//不動產銷售由第三方代理或繳款，系統檢核提示通報。
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					/*
					if("".equals(errMsg)){
						errMsg ="信用卡代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
					}else{
						errMsg =errMsg+"\n信用卡代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
					}
					*/
					errMsg =errMsg+"信用卡代繳款人"+str083DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";
					//客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。
					//客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。
					if("Y".equals(str083Bstatus) || "Y".equals(str083Cstatus)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName ,RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+str083DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '020', '該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="信用卡代繳款人"+str083DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
						}else{
							errMsg =errMsg+"\n信用卡代繳款人"+str083DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
						}
						*/
						errMsg =errMsg+"信用卡代繳款人"+str083DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不符合','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+str083DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
					//客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
					if("Y".equals(str083Rstatus)){
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str083DeputyId+"', '"+str083DeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="信用卡代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
						}else{
							errMsg =errMsg+"\n信用卡代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
						}
						*/
						errMsg =errMsg+"信用卡代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','信用卡資料','"+strActionName+"', '不符合','"+str083DeputyId+"','"+str083DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str083DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
				}else{
					//本人繳款(不適用5,8,17,19,20)
					//5
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//8
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//17
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType,ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','017','該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//17
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType,ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','018','該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//19
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','019','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//20
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','信用卡資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','020','該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}		
			}
		}
		//現金(只有一筆)
		System.out.println("strDeputy=====>"+strDeputy);
		System.out.println("strDeputyName=====>"+strDeputyName);
		System.out.println("strDeputyID=====>"+strDeputyID);
		System.out.println("strDeputyRelationship=====>"+strDeputyRelationship);
		System.out.println("bStatus=====>"+bStatus);
		System.out.println("cStatus=====>"+cStatus);
		System.out.println("rStatus=====>"+rStatus);
		System.out.println("strCashMoney=====>"+strCashMoney);
		//不適用LOG_6,9,10,11,12,15,16
		//6
		strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
		dbSale.execFromPool(strSaleSql);
		intRecordNo++;
		//9
		strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','009','客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
		dbSale.execFromPool(strSaleSql);
		intRecordNo++;
		//10
		strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
		dbSale.execFromPool(strSaleSql);
		intRecordNo++;
		//11
		strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','011','交易最終受益人或交易人為主管機關公告之恐怖分子或團體；或國際認定或追查之恐怖組織；或交易資金疑似與恐怖組織有關聯者，應依資恐防制法進行相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
		dbSale.execFromPool(strSaleSql);
		intRecordNo++;
		//12
		strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
		dbSale.execFromPool(strSaleSql);
		intRecordNo++;
		//15
		strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
		dbSale.execFromPool(strSaleSql);
		intRecordNo++;
		//16
		strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
		dbSale.execFromPool(strSaleSql);
		intRecordNo++;
		//5, 8, 17,18,19,20,21
		if(Double.parseDouble(strCashMoney) != 0){//有現金繳費
			if("Y".equals(strDeputy)){//有代繳人
				//18制裁名單
				//Query_Log 拿生日
				strPW0DSql = "SELECT BIRTHDAY FROM QUERY_LOG WHERE PROJECT_ID = '"+strProjectID1+"' AND QUERY_ID = '"+strDeputyID+"' AND NAME = '"+strDeputyName+"'";
				retQueryLog = dbPW0D.queryFromPool(strPW0DSql);
				if(retQueryLog.length > 0) {
					System.out.println("BIRTHDAY====>"+retQueryLog[0][0].trim().replace("/","-")) ;
					strBDaysql = "AND ( CUSTOMERNAME='"+strDeputyName+"' AND BIRTHDAY = '"+retQueryLog[0][0].trim().replace("/","-")+"' )";
				}else{
					strBDaysql = "AND CUSTOMERNAME='"+strDeputyName+"'";
				}
				System.out.println("strBDaysql====>"+strBDaysql) ;
				//AS400
				str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+strDeputyID+"' "+strBDaysql ;
				retCList = db400CRM.queryFromPool(str400sql);
				if(retCList.length > 0) {
					//400 LOG
					stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(stringSQL);	
					//SALE LOG
					stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "+
									" VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '代繳款人"+strDeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+strDeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(stringSQL);
					intRecordNo++;
					/*
					if("".equals(errMsg)){
						errMsg ="現金代繳款人"+strDeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
					}else{
						errMsg =errMsg+"\n現金代繳款人"+strDeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
					}
					*/
					errMsg =errMsg+"現金代繳款人"+strDeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。\n";
				}else{
					//不符合
					stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "+
									" VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不符合','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+strDeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(stringSQL);
					intRecordNo++;
				}
				//X171
				str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+strDeputyID+"' "+strBDaysql ;
				retCList = db400CRM.queryFromPool(str400sql);
				if(retCList.length > 0) {
					//400 LOG
					stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '021', '客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(stringSQL);	
					//SALE LOG
					stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "+
									" VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '代繳款人"+strDeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+strDeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(stringSQL);
					intRecordNo++;
					/*
					if("".equals(errMsg)){
						errMsg ="現金代繳款人"+strDeputyName+"家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
					}else{
						errMsg =errMsg+"\n現金代繳款人"+strDeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
					}
					*/
					errMsg =errMsg+"現金代繳款人"+strDeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。\n";
				}else{
					//不符合
					stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) "+
									" VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不符合','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+strDeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(stringSQL);
					intRecordNo++;
				}
				//代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
				if("朋友".equals(strDeputyRelationship) || "其他".equals(strDeputyRelationship)){
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					/*
					if("".equals(errMsg)){
						errMsg ="現金代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
					}else{
						errMsg =errMsg+"\n現金代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
					}
					*/
					errMsg =errMsg+"現金代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
				}else{
					//不符合
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不符合','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+strDeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}
				//不動產銷售由第三方代理或繳款，系統檢核提示通報。
				//Sale05M070
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"','代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//AS400
				strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbJGENLIB.execFromPool(strJGENLIBSql);
				/*
				if("".equals(errMsg)){
					errMsg ="現金代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
				}else{
					errMsg =errMsg+"\n現金代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
				}
				*/
				errMsg =errMsg+"現金代繳款人"+strDeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";
				//客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。
				//客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。
				if("Y".equals(bStatus) || "Y".equals(cStatus)){
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"', '代繳款人"+strDeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+strDeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '020', '該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					/*
					if("".equals(errMsg)){
						errMsg ="現金代繳款人"+strDeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
					}else{
						errMsg =errMsg+"\n現金代繳款人"+strDeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
					}
					*/
					errMsg =errMsg+"現金代繳款人"+strDeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
				}else{
					//不符合
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"', '不符合','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+strDeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}
				//客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
				if("Y".equals(rStatus)){
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"', '代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					/*
					if("".equals(errMsg)){
						errMsg ="現金代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
					}else{
						errMsg =errMsg+"\n現金代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
					}
					*/
					errMsg =errMsg+"現金代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
				}else{
					//不符合
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"', '不符合','"+strDeputyID+"','"+strDeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+strDeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}
			}else{//現金本人繳款
				//不適用5,8,17,18,19,20
				//5
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo, Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//8
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//17
				strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','017','該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//18
				strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','018','客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//19
				strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','019','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//20
				strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','現金資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','020','該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
			}
		}
		//銀行
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
		System.out.println("str328Deputy=====>"+str328Deputy);
		System.out.println("str328DeputyName=====>"+str328DeputyName);
		System.out.println("str328DeputyId=====>"+str328DeputyId);
		System.out.println("str328ExPlace=====>"+str328ExPlace);
		System.out.println("str328Rlatsh=====>"+str328Rlatsh);
		System.out.println("str328bStatus=====>"+str328bStatus);
		System.out.println("str328cStatus=====>"+str328cStatus);
		System.out.println("str328rStatus=====>"+str328rStatus);
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//9
				//不適用3,4,6,7,9,11,12,15,16
				//3
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//4
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//6
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//7
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','009','客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//11
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','011','交易最終受益人或交易人為主管機關公告之恐怖分子或團體；或國際認定或追查之恐怖組織；或交易資金疑似與恐怖組織有關聯者，應依資恐防制法進行相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//12
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//15
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//16
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//自金融監督管理委員會函轉國際防制洗錢組織所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循國際防制洗錢組織建議之國家或地區匯入之交易款項。
				strJGENLIBSql =  "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09 = '" + str328ExPlace + "'";
				retPDCZPFTable = dbJGENLIB.queryFromPool(strJGENLIBSql);
				if(retPDCZPFTable.length > 0){
					String strCZ07 =retPDCZPFTable[0][0].trim();
					if("優先法高".equals(strCZ07)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '代繳款人"+str328DeputyName+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','010','代繳款人"+str328DeputyName+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
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
						/*			
						if("".equals(errMsg)){
							errMsg =strTempMsg+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。";
						}else{
							errMsg =errMsg+"\n"+strTempMsg+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。";
						}
						*/
						errMsg =errMsg+strTempMsg+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不符合','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','010','代繳款人"+str328DeputyName+"係來自洗錢及資恐防制有嚴重缺失、未遵循或未充分遵循之國家或地區匯入之款項，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
				}
				if("Y".equals(str328Deputy)){//有代繳人
					//制裁名單
					System.out.println("銀行制裁名單====>") ;
					//Query_Log 拿生日
					strPW0Dsql = "SELECT BIRTHDAY FROM QUERY_LOG WHERE PROJECT_ID = '"+strProjectID1+"' AND QUERY_ID = '"+str328DeputyId+"' AND NAME = '"+str328DeputyName+"'";
					retQueryLog = dbPW0D.queryFromPool(strPW0Dsql);
					if(retQueryLog.length > 0) {
						System.out.println("BIRTHDAY====>"+retQueryLog[0][0].trim().replace("/","-")) ;
						strBDaysql = "AND ( CUSTOMERNAME='"+str328DeputyName+"' AND BIRTHDAY = '"+retQueryLog[0][0].trim().replace("/","-")+"' )";
					}else{
						strBDaysql = "AND CUSTOMERNAME='"+str328DeputyName+"'";
					}
					System.out.println("strBDaysql====>"+strBDaysql) ;
					//AS400
					str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+str328DeputyId+"' "+strBDaysql ;
					retCList = db400CRM.queryFromPool(str400sql);
					if(retCList.length > 0) {
						//400 LOG
						stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(stringSQL);	
						//SALE LOG
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName,RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','代繳款人"+str328DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+str328DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
						/*
						if("".equals(errMsg)){
							errMsg ="銀行代繳款人"+str328DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
						}else{
							errMsg =errMsg+"\n銀行代繳款人"+str328DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
						}
						*/
						errMsg =errMsg+"銀行代繳款人"+str328DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。\n";
					}else{
						//不符合
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName,RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不符合','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+str328DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
					}
					//X171
					str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+str328DeputyId+"' "+strBDaysql ;
					retCList = db400CRM.queryFromPool(str400sql);
					if(retCList.length > 0) {
						//400 LOG
						stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '021', '客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(stringSQL);	
						//SALE LOG
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName,RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','代繳款人"+str328DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+str328DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
						/*
						if("".equals(errMsg)){
							errMsg ="銀行代繳款人"+str328DeputyName+"家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
						}else{
							errMsg =errMsg+"\n銀行代繳款人"+str328DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
						}
						*/
						errMsg =errMsg+"銀行代繳款人"+str328DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。\n";
					}else{
						//不符合
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName,RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不符合','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+str328DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
					}
					//代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
					if("朋友".equals(str328Rlatsh) || "其他".equals(str328Rlatsh)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName, RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="銀行代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
						}else{
							errMsg =errMsg+"\n銀行代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
						}
						*/
						errMsg =errMsg+"銀行代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName, RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不符合','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str328DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
					//不動產銷售由第三方代理或繳款，系統檢核提示通報。
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					/*
					if("".equals(errMsg)){
						errMsg ="銀行代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
					}else{
						errMsg =errMsg+"\n銀行代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
					}
					*/
					errMsg =errMsg+"銀行代繳款人"+str328DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";
					//客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。
					//客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。
					if("Y".equals(str328bStatus) || "Y".equals(str328cStatus)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType,ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','代繳款人"+str328DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+str328DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '020', '該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="銀行代繳款人"+str328DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
						}else{
							errMsg =errMsg+"\n銀行代繳款人"+str328DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
						}
						*/
						errMsg =errMsg+"銀行代繳款人"+str328DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType,ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不符合','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+str328DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
					//客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
					if("Y".equals(str328rStatus)){
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"', '代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str328DeputyId+"', '"+str328DeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="銀行代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
						}else{
							errMsg =errMsg+"\n銀行代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
						}
						*/
						errMsg =errMsg+"銀行代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"', '不符合','"+str328DeputyId+"','"+str328DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str328DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
				}else{
					//本人繳款(不適用)10,5,8,17,19,20
					//5
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName, RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//8
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//10
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//17
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','017','該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//18
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','018','該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//19
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"', '不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','019','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//20
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType,ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','020','該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//21
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType,ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','銀行資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','021','客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
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
		System.out.println("str082Deputy=====>"+str082Deputy);
		System.out.println("str082DeputyName=====>"+str082DeputyName);
		System.out.println("str082DeputyId=====>"+str082DeputyId);
		System.out.println("str082Rlatsh=====>"+str082Rlatsh);
		System.out.println("str082Bstatus=====>"+str082Bstatus);
		System.out.println("str082Cstatus=====>"+str082Cstatus);
		System.out.println("str082Rstatus=====>"+str082Rstatus);
				//不適用2,3,4,6,7,9,10,11,12,15,16
				//2
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','002','同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//3
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','003','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//4
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','004','同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//6
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','006','同一客戶不動產買賣，簽約前退訂取消購買，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//7
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','007','同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//9
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','009','客戶係來自主管機關所公告防制洗錢與打擊資恐有嚴重缺失之國家或地區，及其他未遵循或未充分遵循之國家或地區，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//10
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','010','自主管機關所公告防制洗錢與打擊資助恐怖份子有嚴重缺失之國家或地區、及其他未遵循或未充分遵循之國家或地區匯入之交易款項，應檢核其合理性。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//11
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','011','交易最終受益人或交易人為主管機關公告之恐怖分子或團體；或國際認定或追查之恐怖組織；或交易資金疑似與恐怖組織有關聯者，應依資恐防制法進行相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//12
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','012','客戶要求將不動產權利登記予第三人，未能提出任何關聯或拒絕說明之異常狀況。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//15
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','015','要求公司開立取消禁止背書轉讓支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//16
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','016','要求公司開立撤銷平行線(取消劃線)支票作為給付方式，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
			
				if("Y".equals(str082Deputy)){//有代繳人
					System.out.println("票據制裁名單====>") ;
					//制裁名單
					//Query_Log 拿生日
					strPW0Dsql = "SELECT BIRTHDAY FROM QUERY_LOG WHERE PROJECT_ID = '"+strProjectID1+"' AND QUERY_ID = '"+str082DeputyId+"' AND NAME = '"+str082DeputyName+"'";
					retQueryLog = dbPW0D.queryFromPool(strPW0Dsql);
					if(retQueryLog.length > 0) {
						System.out.println("BIRTHDAY====>"+retQueryLog[0][0].trim().replace("/","-")) ;
						strBDaysql = "AND ( CUSTOMERNAME='"+str082DeputyName+"' AND BIRTHDAY = '"+retQueryLog[0][0].trim().replace("/","-")+"' )";
					}else{
						strBDaysql = "AND CUSTOMERNAME='"+str082DeputyName+"'";
					}
					System.out.println("strBDaysql====>"+strBDaysql) ;
					//AS400
					str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+str082DeputyId+"' "+strBDaysql ;
					retCList = db400CRM.queryFromPool(str400sql);
					if(retCList.length > 0) {
						//400 LOG
						stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '018', '該客戶為控管名單對象之制裁名單，禁止交易並請依防制洗錢內通報作業會辦法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(stringSQL);	
						//SALE LOG
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"', '代繳款人"+str082DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+str082DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
						/*
						if("".equals(errMsg)){
							errMsg ="票據代繳款人"+str082DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
						}else{
							errMsg =errMsg+"\n票據代繳款人"+str082DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
						}
						*/
						errMsg =errMsg+"票據代繳款人"+str082DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。\n";
					}else{
						//不符合
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"', '不符合','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','018','代繳款人"+str082DeputyName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
					}
					//X171
					str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+str082DeputyId+"' "+strBDaysql ;
					retCList = db400CRM.queryFromPool(str400sql);
					if(retCList.length > 0) {
						//400 LOG
						stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '021', '客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(stringSQL);	
						//SALE LOG
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"', '代繳款人"+str082DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+str082DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
						/*
						if("".equals(errMsg)){
							errMsg ="票據代繳款人"+str082DeputyName+"家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
						}else{
							errMsg =errMsg+"\n票據代繳款人"+str082DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。";
						}
						*/
						errMsg =errMsg+"票據代繳款人"+str082DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。\n";
					}else{
						//不符合
						stringSQL = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"', '不符合','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','021','代繳款人"+str082DeputyName+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(stringSQL);
						intRecordNo++;
					}
					//代繳款人與購買人關係為非二等親內血/姻親。請依洗錢防制作業辦理
					if("朋友".equals(str082Rlatsh) || "其他".equals(str082Rlatsh)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '005', '代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="票據代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
						}else{
							errMsg =errMsg+"\n票據代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。";
						}
						*/
						errMsg =errMsg+"票據代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不符合','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','005','代繳款人"+str082DeputyName+"與客戶"+allOrderName+"非二親等內親屬關係，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
					//不動產銷售由第三方代理或繳款，系統檢核提示通報。
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','008','代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '008', '不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					/*
					if("".equals(errMsg)){
						errMsg ="票據代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
					}else{
						errMsg =errMsg+"\n票據代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。";
					}
					*/
					errMsg =errMsg+"票據代繳款人"+str082DeputyName+"代為辦理不動產交易，請依洗錢及資恐防制作業辦理。\n";
					//客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。
					//客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。
					if("Y".equals(str082Bstatus) || "Y".equals(str082Cstatus)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','代繳款人"+str082DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+str082DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '020', '該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="票據代繳款人"+str082DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
						}else{
							errMsg =errMsg+"\n票據代繳款人"+str082DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。";
						}
						*/
						errMsg =errMsg+"票據代繳款人"+str082DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不符合','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','020','代繳款人"+str082DeputyName+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
					//客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。
					if("Y".equals(str082Rstatus)){
						//Sale05M070
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
						//AS400
						strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+str082DeputyId+"', '"+str082DeputyName+"', '773', '019', '該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbJGENLIB.execFromPool(strJGENLIBSql);
						/*
						if("".equals(errMsg)){
							errMsg ="票據代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
						}else{
							errMsg =errMsg+"\n票據代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。";
						}
						*/
						errMsg =errMsg+"票據代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
					}else{
						//不符合
						strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不符合','"+str082DeputyId+"','"+str082DeputyName+"','"+strEDate+"','RY','773','019','代繳款人"+str082DeputyName+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
						dbSale.execFromPool(strSaleSql);
						intRecordNo++;
					}
				}else{//本人繳款
					//不適用5,8,17,19,20
					//5
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','005','代繳款人與購買人關係為非二等親內血/姻親，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//8
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','008','不動產銷售由第三方代理或繳款，系統檢核提示通報。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//17
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo, Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"', '收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','017','該客戶為控管名單對象，請執行加強式客戶盡職審查並依防制洗錢內部通報作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//19
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo, ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','019','該客戶為公司利害關系人，需依保險業與利害關係人從事放款以外之其他交易管理辦法執行。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//20
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','020','該客戶為疑似黑名單對象，請覆核確認後，再進行後續交易。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//21
					strSaleSql = "INSERT INTO Sale05M070 (DocNo, OrderNo, ProjectID1, RecordNo,ActionNo,  Func, RecordType, ActionName, RecordDesc, CustomID, CustomName, EDate, SHB00, SHB06A, SHB06B, SHB06,SHB97,SHB98,SHB99)  VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','票據資料','"+strActionName+"','不適用','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','021','客戶或其受益人、家庭成員及有密切關係之人，為現任、曾任國內外政府或國際組織重要政治性職務，請加強客戶盡職調查，請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}		
			}
		}
		//13.客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。
		if("Y".equals(rule13)){
			//Sale05M070
			strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','013','客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
			dbSale.execFromPool(strSaleSql);
			intRecordNo++;
			//AS400
			strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '013', '客戶支付不動產交易之款項，以現鈔支付訂金以外各期價款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
			dbJGENLIB.execFromPool(strJGENLIBSql);
			/*
			if("".equals(errMsg)){
				errMsg ="客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。";
			}else{
				errMsg =errMsg+"\n客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。";
			}
			*/
			errMsg =errMsg+"客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。\n";
		}else{
			//不符合
			strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','013','客戶"+allCustomName+"以現鈔支付訂金以外各期不動產交易價款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
			dbSale.execFromPool(strSaleSql);
			intRecordNo++;
		}
		//14.客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。
		if("Y".equals(rule14)){
			//Sale05M070
			strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','014','客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
			dbSale.execFromPool(strSaleSql);
			intRecordNo++;
			//AS400
			strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strDeputyID+"', '"+strDeputyName+"', '773', '014', '客戶於簽約前提前付清自備款，且無合理說明資金來源，應檢核是否符合疑似洗錢交易表徵。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
			dbJGENLIB.execFromPool(strJGENLIBSql);
			/*
			if("".equals(errMsg)){
				errMsg ="客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。";
			}else{
				errMsg =errMsg+"\n客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。";
			}
			*/
			errMsg =errMsg+"客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。\n";
		}else{
			//不符合
			strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allCustomID+"','"+allCustomName+"','"+strEDate+"','RY','773','014','客戶"+allCustomName+"簽約前(含當日)提前付清自備款，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
			dbSale.execFromPool(strSaleSql);
			intRecordNo++;
		}
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
