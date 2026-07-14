package com.titishop.autenticacion.entity;

import com.titishop.usuarios.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_codes")
public class PasswordResetCode {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@Column(name = "codigo_hash", nullable = false)
	private String codigoHash;

	@Column(name = "reset_token_hash")
	private String resetTokenHash;

	@Column(name = "expira_en", nullable = false)
	private Instant expiraEn;

	@Column(name = "validado_en")
	private Instant validadoEn;

	@Column(name = "usado_en")
	private Instant usadoEn;

	@Column(nullable = false)
	private int intentos;

	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn;

	protected PasswordResetCode() {
	}

	public PasswordResetCode(Usuario usuario, String codigoHash, Instant expiraEn, Instant creadoEn) {
		this.usuario = usuario;
		this.codigoHash = codigoHash;
		this.expiraEn = expiraEn;
		this.creadoEn = creadoEn;
		this.intentos = 0;
	}

	public UUID getId() {
		return id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public String getCodigoHash() {
		return codigoHash;
	}

	public String getResetTokenHash() {
		return resetTokenHash;
	}

	public Instant getExpiraEn() {
		return expiraEn;
	}

	public Instant getValidadoEn() {
		return validadoEn;
	}

	public Instant getUsadoEn() {
		return usadoEn;
	}

	public int getIntentos() {
		return intentos;
	}

	public Instant getCreadoEn() {
		return creadoEn;
	}

	public void registrarIntentoFallido() {
		this.intentos++;
	}

	public void marcarValidado(String resetTokenHash, Instant validadoEn) {
		this.resetTokenHash = resetTokenHash;
		this.validadoEn = validadoEn;
	}

	public void marcarUsado(Instant usadoEn) {
		this.usadoEn = usadoEn;
	}
}
