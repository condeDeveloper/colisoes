package br.com.conde.colisoes;

import java.util.ArrayList;
import java.util.List;

/**
 * AS MEDIDAS: a conta exata conferida contra a contagem bruta, e as regras de bolso medidas
 * onde elas são usadas.
 */
public final class Medidas {

    private Medidas() {}

    /* ===================== 1. correcao ===================== */

    public record Correcao(int posicoes, int chaves, double exato, long comColisao,
            long casos) {

        public double porForcaBruta() {
            return comColisao / (double) casos;
        }

        public boolean batem() {
            return Math.abs(exato - porForcaBruta()) < 1e-9;
        }
    }

    private static List<Correcao> correcao;

    /**
     * A CONTA EXATA CONTRA A CONTAGEM DE TODOS OS CASOS.
     *
     * <p>A conta exata é um produto de frações. A força bruta gera todas as {@code m^n}
     * maneiras de distribuir as chaves e conta quantas têm colisão, sem usar fórmula nenhuma.
     *
     * <p>Com 6 posições e 4 chaves são 1.296 distribuições, todas visitadas. É o único jeito
     * de saber que o produto de frações está contando o que se pensa que ele conta.
     */
    public static List<Correcao> correcao() {
        if (correcao != null) return correcao;

        List<Correcao> saida = new ArrayList<>();

        for (int posicoes = 2; posicoes <= 6; posicoes++)
            for (int chaves = 1; chaves <= 5; chaves++) {
                long[] bruto = Colisoes.porForcaBruta(chaves, posicoes);

                saida.add(new Correcao(posicoes, chaves,
                        Colisoes.chanceDeColisao(chaves, posicoes), bruto[0], bruto[1]));
            }

        return correcao = List.copyOf(saida);
    }

    public static int divergencias() {
        int quantas = 0;

        for (Correcao linha : correcao()) if (!linha.batem()) quantas++;

        return quantas;
    }

    /* ===================== 2. aniversario ===================== */

    public record Aniversario(int pessoas, double chance) {}

    private static List<Aniversario> aniversario;

    /**
     * O PROBLEMA DO ANIVERSÁRIO, com 365 dias.
     *
     * <p>São 23 pessoas para passar de 50%, que é o número famoso. O que quase ninguém sabe é
     * quantas faltam para chegar a 99%, e a resposta é o que torna o fenômeno estranho de
     * verdade.
     */
    public static List<Aniversario> aniversario() {
        if (aniversario != null) return aniversario;

        List<Aniversario> saida = new ArrayList<>();

        for (int pessoas : new int[] {5, 10, 20, 22, 23, 30, 40, 50, 57, 70})
            saida.add(new Aniversario(pessoas, Colisoes.chanceDeColisao(pessoas, 365)));

        return aniversario = List.copyOf(saida);
    }

    public static int paraMetade() {
        return Colisoes.quantasParaPassarDe(0.5, 365);
    }

    public static int paraNoventaENove() {
        return Colisoes.quantasParaPassarDe(0.99, 365);
    }

    /* ===================== 3. raiz ===================== */

    public record Raiz(int posicoes, int exato, double aproximado) {

        public double erroPorCento() {
            return 100 * (aproximado - exato) / exato;
        }
    }

    private static List<Raiz> raiz;

    /**
     * A REGRA DA RAIZ QUADRADA, e onde ela erra.
     *
     * <p>A regra de bolso: para 50% de chance de colisão bastam {@code 1,1774} vezes a raiz do
     * número de posições. Ela vem de aproximar o produto por uma exponencial.
     *
     * <p>Ela SUBESTIMA sempre, e erra mais justamente em tabela pequena, que é onde alguém faz
     * a conta de cabeça. Com 10 posições ela erra 25%.
     */
    public static List<Raiz> raiz() {
        if (raiz != null) return raiz;

        List<Raiz> saida = new ArrayList<>();

        for (int posicoes : new int[] {10, 100, 365, 1_000, 10_000, 100_000, 1_000_000})
            saida.add(new Raiz(posicoes, Colisoes.quantasParaPassarDe(0.5, posicoes),
                    Colisoes.aproximacaoDaRaiz(posicoes)));

        return raiz = List.copyOf(saida);
    }

    public static Raiz raiz(int posicoes) {
        return raiz().stream().filter(x -> x.posicoes() == posicoes)
                .findFirst().orElseThrow();
    }

    /** A aproximação erra para baixo em todo tamanho medido. */
    public static boolean sempreSubestima() {
        for (Raiz linha : raiz()) if (linha.erroPorCento() >= 0) return false;

        return true;
    }

    /* ===================== 4. baldes ===================== */

    public record Balde(int maior, long casos, double fracao) {}

    private static List<Balde> baldes;

    /** Quantas chaves, e quantas posições, na varredura do maior balde. */
    public static final int CHAVES = 6;
    public static final int POSICOES = 6;

    /**
     * O MAIOR BALDE, com tantas chaves quantas posições.
     *
     * <p>A intuição é que seis chaves em seis posições se espalham. A varredura de todas as
     * 46.656 distribuições diz outra coisa: o caso em que cada uma cai num lugar diferente
     * acontece em 1,54% das vezes.
     *
     * <p>E o maior balde chega a três ou mais em mais de um terço dos casos. Numa tabela de
     * espalhamento isso é o custo da pior busca, e ele não se parece com a média.
     */
    public static List<Balde> baldes() {
        if (baldes != null) return baldes;

        int[] distribuicao = Colisoes.distribuicaoDoMaiorBalde(CHAVES, POSICOES);

        long total = 0;

        for (int quantos : distribuicao) total += quantos;

        List<Balde> saida = new ArrayList<>();

        for (int maior = 1; maior < distribuicao.length; maior++)
            if (distribuicao[maior] > 0)
                saida.add(new Balde(maior, distribuicao[maior],
                        distribuicao[maior] / (double) total));

        return baldes = List.copyOf(saida);
    }

    public static Balde balde(int maior) {
        return baldes().stream().filter(x -> x.maior() == maior).findFirst().orElseThrow();
    }

    /** A fração de casos em que o maior balde tem três ou mais. */
    public static double fracaoComTresOuMais() {
        double soma = 0;

        for (Balde b : baldes()) if (b.maior() >= 3) soma += b.fracao();

        return soma;
    }

    /* ===================== 5. tabela ===================== */

    public record Carga(int posicoes, int chaves, double ocupacao, double chanceDeColisao) {}

    /**
     * QUANDO A COLISÃO VIRA CERTEZA, numa tabela de espalhamento.
     *
     * <p>A ocupação é a razão entre chaves e posições, e é por ela que se costuma decidir
     * quando crescer a tabela. A tabela mostra que a colisão já é quase certa muito antes de
     * a ocupação parecer alta.
     */
    public static List<Carga> tabela() {
        List<Carga> saida = new ArrayList<>();

        int posicoes = 1024;

        for (int chaves : new int[] {8, 16, 32, 38, 64, 128, 256, 512})
            saida.add(new Carga(posicoes, chaves, chaves / (double) posicoes,
                    Colisoes.chanceDeColisao(chaves, posicoes)));

        return saida;
    }

    /** Com que ocupação a colisão já passa de metade, numa tabela de 1024. */
    public static double ocupacaoNaMetade() {
        int chaves = Colisoes.quantasParaPassarDe(0.5, 1024);

        return chaves / 1024.0;
    }
}
