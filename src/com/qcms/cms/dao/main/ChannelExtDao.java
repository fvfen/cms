package com.qcms.cms.dao.main;

import com.qcms.cms.entity.main.ChannelExt;
import com.qcms.common.hibernate3.Updater;

public interface ChannelExtDao {
	public ChannelExt save(ChannelExt bean);

	public ChannelExt updateByUpdater(Updater<ChannelExt> updater);
}