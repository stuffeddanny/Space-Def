import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.random.RandomGenerator;

public class Tester {
    public static void main(String[] args) {
        new Game();
    }
}

class Game {
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;

    private JFrame frame;
    private ImagePanel panel;
    private int highScore = 0;


    public Game() {
        setPanel();
        setFrame();
        setVisible();
    }

    public void setVisible() {
        frame.setVisible(true);
    }



    private void setPanel() {
        panel = new ImagePanel(new ImageIcon("images/background.png").getImage());
        panel.setLayout(null);

        var asteroidIco = new ImageIcon("images/asteroid.png");
        var asteroid = new JLabel(asteroidIco);
        asteroid.setBounds(-100, -80, asteroidIco.getIconWidth(), asteroidIco.getIconHeight());

        var redIco = new ImageIcon("images/red_first.png");
        var red = new JLabel(redIco);
        red.setBounds(WINDOW_WIDTH/2 - redIco.getIconWidth()/2 - 160, 150, redIco.getIconWidth(), redIco.getIconHeight());

        var greenIco = new ImageIcon("images/green_first.png");
        var green = new JLabel(greenIco);
        green.setBounds(WINDOW_WIDTH/2 - greenIco.getIconWidth()/2, 180, greenIco.getIconWidth(), greenIco.getIconHeight());

        var blueIco = new ImageIcon("images/blue_first.png");
        var blue = new JLabel(blueIco);
        blue.setBounds(WINDOW_WIDTH/2 - blueIco.getIconWidth()/2 + 160, 210, blueIco.getIconWidth(), blueIco.getIconHeight());

        var logoIco = new ImageIcon("images/logo.png");
        var logo = new JLabel(logoIco);
        logo.setBounds(WINDOW_WIDTH/2 - logoIco.getIconWidth()/2, 320, logoIco.getIconWidth(), logoIco.getIconHeight());

        var button = new JButton();
        button.setIcon(new ImageIcon("images/play_button.png"));
        button.setBounds(400, 550, 400, 133);
        button.addActionListener(e -> frame.setVisible(false));

        panel.add(red);
        panel.add(green);
        panel.add(blue);
        panel.add(logo);
        setHighScoreImage(panel, 440);
        panel.add(button);
        panel.add(asteroid);

        animateAsteroid(asteroid, new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250), 630, 3);
        cycledAnimate(red, new Point(WINDOW_WIDTH/2 - redIco.getIconWidth()/2 - 160, 210), 60, 15);
        cycledAnimate(blue, new Point(WINDOW_WIDTH/2 - blueIco.getIconWidth()/2 + 160, 150), 60, 15);
        animateGreen(green, greenIco);
        makeRandomBooms(panel);

    }

    private void makeRandomBooms(JPanel panel) {
        var delay = 200;
        new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animateBoom(panel, randomNum(0, 1160), randomNum(0, 760));
                makeRandomBooms(panel);
                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    private int randomNum(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    private void animateBoom(JPanel panel, int x, int y) {
        var boom1Ico = new ImageIcon("images/boom1.png");
        var boom2Ico = new ImageIcon("images/boom2.png");
        var boom3Ico = new ImageIcon("images/boom3.png");

        var delay = 250;
        var boom = new JLabel(boom1Ico);
        boom.setBounds(x, y, boom1Ico.getIconWidth(), boom1Ico.getIconHeight());

        panel.add(boom);

        new Timer(delay, e -> {
            boom.setIcon(boom2Ico);
            new Timer(delay, e1 -> {
                boom.setIcon(boom3Ico);
                new Timer(delay, e11 -> {
                    panel.remove(boom);
                    panel.repaint();
                    ((Timer) e11.getSource()).stop();
                }).start();
                ((Timer) e1.getSource()).stop();
            }).start();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    private void animateGreen(JComponent component, ImageIcon greenIco) {
        Rectangle compBounds = component.getBounds();

        Point oldPoint = new Point(compBounds.x, compBounds.y);

        new Timer(15, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                component.setBounds(oldPoint.x,
                        oldPoint.y + (currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != 30)
                    currentFrame++;
                else {
                    ((Timer) e.getSource()).stop();
                    cycledAnimate(component, new Point(WINDOW_WIDTH / 2 - greenIco.getIconWidth() / 2, 150), 60, 15);
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

    private void animateAsteroid(JComponent component, Point newPoint, int frames, int interval) {
        Rectangle compBounds = component.getBounds();
        var delay = 2000;
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
                else {
                    ((Timer) e.getSource()).stop();
                    var random = randomNum(0, 800);
                    component.setBounds(-100, -80 + random, 100, 80);
                    new Timer(delay, e1 -> {
                        ((Timer) e1.getSource()).stop();
                        animateAsteroid(component, new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250 + random), 630, 3);
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

    private void setHighScoreImage(ImagePanel panel, int y) {
        var stringHighScore = String.valueOf(highScore);
        ImageIcon highScoreImage = new ImageIcon("images/high_score.png");
        JLabel highScoreLabel = new JLabel(highScoreImage);
        ArrayList<ImageIcon> numbers = new ArrayList<>();
        var width = highScoreImage.getIconWidth() + 20;
        for (int i = 0; i < stringHighScore.length(); i++) {
            numbers.add(new ImageIcon("images/numbers/" + stringHighScore.charAt(i) + ".png"));
            width += 35;
        }
        highScoreLabel.setBounds(WINDOW_WIDTH/2 - width/2, y, highScoreImage.getIconWidth(), highScoreImage.getIconHeight());
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
        frame = new JFrame();
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