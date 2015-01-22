#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#define maxLength 20

typedef int bool;
typedef struct trieNode trieNode;
struct trieNode{
  char c;
  trieNode * nexts[256];
  int childCount;
};

trieNode * newNode(char);
bool isMember(trieNode *, char *);
bool addWord(trieNode *, char *);
bool delWord(trieNode *, char *);
void printTrie(trieNode *, char *, int);

trieNode * newNode(char ch){
  trieNode * r = (trieNode *) malloc(sizeof(trieNode));
  if(r != NULL){
    r->c = ch;
    memset(r->nexts, 0, 256);
    r->childCount = 0;
  }
  return r;
}
bool isMember(trieNode * root, char * s){
  if(!root || !s) return 0;
  trieNode * p = root->nexts[s[0]];
  int count = 0;
  while(p != NULL && s[count] != '\0'){
    if(s[count] == p->c){
      count++;
      p = p->nexts[s[count]];
    }
    else{
      return 0;
    }
  }
  if(s[count] == '\0' && p != NULL && p->c == '\0'){
    return 1;
  }
  else return 0;
}
bool addWord(trieNode * root, char * s){
  int a = isMember(root, s);
  if(a == 1){
    printf("word already exists.\n");
    return 0;
  }
  else{
    printf("a new word!\n");
  }
  trieNode * p = root;
  int count = 0;
  while(s[count] != '\0' && p->nexts[s[count]] != NULL){
    p->childCount++;
    p = p->nexts[s[count]];
    count++;
  }

  while(1){
    trieNode * t = newNode(s[count]);
    if(!t){
      printf("fail to locate new memory.\n");
      return 0;
    }
    p->childCount++;
    p->nexts[s[count]] = t;
    p = t;
    if(s[count] == '\0') break;
    else count++;
  }
  return 1;
  
}
bool delWord(trieNode * root, char * s){
  if(isMember(root, s) == 0){
    printf("string does not exist.\n");
    return 0;
  }
  trieNode * p = root;
  trieNode * d = NULL;
  int count = 0;
  while(p->nexts[s[count]] != NULL){
    if(p->nexts[s[count]]->childCount > 1){
      p->childCount--;
      p = p->nexts[s[count]];
      count++;
    }
    else{
      d = p->nexts[s[count]];
      p->nexts[s[count]] = NULL;
      break;
    }
  }
  while(d != NULL){
    trieNode * temp = d->nexts[s[count]];
    count++;
    free(d);
    d = temp;
  }
  return 1;

}

void printTrie(trieNode * root, char * s, int count){
  if(count != -1){
    s[count] = root->c;
    if(root->c == '\0'){
      printf("%s\n", s);
      return;
    }
  }
  for(int i = 0; i < 256; i++){
    if(root->nexts[i] != NULL){
      printTrie(root->nexts[i], s, count+1);
    }
  }
}
