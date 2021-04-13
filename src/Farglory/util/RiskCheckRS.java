package Farglory.util;

import java.util.List;

import com.fglife.soap.cr.MainReply;

/**
 * 計算風險值結果回覆
 * 
 * @author B04391
 *
 */

public class RiskCheckRS {
  private String rsMsg = "";          //風險值結果訊息
  private List sendMailList = null;   //寄發EMAIL資訊
  
  public String getRsMsg() {
    return rsMsg;
  }
  public void setRsMsg(String rsMsg) {
    this.rsMsg = rsMsg;
  }
  public List getSendMailList() {
    return sendMailList;
  }
  public void setSendMailList(List sendMailList) {
    this.sendMailList = sendMailList;
  }
}
