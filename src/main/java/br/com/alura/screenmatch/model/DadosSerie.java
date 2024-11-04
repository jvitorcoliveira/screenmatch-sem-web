package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/*
* @JsonProperty("key") - Tanto serializa quanto desserializa - Pode receber um array de possíveis chaves. Na serialização cria com o mesmo nome
* @JsonAlias("key") - Só serve para desserializar na hora de escrever ele deixa como chave o nome da variavel e não o nome da chave
 * */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora o que você não encontrar
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao) {

}
