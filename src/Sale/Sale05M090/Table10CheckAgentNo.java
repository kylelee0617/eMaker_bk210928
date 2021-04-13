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
    // �i�۩w����ˮֱ��� 
    // �ǤJ�� value ���J�� 
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
          
          //����W��
          talk db400 = getTalk("400CRM");
          String str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X181' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N' AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
          String retCList[][] = db400.queryFromPool(str400sql);
          if(retCList.length > 0) {
            errMsg += "�N�z�H" + retQuery[0][0].trim() + "�����ޤ�����W���H�A�иT�����A�è̬~��������q���@�~�e�e�k��ǡC\n";
          }
          //171
          str400sql = "SELECT * FROM CRCLNAPF WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE AND L.CONTROLCLASSIFICATIONCODE ='X171' AND C.REMOVEDDATE >= '"+strNowTimestamp+"' ) AND ISREMOVE = 'N' AND CUSTOMERID = '"+value+"' AND ( CUSTOMERNAME='"+retQuery[0][0].trim()+"' AND BIRTHDAY='"+BirthDay+"' )";
          String ret171List[][] = db400.queryFromPool(str400sql);
          if(ret171List.length > 0) {
            errMsg += "�N�z�H" + retQuery[0][0].trim() + "�B�a�x�����Φ��K�����Y���H�A�����n�F�v��¾�ȤH�h�A�Х[�j�Ȥ��¾�լd�A�è̬~���θꮣ����@�~��z�C\n";
          }
          
          /*
          // �ܴ�Start
          String birth = " ";
          String Ind = " ";
          setValue("AMLText" , value + "," + retQuery[0][0].trim() + "," + birth + "," + Ind + "," + "query1821");
          getButton("BtCustAML").doClick();
          errMsg += getValue("AMLText").trim();
          // �ܴ�END
          */
          
          //�¦W��&���ަW��
          if("Y".equals(retQuery[0][1].trim()) || "Y".equals(retQuery[0][2].trim())){
            errMsg += "�N�z�H" + retQuery[0][0].trim() + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
          }
          if("Y".equals(retQuery[0][3].trim())){//�Q���H
            errMsg += "�N�z�H" + retQuery[0][0].trim() + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
          }
          if(!"".equals(errMsg)){
            messagebox(errMsg);
          }
      } else {
          setValueAt("table10", "", s_row,"AgentName");
          setValueAt("table10", "", s_row,"IsBlackList");
          setValueAt("table10", "", s_row,"IsControlList");
          setValueAt("table10", "", s_row,"IsLinked");
          message("�L�����N�z�H��T�C") ;
      }
    }
     return true;
  }
  public String getInformation(){
    return "---------------null(null).ACustomNo.field_check()----------------";
  }
}
