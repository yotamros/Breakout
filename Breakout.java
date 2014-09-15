/*
 * File: Breakout.java
 * -------------------
 * The goal of this program is to simulate the game Breakout, which has 10
 * layers of bricks, a paddle and a bouncing ball.  The player has to bounce 
 * the ball with the paddle and get rid of all the bricks.
 */

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.MediaTools;
import acm.util.RandomGenerator;

@SuppressWarnings("serial")
public class Breakout extends GraphicsProgram {

    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Width of a brick */
    private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1)
            * BRICK_SEP)
            / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Number of turns */
    private static final int NTURNS = 3;

    /** Left position of the left row of bricks. */
    private static final int LEFT_ROW_POS = (WIDTH - ((BRICK_WIDTH + BRICK_SEP)
            * NBRICKS_PER_ROW - BRICK_SEP)) / 2;

    private GRect paddle;
    private RandomGenerator rgen = RandomGenerator.getInstance();
    private double vx;
    private double vy = 3.0;
    private GOval ball;
    private int ballHits = 0;
    private boolean gameOver = false;
    private int turn = 0;



    /** 
     * Starts a game. 
     */
    private void startGame() {
        if (turn == 0) {
            nextRound();
        }
        setVX();
        makeBall();
        moveBall();
    }

    /** 
     * Adds sound effects.
     */
    private void sound(String filename) {
        AudioClip bounceClip = MediaTools.loadAudioClip(filename);
        bounceClip.play(); 
    }

    /** 
     * Moves the ball, checks for collision with walls, paddle, and bricks. 
     */
    private void moveBall() {
        while (!gameOver) {
            ball.move(vx, vy);
            checkForWallCollision();
            checkForObjectCollision();
            pause(20);
        }
    }

    /** 
     * Sets the x direction of the ball. 
     */
    private void setVX() {
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5)) {
            vx = -vx;
        }
    }

    /** Checks for collision of ball with blocks and paddle.  After a paddle 
     * collision change ball direction.  After a brick collision change 
     * direction and remove the brick.  */
    private void checkForObjectCollision() {
        GObject collider = getCollidingObject();
        if (collider != null) {
            vy = -vy;
            if (collider == paddle) {
                sound("bounce.au");
            // If the colliding object is not the paddle it must be a brick, in
            // that case the brick is removed from the screen. 
            } else {
                sound("sound.wav");
                remove(collider);
                ballHits++;
                if (ballHits >= NBRICK_ROWS * NBRICK_ROWS) {
                    gameOver();
                }
            }
        }
    }
    
    /** 
     * Announces game over depending on score.
     */
    private void gameOver() {
        gameOver = true;
        if (ballHits >= NBRICK_ROWS * NBRICK_ROWS) {
            makeLabel("You won!");
        } else {
            makeLabel("You lost!");
        }
    }
    
    /**
     * Detects if the ball collided with any object by checking each of the 
     * four corners associated with the radius of the ball.  
     * @return GObject, the object which collided with the ball. Return null 
     * if no object collided. 
     */
    private GObject getCollidingObject() {
        if (getElementAt(ball.getX(), ball.getY()) != null) {
            return getElementAt(ball.getX(), ball.getY());
        }
        if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
            getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
        }
        if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
            return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
        }
        if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()
                + BALL_RADIUS * 2) != null) {
            return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()
                    + BALL_RADIUS * 2);
        }
        return null;
    }
    
    /** Checks for ball collision with walls. If the ball hits any wall other
     * than the bottom one, switch ball direction. If ball hits the bottom wall
     * terminate the turn, increase turn count until game over.
     */
    private void checkForWallCollision() {
        // If the y or x coordinate are equal or smaller than 0 or equal/larger 
        // than the width or height of the screen, minus the size of the ball, 
        // it means the ball has reached a wall.
        if (ball.getLocation().getY() <= 0) {
            vy = -vy;
        }
        if (ball.getLocation().getX() >= WIDTH - BALL_RADIUS * 2
                || ball.getLocation().getX() <= 0) {
            vx = -vx;
        }
        if (ball.getLocation().getY() >= HEIGHT - BALL_RADIUS * 2) {
            sound("lost.wav");
            turn++;
            if (turn == NTURNS) {
                gameOver();
            } else {
                nextRound();
            }
        }
    }
    
    /** 
     * Displays an alert message for the beginning of a round. 
     */
    private void nextRound() {
        makeLabel("Get Ready");
        makeLabel("3");
        makeLabel("2");
        makeLabel("1");
        if (turn != 0) {
            removeAll();
            run();
        }
    }
    
    /** 
     * Displays a text message at the center of the board.
     */
    private void makeLabel(String text) {
        GLabel label = new GLabel(text);
        label.setFont("Ariel-40");
        label.setColor(Color.RED);
        add(label, (WIDTH - label.getWidth()) / 2, HEIGHT / 2);
        pause(1000);
        remove(label);
    }
    
    /** 
     * Creates the ball. 
     */
    private void makeBall() {
        ball = new GOval(BALL_RADIUS * 2 - BALL_RADIUS, BALL_RADIUS * 2
                - BALL_RADIUS);
        ball.setFilled(isEnabled());
        ball.setFillColor(Color.BLACK);
        add(ball, WIDTH / 2, HEIGHT / 2);
    }

    /**
     * Called on mouse press to record the x coordinate of the click and
     * position the paddle accordingly.
     */
    public void mouseClicked(MouseEvent e) {
        double lastX = e.getX();
        if (lastX > WIDTH - PADDLE_WIDTH) {
            lastX = WIDTH - PADDLE_WIDTH;
        }
        add(paddle, lastX, HEIGHT - PADDLE_Y_OFFSET);
    }

    /** 
     * Sets up the board with 10 rows of colored bricks and a paddle. 
     */
    private void setBoard() {
        for (int i = 0; i < NBRICK_ROWS; i++) {
            buildRows(defineColor(i), i);
        }
        makePaddle();
        addMouseListeners();
    }

    /**
     * Builds each rows bricks.
     * @param color, the color of the row.
     * @param level, the level of the row.
     */
    private void buildRows(Color color, int level) {
        for (int i = 0; i < NBRICK_ROWS; i++) {
            GRect brick = makeBrick();
            brick.setFilled(isEnabled());
            brick.setFillColor(color);
            brick.setColor(color);
            int x;
            int y;
            x = i * (BRICK_WIDTH + BRICK_SEP) + LEFT_ROW_POS;
            y = BRICK_Y_OFFSET + level * (BRICK_HEIGHT + BRICK_SEP);
            add(brick, x, y);
        }
    }

    /**
     * Defines the color of the row.
     * @param num, the number of the row multiplied by 2.
     * @return Color, the color of the row.
     */
    private Color defineColor(int num) {
        if (num > 7) {
            return Color.CYAN;
        }
        if (num > 5) {
            return Color.GREEN;
        }
        if (num > 3) {
            return Color.YELLOW;
        }
        if (num > 1) {
            return Color.ORANGE;
        }
        if (num >= 0) {
            return Color.RED;
        }
        return null;
    }
    
    /** 
     * Creates the paddle. 
     */
    private void makePaddle() {
        GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(isEnabled());
        paddle.setFillColor(Color.BLACK);
        add(paddle, (WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_Y_OFFSET);
        this.paddle = paddle;
    }

    /**
     * Creates a brick.
     * @return GRect, a brick.
     */
    private GRect makeBrick() {
        return (new GRect(BRICK_WIDTH, BRICK_HEIGHT));
    }
    
    /** 
     * Runs the Breakout program. 
     */
    public void run() {
        setBoard();
        startGame();
    }
}
