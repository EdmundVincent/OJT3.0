
package com.collaboportal.common.Router;

import java.util.List;

import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.Function;
import com.collaboportal.common.funcs.ParamFunction;
import com.collaboportal.common.funcs.ParamRetFunction;


public class CommonRouterStaff {

	/**
	 * マッチしたかどうかのフラグ変数
	 */
	public boolean isHit = true;
	
	/**
	 * @return マッチしたかどうか
	 */
	public boolean isHit() {
		return isHit;
	}

	/**
	 * @param isHit マッチフラグ
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff setHit(boolean isHit) {
		this.isHit = isHit;
		return this;
	}

	/**
	 * マッチフラグをtrueにリセット
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff reset() {
		this.isHit = true;
		return this;
	}
	
	
	// ----------------- パスのマッチング
	
	/**
	 * ルーティングマッチング
	 * @param patterns ルーティングパターン配列
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff match(String... patterns) {
		if(isHit)  {
			isHit = CommonRouter.isMatchCurrURI(patterns);
		}
		return this;
	}

	/**
	 * ルーティングマッチング除外
	 * @param patterns 除外するルーティングパターン配列
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff notMatch(String... patterns) {
		if(isHit)  {
			isHit = !CommonRouter.isMatchCurrURI(patterns);
		}
		return this;
	}

	/**
	 * ルーティングマッチング
	 * @param patterns ルーティングパターンリスト
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff match(List<String> patterns) {
		if(isHit)  {
			isHit = CommonRouter.isMatchCurrURI(patterns);
		}
		return this;
	}

	/**
	 * ルーティングマッチング除外
	 * @param patterns 除外するルーティングパターンリスト
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff notMatch(List<String> patterns) {
		if(isHit)  {
			isHit = !CommonRouter.isMatchCurrURI(patterns);
		}
		return this;
	}





	// ----------------- 条件マッチング

	/**
	 * boolean値でマッチング
	 * @param flag boolean値
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff match(boolean flag) {
		if(isHit)  {
			isHit = flag;
		}
		return this;
	}

	/**
	 * boolean値でマッチング除外
	 * @param flag boolean値
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff notMatch(boolean flag) {
		if(isHit)  {
			isHit = !flag;
		}
		return this;
	}
	
	/**
	 * カスタムメソッドでマッチング (lazy)
	 * @param fun カスタムメソッド
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff match(ParamRetFunction<Object, Boolean> fun) {
		if(isHit)  {
			isHit = fun.run(this);
		}
		return this;
	}

	/**
	 * カスタムメソッドでマッチング除外 (lazy)
	 * @param fun 除外用カスタムメソッド
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff notMatch(ParamRetFunction<Object, Boolean> fun) {
		if(isHit)  {
			isHit = !fun.run(this);
		}
		return this;
	}
	
	
	// ----------------- 関数検証実行

	/**
	 * 検証関数を実行（引数なし）
	 * @param fun 実行する関数
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff check(Function fun) {
		if(isHit)  {
			fun.run();
		}
		return this;
	}
	
	/**
	 * 検証関数を実行（引数あり）
	 * @param fun 実行する関数
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff check(ParamFunction<CommonRouterStaff> fun) {
		if(isHit)  {
			fun.run(this);
		}
		return this;
	}
	
	/**
	 * フリーマッチング（freeスコープ内でstop()を実行してもAuth関数から抜けず、freeブロックだけ抜ける）
	 * @param fun 実行する関数
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff free(ParamFunction<CommonRouterStaff> fun) {
		if(isHit)  {
			try {
				fun.run(this);
			} catch (StopMatchException e) {
				// freeフリーマッチングブロックから抜ける
			}
		}
		return this;
	}

	
	// ----------------- 直接check関数指定
	
	/**
	 * ルーティングマッチング、マッチした場合は認証関数を実行
	 * @param pattern ルーティングパターン
	 * @param fun 実行する検証メソッド
	 * @return /
	 */
	public CommonRouterStaff match(String pattern, Function fun) {
		return this.match(pattern).check(fun);
	}

	/**
	 * ルーティングマッチング、マッチした場合は認証関数を実行
	 * @param pattern ルーティングパターン
	 * @param fun 実行する検証メソッド
	 * @return /
	 */
	public CommonRouterStaff match(String pattern, ParamFunction<CommonRouterStaff> fun) {
		return this.match(pattern).check(fun);
	}

	/**
	 * ルーティングマッチング（除外パターン指定）、マッチした場合は認証関数を実行
	 * @param pattern ルーティングパターン
	 * @param excludePattern 除外するルーティングパターン
	 * @param fun 実行するメソッド
	 * @return /
	 */
	public CommonRouterStaff match(String pattern, String excludePattern, Function fun) {
		return this.match(pattern).notMatch(excludePattern).check(fun);
	}

	/**
	 * ルーティングマッチング（除外パターン指定）、マッチした場合は認証関数を実行
	 * @param pattern ルーティングパターン
	 * @param excludePattern 除外するルーティングパターン
	 * @param fun 実行するメソッド
	 * @return /
	 */
	public CommonRouterStaff match(String pattern, String excludePattern, ParamFunction<CommonRouterStaff> fun) {
		return this.match(pattern).notMatch(excludePattern).check(fun);
	}
	
	
	// ----------------- 早期終了

	/**
	 * マッチングを停止し、関数から抜ける（複数のマッチングチェーンで一度にAuth関数から抜ける）
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff stop() {
		if(isHit) {
			throw new StopMatchException();
		}
		return this;
	}

	/**
	 * マッチングを停止し、実行を終了し、フロントエンドに結果を返す
	 * @return オブジェクト自身
	 */
	public CommonRouterStaff back() {
		if(isHit) {
			throw new BackResultException("");
		}
		return this;
	}
	
	/**
	 * マッチングを停止し、実行を終了し、フロントエンドに結果を返す
	 * @return オブジェクト自身
	 * @param result 出力する結果
	 */
	public CommonRouterStaff back(Object result) {
		if(isHit) {
			throw new BackResultException(result);
		}
		return this;
	}

	
}
