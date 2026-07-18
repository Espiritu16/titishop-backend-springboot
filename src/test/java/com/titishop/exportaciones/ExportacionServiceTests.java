package com.titishop.exportaciones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.titishop.compartido.response.PaginaResponse;
import com.titishop.exportaciones.exception.ExportacionSinDatosException;
import com.titishop.exportaciones.exception.FormatoExportacionInvalidoException;
import com.titishop.exportaciones.service.ColumnaExportacion;
import com.titishop.exportaciones.service.ExportacionService;
import com.titishop.exportaciones.service.FormatoExportacion;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ExportacionServiceTests {

	private final ExportacionService exportacionService = new ExportacionService();

	@Test
	void exportaExcelRecorriendoPaginasPorBloques() {
		AtomicInteger llamadas = new AtomicInteger();

		var archivo = exportacionService.exportar(
				"Productos",
				"productos",
				FormatoExportacion.EXCEL,
				(page, size) -> {
					llamadas.incrementAndGet();
					if (page == 0) {
						return pagina(List.of(new ProductoFila("Base <nude>", "BEL-001")), page, false);
					}
					return pagina(List.of(new ProductoFila("Serum facial", "BEL-002")), page, true);
				},
				List.of(
						new ColumnaExportacion<>("Producto", ProductoFila::nombre),
						new ColumnaExportacion<>("SKU", ProductoFila::sku)
				)
		);

		String contenido = new String(archivo.contenido(), StandardCharsets.UTF_8);
		assertThat(llamadas).hasValue(2);
		assertThat(archivo.nombreArchivo()).matches("titishop_productos_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}\\.xls");
		assertThat(contenido).contains("Base &lt;nude&gt;", "Serum facial");
	}

	@Test
	void exportaPdfConTablaLegible() {
		var archivo = exportacionService.exportar(
				"Productos",
				"productos",
				FormatoExportacion.PDF,
				(page, size) -> pagina(List.of(new ProductoFila("Paleta sombras nude", "BEL-003")), page, true),
				List.of(
						new ColumnaExportacion<>("Producto", ProductoFila::nombre),
						new ColumnaExportacion<>("SKU", ProductoFila::sku)
				)
		);

		String contenido = new String(archivo.contenido(), StandardCharsets.UTF_8);
		assertThat(archivo.nombreArchivo()).matches("titishop_productos_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}\\.pdf");
		assertThat(contenido).startsWith("%PDF-1.4");
		assertThat(contenido).contains("0.95 g", "0.85 G", "Paleta sombras nude", "BEL-003");
		assertThat(contenido).doesNotContain("40 524 m 802 524 l S");
	}

	@Test
	void rechazaExportacionSinDatos() {
		assertThatThrownBy(() -> exportacionService.exportar(
				"Productos",
				"productos",
				FormatoExportacion.EXCEL,
				(page, size) -> pagina(List.of(), page, true),
				List.of(new ColumnaExportacion<>("Producto", ProductoFila::nombre))
		)).isInstanceOf(ExportacionSinDatosException.class)
				.hasMessage("No hay datos para exportar con los filtros seleccionados.");
	}

	@Test
	void rechazaFormatoInvalido() {
		assertThatThrownBy(() -> FormatoExportacion.desde("word"))
				.isInstanceOf(FormatoExportacionInvalidoException.class)
				.hasMessage("Formato de exportación inválido. Use Excel o PDF.");
	}

	private static PaginaResponse<ProductoFila> pagina(List<ProductoFila> content, int page, boolean last) {
		return new PaginaResponse<>(
				content,
				page,
				100,
				content.size(),
				last ? page + 1 : page + 2,
				page == 0,
				last,
				content.isEmpty()
		);
	}

	private record ProductoFila(String nombre, String sku) {
	}
}
