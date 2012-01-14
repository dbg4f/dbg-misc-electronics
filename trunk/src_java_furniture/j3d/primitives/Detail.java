package j3d.primitives;

/**
 * Detail
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class Detail {

  final OrthogonalBox box;
  final OrthogonalPlate orientation;
  final DetailType detailType;
  final String name;

  boolean visible = true;

  public Detail(OrthogonalBox box, DetailType detailType, String name, OrthogonalPlate orientation) {
    this.box = box;
    this.detailType = detailType;
    this.name = name;
    this.orientation = orientation;
  }

  public Detail cloneDetail () {
    return new Detail(box.cloneBox(), detailType, name, orientation);
  }

  public OrthogonalBox getBox() {
    return box;
  }

  public float[] getSize() {
    return getSize(true);
  }

  public float[] getSize(boolean rotated) {
    return rotated ? orientation.getPlateSizes(box.getDim()) : new float[] {orientation.getPlateSizes(box.getDim())[1], orientation.getPlateSizes(box.getDim())[0]};
  }

  public int getSize1(boolean rotated) {
    return Math.abs(Math.round(getSize(rotated)[0] * 1000f));
  }

  public int getSize2(boolean rotated) {
    return Math.abs(Math.round(getSize(rotated)[1] * 1000f));
  }

  public int[] getOrderedSizes() {
    return getSize1(false) > getSize2(false) ? new int[] {getSize1(false), getSize2(false)} : new int[] {getSize2(false), getSize1(false)};
  }

  public String getOrderedSizesString() {
    return getOrderedSizes()[0] + "x" + getOrderedSizes()[1];
  }

  public DetailType getDetailType() {
    return detailType;
  }

  public String getName() {
    return name;
  }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
  public String toString() {
    return name + " " + getOrderedSizesString() + " " + detailType + " " + getOrderedSizes()[0] * getOrderedSizes()[1] / 1000000f;

  }
}
