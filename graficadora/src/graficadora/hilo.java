/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graficadora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.nfunk.jep.JEP;

/**
 *
 * @author jonathan
 */
public class hilo extends Thread {

    private plano plano;
    private JTextArea ta = new JTextArea();
    private boolean escribir=false;

    ArrayList<String> channel1 = new ArrayList<>();
    ArrayList<String> freq1 = new ArrayList<>();
    ArrayList<String> signal1 = new ArrayList<>();
    ArrayList<String> essid1 = new ArrayList<>();

    public hilo(plano plano, JTextArea ta) {
        this.plano = plano;
        this.ta = ta;
    }

    public ArrayList<String> getChannel() {
        return this.channel1;
    }

    public ArrayList<String> getEssid() {
        return this.essid1;
    }

    public ArrayList<String> getFreq() {
        return this.freq1;
    }

    public ArrayList<String> getSignal() {
        return this.signal1;
    }
    public void setEscribir(boolean escribir){
        this.escribir=escribir;
    }
    public boolean getEscribir(){
        return this.escribir;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ArrayList<ArrayList<Float>> aListX = new ArrayList<ArrayList<Float>>();
                ArrayList<ArrayList<Float>> aListY = new ArrayList<ArrayList<Float>>();
                ArrayList<String> mostrar = new ArrayList<>();
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

                this.essid1 = essid;
                this.channel1 = channel;
                this.freq1 = freq;
                this.signal1 = signal;

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
                    int f = Integer.parseInt(signal.get(j));
                    int a = (int) Math.floor(f / 10) * 10;
                    String ds = "" + f;
                    ds = ds.charAt(ds.length() - 1) + "";

                    int comparar[] = {-100, -90, -80, -70, -60, -50, -40, -30, -20};
                    double señal = 0;
                    for (int i = 0; i < comparar.length; i++) {
                        if (a == comparar[i]) {
                            señal = Double.parseDouble(i + "." + ds);
                            System.out.println("señal: " + señal);
                        }
                    }
                    //pasar frecuencia a algo entendible
                    double fre = Double.parseDouble(freq.get(j));
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
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mostrar.size(); i++) {
                    sb.append(mostrar.get(i) + "\n");
                }
                this.ta.setText(sb.toString());
                System.out.println("grafica..................................");
                this.escribir=true;
                
                plano.setDatos(aListY, aListX, mostrar);
                plano.repaint();
            } catch (IOException ex) {
                Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
