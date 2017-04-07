package view;

import controller.TriviumController;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class TriviumView extends JFrame{

    private TriviumController controller;
    private JButton openButton;
    private JFileChooser chooser;
    private File file, wavFile;
    private JButton submit;
    private String publicKey;
    private String privateKey;
    private JTextField privateKeyField;
    private JTextField publicKeyField;
    private JButton record;
    private static final long RECORD_TIME = 10000;
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private TargetDataLine line;


    private AudioFormat getAudioFormat(){
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, true, true);
    }


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
       ButtonHandler handler = new ButtonHandler();
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
       record = new JButton("Record an audio file");
       record.addActionListener(handler);
       keyPanel.add(record);
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

    private void startRecord(){
        try{
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            Mixer mixer = null;

            for(Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo()){
                //System.out.println(thisMixerInfo.getName());
                if(thisMixerInfo.getName().equals("Primary Sound Capture Driver")) {
                    mixer = AudioSystem.getMixer(thisMixerInfo);
                }
            }
            if (mixer.equals(null)){
                System.out.println("Mixer not found");
                System.exit(0);
            }

            if (!mixer.isLineSupported(info)){
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) mixer.getLine(info);
            System.out.println(line.toString());
            line.open(format);
            line.start();
            System.out.println("Start capturing...");
            AudioInputStream ais = new AudioInputStream(line);
            System.out.println("Start recording...");
            AudioSystem.write(ais, fileType, wavFile);
        }
        catch(LineUnavailableException | IOException ex){
            ex.printStackTrace();
        }
    }

    private void finish(){
        line.stop();
        line.close();
        System.out.println("Finished");
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
            else if (source == submit){
                privateKey = privateKeyField.getText();
                publicKey = publicKeyField.getText();
                controller.run();
            }
            else if (source == record){
                wavFile = new File("RecordAudio.wav");
                Thread stopper = new Thread(() -> {
                    try {
                        Thread.sleep(RECORD_TIME);
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                    finish();
                });
                stopper.start();
                startRecord();
            }
        }
    }
}