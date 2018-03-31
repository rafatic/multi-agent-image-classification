package AgentMoves;

public class Main {
    public static void main(String[] args) {
        AgentMoves a = new AgentMoves();

        int[][] img = new int[][]{
                { 255, 255, 255, 255, 255, 255, 255 },
                { 255, 8, 8, 9, 0, 0, 0 },
                { 255, 8, 8, 8, 0, 0, 0 },
                { 255, 255, 8, 10, 140, 140, 140 },
                { 255, 255, 255, 8, 140, 140, 140 },
                { 255, 255, 255, 255, 140, 140, 95 },
                { 255, 255, 255, 255, 255, 140, 0 }
        };
        Pixel initial = new Pixel(0,0,255);

        a.getImage(img);
        a.getInitialPixel(initial);

        while (a.continueExploration()) {
            Pixel p = a.nextPixel(); // The agent says "Hey, I want to take this pixel"
            a.visitPixel(p); // The coordinator says "Yes you can take it"
        }
        a.printMyPixels();
    }
}
