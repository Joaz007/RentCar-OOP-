package Modelo;

import Calculos.CalculoDiaria;
import java.util.Date;

public class Locacao {
    private int id;
    private Date dataInicio;
    private Date dataFim;
    private double valorTotal;
    private String status;
    private Cliente cliente;
    private Carro carro;
    private CalculoDiaria calculoStrategy;

    public Locacao(int id, Date dataInicio, Date dataFim, Cliente cliente, 
                  Carro carro, CalculoDiaria calculoStrategy) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.cliente = cliente;
        this.carro = carro;
        this.calculoStrategy = calculoStrategy;
        this.status = "Ativa";
    }

    public void calcularValorTotal() {
        long diff = dataFim.getTime() - dataInicio.getTime();
        long dias = diff / (1000 * 60 * 60 * 24);
        valorTotal = calculoStrategy.calcularValor(carro.getValorDiaria(), dias);
    }

    public void finalizarLocacao() {
        status = "Finalizada";
        carro.atualizarStatus(true);
        calcularValorTotal();
    }

    public int getId() { return id; }
    public Date getDataInicio() { return dataInicio; }
    public Date getDataFim() { return dataFim; }
    public double getValorTotal() { return valorTotal; }
    public String getStatus() { return status; }
    public Cliente getCliente() { return cliente; }
    public Carro getCarro() { return carro; }
}