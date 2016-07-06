package org.main.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import javax.swing.BoxLayout;
import javafx.scene.chart.CategoryAxis;
import javax.swing.JButton;
import javafx.embed.swing.JFXPanel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.main.myunitracker.Unit;
import javafx.scene.chart.XYChart;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import org.main.myunitracker.MyUniTracker;

/**
 *
 * @author Sam
 */
public class CombinedPanel extends JPanel {
    
    private LineChart<String,Number> lineChart;
    private ArrayList<XYChart.Series> data;
    private JTextArea text;
    
    public CombinedPanel(double gpa, double expectGPA) {
        this.setVisible(true);
        JPanel cjp = new JPanel();
        cjp.setLayout(new BoxLayout(cjp,BoxLayout.Y_AXIS));
        
        final JFXPanel fxPanel = new JFXPanel();
        JPanel summary = new JPanel();
        JLabel sum = new JLabel("Summary of data:");
        text = new JTextArea("Current GPA: " + MyUniTracker.calculateGPA() + 
                "\nExpected GPA(Based on current units): " + MyUniTracker.expectedGPA()
                + "\nCurrent WAM: " + MyUniTracker.calculateWAM() + 
                "\nExpected WAM(Based on current units): " + MyUniTracker.expectedWAM(),5,2);
        text.setEditable(false);
        text.setFocusable(false);
        JButton addPastUnit = new JButton("Add past results");
        addPastUnit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) { 
                Dialog addPast = new Dialog();
            }
        });
        
        summary.add(sum, BorderLayout.CENTER);
        summary.add(text);
        summary.add(addPastUnit);
        cjp.add(fxPanel);
        cjp.add(summary);
        add(cjp);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }
    
    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    } 
    
    private Scene createScene() {
        data = new ArrayList(1);
        Stage s = new Stage();
        s.setTitle("Unit Progress");
        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Assessments");
        yAxis.setLabel("Marks (%)");
        yAxis.setUpperBound(100.0);
        //creating the chart
        lineChart = new LineChart(xAxis,yAxis);
        lineChart.setTitle("All Units");
        //defining a series
        for (Unit m : MyUniTracker.units) {
            XYChart.Series series = new XYChart.Series();
            series.setName(m.getName());
            for (int i = 0; i < m.getAssessments().size(); i++) {
                if (!m.getAssessments().get(i).getAssessmentName().equals("Final Exam")) 
                series.getData().add(new XYChart.Data(m.getAssessments().get(i).getAssessmentName(),(m.getAssessments().get(i).getPercentage())));  
            }
            data.add(series);
            lineChart.getData().add(series);
        }
        Scene scene = new Scene(lineChart,880,600);
        Platform.setImplicitExit(false);
       
        return scene;
    }
    
    public void updatePanel() {  
        text.setText("Current GPA: " + MyUniTracker.calculateGPA() + 
            "\nExpected GPA(Based on current units): " + MyUniTracker.expectedGPA()
            + "\nCurrent WAM: " + MyUniTracker.calculateWAM() + 
            "\nExpected WAM(Based on current units): " + MyUniTracker.expectedWAM());
        this.repaint();
    } 
    
    public void updateGraph() {
        this.repaint();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                for (XYChart.Series s: data) {
                    s.getData().clear();
                    
                }
                lineChart.getData().clear();
                for (Unit m : MyUniTracker.units) {
                    XYChart.Series series = new XYChart.Series();
                    series.setName(m.getName());
                    for (int i = 0; i < m.getAssessments().size(); i++) {
                        if (!m.getAssessments().get(i).getAssessmentName().equals("Final Exam")) 
                        series.getData().add(new XYChart.Data(m.getAssessments().get(i).getAssessmentName(),(m.getAssessments().get(i).getPercentage())));  
                    }
                    data.add(series);
                    lineChart.getData().add(series);
                }
            }
        });
    }
    
    private class Dialog extends JFrame {
        
        public Dialog() {
            super("New Unit");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(280,150);
            setLocationRelativeTo(null);
            initialise();
            setVisible(true);
        }
        
        private void initialise() {
            JPanel pane = new JPanel();
            
            JLabel name = new JLabel("Unit Name:");
            final JTextField unit_name = new JTextField("Unit Name");
            JLabel grade = new JLabel("Grade:");
            String[] grades = new String[] {"HD", "D", "CR","P","N"};
            final JComboBox gradeComboBox = new JComboBox(grades);
            gradeComboBox.setSelectedIndex(0);
            JLabel mark = new JLabel("Mark:");
            final JTextField final_mark = new JTextField("60.0");
            JLabel credit_pts = new JLabel("Number of Credit Points:");
            final JTextField credit_points = new JTextField("6");
            JButton add_button = new JButton("Add Result");
            
            add_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Unit u = new Unit(unit_name.getText(),Integer.parseInt(credit_points.getText()));
                    u.setFinalGrade((String)gradeComboBox.getSelectedItem());
                    u.setFinalMark(Double.parseDouble(final_mark.getText()));
                    MyUniTracker.past_units.add(u);
                    close();
                }
            });
            
            this.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    close();
                }
            });
            
            pane.add(name);
            pane.add(unit_name);
            pane.add(grade);
            pane.add(gradeComboBox);
            pane.add(mark);
            pane.add(final_mark);
            pane.add(credit_pts);
            pane.add(credit_points);
            pane.add(add_button);
            add(pane);
        }
        
        private void close() { this.setVisible(false); this.dispose(); updatePanel(); }
    }
}