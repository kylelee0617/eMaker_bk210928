package Sale.Sale05M090;

import jcx.jform.bNotify;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;

public class FuncAfterMod extends bNotify{
  public void actionPerformed(String value)throws Throwable{
    // 當執行完 Transaction 時,會執行本段程式
    //可用以寄發Email通知或是自動再處理自定Transaction
    
    //"購屋證明單-同步-行銷(Sale02M030)"-------------------------------------
    System.out.println("修改後------------------------------------S") ;
    Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil() ; // 20090414
    talk  dbSale  =  getTalk((String)get("put_dbSale")) ;//SQL2000
    String  stringSQL = "";
    //   0  ProjectID1    1  OrderDate        2   SaleID1           3  SaleName1       4  SaleID2
    //   5  SaleName2     6  SaleID3,             7  SaleName3        8  SaleID4               9  SaleName4
    // 10  SaleID5         11  SaleName5      12  MediaID             13  MediaName       14  ZoneID
    // 15  ZoneName     16  MajorID           17  MajorName       18  UseType           19  Remark
    // 20  SaleID6        21  SaleName6       22  SaleID7              23  SaleName7        24  SaleID8   
    // 25  SaleName8   26  SaleID9           27  SaleName9         28  SaleID10            29  SaleName10
    // 30  SSMediaID    31  SSMediaID1
    stringSQL = "SELECT ProjectID1,   OrderDate,   SaleID1,        SaleName1,   SaleID2, " + 
                                     " SaleName2,  SaleID3,        SaleName3,  SaleID4,         SaleName4, "  +
                     " SaleID5,        SaleName5,  MediaID,       MediaName,  ZoneID, "  +
                     "  ZoneName,  MajorID,       MajorName,  UseType,       Remark, "  + 
                     " SaleID6,        SaleName6,  SaleID7,         SaleName7,   SaleID8, " +
                     " SaleName8,  SaleID9,        SaleName9,  SaleID10,       SaleName10, " +// 增加售出人 20090525
                     " SSMediaID,   SSMediaID1 "  +  // 2015-05-25 增加 媒體細項代碼 楊信義
                " FROM Sale05M090 " +
              " WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" ;
    String[][]  retSale05M090 = dbSale.queryFromPool(stringSQL);
    if(retSale05M090.length == 0){
        message("購屋証明單:" + getValue("OrderNo").trim() + " 不存在!"); 
      return ;    
    }
    //  Sale05M421
    // 2015-10-15 B3018 START
    Vector      vectorColumnName  =  new  Vector() ;
    Vector      vectorSale05M421  =  exeUtil.getQueryDataHashtable("Sale05M421",  new  Hashtable(),  " AND OrderNo = '" + getValue("OrderNo").trim() + "' ",  vectorColumnName,  dbSale) ;   
    Hashtable    hashtableDB        =  null ; 
    Hashtable    hashtableDBL         =  new  Hashtable() ; 
    Vector      vectorSql           =  new  Vector() ;
    String        stringHouseCarL     =  "" ;
    String        stringPositionL     =  "" ;
    String        stringCarL            =  "" ;
    String        stringTemp            =  "" ;
    // 2015-10-15 B3018 END
    String stringProjectID1 = "";
    String stringOrderDate = "";
    stringProjectID1 = retSale05M090[0][0].trim();
    stringOrderDate = retSale05M090[0][1].trim();
    //
    stringSQL = "SELECT CustomName " +
                " FROM Sale05M091 " +
              " WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" +
               " AND ISNULL(StatusCd,'')<>'C' " +                   
               " ORDER BY RecordNo ";
    String[][]  retSale05M091 = dbSale.queryFromPool(stringSQL);          
    if(retSale05M091.length == 0){
        message("購屋証明單:" + getValue("OrderNo").trim() + " 客戶(Sale05M091) 不存在!");  
      return ;    
    }
    //存至風險客戶名單

    System.out.println("洗錢防治相關-------------------------------------S") ;
    //21-05 Kyle : 更新主要客戶與關聯人
    getButton("RenewRelated").doClick();
    
    //21-05 Kyle : 查詢客戶風險值
    getButton("CheckRiskNew").doClick();
    
    // AML
    setValue("actionText","修改");
    getButton("AML").doClick();
    
    //執行寄發MAIL
    getButton("sendMail").doClick();
    
    System.out.println("洗錢防治相關-------------------------------------E") ;


//    //20201223 Kyle : 新版
//    getButton("CheckRiskNew").doClick();
//    System.out.println("CheckRiskNew=====>"+getButton("CheckRiskNew")) ;
//    //洗錢樣態LOG檢核
//    setValue("actionText","修改");  
//    getButton("AML").doClick();

    String stringCustom = "";
    for(int  intSale05M091=0  ;  intSale05M091<retSale05M091.length  ;  intSale05M091++){
      if(intSale05M091==0)  stringCustom = retSale05M091[intSale05M091][0];
      else stringCustom = "-" + stringCustom + retSale05M091[intSale05M091][0];
    }
    //stringCustom = retSale05M091[0][0].trim();
    //  0  HouseCar     1  Position         2  DealMoney      3  TrxDate      4  StatusCd
    //  5  GiftMoney    6  CommMoney    7  ViMoney            8  CommMoney1
    stringSQL = "SELECT HouseCar, " +
                     " Position, " +
                     " DealMoney, " +
                     " TrxDate, " +
                     " StatusCd, " +
                     " GiftMoney, " +
                     " CommMoney, " +
                     " ViMoney, " +                                                                                                                                
                     " CommMoney1 " +   // 2015-10-13 B3018 中原佣金
                " FROM Sale05M092 " +
              " WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" +
              " AND ISNULL(StatusCd,'')<>'D' " +
              " ORDER BY HouseCar DESC,RecordNo" ;
    String[][]  retSale05M092  =  dbSale.queryFromPool(stringSQL);
    String stringPosition1 = "";
    for(int  intSale05M092=0  ;  intSale05M092<retSale05M092.length  ;  intSale05M092++){
      String stringHouseCar = retSale05M092[intSale05M092][0].trim();
      String stringPosition = retSale05M092[intSale05M092][1].trim();
      //車位連結戶別
      if (stringHouseCar.equals("House")) stringPosition1 = stringPosition;
      String stringDealMoney    = retSale05M092[intSale05M092][2].trim();
      String stringTrxDate         = retSale05M092[intSale05M092][3].trim();
      String stringStatusCd       = retSale05M092[intSale05M092][4].trim();
      String stringGiftMoney      = retSale05M092[intSale05M092][5].trim();
      String stringCommMoney = retSale05M092[intSale05M092][6].trim();
      String stringViMoney        = retSale05M092[intSale05M092][7].trim();
      String stringCommMoney1 = retSale05M092[intSale05M092][8].trim();       // 2015-10-13 B3018 中原佣金
      //
      String stringA_Sale      = " A_Sale";
      String stringASaleMan  =  "A_Sale_SaleID";      // 2015-10-15 B3018
      stringSQL = "SELECT ID1 " +
                       " FROM " + stringA_Sale +
                " WHERE ProjectID1 = '" + stringProjectID1 + "'";
      if(stringHouseCar.equals("House"))
        stringSQL = stringSQL + " AND HouseCar = 'Position'" +  
                              " AND Position = '" + stringPosition + "'";
      if(stringHouseCar.equals("Car"))
        stringSQL = stringSQL + " AND HouseCar = 'Car'" +   
                              " AND Car = '" + stringPosition + "'";
      String[][]  retA_Sale  =  dbSale.queryFromPool(stringSQL);
      System.out.println("A_Sale---------------------"+stringSQL) ;
      if(retA_Sale.length == 0){
          message("行銷 A_Sale 棟樓別不存在!"); 
        return ;    
      }
      String stringID1 = retA_Sale[0][0];
      System.out.println("stringID1---------------------"+stringID1) ;
      //價目表
       stringSQL = "SELECT H_Com, " +
                       " L_Com, " +
                       " PingSu, " +
                       " ListPrice, " +
                       " H_ListPrice, " +
                       " L_ListPrice, " +
                       " FloorPrice, " +
                       " H_FloorPrice, " +
                       " L_FloorPrice " +                                    
                  " FROM Sale05M040 " +
                " WHERE ProjectID1 = '" + stringProjectID1 + "'" +
                    " AND HouseCar = '" + stringHouseCar + "'" +
                    " AND Position = '" + stringPosition + "'";
       String[][]  retSale05M040  =  dbSale.queryFromPool(stringSQL);
      if(retSale05M040.length == 0){
        message("價目表(Sale05M040) " +  stringProjectID1 + "-" + stringPosition + " 不存在!"); 
        return ;    
      }   
      String stringH_Com = retSale05M040[0][0];
      String stringL_Com = retSale05M040[0][1]; 
      String stringPingSu = retSale05M040[0][2];
      if (stringHouseCar.equals("Car")) stringPingSu ="1";
      String stringListPrice = retSale05M040[0][3];
      String stringH_ListPrice = retSale05M040[0][4];
      String stringL_ListPrice = retSale05M040[0][5];
      String stringFloorPrice = retSale05M040[0][6];
      String stringH_FloorPrice = retSale05M040[0][7];
      String stringL_FloorPrice = retSale05M040[0][8];
      if ("".equals(stringH_FloorPrice)) stringH_FloorPrice = "0";
      if ("".equals(stringL_FloorPrice)) stringL_FloorPrice = "0";
      double   doubleHouseRate = exeUtil.doParseDouble(operation.floatDivide(stringH_ListPrice,  stringListPrice,  3));
      double   doubleLandRate   = exeUtil.doParseDouble(operation.floatSubtract(""+1,  ""+doubleHouseRate,  3)) ;
      if("H03A".equals(stringProjectID1)) {
          System.out.println("H03A-----------------------------------------------------S") ;
          doubleHouseRate = 0.595 ;
          doubleLandRate   = 0.405 ;
          stringH_Com         =  "3" ;
          stringL_Com         =  "T" ;
          stringH_ListPrice  =  convert.FourToFive(""+(exeUtil.doParseDouble(stringListPrice) * doubleHouseRate),  4) ; // 2010-11-03
          stringL_ListPrice  =  convert.FourToFive(""+(exeUtil.doParseDouble(stringListPrice) * doubleLandRate),  4) ;// 2010-11-03
          stringH_FloorPrice  =  convert.FourToFive(""+(exeUtil.doParseDouble(stringFloorPrice) * doubleHouseRate),  4) ; // 2010-11-03
          stringL_FloorPrice  =  convert.FourToFive(""+(exeUtil.doParseDouble(stringFloorPrice) * doubleLandRate),  4) ;// 2010-11-03
      }
      //
      stringSQL = "SELECT ProjectID " +
                  " FROM A_Group " + 
                " WHERE ProjectID1 = '" + stringProjectID1 + "'";
       String[][]  retA_Group  =  dbSale.queryFromPool(stringSQL);
      if(retA_Group.length == 0){
        message("行銷 A_Group 案別不存在!"); 
        return ;    
      }
      String stringProjectID = retA_Group[0][0];
      //UPDATE A_Sale
      vectorSql  =  new  Vector() ;// 2015-10/15 B3018
       stringSQL = "UPDATE " + stringA_Sale +
                   " SET YearMM = '" + stringOrderDate + "'," +
                        " Com = '" + stringH_Com + "'," +              
                        " ProjectID = '" + stringProjectID + "'," +
                        " ProjectID0 = '" + stringProjectID1 + "'," +
                        " ProjectID1 = '" + stringProjectID1 + "'," +   
                        " H_Com = '" + stringH_Com + "'," +
                        " H_LandOwner = 'N'," +
                        " H_LandShare = 'N'," +       
                        " L_Com = '" + stringL_Com + "'," +
                        " L_LandOwner = 'N'," +
                        " L_LandShare = 'N',"  +
                      " SaleID1  =  '"         +  retSale05M090[0][2].trim()    +  "', "  +
                      " SaleName1  =  '"   +  retSale05M090[0][3].trim()    +  "', "  +
                      " SaleID2  =  '"         +  retSale05M090[0][4].trim()    +  "', "  +
                      " SaleName2  =  '"  +  retSale05M090[0][5].trim()     +  "', "  +
                      " SaleID3  =  '"         +  retSale05M090[0][6].trim()    +  "', "  +
                      " SaleName3  =  '"   +  retSale05M090[0][7].trim()    +  "', "  +
                      " SaleID4  =  '"         +  retSale05M090[0][8].trim()     +  "', "  +
                      " SaleName4  =  '"   +  retSale05M090[0][9].trim()     +  "', "  +
                      " SaleID5  =  '"          +  retSale05M090[0][10].trim()  +  "', "  +
                      " SaleName5  =  '"    +  retSale05M090[0][11].trim()  +  "', "  +
                      " SaleID6  =  '"          +  retSale05M090[0][20].trim()  +  "', "  +     // 增加售出人 20090414
                      " SaleName6  =  '"    +  retSale05M090[0][21].trim()  +  "', "  +     // 增加售出人 20090414
                      " MediaID  =  '"         +  retSale05M090[0][12].trim()  +  "', "  +
                      " MediaName  =  '"   +  retSale05M090[0][13].trim()  +  "', "  +
                      " ZoneID  =  '"           +  retSale05M090[0][14].trim()  +  "', "  +
                      " ZoneName  =  '"     +  retSale05M090[0][15].trim()  +  "', "  +
                      " MajorID  =  '"          +  retSale05M090[0][16].trim()  +  "', "  +
                      " MajorName  =  '"   +  retSale05M090[0][17].trim()  +  "', "  +
                      " UseType  =  '"        +  retSale05M090[0][18].trim()  +  "', "  +
                      " Remark  =  '"          +  retSale05M090[0][19].trim()  +  "', " ;
      if(stringHouseCar.equals("House")) stringSQL = stringSQL + " Position = '" +  stringPosition + "',";
      else{
          stringSQL = stringSQL + " Car = '" +  stringPosition + "'," +
                                " Position1 = '" +  stringPosition1 + "'," +
                                " Custom1 = '" +  stringCustom + "',";
      }
      if ("".equals("stringDealMoney")) stringDealMoney = "0  ";
      if ("".equals("stringGiftMoney")) stringGiftMoney = "0  ";
      if ("".equals("stringCommMoney")) stringCommMoney = "0  ";
      if ("".equals("stringCommMoney1")) stringCommMoney1 = "0  ";    // 2015-10-13 B3018 中原佣金
      if ("".equals("stringViMoney")) stringViMoney = "0  ";
      /*String stringPureMoney = "" + (Double.parseDouble(stringDealMoney) - 
                                Double.parseDouble(stringGiftMoney) -
                                Double.parseDouble(stringCommMoney) -
                                Double.parseDouble(stringViMoney));*/
      String stringPureMoney = "" + (exeUtil.doParseDouble(stringDealMoney) - 
                                exeUtil.doParseDouble(stringGiftMoney) -
                                exeUtil.doParseDouble(stringCommMoney) -
                                exeUtil.doParseDouble(stringCommMoney1) -   // 2015-10-22 B3018
                                exeUtil.doParseDouble(stringViMoney));
        System.out.println("stringPureMoney"+stringPureMoney);
        System.out.println("stringL_FloorPrice"+stringL_FloorPrice);
      if(exeUtil.doParseDouble(stringGiftMoney)  <=  0)      stringGiftMoney  = "0" ;
      if(exeUtil.doParseDouble(stringCommMoney)  <=  0)  stringCommMoney  = "0" ;
      if(exeUtil.doParseDouble(stringCommMoney1)  <=  0)  stringCommMoney1  = "0" ;       // 2015-10-22 B3018
      if(exeUtil.doParseDouble(stringViMoney)  <=  0)  stringViMoney  = "0" ;
        stringSQL = stringSQL + 
                " Custom = '" + stringCustom + "'," +
                " OrderDate = '" + stringOrderDate + "'," +           
                " PingSu = " + stringPingSu + "," +                       
                " PreMoney = " + stringListPrice + "," +
                " H_PreMoney = " + convert.FourToFive(stringH_ListPrice,4) + "," +                        
                " L_PreMoney = " + convert.FourToFive(stringL_ListPrice,4) + "," +
                " DealMoney = " + convert.FourToFive(stringDealMoney,4) + "," +
                " H_DealMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringDealMoney) * doubleHouseRate,4) + "," +                       
                " L_DealMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringDealMoney) * doubleLandRate,4) + "," +                                                            
                " GiftMoney = " + convert.FourToFive(stringGiftMoney,4) + "," +
                " H_GiftMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringGiftMoney) * doubleHouseRate,4) + "," +                       
                " L_GiftMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringGiftMoney) * doubleLandRate,4) + "," +                                                            
                " CommMoney = " + convert.FourToFive(stringCommMoney,4) + "," +
                " H_CommMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringCommMoney) * doubleHouseRate,4) + "," +
                " L_CommMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringCommMoney) * doubleLandRate,4) + "," +
                " CommMoney1 = " + convert.FourToFive(stringCommMoney1,4) + "," +                                               // 2015-10-13 B3018 中原佣金
                " H_CommMoney1 = " + convert.FourToFive(""+exeUtil.doParseDouble(stringCommMoney1) * doubleHouseRate,4) + "," +         // 2015-10-13 B3018 中原佣金
                " L_CommMoney1 = " + convert.FourToFive(""+exeUtil.doParseDouble(stringCommMoney1) * doubleLandRate,4) + "," +        // 2015-10-13 B3018 中原佣金
                " ViMoney = " + convert.FourToFive(stringViMoney,4) + "," +
                " H_ViMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringViMoney) * doubleHouseRate,4) + "," +                       
                " L_ViMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringViMoney) * doubleLandRate,4) + "," +                                                            
                  " PureMoney = " + stringPureMoney + "," +
                " H_PureMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringPureMoney) * doubleHouseRate,4) + "," +                       
                " L_PureMoney = " + convert.FourToFive(""+exeUtil.doParseDouble(stringPureMoney) * doubleLandRate,4) + "," +                                                            
                " LastMoney = " + stringFloorPrice + "," +
                " H_LastMoney = " + stringH_FloorPrice + "," +                        
                " L_LastMoney = " + stringL_FloorPrice + "," +
                " BalaMoney = " +  convert.FourToFive(""+(exeUtil.doParseDouble(stringPureMoney) - exeUtil.doParseDouble(stringFloorPrice)),4) + "," +
                " H_BalaMoney = " + convert.FourToFive(""+(exeUtil.doParseDouble(stringPureMoney) * doubleHouseRate - exeUtil.doParseDouble(stringH_FloorPrice)),4) + "," +
                " L_BalaMoney = " + convert.FourToFive(""+(exeUtil.doParseDouble(stringPureMoney) * doubleLandRate - exeUtil.doParseDouble(stringL_FloorPrice)),4) + "," +
                " OrderNo = '" + getValue("OrderNo").trim() + "',  " +
                " SSMediaID  =  '"      +  retSale05M090[0][30].trim()  +  "', "  +     // 增加媒體代碼 2010-04-21 楊信義
                " SSMediaID1  =  '"    +  retSale05M090[0][31].trim()  +  "', "  +    // 增加媒體細項代碼 2015-05-25 楊信義
                " SaleID7  =  '"           +  retSale05M090[0][22].trim()  +  "', "  +    // 增加售出人 20090525
                " SaleName7  =  '"     +  retSale05M090[0][23].trim()  +  "', "  +    // 增加售出人 20090525
                " SaleID8  =  '"           +  retSale05M090[0][24].trim()  +  "', "  +    // 增加售出人 20090525
                " SaleName8  =  '"     +  retSale05M090[0][25].trim()  +  "', "  +    // 增加售出人 20090525
                " SaleID9  =  '"           +  retSale05M090[0][26].trim()  +  "', "  +    // 增加售出人 20090525
                " SaleName9  =  '"     +  retSale05M090[0][27].trim()  +  "', "  +    // 增加售出人 20090525
                " SaleID10  =  '"          +  retSale05M090[0][28].trim()  +  "', "  +    // 增加售出人 20090525
                " SaleName10  =  '"    +  retSale05M090[0][29].trim()  +  "' "  +     // 增加售出人 20090525
                " WHERE ID1 = " +  stringID1;
      System.out.println("同步---------------------"+stringSQL);
       // 2015/10/15 B3018 START
       vectorSql.add(stringSQL) ;
       stringSQL  =  exeUtil.doDeleteDB(stringASaleMan,  new  Hashtable(),  " AND  ID1  =  "+stringID1+" ",  false,  dbSale) ;vectorSql.add(stringSQL) ;
       for(int  intNo=0  ;  intNo<vectorSale05M421.size()  ;  intNo++) {
            hashtableDB  =  (Hashtable)  vectorSale05M421.get(intNo) ;  if(hashtableDB  ==  null)  continue ;
          //
          for(int  intNoL=0  ;  intNoL<vectorColumnName.size()  ;  intNoL++) {
              stringTemp  =  ""+vectorColumnName.get(intNoL) ;
              hashtableDBL.put(stringTemp,  ""+hashtableDB.get(stringTemp)) ;
          }
          //
          if(stringHouseCar.equals("House")) {
              stringHouseCarL  = "Position" ;
              stringPositionL      = stringPosition ;
              stringCarL            = "" ;
          } else if(stringHouseCar.equals("Car")) {
              stringHouseCarL  = "Car" ;
              stringPositionL      = "" ;
              stringCarL            = stringPosition ;
          }
          //
          hashtableDBL.put("ID1",         stringID1) ;
          hashtableDBL.put("ProjectID1",    stringProjectID1) ;
          hashtableDBL.put("HouseCar",     stringHouseCarL) ;
          hashtableDBL.put("Position",      stringPositionL) ;
          hashtableDBL.put("Car",               stringCarL) ;
          stringSQL      =  exeUtil.doInsertDB(stringASaleMan,  hashtableDBL,  false,  dbSale) ;
          //
          vectorSql.add(stringSQL) ;
       }
       dbSale.execFromPool((String[])  vectorSql.toArray(new  String[0]));  
       // 2015/10/15 B3018 END
    }
    message("OK!");

    System.out.println("修改後------------------------------------E") ;
    return;
  }
  public String getInformation(){
    return "---------------update_trigger()----------------";
  }
}
