package gui;

import desempenho.Desempenho;
import estatisticas.EstatisticasEstudantes;
import hash.TabelaHash;
import io.LeitorCSV;
import modelo.Estudante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;

/** Interface grafica (Swing) do Sistema de Gestao de Estudantes. */
public class JanelaPrincipal extends JFrame {

    // ---------- paleta ----------
    private static final Color AZUL_ESCURO = new Color(0x1F2A44);
    private static final Color AZUL = new Color(0x2F6FED);
    private static final Color AZUL_CLARO = new Color(0xE8EFFE);
    private static final Color FUNDO = new Color(0xF4F6FA);
    private static final Color BRANCO = Color.WHITE;
    private static final Color CINZA = new Color(0x6B7385);
    private static final Color BORDA = new Color(0xDDE2EC);
    private static final Color VERDE = new Color(0x1E9E63);
    private static final Color VERMELHO = new Color(0xD64545);

    private static final Font F_TEXTO = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font F_NEGRITO = new Font("SansSerif", Font.BOLD, 14);
    private static final Font F_TITULO = new Font("SansSerif", Font.BOLD, 22);
    private static final Font F_MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    // ---------- estado ----------
    private TabelaHash tabela = new TabelaHash(TabelaHash.CAPACIDADE_INICIAL);
    private ArrayList<Estudante> lista = new ArrayList<Estudante>();
    private LeitorCSV leitor = new LeitorCSV();
    private boolean carregado = false;

    // ---------- componentes ----------
    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudo = new JPanel(cartas);
    private final JLabel estado = new JLabel("Nenhum ficheiro carregado.");
    private final java.util.List<JButton> botoesMenu = new ArrayList<JButton>();

    private JTextField campoFicheiro;
    private JLabel[] cartoesCSV;

    private JTextField campoPesquisa;
    private JTextArea resultadoPesquisa;
    private JTextField[] camposInserir;
    private JLabel msgInserir;
    private JTextField campoRemover;
    private JLabel msgRemover;

    private JTextArea textoEstudantes;
    private JLabel[] valoresHash;
    private JProgressBar barraCarga;
    private JTextArea textoDesempenho;
    private JTextArea textoComparacao;

    public JanelaPrincipal() {
        super("Sistema de Gestao de Estudantes - Tabela Hash");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1060, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FUNDO);
        raiz.add(criarCabecalho(), BorderLayout.NORTH);
        raiz.add(criarMenuLateral(), BorderLayout.WEST);

        conteudo.setBackground(FUNDO);
        conteudo.setBorder(new EmptyBorder(20, 24, 20, 24));
        conteudo.add(paginaCarregar(), "carregar");
        conteudo.add(paginaPesquisar(), "pesquisar");
        conteudo.add(paginaInserir(), "inserir");
        conteudo.add(paginaRemover(), "remover");
        conteudo.add(paginaEstudantes(), "estudantes");
        conteudo.add(paginaHash(), "hash");
        conteudo.add(paginaDesempenho(), "desempenho");
        conteudo.add(paginaComparacao(), "comparacao");
        raiz.add(conteudo, BorderLayout.CENTER);

        setContentPane(raiz);
        atualizarHash();
    }

    // =====================================================================
    //  Estrutura geral
    // =====================================================================

    private JComponent criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AZUL_ESCURO);
        p.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel titulo = new JLabel("Sistema de Gestao de Estudantes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(BRANCO);
        JLabel sub = new JLabel("Tabela Hash  |  Double Hashing  |  Estruturas de Dados e Algoritmos");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(0xB8C2DD));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(sub);
        p.add(textos, BorderLayout.WEST);

        estado.setFont(F_NEGRITO);
        estado.setForeground(new Color(0xFFC857));
        p.add(estado, BorderLayout.EAST);
        return p;
    }

    private JComponent criarMenuLateral() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BRANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDA), new EmptyBorder(14, 10, 14, 10)));
        p.setPreferredSize(new Dimension(230, 0));

        String[][] itens = {
            {"Carregar ficheiro CSV", "carregar"},
            {"Pesquisar estudante", "pesquisar"},
            {"Inserir estudante", "inserir"},
            {"Remover estudante", "remover"},
            {"Estatisticas dos estudantes", "estudantes"},
            {"Estatisticas da Hash Table", "hash"},
            {"Teste de desempenho", "desempenho"},
            {"Sequencial vs Hash", "comparacao"},
        };
        for (final String[] it : itens) {
            final JButton b = new JButton(it[0]);
            b.setFont(F_TEXTO);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(true);
            b.setBackground(BRANCO);
            b.setForeground(AZUL_ESCURO);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            b.setBorder(new EmptyBorder(8, 14, 8, 14));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addActionListener(e -> mostrar(it[1], b));
            botoesMenu.add(b);
            p.add(b);
            p.add(Box.createVerticalStrut(4));
        }
        p.add(Box.createVerticalGlue());

        JButton sair = new JButton("Sair");
        sair.setFont(F_TEXTO);
        sair.setForeground(VERMELHO);
        sair.setBackground(BRANCO);
        sair.setFocusPainted(false);
        sair.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        sair.addActionListener(e -> System.exit(0));
        p.add(sair);

        selecionar(botoesMenu.get(0));
        return p;
    }

    private void mostrar(String pagina, JButton botao) {
        // paginas que dependem do CSV
        boolean precisaCSV = pagina.equals("estudantes") || pagina.equals("desempenho")
                || pagina.equals("comparacao");
        if (precisaCSV && !carregado) {
            aviso("Carregue primeiro o ficheiro CSV.");
            return;
        }
        selecionar(botao);
        if (pagina.equals("hash")) {
            atualizarHash();
        } else if (pagina.equals("estudantes")) {
            textoEstudantes.setText(capturar(() -> {
                EstatisticasEstudantes.mostrar(tabela.listarTodos(), leitor);
                return null;
            }));
            textoEstudantes.setCaretPosition(0);
        }
        cartas.show(conteudo, pagina);
    }

    private void selecionar(JButton ativo) {
        for (JButton b : botoesMenu) {
            boolean sel = (b == ativo);
            b.setBackground(sel ? AZUL_CLARO : BRANCO);
            b.setForeground(sel ? AZUL : AZUL_ESCURO);
            b.setFont(sel ? F_NEGRITO : F_TEXTO);
        }
    }

    // =====================================================================
    //  Paginas
    // =====================================================================

    private JPanel paginaCarregar() {
        JPanel pagina = novaPagina("Carregar ficheiro CSV",
                "Le o ficheiro, valida os registos e insere-os na Tabela Hash.");

        JPanel cartao = cartao();
        cartao.setLayout(new BorderLayout(10, 0));
        campoFicheiro = new JTextField("estudantes.csv");
        estilizar(campoFicheiro);
        JButton procurar = botaoSecundario("Procurar...");
        procurar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(new File("."));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                campoFicheiro.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        JButton carregar = botaoPrimario("Carregar");
        carregar.addActionListener(e -> carregarCSV());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(procurar);
        botoes.add(carregar);
        cartao.add(rotulado("Ficheiro", campoFicheiro), BorderLayout.CENTER);
        cartao.add(botoes, BorderLayout.EAST);

        JPanel topo = new JPanel(new BorderLayout(0, 16));
        topo.setOpaque(false);
        topo.add(cartao, BorderLayout.NORTH);

        String[] nomes = {"Linhas lidas", "Registos validos", "Registos invalidos", "Estudantes inseridos"};
        cartoesCSV = new JLabel[nomes.length];
        JPanel grelha = new JPanel(new GridLayout(1, nomes.length, 14, 0));
        grelha.setOpaque(false);
        for (int i = 0; i < nomes.length; i++) {
            cartoesCSV[i] = new JLabel("-", SwingConstants.CENTER);
            grelha.add(metrica(nomes[i], cartoesCSV[i]));
        }
        grelha.setPreferredSize(new Dimension(100, 130));
        topo.add(grelha, BorderLayout.CENTER);
        JPanel envolve = new JPanel(new BorderLayout());
        envolve.setOpaque(false);
        envolve.add(topo, BorderLayout.NORTH);
        pagina.add(envolve, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel paginaPesquisar() {
        JPanel pagina = novaPagina("Pesquisar estudante",
                "Pesquisa pelo codigo (chave da Tabela Hash).");

        JPanel cartao = cartao();
        cartao.setLayout(new BorderLayout(10, 0));
        campoPesquisa = new JTextField();
        estilizar(campoPesquisa);
        JButton b = botaoPrimario("Pesquisar");
        b.addActionListener(e -> pesquisar());
        campoPesquisa.addActionListener(e -> pesquisar());
        cartao.add(rotulado("Codigo", campoPesquisa), BorderLayout.CENTER);
        cartao.add(b, BorderLayout.EAST);

        resultadoPesquisa = areaTexto();
        resultadoPesquisa.setText("O resultado aparece aqui.");
        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(cartao, BorderLayout.NORTH);
        centro.add(comScroll(resultadoPesquisa), BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel paginaInserir() {
        JPanel pagina = novaPagina("Inserir estudante",
                "Os dados sao validados como na leitura do CSV. Codigos duplicados sao rejeitados.");

        String[] nomes = {"Codigo", "Nome", "Curso", "Ano (1-4)", "Idade", "Sexo (M/F)", "Media (0-20)"};
        camposInserir = new JTextField[nomes.length];
        JPanel form = cartao();
        form.setLayout(new GridLayout(4, 2, 16, 12));
        for (int i = 0; i < nomes.length; i++) {
            camposInserir[i] = new JTextField();
            estilizar(camposInserir[i]);
            form.add(rotulado(nomes[i], camposInserir[i]));
        }
        JButton b = botaoPrimario("Inserir");
        b.addActionListener(e -> inserir());
        JPanel cel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 18));
        cel.setOpaque(false);
        cel.add(b);
        form.add(cel);

        msgInserir = new JLabel(" ");
        msgInserir.setFont(F_NEGRITO);
        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.add(form, BorderLayout.NORTH);
        centro.add(msgInserir, BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel paginaRemover() {
        JPanel pagina = novaPagina("Remover estudante",
                "A posicao fica marcada como REMOVIDO (nunca VAZIO), para a sondagem continuar.");

        JPanel cartao = cartao();
        cartao.setLayout(new BorderLayout(10, 0));
        campoRemover = new JTextField();
        estilizar(campoRemover);
        JButton b = botaoPrimario("Remover");
        b.addActionListener(e -> remover());
        campoRemover.addActionListener(e -> remover());
        cartao.add(rotulado("Codigo", campoRemover), BorderLayout.CENTER);
        cartao.add(b, BorderLayout.EAST);

        msgRemover = new JLabel(" ");
        msgRemover.setFont(F_NEGRITO);
        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.add(cartao, BorderLayout.NORTH);
        centro.add(msgRemover, BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel paginaEstudantes() {
        JPanel pagina = novaPagina("Estatisticas dos estudantes",
                "Gerais, por sexo, curso, ano, medias e idades.");
        textoEstudantes = areaTexto();
        pagina.add(comScroll(textoEstudantes), BorderLayout.CENTER);
        return pagina;
    }

    private JPanel paginaHash() {
        JPanel pagina = novaPagina("Estatisticas da Hash Table",
                "Comportamento da propria estrutura de dados.");

        String[] nomes = {"Capacidade inicial", "Capacidade final", "Elementos armazenados",
            "Factor de carga", "Colisoes", "Sondagens realizadas",
            "Media sondagens/insercao", "Maior sondagem", "Redimensionamentos"};
        valoresHash = new JLabel[nomes.length];
        JPanel grelha = new JPanel(new GridLayout(3, 3, 14, 14));
        grelha.setOpaque(false);
        for (int i = 0; i < nomes.length; i++) {
            valoresHash[i] = new JLabel("-", SwingConstants.CENTER);
            grelha.add(metrica(nomes[i], valoresHash[i]));
        }

        barraCarga = new JProgressBar(0, 1000);
        barraCarga.setStringPainted(true);
        barraCarga.setForeground(AZUL);
        barraCarga.setPreferredSize(new Dimension(100, 26));
        JPanel carga = cartao();
        carga.setLayout(new BorderLayout(0, 6));
        JLabel l = new JLabel("Factor de carga  (redimensiona quando passa de 0,70)");
        l.setFont(F_NEGRITO);
        l.setForeground(AZUL_ESCURO);
        carga.add(l, BorderLayout.NORTH);
        carga.add(barraCarga, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.add(grelha, BorderLayout.CENTER);
        centro.add(carga, BorderLayout.SOUTH);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel paginaDesempenho() {
        JPanel pagina = novaPagina("Teste de desempenho",
                "Insercao e pesquisa com 1 000, 5 000 e 10 000 registos.");
        textoDesempenho = areaTexto();
        textoDesempenho.setText("Prima \"Executar\" para correr o teste.");
        JButton b = botaoPrimario("Executar teste");
        b.addActionListener(e -> executarEmFundo(b, textoDesempenho, () -> {
            Desempenho.testeDesempenho(lista);
            return null;
        }));
        pagina.add(acaoEResultado(b, textoDesempenho), BorderLayout.CENTER);
        return pagina;
    }

    private JPanel paginaComparacao() {
        JPanel pagina = novaPagina("Pesquisa sequencial vs Hash",
                "1000 codigos existentes e 1000 inexistentes, repetidos varias rondas.");
        textoComparacao = areaTexto();
        textoComparacao.setText("Prima \"Comparar\" para correr a comparacao.");
        JButton b = botaoPrimario("Comparar");
        b.addActionListener(e -> executarEmFundo(b, textoComparacao, () -> {
            Desempenho.compararPesquisas(tabela, lista);
            return null;
        }));
        pagina.add(acaoEResultado(b, textoComparacao), BorderLayout.CENTER);
        return pagina;
    }

    // =====================================================================
    //  Accoes
    // =====================================================================

    private void carregarCSV() {
        String caminho = campoFicheiro.getText().trim();
        if (caminho.isEmpty()) {
            caminho = "estudantes.csv";
        }
        TabelaHash nova = new TabelaHash(TabelaHash.CAPACIDADE_INICIAL);
        ArrayList<Estudante> novaLista = new ArrayList<Estudante>();
        LeitorCSV novoLeitor = new LeitorCSV();

        final String caminhoFinal = caminho;
        final boolean[] ok = new boolean[1];
        final String saida = capturar(() -> {
            ok[0] = novoLeitor.ler(caminhoFinal, nova, novaLista);
            return null;
        });
        if (!ok[0]) {
            erro("Nao foi possivel ler o ficheiro.\n" + saida.trim());
            return;
        }
        tabela = nova;
        lista = novaLista;
        leitor = novoLeitor;
        carregado = true;

        cartoesCSV[0].setText(String.valueOf(leitor.getTotalLinhas()));
        cartoesCSV[1].setText(String.valueOf(leitor.getValidos()));
        cartoesCSV[2].setText(String.valueOf(leitor.getInvalidos()));
        cartoesCSV[3].setText(String.valueOf(leitor.getInseridos()));
        atualizarEstado();
        atualizarHash();
    }

    private void pesquisar() {
        String codigo = campoPesquisa.getText().trim();
        if (!codigoValido(codigo)) {
            resultadoPesquisa.setText("Codigo invalido (apenas digitos).");
            return;
        }
        long inicio = System.nanoTime();
        Estudante e = tabela.pesquisar(codigo);
        long tempo = System.nanoTime() - inicio;
        if (e == null) {
            resultadoPesquisa.setText("Estudante nao encontrado.\n\n(" + tempo + " ns)");
        } else {
            resultadoPesquisa.setText(e + "\n\nEncontrado em " + tempo + " ns");
        }
    }

    private void inserir() {
        String[] campos = new String[camposInserir.length];
        for (int i = 0; i < campos.length; i++) {
            campos[i] = camposInserir[i].getText().trim();
        }
        Estudante e = LeitorCSV.criarEstudante(campos);
        if (e == null) {
            mensagem(msgInserir, "Dados invalidos. Estudante nao inserido.", VERMELHO);
        } else if (tabela.inserir(e)) {
            lista.add(e);
            mensagem(msgInserir, "Estudante inserido: " + e.getNome() + " (" + e.getCodigo() + ")", VERDE);
            for (JTextField c : camposInserir) {
                c.setText("");
            }
            atualizarEstado();
        } else {
            mensagem(msgInserir, "Ja existe um estudante com esse codigo.", VERMELHO);
        }
    }

    private void remover() {
        String codigo = campoRemover.getText().trim();
        if (!codigoValido(codigo)) {
            mensagem(msgRemover, "Codigo invalido (apenas digitos).", VERMELHO);
            return;
        }
        if (tabela.remover(codigo)) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getCodigo().equals(codigo)) {
                    lista.remove(i);
                    break;
                }
            }
            mensagem(msgRemover, "Estudante removido (posicao marcada como REMOVIDO).", VERDE);
            campoRemover.setText("");
            atualizarEstado();
        } else {
            mensagem(msgRemover, "Estudante nao encontrado.", VERMELHO);
        }
    }

    private boolean codigoValido(String c) {
        if (c.isEmpty() || c.length() > 18) {
            return false;
        }
        for (int i = 0; i < c.length(); i++) {
            if (!Character.isDigit(c.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void atualizarEstado() {
        estado.setText(tabela.tamanho() + " estudantes na tabela");
    }

    private void atualizarHash() {
        valoresHash[0].setText(String.valueOf(tabela.getCapacidadeInicial()));
        valoresHash[1].setText(String.valueOf(tabela.getCapacidade()));
        valoresHash[2].setText(String.valueOf(tabela.tamanho()));
        valoresHash[3].setText(String.format(Locale.US, "%.4f", tabela.fatorCarga()));
        valoresHash[4].setText(String.valueOf(tabela.getColisoes()));
        valoresHash[5].setText(String.valueOf(tabela.getSondagens()));
        valoresHash[6].setText(String.format(Locale.US, "%.2f", tabela.mediaSondagens()));
        valoresHash[7].setText(String.valueOf(tabela.getMaiorSondagem()));
        valoresHash[8].setText(String.valueOf(tabela.getRedimensionamentos()));
        int v = (int) Math.round(tabela.fatorCarga() * 1000);
        barraCarga.setValue(Math.min(v, 1000));
        barraCarga.setString(String.format(Locale.US, "%.1f %%", tabela.fatorCarga() * 100));
    }

    /** Corre uma tarefa pesada fora do thread da interface e mostra a saida de texto. */
    private void executarEmFundo(final JButton botao, final JTextArea destino, final Callable<Void> tarefa) {
        botao.setEnabled(false);
        destino.setText("A executar... aguarde.");
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return capturar(tarefa);
            }

            @Override
            protected void done() {
                try {
                    destino.setText(get());
                } catch (Exception ex) {
                    destino.setText("Erro: " + ex.getMessage());
                }
                destino.setCaretPosition(0);
                botao.setEnabled(true);
            }
        }.execute();
    }

    /** Executa codigo que escreve em System.out e devolve esse texto. */
    private static synchronized String capturar(Callable<Void> tarefa) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, "UTF-8"));
            tarefa.call();
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        } finally {
            System.out.flush();
            System.setOut(original);
        }
        try {
            return buffer.toString("UTF-8").trim();
        } catch (java.io.UnsupportedEncodingException ex) {
            return buffer.toString().trim();
        }
    }

    // =====================================================================
    //  Componentes auxiliares
    // =====================================================================

    private JPanel novaPagina(String titulo, String descricao) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(F_TITULO);
        t.setForeground(AZUL_ESCURO);
        JLabel d = new JLabel(descricao);
        d.setFont(F_TEXTO);
        d.setForeground(CINZA);
        JPanel cab = new JPanel(new GridLayout(2, 1, 0, 2));
        cab.setOpaque(false);
        cab.add(t);
        cab.add(d);
        p.add(cab, BorderLayout.NORTH);
        return p;
    }

    private JPanel cartao() {
        JPanel p = new JPanel();
        p.setBackground(BRANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDA), new EmptyBorder(16, 18, 16, 18)));
        return p;
    }

    private JPanel metrica(String nome, JLabel valor) {
        JPanel p = cartao();
        p.setLayout(new BorderLayout(0, 6));
        valor.setFont(new Font("SansSerif", Font.BOLD, 26));
        valor.setForeground(AZUL);
        JLabel n = new JLabel(nome, SwingConstants.CENTER);
        n.setFont(F_TEXTO);
        n.setForeground(CINZA);
        p.add(valor, BorderLayout.CENTER);
        p.add(n, BorderLayout.SOUTH);
        return p;
    }

    private JPanel rotulado(String texto, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(CINZA);
        p.add(l, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JPanel acaoEResultado(JButton botao, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setOpaque(false);
        linha.add(botao);
        p.add(linha, BorderLayout.NORTH);
        p.add(comScroll(area), BorderLayout.CENTER);
        return p;
    }

    private void estilizar(JTextField c) {
        c.setFont(F_TEXTO);
        c.setPreferredSize(new Dimension(200, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDA), new EmptyBorder(4, 10, 4, 10)));
    }

    private JTextArea areaTexto() {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setFont(F_MONO);
        a.setMargin(new Insets(12, 14, 12, 14));
        a.setBackground(BRANCO);
        return a;
    }

    private JScrollPane comScroll(JComponent c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(new LineBorder(BORDA));
        s.getVerticalScrollBar().setUnitIncrement(16);
        return s;
    }

    private JButton botaoPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(F_NEGRITO);
        b.setBackground(AZUL);
        b.setForeground(BRANCO);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton botaoSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(F_TEXTO);
        b.setBackground(BRANCO);
        b.setForeground(AZUL_ESCURO);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDA), new EmptyBorder(9, 18, 9, 18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void mensagem(JLabel l, String texto, Color cor) {
        l.setText(texto);
        l.setForeground(cor);
    }

    private void aviso(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void erro(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // =====================================================================

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        SwingUtilities.invokeLater(() -> new JanelaPrincipal().setVisible(true));
    }
}
