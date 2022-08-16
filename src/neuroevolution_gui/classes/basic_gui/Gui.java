package basic_gui;

import execution.NeuroevolutionProblemExecutor;
import execution.Problem;
import execution.ProblemExecutor;
import execution.ResultsData;
import execution.common.DataBinder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

public class Gui extends javax.swing.JFrame implements DataBinder {
    private static ProblemExecutor<?, ?, ?> problemExecutor;

    public static void create(Class<? extends ProblemExecutor<?, ?, ?>> problemExecutor) {

        if (problemExecutor == null) throw new IllegalArgumentException(
                "problem executor not null"
        );

        java.awt.EventQueue.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                // handle exception
            }

            Gui gui = new Gui();
            gui.setVisible(true);

            try {
                Gui.problemExecutor = problemExecutor.getConstructor(DataBinder.class).newInstance(gui);
            }
            catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private final LinkedList<JLabel> resultsLabels = new LinkedList<>();
    private final CardLayout evolutionCardLayout, predictionCardLayout;

    /**
     * Creates new form GUI
     */
    private Gui() {
        initComponents();

        evolutionCardLayout = (CardLayout) evolutionPanel.getLayout();
        predictionCardLayout = (CardLayout) predictionPanel.getLayout();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        runButton = new javax.swing.JButton();
        evolutionPanel = new javax.swing.JPanel();
        predictionPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        resultsScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        resultsPanel = new javax.swing.JPanel();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Neuroevolution Platform");

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        evolutionPanel.setPreferredSize(new java.awt.Dimension(200, 350));
        evolutionPanel.setLayout(new java.awt.CardLayout());

        predictionPanel.setPreferredSize(new java.awt.Dimension(200, 350));
        predictionPanel.setLayout(new java.awt.CardLayout());

        progressBar.setBorder(null);
        progressBar.setStringPainted(true);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

        resultsScrollPane.setBorder(null);
        resultsScrollPane.setMaximumSize(new java.awt.Dimension(509, 350));
        resultsScrollPane.setPreferredSize(new java.awt.Dimension(509, 100));

        java.awt.FlowLayout flowLayout2 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT);
        flowLayout2.setAlignOnBaseline(true);
        jPanel1.setLayout(flowLayout2);

        resultsPanel.setMaximumSize(new java.awt.Dimension(509, 32767));
        resultsPanel.setLayout(new java.awt.GridLayout(0, 1));
        jPanel1.add(resultsPanel);

        resultsScrollPane.setViewportView(jPanel1);

        previousButton.setText("Previous");
        previousButton.setEnabled(false);
        previousButton.setPreferredSize(new java.awt.Dimension(100, 30));
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.setEnabled(false);
        nextButton.setPreferredSize(new java.awt.Dimension(100, 30));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(resultsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(previousButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(resultsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(8, 8, 8)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(previousButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nextButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 1258, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(predictionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                                                        .addComponent(evolutionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(evolutionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(predictionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(54, 54, 54)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(runButton)
                                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
        problemExecutor.execute();
    }

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // clear the text color
        resultsLabels.peek().setForeground(null);

        // put it at the end of the queue

        JLabel newSelectedJLabel = resultsLabels.pollLast();

        // make text color red for the next jlabel
        newSelectedJLabel.setForeground(Color.red);

        resultsLabels.addFirst(newSelectedJLabel);


        predictionCardLayout.previous(predictionPanel);
        evolutionCardLayout.previous(evolutionPanel);            
    }

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        JLabel currentJLabel = resultsLabels.poll();

        // clear the text color
        currentJLabel.setForeground(null);

        // put it at the end of the queue
        resultsLabels.add(currentJLabel);

        // make text color red for the next jlabel
        resultsLabels.peek().setForeground(Color.red);

        predictionCardLayout.next(predictionPanel);
        evolutionCardLayout.next(evolutionPanel);            
    }

    @Override
    public void addResults(ResultsData resultsData) {
        synchronized(resultsLabels) {
            JLabel newResult = new JLabel(resultsData.resultsString);
            resultsLabels.add(newResult);

            resultsPanel.add(newResult);

            int labelsCount = resultsLabels.size();
            switch (labelsCount) {
                case 1:
                    newResult.setForeground(Color.red);

                    break;
                case 2:
                    nextButton.setEnabled(true);
                    previousButton.setEnabled(true);
                    newResult.setForeground(Color.LIGHT_GRAY);

                    break;
                default:
                    newResult.setForeground(Color.LIGHT_GRAY);

                    break;
            }
            System.out.println(resultsLabels.size());
            buildEvolutionChart(resultsData);
            buildPredictionChart(resultsData);             
        }
    }

    @Override
    public void addProgress(int progress) {
        progressBar.setValue(progress);
    }

    private void buildEvolutionChart(ResultsData resultsData) {

        XYSeriesCollection evolutionCollection = new XYSeriesCollection();
        XYSeries
                evolutionSeries = new XYSeries("Evolution fitness"),
                validationSeries = new XYSeries("Validation fitness");

        for (int i=0; i<resultsData.evolutionStatistics.length; i++) {

            evolutionSeries.add(i, resultsData.evolutionStatistics[i]);
            validationSeries.add(i, resultsData.validationStatistics[i]);
        }

        evolutionCollection.addSeries(evolutionSeries);
        evolutionCollection.addSeries(validationSeries);

        JFreeChart evolutionChart = ChartFactory.createScatterPlot(
                "Evolution",
                "Epochs", "Value", evolutionCollection
        );

        Shape cross = new Ellipse2D.Double(0, 0, 2, 2);
        ((XYPlot) evolutionChart.getPlot()).getRenderer().setSeriesShape(0, cross);
        ((XYPlot) evolutionChart.getPlot()).getRenderer().setSeriesShape(1, cross);

        evolutionPanel.add(new ChartPanel(evolutionChart));
        evolutionPanel.revalidate();
    }

    private void buildPredictionChart(ResultsData resultsData) {

        XYSeriesCollection predictionCollection = new XYSeriesCollection();
        XYSeries
                realDataSeries = new XYSeries("Real value"),
                predictedDataSeries = new XYSeries("Predicted values");


        int period = (int) Math.ceil(resultsData.realData.length / 100d);
        int p = 0;

        for (int i=0; i<resultsData.realData.length; i++) {

            for (int j=0; j<resultsData.realData[i].length; j++) {

//                if (i % period != 0) {
//                    continue;
//                }
//                if (resultsData.realData[i][j] == resultsData.predictedData[i][j]) {
                    predictedDataSeries.add(i, resultsData.predictedData[i][0]);

//                }
//                else {
//                realDataSeries.add(p, resultsData.realData[i][0]);
                realDataSeries.add(i, resultsData.predictedData[i][1]);

//                }
                p++;
            }
        }

        predictionCollection.addSeries(predictedDataSeries);
        predictionCollection.addSeries(realDataSeries);

        JFreeChart predictionChart = ChartFactory.createScatterPlot(
                "Evolution",
                "Epochs", "Value", predictionCollection
        );

        Shape cross = new Ellipse2D.Double(-2, -2, 4, 4);
        ((XYPlot) predictionChart.getPlot()).getRenderer().setSeriesShape(0, cross);

        predictionPanel.add(new ChartPanel(predictionChart));
        predictionPanel.revalidate();
    }
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    // Set cross-platform Java L&F (also called "Metal")
//                    UIManager.setLookAndFeel(
//                    UIManager.getSystemLookAndFeelClassName());
//                }
//                catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//                   // handle exception
//                }
//
//                new GUI().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify
    private javax.swing.JPanel evolutionPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton nextButton;
    private javax.swing.JPanel predictionPanel;
    private javax.swing.JButton previousButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel resultsPanel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JButton runButton;
}
