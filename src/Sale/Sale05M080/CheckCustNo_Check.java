package Sale.Sale05M080;

import javax.swing.JTable;
import org.apache.commons.lang.StringUtils;
import jcx.db.talk;
import jcx.jform.bvalidate;

public class CheckCustNo_Check extends bvalidate{
  public boolean check(String value)throws Throwable{
    JTable tb2   =  getTable("table2");
    int s_row   =  tb2.getSelectedRow(); 
    String projectID =getValue("field2").trim() ;
    if(!"".equals(value)){
      //取姓名查ID
      talk dbSale = getTalk("Sale");
      talk dbpw0d = getTalk("pw0d");
      String stringSql      = "SELECT TOP 1 NAME,B_STATUS,C_STATUS,R_STATUS,BIRTHDAY FROM QUERY_LOG WHERE RTRIM(QUERY_ID) = '" + value + "' AND PROJECT_ID = '"+projectID+"' ORDER BY QID DESC ";
      String retQuery[][]  =  dbpw0d.queryFromPool(stringSql) ;
      String errMsg="";
      if(retQuery.length  >  0) { 
        setValueAt("table2", retQuery[0][0].trim(), s_row,"DeputyName");
        setValueAt("table2", "Y", s_row,"PaymentDeputy");
        setValueAt("table2",  retQuery[0][1].trim(), s_row,"B_STATUS");
        setValueAt("table2",  retQuery[0][2].trim(), s_row,"C_STATUS");
        setValueAt("table2",  retQuery[0][3].trim(), s_row,"R_STATUS");
        String BirthDay  = retQuery[0][4].trim().replace("/","-");
        String indCode = retQuery[0][5].trim();
        
        //210526 Kyle : 萊斯修改 Start : 對應多筆訂單及客戶
        //取訂單編號 & 日期
        JTable jt3 = this.getTable("table3"); //客戶表格
        JTable jt4 = this.getTable("table4"); //訂單表格
        for(int i=0 ; i<jt4.getRowCount() ; i++) {
          String orderNo = getValueAt("table4", i, "OrderNo").toString().trim();
          String orderDate = "";
          for(int ii=0 ; ii<jt3.getRowCount() ; ii++) {
            String customNo = getValueAt("table3", i, "CustomNo").toString().trim();
            String sql = "select a.orderNo , a.orderDate from Sale05M090 a , Sale05M091 b where a.orderNo=b.orderNo and b.orderNo='"+orderNo+"' and b.customNo='"+customNo+"' ";
            String[][] retOrder = dbSale.queryFromPool(sql);
            if(retOrder.length > 0) {
              orderDate = retOrder[0][1].trim();  //應該只會有一組
              break;
            }
          }
          if(StringUtils.isNotBlank(orderDate)) {
            //訂單&客戶已配對，執行萊斯 start
            String amlText = projectID + "," + orderNo + "," + orderDate + "," + getFunctionName() + "," + "信用卡代繳款人資料" 
                           + "," + value + "," + retQuery[0][0].trim() + "," + BirthDay + "," + indCode + "," + "query1821";
            setValue("AMLText" , amlText);
            getButton("BtCustAML").doClick();
            errMsg += getValue("AMLText").trim();
            // 萊斯END
            
            //應該只會配對到一次，做完就可以閃了
            break;
          }
        }
        //210526 Kyle : 萊斯修改 End

        //19. 利害關係人
        if("Y".equals(retQuery[0][3].trim())){
          errMsg += "代繳款人"+retQuery[0][0].trim()+"為公司利害關系人，請依保險業與利害關係人從事放款以外之其他交易管理辦法執行。\n";
        }

        //17. 控管名單
        if("Y".equals(retQuery[0][1].trim()) || "Y".equals(retQuery[0][2].trim())){
          errMsg += "代繳款人"+retQuery[0][0].trim()+"為疑似黑名單對象，請覆核確認後，再進行後續交易相關作業。\n";
        }

        if(!"".equals(errMsg)){
          messagebox(errMsg);
        }
      } else {
        setValueAt("table2", "", s_row,"DeputyName");
        setValueAt("table2",  "", s_row,"B_STATUS");
        setValueAt("table2",  "", s_row,"C_STATUS");
        setValueAt("table2",  "", s_row,"R_STATUS");
        message("無此筆代理人資訊。") ;
      }
    }
     return true;
  }
  public String getInformation(){
    return "---------------null(null).DeputyID.field_check()----------------";
  }
}
