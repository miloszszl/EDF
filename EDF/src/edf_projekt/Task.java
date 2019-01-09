/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edf_projekt;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author milosz
 */
public class Task {

    private static int counter = 0;

    private SimpleIntegerProperty executionTime;
    private SimpleIntegerProperty period;
    private SimpleStringProperty id;

    public Task(int executionTime, int period) {
        this();
        this.executionTime = new SimpleIntegerProperty(executionTime);
        this.period = new SimpleIntegerProperty(period);
    }
    
    public Task(){
        this.id = new SimpleStringProperty("T_"+counter++);
    }

    public int getExecutionTime() {
        return executionTime.get();
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = new SimpleIntegerProperty(executionTime);
    }

    public int getPeriod() {
        return this.period.get();
    }

    public void setPeriod(int period) {
        this.period=new SimpleIntegerProperty(period);
    }

    public String getId() {
        return id.get();
    }
}
