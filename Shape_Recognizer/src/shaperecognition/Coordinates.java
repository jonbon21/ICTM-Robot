package shaperecognition;

final class Coordinates {
    int Coord_X;
    int Coord_Y;

    public Coordinates(int first, int second) {
        this.Coord_X = first;
        this.Coord_Y = second;
    }

    public int getFirst() {
        return Coord_X;
    }

    public int getSecond() {
        return Coord_Y;
    }
}
