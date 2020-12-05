package Farglory.util ;

public class Result2 {

  private int returnCode = 0;
  private String returnMsg = "ok";
  private Exception exp;
  private Object data;
  private EnumRsStatus rsStat;

  public Result2() {
  }

  public Result2(Object data) {
    this.data = data;
  }
  
  public Result2(int code) {
    this.returnCode= code;
  }

  public Result2(int returnCode, Object data) {
    this.returnCode = returnCode;
    this.data = data;
  }

  public Result2(int returnCode, String msg) {
    this.returnCode = returnCode;
    this.returnMsg = msg;
  }

  //start
  public int getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(int returnCode) {
    this.returnCode = returnCode;
  }

  public String getReturnMsg() {
    return returnMsg;
  }

  public void setReturnMsg(String returnMsg) {
    this.returnMsg = returnMsg;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public Exception getExp() {
    return exp;
  }

  public void setExp(Exception exp) {
    this.exp = exp;
  }

  public EnumRsStatus getRsStat() {
    return rsStat;
  }

  public void setRsStat(EnumRsStatus rsStat) {
    this.rsStat = rsStat;
  }
  
}
