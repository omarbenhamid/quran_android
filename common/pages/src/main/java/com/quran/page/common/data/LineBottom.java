package com.quran.page.common.data;

public class LineBottom {
  private int line;
  private float left, right, bottom;

  public LineBottom(int line, float left, float right, float bottom) {
    this.line = line;
    this.left = left;
    this.right = right;
    this.bottom = bottom;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public float getLeft() {
    return left;
  }

  public void setLeft(float left) {
    this.left = left;
  }

  public float getRight() {
    return right;
  }

  public void setRight(float right) {
    this.right = right;
  }

  public float getBottom() {
    return bottom;
  }

  public void setBottom(float bottom) {
    this.bottom = bottom;
  }
}
