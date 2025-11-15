package br.edu.utfpr.oo2.FinanSystem.service;

import java.io.IOException;
import java.sql.SQLException;
// TODO: Adicionar imports quando implementar relacionamento com Conta
// import java.util.List;
// import br.edu.utfpr.oo2.FinanSystem.entities.Conta;
// import br.edu.utfpr.oo2.FinanSystem.dao.ContaDAO;
// import br.edu.utfpr.oo2.FinanSystem.dao.BancoDados;
// import java.sql.Connection;

import br.edu.utfpr.oo2.FinanSystem.dao.UsuarioDAO;
import br.edu.utfpr.oo2.FinanSystem.entities.Usuario;

public class UsuarioService {
	
	private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void cadastrar(Usuario usuario) throws Exception {
        try {
            Usuario existente = usuarioDAO.buscarPorNomeUsuario(usuario.getNomeUsuario());
            if (existente != null) {
            	
                throw new Exception("Nome de usuário já está em uso!");
            }
            
            usuarioDAO.inserir(usuario);

        } catch (SQLException | IOException e) {
            throw new Exception("Erro ao cadastrar usuário: " + e.getMessage(), e);
        }
    }

    public void atualizar(Usuario usuario) throws Exception {
        try {
            if (usuario.getId() == null) {
                throw new Exception("ID do usuário não pode ser nulo para atualização!");
            }
            
            Usuario existente = usuarioDAO.buscarPorNomeUsuario(usuario.getNomeUsuario());
            if (existente != null && !existente.getId().equals(usuario.getId())) {
                throw new Exception("Nome de usuário já está em uso!");
            }
            usuarioDAO.atualizar(usuario);

        } catch (SQLException | IOException e) {
            throw new Exception("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public void excluir(Integer id) throws Exception {
        try {
            // TODO: IMPLEMENTAR RELACIONAMENTO 1:N COM CONTA
            // Antes de excluir o usuário, verificar se ele possui contas cadastradas
            // Se possuir, decidir a estratégia:
            // 
            // OPÇÃO 1: Não permitir exclusão se houver contas (mais seguro)
            // Usuario usuario = usuarioDAO.buscarPorId(id);
            // if (usuario != null && usuario.getContas() != null && !usuario.getContas().isEmpty()) {
            //     throw new Exception("Não é possível excluir usuário que possui contas cadastradas. " +
            //                        "Exclua as contas primeiro ou transfira-as para outro usuário.");
            // }
            // 
            // OPÇÃO 2: Excluir em cascata (excluir todas as contas do usuário também)
            // Usuario usuario = usuarioDAO.buscarPorId(id);
            // if (usuario != null && usuario.getContas() != null) {
            //     ContaDAO contaDAO = new ContaDAO(BancoDados.conectar());
            //     for (Conta conta : usuario.getContas()) {
            //         contaDAO.excluir(conta.getId());
            //     }
            // }
            // 
            // OPÇÃO 3: Apenas remover a referência (setar userId como null nas contas)
            // Usuario usuario = usuarioDAO.buscarPorId(id);
            // if (usuario != null && usuario.getContas() != null) {
            //     ContaDAO contaDAO = new ContaDAO(BancoDados.conectar());
            //     for (Conta conta : usuario.getContas()) {
            //         conta.setUserId(0); // ou null, dependendo do tipo do campo
            //         contaDAO.atualizar(conta);
            //     }
            // }
            
            usuarioDAO.excluir(id);
        } catch (SQLException | IOException e) {
            throw new Exception("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    public Usuario login(String nomeUsuario, String senha) throws Exception {
        try {
            Usuario usuario = usuarioDAO.autenticar(nomeUsuario, senha);

            if (usuario == null) {
                throw new Exception("Usuário ou senha inválidos.");
            }
            return usuario;

        } catch (SQLException | IOException e) {
            throw new Exception("Erro ao autenticar usuário: " + e.getMessage(), e);
        }
    }
    
    // TODO: IMPLEMENTAR RELACIONAMENTO 1:N COM CONTA
    // Adicionar método para buscar todas as contas de um usuário:
    // 
    // public List<Conta> buscarContasPorUsuario(Integer usuarioId) throws Exception {
    //     try {
    //         Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
    //         if (usuario == null) {
    //             throw new Exception("Usuário não encontrado!");
    //         }
    //         
    //         // As contas já devem estar carregadas no objeto usuario se o DAO foi implementado corretamente
    //         // Caso contrário, buscar diretamente:
    //         // Connection conn = BancoDados.conectar();
    //         // ContaDAO contaDAO = new ContaDAO(conn);
    //         // List<Conta> todasContas = contaDAO.buscarTodos();
    //         // List<Conta> contasDoUsuario = new ArrayList<>();
    //         // for (Conta conta : todasContas) {
    //         //     if (conta.getUserId() == usuarioId) {
    //         //         contasDoUsuario.add(conta);
    //         //     }
    //         // }
    //         // return contasDoUsuario;
    //         
    //         return usuario.getContas();
    //         
    //     } catch (SQLException | IOException e) {
    //         throw new Exception("Erro ao buscar contas do usuário: " + e.getMessage(), e);
    //     }
    // }
    // 
    // Adicionar método para buscar usuário com suas contas carregadas:
    // 
    // public Usuario buscarUsuarioComContas(Integer usuarioId) throws Exception {
    //     try {
    //         Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
    //         if (usuario == null) {
    //             throw new Exception("Usuário não encontrado!");
    //         }
    //         
    //         // As contas devem ser carregadas automaticamente pelo DAO no método mapearUsuario
    //         // Se não estiverem, carregar manualmente:
    //         // List<Conta> contas = usuarioDAO.buscarContasPorUsuarioId(usuarioId);
    //         // usuario.setContas(contas);
    //         
    //         return usuario;
    //         
    //     } catch (SQLException | IOException e) {
    //         throw new Exception("Erro ao buscar usuário: " + e.getMessage(), e);
    //     }
    // }
}
