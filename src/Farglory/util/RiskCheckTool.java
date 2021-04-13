package Farglory.util;

import java.text.SimpleDateFormat;
import java.util.*;
import com.fglife.risk.*;
import jcx.db.*;
import jcx.jform.*;
import java.net.*;
import Farglory.aml.RiskCustomBean;

public class RiskCheckTool extends bvalidate {
  String serverIP, serverName; // ���A��IP , NAME
  talk dbSale, dbEMail, dbBen, dbEIP = null;
  ResultStatus rsStatus = new ResultStatus();

  RiskCheckBean bean = null;
  boolean isTest = true;
  String stringSQL = "";
  String testFlag = "";
  String userNo = "";
  String empNo = "";
  String sysdate = "";
  String systime = "";
  String actionNo = "";
  String strHouse = "";
  String strCar = "";

  public RiskCheckTool(RiskCheckBean bean) throws Throwable {
    InetAddress addr = InetAddress.getLocalHost();
    serverIP = addr.getHostAddress().toString(); // ��o����IP
    serverName = addr.getHostName().toString(); // ��o�����W��
    isTest = serverIP.contains("172.16.14") ? false : true;
    if (isTest) {
      testFlag = " (����) ";
      System.out.println("����>>>" + testFlag);
    }

    this.dbSale = bean.getDbSale();
    this.dbEMail = bean.getDbEMail();
    this.dbBen = bean.getDb400CRM();
    this.dbEIP = bean.getDbEIP();
    this.bean = bean;

    // Emaker�u��
    this.userNo = bean.getUserNo();
    // FG�u��
    this.getEMPNO();
    // �t�Τ���ɶ�
    this.getDateTime();
    // action
    this.getActionNo();
    // HouseCar
    this.getHouseCar();
    
    //�ϥ�bean�ǤJ�����X�A�ഫ���¦��}�C�榡
    if(this.bean.getListCustom().size() > 0) 
      this.processRetCustom();
    if(this.bean.getListBen().size() > 0) 
      this.processRetBen();
  }

  /**
   * ��X : rsMsg : ���I�p�⵲�G sendMailList : �n�o�eEmail��list
   * 
   * @return
   * @throws Throwable
   */
  public Result processRisk() throws Throwable {
    Result rs = new Result();
    RiskCheckRS rcRS = new RiskCheckRS();
    rs.setRsStatus(rsStatus.SUCCESSBUTSOMEERROR);
    
    String sysType = "RYB";// ���ʲ���PB �P��C
    String[][] retCustom = this.bean.getRetCustom();
    String[][] retSBen = this.bean.getRetSBen();
    String strProjectID1 = this.bean.getProjectID1();
    String strOrderNo = this.bean.getOrderNo();
    String strOrderDate = this.bean.getOrderDate();

    System.out.println("strOrderNo=====>" + strOrderNo);
    System.out.println("strProjectID1=====>" + strProjectID1);
    System.out.println("strOrderDate=====>" + strOrderDate);

    System.out.println("�s�J���I�p����q�H���-----------------------------------S");
    // ����H�}�C�w�q
    // 0.OrderNo, CustomNo, RecordNo, BenName, BCustomNo, 5.Birthday, CountryName,
    // HoldType, IsBlackList, IsControlList, 10.IsLinked, TrxDate, StatusCd
    
    //12            4            1           7         3         5          6
    //StatusCd , BCustomNo , CustomNo , HoldType , BenName , Birthday , CountryName
    String usedOid = "";
    for (int i = 0; i < retSBen.length; i++) {
      // �����w�Q���W�� (�λݭn��������ơA��C)
      String statusCd = "".equals(retSBen[i][12]) ? "" : retSBen[i][12].toString().trim();
      if ("C".equals(statusCd))
        continue;
      
      String id = retSBen[i][4];  //����HID
      String ono = this.bean.getOrderNo(); // �q��s��
      String oid = retSBen[i][1]; // �k�HID
      System.out.println("ono:" + ono);
      System.out.println("oid:" + oid);
      
      String oname = ""; // �k�HNAME
      String sql91name = "SELECT CustomName FROM Sale05M091 WHERE OrderNo= '" + ono + "' AND CustomNo = '" + oid + "' AND ISNULL(StatusCd , '') = '' ";
      String[][] ret91Name = null;
      ret91Name = dbSale.queryFromPool(sql91name);
      if (ret91Name.length > 0) {
        oname = ret91Name[0][0];
      }
      System.out.println("oname:" + oname);

      String fileType = retSBen[i][7];
      String name = retSBen[i][3];
      String idType = "1";
      String bdate = retSBen[i][5].trim();
      bdate = bdate.replace("/", "");
      if ("".equals(bdate)) {
        bdate = "0";
      }

      String nation = retSBen[i][6].trim();
      String nationCode = "TWN";
      String PDCZPFSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + nation + "'";
      String[][] retPDCZPF = null;
      retPDCZPF = dbBen.queryFromPool(PDCZPFSql);
      if(retPDCZPF.length > 0) nationCode = retPDCZPF[0][0].trim();

      //�C�Ӫk�H�Ĥ@��
      if( !oid.equals(usedOid) ) {
        //�H�k�H��KEY�A�N�L�Ҧ������q�H�ƻs�ܳƥ���(�q�渹�L��)
        String sqlMove1 = "INSERT INTO PSHAPFHF (Select * FROM PSHAPF WHERE SHA11 = '" + oid + "' And SHA00 = 'RY')";
        dbBen.execFromPool(sqlMove1);

        //�R���D�ɬ���
        String sqlDelete2 = "DELETE FROM PSHAPF WHERE SHA11 = '" + oid + "' And SHA00 = 'RY' ";
        dbBen.execFromPool(sqlDelete2);
      }

      //�g�J�s�����q�H
      String sqlInsert3 = "Insert into PSHAPF (SHA00, SHA02, SHA03, SHA04, SHA05, SHA06, SHA07, SHA08 ,SHA97, SHA98, SHA99, SHA100, SHA101, SHA102, SHA09, SHA10, SHA11, SHA12 ) "
          + "VALUES " + "('RY','" + strOrderNo + "','" + fileType + "','" + name + "','" + idType + "','" + id + "','" + bdate + "','" + nationCode + "','" + empNo + "'," + sysdate
          + "," + systime + ",'" + empNo + "'," + sysdate + "," + systime + ",'R','3','" + oid + "','" + oname + "' )";
      dbBen.execFromPool(sqlInsert3);
      
      usedOid = oid;
    }
    System.out.println("�s�J���I�p����q�H���-----------------------------------E");

    System.out.println("�s�J���I�p��Ȥ���-----------------------------------S");
    ResourceBundle resource = ResourceBundle.getBundle("sale");
    String as400ip = resource.getString("AS400.IP");
    String as400account = resource.getString("AS400.ACCOUNT");
    String as400password = resource.getString("AS400.PASSWORD");
    String as400init = resource.getString("AS400.INIT");
    String cms00c = resource.getString("CMS00C.LIB");
    String blpc00a = resource.getString("BLPC00A.LIB");
    String psri02 = resource.getString("PSRI02.LIB");
    String riskValue = "";
    String riskPoint = "";
    String sqltopmanager = "";
    RPGAS400Interface ra = null;
    RPGAS400Interface rb = null;
    RPGAS400Interface rc = null;

    ArrayList list = new ArrayList();
    try {
      ra = new RPGCMS00C(as400ip, as400account, as400password);
      rb = new RPGBLPC00A(as400ip, as400account, as400password);
      rc = new RPGPSRI02(as400ip, as400account, as400password);

      System.out.println(" retCustom.length=====> :" + retCustom.length);
      String msgboxtext = "";
      String tmpMsgText = "";

      // �Ȥ�}�C�w�q
      // OrderNo, RecordNo, auditorship, Nationality, 4.CountryName, 5.CustomNo,
      // 6.CustomName, Percentage, 8.Birthday, 9.MajorName, 10.PositionName, 11.ZIP, 12.City,
      // 13.Town, 14.Address, Cellphone,
      // 16.Tel, 17.Tel2, eMail, IsLinked, IsControlList, IsBlackList, TrxDate,
      // 23.StatusCd , 24.MajorCD
      
      //     4           5           6           8           9            10        11     12     13      14      16    17       23          24
      //CountryName , CustomNo , CustomName , Birthday , MajorName , PositionName , ZIP , City , Town , Address , Tel , Tel2 , statusCD , industryCode
      for (int i = 0; i < retCustom.length; i++) { // start �Ȥ�for
        
        if (isTest) {
          for(int ii=0 ; ii<retCustom[i].length ; ii++) {
            System.out.println("custom>>>" + ii + "-" + retCustom[i][ii]);
          }
        }

        // �����w�Q���W�� (�λݭn��������ơA��C)
        if ("C".equals(retCustom[i][23].trim())) continue;
        
        //210114 Kyle : ��~�O
        String industryCode = retCustom[i][24] != null? retCustom[i][23].toString().trim():"";
        if( "".equals(industryCode) ) {
          //�¸�ƨS����~�O�N�X
          String sql  =  "SELECT CZ02,CZ09 FROM PDCZPF WHERE CZ01='INDUSTRY' And CZ09 = '"+ retCustom[i][9].trim() +"' " ;
          String[][] retMajor  = dbBen.queryFromPool(sql) ;
          if(retMajor.length  > 0) industryCode = retMajor[0][0] != null? retMajor[0][0].trim():"";
        }
        
        sqltopmanager = " SELECT TOP 1 PositionCD, PName, ChairMan From A_Position " + " WHERE PName = '" + retCustom[i][10] + "'" + " ORDER BY PositionCD DESC ";
        String retPosition2[][] = dbSale.queryFromPool(sqltopmanager);
        String isManager = "N";
        if (retPosition2.length > 0) {
          isManager = retPosition2[0][2];
        }
        String type = "N";
        String sex = "";

        if (retCustom[i][5].trim().length() == 8) {
          type = "C";// N: �ӤH C: ���q F: �~��H
        } else {
          // �~��H(�H��O�P�_)
          if (!"���إ���".equals(retCustom[i][4].trim())) {
            type = "F";
          }
          // �ʧO
          if (retCustom[i][5].charAt(1) == '1') {
            sex = "M";
          } else if (retCustom[i][5].charAt(1) == '2') {
            sex = "F";
          }
        }

        // ���y��X(�Y�Źw�]TWN)
        String strCZ09 = "";
        if ("".equals(retCustom[i][4].trim())) {
          strCZ09 = "���إ���";
        } else {
          strCZ09 = retCustom[i][4].trim();
        }
        String strSaleSql = "SELECT CZ02 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + strCZ09 + "'";
        String retCNYCode[][] = dbBen.queryFromPool(strSaleSql);
        String cnyCode = retCNYCode[0][0].trim();
        System.out.println("cnyCode=====> :" + cnyCode);

        // �}�l�ո���o
        LinkedHashMap map = new LinkedHashMap();
        map.put("INAME", retCustom[i][6]); // �Ȥ�m�W
        map.put("IDATE", retCustom[i][8].replace("/", ""));// �ͤ�
        map.put("ID", retCustom[i][5]); // �����Ҹ�
        map.put("IAD1", retCustom[i][11]);// �a�} 1
        map.put("IAD2", retCustom[i][12]);// �a�} 2
        map.put("IAD3", retCustom[i][13]);// �a�} 3
        map.put("IADD", retCustom[i][12].trim() + retCustom[i][13].trim() + retCustom[i][14].trim());// ���a�}
        map.put("IZIP", retCustom[i][11]);// �l���ϸ�
        map.put("ITELO", retCustom[i][16]);// ���q�q��
        map.put("ITELH", retCustom[i][17]);// ��a�q��
        map.put("TYPE", type);// N: �ӤH C: ���q
        map.put("SEX", sex);// �ʧO M,F
        map.put("CNY", cnyCode);// ���y
        map.put("JOB", "");// ¾�~�N�X
        map.put("VOC", industryCode);// ��~�O
        map.put("CUST", " ");// ���@�ŧi
        map.put("IESTD", " "); // �]�w���
        map.put("IEXEC", isManager);// �����޲z�H Y/N
        map.put("CNY2", " ");// ���y 2
        map.put("CNY3", " ");// ���y 3
        map.put("ICHGD", "");// �ܧ�n�O���
        map.put("CHGNO", empNo);// ���ʤH�����s
        map.put("RTCOD", "");// �^�нX
        map.put("INSN", "");// �Ȥ�s��

        LinkedHashMap mapb = new LinkedHashMap();
        mapb.put("INSID", retCustom[i][5]); // �����Ҹ�
        mapb.put("SYSTEM", sysType); // �t�ΧO
        mapb.put("CHGNO", empNo);// ���ʤH�����s
        mapb.put("CAPTION", strOrderNo);// ����
        mapb.put("STRDATE", "0");// �_�l��
        mapb.put("ENDDATE", "0");// �פ��
        mapb.put("RTCOD", "");// �^�нX

        LinkedHashMap mapc = new LinkedHashMap();
        mapc.put("RI0201", retCustom[i][5]); // �����Ҹ�
        mapc.put("RI0202", "RY");// �t�ΧO
        mapc.put("RI0203", "Y");
        mapc.put("RIPOLN", "");
        mapc.put("RIFILE", strOrderNo);// �ӷ��׸�
        mapc.put("RI0204", "");//
        mapc.put("RI0205", "");//
        mapc.put("RI0206", "");//
        mapc.put("RI0207", "");//
        mapc.put("RI0208", "");//
        mapc.put("RI0209", "");//
        mapc.put("RO0201", "0");//
        mapc.put("RO0202", "");//
        mapc.put("RO0203", "0");//
        mapc.put("RO0204", "");//
        mapc.put("RO0205", "0");//
        mapc.put("RO0206", "");//
        mapc.put("RO0207", "0");//
        mapc.put("RO0208", "");//
        mapc.put("RO0209", "0");//
        mapc.put("RO0210", "");//
        mapc.put("RO0211", "");//
        mapc.put("RO0212", "");//
        mapc.put("RO0213", "");//
        mapc.put("RTNR02", "");// �^�нX

        boolean a = ra.invoke(as400init, cms00c, map);
        System.out.println("RTCODE:" + ra.getResult()[22]);
        boolean b = rb.invoke(as400init, blpc00a, mapb);
        System.out.println("RTCODE:" + rb.getResult()[6]);
        boolean c = rc.invoke(as400init, psri02, mapc);

        // RA ERROR MSG
        String resultMsg = "";
        if ("1".equals(ra.getResult()[22])) {
          resultMsg = "���O��N��C";
        } else if ("2".equals(ra.getResult()[22])) {
          resultMsg = "���O��N�����Ңע��ˮ֦��~�Ψ����Ҥ��i�ť�";
        } else if ("3".equals(ra.getResult()[22])) {
          resultMsg = "���O��N�ͤ餣�i��0";
        } else if ("4".equals(ra.getResult()[22])) {
          resultMsg = "���O��N����榡���~";
        } else if ("5".equals(ra.getResult()[22])) {
          resultMsg = "�m�W���i�ť�";
        } else if ("6".equals(ra.getResult()[22])) {
          resultMsg = "�a�}�ˮֶl�Ϥ��ŦX";
        } else {
          resultMsg = "" + rc.getResult()[20];
        }

        System.out.println("19�~�����I�� :" + rc.getResult()[19]);
        System.out.println("20�~�����I���� :" + rc.getResult()[20]);

        riskPoint = rc.getResult()[19].toString().trim();
        riskValue = rc.getResult()[20].toString().trim();
        System.out.println("RiskValue=====>" + riskValue);
        
        String customTitle = "�Ȥ� ";
        if(this.bean.getRecordType().indexOf("���w�ĤT�H") != -1) {
          customTitle = "���w�ĤT�H "; 
        }
        msgboxtext += customTitle + retCustom[i][6].trim() + " �~�����I���� :" + resultMsg + "\n";
        tmpMsgText += retCustom[i][6] + "\t" + resultMsg.trim() + "\t" + riskPoint.trim() + "\t" + rc.getResult()[11].toString().trim() + "\t"
            + rc.getResult()[13].toString().trim() + "\t" + rc.getResult()[15].toString().trim() + "\t" + rc.getResult()[17].toString().trim() + "\n";

        HashMap m = new HashMap();
        m.put("p01", strProjectID1);
        m.put("p02", strHouse.toUpperCase());
        m.put("p025", strCar.toUpperCase());
        m.put("p03", retCustom[i][6]);
        m.put("p035", retCustom[i][5]);
        m.put("p04", strOrderDate);
        m.put("p05", riskPoint);
        m.put("p06", riskValue.replace("���I", ""));
        m.put("riskValue", riskValue);
        list.add(m);

      } // End �Ȥ�for
      ra.disconnect();

      // ���I�ȵ��G��X
      rcRS.setRsMsg(msgboxtext);

      // ��s�Ȥ᭷�I��
      if (this.bean.isUpdSale05M091()) this.updSaleM091(list);
      
      //��s�X���Ȥ᭷�I��
      if (this.bean.isUpdSale05M277()) this.updSaleM277(list);
      
      //��s�X�����w�ĤT�H���I��
      if (this.bean.isUpdSale05M356()) this.updSaleM356(list);

    } catch (Exception e) {
      System.out.println("ERROR");
      System.out.println("���~�T��:" + e.toString());
      e.printStackTrace();
      ra.disconnect();
    }
    System.out.println("�s�J���I�p��Ȥ���-----------------------------------E");

    // insert into Sale05M070
    if (bean.isUpd070Log()) this.insSale05M070(list);

    // �զ�MAIL
    SendMailBean smBean = new SendMailBean();
    smBean = this.sendMail(list);

    List sendMailList = new ArrayList();
    sendMailList.add(smBean);
    rcRS.setSendMailList(sendMailList);

    rs.setData(rcRS);
    rs.setRsStatus(rsStatus.SUCCESS);
    return rs;
  }

  // ��empNo
  public void getEMPNO() throws Throwable {
    stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + this.userNo + "'";
    String[][] retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      this.empNo = retEip[0][0];
    }
    System.out.println("empNo>>>" + this.empNo);
  }

  // �t�Τ���ɶ�
  public void getDateTime() throws Throwable {
    sysdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
    systime = new SimpleDateFormat("HHmmss").format(new Date());
  }

  // actionNo
  public void getActionNo() throws Throwable {
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    this.actionNo = this.sysdate + this.systime + ram;
    System.out.println("strActionNo>>>" + this.actionNo);
  }

  // �ɼӧO
  public void getHouseCar() throws Throwable {
    String sql092 = "SELECT HouseCar,Position FROM  Sale05M092 WHERE OrderNo = '" + this.bean.getOrderNo() + "' ORDER BY RecordNo";
    String[][] retPosition = dbSale.queryFromPool(sql092);
    if (retPosition.length > 0) {
      for (int a = 0; a < retPosition.length; a++) {
        if ("House".equals(retPosition[a][0].trim())) {
          if ("".equals(strHouse)) {
            strHouse = retPosition[a][1].trim();
          } else {
            strHouse = strHouse + "," + retPosition[a][1].trim();
          }
        } else {
          if ("".equals(strCar)) {
            strCar = retPosition[a][1].trim();
          } else {
            strCar = strCar + "," + retPosition[a][1].trim();
          }
        }
      }
    }
  }

  /**
   * �^�gSale05M091 �Ȥ᭷�I��
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String updSaleM091(List customList) throws Throwable {
    System.out.println("�^�g05M091���----------" + customList.size() + "------------------S");

//    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String M091Sql = "UPDATE Sale05M091 SET RiskValue = '" + data.get("riskValue").toString().trim() + "' " 
                     + "WHERE OrderNo = '" + this.bean.getOrderNo() + "' AND CustomNo = '" + data.get("p035").toString().trim() + "' and ISNULL(StatusCd , '') = '';  ";
      dbSale.execFromPool(M091Sql);
//      trans.append(M091Sql);
    }
//    trans.close();
//    dbSale.execFromPool(trans.getString());

    System.out.println("�^�g05M091���----------" + customList.size() + "------------------E");
    return "0";
  }

  /**
   * �^�gSale05M277 �X���Ȥ᭷�I��
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String updSaleM277(List customList) throws Throwable {
    System.out.println("�^�g05M277���----------" + customList.size() + "------------------S");

    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String M277Sql = "UPDATE Sale05M277 "
               + "SET RiskValue = '" + data.get("riskValue").toString().trim() + "' " 
               + "WHERE ContractNo = '" + this.bean.getContractNo() + "' "
               + "AND CustomNo = '" + data.get("p035").toString().trim() + "' "
               + "AND ISNULL(StatusCd , '') = '' ";
      trans.append(M277Sql);
    }
    trans.close();
    dbSale.execFromPool(trans.getString());

    System.out.println("�^�g05M277���----------" + customList.size() + "------------------E");
    return "0";
  }
  
  /**
   * �^�gSale05M356 �X�����w�ĤT�H���I��
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String updSaleM356(List customList) throws Throwable {
    System.out.println("�^�g05M356���----------" + customList.size() + "------------------S");

    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String M277Sql = "UPDATE Sale05M356 "
               + "SET RiskValue = '" + data.get("riskValue").toString().trim() + "' " 
               + "WHERE ContractNo = '" + this.bean.getContractNo() + "' "
               + "  AND DesignatedId = '" + data.get("p035").toString().trim() + "' ";
      trans.append(M277Sql);
    }
    trans.close();
    dbSale.execFromPool(trans.getString());

    System.out.println("�^�g05M277���----------" + customList.size() + "------------------E");
    return "0";
  }

  /**
   * �g�JSale05M070
   * 
   * @param customList
   * @return
   * @throws Throwable
   */
  public String insSale05M070(List customList) throws Throwable {
    System.out.println("�s�J05M070���-----------------------------------S");

    // �Ǹ�
    int intRecordNo = 1;
    String[][] ret05M070;
    stringSQL = "SELECT MAX(CAST(RecordNo AS decimal(18, 0))) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + this.bean.getOrderNo() + "'";
    ret05M070 = dbSale.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("insSale05M070 intRecordNo >>>" + intRecordNo);

    Transaction trans = new Transaction();
    for (int ii = 0; ii < customList.size(); ii++) {
      HashMap data = (HashMap) customList.get(ii);
      String strCustomNo = data.get("p035").toString().trim();
      String strCustomName = data.get("p03").toString().trim();
      String riskValue = data.get("riskValue").toString().trim();
      stringSQL = "INSERT INTO Sale05M070 "
          + "(OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97, SHB98, SHB99) " + "VALUES "
          + "('" + this.bean.getOrderNo() + "','" + this.bean.getProjectID1() + "','" + intRecordNo + "','" + actionNo + "','" + this.bean.getFunc() + "','"
          + this.bean.getRecordType() + "' " + ",'" + this.bean.getActionText() + "','���I��:" + riskValue + "','" + strCustomNo + "' " + ",'" + strCustomName + "','"
          + this.bean.getOrderDate() + "','RY','773','022','���I��:" + riskValue + "','" + empNo + "'," + this.sysdate + "," + this.systime + ") ";
      trans.append(stringSQL);
      intRecordNo++;
    }
    trans.close();
    dbSale.execFromPool(trans.getString());

    System.out.println("�s�J05M070���-----------------------------------E");
    return "0";
  }

  // �oMAIL
  public SendMailBean sendMail(List customList) throws Throwable {
    System.out.println("�զ� EMAIL-----------------------------------S");
    String userEmail = "";
    String userEmail2 = "";
    String DPCode = "";
    String DPManageemNo = "";
    String DPeMail = "";
    String DPeMail2 = "";
    String[][] reteMail = null;

    ////////////////
    stringSQL = "SELECT DP_CODE,PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPCode = reteMail[0][0];
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        userEmail = reteMail[0][1];
      }
      if (reteMail[0][2] != null && !reteMail[0][2].equals("")) {
        userEmail2 = reteMail[0][2];
      }
    }
    /////////////////////////////////////////
    System.out.println("DPCode===>" + DPCode);
    System.out.println("userEmail===>" + userEmail);
    System.out.println("userEmail2===>" + userEmail2);
    /////////////////////////////////////////////////
    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + DPCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      DPManageemNo = reteMail[0][0];
    }
    /////////////////////////////////////////
    System.out.println("DPManageemNo===>" + DPManageemNo);
    /////////////////////////////////////////////////
    stringSQL = "SELECT PN_EMAIL1,PN_EMAIL2 FROM PERSONNEL WHERE PN_EMPNO='" + DPManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    if (reteMail.length > 0) {
      if (reteMail[0][0] != null && !reteMail[0][0].equals("")) {
        DPeMail = reteMail[0][0];
      }
      if (reteMail[0][1] != null && !reteMail[0][1].equals("")) {
        DPeMail2 = reteMail[0][1];
      }
    }
    ////////////////////////////////////
    System.out.println("DPeMail===>" + DPeMail);
    System.out.println("DPeMail2===>" + DPeMail2);
    /////////////////////////////////////////////////////////
    String PNCode = "";
    String PNManageemNo = "";
    String PNMail = "";

    stringSQL = "SELECT PN_DEPTCODE FROM PERSONNEL WHERE PN_EMPNO='" + empNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNCode = reteMail[0][0];
    System.out.println("PNCode===>" + PNCode);
    stringSQL = "SELECT DP_MANAGEEMPNO FROM CATEGORY_DEPARTMENT WHERE DP_CODE='" + PNCode + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNManageemNo = reteMail[0][0];
    System.out.println("PNManageemNo===>" + PNManageemNo);
    stringSQL = "SELECT PN_EMAIL1 FROM PERSONNEL WHERE PN_EMPNO='" + PNManageemNo + "'";
    reteMail = dbEMail.queryFromPool(stringSQL);
    PNMail = reteMail[0][0];
    System.out.println("PNMail===>" + PNMail);
    ////////////////////////////////////////////////////////////////////////////////////////

    // send email
    boolean hilitgt = false;
    String table1 = "<table style='text-align:center;' border=1><tr><td>�קO</td><td>�ɼӧO</td><td>����O</td><td>�Ȥ�W��</td><td>�I�q���</td><td>���I��X��</td><td>�Ȥ᭷�I����</td><td>����</td></tr>";
    String tail = "</table>";
    String contextsample = "<tr><td>${p01}</td><td>${p02}</td><td>${p025}</td><td>${p03}</td><td>${p04}</td><td>${p05}</td><td>${p06}</td><td align='left' valign='center'>${p07}</td></tr>";
    String cbottom = "</body></html>";
    String context = this.bean.getProjectID1() + "��" + strHouse + "���ʲ��q�ʫȤ᭷�I���ŵ������G�q��" + testFlag + "<BR>";

    context = table1;
    for (int i = 0; i < customList.size(); i++) {
      HashMap cm = (HashMap) customList.get(i);

      String l1 = new String(contextsample);
      l1 = l1.replace("${p01}", (String) cm.get("p01"));
      l1 = l1.replace("${p02}", (String) cm.get("p02"));
      l1 = l1.replace("${p025}", (String) cm.get("p025"));
      l1 = l1.replace("${p03}", (String) cm.get("p03"));
      l1 = l1.replace("${p035}", (String) cm.get("p035"));
      l1 = l1.replace("${p04}", (String) cm.get("p04"));
      l1 = l1.replace("${p05}", (String) cm.get("p05"));
      l1 = l1.replace("${p06}", (String) cm.get("p06"));
      String tempPo6 = "" + cm.get("p06");
      tempPo6 = tempPo6.trim();
      if ("��".equals(tempPo6.trim())) {
        l1 = l1.replace("${p07}", "�~���θꮣ���I������" + tempPo6 + "���I�Ȥ�A�Ш̬~������@�~�W�w�A����[�j���ޱ����I");
        hilitgt = true;
      } else {
        l1 = l1.replace("${p07}", "�~���θꮣ���I������" + tempPo6 + "���I�Ȥ�");
      }

      context = context + l1;
    } // customList for End
    context = context + tail + cbottom;

    //20201222 Kyle : �Ԧ�Server Side�@�Τ����A�L�k�b�o��ϥΤ��ب�ơA�אּ�զ�Mail��T���e�ݰ��oMAIL���ʧ@
//    String sendRS = sendMailbcc("ex.fglife.com.tw", "Emaker-Invoice@fglife.com.tw", arrayUser, subject, context, null, "", "text/html");
//    String subject = this.bean.getProjectID1() + "��" + strHouse + "���ʲ��q�ʫȤ᭷�I���ŵ������G�q��" + testFlag;
    String subject = this.bean.getProjectID1() + "��" + strHouse + "���ʲ�" + this.bean.getFunc() + this.bean.getRecordType() + "�q��" + testFlag;
    SendMailBean send = new SendMailBean();
    send.setColm1("ex.fglife.com.tw");
    send.setColm2("Emaker-Invoice@fglife.com.tw");
    send.setSubject(subject);
    send.setContext(context);
    send.setColm6(null);
    send.setColm7("");
    send.setColm8("text/html");

    if (hilitgt) {
      String[] arrayUser = { "Kyle_Lee@fglife.com.tw", userEmail, DPeMail, PNMail };
      send.setArrayUser(arrayUser);
    } else {
      String[] arrayUser = { "Kyle_Lee@fglife.com.tw", userEmail };
      send.setArrayUser(arrayUser);
    }
    System.out.println("�զ� EMAIL-----------------------------------E");

    return send;
  }
  
  /**
   * �ϥ�bean����ǤJ�����X�A�B�z���q�ΰ}�C�榡 (Custom)
   * 
   * @param listCustom
   * @return
   * @throws Throwable
   */
  private String processRetCustom() throws Throwable{
    List list = this.bean.getListCustom();
    String[][] tmpCustom = new String[list.size()][25];
    for(int ii=0 ; ii<list.size() ; ii++) {
      RiskCustomBean bean = (RiskCustomBean)list.get(ii);
      String[] data = new String[25];
      data[0] = "";
      data[1] = "";
      data[2] = "";
      data[3] = "";
      data[4] = bean.getCountryName(); //CountryName
      data[5] = bean.getCustomNo(); //CustomNo
      data[6] = bean.getCustomName(); //CustomName
      data[7] = "";
      data[8] = bean.getBirthday(); //Birthday
      data[9] = bean.getMajorName();  //majorName
      data[10] = bean.getPositionName();  //PositionName
      data[11] = bean.getZip();  //ZIP
      data[12] = bean.getCity();  //City
      data[13] = bean.getTown();  //Town
      data[14] = bean.getAddress();  //Address
      data[15] = "";
      data[16] = bean.getTel();  //Tel
      data[17] = bean.getTel2();  //Tel2
      data[18] = "";
      data[19] = "";
      data[20] = "";
      data[21] = "";
      data[22] = "";
      data[23] = bean.getStatusCd();  //StatusCd
      data[24] = bean.getIndustryCode();  //industryCode
      tmpCustom[ii] = data;
    }
    this.bean.setRetCustom(tmpCustom);
    
    return "0";
  }
  
  /**
   * �ϥ�bean����ǤJ�����X�A�B�z���q�ΰ}�C�榡 (Ben)
   * 
   * @param listCustom
   * @return
   * @throws Throwable
   */
  private String processRetBen() throws Throwable{
    List list = this.bean.getListBen();
    String[][] tmp = new String[list.size()][13];
    for(int ii=0 ; ii<list.size() ; ii++) {
      RiskBenBean bean = (RiskBenBean)list.get(ii);
      String[] data = new String[24];
      data[0] = "";
      data[1] = bean.getbCustomNo(); //CustomNo
      data[2] = "";
      data[3] = bean.getBenName(); //BenName
      data[4] = bean.getbCustomNo(); //BCustomNo
      data[5] = bean.getBirthday(); //Birthday
      data[6] = bean.getCountryName(); //CountryName
      data[7] = bean.getHoldType(); //HoldType
      data[8] = "";
      data[9] = "";
      data[10] = "";
      data[11] = "";
      data[12] = bean.getStatusCd();  //statusCd
      tmp[ii] = data;
    }
    this.bean.setRetSBen(tmp);
    
    return "0";
  }

  // �O�Ʀr�^�� true�A�_�h�^�� false�C
  public boolean check(String value) throws Throwable {
    return false;
  }

}
