package de.md.swaggerunit.adapter;

import de.md.swaggerunit.usage.SwaggerUnitRule;
import de.md.swaggerunit.usage.ValidationScope;

/**
 * this interface could be used to connect a http client (used in your junit test) with the {@link SwaggerUnitRule}. The rule
 * calles firstly the validate method and after the test is finished the afterValidation method.
 *
 * @author dgoermann
 */
public interface SwaggerUnitAdapter {

	/**
	 * Implement this, to return the request information. SwaggerUnit needs this to validate.
	 *
	 * @return
	 */
	RequestDto getRequest();

	/**
	 * Implement this, to return the response information. SwaggerUnit needs this to validate.
	 * @return
	 */
	ResponseDto getResponse();

}
