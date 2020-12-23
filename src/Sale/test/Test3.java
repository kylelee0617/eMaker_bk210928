package Sale.test;

import Farglory.util.KUtils;
import Farglory.util.RiskCheckBean;
import Farglory.util.RiskCheckTool;
import jcx.db.talk;
import jcx.util.*;
import jcx.html.*;
import java.util.*;
import Farglory.util.*;

public class Test3 extends jcx.jform.sproc {
  KUtils util = new KUtils();

  public String getDefaultValue(String value) throws Throwable {
    System.out.println("test2>>>0");
    System.out.println(datetime.getTime("h:m:s"));

    Map config = util.getProperties();
    String gen1 = config.get("GENLIB").toString().trim();
    String gen2 = util.getProperties().get("GENLIB").toString().trim();
    System.out.println("test31 >>>" + gen1);
    System.out.println("test32 >>>" + gen2);

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
