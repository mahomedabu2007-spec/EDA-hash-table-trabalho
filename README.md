# Sistema de Indexação, Pesquisa e Análise de Dados com Tabela Hash

Trabalho prático de **Estruturas de Dados e Algoritmos** (Universidade Eduardo Mondlane, Faculdade de Engenharia).

Tabela Hash implementada de raiz em Java, com endereçamento aberto e **Double Hashing**, estados `VAZIO`, `OCUPADO` e `REMOVIDO`, redimensionamento automático (factor de carga > 0,70), estatísticas e comparação de desempenho com a pesquisa sequencial. Lê o ficheiro `estudantes.csv` (10 000 registos).

## Estrutura

| Pasta / ficheiro | Conteúdo |
|---|---|
| `modelo/` | `Estudante` e `Entrada` |
| `hash/TabelaHash.java` | Tabela Hash: h1, h2, inserir, pesquisar, remover, redimensionamento, contadores |
| `io/LeitorCSV.java` | Leitura e validação do CSV |
| `estatisticas/` | Estatísticas dos estudantes |
| `desempenho/` | Experiência de desempenho e pesquisa sequencial vs Hash |
| `gui/JanelaPrincipal.java` | Interface gráfica (Swing) |
| `Main.java` | Menu de consola |
| `DemoDefesa.java`, `TesteNucleo.java` | Demonstrações do teste de colisão e do estado `REMOVIDO` |
| `dados_teste/` | CSV de teste adicionais (usados na defesa) |
| `docs/` | Relatório (PDF e fonte LaTeX) e guia da defesa |

## Como executar (Java 8 ou superior)

Na pasta do projecto, onde está o `estudantes.csv`:

```
javac -d out $(find . -name "*.java")          # Linux/Mac
java -cp out gui.JanelaPrincipal                # interface gráfica
java -cp out Main                               # menu de consola
java -cp out DemoDefesa                         # demonstração com m = 7
```

No Windows: `dir /s /b *.java > fontes.txt` e `javac -d out @fontes.txt`.

## Grupo

- Chongola, Shannon Marcos
- Nhamuave, Nicoleta Dito
- Rajá, Mahomed Aboobacar Omar
- Sambo, Elves Damião
- Vilanculo, Agnelo Cosme
