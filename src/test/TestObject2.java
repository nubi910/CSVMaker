package test;

import com.nubi.csv.SpreadOut;

public class TestObject2 {
    private int fiEld1;

    private String field2;

    private boolean field3;

    @SpreadOut
    private TestObject1 testObject1;

    public TestObject2(int fiEld1, String field2, boolean field3, TestObject1 testObject1) {
        this.fiEld1 = fiEld1;
        this.field2 = field2;
        this.field3 = field3;
        this.testObject1 = testObject1;
    }

    @Override
    public String toString() {
        return "TestObject2{" +
                "fiEld1=" + fiEld1 +
                ", field2='" + field2 + '\'' +
                ", field3=" + field3 +
                ", testObject1=" + testObject1 +
                '}';
    }
}
