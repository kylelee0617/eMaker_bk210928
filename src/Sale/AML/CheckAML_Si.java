package Sale.AML;
import javax.swing.*;
import jcx.jform.bproc;
import cLabel;
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

public class CheckAML_Si extends bproc{
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
		boolean Pattern1Show = false;
		boolean Pattern2Show = false;
		boolean Pattern3Show = false;
		boolean Pattern4Show = false;
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
		String[][] orderCustomTable =  getTableData("table3");
		for (int g = 0; g < orderCustomTable.length; g++) {
			if("".equals(allOrderName)){
				allOrderID =  orderCustomTable[g][3].trim();
				allOrderName =  orderCustomTable[g][4].trim();
			}else{
				allOrderID = allOrderID+"、"+ orderCustomTable[g][3].trim();
				allOrderName = allOrderName+"、"+ orderCustomTable[g][4].trim();
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
		String[][] orderNoTable =  getTableData("table4");
		strOrderNo=orderNoTable[0][2].trim();
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
		//Pattern 樣態
		//1同一客戶同一營業日內2筆(含)以上包含現金、匯款、信用卡、支票交易，且每筆皆介於新台幣450,000~499,999元，系統檢核預警。
		//3同一客戶同一營業日現金繳納累計達50萬元(含)以上，須檢核是否符合疑似洗錢交易表徵。
		//2同一客戶3個營業日內，有2日以現金或匯款達450,000~499,999元, 系統檢核提示通報。
		//4同一客戶3個營業日內，累計繳交現金超過50萬元, 系統檢核提示通報。
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Set amlMsg = new TreeSet();
		//此張收款單的所有客戶
		String[][] orderTable3 =  getTableData("table3");
		for (int g = 0; g < orderTable3.length; g++) {//此單的所有人
			String strOrderID= orderTable3[g][3].trim();//身分證
			String strOrderName= orderTable3[g][4].trim();//姓名
			System.out.println("ID=====>"+strOrderID) ;
			System.out.println("姓名=====>"+strOrderName) ;
			//共同
			//數字格式
			DecimalFormat decimalFormat = new DecimalFormat("##.00");//四拾五入小數後兩位
			//此單的收款日(不一定是今日的日期)
			String sameDay = strDocNo.substring(strDocNo.length()-10, strDocNo.length()-3);
			//計算其他日
			SimpleDateFormat EDateSDF = new SimpleDateFormat("yyyy/MM/dd");
			Calendar calar1 = Calendar.getInstance();
			Calendar calar2 = Calendar.getInstance();
			Date eDate = EDateSDF.parse(strEDate);
			calar1.setTime(eDate);
			calar2.setTime(eDate);
			calar1.add(Calendar.DAY_OF_MONTH, -1); 
			calar2.add(Calendar.DAY_OF_MONTH, -2); 
			String strcalar1 =  new SimpleDateFormat("yyyyMMdd").format(calar1.getTime());
			String strcalar2 =  new SimpleDateFormat("yyyyMMdd").format(calar2.getTime());
			//ROC FORMAT
			strcalar1 = Integer.toString( (Integer.parseInt(strcalar1.substring(0,4)) -1911)) + strcalar1.substring(4,8) ;
			strcalar2 =  Integer.toString( (Integer.parseInt(strcalar2.substring(0,4)) -1911))+ strcalar2.substring(4,8) ;
		    
			//此人今日所有單
			//以單計算
			int p1AllCount = 0;//介於新台幣450,000~499,999元次數
			double p3AllAddup = 0;//金額加總
			double m328AllAddup = 0;//匯款加總
			boolean p2AllToday = false; //三日內之今日是否介於45~50之間
			
			
			//以人計算
			int p1Count = 0;//介於新台幣450,000~499,999元次數(個人)
			double p3Addup = 0;//金額加總(個人)
		    double m328Addup = 0;//匯款加總(個人)
			boolean p2Today = false;// //三日內之今日是否介於45~50之間(個人)
			
			
			
			String[][] retSameDay =null;
			String sqlSameDay = "SELECT DocNo,Percentage FROM Sale05M084 WHERE CUSTOMNO = '"+strOrderID+"' AND DOCNO LIKE '%"+sameDay+"%'";
			retSameDay = dbSale.queryFromPool(sqlSameDay);
			System.out.println("今天有"+retSameDay.length+"張單") ;
			for (int m = 0; m < retSameDay.length; m++) {//今天的每張收款單
				String strSSDocNo = retSameDay[m][0].trim();     //  收款單號
				String strSSPercent =  retSameDay[m][1].trim();    // 百分比
				System.out.println("單號=====>"+strSSDocNo) ;
				System.out.println("百分比=====>"+strSSPercent) ;
				//多張購屋證明單要照人數算
				String[][] retCountOrder =null;
				String sqlCountOrder  = "SELECT * FROM Sale05M086 WHERE DOCNO = '"+strSSDocNo+"'";
				retCountOrder = dbSale.queryFromPool(sqlCountOrder);
				int OrderBase = retCountOrder.length ;
				
				//信用卡 083
				String[][] retCCM = null;
				String sqlCCM= "SELECT CreditCardMoney FROM Sale05M083 where DocNo = '"+strSSDocNo+"'";
				retCCM = dbSale.queryFromPool(sqlCCM);
				for (int n = 0; n < retCCM.length; n++) {
					double origCCM = Double.parseDouble(retCCM[n][0].trim());     //信用卡金額
					origCCM = Double.parseDouble(decimalFormat.format( origCCM/OrderBase)); //照人數           
					double dubCCMP  =  origCCM*(Double.parseDouble(strSSPercent)/100)  ;   //照百分比
					if(origCCM>449999 && origCCM<500000){
						p1AllCount++;
					}
					if(dubCCMP>449999 && dubCCMP<500000){
						p1Count++;
					}
				}
				//現金 080
				String[][] ret80CM = null;
				String sql80CM= "SELECT CashMoney FROM Sale05M080 where DocNo = '"+strSSDocNo+"'";
				ret80CM = dbSale.queryFromPool(sql80CM);
				for (int o = 0; o < ret80CM.length; o++) {
					double origCM = Double.parseDouble(ret80CM[o][0].trim()); //現金金額
					origCM = Double.parseDouble(decimalFormat.format( origCM/OrderBase));//照人數
					double dub80CMP  = origCM*(Double.parseDouble(strSSPercent)/100);//照百分比
					if(origCM>449999 && origCM<500000){
						p1AllCount++;
					}
					if(dub80CMP>449999 && dub80CMP<500000){
						p1Count++;
					}
					//現金累加
					p3AllAddup+=origCM;
					p3Addup+=dub80CMP;
				}
				//銀行 328
				String[][] ret328BM = null;
				String sql328BM= "SELECT BankMoney FROM Sale05M328 where DocNo = '"+strSSDocNo+"'";
				ret328BM = dbSale.queryFromPool(sql328BM);
				for (int p = 0; p < ret328BM.length; p++) {
					double origBM = Double.parseDouble(ret328BM[p][0].trim());
					origBM = Double.parseDouble(decimalFormat.format( origBM/OrderBase));
					double dub328BMP  = origBM*(Double.parseDouble(strSSPercent)/100)  ;
					if(origBM>449999 && origBM<500000){
						p1AllCount++;
					}
					if(dub328BMP>449999 && dub328BMP<500000){
						p1Count++;
					}  
					//匯款累加
					m328AllAddup+=origBM;
					m328Addup+=dub328BMP;
				}
				//票據 082
				String[][] ret082CM = null;
				String sql82CM= "SELECT CheckMoney FROM Sale05M082 where DocNo = '"+strSSDocNo+"'";
				ret082CM = dbSale.queryFromPool(sql82CM);
				for (int q = 0; q < ret082CM.length; q++) {
					double origCM = Double.parseDouble(ret082CM[q][0].trim());
					origCM = Double.parseDouble(decimalFormat.format( origCM/OrderBase));
					double dub82CMP  =  origCM*(Double.parseDouble(strSSPercent)/100)  ;
					if(origCM>449999 && origCM<500000){
						p1AllCount++;
					}
					if(dub82CMP>449999 && dub82CMP<500000){
						p1Count++;
					} 
				}
			} //今天的每張收款單 
			//TEST MSG
			System.out.println("此人今日累計") ;
			System.out.println("介於45至50萬之間=====>"+p1AllCount) ;
			System.out.println("介於45至50萬之間(個人)=====>"+p1Count) ;
			System.out.println("現金加總=====>"+p3AllAddup) ;
			System.out.println("現金加總(個人)=====>"+p3Addup) ;
			System.out.println("匯款加總=====>"+m328AllAddup) ;
			System.out.println("匯款加總(個人)=====>"+m328Addup) ;
			
			
			//今日是否介於45萬至50萬之間
			//現金
			if(p3AllAddup>449999 && p3AllAddup<500000){
				p2AllToday =true;
			}
			if(p3Addup>449999 && p3Addup<500000){
				p2Today =true;
			}
			//匯款
			if(m328AllAddup>449999 && m328AllAddup<500000){
				p2AllToday =true;
			}
			if(m328Addup>449999 && m328Addup<500000){
				p2Today =true;
			}
			System.out.println("今日收款單現金及匯款累計達45~50之間===>>"+p2AllToday);
			System.out.println("今日收款單現金及匯款累計達45~50之間(個人)===>>"+p2Today);
			//前一日
			System.out.println("各客戶前一日======="+strcalar1+"=================");
			//此人前一日所有單
			//以單計算
			double previousDayAllAddup = 0;//前一日現金加總
			double previousDaym328AllAddup = 0;//前一日匯款加總
			boolean p2AllPreviousDay = false; //三日內之前一日是否介於45~50之間
			//以人計算
			double previousDayAddup = 0;//前一日現金加總(個人)
		    double previousDaym328Addup = 0;//前一日匯款加總(個人)
			boolean p2PreviousDay = false;// //三日內之今日前一日介於45~50之間(個人)
			
			String[][] retADayBefore84Tab = null;
			String sqlADayBefore = "SELECT  DocNo,Percentage FROM Sale05M084 WHERE CUSTOMNO = '"+strOrderID+"'  AND DOCNO LIKE '%"+strcalar1+"%'";
			retADayBefore84Tab = dbSale.queryFromPool(sqlADayBefore);
			System.out.println("前一日有"+retADayBefore84Tab.length+"張單") ;
			for (int r = 0; r < retADayBefore84Tab.length; r++) { //前一日的每張收款單 
				String strADayBeforeDocNo = retADayBefore84Tab[r][0].trim();
				String strADayBeforePercent =  retADayBefore84Tab[r][1].trim();
				System.out.println("前一日單號=====>"+strADayBeforeDocNo) ;
				System.out.println("前一日百分比=====>"+strADayBeforePercent) ;
				//多張購屋證明單要照人數算
				String[][] retCountOrder =null;
				String sqlCountOrder  = "SELECT * FROM Sale05M086 WHERE DOCNO = '"+strADayBeforeDocNo+"'";
				retCountOrder = dbSale.queryFromPool(sqlCountOrder);
				int OrderBase =retCountOrder.length ;
				//現金
				String [][] retADB80Tab = null;
				String sqlADB80 = "SELECT CashMoney FROM Sale05M080 WHERE DocNo = '"+strADayBeforeDocNo+"'";
				retADB80Tab  =  dbSale.queryFromPool(sqlADB80);
				for (int ra = 0; ra < retADB80Tab.length; ra++) {
					double tempCashMoney = Double.parseDouble(retADB80Tab[ra][0].trim());
					tempCashMoney = Double.parseDouble(decimalFormat.format( tempCashMoney/OrderBase));
					double dubADBCMP  =  tempCashMoney*(Double.parseDouble(strADayBeforePercent)/100)  ;
					previousDayAllAddup+=tempCashMoney;
					previousDayAddup+=dubADBCMP;
				}
				//銀行(會有多筆)
				String [][] retADB328Tab = null;
				String sqlADB328 = "SELECT SUM(BankMoney) FROM Sale05M328 WHERE DocNo = '"+strADayBeforeDocNo+"'";
				retADB328Tab  =  dbSale.queryFromPool(sqlADB328);
				for (int rb = 0; rb < retADB328Tab.length; rb++) {
					String bankMoney = retADB328Tab[rb][0].trim(); 
					if("".equals(bankMoney)){
						bankMoney = "0";
					}
					double tempBankMoney = Double.parseDouble(bankMoney);
					tempBankMoney = Double.parseDouble(decimalFormat.format( tempBankMoney/OrderBase));
					double dubADBBMP  =  tempBankMoney*(Double.parseDouble(strADayBeforePercent)/100)  ;
					previousDaym328AllAddup+=tempBankMoney;
					previousDaym328Addup+=dubADBBMP;
				}
			}//前一日的每張收款單 
			System.out.println("此人前一日累計") ;
			System.out.println("現金加總=====>"+previousDayAllAddup) ;
			System.out.println("現金加總(個人)=====>"+previousDayAddup) ;
			System.out.println("匯款加總=====>"+previousDaym328AllAddup) ;
			System.out.println("匯款加總(個人)=====>"+previousDaym328Addup) ;
			//前一日是否介於45萬至50萬之間
			//現金
			if(previousDayAllAddup>449999 && previousDayAllAddup<500000){
				p2AllPreviousDay =true;
			}
			if(previousDayAddup>449999 && previousDayAddup<500000){
				p2PreviousDay =true;
			}
			//匯款
			if(previousDaym328AllAddup>449999 && previousDaym328AllAddup<500000){
				p2AllPreviousDay =true;
			}
			if(previousDaym328Addup>449999 && previousDaym328Addup<500000){
				p2PreviousDay =true;
			}
			System.out.println("前一日收款單現金及匯款累計達45~50之間===>>"+p2AllPreviousDay);
			System.out.println("前一日收款單現金及匯款累計達45~50之間(個人)===>>"+p2PreviousDay);
			//前二日
			System.out.println("各客戶前二日======="+strcalar2+"=================");
			//此人前二日所有單
			//以單計算twoDaysAgo
			double twoDaysAgoAllAddup = 0;//前二日現金加總
			double twoDaysAgom328AllAddup = 0;//前二日匯款加總
			boolean p2AlltwoDaysAgo = false; //三日內之前二日是否介於45~50之間
			//以人計算
			double twoDaysAgoAddup = 0;//前二日現金加總(個人)
		    double twoDaysAgom328Addup = 0;//前二日匯款加總(個人)
			boolean p2twoDaysAgo = false;// //三日內之今日前二日介於45~50之間(個人)
			
			String[][] retTwoDayBefore84Tab = null;
			String sqlTwoDayBefore = "SELECT  DocNo,Percentage FROM Sale05M084 WHERE CUSTOMNO = '"+strOrderID+"'  AND DOCNO LIKE '%"+strcalar2+"%'";
			retTwoDayBefore84Tab = dbSale.queryFromPool(sqlTwoDayBefore);
			System.out.println("前二日有"+retTwoDayBefore84Tab.length+"張單") ;
			for (int s = 0; s < retTwoDayBefore84Tab.length; s++) {//前二日的每張收款單 
				String strTwoDayBeforeDocNo = retTwoDayBefore84Tab[s][0].trim();
				String strTwoDayBeforePercent =  retTwoDayBefore84Tab[s][1].trim();
				System.out.println("前二日單號=====>"+strTwoDayBeforeDocNo) ;
				System.out.println("前二日百分比=====>"+strTwoDayBeforePercent) ;
				//多張購屋證明單要照人數算
				String[][] retCountOrder =null;
				String sqlCountOrder  = "SELECT * FROM Sale05M086 WHERE DOCNO = '"+strTwoDayBeforeDocNo+"'";
				retCountOrder = dbSale.queryFromPool(sqlCountOrder);
				int OrderBase =retCountOrder.length ;
				//System.out.println("幾張前二日購屋證明單====>"+OrderBase) ;
				//現金
				String [][] retTDB80Tab = null;
				String sqlTDB80 = "SELECT CashMoney FROM Sale05M080 WHERE DocNo = '"+strTwoDayBeforeDocNo+"'";
				retTDB80Tab  =  dbSale.queryFromPool(sqlTDB80);
				for (int sa = 0; sa < retTDB80Tab.length; sa++) {
					String cashMoney = retTDB80Tab[sa][0].trim();
					double tempCashMoney = Double.parseDouble(cashMoney);
					tempCashMoney = Double.parseDouble(decimalFormat.format( tempCashMoney/OrderBase));
					double dubTDBCMP  =  tempCashMoney*(Double.parseDouble(strTwoDayBeforePercent)/100)  ;
					twoDaysAgoAllAddup +=tempCashMoney;
					twoDaysAgoAddup +=dubTDBCMP;
				}
				//銀行
				String [][] retTDB328Tab = null;
				String sqlTDB328 = "SELECT SUM(BankMoney) FROM Sale05M328 WHERE DocNo = '"+strTwoDayBeforeDocNo+"'";
				retTDB328Tab  =  dbSale.queryFromPool(sqlTDB328);
				for (int sb = 0; sb < retTDB328Tab.length; sb++) {
					String bankMoney = retTDB328Tab[sb][0].trim(); 
					if("".equals(bankMoney)){
						bankMoney = "0";
					}
					double tempBankMoney = Double.parseDouble(bankMoney);
					tempBankMoney = Double.parseDouble(decimalFormat.format( tempBankMoney/OrderBase));
					double dubTDBBMP  =  tempBankMoney*(Double.parseDouble(strTwoDayBeforePercent)/100)  ;
					twoDaysAgom328AllAddup+=tempBankMoney;
					twoDaysAgom328Addup+=dubTDBBMP;
				}
			}//前二日的每張收款單 
			System.out.println("此人前二日累計") ;
			System.out.println("現金加總=====>"+twoDaysAgoAllAddup) ;
			System.out.println("現金加總(個人)=====>"+twoDaysAgoAddup) ;
			System.out.println("匯款加總=====>"+twoDaysAgom328AllAddup) ;
			System.out.println("匯款加總(個人)=====>"+twoDaysAgom328Addup) ;
			//前二日是否介於45萬至50萬之間
			//現金
			if(twoDaysAgoAllAddup>449999 && twoDaysAgoAllAddup<500000){
				p2AlltwoDaysAgo =true;
			}
			if(twoDaysAgoAddup>449999 && twoDaysAgoAddup<500000){
				p2twoDaysAgo =true;
			}
			//匯款
			if(twoDaysAgom328AllAddup>449999 && twoDaysAgom328AllAddup<500000){
				p2AlltwoDaysAgo =true;
			}
			if(twoDaysAgom328Addup>449999 && twoDaysAgom328Addup<500000){
				p2twoDaysAgo =true;
			}
			System.out.println("前二日收款單現金及匯款累計達45~50之間===>>"+p2AlltwoDaysAgo);
			System.out.println("前二日收款單現金及匯款累計達45~50之間(個人)===>>"+p2twoDaysAgo);
			//商務邏輯
			//若AML003態樣符合且預警後，清除AML004之累計天數
			int p2AllCount = 0;
			double p4AllAddup = 0;
			int p2Count = 0;
			double p4Addup = 0;
			
			//前二日
			if(twoDaysAgoAllAddup>=500000){
				p2AllCount = 0;
				p4AllAddup = 0;
				System.out.println("此人前二日現金累計超過五十萬，計數歸零："+twoDaysAgoAllAddup) ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數："+p2AllCount) ;
				System.out.println("三日內現金累計："+p4AllAddup) ;
			}else{
				if(p2AlltwoDaysAgo){
					p2AllCount++;
				}
				p4AllAddup+=twoDaysAgoAllAddup;
				System.out.println("此人前二日現金累計未超過五十萬") ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數："+p2AllCount) ;
				System.out.println("三日內現金累計："+p4AllAddup) ;
			}
			if(twoDaysAgoAddup>=500000){
				p2Count = 0;
				p4Addup = 0;
				System.out.println("此人前二日現金累計超過五十萬，計數歸零(個人)："+twoDaysAgoAddup) ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數(個人)："+p2Count) ;
				System.out.println("三日內現金累計(個人)："+p4Addup) ;
			}else{
				if(p2twoDaysAgo){
					p2Count++;
				}
				p4Addup+=twoDaysAgoAddup;
				System.out.println("此人前二日現金累計未超過五十萬(個人)") ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數(個人)："+p2Count) ;
				System.out.println("三日內現金累計(個人)："+p4Addup) ;
			}
			//前一日
			if(previousDayAllAddup>=500000){
				p2AllCount = 0;
				p4AllAddup = 0;
				System.out.println("此人前一日現金累計超過五十萬，計數歸零:"+previousDayAllAddup) ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數："+p2AllCount) ;
				System.out.println("三日內現金累計："+p4AllAddup) ;
			}else{
				if(p2AllPreviousDay){
					p2AllCount++;
				}
				p4AllAddup+=previousDayAllAddup;
				System.out.println("此人前一日現金累計未超過五十萬") ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數："+p2AllCount) ;
				System.out.println("三日內現金累計："+p4AllAddup) ;
			}
			//三日累計超過五十也要歸零
			if(p4AllAddup>=500000){
				p2AllCount = 0;
				p4AllAddup = 0;
			}
			
			if(previousDayAddup>=500000){
				p2Count = 0;
				p4Addup = 0;
				System.out.println("此人前一日現金累計超過五十萬，計數歸零(個人):"+previousDayAddup) ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數(個人)："+p2Count) ;
				System.out.println("三日內現金累計(個人)："+p4Addup) ;
			}else{
				if(p2PreviousDay){
					p2Count++;
				}
				p4Addup+=previousDayAddup;
				System.out.println("此人前一日現金累計未超過五十萬(個人)") ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數(個人)："+p2Count) ;
				System.out.println("三日內現金累計(個人)："+p4Addup) ;
			}
			//三日累計超過五十也要歸零(個人)
			if(p4Addup>=500000){
				p2Count = 0;
				p4Addup = 0;
			}
			
			//今日
			if(p3AllAddup>=500000){
				p2AllCount = 0;
				p4AllAddup = 0;	
				System.out.println("此人今日現金累計超過五十萬，計數歸零:"+p3AllAddup) ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數："+p2AllCount) ;
				System.out.println("三日內現金累計："+p4AllAddup) ;
			}else{
				if(p2AllToday){
					p2AllCount++;
				}
				p4AllAddup+=p3AllAddup;
				System.out.println("此人今日現金累計未超過五十萬") ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數："+p2AllCount) ;
				System.out.println("三日內現金累計："+p4AllAddup) ;
			}
			//今日無現金三日檢核不用跳
			if(p3AllAddup == 0){
				p4AllAddup = 0;	
			}
			//今日現金匯款無45~50之間不用跳
			if(!p2AllToday){
				p2AllCount = 0;
			}
			
			if(p3Addup>=500000){
				p2Count = 0;
				p4Addup = 0;
				System.out.println("此人今日現金累計超過五十萬，計數歸零(個人):"+p3Addup) ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數(個人)："+p2Count) ;
				System.out.println("三日內現金累計(個人)："+p4Addup) ;
			}else{
				if(p2Today){
					p2Count++;
				}
				p4Addup+=p3Addup;//前二日歸零只算今天
				System.out.println("此人今日現金累計未超過五十萬(個人)") ;
				System.out.println("三日內現金、匯款累計介於45~50之間次數(個人)："+p2Count) ;
				System.out.println("三日內現金累計(個人)："+p4Addup) ;
			}
			//今日無現金三日檢核不用跳
			if(p3Addup == 0){
				p4Addup = 0;	
			}
			//今日現金匯款無45~50之間不用跳
			if(!p2Today){
				p2Count = 0;
			}
			
			//ALL
			//樣態1
			if(p1AllCount>=2){
				//Sale05M070
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+allOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','001','客戶"+allOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//AS400
				strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '001', '客戶"+allOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbJGENLIB.execFromPool(strJGENLIBSql);
				amlMsg.add("客戶"+allOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。");
			}else{
				//不符合
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','001','客戶"+allOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
			}
			//樣態3
			if(p3AllAddup>499999){
				//Sale05M070
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+allOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','003','客戶"+allOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//AS400
				strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '003', '客戶"+allOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbJGENLIB.execFromPool(strJGENLIBSql);
				amlMsg.add("客戶"+allOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。");
			}else{
				//不符合
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','003','客戶"+allOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
			}
			//個人
			if(p1Count>=2){
				//Sale05M070
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+strOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','001','客戶"+strOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//AS400
				strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '001', '客戶"+strOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbJGENLIB.execFromPool(strJGENLIBSql);
				amlMsg.add("客戶"+strOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。");
			}else{
				//不符合
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','001','客戶"+strOrderName+"單日單戶支付2筆以上交易款項，且每筆介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
			}
			if(p3Addup>499999){
				//Sale05M070
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+strOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','003','客戶"+strOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
				//AS400
				strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '003', '客戶"+strOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbJGENLIB.execFromPool(strJGENLIBSql);
				amlMsg.add("客戶"+strOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。");
			}else{
				//不符合
				strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','003','客戶"+strOrderName+"單日現金繳納達新台幣50萬元以上，請申報大額通貨交易並依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
				dbSale.execFromPool(strSaleSql);
				intRecordNo++;
			}
			///20200828+若前兩日未收款，不顯示三日累計
			if(retADayBefore84Tab.length != 0 || retTwoDayBefore84Tab.length != 0){
				//樣態2
				if(p2AllCount>=2){
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+allOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','002','客戶"+allOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '002', '客戶"+allOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					amlMsg.add("客戶"+allOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。");
				}else{
					//不符合
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','002','客戶"+allOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}
				//樣態4
				if(p4AllAddup>499999){
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+allOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','004','客戶"+allOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '004', '客戶"+allOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					amlMsg.add("客戶"+allOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。");
				}else{
					//不符合
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','004','客戶"+allOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}
				if(p2Count>=2){
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+strOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','002','客戶"+strOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '002', '客戶"+strOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					amlMsg.add("客戶"+strOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。");
				}else{
					//不符合
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','002','客戶"+strOrderName+"3個營業日內，有2日以現金或匯款介於新台幣45萬且未達50萬元範圍，請依洗錢及資恐防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}
				if(p4Addup>499999){
					//Sale05M070
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','客戶"+strOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','004','客戶"+strOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
					//AS400
					strJGENLIBSql = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '"+strDocNo+"', '"+RocNowDate+"', '"+strOrderID+"', '"+strOrderName+"', '773', '004', '客戶"+strOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbJGENLIB.execFromPool(strJGENLIBSql);
					amlMsg.add("客戶"+strOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。");
				}else{
					//不符合
					strSaleSql = "INSERT INTO Sale05M070 (DocNo,OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,EDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"+strDocNo+"','"+strOrderNo+"','"+strProjectID1+"','"+intRecordNo+"','"+actionNo+"','收款單','客戶資料','"+strActionName+"','不符合','"+allOrderID+"','"+allOrderName+"','"+strEDate+"','RY','773','004','客戶"+strOrderName+"3個營業日內，累計繳交現金超過50萬元。請依洗錢防制作業辦理。','"+empNo+"','"+RocNowDate+"','"+strNowTime+"')";
					dbSale.execFromPool(strSaleSql);
					intRecordNo++;
				}
			}
		}//此單的所有人
		//AML MSG 處理
		Iterator  it = amlMsg.iterator();
		while(it.hasNext()){
			String tempMsg =(String)it.next();
			System.out.println(tempMsg);
			errMsg =errMsg+tempMsg+"\n";
		}
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
