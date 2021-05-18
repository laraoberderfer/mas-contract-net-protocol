package demo;

public class Interest {
    private String value;
    private int rank;

    public Interest(String value, int rank){
        this.value = value;
        this.rank = rank;
    }

    public String getValue(){
        return value;
    }

    public int getRank(){
        return rank;
    }
}