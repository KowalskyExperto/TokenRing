import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TokenRing {
    static DataInputStream entrada = null;
    static DataOutputStream salida = null;
    static long token = 0;
    static int nodo;
    static Boolean primera_vez = true;

    static class Worker extends Thread {
        @Override
        public void run() {
            try {
                ServerSocket servidor = new ServerSocket(5000+nodo);
                Socket conexion = servidor.accept();
                entrada = new DataInputStream(conexion.getInputStream());
                System.out.println("DemoHilo");
            } catch (Exception e) {
                System.err.println("Error en el Worker: " + e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                System.err.println("Se necesita el numero de nodo");
                System.exit(-1);
            }
            else {
                nodo = Integer.valueOf(args[0]);
                Worker w = new Worker();
                w.start();
                Socket conexion = null;
                for(;;) {
                    try {
                        conexion = new Socket("localhost",5000+((nodo+1)%4));
                        break;

                    } catch (Exception e) {
                        Thread.sleep(1000);
                    }
                }
                salida = new DataOutputStream(conexion.getOutputStream());
                w.join();
                for(;;) {
                    if(nodo == 0) {
                        if(primera_vez) {
                            primera_vez = false;
                        } else {
                            token = entrada.readLong();
                        }
                    } else {
                        token = entrada.readLong();
                    }
                    token++;
                    if ((token % (4000 + nodo)) == 0) {
                        System.out.println("Valor =" + token);
                    }
                    salida.writeLong(token);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en Main: " + e);
        }
    }
}