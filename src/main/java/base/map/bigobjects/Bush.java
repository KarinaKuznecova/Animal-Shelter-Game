package base.map.bigobjects;

public class Bush extends BigObject {

    public Bush(int x, int y) {
        super(x, y);
        addPart(3, 62, x - 1, y - 1, false);
        addPart(3, 63, x, y - 1, false);
        addPart(3, 64, x + 1, y - 1, false);

        addPart(2, 65, x - 1, y, false);
        addPart(2, 66, x, y, false);
        addPart(2, 67, x + 1, y, false);

        addPart(2, 68, x - 1, y + 1, false);
        addPart(2, 69, x, y + 1, false);
        addPart(2, 70, x + 1, y + 1, false);
    }
}
