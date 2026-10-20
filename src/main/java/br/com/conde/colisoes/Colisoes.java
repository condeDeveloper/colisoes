package br.com.conde.colisoes;

import java.math.BigInteger;

/**
 * A CHANCE DE DUAS COISAS CAÍREM NO MESMO LUGAR.
 *
 * <p>É o problema do aniversário, e é também o problema de toda tabela de espalhamento: com
 * {@code n} chaves e {@code m} posições, qual a chance de duas chaves disputarem a mesma?
 *
 * <p>A conta exata é a do complemento. A chance de NÃO haver colisão é o produto de
 * {@code (m-k)/m} para k de 0 a n-1: a primeira chave acha tudo livre, a segunda tem m-1
 * posições livres, e assim por diante.
 *
 * <p>Tudo aqui é feito com números racionais exatos, em {@link BigInteger}, e não com ponto
 * flutuante. Com 365 dias e 50 pessoas o produto tem mais de cem casas, e acumular erro de
 * arredondamento numa conta assim é fácil e silencioso.
 */
public final class Colisoes {

    private Colisoes() {}

    /** Quantas multiplicações a conta exata fez. */
    public static long operacoes;

    public static void zerar() {
        operacoes = 0;
    }

    /**
     * A CHANCE DE NÃO HAVER COLISÃO, como fração exata.
     *
     * <p>Devolve o numerador e o denominador separados, porque o resultado não cabe em
     * {@code double} sem perder dígitos, e o ponto deste repositório é não perder.
     */
    public static BigInteger[] semColisaoExato(int chaves, int posicoes) {
        if (posicoes < 1) throw new IllegalArgumentException("posicoes: " + posicoes);

        if (chaves < 0) throw new IllegalArgumentException("chaves: " + chaves);

        /* Mais chaves que posicoes: colisao certa, pelo principio da casa dos pombos. */
        if (chaves > posicoes) return new BigInteger[] {BigInteger.ZERO, BigInteger.ONE};

        BigInteger numerador = BigInteger.ONE;
        BigInteger denominador = BigInteger.ONE;

        BigInteger m = BigInteger.valueOf(posicoes);

        for (int k = 0; k < chaves; k++) {
            operacoes++;

            numerador = numerador.multiply(BigInteger.valueOf(posicoes - k));
            denominador = denominador.multiply(m);
        }

        return new BigInteger[] {numerador, denominador};
    }

    /** A chance de HAVER colisão, em ponto flutuante, para a tabela. */
    public static double chanceDeColisao(int chaves, int posicoes) {
        BigInteger[] sem = semColisaoExato(chaves, posicoes);

        return 1 - new java.math.BigDecimal(sem[0])
                .divide(new java.math.BigDecimal(sem[1]), 40, java.math.RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    /** Quantas chaves bastam para a colisão passar de uma certa chance. */
    public static int quantasParaPassarDe(double chance, int posicoes) {
        for (int chaves = 1; chaves <= posicoes + 1; chaves++)
            if (chanceDeColisao(chaves, posicoes) > chance) return chaves;

        return -1;
    }

    /**
     * A APROXIMAÇÃO QUE TODO MUNDO USA: raiz de m, vezes uma constante.
     *
     * <p>Para 50% de chance, a conta é {@code 1,1774 vezes a raiz de m}. Ela sai de aproximar
     * o produto por uma exponencial, e a terceira medida mede o quanto ela erra.
     */
    public static double aproximacaoDaRaiz(int posicoes) {
        return 1.1774 * Math.sqrt(posicoes);
    }

    /**
     * A VARREDURA EXAUSTIVA, que é o juiz.
     *
     * <p>Com poucas posições e poucas chaves dá para contar TODAS as maneiras de distribuir as
     * chaves e ver quantas têm colisão. São {@code m^n} distribuições, e nenhuma fórmula é
     * usada: é contagem pura.
     */
    public static long[] porForcaBruta(int chaves, int posicoes) {
        long total = 1;

        for (int passo = 0; passo < chaves; passo++) total *= posicoes;

        long comColisao = 0;

        int[] onde = new int[chaves];

        for (long caso = 0; caso < total; caso++) {
            long resto = caso;

            for (int indice = 0; indice < chaves; indice++) {
                onde[indice] = (int) (resto % posicoes);
                resto /= posicoes;
            }

            boolean colidiu = false;

            for (int a = 0; a < chaves && !colidiu; a++)
                for (int b = a + 1; b < chaves && !colidiu; b++)
                    if (onde[a] == onde[b]) colidiu = true;

            if (colidiu) comColisao++;
        }

        return new long[] {comColisao, total};
    }

    /**
     * O MAIOR BALDE: quantas chaves caem na posição mais cheia.
     *
     * <p>Numa tabela de espalhamento isso é o que decide o pior caso de busca, e é um número
     * bem diferente da média. A quarta medida conta o maior balde em todas as distribuições
     * possíveis, em vez de estimar.
     */
    public static int[] distribuicaoDoMaiorBalde(int chaves, int posicoes) {
        long total = 1;

        for (int passo = 0; passo < chaves; passo++) total *= posicoes;

        int[] quantos = new int[chaves + 1];

        int[] baldes = new int[posicoes];

        for (long caso = 0; caso < total; caso++) {
            java.util.Arrays.fill(baldes, 0);

            long resto = caso;

            for (int indice = 0; indice < chaves; indice++) {
                baldes[(int) (resto % posicoes)]++;
                resto /= posicoes;
            }

            int maior = 0;

            for (int balde : baldes) maior = Math.max(maior, balde);

            quantos[maior]++;
        }

        return quantos;
    }
}
