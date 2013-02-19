package com.qcms.cms.dao.assist;

import com.qcms.cms.entity.assist.CmsVoteItem;
import com.qcms.common.hibernate3.Updater;
import com.qcms.common.page.Pagination;

public interface CmsVoteItemDao {
	public Pagination getPage(int pageNo, int pageSize);

	public CmsVoteItem findById(Integer id);

	public CmsVoteItem save(CmsVoteItem bean);

	public CmsVoteItem updateByUpdater(Updater<CmsVoteItem> updater);

	public CmsVoteItem deleteById(Integer id);
}