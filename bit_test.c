#include <stdio.h>
#define HEIGHT 8
#define WIDTH 8

unsigned short bitmap[HEIGHT]={};
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

void debug(){

}
int main(){
  init();
  debug();
}
