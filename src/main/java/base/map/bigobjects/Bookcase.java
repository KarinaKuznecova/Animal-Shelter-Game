package base.map.bigobjects;

public class Bookcase extends BigObject {

    public Bookcase(int x, int y) {
        this(x, y, 1);
    }

    public Bookcase(int x, int y, int type) {
        super(x, y);
        switch (type) {
            case 0:
                addPart(2, 137, x, y, false);
                addPart(3, 136, x, y - 1, false);
                break;
            case 1:
                addPart(2, 139, x, y, false);
                addPart(3, 138, x, y - 1, false);
                break;
            case 2:
                addPart(2, 141, x, y, false);
                addPart(3, 140, x, y - 1, false);
                break;
            case 3:
                addPart(2, 143, x, y, false);
                addPart(3, 142, x, y - 1, false);
                break;
        }
    }
}
