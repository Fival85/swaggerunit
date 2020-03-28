package de.md.swaggerunit.adapter.spring.base;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/test/simpleString")
	public String getSimpleString() {
		return "simple string";
	}

	@GetMapping("/test/simpleObject")
	public SimpleDto getSimpleStringSameClass() {
		return new SimpleDto();
	}
}
