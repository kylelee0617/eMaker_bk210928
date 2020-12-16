package Sale.test;
import java.text.DecimalFormat;

import Farglory.util.KUtils;
import jcx.jform.bproc;

public class test2 extends bproc{
  
  KUtils kUtil = new KUtils();

  public String getDefaultValue(String value)throws Throwable{
    
    DecimalFormat df = new DecimalFormat("#,###");
    String aa = df.format(1234567890);
    System.out.println("aa>>>" + aa);
  
    return value;
  }
  public String getInformation(){
    return "---------------test111(test111).defaultValue()----------------";
  }
}
