package Sale.Sale05M080;

import java.text.SimpleDateFormat;
import javax.swing.JTable;
import jcx.db.talk;
import jcx.jform.bvalidate;

public class CreditNoCheck_bk210526 extends bvalidate{
  public boolean check(String value)throws Throwable{
    // 可自定欄位檢核條件 
    // 傳入值 value 原輸入值 
    String strNowTimestamp =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate());
    JTable tb5   =  getTable("table5");
    int s_row   =  tb5.getSelectedRow(); 
    String projectID =getValue("field2").trim() ;
    if(!"".equals(value)){
      //取姓名查ID
      talk dbpw0d = getTalk("pw0d");
      String stringSql      = "SELECT TOP 1 NAME,B_STATUS,C_STATUS,R_STATUS,BIRTHDAY FROM QUERY_LOG WHERE RTRIM(QUERY_ID) = '" + value + "' AND PROJECT_ID = '"+projectID+"' ORDER BY QID DESC ";
      String retQuery[][]  =  dbpw0d.queryFromPool(stringSql) ;
      String errMsg="";
      if(retQuery.length  >  0) { 
        setValueAt("table5", retQuery[0][0].trim(), s_row,"DeputyName");
        setValueAt("table5", "Y", s_row,"PaymentDeputy");
        setValueAt("table5", retQuery[0][1].trim(), s_row,"B_STATUS");
        setValueAt("table5", retQuery[0][2].trim(), s_row,"C_STATUS");
        setValueAt("table5", retQuery[0][3].trim(), s_row,"R_STATUS");
        String BirthDay  = retQuery[0][4].trim().replace("/","-");
        
        //18. 制裁名單
        talk db400 = getTalk("400CRM");
        String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
        String retCList[][] = db400.queryFromPool(str400sql);
        if(retCList.length > 0) {
          errMsg +="代繳款人"+retQuery[0][0].trim()+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。\n";
        }
        
        //19. 利害關係人
        if("Y".equals(retQuery[0][3].trim())){
          errMsg +="代繳款人"+retQuery[0][0].trim()+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
        }
        
        //20. 黑名單
        
        
        //17. 控管名單
        if("Y".equals(retQuery[0][1].trim()) || "Y".equals(retQuery[0][2].trim())){
          errMsg +="代繳款人"+retQuery[0][0].trim()+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
        }
        
        //21. 政治名單171
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
        String ret171List[][] = db400.queryFromPool(str400sql);
        if(ret171List.length > 0) {
          errMsg +="代繳款人"+retQuery[0][0].trim()+"、家庭成員及有密切關係之人，為重要政治性職務人士，請加強客戶盡職調查，並依洗錢及資恐防制作業辦理。\n";
        }
        
        if(!"".equals(errMsg)){
          messagebox(errMsg);
        }
      } else {
        setValueAt("table5", "", s_row,"DeputyName");
        setValueAt("table5","", s_row,"B_STATUS");
        setValueAt("table5","", s_row,"C_STATUS");
        setValueAt("table5","", s_row,"R_STATUS");
        message("無此筆代理人資訊。") ;
      }
    }
     return true;
  }
  public String getInformation(){
    return "---------------null(null).DeputyID.field_check()----------------";
  }
}
