#include <stdio.h>
#include <stdlib.h>
#define HEIGHT 8
#define WIDTH 8
#define uchar unsigned char

typedef unsigned long long board;

struct CBoard{
  board color[2];//0が黒 1が白
};
struct CBoard bit_Board={0,0};

void setPoint(uchar col, uchar row,uchar c);
void loop();
void getPoint(char* tmp);
void debug();
void init();
int GetRandom(int min,int max);
void putBinary(board x);

int main(){
//  init();
//  debug();
  loop();


}
void setPoint(uchar col, uchar row,uchar c){
   bit_Board.color[c]=bit_Board.color[c]|((board)1<<(col+row*8));


}
void loop(){
  for(int i=0;i<1;i++){
    char tmp[3]={};
    getPoint(tmp);//0,1 は座標　2は色

    setPoint(tmp[0],tmp[1],tmp[2]);
    debug();

  }
}
void getPoint(char* tmp){
  /*
  tmp[0]=GetRandom(0,7);
  printf("%d,",tmp[0] );
  tmp[1]=GetRandom(0,7);
  printf("%d,",tmp[1] );
  tmp[2]=GetRandom(0,1);
  printf("%d\n",tmp[2] );
*/
  tmp[0]=5;
  tmp[1]=5;
  tmp[2]=0;
}
int GetRandom(int min,int max){
	return min + (int)(rand()*(max-min+1.0)/(1.0+RAND_MAX));
}

void debug(){
  putBinary(bit_Board.color[0]);
  putBinary(bit_Board.color[1]);
}
void init(){

}
void putBinary(board x)
{
    int i;
    for (i = 0; i < 64; i++, x <<= 1){
      if(i%8==0)putchar('\n');
        putchar('0' + ((x & 0x8000000000000000) != 0));
    }
    putchar('\n');
}
/*
unsigned long long bitboard[2]={};
void init(){

}
unsigned char getPoint(unsigned char x,unsigned char y){
  unsigned short tmp;
  const unsigned short mask=3;
  return tmp=(bitmap[y]>>((7-x)*2))&mask;
}

void printMap(){
  for(int i=0;i<HEIGHT;i++){
    for(int j=0;j<WIDTH;j++){
      printf("%d",getPoint(j,i));
    }
    printf("\n");
  }
}
void setPoint(unsigned char x,unsigned char y,unsigned char color){
  unsigned short mask=3;
  mask=~(mask<<((7-x)*2));
  bitmap[y]&=mask;
  mask=(short)color;
  mask=(mask<<((7-x)*2));
  bitmap[y]=bitmap[y]|mask;
}
void
void debug(){

}
int main(){
  init();
  debug();
}
*/
