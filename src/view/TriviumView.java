package view;

import controller.TriviumController;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class TriviumView extends javax.swing.JFrame{

    private TriviumController controller;
    private JTextField privatekey;
    private JTextField publickey;

    public TriviumView(TriviumController controller){
        this.controller = controller;
        initComponents();
       // super.setSize(null);
        //super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    private void initComponents(){
        java.awt.GridBagConstraints gridBagConstraints;
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(3840, 2160));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(1100, 900));
        getContentPane().setLayout(new java.awt.GridBagLayout());
        privatekey = new JTextField(20);
        privatekey.setText("Please enter a private key here.");
        publickey = new JTextField(20);
        publickey.setText("Please enter a public key here");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        super.add(privatekey, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        super.add(publickey, gridBagConstraints);

    }
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