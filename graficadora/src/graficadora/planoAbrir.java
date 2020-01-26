package graficadora;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JPanel;
import org.nfunk.jep.JEP;

public class planoAbrir extends JPanel {

    private String interfaz;
    ArrayList<ArrayList<Float>> aListX = new ArrayList<ArrayList<Float>>();
    ArrayList<ArrayList<Float>> aListY = new ArrayList<ArrayList<Float>>();
    ArrayList<String> mostrar = new ArrayList<>();

    ArrayList<String> channel = new ArrayList<>();
    ArrayList<String> freq = new ArrayList<>();
    ArrayList<String> signal = new ArrayList<>();
    ArrayList<String> essid = new ArrayList<>();

    public planoAbrir(ArrayList<String> horas, ArrayList<String> channel,ArrayList<String> freq, ArrayList<String> signal, ArrayList<String> essid) throws IOException {
        this.channel=channel;
        this.freq=freq;
        this.signal=signal;
        this.essid=essid;
        
        System.out.println(horas);
        System.out.println(channel);
        System.out.println(freq);
        System.out.println(signal);
        System.out.println(essid);
        
        this.datos();
        this.setLayout(new BorderLayout());

    }

    public void datos() throws IOException {

        //obtenemos los valores de las graficas
        JEP jep = new JEP();
        jep.addStandardConstants();
        jep.addStandardFunctions();

        for (int j = 0; j < essid.size(); j++) {

            String mostrar1 = "SSID: " + essid.get(j) + ", canal: " + channel.get(j) + ", señal: " + signal.get(j) + ", frecuencia: " + freq.get(j) + ".";
            mostrar.add(mostrar1);
            
            ArrayList<Float> datosX = new ArrayList<Float>();
            ArrayList<Float> datosY = new ArrayList<Float>();

            //pasar datos obtenidos A datos que se puedan graficar
            int f = Integer.parseInt(signal.get(j).replace(" ", ""));
            int a = (int) Math.floor(f / 10) * 10;
            String ds = "" + f;
            ds = ds.charAt(ds.length() - 1) + "";
            System.out.println(mostrar1);
            
            int comparar[] = {-100, -90, -80, -70, -60, -50, -40, -30, -20};
            double señal = 0;
            for (int i = 0; i < comparar.length; i++) {
                if (a == comparar[i]) {
                    señal = Double.parseDouble(i + "." + ds);
                    System.out.println("señal: " + señal);
                }
            }
            //pasar frecuencia a algo entendible
            double fre = Double.parseDouble(freq.get(j).replace(" ", ""));
            double b = (fre * 1000 - 2400);
            b = b / 10;
            System.out.println("frecuencia: " + b);

            float variableA = (float) (-señal / 1.21) * -1;
            variableA = variableA * -1;
            if (b < 12) {
                for (double i = b - 1.2; i < b + 1.2; i += 0.1) {
                    jep.addVariable("x", i); //("variable", numero a evaluar)
                    jep.parseExpression("" + variableA + "*((x-" + b + ")^2)+" + señal);
                    //Si existiere algun error.
                    if (jep.hasError()) {
                        System.out.println(jep.getErrorInfo()); // Imprimir error.
                    }
                    //System.out.println((float) jep.getValue()); //Imprimir resultado.
                    datosY.add((float) jep.getValue() * -1);
                    datosX.add((float) i);
                }
                aListX.add(datosX);
                aListY.add(datosY);
            }
        }

    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        //aqui dibujamos los ejes x,y de las coordenadas
        g.setColor(Color.red);
        //eje x
        System.out.println(this.getWidth() / 2);
        g.drawLine(80, 0, 80, this.getHeight() - 50);
        //eje y
        g.drawLine(50, this.getHeight() - 80, this.getWidth() - 50, this.getHeight() - 80);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        //traslate cambia la posicion 0,0 de java al centro de nuestro plano cartesiano
        g2.translate(80, this.getHeight() - 80);

        //numeracion para Y
        g.setColor(Color.black);
        int valoresY = -100;
        for (double i = 0; i < 10; i++) {
            g.drawString("" + df.format(valoresY), -30, (int) (0 - (i * 50)));
            valoresY += 10;
        }

        //numeracion para x
        int valoresX = 2400;
        for (double i = 0; i < 11; i++) {
            g.drawString("" + df.format(valoresX), (int) (-15 + (i * 70)), 20);
            valoresX += 10;
        }

        //creamos lineas de los ejes 
        g2.setColor(Color.red);
        for (int i = 0; i < 11; i++) {
            //(x,y)
            //eje x
            g2.drawLine(70 * i, -10, 70 * i, 10);
        }
        for (int i = -9; i < 0; i++) {
            //eje y
            g2.drawLine(-10, 50 * i, 10, 50 * i);

        }

        //graficar
        Random rand = new Random();
        for (int i = 0; i < aListX.size(); i++) {
            double aux1 = Math.round(aListX.get(i).get(aListX.get(i).size() / 2));
            double aux2 = Math.round(aListY.get(i).get(aListY.get(i).size() / 2));

            float r1 = rand.nextFloat();
            float g1 = rand.nextFloat();
            float b1 = rand.nextFloat();
            Color randomColor = new Color(r1, g1, b1);
            g2.setColor(randomColor);
            g.drawString(mostrar.get(i), (int) aux1 * 70, (int) aux2 * 50);

            for (int j = 0; j < aListX.get(i).size(); j++) {
                if (j + 1 < aListX.get(i).size()) {
                    if (aListY.get(i).get(j) <= 0.1 && aListY.get(i).get(j + 1) <= 0.1) {
                        //System.out.println("y es igual a cero");
                        Line2D l = new Line2D.Float(
                                (float) aListX.get(i).get(j) * 70.0f,
                                (float) aListY.get(i).get(j) * 50.0f,
                                (float) aListX.get(i).get(j + 1) * 70.0f,
                                (float) aListY.get(i).get(j + 1) * 50.0f
                        );
                        g2.setStroke(new BasicStroke(2.2f));
                        g2.draw(l);
                    }
                }
            }
        }
    }

}
