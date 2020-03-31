package de.md.swaggerunit.adapter;

import de.md.swaggerunit.usage.SwaggerUnitRule;

/**
 * this interface could be used to connect a http client (used in your junit test) with the {@link SwaggerUnitRule}. The rule
 * calles firstly the validate method and after the test is finished the afterValidation method.
 *
 * @author dgoermann
 */
public interface SwaggerUnitAdapter {

	/**
	 * Implement this, to return the response and request informations for swaggerunit to validate.
	 */
	SwaggerUnitValidation[] getSwaggerUnitValidations();

}
