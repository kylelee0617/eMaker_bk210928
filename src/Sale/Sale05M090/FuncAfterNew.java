package Sale.Sale05M090;

import java.util.Hashtable;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bNotify;
import jcx.util.convert;
import jcx.util.operation;

public class FuncAfterNew extends bNotify{
	public void actionPerformed(String value)throws Throwable{
		// ����槹 Transaction ��,�|���楻�q�{��
		//�i�ΥH�H�oEmail�q���άO�۰ʦA�B�z�۩wTransaction
		//�s�ܭ��I�Ȥ�W��

		//"�ʫ��ҩ���-�P�B-��P(Sale02M030)"-------------------------------------
		Farglory.util.FargloryUtil  exeUtil  =  new  Farglory.util.FargloryUtil() ; // 20090414
		talk  dbSale  =  getTalk((String)get("put_dbSale")) ;//SQL2000
		String  stringSQL = "";
		//   0  ProjectID1		1  OrderDate   		  2   SaleID1 		      3  SaleName1    	 4  SaleID2
		//   5  SaleName2     6  SaleID3,             7  SaleName3    	  8  SaleID4               9  SaleName4
		// 10  SaleID5         11  SaleName5    	12  MediaID         		13  MediaName   		14  ZoneID
		// 15  ZoneName 	  16  MajorID       		17  MajorName   		18  UseType         	19  Remark
		//  20  SaleID6        21  SaleName6		22  SaleID7 				23  SaleName7        24  SaleID8 		
		//  25  SaleName8  26  SaleID9 	        27  SaleName9 		28  SaleID10            29  SaleName10
		// 30  SSMediaID	  31  SSMediaID1
		stringSQL = "SELECT ProjectID1,   OrderDate,   SaleID1,        SaleName1,   SaleID2, " + 
		                                 " SaleName2,  SaleID3,        SaleName3,  SaleID4,         SaleName4, "  +
										 " SaleID5,        SaleName5,  MediaID,       MediaName,  ZoneID, "  +
										 "  ZoneName,  MajorID,       MajorName,  UseType,       Remark, "  + 
										 "  SaleID6,        SaleName6, SaleID7,        SaleName7,    SaleID8,  "  +
										 "  SaleName8,  SaleID9,       SaleName9, SaleID10,         SaleName10, "  +// �W�[��X�H 20090525
										 " SSMediaID,    SSMediaID1 "  +  // �W�[ [�C��Ӷ��N�X] ���H�q 2015/05/25�@�@
							  " FROM Sale05M090 " +
							" WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" ;
		String[][]  retSale05M090 = dbSale.queryFromPool(stringSQL);
		if(retSale05M090.length == 0){
				message("�ʫε�����:" + getValue("OrderNo").trim() + " ���s�b!");	
			return ;		
		}
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
				message("�ʫε�����:" + getValue("OrderNo").trim() + " �Ȥ�(Sale05M091) ���s�b!");	
			return ;		
		}
		
    System.out.println("�~�����v����-------------------------------------S") ;
    //21-05 Kyle : ��s�D�n�Ȥ�P���p�H
    getButton("RenewRelated").doClick();
    
    //21-05 Kyle : �d�߫Ȥ᭷�I��
    getButton("CheckRiskNew").doClick();
    
    // AML
    setValue("actionText","�s�W");
    getButton("AML").doClick();
    
    //����H�oMAIL
    getButton("sendMail").doClick();
    
    System.out.println("�~�����v����-------------------------------------E") ;

		//  Sale05M421
		// 2015-10-15 B3018 START
		System.out.println("Sale05M421-------------------------------------S") ;
		Vector  		vectorColumnName	=  new  Vector() ;
		Vector  		vectorSale05M421  =  exeUtil.getQueryDataHashtable("Sale05M421",  new  Hashtable(),  " AND OrderNo = '" + getValue("OrderNo").trim() + "' ",  vectorColumnName,  dbSale) ;   
		System.out.println("Sale05M421-------------------------------------E") ;
		Hashtable   hashtableDB     		=  null ; 
		Hashtable   hashtableDBL     		=  new  Hashtable()  ; 
		Vector  		vectorSql   				=  new  Vector() ;
		String  			stringHouseCarL 		=  "" ;
		String  			stringPositionL 		=  "" ;
		String  			stringCarL         		=  "" ;
		String  			stringTemp  				=  "" ;
		// 2015-10-15 B3018 END
		//
		String stringCustom = "";
		for(int  intSale05M091=0  ;  intSale05M091<retSale05M091.length  ;  intSale05M091++){
			if(intSale05M091==0)	stringCustom = retSale05M091[intSale05M091][0];
			else stringCustom = "-" + stringCustom + retSale05M091[intSale05M091][0];
		}
		//stringCustom = retSale05M091[0][0].trim();
		//  0  HouseCar 		1  Position 				2  DealMoney			3  TrxDate  		4  StatusCd
		//  5  GiftMoney		6  CommMoney		7  ViMoney  				8  CommMoney1
		stringSQL = "SELECT HouseCar, " +
										 " Position, " +
										 " DealMoney, " +
										 " TrxDate, " +
										 " StatusCd, " +
										 " GiftMoney, " +
										 " CommMoney, " +
										 " ViMoney, " +			
										 " CommMoney1 " +  // �W�[ [�������] ���H�q 2015/10/13						 											 											 											 											 											 
							  " FROM Sale05M092 " +
							" WHERE OrderNo = '" + getValue("OrderNo").trim() + "'" +
							" AND ISNULL(StatusCd,'')<>'D' " +
							" ORDER BY HouseCar DESC,RecordNo" ;
		String[][]  retSale05M092  =  dbSale.queryFromPool(stringSQL);
		String stringPosition1 = "";
		for(int  intSale05M092=0  ;  intSale05M092<retSale05M092.length  ;  intSale05M092++){
			String stringHouseCar = retSale05M092[intSale05M092][0].trim();
			String stringPosition = retSale05M092[intSale05M092][1].trim();
			//����s����O
			if (stringHouseCar.equals("House")) stringPosition1 = stringPosition;
			String stringDealMoney    = retSale05M092[intSale05M092][2].trim();
			String stringTrxDate         = retSale05M092[intSale05M092][3].trim();
			String stringStatusCd       = retSale05M092[intSale05M092][4].trim();
			String stringGiftMoney      = retSale05M092[intSale05M092][5].trim();
			String stringCommMoney = retSale05M092[intSale05M092][6].trim();
			String stringViMoney        = retSale05M092[intSale05M092][7].trim();
			String stringCommMoney1 = retSale05M092[intSale05M092][8].trim();   		// �W�[ [�������] ���H�q 2015/10/13			
			//
			String stringA_Sale 		 = " A_Sale";
			String stringASaleMan  =  "A_Sale_SaleID";			// 2015-10/15 B3018
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
			if(retA_Sale.length == 0){
					message("��P A_Sale �ɼӧO���s�b!");	
				return ;		
			}
			String stringID1 = retA_Sale[0][0];
			System.out.println("stringID1---------------------"+stringID1) ;
			//���ت�
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
				message("���ت�(Sale05M040) " +  stringProjectID1 + "-" + stringPosition + " ���s�b!");	
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
			double   doubleHouseRate = Double.parseDouble(operation.floatDivide(stringH_ListPrice,  stringListPrice,  3));
			double   doubleLandRate   = Double.parseDouble(operation.floatSubtract(""+1,  ""+doubleHouseRate,  3)) ;
			if("H03A".equals(stringProjectID1)) {
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
				message("��P A_Group �קO���s�b!");	
				return ;		
			}
			String stringProjectID = retA_Group[0][0];
			//UPDATE A_Sale
			vectorSql  =  new  Vector() ;// 2015-10-15 B3018
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
											" SaleID6  =  '"          +  retSale05M090[0][20].trim()  +  "', "  +   	// �W�[��X�H 20090414
											" SaleName6  =  '"    +  retSale05M090[0][21].trim()  +  "', "  +   	// �W�[��X�H 20090414
											" MediaID  =  '"         +  retSale05M090[0][12].trim()  +  "', "  +
											" MediaName  =  '"   +  retSale05M090[0][13].trim()  +  "', "  +
											" ZoneID  =  '"           +  retSale05M090[0][14].trim()  +  "', "  +
											" ZoneName  =  '"     +  retSale05M090[0][15].trim()  +  "', "  +
											" MajorID  =  '"          +  retSale05M090[0][16].trim()  +  "', "  +
											" MajorName  =  '"   +  retSale05M090[0][17].trim()  +  "', "  +
											" UseType  =  '"        +  retSale05M090[0][18].trim()  +  "', "  +
											" Remark  =  '"          +  retSale05M090[0][19].trim()  +  "', " ;
			if(stringHouseCar.equals("House")) stringSQL = stringSQL + " Position = '" + 	stringPosition + "',";
			else{
					stringSQL = stringSQL + " Car = '" + 	stringPosition + "'," +
														    " Position1 = '" + 	stringPosition1 + "'," +
														    " Custom1 = '" + 	stringCustom + "',";
					}
			if ("".equals("stringDealMoney")) stringDealMoney = "0	";
			if ("".equals("stringGiftMoney")) stringGiftMoney = "0	";
			if ("".equals("stringCommMoney")) stringCommMoney = "0	";
			if ("".equals("stringCommMoney1")) stringCommMoney1 = "0	";  // 2015-10-13 �������
			if ("".equals("stringViMoney")) stringViMoney = "0	";
			String stringPureMoney = "" + (Double.parseDouble(stringDealMoney) - 
															  Double.parseDouble(stringGiftMoney) -
															  Double.parseDouble(stringCommMoney) -
															  Double.parseDouble(stringCommMoney1) -		// 2015-10-22 B3018
															  Double.parseDouble(stringViMoney));
		   	stringSQL = stringSQL	+ 
								" Custom = '" + stringCustom + "'," +
								" OrderDate = '" + stringOrderDate + "'," +						
								" PingSu = " + stringPingSu + "," +												
								" PreMoney = " + stringListPrice + "," +
								" H_PreMoney = " + convert.FourToFive(stringH_ListPrice,4) + "," +												
								" L_PreMoney = " + convert.FourToFive(stringL_ListPrice,4) + "," +
								" DealMoney = " + convert.FourToFive(stringDealMoney,4) + "," +
								" H_DealMoney = " + convert.FourToFive(""+Double.parseDouble(stringDealMoney) * doubleHouseRate,4) + "," +												
								" L_DealMoney = " + convert.FourToFive(""+Double.parseDouble(stringDealMoney) * doubleLandRate,4) + "," +																														
								" GiftMoney = " + convert.FourToFive(stringGiftMoney,4) + "," +
								" H_GiftMoney = " + convert.FourToFive(""+Double.parseDouble(stringGiftMoney) * doubleHouseRate,4) + "," +												
								" L_GiftMoney = " + convert.FourToFive(""+Double.parseDouble(stringGiftMoney) * doubleLandRate,4) + "," +																														
								" CommMoney = " + convert.FourToFive(stringCommMoney,4) + "," +
								" H_CommMoney = " + convert.FourToFive(""+Double.parseDouble(stringCommMoney) * doubleHouseRate,4) + "," +
								" L_CommMoney = " + convert.FourToFive(""+Double.parseDouble(stringCommMoney) * doubleLandRate,4) + "," +
								" CommMoney1 = " + convert.FourToFive(stringCommMoney1,  4) + "," + 																					// 2015-10-13 B3018�������
								" H_CommMoney1 = " + convert.FourToFive(""+Double.parseDouble(stringCommMoney1) * doubleHouseRate,  4) + "," +  		// 2015-10-13 B3018�������
								" L_CommMoney1 = " + convert.FourToFive(""+Double.parseDouble(stringCommMoney1) * doubleLandRate,  4) + "," +   		// 2015-10-13 B3018�������
								" ViMoney = " + convert.FourToFive(stringViMoney,4) + "," +
								" H_ViMoney = " + convert.FourToFive(""+Double.parseDouble(stringViMoney) * doubleHouseRate,4) + "," +												
								" L_ViMoney = " + convert.FourToFive(""+Double.parseDouble(stringViMoney) * doubleLandRate,4) + "," +																														
		    					" PureMoney = " + stringPureMoney + "," +
								" H_PureMoney = " + convert.FourToFive(""+Double.parseDouble(stringPureMoney) * doubleHouseRate,4) + "," +												
								" L_PureMoney = " + convert.FourToFive(""+Double.parseDouble(stringPureMoney) * doubleLandRate,4) + "," +																														
								" LastMoney = " + stringFloorPrice + "," +
								" H_LastMoney = " + stringH_FloorPrice + "," +												
								" L_LastMoney = " + stringL_FloorPrice + "," +
								" BalaMoney = " +  convert.FourToFive(""+(Double.parseDouble(stringPureMoney) - Double.parseDouble(stringFloorPrice)),4) + "," +
								" H_BalaMoney = " + convert.FourToFive(""+(Double.parseDouble(stringPureMoney) * doubleHouseRate - Double.parseDouble(stringH_FloorPrice)),4) + "," +
								" L_BalaMoney = " + convert.FourToFive(""+(Double.parseDouble(stringPureMoney) * doubleLandRate - Double.parseDouble(stringL_FloorPrice)),4) + "," +
								" OrderNo = '" + getValue("OrderNo").trim() + "', " +
								" SSMediaID  =  '"    +  retSale05M090[0][30].trim()  +  "', "  +   	// �W�[[�C��N�X 2010/04/21
								" SSMediaID1  =  '"   +  retSale05M090[0][31].trim()  +  "', "  +   	// �W�[[�C��Ӷ��N�X 2015/05/25
							    " SaleID7  =  '"          +  retSale05M090[0][22].trim()  +  "', "  +   	// �W�[��X�H 20090525
								" SaleName7  =  '"    +  retSale05M090[0][23].trim()  +  "', "  +   	// �W�[��X�H 20090525
							    " SaleID8  =  '"          +  retSale05M090[0][24].trim()  +  "', "  +   	// �W�[��X�H 20090525
								" SaleName8  =  '"    +  retSale05M090[0][25].trim()  +  "', "  +   	// �W�[��X�H 20090525
							    " SaleID9  =  '"          +  retSale05M090[0][26].trim()  +  "', "  +   	// �W�[��X�H 20090525
								" SaleName9  =  '"    +  retSale05M090[0][27].trim()  +  "', "  +   	// �W�[��X�H 20090525
							    " SaleID10  =  '"        +  retSale05M090[0][28].trim()  +  "', "  +   	// �W�[��X�H 20090525
								" SaleName10  =  '"  +  retSale05M090[0][29].trim()  +  "' "  +   	// �W�[��X�H 20090525
							  " WHERE ID1 = " +  stringID1;
		   // 2015'10/15 B3018 START
		   vectorSql.add(stringSQL) ;
		   System.out.println("-----------------------------------------------0") ;
		   //
		   stringSQL  =  exeUtil.doDeleteDB(stringASaleMan,  new  Hashtable(),  " AND  ID1  =  "+stringID1+" ",  false,  dbSale) ;vectorSql.add(stringSQL) ;
		   for(int  intNo=0  ;  intNo<vectorSale05M421.size()  ;  intNo++) {
		    		hashtableDB  =  (Hashtable)  vectorSale05M421.get(intNo) ;  if(hashtableDB  ==  null)  continue ;
					//
					for(int  intNoL=0  ;  intNoL<vectorColumnName.size()  ;  intNoL++) {
							stringTemp  =  ""+vectorColumnName.get(intNoL) ;
							hashtableDBL.put(stringTemp,  ""+hashtableDB.get(stringTemp)) ;
					}
					//
					System.out.println(intNo+"-----------------------------------------------1") ;
					if(stringHouseCar.equals("House")) {
							stringHouseCarL  = "Position" ;
							stringPositionL      = stringPosition ;
							stringCarL            = "" ;
					} else if(stringHouseCar.equals("Car")) {
							stringHouseCarL  = "Car" ;
							stringPositionL      = "" ;
							stringCarL            = stringPosition ;
					}
					System.out.println(intNo+"-----------------------------------------------2") ;
					//
					hashtableDBL.put("ID1",  				stringID1) ;
					hashtableDBL.put("ProjectID1",   	stringProjectID1) ;
					hashtableDBL.put("HouseCar",     stringHouseCarL) ;
					hashtableDBL.put("Position",     	stringPositionL) ;
					hashtableDBL.put("Car",     	        stringCarL) ;
					stringSQL      =  exeUtil.doInsertDB(stringASaleMan,  hashtableDBL,  false,  dbSale) ;
					//
					vectorSql.add(stringSQL) ;
		   }
		   dbSale.execFromPool((String[])  vectorSql.toArray(new  String[0]));  
		   // 2015/10/15 B3018 END
		}
		message("OK!");
		return;
	}
	public String getInformation(){
		return "---------------insert_trigger()----------------";
	}
}
