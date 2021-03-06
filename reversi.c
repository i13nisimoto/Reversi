#include <stdio.h>

#define INPUT_ERROR 3

#define PASS 1
#define FINISH 2

// 手番を表す列挙型
typedef enum TEBAN{
    SENTE = -1,
    GOTE = 1,
    GAME_OVER = 0
}TEBAN;

// 局面を表す構造体
typedef struct BOARD{
    unsigned long long black, white;  // 黒石・白石のビットボード
    TEBAN teban;        // 手番
    int move_num;           // 何手動いたか（手数）
}BOARD;

// 関数プロトタイプ宣言
void Init(BOARD *board);                            // 局面を初期化する関数
void getTurn(BOARD *board);                       // 盤面を表示する関数
unsigned long long GetPos();                                  // 座標を入力させ、posを返す関数
unsigned long long PosTranslate(int file, int rank);         // 座標をunsigned long longのposに変換する関数
void Put(BOARD *board, unsigned long long pos);               // 石を置く関数 posは絶対に合法手
unsigned long long GenValidMove(const BOARD *board);          // 合法手を生成する関数
unsigned long long GetReverse(BOARD *board, unsigned long long pos);    // 反転パターンを求める関数
int CheckFinishPass(BOARD *board);                  // 終了・パス判定
int NumOfStone(unsigned long long bits);                      //石の個数計測
int ShowResult(BOARD *board);                       //結果出力
void GameManVsMan(void);                            //Man vs Man

int main(void){
    int game_mode;

    while(1){
        lcd_putstr(0,0,"ﾓｰﾄﾞｾﾝﾀｸ");
        //printf("モードを選択してください。\n 先手 vs 後手\n0: 人 vs 人\n9: 終了\n");
        //scanf("%d", &game_mode);
        /*
          ここら辺に入力処理書いて
        */
        switch(game_mode){
            case 0: GameManVsMan(); break;
            case 9: lcd_putstr(0,0,"ｼｭｳﾘｮｳ"); return 0;
            default: lcd_putstr(0,0,"ｴﾗｰ"); break;
        }
    }
    return 0;
}
// 人 vs 人
void GameManVsMan(void){
    BOARD board;
    unsigned long long pos, valid;

    Init(&board);
    getTurn(&board);

    // 石を置く
    while(board.teban != GAME_OVER){
        // 合法手を得る
        valid = GenValidMove(&board);
        // 手を受け取る
        pos = GetPos();
        if( pos == INPUT_ERROR ){
            //printf("エラーです。\n");
            continue;
        }else if( (pos & valid) == 0){
            lcd_putstr(0,0,"ｵｸｺﾄｶﾞﾃﾞｷﾏｾﾝ");
            //printf("非合法手です。\n");
            continue;
        }
        Put(&board, pos);
        getTurn(&board);

        CheckFinishPass(&board);
    }

    ShowResult(&board);

    return;
}
// 局面を初期化
void Init(BOARD *board){
    board->black = ((unsigned long long)1<<28)|((unsigned long long)1<<35);
    board->white = ((unsigned long long)1<<27)|((unsigned long long)1<<36);
    board->teban = SENTE;
    board->move_num = 0;
}

// 盤面を表示する関数
void getTurn(BOARD *board){


    // 手番表示
    lcd_putstr(0,0"ﾃﾊﾞﾝ:");
    switch(board->teban){
        case SENTE: lcd_putstr(0,1,"ｾﾝﾃ"); break;
        case GOTE: lcd_putstr(0,1,"ｺﾞﾃ"); break;
        default: break;
    }
}
// 石の数を数える関数
int NumOfStone(unsigned long long bits){
    bits = bits - (bits >> 1 & 0x5555555555555555);                           // 2bitごと
    bits = (bits & 0x3333333333333333) + (bits >> 2 & 0x3333333333333333);    // 4bit
    bits = (bits & 0x0f0f0f0f0f0f0f0f) + (bits >> 4 & 0x0f0f0f0f0f0f0f0f);    // 8bit
    bits = (bits & 0x00ff00ff00ff00ff) + (bits >> 8 & 0x00ff00ff00ff00ff);    //16bit
    bits = (bits & 0x0000ffff0000ffff) + (bits >> 16 & 0x0000ffff0000ffff);   //32bit
    return (bits + (bits >> 32)) & 0x000000000000007f;                        //64bit
}
// 結果を出力する関数
int ShowResult(BOARD *board){
    //ここで個数を出力してくださいNumOfStoneが石の数です
    if(NumOfStone(board->black) > NumOfStone(board->white)){
        lcd_putstr(0,1,"Black win");
        return 1;
    }else if(NumOfStone(board->black) < NumOfStone(board->white)){
        lcd_putstr(0,1,"White win");
        return -1;
    }else{
        lcd_putstr(0,1,"draw");
        return 0;
    }
}
// 座標を入力させ、posを返す関数
unsigned long long GetPos(){
    int file;  // 列番号
    int rank;   // 行番号

    //このintのとこに座標を入れてください。
    unsigned long long pos;   // 指定箇所を示すビットボード

    // 受け取った座標からビットボードを生成
    pos = PosTranslate(file+1, rank+1);
    return pos;
}

// 座標をunsigned long longのposに変換する関数
unsigned long long PosTranslate(int file, int rank){

    unsigned long long pos;


    pos = ( (unsigned long long)1 << ( file+ 8 * (8 - rank) ) );

    return pos;
}

// 石を置く関数 posは絶対に合法手
void Put(BOARD *board, unsigned long long pos){
    unsigned long long rev;

    // 反転パターン取得
    rev = GetReverse(board, pos);

    switch(board->teban){
        case SENTE:
            board->black ^= pos | rev;
            board->white ^= rev;
            board->teban = GOTE;
            break;
        case GOTE:
            board->white ^= pos | rev;
            board->black ^= rev;
            board->teban = SENTE;
            break;
        default:
            break;
    }
    board->move_num++;
    return;
}

// 合法手を生成する関数
unsigned long long GenValidMove(const BOARD *board){
    int i;
    unsigned long long me, enemy, masked_enemy, t, valid = 0, blank;

    // 現在手番の方をme、相手をenemyにする
    if(board->teban == SENTE){
        me = board->black;
        enemy = board->white;
    }else{
        me = board->white;
        enemy = board->black;
    }

    // 空マスのビットボードを（黒または白）のビットNOTで得る
    blank = ~(board->black | board->white);

    // 右方向
    masked_enemy = enemy & 0x7e7e7e7e7e7e7e7e; //端列を除く敵石
    t = masked_enemy & (me << 1); //自石の左隣にある敵石を調べる
    for(i = 0; i < 5; i++){
        t |= masked_enemy & (t << 1);
    }
    valid = blank & (t << 1);

    // 左方向
    masked_enemy = enemy & 0x7e7e7e7e7e7e7e7e;
    t = masked_enemy & (me >> 1);
    for(i = 0; i < 5; i++){
        t |= masked_enemy & (t >> 1);
    }
    valid |= blank & (t >> 1);

    // 上方向
    masked_enemy = enemy & 0x00ffffffffffff00;
    t = masked_enemy & (me << 8);
    for (i = 0; i < 5; i++){
        t |= masked_enemy & (t << 8);
    }
    valid |= blank & (t << 8);

    // 下方向
    masked_enemy = enemy & 0x00ffffffffffff00;
    t = masked_enemy & (me >> 8);
    for (i = 0; i < 5; i++){
        t |= masked_enemy & (t >> 8);
    }
    valid |= blank & (t >> 8);

    // 右上方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00;
    t = masked_enemy & (me << 7);
    for (i = 0; i < 5; i++){
        t |= masked_enemy & (t << 7);
    }
    valid |= blank & (t << 7);

    // 左上方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00;
    t = masked_enemy & (me << 9);
    for (i = 0; i < 5; i++){
        t |= masked_enemy & (t << 9);
    }
    valid |= blank & (t << 9);

    // 右下方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00;
    t = masked_enemy & (me >> 9);
    for (i = 0; i < 5; i++){
        t |= masked_enemy & (t >> 9);
    }
    valid |= blank & (t >> 9);

    // 左下方向
    masked_enemy = enemy & 0x007e7e7e7e7e7e00;
    t = masked_enemy & (me >> 7);
    for (i = 0; i < 5; i++){
        t |= masked_enemy & (t >> 7);
    }
    valid |= blank & (t >> 7);

    return valid;
}

// 反転パターンを求める関数
unsigned long long GetReverse(BOARD *board, unsigned long long pos){
    int i;
    unsigned long long me, enemy, mask, rev = 0, rev_cand;

    // 現在手番の方をme、相手をenemyにする
    if(board->teban == SENTE){
        me = board->black;
        enemy = board->white;
    }else{
        me = board->white;
        enemy = board->black;
    }

    // 右方向
    rev_cand = 0;
    mask = 0x7e7e7e7e7e7e7e7e;
    for( i = 1; ( (pos >> i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos >> i);
    }
    if( ( (pos >> i) & me) != 0 ) rev |= rev_cand;

    // 左方向
    rev_cand = 0;
    mask = 0x7e7e7e7e7e7e7e7e;
    for( i = 1; ( (pos << i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos << i);
    }
    if( ( (pos << i) & me) != 0 ) rev |= rev_cand;

    // 上方向
    rev_cand = 0;
    mask = 0x00ffffffffffff00;
    for( i = 1; ( (pos << 8 * i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos << 8 * i);
    }
    if( ( (pos << 8 * i) & me) != 0 ) rev |= rev_cand;

    // 下方向
    rev_cand = 0;
    mask = 0x00ffffffffffff00;
    for( i = 1; ( (pos >> 8 * i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos >> 8 * i);
    }
    if( ( (pos >> 8 * i) & me) != 0 ) rev |= rev_cand;

    // 右上方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00;
    for( i = 1; ( (pos << 7 * i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos << 7 * i);
    }
    if( ( (pos << 7 * i) & me) != 0 ) rev |= rev_cand;

    // 左上方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00;
    for( i = 1; ( (pos << 9 * i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos << 9 * i);
    }
    if( ( (pos << 9 * i) & me) != 0 ) rev |= rev_cand;

    // 右下方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00;
    for( i = 1; ( (pos >> 9 * i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos >> 9 * i);
    }
    if( ( (pos >> 9 * i) & me) != 0 ) rev |= rev_cand;

    // 左下方向
    rev_cand = 0;
    mask = 0x007e7e7e7e7e7e00;
    for( i = 1; ( (pos >> 7 * i) & mask & enemy ) != 0; i++ ){
        rev_cand |= (pos >> 7 * i);
    }
    if( ( (pos >> 7 * i) & me) != 0 ) rev |= rev_cand;

    return rev;
}

// 終了・パス判定
int CheckFinishPass(BOARD *board){
    unsigned long long valid;

    valid = GenValidMove(board);

    // 終了・パス判定
    if( valid == 0 ){
        board->teban *= -1;
        if(GenValidMove(board) == 0){
            //終了
            board->teban = GAME_OVER;
            return FINISH;
        }
        lcd_putstr(0,0"pass");
        getTurn(board);
        return PASS;
    }
    return 0;
}
