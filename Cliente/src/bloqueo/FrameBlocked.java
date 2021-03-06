package bloqueo;

import cliente.Cliente;
import cliente.SesionCliente;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;

public class FrameBlocked extends javax.swing.JFrame {

    public static int alto;
    Robot shortcutKiller;
    private ArrayList<String> correctos ;
    public boolean inter = false;
    public static int ancho;
    Timer timer;
    Timer fondo;
    int foto;
    ////////////CONSTRUCTOR/////////////////////
    public FrameBlocked()
    {
        this.setUndecorated(true);                                  //quita bordes a jframe
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  //Evita cierre con ALT+F4
        tamaño();                                                   //Obtiene tamaño de pantalla
        initComponents();
        revisaConeccion();                                          //Revisa si hay conexión a internet
        carga();
        fondo=new Timer();
        fondo.schedule(cambioFondo,0,10000);
        pass.setEchoChar('•');
        pass.setUI(new PassHint("Password"));
        user.setUI(new Hint("Codigo"));
    //Carga el keylistener y llama keepfocus
    }
    ////////////////////////////////////////////
    
    //Keylistener para detectar los atajos de teclado y evitar que salgan del bloqueo
        KeyListener listen = new KeyListener() {

        @Override
        public void keyTyped(KeyEvent e) {
            detector(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            detector(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            detector(e);
        }
    };
    
    //Obtiene el tamaño de la pantalla
    public void tamaño() {
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.ancho = d.width ;
        this.alto = d.height;
    }
    
    //Activa el bloqueo con login
    public void login() {
        this.setVisible(true);
        revisaConeccion();
        panel.setVisible(true);
        estilos();
    }
    
    //Activa el bloqueo por tiempo sin posibilidad de login
    public void bloqueoCompleto()
    {
        this.setVisible(true);
        panel.setEnabled(false);
        panel.setVisible(false);
        estilos();
        this.repaint();
    }
    public boolean estaBloqueado()
    {
        return (panel.isVisible());
    }
    
    //Coloca la imagen de fondo, mantiene el focus en el panel e inicia el keylistener
    private void carga()
    {
        revisaAvisos();
        colocarImagen(revisaAvisos());
        this.setExtendedState(MAXIMIZED_BOTH);    //maximizado
        this.setAlwaysOnTop(true);                //siempre al frente       
        new KeepFocus(this).block();              //Envia este frame y lo pone al frente cada 50 milisegundos para evitar perder el focus
        
        try {
            shortcutKiller = new Robot();          //inicializa la variable global shortcutKiller
        } catch (AWTException e) {
            e.printStackTrace();
        }
        
        pass.addKeyListener(listen);
        user.addKeyListener(listen);
        entrar.addKeyListener(listen);
        this.addKeyListener(listen);
    }

    public String revisaAvisos() {
        String sDirectorio = "src/images";
        File f = new File(sDirectorio);
        if (f.exists()) {
            File[] ficheros = f.listFiles();
            correctos=new ArrayList();
            for (int x = 0; x < ficheros.length; x++) {
                String ext=ficheros[x].getName().substring(ficheros[x].getName().length()-3, ficheros[x].getName().length());
                if (ext.equals("jpg")) {
                    correctos.add(ficheros[x].getName());
                }
            }
            
            Random r = new Random();
            return correctos.get(r.nextInt(correctos.size()));
        } else {
            System.err.println("Directorio de avisos inexistente");
            return "1.jpg";
        }
    }
    //Este metodo detiene los atajos de teclado ALT, HOME y CTRL
    public void detector(KeyEvent evt) {
        if (evt.getKeyCode() == 524) {
            shortcutKiller.keyRelease(KeyEvent.VK_HOME);
        }

        if (evt.getKeyCode() == 18) {
            shortcutKiller.keyRelease(KeyEvent.VK_ALT);
            shortcutKiller.keyPress(KeyEvent.VK_SHIFT);
            shortcutKiller.keyRelease(KeyEvent.VK_SHIFT);
        }

        if (evt.getKeyCode() == 17) {
            evt.consume();
            shortcutKiller.keyPress(KeyEvent.VK_SHIFT);
            shortcutKiller.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        user = new javax.swing.JTextField();
        entrar = new javax.swing.JButton();
        pass = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));

        panel.setBackground(new java.awt.Color(204, 204, 204));
        panel.setToolTipText("");
        panel.setFocusable(false);

        user.setPreferredSize(new java.awt.Dimension(196, 28));

        entrar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        entrar.setText("Entrar");
        entrar.setFocusPainted(false);
        entrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entrarActionPerformed(evt);
            }
        });

        pass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setText("jButton1");
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addContainerGap(54, Short.MAX_VALUE)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(35, 35, 35)
                        .addComponent(entrar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pass)
                    .addComponent(user, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
                .addGap(50, 50, 50))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entrar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(345, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(253, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void entrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entrarActionPerformed
        iniciar();
    }//GEN-LAST:event_entrarActionPerformed

    private void passActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passActionPerformed
        iniciar();
    }//GEN-LAST:event_passActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed
    public void iniciar() {
        // REVISA SI LOS CAMPOS ESTÁN VACIOS
        this.panel.repaint();
        if (this.user.getText().equals("")) {

            JOptionPane.showMessageDialog(this, "Ingresa tu código y Nip", ".::ERROR::.", JOptionPane.ERROR_MESSAGE);

        } else if (inter == true) {
            //VALIDACION DE CODIGO Y NIP
            Login log = new Login();
            if (log.login(this.user.getText(), this.pass.getText())) {
                try {
                    Thread.sleep(1000);
                    this.dispose();
                    Cliente.sesion = new SesionCliente(user.getText());
                    this.user.setText("");
                    this.pass.setText("");
                    ((Hint)this.user.getUI()).setVisible(true);
                    ((PassHint)this.pass.getUI()).setVisible(true);
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FrameBlocked.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Datos Incorrectos", ".::ERROR::.", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            try {
                Thread.sleep(1000);
                this.dispose();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameBlocked.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
        //envia la imagen deseada a background
   public void colocarImagen(String img) {
        try {
            Background p = new Background(ancho, alto, "src/images/" + img);
            this.add(p, BorderLayout.CENTER);
            p.repaint();
            Thread.sleep(5);
            pass.repaint();
            panel.repaint();
        } catch (InterruptedException ex) {
            Logger.getLogger(FrameBlocked.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void revisaConeccion() {
        //VERIFICA CONECCIÓN A INTERNET
        CheckConnection internet = new CheckConnection();
        if (internet.isConnected()) {
            inter = true;
            this.pass.setVisible(true);
        } else {
            inter = false;
            borrarComp();
        }
    }
    //borra el campo de contraseña
    public void borrarComp() {
        this.pass.setVisible(false);
    }
    
    //modifica apariencia del panel de usuario y contraseña
    public void estilos(){
        Color c=new Color(145,145,145);
        Color a=new Color(235,37,37,150);
        panel.setBackground(c);
        panel.setBorder( BorderFactory.createLineBorder(a, 1, true));
    }
    TimerTask cambioFondo=new TimerTask() {
        @Override
        public void run() {
            foto++;
            if(foto==correctos.size())
            {
                foto=0;
            }
            colocarImagen(correctos.get(foto));
            
        }
    };
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrameBlocked.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameBlocked.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameBlocked.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameBlocked.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameBlocked().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton entrar;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel panel;
    private javax.swing.JPasswordField pass;
    private javax.swing.JTextField user;
    // End of variables declaration//GEN-END:variables
 
}

