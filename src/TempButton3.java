
import java.util.Hashtable;

import javax.swing.JTable;

import Doc.Doc2M010;
import Farglory.util.FargloryUtil;
import jcx.db.talk;
import jcx.jform.bproc;
import jcx.util.datetime;

public class TempButton3 extends bproc {

  public String getDefaultValue(String value) throws Throwable {

    // --------------------------------------------------------------------------------------------------------------------
  //退戶前查詢 
    System.out.println("退戶前查詢 ---------------------------S") ;
    getButton("button2").setVisible(false);     
    getButton("button3").setVisible(false); 
    getButton("button4").setVisible(false);   

    if (getValue("field1").length()==0){
      message("請輸入案別");     
      return value;
    }

    if (getValue("field2").length()==0){
      message("請輸入退戶日期");     
      return value;
    }

    if (getValue("field3").length()==0){
      message("請輸入使用單編號");      
      return value;
    }
    message("");  
    talk dbSale = getTalk(""+get("put_dbSale"));    
    getButton("button2").setVisible(true);      
    String stringFlag="";
    String stringDiscountOpen="";
    String stringSQL="select DiscountOpen, Amt, DiscountAmt  from SALE05M094  " + 
    " where ProjectID1='" +getValue("field1") + "'"+
    " and OrderNo='" +getValue("field3") + "'"+
    " and TrxDate='" +getValue("field2") + "'";
    String retSale05M094[][] = dbSale.queryFromPool(stringSQL);
    if (retSale05M094.length > 0){
      stringFlag="Y";
      setValue("DiscountOpen",retSale05M094[0][0]);
      setValue("Amt",retSale05M094[0][1]);
      setValue("DiscountAmt",retSale05M094[0][2]);
      if (getValue("DiscountOpen").equals("Y")){
        getButton("button2").setVisible(false);     
        stringDiscountOpen="Y";
      }else{
        getButton("button3").setVisible(true);        
      } 
    }

    stringSQL = "SELECT OrderNo , RecordNo,CustomNo  ,"+
                     " CustomName ,Percentage , Address,Tel,eMail   "+      
                     " FROM Sale05M091"+        
          " where OrderNo='" +getValue("field3")+"'"+
          " and (StatusCd ='' or StatusCd is null)"+
          "order by RecordNo";              
    String retSale05M091[][] = dbSale.queryFromPool(stringSQL);
    setTableData("table1",retSale05M091);   

    int intCount=0;
    String stringCond="";
    stringSQL="select count(*) from SALE05M092"+  
               " where OrderNo='" +getValue("field3")+"' and ISNULL(StatusCd,'') = 'D' " +
               " and TrxDate = '" +getValue("field2") + "' ";
    String retSale05M092[][] = dbSale.queryFromPool(stringSQL);   
    if (stringDiscountOpen.equals("Y")){           
      //intCount=Integer.parseInt(retSale05M092[0][0]);
      stringCond= " and ISNULL(StatusCd,'') = 'D' ";
      System.out.println("stringDiscountOpen=y,stringCond"+stringCond);
    }
    if (stringFlag.equals("")){
      stringCond= " and ISNULL(StatusCd,'') <> 'D' ";
      System.out.println("stringFlag='',stringCond"+stringCond);
    }
     stringSQL="select OrderNo,RecordNo,HouseCar, Position, PingSu"+
               ",ListPrice,DealMoney,TrxDate,StatusCd from sale05m092 " +
               " where OrderNo='" +getValue("field3")+"' " + stringCond +
               " order by RecordNo ";
     retSale05M092 = dbSale.queryFromPool(stringSQL);               
     String [][]ret2=new String[(retSale05M092.length-intCount)][9];         
     if (retSale05M092.length>0){
      for (int k=0;k<(retSale05M092.length-intCount);k++){  
            System.out.println(retSale05M092+"---------------------------") ;
         //if (!retSale05M092[k][8].equals("D")){ 
           if (retSale05M092[k][7].equals(getValue("field2")) && retSale05M092[k][8].equals("D")){
             ret2[k][0]="1";
           }else{
             ret2[k][0]="0";
           }
           ret2[k][1]=retSale05M092[k][0];    
           ret2[k][2]=retSale05M092[k][1];    
           ret2[k][3]=retSale05M092[k][2];                     
           ret2[k][4]=retSale05M092[k][3];                     
           ret2[k][5]=retSale05M092[k][4];                     
           ret2[k][6]=retSale05M092[k][5];                     
           ret2[k][7]=retSale05M092[k][6];             
        //}      
      }
      setTableData("table2",ret2);      
     }          

    stringSQL="select *  from SALE05M096  " + 
    " where ProjectID1='" +getValue("field1") + "'"+
    " and OrderNo='" +getValue("field3") + "'"+
    " and TrxDate='" +getValue("field2") + "'";
    String retSale05M096[][] = dbSale.queryFromPool(stringSQL);
    setTableData("table3",retSale05M096); 

    if (stringDiscountOpen.equals("") && stringFlag.equals("Y")){
      getButton("button4").setVisible(true);
    }
    System.out.println("退戶前查詢 ---------------------------E") ;
    return value;
    // --------------------------------------------------------------------------------------------------------------------

  } // getDefault End

}
