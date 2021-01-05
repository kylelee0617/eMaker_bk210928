package Invoice.utils;

import Invoice.vo.GLEAPFUFBean;
import Invoice.vo.GLEBPFUFBean;
import Invoice.vo.GLECPFUFBean;
import jcx.db.talk;
import jcx.jform.bproc;

/**
 * 糶AS400 じン
 * 
 * @author B04391
 *
 */

public class AS400SQL extends bproc {
  talk dbInvoice2 = null;
  talk dbInvoice = null;
  talk as400 = null;
  
  /**
   * 糶AS400 祇布郎
   * @param aBean
   * @return
   * @throws Throwable
   */
  public String insGLEAPFUF(GLEAPFUFBean aBean) throws Throwable {
    as400 = getTalk("AS400");
    
    StringBuilder sbSQL = new StringBuilder();
    sbSQL.append("insert into GLEAPFUF ");
    sbSQL.append("(EA01U, EA02U, EA03U, EA04U, EA05U, EA06U, EA07U, EA08U, EA09U, EA10U, EA11U, EA12U, EA13U, EA14U, EA15U, EA16U, EA17U, EA18U, EA19U, EA20U, EA21U, EA22U) ");
    sbSQL.append("values ");
    sbSQL.append("(");
    sbSQL.append("'").append(aBean.getEA01U()).append("', ");     //祇布腹絏
    sbSQL.append("'").append(aBean.getEA02U()).append("', ");     //祇布ら戳
    sbSQL.append("'").append(aBean.getEA03U()).append("', ");     //祇布羛Α
    sbSQL.append("'").append(aBean.getEA04U()).append("', ");     //そ絏
    sbSQL.append("'").append(aBean.getEA05U()).append("', ");     //场絏
    sbSQL.append("'").append(aBean.getEA06U()).append("', ");     //絏
    sbSQL.append("'").append(aBean.getEA07U()).append("', ");     //Invoice Way
    sbSQL.append("'").append(aBean.getEA08U()).append("', ");     //め腹
    sbSQL.append("'").append(aBean.getEA09U()).append("', ");     //め腹
    sbSQL.append("'").append(aBean.getEA10U()).append("', ");     //篕璶
    sbSQL.append("").append(aBean.getEA11U()).append(", ");       //ゼ祙
    sbSQL.append("").append(aBean.getEA12U()).append(", ");       //祙肂
    sbSQL.append("").append(aBean.getEA13U()).append(", ");       //祙
    sbSQL.append("'").append(aBean.getEA14U()).append("', ");     //祙
    sbSQL.append("").append(aBean.getEA15U()).append(", ");                      //ч琵肂
    sbSQL.append("").append(aBean.getEA16U()).append(", ");                      //ч琵Ω计
    sbSQL.append("'").append(aBean.getEA17U()).append("', ");     //YN
    sbSQL.append("").append(aBean.getEA18U()).append(", ");             //干Ω计
    sbSQL.append("'").append(aBean.getEA19U()).append("', ");     //紀YN
    sbSQL.append("'").append(aBean.getEA20U()).append("', ");     //眀YN
    sbSQL.append("'").append(aBean.getEA21U()).append("', ");                   //祇布矪瞶よΑ
    sbSQL.append("'").append(aBean.getEA22U().replace("祇布", "")).append("' ");      //Μ蹿/狝
    sbSQL.append(") ");
    
    return as400.execFromPool(sbSQL.toString());
  }
  
  
  public String getDefaultValue(String value) throws Throwable {
    return value;
  }
}
