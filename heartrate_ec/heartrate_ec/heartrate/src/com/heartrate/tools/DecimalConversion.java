package com.heartrate.tools;
/**
 * Author: xiaojun
 * Email: 786206257@qq.com
 * 
 */
//进制转换类
public class DecimalConversion {
	
	//将指定byte数组以16进制的形式打印到控制台
	public static void printHexString( byte[] b) {  
	   for (int i = 0; i < b.length; i++) { 
	     String hex = Integer.toHexString(b[i] & 0xFF); 
	     if (hex.length() == 1) { 
	       hex = '0' + hex; 
	     } 
	     System.out.println(hex.toUpperCase()); 
	   } 

	}
	//将byte数组转16进制
	public static String byteToString( byte[] b) {  
		String str = new String();
		   for (int i = 0; i < b.length; i++) { 
		     String hex = Integer.toHexString(b[i] & 0xFF); 
		     if (hex.length() == 1) { 
		       hex = '0' + hex; 
		     } 
		     str = hex .toUpperCase() + str;
		   }
		     return str; 
		}		
	public static String byteToHex( byte[] b,int i) {  
		 String hex = Integer.toHexString(b[i-1] & 0xFF); 
	     if (hex.length() == 1) { 
	       hex = '0' + hex; 
	     } 
		 return hex; 
		}	
	
/*
	// 二进制转十六进制
	public String BToH(String a) {
		// 将二进制转为十进制再从十进制转为十六进制
		String b = Integer.toHexString(Integer.valueOf(toD(a, 2)));
		return b;
	}

	// 任意进制数转为十进制数
	public String toD(String a, int b) {
		int r = 0;
		for (int i = 0; i < a.length(); i++) {
		r = (int) (r + formatting(a.substring(i, i + 1))
		* Math.pow(b, a.length() - i - 1));
		}
		return String.valueOf(r);
	}
	// 将十六进制中的字母转为对应的数字
	public int formatting(String a) {
		int i = 0;
		for (int u = 0; u < 10; u++) {
			if (a.equals(String.valueOf(u))) {
				i = u;
			}
		}
		if (a.equals("a")) {
			i = 10;
		}
		if (a.equals("b")) {
			i = 11;
		}
		if (a.equals("c")) {
			i = 12;
		}
		if (a.equals("d")) {
			i = 13;
		}
		if (a.equals("e")) {
			i = 14;
		}
		if (a.equals("f")) {
			i = 15;
		}
		return i;
	}
	
	  
	*//** *//**  
	    * 把字节数组转换成16进制字符串  
	    * @param bArray  
	    * @return  
	    *//*   
	public String bytesToHexString(byte[] bArray) {   
	    StringBuffer sb = new StringBuffer(bArray.length);   
	    String sTemp;   
	    for (int i = 0; i < bArray.length; i++) {   
	     sTemp = Integer.toHexString(0xFF & bArray[i]);   
	     if (sTemp.length() < 2)   
	      sb.append(0);   
	     sb.append(sTemp.toUpperCase());   
	    }   
	    return sb.toString();   
	}	
	
	*/
}











