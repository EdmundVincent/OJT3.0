package com.collaboportal.common.interceptor;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.ParamFunction;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

	public ParamFunction<Object> auth = handler -> {
	};

	public Function<Object, Void> handleFunction;

	public AuthInterceptor() {
	}

	public AuthInterceptor(ParamFunction<Object> auth) {
		this.auth = auth;
	}

	public AuthInterceptor(Function<Object, Void> handleFunction) {
		this.handleFunction = handleFunction;
	}

	public AuthInterceptor setAuth(ParamFunction<Object> auth) {
		this.auth = auth;
		return this;
	}

	@Override
	@SuppressWarnings("all")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		try {
			auth.run(handler);

		} catch (StopMatchException e) {
			logger.warn("認証に失敗しました", e);
			CookieUtil.setNoneSameSiteCookie(CommonHolder.getResponse(), Message.Cookie.MOVE_URL, "/#/error");
		} catch (BackResultException e) {
			logger.warn("認証に失敗しました", e);
			throw new AuthenticationException("認証に失敗しました");
		}

		return true;
	}
}
