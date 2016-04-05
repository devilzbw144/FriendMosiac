package com.mosaic.BusinessLogic;

import java.util.List;

import com.mosaic.model.Mosaic;

public class MosaicManager {
	private Mosaic mosaic;
	
	public MosaicManager() {
		mosaic = new Mosaic();
	}
	
	public String getProcessedImg(String profileImg, List<String> tiles) throws Exception {
		tiles.add(profileImg);
		return mosaic.startMosaic(profileImg, tiles);
	}
}
