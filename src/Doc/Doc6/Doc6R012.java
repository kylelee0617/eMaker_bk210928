package Doc.Doc6;

import jcx.jform.bTransaction;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import javax.swing.*;
import com.jacob.activeX.*;
import com.jacob.com.*;
import Farglory.util.FargloryUtil;

public class Doc6R012 extends bTransaction {
  talk dbDoc = getTalk("" + get("put_Doc"));
  talk dbSale = getTalk("" + get("put_Sale"));

  public boolean action(String value) throws Throwable {
    // 回傳值為 true 表示執行接下來的資料庫異動或查詢
    // 回傳值為 false 表示接下來不執行任何指令
    // 傳入值 value 為 "新增","查詢","修改","刪除","列印","PRINT" (列印預覽的列印按鈕),"PRINTALL"
    // (列印預覽的全部列印按鈕) 其中之一
    // 格式 英制 寬度11.8 高度 8.5
    System.out.println("列印------------Doc6R012---------------S2015-11-04");
    FargloryUtil exeUtil = new FargloryUtil();
    //
    String stringPrevFunction = ("" + get("Doc2M010_PRINT_Function")).trim(); // 新加
    String stringBarCode = ("" + get("Doc2M010_PRINT_BarCode")).trim();
    if ("null".equals(stringBarCode) || "".equals(stringBarCode)) return false;
    String stringCheapenMoney = ("" + get("Doc2M010_PRINT_CheapenMoney")).trim();
    String stringRetainMoney = ("" + get("Doc2M010_PRINT_RetainMoney")).trim();
    String stringRealMoneySumPrint = "";
    String stringFunction = ("" + get("Doc2M010_FUNCTION")).trim();
    String stringPrinterNAME = ("" + get("Doc2M010_PRINT_PrinterNAME")).trim();
    put("Doc2M010_PRINT_PrinterNAME", "null");
    String stringPrintable = ("" + get("Doc2M010_PRINT_Enable")).trim();
    put("Doc2M010_PRINT_Enable", "null");
    boolean booleanFlow = true;
    boolean booleanSource = stringPrevFunction.indexOf("Doc5M") == -1;
    boolean booleanToNextFlow = false;

    if ("".equals(stringRetainMoney) || "null".equals(stringRetainMoney)) stringRetainMoney = "0";
    if ("".equals(stringCheapenMoney) || "null".equals(stringCheapenMoney)) stringCheapenMoney = "0";
    //
    stringCheapenMoney = getCheapenMoney(stringBarCode, stringFunction, stringPrevFunction, exeUtil, stringCheapenMoney);
    //
    if ("".equals(stringPrinterNAME) || "null".equals(stringPrinterNAME)) {
//            messagebox("列表機錯誤，請洽資訊室。") ;
//            return  false ;
    }
    setPrinter(stringPrinterNAME);
    //
    if ("A".equals(stringFunction)) {
      /*
       * boolean booleanDoc2M0143Exist =
       * dbDoc.queryFromPool("SELECT  BarCode  FROM  Doc2M0143  WHERE  BarCode  =  '"
       * +stringBarCode+"' ").length>0 ; if(booleanDoc2M0143Exist) stringCheapenMoney
       * = "0" ; // 請款 doRequestDefault( ) ; booleanSource =
       * stringPrevFunction.indexOf("Doc5M")==-1 ; booleanToNextFlow =
       * isToNextFlow(stringBarCode, booleanSource?"Doc2M010":"Doc5M020", exeUtil,
       * dbDoc) ;
       * System.out.println("請款----------------------["+stringCheapenMoney+"]") ;
       * booleanFlow = doPrint1(stringBarCode, stringRealMoneySumPrint,
       * stringRetainMoney, stringCheapenMoney, booleanSource, booleanDoc2M0143Exist,
       * exeUtil, dbDoc, stringPrevFunction) ;
       */
    } else if ("B".equals(stringFunction)) {
      // 借款沖銷
      /*
       * System.out.println("借款沖銷----------------------") ; doRequestDefault( ) ;
       * booleanSource = stringPrevFunction.indexOf("Doc5M")==-1 ; booleanToNextFlow =
       * isToNextFlow(stringBarCode, "Doc6M010", exeUtil, dbDoc) ; booleanFlow =
       * doPrint2(stringBarCode, stringPrevFunction, booleanSource, exeUtil) ;
       */
    } else {
      // 借款
      System.out.println("借款----------------------");
      booleanSource = stringPrevFunction.indexOf("Doc5M") == -1;
      booleanToNextFlow = isToNextFlow(stringBarCode, booleanSource ? "Doc6M010" : "Doc5M030", exeUtil, dbDoc);
      booleanFlow = doPrint3(stringBarCode, stringPrevFunction, booleanSource, exeUtil);
    }
    // 請款單左上角增加列印時間：(97/02 17:00) PrintDateTime2
    String stringToday = datetime.getTime("yy/mm/dd h:m:s");
    String stringPrintCount = getValue("PrintCount").trim();
    int intPos = stringToday.indexOf("/");
    int intPosE = stringToday.lastIndexOf(":");
    int intPrintCount = exeUtil.doParseInteger(stringPrintCount);
    stringToday = stringToday.substring(intPos + 1, intPosE);
    if (booleanToNextFlow) {
      stringToday = stringToday + " 補印" + (intPrintCount + 1);
    }
    setValue("PrintDateTime2", "列印時間：" + stringToday);
    if (booleanFlow) {
      //
      String stringTable = "";
      System.out.println("stringPrintable(" + stringPrintable + ")---------------------------");
      if ("B3018,".indexOf(getUser() + ",") == -1 && "Y".equals(stringPrintable)) {
        getInternalFrame(getFunctionName()).setVisible(false);
        action(5);  //按紐編號(1:新增 2:查詢 3:修改 4:刪除 5:列印(先預覽) 6:直接列印(不預覽) 61:直接列印全部(不預覽) 7:詳細列表 8:流程記錄 9:重整畫面 ).
        System.out.println("列印完成----------Doc6R012-----------------OK");
      }
      // System.out.println("列表機---------------------------11111") ;
      if ("B3018,".indexOf(getUser().toUpperCase() + ",") == -1 && "Y".equals(stringPrintable)) {
        if ("A".equals(stringFunction)) {
          stringTable = booleanSource ? "Doc2M010" : "Doc5M020";
        } else {
          stringTable = booleanSource ? "Doc6M010" : "Doc5M030";
        }
        if (booleanToNextFlow && !"B3018".equals(getUser().toUpperCase())) doAddPrintCount(stringTable, "" + (intPrintCount + 1), stringBarCode);
      }
      put("Doc2M010_Print_OK", "OK");
    }
    put("Doc2M010_PRINT_RetainMoney", "null");
    System.out.println("列印------------Doc6R012---------------E2015-11-04");
    return false;
  }

  // true 已至下一流程 false 仍在本身部室
  public boolean isToNextFlow(String stringBarCode, String stringTableName, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringUndergoWrite = "";
    String stringDocStatus = "";
    String stringDepartNoOld = "";
    String stringDepartNoNow = "";
    String stringSqlAnd = " AND BarCode  =  '" + stringBarCode + "' ";
    Hashtable hashtableDoc1M040 = null;
    Hashtable hashtableDoc2M010 = null;
    Vector vectorDoc1M040 = new Vector();
    /*
     * 補印判斷 申請單退件，仍在原單位 false 申請單正常，最後狀態 出文4，已至下一流程 true 申請單正常，最後狀態非出文4， 同一部室 仍在原單位
     * false 申請單正常，最後狀態非出文4，不同一部室 已至下一流程 true
     */
    hashtableDoc2M010 = exeUtil.getQueryDataHashtableH(stringTableName, new Hashtable(), stringSqlAnd, dbDoc);
    stringUndergoWrite = "" + hashtableDoc2M010.get("UNDERGO_WRITE");
    if ("X".equals(stringUndergoWrite)) return false;
    if ("Y".equals(stringUndergoWrite)) return true;
    vectorDoc1M040 = exeUtil.getQueryDataHashtable("Doc1M040", new Hashtable(), stringSqlAnd + " ORDER BY  EDateTime ", dbDoc);
    if (vectorDoc1M040.size() == 0) return false;
    //
    hashtableDoc1M040 = (Hashtable) vectorDoc1M040.get(vectorDoc1M040.size() - 1);
    stringDocStatus = "" + hashtableDoc1M040.get("DocStatus");
    stringDepartNoNow = "" + hashtableDoc1M040.get("DepartNo");
    if ("4".equals(stringDocStatus)) return true;
    hashtableDoc1M040 = (Hashtable) vectorDoc1M040.get(0);
    stringDepartNoOld = "" + hashtableDoc1M040.get("DepartNo");
    if (stringDepartNoOld.startsWith("022")) {
      if (!stringDepartNoOld.equals(stringDepartNoNow)) return true;
    } else {
      stringDepartNoOld = exeUtil.doSubstring(stringDepartNoOld, 0, 3);
      stringDepartNoNow = exeUtil.doSubstring(stringDepartNoNow, 0, 3);
      if (!stringDepartNoOld.equals(stringDepartNoNow)) return true;
    }
    return false;
  }

  // 列印 START
  /*
   * public boolean doPrint1(String stringBarCode, String stringRealMoneySumPrint,
   * String stringRetainMoney, String stringCheapenMoney, boolean booleanSource,
   * boolean booleanDoc2M0143Exist, FargloryUtil exeUtil, talk dbDoc, String
   * stringPrevFunction) throws Throwable { String stringCostID = "" ; String[][]
   * retDoc2M010 = null ; String[][] retDoc2M011 = null ; String[][] retDoc2M012 =
   * null ; String[][] retDoc2M013 = null ; Vector vectorPocketMoney = new Vector(
   * ) ; boolean booleanPocketMoney = false ; // true 表示為零用金之實例情況 //
   * setValue("DocCodeTXT1", "") ; setValue("DocCodeTXT2", "") ;
   * setValue("DocCodeTXT3", "") ; setValue("DocCodeTXT4", "") ;
   * setValue("DocCodeTXT5", "") ; setValue("DocCodeTXT6", "") ; // 0 ComNo 1
   * Descript 2 3 BarCode 4 DocNo // 5 UNDERGO_WRITE 6 7 8 9 DocNo1 // 10 DocNo2
   * 11 DocNo3 12 DepartNo 13 RetainMoney 14 PayCondition1 // 15 PayCondition2 16
   * CDate 17 OriEmployeeNo 18 Descript 19 PreFinDate // 20 PurchaseNoExist 21
   * PrintCount 22 EDateTime 23 CTime 24 AccountCount // 25 WriteRetainMoney 26
   * RetainBarCode 27 DocNoType 28 FactoryNoSpec 29 LastPayDate // 30 KindNo 31
   * CoinType //System.out.println("------------------------------getDoc2M010-");
   * retDoc2M010 = getDoc2M010(booleanSource?"Doc2M010":"Doc5M020", stringBarCode)
   * ; if(retDoc2M010.length == 0) { messagebox("查無資料。") ; return false; } // 幣別
   * 18 String stringCoinType = retDoc2M010[0][31].trim() ; String stringCoinName
   * = exeUtil.getNameUnion("CoinName", "Doc7M001",
   * " AND  CoinType  =  '"+stringCoinType+"' ", new Hashtable(), dbDoc) ;
   * if(!"".equals(stringCoinName)) { if("B3018".equals(getUser())) {
   * stringCoinName = "幣別："+stringCoinName ; } } setValue("CoinType",
   * stringCoinName) ; // setValue("DeifyDepart", "") ;
   * if("033FZ".equals(retDoc2M010[0][9].trim())) { setValue("DeifyDepart",
   * "敬會人總室") ; } stringRetainMoney = retDoc2M010[0][13].trim() ; String[][]
   * retDoc5M0220 = null ; if(!"".equals(retDoc2M010[0][26].trim())) {
   * retDoc5M0220 = getDoc5M0220(retDoc2M010[0][26].trim(), "", stringBarCode, "")
   * ; if(retDoc5M0220.length > 0) { // 管理費用 } else { // 行銷 return
   * setDataRetain(stringPrevFunction, retDoc2M010, booleanSource, exeUtil); } }
   * // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID // 5 CostID1 6
   * RealMoney 7 RealTotalMoney retDoc2M012 =
   * getDoc2M012(booleanSource?"Doc2M012":"Doc5M022", stringBarCode) ;
   * if(retDoc2M012.length==0 && retDoc5M0220.length==0) {
   * messagebox("查無 [費用] 資料。") ; return false ; } if(retDoc2M012.length>0) {
   * stringCostID = retDoc2M012[0][4].trim( ) ; // 零用金 vectorPocketMoney.add("31")
   * ; vectorPocketMoney.add("32") ; if(vectorPocketMoney.indexOf(stringCostID) !=
   * -1) booleanPocketMoney = true ; } // if(!(booleanPocketMoney &&
   * !"Y".equals(retDoc2M010[0][20].trim())) &&
   * "A".equals(retDoc2M010[0][27].trim())) { // 0 FactoryNo 1 InvoiceKind 2
   * InvoiceDate 3 InvoiceNo 4 InvoiceMoney // 5 InvoiceTax 6 InvoiceTotalMoney 7
   * DeductKind 8 RecordNo retDoc2M011 =
   * getDoc2M011(booleanSource?"Doc2M011":"Doc5M021", stringBarCode) ; //
   * 取得發票資料，Doc2M011 //if(retDoc2M011.length == 0) { retDoc2M013 =
   * getDoc2M013(booleanSource?"Doc2M013":"Doc5M023", stringBarCode) ;
   * //if(retDoc2M013.length == 0) { //message("查無資料。") ;
   * //messagebox("查無 [發票] 或 [扣繳] 資料。") ; //return false ; //} //} }
   * //System.out.println("-------------------------------查詢資料(發票或扣繳)END"); //
   * //System.out.println("-------------------------------設定資料START"); double
   * doubleRealMoneySumPrint = 0 ; double doubleRealMoneySumPrintNeg = 0 ; double
   * doubleRealMoneySumPrintPos = 0 ; double doubleTemp = 0 ; for(int intNo=0 ;
   * intNo<retDoc2M012.length ; intNo++) { doubleTemp =
   * exeUtil.doParseDouble(retDoc2M012[intNo][7].trim()) ; doubleRealMoneySumPrint
   * += doubleTemp ; if(doubleTemp > 0) { doubleRealMoneySumPrintPos += doubleTemp
   * ; } else { doubleRealMoneySumPrintNeg += doubleTemp ; } }
   * stringRealMoneySumPrint = convert.FourToFive(""+doubleRealMoneySumPrint, 0) ;
   * System.out.println("stringRealMoneySumPrint---------------------------["+
   * stringRealMoneySumPrint+"]") ; return
   * setDataDoc2M010(stringRealMoneySumPrint, stringRetainMoney,
   * stringCheapenMoney, booleanPocketMoney, retDoc2M010, retDoc2M011,
   * retDoc2M012, retDoc2M013, retDoc5M0220, booleanSource, booleanDoc2M0143Exist,
   * exeUtil, dbDoc, stringPrevFunction, doubleRealMoneySumPrintPos,
   * doubleRealMoneySumPrintNeg) ; }
   */
  /*
   * public boolean setDataRetain(String stringPrevFunction, String[][]
   * retDoc2M010, boolean booleanSource, FargloryUtil exeUtil) throws Throwable {
   * // String stringDepartNo = retDoc2M010[0][12].trim( ) ; String stringCDate =
   * retDoc2M010[0][16].trim( ) ; String stringDocNo = retDoc2M010[0][4].trim( ) ;
   * String stringDescript = retDoc2M010[0][18].trim( ) ; String stringComNo =
   * retDoc2M010[0][0].trim( ) ; String stringEDateTime = retDoc2M010[0][22].trim(
   * ) ; String stringWriteRetainMoney = retDoc2M010[0][25].trim( ) ; String
   * stringRetainBarCode = retDoc2M010[0][26].trim( ) ; String stringPurchaseNo =
   * "" ; String stringPurchaseNo1 = "" ; String stringPurchaseNo2 = "" ; String
   * stringPurchaseNo3 = "" ; String stringOptometryNo = "" ; String stringBarCode
   * = retDoc2M010[0][3].trim( ) ; String stringPrintCount =
   * retDoc2M010[0][21].trim( ) ; String stringFactoryNo = "" ; String
   * stringInOutFirst = "" ; String stringInOut = "" ; String stringProjectFirst =
   * "" ; String stringProject = "" ; String stringProjectID = "" ; String
   * stringProjectID1 = "" ; String stringProjectShow = "" ; String stringDepart =
   * "" ; String stringCompanyName = "" ; String stringDepartName = "" ; String
   * stringFactoryName = "" ; String stringPercent = "" ; String[] arrayCDate =
   * convert.StringToken(exeUtil.getDateConvert(stringCDate), "/") ; String[][]
   * retDoc2M017 = getDoc2M017(booleanSource?"Doc2M017":"Doc5M027", stringBarCode)
   * ; String[][] retDoc2M018 = getDoc2M018(booleanSource?"Doc2M018":"Doc5M028",
   * stringBarCode) ; double doubleActualMoney = 0 ; Vector vectorFactoryNo = new
   * Vector( ) ; // setValue("CTimePrint", retDoc2M010[0][23].trim()) ;
   * stringPrintCount = ""+exeUtil.doParseInteger(stringPrintCount) ;
   * setValue("PrintCount", stringPrintCount) ; if("".equals(stringBarCode)) {
   * messagebox("[條碼編號] 為空白，請重新列印。") ; return false ; } setValue("BarCodePrint",
   * stringBarCode) ; setVisible("BarCodePrint",
   * stringPrevFunction.indexOf("行銷-請款申請書-承辦")==-1) ; // // 敬會單位 // 固定資產時，敬會人總室
   * DeifyDepart for(int intNo=0 ; intNo<retDoc2M018.length ; intNo++) {
   * if("B".equals(retDoc2M018[intNo][5].trim().toUpperCase())) {
   * setValue("DeifyDepart", "敬會人總室") ;
   * //if(booleanSource)getcLabel("DeifyDepart").setLocation(535,760) ; break ; }
   * } // 合約金額 setValue("ContractMoney", "") ; // 已付金額(與前期估驗數額相同)
   * setValue("PaidUpMoney", "") ; // 未付金額 setValue("NoPayMoney", "") ;
   * //System.out.println("------------------------統一編號") ; String[][] retRetainDB
   * = getDoc2M011(booleanSource?"Doc2M011":"Doc5M021", stringRetainBarCode) ;
   * if(retRetainDB.length == 0) { retRetainDB =
   * getDoc2M013(booleanSource?"Doc2M013":"Doc5M023", stringRetainBarCode) ; }
   * stringFactoryNo = retRetainDB[0][0].trim() ;
   * //System.out.println("------------------------公司名稱") ; stringCompanyName =
   * getCompanyName(stringComNo) ; if("".equals(stringCompanyName)) {
   * messagebox("[公司名稱] 為空白，請重新列印。") ; return false ; } setValue("ComNoPrint",
   * stringCompanyName) ;
   * 
   * String stringDepartNoL = getDeptCdDoc(stringDepartNo, "", "") ;
   * if(!"".equals(stringDepartNoL)) stringDepartNo = stringDepartNoL ;
   * stringDepartName = getDepartName(stringDepartNo) ;
   * //getDepartNameFED1006(stringDepartNo) ; if("".equals(stringDepartName)) {
   * messagebox("[部門名稱] 為空白，請重新列印。") ; return false ; } setValue("DepartPrint",
   * stringDepartName) ; // 承辦單位：部門代碼 if(arrayCDate.length != 3) {
   * messagebox("[日期] 錯誤，請重新列印。") ; return false ; } setValue("YearPrint",
   * arrayCDate[0]) ; // 年(452,92)：日期時間 setValue("MonthPrint", arrayCDate[1]) ; //
   * 月(493,92)：日期時間 setValue("DayPrint", arrayCDate[2]) ; // 日(565,92)：日期時間
   * setValue("DocNoPrint", stringDocNo) ; // 請款編號(891,75)：DocNo //System.out.
   * println("------------------------案別(186,159)：內業為科別，外業為案別，如為多種類時，列印 [詳附件]") ;
   * //setValue("ProjectPrint", stringProjectShow) ;
   * System.out.println("------------------------廠商名稱：由統一編號查詢廠商名稱") ; String[][]
   * retFED1005 = getFED1005(stringFactoryNo) ; if(retFED1005.length == 0) {
   * stringFactoryNo = "Z0001" ; retFED1005 = getFED1005(stringFactoryNo) ;
   * if(retFED1005.length == 0) { messagebox(" [廠商名稱] 為空白，請重新列印。") ; return false
   * ; } } setValue("FactoryNoPrint", retFED1005[0][0].trim()) ;
   * setValue("TELPrint", retFED1005[0][1].trim()) ;
   * //System.out.println("------------------------摘要、項目說明") ;
   * if("".equals(stringDescript)) { messagebox("[摘要] 為空白，請重新列印。") ; return false
   * ; } // int intLength = 100 ; String stringDescriptPrint = "" ; String
   * stringDescriptPrint2 = ""; String stringFirst = "" ; String stringRemain =
   * convert.replace(stringDescript, "\n", "") ; String[] arrayCutString =
   * exeUtil.doCutStringBySize(intLength, stringRemain) ; String[] arraydescript =
   * {"", "", "", ""} ; stringFirst = arrayCutString[0].trim( ) ; stringRemain =
   * arrayCutString[1].trim( ) ; setValue("DescriptPrint1-1", stringFirst) ; //
   * 摘要：公文內容(100*2)。 setValue("DescriptPrint1-2", stringRemain) ; //
   * 摘要：公文內容(100*2)。 // stringRemain = convert.replace(stringDescript, "\n", "") ;
   * intLength = 40 ; for(int intNo=0 ; intNo<4 ; intNo++) {
   * System.out.println(intNo+"intLength("+intLength+")stringRemain("+stringRemain
   * +")-----------------------------------") ; arrayCutString =
   * exeUtil.doCutStringBySize(intLength, stringRemain) ; stringFirst =
   * arrayCutString[0].trim( ) ; stringRemain = arrayCutString[1].trim( ) ; //
   * System.out.println(intNo+"stringFirst("+stringFirst+")stringRemain("+
   * stringRemain+")-----------------------------------") ; arraydescript[intNo] =
   * stringFirst ; if("".equals(stringRemain)) { break ; } }
   * setValue("DescriptPrint2-1", arraydescript[0]) ; // 項目說明(190,327)：公文內容。(40*4)
   * setValue("DescriptPrint2-2", arraydescript[1]) ; // 項目說明(190,327)：公文內容。(40*4)
   * setValue("DescriptPrint2-3", arraydescript[2]) ; // 項目說明(190,327)：公文內容。(40*4)
   * setValue("DescriptPrint2-4", arraydescript[3]) ; // 項目說明(190,327)：公文內容。(40*4)
   * //System.out.println("------------------------本次") ;
   * 
   * setValue("RealMoneySumPrint",
   * format.format(convert.FourToFive(stringWriteRetainMoney,0),
   * "999,999,999,999").trim( )) ; setValue("RealMoneySumPrint2",
   * format.format(convert.FourToFive(stringWriteRetainMoney,0),
   * "999,999,999,999").trim( )) ; setValue("ActualMoneyPrint",
   * format.format(convert.FourToFive(""+stringWriteRetainMoney, 0),
   * "999,999,999,999").trim( )) ; // 承辦人 及 列印日期 int intPos = 0 ; String
   * stringOriEmployeeNo = retDoc2M010[0][17].trim( ) ; String stringToday =
   * datetime.getToday("yy/mm/dd") ; // intPos = stringToday.indexOf("/") ;
   * stringToday = stringToday.substring(intPos+1) ; //
   * //setValue("OriEmployeeNo", getEmpName(stringOriEmployeeNo)) ; //
   * //setValue("PrintDateTime", stringToday) ; return true ; }
   */
  public void doRequestDefault() throws Throwable {
    String stringField = "";
    String[] arrayField = { "BorrowNo1", "BorrowNo2", "BorrowNo-1", "BorrowNo-2", "BorrowNo-3", "BorrowNo-4", "BorrowNo-5", "LastDocNo", "PurchaseNo1", "PurchaseNo2",
        "PurchaseNo3", "PurchaseNo4", "PurchaseNo5", "PurchaseNo1-1", "PurchaseNo1-2", "OptometryNo1-1", "OptometryNo1-2", "OptometryNo1", "OptometryNo2", "OptometryNo3",
        "OptometryNo4", "OptometryNo5", "No1", "No2", "No3", "No4", "ITEM1-1", "ITEM1-2", "ITEM2-1", "ITEM2-2", "ITEM3-1", "ITEM3-2", "ITEM4-1", "ITEM4-2", "SIZE1", "SIZE2",
        "SIZE3", "SIZE4", "UNIT1", "UNIT2", "UNIT3", "UNIT4", "PRICE1", "PRICE2", "PRICE3", "PRICE4", "RealMoneySumPrePercent", "RealMoneySumPrePercent-1",
        "RealMoneySumPrePercent-2", "RealMoneySumPrePercent-3", "RealMoneySumPrePrint", "RealMoneySumPrePrint-1", "RealMoneySumPrePrint-2", "RealMoneySumPrePrint-3",
        "RealMoneySumPercent", "RealMoneySumPercent-1", "RealMoneySumPercent-2", "RealMoneySumPercent-3", "RealMoneySumPrint", "RealMoneySumPrint-1", "RealMoneySumPrint-2",
        "RealMoneySumPrint-3", "RealMoneySumAddUpPercent", "RealMoneySumAddUpPercent-1", "RealMoneySumAddUpPercent-2", "RealMoneySumAddUpPercent--3", "RealMoneySumAddUpPrint",
        "RealMoneySumAddUpPrint-1", "RealMoneySumAddUpPrint-2", "RealMoneySumAddUpPrint-3", "RealMoneySumPrePrint2", "RetainMoneyPrePrint", "CheapenMoneyPrePrint",
        "ActualMoneyPrePrint", "CheapenMoneyTXT", "RealMoneySumPrePercent", "RealMoneySumPrePercent2", "RealMoneySumPercent", "RealMoneySumPercent2", "RealMoneySumAddUpPercent",
        "RealMoneySumAddUpPercent2", "DescriptPrint2-1", "DescriptPrint2-2", "DescriptPrint2-3", "DescriptPrint2-4", "ProjectNo", "ProjectPrint", "RealMoneySumAddUpPrint2",
        "RetainMoneyAddUpPrint", "CheapenMoneyAddUpPrint", "ContractNo" };
    for (int intNo = 0; intNo < arrayField.length; intNo++) {
      stringField = arrayField[intNo].trim();
      //
      if ("".equals(stringField)) continue;
      //
      setValue(stringField, "");
    }
  }

  public boolean setDataDoc2M010(String stringRealMoneySumPrint, String stringRetainMoney, String stringCheapenMoney, boolean booleanPocketMoney, String[][] retDoc2M010,
      String[][] retDoc2M011, String[][] retDoc2M012, String[][] retDoc2M013, String[][] retDoc5M0220, boolean booleanSource, boolean booleanDoc2M0143Exist, FargloryUtil exeUtil,
      talk dbDoc, String stringPrevFunction, double doubleRealMoneySumPrintPos, double doubleRealMoneySumPrintNeg) throws Throwable {
    //
    String stringDepartNo = retDoc2M010[0][12].trim();
    String stringCDate = exeUtil.getDateConvertRoc(retDoc2M010[0][16].trim());
    String stringDocNo = retDoc2M010[0][4].trim();
    String stringDescript = retDoc2M010[0][18].trim();
    String stringComNo = retDoc2M010[0][0].trim();
    setVisible("OriEmployeeNo", !"CS".equals(stringComNo));
    String stringEDateTime = retDoc2M010[0][22].trim();
    String stringWriteRetainMoney = retDoc2M010[0][25].trim();
    String stringRetainBarCode = retDoc2M010[0][26].trim();
    String stringRetainType = ""; // A:請購 B:請款
    String stringDocNoType = retDoc2M010[0][27].trim();
    String stringFactoryNoSpec = retDoc2M010[0][28].trim();
    String stringLastPayDate = retDoc2M010[0][29].trim();
    String stringKindNo = retDoc2M010[0][30].trim();
    String stringKindNoFront = "24".equals(stringKindNo) ? "17" : "15";
    String stringPurchaseNo = "";
    String stringPurchaseNo1 = "";
    String stringPurchaseNo2 = "";
    String stringPurchaseNo3 = "";
    String stringPurchaseNo4 = "";
    String stringOptometryNo = "";
    String stringBarCode = retDoc2M010[0][3].trim();
    String stringPrintCount = retDoc2M010[0][21].trim();
    String stringFactoryNo = "";
    String stringInOutFirst = "";
    String stringInOut = "";
    String stringProjectFirst = "";
    String stringProject = "";
    String stringProjectID = "";
    String stringProjectID1 = "";
    String stringProjectShow = "";
    String stringDepart = "";
    String stringCompanyName = "";
    String stringDepartName = "";
    String stringFactoryName = "";
    String stringPercent = "";
    String stringVersion = "0";
    String stringSqlAndPurchaseNo = "";
    String stringSqlAnd = " AND  (( CDate  >=  '096/10/15'  AND  ApplyType  =  'F' AND (UNDERGO_WRITE  =  'S'  OR UNDERGO_WRITE  =  'H'))  OR " + " UNDERGO_WRITE  =  'Y') "
        + " AND  KindNo  =  '" + stringKindNoFront + "' ";
    String[] arrayCDate = convert.StringToken(stringCDate, "/");
    String[][] retDoc2M010Retain = null;
    String[][] retDoc2M017 = getDoc2M017(booleanSource ? "Doc2M017" : "Doc5M027", stringBarCode);
    String[][] retDoc2M0143 = getDoc2M0143(stringBarCode);
    String[][] retDoc2M018 = getDoc2M018(booleanSource ? "Doc2M018" : "Doc5M028", stringBarCode);
    String[][] retDoc2M0111 = getDoc2M011("Doc5M0211", stringBarCode);
    double doubleActualMoney = 0;
    Vector vectorFactoryNo = new Vector();
    Vector vectorDoc3M012 = new Vector();
    Hashtable hashtableDoc3M012 = new Hashtable();
    boolean booleanSpecPurchaseNo = false;
    // 退保留方式
    if (!"".equals(stringRetainBarCode)) {
      retDoc2M010Retain = getDoc2M010(booleanSource ? "Doc2M010" : "Doc5M020", stringRetainBarCode);
      stringRetainType = (retDoc2M010Retain.length > 0) ? "B" : "A"; // A:請購 B:請款
    }
    if (retDoc5M0220 != null) {
      stringFactoryNo = retDoc5M0220[0][2].trim();
      stringFactoryNoSpec = stringFactoryNo;
    }
    if (!booleanSource) {
      stringSqlAnd = " AND  (( Unipurchase  =  'Y' AND UNDERGO_WRITE  =  'H')  OR " + " UNDERGO_WRITE  =  'Y')";
    }
    // 敬會單位
    // 固定資產時，敬會人總室 DeifyDepart
    for (int intNo = 0; intNo < retDoc2M018.length; intNo++) {
      if ("B".equals(retDoc2M018[intNo][5].trim().toUpperCase())) {
        setValue("DeifyDepart", "敬會人總室");
        // if(booleanSource)getcLabel("DeifyDepart").setLocation(535,760) ;
        break;
      }
    }
    //
    setValue("CTimePrint", retDoc2M010[0][23].trim());
    stringPrintCount = "" + exeUtil.doParseInteger(stringPrintCount);
    setValue("PrintCount", stringPrintCount);
    if ("".equals(stringBarCode)) {
      messagebox("[條碼編號] 為空白，請重新列印。");
      return false;
    }
    setValue("BarCodePrint", stringBarCode);
    setVisible("BarCodePrint", stringPrevFunction.indexOf("行銷-請款申請書-承辦") == -1);
    //
    String stringCostID = "";
    String stringCostID1 = "";
    String stringPurchaseNoExist = retDoc2M010[0][20].trim();
    if (retDoc2M012.length > 0) {
      stringCostID = retDoc2M012[0][4].trim();
      stringCostID1 = retDoc2M012[0][5].trim();
      setValue("ProjectNo", stringCostID + stringCostID1);
    }
    //
    if (stringPurchaseNoExist.startsWith("Y") && "".equals(stringRetainType)) {
      if (retDoc2M017.length == 0) {
        messagebox("查無 [請購單編號]，請重新列印。");
        return false;
      }
      //
      Vector vectorPurchaseNo = new Vector();
      for (int intNo = 0; intNo < retDoc2M017.length; intNo++) {
        stringPurchaseNo1 = retDoc2M017[intNo][0].trim();
        stringPurchaseNo2 = retDoc2M017[intNo][1].trim();
        stringPurchaseNo3 = retDoc2M017[intNo][2].trim();
        stringPurchaseNo4 = retDoc2M017[intNo][5].trim();
        stringFactoryNo = retDoc2M017[intNo][6].trim();
        stringProjectID1 = retDoc2M017[intNo][7].trim();
        //
        vectorPurchaseNo.add(stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3);
        if (!"".equals(stringSqlAndPurchaseNo)) stringSqlAndPurchaseNo += " OR ";
        //
        stringSqlAndPurchaseNo += " PurchaseNo  =  '" + stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3 + "' ";
        if (intNo == 0) {
          if (!booleanSource) {
            stringVersion = "1";
          } else {
            String[][] retDoc3M010 = getDoc3M010(stringPurchaseNo1 + stringProjectID1, stringPurchaseNo2, stringPurchaseNo3, "", stringComNo);
            if (retDoc3M010.length == 0) {
              stringVersion = "1";
            } else {
              stringVersion = "2";
              stringSqlAndPurchaseNo = "";
            }
          }
        }
      }
      //
      if (retDoc2M017.length == 1) {
        int[][] arrayPosition = { { 870, 590 }, { 0, 0 } };
        for (int intNo = 0; intNo < 2; intNo++) {
          if (intNo < retDoc2M017.length) {
            stringPurchaseNo1 = retDoc2M017[intNo][0].trim();
            stringPurchaseNo2 = retDoc2M017[intNo][1].trim();
            stringPurchaseNo3 = retDoc2M017[intNo][2].trim();
            stringPurchaseNo4 = retDoc2M017[intNo][5].trim();
            stringProjectID1 = retDoc2M017[intNo][7].trim();
            stringPurchaseNo = stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3;
            if ("".equals(stringPurchaseNo)) {
              messagebox("[請購單編號] 為空白，請重新列印。");
              return false;
            }
            getcLabel("PurchaseNo1-" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          } else {
            stringPurchaseNo = "";
          }
          setValue("PurchaseNo1-" + (intNo + 1), stringPurchaseNo);
        }
        getcLabel("PurchaseNo1-1").setLocation(870, 567);
      } else if (retDoc2M017.length == 2) {
        int[][] arrayPosition = { { 870, 570 }, { 870, 594 } };
        for (int intNo = 0; intNo < 2; intNo++) {
          if (intNo < retDoc2M017.length) {
            stringPurchaseNo1 = retDoc2M017[intNo][0].trim();
            stringPurchaseNo2 = retDoc2M017[intNo][1].trim();
            stringPurchaseNo3 = retDoc2M017[intNo][2].trim();
            stringPurchaseNo4 = retDoc2M017[intNo][5].trim();
            stringProjectID1 = retDoc2M017[intNo][7].trim();
            stringPurchaseNo = stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3;
            if ("".equals(stringPurchaseNo)) {
              messagebox("[請購單編號] 為空白，請重新列印。");
              return false;
            }
            getcLabel("PurchaseNo1-" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          } else {
            stringPurchaseNo = "";
          }
          setValue("PurchaseNo1-" + (intNo + 1), stringPurchaseNo);
        }

      } else if (retDoc2M017.length == 3 || retDoc2M017.length == 4) {
        int[][] arrayPosition = { { 870, 575 }, { 995, 575 }, { 870, 590 }, { 995, 590 } };
        for (int intNo = 0; intNo < 5; intNo++) {
          if (intNo < retDoc2M017.length) {
            stringPurchaseNo = retDoc2M017[intNo][0].trim() + retDoc2M017[intNo][7].trim() + retDoc2M017[intNo][1].trim() + retDoc2M017[intNo][2].trim();
            if ("".equals(stringPurchaseNo)) {
              messagebox("[請購單編號] 為空白，請重新列印。");
              return false;
            }
            getcLabel("PurchaseNo" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          } else {
            stringPurchaseNo = "";
          }
          setValue("PurchaseNo" + (intNo + 1), stringPurchaseNo);
        }
      } else {
        int[][] arrayPosition = { { 870, 575 }, { 995, 575 }, { 870, 590 }, { 995, 590 }, { 870, 605 } };
        for (int intNo = 0; intNo < 5; intNo++) {
          if (intNo < retDoc2M017.length) {
            stringPurchaseNo = retDoc2M017[intNo][0].trim() + retDoc2M017[intNo][7].trim() + retDoc2M017[intNo][1].trim() + retDoc2M017[intNo][2].trim();
            if ("".equals(stringPurchaseNo)) {
              messagebox("[請購單編號] 為空白，請重新列印。");
              return false;
            }
            getcLabel("PurchaseNo" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          } else {
            stringPurchaseNo = "";
          }
          setValue("PurchaseNo" + (intNo + 1), stringPurchaseNo);
        }
      }
      // 上期請款編號
      String stringLastDoc = getLastDocNo(stringComNo, stringFactoryNo, vectorPurchaseNo, stringBarCode, "", stringKindNo, booleanSource, exeUtil, dbDoc);
      setValue("LastDocNo", stringLastDoc);
      // System.out.println("------------------------驗收單編號") ;
      if (retDoc2M018.length == 0) {
        messagebox("查無 [驗收單編號]，請重新列印。");
        return false;
      }
      if (retDoc2M018.length == 1) {
        int[][] arrayPosition = { { 870, 619 }, { 0, 0 } };
        for (int intNo = 0; intNo < 2; intNo++) {
          if (intNo < retDoc2M018.length) {
            stringOptometryNo = retDoc2M018[intNo][0].trim() + retDoc2M018[intNo][6].trim() + retDoc2M018[intNo][1].trim() + retDoc2M018[intNo][2].trim();
            if ("".equals(stringOptometryNo)) {
              messagebox("[驗收單編號] 為空白，請重新列印。");
              return false;
            }
          } else {
            stringOptometryNo = "";
          }
          getcLabel("OptometryNo1-" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          setValue("OptometryNo1-" + (intNo + 1), stringOptometryNo);
        }
      } else if (retDoc2M018.length == 2) {
        int[][] arrayPosition = { { 870, 632 }, { 870, 658 } };
        for (int intNo = 0; intNo < 2; intNo++) {
          if (intNo < retDoc2M018.length) {
            stringOptometryNo = retDoc2M018[intNo][0].trim() + retDoc2M018[intNo][6].trim() + retDoc2M018[intNo][1].trim() + retDoc2M018[intNo][2].trim();
            if ("".equals(stringOptometryNo)) {
              messagebox("[驗收單編號] 為空白，請重新列印。");
              return false;
            }
            getcLabel("OptometryNo1-" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          } else {
            stringOptometryNo = "";
          }
          setValue("OptometryNo1-" + (intNo + 1), stringOptometryNo);
        }
      } else if (retDoc2M018.length == 3 || retDoc2M018.length == 4) {
        int[][] arrayPosition = { { 870, 625 }, { 995, 625 }, { 870, 642 }, { 995, 642 } };
        for (int intNo = 0; intNo < arrayPosition.length; intNo++) {
          if (intNo < retDoc2M018.length) {
            stringOptometryNo = retDoc2M018[intNo][0].trim() + retDoc2M018[intNo][6].trim() + retDoc2M018[intNo][1].trim() + retDoc2M018[intNo][2].trim();
            if ("".equals(stringOptometryNo)) {
              messagebox("[驗收單編號] 為空白，請重新列印。");
              return false;
            }
            getcLabel("OptometryNo" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          } else {
            stringOptometryNo = "";
          }
          setValue("OptometryNo" + (intNo + 1), stringOptometryNo);
        }
      } else {
        int[][] arrayPosition = { { 870, 625 }, { 995, 625 }, { 870, 642 }, { 995, 642 }, { 870, 659 } };
        for (int intNo = 0; intNo < 5; intNo++) {
          if (intNo < retDoc2M018.length) {
            stringOptometryNo = retDoc2M018[intNo][0].trim() + retDoc2M018[intNo][6].trim() + retDoc2M018[intNo][1].trim() + retDoc2M018[intNo][2].trim();
            if ("".equals(stringOptometryNo)) {
              messagebox("[驗收單編號] 為空白，請重新列印。");
              return false;
            }
            getcLabel("OptometryNo" + (intNo + 1)).setLocation(arrayPosition[intNo][0], arrayPosition[intNo][1]);
          } else {
            stringOptometryNo = "";
          }
          setValue("OptometryNo" + (intNo + 1), stringOptometryNo);
        }
      }
      // 合約金額 2016-06-04
      doSetContractNo(stringComNo, stringKindNoFront, retDoc2M017, booleanSource, exeUtil, dbDoc);
    }
    // 借款單號
    System.out.println("借款單號----------------------------------------S");
    String[][] retDoc5M0202 = getDoc5M0202(stringBarCode);
    if (retDoc5M0202.length > 0) {
      String stringBorrowNo = "";
      for (int intNo = 0; intNo < 4; intNo++) {
        stringBorrowNo = "";
        if (intNo < retDoc5M0202.length) stringBorrowNo = retDoc5M0202[intNo][1].trim();
        switch (intNo) {
        case 0:
          if (retDoc5M0202.length == 1) {
            // System.out.println("借款單號(BorrowNo"+(intNo+1)+")("+stringBorrowNo+")----------------------------------------")
            // ;
            setValue("BorrowNo" + (intNo + 1), stringBorrowNo);
          } else {
            setValue("BorrowNo-" + (intNo + 1), stringBorrowNo);
          }
          break;
        case 1:
          if (retDoc5M0202.length == 2) {
            setValue("BorrowNo" + (intNo + 1), stringBorrowNo);
          } else {
            setValue("BorrowNo-" + (intNo + 1), stringBorrowNo);
          }
          break;
        case 2:
          setValue("BorrowNo-" + (intNo + 1), stringBorrowNo);
          break;
        case 3:
          setValue("BorrowNo-" + (intNo + 1), stringBorrowNo);
          break;
        }
      }
    }
    System.out.println("借款單號----------------------------------------E");

    String stringPurchaseMoney = "";
    String stringExistRealMoney = "";
    String stringBarCodePur = "";
    String stringGroupID = "";
    String[][] retDoc3M010 = null;
    double doubleExistRealMoney = 0;
    double doubleTemp2 = 0;
    boolean booleanUniPurchase = true;
    Hashtable hashtableAnd = new Hashtable();
    if (booleanSource && "2".equals(stringVersion)) {
    } else if ("1".equals(stringVersion) || "A".equals(stringRetainType)) {
      String stringKey = "";
      String[][] retDoc3M011 = null;
      String[][] retDoc3M012 = null;
      double doublePurchaseMoney = 0;
      double doubleTemp = 0;
      //
      doubleExistRealMoney = 0;
      if (!booleanSource) {
        Vector vectorPurchaseNo = new Vector();
        if (!"".equals(stringRetainBarCode)) {
          String[][] retDoc5M011 = getDoc3M011_2(booleanSource ? "Doc3M011" : "Doc5M011", "", "", stringRetainBarCode, stringKindNoFront, "");
          vectorPurchaseNo.add(retDoc5M011[0][1].trim());
        } else {
          stringFactoryNo = retDoc2M017[0][6].trim();
          vectorPurchaseNo.add(retDoc2M017[0][0].trim() + retDoc2M017[0][7].trim() + retDoc2M017[0][1].trim() + retDoc2M017[0][2].trim());
        }
        Vector vectorAllPurchaseNo = getAllPurchaseNo(stringComNo, stringFactoryNo, vectorPurchaseNo, "", "", stringKindNo, booleanSource, dbDoc);
        Vector vectorDoc2M017 = new Vector();
        String strsingPurchaseNo = "";
        String[] arrayTemp = null;
        stringSqlAndPurchaseNo = "";
        for (int intNo = 0; intNo < vectorAllPurchaseNo.size(); intNo++) {
          strsingPurchaseNo = "" + vectorAllPurchaseNo.get(intNo);
          arrayTemp = new String[8];
          arrayTemp[0] = strsingPurchaseNo; // PurchaseNo1
          arrayTemp[1] = ""; // PurchaseNo2
          arrayTemp[2] = ""; // PurchaseNo3
          arrayTemp[3] = ""; // RetainMoney
          arrayTemp[4] = ""; // PurchaseMoney
          arrayTemp[5] = ""; // PurchaseNo4
          arrayTemp[6] = stringFactoryNo; // FactoryNo
          arrayTemp[7] = ""; // ProjectID1
          vectorDoc2M017.add(arrayTemp);
          //
          if (!"".equals(stringSqlAndPurchaseNo)) stringSqlAndPurchaseNo += " OR ";
          stringSqlAndPurchaseNo += " PurchaseNo  =  '" + strsingPurchaseNo + "' ";
        }
        retDoc2M017 = (String[][]) vectorDoc2M017.toArray(new String[0][0]);
      }
      //
      System.out.println(" doubleExistRealMoney(" + convert.FourToFive("" + doubleExistRealMoney, 0) + ")-----------------------------------------------------");
      for (int intNo = 0; intNo < retDoc2M017.length; intNo++) {
        stringPurchaseNo1 = retDoc2M017[intNo][0].trim();
        stringPurchaseNo2 = retDoc2M017[intNo][1].trim();
        stringPurchaseNo3 = retDoc2M017[intNo][2].trim();
        stringProjectID1 = retDoc2M017[intNo][7].trim();
        stringFactoryNo = retDoc2M017[intNo][6].trim();
        // 0 ComNo 1 DocNo 2 CDate 3 NeedDate 4 ApplyType
        // 5 Analysis 6 DepartNo 7 EDateTime 8 CDate 9 PrintCount
        // 10 CheckAdd 11 CheckAddDescript 12 BarCode 13 ID
        retDoc3M011 = getDoc3M011(booleanSource ? "Doc3M011" : "Doc5M011", stringComNo, stringPurchaseNo1 + stringProjectID1, stringPurchaseNo2, stringPurchaseNo3,
            stringKindNoFront, stringSqlAnd);
        if (retDoc3M011.length == 0) {
          messagebox("找不到對應的請購資料，請重新列印。");
          return false;
        }
        stringBarCodePur = retDoc3M011[0][12].trim();
        System.out.println("isSpectPurchaseNo------------------------------------------------S");
        booleanSpecPurchaseNo = isSpectPurchaseNo(stringComNo, stringKindNoFront, stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3,
            " AND  M13.GroupName  LIKE  '%#-#B' ", booleanSource, dbDoc);
        System.out.println("isSpectPurchaseNo------------------------------------------------E");
        stringGroupID = getGroupID("Doc2M0171", stringBarCode, stringBarCodePur, stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3, booleanSource,
            exeUtil, dbDoc);
        /*
         * 0 CostID 1 CostID1 2 ClassName 3 Descript 4 Unit / 5 BudgetNum 6 ActualNum 7
         * HistoryPrice 8 FactoryNo 9 ActualPrice / 10 PurchaseMoney 11 ApplyMoney 12
         * PurchaseMoney 13 ProjectID1 14 ClassNameDescript 15 RecordNo 16 DocNo
         */
        // 合約金額
        // if(!"B3018".equals(getUser())) {
        hashtableAnd.put("FactoryNo", stringFactoryNo);
        hashtableAnd.put("BarCode", stringBarCodePur);
        vectorDoc3M012 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc3M012" : "Doc5M012", hashtableAnd, "", dbDoc);
        /*
         * } else { if(!booleanSource) { retDoc3M012 =
         * getDoc3M012(booleanSource?"Doc3M012":"Doc5M012", stringBarCodePur,
         * stringFactoryNo, "") ; } else { retDoc3M012 =
         * getDoc3M013(booleanSource?"Doc3M013":"Doc5M013", stringBarCodePur,
         * stringFactoryNo) ; } }
         */
        String stringFactoryNoL = "";
        String stringGroupIDL = "";
        String stringPurchaseMoneyL = "";
        // if(!"B3018".equals(getUser())) {
        System.out.println("新版-----------------------------------------------------");
        for (int intDoc3M012 = 0; intDoc3M012 < vectorDoc3M012.size(); intDoc3M012++) {
          hashtableDoc3M012 = (Hashtable) vectorDoc3M012.get(intDoc3M012);
          if (hashtableDoc3M012 == null) continue;
          stringFactoryNoL = "" + hashtableDoc3M012.get("FactoryNo");
          stringGroupIDL = "" + hashtableDoc3M012.get("GroupID");
          stringPurchaseMoneyL = "" + hashtableDoc3M012.get("PurchaseMoney");
          //
          if (!stringFactoryNoL.equals(stringFactoryNo)) continue;
          //
          System.out.println("新版 booleanSpecPurchaseNo(" + booleanSpecPurchaseNo + ")-----------------------------------------------------0");
          if (booleanSpecPurchaseNo) {
            System.out.println("新版-----------------------------------------------------1");
            if (!stringGroupID.equals(stringGroupIDL)) continue;
          }
          System.out.println("新版-----------------------------------------------------2");
          doublePurchaseMoney += exeUtil.doParseDouble(stringPurchaseMoneyL);
        }
        System.out.println("新版 (" + doublePurchaseMoney + ")-----------------------------------------------------");
        /*
         * } else { for(int intDoc3M012=0 ; intDoc3M012<retDoc3M012.length ;
         * intDoc3M012++) { if(!booleanSource) { stringFactoryNoL =
         * retDoc3M012[intDoc3M012][8].trim() ; } else { stringFactoryNoL =
         * retDoc3M012[intDoc3M012][0].trim() ; } System.out.
         * println("舊版 -----------------------------------------------------0") ;
         * if(stringFactoryNoL.equals(stringFactoryNo)) {// if(!booleanSource) {
         * doublePurchaseMoney +=
         * exeUtil.doParseDouble(retDoc3M012[intDoc3M012][12].trim()) ; } else {
         * doublePurchaseMoney +=
         * exeUtil.doParseDouble(retDoc3M012[intDoc3M012][1].trim()) ; break ; } } }
         * System.out.println("舊版 ("+doublePurchaseMoney+
         * ")-----------------------------------------------------") ; }
         */
        //
        doubleTemp2 = getExistRealMoney(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3, stringFactoryNo,
            stringKindNo, stringBarCodePur, stringGroupID, booleanSpecPurchaseNo, booleanSource, exeUtil, dbDoc);
        System.out.println(intNo + " doubleExistRealMoney 本次(" + convert.FourToFive("" + doubleTemp2, 0) + ")-----------------------------------------------------1");
        doubleExistRealMoney += doubleTemp2;
        System.out.println(intNo + " doubleExistRealMoney(" + convert.FourToFive("" + doubleExistRealMoney, 0) + ")-----------------------------------------------------2");
      }
      stringPurchaseMoney = convert.FourToFive("" + doublePurchaseMoney, 0);
      // 合約金額
      setValue("ContractMoney", format.format(stringPurchaseMoney, "999,999,999,999").trim());
      // 已付金額、前期估驗 stringExistRealMoney stringPaidUpMoney
      stringExistRealMoney = convert.FourToFive("" + doubleExistRealMoney, 0);
      setValue("PaidUpMoney", format.format(stringExistRealMoney, "999,999,999,999").trim());
      // 未付金額 stringNoPay
      String stringNoPay = operation.floatSubtract(stringPurchaseMoney, stringExistRealMoney, 0);
      setValue("NoPayMoney", format.format(stringNoPay, "999,999,999,999").trim());
      //
      setPrintable("ContractMoney", true);
      setPrintable("PaidUpMoney", true);
      setPrintable("NoPayMoney", true);
      setPrintable("RetainMoneyAddUpPrint", true);
      setPrintable("CheapenMoneyAddUpPrint", true);
      setPrintable("CheapenMoneyAddUpPrint", true);
      setPrintable("ActualMoneyAddUpPrint", true);
    } else {
      setPrintable("ContractMoney", false);
      setPrintable("PaidUpMoney", false);
      setPrintable("NoPayMoney", false);
      // setPrintable("RetainMoneyAddUpPrint", false) ;
      // setPrintable("CheapenMoneyAddUpPrint", false) ;
      // setPrintable("ActualMoneyAddUpPrint", false) ;
    }
    System.out.println("-booleanPocketMoney(" + booleanPocketMoney + ")-stringDocNoType(" + stringDocNoType + ")-----------------------統一編號S");
    String stringTemp = "";
    String[][] retFED1005 = null;
    boolean booleanCheck = true;
    if (booleanPocketMoney && !stringPurchaseNoExist.startsWith("Y")) {
      // if(booleanPocketMoney && !"Y".equals(stringPurchaseNoExist)) {
      stringInOut = retDoc2M012[0][0].trim();
      stringFactoryNo = ("I".equals(stringInOut)) ? "Z0001" : "Z0007";
    } else {
      stringFactoryNo = stringFactoryNoSpec;
      if ("A".equals(stringDocNoType)) {
        // 一般
        // 代收代付

        String[][] retDoc5M0225 = getDoc5M0225(stringBarCode, dbDoc);
        if (retDoc5M0225.length > 0) {
          // 0 ComNo 1 BarCodeF 2 RecordNo 3 CostID 4 RealTotalMoney
          String stringComNoL = retDoc5M0225[0][0].trim();
          retFED1005 = getFE3D70(stringComNoL);
          if (retFED1005.length > 0) {
            stringFactoryNo = retFED1005[0][2].trim();
            booleanCheck = false;
          }
        }
        if ("".equals(stringFactoryNo)) {
          if (retDoc2M011 == null) retDoc2M011 = new String[0][0];
          if (retDoc2M013 == null) retDoc2M013 = new String[0][0];
          if (retDoc2M0111 == null) retDoc2M0111 = new String[0][0];
          //
          for (int intNo = 0; intNo < retDoc2M011.length; intNo++) {
            stringTemp = retDoc2M011[intNo][0].trim(); // 統一編號
            if (vectorFactoryNo.indexOf(stringTemp) == -1) vectorFactoryNo.add(stringTemp);
          }
          for (int intNo = 0; intNo < retDoc2M013.length; intNo++) {
            stringTemp = retDoc2M013[intNo][0].trim(); // 統一編號
            if (vectorFactoryNo.indexOf(stringTemp) == -1) vectorFactoryNo.add(stringTemp);
          }
          for (int intNo = 0; intNo < retDoc2M0111.length; intNo++) {
            stringTemp = retDoc2M0111[intNo][0].trim(); // 統一編號
            if (vectorFactoryNo.indexOf(stringTemp) == -1) vectorFactoryNo.add(stringTemp);
          }
          if (vectorFactoryNo.size() > 0) {
            stringFactoryNo = "" + vectorFactoryNo.get(0); // 統一編號
          }
          System.out.println("stringFactoryNo(" + stringFactoryNo + ")-----------------------------------");
        }
      } else if ("D".equals(stringDocNoType)) {
        // 金
        if (retDoc5M0202.length > 0) {
          String stringBarCodeBorrow = "";
          // stringFactoryNo = "Z0001" ;
          hashtableAnd.put("ComNo", stringComNo);
          hashtableAnd.put("KindNo", "26");
          hashtableAnd.put("DocNo", retDoc5M0202[0][1].trim());
          stringBarCodeBorrow = exeUtil.getNameUnion("BarCode", "Doc5M030", "", hashtableAnd, dbDoc);
          if (!"".equals(stringBarCodeBorrow)) {
            stringFactoryNo = exeUtil.getNameUnion("FactoryNo", "Doc5M030", " AND  BarCode  =  '" + stringBarCodeBorrow + "' ", new Hashtable(), dbDoc);
            if ("".equals(stringFactoryNo)) {
              stringFactoryNo = exeUtil.getNameUnion("FactoryNo", "Doc5M031", " AND  BarCode  =  '" + stringBarCodeBorrow + "' ", new Hashtable(), dbDoc);
            }
            if ("".equals(stringFactoryNo)) {
              stringFactoryNo = exeUtil.getNameUnion("FactoryNo", "Doc5M033", " AND  BarCode  =  '" + stringBarCodeBorrow + "' ", new Hashtable(), dbDoc);
            }
          }
        }
      } else if ("B".equals(stringDocNoType)) {
        // 逐月開發票
        stringFactoryNo = stringFactoryNoSpec;
      }
    }
    // 是否為需要控管的請款代碼
    double[] arrayMoney = { 0, 0, 0, 0 };
    if (booleanSource && !stringPurchaseNoExist.startsWith("Y")) {
      // if(booleanSource && !"Y".equals(stringPurchaseNoExist)) {
      arrayMoney = getDoc2M048(stringCDate, stringFactoryNo, stringEDateTime, retDoc2M012, exeUtil, dbDoc);
      if (arrayMoney[3] > 0) {
        // 合約金額
        setValue("ContractMoney", exeUtil.getFormatNum("" + arrayMoney[0], ""));
        // 已付金額、前期估驗
        setValue("PaidUpMoney", exeUtil.getFormatNum("" + arrayMoney[1], ""));
        System.out.println("PaidUpMoney1111111(" + exeUtil.getFormatNum("" + arrayMoney[1], "") + ")------------------------------------");
        // 未付金額
        setValue("NoPayMoney", exeUtil.getFormatNum("" + (arrayMoney[0] - arrayMoney[1]), ""));
        setPrintable("ContractMoney", true);
        setPrintable("PaidUpMoney", true);
        setPrintable("NoPayMoney", true);
      }
    }
    // System.out.println("------------------------公司名稱") ;
    stringCompanyName = getCompanyName2(stringComNo, stringBarCode, exeUtil);
    if ("".equals(stringCompanyName)) {
      messagebox("[公司名稱] 為空白，請重新列印。");
      return false;
    }
    setValue("ComNoPrint", stringCompanyName);
    // System.out.println("------------------------部門名稱") ;
    stringDepartName = getDepartName(stringDepartNo);
    if ("".equals(stringDepartName)) {
      //
      stringDepartName = getDeptName(stringDepartNo, "", "");
    }
    if ("".equals(stringDepartName)) {
      messagebox("[部門名稱] 為空白，請重新列印。");
      return false;
    }
    if ("CS".equals(stringComNo)) stringDepartName = stringDepartNo;
    setValue("DepartPrint", stringDepartName); // 承辦單位：部門代碼
    if (arrayCDate.length != 3) {
      messagebox("[日期] 錯誤，請重新列印。");
      return false;
    }
    setValue("YearPrint", arrayCDate[0]); // 年(452,92)：日期時間
    setValue("MonthPrint", arrayCDate[1]); // 月(493,92)：日期時間
    setValue("DayPrint", arrayCDate[2]); // 日(565,92)：日期時間
    setValue("DocNoPrint", stringDocNo); // 請款編號(891,75)：DocNo
    // System.out.println("------------------------案別(186,159)：內業為科別，外業為案別，如為多種類時，列印
    // [詳附件]") ;
    if (retDoc2M012.length != 0) {
      stringInOutFirst = retDoc2M012[0][0].trim(); // 內外業 2
      if ("I".equals(stringInOutFirst)) {
        stringProjectFirst = retDoc2M012[0][1].trim();
        stringProjectShow = stringProjectFirst;
      } else {
        stringProjectFirst = retDoc2M012[0][1].trim() + retDoc2M012[0][2].trim() + retDoc2M012[0][3].trim();
        stringProjectShow = retDoc2M012[0][3].trim();
      }
    }
    System.out.println("------------------------For LOOP");
    Vector vectorCostID224 = booleanSource ? getCostIDVDoc2M0201V(stringComNo, exeUtil.getDateConvert(stringCDate), " AND  FunctionName  LIKE  '%立沖傳票對應%' ")
        : getDoc2M0401V("", "D", " AND  FunctionName  LIKE  '%立沖傳票對應%' ", dbDoc); // 立沖對應傳票，允許部門、案別為空白
    for (int intRowNo = 0; intRowNo < retDoc2M012.length; intRowNo++) {
      stringInOut = retDoc2M012[intRowNo][0].trim(); // 內外業
      stringDepart = retDoc2M012[intRowNo][1].trim(); // 部門
      stringProjectID = retDoc2M012[intRowNo][2].trim(); // 大案別
      stringProjectID1 = retDoc2M012[intRowNo][3].trim(); // 小案別
      if (stringInOut.equals(stringInOutFirst)) {
        if ("I".equals(stringInOutFirst)) {
          stringProject = stringDepart;
        } else {
          stringProject = stringDepart + stringProjectID + stringProjectID1;
        }
        if (!stringProjectFirst.equals(stringProject)) {
          // 科別不相同
          stringProjectShow = "詳附件";
          // doPrintExcel(stringBarCode, stringComNo, retDoc2M011, retDoc2M012,
          // retDoc2M013, exeUtil) ;
          break;
        }
      } else {
        // 非一致的內外業
        stringProjectShow = "詳附件";
        // doPrintExcel(stringBarCode, stringComNo, retDoc2M011, retDoc2M012,
        // retDoc2M013, exeUtil) ;
        break;
      }
    }
    if (retDoc2M0143.length == 0 && retDoc2M012.length > 0 && vectorCostID224.indexOf(retDoc2M012[0][4].trim()) == -1 && "".equals(stringProjectShow)) {
      messagebox(" [案別] 為空白，請重新列印１。");
      return false;
    }
    String[][] retDoc5M0224 = getDoc5M0224(stringBarCode, dbDoc);
    setValue("ProjectPrint", retDoc5M0224.length > 0 ? "" : stringProjectShow); // 立沖傳票時，不列印部門
    // System.out.println("------------------------廠商名稱：由統一編號查詢廠商名稱") ;
    for (int intNo = 0; intNo < retDoc2M012.length; intNo++) {
      stringCostID = retDoc2M012[intNo][4].trim();
      stringCostID1 = retDoc2M012[intNo][5].trim();
      //

      if (vectorFactoryNo.size() > 1 && ",810,,".indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
        retFED1005 = new String[1][2];
        retFED1005[0][0] = "";
        retFED1005[0][1] = "";
        booleanCheck = false;
        break;
      }
      if (vectorFactoryNo.size() > 1 && ",F304101,,".indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
        retFED1005 = new String[1][2];
        retFED1005[0][0] = "";
        retFED1005[0][1] = "";
        booleanCheck = false;
        break;
      }
      if (!stringPurchaseNoExist.startsWith("Y")) {
        // if(!"Y".equals(stringPurchaseNoExist)) {
        // 多廠商，不列印廠商名稱
        if (",F304001,A010107,".indexOf("," + stringCostID + ",") != -1) {
          retFED1005 = new String[1][2];
          retFED1005[0][0] = "";
          retFED1005[0][1] = "";
          booleanCheck = false;
          break;
        }
        if (",100,101,420,".indexOf("," + stringCostID + stringCostID1 + ",") != -1) {
          retFED1005 = getFED1005("Z0001");
          booleanCheck = false;
          break;
        }
        // 2013-06-25 楊信義
        if (",A010451,A010452,A010453,A010454,A010455,A010456,".indexOf("," + stringCostID + stringCostID1 + ",") != -1 && retDoc5M0224.length > 0) {
          retFED1005 = getFED1005("G0014");
          booleanCheck = false;
          break;
        }
        // 撥補零用金
        if (",F297001,A210302,A210202,A210402,C010304,".indexOf("," + stringCostID + ",") != -1) {
          retFED1005 = getFED1005("Z0001");
          booleanCheck = false;
          break;
        }
        // 代管費用-雜支
        if (",D040321,".indexOf("," + stringCostID + ",") != -1) {
          retFED1005 = getFED1005("Z0006");
          booleanCheck = false;
          break;
        }
        // 立沖
        // A110103(辦公室押金)、A312101(退租賃保證金)、A030100(福利金提撥)
        if (",A110103,A312101,A030100,".indexOf("," + stringCostID + ",") != -1) {
          // if("F282201,F282301,F282302,F283101,F283102,F283103,F283201,F283202,F283203,F283204,F283205,F283206,F283207,F283299,F283301,F283401,F283501,F283502".indexOf(retDoc2M012[intNo][4].trim()+",")!=-1)
          // {
          // 0 VOUCHER_YMD 1 VOUCHER_FLOW_NO 2 VOUCHER_SEQ_NO 3 Amt 4 FactoryNo 5 CostID 6
          // CostID1
          if (retDoc5M0224.length > 0) {
            stringFactoryNo = retDoc5M0224[0][4].trim();
          }
          break;
        } else if (",F273701,A312103,A312104,A312105,".indexOf("," + stringCostID + ",") == -1 && vectorCostID224.indexOf(stringCostID) != -1) {
          // 非 F273701(償還銀行借款)之立沖
          // if("F282201,F282301,F282302,F283101,F283102,F283103,F283201,F283202,F283203,F283204,F283205,F283206,F283207,F283299,F283301,F283401,F283501,F283502".indexOf(retDoc2M012[intNo][4].trim()+",")!=-1)
          // {
          retFED1005 = getFED1005("G0010");
          booleanCheck = false;
          break;
        }
      }
    }
    if (booleanCheck) {
      retFED1005 = getFED1005(stringFactoryNo, stringComNo);
      if (retFED1005.length == 0) {
        stringFactoryNo = "Z0001";
        retFED1005 = getFED1005(stringFactoryNo);
        if (retFED1005.length == 0) {
          messagebox(" [廠商名稱] 為空白，請重新列印。");
          return false;
        }
      }
    }
    setValue("FactoryNoPrint", retFED1005[0][0].trim());
    setValue("TELPrint", retFED1005[0][1].trim());
    // System.out.println("------------------------摘要、項目說明") ;
    if ("".equals(stringDescript)) {
      messagebox(" [摘要] 為空白，請重新列印。");
      return false;
    }
    //
    int intLength = 100;
    String stringDescriptPrint = "";
    String stringDescriptPrint2 = "";
    String stringFirst = "";
    String stringRemain = convert.replace(stringDescript, "\n", "　");
    String[] arrayTemp = convert.StringToken(stringDescript, "\n");
    String[] arrayCutString = exeUtil.doCutStringBySize(intLength, stringRemain);
    String[] arraydescript = { "", "", "", "" };
    boolean booleanFlag = true;
    System.out.println("------------------------公文內容(" + stringDescript + ")");
    if (arrayTemp.length < 4) {
      String[] arrayTempL = { "", "" };
      for (int intNo = 0; intNo < 2; intNo++) {
        stringTemp = "";
        if ((intNo * 2) < arrayTemp.length) stringTemp += arrayTemp[intNo * 2].trim();
        if ((intNo * 2 + 1) < arrayTemp.length) stringTemp += "　" + arrayTemp[intNo * 2 + 1].trim();
        arrayTempL[intNo] = stringTemp;
        if (code.StrToByte(stringTemp).length() > intLength) {
          booleanFlag = false;
          break;
        }
      }
      if (booleanFlag) {
        arrayCutString = arrayTempL;
      }
    }
    stringFirst = arrayCutString[0].trim();
    stringRemain = arrayCutString[1].trim();
    setValue("DescriptPrint1-1", stringFirst); // 摘要：公文內容(100*2)。
    setValue("DescriptPrint1-2", stringRemain); // 摘要：公文內容(100*2)。
    stringLastPayDate = "".equals(stringLastPayDate) ? ("(最遲付款日期：無)") : ("(最遲付款日期：" + stringLastPayDate + ")");
    setValue("DescriptPrint", stringLastPayDate); //
    //
    booleanFlag = true;
    intLength = 40;

    if (arrayTemp.length < 4) {
      String[] arrayTempL = { "", "", "", "" };
      for (int intNo = 0; intNo < arrayTemp.length; intNo++) {
        stringTemp = arrayTemp[intNo];
        arrayTempL[intNo] = stringTemp;
        if (code.StrToByte(stringTemp).length() > intLength) {
          booleanFlag = false;
          break;
        }
      }
      if (booleanFlag) {
        arraydescript = arrayTempL;
      }
    } else {
      booleanFlag = false;
    }
    if (!booleanFlag) {
      stringRemain = convert.replace(stringDescript, "\n", "");
      for (int intNo = 0; intNo < 4; intNo++) {
        arrayCutString = exeUtil.doCutStringBySize(intLength, stringRemain);
        stringFirst = arrayCutString[0].trim();
        stringRemain = arrayCutString[1].trim();
        //
        arraydescript[intNo] = stringFirst;
        if ("".equals(stringRemain)) {
          break;
        }
      }
    }
    //
    System.out.println("DescriptPrint2-1(" + arraydescript[0] + ")---------------------------------1");
    setValue("DescriptPrint2-1", arraydescript[0]); // 項目說明(190,327)：公文內容。(40*4)
    setValue("DescriptPrint2-2", arraydescript[1]); // 項目說明(190,327)：公文內容。(40*4)
    setValue("DescriptPrint2-3", arraydescript[2]); // 項目說明(190,327)：公文內容。(40*4)
    setValue("DescriptPrint2-4", arraydescript[3]); // 項目說明(190,327)：公文內容。(40*4)
    System.out.println("DescriptPrint2-1(" + getValue("DescriptPrint2-1") + ")---------------------------------2");
    //
    setValue("No1", "");
    setValue("No2", "");
    setValue("No3", "");
    setValue("No4", "");
    setValue("ITEM1-1", "");
    setValue("ITEM1-2", "");
    setValue("ITEM2-1", "");
    setValue("ITEM2-2", "");
    setValue("ITEM3-1", "");
    setValue("ITEM3-2", "");
    setValue("ITEM4-1", "");
    setValue("ITEM4-2", "");
    setValue("SIZE1", "");
    setValue("SIZE2", "");
    setValue("SIZE3", "");
    setValue("SIZE4", "");
    setValue("UNIT1", "");
    setValue("UNIT2", "");
    setValue("UNIT3", "");
    setValue("UNIT4", "");
    setValue("PRICE1", "");
    setValue("PRICE2", "");
    setValue("PRICE3", "");
    setValue("PRICE4", "");
    setValue("RealMoneySumPrePercent", "");
    setValue("RealMoneySumPrePercent-1", "");
    setValue("RealMoneySumPrePercent-2", "");
    setValue("RealMoneySumPrePercent-3", "");
    setValue("RealMoneySumPrePrint", "0");
    setValue("RealMoneySumPrePrint-1", "");
    setValue("RealMoneySumPrePrint-2", "");
    setValue("RealMoneySumPrePrint-3", "");
    setValue("RealMoneySumPercent", "");
    setValue("RealMoneySumPercent-1", "");
    setValue("RealMoneySumPercent-2", "");
    setValue("RealMoneySumPercent-3", "");
    setValue("RealMoneySumPrint", "");
    setValue("RealMoneySumPrint-1", "");
    setValue("RealMoneySumPrint-2", "");
    setValue("RealMoneySumPrint-3", "");
    setValue("RealMoneySumAddUpPercent", "");
    setValue("RealMoneySumAddUpPercent-1", "");
    setValue("RealMoneySumAddUpPercent-2", "");
    setValue("RealMoneySumAddUpPercent-3", "");
    setValue("RealMoneySumAddUpPrint", "");
    setValue("RealMoneySumAddUpPrint-1", "");
    setValue("RealMoneySumAddUpPrint-2", "");
    setValue("RealMoneySumAddUpPrint-3", "");
    setValue("RealMoneySumPrePrint2", "0");
    setValue("RetainMoneyPrePrint", "");
    setValue("CheapenMoneyPrePrint", "");
    setValue("ActualMoneyPrePrint", "");
    setValue("CheapenMoneyTXT", "");
    // if("B3018".equals(getUser())) messagebox("11") ;
    System.out.println("------------------------本次");
    // 金額
    doubleActualMoney = exeUtil.doParseDouble(stringRealMoneySumPrint) - exeUtil.doParseDouble(stringRetainMoney) - exeUtil.doParseDouble(stringCheapenMoney)
        + exeUtil.doParseDouble(stringWriteRetainMoney);
    System.out.println("------------------------金額：費用合計。" + stringRealMoneySumPrint);
    if ("".equals(stringRealMoneySumPrint)) {
      messagebox(" [費用合計] 為空白，請重新列印。");
      return false;
    }
    if ("B3018".equals(getUser()))
      messagebox("001---doubleRealMoneySumPrintPos(" + doubleRealMoneySumPrintPos + ")doubleRealMoneySumPrintNeg(" + doubleRealMoneySumPrintNeg + ")----");
    setValue("No1", "1");
    setValue("RealMoneySumPrint", exeUtil.getFormatNum2("" + doubleRealMoneySumPrintPos)); // 本期估驗 金額
    setValue("RealMoneySumAddUpPrint", exeUtil.getFormatNum2("" + doubleRealMoneySumPrintPos)); // 累計估驗 金額
    if (doubleRealMoneySumPrintNeg != 0) {
      setValue("No2", "2");
      /*
       * if("".equals(getValue("DescriptPrint2-2"))) { setValue("DescriptPrint2-2",
       * getValue("DescriptPrint2-1")) ; }
       */
      setValue("RealMoneySumPrint-1", exeUtil.getFormatNum2("" + doubleRealMoneySumPrintNeg)); // 本期估驗 金額
      setValue("RealMoneySumAddUpPrint-1", exeUtil.getFormatNum2("" + doubleRealMoneySumPrintNeg)); // 累計估驗 金額
    }
    if ("B3018".equals(getUser())) messagebox("002");
    // 2011-04-01 請款代碼 009 之特別處理S
    int intCostID009Pos = 0;
    double doubleCosID009 = 0;
    if ("".equals(arraydescript[1])) {
      intCostID009Pos = 1;
    } else if ("".equals(arraydescript[2])) {
      intCostID009Pos = 2;
    } else {
      intCostID009Pos = 3;
    }
    for (int intNo = 0; intNo < retDoc2M012.length; intNo++) {
      if ("009".equals(retDoc2M012[intNo][4].trim() + retDoc2M012[intNo][5].trim()) && exeUtil.doParseDouble(retDoc2M012[intNo][7].trim()) < 0) {
        doubleCosID009 += exeUtil.doParseDouble(retDoc2M012[intNo][7].trim());
      }
    }
    if (doubleCosID009 < 0) {
      String stringRealMoneySumPrintL = "" + (exeUtil.doParseDouble(stringRealMoneySumPrint) - doubleCosID009);
      stringRealMoneySumPrintL = convert.FourToFive("" + stringRealMoneySumPrintL, 0);
      setValue("No" + (intCostID009Pos + 1), "2");
      setValue("RealMoneySumPrint-" + intCostID009Pos, exeUtil.getFormatNum2("" + doubleCosID009));
      setValue("DescriptPrint2-" + (intCostID009Pos + 1), getCostID1View(stringComNo, "009", dbDoc));
      //
      setValue("RealMoneySumPrint", exeUtil.getFormatNum2(stringRealMoneySumPrintL)); // 本期估驗 金額
    }
    // 2011-04-01 請款代碼 009 之特別處理E
    if (stringPurchaseNoExist.startsWith("Y")) {
      // if("Y".equals(stringPurchaseNoExist)) {
      // if("2".equals(stringVersion)) {
      if (!booleanUniPurchase || "1".equals(stringVersion)) {
        // if(!booleanUniPurchase && "Y".equals(stringPurchaseNoExist)) {
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumPrint) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
      }
    } else {
      if (arrayMoney[3] > 0) {
        stringPercent = convert.FourToFive("" + (arrayMoney[2] / arrayMoney[0]), 2);
      }
    }
    //
    setValue("RealMoneySumPercent", stringPercent); // 本期估驗 數量
    setValue("RealMoneySumAddUpPercent", stringPercent); // 累計估驗 數量
    System.out.println("------------------------估驗部份小計：費用合計" + stringRealMoneySumPrint);
    setValue("RealMoneySumPrint2", exeUtil.getFormatNum2(stringRealMoneySumPrint)); // 本期估驗合計 金額
    setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(stringRealMoneySumPrint)); // 累計估驗合計 金額
    setValue("RealMoneySumPercent2", stringPercent); // 本期估驗合計 數量
    setValue("RealMoneySumAddUpPercent2", stringPercent); // 累計估驗合計 數量
    if ("".equals(stringRetainMoney) || "null".equals(stringRetainMoney)) stringRetainMoney = "0";
    if (exeUtil.doParseDouble(stringRetainMoney) != 0) {
      setValue("RetainMoneyPrint", exeUtil.getFormatNum2(stringRetainMoney)); // 本期保留 金額
    } else if (exeUtil.doParseDouble(stringWriteRetainMoney) != 0) {
      setValue("RetainMoneyPrint", "-" + exeUtil.getFormatNum2(stringWriteRetainMoney)); // 本期退保留 金額
    } else {
      setValue("RetainMoneyPrint", "0"); // 本期退保留 金額
    }
    setValue("RetainMoneyAddUpPrint", exeUtil.getFormatNum2(stringRetainMoney)); // 累計保留 金額
    System.out.println("------------------------扣款金額：扣款金額。");
    if ("".equals(stringCheapenMoney) || "null".equals(stringCheapenMoney)) stringCheapenMoney = "0";
    setValue("CheapenMoneyPrint", exeUtil.getFormatNum2(stringCheapenMoney)); // 本期扣款 金額
    setValue("CheapenMoneyAddUpPrint", exeUtil.getFormatNum2(stringCheapenMoney)); // 累計扣款 金額
    //
    double doubleReceiptTax = 0;
    double doubleSupplementMoney = 0;
    if (retDoc2M013 != null) {
      for (int intNo = 0; intNo < retDoc2M013.length; intNo++) {
        doubleSupplementMoney += exeUtil.doParseDouble(retDoc2M013[intNo][9].trim());
        doubleReceiptTax += exeUtil.doParseDouble(retDoc2M013[intNo][4].trim());
      }
      stringTemp = "";

      if (doubleSupplementMoney > 0 && !booleanDoc2M0143Exist) {
        stringTemp = "稅" + exeUtil.getFormatNum2("" + doubleReceiptTax) + "+保費" + exeUtil.getFormatNum2("" + doubleSupplementMoney);
        setValue("CheapenMoneyTXT", stringTemp);
      }
    }
    // System.out.println("------------------------實付金額(562,556)：實付金額。") ;
    if (doubleActualMoney == 0) {
      messagebox(" [實付金額] 為零，請重新列印。");
      return false;
    }
    // F273701(償還銀行借款),F282201(繳納營業稅),F282301(繳納暫營所稅)
    if (!booleanSource && "F273701,F282201,F282301,".indexOf(stringCostID) == -1 && vectorCostID224.indexOf(stringCostID) != -1) {
      // if(!booleanSource &&
      // "F282302,F283101,F283102,F283103,F283201,F283202,F283203,F283204,F283205,F283206,F283207,F283299,F283301,F283401,F283501,F283502,".indexOf(stringCostID)!=-1)
      // {
      setValue("ActualMoneyPrint", exeUtil.getFormatNum2(stringRealMoneySumPrint)); // 本期實付 金額
      setValue("ActualMoneyAddUpPrint", exeUtil.getFormatNum2(stringRealMoneySumPrint)); // 累計實付 金額
    } else {
      setValue("ActualMoneyPrint", exeUtil.getFormatNum2("" + doubleActualMoney)); // 本期實付 金額
      setValue("ActualMoneyAddUpPrint", exeUtil.getFormatNum2("" + doubleActualMoney)); // 累計實付 金額
    }
    if ("2".equals(stringVersion)) {
      if ("B3018".equals(getUser())) messagebox("A");
      // Doc3M010
      if (!booleanUniPurchase && stringPurchaseNoExist.startsWith("Y")) {
        // if(!booleanUniPurchase && "Y".equals(stringPurchaseNoExist)) {
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringExistRealMoney) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
        // 前期估驗
        setValue("RealMoneySumPrePrint", exeUtil.getFormatNum2(stringExistRealMoney)); // 前期估驗 金額
        // if(booleanSource)
        setValue("RealMoneySumPrePercent", stringPercent); // 前期估驗 數量
        // 前期估驗小計
        setValue("RealMoneySumPrePrint2", exeUtil.getFormatNum2(stringExistRealMoney)); // 前期估驗合計 金額
        // if(booleanSource)
        setValue("RealMoneySumPrePercent2", stringPercent); // 前期估驗合計 數量
        // 累計估驗
        String stringRealMoneySumAddUpPrint = "" + (exeUtil.doParseDouble(stringExistRealMoney) + exeUtil.doParseDouble(stringRealMoneySumPrint));
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumAddUpPrint) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
        setValue("RealMoneySumAddUpPrint", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint)); // 累計估驗 金額
        // if(booleanSource)
        setValue("RealMoneySumAddUpPercent", stringPercent); // 累計估驗 數量
        // 累計估驗小計
        setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint)); // 累計估驗合計 金額
        // if(booleanSource)
        setValue("RealMoneySumAddUpPercent2", stringPercent); // 累計估驗合計 金額
        //
        setPrintable("RealMoneySumPrePrint", true);
        setPrintable("RealMoneySumPrePrint2", true);
        setPrintable("RealMoneySumAddUpPrint", true);
        setPrintable("RealMoneySumAddUpPrint2", true);
        setPrintable("ActualMoneyAddUpPrint", true);
      } else {
        setPrintable("RealMoneySumAddUpPercent", false);
        setPrintable("RealMoneySumPrePrint", false);
        setPrintable("RealMoneySumPrePrint2", false);
        // setPrintable("RealMoneySumAddUpPrint", false) ;
        // setPrintable("RealMoneySumAddUpPrint2", false) ;
        // setPrintable("ActualMoneyAddUpPrint", false) ;
      }
    } else if ("1".equals(stringVersion)) {
      if ("B3018".equals(getUser())) messagebox("B");
      // 前期估驗
      stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringExistRealMoney) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
      if (exeUtil.doParseDouble(stringExistRealMoney) == 0) stringExistRealMoney = "0";
      setValue("RealMoneySumPrePrint", exeUtil.getFormatNum2(stringExistRealMoney)); // 前期估驗 金額
      // if(booleanSource)
      setValue("RealMoneySumPrePercent", stringPercent); // 前期估驗 數量
      // 前期估驗小計
      setValue("RealMoneySumPrePrint2", exeUtil.getFormatNum2(stringExistRealMoney)); // 前期估驗合計 金額
      // if(booleanSource)
      setValue("RealMoneySumPrePercent2", stringPercent); // 前期估驗合計 數量
      // 累計估驗
      String stringRealMoneySumAddUpPrint = "" + (exeUtil.doParseDouble(stringExistRealMoney) + exeUtil.doParseDouble(stringRealMoneySumPrint));
      stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumAddUpPrint) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
      setValue("RealMoneySumAddUpPrint", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint)); // 累計估驗 金額
      // if(booleanSource)
      setValue("RealMoneySumAddUpPercent", stringPercent); // 累計估驗 數量
      // 累計估驗小計
      setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint)); // 累計估驗合計 金額
      // if(booleanSource)
      setValue("RealMoneySumAddUpPercent2", stringPercent); // 累計估驗合計 數量
      //
      setPrintable("RealMoneySumPrePrint", true);
      setPrintable("RealMoneySumPrePrint2", true);
      setPrintable("RealMoneySumAddUpPrint", true);
      setPrintable("RealMoneySumAddUpPrint2", true);
      setPrintable("ActualMoneyAddUpPrint", true);
    } else {
      if (arrayMoney[3] > 0) {
        // 前期估驗
        stringPercent = convert.FourToFive("" + (arrayMoney[1] / arrayMoney[0]), 2);
        setValue("RealMoneySumPrePrint", exeUtil.getFormatNum2("" + arrayMoney[1]));
        if (booleanSource) setValue("RealMoneySumPrePercent", stringPercent);
        // 前期估驗小計
        setValue("RealMoneySumPrePrint2", exeUtil.getFormatNum2("" + arrayMoney[1]));
        if (booleanSource) setValue("RealMoneySumPrePercent2", stringPercent);
        // 累計估驗
        String stringRealMoneySumAddUpPrint = "" + (arrayMoney[1] + arrayMoney[2]);
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumAddUpPrint) / arrayMoney[0]), 2);
        setValue("RealMoneySumAddUpPrint", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint));
        if (booleanSource) setValue("RealMoneySumAddUpPercent", stringPercent);
        // 累計估驗小計
        setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint));
        if (booleanSource) setValue("RealMoneySumAddUpPercent2", stringPercent);
        //
        setPrintable("RealMoneySumPrePrint", true);
        setPrintable("RealMoneySumPrePrint2", true);
        setPrintable("RealMoneySumAddUpPrint", true);
        setPrintable("RealMoneySumAddUpPrint2", true);
        setPrintable("ActualMoneyAddUpPrint", true);
        if ("B3018".equals(getUser())) messagebox("C1");
      } else {
        // 前期估驗
        setPrintable("RealMoneySumAddUpPercent", false);
        setPrintable("RealMoneySumPrePrint", false);
        // 前期估驗小計
        setPrintable("RealMoneySumPrePrint2", false);
        if (booleanSource) {
          setPrintable("RealMoneySumAddUpPrint", false); // 累計估驗
          // setPrintable("RealMoneySumAddUpPrint2", false) ; // 累計估驗小計
          // setPrintable("ActualMoneyAddUpPrint", false) ;
        } else {
          setPrintable("RealMoneySumAddUpPrint2", true); // 累計估驗
          setPrintable("RealMoneySumAddUpPrint2", true); // 累計估驗小計
          setPrintable("ActualMoneyAddUpPrint", true);
        }
        if ("B3018".equals(getUser())) messagebox("C2");
      }
    }
    if (!stringPurchaseNoExist.startsWith("Y")) {
      // if(!"Y".equals(stringPurchaseNoExist)) {
      // setPrintable("RealMoneySumAddUpPrint-1", false) ;
      // setPrintable("RealMoneySumAddUpPrint-2", false) ;
      // setPrintable("RealMoneySumAddUpPrint-3", false) ;
    }
    //
    String stringTXT = "";
    boolean booleanSumPrint = false;
    Vector vectorKey = new Vector(); // 部門(案別) + 請款代碼
    boolean booleanCostIDOnly = false;
    stringPercent = "";
    // A 請購 B 請款
    if ((stringPurchaseNoExist.startsWith("Y") || "A".equals(stringRetainType))) {
      setValue("RealMoneySumPrePercent", ""); // 前期估驗 數量
      setValue("RealMoneySumPrePercent2", ""); // 前期估驗合計 數量
      setValue("RealMoneySumPercent", ""); // 本期估驗 數量
      setValue("RealMoneySumPercent2", ""); // 本期估驗合計 數量
      setValue("RealMoneySumAddUpPercent", ""); // 累計估驗 數量
      setValue("RealMoneySumAddUpPercent2", ""); // 累計估驗合計 數量
      if (!booleanSource) {
        // 管理費用
        vectorDoc3M012 = new Vector();
        hashtableDoc3M012 = new Hashtable();
        String[] arrayTempL = null;
        System.out.println("getDoc3M012-----------------------------------------S");
        String[][] retDoc3M012 = getDoc3M012(booleanSource, stringComNo, stringFactoryNo, stringCDate, stringSqlAndPurchaseNo.replaceAll("PurchaseNo", "DocNo"), exeUtil);
        System.out.println("getDoc3M012-----------------------------------------E");
        String stringKey = "";
        for (int intNo = 0; intNo < retDoc3M012.length; intNo++) {
          stringKey = retDoc3M012[intNo][16].trim() + retDoc3M012[intNo][15].trim();
          //
          arrayTempL = (String[]) hashtableDoc3M012.get(stringKey);
          if (arrayTempL == null) {
            arrayTempL = retDoc3M012[intNo];
            vectorDoc3M012.add(stringKey);
            hashtableDoc3M012.put(stringKey, arrayTempL);
          } else {
            arrayTempL[17] = "" + (exeUtil.doParseDouble(arrayTempL[17].trim()) + exeUtil.doParseDouble(retDoc3M012[intNo][17].trim()));
          }
        }
        // 請購單 列印
        vectorKey = doPrintPurchaseExist(stringRetainType, stringComNo, stringBarCode, stringEDateTime, stringFactoryNo, stringSqlAndPurchaseNo, stringKindNo, stringKindNoFront,
            retDoc2M012, retDoc2M017, vectorDoc3M012, hashtableDoc3M012, exeUtil, dbDoc);
      } else {
        // if("B3018".equals(getUser())) messagebox("D2") ;
        // 2012/08/08 保留金額修正 RetainMoneyPrePrint
        String stringSql = "";
        String stringRetainMoneyL = getValue("RetainMoneyPrint").replaceAll(",", "");
        String stringPreRetainMoneyL = "";
        String stringAddUpRetainMoneyL = "";
        //
        System.out.println("保留金額修正----------------------------");
        stringSql = " SELECT  SUM(M10.RetainMoney) " + " FROM  Doc2M010  M10,  Doc2M017 M17 " + " WHERE  M10.BarCode  =  M17.BarCode " + " AND  EDateTime  <  '" + stringEDateTime
            + "' " + " AND  ComNo  =  '" + stringComNo + "' " + " AND  KindNo  =  '" + stringKindNo + "' " + " AND (" + stringSqlAndPurchaseNo + ")";
        stringPreRetainMoneyL = dbDoc.queryFromPool(stringSql)[0][0];
        //
        if (exeUtil.doParseDouble(stringPreRetainMoneyL) <= 0) stringPreRetainMoneyL = "0";
        setValue("RetainMoneyPrePrint", exeUtil.getFormatNum2(stringPreRetainMoneyL));
        stringAddUpRetainMoneyL = "" + (exeUtil.doParseDouble(stringPreRetainMoneyL) + exeUtil.doParseDouble(stringRetainMoneyL));
        setValue("RetainMoneyAddUpPrint", exeUtil.getFormatNum2(stringAddUpRetainMoneyL));
        // 2011/12/02 行銷
        double doubleTemp = 0;
        // 前期－實付金額
        doubleTemp = exeUtil.doParseDouble(getValue("RealMoneySumPrePrint2").replaceAll(",", "")) - exeUtil.doParseDouble(getValue("RetainMoneyPrePrint").replaceAll(",", ""))
            - exeUtil.doParseDouble(getValue("CheapenMoneyPrePrint").replaceAll(",", ""));
        stringTemp = exeUtil.getFormatNum2("" + doubleTemp);
        setValue("ActualMoneyPrePrint", stringTemp);
        // 本期－實付金額
        doubleTemp = exeUtil.doParseDouble(getValue("RealMoneySumPrint2").replaceAll(",", "")) - exeUtil.doParseDouble(getValue("RetainMoneyPrint").replaceAll(",", ""))
            - exeUtil.doParseDouble(getValue("CheapenMoneyPrint").replaceAll(",", ""));
        stringTemp = exeUtil.getFormatNum2("" + doubleTemp);
        setValue("ActualMoneyPrint", stringTemp);
        // 累計－實付金額
        doubleTemp = exeUtil.doParseDouble(getValue("RealMoneySumAddUpPrint2").replaceAll(",", "")) - exeUtil.doParseDouble(getValue("RetainMoneyAddUpPrint").replaceAll(",", ""))
            - exeUtil.doParseDouble(getValue("CheapenMoneyAddUpPrint").replaceAll(",", ""));
        stringTemp = exeUtil.getFormatNum2("" + doubleTemp);
        setValue("ActualMoneyAddUpPrint", stringTemp);
      }
    } else {
      if (!stringPurchaseNoExist.startsWith("Y")) {
        // if(!"Y".equals(stringPurchaseNoExist)){
        double doubleTemp = 0;
        // 前期－實付金額
        /*
         * doubleTemp =
         * exeUtil.doParseDouble(getValue("RealMoneySumPrePrint2").replaceAll(",", ""))
         * - exeUtil.doParseDouble(getValue("RetainMoneyPrePrint").replaceAll(",", ""))
         * - exeUtil.doParseDouble(getValue("CheapenMoneyPrePrint").replaceAll(",",
         * "")); stringTemp = exeUtil.getFormatNum2(""+doubleTemp) ;
         * setValue("ActualMoneyPrePrint", stringTemp) ;
         */
        // 本期－實付金額
        doubleTemp = exeUtil.doParseDouble(getValue("RealMoneySumPrint2").replaceAll(",", "")) - exeUtil.doParseDouble(getValue("RetainMoneyPrint").replaceAll(",", ""))
            - exeUtil.doParseDouble(getValue("CheapenMoneyPrint").replaceAll(",", ""));
        stringTemp = exeUtil.getFormatNum2("" + doubleTemp);
        setValue("ActualMoneyPrint", stringTemp);
        // 累計－實付金額
        doubleTemp = exeUtil.doParseDouble(getValue("RealMoneySumAddUpPrint2").replaceAll(",", "")) - exeUtil.doParseDouble(getValue("RetainMoneyAddUpPrint").replaceAll(",", ""))
            - exeUtil.doParseDouble(getValue("CheapenMoneyAddUpPrint").replaceAll(",", ""));
        stringTemp = exeUtil.getFormatNum2("" + doubleTemp);
        setValue("ActualMoneyAddUpPrint", stringTemp);
      }
    }
    if (retDoc2M012.length > 0) {
      // if("B3018".equals(getUser())) messagebox("22") ;
      String stringField = "RealMoneySumPrint";
      String stringFieldAddUp = "";
      String[][] retDoc5M0221 = null;
      stringCostID = retDoc2M012[0][4].trim();

      // 2013-08-09 立沖請款代碼數 START
      int intOtherTable2Row = 0; // 由立沖判斷 Table2(Doc2M012) 的數目，如果 Table2 大於
      String stringCostIDL = "";
      String stringCostID1L = "";
      String stringKeyL = "";
      String stringRealTotalMoney = "";
      Vector vectorCostID = new Vector();
      for (int intNo = 0; intNo < retDoc5M0224.length; intNo++) {
        stringCostIDL = retDoc5M0224[intNo][5].trim();
        stringCostID1L = retDoc5M0224[intNo][6].trim();
        stringKeyL = stringCostIDL + "%-%" + stringCostID1L;
        //
        if (vectorCostID.indexOf(stringKeyL) == -1) vectorCostID.add(stringKeyL);
      }
      intOtherTable2Row = retDoc2M012.length - vectorCostID.size();
      if (booleanSource || intOtherTable2Row <= 0 || retDoc5M0224.length == 0) intOtherTable2Row = 0;
      // 2013-08-09 立沖請款代碼數 END

      if (!booleanSource && "A170701".equals(stringCostID)) { // 銀行利息費用
        retDoc5M0221 = getDoc5M0221(stringBarCode, dbDoc);
        if (retDoc5M0221.length < 5) {
          setValue("DescriptPrint2-1", "");
          setValue("DescriptPrint2-2", "");
          setValue("DescriptPrint2-3", "");
          setValue("DescriptPrint2-4", "");
          // 0 BorrowAmt 1 DateStart 2 DateEnd 3 AccrualRate 4 Formula 5 Accrual
          for (int intNo = 0; intNo < retDoc5M0221.length; intNo++) {
            setValue("No" + (intNo + 1), "" + (intNo + 1));
            //
            stringTemp = exeUtil.getFormatNum2(retDoc5M0221[intNo][5].trim());
            stringField = "RealMoneySumPrint" + ((intNo == 0) ? "" : "-" + intNo);
            stringFieldAddUp = "RealMoneySumAddUpPrint" + ((intNo == 0) ? "" : "-" + intNo);
            setValue(stringField, stringTemp);
            setValue(stringFieldAddUp, stringTemp);
            //
            stringTemp = exeUtil.getFormatNum2(retDoc5M0221[intNo][0].trim()) + "(" + exeUtil.getDateConvertRoc(retDoc5M0221[intNo][1].trim()).replaceAll("/", "") + "～"
                + exeUtil.getDateConvertRoc(retDoc5M0221[intNo][2].trim()).replaceAll("/", "") + ")" + " * " + getDeleteZero(retDoc5M0221[intNo][3].trim(), exeUtil) + " %" + " * "
                + retDoc5M0221[intNo][4].trim() + " ";
            stringField = "DescriptPrint2-" + (intNo + 1);
            // System.out.println("("+stringField+")---------------------("+stringTemp+")");
            setValue(stringField, stringTemp);
          }
          booleanSumPrint = false;
        } else {
          booleanSumPrint = true;
          booleanCostIDOnly = true;
        }
      } else if (!booleanSource && "F293001".equals(stringCostID)) { // 購買基金
        // 申購基金
        String[][] retDoc5M0222 = getDoc5M0222(stringBarCode, dbDoc);
        if (retDoc5M0222.length < 5) {
          // 0 InvestmentTrust 1 FundNo 2 BandNo 3 AccountNo 4 AccountName
          // 5 Amt 6 Unit 7 NetAmt
          setValue("DescriptPrint2-1", "");
          setValue("DescriptPrint2-2", "");
          setValue("DescriptPrint2-3", "");
          setValue("DescriptPrint2-4", "");
          for (int intNo = 0; intNo < retDoc5M0222.length; intNo++) {
            setValue("No" + (intNo + 1), "" + (intNo + 1));
            //
            stringTemp = exeUtil.getFormatNum2(retDoc5M0222[intNo][5].trim());
            stringField = "RealMoneySumPrint" + ((intNo == 0) ? "" : "-" + intNo);
            stringFieldAddUp = "RealMoneySumAddUpPrint" + ((intNo == 0) ? "" : "-" + intNo);
            setValue(stringField, stringTemp);
            setValue(stringFieldAddUp, stringTemp);
            //
            stringTemp = retDoc5M0222[intNo][0].trim() + // 投信公司
                retDoc5M0222[intNo][1].trim() + // 基金統一編號
                retDoc5M0222[intNo][2].trim() + // 匯款銀行
                retDoc5M0222[intNo][3].trim() + // 匯款帳號
                retDoc5M0222[intNo][4].trim(); // 匯款帳號
            stringField = "DescriptPrint2-" + (intNo + 1);
            setValue(stringField, stringTemp);
          }
        } else {
          booleanCostIDOnly = true;
        }
      } else if (!booleanSource && "F273701".equals(stringCostID)) { // 償還銀行借款
        intOtherTable2Row = 0; // 2013-08-09 B3018 intOtherTable2Row 不作處理。
        // 償還銀行借款
        // 0 VOUCHER_YMD 1 VOUCHER_FLOW_NO 2 VOUCHER_SEQ_NO 3 Amt 4 FactoryNo 5 CostID 6
        // CostID1
        if (retDoc5M0224.length < 5) {
          setValue("DescriptPrint2-1", "");
          setValue("DescriptPrint2-2", "");
          setValue("DescriptPrint2-3", "");
          setValue("DescriptPrint2-4", "");
          // String stringTXT = getCost4Name("", stringCostID, dbDoc) ;
          String stringVoucherYMD = "";
          String stringVoucherFlowNo = "";
          String stringVoucherSeqNo = "";
          String[][] retFED1012 = null;
          for (int intNo = 0; intNo < retDoc5M0224.length; intNo++) {
            stringVoucherYMD = retDoc5M0224[intNo][0].trim();
            stringVoucherFlowNo = retDoc5M0224[intNo][1].trim();
            stringVoucherSeqNo = retDoc5M0224[intNo][2].trim();
            retFED1012 = getFED1012(stringVoucherYMD, stringVoucherFlowNo, stringVoucherSeqNo, stringComNo, "0");
            if (retFED1012.length == 0) continue;
            //
            setValue("No" + (intNo + 1), "" + (intNo + 1));
            // 0 DEPT_CD 1 AMT 2 DESCRIPTION_2
            stringTemp = exeUtil.getFormatNum2(retDoc5M0224[intNo][3].trim());
            stringField = "RealMoneySumPrint" + ((intNo == 0) ? "" : "-" + intNo);
            stringFieldAddUp = "RealMoneySumAddUpPrint" + ((intNo == 0) ? "" : "-" + intNo);
            setValue(stringField, stringTemp);
            setValue(stringFieldAddUp, stringTemp);
            //
            stringTemp = "" + (exeUtil.doParseDouble(retFED1012[0][1].trim())
                - getAmtDoc5M0224(stringVoucherYMD, stringVoucherFlowNo, stringVoucherSeqNo, stringBarCode, stringEDateTime, stringComNo, exeUtil, dbDoc));
            if ("01---971001---19---2".equals(stringComNo + "---" + stringVoucherYMD + "---" + stringVoucherFlowNo + "---" + stringVoucherSeqNo)) {
              // 650000000 不在我的系統內
              stringTemp = "" + (exeUtil.doParseDouble(stringTemp) - exeUtil.doParseDouble("650000000"));
            }
            stringTemp = exeUtil.getFormatNum2(stringTemp);
            stringTemp = retFED1012[0][0].trim() + "借款餘額" + stringTemp + "元(" + retFED1012[0][2].trim() + ")";
            stringField = "DescriptPrint2-" + (intNo + 1);
            setValue(stringField, stringTemp);
          }
        } else {
          booleanCostIDOnly = true;
        }
      } else if (!booleanSource && "F282302,F282303,".indexOf(stringCostID) != -1 && retDoc5M0224.length > 0) { // 立沖對應傳票
        String stringUserL = getUser();
        // 2013-05-31 楊信義
        int intPos = 0;
        String stringFactoryNoL = "";
        String stringReceiptTotalMoneyL = "";
        String stringReceiptTotalMoney13L = "";
        String stringTempL = "";
        Vector vectorFactoryNo12L = new Vector();
        Vector vectorFactoryNo13L = new Vector();
        Hashtable hashtablefactoryNo12L = new Hashtable();
        Hashtable hashtablefactoryNo13L = new Hashtable();
        double doubleTempL = 0;
        double doubleSumL = 0;
        for (int intNo = 0; intNo < retDoc2M013.length; intNo++) {
          stringFactoryNoL = retDoc2M013[intNo][0].trim();
          stringReceiptTotalMoneyL = retDoc2M013[intNo][5].trim();
          //
          if (vectorFactoryNo13L.indexOf(stringFactoryNoL) == -1) vectorFactoryNo13L.add(stringFactoryNoL);
          //
          doubleTempL = exeUtil.doParseDouble(stringReceiptTotalMoneyL) + exeUtil.doParseDouble("" + hashtablefactoryNo13L.get(stringFactoryNoL));
          hashtablefactoryNo13L.put(stringFactoryNoL, convert.FourToFive("" + doubleTempL, 0));
        }
        for (int intNo = 0; intNo < retDoc2M012.length; intNo++) {
          stringFactoryNoL = retDoc2M012[intNo][4].trim();
          stringReceiptTotalMoneyL = retDoc2M012[intNo][7].trim();
          //
          if (vectorFactoryNo12L.indexOf(stringFactoryNoL) == -1) vectorFactoryNo12L.add(stringFactoryNoL);
          //
          doubleTempL = exeUtil.doParseDouble(stringReceiptTotalMoneyL) + exeUtil.doParseDouble("" + hashtablefactoryNo12L.get(stringFactoryNoL));
          hashtablefactoryNo12L.put(stringFactoryNoL, convert.FourToFive("" + doubleTempL, 0));
        }
        for (int intNo = 0; intNo < vectorFactoryNo12L.size(); intNo++) {
          stringFactoryNoL = "" + vectorFactoryNo12L.get(intNo);
          stringReceiptTotalMoneyL = "" + hashtablefactoryNo12L.get(stringFactoryNoL);
          stringReceiptTotalMoney13L = "" + hashtablefactoryNo13L.get(stringFactoryNoL);
          //
          if (vectorFactoryNo13L.indexOf(stringFactoryNoL) != -1) vectorFactoryNo13L.remove(stringFactoryNoL);
          //
          stringFieldAddUp = "RealMoneySumAddUpPrint" + ((intPos == 0) ? "" : "-" + intPos);
          stringTemp = "RealMoneySumPrint" + ((intPos == 0) ? "" : "-" + intPos);
          // 以費用表格為主，列印資料
          doubleSumL += exeUtil.doParseDouble(stringReceiptTotalMoneyL);
          intPos++;
          System.out.println("intNo(" + intNo + ")intPos(" + intPos + ")-------------------------------------------");
          setValue("No" + intPos, "" + intPos);
          setValue("DescriptPrint2-" + intPos, stringFactoryNoL + "||" + getCost4Name("", stringFactoryNoL, "", dbDoc));
          setValue(stringTemp, exeUtil.getFormatNum2(stringReceiptTotalMoneyL) + "");
          setValue(stringFieldAddUp, exeUtil.getFormatNum2(stringReceiptTotalMoneyL) + "");
          // 列印 差額
          // F282302 繳納營所稅 無營業稅憑證 不等於 費用表格時，增加 營所稅(高)低估。
          // F282303 未分配盈餘 無營業稅憑證 不等於 費用表格時，增加 未分配盈餘(高)低估。
          doubleTempL = exeUtil.doParseDouble(stringReceiptTotalMoney13L) - exeUtil.doParseDouble(stringReceiptTotalMoneyL);
          doubleTempL = exeUtil.doParseDouble(convert.FourToFive("" + doubleTempL, 0));
          System.out.println("doubleTempL(" + doubleTempL + ")stringReceiptTotalMoney13L(" + stringReceiptTotalMoney13L + ")stringReceiptTotalMoneyL(" + stringReceiptTotalMoneyL
              + ")-------------------------------------------");
          if (doubleTempL != 0) {
            stringFieldAddUp = "RealMoneySumAddUpPrint" + ((intPos == 0) ? "" : "-" + intPos);
            stringTemp = "RealMoneySumPrint" + ((intPos == 0) ? "" : "-" + intPos);
            //
            if ("F282302".equals(stringFactoryNoL)) {
              if (doubleTempL > 0) {
                stringTempL = "營所稅低估";
              } else {
                stringTempL = "營所稅高估";
              }
            } else if ("F282303".equals(stringFactoryNoL)) {
              if (doubleTempL > 0) {
                stringTempL = "未分配盈餘低估";
              } else {
                stringTempL = "未分配盈餘高估";
              }
            } else {
              stringTempL = "";
            }
            //
            doubleSumL += doubleTempL;
            intPos++;
            setValue("No" + intPos, "" + intPos);
            setValue("DescriptPrint2-" + intPos, stringTempL);
            setValue(stringTemp, exeUtil.getFormatNum2(convert.FourToFive("" + doubleTempL, 0)) + "");
            setValue(stringFieldAddUp, exeUtil.getFormatNum2(convert.FourToFive("" + doubleTempL, 0)) + "");
          }
        }
        for (int intNo = 0; intNo < vectorFactoryNo13L.size(); intNo++) {
          stringFactoryNoL = "" + vectorFactoryNo13L.get(intNo);
          stringReceiptTotalMoneyL = "" + hashtablefactoryNo13L.get(stringFactoryNoL);
          doubleSumL += exeUtil.doParseDouble(stringReceiptTotalMoneyL);
          // 列印 差額
          stringFieldAddUp = "RealMoneySumAddUpPrint-" + intPos;
          stringTemp = "RealMoneySumPrint-" + intPos;
          //
          if ("F282302".equals(stringFactoryNoL)) {
            stringTempL = "營所稅(高)低估";
          } else if ("F282303".equals(stringFactoryNoL)) {
            stringTempL = "未分配盈餘(高)低估";
          } else {
            stringTempL = "";
          }
          //
          intPos++;
          setValue("No" + intPos, "" + intPos);
          setValue("DescriptPrint2-" + intPos, stringTempL);
          setValue(stringTemp, exeUtil.getFormatNum2(stringReceiptTotalMoneyL));
          setValue(stringFieldAddUp, exeUtil.getFormatNum2(stringReceiptTotalMoneyL));
        }
        stringTempL = convert.FourToFive("" + doubleSumL, 0);
        setValue("RealMoneySumPrint2", exeUtil.getFormatNum2(stringTempL));
        setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(stringTempL));
        setValue("ActualMoneyPrint", exeUtil.getFormatNum2(stringTempL));
        setValue("ActualMoneyAddUpPrint", exeUtil.getFormatNum2(stringTempL));
      } else if (!booleanSource && vectorCostID224.indexOf(stringCostID) != -1) { // 立沖對應傳票
        String stringAmtL = "";
        String stringFactoryNoL = "";
        String[] arrayTempL = null;
        Vector vectorTableDataT = new Vector();
        Vector vectorKeyT = new Vector();
        //
        if ("B3018".equals(getUser())) messagebox("一般 立沖對應傳票 1");
        intOtherTable2Row = 0;
        // 0 VOUCHER_YMD 1 VOUCHER_FLOW_NO 2 VOUCHER_SEQ_NO 3 Amt 4 FactoryNo 5 CostID 6
        // CostID1
        for (int intNo = 0; intNo < retDoc5M0224.length; intNo++) {
          stringFactoryNoL = retDoc5M0224[intNo][4].trim();
          stringCostIDL = retDoc5M0224[intNo][5].trim();
          stringCostID1L = retDoc5M0224[intNo][6].trim();
          stringAmtL = retDoc5M0224[intNo][3].trim();
          stringKeyL = stringFactoryNoL + "%-%" + stringCostIDL + "%-%" + stringCostID1L + "%-%" + (exeUtil.doParseDouble(stringAmtL) > 0 ? "A" : "B");
          //
          if (vectorKeyT.indexOf(stringKeyL) == -1) {
            vectorTableDataT.add(retDoc5M0224[intNo]);
            vectorKeyT.add(stringKeyL);
          } else {
            arrayTempL = (String[]) vectorTableDataT.get(vectorKeyT.indexOf(stringKeyL));
            arrayTempL[3] = "" + (exeUtil.doParseDouble(arrayTempL[3].trim()) + exeUtil.doParseDouble(retDoc5M0224[intNo][3].trim()));
          }
        }
        stringTXT = getCost4Name("", stringCostID, "", dbDoc);
        if ("B3018".equals(getUser())) messagebox("vectorTableDataT(" + vectorTableDataT.size() + ")---intOtherTable2Row(" + intOtherTable2Row + ")2");
        if (vectorTableDataT.size() + intOtherTable2Row < 4) { // 2013-08-09 增加立沖外額外資料 intOtherTable2Row
          booleanSumPrint = false;
          retDoc5M0224 = (String[][]) vectorTableDataT.toArray(new String[0][0]);
          //
          setValue("DescriptPrint2-1", "");
          setValue("DescriptPrint2-2", "");
          setValue("DescriptPrint2-3", "");
          setValue("DescriptPrint2-4", "");
          for (int intNo = 0; intNo < retDoc5M0224.length; intNo++) {
            if (intNo > 3) continue;
            //
            setValue("No" + (intNo + 1), "" + (intNo + 1));
            //
            stringCostID = retDoc5M0224[intNo][5].trim() + retDoc5M0224[intNo][6].trim();
            stringTXT = getCost4Name("", stringCostID, "", dbDoc);
            setValue("DescriptPrint2-" + (intNo + 1), getObjectNameFED1005(retDoc5M0224[intNo][4].trim()) + " " + stringTXT + "(詳附件)");
            //
            if (intNo > 0) {
              stringTemp = "RealMoneySumPrint-" + intNo;
            } else {
              stringTemp = "RealMoneySumPrint";
            }
            stringFieldAddUp = "RealMoneySumAddUpPrint" + ((intNo == 0) ? "" : "-" + intNo);
            setValue(stringTemp, exeUtil.getFormatNum2(retDoc5M0224[intNo][3].trim()));
            setValue(stringFieldAddUp, exeUtil.getFormatNum2(retDoc5M0224[intNo][3].trim()));
          }
        } else {
          booleanCostIDOnly = true;
          booleanSumPrint = true;
        }
      } else {
        intOtherTable2Row = 0;
        if (!stringPurchaseNoExist.startsWith("Y")) {
          // if(!"Y".equals(stringPurchaseNoExist)){
          booleanSumPrint = true;
        }
      }
      //
      System.out.println("TEST--(" + booleanSumPrint + ")(" + intOtherTable2Row + ")");
      if (intOtherTable2Row > 0) {
        if ("B3018".equals(getUser())) messagebox("intOtherTable2Row(" + intOtherTable2Row + ")-----------3");
        // 2013-08-09 列印 立沖之外的請款代碼資料 START F282201 繳納營業稅(F289991稅務罰款)
        int intPosL = retDoc5M0224.length;
        String stringFactoryNoL = "";
        if (retDoc2M013.length > 0) {
          stringFactoryNoL = retDoc2M013[retDoc2M013.length - 1][0].trim();
          stringFactoryNoL = getObjectNameFED1005(stringFactoryNoL);
          if ("".equals(stringFactoryNoL)) {
            stringFactoryNoL = getValue("FactoryNoPrint");
          }
        } else {
          stringFactoryNoL = getValue("FactoryNoPrint");
        }
        // if("B3018".equals(getUser())) messagebox("666") ;
        for (int intNo = intOtherTable2Row; intNo < retDoc2M012.length; intNo++) {
          stringCostIDL = retDoc2M012[intNo][4].trim();
          stringTXT = getCost4Name("", stringCostID, "", dbDoc);
          stringRealTotalMoney = retDoc2M012[intNo][7].trim();
          //
          intPosL++;
          setValue("No" + intPosL, "" + intPosL);
          setValue("DescriptPrint2-" + intPosL, stringFactoryNoL + " " + stringTXT);
          setValue("RealMoneySumPrint-" + (intPosL - 1), exeUtil.getFormatNum2(stringRealTotalMoney));
          setValue("RealMoneySumAddUpPrint-" + (intPosL - 1), exeUtil.getFormatNum2(stringRealTotalMoney));
        }
        // 2013-08-09 列印 立沖之外的請款代碼資料 END
      } else if (booleanSumPrint) {
        if ("B3018".equals(getUser())) messagebox("intOtherTable2Row(" + intOtherTable2Row + ")-----------4");
        String strinProjectID1L = "";
        String strinDepartNoL = "";
        String strinKeyL = "";
        String stringLimit = "%---%";
        vectorCostID = new Vector();
        Hashtable hashtableMoney = new Hashtable();
        // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
        // 5 CostID1 6 RealMoney 7 RealTotalMoney
        boolean booleanSame = true;
        String stringInOutL = "";
        String stringInOutOldL = "";
        String stringDepartNoL = "";
        String stringProjectID1L = "";
        String stringDepartNoOldL = "";
        boolean booleanCostID013 = false;
        for (int i = 0; i < retDoc2M012.length; i++) {
          stringInOutL = retDoc2M012[i][0].trim();
          stringDepartNoL = retDoc2M012[i][1].trim();
          stringProjectID1L = retDoc2M012[i][3].trim();
          stringCostIDL = retDoc2M012[i][4].trim();
          stringCostID1L = (booleanSource) ? retDoc2M012[i][5].trim() : "";
          //
          if ("013".equals(stringCostIDL + stringCostID1L)) booleanCostID013 = true;
          //
          if (!"".equals(stringProjectID1L)) stringDepartNoL = stringProjectID1L;
          if (i != 0) {
            if (!stringInOutOldL.equals(stringInOutL)) {
              booleanSame = false;
              break;
            }
            if (!stringDepartNoOldL.equals(stringDepartNoL)) {
              booleanSame = false;
              break;
            }
          }
          stringInOutOldL = stringInOutL;
          stringDepartNoOldL = stringDepartNoL;
        }
        String stringFieldValue = "";
        String stringFieldName1 = "";
        String stringFieldName = "";
        if (retDoc2M012.length <= 4 && !booleanCostID013) {
          setValue("DescriptPrint2-1", "");
          setValue("DescriptPrint2-2", "");
          setValue("DescriptPrint2-3", "");
          setValue("DescriptPrint2-4", "");
          if ("B3018".equals(getUser())) messagebox("intOtherTable2Row(" + intOtherTable2Row + ")-----------5");
        }
        // if("B3018".equals(getUser())) messagebox("999") ;
        for (int i = 0; i < retDoc2M012.length; i++) {
          strinDepartNoL = retDoc2M012[i][1].trim();
          stringProjectID1L = retDoc2M012[i][3].trim();
          stringCostIDL = retDoc2M012[i][4].trim();
          stringCostID1L = (booleanSource) ? retDoc2M012[i][5].trim() : "";
          stringRealTotalMoney = retDoc2M012[i][7].trim();
          //
          stringDepartNoOldL = strinDepartNoL;
          if (!"".equals(stringProjectID1L)) stringDepartNoOldL = stringProjectID1L;
          // if(booleanCostIDOnly) {
          strinDepartNoL = "";
          strinProjectID1L = "";
          // }
          if (!"".equals(strinProjectID1L)) {
            if (exeUtil.doParseDouble(stringRealTotalMoney) > 0) {
              strinKeyL = stringCostIDL + stringLimit + stringCostID1L + stringLimit + "O" + stringLimit + strinProjectID1L + stringLimit + "P"; // 正數
            } else {
              strinKeyL = stringCostIDL + stringLimit + stringCostID1L + stringLimit + "O" + stringLimit + strinProjectID1L + stringLimit + "N"; // 負數
            }
          } else {
            if (exeUtil.doParseDouble(stringRealTotalMoney) > 0) {
              strinKeyL = stringCostIDL + stringLimit + stringCostID1L + stringLimit + "I" + stringLimit + strinDepartNoL + stringLimit + "P"; // 正數
            } else {
              strinKeyL = stringCostIDL + stringLimit + stringCostID1L + stringLimit + "I" + stringLimit + strinDepartNoL + stringLimit + "N"; // 負數
            }
          }
          // 列印 [部門 or 案別]+[請款代碼名稱]
          if (retDoc2M012.length <= 4) {
            stringFieldName = "DescriptPrint2-" + (i + 1);
            stringFieldName1 = "RealMoneySumPrint";
            stringFieldAddUp = "RealMoneySumAddUpPrint" + ((i == 0) ? "" : "-" + i);
            if (i > 0) {
              stringFieldName1 += "-" + i;
            }
            if (booleanSame) {
              // [請款代碼名稱]
              stringFieldValue = getCost4Name(stringComNo, stringCostIDL, stringCostID1L, dbDoc);
            } else {
              // [部門 or 案別]+[請款代碼名稱]
              stringFieldValue = stringDepartNoOldL + " " + getCost4Name(stringComNo, stringCostIDL, stringCostID1L, dbDoc);
            }
            setValue("No" + (i + 1), "" + (i + 1));
            setValue(stringFieldName, stringFieldValue); // 估驗名稱
            setValue(stringFieldName1, exeUtil.getFormatNum2(stringRealTotalMoney));
            setPrintable(stringFieldName1, true); // 本期
            setValue(stringFieldAddUp, exeUtil.getFormatNum2(stringRealTotalMoney));
            setPrintable(stringFieldAddUp, true); // 累計
            if ("B3018".equals(getUser())) messagebox("intOtherTable2Row(" + intOtherTable2Row + ")-----------6");
          }
          //
          stringRealTotalMoney = "" + (exeUtil.doParseDouble(stringRealTotalMoney) + exeUtil.doParseDouble("" + hashtableMoney.get(strinKeyL)));
          hashtableMoney.put(strinKeyL, stringRealTotalMoney);
          if (vectorCostID.indexOf(strinKeyL) == -1) vectorCostID.add(strinKeyL);
        }
        if ("B3018".equals(getUser()))
          messagebox("retDoc2M012(" + retDoc2M012.length + ")vectorCostID(" + vectorCostID.size() + ")booleanCostID013(" + booleanCostID013 + ")-----------7");
        if ((retDoc2M012.length <= 4 || vectorCostID.size() > 4) || booleanCostID013) {
          if ("B3018".equals(getUser())) messagebox("--------------8");
        } else {
          String[] arrayCostID = (String[]) vectorCostID.toArray(new String[0]);
          Arrays.sort(arrayCostID);
          //
          // String stringFieldValue = "" ;
          String stringFieldValue1 = "";
          //
          setValue("DescriptPrint2-1", "");
          setValue("DescriptPrint2-2", "");
          setValue("DescriptPrint2-3", "");
          setValue("DescriptPrint2-4", "");
          for (int i = 0; i < 4; i++) {
            // for(int i=0 ; i<arrayCostID.length ; i++) {
            if (i >= arrayCostID.length) {
              stringFieldName = "DescriptPrint2-" + (i + 1);
              stringFieldName1 = "RealMoneySumPrint";
              stringFieldAddUp = "RealMoneySumAddUpPrint" + ((i == 0) ? "" : "-" + i);
              if (i > 0) {
                stringFieldName1 += "-" + i;
              }
              setValue("No" + (i + 1), "");
              setValue(stringFieldName, "");
              setPrintable(stringFieldName, true);
              setValue(stringFieldName1, "");
              setPrintable(stringFieldName1, true);
              setValue(stringFieldAddUp, "");
              setPrintable(stringFieldAddUp, true);
              if ("B3018".equals(getUser())) messagebox("--------------9");
              continue;
            }
            strinKeyL = arrayCostID[i];
            arrayTemp = convert.StringToken(strinKeyL, stringLimit);
            stringCostIDL = arrayTemp[0];
            stringCostID1L = arrayTemp[1];
            strinProjectID1L = arrayTemp[3];
            stringFieldValue1 = exeUtil.getFormatNum2(convert.FourToFive("" + hashtableMoney.get(strinKeyL), 0));
            //
            stringFieldName = "DescriptPrint2-" + (i + 1);
            stringFieldName1 = "RealMoneySumPrint";
            stringFieldAddUp = "RealMoneySumAddUpPrint" + ((i == 0) ? "" : "-" + i);
            if (i > 0) {
              stringFieldName1 += "-" + i;
            }
            // if(!booleanSource) {
            if ("".equals(strinProjectID1L)) {
              stringFieldValue = getCost4Name(stringComNo, stringCostIDL, stringCostID1L, dbDoc) + "(詳附件)";
            } else {
              stringFieldValue = getCost4Name(stringComNo, stringCostIDL, stringCostID1L, dbDoc) + "(" + strinProjectID1L + ")";
            }
            /*
             * } else { System.out.println("--------------------------")
             * if("".equals(strinProjectID1L)) { stringFieldValue =
             * getCostID1View(stringCostIDL+stringCostID1L, dbDoc)+"("+strinProjectID1L+")"
             * ; } else { stringFieldValue = getCostID1View(stringCostIDL+stringCostID1L,
             * dbDoc)+"("+strinProjectID1L+")" ; } }
             */
            setValue("No" + (i + 1), "" + (i + 1));
            setValue(stringFieldName, stringFieldValue);
            setPrintable(stringFieldName, true);
            setValue(stringFieldName1, stringFieldValue1);
            setPrintable(stringFieldName1, true);
            setValue(stringFieldAddUp, stringFieldValue1);
            setPrintable(stringFieldAddUp, true);
            if ("B3018".equals(getUser())) messagebox("--------------10");
          }
        }
      }
    } else {
      if ("B3018".equals(getUser())) messagebox("----------11");
    }
    // if("B3018".equals(getUser())) return false ;
    System.out.println("DescriptPrint2-1(" + getValue("DescriptPrint2-1") + ")---------------------------------6");
    // 20110708
    // 流程
    Vector vectorCostID = new Vector();
    //
    for (int intNo = 0; intNo < retDoc2M012.length; intNo++) {
      stringCostID = retDoc2M012[intNo][4].trim();
      if (vectorCostID.indexOf(stringCostID) == -1) vectorCostID.add(stringCostID);
    }
    //
    String[] arrayCostID = (String[]) vectorCostID.toArray(new String[0]);
    String stringFlowDescript = (new Doc.Doc2M010()).getDocFlow(stringComNo, stringKindNo, "", "" + doubleActualMoney, arrayCostID, exeUtil);
    setValue("FlowDescript", stringFlowDescript);
    setVisible("FlowDescript", "B3018".equals(getUser()));
    // if("B3018,b3018,b1721,".indexOf(getUser())!=-1)
    setVisible("TEST", false);
    // 承辦人 及 列印日期
    int intPos = 0;
    String stringOriEmployeeNo = retDoc2M010[0][17].trim();
    String stringToday = datetime.getToday("yy/mm/dd");
    //
    intPos = stringToday.indexOf("/");
    stringToday = stringToday.substring(intPos + 1);
    //
    if ("J49369".equals(stringBarCode)) {
      stringToday = stringToday.replaceAll("05/27", "04/30");
    }
    // setValue("OriEmployeeNo", getEmpName(stringOriEmployeeNo)) ;
    // setValue("PrintDateTime", stringToday) ;
    //
    if (booleanSource) return true;
    // 附件資訊
    String stringDocCode = "";
    String stringDocCount = "";
    String stringDocTXT = "";
    String[][] retDoc5M0201 = getDoc5M0201("Doc5M0201", stringBarCode, "");
    // 僅允許六個附件，每個附件20字元()
    for (int intNo = 0; intNo < 6; intNo++) {
      stringDocCode = (intNo < retDoc5M0201.length) ? retDoc5M0201[intNo][0].trim() : "";
      stringDocCount = (intNo < retDoc5M0201.length) ? retDoc5M0201[intNo][1].trim() : "";
      stringDocTXT = (intNo < retDoc5M0201.length) ? retDoc5M0201[intNo][2].trim() : "";
      //
      if ("AX9998,AX9999,".indexOf(stringDocCode + ",") != -1) {
        if ("AX9998,".indexOf(stringDocCode + ",") != -1) {
          stringDocCount = "";
        }
        stringDocCode = stringDocTXT;
      } else {
        stringDocCode = getDocDescriptDoc5M0291(stringDocCode);
      }
      if (!"".equals(stringDocCode)) stringDocCode = (intNo + 1) + ". " + stringDocCode;
      //
      if (retDoc5M0201.length > 3 && code.StrToByte(stringDocCode).length() + stringDocCount.length() > 21) {
        stringDocCode = exeUtil.doCutStringBySize(21 - stringDocCount.length(), stringDocCode)[0] + "...";
      }
      if (!"".equals(stringDocCode) && !"".equals(stringDocCount)) stringDocCode += "x" + stringDocCount;
      //
      setValue("DocCodeTXT" + (intNo + 1), stringDocCode);
    }
    // System.out.println("-------------------------------設定資料END");
    return true;
  }

  public void doSetContractNo(String stringComNo, String stringKindNoFront, String[][] retDoc2M017, boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    if (!"B3018".equals(getUser())) return;
    if (booleanSource) return;
    //
    talk dbConstAsk = getTalk("" + get("put_Const_Ask"));
    String stringPurchaseNo1 = retDoc2M017[0][0].trim();
    String stringPurchaseNo2 = retDoc2M017[0][1].trim();
    String stringPurchaseNo3 = retDoc2M017[0][2].trim();
    String stringFactoryNo = retDoc2M017[0][6].trim();
    String stringProjectID1 = retDoc2M017[0][7].trim();
    String stringPurchaseNo = stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3;
    String stringBarCodePur = "";
    String stringContractNo = "";
    String stringPrdocode = "";
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableRelContractDetail = new Hashtable();
    Vector vectorRelContractDetail = null;
    //
    hashtableAnd.put("ComNo", stringComNo);
    hashtableAnd.put("KindNo", stringKindNoFront);
    hashtableAnd.put("DocNo", stringPurchaseNo);
    stringBarCodePur = exeUtil.getNameUnion("BarCode", (booleanSource) ? "Doc3M011" : "Doc5M011", "", hashtableAnd, dbDoc);
    if ("".equals(stringBarCodePur)) return;
    //
    stringPrdocode = exeUtil.getNameUnion("prdocode", "prdt", " AND  social  =  '" + stringFactoryNo + "' ", new Hashtable(), dbConstAsk);

    hashtableAnd.put("barcode", stringBarCodePur);
    hashtableAnd.put("prdocode", stringPrdocode);
    vectorRelContractDetail = exeUtil.getQueryDataHashtable("rel_contract_detail", hashtableAnd, "", dbConstAsk);
    if (vectorRelContractDetail.size() == 0) return;
    hashtableRelContractDetail = (Hashtable) vectorRelContractDetail.get(0);
    if (hashtableRelContractDetail == null) return;
    //
    String stringCcaseCode = ("" + hashtableRelContractDetail.get("ccasecode")).trim();
    if ("null".equals(stringCcaseCode)) stringCcaseCode = "";
    String stringContractIID = ("" + hashtableRelContractDetail.get("contract_iid")).trim();
    if ("null".equals(stringContractIID)) stringContractIID = "";
    //
    setValue("ContractNo", stringCcaseCode + "-" + stringContractIID);
  }

  //
  public Vector doPrintPurchaseExist(String stringRetainType, String stringComNo, String stringBarCode, String stringEDateTime, String stringFactoryNo,
      String stringSqlAndPurchaseNo, String stringKindNo, String stringKindNoFront, String[][] retDoc2M012, String[][] retDoc2M017, Vector vectorDoc3M012,
      Hashtable hashtableDoc3M012, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    System.out.println("doPrintPurchaseExist-------------------------------S");
    Object objectTemp = null;
    int intCount = 1;
    int intSize = 30;
    String stringItemName = "";
    String stringItemName2 = "";
    String stringKey = "";
    String stringAmtPre = ""; // 前期估驗金額
    String stringNumPre = ""; // 前期估驗數量
    String stringAmt = ""; // 本期估驗金額
    String stringNum = ""; // 本期估驗數量
    String stringAmtSum = ""; // 累計估驗金額
    String stringNumSum = "";// 累計估驗數量
    String stringFieldName = "";
    String stringTemp = "";
    String stringFieldName2 = "";
    String stringFieldName11 = "";// 前期估驗金額
    String stringFieldName12 = "";// 前期估驗數量
    String stringFieldName21 = "";// 本期估驗金額
    String stringFieldName22 = "";// 本期估驗數量
    String stringFieldName31 = ""; // 累計估驗金額
    String stringFieldName32 = "";// 累計估驗數量
    String stringControlType = "" + get("ONLY_CONTROL_AMT");
    String[] arrayDoc3M012 = null;
    String[] arrayDoc5M0272 = null;
    String[] arrayTemp = null;
    double[] arrayAmt = { 0, 0, 0, 0, 0, 0 };
    Vector vectorKey = new Vector(); // 部門(案別) + 請款代碼
    Hashtable hashtableDoc5M0272 = new Hashtable();
    Hashtable hashtableMoney = new Hashtable();
    Hashtable hashtableRealMoney = new Hashtable();// 已使用金額
    Hashtable hashtableRealMoneyAll = new Hashtable();
    boolean booleanFlag = true;
    //
    String[][] retDoc5M0272 = getDoc5M0272("", "", stringKindNo, " AND  BarCode =  '" + stringBarCode + "' ", dbDoc);
    String[][] retDoc5M0272L = getITEMCountDoc5M0272(stringEDateTime, stringComNo, stringKindNo, stringFactoryNo, " AND (" + stringSqlAndPurchaseNo + ") ", dbDoc);
    if (retDoc5M0272L.length > 4 || "A".equals(stringRetainType)) {
      setValue("DescriptPrint2-1", getValue("DescriptPrint2-1").trim() + "(詳附件)");
      booleanFlag = false;
      System.out.println("doPrintPurchaseExist-------------------------------詳附件");
    }
    setPrintable("RealMoneySumPrePrint", true);
    setPrintable("RealMoneySumPrePrint-1", true);
    setPrintable("RealMoneySumPrePrint-2", true);
    setPrintable("RealMoneySumPrePrint-3", true);
    setPrintable("RealMoneySumPrePrint2", true);
    setPrintable("RealMoneySumAddUpPercent", true);
    setPrintable("RealMoneySumAddUpPercent-1", true);
    setPrintable("RealMoneySumAddUpPercent-2", true);
    setPrintable("RealMoneySumAddUpPercent-3", true);
    setPrintable("RealMoneySumAddUpPercent2", true);
    if (vectorDoc3M012.size() <= 4 && booleanFlag) {
      // 依請購項目作列印
      setValue("DescriptPrint2-1", "");
      setValue("DescriptPrint2-2", "");
      setValue("DescriptPrint2-3", "");
      setValue("DescriptPrint2-4", "");
      //
      System.out.println("doDoc5M0272---doPrintPurchaseExist-------------------------------詳附件S" + vectorDoc3M012.size());
      doDoc5M0272(stringBarCode, stringEDateTime, stringComNo, stringKindNo, stringKindNoFront, stringFactoryNo, stringSqlAndPurchaseNo, hashtableDoc5M0272, exeUtil, dbDoc);
      System.out.println("doDoc5M0272---doPrintPurchaseExist-------------------------------詳附件E(" + vectorDoc3M012.size() + ")");
      for (int intNo = 0; intNo < vectorDoc3M012.size(); intNo++) {
        stringKey = "" + vectorDoc3M012.get(intNo);
        arrayDoc3M012 = (String[]) hashtableDoc3M012.get(stringKey);
        objectTemp = hashtableDoc5M0272.get(stringKey);
        if (objectTemp == null) continue;
        arrayDoc5M0272 = (String[]) objectTemp;
        stringNumPre = arrayDoc5M0272[0].trim();
        stringAmtPre = arrayDoc5M0272[1].trim();
        stringNum = arrayDoc5M0272[2].trim();
        stringAmt = arrayDoc5M0272[3].trim();
        System.out.println(intNo + "stringKey(" + stringKey + ")stringNumPre(" + stringNumPre + ")stringAmtPre(" + stringAmtPre + ")stringNum(" + stringNum + ")stringAmt("
            + stringAmt + ")----------------------------------");
        stringNumSum = "" + (exeUtil.doParseDouble(stringNumPre) + exeUtil.doParseDouble(stringNum));
        stringAmtSum = "" + (exeUtil.doParseDouble(stringAmtPre) + exeUtil.doParseDouble(stringAmt));
        if (exeUtil.doParseDouble(stringAmtSum) == 0) continue;
        // 項目(請款名稱+內容)
        stringItemName = getCost4Name("", arrayDoc3M012[0], "", dbDoc);
        if (stringItemName.indexOf(arrayDoc3M012[14].trim()) == -1) {
          stringItemName = getCost4Name("", arrayDoc3M012[0], "", dbDoc) + "-" + arrayDoc3M012[14];
        }
        arrayTemp = exeUtil.doCutStringBySize(intSize, stringItemName);
        stringItemName = arrayTemp[0];
        stringItemName2 = exeUtil.doSubstringByte(arrayTemp[1], 0, intSize);
        //
        arrayAmt[0] += exeUtil.doParseDouble(stringNumPre);
        arrayAmt[1] += exeUtil.doParseDouble(stringAmtPre);
        arrayAmt[2] += exeUtil.doParseDouble(stringNum);
        arrayAmt[3] += exeUtil.doParseDouble(stringAmt);
        arrayAmt[4] += exeUtil.doParseDouble(stringNumSum);
        arrayAmt[5] += exeUtil.doParseDouble(stringAmtSum);
        //
        stringFieldName = "ITEM" + intCount + "-1";
        stringFieldName2 = "ITEM" + intCount + "-2";
        stringFieldName11 = "RealMoneySumPrePercent";
        stringFieldName12 = "RealMoneySumPrePrint";
        stringFieldName21 = "RealMoneySumPercent";
        stringFieldName22 = "RealMoneySumPrint";
        stringFieldName31 = "RealMoneySumAddUpPercent";
        stringFieldName32 = "RealMoneySumAddUpPrint";
        if (intCount > 1) {
          stringFieldName11 += "-" + (intCount - 1);
          stringFieldName12 += "-" + (intCount - 1);
          stringFieldName21 += "-" + (intCount - 1);
          stringFieldName22 += "-" + (intCount - 1);
          stringFieldName31 += "-" + (intCount - 1);
          stringFieldName32 += "-" + (intCount - 1);
        }
        //
        if (("," + stringControlType + ",").indexOf("," + arrayDoc3M012[4] + ",") != -1) {
          stringNumPre = "1";
          stringNum = "1";
          stringNumSum = "1";
          arrayAmt[0] = 1;
          arrayAmt[2] = 1;
          arrayAmt[4] = 1;
        }
        setValue("No" + intCount, "" + intCount); // No
        setValue(stringFieldName, stringItemName); // 項目
        setValue(stringFieldName2, stringItemName2); // 項目
        setValue("SIZE" + intCount, exeUtil.getFormatNum2(arrayDoc3M012[6])); // 預算數量
        setValue("UNIT" + intCount, arrayDoc3M012[4]); // 單位
        setValue("PRICE" + intCount, exeUtil.getFormatNum2(arrayDoc3M012[9])); // 單價
        stringNumPre = convert.FourToFive(stringNumPre, 2);
        if (exeUtil.doParseDouble(stringAmtPre) == 0) stringNumPre = "0";
        setValue(stringFieldName11, exeUtil.getFormatNum2(stringNumPre)); // 前期
        setValue(stringFieldName12, exeUtil.getFormatNum2(stringAmtPre));
        stringNum = convert.FourToFive(stringNum, 2);
        setValue(stringFieldName21, exeUtil.getFormatNum2(stringNum)); // 本期
        setValue(stringFieldName22, exeUtil.getFormatNum2(stringAmt));
        stringNumSum = convert.FourToFive(stringNumSum, 2);
        if (exeUtil.doParseDouble(stringAmtSum) == 0) stringNumSum = "0";
        setValue(stringFieldName31, exeUtil.getFormatNum2(stringNumSum)); // 累計
        setValue(stringFieldName32, exeUtil.getFormatNum2(stringAmtSum));
        //
        intCount++;
      }
      // 估驗部份小計
      stringTemp = convert.FourToFive("" + arrayAmt[0], 2);
      if (arrayAmt[1] == 0) stringTemp = "0";
      setValue("RealMoneySumPrePercent2", exeUtil.getFormatNum2(stringTemp));
      setValue("RealMoneySumPrePrint2", exeUtil.getFormatNum2("" + arrayAmt[1]));
      stringTemp = convert.FourToFive("" + arrayAmt[2], 2);
      setValue("RealMoneySumPercent2", exeUtil.getFormatNum2(stringTemp));
      setValue("RealMoneySumPrint2", exeUtil.getFormatNum2("" + arrayAmt[3]));
      stringTemp = convert.FourToFive("" + arrayAmt[4], 2);
      setValue("RealMoneySumAddUpPercent2", exeUtil.getFormatNum2(stringTemp));
      setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2("" + arrayAmt[5]));
      vectorKey.add("OK");
    } else {
      System.out.println("doDoc5M0272---doPrintPurchaseExist-------------------------------超過 4 列");
      /*
       * String stringKeyL = "" ; String stringLimit = "%---%" ; String stringCostIDL
       * = "" ; String stringCostID1L = "" ; String stringProjectID1L = "" ; String
       * stringRealTotalMoney = "" ; double doubleSum = 0 ; double doubleNoUseSum = 0
       * ; // 算出所有 請款代碼 的金額 hashtableRealMoney = getUsedProjectIDMoney(false,
       * stringBarCode, stringEDateTime, stringComNo, retDoc2M017, vectorKey,
       * hashtableRealMoneyAll, exeUtil, dbDoc) ; if(vectorKey.size() <= 4) { // 依 [部門
       * or 案別]+[請款名稱] 作列印 boolean booleanSame = true ; String stringInOutL = "" ;
       * String stringInOutOldL = "" ; String stringDepartNoL = "" ; String
       * stringDepartNoOldL = "" ; // 0 內外業 1 部門 or 案別 2 請款代碼 3 小請款代碼 for(int intNo=0
       * ; intNo<vectorKey.size() ; intNo++) { stringKeyL = ""+vectorKey.get(intNo) ;
       * arrayTemp = convert.StringToken(stringKeyL, stringLimit) ; stringInOutL =
       * arrayTemp[0] ; stringDepartNoL = arrayTemp[1] ; stringCostIDL = arrayTemp[3]
       * ; if(intNo != 0) { if(!stringInOutOldL.equals(stringInOutL)) {booleanSame =
       * false ; break ;} if(!stringDepartNoOldL.equals(stringDepartNoL)) {booleanSame
       * = false ; break ;} } stringInOutOldL = stringInOutL ; stringDepartNoOldL =
       * stringDepartNoL ; } // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
       * // 5 CostID1 6 RealMoney 7 RealTotalMoney for(int intNo=0 ;
       * intNo<retDoc2M012.length ; intNo++) { stringDepartNoL =
       * retDoc2M012[intNo][1].trim() ; stringProjectID1L =
       * retDoc2M012[intNo][3].trim() ; stringCostIDL = retDoc2M012[intNo][4].trim() ;
       * stringCostID1L = retDoc2M012[intNo][5].trim() ; stringRealTotalMoney =
       * retDoc2M012[intNo][7].trim() ; // if(!"".equals(stringProjectID1L)) {
       * stringKeyL =
       * "O"+stringLimit+stringProjectID1L+stringLimit+stringCostIDL+stringLimit+
       * stringCostID1L ; } else { stringKeyL =
       * "I"+stringLimit+stringDepartNoL+stringLimit+stringCostIDL+stringLimit+
       * stringCostID1L ; } // stringRealTotalMoney =
       * ""+(exeUtil.doParseDouble(stringRealTotalMoney)+exeUtil.doParseDouble(""+
       * hashtableMoney.get(stringKeyL))) ; hashtableMoney.put(stringKeyL,
       * stringRealTotalMoney) ; } // 0 內外業 1 部門 or 案別 2 請款代碼 3 小請款代碼 for(int intNo=0
       * ; intNo<vectorKey.size() ; intNo++) { //for(int intNo=0 ;
       * intNo<arrayCostID.length ; intNo++) { stringKeyL = ""+vectorKey.get(intNo) ;
       * //stringKeyL = arrayCostID[intNo] ; arrayTemp =
       * convert.StringToken(stringKeyL, stringLimit) ; stringProjectID1L =
       * arrayTemp[1] ; //stringCostIDL = arrayTemp[2] ; //stringCostID1L =
       * arrayTemp[3] ; //stringKeyL = stringCostID1L ; stringAmt =
       * ""+hashtableMoney.get(stringKeyL) ; // 本期 doubleSum =
       * exeUtil.doParseDouble(""+hashtableRealMoneyAll.get(stringKeyL)) ; // 全部
       * doubleNoUseSum = exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKeyL))
       * ; // 未使用 stringAmtPre = ""+(doubleSum - doubleNoUseSum) ; // 前期 stringAmtSum
       * = ""+(exeUtil.doParseDouble(stringAmtPre) + exeUtil.doParseDouble(stringAmt))
       * ; // 累計 // if(exeUtil.doParseDouble(stringAmtSum)==0) continue ; //
       * arrayAmt[1] += exeUtil.doParseDouble(stringAmtPre) ; arrayAmt[3] +=
       * exeUtil.doParseDouble(stringAmt) ; arrayAmt[5] +=
       * exeUtil.doParseDouble(stringAmtSum) ; // // stringFieldName =
       * "DescriptPrint2-"+intCount ; stringFieldName12 = "RealMoneySumPrePrint" ;
       * stringFieldName22 = "RealMoneySumPrint" ; stringFieldName32 =
       * "RealMoneySumAddUpPrint" ; if(intCount > 1) { stringFieldName12 +=
       * "-"+(intCount-1) ; stringFieldName22 += "-"+(intCount-1) ; stringFieldName32
       * += "-"+(intCount-1) ; } if(booleanSame) { // [請款代碼名稱] stringItemName =
       * getCost4Name(stringCostIDL, dbDoc) ; } else { // [案別 or 部門]+[請款代碼名稱]
       * stringItemName = stringProjectID1L+" "+getCost4Name(stringCostIDL, dbDoc) ; }
       * // 列印資料 setValue("No"+intCount, ""+intCount) ; // No
       * setValue(stringFieldName, stringItemName) ; // 項目 setValue(stringFieldName12,
       * exeUtil.getFormatNum2(stringAmtPre)) ; // 前期 setValue(stringFieldName22,
       * exeUtil.getFormatNum2(stringAmt)) ; // 本期 setValue(stringFieldName32,
       * exeUtil.getFormatNum2(stringAmtSum)) ; // 累計 // intCount++ ; } // 估驗部份小計
       * setValue("RealMoneySumPrePrint2", exeUtil.getFormatNum2(""+arrayAmt[1])) ;
       * setValue("RealMoneySumPrint2", exeUtil.getFormatNum2(""+arrayAmt[3])) ;
       * setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(""+arrayAmt[5])) ;
       * 
       * } else { // 依請款代碼作彙總，列印[請款代碼名稱] + (詳附件) String stringDepartNoL = "" ; Vector
       * vectorCostID = new Vector() ; // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1
       * 4 CostID // 5 CostID1 6 RealMoney 7 RealTotalMoney for(int intNo=0 ;
       * intNo<retDoc2M012.length ; intNo++) { stringDepartNoL =
       * retDoc2M012[intNo][1].trim() ; stringProjectID1L =
       * retDoc2M012[intNo][3].trim() ; stringCostIDL = retDoc2M012[intNo][4].trim() ;
       * stringCostID1L = retDoc2M012[intNo][5].trim() ; stringRealTotalMoney =
       * retDoc2M012[intNo][7].trim() ; // stringKeyL = stringCostIDL ;
       * if(!"".equals(stringProjectID1L)) { //stringKeyL =
       * "O"+stringLimit+stringProjectID1L+stringLimit+stringCostIDL+stringLimit+
       * stringCostID1L ; } else { //stringKeyL =
       * "I"+stringLimit+stringDepartNoL+stringLimit+stringCostIDL+stringLimit+
       * stringCostID1L ; } // stringRealTotalMoney =
       * ""+(exeUtil.doParseDouble(stringRealTotalMoney)+exeUtil.doParseDouble(""+
       * hashtableMoney.get(stringKeyL))) ; hashtableMoney.put(stringKeyL,
       * stringRealTotalMoney) ; //if(vectorCostID.indexOf(stringKeyL) == -1)
       * vectorCostID.add(stringKeyL) ; } int intPos = 0 ; String stringKeyUse = "" ;
       * Vector vectorKeyL = new Vector() ; double doubleAll = 0 ; double doubleUse =
       * 0 ; for(int intNo=0 ; intNo<vectorKey.size() ; intNo++) { stringKeyL =
       * ""+vectorKey.get(intNo) ; arrayTemp = convert.StringToken(stringKeyL,
       * stringLimit) ; stringCostIDL = arrayTemp[3] ; doubleAll =
       * exeUtil.doParseDouble(""+hashtableRealMoneyAll.get(stringKeyL)) ; doubleUse =
       * exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKeyL)) ; // intPos =
       * vectorCostID.indexOf(stringCostIDL) ; if(intPos == -1) {
       * //vectorKeyL.add(stringKeyL) ; vectorCostID.add(stringCostIDL) ;
       * //stringKeyUse = stringCostIDL ; //doubleAll = 0 ; //doubleUse = 0 ; } else {
       * //stringKeyUse = ""+vectorCostID.get(intPos) ; } doubleAll +=
       * exeUtil.doParseDouble(""+hashtableRealMoneyAll.get(stringCostIDL)) ;
       * hashtableRealMoneyAll.put(stringCostIDL, ""+doubleAll) ; doubleUse +=
       * exeUtil.doParseDouble(""+hashtableRealMoney.get(stringCostIDL)) ;
       * hashtableRealMoney.put(stringCostIDL, ""+doubleUse) ; } String[] arrayCostID
       * = (String[]) vectorKey.toArray(new String[0]) ; Arrays.sort(arrayCostID) ;
       * if(arrayCostID.length <= 4) { for(int intNo=0 ; intNo<arrayCostID.length ;
       * intNo++) { stringKeyL = arrayCostID[intNo] ; arrayTemp =
       * convert.StringToken(stringKeyL, stringLimit) ; stringProjectID1L =
       * arrayTemp[1] ; stringCostIDL = arrayTemp[2] ; stringCostID1L = arrayTemp[3] ;
       * stringKeyL = stringCostIDL ; stringAmt = ""+hashtableMoney.get(stringKeyL) ;
       * // 本期 doubleSum =
       * exeUtil.doParseDouble(""+hashtableRealMoneyAll.get(stringKeyL)) ; // 全部
       * doubleNoUseSum = exeUtil.doParseDouble(""+hashtableRealMoney.get(stringKeyL))
       * ; // 未使用 stringAmtPre = ""+(doubleSum - doubleNoUseSum) ; // 前期 stringAmtSum
       * = ""+(exeUtil.doParseDouble(stringAmtPre) + exeUtil.doParseDouble(stringAmt))
       * ; // 累計 stringItemName = getCost4Name(stringCostIDL, dbDoc)+"(詳附件)" ; //
       * if(exeUtil.doParseDouble(stringAmtSum)==0) continue ; // arrayAmt[1] +=
       * exeUtil.doParseDouble(stringAmtPre) ; arrayAmt[3] +=
       * exeUtil.doParseDouble(stringAmt) ; arrayAmt[5] +=
       * exeUtil.doParseDouble(stringAmtSum) ; // // stringFieldName =
       * "DescriptPrint2-"+intCount ; stringFieldName12 = "RealMoneySumPrePrint" ;
       * stringFieldName22 = "RealMoneySumPrint" ; stringFieldName32 =
       * "RealMoneySumAddUpPrint" ; if(intCount > 1) { stringFieldName12 +=
       * "-"+(intCount-1) ; stringFieldName22 += "-"+(intCount-1) ; stringFieldName32
       * += "-"+(intCount-1) ; } // 列印資料 setValue("No"+intCount, ""+intCount) ; // No
       * setValue(stringFieldName, stringItemName) ; // 項目 setValue(stringFieldName12,
       * exeUtil.getFormatNum2(stringAmtPre)) ; // 前期 setValue(stringFieldName22,
       * exeUtil.getFormatNum2(stringAmt)) ; // 本期 setValue(stringFieldName32,
       * exeUtil.getFormatNum2(stringAmtSum)) ; // 累計 // intCount++ ; } // 估驗部份小計
       * setValue("RealMoneySumPrePrint2", exeUtil.getFormatNum2(""+arrayAmt[1])) ;
       * setValue("RealMoneySumPrint2", exeUtil.getFormatNum2(""+arrayAmt[3])) ;
       * setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(""+arrayAmt[5])) ;
       * } else { // 不作處理 //return vectorKey ; } }
       */
    }
    // 保留金額
    String stringPurchaseNo = "";
    String stringRetainMoneyPre = "";
    String stringRetainMoneyAddup = "";
    String stringReceiptTaxPre = "";
    String stringReceiptTaxAddup = "";
    String stringActualMoneyPre = "";
    String stringActualMoneyAddup = "";
    String stringSqlAnd = "";
    String stringSqlAnd2 = "";
    double doubleBackRetainMoney = 0;
    double doubleThisRetainMoneyPrint = exeUtil.doParseDouble(getValue("RetainMoneyPrint").trim().replaceAll(",", ""));
    stringFactoryNo = retDoc2M017[0][6].trim();
    for (int intNo = 0; intNo < retDoc2M017.length; intNo++) {
      stringPurchaseNo = retDoc2M017[intNo][0].trim() + retDoc2M017[intNo][7].trim() + retDoc2M017[intNo][1].trim() + retDoc2M017[intNo][2].trim();
      if (!"".equals(stringSqlAnd)) stringSqlAnd += ", ";
      stringSqlAnd += " '" + stringPurchaseNo + "' ";
    }
    stringSqlAnd2 = " AND  M20.EDateTime  <=  '" + stringEDateTime + "' " + " AND  M220.BarCode  <>  '" + stringBarCode + "' " + " AND  M220.BarCodeRef  IN  (SELECT  BarCode "
        + " FROM  Doc5M011 " + " WHERE  DocNo  IN  (" + stringSqlAnd + ") " + " AND  KindNo=  '" + stringKindNoFront + "' )";
    doubleBackRetainMoney = getBackRetainMoneyDoc5M0220("", stringFactoryNo, "", stringSqlAnd2, exeUtil);
    stringRetainMoneyPre = "" + (exeUtil.doParseDouble(getRetainMoneyDoc5M020(stringBarCode, stringComNo, stringFactoryNo, stringEDateTime, stringKindNo, stringSqlAnd, dbDoc)) - // 前期保留
        doubleBackRetainMoney); // 前期退保留沖銷 Doc5M0220

    if ("A".equals(stringRetainType)) {
      // 本期退保留沖銷 Doc5M0220
    }
    stringRetainMoneyAddup = "" + (exeUtil.doParseDouble(stringRetainMoneyPre) + doubleThisRetainMoneyPrint);
    // 未付金額、已付金額修正 修正
    String stringNoPayMoney = "" + (exeUtil.doParseDouble(getValue("NoPayMoney").trim().replaceAll(",", "")) + exeUtil.doParseDouble(stringRetainMoneyPre));
    String stringPaidUpMoney = "" + (exeUtil.doParseDouble(getValue("PaidUpMoney").trim().replaceAll(",", "")) - exeUtil.doParseDouble(stringRetainMoneyPre));
    // 2015-07-23 盧瑜瑋 取消 未付金額及已付金額扣除保留金機制
    // setValue("NoPayMoney", exeUtil.getFormatNum2(stringNoPayMoney)) ; // 未付金額
    // setValue("PaidUpMoney", exeUtil.getFormatNum2(stringPaidUpMoney)) ; // 已付金額
    //
    setValue("RetainMoneyPrePrint", exeUtil.getFormatNum2(stringRetainMoneyPre)); // 前期保留
    setValue("RetainMoneyAddUpPrint", exeUtil.getFormatNum2(stringRetainMoneyAddup)); // 累計保留
    // 扣繳金額
    stringReceiptTaxPre = getReceiptTaxDoc5M023(stringBarCode, stringComNo, stringFactoryNo, stringEDateTime, stringKindNo, stringSqlAnd, dbDoc);
    stringReceiptTaxAddup = "" + (exeUtil.doParseDouble(stringReceiptTaxPre) + exeUtil.doParseDouble(getValue("CheapenMoneyPrint").trim().replaceAll(",", "")));
    setValue("CheapenMoneyPrePrint", exeUtil.getFormatNum2(stringReceiptTaxPre)); // 前期扣繳
    setValue("CheapenMoneyAddUpPrint", exeUtil.getFormatNum2(stringReceiptTaxAddup)); // 累計扣繳
    // 實付金額
    stringActualMoneyPre = "" + (exeUtil.doParseDouble(getValue("RealMoneySumPrePrint2").trim().replaceAll(",", "")) - exeUtil.doParseDouble(stringReceiptTaxPre)
        - exeUtil.doParseDouble(stringRetainMoneyPre));
    stringActualMoneyAddup = "" + (exeUtil.doParseDouble(getValue("RealMoneySumAddUpPrint2").trim().replaceAll(",", "")) - exeUtil.doParseDouble(stringReceiptTaxAddup)
        - exeUtil.doParseDouble(stringRetainMoneyAddup));
    setValue("ActualMoneyPrePrint", exeUtil.getFormatNum2(stringActualMoneyPre)); // 前期實付
    setValue("ActualMoneyAddUpPrint", exeUtil.getFormatNum2(stringActualMoneyAddup)); // 累計實付
    System.out.println("doPrintPurchaseExist-------------------------------E");
    return vectorKey;
  }

  // Excel 報表列印
  public void doPrintExcel(String stringBarCode, String stringComNo, String[][] retDoc2M011, String[][] retDoc2M012, String[][] retDoc2M013, FargloryUtil exeUtil)
      throws Throwable {
    String stringDepartNoSubject = "" + get("EMP_DEPT_CD");
    if ("0221".equals(stringDepartNoSubject)) return;
    //
    Farglory.Excel.FargloryExcel exeFun = new Farglory.Excel.FargloryExcel(7, 15, 33, 1);
    // 資料處理
    // 取得 Exce 物件
    String stringClientFilePath = "https://emaker.farglory.com.tw:8080/servlet/baServer3?step=6?filename=C:/emaker/batch/EXCEL/Template/Doc/Doc2M010.xlt";
    // 檔案存在檢查
    Vector retVector = exeFun.getExcelObject(stringClientFilePath);
    Dispatch objectSheet1 = (Dispatch) retVector.get(1);
    Dispatch objectSheet2 = (Dispatch) retVector.get(2);
    Dispatch objectClick = null;
    exeFun.setClearCol(1, 4); // 設定清除欄位為 B 到 R
    // 公司
    exeFun.putDataIntoExcel(1, 1, getCompanyName(stringComNo), objectSheet2); // 案別名稱
    //
    int intStartDataRow = exeFun.getStartDataRow();
    int intPageDataRow = exeFun.getPageDataRow();
    int intRecordNo = intStartDataRow;
    String stringInOut = "";
    String stringDepartNo = "";
    String stringProjectID1 = "";
    String stringRealMoney = "";
    for (int intRowNo = 0; intRowNo < retDoc2M012.length; intRowNo++) {
      stringInOut = retDoc2M012[intRowNo][0].trim();
      stringDepartNo = retDoc2M012[intRowNo][1].trim();
      stringProjectID1 = retDoc2M012[intRowNo][3].trim();
      stringRealMoney = retDoc2M012[intRowNo][6].trim();
      //
      if ("I".equals(stringInOut)) {
        exeFun.putDataIntoExcel(1, intRecordNo, getDepartName(stringDepartNo), objectSheet2); // 案別名稱
        exeFun.putDataIntoExcel(2, intRecordNo, stringDepartNo, objectSheet2);// 案別代碼
      } else {
        exeFun.putDataIntoExcel(1, intRecordNo, getProjectName(stringProjectID1), objectSheet2); // 案別名稱
        exeFun.putDataIntoExcel(2, intRecordNo, stringProjectID1, objectSheet2);// 案別代碼
      }
      exeFun.putDataIntoExcel(4, intRecordNo, convert.FourToFive(stringRealMoney, 0), objectSheet2);// 分攤金額

      intRecordNo++;
      // 滿頁時，將 Sheet2 Copy Sheet1
      if (intRecordNo == (intPageDataRow + intStartDataRow)) {
        double doubleAmt = exeUtil.doParseDouble(exeFun.getDataFromExcel(0, 21, objectSheet2)) + exeUtil.doParseDouble(exeFun.getDataFromExcel(0, 22, objectSheet2));
        exeFun.putDataIntoExcel(0, 22, convert.FourToFive("" + doubleAmt, 0), objectSheet2);
      }
      intRecordNo = exeFun.doChangePage(intRecordNo, objectSheet1, objectSheet2);
    } // For END
    // 複製未滿頁
    if (intRecordNo != intStartDataRow) {
      double doubleAmt = exeUtil.doParseDouble(exeFun.getDataFromExcel(0, 21, objectSheet2)) + exeUtil.doParseDouble(exeFun.getDataFromExcel(0, 22, objectSheet2));
      exeFun.putDataIntoExcel(0, 22, convert.FourToFive("" + doubleAmt, 0), objectSheet2);
      //
      exeFun.CopyPage(objectSheet1, objectSheet2);
      exeFun.doClearContents("B" + (intStartDataRow + 1) + ":E" + (intStartDataRow + intPageDataRow), objectSheet2);
      exeFun.doAdd1PageNo();
    }
    //
    int intAllPage = exeFun.getPageNo();
    int intPageAllRow = exeFun.getPageAllRow();
    int intRow = (intAllPage - 2) * intPageAllRow + 21;
    double doubleSum = 0;
    double doubleTotalSum = 0;
    double doubleTax = 0;
    for (int intNo = 0; intNo < retDoc2M012.length; intNo++) {
      doubleSum += exeUtil.doParseDouble(retDoc2M012[intNo][6].trim());
      doubleTotalSum += exeUtil.doParseDouble(retDoc2M012[intNo][7].trim());
    }
    doubleTax = doubleTotalSum - doubleSum;
    // 小計
    exeFun.putDataIntoExcel(4, intRow, convert.FourToFive("" + doubleSum, 0), objectSheet1);
    // 稅額
    exeFun.putDataIntoExcel(4, (intRow + 1), convert.FourToFive("" + doubleTax, 0), objectSheet1);
    // 合計
    exeFun.putDataIntoExcel(4, (intRow + 2), convert.FourToFive("" + doubleTotalSum, 0), objectSheet1);
    // 釋放 Excel 物件
    exeFun.getReleaseExcelObject(retVector);
  }

  // 借款沖銷
  /*
   * public boolean doPrint2(String stringBarCode, String stringPrevFunction,
   * boolean booleanSource, FargloryUtil exeUtil) throws Throwable { String
   * stringCostID = "" ; String stringBarCode11 = "" ; String[][] retBarCode11 =
   * getBarCodeForDoc6M010(booleanSource?"Doc6M010":"Doc5M030", stringBarCode) ;
   * String[][] retDoc6M010 = new String[0][0] ; String[][] retDoc6M011 = new
   * String[0][0] ; String[][] retDoc6M012 = new String[0][0] ; String[][]
   * retDoc6M013 = new String[0][0] ; String[][] retDoc6M0131 = new String[0][0] ;
   * // if(retBarCode11.length == 0) { //message("查無資料。") ;
   * messagebox("查無借款相關資料。") ; return false; } stringBarCode11 =
   * retBarCode11[0][0].trim() ; // 0 ComNo 1 Descript 2 NeedDate 3 BarCode 4
   * DocNo // 5 UNDERGO_WRITE 6 PurchaseNo1 7 PurchaseNo2 8 PurchaseNo3 9 DocNo1
   * // 10 DocNo2 11 DocNo3 12 DepartNo 13 RetainMoney 14 PayCondition1 // 15
   * PayCondition2 16 DestineExpenseDate 17 OriEmployeeNo 18 PrintCount 19
   * PurchaseNoExist // 20 OptometryNo1 21 OptometryNo2 22 OptometryNo3 23
   * BorrowNo1 24 BorrowNo2 // 25 BorrowNo3 26 CDate 27 RetainMoney 28 EDateTime
   * 29 CTime // 30 PayType 31 DestineExpenseDate 32 BorrowMoney 33 FactoryNo 34
   * BorrowMinusMoney // 35 AccountCount 36 PurchaseNo4 retDoc6M010 =
   * getDoc6M010(booleanSource?"Doc6M010":"Doc5M030", stringBarCode) ;
   * if(retDoc6M010.length == 0) { messagebox("查無資料。") ; return false; }
   * setValue("DeifyDepart", "") ; if("033FZ".equals(retDoc6M010[0][9].trim())) {
   * setValue("DeifyDepart", "敬會人總室") ; } // 0 InOut 1 DepartNo 2 ProjectID 3
   * ProjectID1 4 CostID // 5 CostID1 6 RealMoney 7 RealTotalMoney retDoc6M012 =
   * getDoc6M012(booleanSource?"Doc6M012":"Doc5M032", stringBarCode) ;
   * if(retDoc6M012.length == 0) { messagebox("查無 [費用] 資料。") ; return false ; } //
   * 0 FactoryNo 1 InvoiceKind 2 InvoiceDate 3 InvoiceNo 4 InvoiceMoney // 5
   * InvoiceTax 6 InvoiceTotalMoney 7 DeductKind 8 RecordNo retDoc6M011 =
   * getDoc6M011(booleanSource?"Doc6M011":"Doc5M031", stringBarCode) ; //
   * 取得發票資料，Doc2M011 // 0 FactoryNo 1 ReceiptKind 2 ReceiptDate 3 ReceiptMoney 4
   * ReceiptTax // 5 ReceiptTotalMoney 6 ReceiptTaxType 7 RecordNo
   * System.out.println("getDoc6M013-----------------------------------------") ;
   * retDoc6M013 = getDoc6M013(booleanSource?"Doc6M013":"Doc5M033", stringBarCode)
   * ; if(retDoc6M011.length == 0 && retDoc6M013.length == 0) { retDoc6M011 = new
   * String[0][0] ; retDoc6M013 = new String[0][0] ; } // 承辦人 及 列印日期 int intPos =
   * 0 ; String stringOriEmployeeNo = retDoc6M010[0][17].trim( ) ; String
   * stringToday = datetime.getToday("yy/mm/dd") ; // intPos =
   * stringToday.indexOf("/") ; stringToday = stringToday.substring(intPos+1) ; //
   * //setValue("OriEmployeeNo", getEmpName(stringOriEmployeeNo)) ;
   * //setValue("PrintDateTime", stringToday) ; // setValue("DocCodeTXT1", "") ;
   * setValue("DocCodeTXT2", "") ; setValue("DocCodeTXT3", "") ;
   * setValue("DocCodeTXT4", "") ; setValue("DocCodeTXT5", "") ;
   * setValue("DocCodeTXT6", "") ; return setDataDoc6M010(stringPrevFunction,
   * retDoc6M010, retDoc6M011, retDoc6M012, retDoc6M013, retBarCode11,
   * booleanSource, exeUtil) ; }
   */
  public boolean setDataDoc6M010(String stringPrevFunction, String[][] retDoc6M010, String[][] retDoc6M011, String[][] retDoc6M012, String[][] retDoc6M013, String[][] retBarCode11,
      boolean booleanSource, FargloryUtil exeUtil) throws Throwable {
    //
    String stringDepartNo = retDoc6M010[0][12].trim();
    String stringCDate = retDoc6M010[0][26].trim();
    String stringDocNo = retDoc6M010[0][4].trim();
    String stringDescript = retDoc6M010[0][1].trim();
    String stringComNo = retDoc6M010[0][0].trim();
    String stringRetainMoney = retDoc6M010[0][27].trim();
    String stringEDateTime = retDoc6M010[0][28].trim();
    String stringPurchaseNo = "";
    String stringPurchaseNo1 = "";
    String stringPurchaseNo2 = "";
    String stringPurchaseNo3 = "";
    String stringPurchaseNo4 = "";
    String stringOptometryNo = "";
    String stringBarCode = retDoc6M010[0][3].trim();
    String stringPrintCount = retDoc6M010[0][18].trim();
    String stringFactoryNo = retDoc6M010[0][33].trim();
    String stringInOutFirst = "";
    String stringInOut = "";
    String stringProjectFirst = "";
    String stringProject = "";
    String stringProjectID = "";
    String stringProjectID1 = "";
    String stringProjectShow = "";
    String stringDepart = "";
    String stringCompanyName = "";
    String stringDepartName = "";
    String stringFactoryName = "";
    String stringRealMoneySumPrint = "";
    String stringCheapenMoney = "";
    String stringPercent = "";
    String stringBorrowNo = "";
    String stringDate = "096/11/26";
    String stringSqlAnd = " AND  (( CDate  >=  '" + stringDate + "'  AND  ApplyType  =  'F' AND (UNDERGO_WRITE  =  'S'  OR  UNDERGO_WRITE  =  'H'))  OR "
        + " UNDERGO_WRITE  =  'Y')";
    String[] arrayCDate = convert.StringToken(exeUtil.getDateConvert(stringCDate), "/");
    String[][] retDoc6M0101 = null;
    double doubleActualMoney = 0;
    double doubleTemp = 0;
    //
    if (!booleanSource) {
      stringSqlAnd = " AND  (( Unipurchase  =  'Y' AND UNDERGO_WRITE  =  'H')  OR " + " UNDERGO_WRITE  =  'Y')";
    }
    //
    setValue("CTimePrint", retDoc6M010[0][29].trim());
    // 次數
    stringPrintCount = "" + exeUtil.doParseInteger(stringPrintCount);
    setValue("PrintCount", stringPrintCount);
    // 條碼編號
    if ("".equals(stringBarCode)) {
      messagebox("[條碼編號] 為空白，請重新列印。");
      return false;
    }
    setValue("BarCodePrint", stringBarCode);
    setVisible("BarCodePrint", stringPrevFunction.indexOf("行銷-請款申請書-借款沖銷-承辦") == -1);
    // 費用代碼
    String stringCostID = retDoc6M012[0][4].trim();
    String stringCostID1 = retDoc6M012[0][5].trim();
    String stringPurchaseNoExist = retDoc6M010[0][19].trim();
    double[] arrayMoney = { 0, 0, 0, 0 };
    setValue("ProjectNo", stringCostID + stringCostID1);
    // 借款單
    setValue("BorrowNo-1", "");
    setValue("BorrowNo-2", "");
    setValue("BorrowNo1", "");
    setValue("BorrowNo2", "");
    setValue("BorrowNo3", "");
    setValue("BorrowNo4", "");
    setValue("BorrowNo5", "");
    retDoc6M0101 = getDoc6M0101(booleanSource ? "Doc6M0101" : "Doc5M0301", stringBarCode);
    if (retDoc6M0101.length == 0) {
      messagebox("[借款單編號] 為空白，請重新列印。");
      return false;
    }
    for (int intDoc6M0101 = 0; intDoc6M0101 < 4; intDoc6M0101++) {
      stringBorrowNo = "";
      if (intDoc6M0101 < retDoc6M0101.length) stringBorrowNo = retDoc6M0101[intDoc6M0101][2].trim() + retDoc6M0101[intDoc6M0101][3].trim() + retDoc6M0101[intDoc6M0101][4].trim();
      switch (intDoc6M0101) {
      case 0:
        if (retDoc6M0101.length == 1) {
          setValue("BorrowNo" + (intDoc6M0101 + 1), stringBorrowNo);
          // getcLabel("BorrowNo-"+(intDoc6M0101+1)).setLocation(855, 690) ;
        } else {
          setValue("BorrowNo-" + (intDoc6M0101 + 1), stringBorrowNo);
          // getcLabel("BorrowNo"+(intDoc6M0101+1)).setLocation(844, 726) ;
        }
        break;
      case 1:
        if (retDoc6M0101.length == 2) {
          setValue("BorrowNo" + (intDoc6M0101 + 1), stringBorrowNo);
          // getcLabel("BorrowNo-"+(intDoc6M0101+1)).setLocation(855, 710) ;
        } else {
          setValue("BorrowNo-" + (intDoc6M0101 + 1), stringBorrowNo);
          // getcLabel("BorrowNo"+(intDoc6M0101+1)).setLocation(964, 726) ;
        }
        break;
      case 2:
        setValue("BorrowNo-" + (intDoc6M0101 + 1), stringBorrowNo);
        // getcLabel("BorrowNo"+(intDoc6M0101+1)).setLocation(844, 741) ;
        break;
      case 3:
        setValue("BorrowNo-" + (intDoc6M0101 + 1), stringBorrowNo);
        // getcLabel("BorrowNo"+(intDoc6M0101+1)).setLocation(964, 741) ;
        break;
      }
    }
    // 控制位置
    getcLabel("PurchaseNo1-1").setLocation(870, 575);
    getcLabel("PurchaseNo1-2").setLocation(0, 900);
    getcLabel("OptometryNo1-1").setLocation(870, 625);
    getcLabel("OptometryNo1-2").setLocation(0, 900);
    getcLabel("BorrowNo1").setLocation(0, 900);
    getcLabel("BorrowNo2").setLocation(0, 900);
    getcLabel("BorrowNo-1").setLocation(0, 900);
    getcLabel("BorrowNo-2").setLocation(0, 900);
    getcLabel("BorrowNo-3").setLocation(0, 900);
    getcLabel("BorrowNo-4").setLocation(0, 900);
    getcLabel("BorrowNo-5").setLocation(0, 900);
    int intCount = retDoc6M0101.length;
    // intCount = 5 ;
    if (intCount == 1) {
      getcLabel("BorrowNo1").setLocation(870, 664);
    } else if (intCount >= 2 && intCount <= 4) {
      getcLabel("BorrowNo-1").setLocation(870, 660);
      getcLabel("BorrowNo-2").setLocation(870, 675);
      getcLabel("BorrowNo-3").setLocation(1000, 660);
      getcLabel("BorrowNo-4").setLocation(1000, 675);
    } else if (intCount == 5) {
      getcLabel("OptometryNo1-1").setLocation(870, 620);
      getcLabel("BorrowNo-1").setLocation(870, 645);
      getcLabel("BorrowNo-2").setLocation(1000, 645);
      getcLabel("BorrowNo-3").setLocation(870, 660);
      getcLabel("BorrowNo-4").setLocation(1000, 660);
      getcLabel("BorrowNo-4").setLocation(870, 675);
    }
    if (stringPurchaseNoExist.startsWith("Y")) {
      // if("Y".equals(stringPurchaseNoExist)) {
      // 請購單
      int[][] arrayPosition1 = { { 855, 620 }, { 0, 0 } };
      stringPurchaseNo1 = retDoc6M010[0][6].trim();
      stringPurchaseNo2 = retDoc6M010[0][7].trim();
      stringPurchaseNo3 = retDoc6M010[0][8].trim();
      stringPurchaseNo4 = retDoc6M010[0][36].trim();
      stringPurchaseNo = stringPurchaseNo1 + stringPurchaseNo2 + stringPurchaseNo3;
      if ("".equals(stringPurchaseNo)) {
        messagebox("[請購單編號] 為空白，請重新列印。");
        return false;
      }
      setValue("PurchaseNo1-1", stringPurchaseNo);
      stringPurchaseNo = "";
      setValue("PurchaseNo1-2", stringPurchaseNo);
      // System.out.println("------------------------驗收單編號") ;
      int[][] arrayPosition2 = { { 855, 655 }, { 855, 675 } };
      stringOptometryNo = retDoc6M010[0][20].trim() + retDoc6M010[0][21].trim() + retDoc6M010[0][22].trim();
      if ("".equals(stringOptometryNo)) {
        messagebox("[驗收單編號] 為空白，請重新列印。");
        return false;
      }
      // getcLabel("OptometryNo1-1").setLocation(arrayPosition2[0][0],
      // arrayPosition2[0][1]) ;
      setValue("OptometryNo1-1", stringOptometryNo);
      stringOptometryNo = "";
      // getcLabel("OptometryNo1-2").setLocation(arrayPosition2[1][0],
      // arrayPosition2[1][1]) ;
      setValue("OptometryNo1-2", stringOptometryNo);
      // 合約處理
      doSetContractNo(stringComNo, "24", stringPurchaseNo, stringFactoryNo, exeUtil, dbDoc);
    }
    //
    // System.out.println("------------------------廠商名稱：由統一編號查詢廠商名稱") ;
    String stringTemp = "";
    double doubleReceiptTaxType = 0;
    String[][] retDoc = null;
    if ("".equals(stringFactoryNo)) {
      retDoc = getDoc6M011(booleanSource ? "Doc6M011" : "Doc5M031", stringBarCode);
      if (retDoc.length == 0) {
        retDoc = getDoc6M013(booleanSource ? "Doc6M013" : "Doc5M033", stringBarCode);
      }
      if (retDoc.length == 0) {
        // 0 RecordNo 1 BorrowNo 2 BorrowNo1 3 BorrowNo2 4 BorrowNo3
        retDoc = getDoc6M0101(booleanSource ? "Doc6M0101" : "Doc5M0301", stringBarCode);
        if (retDoc.length > 0) {
          stringTemp = getBarCodeForDoc6M010(booleanSource ? "Doc6M0101" : "Doc5M0301", stringComNo, "26", retDoc[0][2].trim(), retDoc[0][3].trim(), retDoc[0][4].trim());
          retDoc = getDoc6M010(booleanSource ? "Doc6M010" : "Doc5M030", stringTemp);
          if (retDoc.length > 0 && !"".equals(retDoc[0][33].trim())) {
            stringTemp = retDoc[0][33].trim();
            retDoc = new String[1][1];
            retDoc[0][0] = stringTemp;
          } else {
            retDoc = getDoc6M011(booleanSource ? "Doc6M011" : "Doc5M031", stringTemp);
            if (retDoc.length == 0) {
              retDoc = getDoc6M013(booleanSource ? "Doc6M013" : "Doc5M033", stringTemp);
              if (retDoc.length > 0) {
                doubleReceiptTaxType = exeUtil.doParseDouble(retDoc[0][6].trim()) / 100;
              }
            }
          }

        }
      }
    }
    if ("".equals(stringFactoryNo)) stringFactoryNo = (retDoc.length > 0) ? retDoc[0][0].trim() : "";
    String[][] retFED1005 = getFED1005(stringFactoryNo);
    if (retFED1005.length == 0) {
      messagebox(" [廠商名稱] 為空白，請重新列印。");
      return false;
    }
    setValue("FactoryNoPrint", retFED1005[0][0].trim());
    setValue("TELPrint", retFED1005[0][1].trim());
    //
    boolean booleanUniPurchase = false;
    String stringPurchaseMoney = "";
    String stringExistRealMoney = "";
    String stringExistRealMoney2 = "";
    String stringGroupID = "";
    Vector vectorDoc3M012 = new Vector();
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableDoc3M012 = new Hashtable();
    if (stringPurchaseNoExist.startsWith("Y")) {
      String[][] retDoc3M011 = null;
      String[][] retDoc3M013 = null;
      String stringBarCodePur = "";
      String stringGroupIDL = "";
      String stringFactoryNoL = "";
      String stringPurchaseMoneyL = "";
      boolean booleanSpecPurchaseNo = false;
      double doublePurchaseMoney = 0;
      // System.out.println("------------------------合約金額") ;
      retDoc3M011 = getDoc3M011(booleanSource ? "Doc3M011" : "Doc5M011", stringComNo, stringPurchaseNo1, stringPurchaseNo2, stringPurchaseNo3, "17", stringSqlAnd);
      if (retDoc3M011.length == 0) {
        messagebox("[請購單] 無對應的 [請購資訊]，請洽資訊室。");
        return false;
      }
      stringBarCodePur = retDoc3M011[0][12].trim();
      booleanSpecPurchaseNo = isSpectPurchaseNo(stringComNo, "17", stringPurchaseNo1 + stringPurchaseNo2 + stringPurchaseNo3, " AND  M13.GroupName  LIKE  '%#-#B' ", booleanSource,
          dbDoc);
      stringGroupID = getGroupID("Doc6M0171", stringBarCode, stringBarCodePur, stringPurchaseNo1 + stringPurchaseNo2 + stringPurchaseNo3, booleanSource, exeUtil, dbDoc);
      hashtableAnd.put("FactoryNo", stringFactoryNo);
      hashtableAnd.put("BarCode", stringBarCodePur);
      vectorDoc3M012 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc3M012" : "Doc5M012", hashtableAnd, "", dbDoc);
      for (int intDoc3M012 = 0; intDoc3M012 < vectorDoc3M012.size(); intDoc3M012++) {
        hashtableDoc3M012 = (Hashtable) vectorDoc3M012.get(intDoc3M012);
        if (hashtableDoc3M012 == null) continue;
        stringFactoryNoL = "" + hashtableDoc3M012.get("FactoryNo");
        stringGroupIDL = "" + hashtableDoc3M012.get("GroupID");
        stringPurchaseMoneyL = "" + hashtableDoc3M012.get("PurchaseMoney");
        //
        if (!stringFactoryNoL.equals(stringFactoryNo)) continue;
        //
        if (booleanSpecPurchaseNo) {
          if (!stringGroupID.equals(stringGroupIDL)) continue;
        }
        doublePurchaseMoney += exeUtil.doParseDouble(stringPurchaseMoneyL);
      }
      // retDoc3M013 = getDoc3M013(booleanSource?"Doc3M013":"Doc5M013",
      // stringBarCodePur, stringFactoryNo) ;
      // if(retDoc3M013.length == 0) {
      // messagebox("[請購單] 無對應的 [付款資訊]，請洽資訊室。") ;
      // return false ;
      // }
      stringPurchaseMoney = convert.FourToFive("" + doublePurchaseMoney, 0); // retDoc3M013[0][1].trim() ;
      // oce
      stringExistRealMoney = "" + getExistRealMoney(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo1 + stringPurchaseNo2 + stringPurchaseNo3, stringFactoryNo, "24",
          stringBarCodePur, stringGroupID, booleanSpecPurchaseNo, booleanSource, exeUtil, dbDoc);
      // stringExistRealMoney =
      // getExistFactoryNoRealMoneyBorrowForDoc6M010(stringBarCode, stringComNo,
      // stringEDateTime, stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3,
      // stringFactoryNo, "24", booleanSource, exeUtil) ;
      // stringExistRealMoney2 = getExistFactoryNoRealMoneyForDoc2M010(stringBarCode,
      // stringComNo, stringEDateTime,
      // stringPurchaseNo1+stringPurchaseNo2+stringPurchaseNo3, stringFactoryNo, "24",
      // booleanSource, exeUtil) ;
      // stringExistRealMoney = ""+(exeUtil.doParseDouble(stringExistRealMoney) +
      // exeUtil.doParseDouble(stringExistRealMoney2)) ;
      stringExistRealMoney = convert.FourToFive(stringExistRealMoney, 0);
      //
      String stringNoPay = operation.floatSubtract(stringPurchaseMoney, stringExistRealMoney, 0);
      //
      setValue("ContractMoney", format.format(stringPurchaseMoney, "999,999,999,999").trim());
      setPrintable("ContractMoney", true);
      setValue("PaidUpMoney", format.format(stringExistRealMoney, "999,999,999,999").trim());
      setPrintable("PaidUpMoney", true);
      setValue("NoPayMoney", format.format(stringNoPay, "999,999,999,999").trim());
      setPrintable("NoPayMoney", true);
    } else {
      setPrintable("ContractMoney", false);
      setPrintable("PaidUpMoney", false);
      setPrintable("NoPayMoney", false);
    }
    //
    if (booleanSource && !stringPurchaseNoExist.startsWith("Y")) {
      // if(booleanSource && !"Y".equals(stringPurchaseNoExist)) {
      arrayMoney = getDoc2M048(stringCDate, stringFactoryNo, stringEDateTime, retDoc6M012, exeUtil, dbDoc);
      if (arrayMoney[3] > 0) {
        // 合約金額
        setValue("ContractMoney", exeUtil.getFormatNum("" + arrayMoney[0], ""));
        // 已付金額、前期估驗
        setValue("PaidUpMoney", exeUtil.getFormatNum("" + arrayMoney[1], ""));
        // 未付金額
        setValue("NoPayMoney", exeUtil.getFormatNum("" + (arrayMoney[0] - arrayMoney[1]), ""));
        setPrintable("ContractMoney", true);
        setPrintable("PaidUpMoney", true);
        setPrintable("NoPayMoney", true);
      }
    }
    // System.out.println("------------------------公司名稱") ;
    stringCompanyName = getCompanyName(stringComNo);
    if ("".equals(stringCompanyName)) {
      messagebox("[公司名稱] 為空白，請重新列印。");
      return false;
    }
    setValue("ComNoPrint", stringCompanyName);
    // 部門
    stringDepartName = getDepartName(stringDepartNo);
    if ("".equals(stringDepartName)) {
      messagebox("[部門名稱] 為空白，請重新列印。");
      return false;
    }
    setValue("DepartPrint", stringDepartName); // 承辦單位：部門代碼
    // 日期
    if (arrayCDate.length != 3) {
      messagebox("[日期] 錯誤，請重新列印。");
      return false;
    }
    setValue("YearPrint", arrayCDate[0]); // 年(452,92)：日期時間
    setValue("MonthPrint", arrayCDate[1]); // 月(493,92)：日期時間
    setValue("DayPrint", arrayCDate[2]); // 日(565,92)：日期時間
    setValue("DocNoPrint", stringDocNo); // 請款編號(891,75)：DocNo
    System.out.println("------------------------案別(186,159)：內業為科別，外業為案別，如為多種類時，列印 [詳附件]");
    if (retDoc6M012.length != 0) {
      stringInOutFirst = retDoc6M012[0][0].trim(); // 內外業 2
      if ("I".equals(stringInOutFirst)) {
        stringProjectFirst = retDoc6M012[0][1].trim();
        stringProjectShow = stringProjectFirst;
      } else {
        stringProjectFirst = retDoc6M012[0][1].trim() + retDoc6M012[0][2].trim() + retDoc6M012[0][3].trim();
        stringProjectShow = retDoc6M012[0][3].trim();
      }
    }
    // System.out.println("------------------------For LOOP") ;
    for (int intRowNo = 0; intRowNo < retDoc6M012.length; intRowNo++) {
      stringInOut = retDoc6M012[intRowNo][0].trim(); // 內外業
      stringDepart = retDoc6M012[intRowNo][1].trim(); // 部門
      stringProjectID = retDoc6M012[intRowNo][2].trim(); // 大案別
      stringProjectID1 = retDoc6M012[intRowNo][3].trim(); // 小案別
      if (stringInOut.equals(stringInOutFirst)) {
        if ("I".equals(stringInOutFirst)) {
          stringProject = stringDepart;
        } else {
          stringProject = stringDepart + stringProjectID + stringProjectID1;
        }
        if (!stringProjectFirst.equals(stringProject)) {
          // 科別不相同
          stringProjectShow = "詳附件";
          // doPrintExcel(stringBarCode, stringComNo, retDoc6M011, retDoc6M012,
          // retDoc6M013, exeUtil) ;
          break;
        }
      } else {
        // 非一致的內外業
        stringProjectShow = "詳附件";
        // doPrintExcel(stringBarCode, stringComNo, retDoc6M011, retDoc6M012,
        // retDoc6M013, exeUtil) ;
        break;
      }
    }
    System.out.println("E------------------------For LOOP");
    // 案別
    if ("".equals(stringProjectShow)) {
      messagebox(" [案別] 為空白，請重新列印。");
      return false;
    }
    setValue("ProjectPrint", stringProjectShow);
    // System.out.println("------------------------摘要、項目說明-----------------"+stringDescript)
    // ;
    if ("".equals(stringDescript)) {
      messagebox(" [摘要] 為空白，請重新列印。");
      return false;
    }
    //
    int intLength = 100;
    String stringFirst = "";
    String stringRemain = convert.replace(stringDescript, "\n", "");
    String[] arrayCutString = exeUtil.doCutStringBySize(intLength, stringRemain);
    String[] arraydescript = { "", "", "", "" };
    // System.out.println("------------------------公文內容") ;
    stringFirst = arrayCutString[0].trim();
    stringRemain = arrayCutString[1].trim();
    setValue("DescriptPrint1-1", stringFirst); // 摘要：公文內容(100*2)。
    setValue("DescriptPrint1-2", stringRemain); // 摘要：公文內容(100*2)。
    //
    stringRemain = convert.replace(stringDescript, "\n", "");
    intLength = 40;
    for (int intNo = 0; intNo < 4; intNo++) {
      arrayCutString = exeUtil.doCutStringBySize(intLength, stringRemain);
      stringFirst = arrayCutString[0].trim();
      stringRemain = arrayCutString[1].trim();
      //
      arraydescript[intNo] = stringFirst;
      if ("".equals(stringRemain)) {
        break;
      }
    }
    setValue("DescriptPrint2-1", arraydescript[0]); // 項目說明(190,327)：公文內容。(40*4)
    setValue("DescriptPrint2-2", arraydescript[1]); // 項目說明(190,327)：公文內容。(40*4)
    setValue("DescriptPrint2-3", arraydescript[2]); // 項目說明(190,327)：公文內容。(40*4)
    setValue("DescriptPrint2-4", arraydescript[3]); // 項目說明(190,327)：公文內容。(40*4)
    // System.out.println("----------------金額") ;
    doubleTemp = 0;
    for (int intNo = 0; intNo < retDoc6M012.length; intNo++) {
      doubleTemp += exeUtil.doParseDouble(retDoc6M012[intNo][7].trim());
    }
    stringRealMoneySumPrint = convert.FourToFive("" + doubleTemp, 0);
    //
    doubleTemp = 0;
    for (int intNo = 0; intNo < retDoc6M013.length; intNo++) {
      doubleTemp += exeUtil.doParseDouble(retDoc6M013[intNo][4].trim());
    }
    if (doubleTemp == 0) {
      String stringBarCodeL = "";
      String[][] retDoc6M013L = null;
      for (int intNo = 0; intNo < retBarCode11.length; intNo++) {
        stringBarCodeL = retBarCode11[intNo][0].trim();
        retDoc6M013L = getDoc6M013(booleanSource ? "Doc6M013" : "Doc5M033", stringBarCodeL);
        for (int intNoL = 0; intNoL < retDoc6M013L.length; intNoL++) {
          doubleTemp += exeUtil.doParseDouble(retDoc6M013L[intNoL][4].trim()) + exeUtil.doParseDouble(retDoc6M013L[intNoL][9].trim());
        }
      }
      // stringCheapenMoney = ""+exeFun.doParseDouble(stringRealMoneySumPrint) *
      // doubleReceiptTaxType ;
      stringCheapenMoney = convert.FourToFive("" + doubleTemp, 0);
      System.out.println("stringCheapenMoney------------------" + stringCheapenMoney);
    } else {
      stringCheapenMoney = convert.FourToFive("" + doubleTemp, 0);
    }
    //
    doubleActualMoney = exeUtil.doParseDouble(stringRealMoneySumPrint) - exeUtil.doParseDouble(stringRetainMoney) - exeUtil.doParseDouble(stringCheapenMoney);
    // stringCheapenMoney = format.format(stringCheapenMoney,
    // "999,999,999,999").trim( ) ;
    stringRetainMoney = format.format(stringRetainMoney, "999,999,999,999").trim();
    // System.out.println("------------------------金額：費用合計。") ;
    if ("".equals(stringRealMoneySumPrint)) {
      messagebox(" [費用合計] 為空白，請重新列印。");
      return false;
    }
    setValue("RealMoneySumPrint", format.format(convert.FourToFive(stringRealMoneySumPrint, 0), "999,999,999,999").trim());
    //
    stringPercent = "";
    if (stringPurchaseNoExist.startsWith("Y")) {
      // if("Y".equals(stringPurchaseNoExist)) {
      if ("Z".equals(stringPurchaseNo4) || !booleanUniPurchase) {
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumPrint) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
      }
    } else {
      if (arrayMoney[3] > 0) {
        stringPercent = convert.FourToFive("" + (arrayMoney[2] / arrayMoney[0]), 2);
      }
    }
    setValue("RealMoneySumPercent", stringPercent);
    // System.out.println("------------------------估驗部份小計：費用合計") ;
    setValue("RealMoneySumPrint2", format.format(convert.FourToFive(stringRealMoneySumPrint, 0), "999,999,999,999").trim());
    setValue("RealMoneySumPercent2", stringPercent);
    // System.out.println("------------------------保留金額：保留金額。") ;
    if ("".equals(stringRetainMoney)) stringRetainMoney = "0";
    setValue("RetainMoneyPrint", stringRetainMoney);
    // System.out.println("------------------------扣款金額：扣款金額。") ;
    if ("".equals(stringCheapenMoney)) stringCheapenMoney = "0";
    setValue("CheapenMoneyPrint", exeUtil.getFormatNum2(stringCheapenMoney));
    // System.out.println("------------------------實付金額(562,556)：實付金額。") ;
    if (doubleActualMoney == 0) {
      messagebox(" [實付金額] 為零，請重新列印。");
      return false;
    }
    setValue("ActualMoneyPrint", format.format(convert.FourToFive("" + doubleActualMoney, 0), "999,999,999,999").trim());
    if (stringPurchaseNoExist.startsWith("Y")) {
      // if("Y".equals(stringPurchaseNoExist)) {
      if ("Z".equals(stringPurchaseNo4)) {
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringExistRealMoney) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
        setValue("RealMoneySumPrePrint", format.format(convert.FourToFive("" + stringExistRealMoney, 0), "999,999,999,999").trim());
        setValue("RealMoneySumPrePercent", stringPercent);
        setValue("RealMoneySumPrePrint2", format.format(convert.FourToFive("" + stringExistRealMoney, 0), "999,999,999,999").trim());
        setValue("RealMoneySumPrePercent2", stringPercent);
        // 累計估驗數額
        String stringRealMoneySumAddUpPrint = "" + (exeUtil.doParseDouble(stringExistRealMoney) + exeUtil.doParseDouble(stringRealMoneySumPrint));
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumAddUpPrint) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
        setValue("RealMoneySumAddUpPrint", format.format(convert.FourToFive("" + stringRealMoneySumAddUpPrint, 0), "999,999,999,999").trim());
        setValue("RealMoneySumAddUpPercent", stringPercent);
        setValue("RealMoneySumAddUpPrint2", format.format(convert.FourToFive("" + stringRealMoneySumAddUpPrint, 0), "999,999,999,999").trim());
        setValue("RealMoneySumAddUpPercent2", stringPercent);
        //
        setPrintable("RealMoneySumPrePrint", true);
        setPrintable("RealMoneySumPrePrint2", true);
        setPrintable("RealMoneySumAddUpPrint", true);
        setPrintable("RealMoneySumAddUpPrint2", true);
        setPrintable("ActualMoneyAddUpPrint", true);
      } else {
        if (!booleanUniPurchase) {
          // String stringExistRealMoney =
          // exeFun.getExistRealMoneyForDoc2M010(stringBarCode, stringCDate,
          // stringPurchaseNo1, stringPurchaseNo2, stringPurchaseNo3) ;
          //
          stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringExistRealMoney) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
          setValue("RealMoneySumPrePrint", format.format(convert.FourToFive("" + stringExistRealMoney, 0), "999,999,999,999").trim());
          setValue("RealMoneySumPrePercent", stringPercent);
          setValue("RealMoneySumPrePrint2", format.format(convert.FourToFive("" + stringExistRealMoney, 0), "999,999,999,999").trim());
          setValue("RealMoneySumPrePercent2", stringPercent);
          // 累計估驗數額
          String stringRealMoneySumAddUpPrint = "" + (exeUtil.doParseDouble(stringExistRealMoney) + exeUtil.doParseDouble(stringRealMoneySumPrint));
          stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumAddUpPrint) / exeUtil.doParseDouble(stringPurchaseMoney)), 2);
          setValue("RealMoneySumAddUpPrint", format.format(convert.FourToFive("" + stringRealMoneySumAddUpPrint, 0), "999,999,999,999").trim());
          setValue("RealMoneySumAddUpPercent", stringPercent);
          setValue("RealMoneySumAddUpPrint2", format.format(convert.FourToFive("" + stringRealMoneySumAddUpPrint, 0), "999,999,999,999").trim());
          setValue("RealMoneySumAddUpPercent2", stringPercent);
          //
          setPrintable("RealMoneySumPrePrint", true);
          setPrintable("RealMoneySumPrePrint2", true);
          setPrintable("RealMoneySumAddUpPrint", true);
          setPrintable("RealMoneySumAddUpPrint2", true);
          setPrintable("ActualMoneyAddUpPrint", true);
        } else {
          setPrintable("RealMoneySumAddUpPercent", false);
          setPrintable("RealMoneySumPrePrint", false);
          setPrintable("RealMoneySumPrePrint2", false);
          setPrintable("RealMoneySumAddUpPrint", false);
          // setPrintable("RealMoneySumAddUpPrint2", false) ;
          setPrintable("ActualMoneyAddUpPrint", false);
        }
      }
    } else {
      if (arrayMoney[3] > 0) {
        stringPercent = convert.FourToFive("" + (arrayMoney[1] / arrayMoney[0]), 2);
        setValue("RealMoneySumPrePrint", exeUtil.getFormatNum2("" + arrayMoney[1]));
        setValue("RealMoneySumPrePercent", stringPercent);
        setValue("RealMoneySumPrePrint2", exeUtil.getFormatNum2("" + arrayMoney[1]));
        setValue("RealMoneySumPrePercent2", stringPercent);
        // 累計估驗數額
        String stringRealMoneySumAddUpPrint = "" + (arrayMoney[1] + arrayMoney[2]);
        stringPercent = convert.FourToFive("" + (exeUtil.doParseDouble(stringRealMoneySumAddUpPrint) / arrayMoney[0]), 2);
        setValue("RealMoneySumAddUpPrint", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint));
        setValue("RealMoneySumAddUpPercent", stringPercent);
        setValue("RealMoneySumAddUpPrint2", exeUtil.getFormatNum2(stringRealMoneySumAddUpPrint));
        setValue("RealMoneySumAddUpPercent2", stringPercent);
        //
        setPrintable("RealMoneySumPrePrint", true);
        setPrintable("RealMoneySumPrePrint2", true);
        setPrintable("RealMoneySumAddUpPrint", true);
        setPrintable("RealMoneySumAddUpPrint2", true);
        setPrintable("ActualMoneyAddUpPrint", true);
      } else {
        setPrintable("RealMoneySumAddUpPercent", false);
        setPrintable("RealMoneySumPrePrint", false);
        setPrintable("RealMoneySumPrePrint2", false);
        setPrintable("RealMoneySumAddUpPrint", false);
        // setPrintable("RealMoneySumAddUpPrint2", false) ;
        setPrintable("ActualMoneyAddUpPrint", false);
      }
    }
    // System.out.println("-------------------------------設定資料END");
    return true;
  }

  public void doSetContractNo(String stringComNo, String stringKindNo, String stringPurchaseNo, String stringFactoryNo, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    if (!"B3018".equals(getUser())) return;
    //
    talk dbConstAsk = getTalk("" + get("put_Const_Ask"));
    String stringBarCodePur = "";
    String stringContractNo = "";
    String stringKindNoFront = "24".equals(stringKindNo) ? "17" : "15";
    String stringPrdocode = "";
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableRelContractDetail = new Hashtable();
    Vector vectorRelContractDetail = null;
    //
    hashtableAnd.put("ComNo", stringComNo);
    hashtableAnd.put("KindNo", stringKindNoFront);
    hashtableAnd.put("DocNo", stringPurchaseNo);
    stringBarCodePur = exeUtil.getNameUnion("BarCode", "Doc3M011", "", hashtableAnd, dbDoc);
    if ("".equals(stringBarCodePur)) return;
    //
    stringPrdocode = exeUtil.getNameUnion("prdocode", "prdt", " AND  social  =  '" + stringFactoryNo + "' ", new Hashtable(), dbConstAsk);
    //
    hashtableAnd.put("barcode", stringBarCodePur);
    hashtableAnd.put("prdocode", stringPrdocode);
    vectorRelContractDetail = exeUtil.getQueryDataHashtable("rel_contract_detail", hashtableAnd, "", dbConstAsk);
    if (vectorRelContractDetail.size() == 0) return;
    hashtableRelContractDetail = (Hashtable) vectorRelContractDetail.get(0);
    if (hashtableRelContractDetail == null) return;
    //
    String stringCcaseCode = ("" + hashtableRelContractDetail.get("ccasecode")).trim();
    if ("null".equals(stringCcaseCode)) stringCcaseCode = "";
    String stringContractIID = ("" + hashtableRelContractDetail.get("contract_iid")).trim();
    if ("null".equals(stringContractIID)) stringContractIID = "";
    //
    setValue("ContractNo", stringCcaseCode + "-" + stringContractIID);
  }

  // 借款
  public boolean doPrint3(String stringBarCode, String stringPrevFunction, boolean booleanSource, FargloryUtil exeUtil) throws Throwable {
    String stringDepartNo = "";
    Vector vectorDoc6M010 = new Vector();
    Vector vectorDoc6M011 = new Vector();
    Vector vectorDoc6M013 = new Vector();
    Vector vectorDoc2M014 = new Vector();
    Hashtable hashtableDoc6M010 = new Hashtable();
    vectorDoc6M010 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc6M010" : "Doc5M030", new Hashtable(), " AND  BarCode = '" + stringBarCode + "' ", dbDoc);
    if (vectorDoc6M010.size() == 0) {
      ;
      messagebox("查無資料。");
      return false;
    }
    hashtableDoc6M010 = (Hashtable) vectorDoc6M010.get(0);
    stringDepartNo = "" + hashtableDoc6M010.get("DepartNo");
    setValue("DeifyDepart", "");
    if ("033FZ".equals(stringDepartNo)) {
      setValue("DeifyDepart", "敬會人總室");
    }
    String stringPayType = "" + hashtableDoc6M010.get("PayType");
    if (!"C".equals(stringPayType) && !"D".equals(stringPayType)) {
      vectorDoc6M011 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc6M011" : "Doc5M031", new Hashtable(), " AND  BarCode = '" + stringBarCode + "' ", dbDoc);
      if (vectorDoc6M011.size() == 0) {
        vectorDoc6M013 = exeUtil.getQueryDataHashtable(booleanSource ? "Doc6M013" : "Doc5M033", new Hashtable(), " AND  BarCode = '" + stringBarCode + "' ", dbDoc);
        if (vectorDoc6M013.size() == 0) {
          messagebox("查無 [發票] 或 [扣繳] 資料。");
          return false;
        }
      }
    }
    // 已轉傳票判斷
    vectorDoc2M014 = exeUtil.getQueryDataHashtable("Doc2M014", new Hashtable(), " AND  BarCode = '" + stringBarCode + "' ", dbDoc);
    if (vectorDoc2M014.size() == 0 || !"Z".equals(exeUtil.getVectorFieldValue(vectorDoc2M014, 0, "STATUS_CD"))) {
      setValue("VoucherDATA", "");
    } else {
      String stringVoucherYMD = exeUtil.getVectorFieldValue(vectorDoc2M014, 0, "VOUCHER_YMD");
      String stringVoucherFlowNo = exeUtil.getVectorFieldValue(vectorDoc2M014, 0, "VOUCHER_FLOW_NO");
      String stringVoucherDATA = "借款傳票號碼：" + stringVoucherYMD + "-" + convert.add0(stringVoucherFlowNo, "4");
      setValue("VoucherDATA", stringVoucherDATA);
    }
    // 承辦人 及 列印日期
    int intPos = 0;
    String stringOriEmployeeNo = "" + hashtableDoc6M010.get("OriEmployeeNo");
    String stringToday = datetime.getToday("yy/mm/dd");
    //
    intPos = stringToday.indexOf("/");
    stringToday = stringToday.substring(intPos + 1);
    //
    // setValue("OriEmployeeNo", getEmpName(stringOriEmployeeNo)) ;
    // setValue("PrintDateTime", stringToday) ;
    return setData3(stringPrevFunction, hashtableDoc6M010, vectorDoc6M011, vectorDoc6M013, booleanSource, exeUtil);
  }

  public boolean setData3(String stringPrevFunction, Hashtable hashtableDoc6M010, Vector vectorDoc6M011, Vector vectorDoc6M013, boolean booleanSource, FargloryUtil exeUtil)
      throws Throwable {
    //
    String stringDepartNo = ("" + hashtableDoc6M010.get("DepartNo")).trim();
    String stringCDate = ("" + hashtableDoc6M010.get("CDate")).trim();
    String stringNeedDate = ("" + hashtableDoc6M010.get("NeedDate")).trim();
    String stringDocNo = ("" + hashtableDoc6M010.get("DocNo")).trim();
    String stringDescript = ("" + hashtableDoc6M010.get("Descript")).trim();
    String stringComNo = ("" + hashtableDoc6M010.get("ComNo")).trim();
    String stringBarCode = ("" + hashtableDoc6M010.get("BarCode")).trim();
    String stringPrintCount = ("" + hashtableDoc6M010.get("PrintCount")).trim();
    String stringPayType = ("" + hashtableDoc6M010.get("PayType")).trim();
    String stringDestineExpenseDate = ("" + hashtableDoc6M010.get("DestineExpenseDate")).trim();
    String stringBorrowMoney = ("" + hashtableDoc6M010.get("BorrowMoney")).trim();
    String stringFactoryNo = ("" + hashtableDoc6M010.get("FactoryNo")).trim();
    String stringPayConditionText1 = ("" + hashtableDoc6M010.get("PayCondition1")).trim();
    String stringPayConditionText2 = ("" + hashtableDoc6M010.get("PayCondition2")).trim();
    String stringCTime = ("" + hashtableDoc6M010.get("CTime")).trim();
    String stringCompanyName = "";
    String stringDepartName = "";
    String stringFactoryName = "";
    String stringRealMoneySumPrint = "";
    String stringCheapenMoney = "";
    String[] arrayCDate = convert.StringToken(exeUtil.getDateConvert(stringCDate), "/");
    double doubleActualMoney = 0;
    double doubleCheapenMoney = 0;
    double doubleTemp = 0;
    double doubleTemp2 = 0;
    double doubleTemp3 = 0;
    Hashtable hashtableDoc6M011 = null;
    Hashtable hashtableDoc6M013 = null;
    //
    // 付款條件
    /*
     * if(!"999".equals(stringPayConditionText1)) {
     * if("000".equals(stringPayConditionText1)) { stringPayConditionText1 =
     * "付款條件1：現金。" ; } else if("0".equals(stringPayConditionText1)) {
     * stringPayConditionText1 = "付款條件1：即期。" ; } else { stringPayConditionText1 =
     * "付款條件1：" + stringPayConditionText1 + " 天。" ; } setValue("PayConditionText1",
     * stringPayConditionText1) ; setVisible("PayConditionText1", true) ; } else {
     * setValue("PayConditionText1", "") ; setVisible("PayConditionText1", false) ;
     * } if(!"999".equals(stringPayConditionText2)) {
     * if("000".equals(stringPayConditionText2)) { stringPayConditionText2 =
     * "付款條件2：現金。" ; } else if("0".equals(stringPayConditionText2)) {
     * stringPayConditionText2 = "付款條件2：即期。" ; } else { stringPayConditionText2 =
     * "付款條件2：" + stringPayConditionText2 + " 天。" ; }
     * setVisible("PayConditionText2", true) ; setValue("PayConditionText2",
     * stringPayConditionText2) ; } else { setValue("PayConditionText2", "") ;
     * setVisible("PayConditionText2", false) ; }
     */
    // 報銷方式
    if ("".equals(stringPayType)) {
      stringPayType = vectorDoc6M013.size() > 0 ? "A" : "B";
    }
    setVisible("PayTypePrint_1", (stringPayType.trim().equals("A")));
    setVisible("PayTypePrint_2", (stringPayType.trim().equals("B")));
    setVisible("PayTypePrint_3", (stringPayType.trim().equals("C")));
    setVisible("PayTypePrint_4", (stringPayType.trim().equals("D")));
    // 次數
    stringPrintCount = "" + exeUtil.doParseInteger(stringPrintCount);
    setValue("PrintCount", stringPrintCount);
    // 條碼編號
    if ("".equals(stringBarCode)) {
      messagebox("[條碼編號] 為空白，請重新列印。");
      return false;
    }
    setValue("BarCodePrint", stringBarCode);
    setVisible("BarCodePrint", stringPrevFunction.indexOf("行銷-借款申請書-承辦") == -1);

    // System.out.println("------------------------公司名稱") ;
    stringCompanyName = getCompanyName(stringComNo);
    if ("".equals(stringCompanyName)) {
      messagebox("[公司名稱] 為空白，請重新列印。");
      return false;
    }
    setValue("ComNoPrint", stringCompanyName);
    // 案別
    setValue("ProjectPrint", stringDepartNo);
    // 部門
    stringDepartName = getDepartName(stringDepartNo);
    if ("".equals(stringDepartName)) {
      messagebox("[部門名稱] 為空白，請重新列印。");
      return false;
    }
    setValue("DepartPrint", stringDepartName); // 承辦單位：部門代碼
    // System.out.println("------------------------廠商名稱：由統一編號查詢廠商名稱") ;
    if (!"C".equals(stringPayType) && !"D".equals(stringPayType)) {
      if (vectorDoc6M011.size() > 0) {
        hashtableDoc6M011 = (Hashtable) vectorDoc6M011.get(0);
        stringFactoryNo = ("" + hashtableDoc6M011.get("FactoryNo")).trim(); // 統一編號
      } else {
        hashtableDoc6M013 = (Hashtable) vectorDoc6M013.get(0);
        stringFactoryNo = ("" + hashtableDoc6M013.get("FactoryNo")).trim(); // 統一編號
      }
    }
    setValue("FactoryPrint", stringFactoryNo);
    String[][] retFED1005 = getFED1005(stringFactoryNo);
    if (retFED1005.length == 0) {
      messagebox(" [廠商名稱] 為空白，請重新列印。");
      return false;
    }
    doPutFactoryName(retFED1005[0][0].trim(), exeUtil);
    // setValue("FactoryNoPrint", retFED1005[0][0].trim()) ;
    setValue("TELPrint", retFED1005[0][1].trim());
    // 借款單編號
    setValue("DocNoPrint", stringDocNo); // 請款編號(891,75)：DocNo
    // 日期
    if (arrayCDate.length != 3) {
      messagebox("[日期] 錯誤，請重新列印。");
      return false;
    }
    setValue("YearPrint", arrayCDate[0]); // 年(452,92)：日期時間
    setValue("MonthPrint", arrayCDate[1]); // 月(493,92)：日期時間
    setValue("DayPrint", arrayCDate[2]); // 日(565,92)：日期時間
    setValue("CTimePrint", stringCTime);
    // 需用日期
    arrayCDate = convert.StringToken(exeUtil.getDateConvert(stringNeedDate), "/");
    setValue("YearPrint2", arrayCDate[0]); // 年(452,92)：日期時間
    setValue("MonthPrint2", arrayCDate[1]); // 月(493,92)：日期時間
    setValue("DayPrint2", arrayCDate[2]); // 日(565,92)：日期時間
    // 預定報銷日期
    arrayCDate = convert.StringToken(exeUtil.getDateConvert(stringDestineExpenseDate), "/");
    if (arrayCDate.length != 3) {
      messagebox("[預定報銷日期] 錯誤，請重新列印。");
      return false;
    }
    setValue("YearPrint3", arrayCDate[0]); // 年(452,92)：日期時間
    setValue("MonthPrint3", arrayCDate[1]); // 月(493,92)：日期時間
    setValue("DayPrint3", arrayCDate[2]); // 日(565,92)：日期時間
    // 報銷方式
    // System.out.println("------------------------摘要、項目說明") ;
    if ("".equals(stringDescript)) {
      messagebox(" [摘要] 為空白，請重新列印。");
      return false;
    }
    //
    int intLength = 38;
    String stringTemp = "";
    String stringFirst = "";
    String stringRemain = convert.replace(stringDescript, "\n", "");
    // stringRemain = convert.replace(stringDescript, " ", "") ;
    /*
     * String[] arrayCutString = exeUtil.doCutStringBySize(intLength, stringRemain)
     * ; String[] arraydescript = {"", "", "", ""} ;
     * System.out.println("------------------------公文內容-----------------"+
     * stringDescript) ; stringFirst = arrayCutString[0].trim( ) ; stringRemain =
     * arrayCutString[1].trim( ) ; setVisible("DescriptPrint1-1", false) ;
     * setVisible("DescriptPrint1-2", false) ; // oce
     * System.out.println("stringFirst-----------------------------------------"+
     * stringFirst) ;
     * System.out.println("stringRemain-----------------------------------------"+
     * stringRemain) ; setValue("DescriptPrint2_1", stringFirst) ; //
     * 摘要：公文內容(100*2)。 //if("B3018".equals(getUser())) messagebox() ; arrayCutString
     * = exeUtil.doCutStringBySize(intLength, stringRemain) ; stringFirst =
     * arrayCutString[0].trim( ) ; stringRemain = arrayCutString[1].trim( ) ;
     * setValue("DescriptPrint2_2", stringFirst) ; // 摘要：公文內容(100*2)。 arrayCutString
     * = exeUtil.doCutStringBySize(intLength, stringRemain) ; stringFirst =
     * arrayCutString[0].trim( ) ; stringRemain = arrayCutString[1].trim( ) ;
     * setValue("DescriptPrint2_3", stringFirst) ; setValue("DescriptPrint2_4",
     * stringRemain) ;
     */
    doPutValues(5, 38, 0, stringRemain, "DescriptPrint2_", exeUtil);
    //
    // System.out.println("----------------金額"+stringBorrowMoney) ;
    stringRealMoneySumPrint = stringBorrowMoney;
    if (!"C".equals(stringPayType) && !"D".equals(stringPayType)) {
      doubleTemp = 0;
      for (int intNo = 0; intNo < vectorDoc6M011.size(); intNo++) {
        hashtableDoc6M011 = (Hashtable) vectorDoc6M011.get(intNo);
        doubleTemp += exeUtil.doParseDouble("" + hashtableDoc6M011.get("InvoiceTotalMoney"));
      }
      stringRealMoneySumPrint = convert.FourToFive("" + doubleTemp, 0);
      //
      doubleTemp = 0;
      doubleTemp2 = 0;
      doubleTemp3 = 0;
      for (int intNo = 0; intNo < vectorDoc6M013.size(); intNo++) {
        hashtableDoc6M013 = (Hashtable) vectorDoc6M013.get(intNo);
        doubleTemp += exeUtil.doParseDouble("" + hashtableDoc6M013.get("ReceiptTax"));
        doubleTemp3 += exeUtil.doParseDouble("" + hashtableDoc6M013.get("SupplementMoney"));
        doubleTemp2 += exeUtil.doParseDouble("" + hashtableDoc6M013.get("ReceiptTotalMoney"));
        System.out.println(intNo + "ReceiptTax(" + convert.FourToFive("" + doubleTemp, 0) + ")--------------------------------------");
        System.out.println(intNo + "SupplementMoney(" + convert.FourToFive("" + doubleTemp3, 0) + ")--------------------------------------");
      }
      // System.out.println(doubleTemp+"----------------"+doubleTemp2) ;
      if (doubleTemp2 > 0) {
        // 補充保費 修正 最大值 稅1,234,567+保費1,234,567=12,345,678
        stringRealMoneySumPrint = convert.FourToFive("" + doubleTemp2, 0);
        doubleCheapenMoney = doubleTemp + doubleTemp3;
        stringCheapenMoney = "稅" + exeUtil.getFormatNum2("" + doubleTemp) + "+保費" + exeUtil.getFormatNum2("" + doubleTemp3) + "=" + exeUtil.getFormatNum2("" + doubleCheapenMoney);
        stringTemp = code.StrToByte(stringCheapenMoney);
        if (stringTemp.length() > 36) {
          stringCheapenMoney = "稅" + convert.FourToFive("" + doubleTemp, 0) + "+保費" + convert.FourToFive("" + doubleTemp3, 0) + "="
              + exeUtil.getFormatNum2("" + doubleCheapenMoney);
          stringTemp = code.StrToByte(stringCheapenMoney);
          if (stringTemp.length() > 36) {
            stringCheapenMoney = "稅" + convert.FourToFive("" + doubleTemp, 0) + "+保費" + convert.FourToFive("" + doubleTemp3, 0) + "="
                + convert.FourToFive("" + doubleCheapenMoney, 0);
          }
        }
      }
    }
    //
    doubleActualMoney = exeUtil.doParseDouble(stringRealMoneySumPrint) - doubleCheapenMoney;
    // System.out.println(stringRealMoneySumPrint+"------------------------金額：費用合計。"+stringRealMoneySumPrint+"-----------"+stringCheapenMoney)
    // ;
    if ("".equals(stringRealMoneySumPrint)) {
      messagebox(" [借款金額] 為空白，請重新列印。");
      return false;
    }
    setValue("RealMoneySumPrint", format.format(convert.FourToFive(stringRealMoneySumPrint, 0), "999,999,999,999").trim());
    //
    setVisible("RealMoneySumPercent", false);
    // System.out.println("------------------------估驗部份小計：費用合計") ;
    setVisible("RealMoneySumPrint2", false);
    setVisible("RealMoneySumPercent2", false);
    // System.out.println("------------------------保留金額：保留金額。") ;
    setVisible("RetainMoneyPrint", false);
    // System.out.println("------------------------扣款金額：扣款金額。") ;
    if ("".equals(stringCheapenMoney)) stringCheapenMoney = "0";
    setValue("CheapenMoneyPrint", stringCheapenMoney);
    // System.out.println("------------------------實付金額(562,556)：實付金額。") ;
    if (doubleActualMoney == 0) {
      messagebox(" [實付金額] 為零，請重新列印。");
      return false;
    }
    setValue("ActualMoneyPrint", format.format(convert.FourToFive("" + doubleActualMoney, 0), "999,999,999,999").trim());
    // System.out.println("-------------------------------設定資料END");
    return true;
  }

  /*
   * public int doPutValues(int intCount, int intSize, int intStart, String
   * stringValue, String stringFieldName, FargloryUtil exeUtil) throws Throwable {
   * int intPos = 0 ; int intCountL = intCount ; if(intCountL <= 0) intCountL = 1
   * ; String stringFieldNameL = "" ; String stringTemp = stringValue ; String[]
   * arrayTemp = null ; if("".equals(stringValue)) return -1 ; for(int intNo=1 ;
   * intNo<=intCountL ; intNo++) { arrayTemp = exeUtil.doCutStringBySize(intSize,
   * stringTemp) ; // intPos = intNo+intStart ; stringFieldNameL =
   * stringFieldName+""+intPos ; if(intCount == 0) stringFieldNameL =
   * stringFieldName ; // if(!"".equals(stringFieldName)) {
   * doSetValue(stringFieldNameL, arrayTemp[0].trim()) ; } //
   * if("".equals(arrayTemp[1])) break ; // stringTemp = arrayTemp[1].trim() ; }
   * return intPos ; }
   */
  public void doPutFactoryName(String stringFactoryNoPrint, FargloryUtil exeUtil) throws Throwable {
    if (code.StrToByte(stringFactoryNoPrint).length() > 14) {
      setVisible("FactoryNoPrint1", true);
      setVisible("FactoryNoPrint2", true);
      setVisible("FactoryNoPrint", false);
      doPutValues(2, 14, 0, stringFactoryNoPrint, "FactoryNoPrint", exeUtil);
    } else {
      setVisible("FactoryNoPrint1", false);
      setVisible("FactoryNoPrint2", false);
      setVisible("FactoryNoPrint", true);
      setValue("FactoryNoPrint", stringFactoryNoPrint);
    }
  }

  public int doPutValues(int intCount, int intSize, int intStart, String stringValue, String stringField, FargloryUtil exeUtil) throws Throwable {
    int intPos = 0;
    String stringFieldName = "";
    String stringTemp = stringValue;
    String[] arrayTemp = null;
    if ("".equals(stringValue)) return -1;
    for (int intNo = 1; intNo <= intCount; intNo++) {
      stringFieldName = stringField + "" + intNo;
      arrayTemp = exeUtil.doCutStringBySize(intSize, stringTemp);
      intPos = intNo + intStart;
      //
      System.out.println("" + stringField + "" + intNo + ")(" + stringValue + ")(" + arrayTemp[0] + ")---------------------------------");
      setVisible(stringField + "" + intPos, true);
      setValue(stringField + "" + intPos, arrayTemp[0].trim());
      //
      if ("".equals(arrayTemp[1])) break;
      //
      stringTemp = arrayTemp[1].trim();
    }
    return intPos;
  }

  public String getDeleteZero(String stringValue, FargloryUtil exeUtil) throws Throwable {
    char charWord = 'A';
    char[] arrayChar = stringValue.toCharArray();
    if (stringValue.indexOf(".") == -1) return stringValue;
    if ("".equals(stringValue)) return stringValue;
    for (int intNo = arrayChar.length - 1; intNo >= 0; intNo--) {
      charWord = arrayChar[intNo];
      if (".".equals("" + charWord)) break;
      if (exeUtil.doParseDouble("" + charWord) > 0) break;
      stringValue = exeUtil.doSubstring(stringValue, 0, intNo);
    }
    return stringValue;
  }

  // 2014-10-02
  public double getExistRealMoney(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo, String stringKindNo,
      String stringBarCodePur, String stringGroupID, boolean booleanSpecPurchaseNo, boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    double doubleTemp = 0;
    double doubleExistRealMoney = 0;
    // 請款申請書
    // doubleTemp = getPurchaseMoneyUSEDoc2M017(stringBarCode, stringComNo,
    // stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo,
    // booleanSource, exeUtil) ;
    doubleTemp = getPurchaseMoneyDoc2USE(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, stringBarCodePur, stringGroupID,
        booleanSource, booleanSpecPurchaseNo, exeUtil, dbDoc);
    doubleExistRealMoney += doubleTemp;
    // 借款申請書
    // doubleTemp = getPurchaseMoneyUSEDoc6M012(stringBarCode, stringComNo,
    // stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo,
    // booleanSource, exeUtil, dbDoc) ;
    doubleTemp = getPurchaseMoneyDoc6USE(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, stringBarCodePur, stringGroupID,
        booleanSource, booleanSpecPurchaseNo, exeUtil, dbDoc);
    doubleExistRealMoney += doubleTemp;
    // 未在系統已使用
    doubleTemp = getUSEMoney(stringComNo, stringPurchaseNo, stringFactoryNo, exeUtil);
    doubleExistRealMoney += doubleTemp;
    return doubleExistRealMoney;
  }

  public double getPurchaseMoneyDoc2USE(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo, String stringKindNo,
      String stringBarCodePur, String stringGroupID, boolean booleanSource, boolean booleanSpecPurchaseNo, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    if (booleanSpecPurchaseNo) {
      return getPurchaseMoneyUSEDoc2M0171(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, stringBarCodePur, stringGroupID,
          booleanSource, exeUtil, dbDoc);
    } else {
      return getPurchaseMoneyUSEDoc2M017(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, booleanSource, exeUtil, dbDoc);
    }
  }

  public double getPurchaseMoneyDoc6USE(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo, String stringKindNo,
      String stringBarCodePur, String stringGroupID, boolean booleanSource, boolean booleanSpecPurchaseNo, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    if (booleanSpecPurchaseNo) {
      return getPurchaseMoneyUSEDoc6M0171(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, stringBarCodePur, stringGroupID,
          booleanSource, exeUtil, dbDoc);
    } else {
      return getPurchaseMoneyUSEDoc6M012(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, booleanSource, exeUtil, dbDoc);
    }
  }

  // 資料庫
  // 資料庫 Doc
  public void doAddPrintCount(String stringTable, String stringPrintCount, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    stringSql = "UPDATE  " + stringTable + "  SET  PrintCount  =  " + stringPrintCount + " " + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    dbDoc.execFromPool(stringSql);
  }

  // 表格 Doc2M010
  public String[][] getDoc2M010(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M010 = null;
    // 0 ComNo 1 Descript 2 3 BarCode 4 DocNo
    // 5 UNDERGO_WRITE 6 7 8 9 DocNo1
    // 10 DocNo2 11 DocNo3 12 DepartNo 13 RetainMoney 14 PayCondition1
    // 15 PayCondition2 16 CDate 17 OriEmployeeNo 18 Descript 19 PreFinDate
    // 20 PurchaseNoExist 21 PrintCount 22 EDateTime 23 CTime 24 AccountCount
    // 25 WriteRetainMoney 26 RetainBarCode 27 DocNoType 28 FactoryNoSpec 29
    // LastPayDate
    // 30 KindNo 31 CoinType
    stringSql = "SELECT  ComNo,                        Descript,                 '',                          BarCode,               DocNo, "
        + " UNDERGO_WRITE,     '',                              '',                           '',                            DocNo1, "
        + " DocNo2,                    DocNo3,                  DepartNo,            RetainMoney,       PayCondition1, "
        + " PayCondition2,        CDate,                     OriEmployeeNo,  Descript,               PreFinDate, "
        + " PurchaseNoExist,    PrintCount,             EDateTime,          CTime,                  AccountCount, "
        + " WriteRetainMoney,  RetainBarCode,     DocNoType,         FactoryNoSpec,  LastPayDate, " + " KindNo,                      CoinType " + " FROM  " + stringTable + " "
        + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc2M010 = dbDoc.queryFromPool(stringSql);
    return retDoc2M010;
  }

  public String getExistFactoryNoRealMoneyForDoc2M010(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo,
      String stringKindNo, boolean booleanSource, FargloryUtil exeUtil) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    return "" + getPurchaseMoneyUSEDoc2M017(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, booleanSource, exeUtil, dbDoc);
  }

  public double getPurchaseMoneyUSEDoc2M017(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo, String stringKindNo,
      boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSumRealMoney = "0";
    String stringSql = "";
    String stringTable10 = booleanSource ? "Doc2M010" : "Doc5M020";
    String stringTable17 = booleanSource ? "Doc2M017" : "Doc5M027";
    String[][] retDoc2M017 = null;
    //
    stringSql = "SELECT  SUM(M17.PurchaseMoney) " + "FROM  " + stringTable10 + " M10,  " + stringTable17 + " M17 " + " WHERE  M10.BarCode  =  M17.BarCode "
        + " AND  M10.UNDERGO_WRITE  <>  'E' " + " AND  M10.ComNo  =  '" + stringComNo + "' " + " AND  M17.PurchaseNo  =  '" + stringPurchaseNo + "' " + " AND  M10.KindNo  =  '"
        + stringKindNo + "' " + " AND  M10.BarCode  <>  '" + stringBarCode + "' " + " AND  M10.EDateTime  <  '" + stringEDateTime + "' " + " AND  M17.FactoryNo  =  '"
        + stringFactoryNo + "' ";
    retDoc2M017 = dbDoc.queryFromPool(stringSql);
    if (retDoc2M017.length != 0) {
      stringSumRealMoney = retDoc2M017[0][0].trim();
      stringSumRealMoney = "" + exeUtil.doParseDouble(stringSumRealMoney);
      stringSumRealMoney = convert.FourToFive(stringSumRealMoney, 0);
    }
    return exeUtil.doParseDouble(stringSumRealMoney);
  }

  public double getPurchaseMoneyUSEDoc2M0171(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo, String stringKindNo,
      String stringBarCodePur, String stringGroupID, boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSumRealMoney = "0";
    String stringSql = "";
    String stringTable210 = booleanSource ? "Doc2M010" : "Doc5M020";
    String stringTable217 = booleanSource ? "Doc2M0171" : "Doc5M0272";
    String stringTable312 = booleanSource ? "Doc3M012" : "Doc5M012";
    String[][] retDoc2M017 = null;
    //
    stringSql = "SELECT  SUM(M17.PurchaseMoney) " + "FROM  " + stringTable210 + " M10,  " + stringTable217 + " M17 " + " WHERE  M10.BarCode  =  M17.BarCode "
        + " AND  M10.UNDERGO_WRITE  <>  'E' " + " AND  M17.RecordNo12  IN (SELECT  RecordNo " + " FROM  " + stringTable312 + " " + " WHERE  BarCode  =  '" + stringBarCodePur + "' "
        + " AND  GroupID  =  '" + stringGroupID + "' ) " + " AND  M10.ComNo  =  '" + stringComNo + "' " + " AND  M17.PurchaseNo  =  '" + stringPurchaseNo + "' "
        + " AND  M10.KindNo  =  '" + stringKindNo + "' " + " AND  M10.BarCode  <>  '" + stringBarCode + "' " + " AND  M10.EDateTime  <  '" + stringEDateTime + "' "
        + " AND  M17.FactoryNo  =  '" + stringFactoryNo + "' ";
    retDoc2M017 = dbDoc.queryFromPool(stringSql);
    if (retDoc2M017.length != 0) {
      stringSumRealMoney = retDoc2M017[0][0].trim();
      stringSumRealMoney = "" + exeUtil.doParseDouble(stringSumRealMoney);
      stringSumRealMoney = convert.FourToFive(stringSumRealMoney, 0);
    }
    return exeUtil.doParseDouble(stringSumRealMoney);
  }

  // 表格 Doc5M0201
  public String[][] getDoc5M0201(String stringTable, String stringBarCode, String stringSqlAnd) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringSqlTemp = "";
    String[][] retDoc5M0201 = null;
    // 0 DocCode 1 DocCount
    stringSql = "SELECT  DocCode,    DocCount,  DocTXT " + " FROM  " + stringTable + " " + " WHERE  DocCount  >  0 ";
    if (!"".equals(stringBarCode)) stringSql += " AND  BarCode  =  '" + stringBarCode + "' ";
    stringSql += stringSqlAnd + " ORDER BY  RecordNo ";
    retDoc5M0201 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0201;
  }

  // 表格 Doc5M0291
  public String getDocDescriptDoc5M0291(String stringDocCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc5M0291 = null;
    //
    stringSql = "SELECT  DocDescript " + " FROM  Doc5M0291 " + " WHERE  DocCode  =  '" + stringDocCode + "' ";
    retDoc5M0291 = dbDoc.queryFromPool(stringSql);
    if (retDoc5M0291.length > 0) return retDoc5M0291[0][0].trim();
    return "";
  }

  // 表格 Doc2M011
  public String[][] getDoc2M011(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M011 = null;
    // 0 FactoryNo 1 InvoiceKind 2 InvoiceDate 3 InvoiceNo 4 InvoiceMoney
    // 5 InvoiceTax 6 InvoiceTotalMoney 7 DeductKind 8 RecordNo
    stringSql = "SELECT  FactoryNo,   InvoiceKind,               InvoiceDate,  InvoiceNo,  InvoiceMoney, " + " InvoiceTax,  InvoiceTotalMoney,  DeductKind,   RecordNo " + " FROM  "
        + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' " + " ORDER BY  RecordNo ";
    retDoc2M011 = dbDoc.queryFromPool(stringSql);
    return retDoc2M011;
  }

  // 表格 Doc2M011
  public String[][] getDoc2M012(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M012 = null;
    // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
    // 5 CostID1 6 RealMoney 7 RealTotalMoney
    stringSql = "SELECT  InOut,       DepartNo,     ProjectID,             ProjectID1,  CostID, " + " CostID1,  RealMoney,  RealTotalMoney " + " FROM  " + stringTable + " "
        + " WHERE  BarCode  =  '" + stringBarCode + "' " + " ORDER BY  RecordNo ";
    retDoc2M012 = dbDoc.queryFromPool(stringSql);
    return retDoc2M012;
  }

  // 表格 Doc2M013
  public String[][] getDoc2M013(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M013 = null;
    // 0 FactoryNo 1 ReceiptKind 2 ReceiptDate 3 ReceiptMoney 4 ReceiptTax
    // 5 ReceiptTotalMoney 6 ReceiptTaxType 7 ACCT_NO 8 PayCondition1 9
    // SupplementMoney
    stringSql = "SELECT  FactoryNo,                 ReceiptKind,         ReceiptDate,  ReceiptMoney,  ReceiptTax, "
        + " ReceiptTotalMoney,  ReceiptTaxType,  ACCT_NO,       PayCondition1,  SupplementMoney " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' "
        + " ORDER BY  RecordNo ";
    retDoc2M013 = dbDoc.queryFromPool(stringSql);
    return retDoc2M013;
  }

  // 表格 Doc2M0143
  public String[][] getDoc2M0143(String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M0143 = null;
    // 0 BarCode
    stringSql = "SELECT  BarCode " + " FROM  Doc2M0143 " + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc2M0143 = dbDoc.queryFromPool(stringSql);
    return retDoc2M0143;
  }

  // 表格 Doc2M017
  public String[][] getDoc2M017(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M017 = null;
    // 0 PurchaseNo1 1 PurchaseNo2 2 PurchaseNo3 3 RetainMoney 4 PurchaseMoney
    // 5 PurchaseNo4 6 FactoryNo 7 ProjectID1
    stringSql = "SELECT  PurchaseNo1,  PurchaseNo2,  PurchaseNo3,  RetainMoney,  PurchaseMoney, " + " PurchaseNo4,  FactoryNo,        ProjectID1 " + " FROM  " + stringTable + " "
        + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc2M017 = dbDoc.queryFromPool(stringSql);
    return retDoc2M017;
  }

  public String[][] getDoc2M018(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M018 = null;
    // 0 OptometryNo1 1 OptometryNo2 2 OptometryNo3 3 OptometryType 4 PurchaseNo
    // 5 OptometryType 6 ProjectID1
    stringSql = "SELECT  OptometryNo1,  OptometryNo2,  OptometryNo3,  OptometryType,  PurchaseNo,  OptometryType,  ProjectID1 " + " FROM  " + stringTable + " "
        + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc2M018 = dbDoc.queryFromPool(stringSql);
    return retDoc2M018;
  }

  public String getCostID1View(String stringComNo, String stringCostID, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc2M020 = null;
    //
    stringSql = " SELECT  DESCRIPTION " + " FROM  Doc2M020 " + " WHERE  ComNo  =  '" + stringComNo + "' " + " AND  RTRIM(CostID)+RTRIM(CostID1)  =  '" + stringCostID + "' ";
    retDoc2M020 = dbDoc.queryFromPool(stringSql);
    if (retDoc2M020.length != 0) {
      stringDescription = retDoc2M020[0][0].trim();
    }
    return stringDescription;
  }

  // 表格 Doc3M010
  public boolean getUniPurchase(String stringCom, String stringPurchaseNo1, String stringPurchaseNo2, String stringPurchaseNo3, String stringPurchaseNo4) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M010 = null;
    boolean booleanUniPurchase = false;
    //
    stringSql = "SELECT  Unipurchase " + " FROM  Doc3M010 " + " WHERE  DocNo1  =  '" + stringPurchaseNo1 + "' " + " AND  DocNo2  =  '" + stringPurchaseNo2 + "' "
        + " AND  DocNo3  =  '" + stringPurchaseNo3 + "' " + " AND  ComNo  =  '" + stringCom + "' ";
    if (!"".equals(stringPurchaseNo4)) stringSql += " AND  DocNo4  =  '" + stringPurchaseNo4 + "' ";
    retDoc3M010 = dbDoc.queryFromPool(stringSql);
    if (retDoc3M010.length != 0 && "Y".equals(retDoc3M010[0][0].trim())) booleanUniPurchase = true;
    return booleanUniPurchase;
  }

  public String getPurchaseMoney(String stringCom, String stringPurchaseNo1, String stringPurchaseNo2, String stringPurchaseNo3, String stringPurchaseNo4) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringPurchaseMoney = "0";
    String[][] retDoc3M010 = null;
    //
    stringSql = "SELECT  PurchaseMoney " + " FROM  Doc3M010 " + " WHERE  DocNo1  =  '" + stringPurchaseNo1 + "' " + " AND  DocNo2  =  '" + stringPurchaseNo2 + "' "
        + " AND  DocNo3  =  '" + stringPurchaseNo3 + "' " + " AND  DocNo4  =  '" + stringPurchaseNo4 + "' " + " AND  ComNo  =  '" + stringCom + "' ";
    retDoc3M010 = dbDoc.queryFromPool(stringSql);
    if (retDoc3M010.length != 0) stringPurchaseMoney = retDoc3M010[0][0].trim();
    return stringPurchaseMoney;
  }

  public String[][] getDoc3M010(String stringPurchaseNo1, String stringPurchaseNo2, String stringPurchaseNo3, String stringPurchaseNo4, String stringComNo) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M010 = null;
    // 0 ComNo 1 DepartNo 2 FactoryNo 3 PayCondition1 4 PayCondition2
    // 5 PurchaseMoney 6 Unipurchase 7 Descript 8 BarCode 9 CostID
    // 10 CostID1 11 ExistPurchaseMoney 12 FactoryNo 13 ContractAffirmDate 14
    // ExistDate
    stringSql = "SELECT  ComNo,                  '',                                     FactoryNo,  PayCondition1,          PayCondition2, "
        + " PurchaseMoney,  Unipurchase,                Descript ,    BarCode,                    CostID, "
        + " CostID1,                 ExistPurchaseMoney,  FactoryNo,  ContractAffirmDate,  ExistDate, " + " DocNo4 " + " FROM  Doc3M010 " + " WHERE  DocNo1  =  '"
        + stringPurchaseNo1 + "' " + " AND  DocNo2  =  '" + stringPurchaseNo2 + "' " + " AND  DocNo3  =  '" + stringPurchaseNo3 + "' " + " AND  ComNo  =  '" + stringComNo + "' ";
    if (!"".equals(stringPurchaseNo4)) stringSql += " AND  DocNo4  =  '" + stringPurchaseNo4 + "' ";
    retDoc3M010 = dbDoc.queryFromPool(stringSql);
    return retDoc3M010;
  }

  // 表格 Doc3M011
  public String[][] getDoc3M011(String stringTable, String stringComNo, String stringDocNo1, String stringDocNo2, String stringDocNo3, String stringKindNoFront,
      String stringSqlAnd) throws Throwable {
    return getDoc3M011_2(stringTable, stringComNo, stringDocNo1 + stringDocNo2 + stringDocNo3, "", stringKindNoFront, "");
  }

  public String[][] getDoc3M011_2(String stringTable, String stringComNo, String stringDocNo, String stringBarCode, String stringKindNoFront, String stringSqlAnd)
      throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M011 = null;
    // 0 ComNo 1 DocNo 2 CDate 3 NeedDate 4 ApplyType
    // 5 Analysis 6 DepartNo 7 EDateTime 8 CDate 9 PrintCount
    // 10 CheckAdd 11 CheckAddDescript 12 BarCode 13 ID 14 PayConditionCross
    // 15 UNDERGO_WRITE 16 ExistDate
    stringSql = " SELECT  ComNo,                    DocNo,                       CDate,           NeedDate,   ApplyType, "
        + " Analysis,                  DepartNo,                  EDateTime,  CDate,          PrintCount, "
        + " CheckAdd,              CheckAddDescript,   BarCode,      ID,                PayConditionCross, " + " UNDERGO_WRITE, ExistDate " + " FROM  " + stringTable + " "
        + " WHERE  1  =  1 ";
    if (!"".equals(stringComNo)) stringSql += " AND  ComNo  =  '" + stringComNo + "' ";
    if (!"".equals(stringDocNo)) stringSql += " AND  DocNo  =  '" + stringDocNo + "' ";
    if (!"".equals(stringBarCode)) stringSql += " AND  BarCode  =  '" + stringBarCode + "' ";
    if (!"".equals(stringKindNoFront)) stringSql += " AND  KindNo  =  '" + stringKindNoFront + "' ";
    if ("".equals(stringSqlAnd)) {
      stringSql += " ORDER BY  DocNo1,  DocNo2,  DocNo3 ";
    }
    retDoc3M011 = dbDoc.queryFromPool(stringSql + stringSqlAnd);
    return retDoc3M011;
  }

  // 表格 Doc3M011_USE
  public double getUSEMoney(String stringComNo, String stringDocNo, String stringFactoryNo, FargloryUtil exeUtil) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    return getUSEMoney(stringComNo, stringDocNo, stringFactoryNo, exeUtil, dbDoc);
  }

  public double getUSEMoney(String stringComNo, String stringDocNo, String stringFactoryNo, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc3M011 = null;
    double doubleMoneySum = 0;
    //
    stringSql = " SELECT  SUM(USEMoney) " + " FROM  Doc3M011_USE " + " WHERE  DocNo  =  '" + stringDocNo + "' " + " AND  ComNo  =  '" + stringComNo + "' " + " AND  FactoryNo  =  '"
        + stringFactoryNo + "' ";
    retDoc3M011 = dbDoc.queryFromPool(stringSql);
    doubleMoneySum = exeUtil.doParseDouble(retDoc3M011[0][0]);
    stringSql = " SELECT  SUM(RequestPrice) " + " FROM  Doc5M02722 " + " WHERE  PurchaseNo  =  '" + stringDocNo + "' " + " AND  ComNo  =  '" + stringComNo + "' "
        + " AND  FactoryNo  =  '" + stringFactoryNo + "' ";
    retDoc3M011 = dbDoc.queryFromPool(stringSql);
    doubleMoneySum += exeUtil.doParseDouble(retDoc3M011[0][0]);
    doubleMoneySum = exeUtil.doParseDouble(convert.FourToFive("" + doubleMoneySum, 0));
    return doubleMoneySum;
  }

  // 表格 Doc3M012
  public String[][] getDoc3M012(String stringTable, String stringBarCode, String stringFactoryNo, String stringSqlAnd) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M012 = null;
    /*
     * 0 CostID 1 CostID1 2 ClassName 3 Descript 4 Unit / 5 BudgetNum 6 ActualNum 7
     * HistoryPrice 8 FactoryNo 9 ActualPrice / 10 PurchaseMoney 11 ApplyMoney 12
     * PurchaseMoney 13 ProjectID1 14 ClassNameDescript
     */
    stringSql = " SELECT  CostID,                   CostID1,         ClassName,          Descript,      Unit, "
        + " BudgetNum,          ActualNum,    HistoryPrice,        FactoryNo,   ActualPrice, " + " PurchaseMoney,  ApplyMoney,  PurchaseMoney,  ProjectID1,  ClassNameDescript, "
        + " RecordNo " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' " + stringSqlAnd;
    if (!"".equals(stringFactoryNo)) stringSql += " AND  FactoryNo  =  '" + stringFactoryNo + "' ";
    stringSql += " ORDER BY  RecordNo ";
    retDoc3M012 = dbDoc.queryFromPool(stringSql);
    return retDoc3M012;
  }

  public String[][] getDoc3M012(boolean booleanSource, String stringComNo, String stringFactoryNo, String stringSqlAnd) throws Throwable {
    return getDoc3M012(booleanSource, stringComNo, stringFactoryNo, "", stringSqlAnd, new FargloryUtil());
  }

  //
  public String[][] getDoc3M012(boolean booleanSource, String stringComNo, String stringFactoryNo, String stringCDate, String stringSqlAnd, FargloryUtil exeUtil) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringTable11 = booleanSource ? "Doc3M011" : "Doc5M011";
    String stringTable12 = booleanSource ? "Doc3M012" : "Doc5M012";
    String stringTable130 = booleanSource ? "" : "Doc5M0130";
    String[][] retDoc3M012 = null;
    stringCDate = exeUtil.getDateConvert(stringCDate);
    /*
     * 0 CostID 1 CostID1 2 ClassName 3 Descript 4 Unit / 5 BudgetNum 6 ActualNum 7
     * HistoryPrice 8 FactoryNo 9 ActualPrice / 10 PurchaseMoney 11 ApplyMoney 12
     * PurchaseMoney 13 ProjectID1 14 ClassNameDescript / 15 RecordNo 16 DocNo, 17
     * NoUseRealMoney
     */
    stringSql = " SELECT  M12.CostID,                 M12.CostID1,        M12.ClassName,          M12.Descript,      M12.Unit, "
        + " M12.BudgetNum,         M12.ActualNum,    M12.HistoryPrice,         M12.FactoryNo,   M12.ActualPrice, "
        + " M12.PurchaseMoney,  M12.ApplyMoney,  M12.PurchaseMoney,  M12.ProjectID1,  M12.ClassNameDescript, "
        + " M12.RecordNo,            M11.DocNo,           M130.NoUseRealMoney " + " FROM  " + stringTable11 + " M11,  " + stringTable12 + " M12,  " + stringTable130 + " M130 "
        + " WHERE  M11.BarCode  =  M12.BarCode " + " AND  M11.BarCode  =  M130.BarCode " + " AND  M12.RecordNo  =  M130.RecordNo " + " AND  M11.ComNo  =  '" + stringComNo + "' "
        + " AND  (  " + stringSqlAnd + ") ";
    if (!"".equals(stringFactoryNo)) stringSql += " AND  M12.FactoryNo  =  '" + stringFactoryNo + "' ";
    if (!"".equals(stringCDate)) stringSql += " AND  ISNULL(M130.ExistDate,'')  <=  '" + stringCDate + "' ";
    stringSql += " ORDER BY  M12.RecordNo ";
    System.out.println("retDoc3M012-------------------------------------------");
    retDoc3M012 = dbDoc.queryFromPool(stringSql);
    return retDoc3M012;
  }

  //
  public double[] getDoc2M048(String stringCDate, String stringFactoryNo, String stringEDateTime, String[][] retDoc2M012, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    stringCDate = exeUtil.getDateConvert(stringCDate);
    //
    String stringCostID = "";
    String stringCostID1 = "";
    String stringKey = "";
    String stringRealTotalMoney = "";
    Vector vectorKey = new Vector();
    double doubleMoney = 0;
    double[] arrayMoney = { 0, 0, 0, 0 }; // 0 合約 1 前期(已付) 2 本次 3 判斷
    Hashtable hashtableThisRealTotalMoney = new Hashtable();
    // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
    // 5 CostID1 6 RealMoney 7 RealTotalMoney
    for (int intNo = 0; intNo < retDoc2M012.length; intNo++) {
      stringCostID = retDoc2M012[intNo][4].trim();
      stringCostID1 = retDoc2M012[intNo][5].trim();
      stringRealTotalMoney = retDoc2M012[intNo][7].trim();
      stringKey = stringCostID + "---" + stringCostID1;
      //
      if (vectorKey.indexOf(stringKey) == -1) vectorKey.add(stringKey);
      //
      doubleMoney = exeUtil.doParseDouble(stringRealTotalMoney) + exeUtil.doParseDouble("" + hashtableThisRealTotalMoney.get(stringKey));
      hashtableThisRealTotalMoney.put(stringKey, "" + doubleMoney);

    }
    String[] arrayTemp = null;
    String[][] retDoc2m048 = null;
    double doubleMoneyThis = 0;
    double doubleMoneyThisSum = 0;
    for (int intNo = 0; intNo < vectorKey.size(); intNo++) {
      stringKey = "" + vectorKey.get(intNo);
      arrayTemp = convert.StringToken(stringKey, "---");
      if (arrayTemp.length != 2) continue;
      stringCostID = arrayTemp[0].trim();
      stringCostID1 = arrayTemp[1].trim();
      doubleMoneyThis = exeUtil.doParseDouble("" + hashtableThisRealTotalMoney.get(stringKey));
      doubleMoneyThisSum += doubleMoneyThis;
      // 存在
      // 0 CostID 1 CostID1 2 LimitDateS 3 LimitDateE 4 FactoryNo 5 RealTotalMoney
      retDoc2m048 = getDoc2M048(stringCostID, stringCostID1, stringCDate, stringFactoryNo, dbDoc);
      if (retDoc2m048.length == 0) {
        // 合約金額
        arrayMoney[0] += doubleMoneyThis;
        continue;
      }
      // 合約金額
      arrayMoney[0] += exeUtil.doParseDouble(retDoc2m048[0][5].trim());
      arrayMoney[3] += exeUtil.doParseDouble(retDoc2m048[0][5].trim());
      // 前期
      arrayMoney[1] += getUseMoneyAboutDoc2M048(stringCostID, stringCostID1, retDoc2m048[0][2].trim(), retDoc2m048[0][3].trim(), stringFactoryNo, stringEDateTime, "<", exeUtil,
          dbDoc);
    }
    arrayMoney[2] = doubleMoneyThisSum;
    return arrayMoney;
  }

  // 表格 Doc2M048
  public String[][] getDoc2M048(String stringCostID, String stringCostID1, String stringDateAC, String stringFactoryNo, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc2M048 = null;
    // 0 CostID 1 CostID1 2 LimitDateS 3 LimitDateE 4 FactoryNo 5 RealTotalMoney
    stringSql = " SELECT  CostID,  CostID1,  LimitDateS,  LimitDateE,  FactoryNo,  RealTotalMoney " + " FROM  Doc2M048  " + " WHERE  CostID  =  '" + stringCostID + "' "
        + " AND  CostID1  =  '" + stringCostID1 + "' " + " AND  FactoryNo  =  '" + stringFactoryNo + "' " + " AND  LimitDateS  <=  '" + stringDateAC + "'  AND  LimitDateE  >=  '"
        + stringDateAC + "' ";
    retDoc2M048 = dbDoc.queryFromPool(stringSql);
    return retDoc2M048;
  }

  public double getUseMoneyAboutDoc2M048(String stringCostID, String stringCostID1, String stringLimitDateS, String stringLimitDateE, String stringFactoryNo,
      String stringEDateTime, String stringSign, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc = null;
    double doubleUserMoneySum = 0;
    //
    stringLimitDateS = exeUtil.getDateConvertRoc(stringLimitDateS);
    stringLimitDateE = exeUtil.getDateConvertRoc(stringLimitDateE);
    // 請款
    stringSql = " SELECT  SUM(RealTotalMoney) " + " FROM  Doc2M012 " + " WHERE  CostID  =  '" + stringCostID + "' " + " AND  CostID1  =  '" + stringCostID1 + "' "
        + " AND  BarCode  IN  (SELECT  BarCode " + " FROM  Doc2M010 " + " WHERE  CDate  BETWEEN  '" + stringLimitDateS + "'  AND  '" + stringLimitDateE + "' " + " AND  EDateTime  "
        + stringSign + " '" + stringEDateTime + "' " + " AND  PurchaseNoExist  =  'N')" + " AND  (BarCode  IN  (SELECT  BarCode  FROM  Doc2M011  WHERE  FactoryNo  =  '"
        + stringFactoryNo + "' )  OR  " + " BarCode  IN  (SELECT  BarCode  FROM  Doc2M013  WHERE  FactoryNo  =  '" + stringFactoryNo + "' )) ";
    retDoc = dbDoc.queryFromPool(stringSql);
    doubleUserMoneySum = exeUtil.doParseDouble(retDoc[0][0]);
    // 借款
    stringSql = " SELECT  SUM(RealTotalMoney) " + " FROM  Doc6M012 " + " WHERE  CostID  =  '" + stringCostID + "' " + " AND  CostID1  =  '" + stringCostID1 + "' "
        + " AND  BarCode  IN  (SELECT  BarCode " + " FROM  Doc6M010 " + " WHERE  CDate  BETWEEN  '" + stringLimitDateS + "'  AND  '" + stringLimitDateE + "' " + " AND  EDateTime  "
        + stringSign + " '" + stringEDateTime + "' " + " AND  PurchaseNoExist  =  'N' " + " AND  KindNo  = '24' )"
        + " AND  (BarCode  IN  (SELECT  BarCode  FROM  Doc6M011  WHERE  FactoryNo  =  '" + stringFactoryNo + "' )  OR  "
        + " BarCode  IN  (SELECT  BarCode  FROM  Doc6M013  WHERE  FactoryNo  =  '" + stringFactoryNo + "' ) OR " + " BarCode  IN  (SELECT  BarCode  FROM  Doc6M0101 "
        + " WHERE  BorrowNo  IN  ( SELECT    DocNo " + " FROM    Doc6M010 " + " WHERE   KindNo  = '26' "
        + " AND  (BarCode  IN  (SELECT  BarCode  FROM  Doc6M011  WHERE  FactoryNo  =  '" + stringFactoryNo + "' )  OR  "
        + " BarCode  IN  (SELECT  BarCode  FROM  Doc6M013  WHERE  FactoryNo  =  '" + stringFactoryNo + "' )  OR  " + " FactoryNo  =  '" + stringFactoryNo + "' " + ") " + ")" + ")"
        + ") ";
    retDoc = dbDoc.queryFromPool(stringSql);
    doubleUserMoneySum += exeUtil.doParseDouble(retDoc[0][0]);
    return doubleUserMoneySum;
  }

  // 表格 Doc5M0221
  public String[][] getDoc5M0221(String stringBarCode, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0221 = null;
    // 0 BorrowAmt 1 DateStart 2 DateEnd 3 AccrualRate 4 Formula 5 Accrual
    stringSql = "SELECT  BorrowAmt,  DateStart,  DateEnd,  AccrualRate,  Formula,  Accrual " + " FROM  Doc5M0221 " + " WHERE  BarCode  =  '" + stringBarCode + "' "
        + " ORDER BY  RecordNo ";
    retDoc5M0221 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0221;
  }

  // 表格 Doc5M0222
  public String[][] getDoc5M0222(String stringBarCode, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0222 = null;
    // 0 InvestmentTrust 1 FundNo 2 BandNo 3 AccountNo 4 AccountName
    // 5 Amt 6 Unit 7 NetAmt
    stringSql = "SELECT  InvestmentTrust,  FundNo,  BandNo,  AccountNo,  AccountName, " + " Amt,                        Unit,         NetAmt " + " FROM  Doc5M0222 "
        + " WHERE  BarCode  =  '" + stringBarCode + "' " + " ORDER BY  RecordNo ";
    retDoc5M0222 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0222;
  }

  // 表格 Doc5M0224
  public double getAmtDoc5M0224(String stringVoucherYMD, String stringVoucherFlowNo, String stringVoucherSeqNo, String stringBarCode, String stringEDateTime, String stringComNo,
      FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0224 = null;
    //
    stringSql = "SELECT  SUM(Amt) " + " FROM  Doc5M0224 " + " WHERE  VOUCHER_YMD  =  " + stringVoucherYMD + " " + " AND  VOUCHER_FLOW_NO  =  " + stringVoucherFlowNo + " "
        + " AND  VOUCHER_SEQ_NO  =  " + stringVoucherSeqNo + " " + " AND  BarCode  <>  '" + stringBarCode + "' " + " AND  BarCode IN (SELECT  BarCode " + " FROM  Doc5M020 "
        + " WHERE  ComNo  =  '" + stringComNo + "' " + " AND  EDateTime  <  '" + stringEDateTime + "' " + " ) ";
    retDoc5M0224 = dbDoc.queryFromPool(stringSql);
    return exeUtil.doParseDouble(retDoc5M0224[0][0].trim());
  }

  public String[][] getDoc5M0224(String stringBarCode, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0224 = null;
    // 0 VOUCHER_YMD 1 VOUCHER_FLOW_NO 2 VOUCHER_SEQ_NO 3 Amt 4 FactoryNo 5 CostID 6
    // CostID1
    stringSql = "SELECT  VOUCHER_YMD,  VOUCHER_FLOW_NO, VOUCHER_SEQ_NO,  Amt,  FactoryNo,  CostID,  CostID1 " + " FROM  Doc5M0224 " + " WHERE  BarCode  =  '" + stringBarCode + "' "
        + " ORDER BY  RecordNo ";
    retDoc5M0224 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0224;
  }

  // 表格 Doc5M0225
  public String[][] getDoc5M0225(String stringBarCode, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0225 = null;
    // 0 ComNo 1 BarCodeF 2 RecordNo 3 CostID 4 RealTotalMoney
    stringSql = "SELECT  ComNo,  BarCodeF,  RecordNo,  CostID,  RealTotalMoney " + " FROM  Doc5M0225 " + " WHERE  BarCode  =  '" + stringBarCode + "' " + " ORDER BY  RecordNo ";
    retDoc5M0225 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0225;
  }

  public String getCost4Name(String stringComNo, String stringCostID, String stringCostID1, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringDescription = "";
    String[][] retDoc7M053 = null;
    //
    if ("".equals(stringCostID)) return stringDescription;
    //
    if (!"".equals(stringCostID1)) {
      stringSql = " SELECT  DESCRIPTION " + " FROM  Doc2M020 " + " WHERE  ComNo  =  '" + stringComNo + "' " + " AND  CostID  =  '" + stringCostID + "' " + " AND  CostID1  =  '"
          + stringCostID1 + "' ";
      retDoc7M053 = dbDoc.queryFromPool(stringSql);
    } else {
      stringSql = "SELECT  DESCRIPTION " + " FROM  Doc7M053 " + " WHERE  CostID  =  '" + stringCostID + "' ";
      retDoc7M053 = dbDoc.queryFromPool(stringSql);
    }
    if (retDoc7M053.length > 0) stringDescription = retDoc7M053[0][0].trim();
    return stringDescription;
  }

  // 表格 Doc5M0202
  public String[][] getDoc5M0202(String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc5M0202 = null;
    // 0 RecordNo 1 BorrowNo 2 BorrowNo1 3 ProjectID1 4 BorrowNo2 5 BorrowNo3
    // 6 BorrowMoney
    stringSql = "SELECT  RecordNo,                     BorrowNo,                       BorrowNo1,            ProjectID1,  BorrowNo2,          BorrowNo3, " + " BorrowMoney  "
        + " FROM  Doc5M0202 " + " WHERE  BarCode  =  '" + stringBarCode + "' " + " ORDER BY  RecordNo ";
    retDoc5M0202 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0202;
  }

  // 表格 Doc6M010
  public String getExistFactoryNoRealMoneyBorrowForDoc6M010(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo,
      String stringKindNo, boolean booleanSource, FargloryUtil exeUtil) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    return "" + getPurchaseMoneyUSEDoc6M012(stringBarCode, stringComNo, stringEDateTime, stringPurchaseNo, stringFactoryNo, stringKindNo, booleanSource, exeUtil, dbDoc);
  }

  public double getPurchaseMoneyUSEDoc6M012(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo, String stringKindNo,
      boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSumRealMoney = "0";
    String stringSql = "";
    String[][] retDoc6M012 = null;
    //
    if (!booleanSource) return 0;
    //
    stringSql = "SELECT  SUM(M12.RealTotalMoney) " + "FROM  Doc6M010 M10,  Doc6M012 M12 " + " WHERE  M10.BarCode  =  M12.BarCode " + " AND  M10.PurchaseNoExist  IN  ('Y',  'YY') "
        + " AND  M10.UNDERGO_WRITE  <>  'E' " + " AND  M10.KindNo  =  '" + stringKindNo + "' " + " AND  M10.ComNo  =  '" + stringComNo + "' " + " AND  M10.PurchaseNo  =  '"
        + stringPurchaseNo + "' " + " AND  M10.FactoryNo  =  '" + stringFactoryNo + "' " + " AND  M10.BarCode  <>  '" + stringBarCode + "' " + " AND  M10.EDateTime  <  '"
        + stringEDateTime + "' ";
    retDoc6M012 = dbDoc.queryFromPool(stringSql);
    if (retDoc6M012.length != 0) {
      stringSumRealMoney = retDoc6M012[0][0].trim();
      stringSumRealMoney = "" + exeUtil.doParseDouble(stringSumRealMoney);
      stringSumRealMoney = convert.FourToFive(stringSumRealMoney, 0);
    }
    return exeUtil.doParseDouble(stringSumRealMoney);
  }

  public double getPurchaseMoneyUSEDoc6M0171(String stringBarCode, String stringComNo, String stringEDateTime, String stringPurchaseNo, String stringFactoryNo, String stringKindNo,
      String stringBarCodePur, String stringGroupID, boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSumRealMoney = "0";
    String stringSql = "";
    String[][] retDoc6M0171 = null;
    //
    if (!booleanSource) return 0;
    //
    stringSql = "SELECT  SUM(M171.PurchaseMoney) " + "FROM  Doc6M010 M10,  Doc6M0171 M171 " + " WHERE  M10.BarCode  =  M171.BarCode "
        + " AND  M10.PurchaseNoExist  IN  ('Y',  'YY') " + " AND  M10.UNDERGO_WRITE  <>  'E' " + " AND  M10.KindNo  =  '" + stringKindNo + "' "
        + " AND  M171.RecordNo12  IN (SELECT  RecordNo " + " FROM  Doc3M012 " + " WHERE  BarCode  =  '" + stringBarCodePur + "' " + " AND  GroupID  =  '" + stringGroupID + "' ) "
        + " AND  M10.ComNo  =  '" + stringComNo + "' " + " AND  M171.PurchaseNo  =  '" + stringPurchaseNo + "' " + " AND  M171.FactoryNo  =  '" + stringFactoryNo + "' "
        + " AND  M10.BarCode  <>  '" + stringBarCode + "' " + " AND  M10.EDateTime  <  '" + stringEDateTime + "' ";
    retDoc6M0171 = dbDoc.queryFromPool(stringSql);
    if (retDoc6M0171.length != 0) {
      stringSumRealMoney = retDoc6M0171[0][0].trim();
      stringSumRealMoney = "" + exeUtil.doParseDouble(stringSumRealMoney);
      stringSumRealMoney = convert.FourToFive(stringSumRealMoney, 0);
    }
    return exeUtil.doParseDouble(stringSumRealMoney);
  }

  public String[][] getBarCodeForDoc6M010(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringBarCide11 = "";
    String stringKindNo = "26";
    String[][] retDoc6M010 = null;
    // 0 BarCode 1 DocNo1 2 DocNo2 3 DocNo3
    stringSql = "SELECT  DISTINCT  M10.BarCode,  M10.DocNo1,  M10.DocNo2,  M10.DocNo3 " + " FROM  " + stringTable + " M10 " + " WHERE  M10.KindNo  =  '" + stringKindNo + "' "
        + " AND  M10.UNDERGO_WRITE  <>  'E' " + " AND  M10.DocNo  IN  (SELECT  BorrowNo " + " FROM  Doc6M0101 " + " WHERE  BarCode  =  '" + stringBarCode + "') "
        + " AND  M10.ComNo  IN  (SELECT  ComNo " + " FROM  Doc6M010 " + " WHERE  BarCode  =  '" + stringBarCode + "') ";
    retDoc6M010 = dbDoc.queryFromPool(stringSql);
    return retDoc6M010;
  }

  public String[][] getDoc6M010(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc6M010 = null;
    // 0 ComNo 1 Descript 2 NeedDate 3 BarCode 4 DocNo
    // 5 UNDERGO_WRITE 6 PurchaseNo1 7 PurchaseNo2 8 PurchaseNo3 9 DocNo1
    // 10 DocNo2 11 DocNo3 12 DepartNo 13 RetainMoney 14 PayCondition1
    // 15 PayCondition2 16 DestineExpenseDate 17 OriEmployeeNo 18 PrintCount 19
    // PurchaseNoExist
    // 20 OptometryNo1 21 OptometryNo2 22 OptometryNo3 23 BorrowNo1 24 BorrowNo2
    // 25 BorrowNo3 26 CDate 27 RetainMoney 28 EDateTime 29 CTime
    // 30 PayType 31 DestineExpenseDate 32 BorrowMoney 33 FactoryNo 34
    // BorrowMinusMoney
    // 35 AccountCount 36 PurchaseNo4 37 DocNoType 38 ID
    stringSql = "SELECT  ComNo,                    Descript,                       NeedDate,            BarCode,           DocNo, "
        + " UNDERGO_WRITE,  PurchaseNo1,              PurchaseNo2,      PurchaseNo3,   DocNo1, "
        + " DocNo2,                   DocNo3,                         DepartNo,            RetainMoney,   PayCondition1, "
        + " PayCondition2,       DestineExpenseDate,  OriEmployeeNo,  PrintCount,       PurchaseNoExist, "
        + " OptometryNo1,       OptometryNo2,              OptometryNo3,   '',                          '', "
        + " '',                              CDate,                             RetainMoney,     EDateTime,         CTime, "
        + " PayType,                  DestineExpenseDate,  BorrowMoney,    FactoryNo,         BorrowMinusMoney,  "
        + " AccountCount,        PurchaseNo4,               DocNoType,         ID " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc6M010 = dbDoc.queryFromPool(stringSql);
    return retDoc6M010;
  }

  // KindNo 24(請款) 26(借款)
  public String getBarCodeForDoc6M010(String stringTable, String stringComNo, String stringKindNo, String stringDocNo1, String stringDocNo2, String stringDocNo3) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringBarCode = "";
    String stringSql = "";
    String[][] retDoc6M010 = null;
    //
    stringSql = "SELECT  BarCode " + " FROM  Doc6M010 " + " WHERE  KindNo  =  '" + stringKindNo + "' " + " AND  ComNo  =  '" + stringComNo + "' " + " AND  DocNo1  =  '"
        + stringDocNo1 + "' " + " AND  DocNo2  =  '" + stringDocNo2 + "' " + " AND  DocNo3  =  '" + stringDocNo3 + "' ";
    retDoc6M010 = dbDoc.queryFromPool(stringSql);
    if (retDoc6M010.length > 0) {
      stringBarCode = retDoc6M010[0][0].trim();
    }
    return stringBarCode;
  }

  // 表格 Doc6M0101
  public String[][] getDoc6M0101(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc6M0101 = null;
    // 0 RecordNo 1 BorrowNo 2 BorrowNo1 3 BorrowNo2 4 BorrowNo3
    // 5 BorrowMoney
    stringSql = "SELECT  RecordNo,                     BorrowNo,                       BorrowNo1,            BorrowNo2,          BorrowNo3, " + " BorrowMoney  " + " FROM  "
        + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc6M0101 = dbDoc.queryFromPool(stringSql);
    return retDoc6M0101;
  }

  // 表格 Doc6M011
  public String[][] getDoc6M011(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc6M011 = null;
    // 0 FactoryNo 1 InvoiceKind 2 InvoiceDate 3 InvoiceNo 4 InvoiceMoney
    // 5 InvoiceTax 6 InvoiceTotalMoney 7 DeductKind 8 RecordNo
    stringSql = "SELECT  FactoryNo,   InvoiceKind,               InvoiceDate,  InvoiceNo,  InvoiceMoney, " + " InvoiceTax,  InvoiceTotalMoney,  DeductKind,   RecordNo " + " FROM  "
        + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' " + " ORDER BY  RecordNo ";
    retDoc6M011 = dbDoc.queryFromPool(stringSql);
    return retDoc6M011;
  }

  // 表格 Doc6M012
  public String[][] getDoc6M012(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc6M012 = null;
    // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
    // 5 CostID1 6 RealMoney 7 RealTotalMoney
    stringSql = "SELECT  InOut,     DepartNo,     ProjectID,             ProjectID1,  CostID, " + " CostID1,  RealMoney,  RealTotalMoney " + " FROM  " + stringTable + " "
        + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc6M012 = dbDoc.queryFromPool(stringSql);
    return retDoc6M012;
  }

  // 表格 Doc6M013
  public String getReceiptTotalMoneyDoc6M013(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc6M013 = null;
    //
    stringSql = "SELECT  SUM(ReceiptTotalMoney) " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc6M013 = dbDoc.queryFromPool(stringSql);
    return retDoc6M013[0][0];
  }

  public String[][] getDoc6M013(String stringTable, String stringBarCode) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc6M013 = null;
    // 0 FactoryNo 1 ReceiptKind 2 ReceiptDate 3 ReceiptMoney 4 ReceiptTax
    // 5 ReceiptTotalMoney 6 ReceiptTaxType 7 RecordNo 8 ACCT_NO 9 SupplementMoney
    stringSql = "SELECT  FactoryNo,                   ReceiptKind,       ReceiptDate,  ReceiptMoney,  ReceiptTax, "
        + " ReceiptTotalMoney,  ReceiptTaxType,  RecordNo,      ACCT_NO,           SupplementMoney " + " FROM  Doc6M013 " + " WHERE  BarCode  =  '" + stringBarCode + "' "
        + " ORDER BY  RecordNo ";
    retDoc6M013 = dbDoc.queryFromPool(stringSql);
    return retDoc6M013;
  }

  public String[][] getDoc3M013(String stringTable, String stringBarCode, String stringFactoryNo) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc3M013 = null;
    // 0 FactoryNo 1 PurchaseSumMoney 2 PercentRate 3 MonthNum 4 PurchaseMoney
    // 5 PayCondition1 6 PayCondition2 7 Descript 8 NoUseRealMoney
    stringSql = " SELECT  FactoryNo,           PurchaseSumMoney,    PercentRate,  MonthNum,      PurchaseMoney, "
        + " PayCondition1,    PayCondition2,               Descript,         NoUseRealMoney " + " FROM  " + stringTable + " " + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    if (!"".equals(stringFactoryNo)) stringSql += " AND  FactoryNo  =  '" + stringFactoryNo + "' ";
    stringSql += " ORDER BY FactoryNo, RecordNo ";
    retDoc3M013 = dbDoc.queryFromPool(stringSql);
    return retDoc3M013;
  }

  public String getDeptName(String stringDeptCd, String stringDeptCdDoc, String stringSqlAnd) throws Throwable {
    String[][] retDoc2M010 = getDoc2M010DeptCd(stringDeptCd, stringDeptCdDoc, stringSqlAnd);
    //
    if (retDoc2M010.length > 0) return retDoc2M010[0][2].trim();
    //
    return "";
  }

  public String[][] getDoc2M010DeptCd(String stringDeptCd, String stringDeptCdDoc, String stringSqlAnd) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String[][] retDoc2M010 = null;
    // 0 DEPT_CD 1 DEPT_CD_Doc 2 DEPT_Name
    stringSql = " SELECT  DEPT_CD,  DEPT_CD_Doc,  DEPT_Name " + " FROM  Doc2M010_DeptCd " + " WHERE  1=1 ";
    if (!"".equals(stringDeptCd)) stringSql += " AND  DEPT_CD  =  '" + stringDeptCd + "' ";
    if (!"".equals(stringDeptCdDoc)) stringSql += " AND  DEPT_CD_Doc  =  '" + stringDeptCdDoc + "' ";
    if ("".equals(stringSqlAnd)) {
      stringSql += " ORDER BY  DEPT_CD ";
    } else {
      stringSql += stringSqlAnd;
    }
    retDoc2M010 = dbDoc.queryFromPool(stringSql);
    //
    return retDoc2M010;
  }

  // 表格 FE3D01
  // 部門名稱
  public String getDepartName(String stringDepartNo) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringDepartName = "";
    String[][] retFE3D01 = null;
    //
    stringSql = " SELECT  DEPT_NAME " + " FROM  FE3D01 " + " WHERE  DEPT_CD  =  '" + stringDepartNo + "' ";
    retFE3D01 = dbDoc.queryFromPool(stringSql);
    if (retFE3D01.length != 0) {
      stringDepartName = retFE3D01[0][0].trim();
    }
    return stringDepartName;
  }

  // 資料庫 FE3D
  // 表格 FE3D05
  // 人員名稱
  public String getEmpName(String stringEmpNo) throws Throwable {
    talk dbDoc = getTalk("" + get("put_Doc"));
    talk dbFE3D = getTalk("" + get("put_FE3D"));
    String stringSql = "";
    String stringEmpName = "";
    String[][] retFE3D05 = null;
    //
    if (stringEmpNo.startsWith("OO")) {
      FargloryUtil exeUtil = new FargloryUtil();
      stringEmpName = exeUtil.getNameUnion("EMP_NAME", "Doc5M011_EmployeeNo", " AND  EMP_NO  =  '" + stringEmpNo + "' ", new Hashtable(), dbDoc);
    }
    if (!"".equals(stringEmpName)) return stringEmpName;
    //
    stringSql = " SELECT  EMP_NAME " + " FROM  FE3D05 " + " WHERE  EMP_NO  =  '" + stringEmpNo + "' ";
    retFE3D05 = dbFE3D.queryFromPool(stringSql);
    if (retFE3D05.length != 0) {
      stringEmpName = retFE3D05[0][0].trim();
    }
    return stringEmpName;
  }

  public String[][] getFE3D70(String stringComNo) throws Throwable {
    talk dbFE3D = getTalk("" + get("put_FE3D"));
    String stringSql = "";
    String[][] retFE3D70 = null;
    //
    stringSql = " SELECT  INSUR_COMP_NAME,  TEL,  FIRM_NO " + " FROM  FE3D70 " + " WHERE  COMPANY_CD  =  '" + stringComNo + "' ";
    retFE3D70 = dbFE3D.queryFromPool(stringSql);
    return retFE3D70;
  }

  // 資料庫 FED1
  // 表格 FED1005
  // 廠商名稱
  public String[][] getFED1005(String stringFactoryNo) throws Throwable {
    return getFED1005(stringFactoryNo, "");
  }

  public String[][] getFED1005(String stringFactoryNo, String stringComNo) throws Throwable {
    talk dbFED1 = getTalk("" + get("put_FED1"));
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringFactoryName = "";
    String[][] retFED1005 = null;
    //
    if (",OO,CS,".indexOf("," + stringComNo + ",") != -1) {
      stringSql = " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO2,  COMPANY_TEL_NO1,  COMPANY_TEL_NO3 " + " FROM  Doc3M015 " + " WHERE  OBJECT_CD  =  '" + stringFactoryNo + "' ";
      // " AND (OBJECT_KIND = '3' OR OBJECT_KIND = '2') " ;
      retFED1005 = dbDoc.queryFromPool(stringSql);
      if (retFED1005.length > 0) {
        if (!"".equals(retFED1005[0][2].trim())) {
          retFED1005[0][1] = retFED1005[0][2].trim() + "-" + retFED1005[0][1].trim();
        }
        if (!"".equals(retFED1005[0][3].trim())) {
          retFED1005[0][1] = retFED1005[0][1].trim() + "#" + retFED1005[0][3].trim();
        }
      } else {
        stringSql = " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO " + " FROM  FED1005 " + " WHERE  OBJECT_CD  =  '" + stringFactoryNo + "' ";
        // " AND (OBJECT_KIND = '3' OR OBJECT_KIND = '2') " ;
        retFED1005 = dbFED1.queryFromPool(stringSql);
      }
    } else {
      stringSql = " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO " + " FROM  FED1005 " + " WHERE  OBJECT_CD  =  '" + stringFactoryNo + "' ";
      // " AND (OBJECT_KIND = '3' OR OBJECT_KIND = '2') " ;
      retFED1005 = dbFED1.queryFromPool(stringSql);
      if (retFED1005.length == 0) {
        stringSql = " SELECT  OBJECT_SHORT_NAME,  COMPANY_TEL_NO2,  COMPANY_TEL_NO1,  COMPANY_TEL_NO3 " + " FROM  Doc3M015 " + " WHERE  OBJECT_CD  =  '" + stringFactoryNo + "' ";
        // " AND (OBJECT_KIND = '3' OR OBJECT_KIND = '2') " ;
        retFED1005 = dbDoc.queryFromPool(stringSql);
        if (retFED1005.length > 0) {
          if (!"".equals(retFED1005[0][2].trim())) {
            retFED1005[0][1] = retFED1005[0][2].trim() + "-" + retFED1005[0][1].trim();
          }
          if (!"".equals(retFED1005[0][3].trim())) {
            retFED1005[0][1] = retFED1005[0][1].trim() + "#" + retFED1005[0][3].trim();
          }
        }
      }
    }
    return retFED1005;
  }

  public String getObjectNameFED1005(String stringFactoryNo) throws Throwable {
    talk dbFED1 = getTalk("" + get("put_FED1"));
    String stringSql = "";
    String stringFactoryName = "";
    String[][] retFED1005 = null;
    //
    stringSql = " SELECT  OBJECT_SHORT_NAME " + " FROM  FED1005 " + " WHERE  OBJECT_CD  =  '" + stringFactoryNo + "' ";
    // " AND (OBJECT_KIND = '3' OR OBJECT_KIND = '2') " ;
    retFED1005 = dbFED1.queryFromPool(stringSql);
    if (retFED1005.length > 0) return retFED1005[0][0].trim();
    return "";
  }

  // 表格 FED1006
  public String getDepartNameFED1006(String stringDepartNo) throws Throwable {
    talk dbFED1 = getTalk("" + get("put_FED1"));
    String stringSql = "";
    String stringDepartName = "";
    String[][] FED1006 = null;
    //
    stringSql = " SELECT  DEPT_CHINESE_NAME " + " FROM  FED1006 " + " WHERE  DEPT_CD  =  '" + stringDepartNo + "' ";
    FED1006 = dbFED1.queryFromPool(stringSql);
    if (FED1006.length != 0) {
      stringDepartName = FED1006[0][0].trim();
    }
    return stringDepartName;
  }

  public String[][] getFED1012(String stringVoucherYMD, String stringVoucherFlowNo, String stringVoucherSeqNo, String stringComNo, String stringKind) throws Throwable {
    talk dbFED1 = getTalk("" + get("put_FED1"));
    String stringSql = "";
    String[][] retFED1012 = null;
    // 0 DEPT_CD 1 AMT 2 DESCRIPTION_2
    stringSql = "SELECT  DEPT_CD,  AMT,  DESCRIPTION_2 " + " FROM  FED1012 " + " WHERE  VOUCHER_YMD  =  " + stringVoucherYMD + " " + " AND  VOUCHER_FLOW_NO  =  "
        + stringVoucherFlowNo + " " + " AND  VOUCHER_SEQ_NO  =  " + stringVoucherSeqNo + " " + " AND  COMPANY_CD  =  '" + stringComNo + "' " + " AND  KIND  =  '" + stringKind
        + "' ";
    retFED1012 = dbFED1.queryFromPool(stringSql);
    return retFED1012;
  }

  // 表格 FED1023
  // 公司名稱
  public String getCompanyName(String stringCompanyCd) throws Throwable {
    talk dbFED1 = getTalk("" + get("put_FED1"));
    String stringSql = "";
    String stringCompanyName = "";
    String[][] retFED1023 = null;
    //
    stringSql = " SELECT  COMPANY_NAME " + " FROM  FED1023 " + " WHERE  COMPANY_CD  =  '" + stringCompanyCd + "' ";
    retFED1023 = dbFED1.queryFromPool(stringSql);
    if (retFED1023.length != 0) {
      stringCompanyName = retFED1023[0][0].trim();
    }
    return stringCompanyName;
  }

  public String getCompanyName2(String stringCompanyCd, String stringBarCode, FargloryUtil exeUtil) throws Throwable {
    talk dbFED1 = getTalk("" + get("put_FED1"));
    talk dbDoc = getTalk("" + get("put_Doc"));
    String stringSql = "";
    String stringCompanyName = "";
    String[][] retFED1023 = null;
    //
    if ("OO".equals(stringCompanyCd)) {
      String stringDepartNo2 = exeUtil.getNameUnion("DepartNo2", "Doc5M020", " AND  BarCode  =  '" + stringBarCode + "'  ", new Hashtable(), dbDoc);
      stringCompanyName = exeUtil.getNameUnion("Descript", "Doc2M010_ProjectID1", " AND  ProjectID1  =  '" + stringDepartNo2 + "'  ", new Hashtable(), dbDoc);
      if (!"".equals(stringCompanyName)) {
        return stringCompanyName;
      }
    }
    stringSql = " SELECT  COMPANY_NAME " + " FROM  FED1023 " + " WHERE  COMPANY_CD  =  '" + stringCompanyCd + "' ";
    retFED1023 = dbFED1.queryFromPool(stringSql);
    if (retFED1023.length != 0) {
      stringCompanyName = retFED1023[0][0].trim();
    }
    return stringCompanyName;
  }

  // 資料庫 Sale
  // 表格 A_Project (intNo+1)
  public String getProjectName(String stringProjectID) throws Throwable {
    talk dbSale = getTalk("" + get("put_Sale"));
    String stringSql = "";
    String stringProjectName = "";
    String[][] retAProject = null;
    //
    stringSql = " SELECT  ProjectName " + " FROM  A_Project " + " WHERE  ProjectID  =  '" + stringProjectID + "' ";
    retAProject = dbSale.queryFromPool(stringSql);
    if (retAProject.length != 0) {
      stringProjectName = retAProject[0][0].trim();
    }
    return stringProjectName;
  }

  // 表格 Doc2M0201
  public Vector getCostIDVDoc2M0201V(String stringComNo, String stringDate, String stringSqlAnd) throws Throwable {
    String stringCostID = "";
    String stringCostID1 = "";
    String[][] retDoc2M0201 = getDoc2M0201(stringComNo, "", "", "", stringDate, stringSqlAnd);
    Vector vectorCostID = new Vector();
    for (int intNo = 0; intNo < retDoc2M0201.length; intNo++) {
      stringCostID = retDoc2M0201[intNo][0].trim();
      stringCostID1 = retDoc2M0201[intNo][1].trim();
      if (vectorCostID.indexOf(stringCostID + stringCostID1) == -1) vectorCostID.add(stringCostID + stringCostID1);
    }
    return vectorCostID;
  }

  public String[][] getDoc2M0201(String stringComNo, String stringCostID, String stringCostID1, String stringFunctionType, String stringDate, String stringSqlAnd)
      throws Throwable {
    String stringSql = "";
    String[][] retDoc2M0201 = null;
    // 0 CostID 1 CostID1 2 FunctionType 3 DateStart 4 DateEnd
    // 5 ComNo 6 Remark 7 FunctionName
    stringSql = " SELECT  CostID,  CostID1,  FunctionType,  DateStart,  DateEnd, " + " ComNo,   Remark,   FunctionName " + " FROM  Doc2M0201 " + " WHERE  1=1 ";
    if (!"".equals(stringComNo)) {
      stringSql += " AND (ComNo  =  'ALL'  OR  ComNo  =  '" + stringComNo + "') ";
    }
    if (!"".equals(stringCostID)) stringSql += " AND  CostID   =  '" + stringCostID + "' ";
    if (!"".equals(stringCostID1)) stringSql += " AND  CostID1  =  '" + stringCostID1 + "' ";
    if (!"".equals(stringFunctionType)) stringSql += " AND  FunctionType  LIKE  '%" + stringFunctionType + "%' ";
    if (!"".equals(stringDate)) {
      stringSql += " AND  (DateStart  <=  '" + stringDate + "' OR  DateStart  =  '9999/99/99') " + " AND  (DateEnd    >=  '" + stringDate + "' OR  DateStart  =  '9999/99/99') ";
    }
    //
    stringSql += stringSqlAnd;
    if (stringSql.indexOf("ORDER") == -1) {
      stringSql += " ORDER BY CostID,  CostID1 ";
    }
    retDoc2M0201 = dbDoc.queryFromPool(stringSql);
    return retDoc2M0201;
  }

  public String getGroupID(String stringTable2171, String stringBarCode, String stringBarCodePur, String stringPurchaseNo, boolean booleanSource, FargloryUtil exeUtil, talk dbDoc)
      throws Throwable {
    String stringGroupID = "";
    String stringPurchaseNoL = "";
    String stringRecordNo12L = "";
    Hashtable hashtableAnd = new Hashtable();
    Hashtable hashtableTableRow = new Hashtable();
    Vector vectorTableData = null;
    //
    if (!booleanSource) return "";
    //
    hashtableAnd.put("BarCode", stringBarCode);
    vectorTableData = exeUtil.getQueryDataHashtable(stringTable2171, hashtableAnd, "", dbDoc);
    for (int intNo = 0; intNo < vectorTableData.size(); intNo++) {
      hashtableTableRow = (Hashtable) vectorTableData.get(intNo);
      if (hashtableTableRow == null) continue;
      stringPurchaseNoL = ("" + hashtableTableRow.get("PurchaseNo")).trim();
      stringRecordNo12L = ("" + hashtableTableRow.get("RecordNo12")).trim();
      //
      if (!stringPurchaseNo.equals(stringPurchaseNoL)) continue;
      //
      hashtableAnd.put("BarCode", stringBarCodePur);
      hashtableAnd.put("RecordNo", stringRecordNo12L);
      stringGroupID = exeUtil.getNameUnion("GroupID", "Doc3M012", "", hashtableAnd, dbDoc);
      return stringGroupID;
    }
    //
    return "";
  }

  // 表格 Doc2M0401
  public String[][] getDoc2M0401(String stringCostID, String stringUseType, String stringSqlAnd, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringSqlAndL = "";
    String[] arrayUseType = convert.StringToken(stringUseType, ",");
    String[][] retDoc2M0401 = null;
    // 0 CostID 1 UseType 2 Remark 3 FunctionName
    stringSql = " SELECT  CostID,  UseType,  Remark,  FunctionName " + " FROM  Doc2M0401 " + " WHERE  1=1 ";
    if (!"".equals(stringCostID)) stringSql += " AND  CostID  =  '" + stringCostID + "' ";
    for (int intNo = 0; intNo < arrayUseType.length; intNo++) {
      if (!"".equals(stringSqlAndL)) stringSqlAndL += " OR ";
      stringSqlAndL += " UseType  LIKE  '%" + arrayUseType[intNo] + "%' ";
    }
    // if(!"".equals(stringUseType)) stringSql += " AND UseType LIKE
    // '%"+stringUseType+"%' " ;
    stringSql += " AND (" + stringSqlAndL + ")";
    stringSql += stringSqlAnd;
    retDoc2M0401 = dbDoc.queryFromPool(stringSql);
    return retDoc2M0401;
  }

  public Vector getDoc2M0401V(String stringCostID, String stringUseType, String stringSqlAnd, talk dbDoc) throws Throwable {
    String[][] retDoc2M040 = getDoc2M0401(stringCostID, stringUseType, stringSqlAnd, dbDoc);
    Vector vectorCostID = new Vector();
    for (int intNo = 0; intNo < retDoc2M040.length; intNo++)
      vectorCostID.add(retDoc2M040[intNo][0].trim());
    return vectorCostID;
  }

  public Hashtable getDoc2M0401H(String stringCostID, String stringUseType, String stringSqlAnd, talk dbDoc) throws Throwable {
    String stringUseTypeL = "";
    String[] arrayUseType = convert.StringToken(stringUseType, ",");
    String[][] retDoc2M0401 = getDoc2M0401(stringCostID, stringUseType, stringSqlAnd, dbDoc);
    Vector vectorCostID = new Vector();
    Hashtable hashtableType = new Hashtable();
    Object objectTemp = null;
    for (int intNo = 0; intNo < retDoc2M0401.length; intNo++) {
      stringCostID = retDoc2M0401[intNo][0].trim();
      stringUseType = retDoc2M0401[intNo][1].trim();
      for (int intNoL = 0; intNoL < arrayUseType.length; intNoL++) {
        stringUseTypeL = arrayUseType[intNoL].trim();
        if (stringUseType.indexOf(stringUseTypeL) == -1) continue;
        //
        objectTemp = hashtableType.get(stringUseTypeL);
        if (objectTemp == null) {
          vectorCostID = new Vector();
          hashtableType.put(stringUseTypeL, vectorCostID);
        } else {
          vectorCostID = (Vector) objectTemp;
        }
        vectorCostID.add(stringCostID);
      }
    }
    return hashtableType;
  }

  public void doDoc5M0272(String stringBarCode, String stringEDateTime, String stringComNo, String stringKindNo, String stringKindNoFront, String stringFactoryNo,
      String stringSqlAndPurchaseNo, Hashtable hashtableDoc5M0272, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    Object objectTemp = null;
    String stringKey = "";
    String stringPurchaseNo = "";
    String stringRecordNo = "";
    String stringRequestNum = "";
    String stringPurchaseMoney = "";
    String[] arrayTemp = new String[4];
    String[][] retPreDoc5M0272 = getDoc5M0272(stringEDateTime, stringComNo, stringKindNo,
        " AND  (" + stringSqlAndPurchaseNo + ") \nAND  BarCode  <>  '" + stringBarCode + "' \n AND  FactoryNo  =  '" + stringFactoryNo + "' ", dbDoc);
    String[][] retDoc5M0272 = getDoc5M0272(stringEDateTime, stringComNo, stringKindNo, " AND  (" + stringSqlAndPurchaseNo + ")  \nAND  BarCode  =  '" + stringBarCode + "' \n",
        dbDoc);
    // 0 PurchaseNo 1 RecordNo 2 RequestNum 3 PurchaseMoney
    for (int intNo = 0; intNo < retPreDoc5M0272.length; intNo++) {
      stringPurchaseNo = retPreDoc5M0272[intNo][0].trim();
      stringRecordNo = retPreDoc5M0272[intNo][1].trim();
      stringRequestNum = retPreDoc5M0272[intNo][2].trim();
      stringPurchaseMoney = retPreDoc5M0272[intNo][3].trim();
      stringKey = stringPurchaseNo + stringRecordNo;
      System.out.println("stringKey(" + stringKey + ")前期--------------------------------(" + stringRequestNum + ")(" + stringPurchaseMoney + ")");
      //
      objectTemp = hashtableDoc5M0272.get(stringKey);
      if (objectTemp == null) {
        arrayTemp = new String[4];
        for (int i = 0; i < arrayTemp.length; i++)
          arrayTemp[i] = "";
        hashtableDoc5M0272.put(stringKey, arrayTemp);
      } else {
        arrayTemp = (String[]) objectTemp;
      }
      arrayTemp[0] = "" + (exeUtil.doParseDouble(arrayTemp[0]) + exeUtil.doParseDouble(stringRequestNum));
      arrayTemp[1] = "" + (exeUtil.doParseDouble(arrayTemp[1]) + exeUtil.doParseDouble(stringPurchaseMoney));
    }
    String[][] retPreDoc5M02722 = getDoc5M02722(stringEDateTime, stringComNo, stringKindNoFront, " AND  (" + stringSqlAndPurchaseNo + ")", exeUtil, dbDoc);
    for (int intNo = 0; intNo < retPreDoc5M02722.length; intNo++) {
      stringPurchaseNo = retPreDoc5M02722[intNo][0].trim();
      stringRecordNo = retPreDoc5M02722[intNo][1].trim();
      stringRequestNum = retPreDoc5M02722[intNo][2].trim();
      stringPurchaseMoney = retPreDoc5M02722[intNo][3].trim();
      stringKey = stringPurchaseNo + stringRecordNo;
      System.out.println("stringKey(" + stringKey + ")前期--------------------------------" + stringRequestNum);
      //
      objectTemp = hashtableDoc5M0272.get(stringKey);
      if (objectTemp == null) {
        arrayTemp = new String[4];
        for (int i = 0; i < arrayTemp.length; i++)
          arrayTemp[i] = "";
        hashtableDoc5M0272.put(stringKey, arrayTemp);
      } else {
        arrayTemp = (String[]) objectTemp;
      }
      arrayTemp[0] = "" + (exeUtil.doParseDouble(arrayTemp[0]) + exeUtil.doParseDouble(stringRequestNum));
      arrayTemp[1] = "" + (exeUtil.doParseDouble(arrayTemp[1]) + exeUtil.doParseDouble(stringPurchaseMoney));
    }
    for (int intNo = 0; intNo < retDoc5M0272.length; intNo++) {
      stringPurchaseNo = retDoc5M0272[intNo][0].trim();
      stringRecordNo = retDoc5M0272[intNo][1].trim();
      stringRequestNum = retDoc5M0272[intNo][2].trim();
      stringPurchaseMoney = retDoc5M0272[intNo][3].trim();
      stringKey = stringPurchaseNo + stringRecordNo;
      System.out.println("stringKey(" + stringKey + ")本期--------------------------------(" + stringRequestNum + ")(" + stringPurchaseMoney + ")");
      //
      objectTemp = hashtableDoc5M0272.get(stringKey);
      if (objectTemp == null) {
        arrayTemp = new String[4];
        for (int i = 0; i < arrayTemp.length; i++)
          arrayTemp[i] = "";
        hashtableDoc5M0272.put(stringKey, arrayTemp);
      } else {
        arrayTemp = (String[]) objectTemp;
      }
      arrayTemp[2] = "" + (exeUtil.doParseDouble(arrayTemp[2]) + exeUtil.doParseDouble(stringRequestNum));
      arrayTemp[3] = "" + (exeUtil.doParseDouble(arrayTemp[3]) + exeUtil.doParseDouble(stringPurchaseMoney));
    }
  }

  // 已請款案別金額整理
  public Hashtable getUsedProjectIDMoney(boolean booleanSource, String stringBarCode, String stringEDateTime, String stringComNo, String[][] retDoc2M017, Vector vectorKey,
      Hashtable hashtableRealMoneyAll, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String[] arrayTemp = null;
    Vector vectorTable6Data = new Vector();
    // 0 PurchaseNo1 1 PurchaseNo2 2 PurchaseNo3 3 RetainMoney 4 PurchaseMoney
    // 5 PurchaseNo4 6 FactoryNo 7 ProjectID1
    for (int intNo = 0; intNo < retDoc2M017.length; intNo++) {
      arrayTemp = new String[7];
      arrayTemp[0] = retDoc2M017[intNo][0].trim(); // PurchaseNo1
      arrayTemp[1] = retDoc2M017[intNo][1].trim(); // "PurchaseNo2
      arrayTemp[2] = retDoc2M017[intNo][2].trim(); // "PurchaseNo3
      arrayTemp[3] = retDoc2M017[intNo][5].trim(); // "PurchaseNo4
      arrayTemp[4] = retDoc2M017[intNo][4].trim(); // "PurchaseMoney
      arrayTemp[5] = retDoc2M017[intNo][6].trim(); // "FactoryNo
      arrayTemp[6] = retDoc2M017[intNo][7].trim();
      vectorTable6Data.add(arrayTemp);
    }
    return getUsedProjectIDMoneyDetail(booleanSource, stringComNo, stringBarCode, stringEDateTime, (String[][]) vectorTable6Data.toArray(new String[0][0]), vectorKey,
        hashtableRealMoneyAll, exeUtil, dbDoc);
  }

  public Hashtable getUsedProjectIDMoneyDetail(boolean booleanSource, String stringComNo, String stringBarCode, String stringEDateTime, String[][] retTable6Data, Vector vectorKey,
      Hashtable hashtableRealMoneyAll, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    // System.out.println("已請款案別金額整理----------------------S") ;
    Hashtable hashtableRealMoney = new Hashtable();
    String stringBarCodeL = "";
    String stringKey = "";
    String stringLimit = "%---%";
    String stringPurchaseMoney = "";
    String stringPurchaseNo = "";
    String stringPurchaseNo1 = "";
    String stringPurchaseNo1L = "";
    String stringPurchaseNo2 = "";
    String stringPurchaseNo2L = "";
    String stringPurchaseNo3 = "";
    String stringPurchaseNo3L = "";
    String stringPurchaseNo4 = "";
    String stringSqlAnd = "";
    String stringSqlAnd1 = "";
    String stringFactoryNo = "";
    String stringFactoryNoL = "";
    String stringPurchaseSumMoney = "";
    String stringNoUseRealMoney = "";
    String stringProjectID1 = "";
    String stringProjectID1L = "";
    String[] arrayTemp = null;
    String[][] retDataTemp = null;
    String[][] retDoc2M010 = null;
    String[][] retDoc2M012 = null;
    String[][] retDoc3M013 = null;
    String[][] retDoc3M014 = null;
    double doubleRealMoney = 0;
    double doublePurchaseMoney = 0;
    double doublePurchaseMoneySum = 0;
    double doubleTemp = 0;
    Vector vectorBarCode = new Vector();
    Vector vectorData = new Vector();
    Vector vectorKeyThis = new Vector();
    Vector vectorFactoryNo = new Vector();
    Vector vectorPurchaseSumMoney = new Vector();
    //
    for (int intNo = 0; intNo < retTable6Data.length; intNo++) {
      stringPurchaseNo1 = retTable6Data[intNo][0].trim();
      stringPurchaseNo2 = retTable6Data[intNo][1].trim();
      stringPurchaseNo3 = retTable6Data[intNo][2].trim();
      stringPurchaseNo4 = retTable6Data[intNo][3].trim();
      stringPurchaseMoney = retTable6Data[intNo][4].trim();
      stringFactoryNo = retTable6Data[intNo][5].trim();
      stringProjectID1 = retTable6Data[intNo][6].trim();
      //
      if (!"".equals(stringSqlAnd)) stringSqlAnd += " OR ";
      stringSqlAnd = " (PurchaseNo  =  '" + stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3 + "') ";
      //
      doublePurchaseMoneySum += exeUtil.doParseDouble(stringPurchaseMoney);
      //
      if (!"Z".equals(stringPurchaseNo4)) {
        System.out.println("已請款案別金額整理----------------------ERROR1");
        return null;
      }
      // 請購之案別分攤整理
      // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
      // 5 CostID1 6 RealMoney 7 BudgetMoney
      retDoc3M014 = getDoc5M014(booleanSource, "", stringComNo, stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3, "", dbDoc);
      vectorKeyThis = new Vector();
      for (int intNoL = 0; intNoL < retDoc3M014.length; intNoL++) {
        doubleRealMoney = exeUtil.doParseDouble(retDoc3M014[intNoL][6].trim());
        //
        if ("".equals(retDoc3M014[intNoL][3].trim())) { // ProjectID1
          stringKey = "I" + stringLimit + retDoc3M014[intNoL][1].trim() + stringLimit + // 0 DepartNo
              retDoc3M014[intNoL][4].trim() + stringLimit + // 6 CostID
              retDoc3M014[intNoL][5].trim(); // 7 CostID1
        } else {
          stringKey = "O" + stringLimit + retDoc3M014[intNoL][3].trim() + stringLimit + // 5 ProjectID1
              retDoc3M014[intNoL][4].trim() + stringLimit + // 6 CostID
              retDoc3M014[intNoL][5].trim(); // 7 CostID1
        }
        doubleTemp = doubleRealMoney + exeUtil.doParseDouble("" + hashtableRealMoney.get(stringKey));
        hashtableRealMoney.put(stringKey, "" + doubleTemp);
        doubleTemp = doubleRealMoney + exeUtil.doParseDouble("" + hashtableRealMoneyAll.get(stringKey));
        hashtableRealMoneyAll.put(stringKey, "" + doubleTemp);
        System.out.println(intNo + "請購案別金額整理----------------------[" + stringKey + "]" + doubleRealMoney);
        //
        if (vectorKey.indexOf(stringKey) == -1) vectorKey.add(stringKey);
        if (vectorKeyThis.indexOf(stringKey) == -1) vectorKeyThis.add(stringKey);
      }
      retDoc3M013 = getDoc3M013Union(stringComNo, stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3, "", booleanSource, dbDoc);
      for (int intNoL = 0; intNoL < retDoc3M013.length; intNoL++) {
        stringFactoryNoL = retDoc3M013[intNoL][0].trim();
        stringPurchaseSumMoney = retDoc3M013[intNoL][1].trim();
        stringNoUseRealMoney = retDoc3M013[intNoL][8].trim();
        //
        if (exeUtil.doParseDouble(stringNoUseRealMoney) > 0) {
          if (vectorFactoryNo.indexOf(stringFactoryNoL) != -1) {
            vectorPurchaseSumMoney.remove(vectorFactoryNo.indexOf(stringFactoryNoL));
            vectorFactoryNo.remove(stringFactoryNoL);
          }
          //
          vectorFactoryNo.add(stringFactoryNoL);
          vectorPurchaseSumMoney.add("" + (exeUtil.doParseDouble(stringPurchaseSumMoney) - exeUtil.doParseDouble(stringNoUseRealMoney)));
        } else {
          if (vectorFactoryNo.indexOf(stringFactoryNoL) == -1) {
            vectorFactoryNo.add(stringFactoryNoL);
            vectorPurchaseSumMoney.add(stringPurchaseSumMoney);
          }
        }
      }
      // 請款申請書
      retDoc2M010 = getDoc5M020(booleanSource, stringBarCode, stringEDateTime, stringComNo, stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3, "",
          dbDoc);
      vectorBarCode = new Vector();
      //
      vectorBarCode.add(stringBarCode); // 本身不計算
      // 請購為單一案別分攤 直接扣除
      // 請款[單請購單] 直接扣除
      // 請款[多請購單] 請款 單費用分攤 直接使用請購金額扣除
      // 請款 多費用分攤 [先扣除所有請款案別分攤] [再加非此請購單之案別分攤]
      for (int intNoL = 0; intNoL < retDoc2M010.length; intNoL++) {
        stringBarCodeL = retDoc2M010[intNoL][0].trim();
        //
        if (vectorBarCode.indexOf(stringBarCodeL) != -1) continue;
        vectorBarCode.add(stringBarCodeL);
        //
        retDataTemp = getDoc5M027Union(booleanSource ? "Doc2M017" : "Doc5M027", stringBarCodeL, dbDoc);
        for (int intDataTemp = 0; intDataTemp < retDataTemp.length; intDataTemp++) {
          stringPurchaseNo1L = retDataTemp[intDataTemp][0].trim();
          stringPurchaseNo2L = retDataTemp[intDataTemp][1].trim();
          stringPurchaseNo3L = retDataTemp[intDataTemp][2].trim();
          stringProjectID1L = retDataTemp[intDataTemp][7].trim();
          stringPurchaseNo = stringPurchaseNo1L + stringProjectID1L + stringPurchaseNo2L + stringPurchaseNo3L;
          //
          if (stringPurchaseNo.equals(stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3)) {
            doublePurchaseMoney = exeUtil.doParseDouble(retDataTemp[intDataTemp][4].trim());
            break;
          }
        }
        // 請購為單一案別分攤
        if (vectorKeyThis.size() == 1) {
          stringKey = ("" + vectorKeyThis.get(0)).trim();
          doubleRealMoney = exeUtil.doParseDouble("" + hashtableRealMoney.get(stringKey)) - doublePurchaseMoney;
          hashtableRealMoney.put(stringKey, "" + doubleRealMoney);
          System.out.println("1請款   案別金額整理----------------------請購為單一案別分攤[" + stringKey + "]" + doubleRealMoney);
          continue;
        }
        // 請款申請書之請購筆數
        vectorData = new Vector();
        // 取得 該筆之費用分攤
        retDoc2M012 = getDoc5M022Union(booleanSource ? "Doc2M012" : "Doc5M022", stringBarCodeL, "");
        for (int intT = 0; intT < retDoc2M012.length; intT++) {
          if ("".equals(retDoc2M012[intT][3].trim())) {
            stringKey = "I" + stringLimit + retDoc2M012[intT][1].trim() + stringLimit + // 3 DepartNo
                retDoc2M012[intT][4].trim() + stringLimit + // 6 CostID
                retDoc2M012[intT][5].trim(); // 7 CostID1
          } else {
            stringKey = "O" + stringLimit + retDoc2M012[intT][3].trim() + stringLimit + // 5 ProjectID1
                retDoc2M012[intT][4].trim() + stringLimit + // 6 CostID
                retDoc2M012[intT][5].trim(); // 7 CostID1
          }
          // 針對請購單之資料，過濾費用分攤資料，並加總金額
          if (vectorKeyThis.indexOf(stringKey) == -1) continue;
          //
          if (retDoc2M012.length == 1) {
            doubleRealMoney = doublePurchaseMoney;
            System.out.println("2 請款   案別金額整理----------------------請款單一案別分攤[" + stringKey + "]" + doubleRealMoney);
          } else {
            doubleRealMoney = exeUtil.doParseDouble(retDoc2M012[intT][7].trim());
            System.out.println("3 請款   案別金額整理----------------------請款多案別分攤 [先] 刪除[" + stringKey + "]" + doubleRealMoney);
          }
          //
          doubleRealMoney = exeUtil.doParseDouble("" + hashtableRealMoney.get(stringKey)) - doubleRealMoney;
          hashtableRealMoney.put(stringKey, "" + doubleRealMoney);
        }
        if (retDataTemp.length > 1) {
          // [請款申請書]對應多筆 [請購單]
          // 0 PurchaseNo1 1 PurchaseNo2 2 PurchaseNo3 3 RetainMoney 4 PurchaseMoney
          // 5 PurchaseNo4 6 FactoryNo
          // 請款多費用分攤
          if (retDoc2M012.length > 1) {
            for (int intT = 0; intT < retDataTemp.length; intT++) {
              stringPurchaseNo1L = retDataTemp[intT][0].trim();
              stringPurchaseNo2L = retDataTemp[intT][1].trim();
              stringPurchaseNo3L = retDataTemp[intT][2].trim();
              stringProjectID1L = retDataTemp[intT][7].trim();
              doublePurchaseMoney = exeUtil.doParseDouble(retDataTemp[intT][4].trim());
              stringPurchaseNo = stringPurchaseNo1L + stringProjectID1L + stringPurchaseNo2L + stringPurchaseNo3L;
              // 非本次請購單才作加回動作
              if (stringPurchaseNo.equals(stringPurchaseNo1 + stringProjectID1 + stringPurchaseNo2 + stringPurchaseNo3)) continue;
              //
              // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
              // 5 CostID1 6 RealMoney 7 BudgetMoney
              retDoc3M014 = getDoc5M014(booleanSource, "", stringComNo, stringPurchaseNo, "", dbDoc);
              for (int intDoc3M014 = 0; intDoc3M014 < retDoc3M014.length; intDoc3M014++) {
                doubleRealMoney = exeUtil.doParseDouble(retDoc3M014[intDoc3M014][6].trim());
                //
                if ("".equals(retDoc3M014[intDoc3M014][3].trim())) {
                  stringKey = "I" + stringLimit + retDoc3M014[intDoc3M014][1].trim() + stringLimit + // 3 DepartNo
                      retDoc3M014[intDoc3M014][4].trim() + stringLimit + // 6 CostID
                      retDoc3M014[intDoc3M014][5].trim(); // 7 CostID1
                } else {
                  stringKey = "O" + stringLimit + retDoc3M014[intDoc3M014][3].trim() + stringLimit + // 5 ProjectID1
                      retDoc3M014[intDoc3M014][4].trim() + stringLimit + // 6 CostID
                      retDoc3M014[intDoc3M014][5].trim(); // 7 CostID1
                }
                if (vectorKeyThis.indexOf(stringKey) == -1) continue;
                //
                if (retDoc3M014.length == 1) {
                  // 該請購單為單一案別分攤
                  doubleRealMoney = exeUtil.doParseDouble("" + hashtableRealMoney.get(stringKey)) + doublePurchaseMoney;
                  System.out.println("4請款   案別金額整理----------------------請款多案別分攤 [後] 加回 請購單單一案別分攤[" + stringKey + "]" + doubleRealMoney);
                  hashtableRealMoney.put(stringKey, "" + doubleRealMoney);
                } else {
                  // 非模糊(該請購為單一廠商，且請款時為一次使用完)
                  doubleRealMoney = exeUtil.doParseDouble("" + hashtableRealMoney.get(stringKey)) + doubleRealMoney;
                  hashtableRealMoney.put(stringKey, "" + doubleRealMoney);
                  System.out.println("4請款   案別金額整理----------------------請款多案別分攤 [後] 加回 請購單一次申請書完[" + stringKey + "]" + doubleRealMoney);
                }
              }
            }
          }
        }
      }
    }
    // 借款沖銷
    if (!"".equals(stringSqlAnd)) stringSqlAnd1 = " AND  BarCode  IN  (SELECT  BarCode " + " FROM  " + (booleanSource ? "Doc6M010" : "Doc5M030") + " " + " WHERE  (" + stringSqlAnd
        + ")" + " AND  EDateTime  <= '" + stringEDateTime + "' " + " AND  PurchaseNoExist  =  'Y' )";
    for (int intNo = 0; intNo < vectorKey.size(); intNo++) {
      stringKey = ("" + vectorKey.get(intNo));
      arrayTemp = convert.StringToken(stringKey, stringLimit);
      //
      if (arrayTemp.length != 4) {
        System.out.println("已請款案別金額整理----------------------ERROR3[" + stringKey + "]");
        return null;
      }
      //
      doubleRealMoney = exeUtil.doParseDouble("" + hashtableRealMoney.get(stringKey));
      doubleRealMoney -= getRealTotalMoneySumForDoc5M032(booleanSource, stringBarCode, arrayTemp[0], arrayTemp[1], arrayTemp[2], arrayTemp[3], stringSqlAnd1, exeUtil);
      hashtableRealMoney.put(stringKey, "" + doubleRealMoney);
      System.out.println(doubleRealMoney + "----------------------[" + stringKey + "]");
    }
    // System.out.println("已請款案別金額整理----------------------E") ;
    return hashtableRealMoney;
  }

  public boolean isSpectPurchaseNo(String stringComNo, String stringKindNo, String stringDocNo, String stringSqlAnd, boolean booleanSource, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc3M013 = null;
    //
    if (!booleanSource) return false;
    //
    stringSql = " SELECT  M13.BarCode " + " FROM  Doc3M011 M11,  Doc3M013 M13 " + " WHERE  M11.BarCode  =  M13.BarCode " + stringSqlAnd + " AND  M11.ComNo  =  '" + stringComNo
        + "' " + " AND  M11.DocNo  =  '" + stringDocNo + "' " + " AND  M11.KindNo  =  '" + stringKindNo + "' ";
    retDoc3M013 = dbDoc.queryFromPool(stringSql);
    return retDoc3M013.length > 0;
  }

  // 表格 Doc5M013
  public String[][] getDoc3M013Union(String stringComNo, String stringDocNo, String stringFactoryNo, boolean booleanTable, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringTable1 = booleanTable ? "Doc3M013" : "Doc5M013";
    String stringTable2 = booleanTable ? "Doc3M011" : "Doc5M011";
    String[][] retDoc3M013 = null;
    // 0 FactoryNo 1 PurchaseSumMoney 2 PercentRate 3 MonthNum 4 PurchaseMoney
    // 5 PayCondition1 6 PayCondition2 7 Descript
    stringSql = " SELECT  FactoryNo,              PurchaseSumMoney,   PercentRate,  MonthNum,      PurchaseMoney, "
        + " PayCondition1,      PayCondition2,              Descript,         NoUseRealMoney" + " FROM  " + stringTable1 + " " + " WHERE  BarCode IN  (SELECT BarCode " + " FROM  "
        + stringTable2 + " " + " WHERE  ComNo =  '" + stringComNo + "' " + " AND  DocNo  =  '" + stringDocNo + "') ";
    if (!"".equals(stringFactoryNo)) stringSql += " AND  FactoryNo  =  '" + stringFactoryNo + "' ";
    stringSql += " ORDER BY FactoryNo, RecordNo ";
    retDoc3M013 = dbDoc.queryFromPool(stringSql);
    return retDoc3M013;
  }

  // 表格 Doc5M014
  public String[][] getDoc5M014(boolean booleanSource, String stringBarCode, String stringComNo, String stringDocNo, String stringSqlAnd, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringSqlTemp = "";
    String stringTable14 = booleanSource ? "Doc3M014" : "Doc5M014";
    String stringTable11 = booleanSource ? "Doc3M011" : "Doc5M011";
    String[][] retDoc3M014 = null;
    // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
    // 5 CostID1 6 RealMoney 7 BudgetMoney
    stringSql = "SELECT  M14.InOut,     M14.DepartNo,                                               M14.ProjectID,            M14.ProjectID1, M14.CostID, "
        + " M14.CostID1, (M14.RealMoney-M14.NoUseRealMoney), M14.BudgetMoney " + " FROM  " + stringTable14 + " M14 ,  " + stringTable11 + " M11 "
        + " WHERE  M14.BarCode  =  M11.BarCode " + " AND  M11.UNDERGO_WRITE  <>  'X' ";
    if (!"".equals(stringBarCode)) stringSql += " AND  M11.BarCode  =  '" + stringBarCode + "' ";
    if (!"".equals(stringComNo)) stringSql += " AND  M11.ComNo    =  '" + stringComNo + "' ";
    if (!"".equals(stringDocNo)) stringSql += " AND  M11.DocNo    =  '" + stringDocNo + "' ";
    stringSql += stringSqlAnd + " ORDER BY  M14.ProjectID1,  M14.CostID,  M14.CostID1 ";
    retDoc3M014 = dbDoc.queryFromPool(stringSql);
    return retDoc3M014;
  }

  // 表格 Doc5M020
  public String getRetainMoneyDoc5M020(String stringBarCode, String stringComNo, String stringFactoryNo, String stringEDateTime, String stringKindNo, String stringSqlAnd,
      talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0272 = null;
    //
    stringSql = "SELECT  SUM(M20.RetainMoney) " + " FROM  Doc5M020 M20,  Doc5M027 M27 " + " WHERE  M20.BarCode  =  M27.BarCode " + " AND  M20.BarCode  <>  '" + stringBarCode + "' "
        + " AND  EDateTime  <=  '" + stringEDateTime + "' " + " AND  ComNo  =  '" + stringComNo + "' " + " AND  KindNo  =  '" + stringKindNo + "' " + " AND  FactoryNo  =  '"
        + stringFactoryNo + "' " + " AND  PurchaseNo  IN  (" + stringSqlAnd + ") ";
    retDoc5M0272 = dbDoc.queryFromPool(stringSql);

    return retDoc5M0272[0][0];
  }

  public String[][] getDoc5M020(boolean booleanSource, String stringBarCode, String stringEDateTime, String stringComNo, String stringPurchaseNo, String stringSqlAnd, talk dbDoc)
      throws Throwable {
    String stringSql = "";
    String stringTable10 = booleanSource ? "Doc2M010" : "Doc5M020";
    String stringTable17 = booleanSource ? "Doc2M017" : "Doc5M027";
    String[][] retDoc5M020 = null;
    // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
    // 5 CostID1 6 RealMoney 7 BudgetMoney
    stringSql = "SELECT  BarCode " + " FROM  " + stringTable10 + " " + " WHERE  BarCode <> '" + stringBarCode + "' " + " AND  UNDERGO_WRITE  <>  'E' " + " AND  ComNo  =  '"
        + stringComNo + "' " + " AND  EDateTime  <=  '" + stringEDateTime + "' " + " AND  BarCode  IN  (SELECT  BarCode " + " FROM  " + stringTable17 + " "
        + " WHERE  PurchaseNo  =  '" + stringPurchaseNo + "') " + " ORDER BY  EDateTime ";
    retDoc5M020 = dbDoc.queryFromPool(stringSql);
    return retDoc5M020;
  }

  public String[][] getDoc5M022Union(String stringTable, String stringBarCode, String stringSqlAnd) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M022 = null;
    // 0 InOut 1 DepartNo 2 ProjectID 3 ProjectID1 4 CostID
    // 5 CostID1 6 RealMoney 7 RealTotalMoney
    stringSql = "SELECT  InOut,      DepartNo,       ProjectID,       ProjectID1,  CostID, " + " CostID1,   RealMoney,    RealTotalMoney " + " FROM  " + stringTable + " "
        + " WHERE  1=1 ";
    if (!"".equals(stringBarCode)) stringSql += " AND  BarCode  =  '" + stringBarCode + "' ";
    stringSql += stringSqlAnd + " ORDER BY  RecordNo ";
    retDoc5M022 = dbDoc.queryFromPool(stringSql);
    return retDoc5M022;
  }

  // 表格 Doc5M023
  public String getReceiptTaxDoc5M023(String stringBarCode, String stringComNo, String stringFactoryNo, String stringEDateTime, String stringKindNo, String stringSqlAnd,
      talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0272 = null;
    //
    stringSql = "SELECT  SUM(ISNULL(ReceiptTax,0)+ISNULL(SupplementMoney,0)) " + " FROM  Doc5M020 M20,  Doc5M027 M27,  Doc5M023 M23 " + " WHERE  M20.BarCode  =  M27.BarCode "
        + " AND  M20.BarCode  =  M23.BarCode " + " AND  M20.BarCode  <>  '" + stringBarCode + "' " + " AND  EDateTime  <=  '" + stringEDateTime + "' " + " AND  ComNo  =  '"
        + stringComNo + "' " + " AND  KindNo  =  '" + stringKindNo + "' " + " AND  M27.FactoryNo  =  '" + stringFactoryNo + "' " + " AND  PurchaseNo  IN  (" + stringSqlAnd + ") ";
    retDoc5M0272 = dbDoc.queryFromPool(stringSql);

    return retDoc5M0272[0][0];
  }

  // 表格 Doc5M027
  public String[][] getDoc5M027Union(String stringTable, String stringBarCode, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M027 = null;
    // 0 PurchaseNo1 1 PurchaseNo2 2 PurchaseNo3 3 RetainMoney 4 PurchaseMoney
    // 5 PurchaseNo4 6 FactoryNo 7 ProjectID1
    stringSql = "SELECT  PurchaseNo1,  PurchaseNo2,  PurchaseNo3,  RetainMoney,  PurchaseMoney, " + " PurchaseNo4,  FactoryNo,    ProjectID1 " + " FROM  " + stringTable + " "
        + " WHERE  BarCode  =  '" + stringBarCode + "' ";
    retDoc5M027 = dbDoc.queryFromPool(stringSql);

    return retDoc5M027;
  }

  // 表格 Doc5M0272
  public String[][] getITEMCountDoc5M0272(String stringEDateTime, String stringComNo, String stringKindNo, String stringFactoryNo, String stringSqlAnd, talk dbDoc)
      throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0272 = null;
    stringSql = "SELECT  DISTINCT  PurchaseNo,  RecordNo12\n" + " FROM  Doc5M0272 \n" + " WHERE  FactoryNo  =  '" + stringFactoryNo + "' " + stringSqlAnd;
    if (!"".equals(stringComNo) || !"".equals(stringEDateTime)) {
      stringSql += " AND  BarCode  IN  (SELECT  BarCode \n" + " FROM  Doc5M020 \n" + " WHERE  UNDERGO_WRITE  <>  'E' \n";
      if (!"".equals(stringEDateTime)) stringSql += " AND  EDateTime  <=  '" + stringEDateTime + "' \n";
      if (!"".equals(stringComNo)) stringSql += " AND  ComNo  =  '" + stringComNo + "' \n";
      if (!"".equals(stringKindNo)) stringSql += " AND  KindNo  =  '" + stringKindNo + "' \n";
      stringSql += ") \n";
    }
    retDoc5M0272 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0272;
  }

  public String[][] getDoc5M0272(String stringEDateTime, String stringComNo, String stringKindNo, String stringSqlAnd, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0272 = null;
    // 0 PurchaseNo 1 RecordNo12 2 RequestNum 3 PurchaseMoney
    stringSql = "SELECT  PurchaseNo,  RecordNo12,  SUM(RequestNum),  SUM(PurchaseMoney) \n" + " FROM  Doc5M0272 \n" + " WHERE  1=1 " + stringSqlAnd;
    if (!"".equals(stringComNo) || !"".equals(stringEDateTime)) {
      stringSql += " AND  BarCode  IN  (SELECT  BarCode \n" + " FROM  Doc5M020 \n" + " WHERE  UNDERGO_WRITE  <>  'E' \n";
      if (!"".equals(stringEDateTime)) stringSql += " AND  EDateTime  <=  '" + stringEDateTime + "' \n";
      if (!"".equals(stringComNo)) stringSql += " AND  ComNo  =  '" + stringComNo + "' \n";
      if (!"".equals(stringKindNo)) stringSql += " AND  KindNo  =  '" + stringKindNo + "' \n";
      stringSql += ") \n";
    }
    stringSql += " GROUP BY  PurchaseNo,  RecordNo12 ";
    retDoc5M0272 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0272;
  }

  // 表格 Doc5M02722
  public String[][] getDoc5M02722(String stringEDateTime, String stringComNo, String stringKindNoFront, String stringSqlAnd, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M02722 = null;
    // 0 PurchaseNo 1 RecordNo12 2 RequestNum 3 PurchaseMoney
    stringSql = "SELECT  PurchaseNo,  RecordNo12,  SUM(RequestNum),  SUM(RequestPrice) \n" + " FROM  Doc5M02722 \n" + " WHERE  1=1 " + stringSqlAnd;
    if (!"".equals(stringEDateTime)) stringSql += " AND  RequestDate  <=  '" + exeUtil.doSubstring(stringEDateTime, 0, 10) + "' \n";
    if (!"".equals(stringComNo)) stringSql += " AND  ComNo  =  '" + stringComNo + "' \n";
    if (!"".equals(stringKindNoFront)) stringSql += " AND  KindNo  =  '" + stringKindNoFront + "' \n";
    stringSql += " GROUP BY  PurchaseNo,  RecordNo12 ";
    retDoc5M02722 = dbDoc.queryFromPool(stringSql);
    return retDoc5M02722;
  }

  // 表格 Doc5M032
  public double getRealTotalMoneySumForDoc5M032(boolean booleanSource, String stringBarCodeExcept, String stringInOut, String stringDepartNo, String stringCostID,
      String stringCostID1, String stringSqlAnd, FargloryUtil exeUtil) throws Throwable {
    String stringSql = "";
    String stringTable10 = booleanSource ? "Doc6M010" : "Doc5M030";
    String stringTable12 = booleanSource ? "Doc6M012" : "Doc5M032";
    String[][] retDoc5M032 = null;
    //
    stringSql = "SELECT  SUM(RealTotalMoney) " + " FROM  " + stringTable12 + " " + " WHERE  BarCode  NOT  IN  (SELECT  BarCode  FROM  Doc6M010  WHERE  UNDERGO_WRITE  =  'E' )";
    if (!"".equals(stringBarCodeExcept)) stringSql += "  AND  BarCode  <>  '" + stringBarCodeExcept + "' ";
    if ("I".equals(stringInOut)) {
      if (!"".equals(stringDepartNo)) stringSql += " AND  ISNULL(ProjectID1,'')  = '' " + " AND  DepartNo  =  '" + stringDepartNo + "' ";
    } else {
      if (!"".equals(stringDepartNo)) stringSql += " AND  ProjectID1  =  '" + stringDepartNo + "' ";
    }
    if (!"".equals(stringCostID)) stringSql += " AND  CostID  =  '" + stringCostID + "' ";
    if (!"".equals(stringCostID1)) stringSql += " AND  CostID1  =  '" + stringCostID1 + "' ";
    stringSql += stringSqlAnd;
    retDoc5M032 = dbDoc.queryFromPool(stringSql);
    return exeUtil.doParseDouble(retDoc5M032[0][0]);
  }

  public String[][] getDoc5M0220(String stringBarCodeRef, String stringFactoryNo, String stringBarCode, String stringSqlAnd) throws Throwable {
    String stringSql = "";
    String[][] retDoc5M0220 = null;
    // 0 BarCode 1 BarCodeRef 2 FactoryNo 3 RecordNo 4 BackRetainMoney 5 EDateTime
    stringSql = "SELECT  M220.BarCode,  M220.BarCodeRef,  M220.FactoryNo,  M220.RecordNo,  M220.BackRetainMoney,  M20.EDateTime " + " FROM  Doc5M0220 M220,  Doc5M020 M20 "
        + "  WHERE  M220.BarCode  =  M20.BarCode " + stringSqlAnd;
    if (!"".equals(stringFactoryNo)) stringSql += "  AND  M220.FactoryNo  =  '" + stringFactoryNo + "' ";
    if (!"".equals(stringBarCode)) stringSql += "  AND  M220.BarCode    =  '" + stringBarCode + "' ";
    if (!"".equals(stringBarCodeRef)) stringSql += "  AND  M220.BarCodeRef =  '" + stringBarCodeRef + "' ";
    stringSql += " ORDER BY  M20.EDateTime,  M220.RecordNo ";
    retDoc5M0220 = dbDoc.queryFromPool(stringSql);
    return retDoc5M0220;
  }

  public double getBackRetainMoneyDoc5M0220(String stringBarCodeRef, String stringFactoryNo, String stringBarCode, String stringSqlAnd, FargloryUtil exeUtil) throws Throwable {
    String[][] retDoc5M0220 = getDoc5M0220(stringBarCodeRef, stringFactoryNo, stringBarCode, stringSqlAnd);
    double doubleBackRetainMoney = 0;
    for (int intNo = 0; intNo < retDoc5M0220.length; intNo++) {
      doubleBackRetainMoney += exeUtil.doParseDouble(retDoc5M0220[intNo][4].trim());
    }
    return doubleBackRetainMoney;
  }

  public String getDeptCdDoc(String stringDeptCd, String stringDeptCdDoc, String stringSqlAnd) throws Throwable {
    return getDeptCdDoc(stringDeptCd, stringDeptCdDoc, stringSqlAnd, new Hashtable());
  }

  public String getDeptCdDoc(String stringDeptCd, String stringDeptCdDoc, String stringSqlAnd, Hashtable hashtableDeptCdDoc) throws Throwable {
    String stringDeptCdDocL = ("" + hashtableDeptCdDoc.get(stringDeptCd)).trim();
    if (!"null".equals(stringDeptCdDocL)) {
      return stringDeptCdDocL;
    }
    //
    String[][] retDoc2M010 = getDoc2M010DeptCd(stringDeptCd, stringDeptCdDoc, stringSqlAnd);
    //
    if (retDoc2M010.length > 0) {
      stringDeptCdDocL = retDoc2M010[0][1].trim();
      hashtableDeptCdDoc.put(stringDeptCd, stringDeptCdDocL);
      return stringDeptCdDocL;
    }
    //
    hashtableDeptCdDoc.put(stringDeptCd, "");
    return "";
  }

  // 由某一組請購單，取得所有有[前後期關係]的請購單號
  public Vector getAllPurchaseNo(String stringComNo, String stringFactoryNo, Vector vectorPurchaseNo, String stringBarCode, String stringSqlAnd, String stringKindNo,
      boolean booleanSource, talk dbDoc) throws Throwable {
    String stringSql = "";
    String stringPurchaseNoSql = getPurchaseNoSql(vectorPurchaseNo);
    String stringPurchaseNo = "";
    String stringTable17 = booleanSource ? "Doc2M017" : "Doc5M027";
    String stringTable10 = booleanSource ? "Doc2M010" : "Doc5M020";
    String stringTable60 = booleanSource ? "Doc6M010" : "Doc5M030";
    String[][] retDoc2M017 = null;
    String[][] retDoc6M010 = null;
    Vector vectorPurchaseNoAll = new Vector();
    if (vectorPurchaseNo.size() == 0) return vectorPurchaseNoAll;
    // 請款
    boolean booleanFlag = true;
    for (int intNo = 0; intNo < 100; intNo++) {
      stringSql = "SELECT  M17.PurchaseNo " + " FROM  " + stringTable17 + " M17,  " + stringTable10 + " M10 " + " WHERE  M17.BarCode  =  M10.BarCode " + " AND  M10.ComNo  =  '"
          + stringComNo + "' " + " AND  M10.KindNo  =  '" + stringKindNo + "' " + " AND  M17.FactoryNo  =  '" + stringFactoryNo + "' " +
          // " AND PurchaseNo NOT IN ("+stringPurchaseNoSql+") " +
          " AND  M10.BarCode  IN  (SELECT  M20.BarCode " + " FROM  " + stringTable17 + " M27,  " + stringTable10 + " M20 " + " WHERE  M27.BarCode  =  M20.BarCode "
          + " AND  M20.ComNo  =  '" + stringComNo + "' " + " AND  M27.FactoryNo  =  '" + stringFactoryNo + "' " + " AND  M27.PurchaseNo  IN (" + stringPurchaseNoSql + ")) "
          + stringSqlAnd;
      if (!"".equals(stringBarCode)) stringSql += " AND  M10.BarCode  <>  '" + stringBarCode + "' ";
      retDoc2M017 = dbDoc.queryFromPool(stringSql);
      booleanFlag = false;
      for (int intNoL = 0; intNoL < retDoc2M017.length; intNoL++) {
        stringPurchaseNo = retDoc2M017[intNoL][0].trim();
        //
        if (vectorPurchaseNoAll.indexOf(stringPurchaseNo) == -1) {
          vectorPurchaseNoAll.add(stringPurchaseNo);
          booleanFlag = true;
        }
      }
      if (!booleanFlag) {
        break;
      }
      stringPurchaseNoSql = getPurchaseNoSql(vectorPurchaseNoAll);
    }
    //
    return vectorPurchaseNoAll;
  }

  public String getPurchaseNoSql(Vector vectorPurchaseNo) throws Throwable {
    String stringSqlAnd = "";
    String stringPurchaseNo = "";
    for (int intNo = 0; intNo < vectorPurchaseNo.size(); intNo++) {
      stringPurchaseNo = ("" + vectorPurchaseNo.get(intNo)).trim();
      if ("null".equals(stringPurchaseNo)) continue;
      if ("".equals(stringPurchaseNo)) continue;
      if (!"".equals(stringSqlAnd)) stringSqlAnd += ", ";
      stringSqlAnd += "'" + stringPurchaseNo + "'";
    }
    return stringSqlAnd;
  }

  public String getLastDocNo(String stringComNo, String stringFactoryNo, Vector vectorPurchaseNo, String stringBarCode, String stringSqlAnd, String stringKindNo,
      boolean booleanSource, FargloryUtil exeUtil, talk dbDoc) throws Throwable {
    Vector vectorAllPurchaseNo = getAllPurchaseNo(stringComNo, stringFactoryNo, vectorPurchaseNo, "", stringSqlAnd, stringKindNo, booleanSource, dbDoc);
    Vector vectorEDateTime = new Vector();
    Hashtable hashtableDocNo = new Hashtable();
    Hashtable hashtableBarCode = new Hashtable();
    //
    doGetPurchaseNoOrderByEDateTime(hashtableDocNo, hashtableBarCode, vectorEDateTime, stringComNo, stringFactoryNo, vectorAllPurchaseNo, "", stringKindNo, booleanSource);
    //
    int intPos = 0;
    String stringEDateTime = "";
    String stringDocNo = "";
    String stringBarCodeCF = "";
    String[] arrayEDateTime = (String[]) vectorEDateTime.toArray(new String[0]);
    String[] arrayDocNo = null;
    Vector vectorDocNo = null;
    Vector vectorBarCode = null;
    boolean booleanFlag = true;
    Arrays.sort(arrayEDateTime);
    for (int intNo = 0; intNo < arrayEDateTime.length; intNo++) {
      stringEDateTime = arrayEDateTime[intNo].trim();
      vectorDocNo = (Vector) hashtableDocNo.get(stringEDateTime);
      vectorBarCode = (Vector) hashtableBarCode.get(stringEDateTime);
      if (vectorBarCode == null) {
        doEMail("條碼編號(" + stringBarCode + ")之請款單列印發生錯誤，vectorBarCode 為 null", exeUtil);
        return "";
      }
      if (vectorDocNo == null) {
        doEMail("條碼編號(" + stringBarCode + ")之請款單列印發生錯誤，vectorDocNo 為 null", exeUtil);
        return "";
      }
      if (vectorDocNo.size() != vectorBarCode.size()) {
        doEMail("條碼編號(" + stringBarCode + ")之請款單列印發生錯誤，vectorDocNo(" + vectorDocNo.size() + ") 與 vectorBarCode(" + vectorBarCode.size() + ") 數量不一致", exeUtil);
        return "";
      }
      if (vectorDocNo.size() == 0) {
        doEMail("條碼編號(" + stringBarCode + ")之請款單列印發生錯誤，vectorDocNo 數量為 0", exeUtil);
        return "";
      }
      if (vectorBarCode.size() == 0) {
        doEMail("條碼編號(" + stringBarCode + ")之請款單列印發生錯誤，vectorBarCode 數量為 0", exeUtil);
        return "";
      }
      booleanFlag = false;
      for (int intNoL = 0; intNoL < vectorBarCode.size(); intNoL++) {
        stringBarCodeCF = ("" + vectorBarCode.get(intNoL)).trim();
        if ("null".equals(stringBarCodeCF)) {
          doEMail("條碼編號(" + stringBarCode + ")之請款單列印發生錯誤，vectorBarCode之第 " + (intNoL + 1) + " 為 null", exeUtil);
          return "";
        }
        if (stringBarCode.equals(stringBarCodeCF)) {
          booleanFlag = true;
          break;
        }
      }
      if (booleanFlag) break;
      //
      arrayDocNo = (String[]) vectorDocNo.toArray(new String[0]);
      Arrays.sort(arrayDocNo);
      stringDocNo = arrayDocNo[0].trim();
    }
    return stringDocNo;
  }

  public void doEMail(String stringContent, FargloryUtil exeUtil) throws Throwable {
    String stringSend = "B3018@farglory.com.tw";
    String[] arrayUser = { stringSend };
    exeUtil.doEMail("請款單列印錯誤", stringContent, stringSend, arrayUser);
  }

  // [前後期關係]的請款單號之請款順序
  public void doGetPurchaseNoOrderByEDateTime(Hashtable hashtableDocNo, Hashtable hashtableBarCode, Vector vectorEDateTime, String stringComNo, String stringFactoryNo,
      Vector vectorPurchaseNo, String stringSqlAnd, String stringKindNo, boolean booleanSource) throws Throwable {
    String stringSql = "";
    String stringPurchaseNoSql = getPurchaseNoSql(vectorPurchaseNo);
    String stringDocNo = "";
    String stringTable17 = booleanSource ? "Doc2M017" : "Doc5M027";
    String stringTable10 = booleanSource ? "Doc2M010" : "Doc5M020";
    String stringTable60 = booleanSource ? "Doc6M010" : "Doc5M030";
    String stringEDateTime = "";
    String stringBarCode = "";
    String[][] retDoc2M017 = null;
    String[][] retDoc6M010 = null;
    Vector vectorTemp = new Vector();
    Vector vectorBarCode = new Vector();
    // 請款
    stringSql = "SELECT  M10.EDateTime,  M10.DocNo, M10.BarCode " + " FROM  " + stringTable17 + " M17,  " + stringTable10 + " M10 " + " WHERE  M17.BarCode  =  M10.BarCode "
        + " AND  M10.ComNo  =  '" + stringComNo + "' " + " AND  M10.KindNo  =  '" + stringKindNo + "' " + " AND  M17.FactoryNo  =  '" + stringFactoryNo + "' "
        + " AND  M17.PurchaseNo  IN (" + stringPurchaseNoSql + ") " + stringSqlAnd + " ORDER BY  M10.EDateTime,  M10.DocNo, M10.BarCode ";
    retDoc2M017 = dbDoc.queryFromPool(stringSql);
    for (int intNo = 0; intNo < retDoc2M017.length; intNo++) {
      stringEDateTime = retDoc2M017[intNo][0].trim();
      stringDocNo = retDoc2M017[intNo][1].trim();
      stringBarCode = retDoc2M017[intNo][2].trim();
      System.out.println("stringBarCode(" + stringBarCode + ")--------------------");
      //
      if (vectorEDateTime.indexOf(stringEDateTime) == -1) vectorEDateTime.add(stringEDateTime);
      //
      vectorTemp = (Vector) hashtableDocNo.get(stringEDateTime);
      vectorBarCode = (Vector) hashtableBarCode.get(stringEDateTime);
      if (vectorTemp == null) {
        vectorTemp = new Vector();
        vectorBarCode = new Vector();
        hashtableDocNo.put(stringEDateTime, vectorTemp);
        hashtableBarCode.put(stringEDateTime, vectorBarCode);
      }
      vectorTemp.add(stringDocNo);
      vectorBarCode.add(stringBarCode);
    }
    // 借款沖銷
    stringSql = "SELECT  M10.EDateTime,  M10.DocNo,  M10.BarCode " + " FROM  " + stringTable60 + " M10 " + " WHERE  1=1 " + " AND  M10.ComNo  =  '" + stringComNo + "' "
        + " AND  M10.KindNo  =  '" + stringKindNo + "' " + " AND  M10.FactoryNo  =  '" + stringFactoryNo + "' " + " AND  M10.PurchaseNo  IN (" + stringPurchaseNoSql + ") "
        + stringSqlAnd + " ORDER BY  M10.EDateTime,  M10.DocNo, M10.BarCode ";
    retDoc6M010 = dbDoc.queryFromPool(stringSql);
    for (int intNo = 0; intNo < retDoc6M010.length; intNo++) {
      stringEDateTime = retDoc2M017[intNo][0].trim();
      stringDocNo = retDoc6M010[intNo][1].trim();
      stringBarCode = retDoc2M017[intNo][2].trim();
      //
      if (vectorEDateTime.indexOf(stringEDateTime) == -1) vectorEDateTime.add(stringEDateTime);
      //
      vectorTemp = (Vector) hashtableDocNo.get(stringEDateTime);
      vectorBarCode = (Vector) hashtableBarCode.get(stringEDateTime);
      if (vectorTemp == null) {
        vectorTemp = new Vector();
        vectorBarCode = new Vector();
        hashtableDocNo.put(stringEDateTime, vectorTemp);
        hashtableBarCode.put(stringEDateTime, vectorBarCode);
      }
      vectorTemp.add(stringDocNo);
      vectorBarCode.add(stringBarCode);
    }
  }

  public String getCheapenMoney(String stringBarCode, String stringFunction, String stringPrevFunction, FargloryUtil exeUtil, String stringCheapenMoney) throws Throwable {
    boolean booleanSource = stringPrevFunction.indexOf("Doc5M") == -1;
    String stringSql = "SELECT  SUM(ISNULL(ReceiptTax,0) + ISNULL(SupplementMoney,0))  FROM  Doc5M023  WHERE  BarCode  =  '" + stringBarCode + "' ";
    String[][] retData = new String[0][0];
    //
    if ("A".equals(stringFunction)) {
      // 請款
      if (booleanSource) {
        // 行銷
        retData = dbDoc.queryFromPool(stringSql.replaceAll("Doc5M023", "Doc2M013"));
      } else {
        // 管理
        retData = dbDoc.queryFromPool(stringSql);
      }
    } else if ("B".equals(stringFunction)) {
      // 借款沖銷
      retData = dbDoc.queryFromPool(stringSql.replaceAll("Doc5M023", "Doc6M013"));
    } else {
      // 借款

    }
    if (retData.length == 0) return stringCheapenMoney;
    //
    stringCheapenMoney = convert.FourToFive("" + exeUtil.doParseDouble(retData[0][0]), 0);
    //
    return stringCheapenMoney;
  }

  public String getInformation() {
    return "---------------新增按鈕程式.preProcess()----------------";
  }
}
