package controller;


import model.Line;
import model.Point2D;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import view.Panel;

import java.awt.event.*;
import java.util.ArrayList;

public class Controller2D {
    private final Panel panel;
    private int selectedColor;
    private PolygonRasterizer polygonRasterizer;
    private Polygon polygon;
    private Point2D lastPoint;
    private ArrayList<Line> lines;
    private LineRasterizer graphics;
    public Controller2D(Panel panel) {
        this.panel = panel;
        lines = new ArrayList<>();
        polygon = new Polygon();
        initListeners();
        selectedColor = 0xff0000;
        graphics = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(graphics);
    }

    private void drawScene() {
        panel.getRaster().clear();
        for(int i = 0; i < lines.size(); i++) {
            graphics.rasterize(lines.get(i).getStart(), lines.get(i).getEnd());
        }
        polygonRasterizer.rasterize(polygon);

        panel.repaint();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
              /*  if(lastPoint == null) {
                    lastPoint = new Point2D(e.getX(), e.getY());
                }
                else {
                    lines.add(new Line(lastPoint, new Point2D(e.getX(), e.getY())));
                    lastPoint = null;
                    drawScene();
                }*/
                polygon.addPoint(new Point2D(e.getX(), e.getY()));
                drawScene();

            }

        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {


            }
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                   var raster = panel.getRaster();
                   int y = (int) (raster.getHeight() / 2 + 0.5);
                   int x1 = (int) (raster.getWidth() / 2 + 0.5);
                   for (int i = x1; i < raster.getWidth(); i++) {
                       panel.getRaster().setPixel(i, y, 0xff0000);
                   }
                   panel.repaint();
               }

                if(e.getKeyCode() == KeyEvent.VK_P)
                    selectedColor = 0xff0000;
                if(e.getKeyCode() == KeyEvent.VK_O)
                    selectedColor = 0x00ff00;
            }
        });
    }

}
