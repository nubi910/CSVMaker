package com.nubi.csv;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * A Csv file maker.
 * Able to use with {@link OutputStream} or {@link Path} with file.
 *
 *
 * @param <T> The class that wants to make csv file.
 */
public class SimpleCSVMaker<T> {

    private Path file;
    private OutputStream outputStream;
    final private Collection<T> objectCollection;
    final private Class<T> clazz;
    final private Collector<CharSequence, ?, String> csvShapeCollector = Collectors.joining(",", "", System.lineSeparator());
    private boolean replaceOld = true;


    /**
     * A constructor that using {@link OutputStream}. The instance that made with this constructor is make CSV data out to given {@link OutputStream}
     *
     *
     * @param outputStream
     * @param objectCollection Collection for make csv body lines.
     * @param clazz Class for make header.
     */
    public SimpleCSVMaker(OutputStream outputStream, Collection<T> objectCollection, Class<T> clazz) {
        if (outputStream == null){
            throw new IllegalArgumentException("outputStream can not be null");
        }
        if (objectCollection == null){
            throw new IllegalArgumentException("objectCollection can not be null");
        }
        if (clazz == null){
            throw new IllegalArgumentException("clazz can not be null");
        }
        this.outputStream = outputStream;
        this.objectCollection = objectCollection;
        this.clazz = clazz;
    }

    /**
     *  A constructor that using {@link Path}. The instance that made with this constructor is make CSV data file to given {@link Path}.
     * If the file is already exists, this will move existing file to '.old' file.
     *
     *
     * @param file
     * @param objectCollection
     * @param clazz
     */
    public SimpleCSVMaker(Path file, Collection<T> objectCollection, Class<T> clazz) {
        if (file == null){
            throw new IllegalArgumentException("file can not be null");
        }
        if (objectCollection == null){
            throw new IllegalArgumentException("objectCollection can not be null");
        }
        if (clazz == null){
            throw new IllegalArgumentException("clazz can not be null");
        }
        this.file = file;
        this.objectCollection = objectCollection;
        this.clazz = clazz;
    }

    public boolean toCSV() throws IOException, IllegalAccessException {
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
        if ( replaceOld && Files.exists(file)){
           Files.move(file, Path.of(String.valueOf(file.toAbsolutePath()).concat( "_"+DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())+ ".old")));
        }
        Files.writeString(file, h, StandardOpenOption.CREATE);

        for (T t : objectCollection){
            Files.writeString(file, makeCSVLine(t, clazz).stream().collect(csvShapeCollector), StandardOpenOption.APPEND);
        }

        return file;
    }

    protected boolean csvStreamOut() throws IOException, IllegalAccessException{
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
        if ( !isSpreadable(c) ){
            return Collections.emptyList();
        }
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
        if (!isSpreadable(c)){
            return Collections.emptyList();
        }
        List<String> r = new ArrayList<>();
        for (Field f : c.getDeclaredFields()){
            if (obj == null){
                r.add("null");
                continue;
            }
            System.out.println(f.getType());
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


    private boolean isSpreadable(Class<?> c){
        if (c.isPrimitive()){
            return false;
        }
        if (c.equals(String.class)){
            return false;
        }
        if (c.getDeclaredFields().length == 0){
            return false;
        }

        return true;
    }

    public void setReplaceOldFile(boolean replaceOld){
        this.replaceOld = replaceOld;
    }

    public boolean getReplaceOldFile(){
        return this.replaceOld;
    }



}
