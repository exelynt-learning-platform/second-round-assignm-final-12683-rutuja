package com.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500)
    private String shippingAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String shippingCity;

    @Size(max = 100)
    private String shippingState;

    @NotBlank(message = "ZIP code is required")
    @Size(max = 20)
    private String shippingZipCode;

    @NotBlank(message = "Country is required")
    @Size(max = 50)
    private String shippingCountry;

    @Size(max = 500)
    private String notes;
}