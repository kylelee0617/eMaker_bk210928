package Invoice.vo;

/**
 * 檔案名稱:  GLEDPFUF (INVOM0C0)
 * 說　　明: 電子發票AS400總帳－客戶(INVOM0C0)上載
 * 
 * @author B04391
 *
 */

public class GLEDPFUFBean {
  private String ED01U = ""; //客戶代號
  private String ED02U = ""; //客戶名稱
  
  public String getED01U() {
    return ED01U;
  }
  public void setED01U(String eD01U) {
    ED01U = eD01U;
  }
  public String getED02U() {
    return ED02U;
  }
  public void setED02U(String eD02U) {
    ED02U = eD02U;
  }
  
}
