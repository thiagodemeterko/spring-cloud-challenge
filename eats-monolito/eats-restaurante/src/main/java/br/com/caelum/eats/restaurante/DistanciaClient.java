package br.com.caelum.eats.restaurante;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("distancia")
public interface DistanciaClient {

    @PostMapping("/restaurantes")
    ResponseEntity<RestauranteDistanciaDto> adiciona(@RequestBody RestauranteDistanciaDto restaurante);

    @PutMapping("/restaurantes/{id}")
    RestauranteDistanciaDto atualiza(@PathVariable("id") Long id, @RequestBody RestauranteDistanciaDto restaurante);

}
