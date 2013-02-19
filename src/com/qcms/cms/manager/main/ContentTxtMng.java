package com.qcms.cms.manager.main;

import com.qcms.cms.entity.main.Content;
import com.qcms.cms.entity.main.ContentTxt;

public interface ContentTxtMng {
	public ContentTxt save(ContentTxt txt, Content content);

	public ContentTxt update(ContentTxt txt, Content content);
}