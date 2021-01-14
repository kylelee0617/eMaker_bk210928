package Invoice.InvoR010;
import javax.swing.*;

import Invoice.utils.InvoicePrintUtil;
import Invoice.vo.*;
import jcx.jform.sproc;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*; 
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class InvoicePrint_Elec extends sproc{
  public String getDefaultValue(String value)throws Throwable{
    System.out.println("-------------------InvoicePrintTest2----------------------S") ;
    
    String PRINTURL = ((Map)get("config")).get("PRINTURL") != null? ((Map)get("config")).get("PRINTURL").toString().trim():"";
    System.out.println("列印位置" + PRINTURL);
    
    String CompanyNo = getValue("CompanyNo");
    String InvoiceDate = getValue("InvoiceDate");
    String [][] table = getTableData("table1");
    String PrintUserNo = getUser();
    StringBuffer choose = new StringBuffer();
    int cv=0;
    for(int x = 0 ; x<table.length ; x++){
      if(table[x][0].trim().equals("Y")){
        if(choose.length()==0) choose.append("'"+table[x][2].trim()+"'"); else choose.append(",'"+table[x][2].trim()+"'");
        cv++;
      }
    }
    if(cv==0){
      messagebox("請至少勾選一筆資料");
      return value;
    }
    
    System.out.println("choose>>>" + choose);
    
    talk t = getTalk("Invoice");
    talk t1 = getTalk("Sale");
    talk tFE5D = getTalk("FE5D");
    
    StringBuffer sql = new StringBuffer();
    sql.append(" select InvoiceNo ");
    sql.append(" from InvoM030");
    sql.append(" where DELYes='Y'");
    if(CompanyNo.length()!=0) sql.append(" and CompanyNo='" + CompanyNo + "'");
    if(InvoiceDate.length()!=0) sql.append(" and SUBSTRING(InvoiceDate,1,4)+SUBSTRING(InvoiceDate,6,2)+SUBSTRING(InvoiceDate,9,2) between "+InvoiceDate.replaceAll("/","")+" ");
    if(choose.length()!=0) sql.append(" and InvoiceNo IN ("+choose.toString()+") ORDER BY InvoiceNo");
    String [][] M030 = t.queryFromPool(sql.toString());
    
    System.out.println("-----------------------------------------11111") ;
    HashMap hM030 = new HashMap();
    for(int x=0 ; x<M030.length ; x++){
      hM030.put(M030[x][0].trim(),M030[x][0].trim());
    }
    StringBuffer vM030 = new StringBuffer();
    for(int x = 0 ; x<table.length ; x++){
      if(hM030. containsKey(table[x][2].trim()) && table[x][0].trim().equals("Y")){
        if(vM030.length()==0) vM030.append("發票號碼作廢，請點選燈泡，檢視作廢發票清單\r\n");
        vM030.append("發票號碼 : "+table[x][2].trim()+"\r\n");
      }
    }
    
//    String PrintDateTime = datetime.getToday("YYYY/mm/dd") + " " + datetime.getTime("h:m:s");
    String PrintDateTime = datetime.getToday("YYYYmmdd") + datetime.getTime("hms");
    setValue("PrintDateTime",PrintDateTime);
    String PrintStatus = "產生發票";
    
    //主檔
    HashMap hM030_1 = new HashMap();
    // 0.發票號碼 1.發票日期 2.買受人名稱 3.營業稅種類
    sql = new StringBuffer(" select a.InvoiceNo, a.InvoiceDate, a.CustomName, a.TaxKind ");
    // 4.案別 5.戶別6.車位 7.發票未稅金額 8.發票稅額 9.發票總金額
    sql.append(",'', a.HuBei, a.HuBei, isnull(a.InvoiceMoney,'0'), isnull(a.InvoiceTax,'0'), isnull(a.InvoiceTotalMoney, '0') ");
    // 10.列印次數 11.發票聯式(2/3) 12.品名代號 13.買受人統編 14.是否刪除 15 發票種類 16 客服戶別
    sql.append(",isnull(a.PrintTimes,'0'), InvoiceKind, PointNo,CustomNo, isnull(DELYes,''), ProcessInvoiceNo, isnull(OBJECT_CD, '' )");
    // 17. 隨機碼 , 18. 公司代碼 , 19. 發票開立日期
    sql.append(",a.RandomCode ,a.CompanyNo , a.CreateDateTime , a.InvoiceTime ");
    sql.append("from InvoM030 a ");
    sql.append("where 1=1 ");
    // sql.append("and isnull(a.RandomCode,'') != '' "); //只找電子發票
    if (choose.length() != 0) sql.append(" and InvoiceNo in (" + choose.toString() + ")");
    String[][] InvoM030 = t.queryFromPool(sql.toString());
    for (int x = 0; x < InvoM030.length; x++) {
      hM030_1.put(InvoM030[x][0].trim(), InvoM030[x]);
      StringBuilder tmpsb = new StringBuilder();
      for(int i=0 ; i<InvoM030[x].length ; i++) {
        tmpsb.append(i).append("-").append(InvoM030[x][i].trim()).append(" ; ");
      }
      System.out.println("InvoM030>>>" + tmpsb.toString());
    }
    
    //案別代號 - 名稱
    HashMap hM0D0 = new HashMap();
    sql = new StringBuffer(" select ProjectNo,ProjectName from InvoM0D0 ");
    String[][] InvoM0D0 = t.queryFromPool(sql.toString());
    for (int x = 0; x < InvoM0D0.length; x++) {
      hM0D0.put(InvoM0D0[x][0].trim(), InvoM0D0[x][1].trim());
    }

    //行銷客戶姓名地址
    HashMap hCustom = new HashMap();
    sql = new StringBuffer(" select DISTINCT "
        + "RTRIM(ISNULL(a.City,'')) + RTRIM(ISNULL(a.Town,'')) + RTRIM(a.Address)"  //0
        + ", a.CustomName"  //1
        + ", RTRIM(c.InvoiceNo) + RTRIM(a.CustomNo)"  //2
        + ", RTRIM(ISNULL(a.ZIP,'')) ");  //3
    sql.append(" from Sale05M091 a,Sale05M086 b,Sale05M087 c ");
    sql.append("  where b.DocNo=c.DocNo and a.OrderNo=b.OrderNo");
    if (choose.length() != 0) sql.append(" and c.InvoiceNo in (" + choose.toString() + ")");
    String[][] Custom = t1.queryFromPool(sql.toString());
    for (int x = 0; x < Custom.length; x++) {
      hCustom.put(Custom[x][2].trim(), Custom[x]);
    }

    //品項代號 - 名稱
    HashMap hM010 = new HashMap();
    sql = new StringBuffer("select PointNo,PointName from InvoM010");
    String[][] InvoM010 = t.queryFromPool(sql.toString());
    for (int x = 0; x < InvoM010.length; x++) {
      hM010.put(InvoM010[x][0].trim(), InvoM010[x][1].trim());
    }

    //期款
    HashMap hM031 = new HashMap();;
  sql = new StringBuffer("select UPPER(InvoiceNo),','+RTRIM(Detailitem)+RTRIM(Remark) from InvoM031");
    if (choose.length() != 0) sql.append(" WHERE InvoiceNo in (" + choose.toString() + ")");
    String[][] InvoM031 = t.queryFromPool(sql.toString());
    for (int x = 0; x < InvoM031.length; x++) {
      String temp[] = { "", "" };
      if (hM031.containsKey(InvoM031[x][0])) {
        String Invo_temp = (String) hM031.get(InvoM031[x][0].trim());
        temp[0] = InvoM031[x][0].trim();
        temp[1] += Invo_temp + InvoM031[x][1].trim();
        hM031.put( temp[0].trim() , temp[1].trim() );
      } else {
        hM031.put( InvoM031[x][0].trim() , InvoM031[x][1].trim() );
      }
    }

    // 開始組資料
    InvoicePrintUtil iPrintUtil = new InvoicePrintUtil( PRINTURL );
    Hashtable result = new Hashtable();
    StringBuilder sbError = new StringBuilder();
    for (int x = 0; x < table.length; x++) {
      if (table[x][0].trim().equals("Y")) {
        String[] Invo_temp = (String[]) hM030_1.get( table[x][2].trim() );
        String InvoiceNo = Invo_temp[0].trim();        // 發票號碼
        
        //過濾
        if (Invo_temp == null) continue;        //沒主檔
        if (!PrintStatus.equals("產生作廢聯") && Invo_temp[14].trim().equals("Y")) continue; //已作廢(刪除)不處理
        
        InvoiceDate = Invo_temp[1].trim();             // 發票日期
        String invoiceTime = Invo_temp[20].trim();     // 發票時間
        String CustomName = Invo_temp[2].trim();       // 客戶名稱
        String ProjectNo = table[x][4].trim();  // 案別
        String HuBei = Invo_temp[5].trim();            // 戶別
        String Detailltem = hM031.get( table[x][2].trim() )!=null? (String)hM031.get( table[x][2].trim() ):""  ; // 期款名稱
        if(Detailltem.length() > 0) {
          Detailltem = Detailltem.substring(1);
        }
        String InvoiceMoney = Invo_temp[7].trim();     // 發票未稅金額
        String InvoiceTax = Invo_temp[8].trim();       // 發票稅額
        String taxKind = Invo_temp[3].trim();          // 營業稅種類
        String InvoiceTotalMoney = Invo_temp[9].trim();// 發票總金額
        int PrintTime = Integer.parseInt(Invo_temp[10].trim());       //列印時間
        String PointNo = Invo_temp[12].trim();         //品項代號
        String PointName = (String) hM010.get(PointNo);
        String randomCode = Invo_temp[17].trim();      //隨機碼
        CompanyNo = Invo_temp[18].trim();
        String createDateTime = Invo_temp[19].trim();      //發票開立日期
        String CustomNo = Invo_temp[13].trim();        //客戶ID
        String ProcessInvoiceNo = Invo_temp[15].trim();
        String OBJECT_CD = Invo_temp[16].trim();
        String buyerZip = "";
        String address = "";
        String[] CustomInfo = (String[]) hCustom.get(table[x][2].trim() + CustomNo.trim()); //客戶資料 (發票號碼+客戶ID)
        if (CustomInfo != null) {
          buyerZip = CustomInfo[3].trim();
          address += CustomInfo[0].trim();
        }
        
        // 2012-06-18 公司名稱... 修正
        String companyInvoNo = "";
        String Company_Name = "";
        String[][] FED1023A = t.queryFromPool("SELECT "
            + "AdminNo "
            + ",Company_Name "
            + ",CompanyInvoiceNo "
            + "FROM FED1023A "
            + "WHERE "
            + "Company_CD = '" + CompanyNo + "' "
            + "AND ProjectNo = '" + ProjectNo + "' ");
        if (FED1023A.length > 0) {
          Company_Name = FED1023A[0][1].trim();
          companyInvoNo = FED1023A[0][2].trim();
        }
        
        // 客服客戶資料
        if (ProcessInvoiceNo.equals("2")) {
          if (ProjectNo.trim().equals("H38")) ProjectNo = "H38A";
          String stringSQL = "SELECT OBJECT_FULL_NAME, MAIL_ADDR FROM FE5D05 " 
              + "WHERE RTRIM(DEPT_CD)+RTRIM(DEPT_CD_1) = '" + ProjectNo + "' AND OBJECT_CD = '" + OBJECT_CD + "'";
          String[][] AFE5D05 = tFE5D.queryFromPool(stringSQL);
          if (AFE5D05.length > 0) {
            address = AFE5D05[0][1].trim();
            CustomName = AFE5D05[0][0].trim();
			//拆出郵遞區號
			//判斷郵遞區號種類
			if (isInteger(AFE5D05[0][1].trim().substring(0, 6)) ){//3+3碼
				//重設address&buyerZip
				buyerZip = AFE5D05[0][1].trim().substring(0, 6) ;
				address = AFE5D05[0][1].trim().substring( 6) ;
			}else if (isInteger(AFE5D05[0][1].trim().substring(0, 5)) ){//3+2碼
				buyerZip = AFE5D05[0][1].trim().substring(0, 5) ;
				address = AFE5D05[0][1].trim().substring( 5) ;
			}else if (isInteger(AFE5D05[0][1].trim().substring(0, 3)) ){//3碼
				buyerZip = AFE5D05[0][1].trim().substring(0, 3) ;
				address = AFE5D05[0][1].trim().substring( 3) ;
			}
          }
          stringSQL = "SELECT "
              + "OBJECT_FULL_NAME " 
              + "FROM InvoM0C0 " 
              + "WHERE CustomNo = '" + CustomNo + "' " 
              + "AND LEN(OBJECT_FULL_NAME) > 0 ";
          String[][] AInvoM0C0 = t.queryFromPool(stringSQL);
          if (AInvoM0C0.length > 0) {
            if (AInvoM0C0[0][0].length() > 0) {
              CustomName = AInvoM0C0[0][0].trim();
            }
          }
          CustomInfo = new String[3];
        }
        
        //以上沒有客戶資料 則從發票系統找
        if(CustomInfo==null){
          String stringSQL = " SELECT "
              + "ZIPCode "
              + ", RTRIM(ISNULL(City,'')) "
              + ", RTRIM(ISNULL(Town,'')) "
              + ",RTRIM(Address) " 
              + "FROM InvoM0C0 " 
              + "WHERE CustomNo = '" + CustomNo + "' ";
          String [][] AInvoM0C0 = t.queryFromPool(stringSQL);
          if (AInvoM0C0.length > 0){
             if(AInvoM0C0[0][1].trim().equals(AInvoM0C0[0][2].trim())) {
               buyerZip = AInvoM0C0[0][0].trim();
               address = AInvoM0C0[0][1].trim() + AInvoM0C0[0][3].trim();
             }else {
               buyerZip = AInvoM0C0[0][0].trim();
               address = AInvoM0C0[0][1].trim() + AInvoM0C0[0][2].trim() + AInvoM0C0[0][3].trim();
             }
          }
        }
        System.out.println("產生發票=" + InvoiceNo);
        
       //明細內容
        StringBuilder sbDetail = new StringBuilder();
        String invoiceDateTime = InvoiceDate + " " + invoiceTime;
        sbDetail.append("營業人統編:").append(companyInvoNo).append(";");
        if(Company_Name.length() > 0) {
          sbDetail.append("名稱:").append( Company_Name.replaceAll("股份有限", "(股)").replaceAll("市辦事處", "辦") ).append(";");
        }
        sbDetail.append("日期:").append(invoiceDateTime).append(";");
        sbDetail.append("發票號碼:").append(InvoiceNo).append(";");
        sbDetail.append("買受人:").append(CustomName).append(";");
        sbDetail.append("案名:").append( hM0D0.get(ProjectNo).toString().trim() ).append(";");
        sbDetail.append("棟樓別:").append(HuBei).append(";");
        sbDetail.append("摘要:").append(PointName + " - " + Detailltem );
        
        InvoicePrintVo vo = new InvoicePrintVo();
        //---收件人
        vo.setRecipientPost(buyerZip);
        vo.setRecipientAddr(address);
        vo.setRecipientCompany("");
        vo.setRecipientName(CustomName);
        //----發票內容
        vo.setInvoiceDate( Integer.toString(Integer.parseInt(InvoiceDate.replaceAll("/", "")) - 19110000) );
        vo.setInvoiceNumber(InvoiceNo);
        vo.setPrintDate(invoiceDateTime.replaceAll("/", "").replaceAll(" ", "").replaceAll(":", ""));
        vo.setRandomCode(randomCode);
        
        //發票金額
        vo.setSaleAmount(InvoiceMoney);
        //稅額
        if( "C".equals(taxKind) ) {
          InvoiceTax = "";
        }
        vo.setTax(InvoiceTax);
        //發票總額
        vo.setTotal(InvoiceTotalMoney);
        
        if(CustomNo.length() == 8) {  //法人才要傳送統編
          vo.setBuyerId(CustomNo);
        }else {
          vo.setBuyerId("");
        }
        vo.setSellerId(companyInvoNo);
        vo.setDetail(sbDetail.toString());
        vo.setPrintCount("" + (PrintTime + 1) );
        vo.setDeptId(PrintUserNo.equals("flife")? "25000":"去印25F啦");
        vo.setBuyerName(CustomName);
        
        String rs = iPrintUtil.doPrint(vo);
        boolean printOK = rs.indexOf("SUCCESS:") == 0;
        if(printOK) {
          StringBuilder sql2 = new StringBuilder();
          sql2.append("update InvoM030 ");
          sql2.append("set printYES='").append("Y").append("' ");
          sql2.append(", printTimes='").append(PrintTime + 1).append("' ");
          sql2.append("where InvoiceNo='").append(InvoiceNo).append("' ");
          t.execFromPool(sql2.toString());
          result.put( InvoiceNo, "列印成功於:" + rs.replace("SUCCESS:", "") );
        }else {
          result.put( InvoiceNo, "發生問題:" + rs.replace("ERROR:", "") );
      if(sbError.length() != 0) sbError.append(",");
          sbError.append(InvoiceNo);
        }
        
      } //if end
    } //for end
    
    System.out.println(">>>result:" + result);
    if(sbError.length() == 0) {
      message("列印完成。。。  \u30fd(\u273f\uff9f▽\uff9f)\u30ce");
    }else {
      messagebox("以下發票列印發生問題，請聯繫資訊主辦: \n" + sbError.toString());
    }
    
    return value;
  }
  public String getInformation(){
    return "---------------button1(發票套印).defaultValue()----------------";
  }
  
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
}
