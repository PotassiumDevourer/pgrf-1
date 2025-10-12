package controller;


import model.Line;
import model.Point2D;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import view.Panel;

import java.awt.event.*;
import java.util.ArrayList;

public class Controller2D {
    private final Panel panel;
    private int selectedColor;
    private PolygonRasterizer polygonRasterizer;
    private Polygon currentPolygon;
    private Point2D lastPoint;
    private Point2D previewPoint;
    private  ArrayList<Polygon> polygons;
    private ArrayList<Line> lines;
    private LineRasterizer lineRasterizer;
    private Mode mode;
    public Controller2D(Panel panel) {
        this.panel = panel;
        lines = new ArrayList<>();
        currentPolygon = new Polygon();
        polygons = new ArrayList<Polygon>();
        initListeners();
        selectedColor = 0xff0000;
        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        mode = Mode.Polygon;
    }

    private  void clear() {
        lines.clear();
        polygons.clear();
        lastPoint = null;
        currentPolygon = new Polygon();
        drawScene();
    }

    private void drawScene() {
        panel.getRaster().clear();
        for(int i = 0; i < lines.size(); i++) {
            lineRasterizer.rasterize(lines.get(i));
        }
        if(mode == Mode.Polygon) {
            polygonRasterizer.preview(currentPolygon, previewPoint);
        }
        else {
            if(lastPoint != null)
                lineRasterizer.rasterize(lastPoint, previewPoint);
        }


        for(int i = 0; i < polygons.size(); i++) {
            polygonRasterizer.rasterize(polygons.get(i));
        }
        panel.repaint();
    }

    private void cancelInput() {
        lastPoint = null;
        currentPolygon = new Polygon();
        drawScene();
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
                if(mode == Mode.Polygon)
                    currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
                else {
                    if(lastPoint == null)
                        lastPoint = new Point2D(e.getX(), e.getY());
                    else {
                        lines.add(new Line(lastPoint, previewPoint));
                        lastPoint = null;
                    }
                }
                drawScene();

            }

        });
        panel.addMouseMotionListener(new MouseAdapter() {


            @Override
            public void mouseMoved(MouseEvent e) {

               if(mode == Mode.StrictLine && lastPoint != null) {
                   int x1 = lastPoint.getX();
                   int x2 = e.getX();
                   int y1 = lastPoint.getY();
                   int y2 = e.getY();

                   int dx = (x2 - x1);
                   int dy = (y2 - y1);

                   int[] possibleValues = {Math.abs(dx), Math.abs(dy), Math.abs(dx - dy), Math.abs(dx + dy)};
                   int minIndex = 0;
                   int min = possibleValues[minIndex];
                   String debugOutput = possibleValues[minIndex] + ",";
                   for(int i = 1; i < possibleValues.length;i++) {
                       if(min > possibleValues[i]) {
                           min = possibleValues[i];
                           minIndex = i;

                       }
                       debugOutput += possibleValues[i] + ",";
                   }
                   System.out.println(debugOutput + " Smallest index:" + minIndex);
                   switch (minIndex) {
                       case 0:
                           // Nejmensi je rozdil x, usecka je vlastne vertikalni
                           previewPoint = new Point2D(x1, y2);
                           break;
                       case 1:
                           // nejmensi je rozdil y, usecka je vlastne rovna cara
                           previewPoint = new Point2D(x2, y1);
                           break;
                       case 2:
                           // nejmensi je rozdil x a y, usecka ma nejbliz k 1. diagonale
                           previewPoint = new Point2D(x2, y1 + dx);
                           break;
                       case 3:
                           // nejmensi je soucet x a y, usecka ma nejbliz k 2. diagonale
                           previewPoint = new Point2D(x2, y1 - dx);
                           break;
                       default:
                           previewPoint = new Point2D(x1, y2);
                           break;
                   }
               }
               else {
                   previewPoint = new Point2D(e.getX(), e.getY());
               }

                drawScene();
            }
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
              switch (e.getKeyCode()) {
                  case KeyEvent.VK_L:
                      mode = Mode.Line;
                      break;
                  case KeyEvent.VK_SHIFT:
                      if(mode == Mode.Line) {
                          mode = Mode.StrictLine;
                          break;
                      }
                      if(mode == Mode.StrictLine)
                          mode = Mode.Line;
                      break;
                  case KeyEvent.VK_P:
                      cancelInput();
                      mode = Mode.Polygon;
                      break;
                  case KeyEvent.VK_ENTER:
                      if(mode == Mode.Polygon) {
                          polygons.add(currentPolygon);
                          currentPolygon = new Polygon();
                      }
                      break;

                  case KeyEvent.VK_C:
                      clear();
                      break;
                  case KeyEvent.VK_ESCAPE:
                      cancelInput();
                      break;
              }
            }
        });
    }

}
