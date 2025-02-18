package Calculos;


public class CalculoNormal implements CalculoDiaria {
    @Override
    public double calcularValor(double valorDiaria, long dias) {
        return valorDiaria * dias;
    }
}