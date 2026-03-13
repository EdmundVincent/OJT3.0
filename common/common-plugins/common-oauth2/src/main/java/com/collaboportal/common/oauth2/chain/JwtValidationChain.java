package com.collaboportal.common.oauth2.chain;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.handler.JwtValidationHandler;

/**
 * JWT検証チェーン処理器、連結リストを使用して責任の鎖パターンを実装
 * 
 * 連結リスト実装の利点：
 * 1. 動的サイズ：実行時に動的にハンドラーを追加・削除でき、固定サイズの事前割り当てが不要
 * 2. メモリ効率：実際に必要なノードのみを割り当て、配列の空間無駄遣いを回避
 * 3. 挿入効率：末尾への新しいハンドラー追加の時間計算量はO(1)
 * 4. 拡張性：条件付きハンドラーや並列処理分岐を簡単に挿入可能
 * 5. 順序保証：追加順序に厳密に従ってハンドラーを実行し、責任の鎖パターンの期待動作に合致
 * 6. デバッグ容易：各ノードが独立しており、特定のハンドラーを単独でテスト・デバッグしやすい
 */
public class JwtValidationChain {

    /**
     * 内部ノードクラス、ハンドラーと次のノードへの参照をカプセル化
     */
    private static class HandlerNode {
        JwtValidationHandler handler;
        HandlerNode next;
        
        HandlerNode(JwtValidationHandler handler) {
            this.handler = handler;
            this.next = null;
        }
    }

    // 連結リストの先頭ノード、最初のハンドラーを指す
    private HandlerNode head;
    // 連結リストの末尾ノード、効率的な新しいハンドラー追加に使用
    private HandlerNode tail;

    /**
     * ハンドラーをチェーンの末尾に追加
     * 時間計算量：O(1)
     * 
     * @param handler 追加するJWT検証ハンドラー
     * @return チェーンインスタンス、流暢な呼び出しをサポート
     */
    public JwtValidationChain addHandler(JwtValidationHandler handler) {
        HandlerNode newNode = new HandlerNode(handler);
        
        if (head == null) {
            // 最初のノードは同時に先頭と末尾
            head = newNode;
            tail = newNode;
        } else {
            // 新しいノードを末尾に連結し、末尾ポインターを更新
            tail.next = newNode;
            tail = newNode;
        }
        
        return this;
    }

    /**
     * 順序に従って検証チェーン全体を実行
     * 連結リストを走査し、各ハンドラーを順次呼び出し
     * 
     * @param context OAuth2プロバイダーコンテキスト
     * @return すべてのハンドラーが成功した場合はtrue、いずれかのハンドラーが失敗した場合はfalse
     */
    public boolean execute(OAuth2ProviderContext context) {
        HandlerNode current = head;
        while (current != null) {
            if (!current.handler.handle(context)) {
                // 短絡実行：いずれかのハンドラーが失敗した場合、即座に戻る
                return false;
            }
            current = current.next;
        }
        return true;
    }

    /**
     * チェーンが空かどうかを確認
     * 
     * @return チェーンにハンドラーがない場合はtrue
     */
    public boolean isEmpty() {
        return head == null;
    }

}
