package br.edu.utfpr.oo2.FinanSystem.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import br.edu.utfpr.oo2.FinanSystem.dao.ContaDAO;
import br.edu.utfpr.oo2.FinanSystem.entities.Conta;
import br.edu.utfpr.oo2.FinanSystem.dao.BancoDados;
import java.sql.Connection;

public class ContaService {

    public void cadastrarConta(Conta conta) throws SQLException, IOException, Exception {
        Connection conn = BancoDados.conectar();
        ContaDAO contaDAO = new ContaDAO(conn);

        Conta existente = contaDAO.buscarPorNumero(conta.getNumeroConta());
        if (existente != null) {
            throw new Exception("Conta já cadastrada com esse número.");
        }

        contaDAO.cadastrar(conta);
    }

    public void atualizarConta(Conta conta) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        ContaDAO contaDAO = new ContaDAO(conn);
        contaDAO.atualizar(conta);
    }

    public void excluirConta(Integer id) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        ContaDAO contaDAO = new ContaDAO(conn);
        contaDAO.excluir(id);
    }

    public Conta buscarPorId(Integer id) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        ContaDAO contaDAO = new ContaDAO(conn);
        return contaDAO.buscarPorId(id);
    }

    public List<Conta> listarContas() throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        ContaDAO contaDAO = new ContaDAO(conn);
        return contaDAO.buscarTodos();
    }
}
