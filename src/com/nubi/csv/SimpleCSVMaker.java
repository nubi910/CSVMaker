package com.nubi.csv;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SimpleCSVMaker<T> {

    private Path file;
    private OutputStream outputStream;
    final private Collection<T> objectCollection;
    final private Class<T> clazz;
    final private Collector<CharSequence, ?, String> csvShapeCollector = Collectors.joining(",", "", System.lineSeparator());

    public SimpleCSVMaker(OutputStream outputStream, Collection<T> objectCollection, Class<T> clazz) {
        this.outputStream = outputStream;
        this.objectCollection = objectCollection;
        this.clazz = clazz;
    }

    public SimpleCSVMaker(Path file, Collection<T> objectCollection, Class<T> clazz) {
        this.file = file;
        this.objectCollection = objectCollection;
        this.clazz = clazz;
    }

    public boolean toCSV() throws Exception{
        if (file != null){
            return csvFileOut() == file ;
        }
        if (outputStream != null){
            return csvStreamOut();
        }
        return false;
    }

    protected Path csvFileOut() throws IOException, IllegalAccessException {
        String h = makeCSVHeader(clazz).stream().collect(csvShapeCollector);
        Files.createDirectories(file.getParent());
        if (Files.exists(file)){
           Files.move(file, Path.of(String.valueOf(file.toAbsolutePath()).concat( "_"+DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())+ ".old")));
        }
        Files.writeString(file, h, StandardOpenOption.CREATE);

        for (T t : objectCollection){
            Files.writeString(file, makeCSVLine(t, clazz).stream().collect(csvShapeCollector), StandardOpenOption.APPEND);
        }

        return file;
    }

    protected boolean csvStreamOut() throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)){
            writer.write(makeCSVHeader(clazz)
                    .stream()
                    .collect(csvShapeCollector));

            for (T t : objectCollection){
               writer.write( makeCSVLine(t,clazz).stream().collect(csvShapeCollector) );
            }
        }

        return true;
    }


    protected List<String> makeCSVHeader(Class<?> c) {
        List<String> r = new ArrayList<>();
        for(Field f : c.getDeclaredFields()){
            f.setAccessible(true);
            if (f.isAnnotationPresent(CSVIgnore.class)){
                continue;
            }
            if (f.isAnnotationPresent(SpreadOut.class)){
                r.addAll(makeCSVHeader(f.getType()));
            }else {
                r.add(f.getName());
            }
        }

        return r;
    }

    private List<String> makeCSVLine(Object obj, Class<?> c) throws IllegalAccessException {
        List<String> r = new ArrayList<>();
        for (Field f : c.getDeclaredFields()){
            if (obj == null){
                r.add("null");
                continue;
            }
            f.setAccessible(true);
            if (f.isAnnotationPresent(CSVIgnore.class)){
                continue;
            }
            if (f.isAnnotationPresent(SpreadOut.class)){
                r.addAll( makeCSVLine(f.get(obj), f.getType()));
            } else {
                Object temp = f.get(obj);
                r.add(temp == null ? "null" : temp.toString());
            }
        }
        return r;
    }



}
