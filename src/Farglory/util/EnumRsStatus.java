/**
 * 2020-03-16 kyle add Enum for AML
 */
package Farglory.util ;

public enum EnumRsStatus {
	SUCCESS(0 , "success" , "成功" , "執行成功")
	,NODATA(700 , "no data" , "查無資料" , "查無資料")
	,NODATA_AMLMSG(710 , "NO AMLMSG" , "查無態樣說明" , "Error: 查無此樣態說明")
	,ERROR(9999 , "Error" , "失敗" , "網路開小差惹");

	private int code;
	private String ename;
	private String cname;
	private String desc;

	private EnumRsStatus(int code , String ename , String cname , String desc) {
		this.code = code;
		this.ename = ename;
		this.cname = cname;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getCname()
	{
		return cname;
	}

	public void setCname(String cname)
	{
		this.cname = cname;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
