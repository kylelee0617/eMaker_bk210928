package Sale.test;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import Farglory.aml.AMLTools_Lyods;
import Farglory.aml.AMLyodsBean;
import Farglory.aml.RiskCustomBean;
import Farglory.util.Result;
import Farglory.util.ResultStatus;

import com.fglife.soap.client.BlickListClient;
import com.fglife.soap.cr.MainQuery;
import com.fglife.soap.cr.MainReply;
import com.fglife.soap.cr.RenewRelatedQuery;
import com.fglife.soap.cr.RenewRelatedReply;

public class Test2 extends jcx.jform.sproc {
  public String getDefaultValue(String value) throws Throwable {
    
    
    return value;
  }

  public String getInformation() {
    return "---------------Test2(Test2).defaultValue()----------------";
  }
}
