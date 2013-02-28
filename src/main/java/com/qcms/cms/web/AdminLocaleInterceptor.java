package com.qcms.cms.web;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.qcms.cms.entity.main.CmsSite;

/**
 * 后台（管理员）本地化信息拦截器
 * 
 * @author qzz
 * 
 */
public class AdminLocaleInterceptor implements Interceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1741593140690679631L;
	/**
	 * 本地化字符串在ModelMap中的名称
	 */
	public static final String LOCALE = "locale";
	HttpServletRequest request;
	HttpServletResponse response;

	public boolean preHandle() throws ServletException {
		LocaleResolver localeResolver = RequestContextUtils
				.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException(
					"No LocaleResolver found: not in a DispatcherServlet request?");
		}
		CmsSite site = CmsUtils.getSite(request);
		String newLocale = site.getLocaleAdmin();
		LocaleEditor localeEditor = new LocaleEditor();
		localeEditor.setAsText(newLocale);
		localeResolver.setLocale(request, response,
				(Locale) localeEditor.getValue());
		// Proceed in any case.
		return true;
	}

	public void postHandle() throws Exception {
		LocaleResolver localeResolver = RequestContextUtils
				.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException(
					"No LocaleResolver found: not in a DispatcherServlet request?");
		}
		/*
		 * if (modelAndView != null) {
		 * modelAndView.getModelMap().addAttribute(LOCALE,
		 * localeResolver.resolveLocale(request).toString()); }
		 */
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		LocaleResolver localeResolver = RequestContextUtils
				.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException(
					"No LocaleResolver found: not in a DispatcherServlet request?");
		}
		CmsSite site = CmsUtils.getSite(request);
		String newLocale = site.getLocaleAdmin();
		LocaleEditor localeEditor = new LocaleEditor();
		localeEditor.setAsText(newLocale);
		localeResolver.setLocale(request, response,
				(Locale) localeEditor.getValue());
		String result = arg0.invoke();
		return result;
	}
}