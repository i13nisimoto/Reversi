import java.util.Random;
public class AI {
  // 深読みするレベル（大きい値だとものすごい時間がかかってしまうので注意）
  private static final int SEARCH_LEVEL = 7;
  // メインパネルへの参照
  public static Board board;
  // 盤面の各場所の価値
  static final int valueOfPlace[][] = {
    {120, -20, 20,  5,  5, 20, -20, 120},
    {-20, -40, -5, -5, -5, -5, -40, -20},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {  5,  -5,  3,  3,  3,  3,  -5,   5},
    {  5,  -5,  3,  3,  3,  3,  -5,   5},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {-20, -40, -5, -5, -5, -5, -40, -20},
    {120, -20, 20,  5,  5, 20, -20, 120}
  };

  public static int turn;
  static Random rnd = new Random();
  //AIの手番


  /**
  * コンストラクタ。メインパネルへの参照を保存。
  *
  * @param panel メインパネルへの参照。
  */
  public AI(Board board,int turn) {
    this.board = board;
    this.turn=turn;
  }

  /**
  * コンピュータの手を決定する。
  *
  */
  public long compute() {
    // α-β法で石を打つ場所を決める
    // 戻ってくる値は bestX+bestY*MASU
    int temp = alphaBeta(true, SEARCH_LEVEL, Integer.MIN_VALUE, Integer.MAX_VALUE);

    // 場所を求める
    int x = temp % 8;
    int y = temp / 8;
    long pos=Reversi.PosTranslate(x,y);
    return pos;
    // 打った場所、ひっくり返した石の位置を記録
    // Undo undo = new Undo(x, y);
    // // その場所に実際に石を打つ
    // panel.putDownStone(x, y, false);
    // // 実際にひっくり返す
    // panel.reverse(undo, false);
    // // 終了したか調べる
    // if (panel.endGame()) return;
    // // 手番を変える
    // panel.nextTurn();
    // if (panel.countCanPutDownStone() == 0) {
    //   System.out.println("Player PASS!");
    //   panel.nextTurn();
    //   compute();
  }


  // /**
  // * Min-Max法。最善手を探索する。打つ場所を探すだけで実際には打たない。
  // *
  // * @param flag AIの手番のときtrue、プレイヤーの手番のときfalse。
  // * @param level 先読みの手数。
  // * @return 子ノードでは盤面の評価値。ルートノードでは最大評価値を持つ場所（bestX + bestY * MAS）。
  // */
  // private int minMax(boolean flag, int level) {
  //   // ノードの評価値
  //   int value;
  //   // 子ノードから伝播してきた評価値
  //   int childValue;
  //   // Min-Max法で求めた最大の評価値を持つ場所
  //   int bestX = 0;
  //   int bestY = 0;
  //
  //   // ゲーム木の末端では盤面評価
  //   // その他のノードはMIN or MAXで伝播する
  //   if (level == 0) {
  //     return valueBoard();
  //   }
  //
  //   if (flag) {
  //     // AIの手番では最大の評価値を見つけたいので最初に最小値をセットしておく
  //     value = Integer.MIN_VALUE;
  //   } else {
  //     // プレイヤーの手番では最小の評価値を見つけたいので最初に最大値をセットしておく
  //     value = Integer.MAX_VALUE;
  //   }
  //
  //   // もしパスの場合はそのまま盤面評価値を返す
  //   if (panel.countCanPutDownStone() == 0) {
  //     return valueBoard();
  //   }
  //
  //   // 打てるところはすべて試す（試すだけで実際には打たない）
  //   for (int y = 0; y < MainPanel.MASU; y++) {
  //     for (int x = 0; x < MainPanel.MASU; x++) {
  //       if (panel.canPutDown(x, y)) {
  //         Undo undo = new Undo(x, y);
  //         // 試しに打ってみる（盤面描画はしないのでtrue指定）
  //         panel.putDownStone(x, y, true);
  //         // ひっくり返す（盤面描画はしないのでtrue指定）
  //         panel.reverse(undo, true);
  //         // 手番を変える
  //         panel.nextTurn();
  //         // 子ノードの評価値を計算（再帰）
  //         // 今度は相手の番なのでflagが逆転する
  //         childValue = minMax(!flag, level - 1);
  //         // 子ノードとこのノードの評価値を比較する
  //         if (flag) {
  //           // AIのノードなら子ノードの中で最大の評価値を選ぶ
  //           if (childValue > value) {
  //             value = childValue;
  //             bestX = x;
  //             bestY = y;
  //           }
  //         } else {
  //           // プレイヤーのノードなら子ノードの中で最小の評価値を選ぶ
  //           if (childValue < value) {
  //             value = childValue;
  //             bestX = x;
  //             bestY = y;
  //           }
  //         }
  //         // 打つ前に戻す
  //         panel.undoBoard(undo);
  //       }
  //     }
  //   }
  //
  //   if (level == SEARCH_LEVEL) {
  //     // ルートノードなら最大評価値を持つ場所を返す
  //     return bestX + bestY * MainPanel.MASU;
  //   } else {
  //     // 子ノードならノードの評価値を返す
  //     return value;
  //   }
  // }

  /**
  * α-β法。最善手を探索する。打つ場所を探すだけで実際には打たない。
  *
  * @param flag AIの手番のときtrue、プレイヤーの手番のときfalse。
  * @param level 先読みの手数。
  * @param alpha α値。このノードの評価値は必ずα値以上となる。
  * @param beta β値。このノードの評価値は必ずβ値以下となる。
  * @return 子ノードでは盤面の評価値。ルートノードでは最大評価値を持つ場所（bestX + bestY * MAS）。
  */
  public int alphaBeta(boolean flag, int level, int alpha, int beta) {
    // ノードの評価値
    int value;
    // 子ノードから伝播してきた評価値
    int childValue;
    // Min-Max法で求めた最大の評価値を持つ場所
    int bestX = 0;
    int bestY = 0;

    // ゲーム木の末端では盤面評価
    // その他のノードはMIN or MAXで伝播する
    if (level == 0) {
      return valueBoard(board,turn);
    }

    if (flag) {
      // AIの手番では最大の評価値を見つけたいので最初に最小値をセットしておく
      value = Integer.MIN_VALUE;
    } else {
      // プレイヤーの手番では最小の評価値を見つけたいので最初に最大値をセットしておく
      value = Integer.MAX_VALUE;
    }

    // もしパスの場合はそのまま盤面評価値を返す
    if (Reversi.CheckPass(board) == 1) {
      return valueBoard(board,turn);
    }

    // 打てるところはすべて試す（試すだけで実際には打たない）
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        long pos=Reversi.PosTranslate(x,y);
        if (((Reversi.GenValidMove(board)&pos)!=0)) {
          // Undo undo = new Undo(x, y);
          Board cloneBoard=Reversi.clone(board);
          // 試しに打ってみる（盤面描画はしないのでtrue指定）
          // panel.putDownStone(x, y, true);
          Reversi.Put(board,pos);
          // // ひっくり返す（盤面描画はしないのでtrue指定）
          // panel.reverse(undo, true);
          // // 手番を変える
          // panel.nextTurn();
          // 子ノードの評価値を計算（再帰）
          // 今度は相手の番なのでflagが逆転する
          childValue = alphaBeta(!flag, level - 1, alpha, beta);
          // 子ノードとこのノードの評価値を比較する
          if (flag) {
            // AIのノードなら子ノードの中で最大の評価値を選ぶ
            if (childValue > value) {
              value = childValue;
              // α値を更新
              alpha = value;
              bestX = x;
              bestY = y;
            }
            // このノードの現在のvalueが受け継いだβ値より大きかったら
            // この枝が選ばれることはないのでこれ以上評価しない
            // = forループをぬける
            if (value > beta) {  // βカット
              //                            System.out.println("βカット");
              // 打つ前に戻す
              board=cloneBoard;
              return value;
            }
          } else {
            // プレイヤーのノードなら子ノードの中で最小の評価値を選ぶ
            if (childValue < value) {
              value = childValue;
              // β値を更新
              beta = value;
              bestX = x;
              bestY = y;
            }
            // このノードのvalueが親から受け継いだα値より小さかったら
            // この枝が選ばれることはないのでこれ以上評価しない
            // = forループをぬける
            if (value < alpha) {  // αカット
              //                            System.out.println("αカット");
              // 打つ前に戻す
              board=cloneBoard;
              return value;
            }
          }
          // 打つ前に戻す
          board=cloneBoard;
        }
      }
    }

    if (level == SEARCH_LEVEL) {
      // ルートノードなら最大評価値を持つ場所を返す
      return bestX + bestY * 8;
    } else {
      // 子ノードならノードの評価値を返す
      return value;
    }
  }
  /**
  * 評価関数。盤面を評価して評価値を返す。盤面の場所の価値を元にする。
  *
  * @return 盤面の評価値。
  */
  static int valueBoard(Board board,int turn) {
    int value = 0;
    //int rand=rnd.nextInt();

    for (int x = 0; x < 8; x++) {
      for (int y = 0; y < 8; y++) {
        // 置かれた石とその場所の価値をかけて足していく
        value += Reversi.getDiscColor(x, y,board) * valueOfPlace[x][y];
      }
    }


    return value*turn;
  }

  //   if(board.move_num>58){
  //     int value = (int)Reversi.NumOfStone(board.black)-(int)Reversi.NumOfStone(board.white);
  //
  //
  //     return -value*turn;
  //  }
  //   else{
  //     //System.out.println("着手可能数が多い方を選ぶ");
  //     int value=0;
  //     for(int i=0;i<8;i++){
  //       for(int j=0;j<8;j++){
  //         long pos=Reversi.PosTranslate(i,j);
  //         if (((Reversi.GenValidMove(board)&pos)!=0)) {
  //           value++;
  //         }
  //       }
  //     }
  //     return -value*turn;
  //   }

}
