package Modelo;

import Exceptions.LocacaoException;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private List<Locacao> locacoes = new ArrayList<>();

    public Cliente(int id, String nome, String cpf, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    public void realizarLocacao(Locacao locacao) throws LocacaoException {
        if (!locacao.getCarro().verificarDisponibilidade()) {
            throw new LocacaoException("Carro não disponível para locação");
        }
        locacao.getCarro().atualizarStatus(false);
        locacoes.add(locacao);
    }

    public List<Locacao> consultarHistorico() {
        return new ArrayList<>(locacoes);
    }


    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
}