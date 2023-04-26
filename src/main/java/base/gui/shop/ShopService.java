package base.gui.shop;

import java.util.List;

public class ShopService {

    private List<ShopItem> shopItemList;

    public int getItemPrice(String itemName) {
        for (ShopItem price : shopItemList) {
            if (price.getItemName().equals(itemName)) {
                return price.getSellPrice();
            }
        }
        return 0;
    }

    public List<ShopItem> getShopItemList() {
        return shopItemList;
    }

    public void setShopItemList(List<ShopItem> shopItemList) {
        this.shopItemList = shopItemList;
    }
}
