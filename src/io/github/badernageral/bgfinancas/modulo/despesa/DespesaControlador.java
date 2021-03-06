/*
Copyright 2012-2018 Jose Robson Mariano Alves

This file is part of bgfinancas.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

package io.github.badernageral.bgfinancas.modulo.despesa;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Tabela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Operacao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Posicao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import io.github.badernageral.bgfinancas.modelo.DespesaCategoria;
import io.github.badernageral.bgfinancas.template.cena.CenaMovimento;

public final class DespesaControlador implements Initializable, Controlador {
       
    private final String TITULO = idioma.getMensagem("despesas");
    @FXML private CenaMovimento cenaController;
    private ObservableList<Despesa> itens;
    private final Tabela<Despesa> tabela = new Tabela<>();
    private final TableView<Despesa> tabelaLista = new TableView<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        cenaController.setTitulo(TITULO);
        cenaController.setTabela(tabelaLista);
        tabela.prepararTabela(tabelaLista);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("categoria"), "nomeCategoria");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("item"), "nomeItem");
        tabela.adicionarColunaNumero(tabelaLista, idioma.getMensagem("quantidade"), "quantidade");
        tabela.adicionarColunaNumero(tabelaLista, idioma.getMensagem("valor"), "valor");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("conta"), "nomeConta");
        tabela.adicionarColunaDataHora(tabelaLista, idioma.getMensagem("data"), "dataHora");
        new DespesaCategoria().montarSelectCategoria(cenaController.getChoiceCategoria());
        acaoFiltrar(false);
        cenaController.adicionarNovoBotaoCadastrar();
    }
    
    @Override
    public void acaoFiltrar(Boolean animacao){
        String id_categoria = null;
        Categoria despesaCategoria = cenaController.getCategoriaSelecionada();
        if(!despesaCategoria.getIdCategoria().equals("todas")) {
            id_categoria = despesaCategoria.getIdCategoria();
        }
        tabelaLista.setItems(new Despesa().setFiltro(cenaController.getFiltro().getText()).setIdCategoria(id_categoria).listar());
        if(animacao){
            Animacao.fadeOutIn(tabelaLista);
        }
        cenaController.atualizarTabelaConta(animacao);
    }
    
    @Override
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, TITULO);
            Controlador.cadastrar(null);
        }else if(botao==2){
            Kernel.principal.acaoDespesaCadastroMultiplo();
        }
    }
    
    @Override
    public void acaoAlterar(int tabela) {
        if(tabela==1){
            itens = tabelaLista.getSelectionModel().getSelectedItems();
            if(Validar.alteracao(itens, cenaController.getBotaoAlterar())){
                DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, TITULO);
                Controlador.alterar(itens.get(0));
            }
        }else if(tabela==2){
            cenaController.alterarConta(TITULO);
        }
    }
    
    @Override
    public void acaoExcluir(int botao) {
        itens = tabelaLista.getSelectionModel().getSelectedItems();
        if(Validar.exclusao(itens,cenaController.getBotaoExcluir())){
            itens.forEach((Despesa d) -> {
                    d.excluir();
                    new Conta().alterarSaldo(Operacao.INCREMENTAR, d.getIdConta(), d.getValor().toString());
            });
            Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
            acaoFiltrar(true);
        }
    }
    
    @Override
    public void acaoGerenciar(int botao) {
        if(botao==1){
            Kernel.principal.acaoDespesaCategoria();
        }else if(botao==2){
            Kernel.principal.acaoDespesaItem();
        }
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(cenaController,cenaController.getBotaoCadastrar2(),tabelaLista);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_desp_1"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoCadastrar(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_cadastrar"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoCadastrar2(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_cadastrar_multiplo"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoAlterar(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_alterar"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoExcluir(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_excluir"));
        Ajuda.getInstance().capitulo(cenaController.getChoiceCategoria(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_desp_2"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoGerenciarCategoria(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_categoria"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoGerenciarItem(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_item"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}