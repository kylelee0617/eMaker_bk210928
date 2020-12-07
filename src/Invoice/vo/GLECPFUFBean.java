package Invoice.vo;

/**
 * 檔案名稱:  GLECPFUF (INVOM041)
 * 說　　明: 電子發票AS400總帳－折讓明細(INVOM041)上載              
 * 
 * @author B04391
 *
 */

public class GLECPFUFBean {
  private String EC01U = ""; //折讓單號碼       
  private String EC02U = ""; //筆數    
  private String EC03U = ""; //勾選    
  private String EC04U = ""; //發票號碼      
  private String EC05U = ""; //摘要代碼      
  private String EC06U = "0"; //發票未稅金額        
  private String EC07U = "0"; //發票稅額      
  private String EC08U = "0"; //發票總金額       
  private String EC09U = "0"; //已折讓金額       
  private String EC10U = "0"; //折讓金額
  
  public String getEC01U() {
    return EC01U;
  }
  public void setEC01U(String eC01U) {
    EC01U = eC01U;
  }
  public String getEC02U() {
    return EC02U;
  }
  public void setEC02U(String eC02U) {
    EC02U = eC02U;
  }
  public String getEC03U() {
    return EC03U;
  }
  public void setEC03U(String eC03U) {
    EC03U = eC03U;
  }
  public String getEC04U() {
    return EC04U;
  }
  public void setEC04U(String eC04U) {
    EC04U = eC04U;
  }
  public String getEC05U() {
    return EC05U;
  }
  public void setEC05U(String eC05U) {
    EC05U = eC05U;
  }
  public String getEC06U() {
    return EC06U;
  }
  public void setEC06U(String eC06U) {
    EC06U = eC06U;
  }
  public String getEC07U() {
    return EC07U;
  }
  public void setEC07U(String eC07U) {
    EC07U = eC07U;
  }
  public String getEC08U() {
    return EC08U;
  }
  public void setEC08U(String eC08U) {
    EC08U = eC08U;
  }
  public String getEC09U() {
    return EC09U;
  }
  public void setEC09U(String eC09U) {
    EC09U = eC09U;
  }
  public String getEC10U() {
    return EC10U;
  }
  public void setEC10U(String eC10U) {
    EC10U = eC10U;
  }
  
}
