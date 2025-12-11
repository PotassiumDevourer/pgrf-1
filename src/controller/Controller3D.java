package controller;


import clip.Clipper;
import enums.Modes3D;
import enums.RotationAxis;
import fill.Filler;
import fill.ScanLineFiller;
import fill.SeedFiller;
import model.*;
import model.Polygon;
import model.Rectangle;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import render.Renderer;
import solid.Arrow;
import solid.AxisSet;
import solid.Cube;
import solid.Solid;
import tools.PointAligner;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller3D {
    private final Panel panel;
    private final LineRasterizer lineRasterizer;
    private Renderer renderer;
    private Solid axis;
    private ArrayList<Solid> solids;
    private int selectedSolid;
    private Camera camera;
    private RotationAxis currentRotationAxis = RotationAxis.Z;
    private Modes3D currentMode;
    private Mat4 proj;
    private int lastX;
    private  int lastY;



    public Controller3D(Panel panel) {
        this.panel = panel;
        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        initListeners();
        this.camera = new Camera().withPosition(new Vec3D(0.5,-1.5,1))
                .withAzimuth(Math.toRadians(90)).withZenith(Math.toRadians(-25)).withFirstPerson(true);
        this.proj = new Mat4PerspRH(Math.toRadians(90), panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(), 0.1, 100);
        renderer = new Renderer(lineRasterizer, panel.getRaster().getWidth(), panel.getRaster().getHeight(), camera.getViewMatrix(), proj);
        currentMode = Modes3D.Movement;
        axis = new AxisSet();
        initObjects();
        drawScene();

    }

    private void initObjects() {
        solids = new ArrayList<Solid>();

        solids.add(new Cube());
        setSelectedSolid(0);
    }


    private void drawScene() {
        panel.getRaster().clear();
        renderer.setView(camera.getViewMatrix());

        renderer.renderSolid(axis);
        renderer.renderSolids(solids);
        panel.repaint();
    }
    private void setSelectedSolid(int index) {
        if(index < 0 || index > solids.size() - 1)
            return;
        solids.get(selectedSolid).setSelected(false);
        solids.get(index).setSelected(true);
        selectedSolid = index;
    }

    private void setCurrentMode(Modes3D mode) {
        currentMode = mode;
    }

    private void setCurrentRotationAxis(int index) {
        RotationAxis[] values = RotationAxis.values();

        currentRotationAxis = values[index % values.length];
        System.out.println(currentRotationAxis.name());
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                camera = camera.addAzimuth(Math.toRadians(0.002*dx));
                camera = camera.addZenith(Math.toRadians(0.002*dy));
                drawScene();
            }
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_Q:
                        setSelectedSolid( selectedSolid - 1);
                        break;
                    case KeyEvent.VK_E:
                        setSelectedSolid( selectedSolid + 1);
                        break;
                    case KeyEvent.VK_LEFT:
                        switch (currentMode) {
                            case Modes3D.Movement:
                                solids.get(selectedSolid).move(-0.5, 0, 0);
                                break;
                            case Modes3D.Rotation:
                                solids.get(selectedSolid).rotate(15, currentRotationAxis);
                                break;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        switch (currentMode) {
                            case Modes3D.Movement:
                                solids.get(selectedSolid).move(0.5, 0, 0);
                                break;
                            case Modes3D.Rotation:
                                solids.get(selectedSolid).rotate(-15, currentRotationAxis);
                                break;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        switch (currentMode) {
                            case Modes3D.Movement:
                                solids.get(selectedSolid).move(0, 0.5, 0);
                                break;

                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        switch (currentMode) {
                            case Modes3D.Movement:
                                solids.get(selectedSolid).move(0, -0.5, 0);
                                break;

                        }
                        break;
                    case KeyEvent.VK_R:
                        setCurrentMode(Modes3D.Rotation);
                        break;
                    case KeyEvent.VK_M:
                        setCurrentMode(Modes3D.Movement);
                        break;
                    case KeyEvent.VK_T:
                        if(currentMode == Modes3D.Rotation) {
                            setCurrentRotationAxis(currentRotationAxis.ordinal() + 1);
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        switch (currentMode) {
                            case Modes3D.Movement:
                                solids.get(selectedSolid).move(0, 0, 0.5);
                                break;
                        }


                    case KeyEvent.VK_SHIFT:
                        if(currentMode == Modes3D.Movement)
                            solids.get(selectedSolid).move(0, 0, -0.5);
                        break;


                }


                if(e.getKeyCode() == KeyEvent.VK_W) {
                    camera = camera.forward(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(0.1);
                }


                drawScene();
            }
        });
    }

}
