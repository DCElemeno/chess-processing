//------------------------------------------------------------------------------------------------------------------------|
//  HELPER CLASSES FOR THE DRAW METHOD

// draw the fancy baground design
void fancyBackgroundSpiral() {
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
void resetBoard() {
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
void gameOverScreen() {
  textSize(50); fill(255);
  text("GAME OVER", 100, 250);
  textSize(30);
  text("press 'n' to restart game", 70, 320);
}

// turn indicators
void turnHUD() {
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
void takenPieces() {
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
void explosion() {
  if (explode) {
     explode = (sec > (size - 1)) ? false : true;
     sec = sec + 2;
     for (int i=0; i < 50; i++) {
         p.parts[i].drawDebris(x, y, sec);
     }
  }   
}

// grey explosion
void greyExplosion() {
  if (greyExplosion) {
    greyExplosion = (sec > (size - 1)) ? false : true;
    sec = sec + 4;
    for (int i=0; i < 50; i++) {
      p.parts[i].drawGreyDebris(x, y, sec);
    }
  }
}

// is selected
void spaceSelected() {
  if (shouldBeDrawing) {    
     drawSquares1(color(random(0, 255), random(0, 255), random(0, 255)), accSelected, globalX, globalY);
     accSelected = (accSelected + 10) % (spaceSize1 + (2 * squareSize1));
  }
}