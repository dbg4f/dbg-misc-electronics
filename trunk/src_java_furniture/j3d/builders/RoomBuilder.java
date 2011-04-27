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

public class RoomBuilder {

  public static final float H           = 0.016f;
  public static final float VERT_CLR_H  = 0.001f;
  public static final float FULL_H      = 2.52f;
  public static final float FULL_W      = 0.45f;
  public static final float FULL_L      = 1.50f;
  public static final float WALL_DIST   = 0.003f;
  public static final float COMP_H      = 0.45f;
  public static final float SHELF_CLR   = 0.001f;
  public static final float FLOOR_BASE  = 0.05f;
  public static final float COMP_DOOR_D1 = 0.02f;
  public static final float COMP_DOOR_D2 = 0.08f;
  public static final float COMP_DOOR_CLR_H = 0.008f;
  public static final float SHELF_W       = FULL_W - 0.12f - H;
  public static final float SHELF_L       = ((FULL_L - (3f * H)) / 2f) - 2f * SHELF_CLR;
  public static final float COMP_DOOR_L   = (FULL_L - 2f * H) / 2f + 0.03f;
  public static final float COMP_DOOR_H   = COMP_H - 2f * COMP_DOOR_CLR_H;
  public static final float H_VERT_UP     = COMP_H + 2f * H;
  public static final float H_VERT_LOW    = FULL_H - H_VERT_UP - VERT_CLR_H;
  public static final float MAIN_COMP_WINDOW_H = H_VERT_LOW - FLOOR_BASE - H - COMP_H - H - H;
  public static final float MAIN_COMP_WINDOW_L = FULL_L - 2f*H;


  public static void main(String[] args) {
    System.out.println("MAIN_COMP_WINDOW_H = " + MAIN_COMP_WINDOW_H);
    System.out.println("MAIN_COMP_WINDOW_L = " + MAIN_COMP_WINDOW_L);
  }

  public static final float H_VERT_CHEST    = 0.75f;
  public static final float L_CHEST         = 0.5f;
  public static final float CHEST_BOX_SIDE_CLR = 0.013f;
  public static final float CHEST_BOX_VERT_GAP = 0.003f;
  public static final float L_CHEST_CAP     = L_CHEST + 2f*H;
  public static final float CHEST_BOX_H     = (H_VERT_CHEST - FLOOR_BASE - H + CHEST_BOX_VERT_GAP)/4f;


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

  private Detail verticalBase(float wallDistance, float h, float floorDist, String name, DetailType detailType) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(wallDistance, 0f, floorDist), new Vector3f(H, FULL_W, h));
    return new Detail(orthogonalBox, detailType, name, OrthogonalPlate.YZ);
  }

  private Detail verticalChestBase(float wallDistance, float h, float floorDist, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(wallDistance, 0f, floorDist), new Vector3f(H, FULL_W - H, h));
    return new Detail(orthogonalBox, DetailType.CHEST_VERTICAL_BASE, name, OrthogonalPlate.YZ);
  }

  private Detail centralVerticalRib(float h, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(FULL_L / 2f - H / 2f, H, h), new Vector3f(H, SHELF_W, COMP_H));
    return new Detail(orthogonalBox, DetailType.VERTICAL_RIB, name, OrthogonalPlate.YZ);
  }

  private Detail horizontalBase(float floorDistance, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(H, 0f, floorDistance), new Vector3f(FULL_L - (2.0f * H), FULL_W, H));
    return new Detail(orthogonalBox, DetailType.HORIZONTAL_BASE, name, OrthogonalPlate.XY);
  }

  private Detail chestCap(String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(FULL_L, 0f, H_VERT_CHEST), new Vector3f(L_CHEST_CAP, FULL_W, H));
    return new Detail(orthogonalBox, DetailType.CHEST_CAP, name, OrthogonalPlate.XY);
  }

  private Detail chestHorizontalBase(String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(FULL_L + H, H, FLOOR_BASE), new Vector3f(L_CHEST, FULL_W - 2f*H, H));
    return new Detail(orthogonalBox, DetailType.CHEST_BASE, name, OrthogonalPlate.XY);
  }

  private Detail backWall(float floorDistance, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(H, 0f, floorDistance), new Vector3f(FULL_L - (2.0f * H), H, COMP_H));
    return new Detail(orthogonalBox, DetailType.BACK_WALL, name, OrthogonalPlate.XZ);
  }

  private Detail chestBackWall(String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(FULL_L + H, 0f, 0f), new Vector3f(L_CHEST, H, H_VERT_CHEST));
    return new Detail(orthogonalBox, DetailType.CHEST_BACK_WALL, name, OrthogonalPlate.XZ);
  }

  private Detail lowerRib(float wallDistance, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(H, wallDistance, 0f), new Vector3f(FULL_L - (2.0f * H), H, FLOOR_BASE));
    return new Detail(orthogonalBox, DetailType.LOWER_RIB, name, OrthogonalPlate.XZ);
  }

  private Detail chestFront(float wallDistance, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(FULL_L + H, wallDistance, 0f), new Vector3f(L_CHEST, H, FLOOR_BASE));
    return new Detail(orthogonalBox, DetailType.CHEST_FRONT, name, OrthogonalPlate.XZ);
  }

  private Detail shelf(float floorDistance, float wallDistance, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(wallDistance, H, floorDistance), new Vector3f(SHELF_L, SHELF_W, H));
    return new Detail(orthogonalBox, DetailType.SHELF, name, OrthogonalPlate.XY);
  }

  private Detail compDoor(float floorDistance, float wallDistance, float depth, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(wallDistance, FULL_W - H - depth, floorDistance), new Vector3f(COMP_DOOR_L, H, COMP_DOOR_H));
    return new Detail(orthogonalBox, DetailType.COMPARTMENT_DOOR, name, OrthogonalPlate.XZ);
  }
/*
  private Detail compMainDoor(float floorDistance, float wallDistance, float depth, String name) {
    final OrthogonalBox orthogonalBox = new OrthogonalBox(new Point3f(wallDistance, FULL_W - H - depth, floorDistance), new Vector3f(COMP_DOOR_L, H, COMP_MAIN_DOOR_H));
    return new Detail(orthogonalBox, DetailType.COMPARTMENT_MAIN_DOOR, name, OrthogonalPlate.XZ);
  }
  */
  private List<Detail> chestBox(float wallDist, float backWallDist, float floorDist, final String name) {
    Point3f start = new Point3f(wallDist, backWallDist, floorDist);
    final OrthogonalBox front = new OrthogonalBox(start, new Vector3f(L_CHEST_CAP-0.003f, H, CHEST_BOX_H));
    final float chestBoxH = CHEST_BOX_H - 2f* CHEST_BOX_SIDE_CLR;
    final float chestBoxDepth = FULL_W - H - CHEST_BOX_SIDE_CLR - H;
    final Vector3f sideSizes = new Vector3f(H, -chestBoxDepth, chestBoxH);
    final Vector3f footSizes = new Vector3f(L_CHEST - 2f* CHEST_BOX_SIDE_CLR - 2f*H, -chestBoxDepth, H);
    final Vector3f backSizes = new Vector3f(L_CHEST-2f*H-2f* CHEST_BOX_SIDE_CLR, H, chestBoxH - H);

    final OrthogonalBox side1 = new OrthogonalBox(new Point3f(start.x + H + CHEST_BOX_SIDE_CLR,                start.y, start.z + CHEST_BOX_SIDE_CLR), sideSizes);
    final OrthogonalBox side2 = new OrthogonalBox(new Point3f(start.x + H + L_CHEST - CHEST_BOX_SIDE_CLR - H,  start.y, start.z + CHEST_BOX_SIDE_CLR), sideSizes);
    final OrthogonalBox foot  = new OrthogonalBox(new Point3f(start.x + H + CHEST_BOX_SIDE_CLR + H,            start.y, start.z + CHEST_BOX_SIDE_CLR), footSizes);
    final OrthogonalBox back  = new OrthogonalBox(new Point3f(start.x + H + CHEST_BOX_SIDE_CLR + H,            start.y - chestBoxDepth, start.z + CHEST_BOX_SIDE_CLR + H), backSizes);

    return new ArrayList<Detail>() {
      {
        add(new Detail(front, DetailType.CHEST_BOX_FRONT, name + "-front",  OrthogonalPlate.XZ));
        add(new Detail(side1, DetailType.CHEST_BOX_SIDE, name + "-side1",   OrthogonalPlate.YZ));
        add(new Detail(side2, DetailType.CHEST_BOX_SIDE, name + "-side2",   OrthogonalPlate.YZ));
        add(new Detail(foot,  DetailType.CHEST_BOX_FOOT, name + "-foot",    OrthogonalPlate.XY));
        add(new Detail(back,  DetailType.CHEST_BOX_BACK, name + "-back",    OrthogonalPlate.XZ));
      }
    };

  }


  private List<Detail> roomBounds(float size, Color3f wallColor, Color3f floorColor) {
    Point3f start = new Point3f(-WALL_DIST, -WALL_DIST, -WALL_DIST);
    final OrthogonalBox floor = new OrthogonalBox(start, new Vector3f(size, size, -H));
    floor.applyDefaultColor(floorColor);
    floor.rebuild();
    final OrthogonalBox wallX = new OrthogonalBox(start, new Vector3f(size, -H, size));
    wallX.applyDefaultColor(new Color3f(wallColor.getX() + 0.08f, wallColor.getY() + 0.08f, wallColor.getZ() + 0.08f));
    wallX.rebuild();
    final OrthogonalBox wallY = new OrthogonalBox(start, new Vector3f(-H, size, size));
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

    detials.add(verticalBase(0f,            H_VERT_LOW, 0f,                      "low1", DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBase(FULL_L - H,    H_VERT_LOW, 0f,                      "low2", DetailType.VERTICAL_BASE_LOWER));
    detials.add(verticalBase(0f,            H_VERT_UP, H_VERT_LOW + VERT_CLR_H,  "up1",  DetailType.VERTICAL_BASE_UPPER));
    detials.add(verticalBase(FULL_L - H,    H_VERT_UP, H_VERT_LOW + VERT_CLR_H,  "up2",  DetailType.VERTICAL_BASE_UPPER));

    detials.add(horizontalBase(FLOOR_BASE,              "1"));
    detials.add(horizontalBase(FLOOR_BASE + H + COMP_H, "2"));
    detials.add(horizontalBase(H_VERT_LOW - H,          "3"));
    detials.add(horizontalBase(H_VERT_LOW + VERT_CLR_H, "4"));
    detials.add(horizontalBase(FULL_H - H,              "5"));

    detials.add(centralVerticalRib(FULL_H - H - COMP_H, "V1"));
    detials.add(centralVerticalRib(FLOOR_BASE + H,      "V2"));

    float lowShelfBaseFloorDist = FLOOR_BASE + H + COMP_H / 2f;
    float upperShelfBaseFloorDist = FULL_H - (H + COMP_H / 2f);

    float shelfBaseY1 = H + SHELF_CLR;
    float shelfBaseY2 = FULL_L - (H + SHELF_CLR * 2f + SHELF_L);

    detials.add(shelf(lowShelfBaseFloorDist, shelfBaseY1, "S1"));
    detials.add(shelf(lowShelfBaseFloorDist, shelfBaseY2, "S2"));

    detials.add(shelf(upperShelfBaseFloorDist, shelfBaseY1, "S3"));
    detials.add(shelf(upperShelfBaseFloorDist, shelfBaseY2, "S4"));

    detials.add(compDoor(FLOOR_BASE + H + COMP_DOOR_CLR_H, H, COMP_DOOR_D1, "D1"));
    detials.add(compDoor(FLOOR_BASE + H + COMP_DOOR_CLR_H, FULL_L - H - COMP_DOOR_L, COMP_DOOR_D2, "D2"));

    detials.add(compDoor(FULL_H - H - COMP_DOOR_CLR_H - COMP_DOOR_H, H, COMP_DOOR_D1, "D3"));
    detials.add(compDoor(FULL_H - H - COMP_DOOR_CLR_H - COMP_DOOR_H, FULL_L - H - COMP_DOOR_L, COMP_DOOR_D2, "D4"));

    //detials.add(compMainDoor(FLOOR_BASE + H + COMP_H + COMP_DOOR_CLR_H, H,                         COMP_DOOR_D1, "DM1"));
    //detials.add(compMainDoor(FLOOR_BASE + H + COMP_H + COMP_DOOR_CLR_H, FULL_L - H - COMP_DOOR_L,  COMP_DOOR_D2, "DM2"));

    detials.add(backWall(FLOOR_BASE + H,              "B1"));
    detials.add(backWall(FLOOR_BASE + H + COMP_H + H, "B2"));
    detials.add(backWall(H_VERT_LOW - COMP_H - H,     "B3"));
    detials.add(backWall(FULL_H - H - COMP_H,         "B4"));

    detials.add(lowerRib(FULL_W/3f,             "LR1"));
    detials.add(lowerRib(FULL_W  - FLOOR_BASE,  "LR2"));


    detials.add(verticalChestBase(FULL_L,                 H_VERT_CHEST, 0f, "B-low1"));
    detials.add(verticalChestBase(FULL_L + L_CHEST + H,   H_VERT_CHEST, 0f, "B-low2"));
    detials.add(chestFront(FULL_W  - FLOOR_BASE, "B-front"));

    detials.add(chestCap("B-CAP"));
    detials.add(chestHorizontalBase("B-Low"));
    detials.add(chestBackWall("B-BackWall"));

    
    detials.addAll(chestBox(FULL_L, FULL_W-H, FLOOR_BASE, "box1"));
    detials.addAll(chestBox(FULL_L, FULL_W-H, FLOOR_BASE + CHEST_BOX_VERT_GAP + CHEST_BOX_H, "box2"));
    detials.addAll(chestBox(FULL_L, FULL_W-H, FLOOR_BASE + 2f*(CHEST_BOX_VERT_GAP + CHEST_BOX_H), "box3"));
    detials.addAll(chestBox(FULL_L, FULL_W-H, FLOOR_BASE + 3f*(CHEST_BOX_VERT_GAP + CHEST_BOX_H), "box4"));

    return detials;
  }

  public void createEnv(BranchGroup objRoot) {
    List<Detail> details = createDetails();

    Set<DetailType> excl = new HashSet<DetailType>(EnumSet.complementOf(DetailType.CHEST_DETAILS));

    //excl.add(DetailType.BACK_WALL);
    //excl.add(DetailType.COMPARTMENT_DOOR);


    //filterOut(details, excl.toArray(new DetailType[excl.size()]));

    //filterOut(details, DetailType.ROOM_WALL);


    details = new DetailsViewFilter().filter(details);

    for (Detail detail : details) {
      objRoot.addChild(detail.getBox());
    }

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
