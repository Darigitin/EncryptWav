package view;

import java.util.Scanner;

public class TriviumView {

    private Scanner keyboard = new Scanner(System.in);
    public String getFilePath(){
        System.out.println("Please enter path to audio file: ");
        return keyboard.nextLine();
    }

    public String getPrivateKey(){
        System.out.println("Please enter a private key: ");
        return keyboard.nextLine();
    }

    public String getPublicKey(){
        System.out.println("Please enter a public key: ");
        return keyboard.nextLine();
    }
}