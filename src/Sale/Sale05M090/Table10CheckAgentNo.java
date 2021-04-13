package Sale.Sale05M090;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Table10CheckAgentNo extends bvalidate{
  public boolean check(String value)throws Throwable{
    // 可自定欄位檢核條件 
    // 傳入值 value 原輸入值 
    String strNowTimestamp =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate());
    JTable tb10   =  getTable("table10");
    int s_row   =  tb10.getSelectedRow(); 
    String projectID =getValue("field1").trim() ;
    if(!"".equals(value)){
      talk dbpw0d = getTalk("pw0d");
      String stringSql      = "SELECT TOP 1 NAME,B_STATUS,C_STATUS,R_STATUS,BIRTHDAY FROM QUERY_LOG WHERE RTRIM(QUERY_ID) = '" + value + "' AND PROJECT_ID = '"+projectID+"' ORDER BY QID DESC ";
      String retQuery[][]  =  dbpw0d.queryFromPool(stringSql) ;
      String errMsg="";
      if(retQuery.length  >  0) { 
          setValueAt("table10", retQuery[0][0].trim(), s_row,"AgentName");
          setValueAt("table10", retQuery[0][1].trim(), s_row,"IsBlackList");
          setValueAt("table10", retQuery[0][2].trim(), s_row,"IsControlList");
          setValueAt("table10", retQuery[0][3].trim(), s_row,"IsLinked");
          String BirthDay  = retQuery[0][4].trim().replace("/","-");
          
          //制裁名單
          talk db400 = getTalk("400CRM");
          String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N' AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
          String retCList[][] = db400.queryFromPool(str400sql);
          if(retCList.length > 0) {
            errMsg += "代理人" + retQuery[0][0].trim() + "為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。\n";
          }
          //171
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N' AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
          String ret171List[][] = db400.queryFromPool(str400sql);
          if(ret171List.length > 0) {
            errMsg += "代理人" + retQuery[0][0].trim() + "、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。\n";
          }
          
          /*
          // 萊斯Start
          String birth = " ";
          String Ind = " ";
          setValue("AMLText" , value + "," + retQuery[0][0].trim() + "," + birth + "," + Ind + "," + "query1821");
          getButton("BtCustAML").doClick();
          errMsg += getValue("AMLText").trim();
          // 萊斯END
          */
          
          //黑名單&控管名單
          if("Y".equals(retQuery[0][1].trim()) || "Y".equals(retQuery[0][2].trim())){
            errMsg += "代理人" + retQuery[0][0].trim() + "為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
          }
          if("Y".equals(retQuery[0][3].trim())){//利關人
            errMsg += "代理人" + retQuery[0][0].trim() + "為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
          }
          if(!"".equals(errMsg)){
            messagebox(errMsg);
          }
      } else {
          setValueAt("table10", "", s_row,"AgentName");
          setValueAt("table10", "", s_row,"IsBlackList");
          setValueAt("table10", "", s_row,"IsControlList");
          setValueAt("table10", "", s_row,"IsLinked");
          message("無此筆代理人資訊。") ;
      }
    }
     return true;
  }
  public String getInformation(){
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
