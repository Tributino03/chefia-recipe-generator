package dev.tributino.ChefIA.service;

import dev.tributino.ChefIA.model.FoodItem;
import dev.tributino.ChefIA.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public List<FoodItem> listar() {
        return foodItemRepository.findAll();
    }

    public Optional<FoodItem> buscarPorId(Long id) {
        return foodItemRepository.findById(id);
    }

    public FoodItem salvar(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    public FoodItem atualizar(Long id, FoodItem foodItemAtualizado) {
        return foodItemRepository.findById(id).map(foodItem -> {
            foodItem.setNome(foodItemAtualizado.getNome());
            foodItem.setCategoriaReceita(foodItemAtualizado.getCategoriaReceita());
            foodItem.setQuantidade(foodItemAtualizado.getQuantidade());
            foodItem.setValidade(foodItemAtualizado.getValidade());
            return foodItemRepository.save(foodItem);
        }).orElseThrow(() -> new RuntimeException("FoodItem não encontrado com o ID: " + id));
    }

    public void deletar(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new RuntimeException("FoodItem não encontrado com o ID: " + id);
        }
        foodItemRepository.deleteById(id);
    }
}