
import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class StationA_PI_Interlocking {

    // ===== Subsystem Classes =====
    static class Section {
        String name;
        char setting = 'F'; // F=Not Set, S=Set

        Section(String name) { this.name = name; }

        void toggleSetting(StringBuilder log) {
            setting = (setting == 'F') ? 'S' : 'F';
            logOperation(log, name + " setting changed to " + setting);
        }
    }

    static class Crossover {
        String name;
        char position = 'N'; // N=Normal, R=Reverse
        char setting = 'F';  // F=Free, S=Set

        Crossover(String name) { this.name = name; }

        void togglePosition(StringBuilder log) {
            position = (position == 'N') ? 'R' : 'N';
            logOperation(log, name + " position changed to " + position);
        }

        void toggleSetting(StringBuilder log) {
            setting = (setting == 'F') ? 'S' : 'F';
            logOperation(log, name + " setting changed to " + setting);
        }
    }

    static class AxleCounterSection {
        String name;
        int inCount = 0, outCount = 0;
        boolean outBeforeIn = false;
        Section section;
        JLabel inLabel, outLabel;

        AxleCounterSection(String name, Section section) {
            this.name = name;
            this.section = section;
            inLabel = new JLabel("IN: 0");
            outLabel = new JLabel("OUT: 0");
        }

        void in(StringBuilder log) {
            if (section.setting != 'S') showSPAD(log, "IN");
            if (outBeforeIn) showWarning(log, "W.D.T.F.", "IN");
            inCount++;
            inLabel.setText("IN: " + inCount);
            logOperation(log, name + " IN count: " + inCount);
        }

        void out(StringBuilder log) {
            if (section.setting != 'S') showSPAD(log, "OUT");
            if (inCount == 0) outBeforeIn = true;
            outCount++;
            outLabel.setText("OUT: " + outCount);
            logOperation(log, name + " OUT count: " + outCount);
            if (outBeforeIn) showWarning(log, "W.D.T.F.", "OUT");
        }

        void reset(StringBuilder log) {
            StringBuilder warnings = new StringBuilder();
            if (inCount != outCount) warnings.append("T.P. ");
            if (outBeforeIn) warnings.append("W.D.T.F. ");
            if (section.setting != 'S') warnings.append("S.P.A.D. ");
            if (warnings.length() > 0) showWarning(log, warnings.toString(), "RESET");
            inCount = 0; outCount = 0; outBeforeIn = false;
            inLabel.setText("IN: 0");
            outLabel.setText("OUT: 0");
            logOperation(log, name + " counters RESET");
        }

        void showWarning(StringBuilder log, String msg, String action) {
            JOptionPane.showMessageDialog(null, "WARNING [" + action + "]: " + msg, "ALERT", JOptionPane.WARNING_MESSAGE);
            logOperation(log, name + " " + action + " WARNING: " + msg);
        }

        void showSPAD(StringBuilder log, String action) {
            showWarning(log, "S.P.A.D. (Section not set)", action);
        }
    }

    // ===== Logging =====
    static void logOperation(StringBuilder log, String message) {
        String time = LocalDateTime.now().toString();
        String entry = "[" + time + "] " + message;
        System.out.println(entry);
        log.append(entry).append("\n");

        try (FileWriter fw = new FileWriter("StationA_Log.txt", true)) {
            fw.write(entry + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ===== Signal GUI Panel =====
    public static class SignalPanel extends JPanel {
        private boolean homeRed = true, homeYellow = false, homeGreen = false;
        private boolean[] homeFeather = new boolean[5]; // 5 white bulbs
        private boolean mlRed = true, mlYellow = false, mlGreen = false;
        private boolean llRed = true, llYellow = false;
        private boolean adRed = true, adGreen = false;

        // ===== Home Signal Setters =====
        public void setHomeRed(boolean val) { homeRed = val; repaint(); }
        public void setHomeYellow(boolean val) { homeYellow = val; repaint(); }
        public void setHomeGreen(boolean val) { homeGreen = val; repaint(); }
        public void setHomeFeather(int idx, boolean val) { homeFeather[idx] = val; repaint(); }
        public void activateAllHomeFeathers(boolean val) { for(int i=0;i<5;i++) homeFeather[i]=val; repaint(); }

        // ===== Other Signals =====
        public void setMLRed(boolean val) { mlRed = val; repaint(); }
        public void setMLYellow(boolean val) { mlYellow = val; repaint(); }
        public void setMLGreen(boolean val) { mlGreen = val; repaint(); }

        public void setLLRed(boolean val) { llRed = val; repaint(); }
        public void setLLYellow(boolean val) { llYellow = val; repaint(); }

        public void setAdRed(boolean val) { adRed = val; repaint(); }
        public void setAdGreen(boolean val) { adGreen = val; repaint(); }

        public boolean getMLRed() { return mlRed; }
        public boolean getLLRed() { return llRed; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0,0,getWidth(),getHeight());

            int radius = 20;
            int baseY = 200; // red aspect as baseline (lowest)
            int spacing = 30;

            // ---- HOME SIGNAL ----
            int homeX = 50;
            // Red (baseline)
            g2.setColor(homeRed ? Color.RED : Color.BLACK);
            g2.fillOval(homeX, baseY, radius, radius);
            // Yellow
            g2.setColor(homeYellow ? Color.YELLOW : Color.BLACK);
            g2.fillOval(homeX, baseY - spacing, radius, radius);
            // Green
            g2.setColor(homeGreen ? Color.GREEN : Color.BLACK);
            g2.fillOval(homeX, baseY - 2*spacing, radius, radius);
            // Feathers (rising staircase above green)
            for(int i=0;i<5;i++){
                g2.setColor(homeFeather[i]?Color.WHITE:Color.BLACK);
                g2.fillOval(homeX + i*25, baseY - 2*spacing - (i+1)*20, radius, radius);
            }
            g2.setColor(Color.WHITE); g2.drawString("HOME", homeX, baseY + 40);

            // ---- ML STARTER ----
            int mlX = homeX + 150;
            g2.setColor(mlRed?Color.RED:Color.BLACK); g2.fillOval(mlX, baseY, radius, radius);
            g2.setColor(mlYellow?Color.YELLOW:Color.BLACK); g2.fillOval(mlX, baseY - spacing, radius, radius);
            g2.setColor(mlGreen?Color.GREEN:Color.BLACK); g2.fillOval(mlX, baseY - 2*spacing, radius, radius);
            g2.setColor(Color.WHITE); g2.drawString("ML_STARTER", mlX, baseY + 40);

            // ---- LL STARTER ----
            int llX = mlX + 150;
            g2.setColor(llRed?Color.RED:Color.BLACK); g2.fillOval(llX, baseY, radius, radius);
            g2.setColor(llYellow?Color.YELLOW:Color.BLACK); g2.fillOval(llX, baseY - spacing, radius, radius);
            g2.setColor(Color.WHITE); g2.drawString("LL_STARTER", llX, baseY + 40);

            // ---- ADVANCE STARTER ----
            int adX = llX + 150;
            g2.setColor(adRed?Color.RED:Color.BLACK); g2.fillOval(adX, baseY, radius, radius);
            g2.setColor(adGreen?Color.GREEN:Color.BLACK); g2.fillOval(adX, baseY - spacing, radius, radius);
            g2.setColor(Color.WHITE); g2.drawString("Ad_STARTER", adX, baseY + 40);
        }
    }

    // ===== Main GUI =====
    public static void main(String[] args) {
        JFrame frame = new JFrame("Station A PI Interlocking");
        frame.setSize(1500,750);
        frame.setLayout(new BorderLayout());

        StringBuilder log = new StringBuilder();

        // Sections
        Section ml_hsf = new Section("ML_HSF");
        Section ll_hsf = new Section("LL_HSF");
        Section ml_ssf = new Section("ML_SSF");
        Section ml_ads = new Section("ML_Ad_S");
        Section[] sections = {ml_hsf, ll_hsf, ml_ssf, ml_ads};

        // Crossovers
        Crossover[] crossovers = { new Crossover("SC_HSF"), new Crossover("SC_SSF"), new Crossover("SC_LL") };

        // Axle Counters
        AxleCounterSection[] axles = {
            new AxleCounterSection("ML_HSF", ml_hsf),
            new AxleCounterSection("LL_HSF", ll_hsf),
            new AxleCounterSection("ML_SSF", ml_ssf),
            new AxleCounterSection("ML_Ad_S", ml_ads),
            new AxleCounterSection("SC_HSF", ml_hsf),
            new AxleCounterSection("SC_SSF", ml_ssf),
            new AxleCounterSection("SC_LL", ll_hsf)
        };

        // Signal Panel
        SignalPanel signalPanel = new SignalPanel();
        signalPanel.setPreferredSize(new Dimension(700,400));
        frame.add(signalPanel, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(6,1,5,5));

        // Sections Buttons
        JPanel sectionPanel = new JPanel(); sectionPanel.setBorder(BorderFactory.createTitledBorder("Sections"));
        sectionPanel.setLayout(new GridLayout(sections.length,1,5,5));
        for(Section s: sections){
            JButton b = new JButton("Toggle "+s.name);
            b.addActionListener(e -> s.toggleSetting(log));
            sectionPanel.add(b);
        }

        // Crossovers Buttons
        JPanel crossPanel = new JPanel(); crossPanel.setBorder(BorderFactory.createTitledBorder("Crossovers"));
        crossPanel.setLayout(new GridLayout(crossovers.length,2,5,5));
        for(Crossover c: crossovers){
            JButton pos = new JButton("Pos "+c.name); pos.addActionListener(e -> c.togglePosition(log));
            JButton set = new JButton("Set "+c.name); set.addActionListener(e -> c.toggleSetting(log));
            crossPanel.add(pos); crossPanel.add(set);
        }

        // Axle counters panel
        JPanel axlePanel = new JPanel(); axlePanel.setBorder(BorderFactory.createTitledBorder("Axle Counters"));
        axlePanel.setLayout(new GridLayout(axles.length,4,5,5));
        for(AxleCounterSection ac: axles){
            JButton in = new JButton("IN"); in.addActionListener(e->ac.in(log));
            JButton out = new JButton("OUT"); out.addActionListener(e->ac.out(log));
            JButton reset = new JButton("RESET"); reset.addActionListener(e->ac.reset(log));
            axlePanel.add(new JLabel(ac.name));
            axlePanel.add(ac.inLabel); axlePanel.add(ac.outLabel);
            JPanel btns = new JPanel(); btns.add(in); btns.add(out); btns.add(reset);
            axlePanel.add(btns);
        }

        // Signal buttons
        JPanel sigPanel = new JPanel(); sigPanel.setLayout(new GridLayout(4,1,5,5));

        // HOME
        JPanel homeP = new JPanel(); homeP.setBorder(BorderFactory.createTitledBorder("HOME"));
        JButton hR = new JButton("RED"); hR.addActionListener(e->{signalPanel.setHomeRed(true); signalPanel.setHomeYellow(false); signalPanel.setHomeGreen(false); signalPanel.activateAllHomeFeathers(false); logOperation(log,"Home RED");});
        JButton hY = new JButton("YELLOW"); hY.addActionListener(e->{signalPanel.setHomeRed(false); signalPanel.setHomeYellow(true); signalPanel.setHomeGreen(false); signalPanel.activateAllHomeFeathers(false); logOperation(log,"Home YELLOW");});
        JButton hG = new JButton("GREEN"); hG.addActionListener(e->{signalPanel.setHomeRed(false); signalPanel.setHomeYellow(false); signalPanel.setHomeGreen(true); signalPanel.activateAllHomeFeathers(false); logOperation(log,"Home GREEN");});
        JButton hF = new JButton("FEATHER"); hF.addActionListener(e->{signalPanel.activateAllHomeFeathers(true); signalPanel.setHomeRed(false); signalPanel.setHomeYellow(true); signalPanel.setHomeGreen(false); logOperation(log,"Home FEATHER activated");});
        homeP.add(hR); homeP.add(hY); homeP.add(hG); homeP.add(hF);

        // ML Starter
        JPanel mlP = new JPanel(); mlP.setBorder(BorderFactory.createTitledBorder("ML_STARTER"));
        JButton mlR = new JButton("RED"); mlR.addActionListener(e->{signalPanel.setMLRed(true); signalPanel.setMLYellow(false); signalPanel.setMLGreen(false); logOperation(log,"ML RED");});
        JButton mlY = new JButton("YELLOW"); mlY.addActionListener(e->{ if(!signalPanel.getLLRed()){ JOptionPane.showMessageDialog(null,"ML & LL cannot both be non-RED"); return;} signalPanel.setMLRed(false); signalPanel.setMLYellow(true); signalPanel.setMLGreen(false); logOperation(log,"ML YELLOW");});
        JButton mlG = new JButton("GREEN"); mlG.addActionListener(e->{ if(!signalPanel.getLLRed()){ JOptionPane.showMessageDialog(null,"ML & LL cannot both be non-RED"); return;} signalPanel.setMLRed(false); signalPanel.setMLYellow(false); signalPanel.setMLGreen(true); logOperation(log,"ML GREEN");});
        mlP.add(mlR); mlP.add(mlY); mlP.add(mlG);

        // LL Starter
        JPanel llP = new JPanel(); llP.setBorder(BorderFactory.createTitledBorder("LL_STARTER"));
        JButton llR = new JButton("RED"); llR.addActionListener(e->{signalPanel.setLLRed(true); signalPanel.setLLYellow(false); logOperation(log,"LL RED");});
        JButton llY = new JButton("YELLOW"); llY.addActionListener(e->{ if(!signalPanel.getMLRed()){ JOptionPane.showMessageDialog(null,"ML & LL cannot both be non-RED"); return;} signalPanel.setLLRed(false); signalPanel.setLLYellow(true); logOperation(log,"LL YELLOW");});
        llP.add(llR); llP.add(llY);

        // AD Starter
        JPanel adP = new JPanel(); adP.setBorder(BorderFactory.createTitledBorder("Ad_STARTER"));
        JButton adR = new JButton("RED"); adR.addActionListener(e->{signalPanel.setAdRed(true); signalPanel.setAdGreen(false); logOperation(log,"Ad RED");});
        JButton adG = new JButton("GREEN"); adG.addActionListener(e->{signalPanel.setAdRed(false); signalPanel.setAdGreen(true); logOperation(log,"Ad GREEN");});
        adP.add(adR); adP.add(adG);

        sigPanel.add(homeP); sigPanel.add(mlP); sigPanel.add(llP); sigPanel.add(adP);

        // Add all to main control
        controlPanel.add(sectionPanel); controlPanel.add(crossPanel);
        controlPanel.add(axlePanel); controlPanel.add(sigPanel);

        frame.add(new JScrollPane(controlPanel), BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
