package com.qcms.cms.dao.main;

import com.qcms.cms.entity.main.ContentTxt;
import com.qcms.common.hibernate3.Updater;

public interface ContentTxtDao {
	public ContentTxt findById(Integer id);

	public ContentTxt save(ContentTxt bean);

	public ContentTxt updateByUpdater(Updater<ContentTxt> updater);
}