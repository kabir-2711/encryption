package com.encryption.advice;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.utilities.log.Log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

/**
 * RequestControllerAdvice extends the {@link RequestBodyAdviceAdapter} to add
 * custom implementation to some of its methods.
 * 
 * <p>
 * This class uses {@code @RestControllerAdvice} to catch the request and caches
 * that in the request parameter.
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestControllerAdvice.html">
 *      RestControllerAdvice</a>
 * @author Kabir Akware
 */
@RestControllerAdvice
@AllArgsConstructor
public class RequestControllerAdvice extends RequestBodyAdviceAdapter {

	/**
	 * {@link HttpServletRequest} parameter
	 */
	private HttpServletRequest request;

	/**
	 * Determines that the request should be cached for all the requests
	 */
	@Override
	public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType,
			@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	/**
	 * The default implementation returns the body that was passed in after caching
	 * the body in request attribute.
	 */
	@Override
	public @NonNull Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage,
			@NonNull MethodParameter parameter, @NonNull Type targetType,
			@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		String methodName = "afterReadBody";
		String refNo = null;

		try {
			JSONObject obj = new JSONObject(body);
			refNo = obj.getString("refNo");
		} catch (JSONException e) {
			Log.warn(this.getClass().getSimpleName(), methodName,
					"Unable to fetch reference number.. setting value as null");
		}

		Log.info(this.getClass().getSimpleName(), methodName, "Caching refNo and request");
		Log.debug(this.getClass().getSimpleName(), methodName, "Caching refNo %s and request %s", refNo, body);
		request.setAttribute("cached-req", body);
		request.setAttribute("ref-no", refNo);
		return body;
	}

}
