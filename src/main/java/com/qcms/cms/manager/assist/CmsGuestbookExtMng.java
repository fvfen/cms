package com.qcms.cms.manager.assist;

import com.qcms.cms.entity.assist.CmsGuestbook;
import com.qcms.cms.entity.assist.CmsGuestbookExt;

public interface CmsGuestbookExtMng {
	public CmsGuestbookExt save(CmsGuestbookExt ext, CmsGuestbook guestbook);

	public CmsGuestbookExt update(CmsGuestbookExt ext);
}