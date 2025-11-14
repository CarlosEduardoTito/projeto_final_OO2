package br.edu.utfpr.oo2.FinanSystem.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.oo2.FinanSystem.dao.BancoDados;
import br.edu.utfpr.oo2.FinanSystem.dao.ContaDAO;
import br.edu.utfpr.oo2.FinanSystem.dao.CategoriaDAO;
import br.edu.utfpr.oo2.FinanSystem.dao.TransacaoDAO;
import br.edu.utfpr.oo2.FinanSystem.entities.Conta;
import br.edu.utfpr.oo2.FinanSystem.entities.Categoria;
import br.edu.utfpr.oo2.FinanSystem.entities.Transacao;

public class TransacaoService {

    public void cadastrarTransacao(Transacao transacao) throws SQLException, IOException, Exception {
        Connection conn = BancoDados.conectar();
        TransacaoDAO transacaoDAO = new TransacaoDAO(conn);
        ContaDAO contaDAO = new ContaDAO(conn);
        CategoriaDAO categoriaDAO = new CategoriaDAO(conn);

        // Validações
        Conta conta = contaDAO.buscarPorId(transacao.getContaId());
        if (conta == null) {
            throw new Exception("Conta não encontrada.");
        }

        Categoria categoria = categoriaDAO.buscarPorId(transacao.getCategoriaId());
        if (categoria == null) {
            throw new Exception("Categoria não encontrada.");
        }

        if (transacao.getValor() <= 0) {
            throw new Exception("O valor da transação deve ser maior que zero.");
        }

        // Cadastra a transação
        transacaoDAO.cadastrar(transacao);
    }

    public void atualizarTransacao(Transacao transacao) throws SQLException, IOException, Exception {
        Connection conn = BancoDados.conectar();
        TransacaoDAO transacaoDAO = new TransacaoDAO(conn);

        if (transacao.getValor() <= 0) {
            throw new Exception("O valor da transação deve ser maior que zero.");
        }

        transacaoDAO.atualizar(transacao);
    }

    public void excluirTransacao(Integer id) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        TransacaoDAO transacaoDAO = new TransacaoDAO(conn);
        transacaoDAO.excluir(id);
    }

    public Transacao buscarPorId(Integer id) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        TransacaoDAO transacaoDAO = new TransacaoDAO(conn);
        return transacaoDAO.buscarPorId(id);
    }

    public List<Transacao> listarTransacoes() throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        TransacaoDAO transacaoDAO = new TransacaoDAO(conn);
        return transacaoDAO.buscarTodos();
    }
}
