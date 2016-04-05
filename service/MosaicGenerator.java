package com.mosaic.service;

import java.util.List;

import com.mosaic.BusinessLogic.MosaicManager;

public class MosaicGenerator {
	private MosaicManager manager = new MosaicManager();
	
	public String getMosaicURL(String profileImg, List<String> tiles) throws Exception {
		String result = manager.getProcessedImg(profileImg, tiles);
		return result;
	}
}
