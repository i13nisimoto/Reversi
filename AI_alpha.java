import java.util.Random;
public class AI_alpha {
  // 深読みするレベル（大きい値だとものすごい時間がかかってしまうので注意）
  private static  int SEARCH_LEVEL = 8;
  // メインパネルへの参照
  public static Board board;
  public static int cnt;
  // 盤面の各場所の価値
  static final int valueOfPlace1[][] = {
    {120, 0, 20,  5,  5, 20, 0, 120},
    {0, -40, -5, -5, -5, -5, -40, 0},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {  5,  -5,  3,  3,  3,  3,  -5,   5},
    {  5,  -5,  3,  3,  3,  3,  -5,   5},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {0, -40, -5, -5, -5, -5, -40, 0},
    {120, 0, 20,  5,  5, 20, 0, 120}
  };
  static final int valueOfPlace2[][] = {
    {150, 20, 20,  5,  5, 20, 20, 150},
    {20, 0, -5, -5, -5, -5, 0, 20},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {  5,  -5,  3,  3,  3,  3,  -5,   5},
    {  5,  -5,  3,  3,  3,  3,  -5,   5},
    { 20,  -5, 15,  3,  3, 15,  -5,  20},
    {20, 0, -5, -5, -5, -5, 0, 20},
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




  public AI_alpha(Board board,int turn,int searchLevel) {
    this.board = board;
    this.turn=turn;
    SEARCH_LEVEL=searchLevel;
  }

  /**
  * コンピュータの手を決定する。
  *
  */
  public long compute() {
    int temp = alphaBeta(true, SEARCH_LEVEL, Integer.MIN_VALUE, Integer.MAX_VALUE);

    // 場所を求める
    int x = temp % 8;
    int y = temp / 8;

    System.out.println("横:"+x+"縦:"+y);
    long pos=Reversi.PosTranslate(x,y);

    return pos;
  }


  public int alphaBeta(boolean flag, int level, int alpha, int beta) {
    // ノードの評価値
    int value;
    // 子ノードから伝播してきた評価値
    int childValue;
    // Min-Max法で求めた最大の評価値を持つ場所
    int bestX = 0;
    int bestY = 0;

    if (level == 0) {
      cnt++;
      return valueBoard(board,turn);
    }

    if (flag) {
      // AIの手番では最初に最小値をセットしておく
      value = Integer.MIN_VALUE;
    } else {
      // プレイヤーの手番では最初に最大値をセットしておく
      value = Integer.MAX_VALUE;
    }
    int checkFinPass=Reversi.CheckOnlyFinishPassNonShow(board);

    if (checkFinPass== 2) {//全部埋まっているとき
      cnt++;
      return valueBoard(board,turn);
    }else if(checkFinPass==1){//パスするとき
      //CheckOnlyFinishPassNonShowで反転しているのでここで反転する必要はない
      board.teban*=-1;
      //flag=!flag;
      //level--;  //パスした際は探索の展開に含まれない気がするのでいらない？？？？
    }

    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        long pos=Reversi.PosTranslate(x,y);
        if (((Reversi.GenValidMove(board)&pos)!=0)) {
          Board cloneBoard=Reversi.clone(board);
          Reversi.Put(board,pos);

          childValue = alphaBeta(!flag, level - 1, alpha, beta);

          if (flag) {
            // AIのノード
            if (childValue > value) {
              value = childValue;
              // α値を更新
              alpha = value;
              bestX = x;
              bestY = y;
            }
            if (value > beta) {  // βカット
              //戻す
              board=cloneBoard;
              return value;
            }
          } else {
            // プレイヤーのノード
            if (childValue < value) {
              value = childValue;
              // β値を更新
              beta = value;
              bestX = x;
              bestY = y;
            }

            if (value < alpha) {  // αカット
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
    if (level == SEARCH_LEVEL) {//ルートノード
      System.out.println("探索ノード数:"+cnt);

      return bestX + bestY * 8;
    } else {

      return value;
    }
  }
  int getNodeCnt(){
    return cnt;
  }
  void delNodeCnt(){
    cnt=0;
  }

  static int valueBoard(Board board,int turn) {
    int value = 0;
    /*
    疑似開放度
    盤面の状態から開放度を推測する
    */
    long tmp=board.black|board.white;
    if(board.move_num>56){//終わりの方
          for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
              // 置かれた石とその場所の価値をかけて足していく
              value += Reversi.getDiscColor(x, y,board) * valueOfPlace3[x][y];
            }
          }
          return value*turn;

    }else if((tmp&0x8100000000000081L)!=0){//終盤
          for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
              // 置かれた石とその場所の価値をかけて足していく
              value += Reversi.getDiscColor(x, y,board) * valueOfPlace2[x][y];
            }
          }
          return value*turn;

    }else if((tmp&0xFF818181818181FFL)!=0){//中盤
          for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
              // 置かれた石とその場所の価値をかけて足していく
              value += Reversi.getDiscColor(x, y,board) * valueOfPlace1[x][y];
            }
          }
          value+=openDeg(board,turn);
          return value*turn;
    }else {
          for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
              // 置かれた石とその場所の価値をかけて足していく
              value += Reversi.getDiscColor(x, y,board) * valueOfPlace1[x][y];
            }
          }
          //value+=openDeg(board,turn);
          return value*turn;
    }

  }
  static int openDeg(Board board,int turn){
    int value=0;
    for(int x=1;x<7;x++){
      for(int y=1;y<7;y++){
        if(Reversi.getDiscColor(x,y,board)==0){
          if(Reversi.getDiscColor(x-1,y-1,board)!=0)value++;
          if(Reversi.getDiscColor(x  ,y-1,board)!=0)value++;
          if(Reversi.getDiscColor(x+1,y-1,board)!=0)value++;
          if(Reversi.getDiscColor(x-1,y  ,board)!=0)value++;
          if(Reversi.getDiscColor(x+1,y  ,board)!=0)value++;
          if(Reversi.getDiscColor(x-1,y+1,board)!=0)value++;
          if(Reversi.getDiscColor(x  ,y+1,board)!=0)value++;
          if(Reversi.getDiscColor(x+1,y+1,board)!=0)value++;
        }
      }
    }
    if(-board.teban==turn){
      return value;
    }else return -value;
  }
}
