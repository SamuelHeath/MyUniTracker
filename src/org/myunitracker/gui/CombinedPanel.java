/*
 * Copyright (c) 2016, Samuel James Serwan Heath
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.myunitracker.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import java.awt.Color;
import javafx.scene.chart.CategoryAxis;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javafx.embed.swing.JFXPanel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javafx.scene.chart.LineChart;
import java.util.Map;
import javafx.scene.chart.NumberAxis;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.myunitracker.main.Unit;
import javafx.scene.chart.XYChart;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import org.myunitracker.main.MyUniTracker;
import java.awt.event.WindowAdapter;
import javax.swing.SwingUtilities;
import org.myunitracker.main.UnitReader;

/*
int gridx,int gridy,int gridwidth,int gridheight,double weightx, double weighty,
int anchor,int fill,Insets insets,int ipadx,int ipady
*/

/**
 * @author Sam
 */
public class CombinedPanel extends JPanel {
    
    private final String[] colour = new String[] {"#f3622d","#fba71b","#57b757","#44aaca","#4258c9","#9a42c8","#c84164","#888888"};
    private ArrayList<XYChart.Series> data;
    private JButton removePast_result, editPast_result;
    private JComboBox past_unitsCB;
    private JLabel cur_wam,expect_wam,core_wam,cur_gpa,expect_gpa,core_expectwam;
    private JPanel checkBoxPanel;
    private JTabbedPane tab;
    private Font fontTitle = MyUniTrackerGUI.fontTitle, fontSubTitle = MyUniTrackerGUI.fontSubTitle, fontText = MyUniTrackerGUI.fontText;
    private LineChart<String,Number> lineChart;
    private Map<JCheckBox,String> checkMap;
    
    public CombinedPanel() {
        this.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR);
        tab = MyUniTrackerGUI.getTabbedPane();
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                JPanel combinedGraphPanel = new JPanel();
                combinedGraphPanel.setBackground(Color.decode("#eeeeee")); //Color.decode("#e0e0e0")
                combinedGraphPanel.setLayout(new GridBagLayout());

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10,10,5,5);

                JPanel statsPanel = new JPanel(new GridBagLayout());
                statsPanel.setBackground(java.awt.Color.WHITE);
                statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
                ((javax.swing.border.TitledBorder) statsPanel.getBorder()).setTitleFont(fontTitle);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 0;
                combinedGraphPanel.add(statsPanel,gbc);

                JPanel unitPanel = new JPanel(new GridBagLayout());
                unitPanel.setBackground(java.awt.Color.WHITE);
                unitPanel.setBorder(BorderFactory.createTitledBorder("Add Units"));
                ((javax.swing.border.TitledBorder) unitPanel.getBorder()).setTitleFont(fontTitle);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 1;
                combinedGraphPanel.add(unitPanel,gbc);

                JPanel pastPanel = new JPanel(new GridBagLayout());
                pastPanel.setBackground(java.awt.Color.WHITE);
                pastPanel.setBorder(BorderFactory.createTitledBorder("Past Results"));
                ((javax.swing.border.TitledBorder) pastPanel.getBorder()).setTitleFont(fontTitle);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.gridx = 0;
                gbc.gridy = 2;
                combinedGraphPanel.add(pastPanel,gbc);

                checkBoxPanel = new JPanel(new GridBagLayout());
                checkBoxPanel.setBackground(java.awt.Color.WHITE);
                checkBoxPanel.setBorder(BorderFactory.createTitledBorder("Display Units"));
                ((javax.swing.border.TitledBorder) checkBoxPanel.getBorder()).setTitleFont(fontTitle);
                gbc.gridx = 0;
                gbc.gridy = 3;
                combinedGraphPanel.add(checkBoxPanel,gbc);

                final JFXPanel fxPanel = new JFXPanel();
                JPanel graphPanel = new JPanel(new GridBagLayout());
                gbc.insets = new Insets(10,5,10,10);
                gbc.gridheight = 4;
                gbc.gridx = 1;
                gbc.gridy = 0;
                fxPanel.setBackground(Color.decode("#eeeeee"));
                graphPanel.add(fxPanel);
                combinedGraphPanel.add(graphPanel,gbc);
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        fxPanel.setScene(createScene());
                        updateGraph();
                    }
                });

                GridBagConstraints gbcStats = new GridBagConstraints();
                gbcStats.fill = GridBagConstraints.HORIZONTAL;
                gbcStats.insets = new Insets(3,0,0,3);

                /*
                 * Sub Panel of Stats For WAM
                 */
                JPanel wamPanel = new JPanel(new GridBagLayout());
                wamPanel.setBackground(java.awt.Color.WHITE);
                wamPanel.setBorder(BorderFactory.createTitledBorder("Weighted Average Mark"));
                ((javax.swing.border.TitledBorder) wamPanel.getBorder()).setTitleFont(fontSubTitle);
                gbcStats.fill = GridBagConstraints.HORIZONTAL;
                gbcStats.anchor = GridBagConstraints.WEST;
                gbcStats.gridx = 0;
                gbcStats.gridy = 0;
                statsPanel.add(wamPanel,gbcStats);

                GridBagConstraints gbcWAM = new GridBagConstraints();
                gbcWAM.insets = new Insets(3,3,3,3);

                JLabel wamLabel = new JLabel("Current WAM:");
                wamLabel.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.EAST;
                gbcWAM.gridx = 0;
                gbcWAM.gridy = 0;
                wamPanel.add(wamLabel,gbcWAM);
                cur_wam = new JLabel(String.valueOf(MyUniTracker.calculateWAM(false)));
                cur_wam.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.WEST;
                gbcWAM.gridx = 1;
                gbcWAM.gridy = 0;
                wamPanel.add(cur_wam,gbcWAM);
                JLabel expect_wamLabel = new JLabel("Expected WAM (cur. units):");
                expect_wamLabel.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.EAST;
                gbcWAM.gridx = 0;
                gbcWAM.gridy = 1;
                wamPanel.add(expect_wamLabel,gbcWAM);
                expect_wam = new JLabel(String.valueOf(MyUniTracker.expectedWAM(false)));
                expect_wam.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.WEST;
                gbcWAM.gridx = 1;
                gbcWAM.gridy = 1;
                wamPanel.add(expect_wam,gbcWAM);
                JLabel core_wamLabel = new JLabel("Current WAM (Major/s):");
                core_wamLabel.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.EAST;
                gbcWAM.gridx = 0;
                gbcWAM.gridy = 2;
                wamPanel.add(core_wamLabel,gbcWAM);
                core_wam = new JLabel(String.valueOf(MyUniTracker.calculateWAM(true)));
                core_wam.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.WEST;
                gbcWAM.gridx = 1;
                gbcWAM.gridy = 2;
                wamPanel.add(core_wam,gbcWAM);
                JLabel core_expectwamLabel = new JLabel("Expected WAM (Major/s):");
                core_expectwamLabel.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.EAST;
                gbcWAM.gridx = 0;
                gbcWAM.gridy = 3;
                wamPanel.add(core_expectwamLabel,gbcWAM);
                core_expectwam = new JLabel(String.valueOf(MyUniTracker.expectedWAM(true)));
                core_expectwam.setFont(fontText);
                gbcWAM.anchor = GridBagConstraints.WEST;
                gbcWAM.gridx = 1;
                gbcWAM.gridy = 3;
                wamPanel.add(core_expectwam,gbcWAM);

                /*
                 * Sub Panel of Stats For GPA
                 */

                JPanel gpaPanel = new JPanel(new GridBagLayout());
                gpaPanel.setBackground(java.awt.Color.WHITE);
                gpaPanel.setBorder(BorderFactory.createTitledBorder("Grade Point Average"));
                ((javax.swing.border.TitledBorder) gpaPanel.getBorder()).setTitleFont(fontSubTitle);
                gbcStats.fill = GridBagConstraints.HORIZONTAL;
                gbcStats.gridx = 0;
                gbcStats.gridy = 1;
                statsPanel.add(gpaPanel,gbcStats);

                GridBagConstraints gbcGPA = new GridBagConstraints();
                gbcGPA.insets = new Insets(3,3,3,3);

                JLabel gpaLabel = new JLabel("Current GPA:");
                gpaLabel.setFont(fontText);
                gbcGPA.anchor = GridBagConstraints.EAST;
                gbcGPA.gridx = 0;
                gbcGPA.gridy = 0;
                gpaPanel.add(gpaLabel,gbcGPA);
                cur_gpa = new JLabel(String.valueOf(MyUniTracker.calculateGPA()));
                cur_gpa.setFont(fontText);
                gbcGPA.anchor = GridBagConstraints.WEST;
                gbcGPA.gridx = 1;
                gbcGPA.gridy = 0;
                gpaPanel.add(cur_gpa,gbcGPA);
                JLabel expect_gpaLabel = new JLabel("Expected GPA (cur. units):");
                expect_gpaLabel.setFont(fontText);
                gbcGPA.anchor = GridBagConstraints.EAST;
                gbcGPA.gridx = 0;
                gbcGPA.gridy = 1;
                gpaPanel.add(expect_gpaLabel,gbcGPA);
                expect_gpa = new JLabel(String.valueOf(MyUniTracker.expectedGPA()));
                expect_gpa.setFont(fontText);
                gbcGPA.anchor = GridBagConstraints.WEST;
                gbcGPA.gridx = 1;
                gbcGPA.gridy = 1;
                gpaPanel.add(expect_gpa,gbcGPA);

                /*
                 * Layout for unit panel
                 */

                GridBagConstraints gbcUnit = new GridBagConstraints();
                gbcUnit.insets = new Insets(3,3,3,3);

                JLabel name = new JLabel("Unit Code:");
                name.setFont(fontText);
                gbcUnit.gridx = 0;
                gbcUnit.gridy = 0;
                gbcUnit.anchor = GridBagConstraints.EAST;
                unitPanel.add(name,gbcUnit);

                final JTextField unit_name = new JTextField("Unit Code");
                unit_name.setToolTipText("Enter the name of the unit wish to add");
                unit_name.setFont(fontText);
                unit_name.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                gbcUnit.fill = GridBagConstraints.HORIZONTAL;
                gbcUnit.gridx = 1;
                gbcUnit.gridy = 0;
                unitPanel.add(unit_name,gbcUnit);
                gbcUnit.fill = GridBagConstraints.NONE;

                JLabel credit_pts = new JLabel("No. Credit Points:");
                credit_pts.setToolTipText("Enter the number of credit points for this unit");
                credit_pts.setFont(fontText);
                gbcUnit.gridx = 0;
                gbcUnit.gridy = 1;
                unitPanel.add(credit_pts,gbcUnit);

                final JTextField credit_points = new JTextField(String.valueOf(MyUniTracker.getDefaultCredit()));
                credit_points.setFont(fontText);
                credit_points.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                gbcUnit.fill = GridBagConstraints.HORIZONTAL;
                gbcUnit.gridx = 1;
                gbcUnit.gridy = 1;
                unitPanel.add(credit_points,gbcUnit);
                gbcUnit.fill = GridBagConstraints.NONE;

                JLabel core_unit = new JLabel("Core Unit:");
                core_unit.setFont(fontText);
                gbcUnit.gridx = 0;
                gbcUnit.gridy = 2;
                unitPanel.add(core_unit,gbcUnit);

                final JCheckBox core_unitCheck = new JCheckBox();
                core_unitCheck.setSelected(true);
                core_unitCheck.setBackground(Color.WHITE);
                gbcUnit.anchor = GridBagConstraints.WEST;
                gbcUnit.gridx = 1;
                unitPanel.add(core_unitCheck,gbcUnit);

                JLabel curtin_student = new JLabel("Curtin Student:");
                curtin_student.setFont(fontText);
                gbcUnit.anchor = GridBagConstraints.EAST;
                gbcUnit.gridx = 0;
                gbcUnit.gridy = 4;
                unitPanel.add(curtin_student,gbcUnit);

                final JCheckBox iscurtin_student = new JCheckBox();
                iscurtin_student.setSelected(MyUniTracker.isCurtin());
                iscurtin_student.setBackground(Color.WHITE);
                gbcUnit.anchor = GridBagConstraints.WEST;
                gbcUnit.gridx = 1;
                unitPanel.add(iscurtin_student,gbcUnit);

                JButton add_button = new JButton("Add Unit");
                add_button.setFont(fontText);
                add_button.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                add_button.setToolTipText("Add unit based on above information");
                //gbcUnit.fill = GridBagConstraints.HORIZONTAL;
                gbcUnit.anchor = GridBagConstraints.CENTER;
                gbcUnit.gridwidth = 2;
                gbcUnit.gridx = 0;
                gbcUnit.gridy = 3;
                unitPanel.add(add_button,gbcUnit);

                gbcUnit.gridwidth = 1;

                /*
                 Layout for past results panel.
                */

                GridBagConstraints gbcPast = new GridBagConstraints();
                gbcPast.insets = new Insets(3,3,3,3);

                JLabel past_resultLabel = new JLabel("Add Past Results:");
                past_resultLabel.setFont(fontText);
                gbcPast.anchor = GridBagConstraints.EAST;
                gbcPast.gridx = 0;
                gbcPast.gridy = 1;
                pastPanel.add(past_resultLabel,gbcPast);
                gbcPast.anchor = GridBagConstraints.CENTER;

                JButton addPastResult = new JButton("Add Results");
                addPastResult.setFont(fontText);
                addPastResult.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                addPastResult.setToolTipText("Click to add previous results");
                gbcPast.fill = GridBagConstraints.HORIZONTAL;
                gbcPast.anchor = GridBagConstraints.WEST;
                gbcPast.gridx = 1;
                gbcPast.gridy = 1;
                pastPanel.add(addPastResult,gbcPast);
                gbcPast.fill = GridBagConstraints.NONE;

                String[] past_unit_data = new String[MyUniTracker.past_results.size()];
                for (int i = 0; i < past_unit_data.length; i++) {
                    past_unit_data[i] = MyUniTracker.past_results.get(i).getUnitName();
                }

                JLabel past_unitLabel = new JLabel("Result:");
                past_unitLabel.setFont(fontText);
                gbcPast.anchor = GridBagConstraints.EAST;
                gbcPast.gridx = 0;
                gbcPast.gridy = 2;
                pastPanel.add(past_unitLabel,gbcPast);

                gbcPast.fill = GridBagConstraints.HORIZONTAL;

                editPast_result = new JButton("Edit Result");
                editPast_result.setFont(fontText);
                editPast_result.setToolTipText("Click to edit the selected result");
                gbcPast.gridx = 0;
                gbcPast.gridy = 3;
                pastPanel.add(editPast_result,gbcPast);

                removePast_result = new JButton("Remove Result");
                removePast_result.setFont(fontText);
                removePast_result.setToolTipText("Click to remove the selected result");
                gbcPast.gridx = 1;
                pastPanel.add(removePast_result,gbcPast);
                
                past_unitsCB = new JComboBox(past_unit_data);
                
                /*
                    Simply check to see if there are any past results and if 
                there is then allow users to use the edit and remove button,
                if not then make sure that they cant because they makes no sense
                and results in an error.
                */
                if (MyUniTracker.past_results.isEmpty()) { removePast_result.setEnabled(false);
                    editPast_result.setEnabled(false);  past_unitsCB.setEnabled(false);
                } else { removePast_result.setEnabled(true); editPast_result.setEnabled(true);
                    past_unitsCB.setEnabled(true);
                }
                
                past_unitsCB.setToolTipText("Select past results");
                past_unitsCB.setFont(fontText);
                past_unitsCB.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                gbcPast.gridx = 1;
                gbcPast.gridy = 2;
                pastPanel.add(past_unitsCB,gbcPast);

                add_button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (!unit_name.getText().equals("Unit Code") && !credit_points.getText().equals("Credit Points")) {
                            Unit u = new Unit(unit_name.getText(), Double.parseDouble(credit_points.getText()));
                            u.setCoreUnit(core_unitCheck.isSelected());
                            MyUniTracker.units.add(u);
                            UnitsPanel up = new org.myunitracker.gui.UnitsPanel(u);
                            up.setVisible(true);
                            tab.insertTab(u.getUnitName(),null,up,null,tab.getTabCount()-1);
                            updateAll();
                            unit_name.setText("Unit Code");
                        }
                    }
                });

                addPastResult.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) { 
                        Dialog addPast = new Dialog(null);
                    }
                });

                editPast_result.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) { 
                        Dialog editPast = new Dialog(MyUniTracker.findResult((String)past_unitsCB.getSelectedItem()));
                    }
                });

                removePast_result.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        MyUniTracker.past_results.remove(MyUniTracker.findResult((String)past_unitsCB.getSelectedItem()));
                        past_unitsCB.removeAllItems();
                        for (Unit u : MyUniTracker.past_results) 
                            past_unitsCB.addItem(u.getUnitName());
                        if (past_unitsCB.getItemCount() == 0) {
                            removePast_result.setEnabled(false);
                            editPast_result.setEnabled(false);
                            past_unitsCB.setEnabled(false);
                        } else {
                            editPast_result.setEnabled(true);
                            editPast_result.setEnabled(true);
                            past_unitsCB.setEnabled(true);
                        }
                        updateAll();
                    }
                });

                iscurtin_student.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MyUniTracker.setIsCurtin(iscurtin_student.isSelected());
                        credit_points.setText(String.valueOf(MyUniTracker.getDefaultCredit()));
                        updateStats();
                    }
                });

                /*
                 Check Box Panel Initialisation
                */
                updateAll();



               add(combinedGraphPanel);

                setVisible(true);
            }
        });
        tab.setSelectedIndex(tab.getTabCount()-1);
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
       
        //Defining a series
        for (Unit m : MyUniTracker.units) {
            XYChart.Series series = new XYChart.Series();
            series.setName(m.getUnitName());
            for (int i = 0; i < m.getAssessments().size(); i++) {
                if (!m.getAssessments().get(i).getAssessmentName().equals("Final Exam")) 
                series.getData().add(new XYChart.Data(m.getAssessments().get(i).getAssessmentName(),(m.getAssessments().get(i).getPercentage())));  
            }
            data.add(series);
            lineChart.getData().add(series);
        }
        Scene scene = new Scene(lineChart,689,669);
        Platform.setImplicitExit(false);
        return scene;
    }
    
    public void updateStats() {
        cur_wam.setText(String.valueOf(MyUniTracker.calculateWAM(false)));
        expect_wam.setText(String.valueOf(MyUniTracker.expectedWAM(false)));
        core_wam.setText(String.valueOf(MyUniTracker.calculateWAM(true)));
        core_expectwam.setText(String.valueOf(MyUniTracker.expectedWAM(true)));
        cur_gpa.setText(String.valueOf(MyUniTracker.calculateGPA()));
        expect_gpa.setText(String.valueOf(MyUniTracker.expectedGPA()));
        this.repaint();
    } 
    
    public void updateGraph() {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                for (XYChart.Series s: data) {
                    s.getData().clear();
                }
                lineChart.getData().clear();
                int i = 0;
                for (Unit unit : MyUniTracker.units) {
                    if (unit.isDisplayed()) {
                        XYChart.Series series = new XYChart.Series();
                        tab.setForegroundAt(i, Color.decode(colour[i%colour.length]));
                        i++;
                        series.setName(unit.getUnitName());
                        for (int j = 0; j < unit.getAssessments().size(); j++) {
                            if (!unit.getAssessments().get(j).getAssessmentName().equals("Final Exam")) 
                                series.getData().add(new XYChart.Data(unit.getAssessments().get(j).getAssessmentName(),(unit.getAssessments().get(j).getPercentage())));  
                        }
                        data.add(series);
                        lineChart.getData().add(series);
                    }
                }
            }
        });
    }
    
    protected void updateAll() {
        checkBoxPanel.removeAll();
        checkMap = new HashMap();
        
        for (int i = 0; i < MyUniTracker.units.size(); i++) {
            GridBagConstraints gbcCheck = new GridBagConstraints();
            JLabel unit = new JLabel(MyUniTracker.units.get(i).getUnitName());
            unit.setFont(fontText);
            gbcCheck.anchor = GridBagConstraints.EAST;
            gbcCheck.gridx = 0;
            gbcCheck.gridy = i;
            checkBoxPanel.add(unit,gbcCheck);
            JCheckBox cb = new JCheckBox();
            cb.setFont(fontText);
            cb.setBackground(Color.WHITE);
            cb.setSelected(MyUniTracker.units.get(i).isDisplayed());
            gbcCheck.anchor = GridBagConstraints.CENTER;
            gbcCheck.gridx = 1;
            checkBoxPanel.add(cb,gbcCheck);
            
            checkMap.put(cb, MyUniTracker.units.get(i).getUnitName());
            cb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox b = (JCheckBox) e.getSource();
                    for (Unit u : MyUniTracker.units) {
                        if (u.getUnitName().equals(checkMap.get(b))) {
                            u.setIsDisplayed(!u.isDisplayed());
                        }
                    }
                    updateStats();
                    updateGraph();
                }
            });
            unit.setForeground(Color.decode(colour[i%colour.length]));
        }
    }
    
    private class Dialog extends JFrame {
        
        private Unit result;
        
        public Dialog(Unit res) {
            super("Add Past Results");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.result = res;
            setSize(250,230);
            setLocationRelativeTo(null);
            initialise();
            setVisible(true);
        }
        
        private int getGradeFromMark(double mark) {
            switch ((int)Math.floor(mark/10.0)) {
                case 5: return Unit.GRADE_P;
                case 6: return Unit.GRADE_CR;
                case 7: return Unit.GRADE_D;
                case 8: return Unit.GRADE_HD;
                case 9: return Unit.GRADE_HD;
                case 10: return Unit.GRADE_HD;
                default: return Unit.GRADE_N;
            }
        }
        
        private void initialise() {
            JPanel pane = new JPanel();
            pane.setLayout(new GridBagLayout());
            pane.setBackground(Color.WHITE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3,5,5,3);
            gbc.anchor = GridBagConstraints.EAST;
            
            JLabel name = new JLabel("Unit Code:");
            name.setFont(fontText);
            gbc.gridx = 0;
            gbc.gridy = 0;
            pane.add(name,gbc);
            
            final JTextField unit_name;
            
            JLabel mark = new JLabel("Mark:");
            mark.setFont(fontText);
            gbc.gridx = 0;
            gbc.gridy = 2;
            pane.add(mark,gbc);
            
            final JTextField final_mark;
            
            JLabel credit_pts = new JLabel("No. Credit Points:");
            credit_pts.setFont(fontText);
            gbc.gridx = 0;
            gbc.gridy = 3;
            pane.add(credit_pts,gbc);
            
            final JTextField credit_points;
            
            JLabel core_unit = new JLabel("Core Unit:");
            core_unit.setFont(fontText);
            gbc.anchor = GridBagConstraints.EAST;
            gbc.gridx = 0;
            gbc.gridy = 4;
            pane.add(core_unit,gbc);
            
            JButton add_button = new JButton("Add Result");
            add_button.setFont(fontText);
            add_button.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridy = 5;
            pane.add(add_button,gbc);
            
            final JCheckBox core_unitCheck = new JCheckBox();
            
            if (result != null) {
                add_button.setText("Save Changes");
                unit_name = new JTextField(result.getUnitName());
                unit_name.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                unit_name.setFont(fontText);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 0;
                pane.add(unit_name,gbc);
                
                gbc.fill = GridBagConstraints.NONE;
                
                final_mark = new JTextField(String.valueOf(result.getFinalMark()));
                final_mark.setFont(fontText);
                final_mark.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 2;
                pane.add(final_mark,gbc);
                gbc.fill = GridBagConstraints.NONE;

                credit_points = new JTextField(String.valueOf(result.getCreditPoints()));
                credit_points.setFont(fontText);
                credit_points.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 3;
                pane.add(credit_points,gbc);
                
                core_unitCheck.setSelected(result.isCoreUnit());
                core_unitCheck.setBackground(Color.WHITE);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.gridx = 1;
                gbc.gridy = 4;
                pane.add(core_unitCheck,gbc);
                
                add_button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        result.setUnitName(unit_name.getText());
                        result.setFinalGrade(getGradeFromMark(Double.parseDouble(final_mark.getText())));
                        result.setFinalMark(Double.parseDouble(final_mark.getText()));
                        result.setCreditPoints(Double.parseDouble(credit_points.getText()));
                        result.setCoreUnit(core_unitCheck.isSelected());
                        past_unitsCB.removeAllItems();
                        for (Unit u : MyUniTracker.past_results) 
                            past_unitsCB.addItem(u.getUnitName());
                        if (past_unitsCB.getItemCount() == 0) {
                            removePast_result.setEnabled(false);
                            editPast_result.setEnabled(false);
                            past_unitsCB.setEnabled(false);
                        } else {
                            editPast_result.setEnabled(true);
                            editPast_result.setEnabled(true);
                            past_unitsCB.setEnabled(true);
                        }
                        close();
                    }
                });
            } else {
                unit_name = new JTextField("Unit Code");
                unit_name.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                unit_name.setFont(fontText);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 0;
                pane.add(unit_name,gbc);
                
                gbc.fill = GridBagConstraints.NONE;
                
                final_mark = new JTextField("80.0");
                final_mark.setFont(fontText);
                final_mark.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 2;
                pane.add(final_mark,gbc);
                gbc.fill = GridBagConstraints.NONE;

                credit_points = new JTextField(String.valueOf(MyUniTracker.getDefaultCredit()));
                credit_points.setFont(fontText);
                credit_points.setBackground(MyUniTrackerGUI.BACKGROUND_COLOUR01);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 3;
                pane.add(credit_points,gbc);
                
                core_unitCheck.setSelected(true);
                core_unitCheck.setBackground(Color.WHITE);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.gridx = 1;
                gbc.gridy = 4;
                pane.add(core_unitCheck,gbc);
                
                add_button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Unit res = new Unit(unit_name.getText(),Double.parseDouble(credit_points.getText()));
                        res.setFinalGrade(getGradeFromMark(Double.parseDouble(final_mark.getText())));
                        res.setFinalMark(Double.parseDouble(final_mark.getText()));
                        res.setCoreUnit(core_unitCheck.isSelected());
                        MyUniTracker.past_results.add(res);
                        past_unitsCB.removeAllItems();
                        for (Unit u : MyUniTracker.past_results) 
                            past_unitsCB.addItem(u.getUnitName());
                        if (past_unitsCB.getItemCount() == 0) {
                            removePast_result.setEnabled(false);
                            editPast_result.setEnabled(false);
                            past_unitsCB.setEnabled(false);
                        } else {
                            removePast_result.setEnabled(true);
                            editPast_result.setEnabled(true);
                            past_unitsCB.setEnabled(true);
                        }
                        close();
                        
                    }
                });
            }
            
            this.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    close();
                }
            });
            add(pane);
        }
        
        private void close() { this.setVisible(false); this.dispose(); updateStats(); }
    }
}