package br.edu.utfpr.oo2.FinanSystem.gui;

import br.edu.utfpr.oo2.FinanSystem.entities.Categoria;
import br.edu.utfpr.oo2.FinanSystem.service.CategoriaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class JanelaCategoria extends JDialog {

    private final CategoriaService categoriaService = new CategoriaService();
    private JTable tabela;
    private DefaultTableModel modelo;

    public JanelaCategoria(Frame owner, boolean modal) {
        super(owner, modal);
        init();
        carregarTabela();
    }

    private void init() {
        setTitle("FinanSystem - Categorias");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Tipo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);

        JPanel botoes = new JPanel(new GridLayout(1, 4, 10, 0));
        JButton add = new JButton("Adicionar");
        JButton edit = new JButton("Editar");
        JButton del = new JButton("Excluir");
        JButton fechar = new JButton("Fechar");

        botoes.add(add);
        botoes.add(edit);
        botoes.add(del);
        botoes.add(fechar);

        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        add.addActionListener(e -> adicionar());
        edit.addActionListener(e -> editar());
        del.addActionListener(e -> excluir());
        fechar.addActionListener(e -> dispose());
    }

    private void carregarTabela() {
        TarefaComCarregamento.executar(
                (Frame) getOwner(),
                () -> {
                    List<Categoria> categorias = categoriaService.listarCategorias();
                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);
                        for (Categoria c : categorias) {
                            modelo.addRow(new Object[]{
                                    c.getId(),
                                    c.getNome(),
                                    c.getTipo()
                            });
                        }
                    });
                },
                null
        );
    }

    private Categoria coletarDados(Categoria existente) {
        JTextField nome = new JTextField(existente != null ? existente.getNome() : "");

        String[] tipos = {"Entrada", "Saída", "Investimento"};
        JComboBox<String> tipo = new JComboBox<>(tipos);
        if (existente != null) tipo.setSelectedItem(existente.getTipo());

        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 5));
        painel.add(new JLabel("Nome:"));
        painel.add(nome);
        painel.add(new JLabel("Tipo:"));
        painel.add(tipo);

        int r = JOptionPane.showConfirmDialog(this, painel, "Dados da Categoria",
                JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return null;

        Categoria c = existente != null ? existente : new Categoria();
        c.setNome(nome.getText().trim());
        c.setTipo(tipo.getSelectedItem().toString());

        return c;
    }

    private Integer getIdSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return null;
        return (Integer) modelo.getValueAt(linha, 0);
    }

    private void adicionar() {
        Categoria nova = coletarDados(null);
        if (nova == null) return;

        TarefaComCarregamento.executar(
                (Frame) getOwner(),
                () -> categoriaService.cadastrarCategoria(nova),
                () -> {
                    JOptionPane.showMessageDialog(this, "Categoria cadastrada com sucesso!");
                    carregarTabela();
                }
        );
    }

    private void editar() {
        Integer id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para editar.");
            return;
        }

        TarefaComCarregamento.executarComRetorno(
                (Frame) getOwner(),
                () -> categoriaService.buscarPorId(id),
                categoria -> {
                    Categoria atualizada = coletarDados(categoria);
                    if (atualizada == null) return;

                    TarefaComCarregamento.executar(
                            (Frame) getOwner(),
                            () -> categoriaService.atualizarCategoria(atualizada),
                            () -> {
                                JOptionPane.showMessageDialog(this, "Categoria atualizada com sucesso!");
                                carregarTabela();
                            }
                    );
                }
        );
    }

    private void excluir() {
        Integer id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para excluir.");
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "Excluir esta categoria?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        TarefaComCarregamento.executar(
                (Frame) getOwner(),
                () -> categoriaService.excluirCategoria(id),
                () -> {
                    JOptionPane.showMessageDialog(this, "Categoria excluída com sucesso!");
                    carregarTabela();
                }
        );
    }
}