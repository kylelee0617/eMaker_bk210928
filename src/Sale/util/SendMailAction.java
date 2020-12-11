package Sale.util;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.util.Result;

public class SendMailAction extends bproc{

   talk dbSale = getTalk("Sale");
   talk dbEIP = getTalk("EIP");
   talk dbEMail = getTalk("eMail");
   String stringSQL = "";
   String userNo = getUser().toUpperCase().trim();
   String empNo="";
   String userEmail = "";
   String userEmail2 = "";
   String DPCode="";
   String DPManageemNo="";
   String DPeMail="";
   String DPeMail2="";
   String [][] retEip=null;
  String [][] reteMail = null;
   String PNMail="";
   String testRemark = "(測試)";                                  //在測試環境要加註測試字樣
   String testPGMail = "Kyle_Lee@fglife.com.tw";    //測試環境寄送測試mail

   //畫面值
   String strProjectID1 =  "" ;   //案別代碼
   String strOrderDate = "" ;   //付訂日期
   String strOrderNo = "" ;      //訂單編號
   String strPosition = "";
   String strCustomName = "";
   String errMsgText = "";

   public SendMailAction() {
      //20200508 kyle Add  根據伺服器是否加測試訊息
      String serverIP = get("serverIP").toString().trim();
      System.out.println("serverIP>>>" + serverIP);

    if ( serverIP.contains("172.16.14.4") ) {
      testRemark = "";
      testPGMail = "";
      System.out.println(">>>正式環境<<<"); 
    }
   }

   public String getDefaultValue(String value) throws Throwable{

    System.out.println("==============洗錢防治檢核SENDMAIL STAR====================================") ;

        this.getSendUser();
        this.getFormData();

    //20200511 - kyle mod : 藉由不同傳入value執行不同mail動作，結束後輸出至result，vlaue還原為預設值
    if ( "rReview".equals(value) ) {
      //定審用
      System.out.println(">>>send email kk");
      String[] biRs = getValue("BuyedResult").trim().split(",");
      Result rs = this.sendRreviewMail( biRs[1] );
      if ( rs.getReturnCode() != 0) {
        messagebox( rs.getReturnMsg() );
      }
      System.out.println( ">>>mail RS:" + rs.getReturnMsg() );
    } else {
      //send email
      System.out.println(">>>send email old");
      //制裁名單
      if(errMsgText.indexOf("制裁名單") >=0 ){
        String msg2 ="一、不動產交易資訊：<BR><BR>1. 案    別：<u>"+strProjectID1+"</u>&emsp;2. 棟樓別：<u>"+strPosition+"</u>&emsp;3. 客戶姓名：<u>"+strCustomName+"</u>&emsp;4. 付訂日期：<u>"+strOrderDate+"</u>&emsp;5. 購屋証明單日期：<u>"+strOrderDate+"</u><BR><BR>二、符合疑似洗錢態樣通知：<BR><BR>客戶"+strCustomName+"為控管之制裁名單對象，請禁止交易，並依洗錢防制內部通報作業送呈法遵室。";
        msg2 = msg2.replace("\n","<BR>");
        
        String subject2 = strProjectID1+"案"+strPosition+"不動產交易符合疑似洗錢或資恐態樣系統通知" + testRemark;
        String[] arrayUser2 = {"Justin_Lin@fglife.com.tw",userEmail,DPeMail,PNMail};
        String  sendRS2 = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser2, subject2, msg2, null,  "", "text/html");
        
        System.out.println("sendRS2===>"+sendRS2);
      }else{
        String msg ="一、不動產交易資訊：<BR><BR>1. 案    別：<u>"+strProjectID1+"</u>&emsp;2. 棟樓別：<u>"+strPosition+"</u>&emsp;3. 客戶姓名：<u>"+strCustomName+"</u>&emsp;4. 付訂日期：<u>"+strOrderDate+"</u>&emsp;5. 購屋証明單日期：<u>"+strOrderDate+"</u><BR><BR>二、符合疑似洗錢態樣通知：<BR><BR>"+ errMsgText;
        msg = msg.replace("\n","<BR>");
        String subject =  strProjectID1+"案"+strPosition+"不動產交易符合疑似洗錢或資恐態樣系統通知" + testRemark;
        String[] arrayUser = {"Justin_Lin@fglife.com.tw",userEmail};
        String  sendRS = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser, subject, msg, null,  "", "text/html");
        
        System.out.println("sendRS===>"+sendRS);
      }
    }
    
    System.out.println("==============洗錢防治檢核SENDMAIL END====================================") ;
    
    return "sendMail" ;
  }

  /*
   * 20200507 kyle add
   * msg = 訊息內容
   */
   public Result sendRreviewMail(String msg) throws Throwable{
      Result rs = new Result();

      try{
         String msg2 ="案別代碼："+strProjectID1+"<BR>棟樓別："+strPosition+"<BR>訂戶姓名："+strCustomName+"<BR>付訂日期："+strOrderDate+"<BR>警示訊息 : " + msg.replaceAll("\n" , "<BR>");
         String subject2 = "購屋證明單  案別："+strProjectID1+"  棟樓別："+strPosition+"  客戶未完成定審通知" + testRemark;
         String[] arrayUser2 = {testPGMail,userEmail,DPeMail,PNMail};
         String  sendRS2 = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser2, subject2, msg2, null,  "", "text/html");
         if( "".equals(sendRS2) ) {
       rs.setReturnMsg("發送Mail成功");
     } else {
       rs.setReturnCode(93);   
       rs.setReturnMsg(sendRS2);
     }
      } catch (Exception ex){
        rs.setReturnCode(92);
    rs.setReturnMsg("發送Mail錯誤!!");
        rs.setExp(ex);
      }
      
      return rs;
   }

   public void getSendUser() throws Throwable {
    //承辦ID
    stringSQL="SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'" ;
    retEip = dbEIP.queryFromPool(stringSQL);
    if(retEip.length>0){
      empNo=retEip[0][0] ;
    }

    //承辦EMAIL
    stringSQL="SELECT DP_CODE,PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'" ;
    reteMail = dbEMail.queryFromPool(stringSQL);
    if(reteMail.length>0){
      DPCode=reteMail[0][0] ;
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        userEmail= reteMail[0][1] ;
      }
      if ( reteMail[0][2] != null && ! reteMail[0][2].equals("")) {
        userEmail2= reteMail[0][2] ;
      } 
    }

    //科長ID
    stringSQL="SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + DPCode + "'" ;
    reteMail = dbEMail.queryFromPool(stringSQL);
    if(reteMail.length>0){
      DPManageemNo=reteMail[0][0] ;
    }

    //科長MAIL
    stringSQL="SELECT PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + DPManageemNo + "'" ;
    reteMail = dbEMail.queryFromPool(stringSQL);
    if(reteMail.length>0){
      if (reteMail[0][0] != null && !reteMail[0][0].equals("")) {
        DPeMail= reteMail[0][0] ;
      }
      if ( reteMail[0][1] != null && ! reteMail[0][1].equals("")) {
        DPeMail2= reteMail[0][1] ;
      }
    }
    
      //部長
    String PNCode="";
    String PNManageemNo="";

    stringSQL="SELECT PN_DEPTCODE FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'" ;
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNCode=reteMail[0][0] ;

    stringSQL="SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + PNCode + "'" ;
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNManageemNo=reteMail[0][0] ;

    stringSQL="SELECT PN_EMAIL1 FROM PERSONNEL WHERE PN_EMPNO='" + PNManageemNo + "'" ;
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNMail= reteMail[0][0] ;
   }

   public void getFormData() throws Throwable {
    //取畫面值
    strProjectID1 =  getValue("field1").trim() ;   //案別代碼
    strOrderDate =  getValue("field2").trim() ;   //付訂日期
    strOrderNo =  getValue("field3").trim() ;      //訂單編號
    errMsgText =getValue("errMsgBoxText").trim();

    strPosition = getTableData("table2")[0][3].toString().trim();
    strCustomName = getTableData("table1")[0][6].toString().trim();
   }

  public String getInformation(){
    return "---------------emailTestBtn(emailTestBtn).defaultValue()----------------";
  }
}
