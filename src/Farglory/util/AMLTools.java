/**
 * 20200602 Kyle : �~���˺A�d�ߥ\��
 * 20200603 Kyle : �������ӭn��2X�ؼ˺A���g�i�ӡA���S�ɶ��ҥH�u�g�F�����PEPS�A�Ʊ�H�ᦳ�ɶ��ɻ�
 * 
 * �i��ϥγB�]�w�@AMLCODE�}�C����
 * select * from saleRY773 order by AMLno asc
 * AML 001  �P�@�Ȥ�P�@��~�餺2��(�t)�H�W�]�t�{���B�״ڡB�H�Υd�B�䲼����A�B�C���Ҥ���s�x��450,000~499,999���A�t���ˮֹwĵ�C
 * AML 002  �P�@�Ȥ�3����~�餺�A��2��H�{���ζ״ڹF450,000~499,999��, �t���ˮִ��ܳq���C
 * AML 003  �P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C
 * AML 004  �P�@�Ȥ�3����~�餺�A�֭pú��{���W�L50�U��, �t���ˮִ��ܳq���C
 * AML 005  �Nú�ڤH�P�ʶR�H���Y���D�G���ˤ���/�ÿˡA�t���ˮִ��ܳq���C
 * AML 006  �P�@�Ȥᤣ�ʲ��R��Añ���e�h�q�����ʶR�A���ˮ֨�X�z�ʡC
 * AML 007  �P�@�Ȥ�P�@��~��{��ú�ǲ֭p�F50�U��(�t)�H�W�A���ˮ֬O�_�ŦX�æ��~�������x�C
 * AML 008  ���ʲ��P��ѲĤT��N�z��ú�ڡA�t���ˮִ��ܳq���C
 * AML 009  �Ȥ�Y�ӦۥD�޾����Ҥ��i����~���P�����ꮣ���Y���ʥ�����a�Φa�ϡA�Ψ�L����`�Υ��R����`����a�Φa�ϡA���ˮ֨�X�z�ʡC
 * AML 010  �ۥD�޾����Ҥ��i����~���P������U���ƥ��l���Y���ʥ�����a�Φa�ϡB�Ψ�L����`�Υ��R����`����a�Φa�϶פJ������ڶ��A���ˮ֨�X�z�ʡC
 * AML 011  ����̲ר��q�H�Υ���H���D�޾������i�����Ƥ��l�ι���F�ΰ�ڻ{�w�ΰl�d�����Ʋ�´�F�Υ������æ��P���Ʋ�´�����p�̡A���̸ꮣ����k�i������@�~�C
 * AML 012  �Ȥ�n�D�N���ʲ��v�Q�n�O���ĤT�H�A���ണ�X�������p�Ωڵ����������`���p�C
 * AML 013  �Ȥ��I���ʲ�������ڶ��A�H�{�r��I�q���H�~�U�����ڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C
 * AML 014  �Ȥ��ñ���e���e�I�M�۳ƴڡA�B�L�X�z��������ӷ��A���ˮ֬O�_�ŦX�æ��~�������x�C
 * AML 015  �n�D���q�}�ߨ����T��I�������䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C
 * AML 016  �n�D���q�}�ߺM�P����u(�������u)�䲼�@�����I�覡�A���ˮ֬O�_�ŦX�æ��~�������x�C
 * AML 017  �Ȥᬰ���ަW���H�A�а���[�j���Ȥ��¾�f�d�ę̀���~�������q���@�~��z�C
 * AML 018  �Ȥᬰ���ަW���H������W��A�T�����ýШ̨���~�����q���@�~�|��k��ǡC
 * AML 019  �Ȥᬰ���q�Q�`���t�H�A�ݨ̫O�I�~�P�Q�`���Y�H�q�Ʃ�ڥH�~����L����޲z��k����C
 * AML 020  �Ȥᬰ�æ��¦W���H�A���ЮֽT�{��A�A�i��������C
 * AML 021  �Ȥ�Ψ���q�H�B�a�x�����Φ��K�����Y���H�A���{���B�����ꤺ�~�F���ΰ�ڲ�´���n�F�v��¾�ȡA�Х[�j�Ȥ��¾�լd�C
 */
package Farglory.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jcx.db.talk;
import jcx.jform.bvalidate;

/**
 * �~���U�A�ˬd�� 1. �쪩(�A��18 & �A��21)�٨S��eclipse�}�o�A�ϥ�bean�˸��ܼƤ���K�A�G����map�C 2.
 * �ĤG���]����beclipse���}�o�F�A�G�ϥ�AMLBean���Ѽƶǻ��B�z�C 3. ����کΫ�⦳�ɶ���쪩���Ҧ����ܤ@�U�C
 * 
 * @author B04391
 *
 */

public class AMLTools extends bvalidate {
  // DB
  talk db400 = null;
  talk dbSale = null;
  talk dbEIP = null;
  talk dbPW0D = null;

  KUtils kutil = new KUtils();
  ResultStatus rsStat = new ResultStatus();

  // param �ǤJ��
  StringBuilder sbRsMsg = new StringBuilder();
  String strDocNo = "";
  String strOrderNo = ""; // �ʫ��ҩ���s��
  String strProjectID1 = ""; // �קO�N�X
  String strOrderDate = ""; // �ʫ��ҩ�����
  String strActionName = "�s��"; // �sSale05M070�ϥ�
  String strActionNo = ""; // �sSale05M070�ϥ�
  String funcName = ""; // ���ڳ�ɷ|���ǤJ

  int intRecordNo = 1; // �sSale05M070�ϥ�
  Map mapAMLMsg = null;
  String strNowDate = "";
  String strNowDate2 = "";
  String rocNowDate = "";
  String strNowTime = "";
  String strNowTime2 = "";

  // ���u�s��
  String userNo = "";
  String empNo = "";

  AMLBean aml;
  Map mapCustomers = new HashMap();
  List orderList = null;
  List customNoList = null;

  public AMLTools() throws Throwable {
  }

  public AMLTools(AMLBean aml) throws Throwable {
    db400 = getTalk("400CRM");
    dbSale = getTalk("Sale");
    dbEIP = getTalk("EIP");
    dbPW0D = getTalk("pw0d");

    this.aml = aml;
//    strDocNo = aml.getDocNo();
//    strOrderNo = aml.getOrderNo();
    strProjectID1 = aml.getProjectID1();
    strOrderDate = aml.getTrxDate();
    strActionName = aml.getActionName();
    funcName = aml.getFuncName();
    orderList = new ArrayList(Arrays.asList(aml.getOrderNos().split(",")));
    customNoList = new ArrayList(Arrays.asList(aml.getCustomNos().split(",")));

    // LOG���,�ɶ�
    this.getDateTime();

    // ���u�s�� & EIPNO
    userNo = getUser().toUpperCase().trim();
    this.getEmpNo();

    // �Ǹ�
    this.getRecordNo070ByType(aml);

    // actionNo
    this.getActionNo();

    // ���oAML�A�ˤ��廡��
    this.getAML();

    // ���o�����ڳ�Ҧ��Ȥ�ID�m�W����
    this.getCustomers();
  }

  public AMLTools(Map cons) throws Throwable {
    db400 = getTalk("400CRM");
    dbSale = getTalk("Sale");
    dbEIP = getTalk("EIP");
    dbPW0D = getTalk("pw0d");

    strOrderNo = cons.get("OrderNo") != null ? cons.get("OrderNo").toString().trim() : "";
    strProjectID1 = cons.get("ProjectID1") != null ? cons.get("ProjectID1").toString().trim() : "";
    strOrderDate = cons.get("TrxDate") != null ? cons.get("TrxDate").toString().trim() : "";
    strActionName = cons.get("ActionName") != null ? cons.get("ActionName").toString().trim() : "";
    funcName = cons.get("funcName") != null ? cons.get("funcName").toString().trim() : "";

    // LOG���,�ɶ�
    this.getDateTime();

    // ���u�s�� & EIPNO
    userNo = getUser().toUpperCase().trim();
    this.getEmpNo();

    // �Ǹ�
    this.getRecordNo070();

    // actionNo
    this.getActionNo();

    // ���oAML�A�ˤ��廡��
    this.getAML();
  }

  public void getCustomers() throws Throwable {
    String sql = "select CustomNo , CustomName from sale05M084 where DocNo = '" + this.aml.getDocNo() + "' ";
    String[][] retQuery = dbSale.queryFromPool(sql);
    if (retQuery.length > 0) {
      for (int i = 0; i < retQuery.length; i++) {
        mapCustomers.put(retQuery[i][0].trim(), retQuery[i][1].trim());
      }
    }
  }

  public Map getAMLReTurn() throws Throwable {
    Map rsMap = new HashMap();
    String sql = "select * from saleRY773 where AMLType = 'AML' order by AMLNo asc";
    String[][] retAML = dbSale.queryFromPool(sql);
    for (int i = 0; i < retAML.length; i++) {
      String[] retAML1 = retAML[i];
      rsMap.put(retAML1[1].trim(), retAML1[2].trim());
    }
    return rsMap;
  }

  public String getAML() throws Throwable {
    String rs = "getAML Error";
    String sql = "select * from saleRY773 where AMLType = 'AML' order by AMLNo asc";
    String[][] retAML = dbSale.queryFromPool(sql);
    mapAMLMsg = new HashMap();
    for (int i = 0; i < retAML.length; i++) {
      String[] retAML1 = retAML[i];
      mapAMLMsg.put(retAML1[1], retAML1[2]);
    }
    rs = "";
    return rs;
  }

  public void getActionNo() throws Throwable {
    String ram = "";
    Random random = new Random();
    for (int i = 0; i < 4; i++) {
      ram += String.valueOf(random.nextInt(10));
    }
    strActionNo = strNowDate + strNowTime + ram;
    System.out.println("strActionNo=====>" + strActionNo);
  }

  // ��̷ӥ\���������
  public void getRecordNo070ByType(AMLBean aml) throws Throwable {
    String stringSQL = "";
    if ("����".equals(aml.getFuncName()) && !"".equals(aml.getDocNo())) {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE DocNo ='" + aml.getDocNo() + "' ";
    } else {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + aml.getOrderNo() + "' ";
    }

    String[][] ret05M070 = dbSale.queryFromPool(stringSQL);
    if (ret05M070.length > 0 && !"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
  }

  public void getRecordNo070() throws Throwable {
    String[][] ret05M070;
    String stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + this.strOrderNo + "' ";
    ret05M070 = dbSale.queryFromPool(stringSQL);
    if (!"".equals(ret05M070[0][0].trim())) {
      intRecordNo = Integer.parseInt(ret05M070[0][0].trim()) + 1;
    }
    System.out.println("intRecordNo=====>" + intRecordNo);
  }

  public void getDateTime() throws Throwable {
    Date now = new Date();
    SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    strNowDate = nowsdf.format(now);

    String tempROCYear = "" + (Integer.parseInt(strNowDate.substring(0, strNowDate.length() - 4)) - 1911);
    rocNowDate = tempROCYear + strNowDate.substring(strNowDate.length() - 4, strNowDate.length());

    SimpleDateFormat nowTimeSdf = new SimpleDateFormat("HHmmss");
    strNowTime = nowTimeSdf.format(now);

    SimpleDateFormat nowsdf2 = new SimpleDateFormat("yyyy-MM-dd");
    strNowDate2 = nowsdf2.format(now);
    SimpleDateFormat nowTimeSdf2 = new SimpleDateFormat("HH:mm:ss");
    strNowTime2 = nowTimeSdf2.format(now);

    System.out.println("RocNowDate=====>" + rocNowDate);
    System.out.println("strNowTime=====>" + strNowTime);
  }

  public void getEmpNo() throws Throwable {
    // ���u�X
    String[][] retEip = null;
    String stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + this.userNo + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    } else {
      System.out.println(">>>None EmpNo<<<");
    }
  }

  public boolean check(String value) throws Throwable {
    return false;
  }

  /**
   * AML 001
   * �P�@�Ȥ�P�@��~�餺2��(�t)�H�W�]�t�{���B�״ڡB�H�Υd�B�䲼����A�B�C���Ҥ���s�x��450,000~499,999���A�t���ˮֹwĵ�C(���ڥ���Ҧ�ú��)
   * Ĳ�o����: ���wĲ�o �d�߱���]�t45~49�P�_�A�{���L���A�B�z
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML001(AMLBean aml, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("001");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    System.out.println("AML01 start>>>");
    String shareCondition = "";
    // �q��
    if ("order".equals(type)) {
      aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
      aml.setCustomNames(aml.getCustomNames());
      shareCondition = "and a.EDate = '" + aml.getTrxDate() + "' and c.OrderNo in (" + aml.getOrderNos() + ") ";
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'cash' , 'cash no' as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(a.CashMoney , 0) as money ");
      sbSQL.append(", ISNULL(a.CashMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) as oneMoney ");
      sbSQL.append("from Sale05M080 a , sale05m086 c ");
      sbSQL.append("where 1=1 and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(a.CashMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and 499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("UNION ");
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'credit' , b.CreditCardNo as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(b.CreditCardMoney, 0) as money ");
      sbSQL.append(", ISNULL(b.CreditCardMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  as oneMoney ");
      sbSQL.append("from Sale05M080 a  , Sale05M083 b , sale05m086 c ");
      sbSQL.append("where b.DocNo = a.DocNo and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(b.CreditCardMoney, 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  BETWEEN 450000 and 499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("UNION ");
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'bank' , b.BankNo as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(b.BankMoney, 0) as money ");
      sbSQL.append(", ISNULL(b.BankMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  as oneMoney ");
      sbSQL.append("from Sale05M080 a  , Sale05M328 b , sale05m086 c ");
      sbSQL.append("where b.DocNo = a.DocNo and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(b.BankMoney, 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and  499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("UNION ");
      sbSQL.append("select distinct c.OrderNo ,a.DocNo ,a.EDate ");
      sbSQL.append(", 'check' , b.CheckNo as prrofNo , STUFF((SELECT ',' + aa.CustomName FROM Sale05M084 aa WHERE aa.docNo = a.docNo FOR XML PATH('')), 1, 1, '') ");
      sbSQL.append(", ISNULL(b.CheckMoney, 0) as money ");
      sbSQL.append(", ISNULL(b.CheckMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)  as oneMoney ");
      sbSQL.append("from Sale05M080 a  , Sale05M082 b , sale05m086 c ");
      sbSQL.append("where b.DocNo = a.DocNo and a.DocNo=c.DocNo ");
      sbSQL.append("and (ISNULL(b.CheckMoney, 0)/(select count(e.OrderNo) from Sale05M086 e where e.DocNo = a.DocNo group by e.DocNo) BETWEEN 450000 and 499999) ");
      sbSQL.append(shareCondition);
      sbSQL.append("order by a.DocNo desc ,a.EDate DESC ");
      String[][] retAML0011 = dbSale.queryFromPool(sbSQL.toString(), 300);
      int count1 = 0;
      String lastKey1 = "";
      for (int i = 0; i < retAML0011.length; i++) {
        String thisKey1 = retAML0011[i][0].trim();
        if (lastKey1.equals(thisKey1)) { // �ۦP�Y��ܲĤG��
          if (count1 >= 1)
            continue; // �T���@���N�n
          String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames());
          sbMsg.append(msg).append("\n");
          aml.setErrMsg(msg);
          count1++;

          // �g�ŦX����
          aml.setOrderNo(thisKey1);
          this.insSale070(aml);
          this.insCR400(aml);

          // �q�M�椤�����ŦX���s��
          orderList.remove("'" + thisKey1 + "'");
        } else {
          count1 = 0;
        }
        lastKey1 = thisKey1;
      }

      // �g���ŦX����
      for (int i = 0; i < orderList.size(); i++) {
        aml.setOrderNo(orderList.get(i).toString().replaceAll("'", ""));
        aml.setErrMsg("���ŦX");
        this.insSale070(aml);
      }
    } // End type = order

    // �Ȥ᳡��
    if ("custom".equals(type)) {
      aml.setOrderNo("");
      shareCondition = "and a.EDate = '" + aml.getTrxDate() + "' and c.CustomNo in (" + aml.getCustomNos() + ") ";
      String sql = "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'cash' " + ", ISNULL(a.CashMoney , 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.CashMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 " + "from Sale05M080 a , sale05m084 c  "
          + "where 1=1 and a.DocNo=c.DocNo  "
          + "and (ISNULL(a.CashMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) "
          + shareCondition + "UNION " + "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'credit' " + ", ISNULL(b.CreditCardMoney, 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.CreditCardMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 "
          + "from Sale05M080 a  , Sale05M083 b , sale05m084 c " + "where b.DocNo = a.DocNo and a.DocNo=c.DocNo "
          + "and (ISNULL(b.CreditCardMoney, 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) "
          + shareCondition + "UNION " + "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'bank' " + ", ISNULL(b.BankMoney, 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.BankMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 "
          + "from Sale05M080 a  , Sale05M328 b , sale05m084 c " + "where b.DocNo = a.DocNo and a.DocNo=c.DocNo "
          + "and (ISNULL(b.BankMoney, 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and  499999) "
          + shareCondition + "UNION " + "select distinct c.CustomNo , c.CustomName ,a.EDate " + ", 'check' " + ", ISNULL(b.CheckMoney, 0) as money "
          + ", (select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo) "
          + ", ISNULL(a.CheckMoney , 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 "
          + "from Sale05M080 a  , Sale05M082 b , sale05m084 c " + "where b.DocNo = a.DocNo and a.DocNo=c.DocNo "
          + "and (ISNULL(b.CheckMoney, 0)*(select top 1 Percentage from Sale05M084 e where e.DocNo=a.DocNo and e.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) "
          + shareCondition + "order by c.CustomNo desc ,a.EDate DESC ";
      String[][] retAML0012 = dbSale.queryFromPool(sql, 300);
      int count2 = 0;
      String lastKey2 = "";
      for (int i = 0; i < retAML0012.length; i++) {
        String thisKey2 = retAML0012[i][0].trim();
        if (lastKey2.equals(thisKey2)) { // �ۦP�Y��ܲĤG��
          if (count2 >= 1)
            continue; // �T���@���N�n
          String msg = amlDesc.replaceAll("<customName>", retAML0012[i][1].trim());
          sbMsg.append(msg).append("\n");
          aml.setErrMsg(msg);
          count2++;

          // �g�ŦX����
          aml.setCustomId(thisKey2);
          aml.setCustomName(retAML0012[i][1].trim());
          this.insSale070(aml);
          this.insCR400(aml);

          // �q�M�椤�����ŦX���s��
          customNoList.remove("'" + thisKey2 + "'");
        } else {
          count2 = 0;
        }

        lastKey2 = thisKey2;
      }

      // �g���ŦX����
      for (int i = 0; i < customNoList.size(); i++) {
        String noMatchId = customNoList.get(i).toString().replaceAll("'", "");
        aml.setCustomId(noMatchId);
        aml.setCustomName(mapCustomers.get(noMatchId).toString());
        aml.setErrMsg("���ŦX");
        this.insSale070(aml);
      }
    } // End of type = custom

    // ���m�W��
    orderList = new ArrayList(Arrays.asList(aml.getOrderNos().split(",")));
    customNoList = new ArrayList(Arrays.asList(aml.getCustomNos().split(",")));

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }

  /**
   * Kyle AML 002 �P�@�Ȥ�3����~�餺�A��2��H�{���ζ״ڹF450,000~499,999��, Ĳ�o����:
   * ���ڳ楻���R���@��45~49�U�ݰ����ˮ֡C �d�߫e���A�R���@���Y���ͺA�ˡC 2020/11/26 :
   * �o�{�q���Ȥ����ӭn���}�B�z(�]���b���ڳ樺��N�n����q���ӤH�O�_�R��)�A�Ȥ᳡���w�g�g�n�N�i�o��F!!
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML002(AMLBean aml, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("002");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    // �]���Ĥ@���R�����ӬO���ڳ楻���A�G�u�n��e��ѧY�i
    String startEDate = kutil.getDateAfterNDays(aml.getTrxDate().trim(), "/", -2);
    String endEDate = kutil.getDateAfterNDays(aml.getTrxDate().trim(), "/", -1);

    System.out.println("AML02 start>>>");
    // �q��
    aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
    aml.setCustomNames(aml.getCustomNames());
    if ("order".equals(type)) {
      sbSQL.append("SELECT c.OrderNo ,a.EDate ");
      sbSQL.append(", SUM(a.CashMoney)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) as CashMoney ");
      sbSQL.append(", SUM(a.BankMoney)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) as BankMoney ");
      sbSQL.append("FROM Sale05M080 a , sale05m086 c ");
      sbSQL.append("WHERE 1=1 and a.DocNo=c.DocNo ");
      sbSQL.append("and a.EDate BETWEEN '" + startEDate + "' AND '" + endEDate + "' and c.OrderNo in (" + aml.getOrderNos() + ") ");
      sbSQL.append("GROUP BY c.OrderNo ,a.EDate , a.DocNo ,a.CashMoney ,a.CreditCardMoney ,a.BankMoney ,a.CheckMoney ");
      sbSQL.append("HAVING ( (ISNULL(a.CashMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and 499999) ");
      sbSQL.append("or (ISNULL(a.BankMoney , 0)/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo) BETWEEN 450000 and 499999) ) ");
      sbSQL.append("ORDER BY c.OrderNo DESC ,a.EDate DESC ");
      String[][] retAML0021 = dbSale.queryFromPool(sbSQL.toString(), 300);
      String lastKey1 = "";
      for (int i = 0; i < retAML0021.length; i++) {
        String thisKey1 = retAML0021[i][0].trim();
        if (lastKey1.equals(thisKey1))
          continue; // ���ƪ����n

        // �B�z�R���T��
        String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames());
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // �g�ŦX����
        aml.setOrderNo(thisKey1);
        this.insSale070(aml);
        this.insCR400(aml);

        // �q�M�椤�����ŦX���s��
        orderList.remove("'" + thisKey1 + "'");

        lastKey1 = thisKey1;
      }

      // �g���ŦX����
      for (int i = 0; i < orderList.size(); i++) {
        aml.setOrderNo(orderList.get(i).toString().replaceAll("'", ""));
        aml.setErrMsg("���ŦX");
        this.insSale070(aml);
      }
    }

    // �Ȥ�
    aml.setOrderNo("");
    if ("custom".equals(type)) {
      sbSQL.append("select c.CustomNo ,c.CustomName ,a.EDate ");
      sbSQL.append(", SUM(a.CashMoney)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 as CashMoney ");
      sbSQL.append(", SUM(a.BankMoney)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 as BankMoney ");
      sbSQL.append("from Sale05M080 a , sale05m084 c ");
      sbSQL.append("where 1=1 and a.DocNo=c.DocNo ");
      sbSQL.append("and a.EDate BETWEEN '" + startEDate + "' AND '" + endEDate + "' and c.CustomNo = '" + aml.getCustomId() + "' ");
      sbSQL.append("GROUP BY a.DocNo ,a.EDate , c.CustomNo ,c.CustomName ,a.CashMoney ,a.CreditCardMoney ,a.BankMoney ,a.CheckMoney ");
      sbSQL.append(
          "HAVING ((ISNULL(a.CashMoney , 0)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) ");
      sbSQL.append(
          "or (ISNULL(a.BankMoney , 0)*(select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100 BETWEEN 450000 and 499999) ) ");
      sbSQL.append("ORDER BY c.CustomNo DESC ,a.EDate DESC ");
      String[][] retAML0022 = dbSale.queryFromPool(sbSQL.toString(), 300);
      String lastKey2 = "";
      for (int i = 0; i < retAML0022.length; i++) {
        String thisKey2 = retAML0022[i][0].trim();
        if (lastKey2.equals(thisKey2))
          continue; // ���ƪ����n

        // �B�z�R���T��
        String msg = amlDesc.replaceAll("<customName>", retAML0022[i][1].trim());
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // �g�ŦX����
        aml.setCustomId(thisKey2);
        aml.setCustomName(retAML0022[i][1].trim());
        this.insSale070(aml);
        this.insCR400(aml);

        // �q�M�椤�����ŦX���s��
        customNoList.remove("'" + thisKey2 + "'");

        lastKey2 = thisKey2;
      }

      // �g���ŦX����
      for (int i = 0; i < customNoList.size(); i++) {
        String noMatchId = customNoList.get(i).toString().replaceAll("'", "");
        aml.setCustomId(noMatchId);
        aml.setCustomName(mapCustomers.get(noMatchId).toString());
        aml.setErrMsg("���ŦX");
        this.insSale070(aml);
      }
    }

    // ���m�W��
    orderList = new ArrayList(Arrays.asList(aml.getOrderNos().split(",")));
    customNoList = new ArrayList(Arrays.asList(aml.getCustomNos().split(",")));

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }

  /**
   * AML 0031 �P�@�Ȥ�3����~�餺�A�֭pú��{���W�L50�U��, �t���ˮִ��ܳq���C Ĳ�o���� :
   * ���ڳ��U�����{���A�B�Q��B�e�饼�R���j�B(AML003) �d�߭q��γ�@�Ȥ�T�餺���ڲ֭p�A�W�L50�U���ͺA��
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML0031(AMLBean aml, String keyNo, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("003");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    //�]�w����d��
    String startEDate = aml.getTrxDate().trim();
    String endEDate = aml.getTrxDate().trim();

    System.out.println("AML031 start>>>");
    System.out.println("AML031 ����d��>>>" + startEDate + " - " + endEDate);
    System.out.println("AML031 �q�渹�X>>>" + aml.getOrderNos()); // sql in �榡
    System.out.println("AML031 �Ȥ�IDS>>>" + aml.getCustomNos()); // sql in �榡
    System.out.println("AML031 �Ȥ�NAMES>>>" + aml.getCustomNames()); // name�Bname�Bname

    // �q��
    aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
    aml.setCustomNames(aml.getCustomNames());
    if ("order".equals(type)) {
      String orderNo = keyNo;
      System.out.println("AML031 �q��>>>"+ orderNo);

      String[][] retO = this.getOrderDayPayA4(startEDate, endEDate, orderNo);
      float dayAmt = 0; // �C��֭p
      for (int i = 0; i < retO.length; i++) {
        String keyDate = retO[i][3].toString().trim();
        float amt = Float.parseFloat(retO[i][2].toString().trim());
        dayAmt += amt;
        System.out.println("AML031 ���>>>" + keyDate + "// ���B>>>" + amt);
      }
      System.out.println("AML031 ���>>>" + aml.getTrxDate() + "// ���֭p>>>" + dayAmt);

      if (dayAmt >= 500000) {
        // ���֭p�F�СA���ͺA��
        String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames() );
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // �g�ŦX����
        aml.setOrderNo(orderNo);
        this.insSale070(aml);
        this.insCR400(aml);

        // �q�M�椤�����ŦX���s��
        customNoList.remove("'" + orderNo + "'");
      }

      // TODO: �g���ŦX�A��
    }

    // �Ȥ�
    aml.setOrderNo("");
    if ("custom".equals(type)) {
      String[] arrCustNos = aml.getCustomNos().split(",");
      String[] arrCustNames = aml.getCustomNames().split("�B");
      int ii = 0;
      for (ii = 0; ii < arrCustNos.length; ii++) {
        if (keyNo.equals(arrCustNos[ii].toString().trim().replaceAll("'", "")))
          break; // �䥻ID�b�}�C������m
      }
      String custName = arrCustNames[ii].toString().trim();
      String custNo = arrCustNos[ii].toString().trim().replaceAll("'", "");
      System.out.println("AML031 �Ȥ�>>>" + custNo+custName);

      String[][] retC = this.getCustDayPayA4(startEDate, endEDate, custNo);
      float dayAmt = 0; // �C��֭p
      for (int i = 0; i < retC.length; i++) {
        String keyDate = retC[i][3].toString().trim();
        float amt = Float.parseFloat(retC[i][2].toString().trim());
        dayAmt += amt;
        System.out.println("AML031 ���>>>" + keyDate + "// ���B>>>" + amt);
      }
      System.out.println("AML031 ���>>>" + aml.getTrxDate() + "// ���֭p>>>" + dayAmt);

      if (dayAmt >= 500000) {
        //�֭p�F��
        String msg = amlDesc.replaceAll("<customName>", custName);
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // �g�ŦX����
        aml.setCustomId(custNo);
        aml.setCustomName(custName);
        this.insSale070(aml);
        this.insCR400(aml);

        // �q�M�椤�����ŦX���s��
        customNoList.remove("'" + custNo + "'");
      }

      // TODO: �g���ŦX�A��
    }

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }

  /**
   * AML 0041 �P�@�Ȥ�3����~�餺�A�֭pú��{���W�L50�U��, �t���ˮִ��ܳq���C Ĳ�o���� :
   * ���ڳ��U�����{���A�B�Q��B�e�饼�R���j�B(AML003) �d�߭q��γ�@�Ȥ�T�餺���ڲ֭p�A�W�L50�U���ͺA��
   * 
   * @param aml
   * @return
   * @throws Throwable
   */
  public Result chkAML0041(AMLBean aml, String keyNo, String type) throws Throwable {
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    StringBuilder sbSQL = new StringBuilder();
    aml.setAMLNo("004");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(rsStat.NODATA_AMLMSG[0]));
      rs.setReturnMsg(rsStat.NODATA_AMLMSG[3]);
      return rs;
    }

    String startEDate = kutil.getDateAfterNDays(aml.getTrxDate().trim(), "/", -2);
    String endEDate = aml.getTrxDate().trim();

    System.out.println("AML041 start>>>");
    System.out.println("AML041 ����d��>>>" + startEDate + " AND " + endEDate);
    System.out.println("AML041 �q�渹�X>>>" + aml.getOrderNos()); // sql in �榡
    System.out.println("AML041 �Ȥ�IDS>>>" + aml.getCustomNos()); // sql in �榡
    System.out.println("AML041 �Ȥ�NAMES>>>" + aml.getCustomNames()); // name�Bname�Bname

    // �q��
    aml.setCustomId(aml.getCustomNos().replaceAll("'", ""));
    aml.setCustomNames(aml.getCustomNames());
    if ("order".equals(type)) {
      String orderNo = keyNo;
      System.out.println("AML041 �q��>>>"+ orderNo);

      String[][] retAML0041 = this.getOrderDayPayA4(startEDate, endEDate, orderNo);
      float totalAmt = 0; // �֭p�`�B
      float dayAmt = 0; // �C��֭p
      String lastKey = "";
      for (int i = 0; i < retAML0041.length; i++) {
        String keyDate = retAML0041[i][3].toString().trim();
        float amt = Float.parseFloat(retAML0041[i][2].toString().trim());

        // key���P �C��[�i�`�B���k�s
        if (!lastKey.equals(keyDate)) {
          System.out.println("AML041 ����p�O" + keyDate + ">>>" + dayAmt);
          totalAmt += dayAmt;
          dayAmt = 0;
        }

        dayAmt += amt;
        System.out.println("AML041 ���>>>" + keyDate + "// ���B>>>" + amt);

        // �C��֭p�F�j�B�зǡA�h�X�y�{(�T�餺���j�B�Y����֭ܲp)
        if (dayAmt >= 500000) {
          System.out.println("AML041 ����j�B" + keyDate + ">>>�A�y�{����");
          break;
        }

        // �̫�@�� = �C��[�^�`�B
        if (i == retAML0041.length - 1)
          totalAmt += dayAmt;

        lastKey = keyDate;
      }
      System.out.println("AML041 �T��֭p>>>" + totalAmt);

      if (totalAmt >= 500000) {
        // �T��֭p�F�СA���ͺA��
        String msg = amlDesc.replaceAll("<customName>", aml.getCustomNames());
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // �g�ŦX����
        aml.setOrderNo(orderNo);
        this.insSale070(aml);
        this.insCR400(aml);

        // �q�M�椤�����ŦX���s��
        customNoList.remove("'" + orderNo + "'");
      }

      /*
       * �Pı�|�����D�A�����g 
       * //�g���ŦX���� 
       * for (int i = 0; i < orderList.size(); i++) {
       * aml.setOrderNo(orderList.get(i).toString().replaceAll("'", ""));
       * aml.setErrMsg("���ŦX"); this.insSale070(aml); }
       */
    }

    // �Ȥ�
    aml.setOrderNo("");
    if ("custom".equals(type)) {
      String[] arrCustNos = aml.getCustomNos().split(",");
      String[] arrCustNames = aml.getCustomNames().split("�B");
      int ii = 0;
      for (ii = 0; ii < arrCustNos.length; ii++) {
        if (keyNo.equals(arrCustNos[ii].toString().trim().replaceAll("'", "")))
          break; // �䥻ID�b�}�C������m
      }
      String custName = arrCustNames[ii].toString().trim();
      String custNo = arrCustNos[ii].toString().trim().replaceAll("'", "");
      System.out.println("AML041 �Ȥ�>>>"+custNo+custName);

      String[][] retCustA4 = this.getCustDayPayA4(startEDate, endEDate, custNo);
      float totalAmt = 0; // �֭p�`�B
      String lastKey = "";
      float dayAmt = 0; // �C��֭p
      for (int i = 0; i < retCustA4.length; i++) {
        String keyDate = retCustA4[i][3].toString().trim();
        float amt = Float.parseFloat(retCustA4[i][2].toString().trim());

        // key���P �C��[�i�`�B���k�s
        if (!lastKey.equals(keyDate)) {
          totalAmt += dayAmt;
          dayAmt = 0;
        }

        dayAmt += amt;
        System.out.println("AML041 ���>>>" + keyDate + "// ���B>>>" + amt);

        // �C��֭p�F�j�B�зǡA�h�X�y�{(�T�餺���j�B�Y����֭ܲp)
        if (dayAmt >= 500000) {
          System.out.println("AML041 ����j�B" + keyDate + ">>>�A�y�{����");
          break;
        }

        // �̫�@�� = �C��[�^�`�B
        if (i == retCustA4.length - 1)
          totalAmt += dayAmt;

        lastKey = keyDate;
      }
      System.out.println("AML041 �T��֭p>>>" + totalAmt);

      if (totalAmt >= 500000) {
        // �T��֭p�F�СA���ͺA��
        String msg = amlDesc.replaceAll("<customName>", custName);
        sbMsg.append(msg).append("\n");
        aml.setErrMsg(msg);

        // �g�ŦX����
        aml.setCustomId(custNo);
        aml.setCustomName(custName);
        this.insSale070(aml);
        this.insCR400(aml);

        // �q�M�椤�����ŦX���s��
        customNoList.remove("'" + custNo + "'");
      }

      /*
       * �Pı�|�����D�A�����g 
       * //�g���ŦX���� 
       * for (int i = 0; i < customNoList.size(); i++) { String
       * noMatchId = customNoList.get(i).toString().replaceAll("'", "");
       * aml.setCustomId(noMatchId);
       * aml.setCustomName(mapCustomers.get(noMatchId).toString());
       * aml.setErrMsg("���ŦX"); this.insSale070(aml); }
       */
    }

    rs.setReturnCode(Integer.parseInt(rsStat.SUCCESS[0]));
    rs.setData(sbMsg.toString());
    return rs;
  }
  
  public String[][] getOrderDayPayA4(String sDate, String eDate, String orderNo) throws Throwable {
    StringBuilder sbSQL = new StringBuilder();
    sbSQL.append("select c.OrderNo , a.DocNo ");
    sbSQL.append(", (a.CashMoney/(select COUNT(cc.OrderNo) from Sale05M086 cc where cc.DocNo=a.DocNo group by cc.DocNo)) as CashMoney ");
    sbSQL.append(", a.EDate ");
    sbSQL.append("from Sale05M080 a , sale05m086 c where 1=1 and a.DocNo=c.DocNo ");
    sbSQL.append("and a.EDate BETWEEN '" + sDate + "' AND '" + eDate + "' and c.OrderNo = '" + orderNo + "' ");
    sbSQL.append("ORDER BY c.OrderNo DESC , a.edate DESC ");
    String[][] ret = dbSale.queryFromPool(sbSQL.toString());
    return ret;
  }

  public String[][] getCustDayPayA4(String sDate, String eDate, String custNo) throws Throwable {
    StringBuilder sbSQL = new StringBuilder();
    sbSQL.append("select c.CustomNo ,c.CustomName ");
    sbSQL.append(", (a.CashMoney * (select top 1 cc.Percentage from Sale05M084 cc where cc.DocNo=a.DocNo and cc.CustomNo=c.CustomNo)/100) as CashMoney ");
    sbSQL.append(", a.EDate ");
    sbSQL.append("from Sale05M080 a , sale05m084 c where 1=1 and a.DocNo=c.DocNo ");
    sbSQL.append("and a.EDate BETWEEN '" + sDate + "' AND '" + eDate + "' and c.CustomNo = '" + custNo + "' ");
    sbSQL.append("ORDER BY c.CustomNo DESC ,a.edate DESC ");
    String[][] ret = dbSale.queryFromPool(sbSQL.toString());
    return ret;
  }

  // 21. �F�vPEPS X171
  public String chkX171_PEPS(Map cons) throws Throwable {
    cons.put("AMLNo", "021");
    String rsMsg = "";

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(cons.get("AMLNo").toString().trim()) == null ? "" : mapAMLMsg.get(cons.get("AMLNo").toString().trim()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rsMsg = "[Error : �d�L���˺A����]";
      return rsMsg;
    }
    // System.out.println("amlDesc>>>" + amlDesc);

    String sql = "SELECT * FROM CRCLNAPF " + "WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L "
        + "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE " + "AND L.CONTROLCLASSIFICATIONCODE ='X171' " + "AND C.REMOVEDDATE >= '" + strNowDate2 + " " + strNowTime2
        + "' ) " + "AND ISREMOVE = 'N'  " + "AND CUSTOMERID ='" + cons.get("customId") + "' ";
    String[][] retQuery = db400.queryFromPool(sql);

    if (retQuery.length > 0) {
      // �ŦX�˺A
      String[] retQuery1 = retQuery[0];
      String cusId = retQuery1[3].toString().trim();
      String cusName = retQuery1[5].toString().trim();
      String customTitle = cons.get("customTitle").toString();
      System.out.println("cusName>>>" + cusName);
      System.out.println("customTitle>>>" + customTitle);
      rsMsg += (amlDesc.replaceAll("<customTitle>", customTitle).replaceAll("<customName>", cusName) + "<br>");
      cons.put("errMsg", rsMsg);

      this.insSale070(cons);
      this.insCR400(cons);
    } else {
      // ���ŦX�˺A
      cons.put("errMsg", "���ŦX");
      this.insSale070(cons);
    }

    return rsMsg;
  }

  // 18. ����W�� X181
  public String chkX181_Sanctions(Map cons) throws Throwable {
    cons.put("AMLNo", "018");
    String rsMsg = "";

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(cons.get("AMLNo").toString().trim()) == null ? "" : mapAMLMsg.get(cons.get("AMLNo").toString().trim()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rsMsg = "[Error : �d�L���˺A����]";
      return rsMsg;
    }

    String sql = "SELECT * FROM CRCLNAPF " + "WHERE CONTROLLISTNAMECODE IN (SELECT DISTINCT C.CONTROLLISTNAMECODE FROM CRCLNCPF C,CRCLCLPF L "
        + "WHERE C.CONTROLCLASSIFICATIONCODE=L.CONTROLCLASSIFICATIONCODE " + "AND L.CONTROLCLASSIFICATIONCODE ='X181' " + "AND C.REMOVEDDATE >= '" + strNowDate2 + " " + strNowTime2
        + "' ) " + "AND ISREMOVE = 'N'  " + "AND CUSTOMERID ='" + cons.get("customId") + "' ";
    String[][] retQuery = db400.queryFromPool(sql);

    if (retQuery.length > 0) {
      // �ŦX�˺A
      String[] retQuery1 = retQuery[0];
      String cusId = retQuery1[3].toString().trim();
      String cusName = retQuery1[5].toString().trim();
      String customTitle = cons.get("customTitle").toString();
      rsMsg += (amlDesc.replaceAll("<customTitle>", customTitle).replaceAll("<customName>", cusName) + "<br>");
      cons.put("errMsg", rsMsg);

      this.insSale070(cons);
      this.insCR400(cons);
    } else {
      // ���ŦX�˺A
      cons.put("errMsg", "���ŦX");
      this.insSale070(cons);
    }

    return rsMsg;
  }

  /**
   * �gSale log Bean�� 
   * funcName(Func) : �\�ඵ EX ���W�B�ʫ��ҩ��� 
   * funcName2(RecordType) : �\�ඵ�Ӷ� EX �Ȥ��ơB�N�z�H��� 
   * ActionName(ActionName) : �s�W�B�ק�B�R�� 
   * errMsg : �ŦX���˺A���e�A�ά�"���A��" or "���ŦX" 
   * AMLNo : AML�˺A�s��
   */
  public String insSale070(AMLBean aml) throws Throwable {
    String rsMsg = "0";
    String sql = "INSERT INTO Sale05M070 "
        + "(DocNo,OrderNo, ContractNo, ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate, EDate, CDate, SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
        + "VALUES " 
        + "('" + aml.getDocNo() + "','" + aml.getOrderNo() + "', '"+ aml.getContractNo()+"', '" + strProjectID1 + "','" + intRecordNo + "','" + strActionNo + "', " + "'" + aml.getFuncName() + "' "
        + ",'"+ aml.getFuncName2() + "','" + strActionName + "','" + aml.getErrMsg() + "', " + "'" + aml.getCustomId() + "','" + aml.getCustomName() + "' "
        + ",'" + aml.getOrderDate() + "' ,'"+aml.geteDate()+"' ,'"+aml.getcDate()+"' "
        + ",'RY','773','" + aml.getAMLNo() + "','" + aml.getErrMsg() + "', " + "'" + empNo + "', '" + rocNowDate + "', '" + strNowTime + "') ";
    dbSale.execFromPool(sql);
    intRecordNo++;
    return rsMsg;
  }

  public String insCR400(AMLBean aml) throws Throwable {
    String pKey = "0";
    String amlNo = aml.getAMLNo(); // ���P�\��U����즳�Ҥ��P
    if ("001".equals(amlNo) || "002".equals(amlNo) || "003".equals(amlNo) || "004".equals(amlNo)) {
      pKey = aml.getDocNo();
    } else if ("018".equals(amlNo) || "021".equals(amlNo)) {
      pKey = aml.getOrderNo();
    }

    String customId = aml.getCustomId().toString().split(",")[0].toString();
    String rsMsg = "";
    String sql = "INSERT INTO PSHBPF " + "(SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) " + "VALUES " + "('RY', '" + pKey + "', '" + rocNowDate
        + "', '" + customId + "', '" + aml.getCustomName() + "'" + ", '773', '" + aml.getAMLNo() + "', " + "'" + aml.getErrMsg() + "','" + empNo + "','" + rocNowDate + "','"
        + strNowTime + "') ";
    db400.execFromPool(sql);
    return rsMsg;
  }

  /**
   * funcName(Func) : �\�ඵ EX ���W�B�ʫ��ҩ��� funcName2(RecordType) : �\�ඵ2 EX �Ȥ��ơB�N�z�H���
   * ActionName(ActionName) : �s�W�B�ק�B�R�� errMsg : �ŦX���˺A���e�A�ά�"���A��"�B"���ŦX" AMLNo :
   * AML�˺A�s��
   */
  public String insSale070(Map cons) throws Throwable {
    String rsMsg = "";
    String sql = "INSERT INTO Sale05M070 "
        + "(OrderNo,ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate,SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) " + "VALUES "
        + "('" + strOrderNo + "','" + strProjectID1 + "','" + intRecordNo + "','" + strActionNo + "', " + "'" + cons.get("funcName") + "','" + cons.get("funcName2") + "','"
        + strActionName + "','" + cons.get("errMsg") + "', " + "'" + cons.get("customId") + "','" + cons.get("customName") + "','" + strOrderDate + "','RY','773','"
        + cons.get("AMLNo") + "','" + cons.get("errMsg") + "', " + "'" + empNo + "', '" + rocNowDate + "', '" + strNowTime + "') ";

    try {
      dbSale.execFromPool(sql);
    } catch (Exception ex) {
      rsMsg = "[Error : ins070 error]";
      System.out.println(">>>ins070 Error : " + cons);
      ex.printStackTrace();
    }

    return rsMsg;
  }

  public String insCR400(Map cons) throws Throwable {
    String rsMsg = "";
    String sql = "INSERT INTO PSHBPF " + "(SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) " + "VALUES ('RY', '" + strOrderNo + "', '" + rocNowDate
        + "', '" + cons.get("customId") + "', '" + cons.get("customName") + "', '773', '" + cons.get("AMLNo") + "', " + "'" + cons.get("errMsg") + "','" + empNo + "','"
        + rocNowDate + "','" + strNowTime + "') ";

    try {
      db400.execFromPool(sql);
    } catch (Exception ex) {
      rsMsg = "[Error : ins400 error]";
      System.out.println(">>>ins400 Error : " + cons);
      ex.printStackTrace();
    }

    return rsMsg;
  }

  // ���oAML�A�˻���
  public Map getAMLDesc() {
    return mapAMLMsg;
  }

}
