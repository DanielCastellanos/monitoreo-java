package servidor;

import cliente.SesionCliente;
import entity.Pc;
import interfaz.AppSystemTray;
import interfaz.BDConfig;
import interfaz.Principal;
import interfaz.Tareas;
import interfaz.VentanaPropiedades;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static servidor.Servidor.bloqueo;

/**
 *
 * @author Ricardo
 */
public class BuscarGrupo extends Principal {

    public static InetAddress miIp;           //ip del servidor
    String nombre;              //nombre del servidor
    MulticastSocket puerto;     //puerto multicast
    Thread escucha, //Hilo para mensajes multicast
            con, //Hilo para que los clientes verifiquen la conexion con el servidor
            sesiones;       //Hilo para resivir los archivos de sesiones de los clientes
    static boolean libre = false;         //Variable para verificar si el grupo Multicast esta ocupado
    InetAddress ia;         //InetAddress para los grupos multicast
    Timer t = new Timer();    //Timer para preguntar en los grupos multicast
    public static Configuracion conf = new Configuracion();        //Variable de la configuracion del servidor
    public static ArrayList<Pc> equipos = new ArrayList<>();   //Lista de clientes
    public static ArrayList<Pc> ePendientes = new ArrayList<>();   //Lista de clientes
    public static ArrayList<SesionCliente> listaSesiones = new ArrayList<>(); //lista de pruebas para las sesiones
    DatagramPacket pregunta;    //Datagrama para enviar los mensajes multicast
    static Tareas tareas = null;  //Objeto de tipo Tarea para los procesos de los clientes
    public static VentanaPropiedades propiedades = null;
    int ip = 1;       //contador para preguntar a los grupos multicast
    ThreadPoolExecutor pool; //variable para ayuda en control de los hilos

    public BuscarGrupo() {
        try {
            miIp = InetAddress.getLocalHost();     //obtenemos la direccion IP de nuestro PC
            System.out.println(miIp.toString());
            puerto = new MulticastSocket(1000);    //declaramos el puerto y lo situamos en el puerto 1000
            sesiones = new Thread(HiloSesiones);  //hilo para la captura de sesiones
            escucha = new Thread(r);        //Iniciamos el timer para Preguntar a los grupos multicast
            con = new Thread(conexion);     //iniciamos el hilo para que los clientes verifiquen conexion 
        } catch (IOException ex) {
            Logger.getLogger(BuscarGrupo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //metodo que se llama para iniciar el seridor
    public void iniciarServidor() {
        //cargamos la lista de clientes
        equipos = Archivos.cargarListaClientes();
        //verificamos si existe configuracion
        conf = Archivos.cargarConf();
        if (conf != null) {
            try {
                conf.pcToString();
                //mostramos mensaje de carga de configuracion
                AppSystemTray.mostrarMensaje("Cargando configuración", AppSystemTray.INFORMATION_MESSAGE);
                //nos unimos al grupo multicast
                puerto.joinGroup(InetAddress.getByName(conf.getGrupo()));
                //Iniciamos los hilos
                this.inicarHilos();
                //pasamos la configuracion a Principal(Interfaz)
                Principal.setConf(conf);
                //Inicia el monitoreo del uso del pc
                Servidor.timerUso.schedule(Servidor.updateUsage, Servidor.tRegUso, Servidor.tRegUso);
                if(!bloqueo.isActive()){
                    bloqueo.setVisible(true);
                }
            } catch (IOException ex) {
                System.out.println("Error");
            }
        } else {
            conf = new Configuracion();
            
            //mostramos mensaje de que no se encontro configuracion
            AppSystemTray.mostrarMensaje("no se encontro configuración", AppSystemTray.INFORMATION_MESSAGE);
            this.buscarGrupo();

        }
    }

    /*metodo que inicia los hilos de busqueda y escuha
    para buscar un grupo multicast libre*/
    public void inicarHilos() {
        escucha.start();
        sesiones.start();
        con.start();
    }

    public void buscarGrupo() {
        conf.setPcServidor(new Ordenes().getInfoPc());
        
        nombreServ();
        inicarHilos();
        //iniciamos la busqueda y preguntamos cada segundo
        t.schedule(tarea, 0, 1000);
    }
    TimerTask tarea = new TimerTask() {
        @Override
        public void run() {
            if (libre)//si el grupo esta libre
            {
                //cancelamos la busqueda
                t.cancel();
                //Agregamos la direccion a la configuracion
                conf.setGrupo(ia.getHostAddress());
                conf.getPcServidor().setGrupo(Byte.parseByte(ia.getHostAddress().substring(ia.getHostAddress().lastIndexOf(".")+1)));
                //mostramos mensaje de que se esta uniendo al grupo
                AppSystemTray.mostrarMensaje("Uniendose al grupo 224.0.0." + ip, AppSystemTray.INFORMATION_MESSAGE);
                //Mostramos la configuracion de la BD
                new BDConfig(conf).setVisible(true);
                //Creamos el nuevo archivo de configuracion
                new Archivos().guardarConf(conf);
                
                //pasamos la configuracion a Principal(Interfaz)
                Principal.setConf(conf);
                //Iniciamos el monitoreo del tiempo de uso
                Servidor.timerUso.schedule(Servidor.updateUsage, Servidor.tRegUso, Servidor.tRegUso);
            } else {
                //si no estalibre seguimos con la siguiente direccion
                try {
                    if (ip > 1) {
                        //si el valor es mayor a 1
                        //abandonamos el grupo que esta ocupado
                        puerto.leaveGroup(InetAddress.getByName("224.0.0." + ip));
                    }
                    //aumentamos el contador
                    ip++;

                    System.err.println("Preguntando a la direccion-->224.0.0." + ip);
                    //creamos la direccion
                    ia = InetAddress.getByName("224.0.0." + ip);
                    //nos unimos al grupo
                    puerto.joinGroup(ia);
                    //convertimos el String a arreglo de bytes
                    byte mensaje[] = "?,".getBytes();
                    //preparamos el paquete con el mensaje
                    pregunta = new DatagramPacket(mensaje, mensaje.length, ia, 1000);
                    //enviamos el mensaje
                    puerto.send(pregunta);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    };

    public void nombreServ() {
        //Mostranos mensaje para  ingresar el nombre del servidor
        do {
            nombre = JOptionPane.showInputDialog(null, "Escriba un nombre para el equipo");
            System.out.println(nombre);
        } while (nombre == null);
        //Agregamos el nombre del servidor a la configuracion
        conf.setNombreServ(nombre);
        conf.getPcServidor().setNombre(nombre);
        try {
            conf.getPcServidor().setHostname(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ex) {
            Logger.getLogger(BuscarGrupo.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (nombre.contains(",")) {
            nombreServ();
        }
        conf.pcToString();
    }
    Runnable conexion = new Runnable() {
        @Override
        public void run() {
            try {
                ServerSocket ss = new ServerSocket(4500);
                while (true) {
                    Socket s = ss.accept();
                    s.close();
                    System.out.println("El Cliente establecio conexion");
                }
            } catch (Exception e) {
                System.out.println("Error Conexion");
            }
        }
    };
//    Runnable webSocket = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                ServerSocket ss = new ServerSocket(4500);
//                while (true) {
//                    System.out.println("esperando mensaje web");
//                    Socket s = ss.accept();
//                    InputStream in = s.getInputStream();
//                    OutputStream out = s.getOutputStream();
//                    String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
//                    Matcher get = Pattern.compile("^GET").matcher(data);
//                    if (get.find()) {
//                        Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
//                        match.find();
//                        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
//                                + "Connection: Upgrade\r\n"
//                                + "Upgrade: websocket\r\n"
//                                + "Sec-WebSocket-Accept: "
//                                + DatatypeConverter
//                                        .printBase64Binary(
//                                                MessageDigest
//                                                        .getInstance("SHA-1")
//                                                        .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
//                                                                .getBytes("UTF-8")))
//                                + "\r\n\r\n")
//                                .getBytes("UTF-8");
//
//                        out.write(response, 0, response.length);
//
//                        System.out.println(new String("*_*_*_*_*_*_*_").trim());
//                        byte bufer[] = new byte[500];
//                        in.read(bufer);
//
//                        ArrayList<Byte> encoded = new ArrayList<>();
//                        byte[] decoded;
//                        byte[] key = new byte[4];
//                        int j = 0;
//                        for (int i = 2; i < 6; i++) {
//                            key[j] = bufer[i];
//                            j++;
//                        }
//                        for (int i = 6; i < bufer.length; i++) {
//                            if (bufer[i] != 0) {
//                                encoded.add(bufer[i]);
//                            }
//                        }
//                        decoded = new byte[encoded.size()];
//
//                        for (int i = 0; i < decoded.length; i++) {
//                            decoded[i] = (byte) (encoded.get(i) ^ key[i & 0x3]);
//                        }
//                        System.out.println(new String(decoded));
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
    Runnable r = new Runnable() {
        @Override
        public void run() {
            //variable que se encarga del manejo de los hilos
            ExecutorService executor = Executors.newCachedThreadPool();
            pool = (ThreadPoolExecutor) executor;
            try {
                //declaramos el buffer
                byte buf[];
                //declaramos el Datragram
                DatagramPacket dp;
                while (true) {
                    //iniciamos en arreglo con una logitud de 500 KB aprox
                    buf = new byte[500000];
                    //Preparamos el paquete
                    dp = new DatagramPacket(buf, buf.length);
                    //recivimos el paquete
                    puerto.receive(dp);
                    //mandamos el paquete a un hilo para su procesado
                    executor.submit(new HiloCliente(dp,InetAddress.getByName(conf.getGrupo()), puerto));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    };

    Runnable HiloSesiones = new Runnable() {
        @Override
        public void run() {
            ExecutorService executor = Executors.newCachedThreadPool();
            try {
                ServerSocket ss = new ServerSocket(4600);
                while (true) {
                    System.out.println("Esperando Sesion");
                    Socket s = ss.accept();
                    executor.submit(new GuardarSesion(s));

                }
            } catch (IOException ex) {
                Logger.getLogger(BuscarGrupo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
}
