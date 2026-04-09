
import javax.swing.*;
import java.awt.*;

public class SignalGUI {

    public static class SignalPanel extends JPanel {

        // ===== Signal states =====
        private boolean homeRed = true, homeYellow = false, homeGreen = false;
        private boolean mlRed = true, mlYellow = false, mlGreen = false;
        private boolean llRed = true, llYellow = false;
        private boolean adRed = true, adGreen = false;

        // Home route feather bulbs (above green)
        private boolean[] homeFeathers = new boolean[5];

        public SignalPanel() {
            setPreferredSize(new Dimension(700, 300));
        }

        // ===== Setters =====
        public void setHomeRed(boolean val) { homeRed = val; repaint(); }
        public void setHomeYellow(boolean val) { homeYellow = val; repaint(); }
        public void setHomeGreen(boolean val) { homeGreen = val; repaint(); }
        public void setHomeFeather(int index, boolean val) { homeFeathers[index] = val; repaint(); }
        public void activateAllHomeFeathers(boolean state) { for(int i=0;i<homeFeathers.length;i++) homeFeathers[i]=state; repaint(); }

        public void setMLRed(boolean val) { mlRed = val; repaint(); }
        public void setMLYellow(boolean val) { mlYellow = val; repaint(); }
        public void setMLGreen(boolean val) { mlGreen = val; repaint(); }

        public void setLLRed(boolean val) { llRed = val; repaint(); }
        public void setLLYellow(boolean val) { llYellow = val; repaint(); }

        public void setAdRed(boolean val) { adRed = val; repaint(); }
        public void setAdGreen(boolean val) { adGreen = val; repaint(); }

        // ===== Getters =====
        public boolean getMLRed() { return mlRed; }
        public boolean getLLRed() { return llRed; }

        // ===== Paint the signals =====
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Background
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // --- Draw Home signal ---
            int homeX = 50, homeY = 200;
            drawSignalBulb(g2, homeX, homeY, homeRed ? Color.RED : Color.DARK_GRAY);
            drawSignalBulb(g2, homeX, homeY - 30, homeYellow ? Color.YELLOW : Color.DARK_GRAY);
            drawSignalBulb(g2, homeX, homeY - 60, homeGreen ? Color.GREEN : Color.DARK_GRAY);

            // Draw 5 home feathers ABOVE green in rising staircase
            for (int i = 0; i < 5; i++) {
                int x = homeX + i * 25;
                int y = homeY - 90 - i * 15; // rising staircase above green
                drawSignalBulb(g2, x, y, homeFeathers[i] ? Color.WHITE : Color.DARK_GRAY);
            }

            // --- Draw Mainline Starter ---
            int mlX = 300, mlY = 200;
            drawSignalBulb(g2, mlX, mlY, mlRed ? Color.RED : Color.DARK_GRAY);
            drawSignalBulb(g2, mlX, mlY - 30, mlYellow ? Color.YELLOW : Color.DARK_GRAY);
            drawSignalBulb(g2, mlX, mlY - 60, mlGreen ? Color.GREEN : Color.DARK_GRAY);

            // --- Draw Loopline Starter ---
            int llX = 450, llY = 200;
            drawSignalBulb(g2, llX, llY, llRed ? Color.RED : Color.DARK_GRAY);
            drawSignalBulb(g2, llX, llY - 30, llYellow ? Color.YELLOW : Color.DARK_GRAY);

            // --- Draw Advance Starter ---
            int adX = 600, adY = 200;
            drawSignalBulb(g2, adX, adY, adRed ? Color.RED : Color.DARK_GRAY);
            drawSignalBulb(g2, adX, adY - 30, adGreen ? Color.GREEN : Color.DARK_GRAY);
        }

        private void drawSignalBulb(Graphics2D g2, int x, int y, Color c) {
            g2.setColor(c);
            g2.fillOval(x, y, 20, 20);
            g2.setColor(Color.WHITE);
            g2.drawOval(x, y, 20, 20);
        }

        // ===== Control Panel with Buttons Below GUI =====
        public static JPanel createControlPanel(SignalPanel panel, StringBuilder log) {
            JPanel control = new JPanel();
            control.setLayout(new GridLayout(4,1,5,5));

            // --- HOME SIGNAL BUTTONS ---
            JPanel homeBtns = new JPanel();
            homeBtns.setBorder(BorderFactory.createTitledBorder("HOME"));
            JButton homeRed = new JButton("RED"); homeRed.addActionListener(e -> { panel.setHomeRed(true); panel.setHomeYellow(false); panel.setHomeGreen(false); panel.activateAllHomeFeathers(false); log.append("Home RED\n"); });
            JButton homeYellow = new JButton("YELLOW"); homeYellow.addActionListener(e -> { panel.setHomeRed(false); panel.setHomeYellow(true); panel.setHomeGreen(false); panel.activateAllHomeFeathers(false); log.append("Home YELLOW\n"); });
            JButton homeGreen = new JButton("GREEN"); homeGreen.addActionListener(e -> { panel.setHomeRed(false); panel.setHomeYellow(false); panel.setHomeGreen(true); panel.activateAllHomeFeathers(false); log.append("Home GREEN\n"); });
            JButton homeFeather = new JButton("FEATHER"); homeFeather.addActionListener(e -> { panel.activateAllHomeFeathers(true); panel.setHomeRed(false); panel.setHomeYellow(true); panel.setHomeGreen(false); log.append("Home FEATHER activated\n"); });
            homeBtns.add(homeRed); homeBtns.add(homeYellow); homeBtns.add(homeGreen); homeBtns.add(homeFeather);

            // --- ML STARTER BUTTONS ---
            JPanel mlBtns = new JPanel();
            mlBtns.setBorder(BorderFactory.createTitledBorder("ML_STARTER"));
            JButton mlRed = new JButton("RED"); mlRed.addActionListener(e -> { panel.setMLRed(true); panel.setMLYellow(false); panel.setMLGreen(false); });
            JButton mlYellow = new JButton("YELLOW"); mlYellow.addActionListener(e -> { if(!panel.getLLRed()) { JOptionPane.showMessageDialog(null,"ML & LL cannot both be non-RED"); return; } panel.setMLRed(false); panel.setMLYellow(true); panel.setMLGreen(false); });
            JButton mlGreen = new JButton("GREEN"); mlGreen.addActionListener(e -> { if(!panel.getLLRed()) { JOptionPane.showMessageDialog(null,"ML & LL cannot both be non-RED"); return; } panel.setMLRed(false); panel.setMLYellow(false); panel.setMLGreen(true); });
            mlBtns.add(mlRed); mlBtns.add(mlYellow); mlBtns.add(mlGreen);

            // --- LL STARTER BUTTONS ---
            JPanel llBtns = new JPanel();
            llBtns.setBorder(BorderFactory.createTitledBorder("LL_STARTER"));
            JButton llRed = new JButton("RED"); llRed.addActionListener(e -> { panel.setLLRed(true); panel.setLLYellow(false); });
            JButton llYellow = new JButton("YELLOW"); llYellow.addActionListener(e -> { if(!panel.getMLRed()) { JOptionPane.showMessageDialog(null,"ML & LL cannot both be non-RED"); return; } panel.setLLRed(false); panel.setLLYellow(true); });
            llBtns.add(llRed); llBtns.add(llYellow);

            // --- AD STARTER BUTTONS ---
            JPanel adBtns = new JPanel();
            adBtns.setBorder(BorderFactory.createTitledBorder("Ad_STARTER"));
            JButton adRed = new JButton("RED"); adRed.addActionListener(e -> { panel.setAdRed(true); panel.setAdGreen(false); });
            JButton adGreen = new JButton("GREEN"); adGreen.addActionListener(e -> { panel.setAdRed(false); panel.setAdGreen(true); });
            adBtns.add(adRed); adBtns.add(adGreen);

            // Add all panels
            control.add(homeBtns); control.add(mlBtns); control.add(llBtns); control.add(adBtns);

            return control;
        }
    }
}
