package info.geostage.inventoryadmin.data;

/*
 * Created by Dimitar on 19.6.2017 г..
 */

public class ItemProvider {

    private final String itemName;
    private final int itemPrice;
    private final int itemQuantity;
    private final String supplierName;
    private final String supplierPhone;
    private final String supplierEmail;
    private final String picture;

    public ItemProvider(String itemName, int itemPrice, int itemQuantity, String supplierName, String supplierPhone, String supplierEmail, String picture) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
        this.supplierEmail = supplierEmail;
        this.picture = picture;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public String getPicture() {
        return picture;
    }
    @Override
    public String toString() {
        return "ItemProvider{" +
                "itemName='" + itemName + '\'' +
                ", itemPrice='" + itemPrice + '\'' +
                ", itemQuantity=" + itemQuantity +
                ", supplierName='" + supplierName + '\'' +
                ", supplierPhone='" + supplierPhone + '\'' +
                ", supplierEmail='" + supplierEmail + '\'' +
                '}';
    }

}
