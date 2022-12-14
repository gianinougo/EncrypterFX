/*
    @author Ugo Gianino
 */

package com.example.encrypterfx.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/*
 Class to define the encryption.
 @version 1.0
 */

public class Encrypter {

    int key;

    /*
    Performs the encryption
    @param key the encryption key
     */
    public Encrypter(int key) {
        this.key = key;
    }

    /*
    Moves the encryption key
     */
    private String moveChar(String line, int key){
        String result = "";

        for (int i = 0; i < line.length(); i++) {
            result += (char)(line.charAt(i) + key);

        }
        return result;
    }

    /*
    Path the encryption key
     */
    public void encryption (Path pathName) throws IOException {

        try(Stream<String> stream= Files.lines(pathName);
            Stream<String> stream2= Files.lines(pathName);
            PrintWriter writer = new PrintWriter(pathName.getParent() + "/encrypted/"+ pathName.getFileName()))
        {
            long signature = stream.mapToLong(line ->{
                return countLetters(line);
            }).sum();
            stream2.map(line-> moveChar(line, key)).forEach(writer::println);
            writer.println("Signature:"+ signature);

        }
    }

    /*
    CountLetters the encryption key
     */
    private long countLetters(String line) {

        long count = 0;
        int index = -1;

        for (char c = 'a'; c <= 'e'; c++){
            while ((index = line.indexOf(c, index + 1)) != -1)
                count++;
            index = -1;
        }

        return count;
    }
    /*
    Callable method encryption
     */
    public Callable<Boolean> checkSignature(Path pathName){
        return () -> {
            System.out.println(pathName.toString());
            try (Stream<String> stream= Files.lines(pathName);
                 Stream<String> stream2= Files.lines(pathName);) {
                long sig1 = stream.filter(line-> !(line.startsWith("Signature:"))).
                        map(line-> moveChar(line, -key)).mapToLong(line-> {
                            return countLetters(line);
                        }).sum();
                long sig2 = stream2.filter(line-> line.startsWith("Signature:"))
                        .mapToInt(line->Integer.parseInt(line.substring(10))).sum();
                return sig1==sig2;
            } catch (IOException e){
                e.printStackTrace();
            }
            return false;
        };
    }
}
