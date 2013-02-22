package com.qcms.cms.action.admin;

import static com.qcms.core.action.front.LoginAct.PROCESS_URL;
import static com.qcms.core.action.front.LoginAct.RETURN_URL;
import static com.qcms.core.manager.AuthenticationMng.AUTH_KEY;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.qcms.cms.entity.main.CmsUser;
import com.qcms.cms.manager.main.CmsUserMng;
import com.qcms.common.security.DisabledException;
import com.qcms.common.web.CookieUtils;
import com.qcms.common.web.RequestUtils;
import com.qcms.common.web.session.SessionProvider;
import com.qcms.core.entity.Authentication;
import com.qcms.core.entity.Config.ConfigLogin;
import com.qcms.core.manager.AuthenticationMng;
import com.qcms.core.manager.ConfigMng;
import com.qcms.core.manager.UnifiedUserMng;
import com.qcms.core.web.WebErrors;

@ParentPackage("admin")
@Namespace("/admin/main")
@Results({
		//@Result(name = "loginui", location = "/cms_sys/${location}"),
		@Result(type="redirect",location = "${returnUrl}"),
		@Result(name = "input", type = "redirectAction", params = {
				"actionName", "input" }) })
public class CmsLoginAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5817531859901185395L;

	protected Logger log = LoggerFactory.getLogger(getClass());
	private String username;
	private String password;
	private String captcha;
	private String processUrl;
	private String returnUrl = "/admin/main/index.action";
	public static final String COOKIE_ERROR_REMAINING = "_error_remaining";

	// @RequestMapping(value = "/login.do", method = RequestMethod.GET)
	/*@Action(value = "input", results = { @Result(type = "redirectAction", params = {
			"returnUrl", "${returnUrl}" }) }, interceptorRefs = {
			@InterceptorRef("defaultStack"),@InterceptorRef("store") })
			*/
	public String input() {
		return "loginui";
	}

	// @RequestMapping(value = "/login.do", method = RequestMethod.POST)
	@Action(value = "login")
	public String submit() {

		if (!hasErrors()) {
			try {
				String ip = RequestUtils.getIpAddr(httpReq);
				Authentication auth = authMng.login(username, password, ip,
						httpReq, httpResp, sessionProvider);
				// 是否需要在这里加上登录次数的更新？按正常的方式，应该在process里面处理的，不过这里处理也没大问题。
				cmsUserMng.updateLoginInfo(auth.getUid(), ip);
				CmsUser user = cmsUserMng.findById(auth.getUid());
				if (user.getDisabled()) {
					// 如果已经禁用，则退出登录。
					authMng.deleteById(auth.getId());
					sessionProvider.logout(httpReq, httpResp);
					throw new DisabledException("user disabled");
				}
				removeCookieErrorRemaining(httpReq, httpResp);
				// String view = getView(processUrl, returnUrl, auth.getId());
				// cmsLogMng.loginSuccess(httpReq, user,
				// "login.log.loginSuccess");
				session.put("user", user);
				return SUCCESS;
				// if (view != null) {
				// return view;
				// } else {
				// return "redirect:login.jspx";
				// }
				// } catch (UsernameNotFoundException e) {
				// errors.addErrorString(e.getMessage());
				// cmsLogMng.loginFailure(httpReq, "login.log.loginFailure",
				// "username=" + username + ";password=" + password);
				// addActionError("login.log.loginFailure");
				// } catch (BadCredentialsException e) {
				// errors.addErrorString(e.getMessage());
				// cmsLogMng.loginFailure(httpReq, "login.log.loginFailure",
				// "username=" + username + ";password=" + password);
				// addActionError("login.log.loginFailure");
				// } catch (DisabledException e) {
				// errors.addErrorString(e.getMessage());
				// cmsLogMng.loginFailure(httpReq, "login.log.loginFailure",
				// "username=" + username + ";password=" + password);
			} catch (Exception e) {
				addActionError("login.log.loginFailure" + e.getMessage());
				// }
			}
		}
		// 登录失败
		writeCookieErrorRemaining(httpReq, httpResp);
		// errors.toModel(model);
		if (!StringUtils.isBlank(processUrl)) {
			// model.addAttribute(PROCESS_URL, processUrl);
		}
		if (!StringUtils.isBlank(returnUrl)) {
			// model.addAttribute(RETURN_URL, returnUrl);
		}
		// if (!StringUtils.isBlank(message)) {
		// model.addAttribute(MESSAGE, message);
		// }

		return "input";
	}

	// @RequestMapping(value = "/logout.do")
	public String logout(HttpServletRequest request,
			HttpServletResponse response) {
		String authId = (String) sessionProvider
				.getAttribute(request, AUTH_KEY);
		if (authId != null) {
			authMng.deleteById(authId);
			sessionProvider.logout(request, response);
		}
		String processUrl = RequestUtils.getQueryParam(request, PROCESS_URL);
		String returnUrl = RequestUtils.getQueryParam(request, RETURN_URL);
		String view = getView(processUrl, returnUrl, authId);
		if (view != null) {
			return view;
		} else {
			return "redirect:login.jspx";
		}
	}

	public void validateSubmit() {
		Integer errorRemaining = unifiedUserMng.errorRemaining(username);
		WebErrors errors = new WebErrors(this);
		errors.ifOutOfLength(username, "username", 1, 100);
		errors.ifOutOfLength(password, "password", 1, 32);
		// 如果输入了验证码，那么必须验证；如果没有输入验证码，则根据当前用户判断是否需要验证码。
		if (!StringUtils.isBlank(captcha)
				|| (errorRemaining != null && errorRemaining < 0)) {
			errors.ifBlank(captcha, "captcha", 100);
			try {
				if (!imageCaptchaService.validateResponseForID(
						sessionProvider.getSessionId(httpReq, httpResp),
						captcha)) {
					errors.addErrorCode("error.invalidCaptcha");
				}
			} catch (CaptchaServiceException e) {
				errors.addErrorCode("error.exceptionCaptcha");
				log.warn("", e);
			}
		}
	}

	/**
	 * 获得地址
	 * 
	 * @param processUrl
	 * @param returnUrl
	 * @param authId
	 * @param defaultUrl
	 * @return
	 */
	private String getView(String processUrl, String returnUrl, String authId) {
		if (!StringUtils.isBlank(processUrl)) {
			StringBuilder sb = new StringBuilder("redirect:");
			sb.append(processUrl).append("?").append(AUTH_KEY).append("=")
					.append(authId);
			if (!StringUtils.isBlank(returnUrl)) {
				sb.append("&").append(RETURN_URL).append("=").append(returnUrl);
			}
			return sb.toString();
		} else if (!StringUtils.isBlank(returnUrl)) {
			StringBuilder sb = new StringBuilder("redirect:");
			sb.append(returnUrl);
			return sb.toString();
		} else {
			return null;
		}
	}

	private void writeCookieErrorRemaining(HttpServletRequest request,
			HttpServletResponse response) {
		// 所有访问的页面都需要写一个cookie，这样可以判断已经登录了几次。
		Integer userErrorRemaining = unifiedUserMng.errorRemaining(username);
		Integer errorRemaining = getCookieErrorRemaining(request, response);
		ConfigLogin configLogin = configMng.getConfigLogin();
		Integer errorInterval = configLogin.getErrorInterval();
		if (userErrorRemaining != null
				&& (errorRemaining == null || userErrorRemaining < errorRemaining)) {
			errorRemaining = userErrorRemaining;
		}
		int maxErrorTimes = configLogin.getErrorTimes();
		if (errorRemaining == null || errorRemaining > maxErrorTimes) {
			errorRemaining = maxErrorTimes;
		} else if (errorRemaining <= 0) {
			errorRemaining = 0;
		} else {
			errorRemaining--;
		}
		// model.addAttribute("errorRemaining", errorRemaining);
		CookieUtils.addCookie(request, response, COOKIE_ERROR_REMAINING,
				errorRemaining.toString(), errorInterval * 60, null);
	}

	private void removeCookieErrorRemaining(HttpServletRequest request,
			HttpServletResponse response) {
		CookieUtils.cancleCookie(request, response, COOKIE_ERROR_REMAINING,
				null);
	}

	private Integer getCookieErrorRemaining(HttpServletRequest request,
			HttpServletResponse response) {
		Cookie cookie = CookieUtils.getCookie(request, COOKIE_ERROR_REMAINING);
		if (cookie != null) {
			String value = cookie.getValue();
			if (NumberUtils.isDigits(value)) {
				return Integer.parseInt(value);
			}
		}
		return null;
	}

	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private ConfigMng configMng;
	@Autowired
	private AuthenticationMng authMng;
	@Autowired
	private SessionProvider sessionProvider;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	//@Autowired
	//private CmsLogMng cmsLogMng;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getProcessUrl() {
		return processUrl;
	}

	public void setProcessUrl(String processUrl) {
		this.processUrl = processUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
}
