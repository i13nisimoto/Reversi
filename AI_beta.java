import java.util.Random;
public class AI_beta {
  // 深読みするレベル（大きい値だとものすごい時間がかかってしまうので注意）
  private static int SEARCH_LEVEL = 7;
  // メインパネルへの参照
  public static Board board;
  // 盤面の各場所の価値
  static final int valueOfPlace1[][] = {
    {120, -10, 20, 10, 10, 20, -10, 120},
    {-10, -30, -5, -5, -5, -5, -30, -10},
    { 20,  -5, 30,  3,  3, 30,  -5,  20},
    { 10,  -5,  3,  0,  0,  3,  -5,  10},
    { 10,  -5,  3,  0,  0,  3,  -5,  10},
    { 20,  -5, 30,  3,  3, 30,  -5,  20},
    {-10, -30, -5, -5, -5, -5, -30, -10},
    {120, -10, 20, 10, 10, 20, -10, 120}
  };

  static final int valueOfPlace2[][] = {
    {150, 20, 20,  5,  5, 20, 20, 150},
    { 20, 40, -5, -5, -5, -5, 40,  20},
    { 20, -5, 15,  3,  3, 15, -5,  20},
    {  5, -5,  3,  3,  3,  3, -5,   5},
    {  5, -5,  3,  3,  3,  3, -5,   5},
    { 20, -5, 15,  3,  3, 15, -5,  20},
    { 20, 40, -5, -5, -5, -5, 40,  20},
    {150, 20, 20,  5,  5, 20, 20, 150}
  };
  static final int valueOfPlace3[][] = {
    {1, 1, 1, 1, 1, 1, 1, 1},
    {1, 1, 1, 1, 1, 1, 1, 1},
    {1, 1, 1, 1, 1, 1, 1, 1},
    {1, 1, 1, 1, 1, 1, 1, 1},
    {1, 1, 1, 1, 1, 1, 1, 1},
    {1, 1, 1, 1, 1, 1, 1, 1},
    {1, 1, 1, 1, 1, 1, 1, 1},
    {1, 1, 1, 1, 1, 1, 1, 1}
  };
  public static int turn;
  static Random rnd = new Random();

  int cnt = 0;

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


    System.out.println("横:"+x+"縦:"+y);
    long pos=Reversi.PosTranslate(x,y);

    return pos;
  }

  int getNodeCnt(){
    return cnt;
  }
  void delNodeCnt(){
    cnt=0;
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
      cnt++;
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
    int checkFinPass=Reversi.CheckOnlyFinishPassNonShow(board);
    // もしパスの場合はそのまま盤面評価値を返す

    //
    if (checkFinPass== 2) {//全部埋まっているとき
      cnt++;
      return valueBoard(board,turn);
    }else if(checkFinPass==1){//パスするとき
      board.teban*=-1;
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
      System.out.println("探索ノード数:"+cnt);
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
  static boolean f = true;
  static int valueBoard(Board board,int turn) {
    int value = 0;
    long tmp=board.black|board.white;
    //value = kakutei(board,turn);
    //int rand=rnd.nextInt();

    if(board.move_num>=55){
      for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
          // 置かれた石とその場所の価値をかけて足していく
          value += Reversi.getDiscColor(x, y,board) * valueOfPlace3[x][y];
        }
      }
      return value*turn;
    } else if(Reversi.getDiscColor(0, 0,board) != 0 || Reversi.getDiscColor(0, 7,board) != 0 || Reversi.getDiscColor(7, 0,board) != 0 || Reversi.getDiscColor(7, 7,board) != 0){
      value = kakutei(board,turn);
      for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
          // 置かれた石とその場所の価値をかけて足していく
          value += Reversi.getDiscColor(x, y,board) * valueOfPlace2[x][y];
        }
      }
      return value*turn;
    } else {
      for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
          // 置かれた石とその場所の価値をかけて足していく
          value += Reversi.getDiscColor(x, y,board) * valueOfPlace1[x][y];
        }
      }
      return value*turn;
    }
  }
  //@turn 自分黒-1　白1

  static int kakutei(Board board,int turn){
    int value = 0;
    int color = 0;
    int x,y;
    //左上角
    if(((color = Reversi.getDiscColor(0,0,board)))!=0){
      value += color;
      for(int i = 1; i < 8; i++){
        if(Reversi.getDiscColor(0,i,board) == color){
          value += color;
        }
      }
      for (int i = 0; i < 8; i++) {
        if(Reversi.getDiscColor(i,0,board) == color){
          value += color;
        }
      }
    }
    //右上角
    if(((color = Reversi.getDiscColor(7,0,board)))!=0){
      value += color;
      for(int i = 6; i >= 0; i--){
        if(Reversi.getDiscColor(i,0,board) == color){
          value += color;
        }
      }
      for (int i = 1; i < 8; i++) {
        if(Reversi.getDiscColor(7,i,board) == color){
          value += color;
        }
      }
    }
    //左下角
    if(((color = Reversi.getDiscColor(0,7,board)))!=0){
      value += color;
      for(int i = 6; i >= 0; i--){
        if(Reversi.getDiscColor(0,i,board) == color){
          value += color;
        }
      }
      for (int i = 1; i < 8; i++) {
        if(Reversi.getDiscColor(i,7,board) == color){
          value += color;
        }
      }
    }
    //右下角
    if(((color = Reversi.getDiscColor(7,7,board)))!=0){
      value += color;
      for(int i = 6; i >= 0; i--){
        if(Reversi.getDiscColor(i,7,board) == color){
          value += color;
        }
      }
      for (int i = 6; i >= 0; i--) {
        if(Reversi.getDiscColor(7,i,board) == color){
          value += color;
        }
      }
    }
    return turn*value*10;
  }

}
