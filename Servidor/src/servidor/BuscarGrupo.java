package servidor;

import interfaz.Principal;
import interfaz.Tareas;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Ricardo
 */
public class BuscarGrupo extends Principal {
    InetAddress miIp;
    String nombre;
    MulticastSocket puerto;
    Thread escucha;
    static boolean libre=false;
    InetAddress ia;
    Timer t=new Timer();
    static ArchivoConf conf = new ArchivoConf();
    static ArrayList<Clientes> cliente=new ArrayList<>();
    DatagramPacket pregunta;
    static Tareas tareas=null;
    int ip=1;
    String p="?,";
    ThreadPoolExecutor pool;

    
    
    public BuscarGrupo()
    {
        try {
            miIp=InetAddress.getLocalHost();
            puerto=new MulticastSocket(1000);
            escucha = new Thread(r);
        
        } catch (IOException ex) {
            Logger.getLogger(BuscarGrupo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //obtiene la configuracion
    public ArchivoConf getConf() {
        return conf;
    }

    //guarda cambios en la configuracion del servidor
    public void setConf(ArchivoConf conf) {
        BuscarGrupo.conf = conf;
    }
    //metodo que se llama para iniciar el seridor
    public void iniciarServidor()
    {
        cliente=new Clientes().cargarClientes();
        if(conf.CargarConf())
        {
            try {
                puerto=new MulticastSocket(1000);
                puerto.joinGroup(InetAddress.getByName(conf.getGrupo()));
                System.out.println("estamos en el grupo ------>"+conf.getGrupo() );
                this.inicarHilos();
                confPrincipal=this.getConf();
            } catch (IOException ex) {
                Logger.getLogger(BuscarGrupo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            this.buscarGrupo();
            confPrincipal=this.getConf();
        }
    }
    /*metodo que inicia los hilos de busqueda y escuha
    para buscar un grupo multicast libre*/
    public void inicarHilos()
    {
            escucha.start();
            System.out.println("Se creo el hilo correctamente");
    }
    public void buscarGrupo()//?
    {
        nombreServ();
        inicarHilos();
        t.schedule(tarea,0,1000);
    }
    TimerTask tarea=new TimerTask() {
        @Override
        public void run() {
            if(libre)
            {
                t.cancel();
//                escucha.stop(); //comentado para hacer pruebas
//                puerto.close();
                conf.setGrupo(ia.getHostAddress());
                System.out.println("Servidor:"+nombre);
                System.out.println("\033[32m****** grupo Libre -> "+"224.0.0."+ip);
                conf.nuevoArchivo();
            }
            else
            {
                try {
                    if(ip>1)
                    {
                        puerto.leaveGroup(InetAddress.getByName("224.0.0."+ip));
                    }
                    ip++;
                    System.err.println("Preguntando a la direccion-->224.0.0."+ip);
                ia=InetAddress.getByName("224.0.0."+ip);
                puerto.joinGroup(ia);
                byte mensaje[]=p.getBytes();
                pregunta=new DatagramPacket(mensaje, mensaje.length,ia,1000);
                puerto.send(pregunta);
                
                } catch (IOException ex) {
                   ex.printStackTrace();
                }
            }
        }
    };
    public void nombreServ()
    {
        nombre=JOptionPane.showInputDialog(null, "Escriba un nombre para el equipo");
        conf.setNombreServ(nombre);
    }
    
    
    Runnable r=new Runnable() {
            @Override
            public void run() {
                ExecutorService executor=Executors.newCachedThreadPool();
                pool=(ThreadPoolExecutor)executor;
                try {
                    byte buf[];
                    DatagramPacket dp;
                    while(true)
                    {
                        buf=new byte[500000];
                        dp=new DatagramPacket(buf,buf.length );
                        System.out.println("esperando respuesta");
                       System.out.println(puerto.getLocalPort());
                        puerto.receive(dp);
                         
//                        System.out.println("asd");
                        executor.submit(new HiloCliente(dp,ia,puerto));
                        
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BuscarGrupo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };//**
    
}
