
package com.collaboportal.common.spring;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SpringMVCUtil {

	private SpringMVCUtil() {
	}

	/**
	 * 現在のセッションのrequestを取得します
	 * 
	 * @return request
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (servletRequestAttributes == null) {
			throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
		}
		return servletRequestAttributes.getRequest();
	}

	/**
	 * 現在のセッションのresponseを取得します
	 * 
	 * @return response
	 */
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (servletRequestAttributes == null) {
			throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
		}
		return servletRequestAttributes.getResponse();
	}

	/**
	 * 現在Webコンテキスト内にいるかどうかを判定します
	 * 
	 * @return request
	 */
	public static boolean isWeb() {
		return RequestContextHolder.getRequestAttributes() != null;
	}

}
