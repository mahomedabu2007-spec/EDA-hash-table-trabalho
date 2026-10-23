package modelo;

public class Estudante {
    private final String codigo;
    private final String nome;
    private final String curso;
    private final int ano;
    private final int idade;
    private final String sexo;
    private final double media;

    public Estudante(String codigo, String nome, String curso,
                     int ano, int idade, String sexo, double media) {
        this.codigo = codigo;
        this.nome = nome;
        this.curso = curso;
        this.ano = ano;
        this.idade = idade;
        this.sexo = sexo;
        this.media = media;
    }

    public String getCodigo() { return codigo; }
    public String getNome()   { return nome; }
    public String getCurso()  { return curso; }
    public int getAno()       { return ano; }
    public int getIdade()     { return idade; }
    public String getSexo()   { return sexo; }
    public double getMedia()  { return media; }

    @Override
    public String toString() {
        return codigo + " | " + nome + " | " + curso + " | " + ano + "º ano | "
             + idade + " anos | " + sexo + " | média " + media;
    }
}
