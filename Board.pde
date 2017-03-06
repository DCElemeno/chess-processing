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
        color c = color((((a % 2) + (b % 2)) % 2 == 1) ? 0 : 255);
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
  boolean canMove(Space start, Space dest) {
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