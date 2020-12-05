/*
 * @(#)Servlet.java	1.00 2000/08/01
 *
 * Copyright 2000-20010 by Internet Information Corp.,
 * All rights reserved.
 * 
 */
package jcx.servlet;

import netscape.server.applet.*;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.util.*;
import jcx.util.upload;
import javax.servlet.http.*;

/**
 * 此類別 <code>Servlet</code> 為一Servlet 之基礎類別,
 * 凡是所有Servlet 程式均須要為繼承此類別另做發展。
 */

public abstract class Servlet extends HttpApplet {
	Hashtable formdata=new Hashtable();
	boolean file_upload=false;
	String filename="";
	Hashtable files=new Hashtable();
	Hashtable file_ins=new Hashtable();

    /**
     * 傳回 Client 端呼叫此 Servlet 全部的參數.
     *
     * @return  Hashtable represented the Parameter.
     */
	public Hashtable getFormData(){
		return formdata;
	}

	
    /**
     * 傳回 Client 端呼叫此 Servlet 的單一參數.
     *
     * @param   key   參數名稱,如果參數不存在則傳回 null.
     * @return  參數的 value
     */
	public String getParameter(String key){
		return (String)formdata.get(key);
	}

	
    /**
     * 傳回 Client 端檔案上傳資料的 FileInputStream.
     *	
	 * <PRE>所謂檔案上傳是以 multipart/form-data 格式上傳的方式,
	 * Exam.
	 *	<form ENCTYPE='multipart/form-data' action='/server-java/[servlet]' method=post>
	 *	<center>
	 *	<h2 align=center>檔案上傳</h2>
	 *	<h4 align=center>使用者代號: Jony</h4>
	 *	<INPUT TYPE='hidden' NAME='id' VALUE='Jony'>
	 *	<TABLE border align=center>
	 *	<TD> 檔案名稱: </TD>
	 *	<TD COLSPAN=3><INPUT TYPE="file" NAME="uploadfile" size=50 ></TD></TR>
	 *	</TABLE>
	 *	<INPUT TYPE='submit' VALUE='上傳'></center>
	 *
	 *  程式寫法
	 *  String user_id=getParameter("id");<BR>
	 *  FileInputStream fin=getFileInputStream("uploadfile");
	 *	</PRE>
     * @param   key   參數名稱,如果參數不存在則傳回 null.
     * @exception  IOException  if an I/O error occurs.
     * @return  上傳資料的 FileInputStream.
	 *
	 */
	public final FileInputStream getFileInputStream(String key) throws IOException {

		ByteArrayOutputStream n1=(ByteArrayOutputStream)files.get(key+".[]");
		if(n1==null) return null;
		n1.flush();

		String filename=(String)files.get(key);
		FileOutputStream fout=new FileOutputStream(filename);
		fout.write(n1.toByteArray());
		fout.close();

		if(filename==null) return null;
		FileInputStream fin=new FileInputStream(filename);
		file_ins.put(key,fin);
		return fin;
	}

	public final InputStream getMemoryInputStream(String key) throws IOException {
		ByteArrayOutputStream n1=(ByteArrayOutputStream)files.get(key+".[]");
		if(n1==null) return null;
		n1.flush();
		ByteArrayInputStream ba=new ByteArrayInputStream(n1.toByteArray());
		return ba;
	}

    private final boolean upload_process() throws Exception {
		 Hashtable h1=new Hashtable();
		 boolean ret=upload.SaveBinaryFile(this,h1,files);
		 if(!ret) {   
			 return false;
		 } else {
			 formdata=h1;
			 return true;
		 }
	}
    private final void clean_up() throws Exception {
		for (Enumeration e = file_ins.keys() ; e.hasMoreElements() ;) {
			String key1=e.nextElement().toString();
			try{
				((FileInputStream)file_ins.get(key1)).close();
			} catch(Exception e1){}
		}
		
		for (Enumeration e = files.keys() ; e.hasMoreElements() ;) {
			String key1=e.nextElement().toString();
			try{
				File f1=new File((String)files.get(key1));
				f1.delete();
			} catch(Exception e1){}
		}
	}

	/**
     * Servlet 保留用途，程式由 run1 開始執行，不允許 override
     *	
	 *
	 */
    public final void run() throws Exception {

     try {
        formdata=super.getFormData();
     }catch (Exception e) {
        if(e.toString().trim().equals("java.io.IOException: illegal content type")){
		  file_upload=true;
	      if(!upload_process()){
			getOutputStream().println("<html><h3>Upload fail ,please call service</h3>");
			return;
		  }
		}
     }

	  run1();
	  if(file_upload){
		clean_up();
	  }
	}
    /**
     * Servlet 程式的進入點，程式由 run1 開始執行
     *	
     * @exception  Exception  if any Exception.
     * @return  none
	 *
	 */
    abstract public void run1() throws Exception;
	
	/**
	*
	* 得到 Browser 端資料的 InputStream.
	*
    * @return  Browser 端資料的 InputStream
	*/
    public InputStream getInputStream() {
	  return super.getInputStream();
    }

	/**
	*
	* 得到 對 Browser 端輸出資料的 PrintStream.
	*
    * @return  Browser 端資料的 PrintStream
	*/
    public PrintStream getOutputStream() {
	  return super.getOutputStream();
    }

    public void setHeader(String name, String value){
      return ;
    }

    public String getHeader(String name){
	  return "";
    }

	/**
	* 得到 Browser 端的 Cookie.
	*
    * @return  Cookie 陣列,如果 Client 端沒有送出 Cookie ,則回傳零長度的陣列
	*/
    public Cookie[] getCookies(){
	  return super.getCookies();
	}

    /**
     * 對 Client 端送出 Cookie,必須在 returnNormalResponse 前執行，否則無效.
     *
     * @param   cookie   欲送出給 Client 端的 Cookie.
     * @return  none
	 */
	public void addCookie(Cookie cookie){
	  super.addCookie(cookie);
      return ;
    }

    /**
     * 對 Client 端送出 HTTP 200 O.K 的訊號，並設定 content-type 為 s1.
     *
     * @param   String   欲送出給 Client 端的 content-type ,如 text/html , image/gif 等等.
     * @return  永遠為 true.
	 */

	public boolean returnNormalResponse(String s1){
		return super.returnNormalResponse(s1);
	}

    /**
     * 對 Client 端送出 HTTP/1.0 401 Unauthorized 的訊號，並設定 realm.
     *
     * @param   String   欲送出給 Client 端的 realm.
     * @return  永遠為 true
	 */
    public boolean returnAuthenticateResponse(String realm){
		return super.returnAuthenticateResponse(realm);
	}

    /**
     * 對 Client 端送出 HTTP/1.0 401 Unauthorized 的訊號，並設定 realm 為 Java Composer.
     *
     * @param   none.
     * @return  永遠為 true
	 */
    public boolean returnAuthenticateResponse(){
		return super.returnAuthenticateResponse();
	}
    /**
     * 對 Client 端送出 HTTP/1.1 400 unknown reason 的訊號，並設定 content-type 為 s1.
     *
     * @param   String   欲送出給 Client 端的 content-type ,如 text/html , image/gif 等等.
     * @param   int      保留，未使用，請傳入零即可.
     * @return  永遠為 true.
	 */
    public boolean returnErrorResponse(String s1,int ii){
		return super.returnErrorResponse(s1,ii);
	}


    /**
     * 傳回本程式的路徑(含程式名稱) 如 /server-java/Hello , /servlet/HelloWorld.
     *
     * @return  傳回本程式的路徑(含程式名稱),不含參數.
	 */
    public String getPath(){
	 return super.getPath();
	}

    /**
     * 傳回呼叫本程式的 Method 如 GET,POST.
     *
     * @return  呼叫本程式的 Method 如 GET,POST.
	 */
    public String getMethod(){
	 return super.getMethod();
	}

    /**
     * 取得 Client 端的屬性 .
     *
     * @param   prop   傳入 ip.
     * @return  Client 端的 IP Address .
	 */
	public String getClientProperty(String prop){
		return super.getClientProperty(prop);
	}


}

