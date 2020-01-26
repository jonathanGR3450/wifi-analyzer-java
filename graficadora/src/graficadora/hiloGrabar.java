/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graficadora;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jonathan
 */
public class hiloGrabar extends Thread {

    private hilo hilo;
    private String ruta;
    private int time = 0;
    private BufferedWriter bw = null;
    private FileWriter fw = null;

    public void setDato(hilo hilo, String ruta, int time) throws IOException {
        this.hilo = hilo;
        this.ruta = ruta;
        this.time = time;

        File file = new File(ruta);
        // Si el archivo no existe, se crea!
        if (!file.exists()) {
            file.createNewFile();
        }
        fw = new FileWriter(file.getAbsoluteFile(), true);
        bw = new BufferedWriter(fw);

    }

    @Override
    public void run() {
        while (true) {
            try {
                int hora, minutos, segundos;

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
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);
                //hora + ":" + minutos + ":" + segundos
                bw.write(hora + ":" + minutos + ":" + segundos + "\n");

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
                bw.write(nombre.toString());
                bw.write(canal.toString());
                bw.write(freq.toString());
                bw.write(señal.toString());

                try {
                    sleep(time * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(hiloGrabar.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(hiloGrabar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void cerrarFichero() {
        try {
            //Cierra instancias de FileWriter y BufferedWriter
            if (bw != null) {
                bw.close();
            }
            if (fw != null) {
                fw.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
