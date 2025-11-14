package br.edu.utfpr.oo2.FinanSystem.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.oo2.FinanSystem.dao.BancoDados;
import br.edu.utfpr.oo2.FinanSystem.dao.CategoriaDAO;
import br.edu.utfpr.oo2.FinanSystem.entities.Categoria;

public class CategoriaService {

    public void cadastrarCategoria(Categoria categoria) throws SQLException, IOException, Exception {
        Connection conn = BancoDados.conectar();
        CategoriaDAO categoriaDAO = new CategoriaDAO(conn);

        Categoria existente = categoriaDAO.buscarPorNome(categoria.getNome());
        if (existente != null) {
            throw new Exception("Categoria já cadastrada com esse nome.");
        }

        categoriaDAO.cadastrar(categoria);
    }

    public void atualizarCategoria(Categoria categoria) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        CategoriaDAO categoriaDAO = new CategoriaDAO(conn);
        categoriaDAO.atualizar(categoria);
    }

    public void excluirCategoria(Integer id) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        CategoriaDAO categoriaDAO = new CategoriaDAO(conn);
        categoriaDAO.excluir(id);
    }

    public Categoria buscarPorId(Integer id) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        CategoriaDAO categoriaDAO = new CategoriaDAO(conn);
        return categoriaDAO.buscarPorId(id);
    }

    public List<Categoria> listarCategorias() throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        CategoriaDAO categoriaDAO = new CategoriaDAO(conn);
        return categoriaDAO.buscarTodos();
    }
}
