package com.schibsted.test.webapp.view;

public class HelperWebappContentPages {
	private static final String htmlContent="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>#page#</title></head><body><div id=\"TitleDiv\" align=\"center\"><h1>#page#<h1></div><div id=\"UserInfoDiv\"><h2>USERNAME: #username#</h2></div><br><div id=\"links\"><a href=\"page1\">page1 -- </a><a href=\"page2\">page2 -- </a><a href=\"page3\">page3 -- </a><a href=\"closeUserSession\">close session</a></div>	</body></html>";			
			
	private static final String usernameTag="#username#"; 
	private static final String pageTag="#page#"; 
	
	public static String getPageXContent(String username, String pageName){
		return htmlContent.replaceAll(usernameTag, username).replaceAll(pageTag,pageName);
	}

}
