package dev.tributino.ChefIA.controller;

import dev.tributino.ChefIA.model.FoodItem;
import dev.tributino.ChefIA.service.FoodItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @GetMapping
    public ResponseEntity<List<FoodItem>> listar() {
        List<FoodItem> itens = foodItemService.listar();
        return ResponseEntity.ok(itens);
    }

    @PostMapping
    public ResponseEntity<FoodItem> criar(@RequestBody FoodItem foodItem) {
        FoodItem salvo = foodItemService.salvar(foodItem);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> atualizar(@PathVariable Long id, @RequestBody FoodItem foodItem) {
        FoodItem atualizado = foodItemService.atualizar(id, foodItem);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        foodItemService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}