package controller;


import model.Line;
import model.Point2D;
import model.PointLocation;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import tools.ColorInterpolator;
import tools.PointAligner;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller2D {
    private final Panel panel;
    private boolean strictLines;
    private final int  EDIT_RADIUS = 500;
    private int selectedColor;
    private PolygonRasterizer polygonRasterizer;
    private Polygon currentPolygon;
    private Point2D lastPoint;
    private Point2D previewPoint;
    private  ArrayList<Polygon> polygons;
    private ArrayList<Line> lines;
    private LineRasterizer lineRasterizer;
    private Mode mode;
    private PointLocation currentEditPoint;
    private Point2D originalEditLocation;
    private int color1 = 0xFF0000;
    private  int color2 = 0xFF0000;

    public Controller2D(Panel panel) {
        this.panel = panel;
        lines = new ArrayList<>();
        polygons = new ArrayList<Polygon>();
        initPolygon();
        initListeners();
        selectedColor = 0xff0000;
        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        mode = Mode.Polygon;

    }

    private void initPolygon() {
        currentPolygon = new Polygon(color1, color2);
    }

    private int getColorFromDialog(int color) {
        return JColorChooser.showDialog(panel, "Select a color", new Color(color)).getRGB();
    }

    private  void clear() {
        cancelInput();
        lines.clear();
        polygons.clear();
        lastPoint = null;
        currentPolygon = null;

        drawScene();
    }


    private PointLocation findClosestPoint(Point2D click, int radius) {
        var location = new PointLocation();
        int smallestDistance = radius + 1;
        for (int i = 0; i < polygons.size(); i++) {
            for (int j = 0; j < polygons.get(i).getCount(); j++) {
                int distance = PointAligner.measureDistance(click,polygons.get(i).getPoint(j));
                if(distance <= radius && distance < smallestDistance) {
                    smallestDistance = distance;
                    location.setMode(Mode.Polygon);
                    location.setPointIndex(j);
                    location.setShapeIndex(i);
                }

            }
        }
        for (int i = 0; i < lines.size(); i++) {
            Line currentLine = lines.get(i);
            Point2D[] linePoints = {currentLine.getStart(), currentLine.getEnd()};
            for (int j = 0; j < linePoints.length; j++) {
                int distance = PointAligner.measureDistance(click,linePoints[j]);
                if(distance <= radius && distance < smallestDistance) {
                    smallestDistance = distance;
                    location.setMode(Mode.Line);
                    location.setPointIndex(j);
                    location.setShapeIndex(i);
                }

            }
        }

        return location;
    }

    private void drawScene() {
        panel.getRaster().clear();
        for(int i = 0; i < lines.size(); i++) {
            lineRasterizer.rasterize(lines.get(i));
        }
        if(mode == Mode.Polygon && currentPolygon != null ) {
            polygonRasterizer.preview(currentPolygon, previewPoint);
        }
        else {
            if(lastPoint != null)
                lineRasterizer.rasterize(lastPoint, previewPoint, color1, color2);
        }


        for(int i = 0; i < polygons.size(); i++) {
            polygonRasterizer.rasterize(polygons.get(i));
        }
        panel.repaint();
    }

    private void cancelInput() {
        lastPoint = null;
        currentPolygon = null;
        if(currentEditPoint != null && currentEditPoint.getMode() != Mode.None) {
            if(currentEditPoint.getMode() == Mode.Polygon) {
                polygons.get(currentEditPoint.getShapeIndex()).setPoint(currentEditPoint.getPointIndex(), originalEditLocation);
            }
            else {
                Line currrentLine = lines.get(currentEditPoint.getShapeIndex());
                if(currentEditPoint.getPointIndex() == 0)
                    currrentLine.setStart(originalEditLocation);
                else
                    currrentLine.setEnd(originalEditLocation);
            }
            currentEditPoint = null;
            originalEditLocation = null;
        }
        drawScene();
    }

    private void setMode(Mode newMode) {
        cancelInput();
        this.mode = newMode;
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                switch (mode) {
                    case Mode.Polygon:
                        if (currentPolygon == null || currentPolygon.getCount() < 1) {
                            initPolygon();
                        }
                        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
                        break;
                    case Mode.StrictLine:
                    case Mode.Line :
                        if(lastPoint == null)
                            lastPoint = new Point2D(e.getX(), e.getY());
                        else {
                            lines.add(new Line(lastPoint, previewPoint, color1, color2));
                            lastPoint = null;
                        }
                        break;

                    case Mode.Edit:
                        if(currentEditPoint != null) {
                            currentEditPoint = null;
                            originalEditLocation = null;
                            break;
                        }

                        currentEditPoint = findClosestPoint(new Point2D(e.getX(), e.getY()), EDIT_RADIUS);
                        if(currentEditPoint.getMode() != Mode.None) {
                            if(currentEditPoint.getMode() == Mode.Polygon) {
                              originalEditLocation = polygons.get(currentEditPoint.getShapeIndex()).getPoint(currentEditPoint.getPointIndex());
                            }
                            else {
                                var currentLine = lines.get(currentEditPoint.getShapeIndex());
                                originalEditLocation = currentEditPoint.getPointIndex() == 0 ? currentLine.getStart() : currentLine.getEnd();
                            }

                        }
                        break;
                }
                drawScene();

            }

        });
        panel.addMouseMotionListener(new MouseAdapter() {


            @Override
            public void mouseMoved(MouseEvent e) {





                   previewPoint = mode == Mode.StrictLine ? PointAligner.align(lastPoint.getX(), lastPoint.getY(), e.getX(), e.getY()) :new Point2D(e.getX(), e.getY());
                   if(mode == Mode.Edit && currentEditPoint != null) {
                       if(currentEditPoint.getMode() == Mode.Polygon) {
                           polygons.get(currentEditPoint.getShapeIndex()).setPoint(currentEditPoint.getPointIndex(), previewPoint);
                       }

                       if(currentEditPoint.getMode() == Mode.Line) {
                           var currentLine = lines.get(currentEditPoint.getShapeIndex());
                           if(currentEditPoint.getPointIndex() == 0)
                               currentLine.setStart(previewPoint);
                           else
                               currentLine.setEnd(previewPoint);
                       }
                   }


                drawScene();
            }
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
              switch (e.getKeyCode()) {
                  case KeyEvent.VK_L:
                      setMode(Mode.Line);
                      break;
                  case KeyEvent.VK_R:
                      color1 = getColorFromDialog(color1);
                      break;
                  case KeyEvent.VK_W:
                      color2 = color1;
                      break;
                  case KeyEvent.VK_T:
                      color2 = getColorFromDialog(color2);
                      break;
                  case KeyEvent.VK_E:
                      setMode(Mode.Edit);
                      break;
                  case KeyEvent.VK_SHIFT:
                      if(mode == Mode.Line) {
                          mode = Mode.StrictLine;
                          break;
                      }
                      if(mode == Mode.StrictLine)
                          mode = Mode.Line;
                      break;
                  case KeyEvent.VK_BACK_SPACE:
                      if(mode == Mode.Edit) {
                         if(currentEditPoint != null && currentEditPoint.getMode() != null) {
                             if(currentEditPoint.getMode() == Mode.Polygon )
                                 polygons.get(currentEditPoint.getShapeIndex()).removePoint(currentEditPoint.getPointIndex());
                             if(currentEditPoint.getMode() == Mode.Line )
                                 lines.remove(currentEditPoint.getShapeIndex());
                             currentEditPoint = null;
                             lastPoint = null;
                         }
                         drawScene();
                      }
                      break;
                  case KeyEvent.VK_P:
                     setMode(Mode.Polygon);
                      break;
                  case KeyEvent.VK_ENTER:
                      if(mode == Mode.Polygon && currentPolygon != null && currentPolygon.getCount() > 2) {
                          polygons.add(currentPolygon);
                          initPolygon();
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
