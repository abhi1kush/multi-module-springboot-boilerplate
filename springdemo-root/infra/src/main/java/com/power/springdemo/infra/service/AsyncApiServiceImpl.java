package com.power.springdemo.infra.service;

import com.power.springdemo.domain.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class AsyncApiServiceImpl implements PaymentService {

	public String processPayment(String paymentId) {
		return "Payment processed for ID: " + paymentId;
	}
}
