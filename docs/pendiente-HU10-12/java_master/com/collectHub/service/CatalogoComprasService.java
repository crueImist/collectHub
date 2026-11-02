package com.collectHub.service;

import java.math.BigDecimal;

public interface CatalogoComprasService {

    BigDecimal obtenerPrecio(Integer idEmpresa,
                             Integer idProducto);

    void validarDisponibilidad(Integer idEmpresa,
                               Integer idProducto,
                               Integer cantidad);

    void descontarExistencias(Integer idEmpresa,
                              Integer idProducto,
                              Integer cantidad);
}