package io;

import hash.TabelaHash;
import modelo.Estudante;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LeitorCSV {

    private int totalLinhas;
    private int validos;
    private int invalidos;
    private int duplicados;
    private int inseridos;

    public int getTotalLinhas() { return totalLinhas; }
    public int getValidos()     { return validos; }
    public int getInvalidos()   { return invalidos; }
    public int getDuplicados()  { return duplicados; }
    public int getInseridos()   { return inseridos; }

    /**
     * Valida os 7 campos (codigo, nome, curso, ano, idade, sexo, media).
     * Devolve o Estudante, ou null se algum campo for invalido.
     */
    public static Estudante criarEstudante(String[] c) {
        if (c.length != 7) {
            return null;
        }

        String codigo = c[0].trim();
        String nome = c[1].trim();
        String curso = c[2].trim();
        String sexo = c[5].trim().toUpperCase();

        if (codigo.isEmpty() || codigo.length() > 18) {
            return null;
        }
        for (int i = 0; i < codigo.length(); i++) {
            if (!Character.isDigit(codigo.charAt(i))) {
                return null;
            }
        }
        if (nome.isEmpty() || curso.isEmpty()) {
            return null;
        }
        if (!sexo.equals("M") && !sexo.equals("F")) {
            return null;
        }

        int ano;
        int idade;
        double media;
        try {
            ano = Integer.parseInt(c[3].trim());
            idade = Integer.parseInt(c[4].trim());
            media = Double.parseDouble(c[6].trim());
        } catch (NumberFormatException ex) {
            return null;
        }

        if (ano < 1 || ano > 4) {
            return null;
        }
        if (idade < 10 || idade > 100) {
            return null;
        }
        if (!(media >= 0 && media <= 20)) {
            return null;
        }

        return new Estudante(codigo, nome, curso, ano, idade, sexo, media);
    }

    /** Le o ficheiro e insere os estudantes na tabela e na lista. */
    public boolean ler(String caminho, TabelaHash tabela, ArrayList<Estudante> lista) {
        totalLinhas = 0;
        validos = 0;
        invalidos = 0;
        duplicados = 0;
        inseridos = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(caminho), StandardCharsets.UTF_8))) {

            String linha = br.readLine();          // cabecalho (ignorado)

            while ((linha = br.readLine()) != null) {
                totalLinhas++;
                String[] campos = linha.split(",", -1);
                Estudante e = criarEstudante(campos);

                if (e == null) {
                    invalidos++;
                } else {
                    validos++;
                    if (tabela.inserir(e)) {
                        inseridos++;
                        lista.add(e);
                    } else {
                        duplicados++;
                    }
                }
            }
            return true;

        } catch (IOException ex) {
            System.out.println("Erro ao ler o ficheiro: " + ex.getMessage());
            return false;
        }
    }
}
