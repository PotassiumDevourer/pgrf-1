package controller;


import clip.Clipper;
import fill.Filler;
import fill.ScanLineFiller;
import fill.SeedFiller;
import model.Line;
import model.Point2D;
import model.PointLocation;
import model.Polygon;
import model.Rectangle;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import tools.PointAligner;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller2D {
    private final Panel panel;
    private final int EDIT_RADIUS = 500;
    private boolean strictLines;
    private final int selectedColor;
    private final PolygonRasterizer polygonRasterizer;
    private Polygon currentPolygon;
    private Polygon clippingPolygon;
    private Point2D lastPoint;
    private Point2D previewPoint;
    private final ArrayList<Polygon> polygons;
    private final ArrayList<Line> lines;
    private final LineRasterizer lineRasterizer;
    private Mode mode;
    private PointLocation currentEditPoint;
    private Point2D originalEditLocation;
    private int color1 = 0xFF0000;
    private int color2 = 0xFF0000;
    private ArrayList<Filler> fillers;

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
        fillers = new ArrayList<Filler>();

    }

    private void initPolygon() {
        currentPolygon = new Polygon(color1, color2);
    }

    private int getColorFromDialog(int color) {
        var newColor = JColorChooser.showDialog(panel, "Select a color", new Color(color));
        return newColor != null ? newColor.getRGB() : color;
    }

    private void clear() {
        cancelInput();
        lines.clear();
        polygons.clear();
        fillers.clear();
        lastPoint = null;
        currentPolygon = null;
        clippingPolygon = null;

        drawScene();
    }


    private PointLocation findClosestPoint(Point2D click, int radius) {
        var location = new PointLocation();
        int smallestDistance = radius + 1;
        for (int i = 0; i < polygons.size(); i++) {
            for (int j = 0; j < polygons.get(i).getCount(); j++) {
                int distance = PointAligner.measureDistance(click, polygons.get(i).getPoint(j));
                if (distance <= radius && distance < smallestDistance) {
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
                int distance = PointAligner.measureDistance(click, linePoints[j]);
                if (distance <= radius && distance < smallestDistance) {
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
        for (int i = 0; i < fillers.size(); i++) {
            fillers.get(i).fill();
        }
        if(clippingPolygon != null && clippingPolygon.getCount() > 2 ) {
            Clipper clipper = new Clipper();
            if(currentPolygon != null && currentPolygon.getCount() > 2) {
                ArrayList<Point2D> withPreview = new ArrayList<>();
                withPreview.addAll(currentPolygon.getPoints());
                withPreview.add(previewPoint);
                var overlap = clipper.clip(clippingPolygon.getPoints(), withPreview);
                ScanLineFiller filler = new ScanLineFiller(polygonRasterizer, lineRasterizer, new Polygon(overlap), 0x0000FF);
                filler.fill();
            }


            for (int i = 0; i < polygons.size() ; i++) {

                var current = polygons.get(i);
                if(current == clippingPolygon)
                    continue;
                var thisOverlap =  clipper.clip(clippingPolygon.getPoints(), current.getPoints());
                ScanLineFiller another = new ScanLineFiller(polygonRasterizer, lineRasterizer, new Polygon(thisOverlap), 0x0000FF);
                another.fill();

            }

        }
        for (int i = 0; i < polygons.size(); i++) {
            polygonRasterizer.rasterize(polygons.get(i));
        }
        for (int i = 0; i < lines.size(); i++) {
            lineRasterizer.rasterize(lines.get(i));
        }
        if (mode == Mode.Polygon && currentPolygon != null) {

        } else {

        }

        switch (mode) {
            case Mode.Polygon:
                if(currentPolygon != null)
                    polygonRasterizer.preview(currentPolygon, previewPoint);
                break;
            case Mode.Line:
                if (lastPoint != null)
                    lineRasterizer.rasterize(lastPoint, previewPoint, color1, color2);
                break;
            case Mode.Rectangle:
                if(currentPolygon != null) {
                    if(currentPolygon.getCount() > 1)
                    {
                        polygonRasterizer.rasterize(new Rectangle(currentPolygon.getPoint(0), currentPolygon.getPoint(1), previewPoint, color1, color2));
                    }
                    else {
                        polygonRasterizer.preview(currentPolygon, previewPoint);
                    }

                }

                break;
        }
        panel.repaint();
    }

    private void cancelInput() {
        lastPoint = null;
        currentPolygon = null;
        if (currentEditPoint != null && currentEditPoint.getMode() != Mode.None) {
            if (currentEditPoint.getMode() == Mode.Polygon) {
                polygons.get(currentEditPoint.getShapeIndex()).setPoint(currentEditPoint.getPointIndex(), originalEditLocation);
            } else {
                Line currrentLine = lines.get(currentEditPoint.getShapeIndex());
                if (currentEditPoint.getPointIndex() == 0)
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
                if(e.getButton() == MouseEvent.BUTTON3) {
                    fillers.add(new SeedFiller(color1, panel.getRaster(), e.getX(), e.getY()));
                    drawScene();
                    return;
                }
                switch (mode) {
                    case Mode.Polygon:
                        if (currentPolygon == null || currentPolygon.getCount() < 1) {
                            initPolygon();
                        }
                        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
                        break;
                    case Mode.Rectangle:

                        if (currentPolygon == null || currentPolygon.getCount() < 1) {
                            initPolygon();
                        }
                        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
                        if(currentPolygon.getCount() > 2) {
                            var rectangle = new Rectangle(currentPolygon.getPoint(0), currentPolygon.getPoint(1),
                                    currentPolygon.getPoint(currentPolygon.getCount() - 1), color1, color2);
                            polygons.add(rectangle);
                            fillers.add(new ScanLineFiller( polygonRasterizer, lineRasterizer,
                                    rectangle, 0xFF0000));
                            initPolygon();
                            break;
                        }

                        break;
                    case Mode.StrictLine:
                    case Mode.Line:
                        if (lastPoint == null)
                            lastPoint = new Point2D(e.getX(), e.getY());
                        else {
                            lines.add(new Line(lastPoint, previewPoint, color1, color2));
                            lastPoint = null;
                        }
                        break;

                    case Mode.Edit:
                        if (currentEditPoint != null) {
                            currentEditPoint = null;
                            originalEditLocation = null;
                            break;
                        }

                        currentEditPoint = findClosestPoint(new Point2D(e.getX(), e.getY()), EDIT_RADIUS);
                        if (currentEditPoint.getMode() != Mode.None) {
                            if (currentEditPoint.getMode() == Mode.Polygon) {
                                originalEditLocation = polygons.get(currentEditPoint.getShapeIndex()).getPoint(currentEditPoint.getPointIndex());
                            } else {
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


                previewPoint = mode == Mode.StrictLine && lastPoint != null ? PointAligner.align(lastPoint.getX(), lastPoint.getY(), e.getX(), e.getY()) : new Point2D(e.getX(), e.getY());
                if (mode == Mode.Edit && currentEditPoint != null) {
                    if (currentEditPoint.getMode() == Mode.Polygon) {
                        polygons.get(currentEditPoint.getShapeIndex()).setPoint(currentEditPoint.getPointIndex(), previewPoint);
                    }

                    if (currentEditPoint.getMode() == Mode.Line) {
                        var currentLine = lines.get(currentEditPoint.getShapeIndex());
                        if (currentEditPoint.getPointIndex() == 0)
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
                        switch (mode) {
                            case Line:
                                mode = Mode.StrictLine;
                                break;
                            case StrictLine:
                                mode = Mode.Line;
                                break;
                            case Rectangle:
                                mode = Mode.Polygon;
                                break;
                            case Polygon:
                                mode = Mode.Rectangle;
                                break;
                        }
                        if (mode == Mode.Line) {

                            break;
                        }
                        if (mode == Mode.StrictLine)
                            mode = Mode.Line;
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        if (mode == Mode.Edit) {
                            if (currentEditPoint != null && currentEditPoint.getMode() != null) {
                                if (currentEditPoint.getMode() == Mode.Polygon)
                                    polygons.get(currentEditPoint.getShapeIndex()).removePoint(currentEditPoint.getPointIndex());
                                if (currentEditPoint.getMode() == Mode.Line)
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
                    case KeyEvent.VK_M:
                        if (mode == Mode.Polygon && currentPolygon != null && currentPolygon.getCount() > 2) {

                            polygons.remove(clippingPolygon);
                            clippingPolygon = currentPolygon;
                            clippingPolygon.setColor1(0x00FF00);
                            clippingPolygon.setColor2(0x00FF00);
                            polygons.add(clippingPolygon);
                            initPolygon();
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (mode == Mode.Polygon && currentPolygon != null && currentPolygon.getCount() > 2) {
                            polygons.add(currentPolygon);
                            fillers.add(new ScanLineFiller( polygonRasterizer, lineRasterizer, currentPolygon, 0xFF0000));
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
