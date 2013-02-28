package com.qcms.cms.result;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;

public class StringResult extends ServletRedirectResult {

	/**
	 * 可以直接使用struts2返回一个字符串,支持Ognl表达式.
	 */
	private static final long serialVersionUID = 6056681651788213195L;
	protected Logger _log = LoggerFactory.getLogger(getClass());
	private String defaultCharset = "utf-8";
	private String charset;

	public StringResult() {
		super();
	}

	public StringResult(String location) {
		super(location);
	}

	public void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {
		HttpServletResponse response = (HttpServletResponse) invocation
				.getInvocationContext().get(HTTP_RESPONSE);
		HttpServletRequest request = (HttpServletRequest) invocation
				.getInvocationContext().get(HTTP_REQUEST);
		if (charset == null) {
			charset = request.getCharacterEncoding();
		}
		if (charset == null || charset == "")
			charset = defaultCharset;
		response.setContentType("text/plain;charset=" + charset);
		response.setHeader("Content-Disposition", "inline");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			String s = "";
			Object o = invocation.getInvocationContext().getValueStack()
					.findValue(finalLocation, false);
			if (finalLocation != null) {
				s = o.toString();
			}
			writer.write(s);
		} catch (NullPointerException e) {
			if (finalLocation.equals("")) {
				_log.error("未指定value", e);
			} else {
				_log.error("空", e);
			}
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
