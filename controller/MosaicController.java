package com.mosaic.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mosaic.service.MosaicGenerator;

@Controller
public class MosaicController {
	@RequestMapping("/gallery")
	public ModelAndView getGalleryPage() {
		
		return new ModelAndView("gallery");
	}
	
	@RequestMapping(value = "/mosaic/create", method = RequestMethod.POST)
	public @ResponseBody String createMosaic(@RequestParam("profileImg") String profileImg, @RequestParam("tiles[]") List<String> tiles) throws Exception {
		MosaicGenerator generator = new MosaicGenerator();
		return generator.getMosaicURL(profileImg, tiles);
	}
}
