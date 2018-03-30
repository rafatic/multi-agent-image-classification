package AgentMoves;

import java.util.ArrayList;

public class AgentMoves {
    // Attributes
    private double _avg = 0; // Average of taken pixels
    private double n = 0; // Number of taken pixels

    private ArrayList<Pixel> _toVisit = new ArrayList<Pixel>();
    private int[][] _img;

    public void getImage(int[][] img) {
        _img = img;
    }

    public void getInitialPixel(Pixel p) {
        visitPixel(p);
    }

    private void updateStats(int pixel) {
        n ++;
        if (n == 1) {
            _avg = (_avg * (n - 1) + pixel) / n;
        }
    }

    private boolean isOutlier(int pixel) {
        // TO DO : trouver une bonne formule de outlier facile à calculer
        double Bmin = _avg*0.5;
        double Bmax = _avg*1.5;

        return pixel < Bmin || pixel > Bmax;
    }

    public Pixel nextPixel () {
        double minDeviation = 255;
        Pixel minPixel = _toVisit.get(0);
        int minIndex = 0;
        for(int i=0; i<_toVisit.size(); i++) {
            if ((Math.abs(_toVisit.get(i).color - _avg) < minDeviation) && _img[_toVisit.get(i).i][_toVisit.get(i).j] != -1) {
                minDeviation = Math.abs(_toVisit.get(i).color - _avg);
                minPixel = _toVisit.get(i);
                minIndex = i;
            }
        }
        _toVisit.remove(minIndex);
        return minPixel;
    }

    public void visitPixel(Pixel p) {
        _img[p.i][p.j] = -1;
        updateStats(p.color);
        addNeighboors(p);
    }

    private void addNeighboors(Pixel p) {
        if ((p.i > 0) && _img[p.i-1][p.j] != -1 && !isOutlier(_img[p.i-1][p.j])) {
            _toVisit.add(new Pixel(p.i-1,p.j,_img[p.i-1][p.j]));
        }

        if ((p.j > 0) && _img[p.i][p.j-1] != -1 && !isOutlier(_img[p.i][p.j-1])) {
            _toVisit.add(new Pixel(p.i,p.j-1,_img[p.i][p.j-1]));
        }

        if ((p.j < _img[0].length-1) && _img[p.i][p.j+1] != -1 && !isOutlier(_img[p.i][p.j+1])) {
            _toVisit.add(new Pixel(p.i,p.j+1,_img[p.i][p.j+1]));
        }

        if ((p.i < _img.length-1) && _img[p.i+1][p.j] != -1 && !isOutlier(_img[p.i+1][p.j])) {
            _toVisit.add(new Pixel(p.i+1,p.j,_img[p.i+1][p.j]));
        }
    }

    public boolean continueExploration(){
        return _toVisit.size() != 0;
    }

    public void printMyPixels() {
        for (int i = 0; i < _img.length; i++){
            for(int j = 0; j < _img.length; j++) {
                if (_img[i][j] == -1) {
                    System.out.println("("+i+","+j+")");
                }
            }
        }
    }
}
