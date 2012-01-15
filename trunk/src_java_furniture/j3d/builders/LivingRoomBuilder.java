package j3d.builders;

import j3d.primitives.OrthogonalBox;
import j3d.primitives.OrthogonalPlate;
import j3d.primitives.DetailType;
import j3d.primitives.Detail;
import j3d.RandomColor;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Color3f;
import java.util.*;

public class LivingRoomBuilder {

  public static final float H           = 0.016f;

  public static final float L_GAP1 = 0.40f - H;
  public static final float L_GAP2 = 0.40f - H;
  public static final float L_GAP3 = 0.80f - H;
  public static final float L_GAP4 = 0.40f - H;
  public static final float L_GAP5 = 0.50f - H;

  public static final float WALL_DIST   = 0.003f;

  public static final float L_FULL = L_GAP1 + L_GAP2 + L_GAP3 + L_GAP4 + L_GAP5;

  public static final float H_LOWER = 0.55f;
  public static final float H_UPPER = 1.44f;

  public static final float H_FULL = H_LOWER + H_UPPER;

  public static final float W_SMALL = 0.30f;
  public static final float W_LARGE = 0.45f;

  public static final float FLOOR_BASE  = 0.05f;



  public static final Color3f COLOR_WALL = new Color3f(0.9f, 0.9f, 0.4f);
  public static final Color3f COLOR_FLOOR = new Color3f(0.7f, 0.5f, 0.2f);

  public static final Color3f COLOR_RED = new Color3f(1f, 0f, 0f);
  public static final Color3f COLOR_GREEN = new Color3f(0f, 1f, 0f);
  public static final Color3f COLOR_BLUE = new Color3f(0f, 0f, 1f);
  public static final Color3f COLOR_YELLOW = new Color3f(1f, 1f, 0f);


  public static final Map<DetailType, Color3f[]> PLATE_COLORS = new HashMap<DetailType, Color3f[]>() {
    {
      //put(DetailType.VERTICAL_BASE,     new Color3f[]{COLOR_RED,    COLOR_GREEN,  COLOR_BLUE});
      put(DetailType.VERTICAL_RIB,      new Color3f[]{COLOR_RED,    COLOR_RED,    COLOR_BLUE});
      put(DetailType.HORIZONTAL_BASE,   new Color3f[]{COLOR_YELLOW, COLOR_RED,    COLOR_RED});
      put(DetailType.SHELF,             new Color3f[]{COLOR_FLOOR,  COLOR_GREEN,  COLOR_GREEN});
      put(DetailType.COMPARTMENT_DOOR,  new Color3f[]{COLOR_RED,    COLOR_GREEN,  COLOR_RED});
      put(DetailType.BACK_WALL,         new Color3f[]{COLOR_RED,    COLOR_GREEN,  COLOR_BLUE});
      put(DetailType.CHEST_BASE,        new Color3f[]{COLOR_GREEN,  COLOR_GREEN,  COLOR_GREEN});
      put(DetailType.CHEST_BACK_WALL,   new Color3f[]{COLOR_BLUE,   COLOR_BLUE,   COLOR_BLUE});
      put(DetailType.CHEST_CAP,         new Color3f[]{COLOR_GREEN,  COLOR_GREEN,  COLOR_GREEN});
      put(DetailType.CHEST_BOX_FRONT,   new Color3f[]{COLOR_BLUE,   COLOR_BLUE,   COLOR_BLUE});
      put(DetailType.CHEST_BOX_BACK,    new Color3f[]{COLOR_GREEN,  COLOR_GREEN,  COLOR_GREEN});
    }

  };


  private void setColors(DetailType detailType, OrthogonalBox box) {
    //if (PLATE_COLORS.containsKey(detailType)) {
      /*
      box.setPlateColor(OrthogonalPlate.XY, PLATE_COLORS.get(detailType)[0]);
      box.setPlateColor(OrthogonalPlate.XZ, PLATE_COLORS.get(detailType)[1]);
      box.setPlateColor(OrthogonalPlate.YZ, PLATE_COLORS.get(detailType)[2]);
      */

      Color3f c = RandomColor.newColor();

      box.setPlateColor(OrthogonalPlate.XY, c);
      box.setPlateColor(OrthogonalPlate.XZ, c);
      box.setPlateColor(OrthogonalPlate.YZ, c);

      box.rebuild();
    //}
  }

  private Detail verticalBaseUpper(float wallDistance, float h, float floorDist, String name, DetailType detailType) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(wallDistance, 0f, floorDist), new Vector3f(H, W_SMALL, h));
    return new Detail(orthogonalBox, detailType, name, OrthogonalPlate.YZ);
  }

  private Detail verticalBaseLower(float wallDistance, float h, float floorDist, float width, String name, DetailType detailType) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(wallDistance, 0f, floorDist), new Vector3f(H, width, h));
    return new Detail(orthogonalBox, detailType, name, OrthogonalPlate.YZ);
  }


  private Detail horizontalBase(float floorDistance, String name, float width) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(H, 0f, floorDistance), new Vector3f(L_FULL - (2.0f * H), width, H));
    return new Detail(orthogonalBox, DetailType.HORIZONTAL_BASE, name, OrthogonalPlate.XY);
  }

  private Detail horizontalPlate(float floorDistance, float width, float length, float sideDist, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(sideDist, 0f, floorDistance), new Vector3f(length, width, H));
    return new Detail(orthogonalBox, DetailType.HORIZONTAL_BASE, name, OrthogonalPlate.XY);
  }



  private List<Detail> roomBounds(float size, Color3f wallColor, Color3f floorColor) {
    Point3f start1 = new Point3f(-WALL_DIST, -WALL_DIST, -WALL_DIST);
    Point3f start2 = new Point3f(-WALL_DIST + 2 * size, -WALL_DIST, -WALL_DIST);
    final OrthogonalBox floor = new OrthogonalBox(start1, new Vector3f(size * 2, size, -H));
    floor.applyDefaultColor(floorColor);
    floor.rebuild();
    final OrthogonalBox wallX = new OrthogonalBox(start1, new Vector3f(size * 2, -H, size));
    wallX.applyDefaultColor(new Color3f(wallColor.getX() + 0.08f, wallColor.getY() + 0.08f, wallColor.getZ() + 0.08f));
    wallX.rebuild();
    final OrthogonalBox wallY = new OrthogonalBox(start2, new Vector3f(-H, size, size));
    wallY.applyDefaultColor(wallColor);
    wallY.rebuild();
    return new ArrayList<Detail>() {
      {
        add(new Detail(floor, DetailType.ROOM_WALL, "floor", OrthogonalPlate.XY));
        add(new Detail(wallX, DetailType.ROOM_WALL, "wall X", OrthogonalPlate.XZ));
        add(new Detail(wallY, DetailType.ROOM_WALL, "wall Y", OrthogonalPlate.YZ));
      }
    };
  }

  

  public List<Detail> createDetails() {

    List<Detail> detials = new ArrayList<Detail>();

    detials.addAll(roomBounds(2.5f, COLOR_WALL, COLOR_FLOOR));
    /*
    detials.add(verticalBaseUpper(0f,                                               H_UPPER + 2.0f * H,  FLOOR_BASE + H_LOWER - H,    "vertUB6-side",  DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseUpper(L_GAP5,                                           H_UPPER,             FLOOR_BASE + H_LOWER,        "vertUB5",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseUpper(L_GAP5 + L_GAP4,                                  H_UPPER,             FLOOR_BASE + H_LOWER,        "vertUB4",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseUpper(L_GAP5 + L_GAP4 + L_GAP3,                         H_UPPER,             FLOOR_BASE + H_LOWER,        "vertUB3",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseUpper(L_GAP5 + L_GAP4 + L_GAP3 + L_GAP2,                H_UPPER,             FLOOR_BASE + H_LOWER,        "vertUB2",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseUpper(L_GAP5 + L_GAP4 + L_GAP3 + L_GAP2 + L_GAP1 - H,   H_UPPER + 2.0f * H,  FLOOR_BASE + H_LOWER - H,    "vertUB1-side",  DetailType.VERTICAL_BASE_LOWER));
      */

    detials.add(verticalBaseLower(L_GAP5,                                           H_LOWER + 2.0f * H + FLOOR_BASE,    0f,                    W_LARGE,  "vertLB2",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseLower(L_GAP5 + L_GAP4,                                  H_LOWER,                            FLOOR_BASE + H,        W_LARGE,  "vertLB3",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseLower(L_GAP5 + L_GAP4 + L_GAP3,                         H_LOWER,                            FLOOR_BASE + H,        W_LARGE,  "vertLB4",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseLower(L_GAP5 + L_GAP4 + L_GAP3 + L_GAP2,                H_LOWER + 2.0f * H + FLOOR_BASE,    0f,                    W_LARGE,  "vertLB5",       DetailType.VERTICAL_BASE_LOWER));

    detials.add(verticalBaseLower(0f,                                               H_LOWER + 2.0f * H + FLOOR_BASE,    0f,                    W_SMALL,  "vertLB1",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseLower(L_GAP5 - H,                                       H_LOWER + 2.0f * H + FLOOR_BASE,    0f,                    W_SMALL,  "vertLB2A",      DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseLower(L_GAP5 + L_GAP4 + L_GAP3 + L_GAP2 + L_GAP1,       H_LOWER + 2.0f * H + FLOOR_BASE,    0f,                    W_SMALL,  "vertLB6",       DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBaseLower(L_GAP5 + L_GAP4 + L_GAP3 + L_GAP2 + H,            H_LOWER + 2.0f * H + FLOOR_BASE,    0f,                    W_SMALL,  "vertLB5A",      DetailType.VERTICAL_BASE_LOWER));

    // TODO: all lower ribs
    // TODO: back-side plates
    detials.add(horizontalPlate(FLOOR_BASE,                 W_LARGE, L_GAP2 + L_GAP3 + L_GAP4 - H,          L_GAP5 + H,                                     "lowerBase1A"));
    detials.add(horizontalPlate(FLOOR_BASE,                 W_SMALL, L_GAP1 - 2.0f * H,                     L_GAP2 + L_GAP3 + L_GAP4 + L_GAP5 + 2.0f * H,   "lowerBase2A"));
    detials.add(horizontalPlate(FLOOR_BASE,                 W_SMALL, L_GAP5 - 2.0f * H,                     H,                                              "lowerBase3A"));

    detials.add(horizontalPlate(FLOOR_BASE + H + H_LOWER,   W_LARGE, L_GAP2 + L_GAP3 + L_GAP4 - H,          L_GAP5 + H,                                     "lowerBase1B"));
    detials.add(horizontalPlate(FLOOR_BASE + H + H_LOWER,   W_SMALL, L_GAP1 - 2.0f * H,                     L_GAP2 + L_GAP3 + L_GAP4 + L_GAP5 + 2.0f * H,   "lowerBase2B"));
    detials.add(horizontalPlate(FLOOR_BASE + H + H_LOWER,   W_SMALL, L_GAP5 - 2.0f * H,                     H,                                              "lowerBase3B"));

    return detials;
  }

  public void createEnv(BranchGroup objRoot) {
    List<Detail> details = createDetails();


    for (Detail detail : details) {
      objRoot.addChild(detail.getBox());
    }

      setAppearance(details);

  }

    public void setAppearance(List<Detail> details) {
        setColors(details);

        makeWired(details, /*DetailType.COMPARTMENT_DOOR, DetailType.ROOM_WALL,*/ DetailType.values());
    }

    private void setColors(List<Detail> details) {
    for (Detail d : details) {
      setColors(d.getDetailType(), d.getBox());
    }
  }

  private void filterOut(List<Detail> details, DetailType... typesToExclude) {
    Set<DetailType> types = new HashSet<DetailType>(Arrays.asList(typesToExclude));
    List<Detail> exclueDetails = new ArrayList<Detail>();
    for (Detail d : details) {
      if (types.contains(d.getDetailType())) {
        exclueDetails.add(d);
      }
    }
    details.removeAll(exclueDetails);
  }

  private void makeWired(List<Detail> details, DetailType... typesToWire) {
    Set<DetailType> types = new HashSet<DetailType>(Arrays.asList(typesToWire));

    for (Detail d : details) {
      if (types.contains(d.getDetailType())) {
        d.getBox().rebuildWired();
      }
    }
  }

}
