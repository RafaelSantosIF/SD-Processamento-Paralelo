# Sistemas Distribuídos - Processamento Paralelo

## Introdução
O programa `ParallelSubsetSum` tem como objetivo encontrar subconjuntos de um conjunto de números que somem a um valor alvo. A implementação utiliza paralelismo para melhorar a eficiência da busca, dividindo a carga de trabalho entre múltiplas threads.

## Estrutura do Código

### Importação de Bibliotecas
O programa utiliza as seguintes bibliotecas:
- `java.io.*`: Para manipulação de arquivos.
- `java.util.*`: Para manipulação de listas e arrays.
- `java.util.concurrent.*`: Para implementar concorrência e paralelismo.
- `java.util.concurrent.atomic.AtomicInteger`: Para manter a contagem total de subconjuntos encontrados de forma segura em um ambiente multithread.

### Funções Principais

#### 1. `generateRandomTarget()`
Gera um número aleatório entre 100 e 1000 para ser utilizado como soma alvo dos subconjuntos.

#### 2. `readNumbersFromFile(String filePath)`
Lê um conjunto de números de um arquivo e os armazena em um array de inteiros. Os números estão separados por espaço na linha lida do arquivo.

#### 3. `SubsetCounter`
Classe interna que implementa `Callable<Integer>`. Essa classe executa a contagem de subconjuntos em uma região específica do array de números, permitindo a execução concorrente.

O método `countSubsets()` executa a busca recursiva para contar subconjuntos cuja soma seja igual ao valor alvo.

#### 4. `findSubsetCount(int[] numbers, int target, int numThreads)`
Esta função é responsável por dividir o problema entre múltiplas threads:
- Cria um `ExecutorService` com `numThreads` threads.
- Divide o array de números em segmentos para cada thread processar.
- Envia as tarefas para execução concorrente.
- Obtém os resultados das threads e soma os subconjuntos encontrados.
- Implementa um tempo limite de execução para evitar que o cálculo fique preso indefinidamente.

#### 5. `main(String[] args)`
- Lê os números de um arquivo chamado `inst5000a.txt`.
- Gera um valor alvo aleatório.
- Executa o algoritmo variando o número de threads de 1 a 4.
- Mede e imprime o tempo de execução e o número de subconjuntos encontrados para cada configuração de threads.

## Funcionamento do Algoritmo
O algoritmo implementa uma busca exaustiva para encontrar subconjuntos cuja soma seja igual ao valor alvo. Como esse problema pertence à classe NP-completo, a solução ingênua teria complexidade exponencial. Para mitigar esse problema, o programa usa paralelismo para dividir a carga de trabalho entre múltiplas threads.

Cada thread processa um segmento do array de entrada e calcula, de forma independente, a quantidade de subconjuntos dentro de seu intervalo. Os resultados são acumulados de maneira segura usando `AtomicInteger`.

A implementação garante que todas as combinações possíveis sejam testadas, respeitando a ordem dos elementos e garantindo que cada subconjunto seja contado uma única vez.

## Conclusão
O programa `ParallelSubsetSum` utiliza paralelismo para acelerar a busca por subconjuntos que somam a um valor alvo. Apesar de a complexidade do problema ser alta, a divisão da carga entre threads reduz o tempo de execução. No entanto, para conjuntos muito grandes, a abordagem ainda pode ser ineficiente, sendo necessárias técnicas mais avançadas como poda de busca ou programação dinâmica para otimização adicional.

