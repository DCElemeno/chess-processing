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
int offset(int s) {
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
void setup() {
  background(0);
  size(500, 500);
  master.initBoard();
  p.initParts();
}

// draw the world
void draw() {
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
void ifNotOver() {
  master.drawBoard(); // draw the board  
  turnHUD();  // the turn indicator 
  spaceSelected();  // is a space selected?
  explosion();  // is killing piece?
  greyExplosion();  // is moving without killing?
  takenPieces();  // draw the taken pieces
}

//------------------------------------------------------------------------------------------------------------------------|