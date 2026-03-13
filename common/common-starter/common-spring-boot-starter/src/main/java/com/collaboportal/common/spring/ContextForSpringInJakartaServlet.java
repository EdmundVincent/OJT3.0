package com.collaboportal.common.spring;

import com.collaboportal.common.context.CommonContext;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.servlet.RequestForServlet;
import com.collaboportal.common.servlet.ResponseForServlet;
import com.collaboportal.common.utils.PathPatternParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextForSpringInJakartaServlet implements CommonContext {

	private static final Logger logger = LoggerFactory.getLogger(ContextForSpringInJakartaServlet.class);

	/**
	 * 現在のリクエストのRequestラッパーオブジェクトを取得します
	 */
	@Override
	public BaseRequest getRequest() {
		return new RequestForServlet(SpringMVCUtil.getRequest());
	}

	/**
	 * 現在のリクエストのResponseラッパーオブジェクトを取得します
	 */
	@Override
	public BaseResponse getResponse() {
		return new ResponseForServlet(SpringMVCUtil.getResponse());
	}

	/**
	 * 指定したルーティングパターンが指定したパスにマッチするかどうかを判定します
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		return PathPatternParserUtil.match(pattern, path);
	}

	/**
	 * 今回のリクエストでこのコンテキストが有効かどうかを判定します
	 */
	@Override
	public boolean isValid() {
		return SpringMVCUtil.isWeb();
	}

}
