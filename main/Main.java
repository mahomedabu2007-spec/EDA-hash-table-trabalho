import desempenho.Desempenho;
import estatisticas.EstatisticasEstudantes;
import hash.TabelaHash;
import io.LeitorCSV;
import modelo.Estudante;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static TabelaHash tabela = new TabelaHash(TabelaHash.CAPACIDADE_INICIAL);
    private static ArrayList<Estudante> lista = new ArrayList<Estudante>();
    private static LeitorCSV leitor = new LeitorCSV();
    private static boolean carregado = false;

    private static Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        int opcao = -1;
        while (opcao != 0) {
            mostrarMenu();
            opcao = lerInteiro();

            if (opcao == 1) {
                carregarCSV();
            } else if (opcao == 2) {
                pesquisarEstudante();
            } else if (opcao == 3) {
                inserirEstudante();
            } else if (opcao == 4) {
                removerEstudante();
            } else if (opcao == 5) {
                if (verificarCarregado()) {
                    EstatisticasEstudantes.mostrar(tabela.listarTodos(), leitor);
                }
            } else if (opcao == 6) {
                estatisticasHash();
            } else if (opcao == 7) {
                if (verificarCarregado()) {
                    Desempenho.testeDesempenho(lista);
                }
            } else if (opcao == 8) {
                if (verificarCarregado()) {
                    Desempenho.compararPesquisas(tabela, lista);
                }
            } else if (opcao == 0) {
                System.out.println("Ate logo.");
            } else {
                System.out.println("Opcao invalida.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n================================================");
        System.out.println("      SISTEMA DE GESTAO DE ESTUDANTES");
        System.out.println("================================================");
        System.out.println(" 1 - Carregar ficheiro CSV");
        System.out.println(" 2 - Pesquisar estudante");
        System.out.println(" 3 - Inserir estudante");
        System.out.println(" 4 - Remover estudante");
        System.out.println(" 5 - Estatisticas dos estudantes");
        System.out.println(" 6 - Estatisticas da Hash Table");
        System.out.println(" 7 - Executar teste de desempenho");
        System.out.println(" 8 - Comparar pesquisa sequencial e Hash");
        System.out.println(" 0 - Sair");
        System.out.print("\n Opcao: ");
    }

    private static int lerInteiro() {
        String linha = teclado.nextLine().trim();
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static String lerTexto(String pergunta) {
        System.out.print(pergunta);
        return teclado.nextLine().trim();
    }

    private static boolean verificarCarregado() {
        if (!carregado) {
            System.out.println("Carregue primeiro o ficheiro CSV (opcao 1).");
        }
        return carregado;
    }

    private static void carregarCSV() {
        String caminho = lerTexto("Ficheiro (Enter = estudantes.csv): ");
        if (caminho.isEmpty()) {
            caminho = "estudantes.csv";
        }

        tabela = new TabelaHash(TabelaHash.CAPACIDADE_INICIAL);
        lista = new ArrayList<Estudante>();
        leitor = new LeitorCSV();

        carregado = leitor.ler(caminho, tabela, lista);
        if (carregado) {
            System.out.println("Ficheiro carregado: " + leitor.getInseridos()
                    + " estudantes inseridos, " + leitor.getInvalidos() + " registos invalidos.");
        }
    }

    private static void pesquisarEstudante() {
        String codigo = lerTexto("Codigo: ");
        if (!codigoValido(codigo)) {
            return;
        }
        Estudante e = tabela.pesquisar(codigo);
        if (e == null) {
            System.out.println("Estudante nao encontrado.");
        } else {
            System.out.println(e);
        }
    }

    private static void inserirEstudante() {
        String[] campos = new String[7];
        campos[0] = lerTexto("Codigo: ");
        campos[1] = lerTexto("Nome: ");
        campos[2] = lerTexto("Curso: ");
        campos[3] = lerTexto("Ano (1-4): ");
        campos[4] = lerTexto("Idade: ");
        campos[5] = lerTexto("Sexo (M/F): ");
        campos[6] = lerTexto("Media (0-20): ");

        Estudante e = LeitorCSV.criarEstudante(campos);
        if (e == null) {
            System.out.println("Dados invalidos. Estudante nao inserido.");
        } else if (tabela.inserir(e)) {
            lista.add(e);
            System.out.println("Estudante inserido.");
        } else {
            System.out.println("Ja existe um estudante com esse codigo.");
        }
    }

    private static void removerEstudante() {
        String codigo = lerTexto("Codigo: ");
        if (!codigoValido(codigo)) {
            return;
        }
        if (tabela.remover(codigo)) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getCodigo().equals(codigo)) {
                    lista.remove(i);
                    break;
                }
            }
            System.out.println("Estudante removido.");
        } else {
            System.out.println("Estudante nao encontrado.");
        }
    }

    /** O codigo tem de ser numerico (e a chave da tabela). */
    private static boolean codigoValido(String codigo) {
        if (codigo.isEmpty() || codigo.length() > 18) {
            System.out.println("Codigo invalido.");
            return false;
        }
        for (int i = 0; i < codigo.length(); i++) {
            if (!Character.isDigit(codigo.charAt(i))) {
                System.out.println("Codigo invalido.");
                return false;
            }
        }
        return true;
    }

    private static void estatisticasHash() {
        System.out.println("\n========== ESTATISTICAS DA HASH TABLE ==========\n");
        System.out.println("Capacidade inicial     : " + tabela.getCapacidadeInicial());
        System.out.println("Capacidade final       : " + tabela.getCapacidade());
        System.out.println("Elementos armazenados  : " + tabela.tamanho());
        System.out.println("Factor de carga        : " + String.format("%.4f", tabela.fatorCarga()));
        System.out.println("Colisoes               : " + tabela.getColisoes());
        System.out.println("Sondagens realizadas   : " + tabela.getSondagens());
        System.out.println("Media sondagens/insercao: " + String.format("%.2f", tabela.mediaSondagens()));
        System.out.println("Maior sondagem         : " + tabela.getMaiorSondagem());
        System.out.println("Redimensionamentos     : " + tabela.getRedimensionamentos());
    }
}
