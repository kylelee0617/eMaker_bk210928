package Sale.Sale05M080;

import java.text.SimpleDateFormat;
import javax.swing.JTable;
import jcx.db.talk;
import jcx.jform.bvalidate;

public class CreditNoCheck_bk210526 extends bvalidate{
  public boolean check(String value)throws Throwable{
    // �i�۩w����ˮֱ��� 
    // �ǤJ�� value ���J�� 
    String strNowTimestamp =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate());
    JTable tb5   =  getTable("table5");
    int s_row   =  tb5.getSelectedRow(); 
    String projectID =getValue("field2").trim() ;
    if(!"".equals(value)){
      //���m�W�dID
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
        
        //18. ����W��
        talk db400 = getTalk("400CRM");
        String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
        String retCList[][] = db400.queryFromPool(str400sql);
        if(retCList.length > 0) {
          errMsg +="�Nú�ڤH"+retQuery[0][0].trim()+"�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC\n";
        }
        
        //19. �Q�`���Y�H
        if("Y".equals(retQuery[0][3].trim())){
          errMsg +="�Nú�ڤH"+retQuery[0][0].trim()+"�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
        }
        
        //20. �¦W��
        
        
        //17. ���ަW��
        if("Y".equals(retQuery[0][1].trim()) || "Y".equals(retQuery[0][2].trim())){
          errMsg +="�Nú�ڤH"+retQuery[0][0].trim()+"���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
        }
        
        //21. �F�v�W��171
        str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N'  AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
        String ret171List[][] = db400.queryFromPool(str400sql);
        if(ret171List.length > 0) {
          errMsg +="�Nú�ڤH"+retQuery[0][0].trim()+"�B�a�x�����Φ��K�����Y���H�A�����n�F�v��¾�ȤH�h�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C\n";
        }
        
        if(!"".equals(errMsg)){
          messagebox(errMsg);
        }
      } else {
        setValueAt("table5", "", s_row,"DeputyName");
        setValueAt("table5","", s_row,"B_STATUS");
        setValueAt("table5","", s_row,"C_STATUS");
        setValueAt("table5","", s_row,"R_STATUS");
        message("�L�����N�z�H��T�C") ;
      }
    }
     return true;
  }
  public String getInformation(){
    return "---------------null(null).DeputyID.field_check()----------------";
  }
}
