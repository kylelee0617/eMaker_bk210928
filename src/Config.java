import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import java.net.*;

public class Config extends jcx.jform.sproc {
  public String getDefaultValue(String value) throws Throwable {
    StringBuilder showConfig = new StringBuilder();
    // 伺服器資訊
    InetAddress addr = InetAddress.getLocalHost();
    String ip = addr.getHostAddress().toString(); // 獲得本機IP
    String address = addr.getHostName().toString(); // 獲得本機名稱
    put("serverIP", ip);
    System.out.println("addr=:" + String.valueOf(addr));
    System.out.println("ip:" + ip + "/ name:" + address);
    showConfig.append("ip:").append(ip).append("\n");
    showConfig.append("name:").append(address).append("\n");
    
    // 設定檔
    Map configMap = new HashMap();
    ResourceBundle resource = ResourceBundle.getBundle("configK");
    String GENLIB = resource.getString("AS400.GENLIB");
    showConfig.append("GENLIB:").append(GENLIB).append("\n");
    configMap.put("GENLIB", GENLIB);
    
    String PRINTURL = resource.getString("SYSTEM.PRINTURL");
    System.out.println("PRINTURL>>>" + PRINTURL);
    showConfig.append("PRINTURL:").append(PRINTURL).append("\n");
    configMap.put("PRINTURL", PRINTURL);
    put("config", configMap);
    setValue("config", showConfig.toString());
    return value;
  }
}