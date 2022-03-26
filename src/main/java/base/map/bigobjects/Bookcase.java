package base.map.bigobjects;

public class Bookcase extends BigObject {

    public Bookcase(int x, int y, int type, int stepSize) {
        super(x, y);

        switch (type) {
            case 0:
                addPart(2, 137, x, y, false);
                addPart(3, 136, x, y - stepSize, false);
                break;
            case 1:
                addPart(2, 139, x, y, false);
                addPart(3, 138, x, y - stepSize, false);
                break;
            case 2:
                addPart(2, 141, x, y, false);
                addPart(3, 140, x, y - stepSize, false);
                break;
            case 3:
                addPart(2, 143, x, y, false);
                addPart(3, 142, x, y - stepSize, false);
                break;
        }
    }
}
