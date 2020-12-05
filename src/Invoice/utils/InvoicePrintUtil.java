package Invoice.utils;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import Invoice.vo.InvoicePrintVo;

public class InvoicePrintUtil{
  protected Call call = null;

  public InvoicePrintUtil() {
  }

  public InvoicePrintUtil(String url) throws ServiceException {
    System.out.println("===================Start to init InvoicePrintUtil====================");
    
    Service printService = new Service();
    call = (Call) printService.createCall();
    call.setTargetEndpointAddress(url + "/InvoicePrintServiceImpl?wsdl"); // invoiceEndpoint
    call.setOperationName("print");
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "sendPost"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN); // 寄件人郵遞區號
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "sendAddr"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN); // 寄件人地址
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "sendCompany"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 寄件人公司
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "sendName"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 寄件人名稱
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "recipientPost"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 收件人郵遞區號
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "recipientAddr"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 收件人地址
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "recipientCompany"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 收件人公司
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "recipientName"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 收件人姓名
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "title"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 發票抬頭
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "invoiceDate"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 發票產生日期
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "invoiceNumber"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 發票編號
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "printDate"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 發票列印日期
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "randomCode"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 隨機碼
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "total"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 總計
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "buyerId"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 買方
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "sellerId"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 賣方
    // call.addParameter(new QName("http://impl.service.invoice.fglife.com", "barcode"),
    // org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 條碼
    // call.addParameter(new QName("http://impl.service.invoice.fglife.com", "qrcode1"),
    // org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// QR CODE1
    // call.addParameter(new QName("http://impl.service.invoice.fglife.com", "qrcode2"),
    // org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// QR CODE2
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "mark1"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 發票下方註記1
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "mark2"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 發票下方註記 2
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "detail"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 明細細項以,作為分隔欄位 ;號分隔筆數
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "printCount"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 列印次數;大於1次會變補印
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "deptId"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 列印者所屬單位
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "saleAmount"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 銷售額
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "buyerName"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 買受人
    call.addParameter(new QName("http://impl.service.invoice.fglife.com", "tax"),
        org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 稅額
    call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
    
    System.out.println("===================init InvoicePrintUtil Complete====================");
  }
  
  public String doPrint(InvoicePrintVo vo) {
    String title = vo.getTitle();
    String sendPost = vo.getSendPost();
    String sendAddr = vo.getSendAddr();
    String sendCompany = vo.getSendCompany();
    String sendName = vo.getSendName();
    String recipientPost = vo.getRecipientPost();
    String recipientAddr = vo.getRecipientAddr();
    String recipientCompany = vo.getRecipientCompany();
    String recipientName = vo.getRecipientName();
    String invoiceDate = vo.getInvoiceDate();
    String invoiceNumber = vo.getInvoiceNumber();
    String printDate = vo.getPrintDate();
    String randomCode = vo.getRandomCode();
    String saleAmount = vo.getSaleAmount();
    String total = vo.getTotal();
    String buyerId = vo.getBuyerId();
    String buyerName = vo.getBuyerName();
    String sellerId = vo.getSellerId();
    String mark1 = vo.getMark1();
    String mark2 = vo.getMark2();
    String detail = vo.getDetail();
    String printCount = vo.getPrintCount();
    String deptId = vo.getDeptId();
    String tax = vo.getTax();
    
    String result = "";
    try {
      result = (String) call.invoke(new Object[] { sendPost, sendAddr, sendCompany, sendName, recipientPost,
          recipientAddr, recipientCompany, recipientName, title, invoiceDate, invoiceNumber, printDate, randomCode,
          total, buyerId, sellerId, mark1, mark2, detail, printCount, deptId, saleAmount, buyerName, tax });
      
      System.out.println(call.getMessageContext().getRequestMessage().getSOAPPartAsString());
    }catch(Exception ex) {
      result = ex.toString();
    }
    
    return result;
  }
  
}
