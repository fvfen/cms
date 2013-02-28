package com.qcms.cms.manager.main;

import com.qcms.cms.entity.main.CmsUser;
import com.qcms.cms.entity.main.CmsUserExt;

public interface CmsUserExtMng {
	public CmsUserExt save(CmsUserExt ext, CmsUser user);

	public CmsUserExt update(CmsUserExt ext, CmsUser user);
}