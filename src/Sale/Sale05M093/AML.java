package Sale.Sale05M093;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import Farglory.util.KUtils;
import Farglory.util.QueryLogBean;
import jcx.db.talk;
import jcx.jform.bproc;

public class AML extends bproc {
  public String getDefaultValue(String value) throws Throwable {
    // 2019/12/28 �~�� ���ަW��LOG
    System.out.println("==============�~�����v�ˮ�LOG START====================================");
    KUtils kutil = new KUtils();
    talk dbSale05M = getTalk("Sale");
    talk dbJGENLIB = getTalk("JGENLIB");
    talk dbEIP = getTalk("EIP");
    
    String stringSQL = "";
    String strBDaysql = "";
    String errMsg = "";
    String[][] retPat001;
    String[][] retQueryLog;
    String[][] tb2_string = getTableData("table2");
    String[][] tb5_string = getTableData("table5");
    String[][] tb6_string = getTableData("table6");
    String strOrderNo = getValue("OrderNo").trim();
    String stringProjectID1 = getValue("ProjectID1").trim();
    String stringOrderDate = getValue("TrxDate").trim();
    String strActionName = "�s��";
    System.out.println("strOrderNo=====>" + strOrderNo);
    System.out.println("stringProjectID1=====>" + stringProjectID1);
    System.out.println("stringOrderDate=====>" + stringOrderDate);
    System.out.println("strActionName=====>" + strActionName);
    
    // LOG���,�ɶ�
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    String strNowDate = nowsdf.format(now);
    String tempROCYear = "" + (Integer.parseInt(strNowDate.substring(0, strNowDate.length() - 4)) - 1911);
    String RocNowDate = tempROCYear + strNowDate.substring(strNowDate.length() - 4, strNowDate.length());
    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HHmmss");
    String strNowTime = nowTimeSdf.format(now);
    SimpleDateFormat nowTimestampSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String strNowTimestamp = nowTimestampSdf.format(now);
    System.out.println("strNowTimestamp=====>" + strNowTimestamp);
    System.out.println("RocNowDate=====>" + RocNowDate);
    System.out.println("strNowTime=====>" + strNowTime);
    
    // ���u�X
    String userNo = getUser().toUpperCase().trim();
    String empNo = "";
    String[][] retEip = null;
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    }
    System.out.println("empNo=====>" + empNo);
    
    // �Ǹ�
    int intRecordNo = 1;
    String[][] ret05M070;
    stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + getValue("OrderNo").trim() + "'";
    ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
    
    // actionNo
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    String strActionNo = strNowDate + strNowTime + ram;
    System.out.println("strActionNo=====>" + strActionNo);

    // ��RY773�~���A��
    Map mapAMLMsg = kutil.getAMLDesc();

    System.out.println("==============�Ȥ���==============");
    if (tb2_string.length > 0) {
      for (int m = 0; m < tb2_string.length; m++) {// �P�@�q��U
        String strCountryName = tb2_string[m][5].trim();
        String strCustomNo = tb2_string[m][6].trim();
        String strCustomName = tb2_string[m][7].trim();
        String strIsBlackList = tb2_string[m][20].trim();
        String strIsControlList = tb2_string[m][21].trim();
        String strIsLinked = tb2_string[m][22].trim();
        String strBirthday = tb2_string[m][9].trim();

        // ���A��LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16 };
        for (int ii = 0; ii < noUseAML.length; ii++) {
          String amlNo = "";
          if (noUseAML[ii] < 10) {
            amlNo = "00" + noUseAML[ii];
          } else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString().replaceAll("<customName>", strCustomName).replaceAll("<customTitle>", "�Ȥ�").replaceAll("<customName2>", "")
              .replaceAll("<customTitle2>", "");
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','�ʫ��ҩ���','�Ȥ���','" + strActionName + "','���A��','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        //TODO: �D�n�Ȥ� 18.��� & 21.PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = strBirthday.length() == 0 ? " " : strBirthday.toString().replace("-", "");
        String indCode = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
        String amlText = stringProjectID1 + "," + strOrderNo + "," + stringOrderDate + "," + getFunctionName() + "," + "�Ȥ�����PEPS" 
            + "," + strCustomNo + "," + strCustomName + "," + birth + "," + indCode + "," + "query1821";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

        // �ꮣ�a��
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("�u���k��".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','�Ȥ�" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + getValue("OrderNo").trim() + "', '"
                + RocNowDate + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '�Ȥ�Y�ӦۥD�޾����Ҥ��i����~���P�����ꮣ���Y���ʥ�����a�Φa�ϡA�Ψ�L����`�Υ��R����`����a�Φa�ϡA���ˮ֨�X�z�ʡC','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            if ("".equals(errMsg)) {
              errMsg = "�Ȥ�" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            } else {
              errMsg = errMsg + "\n�Ȥ�" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            }
          } else {
            // ���ŦX
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','�Ȥ�" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }

        // ����+�¦W��
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','�Ȥ�" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '�ӫȤᬰ�æ��¦W���H�A�а���[�j���Ȥ��¾�f�d�ę̀���~�������q���@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�Ȥ�" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          } else {
            errMsg = errMsg + "\n�Ȥ�" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','�Ȥ�" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        // �Q�`���Y�H
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','�Ȥ�" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','�Ȥ�" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '�ӫȤᬰ���q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�Ȥ�" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          } else {
            errMsg = errMsg + "\n�Ȥ�" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�Ȥ���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','�Ȥ�" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

      } // for end
    }
    System.out.println("============================");

    System.out.println("==============�����q�H==============");
    if (tb5_string.length > 0) {// �����q�H START
      for (int m = 0; m < tb5_string.length; m++) {// �P�@�q��U
        String strCountryName = tb5_string[m][6].trim();
        String strCustomNo = tb5_string[m][4].trim();
        String strCustomName = tb5_string[m][3].trim();
        String strIsBlackList = tb5_string[m][8].trim();
        String strIsControlList = tb5_string[m][9].trim();
        String strIsLinked = tb5_string[m][10].trim();
        String strBirthday = tb5_string[m][5].trim();

        // ���A��LOG1~8, 10~16
        int[] noUseAML = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        for (int ii = 0; ii < noUseAML.length; ii++) {
          String amlNo = "";
          if (noUseAML[ii] < 10) {
            amlNo = "00" + noUseAML[ii];
          } else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                            .replaceAll("<customName>", strCustomName).replaceAll("<customTitle>", "�Ȥ�")
                            .replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','�ʫ��ҩ���','�����q�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        //TODO: �����q�H���PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = strBirthday.length() == 0 ? " " : strBirthday.toString().replace("-", "");
        String indCode = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();
        String amlText = stringProjectID1 + "," + strOrderNo + "," + stringOrderDate + "," + getFunctionName() + "," + "�Ȥ�����PEPS" 
            + "," + strCustomNo + "," + strCustomName + "," + birth + "," + indCode + "," + "query1821";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

        // �ꮣ�a��
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          if ("�u���k��".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','�����q�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '�Ȥ�Y�ӦۥD�޾����Ҥ��i����~���P�����ꮣ���Y���ʥ�����a�Φa�ϡA�Ψ�L����`�Υ��R����`����a�Φa�ϡA���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate
                + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            if ("".equals(errMsg)) {
              errMsg = "�����q�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            } else {
              errMsg = errMsg + "\n�����q�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C";
            }
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','009','�����q�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
              + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // �¦W�� ���ަW��
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','�����q�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '�ӫȤᬰ�æ��¦W���H�A�а���[�j���Ȥ��¾�f�d�ę̀���~�������q���@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�����q�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          } else {
            errMsg = errMsg + "\n�����q�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','�����q�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
        // �Q�`���Y�H
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','�����q�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','�����q�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '�ӫȤᬰ���q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          if ("".equals(errMsg)) {
            errMsg = "�����q�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          } else {
            errMsg = errMsg + "\n�����q�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C";
          }
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�����q�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','�����q�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
      
      } // for end
    } // �����q�H END
    System.out.println("============================");

    System.out.println("==============�N�z�H==============");
    // stringSQL = "SELECT
    // CountryName,ACustomNo,AgentName,IsBlackList,IsControlList,IsLinked, AgentRel
    // FROM Sale05M091Agent WHERE ORDERNO = '"+strOrderNo+"'";
    // ret05M070 = dbSale05M.queryFromPool(stringSQL);
    if (tb6_string.length > 0) {// �N�z�H START
      for (int m = 0; m < tb6_string.length; m++) {// �P�@�q��U
        String strCountryName = tb6_string[m][5].trim();
        String strCustomNo = tb6_string[m][4].trim();
        String strOrderCustomName = tb6_string[m][2].trim();
        String strCustomName = tb6_string[m][3].trim();
        String strIsBlackList = tb6_string[m][8].trim();
        String strIsControlList = tb6_string[m][9].trim();
        String strIsLinked = tb6_string[m][10].trim();
        String strAgentRel = tb6_string[m][6].trim();

        String custSA = "�Ȥ�" + strOrderCustomName + "��";
        if (m == 0)
          errMsg += "\n";

        // ���A��LOG1~4 ,6 ,7 ,10~16
        int[] noUseAML = { 1, 2, 3, 4, 6, 7, 8, 10, 11 ,12 ,13 ,14 ,15 ,16};
        for (int ii = 0; ii < noUseAML.length; ii++) {
          String amlNo = "";
          if (noUseAML[ii] < 10) {
            amlNo = "00" + noUseAML[ii];
          } else {
            amlNo = "0" + noUseAML[ii];
          }
          String amlDesc = mapAMLMsg.get(amlNo).toString()
                            .replaceAll("<customName>", strCustomName).replaceAll("<customTitle>", "�Ȥ�")
                            .replaceAll("<customName2>", "").replaceAll("<customTitle2>", "");
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','�ʫ��ҩ���','�N�z�H���','" + strActionName + "','���A��','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773', '" + amlNo + "' , '" + amlDesc + "','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        //TODO: �N�z�H���PEPS
        QueryLogBean qBean = kutil.getQueryLogByCustNoProjectId(stringProjectID1, strCustomNo);
        String birth = qBean.getBirthday().length() == 0 ? " " : qBean.getBirthday().toString().replace("-", "");
        String indCode = qBean.getJobType().length() == 0 ? " " : qBean.getJobType();  
        String amlText = stringProjectID1 + "," + strOrderNo + "," + stringOrderDate + "," + getFunctionName() + "," + "�Ȥ�����PEPS" 
            + "," + strCustomNo + "," + strCustomName + "," + birth + "," + indCode + "," + "query1821";
        setValue("AMLText" , amlText);
        getButton("BtCustAML").doClick();
        errMsg += getValue("AMLText").trim();

        // �~���ĤK��
        stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
            + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
            + "�N����z���ʲ�����A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','008','�N�z�H" + strCustomName
            + "�N����z���ʲ�����A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbSale05M.execFromPool(stringSQL);
        intRecordNo++;
        // AS400
        stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate + "', '"
            + strCustomNo + "', '" + strCustomName + "', '773', '008', '���ʲ��P��ѲĤT��N�z��ú�ڡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
        dbJGENLIB.execFromPool(stringSQL);
        errMsg += custSA + "�N�z�H" + strCustomName + "�N����z�䤣�ʲ�����A�Ш̬~���θꮣ����@�~��z�C\n";


        // �ꮣ�a��
        stringSQL = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCountryName + "'";
        retPat001 = dbJGENLIB.queryFromPool(stringSQL);
        if (retPat001.length > 0) {
          String strCZ07 = retPat001[0][0].trim();
          System.out.println("strCZ07==>" + strCZ07);
          if ("�u���k��".equals(strCZ07)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','009','�N�z�H" + strCustomName
                + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '009', '�Ȥ�Y�ӦۥD�޾����Ҥ��i����~���P�����ꮣ���Y���ʥ�����a�Φa�ϡA�Ψ�L����`�Υ��R����`����a�Φa�ϡA���ˮ֨�X�z�ʡC','" + empNo + "','" + RocNowDate
                + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            errMsg += custSA + "�N�z�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�Ш̬~���θꮣ����@�~��z�C\n";
          } else {
            // ���ŦX
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','009','�N�z�H" + strCustomName + "�Y�Ӧ۬~���θꮣ����Y���ʥ��B����`�Υ��R����`����a�Φa��,�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }

        // ���Y
        if (!"".equals(strAgentRel)) {
          if ("�B��".equals(strAgentRel) || "��L".equals(strAgentRel)) {
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName + "�P�Ȥ�"
                + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','005','�N�z�H" + strCustomName
                + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
            // AS400
            stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
                + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '005', '�Nú�ڤH�P�ʶR�H���Y���D�G���ˤ���/�ÿˡA�t���ˮִ��ܳq���C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
            dbJGENLIB.execFromPool(stringSQL);
            errMsg += custSA + "�N�z�H" + strCustomName + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C\n";
          } else {
            // ���ŦX
            stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
                + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
                + strCustomName + "','" + stringOrderDate + "','RY','773','005','�N�z�H" + strCustomName + "�P�Ȥ�" + strOrderCustomName + "�D�G�˵����������Y�A�Ш̬~���θꮣ����@�~��z�C','" + empNo + "','"
                + RocNowDate + "','" + strNowTime + "')";
            dbSale05M.execFromPool(stringSQL);
            intRecordNo++;
          }
        }

        // �¦W��+���ަW��
        if ("Y".equals(strIsBlackList) || "Y".equals(strIsControlList)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','020','�N�z�H" + strCustomName
              + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '020', '�ӫȤᬰ�æ��¦W���H�A�а���[�j���Ȥ��¾�f�d�ę̀���~�������q���@�~��z�C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "�N�z�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C\n";
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','020','�N�z�H" + strCustomName + "���æ��¦W���H�A���ЮֽT�{��A�A�i������������@�~�C','" + empNo + "','" + RocNowDate + "','"
              + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }

        // �Q�`���Y�H
        if ("Y".equals(strIsLinked)) {
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','�N�z�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + strCustomNo + "','" + strCustomName + "','" + stringOrderDate + "','RY','773','019','�N�z�H" + strCustomName
              + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
          // AS400
          stringSQL = "INSERT INTO PSHBPF (SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) VALUES ('RY', '" + strOrderNo + "', '" + RocNowDate
              + "', '" + strCustomNo + "', '" + strCustomName + "', '773', '019', '�ӫȤᬰ���q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate + "','" + strNowTime
              + "')";
          dbJGENLIB.execFromPool(stringSQL);
          errMsg += custSA + "�N�z�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C\n";
        } else {
          // ���ŦX
          stringSQL = "INSERT INTO Sale05M070 (OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) VALUES ('"
              + strOrderNo + "','" + stringProjectID1 + "','" + intRecordNo + "','" + strActionNo + "','���W','�N�z�H���','" + strActionName + "','���ŦX','" + strCustomNo + "','"
              + strCustomName + "','" + stringOrderDate + "','RY','773','019','�N�z�H" + strCustomName + "�����q�Q�`���t�H�A�Ш̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C','" + empNo + "','" + RocNowDate
              + "','" + strNowTime + "')";
          dbSale05M.execFromPool(stringSQL);
          intRecordNo++;
        }
        
      } // for end
    } // �N�z�H END
    System.out.println("============================");

    // �e�XerrMsg
    if (!"".equals(errMsg)) {
      setValue("errMsgBoxText", errMsg);
      getButton("errMsgBoxBtn").doClick();
      getButton("sendMail").doClick();
    }
    System.out.println("==============�~�����v�ˮ�LOG END====================================");
    return value;
  }

  public String getInformation() {
    return "---------------button6(AML).defaultValue()----------------";
  }
}
