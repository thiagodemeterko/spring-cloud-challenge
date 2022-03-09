package br.com.caelum.eats.pagamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ClienteRestDoPedido {

    @Autowired
    private PedidoClient pedidoClient;

    void notificaPagamentoDoPedido(Long pedidoId) {

        ResponseEntity<?> response = pedidoClient.atualizaStatus(pedidoId, new PedidoMudancaDeStatusRequest("pago".toUpperCase()));

        if (!HttpStatus.valueOf(response.getStatusCodeValue()).is2xxSuccessful()) {
            throw new RuntimeException("problema ao tentar mudar o status do pedido: " + pedidoId);
        }
    }
}

@Getter
@AllArgsConstructor
class PedidoMudancaDeStatusRequest {
    private String status;
}

@FeignClient("monolito")
interface PedidoClient {

    @PutMapping("/pedidos/{pedidoId}/status")
    ResponseEntity<?> atualizaStatus(@PathVariable Long pedidoId, @RequestBody PedidoMudancaDeStatusRequest pedidoParaAtualizar);
}


