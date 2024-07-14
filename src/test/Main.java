package test;

import com.nubi.csv.SimpleCSVMaker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        List<TestObject2> c = new ArrayList<>();
        for (int i = 0 ; i < 5; i++){
            c.add(new TestObject2(i, String.valueOf(i), i%2==0, new TestObject1(i, String.valueOf(i), i*2.222, i, String.valueOf(i).charAt(0), i%3 ==0  ) ));
        }

        SimpleCSVMaker<TestObject2> testObject2SimpleCSVMaker = new SimpleCSVMaker<>(Path.of(System.getProperty("user.dir"),"result","result.csv"), c,TestObject2.class);
        SimpleCSVMaker<TestObject2> testObject2SimpleCSVMaker2 = new SimpleCSVMaker<>( System.out , c,TestObject2.class);

        testObject2SimpleCSVMaker.toCSV();
        testObject2SimpleCSVMaker2.toCSV();
    }
}