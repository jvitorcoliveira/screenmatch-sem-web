package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();

    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=8af8650d";

    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu(){

        System.out.println("Digite o nome da série para buscar");
        var nomeSerie = leitura.nextLine();

        var json = consumo.obterDados(ENDERECO+nomeSerie.replace(" ", "+")+API_KEY);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

        //System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for(int i = 1 ; i<=dados.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO+nomeSerie.replace(" ", "+")+"&season="+i+API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        //temporadas.forEach(System.out::println);


//        for(int i = 0; i<dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        // temporadas.forEach(temporada -> temporada.episodios().forEach(episodio -> System.out.println(episodio.titulo())));

        //só .toList() gera uma lista imutável
        // o .collect(Collectors.toList()) gera uma lista que pode ser alterada

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 episódios.");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro(N/A): "+ e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação: "+ e))
//                .limit(10)
//                .peek(e -> System.out.println("Limit: "+ e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Map: "+ e))
//                .forEach(System.out::println);


        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);


/*

O que é o Optional?

O Optional é um único valor de container introduzido no Java 8.
Ele pode conter um valor único ou nenhum valor.
O principal uso do Optional é fornecer um tipo de
retorno alternativo quando um metodo pode não retornar um valor.
Usar null é uma prática comum, mas pode levar a erros como NullPointerException.
O Optional ajuda a evitar esses erros fornecendo uma maneira mais segura
de lidar com valores que podem ou não estar presentes.
*/

/*
Melhores práticas

Embora o Optional seja um aliado útil, há algumas coisas que devem ser levadas em consideração para usá-lo de maneira eficaz:

    Prefira o retorno Optional em vez de retornar null: Isso torna suas intenções claras e evita erros.
    Não use Optional.get() sem Optional.isPresent(): O Optional.get() lançará um erro se o valor não estiver presente. Portanto, é melhor verificar antes se o valor está presente.
    Não use Optional para campos da classe ou parâmetros do metodo: O Optional deve ser usado principalmente para retornos de métodos que podem não ter valor.

Entender e usar corretamente o Optional pode levar a um código mais limpo e menos propenso a erros, então vale a pena investir seu tempo para aprender.
 */

//        System.out.println("Digite o nome ou trecho do episódio!");
//        var trechoTitulo = leitura.nextLine();
//        //Em buscas de API o retorno pode ser um option : Pode ser que eu encontre, pode ser que não.
//         Optional<Episodio> episodioBuscado = episodios.stream() // em casos de retornar nulo, o optinoal é uma alternativa para não lidar com isso.
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst(); // Operação final retorna um resultado
//
//         if(episodioBuscado.isPresent()){
//             System.out.println("Episódio encontrado");
//             System.out.println("Temporada: "+episodioBuscado.get().getTemporada());
//         }else{
//             System.out.println("Episódio não encontrado.");
//         }

//
//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1,1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e->
//                        (e.getDataLancamento()!=null && e.getDataLancamento().isAfter(dataBusca))
//                ).forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada()+
//                                " Episódio: "+ e.getTitulo() +
//                                " Data de lançamento: "+ e.getDataLancamento().format(formatador)
//                ));




    Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
            .filter(e -> e.getAvaliacao()>0.0)
            .collect(Collectors.groupingBy(Episodio::getTemporada,
                    Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

    //Gerando estatísticas
    DoubleSummaryStatistics est = episodios.stream()
            .filter(e -> e.getAvaliacao() > 0.0)
            .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: "+est.getAverage());
        System.out.println("Melhor episódio: "+est.getMax());
        System.out.println("Pior episódio: "+est.getMin());
        System.out.println("Quantidade: "+est.getCount());











//        List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");
//
//        nomes.stream().sorted().limit(3)
//                .filter(n -> n.startsWith("N"))
//                .map(n->n.toUpperCase())
//                .forEach(System.out::println);

//        List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//
//        List<Integer> numerosPares = numeros.stream()
//                .filter(n -> n % 2 == 0)
//                .collect(Collectors.toList());
//
//        System.out.println(numerosPares); // Output: [2, 4, 6, 8, 10]


    }
}
