package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(@JsonAlias("Title") String titulo,
                            @JsonAlias("Episode") Integer numeroEpisodio,
                            @JsonAlias("imdbRating") String avaliacao,
                            @JsonAlias("Released") String dataDeLancamento) {

    @Override
    public String toString() {
        return "Data de Lançamento: " + dataDeLancamento + " | " +
                "Número: " + numeroEpisodio + " | " +
                "Avaliação: " + avaliacao + " | " +
                "Título: " + titulo ;
    }
}