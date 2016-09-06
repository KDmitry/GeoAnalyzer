package edu.dmitry.geomap.swingwaypoint;

import edu.dmitry.geomap.MapDlg;
import edu.dmitry.geomap.datamodel.LocationStatistic;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class SwingWaypoint extends DefaultWaypoint {
    private final JButton button;
    private LocationStatistic locationStatistic;
    private MapDlg mapDlg;

    public class RoundButton extends  JButton {
        private int buttonsGroupId;
        private SwingWaypoint swingWaypoint;
        private Color color;
        private Color selectedColor;
        private String text;

        public RoundButton(int buttonsGroupId, SwingWaypoint swingWaypoint, String text, Color color, Color selectedColor) {
            this.buttonsGroupId = buttonsGroupId;
            this.swingWaypoint = swingWaypoint;
            this.text = text;
            this.color = color;
            this.selectedColor = selectedColor;
        }

        public SwingWaypoint getSwingWaypoint() {
            return swingWaypoint;
        }

        public void setRoundButtonSize(int x, int y) {
            setSize(x, y);
            setPreferredSize(new Dimension(x, y));
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isSelected()) {
                g.setColor(selectedColor);
            } else {
                g.setColor(color);
            }

            g.fillOval(0, 0, getSize().width-1, getSize().height-1);
            g.setColor(Color.black);
            g.setFont(new Font("Verdana", Font.PLAIN, 16));
            int x = getSize().width / 2;
            int y = getSize().height / 2;
            g.drawString(text, x - 5 * text.length() , y + 6);
        }

        @Override
        protected void paintBorder(Graphics g) {
            if (isSelected()) {
                g.setColor(selectedColor);
            } else {
                g.setColor(color);
            }

            g.drawOval(0, 0, getSize().width-1, getSize().height-1);
        }

        public int getButtonsGroupId() {
            return buttonsGroupId;
        }
    }

    // new Color(0, 160, 255, 60); голубой
    // new Color(0, 255, 0, 60); зеленый
    // new Color(255, 0, 0, 60); красный
    // new Color(255, 210, 65, 60); оранжевый

    public SwingWaypoint(MapDlg mapDlg, LocationStatistic locationStatistic) {
        super(new GeoPosition(locationStatistic.getLat(), locationStatistic.getLng()));
        this.mapDlg = mapDlg;
        this.locationStatistic = locationStatistic;

        Color color;
        Color selectedColor = new Color(0, 160, 255, 60);

        if (locationStatistic.getPeopleCount() < 300) {
            color = new Color(0, 255, 0, 50);
        } else if (locationStatistic.getPeopleCount() >= 300 && locationStatistic.getPeopleCount() < 1000) {
            color = new Color(255, 160, 65, 80);
        } else {
            color = new Color(255, 0, 0, 80);
        }

        button = new RoundButton(locationStatistic.getId(),
                this ,String.valueOf(locationStatistic.getPeopleCount()),
                color, selectedColor);
        button.addMouseListener(new SwingWaypointMouseListener());
        button.setVisible(true);
    }

    public JButton getButton() {
        return button;
    }

    public int getPeopleCount() {
        return locationStatistic.getPeopleCount();
    }

    public int getZoom() {
        return locationStatistic.getZoom();
    }

    public int getRadius() {
        return locationStatistic.getRadius();
    }

    private class SwingWaypointMouseListener implements MouseListener {
        boolean isAlreadyOneClick;
        boolean isDoubleClick;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (isAlreadyOneClick) {
                isDoubleClick = true;
                mapDlg.changeMap(locationStatistic.getLat(), locationStatistic.getLng());
                isAlreadyOneClick = false;
            } else {
                isAlreadyOneClick = true;
                isDoubleClick = false;
                java.util.Timer timer = new java.util.Timer("doubleClickTimer", false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isAlreadyOneClick = false;
                        if (!isDoubleClick) {
                            button.setSelected(true);
                            button.updateUI();
                            mapDlg.clickOnLocation(locationStatistic, (RoundButton) SwingWaypoint.this.getButton());
                        }
                    }
                }, 300);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
