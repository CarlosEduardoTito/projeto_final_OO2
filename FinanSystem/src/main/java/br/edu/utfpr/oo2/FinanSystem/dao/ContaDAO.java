package br.edu.utfpr.oo2.FinanSystem.dao;

import br.edu.utfpr.oo2.FinanSystem.entities.Conta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO implements DAO<Conta, Integer> {

    private final Connection conn;

    public ContaDAO(Connection conn) {
        this.conn = conn;
    }

    private Conta mapear(ResultSet rs) throws SQLException {
        Conta c = new Conta();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("userId"));
        c.setNomeBanco(rs.getString("nomeBanco"));
        c.setAgencia(rs.getString("agencia"));
        c.setNumeroConta(rs.getInt("numeroConta"));
        c.setSaldoInicial(rs.getDouble("saldoInicial"));
        c.setTipoConta(rs.getString("tipoConta"));
        return c;
    }

    @Override
    public int cadastrar(Conta conta) throws SQLException {
        String sql = """
            INSERT INTO conta (
                userId, nomeBanco, agencia, numeroConta,
                saldoInicial, tipoConta
            ) VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, conta.getUserId());
            st.setString(2, conta.getNomeBanco());
            st.setString(3, conta.getAgencia());
            st.setInt(4, conta.getNumeroConta());
            st.setDouble(5, conta.getSaldoInicial());
            st.setString(6, conta.getTipoConta());

            int linhas = st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) conta.setId(rs.getInt(1));
            }

            return linhas;
        }
    }

    @Override
    public int atualizar(Conta conta) throws SQLException {
        String sql = """
            UPDATE conta SET
                userId=?, nomeBanco=?, agencia=?, numeroConta=?,
                saldoInicial=?, tipoConta=?
            WHERE id=?
        """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, conta.getUserId());
            st.setString(2, conta.getNomeBanco());
            st.setString(3, conta.getAgencia());
            st.setInt(4, conta.getNumeroConta());
            st.setDouble(5, conta.getSaldoInicial());
            st.setString(6, conta.getTipoConta());
            st.setInt(7, conta.getId());

            return st.executeUpdate();
        }
    }

    @Override
    public int excluir(Integer id) throws SQLException {
        String sql = "DELETE FROM conta WHERE id=?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate();
        }
    }

    @Override
    public Conta buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM conta WHERE id=?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Conta buscarPorNumero(Integer numeroConta) throws SQLException {
        String sql = "SELECT * FROM conta WHERE numeroConta=?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, numeroConta);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Conta buscarPorNumeroEUserId(Integer numeroConta, Integer userId) throws SQLException {
        String sql = "SELECT * FROM conta WHERE numeroConta=? AND userId=?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, numeroConta);
            st.setInt(2, userId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Conta> buscarTodos() throws SQLException {
        String sql = """
            SELECT * FROM conta
            ORDER BY nomeBanco, agencia, numeroConta
        """;

        List<Conta> lista = new ArrayList<>();

        try (PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }

    public List<Conta> buscarPorUserId(Integer userId) throws SQLException {
        String sql = """
            SELECT * FROM conta
            WHERE userId = ?
            ORDER BY nomeBanco, agencia, numeroConta
        """;

        List<Conta> lista = new ArrayList<>();

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, userId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        return lista;
    }
}

