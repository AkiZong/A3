package com.example.drawing.a3;

import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Model extends Observable {

    public class Drawings {
        public int index, start_x, start_y, end_x, end_y, line_thickness;
        public Color current_color;
        public int fill_color = Color.WHITE; // why color is int?
        public boolean filled = false;
        public boolean selected = false;
        public String shape;

        public Drawings() {
        }
    }

    public int num_Drawings = 0;
    public List<Drawings> components = new ArrayList<Drawings>();
    public int current_thickness, current_select; // current_select is the index of selected shape
    public boolean selected;
    public String current_tool;
    public int current_color;

    public Model() {
        current_thickness = 1;
        current_color = Color.BLACK;
        current_select = -1;
        selected = false;
        current_tool = "";
        setChanged();
    }

    public void clicked() {
        setChanged();
        notifyObservers();
    }

    public boolean draw_or_not() {
        if ((current_tool == "circle") ||
                (current_tool == "line") ||
                (current_tool == "square")) return true;
        else return false;
    }

    public void drawing_star(Point start_point, String tool, int line_thick, Color fill_color) {
        num_Drawings++;
        components.add(new Drawings());
        components.get(num_Drawings - 1).start_x = start_point.x;
        components.get(num_Drawings - 1).start_y = start_point.y;
        components.get(num_Drawings - 1).end_x = start_point.x;
        components.get(num_Drawings - 1).end_y = start_point.y;
        components.get(num_Drawings - 1).shape = tool;
        components.get(num_Drawings - 1).line_thickness = line_thick;
        components.get(num_Drawings - 1).current_color = fill_color;
        components.get(num_Drawings - 1).index = num_Drawings;
    }

    public void drawing_end(Point end_point) {
        components.get(num_Drawings - 1).end_x = end_point.x;
        components.get(num_Drawings - 1).end_y = end_point.y;
    }

    public int drawing_width(int i) {
        if (get_comp(i).end_x > get_comp(i).start_x) return get_comp(i).end_x - get_comp(i).start_x;
        else return get_comp(i).start_x - get_comp(i).end_x;
    }

    public int drawing_height(int i) {
        if (get_comp(i).end_y > get_comp(i).start_y) return get_comp(i).end_y - get_comp(i).start_y;
        else return get_comp(i).start_y - get_comp(i).end_y;
    }

    public int drawing_start_x(int i) {
        if (get_comp(i).start_x > get_comp(i).end_x) return get_comp(i).end_x;
        else return get_comp(i).start_x;
    }

    public int drawing_start_y(int i) {
        if (get_comp(i).start_y > get_comp(i).end_y) return get_comp(i).end_y;
        else return get_comp(i).start_y;
    }

    public int circle_r(int i) {
        int sx = get_comp(i).start_x;
        int ex = get_comp(i).end_x;
        int sy = get_comp(i).start_y;
        int ey = get_comp(i).end_y;
        int ans = (int) Math.sqrt((sx - ex) * (sx - ex) + (sy - ey) * (sy - ey));
        return ans;
    }

    public int circle_start_x(int i) {
        int ans = get_comp(i).start_x - circle_r(i);
        if (ans < 0) ans = 0;
        return ans;
    }

    public int circle_start_y(int i) {
        int ans = get_comp(i).start_y - circle_r(i);
        if (ans < 0) ans = 0;
        return ans;
    }

    public boolean line_sel_len(int i, int x, int y) {
        float line_y = (float) (x - get_comp(i).start_x) / (get_comp(i).end_x - get_comp(i).start_x);
        if ((line_y < 0) || (line_y > 1)) return false;
        line_y = (float) get_comp(i).start_y + line_y * (get_comp(i).end_y - get_comp(i).start_y);
        if ((y < (line_y + 10)) && (y > line_y - 10)) {
            return true;
        } else {
            return false;
        }
    }

    public int select_index(int x, int y) {
        for (int i = components.size() - 1; i >= 0; i--) {
            Drawings tmp = components.get(i);
            if (tmp.shape == "circle") {
                int csx = circle_start_x(i);
                int cr = circle_r(i);
                int csy = circle_start_y(i);
                float dis = (float) Math.sqrt((x - csx - cr) * (x - csx - cr) + (y - csy - cr) * (y - csy - cr));
                if (dis < circle_r(i)) {
                    selected = true;
                    current_select = i;
                    return i;
                }

            } else if (tmp.shape == "square") {
                if ((x > drawing_start_x(i)) && (x < drawing_start_x(i) + drawing_width(i)) &&
                        (y > drawing_start_y(i)) && (y < drawing_start_y(i) + drawing_height(i))) {
                    get_comp(i).selected = true;
                    selected = true;
                    current_select = i;
                    return i;
                }
            } else if (tmp.shape == "line") {
                if (line_sel_len(i, x, y)) {
                    get_comp(i).selected = true;
                    selected = true;
                    current_select = i;
                    return i;
                }
            }
        }
        current_select = -1;
        return -1;
    }

    public void clear_select() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).selected = false;
        }
        current_select = -1;
        selected = false;
    }

    public void fill(int x, int y) {
        int index = select_index(x, y);
        if (index == -1) return;
        else {
            get_comp(index).filled = true;
            get_comp(index).fill_color = current_color;
        }
    }

    public Drawings get_comp(int i) {
        return components.get(i);
    }

    public int total_shapes() {
        return components.size();
    }

    public void new_can() {
        components = new ArrayList<Drawings>();
    }

    public void new_model() {
        current_thickness = 1;
        current_color = Color.BLACK;
        current_select = -1;
        selected = false;
        current_tool = "";
        setChanged();
    }
}

