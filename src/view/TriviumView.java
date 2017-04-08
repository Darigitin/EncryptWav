package view;

import controller.TriviumController;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class TriviumView extends JFrame{

    private TriviumController controller;
    private JButton openButton;
    private JFileChooser chooser;
    private File file, wavFile;
    private JButton submit, record, stop;
    private String publicKey;
    private String privateKey;
    private JTextField privateKeyField;
    private JTextField publicKeyField;
//    private static final long RECORD_TIME = 10000;
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private TargetDataLine line;
    private boolean running;
    private ByteArrayOutputStream out;


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
       record = new JButton("Record");
       record.addActionListener(handler);
       keyPanel.add(record);
       stop = new JButton("Stop");
       stop.addActionListener(handler);
       keyPanel.add(stop);
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


    private void captureAudio() {
        try {
            final AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            Mixer mixer = null;
            System.out.println(format.getFrameSize());

            for(Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo()){
                //System.out.println(thisMixerInfo.getName());
                if(thisMixerInfo.getName().equals("Primary Sound Capture Driver")) {
                    mixer = AudioSystem.getMixer(thisMixerInfo);
                }
            }
            assert mixer != null;
            if (!mixer.isLineSupported(info)){
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) mixer.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable() {
                int bufferSize = (int)format.getSampleRate()
                        * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];


                public void run() {
                    out = new ByteArrayOutputStream();
                    running = true;
                    try {
                        while (running) {
                            int count = line.read(buffer, 0, buffer.length);
                            if (count > 0) {
                                out.write(buffer, 0, count);
                            }
                        }
                        line.close();
                        out.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-1);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-2);
        }
    }
    private void writeAudio() {
        byte audio[] = out.toByteArray();
        InputStream input =
                new ByteArrayInputStream(audio);
        final AudioFormat format = getAudioFormat();
        final AudioInputStream ais =
                new AudioInputStream(input, format,
                        audio.length / format.getFrameSize());

        Runnable runner = new Runnable() {
            int bufferSize = (int) format.getSampleRate()
                    * format.getFrameSize();
            byte buffer[] = new byte[bufferSize];

            public void run() {
                try {
                    int count;
                    while ((count = ais.read(
                            buffer, 0, buffer.length)) != -1) {
                        if (count > 0) {
                            AudioSystem.write(ais, fileType, wavFile);
                        }
                    }
                    ais.close();
                } catch (IOException e) {
                    System.err.println("I/O problems: " + e);
                    System.exit(-3);
                }
            }
        };
        Thread playThread = new Thread(runner);
        playThread.start();
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
            else if (source == record) {
                wavFile = new File("RecordAudio.wav");
                record.setEnabled(false);
                stop.setEnabled(true);
                captureAudio();
            }
            else if (source == stop){
                record.setEnabled(true);
                stop.setEnabled(false);
                running = false;
                writeAudio();

            }

        }
    }
}