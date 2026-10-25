import hash.TabelaHash;
import modelo.Estudante;

/**
 * Demonstracao para a defesa (m = 7):
 * 1) colisao entre 10 e 17; 2) remover 10 (a primeira); 3) pesquisar 17 (a segunda).
 */
public class DemoDefesa {

    private static Estudante est(String codigo) {
        return new Estudante(codigo, "Estudante " + codigo, "Curso", 1, 20, "F", 14.0);
    }

    public static void main(String[] args) {
        int m = 7;
        TabelaHash t = new TabelaHash(m);

        t.inserir(est("10"));
        t.inserir(est("17"));

        System.out.println("h1(10)=" + t.h1("10") + "  h2(10)=" + t.h2("10"));
        System.out.println("h1(17)=" + t.h1("17") + "  h2(17)=" + t.h2("17"));
        System.out.println("-> 10 e 17 colidem: ambos comecam na posicao 3");
        System.out.println("Colisoes: " + t.getColisoes());

        System.out.println("\nTabela com 10 e 17:");
        t.exibir();

        t.remover("10");
        System.out.println("\nDepois de remover 10 (a primeira chave):");
        t.exibir();

        System.out.println("\nPesquisar 17 (a segunda chave):");
        int h1 = t.h1("17");
        int h2 = t.h2("17");
        for (int i = 0; i < 2; i++) {
            System.out.println("  i=" + i + " -> posicao " + ((h1 + i * h2) % m));
        }
        System.out.println("  A posicao 3 esta REMOVIDO, a pesquisa continua e encontra 17 na posicao 2.");
        System.out.println("Resultado: " + (t.pesquisar("17") != null ? "ENCONTRADO" : "NAO ENCONTRADO"));
    }
}
