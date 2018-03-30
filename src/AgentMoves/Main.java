package AgentMoves;

public class Main {
    public static void main(String[] args) {
        AgentMoves a = new AgentMoves();

        int[][] img = new int[][]{
                { 0, 0, 0, 0, 0, 0, 0 },
                { 0, 8, 8, 9, 0, 0, 0 },
                { 0, 8, 8, 8, 0, 0, 0 },
                { 0, 0, 8, 10, 140, 140, 140 },
                { 0, 0, 0, 8, 140, 140, 140 },
                { 0, 0, 0, 0, 0, 0, 0 }
        };
        Pixel initial = new Pixel(2,2,8);

        a.getImage(img);
        a.getInitialPixel(initial);

        while (a.continueExploration()) {
            Pixel p = a.nextPixel(); // The agent says "Hey, I want to take this pixel"
            a.visitPixel(p); // The coordinator says "Yes you can take it"
        }
        a.printMyPixels();
    }
}
