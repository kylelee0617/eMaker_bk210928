package Farglory.util;

public class Transaction {
  private StringBuilder sb;
  private String sql;

  public Transaction() {
    sb = new StringBuilder();
    sb.append(" DECLARE @chk tinyint ");
    sb.append(" set @chk = 0 ");
    sb.append(" begin transaction ");
  }

  public void append(String str) {
    sb.append(" IF @chk = 0 BEGIN ");
    sb.append(str).append(" ; ");
    sb.append(" END ");
    sb.append(" IF @@Error <> 0 BEGIN SET @chk = 1 END ");
  }

  public void close() {
    sb.append(" IF @chk = 0 Commit ELSE ROLLBACK");
  }

  public String getString() {
    return sb.toString();
  }

}
