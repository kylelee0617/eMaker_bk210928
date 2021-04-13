package Farglory.util;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import java.net.*;

public class DefaultInfo extends jcx.jform.sproc{

  public String getDefaultValue(String value)throws Throwable{
    StringBuilder showConfig = new StringBuilder();
    
    InetAddress addr = InetAddress.getLocalHost();
    String serverIP=addr.getHostAddress().toString();     //��o����IP
    String serverName=addr.getHostName().toString();    //��o�����W��
    put("serverName" , serverName);
    showConfig.append("ip:").append(serverIP).append("\n");
    put("serverIP" , serverIP);
    showConfig.append("name:").append(serverName).append("\n");
    
    String serverType = serverIP.contains("172.16.")? "PROD" : "SIT";
    put("serverType" ,  serverType);
    showConfig.append("serverType:").append(serverType).append("\n");
    
    //=============================== Map �� ==================================
    Map configMap = new HashMap();
    ResourceBundle resource = ResourceBundle.getBundle("configK");
    
    //GENLIB for AS400
    String GENLIB = resource.getString("AS400.GENLIB");
    configMap.put("GENLIB" , GENLIB);
    showConfig.append("GENLIB:").append(GENLIB).append("\n");
    
    //�ܴ��D�����|
    String lyodsSoapURL = resource.getString("lyodsSoapURL");
    configMap.put("lyodsSoapURL" , lyodsSoapURL);
    showConfig.append("lyodsSoapURL:").append(lyodsSoapURL).append("\n");
    
    //EMAKER�D������ : ���� �� ����
    configMap.put("serverIP" , serverIP);
    configMap.put("serverName" , serverName);
    configMap.put("serverType" , serverType);
    put("config" , configMap);
    
    setValue("config" , showConfig.toString());
    
    return value;
  }
  public String getInformation(){
    return "---------------default(default).defaultValue()----------------";
  }
}
