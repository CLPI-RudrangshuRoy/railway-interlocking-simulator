
import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class StationA_EI_Interlocking {

    // ===== Section and Crossover Classes =====
    static class Section {
        String name; char setting='F'; boolean approachLocked=false;
        Section(String name){this.name=name;}
        void forceSetting(char val,StringBuilder log){setting=val; logOperation(log,name+" forced to "+val);}
        void lockApproach(StringBuilder log){approachLocked=true; logOperation(log,name+" APPROACH LOCKED");}
        void unlockApproach(StringBuilder log){approachLocked=false; logOperation(log,name+" APPROACH UNLOCKED");}
    }

    static class Crossover {
        String name; char position='N', setting='F'; boolean flankProtected=false;
        Crossover(String name){this.name=name;}
        void forceSetting(char val,StringBuilder log){setting=val; logOperation(log,name+" forced to "+val);}
        void enableFlank(StringBuilder log){flankProtected=true; logOperation(log,name+" FLANK PROTECTED");}
        void disableFlank(StringBuilder log){flankProtected=false; logOperation(log,name+" FLANK UNPROTECTED");}
    }

    // ===== Axle Counter =====
    static class AxleCounterSection {
        String name; int inCount=0,outCount=0; boolean outBeforeIn=false; Section section;
        JLabel inLabel,outLabel;

        AxleCounterSection(String name,Section sec){this.name=name; this.section=sec; inLabel=new JLabel("IN: 0"); outLabel=new JLabel("OUT: 0");}
        boolean checkSPAD=false,checkWDTF=false,checkTP=false;

        void in(StringBuilder log, Runnable spadCallback){
            if(section.setting!='S'){checkSPAD=true; spadCallback.run();}
            if(outBeforeIn){checkWDTF=true; spadCallback.run();}
            inCount++; inLabel.setText("IN: "+inCount); logOperation(log,name+" IN count: "+inCount);
        }

        void out(StringBuilder log, Runnable spadCallback){
            if(section.setting!='S'){checkSPAD=true; spadCallback.run();}
            if(inCount==0){checkWDTF=true; spadCallback.run(); outBeforeIn=true;}
            outCount++; outLabel.setText("OUT: "+outCount); logOperation(log,name+" OUT count: "+outCount);
        }

        void reset(StringBuilder log, Runnable spadCallback){
            if(inCount!=outCount){checkTP=true; spadCallback.run();}
            inCount=0; outCount=0; outBeforeIn=false;
            inLabel.setText("IN: 0"); outLabel.setText("OUT: 0");
            logOperation(log,name+" counters RESET");
        }

        void resetFlags(){checkSPAD=false; checkTP=false; checkWDTF=false;}
    }

    // ===== Logging =====
    static void logOperation(StringBuilder log,String msg){
        String time=LocalDateTime.now().toString();
        String entry="["+time+"] "+msg;
        System.out.println(entry); log.append(entry).append("\n");
        try(FileWriter fw=new FileWriter("StationA_EI_Log.txt",true)){fw.write(entry+"\n");} catch(IOException e){e.printStackTrace();}
    }

    // ===== Signal Panel =====
    static class SignalPanel extends JPanel {
        boolean homeRed=true, homeYellow=false, homeGreen=false; boolean[] homeFeather=new boolean[5];
        boolean mlRed=true, mlYellow=false, mlGreen=false;
        boolean llRed=true, llYellow=false;
        boolean adRed=true, adGreen=false;

        public void setHomeRed(boolean val){homeRed=val; homeYellow=false; homeGreen=false; activateAllHomeFeathers(false); repaint();}
        public void setHomeYellow(boolean val){homeRed=false; homeYellow=val; homeGreen=false; repaint();}
        public void setHomeGreen(boolean val){homeRed=false; homeYellow=false; homeGreen=val; repaint();}
        public void activateAllHomeFeathers(boolean val){for(int i=0;i<5;i++) homeFeather[i]=val; if(val) homeYellow=true; repaint();}

        public void setMLRed(boolean val){mlRed=val; mlYellow=false; mlGreen=false; repaint();}
        public void setMLYellow(boolean val){mlRed=false; mlYellow=val; mlGreen=false; repaint();}
        public void setMLGreen(boolean val){mlRed=false; mlYellow=false; mlGreen=val; repaint();}

        public void setLLRed(boolean val){llRed=val; llYellow=false; repaint();}
        public void setLLYellow(boolean val){llRed=false; llYellow=val; repaint();}

        public void setAdRed(boolean val){adRed=val; adGreen=false; repaint();}
        public void setAdGreen(boolean val){adRed=false; adGreen=val; repaint();}

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D) g;
            drawSignal(g2,100,400,"HOME",homeRed,homeYellow,homeGreen,homeFeather);
            drawSignal(g2,250,400,"ML_STARTER",mlRed,mlYellow,mlGreen,null);
            drawSignal(g2,400,400,"LL_STARTER",llRed,llYellow,false,null);
            drawSignal(g2,550,400,"AD_STARTER",adRed,false,adGreen,null);
        }

        private void drawSignal(Graphics2D g2,int x,int y,String name,boolean red,boolean yellow,boolean green,boolean[] feathers){
            int aspectSize=20,gap=10;
            g2.setColor(red?Color.RED:Color.BLACK); g2.fillOval(x,y-aspectSize,aspectSize,aspectSize);
            g2.setColor(yellow?Color.YELLOW:Color.BLACK); g2.fillOval(x,y-2*aspectSize-gap,aspectSize,aspectSize);
            g2.setColor(green?Color.GREEN:Color.BLACK); g2.fillOval(x,y-3*aspectSize-2*gap,aspectSize,aspectSize);
            if(feathers!=null){for(int i=0;i<5;i++){g2.setColor(feathers[i]?Color.WHITE:Color.BLACK); int fx=x+i*15; int fy=y-4*aspectSize-2*gap-i*10; g2.fillOval(fx,fy,10,10);}}
            g2.setColor(Color.BLACK); g2.setFont(new Font("Arial",Font.BOLD,12)); g2.drawString(name,x-25,y+20);
        }
    }

    // ===== Main =====
    public static void main(String[] args){
        JFrame frame=new JFrame("Station A EI Interlocking"); frame.setSize(1600,800); frame.setLayout(new BorderLayout());
        StringBuilder log=new StringBuilder();

        Section ML_HSF=new Section("ML_HSF"); Section LL_HSF=new Section("LL_HSF");
        Section ML_SSF=new Section("ML_SSF"); Section ML_Ad_S=new Section("ML_Ad_S");
        Crossover SC_HSF=new Crossover("SC_HSF"); Crossover SC_SSF=new Crossover("SC_SSF"); Crossover SC_LL=new Crossover("SC_LL");

        AxleCounterSection[] axles={
                new AxleCounterSection("ML_HSF",ML_HSF), new AxleCounterSection("LL_HSF",LL_HSF),
                new AxleCounterSection("ML_SSF",ML_SSF), new AxleCounterSection("ML_Ad_S",ML_Ad_S),
                new AxleCounterSection("SC_HSF",ML_HSF), new AxleCounterSection("SC_SSF",ML_SSF),
                new AxleCounterSection("SC_LL",LL_HSF)
        };

        SignalPanel signalPanel=new SignalPanel(); signalPanel.setPreferredSize(new Dimension(800,400));
        frame.add(signalPanel,BorderLayout.CENTER);

        // ===== RRI Options Panel =====
        JPanel eiPanel=new JPanel(); eiPanel.setLayout(new GridLayout(8,1,5,5));
        eiPanel.setBorder(BorderFactory.createTitledBorder("EI Options"));

        String[] optionNames={
                "Station not clear",
                "Station clear upto mainline platform",
                "Station clear upto loopline platform",
                "Station clear upto advance starter by main line",
                "Station clear upto advance starter by loopline",
                "Station pass through by mainline",
                "Station pass through by loopline"
        };

        for(int i=0;i<7;i++){
            int opt=i+1;
            JButton btn=new JButton(optionNames[i]);
            btn.addActionListener(e->applyEIOption(opt,signalPanel,SC_HSF,SC_SSF,SC_LL,ML_HSF,LL_HSF,ML_Ad_S,log,axles));
            eiPanel.add(btn);
        }

        frame.add(eiPanel,BorderLayout.EAST);

        // ===== Axle Counter Panel =====
        JPanel axlePanel=new JPanel(); axlePanel.setLayout(new GridLayout(axles.length,4,5,5));
        axlePanel.setBorder(BorderFactory.createTitledBorder("Axle Counters"));
        for(AxleCounterSection ac: axles){
            JButton in=new JButton("IN"); in.addActionListener(e->ac.in(log,()->revertOption1(signalPanel,SC_HSF,SC_SSF,SC_LL,ML_HSF,LL_HSF,ML_Ad_S,axles,log)));
            JButton out=new JButton("OUT"); out.addActionListener(e->ac.out(log,()->revertOption1(signalPanel,SC_HSF,SC_SSF,SC_LL,ML_HSF,LL_HSF,ML_Ad_S,axles,log)));
            JButton reset=new JButton("RESET"); reset.addActionListener(e->ac.reset(log,()->revertOption1(signalPanel,SC_HSF,SC_SSF,SC_LL,ML_HSF,LL_HSF,ML_Ad_S,axles,log)));
            axlePanel.add(new JLabel(ac.name)); axlePanel.add(ac.inLabel); axlePanel.add(ac.outLabel);
            JPanel btns=new JPanel(); btns.add(in); btns.add(out); btns.add(reset); axlePanel.add(btns);
        }
        frame.add(axlePanel,BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); frame.setVisible(true);
    }

    static void applyEIOption(int opt,SignalPanel s,Crossover SC_HSF,Crossover SC_SSF,Crossover SC_LL,
                               Section ML_HSF,Section LL_HSF,Section ML_Ad_S,StringBuilder log,AxleCounterSection[] axles){
        // Reset all
        s.setHomeRed(true); s.setMLRed(true); s.setLLRed(true); s.setAdRed(true); s.activateAllHomeFeathers(false);
        SC_HSF.forceSetting('F',log); SC_SSF.forceSetting('F',log); SC_LL.forceSetting('F',log);
        ML_HSF.forceSetting('F',log); LL_HSF.forceSetting('F',log); ML_Ad_S.forceSetting('F',log);
        SC_HSF.disableFlank(log); SC_SSF.disableFlank(log); SC_LL.disableFlank(log);
        ML_HSF.unlockApproach(log); LL_HSF.unlockApproach(log); ML_Ad_S.unlockApproach(log);
        for(AxleCounterSection ac:axles) ac.resetFlags();

        switch(opt){
            case 1 -> {} 
            case 2 -> { s.setHomeYellow(true); s.setMLRed(true); SC_HSF.forceSetting('F',log); ML_HSF.forceSetting('S',log); ML_HSF.lockApproach(log);}
            case 3 -> { s.setHomeYellow(true); s.activateAllHomeFeathers(true); s.setLLRed(true); SC_HSF.forceSetting('S',log); LL_HSF.forceSetting('S',log); SC_HSF.enableFlank(log);}
            case 4 -> { s.setHomeYellow(true); s.setMLYellow(true); s.setAdRed(true);
                        SC_HSF.forceSetting('F',log); ML_HSF.forceSetting('S',log); SC_SSF.forceSetting('F',log); ML_Ad_S.forceSetting('F',log);
                        ML_HSF.lockApproach(log); ML_Ad_S.lockApproach(log);}
            case 5 -> { s.setHomeYellow(true); s.activateAllHomeFeathers(true); s.setLLYellow(true); s.setAdRed(true);
                        SC_HSF.forceSetting('S',log); LL_HSF.forceSetting('S',log); SC_LL.forceSetting('S',log);
                        SC_SSF.forceSetting('F',log); ML_Ad_S.forceSetting('F',log);
                        LL_HSF.lockApproach(log);}
            case 6 -> { s.setHomeGreen(true); s.setMLGreen(true); s.setAdGreen(true);
                        SC_HSF.forceSetting('F',log); ML_HSF.forceSetting('S',log); SC_SSF.forceSetting('F',log); ML_Ad_S.forceSetting('S',log);
                        ML_HSF.lockApproach(log); ML_Ad_S.lockApproach(log);}
            case 7 -> { s.setHomeYellow(true); s.activateAllHomeFeathers(true); s.setLLYellow(true); s.setAdGreen(true);
                        SC_HSF.forceSetting('S',log); LL_HSF.forceSetting('S',log); SC_LL.forceSetting('S',log);
                        SC_SSF.forceSetting('F',log); ML_Ad_S.forceSetting('F',log);
                        LL_HSF.lockApproach(log);}
        }
        logOperation(log,"EI Option "+opt+" applied");
    }

    static void revertOption1(SignalPanel s,Crossover SC_HSF,Crossover SC_SSF,Crossover SC_LL,
                              Section ML_HSF,Section LL_HSF,Section ML_Ad_S,AxleCounterSection[] axles,StringBuilder log){
        logOperation(log,"SPAD/TP/W.D.T.F detected! Reverting to Station Not Clear (Option 1)");
        s.setHomeRed(true); s.setMLRed(true); s.setLLRed(true); s.setAdRed(true); s.activateAllHomeFeathers(false);
        SC_HSF.forceSetting('F',log); SC_SSF.forceSetting('F',log); SC_LL.forceSetting('F',log);
        ML_HSF.forceSetting('F',log); LL_HSF.forceSetting('F',log); ML_Ad_S.forceSetting('F',log);
        SC_HSF.disableFlank(log); SC_SSF.disableFlank(log); SC_LL.disableFlank(log);
        ML_HSF.unlockApproach(log); LL_HSF.unlockApproach(log); ML_Ad_S.unlockApproach(log);
        for(AxleCounterSection ac:axles) ac.resetFlags();
    }
}
