package j3d.primitives;

import java.awt.*;

public class PlacedDetail {

  private Detail detail;
  private int x;
  private int y;
  private boolean rotated;


  public PlacedDetail(Detail detail) {
    this.detail = detail;
  }

  public void draw(Graphics g) {

    int size1 = detail.getSize1(rotated);
    int size2 = detail.getSize2(rotated);

    g.drawRect(x, y, size1, size2);

    g.drawString(String.valueOf(size1), x + size1/2, y);
    g.drawString(String.valueOf(size1), x, y + size2/2);


  }

  public void placeRight(PlacedDetail d) {
    x = d.x + d.detail.getSize1(rotated) + 50;
    y = d.y;
  }

  public void placeDown(PlacedDetail d) {
    x = d.x;
    y = d.y + d.detail.getSize2(rotated) + 50;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setRotated(boolean rotated) {
    this.rotated = rotated;
  }

  @Override
  public String toString() {
    return "PlacedDetail{" +
            "detail=" + detail +
            ", x=" + x +
            ", y=" + y +
            ", rotated=" + rotated +
            '}';
  }
}
