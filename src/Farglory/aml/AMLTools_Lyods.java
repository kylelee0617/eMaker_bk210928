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
package Farglory.aml;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.fglife.soap.cr.MainReply;
import com.fglife.soap.cr.RenewRelatedReply;

import Farglory.util.Result;
import Farglory.util.ResultStatus;
import Farglory.util.TalkBean;
import jcx.db.talk;
import jcx.jform.bvalidate;

/**
 * �~���U�A�ˬd��
 * 
 * @author B04391
 *
 */

public class AMLTools_Lyods extends bvalidate {
  // DB
  talk db400 = null;
  talk dbSale = null;
  talk dbEIP = null;
  TalkBean tBean = null;
  boolean isTestServer = true;
  String lyodsSoapURL = "";

  // param �ǤJ��
  StringBuilder sbRsMsg = new StringBuilder();
  String strDocNo = "";
  String strOrderNo = ""; // �ʫ��ҩ���s��
  String strProjectID1 = ""; // �קO�N�X
  String strOrderDate = ""; // �ʫ��ҩ�����
  String strActionName = "�s��"; // �sSale05M070�ϥ�
  String strActionNo = ""; // �sSale05M070�ϥ�
  String func = "";

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

  AMLyodsBean aml;

  public AMLTools_Lyods() throws Throwable {
  }

  public AMLTools_Lyods(AMLyodsBean aml) throws Throwable {
    db400 = aml.getDb400CRM();
    dbSale = aml.getDbSale();
    dbEIP = aml.getDbEIP();
    tBean = aml.gettBean();

    // config
    isTestServer = aml.isTestServer();
    lyodsSoapURL = aml.getLyodsSoapURL();

    this.aml = aml;
    strProjectID1 = aml.getProjectID1();
    strOrderDate = aml.getOrderDate();
    strActionName = aml.getActionName();
    func = aml.getFunc();

    // LOG���,�ɶ�
    this.getDateTime();

    // ���u�s�� & EIPNO
    this.getEmpNo();

    // �Ǹ�
    this.getRecordNo070ByType(aml);

    // actionNo
    this.getActionNo();

    // ���oAML�A�ˤ��廡��
    this.getAML();

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

  // ��̷ӨϥΥ\����recordNo
  public void getRecordNo070ByType(AMLyodsBean aml) throws Throwable {
    String stringSQL = "";
    if (aml.getFunc().indexOf("����") == 0 && !"".equals(aml.getDocNo())) {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE DocNo ='" + aml.getDocNo() + "' ";
    } else if (aml.getFunc().indexOf("�X��") == 0 && !"".equals(aml.getContractNo())) {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE ContractNo ='" + aml.getOrderNo() + "' ";
    } else {
      stringSQL = "SELECT MAX(RecordNo) AS MaxNo FROM Sale05M070 WHERE OrderNo ='" + aml.getOrderNo() + "' ";
    }
    String[][] ret05M070 = dbSale.queryFromPool(stringSQL);
    if (ret05M070.length > 0 && !"".equals(ret05M070[0][0].trim())) {
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
    String stringSQL = "SELECT EMPNO FROM FGEMPMAP where FGEMPNO ='" + this.aml.getEmakerUserNo() + "'";
    retEip = dbEIP.queryFromPool(stringSQL);
    if (retEip.length > 0) {
      empNo = retEip[0][0];
    } else {
      System.out.println(">>>None EmpNo<<<");
      empNo = "B04391";
    }
  }

  public boolean check(String value) throws Throwable {
    return false;
  }

  // �ݩR������
  public String getLyodsHits(RiskCustomBean cBean) throws Throwable {
    System.out.println(">>>getLyodsHits Start");
    StringBuilder sbMsg = new StringBuilder();

    // Lyods GO
    this.aml.setRiskResult("N"); // ��� �S�p��

    LyodsTools lyodsTools = new LyodsTools(this.aml);
    Result result = lyodsTools.checkRisk();
    if (result.getRsStatus()[0] != ResultStatus.SUCCESS[0]) {
      System.out.println("getLyodsHits Error>>>" + result.getExp().toString());
      return "ERROR";
    }

    MainReply mainReply = (MainReply) result.getData();
    if (mainReply != null) {
      if (StringUtils.isBlank(mainReply.getMessage().toString())) {
        List hits = mainReply.getHitStatusList().getHitStatus();
        sbMsg.append("�R��:");
        for (int ii = 0; ii < hits.size(); ii++) {
          String hit = hits.get(ii).toString().trim();
          sbMsg.append(hit).append(";");
        }
      } else {
        sbMsg.append(mainReply.getMessage().toString());
      }

    } else {
      sbMsg.append("Lyods Null");
    }

    System.out.println(">>>getLyodsHits End");
    return sbMsg.toString();
  }

  /**
   * ���A��
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result insNotUse(int[] noUseAML, RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";

    for (int ii = 0; ii < noUseAML.length; ii++) {
      String amlNo = "";
      if (noUseAML[ii] < 10) {
        amlNo = "00" + noUseAML[ii];
      } else {
        amlNo = "0" + noUseAML[ii];
      }
      aml.setAMLNo(amlNo);
      aml.setErrMsg("���A��");

      this.insSale070(cBean);
    }

    rs.setData(rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 5.�N�z�H�D�G����
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result chkAML005(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    String agentRel = cBean.getAgentRel();
    aml.setAMLNo("005");

    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim();
    rsMsg = amlDesc.replaceAll("<customName>", cBean.getCustomName()).replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("<customName2>", cBean.getCustomName2())
        .replaceAll("<customTitle2>", cBean.getCustTitle2());

    if (StringUtils.equals(agentRel, "�B��") || StringUtils.equals(agentRel, "��L")) {
      aml.setErrMsg(rsMsg);
      this.insCR400(cBean);
    } else {
      // ���ŦX
      aml.setErrMsg("���ŦX");
    }
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);

    return rs;
  }

  /**
   * 8.���N�z�H
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result chkAML008(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("008");

    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim();
    rsMsg = amlDesc.replaceAll("<customName>", cBean.getCustomName()).replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("<customName2>", cBean.getCustomName2())
        .replaceAll("<customTitle2>", cBean.getCustTitle2());

    aml.setErrMsg(rsMsg);
    this.insCR400(cBean);
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 9. �ꮣ�a��
   * 
   * @param aml
   * @param keyNo
   * @param type
   * @return
   * @throws Throwable
   */
  public Result chkAML009(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("009");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(ResultStatus.NODATA_AMLMSG[0]));
      rs.setReturnMsg(ResultStatus.NODATA_AMLMSG[3]);
      return rs;
    }

    String sql = "SELECT CZ07 FROM PDCZPF WHERE CZ01='NATIONCODE' AND CZ09='" + cBean.getCountryName() + "' ";
    String[][] ret009 = db400.queryFromPool(sql);
    aml.setErrMsg("���ŦX");
    if (ret009.length > 0) {
      String strCZ07 = ret009[0][0].trim();
      if ("�u���k��".equals(strCZ07)) {
        rsMsg = amlDesc.replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("customName", cBean.getCustomName());

        // As400 �ŦX
        aml.setErrMsg(rsMsg);
        this.insCR400(cBean);
      }
    }
    // �L��hit�P�_���ninsert Sale05M070
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 12.�X���ĤT�H
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result chkAML012(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("012");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    rsMsg = amlDesc.replaceAll("<customName>", cBean.getCustomName()).replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("<customName2>", cBean.getCustomName2())
        .replaceAll("<customTitle2>", cBean.getCustTitle2());
    aml.setErrMsg(rsMsg);
    this.insCR400(cBean);
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  /**
   * 17.�¦W�� (�]�t����)
   * 
   * @param RiskCustomBean
   * @return
   * @throws Throwable
   */
  public Result chkAML017(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("017");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(ResultStatus.NODATA_AMLMSG[0]));
      rs.setReturnMsg(ResultStatus.NODATA_AMLMSG[3]);
      return rs;
    }

    aml.setErrMsg("���ŦX");
    if (StringUtils.equals(cBean.getbStatus(), "Y") || StringUtils.equals(cBean.getbStatus(), "Y")) {
      rsMsg = amlDesc.replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("customName", cBean.getCustomName());

      // As400 �ŦX
      aml.setErrMsg(rsMsg);
      this.insCR400(cBean);
    }
    // �L��hit�P�_���ninsert Sale05M070
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  // 18. ����W�� X181
  public Result chkAML018_San(RiskCustomBean cBean) throws Throwable {
    System.out.println(">>>chkAML018_San Start");
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    aml.setAMLNo("018");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setRsStatus(ResultStatus.NODATA_AMLMSG);
      return rs;
    }

    String custTitle = cBean.getCustTitle();
    String custNo = cBean.getCustomNo();
    String custName = cBean.getCustomName();

    // Lyods GO
    this.aml.setRiskResult("N"); // ��� �S�p��
    this.aml.setCheckAll("N"); // �u�ݨ��
    this.aml.setCustBean(cBean);
    System.out.println("AML018 Test1");
    LyodsTools lyodsTools = new LyodsTools(this.aml);
    System.out.println("AML018 Test2");
    Result result = lyodsTools.checkRisk();
    System.out.println("AML018 Test3");
    if (result.getRsStatus()[0] != ResultStatus.SUCCESS[0]) {
      System.out.println("chkAML018_San Error>>>" + result.getExp().toString());
      return result;
    }

    MainReply mainReply = (MainReply) result.getData();
    boolean isHit = false;
    if (mainReply != null) {
      if (StringUtils.isBlank(mainReply.getMessage().toString())) {
        List hits = mainReply.getHitStatusList().getHitStatus();
        for (int ii = 0; ii < hits.size(); ii++) {
          String hit = hits.get(ii).toString().trim();
          if (hit.equals(HitStatus.SAN[1])) {
            sbMsg.append(amlDesc.replaceAll("<customTitle>", custTitle).replaceAll("<customName>", custName)).append("<br>");
            isHit = true;
            break;
          }
        }
      } else {
        sbMsg.append(mainReply.getMessage().toString());
      }

      if (isHit) {
        aml.setErrMsg(sbMsg.toString());
        this.insCR400(cBean);
      } else {
        aml.setErrMsg("���ŦX");
      }
      this.insSale070(cBean);

      rs.setData(sbMsg.toString());
      rs.setRsStatus(ResultStatus.SUCCESS);
    } else {
      rs.setData("Lyods Null");
      rs.setRsStatus(ResultStatus.ERROR);
    }

    System.out.println(">>>chkAML018_San End");
    return rs;
  }

  /**
   * 19.�Q�`���Y�H
   * 
   * @param RiskCustomBean
   * @return
   * @throws Throwable
   */
  public Result chkAML019(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();
    String rsMsg = "";
    aml.setAMLNo("019");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setReturnCode(Integer.parseInt(ResultStatus.NODATA_AMLMSG[0]));
      rs.setReturnMsg(ResultStatus.NODATA_AMLMSG[3]);
      return rs;
    }

    aml.setErrMsg("���ŦX");
    if (StringUtils.equals(cBean.getrStatus(), "Y")) {
      rsMsg = amlDesc.replaceAll("<customTitle>", cBean.getCustTitle()).replaceAll("customName", cBean.getCustomName());

      // As400 �ŦX
      aml.setErrMsg(rsMsg);
      this.insCR400(cBean);
    }
    // �L��hit�P�_���ninsert Sale05M070
    this.insSale070(cBean);

    rs.setData(rsMsg.length() > 0 ? rsMsg + "<br>" : rsMsg);
    rs.setRsStatus(ResultStatus.SUCCESS);
    return rs;
  }

  // 21. �F�vPEPS X171
  public Result chkAML021_PEPS(RiskCustomBean cBean) throws Throwable {
    System.out.println(">>>chkAML021_PEPS Start");
    Result rs = new Result();
    StringBuilder sbMsg = new StringBuilder();
    aml.setAMLNo("021");

    // �����o�˺A����
    String amlDesc = mapAMLMsg.get(aml.getAMLNo()).toString().trim(); // �˺A��ĵ��r
    if ("".equals(amlDesc)) {
      rs.setRsStatus(ResultStatus.NODATA_AMLMSG);
      return rs;
    }

    String custTitle = cBean.getCustTitle();
    String custNo = cBean.getCustomNo();
    String custName = cBean.getCustomName();

    // Lyods GO
    this.aml.setRiskResult("N"); // ��� �S�p��
    this.aml.setCustBean(cBean);
    LyodsTools lyodsTools = new LyodsTools(this.aml);
    Result result = lyodsTools.checkRisk();
    if (result.getRsStatus()[0] != ResultStatus.SUCCESS[0]) {
      System.out.println("chkAML021_PEPS Error>>>" + result.getExp().toString());
      return result;
    }

    MainReply mainReply = (MainReply) result.getData();
    boolean isHit = false;
    if (mainReply != null) {
      if (StringUtils.isBlank(mainReply.getMessage().toString())) {
        List hits = mainReply.getHitStatusList().getHitStatus();
        for (int ii = 0; ii < hits.size(); ii++) {
          String hit = hits.get(ii).toString().trim();
          if (hit.equals(HitStatus.DPEP[1])) {
            sbMsg.append(amlDesc.replaceAll("<customTitle>", custTitle).replaceAll("<customName>", custName)).append("<br>");
            isHit = true;
            break;
          }
        }
      } else {
        sbMsg.append(mainReply.getMessage().toString());
      }

      if (isHit) {
        aml.setErrMsg(sbMsg.toString());
        this.insCR400(cBean);
      } else {
        aml.setErrMsg("���ŦX");
      }
      this.insSale070(cBean);

      rs.setData(sbMsg.toString());
      rs.setRsStatus(ResultStatus.SUCCESS);
    } else {
      rs.setData("<<Lyods Return Null>>");
      rs.setRsStatus(ResultStatus.ERROR);
    }

    System.out.println(">>>chkAML021_PEPS End");
    return rs;
  }

  /**
   * ��s���p�H
   * 
   * @param cBean
   * @return
   * @throws Throwable
   */
  public Result renewRelated(RiskCustomBean cBean) throws Throwable {
    Result rs = new Result();

    aml.setCustBean(cBean);
    // Lyods GO
    LyodsTools lyodsTools = new LyodsTools(aml);
    Result result = lyodsTools.renewRelated();
    if (result.getExp() != null) {
      System.out.println("renewRelated Error>>>" + result.getExp().toString());
      return result;
    }

    RenewRelatedReply renewRelatedReply = (RenewRelatedReply) result.getData();
    if (renewRelatedReply.getMessage().length() == 0) {
      rs.setRsStatus(ResultStatus.SUCCESS);
      rs.setData(renewRelatedReply);
    } else {
      rs.setRsStatus(ResultStatus.ERROR);
      rs.setReturnMsg(renewRelatedReply.getMessage());
    }

    return lyodsTools.renewRelated();
  }

  /**
   * �gSale log Bean�� Func : �\�ඵ EX ���W�B�ʫ��ҩ��� RecordType : �\�ඵ�Ӷ� EX �Ȥ��ơB�N�z�H���
   * ActionName : �s�W�B�ק�B�R�� errMsg : �ŦX���˺A���e�A�ά� "���A��" or "���ŦX" AMLNo : AML�˺A�s��
   */
  public String insSale070(RiskCustomBean cBean) throws Throwable {
    String rsMsg = "0";
    String sql = "INSERT INTO Sale05M070 "
        + "(DocNo,OrderNo, ContractNo, ProjectID1,RecordNo,ActionNo,Func,RecordType,ActionName,RecordDesc,CustomID,CustomName,OrderDate, EDate, CDate, SHB00,SHB06A,SHB06B,SHB06,SHB97,SHB98,SHB99) "
        + "VALUES " + "('" + aml.getDocNo() + "','" + aml.getOrderNo() + "', '" + aml.getContractNo() + "', '" + strProjectID1 + "','" + intRecordNo + "','" + strActionNo + "', "
        + "'" + aml.getFunc() + "' " + ",'" + aml.getRecordType() + "','" + strActionName + "','" + aml.getErrMsg() + "', " + "'" + cBean.getCustomNo() + "','"
        + cBean.getCustomName() + "' " + ",'" + aml.getOrderDate() + "' ,'" + aml.geteDate() + "' ,'" + aml.getcDate() + "' " + ",'RY','773','" + aml.getAMLNo() + "','"
        + aml.getErrMsg() + "', " + "'" + empNo + "', '" + rocNowDate + "', '" + strNowTime + "') ";
    dbSale.execFromPool(sql);
    intRecordNo++;
    return rsMsg;
  }

  public String insCR400(RiskCustomBean cBean) throws Throwable {
    String pKey = "0";
    String amlNo = aml.getAMLNo(); // ���P�\��U����즳�Ҥ��P
    if ("001".equals(amlNo) || "002".equals(amlNo) || "003".equals(amlNo) || "004".equals(amlNo)) {
      pKey = aml.getDocNo();
    } else if ("018".equals(amlNo) || "021".equals(amlNo)) {
      pKey = aml.getOrderNo();
    }

    String rsMsg = "";
    String sql = "INSERT INTO PSHBPF " + "(SHB00, SHB01, SHB03, SHB04, SHB05, SHB06A, SHB06B, SHB06, SHB97, SHB98, SHB99) " + "VALUES " + "('RY', '" + pKey + "', '" + rocNowDate
        + "', '" + cBean.getCustomNo() + "', '" + cBean.getCustomName() + "'" + ", '773', '" + aml.getAMLNo() + "', " + "'" + aml.getErrMsg() + "','" + empNo + "','" + rocNowDate
        + "','" + strNowTime + "') ";
    db400.execFromPool(sql);
    return rsMsg;
  }

  // ���oAML�A�˻���
  public Map getAMLDesc() {
    return mapAMLMsg;
  }

}
