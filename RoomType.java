public class RoomType {
    private String name;
    private double pricePerNight;
    private int capacity;

    public RoomType(String name, double pricePerNight,int capacity) {
        this.name = name.trim();
        this.pricePerNight = Math.max(0,pricePerNight);
        this.capacity = Math.max(1,capacity);
    }

    public String getName() { return name;}
    public double getPricePerNight() { return pricePerNight;}
    public int getCapacity() { return capacity;}

    public void setName(String name) {
        if(name==null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Name can't be empty");
        }
        this.name = name;
    }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = Math.max(0,pricePerNight);}
    public void setCapacity(int capacity) { this.capacity = Math.max(1,capacity);}



    @Override
    public String toString() {
        return name + "Room, $"  + pricePerNight + " per night";
    }




}