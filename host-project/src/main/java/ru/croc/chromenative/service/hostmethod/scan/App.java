package ru.croc.chromenative.service.hostmethod.scan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;

import ru.croc.chromenative.service.LogService;
import ru.croc.chromenative.service.hostmethod.scan.actions.AcquireAndUploadImageAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.AcquireImageAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.CropImageAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.RemoveAllAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.RemoveImageAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.RotateImageAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.SaveImageAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.SelectScannerAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.UploadImagesAction;
import ru.croc.chromenative.service.hostmethod.scan.actions.ZoomImageAction;
import ru.sggr.scan.listeners.RatioChangeListener;
import ru.sggr.scan.panels.ControlsPanel;
import ru.sggr.scan.panels.CroppableImagePanel;
import ru.sggr.scan.panels.ImagePanel;

/**
 * Форма приложения сканирования
 * @since  26.01.12 Time: 12:34
 */
public class App implements RatioChangeListener {

    public static final Color DARK_BG = new Color(222, 236, 253);// new Color(141,178,227);

    public static final Color DEFAULT_BG = Color.white;// new Color(222,236,253);

    public static final Dimension SPACER_DIMENSION = new Dimension(10, 10);

    public static final int PERCENT_100 = 100;

    protected ImagePanel activeImagePanel;

    private ArrayList<String> scanners = new ArrayList<String>();

    private String scannerId;

    private boolean noscanner = false;

    private boolean useDefaults = false;

    private List<Image> images = new ArrayList<Image>();

    private Image selected;

    private ImagePanel selectedThumbnail;

    private List<Component> selectDependedComponents = new LinkedList<Component>();

    private JPanel pMain;

    private JPanel pCenter;

    private JPanel pSouth;

    private JPanel pImage;

    private JCheckBox cbUseDefaults;

    private JButton bSelectScanner;

    private JButton bScan;

    private JButton bScanAndUpload;

    private JPanel pThumbnails;

    private JLabel tStatus;

    // zoom buttons
    private JButton bZoomIn;

    private JButton bZoomOut;

    private JButton bZoomReset;

    private JLabel tZoomInfo;

    public App() {
        initializeComponents();
    }

    private void initializeComponents() {

        pMain = new JPanel();
        pMain.setLayout(new BorderLayout(0, 0));

        pSouth = new JPanel();
        pSouth.setLayout(new BorderLayout());
        pMain.add(pSouth, BorderLayout.SOUTH);

        pCenter = new JPanel();

        pCenter.setLayout(new BorderLayout());
        pCenter.setBackground(Color.BLACK);
        pMain.add(pCenter, BorderLayout.CENTER);

        pImage = new JPanel();
        pImage.setLayout(new BorderLayout());
        pImage.setBackground(DEFAULT_BG);
        final int minimumWidth = 850;
        final int minimumHeight = 400;
        pImage.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
        pImage.setPreferredSize(new Dimension(minimumWidth, minimumHeight));
        pCenter.add(pImage, BorderLayout.CENTER);

        initializeThumbnails();

        initializeStatusBar();

        initializeControls();

        updateStatus("Сканер не выбран");
    }

    protected void initializeThumbnails() {
        pThumbnails = new JPanel();
        pThumbnails.setLayout(new FlowLayout(FlowLayout.CENTER));
        pThumbnails.setBackground(DEFAULT_BG);
        final int preferredHeight = 220;
        pThumbnails.setPreferredSize(new Dimension(1, preferredHeight));

        final JScrollPane scrollPane = new JScrollPane(pThumbnails,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        final int borderThickness  = 4;
        scrollPane.setBorder(new LineBorder(DARK_BG, borderThickness));

        pSouth.add(scrollPane, BorderLayout.CENTER);
    }

    protected void initializeStatusBar() {
        tStatus = new JLabel();

        tZoomInfo = new JLabel("100%");

        pSouth.add(new JToolBar() {

            {
                setFloatable(false);
                add(tStatus);
                add(Box.createHorizontalGlue());
                add(tZoomInfo);
            }
        }, BorderLayout.SOUTH);
    }

    protected void initializeControls() {

        ControlsPanel pControls = new ControlsPanel(ControlsPanel.ORIENTATION.VERTICAL);
        final int preferredWidth = 145;
        pControls.setPreferredSize(new Dimension(preferredWidth, 1));

        bSelectScanner = new JButton(Actions.selectScannerAction);
        bSelectScanner.setText("Выбрать сканер");
        pControls.add(bSelectScanner);

        /**
         * Умолчания
         */
        cbUseDefaults = new JCheckBox();
        cbUseDefaults.setAction(new AbstractAction() {

            public void actionPerformed(final ActionEvent e) {
                useDefaults = cbUseDefaults.isSelected();
            }
        });
        cbUseDefaults.setText("<html>Использовать<br> настройки<br> по умолчанию</html>");
        cbUseDefaults.doClick();
        pControls.add(cbUseDefaults);

        /**
         * Кнопки
         */
        bScanAndUpload = new JButton(Actions.acquireUploadImageAction);
        bScanAndUpload.setEnabled(false);
        bScanAndUpload.setText("Сканировать");
        bScanAndUpload.setToolTipText("Нажмите кнопку «Сканировать», чтобы начать сканирование документа");
        pControls.add(bScanAndUpload);

        bScan = new JButton(Actions.acquireImageAction);
        bScan.setEnabled(false);
        bScan.setText("Предпросмотр");
        bScan.setToolTipText("Нажмите кнопку «Предпросмотр», чтобы начать предварительное сканирование."
                + " Используйте режим для документов объемом не более 10 страниц");
        pControls.add(bScan);

        // spacer
        pControls.add(Box.createRigidArea(SPACER_DIMENSION));
        /**
         * Действия над изображениями
         */
        JButton bRotateACW = new JButton(Actions.rotateImageActionACW);
        bRotateACW.setText("Повернуть влево");
        bRotateACW.setEnabled(false);
        selectDependedComponents.add(bRotateACW);
        pControls.add(bRotateACW);

        JButton bRotateCW = new JButton(Actions.rotateImageActionCW);
        bRotateCW.setText("Повернуть вправо");
        bRotateCW.setEnabled(false);
        selectDependedComponents.add(bRotateCW);
        pControls.add(bRotateCW);

        // zooooom
        ControlsPanel zoom = new ControlsPanel(ControlsPanel.ORIENTATION.HORIZONTAL);

        bZoomOut = new JButton(Actions.zoomOutImageAction);
        bZoomOut.setText("-");
        bZoomOut.setEnabled(false);
        selectDependedComponents.add(bZoomOut);
        zoom.add(bZoomOut);

        bZoomReset = new JButton(Actions.zoomResetImageAction);
        bZoomReset.setText("100%");
        bZoomReset.setEnabled(false);
        selectDependedComponents.add(bZoomReset);
        zoom.add(bZoomReset);

        bZoomIn = new JButton(Actions.zoomInImageAction);
        bZoomIn.setText("+");
        bZoomIn.setEnabled(false);
        selectDependedComponents.add(bZoomIn);
        zoom.add(bZoomIn);

        pControls.add(zoom);

        // spacer
        pControls.add(Box.createRigidArea(SPACER_DIMENSION));

        JButton bCrop = new JButton(Actions.cropImageAction);
        bCrop.setText("Вырезать");
        bCrop.setEnabled(false);
        selectDependedComponents.add(bCrop);
        pControls.add(bCrop);

        // spacer
        pControls.add(Box.createRigidArea(SPACER_DIMENSION));

        JButton bRemoveImage = new JButton(Actions.removeImageAction);
        bRemoveImage.setText("Удалить");
        bRemoveImage.setEnabled(false);
        selectDependedComponents.add(bRemoveImage);

        pControls.add(bRemoveImage);

        JButton bRemoveAll = new JButton(Actions.removeAllAction);
        bRemoveAll.setText("Удалить все");
        bRemoveAll.setEnabled(false);
        selectDependedComponents.add(bRemoveAll);
        pControls.add(bRemoveAll);

        /*
         * JButton bSave = new JButton(Actions.saveImageAction); bSave.setText("Сохрнаить"); bSave.setEnabled(false);
         * selectDependedComponents.add(bSave); pTop.add(bSave);
         */
        // JPanel jPanel = new JPanel(new BorderLayout());

        pMain.add(pControls, BorderLayout.EAST);
    }

    public boolean hasImages() {
        return images.size() > 0;
    }

    public boolean hasSelected() {
        return selectedThumbnail != null;
    }

    public boolean hasScanner() {
        return scannerId !=null;
    }

    public boolean isNoScanner() {
        return noscanner;
    }

    public void setNoScanner(final boolean noscanner) {
        this.noscanner = noscanner;
        bSelectScanner.setEnabled(!noscanner);
        if (noscanner) {
            bSelectScanner.setText("Сканер не найден");
        }
    }

    public synchronized int remove(final Image image) {
        int pos = images.indexOf(image);
        if (pos != -1) {

            select(null);

            images.remove(image);

            ImagePanel p = getThumbnail(image);
            if (p != null) {
                pThumbnails.remove(p);
                pThumbnails.repaint();
            }
            pCenter.validate();
            pCenter.repaint();
        }
        return pos;
    }

    public synchronized void removeAll() {

        select(null);

        images.clear();

        pThumbnails.removeAll();
        pThumbnails.validate();
        pThumbnails.repaint();

        pCenter.validate();
        pCenter.repaint();
    }

    public void updateStatus(final String statusText) {
        info(new Date() + " : " + statusText);
        tStatus.setText(statusText);
    }

    public JPanel getRootPanel() {
        return pMain;
    }

    public boolean isUseDefaults() {
        return useDefaults;
    }

    public List<Image> getImages() {
        Component[] components = pThumbnails.getComponents();
        List<Image> images = new ArrayList<Image>(components.length);
        for (Component component : components) {
            if (component instanceof ImagePanel) {
                images.add(((ImagePanel) component).getImage());
            }
        }
        return images;
    }

    public String getScannerId() {
        return scannerId;
    }

    /**
     * @param scannerId
     *            Порядковый номер сканера в списке TwainManager.listSources()
     */
    public void setScannerId(final String scannerId) {
        this.scannerId = scannerId;

        bScan.setEnabled(this.scannerId != null);
        bScanAndUpload.setEnabled(bScan.isEnabled());

        if (this.scannerId == null) {
            updateStatus("Сканер не выбран");
            bSelectScanner.setText("Выбрать сканер");
        } else {
            updateStatus("Выбран сканер #" + this.scannerId);
            bSelectScanner.setText("Сменить сканер");
        }
    }

    /**
     * Полуает выдеоенное в текущий момент изображение
     *
     * @return выделенное изображение
     */
    public Image getSelected() {
        return selected;
    }

    public ImagePanel getImagePanel() {
        return activeImagePanel;
    }

    /**
     * Выделить изображение
     *
     * @param image
     *            изображение, которое выбрал пользователь
     */
    public void select(final Image image) {

        selected = image;

        if (selectedThumbnail != null) {
            selectedThumbnail.setBackground(pThumbnails.getBackground());
        }

        pImage.removeAll();

        if (image != null) {
            selectedThumbnail = getThumbnail(image);
            selectedThumbnail.setBackground(DARK_BG);

            activeImagePanel = new CroppableImagePanel(image, false);
            activeImagePanel.setBackground(pImage.getBackground());
            activeImagePanel.addListener(this);
            tZoomInfo.setText("100%");

            JScrollPane centerImageScrollPane = new JScrollPane(activeImagePanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            pImage.add(centerImageScrollPane, BorderLayout.CENTER);
        }

        for (Component c : selectDependedComponents) {
            c.setEnabled(image != null);
        }

        pCenter.validate();
        pImage.validate();
    }

    /**
     * Добавляет изображения, выделяет его после добавления
     *
     * @param image
     *            - изображение, которые необходимо добавить
     */
    public synchronized void add(final Image image) {
        add(image, this.images.size());
    }

    /**
     * Добавляет изображения, выделяет его после добавления
     *
     * @param image
     *            - изображение, которые необходимо добавить
     * @param pos
     *            - позиция на панели с миниатюрами в которую нужно добавить изображения
     */
    public synchronized void add(final Image image, final int pos) {
        // Можно тупо ренлдер общий сделать, чтобы проще жилось...
        this.images.add(pos, image);

        ImagePanel thumbnail = new ImagePanel(image);
        thumbnail.setBackground(pThumbnails.getBackground());
        final int preferredWidth = 150;
        final int preferredHeight = (int) (150 * Math.sqrt(2));
        thumbnail.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        thumbnail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        thumbnail.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                select(image);
            }
        });

        pThumbnails.add(thumbnail, pos);
        pCenter.validate();
        pThumbnails.validate();

        select(image);

    }

    /**
     * Получить панель миниатюры по изображению
     *
     * @param image
     *            изображение
     * @return панель миниатюры
     */
    public ImagePanel getThumbnail(final Image image) {
        for (Component component : pThumbnails.getComponents()) {
            if (component instanceof ImagePanel && ((ImagePanel) component).getImage().equals(image)) {
                return (ImagePanel) component;
            }
        }
        return null;
    }

    public void validateImage() {
        pImage.validate();
    }

    public void change(final double oldValue, final double newValue) {
        tZoomInfo.setText(Math.round(newValue * PERCENT_100) + "%");
        bZoomIn.setEnabled(newValue != ImagePanel.MAX_RATIO);
        bZoomOut.setEnabled(newValue != ImagePanel.MIN_RATIO);
    }

    public ArrayList<String> getSystemScanners() {
        return scanners;
    }

    public static class Actions {
        //прямой угол
        private static final int RIGHT_ANGLE = 90;

        public static SelectScannerAction selectScannerAction = new SelectScannerAction();

        public static AcquireImageAction acquireImageAction = new AcquireImageAction();

        public static AcquireAndUploadImageAction acquireUploadImageAction = new AcquireAndUploadImageAction();

        public static UploadImagesAction uploadImagesAction = new UploadImagesAction();

        public static RotateImageAction rotateImageActionCW = new RotateImageAction(RIGHT_ANGLE);

        public static RotateImageAction rotateImageActionACW = new RotateImageAction(-RIGHT_ANGLE);

        public static RemoveImageAction removeImageAction = new RemoveImageAction();

        public static RemoveAllAction removeAllAction = new RemoveAllAction();

        public static SaveImageAction saveImageAction = new SaveImageAction();

        public static CropImageAction cropImageAction = new CropImageAction();

        public static ZoomImageAction zoomInImageAction = new ZoomImageAction(ZoomImageAction.DIRECTION.IN);

        public static ZoomImageAction zoomOutImageAction = new ZoomImageAction(ZoomImageAction.DIRECTION.OUT);

        public static ZoomImageAction zoomResetImageAction = new ZoomImageAction(ZoomImageAction.DIRECTION.ASIS);
    }

    /**
     * Вывод в лог информационного сообщения.
     *
     * @param msg
     */
    private static void info(final String msg) {
        LogService.getInstance().info(msg);
    }

    /**
     * Вывод в лог сообщения об ошибке.
     *
     * @param e
     */
    private static void error(final Throwable e) {
        LogService.getInstance().error(e);
    }
}