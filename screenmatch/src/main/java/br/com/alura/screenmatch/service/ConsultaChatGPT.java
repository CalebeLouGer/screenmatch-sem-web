// Tentativa de usar a API do ChatGptOpenAI, não funcionou por conta da versão antiga que estava tentando usar.

//package br.com.alura.screenmatch.service;
//
//import com.theokanning.openai.completion.CompletionRequest;
//import com.theokanning.openai.service.OpenAiService;
//
//public class ConsultaChatGPT {
//    public static String obterTraducao(String texto) {
//        OpenAiService service = new OpenAiService("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");
//
//        CompletionRequest requisicao = CompletionRequest.builder()
//                .model("gpt-3.5-turbo-instruct")
//                .prompt("traduza para o português o texto: " + texto)
//                .maxTokens(1000)
//                .temperature(0.7)
//                .build();
//
//        var resposta = service.createCompletion(requisicao);
//        return resposta.getChoices().get(0).getText();
//    }
//}
//
//
// Tentativa de usar a API do ChatGPTOpenAI, uma versão mais nova mas não tive tempo para fazer a requisição e optei por outra API

package br.com.alura.screenmatch.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsultaChatGPT {

    private static final String API_URL = "https://api.openai.com/v1/responses";
    private static final String API_KEY = System.getenv("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");

    public static String obterTraducao(String texto) {

        String json = """
        {
          "model": "gpt-5.2",
          "input": "Traduza para o português o texto: %s"
        }
        """.formatted(texto);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar OpenAI", e);
        }
    }
}