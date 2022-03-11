package br.com.caelum.eats.restaurante;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
class RestauranteController {

	private static final Logger LOG = LoggerFactory.getLogger(RestauranteController.class);

	private RestauranteRepository restauranteRepo;
	private CardapioRepository cardapioRepo;
	private DistanciaClient distanciaClient;

	@GetMapping("/restaurantes/{id}")
	RestauranteDto detalha(@PathVariable("id") Long id) {
		Restaurante restaurante = restauranteRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		return new RestauranteDto(restaurante);
	}

	@GetMapping("/restaurantes")
	List<RestauranteDto> detalhePorIds(@RequestParam("ids") List<Long> ids) {
		return restauranteRepo.findAllById(ids).stream().map(RestauranteDto::new).collect(Collectors.toList());
	}

	@GetMapping("/parceiros/restaurantes/{id}")
	RestauranteDto detalhaParceiro(@PathVariable("id") Long id) {
		Restaurante restaurante = restauranteRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		return new RestauranteDto(restaurante);
	}

	@PostMapping("/parceiros/restaurantes")
	Restaurante adiciona(@RequestBody Restaurante restaurante) {
		restaurante.setAprovado(false);
		Restaurante restauranteSalvo = restauranteRepo.save(restaurante);
		Cardapio cardapio = new Cardapio();
		cardapio.setRestaurante(restauranteSalvo);
		cardapioRepo.save(cardapio);

		LOG.info("Incluindo restaurante {} no serviço de distância", restaurante.getNome());
		distanciaClient.adiciona(new RestauranteDistanciaDto(restauranteSalvo.getId(), restauranteSalvo.getCep(), restauranteSalvo.getTipoDeCozinha().getId()));
		LOG.info("Incluído restaurante {} no serviço de distância", restaurante.getNome());

		return restauranteSalvo;
	}

	@PutMapping("/parceiros/restaurantes/{id}")
	public RestauranteDto atualiza(@PathVariable("id") Long id, @RequestBody RestauranteDto restaurante) {
		Restaurante doBD = restauranteRepo.getOne(id);
		restaurante.populaRestaurante(doBD);

		LOG.info("Atualizando serviço de distância para o restaurante {}", restaurante.getNome());
		distanciaClient.atualiza(doBD.getId(), new RestauranteDistanciaDto(doBD.getId(), restaurante.getCep(), restaurante.getTipoDeCozinha().getId()));
		LOG.info("Atualizado serviço de distância para o restaurante {}", restaurante.getNome());

		return new RestauranteDto(restauranteRepo.save(doBD));
	}


  @GetMapping("/admin/restaurantes/em-aprovacao")
	List<RestauranteDto> emAprovacao() {
		return restauranteRepo.findAllByAprovado(false).stream().map(RestauranteDto::new)
				.collect(Collectors.toList());
	}

	@Transactional
	@PatchMapping("/admin/restaurantes/{id}")
	public void aprova(@PathVariable("id") Long id) {
		restauranteRepo.aprovaPorId(id);
	}
}
