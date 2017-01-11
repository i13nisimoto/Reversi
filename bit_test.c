#include <stdio.h>

unsigned char getPoint(unsigned short bit_y,unsigned char x){
  unsigned short tmp;
  const unsigned short mask=3;
  return tmp=(bit_y>>((7-x)*2))&mask;
}

void printMap(unsigned short c[]){
  for(int i=0;i<8;i++){
    for(int j=0;j<8;j++){
      printf("%d",getPoint(c[i],j));
    }
    printf("\n");
  }
}
void setPoint(unsigned short *bit,unsigned char x,unsigned char y,unsigned char color){
  unsigned short mask=3;
  mask=~(mask<<((7-x)*2));
  bit[y]&=mask;
  mask=(short)color;
  mask=(mask<<((7-x)*2));
  bit[y]=bit[y]|mask;
}


int main(){
  unsigned short bitmap[8];
  //bitmap[0]=255*2;
  //bitmap[1]=50;

  for(int i=2;i<8;i++){
    bitmap[i]=0;
  }
  setPoint(bitmap,0,1,3);
  printMap(bitmap);
}
