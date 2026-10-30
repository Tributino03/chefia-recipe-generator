package dev.tributino.ChefIA.service;

import dev.tributino.ChefIA.model.FoodItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatGptService {

    private final WebClient webClient;

    @Value("${api.key}")
    private String apiKey;

    @Value("${chatgpt.api.url}")
    private String apiUrl;

    public ChatGptService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> generateRecipe(List<FoodItem> foodItems) {

        String alimentos = foodItems.stream()
                .map(item -> String.format("%s (%s) - Quantidade: %d, Validade: %s",
                        item.getNome(),
                        item.getCategoriaReceita(),
                        item.getQuantidade(),
                        item.getValidade()))
                .collect(Collectors.joining("\n"));

        String prompt = "Aqui estão os ingredientes atualmente disponíveis na minha despensa, ordenados dos que vão vencer mais rápido para os com validade mais distante:\n\n" +
                alimentos +
                "\n\nCom base estritamente nesses itens (dando preferência máxima aos que vencem primeiro), crie uma receita saborosa e prática que eu possa preparar agora. Informe o nome da receita, os ingredientes utilizados com suas respectivas quantidades e o passo a passo do modo de preparo.";

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", "Você é um assistente de cozinha profissional, inteligente e criativo, integrado a um sistema de controle de despensa. Seu objetivo é ajudar o usuário a preparar receitas deliciosas utilizando preferencialmente os ingredientes que já estão cadastrados no banco de dados. \n" +
                                "\n" +
                                "Regras fundamentais para a criação das receitas:\n" +
                                "1. Priorize sempre e obrigatoriamente os ingredientes que possuem a data de validade mais próxima do vencimento atual, evitando desperdícios.\n" +
                                "2. Você pode sugerir adicionar ingredientes básicos comuns de despensa que geralmente não faltam em uma cozinha (como sal, óleo, água, azeite, pimenta ou açúcar em pequenas quantidades), mas foque o prato principal nos itens fornecidos.\n" +
                                "3. Seja claro, organizado e objetivo nas instruções de preparo (separando ingredientes com quantidades e o passo a passo numerado).\n" +
                                "4. Adapte-se ao estilo culinário que for solicitado, mantendo um tom encorajador e prático."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        return webClient.post()
                .uri(apiUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return message.get("content").toString();
                    }
                    return "Nenhuma receita foi gerada.";
                });
    }
}