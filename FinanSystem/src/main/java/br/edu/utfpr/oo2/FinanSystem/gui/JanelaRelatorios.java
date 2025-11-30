package br.edu.utfpr.oo2.FinanSystem.gui;

import br.edu.utfpr.oo2.FinanSystem.entities.Transacao;
import br.edu.utfpr.oo2.FinanSystem.service.RelatorioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class JanelaRelatorios extends JDialog {

	private JComboBox<String> cbMes;
	private JTextField txtAno;
	private JTable tabela;
	private DefaultTableModel modelo;
	private final RelatorioService service = new RelatorioService();
	private List<Transacao> transacoesAtuais;

	public JanelaRelatorios(Frame owner) {
		
		super(owner, "Relatórios e Exportação", true);
		setSize(800, 500);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		
		iniciarTopo();
		iniciarTabela();
		iniciarRodape();
		
	}
	
	private void iniciarTopo() {
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cbMes = new JComboBox<>(new String[] {
				"01 - Janeiro", "02 - Fevereiro", "03 - Março", "04 - Abril", 
                "05 - Maio", "06 - Junho", "07 - Julho", "08 - Agosto", 
                "09 - Setembro", "10 - Outubro", "11 - Novembro", "12 - Dezembro"
		});
		
		cbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
		
		txtAno = new JTextField(String.valueOf(LocalDate.now().getYear()), 5);
		
		JButton btnFiltrar = new JButton("Gerar Relatorio em Tela");
		btnFiltrar.addActionListener(e -> carregarDados());
		
		panel.add(new JLabel("Mês:"));
		panel.add(cbMes);
		panel.add(new JLabel("Ano:"));
		panel.add(txtAno);
		panel.add(btnFiltrar);
		
		add(panel, BorderLayout.NORTH);
	}
	
	private void iniciarTabela() {
		
		String[] colunas = {"ID", "Data", "Descrição", "Valor (R$)"};
		modelo = new DefaultTableModel(colunas, 0) {
			
			public boolean isCellEditable(int r, int c) {return false;}
		};
		tabela = new JTable(modelo);
		add(new JScrollPane(tabela), BorderLayout.CENTER);
		
	}
	
	private void iniciarRodape() {
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton btnPdf = new JButton("Exportar PDF");
		btnPdf.setBackground(new Color(200, 50, 50));
		btnPdf.setForeground(Color.WHITE);
		btnPdf.addActionListener(e -> acaoExportar("PDF"));
		
		JButton btnExcel = new JButton("Exportar Excel");
		btnExcel.setBackground(new Color(34, 139, 34));
		btnExcel.setForeground(Color.WHITE);
		btnExcel.addActionListener(e -> acaoExportar("EXCEL"));
		
		JButton btnFechar = new JButton("Fechar");
		btnFechar.addActionListener(e -> dispose());
		
		panel.add(btnPdf);
		panel.add(btnExcel);
		panel.add(btnFechar);
		
		add(panel, BorderLayout.SOUTH);
		
	}
	
	private void carregarDados() {
		
		int mes = cbMes.getSelectedIndex() + 1;
		int ano;
		
		try {
			
			ano = Integer.parseInt(txtAno.getText());
			
		} catch(NumberFormatException ex) {
			
			JOptionPane.showMessageDialog(this, "Ano inválido.");
			return;
		}
		
		TarefaComCarregamento.executarComRetorno(
				(Frame) getOwner(),
				() -> service.buscarTransacoesMensais(mes, ano),
				lista -> {
					this.transacoesAtuais = lista;
					modelo.setRowCount(0);
					for (Transacao t : lista) {
						modelo.addRow(new Object[] {
								t.getId(),
								t.getData(),
								t.getDescricao(),
								t.getValor()
						});
					}
					if (lista.isEmpty()) {
						
						JOptionPane.showMessageDialog(this, "Nenhuma transação encontrada neste período.");
						
					}
				}
		);
	}
	
	private void acaoExportar(String tipo) {
		
		if (transacoesAtuais == null || transacoesAtuais.isEmpty()) {
			
			JOptionPane.showMessageDialog(this, "Gere o relatório na tela antes de exportar.");
			return;
			
		}
		
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Salvar Relatorio " + tipo);
		
		String nomePadrao = "Relatorio_" + LocalDate.now() + (tipo.equals("PDF") ? ".pdf" : ".xlsx");
		jfc.setSelectedFile(new File(nomePadrao));
		
		int r = jfc.showSaveDialog(this);
		if(r == JFileChooser.APPROVE_OPTION) {
			
			File arquivo = jfc.getSelectedFile();
			
			TarefaComCarregamento.executar(
					(Frame) getOwner(),
					() -> {
						if (tipo.equals("PDF")) {
							
							service.exportarPdf(transacoesAtuais, arquivo.getAbsolutePath());
							
						} else {
							
							service.exportarExcel(transacoesAtuais, arquivo.getAbsolutePath());
						}
					},
					() -> JOptionPane.showMessageDialog(this, "Arquivo exportado com sucesso!")
			);
			
		}
		
	}

}
