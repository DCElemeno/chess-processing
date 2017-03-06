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
  void initParts() {
    for (int i=0; i < 100; i++) {
       parts[i] = new Debris();
    }
  };
}
//------------------------------------------------------------------------------------------------------------------------|