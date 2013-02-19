package com.qcms.cms.dao.main;

import com.qcms.cms.entity.main.CmsConfig;
import com.qcms.common.hibernate3.Updater;

public interface CmsConfigDao {
	public CmsConfig findById(Integer id);

	public CmsConfig updateByUpdater(Updater<CmsConfig> updater);
}