package com.quran.page.common.data;

import java.util.List;

public class FingerMotionRange {
  boolean deleting;

  int firstLine;
  int lastLine;

  float firstX;
  float lastX;

  private static LineBottom findLine(List<LineBottom> lineBottoms, float y) {
    LineBottom last = null;
    for(LineBottom l : lineBottoms) {
      if(l == null) continue;
      if(l.getBottom() >= y) return l;
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
   * @param endY
   */
  public void set(List<LineBottom> lineBottoms, float startX, float startY, float endX, float endY) {
    LineBottom startLine = findLine(lineBottoms,startY);
    LineBottom endLine = findLine(lineBottoms,endY) ;

    // Deleting if endline above start line or end is at the right of start on the same line
    deleting = (endLine.getLine() < startLine.getLine()) ||
        (endLine.getLine() == startLine.getLine() && startX < endX);

    if(deleting) {
      firstLine = endLine.getLine();
      lastLine = startLine.getLine();
      firstX = endX;
      lastX = startX;
    } else {
      firstLine = startLine.getLine();
      lastLine = endLine.getLine();
      firstX = startX;
      lastX = endX;
    }

  }

  public static FingerMotionRange fromMotionCoords(List<LineBottom> lineBottoms,
                                                   float startX, float startY,
                                                   float endX, float endY) {
    FingerMotionRange ret = new FingerMotionRange();
    ret.set(lineBottoms, startX, startY, endX, endY);
    return ret;
  }

  public boolean isDeleting() {
    return deleting;
  }

  public int getFirstLine() {
    return firstLine;
  }

  public int getLastLine() {
    return lastLine;
  }

  public float getFirstX() {
    return firstX;
  }

  public float getLastX() {
    return lastX;
  }
}
