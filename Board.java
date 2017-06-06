//Reversi.javaのラッパー

class Board{
  final int SENTE = -1;
  final int GOTE = 1;
  final int GAME_OVER = 0;


  long black,white;
  int teban;
  int move_num;
  int player1,player2;
  int player1_searchLevel,player2_searchLevel;
  String playerName[]={"human","Tamura","Nishimoto","Yamamura"};
  Board(){
    init();
  }

  Board(Board board){
    this.black = board.black;
    this.white = board.white;
    this.setCurrColor(board.getCurrColor());
    this.setTurns(board.getTurns());
  }

  int getCurrColor(){
    return teban;
  }

  void setCurrColor(int color){
    teban=color;
  }

  void changeCurrColor(){
    setCurrColor(getCurrColor()*-1);
    setTurns(getTurns()+1);
  }

  int getTurns(){
    return move_num;
  }

  void setTurns(int num){
    move_num=num;
  }

  int getDisc(int x,int y){
    return Reversi.getDiscColor(x,y,this);
  }

  // void setDisc(int x, int y, int disc){
  // } これ何に使うの？？？？

  void init(){
    setTurns(0);
    Reversi.Init(this);
  }

  // boolean putDisc(int x, int y, boolean dataUpdate){//dataUpdate=dummy
  //   if(dataUpdate == true){
  //       if (canPutDown(x, y)) {
  //       // その場所に石を打つ
  //       setDisc(x, y,currentColor);
  //       // ひっくり返す
  //       reverse(x, y);
  //       // 手番を変える
  //       changeCurrColor();
  //       return true;
  //     }
  //     return false;
  //   }
  //   return canPutDown(x,y);
  // }
  boolean putDisc(long pos,long value,boolean dataUpdate){ //pos はx,y座標をbitボードに直したもの
    if((value&pos)!=0){
      if(dataUpdate){
        Reversi.Put(this,pos);

      }
      return true;
    }
    return false;
  }
  // void reverse(int x, int y) {
  //   // ひっくり返せる石がある方向はすべてひっくり返す
  //   if (canPutDown(x, y, 1, 0))   reverse(x, y, 1, 0);
  //   if (canPutDown(x, y, 0, 1))   reverse(x, y, 0, 1);
  //   if (canPutDown(x, y, -1, 0))  reverse(x, y, -1, 0);
  //   if (canPutDown(x, y, 0, -1))  reverse(x, y, 0, -1);
  //   if (canPutDown(x, y, 1, 1))   reverse(x, y, 1, 1);
  //   if (canPutDown(x, y, -1, -1)) reverse(x, y, -1, -1);
  //   if (canPutDown(x, y, 1, -1))  reverse(x, y, 1, -1);
  //   if (canPutDown(x, y, -1, 1))  reverse(x, y, -1, 1);
  // }
  //
  // void reverse(int x, int y, int vecX, int vecY) {
  //
  //   // 相手の石がある間ひっくり返し続ける
  //   // (x,y)に打てるのは確認済みなので相手の石は必ずある
  //   x += vecX;
  //   y += vecY;
  //   while (board[x][y] != currentColor) {
  //     // ひっくり返す
  //     setDisc(x,y,currentColor);
  //
  //     x += vecX;
  //     y += vecY;
  //   }
  // }

  // boolean canPutDown(int x, int y){
  //   // (x,y)が盤面の外だったら打てない
  //   if (getDisc(x,y) == WALL)
  //   return false;
  //   // (x,y)にすでに石が打たれてたら打てない
  //   if (board[x][y] != EMPTY)
  //   return false;
  //   // 8方向のうち一箇所でもひっくり返せればこの場所に打てる
  //   // ひっくり返せるかどうかはもう1つのcanPutDownで調べる
  //   if (canPutDown(x, y, 1, 0))
  //   return true; // 右
  //   if (canPutDown(x, y, 0, 1))
  //   return true; // 下
  //   if (canPutDown(x, y, -1, 0))
  //   return true; // 左
  //   if (canPutDown(x, y, 0, -1))
  //   return true; // 上
  //   if (canPutDown(x, y, 1, 1))
  //   return true; // 右下
  //   if (canPutDown(x, y, -1, -1))
  //   return true; // 左上
  //   if (canPutDown(x, y, 1, -1))
  //   return true; // 右上
  //   if (canPutDown(x, y, -1, 1))
  //   return true; // 左下
  //
  //   // どの方向もだめな場合はここには打てない
  //   return false;
  // }
  //
  // private boolean canPutDown(int x, int y, int vecX, int vecY) {
  //   // 隣の場所へ。どの隣かは(vecX, vecY)が決める。
  //   x += vecX;
  //   y += vecY;
  //   // 盤面外だったら打てない
  //   if (getDisc(x,y) == WALL)
  //   return false;
  //   // 隣が自分の石の場合は打てない
  //   if (board[x][y] == currentColor)
  //   return false;
  //   // 隣が空白の場合は打てない
  //   if (board[x][y] == EMPTY)
  //   return false;
  //
  //   // さらに隣を調べていく
  //   x += vecX;
  //   y += vecY;
  //   // となりに石がある間ループがまわる
  //   while (getDisc(x,y) != WALL) {
  //     // 空白が見つかったら打てない（１1つもはさめないから）
  //     if (board[x][y] == EMPTY)
  //     return false;
  //     // 自分の石があればはさめるので打てる
  //     if (board[x][y] == currentColor)
  //     return true;
  //     x += vecX;
  //     y += vecY;
  //   }
  //   // 相手の石しかない場合はいずれ盤面の外にでてしまうのでこのfalse
  //   return false;
  // }

  int checkPass(){ //終了も判別できるようにしたためintで返す
    return Reversi.CheckFinishPass(this);

  }

  Board copy(){
    Board boardCopy=Reversi.clone(this);
    return boardCopy;
  }

  // int eval(int color){
  //   return getDiscSum(color) - getDiscSum(color * -1);
  // }

  int getDiscSum(int color){
    int cnt=0;
    if(color==-1){
      cnt=(int)Reversi.NumOfStone(black);

    }else{
      cnt=(int)Reversi.NumOfStone(white);
    }
    return cnt;
  }
  void display(){
    Reversi.ShowBoard(this);
  }
}
