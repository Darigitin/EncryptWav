package model;/*
 * Copyright (C) 2015 Matt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Matt
 */
class Trivium {
    private final boolean[] A = new boolean[93];
    private final boolean[] B = new boolean[84];
    private final boolean[] C = new boolean[111];
    
    public Trivium(boolean[] k, boolean[] v){
        initialize(k,v);
        //System.out.println(status());
        warmUp(1153);
    }
    
    public Trivium(boolean[] k, boolean[] v, int n){
        initialize(k,v);
        warmUp(n);
    }
    
    public Trivium(){
        boolean[] k = new boolean[80];
        boolean[] v = new boolean[80];
        for (int i = 0; i < 80; i++){
            k[i] = true;
            v[i] = false;
        }
        initialize(k,v);
        warmUp(1153);
    }
    
    private void initialize(boolean[] k, boolean[] v){
        
        for (int i = 0; i < 93; i++){
            if (i < 92 && i > 13){
                A[i] = k[i - 13];
            }
            if (i < 83 && i > 4) {
                B[i] = v[i - 4];
            }
            if (i < 2){
                C[i] = true;
            }
        }
    }
    
    private void warmUp(int n){
       for (int i = 0; i < n; i++){
           //System.out.println(status());
           run();
           //System.out.println(status());
       } 
    }
    private int run(){
        boolean outPutA = (A[90] & A[91]) ^ A[92] ^ A[65];
        boolean outPutB = (B[81] & B[82]) ^ B[83] ^ B[68];
        boolean outPutC = (C[109] & C[108]) ^ C[110] ^ C[65];
        boolean fbBitA = A[68], fbBitB = B[77], fbBitC = C[86];
        boolean inPutA = fbBitA ^ outPutC;
        boolean inPutB = fbBitB ^ outPutA;
        boolean inPutC = fbBitC ^ outPutB;
        //System.out.println(inPutA + " " + inPutB + " " + inPutC);
        
        for (int i = 109; i >= 0; i--){
            if (i < 91){
                A[i+1] = A[i];
            }
            if (i < 82){
                B[i+1] = B[i];
            }
            C[i+1] = C[i];
        }
        A[0] = inPutA;
        B[0] = inPutB;
        C[0] = inPutC;
        
        
        if (outPutA ^ outPutB ^ outPutC){
            return 1;
        }
        else
            return 0;
    }
    public final String status (){
        String s1, s2, s3;
        int a = A.length, b = B.length, c = C.length;
        s1 = "A: [ ";
        s2 = "B: [ ";
        s3 = "C: [ ";
        for (int i = 0; i < 110; i++){
            if (i < a){
                if (A[i]){
                    s1 = s1 + "1, ";
                }
                else
                    s1 = s1 + "0, ";
            }
            if (i < b){
                if (B[i]){
                    s2 = s2 + "1, ";
                }
                else
                    s2 = s2 + "0, ";
            }
            if (i < c){
                if (C[i]){
                    s3 = s3 + "1, ";
                }
                else
                    s3 = s3 + "0, ";
            }
        }
        s1 = s1 + "]";
        s2 = s2 + "]";
        s3 = s3 + "]";
        return s1  + "\n" + s2 + "\n" + s3;
    }
    int getBit(){
        return run();
    }
}
