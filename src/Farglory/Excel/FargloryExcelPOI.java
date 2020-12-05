package Farglory.Excel ;
import  java.io.*;//
import  java.util.*;
import  com.jacob.activeX.*;
import  com.jacob.com.*;
import  jcx.util.*;
import  org.apache.poi.hssf.usermodel.*;
import  org.apache.poi.poifs.filesystem.*;
public  class  FargloryExcelPOI {
	// 跳頁設定
	int          intStartDataRowGlobal         =  6 ;                // 開始Excel 列數
	int          intPageDataRowGlobal        =  20 ;               // 結束Excel 列數
	int          intPageAllRowGlobal            =  28 ;               // 一頁之列數
	int          intPageNoGlobal                   =  1 ;                // 初始頁數
	int          intStartClearColG           =  0 ;                // 開始清除欄位(預設 'A')
	int          intEndClearColG             =  17 ;                // 結束清除欄位(預設 'R')
	boolean      booleanFlag                 =  true ;             // 預覽：true 表示要預覽。
	boolean      booleanFirstSheetFlagG      =  true ;             // 設定雙 Sheet 之Action，預設第二個
	boolean      booleanVisibleG             =  true ;             // 設定要不要顯示 Excel 給使用者看
	String       stringFilePath              =  "C:\\temp.xls" ;   // 不預覽時，非空字串時，另存新檔
	HSSFCellStyle  cellStyleG  =  null ;								// 預設字型
	// 建構子
	public FargloryExcelPOI(){	}
/*	public FargloryExcelPOI(int  intStartDataRow,  int  intPageDataRow,  int  intPageAllRow,  int  intPageNo){
       intStartDataRowGlobal  =  intStartDataRow ;
       intPageDataRowGlobal   =  intPageDataRow ;
       intPageAllRowGlobal    =  intPageAllRow ;
       intPageNoGlobal        =  intPageNo ;
	}*/
	//
	public void setVisibleProperty(boolean  booleanVisible) throws Exception {
	    booleanVisibleG  =  booleanVisible ;
	}
	//
/*	public void setClearCol(int  intStartClearCol,  int  intEndClearCol) throws Exception {
	    intStartClearColG  =  intStartClearCol ;
	    intEndClearColG    =  intEndClearCol ;
	}*/
	//
	public void setPreView(boolean  booleanFlagValue,  String  stringPathValue) throws Exception {
	    booleanVisibleG  =  booleanFlagValue ;
	    stringFilePath   =  stringPathValue ;
	}
	//
/*	public int getStartDataRow() throws Exception {
	    return  (intStartDataRowGlobal-1) ;
	}
	public int getPageAllRow() throws Exception {
	    return  intPageAllRowGlobal ;
	}
	public int getPageNo() throws Exception {
	    return  intPageNoGlobal ;
	}
	public int getPageDataRow() throws Exception {    
	    return  intPageDataRowGlobal ;
	}
	public int doAdd1PageNo() throws Exception {     
	    intPageNoGlobal  =  intPageNoGlobal  +  1 ;
	    return  intPageNoGlobal ;
	}*/
	// 建立 Excel 元件

	public HSSFWorkbook getExcelObject(String stringExcelName){
		Vector        retVector  =  new  Vector() ;
		HSSFWorkbook  workBook  =  null ;
		//
		try {
				FileInputStream   fis             =  new  FileInputStream(stringExcelName);
				POIFSFileSystem   fs              =  new  POIFSFileSystem( fis );
				                              workBook  =  new  HSSFWorkbook( fs );   
				//
				setDefaultCellStyle(HSSFFont.COLOR_NORMAL,  HSSFFont.BOLDWEIGHT_NORMAL,  HSSFCellStyle.ALIGN_LEFT,  HSSFCellStyle.BORDER_THIN,  workBook) ;

	  } catch(Exception e) {}
		return  workBook ;
	}
	// 顯示並釋放 Excel 物件
	public void getReleaseExcelObject(HSSFWorkbook  workBook){
     try {
          FileOutputStream  fout  =  new  FileOutputStream(stringFilePath);
          workBook.write( fout );
          fout.close();
					//顯示 Excel
			    if(booleanVisibleG){
								Farglory.Excel.FargloryExcel  exeFun        =  new  Farglory.Excel.FargloryExcel() ;
								Vector                        retVector     =  exeFun.getExcelObject(stringFilePath) ;
								Dispatch                      objectSheet1  =  (Dispatch)retVector.get(1) ;
								Dispatch                      objectClick   =  null ;
								// 釋放 Excel 物件
		  					exeFun.getReleaseExcelObject(retVector) ;
			    }
	    } catch(Exception e) {}
	    workBook  =  null ;
	}
	// 應用
	//Copy Sheet2 Template to Sheet1
/*	public void CopyPage(Dispatch objectSheet1,  Dispatch objectSheet2){
		Dispatch.call(objectSheet2,  "Activate");										 
		Dispatch objectA1  =  Dispatch.invoke(objectSheet2,  "Range", Dispatch.Get,  new Object[] {"A1"},  new int[1]).toDispatch();
		Dispatch.call(objectA1,  "Select");
		Dispatch  objectRow  =  Dispatch.invoke(objectSheet2,  "Rows",  Dispatch.Get,  new Object[] {"1:" + intPageAllRowGlobal},  new int[1]).toDispatch();	
		Dispatch.call(objectRow,  "Copy");
		//Sheet1
		Dispatch.call(objectSheet1,  "Activate");
		objectA1  =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,  new Object[] {"A" + ((intPageNoGlobal - 1) * intPageAllRowGlobal + 1)},  new int[1]).toDispatch();
		Dispatch.call(objectA1,  "Select");
		Dispatch.call(objectSheet1,  "Paste");			
	}
	// 滿頁時，換頁
	public int doChangePage(int  intRecordNo,  Dispatch  objectSheet1,  Dispatch  objectSheet2){
		if(intRecordNo  >=  (intPageDataRowGlobal + intStartDataRowGlobal - 1)){
			CopyPage(objectSheet1,  objectSheet2);
			String  stringStartClearCol  =  getExcelColumnName( "A",  intStartClearColG) ;
			String  stringEndClearColG   =  getExcelColumnName( "A",  intEndClearColG) ;
			//System.out.println("清除"+intStartClearColG+"--"+intEndClearColG+"--------------"+(""+stringStartClearCol + intStartDataRowGlobal + ":" +stringEndClearColG + (intStartDataRowGlobal + intPageDataRowGlobal -1))) ;
			doClearContents(stringStartClearCol + intStartDataRowGlobal + ":" +stringEndClearColG + (intStartDataRowGlobal + intPageDataRowGlobal -1),  objectSheet2) ;
			intRecordNo   =   intStartDataRowGlobal - 1 ;
			intPageNoGlobal++;
		}
		return  intRecordNo ;
	}*/
	// shortColor
	// HSSFFont之COLOR_RED、COLOR_NORMAL
	// shortFont
	//HSSFFont 之 BOLDWEIGHT_BOLD、BOLDWEIGHT_NORMAL
	// shortAlign
	// HSSFCellStyle 之 ALIGN_CENTER、ALIGN_LEFT    、ALIGN_GENERAL、ALIGN_RIGHT、
	//                             ALIGN_FILL、       ALIGN_JUSTIFY、ALIGN_CENTER_SELECTION
	// shortBorder
	// HSSFCellStyle 之 BORDER_NONE、BORDER_DOTTED、BORDER_THIN、BORDER_MEDIUM、BORDER_DASHED、BORDER_DOUBLE
	public void setDefaultCellStyle(short  shortColor,  short  shortFont,  short  shortAlign,  short  shortBorder,  HSSFWorkbook  workbook){	
				// 1、創建字體，設置其為紅色、粗體： 
				HSSFFont  font  =  workbook.createFont( )  ; 
				font.setColor(shortColor) ; 
				font.setBoldweight(shortFont) ; 
				// 2、創建格式 
				HSSFCellStyle  cellStyle  =  workbook.createCellStyle( ) ; 
				cellStyle.setFont(font) ; 
				cellStyle.setAlignment(shortAlign) ;
				// 邊線
        cellStyle.setBorderBottom(shortBorder); //下邊框    
        cellStyle.setBorderLeft(shortBorder);   //左邊框    
        cellStyle.setBorderRight(shortBorder);  //右邊框    
        cellStyle.setBorderTop(shortBorder);    //上邊框    
        //
        cellStyleG  =  cellStyle ;
	}
	public HSSFCellStyle setCellStyle(short  shortColor,  short  shortFont,  short  shortAlign,  short  shortBorder,  HSSFWorkbook  workbook){	
				// 1、創建字體，設置其為紅色、粗體： 
				HSSFFont  font  =  workbook.createFont( )  ; 
				font.setColor(shortColor) ; 
				font.setBoldweight(shortFont) ; 
				// 2、創建格式 
				HSSFCellStyle  cellStyle  =  workbook.createCellStyle( ) ; 
				cellStyle.setFont(font) ; 
				cellStyle.setAlignment(shortAlign) ;
				// 邊線
        cellStyle.setBorderBottom(shortBorder); //下邊框    
        cellStyle.setBorderLeft(shortBorder);   //左邊框    
        cellStyle.setBorderRight(shortBorder);  //右邊框    
        cellStyle.setBorderTop(shortBorder);    //上邊框    
        //
        return  cellStyle ;
	}
	public void setDefaultBorder(short  shortBorder){	
				// 邊線
        cellStyleG.setBorderBottom(shortBorder); //下邊框    
        cellStyleG.setBorderLeft(shortBorder);   //左邊框    
        cellStyleG.setBorderRight(shortBorder);  //右邊框    
        cellStyleG.setBorderTop(shortBorder);    //上邊框    
	}
	public void setDefaultCellStyle(short  shortAlign,  HSSFCellStyle  cellStyle){	
				cellStyle.setAlignment(shortAlign) ;
				cellStyleG  =  cellStyle ;
	}
	public void setDefaultCellStyle(HSSFCellStyle  cellStyle){	
				cellStyleG  =  cellStyle ;
	}
	// intPosColumn  			Excel 的位置(行)
	// intPosRow   			    Excel 的位置(列)
	//  objectSheet2   			Excel 的工作表
	public void putDataIntoExcel(int  intPosColumn,  int  intPosRow,  String  stringPutData, HSSFSheet  sheet){	
				HSSFCell         cell            =  null ;
				HSSFRow        row            =  null ;
				try {
						// 放值
						row   =  sheet.createRow(intPosRow) ;
						cell  =  row.createCell((short)  intPosColumn) ;
						//
						cell.setEncoding( HSSFCell.ENCODING_UTF_16 );
						cell.setCellType(HSSFCell.CELL_TYPE_STRING); 
						if(cellStyleG  !=  null)			cell.setCellStyle(cellStyleG); 
						//
						cell.setCellValue((String)   stringPutData );
				} catch (Exception  e) {
						System.out.println("---------------------"+e.toString()) ;
				}
	}
	// 隱藏第一列之前十個作為格式設定
	public void putDataIntoExcel(int  intPosColumn,  int  intPosRow,  String  stringPutData, String  stringField,  HSSFSheet  sheet){	
				try {
						// 取得型態
						HSSFCellStyle cellStyle     =  null ;
						HSSFCell         cell            =  null ;
						HSSFRow        row            =  null ;
						if(!"".equals(stringField)  &&  stringField.length()  ==  1) {
								int                   intFieldPos  =  (int)stringField.charAt(0)  -  65 ;
								if(intFieldPos  >=  0  &&  intFieldPos  <=  9)   {
										 row              =  sheet.getRow(0) ;
										 cell              =  row.getCell((short) intFieldPos) ;
										 cellStyle      =  cell.getCellStyle(); 
								} else {
										cellStyle  =  cellStyleG ;
								}
						} else {
								cellStyle  =  cellStyleG ;
								System.out.println("---------------------Default") ;
						}
						// 放值
						row   =  sheet.createRow(intPosRow) ;
						cell  =  row.createCell((short)  intPosColumn) ;
						//
						cell.setEncoding( HSSFCell.ENCODING_UTF_16 );
						cell.setCellType(HSSFCell.CELL_TYPE_STRING); 
						if(cellStyle  !=  null)			cell.setCellStyle(cellStyle); 
						//
						cell.setCellValue((String)   stringPutData );
				} catch (Exception  e) {
						System.out.println("---------------------"+e.toString()) ;
				}
	}
	public void putDataIntoExcel(int  intPosColumn,  int  intPosRow,  String  stringPutData, HSSFCellStyle cellStyle,  HSSFSheet  sheet){	
				try {
						// 取得型態
						HSSFCell         cell            =  null ;
						HSSFRow        row            =  null ;
						// 放值
						row   =  sheet.createRow(intPosRow) ;
						cell  =  row.createCell((short)  intPosColumn) ;
						//
						cell.setEncoding( HSSFCell.ENCODING_UTF_16 );
						cell.setCellType(HSSFCell.CELL_TYPE_STRING); 
						if(cellStyle  !=  null)			cell.setCellStyle(cellStyle); 
						//
						cell.setCellValue((String)   stringPutData );
				} catch (Exception  e) {
						System.out.println("---------------------"+e.toString()) ;
				}
	}
	 // intPosColumn  				Excel 的位置(行)
	 // intPosRow   				Excel 的位置(列)
	 //  objectSheet2   			Excel 的工作表
/*	public void putTypeIntoExcel(int  intPosColumn,  int  intPosRow,  String  stringPutType, Dispatch  objectSheet){	
		String  stringTmp  =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch.put(Dispatch.invoke(objectSheet,  "Range",  Dispatch.Get,  new Object[] {stringTmp+ (intPosRow+1)},  new int[1]).toDispatch(),
					"NumberFormatLocal",
					stringPutType
					);
	}*/
	// intPosColumn  			Excel 的位置(行)
	// intPosRow   			    Excel 的位置(列)
	//  objectSheet2   			Excel 的工作表
	public String  getDataFromExcel(int  intPosColumn,  int  intPosRow,  HSSFSheet  sheet){	
			String  stringReturn  =  "" ;
			try {
					HSSFRow    row           =  sheet.getRow(intPosRow) ;
					HSSFCell   cell          =  row.getCell((short)  intPosColumn) ;
					           stringReturn  =  cell.getStringCellValue() ;
			} catch (Exception  e) {
					System.out.println("ERROR------------------"+e.toString());
					return  stringReturn ;		
			}
			if("null".equals(stringReturn))  stringReturn  =  "" ;
			return  stringReturn ;
	}
	// 清除欄位，欄位之公式亦會被清空
/*	public void  doClearContents(String  stringRange,  Dispatch  objectSheet){	
		Dispatch objectRangeClear  =  Dispatch.invoke(objectSheet,  "Range",   Dispatch.Get,  new Object[] {stringRange},  new int[1]).toDispatch();
		Dispatch.call(objectRangeClear,  "ClearContents");
	}
	// 複製一整個欄
	public void  doCopyColumns(int  intPosColumn,  Dispatch  objectSheet){	
		String  stringTmp  =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmp  +  ":"  +  stringTmp}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange, "Copy");
		Dispatch.call(objectRange, "Insert");
	}
	// 複製某區域欄
	public void  doCopyColumns(int  intPosColumnS,  int  intPosColumnE,  Dispatch  objectSheet){	
		String  stringTmpS  =  getExcelColumnName( "A",  intPosColumnS) ;
		String  stringTmpE  =  getExcelColumnName( "A",  intPosColumnE) ;
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmpS  +  ":"  +  stringTmpE}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange, "Copy");
		Dispatch.call(objectRange, "Insert");
	}
	// 複製一整個列
	public void  doCopyRow(int  intPosRow,  Dispatch  objectSheet){	
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intPosRow  +  ":"  + intPosRow}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange, "Copy");
		Dispatch.call(objectRange, "Insert");
	}
	// 刪除一整欄
	public void  doDeleteColumns(int  intPosColumn,  Dispatch  objectSheet){	
		String    stringTmp    =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmp  +  ":"  +  stringTmp}, new int[1]).toDispatch( ) ;
		//Dispatch.put(objectRange, "Delete",  new  varant(-4159));
		Dispatch.call(objectRange, "Delete");
	}
	// 刪除一整列
	public void  doDeleteRows(int  intPosRowS,  int  intPosRowE,  Dispatch  objectSheet){	
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intPosRowS  +  ":"  +  intPosRowE}, new int[1]).toDispatch( ) ;
		//Dispatch.put(objectRange, "Delete",  new  varant(-4159));
		Dispatch.call(objectRange, "Delete");
	}
	// 變更欄寬
	public void  doChangeColumnsSize(int  intPosColumn,  int  intColumnSize,  Dispatch  objectSheet){	
		String  stringTmp  =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmp  +  ":"  +  stringTmp}, new int[1]).toDispatch( ) ;
		Dispatch.put(objectRange, "ColumnWidth", new Variant(intColumnSize));
	}
	// 畫框線
	public void  doLineStyle(String  stringRange,  Dispatch  objectSheet1){	
		Dispatch objectRange    =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
		Dispatch objectBorders  =  null ;
		//
		Dispatch.call(objectRange, "Select");
		// 左
		objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"7"},  new int[1]).toDispatch();
		Dispatch.put(objectBorders,  "LineStyle",  "1") ;
		//上
		objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"8"},  new int[1]).toDispatch();
		Dispatch.put(objectBorders,  "LineStyle",  "1") ;
		//下
		objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"9"},  new int[1]).toDispatch();
		Dispatch.put(objectBorders,  "LineStyle",  "1") ;
		//右
		objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"10"},  new int[1]).toDispatch();
		Dispatch.put(objectBorders,  "LineStyle",  "1") ;
		// 中
		objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"11"},  new int[1]).toDispatch();
		Dispatch.put(objectBorders,  "LineStyle",  "1") ;
	}
	// 頁首
	// 範例：setPageTitleName(objectSheet1,  "&14公司名稱\n電算發票\n94年11月25日申報") ;
	public  void  setPageTitleName(Dispatch  objectSheet1,  String stringPageTitleName){
		objectSheet1  =  Dispatch.get(objectSheet1,  "PageSetup").toDispatch( );
		Dispatch.put(objectSheet1,  "CenterHeader",  stringPageTitleName) ;
	}
	// 取得已開啟之 sheet 之 最大列數
	public  int  getExcelMaxRow(Dispatch  dispatchSheet1){
    Dispatch  dispatchUsedRange =  Dispatch.get(dispatchSheet1, "UsedRange").toDispatch();
		String[]  retArray          =  convert.StringToken(Dispatch.call(dispatchUsedRange, "Address").toString( ),  "$") ;
		int       intRowMax         =  doParseInteger(retArray[4].trim( )) ;
		int       intColMax         =  getExcelColumnNo(retArray[3].trim( )) ;   
		return  intRowMax ;
	}
	// 取得已開啟之 sheet 之 最大欄位數
	public  int  getExcelMaxCol(Dispatch  dispatchSheet1){
    	Dispatch  dispatchUsedRange =  Dispatch.get(dispatchSheet1, "UsedRange").toDispatch();
		String[]  retArray          =  convert.StringToken(Dispatch.call(dispatchUsedRange, "Address").toString( ),  "$") ;
		int       intColMax         =  getExcelColumnNo(retArray[3].trim( )) ;   
		return  intColMax ;
	}*/
}
