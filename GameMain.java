import java.util.Scanner;
class GameMain{
	static final int INPUT_ERROR=3;
	static int player1_cnt,player2_cnt;
	static int player1_time,player2_time;
	public static void main(String args[]){
		while(true){
			GameMain();
		}
	}

	static void GameMain(){
		Board board=new Board();
		long pos=0, valid=0;
		board.display();

		// 石を置く
		while(board.getCurrColor()!=board.GAME_OVER){
			// 合法手を得る
			valid = Reversi.GenValidMove(board);

			// 手を受け取る
			switch (board.getCurrColor()) {
				case -1:{
					long start =System.currentTimeMillis();
					switch(board.player1){
						case 0:{
							pos=Reversi.GetPos();
							break;
						}
						case 1:{
							AI_minMax ai=new AI_minMax(board.copy(),board.teban,board.player1_searchLevel);
							pos=ai.compute();
							player1_cnt+=ai.getNodeCnt();
							ai.delNodeCnt();
							break;
						}
						case 2:{
							AI_alpha ai=new AI_alpha(board.copy(),board.teban,board.player1_searchLevel);
							pos=ai.compute();
							player1_cnt+=ai.getNodeCnt();
							ai.delNodeCnt();
							break;
						}
						case 3:{
							AI_beta ai=new AI_beta(board.copy(),board.teban,board.player1_searchLevel);
							pos=ai.compute();
							player1_cnt+=ai.getNodeCnt();
							ai.delNodeCnt();
							break;
						}

						default: break;
					}


					long end = System.currentTimeMillis();
					long interval = end - start;
					System.out.println(interval + "ミリ秒");
					player1_time+=interval;
					break;

				}
				//case GOTE:pos=GetPos_AI(valid);break;
				// default :printf("%s\n","err" );break;
				case 1:{
					long start =System.currentTimeMillis();
					switch(board.player2){
						case 0:{
							pos=Reversi.GetPos();
							break;
						}
						case 1:{
							AI_minMax ai=new AI_minMax(board.copy(),board.teban,board.player2_searchLevel);
							pos=ai.compute();
							player2_cnt+=ai.getNodeCnt();
							ai.delNodeCnt();
							break;
						}
						case 2:{
							AI_alpha ai=new AI_alpha(board.copy(),board.teban,board.player2_searchLevel);
							pos=ai.compute();
							player2_cnt+=ai.getNodeCnt();
							ai.delNodeCnt();
							break;
						}
						case 3:{
							AI_beta ai=new AI_beta(board.copy(),board.teban,board.player2_searchLevel);
							pos=ai.compute();
							player2_cnt+=ai.getNodeCnt();
							ai.delNodeCnt();
							break;
						}
						default: break;
					}
					long end = System.currentTimeMillis();
					long interval = end - start;
					System.out.println(interval + "ミリ秒");
					player2_time+=interval;
					break;
				}
			}

			if( pos == INPUT_ERROR ){
				System.out.print("エラーです。\n");
				continue;
			}else if(!board.putDisc(pos,valid,true)){
				System.out.print("非合法手です。\n");
				System.exit(1);
				continue;
			}
			// }else if( (pos & valid) == 0){
			// 	System.out.print("非合法手です。\n");
			// 	continue;
			// }
			// Put(board, pos);

			board.checkPass();
			board.display();

		}

		Reversi.ShowResult(board);


	}
}
// 	static int node;
// 	static long nodeTotal[]=new long[2],timeTotal[]=new long[2];
// 	public static void main(String args[]){
// 		Board b=new Board();
// 		b.init();
// 		int pos[]=null;
// 		int passCount=0;
// 		while(passCount<2){//パスが2回未満
// 			if(!(b.checkPass())){//パスではない
// 				b.display();
// 				node=0;
// 				//do{
// 					System.out.println("Please input position.");
// 					long start = System.currentTimeMillis();
// 					long end=start;
// 					if(b.getCurrColor()==Board.BLACK){//黒の手番
//
// 						pos=humanPlayer();//ここを変更してコンピュータプレイヤのメソッドを呼び出す
//
//
// 						end = System.currentTimeMillis();
// 						nodeTotal[0]+=node;
// 						timeTotal[0]+=(end-start);
// 					}else if(b.getCurrColor()==Board.WHITE){//白の手番
//
// 						pos=humanPlayer();//ここを変更してコンピュータプレイヤのメソッドを呼び出す
//
// 						end = System.currentTimeMillis();
// 						nodeTotal[1]+=node;
// 						timeTotal[1]+=(end-start);
// 					}
// 					System.out.println((end-start)+" msec");
// 					System.out.println(node+" nodes");
// 					System.out.println("("+pos[0]+","+pos[1]+")");
// 					if(!(b.putDisc(pos[0],pos[1],true))) throw new RuntimeException("Invalid position.");//指定の場所に駒を置けない場所の場合は中断
// 				//}while(!(b.putDisc(pos[0],pos[1],true)));
// 				passCount=0;
// 			}else {passCount++;b.changeCurrColor();}//パスの場合
// 		}
// 		b.display();
// 		System.out.println("Black's Total Number of Nodes:"+nodeTotal[0]);
// 		System.out.println("Black's Total Time:"+timeTotal[0]);
// 		System.out.println("White's Total Number of Nodes:"+nodeTotal[1]);
// 		System.out.println("White's Total Time:"+timeTotal[1]);
// 	}
//
// 	static int[] humanPlayer(){
// 		return input(2);//２つの整数を入力し、配列で返す。
// 	}
//
// 	static int[] input(int num){
// 		int array[] = new int[num];
// 		Scanner scan = new Scanner(System.in);
// 		array[0] = scan.nextInt();
// 		array[1] = scan.nextInt();
// 		return array;
// 	}
//
// }


// class Board{
//
//   final int SENTE = -1;
//   final int GOTE = 1;
//   final int GAME_OVER = 0;
//
//
//   long black,white;
//   int teban;
//   int move_num;
//   int player1,player2;
//   int player1_searchLevel,player2_searchLevel;
//   String playerName[]={"human","Tamura","Nishimoto","Yamamura"};
// }

class Reversi{
  static final int INPUT_ERROR=3;
  static int player1_cnt,player2_cnt;
  static int player1_time,player2_time;

  static Board clone(Board board){
    Board copy=new Board(board);

    return copy;
  }
  static int getDiscColor(int x,int y,Board board){
    long tmp=0x8000000000000000L;
    tmp=tmp>>>x;
    tmp=tmp>>>(y*8);
    if((tmp&board.black)!=0)return -1;
    else if((tmp&board.white)!=0)return 1;
    else return 0;
  }



  static void Init(Board board){
    board.black = ((long)1<<28)|((long)1<<35);
    board.white = ((long)1<<27)|((long)1<<36);
    board.teban = board.SENTE;
    for(int i=0;i<board.playerName.length;i++){
      System.out.println(i+":"+board.playerName[i]);
    }

    System.out.print("プレイヤー1を選択してください：");
    board.player1=KeyBoard.KeyBoardIntvalue(0,4);
    if(board.player1==4)System.exit(1);
    if(board.player1!=0){
      System.out.print("探索する深さを選択してください：");
      board.player1_searchLevel=KeyBoard.KeyBoardIntvalue(1,9);
    }
    System.out.print("プレイヤー2を選択してください：");
    board.player2=KeyBoard.KeyBoardIntvalue(0,3);
    if(board.player2!=0){
      System.out.print("探索する深さを選択してください：");
      board.player2_searchLevel=KeyBoard.KeyBoardIntvalue(1,9);
    }
    player1_cnt=player2_cnt=player1_time=player2_time=0;
  }
  static void ShowBoard(Board board){
    int rank=0;
    long pos = (long)1<<63;
    System.out.print("  0 1 2 3 4 5 6 7\n");
    // 盤面表示
    for ( int i = 0; i < 64 ; i++){
      // 行番号
      if(i % 8 == 0) System.out.print(rank+++" ");
      // 盤面状態表示
      if( ( board.black & pos )!= 0) System.out.print("黒");
      else if( ( board.white & pos ) != 0) System.out.print("白");
      else System.out.print("・");
      // 8回表示が終わるごとに改行
      if(i % 8 == 7) System.out.print("\n");
      // posを一つずらす
      pos >>>= 1;
    }
    //石数表示
    System.out.printf("黒石: %d個, 白石: %d個\n", NumOfStone(board.black), NumOfStone(board.white));
    // 手番表示

    System.out.println("---------------------------");


    switch(board.teban){
      case -1: System.out.print("手番: 先手\n"); break;
      case 1: System.out.print("手番: 後手\n"); break;
      default: break;
    }

    //System.out.println("評価値="+AI_alpha.valueBoard(board,(-1)*board.teban));
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
    System.out.println("黒ノード:"+GameMain.player1_cnt);
    System.out.println("白ノード:"+GameMain.player2_cnt);
    System.out.println("黒探索"+GameMain.player1_time+"ms");
    System.out.println("白探索"+GameMain.player2_time+"ms");
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
    int tmp[]=KeyBoard.KeyBoardGetPos();//sc.nextInt();
    file=tmp[0];
    rank=tmp[1];

    // 受け取った座標からビットボードを生成
    pos = PosTranslate(file, rank);
    return pos;
  }

  // 座標をlongのposに変換する関数
  static long PosTranslate(int file, int rank){
    long pos=0x8000000000000000L;

    pos = ( pos >>> ( file + 8 * (rank) ) );

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
    //  board.teban = board.GOTE;
      break;
      case 1:
      board.white ^= pos | rev;
      board.black ^= rev;
    //  board.teban = board.SENTE;
      break;
      default:
      break;
    }
    board.changeCurrColor();
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
  static int CheckOnlyFinishPassNonShow(Board board){
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
      board.teban *= -1;
      return 1;
    }
    return 0;
  }
}
class KeyBoard{
  static Scanner sc =new Scanner(System.in);
  static int KeyBoardIntvalue(){
    int tmp;
    while(true){
      String s= sc.next();
      try{
        tmp=Integer.parseInt(s);
        return tmp;
      }catch(Exception e){
        //System.out.println("数字を入力してください");
        return -1;
      }
    }

  }
  static int KeyBoardIntvalue(int min,int max){
    int tmp;
    while(true){
      String s= sc.next();
      try{
        tmp=Integer.parseInt(s);
        if(tmp>=min&&tmp<=max){
          return tmp;
        }
        System.out.print("指定された数字を入力してください:");
      }catch(Exception e){
        System.out.print("数字を入力してください:");
        //return -1;
      }
    }

  }
  static int[] KeyBoardGetPos(){
    int []pos={-1,-1};

    while(true){
      sc=new Scanner(System.in);
      String s1= sc.next();
      String s2=sc.next();
      try{
        pos[0]=Integer.parseInt(s1);
        pos[1]=Integer.parseInt(s2);
        return pos;
      }catch(Exception e){
        System.out.print("数字を入力してください:");
        //return -1;
      }
    }
  }
}
