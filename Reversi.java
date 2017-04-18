import java.util.*;


class Board{

  final int SENTE = -1;
  final int GOTE = 1;
  final int GAME_OVER = 0;


  long black,white;
  int teban;
  int move_num;
}

class Reversi{
  static final int INPUT_ERROR=3;
  static Scanner sc =new Scanner(System.in);
  public static void main(String args[]){
    int gameMode;

    while(true){
      System.out.print("モードを選択してください。\n 先手 vs 後手\n0: 人 vs 人\n1: 人 vs AI\n2: AI vs AI\n9: 終了\n");
      gameMode=sc.nextInt();
      switch(gameMode){
        case 0: GameManVsMan(); break;
        //case 1: GameManVsAI();break;
        //case 2: GameAIVsAI();break;
        //case 9: System.out.print("終了します。\n"); return 0;
        default: System.out.print("エラー。もう一度入力してください\n"); break;
      }
    }
  }
  static void GameManVsMan(){
    Board board=new Board();
    long pos,valid;
    Init(board);
    ShowBoard(board);

    while(board.teban!=board.GAME_OVER){
      valid = GenValidMove(board);
      // 手を受け取る
      pos = GetPos();
      if( pos == INPUT_ERROR ){
        System.out.print("エラーです。\n");
        continue;
      }else if( (pos & valid) == 0){
        System.out.print("非合法手です。\n");
        continue;
      }
      Put(board, pos);
      ShowBoard(board);

      CheckFinishPass(board);
    }

  }

  static void Init(Board board){
    board.black = ((long)1<<28)|((long)1<<35);
    board.white = ((long)1<<27)|((long)1<<36);
    board.teban = board.SENTE;
    board.move_num = 0;
  }
  static void ShowBoard(Board board){
    int rank=0;
    long pos = (long)1<<63;
    System.out.print("  0 1 2 3 4 5 6 7\n");
    // 盤面表示
    for ( int i = 0; i < 64 ; i++){
      // 行番号
      if(i % 8 == 0) System.out.print(rank++);
      // 盤面状態表示
      if( ( board.black & pos )!= 0) System.out.print("黒");
      else if( ( board.white & pos ) != 0) System.out.print("白");
      else System.out.print("口");
      // 8回表示が終わるごとに改行
      if(i % 8 == 7) System.out.print("\n");
      // posを一つずらす
      pos >>>= 1;
    }
    //石数表示
    System.out.printf("黒石: %d個, 白石: %d個\n", NumOfStone(board.black), NumOfStone(board.white));
    // 手番表示
    System.out.print("\n手番: ");
    switch(board.teban){
      case -1: System.out.print("先手\n"); break;
      case 1: System.out.print("後手\n"); break;
      default: break;
    }
  }
  static long NumOfStone(long bits){
    bits = bits - (bits >>> 1 & 0x5555555555555555L);                           // 2bitごと
    bits = (bits & 0x3333333333333333L) + (bits >>> 2 & 0x3333333333333333L);    // 4bit
    bits = (bits & 0x0f0f0f0f0f0f0f0fL) + (bits >>> 4 & 0x0f0f0f0f0f0f0f0fL);    // 8bit
    bits = (bits & 0x00ff00ff00ff00ffL) + (bits >>> 8 & 0x00ff00ff00ff00ffL);    //16bit
    bits = (bits & 0x0000ffff0000ffffL) + (bits >>> 16 & 0x0000ffff0000ffffL);   //32bit
    return (bits + (bits >>> 32)) & 0x000000000000007fL;                        //64bit
  }
  static int ShowResult(Board board){
    if(NumOfStone(board.black) > NumOfStone(board.white)){
      System.out.print("黒の勝ち！\n");
      return 1;
    }else if(NumOfStone(board.black) < NumOfStone(board.white)){
      System.out.print("白の勝ち！\n");
      return -1;
    }else{
      System.out.print("引き分け！\n");
      return 0;
    }
  }
  static long GetPos(){
    int file;  // 列番号
    int rank;   // 行番号
    long pos;   // 指定箇所を示すビットボード

    System.out.print("座標を入力してください。(横　縦)\n");
    file=sc.nextInt();
    rank=sc.nextInt();
    //(" %d%d", &file, &rank);

    // 受け取った座標からビットボードを生成
    pos = PosTranslate(7-file, rank+1);
    return pos;
  }
  // long GetPos_AI(long valid){
  //   int file;  // 列番号
  //   int rank;   // 行番号
  //    pos;   // 指定箇所を示すビットボード
  //
  //   while(1){
  //     file=0 + (int)( rand() * (7 - 0 + 1.0) / (1.0 + RAND_MAX));
  //     rank=0 + (int)( rand() * (7 - 0 + 1.0) / (1.0 + RAND_MAX));
  //     // 受け取った座標からビットボードを生成
  //     pos = PosTranslate(7-file, rank+1);
  //     if((pos&valid)!=0){
  //       break;
  //     }
  //
  //   }
  //
  //
  //   return pos;
  // }
  // 座標をunsigned long longのposに変換する関数
  static long PosTranslate(int file, int rank){
    int file_num=file;
    long pos;



    pos = ( (long)1 << ( file_num + 8 * (8 - rank) ) );

    return pos;
  }
  // 石を置く関数 posは絶対に合法手
  static void Put(Board board,long pos){
    long rev;

    // 反転パターン取得
    rev = GetReverse(board, pos);

    switch(board.teban){
      case -1:
      board.black ^= pos | rev;
      board.white ^= rev;
      board.teban = board.GOTE;
      break;
      case 1:
      board.white ^= pos | rev;
      board.black ^= rev;
      board.teban = board.SENTE;
      break;
      default:
      break;
    }
    board.move_num++;
    return;
  }

  // 合法手を生成する関数
  static long GenValidMove(Board board){
    int i;
    long me, enemy, masked_enemy, t, valid = 0, blank;

    // 現在手番の方をme、相手をenemyにする
    if(board.teban == board.SENTE){
      me = board.black;
      enemy = board.white;
    }else{
      me = board.white;
      enemy = board.black;
    }

    // 空マスのビットボードを（黒または白）のビットNOTで得る
    blank = ~(board.black | board.white);

    // 右方向
    masked_enemy = enemy & 0x7e7e7e7e7e7e7e7eL; //端列を除く敵石
    t = masked_enemy & (me << 1); //自石の左隣にある敵石を調べる
    for(i = 0; i < 5; i++){
      t |= masked_enemy & (t << 1);
    }
    valid = blank & (t << 1);

    // 左方向
    masked_enemy = enemy & 0x7e7e7e7e7e7e7e7eL;
    t = masked_enemy & (me >>> 1);
    for(i = 0; i < 5; i++){
      t |= masked_enemy & (t >>> 1);
    }
    valid |= blank & (t >>> 1);

    // 上方向
    masked_enemy = enemy & 0x00ffffffffffff00L;
    t = masked_enemy & (me << 8);
    for (i = 0; i < 5; i++){
      t |= masked_enemy & (t << 8);
    }
    valid |= blank & (t << 8);

    // 下方向
    masked_enemy = enemy & 0x00ffffffffffff00L;
    t = masked_enemy & (me >>> 8);
    for (i = 0; i < 5; i++){
      t |= masked_enemy & (t >>> 8);
    }
    valid |= blank & (t >>> 8);

    // 右上方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00L;
    t = masked_enemy & (me << 7);
    for (i = 0; i < 5; i++){
      t |= masked_enemy & (t << 7);
    }
    valid |= blank & (t << 7);

    // 左上方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00L;
    t = masked_enemy & (me << 9);
    for (i = 0; i < 5; i++){
      t |= masked_enemy & (t << 9);
    }
    valid |= blank & (t << 9);

    // 右下方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00L;
    t = masked_enemy & (me >>> 9);
    for (i = 0; i < 5; i++){
      t |= masked_enemy & (t >>> 9);
    }
    valid |= blank & (t >>> 9);

    // 左下方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00L;
    t = masked_enemy & (me >>> 7);
    for (i = 0; i < 5; i++){
      t |= masked_enemy & (t >>> 7);
    }
    valid |= blank & (t >>> 7);

    return valid;
  }

  // 反転パターンを求める関数
  static long GetReverse(Board board,long pos){
    int i;
    long me, enemy, mask, rev = 0, rev_cand;

    // 現在手番の方をme、相手をenemyにする
    if(board.teban == board.SENTE){
      me = board.black;
      enemy = board.white;
    }else{
      me = board.white;
      enemy = board.black;
    }

    // 右方向
    rev_cand = 0;
    mask = 0x7e7e7e7e7e7e7e7eL;
    for( i = 1; ( (pos >>> i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos >>> i);
    }
    if( ( (pos >>> i) & me) != 0 ) rev |= rev_cand;

    // 左方向
    rev_cand = 0;
    mask = 0x7e7e7e7e7e7e7e7eL;
    for( i = 1; ( (pos << i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos << i);
    }
    if( ( (pos << i) & me) != 0 ) rev |= rev_cand;

    // 上方向
    rev_cand = 0;
    mask = 0x00ffffffffffff00L;
    for( i = 1; ( (pos << 8 * i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos << 8 * i);
    }
    if( ( (pos << 8 * i) & me) != 0 ) rev |= rev_cand;

    // 下方向
    rev_cand = 0;
    mask = 0x00ffffffffffff00L;
    for( i = 1; ( (pos >>> 8 * i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos >>> 8 * i);
    }
    if( ( (pos >>> 8 * i) & me) != 0 ) rev |= rev_cand;

    // 右上方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00L;
    for( i = 1; ( (pos << 7 * i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos << 7 * i);
    }
    if( ( (pos << 7 * i) & me) != 0 ) rev |= rev_cand;

    // 左上方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00L;
    for( i = 1; ( (pos << 9 * i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos << 9 * i);
    }
    if( ( (pos << 9 * i) & me) != 0 ) rev |= rev_cand;

    // 右下方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00L;
    for( i = 1; ( (pos >>> 9 * i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos >>> 9 * i);
    }
    if( ( (pos >>> 9 * i) & me) != 0 ) rev |= rev_cand;

    // 左下方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00L;
    for( i = 1; ( (pos >>> 7 * i) & mask & enemy ) != 0; i++ ){
      rev_cand |= (pos >>> 7 * i);
    }
    if( ( (pos >>> 7 * i) & me) != 0 ) rev |= rev_cand;

    return rev;
  }
  // 終了・パス判定
  static int CheckFinishPass(Board board){
    long valid;

    valid = GenValidMove(board);

    // 終了・パス判定
    if( valid == 0 ){
      board.teban *= -1;
      if(GenValidMove(board) == 0){
        //終了
        board.teban = board.GAME_OVER;
        return 2;
      }
      System.out.print("パス\n");
      ShowBoard(board);
      return 1;
    }
    return 0;
  }

}
