package org.ei.util;

public class BitwiseOperators {


  public static boolean containsBit(int value, int mask) {
    return ((value & mask) == value);
  }

  public static boolean hasBitSet(int value, int mask) {
    return ((value & mask) > 0);
  }

}
    