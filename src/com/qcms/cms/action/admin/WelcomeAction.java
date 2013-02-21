package com.qcms.cms.action.admin;

import java.util.List;
import java.util.Properties;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.qcms.cms.entity.main.CmsSite;
import com.qcms.cms.entity.main.CmsUser;
import com.qcms.cms.manager.main.CmsSiteMng;
import com.qcms.cms.statistic.CmsStatisticSvc;
import com.qcms.cms.web.AdminContextInterceptor;
import com.qcms.cms.web.CmsUtils;

@ParentPackage("admin")
@Namespace("/admin/main")
// @Result(name =
// "top",type="string",location="/WEB-INF/qcms_sys/${location}")
// @Result(name = "input", type = "redirectAction", params = { "actionName",
// "input", "namespace",
// "/admin/main","a","${return_action}","n","${return_action_namespace}"})
@Results({
		@Result(name = "input", type = "redirectAction", params = {
				"actionName", "input" }),
		@Result(name = "index",type="freemarker",location="/cms_sys/index.html") ,
		@Result(name = "top",type="freemarker",location="/cms_sys/top.html"),
		@Result(name = "main",type="freemarker",location="/cms_sys/main.html"),
		@Result(name = "left",type="freemarker",location="/cms_sys/left.html"),
		@Result(name = "right",type="freemarker",location="/cms_sys/right.html")})
public class WelcomeAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -994227633810367340L;

	// @RequestMapping("/index.do")
	@Action("index")
	public String index() {
		// String returnAction = ActionContext.getContext().getName();
		// String returnActionNamespace =
		// ServletActionContext.getActionMapping()
		// .getNamespace();
		// setValue(RETURN_ACTION, returnAction);
		// setValue(RETURN_ACTION_NAMESPACE, returnActionNamespace);
		return "index";
	}

	// @RequestMapping("/map.do")
	public String map() {
		return "map";
	}

	// @RequestMapping("/top.do")
	@Action("top")
	public String top() {
		// 需要获得站点列表
		List<CmsSite> siteList = cmsSiteMng.getList();
		CmsSite site = CmsUtils.getSite(httpReq);
		CmsUser user = CmsUtils.getUser(httpReq);
		setValue("siteList", siteList);
		setValue("site", site);
		setValue("siteParam", AdminContextInterceptor.SITE_PARAM);
		setValue("user", user);
		//setResult("top");
		setValue("location", "top.html");
		// result.s
		return "top";
	}

	// @RequestMapping("/main.do")
	@Action("main")
	public String main() {
		//setResult("main");
		return "main";
	}

	// @RequestMapping("/left.do")
	@Action("left")
	public String left() {
		return "left";
	}

	// @RequestMapping("/right.do")
	@Action("right")
	public String right() {
		CmsSite site = CmsUtils.getSite(httpReq);
		CmsUser user = CmsUtils.getUser(httpReq);
		String version = site.getConfig().getVersion();
		Properties props = System.getProperties();
		Runtime runtime = Runtime.getRuntime();
		long freeMemoery = runtime.freeMemory();
		long totalMemory = runtime.totalMemory();
		long usedMemory = totalMemory - freeMemoery;
		long maxMemory = runtime.maxMemory();
		long useableMemory = maxMemory - totalMemory + freeMemoery;
		setValue("props", props);
		setValue("freeMemoery", freeMemoery);
		setValue("totalMemory", totalMemory);
		setValue("usedMemory", usedMemory);
		setValue("maxMemory", maxMemory);
		setValue("useableMemory", useableMemory);
		setValue("version", version);
		setValue("user", user);
		setValue("flowMap",
				cmsStatisticSvc.getWelcomeSiteFlowData(site.getId()));
		return "right";
	}

	@Autowired
	private CmsSiteMng cmsSiteMng;
	@Autowired
	private CmsStatisticSvc cmsStatisticSvc;
}
