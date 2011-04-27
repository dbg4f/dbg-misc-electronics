package j3d;

import javax.vecmath.Color3f;

public class RandomColor {
  public static final Color3f[] RANDOM_COLORS = new Color3f[] {
 	  new Color3f(0f,0f,0f)     ,
  	new Color3f(1f,0f,0f)     ,
  	new Color3f(0.7f,0f,0f)   ,
  	new Color3f(0f,1f,0f)     ,
  	new Color3f(0f,0.7f,0f)   ,
  	new Color3f(0f,0f,1f)     ,
  	new Color3f(0f,0f,0.7f)   ,
  	new Color3f(1f,1f,0f)     ,
  	new Color3f(0.7f,0.7f,0f) ,
  	new Color3f(0,1f,1f)      ,
  	new Color3f(0,0.7f,0.7f)  ,
  	new Color3f(1f,0,1f)      ,
  	new Color3f(0.7f,0,0.7f)  ,
  	new Color3f(0.8f,0.8f,0.8f)
  };
  public static int colorIndex = 0;

  public static Color3f newColor() {
    return RANDOM_COLORS[(colorIndex++)% RANDOM_COLORS.length];
  }
}
