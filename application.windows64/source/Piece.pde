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