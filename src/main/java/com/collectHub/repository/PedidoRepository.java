package com.collectHub.repository;

import com.collectHub.domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository
        extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuarioIdUsuarioAndEmpresaIdEmpresaOrderByFechaPedidoDesc(
            Integer idUsuario, Integer idEmpresa);

    List<Pedido> findByEmpresaIdEmpresaOrderByFechaPedidoDesc(
            Integer idEmpresa);

    Optional<Pedido> findByIdPedidoAndUsuarioIdUsuarioAndEmpresaIdEmpresa(
            Integer idPedido, Integer idUsuario, Integer idEmpresa);

    Optional<Pedido> findByIdPedidoAndEmpresaIdEmpresa(
            Integer idPedido, Integer idEmpresa);
}