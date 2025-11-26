package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;
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
        System.out.println("Dados da Série");
        var dadosSerie = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);
        System.out.println("---------------------------------------------------------");


        System.out.println("---------------------------------------------------------");
        System.out.println("Dados das Temporadas da Série");
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoApi.obterDados(URL + nomeSerie.replace(" ","+")+ "&season="+ i + API_KEY);
            var dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
        System.out.println("---------------------------------------------------------");

//        for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println("Título: "+episodiosTemporada.get(j).titulo());
//            }
//        }

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
        System.out.println("---------------------------------------------------------");
        System.out.println("Top 5 de Episodios");
        List<DadosEpisodio> dadosEpisodioList = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        dadosEpisodioList.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);
        System.out.println("---------------------------------------------------------");

        System.out.println("---------------------------------------------------------");
        System.out.println("Dados dos Episodios das Temporadas da Série");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.temporada(), d))
                ).collect(Collectors.toList());
        episodios.forEach(System.out::println);
        System.out.println("---------------------------------------------------------");


        System.out.println("A partir de que ano deseja ver os episodios: ");
        var ano = scanner.nextInt();
        scanner.nextLine();
        var anoAtual = 2025;
        if (ano < anoAtual){
            LocalDate dataBusca = LocalDate.of(ano,1,1);
            episodios.stream()
                    .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca));
        }else {
            System.out.println("Não passamos deste ano ainda!");
        }

    }
}
