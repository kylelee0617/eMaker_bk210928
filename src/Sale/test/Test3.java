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
   
    //config
    ResourceBundle resource = ResourceBundle.getBundle("configK");
    String serverType = resource.getString("serverType").trim();
    String lyodsSoapURL = resource.getString("lyodsSoapURL").trim();
    

    return value;
  }

  public String getInformation() {
    return "---------------test111(test111).defaultValue()----------------";
  }
}
