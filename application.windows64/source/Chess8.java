import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Chess8 extends PApplet {

// GLITZY CHESS PROGRAM MEANT TO BE PLAYED BY TWO PEOPLE

//------------------------------------------------------------------------------------------------------------------------|
//     DECLARE ALL OF THE CONSTANTS

//------------------------------|
// Chess Constants
Space[][] b1 = new Space[8][8];
Board master = new Board(b1);
Space starting, destination;
int clicks = 0;
int turn = 0;
int red, blue;
boolean gameOver = false;
boolean newGame = false;
boolean check = false;
int redTaken = 0;
int blueTaken = 0;
enum chessPiece { 
  EMPTY, PAWN, ROOK, KNIGHT, 
    BISHOP, QUEEN, KING;
}

//------------------------------|
// spiral constants
int acc = 0;
int streakLength = 10;
int spaceSize = 40;
int squareSize = spaceSize/(spaceSize/10);
public int offset(int s) {
  return (s/2) + (s/(s/10));
}

//------------------------------|
// selection constants
int spaceSize1 = 20;  // should be multiple of 10
int squareSize1 = spaceSize1/(spaceSize1/10);
int offset = (spaceSize1 / 2) + squareSize1; 
boolean shouldBeDrawing = false;
int globalX = 0;
int globalY = 0;
int accSelected = 0;

//------------------------------|
// explosion constants
int x, y;
int size = 50;
int sec = size;
boolean explode = false;
boolean greyExplosion = false;
Pieces p = new Pieces();

//------------------------------------------------------------------------------------------------------------------------|  
//     SETUP AND DRAW THE WORLD

// setup the canvas
public void setup() {
  background(0);
  
  master.initBoard();
  p.initParts();
}

// draw the world
public void draw() {
  fancyBackgroundSpiral(); 
  if (newGame == true) {
     resetBoard();
  } else if ((turn < 10/*108*/) || (master.inCheckMate())){
     ifNotOver();
  } else {
     gameOverScreen();
  }
}

// helper method
public void ifNotOver() {
  master.drawBoard(); // draw the board  
  turnHUD();  // the turn indicator 
  spaceSelected();  // is a space selected?
  explosion();  // is killing piece?
  greyExplosion();  // is moving without killing?
  takenPieces();  // draw the taken pieces
}

//------------------------------------------------------------------------------------------------------------------------|
//------------------------------------------------------------------------------------------------------------------------|
//    ALL THE BOARD RULES

// class for initializing and drawing board 
public class Board {
  
  // constructor and empty array
  Space[][] board;
  Board(Space[][] board) {
    this.board = board;
  }
  
  //------------------------------------------------------------------------------------------------------------------------|
  //     SET UP THE BOARD
  
  // initialize the Spaces of the board
  public void initBoard() {
    for (int a=0; a<8; a++) {
      for (int b=0; b<8; b++) { 
        int pieceDir = ((a > 1) && (a < 6)) ? 0 : ((a < 2) ? 1 : -1);
        int c = color((((a % 2) + (b % 2)) % 2 == 1) ? 0 : 255);
        Piece p = new Piece(pieceName(a, b), pieceDir);
        board[a][b] = new Space(b, a, c, p);
      }
    }
  }

  // returns the initial chessPiece of loc(b, a)
  private chessPiece pieceName(int a, int b) {
    if ((a < 1) || (a > 6)) {
      chessPiece cp;
      switch(b) {
      case 0: cp = chessPiece.ROOK; break;
      case 1: cp = chessPiece.KNIGHT; break;
      case 2: cp = chessPiece.BISHOP; break;
      case 3: cp = chessPiece.QUEEN; break;
      case 4: cp = chessPiece.KING; break;
      case 5: cp = chessPiece.BISHOP; break;
      case 6: cp = chessPiece.KNIGHT; break;
      case 7: cp = chessPiece.ROOK; break;
      default: cp = chessPiece.EMPTY; break;
      } return cp;
    } else {
      return ((a < 2) || (a > 5))? chessPiece.PAWN : chessPiece.EMPTY;
    }
  }

  //------------------------------------------------------------------------------------------------------------------------|
  //     DRAW / MEASURE THE BOARD
  
  // draw the squares that make up the board
  public void drawBoard() {
    for (int a=0; a<8; a++) {
      for (int b=0; b<8; b++) {
        board[b][a].drawSpace();
      }
    }
  }
  
  // determines which Space is clicked based on mouseX, mouseY
  public Space whichSpace() {
    int pushLeft = (mouseX > 50) ? 0 : 50; 
    int pushRight = (mouseX < 450) ? 0 : -50;
    int pushUp = (mouseY > 50) ? 0 : 50;
    int pushDown = (mouseY < 450) ? 0 : -50;
    int x = ((mouseX - (mouseX %50)) - 50 + pushLeft + pushRight) / 50;
    int y = ((mouseY - (mouseY %50)) - 50 + pushUp + pushDown) / 50; 
    return board[y][x];
  }
  
  

  //------------------------------------------------------------------------------------------------------------------------|
  //     DETERMINES IF BOARD RULES ARE BROKEN  
  
  // Determines if there are no pieces between target and destination
  public Boolean noneBetween(Space s1, Space s2) {
    boolean rook = (s1.p.p == chessPiece.ROOK);
    boolean bishop = (s1.p.p == chessPiece.BISHOP);
    boolean queen = (s1.p.p == chessPiece.QUEEN);
    if (rook || bishop || queen) {
      if (rook) { return true; } 
      else if (bishop) { return true; }
      else { return true; }
    } else { return true; }
  }
    
  // in check / checkmate
  public Boolean inCheck() { return false; }
  public Boolean inCheckMate() { return false; }

  // Determines if move is legal
  public Boolean moveOkay(Space s1, Space s2) {
    // helpers to shorten conditions
    boolean identicalSpace = ((s1.x == s2.x) && (s1.y == s2.y));
    boolean wrongTurnRed = !((turn %2 == 0) && (s1.p.dir == -1));
    boolean wrongTurnBlue = !((turn %2 == 1) && (s1.p.dir == 1));
    boolean takingOwnPiece = (s1.p.dir == s2.p.dir);
    if (identicalSpace || (wrongTurnRed && wrongTurnBlue) || takingOwnPiece) {
      return false;
    } else {
      return (canMove(s1, s2) && noneBetween(s1, s2));
    }
  }

  //------------------------------------------------------------------------------------------------------------------------|
  //     SETS UP THE BOARD RULES

  // Determines if the piece on this space can make the given move
  public boolean canMove(Space start, Space dest) {
    // helpers to shorten conditions
    boolean upOne = (dest.y == (start.y + start.p.dir));
    boolean rightOne = (dest.x == start.x + 1);
    boolean leftOne = (dest.x == start.x - 1);
    boolean killingEnemy = (start.p.dir == (dest.p.dir * -1));
    boolean row1 = (start.y == 1); 
    boolean row2 = (start.y == 6); 
    boolean whitePiece = (start.p.dir == 1);
    boolean blackPiece = (start.p.dir == -1);
    boolean upTwo = (dest.y == start.y + 2);
    boolean downTwo = (dest.y == start.y - 2);
    boolean sameX = (start.x == dest.x);
    boolean sameY = (start.y == dest.y);
    boolean div0warning = ((dest.x - start.x) == 0);
    int xdiff = (div0warning)? 1 : (dest.x - start.x);
    int ydiff = (dest.y - start.y);
    boolean slopePosOne = ((ydiff/xdiff) == 1); 
    boolean slopeNegOne = ((ydiff/xdiff) == -1);
    boolean slopeAbsOne = (slopePosOne || slopeNegOne);
    boolean horTwo = ((start.x == (dest.x - 2)) || (start.x == (dest.x + 2)));
    boolean verTwo = ((start.y == (dest.y - 1)) || (start.y == (dest.y + 1)));
    boolean horOne = ((start.x == (dest.x - 1)) || (start.x == (dest.x + 1)));
    boolean verOne = ((start.y == (dest.y - 2)) || (start.y == (dest.y + 2)));
    boolean legal;

    switch(start.p.p) {
    case EMPTY : 
      legal = false; 
      break;
    case PAWN : 
      legal = ((upOne && sameX) || (upOne && (rightOne || leftOne) && killingEnemy) || 
        ((row1 && whitePiece && upTwo) || (row2 && blackPiece && downTwo) && sameX));
      break;
    case ROOK :  
      legal = (sameY || sameX);
      break;
    case KNIGHT :  
      legal = ((horTwo && verTwo) || (horOne && verOne));
      break;
    case BISHOP : 
      legal = slopeAbsOne; 
      break;
    case QUEEN :  
      legal = (sameY || sameX || slopeAbsOne);
      break;
    case KING :  
      legal = ((sameX && verOne) || (horOne && (sameY || verOne))); 
      break;
    default : 
      legal = false;
    } 
    return legal;
  }   
}
//------------------------------------------------------------------------------------------------------------------------|
//------------------------------------------------------------------------------------------------------------------------|
//  HELPER CLASSES FOR THE EXPLOSION

// class containing the debris that is flung away 
public class Debris {
  // draw a piece of debris
  public void drawDebris(int x, int y, int sec) {
    noStroke(); fill(255, sec * 4, sec);
    rect(x + random(-sec, sec), y + random(-sec, sec), 5, 5);
  }  
  
  // draw a grey of debris
  public void drawGreyDebris(int x, int y, int sec) {
    noStroke(); fill(sec * 3, sec * 3, sec * 3);
    rect(x + random(-sec, sec), y + random(-sec, sec), 5, 5);
  }  
}

// class to hold all the debris
public class Pieces { 
  Debris[] parts = new Debris[100];
  public void initParts() {
    for (int i=0; i < 100; i++) {
       parts[i] = new Debris();
    }
  };
}
//------------------------------------------------------------------------------------------------------------------------|
//------------------------------------------------------------------------------------------------------------------------|
//     PRINT METHODS

// prints rectangle to screen
public void drawShape(int r, int b) {
  fill(r, 0, b); 
  rect(106 + (turn * 30/*should be 3*/), 12, 23, 23);
}

// draw squares around the board
public void drawSquares(int c, int acc, int x, int y, int s, int offset) {
  fill(c); 
  rect(x - offset, y - offset + acc, s, s); // left side
  rect(x - offset + acc, y + offset, s, s); // top side
  rect(x + offset, y + offset - acc, s, s); // right side
  rect(x + offset - acc, y - offset, s, s); // bottom side
}

// draw squares around a selected area
public void drawSquares1(int c, int acc, int x, int y) {
  fill(c); 
  rect(x - (offset + 5), y - (offset - 5) + acc, squareSize1, squareSize1); // left side
  rect(x - (offset - 5) + acc, y + (offset - 5), squareSize1, squareSize1); // top side
  rect(x + (offset - 5), y + (offset - 5) - acc, squareSize1, squareSize1); // right side
  rect(x + (offset - 5) - acc, y - (offset + 5), squareSize1, squareSize1); // bottom side    
}

// prints the game over text to screen
public void printGameOver() {
  textSize(50);
  fill(255, 0, 0);
  text("GAME OVER", 100, 200);
}

//------------------------------------------------------------------------------------------------------------------------|
//     MOUSE CLICK METHOD

// the click method
public void mouseClicked() {
  if (gameOver == true) {
    printGameOver();
  } 
  else if ((clicks % 2) == 0) {
    starting = (master.whichSpace());
    starting.selected(true); 
    if (starting.p.p != chessPiece.EMPTY) {
       shouldBeDrawing = true;
    }
    clicks += 1;
  } 
  else if ((clicks % 2) == 1) {
    destination = (master.whichSpace());
    shouldBeDrawing = false;
    if (master.moveOkay(starting, destination)) {
      if (destination.p.p != chessPiece.EMPTY) {
        explode = true; sec = 0; 
        x = destination.spaceX(); 
        y = destination.spaceY();
        if (destination.p.dir == 1) {
          redTaken = redTaken + 1;
        }
        else {
          blueTaken = blueTaken + 1;
        }
      }
      else {
        greyExplosion = true; sec = 0; 
        x = destination.spaceX(); 
        y = destination.spaceY();
      }
      destination.p = starting.p;
      starting.p = new Piece(chessPiece.EMPTY, 0);
      turn +=1;  
    }
    starting.selected(false);
    clicks += 1;
  }
}

//------------------------------------------------------------------------------------------------------------------------|
//     KEY PRESS METHOD

// key press
public void keyPressed() 
{
    switch(key) {
     case 'n':
       newGame = true;
       break;
     case 'e':
       turn = 999;
       break;
    }
}

//------------------------------------------------------------------------------------------------------------------------|
//------------------------------------------------------------------------------------------------------------------------|
//  HELPER CLASSES FOR THE DRAW METHOD

// draw the fancy baground design
public void fancyBackgroundSpiral() {
  for (int i = 0; i < 25; i++) {
    drawSquares(      
      color((acc/streakLength) % 255, acc%255, (acc*streakLength) % 255), 
      acc % (490 - (i*20)), 
      245, 245, 
      (squareSize/10)*12, 
      offset(470 - (i*20)));
   }  acc = acc + 10;  
}

// reset the board
public void resetBoard() {
  master.initBoard();
  p.initParts();
  turn = 0;
  clicks = 0;
  redTaken = 0;
  blueTaken = 0;
  newGame = false;
  gameOver = false;
}

// game over screen
public void gameOverScreen() {
  textSize(50); fill(255);
  text("GAME OVER", 100, 250);
  textSize(30);
  text("press 'n' to restart game", 70, 320);
}

// turn indicators
public void turnHUD() {
   // set turn colors
   blue = ((turn % 2) == 0) ? 255 : 0;
   red = ((turn % 2) == 1) ? 255 : 0; 
      
   // the line
   fill(blue,0,red);
   rect(103, 18, 350, 8);
      
   //bg for text
   fill(0);
   rect(23, 8, 80, 30);
   rect(448, 8, 8, 30);
      
   // the text and turn indicator
   drawShape(red, blue);
   textSize(24); fill(255);
   text("Turn : ", 30, 31);
}

// taken pieces
public void takenPieces() {
    // draw the taken red pieces
    for (int r = 0; r < redTaken; r++) {
      fill(255,0,0);
      rect(10 + (r * 20), 460, 15, 15);
    }
      
    // draw the taken blue pieces
    for (int b = 0; b < blueTaken; b++) {
       fill(0,0,255);
       rect(10 + (b * 20), 480, 15, 15);
    }  
}

// explosion
public void explosion() {
  if (explode) {
     explode = (sec > (size - 1)) ? false : true;
     sec = sec + 2;
     for (int i=0; i < 50; i++) {
         p.parts[i].drawDebris(x, y, sec);
     }
  }   
}

// grey explosion
public void greyExplosion() {
  if (greyExplosion) {
    greyExplosion = (sec > (size - 1)) ? false : true;
    sec = sec + 4;
    for (int i=0; i < 50; i++) {
      p.parts[i].drawGreyDebris(x, y, sec);
    }
  }
}

// is selected
public void spaceSelected() {
  if (shouldBeDrawing) {    
     drawSquares1(color(random(0, 255), random(0, 255), random(0, 255)), accSelected, globalX, globalY);
     accSelected = (accSelected + 10) % (spaceSize1 + (2 * squareSize1));
  }
}
//------------------------------------------------------------------------------------------------------------------------|
//     CLASS FOR PIECE

// class for a single piece
public class Piece { 
  int dir; // direction piece faces
  chessPiece p; // which piece it is
  
  Piece(chessPiece p, int dir) {
    this.p = p;
    this.dir = dir;
  }
  
  // draw a single piece
  public char drawPiece() {
    char c;
    switch (p) {
      case EMPTY : c = 'E'; break;
      case PAWN : c = 'P'; break;
      case ROOK : c = 'R'; break;
      case KNIGHT : c = 'k'; break;
      case BISHOP : c = 'B'; break;
      case QUEEN : c = 'Q'; break;
      case KING : c = 'K'; break;
      default: c = 'E'; break;
    } return c;
  } 
}
//------------------------------------------------------------------------------------------------------------------------|
//------------------------------------------------------------------------------------------------------------------------|
//    CLASS FOR SPACES

// class for a single space
public class Space {
  int c; 
  int x, y;
  Piece p; 
  boolean selected = false;

  Space(int x, int y, int c, Piece p) {
    this.x = x;
    this.y = y;
    this.c = c;
    this.p = p;
  }

  // draws the space
  public void drawSpace() {
    fill(c);
    if (selected == false) {
      rect(50+(x*50), 50+(y*50), 50, 50);
    }
    else if ((selected == true) && (p.p == chessPiece.EMPTY)) {
      rect(50+(x*50), 50+(y*50), 50, 50);
    }
    
    int r = (p.dir == 1) ? 255 : 0;
    int b = (p.dir == -1) ? 255 : 0;
    int g = (selected) ? 255: 0;

    if ((p.dir != 0)) {
      fill(c); noStroke();
      rect(60+(x*50), 60+(y*50), 31, 31);
     
      fill(r, g, b); stroke(0);
      rect(62+(x*50), 62+(y*50), 25, 25);
      
      fill(255); 
      textSize(20);
      text(p.drawPiece(), 70+(x*50), 83+(y*50));
    }
  }

  // selected
  public void selected(boolean b) {
    selected = b;
    globalX = 75+(x*50);
    globalY = 75+(y*50);
  } 

  //method to return x value of selected square
  public int spaceX () {
     return 75+(x*50);
  }
  
  //method to return y value of selected square
  public int spaceY () {
     return 75+(y*50);
  }

  // true if occupied
  public Boolean isOccupied() {
    return (this.p.p == chessPiece.EMPTY) ? false : true;
  }
}
//------------------------------------------------------------------------------------------------------------------------|
  public void settings() {  size(500, 500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Chess8" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
