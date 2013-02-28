package com.qcms.cms.action.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements SessionAware,
		RequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5167862439043896993L;
	protected Map<String, Object> request;
	protected Map<String, Object> session;
	protected HttpServletRequest httpReq = ServletActionContext.getRequest();
	protected HttpServletResponse httpResp = ServletActionContext.getResponse();
	protected final static String FREEMARKER_TYPE_CODE = "freemarker_type_code";
	public final static String RETURN_ACTION = "return_action";
	public final static String RETURN_ACTION_NAMESPACE = "return_action_namespace";

	@Override
	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;

	}

	public void setValue(String key, Object value) {
		ActionContext.getContext().getValueStack().set(key, value);
	}

	public void setResult(String resultCode) {
		setValue(FREEMARKER_TYPE_CODE, resultCode);
	}
}
