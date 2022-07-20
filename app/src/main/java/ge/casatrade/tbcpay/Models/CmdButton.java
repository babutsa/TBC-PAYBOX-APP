package ge.casatrade.tbcpay.Models;

import android.graphics.Color;

/**
 * Created by Giorgi Andriadze.
 */
public class CmdButton {
    private String command;
    private String label;
    private int color;

    public CmdButton(String command, String label, String color) {
        this.command = command;
        this.label = label;

        if(!color.substring(0, 1).equals("#")){
            color = "#" + color;
        }
        this.color = Color.parseColor(color);
    }

    public String getCommand() {
        return command;
    }


    public String getLabel() {
        return label;
    }


    public int getColor() {
        return color;
    }

}
