package com.qcms.cms.manager.main;

import com.qcms.cms.entity.main.Content;
import com.qcms.cms.entity.main.ContentExt;

public interface ContentExtMng {
	public ContentExt save(ContentExt ext, Content content);

	public ContentExt update(ContentExt ext);
}