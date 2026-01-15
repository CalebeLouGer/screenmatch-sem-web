package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=5397d6b1";
    private List<DadosSerie> dadosSeriesList = new ArrayList<>();

    private SerieRepository repository;
    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0){
            var menu = """
                    ==================================
                    [1] Buscar por Dados da Série
                    [2] Buscar por Dados dos Epsódios da Série
                    [3] Lista Séries Buscadas
                    [4] Buscar Série por Título
                    [5] Buscar Série por Ator
                    [6] Top 5 Séries
                    [7] Buscar por Categoria
                    
                    [0] Sair
                    ==================================""";

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSerieBuscada();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarPorTopCincoSeries();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o NOME da série para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSerieBuscada();
        System.out.println("Escolha uma Série pelo Nome: ");
        var serieBuscada = leitura.nextLine();

        Optional<Serie> serieOptional = repository.findByTituloContainingIgnoreCase(serieBuscada);

        if(serieOptional.isPresent()){
            var serieEncontrada = serieOptional.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodioList = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.temporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodioList(episodioList);
            repository.save(serieEncontrada);
        }else {
            System.out.println("Série não Encontrada!");
        }

    }

    private void listarSerieBuscada(){
        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma Série pelo Nome: ");
        var serieBuscada = leitura.nextLine();
        Optional<Serie> serieOptional = repository.findByTituloContainingIgnoreCase(serieBuscada);

        if(serieOptional.isPresent()){
            System.out.println("Dados da Série: \n" + serieOptional.get());
        }else {
            System.out.println("Série não encontrada!");
        }
    }
    private void buscarSeriePorAtor() {
        System.out.println("Informe o Nome do Ator para Busca: ");
        var nomeAtor = leitura.nextLine();
        System.out.println("Informe a partir de que Valor deseja as avaliações: ");
        var avaliacaoFiltro = leitura.nextDouble();
        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor,avaliacaoFiltro);
        System.out.println("Séries em que o " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }

    private void buscarPorTopCincoSeries(){
        List<Serie> topSeries = repository.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(s ->
                System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }
    private void buscarSeriePorCategoria(){
        System.out.println("Informe a Categoria que deseja buscar: ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromString(nomeGenero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Séries da Categoria: " + nomeGenero);
        seriesPorCategoria.forEach(s ->
                System.out.println("Avaliação: " + s.getAvaliacao() + " | Categoria: " + s.getGenero() + " | " + s.getTitulo()));
    }
}