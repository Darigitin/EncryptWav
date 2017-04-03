package controller;

import view.TriviumView;
import model.TriviumModel;

public class TriviumController {
    private TriviumView view = new TriviumView(this);
    private static String key, IV;
    private String filePath = view.getFilePath();


    public TriviumController(){
        run();
    }

    private void run()
    {
        getKeys();
        boolean[] keyArray = sizeKeys(key);
        boolean[] IVArray = sizeKeys(IV);

        String fileExtension = ".wav";
        String filePathToReadFrom = filePath + fileExtension;
        TriviumModel model = new TriviumModel(filePathToReadFrom, null, null, keyArray, IVArray);

        model.filePath = model.filePath
                + "-ReadThenWritten"
                + fileExtension;

        model.writeToFilePath();
    }
    private void getKeys(){

        key = view.getPrivateKey();
        IV = view.getPublicKey();
    }

    private static boolean[] sizeKeys(String key){

        int flag;
        if (key.length() < 10){
            flag = 1;
        }
        else if (key.length() > 10){
            flag = 2;
        }
        else
            flag = 3;

        switch (flag){
            case 1:
                key = padKey(key);
                break;
            case 2:
                key = truncKey(key);
                break;
            default:
                break;
        }

        return StringToBooleanArray(key);

    }

    private static String padKey(String key){
        while (key.length() < 10){
            key = key + "a";
        }
        return key;
    }

    private static String truncKey(String key){
        return key.substring(0, 10);
    }

    private static boolean[] StringToBooleanArray(String key){

        boolean[] booleanArray = new boolean[80];

        for (int i = 0; i < key.length(); i++){
            char c = key.charAt(i);
            int ascii = (int) c;
            for (int j = 0; j < 8; j++){
                int bit = 128/(2^7);
                if (bit < ascii){
                    booleanArray[i*8 + j] = true;
                    ascii = ascii - bit;
                }
                else
                    booleanArray[i*j] = false;

            }
        }
        return booleanArray;
    }
}