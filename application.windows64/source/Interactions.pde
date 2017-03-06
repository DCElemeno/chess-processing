//------------------------------------------------------------------------------------------------------------------------|
//     PRINT METHODS

// prints rectangle to screen
void drawShape(int r, int b) {
  fill(r, 0, b); 
  rect(106 + (turn * 30/*should be 3*/), 12, 23, 23);
}

// draw squares around the board
void drawSquares(color c, int acc, int x, int y, int s, int offset) {
  fill(c); 
  rect(x - offset, y - offset + acc, s, s); // left side
  rect(x - offset + acc, y + offset, s, s); // top side
  rect(x + offset, y + offset - acc, s, s); // right side
  rect(x + offset - acc, y - offset, s, s); // bottom side
}

// draw squares around a selected area
void drawSquares1(color c, int acc, int x, int y) {
  fill(c); 
  rect(x - (offset + 5), y - (offset - 5) + acc, squareSize1, squareSize1); // left side
  rect(x - (offset - 5) + acc, y + (offset - 5), squareSize1, squareSize1); // top side
  rect(x + (offset - 5), y + (offset - 5) - acc, squareSize1, squareSize1); // right side
  rect(x + (offset - 5) - acc, y - (offset + 5), squareSize1, squareSize1); // bottom side    
}

// prints the game over text to screen
void printGameOver() {
  textSize(50);
  fill(255, 0, 0);
  text("GAME OVER", 100, 200);
}

//------------------------------------------------------------------------------------------------------------------------|
//     MOUSE CLICK METHOD

// the click method
void mouseClicked() {
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
void keyPressed() 
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