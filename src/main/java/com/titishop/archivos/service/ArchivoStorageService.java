package com.titishop.archivos.service;

import com.titishop.archivos.config.ArchivosProperties;
import com.titishop.archivos.dto.ArchivoResponse;
import com.titishop.archivos.exception.ArchivoInvalidoException;
import com.titishop.archivos.exception.ArchivoStorageException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoStorageService {

	private static final String DIRECTORIO_PRODUCTOS = "productos";
	private static final Map<String, String> EXTENSIONES_PERMITIDAS = Map.of(
			"image/jpeg", ".jpg",
			"image/png", ".png",
			"image/webp", ".webp",
			"image/gif", ".gif"
	);

	private final ArchivosProperties properties;
	private final Path rootDir;

	public ArchivoStorageService(ArchivosProperties properties) {
		this.properties = properties;
		this.rootDir = properties.dir().toAbsolutePath().normalize();
	}

	@PostConstruct
	public void inicializar() {
		try {
			Files.createDirectories(rootDir.resolve(DIRECTORIO_PRODUCTOS));
		} catch (IOException ex) {
			throw new ArchivoStorageException("No se pudo preparar el directorio de archivos.", ex);
		}
	}

	public ArchivoResponse guardarImagenProducto(MultipartFile archivo) {
		validarArchivo(archivo);

		String contentType = normalizarContentType(archivo.getContentType());
		String extension = EXTENSIONES_PERMITIDAS.get(contentType);
		String nombreArchivo = UUID.randomUUID() + extension;
		Path directorioDestino = rootDir.resolve(DIRECTORIO_PRODUCTOS).normalize();
		Path destino = directorioDestino.resolve(nombreArchivo).normalize();

		if (!destino.startsWith(directorioDestino)) {
			throw new ArchivoInvalidoException("Nombre de archivo invalido.");
		}

		try (InputStream inputStream = archivo.getInputStream()) {
			Files.createDirectories(directorioDestino);
			Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new ArchivoStorageException("No se pudo guardar la imagen.", ex);
		}

		String ruta = DIRECTORIO_PRODUCTOS + "/" + nombreArchivo;
		return new ArchivoResponse(
				publicUrlBase() + "/" + ruta,
				ruta,
				archivo.getOriginalFilename(),
				contentType,
				archivo.getSize()
		);
	}

	private void validarArchivo(MultipartFile archivo) {
		if (archivo == null || archivo.isEmpty()) {
			throw new ArchivoInvalidoException("Debe enviar una imagen.");
		}
		if (archivo.getSize() > properties.maxFileSize().toBytes()) {
			throw new ArchivoInvalidoException("La imagen supera el tamano maximo permitido.");
		}
		String contentType = normalizarContentType(archivo.getContentType());
		if (!EXTENSIONES_PERMITIDAS.containsKey(contentType)) {
			throw new ArchivoInvalidoException("Solo se permiten imagenes JPG, PNG, WEBP o GIF.");
		}
	}

	private String normalizarContentType(String contentType) {
		return contentType == null ? "" : contentType.trim().toLowerCase(Locale.ROOT);
	}

	private String publicUrlBase() {
		String publicUrl = properties.publicUrl().trim();
		return publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
	}
}
