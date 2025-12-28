package com.power.springdemo.api.controller;

import com.power.springdemo.domain.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class AsyncApiController {

	private final PaymentService paymentService;

	public AsyncApiController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@GetMapping("/{id}")
	public String process(@PathVariable String id) {
		return paymentService.processPayment(id);
	}
}
