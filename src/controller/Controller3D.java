package controller;


import enums.CubicType;
import enums.Modes3D;
import enums.RenderingMode;
import enums.RotationAxis;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import render.Renderer;
import solid.*;
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
    private Renderer perspRenderer;
    private Renderer orthoRenderer;
    private AxisSet axis;
    private ArrayList<Solid> solids;
    private int selectedSolid;
    private Camera camera;
    private RotationAxis currentRotationAxis = RotationAxis.Z;
    private Modes3D currentMode;
    private Mat4 proj;
    private Mat4 orthoProj;
    private int lastX;
    private  int lastY;
    private JLabel lrenderingMode;
    private RenderingMode currentRenderingMode;
    private CardLayout contextHelp;
    private JPanel pContextHelp;
    private Timer timer;
    private Label lsegmentationHint;

    public Controller3D(Panel panel) {
        this.panel = panel;
        JPanel pageHeader = new JPanel();
        JPanel pageFooter = new JPanel();
        pageFooter.setLayout(new GridLayout(0,2));
        JPanel contextHelp = new JPanel(new CardLayout());
        JPanel renderingModeContainer = new JPanel(new FlowLayout());
        lrenderingMode = new JLabel("Rendering in PERSPECTIVE mode, press X, to switch.");
        renderingModeContainer.add(lrenderingMode);
        prepareContextCards();
        contextHelp.add(pContextHelp);
        pageFooter.add(contextHelp);
        pageFooter.add(renderingModeContainer);

        var boxLayout = new BoxLayout(pageHeader, BoxLayout.X_AXIS);
        pageHeader.add(new JLabel("M - Move R - Rotate L - Scale F - animate "));
        panel.setLayout(new BorderLayout());
        panel.add(pageHeader, BorderLayout.PAGE_START);
        panel.add(pageFooter, BorderLayout.PAGE_END);


        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        initListeners();
        this.camera = new Camera().withPosition(new Vec3D(0.5,-1.5,1))
                .withAzimuth(Math.toRadians(90)).withZenith(Math.toRadians(-25)).withFirstPerson(true);
        this.proj = new Mat4PerspRH(Math.toRadians(90), panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(), 0.1, 100);
        this.orthoProj = new Mat4OrthoRH(2,2, 0.0, -100.0);
        renderer = new Renderer(lineRasterizer, panel.getRaster().getWidth(), panel.getRaster().getHeight(), camera.getViewMatrix(), proj);
        currentMode = Modes3D.Translate;
        currentRenderingMode = RenderingMode.Perspective;
        axis = new AxisSet();
        panel.revalidate();

        initObjects();
        drawScene();
        timer = new Timer(16, e -> animate());


    }

    private void animate(){
        solids.get(selectedSolid).rotate(8, RotationAxis.Z);
        drawScene();
    }

    private void prepareContextCards() {

        contextHelp = new CardLayout();
        pContextHelp  = new JPanel(contextHelp);
        JPanel pTranslate = new JPanel();
        BoxLayout l1 = new BoxLayout( pTranslate, BoxLayout.Y_AXIS);
        pTranslate.setLayout(l1);
        pTranslate.add(new Label("Current mode: Translation"));
        pTranslate.add(new Label("Q/E - switch between objects"));
        pTranslate.add(new Label("Arrow Keys - move objects"));
        pTranslate.add(new Label("Ctrl + Up - Move up"));
        pTranslate.add(new Label("Ctrl + Down - Move down"));
        pContextHelp.add(pTranslate, Modes3D.Translate.name());
        JPanel pRotate = new JPanel();
        BoxLayout l2 = new BoxLayout( pRotate, BoxLayout.Y_AXIS);
        pRotate.setLayout(l2);
        pRotate.add(new Label("Current mode: Rotation"));
        pRotate.add(new Label("Q/E - switch between objects"));
        pRotate.add(new Label("T - switch rotation axis"));
        pRotate.add(new Label("Left/Right arrow - rotate"));

        JPanel pScale = new JPanel();
        BoxLayout l3 = new BoxLayout( pScale, BoxLayout.Y_AXIS);
        pScale.setLayout(l3);
        lsegmentationHint = new Label("O/P - change rendering accuracy");
        lsegmentationHint.setVisible(false);
        pScale.add(new Label("Current mode: Scaling"));
        pScale.add(new Label("Q/E - switch between objects"));
        pScale.add(new Label("Arrow Keys - scale objects"));
        pScale.add(new Label("CTRL + UP/DOWN  - scale objects on the Z axis"));
        pScale.add(lsegmentationHint);



        pContextHelp.add(pScale, Modes3D.Scale.name());
        pContextHelp.add(pRotate, Modes3D.Rotation.name());



    }

    private void initObjects() {
        solids = new ArrayList<Solid>();
        var cube = new Cube();
        cube.setScale(0.5,0.5,0.5);
        cube.move(1,1,1);
        solids.add(cube);
        var cylinder = new Cylinder(10);
        cylinder.setScale(0.5,0.5,0.5);
        solids.add(cylinder);
        solids.add(new CubicModel(
                new Point3D(0.0,0, -1.0),
                new Point3D(0.3,0,  1.2),
                new Point3D(0.7,0, -1.2),
                new Point3D(1.2, 0, 0.0),
                CubicType.Bezier,
                64
        ));
        solids.add(new CubicModel(
                new Point3D(1.2,0,  0.0),
                new Point3D(1.6,0,  0.8),
                new Point3D(2.0,0, -0.8),
                new Point3D(2.4, 0, 0.0),
                CubicType.Coons,
                64
        ));
        solids.add(new CubicModel(
                new Point3D(2.4,0,  0.0),
                new Point3D(2.9, 0, 1.0),
                new Point3D(3.1, 0,-1.0),
                new Point3D(3.6,0, -0.2),
                CubicType.Ferguson,
                64
        ));

        setSelectedSolid(0);
    }

    private void switchRenderingMode() {
        if(currentRenderingMode == RenderingMode.Perspective) {
            renderer.setProj(orthoProj);
            currentRenderingMode = RenderingMode.Orthographic;
        }
        else {
            renderer.setProj(proj);
            currentRenderingMode = RenderingMode.Perspective;
        }
        lrenderingMode.setText(String.format("Rendering in %1S mode, press X, to switch.", currentRenderingMode.toString()));
        drawScene();
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
        if(solids.get(index) instanceof Segmentable) {
            lsegmentationHint.setVisible(true);
            panel.revalidate();
        }
        else {
            lsegmentationHint.setVisible(false);
            panel.revalidate();
        }

        selectedSolid = index;
    }

    private void setCurrentMode(Modes3D mode) {
        if(mode == Modes3D.Rotation)
            axis.showSelectedAxis();
        else
            axis.hideSelectedAxis();
        currentMode = mode;

        contextHelp.show(pContextHelp, currentMode.name());
        drawScene();
    }

    private void setCurrentRotationAxis(int index) {
        RotationAxis[] values = RotationAxis.values();

        currentRotationAxis = values[index % values.length];
        axis.setSelectedAxis(currentRotationAxis);
        axis.showSelectedAxis();
        drawScene();

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
                    case KeyEvent.VK_F:
                        if(timer.isRunning()) {
                            timer.stop();
                        }
                        else {
                            timer.start();
                        }


                        break;
                    case KeyEvent.VK_Q:
                        setSelectedSolid( selectedSolid - 1);
                        break;
                    case KeyEvent.VK_X:
                        switchRenderingMode();
                        break;
                    case KeyEvent.VK_E:
                        setSelectedSolid( selectedSolid + 1);
                        break;
                    case KeyEvent.VK_LEFT:

                        switch (currentMode) {
                            case Modes3D.Scale:
                                var selected = solids.get(selectedSolid);
                                selected.setScale(selected.getScaleX() - 0.1, selected.getScaleY(), selected.getScaleZ());
                                break;
                            case Modes3D.Translate:
                                solids.get(selectedSolid).move(-0.5, 0, 0);
                                break;
                            case Modes3D.Rotation:
                                solids.get(selectedSolid).rotate(15, currentRotationAxis);
                                break;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        switch (currentMode) {
                            case Modes3D.Scale:
                                var selected = solids.get(selectedSolid);
                                selected.setScale(selected.getScaleX() + 0.1, selected.getScaleY(), selected.getScaleZ());
                                break;
                            case Modes3D.Translate:
                                solids.get(selectedSolid).move(0.5, 0, 0);
                                break;
                            case Modes3D.Rotation:
                                solids.get(selectedSolid).rotate(-15, currentRotationAxis);
                                break;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        switch (currentMode) {
                            case Modes3D.Scale:
                                var selected = solids.get(selectedSolid);
                                if(e.isControlDown())
                                    selected.setScale(selected.getScaleX(), selected.getScaleY(), selected.getScaleZ() + 0.5);
                                else
                                    selected.setScale(selected.getScaleX(), selected.getScaleY() + 0.5, selected.getScaleZ());
                                break;
                            case Modes3D.Translate:
                                if(e.isControlDown())
                                    solids.get(selectedSolid).move(0, 0.0, 0.5);
                                else
                                    solids.get(selectedSolid).move(0, 0.5, 0);
                                break;

                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        switch (currentMode) {
                            case Modes3D.Scale:
                                var selected = solids.get(selectedSolid);
                                if(e.isControlDown())
                                    selected.setScale(selected.getScaleX(), selected.getScaleY(), selected.getScaleZ() - 0.5);
                                else
                                    selected.setScale(selected.getScaleX(), selected.getScaleY() - 0.5, selected.getScaleZ());
                                break;
                            case Modes3D.Translate:
                                if(e.isControlDown())
                                    solids.get(selectedSolid).move(0,0 , -0.5);
                                else
                                    solids.get(selectedSolid).move(0, -0.5, 0);
                                break;

                        }
                        break;
                    case KeyEvent.VK_P:
                            var current = solids.get(selectedSolid);
                            if(currentMode == Modes3D.Scale && current instanceof Segmentable) {
                                var segmentable = (Segmentable)current;
                                segmentable.setCurrentSegments(segmentable.getCurrentSegments() + 1);
                                drawScene();
                            }

                        break;
                    case KeyEvent.VK_O:
                        current = solids.get(selectedSolid);
                        if(currentMode == Modes3D.Scale && current instanceof Segmentable) {
                            var segmentable = (Segmentable)current;
                            segmentable.setCurrentSegments(segmentable.getCurrentSegments() - 1);
                            drawScene();
                        }

                        break;
                    case KeyEvent.VK_R:
                        setCurrentMode(Modes3D.Rotation);
                        break;
                    case KeyEvent.VK_L:
                        setCurrentMode(Modes3D.Scale);
                        break;
                    case KeyEvent.VK_M:
                        setCurrentMode(Modes3D.Translate);
                        break;
                    case KeyEvent.VK_T:
                        if(currentMode == Modes3D.Rotation) {
                            setCurrentRotationAxis(currentRotationAxis.ordinal() + 1);
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                       camera = camera.up(0.1);
                    break;

                    case KeyEvent.VK_C:
                            camera = camera.down(0.1);
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
