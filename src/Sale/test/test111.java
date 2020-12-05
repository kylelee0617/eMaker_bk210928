package Sale.test;
import javax.swing.*;
import jcx.jform.bproc;
import java.io.*;
import java.util.*;
import jcx.util.*;
import jcx.html.*;
import jcx.db.*;

public class test111 extends bproc{
  public String getDefaultValue(String value)throws Throwable{
    
    talk dbSale = getTalk("Sale");
    String sql = "select CustomNo , CustomName , EDate , SUM(CashMoney) as CashMoney , SUM(CreditCardMoney) as CreditCardMoney , SUM(BankMoney) as BankMoney " 
               + ", SUM(d.CheckMoney) as CheckMoney " 
               + "from Sale05M080 a , Sale05M086 b , Sale05M091 c , Sale05M082 d "
               + "where 1=1 " 
               + "and a.DocNo = b.DocNo and b.OrderNo = c.OrderNo and a.DocNo = d.DocNo " 
               + "and CustomNo in ('03062401') " 
               + "group by CustomNo , CustomName , edate " 
               + "order by CustomNo , edate ";
    Hashtable h = dbSale.queryFromPoolH(sql);
    
    System.out.println(h);
    System.out.println(h.get("03062401�Ѣݦ榳"));
  
    return value;
  }
  public String getInformation(){
    return "---------------test111(test111).defaultValue()----------------";
  }
}
