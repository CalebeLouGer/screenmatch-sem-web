package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonAlias("Genre") String genero,
                         @JsonAlias("Actors") String atores,
                         @JsonAlias("Plot") String sinopse,
                         @JsonAlias("Poster") String poster) {

    @Override
    public String toString() {
        return "Título: " + titulo + '\n' +
                "Temporadas: " + totalTemporadas + '\n' +
                "Avaliação: " + avaliacao + '\n' +
                "Gênero: " + genero + '\n' +
                "Atores: " + atores + '\n' +
                "Sinopse: " + sinopse + '\n' +
                "Pôster: " + poster;
    }
}
