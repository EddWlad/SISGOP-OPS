package com.tidsec.sisgop_backend.dto.enums;

public enum ContractorReceiptState {
    PENDIENTE,        // despacho aún no confirmado por el contratista
    RECIBIDO,         // recibido conforme
    RECIBIDO_NOVEDADES // recibido con novedades (faltantes/sobrantes/daños)
}
