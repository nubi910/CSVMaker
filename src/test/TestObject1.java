package test;

public class TestObject1 {

    private int field1;

    private String field2;

    private double field3;

    private long field4;

    private char field5;

    private boolean field6;

    public TestObject1(int field1, String field2, double field3, long field4, char field5, boolean field6) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
    }

    @Override
    public String toString() {
        return "TestObject1{" +
                "field1=" + field1 +
                ", field2='" + field2 + '\'' +
                ", field3=" + field3 +
                ", field4=" + field4 +
                ", field5=" + field5 +
                ", field6=" + field6 +
                '}';
    }
}
