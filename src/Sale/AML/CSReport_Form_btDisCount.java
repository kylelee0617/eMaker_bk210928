//2020413 Kyle add
package Sale.AML;

import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import cLabel;
import com.jacob.activeX.*;
import com.jacob.com.*;
import Farglory.util.*;

public class CSReport_Form_btDisCount extends bproc {
    
    //DB connetion
    talk dbINVOICE = getTalk("Invoice");
    talk dbSALE = getTalk("Sale");
    
    //公用
    String deptCd = "";
    String deptCd1 = "";
    String objectCd = "";
    String customName = "";
    String queryDate = "";
    String[][] mainData = null;

    //查詢用
    StringBuilder qryPosition = null;
    StringBuilder qryProjectId1 = null;
    String[][] retOrderData = null;


  public String getDefaultValue(String value) throws Throwable {
        deptCd = getValue("deptCd").trim();
        deptCd1 = getValue("deptCd1").trim();
        objectCd = getValue("objectCd").trim();
        customName = getValue("customName").trim();
        queryDate = getValue("QueryDate").trim();
        mainData = this.getMainData();
        System.out.println("mainData Size>>>" + mainData.length );
        
        doExcel();
    return value;
  }

  public void doExcel() throws Throwable {
        List mainList = processMainList(mainData);
        System.out.println("mainList size>>>" + mainList.size() );

        //建立表格
        int startDataRow = 7;
        int endDataRow = mainList.size() + 5;
        Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(startDataRow , endDataRow , endDataRow , 1);
        
        //吃sample檔路徑
        String stringPrintExcel = "G:\\kyleTest\\Excel\\EMK_CSReport\\CSReport_Form_btDisCount.xlt";
        // String stringPrintExcel = "D:\\CSReport_Form_btOrderCancel.xlt";
        System.out.println(stringPrintExcel);
        
        //建立Excel物件
    Vector retVector = exeFun.getExcelObject(stringPrintExcel);
        Dispatch objectSheet1 = (Dispatch) retVector.get(1);
        
        exeFun.putDataIntoExcel(0, 3, "案號:" + deptCd + deptCd1, objectSheet1);
        
        // Start of Body  資料本體
        for(int intRow=0 ; intRow<mainList.size() ; intRow++){
            String[] thisRow = (String[])mainList.get(intRow);
            int recordNo = intRow + exeFun.getStartDataRow();
            
            for( int intCon=0 ;  intCon < 9 ; intCon++ ){
                exeFun.putDataIntoExcel(intCon , recordNo , thisRow[intCon] , objectSheet1);
            }
        }
        // End of Body
        
        exeFun.getReleaseExcelObject(retVector);
        message("輸出報表完成!!");

    return;
  }

    /**
     * 組成主要列表 List
     */
    public List processMainList(String[][] mainData) throws Throwable {
        List mainList = new ArrayList();

        //取得OrderDate集合
        Map mapOrderData = this.getOrderData();

        for(int rowMD=0 ; rowMD<mainData.length ; rowMD++){
            String[] newRow = new String[9];    //新陣列
            String[] thisRow = mainData[rowMD]; //原陣列
            String projectId1 = thisRow[1].trim().substring(0 , thisRow[1].length() - 1) + "A"; //把原本尾碼換成A
            String thisKey =  projectId1 + thisRow[2].trim(); //原陣列KEY
            System.out.println(">>>key>>>" + thisKey);
            String orderContractDate = mapOrderData.get(thisKey) != null ? mapOrderData.get(thisKey).toString():""; //合約日跟簽約日

            System.out.println(">>>date>>>" + orderContractDate);
            String orderDate = "";
            String contractDate = "";
            String[] splitOrderContractDate = orderContractDate.split(" and ");
            if ( !"".equals(orderContractDate) ) {
                orderDate = splitOrderContractDate[0].trim();
                contractDate = splitOrderContractDate[1].trim();
            }

            newRow[0] = thisRow[1].trim();
            newRow[1] = thisRow[2].trim();
            newRow[2] = thisRow[3].trim();
            newRow[3] = thisRow[4].trim();
            newRow[4] = orderDate;
            newRow[5] = contractDate;
            newRow[6] = thisRow[6].trim();
            newRow[7] = thisRow[7].trim();
            newRow[8] = thisRow[8].trim();  //維護人

            mainList.add(newRow);
        }
        
        return mainList;
    }

    public String[][] getMainData() throws Throwable {
        StringBuilder sbSql = new StringBuilder();
        sbSql.append("select T040.DisCountNo, rtrim(ProjectNo), HuBei, rtrim(T0C0.customName) as customName, T0C0.customNo, T041.InvoiceNo , T040.disCountDate , T040.reason, T040.EmployeeNo ");
        sbSql.append("from invom040 T040 ");
        sbSql.append("left join invom0C0 T0C0 on T040.customNo = T0C0.customNo ");
        sbSql.append("left join invom041 T041 on T040.DisCountNo = T041.DisCountNo ");
        sbSql.append("where 1=1 ");
        sbSql.append("and T040.customNo = T0C0.customNo ");
        sbSql.append("and ISNULL(T040.Hubei, '') != '' ");
        sbSql.append("and ISNULL(T040.ProjectNo, '') != '' ");

        if(deptCd.length() != 0 && deptCd1.length() != 0) {
            sbSql.append("AND T040.projectNo = '" + deptCd + deptCd1 + "'  ");
        } else {
            if (deptCd.length() != 0) {
                sbSql.append("AND T040.projectNo like '" + deptCd + "%'  ");
            }
            if (deptCd1.length() != 0) {
                sbSql.append("AND T040.projectNo like '%" + deptCd1 + "'  ");
            }
        }
        
        if (queryDate.length() != 0) {
          String[] ROCDate = formatQueryDate(queryDate);
          if (!"".equals(ROCDate[0])) {
            sbSql.append("AND T040.disCountDate >= " + ROCDate[0] + " ");
          }
          if (!"".equals(ROCDate[1]) && !ROCDate[0].equals(ROCDate[1])) {
            sbSql.append("AND T040.disCountDate <= " + ROCDate[1] + " ");
          }
        }
        
        //以下選填
        if(objectCd.length() != 0) {
            sbSql.append("AND T040.Hubei = '" + objectCd+ "' ");
        }
        if (customName.length() != 0) {
            sbSql.append("AND T0C0.customName like '%" + customName+ "%' ");
        }
        sbSql.append("ORDER BY ProjectNo, HuBei ");
        String[][] strArrRS = dbINVOICE.queryFromPool( sbSql.toString() );

        //組成訂單資訊查詢條件
        qryPosition = new StringBuilder();
        qryProjectId1 = new StringBuilder();
        for (int z=0 ; z<strArrRS.length ; z++) {
            String[] thisDataRow = strArrRS[z];
            String thisPosition = thisDataRow[2].trim();
            String thisProjectId1 = thisDataRow[1].trim();
            String schProjectId1 = "".equals(thisProjectId1) ? "":thisProjectId1.substring(0 , thisProjectId1.length() - 1) + "A" ;
            if ( "".equals(thisPosition) ||  "".equals(thisProjectId1) ) {
                continue;
            }

            if ( qryPosition.toString().contains(thisPosition) && qryProjectId1.toString().contains(schProjectId1)) {
                continue;
            }

            if (z != 0){
                qryPosition.append(" , ");
                qryProjectId1.append(" , ");
            }
            qryPosition.append("'").append(thisPosition).append("'");
            qryProjectId1.append("'").append(schProjectId1).append("'"); //從發票系統回去查訂單的時候，所有案別都要轉成A
        }

        return strArrRS;
    }

    public Map getOrderData() throws Throwable {
        Map mapOrderData = new HashMap();

        //如果主資料沒有就不查了
        if (this.qryPosition.length() == 0 && this.qryProjectId1.length() == 0 )  {
            return mapOrderData;
        }

        StringBuilder sbSql = new StringBuilder();
        sbSql.append("select T90.PROJECTID1, T92.POSITION, T90.orderDate, T274.contractDate ");
        sbSql.append("from sale05m092 T92, sale05m090 T90, sale05m091 T91, sale05m278 T278, sale05m274 T274 ");
        sbSql.append("where 1=1 ");
        sbSql.append("and T92.OrderNo = T90.ORDERNO ");
        sbSql.append("and T91.ORDERNO = T90.ORDERNO ");
        sbSql.append("and T278.orderno = T90.orderNo ");
        sbSql.append("and T278.contractNo = T274.contractNo ");
        if (this.qryPosition.length() != 0 )  {
            sbSql.append("and T92.position in  (" + this.qryPosition + ") ");
        }
        if (this.qryProjectId1.length() != 0 )  {
            sbSql.append("and T90.PROJECTID1 in (" + this.qryProjectId1 + ") ");
        }
        sbSql.append("order by T90.OrderDate asc , T274.contractDate asc");
        String[][] retTempRS = dbSALE.queryFromPool( sbSql.toString() );
        
        for (int z=0 ; z<retTempRS.length ; z++) {
            String[] thisDataRow = retTempRS[z];
            String thisKey = thisDataRow[0].trim() + thisDataRow[1].trim();

            if( mapOrderData.containsKey(thisKey) ){
                continue;
            }
            System.out.println(">>>key0>>>" + thisKey);
            mapOrderData.put(thisKey , thisDataRow[2].trim() + " and " + thisDataRow[3].trim() );
        }
        System.out.println(">>>map Size>>>" + mapOrderData.size());

        return mapOrderData;
    }

    private String[] formatQueryDate(String dayStr) throws Throwable {
        String[] spDay = dayStr.split(" and ");
        String bDay = spDay[0].trim();
        String eDay = spDay[1].trim();

        if (bDay.length() != 8 || eDay.length() != 8) {
            spDay[0] = " '' ";
            spDay[1] = " '' ";
        } else {
            String newBDay = " '" + (Integer.parseInt( bDay.substring(0,4))) + "/" + bDay.substring(4,6) + "/" + bDay.substring(6,8) + "' ";
            String newEDay = " '" + (Integer.parseInt( eDay.substring(0,4))) + "/" + eDay.substring(4,6) + "/" + eDay.substring(6,8) + "' ";
            spDay[0] = newBDay;
            spDay[1] = newEDay;
        }

        return spDay;
    }

    private String formatROCDate(String dayStr) throws Throwable {
        if (dayStr.length() != 7) {
            return "";
        }

        return dayStr.substring(0,3) + "/" + dayStr.substring(3,5) + "/" + dayStr.substring(5,7);
    }

  public String getInformation() {
    return "---------------button2(列印).defaultValue()----------------";
  }
}