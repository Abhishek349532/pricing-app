package com.sapient.pricing.app.pricingapp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
public class PricingController {
	
	@RequestMapping(value="/getPrice/{productId}",method=RequestMethod.GET,produces="application/json")
        @HystrixCommand(fallbackMethod = "pricingFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
	public long getPrice(@PathVariable String productId) {
		return createPrice(productId);
	}
	
	private long createPrice(String productId) {
		switch (productId) {
		case "Pro1":
			return 35000;
		case "Pro2":
			return 25000;
		case "Pro3":
			return 85000;

		default:
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
		}
		
	}
        
        public long pricingFallback(@PathVariable String productId) {
		return -1;
	}
	
	@ExceptionHandler(value=HttpClientErrorException.class)
	public Map<String,String> handleError(HttpClientErrorException ex,HttpServletResponse response) {
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put("errorMessage",ex.getMessage());
		/*StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String stackTrace = sw.toString();
		errorMap.put("errorStackTrace", stackTrace);*/
		response.setStatus(ex.getStatusCode().value());
		return errorMap;
	}

}
