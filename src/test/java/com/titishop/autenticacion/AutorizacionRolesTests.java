package com.titishop.autenticacion;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
class AutorizacionRolesTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void productosRequiereAdministradorOAlmacenero() throws Exception {
		mockMvc.perform(get("/api/productos").with(jwtSupervisor()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/productos").with(jwtAlmacenero()))
				.andExpect(status().isOk());
	}

	@Test
	void movimientosRequiereAdministradorOAlmacenero() throws Exception {
		mockMvc.perform(get("/api/movimientos").with(jwtSupervisor()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/movimientos").with(jwtAlmacenero()))
				.andExpect(status().isOk());
	}

	@Test
	void reportesYPanelPermitenSupervisorPeroBloqueanAlmacenero() throws Exception {
		mockMvc.perform(get("/api/reportes/stock").with(jwtSupervisor()))
				.andExpect(status().isOk());
		mockMvc.perform(get("/api/reportes/stock").with(jwtAlmacenero()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/panel/resumen").with(jwtSupervisor()))
				.andExpect(status().isOk());
		mockMvc.perform(get("/api/panel/resumen").with(jwtAlmacenero()))
				.andExpect(status().isForbidden());
	}

	@Test
	void usuariosRequiereAdministrador() throws Exception {
		mockMvc.perform(get("/api/usuarios").with(jwtAlmacenero()))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/usuarios").with(jwtAdministrador()))
				.andExpect(status().isOk());
	}

	private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAdministrador() {
		return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
	}

	private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAlmacenero() {
		return jwt().authorities(new SimpleGrantedAuthority("ROLE_ALMACENERO"));
	}

	private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtSupervisor() {
		return jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISOR"));
	}
}
