package com.qcms.cms.dao.main;

import com.qcms.cms.entity.main.CmsUserExt;
import com.qcms.common.hibernate3.Updater;

public interface CmsUserExtDao {
	public CmsUserExt findById(Integer id);

	public CmsUserExt save(CmsUserExt bean);

	public CmsUserExt updateByUpdater(Updater<CmsUserExt> updater);
}