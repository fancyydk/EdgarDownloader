/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgardownloader;

/**
 *
 * @author fancyydk
 */
public class EdgarIndex {
    private int startYear;
    private int startQuarter;
    private int endYear;
    private int endQuarter;
    
    public EdgarIndex() {
    }
    public EdgarIndex(int startYear, int startQuarter, int endYear, int endQuarter) {
        this.startYear = startYear;
        this.startQuarter = startQuarter;
        this.endYear = endYear;
        this.endQuarter = endQuarter;
    }
    
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
    public void setStartYear(String startYear) {
        setStartYear(Integer.parseInt(startYear));
    }
    public int getStartYear() {
        return startYear;
    }
    
    public void setStartQuarter(int startQuarter) {
        this.startQuarter = startQuarter;
    }
    public void setStartQuarter(String startQuarter) {
        setStartQuarter(Integer.parseInt(startQuarter));
    }
    public int getStartQuarter() {
        return startQuarter;
    }
    
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
    public void setEndYear(String endYear) {
        setEndYear(Integer.parseInt(endYear));
    }
    public int getEndYear() {
        return endYear;
    }
    
    public void setEndQuarter(int endQuarter) {
        this.endQuarter = endQuarter;
    }
    public void setEndQuarter(String endQuarter) {
        setEndQuarter(Integer.parseInt(endQuarter));
    }
    public int getEndQuarter() {
        return endQuarter;
    }
    
    public void setAll(int startYear, int startQuarter, int endYear, int endQuarter) {
        setStartYear(startYear);
        setStartQuarter(startQuarter);
        setEndYear(endYear);
        setEndQuarter(endQuarter);
    }
    public void setAll(String startYear, String startQuarter, String endYear, String endQuarter) {
        setAll(Integer.parseInt(startYear), Integer.parseInt(startQuarter), Integer.parseInt(endYear), Integer.parseInt(endQuarter));
    }
}
