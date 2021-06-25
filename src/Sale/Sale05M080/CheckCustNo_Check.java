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
      //���m�W�dID
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
        
        //210526 Kyle : �ܴ��ק� Start : �����h���q��ΫȤ�
        //���q��s�� & ���
        JTable jt3 = this.getTable("table3"); //�Ȥ���
        JTable jt4 = this.getTable("table4"); //�q����
        for(int i=0 ; i<jt4.getRowCount() ; i++) {
          String orderNo = getValueAt("table4", i, "OrderNo").toString().trim();
          String orderDate = "";
          for(int ii=0 ; ii<jt3.getRowCount() ; ii++) {
            String customNo = getValueAt("table3", i, "CustomNo").toString().trim();
            String sql = "select a.orderNo , a.orderDate from Sale05M090 a , Sale05M091 b where a.orderNo=b.orderNo and b.orderNo='"+orderNo+"' and b.customNo='"+customNo+"' ";
            String[][] retOrder = dbSale.queryFromPool(sql);
            if(retOrder.length > 0) {
              orderDate = retOrder[0][1].trim();  //���ӥu�|���@��
              break;
            }
          }
          if(StringUtils.isNotBlank(orderDate)) {
            //�q��&�Ȥ�w�t��A����ܴ� start
            String amlText = projectID + "," + orderNo + "," + orderDate + "," + getFunctionName() + "," + "�H�Υd�Nú�ڤH���" 
                           + "," + value + "," + retQuery[0][0].trim() + "," + BirthDay + "," + indCode + "," + "query1821";
            setValue("AMLText" , amlText);
            getButton("BtCustAML").doClick();
            errMsg += getValue("AMLText").trim();
            // �ܴ�END
            
            //���ӥu�|�t���@���A�����N�i�H�{�F
            break;
          }
        }
        //210526 Kyle : �ܴ��ק� End

        //19. �Q�`���Y�H
        if("Y".equals(retQuery[0][3].trim())){
          errMsg += "�Nú�ڤH"+retQuery[0][0].trim()+"�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
        }

        //17. ���ަW��
        if("Y".equals(retQuery[0][1].trim()) || "Y".equals(retQuery[0][2].trim())){
          errMsg += "�Nú�ڤH"+retQuery[0][0].trim()+"���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
        }

        if(!"".equals(errMsg)){
          messagebox(errMsg);
        }
      } else {
        setValueAt("table2", "", s_row,"DeputyName");
        setValueAt("table2",  "", s_row,"B_STATUS");
        setValueAt("table2",  "", s_row,"C_STATUS");
        setValueAt("table2",  "", s_row,"R_STATUS");
        message("�L�����N�z�H��T�C") ;
      }
    }
     return true;
  }
  public String getInformation(){
    return "---------------null(null).DeputyID.field_check()----------------";
  }
}
