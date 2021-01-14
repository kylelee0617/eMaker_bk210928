package Sale.Sale05M274;
import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;

public class FuncQuery extends bTransaction{
  public boolean action(String value)throws Throwable{
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL" (列印預覽的全部列印按鈕) 其中之一
    String stringDate = getQueryValue("ContractDate").trim();
    // Start 修改日期:20100409 員工編號:B3774
    String retDate[]    = convert.StringToken(stringDate, "and");
    /*
    if(stringDate.length() > 0){
      if(stringDate.replaceAll("/","").length() != 8){
        message("[簽約日期] 格式錯誤(YYYY/MM/DD)!");
        return false;
      }
      Farglory.util.FargloryUtil  exeFun  =  new  Farglory.util.FargloryUtil();
      stringDate = exeFun.getDateAC(stringDate, "簽約日期");
      if(stringDate.length() != 10){
        message(stringDate);
        return false;
      }
    */  
    if(!"and".equals(stringDate)  &&  retDate.length == 2){
      Farglory.util.FargloryUtil  exeFun  =  new  Farglory.util.FargloryUtil();
      String stringDateS = retDate[0].trim().replaceAll("/","");
      String stringDateE = retDate[1].trim().replaceAll("/","");
      //
      stringDateS = exeFun.getDateAC(stringDateS, "簽約日期-起");
      if(stringDateS.length() != 10){
        message(stringDateS);
        return false;
      }
      stringDateE = exeFun.getDateAC(stringDateE, "簽約日期-迄");
      if(stringDateE.length() != 10){
        message(stringDateE);
        return false;
      }
      //
      if(stringDateS.compareTo(stringDateE) > 0){
        message("[簽約日期] 起迄錯誤!");
        return false;
      }
      stringDate = stringDateS + " and " + stringDateE;
    // End 修改日期:20100409 員工編號:B3774 
      setQueryValue("ContractDate", stringDate);
    }
    //
    // Start 修改日期:20100120 員工編號:B3774
    // Start 修改日期:20100409 員工編號:B3774
    /*
    String stringF_INP_TIME = getQueryValue("table19.F_INP_TIME").trim();
    if(stringF_INP_TIME.length() > 0){
      if(!check.isACDay(stringF_INP_TIME)){
        message("[簽核日期] 格式錯誤(YYYYMMDD)!");
        return false;
      }
    */
    stringDate = getQueryValue("table19.F_INP_TIME").trim();
    retDate     = convert.StringToken(stringDate, "and");
    if(!"and".equals(stringDate)  &&  retDate.length == 2){
      Farglory.util.FargloryUtil  exeFun  =  new  Farglory.util.FargloryUtil();
      String stringDateS = retDate[0].trim().replaceAll("/","");
      String stringDateE = retDate[1].trim().replaceAll("/","");
      //
      if(stringDateS.length() != 8  &&  stringDateS.length() != 17){
        message("[簽核日期]-起 格式錯誤!");
        return false;
      }
      if(stringDateS.length() == 8){
        stringDateS = stringDateS + " 00:00:00";
      }
      if(stringDateE.length() != 8  &&  stringDateE.length() != 17){
        message("[簽核日期]-迄 格式錯誤!");
        return false;
      }
      if(stringDateE.length() == 8){
        stringDateE = stringDateE + " 23:59:59";
      }
      //
      if(stringDateS.compareTo(stringDateE) > 0){
        message("[簽核日期] 起迄錯誤!");
        return false;
      }
      stringDate = stringDateS + " and " + stringDateE;
      setQueryValue("table19.F_INP_TIME", stringDate);
    // End 修改日期:20100409 員工編號:B3774 
    }
    // End 修改日期:20100120 員工編號:B3774
    return true;
  }
  public String getInformation(){
    return "---------------\u67e5\u8a62\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
  }
}
