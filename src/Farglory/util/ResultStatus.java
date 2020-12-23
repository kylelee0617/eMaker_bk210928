/**
 * 2020-03-16 kyle add Enum for AML
 */
package Farglory.util ;

public class ResultStatus {
	public static String[] SUCCESS = {"0" , "success" , "成功" , "執行成功"};
	public static String[] SUCCESSBUTSOMEERROR = {"1" , "successButSomeError" , "部分失敗" , "執行成功，但有部分發生問題。"};
	public static String[] NODATA = {"700" , "no data" , "查無資料" , "查無資料"};
	public static String[] NODATA_AMLMSG = {"710" , "NO AMLMSG" , "查無態樣說明" , "Error: 查無此樣態說明"};
	public static String[] ERROR = {"9999" , "Error" , "失敗" , "網路開小差惹"};
	
}
