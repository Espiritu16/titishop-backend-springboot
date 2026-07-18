package com.titishop.exportaciones.service;

import com.titishop.compartido.response.PaginaResponse;
import com.titishop.exportaciones.exception.ExportacionSinDatosException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

@Service
public class ExportacionService {

	private static final int BLOQUE_EXPORTACION = 100;

	public <T> ArchivoExportacion exportar(
			String titulo,
			String nombreBase,
			FormatoExportacion formato,
			BiFunction<Integer, Integer, PaginaResponse<T>> cargarPagina,
			List<ColumnaExportacion<T>> columnas
	) {
		List<List<String>> filas = cargarFilas(cargarPagina, columnas);
		if (filas.isEmpty()) {
			throw new ExportacionSinDatosException();
		}
		byte[] contenido = formato == FormatoExportacion.EXCEL
				? crearExcel(titulo, columnas, filas)
				: crearPdf(titulo, columnas, filas);
		return new ArchivoExportacion(nombreArchivo(nombreBase, formato), formato.mediaType(), contenido);
	}

	private String nombreArchivo(String nombreBase, FormatoExportacion formato) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
		return "titishop_" + nombreBase + "_" + timestamp + "." + formato.extension();
	}

	private <T> List<List<String>> cargarFilas(
			BiFunction<Integer, Integer, PaginaResponse<T>> cargarPagina,
			List<ColumnaExportacion<T>> columnas
	) {
		List<List<String>> filas = new ArrayList<>();
		int page = 0;
		boolean last;
		do {
			PaginaResponse<T> pagina = cargarPagina.apply(page, BLOQUE_EXPORTACION);
			for (T item : pagina.content()) {
				filas.add(columnas.stream().map(columna -> normalizarCelda(columna.valor().apply(item))).toList());
			}
			last = pagina.last();
			page++;
		} while (!last);
		return filas;
	}

	private <T> byte[] crearExcel(String titulo, List<ColumnaExportacion<T>> columnas, List<List<String>> filas) {
		StringBuilder html = new StringBuilder();
		html.append("<!doctype html><html><head><meta charset=\"utf-8\"><style>");
		html.append("table{border-collapse:collapse;font-family:Arial,sans-serif;font-size:12px}");
		html.append("th{background:#eef2ff;color:#1f2937}th,td{border:1px solid #d1d5db;padding:6px 8px;text-align:left}");
		html.append("h1{font-family:Arial,sans-serif;font-size:18px}");
		html.append("</style></head><body><h1>").append(escapeHtml(titulo)).append("</h1><table><thead><tr>");
		for (ColumnaExportacion<T> columna : columnas) {
			html.append("<th>").append(escapeHtml(columna.encabezado())).append("</th>");
		}
		html.append("</tr></thead><tbody>");
		for (List<String> fila : filas) {
			html.append("<tr>");
			for (String celda : fila) {
				html.append("<td>").append(escapeHtml(celda)).append("</td>");
			}
			html.append("</tr>");
		}
		html.append("</tbody></table></body></html>");
		return html.toString().getBytes(StandardCharsets.UTF_8);
	}

	private <T> byte[] crearPdf(String titulo, List<ColumnaExportacion<T>> columnas, List<List<String>> filas) {
		List<String> encabezados = columnas.stream().map(ColumnaExportacion::encabezado).toList();
		List<String> paginas = crearPaginasPdf(titulo, encabezados, filas);
		List<Integer> pageIds = new ArrayList<>();
		for (int index = 0; index < paginas.size(); index++) {
			pageIds.add(3 + index * 2);
		}
		int fontId = 3 + paginas.size() * 2;
		List<String> objetos = new ArrayList<>();
		objetos.add("1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj");
		objetos.add("2 0 obj << /Type /Pages /Kids [" + pageIds.stream().map(id -> id + " 0 R").reduce("", (a, b) -> a + b + " ") + "] /Count " + paginas.size() + " >> endobj");
		for (int index = 0; index < paginas.size(); index++) {
			int pageId = pageIds.get(index);
			int contentId = pageId + 1;
			String contenido = paginas.get(index);
			objetos.add(pageId + " 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] /Resources << /Font << /F1 " + fontId + " 0 R >> >> /Contents " + contentId + " 0 R >> endobj");
			objetos.add(contentId + " 0 obj << /Length " + contenido.length() + " >> stream\n" + contenido + "\nendstream endobj");
		}
		objetos.add(fontId + " 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj");
		StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
		List<Integer> offsets = new ArrayList<>();
		offsets.add(0);
		for (String objeto : objetos) {
			offsets.add(pdf.length());
			pdf.append(objeto).append('\n');
		}
		int xrefOffset = pdf.length();
		pdf.append("xref\n0 ").append(objetos.size() + 1).append("\n0000000000 65535 f \n");
		offsets.stream().skip(1).forEach(offset -> pdf.append(String.format(Locale.ROOT, "%010d 00000 n \n", offset)));
		pdf.append("trailer << /Size ").append(objetos.size() + 1).append(" /Root 1 0 R >>\nstartxref\n").append(xrefOffset).append("\n%%EOF");
		return pdf.toString().getBytes(StandardCharsets.UTF_8);
	}

	private List<String> crearPaginasPdf(String titulo, List<String> encabezados, List<List<String>> filas) {
		int filasPorPagina = 22;
		int altoFila = 20;
		List<ColumnaPdf> columnas = calcularColumnas(encabezados, filas);
		List<String> paginas = new ArrayList<>();
		for (int inicio = 0; inicio < filas.size(); inicio += filasPorPagina) {
			List<List<String>> bloque = filas.subList(inicio, Math.min(inicio + filasPorPagina, filas.size()));
			List<String> lineas = new ArrayList<>();
			lineas.add("0.95 g");
			lineas.add("40 516 762 24 re f");
			lineas.add("0 g");
			lineas.add(textoPdf(titulo, 40, 552, 14));
			lineas.add(textoPdf("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 650, 552, 8));
			lineas.addAll(dibujarFilaPdf(encabezados, columnas, 524, true));
			lineas.add("0.85 G");
			lineas.add("40 516 m 802 516 l S");
			for (int index = 0; index < bloque.size(); index++) {
				int top = 516 - index * altoFila;
				int textY = top - 13;
				if (index % 2 == 0) {
					lineas.add("0.98 g");
					lineas.add("40 " + (top - altoFila) + " 762 " + altoFila + " re f");
					lineas.add("0 g");
				}
				lineas.addAll(dibujarFilaPdf(bloque.get(index), columnas, textY, false));
				lineas.add("0.85 G");
				lineas.add("40 " + (top - altoFila) + " m 802 " + (top - altoFila) + " l S");
				lineas.add("0 g");
			}
			lineas.add("0 g");
			lineas.add(textoPdf("Página " + (paginas.size() + 1), 748, 28, 8));
			paginas.add(String.join("\n", lineas));
		}
		return paginas;
	}

	private List<ColumnaPdf> calcularColumnas(List<String> encabezados, List<List<String>> filas) {
		int totalColumnas = Math.max(encabezados.size(), 1);
		List<Integer> pesos = new ArrayList<>();
		for (int columna = 0; columna < totalColumnas; columna++) {
			int maximo = encabezados.get(columna).length();
			for (List<String> fila : filas) {
				if (columna < fila.size()) {
					maximo = Math.max(maximo, fila.get(columna).length());
				}
			}
			pesos.add(Math.min(Math.max(maximo, 10), 28));
		}
		int totalPesos = pesos.stream().mapToInt(Integer::intValue).sum();
		int x = 40;
		List<ColumnaPdf> columnas = new ArrayList<>();
		for (Integer peso : pesos) {
			int ancho = Math.max(54, Math.floorDiv(peso * 762, totalPesos));
			columnas.add(new ColumnaPdf(x, ancho, Math.max(7, Math.floorDiv(ancho, 5))));
			x += ancho;
		}
		return columnas;
	}

	private List<String> dibujarFilaPdf(List<String> fila, List<ColumnaPdf> columnas, int y, boolean encabezado) {
		List<String> lineas = new ArrayList<>();
		for (int index = 0; index < columnas.size(); index++) {
			ColumnaPdf columna = columnas.get(index);
			String texto = index < fila.size() ? fila.get(index) : "-";
			lineas.add(textoPdf(recortar(texto, columna.caracteres()), columna.x() + 2, y, encabezado ? 8 : 7));
		}
		return lineas;
	}

	private String textoPdf(String texto, int x, int y, int size) {
		return "BT /F1 " + size + " Tf " + x + " " + y + " Td (" + escapePdf(texto) + ") Tj ET";
	}

	private String normalizarCelda(Object value) {
		if (value == null || value.toString().isBlank()) {
			return "-";
		}
		return formatearValor(value);
	}

	private String formatearValor(Object value) {
		if (value instanceof OffsetDateTime fecha) {
			return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		}
		if (value instanceof LocalDateTime fecha) {
			return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		}
		String texto = value.toString();
		try {
			return OffsetDateTime.parse(texto).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		} catch (RuntimeException ignored) {
			return texto;
		}
	}

	private String recortar(String value, int max) {
		if (value.length() <= max) {
			return value;
		}
		return value.substring(0, Math.max(0, max - 3)) + "...";
	}

	private String escapeHtml(String value) {
		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	private String escapePdf(String value) {
		return value
				.replace("\\", "\\\\")
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replaceAll("[^\\x20-\\x7E]", "");
	}

	private record ColumnaPdf(int x, int ancho, int caracteres) {
	}
}
