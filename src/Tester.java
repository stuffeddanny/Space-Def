import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Tester {
    public static void main(String[] args) {
        new MainScreen();
    }
}

class MainScreen {
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;

    private final int MAIN_SCREEN_RANDOM_BOOMS_DELAY = 200;
    private final int BOOM_DELAY_BETWEEN_FRAMES = 250;
    private final int ASTEROID_FALL_DELAY = 2000;

    // Icons
    private final ImageIcon background = new ImageIcon("images/background.png"),
            asteroidIco = new ImageIcon("images/asteroid.png"),
            redIco = new ImageIcon("images/red_first.png"),
            greenIco = new ImageIcon("images/green_first.png"),
            blueIco = new ImageIcon("images/blue_first.png"),
            logoIco = new ImageIcon("images/logo.png"),
            buttonIco = new ImageIcon("images/play_button.png"),
            boom1Ico = new ImageIcon("images/boom1.png"),
            boom2Ico = new ImageIcon("images/boom2.png"),
            boom3Ico = new ImageIcon("images/boom3.png"),
            highScoreIco = new ImageIcon("images/high_score.png");

    // Screen Elements
    private final JLabel asteroid = new JLabel(asteroidIco),
            red = new JLabel(redIco),
            green = new JLabel(greenIco),
            blue = new JLabel(blueIco),
            logo = new JLabel(logoIco),
            boom = new JLabel(boom1Ico),
            highScoreLabel = new JLabel(highScoreIco);
    private final JButton button = new JButton(buttonIco);


    // Frame and Panel
    private final JFrame frame = new JFrame();
    private final ImagePanel panel = new ImagePanel(background.getImage());

    // High score
    private int highScore = 0;

    public MainScreen() {
        setPanel();
        setFrame();
        setVisible();
    }

    public void setVisible() {
        frame.setVisible(true);
    }

    private void setPanel() {
        panel.setLayout(null);

        asteroid.setBounds(-100, -80, asteroidIco.getIconWidth(), asteroidIco.getIconHeight());

        red.setBounds(WINDOW_WIDTH/2 - redIco.getIconWidth()/2 - 160, 150, redIco.getIconWidth(), redIco.getIconHeight());

        green.setBounds(WINDOW_WIDTH/2 - greenIco.getIconWidth()/2, 180, greenIco.getIconWidth(), greenIco.getIconHeight());

        blue.setBounds(WINDOW_WIDTH/2 - blueIco.getIconWidth()/2 + 160, 210, blueIco.getIconWidth(), blueIco.getIconHeight());

        logo.setBounds(WINDOW_WIDTH/2 - logoIco.getIconWidth()/2, 320, logoIco.getIconWidth(), logoIco.getIconHeight());

        button.setBounds(400, 550, 400, 133);
        button.addActionListener(e -> frame.setVisible(false));

        panel.add(red);
        panel.add(green);
        panel.add(blue);
        panel.add(logo);
        setHighScoreIco(440);
        panel.add(button);
        panel.add(asteroid);

        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250), 630, 3);
        cycledAnimate(red, new Point(WINDOW_WIDTH/2 - redIco.getIconWidth()/2 - 160, 210), 60, 15);
        cycledAnimate(blue, new Point(WINDOW_WIDTH/2 - blueIco.getIconWidth()/2 + 160, 150), 60, 15);
        animateGreen();
        makeRandomBooms();
    }

    private void makeRandomBooms() {
        new Timer(MAIN_SCREEN_RANDOM_BOOMS_DELAY, e -> {
            animateBoom(randomNum(0, WINDOW_WIDTH - boom1Ico.getIconWidth()), randomNum(0, WINDOW_HEIGHT - boom1Ico.getIconHeight()));
            makeRandomBooms();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    private int randomNum(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    private void animateBoom(int x, int y) {
        boom.setBounds(x, y, boom1Ico.getIconWidth(), boom1Ico.getIconHeight());
        panel.add(boom);
        new Timer(BOOM_DELAY_BETWEEN_FRAMES, e -> {
            boom.setIcon(boom2Ico);
            new Timer(BOOM_DELAY_BETWEEN_FRAMES, e1 -> {
                boom.setIcon(boom3Ico);
                new Timer(BOOM_DELAY_BETWEEN_FRAMES, e11 -> {
                    panel.remove(boom);
                    panel.repaint();
                    ((Timer) e11.getSource()).stop();
                }).start();
                ((Timer) e1.getSource()).stop();
            }).start();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    private void animateGreen() {
        Rectangle compBounds = green.getBounds();

        Point oldPoint = new Point(compBounds.x, compBounds.y);

        new Timer(15, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                green.setBounds(oldPoint.x,
                        oldPoint.y + (currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != 30)
                    currentFrame++;
                else {
                    ((Timer) e.getSource()).stop();
                    cycledAnimate(green, new Point(WINDOW_WIDTH / 2 - greenIco.getIconWidth() / 2, 150), 60, 15);
                }
            }
        }).start();
    }

    private void animate(JComponent component, Point newPoint, int frames, int interval) {
        Rectangle compBounds = component.getBounds();

        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / frames,
                        (newPoint.y - oldPoint.y) / frames);

        new Timer(interval, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                component.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != frames)
                    currentFrame++;
                else
                    ((Timer)e.getSource()).stop();
            }
        }).start();
    }

    private void animateAsteroid(Point newPoint, int frames, int interval) {
        Rectangle compBounds = asteroid.getBounds();
        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / frames,
                        (newPoint.y - oldPoint.y) / frames);

        new Timer(interval, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                asteroid.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != frames)
                    currentFrame++;
                else {
                    ((Timer) e.getSource()).stop();
                    var random = randomNum(0, 800);
                    asteroid.setBounds(-100, -80 + random, 100, 80);
                    new Timer(ASTEROID_FALL_DELAY, e1 -> {
                        ((Timer) e1.getSource()).stop();
                        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250 + random), 630, 3);
                    }).start();
                }
            }
        }).start();
    }

    private void cycledAnimate(JComponent component, Point newPoint, int frames, int interval) {
        Rectangle compBounds = component.getBounds();

        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / frames,
                        (newPoint.y - oldPoint.y) / frames);

        new Timer(interval, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                component.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != frames) {
                    currentFrame++;
                } else {
                    ((Timer) e.getSource()).stop();
                    cycledAnimate(component, oldPoint, frames, interval);
                }
            }
        }).start();
    }

    private void setHighScoreIco(int y) {
        var stringHighScore = String.valueOf(highScore);
        ArrayList<ImageIcon> numbers = new ArrayList<>();
        var width = highScoreIco.getIconWidth() + 20;
        for (int i = 0; i < stringHighScore.length(); i++) {
            numbers.add(new ImageIcon("images/numbers/" + stringHighScore.charAt(i) + ".png"));
            width += 35;
        }
        highScoreLabel.setBounds(WINDOW_WIDTH/2 - width/2, y, highScoreIco.getIconWidth(), highScoreIco.getIconHeight());
        panel.add(highScoreLabel);
        var x = highScoreLabel.getX() + highScoreLabel.getWidth() + 25;
        for (ImageIcon ico : numbers) {
            var label = new JLabel(ico);
            label.setBounds(x, y, ico.getIconWidth(), ico.getIconHeight());
            panel.add(label);
            x += 35;
        }
    }

    private void setFrame() {
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Space Defenders");
        frame.add(panel);
    }
}

class ImagePanel extends JPanel {

    private final Image img;

    public ImagePanel(String img) {
        this(new ImageIcon(img).getImage());
    }

    public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

}