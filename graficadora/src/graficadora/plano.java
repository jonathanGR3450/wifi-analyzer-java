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

public class plano extends JPanel {

    private int n;
    private int n1;
    private Color color;
    private String interfaz;
    ArrayList<ArrayList<Float>> aListX = new ArrayList<ArrayList<Float>>();
    ArrayList<ArrayList<Float>> aListY = new ArrayList<ArrayList<Float>>();
    ArrayList<String> mostrar = new ArrayList<>();
    private Color[] col=new Color[30];
    private Random rand = new Random();

    public plano() throws IOException {

        this.interfaz();
        this.datos();
        this.setBackground(new Color(130,130,130));
        this.setLayout(new BorderLayout());
        
        for (int i = 0; i < this.col.length; i++) {
            float r1 = rand.nextFloat();
            float g1 = rand.nextFloat();
            float b1 = rand.nextFloat();
            Color randomColor = new Color(r1, g1, b1);
            col[i]=randomColor;
            
        }

    }
    public void setDatos(ArrayList<ArrayList<Float>> aListY, ArrayList<ArrayList<Float>> aListX, ArrayList<String> mostrar){
        this.aListX=aListX;
        this.aListY=aListY;
        this.mostrar=mostrar;
    }
    public String getMostrar(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mostrar.size(); i++) {
            sb.append(this.mostrar.get(i)+"\n");
        }
        return sb.toString();
    }

    public void datos() throws IOException {
        mostrar.clear();
        aListX.clear();
        aListY.clear();
        //consulta de valores a graficar
        ArrayList<String> data = new ArrayList<>();
        String[] consultaDatos = {"/bin/bash", "-c", "echo 160003450 | sudo -S iwlist wlp3s0 scan |egrep 'ESSID|Frequency|Channel|Signal'"};

        Process datos = Runtime.getRuntime().exec(consultaDatos);
        String line1;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(datos.getInputStream()))) {
            while ((line1 = input.readLine()) != null) {
                data.add(line1.replace("                    ", ""));
            }
        }
        ArrayList<String> channel = new ArrayList<>();
        ArrayList<String> freq = new ArrayList<>();
        ArrayList<String> signal = new ArrayList<>();
        ArrayList<String> essid = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            if (i * 4 + 3 < data.size()) {
                //System.out.println(Arrays.toString(data.get(i*4).split(":")));
                String auxchannel[] = data.get(i * 4).split(":");
                channel.add(auxchannel[1]);

                String auxfreq[] = data.get(i * 4 + 1).split(" ");
                String[] x = auxfreq[0].split(":");
                freq.add(x[1]);

                String auxsignal[] = data.get(i * 4 + 2).split("level=");
                String y[] = auxsignal[1].split(" ");
                signal.add(y[0]);

                essid.add(data.get(i * 4 + 3).replaceAll("ESSID:", ""));
            }
        }
        System.out.println(essid);
        System.out.println(signal);
        System.out.println(freq);
        System.out.println(channel);

        //obtenemos los valores de las graficas
        JEP jep = new JEP();
        jep.addStandardConstants();
        jep.addStandardFunctions();

        for (int j = 0; j < essid.size(); j++) {
            
            String mostrar1 = "Nombre: " + essid.get(j) + ", frecuencia: " + freq.get(j) + ", señal: " + signal.get(j) + ", canal: " + channel.get(j) +  ".";
            mostrar.add(mostrar1);
            
            ArrayList<Float> datosX = new ArrayList<Float>();
            ArrayList<Float> datosY = new ArrayList<Float>();

            //pasar datos obtenidos A datos que se puedan graficar
            int f = Integer.parseInt(signal.get(j));
            int a = (int) Math.floor(f / 10) * 10;
            String ds = "" + f;
            ds = ds.charAt(ds.length() - 1) + "";

            int comparar[] = {-100, -90, -80, -70, -60, -50, -40, -30, -20};
            double señal = 0;
            for (int i = 0; i < comparar.length; i++) {
                if (a == comparar[i]) {
                    señal = Double.parseDouble(i + "." + ds);
                }
            }
            //pasar frecuencia a algo entendible
            double fre = Double.parseDouble(freq.get(j));
            double b = (fre * 1000 - 2400);
            b = b / 10;

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

    public void interfaz() throws IOException {
        //hace la consulta para buscar el nombre de la interfaz wifi
        ArrayList<String> iwconfig = new ArrayList<>();
        String cmd = "iwconfig";
        Process pb = Runtime.getRuntime().exec(cmd);
        String line;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                if (!line.equals("")) {
                    iwconfig.add(line);
                }
            }
        }
        for (int i = 0; i < iwconfig.size(); i++) {
            String[] g = iwconfig.get(i).replaceAll("          ", "").split(" ");
            for (int j = 0; j < g.length; j++) {
                if (!g[j].replace(" ", "").equals("")) {
                    if (g[j].replace(" ", "").charAt(0) == 'w') {
                        this.interfaz = g[j].replace(" ", "");
                        //imprime el nombre de la tarjeta de wifi
                        System.out.println(interfaz);
                    }
                }
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
        
        for (int i = 0; i < aListX.size(); i++) {
            double aux1=Math.round(aListX.get(i).get(aListX.get(i).size()/2));
            double aux2=Math.round(aListY.get(i).get(aListY.get(i).size()/2));
            
            if (i<col.length) {
                g2.setColor(col[i]);
            }
            
            String str[]=mostrar.get(i).split(",");
            str=str[0].split(" ");
            g.drawString(str[1], (int) aux1*70, (int) aux2*50);
            
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
                        g2.setStroke(new BasicStroke(2.8f));
                        g2.draw(l);
                    }
                }
            }
        }
    }

}
