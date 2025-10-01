package com.seidor.store.exception.my_exceptions;

public class InsufficientStockException extends RuntimeException {

    private final Integer productId;
    private final int requested;
    private final int available;

    public InsufficientStockException(Integer productId, int requested, int available) {
        super("No hay suficiente stock para el producto " + productId +
                ". Solicitado: " + requested + ", disponible: " + available);
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    public Integer getProductId() { return productId; }
    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}