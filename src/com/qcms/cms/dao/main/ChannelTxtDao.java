package com.qcms.cms.dao.main;

import com.qcms.cms.entity.main.ChannelTxt;
import com.qcms.common.hibernate3.Updater;

public interface ChannelTxtDao {
	public ChannelTxt findById(Integer id);

	public ChannelTxt save(ChannelTxt bean);

	public ChannelTxt updateByUpdater(Updater<ChannelTxt> updater);
}