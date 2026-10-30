package dev.tributino.ChefIA.controller;

import dev.tributino.ChefIA.model.FoodItem;
import dev.tributino.ChefIA.service.ChatGptService;
import dev.tributino.ChefIA.service.FoodItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class RecipeController {

    private final ChatGptService chatGptService;
    private final FoodItemService foodItemService;

    public RecipeController(ChatGptService chatGptService, FoodItemService foodItemService) {
        this.chatGptService = chatGptService;
        this.foodItemService = foodItemService;
    }

    @GetMapping("/generate")
    public Mono<ResponseEntity<String>> generateRecipe() {
        List<FoodItem> foodItemList = foodItemService.listar();
        return chatGptService.generateRecipe(foodItemList)
                .map(recipe -> ResponseEntity.ok(recipe))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}