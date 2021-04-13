USE [Sale]
GO
/****** Object:  StoredProcedure [dbo].[speMakerSale01R290_AO5_COM2]    Script Date: 2021/3/8 下午 04:43:38 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER OFF
GO
ALTER PROCEDURE [dbo].[speMakerSale01R290_AO5_COM2]
	@C_OrderDate1		char(10),
	@C_OrderDate2		char(10),
	@C_EnougDate1		char(10),
	@C_EnougDate2		char(10),
	@C_ContrDate1		char(10),
	@C_ContrDate2		char(10),
	@C_DateCheck1		char(10),
	@C_DateCheck2		char(10), 
	@C_DateRange1		char(10),
	@C_DateRange2		char(10),
	@C_Kind			char(4),
	@C_BuyerDate1		char(10),
	@C_BuyerDate2		char(10),
	@C_EDate1		char(10),
	@C_EDate2		char(10),
	@C_SellerCashDate1	char(10),
	@C_SellerCashDate2	char(10),
	@C_BuyerCashDate1	char(10),
	@C_BuyerCashDate2	char(10),
	@C_SaleNET		char(6),
	@C_CompanyNo char(4)
  ,@C_dateType char(10)
AS
BEGIN
	SET NOCOUNT ON
	
	CREATE TABLE #Sale01R290_Table
	(
		SaleID		char(4),
		TRX_DATE_S	char(10),
		TRX_DATE_E	char(10),
		ProjectID	varchar(10),
		SaleName	nvarchar(10),
		TelAmt		float,
		TelTarget	float,
		DSAmt		float,
		DSTarget	float,
		NewAmt		float,
		NewCustomTarget	float,
		FarAmt		float,
		FarFriendTarget float,
		CatchManAmt	float,
		CatchManTarget	float,
		ComeManAmt	float,
		ComeManTarget	float,
		SaleAmt		float,
		SaleTarget	float,
		LastMoney	float,
		BalaMoney	float,
		SaleAmt2	float
	)
	
	DECLARE @C_SQL			varchar(1200),
		@N_TotalCount		Integer,
		@C_OrderDate1_Roc	varchar(8),
		@C_OrderDate2_Roc	varchar(8),
		@C_EMP_NO		char(5),
		@C_SaleID		char(4),
		@C_TRX_DATE_S_AC	char(10),
		@C_TRX_DATE_E_AC	char(10),
		@C_ProjectID		varchar(10),
		@C_EMP_NAME		nvarchar(20),
		@N_TelAmt		float,
		@N_TelTarget		float,
		@N_DSAmt		float,
		@N_DSTarget		float,
		@N_NewAmt		float,
		@N_NewCustomTarget	float,
		@N_FarAmt		float,
		@N_FarFriendTarget	float,
		@N_CatchManAmt		float,
		@N_CatchManTarget	float,
		@N_ComeManAmt		float,
		@N_ComeManTarget	float,
		@N_SaleAmt		float,
		@N_SaleTarget		float,
		@N_LastMoney		float,
		@N_BalaMoney		float,
		@N_SaleAmt2		float
	
	SET @N_TotalCount = 0

	IF @C_dateType = 'OrderDate'
		BEGIN
			SET @C_OrderDate1_Roc = RTRIM(CONVERT(char(3),SUBSTRING(@C_OrderDate1,1,4)-1911)) + CONVERT(char(2),SUBSTRING(@C_OrderDate1,6,2)) + CONVERT(char(2),SUBSTRING(@C_OrderDate1,9,2))		
			SET @C_OrderDate2_Roc = RTRIM(CONVERT(char(3),SUBSTRING(@C_OrderDate2,1,4)-1911)) + CONVERT(char(2),SUBSTRING(@C_OrderDate2,6,2)) + CONVERT(char(2),SUBSTRING(@C_OrderDate2,9,2))
		END
	IF @C_dateType = 'ContrDate'
		BEGIN
			SET @C_OrderDate1_Roc = RTRIM(CONVERT(char(3),SUBSTRING(@C_ContrDate1,1,4)-1911)) + CONVERT(char(2),SUBSTRING(@C_ContrDate1,6,2)) + CONVERT(char(2),SUBSTRING(@C_ContrDate1,9,2))
			SET @C_OrderDate2_Roc = RTRIM(CONVERT(char(3),SUBSTRING(@C_ContrDate2,1,4)-1911)) + CONVERT(char(2),SUBSTRING(@C_ContrDate2,6,2)) + CONVERT(char(2),SUBSTRING(@C_ContrDate2,9,2))
		END

	--SET @C_OrderDate1_Roc = RTRIM(CONVERT(char(3),SUBSTRING(@C_OrderDate1,1,4)-1911)) + CONVERT(char(2),SUBSTRING(@C_OrderDate1,6,2)) + CONVERT(char(2),SUBSTRING(@C_OrderDate1,9,2))
	IF LEN(@C_OrderDate1_Roc) = 6
		BEGIN
			SET @C_OrderDate1_Roc = '0' + @C_OrderDate1_Roc
		END
	--SET @C_OrderDate2_Roc = RTRIM(CONVERT(char(3),SUBSTRING(@C_OrderDate2,1,4)-1911)) + CONVERT(char(2),SUBSTRING(@C_OrderDate2,6,2)) + CONVERT(char(2),SUBSTRING(@C_OrderDate2,9,2))
	IF LEN(@C_OrderDate2_Roc) = 6
		BEGIN
			SET @C_OrderDate2_Roc = '0' + @C_OrderDate2_Roc
		END
	
	CREATE TABLE #FE3D103
	(
		EMP_NO		char(5),
		SaleID		char(4),
		EMP_NAME	nvarchar(20),
		ProjectID	varchar(10)
	)
	INSERT INTO #FE3D103
	(
		EMP_NO,
		SaleID,
		EMP_NAME,
		ProjectID
	)
	SELECT DISTINCT T103.EMP_NO, 
	RTRIM(CASE WHEN LEFT(T103.EMP_NO,1)='B' THEN SUBSTRING(T103.EMP_NO,2,4) 
		   WHEN LEFT(T103.EMP_NO,1)='A' THEN 'A'+SUBSTRING(T103.EMP_NO,3,3) 
		   WHEN LEFT(T103.EMP_NO,1)='C' THEN 'C'+SUBSTRING(T103.EMP_NO,3,3) 
		   WHEN LEFT(T103.EMP_NO,1)='G' THEN 'G'+SUBSTRING(T103.EMP_NO,3,3) 
		   ELSE T103.EMP_NO END) AS SaleID, 
	RTRIM(T5.EMP_NAME) EMP_NAME, 
	(SELECT TOP 1 RTRIM(SUBSTRING(DEPT_CD,4,10)) 
	 FROM FE3D.dbo.FE3D103 
	 WHERE EMP_NO=T103.EMP_NO 
	 AND SUBSTRING(DEPT_CD,4,10) NOT IN ('2','3','31','32','33','34','6','62','63','8','9','92','922','93','96','37')
	 AND (TRX_DATE_S <= @C_OrderDate2_Roc OR TRX_DATE_E <= @C_OrderDate2_Roc) 
	 ORDER BY TRX_DATE_E DESC) ProjectID 
	FROM FE3D.dbo.FE3D103 T103, FE3D.dbo.FE3D05 T5 
	WHERE T103.EMP_NO=T5.EMP_NO 
	AND (T103.DEPT_CD LIKE '%33%' OR T103.DEPT_CD LIKE '%053%') 
	AND SUBSTRING(T103.DEPT_CD,4,10) NOT IN ('2','3','31','32','33','34','6','62','63','8','9','92','922','93','96','37')
	AND ((T103.SALE_LEV <> '體系' AND T103.SALE_LEV <> '企劃' AND T103.SALE_LEV <> '行管' AND T103.SALE_LEV <> '教訓') OR 
	     (T103.SALE_LEV <> '體系' AND T103.SALE_LEV <> '企劃' AND T103.SALE_LEV <> '行管' AND T103.SALE_LEV <> '展業' AND T103.SALE_LEV <> '教訓')) 
	AND ((T103.TRX_DATE_S >= @C_OrderDate1_Roc AND T103.TRX_DATE_S <= @C_OrderDate2_Roc) OR 
	     (T103.TRX_DATE_E >= @C_OrderDate1_Roc AND T103.TRX_DATE_E <= @C_OrderDate2_Roc) OR 
	     (T103.TRX_DATE_S <= @C_OrderDate1_Roc AND T103.TRX_DATE_E >= @C_OrderDate2_Roc) OR 
	     (T103.TRX_DATE_S >= @C_OrderDate1_Roc AND T103.TRX_DATE_E <= @C_OrderDate2_Roc)) 
		ORDER BY T103.EMP_NO
	
	
		CREATE TABLE #A_Daily
	(
		SaleID		char(4),
		DataDate	char(10),
		ProjectID	char(10),
		TelAmt		float,
		DSAmt		float,
		NewAmt		float,
		FarAmt		float,
		CatchManAmt	float,
		ComeManAmt	float
	)
	INSERT INTO #A_Daily
	(
		SaleID,
		DataDate,
		ProjectID,
		TelAmt,
		DSAmt,
		NewAmt,
		FarAmt,
		CatchManAmt,
		ComeManAmt
	)
	SELECT SaleID,
	CONVERT(char(10),DataDate,111) DataDate,
	(CASE WHEN ProjectID='NEWH82A' THEN 'H82A' ELSE ProjectID END),
	TelAmt,
	DSAmt,
	NewAmt,
	FarAmt,
	CatchManAmt,
	ComeManAmt
	FROM A_Daily
	WHERE DataDate BETWEEN @C_OrderDate1 AND @C_OrderDate2
	ORDER BY SaleID, DataDate, (CASE WHEN ProjectID='NEWH82A' THEN 'H82A' ELSE ProjectID END)
		CREATE TABLE #A_Sale
	(
		OrderDate	char(10),
		ProjectID	char(10),
		SaleID1		char(4),
		SaleID2		char(4),
		SaleID3		char(4),
		SaleID4		char(4),
		SaleID5		char(4),
		SaleID6		char(4),
		SaleID7		char(4),
		SaleID8		char(4),
		SaleID9		char(4),
		SaleID10	char(4),
		DealOrPureMoney	float,
		LastMoney	float,
		BalaMoney	float
	)
	SET @C_SQL = "INSERT INTO #A_Sale "+
		     "SELECT CONVERT(char(10),OrderDate,111) OrderDate, "+
		     "ProjectID1, "+
		     "SaleID1, "+
		     "SaleID2, "+
		     "SaleID3, "+
		     "SaleID4, "+
		     "SaleID5, "+
		     "SaleID6, "+
		     "SaleID7, "+
		     "SaleID8, "+
		     "SaleID9, "+
		     "SaleID10, "
	IF RTRIM(@C_Kind) = "售價"	SET @C_SQL = @C_SQL + " H_DealMoney DealOrPureMoney, "
	IF RTRIM(@C_Kind) = "淨售"	SET @C_SQL = @C_SQL + " H_PureMoney DealOrPureMoney, "
	SET @C_SQL = @C_SQL +
		     "H_LastMoney LastMoney, "+
		     "H_BalaMoney BalaMoney "+
		     "FROM A_Sale "+
		     "WHERE (LEN(Position) > 0 OR LEN(Car) > 0 OR LEN(PositionRent) > 0 OR LEN(CarRent) > 0) "
	IF RTRIM(@C_CompanyNo) = "全案"	SET @C_SQL = @C_SQL + "AND  H_Com <> '' "
	IF RTRIM(@C_CompanyNo) = "代銷"	SET @C_SQL = @C_SQL + "AND  H_Com <> '8' "
	IF RTRIM(@C_CompanyNo) = "建設"	SET @C_SQL = @C_SQL + "AND  H_Com =  '3' "
	IF RTRIM(@C_CompanyNo) = "人壽"	SET @C_SQL = @C_SQL + "AND  H_Com =  '8' "
	--2021 Kyle : EMK20210115004 start
	IF LEN(@C_OrderDate1) > 0  SET @C_SQL = @C_SQL + "AND OrderDate BETWEEN '" + @C_OrderDate1 + "' AND '" + @C_OrderDate2 + "' "
	IF LEN(@C_ContrDate1) > 0  SET @C_SQL = @C_SQL + "AND ContrDate BETWEEN '" + @C_ContrDate1 + "' AND '" + @C_ContrDate2 + "' "
	--2021 Kyle : EMK20210115004 end
	IF LEN(@C_EnougDate1) > 0	SET @C_SQL = @C_SQL + " AND EnougDate >= '" + @C_EnougDate1 + "' "
	IF LEN(@C_EnougDate2) > 0	SET @C_SQL = @C_SQL + " AND EnougDate <= '" + @C_EnougDate2 + "' "
	IF LEN(@C_ContrDate1) > 0	SET @C_SQL = @C_SQL + " AND ContrDate >= '" + @C_ContrDate1 + "' "
	IF LEN(@C_ContrDate2) > 0	SET @C_SQL = @C_SQL + " AND ContrDate <= '" + @C_ContrDate2 + "' "
	IF LEN(@C_DateCheck1) > 0	SET @C_SQL = @C_SQL + " AND DateCheck >= '" + @C_DateCheck1 + "' "
	IF LEN(@C_DateCheck2) > 0	SET @C_SQL = @C_SQL + " AND DateCheck <= '" + @C_DateCheck2 + "' "
	IF LEN(@C_DateRange1) > 0	SET @C_SQL = @C_SQL + " AND DateRange >= '" + @C_DateRange1 + "' "
	IF LEN(@C_DateRange2) > 0	SET @C_SQL = @C_SQL + " AND DateRange <= '" + @C_DateRange2 + "' "
	
	SET @C_SQL = @C_SQL + 
				 " UNION ALL " +
				 "SELECT CONVERT(char(10),OrderDate,111) OrderDate, "+
		     "ProjectID1, "+
		     "SaleID1, "+
		     "SaleID2, "+
		     "SaleID3, "+
		     "SaleID4, "+
		     "SaleID5, "+
		     "SaleID6, "+
		     "SaleID7, "+
		     "SaleID8, "+
		     "SaleID9, "+
		     "SaleID10, "
	IF RTRIM(@C_Kind) = "售價"	SET @C_SQL = @C_SQL + " L_DealMoney DealOrPureMoney, "
	IF RTRIM(@C_Kind) = "淨售"	SET @C_SQL = @C_SQL + " L_PureMoney DealOrPureMoney, "
	SET @C_SQL = @C_SQL +
		     "L_LastMoney LastMoney, "+
		     "L_BalaMoney BalaMoney  "+
		     "FROM A_Sale "+
		     "WHERE (LEN(Position) > 0 OR LEN(Car) > 0 OR LEN(PositionRent) > 0 OR LEN(CarRent) > 0) "
	IF RTRIM(@C_CompanyNo) = "全案"	SET @C_SQL = @C_SQL + "AND  L_Com <> '' "
	IF RTRIM(@C_CompanyNo) = "代銷"	SET @C_SQL = @C_SQL + "AND  L_Com <> '8' "
	IF RTRIM(@C_CompanyNo) = "建設"	SET @C_SQL = @C_SQL + "AND  L_Com =  '3' "
	IF RTRIM(@C_CompanyNo) = "人壽"	SET @C_SQL = @C_SQL + "AND  L_Com =  '8' "
	--SET @C_SQL = @C_SQL + "AND OrderDate BETWEEN '" + @C_OrderDate1 + "' AND '" + @C_OrderDate2 + "' "
	--2021 Kyle : EMK20210115004 start
	IF LEN(@C_OrderDate1) > 0  SET @C_SQL = @C_SQL + "AND OrderDate BETWEEN '" + @C_OrderDate1 + "' AND '" + @C_OrderDate2 + "' "
	IF LEN(@C_ContrDate1) > 0  SET @C_SQL = @C_SQL + "AND ContrDate BETWEEN '" + @C_ContrDate1 + "' AND '" + @C_ContrDate2 + "' "
	--2021 Kyle : EMK20210115004 end
	IF LEN(@C_EnougDate1) > 0	SET @C_SQL = @C_SQL + " AND EnougDate >= '" + @C_EnougDate1 + "' "
	IF LEN(@C_EnougDate2) > 0	SET @C_SQL = @C_SQL + " AND EnougDate <= '" + @C_EnougDate2 + "' "
	IF LEN(@C_ContrDate1) > 0	SET @C_SQL = @C_SQL + " AND ContrDate >= '" + @C_ContrDate1 + "' "
	IF LEN(@C_ContrDate2) > 0	SET @C_SQL = @C_SQL + " AND ContrDate <= '" + @C_ContrDate2 + "' "
	IF LEN(@C_DateCheck1) > 0	SET @C_SQL = @C_SQL + " AND DateCheck >= '" + @C_DateCheck1 + "' "
	IF LEN(@C_DateCheck2) > 0	SET @C_SQL = @C_SQL + " AND DateCheck <= '" + @C_DateCheck2 + "' "
	IF LEN(@C_DateRange1) > 0	SET @C_SQL = @C_SQL + " AND DateRange >= '" + @C_DateRange1 + "' "
	IF LEN(@C_DateRange2) > 0	SET @C_SQL = @C_SQL + " AND DateRange <= '" + @C_DateRange2 + "' "
	
	
	SET @C_SQL = @C_SQL + " ORDER BY OrderDate, ProjectID1"
	EXEC(@C_SQL)
		CREATE TABLE #A_AgencySellerSale
	(
		SalesNo		char(4),
		BuyerDate	char(10),
		ProjectID1	char(10),
		SaleOrNETSale	float
	)
	SET @C_SQL = "INSERT INTO #A_AgencySellerSale "+
		     "SELECT T52.SalesNo, "+
		     "T50.BuyerDate, "+
		     "T50.ProjectID1, "
	IF RTRIM(@C_SaleNET) = "業績"	SET @C_SQL = @C_SQL + " Sale SaleOrNETSale "
	IF RTRIM(@C_SaleNET) = "淨業績"	SET @C_SQL = @C_SQL + " NETSale SaleOrNETSale "
	SET @C_SQL = @C_SQL + 
		     "FROM Sale02M050 T50, Sale02M052 T52 "+
		     "WHERE T50.AgencyNo=T52.AgencyNo "+
		     "AND T50.BuyerDate BETWEEN '" + @C_BuyerDate1 + "' AND '" + @C_BuyerDate2 + "' "
	IF LEN(@C_EDate1) > 0		SET @C_SQL = @C_SQL + " AND T50.EDate >= '" + @C_EDate1 + "' "
	IF LEN(@C_EDate2) > 0		SET @C_SQL = @C_SQL + " AND T50.EDate <= '" + @C_EDate2 + "' "
	IF LEN(@C_SellerCashDate1) > 0	SET @C_SQL = @C_SQL + " AND T50.SellerCashDate >= '" + @C_SellerCashDate1 + "' "
	IF LEN(@C_SellerCashDate2) > 0	SET @C_SQL = @C_SQL + " AND T50.SellerCashDate <= '" + @C_SellerCashDate2 + "' "
	IF LEN(@C_BuyerCashDate1) > 0	SET @C_SQL = @C_SQL + " AND T50.BuyerCashDate >= '" + @C_BuyerCashDate1 + "' "
	IF LEN(@C_BuyerCashDate2) > 0	SET @C_SQL = @C_SQL + " AND T50.BuyerCashDate <= '" + @C_BuyerCashDate2 + "' "
	SET @C_SQL = @C_SQL + " ORDER BY SalesNo, BuyerDate, ProjectID1"
	EXEC(@C_SQL)
		CREATE TABLE #A_AgencyBuyerSale
	(
		SalesNo		char(4),
		BuyerDate	char(10),
		ProjectID1	char(10),
		SaleOrNETSale	float
	)
	SET @C_SQL = "INSERT INTO #A_AgencyBuyerSale "+
		     "SELECT T53.SalesNo, "+
		     "T50.BuyerDate, "+
		     "T50.ProjectID1, "
	IF RTRIM(@C_SaleNET) = "業績"	SET @C_SQL = @C_SQL + " Sale SaleOrNETSale "
	IF RTRIM(@C_SaleNET) = "淨業績"	SET @C_SQL = @C_SQL + " NETSale SaleOrNETSale "
	SET @C_SQL = @C_SQL + 
		     "FROM Sale02M050 T50, Sale02M053 T53 "+
		     "WHERE T50.AgencyNo=T53.AgencyNo "+
		     "AND T50.BuyerDate BETWEEN '" + @C_BuyerDate1 + "' AND '" + @C_BuyerDate2 + "' "
	IF LEN(@C_EDate1) > 0		SET @C_SQL = @C_SQL + " AND T50.EDate >= '" + @C_EDate1 + "' "
	IF LEN(@C_EDate2) > 0		SET @C_SQL = @C_SQL + " AND T50.EDate <= '" + @C_EDate2 + "' "
	IF LEN(@C_SellerCashDate1) > 0	SET @C_SQL = @C_SQL + " AND T50.SellerCashDate >= '" + @C_SellerCashDate1 + "' "
	IF LEN(@C_SellerCashDate2) > 0	SET @C_SQL = @C_SQL + " AND T50.SellerCashDate <= '" + @C_SellerCashDate2 + "' "
	IF LEN(@C_BuyerCashDate1) > 0	SET @C_SQL = @C_SQL + " AND T50.BuyerCashDate >= '" + @C_BuyerCashDate1 + "' "
	IF LEN(@C_BuyerCashDate2) > 0	SET @C_SQL = @C_SQL + " AND T50.BuyerCashDate <= '" + @C_BuyerCashDate2 + "' "
	SET @C_SQL = @C_SQL + " ORDER BY SalesNo, BuyerDate, ProjectID1"
	EXEC(@C_SQL)
	
	
		CREATE TABLE #A_WTarMM
	(
		UserID		varchar(10),
		YearMM		char(7),
		ProjectID	varchar(10),
		TelTarget	float,
		DSTarget	float,
		NewCustomTarget	float,
		FarFriendTarget	float,
		CatchmanTarget	float,
		ComeManTarget	float
	)
	INSERT INTO #A_WTarMM
	(
		UserID,
		YearMM,
		ProjectID,
		TelTarget,
		DSTarget,
		NewCustomTarget,
		FarFriendTarget,
		CatchmanTarget,
		ComeManTarget
	)
	SELECT UserID,
	CONVERT(char(7),YearMM,111) YearMM,
	ProjectID,
	Tel,
	DS,
	NewCustom,
	FarFriend,
	Catchman,
	ComeMan
	FROM A_WTarMM
	WHERE YearMM BETWEEN @C_OrderDate1 AND @C_OrderDate2
	ORDER BY UserID, YearMM, ProjectID
		CREATE TABLE #A_STarMM
	(
		UserID		varchar(10),
		YearMM		char(7),
		ProjectID	varchar(10),
		Targets		float
	)
	INSERT INTO #A_STarMM
	(
		UserID,
		YearMM,
		ProjectID,
		Targets
	)
	SELECT UserID,
	CONVERT(char(7),YearMM,111) YearMM,
	ProjectID,
	Targets
	FROM A_STarMM
	WHERE YearMM BETWEEN @C_OrderDate1 AND @C_OrderDate2
	ORDER BY UserID, YearMM, ProjectID
	
	
	DECLARE curFlow0 INSENSITIVE CURSOR FOR
	SELECT *
	FROM #FE3D103
	ORDER BY EMP_NO
	FOR READ ONLY
		OPEN curFlow0
	WHILE 1 = 1
		BEGIN
			FETCH NEXT FROM curFlow0 INTO	@C_EMP_NO,
							@C_SaleID,
							@C_EMP_NAME,
							@C_ProjectID
			IF @@FETCH_STATUS <> 0 BREAK
						DECLARE curFlow1 INSENSITIVE CURSOR FOR
			SELECT ISNULL(SUM(TelAmt),0) TelAmt,
			ISNULL(SUM(DSAmt),0) DSAmt,
			ISNULL(SUM(NewAmt),0) NewAmt,
			ISNULL(SUM(FarAmt),0) FarAmt,
			ISNULL(SUM(CatchManAmt),0) CatchManAmt,
			ISNULL(SUM(ComeManAmt),0) ComeManAmt
			FROM #A_Daily
			WHERE SaleID = @C_SaleID
			AND DataDate BETWEEN @C_OrderDate1 AND @C_OrderDate2
			FOR READ ONLY
						OPEN curFlow1
			WHILE 1 = 1
				BEGIN
					FETCH NEXT FROM curFlow1 INTO	@N_TelAmt,
									@N_DSAmt,
									@N_NewAmt,
									@N_FarAmt,
									@N_CatchManAmt,
									@N_ComeManAmt
					IF @@FETCH_STATUS <> 0 BREAK
				END
			CLOSE curFlow1
			DEALLOCATE curFlow1
						DECLARE curFlow4 INSENSITIVE CURSOR FOR
			SELECT ISNULL(SUM(DealOrPureMoney * 
							  ((CASE WHEN SaleID1 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID2 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID3 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID4 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID5 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID6 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID7 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID8 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID9 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID10 = @C_SaleID THEN 1 ELSE 0 END)) / 
							  ((CASE WHEN LEN(SaleID1)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID2)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID3)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID4)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID5)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID6)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID7)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID8)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID9)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID10)>0 THEN 1 ELSE 0 END))),0) DealOrPureMoney, 
				   ISNULL(SUM(LastMoney * 
							  ((CASE WHEN SaleID1 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID2 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID3 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID4 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID5 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID6 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID7 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID8 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID9 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID10 = @C_SaleID THEN 1 ELSE 0 END)) / 
							  ((CASE WHEN LEN(SaleID1)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID2)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID3)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID4)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID5)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID6)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID7)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID8)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID9)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID10)>0 THEN 1 ELSE 0 END))),0) LastMoney, 
				   ISNULL(SUM(BalaMoney * 
							  ((CASE WHEN SaleID1 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID2 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID3 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID4 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID5 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID6 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID7 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID8 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID9 = @C_SaleID THEN 1 ELSE 0 END)+
							   (CASE WHEN SaleID10 = @C_SaleID THEN 1 ELSE 0 END)) / 
							  ((CASE WHEN LEN(SaleID1)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID2)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID3)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID4)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID5)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID6)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID7)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID8)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID9)>0 THEN 1 ELSE 0 END)+
							   (CASE WHEN LEN(SaleID10)>0 THEN 1 ELSE 0 END))),0) BalaMoney
			FROM #A_Sale
			WHERE @C_SaleID IN (SaleID1, SaleID2, SaleID3, SaleID4, SaleID5, SaleID6, SaleID7, SaleID8, SaleID9, SaleID10)
			--AND OrderDate BETWEEN @C_OrderDate1 AND @C_OrderDate2
			--2021/03/10 Kyle : EMK20210115004 S
			-- AND ( LEN(@C_OrderDate1) > 0 AND OrderDate BETWEEN @C_OrderDate1 AND @C_OrderDate2 ) 
			-- AND ( LEN(@C_ContrDate1) > 0 AND ContrDate BETWEEN @C_ContrDate1 AND @C_ContrDate2 ) 
			--2021/03/10 Kyle : EMK20210115004 E
			FOR READ ONLY
						OPEN curFlow4
			WHILE 1 = 1
				BEGIN
					FETCH NEXT FROM curFlow4 INTO	@N_SaleAmt,
									@N_LastMoney,
									@N_BalaMoney
					IF @@FETCH_STATUS <> 0 BREAK
				END
			CLOSE curFlow4
			DEALLOCATE curFlow4
						DECLARE curFlow5 INSENSITIVE CURSOR FOR
			SELECT ISNULL(SUM(SaleOrNETSale),0) SaleOrNETSale
			FROM(SELECT SaleOrNETSale
			     FROM #A_AgencySellerSale
			     WHERE SalesNo = @C_SaleID
			     AND BuyerDate BETWEEN @C_OrderDate1 AND @C_OrderDate2
			     UNION ALL
			     SELECT SaleOrNETSale
			     FROM #A_AgencyBuyerSale
			     WHERE SalesNo = @C_SaleID
			     AND BuyerDate BETWEEN @C_OrderDate1 AND @C_OrderDate2) TempData
			FOR READ ONLY
						OPEN curFlow5
			WHILE 1 = 1
				BEGIN
					FETCH NEXT FROM curFlow5 INTO @N_SaleAmt2
					IF @@FETCH_STATUS <> 0 BREAK
				END
			CLOSE curFlow5
			DEALLOCATE curFlow5
						DECLARE curFlow6 INSENSITIVE CURSOR FOR
			SELECT ISNULL(SUM(TelTarget),0) TelTarget,
			       ISNULL(SUM(DSTarget),0) DSTarget,
			       ISNULL(SUM(NewCustomTarget),0) NewCustomTarget,
			       ISNULL(SUM(FarFriendTarget),0) FarFriendTarget,
			       ISNULL(SUM(CatchmanTarget),0) CatchmanTarget,
			       ISNULL(SUM(ComeManTarget),0) ComeManTarget
			FROM #A_WTarMM
			WHERE UserID = @C_SaleID
			AND YearMM BETWEEN SUBSTRING(@C_OrderDate1,1,7) AND SUBSTRING(@C_OrderDate2,1,7)
			FOR READ ONLY
						OPEN curFlow6
			WHILE 1 = 1
				BEGIN
					FETCH NEXT FROM curFlow6 INTO	@N_TelTarget,
									@N_DSTarget,
									@N_NewCustomTarget,
									@N_FarFriendTarget,
									@N_CatchmanTarget,
									@N_ComeManTarget
					IF @@FETCH_STATUS <> 0 BREAK
				END
			CLOSE curFlow6
			DEALLOCATE curFlow6
						DECLARE curFlow7 INSENSITIVE CURSOR FOR
			SELECT ISNULL(SUM(Targets),0) Targets
			FROM #A_STarMM
			WHERE UserID = @C_SaleID
			AND YearMM BETWEEN SUBSTRING(@C_OrderDate1,1,7) AND SUBSTRING(@C_OrderDate2,1,7)
			FOR READ ONLY
						OPEN curFlow7
			WHILE 1 = 1
				BEGIN
					FETCH NEXT FROM curFlow7 INTO @N_SaleTarget
					IF @@FETCH_STATUS <> 0 BREAK
				END
			CLOSE curFlow7
			DEALLOCATE curFlow7
						INSERT
			INTO #Sale01R290_Table
			(
				SaleID,
				TRX_DATE_S,
				TRX_DATE_E,
				ProjectID,
				SaleName,
				TelAmt,
				TelTarget,
				DSAmt,
				DSTarget,
				NewAmt,
				NewCustomTarget,
				FarAmt,
				FarFriendTarget,
				CatchManAmt,
				CatchManTarget,
				ComeManAmt,
				ComeManTarget,
				SaleAmt,
				SaleTarget,
				LastMoney,
				BalaMoney,
				SaleAmt2
			)
			VALUES
			(
				@C_SaleID,
				@C_TRX_DATE_S_AC,
				@C_TRX_DATE_E_AC,
				@C_ProjectID,
				@C_EMP_NAME,
				@N_TelAmt,
				@N_TelTarget,
				@N_DSAmt,
				@N_DSTarget,
				@N_NewAmt,
				@N_NewCustomTarget,
				@N_FarAmt,
				@N_FarFriendTarget,
				@N_CatchManAmt,
				@N_CatchManTarget,
				@N_ComeManAmt,
				@N_ComeManTarget,
				@N_SaleAmt,
				@N_SaleTarget,
				@N_LastMoney,
				@N_BalaMoney,
				@N_SaleAmt2
			)
		END
	CLOSE curFlow0
	DEALLOCATE curFlow0
	
	
	DECLARE curFlowCounts INSENSITIVE CURSOR FOR
	SELECT COUNT(*) FROM #Sale01R290_Table
	FOR READ ONLY
		OPEN curFlowCounts
	WHILE 1 = 1
		BEGIN
			FETCH NEXT FROM curFlowCounts INTO @N_TotalCount
			IF @@FETCH_STATUS <> 0 BREAK
		END
	CLOSE curFlowCounts
	DEALLOCATE curFlowCounts
		SELECT ROW_NUMBER() OVER (ORDER BY (case when SaleTarget=0 then 0 else SaleAmt/SaleTarget end) desc, SaleID) AS SerialNo, 
		   ProjectID, 
		   SaleName, 
		   TelAmt, 
		   TelTarget, 
		   (CASE WHEN TelTarget=0 THEN 0 ELSE TelAmt/TelTarget END) TelRate, 
		   (CONVERT(varchar,RANK() OVER (ORDER BY (CASE WHEN TelTarget=0 THEN 0 ELSE TelAmt/TelTarget END) DESC))+'/'+CONVERT(varchar,@N_TotalCount)) TelRank, 
		   DSAmt, 
		   DSTarget, 
		   (CASE WHEN DSTarget=0 THEN 0 ELSE DSAmt/DSTarget END) DSRate, 
		   (CONVERT(varchar,RANK() OVER (ORDER BY (CASE WHEN DSTarget=0 THEN 0 ELSE DSAmt/DSTarget END) DESC))+'/'+CONVERT(varchar,@N_TotalCount)) DSRank, 
		   NewAmt, 
		   NewCustomTarget, 
		   (CASE WHEN NewCustomTarget=0 THEN 0 ELSE NewAmt/NewCustomTarget END) NewRate, 
		   (CONVERT(varchar,RANK() OVER (ORDER BY (CASE WHEN NewCustomTarget=0 THEN 0 ELSE NewAmt/NewCustomTarget END) DESC))+'/'+CONVERT(varchar,@N_TotalCount)) NewRank, 
		   FarAmt, 
		   FarFriENDTarget, 
		   (CASE WHEN FarFriENDTarget=0 THEN 0 ELSE FarAmt/FarFriENDTarget END) FarRate, 
		   (CONVERT(varchar,RANK() OVER (ORDER BY (CASE WHEN FarFriENDTarget=0 THEN 0 ELSE FarAmt/FarFriENDTarget END) DESC))+'/'+CONVERT(varchar,@N_TotalCount)) FarRank, 
		   CatchManAmt, 
		   CatchManTarget, 
		   (CASE WHEN CatchManTarget=0 THEN 0 ELSE CatchManAmt/CatchManTarget END) CatchManRate, 
		   (CONVERT(varchar,RANK() OVER (ORDER BY (CASE WHEN CatchManTarget=0 THEN 0 ELSE CatchManAmt/CatchManTarget END) DESC))+'/'+CONVERT(varchar,@N_TotalCount)) CatchManRank, 
		   ComeManAmt, 
		   ComeManTarget, 
		   (CASE WHEN ComeManTarget=0 THEN 0 ELSE ComeManAmt/ComeManTarget END) ComeManRate, 
		   (CONVERT(varchar,RANK() OVER (ORDER BY (CASE WHEN ComeManTarget=0 THEN 0 ELSE ComeManAmt/ComeManTarget END) DESC))+'/'+CONVERT(varchar,@N_TotalCount)) ComeManRank, 
		   SaleAmt, 
		   SaleTarget, 
		   (CASE WHEN SaleTarget=0 THEN 0 ELSE SaleAmt/SaleTarget END) SaleRate, 
		   (CONVERT(varchar,RANK() OVER (ORDER BY (CASE WHEN SaleTarget=0 THEN 0 ELSE SaleAmt/SaleTarget END) DESC))+'/'+CONVERT(varchar,@N_TotalCount)) SaleRank, 
		   LastMoney, 
		   BalaMoney, 
		   SaleAmt2 
	FROM #Sale01R290_Table
	WHERE ProjectID in (SELECT ProjectID1 FROM A_Group WHERE PrintOut = 'Y') 
	ORDER BY SaleRate DESC, SaleID
	
	
	DROP TABLE #Sale01R290_Table
	DROP TABLE #A_Daily
	DROP TABLE #A_Sale
	DROP TABLE #A_AgencySellerSale
	DROP TABLE #A_AgencyBuyerSale
	DROP TABLE #A_WTarMM
	DROP TABLE #A_STarMM
	
	SET NOCOUNT OFF
	SELECT -1
	RETURN -1
	ONERROR:
		SET NOCOUNT OFF
		SELECT 0
	
	SET ANSI_NULLS ON
	SET QUOTED_IDENTIFIER OFF
END