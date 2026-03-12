package com.collaboportal.common.Router;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.Function;
import com.collaboportal.common.funcs.ParamFunction;
import com.collaboportal.common.funcs.ParamRetFunction;

import java.util.List;

/**
 * 共通ルータークラス
 * ルーティングのマッチングや認証処理を提供します。
 */
public class CommonRouter {

    private CommonRouter() {};

    /**
     * パターンとパスのマッチングを行います。
     * @param pattern ルートパターン
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(String pattern, String path) {
        boolean result = ConfigManager.getCommonContext().matchPath(pattern, path);
        return result;
    }

    /**
     * パターンリストとパスのマッチングを行います。
     * @param patterns ルートパターンリスト
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(List<String> patterns, String path) {
        if(patterns == null) {
            return false;
        }
        for (String pattern : patterns) {
            if(isMatch(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * パターン配列とパスのマッチングを行います。
     * @param patterns ルートパターン配列
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(String[] patterns, String path) {
        if(patterns == null) {
            return false;
        }
        for (String pattern : patterns) {
            if(isMatch(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 現在のリクエストURIとパターンのマッチングを行います。
     * @param pattern ルートパターン
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(String pattern) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        return isMatch(pattern, currPath);
    }  

    /**
     * 現在のリクエストURIとパターンリストのマッチングを行います。
     * @param patterns ルートパターンリスト
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(List<String> patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        return isMatch(patterns, currPath);
    }

    /**
     * 現在のリクエストURIとパターン配列のマッチングを行います。
     * @param patterns ルートパターン配列
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(String[] patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        return isMatch(patterns, currPath);
    }

    /**
     * 新しいマッチングスタッフを生成します。
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff newMatch() {
        return new CommonRouterStaff();
    }

    /**
     * パターン配列でマッチングを行うスタッフを生成します。
     * @param patterns ルートパターン配列
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String... patterns) {
        return new CommonRouterStaff().match(patterns);
    }

    /**
     * パターン配列でマッチしないスタッフを生成します。
     * @param patterns ルートパターン配列
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(String... patterns) {
        return new CommonRouterStaff().notMatch(patterns);
    }

    /**
     * パターンリストでマッチングを行うスタッフを生成します。
     * @param patterns ルートパターンリスト
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(List<String> patterns) {
        return new CommonRouterStaff().match(patterns);
    }

    /**
     * パターンリストでマッチしないスタッフを生成します。
     * @param patterns ルートパターンリスト
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(List<String> patterns) {
        return new CommonRouterStaff().notMatch(patterns);
    }

    /**
     * boolean値でマッチングを行うスタッフを生成します。
     * @param flag マッチフラグ
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(boolean flag) {
        return new CommonRouterStaff().match(flag);
    }

    /**
     * boolean値でマッチしないスタッフを生成します。
     * @param flag マッチフラグ
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(boolean flag) {
        return new CommonRouterStaff().notMatch(flag);
    }

    /**
     * 関数でマッチングを行うスタッフを生成します。
     * @param fun 判定関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(ParamRetFunction<Object, Boolean> fun) {
        return new CommonRouterStaff().match(fun);
    }

    /**
     * 関数でマッチしないスタッフを生成します。
     * @param fun 判定関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(ParamRetFunction<Object, Boolean> fun) {
        return new CommonRouterStaff().notMatch(fun);
    }

    /**
     * パターンと関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, Function fun) {
        return new CommonRouterStaff().match(pattern, fun);
    }

    /**
     * パターンと関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, ParamFunction<CommonRouterStaff> fun) {
        return new CommonRouterStaff().match(pattern, fun);
    }

    /**
     * パターンと除外パターン、関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param excludePattern 除外パターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, String excludePattern, Function fun) {
        return new CommonRouterStaff().match(pattern, excludePattern, fun);
    }

    /**
     * パターンと除外パターン、関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param excludePattern 除外パターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, String excludePattern, ParamFunction<CommonRouterStaff> fun) {
        return new CommonRouterStaff().match(pattern, excludePattern, fun);
    }

    /**
     * マッチングを停止します（例外をスロー）。
     * @return なし
     */
    public static CommonRouterStaff stop() {
        throw new StopMatchException();
    }

    /**
     * マッチングを停止し、結果を返します（例外をスロー）。
     * @return なし
     */
    public static CommonRouterStaff back() {
        throw new BackResultException("");
    }

    /**
     * マッチングを停止し、指定した結果を返します（例外をスロー）。
     * @param result 返却する結果
     * @return なし
     */
    public static CommonRouterStaff back(Object result) {
        throw new BackResultException(result);
    }

}
