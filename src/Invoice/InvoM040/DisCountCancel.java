package Invoice.InvoM040;
import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;

public class DisCountCancel extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    talk dbInvoice = getTalk("Invoice");
    talk dbSale = getTalk("Sale");
    String stringSQL = "";
    String stringUserkey = "";
    if (getValue("DELYes").equals("Y")){
      message("������w�@�o!�L�k�@�o ");
      return value; 
    }
    
    if (getValue("PrintYes").equals("N")){
      message("�����楼�C�L!�L�k�@�o ");
      return value; 
    }
    
    stringSQL = " SELECT DiscountNo " +
                        " FROM InvoM040" +
                        " WHERE DiscountNo = '" + getValue("DiscountNo") + "'" +
                    " AND DELYes ='Y'";
    String retInvoM040[][] = dbInvoice.queryFromPool(stringSQL);
    if (retInvoM040.length >0){
      message("������w�@�o!�L�k�@�o ");
      return value; 
    }
    if (!getUser().equals("B1085")){
    stringSQL = " SELECT DiscountNo " +
                        " FROM Sale05M194" +
                        " WHERE DiscountNo = '" + getValue("DiscountNo") + "'";
    String retSale05M194[][] = dbSale.queryFromPool(stringSQL);
    if (retSale05M194.length >0){
      message("���ڨt�� �w������!�L�k�@�o ");
      return value; 
    }
    }
    //
    Calendar cal= Calendar.getInstance(); //Current time
    stringUserkey = getUser() + "_T" + ""+( (cal.get(Calendar.HOUR_OF_DAY)*10000) + (cal.get(Calendar.MINUTE)*100) + cal.get(Calendar.SECOND) );
    String retSystemDateTime[][] = dbInvoice.queryFromPool("spInvoSystemDateTime  'Admin'");
    String stringSystemDateTime ="";
    stringSystemDateTime = retSystemDateTime[0][0].replace("-","/");
    stringSystemDateTime = stringSystemDateTime.substring(0,19);
    //
    stringSQL = "spInvoM040UpdateDEL " +
               "'" + getValue("DiscountNo").trim() + "'," +
               "'" + getValue("DiscountDate").trim() + "'," +
               "'" + getValue("CompanyNo").trim() + "'," +
               "'" + getValue("DepartNo").trim() + "'," +
               "'" + getValue("ProjectNo").trim() + "'," +           
               "'" + getValue("HuBei").trim() + "'," +                               
               "'" + getValue("CustomNo").trim() + "'," +                              
               "'" + getValue("DiscountWay").trim() + "'," +                     
               "''," +                               
                       getValue("DiscountMoney").trim() + "," +                                        
                       getValue("DiscountTax").trim() + "," +                                        
                       getValue("DiscountTotalMoney").trim() + "," +
               "'2'," +                              
               "'" + getUser() + "'," +
               "'" + stringSystemDateTime  + "'," +
               "'" + stringSystemDateTime  + "'," +          
                   "'D'," +
               "'" + stringUserkey + "'" ;
    dbInvoice.execFromPool(stringSQL);
    
    //�g�JAS400�����D��(�@�o)
    talk as400 = getTalk("AS400");
    StringBuilder sbSQL = new StringBuilder();
    sbSQL.append("INSERT INTO GLEBPFUF ");
    sbSQL.append("(EB01U, EB02U, EB03U, EB04U, EB05U, EB06U, EB07U, EB08U, EB09U, EB10U, EB11U, EB12U, EB13U, EB14U, EB15U, EB16U, EB17U, EB18U, EB19U) ");
    sbSQL.append("values ");
    sbSQL.append("(");
    sbSQL.append("'").append(getValue("DiscountNo").trim()).append("', ");       //�������X
    sbSQL.append("'").append(getValue("DiscountDate").trim()).append("', ");     //�������
    sbSQL.append("'").append(getValue("CompanyNo").trim()).append("', ");       //���q�N�X
    sbSQL.append("'").append(getValue("DepartNo").trim()).append("', ");        //�����N�X
    sbSQL.append("'").append(getValue("ProjectNo").trim()).append("', ");       //�קO�N�X
    sbSQL.append("'").append(getValue("HuBei").trim()).append("', ");           //��O�N��
    sbSQL.append("'").append(getValue("CustomNo").trim()).append("', ");        //�Ȥ�N��
    sbSQL.append("'").append(getValue("DiscountWay").trim()).append("', ");      //Invoice Way
    sbSQL.append("'").append("").append("', ");                                  //�s��O
    sbSQL.append("").append(getValue("DiscountMoney").trim()).append(", ");      //���|
    sbSQL.append("").append(getValue("DiscountTax").trim()).append(", ");        //�|�B
    sbSQL.append("").append(getValue("DiscountTotalMoney").trim()).append(", "); //�t�|
    sbSQL.append("'").append("N").append("', ");                                //�w�C�LYN
    sbSQL.append("").append(0).append(", ");                                    //�ɦL����
    sbSQL.append("'").append("Y").append("', ");                                //�@�oYN
    sbSQL.append("'").append("N").append("', ");                                //�J�bYN
    sbSQL.append("'").append(getUser()).append("', ");                          //�ק�H
    sbSQL.append("'").append(stringSystemDateTime).append("', ");                //���ڮɶ�
    sbSQL.append("'").append("2").append("' ");                                 //PROCESS DISCOUNT
    sbSQL.append(") ");
    as400.execFromPool(sbSQL.toString());
    
    setValue("DELYes","Y");
    message("�w�@�o������ = " + getValue("DiscountNo"));          
    return value;
  }
}
