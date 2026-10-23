import hash.TabelaHash;
import modelo.Estudante;

public class TesteNucleo {
    static Estudante e(String c) { return new Estudante(c, "Nome " + c, "Eng", 1, 20, "F", 14.0); }

    public static void main(String[] args) {
        // Demonstracao do teste obrigatorio da defesa, com m=7
        TabelaHash t = new TabelaHash(7);
        t.inserir(e("10")); t.inserir(e("17")); t.inserir(e("24"));
        System.out.println("== Tabela (m=7) com 10, 17, 24 (todas h1=3) ==");
        t.exibir();

        System.out.println("\nRemover 17 (1a colisao) e pesquisar 24 (2a):");
        t.remover("17");
        t.exibir();
        System.out.println("pesquisar(24) = " + (t.pesquisar("24") != null));
        System.out.println("pesquisar(99) = " + (t.pesquisar("99") != null));
        System.out.println("inserir duplicado 10 = " + t.inserir(e("10")));

        // Stress: 5000 codigos, capacidade grande, sem resize
        TabelaHash g = new TabelaHash(10007);
        for (int i = 0; i < 5000; i++) g.inserir(e(String.valueOf(20260001 + i)));
        boolean ok = true;
        for (int i = 0; i < 5000; i++) ok &= g.pesquisar(String.valueOf(20260001 + i)) != null;
        System.out.println("\n5000 inseridos, todos encontrados: " + ok
            + " | n=" + g.tamanho() + " | carga=" + String.format("%.3f", g.fatorCarga()));
        for (int i = 0; i < 5000; i += 2) g.remover(String.valueOf(20260001 + i));
        ok = true;
        for (int i = 1; i < 5000; i += 2) ok &= g.pesquisar(String.valueOf(20260001 + i)) != null;
        System.out.println("apos remover metade, restantes encontrados: " + ok + " | n=" + g.tamanho());
    }
}
