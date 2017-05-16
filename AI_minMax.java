import java.util.Random;
public class AI_minMax {
  // 深読みするレベル（大きい値だとものすごい時間がかかってしまうので注意）
  private static int SEARCH_LEVEL = 6;
  // メインパネルへの参照
  public static Board board;
  // 盤面の各場所の価値
  static final int valueOfPlace1[][] = {
    { 100,   0,  30,  15,  15,  30,   0, 100},
    {   0, -60, -10, -30, -30, -10, -60,   0},
    {  30, -10,  15,   5,   5,  15, -10,  30},
    {  15, -30,   5,   0,   0,   5, -30,  15},
    {  15, -30,   5,   0,   0,   5, -30,  15},
    {  30, -10,  15,   5,   5,  15, -10,  30},
    {   0, -60, -10, -30, -30, -10, -60,   0},
    { 100,   0,  30,  15,  15,  30,   0, 100}
  };
  static final int valueOfPlace2[][] = {
    { 150,  80,  30,  15,  15,  30,  80, 150},
    {  80,  60, -10, -30, -30, -10,  60,  80},
    {  30, -10,  15,   5,   5,  15, -10,  30},
    {  15, -30,   5,   0,   0,   5, -30,  15},
    {  15, -30,   5,   0,   0,   5, -30,  15},
    {  30, -10,  15,   5,   5,  15, -10,  30},
    {  80,  60, -10, -30, -30, -10,  60,  80},
    { 150,  80,  30,  15,  15,  30,  80, 150}
  };

  public static int turn;
  static Random rnd = new Random();

  //AIの手番
  /**
  * コンストラクタ。メインパネルへの参照を保存。
  *
  * @param panel メインパネルへの参照。
  */
  public AI_minMax(Board board,int turn,int searchLevel) {
    this.board = board;
    this.turn=turn;
    SEARCH_LEVEL=searchLevel;
  }

  /**
  * コンピュータの手を決定する。
  *
  */
  public long compute() {
    // α-β法で石を打つ場所を決める
    // 戻ってくる値は bestX+bestY*MASU
    int temp = minMax(true, SEARCH_LEVEL);

    // 場所を求める
    int x = temp % 8;
    int y = temp / 8;
    long pos=Reversi.PosTranslate(x,y);
    return pos;
  }

  /**
  * Min-Max法。最善手を探索する。打つ場所を探すだけで実際には打たない。
  *
  * @param flag AIの手番のときtrue、プレイヤーの手番のときfalse。
  * @param level 先読みの手数。
  * @return 子ノードでは盤面の評価値。ルートノードでは最大評価値を持つ場所（bestX + bestY * MAS）。
  */

  public int minMax(boolean flag, int level) {
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
          Reversi.Put(board,pos);
          // ひっくり返す（盤面描画はしないのでtrue指定）
          // panel.reverse(undo, true);
          // 手番を変える
          // panel.nextTurn();
          // 子ノードの評価値を計算（再帰）
          // 今度は相手の番なのでflagが逆転する
          childValue = minMax(!flag, level - 1);
          // 子ノードとこのノードの評価値を比較する
          if (flag) {
            // AIのノードなら子ノードの中で最大の評価値を選ぶ
            if (childValue > value) {
              value = childValue;
              bestX = x;
              bestY = y;
            }
          } else {
            // プレイヤーのノードなら子ノードの中で最小の評価値を選ぶ
            if (childValue < value) {
              value = childValue;
              bestX = x;
              bestY = y;
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
    if(board.move_num<35){
      for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
          // 置かれた石とその場所の価値をかけて足していく
          value += Reversi.getDiscColor(x, y,board) * valueOfPlace1[x][y];
        }
      }
      return value*turn;
    }
    else if(board.move_num>35&&board.move_num<55){
      for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
          // 置かれた石とその場所の価値をかけて足していく
          value += Reversi.getDiscColor(x, y,board) * valueOfPlace2[x][y];
        }
      }
      return value*turn;
    }
    else{
      value = (int)Reversi.NumOfStone(board.black)-(int)Reversi.NumOfStone(board.white);
      return -value*turn;
    }
  }
}
