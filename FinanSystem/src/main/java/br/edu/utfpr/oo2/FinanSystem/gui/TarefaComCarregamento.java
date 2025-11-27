package br.edu.utfpr.oo2.FinanSystem.gui;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Utilitário para executar tarefas demoradas com janela de carregamento.
 * Trata automaticamente erros e atualização da UI.
 */
public class TarefaComCarregamento {

    /**
     * Executa tarefa SEM retorno, com tratamento automático de erros
     * @param owner Janela pai
     * @param tarefa Código a executar (ex: () -> service.salvar(obj))
     * @param aoSucesso Código executado se der certo (ex: () -> recarregarTabela())
     */
    public static void executar(Frame owner, RunnableComExcecao tarefa, Runnable aoSucesso) {
        executar(owner, tarefa, aoSucesso, null);
    }

    /**
     * Executa tarefa SEM retorno, com callbacks de sucesso e erro
     * @param owner Janela pai
     * @param tarefa Código a executar
     * @param aoSucesso Executado em caso de sucesso (pode ser null)
     * @param aoErro Executado em caso de erro (recebe a exceção, pode ser null)
     */
    public static void executar(Frame owner, RunnableComExcecao tarefa, Runnable aoSucesso, Consumer<Exception> aoErro) {
        JanelaCarregamento dialog = new JanelaCarregamento(owner);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private Exception erro = null;

            @Override
            protected Void doInBackground() {
                try {
                    tarefa.executar();
                } catch (Exception e) {
                    erro = e;
                }
                return null;
            }

            @Override
            protected void done() {
                dialog.fechar();

                if (erro != null) {
                    // Trata erro
                    if (aoErro != null) {
                        aoErro.accept(erro);
                    } else {
                        // Erro padrão
                        JOptionPane.showMessageDialog(owner,
                                "Erro: " + erro.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Sucesso
                    if (aoSucesso != null) {
                        aoSucesso.run();
                    }
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }

    /**
     * Executa tarefa COM retorno
     * @param owner Janela pai
     * @param tarefa Código que retorna algo (ex: () -> service.buscar(id))
     * @param aoSucesso Callback com o resultado (ex: conta -> editarConta(conta))
     */
    public static <T> void executarComRetorno(Frame owner, Callable<T> tarefa, Consumer<T> aoSucesso) {
        executarComRetorno(owner, tarefa, aoSucesso, null);
    }

    /**
     * Executa tarefa COM retorno, com callbacks de sucesso e erro
     * @param owner Janela pai
     * @param tarefa Código que retorna algo
     * @param aoSucesso Callback com o resultado
     * @param aoErro Callback de erro (pode ser null)
     */
    public static <T> void executarComRetorno(Frame owner, Callable<T> tarefa, Consumer<T> aoSucesso, Consumer<Exception> aoErro) {
        JanelaCarregamento dialog = new JanelaCarregamento(owner);

        SwingWorker<T, Void> worker = new SwingWorker<>() {
            private Exception erro = null;

            @Override
            protected T doInBackground() {
                try {
                    return tarefa.call();
                } catch (Exception e) {
                    erro = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                dialog.fechar();

                if (erro != null) {
                    // Trata erro
                    if (aoErro != null) {
                        aoErro.accept(erro);
                    } else {
                        JOptionPane.showMessageDialog(owner,
                                "Erro: " + erro.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Sucesso
                    try {
                        T resultado = get();
                        if (aoSucesso != null && resultado != null) {
                            aoSucesso.accept(resultado);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(owner,
                                "Erro ao processar resultado: " + e.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }

    /**
     * Interface funcional para Runnable que pode lançar exceção
     */
    @FunctionalInterface
    public interface RunnableComExcecao {
        void executar() throws Exception;
    }
}