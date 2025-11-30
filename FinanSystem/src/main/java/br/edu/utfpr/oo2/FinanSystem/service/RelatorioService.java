package br.edu.utfpr.oo2.FinanSystem.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.edu.utfpr.oo2.FinanSystem.entities.Categoria;
import br.edu.utfpr.oo2.FinanSystem.entities.Conta;
import br.edu.utfpr.oo2.FinanSystem.entities.Transacao;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.DateFormatter;

public class RelatorioService {

	private final TransacaoService transacaoService = new TransacaoService();
	private final CategoriaService categoriaService = new CategoriaService();
	private final ContaService contaService = new ContaService();
	
	public List<Transacao> buscarTransacoesMensais(int mes, int ano) throws SQLException, IOException{
		
		LocalDate inicio = LocalDate.of(ano, mes, 1);
		LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
		
		return transacaoService.listarPorPeriodo(inicio, fim);
		
	}
	
	public void exportarPdf(List<Transacao> transacoes, String caminhoArquivo) throws Exception {
		
		Map<Integer, String> mapaCategorias = carregarMapaCategorias();
		Map<Integer, String> mapaContas = carregarMapaContas();
		
		PdfWriter writer = new PdfWriter(caminhoArquivo);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);
		
		document.add(new Paragraph("Relatório Financeiro Mensal").setBold().setFontSize(18));
		document.add(new Paragraph("Gerado em: " + LocalDate.now()));
		
		Table table = new Table(UnitValue.createPercentArray(new float[] {15, 20, 20, 25, 20}));
		table.setWidth(UnitValue.createPercentValue(100));
		
		table.addHeaderCell("Data");
		table.addHeaderCell("Conta");
		table.addHeaderCell("Categoria");
		table.addHeaderCell("Descrição");
		table.addHeaderCell("Valor (R$)");
		
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		double total = 0;
		
		for(Transacao t : transacoes) {
			
			table.addCell(t.getData().format(fmt));
			table.addCell(mapaContas.getOrDefault(t.getContaId(), "ID: " + t.getContaId()));
			table.addCell(mapaCategorias.getOrDefault(t.getCategoriaId(), "ID: " + t.getCategoriaId()));
			table.addCell(t.getDescricao());
			table.addCell(String.format("%.2f", t.getValor()));
			
			total += t.getValor();
		}
		
		document.add(table);
		document.add(new Paragraph("\nTotal movimentado (Absoluto): R$ " + String.format("%.2f", total)));
		
		document.close();	
	}
	
	public void exportarExcel(List<Transacao> transacoes, String caminhoArquivo) throws Exception{
		
		Map<Integer, String> mapaCategorias = carregarMapaCategorias();
		Map<Integer, String> mapaContas = carregarMapaContas();
		
		try (Workbook workbook = new XSSFWorkbook()){
			
			Sheet sheet = workbook.createSheet("Relatório");
			
			Row headerRow = sheet.createRow(0);
			String[] colunas = {"ID", "Data", "Conta", "Categoria", "Descrição", "Valor"};
			
			for(int i = 0; i < colunas.length; i++) {
				
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(colunas[i]);
				
			}
			
			int rowNum = 1;
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					
			for(Transacao t : transacoes) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(t.getId());
				row.createCell(1).setCellValue(t.getData().format(fmt));
				row.createCell(2).setCellValue(mapaContas.getOrDefault(t.getContaId(), "ID " + t.getContaId()));
				row.createCell(3).setCellValue(mapaCategorias.getOrDefault(t.getCategoriaId(), "ID " + t.getCategoriaId()));
				row.createCell(4).setCellValue(t.getDescricao());
				row.createCell(5).setCellValue(t.getValor());
			}
			
			for (int i = 0; i < colunas.length; i++) {
				
				sheet.autoSizeColumn(i);
				
			}
			
			try(FileOutputStream fileOut = new FileOutputStream(caminhoArquivo)){
				
				workbook.write(fileOut);
				
			}
		}
		
	}
	
	private Map<Integer, String> carregarMapaCategorias(){
		
		Map<Integer, String> map = new HashMap<>();
		try {
			
			List<Categoria> lista = categoriaService.listarCategorias();
			for(Categoria c : lista) map.put(c.getId(), c.getNome());
			
		} catch(Exception ignored) {}
		return map;
	}
	
	private Map<Integer, String> carregarMapaContas(){
		
		Map<Integer, String> map = new HashMap<>();
		try {
			
			List<Conta> lista = contaService.listarContas();
			for(Conta c : lista) map.put(c.getId(), c.getNomeBanco());
			
		} catch(Exception ignored) {}
		return map;
	}
	
}
