package com.qcms.cms.action.admin.main;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qcms.cms.action.admin.BaseAction;
import com.qcms.cms.entity.main.Channel;
import com.qcms.cms.entity.main.ChannelExt;
import com.qcms.cms.entity.main.ChannelTxt;
import com.qcms.cms.entity.main.CmsGroup;
import com.qcms.cms.entity.main.CmsModel;
import com.qcms.cms.entity.main.CmsModelItem;
import com.qcms.cms.entity.main.CmsSite;
import com.qcms.cms.entity.main.CmsUser;
import com.qcms.cms.manager.main.ChannelMng;
import com.qcms.cms.manager.main.CmsGroupMng;
import com.qcms.cms.manager.main.CmsLogMng;
import com.qcms.cms.manager.main.CmsModelItemMng;
import com.qcms.cms.manager.main.CmsModelMng;
import com.qcms.cms.manager.main.CmsUserMng;
import com.qcms.cms.web.CmsUtils;
import com.qcms.cms.web.WebErrors;
import com.qcms.common.web.RequestUtils;
import com.qcms.core.tpl.TplManager;
import com.qcms.core.web.CoreUtils;

@ParentPackage("admin")
@Namespace("/admin/main/channel")
public class ChannelAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8059311558463455891L;
	private static final Logger log = LoggerFactory
			.getLogger(ChannelAction.class);
	private Integer root;
	private Integer modelId;

	@Action(value = "v_left", results = { @Result(name = "left", type = "freemarker", location = "/cms_sys/channel/left.html") })
	public String left() {
		return "left";
	}

	@Action(value = "v_tree", results = { @Result(name = "tree", type = "freemarker", location = "/cms_sys/channel/tree.html") })
	public String tree() {
		String root = httpReq.getParameter("root");
		if (root == null) {
			root = "source";
		}
		log.debug("tree path={}", root);
		boolean isRoot;
		// jquery treeview的根请求为root=source
		if (StringUtils.isBlank(root) || "source".equals(root)) {
			isRoot = true;
		} else {
			isRoot = false;
		}
		setValue("isRoot", isRoot);
		// WebErrors errors = validateTree(root, httpReq);
		// if (errors.hasErrors()) {
		// log.error(errors.getErrors().get(0));
		// ResponseUtils.renderJson(httpResp, "[]");
		// return null;
		// }
		List<Channel> list;
		if (isRoot) {
			CmsSite site = CmsUtils.getSite(httpReq);
			list = manager.getTopList(site.getId(), false);
		} else {
			Integer rootId = Integer.valueOf(root);
			list = manager.getChildList(rootId, false);
		}
		setValue("list", list);
		httpResp.setHeader("Cache-Control", "no-cache");
		httpResp.setContentType("text/json;charset=UTF-8");
		return "tree";
	}

	@Action(value = "v_list", results = { @Result(name = "list", type = "freemarker", location = "/cms_sys/channel/list.html") })
	public String list() {
		List<Channel> list;
		Integer root = null;
		String r = httpReq.getParameter("root");
		if (r != null) {
			root = Integer.valueOf(r);
		}
		if (root == null) {
			list = manager.getTopList(CmsUtils.getSiteId(httpReq), false);
		} else {
			list = manager.getChildList(root, false);
		}
		setValue("modelList", cmsModelMng.getList(false));
		setValue("root", root);
		setValue("list", list);
		return "list";
	}

	@Action(value = "v_add", results = { @Result(name = "add", type = "freemarker", location = "/cms_sys/channel/add.html") })
	public String add() {
		CmsSite site = CmsUtils.getSite(httpReq);
		Channel parent = null;
		if (root != null) {
			parent = manager.findById(root);
			setValue("parent", parent);
			setValue("root", root);
		}
		// 模型
		CmsModel m = cmsModelMng.findById(modelId);
		// 栏目模板列表
		List<String> channelTplList = getTplChannel(site, m, null);
		// 内容模板列表
		List<String> contentTplList = getTplContent(site, m, null);
		// 模型项列表
		List<CmsModelItem> itemList = cmsModelItemMng.getList(modelId, true,
				false);
		List<CmsGroup> groupList = cmsGroupMng.getList();
		// 浏览会员组列表
		List<CmsGroup> viewGroups = groupList;
		// 投稿会员组列表
		Collection<CmsGroup> contriGroups;
		if (parent != null) {
			contriGroups = parent.getContriGroups();
		} else {
			contriGroups = groupList;
		}
		// 管理员列表
		Collection<CmsUser> users;
		if (parent != null) {
			users = parent.getUsers();
		} else {
			users = cmsUserMng.getAdminList(site.getId(), false, false, null);
		}
		setValue("channelTplList", channelTplList);
		setValue("contentTplList", contentTplList);
		setValue("itemList", itemList);
		setValue("viewGroups", viewGroups);
		setValue("contriGroups", contriGroups);
		setValue("contriGroupIds", CmsGroup.fetchIds(contriGroups));
		setValue("users", users);
		setValue("userIds", CmsUser.fetchIds(users));
		setValue("model", m);
		return "add";
	}

	/**
	 * @return
	 */
	@Action(value = "v_edit", results = {
			@Result(name = "error", type = "string", location = "message"),
			@Result(name = "edit", type = "freemarker", location = "/cms_sys/channel/edit.html") })
	public String edit() {
		Integer id = null;
		String i = httpReq.getParameter("id");
		if (i != null) {
			id = Integer.parseInt(i);
		} else {
			setValue("message", "<script>alert('请求参数错误!请刷新重试')</script>");
			return ERROR;
		}
		String root = httpReq.getParameter("root");
		CmsSite site = CmsUtils.getSite(httpReq);
		WebErrors errors = validateEdit(id, httpReq);
		if (errors.hasErrors()) {
			// return errors.showErrorPage(model);
		}
		if (root != null) {
			setValue("root", root);
		}
		// 栏目
		Channel channel = manager.findById(id);
		// 当前模板，去除基本路径
		int tplPathLength = site.getTplPath().length();
		String tplChannel = channel.getTplChannel();
		if (!StringUtils.isBlank(tplChannel)) {
			tplChannel = tplChannel.substring(tplPathLength);
		}
		String tplContent = channel.getTplContent();
		if (!StringUtils.isBlank(tplContent)) {
			tplContent = tplContent.substring(tplPathLength);
		}
		// 父栏目
		Channel parent = channel.getParent();
		// 模型
		CmsModel m = channel.getModel();
		// 栏目列表
		List<Channel> topList = manager.getTopList(site.getId(), false);
		List<Channel> channelList = Channel.getListForSelect(topList, null,
				channel, false);

		// 栏目模板列表
		List<String> channelTplList = getTplChannel(site, m,
				channel.getTplChannel());
		// 内容模板列表
		List<String> contentTplList = getTplContent(site, m,
				channel.getTplContent());
		List<CmsGroup> groupList = cmsGroupMng.getList();
		// 模型项列表
		List<CmsModelItem> itemList = cmsModelItemMng.getList(m.getId(), true,
				false);
		// 浏览会员组列表、浏览会员组IDS
		List<CmsGroup> viewGroups = groupList;
		Integer[] viewGroupIds = CmsGroup.fetchIds(channel.getViewGroups());
		// 投稿会员组列表
		Collection<CmsGroup> contriGroups;
		if (parent != null) {
			contriGroups = parent.getContriGroups();
		} else {
			contriGroups = groupList;
		}
		// 投稿会员组IDS
		Integer[] contriGroupIds = CmsGroup.fetchIds(channel.getContriGroups());
		// 管理员列表
		Collection<CmsUser> users;
		if (parent != null) {
			users = parent.getUsers();
		} else {
			users = cmsUserMng.getAdminList(site.getId(), false, false, null);
		}
		// 管理员IDS
		Integer[] userIds = channel.getUserIds();
		setValue("channelList", channelList);
		setValue("modelList", cmsModelMng.getList(false));
		setValue("tplChannel", tplChannel);
		setValue("tplContent", tplContent);
		setValue("channelTplList", channelTplList);
		setValue("contentTplList", contentTplList);
		setValue("itemList", itemList);
		setValue("viewGroups", viewGroups);
		setValue("viewGroupIds", viewGroupIds);
		setValue("contriGroups", contriGroups);
		setValue("contriGroupIds", contriGroupIds);
		setValue("users", users);
		setValue("userIds", userIds);
		setValue("channel", channel);
		setValue("model", m);
		return "edit";
	}

	@Action("o_save")
	public String save(Integer root, Channel bean, ChannelExt ext,
			ChannelTxt txt, Integer[] viewGroupIds, Integer[] contriGroupIds,
			Integer[] userIds) {
		WebErrors errors = validateSave(bean, httpReq);
		if (errors.hasErrors()) {
			//return errors.showErrorPage(model);
		}
		CmsSite site = CmsUtils.getSite(httpReq);
		// 加上模板前缀
		String tplPath = site.getTplPath();
		if (!StringUtils.isBlank(ext.getTplChannel())) {
			ext.setTplChannel(tplPath + ext.getTplChannel());
		}
		if (!StringUtils.isBlank(ext.getTplContent())) {
			ext.setTplContent(tplPath + ext.getTplContent());
		}
		bean.setAttr(RequestUtils.getRequestMap(httpReq, "attr_"));
		bean = manager.save(bean, ext, txt, viewGroupIds, contriGroupIds,
				userIds, CmsUtils.getSiteId(httpReq), root, modelId);
		log.info("save Channel id={}, name={}", bean.getId(), bean.getName());
		cmsLogMng.operating(httpReq, "channel.log.save", "id=" + bean.getId()
				+ ";title=" + bean.getTitle());
		setValue("root", root);
		return "redirect:v_list.do";
	}

	@RequestMapping("/channel/o_update.do")
	public String update(Integer root, Channel bean, ChannelExt ext,
			ChannelTxt txt, Integer[] viewGroupIds, Integer[] contriGroupIds,
			Integer[] userIds, Integer parentId, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateUpdate(bean.getId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSite site = CmsUtils.getSite(request);
		// 加上模板前缀
		String tplPath = site.getTplPath();
		if (!StringUtils.isBlank(ext.getTplChannel())) {
			ext.setTplChannel(tplPath + ext.getTplChannel());
		}
		if (!StringUtils.isBlank(ext.getTplContent())) {
			ext.setTplContent(tplPath + ext.getTplContent());
		}
		Map<String, String> attr = RequestUtils.getRequestMap(request, "attr_");
		bean = manager.update(bean, ext, txt, viewGroupIds, contriGroupIds,
				userIds, parentId, attr);
		log.info("update Channel id={}.", bean.getId());
		cmsLogMng.operating(request, "channel.log.update", "id=" + bean.getId()
				+ ";name=" + bean.getName());
		return list();
	}

	@RequestMapping("/channel/o_delete.do")
	public String delete(Integer root, Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Channel[] beans = manager.deleteByIds(ids);
		for (Channel bean : beans) {
			log.info("delete Channel id={}", bean.getId());
			cmsLogMng.operating(request, "channel.log.delete",
					"id=" + bean.getId() + ";title=" + bean.getTitle());
		}
		return list();
	}

	@RequestMapping("/channel/o_priority.do")
	public String priority(Integer root, Integer[] wids, Integer[] priority,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validatePriority(wids, priority, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		manager.updatePriority(wids, priority);
		model.addAttribute("message", "global.success");
		return list();
	}

	private List<String> getTplChannel(CmsSite site, CmsModel model, String tpl) {
		String sol = site.getSolutionPath();
		List<String> tplList = tplManager.getNameListByPrefix(model
				.getTplChannel(sol, false));
		return CoreUtils.tplTrim(tplList, site.getTplPath(), tpl);
	}

	private List<String> getTplContent(CmsSite site, CmsModel model, String tpl) {
		String sol = site.getSolutionPath();
		List<String> tplList = tplManager.getNameListByPrefix(model
				.getTplContent(sol, false));
		return CoreUtils.tplTrim(tplList, site.getTplPath(), tpl);
	}

	@SuppressWarnings("unused")
	private WebErrors validateTree(String path, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifBlank(path, "path", 255)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateSave(Channel bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		bean.setSite(site);
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			if (vldExist(id, site.getId(), errors)) {
				return errors;
			}
			// 检查是否可以删除
			String code = manager.checkDelete(id);
			if (code != null) {
				errors.addErrorCode(code);
				return errors;
			}
		}
		return errors;
	}

	private boolean vldExist(Integer id, Integer siteId, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		Channel entity = manager.findById(id);
		if (errors.ifNotExist(entity, Channel.class, id)) {
			return true;
		}
		if (!entity.getSite().getId().equals(siteId)) {
			errors.notInSite(Channel.class, id);
			return true;
		}
		return false;
	}

	private WebErrors validatePriority(Integer[] wids, Integer[] priority,
			HttpServletRequest request) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = WebErrors.create(request);
		if (errors.ifEmpty(wids, "wids")) {
			return errors;
		}
		if (errors.ifEmpty(priority, "priority")) {
			return errors;
		}
		if (wids.length != priority.length) {
			errors.addErrorString("wids length not equals priority length");
			return errors;
		}
		for (int i = 0, len = wids.length; i < len; i++) {
			if (vldExist(wids[i], site.getId(), errors)) {
				return errors;
			}
			if (priority[i] == null) {
				priority[i] = 0;
			}
		}
		return errors;
	}

	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsModelMng cmsModelMng;
	@Autowired
	private CmsModelItemMng cmsModelItemMng;
	@Autowired
	private CmsGroupMng cmsGroupMng;
	@Autowired
	private TplManager tplManager;
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private ChannelMng manager;

	public Integer getRoot() {
		return root;
	}

	public void setRoot(Integer root) {
		this.root = root;
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}
}