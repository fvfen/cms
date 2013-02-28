package com.qcms.cms.dao.main;

import com.qcms.cms.entity.main.ContentCheck;
import com.qcms.common.hibernate3.Updater;

public interface ContentCheckDao {
	public ContentCheck findById(Long id);

	public ContentCheck save(ContentCheck bean);

	public ContentCheck updateByUpdater(Updater<ContentCheck> updater);
}