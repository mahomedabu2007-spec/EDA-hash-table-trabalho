package modelo;

public class Entrada {
    public static final int VAZIO = 0;
    public static final int OCUPADO = 1;
    public static final int REMOVIDO = 2;

    public Estudante estudante;
    public int estado;

    public Entrada() {
        this.estudante = null;
        this.estado = VAZIO;
    }
}
