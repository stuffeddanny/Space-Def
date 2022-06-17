import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class Tester {
    public static void main(String[] args) {
        new MainScreen();
        try {
            SimpleAudioPlayer();
        } catch (Exception e) {}
    }

    private static void SimpleAudioPlayer() throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("sounds/background.wav").getAbsoluteFile());

        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);

        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}

class MainScreen {
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;

    private final int BOOM_DELAY_BETWEEN_FRAMES = 250;
    private final int ASTEROID_FALL_DELAY = 2000;

    // Icons
    private final ImageIcon background = new ImageIcon("images/background.png"),
            asteroidIco = new ImageIcon("images/icons/asteroid.png"),
            redIco = new ImageIcon("images/monsters/red.png"),
            greenIco = new ImageIcon("images/monsters/green.png"),
            blueIco = new ImageIcon("images/monsters/blue.png"),
            logoIco = new ImageIcon("images/labels/logo.png"),
            buttonIco = new ImageIcon("images/buttons/play_button.png"),
            boom1Ico = new ImageIcon("images/booms/boom1.png"),
            boom2Ico = new ImageIcon("images/booms/boom2.png"),
            boom3Ico = new ImageIcon("images/booms/boom3.png");

    // Screen Elements
    private final JLabel asteroid = new JLabel(asteroidIco),
            red = new JLabel(redIco),
            green = new JLabel(greenIco),
            blue = new JLabel(blueIco),
            logo = new JLabel(logoIco);
    private final JButton button = new JButton(buttonIco);


    // Frame and Panel
    private final JFrame frame = new JFrame();
    private final ImagePanel panel = new ImagePanel(background.getImage());

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
        button.addActionListener(e -> {
            new GameScreen(1);
            frame.dispose();
        });

        panel.add(red);
        panel.add(green);
        panel.add(blue);
        panel.add(logo);
        panel.add(button);
        panel.add(asteroid);

        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250));
        Utilities.cycledAnimate(red, new Point(WINDOW_WIDTH/2 - redIco.getIconWidth()/2 - 160, 210), 60, 15);
        Utilities.cycledAnimate(blue, new Point(WINDOW_WIDTH/2 - blueIco.getIconWidth()/2 + 160, 150), 60, 15);
        animateGreen();
        makeRandomBooms();
    }

    private void makeRandomBooms() {
        int MAIN_SCREEN_RANDOM_BOOMS_DELAY = 200;
        new Timer(MAIN_SCREEN_RANDOM_BOOMS_DELAY, e -> {
            ((Timer) e.getSource()).stop();
            animateBoom(Utilities.randomNum(0, WINDOW_WIDTH - boom1Ico.getIconWidth()), Utilities.randomNum(0, WINDOW_HEIGHT - boom1Ico.getIconHeight()));
            makeRandomBooms();
        }).start();
    }

    private void animateBoom(int x, int y) {
        var boom = new JLabel(boom1Ico);
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
                    Utilities.cycledAnimate(green, new Point(WINDOW_WIDTH / 2 - greenIco.getIconWidth() / 2, 150), 60, 15);
                }
            }
        }).start();
    }

    private void animateAsteroid(Point newPoint) {
        Rectangle compBounds = asteroid.getBounds();
        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / 630,
                        (newPoint.y - oldPoint.y) / 630);

        new Timer(3, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                asteroid.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != 630)
                    currentFrame++;
                else {
                    ((Timer) e.getSource()).stop();
                    var random = Utilities.randomNum(0, 800);
                    asteroid.setBounds(-100, -80 + random, 100, 80);
                    new Timer(ASTEROID_FALL_DELAY, e1 -> {
                        ((Timer) e1.getSource()).stop();
                        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250 + random));
                    }).start();
                }
            }
        }).start();
    }

    private void setFrame() {
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Space Defenders");
        frame.add(panel);
    }
}

class GameScreen {
    private final int WINDOW_WIDTH = 1400;
    private final int WINDOW_HEIGHT = 850;
    private final int BOOM_DELAY_BETWEEN_FRAMES = 200;
    private int TIME_INTERVAL_IN_MONSTERS_MOVEMENTS,
            MIN_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS,
            MAX_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS;

    // Icons
    private static final ImageIcon background = new ImageIcon("images/background.png"),
            livesIco = new ImageIcon("images/labels/lives.png"),
            scoreIco = new ImageIcon("images/labels/score.png"),
            levelIco = new ImageIcon("images/labels/level.png"),
            fullHeartIco = new ImageIcon("images/icons/full_heart.png"),
            emptyHeartIco = new ImageIcon("images/icons/empty_heart.png"),
            rocketIco = new ImageIcon("images/icons/rocket.png"),
            rocketShotIco = new ImageIcon("images/icons/rocket_shot.png"),
            monster10Ico = new ImageIcon("images/monsters/monster1_0.png"),
            monster11Ico = new ImageIcon("images/monsters/monster1_1.png"),
            monster20Ico = new ImageIcon("images/monsters/monster2_0.png"),
            monster21Ico = new ImageIcon("images/monsters/monster2_1.png"),
            monster30Ico = new ImageIcon("images/monsters/monster3_0.png"),
            monster31Ico = new ImageIcon("images/monsters/monster3_1.png"),
            monster40Ico = new ImageIcon("images/monsters/monster4_0.png"),
            monster41Ico = new ImageIcon("images/monsters/monster4_1.png"),
            monster50Ico = new ImageIcon("images/monsters/monster5_0.png"),
            boom1Ico = new ImageIcon("images/booms/boom1.png"),
            boom2Ico = new ImageIcon("images/booms/boom2.png"),
            boom3Ico = new ImageIcon("images/booms/boom3.png"),
            ufoIco = new ImageIcon("images/icons/ufo.png"),
            castleIco = new ImageIcon("images/icons/castle.png"),
            castle1Ico = new ImageIcon("images/icons/castle1.png"),
            castle2Ico = new ImageIcon("images/icons/castle2.png"),
            monsterShotIco = new ImageIcon("images/icons/monster_shot.png"),
            monster51Ico = new ImageIcon("images/monsters/monster5_1.png");

    // Screen Elements
    private final JLabel livesLabel = new JLabel(livesIco),
            scoreLabel = new JLabel(scoreIco),
            levelLabel = new JLabel(levelIco),
            rocketLabel = new JLabel(rocketIco),
            rocketShotLabel = new JLabel(rocketShotIco),
            ufo = new JLabel(ufoIco);
    private final ArrayList<JLabel> hearts = new ArrayList<>(),
            numbers = new ArrayList<>(),
            levelScreenNumbers = new ArrayList<>(),
            scoreScreenNumbers = new ArrayList<>(),
            castles = new ArrayList<>(),
            monsters = new ArrayList<>();



    // Frame and Panel
    private final JFrame frame = new JFrame();
    private final ImagePanel panel = new ImagePanel(background.getImage());

    private boolean rocketIsOut = false,
            down = true;
    private String direction = "right";

    private int score = 0,
            scoreForBonus = 0,
            lives = 3;

    private final int level;

    private final ArrayList<Integer> castlesHp = new ArrayList<>();

    public GameScreen(int level) {
        this.level = level;
        switch (level) {
            case 1 -> {
                TIME_INTERVAL_IN_MONSTERS_MOVEMENTS = 900;
                MIN_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS = 1000;
                MAX_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS = 1300;
            }
            case 2 -> {
                TIME_INTERVAL_IN_MONSTERS_MOVEMENTS = 800;
                MIN_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS = 700;
                MAX_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS = 1000;
            }
            case 3 -> {
                TIME_INTERVAL_IN_MONSTERS_MOVEMENTS = 700;
                MIN_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS = 500;
                MAX_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS = 800;
            }
        }
        setPanel();
        setFrame();
        setVisible();
    }

    public void setVisible() {
        frame.setVisible(true);
    }

    private void setFrame() {
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Space Defenders");
        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.getX() > rocketIco.getIconWidth()/2 && e.getX() < WINDOW_WIDTH - rocketIco.getIconWidth()/2) {
                    rocketLabel.setBounds(e.getX() - rocketIco.getIconWidth()/2, 741, rocketIco.getIconWidth(), rocketIco.getIconHeight());
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getX() > rocketIco.getIconWidth()/2 && e.getX() < WINDOW_WIDTH - rocketIco.getIconWidth()/2) {
                    rocketLabel.setBounds(e.getX() - rocketIco.getIconWidth()/2, 741, rocketIco.getIconWidth(), rocketIco.getIconHeight());
                }
            }
        });

        frame.addMouseListener(new MouseListener() {
            final Timer timer = new Timer(10, e1 -> shoot());
            @Override
            public void mouseClicked(MouseEvent e) {
                shoot();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                timer.start();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                timer.stop();
            }
            @Override
            public void mouseEntered(MouseEvent e) {

            }
            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        frame.add(panel);
    }

    private void setPanel() {
        panel.setLayout(null);

        livesLabel.setBounds(10, 10, levelIco.getIconWidth(), levelIco.getIconHeight());
        var x = livesLabel.getX() + livesIco.getIconWidth() + 10;

        hearts.add(new JLabel());
        hearts.add(new JLabel());
        hearts.add(new JLabel());
        for (JLabel heart: hearts) {
            heart.setBounds(x, 10, fullHeartIco.getIconWidth(), fullHeartIco.getIconHeight());
            panel.add(heart);
            x += fullHeartIco.getIconWidth() + 5;
        }
        for (int i = 0; i < 10; i++) {
            numbers.add(new JLabel(new ImageIcon("images/numbers/" + i + "_30.png")));
        }

        panel.add(livesLabel);
        setLives(lives);
        panel.add(scoreLabel);
        setScore(score);
        panel.add(levelLabel);
        setLevel(level);

        rocketLabel.setBounds(WINDOW_WIDTH/2 - rocketIco.getIconWidth()/2, 741, rocketIco.getIconWidth(), rocketIco.getIconHeight());

        panel.add(rocketLabel);

        int UFO_Y = 50;
        ufo.setBounds(-ufoIco.getIconWidth(), UFO_Y, ufoIco.getIconWidth(), ufoIco.getIconHeight());
        panel.add(ufo);

        int UFO_DELAY = 25000;
        timeUfo(UFO_DELAY, UFO_Y);

        createMonsters();

        createCastles();
        castlesHp.add(30);
        castlesHp.add(30);
        castlesHp.add(30);
        castlesHp.add(30);

        for (JLabel monster : monsters) {
            panel.add(monster);
        }

        int x_MOVEMENT_DIST_FOR_MONSTERS = 20;
        int y_MOVEMENT_DIST_FOR_MONSTERS = 20;
        animateMonsters(y_MOVEMENT_DIST_FOR_MONSTERS, x_MOVEMENT_DIST_FOR_MONSTERS, TIME_INTERVAL_IN_MONSTERS_MOVEMENTS);
        animateMonstersShots();
    }

    private void animateMonstersShots() {
        var delay = Utilities.randomNum(MIN_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS, MAX_TIME_INTERVAL_BETWEEN_MONSTERS_SHOTS);
        new Timer(delay, e ->  {
            monsterShoot(randomMonster());
            animateMonstersShots();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    private void monsterShoot(JLabel monster) {
        var shot = new JLabel(monsterShotIco);
        shot.setBounds(monster.getX()+monster.getWidth()/2-monsterShotIco.getIconWidth()/2, monster.getY() + 10, monsterShotIco.getIconWidth(), monsterShotIco.getIconHeight());
        panel.add(shot);
        animateMonsterShot(shot, new Point(shot.getX(), WINDOW_HEIGHT), WINDOW_HEIGHT - shot.getY(), 2);
    }
    public void animateMonsterShot(JComponent component, Point newPoint, int frames, int interval) {
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

                if (checkCollisionWithRocketOrCastle(component)) {
                    ((Timer) e.getSource()).stop();
                    animateBoom(component.getX() + component.getWidth()/2 - boom1Ico.getIconWidth()/2,
                            component.getY() + component.getHeight() - boom1Ico.getIconHeight()/2);
                    panel.remove(component);
                }
                if (currentFrame != frames) {
                    currentFrame++;
                } else {
                    ((Timer) e.getSource()).stop();
                    panel.remove(component);
                }
            }
        }).start();
    }
    private boolean checkCollisionWithRocketOrCastle(JComponent component) {
        if (Utilities.isContain(rocketLabel, component.getX(), component.getY() + component.getHeight()) ||
                Utilities.isContain(rocketLabel, component.getX() + component.getWidth()/2, component.getHeight()) ||
                Utilities.isContain(rocketLabel, component.getX() + component.getWidth(), component.getHeight())) {
            if (lives == 1) {
                gameOver();
                return true;
            }
            setLives(lives - 1);
            makeRocketTransparent();
            return true;
        }
        for (JLabel castle : castles) {
            if (Utilities.isContain(castle, component.getX(), component.getY() + component.getHeight()) ||
                    Utilities.isContain(castle, component.getX() + component.getWidth()/2, component.getHeight()) ||
                    Utilities.isContain(castle, component.getX() + component.getWidth(), component.getHeight())) {
                var index = castles.indexOf(castle);
                castlesHp.set(index, castlesHp.get(castles.indexOf(castle))-1);
                if (castlesHp.get(index) == 20) {
                    castle.setIcon(castle1Ico);
                } else if (castlesHp.get(index) == 10) {
                    castle.setIcon(castle2Ico);
                } else if (castlesHp.get(index) == 0) {
                    panel.remove(castle);
                    castles.remove(index);
                    castlesHp.remove(index);
                    panel.repaint();
                }
                return true;
            }
        }
        return false;
    }

    private void gameOver() {
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {

        }
        new GameOver();
        frame.dispose();
    }
    private void makeRocketTransparent() {
        new Timer(400, e -> {
            rocketLabel.setVisible(false);
            new Timer(400, e1 -> {
                ((Timer) e1.getSource()).stop();
                ((Timer) e.getSource()).stop();
                rocketLabel.setVisible(true);
            }).start();
        }).start();
    }
    private JLabel randomMonster() {
        return monsters.get(Utilities.randomNum(0, monsters.size()-1));
    }
    private void animateMonsters(int downMovDist, int movementDist, int timeInterval) {
        var width = monster10Ico.getIconWidth();
        new Timer(timeInterval, e -> {
            for (JLabel monster : monsters) {
                if (monster.getY()+monster.getHeight() >= rocketLabel.getY()) {
                    ((Timer) e.getSource()).stop();
                    gameOver();
                    return;
                }
                Icon icon = monster.getIcon();
                if (monster10Ico.equals(icon)) {
                    monster.setIcon(monster11Ico);
                } else if (monster20Ico.equals(icon)) {
                    monster.setIcon(monster21Ico);
                } else if (monster30Ico.equals(icon)) {
                    monster.setIcon(monster31Ico);
                } else if (monster40Ico.equals(icon)) {
                    monster.setIcon(monster41Ico);
                } else if (monster50Ico.equals(icon)) {
                    monster.setIcon(monster51Ico);
                } else if (monster11Ico.equals(icon)) {
                    monster.setIcon(monster10Ico);
                } else if (monster21Ico.equals(icon)) {
                    monster.setIcon(monster20Ico);
                } else if (monster31Ico.equals(icon)) {
                    monster.setIcon(monster30Ico);
                } else if (monster41Ico.equals(icon)) {
                    monster.setIcon(monster40Ico);
                } else if (monster51Ico.equals(icon)) {
                    monster.setIcon(monster50Ico);
                }
            }
            switch (direction) {
                case "right":
                    if (collisionCheck(width)) {
                        direction = "downl";
                    } else {
                        moveMonstersRight(movementDist, timeInterval);
                    }
                    break;
                case "downl":
                    if (down) {
                        moveMonstersDown(downMovDist, timeInterval);
                        ((Timer) e.getSource()).stop();
                        animateMonsters(downMovDist, movementDist, TIME_INTERVAL_IN_MONSTERS_MOVEMENTS-=20);
                        down = false;
                    } else {
                        moveMonstersLeft(movementDist, timeInterval);
                        down = true;
                        direction = "left";
                    }
                    break;
                case "left":
                    if (collisionCheck(width)) {
                        direction = "downr";
                    } else {
                        moveMonstersLeft(movementDist, timeInterval);
                    }
                    break;
                case "downr":
                    if (down) {
                        moveMonstersDown(downMovDist, timeInterval);
                        ((Timer) e.getSource()).stop();
                        animateMonsters(downMovDist, movementDist, TIME_INTERVAL_IN_MONSTERS_MOVEMENTS-=20);
                        down = false;
                    } else {
                        moveMonstersRight(movementDist, timeInterval);
                        down = true;
                        direction = "right";
                    }
                    break;
            }
        }).start();
    }
    private void createMonsters() {
        var distY = 10;
        var y = 100;
        var monsterHeight = monster10Ico.getIconHeight();
        createMonsterRaw(monster10Ico, y);
        y += distY + monsterHeight;
        createMonsterRaw(monster20Ico, y);
        y += distY + monsterHeight;
        createMonsterRaw(monster30Ico, y);
        y += distY + monsterHeight;
        createMonsterRaw(monster40Ico, y);
        y += distY + monsterHeight;
        createMonsterRaw(monster50Ico, y);
    }

    private void createCastles() {
        var y = 630;
        for (int i = 1; i <= 4; i++) {
            castles.add(new JLabel(castleIco));
            castles.get(castles.size()-1).setBounds((WINDOW_WIDTH/5)*i - castleIco.getIconWidth()/2, y, castleIco.getIconWidth(), castleIco.getIconHeight());
            panel.add(castles.get(castles.size()-1));
        }

    }
    private void createMonsterRaw(ImageIcon ico, int y) {
        var distX = 40;
        var monsterWidth = monster10Ico.getIconWidth();
        var monsterHeight = monster10Ico.getIconHeight();

        for (int i = 0; i < 11; i++) {
            monsters.add(new JLabel(ico));
            monsters.get(monsters.size()-1).setBounds(
                    WINDOW_WIDTH/2 - monsterWidth/2 - 5* distX - 5*monsterWidth + i*(monsterWidth + distX), y,
                    monsterWidth, monsterHeight
            );
        }
    }

    private void setLevel(int level) {
        for (JLabel lab : levelScreenNumbers) {
            panel.remove(lab);
        }

        levelScreenNumbers.clear();

        var stringLevel = String.valueOf(level);
        var width = levelIco.getIconWidth() + 20;
        var digits = new ArrayList<Integer>();
        for (int i = 0; i < stringLevel.length(); i++) {
            digits.add(Integer.parseInt(String.valueOf(stringLevel.charAt(i))));
            width += 20;
        }
        levelLabel.setBounds(WINDOW_WIDTH - width - 10, 10, levelIco.getIconWidth(), levelIco.getIconHeight());
        var x = levelLabel.getX() + levelLabel.getWidth() + 25;
        for (Integer dig : digits) {
            levelScreenNumbers.add(new JLabel(numbers.get(dig).getIcon()));
            levelScreenNumbers.get(levelScreenNumbers.size()-1).setBounds(x, 10, numbers.get(dig).getIcon().getIconWidth(), numbers.get(dig).getIcon().getIconHeight());
            panel.add(levelScreenNumbers.get(levelScreenNumbers.size()-1));
            x += 20;
        }
        panel.repaint();
    }
    private void setScore(int score) {
        for (JLabel lab : scoreScreenNumbers) {
            panel.remove(lab);
        }

        scoreScreenNumbers.clear();

        var stringScore = String.valueOf(score);
        var width = scoreIco.getIconWidth() + 20;
        var digits = new ArrayList<Integer>();
        for (int i = 0; i < stringScore.length(); i++) {
            digits.add(Integer.parseInt(String.valueOf(stringScore.charAt(i))));
            width += 20;
        }
        scoreLabel.setBounds(WINDOW_WIDTH/2 - width/2, 10, scoreIco.getIconWidth(), scoreIco.getIconHeight());
        var x = scoreLabel.getX() + scoreLabel.getWidth() + 25;
        for (Integer dig : digits) {
            scoreScreenNumbers.add(new JLabel(numbers.get(dig).getIcon()));
            scoreScreenNumbers.get(scoreScreenNumbers.size()-1).setBounds(x, 10, numbers.get(dig).getIcon().getIconWidth(), numbers.get(dig).getIcon().getIconHeight());
            panel.add(scoreScreenNumbers.get(scoreScreenNumbers.size()-1));
            x += 20;
        }
        panel.repaint();
    }
    private void setLives(int lives) {
        this.lives = lives;
        var empty = 3 - lives;
        for (int i = 0; i < lives; i++) {
            hearts.get(i).setIcon(fullHeartIco);
        }
        for (int i = 1; i <= empty; i++) {
            hearts.get(hearts.size()-i).setIcon(emptyHeartIco);
        }
    }

    private void timeUfo(int delay, int y) {
        new Timer(delay, e -> animateUfo(y)).start();
    }

    private void animateUfo(int y) {
        var frames = WINDOW_WIDTH + ufoIco.getIconWidth();
        Rectangle compBounds = ufo.getBounds();
        Point newPoint = new Point(WINDOW_WIDTH, y);

        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / frames,
                        (newPoint.y - oldPoint.y) / frames);

        new Timer(4, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                ufo.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (! checkShootForCollisionWithUfo(rocketShotLabel) && currentFrame != frames) {
                    currentFrame++;
                } else {
                    ((Timer) e.getSource()).stop();
                    ufo.setBounds(- ufoIco.getIconWidth(), ufo.getY(), ufoIco.getIconWidth(), ufoIco.getIconHeight());
                }
            }
        }).start();
    }

    private void shoot() {
        if (!rocketIsOut) {
            rocketShotLabel.setBounds(rocketLabel.getX() + rocketIco.getIconWidth() / 2 - rocketShotIco.getIconWidth() / 2, rocketLabel.getY() + 15, rocketShotIco.getIconWidth(), rocketShotIco.getIconHeight());
            panel.add(rocketShotLabel);
            rocketIsOut = true;
            animateShoot(rocketShotLabel, new Point(rocketShotLabel.getX(), -rocketShotIco.getIconHeight()));
        }
    }
    private void animateShoot(JComponent component, Point newPoint) {
        Rectangle compBounds = component.getBounds();
        var frames = 398;

        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / frames,
                        (newPoint.y - oldPoint.y) / frames);

        int SHOOT_DELAY_BETWEEN_FRAMES = 3;
        new Timer(SHOOT_DELAY_BETWEEN_FRAMES, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                component.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);
                if (checkShootForCollisionWithMonster(component) || checkShootForCollisionWithCastle(component)) {
                    ((Timer) e.getSource()).stop();
                }
                if (currentFrame != frames)
                    currentFrame++;
                else {
                    rocketIsOut = false;
                    ((Timer) e.getSource()).stop();
                }
            }
        }).start();
    }

    private boolean checkShootForCollisionWithMonster(JComponent rocketShot) {
        for (JLabel monster : monsters) {
            if (Utilities.isContain(monster, rocketShot.getX() + rocketShot.getWidth()/2, rocketShot.getY()) ||
                    Utilities.isContain(monster, rocketShot.getX(), rocketShot.getY()) ||
                    Utilities.isContain(monster, rocketShot.getX() + rocketShot.getWidth(), rocketShot.getY())) {
                panel.remove(monster);
                monsters.remove(monster);
                addScoreForMonster(monster);
                animateBoom(rocketShot.getX() + rocketShot.getWidth()/2 - boom1Ico.getIconWidth()/2, rocketShot.getY() - boom1Ico.getIconWidth()/2);
                panel.remove(rocketShotLabel);
                rocketIsOut = false;
                panel.repaint();
                if (monsters.size() == 0) {
                    nextLevelOrWin();
                }
                return true;
            }
        }
        return false;
    }
    private boolean checkShootForCollisionWithUfo(JComponent rocketShot) {
        if (Utilities.isContain(ufo, rocketShot.getX() + rocketShot.getWidth()/2, rocketShot.getY()) ||
                Utilities.isContain(ufo, rocketShot.getX(), rocketShot.getY()) ||
                Utilities.isContain(ufo, rocketShot.getX() + rocketShot.getWidth(), rocketShot.getY())) {
            addScoreForUfo();
            animateBoom(rocketShot.getX() + rocketShot.getWidth()/2 - boom1Ico.getIconWidth()/2, rocketShot.getY() - boom1Ico.getIconWidth()/2);
            panel.remove(rocketShotLabel);
            rocketIsOut = false;
            panel.repaint();
            return true;
        }
        return false;
    }

    private void nextLevelOrWin() {
        if (level == 1) {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
            new GameScreen(2);
            frame.dispose();
        } else if (level == 2) {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
            new GameScreen(3);
            frame.dispose();
        } else if (level == 3) {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
            new SuccessScreen();
            frame.dispose();
        }
    }
    private boolean checkShootForCollisionWithCastle(JComponent rocketShot) {
        for (JLabel castle : castles) {
            if (Utilities.isContain(castle, rocketShot.getX() + rocketShot.getWidth()/2, rocketShot.getY()) ||
                    Utilities.isContain(castle, rocketShot.getX(), rocketShot.getY()) ||
                    Utilities.isContain(castle, rocketShot.getX() + rocketShot.getWidth(), rocketShot.getY())) {
                animateBoom(rocketShot.getX() + rocketShot.getWidth()/2 - boom1Ico.getIconWidth()/2, rocketShot.getY() - boom1Ico.getIconWidth()/2);
                panel.remove(rocketShotLabel);
                rocketIsOut = false;
                panel.repaint();
                return true;
            }
        }
        return false;
    }

    private void addScoreForUfo() {
        var plot = Utilities.randomNum(1, 4);
        switch (plot) {
            case 1 -> {
                setScore(score += 50);
                scoreForBonus += 50;
            }
            case 2 -> {
                setScore(score += 100);
                scoreForBonus += 100;
            }
            case 3 -> {
                setScore(score += 150);
                scoreForBonus += 150;
            }
            case 4 -> {
                setScore(score += 300);
                scoreForBonus += 300;
            }
        }
        if (scoreForBonus >= 1500) {
            scoreForBonus -= 1500;
            if (lives < 3) {
                setLives(lives + 1);
            }
        }
    }

    private void addScoreForMonster(JLabel monster) {
        var icon = monster.getIcon();
        if (icon.equals(monster11Ico) || icon.equals(monster10Ico) || icon.equals(monster21Ico) || icon.equals(monster20Ico)) {
            setScore(score += 30);
            scoreForBonus += 30;
        } else if (icon.equals(monster31Ico) || icon.equals(monster30Ico)) {
            setScore(score += 20);
            scoreForBonus += 20;
        } else {
            setScore(score += 10);
            scoreForBonus += 10;
        }
        if (scoreForBonus >= 1500) {
            scoreForBonus -= 1500;
            if (lives < 3) {
                setLives(lives + 1);
            }
        }
    }

    private boolean collisionCheck(int width) {
        var min = WINDOW_WIDTH;
        var max = 0;
        for (JLabel monster : monsters) {
            if (monster.getX() > max) {
                max = monster.getX();
            }
            if (monster.getX() < min) {
                min = monster.getX();
            }
        }
        return max + width + width / 2 >= WINDOW_WIDTH || min - width / 2 <= 0;
    }

    private void animateBoom(int x, int y) {
        var boom = new JLabel(boom1Ico);
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

    private void moveMonstersRight(int movementDist, int timeInterval) {
        for (JLabel monster : monsters) {
            Utilities.animate(monster, new Point(monster.getX() + movementDist, monster.getY()), movementDist, timeInterval/2/movementDist);
        }
    }

    private void moveMonstersLeft(int movementDist, int timeInterval) {
        for (JLabel monster : monsters) {
            Utilities.animate(monster, new Point(monster.getX() - movementDist, monster.getY()), movementDist, timeInterval/2/movementDist);
        }
    }

    private void moveMonstersDown(int movementDist, int timeInterval) {
        for (JLabel monster : monsters) {
            Utilities.animate(monster, new Point(monster.getX(), monster.getY() + movementDist), movementDist, timeInterval/2/movementDist);
        }
    }
}

class GameOver {
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;

    private final int BOOM_DELAY_BETWEEN_FRAMES = 250;
    private final int ASTEROID_FALL_DELAY = 2000;

    // Icons
    private final ImageIcon background = new ImageIcon("images/background.png"),
            asteroidIco = new ImageIcon("images/icons/asteroid.png"),
            gameOverIco = new ImageIcon("images/labels/game_over.png"),
            buttonIco = new ImageIcon("images/buttons/restart_button.png"),
            boom1Ico = new ImageIcon("images/booms/boom1.png"),
            boom2Ico = new ImageIcon("images/booms/boom2.png"),
            boom3Ico = new ImageIcon("images/booms/boom3.png");

    // Screen Elements
    private final JLabel asteroid = new JLabel(asteroidIco),
            gameOver = new JLabel(gameOverIco);
    private final JButton button = new JButton(buttonIco);


    // Frame and Panel
    private final JFrame frame = new JFrame();
    private final ImagePanel panel = new ImagePanel(background.getImage());

    public GameOver() {
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

        gameOver.setBounds(WINDOW_WIDTH/2 - gameOverIco.getIconWidth()/2, 300, gameOverIco.getIconWidth(), gameOverIco.getIconHeight());

        button.setBounds(400, 550, 400, 133);
        button.addActionListener(e -> {
            new GameScreen(1);
            frame.dispose();
        });

        panel.add(gameOver);
        panel.add(button);
        panel.add(asteroid);

        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250));
        makeRandomBooms();
    }

    private void makeRandomBooms() {
        int MAIN_SCREEN_RANDOM_BOOMS_DELAY = 200;
        new Timer(MAIN_SCREEN_RANDOM_BOOMS_DELAY, e -> {
            ((Timer) e.getSource()).stop();
            animateBoom(Utilities.randomNum(0, WINDOW_WIDTH - boom1Ico.getIconWidth()), Utilities.randomNum(0, WINDOW_HEIGHT - boom1Ico.getIconHeight()));
            makeRandomBooms();
        }).start();
    }

    private void animateBoom(int x, int y) {
        var boom = new JLabel(boom1Ico);
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

    private void animateAsteroid(Point newPoint) {
        Rectangle compBounds = asteroid.getBounds();
        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / 630,
                        (newPoint.y - oldPoint.y) / 630);

        new Timer(3, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                asteroid.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != 630)
                    currentFrame++;
                else {
                    ((Timer) e.getSource()).stop();
                    var random = Utilities.randomNum(0, 800);
                    asteroid.setBounds(-100, -80 + random, 100, 80);
                    new Timer(ASTEROID_FALL_DELAY, e1 -> {
                        ((Timer) e1.getSource()).stop();
                        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250 + random));
                    }).start();
                }
            }
        }).start();
    }

    private void setFrame() {
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Space Defenders");
        frame.add(panel);
    }
}

class SuccessScreen {
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;

    private final int BOOM_DELAY_BETWEEN_FRAMES = 250;
    private final int ASTEROID_FALL_DELAY = 2000;

    // Icons
    private final ImageIcon background = new ImageIcon("images/background.png"),
            asteroidIco = new ImageIcon("images/icons/asteroid.png"),
            youWonIco = new ImageIcon("images/labels/you_won.png"),
            buttonIco = new ImageIcon("images/buttons/main_menu_button.png"),
            boom1Ico = new ImageIcon("images/booms/boom1.png"),
            boom2Ico = new ImageIcon("images/booms/boom2.png"),
            boom3Ico = new ImageIcon("images/booms/boom3.png");

    // Screen Elements
    private final JLabel asteroid = new JLabel(asteroidIco),
            youWon = new JLabel(youWonIco);
    private final JButton button = new JButton(buttonIco);


    // Frame and Panel
    private final JFrame frame = new JFrame();
    private final ImagePanel panel = new ImagePanel(background.getImage());

    public SuccessScreen() {
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

        youWon.setBounds(WINDOW_WIDTH/2 - youWonIco.getIconWidth()/2, 300, youWonIco.getIconWidth(), youWonIco.getIconHeight());

        button.setBounds(400, 550, 400, 133);
        button.addActionListener(e -> {
            new MainScreen();
            frame.dispose();
        });

        panel.add(youWon);
        panel.add(button);
        panel.add(asteroid);

        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250));
        makeRandomBooms();
    }

    private void makeRandomBooms() {
        int MAIN_SCREEN_RANDOM_BOOMS_DELAY = 200;
        new Timer(MAIN_SCREEN_RANDOM_BOOMS_DELAY, e -> {
            ((Timer) e.getSource()).stop();
            animateBoom(Utilities.randomNum(0, WINDOW_WIDTH - boom1Ico.getIconWidth()), Utilities.randomNum(0, WINDOW_HEIGHT - boom1Ico.getIconHeight()));
            makeRandomBooms();
        }).start();
    }

    private void animateBoom(int x, int y) {
        var boom = new JLabel(boom1Ico);
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

    private void animateAsteroid(Point newPoint) {
        Rectangle compBounds = asteroid.getBounds();
        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / 630,
                        (newPoint.y - oldPoint.y) / 630);

        new Timer(3, new ActionListener() {
            int currentFrame = 0;
            public void actionPerformed(ActionEvent e) {
                asteroid.setBounds(oldPoint.x + (animFrame.x * currentFrame),
                        oldPoint.y + (animFrame.y * currentFrame),
                        compBounds.width,
                        compBounds.height);

                if (currentFrame != 630)
                    currentFrame++;
                else {
                    ((Timer) e.getSource()).stop();
                    var random = Utilities.randomNum(0, 800);
                    asteroid.setBounds(-100, -80 + random, 100, 80);
                    new Timer(ASTEROID_FALL_DELAY, e1 -> {
                        ((Timer) e1.getSource()).stop();
                        animateAsteroid(new Point(WINDOW_WIDTH, WINDOW_HEIGHT - 250 + random));
                    }).start();
                }
            }
        }).start();
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

class Utilities {
    public static int randomNum(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static void animate(JComponent component, Point newPoint, int frames, int interval) {
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
                    ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    public static void cycledAnimate(JComponent component, Point newPoint, int frames, int interval) {
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

    public static boolean isContain(JComponent comp, int x, int y) {
        return (x >= comp.getX() && x <= comp.getX() + comp.getWidth()) && (y >= comp.getY() && y <= comp.getY() + comp.getHeight());
    }
}