package com.quran.page.common.data;

import java.util.List;

public class FingerMotionRange {
  boolean deleting;

  int line;

  float firstX;
  float lastX;

  private static LineBottom findLine(List<LineBottom> lineBottoms, float y) {
    LineBottom last = null;
    for(LineBottom l : lineBottoms) {
      if(l == null) continue;
      if(last != null) {
      if (y <= (last.getBottom() + l.getBottom()) / 2)
          return last;
      }
      last = l;
    }
    return last;
  }

  /**
   * Compute the first / last line from the list of given lien bottoms of a swipe.
   *
   * @param lineBottoms
   * @param startX
   * @param startY
   * @param endX
   */
  public void set(List<LineBottom> lineBottoms, float startX, float startY, float endX) {
    LineBottom lineBottom = findLine(lineBottoms,startY);

    // Deleting if endline above start line or end is at the right of start on the same line
    deleting = startX < endX;
    line = lineBottom.getLine();

    if(deleting) {
      firstX = startX;
      lastX = endX;
    } else {
      firstX = endX;
      lastX = startX;
    }

    if(firstX < lineBottom.getLeft()) firstX = lineBottom.getLeft();
    if(lastX > lineBottom.getRight()) lastX = lineBottom.getRight();
  }

  public static FingerMotionRange fromMotionCoords(List<LineBottom> lineBottoms,
                                                   float startX, float startY,
                                                   float endX) {
    FingerMotionRange ret = new FingerMotionRange();
    ret.set(lineBottoms, startX, startY, endX);
    return ret;
  }

  public boolean isDeleting() {
    return deleting;
  }

  public int getLine() {
    return line;
  }

  public float getFirstX() {
    return firstX;
  }

  public float getLastX() {
    return lastX;
  }
}