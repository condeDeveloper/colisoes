package br.com.conde.colisoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os números que aparecem escritos no README, travados em teste. */
class MedidasTest {

    @Test
    @DisplayName("zero divergencias entre a conta exata e a forca bruta")
    void correcao() {
        assertEquals(0, Medidas.divergencias());

        assertEquals(25, Medidas.correcao().size());
    }

    @Test
    @DisplayName("23 PESSOAS para metade, e 57 para 99 por cento")
    void aniversario() {
        assertEquals(23, Medidas.paraMetade());
        assertEquals(57, Medidas.paraNoventaENove());

        /* Ir de metade a quase certeza custa 34 pessoas a mais. */
        assertEquals(34, Medidas.paraNoventaENove() - Medidas.paraMetade());
    }

    @Test
    @DisplayName("e com 50 pessoas ja passa de 97 por cento")
    void cinquentaPessoas() {
        double comCinquenta = Medidas.aniversario().stream()
                .filter(x -> x.pessoas() == 50).findFirst().orElseThrow().chance();

        assertEquals(0.9704, comCinquenta, 0.0001);
    }

    @Test
    @DisplayName("A REGRA DA RAIZ SUBESTIMA em todo tamanho medido")
    void raizSubestima() {
        assertTrue(Medidas.sempreSubestima());

        for (Medidas.Raiz r : Medidas.raiz())
            assertTrue(r.erroPorCento() < 0, "posicoes " + r.posicoes());
    }

    @Test
    @DisplayName("e erra 25% em tabela pequena e quase nada em tabela grande")
    void raizErraMaisNoPequeno() {
        assertEquals(-25.5, Medidas.raiz(10).erroPorCento(), 0.2);
        assertEquals(-0.1, Medidas.raiz(1_000_000).erroPorCento(), 0.1);

        assertEquals(5, Medidas.raiz(10).exato());
        assertEquals(23, Medidas.raiz(365).exato());
        assertEquals(1178, Medidas.raiz(1_000_000).exato());
    }

    @Test
    @DisplayName("SEIS CHAVES EM SEIS POSICOES se espalham certinho em 1,54% dos casos")
    void espalharEhRaro() {
        assertEquals(720, Medidas.balde(1).casos());
        assertEquals(0.0154, Medidas.balde(1).fracao(), 0.0001);

        /* 720 e seis fatorial: uma chave por balde, em qualquer ordem. */
        assertEquals(720, 6 * 5 * 4 * 3 * 2 * 1);
    }

    @Test
    @DisplayName("e o maior balde chega a tres ou mais em mais de um terco das vezes")
    void baldeGrandeEhComum() {
        assertEquals(0.367, Medidas.fracaoComTresOuMais(), 0.002);

        assertTrue(Medidas.fracaoComTresOuMais() > 1.0 / 3);

        /* O caso mais comum e o maior balde ter dois. */
        assertEquals(0.6173, Medidas.balde(2).fracao(), 0.0001);
    }

    @Test
    @DisplayName("e as fracoes somam um, sem sobra")
    void fracoesSomam() {
        double soma = 0;

        for (Medidas.Balde b : Medidas.baldes()) soma += b.fracao();

        assertEquals(1.0, soma, 1e-9);
    }

    @Test
    @DisplayName("NUMA TABELA DE 1024 a colisao passa de metade com 3,7% de ocupacao")
    void ocupacaoBaixa() {
        assertEquals(0.037, Medidas.ocupacaoNaMetade(), 0.001);

        /* Com 128 chaves em 1024 posicoes, 12,5% de ocupacao, ja e praticamente certa. */
        double com128 = Medidas.tabela().stream()
                .filter(x -> x.chaves() == 128).findFirst().orElseThrow().chanceDeColisao();

        assertTrue(com128 > 0.999, "deu " + com128);
    }
}
