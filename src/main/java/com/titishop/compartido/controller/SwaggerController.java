package com.titishop.compartido.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SwaggerController {

	@GetMapping(value = "/swagger", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String swagger() {
		return """
				<!DOCTYPE html>
				<html lang="es">
				<head>
					<meta charset="UTF-8">
					<meta name="viewport" content="width=device-width, initial-scale=1.0">
					<title>TitiShop API - Swagger</title>
					<link rel="stylesheet" type="text/css" href="/swagger-ui/swagger-ui.css">
					<link rel="stylesheet" type="text/css" href="/swagger-ui/index.css">
					<link rel="icon" type="image/png" href="/swagger-ui/favicon-32x32.png" sizes="32x32">
					<link rel="icon" type="image/png" href="/swagger-ui/favicon-16x16.png" sizes="16x16">
				</head>
				<body>
					<div id="swagger-ui"></div>
					<script src="/swagger-ui/swagger-ui-bundle.js" charset="UTF-8"></script>
					<script src="/swagger-ui/swagger-ui-standalone-preset.js" charset="UTF-8"></script>
					<script>
						window.onload = function() {
							window.ui = SwaggerUIBundle({
								url: "/v3/api-docs",
								configUrl: "/v3/api-docs/swagger-config",
								dom_id: "#swagger-ui",
								deepLinking: true,
								presets: [
									SwaggerUIBundle.presets.apis,
									SwaggerUIStandalonePreset
								],
								plugins: [
									SwaggerUIBundle.plugins.DownloadUrl
								],
								layout: "StandaloneLayout",
								validatorUrl: ""
							});
						};
					</script>
				</body>
				</html>
				""";
	}
}
