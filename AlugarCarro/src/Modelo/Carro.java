package Modelo;

public class Carro {
    private int id;
    private String modelo;
    private String placa;
    private int ano;
    private double valorDiaria;
    private boolean disponivel;

    public Carro(int id, String modelo, String placa, int ano, double valorDiaria) {
        this.id = id;
        this.modelo = modelo;
        this.placa = placa;
        this.ano = ano;
        this.valorDiaria = valorDiaria;
        this.disponivel = true;
    }

    public boolean verificarDisponibilidade() {
        return disponivel;
    }

    public void atualizarStatus(boolean disponivel) {
        this.disponivel = disponivel;
    }


    public int getId() { return id; }
    public String getModelo() { return modelo; }
    public String getPlaca() { return placa; }
    public int getAno() { return ano; }
    public double getValorDiaria() { return valorDiaria; }
    public boolean isDisponivel() { return disponivel; }
}