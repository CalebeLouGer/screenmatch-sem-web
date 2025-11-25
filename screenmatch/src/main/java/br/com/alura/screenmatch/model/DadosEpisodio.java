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
        return "\nDadosEpisodio {" +
                "Número do Episodio: " + numeroEpisodio + ", " +
                "Avaliação: " + avaliacao + ", " +
                "Data De Lançamento: " + dataDeLancamento +  ", " +
                "Titulo: " + titulo +
                "}";
    }
}
