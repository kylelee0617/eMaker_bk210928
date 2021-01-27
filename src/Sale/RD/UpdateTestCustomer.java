package Sale.RD;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import java.text.*;

public class UpdateTestCustomer extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    
    String[] namelist = {
        "英業達;04322046"
        ,"台積電;22099131"
        ,"統一企業;73251209"
        ,"光陽G車;00160202"
        ,"三陽雞車;00166402"
        ,"雅哈哈實業;00168529"
        ,"卡哇沙奇重車;00172002"
        ,"沙士給那魯托;00181804"
        ,"江Ｏ貞;F221371897"
        ,"洪Ｏ益;A120831833"
        ,"黃Ｏ霏;E123606925"
        ,"潘青煌;U121153808"
        ,"超黑王文君;F220492173"
        ,"高路哥哥;Q223996005"
        ,"廖Ｏ弘;F199927685"
        ,"周Ｏ萩;R222761429"
        ,"陳小春;F126393588"
        ,"許戈;K05719268"
        ,"何漢賢;KJ0351263"
        ,"葉志明;K02898965"
        ,"羅雲霏;K02892375"
        ,"鄧炳成;FC01389035"
        ,"蔡英文;B274089637"};
    
    StringBuilder inIdList = new StringBuilder();
    StringBuilder inNameList = new StringBuilder();
    for(int ii=0 ; ii<namelist.length ; ii++) {
      String[] nameID = namelist[ii].split(";");
      if(ii > 0) inNameList.append(",");
      inIdList.append("'").append(nameID[1]).append("'");
      inNameList.append("'").append(nameID[0]).append("'");
    }

    String serverIP = get("serverIP").toString().trim();
    System.out.println("serverIP>>>" + serverIP);
    if ( serverIP.contains("172.16.") ) {
      System.out.println(">>>正式環境<<<"); 
      return value;
    }

    talk dbSale =  getTalk("pw0d") ;
    String projectIds = getValue("ProjectIDs").trim();
    if ( "".equals(projectIds) ) {
      message("輸入案別");
      return value;
    }

    SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddhhmmss");

    Vector vectorSql = new  Vector( ) ;
    String sql = "delete from query_log where project_id in ('" + projectIds.replaceAll("," , "','") + "') "
        + "and ( query_id in (" + inIdList.toString() + ") or NAME in (" + inNameList.toString() + ") ) ";
    vectorSql.add(sql) ;

    
    String[] spProjectids = projectIds.split(",");
    for (int i=0 ; i < spProjectids.length ; i++) {
      String thisPID = spProjectids[i].trim();
      Date nowDate= new Date();
      String qidL = sdFormat.format(nowDate) ;
      System.out.println(">>>qidL>>>" + qidL);

      //法人
      sql = "INSERT INTO query_log VALUES ( '"+ qidL + Integer.toString(i) +  "00'  ,'91','" + thisPID + "','L','1','TWN','英業達','04322046','1985/05/15','M','44',1,9,' ','Y','N','Y','Y',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','台積電','22099131','1987/02/21','M','44',1,9,' ','N','N','N','N',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','統一企業','73251209','1967/08/25','M','44',1,9,' ','N','N','N','N',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','光陽G車','00160202','1985/05/15','M','44',1,9,' ','Y','Y','Y','Y',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','三陽雞車','00166402','1985/05/15','M','44',1,9,' ','N','N','N','N',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','雅哈哈實業','00168529','1985/05/15','M','44',1,9,' ','N','N','N','N',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','卡哇沙奇重車','00172002','1985/05/15','M','44',1,9,' ','N','N','N','N',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','沙士給那魯托','00181804','1985/05/15','M','44',1,9,' ','N','N','N','N',' NONE ',' ','B03918','172.16.17.183','2019/04/29','10:50:54','','')";
      vectorSql.add(sql) ;

      //PEPS
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','江Ｏ貞 ','F221371897','1963/01/28','F','44',3,40,' ','N','Y','N','N','NONEHTML','','B03621','172.16.8.144','2019/04/26','17:48:17','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','洪Ｏ益','A120831833','1970/11/03','M','44',3,39,' ','N','Y','N','N','NONE HTML','','B03901','172.16.8.144','2019/06/23','18:00:54','','')";
      vectorSql.add(sql) ;

      //制裁
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','黃Ｏ霏','E123606925','1989/12/22','M','18',3,39,' ','N','Y','N','N','NONE HTML','','B03967','172.16.8.144','2019/05/29','19:23:10','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','潘青煌','U121153808','1978/11/13','M','21',3,35,' ','N','Y','N','N','NONE HTML','','B03967','172.16.8.144','2019/06/21','12:49:58','','')";
      vectorSql.add(sql) ;

      //定審
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','超黑王文君','F220492173','1989/12/22','M','44',18,277,' ','Y','Y','Y','Y','NONE HTML','','B03554','172.16.8.144','2019/04/26','17:48:59','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','高路哥哥','Q223996005','1990/12/20','F','44',3,40,' ','Y','Y','Y','Y','  ','','B04056','172.16.8.144','2019/04/25','10:08:39','','')";
      vectorSql.add(sql) ;

      //雜魚
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','廖Ｏ弘','F199927685','1985/05/15','M','44',1,9,' ','N','N','N','N','  ' , '  ' ,'B03918','172.16.17.183','2019/04/29','10:51:11','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','周Ｏ萩','R222761429','1982/05/25','F','44',3,45,' ','N','N','N','N','  ','','B03614','172.16.8.144','2019/04/29','16:44:49','','')";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','陳小春','F126393588','1918/01/01','M','44',1,9,' ','N','N','N','N','  ' , '  ' ,'B03918','172.16.17.183','2019/04/23','15:04:17','','')";
      vectorSql.add(sql) ;

      //歪國人
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc) ,'91','" + thisPID + "','F','1','HKG','許戈','K05719268','1967/01/07','F','51',57,3,'康愉街43號花園M座605室','N','Y','Y','N','',null,'B03927','172.16.8.144','2019/12/14','15:31:26',null,null)";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc) ,'91','" + thisPID + "','F','1','HKG','何漢賢','KJ0351263','1966/05/07','M','12',57,1,'康愉街43號花園M座605室','N','Y','N','N','  ',null,'B03927','172.16.8.144','2019/12/14','15:36:32',null,null)";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc) ,'91','" + thisPID + "','F','1','HKG','葉志明','K02898965','1964/03/07','M','99',57,1,'九龍順安村安頌樓1347室','Y','N','N','N','  ',null,'B03927','172.16.8.144','2019/12/14','15:39:32',null,null)";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc) ,'91','" + thisPID + "','F','1','HKG','羅雲霏','K02892375','1966/10/20','F','99',57,1,'香港九龍順安村安頌樓1347室','N','N','N','N','  ',null,'B03927','172.16.8.144','2019/12/14','15:42:40',null,null)";
      vectorSql.add(sql) ;
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc) ,'91','" + thisPID + "','F','1','MYS','鄧炳成','FC01389035','1965/01/21','M','18',1,11,'行義路5巷7號3樓','N','N','N','N','  ',null,'B03593','172.16.8.144','2020/06/14','12:15:55',null,null)";
      vectorSql.add(sql) ;

      //極端
      sql = "INSERT INTO query_log VALUES ( (select top 1 CONVERT(VARCHAR , CAST(QID AS decimal(17,0))+1) from query_log order by CAST(QID AS decimal(17,0)) desc)  ,'91','" + thisPID + "','L','1','TWN','蔡英文','B274089637','1956/08/31','F','43',1,9,' ','Y','Y','Y','Y','  ','','B03918','172.16.17.183','2019/04/29','11:21:47','','')";
      vectorSql.add(sql) ;
    }

    if(vectorSql.size()  >  0) {
      dbSale.execFromPool((String[])  vectorSql.toArray(new  String[0])) ;
    }

    message("完成");
    return value;
  }
  public String getInformation(){
    return "---------------UpdateTestCustomer(\u628a\u90a3\u4e9b\u4eba\u7d66\u6211\u585e\u9032\u53bb!).defaultValue()----------------";
  }
}
