package com.lps.constants;

public class LpsConstants {

	public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
	
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	
	public static final String APPLICATION_PROFILE_KEY = "app_profile";
	
	public static final String LINE_SEPARATOR;
	
	static {
		LINE_SEPARATOR  = System.getProperty("line.separator");
	}
}
