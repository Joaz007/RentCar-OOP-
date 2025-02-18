package GUI;

import Modelo.*;
import Calculos.*;
import Exceptions.LocacaoException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrincipalGUI extends JFrame {

    public PrincipalGUI() {
        configurarJanela();
        adicionarAbas();
        exibirJanela();
    }

    private void configurarJanela() {
        setTitle("Sistema de Locação de Carros");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void adicionarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Nova Locação", new LocacaoPanel());
        abas.addTab("Clientes", new ClientesPanel());
        abas.addTab("Carros", new CarrosPanel());
        add(abas);
    }

    private void exibirJanela() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrincipalGUI::new);
    }

    // -----------------------------
    // LocacaoPanel: Fluxo de Aluguel
    // -----------------------------
    private static class LocacaoPanel extends JPanel {
        private CardLayout cardLayout = new CardLayout();
        private JPanel cards = new JPanel(cardLayout);

        // Variáveis para armazenar dados durante o fluxo
        private Cliente currentCliente;
        private Carro selectedCarro;
        private List<Cliente> clientes = new ArrayList<>();
        private List<Carro> carros = new ArrayList<>();
        private JTextField txtDataInicio, txtDataFim;

        public LocacaoPanel() {
            // Inicializa três carros fictícios
            carros.add(new Carro(1, "Carro 1", "AAA-1111", 2020, 100));
            carros.add(new Carro(2, "Carro 2", "BBB-2222", 2021, 150));
            carros.add(new Carro(3, "Carro 3", "CCC-3333", 2022, 200));

            setLayout(new BorderLayout());
            add(cards, BorderLayout.CENTER);
            criarPassos();
            cardLayout.show(cards, "entrar");
        }

        private void criarPassos() {
            // --- Passo 1: Tela de "Entrar" ---
            JPanel panelEntrar = new JPanel(new BorderLayout());
            JButton btnEntrar = new JButton("Entrar");
            btnEntrar.addActionListener(e -> cardLayout.show(cards, "clienteChoice"));
            panelEntrar.add(new JLabel("Clique em Entrar para iniciar o aluguel", SwingConstants.CENTER), BorderLayout.CENTER);
            panelEntrar.add(btnEntrar, BorderLayout.SOUTH);
            cards.add(panelEntrar, "entrar");

            // --- Passo 2: Escolha entre Cliente Existente ou Novo ---
            JPanel panelClienteChoice = new JPanel(new GridLayout(3, 1, 10, 10));
            JButton btnClienteExistente = new JButton("Cliente Existente");
            JButton btnNovoCliente = new JButton("Novo Cliente");
            panelClienteChoice.add(new JLabel("Selecione Cliente Existente ou Novo Cliente", SwingConstants.CENTER));
            panelClienteChoice.add(btnClienteExistente);
            panelClienteChoice.add(btnNovoCliente);
            btnNovoCliente.addActionListener(e -> cardLayout.show(cards, "novoCliente"));
            btnClienteExistente.addActionListener(e -> cardLayout.show(cards, "clienteExistente"));
            cards.add(panelClienteChoice, "clienteChoice");

            // --- Passo 3a: Cadastro de Novo Cliente ---
            JPanel panelNovoCliente = new JPanel(new GridLayout(5, 2, 5, 5));
            JTextField txtNome = new JTextField();
            JTextField txtCpf = new JTextField();
            JTextField txtEmail = new JTextField();
            panelNovoCliente.add(new JLabel("Nome:"));
            panelNovoCliente.add(txtNome);
            panelNovoCliente.add(new JLabel("CPF:"));
            panelNovoCliente.add(txtCpf);
            panelNovoCliente.add(new JLabel("Email:"));
            panelNovoCliente.add(txtEmail);
            JButton btnCadastrar = new JButton("Cadastrar");
            JButton btnVoltarNovo = new JButton("Voltar");
            panelNovoCliente.add(btnCadastrar);
            panelNovoCliente.add(btnVoltarNovo);
            btnCadastrar.addActionListener(e -> {
                if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty() || txtEmail.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                    return;
                }
                currentCliente = new Cliente(clientes.size() + 1, txtNome.getText(), txtCpf.getText(), txtEmail.getText());
                clientes.add(currentCliente);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
                cardLayout.show(cards, "selecionarCarro");
            });
            btnVoltarNovo.addActionListener(e -> cardLayout.show(cards, "clienteChoice"));
            cards.add(panelNovoCliente, "novoCliente");

            // --- Passo 3b: Buscar Cliente Existente pelo CPF ---
            JPanel panelClienteExistente = new JPanel(new BorderLayout());
            JPanel panelBusca = new JPanel(new FlowLayout());
            JTextField txtBuscaCpf = new JTextField(15);
            JButton btnBuscar = new JButton("Buscar");
            panelBusca.add(new JLabel("Digite o CPF:"));
            panelBusca.add(txtBuscaCpf);
            panelBusca.add(btnBuscar);
            panelClienteExistente.add(panelBusca, BorderLayout.CENTER);
            JButton btnVoltarExistente = new JButton("Voltar");
            panelClienteExistente.add(btnVoltarExistente, BorderLayout.SOUTH);
            btnVoltarExistente.addActionListener(e -> cardLayout.show(cards, "clienteChoice"));
            btnBuscar.addActionListener(e -> {
                String cpfBusca = txtBuscaCpf.getText();
                currentCliente = null;
                for (Cliente c : clientes) {
                    if (c.getCpf().equals(cpfBusca)) {
                        currentCliente = c;
                        break;
                    }
                }
                if (currentCliente == null) {
                    JOptionPane.showMessageDialog(this, "Cliente não encontrado! Por favor, cadastre um novo cliente.");
                    cardLayout.show(cards, "clienteChoice");
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente encontrado: " + currentCliente.getNome());
                    cardLayout.show(cards, "selecionarCarro");
                }
            });
            cards.add(panelClienteExistente, "clienteExistente");

            // --- Passo 4: Seleção do Carro ---
            JPanel panelSelecionarCarro = new JPanel(new BorderLayout());
            JPanel panelCarros = new JPanel(new GridLayout(1, 3, 10, 10));
            for (Carro carro : carros) {
                JButton btnCarro = new JButton("<html>" + carro.getModelo() + "<br>R$" + carro.getValorDiaria() + "</html>");
                btnCarro.addActionListener(e -> {
                    selectedCarro = carro;
                    if (!carro.verificarDisponibilidade()) {
                        JOptionPane.showMessageDialog(this, "Carro não disponível. Selecione outro.");
                    } else {
                        cardLayout.show(cards, "definirDatas");
                    }
                });
                panelCarros.add(btnCarro);
            }
            JButton btnCancelarCarro = new JButton("Cancelar");
            btnCancelarCarro.addActionListener(e -> cardLayout.show(cards, "clienteChoice"));
            panelSelecionarCarro.add(new JLabel("Selecione o Carro desejado:", SwingConstants.CENTER), BorderLayout.NORTH);
            panelSelecionarCarro.add(panelCarros, BorderLayout.CENTER);
            panelSelecionarCarro.add(btnCancelarCarro, BorderLayout.SOUTH);
            cards.add(panelSelecionarCarro, "selecionarCarro");

            // --- Passo 5: Definir Datas da Locação ---
            JPanel panelDefinirDatas = new JPanel(new GridLayout(4, 2, 5, 5));
            txtDataInicio = new JTextField("dd/mm/yyyy");
            txtDataFim = new JTextField("dd/mm/yyyy");
            panelDefinirDatas.add(new JLabel("Data de Início:"));
            panelDefinirDatas.add(txtDataInicio);
            panelDefinirDatas.add(new JLabel("Data de Fim:"));
            panelDefinirDatas.add(txtDataFim);
            JButton btnAvancarData = new JButton("Avançar para Pagamento");
            JButton btnVoltarDatas = new JButton("Voltar");
            panelDefinirDatas.add(btnAvancarData);
            panelDefinirDatas.add(btnVoltarDatas);
            btnAvancarData.addActionListener(e -> {
                try {
                    // Conversão simples do formato dd/mm/yyyy para Date
                    String[] inicio = txtDataInicio.getText().split("/");
                    String[] fim = txtDataFim.getText().split("/");
                    int di = Integer.parseInt(inicio[0]);
                    int mi = Integer.parseInt(inicio[1]);
                    int ai = Integer.parseInt(inicio[2]);
                    int df = Integer.parseInt(fim[0]);
                    int mf = Integer.parseInt(fim[1]);
                    int af = Integer.parseInt(fim[2]);
                    java.util.Calendar calInicio = java.util.Calendar.getInstance();
                    calInicio.set(ai, mi - 1, di);
                    java.util.Calendar calFim = java.util.Calendar.getInstance();
                    calFim.set(af, mf - 1, df);
                    if (calFim.getTime().before(calInicio.getTime())) {
                        JOptionPane.showMessageDialog(this, "Data de fim não pode ser antes da data de início.");
                        return;
                    }
                    // Cria a locação utilizando a estratégia CalculoNormal
                    Locacao locacao = new Locacao(1, calInicio.getTime(), calFim.getTime(), currentCliente, selectedCarro, new CalculoNormal());
                    locacao.calcularValorTotal();
                    // Exibe o resumo e solicita confirmação
                    String resumo = "Cliente: " + currentCliente.getNome() +
                            "\nCarro: " + selectedCarro.getModelo() +
                            "\nData: " + txtDataInicio.getText() + " até " + txtDataFim.getText() +
                            "\nValor Total: R$" + locacao.getValorTotal();
                    int confirm = JOptionPane.showConfirmDialog(this, resumo + "\n\nConfirmar aluguel?", "Pagamento", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        locacao.finalizarLocacao();
                        try {
                            currentCliente.realizarLocacao(locacao);
                        } catch (LocacaoException ex) {
                            JOptionPane.showMessageDialog(this, "Erro na locação: " + ex.getMessage());
                            return;
                        }
                        JOptionPane.showMessageDialog(this, "Pagamento Realizado!\nAluguel cadastrado com sucesso!");
                        cardLayout.show(cards, "entrar");
                    } else {
                        JOptionPane.showMessageDialog(this, "Aluguel cancelado.");
                        cardLayout.show(cards, "clienteChoice");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao processar datas. Verifique o formato dd/mm/yyyy.");
                }
            });
            btnVoltarDatas.addActionListener(e -> cardLayout.show(cards, "selecionarCarro"));
            cards.add(panelDefinirDatas, "definirDatas");
        }
    }

    // -----------------------------
    // ClientesPanel (para futuros aprimoramentos)
    // -----------------------------
    private static class ClientesPanel extends JPanel {
        public ClientesPanel() {
            setLayout(new BorderLayout());
            add(new JLabel("Gerenciamento de Clientes", SwingConstants.CENTER));
        }
    }

    // -----------------------------
    // CarrosPanel (para futuros aprimoramentos)
    // -----------------------------
    private static class CarrosPanel extends JPanel {
        public CarrosPanel() {
            setLayout(new BorderLayout());
            add(new JLabel("Gerenciamento de Carros", SwingConstants.CENTER));
        }
    }
}
