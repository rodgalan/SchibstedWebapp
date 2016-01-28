package com.schibsted.test.webapp.view;

import com.schibsted.test.webapp.core.controller.IViewLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.viewBean.UserInfoBean;

public class Page3 implements IViewLayer{

	private static final String pageName="PAGE 3";

	@Override
	public String renderView(ViewBean viewBean) {
		UserInfoBean userBean=(UserInfoBean)viewBean;
		userBean.setForwardName("success");
		System.out.println("SOY PAGINA 3. USER: "+userBean);		
		return HelperWebappContentPages.getPageXContent(userBean.getUsername(),pageName);
	}

	public Page3() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
