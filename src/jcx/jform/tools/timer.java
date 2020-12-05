package jcx.jform.tools;
import javax.swing.*;
import jcx.jform.bdisplay;
import jcx.util.datetime;
import java.awt.*;

public class timer implements Runnable{
	Thread me=null;
	JLabel jl=null;

	/**
     * J-form 的表單中的文字物件可以設定 自定格式
	 * 本物件為一個小時鐘,只要在自定格式加入一行
	 * <pre>
	 *  if(value==START){
	 *    jcx.jform.tools.timer.init(this);
	 *  } else {
	 *
	 *  } 
	 * </pre>
	 */
	public static void init(bdisplay bd){
		JLabel jl=bd.getLabel();
		new timer(jl);
	}

	/**
     * 保留 .
	 */

	public timer(JLabel label){
		jl=label;
		me=new Thread(this);
		me.start();
	}
	/**
     * 保留 .
	 */

	public void run(){
		while(Thread.currentThread()==me){
			try{
				jl.setText(datetime.getTime("h:m:s"));
				Thread.sleep(1000);
			} catch(Exception e){}
		}
	}
}
