package com.qcms.cms.dao.main;

import com.qcms.cms.entity.main.ContentExt;
import com.qcms.common.hibernate3.Updater;

public interface ContentExtDao {
	public ContentExt findById(Integer id);

	public ContentExt save(ContentExt bean);

	public ContentExt updateByUpdater(Updater<ContentExt> updater);
}