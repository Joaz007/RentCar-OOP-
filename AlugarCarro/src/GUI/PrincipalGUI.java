package GUI;

import Modelo.*;
import Calculos.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class PrincipalGUI extends JFrame {
    private List<Cliente> clientes = new ArrayList<>();
    private List<Carro> carros = new ArrayList<>();

    public PrincipalGUI() {
        configurarJanela();
        inicializarDados();
        adicionarAbas();
        exibirJanela();
    }

    private void configurarJanela() {
        setTitle("Sistema de Locação de Carros");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarDados() {
        carros.add(new Carro(1, "Fiat Uno", "AAA-1111", 2020, 100));
        carros.add(new Carro(2, "VW Gol", "BBB-2222", 2021, 150));
        carros.add(new Carro(3, "Chevrolet Onix", "CCC-3333", 2022, 200));
    }

    private void adicionarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Operações", new OperacoesPanel(clientes, carros));
        abas.addTab("Clientes", new ClientesPanel(clientes));
        abas.addTab("Carros", new CarrosPanel(carros));
        abas.addTab("Controle", new FuncionarioPanel(clientes));
        add(abas);
    }

    private void exibirJanela() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrincipalGUI::new);
    }

    private static class OperacoesPanel extends JPanel {
        public OperacoesPanel(List<Cliente> clientes, List<Carro> carros) {
            setLayout(new GridLayout(2, 1, 10, 10));
            JButton btnLocacao = new JButton("Nova Locação");
            JButton btnDevolucao = new JButton("Devolução de Veículo");
            
            btnLocacao.addActionListener(e -> new LocacaoPanel(clientes, carros));
            btnDevolucao.addActionListener(e -> new DevolucaoDialog(clientes));
            
            add(btnLocacao);
            add(btnDevolucao);
        }
    }

    private static class DevolucaoDialog extends JDialog {
        public DevolucaoDialog(List<Cliente> clientes) {
            setTitle("Devolução de Veículo");
            setSize(500, 400);
            setModal(true);
            
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Cliente", "Carro", "Placa"}, 0);
            JTable tabela = new JTable(model);
            
            for (Cliente c : clientes) {
                for (Locacao l : c.consultarHistorico()) {
                    if (!l.isDevolvido()) {
                        model.addRow(new Object[]{l.getId(), c.getNome(), l.getCarro().getModelo(), l.getCarro().getPlaca()});
                    }
                }
            }
            
            JButton btnDevolver = new JButton("Confirmar Devolução");
            btnDevolver.addActionListener(e -> {
                int linha = tabela.getSelectedRow();
                if (linha != -1) {
                    int idLocacao = (int) model.getValueAt(linha, 0);
                    for (Cliente c : clientes) {
                        for (Locacao l : c.consultarHistorico()) {
                            if (l.getId() == idLocacao) {
                                l.devolverCarro();
                                JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso!");
                                dispose();
                                return;
                            }
                        }
                    }
                }
            });
            
            JPanel content = new JPanel(new BorderLayout());
            content.add(new JScrollPane(tabela), BorderLayout.CENTER);
            content.add(btnDevolver, BorderLayout.SOUTH);
            add(content);
            setVisible(true);
        }
    }

    private static class FuncionarioPanel extends JPanel {
        public FuncionarioPanel(List<Cliente> clientes) {
            setLayout(new BorderLayout());
            String[] colunas = {"Cliente", "Carro", "Placa", "Data Locação", "Data Devolução", "Status"};
            DefaultTableModel model = new DefaultTableModel(colunas, 0);
            JTable tabela = new JTable(model);
            
            JButton btnAtualizar = new JButton("Atualizar");
            btnAtualizar.addActionListener(e -> atualizarTabela(clientes, model));
            
            JPanel painelBotoes = new JPanel();
            painelBotoes.add(btnAtualizar);
            
            add(new JScrollPane(tabela), BorderLayout.CENTER);
            add(painelBotoes, BorderLayout.SOUTH);
            atualizarTabela(clientes, model);
        }
        
        private void atualizarTabela(List<Cliente> clientes, DefaultTableModel model) {
            model.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Cliente c : clientes) {
                for (Locacao l : c.consultarHistorico()) {
                    model.addRow(new Object[]{
                        c.getNome(),
                        l.getCarro().getModelo(),
                        l.getCarro().getPlaca(),
                        sdf.format(l.getDataInicio()),
                        l.isDevolvido() ? sdf.format(l.getDataDevolucao()) : "Pendente",
                        l.isDevolvido() ? "Devolvido" : "Em aberto"
                    });
                }
            }
        }
    }

    private static class ClientesPanel extends JPanel {
        private DefaultTableModel model;
        
        public ClientesPanel(List<Cliente> clientes) {
            setLayout(new BorderLayout());
            JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField txtNome = new JTextField();
            JTextField txtCpf = new JTextField();
            JTextField txtEmail = new JTextField();
            
            formulario.add(new JLabel("Nome:"));
            formulario.add(txtNome);
            formulario.add(new JLabel("CPF:"));
            formulario.add(txtCpf);
            formulario.add(new JLabel("Email:"));
            formulario.add(txtEmail);
            
            JButton btnAdicionar = new JButton("Cadastrar Cliente");
            formulario.add(btnAdicionar);
            
            model = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Email"}, 0);
            JTable tabela = new JTable(model);
            
            btnAdicionar.addActionListener(e -> {
                if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty() || txtEmail.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                    return;
                }
                Cliente novo = new Cliente(clientes.size() + 1, txtNome.getText(), txtCpf.getText(), txtEmail.getText());
                clientes.add(novo);
                model.addRow(new Object[]{novo.getId(), novo.getNome(), novo.getCpf(), novo.getEmail()});
                txtNome.setText("");
                txtCpf.setText("");
                txtEmail.setText("");
            });
            
            add(formulario, BorderLayout.NORTH);
            add(new JScrollPane(tabela), BorderLayout.CENTER);
        }
    }

    private static class CarrosPanel extends JPanel {
        public CarrosPanel(List<Carro> carros) {
            setLayout(new BorderLayout());
            JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));
            JTextField txtModelo = new JTextField();
            JTextField txtPlaca = new JTextField();
            JTextField txtAno = new JTextField();
            JTextField txtDiaria = new JTextField();
            
            formulario.add(new JLabel("Modelo:"));
            formulario.add(txtModelo);
            formulario.add(new JLabel("Placa:"));
            formulario.add(txtPlaca);
            formulario.add(new JLabel("Ano:"));
            formulario.add(txtAno);
            formulario.add(new JLabel("Diária:"));
            formulario.add(txtDiaria);
            
            JButton btnAdicionar = new JButton("Adicionar Carro");
            formulario.add(btnAdicionar);
            
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Modelo", "Placa", "Ano", "Diária", "Disponível"}, 0);
            JTable tabela = new JTable(model);
            
            btnAdicionar.addActionListener(e -> {
                try {
                    Carro novo = new Carro(
                        carros.size() + 1,
                        txtModelo.getText(),
                        txtPlaca.getText(),
                        Integer.parseInt(txtAno.getText()),
                        Double.parseDouble(txtDiaria.getText())
                    );
                    carros.add(novo);
                    model.addRow(new Object[]{
                        novo.getId(),
                        novo.getModelo(),
                        novo.getPlaca(),
                        novo.getAno(),
                        "R$ " + novo.getValorDiaria(),
                        novo.isDisponivel() ? "Sim" : "Não"
                    });
                    txtModelo.setText("");
                    txtPlaca.setText("");
                    txtAno.setText("");
                    txtDiaria.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valores inválidos!");
                }
            });
            
            add(formulario, BorderLayout.NORTH);
            add(new JScrollPane(tabela), BorderLayout.CENTER);
        }
    }

    private static class LocacaoPanel extends JDialog {
        private static int proximoId = 1;
        
        public LocacaoPanel(List<Cliente> clientes, List<Carro> carros) {
            setTitle("Nova Locação");
            setSize(800, 600);
            setModal(true);
            
            CardLayout cardLayout = new CardLayout();
            JPanel cards = new JPanel(cardLayout);
            Cliente[] currentCliente = new Cliente[1];
            Carro[] selectedCarro = new Carro[1];
            
            cards.add(criarPanelCliente(clientes, cards, currentCliente), "cliente");
            cards.add(criarPanelCarro(carros, cards, selectedCarro), "carro");
            cards.add(criarPanelDatas(cards, currentCliente, selectedCarro), "datas");
            
            cardLayout.show(cards, "cliente");
            add(cards);
            setVisible(true);
        }
        
        private JPanel criarPanelCliente(List<Cliente> clientes, JPanel cards, Cliente[] currentCliente) {
            JPanel panel = new JPanel(new BorderLayout());
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nome", "CPF"}, 0);
            
            for (Cliente c : clientes) {
                model.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf()});
            }
            
            JTable tabela = new JTable(model);
            JButton btnNovo = new JButton("Novo Cliente");
            JButton btnProximo = new JButton("Selecionar e Prosseguir");
            
            btnNovo.addActionListener(e -> new NovoClienteDialog(clientes, model));
            btnProximo.addActionListener(e -> {
                int linha = tabela.getSelectedRow();
                if (linha != -1) {
                    currentCliente[0] = clientes.get(linha);
                    ((CardLayout) cards.getLayout()).show(cards, "carro");
                }
            });
            
            JPanel botoes = new JPanel();
            botoes.add(btnNovo);
            botoes.add(btnProximo);
            
            panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
            panel.add(botoes, BorderLayout.SOUTH);
            return panel;
        }
        
        private JPanel criarPanelCarro(List<Carro> carros, JPanel cards, Carro[] selectedCarro) {
            JPanel panel = new JPanel(new BorderLayout());
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Modelo", "Placa", "Diária", "Disponível"}, 0);
            
            for (Carro c : carros) {
                model.addRow(new Object[]{c.getId(), c.getModelo(), c.getPlaca(), "R$ " + c.getValorDiaria(), c.isDisponivel() ? "Sim" : "Não"});
            }
            
            JTable tabela = new JTable(model);
            JButton btnVoltar = new JButton("Voltar");
            JButton btnProximo = new JButton("Selecionar e Prosseguir");
            
            btnVoltar.addActionListener(e -> ((CardLayout) cards.getLayout()).show(cards, "cliente"));
            btnProximo.addActionListener(e -> {
                int linha = tabela.getSelectedRow();
                if (linha != -1) {
                    selectedCarro[0] = carros.get(linha);
                    if (selectedCarro[0].isDisponivel()) {
                        ((CardLayout) cards.getLayout()).show(cards, "datas");
                    } else {
                        JOptionPane.showMessageDialog(LocacaoPanel.this, "Carro indisponível!");
                    }
                }
            });
            
            JPanel botoes = new JPanel();
            botoes.add(btnVoltar);
            botoes.add(btnProximo);
            
            panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
            panel.add(botoes, BorderLayout.SOUTH);
            return panel;
        }
        
        private JPanel criarPanelDatas(JPanel cards, Cliente[] currentCliente, Carro[] selectedCarro) {
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            JTextField txtInicio = new JTextField("dd/mm/aaaa");
            JTextField txtFim = new JTextField("dd/mm/aaaa");
            JButton btnVoltar = new JButton("Voltar");
            JButton btnFinalizar = new JButton("Finalizar Locação");
            
            btnVoltar.addActionListener(e -> ((CardLayout) cards.getLayout()).show(cards, "carro"));
            btnFinalizar.addActionListener(e -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date inicio = sdf.parse(txtInicio.getText());
                    Date fim = sdf.parse(txtFim.getText());
                    
                    if (fim.before(inicio)) {
                        JOptionPane.showMessageDialog(LocacaoPanel.this, "Data final deve ser após a data inicial!");
                        return;
                    }
                    
                    Locacao locacao = new Locacao(
                        proximoId++,
                        inicio,
                        fim,
                        currentCliente[0],
                        selectedCarro[0],
                        new CalculoNormal()
                    );
                    
                    locacao.calcularValorTotal();
                    currentCliente[0].realizarLocacao(locacao);
                    selectedCarro[0].atualizarStatus(false);
                    
                    JOptionPane.showMessageDialog(LocacaoPanel.this, 
                        "Locação realizada com sucesso!\nValor Total: R$ " + locacao.getValorTotal());
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LocacaoPanel.this, "Erro: " + ex.getMessage());
                }
            });
            
            panel.add(new JLabel("Data Início:"));
            panel.add(txtInicio);
            panel.add(new JLabel("Data Fim:"));
            panel.add(txtFim);
            panel.add(btnVoltar);
            panel.add(btnFinalizar);
            return panel;
        }
    }

    private static class NovoClienteDialog extends JDialog {
        public NovoClienteDialog(List<Cliente> clientes, DefaultTableModel model) {
            setTitle("Novo Cliente");
            setSize(300, 200);
            setModal(true);
            
            JPanel panel = new JPanel(new GridLayout(4, 2));
            JTextField txtNome = new JTextField();
            JTextField txtCpf = new JTextField();
            JTextField txtEmail = new JTextField();
            
            panel.add(new JLabel("Nome:"));
            panel.add(txtNome);
            panel.add(new JLabel("CPF:"));
            panel.add(txtCpf);
            panel.add(new JLabel("Email:"));
            panel.add(txtEmail);
            
            JButton btnSalvar = new JButton("Salvar");
            btnSalvar.addActionListener(e -> {
                Cliente novo = new Cliente(
                    clientes.size() + 1,
                    txtNome.getText(),
                    txtCpf.getText(),
                    txtEmail.getText()
                );
                clientes.add(novo);
                model.addRow(new Object[]{novo.getId(), novo.getNome(), novo.getCpf()});
                dispose();
            });
            
            panel.add(btnSalvar);
            add(panel);
            setVisible(true);
        }
    }
}