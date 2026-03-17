package com.collaboportal.common.Router;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.Function;
import com.collaboportal.common.funcs.ParamFunction;
import com.collaboportal.common.funcs.ParamRetFunction;
import java.util.List;

public class CommonRouter {
    private CommonRouter() {}
    public static boolean isMatch(String pattern, String path) {
        return ConfigManager.getCommonContext().matchPath(pattern, path);
    }
    public static boolean isMatch(List<String> patterns, String path) {
        if (patterns == null) return false;
        for (String pattern : patterns) {
            if (isMatch(pattern, path)) return true;
        }
        return false;
    }
    public static boolean isMatch(String[] patterns, String path) {
        if (patterns == null) return false;
        for (String pattern : patterns) {
            if (isMatch(pattern, path)) return true;
        }
        return false;
    }
    public static boolean isMatchCurrURI(String pattern) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        return isMatch(pattern, currPath);
    }
    public static boolean isMatchCurrURI(List<String> patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        return isMatch(patterns, currPath);
    }
    public static boolean isMatchCurrURI(String[] patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        return isMatch(patterns, currPath);
    }
    public static CommonRouterStaff newMatch() { return new CommonRouterStaff(); }
    public static CommonRouterStaff match(String... patterns) { return new CommonRouterStaff().match(patterns); }
    public static CommonRouterStaff notMatch(String... patterns) { return new CommonRouterStaff().notMatch(patterns); }
    public static CommonRouterStaff match(List<String> patterns) { return new CommonRouterStaff().match(patterns); }
    public static CommonRouterStaff notMatch(List<String> patterns) { return new CommonRouterStaff().notMatch(patterns); }
    public static CommonRouterStaff match(boolean flag) { return new CommonRouterStaff().match(flag); }
    public static CommonRouterStaff notMatch(boolean flag) { return new CommonRouterStaff().notMatch(flag); }
    public static CommonRouterStaff match(ParamRetFunction<Object, Boolean> fun) { return new CommonRouterStaff().match(fun); }
    public static CommonRouterStaff notMatch(ParamRetFunction<Object, Boolean> fun) { return new CommonRouterStaff().notMatch(fun); }
    public static CommonRouterStaff match(String pattern, Function fun) { return new CommonRouterStaff().match(pattern, fun); }
    public static CommonRouterStaff match(String pattern, ParamFunction<CommonRouterStaff> fun) { return new CommonRouterStaff().match(pattern, fun); }
    public static CommonRouterStaff match(String pattern, String excludePattern, Function fun) { return new CommonRouterStaff().match(pattern, excludePattern, fun); }
    public static CommonRouterStaff match(String pattern, String excludePattern, ParamFunction<CommonRouterStaff> fun) { return new CommonRouterStaff().match(pattern, excludePattern, fun); }
}
