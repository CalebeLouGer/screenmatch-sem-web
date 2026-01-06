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
    private Scanner scanner = new Scanner(System.in);
    private final String URL = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=5397d6b1";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    public void exibirMenu(){

        System.out.println("Informe o nome da Série para busca: ");
        var nomeSerie = scanner.nextLine();
        var json = consumoApi.obterDados(URL + nomeSerie.replace(" ","+") + API_KEY);

        System.out.println("---------------------------------------------------------");
        System.out.println("--- Dados da Série ---");
        var dadosSerie = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);
        System.out.println("---------------------------------------------------------");


        System.out.println("--- Dados das Temporadas da Série ---");
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoApi.obterDados(URL + nomeSerie.replace(" ","+")+ "&season="+ i + API_KEY);
            var dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
        System.out.println("---------------------------------------------------------");
// #####################################################
// Mostrar os Episódios, o de baixo faz a mesma coisa que o de cima.
//        for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println("Título: "+episodiosTemporada.get(j).titulo());
//            }
//        }
//        System.out.println("---------------------------------------------------------");

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
// #####################################################
//  Mostrando o uso do Peek()
//        System.out.println("Top 10 de Episodios");
//        List<DadosEpisodio> dadosEpisodioList = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        dadosEpisodioList.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro fitro (N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação | \n" + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limitando | \n" + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Transformando | \n" + e))
//                .forEach(System.out::println);
//        System.out.println("---------------------------------------------------------");


        System.out.println("--- Top 10 de Episodios ---");
        List<DadosEpisodio> dadosEpisodioList = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        dadosEpisodioList.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(10)
                .map(e -> e.titulo().toUpperCase())
                .forEach(System.out::println);
        System.out.println("---------------------------------------------------------");



        System.out.println("--- Dados dos Episodios das Temporadas da Série ---");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.temporada(), d))
                ).collect(Collectors.toList());
        episodios.forEach(System.out::println);
        System.out.println("---------------------------------------------------------");

        System.out.println("Informe o Título do Episódio para pesquisa: ");
        var trechoTitulo = scanner.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();

        if(episodioBuscado.isPresent()){
            System.out.println("Episódio Encontrado!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        }else{
            System.out.println("Episódio não Encontrado!");
        }
        System.out.println("---------------------------------------------------------");

        System.out.println("--- Avaliações por Temporada ---");
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);
        System.out.println("---------------------------------------------------------");


        System.out.println("--- Estatísticas da Série ---");
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        String mensagemEstatistica = String.format("""
                Média: %.1f
                Melhor Episódio: %.1f
                Pior Episódio: %.1f
                Quantidade de Episódios Avaliados: %d""",est.getAverage(),est.getMax(),est.getMin(),est.getCount());
        System.out.println(mensagemEstatistica);
        System.out.println("---------------------------------------------------------");
//
        System.out.println("A partir de que ano deseja ver os episodios: ");
        var ano = scanner.nextInt();
        scanner.nextLine();

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataBusca = LocalDate.of(ano,1,1);
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " | Data de Lançamento: " + e.getDataLancamento().format(formatador) +
                                " | Episódio: " + e.getTitulo()
                ));
        System.out.println("---------------------------------------------------------");
    }
}
