package com.israel.madcat.camera360fullsphere;

public class Point {

    private int coordX;
    private int coordY;

    public Point(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public Point() {

    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    @Override
    public String toString() {
        return "Point{" +
                "coordX=" + coordX +
                ", coordY=" + coordY +
                '}';
    }
}
