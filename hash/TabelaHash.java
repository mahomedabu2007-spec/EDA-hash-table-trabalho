package hash;

import modelo.Entrada;
import modelo.Estudante;

/**
 * Tabela Hash com enderecamento aberto e Double Hashing.
 * Sequencia de sondagem: h(k,i) = (h1(k) + i*h2(k)) mod m
 *
 * PESSOA 1: estrutura, h1, h2, sondagem, colisoes, estados.
 * PESSOA 2: redimensionamento (marcado com TODO) e contadores/estatisticas.
 */
public class TabelaHash {

    private Entrada[] tabela;
    private int capacidade;
    private int n;            // posicoes OCUPADAS
    private final int capacidadeInicial;

    public TabelaHash(int capacidadeInicial) {
        this.capacidade = proximoPrimo(capacidadeInicial);
        this.capacidadeInicial = this.capacidade;
        this.tabela = criarTabela(this.capacidade);
        this.n = 0;
    }

    private static Entrada[] criarTabela(int m) {
        Entrada[] t = new Entrada[m];
        for (int i = 0; i < m; i++) t[i] = new Entrada(); // todas VAZIO
        return t;
    }

    // ---------------- Conversao codigo -> numero ----------------

    /** Converte o codigo (String) num inteiro nao negativo. */
    static long chaveNumerica(String codigo) {
        boolean soDigitos = !codigo.isEmpty() && codigo.length() <= 18;
        for (int i = 0; i < codigo.length() && soDigitos; i++) {
            if (!Character.isDigit(codigo.charAt(i))) soDigitos = false;
        }
        if (soDigitos) return Long.parseLong(codigo);

        // codigos nao numericos: hash polinomial (base 31)
        long h = 0;
        for (int i = 0; i < codigo.length(); i++) {
            h = (h * 31 + codigo.charAt(i)) & Long.MAX_VALUE;
        }
        return h;
    }

    // ---------------- Funcoes hash ----------------

    /** h1(k) = k mod m  -> posicao inicial, em [0, m-1] */
    int h1(String codigo) {
        return (int) Math.floorMod(chaveNumerica(codigo), (long) capacidade);
    }

    /** h2(k) = 1 + (k mod (m-1)) -> passo, em [1, m-1]; nunca 0 */
    int h2(String codigo) {
        return 1 + (int) Math.floorMod(chaveNumerica(codigo), (long) (capacidade - 1));
    }

    /** h(k,i) = (h1(k) + i*h2(k)) mod m */
    int sondar(String codigo, int i) {
        long indice = (long) h1(codigo) + (long) i * h2(codigo);
        return (int) (indice % capacidade);
    }

    // ---------------- Operacoes basicas ----------------

    /** @return true se inseriu; false se codigo duplicado ou tabela cheia */
    public boolean inserir(Estudante e) {
        // TODO (Pessoa 2): se (n+1)/capacidade > 0.70 -> redimensionar()
        if (pesquisar(e.getCodigo()) != null) return false;   // duplicado

        int primeiroRemovido = -1;
        for (int i = 0; i < capacidade; i++) {
            int idx = sondar(e.getCodigo(), i);
            Entrada en = tabela[idx];

            if (en.estado == Entrada.OCUPADO) {
                // TODO (Pessoa 2): contar colisao e sondagem
                continue;
            }
            // VAZIO ou REMOVIDO: pode inserir aqui
            colocar(idx, e);
            return true;
        }
        return false; // tabela cheia
    }

    public Estudante pesquisar(String codigo) {
        for (int i = 0; i < capacidade; i++) {
            Entrada en = tabela[sondar(codigo, i)];
            if (en.estado == Entrada.VAZIO) return null;      // fim da cadeia
            if (en.estado == Entrada.OCUPADO
                    && en.estudante.getCodigo().equals(codigo)) {
                return en.estudante;
            }
            // REMOVIDO ou outro codigo: continua a sondar
        }
        return null;
    }

    public boolean remover(String codigo) {
        for (int i = 0; i < capacidade; i++) {
            Entrada en = tabela[sondar(codigo, i)];
            if (en.estado == Entrada.VAZIO) return false;
            if (en.estado == Entrada.OCUPADO
                    && en.estudante.getCodigo().equals(codigo)) {
                en.estado = Entrada.REMOVIDO;   // NUNCA voltar a VAZIO
                en.estudante = null;
                n--;
                return true;
            }
        }
        return false;
    }

    private void colocar(int idx, Estudante e) {
        tabela[idx].estudante = e;
        tabela[idx].estado = Entrada.OCUPADO;
        n++;
    }

    // ---------------- Apoio a outras pessoas ----------------

    public int tamanho()            { return n; }
    public int getCapacidade()      { return capacidade; }
    public int getCapacidadeInicial() { return capacidadeInicial; }
    public double fatorCarga()      { return (double) n / capacidade; }

    /** Para a Pessoa 3 (estatisticas). */
    public Estudante[] listarTodos() {
        Estudante[] r = new Estudante[n];
        int k = 0;
        for (Entrada en : tabela) {
            if (en.estado == Entrada.OCUPADO) r[k++] = en.estudante;
        }
        return r;
    }

    /** Mostra a tabela (so para demonstracao/defesa). */
    public void exibir() {
        for (int i = 0; i < capacidade; i++) {
            Entrada en = tabela[i];
            String s = en.estado == Entrada.VAZIO ? "VAZIO"
                     : en.estado == Entrada.REMOVIDO ? "REMOVIDO"
                     : en.estudante.getCodigo();
            System.out.println(i + " -> " + s);
        }
    }

    // ---------------- Primos (m primo => h2 percorre toda a tabela) ----------------

    public static boolean ehPrimo(int x) {
        if (x < 2) return false;
        if (x % 2 == 0) return x == 2;
        for (int d = 3; (long) d * d <= x; d += 2) if (x % d == 0) return false;
        return true;
    }

    public static int proximoPrimo(int x) {
        while (!ehPrimo(x)) x++;
        return x;
    }
}
