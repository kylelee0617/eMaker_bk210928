package Farglory.util ;
import  jcx.util.*;
import  java.io.* ;
import java.util.* ;
public  class  FargloryFileFilter  extends  javax.swing.filechooser.FileFilter {
		String   stringFileExtension  =  ",xls," ; 		
		Vector  vectorFileExtension  =  new  Vector() ;
		public FargloryFileFilter( ){
				vectorFileExtension.add("xls") ; // 預設判斷檔名中是否有副檔名 xls
		}
		public FargloryFileFilter(String  stringFileFilters ){
				String   stringFileExtensionL  =  "" ;
				String[]  arrayFileFilter  			=  convert.StringToken(stringFileFilters,  ",") ;
				for(int  intNo=0  ;  intNo<arrayFileFilter.length  ;  intNo++) {
						stringFileExtensionL  =  arrayFileFilter[intNo].trim().toLowerCase() ;  if("".equals(stringFileExtensionL))  continue ;
						vectorFileExtension.add(stringFileExtensionL) ;
				}
				if(vectorFileExtension.size()  ==  0) {
						vectorFileExtension.add("xls") ; // 預設判斷檔名中是否有副檔名 xls
				}
		}
		public  void  addFileExtension(String   stringFileExtension) {
				vectorFileExtension.add(stringFileExtension.toLowerCase()) ;
		}
		public  Vector  getFileExtension( ) {
				return vectorFileExtension   ;
		}
		public  void  setFileExtension(Vector   vectorFileExtensionL) {
				vectorFileExtension  =  vectorFileExtensionL ;
		}
		public  boolean  accept(File  obj) {
				String  stringFile  =  ""  ;
				if(obj.getPath(  ).lastIndexOf('.')  >  0) {
						stringFile  =  obj.getPath( ).substring(obj.getPath( ).lastIndexOf('.')+1).toLowerCase( ) ;
				}
				if(stringFile  !=  "") {
						return  vectorFileExtension.indexOf(stringFile)!=-1  ;
				} else {
						return  obj.isDirectory( )  ;
				}
		
		}
		// 顯示在檔案對話方塊的檔案類型描述字串
		public  String  getDescription( ) {
				String  stringFileExtensionL  =  "" ;
				String  stringTemp				  =  "" ;
				for(int  intNo=0  ;  intNo<vectorFileExtension.size()  ;  intNo++) {
						stringTemp  =  ""+vectorFileExtension.get(intNo) ;  
						//
						if("null".equals(stringTemp))  continue ;
						if("".equals(stringTemp)) 	 	continue ;
						//
						if(!"".equals(stringFileExtensionL))  stringFileExtensionL  +=  "," ;
						stringFileExtensionL  +=  "*."+stringTemp ;
				}
				return  "限制檔案類型 ("+stringFileExtensionL+") " ;
		}
}
