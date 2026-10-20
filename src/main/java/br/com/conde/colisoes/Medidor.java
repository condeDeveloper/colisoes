package br.com.conde.colisoes;

import java.util.Locale;

/** O MEDIDOR: roda as contas e imprime as tabelas do README. */
public final class Medidor {

    private Medidor() {}

    public static void main(String[] argumentos) {
        Locale.setDefault(Locale.US);

        String passo = argumentos.length > 0 ? argumentos[0] : "tudo";

        switch (passo) {
            case "correcao" -> correcao();
            case "aniversario" -> aniversario();
            case "raiz" -> raiz();
            case "baldes" -> baldes();
            case "tabela" -> tabela();
            default -> {
                correcao();
                aniversario();
                raiz();
                baldes();
                tabela();
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("=".repeat(texto.length()));
        System.out.println();
    }

    static void correcao() {
        titulo("1. a conta exata contra a contagem de todos os casos");

        System.out.println("posicoes  chaves  conta exata  forca bruta        casos");

        for (Medidas.Correcao c : Medidas.correcao())
            if (c.posicoes() >= 4 && c.chaves() <= 4)
                System.out.printf("%8d  %6d  %11.6f  %11.6f  %11d%s%n", c.posicoes(),
                        c.chaves(), c.exato(), c.porForcaBruta(), c.casos(),
                        c.batem() ? "" : "  <<< DIFERE");

        System.out.printf("%ndivergencias: %d%n", Medidas.divergencias());

        System.out.println();
        System.out.println("a conta exata e um produto de fracoes. a forca bruta gera TODAS as");
        System.out.println("m elevado a n maneiras de distribuir as chaves e conta quantas tem");
        System.out.println("colisao, sem usar formula nenhuma.");
        System.out.println();
        System.out.println("e as fracoes sao exatas, em numero grande: com 365 dias e 50 pessoas");
        System.out.println("o produto tem mais de cem casas, e arredondar ali erra calado.");
    }

    static void aniversario() {
        titulo("2. o problema do aniversario");

        System.out.println("pessoas  chance de duas no mesmo dia");

        for (Medidas.Aniversario a : Medidas.aniversario())
            System.out.printf("%7d  %26.4f%n", a.pessoas(), a.chance());

        System.out.printf("%npara passar de 50%%: %d pessoas%n", Medidas.paraMetade());
        System.out.printf("para passar de 99%%: %d pessoas%n", Medidas.paraNoventaENove());

        System.out.println();
        System.out.println("o 23 e o numero famoso. o que quase ninguem sabe e que ir de metade");
        System.out.printf("a quase certeza custa so %d pessoas a mais.%n",
                Medidas.paraNoventaENove() - Medidas.paraMetade());
        System.out.println();
        System.out.println("a chance nao sobe devagar depois do meio: ela desaba para o teto.");
    }

    static void raiz() {
        titulo("3. a regra da raiz quadrada, e onde ela erra");

        System.out.println("posicoes  exato  pela raiz  erro");

        for (Medidas.Raiz r : Medidas.raiz())
            System.out.printf("%8d  %5d  %9.1f  %+.1f%%%n", r.posicoes(), r.exato(),
                    r.aproximado(), r.erroPorCento());

        System.out.printf("%nsubestima em todos os tamanhos: %s%n",
                Medidas.sempreSubestima() ? "sim" : "NAO");

        System.out.println();
        System.out.println("a regra: para 50% de chance bastam 1,1774 vezes a raiz do numero de");
        System.out.println("posicoes. ela vem de aproximar o produto por uma exponencial.");
        System.out.println();
        System.out.println("ela erra mais justamente em tabela PEQUENA, que e onde alguem faz a");
        System.out.printf("conta de cabeca: com 10 posicoes ela erra %.1f%%.%n",
                Medidas.raiz(10).erroPorCento());
    }

    static void baldes() {
        titulo("4. o maior balde, com seis chaves em seis posicoes");

        System.out.println("maior balde  casos   fracao");

        for (Medidas.Balde b : Medidas.baldes())
            System.out.printf("%11d  %5d  %6.2f%%%n", b.maior(), b.casos(), 100 * b.fracao());

        System.out.printf("%ncada chave num lugar diferente: %.2f%% dos casos%n",
                100 * Medidas.balde(1).fracao());
        System.out.printf("maior balde com TRES ou mais:   %.2f%%%n",
                100 * Medidas.fracaoComTresOuMais());

        System.out.println();
        System.out.println("a intuicao e que seis chaves em seis posicoes se espalham. das");
        System.out.println("46.656 distribuicoes, a que espalha certinho acontece em 1,54%.");
        System.out.println();
        System.out.println("numa tabela de espalhamento o maior balde e o custo da PIOR busca,");
        System.out.println("e ele nao se parece com a media.");
    }

    static void tabela() {
        titulo("5. quando a colisao vira certeza numa tabela");

        System.out.println("posicoes  chaves  ocupacao  chance de colisao");

        for (Medidas.Carga c : Medidas.tabela())
            System.out.printf("%8d  %6d  %7.1f%%  %16.4f%n", c.posicoes(), c.chaves(),
                    100 * c.ocupacao(), c.chanceDeColisao());

        System.out.printf("%na colisao passa de metade com ocupacao de so %.1f%%.%n",
                100 * Medidas.ocupacaoNaMetade());

        System.out.println();
        System.out.println("quem decide crescer a tabela pela OCUPACAO esta olhando o numero");
        System.out.println("errado: com 1024 posicoes e 38 chaves, 3,7% de ocupacao, a colisao");
        System.out.println("ja e mais provavel que nao.");
        System.out.println();
        System.out.println("colisao nao e sinal de tabela cheia. e o estado normal dela.");
    }
}
