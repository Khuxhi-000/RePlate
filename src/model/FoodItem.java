package model;

/**
 * Represents the food entity itself with name, category, and perishability.
 * Demonstrates basic encapsulation.
 */
public class FoodItem {
    private String name;
    private FoodCategory category;
    private boolean isPerishable;

    public FoodItem() {
    }

    public FoodItem(String name, FoodCategory category, boolean isPerishable) {
        this.name = name;
        this.category = category;
        this.isPerishable = isPerishable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public boolean isPerishable() {
        return isPerishable;
    }

    public void setPerishable(boolean perishable) {
        isPerishable = perishable;
    }

    @Override
    public String toString() {
        return name + " [" + category + "]";
    }
}
