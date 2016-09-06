package edu.Dmitry.geodownloader;

import com.github.axet.desktop.Desktop;
import com.github.axet.desktop.DesktopSysTray;
import edu.Dmitry.geodownloader.startup.RunOnStartUp;
import net.sf.image4j.codec.ico.ICODecoder;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TrayDlg {
    public enum  ProgramStatus {
        serverConnecting,
        working,
        checkUpdate,
        noConnection,
        closing
    }

    private DesktopSysTray tray = Desktop.getDesktopSysTray();
    private JPopupMenu menu;
    private JMenuItem menuItemStatus;
    private JMenuItem menuItemAutostart;
    private JMenuItem menuItemExit;
    private Manager manager;

    public TrayDlg() {
        createAndShowMenu();
        manager = new Manager(this);
        manager.start();
    }

    public void setStatus(ProgramStatus status) {
        switch (status) {
            case serverConnecting :
                menuItemStatus.setText("Connecting to server...");
                break;
            case working :
                menuItemStatus.setText("Working...");
                break;
            case checkUpdate:
                menuItemStatus.setText("Checking update...");
                break;
            case noConnection :
                menuItemStatus.setText("No connection");
                break;
            case closing:
                menuItemStatus.setText("Closing...");
                break;
            default:
                break;
        }
        tray.update();
    }

    public void closeTray() {
        tray.close();
        System.exit(0);
    }

    private void createAndShowMenu() {
        menu = new JPopupMenu();
        menuItemStatus = new JMenuItem();
        menuItemStatus.setEnabled(false);
        menu.add(menuItemStatus);
        menu.addSeparator();

        menuItemAutostart = new JMenuItem("Uninstall AutoStart");
        menuItemAutostart.addActionListener(arg0 -> {
            if (menuItemAutostart.getText().equals("Uninstall AutoStart")) {
                RunOnStartUp.uninstall();
                menuItemAutostart.setText("Install AutoStart");
            } else {
                RunOnStartUp.install();
                menuItemAutostart.setText("Uninstall AutoStart");
            }
            tray.update();
        });
        menu.add(menuItemAutostart);
        menu.addSeparator();

        menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(arg0 -> {
            menuItemAutostart.setEnabled(false);
            menuItemExit.setEnabled(false);
            tray.update();
            manager.close();
        });
        menu.add(menuItemExit);

        ImageIcon image = loadIcon("/images/icon.ico");

        tray.setIcon(image);
        tray.setTitle("GeoDownloader");
        tray.setMenu(menu);
        tray.show();
    }


    private ImageIcon loadIcon(String s) {
        try {
            InputStream is = getClass().getResourceAsStream(s);
            List<BufferedImage> bmp = ICODecoder.read(is);
            return new ImageIcon(bmp.get(0));
        } catch (RuntimeException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
