package com.collaboportal.common.utils;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class PathPatternParserUtil {
    public static boolean match(String pattern, String path) {
		PathPattern pathPattern = PathPatternParser.defaultInstance.parse(pattern);
		PathContainer pathContainer = PathContainer.parsePath(path);
		return pathPattern.matches(pathContainer);
    }

}
