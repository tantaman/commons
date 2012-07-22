package com.tantaman.commons.util;

public class Base64Utils {
	public static String trim(String b64) {
		if (b64.charAt(b64.length() - 1) == '=' && b64.charAt(b64.length() - 2) == '=') {
			String trimmed = b64.substring(0, b64.length() - 2);
			return trimmed;
		}
		
		return b64;
	}
}
