package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.hibernate.dialect.SelectItemReferenceStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repository;

    private List<SerieDTO> converteDadosSerie(List<Serie> series){
        return series.stream()
                .map(s -> new SerieDTO(
                        s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getAvaliacao(),
                        s.getGenero(),
                        s.getAtores(),
                        s.getSinopse(),
                        s.getPoster()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDadosSerie(repository.findAll());
    }
    public List<SerieDTO> obterTop5Serie() {
        return converteDadosSerie(repository.findTop5ByOrderByAvaliacaoDesc());
    }
    public List<SerieDTO> obterLancamentos() {
        return converteDadosSerie(repository.encontrarEpisodiosMaisRecentes());
    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(),
                    s.getTitulo(),
                    s.getTotalTemporadas(),
                    s.getAvaliacao(),
                    s.getGenero(),
                    s.getAtores(),
                    s.getSinopse(),
                    s.getPoster());
        }
        return null;
    }
}
