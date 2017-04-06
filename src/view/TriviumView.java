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
    private static final long RECORD_TIME = 10;
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
            Mixer mixer = new Mixer() {
                @Override
                public Info getMixerInfo() {
                    return null;
                }

                @Override
                public Line.Info[] getSourceLineInfo() {
                    return new Line.Info[0];
                }

                @Override
                public Line.Info[] getTargetLineInfo() {
                    return new Line.Info[0];
                }

                @Override
                public Line.Info[] getSourceLineInfo(Line.Info info) {
                    return new Line.Info[0];
                }

                @Override
                public Line.Info[] getTargetLineInfo(Line.Info info) {
                    return new Line.Info[0];
                }

                @Override
                public boolean isLineSupported(Line.Info info) {
                    return false;
                }

                @Override
                public Line getLine(Line.Info info) throws LineUnavailableException {
                    return null;
                }

                @Override
                public int getMaxLines(Line.Info info) {
                    return 0;
                }

                @Override
                public Line[] getSourceLines() {
                    return new Line[0];
                }

                @Override
                public Line[] getTargetLines() {
                    return new Line[0];
                }

                @Override
                public void synchronize(Line[] lines, boolean b) {

                }

                @Override
                public void unsynchronize(Line[] lines) {

                }

                @Override
                public boolean isSynchronizationSupported(Line[] lines, boolean b) {
                    return false;
                }

                @Override
                public Line.Info getLineInfo() {
                    return null;
                }

                @Override
                public void open() throws LineUnavailableException {

                }

                @Override
                public void close() {

                }

                @Override
                public boolean isOpen() {
                    return false;
                }

                @Override
                public Control[] getControls() {
                    return new Control[0];
                }

                @Override
                public boolean isControlSupported(Control.Type type) {
                    return false;
                }

                @Override
                public Control getControl(Control.Type type) {
                    return null;
                }

                @Override
                public void addLineListener(LineListener lineListener) {

                }

                @Override
                public void removeLineListener(LineListener lineListener) {

                }
            }

            if (!AudioSystem.isLineSupported(info)){
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
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