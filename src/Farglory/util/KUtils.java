/**
 * 2020-02-04 kyle的共用元件
 */
package Farglory.util;

import jcx.jform.bvalidate;
import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.table.*;
import jcx.util.*;
import jcx.net.smtp;
import jcx.db.talk;
import javax.mail.MessagingException;
import javax.print.*;
// 剪貼簿
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.*;

public class KUtils extends bvalidate {

  // 是數字回傳 true，否則回傳 false。
  public boolean check(String value) throws Throwable {
    return false;
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
    calendar.setTimeInMillis(time + days * 1000 * 60 * 60 * 24);// 用给定的 long值设置此Calendar的当前时间值
    String newYear = Integer.toString(calendar.get(Calendar.YEAR)); 
    String newMonth = (calendar.get(Calendar.MONTH) + 1) < 10? "0"+(calendar.get(Calendar.MONTH) + 1) : Integer.toString((calendar.get(Calendar.MONTH) + 1));
    String newDay = calendar.get(Calendar.DAY_OF_MONTH) < 10? "0" + calendar.get(Calendar.DAY_OF_MONTH) : Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
    
    return newYear + "/" + newMonth + "/" + newDay;
  }
  
  /**
   * String[]中 gen出符合SQL IN ()  的語法
   * @param srcStr
   * @return
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
