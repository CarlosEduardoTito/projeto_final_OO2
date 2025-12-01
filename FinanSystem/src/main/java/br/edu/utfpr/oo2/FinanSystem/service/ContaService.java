package br.edu.utfpr.oo2.FinanSystem.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.oo2.FinanSystem.dao.ContaDAO;
import br.edu.utfpr.oo2.FinanSystem.entities.Conta;
import br.edu.utfpr.oo2.FinanSystem.dao.BancoDados;

public class ContaService {

    public void cadastrarConta(Conta conta) throws Exception {
        validar(conta);

        Connection conn = BancoDados.conectar();
        ContaDAO dao = new ContaDAO(conn);

        if (dao.buscarPorNumeroEUserId(conta.getNumeroConta(), conta.getUserId()) != null)
            throw new Exception("Você já possui uma conta com esse número.");

        int linhas = dao.cadastrar(conta);
        if (linhas == 0)
            throw new Exception("Erro ao cadastrar conta.");
    }

    public void atualizarConta(Conta conta, Integer userId) throws Exception {
        validar(conta);

        Connection conn = BancoDados.conectar();
        ContaDAO dao = new ContaDAO(conn);

        Conta contaExistente = dao.buscarPorId(conta.getId());
        if (contaExistente == null) {
            throw new Exception("Conta não encontrada.");
        }
        
        if (!contaExistente.getUserId().equals(userId)) {
            throw new Exception("Você não tem permissão para atualizar esta conta.");
        }
        
        conta.setUserId(userId);

        int linhas = dao.atualizar(conta);
        if (linhas == 0)
            throw new Exception("Nenhuma conta foi atualizada.");
    }

    public void excluirConta(Integer id, Integer userId) throws Exception {
        Connection conn = BancoDados.conectar();
        ContaDAO dao = new ContaDAO(conn);

        Conta conta = dao.buscarPorId(id);
        if (conta == null) {
            throw new Exception("Conta não encontrada.");
        }
        
        if (!conta.getUserId().equals(userId)) {
            throw new Exception("Você não tem permissão para excluir esta conta.");
        }

        int linhas = dao.excluir(id);
        if (linhas == 0)
            throw new Exception("A conta não existe.");
    }

    public Conta buscarPorId(Integer id, Integer userId) throws SQLException, IOException, Exception {
        Connection conn = BancoDados.conectar();
        ContaDAO dao = new ContaDAO(conn);
        Conta conta = dao.buscarPorId(id);
        
        if (conta == null) {
            throw new Exception("Conta não encontrada.");
        }
        
        if (!conta.getUserId().equals(userId)) {
            throw new Exception("Você não tem permissão para acessar esta conta.");
        }
        
        return conta;
    }

    public List<Conta> listarContas(Integer userId) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        ContaDAO dao = new ContaDAO(conn);
        return dao.buscarPorUserId(userId);
    }

    private void validar(Conta c) throws Exception {
        if (c.getNomeBanco() == null || c.getNomeBanco().isBlank())
            throw new Exception("O nome do banco é obrigatório.");
        if (c.getAgencia() == null || c.getAgencia().isBlank())
            throw new Exception("A agência é obrigatória.");
        if (c.getNumeroConta() == null)
            throw new Exception("Número da conta é obrigatório.");
        if (c.getSaldoInicial() == null)
            throw new Exception("Saldo inicial é obrigatório.");
        if (c.getSaldoInicial() < 0)
            throw new Exception("Saldo inicial não pode ser negativo.");
        if (c.getTipoConta() == null || c.getTipoConta().isBlank())
            throw new Exception("O tipo da conta é obrigatório.");
    }


    public void transferir(int idOrigem, int idDestino, double valor, Integer userId) throws Exception {

        if (idOrigem == idDestino)
            throw new Exception("A conta de origem e destino não podem ser iguais.");

        if (valor <= 0)
            throw new Exception("O valor da transferência deve ser maior que zero.");

        Connection conn = BancoDados.conectar();
        ContaDAO dao = new ContaDAO(conn);

        Conta origem = dao.buscarPorId(idOrigem);
        Conta destino = dao.buscarPorId(idDestino);

        if (origem == null)
            throw new Exception("Conta de origem não encontrada.");

        if (destino == null)
            throw new Exception("Conta de destino não encontrada.");

        if (!origem.getUserId().equals(userId)) {
            throw new Exception("Você não tem permissão para transferir desta conta.");
        }

        if (origem.getSaldoInicial() < valor)
            throw new Exception("Saldo insuficiente na conta de origem.");


        origem.setSaldoInicial(origem.getSaldoInicial() - valor);
        destino.setSaldoInicial(destino.getSaldoInicial() + valor);


        dao.atualizar(origem);
        dao.atualizar(destino);
    }

}
