package Invoice.vo;

/**
 * 發票列印對應檔
 * @author B04391
 *
 */

public class InvoicePrintVo {
  private String title = "遠雄人壽";                            // 發票抬頭
  
  private String sendName = "";                                 // 寄件人名稱
  private String sendPost = "11073";                            // 寄件人郵遞區號
  private String sendAddr = "台北市松高路1號28樓";              // 寄件人地址
  private String sendCompany = "遠雄人壽保險事業股份有限公司";  // 寄件人公司
  private int DETAIL_LENGTH = 4;                                // 明細行數

  //收件人
  private String recipientPost = "";        // 收件人郵遞區號
  private String recipientAddr = "";        // 收件人地址
  private String recipientCompany = "";     // 收件人公司
  private String recipientName = "";        // 收件人姓名

  //品項、金額
  private String invoiceDate = "";          // 發票產生日期(yyyyMMdd)
  private String invoiceNumber = "";        // 發票編號
  private String printDate = "";            // 發票列印日期(yyyyMMddhhmmss)
  private String randomCode = "";           // 隨機碼
  private String saleAmount = "";           // 銷售額
  private String total = "";                // 總計
  private String buyerId = "";              // 買方
  private String sellerId = "";             // 賣方
  private String mark1 = "";                // 發票下方註記1
  private String mark2 = "";                // 發票下方註記 2
  private String detail = "";               // 明細細項以,作為分隔欄位 ;號分隔筆數
  private String printCount = "1";          // 列印次數;大於1次會變補印
  private String deptId = "2200";           // 列印者所屬單位
  private String buyerName = "";            // 買受人
  private String tax = "";                  // 稅額
  
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getSendPost() {
    return sendPost;
  }
  public void setSendPost(String sendPost) {
    this.sendPost = sendPost;
  }
  public String getSendAddr() {
    return sendAddr;
  }
  public void setSendAddr(String sendAddr) {
    this.sendAddr = sendAddr;
  }
  public String getSendCompany() {
    return sendCompany;
  }
  public void setSendCompany(String sendCompany) {
    this.sendCompany = sendCompany;
  }
  public int getDETAIL_LENGTH() {
    return DETAIL_LENGTH;
  }
  public void setDETAIL_LENGTH(int dETAIL_LENGTH) {
    DETAIL_LENGTH = dETAIL_LENGTH;
  }
  public String getSendName() {
    return sendName;
  }
  public void setSendName(String sendName) {
    this.sendName = sendName;
  }
  public String getRecipientPost() {
    return recipientPost;
  }
  public void setRecipientPost(String recipientPost) {
    this.recipientPost = recipientPost;
  }
  public String getRecipientAddr() {
    return recipientAddr;
  }
  public void setRecipientAddr(String recipientAddr) {
    this.recipientAddr = recipientAddr;
  }
  public String getRecipientCompany() {
    return recipientCompany;
  }
  public void setRecipientCompany(String recipientCompany) {
    this.recipientCompany = recipientCompany;
  }
  public String getRecipientName() {
    return recipientName;
  }
  public void setRecipientName(String recipientName) {
    this.recipientName = recipientName;
  }
  public String getInvoiceDate() {
    return invoiceDate;
  }
  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }
  public String getInvoiceNumber() {
    return invoiceNumber;
  }
  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }
  public String getPrintDate() {
    return printDate;
  }
  public void setPrintDate(String printDate) {
    this.printDate = printDate;
  }
  public String getRandomCode() {
    return randomCode;
  }
  public void setRandomCode(String randomCode) {
    this.randomCode = randomCode;
  }
  public String getSaleAmount() {
    return saleAmount;
  }
  public void setSaleAmount(String saleAmount) {
    this.saleAmount = saleAmount;
  }
  public String getTotal() {
    return total;
  }
  public void setTotal(String total) {
    this.total = total;
  }
  public String getBuyerId() {
    return buyerId;
  }
  public void setBuyerId(String buyerId) {
    this.buyerId = buyerId;
  }
  public String getSellerId() {
    return sellerId;
  }
  public void setSellerId(String sellerId) {
    this.sellerId = sellerId;
  }
  public String getMark1() {
    return mark1;
  }
  public void setMark1(String mark1) {
    this.mark1 = mark1;
  }
  public String getMark2() {
    return mark2;
  }
  public void setMark2(String mark2) {
    this.mark2 = mark2;
  }
  public String getDetail() {
    return detail;
  }
  public void setDetail(String detail) {
    this.detail = detail;
  }
  public String getPrintCount() {
    return printCount;
  }
  public void setPrintCount(String printCount) {
    this.printCount = printCount;
  }
  public String getDeptId() {
    return deptId;
  }
  public void setDeptId(String deptId) {
    this.deptId = deptId;
  }
  public String getBuyerName() {
    return buyerName;
  }
  public void setBuyerName(String buyerName) {
    this.buyerName = buyerName;
  }
  public String getTax() {
    return tax;
  }
  public void setTax(String tax) {
    this.tax = tax;
  }
  
}
