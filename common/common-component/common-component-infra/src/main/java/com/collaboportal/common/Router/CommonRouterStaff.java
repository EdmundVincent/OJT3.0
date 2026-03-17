package com.collaboportal.common.Router;

import java.util.List;
import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.Function;
import com.collaboportal.common.funcs.ParamFunction;
import com.collaboportal.common.funcs.ParamRetFunction;

public class CommonRouterStaff {
    public boolean isHit = true;
    public boolean isHit() { return isHit; }
    public CommonRouterStaff setHit(boolean isHit) { this.isHit = isHit; return this; }
    public CommonRouterStaff reset() { this.isHit = true; return this; }
    public CommonRouterStaff match(String... patterns) { if (isHit) isHit = CommonRouter.isMatchCurrURI(patterns); return this; }
    public CommonRouterStaff notMatch(String... patterns) { if (isHit) isHit = !CommonRouter.isMatchCurrURI(patterns); return this; }
    public CommonRouterStaff match(List<String> patterns) { if (isHit) isHit = CommonRouter.isMatchCurrURI(patterns); return this; }
    public CommonRouterStaff notMatch(List<String> patterns) { if (isHit) isHit = !CommonRouter.isMatchCurrURI(patterns); return this; }
    public CommonRouterStaff match(boolean flag) { if (isHit) isHit = flag; return this; }
    public CommonRouterStaff notMatch(boolean flag) { if (isHit) isHit = !flag; return this; }
    public CommonRouterStaff match(ParamRetFunction<Object, Boolean> fun) { if (isHit) isHit = fun.run(this); return this; }
    public CommonRouterStaff notMatch(ParamRetFunction<Object, Boolean> fun) { if (isHit) isHit = !fun.run(this); return this; }
    public CommonRouterStaff check(Function fun) { if (isHit) fun.run(); return this; }
    public CommonRouterStaff check(ParamFunction<CommonRouterStaff> fun) { if (isHit) fun.run(this); return this; }
    public CommonRouterStaff free(ParamFunction<CommonRouterStaff> fun) {
        if (isHit) {
            try { fun.run(this); } catch (StopMatchException e) {}
        }
        return this;
    }
    public CommonRouterStaff match(String pattern, Function fun) { return this.match(pattern).check(fun); }
    public CommonRouterStaff match(String pattern, ParamFunction<CommonRouterStaff> fun) { return this.match(pattern).check(fun); }
    public CommonRouterStaff match(String pattern, String excludePattern, Function fun) { return this.match(pattern).notMatch(excludePattern).check(fun); }
    public CommonRouterStaff match(String pattern, String excludePattern, ParamFunction<CommonRouterStaff> fun) { return this.match(pattern).notMatch(excludePattern).check(fun); }
    public CommonRouterStaff stop() { if (isHit) throw new StopMatchException(); return this; }
    public CommonRouterStaff back() { if (isHit) throw new BackResultException(""); return this; }
    public CommonRouterStaff back(Object result) { if (isHit) throw new BackResultException(result); return this; }
}
