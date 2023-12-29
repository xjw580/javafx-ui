package club.xiaojiawei.test;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/28 9:07
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author: happyaaakkk
 * @date : 2020年4月28日
 * @Description:贝塞尔曲线测试
 */
public class TestBezier {

    /**
     * 存关键点的x、y的数组
     */
    private static Point2D[] keyPointP;
    /**
     * 存关键点的x、y、width、height的数组
     */
    private static Ellipse2D.Double[] keyPointE;
    /**
     * 关键点数
     */
    private static int keyPointNum;
    /**
     * 偏移量，越小曲线越精细
     */
    private static double t = 0.001;
    /**
     * 显示蓝色辅助线的标记
     */
    private static boolean flagShow = true;

    static class BezierPanel extends JPanel {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        /**
         * 关键点编号
         */
        private int keyPointID = -1;

        BezierPanel() {
            this.addMouseListener(new MouseAction());
            this.addMouseMotionListener(new MouseMotion());
        }

        @Override
        protected void paintComponent(Graphics gs) {
            // 重写repaint
            super.paintComponent(gs);
            Graphics2D g = (Graphics2D) gs;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLUE);
            if (flagShow) {
                for (int i = 0; i < keyPointNum; i++) { // 绘制圆点
                    if (i > 0 && i < (keyPointNum - 1)) {
                        g.fill(keyPointE[i]);// 中间的关键点画实心圆
                    } else {
                        g.draw(keyPointE[i]);// 第一个和最后一个画空心圆
                    }
                    if (keyPointNum > 1 && i < (keyPointNum - 1)) {
                        g.drawLine((int) keyPointP[i].getX(), (int) keyPointP[i].getY(), (int) keyPointP[i + 1].getX(),
                                (int) keyPointP[i + 1].getY());// 画关键点之间连接的直线
                    }
                    if (i == 0) {
                        g.drawString("A", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    } else if (i == 1) {
                        g.drawString("B", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    } else if (i == 2) {
                        g.drawString("C", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    } else if (i == 3) {
                        g.drawString("D", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    }
                }
            }
            // 二次贝塞尔曲线
            if (keyPointNum == 3) {
                double x, y;
                g.setColor(Color.RED);
                for (double k = t; k <= 1 + t; k += t) {
                    double r = 1 - k;
                    x = Math.pow(r, 2) * keyPointP[0].getX() + 2 * k * r * keyPointP[1].getX() + Math.pow(k, 2) * keyPointP[2].getX();
                    y = Math.pow(r, 2) * keyPointP[0].getY() + 2 * k * r * keyPointP[1].getY() + Math.pow(k, 2) * keyPointP[2].getY();
                    g.drawOval((int) x, (int) y, 1, 1);// 画圆的方式比下面注释掉的画线效果更好
                    // g.drawLine((int) x, (int) y, (int) x, (int) y);
                }
            }
            // 三次贝塞尔曲线
            if (keyPointNum == 4) {
                double x, y;
                g.setColor(Color.RED);
                for (double k = t; k <= 1 + t; k += t) {
                    double r = 1 - k;
                    x = Math.pow(r, 3) * keyPointP[0].getX() + 3 * k * Math.pow(r, 2) * keyPointP[1].getX()
                            + 3 * Math.pow(k, 2) * (1 - k) * keyPointP[2].getX() + Math.pow(k, 3) * keyPointP[3].getX();
                    y = Math.pow(r, 3) * keyPointP[0].getY() + 3 * k * Math.pow(r, 2) * keyPointP[1].getY()
                            + 3 * Math.pow(k, 2) * (1 - k) * keyPointP[2].getY() + Math.pow(k, 3) * keyPointP[3].getY();
                    g.drawOval((int) x, (int) y, 1, 1);
                }
            }
        }

        class MouseAction extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 点击鼠标左键
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (keyPointNum < 4) {
                        double x = e.getX();
                        double y = e.getY();
                        keyPointP[keyPointNum] = new Point2D.Double(x, y);
                        keyPointE[keyPointNum] = new Ellipse2D.Double(x - 4, y - 4, 8, 8);
                        keyPointNum++;
                        repaint();
                    }
                } // 点击鼠标右键
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    flagShow = false;// 隐藏蓝色辅助线，但并不能真正移除
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // 按下鼠标,判断是不是点击了关键点
                for (int i = 0; i < keyPointNum; i++) {
                    if (keyPointE[i].contains((Point2D) e.getPoint())) {
                        keyPointID = i;
                        break;
                    } else {
                        keyPointID = -1;
                    }
                }
            }
        }

        class MouseMotion extends MouseMotionAdapter {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 鼠标拖动关键点
                if (keyPointID != -1) {
                    double x = e.getX();
                    double y = e.getY();
                    keyPointP[keyPointID] = new Point2D.Double(x, y);
                    keyPointE[keyPointID] = new Ellipse2D.Double(x - 4, y - 4, 8, 8);
                    repaint();
                }
            }
        }
    }

    public TestBezier() {
        JFrame f = new JFrame();
        f.setTitle("Bezier Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);

        keyPointNum = 0;
        keyPointP = new Point2D[4];
        keyPointE = new Ellipse2D.Double[4];
        BezierPanel bezierPanel = new BezierPanel();
        bezierPanel.setPreferredSize(new Dimension(800, 600));
        bezierPanel.setBackground(Color.WHITE);

        f.setContentPane(bezierPanel);
        f.setVisible(true);
    }

    public static void main(String[] agrs) {
        new TestBezier();
    }
}
