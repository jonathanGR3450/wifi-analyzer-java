package graficadora;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class abrir {

    //creo los atributos de la clase necesarios para la GUI del programa
    private planoAbrir plano;

    private Color colr = Color.BLACK;
    private double valoresy[];
    private double valoresx[];

    private boolean nuevog = false;

    private JFrame ventana = new JFrame("Wifi analyzer");

    private JPanel panel2 = new JPanel();
    private JPanel aux = new JPanel();
    private javax.swing.JComboBox<String> jComboBox1;

    private ArrayList<Double> ecuacionex = new ArrayList<Double>();
    private ArrayList<Double> ecuacioney = new ArrayList<Double>();

    public abrir(ArrayList<String> tamaño, ArrayList<String> horas, ArrayList<String> channel, ArrayList<String> freq, ArrayList<String> signal, ArrayList<String> essid) throws IOException {
        //creo la ventana (JFrame) con un tamaño de 600 por 700,  creo un objeto llamado plano(donde dibujamos la grafica en sus ejes x, y).
        ventana.setSize(850, 700);
        ventana.setLocationRelativeTo(null);
        plano = new planoAbrir( horas, channel, freq, signal, essid);
        //caja.setText("");
        border();
        this.menu();

    }

    public void border() {
        //JPanel llamado aux el cual tiene un GridLayout de una fila y dos columnas;
        //en este JPanel se mostraran los puntos de -6 hasta 6 (los valores de Y respecto a X).
        panel2.add(this.jComboBox1);
        
        aux.setLayout(new GridLayout(4, 1));
        ventana.add(panel2, BorderLayout.NORTH);
        ventana.setLayout(new BorderLayout());
        ventana.add(plano, BorderLayout.CENTER);

    }

    public void menu() {

        ventana.setVisible(true);
        ventana.setResizable(false);

    }

}
