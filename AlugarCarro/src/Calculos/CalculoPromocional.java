package Calculos;

public class CalculoPromocional implements CalculoDiaria {
    @Override
    public double calcularValor(double valorDiaria, long dias) {
        return (valorDiaria * dias) * 0.9; // 10% de desconto
    }
}