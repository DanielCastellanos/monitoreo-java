package interfaz;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.zip.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidor.Ordenes;

/**
 *
 * @author PC11
 */
public class EnviarArchivo extends javax.swing.JFrame{
    
    
    private static File[] archivosTem;
    private Ordenes orden=new Ordenes();
    ArrayList <String> documentos=new ArrayList<>();
    ArrayList <String> nombres=new ArrayList<>();
    boolean individual=false;
    String nombre,ip;
    String ruta;
    //metodo de envio a un solo usuario
    public EnviarArchivo(String ip) {
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setIconImage(Principal.getLogo());
        this.setTitle("Enviar a todos los equipos dentro del grupo");
        this.ip=ip;
    }
    //envio a varios usarios
    public EnviarArchivo(String nombre,String ip) {
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setIconImage(Principal.getLogo());
        this.setTitle("Enviar Archivo a "+nombre);
        this.nombre=nombre;
        this.ip=ip;
    }
    private static JFileChooser agregarFiltros(JFileChooser arch)
  {
      FileNameExtensionFilter imagenes=new FileNameExtensionFilter("JPG, PNG & GIF","jpg","png","gif");
      FileNameExtensionFilter video=new FileNameExtensionFilter("MP4,AVI,FLV,MKV & WMV","mp4","avi","flv","mkv","wmv");
      FileNameExtensionFilter audio=new FileNameExtensionFilter("WMA & MP3","mp3","wma");
      FileNameExtensionFilter archivos=new FileNameExtensionFilter("TXT & PDF","txt","pdf");
      arch.setFileFilter(imagenes);
      arch.setFileFilter(video);
      arch.setFileFilter(audio);
      arch.setFileFilter(archivos);
      return arch;
  }

    private void zip() throws IOException {
        ZipOutputStream zout = null;
        FileOutputStream fos;
        detalles.append("Comprimiendo archivo en zip...\n");
        try {
            ruta="MyFile.zip";
             fos = new FileOutputStream(ruta);
    		
            zout = new ZipOutputStream(fos);
            int i=0;
                for (String documento : documentos) {
                ZipEntry ze = new ZipEntry(nombres.get(i));
                zout.putNextEntry(ze);
                RandomAccessFile r=new RandomAccessFile(documentos.get(i),"r");
                byte[] info=new byte[(int)r.length()];
                r.readFully(info);
                zout.write(info,0,info.length);
                zout.closeEntry();
                i++;
            }
        } finally {
            if (zout != null) {
                zout.close();
            }
        }
        orden.enviarArchivo(ruta,ip);
    }//Zuno

    Thread archivo=new Thread();
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selecionar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        detalles = new javax.swing.JTextArea();
        aceptar = new javax.swing.JButton();
        cancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        selecionar.setText("Agregar archivo");
        selecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selecionarActionPerformed(evt);
            }
        });

        detalles.setColumns(20);
        detalles.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        detalles.setRows(5);
        detalles.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        detalles.setFocusable(false);
        jScrollPane1.setViewportView(detalles);

        aceptar.setText("Enviar");
        aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarActionPerformed(evt);
            }
        });

        cancelar.setText("Cancelar");
        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(selecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                        .addComponent(cancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aceptar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aceptar)
                    .addComponent(cancelar)
                    .addComponent(selecionar))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selecionarActionPerformed
        JFileChooser f = new JFileChooser();
        String ext = null;
        f = agregarFiltros(f);
        f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        f.setFileFilter(f.getAcceptAllFileFilter());
        f.setMultiSelectionEnabled(true);
        
        if (f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            
            archivosTem = f.getSelectedFiles();
            String aux;
            for (int i = 0; i < archivosTem.length; i++) {
                aux=archivosTem[i].getPath();
                documentos.add(aux);
            ext = aux.substring(aux.lastIndexOf((char) 92) + 1);
            nombres.add(ext);
            detalles.append("archivo añadido: "+ext+"\n");
            }
        } else {
            System.out.println("No seleccion ");
        }
    }//GEN-LAST:event_selecionarActionPerformed

    private void aceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarActionPerformed
        try {
            
            zip();
            detalles.append("Compresión finalizada.\n");
            //la ruta del .zip esta en la variable 'ruta'
            if (individual) {
                //envio a una sola pc
                
            }else{
                //envio por multicast
                
            }
            detalles.append("Envio realizado");
            this.dispose();
        } catch (IOException ex) {
            Logger.getLogger(EnviarArchivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_aceptarActionPerformed

    private void cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarActionPerformed
         this.dispose();
    }//GEN-LAST:event_cancelarActionPerformed
    
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
            java.util.logging.Logger.getLogger(EnviarArchivo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EnviarArchivo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EnviarArchivo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EnviarArchivo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() 
            {
                new EnviarArchivo("").setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aceptar;
    private javax.swing.JButton cancelar;
    private javax.swing.JTextArea detalles;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton selecionar;
    // End of variables declaration//GEN-END:variables
}
