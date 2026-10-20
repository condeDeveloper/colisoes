# Colisão de hash

A chance de duas coisas caírem no mesmo lugar, em Java 21, com a conta exata em frações de números grandes e conferida contra a contagem de todos os casos.

É o problema do aniversário e é o problema de toda tabela de espalhamento. Numa tabela de 1.024 posições, a colisão passa de metade com **3,7% de ocupação**.

```
mvn -q exec:java -Dexec.args=tabela
```

## Por que frações exatas

Com 365 dias e 50 pessoas, o produto que dá a chance tem mais de cem casas decimais. Acumular arredondamento ali é fácil, silencioso, e o resultado continua parecendo razoável.

Tudo aqui usa `BigInteger`, e a conta é conferida contra a varredura de todas as `m^n` distribuições possíveis, que não usa fórmula nenhuma.

## As medidas

### 1. A conta exata contra a contagem de todos os casos

| posições | chaves | conta exata | força bruta | casos |
|---:|---:|---:|---:|---:|
| 4 | 3 | 0,625000 | 0,625000 | 64 |
| 5 | 4 | 0,808000 | 0,808000 | 625 |
| 6 | 4 | 0,722222 | 0,722222 | 1.296 |

Zero divergências em 25 combinações.

### 2. O problema do aniversário

| pessoas | chance |
|---:|---:|
| 10 | 0,1169 |
| 22 | 0,4757 |
| **23** | **0,5073** |
| 40 | 0,8912 |
| 50 | 0,9704 |
| **57** | **0,9901** |

O 23 é o número famoso. O que quase ninguém sabe é que ir de metade a quase certeza custa só **34 pessoas a mais**.

A chance não sobe devagar depois do meio: ela desaba para o teto.

### 3. A regra da raiz quadrada, e onde ela erra

| posições | exato | pela raiz | erro |
|---:|---:|---:|---:|
| 10 | 5 | 3,7 | **-25,5%** |
| 100 | 13 | 11,8 | -9,4% |
| 365 | 23 | 22,5 | -2,2% |
| 10.000 | 119 | 117,7 | -1,1% |
| 1.000.000 | 1.178 | 1.177,4 | -0,1% |

A regra de bolso diz que para 50% bastam `1,1774 × √m`. Ela **subestima sempre**, em todos os tamanhos medidos.

E erra mais justamente em tabela **pequena**, que é onde alguém faz a conta de cabeça em vez de rodar o número.

### 4. O maior balde, com seis chaves em seis posições

| maior balde | casos | fração |
|---:|---:|---:|
| 1 | 720 | **1,54%** |
| 2 | 28.800 | 61,73% |
| 3 | 14.700 | 31,51% |
| 4 | 2.250 | 4,82% |
| 5 | 180 | 0,39% |
| 6 | 6 | 0,01% |

A intuição é que seis chaves em seis posições se espalham. Das 46.656 distribuições, a que espalha certinho acontece em **1,54%**.

E o maior balde chega a três ou mais em **36,7%** das vezes. Numa tabela de espalhamento esse é o custo da pior busca, e ele não se parece com a média.

### 5. Quando a colisão vira certeza numa tabela

| posições | chaves | ocupação | chance de colisão |
|---:|---:|---:|---:|
| 1.024 | 16 | 1,6% | 0,1111 |
| 1.024 | **38** | **3,7%** | **0,5010** |
| 1.024 | 64 | 6,3% | 0,8661 |
| 1.024 | 128 | 12,5% | 0,9997 |

Quem decide crescer a tabela pela **ocupação** está olhando o número errado. Com 12,5% de ocupação a colisão é praticamente certa, e 12,5% não parece cheio para ninguém.

Colisão não é sinal de tabela cheia. É o estado normal dela.

## O que as medidas me corrigiram

**A regra da raiz quadrada é ruim exatamente onde é usada de cabeça.** Ela converge bonito para tabelas grandes, onde ninguém precisa dela, e erra 25% com dez posições, onde alguém faria a conta mentalmente.

**Ir de 50% a 99% custa pouco.** São 23 pessoas para metade e 57 para quase certeza. A curva não é suave: depois do joelho ela sobe quase reta, e isso muda o que significa "é improvável" num sistema que roda milhões de vezes.

**Chaves não se espalham.** Seis em seis dão uma por balde em 1,54% dos casos. A imagem mental de distribuição uniforme descreve a média, e a média não é o que dói.

## Rodar

```bash
mvn -q verify                                # 17 testes
mvn -q exec:java                             # todas as medidas
mvn -q exec:java -Dexec.args=correcao        # 1
mvn -q exec:java -Dexec.args=aniversario     # 2
mvn -q exec:java -Dexec.args=raiz            # 3
mvn -q exec:java -Dexec.args=baldes          # 4
mvn -q exec:java -Dexec.args=tabela          # 5
```

Java 21, sem dependência fora do JUnit. Nenhum número deste README foi digitado à mão: todos saem de `Medidas`, e os testes conferem os que aparecem escritos aqui.
