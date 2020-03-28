package de.md.swaggerunit.adapter.spring.base;

public class SimpleDto {

	private String simpleField;

	public SimpleDto() {
		simpleField = "simple test string";
	}

	public SimpleDto(String simpleField) {
		this.simpleField = simpleField;
	}

	public String getSimpleField() {
		return simpleField;
	}

	public void setSimpleField(String simpleField) {
		this.simpleField = simpleField;
	}
}
