package edu.dmitry.geomap;

import com.github.lgooddatepicker.datepicker.DatePicker;
import com.github.lgooddatepicker.timepicker.TimePicker;
import com.github.lgooddatepicker.timepicker.TimePickerSettings;
import edu.dmitry.geomap.datamodel.LocationStatistic;
import edu.dmitry.geomap.swingwaypoint.SwingWaypoint;
import edu.dmitry.geomap.swingwaypoint.SwingWaypointOverlayPainter;
import edu.dmitry.geomap.swingwaypoint.WaypointPainter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.Component;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MapDlg extends JFrame implements Observer {
    private Logger logger = LogManager.getRootLogger();
    private Manager manager;
    private JXMapViewer mapViewer;
    private TimePicker timePicker1;
    private DatePicker datePicker1;
    private TimePicker timePicker2;
    private DatePicker datePicker2;
    private JButton showButton;
    private JCheckBox checkBox;
    private JLabel workStatus;
    private WaypointPainter<SwingWaypoint> swingWaypointPainter;
    private JTextField minPeople;
    private JLabel filterStatus;
    private JLabel fullInfoStatus;
    private JLabel peopleCount;
    private JList placesList;
    private JList hashTagsList;
    private int minPeopleFilter;
    private int zoom;
    private List<Integer> zooms = new ArrayList<>();
    private SwingWaypoint.RoundButton selectedLocation = null;

    public MapDlg(Manager manager) {
        this.manager = manager;
        manager.subscribe(this);

        initDlg();
        showDlg();

        zooms.add(-1);
        zooms.add(-1);
        zooms.add(30);
        zooms.add(50);
        zooms.add(100);
        zooms.add(200);
        zooms.add(400);
        zooms.add(800);
        zooms.add(2000);
        zooms.add(3000);
        zooms.add(6000);
        zooms.add(10000);
        zooms.add(30000);
        zooms.add(50000);
        zooms.add(100000);
        zooms.add(200000);
        zooms.add(400000);
        zooms.add(800000);
    }

    public void showDlg() {
        setVisible(true);
    }

    public void downloadingEnd() {
        workStatus.setText("");
        showButton.setEnabled(true);
    }

    public void setStatus(String status) {
        workStatus.setText(status);
    }

    public void updateStatus(int count, int allCount) {
        workStatus.setText("Отображено: " + count + " из " + allCount);
    }

    private void initDlg() {
        setTitle("Geo Map");
        setResizable(false);
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        setSize(new Dimension(1155, 600));
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.white);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        createMap(contentPane);
        createControllers(contentPane);
    }

    private void createControllers(JPanel contentPane) {
        Font font = new Font("Verdana", Font.PLAIN, 14);

        JLabel label1 = new JLabel("Период отображения");
        label1.setBounds(900, 5, 200, 30);
        label1.setFont(font);
        contentPane.add(label1);

        checkBox = new JCheckBox("Сейчас");
        checkBox.setFont(font);
        checkBox.setBounds(1065, 6, 110, 30);
        contentPane.add(checkBox);

        JLabel label2 = new JLabel("С : ");
        label2.setBounds(818, 40, 30, 30);
        label2.setFont(font);
        contentPane.add(label2);

        JLabel label3 = new JLabel("По : ");
        label3.setBounds(810, 75, 35, 30);
        label3.setFont(font);
        contentPane.add(label3);

        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.use24HourClockFormat();
        //timeSettings.initialTime = LocalTime.of(15, 30);
        timeSettings.generatePotentialMenuTimes(TimePickerSettings.TimeIncrement.FifteenMinutes, null, null);

        timePicker1 = new TimePicker(timeSettings);
        timePicker1.setBounds(850, 40, 90, 30);
        contentPane.add(timePicker1);

        datePicker1 = new DatePicker();
        datePicker1.setBounds(950, 40, 200, 30);
        datePicker1.setBackground(Color.white);
        contentPane.add(datePicker1);

        timePicker2 = new TimePicker(timeSettings);
        timePicker2.setBounds(850, 75, 90, 30);
        contentPane.add(timePicker2);

        datePicker2 = new DatePicker();
        datePicker2.setBounds(950, 75, 200, 30);
        datePicker2.setBackground(Color.white);
        contentPane.add(datePicker2);

        workStatus = new JLabel();
        workStatus.setFont(font);
        workStatus.setBounds(810, 110, 230, 32);
        contentPane.add(workStatus);

        showButton = new JButton("Отобразить");
        showButton.setFont(font);
        showButton.setBounds(1037, 110, 115, 30);
        showButton.addActionListener(e -> showButtonPressed());
        contentPane.add(showButton);

        JLabel label4 = new JLabel("Фильтр");
        label4.setFont(font);
        label4.setBounds(940, 140, 115, 30);
        contentPane.add(label4);

        JLabel label5 = new JLabel("Минимальное число человек: ");
        label5.setFont(font);
        label5.setBounds(810, 165, 265, 30);
        contentPane.add(label5);

        minPeople = new JTextField();
        minPeople.setBounds(1040, 165, 107, 30);
        minPeople.setColumns(10);
        contentPane.add(minPeople);

        filterStatus = new JLabel();
        filterStatus.setFont(font);
        filterStatus.setBounds(810, 200, 300, 30);
        contentPane.add(filterStatus);

        JButton button2 = new JButton("Применить");
        button2.setFont(font);
        button2.setBounds(1037, 200, 115, 30);
        button2.addActionListener(e -> applyFilters());
        contentPane.add(button2);

        JLabel label6 = new JLabel("Информация о локации");
        label6.setFont(font);
        label6.setBounds(895, 225, 220, 30);
        contentPane.add(label6);

        JLabel label7 = new JLabel("Количество человек: ");
        label7.setFont(font);
        label7.setBounds(810, 250, 250, 30);
        contentPane.add(label7);

        peopleCount = new JLabel("");
        peopleCount.setFont(font);
        peopleCount.setBounds(970, 250, 100, 30);
        contentPane.add(peopleCount);

        JLabel label8 = new JLabel("Названия мест:");
        label8.setFont(font);
        label8.setBounds(810, 270, 250, 30);
        contentPane.add(label8);

        placesList = new JList();
        placesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane placesListScrollPane = new JScrollPane(placesList);
        placesListScrollPane.setBounds(810, 300, 333, 70);
        contentPane.add(placesListScrollPane);

        JLabel label9 = new JLabel("Используемые хэштеги:");
        label9.setFont(font);
        label9.setBounds(810, 370, 300, 30);
        contentPane.add(label9);

        hashTagsList = new JList();
        hashTagsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane hashTagListScrollPane = new JScrollPane(hashTagsList);
        hashTagListScrollPane.setBounds(810, 400, 333, 130);
        contentPane.add(hashTagListScrollPane);

        fullInfoStatus = new JLabel();
        fullInfoStatus.setFont(font);
        fullInfoStatus.setBounds(810, 530, 300, 30);
        contentPane.add(fullInfoStatus);
    }

    private void applyFilters() {
        filterStatus.setText("");
        minPeopleFilter = 0;
        if (!minPeople.getText().equals("")) {
            try {
                int min = Integer.parseInt(minPeople.getText());
                if (min < 0) {
                    filterStatus.setText("Введите число больше 0!");
                    minPeople.setText("");
                    return;
                } else {
                    minPeopleFilter = min;
                }
            } catch (Exception ex) {
                filterStatus.setText("Введите целое число!");
                minPeople.setText("");
                return;
            }
        }
        applyPeopleFilter();
    }

    private void applyPeopleFilter() {
        for (Component component : mapViewer.getComponents()) {
            if (component instanceof SwingWaypoint.RoundButton) {
                SwingWaypoint.RoundButton roundButton = (SwingWaypoint.RoundButton) component;
                if (roundButton.getSwingWaypoint().getZoom() == zoom) {
                    if (roundButton.getSwingWaypoint().getPeopleCount() < minPeopleFilter) {
                        if (selectedLocation != null && roundButton.getButtonsGroupId() == selectedLocation.getButtonsGroupId()) {
                            selectedLocation.setSelected(false);
                            selectedLocation = null;

                            peopleCount.setText("");
                            placesList.setListData(new Vector());
                            hashTagsList.setListData(new Vector());
                            fullInfoStatus.setText("");
                        }

                        roundButton.setVisible(false);
                    } else if (!roundButton.isVisible()) {
                        roundButton.setVisible(true);
                    }
                }
            }
        }
    }

    private void showButtonPressed() {
        workStatus.setText("");

        if (timePicker1.getText().equals("") || timePicker2.getText().equals("") ||
                datePicker1.getText().equals("") || datePicker2.getText().equals("")) {
            workStatus.setText("Не все поля заполнены!");
            return;
        }

        LocalDate date1 = datePicker1.getDate();
        LocalTime time1 = timePicker1.getTime();

        LocalDate date2 = datePicker2.getDate();
        LocalTime time2 = timePicker2.getTime();

        DateTime from = new DateTime(date1.getYear(), date1.getMonthValue(), date1.getDayOfMonth(),
                time1.getHour(), time1.getMinute(), time1.getSecond());
        DateTime to = new DateTime(date2.getYear(), date2.getMonthValue(), date2.getDayOfMonth(),
                time2.getHour(), time2.getMinute(), time2.getSecond());

        if (from.isAfter(to)) {
            workStatus.setText("Первая дата больше второй!");
            return;
        }

        if (to.isAfter(new DateTime())) {
            workStatus.setText("Дата(ы) больше текущей!");
            return;
        }

        clearAll();
        showButton.setEnabled(false);
        workStatus.setText("Получение данных...");
        manager.getLocationsStatistic(from, to, false);
    }

    private void clearAll() {
        mapViewer.removeAll();
        mapViewer.updateUI();

        filterStatus.setText("");
        minPeopleFilter = 0;

        selectedLocation = null;
        peopleCount.setText("");
        placesList.setListData(new Vector());
        hashTagsList.setListData(new Vector());
        fullInfoStatus.setText("");
    }

    private void createMap(JPanel contentPane) {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(8);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        LocalResponseCache.installResponseCache(info.getBaseURL(), cacheDir, false);

        // Setup JXMapViewer
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition moscow = new GeoPosition(55.75222, 37.61556);

        // Set the focus
        zoom = 9;
        mapViewer.setZoom(zoom);
        mapViewer.setAddressLocation(moscow);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
        mapViewer.addPropertyChangeListener(evt -> {
            int currentZoom = ((JXMapViewer)evt.getSource()).getZoom();
            if (currentZoom != zoom) {
                zoomChanged(currentZoom);
            }
        });

        // Display the viewer
        mapViewer.setBounds(0, 0, 800, 600);

        swingWaypointPainter = new SwingWaypointOverlayPainter();
        mapViewer.setOverlayPainter(swingWaypointPainter);

        contentPane.add(mapViewer);
    }

    public void changeMap(double lat, double lng) {
        if (zoom > 2) {
            mapViewer.setAddressLocation(new GeoPosition(lat, lng));
            mapViewer.setZoom(zoom - 1);
        }
    }

    public void zoomChanged(int newZoom) {
        zoom = newZoom;

        boolean find = false;
        for (Component component : mapViewer.getComponents()) {
            if (component instanceof SwingWaypoint.RoundButton) {
                SwingWaypoint.RoundButton roundButton = (SwingWaypoint.RoundButton) component;

                if (roundButton.getSwingWaypoint().getZoom() != zoom) {
                    roundButton.setVisible(false);
                } else {
                    if (!roundButton.isVisible()) {
                        roundButton.setVisible(true);
                    }

                    setButtonSize(roundButton);

                    if (!find && selectedLocation != null && roundButton.getButtonsGroupId() == selectedLocation.getButtonsGroupId()) {
                        find = true;
                        selectedLocation.setSelected(false);
                        roundButton.setSelected(true);
                        selectedLocation = roundButton;
                    }

                }
            }
        }

        applyPeopleFilter();

        mapViewer.updateUI();

        if (!find) {
            if (selectedLocation != null) {
                selectedLocation.setSelected(false);
                selectedLocation = null;
            }

            peopleCount.setText("");
            placesList.setListData(new Vector());
            hashTagsList.setListData(new Vector());
        }
    }

    public void clickOnLocation(LocationStatistic locationStatistic, SwingWaypoint.RoundButton location) {
        if (selectedLocation != location) {
            if (selectedLocation != null) {
                selectedLocation.setSelected(false);
                selectedLocation.updateUI();
            }

            selectedLocation = location;

            peopleCount.setText("");
            placesList.setListData(new Vector());
            hashTagsList.setListData(new Vector());

            fullInfoStatus.setText("Идет загрузка данных...");
            manager.getFullLocationStatistic(locationStatistic);
        }
    }

    private void setButtonSize(SwingWaypoint.RoundButton roundButton) {
        int radius = roundButton.getSwingWaypoint().getRadius();
        int peopleCount = roundButton.getSwingWaypoint().getPeopleCount();
        if (radius < 30) {
            int x = 30;

            if (peopleCount > 9 && peopleCount < 50) {
                x = 35;
            }

            if (peopleCount > 50 && peopleCount < 100) {
                x = 40;
            }

            if (peopleCount > 100 && peopleCount < 500) {
                x = 45;
            }

            if (peopleCount > 500) {
                x = 50;
            }

            if (peopleCount > 1000) {
                x = 55;
            }

            roundButton.setRoundButtonSize(x, x);
        } else {
            int size = Math.round((float) radius / (float)  zooms.get(zoom) * 35);
            if (size < 35) {
                size = 35;
            }

            if (peopleCount > 1000 && size < 55) {
                size = 55;
            }

            if (peopleCount > 10000 && size < 65) {
                size = 65;
            }

            roundButton.setRoundButtonSize(size, size);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof LocationStatisticManager) {
            if (arg instanceof List){
                List<LocationStatistic> locationStatistics = (List<LocationStatistic>) arg;

                for (LocationStatistic locationStatistic : locationStatistics) {
                    if (locationStatistic.getPeopleCount() != 0) {
                        SwingWaypoint swingWaypoint = new SwingWaypoint(this, locationStatistic);
                        swingWaypointPainter.addWaypoint(swingWaypoint);
                        if (swingWaypoint.getPeopleCount() < minPeopleFilter || locationStatistic.getZoom() != zoom) {
                            swingWaypoint.getButton().setVisible(false);
                        }
                        setButtonSize((SwingWaypoint.RoundButton) swingWaypoint.getButton());
                        mapViewer.add(swingWaypoint.getButton());
                    }
                }

                mapViewer.updateUI();
            } else if (arg instanceof LocationStatistic) {
                LocationStatistic locationStatistic = (LocationStatistic) arg;

                if (selectedLocation != null && selectedLocation.getButtonsGroupId() == locationStatistic.getId()) {
                    peopleCount.setText(String.valueOf(locationStatistic.getPeopleCount()));
                    placesList.setListData(locationStatistic.getNames().stream().collect(Collectors.toCollection(Vector::new)));
                    hashTagsList.setListData(locationStatistic.getHashTags().stream().collect(Collectors.toCollection(Vector::new)));
                    fullInfoStatus.setText("");
                }
            }
        }
    }
}