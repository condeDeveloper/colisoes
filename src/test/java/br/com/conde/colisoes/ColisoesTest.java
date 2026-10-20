package br.com.conde.colisoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A conta exata e a contagem bruta.
 *
 * <p>O erro silencioso aqui seria acumular arredondamento no produto de frações. Com 365 dias
 * e 50 pessoas o produto tem mais de cem casas, e um double perde dígitos sem avisar. Por isso
 * a conta é feita em número grande e conferida contra contagem de casos inteiros.
 */
class ColisoesTest {

    @Test
    @DisplayName("sem chave nenhuma nao ha colisao, e com uma tambem nao")
    void casosDeBorda() {
        assertEquals(0.0, Colisoes.chanceDeColisao(0, 10), 1e-12);
        assertEquals(0.0, Colisoes.chanceDeColisao(1, 10), 1e-12);
    }

    @Test
    @DisplayName("MAIS CHAVES QUE POSICOES e colisao certa, pela casa dos pombos")
    void casaDosPombos() {
        for (int posicoes = 1; posicoes <= 10; posicoes++)
            for (int chaves = posicoes + 1; chaves <= posicoes + 5; chaves++)
                assertEquals(1.0, Colisoes.chanceDeColisao(chaves, posicoes), 1e-12,
                        chaves + " chaves em " + posicoes + " posicoes");
    }

    @Test
    @DisplayName("posicoes ou chaves invalidas nao passam")
    void invalidos() {
        assertThrows(IllegalArgumentException.class, () -> Colisoes.semColisaoExato(1, 0));
        assertThrows(IllegalArgumentException.class, () -> Colisoes.semColisaoExato(-1, 10));
    }

    @Test
    @DisplayName("A CONTA EXATA BATE COM A CONTAGEM BRUTA, em todos os casos pequenos")
    void exatoBateComBruto() {
        for (int posicoes = 2; posicoes <= 6; posicoes++)
            for (int chaves = 1; chaves <= 5; chaves++) {
                long[] bruto = Colisoes.porForcaBruta(chaves, posicoes);

                assertEquals(bruto[0] / (double) bruto[1],
                        Colisoes.chanceDeColisao(chaves, posicoes), 1e-9,
                        chaves + " chaves em " + posicoes + " posicoes");
            }
    }

    @Test
    @DisplayName("a fracao exata e exata mesmo: 2 chaves em 4 posicoes da um quarto")
    void fracaoExata() {
        BigInteger[] sem = Colisoes.semColisaoExato(2, 4);

        /* 4 vezes 3, sobre 4 vezes 4: 12/16, que e tres quartos sem colisao. */
        assertEquals(BigInteger.valueOf(12), sem[0]);
        assertEquals(BigInteger.valueOf(16), sem[1]);

        assertEquals(0.25, Colisoes.chanceDeColisao(2, 4), 1e-12);
    }

    @Test
    @DisplayName("a chance cresce com o numero de chaves, sempre")
    void chanceSobe() {
        double anterior = -1;

        for (int chaves = 0; chaves <= 60; chaves++) {
            double chance = Colisoes.chanceDeColisao(chaves, 365);

            assertTrue(chance >= anterior, "chaves " + chaves);

            anterior = chance;
        }
    }

    @Test
    @DisplayName("23 pessoas passam de metade, e 22 ainda nao")
    void vinteETres() {
        assertTrue(Colisoes.chanceDeColisao(22, 365) < 0.5);
        assertTrue(Colisoes.chanceDeColisao(23, 365) > 0.5);

        assertEquals(23, Colisoes.quantasParaPassarDe(0.5, 365));
    }

    @Test
    @DisplayName("o maior balde conta todas as distribuicoes, sem perder nenhuma")
    void maiorBaldeFecha() {
        int[] distribuicao = Colisoes.distribuicaoDoMaiorBalde(4, 4);

        long soma = 0;

        for (int quantos : distribuicao) soma += quantos;

        assertEquals(256, soma);

        /* As 4 chaves em 4 baldes diferentes: 4 fatorial = 24 maneiras. */
        assertEquals(24, distribuicao[1]);

        /* Todas no mesmo balde: 4 maneiras, uma por balde. */
        assertEquals(4, distribuicao[4]);
    }
}
