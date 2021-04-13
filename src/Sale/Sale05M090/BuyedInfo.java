package Sale.Sale05M090.Form;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.util.Result;

public class BuyedInfo extends bproc{
    talk dbSale =  getTalk("Sale") ;
  talk db400CRM =  getTalk("400CRM") ;

  public String getDefaultValue(String value)throws Throwable{
    if ( "172.22.14.4".equals( get("serverIP").toString().trim() ) ) {
      System.out.println(">>>測試環境<<<" + value);
    }

    //參數處理
    String[] params = null;
    if ( value.indexOf("=") != -1 ) {
      params = value.split("=");
    }

    Result rs = this.updateTableBuyedInfo(params);
    setValue( "BuyedResult" , rs.getReturnCode() + "," + rs.getReturnMsg() );

    if ( "checkAndEmail".equals(value) ) {
      if ( rs.getReturnCode() != 0 ) {
        messagebox( rs.getReturnMsg() );

        JButton jb = getButton("sendMail");
        jb.setText("rReview");
        jb.doClick();
      }
    }
        
    return "更新購買資訊";
    }
    
    public Result updateTableBuyedInfo(String[] params) throws Throwable {
    System.out.println(">>>更新購買資訊表格 START<<<");
    Result rs = new Result();
    String returnMsg = "";
    String param = "";
    String value = "";

    if (params != null && params.length == 2) {
      param = params[0].trim();
      value = params[1].trim();
    }

    //組成ID name
    String[][] table091 = getTableData("table1");
    System.out.println("table1 size>>>" + table091.length);
    if (table091.length == 0) {
      rs.setReturnCode(80);
      rs.setReturnMsg("尚未指定客戶");
      return rs;
    }

    StringBuilder qCustomNos = new StringBuilder();
    StringBuilder qCustomNames = new StringBuilder();
    for( int i=0 ; i<table091.length ; i++){

      //不要已被換名的
      if ( "C".equals(table091[i][23].trim()) ) {
        continue;
      }

      //沒有Name不要
      if ( "".equals(table091[i][6].trim()) ) {
        continue;
      }

      //在欄位檢核當下，會抓不到那個欄位，從參數帶入
      if ( "userCusNo".equals(param) && "".equals(table091[i][5].trim()) ) {
        table091[i][5] = value ;
      }

      qCustomNos.append("'").append(table091[i][5]).append("'").append(",");
      qCustomNames.append("'").append(table091[i][6]).append("'").append(",");
    }
    String strQcusNos = qCustomNos.substring(0 , qCustomNos.length()-1);
    String strQcusNames = qCustomNames.substring(0 , qCustomNames.length()-1);

    System.out.println("no>>>" + strQcusNos );
    System.out.println("name>>>" + strQcusNames );

    //組成棟樓別
    String[][] table092 = getTableData("table2");
    StringBuilder qPositions = new StringBuilder();
    for( int i=0 ; i<table092.length ; i++){
      if ( i > 0) {
        qPositions.append(",");
      }

      //如果傳來的param = positionNo，要把position帶過來加入
      if ( "positionNo".equals(param) && "".equals(table092[i][3].trim()) ) {
        table092[i][3] = value ;
      }
      qPositions.append("'").append(table092[i][3]).append("'");
    }
    String strPositions = qPositions.toString();

    System.out.println("strPositions>>>" + strPositions );
    //準備 END

    //過去購買
    if ( strQcusNos.length() > 0 ) {
      String[][] orderBuyer =  this.getOrderByIDs(strQcusNos , strPositions);
      setTableData("tableBuyedInfo" , orderBuyer );
    }
    
    //購買人定審
    boolean rReviewAlert = false;   //預警客戶未定審完成
    Map mapPSREPF = this.getMapPSREPFByIDs( strQcusNos );
    String[] cusNos = strQcusNos.split(",");
    String[] cusNames = strQcusNames.split(",");
    String[][] rReviews = new String[cusNos.length][4];
    for (int i=0 ; i<rReviews.length ; i++) {
      String[] rReview = rReviews[i];
      
      //姓名
      rReview[0] = cusNames[i].replaceAll("'" , "").trim();

      //是否定審
      String psrepfKey = (cusNos[i].toString() + cusNames[i].toString()).replaceAll("'" , "");
      // System.out.println("kkkey>>>" + psrepfKey);
      String[] psrepf = (String[])mapPSREPF.get(psrepfKey) ;
      rReview[1] = psrepf==null ? "否":"是" ;
      // System.out.println(">>> psrepf >>>" + psrepfKey);

      //定審結案日
      String rRDate = "無";
      if (psrepf != null) {
        rRDate = "0".equals(psrepf[5].toString().trim()) == true ? "未完成" : formatDate_ROC2AC( psrepf[5].trim() ) ;
      }
      rReview[2] = rRDate ;

      //定審結果
      String rRrs = "無";
      if (psrepf != null) {
        rRrs = "S".equals(psrepf[4].toString().trim()) == true ? "完成" : "未完成" ;
      }
      rReview[3] = rRrs ;

      //有定審未完成時，請於訂單新增時，跳出視窗預警
      if ( "未完成".equals(rRrs) ) {
        rReviewAlert = true;
        returnMsg += "客戶 [" + rReview[0] + "] 未完成定審作業，請依定審作業程序執行。\n";
      }
    }
    setTableData("tableRreview" , rReviews );

    //判讀結果要示警 & 通知
    if( rReviewAlert == true ) {
      rs.setReturnCode(91);
      rs.setReturnMsg(returnMsg);
    };

    System.out.println(">>>更新購買資訊表格 END<<<");
    return rs;
    }

  public String[][] getOrderByIDs(String qCustomNos , String qPositions) throws Throwable {
    StringBuilder sql = new StringBuilder();
    sql.append("select distinct t090.orderNo, t092.recordNo, t091.customName,t091.riskValue, t090.projectid1, t092.position,  tContract.contractDate ");
    sql.append("from sale05m090 t090 ");
    sql.append("left join sale05m091 t091 on t090.orderNo = t091.orderNo ");
    sql.append("left join sale05m092 t092 on t090.orderNO = t092.orderNo ");
    sql.append("left join (select t274.contractNo , t278.orderNo , t274.contractDate from sale05m274 t274, sale05m278 t278 where t274.contractNo = t278.contractNo) tContract on t090.orderNo = tContract.orderNo ");
    sql.append("Where 1=1 ");
    sql.append("AND t091.customNo in (" + qCustomNos + ") AND ISNULL(t091.StatusCd , '') != 'C' ");
    if ( !"".equals(qPositions) ) {
      sql.append("AND t092.position not in (" + qPositions + ") ");
    }
    sql.append("order by t090.orderNo , t092.recordNo asc");

    return dbSale.queryFromPool(sql.toString()) ;
  }

  public String[][] getPSREPFbyIDs(String qCustomNos) throws Throwable {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT RE00 , RE01 , RE12 , RE06 , RE24 , RE34 ");
    sql.append("From PSREPF ");
    sql.append("Where 1=1 ");
    sql.append("and RE01 in (" + qCustomNos + ") ");

    return db400CRM.queryFromPool(sql.toString()) ;
  }

  public Map getMapPSREPFByIDs(String qCustomNos) throws Throwable {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT RE00 , RE01 , RE12 , RE06 , RE24 , RE34 ");
    sql.append("From PSREPF ");
    sql.append("Where 1=1 ");
    sql.append("and RE01 in (" + qCustomNos + ") ");
    String[][] retQueryResult = db400CRM.queryFromPool(sql.toString()) ;

    Map mapRS = new HashMap();
    for ( int ii=0 ; ii<retQueryResult.length ; ii++ ) {
      String key = retQueryResult[ii][1].toString() + retQueryResult[ii][2].toString().replaceAll("　", " ").trim();
      // System.out.println("key>>>" + key);
      mapRS.put( key , retQueryResult[ii]);
    }

    // Map myMap = mapRS;
    // for (Iterator it = myMap.entrySet().iterator(); it.hasNext();) {
    //  Map.Entry mapEntry = (Map.Entry) it.next();
    //  System.out.println("The key is: " + mapEntry.getKey() + ",value is :" + mapEntry.getValue());
    // }
    
    return mapRS ;
  }

  private String formatDate_ROC2AC(String srcStr) throws Throwable {
    String newStr = "";
    if(srcStr.length() != 7) {
      return newStr;
    }
    newStr = "" + (Integer.parseInt( srcStr.substring(0,3) ) + 1911) + "/" + srcStr.substring(3,5) + "/" + srcStr.substring(5,7);
        return newStr;
    }

  public String getInformation(){
    return "---------------Sale05M090_FuncBuyInfo----------------";
  }
}
