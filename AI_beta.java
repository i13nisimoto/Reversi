import java.util.Random;
public class AI_beta {
  // 深読みするレベル（大きい値だとものすごい時間がかかってしまうので注意）
  private static int SEARCH_LEVEL = 7;
  // メインパネルへの参照
  public static Board board;
  // 盤面の各場所の価値
  static final int valueOfPlace1[][] = {
    {120, -50, 20,  10,  10, 20, -50, 120},
    {-50, -90, -5, -5, -5, -5, -90, -50},
    { 20,  -5, 30,  5,  5, 30,  -5,  20},
    {  10,  -5,  5,  0,  0,  5,  -5,   10},
    {  10,  -5,  5,  0,  0,  5,  -5,   10},
    { 20,  -5, 30,  5,  5, 30,  -5,  20},
    {-50, -90, -5, -5, -5, -5, -90, -50},
    {120, -50, 20,  10,  10, 20, -50, 120}
  };
  // static final int valueOfPlace1[][] = {
  //   {120, -40, 20,  0,  0, 20, -40, 120},
  //   {-40, -80, 0, 0, 0, 0, -80, -40},
  //   { 20,  0, 0,  0,  0, 0,  0,  20},
  //   {  0,  0,  0,  0,  0,  0,  0,   0},
  //   {  0,  0,  0,  0,  0,  0,  0,   0},
  //   { 20,  0, 0,  0,  0, 0,  0,  20},
  //   {-40, -80, 0, 0, 0, 0, -80, -40},
  //   {120, -40, 20,  0,  0, 20, -40, 120}
  // };
  static final int valueOfPlace2[][] = {
    {150, 20, 20,  5,  5, 20, 20, 150},
    {20, 40, -5, -5, -5, -5, 40, 20},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {  5,  -5,  3,  0,  0,  3,  -5,   5},
    {  5,  -5,  3,  0,  0,  3,  -5,   5},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {20, 40, -5, -5, -5, -5, 40, 20},
    {150, 20, 20,  5,  5, 20, 20, 150}
  };

  public static int turn;
  static Random rnd = new Random();
  //AIの手番


  /**
  * コンストラクタ。メインパネルへの参照を保存。
  *
  * @param panel メインパネルへの参照。
  */
  public AI_beta(Board board,int turn,int searchLevel) {
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
    int temp = simpleAlphaBeta(true, SEARCH_LEVEL, Integer.MAX_VALUE);

    // 場所を求める
    int x = temp % 8;
    int y = temp / 8;
    long pos=Reversi.PosTranslate(x,y);
    return pos;
  }


  /**
  * 簡易α-β法。最善手を探索する。打つ場所を探すだけで実際には打たない。
  *
  * @param flag AIの手番のときtrue、プレイヤーの手番のときfalse。
  * @param level 先読みの手数。
  * @param beta β値。このノードの評価値は必ずβ値以下となる。
  * @return 子ノードでは盤面の評価値。ルートノードでは最大評価値を持つ場所（bestX + bestY * MAS）。
  */
    public int simpleAlphaBeta(boolean flag, int level, int beta){

    // ノードの評価値
    int value;

    // 子ノードから伝播してきた評価値
    int childValue;

    // 現時点での最大評価値
    // 求めた最大の評価値を持つ場所
    int bestX = 0;
    int bestY = 0;

    // ゲーム木の末端では盤面評価
    // その他のノードはMIN or MAXで伝播する
    if (level == 0) {
      return valueBoard(board,turn);
    }

    if (flag) {
      // 自分の手番では最小値をセットしておく -無限大<評価値
      value = -Integer.MAX_VALUE;
    } else {
      // 相手の手番では最大値をセットしておく 評価値<無限大
      value = Integer.MAX_VALUE;
    }

    // パスの場合は評価値の変化なし
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
          // 試しに打ってみる
          Reversi.Put(board,pos);
          // 子ノードの評価値を計算（再帰）
          // 今度は相手の番なのでflagが逆転する
          childValue = simpleAlphaBeta(!flag, level - 1, beta);
          // 子ノードとこのノードの評価値を比較する
          if (flag) {
            // AIのノードなら子ノードの中で最大の評価値を選ぶ
            if (childValue > value) {
              value = childValue;
              bestX = x;
              bestY = y;
            }
            // このノードの現在のvalueが受け継いだβ値より大きかったら
            // この枝が選ばれることはないのでこれ以上評価しない
            // = forループをぬける
            // βカット
            if (value > beta) {
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
