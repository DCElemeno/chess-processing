//------------------------------------------------------------------------------------------------------------------------|
//    CLASS FOR SPACES

// class for a single space
public class Space {
  color c; 
  int x, y;
  Piece p; 
  boolean selected = false;

  Space(int x, int y, color c, Piece p) {
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