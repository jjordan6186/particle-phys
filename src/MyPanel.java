import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.swing.*;

public class MyPanel extends JPanel implements ActionListener, MouseListener {

    final int PANEL_WIDTH = 800;
    final int PANEL_HEIGHT = PANEL_WIDTH;
    final double G = 1; //because the gravitational constant in computer science is 1!!! XD
    int intrad = 100; //either the radius of all of the particles or the max of random radii
    boolean randrad = true; //use random radii???? very fun!
    boolean setrad = true; //sets the radii to the array below!, no need to use amt.
    int[] radii = {3,3,3,3,3,3,3,3,3,30};
    int amt = 10; //amount of particles
    int types = 10; //types of particles, used for color & emergent behavior
    int maxforce = 3; //emergent max force in forcetable
    int maxintvel = 0; //gives the particles a random velocity with a max of this variable
    boolean edges = true; //if true the particles will not pass the edges
    boolean coll = true; //collision
    int colc = 0; //collision counter :)
    Vector mouse = new Vector();
    //CANT HAVE MORE THAN ONE TRUE!!!!
    boolean emergent = false;
    double dragE = 0.5;
    boolean gravity = !emergent;
    double dragG = .9999;
    //end of the types of johns
    double[][] forcetable; //array of forces to use in emergent behavior
    Particle[] parts;
    double tke = 0;
    JLabel text;
    Timer timer;
    DecimalFormat df1 = new DecimalFormat("0.00");


    MyPanel() {
        amt = setrad?radii.length:amt;
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.addMouseListener(this);
        text = new JLabel("Total Kinetic Energy " + tke);
        this.add(text);
        parts = new Particle[amt];
        for (int i = 0; i < amt; i++) {
            Vector pos = new Vector(0, 0), vel = new Vector(0, 0), acc = new Vector(0, 0);
            pos = pos.randPos(PANEL_WIDTH);
            vel = vel.randVel(maxintvel);
            int type = (int) (Math.random() * types);
            parts[i] = new Particle(pos, vel, acc, type, setrad ? (radii[i]) : (randrad ? ((int) (Math.random() * intrad) + 1 ): intrad), types, emergent ? dragE : dragG)
            ;
        }
        forcetable = new double[types][types];
        for (int i = 0; i < types; i++) {
            for (int j = 0; j < types; j++) {
                forcetable[i][j] = Math.random() * maxforce - (double) maxforce / 2;
                //forcetable[i][j] = -1;
                System.out.print((forcetable[i][j] > 0 ? "  " : " -") + df1.format(Math.abs(forcetable[i][j])));
            }
            System.out.println();

        }
        timer = new Timer(1, this);
        timer.start();

    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        for (Particle p : parts) {
            p.paint(g2D);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        text.setText("Total Kinetic Energy " + df1.format(tke) + ", Particle Count: " + amt + ", Collisions: " + colc);
        tke = 0;
        for (Particle p : parts) {
            //p.updText("fart");
            tke += p.vel.length();
            double rad = p.radius;
            if (edges) {
                if (p.pos.x > PANEL_WIDTH - rad) {
                    p.pos.x = PANEL_WIDTH - rad;
                    p.vel.x *= -1;
                    colc++;
                } else if (p.pos.x < rad) {
                    p.pos.x = rad;
                    p.vel.x *= -1;
                    colc++;
                }
                if (p.pos.y > PANEL_HEIGHT - rad) {
                    p.pos.y = PANEL_HEIGHT - rad;
                    p.vel.y *= -1;
                    colc++;
                } else if (p.pos.y < rad) {
                    p.pos.y = rad;
                    p.vel.y *= -1;
                    colc++;
                }
            } else {
                if (p.pos.x > PANEL_WIDTH) {
                    p.pos.x = 0;
                } else if (p.pos.x < 0) {
                    p.pos.x = PANEL_WIDTH;
                }
                if (p.pos.y > PANEL_HEIGHT) {
                    p.pos.y = 0;
                } else if (p.pos.y < 0) {
                    p.pos.y = PANEL_HEIGHT;
                }
            }
            for (Particle other : parts) {
                if (other != p) {
                    if (emergent) {
                        double max = 200;
                        if (other.pos.subtract(p.pos).normalize().length() < max) {
                            Vector dist = other.pos.subtract(p.pos);
                            Vector norm = dist.normalize();
                            double l = dist.length();
                            double F;
                            double min = max * .3;
                            if (l > min && l < max) {
                                F = 1 - (Math.abs(2 * l - max - min) / (max - min));
                                p.acc = p.acc.add(norm.multiply(F).multiply(forcetable[p.type][other.type]));
                            } else if (l < min) {
                                F = l / min - 1;
                                p.acc = p.acc.add(norm.multiply(F));
                            }
                        }
                    }
                    if (gravity) {
                        Vector dist = other.pos.subtract(p.pos);
                        Vector norm = dist.normalize();
                        double l = dist.length();
                        double F = other.radius / l;
                        p.acc = p.acc.add(norm.multiply(F));
                        if (coll && dist.length() <= rad + other.radius) {
                            Vector len1 = norm.multiply(p.vel.dotProd(norm));
                            Vector len2 = norm.multiply(other.vel.dotProd(norm));
                            p.vel = p.vel.subtract(len1);
                            other.vel = other.vel.subtract(len2);
                            p.vel = p.vel.add(len2);
                            other.vel = other.vel.add(len1);
                            Vector over = norm.multiply(dist.length() - (p.radius + other.radius));
                            double ratio = .5;
                            p.pos = p.pos.add(over.multiply(ratio));
                            other.pos = other.pos.subtract(over.multiply(1 - ratio));
                            colc++;
                        }
                    }
                }
            }
            p.update();
            p.acc = new Vector(0, 0);
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouse = new Vector(e.getX(), e.getY());
        System.out.println(mouse);

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
