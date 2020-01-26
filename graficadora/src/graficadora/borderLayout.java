package graficadora;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public final class borderLayout {

    //creo los atributos de la clase necesarios para la GUI del programa
    private plano plano;

    private JFrame ventana = new JFrame("Wifi Analizer");

    private JPanel panel0 = new JPanel();

    private JPanel aux = new JPanel();

    private JTextArea ta = new JTextArea();
    private JButton grabar = new JButton("Grabar");
    private JButton detener = new JButton("Detener");
    private JPanel panel2 = new JPanel();

    private boolean grabando = false;
    SpinnerModel model = new SpinnerNumberModel(5, 5, 900, 1);
    JSpinner spinner = new JSpinner(model);
    private JLabel tiempo = new JLabel("intervalo de tiempo: ");
    private JLabel limite = new JLabel(" segundos.");

    public borderLayout() throws IOException {
        //creo la ventana (JFrame) con un tamaño de 600 por 700,  creo un objeto llamado plano(donde dibujamos la grafica en sus ejes x, y).
        ventana.setSize(850, 700);
        ventana.setLocationRelativeTo(null);
        plano = new plano();
        //caja.setText("");
        border();
        this.menu();

    }

    public void border() {

        aux.setLayout(new GridLayout(4, 1));
        ventana.setLayout(new BorderLayout());
        ventana.add(plano, BorderLayout.CENTER);

        panel0.setLayout(new GridLayout(1, 2));

        JScrollPane sp = new JScrollPane(ta);
        ta.setRows(4);
        ta.setEditable(false);
        panel0.add(sp);
        ventana.add(panel0, BorderLayout.SOUTH);

        panel2.add(this.grabar);
        panel2.add(this.detener);
        panel2.add(this.tiempo);
        panel2.add(this.spinner);
        panel2.add(this.limite);
        ventana.add(panel2, BorderLayout.NORTH);
        this.detener.setEnabled(false);

    }

    public void menu() {

        //aqui voy a crear el hilo
        hilo hilo = new hilo(plano, ta);
        hilo.start();

        String show = plano.getMostrar();

        this.ta.setText(show);
        hiloGrabar graba = new hiloGrabar();

        this.grabar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {

                JFileChooser fc = new JFileChooser();
                int op = fc.showSaveDialog(ventana);
                if (op == JFileChooser.APPROVE_OPTION) {
                    int a = (int) spinner.getValue();
                    System.out.println(a);
                    try {
                        graba.setDato(hilo, fc.getSelectedFile() + ".txt", a);
                        graba.start();
                    } catch (IOException ex) {
                        Logger.getLogger(borderLayout.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                grabar.setEnabled(false);
                detener.setEnabled(true);

            }
        });
        this.detener.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                graba.cerrarFichero();
                graba.stop();
                grabar.setEnabled(true);
                detener.setEnabled(false);
            }
        });

        JMenuBar menub = new JMenuBar();
        ventana.setJMenuBar(menub);
        JMenu archivo = new JMenu("ARCHIVO");
        archivo.setMnemonic('A');

        JMenuItem guardarplano = new JMenuItem("GUARDAR");
        guardarplano.setMnemonic('G');
        archivo.add(guardarplano);
        guardarplano.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                JFileChooser fc = new JFileChooser();
                int op = fc.showSaveDialog(ventana);
                if (op == JFileChooser.APPROVE_OPTION) {
                    FileWriter fichero = null;
                    PrintWriter pw = null;
                    try {
                        fichero = new FileWriter(fc.getSelectedFile() + ".txt");
                        pw = new PrintWriter(fichero);

                        ArrayList<String> channel1 = new ArrayList<>();
                        ArrayList<String> freq1 = new ArrayList<>();
                        ArrayList<String> signal1 = new ArrayList<>();
                        ArrayList<String> essid1 = new ArrayList<>();

                        StringBuilder nombre = new StringBuilder();
                        StringBuilder freq = new StringBuilder();
                        StringBuilder señal = new StringBuilder();
                        StringBuilder canal = new StringBuilder();

                        channel1 = hilo.getChannel();
                        freq1 = hilo.getFreq();
                        signal1 = hilo.getSignal();
                        essid1 = hilo.getEssid();

                        Calendar calendario = Calendar.getInstance();
                        int hora = calendario.get(Calendar.HOUR_OF_DAY);
                        int minutos = calendario.get(Calendar.MINUTE);
                        int segundos = calendario.get(Calendar.SECOND);
                        //hora + ":" + minutos + ":" + segundos

                        for (int i = 0; i < essid1.size(); i++) {
                            if (i == essid1.size() - 1) {
                                nombre.append(essid1.get(i) + "\n");
                                canal.append(channel1.get(i) + "\n");
                                freq.append(freq1.get(i) + "\n");
                                señal.append(signal1.get(i) + "\n");
                            } else {
                                nombre.append(essid1.get(i) + ",");
                                canal.append(channel1.get(i) + ",");
                                freq.append(freq1.get(i) + ",");
                                señal.append(signal1.get(i) + ",");
                            }
                        }
                        pw.print(hora + ":" + minutos + ":" + segundos + "\n");
                        pw.print(nombre.toString());
                        pw.print(canal.toString());
                        pw.print(freq.toString());
                        pw.print(señal.toString());

                        pw.close();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "ocurrio un error guardando el archivo");
                    }
                }
            }
        });

        JMenuItem guardar = new JMenuItem("GUARDAR PNG");
        guardar.setMnemonic('G');
        archivo.add(guardar);

        JMenuItem abrir = new JMenuItem("ABRIR");
        abrir.setMnemonic('A');
        archivo.add(abrir);
        abrir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                JFileChooser fc = new JFileChooser();

                int op = fc.showOpenDialog(ventana);
                String s = "";
                if (op == JFileChooser.APPROVE_OPTION) {
                    String sele = fc.getSelectedFile().toString();
                    if (".grp".equals(sele.substring(sele.length() - 4, sele.length())) || ".txt".equals(sele.substring(sele.length() - 4, sele.length()))) {
                        FileReader fr = null;
                        BufferedReader br = null;

                        ArrayList<String> channel = new ArrayList<>();
                        ArrayList<String> freq = new ArrayList<>();
                        ArrayList<String> signal = new ArrayList<>();
                        ArrayList<String> essid = new ArrayList<>();
                        ArrayList<String> horas = new ArrayList<>();
                        ArrayList<String> tamaño = new ArrayList<>();

                        StringBuilder texto = new StringBuilder();

                        try {
                            fr = new FileReader(fc.getSelectedFile());
                            br = new BufferedReader(fr);
                            try {
                                while ((s = br.readLine()) != null) {
                                    texto.append(s + "\n");
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "1.ocurrio un error abriendo el archivo");
                            }
                            //ArrayList<String> channel,ArrayList<String> freq, ArrayList<String> signal, ArrayList<String> essid
                            String data[] = texto.toString().split("\n");
                            for (int i = 0; i < data.length; i++) {
                                if (i * 5 + 4 < data.length) {
                                    horas.add(data[i * 5]);
                                                                        
                                    String datassid[] = data[i * 5 + 1].split(",");  
                                    tamaño.add(""+datassid.length);
                                    
                                    for (int j = 0; j < datassid.length; j++) {
                                        essid.add(datassid[j]);
                                    }
                                    String datachannel[] = data[i * 5 + 2].split(",");                                    
                                    for (int j = 0; j < datachannel.length; j++) {
                                        channel.add(datachannel[j]);
                                    }
                                    String datafreq[] = data[i * 5 + 3].split(",");                                    
                                    for (int j = 0; j < datafreq.length; j++) {
                                        freq.add(datafreq[j]);
                                    }
                                    String datasignal[] = data[i * 5 + 4].split(",");                                    
                                    for (int j = 0; j < datasignal.length; j++) {
                                        signal.add(datasignal[j]);
                                    }

                                }
                            }
                            System.out.println(tamaño);
                            abrir n = new abrir(tamaño, horas, channel, freq, signal, essid);

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "2.ocurrio un error abriendo el archivo");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "este tipo de archivo es invalido para el programa");
                    }

                }
            }
        });

        //guardar imagen
        guardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                BufferedImage imagen = new BufferedImage(plano.getWidth(), plano.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = imagen.createGraphics();
                plano.paint(g);
                File file = null;
                JFileChooser fc = new JFileChooser();
                int showSaveDialog = fc.showSaveDialog(null);
                if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
                    file = new File(fc.getSelectedFile() + ".png");
                    try {
                        ImageIO.write(imagen, "png", file);

                    } catch (IOException ex) {
                        Logger.getLogger(borderLayout.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        });

        JMenuItem salir = new JMenuItem("SALIR");
        salir.setMnemonic('S');
        archivo.add(salir);
        salir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                System.exit(0);
            }

        });
        menub.add(archivo);

        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);

    }

}
