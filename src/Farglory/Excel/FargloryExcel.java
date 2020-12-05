package Farglory.Excel ;
import  java.util.*;
import  com.jacob.activeX.*;
import  com.jacob.com.*;
import  jcx.util.*;
import  jcx.jform.bproc;
public  class  FargloryExcel {
	// 跳頁設定
	int          intStartDataRowGlobal       =  6 ;                // 開始Excel 列數(Sheet 顯示，非由0 開始)
	int          intPageDataRowGlobal        =  20 ;               // 結束Excel 列數(實際放值行數)
	int          intPageAllRowGlobal         =  28 ;               // 一頁之列數
	int          intPageNoGlobal             =  1 ;                // 初始頁數
	int          intStartClearColG           =  0 ;                // 開始清除欄位(預設 'A')
	int          intEndClearColG             =  17 ;               // 結束清除欄位(預設 'R')
	boolean      booleanFlag                 =  true ;             // 預覽：true 表示要預覽。
	boolean      booleanType                 =  true ;             // 
	boolean      booleanFirstSheetFlagG      =  true ;             // 設定雙 Sheet 之Action，預設第二個
	boolean      booleanVisibleG             =  true ;             // 設定要不要顯示 Excel 給使用者看
	boolean      booleanSheetEndViewG        =  true ;             // 預設 true 最後顯示第一個 Sheet
	boolean      booleanStopG                =  false ;            // 設定 true 表示停止最後一個動作(Quit 或 View)
	String       stringFilePath              =  "" ;               // 不預覽時，非空字串時，另存新檔
	String       stringExcelFileNameGlobal   =  "" ;
	
	// 建構子
	public FargloryExcel(){
		booleanType  =  true ;    
	}
	
	public FargloryExcel(String  stringExcelFileName){
		stringExcelFileNameGlobal  =  stringExcelFileName ;
		booleanType  =  true ;
	}
	
	public FargloryExcel(int  intStartDataRow,  int  intPageDataRow,  int  intPageAllRow,  int  intPageNo){
		intStartDataRowGlobal  =  intStartDataRow ;
		intPageDataRowGlobal   =  intPageDataRow ;
		intPageAllRowGlobal    =  intPageAllRow ;
		intPageNoGlobal        =  intPageNo ;
		booleanType            =  false ;
	}
	
	// 控制顯不顯示 Excel
	public void setVisibleProperty(boolean  booleanVisible) throws Exception {
		booleanVisibleG  =  booleanVisible ;
	}
	
	public void setSheetEndView(boolean  booleanSheetEndView) throws Exception {
		booleanSheetEndViewG  =  booleanSheetEndView ;
	}
	
	//
	public void setClearCol(int  intStartClearCol,  int  intEndClearCol) throws Exception {
		intStartClearColG  =  intStartClearCol ;
		intEndClearColG    =  intEndClearCol ;
	}
	
	// 當不預覽時，且有傳入路徑時，另存新檔。
	public void setPreView(boolean  booleanFlagValue,  String  stringPathValue) throws Exception {
		booleanFlag     =  booleanFlagValue ;
		stringFilePath  =  stringPathValue ;
	}
	
	// 
	public void doStopAction(boolean  booleanFlagValue) throws Exception {
		booleanStopG  =  booleanFlagValue ;
	}
	
	// 僅對雙 Sheet 有用
	public void setFirstSheet(boolean  booleanFirstSheetFlag) throws Exception {
		booleanFirstSheetFlagG  =  booleanFirstSheetFlag ;
	}
	
	public int getStartDataRow() throws Exception {
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
	}
	
	public void setVisiblePropertyOnFlow(boolean  booleanVisible,  Vector  retVector) throws Exception {
		booleanVisibleG         =  booleanVisible ;
		ActiveXComponent Excel  =  (ActiveXComponent)retVector.get(0);
		Excel.setProperty("Visible",  new Variant(booleanVisible));
	}
	
	// 設定頁數 970131 B3774
	public void setPageNo(int intPageNo) throws Exception {
		intPageNoGlobal  =  intPageNo ;
	}
	
	// 建立 Excel 元件
	public Vector getExcelObject(String stringExcelName){
		Vector  retVector  =  new  Vector( ) ;
		// 建立com元件
		ActiveXComponent Excel;
		ComThread.InitSTA();
		Excel  =  ExcelVerson();
		Excel.setProperty("Visible",  new Variant(booleanVisibleG));
//		Excel.setProperty("Visible",  new Variant(false));
		Excel.setProperty("DisplayAlerts", new Variant(false));	
		Object    objectExcel        =  Excel.getObject();
		Dispatch    objectWorkbooks  =  Excel.getProperty("Workbooks").toDispatch();
		Dispatch    objectWorkbook   = null ;
		objectWorkbook  =  Dispatch.call(objectWorkbooks, "Open", stringExcelName,  new  Variant(false)).toDispatch();
		Dispatch    objectSheets  =  Dispatch.get(objectWorkbook,  "Sheets").toDispatch();
		Dispatch    objectSheet1  =  null ;
		Dispatch    objectSheet2  =  null ;
		if("".equals(stringExcelFileNameGlobal)) {
			if(!booleanType){
    				objectSheet1  =  Dispatch.call(objectSheets,  "Item",  new  Variant(1)).toDispatch() ;
    				objectSheet2  =  Dispatch.call(objectSheets,  "Item",  new  Variant(2)).toDispatch() ;  
    				if(booleanFirstSheetFlagG){
    					Dispatch.call(objectSheet2,  "Activate");
    				} else {
    					Dispatch.call(objectSheet1,  "Activate");
    				} 
			}else {
				objectSheet1  =  Dispatch.call(objectSheets,  "Item",  new  Variant(1)).toDispatch() ;  
				Dispatch.call(objectSheet1,  "Activate");
			}
		} else {
			objectSheet1  =  Dispatch.call(objectSheets,  "Item",  stringExcelFileNameGlobal).toDispatch() ;
			Dispatch.call(objectSheet1,  "Activate");
		}
		// 回傳資料
		retVector.add(Excel) ;
		retVector.add(objectSheet1) ;
		retVector.add(objectSheet2) ;
		retVector.add(objectSheets) ;
		retVector.add(objectWorkbook) ;
		Excel.setProperty("Visible",  new Variant(booleanVisibleG));		
		Excel.setProperty("DisplayAlerts", new Variant(false));			
		return  retVector ;
	}
	// 顯示並釋放 Excel 物件
	public void getReleaseExcelObject(Vector  vectorExcelObject){
		// 資料處理
		ActiveXComponent  Excel          =  (ActiveXComponent)  vectorExcelObject.get(0) ;
		Dispatch          objectSheet1   =  (Dispatch)vectorExcelObject.get(1) ;//
		Dispatch          objectSheet2   =  (Dispatch)vectorExcelObject.get(2) ;//
		Dispatch          objectSheets   =  (Dispatch)vectorExcelObject.get(4) ;//
		Dispatch          objectA1       =   null ;//
		//
		if(!"".equals(stringFilePath)) {
			Dispatch.call(objectSheets,  "SaveAs",  stringFilePath);//
		}
		// 預覽             true booleanFlag
		// 顯示             true booleanVisibleG
		// 停止最後一個動作 true booleanStopG
		if(!booleanType){
			//顯示 Excel
			Excel.setProperty("DisplayAlerts", new Variant(true));
			if(!booleanFlag  &&  !booleanVisibleG){
				Dispatch.call(Excel, "Quit");
			} else {
				if(booleanSheetEndViewG) {
					// 最後顯示第一個 Sheet
					Dispatch.call(objectSheet1,  "Activate");
					objectA1  =  Dispatch.invoke(objectSheet1,  "Range",   Dispatch.Get,  new Object[] {"A1"},  new int[1]).toDispatch();
					Dispatch.call(objectA1,  "Select");
					//
					if(booleanVisibleG) {
					    // 顯示
    					if(booleanFlag) {
    					    if(!booleanStopG) Dispatch.call(objectSheet1,  "PrintPreview");
    					} 
				  } else {
				      // 不顯示
				      Dispatch.call(Excel, "Quit");
				  }
				} else {
					Dispatch.call(objectSheet2,  "Activate");
					objectA1  =  Dispatch.invoke(objectSheet2,  "Range",   Dispatch.Get,  new Object[] {"A1"},  new int[1]).toDispatch();
					Dispatch.call(objectA1,  "Select");
					//
					if(booleanVisibleG) {
					    // 顯示
    					if(booleanFlag) {
    					    if(!booleanStopG) Dispatch.call(objectSheet2,  "PrintPreview");
    					} 
				  } else {
				      // 不顯示
				      Dispatch.call(Excel, "Quit");
				  }
				}
			}
		} else {
			if(!booleanFlag  &&  !booleanVisibleG){
				  Dispatch.call(Excel, "Quit");
			} else {
				  Dispatch.call(objectSheet1,  "Activate");
				  //
					if(booleanVisibleG) {
					    // 顯示
    					if(booleanFlag) {
    					    if(!booleanStopG) {
    					        System.out.println("1111------------------------") ;
    					        Dispatch.call(objectSheet1,  "PrintPreview");
    					    }
    					} 
				  } else {
				      // 不顯示
				      Dispatch.call(Excel, "Quit");
				  }
			}
		}
		// 釋放com元件
		ComThread.Release() ;
	}
	
	//依Client Excel Version 開啟
   public String getExcelVerson(){
          Vector  vectorVersion  =  new  Vector() ;
          //
          ExcelVerson(vectorVersion) ;
          //
          if(vectorVersion.size()  ==  0)  return  "" ;
          //
          return ""+vectorVersion.get(0) ;
          
   }
   public ActiveXComponent ExcelVerson(){
          Vector  vectorVersion  =  new  Vector() ;
          return  ExcelVerson(vectorVersion) ;
   }
	public ActiveXComponent ExcelVerson(Vector  vectorVersion){
		ActiveXComponent Excel;
		ComThread.InitSTA();
		int intExcelVerson = 0;
		try{
			Excel = new ActiveXComponent("Excel.Application.8");//Excel 97
			System.out.println("Excel 97 is OK!");
         vectorVersion.add("97") ;
			return Excel;			 
		}catch(Exception Excel97){
			try{
				Excel = new ActiveXComponent("Excel.Application.9");//Excel 2000
				System.out.println("Excel 2000 is OK!");					 
            vectorVersion.add("2000") ;
				return Excel;
			}catch(Exception Excel2000){
				try{
					Excel = new ActiveXComponent("Excel.Application.10");//Excel 2002
					System.out.println("Excel 2002 is OK!");							 
               vectorVersion.add("2002") ;
					return Excel;
				}catch(Exception Excel2002){
					try{
						Excel = new ActiveXComponent("Excel.Application.11");//Excel 2003
						System.out.println("Excel 2003 is OK!");
                  vectorVersion.add("2003") ;							 
						return Excel;
					}catch(Exception Excel2003){
							try{
									Excel = new ActiveXComponent("Excel.Application.12");//Excel 2003
									System.out.println("Excel 2007 is OK!");	
                           vectorVersion.add("2007") ;						 
									return Excel;
							}catch(Exception Excel2010){
									try{
											Excel = new ActiveXComponent("Excel.Application.13");//Excel 2003
											System.out.println("Excel 2010 is OK!");							 
                                 vectorVersion.add("2010") ;
											return Excel;
									}catch(Exception Excel14){
											try{
													Excel = new ActiveXComponent("Excel.Application.14");//Excel 2003
													System.out.println("Excel.Application.14 is OK!");	
                                       vectorVersion.add(".14") ;
													return Excel;
											}catch(Exception Excel15){
													try{
															Excel = new ActiveXComponent("Excel.Application.15");//Excel 2003
															System.out.println("Excel.Application.15 is OK!");							 
                                             vectorVersion.add(".15") ;
															return Excel;
													}catch(Exception Excel16){
															try{
																	Excel = new ActiveXComponent("Excel.Application.16");//Excel 2003
																	System.out.println("Excel.Application.16 is OK!");							 
                                                   vectorVersion.add(".16") ;
																	return Excel;
															}catch(Exception Excel17){
																	try{
																			Excel = new ActiveXComponent("Excel.Application.17");//Excel 2003
																			System.out.println("Excel.Application.17 is OK!");							 
                                                         vectorVersion.add(".17") ;
																			return Excel;
																	}catch(Exception Excel18){
																			try{
																					Excel = new ActiveXComponent("Excel.Application.18");//Excel 2003
																					System.out.println("Excel.Application.18 is OK!");							 
                                                               vectorVersion.add(".18") ;
																					return Excel;
																			}catch(Exception Excel19){
																					try{
																							Excel = new ActiveXComponent("Excel.Application.19");//Excel 2003
																							System.out.println("Excel.Application.19 is OK!");							 
                                                                     vectorVersion.add(".19") ;
																							return Excel;
																					}catch(Exception Excel20){
																							try{
																									Excel = new ActiveXComponent("Excel.Application.20");//Excel 2003
																									System.out.println("Excel.Application.20 is OK!");							 
                                                                           vectorVersion.add(".20") ;
																									return Excel;
																							}catch(Exception ExcelError){
																									System.out.println("請使用 Excel2010 以上版本!");							 								
																		
																							}
																
																					}
														
																			}
												
																	}
										
															}
								
													}
						
											}
				
									}
		
							}

					}
				}
			}
		}
		Excel = new ActiveXComponent("Excel.Application");
		System.out.println("All is OK!");							 		 
		return Excel;
	}
	// 應用
   // 得到sheet的總數
   public int getSheetCount(Dispatch objectSheets) {  
       int count = Dispatch.get(objectSheets, "count").toInt();  
       return count;  
   }  
	// 取得特定 Sheet 由 0 開始
	public Dispatch  getDispatchSheet(int  intSheet,  Dispatch objectSheets) {
			try {
					Dispatch  objectSheet1  =  Dispatch.call(objectSheets,  "Item",  new  Variant(intSheet+1)).toDispatch() ;
					if(objectSheet1  !=  null)  return  objectSheet1;
			}catch(Exception  e) {
					return  null ;
			}
			return  null ;
	}
	// 依 Sheet 名稱 取得特定 Sheet 
	public Dispatch  getDispatchSheetForName(String  stringSheetName,  Dispatch objectSheets) {
			try {
               Dispatch  objectSheet1  =  Dispatch.call(objectSheets,  "Item",  stringSheetName).toDispatch() ;
					if(objectSheet1  !=  null)  return  objectSheet1;
			}catch(Exception  e) {
					return  null ;
			}
			return  null ;
	}
	// 刪除 Sheet
	public void  doDeleteSheet(Dispatch  objectSheet ){
			Dispatch.call(objectSheet, "Delete") ; 
	}
	// 隱藏 Sheet
	public void  doHideSheet(Dispatch  objectSheet ){
         Dispatch.put(objectSheet, "Visible", new Boolean(false));  
	}
	// 複製 Sheet (將 objectSheetSource 複製到位於 objectSheetDesc 之前。)
	public void  doCopySheet(Dispatch  objectSheetSource,  Dispatch  objectSheetDesc ){
			Dispatch.call(objectSheetSource, "Copy", objectSheetDesc);
	}
	// 取得 Sheet 名稱
	public String getSheetName(Dispatch objectSheet1){
		String  stringSheetName  =  Dispatch.get(objectSheet1, "Name").toString() ;
		return  stringSheetName;
	}
	public void doSetSheetName(String  stringSheetName,  Dispatch objectSheet1){
			Dispatch.put(objectSheet1, "Name", stringSheetName) ;
	}
	//Copy Sheet2 Template to Sheet1
	public void CopyPage(Dispatch objectSheet1,  Dispatch objectSheet2){
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
	}
	
	//Excel 欄位處理
	public int getExcelColumnNo(String stringColumn){
		int  intReturn  =  0 ;
		for(int  intIndex=0  ;  intIndex<stringColumn.length()  ;  intIndex++) {
			intReturn  +=  (stringColumn.charAt(intIndex)  -  'A'  +  1)  *  Math.pow(26,  stringColumn.length()-1-intIndex);
		}
		return intReturn;          
	}
	
	//Excel 欄位處理
	public String getExcelColumnName(String stringColumn,int intCalculate){
		//char charColumn   = stringColumn.charAt(0);
		int    intColumn    = getExcelColumnNo(stringColumn) + 'A'  -1;
		String stringReturn = ""; 
		// < A
		if ((intColumn + intCalculate) < 65 ) {
			stringReturn = "0";
		}
		int  intTemp  =  (intColumn + intCalculate - 'A')  /  26 ;
		stringReturn  =  Character.toString((char)(((intColumn + intCalculate - 'A')  %  26) +  'A')) ;
		while(intTemp  > 0) {
			stringReturn  =  Character.toString((char)((intTemp  %  26)  +  'A'  -  1))  +  stringReturn ;
			intTemp  =  intTemp  /  26 ;
		}
		if(intTemp  >  0) {
			stringReturn  =  Character.toString((char)((intTemp  %  26)  +  'A'  -  1))  +  stringReturn ;
		}
		return stringReturn;            
	}
	
	// intPosColumn  		Excel 的位置(行)
	// intPosRow   			Excel 的位置(列)
	// objectSheet2   		Excel 的工作表
	public void putDataIntoExcel(int  intPosColumn,  int  intPosRow,  String  stringPutData, Dispatch  objectSheet){	
		String  stringTmp  =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch.put(Dispatch.invoke(objectSheet,  "Range",  Dispatch.Get,  new Object[] {stringTmp+ (intPosRow+1)},  new int[1]).toDispatch(),
			     "Value",
			     stringPutData
			    );
	}
	
	// intPosColumn  		Excel 的位置(行)
	// intPosRow   			Excel 的位置(列)
	// objectSheet2   		Excel 的工作表
	public void putTypeIntoExcel(int  intPosColumn,  int  intPosRow,  String  stringPutType, Dispatch  objectSheet){	
		String  stringTmp  =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch.put(Dispatch.invoke(objectSheet,  "Range",  Dispatch.Get,  new Object[] {stringTmp+ (intPosRow+1)},  new int[1]).toDispatch(),
			     "NumberFormatLocal",
			     stringPutType
			    );
	}
	
	// intPosColumn  		Excel 的位置(行)
	// intPosRow   			Excel 的位置(列)
	// objectSheet2   		Excel 的工作表
	public String  getDataFromExcel(int  intPosColumn,  int  intPosRow,  Dispatch  objectSheet){	
		String  stringTmp     =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch cell         =  Dispatch.invoke(objectSheet, "Range", Dispatch.Get, new Object[]{stringTmp+ (intPosRow+1)}, new int[1]).toDispatch();
		String  stringReturn  =  Dispatch.get(cell,"Value").toString();
		//
		return  stringReturn.trim() ;
	}
	
	public String  getDataFromExcel2(int  intPosColumn,  int  intPosRow,  Dispatch  objectSheet)throws  Throwable {		
		String  stringTmp     =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch cell         =  Dispatch.invoke(objectSheet, "Range", Dispatch.Get, new Object[]{stringTmp+ (intPosRow+1)}, new int[1]).toDispatch();
		String  stringReturn  =  Dispatch.get(cell,"Value").toString();
		//
		if("null".equals(stringReturn))  stringReturn  =  "" ;
		//
		if(!"".equals(stringReturn)) {
		    Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil() ;
		    if(exeUtil.doParseDouble(stringReturn)  >  0) {
		        stringReturn  =  exeUtil.doDeleteDogAfterZero (stringReturn);
		    }
		}
		return  stringReturn.trim() ;
	}
	public String  getDataFromExcel3(int  intPosColumn,  int  intPosRow,  Dispatch  objectSheet) throws  Throwable {	
		String  stringTmp     =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch cell                =  Dispatch.invoke(objectSheet, "Range", Dispatch.Get, new Object[]{stringTmp+ (intPosRow+1)}, new int[1]).toDispatch();
		String  stringReturn  =  Dispatch.get(cell,"Text").toString();
		//
		if("null".equals(stringReturn))  stringReturn  =  "" ;
		//
		if(!"".equals(stringReturn)) {
		    Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil() ;
		    if(exeUtil.isDigitNum (stringReturn)) {
		        stringReturn  =  exeUtil.doDeleteDogAfterZero (stringReturn);
		    }
		}
		return  stringReturn.trim() ;
	}
	public String  getDataFromExcel4(int  intPosColumn,  int  intPosRow,  Dispatch  objectSheet){	
		String  stringTmp     =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch cell                =  Dispatch.invoke(objectSheet, "Range", Dispatch.Get, new Object[]{stringTmp+ (intPosRow+1)}, new int[1]).toDispatch();
		String  stringReturn  =  Dispatch.get(cell,"Value").toString();
		//
		if("null".equals(stringReturn))  stringReturn  =  "0" ;
		return  stringReturn.trim() ;
	}
	// 20110531 B3774
	public String getDataFromExcel(int intPosColumn, int intPosRow, String stringType, Dispatch objectSheet){
		String   stringTmp    = getExcelColumnName( "A",  intPosColumn);
		Dispatch cell         = Dispatch.invoke(objectSheet, "Range", Dispatch.Get, new Object[]{stringTmp+(intPosRow+1)}, new int[1]).toDispatch();
		String   stringReturn = Dispatch.get(cell, stringType).toString();
		//
		return stringReturn.trim();
	}
	
	// 清除欄位，欄位之公式亦會被清空
	public void  doClearContents(String  stringRange,  Dispatch  objectSheet){	
		Dispatch objectRangeClear  =  Dispatch.invoke(objectSheet,  "Range",   Dispatch.Get,  new Object[] {stringRange},  new int[1]).toDispatch();
		Dispatch.call(objectRangeClear,  "ClearContents");
	}
	public void  doClearContents(int  intColStart,  int  intRowStart,  int  intColEnd,  int  intRowEnd,  Dispatch  objectSheet){	
       for(int  intCol=intColStart  ;  intCol<=intColEnd  ;  intCol++) {
                for(int  intRow=intRowStart  ;  intRow<=intRowEnd  ;  intRow++) {
                       putDataIntoExcel(intCol,  intRow,  "", objectSheet) ;
                }
       }
	}
	
	// 複製一整個欄
	public void  doCopyColumns(int  intPosColumn,  Dispatch  objectSheet){	
		String  stringTmp  =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmp  +  ":"  +  stringTmp}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange, "Copy");
		Dispatch.call(objectRange, "Insert");
	}
	// 複製一整個欄 intPosColumn，貼至 intPosColumn2
	public void  doCopyColumnsSpecPos(int  intPosColumn,  int  intPosColumn2,  Dispatch  objectSheet){	
		String  stringTmp  =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmp  +  ":"  +  stringTmp}, new int[1]).toDispatch( ) ;
		stringTmp  =  getExcelColumnName( "A",  intPosColumn2) ;
		Dispatch  objectRange2  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmp  +  ":"  +  stringTmp}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange,  "Copy");
		Dispatch.call(objectRange2, "Insert");
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
	
	public void  doCopyRowSpecPos(int  intPosRow,  int  intPosRow2,  Dispatch  objectSheet){
		Dispatch  objectRange   =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intPosRow   +  ":"  + intPosRow}, new int[1]).toDispatch( ) ;
		Dispatch  objectRange2  =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intPosRow2  +  ":"  + intPosRow2}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange,  "Copy");
		Dispatch.call(objectRange2, "Insert");
	}
	
	public void  doCopyRowSpecPos(int  intCopyRowS,  int  intCopyRowE,  int  intInsertRow,  Dispatch  objectSheet){
		Dispatch  objectRange   =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intCopyRowS  +  ":"  + intCopyRowE}, new int[1]).toDispatch( ) ;
		Dispatch  objectRange2  =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intInsertRow  +  ":"  + intInsertRow}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange,  "Copy");
		Dispatch.call(objectRange2, "Insert");
	}
	
	public void  doCopyRow(int  intRowStart,  int  intRowEnd,  Dispatch  objectSheet){	
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intRowStart  +  ":"  + intRowEnd}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange, "Copy");
		Dispatch.call(objectRange, "Insert");
	}
	
	// 刪除一整欄
	public void  doDeleteColumns(int  intPosColumn,  Dispatch  objectSheet){	
		String    stringTmp    =  getExcelColumnName( "A",  intPosColumn) ;
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringTmp  +  ":"  +  stringTmp}, new int[1]).toDispatch( ) ;
		//Dispatch.put(objectRange, "Delete",  new  varant(-4159 ));
		Dispatch.call(objectRange, "Delete");
	}
	
	public void  doDeleteColumns2(String  stringRange,  Dispatch  objectSheet){	
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringRange}, new int[1]).toDispatch( ) ;
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
	// 變更行高
   // Excel 只允許 0 - 409
	public void  doChangeRowSize(int  intPosRow,  int  intRowSize,  Dispatch  objectSheet){	
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{intPosRow  +  ":"  +  intPosRow}, new int[1]).toDispatch( ) ;
      //
      if(intRowSize  <  0)    intRowSize  = 0 ;
      if(intRowSize  >  409)  intRowSize  = 409 ;
      //
		Dispatch.put(objectRange, "RowHeight", new Variant(intRowSize));
	}
	// 畫框線
	//exeExcel.doLineStyle("A1:C1",  objectSheet1) ;
	public void  doLineStyle(int intX1,  int  intY1,  int  intX2,  int  intY2,  Dispatch  objectSheet1) throws Throwable {
	    String  stringRange = "" ;
	    for(int  intNo=intY1  ;  intNo<=intY2  ;  intNo++) {
          stringRange = getExcelColumnName( "A",  intX1)+(intNo+1)+":"+
          	            getExcelColumnName( "A",  intX2)+(intNo+1) ;
	        doLineStyle(stringRange,  objectSheet1) ;
	    }
	}
	public void  doLineStyleBorder(String  stringRange,  Dispatch  objectSheet1){	
		Dispatch objectRange    =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
		Dispatch objectBorders  =  null ;
		//
		Dispatch.call(objectSheet1,  "Activate");
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
	public void  doLineStyle(String  stringRange,  Dispatch  objectSheet1){	
		Dispatch objectRange    =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
		Dispatch objectBorders  =  null ;
		//
		Dispatch.call(objectSheet1,  "Activate");
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
	
	// 畫框線2 970402 B3774
	public void doLineStyle2(String stringRange,      Dispatch objectSheet, 
				 String stringTopLine,    String stringTopWeight,
				 String stringBottomLine, String stringBottomWeight,
				 String stringLeftLine,   String stringLeftWeight,
				 String stringRightLine,  String stringRightWeight,
				 String stringCenterLine, String stringCenterWeight){
		Dispatch objectRange    =  Dispatch.invoke(objectSheet,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
		Dispatch objectBorders  =  null ;
		//
		Dispatch.call(objectSheet,  "Activate");
		Dispatch.call(objectRange, "Select");
		//上
		if(stringTopLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"8"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringTopLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringTopWeight) ;
		}
		//下
		if(stringBottomLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"9"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringBottomLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringBottomWeight) ;
		}
		//左
		if(stringLeftLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"7"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringLeftLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringLeftWeight) ;
		}
		//右
		if(stringRightLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"10"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringRightLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringRightWeight) ;
		}
		//中
		if(stringCenterLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"11"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringCenterLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringCenterWeight) ;
		}
	}
	// 畫框線3 970613 B3774
	public void doLineStyle3(String stringRange,      Dispatch objectSheet, 
				 String stringTopLine,    String stringTopWeight,
				 String stringBottomLine, String stringBottomWeight,
				 String stringLeftLine,   String stringLeftWeight,
				 String stringRightLine,  String stringRightWeight,
				 String stringVertLine,   String stringVertWeight,
				 String stringHoriLine,   String stringHoriWeight){
		Dispatch objectRange    =  Dispatch.invoke(objectSheet,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
		Dispatch objectBorders  =  null ;
		//
		Dispatch.call(objectSheet,  "Activate");
		Dispatch.call(objectRange, "Select");
		//上
		if(stringTopLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"8"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringTopLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringTopWeight) ;
		}
		//下
		if(stringBottomLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"9"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringBottomLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringBottomWeight) ;
		}
		//左
		if(stringLeftLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"7"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringLeftLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringLeftWeight) ;
		}
		//右
		if(stringRightLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"10"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringRightLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringRightWeight) ;
		}
		//中(垂直)
		if(stringVertLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"11"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringVertLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringVertWeight) ;
		}
		//中(水平)
		if(stringHoriLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"12"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringHoriLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringHoriWeight) ;
		}
	}
	// 畫框線4 980619 B3774
	public void doLineStyle4(String stringRange,      Dispatch objectSheet, 
				 String stringTopLine,    String stringTopWeight,    String stringTopColorIndex, 
				 String stringBottomLine, String stringBottomWeight, String stringBottomColorIndex, 
				 String stringLeftLine,   String stringLeftWeight,   String stringLeftColorIndex, 
				 String stringRightLine,  String stringRightWeight,  String stringRightColorIndex, 
				 String stringVertLine,   String stringVertWeight,   String stringVertColorIndex, 
				 String stringHoriLine,   String stringHoriWeight,   String stringHoriColorIndex){
		Dispatch objectRange    =  Dispatch.invoke(objectSheet,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
		Dispatch objectBorders  =  null ;
		//
		Dispatch.call(objectSheet,  "Activate");
		Dispatch.call(objectRange, "Select");
		//上
		if(stringTopLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"8"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringTopLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringTopWeight) ;
			if(stringTopColorIndex.length()!=0){
				Dispatch.put(objectBorders,  "ColorIndex",  stringTopColorIndex) ;
			}
		}
		//下
		if(stringBottomLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"9"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringBottomLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringBottomWeight) ;
			if(stringBottomColorIndex.length()!=0){
				Dispatch.put(objectBorders,  "ColorIndex",  stringBottomColorIndex) ;
			}
		}
		//左
		if(stringLeftLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"7"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringLeftLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringLeftWeight) ;
			if(stringLeftColorIndex.length()!=0){
				Dispatch.put(objectBorders,  "ColorIndex",  stringLeftColorIndex) ;
			}
		}
		//右
		if(stringRightLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"10"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringRightLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringRightWeight) ;
			if(stringRightColorIndex.length()!=0){
				Dispatch.put(objectBorders,  "ColorIndex",  stringRightColorIndex) ;
			}
		}
		//中(垂直)
		if(stringVertLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"11"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringVertLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringVertWeight) ;
			if(stringVertColorIndex.length()!=0){
				Dispatch.put(objectBorders,  "ColorIndex",  stringVertColorIndex) ;
			}
		}
		//中(水平)
		if(stringHoriLine.length()!=0){
			objectBorders  =  Dispatch.invoke(objectRange,  "Borders", Dispatch.Get,new Object[] {"12"},  new int[1]).toDispatch();
			Dispatch.put(objectBorders,  "LineStyle",  stringHoriLine) ;
			Dispatch.put(objectBorders,  "Weight",     stringHoriWeight) ;
			if(stringHoriColorIndex.length()!=0){
				Dispatch.put(objectBorders,  "ColorIndex",  stringHoriColorIndex) ;
			}
		}
	}
	// 欄位對齊方式 971125 B3774
	// 置中 3 靠右 1 靠 2          new Variant(-4152 )
	public void setHVAlign(String stringRange, Dispatch objectSheet, String stringHAlign, String stringVAlign){
		Dispatch objectRange   = Dispatch.invoke(objectSheet,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
		//
		Dispatch.call(objectRange, "Select");
		if(stringHAlign.length() != 0){
			Dispatch.put(objectRange, "HorizontalAlignment", stringHAlign);	
		}
		if(stringVAlign.length() != 0){
			Dispatch.put(objectRange, "HorizontalAlignment", stringVAlign);	
		}
	}
	// 設定字體顏色
	// 3   紅色
	// Bold 粗體(Y)      Italic 斜體(Y)     Underline 底線(xlUnderlineStyleSingle 2) 
	//exeExcel.setFontColorRange("Bold",     "Y",  "3",  "D4",  objectSheet1) ;
	//exeExcel.setFontColorRange("Italic",   "Y",  "3",  "D5",  objectSheet1) ;
	//exeExcel.setFontColorRange("Underline","2",  "3",  "D6",  objectSheet1) ;
	public  void  setFontColorRange(String  stringFontType,  String  stringFontValue,  String stringColor,  String  stringRange,  Dispatch  objectSheet1){
    		Dispatch  objectRange     =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
    		Dispatch  objectFont      =  null ;
		    //		    
	    	//Dispatch.call(objectRange, "Select");
	    	objectFont = Dispatch.get(objectRange, "Font").toDispatch( ) ;
	    	// 字體
	    	if(!"".equals(stringFontType)) {
    			  if(!"Underline".equals(stringFontType)) {
    				    Dispatch.put(objectFont,  stringFontType,  new Variant(stringFontValue.equals("Y"))) ;
    			  } else {
    				    Dispatch.put(objectFont,  stringFontType,  stringFontValue) ;
    	    	}
		    }
	    	// 顏色
	    	if(!"".equals(stringColor))Dispatch.put(objectFont,  "ColorIndex",  stringColor) ;
	}
	// 設定欄位背景顏色
	// 36 淺黃  35 淺綠   34 淺藍   40 淺橘
	//exeExcel.setBackgroundColorRange("40",  "E7",  objectSheet1) ;
	public  void  setBackgroundColorRange(String stringColor,  String  stringRange,  Dispatch  objectSheet1){
		Dispatch  objectRange     =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
	    	Dispatch  objectInterior  =  null ;
	    	//
	    	//Dispatch.call(objectRange, "Select");
	    	objectInterior = Dispatch.get(objectRange, "Interior").toDispatch( ) ;
	    	Dispatch.put(objectInterior,  "ColorIndex",  stringColor) ;
	    	Dispatch.put(objectInterior,  "Pattern",  "1") ;
	}
	//合併儲存格
	public  void  doMergeCells(String  stringRange,  Dispatch  objectSheet1){
		Dispatch objectRange = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[]{stringRange}, new int[1]).toDispatch();
		Dispatch.put(objectRange, "MergeCells", "True");
	}
	//整個隱藏功能
	public  void  doHiddenCells(String  stringRange,  Dispatch  objectSheet1){
		Dispatch objectRange  = Dispatch.invoke(objectSheet1, "Range", Dispatch.Get, new Object[]{stringRange}, new int[1]).toDispatch();
		Dispatch entireColumn = Dispatch.get(objectRange,"EntireColumn").toDispatch();
		//Dispatch.put(entireColumn, "Hidden", new Boolean(true));
		Dispatch.put(entireColumn, "Hidden", new Boolean(true));
	}
	// 頁首
	// 範例：setPageTitleName(objectSheet1,  "&14公司名稱\n電算發票\n94年11月25日申報") ;
	// 範例：setPageTitleName(objectSheet1,  "&\"標楷體,粗體\"&2894年11月25日申報") ; 
	/*格式代碼 描述 
      &L 下一筆字元靠左對齊。 
      &C 下一筆字元置中。 
      &R 下一筆字元靠右對齊。 
      &E 開關列印雙底線功能。 
      &X 開關列印上標字元功能。 
      &Y 開關列印下標字元功能。 
      &B 開關列印粗體字元功能。 
      &I 開關列印斜體字元功能。 
      &U 開關列印底線功能。 
      &S 開關列印刪除線功能。 
      &D 列印目前日期。 
      &T 列印目前時間。 
      &F 列印文件名稱。 
      &A 列印活頁簿標籤名稱。 
      &P 列印頁碼。 
      &P+數字 列印頁碼加上指定數字。 
      &P-數字 列印頁碼減去指定數字。 
      && 列印單個連字號。 
      & "fontname" 以指定字型列印隨後字元。必須加上雙引號。 
      &nn 以指定字型大小列印隨後字元。使用兩位數指定字型的點數大小。 
      &N 列印文件的總頁數。 
  */
	public  void  setPageTitleName(Dispatch  objectSheet1,  String stringPageTitleName){
		setPageTitleName(objectSheet1,  "CenterHeader",  stringPageTitleName);
	}
	/*
	左邊 RightHeader
	中間 CenterHeader
	右邊 LeftHeader
	*/
	public  void  setPageTitleName(Dispatch  objectSheet1,  String  stingPosition,  String stringPageTitleName){
		objectSheet1  =  Dispatch.get(objectSheet1,  "PageSetup").toDispatch( );
		Dispatch.put(objectSheet1,  stingPosition,  stringPageTitleName) ;
	}	

	
	//頁尾
	public  void  setPageFooter(Dispatch  objectSheet1,  String stringPageTitleName){
		objectSheet1  =  Dispatch.get(objectSheet1,  "PageSetup").toDispatch( );
		Dispatch.put(objectSheet1,  "CenterFooter",  stringPageTitleName) ;
	}		
	
	// 取得已開啟之 sheet 之 最大列數
	public  int  getExcelMaxRow(Dispatch  dispatchSheet1){
		Dispatch  dispatchUsedRange =  Dispatch.get(dispatchSheet1, "UsedRange").toDispatch();
		String[]  retArray          =  convert.StringToken(Dispatch.call(dispatchUsedRange, "Address").toString( ),  "$") ;
		if(retArray.length  !=  5)  return  1 ;
		int       intRowMax         =  doParseInteger(retArray[4].trim( )) ;
		int       intColMax         =  getExcelColumnNo(retArray[3].trim( )) ;   
		return  intRowMax ;
	}
	
	// 取得已開啟之 sheet 之 最大欄位數
	public  int  getExcelMaxCol(Dispatch  dispatchSheet1){
		Dispatch  dispatchUsedRange =  Dispatch.get(dispatchSheet1, "UsedRange").toDispatch();
		String[]  retArray          =  convert.StringToken(Dispatch.call(dispatchUsedRange, "Address").toString( ),  "$") ;
		if(retArray.length  !=  5)  return  1 ;
		int       intColMax         =  getExcelColumnNo(retArray[3].trim( )) ;   
		return  intColMax ;
	}
	
	// 自動調整欄寬
	public void doAutoAdjustColWidth(String  stringColumnName,  Dispatch  objectSheet){
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Columns",  Dispatch.Get,  new Object[]{stringColumnName}, new int[1]).toDispatch( ) ;
		Dispatch.call(objectRange,  "AutoFit");
	}
	
	// 分頁 B3774
	public void setPageBreak(int  intRow,  Dispatch  objectSheet){
		Dispatch  objectRange  =  Dispatch.invoke(objectSheet,  "Rows",  Dispatch.Get,  new Object[]{""+intRow}, new int[1]).toDispatch( ) ;
		Dispatch objHPageBreaks = Dispatch.call(objectSheet,  "HPageBreaks").toDispatch();
		Dispatch.call(objHPageBreaks, "Add", objectRange);
	}	
	
	// 其它
	public  int  doParseInteger(String  stringNum) {
		// 
		int  intNum  =  0 ;
		if("".equals(stringNum)  ||  "null".equals(stringNum))  return  0;
		try{
			intNum  =  Integer.parseInt(stringNum) ;
		} catch(Exception e) {
			System.out.println("無法剖析["  +  stringNum  +  "]，回傳 0。") ;
			return  0 ;
		}
		return  intNum ;
	}
   //畫長條圖形
	//type , 距離左邊框的距離 , 距離上邊框的距離 , 圖表的寬度 , 圖表的高度 , 填滿的顏色 , 線框的顏色 , 文字
	//範例 setRectangle(1,100,100,200,10,"150,100,50","150,100,50","test",objectSheet1);
	public  void  setRectangle(int type , int left , int top , int width, int height , String Fill_Color , String Line_Color , String Text,  Dispatch  objectSheet1){

		Dispatch shapes = Dispatch.get(objectSheet1, "Shapes").toDispatch();
		Dispatch   shape      =   Dispatch.invoke(shapes,"AddShape", 1
		                                        , new Object[]{  
							    new Integer(type)//圖表類形 1
							  , new Integer(left)//距離左邊框的距離 10
							  , new Integer(top)//距離上邊框的距離 10 
							  , new Integer(width)//圖表的寬度        100
							  , new Integer(height)//圖表的高度       10
							  }  
							, new int[1]).toDispatch();
		
		//填滿的顏色 紅 0,255,0 藍 150,100,50
  		//可參考 G:\資訊室\Excel\test\jacob_shape_color_list_v1.xlsx  
		System.out.println(" Fill_Color "+Fill_Color);
		Dispatch  object_Fill = Dispatch.get(shape, "Fill").toDispatch( ) ;
		Dispatch  object_Fill_ForeColor = Dispatch.get(object_Fill, "ForeColor").toDispatch( ) ;
	 	Dispatch.put(object_Fill_ForeColor,  "RGB",  Fill_Color) ;
		
		//線框的顏色
		Dispatch  object_Line = Dispatch.get(shape, "Line").toDispatch( ) ;
		Dispatch  object_Line_ForeColor = Dispatch.get(object_Line, "ForeColor").toDispatch( ) ;
	        Dispatch.put(object_Line_ForeColor,  "RGB",  Line_Color) ;			
		
		//文字
		Dispatch  object_TextFrame2 = Dispatch.get(shape, "TextFrame2").toDispatch( ) ;
		Dispatch  object_TextRange = Dispatch.get(object_TextFrame2, "TextRange").toDispatch( ) ;
		Dispatch  object_Characters = Dispatch.get(object_TextRange, "Characters").toDispatch( ) ;
		Dispatch.put(object_Characters,  "Text",  Text ) ;
   }	
	//type , 距離左邊框的距離 , 距離上邊框的距離 , 圖表的寬度 , 圖表的高度 , 填滿的顏色 , 線框的顏色 , 文字
	//範例 setRectangle(1,100,100,200,10,"150,100,50","150,100,50","text",objectSheet1);
	public  void  setRectangle(int type , double left , double top , double width, double height , String Fill_Color , String Line_Color , String Text,  Dispatch  objectSheet1){

		Dispatch shapes = Dispatch.get(objectSheet1, "Shapes").toDispatch();
		Dispatch   shape      =   Dispatch.invoke(shapes,"AddShape", 1
		                                        , new Object[]{  
							    new Integer(type)//圖表類形 1
							  , new Double(left)//距離左邊框的距離 10
							  , new Double(top)//距離上邊框的距離 10 
							  , new Double(width)//圖表的寬度        100
							  , new Double(height)//圖表的高度       10
							  }  
							, new int[1]).toDispatch();
		
		//填滿的顏色 紅 0,255,0 藍 150,100,50
		//可參考 G:\資訊室\Excel\testjacob_shape_color_list_v1.xlsx 
		System.out.println(" Fill_Color "+Fill_Color);
		Dispatch  object_Fill = Dispatch.get(shape, "Fill").toDispatch( ) ;
		Dispatch  object_Fill_ForeColor = Dispatch.get(object_Fill, "ForeColor").toDispatch( ) ;
	 	Dispatch.put(object_Fill_ForeColor,  "RGB",  Fill_Color) ;
		
		//線框的顏色
		Dispatch  object_Line = Dispatch.get(shape, "Line").toDispatch( ) ;
		Dispatch  object_Line_ForeColor = Dispatch.get(object_Line, "ForeColor").toDispatch( ) ;
	    Dispatch.put(object_Line_ForeColor,  "RGB",  Line_Color) ;			
		
		//文字
		Dispatch  object_TextFrame2 = Dispatch.get(shape, "TextFrame2").toDispatch( ) ;
		Dispatch  object_TextRange = Dispatch.get(object_TextFrame2, "TextRange").toDispatch( ) ;
		Dispatch  object_Characters = Dispatch.get(object_TextRange, "Characters").toDispatch( ) ;
		Dispatch.put(object_Characters,  "Text",  Text ) ;
		
		
		
   }	
   
   
   //畫長條線
	//type , 線條左邊X軸 , 線條左邊Y軸 , 線條右邊X軸 , 線條右邊Y軸 , 線條的顏色
	//範例 setConnectorStraight(1,100,100,200,10,"150,100,50",objectSheet1);
	public  void  setConnectorStraight(int type , double leftX , double leftY , double rightX, double rightY , String Line_Color,   Dispatch  objectSheet1){

		Dispatch shapes = Dispatch.get(objectSheet1, "Shapes").toDispatch();
		Dispatch   shape      =   Dispatch.invoke(shapes,"AddConnector", 1
		                                        , new Object[]{  
							    new Integer(type)//圖表類形 1
							  , new Double(leftX)//線條左邊X軸
							  , new Double(leftY)//線條左邊Y軸 
							  , new Double(rightX)//線條右邊X軸
							  , new Double(rightY)//線條右邊Y軸
							  }  
							, new int[1]).toDispatch();
		

		//線條的顏色
		Dispatch  object_Line = Dispatch.get(shape, "Line").toDispatch( ) ;
		Dispatch  object_Line_ForeColor = Dispatch.get(object_Line, "ForeColor").toDispatch( ) ;
	    Dispatch.put(object_Line_ForeColor,  "RGB",  Line_Color) ;		
		
		//Dispatch  object_Line = Dispatch.get(shape, "Line").toDispatch( ) ;
		//Dispatch  object_ Line_Weight = Dispatch.get(object_Line, "Weight").toDispatch( ) ;
	    //Dispatch.put(object_Line_Weight, "Weight" ,   Line_Weight) ;	
		
   } 
	// 保護工作表 B3774
	public  void  setProtect(String  stringPassword,  Dispatch  objectSheet){
		if(stringPassword.length() == 0){
			Dispatch.invoke(objectSheet, "Protect", Dispatch.Method, new Object[] { new Variant("Farglory"), new Variant(true), new Variant(true), new Variant(true)}, new int[1]);	
		}
		else {
			Object objPassword = stringPassword;		
			Dispatch.invoke(objectSheet, "Protect", Dispatch.Method, new Object[] { new Variant(objPassword), new Variant(true), new Variant(true), new Variant(true)}, new int[1]);	
		}

	}
  	//字加刪除線
	//A1:A1欄位的字要加刪除線
	//excel程式範例 Boolean Boolflag = new Boolean("true");
	//                       exeExcel.setStrikethrough(Boolflag,"A1:A1",objectSheet1);
	// flag true 有刪除線 false 沒刪除線         
	public  void  setStrikethrough( Boolean boolflag,  String  stringRange,  Dispatch  objectSheet1){
    		Dispatch  objectRange     =  Dispatch.invoke(objectSheet1,  "Range",  Dispatch.Get,new Object[]{stringRange},  new int[1]).toDispatch( ) ;
    		Dispatch  objectFont      =  null ; 
	    	objectFont = Dispatch.get(objectRange, "Font").toDispatch( ) ;
			Dispatch.put(objectFont, "Strikethrough",boolflag);
	}
}