package com.qcms.cms.result;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.freemarker.FreemarkerResult;

import com.opensymphony.xwork2.ActionInvocation;

import freemarker.template.TemplateException;

public class QFreemarkerResult extends FreemarkerResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6227172313432062226L;
	public String templatePath;

	@Override
	public void doExecute(String locationArg, ActionInvocation invocation)
			throws IOException, TemplateException {
		if (StringUtils.isNotBlank(templatePath)) {
			if (templatePath.endsWith("/")) {
				if (!location.startsWith("/"))
					location = templatePath + location;
				else
					location = templatePath.substring(0,
							templatePath.length() - 1) + location;
			} else {
				if (location.startsWith("/"))
					location = templatePath + location;
				else
					location = templatePath + "/" + location;
			}
		}
		locationArg=location;
		super.doExecute(locationArg, invocation);
	}
}
