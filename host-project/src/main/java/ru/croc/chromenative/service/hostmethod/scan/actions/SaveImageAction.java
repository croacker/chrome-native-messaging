package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import ru.croc.chromenative.service.hostmethod.scan.App;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.sggr.scan.ImageUtils;


/**
 * Обработчик сохранения изображения
 * @since  26.01.12 Time: 12:34
 */
public class SaveImageAction extends AbstractAction implements Runnable {

    public synchronized void actionPerformed(final ActionEvent event) {
        new Thread(this).start();
    }

    public synchronized void run() {
        App app = ScanApp.getApp();
        try {
            app.updateStatus("Working ...");

            Image image = app.getSelected();
            BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);

            JFileChooser chooser = new JFileChooser();
            // Java 1.6 introduced a comfortable ImageIO.getWriterFileSuffixes() method.
            String e[] = ImageIO.getWriterFormatNames();
            for (int i = 0; i < e.length; i++) {
                chooser.addChoosableFileFilter(new Filter(e[i]));
            }
            int result = chooser.showSaveDialog(app.getRootPanel());
            if (result == JFileChooser.APPROVE_OPTION) {
                String ext = chooser.getFileFilter().getDescription();
                ext = ext.substring(0, ext.indexOf(' ')).toLowerCase();
                File file = chooser.getSelectedFile();
                String name = file.getName();
                if (!name.endsWith(ext)) {
                    file = new File(file.getParentFile(), name + "." + ext);
                }
                ImageIO.write(bufferedImage, ext, file);
            }
        } catch (final Throwable exception) {
            JOptionPane.showMessageDialog(app.getRootPanel(), exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
            app.updateStatus("Failed, try again ...");
        }
    }

    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasSelected();
    }

    private class Filter extends FileFilter {

        String type;

        Filter(final String type) {
            this.type = type;
        }

        public boolean accept(final File file) {
            return file.getName().endsWith(type);
        }

        public String getDescription() {
            return type + " Files";
        }
    }
}