/**
 * 2020-02-04 kyle的共用元件
 */
package Farglory.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import jcx.jform.bvalidate;

public class KUtils extends bvalidate {

  // 是數字回傳 true，否則回傳 false。
  public boolean check(String value) throws Throwable {
    return false;
  }
  
  
  /**
   * 是否測試服務器
   * 
   * @return
   * @throws Throwable
   */
  public boolean isTest() throws Throwable {
    String serverIP = get("serverIP").toString().trim();
    boolean isTest = serverIP.contains("172.16.")? false:true;
    return isTest;
  }
  
  /**
   * 取得伺服器IP
   * 
   * @return
   * @throws Throwable
   */
  public String getServerIP() throws Throwable {
    String ip = get("serverIP").toString().trim();
    return ip;
  }
  
  /**
   * 字串前後補字元
   * 
   * @param src   原始字串
   * @param count 補到幾位
   * @param ch    要補的字元
   * @param FB    前0，後1
   * @return      String
   * @throws Throwable
   */
  public String addWhat(String src , int count , String ch , int FB) throws Throwable {
    String strPlus = "";
    for(int ii=0 ; ii<count-(src.length()) ; ii++) {
      strPlus += ch;
    }
    
    if( FB == 0 ) {
      return strPlus + src; 
    }else if (FB == 1) {
      return src + strPlus;
    }else {
      return "參數錯誤";
    }
  }
  
  /**
   * 數字前後補0
   * 
   * @param intSrc  原數字
   * @param count   要補到幾位數
   * @param FB      補在前面F，補在後面B
   * @return
   * @throws Throwable
   */
  public String add0(int intSrc , int count , String FB) throws Throwable {
    String strSrc = Integer.toString(intSrc);
    String strPlus = "";
    for(int ii=0 ; ii<count-(strSrc.length()) ; ii++) {
      strPlus += "0";
    }
    
    if( "F".equals(FB) ) {
      return strPlus + strSrc; 
    }else {
      return strSrc + strPlus;
    }
    
  }
  
  
  /**
   * 兩日期相減，負號為後面日期較大
   * 格式 yyyy/MM/dd
   * 
   * @param day1
   * @param day2
   * @return (day1 - day2)
   * @throws Throwable
   */
  public long subACDaysRDay(String day1 , String day2) throws Throwable {
    if ( "".equals(day1) || "".equals(day2)) {
      return 0;
    }
    if ( day1.length() != 10 || day2.length() != 10) {
      return 0;
    }
    
    Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(day1);
    Date date2 = new SimpleDateFormat("yyyy/MM/dd").parse(day2);
    
//    System.out.println("day1>>>" + day1);
//    System.out.println("day2>>>" + day2);
//    System.out.println("Date1>>>" + date1);
//    System.out.println("Date2>>>" + date2);
//    System.out.println("time>>>" + (date1.getTime()-date2.getTime()) );
	
    //日期相減得到相差的日期
    long day = (date1.getTime()-date2.getTime()) / (24*60*60*1000);
    
    return day;
  }
  
  
  /**
   * format 日期格式  yyyyMMdd to yyyy(dash)MM(dash)dd
   * 
   * @param day
   * @param dash
   * @return
   * @throws Throwable
   */
  public String formatACDate(String day , String dash) throws Throwable {
    if (day == null || "".equals(day)) {
      return "傳入日期為空";
    }
    if (day.length() != 8) {
      return "不為AC格式";
    }
    return day.substring(0, 4) + dash + day.substring(4, 6) + dash + day.substring(6, 8);
  }

  public String formatACDate(String day) throws Throwable {
    if (day == null || "".equals(day)) {
      return "傳入日期為空";
    }
    if (day.length() != 8) {
      return "不為AC格式";
    }
    return day.substring(0, 4) + '/' + day.substring(4, 6) + '/' + day.substring(6, 8);
  }

  /**
   * 取得指定日期+-N天之日期
   * @param dateTime 開始日期
   * @param dash  連接符號 / or -
   * @param days  往前N天，往後-N天
   * @return  結果日期
   */
  public String getDateAfterNDays(String dateTime, String dash , int days) {
    Calendar calendar = Calendar.getInstance();
    String[] dateTimeArray = dateTime.split(dash);
    int year = Integer.parseInt(dateTimeArray[0]);
    int month = Integer.parseInt(dateTimeArray[1]);
    int day = Integer.parseInt(dateTimeArray[2]);
    calendar.set(year, month - 1, day);
    long time = calendar.getTimeInMillis();
    calendar.setTimeInMillis(time + days * 1000 * 60 * 60 * 24);// 用\u7ed9定的 long值\u8bbe置此Calendar的\u5f53前\u65f6\u95f4值
    String newYear = Integer.toString(calendar.get(Calendar.YEAR)); 
    String newMonth = (calendar.get(Calendar.MONTH) + 1) < 10? "0"+(calendar.get(Calendar.MONTH) + 1) : Integer.toString((calendar.get(Calendar.MONTH) + 1));
    String newDay = calendar.get(Calendar.DAY_OF_MONTH) < 10? "0" + calendar.get(Calendar.DAY_OF_MONTH) : Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
    
    return newYear + "/" + newMonth + "/" + newDay;
  }
  
  /**
   * String[]中 gen出符合SQL IN ()  的語法
   * @param srcStr
   * @return '' , '' , '' , '' ....
   * @throws Throwable
   */
  public String genQueryInString(String[] srcStr) throws Throwable {
    StringBuilder sb = new StringBuilder();
    for(int i=0 ; i<srcStr.length ; i++) {
      if(i != 0) {
        sb.append(",");
      }
      sb.append("'").append(srcStr[i].trim()).append("'");
    }
    
    return sb.toString();
  }
  

}
