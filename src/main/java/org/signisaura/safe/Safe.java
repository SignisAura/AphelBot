package org.signisaura.safe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Safe {
    private static final Long idOwner = 182572875360894976L;

    public static String getToken() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/main/java/org/signisaura/safe/token.txt"));
        return scanner.nextLine();
    }

    public static Long getIdOwner() {
        return idOwner;
    }
}
