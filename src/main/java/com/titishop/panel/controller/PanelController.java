package com.titishop.panel.controller;

import com.titishop.panel.service.PanelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/panel")
public class PanelController {

	private final PanelService panelService;

	public PanelController(PanelService panelService) {
		this.panelService = panelService;
	}
}
