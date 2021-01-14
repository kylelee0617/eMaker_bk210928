package Farglory.util;

import jcx.db.talk;

/**
 * 計算風險值 實質受益人物件
 * @author B04391
 *
 */

public class RiskBenBean {
  private String customNo = "";
  private String bCustomNo = "";
  private String benName = "";
  private String birthday = "";
  private String holdType = "";
  private String countryName = "";
  private String statusCd = "";
  
  public String getCustomNo() {
    return customNo;
  }
  public void setCustomNo(String customNo) {
    this.customNo = customNo;
  }
  public String getbCustomNo() {
    return bCustomNo;
  }
  public void setbCustomNo(String bCustomNo) {
    this.bCustomNo = bCustomNo;
  }
  public String getBenName() {
    return benName;
  }
  public void setBenName(String benName) {
    this.benName = benName;
  }
  public String getBirthday() {
    return birthday;
  }
  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }
  public String getHoldType() {
    return holdType;
  }
  public void setHoldType(String holdType) {
    this.holdType = holdType;
  }
  public String getCountryName() {
    return countryName;
  }
  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }
  public String getStatusCd() {
    return statusCd;
  }
  public void setStatusCd(String statusCd) {
    this.statusCd = statusCd;
  }
  
}
