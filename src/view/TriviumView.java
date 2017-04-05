package view;

import controller.TriviumController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

public class TriviumView extends JFrame{

    private TriviumController controller;
    private ButtonHandler handler;
    JButton openButton;
    JFileChooser chooser;
    File file;
    JButton submit;
    private String publicKey;
    private String privateKey;
    private JTextField privateKeyField;
    private JTextField publicKeyField;


    public TriviumView(TriviumController controller){
        super("Trivium Window");
        this.controller = controller;
        initComponents();
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setSize(1200,550);
        //super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    private void initComponents(){
       setLayout(new FlowLayout());
       handler = new ButtonHandler();
       JPanel keyPanel = new JPanel();
       JLabel keyLabel = new JLabel("Keys:");
       keyPanel.add(keyLabel);
       keyPanel.setLayout(new FlowLayout());
       keyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
       privateKeyField = new JTextField("", 15);
       privateKeyField.setText("Private Key");
       privateKeyField.setForeground(Color.black);
       keyPanel.add(privateKeyField);
       publicKeyField = new JTextField("", 15);
       publicKeyField.setText("Public Key");
       publicKeyField.setForeground(Color.black);
       keyPanel.add(publicKeyField);
       chooser = new JFileChooser();
       chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
       openButton = new JButton("Select a audio file");
       openButton.addActionListener(handler);
       keyPanel.add(openButton);
       submit = new JButton("Submit");
       keyPanel.add(submit);
       submit.addActionListener(handler);
       add(keyPanel);


    }
    public String getFilePath(){
        String absPath = file.getAbsolutePath();
        String filePath = absPath.substring(0, absPath.length() - 4);
        System.out.println(filePath);
        return filePath;
    }

    public String getPrivateKey(){
        return privateKey;
    }

    public String getPublicKey(){
        return publicKey;
    }

    private class ButtonHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent event){
            Object source = event.getSource();
            if (source == openButton){
                int returnVal = chooser.showOpenDialog(TriviumView.this);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    file = chooser.getSelectedFile();
                }
            }
            if (source == submit){
                privateKey = privateKeyField.getText();
                publicKey = publicKeyField.getText();
                controller.run();
            }
        }
    }
}