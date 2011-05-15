package dbg.hid2pwm;

public class InputState {
  int rangeX;
  int rangeY;


  public InputState(int rangeX, int rangeY) {
    this.rangeX = rangeX;
    this.rangeY = rangeY;
  }

  public int getRangeX() {
    return rangeX;
  }

  public void setRangeX(int rangeX) {
    this.rangeX = rangeX;
  }

  public int getRangeY() {
    return rangeY;
  }

  public void setRangeY(int rangeY) {
    this.rangeY = rangeY;
  }

  @Override
  public String toString() {
    return "InputState{" +
            "rangeX=" + rangeX +
            ", rangeY=" + rangeY +
            '}';
  }
}
