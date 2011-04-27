package j3d.primitives;

import java.util.EnumSet;

/**
 * DetailType
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public enum DetailType {

  ROOM_WALL,
  VERTICAL_BASE_UPPER,
  VERTICAL_BASE_LOWER,
  VERTICAL_RIB,
  HORIZONTAL_BASE,
  SHELF,
  COMPARTMENT_DOOR,
  //COMPARTMENT_MAIN_DOOR,
  BACK_WALL,
  LOWER_RIB,

  CHEST_VERTICAL_BASE,
  CHEST_CAP,
  CHEST_BASE,
  CHEST_FRONT,
  CHEST_BOX_FRONT,
  CHEST_BOX_FOOT,
  CHEST_BOX_SIDE,
  CHEST_BOX_BACK,
  CHEST_BACK_WALL;

  public static EnumSet<DetailType> CHEST_DETAILS = EnumSet.range(CHEST_VERTICAL_BASE, CHEST_BACK_WALL);

}
