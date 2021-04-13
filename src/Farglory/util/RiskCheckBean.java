package Farglory.util;

import java.util.*;

import jcx.db.talk;

/**
 * 計算風險值資料物件 210107 Kyle : 想把客戶跟實受人資料用物件傳遞，但有點懶得全改，之後再說吧....
 * 
 * @author B04391
 *
 */

public class RiskCheckBean {
	private talk dbSale = null;
	private talk dbEMail = null;
	private talk db400CRM = null;
	private talk dbEIP = null;
	private String userNo = "";
	private String[][] retCustom = new String[0][0];
	private String[][] retSBen = new String[0][0];
	private List listCustom = new ArrayList();
	private List listBen = new ArrayList();
	private String projectID1 = "";
	private String orderNo = "";
	private String orderDate = "";
	private String contractNo = "";
	private String actionText = ""; // 新增、寫入、存檔...等等
	private String func = ""; // 功能 (購屋證明單、收款、合約會審、換名...等等)
	private String recordType = ""; // 甚麼資料 (客戶資料、代理人資料、風險計算...ETC)

	private boolean updSale05M091 = false;
	private boolean updSale05M277 = false;
	private boolean updSale05M356 = false;
	private boolean upd070Log = false;
	private boolean sendMail = false;

	public talk getDbSale() {
		return dbSale;
	}

	public void setDbSale(talk dbSale) {
		this.dbSale = dbSale;
	}

	public talk getDbEMail() {
		return dbEMail;
	}

	public void setDbEMail(talk dbEMail) {
		this.dbEMail = dbEMail;
	}

	public talk getDb400CRM() {
		return db400CRM;
	}

	public void setDb400CRM(talk db400crm) {
		db400CRM = db400crm;
	}

	public talk getDbEIP() {
		return dbEIP;
	}

	public void setDbEIP(talk dbEIP) {
		this.dbEIP = dbEIP;
	}

	public String[][] getRetCustom() {
		return retCustom;
	}

	public void setRetCustom(String[][] retCustom) {
		this.retCustom = retCustom;
	}

	public String[][] getRetSBen() {
		return retSBen;
	}

	public void setRetSBen(String[][] retSBen) {
		this.retSBen = retSBen;
	}

	public String getProjectID1() {
		return projectID1;
	}

	public void setProjectID1(String projectID1) {
		this.projectID1 = projectID1;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getActionText() {
		return actionText;
	}

	public void setActionText(String actionText) {
		this.actionText = actionText;
	}

	public boolean isUpdSale05M091() {
		return updSale05M091;
	}

	public void setUpdSale05M091(boolean updSale05M091) {
		this.updSale05M091 = updSale05M091;
	}

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public boolean isUpd070Log() {
		return upd070Log;
	}

	public void setUpd070Log(boolean upd070Log) {
		this.upd070Log = upd070Log;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public boolean isUpdSale05M277() {
		return updSale05M277;
	}

	public void setUpdSale05M277(boolean updSale05M277) {
		this.updSale05M277 = updSale05M277;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public List getListCustom() {
		return listCustom;
	}

	public void setListCustom(List listCustom) {
		this.listCustom = listCustom;
	}

	public List getListBen() {
		return listBen;
	}

	public void setListBen(List listBen) {
		this.listBen = listBen;
	}

	public boolean isUpdSale05M356() {
		return updSale05M356;
	}

	public void setUpdSale05M356(boolean updSale05M356) {
		this.updSale05M356 = updSale05M356;
	}

}
