package com.schibsted.test.webapp.view;

import com.schibsted.test.webapp.core.controller.IViewLayer;
import com.schibsted.test.webapp.core.controller.ViewBean;
import com.schibsted.test.webapp.viewBean.UserInfoBean;

public class Page1 implements IViewLayer{

	private static final String pageName="PAGE 1";

	@Override
	public String renderView(ViewBean viewBean) {
		UserInfoBean userBean=(UserInfoBean)viewBean;		
		return HelperWebappContentPages.getPageXContent(userBean.getUsername(),pageName);
		
		
		
	}

	public Page1() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}
