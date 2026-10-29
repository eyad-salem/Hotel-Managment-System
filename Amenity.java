public class Amenity {
    private String name;

    public Amenity(String name) {
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Amenity name can't be empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        if(name == null || name.trim().isEmpty()){
            throw new InvalidInputException("Amenity name can't be empty");
        }
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override

    public boolean equals (Object o){
        if(this==o) {
            return true;
        }
        else if(!(o instanceof Amenity)){
            return false;
        }
        Amenity amenity = (Amenity) o;
        return name.equalsIgnoreCase(amenity.name);
    }
}